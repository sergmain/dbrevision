/*
 * org.riverock.dbrevision - Database revision engine
 * For more information about DbRevision, please visit project site
 * http://www.riverock.org
 *
 * Copyright (C) 2006-2006, Riverock Software, All Rights Reserved.
 *
 * Riverock - The Open-source Java Development Community
 * http://www.riverock.org
 *
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.riverock.dbrevision.db.factory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import org.riverock.dbrevision.annotation.schema.db.DbDataFieldData;
import org.riverock.dbrevision.annotation.schema.db.DbField;
import org.riverock.dbrevision.annotation.schema.db.DbForeignKey;
import org.riverock.dbrevision.annotation.schema.db.DbPrimaryKey;
import org.riverock.dbrevision.annotation.schema.db.DbPrimaryKeyColumn;
import org.riverock.dbrevision.annotation.schema.db.DbSequence;
import org.riverock.dbrevision.annotation.schema.db.DbTable;
import org.riverock.dbrevision.annotation.schema.db.DbView;
import org.riverock.dbrevision.db.DatabaseAdapter;
import org.riverock.dbrevision.db.DatabaseManager;
import org.riverock.dbrevision.exception.DbRevisionException;
import org.riverock.dbrevision.utils.DbUtils;
import org.riverock.dbrevision.utils.Utils;

/**
 * IBM DB2 connection
 * $Author: serg_main $
 *
 * $Id: DB2Adapter.java 1141 2006-12-14 14:43:29Z serg_main $
 *
 */
@SuppressWarnings({"UnusedAssignment"})
public class DB2Adapter extends DatabaseAdapter {
    private static Logger log = Logger.getLogger(DB2Adapter.class);

    /**
     * get family for this adapter
     * @return family
     */
    public Family getFamily() {
        return Family.DB2;
    }

    public DB2Adapter(Connection conn) {
        super(conn);
    }

    public int getMaxLengthStringField() {
        return 2000;
    }

    public boolean isBatchUpdate() {
        return true;
    }

    public boolean isNeedUpdateBracket() {
        return false;
    }

    public boolean isByteArrayInUtf8() {
        return false;
    }

    public String getClobField(ResultSet rs, String nameField)
        throws SQLException {
        return getClobField(rs, nameField, 20000);
    }

    public byte[] getBlobField(ResultSet rs, String nameField, int maxLength) throws Exception {
        Blob blob = rs.getBlob(nameField);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int count;
        byte buffer[] = new byte[1024];

        InputStream inputStream = blob.getBinaryStream();
        while ((count = inputStream.read(buffer)) >= 0) {
            outputStream.write(buffer, 0, count);
            outputStream.flush();
        }
        outputStream.close();
        return outputStream.toByteArray();
    }

    public void createTable(DbTable table) {
        if (table == null || table.getFields().isEmpty()) {
            return;
        }

        String sql = "create table \"" + table.getName() + "\" " +
            "(";

        boolean isFirst = true;

        for (DbField field : table.getFields()) {
            if (!isFirst)
                sql += ",";
            else
                isFirst = !isFirst;

            sql += " \"" + field.getName() + "\"";
            switch (field.getJavaType()) {

                case Types.NUMERIC:
                case Types.DECIMAL:
                    sql += " DECIMAL(" + (field.getSize()==null || field.getSize() > 31 ? 31 : field.getSize()) + ',' + field.getDecimalDigit() + ")";
                    break;

                case Types.INTEGER:
                    sql += " INTEGER";
                    break;

                case Types.DOUBLE:
                    sql += " DOUBLE";
                    break;

                case Types.CHAR:
                    sql += " VARCHAR(1)";
                    break;

                case Types.VARCHAR:
                    sql += " VARCHAR(" + field.getSize() + ")";
                    break;

                case Types.TIMESTAMP:
                case Types.DATE:
                    sql += " TIMESTAMP";
                    break;

                case Types.LONGVARCHAR:
                    sql += " VARCHAR(10)";
                    break;

                case Types.LONGVARBINARY:
                    sql += " LONGVARBINARY";
                    break;

                default:
                    field.setJavaStringType("unknown field type field - " + field.getName() + " javaType - " + field.getJavaType());
                    System.out.println("unknown field type field - " + field.getName() + " javaType - " + field.getJavaType());
            }

            if (field.getDefaultValue() != null) {
                String val = field.getDefaultValue().trim();

//                if (!val.equalsIgnoreCase("null"))
//                    val = "'"+val+"'";
                if (DatabaseManager.checkDefaultTimestamp(val))
                    val = "CURRENT TIMESTAMP";

                sql += (" DEFAULT " + val);
            }

            if (field.getNullable() == DatabaseMetaData.columnNoNulls) {
                sql += " NOT NULL ";
            }
        }

        if (table.getPrimaryKey() != null && table.getPrimaryKey().getColumns().size() > 0) {
            DbPrimaryKey pk = table.getPrimaryKey();

            sql += ", CONSTRAINT " + pk.getPkName() + " PRIMARY KEY ( ";

            int seq = Integer.MIN_VALUE;
            isFirst = true;
            for (DbPrimaryKeyColumn primaryKeyColumnType : pk.getColumns()) {
                DbPrimaryKeyColumn column = primaryKeyColumnType;
                int seqTemp = Integer.MAX_VALUE;
                for (DbPrimaryKeyColumn columnTemp : pk.getColumns()) {
                    if (seq < columnTemp.getKeySeq() && columnTemp.getKeySeq() < seqTemp) {
                        seqTemp = columnTemp.getKeySeq();
                        column = columnTemp;
                    }
                }
                seq = column.getKeySeq();

                if (!isFirst)
                    sql += ",";
                else
                    isFirst = !isFirst;

                sql += column.getColumnName();
            }
            sql += " )";
        }

        sql += " )";

        Statement ps = null;
        try {
            ps = this.getConnection().createStatement();
            ps.executeUpdate(sql);
            this.getConnection().commit();
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        }
        finally {
            DbUtils.close(ps);
            ps = null;
        }

    }

    public void createForeignKey(DbTable view) {
    }

    public void dropTable(DbTable table) {
        if (table == null)
            return;

        dropTable(table.getName());
    }

    public void dropTable(String nameTable) {
        if (nameTable == null || nameTable.trim().length() == 0)
            return;

        String sql = "drop table " + nameTable;

        Statement ps = null;
        try {
            ps = this.getConnection().createStatement();
            ps.executeUpdate(sql);
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        }
        finally {
            DbUtils.close(ps);
            ps = null;
        }
    }

    public void dropSequence(String nameSequence) {
    }

    public void dropConstraint(DbForeignKey impPk) {
        if (impPk == null) {
            return;
        }

        String sql = "ALTER TABLE " + impPk.getPkTableName() + " DROP CONSTRAINT " + impPk.getPkName();

        PreparedStatement ps = null;
        try {
            ps = this.getConnection().prepareStatement(sql);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        }
        finally {
            DbUtils.close(ps);
            ps = null;
        }
    }

    public void addColumn(DbTable table, DbField field) {
    }

    public String getOnDeleteSetNull() {
        return null;
    }

    public String getDefaultTimestampValue() {
        return "CURRENT TIMESTAMP";
    }

    public List<DbView> getViewList(String schemaPattern, String tablePattern) {
        return DatabaseManager.getViewList(getConnection(), schemaPattern, tablePattern);
    }

    public List<DbSequence> getSequnceList(String schemaPattern) {
        return new ArrayList<DbSequence>();
    }

    public String getViewText(DbView view) {
        return null;
    }

    public void createView(DbView view) {
        if (view == null ||
            view.getName() == null || view.getName().length() == 0 ||
            view.getText() == null || view.getText().length() == 0
        )
            return;

        String sql_ =
            "CREATE VIEW " + view.getName() +
            " AS " +
            Utils.replaceStringArray(view.getText(),
                new String[][]{{"||", "+"}, {"\n", " "}}).trim();

        Statement ps = null;
        try {
            ps = this.getConnection().createStatement();
            ps.execute(sql_);
        }
        catch (SQLException e) {
            String errorString = "Error create view. Error code " + e.getErrorCode() + "\n" + sql_;
            log.error(errorString, e);
            throw new DbRevisionException(errorString, e);
        }
        finally {
            DbUtils.close(ps);
            ps = null;
        }
    }

    public void createSequence(DbSequence seq) {
    }

    public void setLongVarbinary(PreparedStatement ps, int index, DbDataFieldData fieldData)
        throws SQLException {
        ps.setNull(index, Types.VARCHAR);
    }

    public void setLongVarchar(PreparedStatement ps, int index, DbDataFieldData fieldData)
        throws SQLException {
        ps.setString(index, "");
    }

    public String getClobField(ResultSet rs, String nameField, int maxLength)
        throws SQLException {
        return null;
    }
/*
            CLOB clob = ((OracleResultSet) rs).getCLOB(nameField);

            if (clob == null)
                return null;

            return clob.getSubString(1, maxLength);
        }
*/

    public boolean testExceptionTableNotFound(Exception e) {
        if (e instanceof SQLException) {
            if (((SQLException) e).getErrorCode() == -204)
                return true;
        }
        return false;
    }

    public boolean testExceptionIndexUniqueKey(Exception e, String index) {
        if (e instanceof SQLException) {
            if (((SQLException) e).getErrorCode() == -(org.hsqldb.Trace.VIOLATION_OF_UNIQUE_INDEX))
                return true;
        }
/*
        if ((e instanceof SQLException) &&
                ((e.toString().indexOf("ORA-00001") != -1) &&
                (e.toString().indexOf(index) != -1)))

            return true;
*/
        return false;
    }

    public boolean testExceptionIndexUniqueKey(Exception e) {
        return false;
    }

    public boolean testExceptionTableExists(Exception e) {
        if (e instanceof SQLException) {
            if (((SQLException) e).getErrorCode() == -601)
                return true;
        }
        return false;
    }

    public boolean testExceptionViewExists(Exception e) {
        if (e instanceof SQLException) {
            if (((SQLException) e).getErrorCode() == -601)
                return true;
        }
        return false;
    }

    public boolean testExceptionSequenceExists(Exception e) {
        return false;
    }

    public boolean testExceptionConstraintExists(Exception e) {
        if (e instanceof SQLException) {
            if (((SQLException) e).getErrorCode() == -(org.hsqldb.Trace.CONSTRAINT_ALREADY_EXISTS))
                return true;
        }
        return false;
    }
}

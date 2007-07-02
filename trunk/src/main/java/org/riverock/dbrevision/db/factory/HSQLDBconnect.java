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
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import org.riverock.dbrevision.annotation.schema.db.*;
import org.riverock.dbrevision.db.DatabaseAdapter;
import org.riverock.dbrevision.db.DatabaseManager;
import org.riverock.dbrevision.exception.DbRevisionException;

/**
 *
 * $Author: serg_main $
 *
 * $Id: HSQLDBconnect.java 1141 2006-12-14 14:43:29Z serg_main $
 *
 */
@SuppressWarnings({"UnusedAssignment"})
public class HSQLDBconnect extends DatabaseAdapter {
    private static Logger log = Logger.getLogger( HSQLDBconnect.class );

    /**
     * get family for this adapter
     * @return family
     */
    public Family getFamily() {
        return Family.HSQLDB_FAMALY;
    }

    public HSQLDBconnect(Connection conn) {
        super(conn);
    }

    public int getMaxLengthStringField() {
        return 1000000;
    }

    public boolean isBatchUpdate() {
        return false;
    }

    public boolean isNeedUpdateBracket() {
        return true;
    }

    public boolean isByteArrayInUtf8() {
        return false;
    }

    public String getClobField(ResultSet rs, String nameField) throws SQLException {
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

        String sql = "create table \"" + table.getName() + "\"\n" +
            "(";

        boolean isFirst = true;

        for (DbField field : table.getFields()) {
            if (!isFirst)
                sql += ",";
            else
                isFirst = !isFirst;

            sql += "\n\"" + field.getName() + "\"";
            int fieldType = field.getJavaType();
            switch (fieldType) {

                case Types.NUMERIC:
                case Types.DECIMAL:
                    sql += " DECIMAL";
                    break;

                case Types.INTEGER:
                    sql += " INTEGER";
                    break;

                case Types.DOUBLE:
                    sql += " DOUBLE";
                    break;

                case Types.CHAR:
                case Types.VARCHAR:
                    sql += " VARCHAR";
                    break;

                case Types.DATE:
                case Types.TIMESTAMP:
                    sql += " TIMESTAMP";
                    break;

                case Types.LONGVARCHAR:
                    // Oracle 'long' fields type
                    sql += " LONGVARCHAR";
                    break;

                case Types.LONGVARBINARY:
                    // Oracle 'long raw' fields type
                    sql += " LONGVARBINARY";
                    break;

                default:
                    field.setJavaStringType("unknown field type field - " + field.getName() + " javaType - " + field.getJavaType());
                    System.out.println("unknown field type field - " + field.getName() + " javaType - " + field.getJavaType());
            }

            if (field.getDefaultValue() != null) {
                String val = field.getDefaultValue().trim();

                if (StringUtils.isNotBlank(val)) {
                    switch (fieldType) {
                        case Types.CHAR:
                        case Types.VARCHAR:
                            val = "'" + val + "'";
                            break;
                        case Types.TIMESTAMP:
                        case Types.DATE:
//                            if (DatabaseManager.checkDefaultTimestamp(val))
//                                val = "'CURRENT_TIMESTAMP'";

                            break;
                        default:
                    }
                    sql += (" DEFAULT " + val);
                }
            }

            if (field.getNullable() == DatabaseMetaData.columnNoNulls) {
                sql += " NOT NULL ";
            }
        }
        if (table.getPrimaryKey() != null && table.getPrimaryKey().getColumns().size() > 0) {
            DbPrimaryKey pk = table.getPrimaryKey();

            String namePk = pk.getColumns().get(0).getPkName();

//            constraintDefinition:
//            [ CONSTRAINT name ]
//            UNIQUE ( column [,column...] ) |
//            PRIMARY KEY ( column [,column...] ) |

            sql += ",\nCONSTRAINT " + namePk + " PRIMARY KEY (\n";

            int seq = Integer.MIN_VALUE;
            isFirst = true;
            for (DbPrimaryKeyColumn column : pk.getColumns()) {
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
            sql += "\n)";
        }
        sql += "\n)";

        Statement ps = null;
        try {
            ps = this.getConnection().createStatement();
            ps.execute(sql);
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        }
        finally {
            DatabaseManager.close(ps);
            ps = null;
        }

    }

    public void createForeignKey(DbTable view) {
    }

    public void dropTable(DbTable table) {
        dropTable(table.getName());
    }

    public void dropTable(String nameTable) {
        if (nameTable == null) {
            return;
        }
        String sql = "drop table \"" + nameTable + "\"\n";
        PreparedStatement ps = null;
        try {
            ps = this.getConnection().prepareStatement(sql);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        }
        finally {
            DatabaseManager.close(ps);
            ps = null;
        }
    }

    public void dropSequence(String nameSequence) {
    }

    public void dropConstraint(DbImportedPKColumn impPk) {
        if (impPk == null)
            return;

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
            DatabaseManager.close(ps);
            ps = null;
        }
    }

    public void addColumn(DbTable table, DbField field) {
        String sql = "alter table \"" + table.getName() + "\" add column " + field.getName() + " ";

        int fieldType = field.getJavaType();
        switch (fieldType) {

            case Types.NUMERIC:
            case Types.DECIMAL:
                sql += " DECIMAL";
                break;

            case Types.INTEGER:
                sql += " INTEGER";
                break;

            case Types.DOUBLE:
                sql += " DOUBLE";
                break;

            case Types.CHAR:
            case Types.VARCHAR:
                sql += " VARCHAR";
                break;

            case Types.TIMESTAMP:
            case Types.DATE:
                sql += " TIMESTAMP";
                break;

            case Types.LONGVARCHAR:
                // Oracle 'long' fields type
                sql += " LONGVARCHAR";
                break;

            case Types.LONGVARBINARY:
                // Oracle 'long raw' fields type
                sql += " LONGVARBINARY";
                break;

            default:
                String errorString = "unknown field type field - " + field.getName() + " javaType - " + field.getJavaType();
                log.error(errorString);
                throw new DbRevisionException(errorString);
        }

        if (field.getDefaultValue() != null) {
            String val = field.getDefaultValue().trim();

            if (StringUtils.isNotBlank(val)) {
                switch (fieldType) {
                    case Types.CHAR:
                    case Types.VARCHAR:
                        val = "'" + val + "'";
                        break;
                    case Types.TIMESTAMP:
                    case Types.DATE:
//                            if (DatabaseManager.checkDefaultTimestamp(val))
//                                val = "'CURRENT_TIMESTAMP'";

                        break;
                    default:
                }
                sql += (" DEFAULT " + val);
            }
        }

        if (field.getNullable() == DatabaseMetaData.columnNoNulls) {
            sql += " NOT NULL ";
        }


        if (log.isDebugEnabled())
            log.debug("addColumn sql - \n" + sql);

        PreparedStatement ps = null;
        try {
            ps = this.getConnection().prepareStatement(sql);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        }
        finally {
            DatabaseManager.close(ps);
            ps = null;
        }
    }

    public String getOnDeleteSetNull() {
        return "";
    }

    public String getDefaultTimestampValue() {
        return "current_timestamp";
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

        String sql_ = "create VIEW " + view.getName() + " as " + view.getText();
        PreparedStatement ps = null;
        try {
            ps = this.getConnection().prepareStatement(sql_);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        } finally {
            DatabaseManager.close(ps);
            ps = null;
        }
    }

    public void createSequence(DbSequence seq) {
    }

    public void setLongVarbinary(PreparedStatement ps, int index, DbDataFieldData fieldData)
        throws SQLException {
        ps.setNull(index, Types.LONGVARBINARY);
    }

    public void setLongVarchar(PreparedStatement ps, int index, DbDataFieldData fieldData)
        throws SQLException {
        ps.setNull(index, Types.LONGVARCHAR);
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

    /**
     * Возвращает значение сиквенса(последовательности) для данного имени последовательности.
     * Для разных коннектов к разным базам данных может быть решена по разному.
     *
     * @param sequence - String. Имя последовательноти для получения следующего значения.
     * @return long - следующее значение для ключа из последовательности
     * @throws SQLException
     */
    public long getSequenceNextValue(CustomSequence sequence)
        throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement("select max(" + sequence.getColumnName() + ") max_id from " + sequence.getTableName());

            rs = ps.executeQuery();

            if (rs.next())
                return rs.getLong(1) + 1;
        }
        finally {
            DatabaseManager.close(rs, ps);
            rs = null;
            ps = null;
        }

        return 1;
    }

    public boolean testExceptionTableNotFound(Exception e) {
        if (e == null)
            return false;

        if (e instanceof SQLException) {
            if (((SQLException) e).getErrorCode() == -(org.hsqldb.Trace.TABLE_NOT_FOUND))
                return true;
        }
        return false;
    }

    public boolean testExceptionIndexUniqueKey(Exception e, String index) {
        if (e == null)
            return false;

        if (e instanceof SQLException) {
            if (((SQLException) e).getErrorCode() == -(org.hsqldb.Trace.VIOLATION_OF_UNIQUE_INDEX) &&
                (e.toString().indexOf(index) != -1))
                return true;
        }
        return false;
    }

    public boolean testExceptionIndexUniqueKey(Exception e) {
        if (e == null)
            return false;

        if (e instanceof SQLException) {
            if (((SQLException) e).getErrorCode() == -(org.hsqldb.Trace.VIOLATION_OF_UNIQUE_INDEX))
                return true;
        }
        return false;
    }

    public boolean testExceptionTableExists(Exception e) {
        if (e == null)
            return false;

        if (e instanceof SQLException) {
            if (((SQLException) e).getErrorCode() == -(org.hsqldb.Trace.TABLE_ALREADY_EXISTS))
                return true;
        }
        return false;
    }

    public boolean testExceptionViewExists(Exception e) {
        if (e == null)
            return false;

        if (e instanceof SQLException) {
            if (((SQLException) e).getErrorCode() == -(org.hsqldb.Trace.VIEW_ALREADY_EXISTS))
                return true;
        }
        return false;
    }

    public boolean testExceptionSequenceExists(Exception e) {
        return false;
    }

    public boolean testExceptionConstraintExists(Exception e) {
        if (e == null)
            return false;

        if (e instanceof SQLException) {
            if (((SQLException) e).getErrorCode() == -(org.hsqldb.Trace.CONSTRAINT_ALREADY_EXISTS))
                return true;
        }
        return false;
    }
}
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
package org.riverock.dbrevision.db.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

import oracle.jdbc.driver.OracleResultSet;
import oracle.sql.CLOB;

import org.riverock.dbrevision.annotation.schema.db.DbDataFieldData;
import org.riverock.dbrevision.annotation.schema.db.DbField;
import org.riverock.dbrevision.annotation.schema.db.DbForeignKey;
import org.riverock.dbrevision.annotation.schema.db.DbPrimaryKey;
import org.riverock.dbrevision.annotation.schema.db.DbPrimaryKeyColumn;
import org.riverock.dbrevision.annotation.schema.db.DbSequence;
import org.riverock.dbrevision.annotation.schema.db.DbTable;
import org.riverock.dbrevision.annotation.schema.db.DbView;
import org.riverock.dbrevision.db.Database;
import org.riverock.dbrevision.db.DatabaseManager;
import org.riverock.dbrevision.exception.DbRevisionException;
import org.riverock.dbrevision.exception.ViewAlreadyExistException;
import org.riverock.dbrevision.exception.TableNotFoundException;
import org.riverock.dbrevision.utils.DbUtils;

/**
 * $Id: PostgreeSqlDatabase.java 1141 2006-12-14 14:43:29Z serg_main $
 */
public class PostgreeSqlDatabase extends Database {
    private static Logger log = Logger.getLogger(PostgreeSqlDatabase.class);

    public int getMaxLengthStringField() {
        return 4000;
    }

    public boolean isBatchUpdate() {
        return true;
    }

    public boolean isNeedUpdateBracket() {
        return true;
    }

    public boolean isByteArrayInUtf8() {
        return true;
    }

    public boolean isSchemaSupports() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isForeignKeyControlSupports() {
        return false;
    }

    @Override
    public void changeForeignKeyState(DbForeignKey key, ForeingKeyState state) {
        if (!isForeignKeyControlSupports()) {
            throw new IllegalStateException( "This database type not supported changing state of FK.");
        }
        String s;
        switch (state) {

            case DISABLE:
                s = "DISABLE";
                break;
            case ENABLE:
                s = "ENABLE";
                break;
            default:
                throw new IllegalArgumentException( "Unknown state "+ state);
        }
        String sql = "ALTER TABLE "+key.getFkTableName()+" MODIFY CONSTRAINT "+key.getFkName()+" " + s;

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

    @Override
    public void changeNullableState(DbTable table, DbField field, NullableState state) {
        throw new DbRevisionException("Not implemented");
    }

    public String getDefaultSchemaName(DatabaseMetaData databaseMetaData) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getClobField(ResultSet rs, String nameField) {
        return getClobField(rs, nameField, 20000);
    }

    public void createTable(DbTable table) {
        if (table == null || table.getFields().isEmpty())
            return;

        String sql = "create table \"" + table.getName() + "\"\n" +
            "(";

        boolean isFirst = true;

        for (DbField field : table.getFields()) {
            if (!isFirst)
                sql += ",";
            else
                isFirst = !isFirst;

            sql += "\n\"" + field.getName() + "\"";
            switch (field.getJavaType()) {
                case Types.DECIMAL:
                case Types.DOUBLE:
                case Types.NUMERIC:
                case Types.INTEGER:
                    if (field.getDecimalDigit()==0)
                        sql += " NUMBER";
                    else
                        sql += " NUMBER(" + (field.getSize()==null || field.getSize()>31?31:field.getSize()) + "," + field.getDecimalDigit() + ")";
                    break;

                case Types.CHAR:
                    sql += " VARCHAR2(1)";
                    break;

                case Types.VARCHAR:
                    if (field.getSize() < this.getMaxLengthStringField())
                        sql += " VARCHAR2(" + field.getSize() + ")";
                    else
                        sql += (" VARCHAR2(" + this.getMaxLengthStringField() + ")");
                    break;

                case Types.DATE:
                case Types.TIMESTAMP:
                    sql += " DATE";
                    break;

                case Types.LONGVARCHAR:
                    sql += " LONGVARCHAR";
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

                //TODO rewrite init of def as in createTable

                if (DatabaseManager.checkDefaultTimestamp(val))
                    val = "SYSDATE";

                sql += (" DEFAULT " + val);
            }

            if (field.getNullable() == DatabaseMetaData.columnNoNulls) {
                sql += " NOT NULL ";
            }
        }
        if (table.getPrimaryKey() != null && !table.getPrimaryKey().getColumns().isEmpty()) {
            DbPrimaryKey pk = table.getPrimaryKey();

//            constraintDefinition:
//            [ CONSTRAINT name ]
//            UNIQUE ( column [,column...] ) |
//            PRIMARY KEY ( column [,column...] ) |

            sql += ",\nCONSTRAINT " + pk.getPkName() + " PRIMARY KEY (\n";

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

        PreparedStatement ps = null;
        try {
            ps = this.getConnection().prepareStatement(sql);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            if (!testExceptionTableExists(e)) {
                System.out.println("sql " + sql);
                System.out.println("code " + e.getErrorCode());
                System.out.println("state " + e.getSQLState());
                System.out.println("message " + e.getMessage());
                System.out.println("string " + e.toString());
            }
            throw new DbRevisionException(e);
        }
        finally {
            DbUtils.close(ps);
        }
    }

    /*
ALTER TABLE a_test_1
ADD CONSTRAINT a_test_1_fk FOREIGN KEY (id, id_test)
REFERENCES a_test (id_test,id_lang) ON DELETE SET NULL
/

ALTER TABLE a_test_1
ADD CONSTRAINT a_test_1_fk2 FOREIGN KEY (text1, id_text)
REFERENCES a_test_2 (text2,text_id) ON DELETE CASCADE
DEFERRABLE INITIALLY DEFERRED
/
    */

    public void dropTable(DbTable table) {
        dropTable(table.getName());
    }

    public void dropTable(String nameTable) {
        if (nameTable == null)
            return;

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
            DbUtils.close(ps);
            //noinspection UnusedAssignment
            ps = null;
        }
    }

    public void dropSequence(String nameSequence) {
        if (nameSequence == null)
            return;

        String sql = "drop sequence  " + nameSequence;
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
            //noinspection UnusedAssignment
            ps = null;
        }
    }

    public void addColumn(DbTable table, DbField field) {
        if (log.isDebugEnabled())
            log.debug("addColumn(DbTable table, DbField field)");

        String sql = "alter table " + table.getName() + " add ( " + field.getName() + " ";

        switch (field.getJavaType()) {
            case Types.DECIMAL:
            case Types.DOUBLE:
            case Types.NUMERIC:
            case Types.INTEGER:
                if (field.getDecimalDigit() == 0)
                    sql += " NUMBER";
                else
                    sql += " NUMBER(" + (field.getSize()==null || field.getSize()>31?31:field.getSize()) + "," + field.getDecimalDigit() + ")";
                break;

            case Types.CHAR:
                sql += " VARCHAR2(1)";
                break;

            case Types.VARCHAR:
                if (field.getSize() < this.getMaxLengthStringField())
                    sql += " VARCHAR2(" + field.getSize() + ")";
                else
                    sql += (" VARCHAR2(" + this.getMaxLengthStringField() + ")");
                break;

            case Types.DATE:
            case Types.TIMESTAMP:
                sql += " DATE";
                break;

            case Types.LONGVARCHAR:
                sql += " LONGVARCHAR";
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

            //TODO rewrite init of def as in createTable
//                if (!val.equalsIgnoreCase("null"))
//                    val = "'"+val+"'";
            if (DatabaseManager.checkDefaultTimestamp(val)) {
                val = "current_timestamp";
            }

            sql += (" DEFAULT " + val);
        }

        if (field.getNullable() == DatabaseMetaData.columnNoNulls) {
            sql += " NOT NULL ";
        }
        sql += ")";

        if (log.isDebugEnabled())
            log.debug("addColumn sql - " + sql);

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
            //noinspection UnusedAssignment
            ps = null;
        }
    }

    public String getOnDeleteSetNull() {
        return "ON DELETE SET NULL";
    }

    public String getDefaultTimestampValue() {
        return "SYSDATE";
    }

    public List<DbView> getViewList(String schemaPattern, String tablePattern) {
        return DatabaseManager.getViewList(getConnection(), schemaPattern, tablePattern);
    }

    public List<DbSequence> getSequnceList(String schemaPattern) {
        String sql_ =
            "select SEQUENCE_NAME, MIN_VALUE, TO_CHAR(MAX_VALUE) MAX_VALUE, " +
                "INCREMENT_BY, CYCLE_FLAG, ORDER_FLAG, CACHE_SIZE, LAST_NUMBER " +
                "from SYS.ALL_SEQUENCES " +
                "where SEQUENCE_OWNER=?";

        PreparedStatement ps = null;
        ResultSet rs = null;
        List<DbSequence> v = new ArrayList<DbSequence>();
        try {
            ps = this.getConnection().prepareStatement(sql_);

            ps.setString(1, schemaPattern);
            rs = ps.executeQuery();

            while (rs.next()) {
                DbSequence seq = new DbSequence();
                seq.setName(DbUtils.getString(rs, "SEQUENCE_NAME"));
                seq.setMinValue(DbUtils.getInteger(rs, "MIN_VALUE"));
                seq.setMaxValue(DbUtils.getString(rs, "MAX_VALUE"));
                seq.setIncrementBy(DbUtils.getInteger(rs, "INCREMENT_BY"));
                seq.setIsCycle(DbUtils.getString(rs, "CYCLE_FLAG").equals("Y") ? Boolean.TRUE : Boolean.FALSE);
                seq.setIsOrder(DbUtils.getString(rs, "ORDER_FLAG").equals("Y") ? Boolean.TRUE : Boolean.FALSE);
                seq.setCacheSize(DbUtils.getInteger(rs, "CACHE_SIZE"));
                seq.setLastNumber(DbUtils.getLong(rs, "LAST_NUMBER"));
                v.add(seq);
            }
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        } finally {
            DbUtils.close(rs, ps);
            //noinspection UnusedAssignment
            rs = null;
            //noinspection UnusedAssignment
            ps = null;
        }
        if (v.size() > 0)
            return v;
        else
            return null;
    }

    public String getViewText(DbView view) {
        String sql_ = "select TEXT from SYS.ALL_VIEWS where OWNER=? and VIEW_NAME=?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = this.getConnection().prepareStatement(sql_);

            ps.setString(1, view.getSchema());
            ps.setString(2, view.getName());
            rs = ps.executeQuery();

            if (rs.next()) {
                if (log.isDebugEnabled())
                    log.debug("Found text of view " + view.getSchema() + "." + view.getName());

                return getStream(rs, "TEXT", 0x10000);
            }
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        } catch (IOException e) {
            throw new DbRevisionException(e);
        } finally {
            DbUtils.close(rs, ps);
            //noinspection UnusedAssignment
            rs = null;
            //noinspection UnusedAssignment
            ps = null;
        }
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
            if (testExceptionViewExists(e)) {
                throw new ViewAlreadyExistException("View "+view.getName()+" already exist.");
            }
            if (testExceptionTableNotFound(e)) {
                throw new TableNotFoundException("View "+view.getName()+" refered to unknown table.");
            }
            throw new DbRevisionException(e);
        }
        finally {
            DbUtils.close(ps);
        }
    }

    public void createSequence(DbSequence seq) {
        if (seq == null) {
            return;
        }
/*
        CREATE SEQUENCE MILLENNIUM.SEQ_WM_PORTAL_XSLT
         START WITH  1
         INCREMENT BY  1
         MINVALUE  1
         MAXVALUE  9999999
         NOCACHE
         NOCYCLE

        CREATE SEQUENCE MILLENNIUM.DSF
         START WITH  1
         INCREMENT BY  1
         MAXVALUE  999999999999999
         CACHE 3
         CYCLE
         ORDER
*/
        String sql_ =
            "CREATE SEQUENCE " + seq.getName() + " " +
                "START WITH " + seq.getLastNumber() + " " +
                "INCREMENT BY " + seq.getIncrementBy() + " " +
                "MINVALUE " + seq.getMinValue() + " " +
                "MAXVALUE " + seq.getMaxValue() + " " +
                (seq.getCacheSize()==null || seq.getCacheSize()==0 ? "NOCACHE" : "CACHE " + seq.getCacheSize()) + " " +
                (Boolean.TRUE.equals(seq.isIsCycle()) ? "CYCLE" : "NOCYCLE") + " " +
                (Boolean.TRUE.equals(seq.isIsOrder()) ? "ORDER" : "") + " ";

        PreparedStatement ps = null;

        try {
            ps = this.getConnection().prepareStatement(sql_);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        }
        finally {
            DbUtils.close(ps);
        }
    }

    public void setLongVarbinary(PreparedStatement ps, int index, DbDataFieldData fieldData) {
        try {
            ps.setNull(index, Types.LONGVARBINARY);
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        }
    }

    public void setLongVarchar(PreparedStatement ps, int index, DbDataFieldData fieldData) {
        try {
            ps.setNull(index, Types.LONGVARCHAR);
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        }
    }

    public byte[] getBlobField(ResultSet rs, String nameField, int maxLength) {
        try {
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
        catch (Exception e) {
            throw new DbRevisionException(e);
        }
    }

    public String getClobField(ResultSet rs, String nameField, int maxLength) {
        try {
            CLOB clob = ((OracleResultSet) rs).getCLOB(nameField);

            if (clob == null)
                return null;

            return clob.getSubString(1, maxLength);
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        }
    }

    public String getStream(ResultSet rs, String nameField, int maxLength) throws SQLException, IOException {

        InputStream instream = rs.getBinaryStream(1);

        // Create temporary buffer for read
        byte[] buffer = new byte[maxLength];

        // length of bytes read
        int length;
        String ret = "";
        boolean flag = false;
        // Fetch data
        if ((length = instream.read(buffer)) != -1) {
            flag = true;
            ret = new String(buffer, 0, length, "utf-8");

            if (log.isDebugEnabled())
                log.debug("text from stream\n" + ret);
        }

        // Close input stream
        try {
            instream.close();
            instream = null;
        }
        catch (Exception e) {
            log.warn("error close of stream", e);
        }

        if (flag)
            return ret;
        else
            return null;
    }

    public boolean testExceptionTableNotFound(Exception e) {
        if (e == null)
            return false;

        if ((e instanceof SQLException) &&
            (e.toString().indexOf("ORA-00942") != -1))
            return true;
        return false;
    }

    public boolean testExceptionIndexUniqueKey(Exception e, String index) {
        if (e == null)
            return false;

        if ((e instanceof SQLException) &&
            ((e.toString().indexOf("ORA-00001") != -1) &&
                (e.toString().indexOf(index) != -1)))

            return true;

        return false;
    }

    public boolean testExceptionIndexUniqueKey(Exception e) {
        if (e == null)
            return false;

        if ((e instanceof SQLException) && ((e.toString().indexOf("ORA-00001") != -1)))
            return true;

        return false;
    }

    public boolean testExceptionTableExists(Exception e) {
        if (e == null)
            return false;

        if ((e instanceof SQLException) &&
            (e.toString().indexOf("ORA-00955") != -1))
            return true;

        return false;
    }

    public boolean testExceptionViewExists(Exception e) {
        if (e == null)
            return false;

        if ((e instanceof SQLException) &&
            (e.toString().indexOf("ORA-00955") != -1))
            return true;

        return false;
    }

    public boolean testExceptionSequenceExists(Exception e) {
        if (e == null)
            return false;

        if ((e instanceof SQLException) &&
            (e.toString().indexOf("ORA-00955") != -1))
            return true;

        return false;
    }

    public boolean testExceptionConstraintExists(Exception e) {
        if (e == null)
            return false;

        if ((e instanceof SQLException) &&
            (e.toString().indexOf("ORA-02275") != -1))
            return true;

        return false;
    }

    /**
     * get family for this adapter
     * @return family
     */
    public Family getFamily() {
        return Family.ORACLE;
    }

    public void setBlobField(String tableName, String fieldName, byte[] bytes, String whereQuery, Object[] objects, int[] fieldTyped) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public PostgreeSqlDatabase(Connection conn) {
        super(conn);
    }

}

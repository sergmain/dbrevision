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
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import oracle.jdbc.driver.OracleResultSet;
import oracle.sql.CLOB;
import org.apache.log4j.Logger;

import org.riverock.dbrevision.db.DatabaseAdapter;
import org.riverock.dbrevision.db.DatabaseManager;
import org.riverock.dbrevision.annotation.schema.db.DbView;
import org.riverock.dbrevision.annotation.schema.db.DbTable;
import org.riverock.dbrevision.annotation.schema.db.DbField;
import org.riverock.dbrevision.annotation.schema.db.DbSequence;
import org.riverock.dbrevision.annotation.schema.db.DbPrimaryKey;
import org.riverock.dbrevision.annotation.schema.db.DbPrimaryKeyColumn;
import org.riverock.dbrevision.annotation.schema.db.DbImportedPKColumn;
import org.riverock.dbrevision.annotation.schema.db.DbDataFieldData;
import org.riverock.dbrevision.annotation.schema.db.CustomSequence;
import org.riverock.dbrevision.utils.DbUtils;

/**
 * $Id: PostgreeSQLconnect.java 1141 2006-12-14 14:43:29Z serg_main $
 */
public class PostgreeSQLconnect extends DatabaseAdapter {
    private static Logger log = Logger.getLogger(PostgreeSQLconnect.class);

    public int getMaxLengthStringField() {
        return 4000;
    }

    public boolean getIsBatchUpdate() {
        return true;
    }

    public boolean getIsNeedUpdateBracket() {
        return true;
    }

    public boolean getIsByteArrayInUtf8() {
        return true;
    }

    public String getClobField(ResultSet rs, String nameField)
        throws SQLException {
        return getClobField(rs, nameField, 20000);
    }

    public void createTable(DbTable table) throws Exception {
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
            switch (field.getJavaType().intValue()) {
                case Types.DECIMAL:
                case Types.DOUBLE:
                case Types.NUMERIC:
                case Types.INTEGER:
                    if (field.getDecimalDigit().intValue() == 0)
                        sql += " NUMBER";
                    else
                        sql += " NUMBER(" + field.getSize() + "," + field.getDecimalDigit() + ")";
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

//                if (!val.equalsIgnoreCase("null"))
//                    val = "'"+val+"'";

                if (DatabaseManager.checkDefaultTimestamp(val))
                    val = "SYSDATE";

                sql += (" DEFAULT " + val);
            }

            if (field.getNullable().intValue() == DatabaseMetaData.columnNoNulls) {
                sql += " NOT NULL ";
            }
        }
        if (table.getPrimaryKey() != null && !table.getPrimaryKey().getColumns().isEmpty()) {
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

//        System.out.println( sql );

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
            throw e;
        }
        finally {
            DatabaseManager.close(ps);
            ps = null;
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

    public void dropTable(DbTable table) throws Exception {
        dropTable(table.getName());
    }

    public void dropTable(String nameTable) throws Exception {
        if (nameTable == null)
            return;

        String sql = "drop table \"" + nameTable + "\"\n";

//        System.out.println( sql );

        PreparedStatement ps = null;
        try {
            ps = this.getConnection().prepareStatement(sql);
            ps.executeUpdate();
        }
        catch (SQLException e) {
//            System.out.println( "code "+e.getErrorCode() );
//            System.out.println( "state "+e.getSQLState() );
//            System.out.println( "message "+e.getMessage() );
//            System.out.println( "string "+e.toString() );
            throw e;
        }
        finally {
            DatabaseManager.close(ps);
            ps = null;
        }
    }

    public void dropSequence(String nameSequence) throws Exception {
        if (nameSequence == null)
            return;

        String sql = "drop sequence  " + nameSequence;
        PreparedStatement ps = null;
        try {
            ps = this.getConnection().prepareStatement(sql);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw e;
        }
        finally {
            DatabaseManager.close(ps);
            ps = null;
        }
    }

    public void dropConstraint(DbImportedPKColumn impPk) throws Exception {
        throw new Exception("not implemented");
    }

    public void addColumn(DbTable table, DbField field) throws Exception {
        if (log.isDebugEnabled())
            log.debug("addColumn(DbTable table, DbField field)");

        String sql = "alter table " + table.getName() + " add ( " + field.getName() + " ";

        switch (field.getJavaType().intValue()) {
            case Types.DECIMAL:
            case Types.DOUBLE:
            case Types.NUMERIC:
            case Types.INTEGER:
                if (field.getDecimalDigit().intValue() == 0)
                    sql += " NUMBER";
                else
                    sql += " NUMBER(" + field.getSize() + "," + field.getDecimalDigit() + ")";
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

//                if (!val.equalsIgnoreCase("null"))
//                    val = "'"+val+"'";
            if (DatabaseManager.checkDefaultTimestamp(val))
                val = "current_timestamp";

            sql += (" DEFAULT " + val);
        }

        if (field.getNullable().intValue() == DatabaseMetaData.columnNoNulls) {
            sql += " NOT NULL ";
        }
        sql += ")";

        if (log.isDebugEnabled())
            log.debug("Oracle addColumn sql - " + sql);

        Statement ps = null;
        try {
            ps = this.getConnection().createStatement();
            ps.executeUpdate(sql);
            this.getConnection().commit();
        }
        catch (SQLException e) {
            throw e;
        }
        finally {
            DatabaseManager.close(ps);
            ps = null;
        }
    }

    /**
     * in some DB (Oracle8.0) setTimestamp not work and we need work around
     *
     * @return String
     */
    public String getNameDateBind() {
        return "?";
    }

    public String getOnDeleteSetNull() {
        return "ON DELETE SET NULL";
    }

    /**
     * bind Timestamp value
     *
     * @param ps
     * @param stamp @see java.sql.Timestamp
     * @throws java.sql.SQLException
     */
    public void bindDate(PreparedStatement ps, int idx, Timestamp stamp) throws SQLException {
        ps.setTimestamp(idx, stamp);
    }

    public String getDefaultTimestampValue() {
        return "SYSDATE";
    }

    public void setDefaultValue(DbTable originTable, DbField originField) {
    }

    public List<DbView> getViewList(String schemaPattern, String tablePattern) throws Exception {
        return DatabaseManager.getViewList(getConnection(), schemaPattern, tablePattern);
    }

    public List<DbSequence> getSequnceList(String schemaPattern) throws Exception {
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
        finally {
            DatabaseManager.close(rs, ps);
            rs = null;
            ps = null;
        }
        if (v.size() > 0)
            return v;
        else
            return null;
    }

    public String getViewText(DbView view) throws Exception {
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

//                return getBlobField(rs, "TEXT", 0x10000);
//                return getClobField(rs, "TEXT", 0x10000);
                return getStream(rs, "TEXT", 0x10000);
//                InputStream stream=resultset.getAsciiStream(1);
//                return DbUtils.getString(rs, "TEXT", null);
            }
        }
        finally {
            DatabaseManager.close(rs, ps);
            rs = null;
            ps = null;
        }
        return null;
    }

    public void createView(DbView view)
        throws Exception {
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
        finally {
            DatabaseManager.close(ps);
            ps = null;
        }
    }

    public void createSequence(DbSequence seq) throws Exception {
        if (seq == null)
            return;
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
                (seq.getCacheSize().intValue() == 0 ? "NOCACHE" : "CACHE " + seq.getCacheSize()) + " " +
                (Boolean.TRUE.equals(seq.isIsCycle()) ? "CYCLE" : "NOCYCLE") + " " +
                (Boolean.TRUE.equals(seq.isIsOrder()) ? "ORDER" : "") + " ";

        PreparedStatement ps = null;

        try {
            ps = this.getConnection().prepareStatement(sql_);
            ps.executeUpdate();
        }
        catch (Exception e) {
            System.out.println("Error create sequence " + sql_);
            throw e;
        }
        finally {
            DatabaseManager.close(ps);
            ps = null;
        }
    }

    public void setLongVarbinary(PreparedStatement ps, int index, DbDataFieldData fieldData)
        throws SQLException {
        ps.setNull(index, Types.LONGVARBINARY);
    }

    public void setLongVarchar(PreparedStatement ps, int index, DbDataFieldData fieldData)
        throws SQLException {
        ps.setNull(index, Types.LONGVARCHAR);
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

    public String getClobField(ResultSet rs, String nameField, int maxLength)
        throws SQLException {
        CLOB clob = ((OracleResultSet) rs).getCLOB(nameField);

        if (clob == null)
            return null;

        return clob.getSubString(1, maxLength);
    }

    public String getStream(ResultSet rs, String nameField, int maxLength)
        throws Exception {

//        InputStream instream = rs.getAsciiStream(1);
        InputStream instream = rs.getBinaryStream(1);

        // Create temporary buffer for read
        byte[] buffer = new byte[maxLength];

        // length of bytes read
        int length = 0;

        String ret = "";
        boolean flag = false;
        // Fetch data
        if ((length = instream.read(buffer)) != -1) {
            flag = true;
            String dbCharset = getDc().getDatabaseCharset();
            if (dbCharset == null) {
                log.warn("DatabaseCharset element not defined. We will use 'utf8' charset instead");
                dbCharset = "utf8";
            }

            ret = new String(buffer, 0, length, dbCharset);

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

    public long getSequenceNextValue(String s)
        throws SQLException {
        CustomSequence seq = new CustomSequence();
        seq.setSequenceName(s);

        return getSequenceNextValue(seq);
    }

    public long getSequenceNextValue(CustomSequence seq)
        throws SQLException {
        if (seq == null)
            return -1;

        long id_ = -1;

        String sql_ = "select " + seq.getSequenceName() + ".nextval from dual";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = this.getConnection().prepareStatement(sql_);

            rs = ps.executeQuery();

            if (rs.next())
                id_ = rs.getLong(1);

        }
        finally {
            DatabaseManager.close(rs, ps);
            rs = null;
            ps = null;
        }

        return id_;
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

    public int getFamily() {
        return DatabaseManager.ORACLE_FAMALY;
    }

    public int getVersion() {
        return 8;
    }

    public int getSubVersion() {
        return 1;
    }

    public PostgreeSQLconnect(Connection conn) {
        super(conn);
    }

}

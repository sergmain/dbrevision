/*
 * Copyright 2007 Sergei Maslyukov at riverock.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riverock.dbrevision.db.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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



import oracle.jdbc.OracleResultSet;
import oracle.sql.CLOB;

import org.riverock.dbrevision.schema.db.DbDataFieldData;
import org.riverock.dbrevision.schema.db.DbField;
import org.riverock.dbrevision.schema.db.DbForeignKey;
import org.riverock.dbrevision.schema.db.DbPrimaryKey;
import org.riverock.dbrevision.schema.db.DbPrimaryKeyColumn;
import org.riverock.dbrevision.schema.db.DbSequence;
import org.riverock.dbrevision.schema.db.DbTable;
import org.riverock.dbrevision.schema.db.DbView;
import org.riverock.dbrevision.db.Database;
import org.riverock.dbrevision.db.DatabaseManager;
import org.riverock.dbrevision.exception.DbRevisionException;
import org.riverock.dbrevision.exception.ViewAlreadyExistException;
import org.riverock.dbrevision.exception.TableNotFoundException;
import org.riverock.dbrevision.utils.DbUtils;

/**
 * $Id: OracleDatabase.java 1141 2006-12-14 14:43:29Z serg_main $
 */
@SuppressWarnings({"UnusedAssignment"})
public class OracleDatabase extends Database {

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
        return true;
    }

    @Override
    public boolean isForeignKeyControlSupports() {
        return true;
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
            case ENABLE_VALIDATE:
                s = "ENABLE VALIDATE";
                break;
            default:
                throw new IllegalArgumentException( "Unknown state "+ state);
        }
        String sql = "ALTER TABLE "+key.getFkTable()+" MODIFY CONSTRAINT "+key.getFk()+" " + s;

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
        String s;
        switch (state) {

            case NULL:
                s = "NULL";
                break;
            case NOTNULL:
                s = "NOT NULL";
                break;
            default:
                throw new IllegalArgumentException( "Unknown state "+ state);
        }

/*
ALTER TABLE AUTH_ACCESS_GROUP
 MODIFY (
  ID_ACCESS_GROUP NOT NULL
)
         */
        String sql = "ALTER TABLE "+table.getT()+" MODIFY ( "+field.getName()+"  " + s +")";

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

    public String getDefaultSchemaName(DatabaseMetaData databaseMetaData) {
        try {
            return databaseMetaData.getUserName();
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        }
    }

    public String getClobField(ResultSet rs, String nameField) {
        return getClobField(rs, nameField, 20000);
    }

    public void createTable(DbTable table) {
        if (table == null || table.getFields().isEmpty())
            return;

        String sql = "create table " + table.getT() + " \n" +
            "(";

        boolean isFirst = true;

        for (DbField field : table.getFields()) {
            if (!isFirst)
                sql += ",";
            else
                isFirst = !isFirst;

            sql += "\n" + field.getName() + ' ';
            int fieldType = field.getType();
            switch (fieldType) {
                case Types.BIT:
                    sql += " NUMBER(1,0)";
                    break;

                case Types.TINYINT:
                    sql += " NUMBER(4,0)";
                    break;

                case Types.BIGINT:
                    sql += " NUMBER(38,0)";
                    break;

                case Types.DECIMAL:
                case Types.DOUBLE:
                case Types.NUMERIC:
                case Types.INTEGER:
                    if (field.getDigit() == null || field.getDigit() == 0)
                        sql += " NUMBER";
                    else
                        sql += " NUMBER(" + (field.getSize()==null || field.getSize()>38?38:field.getSize()) + "," + field.getDigit() + ")";
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

                case Types.BLOB:
                    sql += " BLOB";
                    break;

                case Types.CLOB:
                    sql += " CLOB";
                    break;

                default:
                    final String es = "unknown field type field - " + field.getName() + " javaType - " + field.getType();
                    throw new IllegalStateException(es);
//                    System.out.println(es);
            }

            if (field.getDef() != null) {
                String val = field.getDef().trim();

                switch (fieldType) {
                    case Types.CHAR:
                    case Types.VARCHAR:
                        if (!val.equalsIgnoreCase("null")) {
                            val = "'" + val + "'";
                        }
                        break;
                    case Types.DATE:
                    case Types.TIMESTAMP:
                        if (DatabaseManager.checkDefaultTimestamp(val)) {
                            val = getDefaultTimestampValue();
                        }
                        break;
                }
                sql += (" DEFAULT " + val);
            }

            if (field.getNullable() == DatabaseMetaData.columnNoNulls) {
                sql += " NOT NULL ";
            }
        }
        if (table.getPk() != null && !table.getPk().getColumns().isEmpty()) {
            DbPrimaryKey pk = table.getPk();

            //            constraintDefinition:
//            [ CONSTRAINT name ]
//            UNIQUE ( column [,column...] ) |
//            PRIMARY KEY ( column [,column...] ) |

            sql += ",\nCONSTRAINT " + pk.getPk() + " PRIMARY KEY (\n";

            int seq = Integer.MIN_VALUE;
            isFirst = true;
            for (DbPrimaryKeyColumn keyColumn : pk.getColumns()) {
                DbPrimaryKeyColumn column = keyColumn;
                int seqTemp = Integer.MAX_VALUE;
                for (DbPrimaryKeyColumn columnTemp : pk.getColumns()) {
                    if (seq < columnTemp.getSeq() && columnTemp.getSeq() < seqTemp) {
                        seqTemp = columnTemp.getSeq();
                        column = columnTemp;
                    }
                }
                seq = column.getSeq();

                if (!isFirst)
                    sql += ",";
                else
                    isFirst = !isFirst;

                sql += column.getC();
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
            ps = null;
        }

    }

    public void dropTable(DbTable table) {
        dropTable(table.getT());
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
            ps = null;
        }
    }

    public void addColumn(DbTable table, DbField field) {

        String sql = "alter table " + table.getT() + " add ( " + field.getName() + " ";

        int fieldType = field.getType();
        switch (fieldType) {
            case Types.DECIMAL:
            case Types.DOUBLE:
            case Types.NUMERIC:
            case Types.INTEGER:
                if (field.getDigit()== null)
                    sql += " NUMBER";
                else
                    sql += " NUMBER(" + (field.getSize()==null || field.getSize()>38?38:field.getSize()) + "," + field.getDigit() + ")";
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
                final String es = "unknown field type field - " + field.getName() + " javaType - " + field.getType();
                throw new IllegalStateException(es);
//                    System.out.println(es);
        }

        if (field.getDef() != null) {
            String val = field.getDef().trim();

            switch (fieldType) {
                case Types.CHAR:
                case Types.VARCHAR:
                    if (!val.equalsIgnoreCase("null")) {
                        val = "'" + val + "'";
                    }
                    break;
                case Types.DATE:
                case Types.TIMESTAMP:
                    if (DatabaseManager.checkDefaultTimestamp(val)) {
                        val = " SYSDATE ";
                    }
                    break;
            }

            sql += (" DEFAULT " + val);
        }

        if (field.getNullable() == DatabaseMetaData.columnNoNulls) {
            sql += " NOT NULL ";
        }
        sql += ")";

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

    public String getOnDeleteSetNull() {
        return "ON DELETE SET NULL";
    }

    public String getDefaultTimestampValue() {
        return "SYSDATE";
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
                seq.setMin(DbUtils.getInteger(rs, "MIN_VALUE"));
                seq.setMax(DbUtils.getString(rs, "MAX_VALUE"));
                seq.setInc(DbUtils.getInteger(rs, "INCREMENT_BY"));
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
            rs = null;
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

            ps.setString(1, view.getS());
            ps.setString(2, view.getT());
            rs = ps.executeQuery();

            if (rs.next()) {
                return getStream(rs, "TEXT", 0x10000);
            }
            return null;

        } catch (SQLException e) {
            throw new DbRevisionException(e);
        } catch (IOException e) {
            throw new DbRevisionException(e);
        } finally {
            DbUtils.close(rs, ps);
            rs = null;
            ps = null;
        }
    }

    public void createView(DbView view) {
        if (view == null ||
            view.getT() == null || view.getT().length() == 0 ||
            view.getText() == null || view.getText().length() == 0
            )
            return;

        String sql_ = "create VIEW " + view.getT() + " as " + view.getText();
        PreparedStatement ps = null;
        try {
            ps = this.getConnection().prepareStatement(sql_);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            if (testExceptionViewExists(e)) {
                throw new ViewAlreadyExistException("View "+view.getT()+" already exist.");
            }
            if (testExceptionTableNotFound(e)) {
                throw new TableNotFoundException("View "+view.getT()+" refered to unknown table.");
            }
            throw new DbRevisionException(e);
        } finally {
            DbUtils.close(ps);
            ps = null;
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
                "INCREMENT BY " + seq.getInc() + " " +
                "MINVALUE " + seq.getMin() + " " +
                "MAXVALUE " + seq.getMax() + " " +
                (seq.getCacheSize() == 0 ? "NOCACHE" : "CACHE " + seq.getCacheSize()) + " " +
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
            ps = null;
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
        int length = 0;

        String ret = "";
        boolean flag = false;
        // Fetch data
        if ((length = instream.read(buffer)) != -1) {
            flag = true;
            ret = new String(buffer, 0, length, "utf-8");
        }

        // Close input stream
        try {
            instream.close();
            instream = null;
        }
        catch (Exception e) {
            //
        }


        if (flag)
            return ret;
        else
            return null;
    }

    public boolean testExceptionTableNotFound(Exception e) {
        return e != null && (e instanceof SQLException) && (e.toString().indexOf("ORA-00942") != -1);
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
        return e != null && (e instanceof SQLException) && ((e.toString().indexOf("ORA-00001") != -1));
    }

    public boolean testExceptionTableExists(Exception e) {
        return e != null && (e instanceof SQLException) && (e.toString().indexOf("ORA-00955") != -1);
    }

    public boolean testExceptionViewExists(Exception e) {
        return e != null && (e instanceof SQLException) && (e.toString().indexOf("ORA-00955") != -1);
    }

    public boolean testExceptionSequenceExists(Exception e) {
        return e != null && (e instanceof SQLException) && (e.toString().indexOf("ORA-00955") != -1);
    }

    public boolean testExceptionConstraintExists(Exception e) {
        return e != null && (e instanceof SQLException) && (e.toString().indexOf("ORA-02275") != -1);
    }

    /**
     * get family for this adapter
     * @return family
     */
    public Family getFamily() {
        return Family.ORACLE;
    }

    public void setBlobField(String tableName, String fieldName, byte[] bytes, String whereQuery, Object[] objects, int[] fieldTyped) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(
                "select "+fieldName+" from "+tableName+" where "+ whereQuery + " for update"
            );
            for (int i=0; i<objects.length; i++) {
                if (objects[i]!=null) {
                    ps.setObject(i, objects[i], fieldTyped[i]);
                }
                else {
                    ps.setNull(i, fieldTyped[i]);
                }
            }
            rs = ps.executeQuery();

            if (rs.next()) {
                Blob mapBlob = rs.getBlob(fieldName);
                OutputStream blobOutputStream = mapBlob.setBinaryStream(0L);
                blobOutputStream.write(bytes);
                blobOutputStream.flush();
                blobOutputStream.close();
                blobOutputStream=null;
            }
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        }
        catch (IOException e) {
            throw new DbRevisionException(e);
        }
        finally {
            DbUtils.close(rs, ps);
            rs=null;
            ps=null;
        }
    }

    public OracleDatabase(Connection conn) {
        super(conn);
    }

}

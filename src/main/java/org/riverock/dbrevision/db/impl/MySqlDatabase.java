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

import java.io.ByteArrayInputStream;
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
import java.util.Collections;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import org.apache.commons.lang3.StringUtils;
import org.riverock.dbrevision.schema.db.v3.DbDataFieldData;
import org.riverock.dbrevision.schema.db.v3.DbField;
import org.riverock.dbrevision.schema.db.v3.DbForeignKey;
import org.riverock.dbrevision.schema.db.v3.DbPrimaryKey;
import org.riverock.dbrevision.schema.db.v3.DbPrimaryKeyColumn;
import org.riverock.dbrevision.schema.db.v3.DbSequence;
import org.riverock.dbrevision.schema.db.v3.DbTable;
import org.riverock.dbrevision.schema.db.v3.DbView;
import org.riverock.dbrevision.db.Database;
import org.riverock.dbrevision.db.DatabaseManager;
import org.riverock.dbrevision.db.DbPkComparator;
import org.riverock.dbrevision.db.ViewManager;
import org.riverock.dbrevision.exception.DbRevisionException;
import org.riverock.dbrevision.exception.ViewAlreadyExistException;
import org.riverock.dbrevision.exception.TableNotFoundException;
import org.riverock.dbrevision.utils.DbUtils;


/**
 * MySQL database connect
 * $Author: serg_main $
 * <p/>
 * $Id: MySqlDatabase.java 1141 2006-12-14 14:43:29Z serg_main $
 */
@SuppressWarnings({"UnusedAssignment"})
public final class MySqlDatabase extends Database {

    /**
     * get family for this adapter
     * @return family
     */
    public Family getFamily() {
        return Family.MYSQL;
    }

    public void setBlobField(String tableName, String fieldName, byte[] bytes, String whereQuery, Object[] objects, int[] fieldTyped) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(
                "update "+tableName+" set "+fieldName+"=? where "+ whereQuery
            );
            int idx=1;
            ps.setBinaryStream(idx++, new ByteArrayInputStream(bytes), bytes.length);
            for (int i=0; i<objects.length; i++) {
                if (objects[i]!=null) {
                    ps.setObject(idx++, objects[i], fieldTyped[i]);
                }
                else {
                    ps.setNull(idx++, fieldTyped[i]);
                }
            }
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        }
        finally {
            DbUtils.close(rs, ps);
            rs=null;
            ps=null;
        }
    }

    public MySqlDatabase(Connection conn) {
        super(conn);
    }

    public int getMaxLengthStringField() {
        return 65535;
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

    public boolean isSchemaSupports() {
        return true;
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
        throw new DbRevisionException("Not implemented");
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

    public void createTable(DbTable table) {
        if (table == null || table.getFields().isEmpty()) {
            return;
        }

        String sql = "create table " + table.getT() + "\n" +
            "(";

        boolean isFirst = true;

        for (DbField field : table.getFields()) {
            if (!isFirst) {
                sql += ",";
            }
            else {
                isFirst = !isFirst;
            }

            sql += "\n" + field.getName() + "";
            int fieldType = field.getType();
            switch (fieldType) {

                case Types.BIT:
                    sql += " BIT";
                    break;

                case Types.TINYINT:
                    sql += " TINYINT";
                    break;

                case Types.BIGINT:
                    sql += " BIGINT";
                    break;

                case Types.NUMERIC:
                case Types.DECIMAL:
                    if (field.getDigit() == null || field.getDigit() == 0)
                        sql += " DECIMAL";
                    else
                        sql += " DECIMAL(" + (field.getSize()==null || field.getSize()>31?31:field.getSize()) + "," + field.getDigit() + ")";
                    break;

                case Types.INTEGER:
                    sql += " INTEGER";
                    break;

                case Types.DOUBLE:
                    if (field.getDigit() == null || field.getDigit() == 0)
                        sql += " DOUBLE";
                    else
                        sql += " DOUBLE(" + (field.getSize()==null || field.getSize()>31?31:field.getSize()) + "," + field.getDigit() + ")";
                    break;

                case Types.CHAR:
                    sql += " VARCHAR(1)";
                    break;

                case Types.VARCHAR:
                    if (field.getSize() < 0x5555)
                        sql += " VARCHAR(" + field.getSize() + ")";
                    else
                        sql += " TEXT";
                    break;

                case Types.TIMESTAMP:
                case Types.DATE:
                    sql += " TIMESTAMP";
                    break;

                case Types.LONGVARCHAR:
                    sql += " text ";
                    break;

                case Types.LONGVARBINARY:
                case Types.BLOB:
                    sql += " LONGBLOB";
                    break;

                case Types.CLOB:
                    sql += " LONGTEXT";
                    break;

                case Types.OTHER:
                    sql += " LONGTEXT";
                    break;

                default:
                    final String es = "unknown field type field - " + field.getName() + " javaType - " + field.getType();
                    throw new IllegalStateException(es);
//                    System.out.println(es);
            }

            if (field.getDef() != null) {
                String val = field.getDef().trim();

                if (StringUtils.isNotBlank(val)) {
                    switch (fieldType) {
                        case Types.CHAR:
                        case Types.VARCHAR:
                            val = "'" + val + "'";
                            break;
                        case Types.TIMESTAMP:
                        case Types.DATE:
                            if (DatabaseManager.checkDefaultTimestamp(val)) {
                                val = "CURRENT_TIMESTAMP";
                            }

                            break;
                        default:
                    }
                    sql += (" DEFAULT " + val);
                }
            }

            switch (field.getNullable()) {
                case DatabaseMetaData.columnNoNulls:
                    sql += " NOT NULL ";
                    break;

                case DatabaseMetaData.columnNullable:
                    switch (fieldType) {
                        case Types.DATE:
                        case Types.TIMESTAMP:
                            sql += " NULL ";
                            break;
                    }
                    break;
            }
        }
        if (table.getPk() != null && table.getPk().getColumns().size() != 0) {
            DbPrimaryKey pk = table.getPk();

//            String namePk = pk.getColumns(0).getPk();

            // in MySQL all primary keys named as 'PRIMARY'
            sql += ",\n PRIMARY KEY (\n";

            isFirst = true;
            Collections.sort(pk.getColumns(), DbPkComparator.getInstance());
            for (DbPrimaryKeyColumn c : pk.getColumns()) {
                if (!isFirst) {
                    sql += ",";
                }
                else {
                    isFirst = false;
                }

                sql += c.getC();
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
            String es = "Error create table\nSQL:\n" + sql + "\n";
            throw new DbRevisionException(es, e);
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

    public void addColumn(DbTable table, DbField field) {
        String sql = "alter table " + table.getT() + " add " + field.getName() + " ";

        int fieldType = field.getType();
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
                sql += " VARCHAR(1)";
                break;

            case Types.VARCHAR:
                if (field.getSize() < 256)
                    sql += " VARCHAR(" + field.getSize() + ")";
                else
                    sql += " TEXT";
                break;

            case Types.TIMESTAMP:
            case Types.DATE:
                sql += " TIMESTAMP";
                break;

            case Types.LONGVARCHAR:
                // Oracle 'long' fields type
                sql += " VARCHAR(10)";
                break;

            case Types.LONGVARBINARY:
                sql += " LONGVARBINARY";
                break;

            case Types.BLOB:
                sql += " LONGBLOB";
                break;
            
            default:
                final String es = "unknown field type field - " + field.getName() + " javaType - " + field.getType();
                throw new IllegalStateException(es);
//                    System.out.println(es);
        }

        if (field.getDef() != null) {
            String val = field.getDef().trim();

            if (StringUtils.isNotBlank(val)) {
                switch (fieldType) {
                    case Types.CHAR:
                    case Types.VARCHAR:
                        val = "'" + val + "'";
                        break;
                    case Types.TIMESTAMP:
                    case Types.DATE:
                        if (DatabaseManager.checkDefaultTimestamp(val))
                            val = "CURRENT_TIMESTAMP";

                        break;
                    default:
                }
                sql += (" DEFAULT " + val);
            }
        }

        if (field.getNullable() == DatabaseMetaData.columnNoNulls) {
            sql += " NOT NULL ";
        }

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

    public String getOnDeleteSetNull() {
        return "";
    }

    public String getDefaultTimestampValue() {
        return "CURRENT_TIMESTAMP";
    }

    public List<DbSequence> getSequnceList(String schemaPattern) {
        return new ArrayList<DbSequence>();
    }

    public String getViewText(DbView view) {
        return null;
    }

    public void createView(DbView view) {
        // MySql 5.1 support view
        if (view == null ||
            view.getT() == null || view.getT().length() == 0 ||
            view.getText() == null || view.getText().length() == 0
        ) {
            System.out.println("Skip view.");
            return;
        }

        String s = StringUtils.replace(view.getText(), "||", "+");
        
        // replace oracle to_char() with CONCAT(storedMList,'')
        // need implement....

        // remove oracle 'WITH READ ONLY'
        s = ViewManager.removeOracleWithReadOnly(s);
        String sql_ =
            "CREATE VIEW " + view.getT() +
            " AS " + s;

        Statement ps = null;
        try {
            ps = this.getConnection().createStatement();
            ps.execute(sql_);
        }
        catch (SQLException e) {
            if (testExceptionViewExists(e)) {
                throw new ViewAlreadyExistException("View "+view.getT()+" already exist.");
            }
            if (testExceptionTableNotFound(e)) {
                throw new TableNotFoundException("View "+view.getT()+" refered to unknown table.");
            }
            String errorString = "Error create view. Error code " + e.getErrorCode() + "\n" + sql_;
            throw new DbRevisionException(errorString, e);
        }
        finally {
            DbUtils.close(ps);
            ps = null;
        }
    }

    public void createSequence(DbSequence seq) {
    }

    public void setLongVarbinary(PreparedStatement ps, int index, DbDataFieldData fieldData) {
        try {
            byte[] bytes = Base64.decodeBase64(fieldData.getS().getBytes());

            byte[] fileBytes = new byte[]{};
            if (bytes!=null) {
                fileBytes = bytes;
            }
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileBytes);
            ps.setBinaryStream(index, byteArrayInputStream, fileBytes.length);

            bytes = null;
            byteArrayInputStream = null;
            fileBytes = null;
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        }
    }

    public void setLongVarchar(PreparedStatement ps, int index, DbDataFieldData fieldData) {
        try {
            ps.setString(index, fieldData.getS());
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        }
    }

    public String getClobField(ResultSet rs, String nameField, int maxLength) {
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
            SQLException exception = (SQLException) e;
            if (exception.getErrorCode() == 1146) {
                return true;
            }
        }
        return false;
    }

    public boolean testExceptionIndexUniqueKey(Exception e, String index) {
        return testExceptionIndexUniqueKey(e);
    }

    public boolean testExceptionIndexUniqueKey(Exception e) {
        if (e instanceof SQLException) {
            SQLException exception = (SQLException) e;
            if (exception.getErrorCode() == 1062) {
                return true;
            }
        }
        return false;
    }

    public boolean testExceptionTableExists(Exception e) {
        if (e instanceof SQLException) {
            if (((SQLException) e).getErrorCode() == 1050)
                return true;
        }
        return false;
    }

    public boolean testExceptionViewExists(Exception e) {
        if (e instanceof SQLException) {
            if (((SQLException) e).getErrorCode() == 1050)
                return true;
        }
        return false;
    }

    public boolean testExceptionSequenceExists(Exception e) {
        return false;
    }

    public boolean testExceptionConstraintExists(Exception e) {
        return false;
    }
}
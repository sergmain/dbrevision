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
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

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
    private final static Logger log = Logger.getLogger(MySqlDatabase.class);

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

        String sql = "create table " + table.getName() + "\n" +
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
            int fieldType = field.getJavaType();
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
                    if (field.getDecimalDigit() == null || field.getDecimalDigit() == 0)
                        sql += " DECIMAL";
                    else
                        sql += " DECIMAL(" + (field.getSize()==null || field.getSize()>31?31:field.getSize()) + "," + field.getDecimalDigit() + ")";
                    break;

                case Types.INTEGER:
                    sql += " INTEGER";
                    break;

                case Types.DOUBLE:
                    if (field.getDecimalDigit() == null || field.getDecimalDigit() == 0)
                        sql += " DOUBLE";
                    else
                        sql += " DOUBLE(" + (field.getSize()==null || field.getSize()>31?31:field.getSize()) + "," + field.getDecimalDigit() + ")";
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
                            if (DatabaseManager.checkDefaultTimestamp(val)) {
                                val = "CURRENT_TIMESTAMP";
                            }

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
        if (table.getPrimaryKey() != null && table.getPrimaryKey().getColumns().size() != 0) {
            DbPrimaryKey pk = table.getPrimaryKey();

//            String namePk = pk.getColumns(0).getPkName();

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

                sql += c.getColumnName();
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
            log.error(es);
            throw new DbRevisionException(es, e);
        }
        finally {
            DbUtils.close(ps);
            ps = null;
        }

    }

    public void dropTable(DbTable table) {
        dropTable(table.getName());
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
        String sql = "alter table " + table.getName() + " add " + field.getName() + " ";

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

        if (log.isDebugEnabled())
            log.debug("MySql addColumn sql - \n" + sql);

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
            view.getName() == null || view.getName().length() == 0 ||
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
            "CREATE VIEW " + view.getName() +
            " AS " + s;

        Statement ps = null;
        try {
            ps = this.getConnection().createStatement();
            ps.execute(sql_);
        }
        catch (SQLException e) {
            if (testExceptionViewExists(e)) {
                throw new ViewAlreadyExistException("View "+view.getName()+" already exist.");
            }
            if (testExceptionTableNotFound(e)) {
                throw new TableNotFoundException("View "+view.getName()+" refered to unknown table.");
            }
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

    public void setLongVarbinary(PreparedStatement ps, int index, DbDataFieldData fieldData) {
        try {
            byte[] bytes = Base64.decodeBase64(fieldData.getStringData().getBytes());

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
            ps.setString(index, fieldData.getStringData());
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
            log.error("Error code: " + exception.getErrorCode());
            log.error("getSQLState : " + exception.getSQLState());
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
            log.error("Error code: " + exception.getErrorCode());
            log.error("getSQLState : " + exception.getSQLState());
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
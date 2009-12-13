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

import org.apache.commons.lang.StringUtils;


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
 *
 * $Author: serg_main $
 *
 * $Id: HyperSonicDatabase.java 1141 2006-12-14 14:43:29Z serg_main $
 *
 */
@SuppressWarnings({"UnusedAssignment"})
public class HyperSonicDatabase extends Database {

    /**
     * get family for this adapter
     * @return family
     */
    public Family getFamily() {
        return Family.HYPERSONIC;
    }

    public void setBlobField(String tableName, String fieldName, byte[] bytes, String whereQuery, Object[] objects, int[] fieldTyped) {
    }

    public HyperSonicDatabase(Connection conn) {
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

    public boolean isSchemaSupports() {
        return false;
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
        // hypersonic 1.8.x correctly work only with default schema 'PUBLIC'
        return "PUBLIC";
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

        String sql = "create table \"" + table.getT() + "\"\n" +
            "(";

        try {
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
                case Types.BLOB:
                    // Oracle 'long raw' fields type
                    sql += " LONGVARBINARY";
                    break;

                case Types.CLOB:
                    sql += " LONGVARCHAR";
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
                                val = "'CURRENT_TIMESTAMP'";
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
        if (table.getPk() != null && table.getPk().getColumns().size() > 0) {
            DbPrimaryKey pk = table.getPk();

            //            constraintDefinition:
//            [ CONSTRAINT name ]
//            UNIQUE ( column [,column...] ) |
//            PRIMARY KEY ( column [,column...] ) |

            sql += ",\nCONSTRAINT " + pk.getPk() + " PRIMARY KEY (\n";

            int seq = Integer.MIN_VALUE;
            isFirst = true;
            for (DbPrimaryKeyColumn column : pk.getColumns()) {
                int seqTemp = Integer.MAX_VALUE;
                for (DbPrimaryKeyColumn columnTemp : pk.getColumns()) {
                    if (seq < columnTemp.getSeq() && columnTemp.getSeq() < seqTemp) {
                        seqTemp = columnTemp.getSeq();
                        column = columnTemp;
                    }
                }
                seq = column.getSeq();

                if (!isFirst) {
                    sql += ",";
                }
                else {
                    isFirst = !isFirst;
                }

                sql += column.getC();
            }
            sql += "\n)";
        }
        sql += "\n)";

        Statement ps = null;
            try {
                ps = this.getConnection().createStatement();
                ps.execute(sql);
            }
            finally {
                DbUtils.close(ps);
                ps = null;
            }
        }
        catch (Exception e) {
            throw new DbRevisionException(e);
        }

    }

    public void dropTable(DbTable table) {
        dropTable(table.getT());
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
            DbUtils.close(ps);
            ps = null;
        }
    }

    public void dropSequence(String nameSequence) {
    }

    public void addColumn(DbTable table, DbField field) {
        String sql = "alter table \"" + table.getT() + "\" add column " + field.getName() + " ";

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
                String errorString = "unknown field type field - " + field.getName() + " javaType - " + field.getType();
                throw new DbRevisionException(errorString);
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

    public String getOnDeleteSetNull() {
        return "";
    }

    public String getDefaultTimestampValue() {
        return "current_timestamp";
    }

    public List<DbSequence> getSequnceList(String schemaPattern) {
        return new ArrayList<DbSequence>();
    }

    public String getViewText(DbView view) {
        return null;
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
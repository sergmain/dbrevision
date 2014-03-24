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
import org.riverock.dbrevision.db.ViewManager;
import org.riverock.dbrevision.exception.DbRevisionException;
import org.riverock.dbrevision.exception.ViewAlreadyExistException;
import org.riverock.dbrevision.exception.TableNotFoundException;
import org.riverock.dbrevision.exception.CreateTableException;
import org.riverock.dbrevision.utils.DbUtils;

/**
 * IBM DB2 connection
 * $Author: serg_main $
 *
 * $Id: DB2Database.java 1141 2006-12-14 14:43:29Z serg_main $
 *
 */
@SuppressWarnings({"UnusedAssignment"})
public class DB2Database extends Database {

    /**
     * get family for this adapter
     * @return family
     */
    public Family getFamily() {
        return Family.DB2;
    }

    public void setBlobField(String tableName, String fieldName, byte[] bytes, String whereQuery, Object[] objects, int[] fieldTyped) {
    }

    public DB2Database(Connection conn) {
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
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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

        String sql = "create table \"" + table.getT() + "\" " +
            "(";

        boolean isFirst = true;

        for (DbField field : table.getFields()) {
            if (!isFirst)
                sql += ",";
            else
                isFirst = !isFirst;

            sql += " \"" + field.getName() + "\"";
            int fieldType = field.getType();
            switch (fieldType) {

                case Types.NUMERIC:
                case Types.DECIMAL:
                    if (field.getDigit()==null) {
                        throw new CreateTableException("Precision for column '"+field.getName()+"' is null. "+table.getT()+"."+field.getName());
                    }
                    sql += " DECIMAL(" + (field.getSize()==null || field.getSize() > 31 ? 31 : field.getSize()) + ',' + field.getDigit() + ")";
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
                                val = " CURRENT TIMESTAMP ";
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

            sql += ", CONSTRAINT " + pk.getPk() + " PRIMARY KEY ( ";

            int seq = Integer.MIN_VALUE;
            isFirst = true;
            for (DbPrimaryKeyColumn primaryKeyColumnType : pk.getColumns()) {
                DbPrimaryKeyColumn column = primaryKeyColumnType;
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
            throw new DbRevisionException("Error create table. SQL = \n"+sql, e);
        }
        finally {
            DbUtils.close(ps);
            ps = null;
        }

    }

    public void dropTable(DbTable table) {
        if (table == null)
            return;

        dropTable(table.getT());
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

    public void addColumn(DbTable table, DbField field) {
    }

    public String getOnDeleteSetNull() {
        return null;
    }

    public String getDefaultTimestampValue() {
        return "CURRENT TIMESTAMP";
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

//        String s = Utils.replaceStringArray(view.getText(), new String[][]{{"\n", " "}}).trim();
        String s = view.getText().trim();

        // remove oracle 'WITH READ ONLY'
        s = ViewManager.removeOracleWithReadOnly(s);
        String sql_ =
            "CREATE VIEW " + view.getT() +
            " AS " + s;

        Statement ps = null;
        try {
            ps = this.getConnection().createStatement();
            ps.execute(sql_);
            this.getConnection().commit();
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
            ps.setNull(index, Types.VARCHAR);
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        }
    }

    public void setLongVarchar(PreparedStatement ps, int index, DbDataFieldData fieldData) {
        try {
            ps.setString(index, "");
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        }
    }

    public String getClobField(ResultSet rs, String nameField, int maxLength) {
        return null;
    }

    public boolean testExceptionTableNotFound(Exception e) {
        if (e instanceof SQLException) {
            if (((SQLException) e).getErrorCode() == -204)
                return true;
        }
        return false;
    }

    public boolean testExceptionIndexUniqueKey(Exception e, String index) {
        throw new RuntimeException("Not implemented");
/*
        if (e instanceof SQLException) {
            if (((SQLException) e).getErrorCode() == -100)
                return true;
        }
        return false;
*/
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
        throw new RuntimeException("Not implemented");
/*
        if (e instanceof SQLException) {
            if (((SQLException) e).getErrorCode() == -100)
                return true;
        }
        return false;
*/
    }
}

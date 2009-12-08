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
package org.riverock.dbrevision.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;
import java.util.List;

import org.riverock.dbrevision.schema.db.DbDataFieldData;
import org.riverock.dbrevision.schema.db.DbField;
import org.riverock.dbrevision.schema.db.DbForeignKey;
import org.riverock.dbrevision.schema.db.DbSequence;
import org.riverock.dbrevision.schema.db.DbTable;
import org.riverock.dbrevision.schema.db.DbView;

/**
 * Database
 *
 * $Revision: 1141 $ $Date: 2006-12-14 17:43:29 +0300 (Чт, 14 дек 2006) $
 */
public abstract class Database {
    private Connection conn = null;

    /**
     * Database family
     */
    public enum Family {
        ORACLE, MYSQL, DB2, SQLSERVER, HYPERSONIC, MAXDB, INTERBASE, POSTGREES
    }

    public enum ForeingKeyState {
        ENABLE, DISABLE, ENABLE_VALIDATE
    }

    public enum NullableState {
        NULL, NOTNULL
    }

    /**
     * Constructor
     *
     * @param conn connection
     */
    public Database(Connection conn) {
        this.conn = conn;
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    /**
     * Get jdbc connection
     *
     * @return connection
     */
    public Connection getConnection() {
        return conn;
    }

    /**
     * get family for this adapter
     * @return family
     */
    public abstract Family getFamily();

    abstract public void setBlobField(
        String tableName, String fieldName, byte[] bytes,
        String whereQuery,
        Object[] objects, int[] fieldTyped
    );

    /**
     * Is this db support batch update?
     *
     * @return true if support
     */
    public abstract boolean isBatchUpdate();

    public abstract boolean isNeedUpdateBracket();

    public abstract boolean isByteArrayInUtf8();

    public abstract boolean isSchemaSupports();

    public abstract boolean isForeignKeyControlSupports();

    public abstract void changeForeignKeyState(DbForeignKey key, ForeingKeyState state);

    public abstract void changeNullableState(DbTable table, DbField field, NullableState state); 

    public abstract String getDefaultSchemaName(DatabaseMetaData databaseMetaData);

    public abstract String getClobField(ResultSet rs, String nameFeld);

    public abstract byte[] getBlobField(ResultSet rs, String nameField, int maxLength);

    public abstract void createTable(DbTable table);

    public abstract void createView(DbView view);

    public abstract void createSequence(DbSequence seq);

    public abstract void dropTable(DbTable table);

    public abstract void dropTable(String nameTable);

    public abstract void dropSequence(String nameSequence);

    public abstract void addColumn(DbTable table, DbField field);

    public abstract String getOnDeleteSetNull();

    public abstract String getDefaultTimestampValue();

    public abstract List<DbSequence> getSequnceList(String schemaPattern);

    public abstract String getViewText(DbView view);

    public abstract void setLongVarbinary(PreparedStatement ps, int index, DbDataFieldData fieldData);

    public abstract void setLongVarchar(PreparedStatement ps, int index, DbDataFieldData fieldData);

    /**
     * @param rs result set
     * @param nameFeld name of field
     * @param maxLength max length of CLOB field
     * @return value of specific table columns
     */
    public abstract String getClobField(ResultSet rs, String nameFeld, int maxLength);

    public abstract boolean testExceptionTableNotFound(Exception e);

    public abstract boolean testExceptionIndexUniqueKey(Exception e, String index);

    public abstract boolean testExceptionIndexUniqueKey(Exception e);

    public abstract boolean testExceptionTableExists(Exception e);

    public abstract boolean testExceptionViewExists(Exception e);

    public abstract boolean testExceptionSequenceExists(Exception e);

    public abstract boolean testExceptionConstraintExists(Exception e);

    /**
     * @return - int. Max size of char field
     */
    public abstract int getMaxLengthStringField();
}

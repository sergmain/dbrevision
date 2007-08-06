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
package org.riverock.dbrevision.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.riverock.dbrevision.annotation.schema.db.DbDataFieldData;
import org.riverock.dbrevision.annotation.schema.db.DbField;
import org.riverock.dbrevision.annotation.schema.db.DbForeignKey;
import org.riverock.dbrevision.annotation.schema.db.DbSequence;
import org.riverock.dbrevision.annotation.schema.db.DbTable;
import org.riverock.dbrevision.annotation.schema.db.DbView;

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

    /**
     * Constructor
     *
     * @param conn connection
     */
    public Database(Connection conn) {
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

    /**
     * Is this db support batch update?
     *
     * @return true if support
     */
    public abstract boolean isBatchUpdate();

    public abstract boolean isNeedUpdateBracket();

    public abstract boolean isByteArrayInUtf8();

    public abstract String getClobField(ResultSet rs, String nameFeld);

    public abstract byte[] getBlobField(ResultSet rs, String nameField, int maxLength);

    public abstract void createTable(DbTable table);

    public abstract void createView(DbView view);

    public abstract void createSequence(DbSequence seq);

    public abstract void dropTable(DbTable table);

    public abstract void dropTable(String nameTable);

    public abstract void dropSequence(String nameSequence);

    public abstract void dropConstraint(DbForeignKey impPk);

    public abstract void addColumn(DbTable table, DbField field);

    public abstract void createForeignKey(DbTable view);

    public abstract String getOnDeleteSetNull();

    public abstract String getDefaultTimestampValue();

    public abstract List<DbView> getViewList(String schemaPattern, String tablePattern);

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

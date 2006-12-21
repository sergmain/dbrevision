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
import java.sql.SQLException;
import java.util.List;

import org.riverock.dbrevision.annotation.schema.db.CustomSequence;
import org.riverock.dbrevision.annotation.schema.db.DbDataFieldData;
import org.riverock.dbrevision.annotation.schema.db.DbField;
import org.riverock.dbrevision.annotation.schema.db.DbImportedPKColumn;
import org.riverock.dbrevision.annotation.schema.db.DbSequence;
import org.riverock.dbrevision.annotation.schema.db.DbTable;
import org.riverock.dbrevision.annotation.schema.db.DbView;

/**
 * $Revision: 1141 $ $Date: 2006-12-14 17:43:29 +0300 (Чт, 14 дек 2006) $
 */
public abstract class DatabaseAdapter {
    private Connection conn = null;

    public DatabaseAdapter(Connection conn) {
        this.setConnection(conn);
    }

    public Connection getConnection() {
        return conn;
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    public abstract int getFamily();

    public abstract int getVersion();

    public abstract int getSubVersion();

    public abstract boolean getIsBatchUpdate();

    public abstract boolean getIsNeedUpdateBracket();

    public abstract boolean getIsByteArrayInUtf8();

    public abstract String getClobField(ResultSet rs, String nameFeld) throws SQLException;

    public abstract byte[] getBlobField(ResultSet rs, String nameField, int maxLength) throws Exception;

    public abstract void createTable(DbTable table) throws Exception;

    public abstract void createView(DbView view) throws Exception;

    public abstract void createSequence(DbSequence seq);

    public abstract void dropTable(DbTable table) throws Exception;

    public abstract void dropTable(String nameTable) throws Exception;

    public abstract void dropSequence(String nameSequence) throws Exception;

    public abstract void dropConstraint(DbImportedPKColumn impPk);

    public abstract void addColumn(DbTable table, DbField field);

    public abstract String getOnDeleteSetNull();

    public abstract String getDefaultTimestampValue();

    public abstract List<DbView> getViewList(String schemaPattern, String tablePattern) throws Exception;

    public abstract List<DbSequence> getSequnceList(String schemaPattern) throws Exception;

    public abstract String getViewText(DbView view) throws Exception;

    public abstract void setLongVarbinary(PreparedStatement ps, int index, DbDataFieldData fieldData)
        throws SQLException;

    public abstract void setLongVarchar(PreparedStatement ps, int index, DbDataFieldData fieldData)
        throws SQLException;

    /**
     * @param rs
     * @param nameFeld
     * @param maxLength
     * @return
     * @throws SQLException
     */
    public abstract String getClobField(ResultSet rs, String nameFeld, int maxLength)
        throws SQLException;

    /**
     * ��������� �������� �� ������ ����������� ��������� � �� ������������ � ���� �������.
     *
     * @param e - Exception
     * @return boolean. true - ���� ������ �������� � ���������� ��������� � �� ������������
     *         �������. ����� false.
     */
    public abstract boolean testExceptionTableNotFound(Exception e);

    /**
     * ��������� �������� �� ������ ����������� ������� ������� ���������� ������ � ���������� ����
     *
     * @param e     - Exception
     * @param index - String. ��� ����������� ����� ��� �������� ��� ������ �������� ������ � ���.
     * @return boolean. true - ���� ������ ���� ����������� ������� ������� ���������� ������
     *         � ���������� ����. ����� false.
     */
    public abstract boolean testExceptionIndexUniqueKey(Exception e, String index);

    public abstract boolean testExceptionIndexUniqueKey(Exception e);

    public abstract boolean testExceptionTableExists(Exception e);

    public abstract boolean testExceptionViewExists(Exception e);

    public abstract boolean testExceptionSequenceExists(Exception e);

    public abstract boolean testExceptionConstraintExists(Exception e);

    /**
     * ���������� �������� ��������(������������������) ��� ������� ����� ������������������.
     * ��� ������ ��������� � ������ ����� ������ ����� ���� ������ �� �������.
     *
     * @param sequence - String. ��� ����������������� ��� ��������� ���������� ��������.
     * @return long - ��������� �������� ��� ����� �� ������������������
     * @throws SQLException
     */
    public abstract long getSequenceNextValue(final CustomSequence sequence) throws SQLException;

    /**
     * @return - int. Max size of char field
     */
    public abstract int getMaxLengthStringField();
}

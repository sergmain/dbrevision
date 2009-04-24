package org.riverock.dbrevision.db.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

import org.riverock.dbrevision.annotation.schema.db.DbDataFieldData;
import org.riverock.dbrevision.annotation.schema.db.DbField;
import org.riverock.dbrevision.annotation.schema.db.DbForeignKey;
import org.riverock.dbrevision.annotation.schema.db.DbSequence;
import org.riverock.dbrevision.annotation.schema.db.DbTable;
import org.riverock.dbrevision.annotation.schema.db.DbView;
import org.riverock.dbrevision.db.Database;
import org.riverock.dbrevision.exception.DbRevisionException;
import org.riverock.dbrevision.utils.DbUtils;

/**
 * User: SergeMaslyukov
 * Date: 28.07.2007
 * Time: 21:05:32
 */
public class LocalDatabase extends Database {
    public LocalDatabase(Connection conn) {
        super(conn);
    }

    public Family getFamily() {
        return null;
    }

    public void setBlobField(String tableName, String fieldName, byte[] bytes, String whereQuery, Object[] objects, int[] fieldTyped) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isBatchUpdate() {
        return false;
    }

    public boolean isNeedUpdateBracket() {
        return false;
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
        return null;  
    }

    public String getClobField(ResultSet rs, String nameFeld) {
        return null;
    }

    public byte[] getBlobField(ResultSet rs, String nameField, int maxLength) {
        return new byte[0];
    }

    public void createTable(DbTable table) {

    }

    public void createView(DbView view) {

    }

    public void createSequence(DbSequence seq) {

    }

    public void dropTable(DbTable table) {

    }

    public void dropTable(String nameTable) {

    }

    public void dropSequence(String nameSequence) {

    }

    public void addColumn(DbTable table, DbField field) {

    }

    public String getOnDeleteSetNull() {
        return null;
    }

    public String getDefaultTimestampValue() {
        return null;
    }

    public List<DbSequence> getSequnceList(String schemaPattern) {
        return null;
    }

    public String getViewText(DbView view) {
        return null;
    }

    public void setLongVarbinary(PreparedStatement ps, int index, DbDataFieldData fieldData) {

    }

    public void setLongVarchar(PreparedStatement ps, int index, DbDataFieldData fieldData) {

    }

    public String getClobField(ResultSet rs, String nameFeld, int maxLength) {
        return null;
    }

    public boolean testExceptionTableNotFound(Exception e) {
        return false;
    }

    public boolean testExceptionIndexUniqueKey(Exception e, String index) {
        return false;
    }

    public boolean testExceptionIndexUniqueKey(Exception e) {
        return false;
    }

    public boolean testExceptionTableExists(Exception e) {
        return false;
    }

    public boolean testExceptionViewExists(Exception e) {
        return false;
    }

    public boolean testExceptionSequenceExists(Exception e) {
        return false;
    }

    public boolean testExceptionConstraintExists(Exception e) {
        return false;
    }

    public int getMaxLengthStringField() {
        return 0;  
    }
}

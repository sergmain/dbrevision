package org.riverock.dbrevision.db.impl;

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
import org.riverock.dbrevision.db.Database;

/**
 * Created by IntelliJ IDEA.
 * User: SergeMaslyukov
 * Date: 28.07.2007
 * Time: 21:05:32
 * To change this template use File | Settings | File Templates.
 */
public class LocalDatabase extends Database {
    public LocalDatabase(Connection conn) {
        super(conn);
    }

    public Family getFamily() {
        return null;
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

    public void dropConstraint(DbForeignKey impPk) {

    }

    public void addColumn(DbTable table, DbField field) {

    }

    public void createForeignKey(DbTable view) {

    }

    public String getOnDeleteSetNull() {
        return null;
    }

    public String getDefaultTimestampValue() {
        return null;
    }

    public List<DbView> getViewList(String schemaPattern, String tablePattern) {
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

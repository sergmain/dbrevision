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
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import org.riverock.dbrevision.annotation.schema.db.DbField;
import org.riverock.dbrevision.annotation.schema.db.DbPrimaryKey;
import org.riverock.dbrevision.annotation.schema.db.DbSchema;
import org.riverock.dbrevision.annotation.schema.db.DbTable;
import org.riverock.dbrevision.annotation.schema.db.DbView;
import org.riverock.dbrevision.exception.DbRevisionException;
import org.riverock.dbrevision.exception.ViewAlreadyExistException;
import org.riverock.dbrevision.exception.TableNotFoundException;
import org.riverock.dbrevision.utils.DbUtils;

/**
 * User: Admin
 * Date: Aug 30, 2003
 * Time: 5:07:17 PM
 * <p/>
 * $Id: DatabaseManager.java 1141 2006-12-14 14:43:29Z serg_main $
 */
@SuppressWarnings({"UnusedAssignment"})
public final class DatabaseManager {
    private static Logger log = Logger.getLogger(DatabaseManager.class);

    private static final String DEFAULT_DATE_VALUES[] = {"sysdate", "current_timestamp", "current_time", "current_date"};

    public static void addPrimaryKey(final Database db_, final DbTable table, final DbPrimaryKey pk) {
        if (table == null) {
            String s = "Add primary key failed - table object is null";
            System.out.println(s);
            if (log.isInfoEnabled()) {
                log.info(s);
            }

            return;
        }

        DbPrimaryKey checkPk = DatabaseStructureManager.getPrimaryKey(db_, table.getSchema(), table.getName());

        if (checkPk != null && checkPk.getColumns().size() != 0) {
            String s = "primary key already exists";
            System.out.println(s);
            if (log.isInfoEnabled()) {
                log.info(s);
            }

            return;
        }

        String tempTable = table.getName() + '_' + table.getName();
        duplicateTable(db_, table, tempTable);
        db_.dropTable(table);
        table.setPrimaryKey(pk);
        db_.createTable(table);
        copyData(db_, table, tempTable, table.getName());
        db_.dropTable(tempTable);
    }

    public static void copyData(
        final Database db_, final DbTable fieldsList, final String sourceTable, final String targetTableName
    ) {
        if (fieldsList == null || sourceTable == null || targetTableName == null) {
            if (log.isInfoEnabled()) {
                log.info("copy data failed, some objects is null");
            }

            return;
        }

        String fields = "";
        boolean isNotFirst = false;
        for (DbField DbField : fieldsList.getFields()) {
            if (isNotFirst) {
                fields += ", ";
            }
            else {
                isNotFirst = true;
            }
            fields += DbField.getName();
        }

        String sql_ =
            "insert into " + targetTableName +
                "(" + fields + ")" +
                (db_.isNeedUpdateBracket() ? "(" : "") +
                "select " + fields + " from " + sourceTable +
                (db_.isNeedUpdateBracket() ? ")" : "");

        Statement ps = null;
        try {
            ps = db_.getConnection().createStatement();
            ps.execute(sql_);
        }
        catch (SQLException e) {
            String errorString = "Error copy data from table '" + sourceTable +
                "' to '" + targetTableName + "' " + e.getErrorCode() + "\nsql - " + sql_;

            log.error(errorString, e);
            System.out.println(errorString);
            throw new DbRevisionException(e);
        }
        finally {
            DbUtils.close(ps);
            ps = null;
        }
    }

    public static void duplicateTable(final Database db_, final DbTable srcTable, final String targetTableName) {
        if (srcTable == null) {
            log.error("duplicate table failed, source table object is null");
            return;
        }

        DbTable tempTable = DatabaseStructureManager.cloneDescriptionTable(srcTable);
        tempTable.setName(targetTableName);
        tempTable.setPrimaryKey(null);
        tempTable.setData(null);

        db_.createTable(tempTable);
        copyData(db_, tempTable, srcTable.getName(), targetTableName);
    }

    public static DbField getFieldFromStructure(final DbSchema schema, final String tableName, final String fieldName) {
        if (schema == null || tableName == null || fieldName == null) {
            return null;
        }

        for (DbTable DbTable : schema.getTables()) {
            if (tableName.equalsIgnoreCase(DbTable.getName())) {
                for (DbField DbField : DbTable.getFields()) {
                    if (fieldName.equalsIgnoreCase(DbField.getName())) {
                        return DbField;
                    }
                }
            }

        }
        return null;
    }

    // cheak what 'tableName' is a table or a view
    public static DbTable getTableFromStructure(final DbSchema schema, final String tableName) {
        if (schema == null || tableName == null) {
            return null;
        }

        for (DbTable checkTable : schema.getTables()) {
            if (tableName.equalsIgnoreCase(checkTable.getName())) {
                return checkTable;
            }
        }
        return null;
    }

    public static DbView getViewFromStructure(final DbSchema schema, final String viewName) {
        if (schema == null || viewName == null) {
            return null;
        }

        for (DbView checkView : schema.getViews()) {
            if (viewName.equalsIgnoreCase(checkView.getName())) {
                return checkView;
            }
        }
        return null;
    }

    public static boolean isFieldExists(final DbSchema schema, final DbTable table, final DbField field) {
        if (schema == null || table == null || field == null) {
            return false;
        }

        for (DbTable DbTable : schema.getTables()) {
            if (table.getName().equalsIgnoreCase(DbTable.getName())) {
                for (DbField DbField : DbTable.getFields()) {
                    if (field.getName().equalsIgnoreCase(DbField.getName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isTableExists(final DbSchema schema, final DbTable table) {
        if (schema == null || table == null) {
            return false;
        }

        for (DbTable DbTable : schema.getTables()) {
            if (table.getName().equalsIgnoreCase(DbTable.getName())) {
                return true;
            }
        }
        return false;
    }

    public static DbSchema getDbStructure(Database adapter) {
        return getDbStructure(adapter, true);
    }

    public static DbSchema getDbStructure(Database adapter, boolean isOnlyCurrent) {
        DbSchema schema = new DbSchema();

        String dbSchema;
        if (isOnlyCurrent) {
            try {
                DatabaseMetaData metaData = adapter.getConnection().getMetaData();
                dbSchema = metaData.getUserName();
            }
            catch (SQLException e) {
                throw new DbRevisionException("Error get metadata", e);
            }
        }
        else {
            dbSchema = "%";
        }

        List<DbTable> list = DatabaseStructureManager.getTableList(adapter, dbSchema, "%");
        for (DbTable table : list) {
            schema.getTables().add(table);
        }
        schema.getViews().addAll(adapter.getViewList(dbSchema, "%"));
        schema.getSequences().addAll(adapter.getSequnceList(dbSchema));

        for (DbTable table : schema.getTables()) {
            table.getFields().addAll(DatabaseStructureManager.getFieldsList(adapter, table.getSchema(), table.getName()));
            table.setPrimaryKey(DatabaseStructureManager.getPrimaryKey(adapter, table.getSchema(), table.getName()));
            table.getForeignKeys().addAll(DatabaseStructureManager.getForeignKeys(adapter, table.getSchema(), table.getName()));
            table.getIndexes().addAll(DatabaseStructureManager.getIndexes(adapter, table.getSchema(), table.getName()));
        }

        for (DbView view : schema.getViews()) {
            view.setText(adapter.getViewText(view));
        }

        return schema;
    }

    public static void createWithReplaceAllView(final Database adapter, final DbSchema millSchema) {
        createWithReplaceAllView(adapter, millSchema.getViews());
    }
    
    public static void createWithReplaceAllView(final Database adapter, final List<DbView> views) {
        boolean[] idx = new boolean[views.size()];
        for (int i = 0; i < idx.length; i++) {
            idx[i] = false;
        }

        for (boolean anIdx : idx) {
            if (anIdx) {
                continue;
            }

            for (int i = 0; i < idx.length; i++) {
                if (idx[i]) {
                    continue;
                }

                DbView view = views.get(i);
                try {
                    adapter.createView(view);
                    idx[i] = true;
                }
                catch (ViewAlreadyExistException e) {
                    try {
                        DatabaseStructureManager.dropView(adapter, view);
                    }
                    catch (Exception e1) {
                        String es = "Error drop view";
                        log.error(es, e1);
                        throw new DbRevisionException(es, e1);
                    }

                    try {
                        adapter.createView(view);
                        idx[i] = true;
                    }
                    catch (Exception e1) {
                        // do not throw any exception
                    }
                }
                catch(TableNotFoundException e) {
                    // do not throw any exception
                }
            }
        }
    }

    public static List<DbView> getViewList(final Connection conn, final String schemaPattern, final String tablePattern) {
        String[] types = {"VIEW"};

        ResultSet meta = null;
        List<DbView> v = new ArrayList<DbView>();
        try {
            DatabaseMetaData dbMeta = conn.getMetaData();

            meta = dbMeta.getTables(
                null,
                schemaPattern,
                tablePattern,
                types
            );

            while (meta.next()) {

                DbView table = new DbView();

                table.setSchema(meta.getString("TABLE_SCHEM"));
                table.setName(meta.getString("TABLE_NAME"));
                table.setType(meta.getString("TABLE_TYPE"));
                table.setRemark(meta.getString("REMARKS"));

                if (log.isDebugEnabled()) {
                    log.debug("View - " + table.getName() + "  remak - " + table.getRemark());
                }

                v.add(table);
            }
        }
        catch (Exception e) {
            String es = "Error get list of view";
            log.error(es, e);
        }
        return v;
    }

    public static boolean isSkipTable(final String table) {
        if (table == null) {
            return false;
        }

        String s = table.trim();

        String fullCheck[] = {"SQLN_EXPLAIN_PLAN", "DBG", "CHAINED_ROWS"};
        for (String aFullCheck : fullCheck) {
            if (aFullCheck.equalsIgnoreCase(s)) {
                return true;
            }
        }

        String startCheck[] = {"F_D_", "FOR_DEL_", "F_DEL_", "FOR_D_"};
        for (String aStartCheck : startCheck) {
            if (s.toLowerCase().startsWith(aStartCheck.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check what field's default value is default timestamp(date) for bd column
     * For example for Oracle value is 'SYSDATE'
     *
     * @param val value for DEFAULT_DATE_VALUES
     * @return true, if value is date, otherwise false
     */
    public static boolean checkDefaultTimestamp(final String val) {
        if (val == null) {
            return false;
        }

        String s = val.trim().toLowerCase();
        for (String aCheck : DEFAULT_DATE_VALUES) {
            if (aCheck.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    public static int sqlTypesMapping(final String type) {
        if (type == null) {
            return Types.OTHER;
        }

        if ("BIT".equals(type)) {
            return Types.BIT;
        }
        else if ("TINYINT".equals(type)) {
            return Types.TINYINT;
        }
        else if ("SMALLINT".equals(type)) {
            return Types.SMALLINT;
        }
        else if ("INTEGER".equals(type)) {
            return Types.INTEGER;
        }
        else if ("BIGINT".equals(type)) {
            return Types.BIGINT;
        }
        else if ("FLOAT".equals(type)) {
            return Types.FLOAT;
        }
        else if ("REAL".equals(type)) {
            return Types.REAL;
        }
        else if ("DOUBLE".equals(type)) {
            return Types.DOUBLE;
        }
        else if ("NUMERIC".equals(type)) {
            return Types.NUMERIC;
        }
        else if ("DECIMAL".equals(type)) {
            return Types.DECIMAL;
        }
        else if ("NUMBER".equals(type)) {
            return Types.DECIMAL;
        }
        else if ("CHAR".equals(type)) {
            return Types.CHAR;
        }
        else if ("VARCHAR".equals(type)) {
            return Types.VARCHAR;
        }
        else if ("LONGVARCHAR".equals(type)) {
            return Types.LONGVARCHAR;
        }
        else if ("DATE".equals(type)) {
            return Types.DATE;
        }
        else if ("TIME".equals(type)) {
            return Types.TIME;
        }
        else if ("TIMESTAMP".equals(type)) {
            return Types.TIMESTAMP;
        }
        else if ("BINARY".equals(type)) {
            return Types.BINARY;
        }
        else if ("VARBINARY".equals(type)) {
            return Types.VARBINARY;
        }
        else if ("LONGVARBINARY".equals(type)) {
            return Types.LONGVARBINARY;
        }
        else if ("NULL".equals(type)) {
            return Types.NULL;
        }
        else if ("OTHER".equals(type)) {
            return Types.OTHER;
        }
        else if ("JAVA_OBJECT".equals(type)) {
            return Types.JAVA_OBJECT;
        }
        else if ("DISTINCT".equals(type)) {
            return Types.DISTINCT;
        }
        else if ("STRUCT".equals(type)) {
            return Types.STRUCT;
        }
        else if ("ARRAY".equals(type)) {
            return Types.ARRAY;
        }
        else if ("BLOB".equals(type)) {
            return Types.BLOB;
        }
        else if ("CLOB".equals(type)) {
            return Types.CLOB;
        }
        else if ("REF".equals(type)) {
            return Types.REF;
        }
        else {
            return Types.OTHER;
        }

    }

    public static List<Long> getLongValueList(final Database db, final String sql, final Object[] params, final int[] types) {

        Statement stmt = null;
        PreparedStatement pstm;
        ResultSet rs = null;
        List<Long> list = new ArrayList<Long>();
        try {
            if (params == null) {
                stmt = db.getConnection().createStatement();
                rs = stmt.executeQuery(sql);
            }
            else {
                pstm = db.getConnection().prepareStatement(sql);
                for (int i = 0; i < params.length; i++) {
                    if (types == null) {
                        pstm.setObject(i + 1, params[i]);
                    }
                    else {
                        pstm.setObject(i + 1, params[i], types[i]);
                    }
                }
                rs = pstm.executeQuery();
                stmt = pstm;
            }

            while (rs.next()) {
                long tempLong = rs.getLong(1);
                if (rs.wasNull()) {
                    continue;
                }

                list.add(tempLong);
            }
            return list;
        }
        catch (SQLException e) {
            log.error("error getting long value fron sql '" + sql + "'", e);
            throw new DbRevisionException(e);
        }
        finally {
            DbUtils.close(rs, stmt);
            rs = null;
            stmt = null;
            pstm = null;
        }
    }

    public static List<Long> getIdByList(final Database adapter, final String sql, final Object[] param) {
        Statement stmt = null;
        PreparedStatement pstm;
        ResultSet rs = null;
        List<Long> list = new ArrayList<Long>();
        try {
            if (param == null) {
                stmt = adapter.getConnection().createStatement();
                rs = stmt.executeQuery(sql);
            }
            else {
                pstm = adapter.getConnection().prepareStatement(sql);
                for (int i = 0; i < param.length; i++) {
                    pstm.setObject(i + 1, param[i]);
                }

                rs = pstm.executeQuery();
                stmt = pstm;
            }

            while (rs.next()) {
                long tempLong = rs.getLong(1);
                if (rs.wasNull()) {
                    continue;
                }

                list.add(tempLong);
            }
            return list;
        }
        catch (SQLException e) {
            final String es = "error getting long value fron sql '" + sql + "'";
            log.error(es, e);
            throw new RuntimeException(es, e);
        }
        finally {
            DbUtils.close(rs, stmt);
            rs = null;
            stmt = null;
            pstm = null;
        }
    }
}

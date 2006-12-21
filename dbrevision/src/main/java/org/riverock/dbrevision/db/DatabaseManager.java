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
import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;

import org.riverock.dbrevision.annotation.schema.db.*;
import org.riverock.dbrevision.utils.Utils;
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

    public final static int ORACLE_FAMALY = 1;
    public final static int MYSQL_FAMALY = 2;
    public final static int DB2_FAMALY = 3;
    public final static int MSSQL_FAMALY = 4;
    public final static int HSQLDB_FAMALY = 5;
    public final static int SAPDB_FAMALY = 6;
    public final static int INTERBASE_FAMALY = 7;

    public static final String NUMBER_TYPE="number";
    public static final String STRING_TYPE="string";
    public static final String DATE_TYPE="date";

    private static final int UNKNOWN_TYPE_VALUE = 0;
    private static final int NUMBER_TYPE_VALUE = 1;
    private static final int STRING_TYPE_VALUE = 2;
    private static final int DATE_TYPE_VALUE = 3;

    private static Map<String, Integer> dataType = new HashMap<String, Integer>();
    public static final DbPkComparator pkComparator = new DbPkComparator();

    static {
        dataType.put(NUMBER_TYPE, NUMBER_TYPE_VALUE);
        dataType.put(STRING_TYPE, STRING_TYPE_VALUE);
        dataType.put(DATE_TYPE, DATE_TYPE_VALUE);
    }

    public static void close(final ResultSet rs, final Statement st) {
        if (rs != null) {
            try {
                rs.close();
            }
            catch (Exception e01) {
                // catch SQLException
            }
        }
        if (st != null) {
            try {
                st.close();
            }
            catch (Exception e02) {
                // catch SQLException
            }
        }
    }

    public static void close(final Statement st) {
        if (st != null) {
            try {
                st.close();
            }
            catch (SQLException e201) {
                // catch SQLException
            }
        }
    }

    public static void addPrimaryKey(final DatabaseAdapter db_, final DbTable table, final DbPrimaryKey pk)
        throws Exception {
        if (table == null) {
            String s = "Add primary key failed - table object is null";
            System.out.println(s);
            if (log.isInfoEnabled())
                log.info(s);

            return;
        }

        DbPrimaryKey checkPk = DatabaseStructureManager.getPrimaryKey(db_.getConnection(), table.getSchema(), table.getName());

        if (checkPk != null && checkPk.getColumns().size() != 0) {
            String s = "primary key already exists";
            System.out.println(s);
            if (log.isInfoEnabled())
                log.info(s);

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
        final DatabaseAdapter db_, final DbTable fieldsList, final String sourceTable, final String targetTableName
    )
        throws Exception {
        if (fieldsList == null || sourceTable == null || targetTableName == null) {
            if (log.isInfoEnabled())
                log.info("copy data failed, some objects is null");

            return;
        }

        String fields = "";
        boolean isNotFirst = false;
        for (DbField DbField : fieldsList.getFields()) {
            if (isNotFirst) {
                fields += ", ";
            } else {
                isNotFirst = true;
            }
            fields += DbField.getName();
        }

        String sql_ =
            "insert into " + targetTableName +
                "(" + fields + ")" +
                (db_.getIsNeedUpdateBracket() ? "(" : "") +
                "select " + fields + " from " + sourceTable +
                (db_.getIsNeedUpdateBracket() ? ")" : "");

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
            throw e;
        }
        finally {
            close(ps);
            ps = null;
        }
    }

    public static void copyFieldData(
        final DatabaseAdapter db_, final DbTable table, final DbField sourceField, final DbField targetField
    )
        throws Exception {
        if (table == null || sourceField == null || targetField == null) {
            if (log.isInfoEnabled())
                log.info("copy field data failed, some objects is null");

            return;
        }

        String sql_ =
            "update " + table.getName() + ' ' +
                "SET " + targetField.getName() + '=' + sourceField.getName();

        Statement ps = null;
        try {
            ps = db_.getConnection().createStatement();
            ps.execute(sql_);
        }
        catch (SQLException e) {
            String errorString = "Error copy data from field '" + table.getName() + '.' + sourceField.getName() +
                "' to '" + table.getName() + '.' + targetField.getName() + "' " + e.getErrorCode() + "\nsql - " + sql_;

            log.error(errorString, e);
            System.out.println(errorString);
            throw e;
        }
        finally {
            close(ps);
            ps = null;
        }
    }

    public static void duplicateTable(final DatabaseAdapter db_, final DbTable srcTable, final String targetTableName)
        throws Exception {
        if (srcTable == null) {
            log.error("duplicate table failed, source table object is null");
            return;
        }

        DbTable tempTable = cloneDescriptionTable(srcTable);
        tempTable.setName(targetTableName);
        tempTable.setPrimaryKey(null);
        tempTable.setData(null);

        db_.createTable(tempTable);
        copyData(db_, tempTable, srcTable.getName(), targetTableName);
    }

    public static DbPrimaryKeyColumn cloneDescriptionPrimaryKeyColumn(final DbPrimaryKeyColumn srcCol) {
        DbPrimaryKeyColumn c = new DbPrimaryKeyColumn();
        c.setCatalogName(srcCol.getCatalogName());
        c.setColumnName(srcCol.getColumnName());
        c.setKeySeq(srcCol.getKeySeq());
        c.setPkName(srcCol.getPkName());
        c.setSchemaName(srcCol.getSchemaName());
        c.setTableName(srcCol.getTableName());

        return c;
    }

    public static DbImportedPKColumn cloneDescriptionFK(final DbImportedPKColumn srcFk) {
        if (srcFk == null)
            return null;

        DbImportedPKColumn f = new DbImportedPKColumn();
        f.setDeferrability(srcFk.getDeferrability());
        f.setDeleteRule(srcFk.getDeleteRule());
        f.setFkColumnName(srcFk.getFkColumnName());
        f.setFkName(srcFk.getFkName());
        f.setFkTableName(srcFk.getFkTableName());
        f.setFkSchemaName(srcFk.getFkSchemaName());
        f.setKeySeq(srcFk.getKeySeq());
        f.setPkColumnName(srcFk.getPkColumnName());
        f.setPkName(srcFk.getPkName());
        f.setPkTableName(srcFk.getPkTableName());
        f.setPkSchemaName(srcFk.getPkSchemaName());
        f.setUpdateRule(srcFk.getUpdateRule());

        return f;
    }

    public static DbPrimaryKey cloneDescriptionPK(final DbPrimaryKey srcPk) {
        if (srcPk == null)
            return null;

        DbPrimaryKey pk = new DbPrimaryKey();
        for (DbPrimaryKeyColumn column : srcPk.getColumns()) {
            pk.getColumns().add(cloneDescriptionPrimaryKeyColumn(column));
        }

        return pk;
    }

    public static DbField cloneDescriptionField(final DbField srcField) {
        if (srcField == null)
            return null;

        DbField f = new DbField();
        f.setApplType(srcField.getApplType());
        f.setComment(srcField.getComment());
        f.setDataType(srcField.getDataType());
        f.setDecimalDigit(srcField.getDecimalDigit());
        f.setDefaultValue(srcField.getDefaultValue());
        f.setJavaStringType(srcField.getJavaStringType());
        f.setJavaType(srcField.getJavaType());
        f.setName(srcField.getName());
        f.setNullable(srcField.getNullable());
        f.setSize(srcField.getSize());

        return f;
    }

    /**
     * Clone description of table. Data not cloned
     *
     * @param srcTable source table
     * @return DbTable cloned table
     */
    public static DbTable cloneDescriptionTable(final DbTable srcTable) {
        if (srcTable == null)
            return null;

        DbTable r = new DbTable();

        r.setSchema(srcTable.getSchema());
        r.setName(srcTable.getName());
        r.setType(srcTable.getType());

        DbPrimaryKey pk = cloneDescriptionPK(srcTable.getPrimaryKey());
        r.setPrimaryKey(pk);

        for (DbField DbField : srcTable.getFields()) {
            DbField f = cloneDescriptionField(DbField);
            r.getFields().add(f);
        }

        for (DbImportedPKColumn DbImportedPKColumn : srcTable.getImportedKeys()) {
            DbImportedPKColumn f = cloneDescriptionFK(DbImportedPKColumn);
            r.getImportedKeys().add(f);
        }

        return r;
    }

    public static DbField getFieldFromStructure(final DbSchema schema, final String tableName, final String fieldName) {
        if (schema == null || tableName == null || fieldName == null)
            return null;

        for (DbTable DbTable : schema.getTables()) {
            if (tableName.equalsIgnoreCase(DbTable.getName())) {
                for (DbField DbField : DbTable.getFields()) {
                    if (fieldName.equalsIgnoreCase(DbField.getName()))
                        return DbField;
                }
            }

        }
        return null;
    }

    // check is 'tableName' is table or view
    public static DbTable getTableFromStructure(final DbSchema schema, final String tableName) {
        if (schema == null || tableName == null)
            return null;

        for (DbTable checkTable : schema.getTables()) {
            if (tableName.equalsIgnoreCase(checkTable.getName()))
                return checkTable;
        }
        return null;
    }

    public static DbView getViewFromStructure(final DbSchema schema, final String viewName) {
        if (schema == null || viewName == null)
            return null;

        for (DbView checkView : schema.getViews()) {
            if (viewName.equalsIgnoreCase(checkView.getName()))
                return checkView;
        }
        return null;
    }

    public static boolean isFieldForeignKey(final DbTable table, final DbField field) {
        if (table == null || field == null)
            return false;

        for (DbImportedPKColumn DbImportedPKColumn : table.getImportedKeys()) {
            if (table.getName().equalsIgnoreCase(DbImportedPKColumn.getFkTableName()) &&
                field.getName().equalsIgnoreCase(DbImportedPKColumn.getFkColumnName()))
                return true;
        }
        return false;
    }

    public static boolean isFieldPrimaryKey(final DbTable table, final DbField field) {
        if (table == null || field == null)
            return false;

        DbPrimaryKey pk = table.getPrimaryKey();
        for (DbPrimaryKeyColumn DbPrimaryKeyColumn : pk.getColumns()) {
            if (field.getName().equalsIgnoreCase(DbPrimaryKeyColumn.getColumnName()))
                return true;
        }
        return false;
    }

    public static boolean isFieldExists(final DbSchema schema, final DbTable table, final DbField field) {
        if (schema == null || table == null || field == null)
            return false;

        for (DbTable DbTable : schema.getTables()) {
            if (table.getName().equalsIgnoreCase(DbTable.getName())) {
                for (DbField DbField : DbTable.getFields()) {
                    if (field.getName().equalsIgnoreCase(DbField.getName()))
                        return true;
                }
            }
        }
        return false;
    }

    public static boolean isTableExists(final DbSchema schema, final DbTable table) {
        if (schema == null || table == null)
            return false;

        for (DbTable DbTable : schema.getTables()) {
            if (table.getName().equalsIgnoreCase(DbTable.getName()))
                return true;
        }
        return false;
    }

    public static DbSchema getDbStructure(DatabaseAdapter db_) {
        DbSchema schema = new DbSchema();

        DatabaseMetaData db = db_.getConnection().getMetaData();
        String dbSchema = db.getUserName();

        List<DbTable> list = DatabaseStructureManager.getTableList(db_.getConnection(), dbSchema, "%");
        for (DbTable table : list) {
            schema.getTables().add(table);
        }

        List<DbView> viewVector = db_.getViewList(dbSchema, "%");
        if (viewVector != null)
            schema.getViews().addAll( viewVector );

        schema.getSequences().addAll(db_.getSequnceList(dbSchema));

        for (DbTable table : schema.getTables()) {
            table.getFields().addAll(DatabaseStructureManager.getFieldsList(db_.getConnection(), table.getSchema(), table.getName(), db_.getFamily()));
            table.setPrimaryKey(DatabaseStructureManager.getPrimaryKey(db_.getConnection(), table.getSchema(), table.getName()));
            table.getImportedKeys().addAll(DatabaseStructureManager.getImportedKeys(db_.getConnection(), table.getSchema(), table.getName()));
        }

        for (DbView view : schema.getViews()) {
            view.setText(db_.getViewText(view));
        }

        return schema;
    }

    public static void createWithReplaceAllView(final DatabaseAdapter db_, final DbSchema millSchema) throws Exception {
        boolean[] idx = new boolean[millSchema.getViews().size()];
        for (int i = 0; i < idx.length; i++) {
            idx[i] = false;
        }

        for (boolean anIdx : idx) {
            if (anIdx)
                continue;

            for (int i = 0; i < idx.length; i++) {
                if (idx[i])
                    continue;

                DbView view = millSchema.getViews().get(i);
                try {
                    db_.createView(view);
                    idx[i] = true;
                }
                catch (Exception e) {
                    if (db_.testExceptionViewExists(e)) {
                        try {
                            DatabaseStructureManager.dropView(db_, view);
                        }
                        catch (Exception e1) {
                            log.error("Error drop view", e1);
                            throw e1;
                        }

                        try {
                            db_.createView(view);
                            idx[i] = true;
                        }
                        catch (Exception e1) {
                            log.error("Error create view", e1);
//                            throw e1;
                        }
                    }
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

                if (log.isDebugEnabled())
                    log.debug("View - " + table.getName() + "  remak - " + table.getRemark());

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
        if (table == null)
            return false;

        String s = table.trim();

        String fullCheck[] = {"SQLN_EXPLAIN_PLAN", "DBG", "CHAINED_ROWS"};
        for (String aFullCheck : fullCheck) {
            if (aFullCheck.equalsIgnoreCase(s))
                return true;
        }

        String startCheck[] = {"F_D_", "FOR_DEL_", "F_DEL_", "FOR_D_"};
        for (String aStartCheck : startCheck) {
            if (s.toLowerCase().startsWith(aStartCheck.toLowerCase()))
                return true;
        }

        return false;
    }

    public static boolean isMultiColumnFk(final DbTable table, final DbImportedPKColumn key) {
        for (DbImportedPKColumn checkKey : table.getImportedKeys()) {
            if (checkKey.getFkColumnName().equals(key.getFkColumnName()) &&
                checkKey.getKeySeq() != key.getKeySeq()
                )
                return true;
        }
        return false;
    }

    /**
     * Check what field's default value is default timestamp(date) for bd column
     * For example for Oracle value is 'SYSDATE'
     *
     * @param val value for check
     * @return true, if value is date, otherwise false
     */
    public static boolean checkDefaultTimestamp(final String val) {
        if (val == null)
            return false;

        String s = val.trim().toLowerCase();
        String check[] =
            {"sysdate",
//                  'now', 'today',
                "current_timestamp", "current_time", "current_date"
            };
        for (String aCheck : check) {
            if (aCheck.equals(s))
                return true;
        }
        return false;
    }

    /**
     * @param dbDyn
     * @param idRec             - value of PK in main table
     * @param pkName            - name PK in main table
     * @param pkType            - type of PK in main table
     * @param nameTargetTable   - name of slave table
     * @param namePkTargetTable - name of PK in slave table
     * @param nameTargetField   - name of filed with BigText data in slave table
     * @param insertString      - insert string
     * @param isDelete          - delete data from slave table before insert true/false
     * @throws Exception
     */
    public static void insertBigText(
        final DatabaseAdapter dbDyn, final Object idRec, final String pkName,
        final PrimaryKey pkType,
        final String nameTargetTable,
        final String namePkTargetTable,
        final String nameTargetField,
        final String insertString,
        final boolean isDelete
    ) throws Exception {
        if (pkType.getType()==null) {
            throw new IllegalStateException("type of PK is null");
        }
        String sql_ = null;
        try {

            if (log.isDebugEnabled()) log.debug("First delete data flag - " + isDelete);

            if (isDelete)
                deleteFromBigTable(dbDyn, nameTargetTable, pkName, pkType, idRec);

            PreparedStatement ps1 = null;
            try {

                log.debug("Start insert dta in bigtext field ");

                int pos = 0;
                int prevPos = 0;
                int maxByte = dbDyn.getMaxLengthStringField();

                sql_ =
                    "insert into " + nameTargetTable +
                        "(" + namePkTargetTable + "," + pkName + "," + nameTargetField + ")" +
                        "values" +
                        "(?,?,?)";

                if (log.isDebugEnabled()) log.debug("insert bigtext. sql 2 - " + sql_);

                byte b[] = Utils.getBytesUTF(insertString);

                ps1 = dbDyn.getConnection().prepareStatement(sql_);
                while ((pos = Utils.getStartUTF(b, maxByte, pos)) != -1) {
                    if (log.isDebugEnabled()) log.debug("Name sequence - " + "seq_" + nameTargetTable);

                    CustomSequence seq = new CustomSequence();
                    seq.setSequenceName("seq_" + nameTargetTable);
                    seq.setTableName(nameTargetTable);
                    seq.setColumnName(namePkTargetTable);
                    long idSeq = dbDyn.getSequenceNextValue(seq);

                    if (log.isDebugEnabled()) log.debug("Bind param #1" + idSeq);

                    ps1.setLong(1, idSeq);

                    Integer type = dataType.get(pkType.getType().toLowerCase());
                    if (type==null) {
                        log.warn("Integer value of primary not found. PK type: " + pkType.getType().toLowerCase());
                        type = UNKNOWN_TYPE_VALUE;
                    }

                    switch(type) {
                        case NUMBER_TYPE_VALUE:

                            if (log.isDebugEnabled()) log.debug("Bind param #2 " + idRec);

                            ps1.setLong(2, ((Long) idRec));
                            break;

                        case STRING_TYPE_VALUE:
                            if (log.isDebugEnabled()) log.debug("Bind param #2 " + idRec);

                            ps1.setString(2, (String) idRec);
                            break;
                        case DATE_TYPE_VALUE:
/*
                        if (content.getQueryArea().getPrimaryKeyMask()==null ||
                            content.getQueryArea().getPrimaryKeyMask().trim().length()==0)
                            throw new Exception("date mask for primary key not specified");

                        primaryKeyValue = DbUtils.getStringDate(rs, content.getQueryArea().getPrimaryKey(),
                            content.getQueryArea().getPrimaryKeyMask(), "error", Locale.ENGLISH);
*/
                            throw new Exception("Type of PK 'date' for big_text not implemented");
                        
                        default:
                            throw new Exception("Wrong type of primary key");
                    }

                    String s = new String(b, prevPos, pos - prevPos, "utf-8");

                    if (log.isDebugEnabled()) log.debug("Bind param #3 " + s);

                    ps1.setString(3, s);

                    int count = ps1.executeUpdate();

                    if (log.isDebugEnabled()) log.debug("number of updated records - " + count);

                    ps1.clearParameters();
                    prevPos = pos;

                } 
            }
            finally {
                close(ps1);
                ps1 = null;
            }
        }
        catch (Exception e) {
            log.error("Error insert dat in bigText field.\nSql:\n" + sql_, e);
            throw e;
        }
    }

    public static void deleteFromBigTable(final DatabaseAdapter dbDyn, final String nameTargetTable, final String pkName, final PrimaryKey pkType, final Object idRec) throws Exception {
        if (pkType.getType()==null) {
            throw new IllegalStateException("type of PK is null");
        }
        PreparedStatement ps = null;
        try {
            String sql_ = "delete from " + nameTargetTable + " where " + pkName + "=?";
            if (log.isDebugEnabled()) log.debug("#13.07.01 " + sql_);

            ps = dbDyn.getConnection().prepareStatement(sql_);

            Integer type = dataType.get(pkType.getType().toLowerCase());
            if (type==null) {
                log.warn("Integer value of primary not found. PK type: " + pkType.getType().toLowerCase());
                type = UNKNOWN_TYPE_VALUE;
            }
            switch(type) {
                case NUMBER_TYPE_VALUE:
                    if (log.isDebugEnabled()) log.debug("#88.01.02 " + idRec.getClass().getName());

                    ps.setLong(1, ((Long)idRec));
                    break;
                case STRING_TYPE_VALUE:
                    ps.setString(1, (String) idRec);
                    break;
                case DATE_TYPE_VALUE:
/*
                        if (content.getQueryArea().getPrimaryKeyMask()==null ||
                            content.getQueryArea().getPrimaryKeyMask().trim().length()==0)
                            throw new Exception("date mask for primary key not specified");

                        primaryKeyValue = DbUtils.getStringDate(rs, content.getQueryArea().getPrimaryKey(),
                            content.getQueryArea().getPrimaryKeyMask(), "error", Locale.ENGLISH);
*/
                    throw new Exception("Type of PK 'date' for big_text not implemented");
                default:
                    throw new Exception("Wrong type of primary key");
            }
            int count = ps.executeUpdate();

            if (log.isDebugEnabled())
                log.debug("number of updated records - " + count);

        }
        finally {
            close(ps);
            ps = null;
        }
    }

    public static String getBigTextField(final DatabaseAdapter db_, final Long id_,
                                         final String field_,
                                         final String table_,
                                         final String idx_field_,
                                         final String order_field_
    )
        throws SQLException {
        if (id_ == null)
            return "";

        PreparedStatement ps = null;
        ResultSet rset = null;
        String sql_ = "select " + field_ + " from " + table_ + " where " + idx_field_ + "= ? order by " + order_field_ + " ASC";

        if (log.isDebugEnabled()) {
            log.debug("ID: " + id_);
            log.debug("SQL: " + sql_);
        }

        StringBuilder text = new StringBuilder();
        try {
            ps = db_.getConnection().prepareStatement(sql_);

            if (log.isDebugEnabled())
                log.debug("11.03.01");

            DbUtils.setLong(ps, 1, id_);
            rset = ps.executeQuery();

            while (rset.next()) {
                if (log.isDebugEnabled()) {
                    log.debug("11.03.01 " + text);
                }
                text.append(DbUtils.getString(rset, field_));
            }
        } finally {
            DatabaseManager.close(rset, ps);
            rset = null;
            ps = null;
        }
        return text.toString();
    }

    public static String getRelateString(final DbImportedPKColumn column) {
        return (
            column.getFkSchemaName() == null || column.getFkSchemaName().length() == 0 ?
                "" :
                column.getFkSchemaName() + '.'
        ) +
            column.getFkTableName() + '.' +
            column.getFkColumnName() +
            "->" +
            (
                column.getPkSchemaName() == null || column.getPkSchemaName().length() == 0 ?
                    "" :
                    column.getPkSchemaName() + '.'
            ) +
            column.getPkTableName() + '.' +
            column.getFkColumnName()
            ;
    }

    public static Map<String,DbImportedPKColumn> getFkNames(final List<DbImportedPKColumn> keys) {
        Map<String,DbImportedPKColumn> hash = new HashMap<String,DbImportedPKColumn>();
        for (DbImportedPKColumn column : keys) {
            String search = getRelateString(column);
            Object obj = hash.get(search);
            if (obj == null)
                hash.put(search, column);

        }
        return hash;
/*
        DbImportedPKColumn[] result = new DbImportedPKColumn[hash.size()];

        int i=0;
        for (Enumeration e = hash.elements(); e.hasMoreElements() ;i++)
        {
            result[i] = (DbImportedPKColumn)e.nextElement();
        }
        if (log.isDebugEnabled())
        {
            for (i=0; i<result.length; i++)
                log.debug("key: "+result[i] );
        }
        return result;
*/
    }

    public static int sqlTypesMapping(final String type) {
        if (type == null)
            return Types.OTHER;

        if ("BIT".equals(type)) return Types.BIT;
        else if ("TINYINT".equals(type)) return Types.TINYINT;
        else if ("SMALLINT".equals(type)) return Types.SMALLINT;
        else if ("INTEGER".equals(type)) return Types.INTEGER;
        else if ("BIGINT".equals(type)) return Types.BIGINT;
        else if ("FLOAT".equals(type)) return Types.FLOAT;
        else if ("REAL".equals(type)) return Types.REAL;
        else if ("DOUBLE".equals(type)) return Types.DOUBLE;
        else if ("NUMERIC".equals(type)) return Types.NUMERIC;
        else if ("DECIMAL".equals(type)) return Types.DECIMAL;
        else if ("CHAR".equals(type)) return Types.CHAR;
        else if ("VARCHAR".equals(type)) return Types.VARCHAR;
        else if ("LONGVARCHAR".equals(type)) return Types.LONGVARCHAR;
        else if ("DATE".equals(type)) return Types.DATE;
        else if ("TIME".equals(type)) return Types.TIME;
        else if ("TIMESTAMP".equals(type)) return Types.TIMESTAMP;
        else if ("BINARY".equals(type)) return Types.BINARY;
        else if ("VARBINARY".equals(type)) return Types.VARBINARY;
        else if ("LONGVARBINARY".equals(type)) return Types.LONGVARBINARY;
        else if ("NULL".equals(type)) return Types.NULL;
        else if ("OTHER".equals(type)) return Types.OTHER;
        else if ("JAVA_OBJECT".equals(type)) return Types.JAVA_OBJECT;
        else if ("DISTINCT".equals(type)) return Types.DISTINCT;
        else if ("STRUCT".equals(type)) return Types.STRUCT;
        else if ("ARRAY".equals(type)) return Types.ARRAY;
        else if ("BLOB".equals(type)) return Types.BLOB;
        else if ("CLOB".equals(type)) return Types.CLOB;
        else if ("REF".equals(type)) return Types.REF;
        else return Types.OTHER;

    }

    public static void fixBigTextTable(final DbTable table, final List<DbBigTextTable> bigTables, final int maxLengthTextField) {
        if (bigTables == null || table == null)
            return;

        for (DbBigTextTable big : bigTables) {
            if (big.getSlaveTable().equals(table.getName())) {
                for (DbField field : table.getFields()) {
                    if (big.getStorageField().equals(field.getName()))
                        field.setSize(maxLengthTextField);
                }
            }
        }
    }

    public static DbBigTextTable getBigTextTableDesc(final DbTable table, final List<DbBigTextTable> bigTables) {
        if (bigTables == null || table == null)
            return null;

        for (DbBigTextTable big : bigTables) {
            if (big.getSlaveTable().equals(table.getName()))
                return big;
        }
        return null;
    }

    public static void createDbStructure(final DatabaseAdapter db_, final DbSchema millSchema) throws Exception {
        // create sequences
        for (DbSequence seq : millSchema.getSequences()) {
            try {
                if (log.isDebugEnabled())
                    log.debug("create sequence " + seq.getName());

                db_.createSequence(seq);
            }
            catch (Exception e) {
                if (db_.testExceptionSequenceExists(e)) {
                    if (log.isDebugEnabled()) {
                        log.debug("sequence " + seq.getName() + " already exists");
                        log.debug("drop sequence " + seq.getName());
                    }
                    db_.dropSequence(seq.getName());

                    if (log.isDebugEnabled())
                        log.debug("create sequence " + seq.getName());

                    try {
                        db_.createSequence(seq);
                    }
                    catch (Exception e1) {
                        log.error("Error create sequence - ", e1);
                        throw e;
                    }
                } else {
                    log.error("Error create sequence - ", e);
                    throw e;
                }
            }
        }
        for (DbTable table : millSchema.getTables()) {

            fixBigTextTable(
                table,
                millSchema.getBigTextTable(),
                db_.getMaxLengthStringField()
            );

            if (!isSkipTable(table.getName())) {
                try {
                    if (log.isDebugEnabled())
                        log.debug("create table " + table.getName());

                    db_.createTable(table);
                }
                catch (SQLException e) {
                    if (db_.testExceptionTableExists(e)) {
                        if (log.isDebugEnabled()) {
                            log.debug("table " + table.getName() + " already exists");
                            log.debug("drop table " + table.getName());
                        }
                        db_.dropTable(table);
                        db_.getConnection().commit();

                        if (log.isDebugEnabled())
                            log.debug("create table " + table.getName());

                        db_.createTable(table);
                        db_.getConnection().commit();
                    } else {
                        log.error("Error create table " + table.getName(), e);
                        throw e;
                    }
                }
                DatabaseStructureManager.setDataTable(db_, table, millSchema.getBigTextTable());
                db_.getConnection().commit();
            } else {
                if (log.isDebugEnabled())
                    log.debug("skip table " + table.getName());
            }
        }

        createWithReplaceAllView(db_, millSchema);
    }

    public static DbKeyActionRule decodeUpdateRule(final ResultSet rs) {
        try {
            Object obj = rs.getObject("UPDATE_RULE");
            if (obj == null)
                return null;

            DbKeyActionRule rule = new DbKeyActionRule();
            rule.setRuleType(DbUtils.getInteger(rs, "UPDATE_RULE"));

            switch (rule.getRuleType().intValue()) {
                case DatabaseMetaData.importedKeyNoAction:
                    rule.setRuleName("java.sql.DatabaseMetaData.importedKeyNoAction");
                    break;

                case DatabaseMetaData.importedKeyCascade:
                    rule.setRuleName("java.sql.DatabaseMetaData.importedKeyCascade");
                    break;

                case DatabaseMetaData.importedKeySetNull:
                    rule.setRuleName("java.sql.DatabaseMetaData.importedKeySetNull");
                    break;

                case DatabaseMetaData.importedKeySetDefault:
                    rule.setRuleName("java.sql.DatabaseMetaData.importedKeySetDefault");
                    break;

                case DatabaseMetaData.importedKeyRestrict:
                    rule.setRuleName("java.sql.DatabaseMetaData.importedKeyRestrict");
                    break;

                default:
                    rule.setRuleName("unknown UPDATE_RULE(" + rule.getRuleType() + ")");
                    System.out.println("unknown UPDATE_RULE(" + rule.getRuleType() + ")");
                    break;
            }
            return rule;
        }
        catch (Exception e) {
        }
        return null;
    }

    public static DbKeyActionRule decodeDeleteRule(final ResultSet rs) {
        try {
            Object obj = rs.getObject("DELETE_RULE");
            if (obj == null)
                return null;

            DbKeyActionRule rule = new DbKeyActionRule();
            rule.setRuleType(DbUtils.getInteger(rs, "DELETE_RULE"));

            switch (rule.getRuleType().intValue()) {
                case DatabaseMetaData.importedKeyNoAction:
                    rule.setRuleName("java.sql.DatabaseMetaData.importedKeyNoAction");
                    break;

                case DatabaseMetaData.importedKeyCascade:
                    rule.setRuleName("java.sql.DatabaseMetaData.importedKeyCascade");
                    break;

                case DatabaseMetaData.importedKeySetNull:
                    rule.setRuleName("java.sql.DatabaseMetaData.importedKeySetNull");
                    break;

                case DatabaseMetaData.importedKeyRestrict:
                    rule.setRuleName("java.sql.DatabaseMetaData.importedKeyRestrict");
                    break;

                case DatabaseMetaData.importedKeySetDefault:
                    rule.setRuleName("java.sql.DatabaseMetaData.importedKeySetDefault");
                    break;

                default:
                    rule.setRuleName("unknown DELETE_RULE(" + rule.getRuleType() + ")");
                    System.out.println("unknown DELETE_RULE(" + rule.getRuleType() + ")");
                    break;
            }
            return rule;
        }
        catch (Exception e) {
        }
        return null;
    }

    public static DbKeyActionRule decodeDeferrabilityRule(final ResultSet rs) {
        try {
            Object obj = rs.getObject("DEFERRABILITY");
            if (obj == null)
                return null;

            DbKeyActionRule rule = new DbKeyActionRule();
            rule.setRuleType(DbUtils.getInteger(rs, "DEFERRABILITY"));

            switch (rule.getRuleType().intValue()) {
                case DatabaseMetaData.importedKeyInitiallyDeferred:
                    rule.setRuleName("java.sql.DatabaseMetaData.importedKeyInitiallyDeferred");
                    break;
                case DatabaseMetaData.importedKeyInitiallyImmediate:
                    rule.setRuleName("java.sql.DatabaseMetaData.importedKeyInitiallyImmediate");
                    break;
                case DatabaseMetaData.importedKeyNotDeferrable:
                    rule.setRuleName("java.sql.DatabaseMetaData.importedKeyNotDeferrable");
                    break;
                default:
                    rule.setRuleName("unknown DEFERRABILITY(" + rule.getRuleType() + ")");
                    System.out.println("unknown DEFERRABILITY(" + rule.getRuleType() + ")");
                    break;
            }
            return rule;
        }
        catch (Exception e) {
        }
        return null;
    }

    public static int runSQL(final DatabaseAdapter db, final String query, final Object[] params, final int[] types)
        throws SQLException {
        int n = 0;
        Statement stmt = null;
        PreparedStatement pstm = null;

        try {
            if (params == null) {
                stmt = db.getConnection().createStatement();
                n = stmt.executeUpdate(query);
            } else {
                pstm = db.getConnection().prepareStatement(query);
                for (int i = 0; i < params.length; i++) {
                    if (params[i] != null)
                        pstm.setObject(i + 1, params[i], types[i]);
                    else
                        pstm.setNull(i + 1, types[i]);
                }

                n = pstm.executeUpdate();
                stmt = pstm;
            }
        }
        catch (SQLException e) {
            log.error("SQL query:\n" + query);
            if (params != null) {
                for (int ii = 0; ii < params.length; ii++)

                    log.error("parameter #" + (ii + 1) + ": " + (params[ii] != null ? params[ii].toString() : null));
            }
            log.error("SQLException", e);
            throw e;
        }
        finally {
            close(stmt);
            stmt = null;
            pstm = null;
        }
        return n;
    }

    public static Long getLongValue(final DatabaseAdapter db, final String sql, final Object[] params, final int[] types)
        throws SQLException {
        Statement stmt = null;
        PreparedStatement pstm;
        ResultSet rs = null;

        try {
            if (params == null) {
                stmt = db.getConnection().createStatement();
                rs = stmt.executeQuery(sql);
            } else {
                pstm = db.getConnection().prepareStatement(sql);
                for (int i = 0; i < params.length; i++) {
                    if (types==null) {
                        pstm.setObject(i + 1, params[i]);
                    }
                    else {
                        pstm.setObject(i + 1, params[i], types[i]);
                    }
                }

                rs = pstm.executeQuery();
                stmt = pstm;
            }

            if (rs.next()) {
                long tempLong = rs.getLong(1);
                if (rs.wasNull())
                    return null;

                return tempLong;
            }
            return null;
        }
        catch (SQLException e) {
            log.error("error getting long value fron sql '" + sql + "'", e);
            throw e;
        }
        finally {
            close(rs, stmt);
            rs = null;
            stmt = null;
            pstm = null;
        }
    }

    public static List<Long> getLongValueList(final DatabaseAdapter db, final String sql, final Object[] params, final int[] types)
        throws SQLException {

        Statement stmt = null;
        PreparedStatement pstm;
        ResultSet rs = null;
        List<Long> list = new ArrayList<Long>();
        try {
            if (params == null) {
                stmt = db.getConnection().createStatement();
                rs = stmt.executeQuery(sql);
            } else {
                pstm = db.getConnection().prepareStatement(sql);
                for (int i = 0; i < params.length; i++) {
                    if (types==null) {
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
                if (rs.wasNull())
                    continue;

                list.add(tempLong);
            }
            return list;
        }
        catch (SQLException e) {
            log.error("error getting long value fron sql '" + sql + "'", e);
            throw e;
        }
        finally {
            close(rs, stmt);
            rs = null;
            stmt = null;
            pstm = null;
        }
    }

    public static List<Long> getIdByList(final DatabaseAdapter adapter, final String sql, final Object[] param) {
        Statement stmt = null;
        PreparedStatement pstm;
        ResultSet rs = null;
        List<Long> list = new ArrayList<Long>();
        try {
            if (param == null) {
                stmt = adapter.getConnection().createStatement();
                rs = stmt.executeQuery(sql);
            } else {
                pstm = adapter.getConnection().prepareStatement(sql);
                for (int i = 0; i < param.length; i++)
                    pstm.setObject(i + 1, param[i]);

                rs = pstm.executeQuery();
                stmt = pstm;
            }

            while (rs.next()) {
                long tempLong = rs.getLong(1);
                if (rs.wasNull())
                    continue;

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
            close(rs, stmt);
            rs = null;
            stmt = null;
            pstm = null;
        }
    }
}

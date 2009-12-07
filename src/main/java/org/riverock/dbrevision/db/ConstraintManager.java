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

import org.riverock.dbrevision.annotation.schema.db.*;
import org.riverock.dbrevision.exception.DbRevisionException;
import org.riverock.dbrevision.utils.DbUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * User: SergeMaslyukov
 * Date: 17.03.2009
 * Time: 14:34:29
 */
public class ConstraintManager {
    private final static Logger log = Logger.getLogger(DatabaseStructureManager.class);

    /*

ALTER TABLE auth_object_arm
ADD CONSTRAINT id_code_arm_aoa_uk UNIQUE (code_object_arm, id_arm)

     */
    public static void createIndex(Database db, DbIndex index) {
        if (StringUtils.isBlank(index.getIndexName())) {
            throw new DbRevisionException("Index name is null");
        }

/*
CREATE INDEX idx_id_employee_b_advance ON b_advance
  (
    id_employee                     ASC
  )
         */
        String sql;
        if (index.isNonUnique()) {
            sql = "CREATE INDEX " + index.getIndexName() + " ON " + index.getTableName() + " (";

            Collections.sort(index.getColumns(), DbIdxComparator.getInstance());
            boolean isFirst = true;
            for (DbIndexColumn indexColumn : index.getColumns()) {
                if (!isFirst) {
                    sql += ",";
                }
                else {
                    isFirst = false;
                }

                sql += (indexColumn.getColumnName() +' ' + ((Boolean.TRUE.equals(indexColumn.isAscending()))?" ASC ": " DESC ") +' ');
            }
            sql += ") ";
        }
        else {
/*
        List<DbIndex> checkIdx = getIndexes(db, index.getSchemaName(), index.getTableName());
        if (checkIdx != null && checkIdx.getColumns().size() != 0) {
            String s = "primary key already exists";
            System.out.println(s);
            if (log.isInfoEnabled()) {
                log.info(s);
            }

            throw new DbRevisionException(s);
        }
*/


/*
        ALTER TABLE QQQ.AUTH_ACCESS_GROUP ADD CONSTRAINT AAA
  PRIMARY KEY (
  ID_ACCESS_GROUP
)
*/

            sql = "ALTER TABLE " + index.getTableName() + " " +
                "ADD CONSTRAINT " + index.getIndexName() + " UNIQUE (";

            Collections.sort(index.getColumns(), DbIdxComparator.getInstance());
            boolean isFirst = true;
            for (DbIndexColumn indexColumn : index.getColumns()) {
                if (!isFirst) {
                    sql += ",";
                }
                else {
                    isFirst = false;
                }

                sql += indexColumn.getColumnName();
            }
            sql += ") USING INDEX ";
        }
        PreparedStatement ps = null;
        try {
            ps = db.getConnection().prepareStatement(sql);
            ps.executeUpdate();
        }
        catch (SQLException exc) {
            throw new DbRevisionException(exc);
        }
        finally {
            DbUtils.close(ps);
            ps = null;
        }

    }

    public static void dropIndex(Database db, DbIndex index) {
        try {
            dropAsConstraint(db, index.getTableName(), index.getIndexName());
        }
        catch (Throwable e) {

            if (e.getCause()!=null && ExceptionManager.isConstraintNonExist(db.getFamily(), e.getCause())) {
                dropAsIndex(db, index.getTableName(), index.getIndexName());
            }
            else {
                throw new DbRevisionException("Error drop constraint", e);
            }
        }
    }

    public static void addPk(final Database db_, final DbPrimaryKey pk) {
        if (StringUtils.isBlank(pk.getPkName())) {
            throw new DbRevisionException("Primary key name is null");
        }

        DbPrimaryKey checkPk = getPrimaryKey(db_, pk.getSchemaName(), pk.getTableName());

        if (checkPk != null && checkPk.getColumns().size() != 0) {
            String s = "primary key already exists";
            System.out.println(s);
            if (log.isInfoEnabled()) {
                log.info(s);
            }

            throw new DbRevisionException(s);
        }


/*
        ALTER TABLE QQQ.AUTH_ACCESS_GROUP ADD CONSTRAINT AAA
  PRIMARY KEY (
  ID_ACCESS_GROUP
)
*/


        String sql =
            "ALTER TABLE " + pk.getTableName() + " " +
                "ADD CONSTRAINT " + pk.getPkName() + " PRIMARY KEY (";

        Collections.sort(pk.getColumns(), DbPkComparator.getInstance());
        boolean isFirst = true;
        for (DbPrimaryKeyColumn primaryKeyColumn : pk.getColumns()) {
            if (!isFirst) {
                sql += ",";
            }
            else {
                isFirst = false;
            }

            sql += primaryKeyColumn.getColumnName();
        }
        sql += ")";

        PreparedStatement ps = null;
        try {
            ps = db_.getConnection().prepareStatement(sql);
            ps.executeUpdate();
        }
        catch (SQLException exc) {
            throw new DbRevisionException(exc);
        }
        finally {
            DbUtils.close(ps);
            ps = null;
        }
    }

    public static void dropPk(Database db, DbPrimaryKey pk){
        dropAsConstraint(db, pk.getTableName(), pk.getPkName());
    }

    public static void dropFk(Database db, DbForeignKey fk){
        dropAsConstraint(db, fk.getFkTableName(), fk.getFkName());
    }

    private static void dropAsConstraint(Database db, String tableName, String constraintName){
        String sql = "alter table "+tableName+" drop constraint " + constraintName;
        PreparedStatement ps = null;
        try {
            ps = db.getConnection().prepareStatement(sql);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new DbRevisionException("Error drop constraint '"+constraintName+ " for table '"+tableName,  e);
        }
        finally {
            DbUtils.close(ps);
            ps = null;
        }
    }

    private static void dropAsIndex(Database db, String tableName, String constraintName){
        String sql = "DROP INDEX " + constraintName;
        PreparedStatement ps = null;
        try {
            ps = db.getConnection().prepareStatement(sql);
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

    /**
     * Return info about all PK for tables, which referenced from current table(tableName)
     *
     * @param adapter db adapter
     * @param tableName  name of table
     * @param schemaName name of schema
     * @return List<DbForeignKey>
     */
    public static List<DbForeignKey> getForeignKeys(Database adapter, String schemaName, String tableName) {
        List<DbForeignKey> v = new ArrayList<DbForeignKey>();
        try {
            DatabaseMetaData db = adapter.getConnection().getMetaData();
            ResultSet columnNames = null;

            if (log.isDebugEnabled()) {
                log.debug("Get data from getForeignKeys");
            }

            try {
                columnNames = db.getImportedKeys(null, schemaName, tableName);

                DbForeignKey key=null;
                while (columnNames.next()) {
                    if (key==null) {
                        key = processResultSetForForeignKey(columnNames);
                        v.add(key);
                    }
                    else {

                        DbForeignKey fk = processResultSetForForeignKey(columnNames);
                        if (
                            !StringUtils.equals(key.getPkSchemaName(), fk.getPkSchemaName()) ||
                                !StringUtils.equals(key.getPkTableName(), fk.getPkTableName()) ||
                                !StringUtils.equals(key.getPkName(), fk.getPkName()) ||
                                !StringUtils.equals(key.getFkSchemaName(), fk.getFkSchemaName()) ||
                                !StringUtils.equals(key.getFkTableName(), fk.getFkTableName()) ||
                                !StringUtils.equals(key.getFkName(), fk.getFkName())
                            )
                        {
                            key = fk;
                            v.add(key);
                        }
                    }
                    DbForeignKeyColumn column = new DbForeignKeyColumn();
                    column.setPkColumnName(columnNames.getString("PKCOLUMN_NAME"));
                    column.setFkColumnName(columnNames.getString("FKCOLUMN_NAME"));
                    column.setKeySeq(DbUtils.getInteger(columnNames, "KEY_SEQ"));

                    key.getColumns().add(column);


                    if (log.isDebugEnabled()) {
                        log.debug(
                            columnNames.getString("PKTABLE_CAT") + " - " +
                                columnNames.getString("PKTABLE_SCHEM") + "." +
                                columnNames.getString("PKTABLE_NAME") +
                                " - " +
                                columnNames.getString("PKCOLUMN_NAME") +
                                " >> " +
                                columnNames.getString("FKTABLE_CAT") + "." +
                                columnNames.getString("FKTABLE_SCHEM") + "." +
                                columnNames.getString("FKTABLE_NAME") +
                                "; " +
                                columnNames.getShort("KEY_SEQ") + " " +
                                columnNames.getString("UPDATE_RULE") + " " +
                                columnNames.getShort("DELETE_RULE") + " ");
                        Object obj = null;
                        int deferr;
                        obj = columnNames.getObject("DELETE_RULE");

                        if (obj == null)
                            deferr = Integer.MIN_VALUE;
                        else
                            deferr = (int) columnNames.getShort("DELETE_RULE");

                        switch (deferr) {
                            case DatabaseMetaData.importedKeyNoAction:
                                log.debug("DELETE_RULE.importedKeyNoAction");
                                break;
                            case DatabaseMetaData.importedKeyCascade:
                                log.debug("DELETE_RULE.importedKeyCascade");
                                break;
                            case DatabaseMetaData.importedKeySetNull:
                                log.debug("DELETE_RULE.importedKeySetNull");
                                break;
                            case DatabaseMetaData.importedKeyRestrict:
                                log.debug("DELETE_RULE.importedKeyRestrict");
                                break;
                            case DatabaseMetaData.importedKeySetDefault:
                                log.debug("DELETE_RULE.importedKeySetDefault");
                                break;
                            default:
                                log.debug("unknown DELETE_RULE(" + deferr + ")");
                                break;
                        }
                        log.debug("obj: " + obj.getClass().getName() + " ");

                        log.debug("Foreign key name: " + columnNames.getString("FK_NAME") + " ");
                        log.debug("Primary key name: " + columnNames.getString("PK_NAME") + " ");

                        obj = columnNames.getObject("DEFERRABILITY");
                        if (obj == null)
                            deferr = -1;
                        else
                            deferr = (int) columnNames.getShort("DEFERRABILITY");

                        switch (deferr) {
                            case DatabaseMetaData.importedKeyInitiallyDeferred:
                                log.debug("importedKeyInitiallyDeferred");
                                break;
                            case DatabaseMetaData.importedKeyInitiallyImmediate:
                                log.debug("importedKeyInitiallyImmediate");
                                break;
                            case DatabaseMetaData.importedKeyNotDeferrable:
                                log.debug("importedKeyNotDeferrable");
                                break;
                            default:
                                log.debug("unknown DEFERRABILITY(" + deferr + ")");
                                break;
                        }
                    }
                }
                columnNames.close();
                columnNames = null;

            }
            catch (Exception e1) {
                log.debug("Method getForeignKeys(null, null, tableName) not supported", e1);
            }
            log.debug("Done  data from getForeignKeys");

        }
        catch (Exception e) {
            throw new DbRevisionException(e);
        }
        return v;
    }

    private static DbForeignKey processResultSetForForeignKey(ResultSet columnNames) throws SQLException {
        DbForeignKey key = new DbForeignKey();
        key.setPkSchemaName(columnNames.getString("PKTABLE_SCHEM"));
        key.setPkTableName(columnNames.getString("PKTABLE_NAME"));
        key.setPkName(columnNames.getString("PK_NAME"));

        key.setFkSchemaName(columnNames.getString("FKTABLE_SCHEM"));
        key.setFkTableName(columnNames.getString("FKTABLE_NAME"));
        key.setFkName(columnNames.getString("FK_NAME"));

        key.setUpdateRule(decodeUpdateRule(columnNames));
        key.setDeleteRule(decodeDeleteRule(columnNames));
        key.setDeferrability(decodeDeferrabilityRule(columnNames));
        return key;
    }

    /**
     * <P>Each index column description has the following columns:
     *  <OL>
     *	<LI><B>TABLE_CAT</B> String => table catalog (may be <code>null</code>)
     *	<LI><B>TABLE_SCHEM</B> String => table schema (may be <code>null</code>)
     *	<LI><B>TABLE_NAME</B> String => table name
     *	<LI><B>NON_UNIQUE</B> boolean => Can index values be non-unique.
     *      false when TYPE is tableIndexStatistic
     *	<LI><B>INDEX_QUALIFIER</B> String => index catalog (may be <code>null</code>);
     *      <code>null</code> when TYPE is tableIndexStatistic
     *	<LI><B>INDEX_NAME</B> String => index name; <code>null</code> when TYPE is
     *      tableIndexStatistic
     *	<LI><B>TYPE</B> short => index type:
     *      <UL>
     *      <LI> tableIndexStatistic - this identifies table statistics that are
     *           returned in conjuction with a table's index descriptions
     *      <LI> tableIndexClustered - this is a clustered index
     *      <LI> tableIndexHashed - this is a hashed index
     *      <LI> tableIndexOther - this is some other style of index
     *      </UL>
     *	<LI><B>ORDINAL_POSITION</B> short => column sequence number
     *      within index; zero when TYPE is tableIndexStatistic
     *	<LI><B>COLUMN_NAME</B> String => column name; <code>null</code> when TYPE is
     *      tableIndexStatistic
     *	<LI><B>ASC_OR_DESC</B> String => column sort sequence, "A" => ascending,
     *      "D" => descending, may be <code>null</code> if sort sequence is not supported;
     *      <code>null</code> when TYPE is tableIndexStatistic
     *	<LI><B>CARDINALITY</B> int => When TYPE is tableIndexStatistic, then
     *      this is the number of rows in the table; otherwise, it is the
     *      number of unique values in the index.
     *	<LI><B>PAGES</B> int => When TYPE is  tableIndexStatisic then
     *      this is the number of pages used for the table, otherwise it
     *      is the number of pages used for the current index.
     *	<LI><B>FILTER_CONDITION</B> String => Filter condition, if any.
     *      (may be <code>null</code>)
     *  </OL>
     *
     * @param columnNames result set
     * @return index
     * @throws SQLException on error
     */
    private static DbIndex processResultSetForIndex(ResultSet columnNames) throws SQLException {
        DbIndex index = new DbIndex();
        index.setCatalogName(columnNames.getString("TABLE_CAT"));
        index.setSchemaName(columnNames.getString("TABLE_SCHEM"));
        index.setTableName(columnNames.getString("TABLE_NAME"));
        index.setNonUnique(DbUtils.getBoolean(columnNames, "NON_UNIQUE", false));

        index.setIndexQualifier(columnNames.getString("INDEX_QUALIFIER"));
        index.setIndexName(columnNames.getString("INDEX_NAME"));
        index.setType(DbUtils.getInteger(columnNames, "TYPE"));
        index.setCardinality(DbUtils.getInteger(columnNames, "CARDINALITY"));
        index.setPages(DbUtils.getInteger(columnNames, "PAGES"));
        index.setFilterCondition(columnNames.getString("FILTER_CONDITION"));

        return index;
    }

    public static DbPrimaryKey getPrimaryKey(Database adapter, String schemaPattern, String tablePattern) {

        if (log.isDebugEnabled()) {
            log.debug("Get data from getPrimaryKeys");
        }

        DbPrimaryKey pk=null;
        try {
            DatabaseMetaData db = adapter.getConnection().getMetaData();
            ResultSet metaData = null;
            metaData = db.getPrimaryKeys(null, schemaPattern, tablePattern);

            while (metaData.next()) {
                if (pk==null) {
                    pk = new DbPrimaryKey();
                    pk.setCatalogName(metaData.getString("TABLE_CAT"));
                    pk.setSchemaName(metaData.getString("TABLE_SCHEM"));
                    pk.setTableName(metaData.getString("TABLE_NAME"));
                    pk.setPkName(metaData.getString("PK_NAME"));
                }
                DbPrimaryKeyColumn pkColumn = new DbPrimaryKeyColumn();

                pkColumn.setColumnName(metaData.getString("COLUMN_NAME"));
                pkColumn.setKeySeq(DbUtils.getInteger(metaData, "KEY_SEQ"));

                pk.getColumns().add(pkColumn);

                if (log.isDebugEnabled()) {
                    log.debug(
                        pk.getCatalogName() + "." +
                            pk.getSchemaName() + "." +
                            pk.getTableName() +
                            " - " +
                            pkColumn.getColumnName() +
                            " " +
                            pkColumn.getKeySeq() + " " +
                            pk.getPkName() + " " +
                            ""
                    );
                }
            }
            metaData.close();
            metaData = null;
        }
        catch (SQLException e1) {
            throw new DbRevisionException(e1);
        }

        if (log.isDebugEnabled()) {
            log.debug("Done data from getPrimaryKeys");
        }
        if (pk==null) {
            return null;
        }

        Collections.sort(pk.getColumns(), DbPkComparator.getInstance());

        if (log.isDebugEnabled()) {
            if (pk.getColumns().size() > 1) {
                log.debug("Table with multicolumn PK.");

                for (DbPrimaryKeyColumn pkColumn : pk.getColumns()) {
                    log.debug(
                            pk.getCatalogName() + "." +
                                    pk.getSchemaName() + "." +
                                    pk.getTableName() +
                                    " - " +
                                    pkColumn.getColumnName() +
                                    " " +
                                    pkColumn.getKeySeq() + " " +
                                    pk.getPkName() + " " +
                                    ""
                    );
                }
            }
        }
        return pk;
    }

    /**
     * create foreign key
     *
     * @param adapter db adapter
     * @param fk list of foreign keys
     */
    public static void createFk(Database adapter, DbForeignKey fk) {
        if (fk == null) {
            return;
        }

        if (StringUtils.isBlank(fk.getFkName())) {
            throw new DbRevisionException("Foreign key name is null");
        }

        String sql =
            "ALTER TABLE " + fk.getFkTableName() + " " +
                "ADD CONSTRAINT " + fk.getFkName() + " FOREIGN KEY (";

        Collections.sort(fk.getColumns(), DbFkComparator.getInstance());
        boolean isFirst = true;
        for (DbForeignKeyColumn foreignKeyColumn : fk.getColumns()) {
            if (!isFirst) {
                sql += ",";
            }
            else {
                isFirst = false;
            }

            sql += foreignKeyColumn.getFkColumnName();
        }
        sql += ")\nREFERENCES " + fk.getPkTableName() + " (";

        isFirst = true;
        for (DbForeignKeyColumn foreignKeyColumn : fk.getColumns()) {
            if (!isFirst) {
                sql += ",";
            }
            else {
                isFirst = false;
            }

            sql += foreignKeyColumn.getPkColumnName();
        }
        sql += ") ";
        switch (fk.getDeleteRule().getRuleType()) {
            case DatabaseMetaData.importedKeyRestrict:
                sql += adapter.getOnDeleteSetNull();
                break;
            case DatabaseMetaData.importedKeyCascade:
                sql += "ON DELETE CASCADE ";
                break;

            default:
                throw new IllegalArgumentException(" imported keys delete rule '" +
                    fk.getDeleteRule().getRuleName() + "' not supported");
        }
        switch (fk.getDeferrability().getRuleType()) {
            case DatabaseMetaData.importedKeyNotDeferrable:
                break;
            case DatabaseMetaData.importedKeyInitiallyDeferred:
                sql += " DEFERRABLE INITIALLY DEFERRED";
                break;

            default:
                throw new IllegalArgumentException(" imported keys deferred rule '" +
                    fk.getDeferrability().getRuleName() + "' not supported");
        }

        PreparedStatement ps = null;
        try {
            ps = adapter.getConnection().prepareStatement(sql);
            ps.executeUpdate();
        }
        catch (SQLException exc) {
            if (!adapter.testExceptionTableExists(exc)) {
                log.error("sql " + sql);
                log.error("code " + exc.getErrorCode());
                log.error("state " + exc.getSQLState());
                log.error("message " + exc.getMessage());
                log.error("string " + exc.toString());
            }
            throw new DbRevisionException(exc);
        }
        finally {
            DbUtils.close(ps);
            ps = null;
        }
    }

    public static List<DbIndex> getIndexes(Database adapter, String schemaName, String tableName) {
        List<DbIndex> v = new ArrayList<DbIndex>();
        try {
            DatabaseMetaData db = adapter.getConnection().getMetaData();
            ResultSet columnNames = null;

            if (log.isDebugEnabled()) {
                log.debug("Get data from getIndexes");
            }

            columnNames = db.getIndexInfo(null, schemaName, tableName, false, false);

            DbIndex key=null;
            while (columnNames.next()) {
                if (key==null) {
                    key = processResultSetForIndex(columnNames);
                    v.add(key);
                }
                else {

                    DbIndex fk = processResultSetForIndex(columnNames);
                    if (
                        !StringUtils.equals(key.getCatalogName(), fk.getCatalogName()) ||
                            !StringUtils.equals(key.getSchemaName(), fk.getSchemaName()) ||
                            !StringUtils.equals(key.getTableName(), fk.getTableName()) ||
                            !StringUtils.equals(key.getIndexName(), fk.getIndexName())
                        )
                    {
                        key = fk;
                        v.add(key);
                    }
                }
                DbIndexColumn column = new DbIndexColumn();
                column.setColumnName(columnNames.getString("COLUMN_NAME"));
                column.setKeySeq(DbUtils.getInteger(columnNames, "ORDINAL_POSITION"));
                String asc = columnNames.getString("ASC_OR_DESC");
                Boolean isAscending = null;
                if (StringUtils.equals(asc, "A")) {
                    isAscending = true;
                }
                else if (StringUtils.equals(asc, "D")) {
                    isAscending = false;
                }
                column.setAscending(isAscending);

                key.getColumns().add(column);


                if (log.isDebugEnabled()) {
                    log.debug(
                        key.getCatalogName() + " - " +
                            key.getSchemaName() + "." +
                            key.getTableName() +
                            " - " +
                            column.getColumnName() +
                            "; " +
                            column.getKeySeq() + " " +
                            column.isAscending() + " "
                    );
                }
            }
            columnNames.close();
            columnNames = null;

            log.debug("Done  data from getForeignKeys");
        }
        catch (Exception e) {
            throw new DbRevisionException(e);
        }
        return v;
    }

    public static DbKeyActionRule decodeUpdateRule(final ResultSet rs) {
        Object obj;
        DbKeyActionRule rule = null;
        try {
            obj = rs.getObject("UPDATE_RULE");
            if (obj == null) {
                return null;
            }

            rule = new DbKeyActionRule();
            rule.setRuleType(DbUtils.getInteger(rs, "UPDATE_RULE"));
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        }

        switch (rule.getRuleType()) {
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

    public static DbKeyActionRule decodeDeleteRule(final ResultSet rs) {
        DbKeyActionRule rule = null;
        try {
            Object obj = rs.getObject("DELETE_RULE");
            if (obj == null) {
                return null;
            }

            rule = new DbKeyActionRule();
            rule.setRuleType(DbUtils.getInteger(rs, "DELETE_RULE"));
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        }

        switch (rule.getRuleType()) {
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

    public static DbKeyActionRule decodeDeferrabilityRule(final ResultSet rs) {
        DbKeyActionRule rule = null;
        try {
            Object obj = rs.getObject("DEFERRABILITY");
            if (obj == null) {
                return null;
            }

            rule = new DbKeyActionRule();
            rule.setRuleType(DbUtils.getInteger(rs, "DEFERRABILITY"));
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        }

        switch (rule.getRuleType()) {
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
}

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


/**
 * User: SergeMaslyukov
 * Date: 17.03.2009
 * Time: 14:34:29
 */
public class ConstraintManager {
    
    /*

ALTER TABLE auth_object_arm
ADD CONSTRAINT id_code_arm_aoa_uk UNIQUE (code_object_arm, id_arm)

     */
    public static void createIndex(Database db, DbIndex index) {
        if (StringUtils.isBlank(index.getI())) {
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
            sql = "CREATE INDEX " + index.getI() + " ON " + index.getT() + " (";

            Collections.sort(index.getColumns(), DbIdxComparator.getInstance());
            boolean isFirst = true;
            for (DbIndexColumn indexColumn : index.getColumns()) {
                if (!isFirst) {
                    sql += ",";
                }
                else {
                    isFirst = false;
                }

                sql += (indexColumn.getC() +' ' + ((Boolean.TRUE.equals(indexColumn.isAsc()))?" ASC ": " DESC ") +' ');
            }
            sql += ") ";
        }
        else {
/*
        List<DbIndex> checkIdx = getIndexes(db, index.getS(), index.getT());
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

            sql = "ALTER TABLE " + index.getT() + " " +
                "ADD CONSTRAINT " + index.getI() + " UNIQUE (";

            Collections.sort(index.getColumns(), DbIdxComparator.getInstance());
            boolean isFirst = true;
            for (DbIndexColumn indexColumn : index.getColumns()) {
                if (!isFirst) {
                    sql += ",";
                }
                else {
                    isFirst = false;
                }

                sql += indexColumn.getC();
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
            dropAsConstraint(db, index.getT(), index.getI());
        }
        catch (Throwable e) {

            if (e.getCause()!=null && ExceptionManager.isConstraintNonExist(db.getFamily(), e.getCause())) {
                dropAsIndex(db, index.getT(), index.getI());
            }
            else {
                throw new DbRevisionException("Error drop constraint", e);
            }
        }
    }

    public static void addPk(final Database db_, final DbPrimaryKey pk) {
        if (StringUtils.isBlank(pk.getPk())) {
            throw new DbRevisionException("Primary key name is null");
        }

        DbPrimaryKey checkPk = getPk(db_, pk.getS(), pk.getT());

        if (checkPk != null && checkPk.getColumns().size() != 0) {
            String s = "primary key already exists";
            throw new DbRevisionException(s);
        }


/*
        ALTER TABLE QQQ.AUTH_ACCESS_GROUP ADD CONSTRAINT AAA
  PRIMARY KEY (
  ID_ACCESS_GROUP
)
*/


        String sql =
            "ALTER TABLE " + pk.getT() + " " +
                "ADD CONSTRAINT " + pk.getPk() + " PRIMARY KEY (";

        Collections.sort(pk.getColumns(), DbPkComparator.getInstance());
        boolean isFirst = true;
        for (DbPrimaryKeyColumn primaryKeyColumn : pk.getColumns()) {
            if (!isFirst) {
                sql += ",";
            }
            else {
                isFirst = false;
            }

            sql += primaryKeyColumn.getC();
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
        dropAsConstraint(db, pk.getT(), pk.getPk());
    }

    public static void dropFk(Database db, DbForeignKey fk){
        dropAsConstraint(db, fk.getFkTable(), fk.getFk());
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
                            !StringUtils.equals(key.getPkSchema(), fk.getPkSchema()) ||
                                !StringUtils.equals(key.getPkTable(), fk.getPkTable()) ||
                                !StringUtils.equals(key.getPk(), fk.getPk()) ||
                                !StringUtils.equals(key.getFkSchema(), fk.getFkSchema()) ||
                                !StringUtils.equals(key.getFkTable(), fk.getFkTable()) ||
                                !StringUtils.equals(key.getFk(), fk.getFk())
                            )
                        {
                            key = fk;
                            v.add(key);
                        }
                    }
                    DbForeignKeyColumn column = new DbForeignKeyColumn();
                    column.setPkCol(columnNames.getString("PKCOLUMN_NAME"));
                    column.setFkCol(columnNames.getString("FKCOLUMN_NAME"));
                    column.setSeq(DbUtils.getInteger(columnNames, "KEY_SEQ"));

                    key.getColumns().add(column);

/*
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
*/
                }
                columnNames.close();
                columnNames = null;

        }
        catch (Exception e) {
            throw new DbRevisionException(e);
        }
        return v;
    }

    private static DbForeignKey processResultSetForForeignKey(ResultSet columnNames) throws SQLException {
        DbForeignKey key = new DbForeignKey();
        key.setPkSchema(columnNames.getString("PKTABLE_SCHEM"));
        key.setPkTable(columnNames.getString("PKTABLE_NAME"));
        key.setPk(columnNames.getString("PK_NAME"));

        key.setFkSchema(columnNames.getString("FKTABLE_SCHEM"));
        key.setFkTable(columnNames.getString("FKTABLE_NAME"));
        key.setFk(columnNames.getString("FK_NAME"));

        key.setURule(decodeUpdateRule(columnNames));
        key.setDRule(decodeDeleteRule(columnNames));
        key.setDefer(decodeDeferrabilityRule(columnNames));
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
        index.setC(columnNames.getString("TABLE_CAT"));
        index.setS(columnNames.getString("TABLE_SCHEM"));
        index.setT(columnNames.getString("TABLE_NAME"));
        index.setNonUnique(DbUtils.getBoolean(columnNames, "NON_UNIQUE", false));

        index.setIndexQualifier(columnNames.getString("INDEX_QUALIFIER"));
        index.setI(columnNames.getString("INDEX_NAME"));
        index.setType(DbUtils.getInteger(columnNames, "TYPE"));
        index.setCardinality(DbUtils.getInteger(columnNames, "CARDINALITY"));
        index.setPages(DbUtils.getInteger(columnNames, "PAGES"));
        index.setFilterCondition(columnNames.getString("FILTER_CONDITION"));

        return index;
    }

    public static DbPrimaryKey getPk(Database adapter, String schemaPattern, String tablePattern) {

        DbPrimaryKey pk=null;
        try {
            DatabaseMetaData db = adapter.getConnection().getMetaData();
            ResultSet metaData = null;
            metaData = db.getPrimaryKeys(null, schemaPattern, tablePattern);

            while (metaData.next()) {
                if (pk==null) {
                    pk = new DbPrimaryKey();
                    pk.setC(metaData.getString("TABLE_CAT"));
                    pk.setS(metaData.getString("TABLE_SCHEM"));
                    pk.setT(metaData.getString("TABLE_NAME"));
                    pk.setPk(metaData.getString("PK_NAME"));
                }
                DbPrimaryKeyColumn pkColumn = new DbPrimaryKeyColumn();

                pkColumn.setC(metaData.getString("COLUMN_NAME"));
                pkColumn.setSeq(DbUtils.getInteger(metaData, "KEY_SEQ"));

                pk.getColumns().add(pkColumn);

/*
                if (log.isDebugEnabled()) {
                    log.debug(
                        pk.getC() + "." +
                            pk.getS() + "." +
                            pk.getT() +
                            " - " +
                            pkColumn.getC() +
                            " " +
                            pkColumn.getSeq() + " " +
                            pk.getPk() + " " +
                            ""
                    );
                }
*/
            }
            metaData.close();
            metaData = null;
        }
        catch (SQLException e1) {
            throw new DbRevisionException(e1);
        }

        if (pk==null) {
            return null;
        }

        Collections.sort(pk.getColumns(), DbPkComparator.getInstance());

/*
        if (log.isDebugEnabled()) {
            if (pk.getColumns().size() > 1) {
                log.debug("Table with multicolumn PK.");

                for (DbPrimaryKeyColumn pkColumn : pk.getColumns()) {
                    log.debug(
                            pk.getC() + "." +
                                    pk.getS() + "." +
                                    pk.getT() +
                                    " - " +
                                    pkColumn.getC() +
                                    " " +
                                    pkColumn.getSeq() + " " +
                                    pk.getPk() + " " +
                                    ""
                    );
                }
            }
        }
*/
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

        if (StringUtils.isBlank(fk.getFk())) {
            throw new DbRevisionException("Foreign key name is null");
        }

        String sql =
            "ALTER TABLE " + fk.getFkTable() + " " +
                "ADD CONSTRAINT " + fk.getFk() + " FOREIGN KEY (";

        Collections.sort(fk.getColumns(), DbFkComparator.getInstance());
        boolean isFirst = true;
        for (DbForeignKeyColumn foreignKeyColumn : fk.getColumns()) {
            if (!isFirst) {
                sql += ",";
            }
            else {
                isFirst = false;
            }

            sql += foreignKeyColumn.getFkCol();
        }
        sql += ")\nREFERENCES " + fk.getPkTable() + " (";

        isFirst = true;
        for (DbForeignKeyColumn foreignKeyColumn : fk.getColumns()) {
            if (!isFirst) {
                sql += ",";
            }
            else {
                isFirst = false;
            }

            sql += foreignKeyColumn.getPkCol();
        }
        sql += ") ";
        switch (fk.getDRule().getRuleType()) {
            case DatabaseMetaData.importedKeyRestrict:
                sql += adapter.getOnDeleteSetNull();
                break;
            case DatabaseMetaData.importedKeyCascade:
                sql += "ON DELETE CASCADE ";
                break;

            default:
                throw new IllegalArgumentException(" imported keys delete rule '" +
                    fk.getDRule().getRuleName() + "' not supported");
        }
        switch (fk.getDefer().getRuleType()) {
            case DatabaseMetaData.importedKeyNotDeferrable:
                break;
            case DatabaseMetaData.importedKeyInitiallyDeferred:
                sql += " DEFERRABLE INITIALLY DEFERRED";
                break;

            default:
                throw new IllegalArgumentException(" imported keys deferred rule '" +
                    fk.getDefer().getRuleName() + "' not supported");
        }

        PreparedStatement ps = null;
        try {
            ps = adapter.getConnection().prepareStatement(sql);
            ps.executeUpdate();
        }
        catch (SQLException exc) {
            String es = "Error. ";
            if (!adapter.testExceptionTableExists(exc)) {
                es += ("sql " + sql);
                es += ("code " + exc.getErrorCode());
                es += ("state " + exc.getSQLState());
                es += ("message " + exc.getMessage());
                es += ("string " + exc.toString());
            }
            throw new DbRevisionException(es, exc);
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

            columnNames = db.getIndexInfo(null, schemaName, tableName, false, false);

            DbIndex key=null;
            while (columnNames.next()) {
                final String columnName = columnNames.getString("COLUMN_NAME");
                if (key==null) {
                    key = processResultSetForIndex(columnNames);
                    v.add(key);
                }
                else {

                    DbIndex fk = processResultSetForIndex(columnNames);
                    if (adapter.getFamily()== Database.Family.DB2 && columnName==null) {
                        break;
                    }
                    if (
                        !StringUtils.equals(key.getC(), fk.getC()) ||
                            !StringUtils.equals(key.getS(), fk.getS()) ||
                            !StringUtils.equals(key.getT(), fk.getT()) ||
                            !StringUtils.equals(key.getI(), fk.getI())
                        )
                    {
                        key = fk;
                        v.add(key);
                    }
                }

                if (columnName==null) {
                    continue;
                }
                DbIndexColumn column = new DbIndexColumn();
                column.setC(columnName);
                column.setSeq(DbUtils.getInteger(columnNames, "ORDINAL_POSITION"));
                String asc = columnNames.getString("ASC_OR_DESC");
                Boolean isAsc = null;
                if (StringUtils.equals(asc, "A")) {
                    isAsc = true;
                }
                else if (StringUtils.equals(asc, "D")) {
                    isAsc = false;
                }
                column.setAsc(isAsc);

                key.getColumns().add(column);


/*
                if (log.isDebugEnabled()) {
                    log.debug(
                        key.getC() + " - " +
                            key.getS() + "." +
                            key.getT() +
                            " - " +
                            column.getC() +
                            "; " +
                            column.getSeq() + " " +
                            column.isAsc() + " "
                    );
                }
*/
            }
            columnNames.close();
            columnNames = null;
        }
        catch (Exception e) {
            throw new DbRevisionException("Error in getIndexes()", e);
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

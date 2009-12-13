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

package org.riverock.dbrevision.manager.dao;

import org.riverock.dbrevision.manager.RevisionBean;
import org.riverock.dbrevision.db.Database;
import org.riverock.dbrevision.db.DatabaseStructureManager;
import org.riverock.dbrevision.schema.db.v3.DbTable;
import org.riverock.dbrevision.schema.db.v3.DbField;
import org.riverock.dbrevision.Constants;
import org.riverock.dbrevision.utils.DbUtils;
import org.riverock.dbrevision.exception.DbRevisionException;

import java.util.ArrayList;
import java.util.List;
import java.sql.DatabaseMetaData;
import java.sql.Types;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;

/**
 * User: SergeMaslyukov
 * Date: 29.07.2007
 * Time: 13:13:29
 */
public class ManagerDaoImpl implements ManagerDao {
    private static final String SELECT_REVISION_SQL = "select MODULE_NAME, CURRENT_VERSION, LAST_PATCH from "+ Constants.DB_REVISION_TABLE_NAME;

    public List<RevisionBean> getRevisions(Database database) {
        checkDbRevisionTableExist(database);
        List<RevisionBean> list = new ArrayList<RevisionBean>();
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = database.getConnection().prepareStatement(SELECT_REVISION_SQL);
            rs = ps.executeQuery();
            while (rs.next()) {
                RevisionBean bean = new RevisionBean();
                bean.setModuleName(rs.getString("MODULE_NAME"));
                bean.setCurrentVerson(rs.getString("CURRENT_VERSION"));
                bean.setLastPatch(rs.getString("LAST_PATCH"));
                if (rs.wasNull()) {
                    bean.setLastPatch(null);
                }
                list.add(bean);
            }
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        }
        finally {
            DbUtils.close(rs, ps);
            //noinspection UnusedAssignment
            rs = null;
            //noinspection UnusedAssignment
            ps = null;
        }
        return list;
    }

    public RevisionBean getRevision(Database database, String moduleName, String versionName) {
        checkDbRevisionTableExist(database);
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = database.getConnection().prepareStatement(
                "select MODULE_NAME, CURRENT_VERSION, LAST_PATCH from "+ Constants.DB_REVISION_TABLE_NAME + ' ' +
                    "where MODULE_NAME=? and CURRENT_VERSION=?"
            );
            ps.setString(1, moduleName);
            ps.setString(2, versionName);
            rs = ps.executeQuery();
            RevisionBean revision=null;
            if (rs.next()) {
                revision = new RevisionBean();
                revision.setModuleName(rs.getString("MODULE_NAME"));
                revision.setCurrentVerson(rs.getString("CURRENT_VERSION"));
                revision.setLastPatch(rs.getString("LAST_PATCH"));
                if (rs.wasNull()) {
                    revision.setLastPatch(null);
                }
            }
            return revision;
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        }
        finally {
            DbUtils.close(rs, ps);
            //noinspection UnusedAssignment
            rs = null;
            //noinspection UnusedAssignment
            ps = null;
        }
    }

    public void checkDbRevisionTableExist(Database database) {
        try {
            DatabaseMetaData metaData = database.getConnection().getMetaData();
            String dbSchema = database.getDefaultSchemaName(metaData);
            List<DbTable> list = DatabaseStructureManager.getTableList(database, dbSchema, Constants.DB_REVISION_TABLE_NAME);
            if (list.isEmpty()) {
                DbTable table = new DbTable();
                table.setT(Constants.DB_REVISION_TABLE_NAME);
                table.setS(null);

                table.getFields().add(getField("MODULE_NAME", Types.VARCHAR, 50, 0, 0));
                table.getFields().add(getField("CURRENT_VERSION", Types.VARCHAR, 50, 0, 0));
                table.getFields().add(getField("LAST_PATCH", Types.VARCHAR, 50, 0, 1));

                // TODO add unique index on module_name column
/*
                DbForeignKey uniqueNameIdx = new DbForeignKey();
                uniqueNameIdx.setPk();

                table.getForeignKeys().add()
*/
                database.createTable(table);
            }
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        }
    }

    public void makrCurrentVersion(Database database, String moduleName, String versionName, String patchName) {
        checkDbRevisionTableExist(database);
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            Connection conn = database.getConnection();
            DbUtils.runSQL(
                conn,
                "delete from "+Constants.DB_REVISION_TABLE_NAME + " where MODULE_NAME=?",
                new Object[]{moduleName},
                new int[] {Types.VARCHAR}
            );

            ps = conn.prepareStatement(
                "insert into " + Constants.DB_REVISION_TABLE_NAME+ " " +
                    "(MODULE_NAME, CURRENT_VERSION, LAST_PATCH)" +
                    "values" +
                    "(?, ?, ?)"
            );
            ps.setString(1, moduleName);
            ps.setString(2, versionName);
            if (patchName!=null) {
                ps.setString(3, patchName);
            }
            else {
                ps.setNull(3, Types.VARCHAR);
            }
            ps.executeUpdate();

            conn.commit();
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        }
        finally {
            DbUtils.close(rs, ps);
            //noinspection UnusedAssignment
            rs = null;
            //noinspection UnusedAssignment
            ps = null;
        }
    }

    public RevisionBean getRevision(Database database, String moduleName) {
        checkDbRevisionTableExist(database);
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = database.getConnection().prepareStatement(
                "select MODULE_NAME, CURRENT_VERSION, LAST_PATCH from "+ Constants.DB_REVISION_TABLE_NAME + ' ' +
                    "where MODULE_NAME=?"
            );
            ps.setString(1, moduleName);
            rs = ps.executeQuery();
            RevisionBean revision=null;
            if (rs.next()) {
                revision = new RevisionBean();
                revision.setModuleName(rs.getString("MODULE_NAME"));
                revision.setCurrentVerson(rs.getString("CURRENT_VERSION"));
                revision.setLastPatch(rs.getString("LAST_PATCH"));
                if (rs.wasNull()) {
                    revision.setLastPatch(null);
                }
            }
            return revision;
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        }
        finally {
            DbUtils.close(rs, ps);
            //noinspection UnusedAssignment
            rs = null;
            //noinspection UnusedAssignment
            ps = null;
        }
    }

    private static DbField getField(String name, int type, int size, int decimalDigit, int nullable) {
        DbField field;
        field = new DbField();
        field.setName(name);
        field.setType(type);
        field.setSize(size);
        field.setDigit(decimalDigit);
        field.setNullable(nullable);
        return field;
    }
}

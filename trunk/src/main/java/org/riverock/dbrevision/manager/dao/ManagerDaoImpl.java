package org.riverock.dbrevision.manager.dao;

import org.riverock.dbrevision.manager.RevisionBean;
import org.riverock.dbrevision.db.Database;
import org.riverock.dbrevision.db.DatabaseStructureManager;
import org.riverock.dbrevision.annotation.schema.db.DbTable;
import org.riverock.dbrevision.annotation.schema.db.DbField;
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

    public List<RevisionBean> getRevisions(Database adapter) {
        checkDbRevisionTableExist(adapter);
        List<RevisionBean> list = new ArrayList<RevisionBean>();
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = adapter.getConnection().prepareStatement(SELECT_REVISION_SQL);
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

    public RevisionBean getRevision(Database adapter, String moduleName, String versionName) {
        checkDbRevisionTableExist(adapter);
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = adapter.getConnection().prepareStatement(
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

    public void checkDbRevisionTableExist(Database adapter) {
        try {
            DatabaseMetaData metaData = adapter.getConnection().getMetaData();
            String dbSchema = metaData.getUserName();
            List<DbTable> list = DatabaseStructureManager.getTableList(adapter.getConnection(), dbSchema, Constants.DB_REVISION_TABLE_NAME);
            if (list.isEmpty()) {
                DbTable table = new DbTable();
                table.setName(Constants.DB_REVISION_TABLE_NAME);
                table.setSchema(null);

                table.getFields().add(getField("MODULE_NAME", Types.VARCHAR, 50, 0, 0));
                table.getFields().add(getField("CURRENT_VERSION", Types.VARCHAR, 50, 0, 0));
                table.getFields().add(getField("LAST_PATCH", Types.VARCHAR, 50, 0, 1));

                // TODO add unique index on module_name column
/*
                DbForeignKey uniqueNameIdx = new DbForeignKey();
                uniqueNameIdx.setPkName();    

                table.getForeignKeys().add()
*/
                adapter.createTable(table);
            }
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        }
    }

    public void makrCurrentVersion(Database adapter, String moduleName, String versionName, String patchName) {
        checkDbRevisionTableExist(adapter);
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            Connection conn = adapter.getConnection();
            DbUtils.runSQL(
                conn,
                "delete from "+Constants.DB_REVISION_TABLE_NAME + " where MODULE_NAME=? and CURRENT_VERSION=? ",
                new Object[]{moduleName, versionName},
                new int[] {Types.VARCHAR,  Types.VARCHAR}
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

    private static DbField getField(String name, int type, int size, int decimalDigit, int nullable) {
        DbField field;
        field = new DbField();
        field.setName(name);
        field.setJavaType(type);
        field.setSize(size);
        field.setDecimalDigit(decimalDigit);
        field.setNullable(nullable);
        return field;
    }
}

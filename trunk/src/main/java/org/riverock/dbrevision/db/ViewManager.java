package org.riverock.dbrevision.db;

import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.riverock.dbrevision.annotation.schema.db.DbView;
import org.riverock.dbrevision.exception.DbRevisionException;

/**
 * User: SergeMaslyukov
 * Date: 19.03.2009
 * Time: 16:24:31
 */
public class ViewManager
{
    public static String removeOracleWithReadOnly(String viewText) {
        int idx = viewText.toUpperCase().indexOf("WITH");
        if (idx!=-1) {
            String sr = viewText.substring(idx+4).trim().toUpperCase();
            if  (sr.startsWith("READ")) {
                String so = sr.substring(4).trim();
                if (so.startsWith("ONLY")) {
                    int idxEnd = viewText.toUpperCase().indexOf("ONLY", idx);
                    viewText = viewText.substring(0, idx) + viewText.substring(idxEnd+4);
                }
            }
        }
        return viewText;
    }

    public static List<DbView> getViewList(final Database database, final String schemaPattern, final String viewPattern) {
        String[] types = {"VIEW"};

        ResultSet meta = null;
        List<DbView> v = new ArrayList<DbView>();
        try {
            DatabaseMetaData dbMeta = database.getConnection().getMetaData();

            meta = dbMeta.getTables(
                null,
                schemaPattern,
                viewPattern,
                types
            );

            while (meta.next()) {

                DbView table = new DbView();

                table.setSchema(meta.getString("TABLE_SCHEM"));
                table.setName(meta.getString("TABLE_NAME"));
                table.setType(meta.getString("TABLE_TYPE"));
                table.setRemark(meta.getString("REMARKS"));

                v.add(table);
            }
        }
        catch (Exception e) {
            throw new DbRevisionException("Error get list of view", e);
        }
        finally {
            try {
                if (meta!=null) {
                    meta.close();
                }
            }
            catch (SQLException e) {
                //
            }
        }
        return v;
    }

}

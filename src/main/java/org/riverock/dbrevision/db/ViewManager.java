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

import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.riverock.dbrevision.schema.db.DbView;
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

                table.setS(meta.getString("TABLE_SCHEM"));
                table.setT(meta.getString("TABLE_NAME"));
                table.setR(meta.getString("REMARKS"));

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

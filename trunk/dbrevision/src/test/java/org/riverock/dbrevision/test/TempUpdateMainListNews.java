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
package org.riverock.dbrevision.test;

import org.riverock.dbrevision.db.DatabaseAdapter;
import org.riverock.dbrevision.db.DatabaseManager;
import org.riverock.dbrevision.utils.DbUtils;
import org.riverock.dbrevision.utils.StartupApplication;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * User: Admin
 * Date: Mar 3, 2003
 * Time: 6:43:34 PM
 * <p/>
 * $Id: TempUpdateMainListNews.java 1141 2006-12-14 14:43:29Z serg_main $
 */
public class TempUpdateMainListNews {
    public static void main(String s[])
        throws Exception {
        StartupApplication.init();
        DatabaseAdapter db_ = null;
//        db_ = DatabaseAdapter.getInstance( "HSQLDB" );

        String sql_ =
            "select ID_NEWS from WM_NEWS_LIST";

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = db_.getConnection().prepareStatement(sql_);

            rs = ps.executeQuery();

            while (rs.next()) {
                Long id = DbUtils.getLong(rs, "ID_NEWS");


                String sql_temp_ =
                    "update WM_NEWS_LIST " +
                        "set ID_SITE_SUPPORT_LANGUAGE = " +
                        "( select ID_SITE_SUPPORT_LANGUAGE " +
                        "from WM_PORTAL_SITE_LANGUAGE b " +
                        "where ID_SITE = b.ID_SITE and " +
                        "ID_LANGUAGE =b.ID_LANGUAGE " +
                        ") where ID_NEWS=?";

                PreparedStatement ps1 = null;
                try {
                    ps1 = db_.getConnection().prepareStatement(sql_temp_);
                    ps1.setLong(1, id.longValue());
                    ps1.executeUpdate();
                }
                catch (Exception e) {

                }
                finally {
                    DatabaseManager.close(ps1);
                    ps1 = null;
                }

            }
        }
        finally {
            DatabaseManager.close(rs, ps);
            rs = null;
            ps = null;
        }

        db_.getConnection().commit();

    }
}

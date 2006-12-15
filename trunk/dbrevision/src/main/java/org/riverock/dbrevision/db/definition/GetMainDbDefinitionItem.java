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
package org.riverock.dbrevision.db.definition;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.riverock.dbrevision.db.DatabaseAdapter;
import org.riverock.dbrevision.db.DatabaseManager;

@SuppressWarnings({"UnusedAssignment"})
public class GetMainDbDefinitionItem {

    public MainDbDefinitionItem item = new MainDbDefinitionItem();

    public boolean isFound = false;

    public GetMainDbDefinitionItem() {
    }

    public void copyItem(MainDbDefinitionItem target) {
        copyItem(this.item, target);
    }

    public static void copyItem(MainDbDefinitionItem source, MainDbDefinitionItem target) {
        if (source == null || target == null)
            return;

        target.setIdDbDefinition(
            source.getIdDbDefinition()
        );
        target.setNameDefinition(
            source.getNameDefinition()
        );
        target.setAplayDate(
            source.getAplayDate()
        );
    }

    private static String sql_ = "select * from WM_DB_DEFINITION where ID_DB_DEFINITION=?";

    public GetMainDbDefinitionItem(DatabaseAdapter db_, Long id) throws Exception {
        this(db_, id, sql_);
    }

    public GetMainDbDefinitionItem(DatabaseAdapter db_, Long id, String sqlString)
        throws Exception {

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = db_.getConnection().prepareStatement(sqlString);
            ps.setLong(1, id);

            rs = ps.executeQuery();
            if (rs.next()) {
                isFound = true;

                long tempLong0 = rs.getLong("ID_DB_DEFINITION");
                if (!rs.wasNull())
                    item.setIdDbDefinition(tempLong0);
                String tempString1 = rs.getString("NAME_DEFINITION");
                if (!rs.wasNull())
                    item.setNameDefinition(tempString1);
                java.sql.Timestamp tempTimestamp2 = rs.getTimestamp("APLAY_DATE");
                if (!rs.wasNull())
                    item.setAplayDate(tempTimestamp2);
            }
        }
        catch (Exception e) {
            throw e;
        }
        catch (Error err) {
            throw err;
        }
        finally {
            DatabaseManager.close(rs, ps);
            rs = null;
            ps = null;
        }

    }
}

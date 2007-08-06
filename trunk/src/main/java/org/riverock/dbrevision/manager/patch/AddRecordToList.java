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
package org.riverock.dbrevision.manager.patch;

import java.sql.PreparedStatement;

import org.apache.log4j.Logger;

import org.riverock.dbrevision.annotation.schema.db.Action;
import org.riverock.dbrevision.db.Database;
import org.riverock.dbrevision.exception.DbRevisionException;
import org.riverock.dbrevision.utils.DbUtils;

/**
 * User: Admin
 * Date: May 20, 2003
 * Time: 9:49:58 PM
 * <p/>
 * $Id: AddRecordToList.java 1141 2006-12-14 14:43:29Z serg_main $
 */
@SuppressWarnings({"UnusedAssignment"})
public class AddRecordToList implements PatchAction {
    private static Logger log = Logger.getLogger(AddRecordToList.class);

    public PatchStatus process(Database adapter, Action action) {

        PreparedStatement ps = null;
        try {
            if (log.isDebugEnabled())
                log.debug("db connect - " + adapter.getClass().getName());

            String seqName = PatchService.getString(action, "sequence_name", null);
            if (seqName == null) {
                String errorString = "Name of sequnce not found";
                log.error(errorString);
                throw new Exception(errorString);
            }

            String tableName = PatchService.getString(action, "name_table", null);
            if (tableName == null) {
                String errorString = "Name of table not found";
                log.error(errorString);
                throw new Exception(errorString);
            }

            String columnName = PatchService.getString(action, "name_pk_field", null);
            if (columnName == null) {
                String errorString = "Name of column not found";
                log.error(errorString);
                throw new Exception(errorString);
            }

            String valueColumnName = PatchService.getString(action, "name_value_field", null);
            if (valueColumnName == null) {
                String errorString = "Name of valueColumnName not found";
                log.error(errorString);
                throw new Exception(errorString);
            }

            String insertValue = PatchService.getString(action, "insert_value", null);
            if (insertValue == null) {
                String errorString = "Name of insertValue not found";
                log.error(errorString);
                throw new Exception(errorString);
            }

            String sql =
                "insert into " + tableName + " " +
                    "(" + columnName + "," + valueColumnName + ")" +
                    "values" +
                    "(?,?)";

            if (log.isDebugEnabled()) {
                log.debug(sql);
                log.debug("value " + insertValue);
            }

            ps = adapter.getConnection().prepareStatement(sql);
            ps.setLong(1, -1);
            ps.setString(2, insertValue);

            ps.executeUpdate();

        }
        catch (Exception e) {
            log.error("Error insert value", e);
            throw new DbRevisionException(e);
        }
        finally {
            DbUtils.close(ps);
            ps = null;
        }
        return null;
    }
}

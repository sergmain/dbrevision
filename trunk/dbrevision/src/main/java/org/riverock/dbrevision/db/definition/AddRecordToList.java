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

import org.apache.log4j.Logger;

import org.riverock.dbrevision.db.DatabaseAdapter;
import org.riverock.dbrevision.db.DatabaseManager;
import org.riverock.dbrevision.annotation.schema.db.DefinitionActionDataList;
import org.riverock.dbrevision.annotation.schema.db.CustomSequence;

/**
 * User: Admin
 * Date: May 20, 2003
 * Time: 9:49:58 PM
 * <p/>
 * $Id: AddRecordToList.java 1141 2006-12-14 14:43:29Z serg_main $
 */
@SuppressWarnings({"UnusedAssignment"})
public class AddRecordToList implements DefinitionProcessingInterface {
    private static Logger log = Logger.getLogger(AddRecordToList.class);

    public AddRecordToList() {
    }

    public void processAction(DatabaseAdapter db_, DefinitionActionDataList parameters) throws Exception {
        PreparedStatement ps = null;
        try {
            if (log.isDebugEnabled())
                log.debug("db connect - " + db_.getClass().getName());

            String seqName = DefinitionService.getString(parameters, "sequence_name", null);
            if (seqName == null) {
                String errorString = "Name of sequnce not found";
                log.error(errorString);
                throw new Exception(errorString);
            }

            String tableName = DefinitionService.getString(parameters, "name_table", null);
            if (tableName == null) {
                String errorString = "Name of table not found";
                log.error(errorString);
                throw new Exception(errorString);
            }

            String columnName = DefinitionService.getString(parameters, "name_pk_field", null);
            if (columnName == null) {
                String errorString = "Name of column not found";
                log.error(errorString);
                throw new Exception(errorString);
            }

            CustomSequence seqSite = new CustomSequence();
            seqSite.setSequenceName(seqName);
            seqSite.setTableName(tableName);
            seqSite.setColumnName(columnName);
            long seqValue = db_.getSequenceNextValue(seqSite);

            String valueColumnName = DefinitionService.getString(parameters, "name_value_field", null);
            if (valueColumnName == null) {
                String errorString = "Name of valueColumnName not found";
                log.error(errorString);
                throw new Exception(errorString);
            }

            String insertValue = DefinitionService.getString(parameters, "insert_value", null);
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
                log.debug("pk " + seqValue);
                log.debug("value " + insertValue);
            }

            ps = db_.getConnection().prepareStatement(sql);
            ps.setLong(1, seqValue);
            ps.setString(2, insertValue);

            ps.executeUpdate();

            db_.getConnection().commit();
        }
        catch (Exception e) {
            try {
                if (db_ != null) {
                    db_.getConnection().rollback();
                }
            }
            catch (Exception e1) {
                // catch rollback error
            }
            log.error("Error insert value", e);
            throw e;
        }
        finally {
            DatabaseManager.close(ps);
            ps = null;
        }
    }
}

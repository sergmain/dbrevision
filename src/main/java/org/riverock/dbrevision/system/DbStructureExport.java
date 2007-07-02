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
package org.riverock.dbrevision.system;

import java.io.OutputStream;

import org.riverock.dbrevision.annotation.schema.db.DbSchema;
import org.riverock.dbrevision.annotation.schema.db.DbTable;
import org.riverock.dbrevision.db.DatabaseAdapter;
import org.riverock.dbrevision.db.DatabaseManager;
import org.riverock.dbrevision.db.DatabaseStructureManager;
import org.riverock.dbrevision.exception.DbRevisionException;
import org.riverock.dbrevision.utils.Utils;

/**
 * Export data from DB to XML file
 * <p/>
 * Author: mill
 * Date: Nov 28, 2002
 * Time: 3:10:19 PM
 * <p/>
 * $Id: DbStructureExport.java 1141 2006-12-14 14:43:29Z serg_main $
 */
public class DbStructureExport {

    public static void export(DatabaseAdapter db, OutputStream outputStream, boolean isData) {
        try {
            DbSchema schema = DatabaseManager.getDbStructure(db);
            for (DbTable table : schema.getTables()) {
                if (isData) {
                    table.setData(DatabaseStructureManager.getDataTable(db, table));
                }
            }
            Utils.writeObjectAsXml(schema, outputStream, "utf-8");
        } catch (Exception e) {
            throw new DbRevisionException(e);
        }
    }
}

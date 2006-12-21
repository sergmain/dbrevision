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

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;

import org.riverock.dbrevision.annotation.schema.db.DbSchema;
import org.riverock.dbrevision.annotation.schema.db.DbTable;
import org.riverock.dbrevision.offline.DbRevisionConfig;
import org.riverock.dbrevision.offline.StartupApplication;
//import org.riverock.dbrevision.config.PropertiesProvider;
import org.riverock.dbrevision.db.DatabaseAdapter;
import org.riverock.dbrevision.db.DatabaseManager;
import org.riverock.dbrevision.db.DatabaseStructureManager;
import org.riverock.dbrevision.db.factory.ORAconnect;
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

    private static final boolean IS_EXTRACT_DATA = true;

    public static void main(String args[]) throws Exception {
        StartupApplication.init();
        System.out.println("DebugDir: " + DbRevisionConfig.getGenericDebugDir());
        Connection conn = null;
        DatabaseAdapter db = new ORAconnect(conn);
        File fileWithBigTable = new File(args[3]);
        if (!fileWithBigTable.exists()) {
            System.out.println("File with definition for big tables not exist, file: " + fileWithBigTable.getAbsolutePath());
        }
        export(db, new FileOutputStream(DbRevisionConfig.getGenericDebugDir() + "webmill-schema.xml"), fileWithBigTable, IS_EXTRACT_DATA);
    }

    public static void export(DatabaseAdapter db, OutputStream outputStream, File fileWithBigTable, boolean isData) {

        DbSchema schema = DatabaseManager.getDbStructure(db);

        List<DbTable> tables = new ArrayList<DbTable>();
        for (DbTable table : schema.getTables()) {
            if (
                table.getName().toUpperCase().startsWith("A_") ||
                    table.getName().toUpperCase().startsWith("BIN$") ||
                    table.getName().toUpperCase().startsWith("CIH_") ||
                    table.getName().toUpperCase().startsWith("TB_") ||
                    table.getName().toUpperCase().startsWith("HAM_")
                ) {
                continue;
            }
            tables.add(table);
            System.out.println("Table - " + table.getName());

            table.getFields().addAll(DatabaseStructureManager.getFieldsList(db.getConnection(), table.getSchema(), table.getName(), dbOra.getFamily()));
            table.setPrimaryKey(DatabaseStructureManager.getPrimaryKey(db.getConnection(), table.getSchema(), table.getName()));
            table.getImportedKeys().addAll(DatabaseStructureManager.getImportedKeys(db.getConnection(), table.getSchema(), table.getName()));

            boolean isSkipData = false;
            if (table.getName().toUpperCase().startsWith("WM_FORUM") ||
                table.getName().toUpperCase().startsWith("WM_PORTLET_FAQ") ||
                table.getName().toUpperCase().startsWith("WM_JOB")
                ) {
                isSkipData = true;
            }

            if (isData && !isSkipData) {
                table.setData(DatabaseStructureManager.getDataTable(db.getConnection(), table, db.getFamily()));
            }
        }
        DbSchema schemaBigTable = Utils.getObjectFromXml(DbSchema.class, new FileInputStream(fileWithBigTable));
        schema.getBigTextTable().addAll(schemaBigTable.getBigTextTable());

        Utils.writeObjectAsXml(schema, outputStream, "utf-8");
    }
}

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
import java.util.ArrayList;
import java.util.List;

import org.riverock.dbrevision.annotation.schema.db.DbSchema;
import org.riverock.dbrevision.annotation.schema.db.DbTable;
import org.riverock.dbrevision.annotation.schema.db.DbView;
import org.riverock.dbrevision.config.GenericConfig;
import org.riverock.dbrevision.config.PropertiesProvider;
import org.riverock.dbrevision.db.DatabaseAdapter;
import org.riverock.dbrevision.db.DatabaseManager;
import org.riverock.dbrevision.db.DatabaseStructureManager;
import org.riverock.dbrevision.utils.Utils;
import org.riverock.dbrevision.utils.StartupApplication;

/**
 * Author: mill
 * Date: Nov 28, 2002
 * Time: 3:10:19 PM
 *
 * $Id: DbStructureExport.java 1141 2006-12-14 14:43:29Z serg_main $
 */
/**
 * Export data from DB to XML file
 */
public class DbStructureExport {

    private static final boolean IS_EXTRACT_DATA = true;

    public static void main(String args[])
        throws Exception
    {
        StartupApplication.init();
        System.out.println("GenericConfig.getGenericDebugDir() = " + GenericConfig.getGenericDebugDir());
        export(GenericConfig.getGenericDebugDir()+"webmill-schema.xml", IS_EXTRACT_DATA);
    }

    public static void export(String fileName, boolean isData)
        throws Exception
    {


        DatabaseAdapter dbOra = null;
//        dbOra = DatabaseAdapter.getInstance("MYSQL");
//        DatabaseAdapter dbOra = DatabaseAdapter.getInstance(false, "HSQLDB");
//        DatabaseAdapter dbOra = DatabaseAdapter.getInstance(false, "ORACLE_TEST");
//        DatabaseAdapter dbOra = DatabaseAdapter.getInstance(false, "ORACLE");
//        dbOra = DatabaseAdapter.getInstance( "MYSQL_CONTEST");
//        DatabaseAdapter dbOra = DatabaseAdapter.getInstance(false, "ORACLE-DART");
//        DatabaseAdapter db_ = DatabaseAdapter.getInstance(false, "IBM-DB2");
//        DatabaseAdapter db_ = DatabaseAdapter.getInstance(false, "ORACLE_AAA");
//        DatabaseAdapter dbOra = DatabaseAdapter.getInstance(false, "MSSQL-JTDS");

/*
        DatabaseManager.runSQL( dbOra, "delete from WM_PORTAL_ACCESS_STAT", null, null);
        DatabaseManager.runSQL( dbOra, "delete from WM_PORTAL_ACCESS_URL", null, null);
        DatabaseManager.runSQL( dbOra, "delete from WM_PORTAL_ACCESS_USERAGENT", null, null);
        DatabaseManager.runSQL( dbOra, "delete from wm_price_relate_user_order", null, null);
        DatabaseManager.runSQL( dbOra, "delete from wm_price_order", null, null);
        DatabaseManager.runSQL( dbOra, "delete from WM_PRICE_IMPORT_TABLE", null, null);
        DatabaseManager.runSQL( dbOra, "delete from WM_AUTH_USER where id_user in (select id_user from WM_LIST_USER where is_deleted=1)", null, null);
        DatabaseManager.runSQL( dbOra, "delete from WM_LIST_USER_METADATA where id_user in (select id_user from WM_LIST_USER where is_deleted=1)", null, null);
        DatabaseManager.runSQL( dbOra, "delete from WM_LIST_USER where is_deleted=1", null, null);
*/
//        DatabaseManager.runSQL( dbOra, "delete from WM_PRICE_USER_DISCOUNT where id_user in (select id_user from WM_LIST_USER where is_deleted=1)", null, null);


        dbOra.getConnection().commit();

        DbSchema schema = DatabaseManager.getDbStructure( dbOra );

        List<DbTable> tables = new ArrayList<DbTable>();
        for (DbTable table : schema.getTables()) {
            if (
                table.getName().toUpperCase().startsWith("A_") ||
                table.getName().toUpperCase().startsWith("BIN$") ||
                table.getName().toUpperCase().startsWith("CIH_") ||
                table.getName().toUpperCase().startsWith("TB_") ||
                table.getName().toUpperCase().startsWith("HAM_")
            )
            {
                continue;
            }
            tables.add(table);
            System.out.println( "Table - " + table.getName() );

            table.getFields().addAll(DatabaseStructureManager.getFieldsList(dbOra.getConnection(), table.getSchema(), table.getName(), dbOra.getFamily()));
            table.setPrimaryKey(DatabaseStructureManager.getPrimaryKey(dbOra.getConnection(), table.getSchema(), table.getName()));
            table.getImportedKeys().addAll(DatabaseStructureManager.getImportedKeys(dbOra.getConnection(), table.getSchema(), table.getName()));

            boolean isSkipData = false;
            if ( table.getName().toUpperCase().startsWith("WM_FORUM") ||
                table.getName().toUpperCase().startsWith("WM_PORTLET_FAQ") ||
                table.getName().toUpperCase().startsWith("WM_JOB")
            ) {
                isSkipData = true;
            }

            if (isData && !isSkipData)
                table.setData(DatabaseStructureManager.getDataTable(dbOra.getConnection(), table, dbOra.getFamily()));
        }

        String fileNameBigText =
            PropertiesProvider.getConfigPath()+
            File.separatorChar+"data-definition" +
            File.separatorChar+"data" +
            File.separatorChar+"big-text-table-def.xml";
        
        fileNameBigText =
            "\\sandbox\\riverock\\trunk\\riverock-webmill-db\\xml\\webmill-schema.xml";

        FileInputStream stream = new FileInputStream(fileNameBigText);
        DbSchema schemaBigTable = Utils.getObjectFromXml(DbSchema.class, stream);

        schema.getBigTextTable().addAll( schemaBigTable.getBigTextTable() );

        List<DbView> views = new ArrayList<DbView>();
        for (DbView view : schema.getViews()) {
            if (!view.getName().toUpperCase().startsWith("F_D_") &&
                !view.getName().toUpperCase().startsWith("F_DEL_") &&
                !view.getName().toUpperCase().startsWith("FOR_DEL_") &&
                !view.getName().toUpperCase().startsWith("FOR_D_")
            )
                views.add( view );
        }

        schema.getViews().addAll( views );

        System.out.println("Marshal data to file "+fileName);
        Utils.writeToFile(schema, fileName);
    }
}

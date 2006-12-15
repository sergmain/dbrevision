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
import org.riverock.dbrevision.annotation.schema.db.DbSchema;
import org.riverock.dbrevision.annotation.schema.db.DbTable;
import org.riverock.dbrevision.annotation.schema.db.DbPrimaryKey;
import org.riverock.dbrevision.utils.StartupApplication;

/**
 * User: Admin
 * Date: Mar 3, 2003
 * Time: 6:43:34 PM
 *
 * $Id: TestDbService.java 1141 2006-12-14 14:43:29Z serg_main $
 */
public class TestDbService
{
    public static void main(String s[])
        throws Exception
    {
        StartupApplication.init();
        DatabaseAdapter db_=null;
//        db_ = DatabaseAdapter.getInstance( "MSSQL-JTDS" );
        DbSchema schema = DatabaseManager.getDbStructure(db_ );
        DatabaseAdapter dbOra=null;
//        dbOra = DatabaseAdapter.getInstance( "ORACLE" );
        DbSchema schemaOracle = DatabaseManager.getDbStructure(dbOra );

        DbTable sourceTableOracle =
            DatabaseManager.getTableFromStructure( schemaOracle, "WM_PRICE_SHOP_LIST");

        DbTable checkTable = DatabaseManager.getTableFromStructure( schema, "WM_PRICE_SHOP_LIST");
        checkTable.setData( null );

//        DbService.duplicateTable(db_, sourceTable, sourceTable.getName()+"_TEMP");

        DbPrimaryKey pk = sourceTableOracle.getPrimaryKey();
        if (pk==null)
            System.out.println("PK is null");

        System.out.println("add primary key '"+pk.getColumns().get(0).getPkName()+"'");

        DatabaseManager.addPrimaryKey(db_, checkTable, sourceTableOracle.getPrimaryKey());

//        byte[] sourceByte = XmlTools.getXml( sourceTable, null );
//        byte[] targetByte = XmlTools.getXml( targetTable, null );

    }
}

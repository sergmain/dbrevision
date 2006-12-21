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

import org.riverock.dbrevision.db.DatabaseAdapter;
import org.riverock.dbrevision.db.DatabaseManager;
import org.riverock.dbrevision.annotation.schema.db.DbSchema;
import org.riverock.dbrevision.offline.StartupApplication;

/**
 * User: SergeMaslyukov
 * Date: 29.12.2004
 * Time: 13:40:18
 * $Id: MakeWebmillStructure.java 1141 2006-12-14 14:43:29Z serg_main $
 */
public class MakeWebmillStructure {
//    private static Logger log = Logger.getLogger("org.riverock.webmill.system.MakeWebmillStructure");

    public MakeWebmillStructure(){}

    private static DbSchema makeSchema(String nameConnection, String nameOutputFiel)
        throws Exception
    {
        DatabaseAdapter db_ = null;
        try
        {
//            db_ = DatabaseAdapter.getInstance( nameConnection );
            DbSchema schema = DatabaseManager.getDbStructure(db_ );

/*
            String encoding = "UTF-8";
            String nameFile = nameOutputFiel;
            String outputSchemaFile = GenericConfig.getGenericDebugDir()+nameFile;
            System.out.println("Marshal data to file " + outputSchemaFile);

            FileOutputStream fos = new FileOutputStream( outputSchemaFile );
            Marshaller marsh = new Marshaller(new OutputStreamWriter(fos, encoding));
            marsh.setMarshalAsDocument( true );
            marsh.setEncoding( encoding );
            marsh.marshal( schema );
*/

            return schema;
        }
        finally {
//            DatabaseAdapter.close(db_);
//            db_ = null;
        }
    }

    public static void main(String args[])
        throws Exception
    {
        StartupApplication.init();

        makeSchema("MYSQL", "webmill-schema.xml");
//        makeSchema("MSSQL-JTDS", "webmill-schema-mssql.xml");

    }
}

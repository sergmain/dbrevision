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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.SQLException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.riverock.dbrevision.annotation.schema.db.DbSchema;
import org.riverock.dbrevision.annotation.schema.db.DbTable;
import org.riverock.dbrevision.annotation.schema.db.DbView;
import org.riverock.dbrevision.config.GenericConfig;
import org.riverock.dbrevision.db.DatabaseAdapter;
import org.riverock.dbrevision.db.DatabaseManager;
import org.riverock.dbrevision.db.DatabaseStructureManager;
import org.riverock.dbrevision.db.factory.ORAconnect;
import org.riverock.dbrevision.utils.StartupApplication;

/**
 * Author: mill
 * Date: Nov 28, 2002
 * Time: 3:10:19 PM
 *
 * $Id: DbStructure.java 1141 2006-12-14 14:43:29Z serg_main $
 */
public class DbStructure
{
//    private static Logger cat = Logger.getLogger("org.riverock.system.DbStructure");

    public DbStructure() {

    }

    public static void main(String args[]) throws Exception {

        StartupApplication.init();


//        DatabaseAdapter db_ = DatabaseAdapter.getInstance(false, "SAPDB_DBA");

//        DatabaseAdapter db_ = DatabaseAdapter.getInstance(false, "MSSQL-JTDS");
//        DatabaseAdapter db_ = DatabaseAdapter.getInstance(false, "HSQLDB");
//        schema.setViews( db_.getViewList( "SA", "%"));
//        schema.setTables(db_.getTableList("SA", "%"));

        DatabaseAdapter dbOra=null;
//        dbOra = DatabaseAdapter.getInstance( "ORACLE");
//        DatabaseAdapter db_ = DatabaseAdapter.getInstance(false, "IBM-DB2");

//        DatabaseAdapter db_ = DatabaseAdapter.getInstance(false, "ORACLE_AAA");
        DbSchema schema = DatabaseManager.getDbStructure(dbOra );

//        schema.setViews( db_.getViewList( "MILLENNIUM", "%"));
//        schema.setTables( db_.getTableList( "MILLENNIUM", "%"));
//        schema.setTables(db_.getTableList("MILLENNIUM", "WM_AUTH_APPLICATION"));

/*
            DatabaseMetaData db = db_.conn.getMetaData();
            ResultSet rs = db.getCrossReference( null,
                                            null,
                                            "SERV_LIST_LOGIN_SERVICE",
                                            null,
                                            null,
                                            "SERV_LIST_LOGIN_SERVICE");

            while (rs.next())
            {
                System.out.println("new record");
                ResultSetMetaData rsmd = rs.getMetaData();
                int numberOfColumns = rsmd.getColumnCount();

                for (int k=1; k<=numberOfColumns;k++)
                {
                    String name = rsmd.getColumnName(k);
                    String value = rs.getString(1);
                    System.out.println("field name - "+name+" value "+value);
                }
            }
*/

        for (DbTable table : schema.getTables()) {
            System.out.println( "Table - " + table.getName() );

            table.getFields().addAll(DatabaseStructureManager.getFieldsList(dbOra.getConnection(), table.getSchema(), table.getName(), dbOra.getFamily()));
            table.setPrimaryKey(DatabaseStructureManager.getPrimaryKey(dbOra.getConnection(), table.getSchema(), table.getName()));
            table.getImportedKeys().addAll(DatabaseStructureManager.getImportedKeys(dbOra.getConnection(), table.getSchema(), table.getName()));
            table.setData(DatabaseStructureManager.getDataTable(dbOra.getConnection(), table, dbOra.getFamily()));
        }

        String encoding = "UTF-8";
        String outputSchemaFile = GenericConfig.getGenericDebugDir()+"schema.xml";
        System.out.println("Marshal data to file");

        JAXBContext jaxbContext = JAXBContext.newInstance ( DbSchema.class.getPackage().getName() );
        FileOutputStream fos = new FileOutputStream( outputSchemaFile );
        Marshaller marshaller = jaxbContext.createMarshaller();

//        marshaller.setMarshalAsDocument( true );
//        marshaller.setEncoding( encoding );
        marshaller.marshal( schema, new OutputStreamWriter(fos, encoding) );

        System.out.println( "Unmarshal current data from file" );
        FileInputStream stream = new FileInputStream(outputSchemaFile);
        Source source = new StreamSource( stream );
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        DbSchema  millCurrSchema = unmarshaller.unmarshal(source, DbSchema.class).getValue();

        for (DbView view : millCurrSchema.getViews()) {
            System.out.println("View " + view.getName()+" text\n"+view.getText());
        }

        System.out.println("Unmarshal data from file");
        Source inSrc = new StreamSource(new FileInputStream( GenericConfig.getGenericDebugDir()+"schema-mill.xml" ));
        DbSchema millSchema = unmarshaller.unmarshal(inSrc, DbSchema.class).getValue();

        DatabaseAdapter db_=null;
//        db_ = DatabaseAdapter.getInstance( "ORACLE" );

        if (!(db_ instanceof ORAconnect))
        {
            for (DbTable table : millSchema.getTables()) {
                if (!DatabaseManager.isSkipTable(table.getName()))
                {
                    try
                    {
                        System.out.println("create table " + table.getName());
                        db_.createTable(table);
                    }
                    catch (SQLException e)
                    {
                        System.out.println("Error code "+e.getErrorCode());
                        if (db_.testExceptionTableExists(e))
                        {
                            System.out.println("table " + table.getName() + " already exists");
                            System.out.println("drop table " + table.getName());
                            db_.dropTable(table);
                            System.out.println("create table " + table.getName());
                            db_.createTable(table);
                        }
                        else
                            throw e;
                    }
                    DatabaseStructureManager.setDataTable(db_, table);
                }
                else
                    System.out.println("skip table " + table.getName());

            }
        }



        if (!(db_ instanceof ORAconnect))
        {
            for (DbView view : millSchema.getViews()) {
                DatabaseManager.createWithReplaceAllView(db_, millSchema);
                try
                {
                    System.out.println("create view " + view.getName());
                    db_.createView( view );
                }
                catch (Exception e)
                {
                    if (db_.testExceptionViewExists(e))
                    {
                        System.out.println("view " + view.getName() + " already exists");
                        System.out.println("drop view " + view.getName());
                        DatabaseStructureManager.dropView(db_, view);
                        System.out.println("create view " + view.getName());
                        try
                        {
                            db_.createView(view);
                        }
                        catch(Exception e1)
                        {
                            System.out.println("Error create view - "+e1.toString());
                        }
                    }
                    else
                    {
                        System.out.println("Error create view - "+e.toString());
//                        throw e;
                    }
                }
            }
            DatabaseManager.createWithReplaceAllView(db_, millSchema);
        }
//        DatabaseAdapter.close( db_ );
//        DatabaseAdapter.close( dbOra );
    }

}

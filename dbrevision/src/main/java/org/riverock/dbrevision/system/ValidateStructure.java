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
import java.sql.SQLException;

import org.xml.sax.InputSource;

import org.riverock.dbrevision.offline.GenericConfig;
import org.riverock.dbrevision.offline.StartupApplication;
import org.riverock.dbrevision.db.DatabaseAdapter;
import org.riverock.dbrevision.db.DatabaseManager;
import org.riverock.dbrevision.db.DatabaseStructureManager;
import org.riverock.dbrevision.annotation.schema.db.DbSchema;
import org.riverock.dbrevision.annotation.schema.db.DbView;
import org.riverock.dbrevision.annotation.schema.db.DbTable;
import org.riverock.dbrevision.annotation.schema.db.DbImportedKeyList;
import org.riverock.dbrevision.annotation.schema.db.DbField;
import org.riverock.dbrevision.utils.Utils;

/**
 * Author: mill
 * Date: Nov 28, 2002
 * Time: 3:10:19 PM
 * <p/>
 * $Id: ValidateStructure.java 1141 2006-12-14 14:43:29Z serg_main $
 */
public class ValidateStructure {

    public ValidateStructure() {
    }

    private static void processAllView(DatabaseAdapter db_, DbSchema millSchema) throws Exception {
        for (DbView view : millSchema.getViews()) {
            DatabaseManager.createWithReplaceAllView(db_, millSchema);
            try {
                System.out.println("create view " + view.getName());
                db_.createView(view);
            }
            catch (Exception e) {
                if (db_.testExceptionViewExists(e)) {
                    System.out.println("view " + view.getName() + " already exists");
                    System.out.println("drop view " + view.getName());
                    DatabaseStructureManager.dropView(db_, view);
                    System.out.println("create view " + view.getName());
                    try {
                        db_.createView(view);
                    }
                    catch (Exception e1) {
                        System.out.println("Error create view - " + e1.toString());
                    }
                }
                else {
                    System.out.println("Error create view - " + e.toString());
                }
            }
        }
        DatabaseManager.createWithReplaceAllView(db_, millSchema);
    }

    private static void processForeignKey(DatabaseAdapter db_, DbSchema millSchema) throws Exception {
        for (DbTable table : millSchema.getTables()) {
            if (!DatabaseManager.isSkipTable(table.getName())) {
                try {
                    System.out.println("Create foreign key for table " + table.getName());
                    DbImportedKeyList fk = new DbImportedKeyList();
                    fk.getKeys().addAll(table.getImportedKeys());
                    DatabaseStructureManager.createForeignKey(db_, fk);
                }
                catch (SQLException e) {
                    System.out.println("Error code " + e.getErrorCode());
                    System.out.println("Error create foreign key " + e.getMessage());
                }
            }
            else
                System.out.println("skip table " + table.getName());
        }
    }

    private static void validateStructure(DbSchema millSchema, String nameConnection) throws Exception {
        System.out.println("Connection - " + nameConnection);

        DatabaseAdapter db_=null;
//        db_ = DatabaseAdapter.getInstance(nameConnection);
        DbSchema schema = DatabaseManager.getDbStructure(db_);

        String nameFile = "test-schema.xml";
        String outputSchemaFile = GenericConfig.getGenericDebugDir() + nameFile;
        System.out.println("Marshal data to file " + nameFile);

        Utils.writeToFile(schema, outputSchemaFile);

        for (DbTable table : millSchema.getTables()) {
            if (!DatabaseManager.isSkipTable(table.getName())) {
                DbTable originTable = DatabaseManager.getTableFromStructure(schema, table.getName());
                if (!DatabaseManager.isTableExists(schema, table)) {
                    try {
                        System.out.println("Create new table " + table.getName());
                        db_.createTable(table);
                    }
                    catch (SQLException e) {
                        System.out.println("Error code " + e.getErrorCode());
                        System.out.println("Error create table " + e.getMessage());
                    }
                }
                else {
                    // check valid structure of fields
                    for (DbField field : table.getFields()) {
                        if (!DatabaseManager.isFieldExists(schema, table, field)) {
                            System.out.println("Add field '" + field.getName() + "' to table '" + table.getName() + "'");
                            db_.addColumn(table, field);
                        }

                        DbField originField =
                            DatabaseManager.getFieldFromStructure(schema, table.getName(), field.getName());

                        if (originField != null && (originField.getDefaultValue() == null && field.getDefaultValue() != null)) {
                            System.out.println("Default value of field " + table.getName() + '.' + originField.getName() +
                                " not set to " + field.getDefaultValue());
                            if (DatabaseManager.checkDefaultTimestamp(field.getDefaultValue())) {
                                System.out.println("Field recognized as default date field");
                                DatabaseStructureManager.setDefaultValueTimestamp(db_, originTable,
                                    field
                                );
                            }
                            else
                                System.out.println("Unknown default type of field");
                        }
                    }

                }
            }
            else
                System.out.println("skip table " + table.getName());
        }

/*
        // get new instance of schema
        schema = DbService.getDbStructure(db_, "MILLENNIUM");
        try
        {
            // check for correct PK
            DbTable tableForCheckPk =
                DbService.getTableFromStructure(schema, table.getName());

//                    System.out.println("orig table '"+table.getName()+"', table for check'"+tableForCheckPk.getName()+"'");
//                    System.out.println("orig table PK "+table.getPrimaryKey()+", table for check PK "+tableForCheckPk.getPrimaryKey()+"");

            if (table.getPrimaryKey()!=null &&
                table.getPrimaryKey().getColumnsCount()>0 &&
                (tableForCheckPk.getPrimaryKey()==null || tableForCheckPk.getPrimaryKey().getColumnsCount()==0))
            {
                System.out.println("Add PK to table '"+tableForCheckPk.getName()+"'");
                DbService.addPrimaryKey(db_, tableForCheckPk, table.getPrimaryKey());
            }
        }
        catch(Exception e)
        {
            System.out.println("Error add PK "+e.toString());
        }
*/

        processAllView(db_, millSchema);
        processForeignKey(db_, millSchema);

        db_.getConnection().commit();
        DbSchema schemaResult = DatabaseManager.getDbStructure(db_);
        System.out.println("Marshal data to file");
        Utils.writeToFile(schemaResult, GenericConfig.getGenericDebugDir() + "schema-result-" + nameConnection + ".xml");
//        DatabaseAdapter.close(db_);
    }

    public static void main(String args[]) throws Exception {
        long mills = System.currentTimeMillis();
        StartupApplication.init();

        System.out.println("Unmarshal data from file");
        FileInputStream stream = new FileInputStream(GenericConfig.getGenericDebugDir() + "webmill-schema.xml");
        InputSource inSrc = new InputSource( stream );
        DbSchema millSchema = Utils.getObjectFromXml(DbSchema.class, stream);

//        validateStructure(millSchema, "ORACLE_MILL_TEST");
        validateStructure(millSchema, "HSQLDB");
//        validateStructure(millSchema, "MYSQL");
        validateStructure(millSchema, "MSSQL-JTDS");

//        validateStructure(millSchema, "IBM-DB2");

        System.out.println("Done validate structure in " + (System.currentTimeMillis() - mills) + " milliseconds");

    }
}

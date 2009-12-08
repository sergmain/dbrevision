/*
 * Copyright 2007 Sergei Maslyukov at riverock.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riverock.dbrevision.system;

import java.io.FileInputStream;

import org.apache.commons.lang.StringUtils;

import org.riverock.dbrevision.schema.db.DbField;
import org.riverock.dbrevision.schema.db.DbForeignKey;
import org.riverock.dbrevision.schema.db.DbSchema;
import org.riverock.dbrevision.schema.db.DbTable;
import org.riverock.dbrevision.db.Database;
import org.riverock.dbrevision.db.DatabaseManager;
import org.riverock.dbrevision.db.DatabaseStructureManager;
import org.riverock.dbrevision.db.ConstraintManager;
import org.riverock.dbrevision.utils.Utils;

/**
 * Author: mill
 * Date: Nov 28, 2002
 * Time: 3:10:19 PM
 * <p/>
 * $Id: ValidateStructure.java 1141 2006-12-14 14:43:29Z serg_main $
 */
public class ValidateStructure {

    private static void processForeignKeys(Database adapter, DbSchema millSchema) throws Exception {
        for (DbTable table : millSchema.getTables()) {
            if (!DatabaseManager.isSkipTable(table.getT())) {
                System.out.println("Create foreign key for table " + table.getT());

                int p = 0;
                for (DbForeignKey foreignKey : table.getForeignKeys()) {
                    if (StringUtils.isBlank(foreignKey.getFk())) {
                        foreignKey.setFk(foreignKey.getFkTable() + p + "_fk");
                    }
                    ConstraintManager.createFk(adapter, foreignKey);
                }
            }
            else {
                System.out.println("skip table " + table.getT());
            }
        }
    }

    private static DbSchema validateStructure(Database adapter, DbSchema millSchema) throws Exception {
        DbSchema schema = DatabaseManager.getDbStructure(adapter);

        String nameFile = "test-schema.xml";
        System.out.println("Marshal data to file " + nameFile);

        Utils.writeToFile(schema, nameFile);

        for (DbTable table : millSchema.getTables()) {
            if (!DatabaseManager.isSkipTable(table.getT())) {
                DbTable originTable = DatabaseManager.getTableFromStructure(schema, table.getT());
                if (!DatabaseManager.isTableExists(schema, table)) {
                    System.out.println("Create new table " + table.getT());
                    adapter.createTable(table);
                }
                else {
                    // check valid structure of fields
                    for (DbField field : table.getFields()) {
                        if (!DatabaseManager.isFieldExists(schema, table, field)) {
                            System.out.println("Add field '" + field.getName() + "' to table '" + table.getT() + "'");
                            adapter.addColumn(table, field);
                        }

                        DbField originField =
                            DatabaseManager.getFieldFromStructure(schema, table.getT(), field.getName());

                        if (originField != null && (originField.getDef() == null && field.getDef() != null)) {
                            System.out.println("Default value of field " + table.getT() + '.' + originField.getName() +
                                " not set to " + field.getDef());
                            if (DatabaseManager.checkDefaultTimestamp(field.getDef())) {
                                System.out.println("Field recognized as default date field");
                                DatabaseStructureManager.setDefTimestamp(adapter, originTable, field );
                            }
                            else
                                System.out.println("Unknown default type of field");
                        }
                    }

                }
            }
            else
                System.out.println("skip table " + table.getT());
        }

/*
        // get new instance of schema
        schema = DbService.getDbStructure(db_, "MILLENNIUM");
        try
        {
            // check for correct PK
            DbTable tableForCheckPk =
                DbService.getTableFromStructure(schema, table.getT());

//                    System.out.println("orig table '"+table.getT()+"', table for check'"+tableForCheckPk.getT()+"'");
//                    System.out.println("orig table PK "+table.getPk()+", table for check PK "+tableForCheckPk.getPk()+"");

            if (table.getPk()!=null &&
                table.getPk().getColumnsCount()>0 &&
                (tableForCheckPk.getPk()==null || tableForCheckPk.getPk().getColumnsCount()==0))
            {
                System.out.println("Add PK to table '"+tableForCheckPk.getT()+"'");
                DbService.addPrimaryKey(db_, tableForCheckPk, table.getPk());
            }
        }
        catch(Exception e)
        {
            System.out.println("Error add PK "+e.toString());
        }
*/

        DbStructureImport.fullCreateViews(adapter, millSchema.getViews());
        processForeignKeys(adapter, millSchema);

        return DatabaseManager.getDbStructure(adapter);
    }

    public static void main(String args[]) throws Exception {
        long mills = System.currentTimeMillis();


        System.out.println("Unmarshal data from file");
        FileInputStream stream = new FileInputStream("webmill-schema.xml");
        DbSchema millSchema = Utils.getObjectFromXml(DbSchema.class, stream);

        Database adapter=null;
//        validateStructure(millSchema, "ORACLE_MILL_TEST");
        validateStructure(adapter, millSchema);
//        validateStructure(millSchema, "MYSQL");
        validateStructure(adapter, millSchema);

//        validateStructure(millSchema, "IBM-DB2");

        System.out.println("Done validate structure in " + (System.currentTimeMillis() - mills) + " milliseconds");

    }
}

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

import org.apache.commons.lang.StringUtils;

import org.riverock.dbrevision.annotation.schema.db.DbField;
import org.riverock.dbrevision.annotation.schema.db.DbForeignKey;
import org.riverock.dbrevision.annotation.schema.db.DbSchema;
import org.riverock.dbrevision.annotation.schema.db.DbTable;
import org.riverock.dbrevision.db.Database;
import org.riverock.dbrevision.db.DatabaseManager;
import org.riverock.dbrevision.db.DatabaseStructureManager;
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
            if (!DatabaseManager.isSkipTable(table.getName())) {
                System.out.println("Create foreign key for table " + table.getName());

                int p = 0;
                for (DbForeignKey foreignKey : table.getForeignKeys()) {
                    if (StringUtils.isBlank(foreignKey.getFkName())) {
                        foreignKey.setFkName(foreignKey.getFkTableName() + p + "_fk");
                    }
                    DatabaseStructureManager.createForeignKey(adapter, foreignKey);
                }
            }
            else {
                System.out.println("skip table " + table.getName());
            }
        }
    }

    private static DbSchema validateStructure(Database adapter, DbSchema millSchema) throws Exception {
        DbSchema schema = DatabaseManager.getDbStructure(adapter);

        String nameFile = "test-schema.xml";
        System.out.println("Marshal data to file " + nameFile);

        Utils.writeToFile(schema, nameFile);

        for (DbTable table : millSchema.getTables()) {
            if (!DatabaseManager.isSkipTable(table.getName())) {
                DbTable originTable = DatabaseManager.getTableFromStructure(schema, table.getName());
                if (!DatabaseManager.isTableExists(schema, table)) {
                    System.out.println("Create new table " + table.getName());
                    adapter.createTable(table);
                }
                else {
                    // check valid structure of fields
                    for (DbField field : table.getFields()) {
                        if (!DatabaseManager.isFieldExists(schema, table, field)) {
                            System.out.println("Add field '" + field.getName() + "' to table '" + table.getName() + "'");
                            adapter.addColumn(table, field);
                        }

                        DbField originField =
                            DatabaseManager.getFieldFromStructure(schema, table.getName(), field.getName());

                        if (originField != null && (originField.getDefaultValue() == null && field.getDefaultValue() != null)) {
                            System.out.println("Default value of field " + table.getName() + '.' + originField.getName() +
                                " not set to " + field.getDefaultValue());
                            if (DatabaseManager.checkDefaultTimestamp(field.getDefaultValue())) {
                                System.out.println("Field recognized as default date field");
                                DatabaseStructureManager.setDefaultValueTimestamp(adapter, originTable, field );
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

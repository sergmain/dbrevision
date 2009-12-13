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

package org.riverock.dbrevision.db;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.riverock.dbrevision.exception.DbRevisionException;

/**
 * User: SergeMaslyukov
 * Date: 16.03.2009
 * Time: 21:11:41
 * $Id$
 */
public class TableStructureManger {

    public static void changeFieldSize(
        Database db, DbSchema schema, String tableName, String fieldName, int fieldSize, int fieldDecimalDigit) {

        DbTable table = DatabaseManager.getTableFromStructure(schema, tableName);
        if (table==null) {
            throw new IllegalArgumentException("Table '"+tableName+"' not found");
        }
        List<DbField> fields = DatabaseStructureManager.getFieldsList(db, table.getS(), table.getT());
        DbField f=null;
        for (DbField field : fields) {
            if (field.getName().equalsIgnoreCase(fieldName)) {
                f = field;
                break;
            }
        }
        if (f==null) {
            throw new IllegalArgumentException("Field '"+fieldName+"' not found in table '"+tableName);
        }
        DbField targetField = DatabaseStructureManager.cloneDescriptionField(f);
        targetField.setName( getUniqueFieldName(table, f.getName()));
        targetField.setSize(fieldSize);
        targetField.setDigit(fieldDecimalDigit);
        targetField.setNullable(1);

        DbField originField = DatabaseStructureManager.cloneDescriptionField(f);
        originField.setSize(fieldSize);
        originField.setDigit(fieldDecimalDigit);
        originField.setNullable(1);



        db.addColumn(table, targetField);
        DatabaseStructureManager.copyFieldData(db, table, f, targetField);
        DatabaseManager.commit(db);

        List<DbForeignKey> fk=new ArrayList<DbForeignKey>();
        if (table.getPk()!=null) {
            fk = DatabaseManager.getForeignKeys(schema, table.getT(), table.getPk().getColumns().get(0).getC());
            Iterator<DbForeignKey> it = fk.iterator();
            while (it.hasNext()) {
                DbForeignKey foreignKey = it.next();
                // не удаляем FK в текущей таблице 
                if (table.getT().equalsIgnoreCase(foreignKey.getFkTable())) {
                    it.remove();
                }
                else {
                    ConstraintManager.dropFk(db, foreignKey);
                }
            }
        }
        //TODO если поле, которое изменяем не содержит FK то и дропать не надо
        if (table.getForeignKeys()!=null) {
            for (DbForeignKey foreignKey : table.getForeignKeys()) {
                ConstraintManager.dropFk(db, foreignKey);
            }
        }

        DbPrimaryKey pk = table.getPk();
        if (pk!=null) {
            ConstraintManager.dropPk(db, pk);
        }

        List<DbIndex> indexes = new ArrayList<DbIndex>();
        List<DbIndex> idxs = ConstraintManager.getIndexes(db, table.getS(), table.getT() );
        for (DbIndex dbIndex : idxs) {
            for (DbIndexColumn column : dbIndex.getColumns()) {
                if (column.getC()!=null && column.getC().equalsIgnoreCase(fieldName)) {
                    indexes.add(dbIndex);
                    break;
                }
            }
        }
        for (DbIndex index : indexes) {
            ConstraintManager.dropIndex(db, index);
        }

        DatabaseStructureManager.dropColumn(db, table, f);
        db.addColumn(table, originField);
        DatabaseStructureManager.copyFieldData(db, table, targetField, originField);
        DatabaseManager.commit(db);
        if (f.getNullable()==0) {
            db.changeNullableState(table, originField, Database.NullableState.NOTNULL);
        }

        DatabaseStructureManager.dropColumn(db, table, targetField);

        if (pk!=null) {
            ConstraintManager.addPk(db, pk);
        }

        for (DbIndex index : indexes) {
            ConstraintManager.createIndex(db, index);
        }
        for (DbForeignKey foreignKey : fk) {
            ConstraintManager.createFk(db, foreignKey);
        }
        if (table.getForeignKeys()!=null) {
            for (DbForeignKey foreignKey : table.getForeignKeys()) {
                ConstraintManager.createFk(db, foreignKey);
            }
        }
    }

    private static String getUniqueFieldName(DbTable table, String name) {
        for (int i=0; i<100; i++) {
            boolean isFound=false;
            for (DbField field : table.getFields()) {
                if (field.getName().equalsIgnoreCase(name+i)) {
                    isFound=true;
                    break;
                }
            }
            if (!isFound) {
                return name+i;
            }
        }
        throw new DbRevisionException("Error create unique name for field '"+name+"'");
    }
}

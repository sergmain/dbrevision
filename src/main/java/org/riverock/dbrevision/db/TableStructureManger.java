package org.riverock.dbrevision.db;

import java.util.List;
import java.util.ArrayList;

import org.riverock.dbrevision.annotation.schema.db.*;

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
        List<DbField> fields = DatabaseStructureManager.getFieldsList(db, table.getSchema(), table.getName());
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
        targetField.setName(f.getName() + '1');
        targetField.setSize(fieldSize);
        targetField.setDecimalDigit(fieldDecimalDigit);
        targetField.setNullable(1);

        DbField originField = DatabaseStructureManager.cloneDescriptionField(f);
        originField.setSize(fieldSize);
        originField.setDecimalDigit(fieldDecimalDigit);
        originField.setNullable(1);



        db.addColumn(table, targetField);
        DatabaseStructureManager.copyFieldData(db, table, f, targetField);
        DatabaseManager.commit(db);

        List<DbForeignKey> fk=null;
        if (table.getPrimaryKey()!=null) {
            fk = DatabaseManager.getForeignKeys(schema, table.getName(), table.getPrimaryKey().getColumns().get(0).getColumnName());
            for (DbForeignKey foreignKey : fk) {
                ConstraintManager.dropFk(db, foreignKey);
            }
        }
        if (table.getForeignKeys()!=null) {
            for (DbForeignKey foreignKey : table.getForeignKeys()) {
                ConstraintManager.dropFk(db, foreignKey);
            }
        }

        DbPrimaryKey pk = table.getPrimaryKey();
        if (pk!=null) {
            ConstraintManager.dropPk(db, pk);
        }

        List<DbIndex> indexes = new ArrayList<DbIndex>();
        List<DbIndex> idxs = ConstraintManager.getIndexes(db, table.getSchema(), table.getName() );
        for (DbIndex dbIndex : idxs) {
            for (DbIndexColumn column : dbIndex.getColumns()) {
                if (column.getColumnName()!=null && column.getColumnName().equalsIgnoreCase(fieldName)) {
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
        if (fk!=null) {
            for (DbForeignKey foreignKey : fk) {
                ConstraintManager.createFk(db, foreignKey);
            }
        }
        if (table.getForeignKeys()!=null) {
            for (DbForeignKey foreignKey : table.getForeignKeys()) {
                ConstraintManager.createFk(db, foreignKey);
            }
        }
    }
}

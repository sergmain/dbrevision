package org.riverock.dbrevision.manager.patch;

import java.io.InputStream;

import junit.framework.TestCase;

import org.riverock.dbrevision.annotation.schema.db.DbSchema;
import org.riverock.dbrevision.annotation.schema.db.Patches;
import org.riverock.dbrevision.annotation.schema.db.Patch;
import org.riverock.dbrevision.annotation.schema.db.PrimaryKey;
import org.riverock.dbrevision.annotation.schema.db.DbImportedPKColumn;
import org.riverock.dbrevision.annotation.schema.db.DbPrimaryKey;
import org.riverock.dbrevision.annotation.schema.db.DbPrimaryKeyColumn;
import org.riverock.dbrevision.annotation.schema.db.Action;
import org.riverock.dbrevision.annotation.schema.db.ActionParameter;
import org.riverock.dbrevision.utils.Utils;

/**
 * User: SMaslyukov
 * Date: 02.08.2007
 * Time: 13:23:15
 */
public class TestPatchUnmarshaling extends TestCase {

    public void testUnmarshalPatch_1() throws Exception {
        InputStream inputStream = TestPatchUnmarshaling.class.getResourceAsStream("/xml/patch/patch-1.xml"); 
        Patches patches = Utils.getObjectFromXml(Patches.class, inputStream);
        assertNotNull(patches);
        assertNotNull(patches.getPatches());
        assertEquals(6, patches.getPatches().size());
        Patch p;
        p = patches.getPatches().get(0);
        assertEquals("test_2_1", p.getName());
        assertEquals("test_1_1", p.getPreviousName());

        assertEquals(2, p.getActionOrTableDataOrTable().size());

        assertTrue( p.getActionOrTableDataOrTable().get(0) instanceof DbPrimaryKey);
        assertTrue( p.getActionOrTableDataOrTable().get(1) instanceof DbImportedPKColumn);

        DbPrimaryKey pk = (DbPrimaryKey)p.getActionOrTableDataOrTable().get(0);
        assertEquals(1, pk.getColumns().size());

        DbPrimaryKeyColumn col = pk.getColumns().get(0);
        // schemaName="MILLENNIUM" tableName="TEST_1_1" columnName="ID_TEST11" keySeq="1" pkName="ID_TEST11_T11_PK"
        assertEquals("MILLENNIUM", col.getSchemaName());
        assertEquals("TEST_1_1", col.getTableName());
        assertEquals("ID_TEST11", col.getColumnName());
        assertEquals(1, col.getKeySeq());
        assertEquals("ID_TEST11_T11_PK", col.getPkName());

        DbImportedPKColumn fk = (DbImportedPKColumn)p.getActionOrTableDataOrTable().get(1);
        assertNotNull(fk.getDeleteRule());
        assertEquals(new Integer(0), fk.getDeleteRule().getRuleType());
        assertEquals("java.sql.DatabaseMetaData.importedKeyCascade", fk.getDeleteRule().getRuleName());
        assertNotNull(fk.getDeferrability());
        assertEquals(new Integer(7), fk.getDeferrability().getRuleType());
        assertEquals("java.sql.DatabaseMetaData.importedKeyNotDeferrable", fk.getDeferrability().getRuleName());

        // pkSchemaName="MILLENNIUM" pkTableName="TEST_1_1" pkColumnName="ID_TEST11" pkName="ID_TEST11_T11_PK"
        // fkSchemaName="MILLENNIUM" fkTableName="TEST_1_2" fkColumnName="ID_TEST11" keySeq="1" fkName="ID_TEST11_T12_FK"
        assertEquals("MILLENNIUM", fk.getPkSchemaName());
        assertEquals("MILLENNIUM", fk.getFkSchemaName());
        assertEquals("TEST_1_1", fk.getPkTableName());
        assertEquals("TEST_1_2", fk.getFkTableName());
        assertEquals("ID_TEST11", fk.getPkColumnName());
        assertEquals("ID_TEST11", fk.getFkColumnName());
        assertEquals("ID_TEST11_T11_PK", fk.getPkName());
        assertEquals("ID_TEST11_T12_FK", fk.getFkName());
        assertEquals(new Integer(1), fk.getKeySeq());
    }

    public void testUnmarshalPatch_2() throws Exception {
        InputStream inputStream = TestPatchUnmarshaling.class.getResourceAsStream("/xml/patch/patch-2.xml"); 
        Patches patches = Utils.getObjectFromXml(Patches.class, inputStream);
        assertNotNull(patches);
        assertNotNull(patches.getPatches());
        assertEquals(1, patches.getPatches().size());
        Patch p;
        p = patches.getPatches().get(0);
        assertEquals("webmill_init_def_v2", p.getName());
        assertNull(p.getPreviousName());

        assertEquals(1, p.getActionOrTableDataOrTable().size());

        assertTrue( p.getActionOrTableDataOrTable().get(0) instanceof Action);

        Action action = (Action)p.getActionOrTableDataOrTable().get(0);
        assertEquals("CUSTOM_CLASS_ACTION", action.getType());

        assertEquals(1, action.getActionParameters().size());
        ActionParameter ap = action.getActionParameters().get(0);
        assertEquals("class_name", ap.getName());
        assertEquals("org.riverock.db.definition.InitWebmillStructureV2", ap.getData());
    }
}

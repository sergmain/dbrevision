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

package org.riverock.dbrevision.manager.patch;

import java.io.InputStream;

import junit.framework.TestCase;

import org.riverock.dbrevision.schema.db.Action;
import org.riverock.dbrevision.schema.db.ActionParameter;
import org.riverock.dbrevision.schema.db.DbForeignKey;
import org.riverock.dbrevision.schema.db.DbPrimaryKey;
import org.riverock.dbrevision.schema.db.DbPrimaryKeyColumn;
import org.riverock.dbrevision.schema.db.Patch;
import org.riverock.dbrevision.schema.db.Patches;
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

        assertEquals(2, p.getActionOrCustomClassActionOrSqlAction().size());

        assertTrue( p.getActionOrCustomClassActionOrSqlAction().get(0) instanceof DbPrimaryKey);
        assertTrue( p.getActionOrCustomClassActionOrSqlAction().get(1) instanceof DbForeignKey);

        DbPrimaryKey pk = (DbPrimaryKey)p.getActionOrCustomClassActionOrSqlAction().get(0);
        assertEquals(1, pk.getColumns().size());

        DbPrimaryKeyColumn col = pk.getColumns().get(0);
        // schemaName="MILLENNIUM" tableName="TEST_1_1" columnName="ID_TEST11" keySeq="1" pkName="ID_TEST11_T11_PK"
        assertEquals("MILLENNIUM", pk.getS());
        assertEquals("TEST_1_1", pk.getT());
        assertEquals("ID_TEST11", col.getC());
        assertEquals(1, col.getSeq());
        assertEquals("ID_TEST11_T11_PK", pk.getPk());

        DbForeignKey fk = (DbForeignKey)p.getActionOrCustomClassActionOrSqlAction().get(1);
        assertNotNull(fk.getDRule());
        assertEquals(new Integer(0), fk.getDRule().getRuleType());
        assertEquals("java.sql.DatabaseMetaData.importedKeyCascade", fk.getDRule().getRuleName());
        assertNotNull(fk.getDefer());
        assertEquals(new Integer(7), fk.getDefer().getRuleType());
        assertEquals("java.sql.DatabaseMetaData.importedKeyNotDeferrable", fk.getDefer().getRuleName());

        // pkSchemaName="MILLENNIUM" pkTableName="TEST_1_1" pkColumnName="ID_TEST11" pkName="ID_TEST11_T11_PK"
        // fkSchemaName="MILLENNIUM" fkTableName="TEST_1_2" fkColumnName="ID_TEST11" keySeq="1" fkName="ID_TEST11_T12_FK"
        assertEquals("MILLENNIUM", fk.getPkSchema());
        assertEquals("MILLENNIUM", fk.getFkSchema());
        assertEquals("TEST_1_1", fk.getPkTable());
        assertEquals("TEST_1_2", fk.getFkTable());
        assertEquals("ID_TEST11_T11_PK", fk.getPk());
        assertEquals("ID_TEST11_T12_FK", fk.getFk());
        assertEquals(1, fk.getColumns().size());
        assertEquals("ID_TEST11", fk.getColumns().get(0).getPkCol());
        assertEquals("ID_TEST11", fk.getColumns().get(0).getFkCol());
        assertEquals(new Integer(1), fk.getColumns().get(0).getSeq());
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

        assertEquals(1, p.getActionOrCustomClassActionOrSqlAction().size());

        assertTrue( p.getActionOrCustomClassActionOrSqlAction().get(0) instanceof Action);

        Action action = (Action)p.getActionOrCustomClassActionOrSqlAction().get(0);
        assertEquals("CUSTOM_CLASS_ACTION", action.getType());

        assertEquals(1, action.getActionParameters().size());
        ActionParameter ap = action.getActionParameters().get(0);
        assertEquals("class_name", ap.getName());
        assertEquals("org.riverock.db.definition.InitWebmillStructureV2", ap.getData());
    }
}

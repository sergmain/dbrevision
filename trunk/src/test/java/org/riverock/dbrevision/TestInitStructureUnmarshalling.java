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

package org.riverock.dbrevision;

import java.io.InputStream;

import junit.framework.TestCase;

import org.riverock.dbrevision.annotation.schema.db.DbSchema;
import org.riverock.dbrevision.utils.Utils;

/**
 * User: SMaslyukov
 * Date: 30.07.2007
 * Time: 13:30:46
 */
public class TestInitStructureUnmarshalling extends TestCase {

    public void testUnmarshalling() throws Exception {
        InputStream inputStream = TestInitStructureUnmarshalling.class.getResourceAsStream("/xml/init-structure/init-structure.xml"); 
        DbSchema schema = Utils.getObjectFromXml(DbSchema.class, inputStream);
        // test only v2 structure
        assertEquals("org.riverock.dbrevision.annotation.schema.db.DbSchema", schema.getClass().getName());
        
        assertNotNull(schema);
        assertNotNull(schema.getTables());
        assertEquals(2, schema.getTables().size());
        assertEquals("WM_PORTAL_IDS", schema.getTables().get(0).getName());
        assertEquals("WM_AUTH_ACCESS_GROUP", schema.getTables().get(1).getName());


        assertEquals(1, schema.getSequences().size());
        assertEquals("SEQ_WM_PRICE_QUERY_TABLE", schema.getSequences().get(0).getName());

    }
}

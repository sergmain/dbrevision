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
        assertNotNull(schema);
        assertNotNull(schema.getTables());
        assertEquals(2, schema.getTables().size());
        assertEquals("WM_PORTAL_IDS", schema.getTables().get(0).getName());
        assertEquals("WM_AUTH_ACCESS_GROUP", schema.getTables().get(1).getName());


        assertEquals(1, schema.getSequences().size());
        assertEquals("SEQ_WM_PRICE_QUERY_TABLE", schema.getSequences().get(0).getName());

    }
}

package org.riverock.dbrevision.manager.config;

import junit.framework.TestCase;
import org.riverock.dbrevision.manager.Config;
import org.riverock.dbrevision.manager.ModuleConfig;

import java.io.InputStream;

/**
 * User: SergeMaslyukov
 * Date: 29.07.2007
 * Time: 17:11:29
 */
public class TestConfigParser extends TestCase {

    public void testConfigParser() throws Exception {
        InputStream inputStream = TestConfigParser.class.getResourceAsStream("/xml/config/config.xml");
        Config config = ConfigParserFactory.getConfigParser().parse(inputStream );
        assertNotNull(config);
        assertNotNull(config.getModuleConfigs());
        assertEquals(1, config.getModuleConfigs().size());
        ModuleConfig moduleConfig = config.getModuleConfigs().get(0);
        assertNotNull(moduleConfig);
        assertEquals("Webmill portal", moduleConfig.getDescription());
        assertEquals("webmill", moduleConfig.getName());
        assertNotNull(moduleConfig.getVersions());
        assertEquals(4, moduleConfig.getVersions().size());
        assertEquals("5.7.0", moduleConfig.getVersions().get(0));
        assertEquals("5.7.1", moduleConfig.getVersions().get(1));
        assertEquals("5.7.2", moduleConfig.getVersions().get(2));
        assertEquals("5.8.0", moduleConfig.getVersions().get(3));
    }
}

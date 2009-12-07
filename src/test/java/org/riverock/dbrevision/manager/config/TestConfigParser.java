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
        assertEquals(2, config.getModuleConfigs().size());
        ModuleConfig moduleConfig;
        moduleConfig = config.getModuleConfigs().get(0);
        assertNotNull(moduleConfig);
        assertEquals("Webmill portal", moduleConfig.getDescription());
        assertEquals("webmill", moduleConfig.getName());
        assertNotNull(moduleConfig.getVersions());
        assertEquals(4, moduleConfig.getVersions().size());
        assertEquals("5.7.0", moduleConfig.getVersions().get(0));
        assertEquals("5.7.1", moduleConfig.getVersions().get(1));
        assertEquals("5.7.2", moduleConfig.getVersions().get(2));
        assertEquals("5.8.0", moduleConfig.getVersions().get(3));

        moduleConfig = config.getModuleConfigs().get(1);
        assertNotNull(moduleConfig);
        assertEquals("Second module", moduleConfig.getDescription());
        assertEquals("second-module", moduleConfig.getName());
        assertNotNull(moduleConfig.getVersions());
        assertEquals(2, moduleConfig.getVersions().size());
        assertEquals("1.0.0", moduleConfig.getVersions().get(0));
        assertEquals("1.1.0", moduleConfig.getVersions().get(1));


    }
}

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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.riverock.dbrevision.schema.db.v3.DbRevision;
import org.riverock.dbrevision.exception.ConfigParseException;
import org.riverock.dbrevision.manager.Config;
import org.riverock.dbrevision.manager.ModuleConfig;
import org.riverock.dbrevision.utils.Utils;

/**
 * User: SMaslyukov
 * Date: 30.07.2007
 * Time: 12:08:15
 */
public class JaxbConfigParserImpl implements ConfigParser {
    public Config parse(InputStream inputStream) {
        try {
            DbRevision dbRevision = Utils.getObjectFromXml(DbRevision.class, inputStream);
            List<ModuleConfig> modules = new ArrayList<ModuleConfig>();
            for (DbRevision.Module module : dbRevision.getModules()) {
                ModuleConfig m = new ModuleConfig();
                m.setDescription(module.getDescription());
                m.setT(module.getName());
                m.setVersions(module.getVersions().getVersions());

                modules.add(m);
            }

            Config config = new Config();
            config.setModuleConfigs(modules);
            return config;
        }
        catch (Exception e) {
            throw new ConfigParseException(e);
        }
    }
}

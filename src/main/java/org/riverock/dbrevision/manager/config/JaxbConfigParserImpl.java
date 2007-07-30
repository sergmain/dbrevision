package org.riverock.dbrevision.manager.config;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.riverock.dbrevision.annotation.schema.db.DbRevision;
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
                m.setName(module.getName());
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

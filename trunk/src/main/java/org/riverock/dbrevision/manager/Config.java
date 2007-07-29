package org.riverock.dbrevision.manager;

import java.util.ArrayList;
import java.util.List;

/**
 * User: SergeMaslyukov
 * Date: 29.07.2007
 * Time: 1:09:59
 */
public class Config {
    private List<ModuleConfig> moduleConfigs = new ArrayList<ModuleConfig>();

    public List<ModuleConfig> getModuleConfigs() {
        return moduleConfigs;
    }

    public void setModuleConfigs(List<ModuleConfig> moduleConfigs) {
        this.moduleConfigs = moduleConfigs;
    }

    public void addModule(ModuleConfig moduleConfig) {
        moduleConfigs.add( moduleConfig );
    }
}

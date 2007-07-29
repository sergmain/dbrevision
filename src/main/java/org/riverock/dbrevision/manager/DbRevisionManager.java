package org.riverock.dbrevision.manager;

import org.riverock.dbrevision.Constants;
import org.riverock.dbrevision.db.DatabaseAdapter;
import org.riverock.dbrevision.exception.ConfigFileNotFoundException;
import org.riverock.dbrevision.exception.DbRevisionPathNotFoundException;
import org.riverock.dbrevision.manager.dao.ManagerDaoFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * User: SergeMaslyukov
 * Date: 28.07.2007
 * Time: 20:01:14
 */
public class DbRevisionManager {

    private File path=null;
    
    private File configFile=null;

    private List<Module> modules = new ArrayList<Module>();

    private DatabaseAdapter databaseAdapter;

    public DbRevisionManager(DatabaseAdapter databaseAdapter, String dbRevisionPath) {
        if (databaseAdapter==null) {
            throw new IllegalArgumentException("DatabaseAdapter is null");
        }
        if (dbRevisionPath==null) {
            throw new IllegalArgumentException("dbRevisionPath is null");
        }
        this.databaseAdapter = databaseAdapter;
        this.path = new File(dbRevisionPath);
        if (!path.exists()) {
            throw new DbRevisionPathNotFoundException("DbRevision path not found: " + path.getAbsolutePath());
        }
        this.configFile = new File(path, Constants.CONFIG_FILE_NAME);
        if (!configFile.exists()) {
            throw new ConfigFileNotFoundException("Config file not found: " + configFile.getAbsolutePath() );
        }
        processConfigFile();
        prepareCurrentVersions();
    }

    /**
     * get module by name
     *
     * @param name module name
     * @return module
     */
    public Module getModule(String name) {
        for (Module module : modules) {
            if (module.getName().equals(name)) {
                return module;
            }
        }
        return null;
    }

    private void processConfigFile() {
        Config config = parseConfigFiles();
        for (ModuleConfig moduleConfig : config.getModuleConfigs()) {
            modules.add( new Module(databaseAdapter, path, moduleConfig) );
        }
    }

    private Config parseConfigFiles() {
        Config config = new Config();

        ModuleConfig moduleConfig = new ModuleConfig();
        moduleConfig.setName("Webmill portal");
        moduleConfig.setPath("webmill");
        moduleConfig.getVersions().add("5.7.0");
        moduleConfig.getVersions().add("5.7.1");

        config.getModuleConfigs().add(moduleConfig);
        return config;
    }

    List<Module> getModules() {
        return modules;
    }

    private void prepareCurrentVersions() {
        List<RevisionBean> revisionBeans = ManagerDaoFactory.getManagerDao().getRevisionBean();
        for (Module module : modules) {
            
        }
    }
}

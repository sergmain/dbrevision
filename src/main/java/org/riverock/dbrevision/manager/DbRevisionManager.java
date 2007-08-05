package org.riverock.dbrevision.manager;

import org.riverock.dbrevision.Constants;
import org.riverock.dbrevision.annotation.schema.db.Patch;
import org.riverock.dbrevision.db.DatabaseAdapter;
import org.riverock.dbrevision.exception.ConfigFileNotFoundException;
import org.riverock.dbrevision.exception.CurrentVersionCodeNotFoundException;
import org.riverock.dbrevision.exception.DbRevisionPathNotFoundException;
import org.riverock.dbrevision.exception.ModuleNotConfiguredException;
import org.riverock.dbrevision.exception.DbRevisionException;
import org.riverock.dbrevision.manager.config.ConfigParserFactory;
import org.riverock.dbrevision.manager.dao.ManagerDaoFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

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
        Config config;
        try {
            config = ConfigParserFactory.getConfigParser().parse( new FileInputStream(configFile) );
        } catch (FileNotFoundException e) {
            throw new ConfigFileNotFoundException(e);
        }
        for (ModuleConfig moduleConfig : config.getModuleConfigs()) {
            modules.add( new Module(databaseAdapter, path, moduleConfig) );
        }
    }

    public List<Module> getModules() {
        return modules;
    }

    private void prepareCurrentVersions() {
        List<RevisionBean> revisionBeans = ManagerDaoFactory.getManagerDao().getRevisions(databaseAdapter);
        for (RevisionBean revisionBean : revisionBeans) {
            Module module = getModule(revisionBean.getModuleName());
            if (module!=null) {
                markCurrentVersion(module, revisionBean);
            }
            else {
                throw new ModuleNotConfiguredException("Module '"+ revisionBean.getModuleName()+"' not configured.");
            }
        }
    }

    private void markCurrentVersion(Module module, RevisionBean revisionBean) {
        boolean isFound = false;
        for (Version version : module.getVersions()) {
            if (version.getVersionName().equals(revisionBean.getCurrentVerson())) {
                markAllCompleteProccesedVesion(version, revisionBean);
                isFound = true;
                break;
            }
        }
        if (!isFound) {
            throw new CurrentVersionCodeNotFoundException(
                    "Current version '"+revisionBean.getCurrentVerson()+"' not exist in configuration for module '"+revisionBean.getModuleName()+"'. " +
                            "Check '<db-revision-path>/config.xml' file."
            );
        }
        Version v = module.getLastVersion();
        if (v==null || v.isComplete()) {
            module.setComplete(true);
        }
    }

    private void markAllCompleteProccesedVesion(Version version, RevisionBean revisionBean) {
        Version v;
        if (version.getPatches().isEmpty()) {
            if (StringUtils.isNotBlank(revisionBean.getLastPatch())) {
                throw new DbRevisionException("Invalid value of lastPatch in database. Module: " + revisionBean.getModuleName()+", version: " + revisionBean.getCurrentVerson());
            }
            v = version;  
        }
        else if (StringUtils.isBlank(revisionBean.getLastPatch())) {
                throw new DbRevisionException("Invalid value of lastPatch in database. Module: " + revisionBean.getModuleName()+", version: " + revisionBean.getCurrentVerson());
        }
        else {
            if (version.getPatches().get(version.getPatches().size()-1).getName().equals(revisionBean.getLastPatch())) {
                v = version;
            }
            else {
                v = version.getPreviousVersion();
                List<Patch> completed = new ArrayList<Patch>();
                boolean isFound = false;
                for (Patch patch : version.getPatches()) {
                    if (patch.getName().equals(revisionBean.getLastPatch())) {
                        completed.add(patch);
                        isFound = true;
                        break;
                    }
                    completed.add(patch);
                }

                if (isFound) {
                    for (Patch patch : completed) {
                        patch.setProcessed(true);
                    }
                }
                else {
                    throw new DbRevisionException("Value of lastPatch in database not equals to last value in config. Module: " + revisionBean.getModuleName()+", version: " + revisionBean.getCurrentVerson());
                }
            }
        }

        while (v!=null) {
            v.setComplete(true);
            v = v.getPreviousVersion();
        }
    }
}

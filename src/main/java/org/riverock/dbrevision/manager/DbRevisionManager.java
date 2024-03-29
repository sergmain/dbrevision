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

package org.riverock.dbrevision.manager;

import org.apache.commons.lang3.StringUtils;
import org.riverock.dbrevision.Constants;
import org.riverock.dbrevision.utils.DbUtils;
import org.riverock.dbrevision.schema.db.v3.Patch;
import org.riverock.dbrevision.db.Database;
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
import java.io.IOException;
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

    private Database database;

    public DbRevisionManager(Database database, String dbRevisionPath) {
        if (database ==null) {
            throw new IllegalArgumentException("Database is null");
        }
        if (dbRevisionPath==null) {
            throw new IllegalArgumentException("dbRevisionPath is null");
        }
        this.database = database;
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
    
    protected void finalize() throws Throwable {
        destroy();
        super.finalize();
    }

    public void destroy() {

        if (modules!=null) {
            for (Module module : modules) {
                module.destroy();
            }
            modules.clear();
            modules=null;
        }
        if (database!=null) {
            if (database.getConnection()!=null) {
                DbUtils.close(database.getConnection());
                database.setConnection(null);
            }
            this.database=null;
        }
    }

    /**
     * get module by name
     *
     * @param name module name
     * @return module
     */
    public Module getModule(String name) {
        for (Module module : modules) {
            if (module.getT().equals(name)) {
                return module;
            }
        }
        return null;
    }

    /**
     * is all moudles completed?
     *
     * @return module
     */
    public boolean isAllCompleted() {
        for (Module module : modules) {
            if (!module.isComplete()) {
                return false;
            }
        }
        return true;
    }

    private void processConfigFile() {
        Config config;
        FileInputStream inputStream=null;
        try {
            inputStream = new FileInputStream(configFile);
            config = ConfigParserFactory.getConfigParser().parse(inputStream);
        }
        catch (FileNotFoundException e) {
            throw new ConfigFileNotFoundException(e);
        }
        finally {
            if (inputStream!=null) {
                try {
                    inputStream.close();
                }
                catch (IOException e1) {
                    //
                }
            }
        }
        for (ModuleConfig moduleConfig : config.getModuleConfigs()) {
            modules.add( new Module(database, path, moduleConfig) );
        }
    }

    public List<Module> getModules() {
        return modules;
    }

    private void prepareCurrentVersions() {
        List<RevisionBean> revisionBeans = ManagerDaoFactory.getManagerDao().getRevisions(database);
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
        Version prevVersion = null;
        for (Version version : module.getVersions()) {
            version.setPreviousVersion(prevVersion);
            if (prevVersion!=null) {
                prevVersion.setNextVersion(version);
            }
            prevVersion = version;
        }

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
            v = version;    
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

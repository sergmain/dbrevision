package org.riverock.dbrevision.manager;

import org.riverock.dbrevision.db.Database;
import org.riverock.dbrevision.exception.FirstVersionWithPatchdException;
import org.riverock.dbrevision.exception.ModulePathNotFoundException;
import org.riverock.dbrevision.exception.CurrentVersionNotDefinedException;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: SergeMaslyukov
 * Date: 28.07.2007
 * Time: 20:03:59
 */
public class Module implements Serializable {

    List<Version> versions=new ArrayList<Version>();

    Database database =null;

    private String description=null;

    private String name =null;

    private File modulePath=null;

    private boolean isComplete=false;

    public Module(Database database, File dbRevisionPath, ModuleConfig moduleConfig) {
        this.database = database;
        this.description = moduleConfig.getDescription();
        this.name = moduleConfig.getName();

        this.modulePath = new File(dbRevisionPath, name);
        if (!modulePath.exists()) {
            throw new ModulePathNotFoundException("Module path not found: " + modulePath.getAbsolutePath() );
        }
        for (String versionName : moduleConfig.getVersions()) {
            Version version = new Version(database, modulePath, versionName);
            versions.add(version);
        }
        if (versions.size()>0) {
            File patchPath = versions.get(0).getPatchPath();
            if (patchPath.exists()) {
                throw new FirstVersionWithPatchdException("First version contains patch directory: " + patchPath.getAbsolutePath());
            }
        }
    }
    public void apply() {
        if (versions.isEmpty()) {
            return;
        }
        if (DbRevisionChecker.isModuleReleased(database, name)) { 
            for (Version version : versions) {
                if (version.isComplete()) {
                    continue;
                }
                version.apply();
            }
        }
        else {
            Version version = versions.get(versions.size()-1);
            version.applyInitStructure();
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Version> getVersions() {
        return versions;
    }

    public Version getCurrentVersion() {
        Version v=null;
        for (Version version : versions) {
            if (version.isComplete()) {
                v = version;
            }
        }
        if (v==null) {
            throw new CurrentVersionNotDefinedException();
        }
        return v;
    }

    public Version getFirstVersion() {
        if (versions.isEmpty()) {
            return null;
        }
        return versions.get(0);
    }

    public Version getLastVersion() {
        if (versions.isEmpty()) {
            return null;
        }
        return versions.get(versions.size()-1);
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public String toString() {
        return "["+name+";"+description+";["+versions+"]]";
    }
}
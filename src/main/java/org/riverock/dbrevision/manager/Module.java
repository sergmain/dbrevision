package org.riverock.dbrevision.manager;

import org.riverock.dbrevision.db.DatabaseAdapter;
import org.riverock.dbrevision.exception.FirstVersionWithPatchdException;
import org.riverock.dbrevision.exception.ModulePathNotFoundException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * User: SergeMaslyukov
 * Date: 28.07.2007
 * Time: 20:03:59
 */
public class Module {

    private List<Version> versions=new ArrayList<Version>();

    private DatabaseAdapter databaseAdapter=null;

    private String description=null;

    private String name =null;

    private File modulePath=null;

    public Module(DatabaseAdapter databaseAdapter, File dbRevisionPath, ModuleConfig moduleConfig) {
        this.databaseAdapter = databaseAdapter;
        this.description = moduleConfig.getDescription();
        this.name = moduleConfig.getName();

        this.modulePath = new File(dbRevisionPath, name);
        if (!modulePath.exists()) {
            throw new ModulePathNotFoundException("Module path not found: " + modulePath.getAbsolutePath() );
        }
        for (String versionName : moduleConfig.getVersions()) {
            Version version = new Version(databaseAdapter, modulePath, versionName);
            versions.add(version);
        }
        if (versions.size()>0) {
            File patchPath = versions.get(0).getPatchPath();
            if (patchPath.exists()) {
                throw new FirstVersionWithPatchdException("First version contains patch directory: " + patchPath.getAbsolutePath());
            }
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

        return null;
    }

    public Version getLastVersion() {

        return null;
    }

    public void applay() {
        
    }
}

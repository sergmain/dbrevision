package org.riverock.dbrevision.manager;

import org.riverock.dbrevision.Constants;
import org.riverock.dbrevision.db.DatabaseAdapter;
import org.riverock.dbrevision.exception.InitStructureFileNotFoundException;
import org.riverock.dbrevision.exception.VersionPathNotFoundException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * User: SergeMaslyukov
 * Date: 28.07.2007
 * Time: 20:04:13
 */
public class Version {

    private Version previousVersion=null;
    private Version nextVersion=null;

    List<Patch> patches=new ArrayList<Patch>();

    private DatabaseAdapter databaseAdapter=null;

    private boolean isComplete = true;

    private String versionName;

    private File versionPath=null;

    private File initStructureFile=null;

    private File patchPath=null;

    public Version(DatabaseAdapter databaseAdapter, File modulePath, String versionName) {
        this.databaseAdapter = databaseAdapter;
        this.versionName = versionName;
        this.versionPath = new File(modulePath, this.versionName);
        if (!versionPath.exists()) {
            throw new VersionPathNotFoundException("Version path not found: " + versionPath.getAbsolutePath() );
        }
        this.initStructureFile = new File(versionPath, Constants.INIT_STRUCTURE_FILE_NAME);
        if (!initStructureFile.exists()) {
            throw new InitStructureFileNotFoundException("Init structure file not found: " + initStructureFile.getAbsolutePath() );
        }
        this.patchPath = new File(versionPath, Constants.PATCH_DIR_NAME);
    }

    public void applay() {
    }

    public Version getPreviousVersion() {
        return previousVersion;
    }

    void setPreviousVersion(Version previousVersion) {
        this.previousVersion = previousVersion;
    }

    public String getVersionName() {
        return versionName;
    }

    public File getPatchPath() {
        return patchPath;
    }

    public void setPatchPath(File patchPath) {
        this.patchPath = patchPath;
    }

    public Patch getLastPatch() {
        if (patches.isEmpty()) {
            return null;
        }
        return patches.get(patches.size()-1);
    }

    private boolean isPreviousVersionCorrect() {

        return false;
    }

    public boolean isComplete() {
        return isComplete;
    }

    void setComplete(boolean complete) {
        isComplete = complete;
    }

    public List<Patch> getPatches() {
        return patches;
    }

    void setPatches(List<Patch> patches) {
        this.patches = patches;
    }

    Version getNextVersion() {
        return nextVersion;
    }

    void setNextVersion(Version nextVersion) {
        this.nextVersion = nextVersion;
    }

}

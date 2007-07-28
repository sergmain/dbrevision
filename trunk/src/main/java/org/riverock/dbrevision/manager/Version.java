package org.riverock.dbrevision.manager;

import org.riverock.dbrevision.db.DatabaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: SergeMaslyukov
 * Date: 28.07.2007
 * Time: 20:04:13
 * To change this template use File | Settings | File Templates.
 */
public class Version {

    private Version previousVersion=null;
    private Version nextVersion=null;

    List<Patch> patches=new ArrayList<Patch>();

    private DatabaseAdapter databaseAdapter=null;

    public Version(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
    }

    public void applay() {
        
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

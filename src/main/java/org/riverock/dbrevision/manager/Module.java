package org.riverock.dbrevision.manager;

import org.riverock.dbrevision.db.DatabaseAdapter;

import java.util.List;

/**
 * User: SergeMaslyukov
 * Date: 28.07.2007
 * Time: 20:03:59
 */
public class Module {

    private List<Version> versions=null;

    private DatabaseAdapter databaseAdapter=null;

    private String name=null;

    private String path=null; 

    public Module(DatabaseAdapter databaseAdapter, String name, String path) {
        this.databaseAdapter = databaseAdapter;
        this.name = name;
        this.path = path;

        
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

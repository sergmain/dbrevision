package org.riverock.dbrevision.manager;

import org.riverock.dbrevision.db.DatabaseAdapter;
import org.riverock.dbrevision.exception.DbRevisionPathNotFoundException;

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
            throw new DbRevisionPathNotFoundException();
        }
    }

    public List<Module> getModules() {
        return modules;
    }
}

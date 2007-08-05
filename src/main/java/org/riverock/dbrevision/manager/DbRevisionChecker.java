package org.riverock.dbrevision.manager;

import org.riverock.dbrevision.db.DatabaseAdapter;
import org.riverock.dbrevision.manager.dao.ManagerDaoFactory;

/**
 * User: SergeMaslyukov
 * Date: 05.08.2007
 * Time: 21:39:25
 */
public class DbRevisionChecker {

    public static boolean isUpToDate(DatabaseAdapter adapter, String moduleName, String versionName) {
        RevisionBean bean = ManagerDaoFactory.getManagerDao().getRevision(adapter, moduleName, versionName);
        return !(bean==null);
    }

}

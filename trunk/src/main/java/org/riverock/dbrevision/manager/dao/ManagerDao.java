package org.riverock.dbrevision.manager.dao;

import org.riverock.dbrevision.manager.RevisionBean;
import org.riverock.dbrevision.db.DatabaseAdapter;

import java.util.List;

/**
 * User: SergeMaslyukov
 * Date: 29.07.2007
 * Time: 13:12:11
 */
public interface ManagerDao {
    List<RevisionBean> getRevisions(DatabaseAdapter adapter);

    RevisionBean getRevision(DatabaseAdapter adapter, String moduleName, String versionName);

    void checkDbRevisionTableExist(DatabaseAdapter adapter);

    void makrCurrentVersion(DatabaseAdapter adapter, String moduleName, String versionName, String patchName);


}

package org.riverock.dbrevision.manager.dao;

import org.riverock.dbrevision.manager.RevisionBean;
import org.riverock.dbrevision.db.Database;

import java.util.List;

/**
 * User: SergeMaslyukov
 * Date: 29.07.2007
 * Time: 13:12:11
 */
public interface ManagerDao {
    List<RevisionBean> getRevisions(Database adapter);

    RevisionBean getRevision(Database adapter, String moduleName, String versionName);

    void checkDbRevisionTableExist(Database adapter);

    void makrCurrentVersion(Database adapter, String moduleName, String versionName, String patchName);


}

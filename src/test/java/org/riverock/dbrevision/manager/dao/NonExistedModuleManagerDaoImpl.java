package org.riverock.dbrevision.manager.dao;

import org.riverock.dbrevision.manager.RevisionBean;
import org.riverock.dbrevision.db.DatabaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * User: SergeMaslyukov
 * Date: 29.07.2007
 * Time: 14:10:12
 */
public class NonExistedModuleManagerDaoImpl implements ManagerDao {
    public List<RevisionBean> getRevisions(DatabaseAdapter adapter) {
        List<RevisionBean> list = new ArrayList<RevisionBean>();
        RevisionBean bean = new RevisionBean();
        bean.setModuleName("unknown-module");
        bean.setCurrentVerson("5.x.x");
        bean.setLastPatch(null);

        list.add(bean);
        return list;
    }

    public RevisionBean getRevision(DatabaseAdapter adapter, String moduleNAme, String versionName) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void checkDbRevisionTableExist(DatabaseAdapter adapter) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void makrCurrentVersion(DatabaseAdapter databaseAdapter, String moduleName, String versionName, String patchName) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

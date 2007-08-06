package org.riverock.dbrevision.manager.dao;

import org.riverock.dbrevision.manager.RevisionBean;
import org.riverock.dbrevision.db.Database;

import java.util.ArrayList;
import java.util.List;

/**
 * User: SergeMaslyukov
 * Date: 29.07.2007
 * Time: 14:10:12
 */
public class CurrentVersionInMiddleOfListManagerDaoImpl implements ManagerDao {
    public List<RevisionBean> getRevisions(Database adapter) {
        List<RevisionBean> list = new ArrayList<RevisionBean>();
        RevisionBean bean = new RevisionBean();
        bean.setModuleName("webmill");
        bean.setCurrentVerson("5.7.2");
        bean.setLastPatch("test_0_2");

        list.add(bean);
        return list;
    }

    public RevisionBean getRevision(Database adapter, String moduleNAme, String versionName) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void checkDbRevisionTableExist(Database adapter) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void makrCurrentVersion(Database database, String moduleName, String versionName, String patchName) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

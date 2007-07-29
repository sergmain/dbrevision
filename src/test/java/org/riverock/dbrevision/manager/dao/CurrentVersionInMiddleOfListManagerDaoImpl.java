package org.riverock.dbrevision.manager.dao;

import org.riverock.dbrevision.manager.RevisionBean;

import java.util.ArrayList;
import java.util.List;

/**
 * User: SergeMaslyukov
 * Date: 29.07.2007
 * Time: 14:10:12
 */
public class CurrentVersionInMiddleOfListManagerDaoImpl implements ManagerDao {
    public List<RevisionBean> getRevisionBean() {
        List<RevisionBean> list = new ArrayList<RevisionBean>();
        RevisionBean bean = new RevisionBean();
        bean.setModuleName("webmill");
        bean.setCurrentVerson("5.7.2");
        bean.setComplete(true);
        bean.setLastProcessedStep(null);
        bean.setLastPatch(null);

        list.add(bean);
        return list;
    }
}

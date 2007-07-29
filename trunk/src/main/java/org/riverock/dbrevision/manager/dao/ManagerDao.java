package org.riverock.dbrevision.manager.dao;

import org.riverock.dbrevision.manager.RevisionBean;

import java.util.List;

/**
 * User: SergeMaslyukov
 * Date: 29.07.2007
 * Time: 13:12:11
 */
public interface ManagerDao {
    List<RevisionBean> getRevisionBean();
}

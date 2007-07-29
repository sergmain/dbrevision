package org.riverock.dbrevision.manager.dao;

/**
 * User: SergeMaslyukov
 * Date: 29.07.2007
 * Time: 14:08:31
 */
public class ManagerDaoFactory {
    private static ManagerDao managerDao = new ManagerDaoImpl();

    public static ManagerDao getManagerDao() {
        return managerDao;
    }

    static void setManagerDao(ManagerDao managerDao) {
        ManagerDaoFactory.managerDao = managerDao;
    }
}

package org.riverock.dbrevision.manager.dao;

/**
 * User: SergeMaslyukov
 * Date: 29.07.2007
 * Time: 14:12:25
 */
public class InitManagerDaoFactory {
    public static void init() {
        ManagerDaoFactory.setManagerDao(new TestManagerDaoImpl());
    }
}

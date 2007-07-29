package org.riverock.dbrevision.manager.dao;

/**
 * User: SergeMaslyukov
 * Date: 29.07.2007
 * Time: 14:12:25
 */
public class InitManagerDaoFactory {
    public static void initCompleteV570() {
        ManagerDaoFactory.setManagerDao(new CorrectedManagerDaoImpl());
    }

    public static void initNotExistedVersionModule() {
        ManagerDaoFactory.setManagerDao(new NonExistedVersionManagerDaoImpl());
    }

    public static void initNotExistedModule() {
        ManagerDaoFactory.setManagerDao(new NonExistedModuleManagerDaoImpl());
    }

    public static void initCurrentVersionInMiddleOfList() {
        ManagerDaoFactory.setManagerDao(new CurrentVersionInMiddleOfListManagerDaoImpl());
    }

}

package org.riverock.dbrevision.manager;

import junit.framework.TestCase;
import org.riverock.dbrevision.db.factory.LocalDatabaseAdapter;
import org.riverock.dbrevision.exception.DbRevisionPathNotFoundException;

import java.util.List;

/**
 * User: SergeMaslyukov
 * Date: 28.07.2007
 * Time: 20:59:49
 */
public class TestDbRevisionManager extends TestCase {
    private static final String NOT_EXISTED_PATH = "not-existed-path";

    public static final String DBREVISION_CONFIG = "src\\test\\configs\\config-1";

    public void testContructor() throws Exception {
        DbRevisionManager manager;
        boolean isCorrect = false;
        try {
            manager = new DbRevisionManager(null, null);
        }
        catch (IllegalArgumentException e) {
            isCorrect = true;
        }
        assertTrue(isCorrect);
        isCorrect=false;
        LocalDatabaseAdapter localDatabaseAdapter = new LocalDatabaseAdapter(null);
        try {
            manager = new DbRevisionManager(localDatabaseAdapter, null);
        }
        catch (IllegalArgumentException e) {
            isCorrect = true;
        }
        assertTrue(isCorrect);
        isCorrect=false;
        try {
            manager = new DbRevisionManager(null, NOT_EXISTED_PATH);
        }
        catch (IllegalArgumentException e) {
            isCorrect = true;
        }
        assertTrue(isCorrect);
        isCorrect=false;
        try {
            manager = new DbRevisionManager(localDatabaseAdapter, NOT_EXISTED_PATH);
        }
        catch (DbRevisionPathNotFoundException e) {
            isCorrect = true;
        }
        assertTrue(isCorrect);
        isCorrect=false;


    }

    public void testConfig_1() throws Exception {
        LocalDatabaseAdapter localDatabaseAdapter = new LocalDatabaseAdapter(null);
        DbRevisionManager manager = new DbRevisionManager(localDatabaseAdapter, NOT_EXISTED_PATH);
        List<Module> modules = manager.getModules();
        assertNotNull(modules);
        assertEquals(1, modules.size());

        Module module = modules.get(0);
        assertNotNull(module);

        List<Version> versions = module.getVersions();
        assertNotNull(versions);
        assertEquals(2, versions.size());

        
    }
}

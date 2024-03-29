/*
 * Copyright 2007 Sergei Maslyukov at riverock.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.riverock.dbrevision.manager;

import junit.framework.TestCase;
import org.riverock.dbrevision.db.impl.LocalDatabase;
import org.riverock.dbrevision.exception.*;
import org.riverock.dbrevision.manager.dao.InitManagerDaoFactory;

import java.io.File;
import java.util.List;

/**
 * User: SergeMaslyukov
 * Date: 28.07.2007
 * Time: 20:59:49
 */
public class TestDbRevisionManager extends TestCase {
    private static final String NOT_EXISTED_PATH = "not-existed-path";

    public static final String DBREVISION_CONFIGS = "src"+ File.separatorChar+"test" + File.separatorChar+ "configs";

    public void setUp() {
        InitManagerDaoFactory.initCompleteV570();
    }

    public void testContructor() throws Exception {
        boolean isCorrect = false;
        try {
            new DbRevisionManager(null, null);
        }
        catch (IllegalArgumentException e) {
            isCorrect = true;
        }
        assertTrue(isCorrect);
        isCorrect=false;
        LocalDatabase localDatabaseAdapter = new LocalDatabase(null);
        try {
            new DbRevisionManager(localDatabaseAdapter, null);
        }
        catch (IllegalArgumentException e) {
            isCorrect = true;
        }
        assertTrue(isCorrect);
        isCorrect=false;
        try {
            new DbRevisionManager(null, NOT_EXISTED_PATH);
        }
        catch (IllegalArgumentException e) {
            isCorrect = true;
        }
        assertTrue(isCorrect);
        isCorrect=false;
        try {
            new DbRevisionManager(localDatabaseAdapter, NOT_EXISTED_PATH);
        }
        catch (DbRevisionPathNotFoundException e) {
            isCorrect = true;
        }
        assertTrue(isCorrect);
    }

    /**
     * test missing config.xml file
     * 
     * @throws Exception on error
     */
    public void testConfig_0() throws Exception {
        String path = DBREVISION_CONFIGS+File.separatorChar+"config-0";
        LocalDatabase localDatabaseAdapter = new LocalDatabase(null);
        boolean isCorrect=false;
        try {
           new DbRevisionManager(localDatabaseAdapter, path);
        }
        catch (ConfigFileNotFoundException e) {
            isCorrect=true;
        }
        assertTrue(isCorrect);
    }

    /**
     * test missing directory with module
     *
     * @throws Exception on error
     */
    public void testConfig_1() throws Exception {
        String path = DBREVISION_CONFIGS+File.separatorChar+"config-1";
        LocalDatabase localDatabaseAdapter = new LocalDatabase(null);
        boolean isCorrect=false;
        try {
           new DbRevisionManager(localDatabaseAdapter, path);
        }
        catch (ModulePathNotFoundException e) {
            isCorrect=true;
        }
        assertTrue(isCorrect);
    }

    /**
     * test missing directories with version
     *
     * @throws Exception on error
     */
    public void testConfig_2() throws Exception {
        String path = DBREVISION_CONFIGS+File.separatorChar+"config-2";
        LocalDatabase localDatabaseAdapter = new LocalDatabase(null);
        boolean isCorrect=false;
        try {
           new DbRevisionManager(localDatabaseAdapter, path);
        }
        catch (VersionPathNotFoundException e) {
            isCorrect=true;
        }
        assertTrue(isCorrect);
    }

    /**
     * test in version directory missing webmill/5.7.0/init-structure.xml file
     * 
     * @throws Exception on error
     */
    public void testConfig_3() throws Exception {
        String path = DBREVISION_CONFIGS+File.separatorChar+"config-3";
        LocalDatabase localDatabaseAdapter = new LocalDatabase(null);
        boolean isCorrect=false;
        try {
           new DbRevisionManager(localDatabaseAdapter, path);
        }
        catch (InitStructureFileNotFoundException e) {
            isCorrect=true;
        }
        assertTrue(isCorrect);
    }

    /**
     * test directory with first version must not contain patches ('patch' directory)
     * 
     * @throws Exception on error
     */
    public void testConfig_4() throws Exception {
        String path = DBREVISION_CONFIGS+File.separatorChar+"config-4";
        LocalDatabase localDatabaseAdapter = new LocalDatabase(null);
        boolean isCorrect=false;
        try {
           new DbRevisionManager(localDatabaseAdapter, path);
        }
        catch (FirstVersionWithPatchdException e) {
            isCorrect=true;
        }
        assertTrue(isCorrect);
    }

    /**
     * test not correted data in DB
     *
     * @throws Exception on error
     */
    public void testConfig_5_moduleNotConfigured() throws Exception {

        InitManagerDaoFactory.initNotExistedModule();

        String path = DBREVISION_CONFIGS+File.separatorChar+"config-5";
        LocalDatabase localDatabaseAdapter = new LocalDatabase(null);
        boolean isCorrect=false;
        try {
            new DbRevisionManager(localDatabaseAdapter, path);
        }
        catch (ModuleNotConfiguredException e) {
            isCorrect=true;
        }
        assertTrue(isCorrect);
    }

    /**
     * test not correted data in DB
     *
     * @throws Exception on error
     */
    public void testConfig_5_moduleVersionNotConfigured() throws Exception {
        
        InitManagerDaoFactory.initNotExistedVersionModule();

        String path = DBREVISION_CONFIGS+File.separatorChar+"config-5";
        LocalDatabase localDatabaseAdapter = new LocalDatabase(null);
        boolean isCorrect=false;
        try {
            new DbRevisionManager(localDatabaseAdapter, path);
        }
        catch (CurrentVersionCodeNotFoundException e) {
            isCorrect=true;
        }
        assertTrue(isCorrect);
    }

    /**
     * test correted data in DB
     *
     * @throws Exception on error
     */
    public void testConfigValid_currentVersionInMiddleOfList() throws Exception {

        InitManagerDaoFactory.initCurrentVersionInMiddleOfList();

        String path = DBREVISION_CONFIGS+File.separatorChar+"config-valid";
        LocalDatabase localDatabaseAdapter = new LocalDatabase(null);
        DbRevisionManager manager = new DbRevisionManager(localDatabaseAdapter, path);

        List<Module> modules = manager.getModules();
        assertNotNull(modules);
        assertEquals(1, modules.size());

        Module module = modules.get(0);
        assertNotNull(module);

        List<Version> versions = module.getVersions();
        assertNotNull(versions);
        assertEquals(4, versions.size());

        Version currentVersion = module.getCurrentVersion();
        assertNotNull(currentVersion);
        assertEquals("5.7.2", currentVersion.getVersionName());
        assertEquals("5.8.0", module.getLastVersion().getVersionName());
        assertEquals("5.7.0", module.getFirstVersion().getVersionName());

        assertNotNull(currentVersion.getPreviousVersion());
        assertNotNull(currentVersion.getNextVersion());
        assertEquals("5.7.1", currentVersion.getPreviousVersion().getVersionName());
        assertEquals("5.8.0", currentVersion.getNextVersion().getVersionName());

        Version v = currentVersion;
        while (v!=null) {
            assertTrue(v.isComplete());
            v = v.getPreviousVersion();
        }

        v = currentVersion.getNextVersion();
        while (v!=null) {
            assertFalse(v.isComplete());
            v = v.getNextVersion();
        }
    }

    /**
     * test correted data in DB
     *
     * @throws Exception on error
     */
    public void testConfigValid_lastVersion() throws Exception {

        InitManagerDaoFactory.initCurrentVersionIsLast();

        String path = DBREVISION_CONFIGS+File.separatorChar+"config-valid";
        LocalDatabase localDatabaseAdapter = new LocalDatabase(null);
        DbRevisionManager manager = new DbRevisionManager(localDatabaseAdapter, path);

        List<Module> modules = manager.getModules();
        assertNotNull(modules);
        assertEquals(1, modules.size());

        Module module = modules.get(0);
        assertNotNull(module);
        assertTrue(module.isComplete());

        List<Version> versions = module.getVersions();
        assertNotNull(versions);
        assertEquals(4, versions.size());

        Version currentVersion = module.getCurrentVersion();
        assertNotNull(currentVersion);
        assertEquals("5.8.0", currentVersion.getVersionName());
        assertEquals("5.7.2", currentVersion.getPreviousVersion().getVersionName());
        assertEquals("5.8.0", module.getLastVersion().getVersionName());
        assertEquals("5.7.0", module.getFirstVersion().getVersionName());

        for (Version version : versions) {
            assertTrue(version.isComplete());
        }
    }

    /**
     * test correted data in DB
     *
     * @throws Exception on error
     */
    public void testConfigValid_1() throws Exception {

        String path = DBREVISION_CONFIGS+File.separatorChar+"config-valid-1";
        LocalDatabase localDatabaseAdapter = new LocalDatabase(null);
        DbRevisionManager manager = new DbRevisionManager(localDatabaseAdapter, path);

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

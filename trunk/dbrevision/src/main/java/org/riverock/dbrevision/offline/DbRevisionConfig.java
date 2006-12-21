/*
 * org.riverock.dbrevision - Database revision engine
 * For more information about DbRevision, please visit project site
 * http://www.riverock.org
 *
 * Copyright (C) 2006-2006, Riverock Software, All Rights Reserved.
 *
 * Riverock - The Open-source Java Development Community
 * http://www.riverock.org
 *
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.riverock.dbrevision.offline;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import org.riverock.dbrevision.offline.config.DatabaseConnectionType;
import org.riverock.dbrevision.offline.config.GenericConfigType;

/**
 * $Id: DbRevisionConfig.java 1141 2006-12-14 14:43:29Z serg_main $
 */
public final class DbRevisionConfig {
    private final static Logger log = Logger.getLogger(DbRevisionConfig.class);

    private static final String NAME_CONFIG_FILE = "dbrevision.xml";

    private static GenericConfigType configObject = null;
    private static Map<String, DatabaseConnectionType> dbConfig = null;

    private static boolean isConfigProcessed = false;
    private static String defaultConnectionName = null;
    private static String genericDebugDir = null;

    private static void readConfig() {

        if (isConfigProcessed) {
            return;
        }

        synchronized (DbRevisionConfig.class) {
            if (isConfigProcessed) {
                return;
            }
            configObject = ConfigObject.loadConfigFile( NAME_CONFIG_FILE );

            if (dbConfig != null) {
                dbConfig.clear();
                dbConfig = null;
            }

            // config not found as init parameter in web.xml file or as JDNI reference
            if (configObject ==null) {
                throw new ConfigException("generic config object is null");
            }

            if (log.isDebugEnabled()) {
                log.debug("#15.006");
            }

            dbConfig = new HashMap<String, DatabaseConnectionType>();
            for (DatabaseConnectionType dbc : configObject.getDatabaseConnection()) {
                dbConfig.put(dbc.getName(), dbc);
            }

            if (log.isInfoEnabled()) {
                log.info("Name default DB connect " + configObject.getDefaultConnectionName());
            }

            isConfigProcessed = true;
        }
    }

//-----------------------------------------------------
// PUBLIC SECTION
//-----------------------------------------------------

    private final static Object syncDebugDir = new Object();

    public static void setGenericDebugDir(String genericDebugDir) {
        synchronized(syncDebugDir) {
            String dir = genericDebugDir;
            File dirTest = new File(dir);
            if (!dirTest.exists()) {
                log.warn("Specified debug directory '" + dir + "' not exists. Set to default java input/output temp directory");
                dir = System.getProperty("java.io.tmpdir");
            }

            if (!dirTest.canWrite()) {
                log.warn("Specified debug directory '" + dir + "' not writable. Set to default java input/output temp directory");
                dir = System.getProperty("java.io.tmpdir");
            }

            DbRevisionConfig.genericDebugDir = dir;
        }
    }

    public static String getGenericDebugDir() {

        if (genericDebugDir==null) {
            synchronized(syncDebugDir) {
                if (genericDebugDir==null) {
                    genericDebugDir = System.getProperty("java.io.tmpdir");
                }
            }
        }

        return genericDebugDir;
    }

    public static DatabaseConnectionType getDatabaseConnection(final String connectionName) {

        if (log.isDebugEnabled()) log.debug("#15.909");

        if (!isConfigProcessed) readConfig();

        if (log.isDebugEnabled()) log.debug("#15.910");

        return dbConfig.get(connectionName);
    }

    public static void setDefaultConnectionName(final String defaultConnectionName_) {
        defaultConnectionName = defaultConnectionName_;
    }

    public static String getDefaultConnectionName() {

        // if defaultConnectionName is overrided, then return new value(not from config)
        if (defaultConnectionName != null)
            return defaultConnectionName;

        if (log.isDebugEnabled()) log.debug("#15.951");
        if (!isConfigProcessed) readConfig();
        if (log.isDebugEnabled()) log.debug("#15.952");

        return configObject.getDefaultConnectionName();
    }
}

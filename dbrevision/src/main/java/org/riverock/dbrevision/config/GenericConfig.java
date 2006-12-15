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
package org.riverock.dbrevision.config;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import org.riverock.dbrevision.annotation.schema.config.DatabaseConnectionType;
import org.riverock.dbrevision.annotation.schema.config.DateTimeSavingType;
import org.riverock.dbrevision.annotation.schema.config.GenericConfigType;
import org.riverock.dbrevision.annotation.schema.config.PropertyType;

/**
 * $Id: GenericConfig.java 1141 2006-12-14 14:43:29Z serg_main $
 */
public final class GenericConfig {
    private final static Logger log = Logger.getLogger(GenericConfig.class);

    public static final String CONFIG_FILE_PARAM_NAME = "generic-config-file";

    private static final String NAME_CONFIG_FILE = "-generic.xml";
    private static String configPrefix = "jsmithy";
    public final static String JNDI_GENERIC_CONFIG_FILE = "jsmithy/generic/ConfigFile";

    public final static String JNDI_DB_NAME = "webmill/db-name";
    public final static String JNDI_DB_FAMALY = "webmill/db-famaly";

    private static ConfigObject configObject = null;
    private static Map<String, DatabaseConnectionType> dbConfig = null;
    private static TimeZone currentTimeZone = null;

    private static boolean isConfigProcessed = false;
    private static String defaultConnectionName = null;
    private static String genericDebugDir = null;

    public static String getConfigPrefix() {
        return configPrefix;
    }

    public static void setConfigPrefix(final String configPrefix) {
        GenericConfig.configPrefix = configPrefix;
    }

    public static GenericConfigType getConfig() {
        return (GenericConfigType) configObject.getConfigObject();
    }

    private final static Object syncReadConfig = new Object();

    private static void readConfig() {

        if (isConfigProcessed) return;

        synchronized (syncReadConfig) {
            if (isConfigProcessed)
                return;

            configObject = ConfigObject.load(
                JNDI_GENERIC_CONFIG_FILE, CONFIG_FILE_PARAM_NAME, configPrefix + NAME_CONFIG_FILE, GenericConfigType.class
            );

            if (dbConfig != null) {
                dbConfig.clear();
                dbConfig = null;
            }

            // config not found as init parameter in web.xml file or as JDNI reference
            if (getConfig()==null) {
                throw new ConfigException("generic config object is null");
            }

            if (log.isDebugEnabled()) {
                log.debug("#15.006");
            }

            dbConfig = new HashMap<String, DatabaseConnectionType>();
            for (DatabaseConnectionType dbc : getConfig().getDatabaseConnection()) {
                dbConfig.put(dbc.getName(), dbc);
            }

            if (log.isInfoEnabled()) {
                log.info("Name default DB connect " + getConfig().getDefaultConnectionName());
            }

            isConfigProcessed = true;
        }
    }

//-----------------------------------------------------
// PUBLIC SECTION
//-----------------------------------------------------

    private final static Object syncTZ = new Object();

    public static TimeZone getTZ() throws ConfigException {

        if (log.isDebugEnabled()) log.debug("GenericConfig.getTZ() #1");
        if (!isConfigProcessed) readConfig();
        if (currentTimeZone != null) return currentTimeZone;

        if (log.isDebugEnabled()) log.debug("GenericConfig.getTZ() #3 Set new TimeZone");

        synchronized (syncTZ) {

            if (currentTimeZone != null) return currentTimeZone;

            DateTimeSavingType dts = getConfig().getDTS();
            if (dts.getTimeZoneName() != null) {
                String nameTimeZone = dts.getTimeZoneName();

                // work around NPE in TimeZone.getTimeZone
                if (nameTimeZone != null && nameTimeZone.trim().length() != 0) {

                    if (log.isDebugEnabled())
                        log.debug("GenericConfig.getTZ(). Set TimeZone to " + nameTimeZone);

                    TimeZone tz = TimeZone.getTimeZone(nameTimeZone);
                    if (tz == null) {
                        log.fatal("TimeZone '" + nameTimeZone + "' not found. You must correct NameTimeZone element in config file");
                        return null;
                    }
                    currentTimeZone = tz;
                    TimeZone.setDefault(currentTimeZone);
                    return tz;
                }
                log.fatal("<NameTimeZone> element not found. You must correct config file.");
                return null;
            } else {
                currentTimeZone =
                    new SimpleTimeZone(
                        dts.getRawOffset(),
                        dts.getId(),
                        dts.getStart().getMonth(),
                        dts.getStart().getDay(),
                        dts.getStart().getDayOfWeek(),
                        dts.getStart().getTime(),
                        dts.getEnd().getMonth(),
                        dts.getEnd().getDay(),
                        dts.getEnd().getDayOfWeek(),
                        dts.getEnd().getTime()
                    );
                TimeZone.setDefault(currentTimeZone);
                return currentTimeZone;
            }
//            log.fatal("<DTS> element present, but is empty. You must correct <DTS> element in config file.");
//            return null;
        }
    }

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

            GenericConfig.genericDebugDir = dir;
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

        return getConfig().getDefaultConnectionName();
    }

    public static List<PropertyType> getProperty() {

        if (log.isDebugEnabled()) log.debug("#16.951");
        if (!isConfigProcessed) readConfig();
        if (log.isDebugEnabled()) log.debug("#16.952");

        return getConfig().getProperty();
    }
}

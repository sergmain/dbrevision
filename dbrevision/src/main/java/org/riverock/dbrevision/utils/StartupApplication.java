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
package org.riverock.dbrevision.utils;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import org.riverock.dbrevision.config.ConfigException;
import org.riverock.dbrevision.config.PropertiesProvider;

/**
 * @author Sergei Maslyukov
 *         Date: 14.12.2006
 *         Time: 17:22:20
 *         <p/>
 *         $Id$
 */
public class StartupApplication {
    private final static Logger log = Logger.getLogger(StartupApplication.class);

    private static boolean isInit = false;

    private final static String DEFAULT_DIR_NAME = "cfg";

    public static void init() throws ConfigException {
        init(DEFAULT_DIR_NAME, "log4j.properties", "riverock");
    }

    public static void init(String defaultNameDir, String log4jFileName, String configPrefix)
        throws ConfigException {
        if (!isInit) {
            PropertiesProvider.setApplicationPath(
                System.getProperties().getProperty("user.dir")
            );

            PropertiesProvider.setConfigPath(
                PropertiesProvider.getApplicationPath() +
                    (PropertiesProvider.getApplicationPath().endsWith(File.separator) ? "" : File.separator) +
                    defaultNameDir
            );

            PropertiesProvider.setIsServletEnv(false);

            String millLogPath = PropertiesProvider.getConfigPath() + File.separatorChar + "log";
            File tempDir = new File(millLogPath);
            if (!tempDir.exists()) {
                tempDir.mkdir();
            }
            System.setProperty("mill.logging.path", millLogPath);
            System.setProperty("riverock.logging.path", millLogPath);

            PropertyConfigurator.configure(
                PropertiesProvider.getConfigPath() + File.separatorChar + log4jFileName
            );
            log.info("Application path: " + PropertiesProvider.getApplicationPath());

            isInit = true;
        }
    }

}

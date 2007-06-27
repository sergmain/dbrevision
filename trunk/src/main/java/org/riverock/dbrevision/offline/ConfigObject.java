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
import java.io.FileInputStream;

import org.apache.log4j.Logger;

import org.riverock.dbrevision.offline.config.GenericConfigType;
import org.riverock.dbrevision.utils.Utils;

/**
 * @author Sergei Maslyukov
 *         Date: 14.12.2006
 *         Time: 17:05:46
 *         <p/>
 *         $Id$
 */
public class ConfigObject {
    private static Logger log = Logger.getLogger(ConfigObject.class);

    public static GenericConfigType loadConfigFile(String nameConfigFile) {
        if (PropertiesProvider.getConfigPath()==null) {
            String es = "Config path not resolved";
            log.fatal(es);
            throw new IllegalStateException(es);
        }
        if (log.isDebugEnabled())
            log.debug("#15.100");

        String fullPath = PropertiesProvider.getConfigPath() +
            (PropertiesProvider.getConfigPath().endsWith(File.separator) ? "" : File.separator) +
            nameConfigFile;

        if (log.isDebugEnabled()) {
            log.debug("#15.101" + fullPath);
        }

        if (fullPath == null) {
            String errorString = "name of config file not determinated";
            log.error(errorString);
            throw new IllegalArgumentException(errorString);
        }

        fullPath = fullPath.replace( File.separatorChar == '/' ? '\\' : '/', File.separatorChar );

        if (log.isInfoEnabled()) {
            log.info("nameConfigFile: " + fullPath);
        }

        File configFile = new File(fullPath);

        if (!configFile.exists()) {
            String errorString = "Config file '" + configFile.getName() + "' not found";
            log.error(errorString);
            throw new IllegalArgumentException(errorString);
        }

        if (log.isDebugEnabled())
            log.debug("Start unmarshalling file " + nameConfigFile);

        try {
            System.out.println("Start unmarshal config file: " + configFile);
            FileInputStream stream = new FileInputStream(configFile);
            return Utils.getObjectFromXml(GenericConfigType.class, stream);
        }
        catch (Throwable e) {
            String es = "Error while unmarshalling config file ";
            log.fatal(es, e);
            throw new ConfigException(es, e);
        }
    }
}

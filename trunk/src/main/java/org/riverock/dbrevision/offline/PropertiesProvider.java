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

import java.util.Map;
import java.util.HashMap;

/**
 * @author Sergei Maslyukov
 *         Date: 14.12.2006
 *         Time: 17:24:53
 *         <p/>
 *         $Id$
 */
public class PropertiesProvider {
    private static boolean isServletEnv = false;
    private static String applicationPath = null;
    private static String configPath = null;
    private static Map<String, String> parameters = new HashMap<String, String>();

    public static String getConfigPath() {
        return configPath;
    }

    public static void setConfigPath(String configPath) {
        PropertiesProvider.configPath = configPath;
    }

    public static String getApplicationPath() {
        return applicationPath;
    }

    public static void setApplicationPath(String applicationPath) {
        PropertiesProvider.applicationPath = applicationPath;
    }

    public static boolean getIsServletEnv() {
        return isServletEnv;
    }

    public static void setIsServletEnv(boolean servletEnv) {
        isServletEnv = servletEnv;
    }

    public static Map<String, String> getParameters() {
        return parameters;
    }

    public static void setParameters(Map<String, String> parameters) {
        PropertiesProvider.parameters = parameters;
    }

    public static String getParameter(String key) {
        if (key==null) {
            return null;
        }
        return PropertiesProvider.parameters.get( key );
    }
}

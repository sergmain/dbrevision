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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Iterator;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

/**
 * @author Sergei Maslyukov
 *         Date: 14.12.2006
 *         Time: 17:05:46
 *         <p/>
 *         $Id$
 */
public class ConfigObject {
    private static Logger log = Logger.getLogger(ConfigObject.class);
    public static final String LOCAL_CONFIG_PARAM_NAME = "is-local-config";

    private String nameConfigFile = null;

    private Object configObject = null;

    public ConfigObject() {
    }

    public Object getConfigObject() {
        return configObject;
    }

    public static ConfigObject load(String nameJndiCtx, String nameConfigParam, String nameConfigFile_, Class configClass) {
        ConfigObject config = new ConfigObject();
        File configFile;
        if (PropertiesProvider.getIsServletEnv()) {
            boolean isLocal = "true".equalsIgnoreCase(PropertiesProvider.getParameter(LOCAL_CONFIG_PARAM_NAME));
            if (isLocal) {
                config.nameConfigFile =
                    PropertiesProvider.getApplicationPath() +
                        File.separatorChar +
                        "WEB-INF" +
                        File.separatorChar +
                        PropertiesProvider.getParameter(nameConfigParam);
            } else {
                if (log.isDebugEnabled()) {
                    ClassLoader cl = Thread.currentThread().getContextClassLoader();
                    log.debug("classLoader: " + cl + "\nhash: " + cl.hashCode());
                }
                String name = "java:comp/env/" + nameJndiCtx;
                try {
                    InitialContext ic = new InitialContext();
                    config.nameConfigFile = (String) ic.lookup(name);
                }
                catch (NamingException e) {
                    String es = "Error get value from JDNI context. Name: " + name;
                    try {
                        InitialContext ic = new InitialContext();
                        Map map = ic.getEnvironment();
                        Iterator<Map.Entry> iterator = map.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry entry = iterator.next();
                            log.error("key: " + entry.getKey() + ", value: " + entry.getValue());
                        }
                    }
                    catch (Throwable th) {
                        log.error("erorr", th);
                    }
                    log.error(es, e);
                    throw new ConfigException(es, e);
                }
            }
            if (config.nameConfigFile == null) {
                log.warn("Config file not defined. isLocal: " + isLocal);
            }
        } else {
            if (PropertiesProvider.getConfigPath() == null) {
                String es = "Config path not resolved";
                log.fatal(es);
                throw new IllegalStateException(es);
            }
            if (log.isDebugEnabled())
                log.debug("#15.100");

            String defURL = PropertiesProvider.getConfigPath() +
                (PropertiesProvider.getConfigPath().endsWith(File.separator) ? "" : File.separator) +
                nameConfigFile_;

            if (log.isDebugEnabled())
                log.debug("#15.101" + defURL);

            config.nameConfigFile = defURL;
        }

        if (config.nameConfigFile == null) {
            String errorString = "name of config file not determinated";
            log.error(errorString);
            throw new IllegalArgumentException(errorString);
        }

        config.nameConfigFile = config.nameConfigFile.replace(
            File.separatorChar == '/' ? '\\' : '/', File.separatorChar
        );

        if (log.isInfoEnabled()) {
            log.info("nameConfigFile: " + config.nameConfigFile);
        }

        configFile = new File(config.nameConfigFile);

        if (!configFile.exists()) {
            String errorString = "Config file '" + configFile.getName() + "' not found";
            log.error(errorString);
            throw new IllegalArgumentException(errorString);
        }


        if (log.isDebugEnabled())
            log.debug("Start unmarshalling file " + nameConfigFile_);

        try {
            System.out.println("Start unmarshal config file: " + configFile);
            FileInputStream stream = new FileInputStream(configFile);
            config.configObject = unmarshal(stream, configClass);
        }
        catch (Throwable e) {
            String es = "Error while unmarshalling config file ";
            log.fatal(es, e);
            throw new ConfigException(es, e);
        }

        return config;
    }

    private static Object unmarshal(FileInputStream configFile, Class clazz) throws JAXBException, FileNotFoundException {
        JAXBContext jaxbContext = JAXBContext.newInstance ( clazz.getPackage().getName());

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Source source =  new StreamSource( configFile );
        return unmarshaller.unmarshal( source, clazz).getValue();
    }
}

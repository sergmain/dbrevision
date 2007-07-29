package org.riverock.dbrevision.manager.config;

/**
 * User: SergeMaslyukov
 * Date: 29.07.2007
 * Time: 16:55:52
 */
public class ConfigParserFactory {
    private static final ConfigParser CONFIG_PARSER = new SimpleConfigParserImpl();

    public static ConfigParser getConfigParser() {
        return CONFIG_PARSER;
    }
}

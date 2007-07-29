package org.riverock.dbrevision.manager.config;

import org.apache.commons.digester.Digester;
import org.riverock.dbrevision.exception.ConfigParseException;
import org.riverock.dbrevision.manager.Config;
import org.riverock.dbrevision.manager.ModuleConfig;

import java.io.InputStream;

/**
 * User: SergeMaslyukov
 * Date: 29.07.2007
 * Time: 16:55:13
 */
public class SimpleConfigParserImpl implements ConfigParser {

    private static Digester digester = null;
    static {
        digester = new Digester();
        digester.setValidating(false);

/*
<DbRevision>
    <Module>
        <Description>Webmill portal</Description>
        <Name>webmill</Name>
        <Versions>
            <Version>5.7.0</Version>
            <Version>5.7.1</Version>
            <Version>5.7.2</Version>
            <Version>5.8.0</Version>
        </Versions>
    </Module>
</DbRevision>
*/
        digester.addObjectCreate("DbRevision", Config.class);

        digester.addObjectCreate("DbRevision/Module", ModuleConfig.class);
        digester.addBeanPropertySetter("DbRevision/Module/Description", "description");
        digester.addBeanPropertySetter("DbRevision/Module/Name", "name");
        digester.addSetNext("DbRevision/Module", "addModule" );

        digester.addCallMethod("DbRevision/Module/Versions/Version", "addVersion", 0, new Class[]{String.class});

    }
    
    public Config parse(InputStream inputStream) {
        Config st;
        try {
            st = (Config)digester.parse(inputStream);
        }
        catch (Exception e) {
            throw new ConfigParseException(e);
        }
        return st;  
    }
}

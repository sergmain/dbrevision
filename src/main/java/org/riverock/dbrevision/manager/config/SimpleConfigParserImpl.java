package org.riverock.dbrevision.manager.config;

import org.apache.commons.digester.Digester;
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

        digester.addObjectCreate("DbRevision", Config.class);

        digester.addObjectCreate("DbRevision/Module", ModuleConfig.class);
        digester.addSetProperties("DbRevision/Module", "Description", "type");
        digester.addSetProperties("DbRevision/Module", "value", "value");
        digester.addSetProperties("DbRevision/Module", "code", "code");
        digester.addSetProperties("DbRevision/Module", "xmlRoot", "xmlRoot");
        digester.addSetProperties("DbRevision/Module", "role", "role");
        digester.addSetProperties("DbRevision/Module", "template", "template");
        digester.addSetNext("DbRevision/Module", "addModule" );

/*
        digester.addObjectCreate("DbRevision/Module/Parameter", PortalTemplateParameterImpl.class);
        digester.addSetProperties("DbRevision/Module/Parameter", "name", "name");
        digester.addSetProperties("DbRevision/Module/Parameter", "value", "value");
        digester.addSetNext("DbRevision/Module/Parameter", "addParameter");
*/

    }
    
    public Config parse(InputStream inputStream) {
//        Config st = (Config)digester.parse( new FileInputStream(inputStream));
        return null;  
    }
}

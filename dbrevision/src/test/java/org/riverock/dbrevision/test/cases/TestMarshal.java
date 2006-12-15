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
package org.riverock.dbrevision.test.cases;

import java.io.FileInputStream;

import junit.framework.TestCase;

import org.riverock.dbrevision.annotation.schema.db.DbSchema;
import org.riverock.dbrevision.config.GenericConfig;
import org.riverock.dbrevision.db.DatabaseAdapter;
import org.riverock.dbrevision.db.DatabaseManager;
import org.riverock.dbrevision.utils.Utils;
import org.riverock.dbrevision.utils.StartupApplication;

/**
 * User: Admin
 * Date: Dec 19, 2002
 * Time: 11:41:42 AM
 * <p/>
 * $Id: TestMarshal.java 1141 2006-12-14 14:43:29Z serg_main $
 */
public class TestMarshal extends TestCase {
    public TestMarshal(String testName) {
        super(testName);
    }

    private DbSchema makeSchema(String nameConnection, String nameOutputFiel)
        throws Exception {
        DatabaseAdapter db_=null;
//        db_ = DatabaseAdapter.getInstance(nameConnection);
        DbSchema schema = DatabaseManager.getDbStructure(db_);

        String encoding = "utf-8";
        String outputSchemaFile = GenericConfig.getGenericDebugDir() + nameOutputFiel;
        System.out.println("Marshal data to file " + outputSchemaFile);

        Utils.writeToFile(schema, outputSchemaFile, encoding);

        return schema;
    }

    private String fileName = "webmill-schema-test.xml";

    public void doTest() throws Exception {
        StartupApplication.init();

        makeSchema("ORACLE", fileName);

        System.out.println("Unmarshal data from file");
        FileInputStream stream = new FileInputStream(GenericConfig.getGenericDebugDir() + fileName);
        Utils.getObjectFromXml(DbSchema.class, stream);
    }

    public static void main(String args[])
        throws Exception {
        TestMarshal test = new TestMarshal("test");
        test.doTest();
    }
}

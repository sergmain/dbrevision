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
package org.riverock.dbrevision.test;

import org.apache.log4j.Logger;

/**
 * Author: mill
 * Date: Mar 20, 2003
 * Time: 10:08:39 AM
 *
 * $Id: TestSchemaImport.java 1042 2006-11-14 14:24:45Z serg_main $
 */
public class TestSchemaImport
{
    private static Logger cat = Logger.getLogger( "org.riverock.test.TestSchemaImport" );

    public TestSchemaImport()
    {
    }
/*
    public static void main(String[] s)
        throws Exception
    {
        ShootType shoot = new ShootType();
        ShootItemType item1 = new ShootItemType();
        item1.setShootName("t1");
        item1.setShootNumber(1);
        ShootItemType item2 = new ShootItemType();
        item2.setShootName("t2");
        item2.setShootNumber(2);
        ShootItemType item3 = new ShootItemType();
        item3.setShootName("t3");
        item3.setShootNumber(3);

        shoot.addItem( item1 );
        shoot.addItem( item2 );
        shoot.addItem( item3 );

        String[][] ns = new String[][]
        {
            { "mill-core", "http://webmill.askmore.info/mill/xsd/mill-core.xsd" },
            { "mill-auth", "http://webmill.askmore.info/mill/xsd/mill-auth.xsd" },
            { "prr-core", "http://prr.esrr.mps/xsd/prr-core.xsd" },
            { "prr-complex", "http://prr.esrr.mps/xsd/prr-complex.xsd" }
        };
        writeToFile(shoot, "c:\\opt1\\test-import-schema.xml", "utf-8", null, true, ns);
    }

    public static void writeToFile(Object obj, String fileName, String encoding, String root,
                                   boolean isValidate, String namespace[][])
        throws Exception
    {
        FileOutputStream fos = new FileOutputStream( fileName );
        Marshaller marsh = new Marshaller( new OutputStreamWriter(fos, encoding) );
        if (root!=null)
            marsh.setRootElement( root );

        marsh.setValidation( isValidate );
        marsh.setMarshalAsDocument(true);
        marsh.setEncoding(encoding);
        marsh.setSchemaLocation(
            "http://prr.esrr.mps/xsd/prr-complex.xsd E:\\sandbox\\millengine\\src\\xsd\\test\\prr_complex_only.xsd"
        );
        marsh.setSuppressXSIType( false );
        marsh.setValidation( isValidate );
        if (namespace!=null)
        {
            for (int i=0; i<namespace.length; i++)
                marsh.setNamespaceMapping(namespace[i][0], namespace[i][1]);
        }
        marsh.marshal(obj);
        fos.flush();
        fos.close();
        fos = null;
    }
*/
}

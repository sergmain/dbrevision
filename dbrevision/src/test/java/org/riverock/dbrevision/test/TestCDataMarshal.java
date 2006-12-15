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

import org.riverock.dbrevision.utils.StartupApplication;

/**
 * Author: mill
 * Date: Apr 9, 2003
 * Time: 2:08:35 PM
 * <p/>
 * $Id: TestCDataMarshal.java 1141 2006-12-14 14:43:29Z serg_main $
 */
public class TestCDataMarshal {
    public String text = "";

    public TestCDataMarshal() {
    }

    public static void main(String args[]) throws Exception {
        long mills = System.currentTimeMillis();
        StartupApplication.init();

        String tempFile = "c:\\temp\\cdata-test.xml";

//        <meta name="robots" content="index,follow"/>


        TestCDataMarshal cdata = new TestCDataMarshal();
        cdata.text = "&lt;![CDATA[&lt;a href=&quot;http://baikal.askmore.info&quot;>Prebaikalsky National Park&lt;/a>]]>";

/*
        writeToFile(cdata, tempFile, "utf-8", null, false, null);

        InputSource inCurrSrc = new InputSource(new FileInputStream(tempFile));
        TestCDataMarshal cdataResult = (TestCDataMarshal) Unmarshaller.unmarshal(TestCDataMarshal.class, inCurrSrc);
*/
    }
}

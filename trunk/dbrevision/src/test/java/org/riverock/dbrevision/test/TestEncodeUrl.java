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

/**
 * User: Admin
 * Date: Jan 29, 2003
 * Time: 12:21:44 AM
 *
 * $Id: TestEncodeUrl.java 1042 2006-11-14 14:24:45Z serg_main $
 */
public class TestEncodeUrl
{
    public static void main(String args[])
        throws Exception
    {
        String s =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
            "<SiteListSite IdSite=\"16\" IdFirm=\"1\" IsCssDynamic=\"true\" IsRegisterAllowed=\"false\" IsActivateEmailOrder=\"true\"><DefLanguage>ru</DefLanguage><DefCountry>RU</DefCountry><DefVariant></DefVariant><NameSite>Mill Engine</NameSite><AdminEmail></AdminEmail><CssFile>/front_styles.css</CssFile><OrderEmail>'tiger@localhost'%</OrderEmail></SiteListSite>";

//        System.out.println( URIUtil.encodeQuery( s ) );

        String s1 = "%3C%3Fxml%20version%3D%221.0%22%20encoding%3D%22UTF-8%22%3F%3E%3CSiteListSite%20IdSite%3D%2216%22%20IdFirm%3D%221%22%20IsCssDynamic%3D%22true%22%20IsRegisterAllowed%3D%22false%22%20IsActivateEmailOrder%3D%22true%22%3E%3CDefLanguage%3Eru%3C%2FDefLanguage%3E%3CDefCountry%3ERU%3C%2FDefCountry%3E%3CDefVariant%3E%3C%2FDefVariant%3E%3CNameSite%3EMill%20Engine%3C%2FNameSite%3E%3CAdminEmail%3E%3C%2FAdminEmail%3E%3CCssFile%3E%2Ffront_styles.css%3C%2FCssFile%3E%3COrderEmail%3Etiger%40localhost%3C%2FOrderEmail%3E%3C%2FSiteListSite%3E";
//        System.out.println( URIUtil.decode( s1 ) );
    }
}

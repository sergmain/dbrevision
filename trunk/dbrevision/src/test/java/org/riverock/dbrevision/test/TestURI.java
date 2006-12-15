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

import java.net.URL;

/**
 * User: Admin
 * Date: Sep 14, 2003
 * Time: 10:00:05 PM
 *
 * $Id: TestURI.java 1042 2006-11-14 14:24:45Z serg_main $
 */
public class TestURI
{
//    private static String addr = "http://me.askmore:8080/mill/ctx?aaa=bbb&ccc=ddd";
    private static String addr = "http://me.askmore";

    public static void main(String args[])
        throws Exception
    {
        URL url = new URL(addr);
        System.out.println( url.toString());
        System.out.println( "getProtocol " + url.getProtocol() );
        System.out.println( "getHost " + url.getHost() );
        System.out.println( "getPort " + url.getPort() );
        System.out.println( "getPath " + url.getPath() );
        System.out.println( "getFile " + url.getFile() );
        System.out.println( "getQuery " + url.getQuery() );
        System.out.println( "getRef " + url.getRef() );
    }
}

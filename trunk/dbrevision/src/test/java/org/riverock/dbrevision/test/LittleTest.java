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
 * Date: Jan 25, 2003
 * Time: 9:57:52 PM
 *
 * $Id: LittleTest.java 1042 2006-11-14 14:24:45Z serg_main $
 */
public class LittleTest
{

    public byte LitleTest()
    {
//        byte b; b = (b + 5);
//        float f = 3.1;

        double d = 1.0 + 0.2 - 1.0;
        return 0;
    }

    public static void main(String s[])
        throws Exception
    {
        Class c = Void.class;

        float f = 30;
        Integer i = new Integer((int)(30 * ((f+100)/100)) );

        System.out.println("i - "+i.intValue());

        System.out.println("Class name - "+c.getName());
//        byte b = '\l';
    }
}

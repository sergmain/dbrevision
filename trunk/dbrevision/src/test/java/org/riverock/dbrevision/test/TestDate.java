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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.riverock.dbrevision.utils.Utils;
import org.riverock.dbrevision.utils.StartupApplication;

/**
 * User: Admin
 * Date: Dec 12, 2002
 * Time: 11:07:46 AM
 *
 * $Id: TestDate.java 1141 2006-12-14 14:43:29Z serg_main $
 */
public class TestDate
{
    public static void main(String args[])
        throws Exception
    {
        StartupApplication.init();

        String mask = "dd.MMM.yyyy HH:mm:ss";

        Calendar cal = GregorianCalendar.getInstance();
        String currDate = Utils.getStringDate( new GregorianCalendar(), mask, Locale.ENGLISH );

        System.out.println("current date/time "+currDate);

        currDate = Utils.getStringDate( cal, mask );
        System.out.println(currDate);

        Date date = cal.getTime();

        Calendar cal1 = new GregorianCalendar();
        cal1.setTime( date );

        System.out.println( Utils.getStringDate( cal1, mask, new Locale("en", "US") ) );

//        Locale loc = new Locale("en_US");
        Locale loc = new Locale("en", "US","Test");
//        Locale loc = Locale.UK;

        SimpleDateFormat df = new SimpleDateFormat(mask,  loc);
        df.setTimeZone(cal1.getTimeZone());
        System.out.println(df.format(cal1.getTime()));
    }
}

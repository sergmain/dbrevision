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
package org.riverock.dbrevision.utils;

import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Marshaller;
import javax.xml.bind.JAXBElement;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

/**
 * @author Sergei Maslyukov
 *         Date: 14.12.2006
 *         Time: 16:37:21
 *         <p/>
 *         $Id$
 */
public class Utils {
    private final static Logger log = Logger.getLogger(Utils.class);

    public static java.sql.Timestamp getCurrentTime() {
        return new Timestamp(System.currentTimeMillis());
    }
    public static String getStringDate( final Calendar c, final String mask, final Locale loc ) {
        if (c == null) return null;
        return DateFormatUtils.format(c.getTimeInMillis(), mask, c.getTimeZone(), loc);
    }

    public static String getStringDate( final Calendar c, final String mask ) {
        return DateFormatUtils.format(c.getTimeInMillis(), mask, c.getTimeZone(), Locale.ENGLISH);
    }

    public static java.util.Date getDateWithMask( final String date, final String mask )
        throws java.text.ParseException {
        if (date == null || mask == null)
            return null;

        SimpleDateFormat dFormat = new SimpleDateFormat(mask);

        return dFormat.parse(date);
    }

    public static String getStringDate( final java.util.Date date, final String mask, final Locale loc, final TimeZone tz) {
        if (date == null) return null;

        SimpleDateFormat df = new SimpleDateFormat(mask, loc);
        df.setTimeZone( tz );

        return df.format( date );
    }

    public static byte[] getXml(final Object obj, final String rootElement) throws Exception {
        return getXml(obj, rootElement, "utf-8");
    }

    public static byte[] getXml(final Object obj, final String rootElement, final String encoding) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("getXml(). Object to marshaling " + obj);
            log.debug("getXml(). rootElement " + rootElement);
            log.debug("getXml(). encoding " + encoding);
        }
        ByteArrayOutputStream fos=null;
        try {
            fos = new ByteArrayOutputStream(1000);

            if (log.isDebugEnabled())
                log.debug("ByteArrayOutputStream object - " + fos);

            JAXBContext jaxbContext = JAXBContext.newInstance ( obj.getClass().getPackage().getName() );
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);

            if (rootElement != null && rootElement.trim().length() > 0) {
                // http://weblogs.java.net/blog/kohsuke/archive/2005/10/101_ways_to_mar.html
                marshaller.marshal( new JAXBElement(new QName("",rootElement), obj.getClass(), obj ), fos);
            }
            else {
                marshaller.marshal(obj, fos);
            }

            fos.flush();
            fos.close();

            return fos.toByteArray();
        }
        catch (Exception e) {
            log.error("Exception when marshaling object", e);
            throw e;
        }
        catch (Error err) {
            log.error("Error when marshaling object", err);
            throw err;
        }
        finally {
            fos.close();
            fos = null;
        }
    }

    public static void writeToFile(final Object obj, final String fileName)
        throws Exception {

        writeToFile(obj, fileName, "utf-8");
    }

    public static void writeToFile(final Object obj, final String fileName, final String encoding)
        throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance ( obj.getClass().getPackage().getName() );
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
        FileOutputStream fos = new FileOutputStream(fileName);
        marshaller.marshal(obj, fos);
    }

    public static <T> T getObjectFromXml(final Class<T> classType, InputStream is) throws Exception {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance ( classType.getPackage().getName() );
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            Source inSrc = new StreamSource(is);
            return unmarshaller.unmarshal(inSrc, classType).getValue();
        }
        catch (Exception e) {
            log.error("Error get object from xml string\n", e);
            throw e;
        }
    }

    public static Object createCustomObject(final String s)
        throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Object obj;
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (log.isDebugEnabled()) {
                log.debug("Starting create class object for name '" + s + "'");
                log.debug("    class loader:\n" + classLoader +"\nhash: "+ classLoader.hashCode() );
            }

            if (s == null)
                return null;

            Class className;

            if (log.isDebugEnabled())
                log.debug("Create class for name '" + s + "'");

            className = Class.forName(s, true, classLoader);

            if (log.isDebugEnabled())
                log.debug("Class for name '" + s + "' is " + className);

            if (className == null)
                throw new ClassNotFoundException("Error create class for name " + s);

            if (log.isDebugEnabled())
                log.debug("Create object for name '" + s + "'");

            obj = className.newInstance();

            if (log.isDebugEnabled())
                log.debug("Object for name '" + s + "' is " + obj);
        }
        catch (ClassNotFoundException e) {
            log.error("Error create reflection object for class name '" + s + "'", e);
            throw e;
        }
        catch (InstantiationException e) {
            log.error("Error create reflection object for class name '" + s + "'", e);
            throw e;
        }
        catch (IllegalAccessException e) {
            log.error("Error create reflection object for class name '" + s + "'", e);
            throw e;
        }
        return obj;
    }

    /**
     * @param str_ source string
     * @param repl array of values for search and replace
     * @return resulting string
     */
    public static String replaceStringArray( final String str_, final String repl[][]) {
        String qqq = str_;
        for (final String[] newVar : repl) {
            qqq = StringUtils.replace(qqq, newVar[0], newVar[1]);
        }
        return qqq;

    }

    public static byte[] getBytesUTF( final String s) {
        if (s==null)
            return new byte[0];

        try {
            return s.getBytes("utf-8");
        }
        catch (java.io.UnsupportedEncodingException e) {
            log.warn("String.getBytes(\"utf-8\") not supported");
            return new byte[0];
        }
    }

    public static int getStartUTF( final String s, final int maxByte) {
        return getStartUTF(getBytesUTF(s), maxByte);
    }

    public static int getStartUTF( final byte[] b, final int maxByte) {
        return getStartUTF(b, maxByte, 0);
    }

    public static int getStartUTF( final byte[] b, final int maxByte, final int offset) {
        if (b.length <= offset)
            return -1;

        if (b.length < maxByte)
            return b.length;

        int idx = Math.min(b.length, maxByte + offset);

        for (int i = idx - 1; i > offset; i--)
        {
            int j = (b[i] < 0?0x100 + b[i]:b[i]);
            if (j < 0x80)
            {
                return i + 1;
            }
        }
        return -1;
    }
}

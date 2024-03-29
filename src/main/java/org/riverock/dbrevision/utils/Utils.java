/*
 * Copyright 2007 Sergei Maslyukov at riverock.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riverock.dbrevision.utils;

import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * Common utils
 *
 * @author Sergei Maslyukov
 *         Date: 14.12.2006
 *         Time: 16:37:21
 *         <p/>
 *         $Id$
 */
public class Utils {

    /**
     * Put value in map. If map already contained value for specified key, this value and previous value
     * inserted in list and this list associated with given key
     *
     * @param map map
     * @param key key
     * @param value value
     */
    public static void putKey(final Map<String, Object> map, final String key, final Object value) {
        Object obj = map.get(key);
        if (obj == null) {
            map.put(key, value);
            return;
        }

        if (obj instanceof List) {
            if (value instanceof List) {
                ((List) obj).addAll((List) value);
            }
            else {
                ((List) obj).add(value);
            }
        }
        else {
            List<Object> v = new ArrayList<Object>();
            v.add(obj);

            if (value instanceof List) {
                v.addAll((List) value);
            }
            else {
                v.add(value);
            }

            map.remove(key);
            map.put(key, v);
        }
    }

    /**
     * Get current time as java.sql.Timestamp
     *
     * @return current time
     */
    public static java.sql.Timestamp getCurrentTime() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * convert given Calendar with mask and locale to String
     *
     * @param c calendar
     * @param mask format mask
     * @param loc locale
     * @return date as String
     */
    public static String getStringDate( final Calendar c, final String mask, final Locale loc ) {
        if (c == null) return null;
        return DateFormatUtils.format(c.getTimeInMillis(), mask, c.getTimeZone(), loc);
    }

    /**
     * convert given Calendar with mask to String. 
     *
     * @param c calendar
     * @param mask format mask
     * @return date as String
     */
    public static String getStringDate( final Calendar c, final String mask ) {
        return DateFormatUtils.format(c.getTimeInMillis(), mask, c.getTimeZone(), Locale.ENGLISH);
    }

    /**
     *
     * @param date
     * @param mask
     * @return
     * @throws java.text.ParseException
     */
    public static java.util.Date getDateWithMask( final String date, final String mask )
        throws java.text.ParseException {
        if (date == null || mask == null)
            return null;

        SimpleDateFormat dFormat = new SimpleDateFormat(mask);

        return dFormat.parse(date);
    }

    /**
     *
     * @param date
     * @param mask
     * @param loc
     * @param tz
     * @return
     */
    public static String getStringDate( final java.util.Date date, final String mask, final Locale loc, final TimeZone tz) {
        if (date == null) return null;

        SimpleDateFormat df = new SimpleDateFormat(mask, loc);
        df.setTimeZone( tz );

        return df.format( date );
    }

    /**
     *
     * @param obj
     * @param rootElement
     * @return
     * @throws Exception
     */
    public static byte[] getXml(final Object obj, final String rootElement) throws Exception {
        return getXml(obj, rootElement, "utf-8");
    }

    /**
     *
     * @param obj
     * @param rootElement
     * @param encoding
     * @return
     * @throws JAXBException
     */
    public static byte[] getXml(final Object obj, final String rootElement, final String encoding) throws JAXBException {
        return getXml(obj, rootElement, encoding, false, null);
    }


    /**
     *
     * @param obj
     * @param rootElement
     * @param encoding
     * @param isIndent
     * @param namespacePrefixMappers
     * @return
     * @throws JAXBException
     */
    public static byte[] getXml(final Object obj, final String rootElement, final String encoding, boolean isIndent, NamespacePrefixMapper[] namespacePrefixMappers) throws JAXBException {
/*
        if (log.isDebugEnabled()) {
            log.debug("getXml(). Object to marshaling " + obj);
            log.debug("getXml(). rootElement " + rootElement);
            log.debug("getXml(). encoding " + encoding);
        }
*/
        ByteArrayOutputStream fos = new ByteArrayOutputStream(1000);

/*
        if (log.isDebugEnabled()) {
            log.debug("ByteArrayOutputStream object - " + fos);
        }
*/

        writeMarshalToOutputStream(obj, encoding, rootElement, fos, isIndent, namespacePrefixMappers);
        return fos.toByteArray();
    }


    /**
     * 
     * @param obj
     * @param encoding
     * @param rootElement
     * @param fos
     * @throws JAXBException
     */
    public static void writeMarshalToOutputStream(
        Object obj, String encoding, String rootElement, OutputStream fos,
        boolean isIndent, NamespacePrefixMapper[] namespacePrefixMappers) throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance ( obj.getClass().getPackage().getName() );
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, isIndent);

        if (namespacePrefixMappers!=null) {
            for (NamespacePrefixMapper namespacePrefixMapper : namespacePrefixMappers) {
                marshaller.setProperty( "com.sun.xml.bind.namespacePrefixMapper", namespacePrefixMapper );
            }
        }

        if (rootElement != null && rootElement.trim().length() > 0) {
            // http://weblogs.java.net/blog/kohsuke/archive/2005/10/101_ways_to_mar.html
            marshaller.marshal( new JAXBElement(new QName("",rootElement), obj.getClass(), obj ), fos);
        }
        else {
            marshaller.marshal(obj, fos);
        }
    }

    /**
     *
     * @param obj
     * @param fileName
     * @throws JAXBException
     * @throws FileNotFoundException
     */
    public static void writeToFile(final Object obj, final String fileName) throws JAXBException, FileNotFoundException {
        writeToFile(obj, fileName, "utf-8");
    }

    /**
     *
     * @param obj
     * @param fileName
     * @param encoding
     * @throws FileNotFoundException
     * @throws JAXBException
     */
    public static void writeToFile(final Object obj, final String fileName, final String encoding) throws FileNotFoundException, JAXBException {
        writeMarshalToOutputStream(obj, encoding, null, new FileOutputStream(fileName), false, null );
    }

    /**
     *
     * @param obj
     * @param outputStream
     * @param encoding
     * @throws JAXBException
     */
    public static void writeObjectAsXml(final Object obj, OutputStream outputStream, final String encoding) throws JAXBException {
        writeMarshalToOutputStream(obj, encoding, null, outputStream, false, null );
    }

    /**
     *
     * @param obj
     * @param outputStream
     * @param rootElement
     * @param encoding
     * @throws JAXBException
     */
    public static void writeObjectAsXml(final Object obj, OutputStream outputStream, String rootElement, final String encoding) throws JAXBException {
        writeMarshalToOutputStream(obj, encoding, rootElement, outputStream, false, null );
    }

    public static <T> T getObjectFromXml(final Class<T> classType, final String str) throws JAXBException {
        return getObjectFromXml(classType, new StreamSource(new StringReader(str)), null);
    }

    /**
     *
     * @param classType
     * @param is
     * @return
     * @throws javax.xml.bind.JAXBException on error
     */
    public static <T> T getObjectFromXml(final Class<T> classType, InputStream is) throws JAXBException {
        return getObjectFromXml(classType, new StreamSource(is), null);
    }

    public static <T> T getObjectFromXml(final Class<T> classType, InputStream is, ValidationEventHandler handler) throws JAXBException {
        return getObjectFromXml(classType, new StreamSource(is), handler);
    }

    public static <T> T getObjectFromXml(final Class<T> classType, Source inSrc, ValidationEventHandler handler) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance ( classType.getPackage().getName() );
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        if (handler!=null) {
            unmarshaller.setEventHandler(handler);
        }

        return unmarshaller.unmarshal(inSrc, classType).getValue();
    }

    /**
     *
     * @param s
     * @return
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static Object createCustomObject(final String s)
        throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Object obj;
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            if (s == null)
                return null;

            Class className;

            className = Class.forName(s, true, classLoader);

            if (className == null)
                throw new ClassNotFoundException("Error create class for name " + s);

            obj = className.newInstance();
        }
        catch (ClassNotFoundException e) {
            throw e;
        }
        catch (InstantiationException e) {
            throw e;
        }
        catch (IllegalAccessException e) {
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

    /**
     *
     * @param s
     * @return
     */
    public static byte[] getBytesUTF( final String s) {
        if (s==null)
            return new byte[0];

        try {
            return s.getBytes("utf-8");
        }
        catch (java.io.UnsupportedEncodingException e) {
            return new byte[0];
        }
    }

    /**
     *
     * @param s
     * @param maxByte
     * @return
     */
    public static int getStartUTF( final String s, final int maxByte) {
        return getStartUTF(getBytesUTF(s), maxByte);
    }

    /**
     *
     * @param b
     * @param maxByte
     * @return
     */
    public static int getStartUTF( final byte[] b, final int maxByte) {
        return getStartUTF(b, maxByte, 0);
    }

    /**
     *
     * @param b
     * @param maxByte
     * @param offset
     * @return
     */
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

    /**
     * 
     * @return
     */
    public static String getTempDir() {
        return System.getProperty("java.io.tmpdir");
    }
}

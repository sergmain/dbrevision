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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;

/**
 * Author: mill
 * Date: Feb 13, 2003
 * Time: 4:43:06 PM
 *
 * $Id: GzipTest.java 1042 2006-11-14 14:24:45Z serg_main $
 */
public class GzipTest
{
    private static Logger cat = Logger.getLogger("org.riverock.test.GzipTest");

    public GzipTest()
    {
    }

    public static byte[] getFileBytes(InputStream in, int sizeBuff)
            throws Exception
    {

        int ch;
        byte buffBytes[] = new byte[sizeBuff];
        int i = 0;
        while (((ch = in.read()) != -1) && (i < sizeBuff))
            buffBytes[i++] = (byte) ch;

        in.close();
        byte retBytes[] = new byte[i];
        for (int j = 0; j < i; j++)
            retBytes[j] = buffBytes[j];

        return retBytes;
    }

    private static String writeToFile(String file_, byte bytes[])
        throws Exception
    {

        FileOutputStream out = new FileOutputStream(file_, false);

        out.write(bytes, 0, bytes.length);
        out.flush();
        out.close();
        out = null;

        return "File " + file_ + " создан успешно";
    }

    public static void main(String[] s)
        throws Exception
    {
        File tempFile = new File("source");

        byte[] bytes = getFileBytes(
                new FileInputStream(tempFile), 10000000
        );

        cat.debug("Length to gzip " + bytes.length);

        byte[] bytesToEncrypt = null;

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        GZIPOutputStream gzip = new GZIPOutputStream(byteStream);
        gzip.write(bytes);
        gzip.flush();
        gzip.close();

        bytesToEncrypt = byteStream.toByteArray();


        writeToFile("source.gzip", byteStream.toByteArray());

/*
        GZIPInputStream gzip1 = new GZIPInputStream();
        cat.debug("write UnGZiped data in file " + tempDir + "file.gzip.3");
        cat.debug( writeToFile(tempDir + "file.gzip.3", getFileBytes(gzip1, 10000000)) );
*/
    }

}

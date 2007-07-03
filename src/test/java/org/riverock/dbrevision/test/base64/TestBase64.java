package org.riverock.dbrevision.test.base64;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.DecoderException;

/**
 * Created by IntelliJ IDEA.
 * User: SergeMaslyukov
 * Date: 22.02.2007
 * Time: 0:21:36
 */
public class TestBase64 {
    public static void main(String[] args) throws DecoderException {
        String s = "‡·‚„‰ÂÊÁabcdefghk1234567890";

        Base64 base64 = new Base64();
        byte[] bytes = base64.encode(s.getBytes());

        String s1 = new String(bytes);
        System.out.println("Base64: " + s1);

        bytes = base64.decode(s1.getBytes());

        System.out.println("Base64: " + new String(bytes));


    }
}

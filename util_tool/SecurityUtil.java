package com.secmask.util.tool;

import java.math.BigInteger;
import java.security.MessageDigest;

public class SecurityUtil {
	
	public static String parseMD5(String str) throws Exception{
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(str.getBytes());
        return new BigInteger(1, md.digest()).toString(16);
    }

    public static String parseMD5(String str, String sixteen) throws Exception{
	    MessageDigest md = MessageDigest.getInstance("MD5");
	    md.update(str.getBytes());
        byte b[] = md.digest();
        int i;
        StringBuilder buf = new StringBuilder("");
        for (byte aB : b) {
            i = aB;
            if (i < 0) {
                i += 256;
            }
            if (i < 16) {
                buf.append("0");
            }
            buf.append(Integer.toHexString(i));
        }
        return buf.toString().substring(8,24);
    }
	
}

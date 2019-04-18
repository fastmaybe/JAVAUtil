package com.secmask.util.tool;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.StringUtils;

public class BytesTranslate {

	static private final int    LOOKUP_LENGTH      = 16;
	static final private char[] UPPER_CHARS        = new char[LOOKUP_LENGTH];
    static final private char[] LOWER_CHARS        = new char[LOOKUP_LENGTH];

    static {
        for (int i = 0; i < 10; i++) {
            UPPER_CHARS[i] = (char) ('0' + i);
            LOWER_CHARS[i] = (char) ('0' + i);
        }
        for (int i = 10; i <= 15; i++) {
            UPPER_CHARS[i] = (char) ('A' + i - 10);
            LOWER_CHARS[i] = (char) ('a' + i - 10);
        }
    }
    
    public static byte[] subBytes(byte[] bytes, int startIndex, int length) {
    	if (bytes == null 
				|| startIndex < 0 || startIndex >= bytes.length
				|| length > bytes.length || length <= 0) {
            return null;
        }
    	byte[] subBytes = new byte[length];
    	System.arraycopy(bytes, startIndex, subBytes, 0, length);
    	return subBytes;
    }
    
    public static byte[] combinBytes(byte[] bytes1, byte[] bytes2) {
    	if(bytes1 == null && bytes2 == null) {
    		return null;
    	}else if(bytes1 == null) {
    		return bytes2;
    	}else if(bytes2 == null) {
    		return bytes1;
    	}else {
    		byte[] bytes = new byte[bytes1.length + bytes2.length];
    		System.arraycopy(bytes1, 0, bytes, 0, bytes1.length);
    		System.arraycopy(bytes2, 0, bytes, bytes1.length, bytes2.length);
    		return bytes;
    	}
    }
	
	/**
	 * 将byte[]转换成16进制字符串
	 */
	public static String bytesToHex(byte[] bytes) {
		StringBuilder buf = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) { // 使用String的format方法进行转换
			buf.append(String.format("%02x", new Integer(b & 0xff)));
		}
		return buf.toString();
	}
	
	/**
	 * 将byte[]转换成16进制字符串
	 */
	public static String bytesToHex(byte[] bytes, int startIndex, int length, boolean upperCase) {
		if (bytes == null 
				|| startIndex < 0 || startIndex >= bytes.length
				|| length > bytes.length || length <= 0) {
            return null;
        }

        final char[] chars = upperCase ? UPPER_CHARS : LOWER_CHARS;

        char[] hex = new char[length * 2];
        int hexIndex = 0;
        for (int i = startIndex; i < startIndex + length; i++) {
            int b = bytes[i] & 0xFF;
            hex[hexIndex * 2] = chars[b >> 4];
            hex[hexIndex * 2 + 1] = chars[b & 0xf];
            hexIndex ++;
        }
        return new String(hex);
	}

	/**
	 * 将16进制字符串转换为byte[]
	 */
	public static byte[] hexToBytes(String hex) {
		if (hex == null || hex.trim().equals("")) {
			return new byte[0];
		}

		byte[] bytes = null;
		boolean isOddHexLength = hex.length() % 2 != 0;
		if(!isOddHexLength) {//16进制字符串长度为偶数
			bytes = new byte[hex.length() / 2];
		}else {//16进制字符串长度为奇数
			bytes = new byte[hex.length() / 2 + 1];
		}
		for (int i = 0; i < hex.length() / 2; i++) {
			String subStr = hex.substring(i * 2, i * 2 + 2);
			bytes[i] = (byte) Integer.parseInt(subStr, 16);
		}
		if(isOddHexLength) {
			bytes[hex.length() / 2] = (byte) Integer.parseInt(hex.substring(hex.length() - 1, hex.length()), 16);
		}
		return bytes;
	}
	
	/**
	 * 将16进制字符串转换为字符串
	 * @throws UnsupportedEncodingException 
	 */
	public static String hexToStr(String hex, String charset) throws UnsupportedEncodingException {
		String str = "";
		if (StringUtils.isNotBlank(hex)) {
			str = new String(hexToBytes(hex), charset);
		}
		return str;
	}
	
	/**  
     * byte数组（1-4字节）中取int数值，本方法适用于(低位在后，高位在前)的顺序。
     */  
	public static int bytesToInt(byte[] bytes) {
	    return bytesToInt(bytes, true);
	}
	
	/**  
	 * byte数组（1-4字节）中取int数值
	 */  
	public static int bytesToInt(byte[] bytes, boolean isHighBitPositionAtFront) {
		return bytesToInt(bytes, isHighBitPositionAtFront, 0, bytes.length);
	}
	
	/**  
	 * byte数组（1-4字节）中取int数值
	 */  
	public static int bytesToInt(byte[] bytes, boolean isHighBitPositionAtFront, int startIndex, int length) {
		int byteNum = length;
		int value = 0;
		for(int index=0; index<byteNum; index++) {
			if(isHighBitPositionAtFront) {
				value = value | (bytes[index+startIndex] & 0xFF)<<8*(byteNum-index-1);
			}else {
				value = value | (bytes[index+startIndex] & 0xFF)<<8*(index);
			}
		}
		return value;
	}
	
	/**
     * 将int数值转换为byte数组（1-4字节），本方法适用于(高位在前，低位在后)的顺序
     */
	public static byte[] intToBytes(int value, int byteNum){
	    return intToBytes(value, byteNum, true);  
	}
	
	/**
	 * 将int数值转换为byte数组（1-4字节）
	 */
	public static byte[] intToBytes(int value, int byteNum, boolean isHighBitPositionAtFront){
		byte[] bytes = new byte[byteNum];
		for(int index=0; index<byteNum; index++) {
			if(isHighBitPositionAtFront) {
				bytes[index] = (byte) ((value>>8*(byteNum-index-1)) & 0xFF);
			}else {
				bytes[byteNum-index-1] = (byte) ((value>>8*(byteNum-index-1)) & 0xFF);
			}
		}
		return bytes;  
	}
	
	/**
	 * 将int数值转换为byte数组，数组大小根据int数值大小变化
	 */
	public static byte[] intToBytes(int value, boolean isHighBitPositionAtFront) {
		byte[] bytes = intToBytes(value, 4, isHighBitPositionAtFront);
		int bytesLen = 4;
		for(int index=0; index<4; index++) {
			if(isHighBitPositionAtFront) {
				if(bytes[index] == 0) {
					bytesLen --;
				}else {
					break;
				}
			}else {
				if(bytes[4-index] == 0) {
					bytesLen --;
				}else {
					break;
				}
			}
		}
		
		byte[] result = new byte[bytesLen];
		if(isHighBitPositionAtFront) {
			System.arraycopy(bytes, 4-bytesLen, result, 0, bytesLen);
		}else {
			System.arraycopy(bytes, 0, result, 0, bytesLen);
		}
		return result;
	}

}

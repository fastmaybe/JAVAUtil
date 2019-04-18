package com.secmask.util.tool;

public class BytesUtil {
	
	public static void fillHexZero(byte[] bytes, int startIndex, int endIndex) {
		for(int index=startIndex; index<endIndex; index++) {
			bytes[index] = 0x00;
		}
	}
	
}

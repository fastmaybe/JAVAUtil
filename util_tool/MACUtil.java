package com.secmask.util.tool;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MACUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(MACUtil.class);

	public static String[] getMacAddr(){
		try {
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			List<String> stringList = new ArrayList<String>();
			while (networkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = networkInterfaces.nextElement();
				byte[] x = networkInterface.getHardwareAddress();
				if (x != null) {
					stringList.add(BytesTranslate.bytesToHex(x));
				}
			}
			return stringList.toArray(new String[stringList.size()]);
		} catch (SocketException e) {
			logger.error("获取MAC地址失败：", e);
		}
		return null;
	}
	
	public static String getFirstMacAddr(){
		String firstMacAddr = "";
		try {
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			while (networkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = networkInterfaces.nextElement();
				byte[] x = networkInterface.getHardwareAddress();
				if (x != null) {
					firstMacAddr = BytesTranslate.bytesToHex(x);
					break;
				}
			}
		} catch (SocketException e) {
			logger.error("获取MAC地址失败：", e);
		}
		return firstMacAddr.toUpperCase();
	}
	
}

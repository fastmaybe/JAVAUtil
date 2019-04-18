package com.secmask.util.tool;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InetUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(InetUtil.class);

	@SuppressWarnings("rawtypes")
	public static InetAddress getLocalHostLANAddress() {
		try {
			InetAddress candidateAddress = null;
			// 遍历所有的网络接口
			for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
				NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
				// 在所有的接口下再遍历IP
				for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
					InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
					if (!inetAddr.isLoopbackAddress()) {// 排除loopback类型地址
						if (inetAddr.isSiteLocalAddress()) {
							// 如果是site-local地址，就是它了
							return inetAddr;
						} else if (candidateAddress == null) {
							// site-local类型的地址未被发现，先记录候选地址
							candidateAddress = inetAddr;
						}
					}
				}
			}
			if (candidateAddress != null) {
				return candidateAddress;
			}
			// 如果没有发现 non-loopback地址.只能用最次选的方案
			InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
			return jdkSuppliedAddress;
		} catch (IOException e) {
			LOGGER.error("获取本地IP失败：", e.toString());
		}
		return null;
	}

	public static String getLocalIp() {
		String localIp = "";
		if (InetUtil.getLocalHostLANAddress() != null) {
			localIp = InetUtil.getLocalHostLANAddress().getHostAddress();
		}
		return localIp;
	}

	/**
	 * 获取服务端口号
	 * 
	 * @return 端口号
	 */
	public static int getServerPort(boolean secure){
		try {
			MBeanServer mBeanServer = null;
			if (MBeanServerFactory.findMBeanServer(null).size() > 0) {
				mBeanServer = (MBeanServer) MBeanServerFactory.findMBeanServer(null).get(0);
			}
	
			if (mBeanServer == null) {
				return -1;
			}
	
			Set<ObjectName> names = null;
			try {
				names = mBeanServer.queryNames(new ObjectName("Catalina:type=Connector,*"), null);
			} catch (Exception e) {
				return -1;
			}
			Iterator<ObjectName> it = names.iterator();
			ObjectName oname = null;
			while (it.hasNext()) {
				oname = (ObjectName) it.next();
				String protocol = (String) mBeanServer.getAttribute(oname, "protocol");
				String scheme = (String) mBeanServer.getAttribute(oname, "scheme");
				Boolean secureValue = (Boolean) mBeanServer.getAttribute(oname, "secure");
				Boolean SSLEnabled = (Boolean) mBeanServer.getAttribute(oname, "SSLEnabled");
				if (SSLEnabled != null && SSLEnabled) {// tomcat6开始用SSLEnabled
					secureValue = true;// SSLEnabled=true但secure未配置的情况
					scheme = "https";
				}
				if (protocol != null && ("HTTP/1.1".equals(protocol) || protocol.contains("http"))) {
					if (secure && "https".equals(scheme) && secureValue) {
						return (Integer) mBeanServer.getAttribute(oname, "port");
					} else if (!secure && !"https".equals(scheme) && !secureValue) {
						return (Integer) mBeanServer.getAttribute(oname, "port");
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("获取服务端口失败：", e.toString());
		}
		return -1;
	}

}

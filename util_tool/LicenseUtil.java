package com.secmask.util.tool;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 注册码工具<br>
 * 1、生成注册码<br>
 * 2、校验注册码<br>
 * 3、生成识别码<br>
 * @author voyager
 *
 */
public class LicenseUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(LicenseUtil.class);
	
	/**
	 * 生成16进制注册码
	 * @param idCodeHexHashStr
	 * @param date
	 * @return
	 */
	public static String generateLicense(String idCodeHexHashStr, Date date) {
		String encodedDateStr = encodeDate(date);
		String dateHexHashStr = hexHashDate(date);
		return insertingTwoSameLenStr(idCodeHexHashStr, dateHexHashStr) + encodedDateStr;
	}
	
	/**
	 * 校验16进制注册码
	 * @param hexLicense
	 * @param modelId
	 * @return
	 */
	public static boolean isValidLicense(String hexLicense, int modelId) {
		boolean isValid = false;
		if(StringUtils.isNotBlank(hexLicense) && hexLicense.length() > 64) {
			String deviceUuid = createLocalDeviceUuid(modelId);
			String deviceHexHashStr = getDeviceHexHashStr(hexLicense);
			String dateHexHashStr = getDateHexHashStr(hexLicense);
			Date date = decodeDate(hexLicense.substring(64, hexLicense.length()));
			if(StringUtils.equals(dateHexHashStr, hexHashDate(date))
					&& StringUtils.equals(deviceHexHashStr, hexHash(deviceUuid))) {
				isValid = true;
			}
		}
		return isValid;
	}
	
	/**
	 * 从16进制注册码中获取“有效时间”信息
	 * @param hexLicense
	 * @return
	 */
	public static Date getLicenseValidDate(String hexLicense) {
		Date date = null;
		if(StringUtils.isNotBlank(hexLicense) && hexLicense.length() > 64) {
			date = decodeDate(hexLicense.substring(64, hexLicense.length()));
		}
		return date;
	}
	
	/**
	 * 生成本机设备唯一ID
	 * @param modelId
	 * @return
	 */
	private static String createLocalDeviceUuid(int modelId) {
		String macAddr = MACUtil.getFirstMacAddr();
		//去掉主板序列号和CPU序列号，因为获取不稳定
		//String mainBordUuid = MainBordUtil.getMainBordId();
		//String cpuUuid = CPUUtil.getCPUId();
		return macAddr + "-" + modelId;
	}
	
	/**
	 * 创建模块的16进制摘要识别码
	 * @param modelId 模块ID
	 * @return
	 */
	public static String createIdCode(int modelId) {
		return hexHash(createLocalDeviceUuid(modelId));
	}
	
	/**
	 * 转换成十六进制字符串，并反转
	 * @param date
	 * @return
	 */
	private static String encodeDate(Date date) {
		//这里要和RSA的16进制加解密方式一致，需要使用字符串转16进制的方式
		//String hexStr = Long.toHexString(date.getTime());
		String encodedDateStr = null;
		try {
			String hexStr = BytesTranslate.bytesToHex(String.valueOf(date.getTime()).getBytes("ISO-8859-1"));
			encodedDateStr = new StringBuffer(hexStr).reverse().toString();
		} catch (UnsupportedEncodingException e) {
			logger.error("加密日期失败！", e);
		}
		return encodedDateStr;
	}
	
	private static Date decodeDate(String encodedDateStr) {
		Date date = null;
		//这里要和RSA的16进制加解密方式一致，需要使用字符串转16进制的方式
		//long time = Long.parseLong(reverseStr(encodedDateStr), 16);
		try {
			date = new Date();
			long time = Long.parseLong(BytesTranslate.hexToStr(reverseStr(encodedDateStr), "ISO-8859-1"));
			date.setTime(time);
		} catch (Exception e) {
			logger.error("解密日期失败！", e);
		}
		return date;
	}
	
	/**
	 * 日期转换成字符串，并获取16进制摘要信息
	 * @param date
	 * @return
	 */
	private static String hexHashDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateStr = formatter.format(date);
		return hexHash(dateStr);
	}
	
	/**
	 * 获取MD5的16进制摘要信息（32位）
	 * @param srcStr
	 * @return
	 */
	private static String hexHash(String srcStr) {
		String hexStr = "";
		try {
			MessageDigest digest = MessageDigest.getInstance("md5");
			digest.update(srcStr.getBytes("UTF-8"));
			//转换成16进制字符串
			hexStr = BytesTranslate.bytesToHex(digest.digest());
		} catch (NoSuchAlgorithmException e) {
		} catch (UnsupportedEncodingException e) {
		}
		return hexStr;
	}
	
	private static String reverseStr(String str) {
		return new StringBuffer(str).reverse().toString();
	}
	
	/**
	 * 从16进制注册码中获取设备的16进制摘要信息
	 * @param hexLicense
	 * @return
	 */
	private static String getDeviceHexHashStr(String hexLicense) {
		char[] licenseChars = hexLicense.toCharArray();
		char[] deviceHashChars = new char[32];
		for(int index=0; index<32; index++) {
			deviceHashChars[index] = licenseChars[index * 2];
		}
		return reverseStr(new String(deviceHashChars));
	}
	
	/**
	 * 从16进制注册码中获取日期的16进制摘要信息
	 * @param hexLicense
	 * @return
	 */
	private static String getDateHexHashStr(String hexLicense) {
		char[] hexLicenseChars = hexLicense.toCharArray();
		char[] dateHexHashChars = new char[32];
		for(int index=0; index<32; index++) {
			dateHexHashChars[index] = hexLicenseChars[index * 2 + 1];
		}
		return reverseStr(new String(dateHexHashChars));
	}
	
	//两个相同长度的字符串，以间隔插入的方式拼接成一个新字符串
	private static String insertingTwoSameLenStr(String str1, String str2) {
		String result = "";
		if(str1.length() == str2.length()) {
			char[] resultChars = new char[str1.length() * 2];
			int index = str1.length() - 1;
			int charIndex = 0;
			char[] hashStr1Chars = str1.toCharArray();
			char[] hashStr2Chars = str2.toCharArray();
			for(; index>=0; index--) {
				resultChars[charIndex ++] = hashStr1Chars[index];
				resultChars[charIndex ++] = hashStr2Chars[index];
			}
			result = new String(resultChars);
		}
		return result;
	}
	
}

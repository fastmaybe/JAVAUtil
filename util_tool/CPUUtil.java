package com.secmask.util.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CPUUtil {

	private static final Logger logger = LoggerFactory.getLogger(CPUUtil.class);

	/**
	 * 获取当前操作系统名称. return 操作系统名称 例如:windows xp,linux 等.
	 */
	public static String getOSName() {
		return System.getProperty("os.name").toLowerCase();
	}

	/**
	 * 获取CPU序列号
	 * 
	 * @return
	 */
	private static String getCPUID_Windows() {
		String result = "";
		try {
			File file = File.createTempFile("tmp", ".vbs");
			file.deleteOnExit();
			FileWriter fw = new java.io.FileWriter(file);
			String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
					+ "Set colItems = objWMIService.ExecQuery _ \n" + "   (\"Select * from Win32_Processor\") \n"
					+ "For Each objItem in colItems \n" + "    Wscript.Echo objItem.ProcessorId \n"
					+ "    exit for  ' do the first cpu only! \n" + "Next \n";

			// + " exit for \r\n" + "Next";
			fw.write(vbs);
			fw.close();
			Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = input.readLine()) != null) {
				result += line;
			}
			input.close();
			file.delete();
		} catch (Exception e) {
			logger.error("获取cpu信息错误", e);
		}
		return result.trim();
	}

	private static String getCPUID_linux() {
		String result = "";
		String CPU_ID_CMD = "dmidecode | grep 'ID' | awk '{print $2 $3 $4 $5 $6 $7 $8 $9}' | head -2 | tail -1";
		BufferedReader bufferedReader = null;
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(new String[] { "sh", "-c", CPU_ID_CMD });// 管道
			bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				result += line;
                break;
			}

		} catch (IOException e) {
			logger.error("获取cpu信息错误", e);
		}
		return result.trim();
	}

	public static String getCPUId() {
		String os = getOSName();
		String cpuId = "";
		if (os.startsWith("windows")) {
			cpuId = CPUUtil.getCPUID_Windows();
		} else if (os.startsWith("linux")) {
			cpuId = CPUUtil.getCPUID_linux();
		}
		return cpuId;
	}


}

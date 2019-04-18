package com.secmask.util.tool;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 服务器代码计时
 */
public class ExecTime {

private long start;

private long end;

public ExecTime() {
	start = System.nanoTime();
}

	@Override
	public String toString() {
		end = System.nanoTime();
		DecimalFormat df = new DecimalFormat("0.000");
		double f = (end - start)/1000000.0;
		String all = df.format(f);
		return all + " 毫秒";
	}

	//获取耗时
	public int getTime() {
		end = System.nanoTime();
		return (int)(end-start)/1000000;
	}

	public static String timeFormat(Date date){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			return df.format(date);
		}catch (Exception e) {
			return "";
		}
	}
}



package com.secmask.util.tool;

import java.io.Closeable;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceCloseUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(ResourceCloseUtil.class);
	
	public static void close(Closeable c) {
		if(c != null){
			try {
				c.close();
			} catch (IOException e) {
				logger.error(e.toString(), e);
			}
		}
	}
	
	public static void close(AutoCloseable ac) {
		if(ac != null){
			try {
				ac.close();
			} catch (Exception e) {
				logger.error(e.toString(), e);
			}
		}
	}
	
}

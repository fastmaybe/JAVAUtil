package com.secmask.util.tool;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class PropertiesConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(PropertiesConfig.class);
	private static final String defaultPropertyFileName = "config.properties";
	
	public static Map<String, String> propertiesMap = new HashMap<String, String>();
	
	static {
		loadDefaultProperties();
	}

	private static void processProperties(Properties props) throws BeansException {
		propertiesMap = new HashMap<String, String>();
		for (Object key : props.keySet()) {
			String keyStr = key.toString();
			try {
				propertiesMap.put(keyStr, props.getProperty(keyStr));
			} catch (java.lang.Exception e) {
				logger.error(e.toString(), e);;
			}
		}
	}

	private static void loadProperties(String propertyFileName) {
		try {
			Properties properties = PropertiesLoaderUtils.loadAllProperties(propertyFileName);
			processProperties(properties);
		} catch (IOException e) {
			logger.error(e.toString(), e);;
		}
	}
	
	public static void loadDefaultProperties() {
		loadProperties(defaultPropertyFileName);
	}

	public static String getString(String name) {
		return propertiesMap.get(name);
	}
	
	public static Integer getInteger(String name) {
		return Integer.parseInt(getString(name));
	}

	public static Map<String, String> getAll() {
		return propertiesMap;
	}
}

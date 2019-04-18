package com.secmask.util.tool;

import java.util.HashMap;
import java.util.Map;

public class DataCache {
	
	private static Map<String, Map<String, Object>> cache = new HashMap<String, Map<String, Object>>();
	
	private DataCache() {}
	
	public static boolean isExist(String key, String field) {
		Map<String, Object> data = cache.get(key);
		if(data == null)
			return false;
		else
			return data.containsKey(field);
	}
	
	public static void add(String key, String field, Object value) {
		Map<String, Object> data = cache.get(key);
		if(data == null) {
			data = new HashMap<String, Object>();
			cache.put(key, data);
		}
		data.put(field, value);
	}
	
	public static Object get(String key, String field) {
		Map<String, Object> data = cache.get(key);
		if(data == null)
			return null;
		else 
			return data.get(field);
	}
	
	public static String getString(String key, String field) {
		Object value = get(key, field);
		if(value == null)
			return null;
		else
			return (String)value;
	}
	
	public static Integer getInteger(String key, String field) {
		Object value = get(key, field);
		if(value == null)
			return null;
		else
			return (Integer)value;
	}
	
	public static Boolean getBoolean(String key, String field) {
		Object value = get(key, field);
		if(value == null)
			return null;
		else
			return (Boolean)value;
	}
	
	public static byte[] getBytes(String key, String field) {
		Object value = get(key, field);
		if(value == null)
			return null;
		else
			return (byte[])value;
	}
	
	public static void delete(String key) {
		cache.remove(key);
	}
	
	public static void clean() {
		cache.clear();
	}
	
}

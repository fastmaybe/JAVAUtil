package com.secmask.util.tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.StringUtils;

public class StringCommonUtil {
	
	public static List<String> splitByChar(String str, String spliterator){
		List<String> result = new ArrayList<String>();
		if(!StringUtils.isEmpty(str)) {
			result.addAll(Arrays.asList(str.split(spliterator)));
		}
		return result;
	}
	
	public static List<Integer> splitByChar2IntList(String str, String spliterator) throws NumberFormatException{
		List<Integer> result = new ArrayList<Integer>();
		if(!StringUtils.isEmpty(str)) {
			for(String piece : str.split(spliterator)) {
				result.add(Integer.parseInt(piece));
			}
		}
		return result;
	}

}

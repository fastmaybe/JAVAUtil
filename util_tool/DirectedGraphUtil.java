package com.secmask.util.tool;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectedGraphUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(DirectedGraphUtil.class);
	
	public static <T> boolean checkCircularPath(Map<T, List<T>> relations, Set<T> allPoints) {
		boolean result = false;
		Set<T> unreachedPoints = new HashSet<T>(allPoints);
		Iterator<T> itr = allPoints.iterator();
		while(itr.hasNext()) {
			T startPoint = itr.next();
			if(unreachedPoints.contains(startPoint)) {
				if(checkCircularPath(startPoint, new Stack<T>(), relations, unreachedPoints)) {
					result = true;
				}
			}
		}
		return result;
	}
	
	private static <T> boolean checkCircularPath(T startPoint, Stack<T> path, 
			Map<T, List<T>> relations, Set<T> unreachedPoints) {
		if(path.contains(startPoint)) {
			//检测到循环路径
			path.push(startPoint);
			logger.info("检测到循环路径：" + path);
			System.out.println("检测到循环路径：" + path);
			path.pop();
			return true;
		}
		if(!unreachedPoints.contains(startPoint)) {
			//遍历过的节点
			return false;
		}
		unreachedPoints.remove(startPoint);
		path.add(startPoint);
		List<T> endPointList = relations.get(startPoint);
		if(endPointList == null) {
			//Reach dead end
			path.pop();
			return false;
		}
		boolean hasCircularPath = false;
		for(T endPoint : endPointList) {
			if(checkCircularPath(endPoint, path, relations, unreachedPoints)) {
				hasCircularPath = true;
			}
		}
		path.pop();
		return hasCircularPath;
	}
	
}

package com.secmask.util.tool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleMsgQueue {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private BlockingQueue<String> queue = new LinkedBlockingQueue<String>(100000);
	
	public boolean tryAdd(String msg) {
		boolean result = false;
		try {
			result = queue.add(msg);
		} catch (IllegalStateException e) {
			logger.warn("队列已满，消息插入失败：" + msg);
		}
		return result;
	}
	
	public String take() throws InterruptedException {
		return queue.take();
	}
	
}

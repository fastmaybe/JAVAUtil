package com.secmask.web.common.util.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author wd
 * @Program DBMaskerServer
 * @create 2018-06-27 12:30
 */
@Component("redisClient")
public class RedisClient {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
    @Autowired
    private JedisPool jedisPool;

    /**
     * 无时限的值
     * @param key
     * @param value
     */
    public void set(String key, String value){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.set(key, value);
        } finally {
            close(jedis);
        }
    }

    /**
     * 有时间的值
     * @param key
     * @param value
     * @param ex
     */
    public void set(String key, String value, int ex){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.set(key,value,"NX","PX",ex);
        } finally {
            close(jedis);
        }
    }

    /**
     * 续期
     * @param key
     * @param ex
     */
    public void expire(String key, int ex) {
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            jedis.expire(key,ex);
        }finally {
            close(jedis);
        }
    }

    /**
     * 取值
     * @param key
     * @return
     */
    public String get(String key)  {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.get(key);
        } catch (Exception e){
        	logger.error("", e);
        	return null;
        }
        finally {
            close(jedis);
        }
    }

    /**
     * 设置有锁值
     * @param key
     * @param value
     * @param ex
     * @return
     */
    public Long setnx(String key, String value, int ex){
        Jedis jedis = null;
        Long result = 0L;
        try {
            jedis = jedisPool.getResource();
            result = jedis.setnx(key, value);
            jedis.expire(key,ex);
        } finally {
            close(jedis);
        }
        return result;
    }

    public void del(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(key);
        } finally {
            close(jedis);
        }
    }

    private void close(Jedis jedis) {
        try {
            jedis.close();
        }catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 管理集群自动任务
     */
    public boolean  clusterTaskJudgment(String key) {
        //判断该键是否已经存在，不存在则创建定时键，并加锁
        return setnx(key,"1", 50) == 1L;
    }
}

package com.secmask.web.common.controller.base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.secmask.util.tool.PropertiesConfig;
import com.secmask.web.common.util.redis.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Date;

/**
 * @author wangduo
 */
public class LoginInterceptor implements HandlerInterceptor{

    @Autowired
    RedisClient redisClient;

    public static Boolean member;
    public static Date validity;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //验证是否注册
        /*if(member&&validity.getTime()> System.currentTimeMillis()) {

        }else {
            member = false;
            response.setStatus(402);
            return false;
        }*/
        // 如果已经登录返回true。
        // 如果没有登录，可以使用 reponse.send() 跳转页面。后面要跟return false,否则无法结束;
        if(redisClient.get(request.getHeader("userName")+"")!=null&&redisClient.get(request.getHeader("userName")+"").equals(request.getHeader("loginToken"))){
            //更新用户存留时间
            Integer userTimeLimit = PropertiesConfig.getInteger("user_time_limit");
            redisClient.expire(request.getHeader("userName")+"",userTimeLimit);
            return true;
        }else {//没有登录，创建登录
            response.setStatus(401);
            return false;
        }
    }
}

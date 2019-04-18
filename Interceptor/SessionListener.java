package com.secmask.web.common.controller.base;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.HashSet;

/**
 * @author wangduo
 */
/**
 * 去掉注释打开session监听
 * @WebListener
 * */
public class SessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent arg0) {
// TODO Auto-generated method stub
        ServletContext context = arg0.getSession().getServletContext();
        String a = context.getAttribute("user")+"";
        System.out.println("创建"+a);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent arg0) {
// TODO Auto-generated method stub
        ServletContext context = arg0.getSession().getServletContext();
        String a = context.getAttribute("user")+"";
        System.out.println("过时"+a);
    }
}

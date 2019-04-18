package com.secmask.util.tool;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TomcatUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(TomcatUtil.class);
	
	private static final String TOMCAT_USERNAME = PropertiesConfig.getString("proxy.server.username");
	private static final String TOMCAT_PASSWORD = PropertiesConfig.getString("proxy.server.password");
	
	private static String message(String operateURL) {
        StringBuffer dataResult = new StringBuffer();
        URL url = null;
        try {
            url = new URL(operateURL);
            URLConnection conn = (URLConnection) url.openConnection();

            String configuration = TOMCAT_USERNAME+":"+TOMCAT_PASSWORD; // manager角色的用户
            String encodedPassword = Base64.getEncoder().encodeToString(configuration.getBytes());
            conn.setRequestProperty("Authorization", "Basic " + encodedPassword);

            InputStream is = conn.getInputStream();
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = bufreader.readLine()) != null) {
                dataResult.append(line);
            }
        } catch (Exception e) {
        	logger.error(e.toString(), e);
        }
        return dataResult.toString();
    }

	/**
     * 重新部署一个项目
     * @param webAppName
     * @return
     */
    public static boolean reloadWebApp(String webAppName, String ip, int port){
    	String url = "http://"+ip+":"+port+"/manager/text/reload?path=/"+webAppName;
        String data = message(url);
        if(data.startsWith("OK")){
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 停止一个项目
     * @param webAppName
     * @return
     */
    public static boolean stopWebApp(String webAppName, String ip, int port){
    	String url = "http://"+ip+":"+port+"/manager/text/stop?path=/"+webAppName;
        String data = message(url);
        if(data.startsWith("OK")){
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 开始一个项目
     * @param webAppName
     * @return
     */
    public static boolean startWebApp(String webAppName, String ip, int port){
    	String url = "http://"+ip+":"+port+"/manager/text/start?path=/"+webAppName;
        String data = message(url);
        if(data.startsWith("OK")){
            return true;
        }
        else {
            return false;
        }
    }
	
}

package com.secmask.web.common.controller.base.aes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.secmask.util.tool.AesUtil;
import com.secmask.util.tool.PropertiesConfig;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author wd
 * @Program DBMaskerServer
 * @create 2019-01-15 10:10
 */
@Component
public class RequestParameterFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String me = PropertiesConfig.getString("message_encrypt");
        if("true".equals(me)) {
            if (request.getHeader("Content-Type") != null && request.getHeader("Content-Type").contains("x-www-form-urlencoded")) {
                String jsonString = AesUtil.aesDecode(request.getParameter("axiosdata") + "");
                JSONObject data = JSON.parseObject(jsonString);
                if (data != null) {
                    Map paramter = data.getInnerMap();
                    ParameterRequestWrapper wrapper = new ParameterRequestWrapper(request, paramter);
                    filterChain.doFilter(wrapper, response);
                } else {
                    filterChain.doFilter(request, response);
                }
                return;
            }
        }
        filterChain.doFilter(request,response);
    }
}
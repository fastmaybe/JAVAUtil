package com.secmask.web.common.controller.base.aes;

import com.alibaba.fastjson.JSON;
import com.secmask.util.tool.AesUtil;
import com.secmask.util.tool.PropertiesConfig;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wd
 * @Program DBMaskerServer
 * @create 2019-01-14 20:07
 */
@ControllerAdvice
public class ResponseDataHandler implements ResponseBodyAdvice{
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        String me = PropertiesConfig.getString("message_encrypt");
        if("true".equals(me)&&!selectedContentType.toString().contains("x-www-form-urlencoded")) {
            Map<String, Object> result = new HashMap<>();
            result.put("data", AesUtil.aesEncode(JSON.toJSONString(body)));
            return result;
        }
        return body;
    }
}

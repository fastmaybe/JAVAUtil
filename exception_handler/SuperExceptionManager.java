package com.secmask.web.common.controller.base;
import com.secmask.pojo.DTO.MyException;
import com.secmask.pojo.DTO.ResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wd
 * @Program DBMaskerServer
 * @create 2018-09-04 15:17
 */
@ControllerAdvice
public class SuperExceptionManager {

	private static final Logger logger = LoggerFactory.getLogger(SuperExceptionManager.class);
	
    @ExceptionHandler(value = Exception.class)
    @ResponseBody// 返回json数据
    public ResultDTO jsonErrorHandler(HttpServletRequest req, HttpServletResponse response, Exception e) {
    	logger.error(e.getMessage(), e);

        response.setStatus(500);
        return new ResultDTO("500", e.getMessage());
    }

    @ExceptionHandler(value = MyException.class)
    @ResponseBody
    public ResultDTO myExceptionHandler(MyException e) {

        return new ResultDTO(e.getCode(), e.getMsg(), e.getMode());
    }

}
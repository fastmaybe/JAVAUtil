package com.secmask.web.common.controller.base;


import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author wangduo
 */
@Configuration
public class WebAppConfigration implements WebMvcConfigurer {

    @Bean
    LoginInterceptor localInterceptor() {
        return new LoginInterceptor();
    }
    //手动注册task线程管理类
    @Bean
    ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler tp = new ThreadPoolTaskScheduler();
        tp.setPoolSize(5);
        return tp;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册自定义拦截器，添加拦截路径和排除拦截路径
        registry.addInterceptor(localInterceptor())
        .addPathPatterns("/**")
        .excludePathPatterns("/error","/login/*","/user/checkPassword","/user/editUser2",
        		"/webjars/**","/swagger-resources/**","/v2/api-docs","/configuration/**","/swagger-ui.html");//Swagger2 接口API文档资源放行
    }

    @Bean
    public HttpMessageConverters useConverters() {

        return new HttpMessageConverters(new FastJsonHttpMessageConverter());
    }
}

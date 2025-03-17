package com.cs.config;

import com.alibaba.cloud.seata.web.SeataHandlerInterceptor;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.SentinelWebInterceptor;
import com.cs.common.Constants;
import com.cs.config.handler.TokenToAdminUserMethodArgumentResolver;
import com.cs.config.handler.TokenToMallUserMethodArgumentResolver;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Configuration
public class UserServiceMvcConfigurer extends WebMvcConfigurationSupport {

    @Resource
    private TokenToAdminUserMethodArgumentResolver tokenToAdminUserMethodArgumentResolver;
    @Autowired
    private TokenToMallUserMethodArgumentResolver tokenToMallUserMethodArgumentResolver;

    //定义注解处理方法
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers){
        argumentResolvers.add(tokenToAdminUserMethodArgumentResolver);
        argumentResolvers.add(tokenToMallUserMethodArgumentResolver);
    }

    //指定文件路径
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**").addResourceLocations("file:" + Constants.FILE_UPLOAD_DIC);
        registry.addResourceHandler("/goods-img/**").addResourceLocations("file:" + Constants.FILE_UPLOAD_DIC);

        registry.
                addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
                .resourceChain(false);
    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {

        // 将xid全局事物id传送给下游 否则会被MVC拦截变为空
        registry.addInterceptor(new SeataHandlerInterceptor()).addPathPatterns("/**");

        registry.addInterceptor(new SentinelWebInterceptor()).addPathPatterns("/**");
        super.addInterceptors(registry);
    }
}

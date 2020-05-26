package com.xiao.springcloud.loadbalancer.common.configuration;

import com.xiao.springcloud.loadbalancer.common.interceptor.HttpRequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * webmvc模式下的 webconfigurer配置
 * <p>
 * 必须存在于webmvc模式下
 *
 * @author xiaolinlin
 * @version 1.0, 2020/5/8 10:07
 */
@Configuration
// @ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
@Slf4j
public class GrayWebMvcConfigurer implements WebMvcConfigurer {

    @Bean
    public HttpRequestInterceptor getInterceptor() {
        log.debug("Init HttpRequestInterceptor...............................");
        return new HttpRequestInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.debug("Add HttpRequestInterceptor to InterceptorRegistry...............................");
        registry.addInterceptor(getInterceptor()).addPathPatterns("/**");
    }
}

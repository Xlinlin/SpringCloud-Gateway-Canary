package com.xiao.springcloud.webmvc.consumer.loadbalancer.configure;

import com.xiao.springcloud.webmvc.consumer.loadbalancer.interceptor.FeignRequestInterceptor;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * restTemplate 拦截添加
 *
 * @author xiaolinlin
 * @version 1.0, 2020/5/8 10:26
 */
@Configuration
// @ConditionalOnBean(FeignClient.class)
@Slf4j
public class FeignInterceptorConfigurer {

    @Bean
    public RequestInterceptor feignRequestInterceptor() {
        log.info("Init RequestInterceptor...............................");
        return new FeignRequestInterceptor();
    }
}

package com.xiao.springcloud.loadbalancer.common.configuration;

import com.xiao.springcloud.loadbalancer.common.interceptor.FeignRequestInterceptor;
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
        log.debug("Init RequestInterceptor...............................");
        return new FeignRequestInterceptor();
    }
}

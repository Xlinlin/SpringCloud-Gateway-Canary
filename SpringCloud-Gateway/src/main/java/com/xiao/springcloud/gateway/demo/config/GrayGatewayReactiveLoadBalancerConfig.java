package com.xiao.springcloud.gateway.demo.config;

import com.xiao.springcloud.gateway.demo.filter.GrayReactiveLoadBalancerFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.gateway.config.LoadBalancerProperties;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TODO 添加类的描述
 *
 * @author xiaolinlin
 * @version 1.0, 2020/5/5 15:42
 */
@Configuration
public class GrayGatewayReactiveLoadBalancerConfig {

    /**
     * TODO 添加方法描述
     *
     * @param loadBalancerClientFactory :
     * @param loadBalancerProperties :
     * @return com.xiao.springcloud.gateway.demo.filter.GrayReactiveLoadBalancerFilter
     * @author xiaolinlin  2020/5/5 - 15:43
     **/
    @Bean
    @ConditionalOnMissingBean({GrayReactiveLoadBalancerFilter.class})
    public GrayReactiveLoadBalancerFilter grayReactiveLoadBalancerFilter(LoadBalancerClientFactory loadBalancerClientFactory,
        LoadBalancerProperties loadBalancerProperties) {
        System.out.println("Init GrayReactiveLoadBalancerFilter===============================");
        return new GrayReactiveLoadBalancerFilter(loadBalancerClientFactory, loadBalancerProperties);
    }
}

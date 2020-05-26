package com.xiao.springcloud.loadbalancer.eureka.rule;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.Server;
import com.xiao.springcloud.loadbalancer.common.context.MetadataConstants;
import com.xiao.springcloud.loadbalancer.common.context.RibbonFilterContextHolder;
import com.xiao.springcloud.loadbalancer.eureka.chooser.EurekaBalancer;
import com.xiao.springcloud.loadbalancer.eureka.server.EurekaServer;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.util.CollectionUtils;

/**
 * @ClassName: EurekaWeightedRule
 * @Description: eureka 权重路由
 * @author: xiaolinlin
 * @date: 2020/5/26 18:04
 **/
@Slf4j
public class EurekaWeightedRule extends AbstractLoadBalancerRule {

    /**
     * eureka discovery
     */
    @Autowired
    private DiscoveryClient discoveryClient;

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Override
    public Server choose(Object o) {
        BaseLoadBalancer loadBalancer = (BaseLoadBalancer) this.getLoadBalancer();
        log.debug("lb = {}", loadBalancer);

        // 需要请求的微服务名称
        String serviceId = loadBalancer.getName();
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
        List<ServiceInstance> metadataMatchInstances = instances;
        String currentVersion = RibbonFilterContextHolder.getCurrentContext().get(MetadataConstants.HEADER_VERSION);
        log.debug("从请求上下文中获取到的版本version：{}", currentVersion);
        // 版本号不为空则表示带版本请求
        if (StringUtils.isNotEmpty(currentVersion)) {
            metadataMatchInstances = filter(metadataMatchInstances,
                server -> currentVersion.equals(server.getMetadata().get(MetadataConstants.HEADER_VERSION)));
            if (CollectionUtils.isEmpty(metadataMatchInstances)) {
                log.warn("未找到元数据匹配的目标实例！请检查配置。targetVersion = {}, instance = {}",
                    currentVersion, instances);
                return null;
            }
        }
        // 随机路由取一条
        //return new EurekaServer(EurekaBalancer.getByRandom(metadataMatchInstances));
        // 路由权重
        return new EurekaServer(EurekaBalancer.getByRandomWeight(metadataMatchInstances));
    }

    /**
     * 通过过滤规则过滤实例列表
     */
    private List<ServiceInstance> filter(List<ServiceInstance> serviceInstances, Predicate<ServiceInstance> predicate) {
        return serviceInstances.stream()
            .filter(predicate)
            .collect(Collectors.toList());
    }
}

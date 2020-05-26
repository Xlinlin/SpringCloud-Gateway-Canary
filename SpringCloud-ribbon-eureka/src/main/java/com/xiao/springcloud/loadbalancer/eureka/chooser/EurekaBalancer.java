package com.xiao.springcloud.loadbalancer.eureka.chooser;

import com.xiao.springcloud.loadbalancer.common.context.MetadataConstants;
import com.xiao.springcloud.loadbalancer.eureka.util.BalancerThreadLocalRandom;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.client.ServiceInstance;

/**
 * @ClassName: RibbonBalancer
 * @Description: Ribbon eureka负载
 * @author: xiaolinlin
 * @date: 2020/5/26 21:53
 **/
@Slf4j
public class EurekaBalancer {

    /**
     * 随机取一个实例
     *
     * @param serviceInstances :
     * @return org.springframework.cloud.client.ServiceInstance
     * @Author xiaolinlin
     * @Date 22:08 2020/5/26
     **/
    public static ServiceInstance getByRandom(List<ServiceInstance> serviceInstances) {

        if (null == serviceInstances || serviceInstances.size() == 0) {
            return null;
        }
        if (serviceInstances.size() == 1) {
            return serviceInstances.get(0);
        }
        return serviceInstances.get(BalancerThreadLocalRandom.current().nextInt(serviceInstances.size()));
    }

    /**
     * 按权重随机取一个服务实例
     *
     * @param serviceInstances :
     * @return org.springframework.cloud.client.ServiceInstance
     * @Author xiaolinlin
     * @Date 22:08 2020/5/26
     **/
    public static ServiceInstance getByRandomWeight(List<ServiceInstance> serviceInstances) {
        log.debug("entry randomWithWeight");
        if (serviceInstances == null || serviceInstances.size() == 0) {
            log.debug("serviceInstances == null || serviceInstances.size() == 0");
            return null;
        }
        BalancerChooser<String, ServiceInstance> chooser = new BalancerChooser<>("com.netflix.eureka");
        log.debug("new Chooser");

        List<BalancerPair<ServiceInstance>> serviceWithWeight = new ArrayList<>();
        log.debug("Set instance with weight");
        // 从metadata中获取权重，如果没有设置权重，默认权重值为1
        serviceInstances.forEach(serviceInstance ->
            serviceWithWeight.add(new BalancerPair<>(serviceInstance,
                getWeight(serviceInstance.getMetadata().get(MetadataConstants.SERVER_WEIGHT))))
        );
        log.debug("Chooser service instance");
        chooser.refresh(serviceWithWeight);
        return chooser.randomWithWeight();
    }

    private static double getWeight(String weight) {
        double w = 1d;
        if (StringUtils.isNotEmpty(weight)) {
            try {
                w = Double.parseDouble(weight);
            } catch (NumberFormatException e) {
                log.warn("Weight not double type !");
            }
        }
        return w;
    }
}

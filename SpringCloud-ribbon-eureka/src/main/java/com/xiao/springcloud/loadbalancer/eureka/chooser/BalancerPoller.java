package com.xiao.springcloud.loadbalancer.eureka.chooser;

import java.util.List;

/**
 * {@link com.alibaba.nacos.client.naming.utils.Poller}
 *
 * @ClassName: BalancerPoller
 * @Description: poller
 * @author: xiaolinlin
 * @date: 2020/5/26 21:36
 **/
public interface BalancerPoller<T> {

    /**
     * Get next element selected by poller
     *
     * @return next element
     */
    T next();

    /**
     * Update items stored in poller
     *
     * @param items new item list
     * @return new poller instance
     */
    BalancerGenericPoller<T> refresh(List<T> items);
}

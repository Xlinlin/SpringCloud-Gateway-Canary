package com.xiao.springcloud.loadbalancer.eureka.chooser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link com.alibaba.nacos.client.naming.utils.GenericPoller}
 *
 * @ClassName: BalancerGenericPoller
 * @Description: GenericPoller
 * @author: xiaolinlin
 * @date: 2020/5/26 21:37
 **/
public class BalancerGenericPoller<T> implements BalancerPoller<T> {

    private AtomicInteger index = new AtomicInteger(0);
    private List<T> items = new ArrayList<T>();

    public BalancerGenericPoller(List<T> items) {
        this.items = items;
    }

    /**
     * Get next element selected by poller
     *
     * @return next element
     */
    @Override
    public T next() {
        return items.get(Math.abs(index.getAndIncrement() % items.size()));
    }

    /**
     * Update items stored in poller
     *
     * @param items new item list
     * @return new poller instance
     */
    @Override
    public BalancerGenericPoller<T> refresh(List<T> items) {
        return new BalancerGenericPoller<T>(items);
    }
}

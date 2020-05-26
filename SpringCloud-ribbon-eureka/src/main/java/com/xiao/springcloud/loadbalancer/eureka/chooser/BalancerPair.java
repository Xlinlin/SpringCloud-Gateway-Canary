package com.xiao.springcloud.loadbalancer.eureka.chooser;

/**
 * {@link com.alibaba.nacos.client.naming.utils.Pair}
 *
 * @ClassName: BalancerPair
 * @Description: 权重对象
 * @author: xiaolinlin
 * @date: 2020/5/26 21:33
 **/
public class BalancerPair<T> {

    private T item;
    private double weight;

    public BalancerPair(T item, double weight) {
        this.item = item;
        this.weight = weight;
    }

    public T item() {
        return item;
    }

    public double weight() {
        return weight;
    }
}

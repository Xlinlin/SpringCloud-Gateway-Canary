package com.xiao.springcloud.gateway.demo.loadbalancer.weight;

import lombok.Data;

/**
 * 权重类型
 *
 * @author xiaolinlin
 * @version 1.0, 2020/5/7 10:46
 */
@Data
public class Weight {

    private String serviceId;
    private int weight;
    private int currentWeight;

    public Weight(String serviceId, int weight) {
        this.serviceId = serviceId;
        this.weight = weight;
    }
}

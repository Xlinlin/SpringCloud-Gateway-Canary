package com.xiao.springcloud.gateway.demo.loadbalancer.weight;

import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * TODO 添加类的描述
 *
 * @author xiaolinlin
 * @version 1.0, 2020/5/7 10:51
 */
public class WeightedRoundRobin {

    private volatile List<Weight> weights;
    private ReentrantLock lock = new ReentrantLock();

    public WeightedRoundRobin(List<Weight> weights) {
        this.weights = weights;
    }

    public Weight select() {
        try {
            lock.lock();
            return this.selectInner();
        } finally {
            lock.unlock();
        }
    }

    private Weight selectInner() {
        int totalWeight = 0;
        int maxWeight = 0;
        Weight mWeight = null;
        for (Weight weight1 : weights) {
            totalWeight += weight1.getWeight();
            weight1.setCurrentWeight(weight1.getCurrentWeight() + weight1.getWeight());
            // 保存当前权重最大的节点
            if (mWeight == null || maxWeight < weight1.getCurrentWeight()) {
                mWeight = weight1;
                maxWeight = weight1.getCurrentWeight();
            }
        }
        // 被选中的节点权重减掉总权重
        mWeight.setCurrentWeight(mWeight.getCurrentWeight() - totalWeight);
        return mWeight;
    }

    public static void main(String[] args) {
        List<Weight> weights = new ArrayList<>();
        weights.add(new Weight("test-1111", 4));
        weights.add(new Weight("test-222", 3));
        weights.add(new Weight("test-333", 2));
        weights.add(new Weight("test-444", 1));

        WeightedRoundRobin weightedRoundRobin = new WeightedRoundRobin(weights);

        for (int i = 0; i < 10; i++) {
            Weight select = weightedRoundRobin.select();
            System.out.println(JSON.toJSONString(select));
        }

    }
}

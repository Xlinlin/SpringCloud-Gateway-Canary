package com.xiao.springcloud.loadbalancer.common.context;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableDefault;

/**
 * TODO 添加类的描述
 *
 * @author xiaolinlin
 * @version 1.0, 2020/5/8 09:17
 */
public class HystrixRequestDefaultContext {

    private static final HystrixRequestVariableDefault<HystrixRequestDefaultContext> CURRENT_CONTEXT = new HystrixRequestVariableDefault<HystrixRequestDefaultContext>();
}

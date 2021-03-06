package com.xiao.canary.consumer.controller;

import com.xiao.canary.consumer.feign.ProviderFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * webflux消费端
 *
 * @author xiaolinlin
 * @version 1.0, 2020/5/5 17:50
 */
@RestController
@RequestMapping("/consumer")
public class ConsumerController {

    @Autowired
    private ProviderFeign providerFeign;
    @Value("${server.port}")
    private int port;

    @RequestMapping("/consumer")
    public String consumer() {
        return "consumer: " + port;
    }

    @RequestMapping("/hello")
    public String hello() {
        return providerFeign.hello();
    }
}

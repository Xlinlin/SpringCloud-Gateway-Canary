package com.xiao.springcloud.gateway.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * TODO 添加类的描述
 *
 * @author xiaolinlin
 * @version 1.0, 2020/5/5 11:29
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayDemoApp {

    public static void main(String[] args) {
        SpringApplication.run(GatewayDemoApp.class, args);
    }
}

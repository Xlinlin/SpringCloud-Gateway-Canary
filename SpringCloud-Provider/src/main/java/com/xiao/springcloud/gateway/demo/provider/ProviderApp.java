package com.xiao.springcloud.gateway.demo.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * webflux 服务端
 *
 * @author xiaolinlin
 * @version 1.0, 2020/5/5 17:44
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ProviderApp {

    public static void main(String[] args) {
        SpringApplication.run(ProviderApp.class, args);
    }
}

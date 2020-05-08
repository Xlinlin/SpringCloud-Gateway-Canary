package com.xiao.provider.webmvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * webmvc 服务端
 *
 * @author xiaolinlin
 * @version 1.0, 2020/5/7 17:50
 */
@SpringBootApplication
@EnableDiscoveryClient
public class WebMvcApp {

    public static void main(String[] args) {
        SpringApplication.run(WebMvcApp.class, args);
    }
}

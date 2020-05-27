package com.xiao.springcloud.gateway.demo.provider.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * provider
 *
 * @author xiaolinlin
 * @version 1.0, 2020/5/5 17:52
 */
@RestController
@RequestMapping("/provider")
public class ProviderController {

    @Value("${server.port}")
    private int port;


    @RequestMapping("/hello")
    public String hell() {
        return "hello provider :" + port;
    }
}

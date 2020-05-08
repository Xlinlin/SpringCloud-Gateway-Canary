package com.xiao.springcloud.webmvc.consumer.controller;

import com.xiao.springcloud.webmvc.consumer.feign.ProviderFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * webmvc controller
 *
 * @author xiaolinlin
 * @version 1.0, 2020/5/8 14:43
 */
@RestController
@RequestMapping("/webmvc")
public class WebmvcController {

    @Autowired
    private ProviderFeign providerFeign;

    @RequestMapping("/hello")
    public String hello() {
        return providerFeign.hello();
    }
}

package com.xiao.provider.webmvc.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * webmvc 服务
 *
 * @author xiaolinlin
 * @version 1.0, 2020/5/7 17:53
 */
@RequestMapping("/webmvc")
@RestController
public class WebmvcController {

    @RequestMapping("/hello")
    public String hell() {
        return "webmvc-hello";
    }
}

package com.xiao.springcloud.gateway.demo.provider.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * webflux provider
 *
 * @author xiaolinlin
 * @version 1.0, 2020/5/5 17:52
 */
@RestController
@RequestMapping("/provider")
public class ProviderController {

    @RequestMapping("/hello")
    public Mono<String> hell() {
        return Mono.just("hello provider v1");
    }
}

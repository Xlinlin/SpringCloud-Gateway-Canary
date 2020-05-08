package com.xiao.canary.consumer.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * webflux-provider
 *
 * @author xiaolinlin
 * @version 1.0, 2020/5/7 18:54
 */
@FeignClient(name = "hello-provider", path = "/provider")
public interface ProviderFeign {

    @RequestMapping("/hello")
    String hello();
}

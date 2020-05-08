package com.xiao.springcloud.webmvc.consumer.loadbalancer.interceptor;

import com.xiao.springcloud.webmvc.consumer.loadbalancer.common.MetadataConstants;
import com.xiao.springcloud.webmvc.consumer.loadbalancer.common.RibbonFilterContext;
import com.xiao.springcloud.webmvc.consumer.loadbalancer.common.RibbonFilterContextHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * feign拦截，将header信息保存到上下文中传递参数
 *
 * @author xiaolinlin
 * @version 1.0, 2020/5/8 09:46
 */
@Slf4j
public class FeignRequestInterceptor implements RequestInterceptor {

    /**
     * Called for every request. Add data using methods on the supplied {@link RequestTemplate}.
     *
     * @param template
     */
    @Override
    public void apply(RequestTemplate template) {
        RibbonFilterContext ribbonFilterContext = RibbonFilterContextHolder.getCurrentContext();
        String version = ribbonFilterContext.get(MetadataConstants.HEADER_VERSION);
        if (StringUtils.isNotEmpty(version)) {
            log.info("2.FeignRequestInterceptor 设置header到resttemplate中,version: {}", version);
            template.header(MetadataConstants.HEADER_VERSION, version);
        }
    }
}

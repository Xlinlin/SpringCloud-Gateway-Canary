package com.xiao.canary.consumer.interceptor;

import com.xiao.springcloud.loadbalancer.common.MetadataConstants;
import com.xiao.springcloud.loadbalancer.common.RibbonFilterContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * webflux的请求拦截，将version保存到上下文中
 *
 * @author xiaolinlin
 * @version 1.0, 2020/5/8 10:30
 */
@Component
@Order(1)
public class WebfluxRequestFilter implements WebFilter {

    /**
     * Process the Web request and (optionally) delegate to the next {@code WebFilter} through the given {@link
     * WebFilterChain}.
     *
     * @param exchange the current server exchange
     * @param chain provides a way to delegate to the next filter
     * @return {@code Mono<Void>} to indicate when request processing is complete
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String version = exchange.getRequest().getHeaders().getFirst(MetadataConstants.HEADER_VERSION);
        RibbonFilterContextHolder.getCurrentContext().add(MetadataConstants.HEADER_VERSION, version);
        return chain.filter(exchange);
    }
}

package com.xiao.springcloud.loadbalancer.interceptor;

import com.xiao.springcloud.loadbalancer.common.MetadataConstants;
import com.xiao.springcloud.loadbalancer.common.RibbonFilterContextHolder;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 请求头拦截，仅针对webmvc的
 *
 * <p>
 * 实现WebMvcConfigurer.addInterceptors
 * <p>
 * registry.addInterceptor(getInterceptor()).addPathPatterns("/**");
 * </p>
 *
 * @author xiaolinlin
 * @version 1.0, 2020/5/8 08:55
 */
@Slf4j
public class HttpRequestInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {
        // 获取头部header
        Enumeration<String> headers = request.getHeaders(MetadataConstants.HEADER_VERSION);
        if (headers.hasMoreElements()) {
            String version = headers.nextElement();
            log.debug("1. HttpRequestInterceptor(webmvc)拦截将请求header设置到Context中，供ribbon使用，当前header信息Key:{} Value:{}",
                MetadataConstants.HEADER_VERSION,
                version);
            RibbonFilterContextHolder.getCurrentContext().add(MetadataConstants.HEADER_VERSION, version);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
        throws Exception {
        RibbonFilterContextHolder.clearCurrentContext();
    }
}

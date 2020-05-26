package com.xiao.springcloud.loadbalancer.common.interceptor;

import com.xiao.springcloud.loadbalancer.common.context.MetadataConstants;
import com.xiao.springcloud.loadbalancer.common.context.RibbonFilterContext;
import com.xiao.springcloud.loadbalancer.common.context.RibbonFilterContextHolder;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;

/**
 * Request template 拦截
 * <p>
 * restTemplate.setInterceptors(ris);
 * </p>
 *
 * @author xiaolinlin
 * @version 1.0, 2020/5/8 09:51
 */
@Slf4j
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

    /**
     * Intercept the given request, and return a response. The given {@link ClientHttpRequestExecution} allows the interceptor
     * to pass on the request and response to the next entity in the chain.
     * <p>A typical implementation of this method would follow the following pattern:
     * <ol>
     * <li>Examine the {@linkplain HttpRequest request} and body</li>
     * <li>Optionally {@linkplain HttpRequestWrapper
     * wrap} the request to filter HTTP attributes.</li>
     * <li>Optionally modify the body of the request.</li>
     * <li><strong>Either</strong>
     * <ul>
     * <li>execute the request using
     * {@link ClientHttpRequestExecution#execute(HttpRequest, byte[])},</li>
     * <strong>or</strong>
     * <li>do not execute the request to block the execution altogether.</li>
     * </ul>
     * <li>Optionally wrap the response to filter HTTP attributes.</li>
     * </ol>
     *
     * @param request the request, containing method, URI, and headers
     * @param body the body of the request
     * @param execution the request execution
     * @return the response
     * @throws IOException in case of I/O errors
     */
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
        throws IOException {
        HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);
        RibbonFilterContext currentContext = RibbonFilterContextHolder.getCurrentContext();
        String version = currentContext.getAttributes().get(MetadataConstants.HEADER_VERSION);
        if (StringUtils.isNotBlank(version)) {
            log.debug("3.RestTemplateInterceptor 拦截，设置Header到RestTemplate中，version: {}", version);
            requestWrapper.getHeaders().add(MetadataConstants.HEADER_VERSION, version);
        }
        return execution.execute(requestWrapper, body);
    }
}

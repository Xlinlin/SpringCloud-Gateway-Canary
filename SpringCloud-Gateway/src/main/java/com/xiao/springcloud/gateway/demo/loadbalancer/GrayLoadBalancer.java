package com.xiao.springcloud.gateway.demo.loadbalancer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.reactive.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.reactive.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.reactive.Request;
import org.springframework.cloud.client.loadbalancer.reactive.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 负载均衡策略
 *
 * @author xiaolinlin
 * @version 1.0, 2020/5/5 15:34
 */
public class GrayLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    private static final Log log = LogFactory.getLog(GrayLoadBalancer.class);
    private ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;
    private String serviceId;

    private final AtomicInteger position;

    public GrayLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider, String serviceId) {
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
        this.position = new AtomicInteger(new Random().nextInt(1000));
    }

    /**
     * Choose the next server based on the load balancing algorithm.
     *
     * @param request - an input request
     * @return - mono of response
     */
    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        HttpHeaders headers = (HttpHeaders) request.getContext();
        if (this.serviceInstanceListSupplierProvider != null) {
            ServiceInstanceListSupplier supplier = this.serviceInstanceListSupplierProvider
                .getIfAvailable(NoopServiceInstanceListSupplier::new);
            return ((Flux) supplier.get()).next().map(list -> getInstanceResponse((List<ServiceInstance>) list, headers));
        }

        // ServiceInstanceListSupplier supplier = this.serviceInstanceListSupplierProvider
        //     .getIfAvailable(NoopServiceInstanceListSupplier::new);
        return null;
    }

    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances, HttpHeaders headers) {
        if (instances.isEmpty()) {
            return getServiceInstanceEmptyResponse();
        } else {
            return getServiceInstanceResponseByVersion(instances, headers);
        }
    }


    /**
     * 根据版本进行分发
     *
     * @param instances
     * @param headers
     * @return
     */
    private Response<ServiceInstance> getServiceInstanceResponseByVersion(List<ServiceInstance> instances,
        HttpHeaders headers) {

        // 要确保 version中有值
        String versionNo = headers.getFirst("version");

        //能使用的instance
        List<ServiceInstance> canUseInstance = new ArrayList<>();
        if (StringUtils.isNotEmpty(versionNo)) {
            // 依据版本号做请求分发
            System.out.println("Header version:" + versionNo);
            List<ServiceInstance> currentVersionInstances = new ArrayList<>();

            instances.forEach(serviceInstance -> {
                // 迭代所有服务版本号
                final Map<String, String> metadata = serviceInstance.getMetadata();
                String version = metadata.get("version");
                System.out.println("-->version: " + version);
                if (versionNo.equals(version)) {
                    currentVersionInstances.add(serviceInstance);
                }
            });
            canUseInstance.addAll(currentVersionInstances);
        } else {
            canUseInstance.addAll(instances);
        }

        // 如果服务为空返回空
        if (CollectionUtils.isEmpty(canUseInstance)) {
            return getServiceInstanceEmptyResponse();
        }

        // 可用服务做负载均衡,轮询、权重等
        canUseInstance.forEach(canInstance -> {
            final Map<String, String> metadata = canInstance.getMetadata();
            // nacos上的权重
            String weight = metadata.get("nacos.weight");
            System.out.println("ServiceId: " + canInstance.getServiceId() + " Weight: " + weight);
            // 先随机取一个
        });

        // 先走默认轮询
        return getInstanceResponse(canUseInstance);
    }

    private Response<ServiceInstance> getServiceInstanceEmptyResponse() {
        log.warn("No servers available for service: " + this.serviceId);
        return new EmptyResponse();
    }

    private Response<ServiceInstance> getInstanceResponse(
        List<ServiceInstance> instances) {
        if (instances.isEmpty()) {
            log.warn("No servers available for service: " + this.serviceId);
            return new EmptyResponse();
        }
        // TODO: enforce order?
        int pos = Math.abs(this.position.incrementAndGet());

        ServiceInstance instance = instances.get(pos % instances.size());

        return new DefaultResponse(instance);
    }
}

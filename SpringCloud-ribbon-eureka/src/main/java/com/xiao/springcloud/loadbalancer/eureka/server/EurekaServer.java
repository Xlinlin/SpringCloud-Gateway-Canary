package com.xiao.springcloud.loadbalancer.eureka.server;

import com.netflix.loadbalancer.Server;
import java.util.Map;
import org.springframework.cloud.client.ServiceInstance;

/**
 * @ClassName: EurekaServer
 * @Description: eureka server
 * @author: xiaolinlin
 * @date: 2020/5/26 22:41
 **/
public class EurekaServer extends Server {

    private final ServiceInstance serviceInstance;

    private final MetaInfo metaInfo;

    private final Map<String, String> metadata;

    public EurekaServer(final ServiceInstance serviceInstance) {
        super(serviceInstance.getHost(), serviceInstance.getPort());
        this.serviceInstance = serviceInstance;

        metaInfo = new MetaInfo() {
            @Override
            public String getAppName() {
                return serviceInstance.getServiceId();
            }

            @Override
            public String getServerGroup() {
                return null;
            }

            @Override
            public String getServiceIdForDiscovery() {
                return null;
            }

            @Override
            public String getInstanceId() {
                return serviceInstance.getServiceId();
            }
        };

        metadata = serviceInstance.getMetadata();
    }

    @Override
    public MetaInfo getMetaInfo() {
        return metaInfo;
    }

    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }
}

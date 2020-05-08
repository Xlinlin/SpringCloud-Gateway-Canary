1.SpringCloud Gateway 源码导读整理<br>
* META-INF/spring.factories
* GatewayClassPathWarningAutoConfiguration
  * ``DispatcherServlet`` webmvc检测存在警告，``DispatcherHandler`` webflux不存在检测警告
* GatewayAutoConfiguration
  * 必须是在webflux环境下``DispatcherHandler``
  * 初始化Route相关：``RouteLocatorBuilder、PropertiesRouteDefinitionLocator、InMemoryRouteDefinitionRepository、RouteLocator``
  * ``FilteringWebHandler、RouteRefreshListener、ConfigurationService、RoutePredicateHandlerMapping、WebSocketService``
  * 配置：``GlobalCorsProperties、GatewayProperties、SecureHeadersProperties``
  * Filter：``ForwardedHeadersFilter、RemoveHopByHopHeadersFilter、XForwardedHeadersFilter、AdaptCachedBodyGlobalFilter、RemoveCachedBodyFilter、
RouteToRequestUrlFilter、ForwardRoutingFilter、ForwardPathFilter、WebsocketRoutingFilter、WeightCalculatorWebFilter``
  * Predicate BeanFactory：``AfterRoutePredicateFactory、BeforeRoutePredicateFactory、BetweenRoutePredicateFactory、CookieRoutePredicateFactory、
HeaderRoutePredicateFactory、HostRoutePredicateFactory、MethodRoutePredicateFactory、PathRoutePredicateFactory、QueryRoutePredicateFactory、
ReadBodyPredicateFactory、RemoteAddrRoutePredicateFactory、WeightRoutePredicateFactory、CloudFoundryRouteServiceRoutePredicateFactory``
  * Filter BeanFactory：``AddRequestHeaderGatewayFilterFactory、MapRequestHeaderGatewayFilterFactory、AddRequestParameterGatewayFilterFactory、
AddResponseHeaderGatewayFilterFactory、ModifyRequestBodyGatewayFilterFactory、DedupeResponseHeaderGatewayFilterFactory、ModifyResponseBodyGatewayFilterFactory、
PrefixPathGatewayFilterFactory、PreserveHostHeaderGatewayFilterFactory、RedirectToGatewayFilterFactory、RemoveRequestHeaderGatewayFilterFactory、
RemoveRequestParameterGatewayFilterFactory、RemoveResponseHeaderGatewayFilterFactory、RequestRateLimiterGatewayFilterFactory、RewritePathGatewayFilterFactory、
RetryGatewayFilterFactory、SetPathGatewayFilterFactory、SecureHeadersGatewayFilterFactory、SetRequestHeaderGatewayFilterFactory、SetResponseHeaderGatewayFilterFactory、
RewriteResponseHeaderGatewayFilterFactory、RewriteLocationResponseHeaderGatewayFilterFactory、SetStatusGatewayFilterFactory、SaveSessionGatewayFilterFactory
StripPrefixGatewayFilterFactory、RequestHeaderToRequestUriGatewayFilterFactory、RequestSizeGatewayFilterFactory、RequestHeaderSizeGatewayFilterFactory``
  * Resolver：``PrincipalNameKeyResolver、GzipMessageBodyResolver``
  * NettyConfiguration：``HttpClient、HttpClientProperties、NettyRoutingFilter、NettyWriteResponseFilter、ReactorNettyWebSocketClient、NettyWebServerFactoryCustomizer``

* GatewayHystrixCircuitBreakerAutoConfiguration
  * Hystrix熔断：``SpringCloudCircuitBreakerHystrixFilterFactory、FallbackHeadersGatewayFilterFactory``

* GatewayResilience4JCircuitBreakerAutoConfiguration
  * Resilience4J熔断，Java8和函数式编程设计的轻量级容错框架：``SpringCloudCircuitBreakerResilience4JFilterFactory、FallbackHeadersGatewayFilterFactory``

* GatewayLoadBalancerClientAutoConfiguration
  * 负载均衡：``LoadBalancerClientFilter``

* GatewayNoLoadBalancerClientAutoConfiguration
  * 不需要负载均衡  404？

* GatewayMetricsAutoConfiguration
  * 监控数据：``GatewayHttpTagsProvider、GatewayRouteTagsProvider、PropertiesTagsProvider、GatewayMetricsFilter``

* GatewayRedisAutoConfiguration
  * Redis支持,redis实现限流：``RedisScript、RedisRateLimiter``

* GatewayDiscoveryClientAutoConfiguration
  * 注册中心做服务发现，默认
    ```html
    Predicat：pattern='/'+serviceId+'/**'形式
    Filter：regexp='/' + serviceId + '/(?<remaining>.*)'、replacement='/${remaining}'
    ```
* DiscoveryClientRouteDefinitionLocator
* SimpleUrlHandlerMappingGlobalCorsAutoConfiguration
* GatewayReactiveLoadBalancerClientAutoConfiguration
  * 替代GatewayLoadBalancerClientAutoConfiguration？ ``ReactiveLoadBalancerClientFilter``

* 请求流程
  * client-> DispatcherHandler(请求调度) --> RoutePredicateHandlerMapping(lookupRoute)(路由匹配) 
--> SimpleHandlerAdapter --> FilteringWebHandler(filters) --> NettyRoutingFilter(proxy filter) --> services
  * DispatcherHandler.initStrategies 到Contenxt上下文查找所有的HandlerMapping，走到对应的RoutePredicateHandlerMapping
  * RoutePredicateHandlerMapping.lookupRoute查找路由并过滤不符合Predicate定义的路由，将路由信息设置当上下文环境中，返回gatway自定的webhandler（FilteringWebHandler）
    exchange.getAttributes().put(GATEWAY_HANDLER_MAPPER_ATTR, getClass().getSimpleName());<br>
    exchange.getAttributes().put(GATEWAY_ROUTE_ATTR, r);<br>
    exchange.getAttributes().put(GATEWAY_PREDICATE_ROUTE_ATTR, route.getId());<br>
  * FilteringWebHandler.handle <br>
    对所有全局GlobalFilter包装成GatewayFilter<br>
    获取上下文中路由信息exchange.getRequiredAttribute(GATEWAY_ROUTE_ATTR)<br>
    进行路由排序，然后执行filter调用链DefaultGatewayFilterChain(GatewayFilterChain).filter<br>
  * RouteLocator: 获取路由类型，
    * Route组成三部分：Proxy(代理信息，service)、Predicate(谓语，请求方法、path等)、Filter(自定义过滤)<br>
    * Route的三种来源：
        * 1.``RouteLocatorBuilder``方式，代码中通过RouteLocatorBuilder方式构建的路由
        * 2.``Properties``配置方式，通过本地配置``PropertiesRouteDefinitionLocator``和服务发现``DiscoveryClientRouteDefinitionLocator``
        * 3.``RouteDefinitionRepository``持久存储，内置``InMemoryRouteDefinitionRepository``,可扩展mysql&Redis等

2.SpringCloud API Gateway+Nacos实现多版本访问：
* 先实现一个全局的拦截filter ``GrayReactiveLoadBalancerFilter``，其加载的顺序Order必须与``ReactiveLoadBalancerClientFilter``保持一直或提前
* 实现一个自定义的LoadBalancer ``GrayLoadBalancer``实现接口: ``ReactorServiceInstanceLoadBalancer``

3.全链路多版本路由请求 
* webmvc+nacos+feign+ribbon版本实现
  * 主要实现思路：  client(header: version=v1)->webmvc->feign->ribbon->service
     * 服务注册到nacos时添加版本号元数据
     ```yaml
        spring:
          application:
            name: hello-provider
          cloud:
            nacos:
              discovery:
                server-addr: localhost:8848
                metadata:
                  version: v1
     ```
     * 通过拦截 webmvc的``HandlerInterceptor``将request的header保存到ThreadLocal中，以及在``RequestInterceptor``(Feign)做header传递
     * 在ribbon层实现自己的路由算法``com.xiao.springcloud.webmvc.consumer.loadbalancer.ribbon.NacosWeightedRule``，通过ThreadLocal获取header进行路由
     * 客户端针对路由指定负载均衡算法：
     ```yaml
     service_id:
       ribbon:
         NFLoadBalancerRuleClassName: com.xiao.springcloud.loadbalancer.ribbon.NacosWeightedRule
     ```
     * 思考如何将com.xiao.springcloud.webmvc.consumer.loadbalancer做成通用的组件starter，供客户端直接引用
* webflux 待定
  * 如何做好header的在上下文传递以及在做负载均衡的时候如何做版本负载
  
4.参考资料<br>
[Spring Cloud Alibaba之负载均衡组件 - Ribbon](https://blog.51cto.com/zero01/2424180)

[基于springcloud gateway + nacos实现灰度发布（reactive版）](https://www.cnblogs.com/linyb-geek/p/12774014.html)
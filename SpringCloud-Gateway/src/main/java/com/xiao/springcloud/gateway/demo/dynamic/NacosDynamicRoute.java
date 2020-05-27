// package com.xiao.springcloud.gateway.demo.dynamic;
//
// import com.alibaba.fastjson.JSON;
// import com.alibaba.nacos.api.NacosFactory;
// import com.alibaba.nacos.api.config.ConfigService;
// import com.alibaba.nacos.api.config.listener.Listener;
// import com.alibaba.nacos.api.exception.NacosException;
// import java.util.concurrent.Executor;
// import java.util.concurrent.Executors;
// import java.util.concurrent.ScheduledExecutorService;
// import java.util.concurrent.TimeUnit;
// import java.util.concurrent.atomic.AtomicBoolean;
// import javax.annotation.Resource;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.cloud.gateway.route.RouteDefinition;
// import org.springframework.context.ApplicationContextInitializer;
// import org.springframework.context.ConfigurableApplicationContext;
// import org.springframework.stereotype.Component;
//
// /**
//  * Nacos配置中心动态路由
//  *
//  * @author xiaolinlin
//  * @version 1.0, 2020/5/6 16:08
//  */
// @Component
// @Slf4j
// public class NacosDynamicRoute implements ApplicationContextInitializer {
//
//     @Value("${nacos.host:127.0.0.1:8488}")
//     private String nacosHost;
//     @Value("${nacos.api.gateway.dataid:nacos-gateway}")
//     private String dataId;
//     @Value("${nacos.api.gateway.group:nacos-gateway}")
//     private String group;
//
//     @Resource(name = "basicDynamicRoute")
//     private DynamicRouteService dynamicRouteService;
//
//     private AtomicBoolean isStart = new AtomicBoolean(false);
//
//     private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(r -> {
//         Thread t = new Thread(r);
//         t.setName("Check-Nacos-Server-Task");
//         return t;
//     });
//
//
//     /**
//      * 更新路由
//      *
//      * @return boolean
//      * @author xiaolinlin  2020/5/6 - 16:12
//      **/
//     public void updateRoute() {
//         if (isStart.compareAndSet(false, true)) {
//             try {
//                 // 考虑健康
//                 ConfigService configService = NacosFactory.createConfigService(nacosHost);
//                 configService.getConfig(dataId, group, 5000);
//                 configService.addListener(dataId, group, new Listener() {
//                     @Override
//                     public Executor getExecutor() {
//                         return null;
//                     }
//
//                     @Override
//                     public void receiveConfigInfo(String configInfo) {
//                         RouteDefinition definition = JSON.parseObject(configInfo, RouteDefinition.class);
//                         dynamicRouteService.updateRoute(definition);
//                     }
//                 });
//             } catch (NacosException e) {
//                 log.error("NacosException: ", e);
//                 isStart.set(false);
//             }
//         }
//     }
//
//     /**
//      * Initialize the given application context.
//      *
//      * @param applicationContext the application to configure
//      */
//     @Override
//     public void initialize(ConfigurableApplicationContext applicationContext) {
//         System.out.println("Update route!");
//         scheduledExecutorService.scheduleWithFixedDelay(() -> updateRoute(), 1, 2, TimeUnit.SECONDS);
//     }
// }

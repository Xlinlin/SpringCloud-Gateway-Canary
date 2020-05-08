package com.xiao.springcloud.gateway.demo.dynamic;

import org.springframework.cloud.gateway.route.RouteDefinition;

/**
 * 动态路由发现服务
 *
 * @author xiaolinlin
 * @version 1.0, 2020/5/6 15:43
 */
public interface DynamicRouteService {

    /**
     * 添加一个路由
     *
     * @param routeDefinition :
     * @return boolean
     * @author xiaolinlin  2020/5/6 - 15:43
     **/
    boolean addRoute(RouteDefinition routeDefinition);

    /**
     * 更新路由
     *
     * @param routeDefinition :
     * @return boolean
     * @author xiaolinlin  2020/5/6 - 15:44
     **/
    boolean updateRoute(RouteDefinition routeDefinition);

    /**
     * 删除一个路由信息
     *
     * @param routeId :
     * @return boolean
     * @author xiaolinlin  2020/5/6 - 15:45
     **/
    boolean delRoute(String routeId);

    /**
     * 通过路由ID查找一个路由
     *
     * @param routeId :
     * @return org.springframework.cloud.gateway.route.RouteDefinition
     * @author xiaolinlin  2020/5/6 - 15:46
     **/
    RouteDefinition findById(String routeId);
}

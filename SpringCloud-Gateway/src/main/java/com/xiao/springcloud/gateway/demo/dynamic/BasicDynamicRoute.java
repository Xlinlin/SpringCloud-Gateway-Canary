package com.xiao.springcloud.gateway.demo.dynamic;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * 动态路由基本类
 *
 * @author xiaolinlin
 * @version 1.0, 2020/5/6 15:46
 */
@Slf4j
@Service("basicDynamicRoute")
public class BasicDynamicRoute implements ApplicationEventPublisherAware, DynamicRouteService {

    private ApplicationEventPublisher publisher;

    @Autowired
    private RouteDefinitionWriter routeDefinitionWriter;


    /**
     * 添加一个路由
     *
     * @param definition :
     * @return boolean
     * @author xiaolinlin  2020/5/6 - 15:56
     **/
    public boolean save(RouteDefinition definition) {
        if (null == definition) {
            return false;
        }
        routeDefinitionWriter.save(Mono.just(definition)).subscribe();
        publisher.publishEvent(new RefreshRoutesEvent(this));
        return true;
    }

    /**
     * 更新一个路由
     *
     * @param definition :
     * @return boolean
     * @author xiaolinlin  2020/5/6 - 15:57
     **/
    public boolean update(RouteDefinition definition) {
        if (null == definition || StringUtils.isEmpty(definition.getId())) {
            this.delete(definition.getId());
        }
        return this.save(definition);
    }

    /**
     * 通过路由ID删除一个路由
     *
     * @param id
     * @return
     */
    public boolean delete(String id) {
        try {
            this.routeDefinitionWriter.delete(Mono.just(id));
        } catch (NotFoundException e) {
            log.error("update fail,not find route  routeId: " + id);
            return false;
        }
        return true;
    }


    /**
     * Set the ApplicationEventPublisher that this object runs in.
     * <p>Invoked after population of normal bean properties but before an init
     * callback like InitializingBean's afterPropertiesSet or a custom init-method. Invoked before ApplicationContextAware's
     * setApplicationContext.
     *
     * @param applicationEventPublisher event publisher to be used by this object
     */
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    /**
     * 添加一个路由
     *
     * @param routeDefinition :
     * @return boolean
     * @author xiaolinlin  2020/5/6 - 15:43
     **/
    @Override
    public boolean addRoute(RouteDefinition routeDefinition) {
        return this.save(routeDefinition);
    }

    /**
     * 更新路由
     *
     * @param routeDefinition :
     * @return boolean
     * @author xiaolinlin  2020/5/6 - 15:44
     **/
    @Override
    public boolean updateRoute(RouteDefinition routeDefinition) {
        return this.update(routeDefinition);
    }

    /**
     * 删除一个路由信息
     *
     * @param routeId :
     * @return boolean
     * @author xiaolinlin  2020/5/6 - 15:45
     **/
    @Override
    public boolean delRoute(String routeId) {
        return this.delete(routeId);
    }

    /**
     * 通过路由ID查找一个路由
     *
     * @param routeId :
     * @return org.springframework.cloud.gateway.route.RouteDefinition
     * @author xiaolinlin  2020/5/6 - 15:46
     **/
    @Override
    public RouteDefinition findById(String routeId) {
        return null;
    }
}

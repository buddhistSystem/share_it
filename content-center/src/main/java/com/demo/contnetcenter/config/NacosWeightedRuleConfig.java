package com.demo.contnetcenter.config;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * 配置Ribbo支持Nacos权重配置
 */
@Slf4j
public class NacosWeightedRuleConfig extends AbstractLoadBalancerRule {

    @Resource
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    /**
     * 读取配置文件并且初始化NacosWeightedRule
     *
     * @param iClientConfig
     */
    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Override
    public Server choose(Object o) {
        BaseLoadBalancer loadBalancer = (BaseLoadBalancer) this.getLoadBalancer();

        //要请求的服务名称
        String name = loadBalancer.getName();

        /**
         * 实现负载均衡算法
         */
        //拿到服务发现的相关API
        NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();
        //Nacos client自动通过基于权重的负载均衡算法选择一个实例i
        try {
            Instance instance = namingService.selectOneHealthyInstance(name);
            log.info("被调用的NacosClient是：{}", instance);
            return new NacosServer(instance);
        } catch (NacosException e) {
            log.error(e.getErrMsg());
            return null;
        }

    }
}

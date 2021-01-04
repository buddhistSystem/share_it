package com.demo.contentcenter.config;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.core.Balancer;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 配置Ribbo支持Nacos权重配置,同集群优先调用
 */
@Slf4j
public class NacosSameClusterWeightedRuleConfig extends AbstractLoadBalancerRule {

    @Resource
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Override
    public Server choose(Object o) {

        //获得配置文件中的集群名称
        String clusterName = nacosDiscoveryProperties.getClusterName();
        //获得想要调用的服务名称
        BaseLoadBalancer loadBalancer = (BaseLoadBalancer) this.getLoadBalancer();
        String name = loadBalancer.getName();
        //拿到服务发现的相关API
        NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();

        try {
            //找到该服务所有节点
            List<Instance> instances = namingService.selectInstances(name, true);
            //获取同集群下所有节点
            List<Instance> sameClusterInstance = instances.
                    stream().
                    filter(instance -> Objects.equals(instance.getClusterName(), clusterName))
                    .collect(Collectors.toList());
            List<Instance> resultInstances = new ArrayList<>();
            if (CollectionUtils.isEmpty(sameClusterInstance)) {
                resultInstances = instances;
                log.warn("发生跨集群调用,name={},clusterName={},instances={}", name, clusterName, instances);
            } else {
                resultInstances = sameClusterInstance;
            }
            //基于权重选择一个实例
            Instance instance = ExtendsBalancer.getOneInstanceByRandomWeight(resultInstances);
            log.info("选择的实例 instance={}", instance);
            return new NacosServer(instance);

        } catch (NacosException e) {
            log.error(e.getErrMsg());
            return null;
        }

    }
}

/**
 * 继承Balancer 调用其基于权重的负载均衡算法
 */
class ExtendsBalancer extends Balancer {
    public static Instance getOneInstanceByRandomWeight(List<Instance> hosts) {
        return getHostByRandomWeight(hosts);
    }
}

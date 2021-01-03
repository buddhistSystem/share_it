package com.demo.contnetcenter.config;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Configuration;
import ribbonconfiguration.RibbonConfiguration;

/**
 * 配置单个服务user-center微服务负载均衡策略
 * 若有其他服务，按照此方式也可实现
 *
 * 暂时弃用
 */
//@Configuration
//@RibbonClient(name = "user-center", configuration = RibbonConfiguration.class)
public class UserCenterRibbonConfig {
}

package com.demo.contnetcenter.config;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Configuration;
import ribbonconfiguration.RibbonConfiguration;

/**
 * user-center 微服务负载均衡策略
 *
 * 暂时弃用，采用配置文件配置
 */
//@Configuration
//@RibbonClient(name = "user-center", configuration = RibbonConfiguration.class)
public class UserCenterRibbonConfig {
}

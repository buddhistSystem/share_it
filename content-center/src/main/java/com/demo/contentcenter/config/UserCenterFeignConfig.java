package com.demo.contentcenter.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;

/**
 * user-center服务Feign客户端配置类
 * 若添加@Configuration 注解，则该配置类必须在启动类上层，否则产生上下文重叠
 * 所有的Feign客户端都会使用该类的配置
 */
public class UserCenterFeignConfig {

    /**
     * 配置feign日志级别
     * NONE  默认
     * BASIC 仅记录请求方法，url，响应状态码，执行时间
     * HEADERS 在BASIC基础上记录请求和响应的header
     * FULL 最大日志级别
     *
     * @return
     */
    @Bean
    public Logger.Level level() {
        return Logger.Level.FULL;
    }
}

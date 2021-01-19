package com.demo.contentcenter.config;

import com.alibaba.cloud.sentinel.annotation.SentinelRestTemplate;
import com.demo.contentcenter.feignclient.interceptor.RestTemplateRequestInterceptor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
public class RestTemplateConfig {

    /**
     * @return RestTemplate
     * @LoadBalanced 注解集成Ribbon
     */
    @Bean
    @LoadBalanced
    @SentinelRestTemplate
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        //为restTemplate设置拦截器传递token
        restTemplate.setInterceptors(Collections.singletonList(
                new RestTemplateRequestInterceptor()
        ));
        return restTemplate;
    }

}

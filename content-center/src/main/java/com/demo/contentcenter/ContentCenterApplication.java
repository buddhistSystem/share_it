package com.demo.contentcenter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.spring.annotation.MapperScan;

@EnableFeignClients
@MapperScan("com.demo.contentcenter.dao")
@SpringBootApplication
@RestController
@EnableBinding(Source.class)
public class ContentCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContentCenterApplication.class, args);
    }

    @GetMapping("ping")
    public String ping() {
        return "pong";
    }

    @Value("config")
    private String config;

    @GetMapping("config")
    public String config() {
        return config;
    }

}

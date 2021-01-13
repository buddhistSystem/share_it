package com.demo.contentcenter.feignclient;

import com.demo.contentcenter.domain.dto.user.UserDto;
import com.demo.contentcenter.feignclient.fallbackFactory.UserCenterFeignClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * user-center服务feign客户端
 * configuration可以指定单个feignClient的自定义配置，目前采用全局配置，暂时弃用
 *
 * fallback 和 fallbackFactory 使用其中一个，fallbackFactory可以拿到异常
 */
@FeignClient(name = "user-center",
        //fallback = UserCenterFeignClientFallback.class,
        fallbackFactory = UserCenterFeignClientFallbackFactory.class
)
//@FeignClient(name = "user-center",configuration = UserCenterFeignConfig.class)
public interface UserCenterFeignClient {

    @GetMapping("/users/{id}")
    UserDto findById(@PathVariable Integer id);
}

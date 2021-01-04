package com.demo.contentcenter.feignclient;

import com.demo.contentcenter.domain.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * user-center服务feign客户端
 * configuration可以指定单个feignClient的自定义配置，目前采用全局配置，暂时弃用
 */
@FeignClient(name = "user-center")
//@FeignClient(name = "user-center",configuration = UserCenterFeignConfig.class)
public interface UserCenterFeignClient {

    @GetMapping("/users/{id}")
    UserDto findById(@PathVariable Integer id);
}

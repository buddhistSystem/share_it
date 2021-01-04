package com.demo.contentcenter.feignclient;

import com.demo.contentcenter.config.UserCenterFeignConfig;
import com.demo.contentcenter.domain.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * user-center服务feign客户端
 */
@FeignClient(name = "user-center",configuration = UserCenterFeignConfig.class)
public interface UserCenterFeignClient {

    @GetMapping("/users/{id}")
    UserDto findById(@PathVariable Integer id);
}

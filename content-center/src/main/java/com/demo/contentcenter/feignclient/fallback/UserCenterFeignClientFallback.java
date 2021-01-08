package com.demo.contentcenter.feignclient.fallback;

import com.demo.contentcenter.domain.dto.user.UserDto;
import com.demo.contentcenter.feignclient.UserCenterFeignClient;
import org.springframework.stereotype.Component;

/**
 * user-center 服务降级类
 */
@Component
public class UserCenterFeignClientFallback implements UserCenterFeignClient {


    @Override
    public UserDto findById(Integer id) {
        UserDto userDto = new UserDto();
        userDto.setWxNickname("服务发生错误，这是默认返回值");
        return userDto;
    }
}

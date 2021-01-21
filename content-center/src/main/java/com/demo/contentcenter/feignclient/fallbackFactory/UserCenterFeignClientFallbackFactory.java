package com.demo.contentcenter.feignclient.fallbackFactory;

import com.demo.contentcenter.domain.dto.user.UserAddBonusDto;
import com.demo.contentcenter.domain.dto.user.UserDto;
import com.demo.contentcenter.feignclient.UserCenterFeignClient;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserCenterFeignClientFallbackFactory implements FallbackFactory<UserCenterFeignClient> {
    @Override
    public UserCenterFeignClient create(Throwable cause) {
        return new UserCenterFeignClient() {
            @Override
            public UserDto findById(Integer id) {
                log.warn("远程调用被限流，或者降级");
                UserDto userDto = new UserDto();
                userDto.setWxNickname("服务发生错误限流或者降级，这是默认返回值");
                return userDto;
            }

            @Override
            public UserDto addBonus(UserAddBonusDto userAddBonusDto) {
                UserDto userDto = new UserDto();
                userDto.setWxNickname("添加积分熔断，或限流！");
                return userDto;
            }
        };
    }
}

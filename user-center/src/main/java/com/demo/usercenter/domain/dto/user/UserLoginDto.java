package com.demo.usercenter.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDto {

    /**
     * 微信登录产生的code
     */
    private String code;

    /**
     * 头像地址
     */
    private String avatarUrl;

    /**
     * 微信昵称
     */
    private String wxNickName;
}

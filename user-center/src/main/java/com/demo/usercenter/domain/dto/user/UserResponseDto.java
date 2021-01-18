package com.demo.usercenter.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    /**
     * 用户id
     */
    private Integer id;

    /**
     * 头像地址
     */
    private String avatarUrl;

    /**
     * 积分
     */
    private Integer bonus;

    /**
     * 微信昵称
     */
    private String wxNickName;

}

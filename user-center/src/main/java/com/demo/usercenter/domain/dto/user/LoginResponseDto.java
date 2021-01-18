package com.demo.usercenter.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {

    /**
     * token信息
     */
    private JwtTokenResponseDto token;

    /**
     * 用户信息
     */
    private UserResponseDto user;
}

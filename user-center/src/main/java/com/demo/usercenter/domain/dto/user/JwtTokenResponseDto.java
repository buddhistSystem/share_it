package com.demo.usercenter.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenResponseDto {

    /**
     * 生成的token
     */
    private String token;

    /**
     * 过期事件
     */
    private Long expirationTime;
}

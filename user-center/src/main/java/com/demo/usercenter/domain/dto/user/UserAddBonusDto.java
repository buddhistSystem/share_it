package com.demo.usercenter.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAddBonusDto {

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 积分
     */
    private Integer bonus;
}

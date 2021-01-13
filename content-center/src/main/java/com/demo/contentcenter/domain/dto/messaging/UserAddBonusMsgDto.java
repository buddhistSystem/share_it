package com.demo.contentcenter.domain.dto.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户增加积分MQ消息实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAddBonusMsgDto {

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 需要增加得积分
     */
    private Integer bonus;

}

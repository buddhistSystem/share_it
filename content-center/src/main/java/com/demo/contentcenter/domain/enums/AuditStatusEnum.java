package com.demo.contentcenter.domain.enums;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 分享 审核状态
 */
@Getter
@NoArgsConstructor
public enum AuditStatusEnum {


    /**
     * 待审核
     */
    NOT_YET,
    /**
     * 通过
     */
    PASS,
    /**
     * 不通过
     */
    REJECT

}

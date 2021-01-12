package com.demo.contentcenter.domain.dto.content;

import com.demo.contentcenter.domain.enums.AuditStatusEnum;
import lombok.Data;

/**
 * 分享审核DTO
 */
@Data
public class ShareAuditDto {

    /**
     * 审核状态
     */
    private AuditStatusEnum auditStatusEnum;

    /**
     * 原因
     */
    private String reason;
}

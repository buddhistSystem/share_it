package com.demo.contentcenter.controller.content;

import com.demo.contentcenter.auth.CheckRole;
import com.demo.contentcenter.domain.dto.content.ShareAuditDto;
import com.demo.contentcenter.domain.entity.Share;
import com.demo.contentcenter.service.share.ShareService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/admin/shares/")
public class ShareAdminController {

    @Resource
    private ShareService shareService;

    @PutMapping("audit/{id}")
    @CheckRole("admin")
    public Share auditById(@PathVariable Integer id, @RequestBody ShareAuditDto shareAuditDto){
        // TODO 认证，授权
        return this.shareService.auditById(id,shareAuditDto);
    }
}

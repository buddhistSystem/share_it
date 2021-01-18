package com.demo.contentcenter.controller.content;

import com.demo.contentcenter.auth.CheckLogin;
import com.demo.contentcenter.domain.dto.content.ShareDto;
import com.demo.contentcenter.service.share.ShareService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/shares")
public class ShareController {

    @Resource
    private ShareService shareService;

    @RequestMapping("/{id}")
    @CheckLogin
    public ShareDto findById(@PathVariable Integer id) {
        return shareService.feignFindById(id);
    }

}

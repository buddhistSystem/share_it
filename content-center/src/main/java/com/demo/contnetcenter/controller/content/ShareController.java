package com.demo.contnetcenter.controller.content;

import com.demo.contnetcenter.domain.dto.content.ShareDto;
import com.demo.contnetcenter.service.share.ShareService;
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
    public ShareDto findById(@PathVariable Integer id){
        return shareService.findById(id);
    }

}

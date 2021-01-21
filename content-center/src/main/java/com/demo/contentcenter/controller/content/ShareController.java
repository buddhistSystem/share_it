package com.demo.contentcenter.controller.content;

import com.demo.contentcenter.auth.CheckLogin;
import com.demo.contentcenter.domain.dto.content.ShareDto;
import com.demo.contentcenter.domain.entity.Share;
import com.demo.contentcenter.service.share.ShareService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/shares")
public class ShareController {

    @Resource
    private ShareService shareService;

    @Resource
    private HttpServletRequest httpServletRequest;

    @RequestMapping("/{id}")
    @CheckLogin
    public ShareDto findById(@PathVariable Integer id) {
        return shareService.feignFindById(id);
    }

    @RequestMapping("/q")
    @CheckLogin
    public PageInfo<Share> q(@RequestParam(required = false) String title,
                             @RequestParam(required = false, defaultValue = "1") Integer pageNo,
                             @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        //注意控制pageSize，不能过大
        if (pageSize > 100) {
            pageSize = 100;
        }
        return shareService.q(title, pageNo, pageSize);
    }

    @PutMapping("/exchange/{id}")
    @CheckLogin
    public Share exchange(@PathVariable Integer id) {
        return shareService.exchangeById(id, httpServletRequest);
    }

}

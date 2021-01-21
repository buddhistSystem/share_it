package com.demo.contentcenter.controller.content;

import com.demo.contentcenter.auth.CheckLogin;
import com.demo.contentcenter.domain.dto.content.ShareDto;
import com.demo.contentcenter.domain.entity.Share;
import com.demo.contentcenter.service.share.ShareService;
import com.demo.contentcenter.util.JwtOperator;
import com.github.pagehelper.PageInfo;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
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

    @Resource
    private JwtOperator jwtOperator;

    @RequestMapping("/{id}")
    @CheckLogin
    public ShareDto findById(@PathVariable Integer id) {
        return shareService.feignFindById(id);
    }

    @RequestMapping("/q")
    public PageInfo<Share> q(@RequestParam(required = false) String title,
                             @RequestParam(required = false, defaultValue = "1") Integer pageNo,
                             @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                             @RequestHeader(value = "X-Token", required = false) String token) {
        //注意控制pageSize，不能过大
        if (pageSize > 100) {
            pageSize = 100;
        }
        Integer userId = null;
        if (StringUtils.isNotBlank(token)) {
            Claims claims = jwtOperator.getClaimsFromToken(token);
            userId = (Integer) claims.get("id");
        }
        return shareService.q(title, pageNo, pageSize, userId);
    }

    @GetMapping("/exchange/{id}")
    @CheckLogin
    public Share exchange(@PathVariable Integer id) {
        return shareService.exchangeById(id, httpServletRequest);
    }

}

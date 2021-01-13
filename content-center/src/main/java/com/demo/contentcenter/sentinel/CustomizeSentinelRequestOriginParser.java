package com.demo.contentcenter.sentinel;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * sentinel 区分来源
 * <p>
 * 流控规则，授权规则种可以指定来源
 */
//@Component
public class CustomizeSentinelRequestOriginParser implements RequestOriginParser {

    @Override
    public String parseOrigin(HttpServletRequest request) {
        // 从参数中获取origin,不推荐该种方式，可以将origin写入httpHeader中
        String origin = request.getParameter("origin");
        if (StringUtils.isBlank(origin)) {
            throw new IllegalArgumentException("origin 来源必须被指定！");
        }
        return origin;
    }
}

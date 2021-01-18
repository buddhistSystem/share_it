package com.demo.contentcenter.feignclient.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * feign 请求传递token
 */
public class TokenRelayRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        //1.获取token
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String token = request.getHeader("X-Token");

        //2.传递token
        if (StringUtils.isNotBlank(token)) {
            template.header("X-Token", token);
        }

    }
}

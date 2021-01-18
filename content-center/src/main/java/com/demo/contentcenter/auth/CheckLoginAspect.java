package com.demo.contentcenter.auth;

import com.demo.contentcenter.exception.SecurityException;
import com.demo.contentcenter.util.JwtOperator;
import io.jsonwebtoken.Claims;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class CheckLoginAspect {

    @Resource
    private JwtOperator jwtOperator;

    @Around("@annotation(com.demo.contentcenter.auth.CheckLogin)")
    public Object checkLogin(ProceedingJoinPoint point) {
        try {
            // 从header中获取token
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = servletRequestAttributes.getRequest();

            // 校验token是否合法
            String token = request.getHeader("X-Token");
            Boolean result = jwtOperator.validateToken(token);
            if (!result) {
                throw new SecurityException("token 不合法");
            }

            // 将用户信息设置到request
            Claims claims = jwtOperator.getClaimsFromToken(token);
            request.setAttribute("id", claims.get("id"));
            request.setAttribute("role", claims.get("role"));
            request.setAttribute("wxNickName", claims.get("wxNickName"));
            return point.proceed();
        } catch (Throwable throwable) {
            throw new SecurityException("token 不合法");
        }
    }

}

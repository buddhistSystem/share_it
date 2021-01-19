package com.demo.usercenter.auth;

import com.demo.usercenter.exception.SecurityException;
import com.demo.usercenter.util.JwtOperator;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Aspect
@Component
public class AuthAspect {

    @Resource
    private JwtOperator jwtOperator;

    /**
     * 检查登录切面
     *
     * @param point
     * @return
     */
    @Around("@annotation(com.demo.usercenter.auth.CheckLogin)")
    public Object checkLogin(ProceedingJoinPoint point) throws Throwable {
        // 从header中获取token
        HttpServletRequest request = this.getHttpServletRequest();
        // 校验token是否合法
        this.validToken(request);
        return point.proceed();
    }

    /**
     * 检查权限切面
     *
     * @param point
     * @return
     */
    @Around("@annotation(com.demo.usercenter.auth.CheckRole)")
    public Object checkRole(ProceedingJoinPoint point) throws Throwable {
        try {
            HttpServletRequest request = this.getHttpServletRequest();
            this.validToken(request);
            String role = (String) request.getAttribute("role");

            //获取添加@CheckRole注解方法上的value
            MethodSignature signature = ((MethodSignature) point.getSignature());
            Method method = signature.getMethod();
            CheckRole annotation = method.getAnnotation(CheckRole.class);
            String value = annotation.value();

            //对比权限
            if (!StringUtils.equals(role, value)) {
                throw new SecurityException("无权访问");
            }
        } catch (Throwable throwable) {
            throw new SecurityException("无权访问");
        }
        return point.proceed();
    }

    private String validToken(HttpServletRequest request) {
        try {
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
            return token;
        } catch (SecurityException e) {
            throw new SecurityException("token 不合法");
        }
    }


    private HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = servletRequestAttributes.getRequest();
        return request;
    }

}

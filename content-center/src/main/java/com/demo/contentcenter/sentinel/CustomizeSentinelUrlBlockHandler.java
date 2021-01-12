package com.demo.contentcenter.sentinel;


import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * sentinel对于降级和限流都返回相同的异常信息
 * <p>
 * 该类用于自定义返回限流和降级的异常信息
 * <p>
 * 旧版本的UrlBlockHandler被BlockExceptionHandler取代
 * <p>
 * RT 平均响应时间    是指1s内进入5个请求，平均响应时间（秒级） 超过阈值RT（毫秒级）
 * 那么在接下来的时间窗口之内对该方法的调用进入降级
 */
@Component
public class CustomizeSentinelUrlBlockHandler implements BlockExceptionHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException ex) throws Exception {
        String msg = null;
        if (ex instanceof FlowException) {
            msg = "限流了";
        } else if (ex instanceof DegradeException) {
            msg = "降级了";
        } else if (ex instanceof ParamFlowException) {
            msg = "热点参数限流";
        } else if (ex instanceof SystemBlockException) {
            msg = "系统规则（负载/...不满足要求）";
        } else if (ex instanceof AuthorityException) {
            msg = "授权规则不通过";
        }
        // http状态码
        response.setStatus(500);
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Type", "application/json;charset=utf-8");
        response.setContentType("application/json;charset=utf-8");
        // spring mvc自带的json操作工具，叫jackson
        new ObjectMapper()
                .writeValue(
                        response.getWriter(), msg
                );
    }
}

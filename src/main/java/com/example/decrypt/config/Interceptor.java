package com.example.decrypt.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author HeYiyu
 * @date 2023/2/11
 */
@Slf4j
@Component
public class Interceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.debug("收到请求：{} {}", request.getMethod(), CollUtil.isEmpty(request.getParameterMap()) ? request.getRequestURI() : request.getRequestURI() + "，请求参数：" + JSONUtil.toJsonStr(request.getParameterMap()));
        return true;
    }
}

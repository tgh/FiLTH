package com.filth.interceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.filth.util.ModelAndViewUtil;

/**
 * This interceptor intercepts responses from the servlet dispatcher prior to
 * view rendering in order to add objects to the model map that most
 * (if not all) pages need.
 */
@Component
public class ModelAndViewInterceptor extends HandlerInterceptorAdapter {

    @Resource
    private ModelAndViewUtil _modelAndViewUtil;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) {
        if (modelAndView != null) {
            _modelAndViewUtil.addGlobalModelObjects(modelAndView, request);
        }
    }
}

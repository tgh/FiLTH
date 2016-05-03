package com.filth.interceptor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.filth.annotation.SkipInterceptor;

/**
 * This abstract interceptor should be extended by any interceptor that
 * may be skipped by the @SkipInterceptor annotation.
 */
public abstract class SkippableInterceptor extends HandlerInterceptorAdapter {
    
    @Override
    public final boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {
        if (interceptorApplies(handler)) {
            preHandleIfApplies(request,response, handler);
        } else {
            preHandleIfNotApplies(request, response, handler);
        }
        return super.preHandle(request, response, handler); //true
    }

    @Override
    public final void postHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler, ModelAndView modelAndView) throws Exception {
        if (interceptorApplies(handler)) {
            postHandleIfApplies(request, response, handler, modelAndView);
        } else {
            postHandleIfNotApplies(request, response, handler, modelAndView);
        }
    }

    @Override
    public final void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) throws Exception {
        if (interceptorApplies(handler)) {
            afterCompletionIfApplies(request, response, handler, ex);
        } else {
            afterCompletionIfNotApplies(request, response, handler, ex);
        }
    }
    
    /**
     * @param handler {@link HandlerMethod} object
     * @return True if the controller method has not declared to skip the current interceptor,
     * false otherwise.
     */
    protected boolean interceptorApplies(Object handler) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        
        //get the controller method-level SkipInterceptor annotation
        SkipInterceptor methodSkipAnnotation = handlerMethod.getMethodAnnotation(SkipInterceptor.class);
        
        //get the class-level (Controller) SkipInterceptor annotation
        SkipInterceptor controllerSkipAnnotation = handlerMethod.getBeanType().getAnnotation(SkipInterceptor.class);
        
        for (Class<?> klass : getInterceptorsTobeSkipped(methodSkipAnnotation, controllerSkipAnnotation)) {
            //is the current interceptor's class one of the interceptor classes to be skipped? If so, return false.
            if (getClass().equals(klass)) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Overrides of this method will be called after the handler is chosen, but before it is invoked,
     * if this interceptor <b>does</b> apply to the current request (e.g. not skipped by {@link SkipInterceptor}).
     * @param request the current HTTP request (for reading headers, and the like)
     * @param response the current HTTP response (for setting headers, or conceivably writing output)
     * @param handler the HandlerMethod object that is about to handle this request.
     */
    protected void preHandleIfApplies(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {
    }

    /**
     * Overrides of this method will be called after the handler is chosen, but before it is invoked,
     * if this interceptor <b>does not</b> apply to the current request (e.g. skipped by {@link SkipInterceptor}).
     * @param request the current HTTP request (for reading headers, and the like)
     * @param response the current HTTP response (for setting headers, or conceivably writing output)
     * @param handler the HandlerMethod object that is about to handle this request.
     */
    protected void preHandleIfNotApplies(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {
    }

    /**
     * Overrides of this method will be called after the handler returns, but before the view is rendered,
     * if this interceptor <b>does</b> apply to the current request (e.g. not skipped by {@link SkipInterceptor}).
     * @param request the current HTTP request (for reading headers, and the like)
     * @param response the current HTTP response (for setting headers, or conceivably writing output)
     * @param handler the HandlerMethod object that is about to handle this request.
     * @param modelAndView the modelAndView returned by the handler
     */
    protected void postHandleIfApplies(HttpServletRequest request, HttpServletResponse response,
            Object handler, ModelAndView modelAndView) throws Exception {
    }
    /**
     * Overrides of this method will be called after the handler returns, but before the view is rendered,
     * if this interceptor <b>does not</b> apply to the current request (e.g. skipped by {@link SkipInterceptor}).
     * @param request the current HTTP request (for reading headers, and the like)
     * @param response the current HTTP response (for setting headers, or conceivably writing output)
     * @param handler the HandlerMethod object that is about to handle this request.
     * @param modelAndView the modelAndView returned by the handler
     */
    protected void postHandleIfNotApplies(HttpServletRequest request, HttpServletResponse response,
            Object handler, ModelAndView modelAndView) throws Exception {
    }

    /**
     * Overrides of this method will be called after the request is completed if this interceptor
     * <b>does</b> apply to the current request (e.g. not skipped by {@link SkipInterceptor}).
     * @param request the current HTTP request (for reading headers, and the like)
     * @param response the current HTTP response (for setting headers, or conceivably writing output)
     * @param handler the HandlerMethod object that is about to handle this request.
     * @param ex exception thrown on handler execution, if any
     */

    protected void afterCompletionIfApplies(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) throws Exception {
    }

    /**
     * Overrides of this method will be called after the request is completed if this interceptor
     * <b>does not</b> apply to the current request (e.g. skipped by {@link SkipInterceptor}).
     * @param request the current HTTP request (for reading headers, and the like)
     * @param response the current HTTP response (for setting headers, or conceivably writing output)
     * @param handler the HandlerMethod object that is about to handle this request.
     * @param ex exception thrown on handler execution, if any
     */
    protected void afterCompletionIfNotApplies(HttpServletRequest request,
            HttpServletResponse response, Object handler, Exception ex) throws Exception{
    }

    private Set<Class<?>> getInterceptorsTobeSkipped(SkipInterceptor... annotations) {
        Set<Class<?>> interceptorsToSkip = new HashSet<>();
        
        for (SkipInterceptor annotation : annotations) {
            if(annotation != null) {
                interceptorsToSkip.addAll(Arrays.asList(annotation.value()));
            }
        }
        
        return interceptorsToSkip;
    }
}

package com.filth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.filth.interceptor.SkippableInterceptor;

/**
 * This annotation, when placed on a controller class/method, indicates that the
 * {@link SkippableInterceptor}s specified should not fire when the controllers'
 * handler method(s) run.
 * 
 * For example, any Controller methods handling ajax calls should skip the
 * BackgroundImageInterceptor.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface SkipInterceptor {
    
    public Class<? extends SkippableInterceptor>[] value();
    
}
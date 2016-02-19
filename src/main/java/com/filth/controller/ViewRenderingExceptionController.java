package com.filth.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.filth.resolver.ExceptionResolver;

/**
 * This is a small controller whose only job is to display an error
 * page for exceptions that occur during template rendering. Spring
 * doesn't handle these using an ExceptionResolver, and since it
 * renders templates straight to the response's OutputWriter instead
 * of to a buffer and then sending the buffer, there's no way to
 * intercept the error and display a different page instead. So
 * instead we let it bubble an exception up to the servlet container,
 * and use an error mapping in web.xml to send the user here, where
 * we'll just render the same general error template that we'd render
 * otherwise.
 */
@Controller
public class ViewRenderingExceptionController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ViewRenderingExceptionController.class);
    
    private static final String URL = "/error/viewRenderingError";
    @Value("${general.error.template:error/general_error}")
    private String _template;
    
    @Resource
    private ExceptionResolver _exceptionResolver;
        
    @RequestMapping(value = URL)
    public ModelAndView displayError(HttpServletRequest request) {
        Throwable exception = (Throwable) request.getAttribute("javax.servlet.error.exception");
        
        LOGGER.error("Exception during view rendering", exception);
        
        ModelMap mm = _exceptionResolver.getModelWithException(exception);
        ModelAndView modelAndView = new ModelAndView(_template, mm);
        
        return modelAndView;
    }
}

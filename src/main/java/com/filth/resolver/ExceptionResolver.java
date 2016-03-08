package com.filth.resolver;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import com.filth.interceptor.BackgroundImageInterceptor;
import com.filth.util.ModelAndViewUtil;

/**
 * Handles uncaught exceptions thrown during app execution.
 */
public class ExceptionResolver extends SimpleMappingExceptionResolver {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionResolver.class);
    
    /** Model keys */
    public static final class ModelKeys {
        public static final String EXCEPTION = "exception";
        public static final String MESSAGE = "message";
        public static final String STACK_TRACE = "stacktrace";
    }

    private Map<String, Integer> _exceptionStatusCodeMappings;

    @Resource
    private ModelAndViewUtil _modelAndViewUtil;

    @Resource
    public void setExceptionStatusCodeMappings(Properties exceptionStatusCodeMappings) {
        _exceptionStatusCodeMappings = new HashMap<String, Integer>();
        for (Enumeration<?> names = exceptionStatusCodeMappings.propertyNames(); names.hasMoreElements();) {
            String exception = (String) names.nextElement();
            Integer statusCode = Integer.parseInt(exceptionStatusCodeMappings.getProperty(exception));
            _exceptionStatusCodeMappings.put(exception, statusCode);
        }
    }
    
    /**
     * Handler Method for all exceptions in the WebApp.
     * 
     * @param request
     *            HttpServletRequest from the user
     * @param response
     *            HttpServletResponse to be returned to the user
     * @param handler
     *            Handler that threw the Exception.
     * @param ex
     *            Exception thrown in WebApp.
     * 
     * @return ModelAndView to display to the user, or null if the exception was not resolved.
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) {
        LOGGER.error("Exception while processing request ", ex);

        ModelAndView modelView = super.doResolveException(request, response, handler, ex);
        
        if (modelView != null) {
            modelView.addAllObjects(getModelWithException(ex));
            
            //add default background image (TODO: could designate an image exclusively for errors)
            _modelAndViewUtil.addBackgroundImageData(modelView.getModelMap(),
                    BackgroundImageInterceptor.BG_IMAGES_PATH,
                    BackgroundImageInterceptor.DEFAULT_BG_IMAGE,
                    BackgroundImageInterceptor.DEFAULT_BG_IMAGE_MOVIE_TITLE,
                    BackgroundImageInterceptor.DEFAULT_BG_IMAGE_MOVIE_YEAR);
    
            int statusCode;
            String exceptionName = ex.getClass().getCanonicalName();
            
            if (_exceptionStatusCodeMappings.containsKey(exceptionName)) {
                statusCode = _exceptionStatusCodeMappings.get(exceptionName);
            } else {
                statusCode = _exceptionStatusCodeMappings.get("java.lang.Exception");
            }
            
            applyStatusCodeIfPossible(request, response, statusCode);
        }
        return modelView;
    }

    /**
     * Returns ModelMap with "message" and "stackTrace" from exception.
     */
    public ModelMap getModelWithException(Throwable exception) {
        ModelMap modelMap = new ModelMap();
        
        //NOTE: should probably not display stacktraces to users on production.
        //You can add a private boolean field, set it through *-servlet.xml
        //(or wherever the bean for this class is defined), and check that
        //field here.
        if (exception != null) {
            StringWriter stringWriter = new StringWriter();
            exception.printStackTrace(new PrintWriter(stringWriter));
            modelMap.addAttribute(ModelKeys.STACK_TRACE, stringWriter.toString());
            modelMap.addAttribute(ModelKeys.MESSAGE, exception.getMessage());
            modelMap.addAttribute(ModelKeys.EXCEPTION, exception);
        }

        return modelMap;
    }
}

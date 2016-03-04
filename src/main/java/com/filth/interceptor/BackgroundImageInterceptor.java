package com.filth.interceptor;

import java.io.File;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.filth.util.ModelAndViewUtil;

/**
 * Interceptor to randomly choose a background image for the page.
 */
@Component
public class BackgroundImageInterceptor extends HandlerInterceptorAdapter {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BackgroundImageInterceptor.class);
    
    private static final String DEFAULT_BACKGROUND_IMAGE = "tol1.jpg";
    
    @Resource
    private ModelAndViewUtil _modelAndViewUtil;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) {
        if (modelAndView != null) {
            String bgImageFilename = DEFAULT_BACKGROUND_IMAGE;
            
            try {
                File backgroundsFolder = new File(request.getSession().getServletContext().getRealPath("/images/backgrounds"));
                File[] backgrounds = backgroundsFolder.listFiles();
                int numBackgrounds = backgrounds.length;

                Random random = new Random();
                int n = random.nextInt(numBackgrounds);
                bgImageFilename = backgrounds[n].getName();
            } catch (Exception e) {
                LOGGER.error("Error during background image selection--"
                             + "falling back to the default: " + DEFAULT_BACKGROUND_IMAGE, e);
            }
            
            _modelAndViewUtil.addBackgroundImageFile(modelAndView.getModelMap(), bgImageFilename);
        }
    }
}

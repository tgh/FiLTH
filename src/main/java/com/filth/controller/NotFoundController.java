package com.filth.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for HTTP requests returning 404
 */
@Controller
public class NotFoundController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(NotFoundController.class);

    private static final String URL = "/error/notFound";
    
    @RequestMapping(value=URL, method=RequestMethod.GET)
    public ModelAndView handle404(HttpServletRequest request) {
        String servletURI = (String) request.getAttribute("javax.servlet.error.message");
        LOGGER.error("404");
        ModelMap mm = new ModelMap();
        mm.put("servletURI", servletURI);
        return new ModelAndView("error/not_found", mm);
    }
}

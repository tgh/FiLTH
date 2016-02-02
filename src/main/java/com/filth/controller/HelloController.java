package com.filth.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for the "Hello" page
 */
@Controller
public class HelloController {
    
    private static final Log LOGGER = LogFactory.getLog(HelloController.class);

    @RequestMapping(value="/hello.html", method = RequestMethod.GET)
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        LOGGER.info("Returning hello view");
        
        ModelMap mm = new ModelMap();
        mm.put("message", "HELLO WORLD©");  //throw in a special character to verify UTF-8 settings

        return new ModelAndView("hello", mm);
    }

}

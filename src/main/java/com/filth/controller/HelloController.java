package com.filth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HelloController.class);

    @RequestMapping(value="/hello.html", method = RequestMethod.GET)
    public ModelAndView handleRequest() {

        LOGGER.info("Information!");
        LOGGER.debug("Debug message!");
        LOGGER.error("***ERROR***");
        
        ModelMap mm = new ModelMap();
        mm.put("message", "HELLO WORLD©");  //throw in a special character to verify UTF-8 settings

        return new ModelAndView("hello", mm);
    }

}

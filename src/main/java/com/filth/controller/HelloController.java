package com.filth.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for the "Hello" page
 */
public class HelloController {
    
    private static final Log LOGGER = LogFactory.getLog(HelloController.class);

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        LOGGER.info("Returning hello view");

        return new ModelAndView("view/hello.jsp");
    }

}

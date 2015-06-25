package com.filth.controller;

import junit.framework.TestCase;

import org.springframework.web.servlet.ModelAndView;

/**
 * Test coverage for {@link HelloController}.
 */
public class HelloControllerTest extends TestCase {

    public void testHandleRequestView() throws Exception {       
        HelloController controller = new HelloController();
        ModelAndView modelAndView = controller.handleRequest(null, null);       
        assertEquals("view/hello.jsp", modelAndView.getViewName());
    }
}

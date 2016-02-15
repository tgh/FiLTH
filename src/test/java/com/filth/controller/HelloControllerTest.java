package com.filth.controller;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

/**
 * Test coverage for {@link HelloController}.
 */
public class HelloControllerTest {

    @Test
    public void testHandleRequestView() throws Exception {       
        HelloController controller = new HelloController();
        ModelAndView modelAndView = controller.handleRequest();       
        assertEquals("hello", modelAndView.getViewName());
    }
}

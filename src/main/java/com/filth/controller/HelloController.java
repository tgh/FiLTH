package com.filth.controller;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.filth.model.Oscar;
import com.filth.service.OscarService;

/**
 * Hello World Controller
 */
@Controller
public class HelloController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HelloController.class);
    
    @Resource
    private OscarService _oscarService;

    @RequestMapping(value="/hello.html", method = RequestMethod.GET)
    public ModelAndView handleRequest() {

        List<Oscar> oscars = _oscarService.getAllOscars();
        
        ModelMap mm = new ModelMap();
        mm.put("message", oscars.get(0).toString());  //throw in a special character to verify UTF-8 settings

        return new ModelAndView("hello", mm);
    }

}

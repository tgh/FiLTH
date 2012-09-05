package net.tylerhayes.filth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {

  public static final class URLPrefix {
    public static final String HOME = "/";
    public static final String HOME_URL = "/home";
  }
  
  public static final class ModelKeys {
    public static final String HELLO = "hello";
  }
  
  @RequestMapping(value = {URLPrefix.HOME, URLPrefix.HOME_URL}, method = RequestMethod.GET)
  public ModelMap home() {
    ModelMap mm = new ModelMap();
    mm.addAttribute(ModelKeys.HELLO, "Hello");
    
    return mm;
  }
}

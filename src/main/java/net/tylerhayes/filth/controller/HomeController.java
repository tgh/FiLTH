package net.tylerhayes.filth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {

  public static final class URLPrefix {
    public static final String HOME = "/";
    public static final String HOME_URL = "/home";
  }
  
  @RequestMapping(value = {URLPrefix.HOME, URLPrefix.HOME_URL}, method = RequestMethod.GET)
  public String home() {
    return "Home";
  }
}

package com.filth.controller.admin;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.filth.model.Oscar;
import com.filth.service.OscarService;
import com.filth.util.ModelAndViewUtil;

@Controller
public class ManageOscarsController extends AdminController {
    
    private static final String OSCARS_VIEW_PREFIX = ADMIN_VIEW_PREFIX + "/oscars";
    
    @Resource
    private OscarService _oscarService;
    @Resource
    private ModelAndViewUtil _modelAndViewUtil;
    
    private static final class URL {
        public static final String OSCARS = ADMIN_URL_PREFIX + "/oscars";
        public static final String DELETE = OSCARS + "/delete";
        public static final String EDIT = OSCARS + "/edit";
        public static final String CREATE = OSCARS + "/create";
        public static final String SAVE = OSCARS + "/save";
    }
    
    private static final class URLParam {
        public static final String ID = "id";
        public static final String CATEGORY = "category";
    }
    
    private static final class ModelKey {
        public static final String OSCARS = "oscars";
        public static final String OSCAR = "oscar";
        public static final String CATEGORY = "category";
    }

    @RequestMapping(value=URL.OSCARS, method=RequestMethod.GET)
    public ModelAndView manageOscars() {
        List<Oscar> oscars = _oscarService.getAllOscars();
        
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.OSCARS, oscars);

        return new ModelAndView(OSCARS_VIEW_PREFIX + "/manage_oscars", mm);
    }
    
    @RequestMapping(value=URL.EDIT, method=RequestMethod.GET)
    public ModelAndView editOscar(
            @RequestParam(value=URLParam.ID) Integer id) {
        Oscar oscar = _oscarService.getOscarById(id);
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.OSCAR, oscar);
        
        return new ModelAndView(OSCARS_VIEW_PREFIX + "/edit_oscar", mm);
    }
    
    @RequestMapping(value=URL.CREATE, method=RequestMethod.GET)
    public ModelAndView createOscar() {
        return new ModelAndView(OSCARS_VIEW_PREFIX + "/create_oscar");
    }
    
    @RequestMapping(value=URL.SAVE, method=RequestMethod.POST)
    public ModelAndView saveOscar(
            @RequestParam(value=URLParam.CATEGORY) String category,
            @RequestParam(value=URLParam.ID, required=false) Integer id) throws Exception {
        Oscar oscar = null;
        if (null != id) {
            oscar = _oscarService.getOscarById(id);
        } else {
            oscar = new Oscar();
        }
        
        oscar.setCategory(category);
        _oscarService.saveOscar(oscar);
        
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.OSCAR, oscar);
        
        return _modelAndViewUtil.createSuccessJsonModelAndViewWithHtml(
                OSCARS_VIEW_PREFIX + "/save_oscar_success", mm);
    }
    
    @RequestMapping(value=URL.DELETE, method=RequestMethod.POST)
    public ModelAndView deleteOscar(
            @RequestParam(value=URLParam.ID) Integer id) throws Exception {
        Oscar oscar = _oscarService.getOscarById(id);
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.CATEGORY, oscar.getCategory());
        
        _oscarService.deleteOscarById(id);
        
        return _modelAndViewUtil.createSuccessJsonModelAndViewWithHtml(
                OSCARS_VIEW_PREFIX + "/delete_oscar_success", mm);
    }
        
}

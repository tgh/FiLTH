package com.filth.controller.admin;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.filth.link.Link;
import com.filth.link.ManageOscarsLinkGenerator;
import com.filth.model.Oscar;
import com.filth.service.OscarService;
import com.filth.util.ModelAndViewUtil;

@Controller
public class ManageOscarsController extends ManageEntityController implements ManageOscarsLinkGenerator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ManageOscarsController.class);
    
    private static final String ENTITY_NAME = "Oscar";
    
    @Resource
    private OscarService _oscarService;
    @Resource
    private ModelAndViewUtil _modelAndViewUtil;
    
    private static final class URL {
        public static final String OSCARS = ADMIN_URL_PREFIX + "/oscars";
        public static final String DELETE = OSCARS + "/delete";
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

    @Override
    public Link getLinkToManageOscars() {
        return new Link(URL.OSCARS);
    }

    @Override
    public Link getLinkToDeleteOscar() {
        return new Link(URL.DELETE);
    }

    @Override
    public Link getLinkToDeleteOscar(int id) {
        return new Link(URL.DELETE).setParam(URLParam.ID, String.valueOf(id));
    }
    
    @Override
    public Link getLinkToSaveOscar() {
        return new Link(URL.SAVE);
    }

    @RequestMapping(value=URL.OSCARS, method=RequestMethod.GET)
    public ModelAndView manageOscars() {
        List<Oscar> oscars = _oscarService.getAllOscars();
        
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.OSCARS, oscars);

        return new ModelAndView(ADMIN_VIEW_PREFIX + "/manage_oscars", mm);
    }
    
    @RequestMapping(value=URL.SAVE, method=RequestMethod.POST)
    public ModelAndView saveOscar(
            @RequestParam(value=URLParam.CATEGORY) String category,
            @RequestParam(value=URLParam.ID, required=false) Integer id) throws Exception {
        Oscar oscar = null;
        
        try {
            if (null != id) {
                oscar = _oscarService.getOscarById(id);
            } else {
                oscar = new Oscar();
            }
            
            oscar.setCategory(category);
            _oscarService.saveOscar(oscar);
        } catch (Exception e) {
            LOGGER.error("An error occurred attempting to save oscar '" + category + "'", e);
            return _modelAndViewUtil.createErrorJsonModelAndView(
                    String.format(SAVE_ERROR_MESSAGE_FORMAT, ENTITY_NAME, category), new ModelMap());
        }
        
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.OSCAR, oscar);
        
        return _modelAndViewUtil.createSuccessJsonModelAndView(
                String.format(SAVE_SUCCESS_MESSAGE_FORMAT, ENTITY_NAME, category), mm);
    }
    
    @RequestMapping(value=URL.DELETE, method=RequestMethod.POST)
    public ModelAndView deleteOscar(
            @RequestParam(value=URLParam.ID) Integer id) throws Exception {
        ModelMap mm = new ModelMap();
        Oscar oscar = _oscarService.getOscarById(id);
        
        if (null == oscar) {
            LOGGER.error("Did not find oscar with id " + id);
            return _modelAndViewUtil.createErrorJsonModelAndView(
                    String.format(DELETE_ERROR_MESSAGE_FORMAT, ENTITY_NAME, id), mm);
        }
        
        try {
            _oscarService.deleteOscarById(id);
        } catch (Exception e) {
            LOGGER.error("An error occurred attempting to delete oscar '" + oscar.getCategory() + "'", e);
            return _modelAndViewUtil.createErrorJsonModelAndView(
                    String.format(DELETE_ERROR_MESSAGE_FORMAT, ENTITY_NAME, oscar.getCategory()), mm);
        }
        
        return _modelAndViewUtil.createSuccessJsonModelAndView(
                String.format(DELETE_SUCCESS_MESSAGE_FORMAT, ENTITY_NAME, oscar.getCategory()), mm);
    }
        
}

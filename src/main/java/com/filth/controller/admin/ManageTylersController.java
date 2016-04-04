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
import com.filth.link.ManageTylersLinkGenerator;
import com.filth.model.Tyler;
import com.filth.service.TylerService;
import com.filth.util.ModelAndViewUtil;

@Controller
public class ManageTylersController extends ManageEntityController implements ManageTylersLinkGenerator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ManageTylersController.class);
    
    private static final String ENTITY_NAME = "Tyler";
    
    @Resource
    private TylerService _tylerService;
    @Resource
    private ModelAndViewUtil _modelAndViewUtil;
    
    private static final class URL {
        public static final String OSCARS = ADMIN_URL_PREFIX + "/tylers";
        public static final String DELETE = OSCARS + "/delete";
        public static final String SAVE = OSCARS + "/save";
    }
    
    private static final class URLParam {
        public static final String ID = "id";
        public static final String CATEGORY = "category";
    }
    
    private static final class ModelKey {
        public static final String OSCARS = "tylers";
        public static final String OSCAR = "tyler";
    }

    @Override
    public Link getLinkToManageTylers() {
        return new Link(URL.OSCARS);
    }

    @Override
    public Link getLinkToDeleteTyler() {
        return new Link(URL.DELETE);
    }

    @Override
    public Link getLinkToDeleteTyler(int id) {
        return new Link(URL.DELETE).setParam(URLParam.ID, String.valueOf(id));
    }
    
    @Override
    public Link getLinkToSaveTyler() {
        return new Link(URL.SAVE);
    }

    @RequestMapping(value=URL.OSCARS, method=RequestMethod.GET)
    public ModelAndView manageTylers() {
        List<Tyler> tylers = _tylerService.getAllTylers();
        
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.OSCARS, tylers);

        return new ModelAndView(ADMIN_VIEW_PREFIX + "/manage_tylers", mm);
    }
    
    @RequestMapping(value=URL.SAVE, method=RequestMethod.POST)
    public ModelAndView saveTyler(
            @RequestParam(value=URLParam.CATEGORY) String category,
            @RequestParam(value=URLParam.ID, required=false) Integer id) throws Exception {
        Tyler tyler = null;
        
        try {
            if (null != id) {
                tyler = _tylerService.getTylerById(id);
            } else {
                tyler = new Tyler();
            }
            
            tyler.setCategory(category);
            _tylerService.saveTyler(tyler);
        } catch (Exception e) {
            LOGGER.error("An error occurred attempting to save tyler '" + category + "'", e);
            return _modelAndViewUtil.createErrorJsonModelAndView(
                    String.format(SAVE_ERROR_MESSAGE_FORMAT, ENTITY_NAME, category), new ModelMap());
        }
        
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.OSCAR, tyler);
        
        return _modelAndViewUtil.createSuccessJsonModelAndView(
                String.format(SAVE_SUCCESS_MESSAGE_FORMAT, ENTITY_NAME, category), mm);
    }
    
    @RequestMapping(value=URL.DELETE, method=RequestMethod.POST)
    public ModelAndView deleteTyler(
            @RequestParam(value=URLParam.ID) Integer id) throws Exception {
        ModelMap mm = new ModelMap();
        Tyler tyler = _tylerService.getTylerById(id);
        
        if (null == tyler) {
            LOGGER.error("Did not find tyler with id " + id);
            return _modelAndViewUtil.createErrorJsonModelAndView(
                    String.format(DELETE_ERROR_MESSAGE_FORMAT, ENTITY_NAME, id), mm);
        }
        
        try {
            _tylerService.deleteTylerById(id);
        } catch (Exception e) {
            LOGGER.error("An error occurred attempting to delete tyler '" + tyler.getCategory() + "'", e);
            return _modelAndViewUtil.createErrorJsonModelAndView(
                    String.format(DELETE_ERROR_MESSAGE_FORMAT, ENTITY_NAME, tyler.getCategory()), mm);
        }
        
        return _modelAndViewUtil.createSuccessJsonModelAndView(
                String.format(DELETE_SUCCESS_MESSAGE_FORMAT, ENTITY_NAME, tyler.getCategory()), mm);
    }
        
}

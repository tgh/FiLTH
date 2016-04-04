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
import com.filth.link.ManageListsLinkGenerator;
import com.filth.service.ListService;
import com.filth.util.ModelAndViewUtil;

@Controller
public class ManageListsController extends ManageEntityController implements ManageListsLinkGenerator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ManageListsController.class);
    
    private static final String ENTITY_NAME = "List";
    
    @Resource
    private ListService _listService;
    @Resource
    private ModelAndViewUtil _modelAndViewUtil;
    
    private static final class URL {
        public static final String LISTS = ADMIN_URL_PREFIX + "/lists";
        public static final String DELETE = LISTS + "/delete";
        public static final String SAVE = LISTS + "/save";
    }
    
    private static final class URLParam {
        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String AUTHOR = "author";
    }
    
    private static final class ModelKey {
        public static final String LISTS = "lists";
        public static final String LIST = "list";
    }

    @Override
    public Link getLinkToManageLists() {
        return new Link(URL.LISTS);
    }

    @Override
    public Link getLinkToDeleteList() {
        return new Link(URL.DELETE);
    }

    @Override
    public Link getLinkToDeleteList(int id) {
        return new Link(URL.DELETE).setParam(URLParam.ID, String.valueOf(id));
    }

    @Override
    public Link getLinkToSaveList() {
        return new Link(URL.SAVE);
    }
    
    @RequestMapping(value=URL.LISTS, method=RequestMethod.GET)
    public ModelAndView manageLists() {
        List<com.filth.model.List> lists = _listService.getAllLists();
        
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.LISTS, lists);

        return new ModelAndView(ADMIN_VIEW_PREFIX + "/manage_lists", mm);
    }
    
    @RequestMapping(value=URL.SAVE, method=RequestMethod.POST)
    public ModelAndView saveList(
            @RequestParam(value=URLParam.TITLE) String title,
            @RequestParam(value=URLParam.AUTHOR, required=false) String author,
            @RequestParam(value=URLParam.ID, required=false) Integer id) throws Exception {
        com.filth.model.List list = null;
        
        try {
            if (null != id) {
                list = _listService.getListById(id);
            } else {
                list = new com.filth.model.List();
            }
            
            list.setTitle(title);
            
            if (null != author) {
                list.setAuthor(author);
            }
            
            _listService.saveList(list);
        } catch (Exception e) {
            LOGGER.error("An error occurred attempting to save list '" + title + "'", e);
            return _modelAndViewUtil.createErrorJsonModelAndView(
                    String.format(SAVE_ERROR_MESSAGE_FORMAT, ENTITY_NAME, title), new ModelMap());
        }
        
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.LIST, list);
        
        return _modelAndViewUtil.createSuccessJsonModelAndView(
                String.format(SAVE_SUCCESS_MESSAGE_FORMAT, ENTITY_NAME, title), mm);
    }
    
    @RequestMapping(value=URL.DELETE, method=RequestMethod.POST)
    public ModelAndView deleteList(
            @RequestParam(value=URLParam.ID) Integer id) throws Exception {
        ModelMap mm = new ModelMap();
        com.filth.model.List list = _listService.getListById(id);
        
        if (null == list) {
            LOGGER.error("Did not find list with id " + id);
            return _modelAndViewUtil.createErrorJsonModelAndView(
                    String.format(DELETE_ERROR_MESSAGE_FORMAT, ENTITY_NAME, id), mm);
        }
        
        try {
            _listService.deleteListById(id);
        } catch (Exception e) {
            LOGGER.error("An error occurred attempting to delete list '" + list.getTitle() + "'", e);
            return _modelAndViewUtil.createErrorJsonModelAndView(
                    String.format(DELETE_ERROR_MESSAGE_FORMAT, ENTITY_NAME, list.getTitle()), mm);
        }
        
        return _modelAndViewUtil.createSuccessJsonModelAndView(
                String.format(DELETE_SUCCESS_MESSAGE_FORMAT, ENTITY_NAME, list.getTitle()), mm);
    }

}

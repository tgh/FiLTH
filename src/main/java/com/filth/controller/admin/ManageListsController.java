package com.filth.controller.admin;

import java.util.List;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.filth.annotation.SkipInterceptor;
import com.filth.interceptor.BackgroundImageInterceptor;
import com.filth.json.ListJSONTranslator;
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
    @Resource
    private ListJSONTranslator _listJSONTranslator;
    
    private static final class URL {
        public static final String LISTS = ADMIN_URL_PREFIX + "/lists";
        public static final String LIST = ADMIN_URL_PREFIX + "/list";
        public static final String DELETE = LISTS + "/delete";
        public static final String SAVE = LISTS + "/save";
    }
    
    private static final class URLParam {
        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String AUTHOR = "author";
        public static final String LIST_JSON = "listJSON";
    }
    
    private static final class ModelKey {
        public static final String LISTS = "lists";
        public static final String LIST = "list";
        public static final String LIST_JSON = "listJSON";
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
    
    @Override
    public Link getLinkToList(int id) {
        return new Link(URL.LIST).setParam(URLParam.ID, String.valueOf(id));
    }
    
    @RequestMapping(value=URL.LISTS, method=RequestMethod.GET)
    public ModelAndView manageLists() {
        List<com.filth.model.List> lists = _listService.getAllLists();
        
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.LISTS, lists);

        return new ModelAndView(ADMIN_VIEW_PREFIX + "/manage_lists", mm);
    }
    
    @SkipInterceptor({BackgroundImageInterceptor.class})
    @RequestMapping(value=URL.SAVE, method=RequestMethod.POST, params=URLParam.TITLE)
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
            LOGGER.error(String.format(SAVE_ERROR_LOG_MESSAGE_FORMAT, ENTITY_NAME, title), e);
            return _modelAndViewUtil.createErrorJsonModelAndView(
                    String.format(SAVE_ERROR_MESSAGE_FORMAT, ENTITY_NAME, title), new ModelMap());
        }
        
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.LIST, list);
        
        return _modelAndViewUtil.createSuccessJsonModelAndView(
                String.format(SAVE_SUCCESS_MESSAGE_FORMAT, ENTITY_NAME, title), mm);
    }
    
    @SkipInterceptor({BackgroundImageInterceptor.class})
    @RequestMapping(value=URL.SAVE, method=RequestMethod.POST, params=URLParam.LIST_JSON)
    public ModelAndView saveListJSON(
            @RequestParam(value=URLParam.LIST_JSON) String listJSON) throws Exception {
        String title = "[unknown title]";
        com.filth.model.List originalList = null;
        
        try {
            com.filth.model.List newList = _listJSONTranslator.fromJSON(listJSON);
            title = newList.getTitle();
            originalList = _listService.getListById(newList.getId());
            originalList.copyContent(newList);
            _listService.saveList(originalList);
        } catch (Exception e) {
            LOGGER.error(String.format(SAVE_ERROR_LOG_MESSAGE_FORMAT, ENTITY_NAME, title), e);
            return _modelAndViewUtil.createErrorJsonModelAndView(
                    String.format(SAVE_ERROR_MESSAGE_FORMAT, ENTITY_NAME, title), new ModelMap());
        }
        
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.LIST, originalList);
        
        return _modelAndViewUtil.createSuccessJsonModelAndView(
                String.format(SAVE_SUCCESS_MESSAGE_FORMAT, ENTITY_NAME, title), mm);
    }
    
    @SkipInterceptor({BackgroundImageInterceptor.class})
    @RequestMapping(value=URL.DELETE, method=RequestMethod.POST)
    public ModelAndView deleteList(
            @RequestParam(value=URLParam.ID) Integer id) throws Exception {
        ModelMap mm = new ModelMap();
        com.filth.model.List list = _listService.getListById(id);
        
        if (null == list) {
            LOGGER.error(String.format(UNKNOWN_ENTITY_LOG_MESSAGE_FORMAT, ENTITY_NAME, id));
            return _modelAndViewUtil.createErrorJsonModelAndView(
                    String.format(DELETE_ERROR_MESSAGE_FORMAT, ENTITY_NAME, id), mm);
        }
        
        try {
            _listService.deleteListById(id);
        } catch (Exception e) {
            LOGGER.error(String.format(DELETE_ERROR_LOG_MESSAGE_FORMAT, ENTITY_NAME, list.getTitle()), e);
            return _modelAndViewUtil.createErrorJsonModelAndView(
                    String.format(DELETE_ERROR_MESSAGE_FORMAT, ENTITY_NAME, list.getTitle()), mm);
        }
        
        return _modelAndViewUtil.createSuccessJsonModelAndView(
                String.format(DELETE_SUCCESS_MESSAGE_FORMAT, ENTITY_NAME, list.getTitle()), mm);
    }
    
    @RequestMapping(value=URL.LIST, method=RequestMethod.GET)
    public ModelAndView viewList(
            @RequestParam(value=URLParam.ID) Integer id) {
        ModelMap mm = new ModelMap();
        com.filth.model.List list = _listService.getListById(id);
        mm.put(ModelKey.LIST, list);
        
        JSONObject jsonObject = _listJSONTranslator.toJSON(list);
        mm.put(ModelKey.LIST_JSON, jsonObject.toString());
        
        return new ModelAndView(ADMIN_VIEW_PREFIX + "/view_list", mm);
    }

}

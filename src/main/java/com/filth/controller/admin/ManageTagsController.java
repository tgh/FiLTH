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
import com.filth.link.ManageTagsLinkGenerator;
import com.filth.model.Tag;
import com.filth.service.TagService;
import com.filth.util.ModelAndViewUtil;

@Controller
public class ManageTagsController extends AdminController implements ManageTagsLinkGenerator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ManageTagsController.class);
    
    private static final String TAGS_VIEW_PREFIX = ADMIN_VIEW_PREFIX + "/tags";
    private static final String DELETE_SUCCESS_MESSAGE_FORMAT = "Tag \"%s\" has been deleted.";
    private static final String DELETE_ERROR_MESSAGE_FORMAT = "An error occurred deleting tag \"%s\".";
    private static final String SAVE_SUCCESS_MESSAGE_FORMAT = "Tag \"%s\" saved.";
    private static final String SAVE_ERROR_MESSAGE_FORMAT = "An error occurred saving tag \"%s\".";
    
    @Resource
    private TagService _tagService;
    @Resource
    private ModelAndViewUtil _modelAndViewUtil;
    
    private static final class URL {
        public static final String TAGS = ADMIN_URL_PREFIX + "/tags";
        public static final String DELETE = TAGS + "/delete";
        public static final String SAVE = TAGS + "/save";
    }
    
    private static final class URLParam {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String PARENT = "parent";
    }
    
    private static final class ModelKey {
        public static final String TAGS = "tags";
        public static final String TAG = "tag";
        public static final String NAME = "name";
    }

    @Override
    public Link getLinkToManageTags() {
        return new Link(URL.TAGS);
    }

    @Override
    public Link getLinkToDeleteTag(int id) {
        return new Link(URL.DELETE).setParam(URLParam.ID, String.valueOf(id));
    }

    @Override
    public Link getLinkToSaveTag() {
        return new Link(URL.SAVE);
    }

    @RequestMapping(value=URL.TAGS, method=RequestMethod.GET)
    public ModelAndView manageTags() {
        List<Tag> tags = _tagService.getAllTags();
        
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.TAGS, tags);

        return new ModelAndView(TAGS_VIEW_PREFIX + "/manage_tags", mm);
    }
    
    @RequestMapping(value=URL.SAVE, method=RequestMethod.POST)
    public ModelAndView saveTag(
            @RequestParam(value=URLParam.NAME) String name,
            @RequestParam(value=URLParam.PARENT, required=false) Integer parentId, 
            @RequestParam(value=URLParam.ID, required=false) Integer id) throws Exception {
        Tag tag = null;
        
        try {
            if (null != id) {
                tag = _tagService.getTagById(id);
            } else {
                tag = new Tag();
            }
            
            if (null != parentId) {
                Tag parent = _tagService.getTagById(parentId.intValue());   //FIXME: intValue() shouldn't be necessary
                tag.setParent(parent);
            }
            
            tag.setName(name);
            _tagService.saveTag(tag);
        } catch (Exception e) {
            LOGGER.error("An error occurred attempting to save tag '" + tag.getName() + "'", e);
            return _modelAndViewUtil.createErrorJsonModelAndView(
                    String.format(SAVE_ERROR_MESSAGE_FORMAT, tag.getName()), new ModelMap());
        }
        
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.TAG, tag);
        
        return _modelAndViewUtil.createSuccessJsonModelAndView(
                String.format(SAVE_SUCCESS_MESSAGE_FORMAT, name), mm);
    }
    
    @RequestMapping(value=URL.DELETE, method=RequestMethod.POST)
    public ModelAndView deleteTag(
            @RequestParam(value=URLParam.ID) Integer id) throws Exception {
        Tag tag = _tagService.getTagById(id);
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.NAME, tag.getName());
        
        try {
            _tagService.deleteTagById(id);
        } catch (Exception e) {
            LOGGER.error("An error occurred attempting to delete tag '" + tag.getName() + "'", e);
            return _modelAndViewUtil.createErrorJsonModelAndView(
                    String.format(DELETE_ERROR_MESSAGE_FORMAT, tag.getName()), mm);
        }
        
        return _modelAndViewUtil.createSuccessJsonModelAndView(
                String.format(DELETE_SUCCESS_MESSAGE_FORMAT, tag.getName()), mm);
    }

}

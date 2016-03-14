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
    
    @Resource
    private TagService _tagService;
    @Resource
    private ModelAndViewUtil _modelAndViewUtil;
    
    private static final class URL {
        public static final String TAGS = ADMIN_URL_PREFIX + "/tags";
        public static final String DELETE = TAGS + "/delete";
        public static final String EDIT = TAGS + "/edit";
        public static final String CREATE = TAGS + "/create";
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
    public Link getLinkToEditTag(int id) {
        return new Link(URL.EDIT).setParam(URLParam.ID, String.valueOf(id));
    }

    @Override
    public Link getLinkToCreateTag() {
        return new Link(URL.CREATE);
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
    
    @RequestMapping(value=URL.EDIT, method=RequestMethod.GET)
    public ModelAndView editTag(
            @RequestParam(value=URLParam.ID) Integer id) {
        Tag tag = _tagService.getTagById(id);
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.TAG, tag);
        
        return new ModelAndView(TAGS_VIEW_PREFIX + "/edit_tag", mm);
    }
    
    @RequestMapping(value=URL.CREATE, method=RequestMethod.GET)
    public ModelAndView createTag() {
        return new ModelAndView(TAGS_VIEW_PREFIX + "/create_tag");
    }
    
    @RequestMapping(value=URL.SAVE, method=RequestMethod.POST)
    public ModelAndView saveTag(
            @RequestParam(value=URLParam.NAME) String name,
            @RequestParam(value=URLParam.PARENT, required=false) Integer parentId, 
            @RequestParam(value=URLParam.ID, required=false) Integer id) throws Exception {
        Tag tag = null;
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
        
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.TAG, tag);
        
        return _modelAndViewUtil.createSuccessJsonModelAndViewWithHtml(
                TAGS_VIEW_PREFIX + "/save_tag_success", mm);
    }
    
    @RequestMapping(value=URL.DELETE, method=RequestMethod.POST)
    public ModelAndView deleteTag(
            @RequestParam(value=URLParam.ID) Integer id) throws Exception {
        Tag tag = _tagService.getTagById(id);
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.NAME, tag.getName());
        
        _tagService.deleteTagById(id);
        
        return _modelAndViewUtil.createSuccessJsonModelAndViewWithHtml(
                TAGS_VIEW_PREFIX + "/delete_tag_success", mm);
    }

}

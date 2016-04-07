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
import com.filth.link.ManagePositionsLinkGenerator;
import com.filth.model.Position;
import com.filth.service.PositionService;
import com.filth.util.ModelAndViewUtil;

@Controller
public class ManagePositionsController extends ManageEntityController implements ManagePositionsLinkGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagePositionsController.class);
    
    private static final String ENTITY_NAME = "Position";
    
    @Resource
    private PositionService _positionService;
    @Resource
    private ModelAndViewUtil _modelAndViewUtil;
    
    private static final class URL {
        public static final String POSITIONS = ADMIN_URL_PREFIX + "/positions";
        public static final String SAVE = POSITIONS + "/save";
    }
    
    private static final class URLParam {
        public static final String ID = "id";
        public static final String TITLE = "title";
    }
    
    private static final class ModelKey {
        public static final String POSITIONS = "positions";
        public static final String POSITION = "position";
    }

    @Override
    public Link getLinkToManagePositions() {
        return new Link(URL.POSITIONS);
    }
    
    @Override
    public Link getLinkToSavePosition() {
        return new Link(URL.SAVE);
    }

    @RequestMapping(value=URL.POSITIONS, method=RequestMethod.GET)
    public ModelAndView managePositions() {
        List<Position> positions = _positionService.getAllPositions();
        
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.POSITIONS, positions);

        return new ModelAndView(ADMIN_VIEW_PREFIX + "/manage_positions", mm);
    }
    
    @RequestMapping(value=URL.SAVE, method=RequestMethod.POST)
    public ModelAndView savePosition(
            @RequestParam(value=URLParam.ID, required=false) Integer id,
            @RequestParam(value=URLParam.TITLE) String title) throws Exception {
        Position position = null;
        
        try {
            if (null != id) {
                position = _positionService.getPosition(id);
            } else {
                position = new Position();
            }
            
            position.setTitle(title);
            _positionService.savePosition(position);
        } catch (Exception e) {
            LOGGER.error(String.format(SAVE_ERROR_LOG_MESSAGE_FORMAT, ENTITY_NAME, title), e);
            return _modelAndViewUtil.createErrorJsonModelAndView(
                    String.format(SAVE_ERROR_MESSAGE_FORMAT, ENTITY_NAME, title), new ModelMap());
        }
        
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.POSITION, position);
        
        return _modelAndViewUtil.createSuccessJsonModelAndView(
                String.format(SAVE_SUCCESS_MESSAGE_FORMAT, ENTITY_NAME, title), mm);
    }
    
}

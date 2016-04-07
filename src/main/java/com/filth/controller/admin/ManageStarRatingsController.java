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
import com.filth.link.ManageStarRatingsLinkGenerator;
import com.filth.model.StarRating;
import com.filth.service.StarRatingService;
import com.filth.util.ModelAndViewUtil;

@Controller
public class ManageStarRatingsController extends ManageEntityController implements ManageStarRatingsLinkGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManageStarRatingsController.class);
    
    private static final String ENTITY_NAME = "Star Rating";
    
    @Resource
    private StarRatingService _starService;
    @Resource
    private ModelAndViewUtil _modelAndViewUtil;
    
    private static final class URL {
        public static final String STAR_RATINGS = ADMIN_URL_PREFIX + "/starRatings";
        public static final String SAVE = STAR_RATINGS + "/save";
    }
    
    private static final class URLParam {
        public static final String ID = "id";
        public static final String RATING = "rating";
    }
    
    private static final class ModelKey {
        public static final String RATINGS = "starRatings";
        public static final String RATING = "starRating";
    }

    @Override
    public Link getLinkToManageStarRatings() {
        return new Link(URL.STAR_RATINGS);
    }
    
    @Override
    public Link getLinkToSaveStarRating() {
        return new Link(URL.SAVE);
    }

    @RequestMapping(value=URL.STAR_RATINGS, method=RequestMethod.GET)
    public ModelAndView manageStarRatings() {
        List<StarRating> starRatings = _starService.getAllStarRatings();
        
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.RATINGS, starRatings);

        return new ModelAndView(ADMIN_VIEW_PREFIX + "/manage_star_ratings", mm);
    }
    
    @RequestMapping(value=URL.SAVE, method=RequestMethod.POST)
    public ModelAndView saveStarRating(
            @RequestParam(value=URLParam.ID, required=false) Integer id,
            @RequestParam(value=URLParam.RATING) String rating) throws Exception {
        StarRating starRating = null;
        
        try {
            if (null != id) {
                starRating = _starService.getStarRating(id);
            } else {
                starRating = new StarRating();
            }
            
            starRating.setRating(rating);
            _starService.saveStarRating(starRating);
        } catch (Exception e) {
            LOGGER.error(String.format(SAVE_ERROR_LOG_MESSAGE_FORMAT, ENTITY_NAME, rating), e);
            return _modelAndViewUtil.createErrorJsonModelAndView(
                    String.format(SAVE_ERROR_MESSAGE_FORMAT, ENTITY_NAME, rating), new ModelMap());
        }
        
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.RATING, starRating);
        
        return _modelAndViewUtil.createSuccessJsonModelAndView(
                String.format(SAVE_SUCCESS_MESSAGE_FORMAT, ENTITY_NAME, rating), mm);
    }
    
}

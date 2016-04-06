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
import com.filth.link.ManageMpaaRatingsLinkGenerator;
import com.filth.model.MpaaRating;
import com.filth.service.MpaaRatingService;
import com.filth.util.ModelAndViewUtil;

@Controller
public class ManageMpaaRatingsController extends ManageEntityController implements ManageMpaaRatingsLinkGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManageMpaaRatingsController.class);
    
    private static final String ENTITY_NAME = "MPAA Rating";
    
    @Resource
    private MpaaRatingService _mpaaService;
    @Resource
    private ModelAndViewUtil _modelAndViewUtil;
    
    private static final class URL {
        public static final String MPAA_RATINGS = ADMIN_URL_PREFIX + "/mpaa";
        public static final String SAVE = MPAA_RATINGS + "/save";
    }
    
    private static final class URLParam {
        public static final String ID = "id";
        public static final String RATING_CODE = "ratingCode";
    }
    
    private static final class ModelKey {
        public static final String RATINGS = "mpaaRatings";
        public static final String RATING = "mpaaRating";
    }

    @Override
    public Link getLinkToManageMpaaRatings() {
        return new Link(URL.MPAA_RATINGS);
    }
    
    @Override
    public Link getLinkToSaveMpaaRating() {
        return new Link(URL.SAVE);
    }

    @RequestMapping(value=URL.MPAA_RATINGS, method=RequestMethod.GET)
    public ModelAndView manageMpaaRatings() {
        List<MpaaRating> mpaaRatings = _mpaaService.getAllMpaaRatings();
        
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.RATINGS, mpaaRatings);

        return new ModelAndView(ADMIN_VIEW_PREFIX + "/manage_mpaa_ratings", mm);
    }
    
    @RequestMapping(value=URL.SAVE, method=RequestMethod.POST)
    public ModelAndView saveMpaaRating(
            @RequestParam(value=URLParam.ID, required=false) Integer id,
            @RequestParam(value=URLParam.RATING_CODE) String ratingCode) throws Exception {
        MpaaRating mpaaRating = null;
        
        try {
            if (null != id) {
                mpaaRating = _mpaaService.getMpaaRating(id);
            } else {
                mpaaRating = new MpaaRating();
            }
            
            mpaaRating.setRatingCode(ratingCode);
            _mpaaService.saveMpaaRating(mpaaRating);
        } catch (Exception e) {
            LOGGER.error(String.format(SAVE_ERROR_LOG_MESSAGE_FORMAT, ENTITY_NAME, ratingCode), e);
            return _modelAndViewUtil.createErrorJsonModelAndView(
                    String.format(SAVE_ERROR_MESSAGE_FORMAT, ENTITY_NAME, ratingCode), new ModelMap());
        }
        
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.RATING, mpaaRating);
        
        return _modelAndViewUtil.createSuccessJsonModelAndView(
                String.format(SAVE_SUCCESS_MESSAGE_FORMAT, ENTITY_NAME, ratingCode), mm);
    }
    
}

package com.filth.controller.admin;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
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
import com.filth.link.Link;
import com.filth.link.ManageMovieSequencesLinkGenerator;
import com.filth.model.MovieSequence;
import com.filth.model.MovieSequenceType;
import com.filth.service.MovieSequenceService;
import com.filth.util.ModelAndViewUtil;

@Controller
public class ManageMovieSequencesController extends ManageEntityController implements ManageMovieSequencesLinkGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManageMovieSequencesController.class);
    
    private static final String ENTITY_NAME = "MovieSequence";
    
    @Resource
    private MovieSequenceService _sequenceService;
    @Resource
    private ModelAndViewUtil _modelAndViewUtil;
    
    private static final class URL {
        public static final String MOVIE_SEQUENCES = ADMIN_URL_PREFIX + "/movieSequences";
        public static final String SAVE = MOVIE_SEQUENCES + "/save";
    }
    
    private static final class URLParam {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String TYPE = "type";
        
    }
    
    private static final class ModelKey {
        public static final String MOVIE_SEQUENCES = "sequences";
        public static final String MOVIE_SEQUENCE = "sequence";
        public static final String MOVIE_SEQUENCE_TYPES = "sequenceTypes";
    }

    @Override
    public Link getLinkToManageMovieSequences() {
        return new Link(URL.MOVIE_SEQUENCES);
    }

    @Override
    public Link getLinkToSaveMovieSequence() {
        return new Link(URL.SAVE);
    }
    
    @RequestMapping(value=URL.MOVIE_SEQUENCES, method=RequestMethod.GET)
    public ModelAndView manageMovieSequences() {
        List<MovieSequence> movieSequences = _sequenceService.getAllSequences();
        
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.MOVIE_SEQUENCES, movieSequences);
        mm.put(ModelKey.MOVIE_SEQUENCE_TYPES, MovieSequenceType.values());

        return new ModelAndView(ADMIN_VIEW_PREFIX + "/manage_movie_sequences", mm);
    }
    
    @SkipInterceptor({BackgroundImageInterceptor.class})
    @RequestMapping(value=URL.SAVE, method=RequestMethod.POST)
    public ModelAndView saveMovieSequence(
            @RequestParam(value=URLParam.NAME) String name,
            @RequestParam(value=URLParam.TYPE, required=false) String type,
            @RequestParam(value=URLParam.ID, required=false) Integer id) throws Exception {
        MovieSequence movieSequence = null;
        
        try {
            if (null != id) {
                movieSequence = _sequenceService.getSequenceById(id);
            } else {
                movieSequence = new MovieSequence();
            }
            
            if (StringUtils.isNotEmpty(type)) {
                movieSequence.setSequenceType(type);
            }
            
            movieSequence.setName(name);
            
            _sequenceService.saveSequence(movieSequence);
        } catch (Exception e) {
            LOGGER.error(String.format(SAVE_ERROR_LOG_MESSAGE_FORMAT, ENTITY_NAME, name), e);
            return _modelAndViewUtil.createErrorJsonModelAndView(
                    String.format(SAVE_ERROR_MESSAGE_FORMAT, ENTITY_NAME, name), new ModelMap());
        }
        
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.MOVIE_SEQUENCE, movieSequence);
        
        return _modelAndViewUtil.createSuccessJsonModelAndView(
                String.format(SAVE_SUCCESS_MESSAGE_FORMAT, ENTITY_NAME, name), mm);
    }
}

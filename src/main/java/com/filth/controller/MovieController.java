package com.filth.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.filth.annotation.SkipInterceptor;
import com.filth.interceptor.BackgroundImageInterceptor;
import com.filth.model.Movie;
import com.filth.service.MovieService;
import com.filth.util.ModelAndViewUtil;

@Controller
public class MovieController {
    
    private static final String MOVIES_VIEW_PREFIX = "/movie";
    
    @Resource
    private MovieService _movieService;
    @Resource
    private ModelAndViewUtil _modelAndViewUtil;
    
    private static final class URL {
        public static final String MOVIE = "/movie";
        public static final String MOVIES = "/movies";
    }
    
    private static final class URLParam {
        public static final String ID = "id";
    }
    
    private static final class ModelKey {
        public static final String MOVIE = "movie";
        public static final String MOVIES = "movies";
    }
    
    @RequestMapping(value=URL.MOVIES, method=RequestMethod.GET)
    public ModelAndView movies() {
        List<Movie> movies = _movieService.getAllMovies();
        
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.MOVIES, movies);

        return new ModelAndView(MOVIES_VIEW_PREFIX + "/movies", mm);
    }
    
    @SkipInterceptor({BackgroundImageInterceptor.class})
    @RequestMapping(value=URL.MOVIE, method=RequestMethod.GET)
    public ModelAndView viewMovie(
            @RequestParam(value=URLParam.ID) Integer movieId) {
        Movie movie = _movieService.getMovieById(movieId);
        
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.MOVIE, movie);

        return new ModelAndView(MOVIES_VIEW_PREFIX + "/movieModal", mm);
    }
}

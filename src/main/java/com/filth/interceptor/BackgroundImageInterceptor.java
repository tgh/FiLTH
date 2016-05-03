package com.filth.interceptor;

import java.io.File;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import com.filth.model.Movie;
import com.filth.resolver.ExceptionResolver;
import com.filth.service.MovieService;
import com.filth.util.ModelAndViewUtil;

/**
 * Interceptor to randomly choose a background image for the page.
 */
@Component
public class BackgroundImageInterceptor extends SkippableInterceptor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BackgroundImageInterceptor.class);
    
    /** these are public so that {@link ExceptionResolver} can use them */
    public static final String BG_IMAGES_PATH = "/images/backgrounds";
    public static final String DEFAULT_BG_IMAGE = "1548.1.jpg";
    public static final String DEFAULT_BG_IMAGE_MOVIE_TITLE = "Star Wars";
    public static final Integer DEFAULT_BG_IMAGE_MOVIE_YEAR = 1977;
    public static final Integer DEFAULT_BG_IMAGE_MOVIE_ID = 1548;
    
    @Resource
    private ModelAndViewUtil _modelAndViewUtil;
    @Resource
    private MovieService _movieService;

    @Override
    public void postHandleIfApplies(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) {
        if (modelAndView != null) {
            String bgImageFilename = DEFAULT_BG_IMAGE;
            String bgImageMovieTitle = DEFAULT_BG_IMAGE_MOVIE_TITLE;
            Integer bgImageMovieYear = DEFAULT_BG_IMAGE_MOVIE_YEAR;
            Integer bgImageMovieId = DEFAULT_BG_IMAGE_MOVIE_ID;
            
            try {
                //get list of all bg images
                File backgroundsFolder = new File(request.getSession().getServletContext().getRealPath(BG_IMAGES_PATH));
                File[] backgrounds = backgroundsFolder.listFiles();
                int numBackgrounds = backgrounds.length;

                //choose a random bg image
                Random random = new Random();
                int n = random.nextInt(numBackgrounds);
                bgImageFilename = backgrounds[n].getName();
                
                //extract the movie id from the image filename
                String bgImageMidString = bgImageFilename.substring(0, bgImageFilename.indexOf('.'));
                int mid = Integer.parseInt(bgImageMidString);
                
                //get the Movie corresponding to the bg image
                Movie movie = _movieService.getMovieByIdUninitialized(mid);
                if (null != movie) {
                    bgImageMovieTitle = movie.getTitle();
                    bgImageMovieYear = movie.getYear();
                    bgImageMovieId = movie.getId();
                } else {
                    //movie not found (for example, when running the app with test data), so use default
                    bgImageFilename = DEFAULT_BG_IMAGE;
                }
            } catch (Exception e) {
                LOGGER.error("Error during background image selection--falling back to the default: " +
                              DEFAULT_BG_IMAGE + " -- \"" + DEFAULT_BG_IMAGE_MOVIE_TITLE + "\" (" +
                              DEFAULT_BG_IMAGE_MOVIE_YEAR + ")", e);
            }
            
            _modelAndViewUtil.addBackgroundImageData(modelAndView.getModelMap(), BG_IMAGES_PATH, bgImageFilename,
                    bgImageMovieTitle, bgImageMovieYear, bgImageMovieId);
        }
    }
}

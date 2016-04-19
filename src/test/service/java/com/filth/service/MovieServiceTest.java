package com.filth.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import com.filth.model.ListMovie;
import com.filth.model.Movie;
import com.filth.model.MovieCrewPerson;
import com.filth.model.MovieOscar;
import com.filth.model.MovieTyler;
import com.filth.model.Tag;

public class MovieServiceTest extends ServiceTestAbstract {
    
    @Resource
    private MovieService _movieService;
    
    
    @Test
    public void getTags() {
        Movie movie = _movieService.getMovieById(3873); //The Revenant
        assertNotNull(movie);
        Set<Tag> tags = movie.getTags();
        assertTrue(CollectionUtils.isNotEmpty(tags));
        assertEquals(17, tags.size());
    }
    
    @Test
    public void getMovieCrewPersons() {
        Movie movie = _movieService.getMovieById(1548); //Star Wars
        assertNotNull(movie);
        Set<MovieCrewPerson> movieCrewPersons = movie.getMovieCrewPersons();
        assertTrue(CollectionUtils.isNotEmpty(movieCrewPersons));
        assertEquals(3, movieCrewPersons.size());
    }
    
    @Test
    public void getMovieOscars() {
        Movie movie = _movieService.getMovieById(338); //Citizen Kane
        assertNotNull(movie);
        Set<MovieOscar> movieOscars = movie.getMovieOscars();
        assertTrue(CollectionUtils.isNotEmpty(movieOscars));
        assertEquals(6, movieOscars.size());
    }
    
    @Test
    public void getMovieTylers() {
        Movie movie = _movieService.getMovieById(283); //Cache
        assertNotNull(movie);
        Set<MovieTyler> movieTylers = movie.getMovieTylers();
        assertTrue(CollectionUtils.isNotEmpty(movieTylers));
        assertEquals(6, movieTylers.size());
    }
    
    @Test
    public void getListMovies() {
        Movie movie = _movieService.getMovieById(339); //City Lights
        assertNotNull(movie);
        Set<ListMovie> listMovies = movie.getListMovies();
        assertTrue(CollectionUtils.isNotEmpty(listMovies));
        assertEquals(1, listMovies.size());
    }

}

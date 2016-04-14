package com.filth.service;

import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;

import org.junit.Test;

import com.filth.model.Movie;

public class MovieServiceTest extends ServiceTestAbstract {
    
    @Resource
    private MovieService _movieService;
    
    
    @Test
    public void test() {
        Movie movie = _movieService.getMovieById(1545);
        assertNotNull(movie);
    }
    
    @Test
    public void test2() {
        Movie movie = _movieService.getMovieById(9);
        assertNotNull(movie);
    }

}

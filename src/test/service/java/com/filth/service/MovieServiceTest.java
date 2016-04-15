package com.filth.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import com.filth.model.Movie;
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

}

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

public class TagServiceTest extends ServiceTestAbstract {

    @Resource
    private TagService _tagService;
    
    @Test
    public void getMovies() {
        Tag tag = _tagService.getTagById(38);   //new-york-city
        assertNotNull(tag);
        Set<Movie> movies = tag.getMovies();
        assertTrue(CollectionUtils.isNotEmpty(movies));
        assertEquals(6, movies.size());
    }
}

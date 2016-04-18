package com.filth.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import com.filth.model.MovieOscar;
import com.filth.model.Oscar;


public class OscarServiceTest extends ServiceTestAbstract {

    @Resource
    private OscarService _oscarService;
    
    @Test
    public void test() {
        Oscar oscar = _oscarService.getOscarById(1);
        assertNotNull(oscar);
    }
    
    @Test
    public void getMovieOscars() {
        Oscar oscar = _oscarService.getOscarById(1); //Best Picture
        assertNotNull(oscar);
        Set<MovieOscar> movieOscars = oscar.getMovieOscars();
        assertTrue(CollectionUtils.isNotEmpty(movieOscars));
        assertEquals(9, movieOscars.size());
    }
}

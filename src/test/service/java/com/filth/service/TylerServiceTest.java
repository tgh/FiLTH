package com.filth.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import com.filth.model.MovieTyler;
import com.filth.model.Tyler;

public class TylerServiceTest extends ServiceTestAbstract {
    
    @Resource
    private TylerService _tylerService;
    
    @Test
    public void getMovieTylers() {
        Tyler tyler = _tylerService.getTylerById(12); //Best Scene
        assertNotNull(tyler);
        Set<MovieTyler> movieTylers = tyler.getMovieTylers();
        assertTrue(CollectionUtils.isNotEmpty(movieTylers));
        assertEquals(5, movieTylers.size());
    }

}

package com.filth.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import com.filth.model.List;
import com.filth.model.ListMovie;

public class ListServiceTest extends ServiceTestAbstract {
    
    @Resource
    private ListService _listService;
    
    @Test
    public void getListMovies() {
        List list = _listService.getListById(1);    //Greatest Movies #1
        assertNotNull(list);
        Set<ListMovie> listMovies = list.getListMovies();
        assertTrue(CollectionUtils.isNotEmpty(listMovies));
        assertEquals(7, listMovies.size());
    }

}

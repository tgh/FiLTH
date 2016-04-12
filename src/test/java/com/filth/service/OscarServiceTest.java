package com.filth.service;

import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;

import org.junit.Test;

import com.filth.model.Oscar;


public class OscarServiceTest extends ServiceTestAbstract {

    @Resource
    private OscarService _oscarService;
    
    @Test
    public void test() {
        Oscar oscar = _oscarService.getOscarById(1);
        assertNotNull(oscar);
    }
}

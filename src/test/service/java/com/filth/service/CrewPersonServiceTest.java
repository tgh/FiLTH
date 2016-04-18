package com.filth.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import com.filth.model.CrewPerson;
import com.filth.model.MovieCrewPerson;

public class CrewPersonServiceTest extends ServiceTestAbstract {
    
    @Resource
    private CrewPersonService _crewPersonService;

    @Test
    public void getMovieCrewPersons() {
        CrewPerson crewPerson = _crewPersonService.getCrewPersonById(319); //George Lucas
        assertNotNull(crewPerson);
        Set<MovieCrewPerson> movieCrewPersons = crewPerson.getMovieCrewPersons();
        assertTrue(CollectionUtils.isNotEmpty(movieCrewPersons));
        assertEquals(8, movieCrewPersons.size());
    }
    
}

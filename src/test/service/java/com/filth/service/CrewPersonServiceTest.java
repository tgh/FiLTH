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
import com.filth.model.MovieOscar;
import com.filth.model.MovieTyler;

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

    @Test
    public void getMovieOscars() {
        CrewPerson crewPerson = _crewPersonService.getCrewPersonById(309); //Stanley Kubrick
        assertNotNull(crewPerson);
        Set<MovieOscar> movieOscars = crewPerson.getMovieOscars();
        assertTrue(CollectionUtils.isNotEmpty(movieOscars));
        assertEquals(2, movieOscars.size());
    }

    @Test
    public void getMovieTylers() {
        CrewPerson crewPerson = _crewPersonService.getCrewPersonById(252); //Darren Aronofsky
        assertNotNull(crewPerson);
        Set<MovieTyler> movieTylers = crewPerson.getMovieTylers();
        assertTrue(CollectionUtils.isNotEmpty(movieTylers));
        assertEquals(2, movieTylers.size());
    }
    
}

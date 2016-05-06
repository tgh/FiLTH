package com.filth.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import com.filth.model.MovieSequence;
import com.filth.model.MovieSequenceMovie;

public class MovieSequenceServiceTest extends ServiceTestAbstract {

    @Resource
    private MovieSequenceService _sequenceService;
    
    @Test
    public void getAllSequences() {
        List<MovieSequence> sequences = _sequenceService.getAllSequences();
        assertTrue(CollectionUtils.isNotEmpty(sequences));
        assertEquals(4, sequences.size());
    }
    
    @Test
    public void getSequenceById() {
        MovieSequence sequence = _sequenceService.getSequenceById(1); //The Up Documentaries
        assertNotNull(sequence);
        assertEquals("The 'Up' Documentaries", sequence.getName());
        assertEquals("SERIES", sequence.getSequenceType());
        Set<MovieSequenceMovie> sequenceMovies = sequence.getMovieSequenceMovies();
        assertTrue(CollectionUtils.isNotEmpty(sequenceMovies));
        assertEquals(8, sequenceMovies.size());
    }
}

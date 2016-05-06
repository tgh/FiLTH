package com.filth.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import com.filth.model.ListMovie;
import com.filth.model.Movie;
import com.filth.model.MovieCrewPerson;
import com.filth.model.MovieLink;
import com.filth.model.MovieOscar;
import com.filth.model.MovieSequenceMovie;
import com.filth.model.MovieTyler;
import com.filth.model.Status;
import com.filth.model.Tag;

public class MovieServiceTest extends ServiceTestAbstract {
    
    @Resource
    private MovieService _movieService;
    
    
    @Test
    public void getAllMovies() {
        List<Movie> movies = _movieService.getAllMovies();
        assertTrue(CollectionUtils.isNotEmpty(movies));
        assertEquals(61, movies.size());
    }
    
    @Test
    public void getMovieById() {
        Movie movie = _movieService.getMovieById(1548); //Star Wars
        Set<Tag> tags = movie.getTags();
        assertNotNull("Tag set is unexpectedly null", tags);
        assertEquals("Number of tags", 5, tags.size());
        Set<MovieCrewPerson> movieCrewPersons = movie.getMovieCrewPersons();
        assertNotNull("MovieCrewPerson set is unexpectedly null", movieCrewPersons);
        assertEquals("Number of crew persons", 3, movieCrewPersons.size());
        Set<MovieOscar> movieOscars = movie.getMovieOscars();
        assertNotNull("MovieOscar set is unexpectedly null", movieOscars);
        assertEquals("Number of oscars", 4, movieOscars.size());
        Set<MovieTyler> movieTylers = movie.getMovieTylers();
        assertTrue("MovieTyler set unexpectedly not empty", CollectionUtils.isEmpty(movieTylers));
        Set<ListMovie> listMovies = movie.getListMovies();
        assertNotNull("ListMovie set unexpectedly null", listMovies);
        assertEquals("Number of lists", 1, listMovies.size());
        Set<MovieLink> movieLinks = movie.getMovieLinks();
        assertTrue("Movie-links-from set unexpectedly not empty", CollectionUtils.isEmpty(movieLinks));
        Set<MovieSequenceMovie> sequenceMovies = movie.getMovieSequenceMovies();
        assertNotNull("MovieSequenceMovie set is unexpectedly null", sequenceMovies);
        assertEquals("Number of sequences", 2, sequenceMovies.size());
        Movie parent = movie.getParent();
        assertNull("Parent movie unexpectedly not null", parent);
        Set<Movie> children = movie.getChildren();
        assertTrue("Child movies set unexpectedly not empty", CollectionUtils.isEmpty(children));
    }
    
    @Test
    public void getTags() {
        Movie movie = _movieService.getMovieById(3873); //The Revenant
        assertNotNull(movie);
        Set<Tag> tags = movie.getTags();
        assertTrue(CollectionUtils.isNotEmpty(tags));
        assertEquals(17, tags.size());
    }
    
    @Test
    public void getMovieCrewPersons() {
        Movie movie = _movieService.getMovieById(1548); //Star Wars
        assertNotNull(movie);
        Set<MovieCrewPerson> movieCrewPersons = movie.getMovieCrewPersons();
        assertTrue(CollectionUtils.isNotEmpty(movieCrewPersons));
        assertEquals(3, movieCrewPersons.size());
    }
    
    @Test
    public void getMovieOscars() {
        Movie movie = _movieService.getMovieById(338); //Citizen Kane
        assertNotNull(movie);
        Set<MovieOscar> movieOscars = movie.getMovieOscars();
        assertTrue(CollectionUtils.isNotEmpty(movieOscars));
        assertEquals(6, movieOscars.size());
        MovieOscar movieOscar = movieOscars.iterator().next();
        Status status = movieOscar.getStatus();
        assertNotNull(status);
    }
    
    @Test
    public void getMovieTylers() {
        Movie movie = _movieService.getMovieById(283); //Cache
        assertNotNull(movie);
        Set<MovieTyler> movieTylers = movie.getMovieTylers();
        assertTrue(CollectionUtils.isNotEmpty(movieTylers));
        assertEquals(6, movieTylers.size());
        MovieTyler movieTyler = movieTylers.iterator().next();
        Status status = movieTyler.getStatus();
        assertNotNull(status);
    }
    
    @Test
    public void getListMovies() {
        Movie movie = _movieService.getMovieById(339); //City Lights
        assertNotNull(movie);
        Set<ListMovie> listMovies = movie.getListMovies();
        assertTrue(CollectionUtils.isNotEmpty(listMovies));
        assertEquals(1, listMovies.size());
    }
    
    @Test
    public void getMovieLinks() {
        Movie movie = _movieService.getMovieById(3873); //The Revenant
        assertNotNull(movie);
        Set<MovieLink> movieLinks = movie.getMovieLinks();
        assertTrue(CollectionUtils.isNotEmpty(movieLinks));
        assertEquals(1, movieLinks.size());
        
        movie = _movieService.getMovieById(3916); //The Man in the Wilderness
        assertNotNull(movie);
        movieLinks = movie.getMovieLinks();
        assertTrue(CollectionUtils.isNotEmpty(movieLinks));
        assertEquals(1, movieLinks.size());
    }
    
    @Test
    public void getParent() {
        Movie movie = _movieService.getMovieById(20); //42 Up
        assertNotNull(movie);
        Movie parent = movie.getParent();
        assertNotNull(parent);
        assertEquals("The Up Documentaries", parent.getTitle());
    }
    
    @Test
    public void getChildren() {
        Movie movie = _movieService.getMovieById(3762); //The Up Documentaries
        assertNotNull(movie);
        Set<Movie> children = movie.getChildren();
        assertTrue(CollectionUtils.isNotEmpty(children));
        assertEquals(8, children.size());
    }
    
    @Test
    public void getRemakeOfMovie() {
        Movie movie = _movieService.getMovieById(3560); //Sabrina (1995)
        assertNotNull(movie);
        Movie original = movie.getRemakeOfMovie();
        assertNotNull(original);
        assertTrue(original.getTitle().equals("Sabrina") && original.getYear() == 1954);
    }
    
    @Test
    public void getRemadeByMovies() {
        Movie movie = _movieService.getMovieById(2340); //Sabrina (1954)
        assertNotNull(movie);
        Set<Movie> remakes = movie.getRemadeByMovies();
        assertTrue(CollectionUtils.isNotEmpty(remakes));
        assertEquals(1, remakes.size());
        //since we know there is only 1 at this point
        for (Movie remake : remakes) {
            assertTrue(remake.getTitle().equals("Sabrina") && remake.getYear() == 1995);
        }
    }
    
    @Test
    public void getMovieSequenceMovies() {
        Movie movie = _movieService.getMovieById(17); //28 Up
        assertNotNull(movie);
        Set<MovieSequenceMovie> sequenceMovies = movie.getMovieSequenceMovies();
        assertTrue(CollectionUtils.isNotEmpty(sequenceMovies));
        assertEquals(1, sequenceMovies.size());
        //since we know there is only 1 at this point
        for (MovieSequenceMovie sequenceMovie : sequenceMovies) {
            assertEquals("The 'Up' Documentaries", sequenceMovie.getSequence().getName());
            assertEquals(4, sequenceMovie.getOrderIndex());
        }
    }

}

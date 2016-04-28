package com.filth.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.filth.dao.MovieDAO;
import com.filth.model.Movie;

@Service
public class MovieService {
    
    @Resource
    private MovieDAO _movieDAO;
    
    @Transactional(readOnly=true)
    public List<Movie> getAllMovies() {
        return _movieDAO.getAll();
    }
    
    @Transactional(readOnly=true)
    public Movie getMovieById(int id) {
        return _movieDAO.getById(id);
    }
    
    /**
     * Get the movie that has the given id, but without it's join properties
     * initialized by Hibernate. Essentially a bare-bones {@link Movie} object
     * with only those properties in the movie table--no joined entities
     * attached.
     */
    @Transactional(readOnly=true)
    public Movie getMovieByIdUninitialized(int id) {
        return _movieDAO.getByIdUninitialized(id);
    }

}

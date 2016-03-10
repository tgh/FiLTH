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

}

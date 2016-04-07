package com.filth.dao;

import org.springframework.stereotype.Repository;

import com.filth.model.Movie;

@Repository
public class MovieDAO extends HibernateDAO<Movie> {

    @Override
    protected Class<Movie> getEntityClass() {
        return Movie.class;
    }
    
}

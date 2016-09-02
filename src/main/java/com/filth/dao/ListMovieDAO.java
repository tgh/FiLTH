package com.filth.dao;

import org.springframework.stereotype.Repository;

import com.filth.model.ListMovie;

@Repository
public class ListMovieDAO extends HibernateDAO<ListMovie> {

    @Override
    protected Class<ListMovie> getEntityClass() {
        return ListMovie.class;
    }

}

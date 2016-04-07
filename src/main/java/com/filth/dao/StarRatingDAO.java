package com.filth.dao;

import org.springframework.stereotype.Repository;

import com.filth.model.StarRating;

@Repository
public class StarRatingDAO extends HibernateDAO<StarRating> {
    
    @Override
    protected Class<StarRating> getEntityClass() {
        return StarRating.class;
    }
    
}

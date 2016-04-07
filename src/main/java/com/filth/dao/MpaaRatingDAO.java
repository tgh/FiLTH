package com.filth.dao;

import org.springframework.stereotype.Repository;

import com.filth.model.MpaaRating;

@Repository
public class MpaaRatingDAO extends HibernateDAO<MpaaRating> {
    
    @Override
    protected Class<MpaaRating> getEntityClass() {
        return MpaaRating.class;
    }
    
}

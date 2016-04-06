package com.filth.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.filth.model.MpaaRating;

@Repository
public class MpaaRatingDAO extends HibernateDAO<MpaaRating> {
    
    public List<MpaaRating> getAll() {
        Query query = getSession().createQuery("from MpaaRating");
        List<MpaaRating> ratingList = extractTypedList(query);
        return ratingList;
    }
    
    public MpaaRating getById(int id) {
        return (MpaaRating) getSession().get(MpaaRating.class.getName(), id);
    }

    public void save(MpaaRating rating) {
        getSession().saveOrUpdate(rating);
    }
    
}

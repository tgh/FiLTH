package com.filth.dao;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.filth.model.MovieSequence;

@Repository
public class MovieSequenceDAO extends HibernateDAO<MovieSequence> {

    @Override
    public MovieSequence getById(int id) {
        Query query = getSession().createQuery(
                "SELECT s FROM MovieSequence s " +
                "LEFT JOIN FETCH s._movieSequenceMovies " +
                "WHERE s.id = :id"
        );
        query.setParameter("id", id);
        return (MovieSequence) query.uniqueResult();
    }
    
    @Override
    protected Class<MovieSequence> getEntityClass() {
        return MovieSequence.class;
    }

}

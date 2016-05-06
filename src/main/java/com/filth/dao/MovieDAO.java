package com.filth.dao;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.filth.model.Movie;

@Repository
public class MovieDAO extends HibernateDAO<Movie> {

    @Override
    protected Class<Movie> getEntityClass() {
        return Movie.class;
    }
    
    @Override
    public Movie getById(int id) {
        Query query = getSession().createQuery(
                "SELECT m FROM Movie m " +
                "LEFT JOIN FETCH m._tags " +
                "LEFT JOIN FETCH m._movieCrewPersons " +
                "LEFT JOIN FETCH m._movieOscars " +
                "LEFT JOIN FETCH m._movieTylers " +
                "LEFT JOIN FETCH m._listMovies " +
                "LEFT JOIN FETCH m._movieLinksFromThisMovie " +
                "LEFT JOIN FETCH m._movieLinksToThisMovie " +
                "LEFT JOIN FETCH m._childMovies " +
                "LEFT JOIN FETCH m._remadeByMovies " +
                "LEFT JOIN FETCH m._movieSequenceMovies " +
                "WHERE m.id = :id"
        );
        query.setParameter("id", id);
        return (Movie) query.uniqueResult();
    }
    
    /**
     * Get the movie with the given id but do not initialize Lazy-loaded
     * associations.
     */
    public Movie getByIdUninitialized(int id) {
        return super.getById(id);
    }
    
}

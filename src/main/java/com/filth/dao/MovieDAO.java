package com.filth.dao;

import org.hibernate.Hibernate;
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
        Movie movie = super.getById(id);
        
        if (null != movie) {
            if (false == Hibernate.isInitialized(movie.getTags())) {
                Hibernate.initialize(movie.getTags());
            }
            if (false == Hibernate.isInitialized(movie.getMovieCrewPersons())) {
                Hibernate.initialize(movie.getMovieCrewPersons());
            }
            if (false == Hibernate.isInitialized(movie.getMovieOscars())) {
                Hibernate.initialize(movie.getMovieOscars());
            }
            if (false == Hibernate.isInitialized(movie.getMovieTylers())) {
                Hibernate.initialize(movie.getMovieTylers());
            }
            if (false == Hibernate.isInitialized(movie.getMovieLinksFromThisMovie())) {
                Hibernate.initialize(movie.getMovieLinksFromThisMovie());
            }
            if (false == Hibernate.isInitialized(movie.getMovieLinksToThisMovie())) {
                Hibernate.initialize(movie.getMovieLinksToThisMovie());
            }
            if (false == Hibernate.isInitialized(movie.getListMovies())) {
                Hibernate.initialize(movie.getListMovies());
            }
        }
        
        return movie;
    }
    
    /**
     * Get the movie with the given id but do not initialize Lazy-loaded
     * associations.
     */
    public Movie getByIdUninitialized(int id) {
        return super.getById(id);
    }
    
}

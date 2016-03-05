package com.filth.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.filth.model.Movie;

@Repository
public class MovieDAO extends HibernateDAO<Movie> {

    public List<Movie> getAll() {
        Query query = getSession().createQuery("from Movie");
        List<Movie> movieList = extractTypedList(query);
        return movieList;
    }
    
    public Movie getById(int id) {
        return (Movie) getSession().get(Movie.class.getName(), id);
    }
    
    public void save(Movie movie) {
        getSession().saveOrUpdate(movie);
    }
    
}

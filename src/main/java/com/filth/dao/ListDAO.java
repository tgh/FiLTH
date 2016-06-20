package com.filth.dao;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository
public class ListDAO extends HibernateDAO<com.filth.model.List> {

    @Override
    protected Class<com.filth.model.List> getEntityClass() {
        return com.filth.model.List.class;
    }
    
    @Override
    public com.filth.model.List getById(int id) {
        Query query = getSession().createQuery(
            "SELECT l FROM List l " +
            "LEFT JOIN FETCH l._listMovies lm " +
            "LEFT JOIN FETCH lm._movie " +
            "WHERE l.id = :id"
        );
        query.setParameter("id", id);
        return (com.filth.model.List) query.uniqueResult();
    }
    
}

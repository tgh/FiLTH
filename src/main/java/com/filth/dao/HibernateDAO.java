package com.filth.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class HibernateDAO<T> {

    @Autowired
    protected SessionFactory _sessionFactory;
    
    protected Session getSession() {
        return _sessionFactory.getCurrentSession();
    }
    
    /**
     * Convenience method to remove constant use of SuppressWarnings("unchecked")
     * from child classes.
     * 
     * @param <T> the type of object being listed
     * @param q a query that can have list() called on it to produce a list of the given type
     * @return simply calls q.list() and returns the result--this method only exists for
     * compile-time convenience
     */
    @SuppressWarnings("unchecked")
    protected List<T> extractTypedList(Query q) {
        return q.list();
    }
}

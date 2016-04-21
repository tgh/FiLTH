package com.filth.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class HibernateDAO<T> {

    @Autowired
    protected SessionFactory _sessionFactory;
    
    protected abstract Class<T> getEntityClass();
    
    @SuppressWarnings("unchecked")
    public T getById(int id) {
        return (T) getSession().get(getEntityClass().getName(), id);
    }
    
    public List<T> getAll() {
        Criteria criteria = getSession().createCriteria(getEntityClass())
                                        //prevent duplicates
                                        .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return extractTypedList(criteria);
    }
    
    public void save(T entity) {
        getSession().saveOrUpdate(entity);
    }
    
    public void delete(T entity) {
        getSession().delete(entity);
    }
    
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
    
    /**
     * Convenience method to remove constant use of SuppressWarnings("unchecked")
     * from child classes.
     * 
     * @param <T> the type of object being listed
     * @param c a {@link Criteria} that can have list() called on it to produce a list of the given type
     * @return simply calls c.list() and returns the result--this method only exists for
     * compile-time convenience
     */
    @SuppressWarnings("unchecked")
    protected List<T> extractTypedList(Criteria c) {
        return c.list();
    }

    /**
     * Extract a list without duplicate entries (useful for entities with a greedy
     * one-to-many relationship).
     */
    protected List<T> extractDistinctList(Criteria c) {
        c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return extractTypedList(c);
    }

    /**
     * Extract an unordered set from the criteria (requires a functioning equals() method, clearly).
     */
    protected Set<T> extractSet(Criteria c) {
        List<T> entityList = extractDistinctList(c);
        return new HashSet<T>(entityList);
    }
    
}

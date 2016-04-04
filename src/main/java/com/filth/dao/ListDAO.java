package com.filth.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository
public class ListDAO extends HibernateDAO<com.filth.model.List> {

    public List<com.filth.model.List> getAll() {
        Query query = getSession().createQuery("from List");
        List<com.filth.model.List> listList = extractTypedList(query);
        return listList;
    }
    
    public com.filth.model.List getById(int id) {
        return (com.filth.model.List) getSession().get(com.filth.model.List.class.getName(), id);
    }
    
    public void save(com.filth.model.List list) {
        getSession().saveOrUpdate(list);
    }
    
    public void delete(com.filth.model.List list) {
        getSession().delete(list);
    }
    
}

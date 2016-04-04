package com.filth.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.filth.model.Tyler;

@Repository
public class TylerDAO extends HibernateDAO<Tyler> {
    
    public List<Tyler> getAll() {
        Query query = getSession().createQuery("from Tyler");
        List<Tyler> tylerList = extractTypedList(query);
        return tylerList;
    }
    
    public Tyler getById(int id) {
        return (Tyler) getSession().get(Tyler.class.getName(), id);
    }
    
    public void save(Tyler tyler) {
        getSession().saveOrUpdate(tyler);
    }
    
    public void delete(Tyler tyler) {
        getSession().delete(tyler);
    }

}

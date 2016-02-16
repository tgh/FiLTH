package com.filth.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.filth.model.Oscar;

@Repository
public class OscarDAO extends HibernateDAO<Oscar> {
    
    public List<Oscar> getAll() {
        Query query = getSession().createQuery("from Oscar");
        List<Oscar> oscarList = extractTypedList(query);
        return oscarList;
    }
    
    public Oscar getById(int id) {
        return (Oscar) getSession().get(Oscar.class.getName(), id);
    }
    
    public void save(Oscar oscar) {
        getSession().saveOrUpdate(oscar);
    }
    
    public void delete(Oscar oscar) {
        getSession().delete(oscar);
    }

}

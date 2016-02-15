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

}

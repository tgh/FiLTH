package com.filth.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.filth.model.CrewPerson;

@Repository
public class CrewPersonDAO extends HibernateDAO<CrewPerson> {

    @Override
    protected Class<CrewPerson> getEntityClass() {
        return CrewPerson.class;
    }
    
    @Override
    public List<CrewPerson> getAll() {
        //exclude the dummy crew whose id is 0
        Query query = getSession().createQuery("from CrewPerson where cid > 0");
        return extractTypedList(query);
    }
    
}

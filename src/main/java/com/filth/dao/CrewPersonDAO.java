package com.filth.dao;

import org.springframework.stereotype.Repository;

import com.filth.model.CrewPerson;

@Repository
public class CrewPersonDAO extends HibernateDAO<CrewPerson> {

    @Override
    protected Class<CrewPerson> getEntityClass() {
        return CrewPerson.class;
    }
    
}

package com.filth.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.filth.model.CrewPerson;

@Repository
public class CrewPersonDAO extends HibernateDAO<CrewPerson> {

    public List<CrewPerson> getAll() {
        Query query = getSession().createQuery("from CrewPerson");
        List<CrewPerson> crewList = extractTypedList(query);
        return crewList;
    }
    
    public CrewPerson getById(int id) {
        return (CrewPerson) getSession().get(CrewPerson.class.getName(), id);
    }
    
    public void save(CrewPerson crewPerson) {
        getSession().saveOrUpdate(crewPerson);
    }
    
}

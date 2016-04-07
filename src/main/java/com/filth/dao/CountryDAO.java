package com.filth.dao;

import org.springframework.stereotype.Repository;

import com.filth.model.Country;

@Repository
public class CountryDAO extends HibernateDAO<Country> {
    
    @Override
    protected Class<Country> getEntityClass() {
        return Country.class;
    }
    
}

package com.filth.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.filth.model.Country;

@Repository
public class CountryDAO extends HibernateDAO<Country> {
    
    public List<Country> getAll() {
        Query query = getSession().createQuery("from Country");
        List<Country> countryList = extractTypedList(query);
        return countryList;
    }
    
    public Country getById(int id) {
        return (Country) getSession().get(Country.class.getName(), id);
    }

    public void save(Country country) {
        getSession().saveOrUpdate(country);
    }
    
}

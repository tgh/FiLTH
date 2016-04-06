package com.filth.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.filth.dao.CountryDAO;
import com.filth.model.Country;

@Service
public class CountryService {
    
    @Resource
    private CountryDAO _mpaaDAO;

    @Transactional(readOnly=true)
    public List<Country> getAllCountries() {
        return _mpaaDAO.getAll();
    }
    
    @Transactional(readOnly=true)
    public Country getCountry(int id) {
        return _mpaaDAO.getById(id);
    }
    
    @Transactional(readOnly=false)
    public void saveCountry(Country country) {
        _mpaaDAO.save(country);
    }
    
}

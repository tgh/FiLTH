package com.filth.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.filth.dao.CrewPersonDAO;
import com.filth.model.CrewPerson;

@Service
public class CrewPersonService {

    @Resource
    private CrewPersonDAO _crewPersonDAO;
    
    @Transactional(readOnly=true)
    public List<CrewPerson> getAllCrewPersons() {
        return _crewPersonDAO.getAll();
    }
    
    @Transactional(readOnly=true)
    public CrewPerson getCrewPersonById(int id) {
        return _crewPersonDAO.getById(id);
    }
    
    @Transactional(readOnly=false)
    public void saveCrewPerson(CrewPerson crewPerson) {
        _crewPersonDAO.save(crewPerson);
    }
    
}

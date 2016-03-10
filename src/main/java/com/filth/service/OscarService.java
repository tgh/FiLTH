package com.filth.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.filth.dao.OscarDAO;
import com.filth.model.Oscar;

@Service
public class OscarService {
    
    @Resource
    private OscarDAO _oscarDAO;
    
    @Transactional(readOnly=true)
    public List<Oscar> getAllOscars() {
        return _oscarDAO.getAll();
    }
    
    @Transactional(readOnly=true)
    public Oscar getOscarById(int id) {
        return _oscarDAO.getById(id);
    }
    
    @Transactional(readOnly=false)
    public void saveOscar(Oscar oscar) {
        _oscarDAO.save(oscar);
    }
    
    @Transactional(readOnly=false)
    public void deleteOscar(Oscar oscar) {
        _oscarDAO.delete(oscar);
    }
    
    @Transactional(readOnly=false)
    public void deleteOscarById(int id) {
        Oscar oscar = getOscarById(id);
        _oscarDAO.delete(oscar);
    }

}

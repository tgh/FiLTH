package com.filth.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.filth.dao.TylerDAO;
import com.filth.model.Tyler;

@Service
public class TylerService {
    
    @Resource
    private TylerDAO _tylerDAO;
    
    @Transactional(readOnly=true)
    public List<Tyler> getAllTylers() {
        return _tylerDAO.getAll();
    }
    
    @Transactional(readOnly=true)
    public Tyler getTylerById(int id) {
        return _tylerDAO.getById(id);
    }
    
    @Transactional(readOnly=false)
    public void saveTyler(Tyler tyler) {
        _tylerDAO.save(tyler);
    }
    
    @Transactional(readOnly=false)
    public void deleteTyler(Tyler tyler) {
        _tylerDAO.delete(tyler);
    }
    
    @Transactional(readOnly=false)
    public void deleteTylerById(int id) {
        Tyler tyler = getTylerById(id);
        _tylerDAO.delete(tyler);
    }

}

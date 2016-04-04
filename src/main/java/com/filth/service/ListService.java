package com.filth.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.filth.dao.ListDAO;

@Service
public class ListService {
    
    @Resource
    private ListDAO _listDAO;
    
    @Transactional(readOnly=true)
    public List<com.filth.model.List> getAllLists() {
        return _listDAO.getAll();
    }
    
    @Transactional(readOnly=true)
    public com.filth.model.List getListById(int id) {
        return _listDAO.getById(id);
    }
    
    @Transactional(readOnly=false)
    public void saveList(com.filth.model.List list) {
        _listDAO.save(list);
    }
    
    @Transactional(readOnly=false)
    public void deleteList(com.filth.model.List list) {
        _listDAO.delete(list);
    }
    
    @Transactional(readOnly=false)
    public void deleteListById(int id) {
        com.filth.model.List list = getListById(id);
        _listDAO.delete(list);
    }

}

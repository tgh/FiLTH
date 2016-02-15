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

}

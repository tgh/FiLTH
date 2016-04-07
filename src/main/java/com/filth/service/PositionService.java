package com.filth.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.filth.dao.PositionDAO;
import com.filth.model.Position;

@Service
public class PositionService {
    
    @Resource
    private PositionDAO _positionDAO;

    @Transactional(readOnly=true)
    public List<Position> getAllPositions() {
        return _positionDAO.getAll();
    }
    
    @Transactional(readOnly=true)
    public Position getPosition(int id) {
        return _positionDAO.getById(id);
    }
    
    @Transactional(readOnly=false)
    public void savePosition(Position position) {
        _positionDAO.save(position);
    }
    
}

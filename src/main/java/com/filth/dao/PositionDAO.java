package com.filth.dao;

import org.springframework.stereotype.Repository;

import com.filth.model.Position;

@Repository
public class PositionDAO extends HibernateDAO<Position> {
    
    @Override
    protected Class<Position> getEntityClass() {
        return Position.class;
    }
    
}

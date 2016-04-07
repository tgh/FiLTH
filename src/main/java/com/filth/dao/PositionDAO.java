package com.filth.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.filth.model.Position;

@Repository
public class PositionDAO extends HibernateDAO<Position> {
    
    public List<Position> getAll() {
        Query query = getSession().createQuery("from Position");
        List<Position> positionList = extractTypedList(query);
        return positionList;
    }
    
    public Position getById(int id) {
        return (Position) getSession().get(Position.class.getName(), id);
    }

    public void save(Position position) {
        getSession().saveOrUpdate(position);
    }
    
}

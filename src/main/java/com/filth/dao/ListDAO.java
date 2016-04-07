package com.filth.dao;

import org.springframework.stereotype.Repository;

@Repository
public class ListDAO extends HibernateDAO<com.filth.model.List> {

    @Override
    protected Class<com.filth.model.List> getEntityClass() {
        return com.filth.model.List.class;
    }
    
}

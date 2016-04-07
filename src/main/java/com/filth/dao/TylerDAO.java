package com.filth.dao;

import org.springframework.stereotype.Repository;

import com.filth.model.Tyler;

@Repository
public class TylerDAO extends HibernateDAO<Tyler> {
    
    @Override
    protected Class<Tyler> getEntityClass() {
        return Tyler.class;
    }

}

package com.filth.dao;

import org.springframework.stereotype.Repository;

import com.filth.model.Oscar;

@Repository
public class OscarDAO extends HibernateDAO<Oscar> {

    @Override
    protected Class<Oscar> getEntityClass() {
        return Oscar.class;
    }

}

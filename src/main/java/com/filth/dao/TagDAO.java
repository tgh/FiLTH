package com.filth.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.filth.model.Tag;

@Repository
public class TagDAO extends HibernateDAO<Tag> {
    
    public List<Tag> getAll() {
        Query query = getSession().createQuery("from Tag");
        List<Tag> tagList = extractTypedList(query);
        return tagList;
    }
    
    public List<Tag> getAllRootTags() {
        Criteria criteria = getSession().createCriteria(Tag.class)
                                        .add(Restrictions.isNull("_parent"))
                                        .addOrder(Order.asc("_id"));
        List<Tag> tags = extractDistinctList(criteria);
        return tags;
    }
    
    public Tag getById(int id) {
        return (Tag) getSession().get(Tag.class.getName(), id);
    }
    
    public void save(Tag tag) {
        getSession().saveOrUpdate(tag);
    }
    
    public void delete(Tag tag) {
        getSession().delete(tag);
    }

}

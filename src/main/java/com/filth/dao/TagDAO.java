package com.filth.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.filth.model.Tag;

@Repository
public class TagDAO extends HibernateDAO<Tag> {
    
    @Override
    protected Class<Tag> getEntityClass() {
        return Tag.class;
    }
    
    @Override
    public List<Tag> getAll() {
        Query query = getSession().createQuery("from Tag order by tid asc"); //FIXME: order by shouldn't be necessary
        List<Tag> tagList = extractTypedList(query);
        return tagList;
    }
    
    public List<Tag> getAllRootTags() {
        Criteria criteria = getSession().createCriteria(Tag.class)
                                        .add(Restrictions.isNull("_parent"))
                                        .addOrder(Order.asc("_id")); //FIXME: order by shouldn't be necessary
        List<Tag> tags = extractDistinctList(criteria);
        return tags;
    }

}

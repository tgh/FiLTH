package com.filth.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.filth.dao.TagDAO;
import com.filth.model.Tag;

@Service
public class TagService {

    @Resource
    private TagDAO _tagDAO;
    
    @Transactional(readOnly=true)
    public List<Tag> getAllTags() {
        return _tagDAO.getAll();
    }
    
    @Transactional(readOnly=true)
    public List<Tag> getAllRootTags() {
        return _tagDAO.getAllRootTags();
    }
    
    @Transactional(readOnly=true)
    public Tag getTagById(int id) {
        return _tagDAO.getById(id);
    }
    
    @Transactional(readOnly=false)
    public void saveTag(Tag tag) {
        _tagDAO.save(tag);
    }
    
    @Transactional(readOnly=false)
    public void deleteTag(Tag tag) {
        _tagDAO.delete(tag);
    }
    
    @Transactional(readOnly=false)
    public void deleteTagById(int id) {
        Tag tag = getTagById(id);
        _tagDAO.delete(tag);
    }
}

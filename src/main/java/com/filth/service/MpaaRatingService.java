package com.filth.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.filth.dao.MpaaRatingDAO;
import com.filth.model.MpaaRating;

@Service
public class MpaaRatingService {
    
    @Resource
    private MpaaRatingDAO _mpaaDAO;

    @Transactional(readOnly=true)
    public List<MpaaRating> getAllMpaaRatings() {
        return _mpaaDAO.getAll();
    }
    
    @Transactional(readOnly=true)
    public MpaaRating getMpaaRating(int id) {
        return _mpaaDAO.getById(id);
    }
    
    @Transactional(readOnly=false)
    public void saveMpaaRating(MpaaRating rating) {
        _mpaaDAO.save(rating);
    }
    
}

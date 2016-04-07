package com.filth.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.filth.dao.StarRatingDAO;
import com.filth.model.StarRating;

@Service
public class StarRatingService {
    
    @Resource
    private StarRatingDAO _starDAO;

    @Transactional(readOnly=true)
    public List<StarRating> getAllStarRatings() {
        return _starDAO.getAll();
    }
    
    @Transactional(readOnly=true)
    public StarRating getStarRating(int id) {
        return _starDAO.getById(id);
    }
    
    @Transactional(readOnly=false)
    public void saveStarRating(StarRating rating) {
        _starDAO.save(rating);
    }
    
}

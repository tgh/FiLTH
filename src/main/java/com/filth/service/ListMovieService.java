package com.filth.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.filth.dao.ListMovieDAO;
import com.filth.model.ListMovie;

@Service
public class ListMovieService {
    
    @Resource
    private ListMovieDAO _listMovieDAO;
    
    @Transactional(readOnly=false)
    public void deleteListMovie(ListMovie listMovie) {
        _listMovieDAO.delete(listMovie);
    }

}

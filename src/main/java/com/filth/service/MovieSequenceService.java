package com.filth.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.filth.dao.MovieSequenceDAO;
import com.filth.model.MovieSequence;

@Service
public class MovieSequenceService {
    
    @Resource
    private MovieSequenceDAO _sequenceDAO;
    
    @Transactional(readOnly=true)
    public List<MovieSequence> getAllSequences() {
        return _sequenceDAO.getAll();
    }
    
    @Transactional(readOnly=true)
    public MovieSequence getSequenceById(int id) {
        return _sequenceDAO.getById(id);
    }
    
    @Transactional(readOnly=false)
    public void saveSequence(MovieSequence sequence) {
        _sequenceDAO.save(sequence);
    }

}

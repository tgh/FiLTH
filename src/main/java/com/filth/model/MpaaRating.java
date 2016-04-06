package com.filth.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="mpaa")
public class MpaaRating {

    @Id
    @Column(name="mid")
    @SequenceGenerator(name="mpaa_seq", sequenceName="mpaa_mid_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="mpaa_seq")
    private int _id;
    
    @Column(name="rating")
    private String _ratingCode;
    
    public int getId() {
        return _id;
    }

    public void setId(int id) {
        _id = id;
    }
    
    public String getRatingCode() {
        return _ratingCode;
    }
    
    public void setRatingCode(String ratingCode) {
        _ratingCode = ratingCode;
    }
    
    @Override
    public String toString() {
        return "MpaaRating (" + _ratingCode + ")";
    }
    
}

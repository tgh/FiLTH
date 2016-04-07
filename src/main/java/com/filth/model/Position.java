package com.filth.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="position")
public class Position {

    @Id
    @Column(name="pid")
    @SequenceGenerator(name="position_seq", sequenceName="position_pid_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="position_seq")
    private int _id;
    
    @Column(name="position_title")
    private String _title;
    
    public int getId() {
        return _id;
    }

    public void setId(int id) {
        _id = id;
    }
    
    public String getTitle() {
        return _title;
    }
    
    public void setTitle(String ratingCode) {
        _title = ratingCode;
    }
    
    @Override
    public String toString() {
        return "Position (" + _title + ")";
    }
    
}

package com.filth.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="tyler")
public class Tyler {
    
    @Id
    @Column(name="tid")
    @SequenceGenerator(name="tyler_seq", sequenceName="tyler_tid_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="tyler_seq")
    private int _id;
    
    @Column(name="category")
    private String _category;

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        _id = id;
    }

    public String getCategory() {
        return _category;
    }

    public void setCategory(String category) {
        _category = category;
    }
    
    @Override
    public String toString() {
        return "Tyler (" + _id + ", " + _category + ")";
    }
}

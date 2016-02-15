package com.filth.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="oscar")
public class Oscar {
    
    @Id
    @Column(name="oid")
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
        return "Oscar (" + _id + "," + _category + ")";
    }
}

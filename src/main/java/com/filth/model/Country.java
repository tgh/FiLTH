package com.filth.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="country")
public class Country {

    @Id
    @Column(name="cid")
    @SequenceGenerator(name="country_seq", sequenceName="country_cid_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="country_seq")
    private int _id;
    
    @Column(name="country_name")
    private String _name;
    
    public int getId() {
        return _id;
    }

    public void setId(int id) {
        _id = id;
    }
    
    public String getName() {
        return _name;
    }
    
    public void setName(String name) {
        _name = name;
    }
    
    @Override
    public String toString() {
        return "Country (" + _name + ")";
    }
    
}

package com.filth.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="list")
public class List {

    @Id
    @Column(name="lid")
    @SequenceGenerator(name="list_seq", sequenceName="list_lid_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="list_seq")
    private int _id;
    
    @Column(name="list_title")
    private String _title;
    
    @Column(name="list_author")
    private String _author;

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        _id = id;
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        _title = title;
    }
    
    public String getAuthor() {
        return _author;
    }
    
    public void setAuthor(String author) {
        _author = author;
    }
    
    @Override
    public String toString() {
        return "List (" + _id + ", " + _title + ")";
    }
}

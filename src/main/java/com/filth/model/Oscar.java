package com.filth.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="oscar")
public class Oscar {
    
    @Id
    @Column(name="oid")
    @SequenceGenerator(name="oscar_seq", sequenceName="oscar_oid_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="oscar_seq")
    private int _id;
    
    @Column(name="category")
    private String _category;
    
    @OneToMany(cascade=CascadeType.ALL, mappedBy="_oscar")
    private Set<MovieOscar> _movieOscars;

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
    
    public Set<MovieOscar> getMovieOscars() {
        return _movieOscars;
    }
    
    public void setMovieOscars(Set<MovieOscar> movieOscars) {
        _movieOscars = movieOscars;
    }
    
    @Override
    public String toString() {
        return "Oscar (" + _id + ", " + _category + ")";
    }
}

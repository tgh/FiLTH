package com.filth.model;

import java.util.HashSet;
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
@Table(name="list")
public class List {

    @Id
    @Column(name="lid")
    @SequenceGenerator(name="list_seq", sequenceName="list_lid_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="list_seq")
    private Integer _id;
    
    @Column(name="list_title")
    private String _title;
    
    @Column(name="list_author")
    private String _author;
    
    @OneToMany(cascade=CascadeType.ALL, mappedBy="_list")
    private Set<ListMovie> _listMovies;

    public Integer getId() {
        return _id;
    }

    public void setId(Integer id) {
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
    
    public Set<ListMovie> getListMovies() {
        return _listMovies;
    }
    
    public void setListMovies(Set<ListMovie> listMovies) {
        _listMovies = listMovies;
    }
    
    public void addListMovie(ListMovie listMovie) {
        if (null == _listMovies) {
            _listMovies = new HashSet<>();
        }
        _listMovies.add(listMovie);
    }
    
    /**
     * Copies the contents of the given list into this list.
     * This will write over any existing content of this list.
     */
    public void copyContent(com.filth.model.List otherList) {
        setId(otherList.getId());
        setTitle(otherList.getTitle());
        setAuthor(otherList.getAuthor());
        setListMovies(otherList.getListMovies());
    }
    
    @Override
    public String toString() {
        return "List (" + _id + ", " + _title + ")";
    }
}

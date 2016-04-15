package com.filth.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="tag")
public class Tag {
    
    @Id
    @Column(name="tid")
    @SequenceGenerator(name="tag_seq", sequenceName="tag_tid_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="tag_seq")
    private int _id;
    
    @Column(name="tag_name")
    private String _name;
    
    @ManyToOne()
    @JoinColumn(name="parent_tid")
    private Tag _parent;
    
    @OneToMany(mappedBy="_parent", fetch=FetchType.EAGER)
    @OrderBy("_id")
    private Set<Tag> _children;
    
    @ManyToMany(mappedBy="_tags", fetch=FetchType.EAGER)
    private Set<Movie> _movies;
    

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
    
    public Tag getParent() {
        return _parent;
    }
    
    public void setParent(Tag parent) {
        _parent = parent;
    }
    
    public Set<Tag> getChildren() {
        return _children;
    }
    
    public void setChildren(Set<Tag> children) {
        _children = children;
    }
    
    public Set<Movie> getMovies() {
        return _movies;
    }
    
    public void setMovies(Set<Movie> movies) {
        _movies = movies;
    }
    
    @Override
    public String toString() {
        return "Tag (" + _id + ", " + _name + ")";
    }

}

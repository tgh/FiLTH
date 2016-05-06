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
@Table(name="movie_sequence")
public class MovieSequence {

    @Id
    @Column(name="id")
    @SequenceGenerator(name="movie_sequence_seq", sequenceName="movie_sequence_id_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="movie_sequence_seq")
    private int _id;
    
    @Column(name="sequence_type")
    private String _sequenceType;
    
    @Column(name="name")
    private String _name;
    
    @OneToMany(cascade=CascadeType.ALL, mappedBy="_sequence")
    private Set<MovieSequenceMovie> _movieSequenceMovies;
    
    public int getId() {
        return _id;
    }
    
    public void setId(int id) {
        _id = id;
    }
    
    public String getSequenceType() {
        return _sequenceType;
    }
    
    public void setSequenceType(String sequenceType) {
        _sequenceType = sequenceType;
    }
    
    public String getName() {
        return _name;
    }
    
    public void setName(String name) {
        _name = name;
    }
    
    public Set<MovieSequenceMovie> getMovieSequenceMovies() {
        return _movieSequenceMovies;
    }
    
    public void setMovieSequenceMovies(Set<MovieSequenceMovie> movieSequenceMovies) {
        _movieSequenceMovies = movieSequenceMovies;
    }
    
    @Override
    public String toString() {
        return "MovieSequence (" + _id + ", \"" + _name + "\", " + _sequenceType + ")";
    }
    
}

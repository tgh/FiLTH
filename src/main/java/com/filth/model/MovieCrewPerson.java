package com.filth.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="worked_on")
public class MovieCrewPerson {

    @Id
    @Column(name="wid")
    @SequenceGenerator(name="worked_on_seq", sequenceName="worked_on_wid_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="worked_on_seq")
    private int _id;
    
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name="mid")
    private Movie _movie;
    
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name="cid")
    private CrewPerson _crewPerson;
    
    @Column(name="position")
    private String _position;
    
    public int getId() {
        return _id;
    }
    
    public void setId(int id) {
        _id = id;
    }
    
    public Movie getMovie() {
        return _movie;
    }
    
    public void setMovie(Movie movie) {
        _movie = movie;
    }
    
    public CrewPerson getCrewPerson() {
        return _crewPerson;
    }
    
    public void setCrewPerson(CrewPerson crewPerson) {
        _crewPerson = crewPerson;
    }
    
    public String getPosition() {
        return _position;
    }
    
    public void setPosition(String position) {
        _position = position;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("MovieCrewPerson (");
        sb.append(_id + ", ");
        sb.append(_movie.getTitle());
        sb.append(" (" + _movie.getYear() + "), ");
        sb.append(_crewPerson.getFullName() + ", ");
        sb.append(_position);
        sb.append(")");
        return sb.toString();
    }
    
}

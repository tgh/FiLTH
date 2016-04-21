package com.filth.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="oscar_given_to")
public class MovieOscar {

    @Id
    @Column(name="id")
    @SequenceGenerator(name="oscar_given_to_seq", sequenceName="oscar_given_to_id_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="oscar_given_to_seq")
    private int _id;
    
    @ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name="mid")
    private Movie _movie;
    
    @ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name="oid")
    private Oscar _oscar;
    
    @ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name="cid")
    private CrewPerson _crewPerson;
    
    @Column(name="year")
    private int _year;
    
    @Column(name="status")
    @Enumerated(EnumType.ORDINAL)
    private Status _status;
    
    @Column(name="sharing_with")
    private Integer _sharingWith;
    
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
    
    public Oscar getOscar() {
        return _oscar;
    }
    
    public void setOscar(Oscar oscar) {
        _oscar = oscar;
    }
    
    public CrewPerson getCrewPerson() {
        return _crewPerson;
    }
    
    public void setCrewPerson(CrewPerson crewPerson) {
        _crewPerson = crewPerson;
    }
    
    public int getYear() {
        return _year;
    }
    
    public void setYear(int year) {
        _year = year;
    }
    
    public Status getStatus() {
        return _status;
    }
    
    public void setStatus(Status status) {
        _status = status;
    }
    
    public Integer getSharingWith() {
        return _sharingWith;
    }
    
    public void setSharingWith(Integer sharingWith) {
        _sharingWith = sharingWith;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("MovieOscar (");
        sb.append(_id + ", ");
        sb.append(_movie.getTitle());
        sb.append(" (" + _movie.getYear() + "), ");
        sb.append(_oscar.getCategory());
        sb.append(" (" + _status + "), ");
        sb.append(_year);
        if (_crewPerson.getId() != 0) {
            sb.append(", " + _crewPerson.getFullName());
        }
        sb.append(")");
        return sb.toString();
    }
    
}

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

import org.apache.commons.lang.StringUtils;

@Entity
@Table(name="tyler_given_to")
public class MovieTyler {
    
    @Id
    @Column(name="id")
    @SequenceGenerator(name="tyler_given_to_seq", sequenceName="tyler_given_to_id_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="tyler_given_to_seq")
    private int _id;
    
    @ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name="mid")
    private Movie _movie;
    
    @ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name="tid")
    private Tyler _tyler;
    
    @ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name="cid")
    private CrewPerson _crewPerson;
    
    @Column(name="status")
    @Enumerated(EnumType.ORDINAL)
    private Status _status;
    
    @Column(name="scene_title")
    private String _sceneTitle;
    
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
    
    public Tyler getTyler() {
        return _tyler;
    }
    
    public void setTyler(Tyler tyler) {
        _tyler = tyler;
    }
    
    public CrewPerson getCrewPerson() {
        return _crewPerson;
    }
    
    public void setCrewPerson(CrewPerson crewPerson) {
        _crewPerson = crewPerson;
    }
    
    public Status getStatus() {
        return _status;
    }
    
    public void setStatus(Status status) {
        _status = status;
    }
    
    public String getSceneTitle() {
        return _sceneTitle;
    }
    
    public void setSharingWith(String sceneTitle) {
        _sceneTitle = sceneTitle;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("MovieTyler (");
        sb.append(_id + ", ");
        sb.append(_movie.getTitle());
        sb.append(" (" + _movie.getYear() + "), ");
        sb.append(_tyler.getCategory());
        sb.append(" (" + _status + ")");
        if (_crewPerson.getId() != 0) {
            sb.append(", " + _crewPerson.getFullName());
        }
        if (StringUtils.isNotEmpty(_sceneTitle)) {
            sb.append(", Scene: \"" + _sceneTitle + "\"");
        }
        sb.append(")");
        return sb.toString();
    }

}

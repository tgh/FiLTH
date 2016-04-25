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
@Table(name="movie_link")
public class MovieLink {

    @Id
    @Column(name="id")
    @SequenceGenerator(name="movie_link_seq", sequenceName="movie_link_id_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="movie_link_seq")
    private int _id;
    
    @ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name="base_mid")
    private Movie _baseMovie;
    
    @ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name="linked_mid")
    private Movie _linkedMovie;
    
    @Column(name="link_type")
    @Enumerated(EnumType.STRING)
    private MovieLinkType _linkType;
    
    @Column(name="comments")
    private String _comments;
    
    public int getId() {
        return _id;
    }
    
    public void setId(int id) {
        _id = id;
    }
    
    public Movie getBaseMovie() {
        return _baseMovie;
    }
    
    public void setBaseMovie(Movie movie) {
        _baseMovie = movie;
    }
    
    public Movie getLinkedMovie() {
        return _linkedMovie;
    }
    
    public void setLinkedMovie(Movie movie) {
        _linkedMovie = movie;
    }
    
    public MovieLinkType getLinkType() {
        return _linkType;
    }
    
    public void setLinkType(MovieLinkType linkType) {
        _linkType = linkType;
    }
    
    public String getComments() {
        return _comments;
    }
    
    public void setComments(String comments) {
        _comments = comments;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("MovieLink (");
        sb.append(_id + ", ");
        sb.append("Base: " + _baseMovie.getTitle());
        sb.append(" (" + _baseMovie.getYear() + "), ");
        sb.append("Linked: " + _linkedMovie.getTitle());
        sb.append(" (" + _linkedMovie.getYear() + "), ");
        sb.append(_linkType.name());
        if (StringUtils.isNotEmpty(_comments)) {
            sb.append(", \"" + _comments + "\"");
        }
        sb.append(")");
        return sb.toString();
    }
    
}
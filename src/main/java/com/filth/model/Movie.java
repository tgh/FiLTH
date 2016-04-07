package com.filth.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="movie")
public class Movie {

    @Id
    @Column(name="mid")
    @SequenceGenerator(name="movie_seq", sequenceName="movie_mid_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="movie_seq")
    private int _id;
  
    @Column(name="title")
    private String _title;
  
    @Column(name="year")
    private Integer _year;
  
    @Column(name="star_rating")
    private String _starRating;
  
    @Column(name="mpaa")
    private String _mpaa;
  
    @Column(name="country")
    private String _country;
  
    @Column(name="comments")
    private String _comments;
  
    @Column(name="imdb_id")
    private String _imdbId;
  
    @Column(name="theater_viewings")
    private Integer _theaterViewings;
  
    @Column(name="tmdb_id")
    private Long _tmdbId;

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
  
    public Integer getYear() {
        return _year;
    }
  
    public void setYear(Integer year) {
        _year = year;
    }
  
    public String getStarRating() {
        return _starRating;
    }
  
    public void setStarRating(String starRating) {
        _starRating = starRating;
    }
  
    public String getMpaaRating() {
        return _mpaa;
    }
  
    public void setMpaaRating(String mpaa) {
        _mpaa = mpaa;
    }
  
    public String getCountry() {
        return _country;
    }
  
    public void setCountry(String country) {
        _country = country;
    }
  
    public String getComments() {
        return _comments;
    }
  
    public void setComments(String comments) {
        _comments = comments;
    }
  
    public String getImdbId() {
        return _imdbId;
    }
  
    public void setImdbId(String imdbId) {
        _imdbId = imdbId;
    }
  
    public Integer getTheaterViewings() {
        return _theaterViewings;
    }
    
    public void setTheaterViewings(Integer viewings) {
        _theaterViewings = viewings;
    }
  
    public Long getTmdbId() {
        return _tmdbId;
    }
  
    public void setTmdbId(Long tmdbId) {
        _tmdbId = tmdbId;
    }
    
    public String getStarRatingForDisplay() throws Exception {
        return StarRating.encodeToHTML(_starRating);
    }
  
    @Override
    public String toString() {
        return "Movie (" + _id + ", " + _title + " (" + _year + "))";
    }
}
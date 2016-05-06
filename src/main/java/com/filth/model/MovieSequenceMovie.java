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
@Table(name="movie_sequence_movie")
public class MovieSequenceMovie {
    
    @Id
    @Column(name="id")
    @SequenceGenerator(name="movie_sequence_movie_seq", sequenceName="movie_sequence_movie_id_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="movie_sequence_movie_seq")
    private int _id;
    
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name="mid")
    private Movie _movie;
    
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name="sid")
    private MovieSequence _sequence;
    
    @Column(name="sequence_order")
    private int _orderIndex;
    
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
    
    public MovieSequence getSequence() {
        return _sequence;
    }
    
    public void setSequence(MovieSequence sequence) {
        _sequence = sequence;
    }
    
    public int getOrderIndex() {
        return _orderIndex;
    }
    
    public void setOrderIndex(int orderIndex) {
        _orderIndex = orderIndex;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("MovieSequenceMovie (");
        sb.append(_id + ", ");
        sb.append(_movie.getTitle());
        sb.append(" (" + _movie.getYear() + "), ");
        sb.append("Sequence: \"" + _sequence.getName() + "\" (" + _sequence.getSequenceType() + "), ");
        sb.append("Order index: " + _orderIndex);
        sb.append(")");
        return sb.toString();
    }

}

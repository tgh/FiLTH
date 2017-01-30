package com.filth.model;

import java.util.Objects;

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
@Table(name="list_contains")
public class ListMovie {

    @Id
    @Column(name="id")
    @SequenceGenerator(name="list_cotnains_seq", sequenceName="list_contains_id_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="list_cotnains_seq")
    private Integer _id;
    
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name="mid")
    private Movie _movie;
    
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name="lid")
    private List _list;
    
    @Column(name="rank")
    private Integer _rank;
    
    @Column(name="comments")
    private String _comments;
    
    public Integer getId() {
        return _id;
    }
    
    public void setId(Integer id) {
        _id = id;
    }
    
    public Movie getMovie() {
        return _movie;
    }
    
    public void setMovie(Movie movie) {
        _movie = movie;
    }
    
    public List getList() {
        return _list;
    }
    
    public void setList(List list) {
        _list = list;
    }
    
    public Integer getRank() {
        return _rank;
    }
    
    public void setRank(Integer rank) {
        _rank = rank;
    }
    
    public String getComments() {
        return _comments;
    }
    
    public void setComments(String comments) {
        _comments = comments;
    }
    
    /**
     * Copies the contents (rank and comments only) of the given ListMovie to this ListMovie
     * (only if they differ).
     */
    public void copyContent(ListMovie otherListMovie) {
        if (! Objects.equals(this.getComments(), otherListMovie.getComments())) {
            setComments(otherListMovie.getComments());
        }
        
        if (! Objects.equals(this.getRank(), otherListMovie.getRank())) {
            setRank(otherListMovie.getRank());
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ListMovie (");
        sb.append(_id + ", ");
        sb.append(_movie.getTitle());
        sb.append(" (" + _movie.getYear() + "), ");
        sb.append("\"" + _list.getTitle() + "\"");
        if (null != _rank) {
            sb.append(", Rank: " + _rank);
        }
        sb.append(")");
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object listMovie) {
        boolean equals = false;
        
        if (listMovie instanceof ListMovie) {
            //if one of the ListMovie objects hasn't been persisted yet (no id),
            //then use the movie and list ids
            if (getId() == null || ((ListMovie)listMovie).getId() == null) {
                equals =  getMovie().getId() == ((ListMovie)listMovie).getMovie().getId()
                       && getList().getId() == ((ListMovie)listMovie).getList().getId();
            } else {
                equals = getId().equals(((ListMovie)listMovie).getId());
            }
        }
        
        return equals;
    }
    
}

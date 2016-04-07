package com.filth.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="star_rating")
public class StarRating {
    
    private static final String STAR_HTML_CODE = "&starf;";
    private static final String HALF_HTML_CODE = "&frac12;";

    @Id
    @Column(name="sid")
    @SequenceGenerator(name="star_rating_seq", sequenceName="star_rating_sid_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="star_rating_seq")
    private int _id;
    
    @Column(name="rating")
    private String _rating;
    
    public int getId() {
        return _id;
    }

    public void setId(int id) {
        _id = id;
    }
    
    public String getRating() {
        return _rating;
    }
    
    public void setRating(String rating) {
        _rating = rating;
    }
    
    public String getStarRatingForDisplay() throws Exception {
        return encodeToHTML(_rating);
    }
    
    public static String encodeToHTML(String rating) throws Exception {
        if (false == rating.contains("*")) {
            return rating;
        }
        
        StringBuilder starRatingBuilder = new StringBuilder();
        char[] chars = rating.toCharArray();
        for (char c : chars) {
            if (c == '*') {
                starRatingBuilder.append(STAR_HTML_CODE);
            } else if (c == '½') {
                starRatingBuilder.append(HALF_HTML_CODE);
            } else {
                throw new Exception("Unknown character in star rating '" + c + "'");
            }
        }
        
        return starRatingBuilder.toString();
    }
    
    @Override
    public String toString() {
        return "StarRating (" + _rating + ")";
    }
    
}

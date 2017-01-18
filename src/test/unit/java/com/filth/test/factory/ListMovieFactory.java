package com.filth.test.factory;

import com.filth.model.ListMovie;


/**
 * Factory class for creating {@link ListMovie} objects for use in tests.
 */
public class ListMovieFactory {
    
    //public for tests to reference
    public static final int SIMPLE_ID = 1;
    public static final int SIMPLE_RANK = 1;
    public static final String SIMPLE_COMMENTS = "Comments.";
    
    /**
     * Creates a ListMovie object with simple values with no List
     * nor Movies.
     */
    public static ListMovie createSimple() {
        ListMovie listMovie = new ListMovie();
        
        listMovie.setId(SIMPLE_ID);
        listMovie.setRank(SIMPLE_RANK);
        listMovie.setComments(SIMPLE_COMMENTS);
        
        return listMovie;
    }
    
    /**
     * Creates a ListMovie object that contains a random movie without a ranking.
     */
    public static ListMovie createWithRandomMovieNotRanked() {
        ListMovie listMovie = new ListMovie();
        
        listMovie.setId(SIMPLE_ID);
        listMovie.setMovie(MovieFactory.createRandom());
        
        return listMovie;
    }
    
    /**
     * Creates a ListMovie object that contains a random movie with the
     * given ranking.
     */
    public static ListMovie createWithRandomMovieRanked(int rank) {
        ListMovie listMovie = new ListMovie();
        
        listMovie.setId(SIMPLE_ID);
        listMovie.setRank(rank);
        listMovie.setMovie(MovieFactory.createRandom());
        
        return listMovie;
    }

}

package com.filth.test.factory;

import com.filth.model.ListMovie;

/**
 * Factory class for creating {@link com.filth.model.List} objects for use
 * in tests.
 */
public class ListFactory {

    public static final int SIMPLE_ID = 1;
    public static final String SIMPLE_TITLE = "List Title";
    public static final String SIMPLE_AUTHOR = "Me";
    
    /**
     * Creates a List with simple values and no movies.
     */
    public static com.filth.model.List createSimple() {
        com.filth.model.List list = new com.filth.model.List();
        
        list.setId(SIMPLE_ID);
        list.setTitle(SIMPLE_TITLE);
        list.setAuthor(SIMPLE_AUTHOR);
        
        return list;
    }
    

    /**
     * Creates a List of random movies, but no rankings on the movies.
     * 
     * @param numMovies The number of movies desired in the list
     */
    public static com.filth.model.List createWithRandomMoviesNoRankings(int numMovies) {
        com.filth.model.List list = createSimple();
        
        for (int i=0; i < numMovies; ++i) {
            ListMovie listMovie = ListMovieFactory.createWithRandomMovieNotRanked();
            list.addListMovie(listMovie);
        }
        
        return list;
    }
}

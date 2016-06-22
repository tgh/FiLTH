package com.filth.test.factory;

import java.util.Random;

import com.filth.model.Movie;
import com.filth.test.util.RandomMovieTitleGenerator;

/**
 * Factory class for creating {@link Movie} objects for use in tests.
 */
public class MovieFactory {

    private static final int SIMPLE_ID = 1;
    private static final String SIMPLE_TITLE = "Movie Title";
    private static final int SIMPLE_YEAR = 2016;
    private static final String SIMPLE_MPAA = "PG";
    private static final String SIMPLE_STAR_RATING = "****";
    private static final String SIMPLE_COUNTRY = "USA";
    private static final String SIMPLE_COMMENTS = "Comments.";
    private static final String SIMPLE_IMDB_ID = "tt000001";
    private static final long SIMPLE_TMDB_ID = 1;
    private static final int SIMPLE_THEATER_VIEWINGS = 0;
    
    /* Value sets for randomness--values may be repeated to increase probability of selection */
    
    private static final String[] MPAAS = { "NR", "G", "PG", "PG-13", "R" };
    private static final String[] STARS = { "*", "**", "****", "****" };
    private static final String[] COUNTRIES = { "USA", "USA", "USA", "USA", "France",
        "Italy", "Japan", "England", "Germany", "USA", "USA"
    };
    private static final int MIN_RANDOM_YEAR = 1925;
    private static final int MAX_RANDOM_YEAR = 2016;
    private static final int MAX_MOVIE_ID = 5000;
    private static final int MAX_IMDB_ID = 100000;
    private static final int MAX_TMDB_ID = 10000;
    
    /**
     * Creates a Movie object with simple values and no joint models
     * (tags, parent, children, remakeOf, remadeBy, etc).
     */
    public static Movie createSimple() {
        Movie movie = new Movie();
        
        movie.setId(SIMPLE_ID);
        movie.setTitle(SIMPLE_TITLE);
        movie.setYear(SIMPLE_YEAR);
        movie.setMpaaRating(SIMPLE_MPAA);
        movie.setStarRating(SIMPLE_STAR_RATING);
        movie.setCountry(SIMPLE_COUNTRY);
        movie.setComments(SIMPLE_COMMENTS);
        movie.setImdbId(SIMPLE_IMDB_ID);
        movie.setTmdbId(SIMPLE_TMDB_ID);
        movie.setTheaterViewings(SIMPLE_THEATER_VIEWINGS);
        
        return movie;
    }
    
    /**
     * Create a basic Movie object (no joint models) with randomized
     * values (except comments--no comments). Note the movie has been
     * seen, however (has a star rating, and therefore theater viewings
     * value is not null).
     */
    public static Movie createRandom() {
        Movie movie = new Movie();

        movie.setId(getRandomId());
        movie.setTitle(RandomMovieTitleGenerator.generateTitle());
        movie.setYear(getRandomYear());
        movie.setMpaaRating(getRandomMpaaRating());
        movie.setStarRating(getRandomStarRating());
        movie.setCountry(getRandomCountry());
        movie.setImdbId(getRandomImdbId());
        movie.setTmdbId(getRandomTmdbId());
        movie.setTheaterViewings(getRandomTheaterViewings());
        
        return movie;
    }
    
    private static int getRandomId() {
        Random random = new Random();
        return random.nextInt(MAX_MOVIE_ID);
    }
    
    private static int getRandomYear() {
        Random random = new Random();
        //add 1 to make the max value inclusive
        return random.nextInt((MAX_RANDOM_YEAR - MIN_RANDOM_YEAR) + 1) + MIN_RANDOM_YEAR;
    }
    
    private static String getRandomMpaaRating() {
        Random random = new Random();
        int idx = random.nextInt(MPAAS.length);
        return MPAAS[idx];
    }
    
    private static String getRandomStarRating() {
        Random random = new Random();
        int idx = random.nextInt(STARS.length);
        return STARS[idx];
    }
    
    private static String getRandomCountry() {
        Random random = new Random();
        int idx = random.nextInt(COUNTRIES.length);
        return COUNTRIES[idx];
    }
    
    private static String getRandomImdbId() {
        Random random = new Random();
        int id = random.nextInt(MAX_IMDB_ID);
        return "tt" + String.valueOf(id);
    }
    
    private static long getRandomTmdbId() {
        Random random = new Random();
        return random.nextInt(MAX_TMDB_ID);
    }
    
    private static int getRandomTheaterViewings() {
        Random random = new Random();
        //meh, just 0 or 1
        return random.nextInt(2);
    }
}

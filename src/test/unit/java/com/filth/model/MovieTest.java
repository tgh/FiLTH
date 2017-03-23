package com.filth.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit tests for the Movie model object.
 */
public class MovieTest {
	
	@Test
	public void getColorStringForStarRating_expectedResults() {
		Movie movie = new Movie();
		movie.setStarRating("****");
		assertEquals("green", movie.getColorStringForStarRating());
		movie.setStarRating("***½");
		assertEquals("blue", movie.getColorStringForStarRating());
		movie.setStarRating("***");
		assertEquals("blue", movie.getColorStringForStarRating());
		movie.setStarRating("**½");
		assertEquals("purple", movie.getColorStringForStarRating());
		movie.setStarRating("**");
		assertEquals("purple", movie.getColorStringForStarRating());
		movie.setStarRating("*½");
		assertEquals("orange", movie.getColorStringForStarRating());
		movie.setStarRating("*");
		assertEquals("orange", movie.getColorStringForStarRating());
		movie.setStarRating("½*");
		assertEquals("red", movie.getColorStringForStarRating());
		movie.setStarRating("NO STARS");
		assertEquals("red", movie.getColorStringForStarRating());
		movie.setStarRating("not seen");
		assertEquals("defaultColor", movie.getColorStringForStarRating());
		movie.setStarRating("N/A");
		assertEquals("defaultColor", movie.getColorStringForStarRating());
		movie.setStarRating(null);
		assertEquals("defaultColor", movie.getColorStringForStarRating());
	}

}

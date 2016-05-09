package com.filth.util;

import java.util.Comparator;

import com.filth.model.MovieSequenceMovie;

public class MovieSequenceMovieOrderIndexComparator implements Comparator<MovieSequenceMovie> {

    @Override
    public int compare(MovieSequenceMovie msm1, MovieSequenceMovie msm2) {
        return Integer.compare(msm1.getOrderIndex(), msm2.getOrderIndex());
    }
    
}

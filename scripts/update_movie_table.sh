#!/bin/bash

##
# This script updates only the movie table of the database.
#

filth_path=~/workspace/FiLTH
filth_temp_path=~/workspace/FiLTH/temp

# remove the previous_movie_ratings.txt file that is created and checked for by movie.sh
echo "Removing previous_movie_ratings.txt (if exists)..."
sleep 0.5
if [ -f $filth_temp_path/previous_movie_ratings.txt ]
then
  rm $filth_temp_path/previous_movie_ratings.txt
fi

# create the /sql/movie.sql file based on /data/Movie_Ratings.doc
echo "Running movie.sh script..."
$filth_path/scripts/movie.sh

# remove all rows from the movie table in the database
echo "Removing all rows in movie table..."
sleep 0.5
psql -U postgres -d filth -c "DELETE FROM movie;" > /dev/null 2>$filth_temp_path/update_movie_error.txt

# reset the movie id primary key sequence
echo "Resetting the mid sequence for the movie table..."
sleep 0.5
psql -U postgres -d filth -c "ALTER SEQUENCE movie_mid_seq RESTART WITH 1;" > /dev/null 2>>$filth_temp_path/update_movie_error.txt

# execute SQL statements in movie.sql
echo "Inserting all movies in movie.sql..."
psql -U postgres -d filth -f $filth_path/sql/movie.sql > /dev/null 2>>$filth_temp_path/update_movie_error.txt

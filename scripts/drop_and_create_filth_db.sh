#!/bin/bash

filth_path=~/workspace/FiLTH
filth_sql_path=$filth_path/sql
filth_temp_path=$filth_path/temp
error_file=$filth_temp_path/drop_filth_db_error.txt


function populate_db_table {
  echo "Populating $1..."
  sleep 0.5
  psql -U postgres -d filth -f $filth_sql_path/$1.sql > /dev/null 2>>$error_file
}


#------------------------------------------------------------------------------


echo "Dropping filth database..."
sleep 0.5
psql -U postgres -c "DROP DATABASE filth;" > /dev/null 2>$error_file


echo "Creating database filth..."
sleep 0.5
createdb -U postgres -O postgres filth > /dev/null 2>>$error_file


echo "Creating database schema..."
sleep 0.5
psql -U postgres -d filth -f $filth_sql_path/init_pg_database.sql > /dev/null 2>>$error_file


#populate entity tables
populate_db_table "country"
populate_db_table "oscar"
populate_db_table "tyler"
populate_db_table "mpaa"
populate_db_table "star_rating"
populate_db_table "position"
populate_db_table "crew_person"
populate_db_table "tag"
populate_db_table "movie"

#populate relationship tables
populate_db_table "oscar_given_to"
populate_db_table "tag_given_to"
populate_db_table "worked_on"


echo "Creating temp/previous_movie_ratings.txt..."
sleep 0.5
antiword -w 120 $filth_path/data/Movie_Ratings.doc > $filth_temp_path/previous_movie_ratings.txt

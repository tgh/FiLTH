#!/bin/bash

# This script assumes:
#  - a postgresql user "filth_admin" has been created (see the "SETTING UP ENVIRONMENT"
#    section in /doc/NOTES about setting up the user

source common.sh

ERROR_FILE=$FILTH_TEMP_PATH/drop_filth_db_error.txt


function populate_db_table {
  echo "Populating $1..."
  sleep 0.5
  psql -U filth_admin -d filth -f $FILTH_SQL_PATH/$1.sql > /dev/null 2>>$ERROR_FILE
}


#------------------------------------------------------------------------------


echo "Creating database and schema..."
sleep 0.5
psql -U filth_admin -d filth -f $FILTH_SQL_PATH/init_pg_database.sql > /dev/null 2>>$ERROR_FILE


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
populate_db_table "tyler_given_to"


echo "Creating temp/previous_movie_ratings.txt..."
sleep 0.5
antiword -w 120 $FILTH_PATH/data/Movie_Ratings.doc > $FILTH_TEMP_PATH/previous_movie_ratings.txt

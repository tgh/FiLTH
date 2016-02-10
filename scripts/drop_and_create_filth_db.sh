#!/bin/bash

# This script assumes:
#  - a postgresql user "filth_admin" has been created (see the "SETTING UP ENVIRONMENT"
#    section in /doc/NOTES about setting up the user

source common.sh

LOG_FILE=$FILTH_PATH/logs/drop_and_create_filth_db.log


function populate_db_table {
  echo "Populating $1..."
  sleep 0.5
  psql -U filth_admin -d filth -f $FILTH_SQL_PATH/$1.sql > /dev/null 2>>$LOG_FILE
}

function clear_db_table {
  echo "Clearing $1..."
  sleep 0.5
  psql -U filth_admin -d filth -c "DELETE FROM filth.$1;" >>$LOG_FILE 2>>$LOG_FILE
}


#------------------------------------------------------------------------------

echo "Clearing log: $LOG_FILE ..."
sleep 0.5
> $LOG_FILE

if [ $# -gt 0 ]
  #clear and populate specific tables
  then
    for i in $@
    do
      clear_db_table $1
      populate_db_table $1
    done
  #drop and create the entire database
  else
    echo "Dropping existing filth db..."
    sleep 0.5
    sudo -u postgres dropdb --if-exists filth

    echo "Creating database filth..."
    sleep 0.5
    sudo -u postgres createdb --owner=filth_admin filth

    echo "Creating schema..."
    sleep 0.5
    psql -U filth_admin -d filth -f $FILTH_SQL_PATH/init_pg_database.sql > /dev/null 2>>$LOG_FILE


    #populate entity tables
    populate_db_table "movie_link_type"
    populate_db_table "country"
    populate_db_table "oscar"
    populate_db_table "tyler"
    populate_db_table "mpaa"
    populate_db_table "star_rating"
    populate_db_table "position"
    populate_db_table "crew_person"
    populate_db_table "tag"
    populate_db_table "movie"
    populate_db_table "list"

    #populate relationship tables
    populate_db_table "movie_link"
    populate_db_table "oscar_given_to"
    populate_db_table "tag_given_to"
    populate_db_table "worked_on"
    populate_db_table "tyler_given_to"
    populate_db_table "list_contains"


    echo "Creating temp/previous_movie_ratings.txt..."
    sleep 0.5
    antiword -w 120 $FILTH_PATH/data/Movie_Ratings.doc > $FILTH_TEMP_PATH/previous_movie_ratings.txt
fi

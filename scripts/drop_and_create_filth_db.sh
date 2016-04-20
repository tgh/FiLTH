#!/bin/bash

# This script assumes:
#  - a postgresql user "filth_admin" has been created (see the "SETTING UP ENVIRONMENT"
#    section in /doc/NOTES about setting up the user

source common.sh

LOG_FILE=$FILTH_PATH/logs/drop_and_create_filth_db.log


function getLastFileId {
  file=$1.sql
  lastFileId=`tac $FILTH_SQL_PATH/$file | egrep -m 1 . | sed -E "s/.*\(([0-9]+),.*/\\1/"`
}

function update_sequence {
  echo "Updating id sequence for $1..."
  sleep 0.5
  getLastFileId $1
  table=$1

  if [ "$1" == "oscar_given_to" -o "$1" == "tyler_given_to" -o "$1" == "list_contains" -o "$1" == "movie_link" ]
  then
    tableId="id"
  else
    tableId="$(echo $table | head -c 1)id" # take the first letter of the table name and append "id"
  fi

  sequenceValue=$lastFileId
  sequence=${table}_${tableId}_seq
  psql -U filth_admin -d filth -c "SELECT setval('filth.$sequence', $sequenceValue);" > /dev/null 2>>$LOG_FILE
}

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


    #populate integrity tables
    populate_db_table "country"
    populate_db_table "movie_link_type"
    populate_db_table "mpaa"
    populate_db_table "position"
    populate_db_table "star_rating"

    #populate entity tables
    populate_db_table "crew_person"
    populate_db_table "list"
    populate_db_table "movie"
    populate_db_table "oscar"
    populate_db_table "tag"
    populate_db_table "tyler"

    #populate relationship tables
    populate_db_table "list_contains"
    populate_db_table "movie_link"
    populate_db_table "oscar_given_to"
    populate_db_table "tag_given_to"
    populate_db_table "tyler_given_to"
    populate_db_table "worked_on"

    #update sequences
    update_sequence "crew_person"
    update_sequence "list"
    update_sequence "movie"
    update_sequence "oscar"
    update_sequence "tag"
    update_sequence "tyler"
    update_sequence "mpaa"
    update_sequence "counry"
    update_sequence "position"
    update_sequence "star_rating"
    update_sequence "worked_on"
    update_sequence "oscar_given_to"
    update_sequence "tyler_given_to"
    update_sequence "list_contains"
    update_sequence "movie_link"

    echo "Creating temp/previous_movie_ratings.txt..."
    sleep 0.5
    antiword -w 120 $FILTH_PATH/data/Movie_Ratings.doc > $FILTH_TEMP_PATH/previous_movie_ratings.txt
fi

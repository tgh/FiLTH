#!/bin/bash

# This script assumes:
#  - a postgresql user "filth_admin" has been created (see the "SETTING UP ENVIRONMENT"
#    section in /doc/NOTES about setting up the user
#  - the OS user "postgres" is running the script
#  - the following files have write access for postgres user
#    - sql/crew_person_additions.sql
#    - sql/movie_additions.sql
#    - sql/tag_additions.sql
#    - sql/tag_given_to_additions.sql
#    - sql/worked_on_additions.sql
#    - temp/drop_filth_db_error.txt
#    - temp/previous_movie_ratings.txt
#  - the FILTH_HOME environment variable may also need to be set for user postgres

source common.sh

ERROR_FILE=$FILTH_TEMP_PATH/drop_filth_db_error.txt


function populate_db_table {
  echo "Populating $1..."
  sleep 0.5
  psql -U filth_admin -d filth -f $FILTH_SQL_PATH/$1.sql > /dev/null 2>>$ERROR_FILE
}


#------------------------------------------------------------------------------


echo "Dropping filth database..."
sleep 0.5
psql -U filth_admin -c "DROP DATABASE filth;" > /dev/null 2>$ERROR_FILE


echo "Creating database filth..."
sleep 0.5
createdb -U filth_admin -O filth_admin filth > /dev/null 2>>$ERROR_FILE


echo "Creating database schema..."
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

echo "Clearing *additions.sql files..."
> $FILTH_SQL_PATH/crew_person_additions.sql
> $FILTH_SQL_PATH/movie_additions.sql
> $FILTH_SQL_PATH/tag_additions.sql
> $FILTH_SQL_PATH/tag_given_to_additions.sql
> $FILTH_SQL_PATH/worked_on_additions.sql
sleep 0.5
echo "Creating temp/previous_movie_ratings.txt..."
sleep 0.5
antiword -w 120 $FILTH_PATH/data/Movie_Ratings.doc > $FILTH_TEMP_PATH/previous_movie_ratings.txt

#!/bin/bash

filth_sql_path=~/Projects/FiLTH/sql
filth_temp_path=~/Projects/FiLTH/temp

echo "Dropping test database..."
sleep 0.5
psql -U postgres -c "DROP DATABASE test;" > /dev/null 2>$filth_temp_path/drop_test_db_error.txt

echo "Creating database test..."
sleep 0.5
createdb -U postgres -O postgres test > /dev/null 2>>$filth_temp_path/drop_test_db_error.txt

echo "Creating database schema..."
sleep 0.5
psql -U postgres -d test -f $filth_sql_path/init_pg_database.sql > /dev/null 2>>$filth_temp_path/drop_test_db_error.txt

echo "Populating genre table..."
sleep 0.5
psql -U postgres -d test -f $filth_sql_path/genre.sql > /dev/null 2>>$filth_temp_path/drop_test_db_error.txt

echo "Populating oscar table..."
sleep 0.5
psql -U postgres -d test -f $filth_sql_path/oscar.sql > /dev/null 2>>$filth_temp_path/drop_test_db_error.txt

echo "Populating tyler table..."
sleep 0.5
psql -U postgres -d test -f $filth_sql_path/tyler.sql > /dev/null 2>>$filth_temp_path/drop_test_db_error.txt

echo "Populating crewperson table..."
sleep 0.5
psql -U postgres -d test -f $filth_sql_path/crew_person.sql > /dev/null 2>>$filth_temp_path/drop_test_db_error.txt

echo "Populating movie table..."
sleep 0.5
psql -U postgres -d test -f $filth_sql_path/movie.sql > /dev/null 2>>$filth_temp_path/drop_test_db_error.txt

echo "Creating temp/previous_movie_ratings.txt..."
sleep 0.5
antiword -w 120 $filth_path/data/Movie_Ratings.doc > $filth_temp_path/previous_movie_ratings.txt

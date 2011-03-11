#!/bin/bash

filth_sql_path=~/Projects/FiLTH/sql
filth_temp_path=~/Projects/FiLTH/temp

psql -U postgres -c "DROP DATABASE test;" > $filth_temp_path/drop_test_db_output.txt
createdb -U postgres -O postgres test >> $filth_temp_path/drop_test_db_output.txt
psql -U postgres -d test -f $filth_sql_path/init_pg_database.sql >> $filth_temp_path/drop_test_db_output.txt
psql -U postgres -d test -f $filth_sql_path/genre.sql >> $filth_temp_path/drop_test_db_output.txt
psql -U postgres -d test -f $filth_sql_path/oscar.sql >> $filth_temp_path/drop_test_db_output.txt
psql -U postgres -d test -f $filth_sql_path/tyler.sql >> $filth_temp_path/drop_test_db_output.txt
psql -U postgres -d test -f $filth_sql_path/crewperson.sql >> $filth_temp_path/drop_test_db_output.txt
psql -U postgres -d test -f $filth_sql_path/movie.sql >> $filth_temp_path/drop_test_db_output.txt

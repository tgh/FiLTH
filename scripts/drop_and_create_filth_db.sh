#!/bin/bash

filth_sql_path=~/Projects/FiLTH/sql
filth_temp_path=~/Projects/FiLTH/temp

psql -U postgres -c "DROP DATABASE filth;" > $filth_temp_path/drop_filth_db_output.txt
createdb -U postgres -O postgres filth >> $filth_temp_path/drop_filth_db_output.txt
psql -U postgres -d filth -f $filth_sql_path/init_pg_database.sql >> $filth_temp_path/drop_filth_db_output.txt
psql -U postgres -d filth -f $filth_sql_path/genre.sql >> $filth_temp_path/drop_filth_db_output.txt
psql -U postgres -d filth -f $filth_sql_path/oscar.sql >> $filth_temp_path/drop_filth_db_output.txt
psql -U postgres -d filth -f $filth_sql_path/tyler.sql >> $filth_temp_path/drop_filth_db_output.txt
psql -U postgres -d filth -f $filth_sql_path/crewperson.sql >> $filth_temp_path/drop_filth_db_output.txt
psql -U postgres -d filth -f $filth_sql_path/movie.sql >> $filth_temp_path/drop_filth_db_output.txt

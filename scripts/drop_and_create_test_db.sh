#!/bin/bash

filth_sql_path=~/Projects/FiLTH/sql

psql -U postgres -c "DROP DATABASE test;"
createdb -U postgres -O postgres test
psql -U postgres -d test -f $filth_sql_path/init_pg_database.sql
psql -U postgres -d test -f $filth_sql_path/genre.sql
psql -U postgres -d test -f $filth_sql_path/oscar.sql
psql -U postgres -d test -f $filth_sql_path/tyler.sql
psql -U postgres -d test -f $filth_sql_path/crewperson.sql
psql -U postgres -d test -f $filth_sql_path/movie.sql

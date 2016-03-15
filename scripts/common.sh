#!/bin/bash

TRUE=1
FALSE=0
SUCCESS=0
ERROR=1

if [ -z "$FILTH_PATH" ]
then
    FILTH_PATH=/home/tgh/workspace/FiLTH
fi

FILTH_TEMP_PATH=$FILTH_PATH/temp
FILTH_BACKUP_PATH=$FILTH_TEMP_PATH/backups
FILTH_SQL_PATH=$FILTH_PATH/sql
FILTH_SQL_TEST_PATH=$FILTH_SQL_PATH/test
FILTH_SCRIPTS_PATH=$FILTH_PATH/scripts

movie_sql_file=$FILTH_SQL_PATH/movie.sql
tag_sql_file=$FILTH_SQL_PATH/tag.sql
tgt_sql_file=$FILTH_SQL_PATH/tag_given_to.sql
cp_sql_file=$FILTH_SQL_PATH/crew_person.sql
wo_sql_file=$FILTH_SQL_PATH/worked_on.sql

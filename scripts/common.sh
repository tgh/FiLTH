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
FILTH_SCRIPTS_PATH=$FILTH_PATH/scripts

movie_sql_file=$FILTH_SQL_PATH/movie.sql
tag_sql_file=$FILTH_SQL_PATH/tag.sql
tgt_sql_file=$FILTH_SQL_PATH/tag_given_to.sql
cp_sql_file=$FILTH_SQL_PATH/crew_person.sql
wo_sql_file=$FILTH_SQL_PATH/worked_on.sql

movie_additions_sql_file=$FILTH_SQL_PATH/movie_additions.sql
tag_additions_sql_file=$FILTH_SQL_PATH/tag_additions.sql
tgt_additions_sql_file=$FILTH_SQL_PATH/tag_given_to_additions.sql
cp_additions_sql_file=$FILTH_SQL_PATH/crew_person_additions.sql
wo_additions_sql_file=$FILTH_SQL_PATH/worked_on_additions.sql

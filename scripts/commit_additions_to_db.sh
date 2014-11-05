#!/bin/bash

source common.sh

previous_error_file_size=0
error_file=$FILTH_TEMP_PATH/commit_additions_to_db.sh.error


#== FUNCTIONS =================================================================

function run_sql_inserts() {
  # redirects any psql errors to the error file (because apparently psql always returns 0?)

  echo -e "\n[exec] psql -- Inserting movie additions"
  psql -U postgres -d filth -f $movie_additions_sql_file > /dev/null 2>>$error_file
  check_psql_error $movie_additions_sql_file
  status=$?
  echo -e "\n[exec] psql -- Inserting tag additions"
  psql -U postgres -d filth -f $tag_additions_sql_file > /dev/null 2>>$error_file
  check_psql_error $tag_additions_sql_file
  status=$((status + $?))
  echo -e "\n[exec] psql -- Inserting tag_given_to additions"
  psql -U postgres -d filth -f $tgt_additions_sql_file > /dev/null 2>>$error_file
  check_psql_error $tgt_additions_sql_file
  status=$((status + $?))
  echo -e "\n[exec] psql -- Inserting crew_person additions"
  psql -U postgres -d filth -f $cp_additions_sql_file > /dev/null 2>>$error_file
  check_psql_error $cp_additions_sql_file
  status=$((status + $?))
  echo -e "\n[exec] psql -- Inserting worked_on additions"
  psql -U postgres -d filth -f $wo_additions_sql_file > /dev/null 2>>$error_file
  check_psql_error $wo_additions_sql_file
  status=$((status + $?))

  if [ $status -ne $SUCCESS ]
  then
    echo -e "\n**There was an error running one or more of the *_additions.sql files."
    echo -e "  Check $error_file and the *_additions.sql files in sql/ that caused the error(s)."
    exit
  fi
}


#------------------------------------------------------------------------------

function check_psql_error() {
  # see if the error file has grown since the last psql execution (to see if any error occurred)
  current_error_file_size=`stat -c %s $error_file`
  if [ $current_error_file_size -gt $previous_error_file_size ]
  then
    echo -e "\n[exec] psql -- ERROR running $1"
    previous_error_file_size=$current_error_file_size
    return $ERROR
  fi
  # truncate the sql file
  > $1
  return $SUCCESS
}


#== SCRIPT ====================================================================

# truncate or create the error file
> $error_file

run_sql_inserts

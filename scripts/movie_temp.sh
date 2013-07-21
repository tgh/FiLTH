#!/bin/bash

TRUE=1
FALSE=0
SUCCESS=0
ERROR=1

FILTH_PATH=~/workspace/FiLTH
FILTH_TEMP_PATH=$FILTH_PATH/temp
FILTH_BACKUP_PATH=$FILTH_TEMP_PATH/backups
FILTH_SQL_PATH=$FILTH_PATH/sql
FILTH_SCRIPTS_PATH=$FILTH_PATH/scripts

previous_ratings_file=$FILTH_TEMP_PATH/previous_movie_ratings.txt
previous_ratings_backup=$FILTH_BACKUP_PATH/previous_movie_ratings.txt.backup
temp_file=$FILTH_TEMP_PATH/temp
movie_sql_file=$FILTH_SQL_PATH/movie.sql
movie_sql_backup=$FILTH_BACKUP_PATH/movie.sql.backup
movie_additions_sql_file=$FILTH_TEMP_PATH/movie_additions.sql
tag_sql_file=$FILTH_SQL_PATH/tag.sql
tag_additions_sql_file=$FILTH_TEMP_PATH/tag_additions.sql
tgt_sql_file=$FILTH_SQL_PATH/tag_given_to.sql
tgt_additions_sql_file=$FILTH_TEMP_PATH/tag_given_to_additions.sql
cp_sql_file=$FILTH_SQL_PATH/crew_person.sql
cp_additions_sql_file=$FILTH_TEMP_PATH/crew_person_additions.sql
wo_sql_file=$FILTH_SQL_PATH/worked_on.sql
wo_additions_sql_file=$FILTH_TEMP_PATH/worked_on_additions.sql

error_file=$FILTH_TEMP_PATH/movie.sh.error
previous_error_file_size=0

first_run=$FALSE


#== FUNCTIONS =================================================================

function backup_previous_ratings() {
  cp $previous_ratings_file $previous_ratings_backup
}


#------------------------------------------------------------------------------

function backup_movie_sql_file() {
  cp $movie_sql_file $movie_sql_backup
}


#------------------------------------------------------------------------------

function restore_previous_ratings() {
  if [ $first_run -eq $TRUE ]
  then
    rm $previous_ratings_file
  else
    cp $previous_ratings_backup $previous_ratings_file
  fi
}


#------------------------------------------------------------------------------

function restore_movie_sql_file() {
  if [ $first_run -eq $FALSE ]
  then
    cp $movie_sql_backup $movie_sql_file
  fi
}


#------------------------------------------------------------------------------

function clean_movie_ratings() {
  # replace special characters with ASCII
  sed -i "s/'/''/g" $1
  sed -i "s/’/''/g" $1        # (double apostrophes for SQL)
  sed -i "s/Chaplin short/short/g" $1
  sed -i "s/Keaton short/short/g" $1
  sed -i "s/‘/''/g" $1
  sed -i "s/…/.../g" $1
  sed -i "s/♥/Heart/g" $1
  # remove the alphabetical headers
  sed -i "/^[A-Z]$/d" $1
  # remove the blank lines
  sed -i "/^$/d" $1
  sed -i "/X, Y, Z/d" $1
  # remove the totals at the bottom
  sed -i "/Total:/d" $1
  sed -i "/shorts$/d" $1
}


#------------------------------------------------------------------------------

function remove_additions_sql_files() {
  rm -f $movie_additions_sql_file
  rm -f $tag_additions_sql_file
  rm -f $tgt_additions_sql_file
  rm -f $cp_additions_sql_file
  rm -f $wo_additions_sql_file
}


#------------------------------------------------------------------------------

function process_return_value() {
  # did previously run program fail?
  if [ $? -ne $SUCCESS ]
  # put previous_movie_ratings.txt and sql files back to original state and exit
  then
    restore_previous_ratings
    restore_movie_sql_file
    remove_additions_sql_files
    # output given error message
    echo -e $1
    exit
  fi
}


#------------------------------------------------------------------------------

function validate_movie_ratings() {
  echo -e "\n[exec] mrc.py -- Verifying Movie_Ratings..."
  $FILTH_SCRIPTS_PATH/mrc.py $1

  # did mrc.py fail?
  process_return_value "\n[exec] mrc.py -- FAILURE"

  echo -e "\n[exec] mrc.py -- Complete: Movie_Ratings ok.\n"
}


#------------------------------------------------------------------------------

function run_movie2sql() {
  echo -e "\n[exec] movie2sql.py -- Converting movies to sql..."

  # if this is the first run, just create movie.sql
  if [ $first_run -eq $TRUE ]
  then
    # movie2sql.py without the -u option creates $movie_sql_file
    $FILTH_SCRIPTS_PATH/movie2sql.py -i $1 -m $movie_sql_file
  else
    # make copies of sql files so we can revert them in the event of an error later
    backup_movie_sql_file

    # movie2sql.py with the -u option creates/overwrites movie_additions.sql,
    # which is a file of sql inserts for just the new movies being added
    # (the -u option also checks for, and applies, updates to movies already in
    # the db)
    $FILTH_SCRIPTS_PATH/movie2sql.py -u -i $1 -m $movie_sql_file -t $tag_additions_sql_file -g $tgt_additions_sql_file -c $cp_additions_sql_file -w $wo_additions_sql_file
  fi

  # did movie2sql.py fail?
  process_return_value "\n[exec] movie2sql.py -- ERROR"

  echo -e "\n[exec] movie2sql.py -- Complete"
}


#------------------------------------------------------------------------------

function check_psql_error() {
  # see if the error file has grown since the last psql execution (to see if any error occurred)
  current_error_file_size=`stat -c %s $error_file`
  if [ $error_file_size -gt $previous_error_file_size ]
  then
    echo -e "\n[exec] psql -- ERROR running $1"
    previous_error_file_size=$error_file_size
    return $ERROR
  fi
}


#------------------------------------------------------------------------------

function run_sql_inserts() {
  # redirects any psql errors to the error file (because apparently psql always returns 0?)

  echo -e "\n[exec] psql -- Inserting movie additions"
  psql -U postgres -d filth -f $movie_additions_sql_file > /dev/null 2>>$error_file
  check_psql_error $movie_additions_sql_file
  echo -e "\n[exec] psql -- Inserting tag additions"
  psql -U postgres -d filth -f $tag_additions_sql_file > /dev/null 2>>$error_file
  check_psql_error $tag_additions_sql_file
  echo -e "\n[exec] psql -- Inserting tag_given_to additions"
  psql -U postgres -d filth -f $tgt_additions_sql_file > /dev/null 2>>$error_file
  check_psql_error $tgt_additions_sql_file
  echo -e "\n[exec] psql -- Inserting crew_person additions"
  psql -U postgres -d filth -f $cp_additions_sql_file > /dev/null 2>>$error_file
  check_psql_error $cp_additions_sql_file
  echo -e "\n[exec] psql -- Inserting worked_on additions"
  psql -U postgres -d filth -f $wo_additions_sql_file > /dev/null 2>>$error_file
  status=check_psql_error $wo_additions_sql_file
  if [ $status -eq $ERROR ]
  then
    echo -e "\n**There was an error running the additional sql insert statements."
    echo -e "\n  Check the *_additions.sql files in temp/ and note that they will be overwritten the next running of movie.sh."
    echo -e "\n  Any sql statements that you want to keep will have to be manually appended to their cooresponding main sql file (e.g. tag_additions.sql >> tag.sql)."
    exit
  fi
}


#------------------------------------------------------------------------------

function append_additions_to_sql_files() {
  cat $movie_additions_sql_file >> $movie_sql_file
  cat $tag_additions_sql_file >> $tag_sql_file
  cat $tgt_additions_sql_file >> $tgt_sql_file
  cat $cp_additions_sql_file >> $cp_sql_file
  cat $wo_additions_sql_file >> $wo_sql_file
}


#== SCRIPT ====================================================================

# the existence of previous_movie_ratings.txt determines whether or not this is
#  a "first run"
if [ ! -f $previous_ratings_file ]
then
  # create the (empty) file previous_movie_ratings.txt
  first_run=$TRUE
  touch $previous_ratings_file
else
  # make a copy of previous_movie_ratings.txt in case Movie Ratings fails
  #  verification later...
  backup_previous_ratings
fi

# convert Word document to text file
#  (the '-w 120' option tells antiword to use line width of 120 chars)
antiword -w 120 $FILTH_PATH/data/Movie_Ratings.doc > $temp_file

# make a copy of the new text version of Movie_Ratings.doc to be used in a diff
#  the next time around
cp $temp_file $previous_ratings_file

# extract the additions to the Movie_Ratings document from the previous version
diff $previous_ratings_file $temp_file | $FILTH_SCRIPTS_PATH/diff.py > $temp_file

#clean up special characters, remove non-movie lines, etc
clean_movie_ratings $temp_file

# verify that the movie ratings are valid (there are no syntax errors and such)
validate_movie_ratings $temp_file

# run the movie2sql program on the resulting text
run_movie2sql $temp_file
  
if [ $first_run -eq $FALSE ]
then
  # insert any and all additions into the Postgres database
  run_sql_inserts
  # append the new insertions to the main sql files
  append_additions_to_sql_files
  remove_additions_sql_files
fi

# create a PDF of the movie ratings document (creates $FILTH_PATH/pdf/movie_ratings.pdf)
echo -e "\nCreating pdf file...\n"
$FILTH_SCRIPTS_PATH/movie_ratings_to_pdf.sh

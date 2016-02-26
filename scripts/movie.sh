#!/bin/bash

source common.sh

movie_sql_backup=$FILTH_BACKUP_PATH/movie.sql.backup
previous_ratings_file=$FILTH_TEMP_PATH/previous_movie_ratings.txt
previous_ratings_backup=$FILTH_BACKUP_PATH/previous_movie_ratings.txt.backup
current_ratings_file=$FILTH_TEMP_PATH/current_movie_ratings.txt
ratings_diff=$FILTH_TEMP_PATH/movie_ratings_diff.txt

error_file=$FILTH_TEMP_PATH/movie.sh.error

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

function process_return_value() {
  # did previously run program fail?
  if [ $? -ne $SUCCESS ]
  # put previous_movie_ratings.txt and sql files back to original state and exit
  then
    restore_previous_ratings
    restore_movie_sql_file
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
  if [ $? -ne $SUCCESS ]
  # put previous_movie_ratings.txt back to original state and exit
  then
    restore_previous_ratings
    echo -e "\n[exec] mrc.py -- FAILURE"
    exit
  fi

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

    # movie2sql.py with the -u option appends/modifies movie.sql
    # and prompts user for tags and crew of any new movies
    $FILTH_SCRIPTS_PATH/movie2sql.py -u -i $1 -m $movie_sql_file
  fi

  # did movie2sql.py fail?
  process_return_value "\n[exec] movie2sql.py -- ERROR"

  echo -e "\n[exec] movie2sql.py -- Complete"
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
antiword -w 120 $FILTH_PATH/data/Movie_Ratings.doc > $current_ratings_file

# extract the additions to the Movie_Ratings document from the previous version
diff $previous_ratings_file $current_ratings_file | $FILTH_SCRIPTS_PATH/diff.py > $ratings_diff

# make a copy of the new text version of Movie_Ratings.doc to be used in a diff
#  the next time around
cp $current_ratings_file $previous_ratings_file

#clean up special characters, remove non-movie lines, etc
clean_movie_ratings $ratings_diff

# verify that the movie ratings are valid (there are no syntax errors and such)
validate_movie_ratings $ratings_diff

# run the movie2sql program on the resulting text
run_movie2sql $ratings_diff

# create a PDF of the movie ratings document (creates $FILTH_PATH/pdf/movie_ratings.pdf)
echo -e "\nCreating pdf file...\n"
$FILTH_SCRIPTS_PATH/movie_ratings_to_pdf.sh

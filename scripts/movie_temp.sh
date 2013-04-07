#!/bin/bash

FILTH_PATH=~/workspace/FiLTH
FILTH_TEMP_PATH=$FILTH_PATH/temp
FILTH_SQL_PATH=$FILTH_PATH/sql
FILTH_SCRIPTS_PATH=$FILTH_PATH/scripts

$previous_ratings_file=$FILTH_TEMP_PATH/previous_movie_ratings.txt
$previous_ratings_backup=$previous_ratings_file.backup
$temp_file=$FILTH_TEMP_PATH/temp
$movie_sql_file=$FILTH_SQL_PATH/movie.sql
$movie_sql_backup=$FILTH_TEMP_PATH/movie.sql.backup
$movie_additions_sql_file=$FILTH_TEMP_PATH/movie_additions.sql
$tag_sql_file=$FILTH_SQL_PATH/tag.sql
$tag_sql_backup=$FILTH_TEMP_PATH/tag.sql.backup
$tgt_sql_file=$FILTH_SQL_PATH/tag_given_to.sql
$tgt_sql_backup=$FILTH_TEMP_PATH/tag_given_to.sql.backup
$cp_sql_file=$FILTH_SQL_PATH/crew_person.sql
$cp_sql_backup=$FILTH_TEMP_PATH/crew_person.sql.backup
$wo_sql_file=$FILTH_SQL_PATH/worked_on.sql
$wo_sql_backup=$FILTH_TEMP_PATH/worked_on.sql.backup

first_run=0

#------------------------------------------------------------------------------

function backup_previous_ratings() {
  cp $previous_ratings_file $previous_ratings_backup
}


function backup_sql_files() {
  cp $movie_sql_file $movie_sql_backup
  cp $tag_sql_file $tag_sql_backup
  cp $tgt_sql_file $tgt_sql_backup
  cp $cp_sql_file $cp_sql_backup
  cp $wo_sql_file $wo_sql_backup
}


function restore_previous_ratings() {
  cp $previous_ratings_backup $previous_ratings_file
}


function restore_sql_files() {
  cp $movie_sql_backup $movie_sql_file
  cp $tag_sql_backup $tag_sql_file
  cp $tgt_sql_backup $tgt_sql_file
  cp $cp_sql_backup $cp_sql_file
  cp $wo_sql_backup $wo_sql_file
}


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

# the existence of previous_movie_ratings.txt determines whether or not this is
#  a "first run"
if [ ! -f $previous_ratings_file ]
then
  # create the (empty) file previous_movie_ratings.txt
  first_run=1
  touch $previous_ratings_file
else
  # make a copy of previous_movie_ratings.txt in case Movie Ratings fails
  #  verification later...
  backup_previous_ratings
fi

# convert Word document to text file
#  (the '-w 120' option tells antiword to use line width of 120 chars)
antiword -w 120 $FILTH_PATH/data/Movie_Ratings.doc > $temp_file

# extract the additions to the Movie_Ratings document from the previous version
diff $previous_ratings_file $temp_file | $FILTH_SCRIPTS_PATH/diff.py > $temp_file

# make a copy of the new text version of Movie_Ratings.doc to be used in a diff
#  the next time around
cp $temp_file $previous_ratings_file

#clean up special characters, remove non-movie lines, etc
clean_movie_ratings $temp_file

# verify the movie ratings (make sure there are no syntax errors and such)
echo -e "\n[exec] mrc.py -- Verifying Movie_Ratings..."
$FILTH_SCRIPTS_PATH/mrc.py $temp_file

# see if mrc.py failed
if [ $? -ne 0 ]
then
  # put previous_movie_ratings.txt back to its original state
  if [ $first_run -eq 1 ]
  then
    rm $previous_ratings_file
  else
    restore_previous_ratings
  fi
  exit
fi
echo -e "\n[exec] mrc.py -- Complete: Movie_Ratings ok.\n"

# run the movie2sql program on the resulting text
# if this is the first run, just create movie.sql
if [ $first_run -eq 1 ]
then
  # movie2sql.py without the -u option creates $movie_sql_file
  $FILTH_SCRIPTS_PATH/movie2sql.py -i $temp_file -m $movie_sql_file
  
  # see if movie2sql.py failed
  if [ $? -ne 0 ]
  then
    rm $previous_ratings_file
    exit
  fi
  
# if this is not the first run...
else
  # make copies of sql files so we can revert them in the event of an error later
  backup_sql_files

  # movie2sql.py with the -u option creates/overwrites movie_additions.sql,
  # which is a file of sql inserts for just the new movies being added
  # (the -u option also checks for, and applies, updates to movies already in
  # the db)
  $FILTH_SCRIPTS_PATH/movie2sql.py -u -i $temp_file -t $tag_sql_file -g $tgt_sql_file -c $cp_sql_file -w $wo_sql_file
  
  # see if movie2sql.py failed
  if [ $? -ne 0 ]
  # put previous_movie_ratings.txt and sql files back to their original state
  then
    restore_previous_ratings
    restore_sql_files
    exit
  fi
  
  # append the new insertions to the main movie.sql file
  cat $movie_additions_sql_file >> $movie_sql_file
  # insert the additions into the Postgres database
  psql -U postgres -d filth -f $movie_additions_sql_file
fi

# create a PDF of the movie ratings document (creates $FILTH_PATH/pdf/movie_ratings.pdf)
echo -e "\nCreating pdf file...\n"
$FILTH_SCRIPTS_PATH/movie_ratings_to_pdf.sh

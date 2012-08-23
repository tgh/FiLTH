#!/bin/bash

filth_path=~/workspace/FiLTH
filth_temp_path=~/workspace/FiLTH/temp
first_run=0

# create the file previous_movie_ratings.txt if not already created
if [ ! -f $filth_temp_path/previous_movie_ratings.txt ]
then
  first_run=1
  touch $filth_temp_path/previous_movie_ratings.txt
else
  # make a copy of previous_movie_ratings.txt in case Movie Ratings fails
  #  verification later...
  cp $filth_temp_path/previous_movie_ratings.txt $filth_temp_path/previous_movie_ratings.txt.backup
fi

# convert Word document to text file
#  (the '-w 120' option tells antiword to use line width of 120 chars)
antiword -w 120 $filth_path/data/Movie_Ratings.doc > $filth_temp_path/temp2

# extract the additions to the Movie_Ratings document from the previous version
diff $filth_temp_path/previous_movie_ratings.txt $filth_temp_path/temp2 | $filth_path/scripts/diff.py > $filth_temp_path/temp

# make a copy of the new text version of Movie_Ratings.doc to be used in a diff
#  the next time around
cp $filth_temp_path/temp2 $filth_temp_path/previous_movie_ratings.txt

# replace special characters with ASCII
sed -i "s/'/''/g" $filth_temp_path/temp
sed -i "s/’/''/g" $filth_temp_path/temp        # (double apostrophes for SQL)
sed -i "s/Chaplin short/short/g" $filth_temp_path/temp
sed -i "s/Keaton short/short/g" $filth_temp_path/temp
sed -i "s/‘/''/g" $filth_temp_path/temp
sed -i "s/…/.../g" $filth_temp_path/temp
sed -i "s/♥/Heart/g" $filth_temp_path/temp

# remove the alphabetical headers
sed "/^[A-Z]$/d" $filth_temp_path/temp > $filth_temp_path/temp2
# remove the blank lines
sed "/^$/d" $filth_temp_path/temp2 > $filth_temp_path/temp
sed "/X, Y, Z/d" $filth_temp_path/temp > $filth_temp_path/temp2
# remove the totals at the bottom
sed "/Total:/d" $filth_temp_path/temp2 > $filth_temp_path/temp
sed "/shorts$/d" $filth_temp_path/temp > $filth_temp_path/temp2

# verify the movie ratings (make sure there are no syntax errors and such)
echo -e "\n[exec] mrc.py -- Verifying Movie_Ratings..."
$filth_path/scripts/mrc.py $filth_temp_path/temp2
if [ $? -ne 0 ]
then
  # put previous_movie_ratings.txt back to its original state
  if [ $first_run -eq 1 ]
  then
    rm $filth_temp_path/previous_movie_ratings.txt
  else
    cp $filth_temp_path/previous_movie_ratings.txt.backup $filth_temp_path/previous_movie_ratings.txt
  fi
  exit
fi
echo -e "\n[exec] mrp.py -- Complete: Movie_Ratings ok.\n"

# remove the backup of previous_movie_ratings.txt since verification passed
if [ ! -f $filth_temp_path/previous_movie_ratings.txt.backup ]
then
  rm $filth_temp_path/previous_movie_ratings.txt.backup
fi

# run the movie2sql program on the resulting text
# if this is the first run, just create movie.sql
if [ $first_run -eq 1 ]
then
  # movie2sql.py without the -u option creates $filth_path/sql/movie.sql
  $filth_path/scripts/movie2sql.py $filth_temp_path/temp2
# if this is not the first run...
else
  # movie2sql.py with the -u option creates/overwrites movie_additions.sql,
  # which is a file of sql inserts for just the new movies being added
  # (the -u option also checks for, and applies, updates to movies already in
  # the db)
  $filth_path/scripts/movie2sql.py -u $filth_temp_path/temp2
  # append the new insertions to the main movie,sql file
  cat $filth_temp_path/movie_additions.sql >> $filth_path/sql/movie.sql
  # insert the additions into the Postgres database
  psql -U postgres -d filth -f $filth_temp_path/movie_additions.sql
fi

# create a PDF of the movie ratings document (creates $filth_path/pdf/movie_ratings.pdf)
echo -e "\nCreating pdf file...\n"
$filth_path/scripts/movie_ratings_to_pdf.sh

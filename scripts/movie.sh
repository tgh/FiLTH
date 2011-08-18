#!/bin/bash

filth_path=~/Projects/FiLTH
filth_temp_path=~/Projects/FiLTH/temp
first_run=0

#create the file previous_movie_ratings.txt if not already created
if [ ! -f $filth_temp_path/previous_movie_ratings.txt ]
then
  first_run=1
  touch $filth_temp_path/previous_movie_ratings.txt
fi

# convert Word document to text file
# (the '-w 120' option tells antiword to use line width of 120 chars)
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

# replace the star ratings with corresponding integer
sed -i "s/NO STARS/0/g" $filth_temp_path/temp
sed -i "s/\*\*\*\*/8/g" $filth_temp_path/temp
sed -i "s/\*\*\*½/7/g" $filth_temp_path/temp
sed -i "s/\*\*\*/6/g" $filth_temp_path/temp
sed -i "s/\*\*½/5/g" $filth_temp_path/temp
sed -i "s/\*\*/4/g" $filth_temp_path/temp
sed -i "s/\*½/3/g" $filth_temp_path/temp
sed -i "s/½\*/1/g" $filth_temp_path/temp
sed -i "s/ \* / 2 /g" $filth_temp_path/temp  # there needs to be a space before
                                             # and after the '*' here, otherwise
                                             # 'M*A*S*H' becomes 'M2A2S2H'.  Now
                                             # I may change Woody Allen's
                                             # 'Everything You Always...' to
                                             # have ' * ' as its official title
                                             # actually does, which means this
                                             # regex needs changed again...
sed -i "s/N\/A/-1/g" $filth_temp_path/temp

# replace mpaa ratings with corresponding integer
sed -i "s/\[NR\]/\[0\]/g" $filth_temp_path/temp
sed -i "s/\[G\]/\[1\]/g" $filth_temp_path/temp
sed -i "s/\[PG\]/\[2\]/g" $filth_temp_path/temp
sed -i "s/\[PG-13\]/\[3\]/g" $filth_temp_path/temp
sed -i "s/\[R\]/\[4\]/g" $filth_temp_path/temp
sed -i "s/\[X\]/\[5\]/g" $filth_temp_path/temp
sed -i "s/\[NC\-17\]/\[6\]/g" $filth_temp_path/temp

# remove the alphabetical headers
sed "/^[A-Z]$/d" $filth_temp_path/temp > $filth_temp_path/temp2
# remove the blank lines
sed "/^$/d" $filth_temp_path/temp2 > $filth_temp_path/temp
sed "/X, Y, Z/d" $filth_temp_path/temp > $filth_temp_path/temp2
# remove the totals at the bottom
sed "/Total:/d" $filth_temp_path/temp2 > $filth_temp_path/temp
sed "/shorts$/d" $filth_temp_path/temp > $filth_temp_path/temp2

# run the movie2sql program on the resulting text
# if this is the first run, just create movie.sql
if [ $first_run -eq 1 ]
then
  $filth_path/scripts/movie2sql.py $filth_temp_path/temp2 1 > $filth_path/sql/movie.sql
# if this is not the first run...
else
  # create/overwrite movie_additions.sql which is a file of sql inserts for just
  # the new movies being added
  $filth_path/scripts/movie2sql.py $filth_temp_path/temp2 0 > $filth_temp_path/movie_additions.sql
  # append the new insertions to the main movie,sql file
  cat $filth_temp_path/movie_additions.sql >> $filth_path/sql/movie.sql
  # insert the additions into the Postgres database
  psql -U postgres -d filth -f $filth_temp_path/movie_additions.sql
fi

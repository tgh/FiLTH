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

# replace country of origin with corresponding integer
sed -i "s/USA$/1/g" $filth_temp_path/temp
sed -i "s/France$/2/g" $filth_temp_path/temp
sed -i "s/England$/3/g" $filth_temp_path/temp
sed -i "s/Canada$/4/g" $filth_temp_path/temp
sed -i "s/China$/5/g" $filth_temp_path/temp
sed -i "s/Russia$/6/g" $filth_temp_path/temp
sed -i "s/Germany$/7/g" $filth_temp_path/temp
sed -i "s/Argentina$/8/g" $filth_temp_path/temp
sed -i "s/Portugal$/9/g" $filth_temp_path/temp
sed -i "s/Spain$/10/g" $filth_temp_path/temp
sed -i "s/Mexico$/11/g" $filth_temp_path/temp
sed -i "s/Italy$/12/g" $filth_temp_path/temp
sed -i "s/Ireland$/13/g" $filth_temp_path/temp
sed -i "s/Scotland$/14/g" $filth_temp_path/temp
sed -i "s/Czech Republic$/15/g" $filth_temp_path/temp
sed -i "s/Iran$/16/g" $filth_temp_path/temp
sed -i "s/The Netherlands$/17/g" $filth_temp_path/temp
sed -i "s/Sweden$/18/g" $filth_temp_path/temp
sed -i "s/Finland$/19/g" $filth_temp_path/temp
sed -i "s/Norway$/20/g" $filth_temp_path/temp
sed -i "s/Poland$/21/g" $filth_temp_path/temp
sed -i "s/Bosnia$/22/g" $filth_temp_path/temp
sed -i "s/Japan$/23/g" $filth_temp_path/temp
sed -i "s/Taiwan$/24/g" $filth_temp_path/temp
sed -i "s/India$/25/g" $filth_temp_path/temp
sed -i "s/Greece$/26/g" $filth_temp_path/temp
sed -i "s/Israel$/27/g" $filth_temp_path/temp
sed -i "s/Lebanon$/28/g" $filth_temp_path/temp
sed -i "s/South Africa$/29/g" $filth_temp_path/temp
sed -i "s/Australia$/30/g" $filth_temp_path/temp
sed -i "s/New Zealand$/31/g" $filth_temp_path/temp
sed -i "s/Brazil$/32/g" $filth_temp_path/temp
sed -i "s/Iceland$/33/g" $filth_temp_path/temp
sed -i "s/Vietnam$/34/g" $filth_temp_path/temp
sed -i "s/Denmark$/35/g" $filth_temp_path/temp
sed -i "s/Belgium$/36/g" $filth_temp_path/temp
sed -i "s/Switzerland/37/g" $filth_temp_path/temp

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
  $filth_path/scripts/movie2sql.py $filth_temp_path/temp2 > $filth_path/sql/movie.sql
# if this is nor the first run...
else
  # create/overwrite movie_additions.sql which is a file of sql inserts for just
  # the new movies being added
  $filth_path/scripts/movie2sql.py $filth_temp_path/temp2 > $filth_temp_path/movie_additions.sql
  # append the new insertions to the main movie,sql file
  cat $filth_temp_path/movie_additions.sql >> $filth_path/sql/movie.sql
  # insert the additions into the Postgres database
  psql -U postgres -d filth -f $filth_temp_path/movie_additions.sql
fi

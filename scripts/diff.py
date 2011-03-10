#!/usr/bin/env python

'''
This script is used within the scripts/movie.sh shell script.  It's purpose is
to read in the output resulting from

$ diff temp/previous_movie_ratings.txt temp/temp2

where temp2 is the text translation of data/Movie_Ratings.doc produced by
antiword.  It parses the diff output for the new lines in temp2 that are not in
previous_movie_ratings.txt, essentially grabbing the movies that were added
since the previous run of the movie.sh script.  It prints these to stdout to be
redirected within the movie.sh script.
'''

import sys

#read in all lines from the output of diff, but only keep the lines starting
# with '>' (it is assumed the newer version of the files being compared is the
# one passed as the second argument to diff)
for line in sys.stdin:
  if line[0] == '>':
    #drop the "> "
    print line[2:-1]

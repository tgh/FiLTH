#!/usr/bin/env python

from MovieTagger import MovieTagger
from QuitException import QuitException
import re

def initMovies(lastProcessed):
  movies = []
  movieFile = open('/home/thayes/Projects/FiLTH/sql/movie.sql', 'r')
  movielines = movieFile.readlines()
  for movieline in movielines:
    movieline = movieline.replace("''", "'")
    vals = re.search('VALUES \\((.*)\\);', movieline).group(1)

    movie = {}
    movie['mid'] = re.search('(\d+)', vals).group(1)
    #skip movie if already tagged
    if int(movie['mid']) <= lastProcessed:
      continue
    titleStartIndex = vals.find("'") + 1
    titleEndIndex = vals.find("', ")
    movie['title'] = vals[titleStartIndex:titleEndIndex]
    vals = vals[(titleEndIndex + 3):]
    vals = vals.split(', ')
    #skip movie haven't seen
    if vals[1] == "'not seen'":
      continue
    movie['year'] = int(vals[0])
    movies.append(movie)
  return movies


if __name__ == '__main__':
  log = open('/home/thayes/Projects/FiLTH/logs/MovieTaggerTest.log', 'w')
  tagger = MovieTagger('/home/thayes/Projects/FiLTH/sql/tag_given_to.sql', '/home/thayes/Projects/FiLTH/sql/tag.sql', log)
  #movies = initMovies(100)
  try:
    #for m in movies:
    #  tagger.promptUserForTag(m['mid'], m['title'], m['year'])
    tagger.promptUserForTag(999999, 'Foo', 1999)
  except (QuitException, KeyboardInterrupt):
    print '\nQUITTING\n'
  finally:
    if tagger.hasInserts():
       while True:
         response = raw_input('\nThere are still unwritten sql insert statements. Write them out? ').lower()
         if response not in ['y','n']:
           print "Only 'y'/'n'\n"
           continue
         if response == 'y':
           tagger.writeTagInsertsToFile(open('/home/thayes/Projects/FiLTH/sql/tag.sql', 'a'))
           tagger.writeTagGivenToInsertsToFile(open('/home/thayes/Projects/FiLTH/sql/tag_given_to.sql', 'a'))
           break
         else:
           break
    tagger.close()

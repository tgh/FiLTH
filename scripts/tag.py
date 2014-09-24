#!/usr/bin/env python

#------------------------------------------------------------------------------
#
# This script interacts with the user to create sql insert statements to
# populate the tag and tag_given_to tables in the filth database.
#
#------------------------------------------------------------------------------

import sys
import re
from MovieTagger import MovieTagger
from QuitException import QuitException

FILTH_PATH = '/home/thayes/Projects/FiLTH'
#FILTH_PATH = '/home/tgh/workspace/FiLTH'

TAG_GIVEN_TO_FILENAME = FILTH_PATH + '/sql/tag_given_to.sql'
tagGivenToFile = None
TAG_FILENAME = FILTH_PATH + '/sql/tag.sql'
tagFile = None
LOG_FILENAME = FILTH_PATH + '/logs/tag.log'
logger = None
TEMP_FILENAME = FILTH_PATH + '/temp/tagTemp.txt'
tempFile = None
MOVIE_FILENAME = FILTH_PATH + '/sql/movie.sql'
movieFile = None
movies = []
  


def log(func, message):
  try:
    logger.write('[' + func + '] - ' + message + '\n')
  except UnicodeEncodeError:
    logger.write('[' + func + '] - MOVIE: 8 1/2 (1963)\n')


def initMovies(lastProcessed):
  global movies

  log('initMovies', '>> Initializing movie map <<')
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
  log('initMovies', '>> movie map initialized <<')


def closeFiles():
  if tagGivenToFile:
    tagGivenToFile.close()
  if tagFile:
    tagFile.close()
  if logger:
    logger.close()
  if tempFile:
    tempFile.close()
  if movieFile:
    movieFile.close()


def quit(mid):
  global tempFile

  log('quit', 'quitting')
  tempFile = open(TEMP_FILENAME, 'w')
  tempFile.write(str(mid))
  tempFile.close()


if __name__ == '__main__':
  try:
    tagGivenToFile = open(TAG_GIVEN_TO_FILENAME, 'a')
    tagFile = open(TAG_FILENAME, 'a')
    logger = open(LOG_FILENAME, 'w')
    tempFile = open(TEMP_FILENAME, 'r')
    movieFile = open(MOVIE_FILENAME, 'r')
  except IOError as e:
    sys.stderr.write("**ERROR: opening file: " + str(e) + ".\n")
    sys.exit()

  movieTagger = MovieTagger(TAG_GIVEN_TO_FILENAME, TAG_FILENAME, logger)

  lastProcessed = tempFile.read()
  log('main', 'last mid processed (read from ' + TEMP_FILENAME + '): ' + lastProcessed)
  lastProcessed = int(lastProcessed)

  #grab all movies seen from movies file
  initMovies(lastProcessed)

  try:
    for movie in movies:
      movieTagger.promptUserForTag(movie['mid'], movie['title'], movie['year'])
      lastProcessed += 1
  except QuitException, KeyboardInterrupt:
    if movieTagger.hasInserts():
      while True:
        response = raw_input('\n**WARNING: There are still unwritten sql insert statements. Write them out? ').lower()
        if response not in ['y','n']:
          print "Only 'y'/'n'\n"
          continue
        if response == 'y':
          movieTagger.writeTagInsertsToFile(tagFile)
          movieTagger.writeTagGivenToInsertsToFile(tagGivenToFile)
          quit(lastProcessed)
        break
  finally:
    closeFiles()

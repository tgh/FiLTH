#!/usr/bin/env python

#------------------------------------------------------------------------------
#
# This script interacts with the user to create sql insert statements to
# populate the crew_person, worked_on, tag, and tag_given_to tables in the
# filth database.
#
#------------------------------------------------------------------------------

import sys
import re
from os import getenv
from MovieTagger import MovieTagger
from MovieCrew import MovieCrew
from QuitException import QuitException
from getopt import getopt
from getopt import GetoptError

FILTH_PATH = getenv('FILTH_PATH', '/home/tgh/workspace/FiLTH')

CREW_PERSON_SQL_FILE = FILTH_PATH + '/sql/crew_person.sql'
CREW_PERSON_ADDITIONS_SQL_FILE = FILTH_PATH + '/sql/crew_person_additions.sql'
MOVIE_SQL_FILE = FILTH_PATH + '/sql/movie.sql'
TAG_GIVEN_TO_SQL_FILE = FILTH_PATH + '/sql/tag_given_to.sql'
TAG_GIVEN_TO_ADDITIONS_SQL_FILE = FILTH_PATH + '/sql/tag_given_to_additions.sql'
TAG_SQL_FILE = FILTH_PATH + '/sql/tag.sql'
TAG_SQL_ADDITIONS_FILE = FILTH_PATH + '/sql/tag_additions.sql'
WORKED_ON_SQL_FILE = FILTH_PATH + '/sql/worked_on.sql'
WORKED_ON_ADDITIONS_SQL_FILE = FILTH_PATH + '/sql/worked_on_additions.sql'
LOG_FILENAME = FILTH_PATH + '/logs/crew_and_tag.log'
TEMP_FILENAME = FILTH_PATH + '/temp/crewTagTemp.txt'

crewPersonFile = None
workedOnFile = None
tagGivenToFile = None
tagFile = None
logger = None
tempFile = None
movieFile = None
movies = []
  


def log(func, message):
  try:
    logger.write('[' + func + '] - ' + message + '\n')
  except UnicodeEncodeError:
    logger.write('[' + func + '] - MOVIE: 8 1/2 (1963)\n')


def usage():
    print "  usage: crew_and_tag.py [--mids=<comma-separated list of movie ids>]\n"


def processArgs():
    #no args--default to processing every movie after "last processed"
    if len(sys.argv[1:]) == 0:
        return []

    try:
        opts, args = getopt(sys.argv[1:], '', ['mids='])
    except GetoptError as goe:
        sys.stderr.write(str(goe) + '\n\n')
        usage()
        sys.exit(1)

    #gather requested movie ids
    mids = []
    midStrings = opts[0][1].split(',')
    for midString in midStrings:
        try:
            mids.append(int(midString))
        except ValueError as e:
            sys.stderr.write(str(e) + '\n\n')
            print '\nThe list of mids contains a non-integer\n'
            sys.exit(1)

    return mids


def createMovie(vals):
    movie = {}
    movie['mid'] = int(re.search('(\d+)', vals).group(1))
    titleStartIndex = vals.find("'") + 1
    titleEndIndex = vals.find("', ")
    movie['title'] = vals[titleStartIndex:titleEndIndex]
    vals = vals[(titleEndIndex + 3):]
    vals = vals.split(', ')
    movie['star_rating'] = vals[1]
    if vals[0] == 'NULL':
        movie['year'] = vals[0]
    else:
        movie['year'] = int(vals[0])
    return movie


def initMovies(lastProcessed):
  global movies

  log('initMovies', '>> Initializing movie map <<')
  movielines = movieFile.readlines()
  for movieline in movielines:
    movieline = movieline.replace("''", "'")
    vals = re.search('VALUES \\((.*)\\);', movieline).group(1)
    movie = createMovie(vals)

    #skip movie if already tagged
    if int(movie['mid']) <= lastProcessed:
      continue
    #skip movies not seen
    if movie['star_rating'] == 'not seen':
      continue

    movies.append(movie)
  log('initMovies', '>> movie map initialized <<')


def initRequestedMovies(mids):
    global movies

    log('initRequestedMovies', '>> Initilizing movie map for requested movies <<')
    movielines = movieFile.readlines()
    for movieline in movielines:
        movieline = movieline.replace("''", "'")
        vals = re.search('VALUES \\((.*)\\);', movieline).group(1)
        movie = createMovie(vals)

        #skip movies not in the requested list
        if movie['mid'] not in mids:
            continue

        movies.append(movie)
    log('initRequestedMovies', '>> movie map initialized <<')


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
  if crewPersonFile:
    crewPersonFile.close()
  if workedOnFile:
    workedOnFile.close()


def quit(mid):
  global tempFile

  log('quit', 'quitting')
  tempFile = open(TEMP_FILENAME, 'w')
  tempFile.write(str(mid))
  tempFile.close()


if __name__ == '__main__':
  try:
    tagGivenToFile = open(TAG_GIVEN_TO_SQL_FILE, 'a')
    tagGivenToAdditionsFile = open(TAG_GIVEN_TO_ADDITIONS_SQL_FILE, 'a')
    tagFile = open(TAG_SQL_FILE, 'a')
    tagAdditionsFile = open(TAG_SQL_ADDITIONS_FILE, 'a')
    logger = open(LOG_FILENAME, 'w')
    tempFile = open(TEMP_FILENAME, 'r+')
    movieFile = open(MOVIE_SQL_FILE, 'r')
    crewpersonFile = open(CREW_PERSON_SQL_FILE, 'a')
    crewpersonAdditionsFile = open(CREW_PERSON_ADDITIONS_SQL_FILE, 'a')
    workedOnAdditionsFile = open(WORKED_ON_SQL_FILE, 'a')
    workedOnFile = open(WORKED_ON_ADDITIONS_SQL_FILE, 'a')
  except IOError as e:
    sys.stderr.write("**ERROR: opening file: " + str(e) + ".\n")
    sys.exit()

  movieTagger = MovieTagger(TAG_GIVEN_TO_SQL_FILE, TAG_SQL_FILE, logger)
  crewHandler = MovieCrew(WORKED_ON_SQL_FILE, CREW_PERSON_SQL_FILE, logger)

  #gather optional movie ids from command-line
  mids = processArgs()
  #no mids given on command-line--use "last processed" movie id
  if len(mids) == 0:
    lastProcessed = tempFile.read()
    log('main', 'last mid processed (read from ' + TEMP_FILENAME + '): ' + lastProcessed)
    lastProcessed = int(lastProcessed)

    #grab all movies seen from movies file after "last processed"
    initMovies(lastProcessed)
  #process only the requested movies with the given ids
  else:
    initRequestedMovies(mids)

  try:
    for movie in movies:
      movieTagger.promptUserForTag(movie['mid'], movie['title'], movie['year'])
      crewHandler.promptUserForCrewPerson(movie['mid'], movie['title'], movie['year'])
      lastProcessed = movie['mid']
  except QuitException, KeyboardInterrupt:
    if movieTagger.hasInserts():
      while True:
        response = raw_input('\n**WARNING: There are still unwritten tag sql insert statements. Write them out? ').lower()
        if response not in ['y','n']:
          print "Only 'y'/'n'\n"
          continue
        if response == 'y':
          movieTagger.writeTagInsertsToFile(tagFile)
          movieTagger.writeTagGivenToInsertsToFile(tagGivenToFile)
          movieTagger.writeTagInsertsToFile(tagAdditionsFile)
          movieTagger.writeTagGivenToInsertsToFile(tagGivenToAdditionsFile)
        break
    if crewHandler.hasInserts():
      while True:
        response = raw_input('\n**WARNING: There are still unwritten crew sql insert statements. Write them out? ').lower()
        if response not in ['y','n']:
          print "Only 'y'/'n'\n"
          continue
        if response == 'y':
          crewHandler.writeCrewInsertsToFile(crewpersonFile)
          crewHandler.writeWorkedOnInsertsToFile(workedOnFile)
          crewHandler.writeCrewInsertsToFile(crewpersonAdditionsFile)
          crewHandler.writeWorkedOnInsertsToFile(workedOnAdditionsFile)
        break
  finally:
    quit(lastProcessed)
    closeFiles()

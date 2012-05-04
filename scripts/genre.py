#!/usr/bin/env python

#------------------------------------------------------------------------------
#
# This script interacts with the user to create swl insert statements to
# populate the genre_contains table in the filth database.
#
#------------------------------------------------------------------------------

import sys
import imp
import string
import traceback

models = imp.load_source('models', '/home/tgh/Projects/FiLTH/src/orm/models.py')
sqlFilename = "/home/tgh/Projects/FiLTH/sql/genre_contains.sql"
sqlFile = None
logFilename = '/home/tgh/Projects/FiLTH/temp/genre.log'
logger = None
tempFilename = '/home/tgh/Projects/FiLTH/temp/genreTemp.txt'
tempFile = None
genres = ['Drama', 'Comedy', 'Thriller', 'Independent', 'Fantasy', 'Science-Fiction', 'Animated', 'Mystery', 'Horror', 'Action', 'Adventure', 'Documentary', 'Christmas', 'Short', 'Unconventional', 'Western', 'War']


def log(func, message):
  global count
  try:
    logger.write('[' + func + '] - ' + message + '\n')
  except UnicodeEncodeError:
    logger.write('[' + func + '] - MOVIE: 8 1/2 (1963)\n')


def printGenres():
  count = 0
  for genre in genres:
    print '  ' + str(count) + ' = ' + genre
    count += 1


def writeSql(mid, gid):
  log('writeSql', 'writing sql: INSERT INTO genre_contains VALUES(' + str(mid) + ', ' + str(gid) + ');')
  sqlFile.write('INSERT INTO genre_contains VALUES(' + str(mid) + ', ' + str(gid) + ');\n')


def getGid(genre):
  return models.Genre.query.filter(models.Genre.gen_name == genre).one().gid


def quit(mid):
  global tempFile

  log('quit', 'quitting')
  logger.close()
  sqlFile.close()
  tempFile.close()
  tempFile = open(tempFilename, 'w')
  tempFile.write(mid)
  tempFile.close()
  sys.exit(0)


def extractGenreIds(userInput):
  '''Throws ValueError'''

  gids = userInput.split(',')
  map(string.split, gids)
  gids = map(int, gids)
  for gid in gids:
    if gid < 0 or gid > len(genres)-1:
      raise ValueError


def inquireMovie(movie):
  try:
    log('inquireMovie', 'MOVIE: ' + movie.title + ' (' + str(movie.year) + ')')
    print '\nMOVIE: [' + str(movie.mid) + '] ' + movie.title + ' (' + str(movie.year) + ')\n'
    printGenres()
    print 'You may enter \'q\' to quit, \'skip\' to skip the current movie, or any number of genres as a comma-separated list (e.g. "0,3,5").'
    while(True):
      try:
        response = raw_input('Enter genres: ').lower()
        if response == 'q':
          quit(movie.mid)
        if response == 'skip':
          return
        gids = extractGenreIds(response)
      except ValueError:
        print '\n**Only numeric values from 0 to ' + str(len(genres)-1)
        continue
      log('inquireMovie', 'user entered genre(s): ' + str(gids))
      for gid in gids:
        writeSql(movie.mid, getGid(genres[gid]))
      break
  except Exception as e:
    print '\n**FATAL ERROR: ' + str(e) + '\n'
    log('EXCEPTION', str(e))
    traceback.print_exc(file=logger)
    quit(movie.mid)


if __name__ == '__main__':
  try:
    sqlFile = open(sqlFilename, 'a')
    logger = open(logFilename, 'w')
    tempFile = open(tempFilename, 'r')
  except IOError as e:
    sys.stderr.write("**ERROR: opening file: " + str(e) + ".\n")
    sys.exit()
  lastProcessed = tempFile.read()
  log('main', 'last mid processed (from file): ' + lastProcessed)
  lastProcessed = int(lastProcessed)
  log('main', 'last mid processed (cast): ' + str(lastProcessed))
  movies = models.Movie.query.filter(models.Movie.star_rating != 'not_seen').filter(models.Movie.mid > lastProcessed).order_by(models.Movie.mid).all()
  map(inquireMovie, movies)

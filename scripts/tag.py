#!/usr/bin/env python

#------------------------------------------------------------------------------
#
# This script interacts with the user to create sql insert statements to
# populate the tag and tag_given_to tables in the filth database.
#
#------------------------------------------------------------------------------

import sys
import imp
import string
import traceback

models = imp.load_source('models', '/home/tgh/Projects/FiLTH/src/orm/models.py')
tagGivenToFilename = "/home/tgh/Projects/FiLTH/sql/tag_given_to.sql"
tagGivenToFile = None
tagFilename = '/home/tgh/Projects/FiLTH/sql/tag.sql'
tagFile = None
logFilename = '/home/tgh/Projects/FiLTH/temp/tagging.log'
logger = None
tempFilename = '/home/tgh/Projects/FiLTH/temp/tagTemp.txt'
tempFile = None
tags = []
  


def log(func, message):
  global count
  try:
    logger.write('[' + func + '] - ' + message + '\n')
  except UnicodeEncodeError:
    logger.write('[' + func + '] - MOVIE: 8 1/2 (1963)\n')


def updateTags(tag='NO TAG GIVEN'):
  global tags
  tags = models.Tag.query.all()
  log('updateTags', 'tags updated with \'' + tag + '\'')


def printTags():
  for tag in tags:
    print '  ' + str(tag.tid) + ' = ' + str(tag.tag_name)


def writeSql(mid, tid):
  log('writeSql', 'writing sql: INSERT INTO tag_given_to VALUES(' + str(mid) + ', ' + str(tid) + ');')
  tagGivenToFile.write('INSERT INTO tag_given_to VALUES(' + str(mid) + ', ' + str(tid) + ');\n')


def quit(mid):
  global tempFile

  log('quit', 'quitting')
  logger.close()
  tagGivenToFile.close()
  tagFile.close()
  tempFile.close()
  tempFile = open(tempFilename, 'w')
  tempFile.write(str(mid))
  tempFile.close()
  sys.exit(0)


def extractTagIds(userInput):
  '''Throws ValueError'''

  tids = userInput.split(',')
  map(string.strip, tids)
  tids = map(int, tids)
  for tid in tids:
    if tid < 1 or tid > len(tags):
      raise ValueError
  return tids


def addTag(tag):
  log('addTag', 'writing sql: INSERT INTO tag VALUES(DEFAULT, \'' + tag + '\');')
  tagFile.write('INSERT INTO tag VALUES (DEFAULT, \'' + tag + '\');\n')
  log('addTag', 'adding tag, \'' + tag + '\', to the database...')
  newTag = models.Tag(tag_name=tag)
  models.session.add(newTag)
  models.session.commit()
  log('addTag', 'tag, \'' + tag + '\', added to database')


def addTagUI():
  while(True):
    tag = raw_input('\nEnter new tag: ')
    while(True):
      confirm = raw_input('Is this what you wanted: ' + tag + ' (y/n)? ').lower()
      if 'y' == confirm:
        log('addTagUI', 'User wants to add tag \'' + tag + '\'')
        addTag(tag)
        return
      elif 'n' == confirm:
        break
      else:
        print '\n**Only \'y\' or \'n\' please.'


def inquireMovie(movie):
  try:
    log('inquireMovie', 'MOVIE: ' + movie.title + ' (' + str(movie.year) + ')')
    print '\nMOVIE: [' + str(movie.mid) + '] ' + movie.title + ' (' + str(movie.year) + ')\n'
    printTags()
    print 'You may enter \'q\' to quit, \'skip\' to skip the current movie, \'add\' to add a new tag, or any number of tags as a comma-separated list (e.g. "0,3,5").'
    while(True):
      try:
        response = raw_input('Enter tags: ').lower()
        if response == 'q':
          quit(movie.mid-1)
        if response == 'skip':
          return
        if response == 'add':
          addTagUI()
          updateTags()
          printTags()
          continue
        tids = extractTagIds(response)
      except ValueError:
        print '\n**Only numeric values from 0 to ' + str(len(tags)-1)
        continue
      log('inquireMovie', 'user entered tag(s): ' + str(tids))
      for tid in tids:
        writeSql(movie.mid, tid)
      break
  except Exception as e:
    print '\n**FATAL ERROR: ' + str(e) + '\n'
    log('EXCEPTION', str(e))
    traceback.print_exc(file=logger)
    quit(movie.mid)


if __name__ == '__main__':
  try:
    tagGivenToFile = open(tagGivenToFilename, 'a')
    tagFile = open(tagFilename, 'a')
    logger = open(logFilename, 'w')
    tempFile = open(tempFilename, 'r')
  except IOError as e:
    sys.stderr.write("**ERROR: opening file: " + str(e) + ".\n")
    sys.exit()
  lastProcessed = tempFile.read()
  log('main', 'last mid processed (from file): ' + lastProcessed)
  lastProcessed = int(lastProcessed)
  log('main', 'last mid processed (cast): ' + str(lastProcessed))
  #grab all tags currently in db
  tags = models.Tag.query.all()
  #grab all movies seen
  movies = models.Movie.query.filter(models.Movie.star_rating != 'not_seen').filter(models.Movie.mid > lastProcessed).order_by(models.Movie.mid).all()
  map(inquireMovie, movies)

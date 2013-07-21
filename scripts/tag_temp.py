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
import re

FILTH_PATH = '/home/tgh/workspace/FiLTH'

models = imp.load_source('models', FILTH_PATH + '/src/python/models.py')
tagGivenToFilename = FILTH_PATH + '/sql/tag_given_to.sql'
tagGivenToFile = None
tagFilename = FILTH_PATH + '/sql/tag.sql'
tagFile = None
logFilename = FILTH_PATH + '/logs/tag.log'
logger = None
tempFilename = FILTH_PATH + '/temp/tagTemp.txt'
tempFile = None
movieFilename = FILTH_PATH + '/sql/movie.sql'
movieFile = None
tagMap = {}
movies = []
nextTid = 0
  


def log(func, message):
  try:
    logger.write('[' + func + '] - ' + message + '\n')
  except UnicodeEncodeError:
    logger.write('[' + func + '] - MOVIE: 8 1/2 (1963)\n')


def initTags():
  global tagMap

  log('initTags', '***Initializing tag map***')
  taglines = tagFile.readlines()
  for tagline in lines:
    tid = re.search('VALUES \\((\d+),' tagline).group(1)
    tag = re.search(", '([a-zA-Z\- ]+)'\\);", tagline).group(1)
    log('initTags', 
    tagMap[int(tid)] = tag
  log('initTags', 'tagMap initialized')


def initMovies(lastProcessed):
  global movies

  log('initMovies', '***Initializing movie map***')
  movielines = movieFile.readlines()
  for movieline in movielines:
    vals = re.search('VALUES \\((.*)\\);', movieline).group(1)
    vals = vals.split(', ')
    #skip movie haven't seen or already tagged
    if vals[3] == "'not seen'" or int(vals[0]) <= lastProcessed:
      continue
    movie = {}
    movie['mid'] = int(vals[0])
    movie['title'] = vals[1].strip("'")
    movie['year'] = int(vals[2])
    movie['stars'] = vals[3].strip("'")
    movie['mpaa'] = vals[4].strip("'")
    movie['country'] = vals[5].strip("'")
    movies.append(movie)


def printTags():
  count = 1
  prevLength = 0
  for tag in tagMap.values():
    if count % 2 == 1:
      print '  ' + str(count) + ' = ' + tag,
      prevLength = len(tag)
    else:
      if prevLength >= 18:
        print '\t' + str(count) + ' = ' + tag
      elif prevLength >= 8 and count > 9:
        print '\t\t' + str(count) + ' = ' + tag
      else:
        print '\t\t\t' + str(count) + ' = ' + tag
    count += 1
  if len(tagMap) % 2 == 1:
    print '\n'


def writeSql(movie, tid):
  log('writeSql', 'writing sql: INSERT INTO tag_given_to VALUES(' + str(movie['mid']) + ', ' + str(tid) + ');')
  tagGivenToFile.write('INSERT INTO tag_given_to VALUES(' + str(movie['mid']) + ', ' + str(tid) + ');  -- ' + movie['title'] + ' (' + str(movie['year']) + ') tagged with \'' + tagMap[tid] + '\'\n')


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
  closeFiles()
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
    if tid < 1 or tid > len(tagMap):
      raise ValueError
  return tids


def addTag(tag):
  global tagMap

  log('addTag', 'writing sql: INSERT INTO tag VALUES(' + nextTid + ', \'' + tag + '\');')
  tagFile.write('INSERT INTO tag VALUES (DEFAULT, \'' + tag + '\');\n')
  tagMap[nextTid] = tag
  nextTid = nextTid + 1


def addTagUI():
  while(True):
    tag = raw_input('\nEnter new tag: ')
    while(True):
      confirm = raw_input('Is this what you wanted: ' + tag + ' (y/n)? ').lower()
      if 'y' == confirm:
        log('addTagUI', 'User wants to add tag \'' + tag + '\'')
        addTag(tag)
      elif 'n' == confirm:
        break
      else:
        print '\n**Only \'y\' or \'n\' please.'


def inquireMovie(movie):
  try:
    log('inquireMovie', 'MOVIE: ' + movie['title'] + ' (' + str(movie['year']) + ')')
    print '\n--------------------------------------------------------------------'
    print '\nMOVIE: [' + str(movie['mid']) + '] ' + movie['title'] + ' (' + str(movie['year']) + ')\n'
    printTags()
    print 'You may enter \'q\' to quit, \'skip\' to skip the current movie, \'add\' to add a new tag, or any number of tags as a comma-separated list (e.g. "0,3,5").'
    while(True):
      try:
        response = raw_input('Enter tags: ').lower()
        if response == 'q':
          quit(movie['mid']-1)
        if response == 'skip':
          return
        if response == 'add':
          addTagUI()
          printTags()
          continue
        tids = extractTagIds(response)
      except ValueError:
        print '\n**Only numeric values from 0 to ' + str(len(tags)-1)
        continue
      log('inquireMovie', 'user entered tag(s): ' + str(tids))
      for tid in tids:
        writeSql(movie, tid)
      break
  except Exception as e:
    print '\n**FATAL ERROR: ' + str(e) + '\n'
    log('EXCEPTION', str(e))
    traceback.print_exc(file=logger)
    traceback.print_exc(file=sys.stdout)
    quit(movie.mid)



if __name__ == '__main__':
  try:
    tagGivenToFile = open(tagGivenToFilename, 'a')
    tagFile = open(tagFilename, 'r+')
    logger = open(logFilename, 'w')
    tempFile = open(tempFilename, 'r')
    movieFile = open(movieFilename, 'r')
  except IOError as e:
    sys.stderr.write("**ERROR: opening file: " + str(e) + ".\n")
    sys.exit()
  lastProcessed = tempFile.read()
  log('main', 'last mid processed (from file): ' + lastProcessed)
  lastProcessed = int(lastProcessed)
  log('main', 'last mid processed (cast): ' + str(lastProcessed))
  #grab all tags from tag file
  initTags()
  nextTid = len(tagMap) + 1
  #grab all movies seen from movies file
  initMovies(lastProcessed)
  map(inquireMovie, movies)
  closeFiles()

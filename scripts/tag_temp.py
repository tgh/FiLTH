#!/usr/bin/env python

#------------------------------------------------------------------------------
#
# This script interacts with the user to create sql insert statements to
# populate the tag and tag_given_to tables in the filth database.
#
#------------------------------------------------------------------------------

import sys
import string
import traceback
import re

FILTH_PATH = '/home/tgh/workspace/FiLTH'

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
longestTagLength = 0
  


def log(func, message):
  try:
    logger.write('[' + func + '] - ' + message + '\n')
  except UnicodeEncodeError:
    logger.write('[' + func + '] - MOVIE: 8 1/2 (1963)\n')


def initTags():
  global tagMap, longestTagLength

  log('initTags', '>> Initializing tag map <<')
  taglines = tagFile.readlines()
  for tagline in taglines:
    tid = re.search('VALUES \\((\d+),', tagline).group(1)
    tag = re.search(", '([a-zA-Z\- ]+)'\\);", tagline).group(1)
    log('initTags', 'Found tag: ' + tid + ' - ' + tag)
    tagMap[int(tid)] = tag
    if len(tag) > longestTagLength:
      longestTagLength = len(tag)
  log('initTags', '>> tag map initialized <<')


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


def printTags():
  padding = '  '
  for item in tagMap.items():
    key = item[0]
    tag = item[1]

    if len(str(key)) == 1:
      number = ' ' + str(key)
    else:
      number = str(key)

    if key % 3 == 0:
      print number + ' = ' + tag
      continue
    if key % 3 == 1:
      sys.stdout.write(padding)
    sys.stdout.write(number + ' = ' + tag)
    longestLengthDiff = longestTagLength - len(tag)
    for i in range(0,longestLengthDiff):
      sys.stdout.write(' ')
    sys.stdout.write(padding)
  if len(tagMap) % 3 != 0:
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
  global tagMap, nextTid

  log('addTag', 'writing sql: INSERT INTO tag VALUES(' + str(nextTid) + ', \'' + tag + '\');')
  tagFile.write('INSERT INTO tag VALUES (' + str(nextTid) + ', \'' + tag + '\');\n')
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
        return
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
          quit(int(movie['mid'])-1)
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
    print '\n***FATAL ERROR: ' + str(e) + '\n'
    log('EXCEPTION', str(e))
    traceback.print_exc(file=logger)
    traceback.print_exc(file=sys.stdout)
    quit(int(movie['mid'])-1)
  except KeyboardInterrupt:
    print '\n**Fine.Whatever.\n'
    log('EXCEPTION', '***Force quit by user.***')
    quit(int(movie['mid'])-1)



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
  log('main', 'last mid processed (read from ' + tempFilename + '): ' + lastProcessed)
  lastProcessed = int(lastProcessed)
  #grab all tags from tag file
  initTags()
  nextTid = len(tagMap) + 1
  #grab all movies seen from movies file
  initMovies(lastProcessed)
  map(inquireMovie, movies)
  closeFiles()

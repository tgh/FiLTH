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

FILTH_PATH = '/home/thayes/Projects/FiLTH'
#FILTH_PATH = '/home/tgh/workspace/FiLTH'

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
tagMap = {} # tid -> (tag, parent id, [child tids])
movies = []
nextTid = 0
longestTagLength = 0
  


def log(func, message):
  try:
    logger.write('[' + func + '] - ' + message + '\n')
  except UnicodeEncodeError:
    logger.write('[' + func + '] - MOVIE: 8 1/2 (1963)\n')


def initTags():
  global tagMap

  log('initTags', '>> Initializing tag map <<')
  taglines = tagFile.readlines()
  for tagline in taglines:
    tid = re.search('VALUES \\((\d+),', tagline).group(1)
    tag = re.search(", '([0-9a-zA-Z/\(\)\.\- ']+)', ", tagline).group(1)
    log('initTags', 'Found tag: ' + tid + ' - ' + tag)

    if (', NULL)' not in tagline):
      parentId = re.search(', (\d+)\\);', tagline).group(1)
      tagMap[int(parentId)][2].append(int(tid))
      tagMap[int(tid)] = (tag, int(parentId), [])
    else:
      tagMap[int(tid)] = (tag, None, [])
      

def printTags():
  tagsPrinted = []
  for tid in tagMap:
    printTagsHelper(tid, 0, tagsPrinted)


def printTagsHelper(tid, level, tagsPrinted):
  if tid not in tagsPrinted:
    for i in range(0,level):
      print '  ',
    print str(tid) + ': ' + tagMap[tid][0]
    tagsPrinted.append(tid)
    if len(tagMap[tid][2]) > 0:
      for childid in tagMap[tid][2]:
        printTagsHelper(childid, level+1, tagsPrinted)


def deprecatedInitTags():
  global tagMap, longestTagLength

  log('initTags', '>> Initializing tag map <<')
  taglines = tagFile.readlines()
  for tagline in taglines:
    tid = re.search('VALUES \\((\d+),', tagline).group(1)
    tag = re.search(", '([0-9a-zA-Z/\(\)\.\- ']+)', ", tagline).group(1)
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


def deprecatedPrintTags():
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


def printTagsForMovie(mid, title):
  tgt = open(tagGivenToFilename, 'r')
  relevantLines = []
  tids = []
  tags = []

  #get lines from tag_given_to file with given movie id
  while True:
    line = tgt.readline()
    #eof
    if '' == line:
      tgt.close()
      break
    #keep lines only containing the given movie id
    if 'VALUES(' + str(mid) + ',' in line:
      relevantLines.append(line.rstrip())
  #extract tag ids
  for line in relevantLines:
    tid = re.search('VALUES\\(\d+, (\d+)', line).group(1)
    tids.append(int(tid))

  sys.stdout.write('\n"' + title + '" is already tagged with: ')

  #for each tid, get the corresponding tag value from tagMap and print
  for tid in tids[:-1]:
    sys.stdout.write(tagMap[tid][0] + ' (' + str(tid) + '), ')
  sys.stdout.write(tagMap[tids[-1]][0] + ' (' + str(tids[-1]) + ')\n\n')


def writeSql(movie, tid):
  log('writeSql', 'writing sql: INSERT INTO tag_given_to VALUES(' + str(movie['mid']) + ', ' + str(tid) + ');')
  tagGivenToFile.write('INSERT INTO tag_given_to VALUES(' + str(movie['mid']) + ', ' + str(tid) + ');  -- ' + movie['title'] + ' (' + str(movie['year']) + ') tagged with \'' + tagMap[tid][0] + '\'\n')


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


def addTag(tag, parentId):
  global tagMap, nextTid

  log('addTag', 'writing sql: INSERT INTO tag VALUES(' + str(nextTid) + ', \'' + tag + '\', ' + str(parentId) + ');')
  tagFile.write('INSERT INTO tag VALUES (' + str(nextTid) + ', \'' + tag + '\', ' + str(parentId) + ');\n')
  if parentId != 'NULL':
    tagMap[parentId][2].append(nextTid)
    tagMap[nextTid] = (tag, parentId, [])
  else:
    tagMap[nextTid] = (tag, None, [])
  nextTid = nextTid + 1


def addTagUI():
  while(True):
    tag = raw_input('\nEnter new tag: ')
    while(True):
      confirm = raw_input('Is this what you wanted: ' + tag + ' (y/n)? ').lower()
      if 'y' == confirm:
        while(True):
          parentId = raw_input('Parent tag id (just leave blank if none): ')
          try:
            if parentId == '':
              parentId = 'NULL'
              break
            parentId = int(parentId)
            if parentId < 1 or parentId > len(tagMap):
              raise ValueError
            break
          except ValueError:
            print '\n**Only numeric values between 1 and ' + len(tagMap) + '.'
            continue
        log('addTagUI', 'User wants to add tag \'' + tag + '\' with parent id: ' + str(parentId))
        addTag(tag, parentId)
        return
      elif 'n' == confirm:
        break
      else:
        print '\n**Only \'y\' or \'n\' please.'


def addParentTagIds(tids):
  for tid in tids:
    addParentTagIdsHelper(tid, tids)
  return tids


def addParentTagIdsHelper(tid, tids):
  parentId = tagMap[tid][1]
  if parentId != None:
    if parentId not in tids:
      tids.append(parentId)
      log('addParentTagIdsHelper', 'Auto-adding parent tag \'' + tagMap[parentId][0] + '\' for tag \'' + tagMap[tid][0] + '\'')
    addParentTagIdsHelper(parentId, tids)


def inquireMovie(movie):
  try:
    log('inquireMovie', 'MOVIE: ' + movie['title'] + ' (' + str(movie['year']) + ')')
    print '\n--------------------------------------------------------------------'
    print '\nMOVIE: [' + str(movie['mid']) + '] ' + movie['title'] + ' (' + str(movie['year']) + ')\n'
    printTags()
    printTagsForMovie(movie['mid'], movie['title'])
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
        log('inquireMovie', 'user entered tag(s): ' + str(tids))
        tids = addParentTagIds(tids)
      except ValueError:
        print '\n**Only numeric values from 1 to ' + str(len(tagMap))
        continue
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

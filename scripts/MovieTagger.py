#/usr/bin/env python

import string
import sys
import re
from QuitException import QuitException


class MovieTagger(object):

  def __init__(self, tagGivenToSqlFilePath, tagSqlFilePath, logFile):
    ''' Initialization

        tagGivenToSqlFilePath (string) : name of the sql file from which to get tags already given to movies
        tagSqlFilePath (string) : name of the sql file from which to init the tag map
        logFile (file) : file to write log statements to
    '''
    self._tagGivenToInserts = []  #sql insert statements for the tag_given_to db table
    self._tagInserts = []         #sql insert statements for the tag db table
    self._tagGivenToSqlFilePath = tagGivenToSqlFilePath
    self._logFile = logFile
    self._tagMap = {}             #tid -> (tag, parent id, [child tids])
    self._initTagMap(tagSqlFilePath)
    self._nextTid = len(self._tagMap) + 1
    self._existingTagIds = []     #tids for movies already tagged


  #----------------------------------------------------------------------------

  def _initTagMap(self, tagSqlFilePath):
    ''' Initialize the tag map (tag id -> (tag name, parent tag id, [child tag ids]) )
    '''
    self._log('_initTagMap', '>> Initializing tag map <<')
    tagSqlFile = open(tagSqlFilePath, 'r')
    taglines = tagSqlFile.readlines()
    tagSqlFile.close()
    for tagline in taglines:
      tid = re.search('VALUES \\((\d+),', tagline).group(1)
      tag = re.search(", '([0-9a-zA-Z/\(\)\.\- ']+)', ", tagline).group(1)
      #uncomment for logging all tags found
      #self._log('_initTagMap', 'Found tag: ' + tid + ' - ' + tag)

      if (', NULL)' not in tagline):
        parentId = re.search(', (\d+)\\);', tagline).group(1)
        self._tagMap[int(parentId)][2].append(int(tid))
        self._tagMap[int(tid)] = (tag, int(parentId), [])
      else:
        self._tagMap[int(tid)] = (tag, None, [])


  #----------------------------------------------------------------------------

  def _log(self, func, message):
    ''' Writes a message to the log file

        func (string) : name of the function current execution is in at the time of this log entry
        message (string) : log entry message
    '''
    self._logFile.write('[MovieTagger.' + func + '] - ' + message + '\n')


  #----------------------------------------------------------------------------

  def _extractTagIds(self, userInput):
    ''' Extracts numeric tag ids from the given string

        userInput (string) : expected comma-separated list of ids

        Returns [int] : a list of tag ids
        Raises : ValueError when string does not contain a valid number
                 (non-numeric or not within the range of tag ids)
    '''
    tids = userInput.split(',')
    map(string.strip, tids)
    tids = map(int, tids)
    for tid in tids:
      if tid < 1 or tid > len(self._tagMap):
        raise ValueError
    return tids


  #----------------------------------------------------------------------------

  def _createTagGivenToSql(self, mid, title, year, tid):
    ''' Creates a SQL INSERT statement with the given tag id and movie for the
        tag_given_to database table and appends to the list of sql statements.

        mid (int) : the movie id
        title (string) : the title of the movie
        year (int) : the year of the movie
        tid (int) : the tag id
    '''
    insertStatement = 'INSERT INTO filth.tag_given_to VALUES(' + str(mid) + ', ' + str(tid) + ');  -- ' + str(title) + ' (' + str(year) + ') tagged with \'' + self._tagMap[tid][0] + '\''
    self._log('_createTagGivenToSql', 'writing sql: ' + insertStatement)
    self._tagGivenToInserts.append(insertStatement)


  #----------------------------------------------------------------------------

  def _addTag(self, tag, parentId):
    ''' Creates a SQL INSERT statement for the new tag to the corresponding sql
        file for the tag database table, and adds the tag to the tag map.

        tag (string) : the tag name
        parentId (string) : the id of the tag's parent tag (or 'NULL')
    '''
    insertStatement = "INSERT INTO filth.tag VALUES ({0}, '{1}', {2});".format(str(self._nextTid), tag, str(parentId))
    self._log('_addTag', 'writing sql: ' + insertStatement)
    self._tagInserts.append(insertStatement)
    #add the tag to the map
    if parentId != 'NULL':
      self._tagMap[parentId][2].append(self._nextTid)
      self._tagMap[self._nextTid] = (tag, parentId, [])
    else:
      self._tagMap[self._nextTid] = (tag, None, [])
    print '\nTag "' + tag + '" added\n'
    #update the next tid
    self._nextTid = self._nextTid + 1


  #----------------------------------------------------------------------------

  def _promptUserAddingTag(self):
    ''' Interacts with the user to get a new tag name.
    '''
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
              if parentId < 1 or parentId > len(self._tagMap):
                raise ValueError
              break
            except ValueError:
              print '\n**Only numeric values between 1 and ' + len(self._tagMap) + '.'
              continue
          self._log('_promptUserAddingTag', 'User wants to add tag \'' + tag + '\' with parent id: ' + str(parentId))
          self._addTag(tag, parentId)
          return
        elif 'n' == confirm:
          break
        else:
          print '\n**Only \'y\' or \'n\' please.'


  #----------------------------------------------------------------------------

  def promptUserForTag(self, mid, title, year):
    ''' Interacts with the user to attach tags to the given movie.

        mid (int) : the movie id
        title (string) : the title of the movie
        year (int) : the year of the movie

        Raises QuitException when user quits
               Exception when an unknown error occurs
    '''
    self._log('_promptUserForTag', '*** Tagging movie: "' + str(title) + '" (' + str(year) + ') ***')
    while(True):
      self._printTags()
      print '\n--------------------------------------------------------------------'
      print '\nMOVIE: [' + str(mid) + '] ' + title + ' (' + str(year) + ')\n'
      self._printTagsForMovie(mid, title)
      print 'You may enter \'q\' to quit, \'s\' to skip the current movie, \'add\' to add a new tag, or any number of tags as a comma-separated list (e.g. "1,3,5").'
      response = raw_input('Enter tags: ').lower()
      if response == 'q':
        raise QuitException('user quit')
      elif response == 's':
        self._log('_promptUserForTag', 'User is skipping \'' + str(title) + '\' (' + str(year) + ')')
        print '\nSkipping...\n'
        if len(self._existingTagIds) > 0:
          tids = self._addParentTagIds(self._existingTagIds)
          for tid in tids:
            if tid not in self._existingTagIds:
              self._createTagGivenToSql(mid, title, year, tid)
        return
      elif response == 'add':
        self._promptUserAddingTag()
        continue
      else:
        try:
          tids = self._extractTagIds(response)
        except ValueError:
          print '\n**Only numeric values from 1 to ' + str(len(self._tagMap))
          raw_input('HIT ENTER KEY TO CONTINUE')
          continue
        self._log('_promptUserForTag', 'user entered tag(s): ' + str(map(lambda t : (t, self._tagMap[t][0]), tids)))
        tids.extend(self._existingTagIds)
        tids = self._removeDuplicates(tids)
        tids = self._addParentTagIds(tids)
        for tid in tids:
          if tid not in self._existingTagIds:
            self._createTagGivenToSql(mid, title, year, tid)
        break


  #----------------------------------------------------------------------------

  def _printTags(self):
    tagsPrinted = []
    for tid in self._tagMap:
      self._printTagsHelper(tid, 0, tagsPrinted)


  #----------------------------------------------------------------------------

  def _printTagsHelper(self, tid, level, tagsPrinted):
    if tid not in tagsPrinted:
      for i in range(0,level):
        print '  ',
      print str(tid) + ': ' + self._tagMap[tid][0]
      tagsPrinted.append(tid)
      if len(self._tagMap[tid][2]) > 0:
        for childid in self._tagMap[tid][2]:
          self._printTagsHelper(childid, level+1, tagsPrinted)


  #----------------------------------------------------------------------------


  def _printTagsForMovie(self, mid, title):
    tagGivenToFile = open(self._tagGivenToSqlFilePath, 'r')
    relevantLines = []
    tids = []
    tags = []

    #get lines from tag_given_to file with given movie id
    while True:
      line = tagGivenToFile.readline()
      #eof
      if '' == line:
        tagGivenToFile.close()
        break
      #keep lines only containing the given movie id
      if 'VALUES(' + str(mid) + ',' in line:
        relevantLines.append(line.rstrip())
    #extract tag ids
    for line in relevantLines:
      tid = re.search('VALUES\\(\d+, (\d+)', line).group(1)
      self._log('_printTagsForMovie', 'found existing tag for movie (' + str(mid) + ': "' + title + '"): "' + self._tagMap[int(tid)][0] + '" (' + str(tid) + ')')
      tids.append(int(tid))

    self._existingTagIds = tids

    if len(tids) > 0:
      sys.stdout.write('\n"' + title + '" (' + str(mid) + ') is already tagged with: ')

      #for each tid, get the corresponding tag value from tagMap and print
      for tid in tids[:-1]:
        sys.stdout.write(self._tagMap[tid][0] + ' (' + str(tid) + '), ')
      sys.stdout.write(self._tagMap[tids[-1]][0] + ' (' + str(tids[-1]) + ')\n\n')


  #----------------------------------------------------------------------------

  def _addParentTagIds(self, tids):
    newTidList = list(tids)
    for tid in newTidList:
      self._addParentTagIdsHelper(tid, newTidList)
    return newTidList


  #----------------------------------------------------------------------------

  def _addParentTagIdsHelper(self, tid, tids):
    parentId = self._tagMap[tid][1]
    if parentId != None:
      if parentId not in tids:
        tids.append(parentId)
        self._log('_addParentTagIdsHelper', 'Auto-adding parent tag \'' + self._tagMap[parentId][0] + '\' for tag \'' + self._tagMap[tid][0] + '\'')
      self._addParentTagIdsHelper(parentId, tids)


  #----------------------------------------------------------------------------

  def _removeDuplicates(self, tids):
    ids = []
    for tid in tids:
      if tid not in ids:
        ids.append(tid)
    return ids


  #----------------------------------------------------------------------------

  def writeTagInsertsToFile(self, tagSqlFile):
    for statement in self._tagInserts:
      tagSqlFile.write(statement + '\n')


  #----------------------------------------------------------------------------

  def writeTagGivenToInsertsToFile(self, tgtSqlFile):
    for statement in self._tagGivenToInserts:
      tgtSqlFile.write(statement + '\n')


  #----------------------------------------------------------------------------

  def hasInserts(self):
    return len(self._tagGivenToInserts) > 0 or len(self._tagInserts) > 0


  #----------------------------------------------------------------------------

  def close(self):
    ''' Empties the tag list and tag map, and insert statements
    '''
    self._tags = []
    self._tagMap = {}
    self._tagGivenToInserts = []
    self._tagInserts = []

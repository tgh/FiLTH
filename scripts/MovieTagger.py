#/usr/bin/env python

import string
import sys
from QuitException import QuitException


class MovieTagger(object):

  def __init__(self, tagGivenToSqlFilePath, tagSqlFilePath, logFile, models):
    ''' Initialization

        tagGivenToSqlFilePath (string) : name of the sql file to write inserts for the tag_given_to db table
        tagSqlFilePath (string) : name of the sql file to write inserts for the tag db table
        logFile (file) : file to write log statements to
        models (module) : module of SQLAlchemy data model objects for the FiLTH database
    '''
    self._tagGivenToInserts = []  #sql insert statements for the tag_given_to db table
    self._tagInserts = []         #sql insert statements for the tag db table
    self._tgtSqlFile = None
    self._tagSqlFile = None
    self._logFile = logFile
    self._models = models
    self._openFiles(tagGivenToSqlFilePath, tagSqlFilePath)
    self._tagMap = {}
    self._initTagMap()
    self._longestTagLength = self._determineLongestTagLength()


  #----------------------------------------------------------------------------

  def _openFiles(self, tagGivenToSqlFilePath, tagSqlFilePath):
    ''' Attempts to open the sql files to append to by the given file names

        tagGivenToSqlFilePath (string) : name of the sql file to write inserts for the tag_given_to db table
        tagSqlFilePath (string) : name of the sql file to write inserts for the tag db table

        Raises : IOError when there is a problem opening one of the files
    '''
    try:
      self._tgtSqlFile = open(tagGivenToSqlFilePath, 'w')
      self._tagSqlFile = open(tagSqlFilePath, 'w')
    except IOError as e:
      sys.stderr.write("**ERROR: opening file: " + str(e) + ".\n")
      self.close()


  #----------------------------------------------------------------------------

  def _initTagMap(self):
    ''' Initialize a map from tag ids to tag names
    '''
    for tag in self._models.Tag.query.order_by(self._models.Tag.tid).all():
      self._tagMap[int(tag.tid)] = str(tag.tag_name)


  #----------------------------------------------------------------------------

  def _determineLongestTagLength(self):
    ''' Iterates over the tags and returns the length of the longest tag

        Returns (int) : the length of the longest tag string
    '''
    longestLen = 0
    for tag in self._tagMap.values():
      if len(tag) > longestLen:
        longestLen = len(tag)
    
    return longestLen


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
        tag_given_to database table and appens to the list of sql statements.

        mid (int) : the movie id
        title (string) : the title of the movie
        year (int) : the year of the movie
        tid (int) : the tag id
    '''
    insertStatement = 'INSERT INTO tag_given_to VALUES(' + str(mid) + ', ' + str(tid) + ');  -- ' + str(title) + ' (' + str(year) + ') tagged with \'' + self._tagMap[tid] + '\''
    self._log('createTagGivenToSql', 'writing sql: ' + insertStatement)
    self._tagGivenToInserts.append(insertStatement)


  #----------------------------------------------------------------------------

  def _addTag(self, tag):
    ''' Creates a SQL INSERT statement for the new tag to the corresponding sql
        file for the tag database table, and adds the tag to the tag map.

        tag (string) : the tag name
    '''
    insertStatement = 'INSERT INTO tag VALUES (DEFAULT, \'' + tag + '\');'
    self._log('_addTag', 'writing sql: ' + insertStatement)
    self._tagInserts.append(insertStatement)
    #add the tag to the map
    self._tagMap[len(self._tagMap) + 1] = tag
    print '\nTag "' + tag + '" added\n'
    #update the longest tag length if applicable
    if len(tag) > self._longestTagLength:
      self._longestTagLength = len(tag)


  #----------------------------------------------------------------------------

  def _promptUserAddingTag(self):
    ''' Interacts with the user to get a new tag name.
    '''
    while(True):
      tag = raw_input('\nEnter new tag: ')
      while(True):
        confirm = raw_input('Is this what you wanted: ' + tag + ' (y/n)? ').lower()
        if 'y' == confirm:
          self._log('_promptUserAddingTag', 'User wants to add tag \'' + tag + '\'')
          self._addTag(tag)
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
    self._log('promptUserForTag', '*** Tagging movie: "' + str(title) + '" (' + str(year) + ') ***')
    self._printTags()
    print 'You may enter \'q\' to quit, \'skip\' to skip the current movie, \'add\' to add a new tag, or any number of tags as a comma-separated list (e.g. "1,3,5").'
    while(True):
      try:
        response = raw_input('Enter tags: ').lower()
        if response == 'q':
          raise QuitException('user quit')
        if response == 'skip':
          self._log('promptUserForTag', 'User is skipping \'' + str(title) + '\'')
          print '\nSkipping...\n'
          return
        if response == 'add':
          self._promptUserAddingTag()
          self._printTags()
          continue
        tids = self._extractTagIds(response)
      except ValueError:
        print '\n**Only numeric values from 1 to ' + str(len(self._tagMap))
        continue
      self._log('promptUserForTag', 'user entered tag(s): ' + str(map(lambda t : (t, self._tagMap[t]), tids)))
      for tid in tids:
        self._createTagGivenToSql(mid, title, year, tid)
      break


  #----------------------------------------------------------------------------

  def _printTags(self):
    ''' Pretty-prints all tags with their tag ids.
    '''
    padding = '  '
    for item in self._tagMap.items():
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
      longestLengthDiff = self._longestTagLength - len(tag)
      for i in range(0,longestLengthDiff):
        sys.stdout.write(' ')
      sys.stdout.write(padding)
    if len(self._tagMap) % 3 != 0:
      print '\n'


  #----------------------------------------------------------------------------

  def flush(self):
    ''' Writes out all sql statements to their respective files.
    '''
    for statement in self._tagGivenToInserts:
      self._tgtSqlFile.write(statement + '\n')
    for statement in self._tagInserts:
      self._tagSqlFile.write(statement + '\n')


  #----------------------------------------------------------------------------

  def close(self):
    ''' Closes the sql files properly and empties the tag list and tag map.
    '''
    self._tags = []
    self._tagMap = {}
    if self._tgtSqlFile:
      self._tgtSqlFile.close()
    if self._tagSqlFile:
      self._tagSqlFile.close()

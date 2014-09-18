#/usr/bin/env python

import string
import sys
from QuitException import QuitException


class MovieTagger(object):

  def __init__(self, tagGivenToSqlFilePath, tagSqlFilePath, logFile):
    ''' Initialization

        tagGivenToSqlFilePath (string) : name of the sql file to write inserts for the tag_given_to db table
        tagSqlFilePath (string) : name of the sql file to write inserts for the tag db table
        logFile (file) : file to write log statements to
    '''
    self._tagGivenToInserts = []  #sql insert statements for the tag_given_to db table
    self._tagInserts = []         #sql insert statements for the tag db table
    self._tgtSqlFile = None
    self._tagSqlFile = None
    self._logFile = logFile
    self._openFiles(tagGivenToSqlFilePath, tagSqlFilePath)
    self._tagMap = {}             #tid -> (tag, parent id, [child tids])
    self._initTagMap()
    self._nextTid = len(self._tagMap) + 1


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
      self._log('_openFiles', '**ERROR: opening file: ' + str(e) + '.\n')
      sys.stderr.write("**ERROR: opening file: " + str(e) + ".\n")
      self.close()


  #----------------------------------------------------------------------------

  def _initTagMap(self):
    ''' Initialize a map from tag ids to tag names
    '''
    pass #init tags from file


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
      if tid < 1 or tid >= self._nextTid:
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
    insertStatement = "INSERT INTO tag VALUES ({0}, '{1}');".format(str(self._nextTid), tag)
    self._log('_addTag', 'writing sql: ' + insertStatement)
    self._tagInserts.append(insertStatement)
    #add the tag to the map
    self._tagMap[self._nextTid] = tag
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

  def _printTags():
    tagsPrinted = []
    for tid in self._tagMap:
      self._printTagsHelper(tid, 0, tagsPrinted)


  #----------------------------------------------------------------------------

  def _printTagsHelper(tid, level, tagsPrinted):
    if tid not in tagsPrinted:
      for i in range(0,level):
        print '  ',
      print str(tid) + ': ' + self._tagMap[tid][0]
      tagsPrinted.append(tid)
      if len(self._tagMap[tid][2]) > 0:
        for childid in self._tagMap[tid][2]:
          printTagsHelper(childid, level+1, tagsPrinted)


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

#/usr/bin/env python

import imp
import string
from QuitException import QuitException

FILTH_PATH = '/home/tgh/workspace/FiLTH'
models = imp.load_source('models', FILTH_PATH + '/src/python/models.py')


class MovieTagger(object):

  def __init__(self, tagGivenToSqlFilePath, tagSqlFilePath, logFile, session):
    ''' Initialization

        tagGivenToSqlFilePath (string) : name of the sql file to write inserts for the tag_given_to db table
        tagSqlFilePath (string) : name of the sql file to write inserts for the tag db table
        logFile (file) : file to write log statements to
        session (sqlalchemy.orm.session.Session) : SQLAlchemy session to commit new tags to
    '''
    self._tags = models.Tag.query.order_by(models.Tag.tid).all()
    self._tgtSqlFile = None
    self._tagSqlFile = None
    self._logFile = logFile
    self._session = session
    self._openFiles(tagGivenToSqlFilePath, tagSqlFilePath)
    self._tagMap = {}
    self._initTagMap()


  #----------------------------------------------------------------------------

  def _openFiles(self, tagGivenToSqlFilePath, tagSqlFilePath):
    ''' Attempts to open the sql files to append to by the given file names

        tagGivenToSqlFilePath (string) : name of the sql file to write inserts for the tag_given_to db table
        tagSqlFilePath (string) : name of the sql file to write inserts for the tag db table

        Throws : IOError when there is a problem opening one of the files
    '''
    try:
      self._tgtSqlFile = open(tagGivenToSqlFilePath, 'a')
      self._tagSqlFile = open(tagSqlFilePath, 'a')
    except IOError as e:
      sys.stderr.write("**ERROR: opening file: " + str(e) + ".\n")
      self.close()


  #----------------------------------------------------------------------------

  def _initTagMap(self):
    ''' Initialize a map from tag ids to tag names
    '''
    for tag in self._tags:
      self._tagMap[int(tag.tid)] = str(tag.tag_name)


  #----------------------------------------------------------------------------

  def _log(self, func, message):
    ''' Writes a message to the log file
    '''
    self._logFile.write('[MovieTagger.' + func + '] - ' + message + '\n')


  #----------------------------------------------------------------------------

  def _extractTagIds(self, userInput):
    ''' Extracts numeric tag ids from the given string

        userInput (string) : expected comma-separated list of ids

        Returns [int] : a list of tag ids
        Throws : ValueError when string does not contain a valid number
                 (non-numeric or not within the range of tag ids)
    '''
    tids = userInput.split(',')
    map(string.strip, tids)
    tids = map(int, tids)
    for tid in tids:
      if tid < 1 or tid > len(self._tags):
        raise ValueError
    return tids


  #----------------------------------------------------------------------------

  def _writeTagGivenToSql(self, movie, tid):
    ''' Writes a SQL INSERT statement with the given tag id and movie for the
        tag_given_to database table to the corresponding sql file.

        movie (models.Movie) : the movie being tagged
        tid (int) : the tag id
    '''
    self._log('writeTagGivenToSql', 'writing sql: INSERT INTO tag_given_to VALUES(' + str(movie.mid) + ', ' + str(tid) + ');')
    self._tgtSqlFile.write('INSERT INTO tag_given_to VALUES(' + str(movie.mid) + ', ' + str(tid) + ');  -- ' + str(movie.title) + ' (' + str(movie.year) + ') tagged with \'' + self._tagMap[tid] + '\'\n')


  #----------------------------------------------------------------------------

  def _addTag(self, tag):
    ''' Adds a new tag to the database as well as writing a SQL INSERT statement
        for the new tag to the corresponding sql file for the tag database table

        tag (string) : the tag name

        Returns models.Tag : the new tag as a Tag object
    '''
    self._log('_addTag', 'writing sql: INSERT INTO tag VALUES(DEFAULT, \'' + tag + '\');')
    self._tagSqlFile.write('INSERT INTO tag VALUES (DEFAULT, \'' + tag + '\');\n')
    self._log('_addTag', 'adding tag, \'' + tag + '\', to the database...')
    newTag = models.Tag(tag_name=tag)
    this._session.add(newTag)
    this._session.commit()
    self._log('_addTag', 'tag, \'' + tag + '\', added to database')
    return newTag


  #----------------------------------------------------------------------------

  def _promptUserAddingTag(self):
    ''' Interacts with the user to get a new tag name.

        Returns models.Tag : the new tag as a Tag object
    '''
    while(True):
      tag = raw_input('\nEnter new tag: ')
      while(True):
        confirm = raw_input('Is this what you wanted: ' + tag + ' (y/n)? ').lower()
        if 'y' == confirm:
          self._log('_promptUserAddingTag', 'User wants to add tag \'' + tag + '\'')
          return self._addTag(tag)
        elif 'n' == confirm:
          break
        else:
          print '\n**Only \'y\' or \'n\' please.'


  #----------------------------------------------------------------------------

  def _updateTags(self, tid, tag):
    ''' Refreshes the tag list by loading all tags from the database and adds
        the given tag to the tag map.

        tid (int) : a tag id
        tag (string) : a tag
    '''
    self._tags = models.Tag.query.order_by(models.Tag.tid).all()
    self._tagMap[tid] = tag
    self._log('_updateTags', 'tags updated with \'' + tag + '\'')


  #----------------------------------------------------------------------------

  def promptUserForTag(self, movie):
    ''' Interacts with the user to attach tags to the given movie.

        movie (models.Movie) : the movie in which tags are being applied

        Throws QuitException when user quits
               Exception when an unknown error occurs
    '''
    try:
      self._printTags()
      print 'You may enter \'q\' to quit, \'skip\' to skip the current movie, \'add\' to add a new tag, or any number of tags as a comma-separated list (e.g. "0,3,5").'
      while(True):
        try:
          response = raw_input('Enter tags: ').lower()
          if response == 'q':
            self.close()
            raise QuitException('user quit')
          if response == 'skip':
            print 'Skipping...\n'
            return
          if response == 'add':
            tag = self._promptUserAddingTag()
            self._updateTags(int(tag.tid), str(tag.tag_name))
            self._printTags()
            continue
          tids = self._extractTagIds(response)
        except ValueError:
          print '\n**Only numeric values from 0 to ' + str(len(self._tags)-1)
          continue
        self._log('promptUserForTag', 'user entered tag(s): ' + str(tids))
        for tid in tids:
          self._writeTagGivenToSql(movie, tid)
        break
    except Exception as e:
      self.close()
      raise e


  #----------------------------------------------------------------------------

  def _printTags(self):
    ''' Pretty-prints all tags with their tag ids.
    '''
    count = 1
    prevLength = 0
    for tag in self._tags:
      if count % 2 == 1:
        print '  ' + str(tag.tid) + ' = ' + str(tag.tag_name),
        prevLength = len(tag.tag_name)
      else:
        if prevLength >= 25:
          print '\t' + str(tag.tid) + ' = ' + str(tag.tag_name)
        elif prevLength >= 18:
          print '\t\t' + str(tag.tid) + ' = ' + str(tag.tag_name)
        elif prevLength >= 8 and int(tag.tid) > 9:
          print '\t\t\t' + str(tag.tid) + ' = ' + str(tag.tag_name)
        else:
          print '\t\t\t\t' + str(tag.tid) + ' = ' + str(tag.tag_name)
      count += 1
    if len(self._tags) % 2 == 1:
      print '\n'


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

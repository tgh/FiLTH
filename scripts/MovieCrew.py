#!/usr/bin/env python

import sys
import string
import re
from QuitException import QuitException

POSITIONS_FILE_PATH = "/home/thayes/Projects/FiLTH/sql/position.sql"


class MovieCrew(object):

  def __init__(self, workedOnSqlFilePath, crewSqlFilePath, logFile):
    ''' Initialization

        workedOnSqlFilePath (string) : name of the sql file from which to read existing worked on relationships
        crewSqlFilePath (string) : name of the sql file from which to read existing crew members
        logFile (file) : file to write log statements to
    '''
    self._crewInserts = []        # sql INSERT statements for the crew_person table
    self._workedOnInserts = []    # sql INSERT statements for the worked_on table
    self._logFile = logFile
    self._workedOnSqlFilePath = workedOnSqlFilePath
    self._positions = []
    self._initPositions()
    self._crewMap = {}            # full name (string) -> cid (int)
    self._initCrewMap(crewSqlFilePath)
    self._nextCid = len(self._crewMap) + 1


  #----------------------------------------------------------------------------

  def _initPositions(self):
    positionSqlFile = open(POSITIONS_FILE_PATH, 'r')
    lines = positionSqlFile.readlines()
    positionSqlFile.close()
    for line in lines:
      position = re.search("VALUES\\('([a-zA-Z ]+)'", line).group(1)
      self._positions.append(position)


  #----------------------------------------------------------------------------

  def _initCrewMap(self, crewSqlFilepath):
    self._log('_initCrewMap', '>> Initializing crew map <<')
    crewSqlFile = open(crewSqlFilepath, 'r')
    lines = crewSqlFile.readlines()
    crewSqlFile.close()
    for line in lines:
      matcher = re.search("(\d+), ([a-zA-Z' \-\.]+), ([a-zA-Z' \-\.]+), ([a-zA-Z' \-\.]+), ", line)
      cid = int(matcher.group(1))
      lastName = self._sanitizeName(matcher.group(2))
      firstName = self._sanitizeName(matcher.group(3))
      middleName = self._sanitizeName(matcher.group(4))
      fullName = " ".join([firstName, middleName, lastName]).replace('  ', ' ').rstrip()
      self._crewMap[fullName] = cid
      self._log('_initCrewMap', 'crew person: ' + fullName + ' (' + str(cid) + ')')


  #----------------------------------------------------------------------------

  def _sanitizeName(self, name):
    if name in ['NULL', 'DEFAULT']:
      return ''
    return name.strip("'")


  #----------------------------------------------------------------------------

  def _log(self, func, message):
    ''' Writes a message to the log file

        func (string) : name of the function current execution is in at the time of this log entry
        message (string) : log entry message
    '''
    self._logFile.write('[MovieCrew.' + func + '] - ' + message + '\n')


  #----------------------------------------------------------------------------

  def _createInsertStatementForCrew(self, last, first, middle, fullName, position):
    ''' Creates a SQL INSERT statement for the crew_person db table for the
        given new crew person and appends to the list of crew_person INSERT
        statements.

        last (string) : last name
        first (string) : first name (with the appropriate surrounding apostrophes if applicable)
        middle (string) : middle name (with the appropriate surrounding apostrophes if applicable)
        fullName (string): full name
        position (string) : the position name
    '''
    insertStatement = "INSERT INTO crew_person VALUES ({0}, '{1}', {2}, {3}, '{4}');  -- {4}: {5}".format(str(self._nextCid), last, first, middle, position, fullName)
    self._log('_createInsertStatementForCrew', 'created SQL: ' + insertStatement)
    self._crewInserts.append(insertStatement)


  #----------------------------------------------------------------------------

  def _createInsertStatementForWorkedOn(self, mid, cid, position, name, title, year):
    ''' Creates a SQL INSERT statement for the worked_on db table with the given
        values and appends to the list of worked_on INSERT statements.

        mid (int) : the database primary key value of the movie
        cid (int) : crew person id
        position (string) : the name of the position the crew person worked as on the movie
        name (string) : full name of the person
        title (string) : title of the movie
        year (int) : year of the movie
    '''
    insertStatement = "INSERT INTO worked_on VALUES({0}, {1}, '{2}');  -- {3} for {4} ({5})".format(str(mid),\
                      str(cid), position, name, title, str(year))
    insertStatement = insertStatement.replace('NULL ', '')
    self._log('_createInsertStatementForWorkedOn', 'created SQL: ' + insertStatement)
    self._workedOnInserts.append(insertStatement)


  #----------------------------------------------------------------------------

  def _promptUserForPosition(self, prompt):
    ''' Prompts the use for a number corresponding to a position printed to the
        screen.

        prompt (string) : the prompt to display to the user

        Returns int : the number the user entered
    '''
    self._printPositions()
    while True:
      response = raw_input(prompt)
      self._checkForQuit(response, '_promptUserForPosition')
      try:
        num = self._parseIntInRangeInclusive(response, 1, len(self._positions))
        return num
      except ValueError:
        continue


  #----------------------------------------------------------------------------

  def _promptUserForCrewPersonHelper(self, mid, title, year):
    ''' Prompts the user for a crew person

        mid (int) : the database primary key value of the movie
        title (string) : title of the movie
        year (int) : year of the movie
    '''
    #prompt user for a valid person name
    while True:
      response = raw_input('\nEnter the name of someone who worked on this movie (or \'quit\' at anytime): ')
      self._checkForQuit(response, '_promptUserForCrewPersonHelper')
      matcher = re.search("[^a-zA-Z '\-\.]", response)
      if matcher != None:
        print '**Input error: the name you entered contains at least one character not expected in a name.\n'
      else:
        break
    
    name = response
    self._log('_promptUserForCrewPersonHelper', 'user entered crew person: ' + name)

    try:
      #get the id of the crew person
      cid = self._crewMap[name]
      self._log('_promptUserForCrewPersonHelper', 'crew person found with id of ' + str(cid))
    except KeyError:
      #crew person was not found, prompt if this is a new addition or a typo
      self._log('_promptUserForCrewPersonHelper', 'crew person not found')
      while True:
        response = raw_input('\nCrew person {0} not found. New person? (y/n/quit): '.format(name))
        self._checkForQuit(response, '_promptUserForCrewPersonHelper')
        if response.lower() not in ['y','n']:
          print '\n**Invalid entry: \'y\', \'n\', or \'quit\' please.\n'
          continue
        if response.lower() == 'n':
          print '\nLet\'s try this again, then...'
          self.promptUserForCrewPerson(mid, title, year)
          return
        #user entered 'y'
        break
      #end while

      self._log('_promptUserForCrewPersonHelper', 'this is a new crew person')
        
      #prompt user for what the person is known as
      num = self._promptUserForPosition('\nWhat is this person known as (1-' + str(len(self._positions)) + ') or \'quit\')? ')
      self._log('_promptUserForCrewPersonHelper', 'user entered ' + str(num) + '--new crew person is known as ' + self._positions[num-1])

      last   = None       #last name string for sql
      middle = 'NULL'     #middle name string for sql
      first  = 'NULL'     #first name string for sql
      nameList = name.split(' ')
      if len(nameList) == 3:
        last = nameList[2]
        middle = nameList[1]
        first = nameList[0]
      elif len(nameList) == 2:
        last = nameList[1]
        first = nameList[0]
      else:
        last = nameList[0]
      self._createInsertStatementForCrew(last, first, middle, name, self._positions[num-1])
      cid = self._nextCid
      self._crewMap[name] = cid
      self._log('_promptUserForCrewPersonHelper', 'new crew person has an id of ' + str(cid))
      self._nextCid += 1
    #end except KeyError

    #prompt user for what positions the person worked as
    pids = self._promptUserForWorkedAs(name, title, year)

    #create an SQL INSERT statement for each of those positions
    for pid in pids:
      self._createInsertStatementForWorkedOn(mid, cid, self._positions[pid-1], name, title, year)
    #end for


  #----------------------------------------------------------------------------

  def _promptUserForWorkedAs(self, name, title, year):
    ''' Prompts the user for positions that the person worked on for the movie

        name (string) : the name of the person
        title (string) : the title of the movie
        year (int) : the year of the movie

        Returns [int] : list of position ids
    '''
    while True:
      print '\nWhat positions did {0} work as in this movie?'.format(name)
      try:
        self._printPositions()
        print '\nYou may enter \'quit\', or any number of positions as a comma-separated list (e.g. "1,3,5").'.format(name)
        response = raw_input('Enter positions: ').lower()
        self._checkForQuit(response, '_promptUserForWorkedAs')
        pids = self._extractPositionIds(response)
      except ValueError:
        print '\n**Only numeric values from 1 to ' + str(len(self._positions))
        continue
      self._log('_promptUserForCrewPersonHelper', 'user entered ' + str(pids) + '--crew person worked on "' + title + '" (' + str(year) + ') as ' + str(map(lambda p : self._positions[p-1], pids)))
      return pids
    #end while


  #----------------------------------------------------------------------------

  def _extractPositionIds(self, userInput):
    ''' Extracts numeric position ids from the given string

        userInput (string) : expected comma-separated list of ids

        Returns [int] : a list of tag ids
        Raises : ValueError when string does not contain a valid number
                 (non-numeric or not within the range of position ids)
    '''
    pids = userInput.split(',')
    map(string.strip, pids)
    pids = map(int, pids)
    for pid in pids:
      if pid < 1 or pid > len(self._positions):
        raise ValueError
    return pids


  #----------------------------------------------------------------------------
  #PUBLIC
  #
  def promptUserForCrewPerson(self, mid, title, year):
    ''' Wrapper for prompting user for crew persons for a new movie
        
        mid (int) : the database primary key value of the movie
        title (string) : title of the movie
        year (int) : year of the movie

        Raises : QuitException when user quits
                 Exception when an unknown error occurs
    '''
    # print any existing worked-on relationships for this movie
    self._printWorkedOnRelationshipsForMovie(mid, title, year)

    # does the user even want to add crew members?
    while True:
      response = raw_input('\nAre there any crew members that you want to associate with this movie (y/n/quit)? ')
      if response.lower() not in ['y', 'n', 'quit']:
        print '\n**Invalid entry: \'y\', \'n\', or \'quit\' please.\n'
        continue
      if response.lower() == 'n':
        return
      if response.lower() == 'quit':
        self._quit('promptUserForCrewPerson')
        return
      break

    # prompt for crew members
    while True:
      self._promptUserForCrewPersonHelper(mid, title, year)
      while True:
        response = raw_input('\nAny more people work on this movie? (y/n/quit) ')
        self._checkForQuit(response, 'promptUserForCrewPerson')
        if response.lower() not in ['y', 'n', 'quit']:
          print '\n**Invalid entry: \'y\', \'n\', or \'quit\' please.\n'
          continue
        if response.lower() == 'y':
          break
        return


  #----------------------------------------------------------------------------

  def _printWorkedOnRelationshipsForMovie(self, mid, title, year):
    workedOnSqlFile = open(self._workedOnSqlFilePath, 'r')
    lines = workedOnSqlFile.readlines()
    workedOnSqlFile.close()

    cidsAndPositions = {}

    for line in lines:
      currentMid = int(re.search('VALUES\\((\d+),', line).group(1))
      if mid == currentMid:
        matcher = re.search('VALUES\\(\d+, (\d+), \'([a-zA-Z ]+)\'', line)
        cid = int(matcher.group(1))
        position = matcher.group(2)
        cidsAndPositions[cid] = position
    if len(cidsAndPositions) > 0:
      print '\n--------------------------------------------------------------------'
      print 'Existing worked-on relationships for ' + title + ' (' + str(year) + '):\n'
      for existingCid, position in cidsAndPositions.iteritems():
        for name, currentCid in self._crewMap.iteritems():
          if existingCid == currentCid:
            print ' - ' + name + ' (' + position + ')'


  #----------------------------------------------------------------------------

  def _parseIntInRangeInclusive(self, response, low, high):
    ''' Parses the given response string for an integer between the given range
        (inclusive).

        response (string) : a user's response text
        low (int) : minimum acceptable value
        high (int) : maximum acceptable value

        Returns int : the user's reponse as an integer
        Raises : ValueError if user entered an invalid response
    '''
    try:
      num = int(response)
      if (num > high or num < low):
        raise ValueError
      return num
    except ValueError as ve:
      print "\n**Invalid entry: '" + str(low) + "'-'" + str(high) + "', or 'quit', please."
      raise ve


  #----------------------------------------------------------------------------

  def _printPositions(self):
    ''' Pretty-prints all positions (numbered).
    '''
    print
    i = 1
    for position in self._positions:
      print '{0}. {1}'.format(str(i), position)
      i = i + 1


  #----------------------------------------------------------------------------

  def _checkForQuit(self, response, functionName):
    ''' Checks the given response string for "quit"

        response (string) : a user's response text
        functionName (string) : the function name of caller
    '''
    if response.lower() == 'quit':
      self._quit(functionName)


  #------------------------------------------------------------------------------

  def _quit(self, functionName):
    ''' This is called when the user enters "quit".  Log entry is written, and
        a QuitException is raised.

        Raises : QuitException
    '''
    self._log(functionName, 'quitting...')
    raise QuitException('user is quitting')


  #----------------------------------------------------------------------------
  #PUBLIC
  #
  def writeCrewInsertsToFile(self, crewSqlFile):
    for statement in self._crewInserts:
      crewSqlFile.write(statement + '\n')
    self._crewInserts = []


  #----------------------------------------------------------------------------
  #PUBLIC
  #
  def writeWorkedOnInsertsToFile(self, workedOnSqlFile):
    for statement in self._workedOnInserts:
      workedOnSqlFile.write(statement + '\n')
    self._workedOnInserts = []


  #----------------------------------------------------------------------------
  #PUBLIC
  #
  def hasInserts(self):
    return len(self._crewInserts) > 0 or len(self._workedOnInserts) > 0


  #----------------------------------------------------------------------------
  #PUBLIC
  #
  def close(self):
    self._workedOnInserts = []
    self._crewInserts = []

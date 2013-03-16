#!/usr/bin/env python

import sys
import string
import imp
from QuitException import QuitException
from sqlalchemy.orm.exc import NoResultFound

FILTH_PATH = '/home/tgh/workspace/FiLTH'
models = imp.load_source('models', FILTH_PATH + '/src/python/models.py')


class MovieCrew(object):

  def __init__(self, workedOnSqlFilePath, crewSqlFilePath, logFile, positions, nextCid):
    ''' Initialization

        workedOnSqlFilePath (string) : name of the sql file to write inserts for the worked_on db table
        crewSqlFilePath (string) : name of the sql file to write inserts for the crew_person db table
        logFile (file) : file to write log statements to
        positions ([string]) : list of crew person positions as strings
        nextCid (int) : the database id of crew_person for the next new crew person
    '''
    self._crewInserts = []        # sql INSERT statements for the crew_person table
    self._workedOnInserts = []    # sql INSERT statements for the worked_on table
    self._logFile = logFile
    self._positions = positions
    self._nextCid = nextCid
    self._openFiles(workedOnSqlFilePath, crewSqlFilePath)


  #----------------------------------------------------------------------------

  def _openFiles(self, workedOnSqlFilePath, crewSqlFilePath):
    ''' Attempts to open the sql files to append to by the given file names

        workedOnSqlFilePath (string) : name of the sql file to write inserts for the worked_on db table
        crewSqlFilePath (string) : name of the sql file to write inserts for the crew_person db table

        Raises : IOError when there is a problem opening one of the files
    '''
    try:
      self._workedOnSqlFile = open(workedOnSqlFilePath, 'a')
      self._crewSqlFile = open(crewSqlFilePath, 'a')
    except IOError as e:
      sys.stderr.write("**ERROR: opening file: " + str(e) + ".\n")
      self.close()


  #----------------------------------------------------------------------------

  def _log(self, func, message):
    ''' Writes a message to the log file

        func (string) : name of the function current execution is in at the time of this log entry
        message (string) : log entry message
    '''
    self._logFile.write('[MovieCrew.' + func + '] - ' + message + '\n')


  #----------------------------------------------------------------------------

  def _getCid(self, last, middle, first):
    ''' Returns the database id for the person with the given last name, middle
        name, and first name.

        last (string) : last name
        middle (string) : middle name
        first (string) : first name

        Returns int : the db id corresponding to the desired crew person
    '''
    first = first.strip("'")
    middle = middle.strip("'")
    last = last.strip("'")
    if first == 'NULL':
      first = None
    if middle == 'NULL':
      middle = None
    crew = models.CrewPerson.query.filter(models.CrewPerson.l_name == last)\
                                  .filter(models.CrewPerson.m_name == middle)\
                                  .filter(models.CrewPerson.f_name == first)\
                                  .one()
    return int(crew.cid)


  #----------------------------------------------------------------------------

  def _createInsertStatementForCrew(self, last, first, middle, position):
    ''' Creates a SQL INSERT statement for the crew_person db table for the
        given new crew person and appends to the list of crew_person INSERT
        statements.

        last (string) : last name (with the appropriate surrounding apostrophes if applicable)
        first (string) : first name (with the appropriate surrounding apostrophes if applicable)
        middle (string) : middle name (with the appropriate surrounding apostrophes if applicable)
        position (string) : the position name
    '''
    insertStatement = "INSERT INTO crew_person VALUES(DEFAULT, {0}, {1}, {2}, '{3}');".format(last, first, middle, position)
    self._log('_createInsertStatementForCrew', 'created SQL: ' + insertStatement)
    self._crewInserts.append(insertStatement)


  #----------------------------------------------------------------------------

  def _createInsertStatementForWorkedOn(self, mid, cid, position, first, middle, last, title, year):
    ''' Creates a SQL INSERT statement for the worked_on db table with the given
        values and appends to the list of worked_on INSERT statements.

        mid (int) : the database primary key value of the movie
        cid (int) : crew person id
        position (string) : the name of the position the crew person worked as on the movie
        first (string) : first name (with the appropriate surrounding apostrophes if applicable)
        middle (string) : middle name (with the appropriate surrounding apostrophes if applicable)
        last (string) : last name (with the appropriate surrounding apostrophes if applicable)
        title (string) : title of the movie
        year (int) : year of the movie
    '''
    first = first.strip("'")
    middle = middle.strip("'")
    last = last.strip("'")
    insertStatement = "INSERT INTO worked_on VALUES({0}, {1}, '{2}');  -- {3} {4} {5} for {6} ({7})".format(str(mid),\
                      str(cid), position, first, middle, last, title, str(year))
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
        num = self._parseIntInRangeInclusive(response, 1, 5)
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
    crew   = None   #CrewPerson object
    last   = None   #last name string for sql
    middle = 'NULL' #middle name string for sql
    first  = 'NULL' #first name string for sql
    num    = 0      #numeric input from user
    cid    = 0      #crew person id

    #prompt user for a valid person name
    print '\nEnter the name of someone who worked on this movie (or \'quit\' at anytime). Just hit [enter] to skip the first or middle name.'

    #first name
    response = raw_input('\tFirst name: ')
    self._checkForQuit(response, '_promptUserForCrewPersonHelper')
    if response.lower() != '':
      first = response
    #middle name
    response = raw_input('\tMiddle name: ')
    self._checkForQuit(response, '_promptUserForCrewPersonHelper')
    if response.lower() != '':
      middle = response
    #last name
    last = raw_input('\tLast name: ')
    self._checkForQuit(last, '_promptUserForCrewPersonHelper')

    name = first + ' ' + middle + ' ' + last
    self._log('_promptUserForCrewPersonHelper', 'user entered crew person: ' + name)

    try:
      #get the id of the crew person from the database
      cid = self._getCid(last, middle, first)
      self._log('_promptUserForCrewPersonHelper', 'crew person found in database with id of ' + str(cid))
    except NoResultFound:
      #crew person was not found in database, prompt if this is a new addition or a typo
      self._log('_promptUserForCrewPersonHelper', 'crew person not found in database')
      while True:
        response = raw_input('\nCrew person {0} not found. New person? (y/n/quit): '.format(name))
        self._checkForQuit(response, '_promptUserForCrewPersonHelper')
        if response.lower() not in ['y','n']:
          print '\n**Invalid entry: \'y\', \'n\', or \'quit\' please.\n'
          continue
        if response.lower() == 'n':
          print '\nLet\'s try this again, then...'
          self.promptUserForCrewPerson(title, year)
          return
        #user entered 'y'
        break
      #end while

      self._log('_promptUserForCrewPersonHelper', 'this is a new crew person')
        
      #prompt user for what the person is known as
      num = self._promptUserForPosition('\nWhat is this person known as (1-5 or \'quit\')? ')
      self._log('_promptUserForCrewPersonHelper', 'user entered ' + str(num) + '--new crew person is known as ' + self._positions[num-1])

      self._createInsertStatementForCrew(last, first, middle, self._positions[num-1])
      cid = self._nextCid
      self._log('_promptUserForCrewPersonHelper', 'new crew person has an id of ' + str(cid))
      self._nextCid = self._nextCid + 1
    #end except NoResultFound

    #prompt user for what positions the person worked as
    pids = self._promptUserForWorkedAs(name, title, year)

    #create an SQL INSERT statement for each of those positions
    for pid in pids:
      self._createInsertStatementForWorkedOn(mid, cid, self._positions[pid-1], first, middle, last, title, year)
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

  def promptUserForCrewPerson(self, mid, title, year):
    ''' Wrapper for prompting user for crew persons for a new movie
        
        mid (int) : the database primary key value of the movie
        title (string) : title of the movie
        year (int) : year of the movie

        Raises : QuitException when user quits
                 Exception when an unknown error occurs
    '''
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

  def flush(self):
    ''' Writes out all sql statements to their respective files.
    '''
    for statement in self._crewInserts:
      self._crewSqlFile.write(statement + '\n')
    for statement in self._workedOnInserts:
      self._workedOnSqlFile.write(statement + '\n')


  #----------------------------------------------------------------------------

  def close(self):
    ''' Closes the sql files properly..
    '''
    if self._workedOnSqlFile:
      self._workedOnSqlFile.close()
    if self._crewSqlFile:
      self._crewSqlFile.close()

#!/usr/bin/env python

import imp
from QuitException import QuitException

FILTH_PATH = '/home/tgh/workspace/FiLTH'
models = imp.load_source('models', FILTH_PATH + '/src/python/models.py')


class MovieCrew(object):

  def __init__(self, workedOnSqlFilePath, crewSqlFilePath, logFile, positions, nextCid, nextMid):
    ''' Initialization

        workedOnSqlFilePath (string) : name of the sql file to write inserts for the worked_on db table
        crewSqlFilePath (string) : name of the sql file to write inserts for the crew_person db table
        logFile (file) : file to write log statements to
        positions ([string]) : list of crew person positions as strings
        nextCid (int) : the database id of crew_person for the next new crew person
        nextMid (int) : the database id of movie for the next new movie
    '''
    self._crewInserts = []        # sql INSERT statements for the crew_person table
    self._workedOnInserts = []    # sql INSERT statements for the worked_on table
    self._logFile = logFile
    self._positions = positions
    self._nextCid = nextCid
    self._nextMid = nextMid
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
    crew = models.CrewPerson.query.filter(_models.CrewPerson.l_name == last)\
                                  .filter(_models.CrewPerson.m_name == middle)\
                                  .filter(_models.CrewPerson.f_name == first)\
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

  def _createInsertStatementForWorkedOn(self, cid, position, first, middle, last, title, year):
    ''' Creates a SQL INSERT statement for the worked_on db table with the given
        values and appends to the list of worked_on INSERT statements.

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
    insertStatement = "INSERT INTO worked_on VALUES({0}, {1}, '{2}');  -- {3} {4} {5} for {6} ({7})".format(str(self._nextMid),\
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

  def _promptUserForCrewPersonHelper(self, title, year):
    ''' Prompts the user for a crew person

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
    while True:
      response = raw_input('\nEnter the name of someone who worked on this movie (or \'quit\'): ')
      self._log('_promptUserForCrewPersonHelper', 'user entered crew person: ' + response)

      self._checkForQuit(response, '_promptUserForCrewPersonHelper')

      name = response.split()
      if len(name) == 2:
        last = "'" + name[1] + "'"
        first = "'" + name[0] + "'"
      elif len(name) == 3:
        last = "'" + name[2] + "'"
        middle = "'" + name[1] + "'"
        first = "'" + name[0] + "'"
      elif len(name) == 1:
        last = "'" + name[0] + "'"
      else:
        print '\n**Invalid entry: name cannot be empty or more than 3 names long.\n'
        continue
      break
    #end while

    self._log('_promptUserForCrewPersonHelper', 'first: [' + first + '], middle: [' + middle + '], last: [' + last + ']')

    try:
      #get the id of the crew person from the database
      cid = self._getCid(last, middle, first)
      self._log('_promptUserForCrewPersonHelper', 'crew person found in database with id of ' + str(cid))
    except NoResultFound:
      #crew person was not found in database, prompt if this is a new addition or a typo
      self._log('_promptUserForCrewPersonHelper', 'crew person not found in database')
      while True:
        response = raw_input('\nCrew person {0} not found. New person? (y/n/quit): ')
        self._checkForQuit(response, '_promptUserForCrewPersonHelper')
        if response.lower() not in ['y','n']:
          print '\n**Invalid entry: \'y\', \'n\', or \'quit\' please.\n'
          continue
        if response.lower() == 'n':
          print '\nLet\'s try this again, then...'
          self.promptUserForCrewPerson()
          return
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

    #prompt user for what position the person worked as
    num = self._promptUserForPosition('\nWhat is this work as in this movie (1-5 or \'quit\')? ')
    self._log('_promptUserForCrewPersonHelper', 'user entered ' + str(num) + '--crew person worked on "' + title + '" (' + str(year) + ') as ' + self._positions[num-1])

    self._createInsertStatementForWorkedOn(cid, _positions[num-1], first, middle, last, title, year)


  #----------------------------------------------------------------------------

  def promptUserForCrewPerson(title, year):
    ''' Wrapper for prompting user for crew persons for a new movie
       
        title (string) : title of the movie
        year (int) : year of the movie

        Raises : QuitException when user quits
                 Exception when an unknown error occurs
    '''
    try:
      while True:
        self._promptUserForCrewPersonHelper(title, year)
        while True:
          response = raw_input('\nAny more? (y/n/quit) ')
          self._checkForQuit(response, 'promptUserForCrewPerson')
          if response.lower() not in ['y', 'n', 'quit']:
            print '\n**Invalid entry: \'y\', \'n\', or \'quit\' please.\n'
            continue
          if response.lower() == 'y':
            break
          return
    except Exception e:
      self.close()
      raise e


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
      print "\n**Invalid entry: '" + low + "'-'" + high + "', or 'quit', please."
      raise ve


  #----------------------------------------------------------------------------

  def _printPositions(self):
    ''' Pretty-prints all positions (numbered).
    '''
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
    ''' This is called when the user enters "quit".  Log entry is written, the
        appropriate files are closed, and a QuitException is raised.

        Raises : QuitException
    '''
    self._log(functionName, 'quitting...')
    self._close()
    raise QuitException('user is quitting')


  #----------------------------------------------------------------------------

  def close(self):
    ''' Closes the sql files properly..
    '''
    if self._workedOnSqlFile:
      self._workedOnSqlFile.close()
    if self._crewSqlFile:
      self._crewSqlFile.close()

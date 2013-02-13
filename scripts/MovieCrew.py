#!/usr/bin/env python

class MovieCrew(object):

  def __init__(self, workedOnSqlFilePath, crewSqlFilePath, logFile, session):
    pass


#----------------------------------------------------------------------------

  def _openFiles(self, workedOnSqlFilePath, crewSqlFilePath):
    ''' Attempts to open the sql files to append to by the given file names

        workedOnSqlFilePath (string) : name of the sql file to write inserts for the worked_on db table
        crewSqlFilePath (string) : name of the sql file to write inserts for the crew_person db table

        Throws : IOError when there is a problem opening one of the files
    '''
    try:
      self._workedOnSqlFile = open(workedOnSqlFilePath, 'a')
      self._crewSqlFile = open(crewSqlFilePath, 'a')
    except IOError as e:
      sys.stderr.write("**ERROR: opening file: " + str(e) + ".\n")
      self.close()

#!/usr/bin/env python

import sys
import string
import imp
import traceback
import re
from sqlalchemy.orm.exc import NoResultFound
from os import system

FILTH_PATH = '/home/tgh/workspace/FiLTH'

_models = imp.load_source('models', FILTH_PATH + '/src/python/models.py')
_movieSqlFile = FILTH_PATH + '/sql/movie.sql'
_logFile = FILTH_PATH + '/temp/movie2sql.log'
_crewSqlFile = FILTH_PATH + '/sql/crew_person.sql'
_workedOnSqlFile = FILTH_PATH + '/sql/worked_on.sql'
_tagGivenToSqlFile = FILTH_PATH + '/sql/tag_given_to.sql'
_log = None
_nextMid = 0
_nextCid = 0
_positions = []        # list of strings for crew person positions
_crewInserts = []      # sql INSERT statements for the crew_person table
_workedOnInserts = []  # sql INSERT statements for the worked_on table


def checkArgs():
  """Sanity checks the command-line arguments."""

  fileIdx = 0
  update = False

  if len(sys.argv) != 2 and len(sys.argv) != 3:
    sys.stderr.write("**ERROR: arguments\n\n")
    sys.stderr.write("  usage: movie2sql.py [-u] <input file>\n\n")
    sys.exit(1)
  #no update option
  if len(sys.argv) == 2:
    fileIdx = 1
  #possible "-u" option passed in through command-line
  else:
    if sys.argv[1] != '-u':
      sys.stderr.write("**ERROR: unknown first argument: {0}\n\n".format(sys.argv[1]))
      sys.stderr.write("  usage: movie2sql.py [-u] <input file>\n\n")
      sys.exit()
    update = True
    fileIdx = 2

  return fileIdx, update


#------------------------------------------------------------------------------

def getPositions():
  global _positions

  for position in _models.Position.query.all():
    _positions.append(str(position.position_title))


#------------------------------------------------------------------------------

def printPositions():
  global _positions

  i = 1
  for position in _positions:
    print '{0}. {1}'.format(str(i), position)
    i = i + 1


#------------------------------------------------------------------------------

def FormatTitle(title):
  """Properly format the movie title. e.g. "Falcon And The Snowman, The" ->
     "The Falcon and the Snowman"
  """
  #split title into separate words
  words = title.split()
  #one word title--no need to format
  if len(words) == 1:
    return title
  #last word is '[short]'
  if words[-1] == '[short]':
    del words[-1]
  #check the number of words again
  if len(words) == 1:
    return words[0]
  #second to last word has a comma at the end (e.g. 'Accused,', 'The')
  if words[-2][-1] == ',' and (words[-1] == 'The' or words[-1] == 'A' or words[-1] == 'An'):
    words[-2] = words[-2].translate(None, ',')
    words.insert(0, words[-1])
    del words[-1]
  #title is only two words--no need to format further
  if len(words) == 2:
    return ' '.join(words)
  #title is 3 words or more:
  #iterate over the words between the first and last words
  for i in range(1,len(words)-1):
    #lower case occurrances of these words:
    if words[i] in ('The','And','Of','At','In','As','It','By','On','An','To','A'):
      words[i] = string.lower(words[i])
  #go back and check for words ending with ':' (e.g. "X-Files: the" -> "X-Files: The")
  for i in range (0,len(words)-1):
    if words[i][-1] == ':':
      words[i+1] = string.capitalize(words[i+1])

  return ' '.join(words)


#------------------------------------------------------------------------------

def lg(func, mesg):
  '''Writes an entry to the log file with format "[function name]: message"'''

  _log.write('[' + func + ']: ' + mesg + '\n')


#------------------------------------------------------------------------------

def quit(functionName):
  lg(functionName, 'quitting...')
  raise Exception('user is quitting')

#XXX Make sure this Exception doesn't get caught anywhere other than main...

#------------------------------------------------------------------------------

def checkForQuit(response, functionName):
  if response.lower() == 'quit':
    quit(functionName)


#------------------------------------------------------------------------------

def parseIntInRangeInclusive(response, low, high):
  try:
    num = int(response)
    if (num > high or num < low):
      raise ValueError
  except ValueError as ve:
    print "\n**Invalid entry: '" + low + "'-'" + high + "', or 'quit', please."
    raise ve


#------------------------------------------------------------------------------

def getCid(last, middle, first):
  '''Returns the database id for the person with the given last name, middle
     name, and first name.
  '''

  crew = _models.CrewPerson.query.filter(_models.CrewPerson.l_name == last)\
                                    .filter(_models.CrewPerson.m_name == middle)\
                                    .filter(_models.CrewPerson.f_name == first)\
                                    .one()
  return int(crew.cid)


#------------------------------------------------------------------------------

def createInsertStatementForCrew(last, first, middle, position):
  global _crewInserts

  insertStatement = "INSERT INTO crew_person VALUES(DEFAULT, {0}, {1}, {2}, '{3}');".format(last, first, middle, position)
  lg('createInsertStatementForCrew', 'created SQL: ' + insertStatement)
  _crewInserts.append(insertStatement)


#------------------------------------------------------------------------------

def createInsertStatementForWorkedOn(cid, position, first, middle, last, title, year):
  global _nextMid, _workedOnInserts

  first = first.strip("'")
  middle = middle.strip("'")
  last = last.strip("'")
  insertStatement = "INSERT INTO worked_on VALUES({0}, {1}, '{2}');  -- {3} {4} {5} for {6} ({7})".format(str(_nextMid), str(cid), position, first, middle, last, title, str(year))
  insertStatement = insertStatement.replace('NULL ', '')
  lg('createInsertStatementForWorkedOn', 'created SQL: ' + insertStatement)
  _workedOnInserts.append(insertStatement)


#------------------------------------------------------------------------------

def promptUserForCrewPerson(title, year):
  '''Prompts the user for a crew person'''

  global _positions, _nextMid, _nextCid

  crew   = None   #CrewPerson object
  last   = None   #last name string for sql
  middle = 'NULL' #middle name string for sql
  first  = 'NULL' #first name string for sql
  num    = 0      #numeric input from user
  cid    = 0      #crew person id

  #prompt user for a valid person name
  while True:
    response = raw_input('\nEnter the name of someone who worked on this movie (or \'quit\'): ')
    lg('promptUserForCrewPerson', 'user entered crew person: ' + response)

    checkForQuit(response, 'promptUserForCrewPerson')

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

  lg('promptUserForCrewPerson', 'first: [' + first + '], middle: [' + middle + '], last: [' + last + ']')

  try:
    #get the id of the crew person from the database
    cid = getCid(last, middle, first)
    lg('promptUserForCrewPerson', 'crew person found in database with id of ' + str(cid))
  except NoResultFound:
    #crew person was not found in database, prompt if this is a new addition or a typo
    lg('promptUserForCrewPerson', 'crew person not found in database')
    while True:
      response = raw_input('\nCrew person {0} not found. New person? (y/n/quit): ')
      checkForQuit(response, 'promptUserForCrewPerson')
      if response.lower() not in ['y','n']:
        print '\n**Invalid entry: \'y\', \'n\', or \'quit\' please.\n'
        continue
      if response.lower() == 'n':
        print '\nLet\'s try this again, then...'
        getCrewForMovie()
        return
    #end while

    lg('promptUserForCrewPerson', 'this is a new crew person')
      
    #prompt user for what the person is known as
    printPositions()
    while True:
      response = raw_input('\nWhat is this person known as (1-5 or \'quit\')? ')
      checkForQuit(response, 'promptUserForCrewPerson')
      try:
        num = parseIntInRangeInclusive(response, 1, 5)
      except ValueError:
        continue
    #end while

    lg('promptUserForCrewPerson', 'user entered ' + str(num) + '--new crew person is known as ' + _positions[num-1])

    createInsertStatementForCrew(last, first, middle, _positions[num-1])
    cid = _nextCid
    lg('promptUserForCrewPerson', 'new crew person has an id of ' + str(cid))
    _nextCid = _nextCid + 1
  #end except NoResultFound

  #prompt user for what position the person worked as
  printPositions()
  while True:
    response = raw_input('\nWhat is this work as in this movie (1-5 or \'quit\')? ')
    checkForQuit(response, 'promptUserForCrewPerson')
    try:
      num = parseIntInRangeInclusive(response, 1, 5)
    except ValueError:
      continue
  #end while

  lg('promptUserForCrewPerson', 'user entered ' + str(num) + '--crew person worked on "' + title + '" (' + str(year) + ') as ' + _positions[num-1])

  createInsertStatementForWorkedOn(cid, _positions[num-1], first, middle, last, title, year)


#------------------------------------------------------------------------------

def getCrewForMovie(title, year):
  '''Wrapper for prompting user for crew persons for a new movie'''

  while True:
    promptUserForCrewPerson(title, year)
    while True:
      response = raw_input('\nAny more? (y/n/quit) ')
      if response.lower() not in ['y', 'n', 'quit']:
        print '\n**Invalid entry: \'y\', \'n\', or \'quit\' please.\n'
        continue
      if response.lower() == 'quit':
        quit('getCrewForMovie')
      if response.lower() == 'y':
        break
      return


#------------------------------------------------------------------------------

def checkForUpdate(title, year, stars, mpaa, country):
  """Is this movie already in the database?  If so, update it."""

  global _models, _movieSqlFile
  movie = None  #to hold a Movie object

  lg('checkForUpdate', 'in checkForUpdate')

  #convert the strings of integers to integers
  year = int(year)

  #these original variables will be used to search in the movie.sql file for the
  # original INSERT sql statement for the movie in order to be updated
  origTitle   = title
  origYear    = year
  origStars   = stars
  origMpaa    = mpaa
  origCountry = country

  #query for the movie in the db using the title and year since there is a
  # unique contraint on movies with those attributes
  try:
    lg('checkForUpdate', 'querying db for "' + title + '" (' + str(year) + ')...')
    movie = _models.Movie.query.filter(_models.Movie.title == unicode(title, 'utf_8')).filter(_models.Movie.year == year).one()
    lg('checkForUpdate', 'movie found')
  #even though the movie was not found in the db, this still might be an update
  # (for the title, or year, or both)
  except NoResultFound:
    lg('checkForUpdate', 'movie NOT found in the db')
    response = ''
    while(True):
      #prompt user if this is in fact an update
      response = raw_input("\nDid not find <\"{0}\" ({1}) {2} [{3}] {4}> in the database.\nIs this an update? (y/n/quit) "\
                           .format(title,\
                                   year,\
                                   stars,\
                                   mpaa,\
                                   country))
      if response.lower() in ['n', 'y', 'quit']:
        break
      print '\n**Invalid entry: \'y\', \'n\', or \'quit\' please.\n'
    if response.lower() == 'n':
      lg('checkForUpdate', 'user marked this movie entry as not an update')
      #
      #TODO Prompt user for crew members and tags
      #  getCrewForMovie(title, year)
      #  getTagsForMovie(title, year)
      #TODO increment _nextMid
      #  _nextMid = _nextMid + 1
      return False
    elif response.lower() == 'quit':
      quit('checkForUpdate')

    lg('checkForUpdate', 'user marked this movie entry as an update')
    #prompt user for the id of the movie (until a valid id is given)
    while True:
      try:
        response = int(raw_input("\nWhat is the id of the movie? "))
        lg('checkForUpdate', 'user entered ' + str(response) + ' as the movie id')
        #get the movie from the db
        lg('checkForUpdate', 'querying db for movie with id ' + str(response) + '...')
        movie = _models.Movie.query.filter(_models.Movie.mid == response).one()
        break
      except NoResultFound:
        print "\t**ERROR: id does not exist."
      except ValueError:
        print "\t**ERROR: invalid id."
    #update the title and/or year
    if movie.title != unicode(title, 'utf_8'):
      lg('checkForUpdate', 'titles differ: db title = ' + movie.title + ', entry title = "' + title + '".  Updating...')
      origTitle = movie.title
      movie.title = title
    if movie.year != year:
      lg('checkForUpdate', 'years differ: db year = ' + str(movie.year) + ', entry year = ' + str(year) + '.  Updating...')
      origYear = movie.year
      movie.year = year
  #update what needs updating
  if movie.star_rating != unicode(stars, 'utf_8'):
    #replace "\xc2\xbd" (the representation of a '1/2' character) with the
    # literal "1/2", because it is just too much of a pain otherwise
    dbStars = re.sub(r'[\xc2\xbd]', '1/2', movie.star_rating)
    entryStars = re.sub(r'[\xc2\xbd]', '1/2', stars)
    lg('checkForUpdate', 'star ratings differ: db star rating = ' + dbStars + ', entry star rating = ' + entryStars + '.  Updating...')
    origStars = movie.star_rating
    movie.star_rating = stars
  if movie.mpaa != mpaa:
    lg('checkForUpdate', 'mpaa ratings differ: db mpaa = ' + str(movie.mpaa) + ', entry mpaa = ' + str(mpaa) + '.  Updating...')
    origMpaa = movie.mpaa
    movie.mpaa = mpaa
  if movie.country != country:
    lg('checkForUpdate', 'countries differ: db country = ' + str(movie.country) + ', entry title = ' + str(country) + '.  Updating...')
    origCountry = movie.country
    movie.country = country
  
  #rewrite the INSERT statement in movie.sql
  search  = "'{0}', {1}, '{2}', '{3}', '{4}'".format(origTitle.encode('utf-8').replace("'","''").replace("/","\/"), origYear, origStars.encode('utf-8').replace("*","\*"), origMpaa, origCountry)
  replace = "'{0}', {1}, '{2}', '{3}', '{4}'".format(title.replace("'","''").replace("/","\/"), year, stars, mpaa, country)
  lg('checkForUpdate', 'rewriting INSERT statement in movie.sql file.  search string: ' + search + ', replace string: ' + replace)
  system("sed -i \"s/{0}/{1}/g\" {2}".format(search, replace, _movieSqlFile))

  #output message
  print "\nUPDATE:\n    updated: \"{0}\" ({1}) {2} [{3}] {4}"\
         .format(title,\
                 year,\
                 stars,\
                 mpaa,\
                 country)
  print "   original: \"{0}\" ({1}) {2} [{3}] {4}\n"\
         .format(origTitle.encode('utf-8'),\
                 origYear,\
                 origStars.encode('utf-8'),\
                 origMpaa,\
                 origCountry)
  return True


#------------------------------------------------------------------------------

#------------
#--- MAIN ---
#------------
if __name__ == '__main__':
  fileIdx = 0       #index into argv for the filename
  update  = False   #flag for checking for updates
  retVal  = 0       #value to return to shell
  inserts = []      #list of INSERT statements

  #check the command-line arguments
  fileIdx, update = checkArgs()

  #open the file
  try:
    f = open(sys.argv[fileIdx], 'r')
    _log = open(_logFile, 'w')
  except IOError as e:
    sys.stderr.write("**ERROR: opening file: " + e + ".\n")
    sys.exit()

  try:
    #grab all of the lines in the file
    lines = f.readlines()
    #close the file
    f.close()

    #determine what the next mid and cid values will be for the next new movie and crew person, respectively
    #XXX _nextMid = _models.session.query(_models.Movie).order_by(_models.Movie.mid.desc()).first().mid + 1
    #XXX _nextCid = _models.session.query(_models.CrewPerson).order_by(_models.CrewPerson.cid.desc()).first().cid + 1

    #XXX getPositions()

    if update:
      lg('main', 'this is an update')
      f = open(FILTH_PATH + '/temp/movie_additions.sql', 'w')
    else:
      lg('main', 'this is NOT an update')
      f = open(FILTH_PATH + '/sql/movie.sql', 'w')

    #iterate over the lines retrieved from the file
    for line in lines:
      #strip ending newline character
      line = line.rstrip('\n')
      lg('main', 'current movie: "' + line + '"')
      #no country is specified for this movie
      if line[-1] == ']':
        lg('main', 'no country found for this movie')
        title, year, stars, mpaa = line.rsplit(None, 3)
        country = "DEFAULT"
      #this movie does have a country associated with it
      else:
        lg('main', 'country found')
        #get the index of the ']' of the mpaa rating
        idx = line.rfind(']')
        #find out the length of the country in words (e.g. The Netherlands = 2)
        countryWordCount = line.count(' ', idx, -1)
        #create a list of strings where the tail represents the country
        countryTemp = line.rsplit(None, countryWordCount)
        #join the tail into its own string--this is the country
        country = string.join(countryTemp[-countryWordCount:])
        #add single quotes to the beginning and end of the country
        country = string.join(["'",country,"'"], "")
        #strip off the country from the original line now that we have the country
        temp = string.join(countryTemp[:-countryWordCount])
        #split the rest
        try:
          title, year, stars, mpaa = temp.rsplit(None, 3)
        #oops, there's probably an extra space somewhere in Movie_Raings.doc
        except ValueError as e:
          sys.stderr.write('Error with "' + line + '": ' + str(e) + '\n')
          lg('main', '**ERROR: ValueError exception caught while trying to split title, year, stars, and mpaa: ' + e)
          f.close()
          _log.close()
          sys.exit()
      lg('main', '  unformatted title: ' + title)
      #format the title
      title = FormatTitle(title)
      lg('main', '  formatted title: ' + title)
      #remove the parens around the year
      year = year[1:-1]
      #remove the brackets around the mpaa rating
      mpaa = mpaa[1:-1]

      lg('main', '  year: ' + year)
      lg('main', '  star rating: ' + stars)
      lg('main', '  mpaa rating: ' + mpaa)
      lg('main', '  country: ' + country)
      
      insert = True   #flag for whether or not this will be a sql INSERT statement

      #check to see if this new line is an update of a movie already in the db
      if update:
        insert = not checkForUpdate(title, year, stars, mpaa, country.replace("'",""))
      #nope, this is a regular INSERT statement
      if insert:
        #add the sql to the INSERT statement list
        inserts.append("INSERT INTO movie VALUES (DEFAULT, '{0}', {1}, '{2}', '{3}', {4}, NULL);\n"\
                .format(title.replace("'","''"), year, stars, mpaa, country))
    #end for line in lines

    #write out all sql INSERT statements to sql files
    for insertStatement in inserts:
      f.write(insertStatement)
    #TODO write out crew, worked_on, and tag sql statements

    #commit the changes to the db (if any)
    if update:
      lg('main', 'committing changes to database...')
      _models.session.commit()
      lg('main', 'exiting with return value \'0\'')
  except Exception:
    traceback.print_exc(file=_log)
    traceback.print_exc(file=sys.stdout)
    retVal = 1
  finally:
    _log.close()
    f.close()
    #TODO close other sql files
    sys.exit(retVal)

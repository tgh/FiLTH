#!/usr/bin/env python

import sys
import string
import imp
import traceback
import re
from sqlalchemy.orm.exc import NoResultFound
from os import system
from MovieTagger import MovieTagger
from MovieCrew import MovieCrew
from QuitException import QuitException
from getopt import getopt
from getopt import GetoptError

FILTH_PATH = '/home/tgh/workspace/FiLTH'
MOVIE_ADDITIONS_SQL_FILE = FILTH_PATH + '/temp/movie_additions.sql'
INSERT_FORMAT_STRING = "INSERT INTO movie VALUES ({0}, '{1}', {2}, '{3}', '{4}', {5}, NULL);\n";

_models = imp.load_source('models', FILTH_PATH + '/src/python/models.py')
_logFile = FILTH_PATH + '/logs/movie2sql.log'
#XXX: why is this not a constant? (FILTH_PATH + '/sql/movie.sql')
_movieSqlFile = None
_crewPersonSqlFile = None
_workedOnSqlFile = None
_tagGivenToSqlFile = None
_tagSqlFile = None
_log = None
_nextMid = 0


#------------------------------------------------------------------------------

def usage():
  print "  usage: movie2sql.py [-u] -i <input file> -m <movie sql file> -t <tag sql file> -g <tagGivenTo sql file> -c <crewPerson sql file> -w <workedOn sql file>\n"
  print "\t-u\tuse this option for update mode"
  print "\t-i\tThe input file (to read each movie from) [required]"
  print "\t-m\tThe file for sql insert statements for the movie table of the FiLTH database [required]"
  print "\t-t\tThe file for sql insert statements for the tag table of the FiLTH database [required for update mode]"
  print "\t-g\tThe file for sql insert statements for the tag_given_to table of the FiLTH database [required for update mode]"
  print "\t-c\tThe file for sql insert statements for the crew_person table of the FiLTH database [required for update mode]"
  print "\t-w\tThe file for sql insert statements for the worked_on table of the FiLTH database [required for update mode]\n"


#------------------------------------------------------------------------------

def checkForMissingFileArg(arg):
  if arg == None:
    sys.stderr.write('***ERROR: missing file argument\n')
    usage()
    sys.exit(1)


#------------------------------------------------------------------------------

def processArgs():
  """Sanity checks the command-line arguments."""
  global _movieSqlFile, _tagSqlFile, _tagGivenToSqlFile, _crewPersonSqlFile, _workedOnSqlFile

  inputFile = None
  isUpdate = False #TODO: rename this option ('update' is overloaded in this file and is confusing)

  try:
    opts, args = getopt(sys.argv[1:], 'ui:m:t:g:c:w:')
  except GetoptError as goe:
    sys.stderr.write(str(goe) + '\n\n')
    usage()
    sys.exit(1)

  for o, a in opts:
    if o == '-u':
      isUpdate = True
    elif o == '-i':
      inputFile = a
    elif o == '-m':
      _movieSqlFile = a
    elif o == '-t':
      _tagSqlFile = a
    elif o == '-g':
      _tagGivenToSqlFile = a
    elif o == '-c':
      _crewPersonSqlFile = a
    elif o == '-w':
      _workedOnSqlFile = a

  if isUpdate:
    map(checkForMissingFileArg, [inputFile, _movieSqlFile, _tagSqlFile, _tagGivenToSqlFile, _crewPersonSqlFile, _workedOnSqlFile])
  else:
    map(checkForMissingFileArg, [inputFile, _movieSqlFile])

  return inputFile, isUpdate
  

#------------------------------------------------------------------------------

def getLinesFromFile(filename):
  f = open(filename, 'r')
  #grab all of the lines in the file (stripping the ending newlines)
  lines = map(string.rstrip, f.readlines())
  f.close()
  return lines


#------------------------------------------------------------------------------

def getNextMid():
  return _models.session.query(_models.Movie.mid).order_by(_models.Movie.mid.desc()).first().mid + 1


#------------------------------------------------------------------------------

def FormatTitle(title):
  """Properly format the movie title. e.g. "Falcon And The Snowman, The" ->
     "The Falcon and the Snowman"
  """
  lg('FormatTitle', '  unformatted title: ' + title)
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

def getMovieData(line):
  title = re.search('(.*) \\(', line).group(1)
  year = re.search('\\((\d+)\\)', line).group(1)
  stars = re.search('\\) (.*) \[', line).group(1)
  mpaa = re.search('\[(.*)\]', line).group(1)

  countryMatch = re.search('\] ([a-zA-Z ]+)', line)

  #no country is specified for this movie
  if not countryMatch:
    lg('getMovieData', 'no country found for this movie')
    country = "DEFAULT"
  #this movie does have a country associated with it
  else:
    lg('getMovieData', 'country found')
    country = countryMatch.group(1)
    #add single quotes to the beginning and end of the country
    country = string.join(["'",country,"'"], "")

  #properly format the title
  title = FormatTitle(title)

  lg('getMovieData', '  formatted title: ' + title)
  lg('getMovieData', '  year: ' + year)
  lg('getMovieData', '  star rating: ' + stars)
  lg('getMovieData', '  mpaa rating: ' + mpaa)
  lg('getMovieData', '  country: ' + country)

  return title, year, stars, mpaa, country


#------------------------------------------------------------------------------

def lg(func, mesg):
  '''Writes an entry to the log file with format "[function name]: message"'''

  _log.write('[' + func + ']: ' + mesg + '\n')


#------------------------------------------------------------------------------

def quit(functionName):
  lg(functionName, 'quitting...')
  raise Exception('user is quitting')


#------------------------------------------------------------------------------

def checkForQuit(response, functionName):
  if response.lower() == 'quit':
    quit(functionName)


#------------------------------------------------------------------------------

def isNewMovie(title, year, stars, mpaa, country):
  """Is this movie already in the database?  If so, update it."""

  movie = None  #to hold a Movie object

  lg('isNewMovie', 'in isNewMovie')

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
    lg('isNewMovie', 'querying db for "' + title + '" (' + str(year) + ')...')
    movie = _models.Movie.query.filter(_models.Movie.title == unicode(title, 'utf_8')).filter(_models.Movie.year == year).one()
    lg('isNewMovie', 'movie found')

  except NoResultFound:
    #even though the movie was not found in the db, this still might be an update
    # (for the title, or year, or both)
    lg('isNewMovie', 'movie NOT found in the db')
    response = ''
    while True:
      #prompt user if this is in fact an update
      response = raw_input("\nDid not find <\"{0}\" ({1}) {2} [{3}] {4}> in the database.\nIs this a new movie? (y/n/quit) "\
                           .format(title,\
                                   year,\
                                   stars,\
                                   mpaa,\
                                   country)).lower()
      checkForQuit(response, 'isNewMovie')
      if response not in ['n', 'y', 'quit']:
        print '\n**Invalid entry: \'y\', \'n\', or \'quit\' please.\n'
      else:
        break
    #end while

    if response == 'y':
      lg('isNewMovie', 'user marked this movie entry as a new movie')
      return True

    lg('isNewMovie', 'user marked this movie entry as an update')

    #prompt user for the id of the movie (until a valid id is given)
    while True:
      try:
        response = int(raw_input("\nWhat is the id of the movie? "))
        lg('isNewMovie', 'user entered ' + str(response) + ' as the movie id')
        #get the movie from the db
        lg('isNewMovie', 'querying db for movie with id ' + str(response) + '...')
        movie = _models.Movie.query.filter(_models.Movie.mid == response).one()
        break
      except NoResultFound:
        print "\t**ERROR: id does not exist."
      except ValueError:
        print "\t**ERROR: invalid id."
    #end while

    #update the title and/or year
    if movie.title != unicode(title, 'utf_8'):
      lg('isNewMovie', 'titles differ: db title = ' + movie.title + ', entry title = "' + title + '".  Updating...')
      origTitle = movie.title
      movie.title = title
    if movie.year != year:
      lg('isNewMovie', 'years differ: db year = ' + str(movie.year) + ', entry year = ' + str(year) + '.  Updating...')
      origYear = movie.year
      movie.year = year
  #update what needs updating
  if movie.star_rating != unicode(stars, 'utf_8'):
    #replace "\xc2\xbd" (the representation of a '1/2' character) with the
    # literal "1/2", because it is just too much of a pain otherwise
    dbStars = re.sub(r'[\xc2\xbd]', '1/2', movie.star_rating)
    entryStars = re.sub(r'[\xc2\xbd]', '1/2', stars)
    lg('isNewMovie', 'star ratings differ: db star rating = ' + dbStars + ', entry star rating = ' + entryStars + '.  Updating...')
    origStars = movie.star_rating
    movie.star_rating = stars
  if movie.mpaa != mpaa:
    lg('isNewMovie', 'mpaa ratings differ: db mpaa = ' + str(movie.mpaa) + ', entry mpaa = ' + str(mpaa) + '.  Updating...')
    origMpaa = movie.mpaa
    movie.mpaa = mpaa
  if movie.country != country:
    lg('isNewMovie', 'countries differ: db country = ' + str(movie.country) + ', entry title = ' + str(country) + '.  Updating...')
    origCountry = movie.country
    movie.country = country
  
  #rewrite the INSERT statement in movie.sql
  search  = "'{0}', {1}, '{2}', '{3}', '{4}'".format(origTitle.encode('utf-8').replace("'","''").replace("/","\/"), origYear, origStars.encode('utf-8').replace("*","\*"), origMpaa, origCountry)
  replace = "'{0}', {1}, '{2}', '{3}', '{4}'".format(title.replace("'","''").replace("/","\/"), year, stars, mpaa, country)
  lg('isNewMovie', 'rewriting INSERT statement in movie.sql file.  search string: ' + search + ', replace string: ' + replace)
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
  return False


#------------------------------------------------------------------------------

#------------
#--- MAIN ---
#------------
if __name__ == '__main__':
  inputFile   = None    #the path of the input file to read from (from command line arg)
  isUpdate    = False   #boolean flag for whether or not we are updating the database
                        # (i.e. false == creating movie.sql from scratch, true == modifying movie.sql and movie_additions.sql)
  isInsert    = True    #boolean flag for whether or not the particular line is going to be a movie SQL INSERT statement
  retVal      = 0       #value to return to shell
  inserts     = []      #list of INSERT statements
  tagger      = None    #MovieTagger object
  crewHandler = None    #MovieCrew object
  f           = None    #file holder

  #process the command-line arguments
  inputFile, isUpdate = processArgs()

  #open the input file to read from
  try:
    _log = open(_logFile, 'w')

    #setup a tagger and crew person handler only if in update mode
    if isUpdate:
      tagger = MovieTagger(_tagGivenToSqlFile, _tagSqlFile, _log, _models)
      crewHandler = MovieCrew(_workedOnSqlFile, _crewPersonSqlFile, _log, _models)
      #determine what mid value will be for the next new movie (XXX: this is assuming there is data in the Movie table in the db--do we want to assume that?)
      _nextMid = getNextMid()
    else:
      _nextMid = 1

    #iterate over the lines retrieved from the file
    for line in getLinesFromFile(inputFile):
      lg('main', 'current movie: "' + line + '"')
      title, year, stars, mpaa, country = getMovieData(line)

      if not isUpdate:
        #we are not updating, so just add an INSERT statement from the movie data
        inserts.append(INSERT_FORMAT_STRING.format(_nextMid, title.replace("'","''"), year, stars, mpaa, country))
      else:
        #we are updating so see if we are updating a movie rather than adding a new one
        isInsert = isNewMovie(title, year, stars, mpaa, country.replace("'",""))
        if isInsert:
          #add an INSERT statement for the new movie
          inserts.append(INSERT_FORMAT_STRING.format(_nextMid, title.replace("'","''"), year, stars, mpaa, country))
          #ask user for tags for the movie
          tagger.promptUserForTag(_nextMid, title, year)
          #ask user for crew members who worked on the movie
          crewHandler.promptUserForCrewPerson(_nextMid, title, year)

      #update the next mid
      _nextMid = _nextMid + 1
    #end for

    #determine which file to write out the movie INSERT statements to
    if isUpdate:
      lg('main', 'writing to ' + MOVIE_ADDITIONS_SQL_FILE)
      f = open(MOVIE_ADDITIONS_SQL_FILE, 'w')
    else:
      lg('main', 'writing to ' + _movieSqlFile)
      f = open(_movieSqlFile, 'w')

    #write out all sql INSERT statements to sql files
    for insertStatement in inserts:
      f.write(insertStatement)
    if (tagger):
      tagger.flush()
    if (crewHandler):
      crewHandler.flush()

    #commit any changes to the db
    if isUpdate:
      lg('main', 'committing changes to database...')
      _models.session.commit()
      lg('main', 'exiting with return value \'0\'')
  except Exception:
    traceback.print_exc(file=_log)
    traceback.print_exc(file=sys.stdout)
    retVal = 1
  finally:
    _log.close()
    if f:
      f.close()
    if (tagger):
      tagger.close()
    if (crewHandler):
      crewHandler.close()
    sys.exit(retVal)

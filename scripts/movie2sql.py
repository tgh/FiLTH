#!/usr/bin/env python

import sys
import string
import traceback
import re
from os import system
from os import getenv
from MovieTagger import MovieTagger
from MovieCrew import MovieCrew
from QuitException import QuitException
from getopt import getopt
from getopt import GetoptError

FILTH_PATH = getenv('FILTH_PATH', '/home/tgh/workspace/FiLTH')
TAG_GIVEN_TO_SQL_FILE = FILTH_PATH + '/sql/tag_given_to.sql'
TAG_SQL_FILE = FILTH_PATH + '/sql/tag.sql'
WORKED_ON_SQL_FILE = FILTH_PATH + '/sql/worked_on.sql'
CREW_PERSON_SQL_FILE = FILTH_PATH + '/sql/crew_person.sql'
                                                      #mid, title, year, star, mpaa, country, comments, imdb, theatre, tmdb, parent mid, remake mid, runtime
INSERT_FORMAT_STRING = "INSERT INTO filth.movie VALUES ({0}, '{1}', {2}, '{3}', '{4}', {5}, {6}, {7}, {8}, {9}, NULL, NULL, {10});\n";

_inserts = []   #list of INSERT statements for movies
_updates = []   #list of UPDATE statements for movies
_logFile = FILTH_PATH + '/logs/movie2sql.log'
#XXX: why is this not a constant? (FILTH_PATH + '/sql/movie.sql')
_movieSqlFile = None
_log = None
_nextMid = 0


#------------------------------------------------------------------------------

class MovieNotFoundException(Exception):
  def __init__(self, mesg):
    self.mesg = mesg


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
  global _movieSqlFile

  inputFile = None
  isUpdate = False #TODO: rename this option ('update' is overloaded in this file and is confusing)

  try:
    opts, args = getopt(sys.argv[1:], 'ui:m:')
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
  f = open(_movieSqlFile, 'r')
  lastLine = f.readlines()[-1]
  f.close()
  return int(re.search('VALUES \\((\d+),', lastLine).group(1)) + 1


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
  # or words equal to '-' (e.g. "Star Wars: Episode I - The Phantom Menace")
  for i in range (0,len(words)-1):
    if words[i][-1] == ':' or words[i] == '-':
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
  raise QuitException('user is quitting')


#------------------------------------------------------------------------------

def checkForQuit(response, functionName):
  if response.lower() == 'quit':
    quit(functionName)


#------------------------------------------------------------------------------

def searchForMovieByTitleAndYear(title, year):
  lg('searchForMovieByTitleAndYear', 'looking for "' + title + '" (' + str(year) + ')...')
  f = open(_movieSqlFile, 'r')
  lines = f.readlines()
  f.close()
  title = "'" + title + "'"
  year = str(year) + ','
  for line in lines:
    if title in line and str(year) in line:
      lg('searchForMovieByTitleAndYear', 'movie found')
      movie = {}
      matcher = re.search("VALUES \\((\d+), '(.*?)', (\d+), \'(.*?)\', \'(.*?)\', \'?(.*?)\'?, ", line)
      movie['mid'] = int(matcher.group(1))
      movie['title'] = matcher.group(2)
      movie['year'] = int(matcher.group(3))
      movie['star_rating'] = matcher.group(4)
      movie['mpaa'] = matcher.group(5)
      movie['country'] = matcher.group(6)
      return movie
  raise MovieNotFoundException(title + ' (' + year.rstrip(',') + ') not found')


#------------------------------------------------------------------------------

def searchForMovieById(mid):
  lg('searchForMovieById', 'searching for movie with id ' + str(mid) + '...')
  f = open(_movieSqlFile, 'r')
  lines = f.readlines()
  f.close()
  for line in lines:
    currentMid = int(re.search('VALUES \\((\d+),', line).group(1))
    if mid == currentMid:
      movie = {}
      matcher = re.search("VALUES \\(\d+, '(.*?)', (\d+), \'(.*?)\', \'(.*?)\', \'?(.*?)\'?, ", line)
      movie['mid'] = mid
      movie['title'] = matcher.group(1)
      movie['year'] = int(matcher.group(2))
      movie['star_rating'] = matcher.group(3)
      movie['mpaa'] = matcher.group(4)
      movie['country'] = matcher.group(5)
      return movie
  raise MovieNotFoundException('Movie with id ' + str(mid) + ' not found')


#------------------------------------------------------------------------------

def isNewMovie(title, year, stars, mpaa, country):
  """Is this movie already in the database?  If so, update it.

  Returns a (boolean, Integer) pair: (isUpdate?, mid of movie if found)
  """
  global _inserts

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

  updateValueList = []    #e.g. ["star_rating = '***'", "country = 'USA'"]

  #search for the movie using the title and year since there is a
  # unique contraint on movies with those attributes
  try:
    movie = searchForMovieByTitleAndYear(title, year)
  except MovieNotFoundException:
    #even though the movie was not found in the db, this still might be an update
    # (for the title, or year, or both)
    lg('isNewMovie', 'movie NOT found in the db')
    response = ''
    while True:
      #prompt user if this is in fact an update
      response = raw_input("\nDid not find <\"{0}\" ({1}) {2} [{3}] {4}>.\nIs this a new movie? (y/n/quit) "\
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
      return True, None

    lg('isNewMovie', 'user marked this movie entry as an update')

    #prompt user for the id of the movie (until a valid id is given)
    while True:
      try:
        response = int(raw_input("\nWhat is the id of the movie? "))
        lg('isNewMovie', 'user entered ' + str(response) + ' as the movie id')
        mid = response
        movie = searchForMovieById(mid)
        break
      except MovieNotFoundException:
        print "\t**ERROR: id does not exist."
      except ValueError:
        print "\t**ERROR: invalid id."
    #end while

    #update the title and/or year
    if movie['title'] != title:
      lg('isNewMovie', 'titles differ: db title = ' + movie['title'] + ', entry title = "' + title + '".  Updating...')
      origTitle = movie['title']
      movie['title'] = title
      updateValueList.append("title = '" + title + "'")
    if movie['year'] != year:
      lg('isNewMovie', 'years differ: db year = ' + str(movie['year']) + ', entry year = ' + str(year) + '.  Updating...')
      origYear = movie['year']
      movie['year'] = year
      updateValueList.append("year = " + str(year))
  #end except

  #update what needs updating
  if movie['star_rating'] != stars:
    #replace "\xc2\xbd" (the representation of a '1/2' character) with the
    # literal "1/2", because it is just too much of a pain otherwise
    dbStars = re.sub(r'[\xc2\xbd]', '1/2', movie['star_rating'])
    entryStars = re.sub(r'[\xc2\xbd]', '1/2', stars)
    lg('isNewMovie', 'star ratings differ: db star rating = ' + dbStars + ', entry star rating = ' + entryStars + '.  Updating...')
    origStars = movie['star_rating']
    updateValueList.append("star_rating = '" + stars + "'")
  if movie['mpaa'] != mpaa:
    lg('isNewMovie', 'mpaa ratings differ: db mpaa = ' + str(movie['mpaa']) + ', entry mpaa = ' + str(mpaa) + '.  Updating...')
    origMpaa = movie['mpaa']
    updateValueList.append("mpaa = '" + mpaa + "'")
  if movie['country'] != country:
    lg('isNewMovie', 'countries differ: db country = ' + str(movie['country']) + ', entry title = ' + str(country) + '.  Updating...')
    origCountry = movie['country']
    updateValueList.append("country = '" + country + "'")

  #add UPDATE statement
  updateStatement = 'UPDATE filth.movie SET ' + ', '.join(updateValueList) + ' WHERE mid = ' + str(movie['mid']) + ';\n'
  _updates.append(updateStatement)

  if (origCountry != 'DEFAULT'):
    origCountry = "'" + origCountry + "'"
  if (country != 'DEFAULT'):
    country = "'" + country + "'"
  
  #rewrite the INSERT statement in movie.sql
  search  = "'{0}', {1}, '{2}', '{3}', {4}".format(origTitle.encode('utf-8').replace("/","\/"), origYear, origStars.replace("*","\*"), origMpaa, origCountry)
  replace = "'{0}', {1}, '{2}', '{3}', {4}".format(title.replace("/","\/").replace("&","\&"), year, stars, mpaa, country)
  lg('isNewMovie', 'rewriting INSERT statement in movie.sql file.  search string: ' + search + ', replace string: ' + replace)
  system("sed -i '' \"s/{0}/{1}/g\" {2}".format(search, replace, _movieSqlFile))

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
                 origStars,\
                 origMpaa,\
                 origCountry)
  return False, movie['mid']


#------------------------------------------------------------------------------

def writeOutTagInserts(tagger):
  lg('writeOutTagInserts', 'writing to ' + TAG_SQL_FILE)
  tagf = open(TAG_SQL_FILE, 'a')
  tagger.writeTagInsertsToFile(tagf)
  tagf.close()
  lg('writeOutTagInserts', 'writing to ' + TAG_GIVEN_TO_SQL_FILE)
  tgtf = open(TAG_GIVEN_TO_SQL_FILE, 'a')
  tagger.writeTagGivenToInsertsToFile(tgtf)
  tgtf.close()
  tagger.close()


#------------------------------------------------------------------------------

def writeOutCrewInserts(crewHandler):
  lg('writeOutCrewInserts', 'writing to ' + CREW_PERSON_SQL_FILE)
  cf  = open(CREW_PERSON_SQL_FILE, 'a')
  crewHandler.writeCrewInsertsToFile(cf)
  cf.close()
  lg('writeOutCrewInserts', 'writing to ' + WORKED_ON_SQL_FILE)
  wof = open(WORKED_ON_SQL_FILE, 'a')
  crewHandler.writeWorkedOnInsertsToFile(wof)
  wof.close()
  crewHandler.close()


#------------------------------------------------------------------------------

def promptUserForImdbId():
  response = raw_input('\nIMDB id (\'s\' to skip): ')
  if response == 's':
    return 'NULL'
  return "'" + response + "'"


#------------------------------------------------------------------------------

def promptUserIfSeenInTheater():
  while True:
    response = raw_input('\nSaw it in the theater? ').lower()
    if response not in ['n', 'y']:
      print "\n**Invalid entry: 'y' or 'n' please.\n"
    else:
      break
  if response == 'y':
    return '1'
  return '0'


#------------------------------------------------------------------------------

def promptUserForTmdbId():
  response = raw_input('\nTMDB id (\'s\' to skip): ')
  if response == 's':
    return 'NULL'
  return response


#------------------------------------------------------------------------------

def promptUserForComments():
  response = raw_input('\nComments (\'s\' to skip): ')
  if response == 's':
    return 'NULL'
  return "'" + response.replace("'", "''") + "'"


#------------------------------------------------------------------------------

def promptUserForRuntime():
  response = raw_input('\nRuntime (\'s\' to skip): ')
  if response == 's':
    return 'NULL'
  return response



#------------------------------------------------------------------------------

#------------
#--- MAIN ---
#------------
if __name__ == '__main__':
  inputFile   = None    #the path of the input file to read from (from command line arg)
  isUpdate    = False   #boolean flag for whether or not we are updating
                        # (i.e. false == creating movie.sql from scratch, true == modifying movie.sql)
  retVal      = 0       #value to return to shell
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
      tagger = MovieTagger(TAG_GIVEN_TO_SQL_FILE, TAG_SQL_FILE, _log)
      crewHandler = MovieCrew(WORKED_ON_SQL_FILE, CREW_PERSON_SQL_FILE, _log)
      _nextMid = getNextMid()
    else:
      _nextMid = 1

    #iterate over the lines retrieved from the file
    for line in getLinesFromFile(inputFile):
      lg('main', 'current movie: "' + line + '"')
      title, year, stars, mpaa, country = getMovieData(line)

      if not isUpdate:
        #we are not updating existing sql file (i.e. we are starting from scratch), so just add an INSERT statement from the movie data
        _inserts.append(INSERT_FORMAT_STRING.format(_nextMid, title, year, stars, mpaa, country, 'NULL', 'NULL', 'NULL', 'NULL', 'NULL'))
      else:
        #are we updating an existing movie rather than adding a new one?
        isNew, mid = isNewMovie(title, year, stars, mpaa, country.replace("'",""))
        if isNew:
          #ask user for imdb id
          imdbId = promptUserForImdbId()
          #ask user for tmdb id
          tmdbId = promptUserForTmdbId()
          #ask user if seen in theater
          seenInTheater = promptUserIfSeenInTheater()
          #ask user for runtime
          runtime = promptUserForRuntime()
          #ask user for comments
          comments = promptUserForComments()
          #add an INSERT statement for the new movie
          _inserts.append(INSERT_FORMAT_STRING.format(_nextMid, title, year, stars, mpaa, country, comments, imdbId, seenInTheater, tmdbId, runtime))
          mid = _nextMid

        #ask user for tags for the movie
        print '\nTAGS'
        print '-----\n'
        tagger.promptUserForTag(mid, title, year)
        #ask user for crew members who worked on the movie
        print '\nCREW'
        print '-----'
        crewHandler.promptUserForCrewPerson(mid, title, year)

        if isNew:
          #update the next mid
          _nextMid = _nextMid + 1
      #end if-else
    #end for

    #write out all sql INSERT statements to sql files
    lg('main', 'writing to ' + _movieSqlFile)
    f = open(_movieSqlFile, 'a')
    for insertStatement in _inserts:
      f.write(insertStatement)
    f.close()

    if tagger:
      writeOutTagInserts(tagger)

    if crewHandler:
      writeOutCrewInserts(crewHandler)

  except QuitException:
    lg('main', 'caught QuitException')
  except Exception as e:
    lg('main', 'caught Exception: ' + str(e))
    print '\t***ERROR: Exception caught'
    if crewHandler and crewHandler.hasInserts():
        while True:
          response = raw_input('\nThere are still unwritten crew sql insert statements. Write them out? ').lower()
          if response not in ['y','n']:
            print "Only 'y'/'n'\n"
            continue
          if response == 'y':
            writeOutCrewInserts(crewHandler)
          break
    if tagger and tagger.hasInserts():
        while True:
          response = raw_input('\nThere are still unwritten tag sql insert statements. Write them out? ').lower()
          if response not in ['y','n']:
            print "Only 'y'/'n'\n"
            continue
          if response == 'y':
            writeOutTagInserts(tagger)
          break
    traceback.print_exc(file=_log)
    traceback.print_exc(file=sys.stdout)
    retVal = 1
  finally:
    _log.close()
    if f:
      f.close()
    if tagger:
      tagger.close()
    if crewHandler:
      crewHandler.close()
    sys.exit(retVal)

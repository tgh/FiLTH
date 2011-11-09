#!/usr/bin/env python

import sys
import string
import imp
from sqlalchemy.orm.exc import NoResultFound
from os import system

models = imp.load_source('models', '/home/tgh/Projects/FiLTH/src/orm/models.py')
movieSqlFile = "/home/tgh/Projects/FiLTH/sql/movie.sql"


def checkArgs():
  """Sanity checks the command-line arguments."""

  fileIdx = 0
  update = False

  if len(sys.argv) != 2 and len(sys.argv) != 3:
    sys.stderr.write("**ERROR: arguments\n\n")
    sys.stderr.write("  usage: movie2sql.py [-u] <input file>\n\n")
    sys.exit()
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

def checkForUpdate(title, year, stars, mpaa, country):
  """Is this movie already in the database?  If so, update it."""

  global models, movieSqlFile
  movie = None  #to hold a Movie object

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
    movie = models.Movie.query.filter(models.Movie.title == title).filter(models.Movie.year == year).one()
  #even though the movie was not found in the db, this still might be an update
  # (for the title, or year, or both)
  except NoResultFound:
    #prompt user if this is in fact an update
    response = raw_input("\nDid not find <\"{0}\" ({1}) {2} [{3}] {4}> in the database.\nIs this an update? (y/n) "\
                         .format(title,\
                                 year,\
                                 models.MovieMgr.starRatingToString(stars),\
                                 models.MovieMgr.mpaaToString(mpaa),\
                                 country))
    if response.lower() == 'n':
      return False
    #prompt user for the id of the movie (until a valid id is given)
    while True:
      try:
        response = int(raw_input("\nWhat is the id of the movie? "))
        #get the movie from the db
        movie = models.Movie.query.filter(models.Movie.mid == response).one()
        break
      except NoResultFound:
        print "\t**ERROR: id does not exist."
      except ValueError:
        print "\t**ERROR: invalid id."
    #update the title and/or year
    if movie.title != title:
      origTitle = movie.title
      movie.title = title
    if movie.year != year:
      origYear = movie.year
      movie.year = year
  #update what needs updating
  if movie.star_rating != stars:
    origStars = movie.star_rating
    movie.star_rating = stars
  if movie.mpaa != mpaa:
    origMpaa = movie.mpaa
    movie.mpaa = mpaa
  if movie.country != country:
    origCountry = movie.country
    movie.country = country

  
  #rewrite the INSERT statement in movie.sql
  search  = "'{0}', {1}, '{2}', '{3}', '{4}'".format(origTitle.replace("'","''").replace("/","\/"), origYear, origStars, origMpaa, origCountry)
  replace = "'{0}', {1}, '{2}', '{3}', '{4}'".format(title.replace("'","''").replace("/","\/"), year, stars, mpaa, country)
  system("sed -i \"s/{0}/{1}/g\" {2}".format(search, replace, movieSqlFile))

  #output message
  print "UPDATE:\n   original: \"{0}\" ({1}) {2} [{3}] {4}"\
         .format(title,\
                 year,\
                 models.MovieMgr.starRatingToString(stars),\
                 models.MovieMgr.mpaaToString(mpaa),\
                 country)
  print "    updated: \"{0}\" ({1}) {2} [{3}] {4}\n"\
         .format(origTitle,\
                 origYear,\
                 models.MovieMgr.starRatingToString(origStars),\
                 models.MovieMgr.mpaaToString(origMpaa),\
                 origCountry)
  return True


#------------------------------------------------------------------------------

#------------
#--- MAIN ---
#------------
if __name__ == '__main__':
  fileIdx = 0       #index into argv for the filename
  update  = False   #flag for checking for updates

  #check the command-line arguments
  fileIdx, update = checkArgs()

  #open the file
  try:
    f = open(sys.argv[fileIdx], 'r')
  except IOError:
    sys.stderr.write("**ERROR: opening file.\n")
    sys.exit()
  #grab all of the lines in the file
  lines = f.readlines()
  #close the file
  f.close()

  if update:
    f = open('/home/tgh/Projects/FiLTH/temp/movie_additions.sql', 'w')
  else:
    f = open('/home/tgh/Projects/FiLTH/sql/movie.sql', 'w')

  #iterate over the lines retrieved from the file
  for line in lines:
    #strip ending newline character
    line = line.rstrip('\n')
    #no country is specified for this movie
    if line[-1] == ']':
      title, year, stars, mpaa = line.rsplit(None, 3)
      country = "DEFAULT"
    #this movie does have a country associated with it
    else:
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
      except ValueError:
        sys.stderr.write('Error with "')
        sys.stderr.write(line)
        sys.stderr.write('"\n')
        sys.exit()
    #format the title
    title = FormatTitle(title)
    #remove the parens around the year
    year = year[1:-1]
    #remove the brackets around the mpaa rating
    mpaa = mpaa[1:-1]
    
    insert = True   #flag for wheter or not this will be a sql INSERT statement

    #check to see if this new line is an update of a movie already in the db
    if update:
      insert = not checkForUpdate(title, year, stars, mpaa, country.replace("'",""))
    #nope, this is a regular INSERT statement
    if insert:
      #output the sql
      f.write("INSERT INTO movie VALUES (DEFAULT, '{0}', {1}, '{2}', '{3}', {4}, NULL);\n"\
              .format(title.replace("'","''"), year, stars, mpaa, country))

  #commit the changes to the db (if any)
  if update:
    models.session.commit()
  f.close()

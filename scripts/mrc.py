#!/usr/bin/env python

import sys
import re
import os
import datetime
import string

'''
A sloppy compiler for Movie_Ratings
'''
#------------------------------------------------------------------------------

class MovieRatingKeys():
  TITLE   = 'Title'
  YEAR    = 'Year'
  STARS   = 'Star rating'
  MPAA    = 'MPAA'
  COUNTRY = 'Country'


#------------------------------------------------------------------------------

class MovieRatingsCompilerException(Exception):
  pass


#------------------------------------------------------------------------------

class LexicalAnalzyerException(MovieRatingsCompilerException):
  pass


#------------------------------------------------------------------------------

class ParserException(MovieRatingsCompilerException):
  pass


#------------------------------------------------------------------------------

class MovieRatingsLexicalAnalyzer():

  def __init__(self):
    self._currLine = ''
    self._currLineNum = 0


  def analyze(self, lines):
    #list of lists of tokens
    tokenSets = []
    self._currLineNum = 1

    for line in lines:
      #remove trailing newline
      line = line.rstrip()
      self._currLine = line
      tokens = line.split()
      self._analyzeTokens(tokens)
      tokenSets.append(tokens)
      self._currLineNum += 1
    return tokenSets


  def _analyzeTokens(self, tokens):
    for token in tokens:
      if token[0] == '(':
        self._analyzeYear(token.lstrip('('))
      elif token[0] == '[':
        self._analyzeMpaa(token.lstrip('['))

  
  def _analyzeYear(self, year):
    match = re.match('[0-9][0-9][0-9][0-9]', year)
    if match == None:
      raise LexicalAnalzyerException(self._createBaseErrorMessage() + 'year does not match regular expression "\d{4})" (four digits): ' + str(year))
    if not year.endswith(')'):
      raise LexicalAnalzyerException(self._createBaseErrorMessage() + 'missing closing parens for year.')


  def _analyzeMpaa(self, mpaa):
    match = re.match('[NRGP\-13C7X]+', mpaa)
    if match == None:
      raise LexicalAnalzyerException(self._createBaseErrorMessage() + 'MPAA rating contains unknown character: ' + mpaa)
    if not mpaa.endswith(']'):
      raise LexicalAnalzyerException(self._createBaseErrorMessage() + 'missing closing bracket for mpaa rating.')



  def _createBaseErrorMessage(self):
    return '\n**FAILURE on line ' + str(self._currLineNum) + ' ("' + self._currLine + '"):\n\tCause: '
      


#------------------------------------------------------------------------------

class MovieRatingsParser():

  def __init__(self):
    self._currLine = ''
    self._currLineNum = 0
    self._tokenSets = []  #list of lists
    self._currTokenSet = []
    self._tokenSetIndex = 0
    self._movieRatingDictList = []  #list of dictionaries
    self._line  = ''
    self._errorMessage = ''
    self._yearMin = 1900
    self._yearMax = datetime.datetime.now().year + 3
    self._validStarRatings = ['NO STARS', 'N/A', '\xc2\xbd*', '*', '*\xc2\xbd', '**', '**\xc2\xbd', '***', '***\xc2\xbd', '****']
    self._validMpaaRatings = ['NR','G','PG','PG-13','R','NC-17','X']
    self._countries = ['USA', 'France', 'England', 'Canada', 'China', 'Russia', 'Germany', 'Argentina', 'Portugal', 'Spain', 'Mexico', 'Italy', 'Ireland', 'Scotland', 'Czech Republic', 'Iran', 'The Netherlands', 'Sweden', 'Finland', 'Norway', 'Poland', 'Bosnia', 'Japan', 'Taiwan', 'India', 'Greece', 'Israel', 'Lebanon', 'South Africa', 'Australia', 'New Zealand', 'Brazil', 'Iceland', 'Vietnam', 'Denmark', 'Belgium', 'Switzerland', 'Austria', 'Kazakhstan', 'Algeria', 'Palestine', 'Nepal', 'Georgia', 'Macedonia', 'Cuba', 'Czechoslovakia', 'Puerto Rico', 'Hungary', 'Yugoslavia', 'Nicaragua', 'USSR', 'Ivory Coast', 'Wales']


  def parse(self, tokenSets):
    self._tokenSets = tokenSets
    self._currLineNum = 1
    for tokenSet in self._tokenSets:
      self._currTokenSet = tokenSet
      self._currLine = string.join(tokenSet)
      title = self._Title()
      year  = self._Year()
      stars = self._StarRating()
      mpaa  = self._MpaaRating()
      country = self._Country()
      self._addMovieToDictionaryList(title, year, stars, mpaa, country)
      self._currLineNum += 1


  def _Title(self):
    self._tokenSetIndex = 0
    for token in self._currTokenSet:
      #this token is the movie year
      if token[0] == '(':
        return string.join(self._currTokenSet[:self._tokenSetIndex])
      self._tokenSetIndex += 1
    raise ParserException(self._createBaseErrorMessage() + 'missing year.')


  def _Year(self):
    year = int(self._currTokenSet[self._tokenSetIndex].lstrip('(').rstrip(')'))
    if year < self._yearMin:
      raise ParserException(self._createBaseErrorMessage() + 'year is less than 1900: ' + str(year))
    if year > self._yearMax:
      raise ParserException(self._createBaseErrorMessage() + 'year is greater than 3 years in the future: ' + str(year))
    return year


  def _StarRating(self):
    self._tokenSetIndex += 1
    stars = self._currTokenSet[self._tokenSetIndex]
    if stars == 'NO':
      stars = string.join([stars, self._currTokenSet[self._tokenSetIndex+1]])
    if stars not in self._validStarRatings:
        raise ParserException(self._createBaseErrorMessage() + 'Invalid star rating: "' + stars + '".')
    if stars == 'NO STARS':
      self._tokenSetIndex += 2
    else:
      self._tokenSetIndex += 1
    return stars


  def _MpaaRating(self):
    mpaa = self._currTokenSet[self._tokenSetIndex].lstrip('[').rstrip(']')
    match = re.match('[NRGP\-13C7X]*', mpaa)
    if match == None:
      raise ParserException(self._createBaseErrorMessage() + 'illegal character in MPAA rating: ' + mpaa)
    if mpaa not in self._validMpaaRatings:
      raise ParserException(self._createBaseErrorMessage() + 'Unknown MPAA rating: ' + mpaa)
    self._tokenSetIndex += 1
    return mpaa


  def _Country(self):
    if self._tokenSetIndex == len(self._currTokenSet):
      return None
    country = string.join(self._currTokenSet[self._tokenSetIndex:])
    match = re.match('[a-zA-Z ]+', country)
    if match == None:
      raise ParserException(self._createBaseErrorMessage() + 'illegal character in the country: ' + country)
    if country not in self._countries:
      raise ParserException(self._createBaseErrorMessage() + 'Unknown country: ' + country)
    return country


  def _addMovieToDictionaryList(self, title, year, stars, mpaa, country):
    movieDict = {}
    movieDict[MovieRatingKeys.TITLE] = title
    movieDict[MovieRatingKeys.YEAR]  = year
    movieDict[MovieRatingKeys.STARS] = stars
    movieDict[MovieRatingKeys.MPAA]  = mpaa
    movieDict[MovieRatingKeys.COUNTRY] = country
    self._movieRatingDictList.append(movieDict)


  def _createBaseErrorMessage(self):
    return '\n**FAILURE on line ' + str(self._currLineNum) + ' ("' + self._currLine + '"):\n\tCause: '

    
#------------------------------------------------------------------------------

class MovieRatingsCompiler():

  def __init__(self, filename):
    f = open(filename, 'r')
    self._lines  = f.readlines()
    f.close()
    self._lex    = MovieRatingsLexicalAnalyzer()
    self._parser = MovieRatingsParser()


  def compileMovieRatings(self):
    tokenSets = self._lex.analyze(self._lines)
    self._parser.parse(tokenSets)


#------------------------------------------------------------------------------

def processArgs():
  if len(sys.argv) != 2:
    raise Exception
  if not os.path.exists(sys.argv[1]):
    print '\n**ERROR: file ' + sys.argv[1] + ' does not exist.'
    raise Exception


#------------------------------------------------------------------------------

def usage():
  print '\n\tusage: mrc.py <filename>\n'


#------------------------------------------------------------------------------

if __name__ == '__main__':
  try:
    processArgs()
  except Exception:
    usage()
    sys.exit(1)
  compiler = MovieRatingsCompiler(sys.argv[1])
  try:
    compiler.compileMovieRatings()
  except MovieRatingsCompilerException as e:
    print str(e)
    sys.exit(1)
  sys.exit(0)

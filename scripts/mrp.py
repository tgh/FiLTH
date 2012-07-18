#!/usr/bin/env python

import sys
import re
import os

'''
A sloppy parser for Movie_Ratings
'''

class MovieRatingsParser():

  def __init__(self, filename):
    self._file  = open(filename, 'r')
    self._line  = ''
    self._errorMessage = ''
    self._countries = ['USA', 'France', 'England', 'Canada', 'China', 'Russia', 'Germany', 'Argentina', 'Portugal', 'Spain', 'Mexico', 'Italy', 'Ireland', 'Scotland', 'Czech Republic', 'Iran', 'The Netherlands', 'Sweden', 'Finland', 'Norway', 'Poland', 'Bosnia', 'Japan', 'Taiwan', 'India', 'Greece', 'Israel', 'Lebanon', 'South Africa', 'Australia', 'New Zealand', 'Brazil', 'Iceland', 'Vietnam', 'Denmark', 'Belgium', 'Switzerland', 'Austria', 'Kazakhstan', 'Algeria', 'Palestine', 'Nepal', 'Georgia', 'Macedonia', 'Cuba', 'Czechoslovakia', 'Puerto Rico', 'Hungary', 'Yugoslavia', 'Nicaragua', 'USSR', 'Ivory Coast', 'Wales']


  def close(self):
    self._file.close()


  def parse(self):
    lines = self._file.readlines()
    self.close()
    for line in lines:
      self._line = line
      self._Movie()


  def _Movie(self):
    index = self._Title()
    self._mustBe(index, '(')
    index = self._Year(index+1)
    self._mustBe(index, ')')
    index = self._StarRating(index+1)
    self._mustBe(index, '[')
    index = self._MpaaRating(index+1)
    self._mustBe(index, ']')
    index = self._Country(index+1)
    self._mustBe(index, '\n')


  def _Title(self):
    match = re.match('[^(]*', self._line)
    if match == None:
      self._setErrorMessage('Error while parsing title.')
      self._fail()
    trailingSpaces = self._trailingSpaces(match.group(0))
    title = match.group(0).strip(' ')
    if title.find('  ') != -1:
      self._setErrorMessage('Title contains more than one space in between tokens.')
      self._fail()
    if title.find('\t') != -1:
      self._setErrorMessage('Title contains a tab.')
      self._fail()
    return len(title) + trailingSpaces


  def _Year(self, index):
    match = re.search('[0-9][0-9][0-9][0-9]', self._line[index:])
    if match == None:
      self._setErrorMessage('Year not found.')
      self._fail()
    if int(match.group(0)) < 1900:
      self._setErrorMessage('Year is less than 1900')
      self._fail()
    if int(match.group(0)) > 2015:
      self._setErrorMessage('Year is greater than 2015')
      self._fail()
    return index+4


  def _StarRating(self, index):
    index = self._eatSpacesAndTabs(index)
    if re.match('NO STARS', self._line[index:]) != None:
      index += len('NO STARS')
    elif re.match('N/A', self._line[index:]) != None:
      index += len('N/A')
    else:
      match = re.match('[^ ]*', self._line[index:])
      if match == None:
        self._setErrorMessage('Star rating not found.')
        self._fail()
      for c in match.group(0):
        if c not in ['*','\xc2','\xbd']:
          self._setErrorMessage('Invalid character in star rating: ' + c)
          self._fail()
      index += len(match.group(0))
    return self._eatSpacesAndTabs(index)


  def _MpaaRating(self, index):
    match = re.match('[NRGP\-13C7X]*', self._line[index:])
    if match == None:
      self._setErrorMessage('MPAA rating not found.')
      self._fail()
    mpaa = match.group(0)
    if mpaa not in ['NR','G','PG','PG-13','R','NC-17','X']:
      self._setErrorMessage('Unknown MPAA rating: ' + mpaa)
      self._fail()
    return index + len(mpaa)


  def _Country(self, index):
    index = self._eatSpacesAndTabs(index)
    match = re.match('[a-zA-Z ]+', self._line[index:])
    if match == None:
      return index
    country = match.group(0)
    if country not in self._countries:
      self._setErrorMessage('Unknown country: ' + country)
      self._fail()
    index = self._eatSpacesAndTabs(index+len(country))
    return index


  def _mustBe(self, index, char):
    if char != self._line[index]:
      self._setErrorMessage('UNEXPECTED CHARACTER. Expected \'' + char + '\', found: \'' + self._line[index] + '\'.')
      self._fail()

  def _trailingSpaces(self, s):
    i = -1
    count = 0
    while s[i] == ' ':
      count += 1
      i -= 1
    return count


  def _eatSpacesAndTabs(self, index):
    while self._line[index] == ' ' or self._line[index] == '\t':
      index += 1
    return index


  def _setErrorMessage(self, message):
    self._errorMessage = '\n**FAILURE at: ' + self._line + '\n\tCause: ' + message + '\n'


  def _fail(self):
    print self._errorMessage
    sys.exit(1)
    


#------------------------------------------------------------------------------

def processArgs():
  if len(sys.argv) != 2:
    raise Exception
  if not os.path.exists(sys.argv[1]):
    print '\n**ERROR: file ' + sys.argv[1] + ' does not exist.'
    raise Exception


#------------------------------------------------------------------------------

def usage():
  print '\n\tusage: mrp.py <filename>\n'


#------------------------------------------------------------------------------

if __name__ == '__main__':
  try:
    processArgs()
  except Exception:
    usage()
    sys.exit(1)
  print '\n[exec] mrp.py -- Parsing Movie_Ratings...'
  parser = MovieRatingsParser(sys.argv[1])
  parser.parse()
  print '\n[exec] Movie_Ratings ok.\n'
  sys.exit(0)

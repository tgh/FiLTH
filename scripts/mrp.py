#!/usr/bin/env python

import sys
import re
import os

'''
  G -> M (Y) S [R] C
  M -> [title characters]
  Y -> [year]
  S -> [star rating]
  R -> [mpaa rating]
  C -> [country] | epsilon
'''

class MovieRatingsParser():

  def __init__(self, filename):
    self._file  = open(filename, 'r')
    self._token = ''
    self._line  = ''
    self._errorMessage = ''

  def close(self):
    self._file.close()

  def parse(self):
    lines = f.readlines()
    self.close()
    for line in lines:
      self._line = line
      self._Start()

  def _Start(self):
    index, message = self._Title()
    self._mustBe(index, '(', message)
    index = self._Year(index+1)
    self._mustBe(index, ')')
    index = self._StarRating(index+1)
    self._mustBe(index, '[')
    index = self._MpaaRating()
    self._mustBe(index, ']')
    index = self._Country()
    self._mustBe(index, '\n')

  def _Title(self):
    for i in range(0, len(self._line)):
      if self._line[i] == ' ':
        if self._line[i+1] == ' ':
          return i, 'Title contains more than one space in between tokens.'
        continue
      if self._line[i] == '\t':
          return i, 'Title contains a tab.'

  def _Year(self, index):
    match = re.match('\d{4}', self._line, index)
    if match == None:
      self._setErrorMessage('Year not found.')
      self._fail()
    elif int(match.group(0)) < 1900:
      self._setErrorMessage('Year is less than 1900')
      self._fail()
    elif int(match.group(0)) > 2015:
      self._setErrorMessage('Year is greater than 2015')
      self._fail()
    return index+4

  def _StarRating(self, index):
    index = self._eatSpacesAndTabs(index)
    #TODO
    return self._eatSpacesAndTabs(index)

  def _MpaaRating(self, index):
    pass

  def _Country(self, index):
    pass

  def _scan(self):
    self._char = f.read(1)

  def _mustBe(self, index, char, additionalFailMessage=''):
    if additionFailMessage != '':
      additionFailMessage = '\n\tADDITIONAL INFORMATION: ' + additionalFailMessage
    if char != self._line[index]:
      self._setErrorMessage('UNEXPECTED CHARACTER. Expected \'' + char + '\', found: \'' + self._line[index] + '\'.' + additionalFailMessage)
      self._fail()

  def _eatSpaces(self, index):
    while self._line[index] == ' ':
      index += 1
    return index

  def _eatSpacesAndTabs(self, index):
    while self._line[index] == ' ' or self._line[index] == '\t':
      index += 1
    return index

  def _setErrorMessage(self, message):
    self._errorMessage = '\n**FAILURE at: ' + self._line + '\n\n\tCause: ' + message + '\n'

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
  parser = MovieRatingsParser(sys.argv[1])
  parser.parse()

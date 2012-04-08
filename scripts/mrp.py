#!/usr/bin/env python

import sys
import re

'''
  G -> M (Y) S [R] C
  M -> [title characters]
  Y -> [year]
  S -> [star rating]
  R -> [mpaa rating]
  C -> [country] | epsilon
'''

class MovieRatingsParser():

  self._titleRegex = re.compile('[^()]+')

  def __init__(self, filename):
    self._file = open(filename, 'r')
    self._token = ''

  def close(self):
    self._file.close()

  def parse(self):
    lines = f.readlines()
    self.close()
    for line in lines:
      self._Start(line)

  def _Start(self, line):
    self._Title()
    self._mustBe('(')
    self._Year()
    self._mustBe(')')
    self._StarRating()
    self._mustBe('[')
    self._MpaaRating()
    self._mustBe(']')
    self._Country()

  def _Title(self):
    self._token = self._titleRegex.match(line).group(0).restrip(' ')

  def _Year(self):
    pass

  def _StarRating(self):
    pass

  def _MpaaRating(self):
    pass

  def _Country(self):
    pass

  def _scan(self):
    self._char = f.read(1)

  def _mustBe(self, char):
    if char != self._token:
      pass #TODO: throw exception (unexpected character)


#------------------------------------------------------------------------------

if __name__ == '__main__':
  parser = MovieRatingsParser()
  parser.parse()

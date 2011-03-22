#!/usr/bin/env python

import sys
from string import find
from string import replace

if __name__ == '__main__':
  # check for command-line arg for data file
  if len(sys.argv) != 2:
    print "  usage: crew2sql {file containing crewperson data}"
    sys.exit()

  #open the data file
  try:
    f = open(sys.argv[1], 'r')
  except IOError:
    print "**ERROR: opening file."

  #grab all of the lines in the file
  lines = f.readlines()
  #close the file
  f.close()
  #iterate over the lines retrieved from the file
  for line in lines:
    if find(line, "'") != -1:
      line = replace(line, "'", "''")
    words = line.split(',')
    if len(words) == 2:
      print "INSERT INTO crew_person VALUES(DEFAULT, '" + str(words[0]) + "', '" + str(words[1].strip()) + "', DEFAULT);"
    elif len(words) == 3:
      print "INSERT INTO crew_person VALUES(DEFAULT, '" + str(words[0]) + "', '" + str(words[1]) + "', '" + str(words[2].strip()) + "');"
    elif len(words) == 1:
      print "INSERT INTO crew_person VALUES(DEFAULT, '" + str(words[0].strip()) + "', DEFAULT, DEFAULT);"

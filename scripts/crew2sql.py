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

  #create the first record, which is a dummy record.  Tables such as
  # oscar_given_to have a foreign key to the primary key of crew_person (cid)
  # which can't be NULL, but there are many records in oscar_given_to where a
  # NULL value is desired (for oscars in which recipients don't matter like
  # Best Picture, Best Documentary, etc).  The sequence giving the values of
  # cid has been set to start at 0 by the database schema (see
  # sql/init_pg_database.sql).  This 0 value is to represent a "no-recipient"
  # value--a work around for not be able to use NULL.  The first inserted record
  # into crew_person represented by this print statement will have the cid
  # value of 0.
  print "INSERT INTO crew_person VALUES (0, '', DEFAULT, DEFAULT, DEFAULT); -- dummy record"

  cid = 1
  #iterate over the lines retrieved from the file
  for line in lines:
    if find(line, "'") != -1:
      line = replace(line, "'", "''")
    words = line.split(',')
    if len(words) == 2:
      print "INSERT INTO crew_person VALUES ({0}, '{1}', '{2}', DEFAULT, DEFAULT);".format(str(cid), str(words[0]), str(words[1].strip()))
    elif len(words) == 3:
      print "INSERT INTO crew_person VALUES ({0}, '{1}', '{2}', '{3}', DEFAULT);".format(str(cid), str(words[0]), str(words[1]), str(words[2].strip()))
    elif len(words) == 1:
      print "INSERT INTO crew_person VALUES ({0}, '{1}', DEFAULT, DEFAULT, DEFAULT);".format(str(cid), str(words[0].strip()))
    cid = cid + 1

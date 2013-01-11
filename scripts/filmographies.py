#!/usr/bin/env python

'''
This script is used within the filmographies.sh script, which converts the
Filmographies.doc document into a csv file for easier ETL of the data into the
database.
'''

import sys

if __name__ == '__main__':
  director = ''
  for line in sys.stdin:
    if line[0] != '"':
      director = line.rstrip()
      continue
    print director + ',' + line.rstrip()

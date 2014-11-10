#!/usr/bin/env python

import sys
import re
from os import system
from os import getenv

FILTH_PATH = getenv('FILTH_PATH', '/home/tgh/workspace/FiLTH')
WORKED_ON_FILENAME = FILTH_PATH + '/sql/worked_on.sql'
TEMP_FILENAME = FILTH_PATH + '/temp/acting_temp.txt'
LOG_FILENAME = FILTH_PATH + '/logs/acting.log'

tempFile = None
logger = None


def log(func, message):
  try:
    logger.write('[' + func + '] - ' + message + '\n')
  except UnicodeEncodeError:
    logger.write('[' + func + '] - MOVIE: 8 1/2 (1963)\n')


def quit(lineNum):
  global tempFile, logger

  log('quit', 'quitting')
  tempFile = open(TEMP_FILENAME, 'w')
  tempFile.write(str(lineNum))
  tempFile.close()
  logger.close()
  sys.exit(0)


if __name__ == '__main__':
  try:
    logger = open(LOG_FILENAME, 'w')
    workedOnFile = open(WORKED_ON_FILENAME, 'r')
    tempFile = open(TEMP_FILENAME, 'r')
  except IOError as e:
    sys.stderr.write("**ERROR: opening file: " + str(e) + ".\n")
    sys.exit()

  lastLineProcessed = tempFile.read()
  log('main', 'last line of ' + WORKED_ON_FILENAME + ' processed (read from ' + TEMP_FILENAME + '): ' + lastLineProcessed)
  lastLineProcessed = int(lastLineProcessed)

  lines = workedOnFile.readlines()
  workedOnFile.close()
  
  count = 0
  for line in lines:
    line = line.strip('\n')
    if lastLineProcessed > count:
      count += 1
      continue
    matcher = re.search("(.*?\\(\d+, \d+, ')(.*)('\\);  )(-- .*)", line)
    statementStart = matcher.group(1)
    position = matcher.group(2)
    statementEnd = matcher.group(3)
    comment = matcher.group(4)

    if position != 'Actor' and position != 'Actress':
      count += 1
      continue
    
    newPosition = ''
    print '\n' + comment.replace('--', '')
    while True:
      response = raw_input('  Lead (1) or Supporting (2) (or \'q\' for quit)? ')
      if response not in ['1', '2', 'q']:
        '**ONLY \'1\', \'2\', or \'q\'**'
        continue
      break
    if response == 'q':
      quit(count)
    if response == '1':
      newPosition = 'Lead ' + position
    else:
      newPosition = 'Supporting ' + position
    newLine = line.replace(position, newPosition)

    log('main', 'replacing:\n\t"{0}"\n\twith\n\t"{1}"'.format(line, newLine))
    system("sed -i \"s/{0}/{1}/g\" {2}".format(line, newLine, WORKED_ON_FILENAME))

    count += 1

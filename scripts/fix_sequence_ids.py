#!/usr/bin/env python

'''
Read through ever line in a sql file and fix any primary ids
that are out of sequence.

For example,

INSERT INTO filth.movie VALUES (2, 'Foo', 1991, '***', 'PG', 'USA', NULL, 'tt001', 0, 001, NULL, NULL);
INSERT INTO filth.movie VALUES (4, 'Foo Part II', 1992, '***', 'PG', 'USA', NULL, 'tt002', 0, 002, NULL, NULL);
INSERT INTO filth.movie VALUES (7, 'Bar', 1993, '****', 'R', 'USA', NULL, 'tt003', 0, 003, NULL, NULL);

becomes

INSERT INTO filth.movie VALUES (2, 'Foo', 1991, '***', 'PG', 'USA', NULL, 'tt001', 0, 001, NULL, NULL);
INSERT INTO filth.movie VALUES (3, 'Foo Part II', 1992, '***', 'PG', 'USA', NULL, 'tt002', 0, 002, NULL, NULL);
INSERT INTO filth.movie VALUES (4, 'Bar', 1993, '****', 'R', 'USA', NULL, 'tt003', 0, 003, NULL, NULL);
'''

import sys
import re
from os import getenv
from os import system

FILTH_PATH = getenv('FILTH_PATH', '/home/tgh/workspace/FiLTH')
ID_REGEX = 'VALUES *\((\d+), '

_argFile = None


def usage():
    print '\nusage: fix_sequence_ids.py <filename>'


def processArgs():
    global _argFile

    if len(sys.argv) != 2:
        print 'Missing argument'
        usage()
        sys.exit(1)
    _argFile = sys.argv[1]


def getLines():
    f = open(_argFile, 'r')
    lines = f.readlines()
    f.close()
    return lines


def getId(line):
    matcher = re.search(ID_REGEX, line)
    if None == matcher:
        print '***Error: regex "' + REGEX_ID + '" did not match in ' + line
        sys.exit(2)
    initialId = matcher.group(1)
    return int(initialId)
    

def getInitialId(lines):
    return getId(lines[0])


def replaceLine(expectedId, currentId):
    retval = system("sed -i '' \"s/{0}/{1}/g\" {2}".format('(' + str(currentId) + ',', '(' + str(expectedId) + ',', _argFile))
    if retval != 0:
        print '***Error in sed command: ' + "sed -i '' \"s/{0}/{1}/g\" {2}".format(line, newline, _argFile)
        sys.exit(3)


if __name__ == '__main__':
    processArgs()
    lines = getLines()
    expectedId = getInitialId(lines)
    
    expectedOffset = 0
    for line in lines:
        currentId = getId(line)
        if expectedId != currentId:
            offset = currentId - expectedId 
            if offset != expectedOffset:
                print '*** Gap in sequence detected at expected id ' + str(expectedId)
                expectedOffset = offset
            replaceLine(expectedId, currentId)
        expectedId += 1

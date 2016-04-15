#!/usr/bin/env python

import sys
from os import getenv
from os import system
from getopt import getopt
from getopt import GetoptError

FILTH_PATH = getenv('FILTH_PATH', '/home/tgh/workspace/FiLTH')
_sqlFilePath = None

def usage():
    print "  usage: add_ids.py <sql file>\n"

def processArgs():
    global _sqlFilePath

    #no args
    if len(sys.argv[1:]) != 1:
        print '\n***ERROR: missing sql file argument.'
        usage()
        sys.exit(1)

    try:
        opts, args = getopt(sys.argv[1:], '')
    except GetoptError as goe:
        sys.stderr.write(str(goe) + '\n\n')
        usage()
        sys.exit(1)

    _sqlFilePath = args[0]


def addIds():
    f = open(_sqlFilePath, 'r')
    lines = f.readlines()
    f.close()

    pid = 1
    for line in lines:
        line = line.replace('\n', '')
        idx = line.index('(') + 1
        pre = line[:idx]
        post = line[idx:]
        newline = pre + str(pid) + ', ' + post
        print newline
        pid += 1


if __name__ == '__main__':
    processArgs()
    addIds()

#!/usr/bin/env python

import re
from os import getenv

FILTH_PATH = getenv('FILTH_PATH', '/home/tgh/workspace/FiLTH')

if __name__ == '__main__':
    f = open(FILTH_PATH + '/data/siskel_and_roeper_top_tens.txt', 'r')
    lines = map(str.strip, f.readlines())
    f.close()

    wait = False
    waitFor = ''
    currentYear = 0
    critic = ''
    listTitle = ''
    bestOf = False
    for line in lines:
        if line == 'Gene Siskel':
            critic = 'Gene Siskel'
            continue

        if line == 'Richard Roeper':
            critic = 'Richard Roeper'
            continue

        # skip blank lines
        if line == '' or '--' in line:
            #print 'Skipping blank line'
            continue

        # wait for the next expected line
        if wait and waitFor != line:
            #print 'Skipping line: waiting for "' + waitFor + '"'
            continue

        wait = False

        # if 'Best Films of' line
        if 'Best Films of' in line:
            bestOf = True
            listTitle = line
            continue

        # read year line
        if re.match('\d\d\d\d', line):
            bestOf = False
            if line.startswith('2008'):
                currentYear = 2008
            else:
                currentYear = int(line)
            #print 'Found year: ' + str(currentYear)
            continue

        if currentYear != 2013:
            # regex order
            order = int(re.search('(\d\d)[ab]*\.', line).group(1))
            #print 'Read order: ' + str(order)

            # regex title
            try:
                title = re.search('\. ([^\(]*) \(', line).group(1)
            except AttributeError as e:
                title = re.search('\. (.*)', line).group(1)
            #print 'Read title: "' + title + '"'
        else:
            order = ''
            title = line.strip('*').strip()

        if currentYear == 2008 or currentYear == 2013:
            listTitle = 'Best Movies of ' + str(currentYear)
        elif not bestOf:
            listTitle = 'Top Ten Movies of ' + str(currentYear)
        else:
            currentYear = int(re.search(', (\d\d\d\d)\)', line).group(1))

        print '"' + listTitle + '","' + critic + '",' + str(currentYear) + ',"' + title + '",' + str(order)

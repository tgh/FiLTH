#!/usr/bin/env python

import re

if __name__ == '__main__':
    f = open('/home/thayes/workspace/FiLTH/data/ebert_top_ten.txt', 'r')
    lines = map(str.strip, f.readlines())
    f.close()

    wait = False
    waitFor = ''
    currentYear = 0
    listTitle = ''
    bestOf = False
    for line in lines:
        # skip blank lines
        if line == '':
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

        # if 2006, skip until 2007
        if line == '2006':
            #print '2006, skipping until 2007...'
            wait = True
            waitFor = '2007'
            continue
            
        # if 2008, skip until 2010
        if line.startswith('2008'):
            #print '2008, skipping until 2010...'
            wait = True
            waitFor = '2010'
            continue

        # if line contains 'Best Documentaries', skip until 2012
        if 'Best Documentaries' in line:
            #print '"' + line + '", skipping until 2012'
            wait = True
            waitFor = '2012'
            continue

        # read year line
        if re.match('\d\d\d\d', line):
            bestOf = False
            currentYear = int(line)
            #print 'Found year: ' + str(currentYear)
            continue

        # regex order
        order = int(re.search('(\d\d)[ab]*\.', line).group(1))
        #print 'Read order: ' + str(order)
        # regex title
        try:
            title = re.search('\. ([^\(]*) \(', line).group(1)
        except AttributeError as e:
            title = re.search('\. (.*)', line).group(1)
        #print 'Read title: "' + title + '"'

        if not bestOf:
            listTitle = 'Top Ten Movies of ' + str(currentYear)
        else:
            currentYear = int(re.search(', (\d\d\d\d)\)', line).group(1))

        print '"' + listTitle + '","Roger Ebert",' + str(currentYear) + ',"' + title + '",' + str(order)

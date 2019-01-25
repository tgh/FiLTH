#!/usr/bin/env python

'''
Iterate over all movies in movie.sql and add number of times seen.
'''

from os import getenv
from os import system
import re
import traceback
import sys
from QuitException import QuitException

FILTH_PATH = getenv('FILTH_PATH', '/home/tgh/workspace/FiLTH')
MOVIE_SQL_FILE = FILTH_PATH + '/sql/movie.sql'
LOG_FILENAME = FILTH_PATH + '/logs/add_viewings.log'


def log(func, message):
    logger.write('[' + func + '] - ' + message + '\n')


def getMovieLines():
    f = open(MOVIE_SQL_FILE, 'r')
    lines = f.readlines()
    f.close()
    return lines


if __name__ == '__main__':
    logger = open(LOG_FILENAME, 'w')
    lines = getMovieLines()

    try:
        for line in lines:
            # ignore commented lines
            if line.startswith('--'):
                continue

            try:
                # match for everything up to year and then everything else
                match  = re.search('(.*?\', )(.*)', line)
                #               0      1     2      3         4      5       6      7      8       9       10       11 (if present)
                # vals is now [year, stars, mpaa, country, comment, imdb, theater, tmdb, parent, remake, runtime, viewings]
                # FIXME: if there are commas in the comments then this will be unexpected
                vals = match.group(2).split(',')
            except AttributeError as e:
                print '*** AttributeError on line: ' + line
                log('main', '***Regex error on line: ' + line)
                log('main', str(e))
                continue

            title = re.search("VALUES \\(\d+, '(.*)',", match.group(1)).group(1)
            year = vals[0].strip()

            # skip movies already having viewings
            if len(vals) >= 12:
                log('main', 'SKIP "' + title + '" (' + str(year) + ') -- already has viewings')
                continue

            if str(vals[1].strip()) != "'not seen'":
                print '\n' + title + ' (' + year + ')'
                response = raw_input('How many times have you seen this movie? (q to quit, s to skip) ')
                if response == 'q':
                    raise QuitException('quit')
                if response != 's':
                    viewings = "'" + response + "'"
                else:
                    log('main', title + ' (' + year + ') being skipped. Using NULL for viewings.')
                    viewings = 'NULL'
            else:
                log('main', title + ' (' + year + ') not seen. Using NULL for viewings.')
                viewings = 'NULL'
            #strip off the ");" form the last column
            vals[-1] = vals[-1].strip(");")
            #add the viewings column
            vals.append(' ' + str(viewings) + ');')

            originalLine = line.strip() \
                               .replace(';','\\;') \
                               .replace('*','\\*') \
                               .replace(':','\\:') \
                               .replace('"','\\"') \
                               .replace('!','\\!')
            newline = match.group(1) + (',').join(vals).replace('&', '\\&').replace('"','\\"')
            # FIXME: if '&' is in a movie title this will not work as expected
            retval = system("sed -i '' \"s|{0}|{1}|g\" {2}".format(originalLine, newline, MOVIE_SQL_FILE))

            if retval != 0:
                print '*** Error in sed on line: ' + line
                log('main', '*** Error in sed command: ' + "sed -i '' \"s|{0}|{1}|g\" {2}".format(originalLine, newline, MOVIE_SQL_FILE))
                continue
            else:
                print 'Successfully processed "' + title + '" (' + str(year) + ')'
                log('main', 'successfully processed "' + title + '" (' + str(year) + ')')
    except QuitException:
        log('main', 'quitting')
    except Exception as e:
        log('main', '*** caught Exception: ' + str(e))
        traceback.print_exc(file=logger)
        traceback.print_exc(file=sys.stdout)
    finally:
        logger.close()

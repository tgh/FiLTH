#!/usr/bin/env python

'''
Iterate over all movies in movie.sql, get the running time for the movie from TMDB,
and insert into movie.sql.
'''

from os import getenv
from os import system
import time
import re
import traceback
import json
import sys
import urllib

FILTH_PATH = getenv('FILTH_PATH', '/home/tgh/workspace/FiLTH')
TMDB_API_KEY = getenv('TMDB_API_KEY')   #environment variable TMDB_API_KEY required
MOVIE_SQL_FILE = FILTH_PATH + '/sql/movie.sql'
LOG_FILENAME = FILTH_PATH + '/logs/add_running_time.log'

TMDB_API_URL_FORMAT = 'http://api.themoviedb.org/3/movie/{0}?api_key={1}'


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
                #               0      1     2      3         4      5       6      7      8       9       10 (if present)
                # vals is now [year, stars, mpaa, country, comment, imdb, theater, tmdb, parent, remake, runtime]
                vals = match.group(2).split(',')
            except AttributeError as e:
                print '*** AttributeError on line: ' + line
                log('main', '***Regex error on line: ' + line)
                log('main', str(e))
                continue

            title = re.search("VALUES \\(\d+, '(.*)',", match.group(1)).group(1)
            year = vals[0].strip()

            #skip movies already having runtime
            if len(vals) >= 11:
                log('main', 'SKIP "' + title + '" (' + str(year) + ') -- already has runtime')
                continue

            tmdbId = vals[7].strip()

            #skip movies with no tmdb ids
            if 'NULL' == tmdbId:
                continue

            try:
                response = urllib.urlopen(TMDB_API_URL_FORMAT.format(tmdbId, TMDB_API_KEY))
            except Exception as e:
                print '*** Exception hitting TMDB on line: ' + line
                log('main', '*** Exception caught hitting TMDB at line: ' + line)
                log('main', str(e))
                continue

            tmdbJson = response.read()
            movieObject = json.loads(tmdbJson)

            try:
                runtime = movieObject['runtime']
            except KeyError:
                print '*** KeyError for "runtime" on line: ' + line
                log('main', 'Key error for TMDB id: "' + tmdbId + '". json received: ' + tmdbJson)
                continue

            #strip off the ");" form the last column
            vals[-1] = vals[-1].strip(");")
            #add the runtime column
            vals.append(' ' + str(runtime) + ');')

            originalLine = line.strip() \
                               .replace(';','\\;') \
                               .replace('*','\\*') \
                               .replace(':','\\:') \
                               .replace('"','\\"') \
                               .replace('!','\\!')
            newline = match.group(1) + (',').join(vals).replace('&', '\\&').replace('"','\\"')
            retval = system("sed -i '' \"s|{0}|{1}|g\" {2}".format(originalLine, newline, MOVIE_SQL_FILE))

            if retval != 0:
                print '*** Error in sed on line: ' + line
                log('main', '*** Error in sed command: ' + "sed -i '' \"s|{0}|{1}|g\" {2}".format(originalLine, newline, MOVIE_SQL_FILE))
                continue
            else:
                print 'Successfully processed "' + title + '" (' + str(year) + ')'
                log('main', 'successfully processed "' + title + '" (' + str(year) + ')')

            time.sleep(0.5)  #be nice to TMDB and don't hammer it
    except Exception as e:
        log('main', '*** caught Exception: ' + str(e))
        traceback.print_exc(file=logger)
        traceback.print_exc(file=sys.stdout)
    finally:
        logger.close()

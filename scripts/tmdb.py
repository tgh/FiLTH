#!/usr/bin/env python

'''
Iterate over all movies seen in movie.sql and add a tmdb id based on the imdb id.
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
NEW_MOVIE_SQL_FILE = FILTH_PATH + '/sql/movie_with_tmdb_ids.sql'
LOG_FILENAME = FILTH_PATH + '/logs/tmdb.log'

CURL_COMMAND_FORMAT = 'curl --header "Accept:application/json" \'http://api.themoviedb.org/3/movie/{0}?api_key={1}\''
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
    newMovieSqlFile = open(NEW_MOVIE_SQL_FILE, 'w')
    lines = getMovieLines()

    try:
        for line in lines:
            print line

            #skip movies not seen
            if "'not seen'" in line:
                newLine = line.replace(lineEnding, ', NULL' + lineEnding)
                newMovieSqlFile.write(newLine)
                continue
            else:
                try:
                    imdbId = re.search("'(tt\d+)'", line).group(1)
                except AttributeError as e:
                    log('main', 'No imdb id found in: ' + line)
                    newLine = line.replace(lineEnding, ', NULL' + lineEnding)
                    newMovieSqlFile.write(newLine)
                    continue
                lineEnding = re.search('\\);.*', line).group(0)

                response = urllib.urlopen(TMDB_API_URL_FORMAT.format(imdbId, TMDB_API_KEY))
                tmdbJson = response.read()
                
                movieObject = json.loads(tmdbJson)
                try:
                    tmdbId = movieObject['id']
                except KeyError:
                    log('main', 'Key error for IMDB id: ' + imdbId)
                    tmdbId = 'NULL'

                newLine = line.replace(lineEnding, ', ' + str(tmdbId) + lineEnding)
                newMovieSqlFile.write(newLine)
                time.sleep(.5)  #be nice to TMDB and don't hammer it
    except Exception as e:
        log('main', 'caught Exception: ' + str(e))
        traceback.print_exc(file=logger)
        traceback.print_exc(file=sys.stdout)
    finally:
        logger.close()
        newMovieSqlFile.close()

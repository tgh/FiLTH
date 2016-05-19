#!/usr/bin/env python

'''
Iterate over all movies in movie.sql and add a tmdb id based on the imdb id
if a tmdb id is not present.
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
    lines = getMovieLines()

    try:
        for line in lines:
            match  = re.search('(.*?\', )(.*)', line)

            try:
                vals   = match.group(2).split(',')
            except AttributeError as e:
                print '*** AttributeError on line: ' + line
                log('main', '***Regex error on line: ' + line)
                log('main', str(e))
                continue

            tmdbId = vals[7]
            imdbId = vals[5].strip().strip("'")

            #skip movies that already have a tmdbId
            if tmdbId != ' NULL':
                continue

            if False == imdbId.startswith('tt'):
                print '*** Missing IMDB id on line: ' + line
                log('main', 'No imdb id found in: ' + line)
                continue

            try:
                response = urllib.urlopen(TMDB_API_URL_FORMAT.format(imdbId, TMDB_API_KEY))
            except Exception as e:
                print '*** Exception hitting TMDB on line: ' + line
                log('main', '*** Exception caught hitting TMDB at line: ' + line)
                log('main', str(e))
                continue

            tmdbJson = response.read()
            movieObject = json.loads(tmdbJson)

            try:
                tmdbId = movieObject['id']
            except KeyError:
                print '*** KeyError on line: ' + line
                log('main', 'Key error for IMDB id: "' + imdbId + '". json received: ' + tmdbJson)
                continue

            vals[7] = ' ' + str(tmdbId)
            newline = match.group(1) + (',').join(vals)
            retval = system("sed -i \"s/{0}/{1}/g\" {2}".format(line.strip().replace(';','\\;'), newline.replace('&', '\\&'), MOVIE_SQL_FILE))

            if retval != 0:
                print '*** Error in sed on line: ' + line
                log('main', '*** Error in sed command: ' + "sed -i \"s/{0}/{1}/g\" {2}".format(line.strip().replace(';','\\;'), newline.replace('&', '\\&'), MOVIE_SQL_FILE))
                continue
            else:
                log('main', 'successfully processed line: ' + line)

            time.sleep(1)  #be nice to TMDB and don't hammer it
    except Exception as e:
        log('main', '*** caught Exception: ' + str(e))
        traceback.print_exc(file=logger)
        traceback.print_exc(file=sys.stdout)
    finally:
        logger.close()

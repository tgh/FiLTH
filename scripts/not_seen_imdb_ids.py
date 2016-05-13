#!/usr/bin/env python

# This script prompts user for an IMDB id for each movie not seen.
# The IMDB id is then inserted into the movie's corresponding sql
# INSERT statement in movie.sql via a `sed` command.

import re
from os import getenv
from os import system
from QuitException import QuitException

FILTH_PATH = getenv('FILTH_PATH', '/home/tgh/workspace/FiLTH')

MOVIE_SQL_FILE = FILTH_PATH + '/sql/movie.sql'
LOG_FILENAME = FILTH_PATH + '/logs/not_seen_imdb_ids.log'
TEMP_FILENAME = FILTH_PATH + '/temp/not_seen_imdb_ids.temp.txt'

_movies = []
_movieLineMap = {}
_logFile = None


def log(func, message):
    try:
        _logFile.write('[' + func + '] - ' + message + '\n')
    except UnicodeEncodeError:
        _logFile.write('[' + func + '] - MOVIE: 8 1/2 (1963)\n')


def createMovie(vals, movieLine):
    global _movieLineMap

    movie = {}
    mid = int(re.search('(\d+)', vals).group(1))
    movie['mid'] = mid
    titleStartIndex = vals.find("'") + 1
    titleEndIndex = vals.find("', ")
    movie['title'] = vals[titleStartIndex:titleEndIndex].replace("''", "'")
    vals = vals[(titleEndIndex + 3):]
    vals = vals.split(', ')
    movie['star_rating'] = vals[1]
    if vals[0] == 'NULL':
        movie['year'] = vals[0]
    else:
        movie['year'] = int(vals[0])

    _movieLineMap[mid] = movieLine.replace('\n', '')

    return movie


def initMovies(lastProcessed):
    global _movies

    log('initMovies', '>> Initializing movie list <<')
    f = open(MOVIE_SQL_FILE, 'r')
    movielines = f.readlines()
    f.close()

    for movieline in movielines:
        vals = re.search('VALUES \\((.*)\\);', movieline).group(1)
        movie = createMovie(vals, movieline)

        #skip movie if already processed
        if int(movie['mid']) <= lastProcessed:
            continue
        #skip movies seen
        if movie['star_rating'] != "'not seen'":
            continue
        #skip movies that already have IMDB ids
        if ", 'tt" in movieline:
            continue

        _movies.append(movie)
    log('initMovies', '>> movie list initialized <<')


def promptForMovieImdbId(movie):
    log('promptForMovieImdbId', 'Movie: [' + str(movie['mid']) + '] ' + movie['title'] + ' (' + str(movie['year']) + ')')
    print '\n[' + str(movie['mid']) + '] ' + movie['title'] + ' (' + str(movie['year']) + ')'
    imdbId = raw_input('IMDB id (or \'q\' to quit): ').lower()
    if imdbId == 'q':
        raise QuitException('quitting')
    line = _movieLineMap[movie['mid']]
    linesplit = line.split(',')
    linesplit[7] = ' \'' + imdbId + '\''
    newline = ','.join(linesplit)
    log('promptForMovieImdbId', 'replacing:\n\t\t' + line + '\n\twith:\n\t\t' + newline)
    log('promptForMovieImdbId', 'sed command: ' + "sed -i \"s/{0}/{1}/g\" {2}".format(line, newline, MOVIE_SQL_FILE))
    system("sed -i \"s/{0}/{1}/g\" {2}".format(line, newline.replace('&', '\\&'), MOVIE_SQL_FILE))


def quit(mid):
    log('quit', 'quitting')
    tempFile = open(TEMP_FILENAME, 'w')
    tempFile.write(str(mid))
    tempFile.close()
    _logFile.close()
    


if __name__ == '__main__':
    _logFile = open(LOG_FILENAME, 'w')

    f = open(TEMP_FILENAME, 'r+')
    lastProcessed = f.read()
    f.close()
    log('main', 'last mid processed (read from ' + TEMP_FILENAME + '): ' + lastProcessed)
    lastProcessed = int(lastProcessed)

    initMovies(lastProcessed)

    try:
        for movie in _movies:
            promptForMovieImdbId(movie)
            lastProcessed = movie['mid']
    except QuitException, KeyboardInterrupt:
        pass
    finally:
        quit(lastProcessed)

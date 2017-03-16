#!/usr/bin/env python

"""
Usage:
    create_lists.py FILE

Options:
    -h --help       Show this screen.

"""

from os import getenv
from os import path
from Movies import Movies
from Lists import Lists
from QuitException import QuitException
from docopt import docopt
import traceback
import sys
import pyperclip

FILTH_PATH = getenv('FILTH_PATH', '/home/tgh/workspace/FiLTH')
MOVIE_SQL_FILE = FILTH_PATH + '/sql/movie.sql'
LOG_FILENAME = FILTH_PATH + '/logs/lists.log'

LIST   = 0
AUTHOR = 1
YEAR   = 2
MOVIE  = 3
ORDER  = 4

logger = None
movies = None
lists  = None


def log(func, message, writeToStdout):
    logger.write('[' + func + '] - ' + message + '\n')
    if writeToStdout:
        print '[' + func + '] - ' + message


def quit():
    if logger:
        logger.close()


def processLine(line):
    line = line.strip()
    vals = line.split('|')
    log('processLine', 'Line vals: ' + str(vals), False)

    listTitle = vals[LIST]
    author = vals[AUTHOR]
    year = vals[YEAR]
    movieTitle = vals[MOVIE]
    order = vals[ORDER]
    
    #check for list
    mlist = lists.getListByTitleAndAuthor(listTitle, author)
    if mlist is None:
        log('processLine', 'Adding list: "' + listTitle + '" by ' + str(author), True)
        #create list
        mlist = lists.addList(listTitle, author)
    #check for movie
    movie = movies.getMovieByTitleAndYear(movieTitle, year)
    if movie is None:
        #try again without the year
        movie = movies.getMovieByTitle(movieTitle)
    if movie is None:
        log('processLine', 'Unknown movie: "' + movieTitle + '" (' + str(year) + ')', True)
        while True:
            #copy title to clipboard
            pyperclip.copy(movieTitle)
            response = raw_input('Is this a new movie (\'y\', \'n\', \'q\')? ').lower()
            if response not in ['y','n','q']:
                print 'Only \'y\', \'n\', or \'q\' responses.'
                continue
            if response == 'q':
                raise QuitException('quitting')
            if response == 'n':
                movie = {}
                movie['mid'] = int(raw_input('Mid: '))
            else:
                #create movie
                titleResponse = movies.promptUserForTitle()
                #check for 'k' for "keep" (use the title in the csv row)
                if titleResponse != 'k':
                    movieTitle = titleResponse
                year = movies.promptUserForYear()
                mpaa = movies.promptUserForMpaa()
                country = movies.promptUserForCountry()
                imdbId = movies.promptUserForImdbId()
                tmdbId = movies.promptUserForTmdbId()
                parentId = movies.promptUserForParentId()
                remakeOfId = movies.promptUserForRemakeOfId()
                runtime = movies.promptUserForRuntime()
                movie = movies.addMovie(movieTitle, year, mpaa, country, imdbId, tmdbId, parentId, remakeOfId, runtime)
            break
    #add movie to list
    lists.addMovieToList(movie['mid'], mlist['lid'], order, None, movieTitle, listTitle, author)


if __name__ == '__main__':
    logger = open(LOG_FILENAME, 'w')
    movies = Movies(logger)
    lists  = Lists(logger)

    args = docopt(__doc__, version='create_lists.py 1.0')
    csvFile = args['FILE']

    if not path.isfile(csvFile):
        print '***ERROR: ' + csvFile + ' does not exist or is not a file'
        sys.exit(1)

    try:
        f = open(csvFile, 'r')
        lines = f.readlines()
        f.close()

        for line in lines:
            processLine(line)
    except QuitException:
        pass
    except Exception as e:
        traceback.print_exc(file=logger)
        traceback.print_exc(file=sys.stdout)
    finally:
        if movies and movies.hasInserts():
            while True:
                response = raw_input('\n**WARNING: There are still unwritten movie sql insert statements. Write them out? ').lower()
                if response not in ['y','n']:
                    print "Only 'y'/'n'\n"
                    continue
                if response == 'y':
                    movies.writeAllInsertsToFiles()
                break
        if lists and lists.hasInserts():
            while True:
                response = raw_input('\n**WARNING: There are still unwritten list sql insert statements. Write them out? ').lower()
                if response not in ['y','n']:
                    print "Only 'y'/'n'\n"
                    continue
                if response == 'y':
                    lists.writeAllInsertsToFiles()
                break
        quit()

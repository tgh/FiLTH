#!/usr/bin/env python

from os import getenv
from Movies import Movies
from Lists import Lists
from QuitException import QuitException
import traceback
import sys

FILTH_PATH = getenv('FILTH_PATH', '/home/tgh/workspace/FiLTH')
MOVIE_SQL_FILE = FILTH_PATH + '/sql/movie.sql'
LIST_CSV_FILE = FILTH_PATH + '/data/lists.csv'
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
        log('processLine', 'Unknown movie: "' + movieTitle + '" (' + str(year) + ')', True)
        while True:
            response = raw_input('Is this a new movie? ').lower()
            if response not in ['y','n','q']:
                print 'Only \'y\', \'n\', or \'q\' responses.'
                continue
            if response == 'q':
                raise QuitException
            if response == 'n':
                movie = {}
                movie['mid'] = int(raw_input('Mid: '))
            else:
                #create movie
                mpaa = movies.promptUserForMpaa()
                country = movies.promptUserForCountry()
                imdbId = movies.promptUserForImdbId()
                tmdbId = movies.promptUserForTmdbId()
                parentId = movies.promptUserForParentId()
                remakeOfId = movies.promptUserForRemakeOfId()
                movie = movies.addMovie(movieTitle, year, mpaa, country, imdbId, tmdbId, parentId, remakeOfId)
            break
    #add movie to list
    lists.addMovieToList(movie['mid'], mlist['lid'], order, None, movieTitle, listTitle, author)


if __name__ == '__main__':
    logger = open(LOG_FILENAME, 'w')
    movies = Movies(logger)
    lists  = Lists(logger)

    try:
        f = open(LIST_CSV_FILE, 'r')
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

#!/usr/bin/env python

import sys
import re
import traceback
from os import getenv

FILTH_PATH = getenv('FILTH_PATH', '/home/tgh/workspace/FiLTH')
MOVIE_FILE = FILTH_PATH + '/sql/movie.sql'
LIST_FILE = FILTH_PATH + '/sql/list.sql'
LIST_CONTAINS_FILE = FILTH_PATH + '/sql/list_contains.sql'
COUNTRY_FILE = FILTH_PATH + '/sql/country.sql'
LOG_FILE = FILTH_PATH + '/logs/ebert_lists.log'
                                             # lid, title, author
LIST_INSERT_FORMAT = "INSERT INTO filth.list VALUES ({0}, '{1}', '{2}');\n";
                                                               # mid, lid, rank    title          list    author
LIST_CONTAINS_INSERT_FORMAT = 'INSERT INTO filth.list_contains VALUES ({0}, {1}, {2}); -- "{3}" in list "{4}" by {5}"\n'
                                               # mid, title, year, star_rating, mpaa, country, comments, imdb_id, theatre_viewings
MOVIE_INSERT_FORMAT = "INSERT INTO filth.movie VALUES ({0}, '{1}', {2}, {3}, {4}, {5}, {6}, {7}, {8});\n";

_inputFileLines = []
_listInserts = []
_listContainsInserts = []
_movieInserts = []
_countryInserts = []
_countries = [] # set of known countries
_movies = {}    # "title" -> {year -> mid} (if more than one movie has the same title
_movieFile = None
_logFile = None
_nextLid = 0
_nextMid = 0


#-----------------------------------------------------------------------------

def processArgs():
    global _inputFileLines

    # check for command-line arg for data file
    if len(sys.argv) != 2:
        print "  usage: ebert_lists.py {file containing ebert list data}"
        sys.exit()

    #open the data file
    try:
        f = open(sys.argv[1], 'r')
    except IOError:
        print "**ERROR: opening file."

    #grab all of the lines in the file
    _inputFileLines = f.readlines()
    #close the file
    f.close()


#-----------------------------------------------------------------------------

def init():
    global _logFile

    _logFile = open(LOG_FILE, 'w')
    initMoviesMap()
    initCountries()
    initNextLid()


#-----------------------------------------------------------------------------

def initMoviesMap():
    global _movieFile, _logFile, _movies, _nextMid

    _logFile.write('>>> Initializing movie map <<<\n\n')
    _movieFile = open(MOVIE_FILE, 'r')
    movielines = _movieFile.readlines()
    _movieFile.close()

    for movieline in movielines:
        movieline = movieline.replace("''", "'")
        vals = re.search('VALUES \\((.*)\\);', movieline).group(1)

        mid = re.search('(\d+)', vals).group(1)

        titleStartIndex = vals.find("'") + 1
        titleEndIndex = vals.find("', ")
        title = vals[titleStartIndex:titleEndIndex]

        vals = vals[(titleEndIndex + 3):]
        vals = vals.split(', ')

        year = vals[0]

        if title in _movies:
            _movies[title][year] = mid
        else:
            _movies[title] = {year:mid}
        _nextMid = str(int(mid)+1)


#-----------------------------------------------------------------------------

def initNextLid():
    global _nextLid

    f = open(LIST_FILE, 'r')
    lines = f.readlines()
    f.close()
    _nextLid = str(len(lines) + 1)


#-----------------------------------------------------------------------------

def initCountries():
    global _countries, _logFile

    _logFile.write('\n\n>>> Initializing countries set <<<\n\n')
    f = open(COUNTRY_FILE, 'r')
    lines = f.readlines()
    f.close()

    for line in lines:
        country = re.search('VALUES \\(\'(.*)\'\\);', line).group(1)
        _countries.append(country)


#-----------------------------------------------------------------------------

def getMid(title):
    global _movieInserts, _countryInserts, _nextMid

    try:
        yearMap = _movies[title]
        if len(yearMap) == 1:
            return yearMap.values()[0]
        else:
            print '\n-- Multiple movies found for "' + title + '". Which year?'
            possibleYears = []
            for year in yearMap.keys():
                possibleYears.append(year)
                print '\t- ' + year
            while True:
                yearEntered = raw_input('-- Enter year: ')
                if yearEntered not in possibleYears:
                    print '** Only these years are allowed: '
                    for pyear in possibleYears:
                        print '\t- ' + pyear
                    continue
                else:
                    break
            return yearMap[yearEntered]
    except KeyError:
        while True:
            response = raw_input('*** "' + title + '" not found. New movie (y/n)? ').lower()
            if response not in ['y','n']:
                print '** only "y" or "n" responses'
                continue
            else:
                break
        if response == 'n':
            return raw_input('Enter mid: ')
        else:
            #title
            title = title.replace("'","''")

            #year
            year = raw_input('\tYear (or leave blank): ')
            if year == '':
                year = 'NULL'

            #star rating
            while True:
                response = raw_input('\tStar rating: 1) not seen 2) NULL : ')
                if response not in ['1','2']:
                    print '** Only "1" or "2"'
                else:
                    break
            if response == '1':
                star_rating = "'not seen'"
            else:
                star_rating = 'NULL'

            #MPAA
            while True:
                mpaa = raw_input('\tEnter MPAA rating (or leave blank): ')
                if mpaa == '':
                    mpaa = 'NULL'
                    break
                elif mpaa not in ['G','PG','PG-13','R','NC-17','X','NR']:
                    print '** Unknown MPAA rating: "' + mpaa + '". Only G, PG, PG-13, R, NC-17, X, or NR'
                    continue
                else:
                    break
            if mpaa != 'NULL':
                mpaa = "'" + mpaa + "'"

            #country
            while True:
                country = raw_input('\tCountry (or leave blank): ')
                if country == '':
                    country = 'NULL'
                    break
                elif country not in _countries:
                    response = raw_input('** Country "' + country + '" not found. New country (y/n)? ')
                    if response == 'n':
                        print 'Ok, try again.'
                        continue
                    else:
                        _countries.append(country)
                        countryInsert = "INSERT INTO filth.country VALUES ('" + country + "');\n"
                        _countryInserts.append(countryInsert)
                        break
                else:
                    break
            if country != 'NULL':
                country = "'" + country + "'"

            #comments
            comments = 'NULL'

            #IMDB id
            imdbId = raw_input('\tIMDB id (or leave blank): ')
            if imdbId == '':
                imdbId = 'NULL'
            else:
                imdbId = "'" + imdbId + "'"

            #theatre viewings
            theatre_viewings = 'NULL'
            
            _movieInserts.append(MOVIE_INSERT_FORMAT.format(_nextMid, title, year, star_rating, mpaa, country, comments, imdbId, theatre_viewings))
            mid = _nextMid
            _nextMid = str(int(_nextMid)+1)
            return mid


#-----------------------------------------------------------------------------

def processInputFileLines():
    global _logFile

    listTitle = _inputFileLines[0].strip()
    listAuthor = _inputFileLines[1].strip()
    _listInserts.append(LIST_INSERT_FORMAT.format(_nextLid, listTitle, listAuthor))

    for line in _inputFileLines[2:]:
        _logFile.write('Line: ' + line)
        
        title = line.strip().strip('*')
        mid = getMid(title)

        listContainsInsert = LIST_CONTAINS_INSERT_FORMAT.format(mid, _nextLid, 'NULL', title, listTitle, listAuthor)
        _listContainsInserts.append(listContainsInsert)



#-----------------------------------------------------------------------------

def writeInserts():
    if len(_listInserts) > 0:
        f = open(LIST_FILE, 'a')
        for insert in _listInserts:
            f.write(insert)
        f.close()
    if len(_listContainsInserts) > 0:
        f = open(LIST_CONTAINS_FILE, 'a')
        for insert in _listContainsInserts:
            f.write(insert)
        f.close()
    if len(_movieInserts) > 0:
        f = open(MOVIE_FILE, 'a')
        for insert in _movieInserts:
            f.write(insert)
        f.close()
    if len(_countryInserts) > 0:
        f = open(COUNTRY_FILE, 'a')
        for insetr in _countryInserts:
            f.write(insert)
        f.close()


#-----------------------------------------------------------------------------

def quit():
    global _logFile

    if _logFile:
        _logFile.close()


#=============================================================================

if __name__ == '__main__':
    processArgs()
    init()
    try:
        processInputFileLines()
    except Exception:
        traceback.print_exc()
    finally:
        writeInserts()
        quit()

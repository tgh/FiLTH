#!/usr/bin/env python

import sys
import string
import re
from os import getenv
from QuitException import QuitException

FILTH_PATH = getenv('FILTH_PATH', '/home/tgh/workspace/FiLTH')
MOVIE_SQL_FILE = FILTH_PATH + '/sql/movie.sql'
MPAA_SQL_FILE = FILTH_PATH + '/sql/mpaa.sql'
COUNTRY_SQL_FILE = FILTH_PATH + '/sql/country.sql'
                                                       #mid, title, year, star, mpaa, country, comments, imdb, theatre, tmdb, parent mid, remake mid
MOVIE_INSERT_FORMAT = "INSERT INTO filth.movie VALUES ({0}, '{1}', {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}, {10}, {11});";
COUNTRY_INSERT_FORMAT = "INSERT INTO filth.country VALUES ({0}, '{1}');"


class Movies(object):

    def __init__(self, logFile):
        ''' Initialization

            logFile (file) : file to write log statements to
        '''
        self._logFile = logFile
        self._movies = []
        #initialize movies and get next mid
        self._nextMid = self._initMovies() + 1
        self._movieInserts = []
        self._validMpaaRatings = []
        self._initValidMpaaRatings()
        self._countries = []
        #initialize countries and get next country id
        self._nextCountryId = self._initCountries() + 1
        self._countryInserts = []
        self._log('__init__', 'Next mid: ' + str(self._nextMid))


    #----------------------------------------------------------------------------

    def _createMovie(self, vals):
        movie = {}
        movie['mid'] = int(re.search('(\d+)', vals).group(1))
        titleStartIndex = vals.find("'") + 1
        titleEndIndex = vals.find("', ")
        movie['title'] = vals[titleStartIndex:titleEndIndex]
        vals = vals[(titleEndIndex + 3):]
        vals = vals.split(', ')
        movie['star_rating'] = vals[1]
        if vals[0] == 'NULL':
            movie['year'] = 'NULL'
        else:
            movie['year'] = int(vals[0])
        return movie


    #----------------------------------------------------------------------------

    def _initMovies(self):
        self._log('_initMovies', '>> Initializing movie list <<')
        movieFile = open(MOVIE_SQL_FILE, 'r')
        movielines = movieFile.readlines()
        movieFile.close()
        lastMid = 0
        for movieline in movielines:
            movieline = movieline.replace("''", "'")
            try:
                vals = re.search('VALUES \\((.*)\\);', movieline).group(1)
            except AttributeError as e:
                self._log('_initMovies', '***Line in movie.sql did not match regex: ' + movieline)
                raise e
            movie = self._createMovie(vals)
            self._movies.append(movie)
            lastMid = movie['mid']
        self._log('_initMovies', '>> movie list initialized <<')
        return lastMid


    #----------------------------------------------------------------------------

    def _initValidMpaaRatings(self):
        self._log('_initValidMpaaRatings', '>> Initializing valid mpaa ratings <<')
        f = open(MPAA_SQL_FILE, 'r')
        lines = f.readlines()
        f.close()
        for line in lines:
            rating = re.search('VALUES *\\([0-9]+, \'([A-Z0-9\-]+)\'\\);', line).group(1)
            self._log('_initValidMpaaRatings', '>> Mpaa rating: ' + rating)
            self._validMpaaRatings.append(rating)
        self._log('_initValidMpaaRatings', '>> Valid mpaa ratings initialized <<')


    #----------------------------------------------------------------------------

    def _initCountries(self):
        self._log('_initCountries', '>> Initializing countries <<')
        f = open(COUNTRY_SQL_FILE, 'r')
        lines = f.readlines()
        f.close()
        countryId = 0
        for line in lines:
            matcher = re.search('VALUES *\\(([0-9]+), \'([^\']+)\'\\);', line)
            countryId = int(matcher.group(1))
            country = matcher.group(2)
            self._log('_initCountries', 'Country: ' + country)
            self._countries.append(country)
        self._log('_initCountries', '>> Countries initialized <<')
        return countryId


    #--------------------------------------------------------------------------

    def _log(self, func, message):
        ''' Writes a message to the log file

            func (string) : name of the function current execution is in at the time of this log entry
            message (string) : log entry message
        '''
        self._logFile.write('[Movies.' + func + '] - ' + message + '\n')


    #--------------------------------------------------------------------------

    def _createInsertStatementForMovie(self, title, year, starRating, mpaa, country, imdbId, tmdbId, parentId, remakeOfId):
        ''' Creates a SQL INSERT statement for the movie db table with the
            given movie data and appends to the list of movie INSERT
            statements. This assumes that the movie has not been seen.
            It is also assumed that the arguments are valid.

            title (string)             : title of movie
            year (string or int)       : year of movie (or None)
            starRating (string)        : star rating (or 'NULL' or None)
            mpaa (string)              : mpaa rating of movie (or 'NULL' or None)
            country (string)           : country of movie (or 'NULL' or None)
            imdbId (string)            : IMDB id of movie (or 'NULL' or None)
            tmdbId (string or int)     : TMDB if of movie
            parentId (string or int)   : mid of parent movie (or 'NULL' or None)
            remakeOfId (string or int) : mid of movie this movie is a remake of (or 'NULL' or None)
        '''
        title = title.replace("'", "''")

        if year is None:
            year = 'NULL'

        #TODO: make this if/else a helper function
        if starRating is None:
            starRating = 'NULL'
        elif starRating != 'NULL':
            starRating = "'" + starRating + "'"

        if mpaa is None:
            mpaa = 'NULL'
        elif mpaa != 'NULL':
            mpaa = "'" + mpaa + "'"

        if country is None:
            country = 'NULL'
        elif country != 'NULL':
            country = "'" + country + "'"

        if imdbId is None:
            imdbId = 'NULL'
        elif imdbId != 'NULL':
            imdbId = "'" + imdbId + "'"

        if tmdbId is None:
            tmdbId = 'NULL'

        if parentId is None:
            parentId = 'NULL'

        if remakeOfId is None:
            remakeOfId = 'NULL'

        insertStatement = MOVIE_INSERT_FORMAT.format(str(self._nextMid), title, str(year), "'not seen'", mpaa, country, 'NULL', imdbId, 'NULL', str(tmdbId), str(parentId), str(remakeOfId))
        self._log('_createInsertStatementForMovie', 'created SQL: ' + insertStatement)
        self._movieInserts.append(insertStatement)
        self._nextMid += 1


    #----------------------------------------------------------------------------

    def _createInsertStatementForCountry(self, country):
        insertStatement = COUNTRY_INSERT_FORMAT.format(str(self._nextCountryId), country)
        self._log('_createInsertStatementForCountry', 'created SQL: ' + insertStatement)
        self._countryInserts.append(insertStatement)
        self._nextCountryId += 1


    #----------------------------------------------------------------------------

    def _checkForQuit(self, response, functionName):
        ''' Checks the given response string for quit ("q")

            response (string) : a user's response text
            functionName (string) : the function name of caller
        '''
        if response.lower() == 'q':
            self._quit(functionName)


    #------------------------------------------------------------------------------

    def _quit(self, functionName):
        ''' This is called when the user enters "q".  Log entry is written, and
            a QuitException is raised.

            Raises : QuitException
        '''
        self._log(functionName, 'quitting...')
        raise QuitException('user is quitting')


    #--------------------------------------------------------------------------
    #PUBLIC
    #
    def getMovieByTitle(self, title):
        ''' Gets a movie object using the given title.

            title (string) : a movie title

            Returns: A movie object matching the given title, or None if not found
        '''
        moviesFound = []
        for movie in self._movies:
            if movie['title'].lower() == title.lower():
                moviesFound.append(movie)
        if len(moviesFound) == 0:
            self._log('getMovieByTitle', 'movie not found: "' + title + '"')
            return None
        if len(moviesFound) == 1:
            return moviesFound[0]
        print '! Multiple movies found for "' + title + '"'
        midToMovieMap = {}
        for movie in moviesFound:
            print '\t' + str(movie['mid']) + ': "' + movie['title'] + '" (' + str(movie['year']) + ')'
            midToMovieMap[str(movie['mid'])] = movie
        while True:
            response = raw_input('Enter the id of the correct movie (or \'n\' if none apply): ').lower()
            if response not in midToMovieMap.keys() and response != 'n':
                print 'That is not one of the ids listed. Try again.'
                continue
            if response == 'n':
                return None
            break
        return midToMovieMap[response]


    #--------------------------------------------------------------------------
    #PUBLIC
    #
    def getMovieByTitleAndYear(self, title, year):
        ''' Gets a movie object using the given title and year.

            title (string) : a movie title
            year (int)     : a year (as int)

            Returns: A movie object matching the given title and year, or None if not found
        '''
        moviesFound = []
        for movie in self._movies:
            if movie['title'].lower() == title.lower():
                #+/- 1 year margin of error
                if (movie['year'] == int(year) or
                    movie['year'] == (int(year)-1) or
                    movie['year'] == (int(year)+1)):
                    moviesFound.append(movie)
        if len(moviesFound) == 0:
            self._log('getMovieByTitleAndYear', 'movie not found: "' + title + '" (' + str(year) + ')')
            return None
        if len(moviesFound) == 1:
            return moviesFound[0]
        print '! Multiple movies found for "' + title + '" (' + str(year) + '):'
        midToMovieMap = {}
        for movie in moviesFound:
            print '\t' + str(movie['mid']) + ': "' + movie['title'] + '" (' + str(movie['year']) + ')'
            midToMovieMap[str(movie['mid'])] = movie
        while True:
            response = raw_input('Enter the id of the correct movie (or \'n\' if none apply): ').lower()
            if response not in midToMovieMap.keys() and response != 'n':
                print 'That is not one of the ids listed. Try again.'
                continue
            if response == 'n':
                return None
            break
        return midToMovieMap[response]


    #--------------------------------------------------------------------------
    #PUBLIC
    #
    def addMovie(self, title, year, mpaa, country, imdbId, tmdbId, parentId, remakeOfId):
        movie = {}
        movie['mid'] = self._nextMid
        movie['title'] = title
        movie['star_rating'] = 'not seen'
        movie['year'] = int(year)
        self._movies.append(movie)
        self._createInsertStatementForMovie(title, year, None, mpaa, country, imdbId, tmdbId, parentId, remakeOfId)
        return movie


    #--------------------------------------------------------------------------
    #PUBLIC
    #
    def promptUserForTitle(self):
        ''' Prompts user for the title of a new movie.

            Returns: The user's input for the movie title
            Raises : QuitException when user quits
        '''
        while True:
            response = raw_input('What is the TITLE of this new movie? (\'q\' to quit) ')
            self._checkForQuit(response, 'promptUserForTitle')
            return response


    #--------------------------------------------------------------------------
    #PUBLIC
    #
    def promptUserForYear(self):
        ''' Prompts user for the year of a new movie.

            Returns: The user's input for the movie year (as a string)
            Raises : QuitException when user quits
        '''
        while True:
            response = raw_input('What is the YEAR for this new movie? (\'q\' to quit) ')
            self._checkForQuit(response, 'promptUserForTitle')
            try:
                year = int(response)
                if year < 1900 or year > 2020:  #arbitrary
                    raise ValueError
            except ValueError:
                print '**That is not a valid year--try again.'
                continue
            return response


    #--------------------------------------------------------------------------
    #PUBLIC
    #
    def promptUserForMpaa(self):
        ''' Prompts user for the mpaa rating of a new movie.

            Returns: The user's input for the movie mpaa rating
            Raises : QuitException when user quits
        '''
        while True:
            response = raw_input('What is the MPAA rating for this new movie? (\'q\' to quit) ')
            self._checkForQuit(response, 'promptUserForMpaa')
            if response not in self._validMpaaRatings:
                print '**"' + response + '" is not a valid MPAA rating. Should be one of: ' + str(self._validMpaaRatings)
                continue
            return response


    #--------------------------------------------------------------------------
    #PUBLIC
    #
    def promptUserForCountry(self):
        ''' Prompts user for the country of a new movie. If the country is
            unknown, user is further prompted whether or not to add this
            country.

            Returns: The user's input for the movie country, or None if skipped
            Raises : QuitException when user quits
        '''
        while True:
            response = raw_input('What is the COUNTRY for this new movie? (\'q\' to quit, or \'s\' to skip) ')
            self._checkForQuit(response, 'promptUserForMpaa')
            if response == 's':
                return None
            if response not in self._countries:
                while True:
                    response2 = raw_input('>> Unkown country: "' + response + '". Add this country? (\'y\', \'n\', \'q\'): ').lower()
                    self._checkForQuit(response2, 'promptUserForMpaa')
                    if response2 not in ['y','n','q']:
                        print "**Only 'y', 'n', or 'q' please." 
                        continue
                    if response2 == 'y':
                        self._createInsertStatementForCountry(response)
                        return response
                    else:
                        print '>> Try again. Country should be one of: ' + str(self._countries)
            return response


    #--------------------------------------------------------------------------
    #PUBLIC
    #
    def promptUserForImdbId(self):
        ''' Prompts user for the IMDB id of a new movie.

            Returns: The user's input for the movie IMDB id
            Raises : QuitException when user quits
        '''
        while True:
            response = raw_input('What is the IMDB id of this new movie? (\'q\' to quit) ')
            self._checkForQuit(response, 'promptUserForImdbId')
            if not response.startswith('tt'):
                print '**Invalid IMDB id. IMDB ids start with "tt"'
                continue
            return response


    #--------------------------------------------------------------------------
    #PUBLIC
    #
    def promptUserForTmdbId(self):
        ''' Prompts user for the TMDB id of a new movie.

            Returns: The user's input for the movie TMDB id
            Raises : QuitException when user quits
        '''
        while True:
            response = raw_input('What is the TMDB id of this new movie? (\'q\' to quit) ')
            self._checkForQuit(response, 'promptUserForTmdbId')
            try:
                tmdbId = int(response)
                if tmdbId < 1:
                    raise ValueError
            except ValueError:
                print '**Invalid TMDB id. TMDB ids are positive integers.'
                continue
            return response


    #--------------------------------------------------------------------------
    #PUBLIC
    #
    def promptUserForParentId(self):
        ''' Prompts user for the parent mid of a new movie.

            Returns: The user's input for the parent id or None if skipped
            Raises : QuitException when user quits
        '''
        while True:
            response = raw_input('What is the PARENT MID of this new movie? (\'q\' to quit, or \'s\' to skip) ')
            self._checkForQuit(response, 'promptUserForParentId')
            if response.lower() == 's':
                return None

            try:
                parentId = int(response)
                if parentId < 1:
                    raise ValueError
            except ValueError:
                print '**Invalid mid. mids ids are positive integers.'
                continue
            return response


    #--------------------------------------------------------------------------
    #PUBLIC
    #
    def promptUserForRemakeOfId(self):
        ''' Prompts user for the remakeOf mid of a new movie.

            Returns: The user's input for the remakeOf id or None if skipped
            Raises : QuitException when user quits
        '''
        while True:
            response = raw_input('What is the REMAKE MID of this new movie? (\'q\' to quit, or \'s\' to skip) ')
            self._checkForQuit(response, 'promptUserForRemakeOfId')
            if response.lower() == 's':
                return None

            try:
                remakeOfId = int(response)
                if remakeOfId < 1:
                    raise ValueError
            except ValueError:
                print '**Invalid mid. mids ids are positive integers.'
                continue
            return response


    #----------------------------------------------------------------------------
    #PUBLIC
    #
    def writeAllInsertsToFiles(self):
        self.writeMovieInsertsToFile()
        self.writeCountryInsertsToFile()


    #----------------------------------------------------------------------------
    #PUBLIC
    #
    def writeMovieInsertsToFile(self):
        f = open(MOVIE_SQL_FILE, 'a')
        for statement in self._movieInserts:
            f.write(statement + '\n')
        f.close()


    #----------------------------------------------------------------------------
    #PUBLIC
    #
    def writeCountryInsertsToFile(self):
        f = open(COUNTRY_SQL_FILE, 'a')
        for statement in self._countryInserts:
            f.write(statement + '\n')
        f.close()


    #----------------------------------------------------------------------------
    #PUBLIC
    #
    def hasInserts(self):
        return len(self._movieInserts) > 0 or len(self._countryInserts) > 0


    #----------------------------------------------------------------------------
    #PUBLIC
    #
    def close(self):
        self._movieInserts = []
        self._movies = []
        self._countries = []
        self._validMpaaRatings = []
        self._countryInserts = []

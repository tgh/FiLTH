#!/usr/bin/env python

import re
import traceback
from os import getenv

YEAR = 0
CATEGORY = 1
NOMINEES = 2
TITLE = 3
STATUS = 4
COUNTRY = 5

POSITIONS = {'1':'Director',
             '2':'Screenwriter',
             '3':'Cinematographer',
             '4':'Actor',
             '5':'Actress',
             '6':'Lead Actor',
             '7':'Supporting Actor',
             '8':'Lead Actress',
             '9':'Supporting Actress'}

CATEGORY_POSITIONS = {'Best Actor':'Lead Actor',
                      'Best Actress':'Lead Actress',
                      'Best Supporting Actor':'Supporting Actor',
                      'Best Supporting Actress':'Supporting Actress',
                      'Best Director':'Director',
                      'Best Cinematography':'Cinematographer',
                      'Best Adapted Screenplay':'Screenwriter',
                      'Best Original Screenplay':'Screenwriter'}

FILTH_PATH = getenv('FILTH_PATH', '/home/tgh/workspace/FiLTH')
OSCARS_CSV = FILTH_PATH + '/data/oscars_2009_and_up.csv'
MOVIE_FILE = FILTH_PATH + '/sql/movie.sql'
CREW_FILE = FILTH_PATH + '/sql/crew_person.sql'
WORKED_ON_FILE = FILTH_PATH + '/sql/worked_on.sql'
LOG_FILE = FILTH_PATH + '/logs/oscars_2009_and_up.log'
OSCAR_FILE = FILTH_PATH + '/sql/oscar.sql'
OSCAR_GIVEN_TO_FILE = FILTH_PATH + '/sql/oscar_given_to.sql'
COUNTRY_FILE = FILTH_PATH + '/sql/country.sql'

                                                         # mid, oid, cid, year, status, sharing_with
INSERT_FORMAT_STRING = "INSERT INTO filth.oscar_given_to VALUES ({0}, {1}, {2}, {3}, {4});";

_inserts = []
_crewInserts = []
_workedOnInserts = []
_movieInserts = []
_countryInserts = []
_countries = [] # set of known countries
_woCache = []   # list of tuples: (mid, cid, position)
_movies = {}    # "title (year)" -> id 
_crew = {}      # "firstname middlename lastname" -> id
_oscar = {}     # "category" -> id
_movieFile = None
_crewFile = None
_oscarFile = None
_oscarGivenToFile = None
_workedOnFile = None
_logFile = None
_nextCid = 0
_nextMid = 0


#-----------------------------------------------------------------------------

def init():
    global _logFile

    _logFile = open(LOG_FILE, 'w')
    initMoviesMap()
    initCrewMap()
    initOscarMap()
    initWorkedOnCache()
    initCountries()


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

        if title == 'Good Night, and Good Luck.':
            title = 'Good Night and Good Luck.'
        elif title == 'The Chronicles of Narnia: The Lion, the Witch, and the Wardrobe':
            title = 'The Chronicles of Narnia: The Lion the Witch and the Wardrobe'
        elif title == 'Oslo, August 31st':
            title = 'Oslo August 31st'
        elif title == 'Crazy, Stupid, Love.':
            title = 'Crazy Stupid Love.'

        vals = vals[(titleEndIndex + 3):]
        vals = vals.split(', ')

        year = vals[0]

        _movies[title + ' (' + year + ')'] = mid

    _nextMid = str(int(mid) + 1)


#-----------------------------------------------------------------------------

def initCrewMap():
    global _crewFile, _logFile, _crew, _nextCid
    
    _logFile.write('\n\n>>> Initializing crew map <<<\n\n')
    _crewFile = open(CREW_FILE, 'r')
    crewlines = _crewFile.readlines()
    _crewFile.close()

    for crewline in crewlines:
        crewline = crewline.replace("''", "'")
        vals = re.search('VALUES \\((.*)\\);', crewline).group(1).split(',')

        cid = vals[0]
        name = vals[4].strip().strip("'")

        _crew[name] = cid

    _nextCid = str(int(cid) + 1)


#-----------------------------------------------------------------------------

def initOscarMap():
    global _oscarFile, _logFile, _oscar
    
    _logFile.write('\n\n>>> Initializing oscar map <<<\n\n')
    _oscarFile = open(OSCAR_FILE, 'r')
    lines = _oscarFile.readlines()
    _oscarFile.close()

    for line in lines:
        line = line.replace("''", "'")
        vals = re.search('VALUES \\((.*)\\);', line).group(1).split(',')

        oid = vals[0]
        name = vals[1].strip().strip("'")

        _oscar[name] = oid
        _logFile.write('Award category: ' + name + ' -> ' + oid + '\n')


#-----------------------------------------------------------------------------

def initWorkedOnCache():
    global _woCache, _logFile

    _logFile.write('\n\n>>> Initializing worked_on map <<<\n\n')
    _workedOnFile = open(WORKED_ON_FILE, 'r')
    lines = _workedOnFile.readlines()
    _workedOnFile.close()

    for line in lines:
        vals = re.search('VALUES\\((.*)\\);', line).group(1).split(',')

        mid = vals[0]
        cid = vals[1].strip()
        pos = vals[2].strip().strip("'")

        _woCache.append((mid,cid,pos))
        _logFile.write('    (' + mid + ', ' + cid + ', ' + pos + ')\n')


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
        _logFile.write('    Adding country: ' + country + '\n')


#-----------------------------------------------------------------------------

def getMid(title, year, country):
    global _nextMid, _logFile, _countries, _countryInserts

    try:
        mid = _movies[title + ' (' + year + ')']
    except KeyError:
        try:
            mid = _movies[title + ' (' + str(int(year)+1) + ')']
        except KeyError:
            try:
                mid = _movies[title + ' (' + str(int(year)-1) + ')']
            except KeyError:
                while (True):
                    response = raw_input('\n"' + title + '" (' + year + ') not found. New movie (y/n)? ').lower()
                    if response not in ['y','n']:
                        print '**ERROR: y/n response please'
                        continue
                    break
                if response == 'y':
                    rating = raw_input('\nMPAA rating? ')
                    imdbId = raw_input('\nIMDB id? ')
                    if country == '' or country == '\n':
                        country = raw_input('Country (leave blank if unknown)? ')
                        if country == '':
                            country = 'DEFAULT'
                    else:
                        country = country.strip()

                    if country != 'DEFAULT' and country not in _countries:
                        _logFile.write('! Unknown country: ' + country + ' -- adding country\n')
                        _countries.append(country)
                        countryInsert = "INSERT INTO filth.country VALUES ('" + country + "');\n"
                        _countryInserts.append(countryInsert)
                    country = "'" + country + "'"

                    mid = _nextMid
                    _nextMid = str(int(_nextMid) + 1)
                    _movies[title + ' (' + year + ')'] = mid

                    movieInsert = "INSERT INTO filth.movie VALUES (" + mid + ", '" + title + "', " + year + ", 'not seen', '" + rating + "', " + country + ", NULL, '" + imdbId + "', NULL);\n"
                    _movieInserts.append(movieInsert)
                    _logFile.write('\n::: Added movie: "' + title + '" (' + year + ') [' + rating + '] ' + country + '\n')
                else:
                    mid = raw_input('\nWhat is the id of the movie? ')
    return mid


#-----------------------------------------------------------------------------

def getCid(name, mid, title, year, category):
    global _crewInserts, _workedOnInserts, _nextCid

    try:
        cid = _crew[name]

        position = CATEGORY_POSITIONS[category]

        if (mid, cid, position) not in _woCache:
            _logFile.write('### No worked_on entry found for ' + name + ' for "' + title + '" (' + year + ')--adding INSERT to worked_on.sql\n')
            
            position = "'" + position + "'"        
            woInsert = 'INSERT INTO filth.worked_on VALUES(' + str(mid) + ', ' + str(cid) + ', ' + position + ');  -- ' + name + ' for ' + title + ' (' + year + ')\n'
            _workedOnInserts.append(woInsert)
    except KeyError:
        while True:
            response = raw_input('\n' + name + ' (' + category + ' for ' + title + ' (' + year + ')) not found. New person? ').lower()
            if response not in ['n', 'y']:
                print "'y' or 'n'"
            else:
                break
        if response == 'n':
            raise KeyError
        
        names = name.split(' ')
        if len(names) == 1:
            first = 'NULL'
            middle = 'NULL'
            last = "'" + names[0] + "'"
        elif len(names) == 2:
            first = "'" + names[0] + "'"
            middle = 'NULL'
            last = "'" + names[1] + "'"
        elif len(names) == 3:
            first = "'" + names[0] + "'"
            middle = "'" + names[1] + "'"
            last = "'" + names[2] + "'"
        else:
            first = "'" + raw_input('First name: ') + "'"
            middle = "'" + raw_input('Middle name (or blank): ') + "'"
            last = "'" + raw_input('Last name: ') + "'"
            name = ' '.join[first, middle, last]

        while True:
            response = raw_input('Position? \n\t1. Director\n\t2. Screenwriter\n\t3. Cinematographer\n\t4. Actor\n\t5. Actress\n')
            if response not in ['1','2','3','4','5']:
                print "Only '1', '2', '3', '4', or '5' please\n"
            else:
                break

        position = POSITIONS[response]

        cid = _nextCid
        _nextCid = str(int(_nextCid) + 1)
        _crew[name] = cid

        crewInsert = 'INSERT INTO filth.crew_person VALUES (' + cid + ', ' + last + ', ' + first + ', ' + middle + ', \'' + name + '\', \'' + position + '\');  -- ' + position + ': ' + name + '\n'
        _crewInserts.append(crewInsert)

        woPosition = "'" + CATEGORY_POSITIONS[category] + "'"        
        woInsert = 'INSERT INTO filth.worked_on VALUES(' + mid + ', ' + cid + ', ' + woPosition + ');  -- ' + name + ' for ' + title + ' (' + year + ')\n'
        _workedOnInserts.append(woInsert)

    return cid


#-----------------------------------------------------------------------------

def processOscarFile():
    global _inserts, _logFile

    _logFile.write('\n--> Processing: ' + OSCARS_CSV + '\n')

    f = open(OSCARS_CSV)
    lines = f.readlines()
    f.close()

    for line in lines:
        _logFile.write('Line: ' + line)

        fields = line.split(',')
        
        year = fields[YEAR]
        category = fields[CATEGORY]
        oid = _oscar[category]
        status = fields[STATUS]
        title = fields[TITLE]
        country = fields[COUNTRY]

        if title == 'Food Inc.':
            mid = 3538
        elif title == 'Two Days One Night':
            mid = 3682
        else:
            mid = getMid(title, year, country)

        if '|' in fields[NOMINEES]:
            nominees = fields[NOMINEES].split('|')
            for nominee in nominees:
                cid = getCid(nominee, mid, title, year, category)
                insert = INSERT_FORMAT_STRING.format(mid, oid, cid, year, status, 'DEFAULT')
                comment = ' -- ' + year + ' ' + category + ': ' + nominee + ' for "' + title + '"\n'
                _inserts.append(insert + comment)
        else:
            if fields[NOMINEES] == '':
                cid = '0'
                comment = ' -- ' + year + ' ' + category + ': "' + title + '"\n'
            else:
                cid = getCid(fields[NOMINEES], mid, title, year, category)
                comment = ' -- ' + year + ' ' + category + ': ' + fields[NOMINEES] + ' for "' + title + '"\n'
            insert = INSERT_FORMAT_STRING.format(mid, oid, cid, year, status, 'DEFAULT')
            _inserts.append(insert + comment)


#-----------------------------------------------------------------------------

def writeInserts():
    if len(_inserts) > 0:
        f = open(OSCAR_GIVEN_TO_FILE, 'a')
        for insert in _inserts:
            f.write(insert)
        f.close()
    if len(_crewInserts) > 0:
        f = open(CREW_FILE, 'a')
        for insert in _crewInserts:
            f.write(insert)
        f.close()
    if len(_workedOnInserts) > 0:
        f = open(WORKED_ON_FILE, 'a')
        for insert in _workedOnInserts:
            f.write(insert)
        f.close()
    if len(_movieInserts) > 0:
        f = open(MOVIE_FILE, 'a')
        for insert in _movieInserts:
            f.write(insert)
        f.close()
    if len(_countryInserts) > 0:
        f = open(COUNTRY_FILE, 'a')
        for insert in _countryInserts:
            f.write(insert)
        f.close()


#-----------------------------------------------------------------------------

def quit():
    global _movieFile, _crewFile, _oscarFile, _oscarGivenToFile, _workedOnFile, _logFile

    if _movieFile:
        _movieFile.close()
    if _crewFile:
        _crewFile.close()
    if _workedOnFile:
        _workedOnFile.close()
    if _oscarFile:
        _oscarFile.close()
    if _oscarGivenToFile:
        _oscarGivenToFile.close()
    if _workedOnFile:
        _workedOnFile.close()
    if _logFile:
        _logFile.close()



#=============================================================================

if __name__ == '__main__':
    init()
    try:
        processOscarFile()
    except Exception:
        traceback.print_exc()
    finally:
        writeInserts()
        quit()

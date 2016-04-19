#!/usr/bin/env python

import re
import traceback
from os import getenv

YEAR = 0
CATEGORY = 1
TITLE = 2
NOMINEES = 3
STATUS = 4
SCENE = 5

POSITIONS = {'1':'Director','2':'Screenwriter','3':'Cinematographer','4':'Actor','5':'Actress','6':'Lead Actor','7':'Supporting Actor','8':'Lead Actress','9':'Supporting Actress'}
CATEGORY_POSITIONS = {'Best Actor':'Lead Actor','Best Actress':'Lead Actress','Best Supporting Actor':'Supporting Actor','Best Supporting Actress':'Supporting Actress','Best Director':'Director','Best Cinematography':'Cinematographer','Best Adapted Screenplay':'Screenwriter','Best Original Screenplay':'Screenwriter'}

FIRST_YEAR = 1997
LAST_YEAR = 2012
FILTH_PATH = getenv('FILTH_PATH', '/home/tgh/workspace/FiLTH')
DATA_PATH = FILTH_PATH + '/data/'
MOVIE_FILE = FILTH_PATH + '/sql/movie.sql'
CREW_FILE = FILTH_PATH + '/sql/crew_person.sql'
TYLER_FILE = FILTH_PATH + '/sql/tyler.sql'
TYLER_GIVEN_TO_FILE = FILTH_PATH + '/sql/tyler_given_to.sql'
WORKED_ON_FILE = FILTH_PATH + '/sql/worked_on.sql'
LOG_FILE = FILTH_PATH + '/logs/tyler_awards.log'
                                                               # id, mid, tid, cid, status, scene
INSERT_FORMAT_STRING = "INSERT INTO filth.tyler_given_to VALUES ({0}, {1}, {2}, {3}, {4}, {5});";

_inserts = []
_crewInserts = []
_workedOnInserts = []
_movies = {}    # "title (year)" -> id 
_crew = {}      # "firstname middlename lastname" -> id
_tyler = {}     # "category" -> id
_movieFile = None
_crewFile = None
_tylerFile = None
_tylerGivenToFile = None
_workedOnFile = None
_logFile = None
_nextCid = 0
_nextWid = 0
_nextTgtId = 0



#-----------------------------------------------------------------------------

def init():
    global _logFile

    _logFile = open(LOG_FILE, 'w')
    initMoviesMap()
    initCrewMap()
    initTylerMap()
    initNextWid()
    initNextTgtId()


#-----------------------------------------------------------------------------

def initMoviesMap():
    global _movieFile, _logFile, _movies

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
        title = title.replace(',','')

        '''
        if title == 'Good Night, and Good Luck.':
            title = 'Good Night and Good Luck.'
        elif title == 'The Chronicles of Narnia: The Lion, the Witch, and the Wardrobe':
            title = 'The Chronicles of Narnia: The Lion the Witch and the Wardrobe'
        elif title == 'Oslo, August 31st':
            title = 'Oslo August 31st'
        elif title == 'Crazy, Stupid, Love.':
            title = 'Crazy Stupid Love.'
        elif title == 'Fast, Cheap, and Out of Control':
            title = 'Fast Cheap and Out of Control'
        elif title == 'Crouching Tiger, Hidden Dragon':
            title = 'Crouching Tiger Hidden Dragon'
        '''

        vals = vals[(titleEndIndex + 3):]
        vals = vals.split(', ')
        #skip movie haven't seen
        if vals[1] == "'not seen'":
          continue

        year = vals[0]

        _movies[title + ' (' + year + ')'] = mid
        _logFile.write('Movie: ' + title + ' (' + year + ') -> ' + mid + '\n')


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
        _logFile.write('Crew: ' + name + ' -> ' + cid + '\n')

    _nextCid = str(int(cid) + 1)


#-----------------------------------------------------------------------------

def initTylerMap():
    global _tylerFile, _logFile, _tyler
    
    _logFile.write('\n\n>>> Initializing tyler map <<<\n\n')
    _tylerFile = open(TYLER_FILE, 'r')
    lines = _tylerFile.readlines()
    _tylerFile.close()

    for line in lines:
        line = line.replace("''", "'")
        vals = re.search('VALUES \\((.*)\\);', line).group(1).split(',')

        tid = vals[0]
        name = vals[1].strip().strip("'")

        _tyler[name] = tid
        _logFile.write('Award category: ' + name + ' -> ' + tid + '\n')


#-----------------------------------------------------------------------------

def initNextWid():
    global _nextWid

    f = open(WORKED_ON_FILE, 'r')
    lines = f.readlines()
    f.close()

    lastLine = lines[len(lines)-1]
    vals = re.search('VALUES \\((\d+)\\);', line).group(1).split(',')
    wid = vals[0]
    _nextWid = str(int(wid) + 1)


#-----------------------------------------------------------------------------

def initNextTgtId():
    global _nextTgtId

    f = open(TYLER_GIVEN_TO_FILE, 'r')
    lines = f.readlines()
    f.close()

    lastLine = lines[len(lines)-1]
    vals = re.search('VALUES \\((\d+)\\);', lastLine).group(1).split(',')
    tgtid = vals[0]
    _nextTgtId = str(int(tgtid) + 1)


#-----------------------------------------------------------------------------

def getMid(title, year):
    try:
        mid = _movies[title + ' (' + year + ')']
    except KeyError:
        try:
            mid = _movies[title + ' (' + str(int(year)+1) + ')']
        except KeyError:
            mid = _movies[title + ' (' + str(int(year)-1) + ')']
    return mid


#-----------------------------------------------------------------------------

def getCid(name, mid, title, year, category):
    global _crew, _crewInserts, _workedOnInserts, _nextCid

    try:
        cid = _crew[name]
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

        wid = _nextWid
        _nextWid = str(int(_nextWid) + 1)
        woPosition = "'" + CATEGORY_POSITIONS[category] + "'"

        woInsert = 'INSERT INTO filth.worked_on VALUES(' + wid + ', ' + mid + ', ' + cid + ', ' + woPosition + ');  -- ' + name + ' for ' + title + ' (' + year + ')\n'
        _workedOnInserts.append(woInsert)
    return cid


#-----------------------------------------------------------------------------

def processAwardFile(filename):
    global _inserts, _logFile

    _logFile.write('\n--> Processing: ' + filename + '\n')

    f = open(DATA_PATH + filename)
    lines = f.readlines()
    f.close()

    for line in lines:
        _logFile.write('Line: ' + line)

        fields = line.split(',')
        #some fields might be surrounded by double-quotes--strip those off
        fields = [field.strip('"') for field in fields]

        if fields[YEAR] == 'YEAR':
            #this is the header row, skip it
            continue
        
        year = fields[YEAR]
        tid = _tyler[fields[CATEGORY]]
        status = fields[STATUS]
        if fields[SCENE].strip() == '':
            scene = 'DEFAULT'
        else:
            scene = "'" + fields[SCENE].strip().strip('"').replace("'", "''") + "'"

        if '|' in fields[TITLE]:
            movies = fields[TITLE].split('|')
            for movie in movies:
                mid = getMid(movie, year)
                cid = getCid(fields[NOMINEES], mid, movie, year, fields[CATEGORY])
                tgtid = _nextTgtId
                _nextTgtId = str(int(_nextTgtId) + 1)
                insert = INSERT_FORMAT_STRING.format(tgtid, mid, tid, cid, status, scene)
                comment = ' -- ' + fields[NOMINEES] + ' for ' + fields[CATEGORY] + ' for ' + movie + ' (' + year + ')\n'
                _inserts.append(insert + comment)
            continue
        else:
            mid = getMid(fields[TITLE], year)

        if '|' in fields[NOMINEES]:
            nominees = fields[NOMINEES].split('|')
            for nominee in nominees:
                cid = getCid(nominee, mid, fields[TITLE], year, fields[CATEGORY])
                tgtid = _nextTgtId
                _nextTgtId = str(int(_nextTgtId) + 1)
                insert = INSERT_FORMAT_STRING.format(tgtid, mid, tid, cid, status, scene)
                comment = ' -- ' + nominee + ' for ' + fields[CATEGORY] + ' for ' + fields[TITLE] + ' (' + year + ')\n'
                _inserts.append(insert + comment)
        else:
            if fields[NOMINEES] == '':
                cid = '0'
                comment = ' -- ' + fields[TITLE] + ' (' + year + ')' + ' for ' + fields[CATEGORY] + '\n'
            else:
                cid = getCid(fields[NOMINEES], mid, fields[TITLE], year, fields[CATEGORY])
                comment = ' -- ' + fields[NOMINEES] + ' for ' + fields[CATEGORY] + ' for ' + fields[TITLE] + ' (' + year + ')\n'
            tgtid = _nextTgtId
            _nextTgtId = str(int(_nextTgtId) + 1)
            insert = INSERT_FORMAT_STRING.format(tgtid, mid, tid, cid, status, scene)
            _inserts.append(insert + comment)


#-----------------------------------------------------------------------------

def processAwardFiles():
    global _inserts

    for i in range(FIRST_YEAR, LAST_YEAR+1):
        _inserts.append('\n-- ' + str(i) + '\n')
        processAwardFile(str(i) + '.csv')


#-----------------------------------------------------------------------------

def writeInserts():
    if len(_inserts) > 0:
        f = open(TYLER_GIVEN_TO_FILE, 'w')
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


#-----------------------------------------------------------------------------

def quit():
    global _movieFile, _crewFile, _tylerGivenToFile, _logFile

    if _movieFile:
        _movieFile.close()
    if _crewFile:
        _crewFile.close()
    if _workedOnFile:
        _workedOnFile.close()
    if _tylerFile:
        _tylerFile.close()
    if _tylerGivenToFile:
        _tylerGivenToFile.close()
    if _workedOnFile:
        _workedOnFile.close()
    if _logFile:
        _logFile.close()


#=============================================================================

if __name__ == '__main__':
    init()
    try:
        processAwardFiles()
    except Exception:
        traceback.print_exc()
    finally:
        writeInserts()
        quit()

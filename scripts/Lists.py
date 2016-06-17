#!/usr/bin/env python

from os import getenv
import re

FILTH_PATH = getenv('FILTH_PATH', '/home/tgh/workspace/FiLTH')
LIST_SQL_FILE = FILTH_PATH + '/sql/list.sql'
LIST_CONTAINS_SQL_FILE = FILTH_PATH + '/sql/list_contains.sql'
                                                    #lid, title, author
LIST_INSERT_FORMAT = "INSERT INTO filth.list VALUES ({0}, '{1}', {2});"
                                                                      #id, mid, lid, order, comment,  movie title,  list title, author   
LIST_CONTAINS_INSERT_FORMAT = "INSERT INTO filth.list_contains VALUES ({0}, {1}, {2}, {3}, {4}); -- \"{5}\" in list \"{6}\" by {7}"


class Lists(object):

    def __init__(self, logFile):
        self._logFile = logFile
        self._lists = []
        #initialize lists and get next lid
        self._nextLid = self._initLists() + 1
        self._listInserts = []
        self._listContainsInserts = []
        self._nextListContainsId = self._getNextListContainsId()
        self._listContainsMap = {}  #lid -> [mids]
        self._initListContainsMap()
        self._log('__init__', 'Next lid: ' + str(self._nextLid))


    #--------------------------------------------------------------------------

    def _log(self, func, message):
        ''' Writes a message to the log file

            func (string) : name of the function current execution is in at the time of this log entry
            message (string) : log entry message
        '''
        self._logFile.write('[Lists.' + func + '] - ' + message + '\n')


    #----------------------------------------------------------------------------

    def _initLists(self):
        self._log('_initLists', '>> Initializing Lists <<')
        f = open(LIST_SQL_FILE, 'r')
        lines = f.readlines()
        f.close()
        lid = 0
        for line in lines:
            line = line.replace("''", "'")
            matcher = re.search('VALUES \\(([0-9]+), \'([^\']+?)\', ([^\\)]+)\\);', line)
            mlist = {}
            mlist['lid'] = int(matcher.group(1))
            mlist['title'] = matcher.group(2)
            mlist['author'] = matcher.group(3).strip("'")
            self._lists.append(mlist)
            lid = mlist['lid']
            self._log('_initLists', 'List: "' + mlist['title'] + '" by ' + mlist['author'])
        self._log('_initLists', '>> Lists initialized <<')
        return lid


    #----------------------------------------------------------------------------

    def _initListContainsMap(self):
        self._log('_initListContainsMap', '>> Initializing ListContains map <<')
        f = open(LIST_CONTAINS_SQL_FILE, 'r')
        lines = f.readlines()
        f.close()

        for line in lines:
            matcher = re.search('VALUES *\\([0-9]+, ([0-9]+), ([0-9]+),', line)
            mid = int(matcher.group(1))
            lid = int(matcher.group(2))
            try:
                mids = self._listContainsMap[lid]
            except KeyError:
                mids = []
                self._listContainsMap[lid] = mids
            mids.append(mid)
                
        self._log('_initListContainsMap', '>> ListContains map initialized <<')


    #----------------------------------------------------------------------------

    def _getNextListContainsId(self):
        f = open(LIST_CONTAINS_SQL_FILE, 'r')
        lines = f.readlines()
        f.close()

        lastline = lines[-1]
        lcid = int(re.search('VALUES *\\(([0-9]+),', lastline).group(1))
        return lcid + 1


    #----------------------------------------------------------------------------

    def _createInsertStatementForList(self, title, author):
        if author is None:
            author = 'NULL'
        elif author != 'NULL':
            author = "'" + author + "'"

        insertStatement = LIST_INSERT_FORMAT.format(str(self._nextLid), title, author)
        self._log('_createInsertStatementForList', 'created SQL: ' + insertStatement)
        self._listInserts.append(insertStatement)
        self._nextLid += 1


    #----------------------------------------------------------------------------

    def _createInsertStatementForListContains(self, mid, lid, order, comment, movieTitle, listTitle, author):
        if order is None or order == '':
            order = 'NULL'

        if comment is None:
            comment = 'NULL'
        elif comment != 'NULL':
            comment = "'" + comment + "'"

        if author is None:
            author = '[no author]'

        insertStatement = LIST_CONTAINS_INSERT_FORMAT.format(str(self._nextListContainsId), mid, lid, order, comment, movieTitle, listTitle, author)
        self._log('_createInsertStatementForListContains', 'created SQL: ' + insertStatement)
        self._listContainsInserts.append(insertStatement)
        self._nextListContainsId += 1


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
    def addList(self, title, author):
        mlist = {}
        mlist['lid'] = self._nextLid
        mlist['title'] = title.replace("'","''")
        mlist['author'] = author
        self._lists.append(mlist)
        self._createInsertStatementForList(title, author)
        return mlist


    #--------------------------------------------------------------------------
    #PUBLIC
    #
    def addMovieToList(self, mid, lid, order, comment, movieTitle, listTitle, author):
        ''' This is a no-op if the given relationship already exists
        '''
        if lid in self._listContainsMap and mid in self._listContainsMap[lid]:
            #this relationship already exists
            self._log('addMovieToList', '! ListContains relationship already exists: (mid: ' + str(mid) + ', lid: ' + str(lid) + '). Not adding.')
            return
        self._createInsertStatementForListContains(mid, lid, order, comment, movieTitle, listTitle, author)


    #--------------------------------------------------------------------------
    #PUBLIC
    #
    def getListByTitleAndAuthor(self, title, author):
        ''' Gets a list object using the given title and author.

            title (string)  : a list title
            author (string) : an author name (or None)

            Returns: A list object matching the given title and author, or None if not found
        '''
        for mlist in self._lists:
            if mlist['title'] == title and mlist['author'] == author:
                return mlist

        if author is None:
            self._log('getListByTitleAndAuthor', 'list not found: "' + title + '" (no author)')
        else:
            self._log('getListByTitleAndAuthor', 'list not found: "' + title + '" by ' + author)
        return None


    #----------------------------------------------------------------------------
    #PUBLIC
    #
    def writeAllInsertsToFiles(self):
        self.writeListContainsInsertsToFile()
        self.writeListInsertsToFile()


    #----------------------------------------------------------------------------
    #PUBLIC
    #
    def writeListContainsInsertsToFile(self):
        f = open(LIST_CONTAINS_SQL_FILE, 'a')
        for statement in self._listContainsInserts:
            f.write(statement + '\n')
        f.close()


    #----------------------------------------------------------------------------
    #PUBLIC
    #
    def writeListInsertsToFile(self):
        f = open(LIST_SQL_FILE, 'a')
        for statement in self._listInserts:
            f.write(statement + '\n')
        f.close()


    #----------------------------------------------------------------------------
    #PUBLIC
    #
    def hasInserts(self):
        return len(self._listInserts) > 0 or len(self._listContainsInserts) > 0


    #----------------------------------------------------------------------------
    #PUBLIC
    #
    def close(self):
        self._listInserts = []
        self._lists = []
        self._listContainsInserts = []

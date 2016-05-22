#!/usr/bin/env python

from os import getenv

FILTH_PATH = getenv('FILTH_PATH', '/home/tgh/workspace/FiLTH')
MOVIE_SQL_FILE = FILTH_PATH + '/sql/movie.sql'
LIST_CSV_FILE = FILTH_PATH + '/data/lists.csv'
LOG_FILENAME = FILTH_PATH + '/logs/crew_and_tag.log'

LIST   = 0
AUTHOR = 1
YEAR   = 2
MOVIE  = 3
ORDER  = 4

movies = {}
logger = None

def log(func, message):
  try:
    logger.write('[' + func + '] - ' + message + '\n')
  except UnicodeEncodeError:
    logger.write('[' + func + '] - MOVIE: 8 1/2 (1963)\n')


def createMovie(vals):
    movie = {}
    movie['mid'] = int(re.search('(\d+)', vals).group(1))
    titleStartIndex = vals.find("'") + 1
    titleEndIndex = vals.find("', ")
    movie['title'] = vals[titleStartIndex:titleEndIndex]
    vals = vals[(titleEndIndex + 3):]
    vals = vals.split(', ')
    movie['star_rating'] = vals[1]
    if vals[0] == 'NULL':
        movie['year'] = vals[0]
    else:
        movie['year'] = int(vals[0])
    return movie


def initMovies(lastProcessed):
  global movies

  log('initMovies', '>> Initializing movie map <<')
  movieFile = open(MOVIE_SQL_FILE, 'r')
  movielines = movieFile.readlines()
  movieFile.close()
  for movieline in movielines:
    movieline = movieline.replace("''", "'")
    vals = re.search('VALUES \\((.*)\\);', movieline).group(1)
    movie = createMovie(vals)

    #skip movie if already tagged
    if int(movie['mid']) <= lastProcessed:
      continue
    #skip movies not seen
    if movie['star_rating'] == "'not seen'":
      continue

    movies.append(movie)
  log('initMovies', '>> movie map initialized <<')


def quit():
  if logger:
    logger.close()


if __name__ == '__main__':
    logger = open(LOG_FILENAME, 'w')

    try:
      f = open(LIST_CSV_FILE, 'r')
      lines = map(str.strip, f.readlines())
      f.close()

      for line in lines:
        vals = line.split('|')
        print vals
        break
    except Exception:
      pass
    finally
      quit()

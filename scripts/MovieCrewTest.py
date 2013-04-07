#!/usr/bin/env python

from MovieCrew import MovieCrew
from QuitException import QuitException
import imp

if __name__ == '__main__':
  models = imp.load_source('models', '/home/tgh/workspace/FiLTH/src/python/models.py')
  log = open('/home/tgh/workspace/FiLTH/temp/movieCrew_temp.log', 'w')

  movieCrew = MovieCrew('/home/tgh/workspace/FiLTH/temp/wosql_temp.sql', '/home/tgh/workspace/FiLTH/temp/crewsql_temp.sql', log, models)
  movies = models.Movie.query.all()
  try:
    for movie in movies:
      movieCrew.promptUserForCrewPerson(movie.mid, movie.title, movie.year)
  except QuitException:
    movieCrew.flush()
    movieCrew.close()

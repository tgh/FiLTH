#!/usr/bin/env python

from MovieCrew import MovieCrew
from QuitException import QuitException
import imp

if __name__ == '__main__':
  models = imp.load_source('models', '/home/tgh/workspace/FiLTH/src/python/models.py')
  positions = []
  for position in models.Position.query.all():
    positions.append(str(position.position_title))
  log = open('/home/tgh/workspace/FiLTH/temp/movieCrew_temp.log', 'w')
  nextCid = models.session.query(models.CrewPerson).order_by(models.CrewPerson.cid.desc()).first().cid + 1
  nextMid = models.session.query(models.Movie).order_by(models.Movie.mid.desc()).first().mid + 1

  movieCrew = MovieCrew('/home/tgh/workspace/FiLTH/temp/wosql_temp.sql', '/home/tgh/workspace/FiLTH/temp/crewsql_temp.sql', log, positions, nextCid, nextMid)
  movies = models.Movie.query.all()
  try:
    for movie in movies:
      movieCrew.promptUserForCrewPerson(movie.title, movie.year)
  except QuitException:
    movieCrew.flush()
    movieCrew.close()

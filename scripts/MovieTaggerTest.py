#!/usr/bin/env python

from MovieTagger import MovieTagger
from QuitException import QuitException
import imp

if __name__ == '__main__':
  models = imp.load_source('models', '/home/tgh/workspace/FiLTH/src/python/models.py')
  log = open('/home/tgh/workspace/FiLTH/temp/tagger_temp.log', 'w')
  tagger = MovieTagger('/home/tgh/workspace/FiLTH/temp/tgtsql_temp.sql', '/home/tgh/workspace/FiLTH/temp/tagsql_temp.sql', log)
  movies = models.Movie.query.all()
  try:
    map(tagger.promptUserForTag, movies)
  except QuitException:
    tagger.flush()
    tagger.close()

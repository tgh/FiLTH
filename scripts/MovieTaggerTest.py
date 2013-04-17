#!/usr/bin/env python

from MovieTagger import MovieTagger
from QuitException import QuitException
import imp

if __name__ == '__main__':
  models = imp.load_source('models', '/home/tgh/workspace/FiLTH/src/python/models.py')
  log = open('/home/tgh/workspace/FiLTH/logs/MovieTaggerTest.log', 'w')
  tagger = MovieTagger('/home/tgh/workspace/FiLTH/temp/tgtsql_temp.sql', '/home/tgh/workspace/FiLTH/temp/tagsql_temp.sql', log, models)
  movies = models.Movie.query.all()
  try:
    for m in movies:
      tagger.promptUserForTag(m.mid, m.title, m.year)
  except QuitException:
    tagger.flush()
    tagger.close()

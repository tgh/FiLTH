#!/usr/bin/env python

from MovieTagger import MovieTagger
import imp

if __name__ == '__main__':
  models = imp.load_source('models', '/home/tgh/workspace/FiLTH/src/python/models.py')
  log = open('/home/tgh/workspace/FiLTH/temp/tagger_temp.log', 'w')
  tagger = MovieTagger('/home/tgh/workspace/FiLTH/temp/tgtsql_temp.sql', '/home/tgh/workspace/FiLTH/temp/tagsql_temp.sql', log, models.session)
  movies = models.Movie.query.all()
  map(tagger.promptUserForTag, movies)

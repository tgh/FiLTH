#!/usr/bin/env python

from MovieCrew import MovieCrew
from QuitException import QuitException
import traceback

if __name__ == '__main__':
  log = open('/home/thayes/Projects/FiLTH/logs/MovieCrewTest.log', 'w')

  movieCrew = None
  try:
    movieCrew = MovieCrew('/home/thayes/Projects/FiLTH/sql/worked_on.sql', '/home/thayes/Projects/FiLTH/sql/crew_person.sql', log)
    movies = [(1063, 'Michael Clayton', 2007), (792, 'Il Postino', 1997), (1460, 'Shawshank', 1994)]
    for movie in movies:
      movieCrew.promptUserForCrewPerson(movie[0], movie[1], movie[2])
  except QuitException:
    print '\nQUITTING\n'
  except Exception as e:
    print '\n***EXCEPTION!***'
    traceback.print_exc()
  finally:
    if movieCrew:
      if movieCrew.hasInserts():
        while True:
          response = raw_input('\nThere are still unwritten sql insert statements. Write them out? ').lower()
          if response not in ['y','n']:
            print "Only 'y'/'n'\n"
            continue
          if response == 'y':
            movieCrew.writeCrewInsertsToFile(open('/home/thayes/Projects/FiLTH/sql/crew_person.sql', 'a'))
            movieCrew.writeWorkedOnInsertsToFile(open('/home/thayes/Projects/FiLTH/sql/worked_on.sql', 'a'))
            break
          else:
            break
      movieCrew.close()
    log.close()

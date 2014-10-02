#!/usr/bin/env python

from MovieCrew import MovieCrew
from QuitException import QuitException

if __name__ == '__main__':
  log = open('/home/thayes/Projects/FiLTH/logs/MovieCrewTest.log', 'w')

  movieCrew = None
  try:
    movieCrew = MovieCrew('/home/thayes/Projects/FiLTH/sql/worked_on.sql', '/home/thayes/Projects/FiLTH/sql/crew_person.sql', log)
    movieCrew.promptUserForCrewPerson(99999, 'Foo', 2000)
  except QuitException:
    print '\nQUITTING\n'
  except Exception:
    print '\n***EXCEPTION!***\n'
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

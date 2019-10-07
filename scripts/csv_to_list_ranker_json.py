#!/usr/bin/env python

"""
Usage:
    csv_to_list_ranker_json.py FILE TITLE

Options:
    -h --help       Show this screen.

"""

import sys
import csv
import requests
import time
from os import path
from docopt import docopt


csv.register_dialect('pipes', delimiter='|')


def getImageUrl(movie):
    # be nice to TMDB
    time.sleep(0.5)

    tmdbId = movie['tmdb_id']
    title = movie['title']
    response = requests.get(
        'https://api.themoviedb.org/4/movie/' + tmdbId + '/images',
        params={'api_key': '02c05c166237c1a2e3e5cc2744077613'}
    )
    if response.status_code != 200:
        message = '\nERROR from tmdb request for "' + title + '": ' + response.text + '\n'
        raise Exception(message)
    else:
        try:
            response_json = response.json()
            posters = response_json['posters']
            if not posters:
                message = '\nERROR no posters for "' + title + '": ' + response.text + '\n'
                print(message)
                raise Exception(message)
            else:
                filePath = posters[0]['file_path']
                return 'https://image.tmdb.org/t/p/w185/' + filePath
        except KeyError:
            message = '\nERROR missing "posters" key in json for "' + title + '": ' + response.text + '\n'
            raise Exception(message)


if __name__ == '__main__':
    args = docopt(__doc__)
    csvFile = args['FILE']
    title = args['TITLE']

    if not path.isfile(csvFile):
        print(f'***ERROR: {csvFile} does not exist or is not a file')
        sys.exit(1)

    f = open(csvFile)
    csv_lines = f.readlines()
    f.close()
    movies = csv.DictReader(csv_lines, dialect='pipes') # '|' as the field separator

    print('{')
    print('  "id": TO_BE_FILLED_IN,')
    print(f'  "title": "{title}",')
    print('  "items": [')

    for movie in movies:
        imageUrl = getImageUrl(movie)
        print('    {')
        print(f'      "text": "{movie["title"]} ({movie["year"]})",')
        print(f'      "imageUrl": "{imageUrl}"')
        print('    },')

    # close out the json
    print('  ]')
    print('}')

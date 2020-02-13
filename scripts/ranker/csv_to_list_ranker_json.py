#!/usr/bin/env python

"""
Usage:
    csv_to_list_ranker_json.py FILE TITLE

Options:
    -h --help       Show this screen.

"""

import sys
import csv
from os import path
from docopt import docopt
from util import getImageUrl, getImdbUrl

csv.register_dialect('pipes', delimiter='|')


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
        try:
            imageUrl = getImageUrl(movie['tmdb_id'])
        except Exception as e:
            print(f'Exception: {e}')
            continue
        imdbUrl = getImdbUrl(movie['tmdb_id'])
        print('    {')
        print(f'      "text": "{movie["title"]} ({movie["year"]})",')
        print(f'      "imageUrl": "{imageUrl}",')
        print(f'      "textLinkUrl": "{imdbUrl}",')
        print('    },')

    # close out the json
    print('  ]')
    print('}')

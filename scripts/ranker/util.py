import requests
import time
from os import getenv

TMDB_API_KEY = getenv('TMDB_API_KEY')   #environment variable TMDB_API_KEY required


def getImageUrl(tmdbId):
    # be nice to TMDB
    time.sleep(0.5)

    response = requests.get(
        'https://api.themoviedb.org/3/movie/' + tmdbId + '/images',
        params={'api_key': TMDB_API_KEY}
    )
    if response.status_code != 200:
        message = '\nERROR from tmdb request for "' + tmdbId + '": ' + response.text + '\n'
        raise Exception(message)
    else:
        try:
            response_json = response.json()
            posters = response_json['posters']
            if not posters:
                message = '\nERROR no posters for "' + tmdbId + '": ' + response.text + '\n'
                print(message)
                raise Exception(message)
            else:
                filePath = posters[0]['file_path']
                return 'https://image.tmdb.org/t/p/w185/' + filePath
        except KeyError:
            message = '\nERROR missing "posters" key in json for "' + tmdbId + '": ' + response.text + '\n'
            raise Exception(message)

def getImdbUrl(imdbId):
    return f'http://www.imdb.com/title/{imdbId}'
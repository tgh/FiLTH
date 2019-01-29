#!/usr/bin/env python

import requests
import time

if __name__ == '__main__':
    f = open('../data/four_star_movies.csv', 'r')
    lines = f.readlines()
    f.close()
    print 'export const TOP_100_MOVIES = {'
    print '  title: "Top 100 Movies",'
    print '  items: ["'
    for line in lines:
        title, year, id = line.split('|')
        response = requests.get(
            'https://api.themoviedb.org/3/movie/' + id + '/images',
            params={'api_key': '02c05c166237c1a2e3e5cc2744077613'}
        )
        if response.status_code != 200:
            print '\nERROR from tmdb request for "' + title + '": ' + response.text + '\n'
        else:
            try:
                response_json = response.json()
                posters = response_json['posters']
                if not posters:
                    print '\nERROR no posters for "' + title + '": ' + response.text + '\n'
                else:
                    filePath = posters[0]['file_path']
            except KeyError:
                print '\nERROR missing "posters" key in json for "' + title + '": ' + response.text + '\n'
            print '    {text:"' + title + ' (' + year + ')",imageTitle:"' + title + '",imageUrl:"https://image.tmdb.org/t/p/w185/' + filePath + '"},'
        time.sleep(1)
    print '  ],'
    print '};'

select m.title, m.year, m.star_rating, m.tmdb_id
from filth.movie m
join filth.tag_given_to tgt using (mid)
join filth.tag t using (tid)
where t.tag_name = 'action'
and (m.star_rating = '****'
or m.star_rating = '***½'
or m.star_rating = '***')
order by star_rating <> '****',
         star_rating <> '***½',
         star_rating <> '***';
-----------------------------
-- Primary Key constraints --
-----------------------------

ALTER TABLE movie ADD CONSTRAINT movie_pkey PRIMARY KEY(mid);
ALTER TABLE crew_person ADD CONSTRAINT crew_pkey PRIMARY KEY(cid);
ALTER TABLE worked_on ADD CONSTRAINT worked_pkey PRIMARY KEY(mid, cid, position);
ALTER TABLE genre ADD CONSTRAINT genre_pkey PRIMARY KEY(genname);
ALTER TABLE genre_contains ADD CONSTRAINT genrecontains_pkey PRIMARY KEY(mid, genname);
ALTER TABLE oscar ADD CONSTRAINT oscar_pkey PRIMARY KEY (category);
ALTER TABLE oscar_given_to ADD CONSTRAINT oscargiven_pkey PRIMARY KEY(mid, category);
ALTER TABLE list ADD CONSTRAINT list_pkey PRIMARY KEY(listtitle);
ALTER TABLE list_contains ADD CONSTRAINT listcontains_pkey PRIMARY KEY(mid, listtitle);
ALTER TABLE country ADD CONSTRAINT country_pkey PRIMARY KEY(countryname);

-----------------------------
-- Foreign Key constraints --
-----------------------------

-- movie table country FK
ALTER TABLE movie ADD CONSTRAINT movie_country_fkey
FOREIGN KEY (country) REFERENCES country(country_name)
ON UPDATE CASCADE ON DELETE SET NULL;

-- workedon table movie FK
ALTER TABLE worked_on ADD CONSTRAINT worked_mov_fkey
FOREIGN KEY (mid) REFERENCES movie(mid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- worked on table crew FK
ALTER TABLE worked_on ADD CONSTRAINT worked_crew_fkey
FOREIGN KEY (cid) REFERENCES crew_person(cid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- genrecontains table movie FK
ALTER TABLE genre_contains ADD CONSTRAINT genre_movie_fkey
FOREIGN KEY (mid) REFERENCES movie(mid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- genrecontains table genre FK
ALTER TABLE genre_contains ADD CONSTRAINT genre_genname_fkey
FOREIGN KEY (gen_name) REFERENCES genre(gen_name)
ON UPDATE CASCADE ON DELETE CASCADE;

-- oscargivento table movie FK
ALTER TABLE oscar ADD CONSTRAINT oscar_movie_fkey
FOREIGN KEY (mid) REFERENCES movie(mid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- oscargivento table recipient FK
ALTER TABLE oscar ADD CONSTRAINT oscar_recip_fkey
FOREIGN KEY (recipient) REFERENCES crew_person(cid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- oscargivento table category FK
ALTER TABLE oscar ADD CONSTRAINT oscar_cat_fkey
FOREIGN KEY (category) REFERENCES oscar(category)
ON UPDATE CASCADE ON DELETE CASCADE;

-- listcontains table movie FK
ALTER TABLE list_contains ADD CONSTRAINT list_movie_fkey
FOREIGN KEY (mid) REFERENCES movie(mid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- listcontains table list FK
ALTER TABLE list_contains ADD CONSTRAINT list_title_fkey
FOREIGN KEY (list_title) REFERENCES list(list_title)
ON UPDATE CASCADE ON DELETE CASCADE;

---------------------------
-- Integrity constraints --
---------------------------

-- movie year
ALTER TABLE movie ADD CONSTRAINT year_constraint
CHECK (year >= 1900 AND year <= 2012);

-- movie star rating
ALTER TABLE movie ADD CONSTRAINT star_constraint
CHECK (my_star_rating = 'NO STARS' OR
       my_star_rating = '&frac12*' OR
       my_star_rating = '*' OR
       my_star_rating = '*&frac12' OR
       my_star_rating = '**' OR
       my_star_rating = '**&frac12' OR
       my_star_rating = '***' OR
       my_star_rating = '***&frac12' OR
       my_star_rating = '****' OR
       my_star_rating = 'N/A' OR
       my_star_rating = '[not seen]');

-- movie mpaa rating
ALTER TABLE movie ADD CONSTRAINT mpaa_constraint
CHECK (mpaa = 'NR' OR
       mpaa = 'G' OR
       mpaa = 'PG' OR
       mpaa = 'PG-13' OR
       mpaa = 'R' OR
       mpaa = 'X' OR
       mpaa = 'NC-17');

-- listcontains rank
ALTER TABLE list_contains ADD CONSTRAINT rank_constraint
CHECK (rank > 0);

-- oscargivento status
ALTER TABLE oscar_given_to ADD CONSTRAINT osc_stat_constraint
CHECK (status = 'won' OR
       status = 'nominated');

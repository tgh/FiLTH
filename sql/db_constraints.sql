-----------------------------
-- Primary Key constraints --
-----------------------------

ALTER TABLE movie ADD CONSTRAINT movie_pkey PRIMARY KEY(movieid);
ALTER TABLE crewperson ADD CONSTRAINT crew_pkey PRIMARY KEY(crewid);
ALTER TABLE workedon ADD CONSTRAINT worked_pkey PRIMARY KEY(movieid, crewid, position);
ALTER TABLE genre ADD CONSTRAINT genre_pkey PRIMARY KEY(genname);
ALTER TABLE genrecontains ADD CONSTRAINT genrecontains_pkey PRIMARY KEY(movieid, genname);
ALTER TABLE oscar ADD CONSTRAINT oscar_pkey PRIMARY KEY (category);
ALTER TABLE oscargivento ADD CONSTRAINT oscargiven_pkey PRIMARY KEY(movieid, category);
ALTER TABLE list ADD CONSTRAINT list_pkey PRIMARY KEY(listtitle);
ALTER TABLE listcontains ADD CONSTRAINT listcontains_pkey PRIMARY KEY(movieid, listtitle);
ALTER TABLE country ADD CONSTRAINT country_pkey PRIMARY KEY(countryname);

-----------------------------
-- Foreign Key constraints --
-----------------------------

-- movie table country FK
ALTER TABLE movie ADD CONSTRAINT movie_country_fkey
FOREIGN KEY (country) REFERENCES country(countryname)
ON UPDATE CASCADE ON DELETE SET NULL;

-- workedon table movie FK
ALTER TABLE workedon ADD CONSTRAINT worked_mov_fkey
FOREIGN KEY (movieid) REFERENCES movie(movieid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- worked on table crew FK
ALTER TABLE workedon ADD CONSTRAINT worked_crew_fkey
FOREIGN KEY (crewid) REFERENCES crewperson(crewid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- genrecontains table movie FK
ALTER TABLE genrecontains ADD CONSTRAINT genre_movie_fkey
FOREIGN KEY (movieid) REFERENCES movie(movieid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- genrecontains table genre FK
ALTER TABLE genrecontains ADD CONSTRAINT genre_genname_fkey
FOREIGN KEY (genname) REFERENCES genre(genname)
ON UPDATE CASCADE ON DELETE CASCADE;

-- oscargivento table movie FK
ALTER TABLE oscar ADD CONSTRAINT oscar_movie_fkey
FOREIGN KEY (movieid) REFERENCES movie(movieid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- oscargivento table recipient FK
ALTER TABLE oscar ADD CONSTRAINT oscar_recip_fkey
FOREIGN KEY (recipient) REFERENCES crewperson(crewid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- oscargivento table category FK
ALTER TABLE oscar ADD CONSTRAINT oscar_cat_fkey
FOREIGN KEY (category) REFERENCES oscar(category)
ON UPDATE CASCADE ON DELETE CASCADE;

-- listcontains table movie FK
ALTER TABLE listcontains ADD CONSTRAINT list_movie_fkey
FOREIGN KEY (movieid) REFERENCES movie(movieid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- listcontains table list FK
ALTER TABLE listcontains ADD CONSTRAINT list_title_fkey
FOREIGN KEY (listtitle) REFERENCES list(listtitle)
ON UPDATE CASCADE ON DELETE CASCADE;

---------------------------
-- Integrity constraints --
---------------------------

-- movie year
ALTER TABLE movie ADD CONSTRAINT year_constraint
CHECK (year >= 1900 AND year <= 2012);

-- movie star rating
ALTER TABLE movie ADD CONSTRAINT star_constraint
CHECK (mystarrating = 'NO STARS' OR
       mystarrating = '&frac12*' OR
       mystarrating = '*' OR
       mystarrating = '*&frac12' OR
       mystarrating = '**' OR
       mystarrating = '**&frac12' OR
       mystarrating = '***' OR
       mystarrating = '***&frac12' OR
       mystarrating = '****' OR
       mystarrating = 'N/A' OR
       mystarrating = '[not seen]');

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
ALTER TABLE listcontains ADD CONSTRAINT rank_constraint
CHECK (rankinlist > 0);

-- oscargivento status
ALTER TABLE oscargivento ADD CONSTRAINT osc_stat_constraint
CHECK (status = 'won' OR
       status = 'nominated');
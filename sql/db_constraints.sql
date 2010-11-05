-----------------------------
-- Primary Key constraints --
-----------------------------

ALTER TABLE movie ADD CONSTRAINT movie_pkey PRIMARY KEY(mid);
ALTER TABLE crew_person ADD CONSTRAINT crew_pkey PRIMARY KEY(cid);
ALTER TABLE worked_on ADD CONSTRAINT worked_pkey PRIMARY KEY(mid, cid, position);
ALTER TABLE genre ADD CONSTRAINT genre_pkey PRIMARY KEY(gid);
ALTER TABLE genre_contains ADD CONSTRAINT genrecontains_pkey PRIMARY KEY(mid, gid);
ALTER TABLE oscar ADD CONSTRAINT oscar_pkey PRIMARY KEY (oid);
ALTER TABLE oscar_given_to ADD CONSTRAINT oscargiven_pkey PRIMARY KEY(mid, oid);
ALTER TABLE list ADD CONSTRAINT list_pkey PRIMARY KEY(lid);
ALTER TABLE list_contains ADD CONSTRAINT listcontains_pkey PRIMARY KEY(mid, lid);
ALTER TABLE country ADD CONSTRAINT country_pkey PRIMARY KEY(coid);

-----------------------------
-- Foreign Key constraints --
-----------------------------

-- movie table country FK
ALTER TABLE movie ADD CONSTRAINT movie_country_fkey
FOREIGN KEY (country) REFERENCES country(coid)
ON UPDATE CASCADE ON DELETE SET NULL;

-- worked_on table movie FK
ALTER TABLE worked_on ADD CONSTRAINT worked_mid_fkey
FOREIGN KEY (mid) REFERENCES movie(mid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- worked_on table crew FK
ALTER TABLE worked_on ADD CONSTRAINT worked_cid_fkey
FOREIGN KEY (cid) REFERENCES crew_person(cid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- genre_contains table movie FK
ALTER TABLE genre_contains ADD CONSTRAINT genre_mid_fkey
FOREIGN KEY (mid) REFERENCES movie(mid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- genre_contains table genre FK
ALTER TABLE genre_contains ADD CONSTRAINT genre_gid_fkey
FOREIGN KEY (gid) REFERENCES genre(gid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- oscar_given_to table movie FK
ALTER TABLE oscar ADD CONSTRAINT oscar_mid_fkey
FOREIGN KEY (mid) REFERENCES movie(mid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- oscar_given_to table crew FK
ALTER TABLE oscar ADD CONSTRAINT oscar_cid_fkey
FOREIGN KEY (cid) REFERENCES crew_person(cid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- oscar_given_to table category FK
ALTER TABLE oscar ADD CONSTRAINT oscar_oid_fkey
FOREIGN KEY (oid) REFERENCES oscar(oid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- list_contains table movie FK
ALTER TABLE list_contains ADD CONSTRAINT list_contains_movie_fkey
FOREIGN KEY (mid) REFERENCES movie(mid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- list_contains table list FK
ALTER TABLE list_contains ADD CONSTRAINT list_contains_lid_fkey
FOREIGN KEY (lid) REFERENCES list(lid)
ON UPDATE CASCADE ON DELETE CASCADE;


---------------------------
-- Integrity constraints --
---------------------------

-- movie year
ALTER TABLE movie ADD CONSTRAINT year_constraint
CHECK (year >= 1900 AND year <= 2012);

-- movie star rating
ALTER TABLE movie ADD CONSTRAINT star_constraint
CHECK (star_rating >= 0 AND star_rating <= 10);
--  0 = no stars
--  1 = ½*
--  2 = *
--  3 = *½
--  4 = **
--  5 = **½
--  6 = ***
--  7 = ***½
--  8 = ****
--  9 = N/A (so far I've only had to use this once: for "Jackass: The Movie")
-- 10 = not seen

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
CHECK (status >= 0 AND status <= 2);
-- 0 = nominated
-- 1 = won
-- 2 = tie (I believe this has only happened once in oscar history)

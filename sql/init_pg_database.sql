/*
 * This sql script is for PostgreSQL.
 */

-- --------------------
-- Create the schema --
-- --------------------

CREATE SCHEMA filth AUTHORIZATION filth_admin;

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA filth TO filth_admin;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA filth TO filth;


-- --------------------
-- Create the tables --
-- --------------------

-- a movie entity -------------------------------------------------------------
CREATE SEQUENCE filth.movie_mid_seq;
CREATE TABLE filth.movie (
mid smallint DEFAULT nextval('filth.movie_mid_seq') NOT NULL,
title text NOT NULL,
-- smallint is 2 bytes in Postgres, plenty of bits for a year
-- NULL year would indicate a reference movie--i.e. one that REFERENCES filth.a collection of movies--for example 'The Apu Trilogy', or 'The Up Documentaries'
year smallint DEFAULT NULL,
-- my star rating for the movie ("****", "NO STARS", "not seen", etc)
star_rating text DEFAULT NULL,
-- mpaa rating ("PG", "R", etc)
mpaa text DEFAULT NULL,
-- country of origin. I only care about a movie being associated with one
-- country, so there is no country <--> movie relationship.  In order to prevent
-- inserting spelling errors and bogus countries (e.g. france instead of France,
-- or Sweden instead of Sewden), there will be a foreign key constraint on this
-- column referencing the country table.
country text DEFAULT NULL,
-- text field for comments/notes regarding the movie
comments text DEFAULT NULL,
-- id for IMDB
imdb_id text DEFAULT NULL,
-- number of times seen in the theater
theater_viewings smallint DEFAULT NULL,
-- id for TMDB
tmdb_id integer DEFAULT NULL);


-- movie <--> movie relationship ----------------------------------------------
CREATE TABLE filth.movie_link (
-- id of the movie being linked
base_mid smallint NOT NULL,
-- id of the linked movie
linked_mid smallint NOT NULL,
-- type of link
link_type text NOT NULL,
-- comments, such as if the type is "RELATED_TO", then comments may explain
-- how they are related
comments text DEFAULT NULL);


-- a movie link type entity ---------------------------------------------------
-- (only used as an integrity constraint for movie_link.link_type)
--
-- current values:
-- PREDECESSOR_OF (e.g. "Rocky" is a predecessor of "Rocky II")
-- SUCCESSOR_TO (e.g. "Rocky II" is a successor to "Rocky")
-- CHILD_OF (e.g. "28 Up" is a child of "The 'Up' Documentaries")
-- PARENT_TO (e.g. "The 'Up' Documentaries" is a parent to "28 Up")
-- REMAKE_OF (e.g. "You've Got Mail" is a remake of "The Shop Around the Corner")
-- RELATED_TO (e.g. "The Walk" is related to "Man on Wire")
CREATE TABLE filth.movie_link_type (
link_type text NOT NULL);


-- a country entity -----------------------------------------------------------
-- (only used as an integrity constraint for movie.country)
CREATE TABLE filth.country (
country_name text NOT NULL);


-- a star rating entity -------------------------------------------------------
-- (only used as an integrity constraint for movie.star_rating) (see comment
-- above the position table creation statement)
--
-- current values:
-- "not seen"
-- "N/A" (so far I've only had to use this once: for "Jackass: The Movie")
-- "NO STARS"
-- "½*"
-- "*"
-- "*½"
-- "**"
-- "**½"
-- "***"
-- "***½"
-- "****"
CREATE TABLE filth.star_rating (
rating text NOT NULL);


-- an mpaa rating entity ------------------------------------------------------
-- (only used as an integrity constraint for movie.mpaa)
-- (see comment above the position table creation statement)
--
-- current values:
-- "NR" (Not Rated)
-- "G" (General audiences)
-- "PG" (Parental Guidance suggested)
-- "PG-13" (Parental Guidance strongly suggested for those under 13)
-- "R" (Restricted)
-- "X" (no one under 17 admitted [prior to 1990])
-- "NC-17" (no one under 17 admitted [after 1990 when X renamed to NC-17])
CREATE TABLE filth.mpaa (
rating text NOT NULL);


-- a crewperson entity --------------------------------------------------------
CREATE SEQUENCE filth.crew_person_cid_seq;
CREATE TABLE filth.crew_person (
cid smallint DEFAULT nextval('filth.crew_person_cid_seq') NOT NULL,
last_name text NOT NULL,
-- first and middle names can be NULL (in cases
-- such as Madonna, Cher, Costa-Gavras, etc, their
-- names will be considered last names)
first_name text DEFAULT NULL,
middle_name text DEFAULT NULL,
full_name text NOT NULL,
known_as text DEFAULT NULL);

-- Sequences start with 1 by default.  Since the oscar_given_to and
-- tyler_given_to tables can contain records where cid (foreign key to
-- crew_person's cid) is not needed (e.g. Best Picture, Best Documentary, etc.
-- where no recipient is desired), there needs to be a value indicating no
-- recipient (the value of cid cannot be NULL since it is a foreign key to a
-- primary key).  Thus, the SEQUENCE filth.is altered here so that 0 is a valid cid
-- value, indicating no recipient.
ALTER SEQUENCE filth.crew_person_cid_seq MINVALUE 0 RESTART WITH 0;


-- crewperson <--> movie relationship -----------------------------------------
CREATE TABLE filth.worked_on (
mid smallint NOT NULL,
cid smallint NOT NULL,
position text NOT NULL);


-- a position entity ----------------------------------------------------------
-- (only used as an integrity constraint for crew_person.known_as and
-- worked_on.position).  Normally I would just use a CHECK constraint for this
-- since there are only about 5 positions I care about right now, but if I ever
-- wanted to add a position (such as costume designer) this would make it much
-- easier.
CREATE TABLE filth.position (
position_title text NOT NULL);


-- a tag entity ---------------------------------------------------------------
--
-- Tags are used to, well, tag a movie.  Movies can be tagged with keywords or
-- phrases (e.g. "New York", "Indie", "Unconventional").  This will probably be
-- used mostly for marking a movie with one or more genres.
CREATE SEQUENCE filth.tag_tid_seq;
CREATE TABLE filth.tag (
tid smallint DEFAULT nextval('filth.tag_tid_seq') NOT NULL,
tag_name text NOT NULL,
parent_tid smallint);


-- tag <--> movie relationship ------------------------------------------------
CREATE TABLE filth.tag_given_to (
mid smallint NOT NULL,
tid smallint NOT NULL);


-- an oscar entity ------------------------------------------------------------
CREATE SEQUENCE filth.oscar_oid_seq;
CREATE TABLE filth.oscar (
oid smallint DEFAULT nextval('filth.oscar_oid_seq') NOT NULL,
category text NOT NULL);


-- oscar <--> movie relationship ----------------------------------------------
CREATE TABLE filth.oscar_given_to (
mid smallint NOT NULL,
oid smallint NOT NULL,
cid smallint DEFAULT 0,  -- value of 0 indicates no recipient for the oscar
year smallint NOT NULL,
-- status of the oscar: 0, 1, or 2 (nominated, won, or tie, respectively)
status smallint NOT NULL,
-- indicates how many other recipients this nominee is sharing the nomination
-- with
sharing_with smallint DEFAULT NULL);


-- a list entity --------------------------------------------------------------
CREATE SEQUENCE filth.list_lid_seq;
CREATE TABLE filth.list (
lid smallint DEFAULT nextval('filth.list_lid_seq') NOT NULL,
list_title text NOT NULL,
list_author text DEFAULT NULL);


-- list <--> movie relationship -----------------------------------------------
CREATE TABLE filth.list_contains (
mid smallint NOT NULL,
lid smallint NOT NULL,
rank smallint DEFAULT NULL);


-- a tyler entity -------------------------------------------------------------
-- (like oscar, but for my annual awards)
CREATE SEQUENCE filth.tyler_tid_seq;
CREATE TABLE filth.tyler (
tid smallint DEFAULT nextval('filth.tyler_tid_seq') NOT NULL,
category text NOT NULL);


-- tyler <--> movie relationship ----------------------------------------------
CREATE TABLE filth.tyler_given_to (
mid smallint NOT NULL,
tid smallint NOT NULL,
cid smallint DEFAULT 0,  -- value of 0 indicates no recipient for the award
-- status of the award: 0, 1, or 2 (nominated, won, or tie, respectively)
status smallint NOT NULL,
-- this column is only used for the Best Scene category and is not NULLable
-- in order to be included in the table's primary key so that a movie can be
-- nominated in the Best Scene category for more than one scene (for awards
-- of any category other than Best Scene an empty string is used by default)
scene_title text DEFAULT '');


-- --------------------------
-- Primary Key constraints --
-- --------------------------

ALTER TABLE filth.movie ADD CONSTRAINT movie_pkey PRIMARY KEY(mid);
ALTER TABLE filth.movie_link ADD CONSTRAINT movie_link_pkey PRIMARY KEY(base_mid, linked_mid);
ALTER TABLE filth.movie_link_type ADD CONSTRAINT movie_link_type_pkey PRIMARY KEY(link_type);
ALTER TABLE filth.star_rating ADD CONSTRAINT star_rating_pkey PRIMARY KEY(rating);
ALTER TABLE filth.mpaa ADD CONSTRAINT mpaa_pkey PRIMARY KEY(rating);
ALTER TABLE filth.country ADD CONSTRAINT country_pkey PRIMARY KEY(country_name);
ALTER TABLE filth.crew_person ADD CONSTRAINT crew_pkey PRIMARY KEY(cid);
ALTER TABLE filth.worked_on ADD CONSTRAINT worked_pkey PRIMARY KEY(mid, cid, position);
ALTER TABLE filth.position ADD CONSTRAINT position_pkey PRIMARY KEY(position_title);
ALTER TABLE filth.tag ADD CONSTRAINT tag_pkey PRIMARY KEY(tid);
ALTER TABLE filth.tag_given_to ADD CONSTRAINT tag_given_to_pkey PRIMARY KEY(mid, tid);
ALTER TABLE filth.oscar ADD CONSTRAINT oscar_pkey PRIMARY KEY(oid);
ALTER TABLE filth.oscar_given_to ADD CONSTRAINT oscar_given_to_pkey PRIMARY KEY(mid, oid, cid);
ALTER TABLE filth.list ADD CONSTRAINT list_pkey PRIMARY KEY(lid);
ALTER TABLE filth.list_contains ADD CONSTRAINT list_contains_pkey PRIMARY KEY(mid, lid);
ALTER TABLE filth.tyler ADD CONSTRAINT tyler_pkey PRIMARY KEY(tid);
ALTER TABLE filth.tyler_given_to ADD CONSTRAINT tyler_given_to_pkey PRIMARY KEY(mid, tid, cid, scene_title);


-- ---------------------
-- Unique Constraints --
-- ---------------------

ALTER TABLE filth.movie ADD CONSTRAINT movie_title_year_constraint UNIQUE(title, year);
ALTER TABLE filth.tag ADD CONSTRAINT tag_unique_constraint UNIQUE(tag_name);


-- --------------------------
-- Foreign Key constraints --
-- --------------------------

-- movie table country FK
ALTER TABLE filth.movie ADD CONSTRAINT movie_country_fkey
FOREIGN KEY (country) REFERENCES filth.country(country_name)
ON UPDATE CASCADE ON DELETE SET NULL;

-- movie table star_rating FK
ALTER TABLE filth.movie ADD CONSTRAINT movie_star_rating_fkey
FOREIGN KEY (star_rating) REFERENCES filth.star_rating(rating)
ON UPDATE CASCADE ON DELETE SET NULL;

-- movie table mpaa FK
ALTER TABLE filth.movie ADD CONSTRAINT movie_mpaa_fkey
FOREIGN KEY (mpaa) REFERENCES filth.mpaa(rating)
ON UPDATE CASCADE ON DELETE SET NULL;

-- movie_link table base_mid FK
ALTER TABLE filth.movie_link ADD CONSTRAINT movie_link_base_mid_fkey
FOREIGN KEY (base_mid) REFERENCES filth.movie(mid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- movie_link table linked_mid FK
ALTER TABLE filth.movie_link ADD CONSTRAINT movie_link_linked_mid_fkey
FOREIGN KEY (linked_mid) REFERENCES filth.movie(mid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- movie_link table link_type FK
ALTER TABLE filth.movie_link ADD CONSTRAINT movie_link_link_type_fkey
FOREIGN KEY (link_type) REFERENCES filth.movie_link_type(link_type)
ON UPDATE CASCADE ON DELETE CASCADE;

-- crew_person table known_as FK
ALTER TABLE filth.crew_person ADD CONSTRAINT crew_known_as_fkey
FOREIGN KEY (known_as) REFERENCES filth.position(position_title)
ON UPDATE CASCADE ON DELETE SET NULL;

-- worked_on table movie FK
ALTER TABLE filth.worked_on ADD CONSTRAINT worked_mid_fkey
FOREIGN KEY (mid) REFERENCES filth.movie(mid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- worked_on table crew FK
ALTER TABLE filth.worked_on ADD CONSTRAINT worked_cid_fkey
FOREIGN KEY (cid) REFERENCES filth.crew_person(cid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- worked_on table position FK
ALTER TABLE filth.worked_on ADD CONSTRAINT worked_position_fkey
FOREIGN KEY (position) REFERENCES filth.position(position_title)
ON UPDATE CASCADE ON DELETE SET NULL;

-- tag_given_to table movie FK
ALTER TABLE filth.tag_given_to ADD CONSTRAINT tag_mid_fkey
FOREIGN KEY (mid) REFERENCES filth.movie(mid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- tag_given_to table tag FK
ALTER TABLE filth.tag_given_to ADD CONSTRAINT tag_tid_fkey
FOREIGN KEY (tid) REFERENCES filth.tag(tid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- oscar_given_to table movie FK
ALTER TABLE filth.oscar_given_to ADD CONSTRAINT oscar_mid_fkey
FOREIGN KEY (mid) REFERENCES filth.movie(mid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- oscar_given_to table crew FK
ALTER TABLE filth.oscar_given_to ADD CONSTRAINT oscar_cid_fkey
FOREIGN KEY (cid) REFERENCES filth.crew_person(cid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- oscar_given_to table oid FK
ALTER TABLE filth.oscar_given_to ADD CONSTRAINT oscar_oid_fkey
FOREIGN KEY (oid) REFERENCES filth.oscar(oid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- list_contains table movie FK
ALTER TABLE filth.list_contains ADD CONSTRAINT list_contains_movie_fkey
FOREIGN KEY (mid) REFERENCES filth.movie(mid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- list_contains table list FK
ALTER TABLE filth.list_contains ADD CONSTRAINT list_contains_lid_fkey
FOREIGN KEY (lid) REFERENCES filth.list(lid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- tyler_given_to table movie FK
ALTER TABLE filth.tyler_given_to ADD CONSTRAINT tyler_mid_fkey
FOREIGN KEY (mid) REFERENCES filth.movie(mid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- tyler_given_to table crew FK
ALTER TABLE filth.tyler_given_to ADD CONSTRAINT tyler_cid_fkey
FOREIGN KEY (cid) REFERENCES filth.crew_person(cid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- tyler_given_to table tid FK
ALTER TABLE filth.tyler_given_to ADD CONSTRAINT tyler_tid_fkey
FOREIGN KEY (tid) REFERENCES filth.tyler(tid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- tag parent_tid FK
ALTER TABLE filth.tag ADD CONSTRAINT tag_parent_tid_fkey
FOREIGN KEY (parent_tid) REFERENCES filth.tag(tid)
ON UPDATE CASCADE ON DELETE SET NULL;


-- ------------
-- Functions --
-- ------------

-------------------------------------------------------------------------------
--
-- Converts strings with accented characters to strings with regular characters.
--
-- "Internal" functions are functions written in C that have been statically
-- linked into the PostgreSQL server. The "body" of the function definition
-- specifies the C-language name of the function, which need not be the same as
-- the name being declared for SQL use.
-- http://developer.postgresql.org/pgdocs/postgres/xfunc-internal.html
--
CREATE FUNCTION filth.to_ascii(bytea, name) RETURNS text STRICT AS 'to_ascii_encname'
LANGUAGE internal;


-------------------------------------------------------------------------------
--
-- Sanity checks the year of a movie.  A movie year must be between 1900 and
-- 2 years into the future (current year + 2) inclusive.
--
CREATE FUNCTION filth.movie_year_ok(year smallint) RETURNS boolean AS $$
BEGIN
  IF year < 1900 OR year > (SELECT extract(year FROM current_date) + 2) THEN
    RETURN false;
  END IF;
  RETURN true;
END;
$$ LANGUAGE plpgsql;


-------------------------------------------------------------------------------
--
-- Returns the number of movies actually seen.
--
CREATE FUNCTION filth.num_movies_seen() RETURNS integer AS $$
DECLARE
  total integer;
BEGIN
  SELECT COUNT(*) INTO total
    FROM movie
    WHERE star_rating <> -2;
  RETURN total;
END;
$$ LANGUAGE plpgsql;


-------------------------------------------------------------------------------
--
-- Inserts a movie into the database.  This is to ensure that the year of the
-- movie is not more than 2 years after the current year.
--
CREATE FUNCTION filth.insert_movie(title text, year smallint, stars smallint, mpaa smallint, country text, comments text) RETURNS void AS $$
BEGIN
  -- check that the year makes sense (is not less than 1900 nor more than 2
  -- years into the future)
  IF NOTfilth.movie_year_ok(year) THEN
    RAISE EXCEPTION 'Movie year cannot be before 1900 nor more than 2 years into the future.';
  END IF;
  INSERT INTO movie VALUES (DEFAULT, title, year, stars, mpaa, country, note);
END;
$$ LANGUAGE plpgsql;


-------------------------------------------------------------------------------
--
-- Updates the year of a movie given the movie's unique id.
--
CREATE FUNCTION filth.update_movie_year(movie_id integer, new_year smallint) RETURNS void AS $$
BEGIN
  IF NOTfilth.movie_year_ok(new_year) THEN
    RAISE EXCEPTION 'Movie year cannot be before 1900 nor more than 2 years into the future.';
  END IF;
  UPDATE movie SET year = new_year WHERE mid = movie_id;
END;
$$ LANGUAGE plpgsql;


-------------------------------------------------------------------------------
--
-- Updates the year of a movie given the movie's title.
--
CREATE FUNCTION filth.update_movie_year(movie_title text, new_year smallint) RETURNS void AS $$
BEGIN
  IF NOTfilth.movie_year_ok(new_year) THEN
    RAISE EXCEPTION 'Movie year cannot be before 1900 nor more than 2 years into the future.';
  END IF;
  UPDATE movie SET year = new_year WHERE title = movie_title;
END;
$$ LANGUAGE plpgsql;


-- ------------------------
-- Integrity constraints --
-- ------------------------

-- movie year (calls functionfilth.movie_year_ok in order to check that the year is
-- less than the current year + 3)
ALTER TABLE filth.movie ADD CONSTRAINT year_constraint
CHECK (filth.movie_year_ok(year));

-- list_contains rank
ALTER TABLE filth.list_contains ADD CONSTRAINT rank_constraint
CHECK (rank > 0);

-- oscar_given_to status
ALTER TABLE filth.oscar_given_to ADD CONSTRAINT oscar_status_constraint
CHECK (status >= 0 AND status <= 2);
-- 0 = nominated
-- 1 = won
-- 2 = tie (I believe this has only happened once in oscar history)

-- oscar_given_to sharing_with
ALTER TABLE filth.oscar_given_to ADD CONSTRAINT oscar_sharing_constraint
CHECK (sharing_with >= 0);

-- tyler_given_to status
ALTER TABLE filth.tyler_given_to ADD CONSTRAINT tyler_status_constraint
CHECK (status >= 0 AND status <= 2);
-- 0 = nominated
-- 1 = won
-- 2 = tie

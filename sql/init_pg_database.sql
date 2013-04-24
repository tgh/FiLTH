/*
 * This sql script is for PostgreSQL.
 */

-- --------------------
-- Create the tables --
-- --------------------

-- a movie entity -------------------------------------------------------------
DROP SEQUENCE IF EXISTS movie_mid_seq;
CREATE SEQUENCE movie_mid_seq;
DROP TABLE IF EXISTS movie CASCADE;
CREATE TABLE movie (
mid smallint DEFAULT nextval('movie_mid_seq') NOT NULL,
title text NOT NULL,
-- smallint is 2 bytes in Postgres, plenty of bits for a year
year smallint NOT NULL,
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
comments text DEFAULT NULL);


-- a country entity -----------------------------------------------------------
-- (only used as an integrity constraint for movie.country)
DROP TABLE IF EXISTS country CASCADE;
CREATE TABLE country (
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
DROP TABLE IF EXISTS star_rating CASCADE;
CREATE TABLE star_rating (
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
DROP TABLE IF EXISTS mpaa CASCADE;
CREATE TABLE mpaa (
rating text NOT NULL);


-- a crewperson entity --------------------------------------------------------
DROP SEQUENCE IF EXISTS crew_person_cid_seq;
CREATE SEQUENCE crew_person_cid_seq;
DROP TABLE IF EXISTS crew_person CASCADE;
CREATE TABLE crew_person (
cid smallint DEFAULT nextval('crew_person_cid_seq') NOT NULL,
l_name text NOT NULL,
f_name text DEFAULT NULL,     -- first and middle names can be NULL (in cases
m_name text DEFAULT NULL,     -- such as Madonna, Cher, Costa-Gavras, etc, their
known_as text DEFAULT NULL);  -- names will be considered last names)

-- Sequences start with 1 by default.  Since the oscar_given_to and
-- tyler_given_to tables can contain records where cid (foreign key to
-- crew_person's cid) is not needed (e.g. Best Picture, Best Documentary, etc.
-- where no recipient is desired), there needs to be a value indicating no
-- recipient (the value of cid cannot be NULL since it is a foreign key to a
-- primary key).  Thus, the sequence is altered here so that 0 is a valid cid
-- value, indicating no recipient.
ALTER SEQUENCE crew_person_cid_seq MINVALUE 0 RESTART WITH 0;


-- crewperson <--> movie relationship -----------------------------------------
DROP TABLE IF EXISTS worked_on CASCADE;
CREATE TABLE worked_on (
mid smallint NOT NULL,
cid smallint NOT NULL,
position text NOT NULL);


-- a position entity ----------------------------------------------------------
-- (only used as an integrity constraint for crew_person.known_as and
-- worked_on.position).  Normally I would just use a CHECK constraint for this
-- since there are only about 5 positions I care about right now, but if I ever
-- wanted to add a position (such as costume designer) this would make it much
-- easier.
DROP TABLE IF EXISTS position CASCADE;
CREATE TABLE position (
position_title text NOT NULL);


-- a tag entity ---------------------------------------------------------------
--
-- Tags are used to, well, tag a movie.  Movies can be tagged with keywords or
-- phrases (e.g. "New York", "Indie", "Unconventional").  This will probably be
-- used mostly for marking a movie with one or more genres.
DROP SEQUENCE IF EXISTS tag_tid_seq;
CREATE SEQUENCE tag_tid_seq;
DROP TABLE IF EXISTS tag CASCADE;
CREATE TABLE tag (
tid smallint DEFAULT nextval('tag_tid_seq') NOT NULL,
tag_name text NOT NULL);


-- tag <--> movie relationship ------------------------------------------------
DROP TABLE IF EXISTS tag_given_to CASCADE;
CREATE TABLE tag_given_to (
mid smallint NOT NULL,
tid smallint NOT NULL);


-- an oscar entity ------------------------------------------------------------
DROP SEQUENCE IF EXISTS oscar_oid_seq;
CREATE SEQUENCE oscar_oid_seq;
DROP TABLE IF EXISTS oscar CASCADE;
CREATE TABLE oscar (
oid smallint DEFAULT nextval('oscar_oid_seq') NOT NULL,
category text NOT NULL);


-- oscar <--> movie relationship ----------------------------------------------
DROP TABLE IF EXISTS oscar_given_to CASCADE;
CREATE TABLE oscar_given_to (
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
DROP SEQUENCE IF EXISTS list_lid_seq;
CREATE SEQUENCE list_lid_seq;
DROP TABLE IF EXISTS list CASCADE;
CREATE TABLE list (
lid smallint DEFAULT nextval('list_lid_seq') NOT NULL,
list_title text NOT NULL,
list_author text DEFAULT NULL);


-- list <--> movie relationship -----------------------------------------------
DROP TABLE IF EXISTS list_contains CASCADE;
CREATE TABLE list_contains (
mid smallint NOT NULL,
lid smallint NOT NULL,
rank smallint DEFAULT NULL);


-- a tyler entity -------------------------------------------------------------
-- (like oscar, but for my annual awards)
DROP SEQUENCE IF EXISTS tyler_tid_seq;
CREATE SEQUENCE tyler_tid_seq;
DROP TABLE IF EXISTS tyler CASCADE;
CREATE TABLE tyler (
tid smallint DEFAULT nextval('tyler_tid_seq') NOT NULL,
category text NOT NULL);


-- tyler <--> movie relationship ----------------------------------------------
DROP TABLE IF EXISTS tyler_given_to CASCADE;
CREATE TABLE tyler_given_to (
mid smallint NOT NULL,
tid smallint NOT NULL,
cid smallint DEFAULT 0,  -- value of 0 indicates no recipient for the oscar
-- status of the award: 0, 1, or 2 (nominated, won, or tie, respectively)
status smallint NOT NULL,
-- this attribute is only used for the Best Scene category for the title of the
-- scene--a waste, I know, but what else should I do?
scene_title text DEFAULT NULL);


-- --------------------------
-- Primary Key constraints --
-- --------------------------

ALTER TABLE movie ADD CONSTRAINT movie_pkey PRIMARY KEY(mid);
ALTER TABLE star_rating ADD CONSTRAINT star_rating_pkey PRIMARY KEY(rating);
ALTER TABLE mpaa ADD CONSTRAINT mpaa_pkey PRIMARY KEY(rating);
ALTER TABLE country ADD CONSTRAINT country_pkey PRIMARY KEY(country_name);
ALTER TABLE crew_person ADD CONSTRAINT crew_pkey PRIMARY KEY(cid);
ALTER TABLE worked_on ADD CONSTRAINT worked_pkey PRIMARY KEY(mid, cid, position);
ALTER TABLE position ADD CONSTRAINT position_pkey PRIMARY KEY(position_title);
ALTER TABLE tag ADD CONSTRAINT tag_pkey PRIMARY KEY(tid);
ALTER TABLE tag_given_to ADD CONSTRAINT tag_given_to_pkey PRIMARY KEY(mid, tid);
ALTER TABLE oscar ADD CONSTRAINT oscar_pkey PRIMARY KEY(oid);
ALTER TABLE oscar_given_to ADD CONSTRAINT oscar_given_to_pkey PRIMARY KEY(mid, oid, cid);
ALTER TABLE list ADD CONSTRAINT list_pkey PRIMARY KEY(lid);
ALTER TABLE list_contains ADD CONSTRAINT list_contains_pkey PRIMARY KEY(mid, lid);
ALTER TABLE tyler ADD CONSTRAINT tyler_pkey PRIMARY KEY(tid);
ALTER TABLE tyler_given_to ADD CONSTRAINT tyler_given_to_pkey PRIMARY KEY(mid, tid, cid);


-- ---------------------
-- Unique Constraints --
-- ---------------------

ALTER TABLE movie ADD CONSTRAINT movie_title_year_constraint UNIQUE(title, year);
ALTER TABLE tag ADD CONSTRAINT tag_unique_constraint UNIQUE(tag_name);


-- --------------------------
-- Foreign Key constraints --
-- --------------------------

-- movie table country FK
ALTER TABLE movie ADD CONSTRAINT movie_country_fkey
FOREIGN KEY (country) REFERENCES country(country_name)
ON UPDATE CASCADE ON DELETE SET NULL;

-- movie table star_rating FK
ALTER TABLE movie ADD CONSTRAINT movie_star_rating_fkey
FOREIGN KEY (star_rating) REFERENCES star_rating(rating)
ON UPDATE CASCADE ON DELETE SET NULL;

-- movie table mpaa FK
ALTER TABLE movie ADD CONSTRAINT movie_mpaa_fkey
FOREIGN KEY (mpaa) REFERENCES mpaa(rating)
ON UPDATE CASCADE ON DELETE SET NULL;

-- crew_person table known_as FK
ALTER TABLE crew_person ADD CONSTRAINT crew_known_as_fkey
FOREIGN KEY (known_as) REFERENCES position(position_title)
ON UPDATE CASCADE ON DELETE SET NULL;

-- worked_on table movie FK
ALTER TABLE worked_on ADD CONSTRAINT worked_mid_fkey
FOREIGN KEY (mid) REFERENCES movie(mid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- worked_on table crew FK
ALTER TABLE worked_on ADD CONSTRAINT worked_cid_fkey
FOREIGN KEY (cid) REFERENCES crew_person(cid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- worked_on table position FK
ALTER TABLE worked_on ADD CONSTRAINT worked_position_fkey
FOREIGN KEY (position) REFERENCES position(position_title)
ON UPDATE CASCADE ON DELETE SET NULL;

-- tag_given_to table movie FK
ALTER TABLE tag_given_to ADD CONSTRAINT tag_mid_fkey
FOREIGN KEY (mid) REFERENCES movie(mid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- tag_given_to table tag FK
ALTER TABLE tag_given_to ADD CONSTRAINT tag_tid_fkey
FOREIGN KEY (tid) REFERENCES tag(tid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- oscar_given_to table movie FK
ALTER TABLE oscar_given_to ADD CONSTRAINT oscar_mid_fkey
FOREIGN KEY (mid) REFERENCES movie(mid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- oscar_given_to table crew FK
ALTER TABLE oscar_given_to ADD CONSTRAINT oscar_cid_fkey
FOREIGN KEY (cid) REFERENCES crew_person(cid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- oscar_given_to table oid FK
ALTER TABLE oscar_given_to ADD CONSTRAINT oscar_oid_fkey
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

-- tyler_given_to table movie FK
ALTER TABLE tyler_given_to ADD CONSTRAINT tyler_mid_fkey
FOREIGN KEY (mid) REFERENCES movie(mid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- tyler_given_to table crew FK
ALTER TABLE tyler_given_to ADD CONSTRAINT tyler_cid_fkey
FOREIGN KEY (cid) REFERENCES crew_person(cid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- tyler_given_to table tid FK
ALTER TABLE tyler_given_to ADD CONSTRAINT tyler_tid_fkey
FOREIGN KEY (tid) REFERENCES tyler(tid)
ON UPDATE CASCADE ON DELETE CASCADE;


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
DROP FUNCTION IF EXISTS to_ascii(bytea, name);
CREATE FUNCTION to_ascii(bytea, name) RETURNS text STRICT AS 'to_ascii_encname'
LANGUAGE internal;


-------------------------------------------------------------------------------
--
-- Sanity checks the year of a movie.  A movie year must be between 1900 and
-- 2 years into the future (current year + 2) inclusive.
--
DROP FUNCTION IF EXISTS movie_year_ok(year smallint);
CREATE FUNCTION movie_year_ok(year smallint) RETURNS boolean AS $$
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
DROP FUNCTION IF EXISTS num_movies_seen();
CREATE FUNCTION num_movies_seen() RETURNS integer AS $$
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
DROP FUNCTION IF EXISTS insert_movie(title text, year smallint, stars smallint, mpaa smallint, country text, comments text);
CREATE FUNCTION insert_movie(title text, year smallint, stars smallint, mpaa smallint, country text, comments text) RETURNS void AS $$
BEGIN
  -- check that the year makes sense (is not less than 1900 nor more than 2
  -- years into the future)
  IF NOT movie_year_ok(year) THEN
    RAISE EXCEPTION 'Movie year cannot be before 1900 nor more than 2 years into the future.';
  END IF;
  INSERT INTO movie VALUES (DEFAULT, title, year, stars, mpaa, country, note);
END;
$$ LANGUAGE plpgsql;


-------------------------------------------------------------------------------
--
-- Updates the year of a movie given the movie's unique id.
--
DROP FUNCTION IF EXISTS update_movie_year(movie_id integer, new_year smallint);
CREATE FUNCTION update_movie_year(movie_id integer, new_year smallint) RETURNS void AS $$
BEGIN
  IF NOT movie_year_ok(new_year) THEN
    RAISE EXCEPTION 'Movie year cannot be before 1900 nor more than 2 years into the future.';
  END IF;
  UPDATE movie SET year = new_year WHERE mid = movie_id;
END;
$$ LANGUAGE plpgsql;


-------------------------------------------------------------------------------
--
-- Updates the year of a movie given the movie's title.
--
DROP FUNCTION IF EXISTS update_movie_year(movie_title text, new_year smallint);
CREATE FUNCTION update_movie_year(movie_title text, new_year smallint) RETURNS void AS $$
BEGIN
  IF NOT movie_year_ok(new_year) THEN
    RAISE EXCEPTION 'Movie year cannot be before 1900 nor more than 2 years into the future.';
  END IF;
  UPDATE movie SET year = new_year WHERE title = movie_title;
END;
$$ LANGUAGE plpgsql;


-- ------------------------
-- Integrity constraints --
-- ------------------------

-- movie year (calls function movie_year_ok in order to check that the year is
-- less than the current year + 3)
ALTER TABLE movie ADD CONSTRAINT year_constraint
CHECK (movie_year_ok(year));

-- list_contains rank
ALTER TABLE list_contains ADD CONSTRAINT rank_constraint
CHECK (rank > 0);

-- oscar_given_to status
ALTER TABLE oscar_given_to ADD CONSTRAINT oscar_status_constraint
CHECK (status >= 0 AND status <= 2);
-- 0 = nominated
-- 1 = won
-- 2 = tie (I believe this has only happened once in oscar history)

-- oscar_given_to sharing_with
ALTER TABLE oscar_given_to ADD CONSTRAINT oscar_sharing_constraint
CHECK (sharing_with >= 0);

-- tyler_given_to status
ALTER TABLE tyler_given_to ADD CONSTRAINT tyler_status_constraint
CHECK (status >= 0 AND status <= 2);
-- 0 = nominated
-- 1 = won
-- 2 = tie

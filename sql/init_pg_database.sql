/*
 * This sql script is for Postgresql.  Also in the sql/ directory is a file
 * named init_mysql_database.sql for MySQL.  The difference in these is the
 * data types.  MySQL offers a tinyint--a numeric type of only one byte in size,
 * which is great for a lot of these attributes.  However, Postgres only offers
 * smallint, which is two bytes.
 */

-- --------------------
-- Create the tables --
-- --------------------

-- a movie entity
DROP TABLE IF EXISTS movie CASCADE;
CREATE TABLE movie (
mid serial NOT NULL,
title text NOT NULL,
-- smallint is 2 bytes in Postgres, plenty of bits for a year
year smallint NOT NULL,
-- my star rating for the movie (or "haven't seen it"). Only 11 possible values
-- (listed in the integrity constraint section at the end of this file.
-- smallint to save space--front-end can translate the number to a star rating.
star_rating smallint DEFAULT NULL,
-- mpaa rating (PG, R, etc).  Only 7 possible values (listed in the integrity
-- constraints section at the end of this file).  smallint to save space--front-
-- end can translate the number to the MPAA rating as a string.
mpaa smallint DEFAULT NULL,
-- country of origin. I only care about a movie being associated with one
-- country, so there is no country <--> movie relationship.  In order to prevent
-- inserting spelling errors and bogus countries (e.g. france instead of France,
-- or Sweden instead of Sewden), there will be a foreign key constraint on this
-- column referencing the country table.
country text DEFAULT NULL,
-- text field for comments/notes regarding the movie
comments text DEFAULT NULL);


-- a country entity (only used as an integrity constraint for movie.country)
DROP TABLE IF EXISTS country CASCADE;
CREATE TABLE country (
country_name text NOT NULL);


-- a crewperson entity
DROP TABLE IF EXISTS crew_person CASCADE;
CREATE TABLE crew_person (
cid serial NOT NULL,
l_name text NOT NULL,
f_name text DEFAULT NULL,
m_name text DEFAULT NULL);


-- crewperson <--> movie relationship
DROP TABLE IF EXISTS worked_on CASCADE;
CREATE TABLE worked_on (
mid smallint NOT NULL,
cid smallint NOT NULL,
position text NOT NULL);


-- a genre entity
DROP TABLE IF EXISTS genre CASCADE;
CREATE TABLE genre (
gid serial NOT NULL,
gen_name text NOT NULL);


-- genre <--> movie relationship
DROP TABLE IF EXISTS genre_contains CASCADE;
CREATE TABLE genre_contains (
mid smallint NOT NULL,
gid smallint NOT NULL);


-- an oscar entity
DROP TABLE IF EXISTS oscar CASCADE;
CREATE TABLE oscar (
oid serial NOT NULL,
ocategory text NOT NULL);


-- oscar <--> movie relationship
DROP TABLE IF EXISTS oscar_given_to CASCADE;
CREATE TABLE oscar_given_to (
mid smallint NOT NULL,
oid smallint NOT NULL,
cid smallint DEFAULT NULL,
year smallint NOT NULL,
-- status of the oscar: 0, 1, or 2 (nominated, won, or tie, respectively)
ostatus smallint DEFAULT NULL,
-- indicates how many other recipients this nominee is sharing the nomination
-- with
sharing_with smallint DEFAULT NULL);


-- a list entity
DROP TABLE IF EXISTS list CASCADE;
CREATE TABLE list (
lid serial NOT NULL,
list_title text NOT NULL,
list_author text DEFAULT NULL);


-- list <--> movie relationship
DROP TABLE IF EXISTS list_contains CASCADE;
CREATE TABLE list_contains (
mid smallint NOT NULL,
lid smallint NOT NULL,
rank smallint DEFAULT NULL);


-- a tyler entity (like oscar, but for my annual awards)
DROP TABLE IF EXISTS tyler CASCADE;
CREATE TABLE tyler (
tid serial NOT NULL,
tcategory text NOT NULL);


-- tyler <--> movie relationship
DROP TABLE IF EXISTS tyler_given_to CASCADE;
CREATE TABLE tyler_given_to (
mid smallint NOT NULL,
tid smallint NOT NULL,
cid smallint DEFAULT NULL,
-- status of the award: 0, 1, or 2 (nominated, won, or tie, respectively)
tstatus smallint DEFAULT NULL,
-- this attribute is only used for the Best Scene category for the title of the
-- scene--a waste, I know, but what else should I do?
scene_title text DEFAULT NULL);


-- --------------------------
-- Primary Key constraints --
-- --------------------------

--There is no primary key for oscar_given_to table out of necessity.  There
-- should be one on (mid, oid, cid), but it is necessary for cid to have the
-- possibility of being NULL due to the fact that some oscar categories (e.g.
-- Best Picture) do not have a crew person associated with it.  If cid were a
-- part of a primary key, it can not be NULL.  Also, cid cannot be, for example,
-- -1, because it is a foreign key to crew_person's primary key (cid), and that
-- foreign key is necessary.
-- For the same reason, there is no primary key for the tyler_given_to table, as
-- well.
-- However, these two tables will need an index to speed up queries.

ALTER TABLE movie ADD CONSTRAINT movie_pkey PRIMARY KEY(mid);
ALTER TABLE country ADD CONSTRAINT country_pkey PRIMARY KEY(country_name);
ALTER TABLE crew_person ADD CONSTRAINT crew_pkey PRIMARY KEY(cid);
ALTER TABLE worked_on ADD CONSTRAINT worked_pkey PRIMARY KEY(mid, cid, position);
ALTER TABLE genre ADD CONSTRAINT genre_pkey PRIMARY KEY(gid);
ALTER TABLE genre_contains ADD CONSTRAINT genrecontains_pkey PRIMARY KEY(mid, gid);
ALTER TABLE oscar ADD CONSTRAINT oscar_pkey PRIMARY KEY (oid);
ALTER TABLE list ADD CONSTRAINT list_pkey PRIMARY KEY(lid);
ALTER TABLE list_contains ADD CONSTRAINT listcontains_pkey PRIMARY KEY(mid, lid);
ALTER TABLE tyler ADD CONSTRAINT tyler_pkey PRIMARY KEY (tid);


-- --------------------------
-- Foreign Key constraints --
-- --------------------------

-- movie table country FK
ALTER TABLE movie ADD CONSTRAINT movie_country_fkey
FOREIGN KEY (country) REFERENCES country(country_name)
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


-- ------------------------
-- Integrity constraints --
-- ------------------------

-- movie year
ALTER TABLE movie ADD CONSTRAINT year_constraint
CHECK (year >= 1900 AND year <= 2014);

-- movie star rating
ALTER TABLE movie ADD CONSTRAINT star_constraint
CHECK (star_rating >= -2 AND star_rating <= 8);
-- -2 = not seen
-- -1 = N/A (so far I've only had to use this once: for "Jackass: The Movie")
--  0 = no stars
--  1 = ½*
--  2 = *
--  3 = *½
--  4 = **
--  5 = **½
--  6 = ***
--  7 = ***½
--  8 = ****

-- movie mpaa rating
ALTER TABLE movie ADD CONSTRAINT mpaa_constraint
CHECK (mpaa >= 0 AND mpaa <= 6);
-- 0 = NR (Not Rated)
-- 1 = G (General audiences)
-- 2 = PG (Parental Guidance suggested)
-- 3 = PG-13 (Parental Guidance strongly suggested for those under 13)
-- 4 = R (Restricted)
-- 5 = X (no one under 17 admitted [prior to 1990])
-- 6 = NC-17 (no one under 17 admitted [after 1990 when X renamed to NC-17])

-- list_contains rank
ALTER TABLE list_contains ADD CONSTRAINT rank_constraint
CHECK (rank > 0);

-- oscar_given_to ostatus
ALTER TABLE oscar_given_to ADD CONSTRAINT oscar_status_constraint
CHECK (ostatus >= 0 AND ostatus <= 2);
-- 0 = nominated
-- 1 = won
-- 2 = tie (I believe this has only happened once in oscar history)

-- oscar_given_to sharing_with
ALTER TABLE oscar_given_to ADD CONSTRAINT oscar_sharing_constraint
CHECK (sharing_with >= 0);

-- tyler_given_to tstatus
ALTER TABLE tyler_given_to ADD CONSTRAINT tyler_status_constraint
CHECK (tstatus >= 0 AND tstatus <= 2);
-- 0 = nominated
-- 1 = won
-- 2 = tie


-- ------------
-- Functions --
-- ------------

-- function to convert strings with accented characters to strings with regular
-- characters
CREATE FUNCTION to_ascii(bytea, name) RETURNS text STRICT AS 'to_ascii_encname'
LANGUAGE internal; 

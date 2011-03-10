/*
 * This sql script is for MySQL.  Also in the sql/ directory is a file
 * named init_pg_database.sql for Postgres.  The difference in these is the
 * data types.  MySQL offers a tinyint--a numeric type of only one byte in size,
 * which is great for a lot of these attributes.  However, Postgres only offers
 * smallint, which is two bytes.
 */

-- ----------------------
-- Create the database --
-- ----------------------

CREATE DATABASE filth;


-- --------------------
-- Create the tables --
-- --------------------

-- a movie entity
DROP TABLE IF EXISTS filth.movie CASCADE;
CREATE TABLE filth.movie (
mid serial NOT NULL,
title varchar(100) NOT NULL,
-- smallint is 2 bytes in Postgres, plenty of bits for a year
year smallint NOT NULL,
-- my star rating for the movie (or "haven't seen it"). Only 11 possible values.
-- tinyint to save space--front-end can translate the number to a star rating.
star_rating tinyint DEFAULT NULL,
-- mpaa rating (PG, R, etc).  This is varchar rather than tinyint in case the
-- MPAA adds to / changes the rating system.
mpaa varchar(7) DEFAULT NULL,
-- country of origin. This is a foreign key to country table, and it's a
-- tinyint because there's not that many countries. I only care about a movie
-- being associated with one country, so there is no country <--> movie
-- relationship, but in case I wanted to have multiple countries per movie, this
-- is a foreign key into a separate country table.
country tinyint DEFAULT NULL,
-- text field for comments/notes regarding the movie (MySQL does not allow a
-- default value for TEXT type
comments text);


-- a crewperson entity
DROP TABLE IF EXISTS filth.crew_person CASCADE;
CREATE TABLE filth.crew_person (
cid serial NOT NULL,
l_name varchar(20) NOT NULL,
f_name varchar(20) DEFAULT NULL,
m_name varchar(15) DEFAULT NULL);


-- crewperson <--> movie relationship
DROP TABLE IF EXISTS filth.worked_on CASCADE;
CREATE TABLE filth.worked_on (
mid smallint NOT NULL,
cid smallint NOT NULL,
position varchar(20) NOT NULL);


-- a genre entity
DROP TABLE IF EXISTS filth.genre CASCADE;
CREATE TABLE filth.genre (
gid serial NOT NULL,
gen_name varchar(25) NOT NULL);


-- genre <--> movie relationship
DROP TABLE IF EXISTS filth.genre_contains CASCADE;
CREATE TABLE filth.genre_contains (
mid smallint NOT NULL,
gid tinyint NOT NULL);


-- an oscar entity
DROP TABLE IF EXISTS filth.oscar CASCADE;
CREATE TABLE filth.oscar (
oid serial NOT NULL,
category varchar(40) NOT NULL);


-- oscar <--> movie relationship
DROP TABLE IF EXISTS filth.oscar_given_to CASCADE;
CREATE TABLE filth.oscar_given_to (
mid smallint NOT NULL,
oid tinyint NOT NULL,
cid smallint NOT NULL,
-- status of the oscar: 0, 1, or 2 (nominated, won, or tie, respectively)
status tinyint DEFAULT NULL);


-- a list entity
DROP TABLE IF EXISTS filth.list CASCADE;
CREATE TABLE filth.list (
lid serial NOT NULL,
list_title varchar(50) NOT NULL,
list_author varchar(25) DEFAULT NULL);


-- list <--> movie relationship
DROP TABLE IF EXISTS filth.list_contains CASCADE;
CREATE TABLE filth.list_contains (
mid smallint NOT NULL,
lid tinyint NOT NULL,
rank smallint DEFAULT NULL);


-- --------------------------
-- Primary Key constraints --
-- --------------------------

ALTER TABLE filth.movie ADD CONSTRAINT movie_pkey PRIMARY KEY(mid);
ALTER TABLE filth.crew_person ADD CONSTRAINT crew_pkey PRIMARY KEY(cid);
ALTER TABLE filth.worked_on ADD CONSTRAINT worked_pkey PRIMARY KEY(mid, cid, position);
ALTER TABLE filth.genre ADD CONSTRAINT genre_pkey PRIMARY KEY(gid);
ALTER TABLE filth.genre_contains ADD CONSTRAINT genrecontains_pkey PRIMARY KEY(mid, gid);
ALTER TABLE filth.oscar ADD CONSTRAINT oscar_pkey PRIMARY KEY (oid);
ALTER TABLE filth.oscar_given_to ADD CONSTRAINT oscargiven_pkey PRIMARY KEY(mid, oid);
ALTER TABLE filth.list ADD CONSTRAINT list_pkey PRIMARY KEY(lid);
ALTER TABLE filth.list_contains ADD CONSTRAINT listcontains_pkey PRIMARY KEY(mid, lid);


-- --------------------------
-- Foreign Key constraints --
-- --------------------------

-- worked_on table movie FK
ALTER TABLE filth.worked_on ADD CONSTRAINT worked_mid_fkey
FOREIGN KEY (mid) REFERENCES filth.movie(mid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- worked_on table crew FK
ALTER TABLE filth.worked_on ADD CONSTRAINT worked_cid_fkey
FOREIGN KEY (cid) REFERENCES filth.crew_person(cid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- genre_contains table movie FK
ALTER TABLE filth.genre_contains ADD CONSTRAINT genre_mid_fkey
FOREIGN KEY (mid) REFERENCES filth.movie(mid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- genre_contains table genre FK
ALTER TABLE filth.genre_contains ADD CONSTRAINT genre_gid_fkey
FOREIGN KEY (gid) REFERENCES filth.genre(gid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- oscar_given_to table movie FK
ALTER TABLE filth.oscar_given_to ADD CONSTRAINT oscar_mid_fkey
FOREIGN KEY (mid) REFERENCES filth.movie(mid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- oscar_given_to table crew FK
ALTER TABLE filth.oscar_given_to ADD CONSTRAINT oscar_cid_fkey
FOREIGN KEY (cid) REFERENCES filth.crew_person(cid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- oscar_given_to table category FK
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


-- ------------------------
-- Integrity constraints --
-- ------------------------

-- movie year
ALTER TABLE filth.movie ADD CONSTRAINT year_constraint
CHECK (year >= 1900 AND year <= 2012);

-- movie star rating
ALTER TABLE filth.movie ADD CONSTRAINT star_constraint
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
ALTER TABLE filth.movie ADD CONSTRAINT mpaa_constraint
CHECK (mpaa IN ('NR', 'G', 'PG', 'PG-13', 'R', 'X', 'NC-17'));

-- movie country
ALTER TABLE movie ADD CONSTRAINT country_constraint
CHECK (country >= 1 AND country <= 37);
--  1 = USA
--  2 = France
--  3 = England
--  4 = Canada
--  5 = China
--  6 = Russia
--  7 = Germany
--  8 = Argentina
--  9 = Portugal
-- 10 = Spain
-- 11 = Mexico
-- 12 = Italy
-- 13 = Ireland
-- 14 = Scotland
-- 15 = Czech Republic
-- 16 = Iran
-- 17 = The Netherlands
-- 18 = Sweden
-- 19 = Finland
-- 20 = Norway
-- 21 = Poland
-- 22 = Bosnia
-- 23 = Japan
-- 24 = Taiwan
-- 25 = India
-- 26 = Greece
-- 27 = Israel
-- 28 = Lebanon
-- 29 = South Africa
-- 30 = Australia
-- 31 = New Zealand
-- 32 = Brazil
-- 33 = Iceland
-- 34 = Vietnam
-- 35 = Denmark
-- 36 = Belgium
-- 37 = Switzerland

-- listcontains rank
ALTER TABLE filth.list_contains ADD CONSTRAINT rank_constraint
CHECK (rank > 0);

-- oscargivento status
ALTER TABLE filth.oscar_given_to ADD CONSTRAINT oscar_status_constraint
CHECK (status >= 0 AND status <= 2);
-- 0 = nominated
-- 1 = won
-- 2 = tie (I believe this has only happened once in oscar history)

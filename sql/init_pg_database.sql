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
title varchar(100) NOT NULL,
-- smallint is 2 bytes in Postgres, plenty of bits for a year
year smallint NOT NULL,
-- my star rating for the movie (or "haven't seen it"). Only 11 possible values.
-- smallint to save space--front-end can translate the number to a star rating.
star_rating smallint DEFAULT NULL,
-- mpaa rating (PG, R, etc).  This is varchar rather than smallint in case the
-- MPAA adds to / changes the rating system.
mpaa varchar(7) DEFAULT NULL,
-- country of origin. This is a foreign key to country table, and it's a
-- smallint because there's not that many countries. Even one byte would suffice
-- but Postgres does not have a tinyint type. I only care about a movie being
-- associated with one country, so there is no country <--> movie relationship,
-- but in case I wanted to have multiple countries per movie, this is a foreign
-- key into a separate country table.
country smallint DEFAULT NULL);


-- a crewperson entity
DROP TABLE IF EXISTS crew_person CASCADE;
CREATE TABLE crew_person (
cid serial NOT NULL,
l_name varchar(20) NOT NULL,
f_name varchar(20) DEFAULT NULL,
m_name varchar(15) DEFAULT NULL);


-- crewperson <--> movie relationship
DROP TABLE IF EXISTS worked_on CASCADE;
CREATE TABLE worked_on (
mid smallint NOT NULL,
cid smallint NOT NULL,
position varchar(20) NOT NULL);


-- a genre entity
DROP TABLE IF EXISTS genre CASCADE;
CREATE TABLE genre (
gid serial NOT NULL,
gen_name varchar(25) NOT NULL);


-- genre <--> movie relationship
DROP TABLE IF EXISTS genre_contains CASCADE;
CREATE TABLE genre_contains (
mid smallint NOT NULL,
gid smallint NOT NULL);


-- an oscar entity
DROP TABLE IF EXISTS oscar CASCADE;
CREATE TABLE oscar (
oid serial NOT NULL,
category varchar(40) NOT NULL);


-- oscar <--> movie relationship
DROP TABLE IF EXISTS oscar_given_to CASCADE;
CREATE TABLE oscar_given_to (
mid smallint NOT NULL,
oid smallint NOT NULL,
cid smallint NOT NULL,
-- status of the oscar: 0, 1, or 2 (nominated, won, or tie, respectively)
status smallint DEFAULT NULL);


-- a list entity
DROP TABLE IF EXISTS list CASCADE;
CREATE TABLE list (
lid serial NOT NULL,
list_title varchar(50) NOT NULL,
list_author varchar(25) DEFAULT NULL);


-- list <--> movie relationship
DROP TABLE IF EXISTS list_contains CASCADE;
CREATE TABLE list_contains (
mid smallint NOT NULL,
lid smallint NOT NULL,
rank smallint DEFAULT NULL);


-- a country entity
--
-- this has no relationship at this time.  It is possible that a country <-->
-- movie relationship could be added in the future so that a movie could have
-- multiple countries associated with it (I don't care), but as of now this only
-- exists so that there doesn't have to be a big integrity contraint for a
-- movie's country (like the star_contraint for the movie table).
DROP TABLE IF EXISTS country CASCADE;
CREATE TABLE country (
coid serial NOT NULL,
country_name varchar(30) NOT NULL);


-- --------------------------
-- Primary Key constraints --
-- --------------------------

ALTER TABLE movie ADD CONSTRAINT movie_pkey PRIMARY KEY(mid);
ALTER TABLE crew_person ADD CONSTRAINT crew_pkey PRfree movies onlineIMARY KEY(cid);
ALTER TABLE worked_on ADD CONSTRAINT worked_pkey PRIMARY KEY(mid, cid, position);
ALTER TABLE genre ADD CONSTRAINT genre_pkey PRIMARY KEY(gid);
ALTER TABLE genre_contains ADD CONSTRAINT genrecontains_pkey PRIMARY KEY(mid, gid);
ALTER TABLE oscar ADD CONSTRAINT oscar_pkey PRIMARY KEY (oid);
ALTER TABLE oscar_given_to ADD CONSTRAINT oscargiven_pkey PRIMARY KEY(mid, oid);
ALTER TABLE list ADD CONSTRAINT list_pkey PRIMARY KEY(lid);
ALTER TABLE list_contains ADD CONSTRAINT listcontains_pkey PRIMARY KEY(mid, lid);
ALTER TABLE country ADD CONSTRAINT country_pkey PRIMARY KEY(coid);


-- --------------------------
-- Foreign Key constraints --
-- --------------------------

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
ALTER TABLE oscar_given_to ADD CONSTRAINT oscar_mid_fkey
FOREIGN KEY (mid) REFERENCES movie(mid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- oscar_given_to table crew FK
ALTER TABLE oscar_given_to ADD CONSTRAINT oscar_cid_fkey
FOREIGN KEY (cid) REFERENCES crew_person(cid)
ON UPDATE CASCADE ON DELETE CASCADE;

-- oscar_given_to table category FK
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


-- ------------------------
-- Integrity constraints --
-- ------------------------

-- movie year
ALTER TABLE movie ADD CONSTRAINT year_constraint
CHECK (year >= 1900 AND year <= 2012);

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
CHECK (mpaa IN ('NR', 'G', 'PG', 'PG-13', 'R', 'X', 'NC-17'));

-- listcontains rank
ALTER TABLE list_contains ADD CONSTRAINT rank_constraint
CHECK (rank > 0);

-- oscargivento status
ALTER TABLE oscar_given_to ADD CONSTRAINT oscar_status_constraint
CHECK (status >= 0 AND status <= 2);
-- 0 = nominated
-- 1 = won
-- 2 = tie (I believe this has only happened once in oscar history)

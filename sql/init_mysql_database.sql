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
-- my star rating for the movie (or "haven't seen it"). Only 11 possible values
-- (listed in the integrity constraint section at the end of this file.
-- smallint to save space--front-end can translate the number to a star rating.
star_rating tinyint DEFAULT NULL,
-- mpaa rating (PG, R, etc).  Only 7 possible values (listed in the integrity
-- constraints section at the end of this file).  smallint to save space--front-
-- end can translate the number to the MPAA rating as a string.
mpaa tinyint DEFAULT NULL,
-- country of origin. I only care about a movie being associated with one
-- country, so there is no country <--> movie relationship.  In order to prevent
-- inserting spelling errors and bogus countries (e.g. france instead of France,
-- or Sweden instead of Sewden), there will be a foreign key constraint on this
-- column referencing the country table.
country varchar(35) DEFAULT NULL,
-- text field for comments/notes regarding the movie (MySQL does not allow a
-- default value for TEXT type
comments text);


-- a country entity (only used as an integrity constraint for movie.country)
DROP TABLE IF EXISTS filth.country CASCADE;
CREATE TABLE filth.country (
country_name varchar(35) NOT NULL);


-- a crewperson entity
DROP TABLE IF EXISTS filth.crew_person CASCADE;
CREATE TABLE filth.crew_person (
cid serial NOT NULL,
l_name varchar(20) NOT NULL,
f_name varchar(20) DEFAULT NULL,
m_name varchar(15) DEFAULT NULL,
known_as varchar(16) DEFAULT NULL);


-- crewperson <--> movie relationship
DROP TABLE IF EXISTS filth.worked_on CASCADE;
CREATE TABLE filth.worked_on (
mid smallint NOT NULL,
cid smallint NOT NULL,
position varchar(20) NOT NULL);


-- a position entity (only used as an integrity constraint for
-- crew_person.known_as and worked_on.position).  Normally I would just use a
-- CHECK constraint for this since there are only about 5 positions I care about
-- right now, but if I ever wanted to add a position (such as costume designer)
-- this would make it much easier.
DROP TABLE IF EXISTS filth.position CASCADE;
CREATE TABLE filth.position (
position_title varchar(20) NOT NULL);


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
ocategory varchar(40) NOT NULL);


-- oscar <--> movie relationship
DROP TABLE IF EXISTS filth.oscar_given_to CASCADE;
CREATE TABLE filth.oscar_given_to (
mid smallint NOT NULL,
oid tinyint NOT NULL,
cid smallint DEFAULT NULL,
year smallint NOT NULL,
-- status of the oscar: 0, 1, or 2 (nominated, won, or tie, respectively)
ostatus tinyint DEFAULT NULL,
-- indicates how many other recipients this nominee is sharing the nomination
-- with
sharing_with tinyint DEFAULT NULL);


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


-- a tyler entity (like oscar, but for my annual awards)
DROP TABLE IF EXISTS filth.tyler CASCADE;
CREATE TABLE filth.tyler (
tid serial NOT NULL,
tcategory varchar(40) NOT NULL);


-- tyler <--> movie relationship
DROP TABLE IF EXISTS filth.tyler_given_to CASCADE;
CREATE TABLE filth.tyler_given_to (
mid smallint NOT NULL,
tid smallint NOT NULL,
cid smallint DEFAULT NULL,
-- status of the award: 0, 1, or 2 (nominated, won, or tie, respectively)
tstatus smallint DEFAULT NULL,
-- this attribute is only used for the Best Scene category for the title of the
-- scene--a waste, I know, but what else should I do?
scene_title text);


-- --------------------------
-- Primary Key constraints --
-- --------------------------

ALTER TABLE filth.movie ADD CONSTRAINT movie_pkey PRIMARY KEY(mid);
ALTER TABLE filth.country ADD CONSTRAINT country_pkey PRIMARY KEY(country_name);
ALTER TABLE filth.crew_person ADD CONSTRAINT crew_pkey PRIMARY KEY(cid);
ALTER TABLE filth.worked_on ADD CONSTRAINT worked_pkey PRIMARY KEY(mid, cid, position);
ALTER TABLE filth.position ADD CONSTRAINT position_pkey PRIMARY KEY(position_title);
ALTER TABLE filth.genre ADD CONSTRAINT genre_pkey PRIMARY KEY(gid);
ALTER TABLE filth.genre_contains ADD CONSTRAINT genrecontains_pkey PRIMARY KEY(mid, gid);
ALTER TABLE filth.oscar ADD CONSTRAINT oscar_pkey PRIMARY KEY (oid);
ALTER TABLE filth.oscar_given_to ADD CONSTRAINT oscar_given_to_pkey PRIMARY KEY(mid, oid, cid);
ALTER TABLE filth.list ADD CONSTRAINT list_pkey PRIMARY KEY(lid);
ALTER TABLE filth.list_contains ADD CONSTRAINT listcontains_pkey PRIMARY KEY(mid, lid);
ALTER TABLE filth.tyler ADD CONSTRAINT tyler_pkey PRIMARY KEY (tid);
ALTER TABLE filth.tyler_given_to ADD CONSTRAINT tyler_given_to_pkey PRIMARY KEY(mid, tid, cid);


-- ---------------------
-- Unique Constraints --
-- ---------------------

ALTER TABLE movie ADD CONSTRAINT movie_title_year_constraint UNIQUE(title, year);


-- --------------------------
-- Foreign Key constraints --
-- --------------------------

-- movie table country FK
ALTER TABLE filth.movie ADD CONSTRAINT movie_country_fkey
FOREIGN KEY (country) REFERENCES filth.country(country_name)
ON UPDATE CASCADE ON DELETE SET NULL;

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
CHECK (mpaa >= 0 AND mpaa <= 6);
-- 0 = NR (Not Rated)
-- 1 = G (General audiences)
-- 2 = PG (Parental Guidance suggested)
-- 3 = PG-13 (Parental Guidance strongly suggested for those under 13)
-- 4 = R (Restricted)
-- 5 = X (no one under 17 admitted [prior to 1990])
-- 6 = NC-17 (no one under 17 admitted [after 1990 when X renamed to NC-17])

-- list_contains rank
ALTER TABLE filth.list_contains ADD CONSTRAINT rank_constraint
CHECK (rank > 0);

-- oscar_given_to status
ALTER TABLE filth.oscar_given_to ADD CONSTRAINT oscar_status_constraint
CHECK (ostatus >= 0 AND ostatus <= 2);
-- 0 = nominated
-- 1 = won
-- 2 = tie (I believe this has only happened once in oscar history)

-- oscar_given_to sharing_with
ALTER TABLE filth.oscar_given_to ADD CONSTRAINT oscar_sharing_constraint
CHECK (sharing_with >= 0);

-- tyler_given_to status
ALTER TABLE filth.tyler_given_to ADD CONSTRAINT tyler_status_constraint
CHECK (tstatus >= 0 AND tstatus <= 2);
-- 0 = nominated
-- 1 = won
-- 2 = tie

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
-- country of origin. This is a foreign key to country table, and it's a
-- smallint because there's not that many countries. Even one byte would suffice
-- but Postgres does not have a tinyint type. I only care about a movie being
-- associated with one country, so there is no country <--> movie relationship,
-- but in case I wanted to have multiple countries per movie, this is a foreign
-- key into a separate country table.
country smallint DEFAULT NULL,
-- text field for comments/notes regarding the movie
comments text DEFAULT NULL);


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
cid smallint NOT NULL,
-- status of the oscar: 0, 1, or 2 (nominated, won, or tie, respectively)
ostatus smallint DEFAULT NULL);


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

ALTER TABLE movie ADD CONSTRAINT movie_pkey PRIMARY KEY(mid);
ALTER TABLE crew_person ADD CONSTRAINT crew_pkey PRIMARY KEY(cid);
ALTER TABLE worked_on ADD CONSTRAINT worked_pkey PRIMARY KEY(mid, cid, position);
ALTER TABLE genre ADD CONSTRAINT genre_pkey PRIMARY KEY(gid);
ALTER TABLE genre_contains ADD CONSTRAINT genrecontains_pkey PRIMARY KEY(mid, gid);
ALTER TABLE oscar ADD CONSTRAINT oscar_pkey PRIMARY KEY (oid);
ALTER TABLE oscar_given_to ADD CONSTRAINT oscargiven_pkey PRIMARY KEY(mid, oid);
ALTER TABLE list ADD CONSTRAINT list_pkey PRIMARY KEY(lid);
ALTER TABLE list_contains ADD CONSTRAINT listcontains_pkey PRIMARY KEY(mid, lid);
ALTER TABLE tyler ADD CONSTRAINT tyler_pkey PRIMARY KEY (tid);
ALTER TABLE tyler_given_to ADD CONSTRAINT tylergiven_pkey PRIMARY KEY(mid, tid);


-- --------------------------
-- Foreign Key constraints --
-- --------------------------

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
CHECK (mpaa >= 0 AND mpaa <= 6);
-- 0 = NR (Not Rated)
-- 1 = G (General audiences)
-- 2 = PG (Parental Guidance suggested)
-- 3 = PG-13 (Parental Guidance strongly suggested for those under 13)
-- 4 = R (Restricted)
-- 5 = X (no one under 17 admitted [prior to 1990])
-- 6 = NC-17 (no one under 17 admitted [after 1990 when X renamed to NC-17])

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
ALTER TABLE list_contains ADD CONSTRAINT rank_constraint
CHECK (rank > 0);

-- oscargivento status
ALTER TABLE oscar_given_to ADD CONSTRAINT oscar_status_constraint
CHECK (status >= 0 AND status <= 2);
-- 0 = nominated
-- 1 = won
-- 2 = tie (I believe this has only happened once in oscar history)

-- tylergivento status
ALTER TABLE tyler_given_to ADD CONSTRAINT tyler_status_constraint
CHECK (status >= 0 AND status <= 2);
-- 0 = nominated
-- 1 = won
-- 2 = tie

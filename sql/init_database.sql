-----------------------
-- Create the tables --
-----------------------

-- a movie entity
CREATE TABLE movie (
mid serial NOT NULL,
title varchar(100) NOT NULL,
year integer NOT NULL,
-- my star rating for the movie (or "haven't see it").  Only 11 possible values.
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
CREATE TABLE crew_person (
cid serial NOT NULL,
l_name varchar(20) NOT NULL,
f_name varchar(20) DEFAULT NULL,
m_name varchar(15) DEFAULT NULL);


-- crewperson <--> movie relationship
CREATE TABLE worked_on (
mid integer NOT NULL,
cid integer NOT NULL,
position varhcar(20) NOT NULL);


-- a genre entity
CREATE TABLE genre (
gid serial NOT NULL,
gen_name varchar(25) NOT NULL);


-- genre <--> movie relationship
CREATE TABLE genre_contains (
mid integer NOT NULL,
gid integer NOT NULL);


-- an oscar entity
CREATE TABLE oscar (
oid serial NOT NULL,
category varchar(25) NOT NULL);


-- oscar <--> movie relationship
CREATE TABLE oscar_given_to (
mid integer NOT NULL,
oid integer NOT NULL,
cid integer NOT NULL,
--status of the oscar: 0, 1, or 2 (nominated, won, or tie, respectively)
status smallint DEFAULT NULL);


-- a list entity
CREATE TABLE list (
lid serial NOT NULL,
list_title varchar(50) NOT NULL,
list_author varchar(25) DEFAULT NULL);


-- list <--> movie relationship
CREATE TABLE list_contains (
mid integer NOT NULL,
lid integer NOT NULL,
rank integer DEFAULT NULL);


-- a country entity
--
-- this has no relationship at this time.  It is possible that a country <-->
-- movie relationship could be added in the future so that a movie could have
-- multiple countries associated with it (I don't care), but as of now this only
-- exists so that there doesn't have to be a big OR'd integrity contraint for a
-- movie's country (like the star_contraint for the movie table).
CREATE TABLE country (
coid serial NOT NULL,
country_name varchar(30) NOT NULL);

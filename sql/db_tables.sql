-----------------------
-- Create the tables --
-----------------------

-- a movie entity
CREATE TABLE movie (
mid serial NOT NULL,
title varchar(100) NOT NULL,
year integer NOT NULL,
star_rating varchar(11) DEFAULT NULL,
mpaa varchar(7) DEFAULT NULL,
country varchar(30) DEFAULT NULL);

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
gen_name varchar(25) NOT NULL);

-- genre <--> movie relationship
CREATE TABLE genre_contains (
mid integer NOT NULL,
gen_name varchar(25) NOT NULL);

-- an oscar entity
CREATE TABLE oscar (
category varchar(25) NOT NULL);

-- oscar <--> movie relationship
CREATE TABLE oscar_given_to (
mid integer NOT NULL,
category varchar(25) NOT NULL,
recipient integer NOT NULL,       --(foreign key to crewperson.crewid)
status varchar(10) DEFAULT NULL); --(nominated or won)

-- a list entity
CREATE TABLE list (
list_title varchar(50) NOT NULL,
list_author varchar(25) DEFAULT NULL);

-- list <--> movie relationship
CREATE TABLE list_contains (
mid integer NOT NULL,
list_title varchar(50) NOT NULL,
rank integer DEFAULT NULL);

-- a country entity
--
-- this has no relationship at this time.  It is possible that a country <-->
-- movie relationship could be added in the future so that a movie could have
-- multiple countries associated with it (I don't care), but as of now this only
-- exists so that there doesn't have to be a big OR'd integrity contraint for a
-- movie's country (like the star_contraint for the movie table).
CREATE TABLE country (
country_name varchar(30) NOT NULL);

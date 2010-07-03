-----------------------
-- Create the tables --
-----------------------

-- a movie entity
CREATE TABLE movie (
movieid serial NOT NULL,
title varchar(100) NOT NULL,
year integer NOT NULL,
mystarrating varchar(11) DEFAULT NULL,
mpaa varchar(7) DEFAULT NULL,
country varchar(30) DEFAULT NULL);

-- a crewperson entity
CREATE TABLE crewperson (
crewid serial NOT NULL,
lname varchar(20) NOT NULL,
fname varchar(20) DEFAULT NULL,
mname varchar(15) DEFAULT NULL);

-- crewperson <--> movie relationship
CREATE TABLE workedon (
movieid integer NOT NULL,
crewid integer NOT NULL,
position varhcar(20) NOT NULL);

-- a genre entity
CREATE TABLE genre (
genname varchar(25) NOT NULL);

-- genre <--> movie relationship
CREATE TABLE genrecontains (
movieid integer NOT NULL,
genname varchar(25) NOT NULL);

-- an oscar entity
CREATE TABLE oscar (
category varchar(25) NOT NULL);

-- oscar <--> movie relationship
CREATE TABLE oscargivento (
movieid integer NOT NULL,
category varchar(25) NOT NULL,
recipient integer NOT NULL,       --(foreign key to crewperson.crewid)
status varchar(10) DEFAULT NULL); --(nominated or won)

-- a list entity
CREATE TABLE list (
listtitle varchar(50) NOT NULL,
listauthor varchar(25) DEFAULT NULL);

-- list <--> movie relationship
CREATE TABLE listcontains (
movieid integer NOT NULL,
listtitle varchar(50) NOT NULL,
rankinlist integer DEFAULT NULL);

-- a country entity
--
-- this has no relationship at this time.  It is possible that a country <-->
-- movie relationship could be added in the future so that a movie could have
-- multiple countries associated with it (I don't care), but as of now this only
-- exists so that there doesn't have to be a big OR'd integrity contraint for a
-- movie's country (like the star_contraint for the movie table).
CREATE TABLE country (
countryname varchar(30) NOT NULL);

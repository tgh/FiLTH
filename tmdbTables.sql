-----------------------
-- Create the tables --
-----------------------

CREATE TABLE movie (
movieid serial NOT NULL,
title varchar(100) NOT NULL,
year integer NOT NULL,
mystarrating varchar(11) DEFAULT NULL,
country varchar(30) DEFAULT NULL,
mpaa varchar(7) DEFAULT NULL);

CREATE TABLE crewperson (
crewid serial NOT NULL,
fname varchar(15) NOT NULL,
lname varchar(20) NOT NULL);

CREATE TABLE workedon (
movieid integer NOT NULL,
crewid integer NOT NULL,
position varhcar(20) NOT NULL);

CREATE TABLE genre (
genname varchar(25) NOT NULL);

CREATE TABLE genrecontains (
movieid integer NOT NULL,
genname varchar(25) NOT NULL);

CREATE TABLE oscar (
category varchar(25) NOT NULL);

CREATE TABLE oscargivento (
movieid integer NOT NULL,
category varchar(25) NOT NULL,
recipient integer NOT NULL,
status varchar(10) DEFAULT NULL);

CREATE TABLE list (
listtitle varchar(50) NOT NULL,
listauthor varchar(25) DEFAULT NULL);

CREATE TABLE listcontains (
movieid integer NOT NULL,
listtitle varchar(50) NOT NULL,
rankinlist integer DEFAULT NULL);

CREATE TABLE country (
countryname varchar(30) NOT NULL);

/*
CREATE TABLE movie (
title varchar(100) NOT NULL,
year integer NOT NULL,
mystarrating varchar(11) DEFAULT NULL,
country varchar(30) DEFAULT NULL,
mpaa varchar(7) DEFAULT NULL);

CREATE TABLE director (
dirname varchar(25) NOT NULL);

CREATE TABLE actor (
actname varchar(25) NOT NULL);

CREATE TABLE screenwriter (
scrname varchar(25) NOT NULL);

CREATE TABLE cinematographer (
cinname varchar(25) NOT NULL);

CREATE TABLE oscar (
title varchar(100) NOT NULL,
year integer NOT NULL,
category varchar(25) NOT NULL,
recipientName varchar(25) DEFAULT NULL,
status varchar(10) DEFAULT NULL);

CREATE TABLE directed (
title varchar(100) NOT NULL,
year integer NOT NULL,
dirname varchar(25) NOT NULL);

CREATE TABLE directed (
title varchar(100) NOT NULL,
year integer NOT NULL,
dirfname varchar(15) NOT NULL,
dirlname varchar(20) NOT NULL);

CREATE TABLE actedin (
title varchar(100) NOT NULL,
year integer NOT NULL,

CREATE TABLE actedin (
title varchar(100) NOT NULL,
year integer NOT NULL,
actfname varchar(15) NOT NULL,
actlname varchar(20) NOT NULL);

CREATE TABLE wrote (
title varchar(100) NOT NULL,
year integer NOT NULL,
scrname varchar(25) NOT NULL);

CREATE TABLE wrote (
title varchar(100) NOT NULL,
year integer NOT NULL,
scrfname varchar(15) NOT NULL,
scrlname varchar(20) NOT NULL);

CREATE TABLE shot (
title varchar(100) NOT NULL,
year integer NOT NULL,
cinname varchar(25) NOT NULL);

CREATE TABLE shot (
title varchar(100) NOT NULL,
year integer NOT NULL,
cinfname varchar(15) NOT NULL,
cinlname varchar(20) NOT NULL);
*/

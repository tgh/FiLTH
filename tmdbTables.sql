-----------------------
-- Create the tables --
-----------------------

CREATE TABLE movie (
title varchar(100) NOT NULL,
year integer NOT NULL,
mystarrating varchar(11) DEFAULT NULL,
country varchar(30) DEFAULT NULL,
mpaa varchar(7) DEFAULT NULL);
/*
CREATE TABLE crewperson (
fname varchar(15) NOT NULL,
lname varchar(20) NOT NULL,
position varchar(20) NOT NULL);

CREATE TABLE genre (
genname varchar(25) NOT NULL);
*/
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
/*
CREATE TABLE oscar (
title varchar(100) NOT NULL,
year integer NOT NULL,
category varchar(25) NOT NULL,
recipientfname varchar(15) NOT NULL,
recipientlname varchar(20) NOT NULL,
status varchar(10) DEFAULT NULL);
*/
CREATE TABLE list (
listtitle varchar(50) NOT NULL,
listauthor varchar(25) DEFAULT NULL);

CREATE TABLE directed (
title varchar(100) NOT NULL,
year integer NOT NULL,
dirname varchar(25) NOT NULL);
/*
CREATE TABLE directed (
title varchar(100) NOT NULL,
year integer NOT NULL,
dirfname varchar(15) NOT NULL,
dirlname varchar(20) NOT NULL);
*/
CREATE TABLE actedin (
title varchar(100) NOT NULL,
year integer NOT NULL,
actname varchar(25) NOT NULL);
/*
CREATE TABLE actedin (
title varchar(100) NOT NULL,
year integer NOT NULL,
actfname varchar(15) NOT NULL,
actlname varchar(20) NOT NULL);
*/
CREATE TABLE wrote (
title varchar(100) NOT NULL,
year integer NOT NULL,
scrname varchar(25) NOT NULL);
/*
CREATE TABLE wrote (
title varchar(100) NOT NULL,
year integer NOT NULL,
scrfname varchar(15) NOT NULL,
scrlname varchar(20) NOT NULL);
*/
CREATE TABLE shot (
title varchar(100) NOT NULL,
year integer NOT NULL,
cinname varchar(25) NOT NULL);
/*
CREATE TABLE shot (
title varchar(100) NOT NULL,
year integer NOT NULL,
cinfname varchar(15) NOT NULL,
cinlname varchar(20) NOT NULL);
*/
CREATE TABLE listcontains (
title varchar(100) NOT NULL,
year integer NOT NULL,
listtitle varchar(50) NOT NULL,
rankinlist integer DEFAULT NULL);
/*
CREATE TABLE genrecontains (
title varchar(100) NOT NULL,
year integer NOT NULL,
genname varchar(25) NOT NULL);

CREATE TABLE country (
countryname varchar(30) NOT NULL);
*/

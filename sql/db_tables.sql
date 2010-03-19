-----------------------
-- Create the tables --
-----------------------

CREATE TABLE movie (
movieid serial NOT NULL,
title varchar(100) NOT NULL,
year integer NOT NULL,
mystarrating varchar(11) DEFAULT NULL,
mpaa varchar(7) DEFAULT NULL,
country varchar(30) DEFAULT NULL);

CREATE TABLE crewperson (
crewid serial NOT NULL,
lname varchar(20) NOT NULL,
fname varchar(20) DEFAULT NULL,
mname varchar(15) NOT NULL);

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

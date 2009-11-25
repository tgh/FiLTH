-----------------------
-- Create the tables --
-----------------------

CREATE TABLE movie (
title varchar(70) NOT NULL,
year integer NOT NULL,
mystarrating varchar(8) DEFAULT NULL,
country varchar(30) DEFAULT NULL,
viewstatus varchar(11) DEFAULT NULL);

CREATE TABLE director (
dirname varchar(25) NOT NULL);

CREATE TABLE actor (
actname varchar(25) NOT NULL);

CREATE TABLE screenwriter (
scrname varchar(25) NOT NULL);

CREATE TABLE cinematographer (
cinname varchar(25) NOT NULL);

CREATE TABLE oscar (
oscarmovie varchar(70) NOT NULL,
oscaryear integer NOT NULL,
category varchar(25) NOT NULL,
recipientName varchar(25) DEFAULT NULL,
status varchar(10) DEFAULT NULL);

CREATE TABLE list (
listtitle varchar(50) NOT NULL,
listauthor varchar(25) DEFAULT NULL);

CREATE TABLE directed (
title varchar(70) NOT NULL,
year integer NOT NULL,
dirname varchar(25) NOT NULL);

CREATE TABLE actedin (
title varchar(70) NOT NULL,
year integer NOT NULL,
actname varchar(25) NOT NULL,
chatactername varchar(25) DEFAULT NULL);

CREATE TABLE wrote (
title varchar(70) NOT NULL,
year integer NOT NULL,
scrname varchar(25) NOT NULL);

CREATE TABLE shot (
title varchar(70) NOT NULL,
year integer NOT NULL,
cinname varchar(25) NOT NULL);

CREATE TABLE listcontains (
title varchar(70) NOT NULL,
year integer NOT NULL,
listtitle varchar(50) NOT NULL,
rankinlist integer DEFAULT NULL);

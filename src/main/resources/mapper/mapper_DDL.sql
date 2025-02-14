CREATE DATABASE LOSTARKPROJECT;
USE LOSTARKPROJECT;

CREATE TABLE CONTENT (
CONTENT_NAME VARCHAR(255) PRIMARY KEY NOT NULL,
CONTENT_ALARMSETTINGSTATUS CHAR(1) UNIQUE NOT NULL,
CONTENT_CATEGORY VARCHAR(30) NOT NULL,
CONTENT_ICON VARCHAR(255) NOT NULL,
CONTENT_MINITEMLEVEL INTEGER NOT NULL,
CONTENT_STARTTIME DATE DEFAULT (CURRENT_DATE) NOT NULL
);

CREATE TABLE USER (
CHARACTERNAME VARCHAR(200) PRIMARY KEY NOT NULL,
USER_ID VARCHAR(100) NOT NULL,
USER_PW VARCHAR(255) NOT NULL,
JOIN_AT DATE NOT NULL,
CONTENT_ALARMSETTINGSTATUS CHAR(1) NOT NULL,
FOREIGN KEY (CONTENT_ALARMSETTINGSTATUS) REFERENCES CONTENT(CONTENT_ALARMSETTINGSTATUS)
);

CREATE TABLE USER_CHARACTER (
CHARACTERNAME VARCHAR(200) NOT NULL,
SERVERNAME VARCHAR(30) NOT NULL,
CHARACTERCLASSNAME VARCHAR(30) NOT NULL,
ITEMAVGLEVEL INTEGER NOT NULL,
SIBLINGS VARCHAR(200) NOT NULL,
ITEMMAXLEVEL INTEGER NOT NULL,
EXPEDITIONLEVEL INTEGER NOT NULL,
FOREIGN KEY (CHARACTERNAME) REFERENCES USER(CHARACTERNAME)
);



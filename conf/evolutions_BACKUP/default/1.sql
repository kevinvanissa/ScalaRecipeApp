# --- !Ups
CREATE TABLE `user` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(120) NOT NULL,
  `password` varchar(140) DEFAULT NULL,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;


CREATE TABLE `kitchen` (
  `kid` int(11) NOT NULL AUTO_INCREMENT,
  `storagename` varchar(120) DEFAULT NULL,
  PRIMARY KEY (`kid`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;



CREATE TABLE `dietarypref` (
  `did` int(11) NOT NULL AUTO_INCREMENT,
  `diet_pref` varchar(120) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  PRIMARY KEY (`did`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;


CREATE TABLE `recipe` (
  `rid` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(120) NOT NULL,
  `rtype` int(11) DEFAULT NULL,
  `description` varchar(120) DEFAULT NULL,
  `ingredient` text,
  `instruction` text,
  `image` varchar(140) DEFAULT NULL,
  PRIMARY KEY (`rid`),
  CONSTRAINT `recipe_ibfk_1` FOREIGN KEY (`rtype`) REFERENCES `dietarypref` (`did`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;


CREATE TABLE `profile` (
  `uid` int(11) NOT NULL,
  `fname` varchar(120) DEFAULT NULL,
  `lname` varchar(120) DEFAULT NULL,
  PRIMARY KEY (`uid`),
  CONSTRAINT `profile_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE `stores` (
  `sid` int(11) NOT NULL AUTO_INCREMENT,
  `kid` int(11) NOT NULL,
  `ingredient` varchar(120) NOT NULL,
  PRIMARY KEY (`sid`),
  KEY `kid` (`kid`),
  CONSTRAINT `stores_ibfk_1` FOREIGN KEY (`kid`) REFERENCES `kitchen` (`kid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE `mealplan` (
  `uid` int(11) NOT NULL,
  `rid` int(11) NOT NULL,
  `created` datetime DEFAULT NULL,
  PRIMARY KEY (`uid`,`rid`),
  KEY `rid` (`rid`),
  CONSTRAINT `mealplan_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`),
  CONSTRAINT `mealplan_ibfk_2` FOREIGN KEY (`rid`) REFERENCES `recipe` (`rid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE diagnosis(
	diagnosisid varchar(10),
	diagnosisname varchar(300),
	PRIMARY KEY(diagnosisid)

);

CREATE TABLE drug(
	drugid varchar(10),
	proprietray varchar(300),
	nproprietary varchar(300),
	dosage varchar(50),
	routename varchar(50),
	PRIMARY KEY(drugid)
);


CREATE TABLE `medicalprofile` (
  `uid` int(11) NOT NULL,
  `mcid` varchar(10) NOT NULL,
  PRIMARY KEY (`uid`,`mcid`),
  KEY `mcid` (`mcid`),
  CONSTRAINT `medicalprofile_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`),
  CONSTRAINT `medicalprofile_ibfk_2` FOREIGN KEY (`mcid`) REFERENCES `diagnosis` (`diagnosisid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE `dietaryprofile` (
  `uid` int(11) NOT NULL,
  `did` int(11) NOT NULL,
  PRIMARY KEY (`uid`,`did`),
  KEY `did` (`did`),
  CONSTRAINT `dietaryprofile_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`),
  CONSTRAINT `dietaryprofile_ibfk_2` FOREIGN KEY (`did`) REFERENCES `dietarypref` (`did`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

# --- !Downs

DROP TABLE user;

DROP TABLE kitchen;

DROP TABLE dietarypref;

DROP TABLE recipe;

DROP TABLE profile;

DROP TABLE stores;

DROP TABLE mealplan;

DROP TABLE drug;

DROP TABLE diagnosis;

DROP TABLE medicalprofile;

DROP TABLE dietaryprofile;

CREATE TABLE `tweets` (
  `id` varchar(255) NOT NULL,
  `createdAt` datetime DEFAULT NULL,
  `text` varchar(255) DEFAULT NULL,
  `userId` varchar(255) DEFAULT NULL,
  `isRetweet` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `profiles` (
  `userId` varchar(255) NOT NULL DEFAULT '',
  `description` varchar(5000) DEFAULT NULL,
  `friendsCount` int(11) DEFAULT NULL,
  `followersCount` int(11) DEFAULT NULL,
  `screenName` varchar(255) DEFAULT NULL,
  `statusesCount` int(11) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
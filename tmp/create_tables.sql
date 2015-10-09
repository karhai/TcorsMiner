CREATE TABLE `tweets` (
  `id` varchar(190) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `createdAt` datetime DEFAULT NULL,
  `text` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `userId` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `isRetweet` tinyint(1) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `twitter_profiles` (
  `userId` varchar(190) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `description` varchar(5000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `friendsCount` int(11) DEFAULT NULL,
  `followersCount` int(11) DEFAULT NULL,
  `screenName` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `statusesCount` int(11) DEFAULT NULL,
  `location` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `instagram` (
  `id` varchar(190) NOT NULL,
  `createdTime` datetime DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `caption` varchar(2500) DEFAULT NULL,
  `likes` int(11) DEFAULT NULL,
  `comments` int(11) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `storePicture` int(11) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `instagram_comments` (
  `id` varchar(190) NOT NULL DEFAULT '',
  `parent_id` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `comment` varchar(2500) DEFAULT NULL,
  `createdTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `instagram_terms` (
  `search_term` varchar(190) NOT NULL DEFAULT '',
  `min_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`search_term`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `instagram_users` (
  `id` varchar(190) NOT NULL,
  `fullname` varchar(255) DEFAULT NULL,
  `bio` varchar(2500) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `follows` int(11) DEFAULT NULL,
  `followedBy` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
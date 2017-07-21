CREATE TABLE `tweets` (
  `id` varchar(190) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `createdAt` datetime DEFAULT NULL,
  `text` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `userId` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `isRetweet` tinyint(1) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `place_country` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `place_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `place_type` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_tweets_userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `twitter_profiles` (
  `userId` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `description` varchar(5000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `friendsCount` int(11) DEFAULT NULL,
  `followersCount` int(11) DEFAULT NULL,
  `screenname` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `statusesCount` int(11) DEFAULT NULL,
  `location` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`userId`),
  KEY `idx_twitter_profiles_screenname` (`screenname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `instagram` (
  `id` varchar(190) COLLATE utf8mb4_unicode_ci NOT NULL,
  `createdTime` datetime DEFAULT NULL,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `caption` varchar(2500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `likes` int(11) DEFAULT NULL,
  `comments` int(11) DEFAULT NULL,
  `url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `location` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `storePicture` int(11) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_instagram_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `instagram_comments` (
  `id` varchar(190) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `parent_id` varchar(190) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `username` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `comment` varchar(2500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createdTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_instagram_comments_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `instagram_terms` (
  `search_term` varchar(190) NOT NULL DEFAULT '',
  `min_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`search_term`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `instagram_users` (
  `id` varchar(190) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fullname` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bio` varchar(2500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `follows` int(11) DEFAULT NULL,
  `followedBy` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_instagram_users_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
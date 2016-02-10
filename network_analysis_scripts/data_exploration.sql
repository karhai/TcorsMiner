# explore tweets by time (eg, hour)
SELECT YEAR(createdAt) AS Y, MONTH(createdAt) AS M, DAY(createdAt) AS D, HOUR(createdAt) AS H, COUNT(*) AS total
FROM tweets
WHERE createdAt > '2016-01-26 03:20:00'
AND createdAt < '2015-05-14 16:00:00'
GROUP BY Y,M,D,H

# explore users
SELECT *
FROM twitter_profiles
WHERE screenName LIKE "%militia%"

# explore GPS info
SELECT *
FROM tweets
WHERE latitude > 0
ORDER BY createdAt DESC
################################################################################
#
# explore by terms, users (edge list), for export
#
# this script serves multiple general purposes:
# - for retweet network analysis, joins the user who is retweeting with the original poster
# - for content analysis, include text from tweet, user profile information
# - to allow for export into other software, line breaks are stripped for easier access
# - to allow for export into network software as edge list of user ties
#
# input:
# - search terms
# - time frame
#
################################################################################

SELECT t.createdAt, REPLACE(REPLACE(t.text,'\n',' '),'\r',' ') AS text, p.screenName, REPLACE(REPLACE(description,'\n',''),'\r','') as description, p.friendsCount, p.followersCount, p.statusesCount, p.location, p.name, SUBSTRING_INDEX(SUBSTRING(t.text,INSTR(t.text,"@")+1),":",1) AS retweet_source
FROM tweets t JOIN twitter_profiles p ON tweets.userId = twitter_profiles.userId
WHERE createdAt > "2015-10-01 00:00:00"
AND createdAt < "2015-11-01 00:00:00"
AND (text REGEXP '\\#ecig[[:>:]]' 
OR text REGEXP '\\#ejuice[[:>:]]'
OR text REGEXP '\\#eliquid[[:>:]]'
OR text REGEXP '\\#vape[[:>:]]'
OR text REGEXP '\\#vaping[[:>:]]'
OR text REGEXP '\\#vapelife[[:>:]]')
AND NOT (text LIKE "%blu%" AND text LIKE "%ray%")
AND isRetweet = 1
ORDER BY createdAt ASC
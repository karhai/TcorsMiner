# Jennifer query, using any order
SELECT *
FROM tweets
WHERE (text REGEXP "secondhand" AND text REGEXP "vape")
OR (text REGEXP "secondhand" AND text REGEXP "vaping")
OR (text REGEXP "second-hand" AND text REGEXP "vape")
OR (text REGEXP "second-hand" AND text REGEXP "vaping")
OR (text REGEXP "vape" AND text REGEXP "smoke")
OR (text REGEXP "ecig" AND text REGEXP "smoke")
OR (text REGEXP "e-cig" AND text REGEXP "smoke")
OR (text REGEXP "e-cigarette" AND text REGEXP "smoke")
OR (text REGEXP "vape" AND text REGEXP "shs") 
OR (text REGEXP "ecig" AND text REGEXP "shs") 
OR (text REGEXP "vape" AND text REGEXP "secondhand" AND text REGEXP "smoke")
OR (text REGEXP "vape" AND text REGEXP "second-hand" AND text REGEXP "smoke")
OR text REGEXP "esmoke"
OR text REGEXP "e-smoke"

# Jennifer query, using strict order
SELECT *
FROM tweets
WHERE text REGEXP "secondhand vape" 
OR text REGEXP "secondhand vaping" 
OR text REGEXP "second-hand vape" 
OR text REGEXP "second-hand vaping" 
OR text REGEXP "vape smoke" 
OR text REGEXP "ecig smoke" 
OR text REGEXP "e-cig smoke" 
OR text REGEXP "e-cigarette smoke"
OR text REGEXP "vape shs" 
OR text REGEXP "ecig shs" 
OR text REGEXP "vape secondhand smoke" 
OR text REGEXP "vape second-hand smoke"  
OR text REGEXP "esmoke" 
OR text REGEXP "e-smoke"

# Combine Jennifer query with profiles, clean newlines
SELECT id, createdAt, REPLACE(REPLACE(text,'\n',' '),'\r',' ') AS text, isRetweet, p.userid, REPLACE(REPLACE(p.description,'\n',' '),'\r',' '), p.followersCount, p.friendsCount, p.location, p.name, p.screenName, p.statusesCount
FROM tweets t, profiles p
WHERE (text REGEXP "secondhand vape" 
OR text REGEXP "secondhand vaping" 
OR text REGEXP "second-hand vape" 
OR text REGEXP "second-hand vaping" 
OR text REGEXP "vape smoke" 
OR text REGEXP "ecig smoke" 
OR text REGEXP "e-cig smoke" 
OR text REGEXP "e-cigarette smoke"
OR text REGEXP "vape shs" 
OR text REGEXP "ecig shs" 
OR text REGEXP "vape secondhand smoke" 
OR text REGEXP "vape second-hand smoke"  
OR text REGEXP "esmoke" 
OR text REGEXP "e-smoke")
AND t.userid = p.userid
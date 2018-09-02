-- ==========================================================================================================================================
-- ==========================================================================================================================================

/*
* Dev			TcorsTwitter	2017-07-20
* Test			TcorsTwitter	2017-07-20
* Prod			TcorsTwitter	2017-07-20
*/


-- create database TcorsTwitter
use TcorsTwitter;

-- ==============
-- Keywords table
-- ==============
CREATE TABLE `tcorstwitter`.`twitter_keywords` (
  `keyword_id` INT NOT NULL AUTO_INCREMENT,
  `keyword` VARCHAR(100) NOT NULL,
  `enabled` INT NOT NULL DEFAULT 1,
  `add_notes` VARCHAR(500) NULL,
  `creation_date` DATETIME NOT NULL,
  `created_by` VARCHAR(128) NOT NULL,
  `disabled_notes` VARCHAR(500) NULL,
  `disabled_date` DATETIME NULL,
  `disabled_by` VARCHAR(128) NULL,
  PRIMARY KEY (`keyword_id`));

/*
--('e-cig', 1, 'generic terms -- added 3/24/2015', Now(), '0') 
*/

-- Insert the keywords
INSERT INTO `tcorstwitter`.`twitter_keywords`
(`keyword`,
`enabled`,
`add_notes`,
`creation_date`,
`created_by`)
VALUES
('ecig', 1, 'generic terms -- added 3/24/2015', Now(), '0')
,('e-cigs', 1, 'generic terms -- added 3/24/2015', Now(), '0')
,('ecigs', 1, 'generic terms -- added 3/24/2015', Now(), '0')
,('e-cigarette', 1, 'generic terms -- added 3/24/2015', Now(), '0')
,('ecigarette', 1, 'generic terms -- added 3/24/2015', Now(), '0')
,('e-cigarettes', 1, 'generic terms -- added 3/24/2015', Now(), '0')
,('ecigarettes', 1, 'generic terms -- added 3/24/2015', Now(), '0')
,('vape', 1, 'generic terms -- added 3/24/2015', Now(), '0')
,('vaper', 1, 'generic terms -- added 3/24/2015', Now(), '0')
,('vaping', 1, 'generic terms -- added 3/24/2015', Now(), '0')
,('vapes', 1, 'generic terms -- added 3/24/2015', Now(), '0')
,('vapers', 1, 'generic terms -- added 3/24/2015', Now(), '0')
,('nicotine', 1, 'generic terms -- added 3/24/2015', Now(), '0')
,('tobacco', 1, 'generic terms -- added 3/24/2015', Now(), '0')
,('cigarette', 1, 'generic terms -- added 3/24/2015', Now(), '0')
,('cigarettes', 1, 'generic terms -- added 3/24/2015', Now(), '0')
,('cigar', 1, 'generic terms -- added 3/24/2015', Now(), '0')
,('atomizer', 1, 'generic terms -- added 3/24/2015', Now(), '0')
,('atomizers', 1, 'generic terms -- added 3/24/2015', Now(), '0')
,('cartomizer', 1, 'generic terms -- added 3/24/2015', Now(), '0')
,('cartomizers', 1, 'generic terms -- added 3/24/2015', Now(), '0')
,('ehookah', 1, 'generic terms -- added 3/24/2015', Now(), '0')
,('e-hookah', 1, 'generic terms -- added 3/24/2015', Now(), '0')
,('ejuice', 1, 'generic terms -- added 3/24/2015', Now(), '0')
,('ejuices', 1, 'generic terms -- added 3/24/2015', Now(), '0')
,('e-juice', 1, 'generic terms -- added 3/24/2015', Now(), '0')
,('e-juices', 1, 'generic terms -- added 3/24/2015', Now(), '0')
,('eliquid', 1, 'generic terms -- added 3/24/2015', Now(), '0')
,('eliquids', 1, 'generic terms -- added 3/24/2015', Now(), '0')
,('e-liquid', 1, 'generic terms -- added 3/24/2015', Now(), '0')
,('e-liquids', 1, 'generic terms -- added 3/24/2015', Now(), '0')
,('blu', 1, 'brands -- added 3/24/2015', Now(), '0')
,('njoy', 1, 'brands -- added 3/24/2015', Now(), '0')
,('green smoke', 1, 'brands -- added 3/24/2015', Now(), '0')
,('south beach smoke', 1, 'brands -- added 3/24/2015', Now(), '0')
,('eversmoke', 1, 'brands -- added 3/24/2015', Now(), '0')
,('joye 510', 1, 'brands -- added 3/24/2015', Now(), '0')
,('joye510', 1, 'brands -- added 3/24/2015', Now(), '0')
,('joyetech', 1, 'brands -- added 3/24/2015', Now(), '0')
,('lavatube', 1, 'brands -- added 3/24/2015', Now(), '0')
,('lavatubes', 1, 'brands -- added 3/24/2015', Now(), '0')
,('logicecig', 1, 'brands -- added 3/24/2015', Now(), '0')
,('logicecigs', 1, 'brands -- added 3/24/2015', Now(), '0')
,('smartsmoker', 1, 'brands -- added 3/24/2015', Now(), '0')
,('smokestiks', 1, 'brands -- added 3/24/2015', Now(), '0')
,('v2 cig', 1, 'brands -- added 3/24/2015', Now(), '0')
,('v2 cigs', 1, 'brands -- added 3/24/2015', Now(), '0')
,('v2cigs', 1, 'brands -- added 3/24/2015', Now(), '0')
,('v2cig', 1, 'brands -- added 3/24/2015', Now(), '0')
,('mistic', 1, 'brands -- added 3/24/2015', Now(), '0')
,('21st century smoke', 1, 'brands -- added 3/24/2015', Now(), '0')
,('logic black label', 1, 'brands -- added 3/24/2015', Now(), '0')
,('finiti', 1, 'brands -- added 3/24/2015', Now(), '0')
,('nicotek', 1, 'brands -- added 3/24/2015', Now(), '0')
,('cigirex', 1, 'brands -- added 3/24/2015', Now(), '0')
,('logic platinum', 1, 'brands -- added 3/24/2015', Now(), '0')
,('cigalectric', 1, 'brands -- added 3/24/2015', Now(), '0')
,('xhale o2', 1, 'brands -- added 3/24/2015', Now(), '0')
,('cig2o', 1, 'brands -- added 3/24/2015', Now(), '0')
,('green smart living', 1, 'brands -- added 3/24/2015', Now(), '0')
,('krave', 1, 'brands -- added 3/24/2015', Now(), '0')
,('secondhand vape', 1, 'second hand vape terms -- added 3/24/2015', Now(), '0')
,('secondhand vaping', 1, 'second hand vape terms -- added 3/24/2015', Now(), '0')
,('second-hand vape', 1, 'second hand vape terms -- added 3/24/2015', Now(), '0')
,('second-hand vaping', 1, 'second hand vape terms -- added 3/24/2015', Now(), '0')
,('vape smoke', 1, 'second hand vape terms -- added 3/24/2015', Now(), '0')
,('ecig smoke', 1, 'second hand vape terms -- added 3/24/2015', Now(), '0')
,('e-cig smoke', 1, 'second hand vape terms -- added 3/24/2015', Now(), '0')
,('e-cigarette smoke', 1, 'second hand vape terms -- added 3/24/2015', Now(), '0')
,('vape shs', 1, 'second hand vape terms -- added 3/24/2015', Now(), '0')
,('ecig shs', 1, 'second hand vape terms -- added 3/24/2015', Now(), '0')
,('vape secondhand smoke', 1, 'second hand vape terms -- added 3/24/2015', Now(), '0')
,('vape second-hand smoke', 1, 'second hand vape terms -- added 3/24/2015', Now(), '0')
,('esmoke', 1, 'second hand vape terms -- added 3/24/2015', Now(), '0')
,('e-smoke', 1, 'second hand vape terms -- added 3/24/2015', Now(), '0')
,('stillblowingsmoke', 1, 'blowing smoke campaign -- added 4/19/2015', Now(), '0')
,('still blowing smoke', 1, 'blowing smoke campaign -- added 4/19/2015', Now(), '0')
,('notblowingsmoke', 1, 'blowing smoke campaign -- added 4/19/2015', Now(), '0')
,('not blowing smoke', 1, 'blowing smoke campaign -- added 4/19/2015', Now(), '0')
,('capublichealth', 1, 'blowing smoke campaign -- added 4/19/2015', Now(), '0')
,('tobaccofreekids', 1, 'tobacco free kids -- added 4/30/2015', Now(), '0')
,('notareplacement', 1, 'tobacco free kids -- added 4/30/2015', Now(), '0')
,('trulyfree', 1, 'truly free campaign -- added 5/29/2015', Now(), '0')
,('truly free', 1, 'truly free campaign -- added 5/29/2015', Now(), '0')
,('sb140', 1, 'california laws -- added 5/30/2015', Now(), '0')
,('sb 140', 1, 'california laws -- added 5/30/2015', Now(), '0')
,('sb24', 1, 'california laws -- added 5/30/2015', Now(), '0')
,('sb 24', 1, 'california laws -- added 5/30/2015', Now(), '0')
,('cherry tip cigarillos', 1, 'swisher terms -- added 6/16/2015', Now(), '0')
,('mini-cigarillos', 1, 'swisher terms -- added 6/16/2015', Now(), '0')
,('tip cigarillos', 1, 'swisher terms -- added 6/16/2015', Now(), '0')
,('king edward cigars', 1, 'swisher terms -- added 6/16/2015', Now(), '0')
,('royal gold cigars', 1, 'swisher terms -- added 6/16/2015', Now(), '0')
,('sweet coronella', 1, 'swisher terms -- added 6/16/2015', Now(), '0')
,('swisher blk', 0, 'removed and added swisher', Now(), '0')
,('swisher sweets', 0, 'removed and added swisher', Now(), '0')
,('vapercon', 1, 'vaporcon -- added 6/19/2015', Now(), '0')
,('vapercon west', 1, 'vaporcon -- added 6/19/2015', Now(), '0')
,('grimmgreen', 1, 'vaporcon -- added 6/19/2015', Now(), '0')
,('vapor', 1, 'vaporcon -- added 6/19/2015', Now(), '0')
,('electronic cigarette', 1, 'vaporcon -- added 6/19/2015', Now(), '0')
,('vape meet', 1, 'vaporcon -- added 6/19/2015', Now(), '0')
,('EcigsSaveLive', 1, 'random terms -- added 7/13/2015', Now(), '0')
,('EcigsSaveLives', 1, 'random terms -- added 7/13/2015', Now(), '0')
,('EcigsSavesLives', 1, 'random terms -- added 7/13/2015', Now(), '0')
,('vapecon', 1, 'vapecon -- added 10/8/2015', Now(), '0')
,('fresh empire', 1, 'added 10/14/2015', Now(), '0')
,('freshempire', 1, 'added 10/14/2015', Now(), '0')
,('camel crush bold', 1, 'added 10/14/2015', Now(), '0')
,('camelcrushbold', 1, 'added 10/14/2015', Now(), '0')
,('menthol', 1, 'survey in study 3 -- added 1/26/2016', Now(), '0')
,('clove', 1, 'survey in study 3 -- added 1/26/2016', Now(), '0')
,('hookah', 1, 'survey in study 3 -- added 1/26/2016', Now(), '0')
,('cigarillo', 1, 'survey in study 3 -- added 1/26/2016', Now(), '0')
,('blunt', 1, 'jon request, additional survey terms -- added 1/27/2016', Now(), '0')
,('vaporcade', 1, 'jon request, additional survey terms -- added 1/27/2016', Now(), '0')
,('narguile', 1, 'jon request, hookah study -- added 2/18/2016', Now(), '0')
,('shisha', 1, 'jon request, hookah study -- added 2/18/2016', Now(), '0')
,('marlboro', 1, 'companies being studied by S1 -- added 3/5/2016', Now(), '0')
,('vuse', 1, 'companies being studied by S1 -- added 3/5/2016', Now(), '0')
,('swisher', 1, 'companies being studied by S1 -- added 3/5/2016', Now(), '0')
,('black and mild', 1, 'companies being studied by S1 -- added 3/5/2016', Now(), '0')
,('copenhagen', 1, 'companies being studied by S1 -- added 3/5/2016', Now(), '0')
,('camel', 1, 'companies being studied by S1 -- added 3/5/2016', Now(), '0')
,('snus', 1, 'companies being studied by S1 -- added 3/5/2016', Now(), '0')
,('pall mall', 1, 'companies being studied by S1 -- added 3/5/2016', Now(), '0')
,('newport', 1, 'companies being studied by S1 -- added 3/5/2016', Now(), '0')
,('wakeup', 1, 'new campaign -- added 4/1/2016', Now(), '0')
,('cheerupbigtobacco', 1, 'new campaign (more) -- added 4/4/2016', Now(), '0')
,('transformtobacco', 1, 'patricia request -- added 4/11/2016', Now(), '0')
,('swishersweets', 1, 'tess request -- added 4/26/2016', Now(), '0')
,('swisherartistproject', 1, 'tess request -- added 4/26/2016', Now(), '0')
,('swisherartistgrant', 1, 'tess request -- added 4/26/2016', Now(), '0')
,('swisheratl', 1, 'tess request -- added 4/26/2016', Now(), '0')
,('blunation', 1, 'tess request -- added 4/26/2016', Now(), '0')
,('blucigs', 1, 'tess request -- added 4/26/2016', Now(), '0')
,('justyouandblu', 1, 'tess request -- added 4/26/2016', Now(), '0')
,('plusworks', 1, 'tess request -- added 4/26/2016', Now(), '0')
,('thisfreelife', 1, 'jennifer request -- added 5/2/2016', Now(), '0')
,('swishersweeties', 1, 'more swisher -- added 5/6/2016', Now(), '0')
,('swishermusiccity', 1, 'more swisher -- added 5/6/2016', Now(), '0')
,('tobacco21', 1, 'new laws -- added 5/6/2016', Now(), '0')
,('sbx27', 1, 'new laws -- added 5/6/2016', Now(), '0')
,('sbx25', 1, 'new laws -- added 5/6/2016', Now(), '0')
,('FDAdeeming', 1, 'new laws -- added 5/6/2016', Now(), '0')
,('FDAtobacco', 1, 'new laws -- added 5/6/2016', Now(), '0')
,('prop56', 1, 'new laws -- added 5/6/2016', Now(), '0')
,('prop64', 1, 'new laws -- added 5/6/2016', Now(), '0')
,('juul', 1, 'new terms -- added 4/10/2017', Now(), '0')
,('smokeless', 1, 'new terms -- added 4/10/2017', Now(), '0')
,('ryo', 1, 'new terms -- added 4/10/2017', Now(), '0')
,('rollyourown', 1, 'added 5/11/2017juuling', Now(), '0');

select * from twitter_keywords;
/*drop table twitter_keywords;*/

-- Insert the keywords
INSERT INTO `tcorstwitter`.`twitter_keywords`
(`keyword`,
`enabled`,
`add_notes`,
`creation_date`,
`created_by`)
VALUES
('7000chemicals', 1, 'From the current FDA campaigns', Now(), '0')
,('therealcost', 1, 'From the current FDA campaigns', Now(), '0')
,('tobaccofreelife', 1, 'From the current FDA campaigns', Now(), '0')
,('freshevents', 1, 'From the current FDA campaigns', Now(), '0')
,('everytrycounts', 1, 'New FDA campaign that launches in January 2018', Now(), '0');


-- ==========================================================================================================================================
-- ==========================================================================================================================================

/*
* Dev			TcorsTwitter	2018-02-01
* Test			TcorsTwitter	2018-02-01
* Prod			TcorsTwitter	2018-02-01 Ryan already extended the text column to 300
*/

-- Extend the text column size to store 280 character tweets
-- Some texts are 301 char long, extend it to 350

USE tcorstwitter;

select text, max(length(text)) from tweets;

USE tcorstwitter;

ALTER TABLE tweets MODIFY text varchar(350);

-- ==========================================================================================================================================
-- ==========================================================================================================================================

/*
* Dev			TcorsTwitter	2018-02-04
* Test			TcorsTwitter	2018-02-04
* Prod			TcorsTwitter	2018-02-05
*/

-- I have found 688 long tweet text:
/*
RT @fhay_cotton_h: ใครสายกุ้งต้องมาโดนร้านนี้นะ Vapor ในหมู่บ้านนิชดา กุ้งเผาตัวโตๆ มันเยิ้มๆ เสิร์ฟพร้อมข้าวสวยอุ่น ตัดมันกุ้ง และน้ำปลาราดคือฟิน รสชาติน้ำจิ้มซีฟู้ดก็กลมกล่อม แถมแกะเนื้อก้ามกุ้งให้อีกด้วย ราคาประมาณ 200/1ขีด ในภาพ 5 ขีดฮะ #อร่อยไปแดก https://t.co/p6WOAFXIcG
*/

-- 463 long text
/*
Storing tweet
id = 960319102089768960 length = 18
createdAt = 2018-02-04 17:08:10.0
originalText =  length = 0
this_tweets_text = @AwkwardBunny26 @TrevorO97466582 @BeautifulDayMom @RiggyWoo @Eddiebr88701857 @KristinFaller11 @ZenitForward @axlsnakeoil @cavanagh_kyle @FAlanSlack4lyfe @RealAmBEAR @JesTheBelle @hikingwithjulie @DolevBroughton @CorneliusGegan @bunnyknows @ZeithBear @Koncept187 @vexed_llama My bro and I ate some when they were really strong and smoked a blunt before the avengers movie. About 20 min we started to fall asleep haha, left the movie and took a six hour midday nap. length = 463
textToStore = @AwkwardBunny26 @TrevorO97466582 @BeautifulDayMom @RiggyWoo @Eddiebr88701857 @KristinFaller11 @ZenitForward @axlsnakeoil @cavanagh_kyle @FAlanSlack4lyfe @RealAmBEAR @JesTheBelle @hikingwithjulie @DolevBroughton @CorneliusGegan @bunnyknows @ZeithBear @Koncept187 @vexed_llama My bro and I ate some when they were really strong and smoked a blunt before the avengers movie. About 20 min we started to fall asleep haha, left the movie and took a six hour midday nap. length = 463
userId = 954552465978023936 length = 18
isRetweet = false
latitude = 0.0
longitude = 0.0
place_country =  length = 0
place_name =  length = 0
place_type =  length = 0
*/

USE tcorstwitter;
ALTER TABLE tweets MODIFY text varchar(1024);


-- ==========================================================================================================================================
-- ==========================================================================================================================================

/*
* Dev			TcorsTwitter	Deployed 2018-02-21
* Test			TcorsTwitter	Deployed 2018-02-27
* Prod			TcorsTwitter	Deployed 2018-02-27
*/



-- Add enabled info to the `tcorstwitter`.`twitter_keywords` table

USE tcorstwitter;

-- Delete unused columns
ALTER TABLE twitter_keywords DROP disabled_notes;
ALTER TABLE twitter_keywords DROP disabled_date;
ALTER TABLE twitter_keywords DROP disabled_by;

-- Add new columns
ALTER TABLE twitter_keywords ADD notes VARCHAR(2048) NULL;
ALTER TABLE twitter_keywords ADD change_date DATETIME NULL;
ALTER TABLE twitter_keywords ADD changed_by VARCHAR(128) NULL;
ALTER TABLE twitter_keywords ADD change_action VARCHAR(50) NULL;
ALTER TABLE twitter_keywords ADD change_notes VARCHAR(500) NULL;


-- Copy the add notes to the notes column
UPDATE twitter_keywords SET notes = add_notes WHERE 0 < keyword_id; 

SELECT * FROM twitter_keywords;

-- Populate the creation date
USE tcorstwitter;

START TRANSACTION;
	UPDATE twitter_keywords SET creation_date = '2015-03-24' WHERE keyword_id > 0 AND add_notes like '%3/24/2015';
	UPDATE twitter_keywords SET creation_date = '2015-04-19' WHERE keyword_id > 0 AND add_notes like '%4/19/2015';
	UPDATE twitter_keywords SET creation_date = '2015-04-30' WHERE keyword_id > 0 AND add_notes like '%4/30/2015';
	UPDATE twitter_keywords SET creation_date = '2015-05-29' WHERE keyword_id > 0 AND add_notes like '%5/29/2015';
	UPDATE twitter_keywords SET creation_date = '2015-05-30' WHERE keyword_id > 0 AND add_notes like '%5/30/2015';
	UPDATE twitter_keywords SET creation_date = '2015-06-16' WHERE keyword_id > 0 AND add_notes like '%6/16/2015';
	UPDATE twitter_keywords SET creation_date = '2015-06-16' WHERE keyword_id > 0 AND keyword in ('swisher blk','swisher sweets');
	UPDATE twitter_keywords SET creation_date = '2015-06-19' WHERE keyword_id > 0 AND add_notes like '%6/19/2015';
	UPDATE twitter_keywords SET creation_date = '2015-07-13' WHERE keyword_id > 0 AND add_notes like '%7/13/2015';
	UPDATE twitter_keywords SET creation_date = '2015-10-08' WHERE keyword_id > 0 AND add_notes like '%10/8/2015';
	UPDATE twitter_keywords SET creation_date = '2015-10-14' WHERE keyword_id > 0 AND add_notes like '%10/14/2015';
	UPDATE twitter_keywords SET creation_date = '2016-01-26' WHERE keyword_id > 0 AND add_notes like '%1/26/2016';
	UPDATE twitter_keywords SET creation_date = '2016-01-27' WHERE keyword_id > 0 AND add_notes like '%1/27/2016';
	UPDATE twitter_keywords SET creation_date = '2016-02-18' WHERE keyword_id > 0 AND add_notes like '%2/18/2016';
	UPDATE twitter_keywords SET creation_date = '2016-03-05' WHERE keyword_id > 0 AND add_notes like '%3/5/2016';
	UPDATE twitter_keywords SET creation_date = '2016-04-01' WHERE keyword_id > 0 AND add_notes like '%4/1/2016';
	UPDATE twitter_keywords SET creation_date = '2016-04-04' WHERE keyword_id > 0 AND add_notes like '%4/4/2016';
	UPDATE twitter_keywords SET creation_date = '2016-04-11' WHERE keyword_id > 0 AND add_notes like '%4/11/2016';
	UPDATE twitter_keywords SET creation_date = '2016-04-26' WHERE keyword_id > 0 AND add_notes like '%4/26/2016';
	UPDATE twitter_keywords SET creation_date = '2016-05-02' WHERE keyword_id > 0 AND add_notes like '%5/2/2016';
	UPDATE twitter_keywords SET creation_date = '2016-05-06' WHERE keyword_id > 0 AND add_notes like '%5/6/2016';
	UPDATE twitter_keywords SET creation_date = '2017-04-10' WHERE keyword_id > 0 AND add_notes like '%4/10/2017';
	UPDATE twitter_keywords SET creation_date = '2017-05-11' WHERE keyword_id > 0 AND add_notes like '%5/11/2017%';
	UPDATE twitter_keywords SET creation_date = '2017-11-09' WHERE keyword_id > 0 AND add_notes like '%11/09/2017';

	SELECT * FROM twitter_keywords;
-- ROLLBACK;
-- COMMIT;

-- ==========================================================================================================================================
-- ==========================================================================================================================================

/*
* Dev			TcorsTwitter	Deployed 2018-03-04
* Test			TcorsTwitter	Deployed 2018-03-04
* Prod			TcorsTwitter	Deployed 2018-03-04
*/

-- Add indexes to the twitter_profile table

USE tcorstwitter;
CREATE INDEX `idx_twitter_profiles_statusesCount` ON `twitter_profiles` (`statusesCount`);
CREATE INDEX `idx_twitter_profiles_followersCount` ON `twitter_profiles` (`followersCount`);
CREATE INDEX `idx_twitter_profiles_friendsCount` ON `twitter_profiles` (`friendsCount`);

use tcorstwitter;
select * from tweets order by createdAt desc limit 5;
select count(twitter_profilestwitter_profiles1) from tweets;
select count(*) from twitter_profiles;
select * from twitter_profiles order by statusesCount desc limit 5;

-- ==========================================================================================================================================
-- ==========================================================================================================================================

-- NOT NEEDED
-- The Twitter stream runs in a loop, cannot check for tasks to execute

/*
* Dev			TcorsTwitter	Deployed 2018-03-04
* Test			TcorsTwitter	Deployed 2018-03-04
* Prod			TcorsTwitter	Deployed 2018-03-04
*/

/*
-- Add task tableto control the Java application from the web application

USE tcorstwitter;

CREATE TABLE `tcorstwitter`.`valid_task_type` (
  `task_type_id` INT NOT NULL,
  `name` VARCHAR(45) NULL,
  PRIMARY KEY (`task_type_id`));

CREATE TABLE `tcorstwitter`.`task` (
  `task_id` INT NOT NULL,
  `task_type_id` INT NULL,
  `request_date` DATETIME NULL,
  `requested_by` VARCHAR(45) NULL,
  `execute_after` DATETIME NULL,
  `completed_date` DATETIME NULL,
  PRIMARY KEY (`task_id`));

ALTER TABLE `tcorstwitter`.`task` 
ADD INDEX `task_type_idx` (`task_type_id` ASC);
ALTER TABLE `tcorstwitter`.`task` 
ADD CONSTRAINT `task_type`
  FOREIGN KEY (`task_type_id`)
  REFERENCES `tcorstwitter`.`valid_task_type` (`task_type_id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

*/

-- Add After Insert trigger to save the latest tweets in a reporting table for fast streaming of the last tweets

-- Create the tweets reporting table

use tcorstwitter;

-- DROP TABLE reporting_tweets;

CREATE TABLE `reporting_tweets` (
  `id` varchar(190) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `createdAt` datetime DEFAULT NULL,
  `text` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `userId` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `isRetweet` tinyint(1) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `place_country` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `place_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `place_type` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- -------------------------


-- DROP TRIGGER tweets_after_insert;


use tcorstwitter;

-- Use the DELIMITER to allow the execution of the inner and outer commands with "under cursor"
DELIMITER //

CREATE TRIGGER tweets_after_insert
AFTER INSERT
   ON tweets FOR EACH ROW

BEGIN

set @max_row_count = 10;
select count(0) into @reporting_tweets_count  from reporting_tweets;

IF @reporting_tweets_count < @max_row_count THEN
	INSERT INTO `tcorstwitter`.`reporting_tweets`
	(`id`,
	`createdAt`,
	`text`,
	`userId`,
	`isRetweet`,
	`latitude`,
	`longitude`,
	`place_country`,
	`place_name`,
	`place_type`)
	VALUES
	(NEW.id,
	NEW.createdAt,
	NEW.text,
	NEW.userId,
	NEW.isRetweet,
	NEW.latitude,
	NEW.longitude,
	NEW.place_country,
	NEW.place_name,
	NEW.place_type);

END IF;
/*
-- Delete old rows from the reporting table, keep only the last 100
-- https://stackoverflow.com/questions/578867/sql-query-delete-all-records-from-the-table-except-latest-n/8303440#8303440
DELETE FROM `reporting_tweets`
  WHERE id <= (
    SELECT id
    FROM (
      SELECT id
      FROM `reporting_tweets`
      ORDER BY id DESC
      LIMIT 1 OFFSET 100 -- keep this many records
    ) my_alias
  );
*/
END; //

DELIMITER ;


-- ===================================================================
-- Testing the trigger

DELIMITER //

SET @mynum = '0054';

INSERT INTO `tcorstwitter`.`tweets`
(`id`,
`createdAt`,
`text`,
`userId`,
`isRetweet`,
`latitude`,
`longitude`,
`place_country`,
`place_name`,
`place_type`)
VALUES
(CONCAT('id',@mynum),
SYSDATE(),
CONCAT('text ',@mynum),
CONCAT('user id ',@mynum),
0,
0,
0,
CONCAT('country ',@mynum),
CONCAT('place ',@mynum),
CONCAT('place type ',@mynum));

 //

DELIMITER ;

select * from tweets order by createdAt desc limit 30;
select * from reporting_tweets order by createdAt desc;
-- delete from reporting_tweets;

set @max_row_count = 20;
select @reporting_tweets_count := count(0) from reporting_tweets;
select @reporting_tweets_count;


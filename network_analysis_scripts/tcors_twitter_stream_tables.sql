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
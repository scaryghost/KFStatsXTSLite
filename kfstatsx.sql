CREATE  TABLE `deaths` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NOT NULL ,
  `count` SMALLINT UNSIGNED NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) )
COMMENT = 'Stores the death count from each unique source';

CREATE  TABLE `records` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `steamID64` VARCHAR(45) NOT NULL ,
  `wins` SMALLINT UNSIGNED NOT NULL DEFAULT 0 ,
  `losses` SMALLINT UNSIGNED NOT NULL DEFAULT 0 ,
  `disconnects` SMALLINT UNSIGNED NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `steamID64_UNIQUE` (`steamID64` ASC) )
COMMENT = 'Players\' W/L/D records';

CREATE  TABLE `aggregate` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `stat` VARCHAR(45) NOT NULL ,
  `value` BIGINT UNSIGNED NOT NULL DEFAULT 0 ,
  `group` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `stat_group_UNIQUE` USING HASH (`stat` ASC, `group` ASC))
COMMENT = 'Stores the sum of each stat over all players';

CREATE  TABLE `difficulty` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NOT NULL ,
  `length` VARCHAR(45) NOT NULL ,
  `wins` SMALLINT UNSIGNED NOT NULL DEFAULT 0 ,
  `losses` SMALLINT UNSIGNED NOT NULL DEFAULT 0 ,
  `wave` INT UNSIGNED NOT NULL DEFAULT 0 ,
  `time` INT UNSIGNED NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `name_length_UNIQUE` USING HASH (`name` ASC, `length` ASC))
COMMENT = 'Stores information about each difficulty';

CREATE  TABLE `levels` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NOT NULL ,
  `wins` SMALLINT UNSIGNED NOT NULL DEFAULT 0 ,
  `losses` SMALLINT UNSIGNED NOT NULL DEFAULT 0 ,
  `time` INT UNSIGNED NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) );

CREATE  TABLE `player` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `steamID64` VARCHAR(45) NOT NULL ,
  `stat` VARCHAR(45) NOT NULL ,
  `value` INT UNSIGNED NOT NULL DEFAULT 0 ,
  `group` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `steamID64_stat_group_UNIQUE` USING HASH (`steamID64` ASC, `group` ASC, `stat` ASC)) 
COMMENT = 'Stores individual player statistics';

DROP procedure IF EXISTS `update_difficulty_and_level`;
DELIMITER $$
CREATE PROCEDURE `update_difficulty_and_level` 
    (diffname VARCHAR(45), length VARCHAR(45), levelname VARCHAR(45), 
wins SMALLINT UNSIGNED, losses SMALLINT UNSIGNED, wave INT UNSIGNED, time INT UNSIGNED)
BEGIN
INSERT INTO difficulty values (NULL, diffname, length, wins, losses, wave, time)
    ON DUPLICATE KEY UPDATE `wins`= `wins` + wins, `losses`= `losses` + losses, 
    `wave`= `wave` + wave, `time`= `time` + time;

INSERT INTO level values (NULL, levelname, wins, losses, time)
    ON DUPLICATE KEY UPDATE  `wins`= `wins` + wins, `losses`= `losses` + losses,
    `time`= `time` + time;
END$$
DELIMITER ;

DROP procedure IF EXISTS `update_player_aggregate`;
DELIMITER $$
CREATE PROCEDURE `update_player_aggregate` 
    (steamID64 VARCHAR(45), stat VARCHAR(45), offset INT UNSIGNED, statgroup VARCHAR(45))
BEGIN
INSERT INTO player values (steamID64, stat, offset, statgroup)
    ON DUPLICATE KEY UPDATE value= value + offset;

INSERT INTO aggregate values (stat, offset, statgroup)
    ON DUPLICATE KEY UPDATE value= value + offset;
END$$
DELIMITER ;

DROP procedure IF EXISTS `update_record`;
DELIMITER $$
CREATE PROCEDURE `update_record` 
    (steamID64 VARCHAR(45), wins SMALLINT UNSIGNED, losses SMALLINT UNSIGNED,
    disconnects SMALLINT UNSIGNED)
BEGIN
INSERT INTO records VALUES(steamID64, wins, losses, disconnects)
    ON DUPLICATE KEY UPDATE `wins`= `wins` + wins, `losses`= `losses` + losses,
    `disconnects`= `disconnects` + disconnects;
END$$
DELIMITER ;

DROP procedure IF EXISTS `update_deaths`;
DELIMITER $$
CREATE PROCEDURE `update_deaths` (name VARCHAR(45), offset SMALLINT UNSIGNED)
BEGIN
INSERT INTO deaths VALUES (name, count)
    ON DUPLICATE KEY UPDATE count= count + offset;

END$$
DELIMITER ;

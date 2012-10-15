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
COMMENT = 'Stores win, loss, and disconnect records for the players';

CREATE  TABLE `aggregate` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `stat` VARCHAR(45) NOT NULL ,
  `value` BIGINT UNSIGNED NOT NULL DEFAULT 0 ,
  `category` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `stat_category_UNIQUE` USING HASH (`stat` ASC, `category` ASC))
COMMENT = 'Stores the sum of each stat over all players';

CREATE  TABLE `difficulties` (
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
  `category` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `steamID64_category_stat_UNIQUE` USING HASH (`steamID64` ASC, `category` ASC, `stat` ASC)) 
COMMENT = 'Stores individual player statistics';

DELIMITER $$
CREATE PROCEDURE `update_difficulty_and_level` 
    (diffname VARCHAR(45), length VARCHAR(45), levelname VARCHAR(45), 
winDelta SMALLINT UNSIGNED, lossDelta SMALLINT UNSIGNED, waveDelta INT UNSIGNED, timeDelta INT UNSIGNED)
BEGIN
INSERT INTO difficulties values (NULL, diffname, length, winDelta, lossDelta, waveDelta, timeDelta)
    ON DUPLICATE KEY UPDATE wins= wins + winDelta, losses= losses + lossDelta, 
    wave= wave + waveDelta, time= time + timeDelta;

INSERT INTO levels values (NULL, levelname, winDelta, lossDelta, timeDelta)
    ON DUPLICATE KEY UPDATE  wins= wins + winDelta, losses= losses + lossDelta,
    time= time + timeDelta;
END$$

CREATE PROCEDURE `update_player_aggregate` 
    (steamID64 VARCHAR(45), stat VARCHAR(45), offset INT UNSIGNED, category VARCHAR(45))
BEGIN
INSERT INTO player values (NULL, steamID64, stat, offset, category)
    ON DUPLICATE KEY UPDATE value= value + offset;

INSERT INTO aggregate values (NULL, stat, offset, category)
    ON DUPLICATE KEY UPDATE value= value + offset;
END$$

CREATE PROCEDURE `update_record` 
    (steamID64 VARCHAR(45), winDelta SMALLINT UNSIGNED, lossDelta SMALLINT UNSIGNED,
    disconnectDelta SMALLINT UNSIGNED)
BEGIN
INSERT INTO records VALUES (NULL, steamID64, winDelta, lossDelta, disconnectDelta)
    ON DUPLICATE KEY UPDATE wins= wins + winDelta, losses= losses + lossDelta,
    disconnects= disconnects + disconnectDelta;
END$$

CREATE PROCEDURE `update_deaths` (name VARCHAR(45), offset SMALLINT UNSIGNED)
BEGIN
INSERT INTO deaths VALUES (NULL, name, offset)
    ON DUPLICATE KEY UPDATE count= count + offset;
END$$
DELIMITER ;

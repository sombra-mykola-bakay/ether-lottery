-- MySQL Script generated by MySQL Workbench
-- пт, 16-лис-2018 22:03:32 +0200
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema lottery
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema lottery
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `lottery` DEFAULT CHARACTER SET latin1 ;
USE `lottery` ;

-- -----------------------------------------------------
-- Table `lottery`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lottery`.`user` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(225) NULL DEFAULT NULL,
  `password` VARCHAR(225) NULL DEFAULT NULL,
  `role` VARCHAR(225) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 7
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `lottery`.`auction`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lottery`.`auction` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `creator_id` INT(11) NOT NULL,
  `address` VARCHAR(225) NOT NULL,
  `description` LONGTEXT NOT NULL,
  `state` VARCHAR(9) NOT NULL,
  `hash` VARCHAR(225) NOT NULL,
  `winner_index` VARCHAR(225) NOT NULL,
  `winner_id` INT(11) NULL DEFAULT NULL,
  `version` VARCHAR(45) NOT NULL,
  `min_payable_ether_amount` VARCHAR(225) NOT NULL,
  `expected_participants` INT(5) NOT NULL,
  `valid_till` VARCHAR(225) NULL DEFAULT NULL,
  `winner_message` VARCHAR(225) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `address_UNIQUE` (`address` ASC),
  INDEX `fk_auction_user_idx` (`creator_id` ASC),
  INDEX `fk_auction_winner_user_idx` (`winner_id` ASC),
  CONSTRAINT `fk_auction_creator`
    FOREIGN KEY (`creator_id`)
    REFERENCES `lottery`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_auction_winner_user`
    FOREIGN KEY (`winner_id`)
    REFERENCES `lottery`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 39
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `lottery`.`auction_participant`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lottery`.`auction_participant` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `auction_id` INT(11) NOT NULL,
  `user_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_user_idx` (`user_id` ASC),
  INDEX `fk_auction_idx` (`auction_id` ASC),
  CONSTRAINT `fk_auction_participant_auction`
    FOREIGN KEY (`auction_id`)
    REFERENCES `lottery`.`auction` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_auction_participant_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `lottery`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 29
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `lottery`.`wallet`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lottery`.`wallet` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NOT NULL,
  `wallet` BLOB NOT NULL,
  `address` VARCHAR(225) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_wallet_user` (`user_id` ASC),
  CONSTRAINT `fk_wallet_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `lottery`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 7
DEFAULT CHARACTER SET = latin1;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
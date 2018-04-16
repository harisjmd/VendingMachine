-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema vm
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `vm` ;

-- -----------------------------------------------------
-- Schema vm
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `vm` DEFAULT CHARACTER SET utf8 ;
USE `vm` ;

-- -----------------------------------------------------
-- Table `vm`.`Product_Category`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `vm`.`Product_Category` ;

CREATE TABLE IF NOT EXISTS `vm`.`Product_Category` (
  `category_id` INT NOT NULL AUTO_INCREMENT,
  `category_name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`category_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `vm`.`Products`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `vm`.`Products` ;

CREATE TABLE IF NOT EXISTS `vm`.`Products` (
  `product_id` INT NOT NULL AUTO_INCREMENT,
  `product_name` VARCHAR(45) NOT NULL,
  `product_category` INT NOT NULL,
  `product_price` DOUBLE NOT NULL,
  `product_weight_vol` DOUBLE NOT NULL,
  PRIMARY KEY (`product_id`),
  INDEX `fk_category_idx` (`product_category` ASC),
  CONSTRAINT `fk_category`
    FOREIGN KEY (`product_category`)
    REFERENCES `vm`.`Product_Category` (`category_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `vm`.`VendingMachine`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `vm`.`VendingMachine` ;

CREATE TABLE IF NOT EXISTS `vm`.`VendingMachine` (
  `vm_id` VARCHAR(100) NOT NULL,
  `vm_location` VARCHAR(45) NOT NULL,
  `operating` TINYINT NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`vm_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `vm`.`Storage`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `vm`.`Storage` ;

CREATE TABLE IF NOT EXISTS `vm`.`Storage` (
  `storage_id` VARCHAR(100) NOT NULL,
  `vm_id` VARCHAR(100) NOT NULL,
  `product_id` INT NOT NULL,
  `quantity` TINYINT NOT NULL,
  `capacity` TINYINT NOT NULL,
  PRIMARY KEY (`storage_id`, `vm_id`),
  INDEX `fk_vm_idx` (`vm_id` ASC),
  INDEX `fk_product_idx` (`product_id` ASC),
  CONSTRAINT `fk_vm`
    FOREIGN KEY (`vm_id`)
    REFERENCES `vm`.`VendingMachine` (`vm_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_product`
    FOREIGN KEY (`product_id`)
    REFERENCES `vm`.`Products` (`product_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `vm`.`User`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `vm`.`User` ;

CREATE TABLE IF NOT EXISTS `vm`.`User` (
  `user_id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(100) NOT NULL,
  `password` VARCHAR(20) NOT NULL,
  `user_privilege` CHAR(1) NOT NULL,
  PRIMARY KEY (`user_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `vm`.`Transaction`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `vm`.`Transaction` ;

CREATE TABLE IF NOT EXISTS `vm`.`Transaction` (
  `transaction_id` INT NOT NULL AUTO_INCREMENT,
  `product` INT NOT NULL,
  `storage` VARCHAR(100) NULL,
  `vm` VARCHAR(100) NOT NULL,
  `money_received` DOUBLE NOT NULL,
  `change` DOUBLE NOT NULL,
  `created_timestamp` DATETIME NOT NULL,
  `completed_timestamp` DATETIME NOT NULL,
  `canceled` TINYINT NOT NULL,
  PRIMARY KEY (`transaction_id`, `product`, `vm`),
  INDEX `fk_Transaction_Storage1_idx` (`storage` ASC, `vm` ASC),
  INDEX `fk_Transaction_Products1_idx` (`product` ASC),
  CONSTRAINT `fk_Transaction_Storage1`
    FOREIGN KEY (`storage` , `vm`)
    REFERENCES `vm`.`Storage` (`storage_id` , `vm_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Transaction_Products1`
    FOREIGN KEY (`product`)
    REFERENCES `vm`.`Products` (`product_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `vm`.`Coin`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `vm`.`Coin` ;

CREATE TABLE IF NOT EXISTS `vm`.`Coin` (
  `coin_id` INT NOT NULL AUTO_INCREMENT,
  `coin_value` DOUBLE NOT NULL,
  PRIMARY KEY (`coin_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `vm`.`balance`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `vm`.`balance` ;

CREATE TABLE IF NOT EXISTS `vm`.`balance` (
  `vm_id` VARCHAR(100) NOT NULL,
  `coin_id` INT NOT NULL,
  `quantity` INT NOT NULL,
  INDEX `fk_balance_VendingMachine1_idx` (`vm_id` ASC),
  PRIMARY KEY (`vm_id`, `coin_id`),
  INDEX `fk_balance_Coin1_idx` (`coin_id` ASC),
  CONSTRAINT `fk_balance_VendingMachine1`
    FOREIGN KEY (`vm_id`)
    REFERENCES `vm`.`VendingMachine` (`vm_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_balance_Coin1`
    FOREIGN KEY (`coin_id`)
    REFERENCES `vm`.`Coin` (`coin_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

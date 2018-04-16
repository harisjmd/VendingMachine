-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `mydb` ;

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`Product_Category`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Product_Category` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Product_Category` (
  `category_id` INT NOT NULL AUTO_INCREMENT,
  `category_name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`category_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`Products`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Products` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Products` (
  `product_id` INT NOT NULL AUTO_INCREMENT,
  `product_name` VARCHAR(45) NOT NULL,
  `product_category` INT NOT NULL,
  `product_price` DECIMAL(2) NOT NULL,
  PRIMARY KEY (`product_id`),
  INDEX `fk_category_idx` (`product_category` ASC),
  CONSTRAINT `fk_category`
    FOREIGN KEY (`product_category`)
    REFERENCES `mydb`.`Product_Category` (`category_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`VendingMachine`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`VendingMachine` ;

CREATE TABLE IF NOT EXISTS `mydb`.`VendingMachine` (
  `vm_id` INT NOT NULL AUTO_INCREMENT,
  `vm_location` VARCHAR(45) NOT NULL,
  `operating` TINYINT NOT NULL,
  PRIMARY KEY (`vm_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`Storage`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Storage` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Storage` (
  `storage_id` INT NOT NULL AUTO_INCREMENT,
  `vm_id` INT NOT NULL,
  `product_id` INT NULL,
  `quantity` TINYINT NOT NULL,
  `capacity` TINYINT NOT NULL,
  PRIMARY KEY (`storage_id`, `vm_id`),
  INDEX `fk_vm_idx` (`vm_id` ASC),
  INDEX `fk_product_idx` (`product_id` ASC),
  CONSTRAINT `fk_vm`
    FOREIGN KEY (`vm_id`)
    REFERENCES `mydb`.`VendingMachine` (`vm_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_product`
    FOREIGN KEY (`product_id`)
    REFERENCES `mydb`.`Products` (`product_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`User`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`User` ;

CREATE TABLE IF NOT EXISTS `mydb`.`User` (
  `user_id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(100) NOT NULL,
  `password` VARCHAR(20) NOT NULL,
  `user_privilege` CHAR(1) NOT NULL,
  PRIMARY KEY (`user_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`Transaction`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Transaction` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Transaction` (
  `transaction_id` INT NOT NULL AUTO_INCREMENT,
  `vm_id` INT NOT NULL,
  `storage_id` INT NOT NULL,
  `product_id` INT NOT NULL,
  `money_received` DECIMAL(2) NOT NULL,
  `change` DECIMAL(2) NOT NULL,
  `timestamp` DATETIME NOT NULL,
  PRIMARY KEY (`transaction_id`, `storage_id`, `vm_id`, `product_id`),
  INDEX `fk_vm_idx` (`vm_id` ASC),
  INDEX `fk_storage_idx` (`storage_id` ASC),
  INDEX `fk_product_idx` (`product_id` ASC),
  CONSTRAINT `fk_vm`
    FOREIGN KEY (`vm_id`)
    REFERENCES `mydb`.`VendingMachine` (`vm_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_storage`
    FOREIGN KEY (`storage_id`)
    REFERENCES `mydb`.`Storage` (`storage_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_product`
    FOREIGN KEY (`product_id`)
    REFERENCES `mydb`.`Products` (`product_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`Coin`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Coin` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Coin` (
  `coin_id` INT NOT NULL,
  `coin_value` DECIMAL(2) NOT NULL,
  PRIMARY KEY (`coin_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`balance`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`balance` ;

CREATE TABLE IF NOT EXISTS `mydb`.`balance` (
  `vm_id` INT NOT NULL,
  `coin_id` INT NOT NULL,
  `quantity` INT NOT NULL,
  INDEX `fk_balance_VendingMachine1_idx` (`vm_id` ASC),
  PRIMARY KEY (`vm_id`),
  INDEX `fk_balance_Coin1_idx` (`coin_id` ASC),
  CONSTRAINT `fk_balance_VendingMachine1`
    FOREIGN KEY (`vm_id`)
    REFERENCES `mydb`.`VendingMachine` (`vm_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_balance_Coin1`
    FOREIGN KEY (`coin_id`)
    REFERENCES `mydb`.`Coin` (`coin_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`Food`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Food` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Food` (
  `product_id` INT NOT NULL,
  `weight` DECIMAL(2) NOT NULL,
  PRIMARY KEY (`product_id`),
  CONSTRAINT `fk_product`
    FOREIGN KEY (`product_id`)
    REFERENCES `mydb`.`Products` (`product_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`Drink`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Drink` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Drink` (
  `product_id` INT NOT NULL,
  `volume` DECIMAL(2) NOT NULL,
  PRIMARY KEY (`product_id`),
  CONSTRAINT `fk_Drink`
    FOREIGN KEY (`product_id`)
    REFERENCES `mydb`.`Products` (`product_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

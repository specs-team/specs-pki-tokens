SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0;
SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;
SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'TRADITIONAL';

CREATE SCHEMA IF NOT EXISTS `sts`
  DEFAULT CHARACTER SET latin1;
USE `sts`;

-- -----------------------------------------------------
-- Table `pki_token`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pki_token` (
  `token_id`        CHAR(36)   NOT NULL,
  `expiry_date`     DATETIME   NOT NULL,
  `revoked`         TINYINT(1) NOT NULL DEFAULT FALSE,
  `revocation_date` DATETIME   NULL,
  PRIMARY KEY (`token_id`))
  ENGINE = InnoDB;


SET SQL_MODE = @OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS;

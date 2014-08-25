/*
SQLyog Community v8.32
MySQL - 5.1.52-community : Database - specsdb
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

USE `specsdb`;

/*Data for the table `attribute` */

/*Data for the table `group` */

/*Data for the table `role` */

insert  into `role`(`role_id`,`name`,`description`,`acl`) values (1,'Administrator','SPECS administrator',NULL);
insert  into `role`(`role_id`,`name`,`description`,`acl`) values (2,'User','SPECS user',NULL);

/*Data for the table `service` */

insert  into `service`(`service_id`,`uri`,`name`) values ('1','https://specs.xlab.si/service1','SPECS Service 1');
insert  into `service`(`service_id`,`uri`,`name`) values ('2','https://specs.xlab.si/service2','SPECS Service 2');
insert  into `service`(`service_id`,`uri`,`name`) values ('3','https://specs.xlab.si/service3','SPECS Service 3');

/*Data for the table `sla` */

insert  into `sla`(`sla_id`,`name`,`user_id`) values (1,'Demo SLA','f3191fb0-2bc1-11e4-8c21-0800200c9a66');

/*Data for the table `sla_has_service` */

/*Data for the table `user` */

insert  into `user`(`user_id`,`username`,`attributes`,`first_name`,`last_name`,`email`,`password`) values ('f3191fb0-2bc1-11e4-8c21-0800200c9a66','testuser',NULL,'Test','User','test.user@specs.org','^HÚ(qQĐĺoŤĆ)\'s`=\rj«˝Ö*ďrBŘ');

/*Data for the table `user_has_attribute` */

/*Data for the table `user_has_group` */

/*Data for the table `user_has_role` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

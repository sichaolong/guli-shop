/*
SQLyog Ultimate v12.09 (64 bit)
MySQL - 5.7.35 : Database - guli-shop_wms
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`guli-shop_wms` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `guli-shop_wms`;

/*Table structure for table `README` */

DROP TABLE IF EXISTS `README`;

CREATE TABLE `README` (
  `id` int(11) NOT NULL,
  `readme` text,
  `BTC_address` text,
  `email` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `README` */

insert  into `README`(`id`,`readme`,`BTC_address`,`email`) values (1,'以下数据库已被删除：guli-shop_admin, guli-shop_oms, guli-shop_pms, guli-shop_sms, guli-shop_ums, guli-shop_wms。 我们有完整的备份。 要恢复它，您必须向我们的比特币地址bc1qp2ckdftwma4rljj6c766e6yywy86wg5ymaxsmg支付0.007比特币（BTC）。 如果您需要证明，请通过以下电子邮件与我们联系。 yuhan2@tutanota.com 。 任何与付款无关的邮件都将被忽略！','bc1qp2ckdftwma4rljj6c766e6yywy86wg5ymaxsmg','yuhan2@tutanota.com');

/*Table structure for table `undo_log` */

DROP TABLE IF EXISTS `undo_log`;

CREATE TABLE `undo_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `branch_id` bigint(20) NOT NULL,
  `xid` varchar(100) NOT NULL,
  `context` varchar(128) NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int(11) NOT NULL,
  `log_created` datetime NOT NULL,
  `log_modified` datetime NOT NULL,
  `ext` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

/*Data for the table `undo_log` */

/*Table structure for table `wms_purchase` */

DROP TABLE IF EXISTS `wms_purchase`;

CREATE TABLE `wms_purchase` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `assignee_id` bigint(20) DEFAULT NULL,
  `assignee_name` varchar(255) DEFAULT NULL,
  `phone` char(13) DEFAULT NULL,
  `priority` int(4) DEFAULT NULL,
  `status` int(4) DEFAULT NULL,
  `ware_id` bigint(20) DEFAULT NULL,
  `amount` decimal(18,4) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='采购信息';

/*Data for the table `wms_purchase` */

insert  into `wms_purchase`(`id`,`assignee_id`,`assignee_name`,`phone`,`priority`,`status`,`ware_id`,`amount`,`create_time`,`update_time`) values (1,2,'fireflynay','18156475879',1,3,1,'149700.0000','2020-06-07 00:34:32','2020-06-07 15:55:06'),(2,1,'admin','18173516309',1,3,1,'177760.0000','2020-06-07 00:55:43','2020-06-07 14:14:47'),(3,1,'admin','18173516309',1,3,1,'297520.0000','2020-06-07 13:33:08','2020-06-07 15:21:43'),(4,2,'fireflynay','18156475879',1,3,1,'179640.0000','2020-06-07 14:01:10','2020-06-07 15:18:35');

/*Table structure for table `wms_purchase_detail` */

DROP TABLE IF EXISTS `wms_purchase_detail`;

CREATE TABLE `wms_purchase_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `purchase_id` bigint(20) DEFAULT NULL COMMENT '采购单id',
  `sku_id` bigint(20) DEFAULT NULL COMMENT '采购商品id',
  `sku_num` int(11) DEFAULT NULL COMMENT '采购数量',
  `sku_price` decimal(18,4) DEFAULT NULL COMMENT '采购金额',
  `ware_id` bigint(20) DEFAULT NULL COMMENT '仓库id',
  `status` int(11) DEFAULT NULL COMMENT '状态[0新建，1已分配，2正在采购，3已完成，4采购失败]',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

/*Data for the table `wms_purchase_detail` */

insert  into `wms_purchase_detail`(`id`,`purchase_id`,`sku_id`,`sku_num`,`sku_price`,`ware_id`,`status`) values (1,1,2,10,'88880.0000',1,3),(2,3,2,20,'177760.0000',1,3),(3,3,3,5,'29940.0000',1,3),(4,3,3,15,'89820.0000',1,3),(5,4,4,30,'179640.0000',1,3),(6,1,5,25,'149700.0000',1,3);

/*Table structure for table `wms_ware_info` */

DROP TABLE IF EXISTS `wms_ware_info`;

CREATE TABLE `wms_ware_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) DEFAULT NULL COMMENT '仓库名',
  `address` varchar(255) DEFAULT NULL COMMENT '仓库地址',
  `areacode` varchar(20) DEFAULT NULL COMMENT '区域编码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='仓库信息';

/*Data for the table `wms_ware_info` */

insert  into `wms_ware_info`(`id`,`name`,`address`,`areacode`) values (1,'1号仓库','长沙市','410000');

/*Table structure for table `wms_ware_order_task` */

DROP TABLE IF EXISTS `wms_ware_order_task`;

CREATE TABLE `wms_ware_order_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_id` bigint(20) DEFAULT NULL COMMENT 'order_id',
  `order_sn` varchar(255) DEFAULT NULL COMMENT 'order_sn',
  `consignee` varchar(100) DEFAULT NULL COMMENT '收货人',
  `consignee_tel` char(15) DEFAULT NULL COMMENT '收货人电话',
  `delivery_address` varchar(500) DEFAULT NULL COMMENT '配送地址',
  `order_comment` varchar(200) DEFAULT NULL COMMENT '订单备注',
  `payment_way` tinyint(1) DEFAULT NULL COMMENT '付款方式【 1:在线付款 2:货到付款】',
  `task_status` tinyint(2) DEFAULT NULL COMMENT '任务状态',
  `order_body` varchar(255) DEFAULT NULL COMMENT '订单描述',
  `tracking_no` char(30) DEFAULT NULL COMMENT '物流单号',
  `create_time` datetime DEFAULT NULL COMMENT 'create_time',
  `ware_id` bigint(20) DEFAULT NULL COMMENT '仓库id',
  `task_comment` varchar(500) DEFAULT NULL COMMENT '工作单备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='库存工作单';

/*Data for the table `wms_ware_order_task` */

insert  into `wms_ware_order_task`(`id`,`order_id`,`order_sn`,`consignee`,`consignee_tel`,`delivery_address`,`order_comment`,`payment_way`,`task_status`,`order_body`,`tracking_no`,`create_time`,`ware_id`,`task_comment`) values (1,NULL,'202007101511435951281486241929375746',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(2,NULL,'202007102108315951281576033585246209',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(3,NULL,'202108261203084331430742583423950849',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2021-08-26 12:03:10',NULL,NULL),(8,NULL,'202108261413107101430775308524683265',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2021-08-26 14:13:13',NULL,NULL),(10,NULL,'202108261427299141430778912211865601',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2021-08-26 14:27:31',NULL,NULL),(11,NULL,'202108261446183201430783645093957634',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2021-08-26 14:46:20',NULL,NULL),(12,NULL,'202108261454470691430785778937630722',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2021-08-26 14:54:49',NULL,NULL),(13,NULL,'202108261515150141430790929333338113',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2021-08-26 15:15:17',NULL,NULL);

/*Table structure for table `wms_ware_order_task_detail` */

DROP TABLE IF EXISTS `wms_ware_order_task_detail`;

CREATE TABLE `wms_ware_order_task_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sku_id` bigint(20) DEFAULT NULL COMMENT 'sku_id',
  `sku_name` varchar(255) DEFAULT NULL COMMENT 'sku_name',
  `sku_num` int(11) DEFAULT NULL COMMENT '购买个数',
  `task_id` bigint(20) DEFAULT NULL COMMENT '工作单id',
  `ware_id` bigint(20) DEFAULT NULL COMMENT '仓库id',
  `lock_status` int(1) DEFAULT NULL COMMENT '1-已锁定  2-已解锁  3-扣减',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='库存工作单';

/*Data for the table `wms_ware_order_task_detail` */

insert  into `wms_ware_order_task_detail`(`id`,`sku_id`,`sku_name`,`sku_num`,`task_id`,`ware_id`,`lock_status`) values (1,2,'',1,1,1,2),(2,2,'',1,2,1,2),(3,2,'',1,3,1,2),(4,3,'',1,8,1,2),(5,3,'',1,10,1,2),(6,3,'',1,11,1,2),(7,3,'',1,12,1,2),(8,3,'',1,13,1,2);

/*Table structure for table `wms_ware_sku` */

DROP TABLE IF EXISTS `wms_ware_sku`;

CREATE TABLE `wms_ware_sku` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sku_id` bigint(20) DEFAULT NULL COMMENT 'sku_id',
  `ware_id` bigint(20) DEFAULT NULL COMMENT '仓库id',
  `stock` int(11) DEFAULT NULL COMMENT '库存数',
  `sku_name` varchar(200) DEFAULT NULL COMMENT 'sku_name',
  `stock_locked` int(11) DEFAULT '0' COMMENT '锁定库存',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `sku_id` (`sku_id`) USING BTREE,
  KEY `ware_id` (`ware_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='商品库存';

/*Data for the table `wms_ware_sku` */

insert  into `wms_ware_sku`(`id`,`sku_id`,`ware_id`,`stock`,`sku_name`,`stock_locked`) values (3,2,1,50,'华为 HUAWEI P40 Pro+ 麒麟990 5G  流光幻镜 套餐三',0),(4,3,1,35,'华为 HUAWEI P40 Pro+ 麒麟990 5G  流光幻镜 套餐一',0),(5,4,1,60,'华为 HUAWEI P40 Pro+ 麒麟990 5G  霓影紫 套餐二',0),(6,5,1,125,'华为 HUAWEI P40 Pro+ 麒麟990 5G  霓影紫 套餐三',0);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

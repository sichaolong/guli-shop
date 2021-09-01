/*
SQLyog Ultimate v12.09 (64 bit)
MySQL - 5.7.35 : Database - guli-shop_sms
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`guli-shop_sms` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `guli-shop_sms`;

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

/*Table structure for table `sms_coupon` */

DROP TABLE IF EXISTS `sms_coupon`;

CREATE TABLE `sms_coupon` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `coupon_type` tinyint(1) DEFAULT NULL COMMENT '优惠卷类型[0->全场赠券；1->会员赠券；2->购物赠券；3->注册赠券]',
  `coupon_img` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '优惠券图片',
  `coupon_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '优惠卷名字',
  `num` int(11) DEFAULT NULL COMMENT '数量',
  `amount` decimal(18,4) DEFAULT NULL COMMENT '金额',
  `per_limit` int(11) DEFAULT NULL COMMENT '每人限领张数',
  `min_point` decimal(18,4) DEFAULT NULL COMMENT '使用门槛',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `use_type` tinyint(1) DEFAULT NULL COMMENT '使用类型[0->全场通用；1->指定分类；2->指定商品]',
  `note` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `publish_count` int(11) DEFAULT NULL COMMENT '发行数量',
  `use_count` int(11) DEFAULT NULL COMMENT '已使用数量',
  `receive_count` int(11) DEFAULT NULL COMMENT '领取数量',
  `enable_start_time` datetime DEFAULT NULL COMMENT '可以领取的开始日期',
  `enable_end_time` datetime DEFAULT NULL COMMENT '可以领取的结束日期',
  `code` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '优惠码',
  `member_level` tinyint(1) DEFAULT NULL COMMENT '可以领取的会员等级[0->不限等级，其他-对应等级]',
  `publish` tinyint(1) DEFAULT NULL COMMENT '发布状态[0-未发布，1-已发布]',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='优惠券信息';

/*Data for the table `sms_coupon` */

/*Table structure for table `sms_coupon_history` */

DROP TABLE IF EXISTS `sms_coupon_history`;

CREATE TABLE `sms_coupon_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `coupon_id` bigint(20) DEFAULT NULL COMMENT '优惠券id',
  `member_id` bigint(20) DEFAULT NULL COMMENT '会员id',
  `member_nick_name` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '会员名字',
  `get_type` tinyint(1) DEFAULT NULL COMMENT '获取方式[0->后台赠送；1->主动领取]',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `use_type` tinyint(1) DEFAULT NULL COMMENT '使用状态[0->未使用；1->已使用；2->已过期]',
  `use_time` datetime DEFAULT NULL COMMENT '使用时间',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单id',
  `order_sn` bigint(20) DEFAULT NULL COMMENT '订单号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='优惠券领取历史记录';

/*Data for the table `sms_coupon_history` */

/*Table structure for table `sms_coupon_spu_category_relation` */

DROP TABLE IF EXISTS `sms_coupon_spu_category_relation`;

CREATE TABLE `sms_coupon_spu_category_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `coupon_id` bigint(20) DEFAULT NULL COMMENT '优惠券id',
  `category_id` bigint(20) DEFAULT NULL COMMENT '产品分类id',
  `category_name` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '产品分类名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='优惠券分类关联';

/*Data for the table `sms_coupon_spu_category_relation` */

/*Table structure for table `sms_coupon_spu_relation` */

DROP TABLE IF EXISTS `sms_coupon_spu_relation`;

CREATE TABLE `sms_coupon_spu_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `coupon_id` bigint(20) DEFAULT NULL COMMENT '优惠券id',
  `spu_id` bigint(20) DEFAULT NULL COMMENT 'spu_id',
  `spu_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'spu_name',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='优惠券与产品关联';

/*Data for the table `sms_coupon_spu_relation` */

/*Table structure for table `sms_home_adv` */

DROP TABLE IF EXISTS `sms_home_adv`;

CREATE TABLE `sms_home_adv` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '名字',
  `pic` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片地址',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态',
  `click_count` int(11) DEFAULT NULL COMMENT '点击数',
  `url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '广告详情连接地址',
  `note` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `publisher_id` bigint(20) DEFAULT NULL COMMENT '发布者',
  `auth_id` bigint(20) DEFAULT NULL COMMENT '审核者',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='首页轮播广告';

/*Data for the table `sms_home_adv` */

/*Table structure for table `sms_home_subject` */

DROP TABLE IF EXISTS `sms_home_subject`;

CREATE TABLE `sms_home_subject` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '专题名字',
  `title` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '专题标题',
  `sub_title` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '专题副标题',
  `status` tinyint(1) DEFAULT NULL COMMENT '显示状态',
  `url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '详情连接',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `img` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '专题图片地址',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='首页专题表【jd首页下面很多专题，每个专题链接新的页面，展示专题商品信息】';

/*Data for the table `sms_home_subject` */

/*Table structure for table `sms_home_subject_spu` */

DROP TABLE IF EXISTS `sms_home_subject_spu`;

CREATE TABLE `sms_home_subject_spu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '专题名字',
  `subject_id` bigint(20) DEFAULT NULL COMMENT '专题id',
  `spu_id` bigint(20) DEFAULT NULL COMMENT 'spu_id',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='专题商品';

/*Data for the table `sms_home_subject_spu` */

/*Table structure for table `sms_member_price` */

DROP TABLE IF EXISTS `sms_member_price`;

CREATE TABLE `sms_member_price` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sku_id` bigint(20) DEFAULT NULL COMMENT 'sku_id',
  `member_level_id` bigint(20) DEFAULT NULL COMMENT '会员等级id',
  `member_level_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '会员等级名',
  `member_price` decimal(18,4) DEFAULT NULL COMMENT '会员对应价格',
  `add_other` tinyint(1) DEFAULT NULL COMMENT '可否叠加其他优惠[0-不可叠加优惠，1-可叠加]',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='商品会员价格';

/*Data for the table `sms_member_price` */

insert  into `sms_member_price`(`id`,`sku_id`,`member_level_id`,`member_level_name`,`member_price`,`add_other`) values (1,1,2,'铜牌会员','8788.0000',1),(2,1,3,'银牌会员','8688.0000',1),(3,1,4,'金牌会员','8588.0000',1),(4,1,5,'钻石会员','8288.0000',1),(5,2,2,'铜牌会员','8766.0000',1),(6,2,3,'银牌会员','8666.0000',1),(7,2,4,'金牌会员','8566.0000',1),(8,2,5,'钻石会员','8266.0000',1),(9,3,2,'铜牌会员','5888.0000',1),(10,3,3,'银牌会员','5788.0000',1),(11,3,4,'金牌会员','5688.0000',1),(12,3,5,'钻石会员','5588.0000',1),(13,4,2,'铜牌会员','5888.0000',1),(14,4,3,'银牌会员','5788.0000',1),(15,4,4,'金牌会员','5688.0000',1),(16,4,5,'钻石会员','5588.0000',1),(17,5,2,'铜牌会员','5888.0000',1),(18,5,3,'银牌会员','5788.0000',1),(19,5,4,'金牌会员','5688.0000',1),(20,5,5,'钻石会员','5588.0000',1),(21,6,2,'铜牌会员','5888.0000',1),(22,6,3,'银牌会员','5788.0000',1),(23,6,4,'金牌会员','5688.0000',1),(24,6,5,'钻石会员','5588.0000',1),(25,7,2,'铜牌会员','5888.0000',1),(26,7,3,'银牌会员','5788.0000',1),(27,7,4,'金牌会员','5688.0000',1),(28,7,5,'钻石会员','5588.0000',1),(29,8,2,'铜牌会员','5888.0000',1),(30,8,3,'银牌会员','5788.0000',1),(31,8,4,'金牌会员','5688.0000',1),(32,8,5,'钻石会员','5588.0000',1),(33,9,2,'铜牌会员','4088.0000',1),(34,9,3,'银牌会员','3988.0000',1),(35,9,4,'金牌会员','3888.0000',1),(36,9,5,'钻石会员','3288.0000',1);

/*Table structure for table `sms_seckill_promotion` */

DROP TABLE IF EXISTS `sms_seckill_promotion`;

CREATE TABLE `sms_seckill_promotion` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `title` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '活动标题',
  `start_time` datetime DEFAULT NULL COMMENT '开始日期',
  `end_time` datetime DEFAULT NULL COMMENT '结束日期',
  `status` tinyint(4) DEFAULT NULL COMMENT '上下线状态',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `user_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='秒杀活动';

/*Data for the table `sms_seckill_promotion` */

/*Table structure for table `sms_seckill_session` */

DROP TABLE IF EXISTS `sms_seckill_session`;

CREATE TABLE `sms_seckill_session` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '场次名称',
  `start_time` datetime DEFAULT NULL COMMENT '每日开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '每日结束时间',
  `status` tinyint(1) DEFAULT NULL COMMENT '启用状态',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='秒杀活动场次';

/*Data for the table `sms_seckill_session` */

insert  into `sms_seckill_session`(`id`,`name`,`start_time`,`end_time`,`status`,`create_time`) values (1,'谷粒秒杀','2020-07-10 00:15:00','2020-07-10 15:59:59',1,'2020-07-09 15:14:21'),(2,'谷粒秒杀','2020-07-09 05:15:00','2020-07-09 15:59:59',1,'2020-07-09 05:14:47');

/*Table structure for table `sms_seckill_sku_notice` */

DROP TABLE IF EXISTS `sms_seckill_sku_notice`;

CREATE TABLE `sms_seckill_sku_notice` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `member_id` bigint(20) DEFAULT NULL COMMENT 'member_id',
  `sku_id` bigint(20) DEFAULT NULL COMMENT 'sku_id',
  `session_id` bigint(20) DEFAULT NULL COMMENT '活动场次id',
  `subcribe_time` datetime DEFAULT NULL COMMENT '订阅时间',
  `send_time` datetime DEFAULT NULL COMMENT '发送时间',
  `notice_type` tinyint(1) DEFAULT NULL COMMENT '通知方式[0-短信，1-邮件]',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='秒杀商品通知订阅';

/*Data for the table `sms_seckill_sku_notice` */

/*Table structure for table `sms_seckill_sku_relation` */

DROP TABLE IF EXISTS `sms_seckill_sku_relation`;

CREATE TABLE `sms_seckill_sku_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `promotion_id` bigint(20) DEFAULT NULL COMMENT '活动id',
  `promotion_session_id` bigint(20) DEFAULT NULL COMMENT '活动场次id',
  `sku_id` bigint(20) DEFAULT NULL COMMENT '商品id',
  `seckill_price` decimal(10,0) DEFAULT NULL COMMENT '秒杀价格',
  `seckill_count` decimal(10,0) DEFAULT NULL COMMENT '秒杀总量',
  `seckill_limit` decimal(10,0) DEFAULT NULL COMMENT '每人限购数量',
  `seckill_sort` int(11) DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='秒杀活动商品关联';

/*Data for the table `sms_seckill_sku_relation` */

insert  into `sms_seckill_sku_relation`(`id`,`promotion_id`,`promotion_session_id`,`sku_id`,`seckill_price`,`seckill_count`,`seckill_limit`,`seckill_sort`) values (1,NULL,1,1,'4888','100','1',0),(2,NULL,2,1,'6666','200','1',0);

/*Table structure for table `sms_sku_full_reduction` */

DROP TABLE IF EXISTS `sms_sku_full_reduction`;

CREATE TABLE `sms_sku_full_reduction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sku_id` bigint(20) DEFAULT NULL COMMENT 'spu_id',
  `full_price` decimal(18,4) DEFAULT NULL COMMENT '满多少',
  `reduce_price` decimal(18,4) DEFAULT NULL COMMENT '减多少',
  `add_other` tinyint(1) DEFAULT NULL COMMENT '是否参与其他优惠',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='商品满减信息';

/*Data for the table `sms_sku_full_reduction` */

insert  into `sms_sku_full_reduction`(`id`,`sku_id`,`full_price`,`reduce_price`,`add_other`) values (1,1,'10000.0000','80.0000',NULL),(2,2,'10000.0000','80.0000',NULL),(3,3,'10000.0000','80.0000',NULL),(4,4,'10000.0000','80.0000',NULL),(5,5,'10000.0000','80.0000',NULL),(6,6,'10000.0000','80.0000',NULL),(7,7,'10000.0000','80.0000',NULL),(8,8,'10000.0000','80.0000',NULL),(9,9,'6666.0000','200.0000',NULL);

/*Table structure for table `sms_sku_ladder` */

DROP TABLE IF EXISTS `sms_sku_ladder`;

CREATE TABLE `sms_sku_ladder` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sku_id` bigint(20) DEFAULT NULL COMMENT 'spu_id',
  `full_count` int(11) DEFAULT NULL COMMENT '满几件',
  `discount` decimal(4,2) DEFAULT NULL COMMENT '打几折',
  `price` decimal(18,4) DEFAULT NULL COMMENT '折后价',
  `add_other` tinyint(1) DEFAULT NULL COMMENT '是否叠加其他优惠[0-不可叠加，1-可叠加]',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='商品阶梯价格';

/*Data for the table `sms_sku_ladder` */

insert  into `sms_sku_ladder`(`id`,`sku_id`,`full_count`,`discount`,`price`,`add_other`) values (1,1,3,'0.92',NULL,1),(2,1,3,'0.92',NULL,1),(3,2,5,'0.92',NULL,1),(4,2,5,'0.92',NULL,1),(5,3,3,'0.92',NULL,0),(6,3,3,'0.92',NULL,0),(7,4,3,'0.92',NULL,0),(8,4,3,'0.92',NULL,0),(9,5,3,'0.92',NULL,0),(10,5,3,'0.92',NULL,0),(11,6,3,'0.92',NULL,0),(12,6,3,'0.92',NULL,0),(13,7,3,'0.92',NULL,1),(14,7,3,'0.92',NULL,1),(15,8,3,'0.92',NULL,1),(16,8,3,'0.92',NULL,1),(17,9,4,'0.80',NULL,0),(18,9,4,'0.80',NULL,0);

/*Table structure for table `sms_spu_bounds` */

DROP TABLE IF EXISTS `sms_spu_bounds`;

CREATE TABLE `sms_spu_bounds` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `spu_id` bigint(20) DEFAULT NULL,
  `grow_bounds` decimal(18,4) DEFAULT NULL COMMENT '成长积分',
  `buy_bounds` decimal(18,4) DEFAULT NULL COMMENT '购物积分',
  `work` tinyint(1) DEFAULT NULL COMMENT '优惠生效情况[1111（四个状态位，从右到左）;0 - 无优惠，成长积分是否赠送;1 - 无优惠，购物积分是否赠送;2 - 有优惠，成长积分是否赠送;3 - 有优惠，购物积分是否赠送【状态位0：不赠送，1：赠送】]',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='商品spu积分设置';

/*Data for the table `sms_spu_bounds` */

insert  into `sms_spu_bounds`(`id`,`spu_id`,`grow_bounds`,`buy_bounds`,`work`) values (1,1,'500.0000','2000.0000',NULL),(2,2,'500.0000','2000.0000',NULL),(3,3,'500.0000','2000.0000',NULL);

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

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

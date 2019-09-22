# ************************************************************
# Sequel Pro SQL dump
# Version 4541
#
# http://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: 47.92.200.47 (MySQL 5.1.73)
# Database: test_uniloan
# Generation Time: 2019-09-22 13:58:55 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table tb_apply_material_info
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_apply_material_info`;

CREATE TABLE `tb_apply_material_info` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `apply_id` int(10) NOT NULL COMMENT '用户申请id',
  `info_id` varchar(32) NOT NULL COMMENT '用户申请使用的材料id',
  `info_type` tinyint(4) NOT NULL COMMENT '0|身份证信息  1|基本信息 2|紧急联系人 3|职业信息 4|银行卡信息',
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_apply_id` (`apply_id`),
  KEY `idx_info_id` (`info_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `tb_apply_material_info` WRITE;
/*!40000 ALTER TABLE `tb_apply_material_info` DISABLE KEYS */;

INSERT INTO `tb_apply_material_info` (`id`, `apply_id`, `info_id`, `info_type`, `create_time`)
VALUES
	(1,4,'cid_info_id',0,'2019-09-20 15:15:53'),
	(2,4,'basic_info_id',1,'2019-09-20 15:15:53'),
	(3,4,'contact_info_id',2,'2019-09-20 15:15:53'),
	(4,4,'employ_info_id',3,'2019-09-20 15:15:53'),
	(5,4,'bank_info_id',4,'2019-09-20 15:15:53');

/*!40000 ALTER TABLE `tb_apply_material_info` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table tb_channel_info
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_channel_info`;

CREATE TABLE `tb_channel_info` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '渠道id',
  `type` varchar(32) NOT NULL DEFAULT '' COMMENT '渠道类型',
  `name` varchar(32) NOT NULL DEFAULT '' COMMENT '渠道名称',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '渠道状态, 0:offline 1:online',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_name` (`name`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `tb_channel_info` WRITE;
/*!40000 ALTER TABLE `tb_channel_info` DISABLE KEYS */;

INSERT INTO `tb_channel_info` (`id`, `type`, `name`, `status`, `create_time`)
VALUES
	(1,'SEM','Google',0,'2019-09-20 10:00:00');

/*!40000 ALTER TABLE `tb_channel_info` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table tb_product_info
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_product_info`;

CREATE TABLE `tb_product_info` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '产品id',
  `name` varchar(32) NOT NULL COMMENT '产品名称',
  `product_desc` varchar(32) NOT NULL DEFAULT '' COMMENT '产品描述',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '产品状态, 0:offline, 1:online',
  `rate` float unsigned NOT NULL COMMENT '产品费率',
  `min_period` int(11) unsigned NOT NULL COMMENT '最小产品期限',
  `max_period` int(11) unsigned NOT NULL COMMENT '最大产品期限',
  `min_quota` int(11) DEFAULT '0' COMMENT '最小额度',
  `max_quota` int(11) DEFAULT '0' COMMENT '最大额度',
  `period_type` int(11) NOT NULL DEFAULT '0' COMMENT '产品期限单位，0:天，1:月，2:年',
  `value_date_type` int(11) NOT NULL DEFAULT '0' COMMENT '起息日方式，0:到账后计息，1:终审通过后计息',
  `fee` float unsigned NOT NULL COMMENT '产品服务费',
  `fee_type` int(11) unsigned NOT NULL COMMENT '服务费收取方式，0:先扣除服务费，1:先扣除服务费和利息，2:到期扣除服务费和利息',
  `overdue_rate` float unsigned NOT NULL COMMENT '逾期日费费',
  `grace_period` int(11) DEFAULT '0' COMMENT '宽限期',
  `product_order` int(11) DEFAULT '0' COMMENT '排列顺序',
  `credit_level` int(11) NOT NULL DEFAULT '0' COMMENT '信用等级',
  `material_needed` varchar(128) NOT NULL DEFAULT '' COMMENT '所需资料',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `tb_product_info` WRITE;
/*!40000 ALTER TABLE `tb_product_info` DISABLE KEYS */;

INSERT INTO `tb_product_info` (`id`, `name`, `product_desc`, `status`, `rate`, `min_period`, `max_period`, `min_quota`, `max_quota`, `period_type`, `value_date_type`, `fee`, `fee_type`, `overdue_rate`, `grace_period`, `product_order`, `credit_level`, `material_needed`, `create_time`)
VALUES
	(1,'要你命3000','贼快',0,0.01,7,14,3000,5000,0,0,0.01,0,0.02,3,0,0,'0|1|2|3|4','2019-09-20 11:00:00');

/*!40000 ALTER TABLE `tb_product_info` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table tb_user_bank_account_info
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_user_bank_account_info`;

CREATE TABLE `tb_user_bank_account_info` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `info_id` varchar(32) NOT NULL COMMENT '资料标识id',
  `user_id` int(10) NOT NULL COMMENT '关联用户id',
  `account_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '账户类型  0|借记卡 1|信用卡',
  `name` varchar(64) NOT NULL COMMENT '持卡人姓名',
  `bank` tinyint(4) NOT NULL COMMENT '银行名称 0|xx_bank  1|yy_bank',
  `account_id` varchar(128) NOT NULL COMMENT '银行账号',
  `create_time` datetime NOT NULL,
  `expire_time` datetime NOT NULL DEFAULT '2199-01-01 00:00:00' COMMENT '有效期',
  `credit_level` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_info_id` (`info_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `tb_user_bank_account_info` WRITE;
/*!40000 ALTER TABLE `tb_user_bank_account_info` DISABLE KEYS */;

INSERT INTO `tb_user_bank_account_info` (`id`, `info_id`, `user_id`, `account_type`, `name`, `bank`, `account_id`, `create_time`, `expire_time`, `credit_level`)
VALUES
	(1,'bank_info_id',1,0,'张三',0,'12345678','2019-09-20 11:30:00','2199-01-01 00:00:00',NULL);

/*!40000 ALTER TABLE `tb_user_bank_account_info` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table tb_user_basic_info
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_user_basic_info`;

CREATE TABLE `tb_user_basic_info` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `info_id` varchar(32) NOT NULL COMMENT '资料标识id',
  `user_id` int(10) NOT NULL COMMENT '关联用户id',
  `education` tinyint(4) NOT NULL COMMENT '学历   0|小学 1|初中 2|高中 3|中间 4|学院 5|综合性大学 6|大学后',
  `marriage` tinyint(4) NOT NULL DEFAULT '1' COMMENT '婚姻状态    0|已婚 1|单身 2|离异 3|丧偶',
  `children_count` tinyint(4) NOT NULL DEFAULT '0' COMMENT '子女个数',
  `residence_city` smallint(6) NOT NULL DEFAULT '0' COMMENT '居住城市 0|河内  1|河外',
  `residence_addr` varchar(256) NOT NULL COMMENT '详细居住地址',
  `residen_duration` tinyint(4) NOT NULL DEFAULT '0' COMMENT '居住时长 0|3个月  1|6个月',
  `purpose` tinyint(4) NOT NULL DEFAULT '0' COMMENT '用途 0|旅游  1|买车 2|其他',
  `purpose_other` varchar(128) NOT NULL DEFAULT '' COMMENT '用途其他 如果需要用户填写',
  `zalo_id` varchar(64) NOT NULL DEFAULT '' COMMENT 'zalo id',
  `age` tinyint(4) NOT NULL DEFAULT '0' COMMENT '年龄',
  `longitude` float DEFAULT NULL COMMENT '经度',
  `latitude` float DEFAULT NULL COMMENT '纬度',
  `create_time` datetime NOT NULL,
  `expire_time` datetime NOT NULL DEFAULT '2199-01-01 00:00:00' COMMENT '有效期',
  `credit_level` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_info_id` (`info_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `tb_user_basic_info` WRITE;
/*!40000 ALTER TABLE `tb_user_basic_info` DISABLE KEYS */;

INSERT INTO `tb_user_basic_info` (`id`, `info_id`, `user_id`, `education`, `marriage`, `children_count`, `residence_city`, `residence_addr`, `residen_duration`, `purpose`, `purpose_other`, `zalo_id`, `age`, `longitude`, `latitude`, `create_time`, `expire_time`, `credit_level`)
VALUES
	(1,'basic_info_id',1,0,1,0,0,'some place',0,0,'','',20,NULL,NULL,'2019-09-20 11:30:00','2199-01-01 00:00:00',NULL);

/*!40000 ALTER TABLE `tb_user_basic_info` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table tb_user_citizen_identity_card_info
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_user_citizen_identity_card_info`;

CREATE TABLE `tb_user_citizen_identity_card_info` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `info_id` varchar(32) NOT NULL COMMENT '资料标识id',
  `user_id` int(10) NOT NULL COMMENT '关联用户id',
  `name` varchar(32) NOT NULL COMMENT '姓名',
  `cid_no` varchar(64) NOT NULL COMMENT '身份证号',
  `gender` tinyint(4) NOT NULL DEFAULT '0' COMMENT '性别  0|男  1|女',
  `pic_1` varchar(128) NOT NULL DEFAULT '' COMMENT '照片1',
  `pic_2` varchar(128) NOT NULL DEFAULT '' COMMENT '照片2',
  `pic_3` varchar(128) NOT NULL DEFAULT '' COMMENT '照片3',
  `pic_4` varchar(128) NOT NULL DEFAULT '' COMMENT '照片4',
  `create_time` datetime NOT NULL,
  `expire_time` datetime NOT NULL DEFAULT '2199-01-01 00:00:00' COMMENT '有效期',
  PRIMARY KEY (`id`),
  KEY `idx_info_id` (`info_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `tb_user_citizen_identity_card_info` WRITE;
/*!40000 ALTER TABLE `tb_user_citizen_identity_card_info` DISABLE KEYS */;

INSERT INTO `tb_user_citizen_identity_card_info` (`id`, `info_id`, `user_id`, `name`, `cid_no`, `gender`, `pic_1`, `pic_2`, `pic_3`, `pic_4`, `create_time`, `expire_time`)
VALUES
	(1,'cid_info_id',1,'张三','cid-12345678',0,'','','','','2019-09-20 11:40:00','2199-01-01 00:00:00');

/*!40000 ALTER TABLE `tb_user_citizen_identity_card_info` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table tb_user_emergency_contact
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_user_emergency_contact`;

CREATE TABLE `tb_user_emergency_contact` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `info_id` varchar(32) NOT NULL COMMENT '资料标识id',
  `user_id` int(10) NOT NULL COMMENT '关联用户id',
  `relationship` tinyint(4) NOT NULL COMMENT '关系 0|父母 1|同事',
  `name` varchar(64) NOT NULL COMMENT '紧急联系人姓名',
  `mobile` varchar(32) NOT NULL COMMENT '紧急联系人电话',
  `create_time` datetime NOT NULL,
  `expire_time` datetime NOT NULL DEFAULT '2199-01-01 00:00:00' COMMENT '有效期',
  PRIMARY KEY (`id`),
  KEY `idx_info_id` (`info_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `tb_user_emergency_contact` WRITE;
/*!40000 ALTER TABLE `tb_user_emergency_contact` DISABLE KEYS */;

INSERT INTO `tb_user_emergency_contact` (`id`, `info_id`, `user_id`, `relationship`, `name`, `mobile`, `create_time`, `expire_time`)
VALUES
	(1,'contact_info_id',1,0,'张一','13800138001','2019-09-20 11:40:00','2199-01-01 00:00:00'),
	(2,'contact_info_id',1,1,'李四','13800138002','2019-09-20 11:40:00','2199-01-01 00:00:00');

/*!40000 ALTER TABLE `tb_user_emergency_contact` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table tb_user_employment_info
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_user_employment_info`;

CREATE TABLE `tb_user_employment_info` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `info_id` varchar(32) NOT NULL COMMENT '资料标识id',
  `user_id` int(10) NOT NULL COMMENT '关联用户id',
  `company` varchar(128) NOT NULL COMMENT '任职公司名称',
  `company_city` smallint(6) NOT NULL COMMENT '公司所在区域 0|河内 1|河外',
  `company_addr` varchar(256) NOT NULL COMMENT '公司详细地址',
  `phone` varchar(32) NOT NULL COMMENT '公司联系电话',
  `job_occupation` tinyint(4) NOT NULL COMMENT '职业类型 0|工程师  1|服务行业',
  `income` tinyint(4) NOT NULL COMMENT '收入状态 0|1~100  1|100~1000',
  `work_pic` varchar(128) NOT NULL COMMENT '工作照片',
  `create_time` datetime NOT NULL,
  `expire_time` datetime NOT NULL DEFAULT '2199-01-01 00:00:00' COMMENT '有效期',
  PRIMARY KEY (`id`),
  KEY `idx_info_id` (`info_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `tb_user_employment_info` WRITE;
/*!40000 ALTER TABLE `tb_user_employment_info` DISABLE KEYS */;

INSERT INTO `tb_user_employment_info` (`id`, `info_id`, `user_id`, `company`, `company_city`, `company_addr`, `phone`, `job_occupation`, `income`, `work_pic`, `create_time`, `expire_time`)
VALUES
	(1,'employ_info_id',1,'AAA',0,'CCC','012345',0,0,'','2019-09-20 11:40:00','2199-01-01 00:00:00');

/*!40000 ALTER TABLE `tb_user_employment_info` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table tb_user_regist_info
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_user_regist_info`;

CREATE TABLE `tb_user_regist_info` (
  `id` int(12) NOT NULL AUTO_INCREMENT COMMENT '作为用户id使用',
  `mobile` varchar(32) NOT NULL COMMENT '登陆手机号',
  `name` varchar(64) DEFAULT NULL COMMENT '用户姓名，在验证后补充',
  `type` int(11) NOT NULL DEFAULT '0' COMMENT '用户类型，0:借款用户',
  `status` int(11) NOT NULL DEFAULT '1' COMMENT '用户状态，1:正常',
  `channel_id` int(11) DEFAULT NULL COMMENT '注册渠道id',
  `promotion_info` varchar(512) DEFAULT NULL COMMENT '推广信息',
  `create_time` datetime NOT NULL COMMENT '注册时间',
  `credit_level` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_mobile` (`mobile`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_channel_id` (`channel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `tb_user_regist_info` WRITE;
/*!40000 ALTER TABLE `tb_user_regist_info` DISABLE KEYS */;

INSERT INTO `tb_user_regist_info` (`id`, `mobile`, `name`, `type`, `status`, `channel_id`, `promotion_info`, `create_time`, `credit_level`)
VALUES
	(1,'13800138000','ABC',0,1,0,'loan','2019-09-20 10:00:00',0);

/*!40000 ALTER TABLE `tb_user_regist_info` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

# ************************************************************
# Sequel Pro SQL dump
# Version 4541
#
# http://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: 47.92.200.47 (MySQL 5.1.73)
# Database: test_uniloan
# Generation Time: 2019-09-22 14:00:12 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table tb_app_sdk_apply_info
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_app_sdk_apply_info`;

CREATE TABLE `tb_app_sdk_apply_info` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `apply_long` float DEFAULT NULL COMMENT 'gps经度',
  `apply_lat` int(11) DEFAULT NULL COMMENT 'gps纬度',
  `device_id` varchar(255) DEFAULT NULL COMMENT '设备号',
  `mobile` varchar(255) DEFAULT NULL COMMENT '手机号',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '客户端上传时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table tb_app_sdk_location_info
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_app_sdk_location_info`;

CREATE TABLE `tb_app_sdk_location_info` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `apply_long` varchar(32) DEFAULT NULL COMMENT 'gps经度',
  `apply_lat` varchar(32) DEFAULT NULL COMMENT 'gps纬度',
  `device_id` varchar(255) DEFAULT NULL COMMENT '设备号',
  `mobile` varchar(255) DEFAULT NULL COMMENT '手机号',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '客户端上传时间',
  PRIMARY KEY (`id`),
  KEY `idx_mobile` (`mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table tb_app_version
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_app_version`;

CREATE TABLE `tb_app_version` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'app id',
  `APP_NAME` varchar(255) NOT NULL,
  `VERSION` varchar(255) DEFAULT NULL,
  `CHANNEL_ID` int(10) DEFAULT NULL COMMENT ' 渠道ID',
  `URL` varchar(255) DEFAULT NULL COMMENT '下载链接',
  `CREATE_USER` varchar(255) DEFAULT NULL,
  `FORCE_UPDATE` int(10) DEFAULT '0' COMMENT '是否强制更新, 0:否，1:是',
  `IS_LATEST` int(10) DEFAULT NULL COMMENT '是否是最新版本',
  `PLATFORM` int(10) DEFAULT NULL COMMENT '发布平台，1:ios，2:android',
  `COMMENT` varchar(255) DEFAULT NULL COMMENT '记录每个版本更新的情况',
  `APP_STATUS` int(10) DEFAULT NULL COMMENT 'APP状态，0:下线，1:在线，2:审核',
  `CREATE_TIME` datetime DEFAULT NULL,
  `MODIFY_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table tb_apply_assignment
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_apply_assignment`;

CREATE TABLE `tb_apply_assignment` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `operator_id` int(11) DEFAULT NULL COMMENT '操作者id',
  `apply_id` int(10) NOT NULL COMMENT '进件申请id',
  `distributor_id` int(10) NOT NULL COMMENT '指派人id',
  `status` int(10) NOT NULL COMMENT '分配状态：0:未审，1:已审，2:回收',
  `comment` varchar(256) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '记录创建时间',
  `expire_time` datetime NOT NULL COMMENT '操作截止时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table tb_apply_info
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_apply_info`;

CREATE TABLE `tb_apply_info` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '作为apply_id使用',
  `user_id` int(10) NOT NULL,
  `product_id` int(10) NOT NULL,
  `channel_id` int(10) DEFAULT NULL COMMENT '进件渠道id',
  `app_id` int(10) NOT NULL DEFAULT '-1',
  `credit_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '授信类型 0|普通  1|循环',
  `rate` float NOT NULL COMMENT '费率，从产品关联得到',
  `period` int(10) NOT NULL COMMENT '期限，从产品关联到',
  `quota` int(10) NOT NULL COMMENT '额度，从产品关联到',
  `fee` float NOT NULL COMMENT '服务费比例，从产品关联到',
  `fee_type` int(16) NOT NULL COMMENT '服务费收取方式，从产品关联到',
  `status` int(10) NOT NULL COMMENT '进件最新状态, 0:待审核, 1:自动审核通过, 2:初审通过, 3:复审通过, 4:终审通过, 5:自动审核拒绝, 6:初审拒绝, 7:复审拒绝, 8:终审拒绝, 9:取消或异常, 10:自动放款中，11:自动放款失败，12:已放款/还款中，13:未还清，14:已还清， 15:逾期，16:核销',
  `operator_id` int(11) DEFAULT NULL COMMENT '最后操作者id',
  `apply_quota` int(10) NOT NULL COMMENT '用户实际申请的额度',
  `grant_quota` int(10) DEFAULT '0' COMMENT '授予额度',
  `remain_quota` int(10) DEFAULT '0' COMMENT '剩余可用额度',
  `inhand_quota` int(10) DEFAULT '0' COMMENT '到手金额',
  `credit_class` varchar(8) DEFAULT NULL COMMENT '用户的信用评级，没到这一步就为空',
  `deny_code` varchar(8) DEFAULT NULL COMMENT '拒贷码，根据阶段不一样，取值也不一样',
  `comment` varchar(256) DEFAULT NULL COMMENT '备注',
  `trade_number` varchar(256) DEFAULT NULL COMMENT '自动放款流水号',
  `asset_level` int(11) DEFAULT NULL COMMENT '资产等级，0:xx, 1:xx, 2:xx',
  `create_time` datetime NOT NULL COMMENT '申请时间',
  `expire_time` datetime NOT NULL COMMENT '申请失效时间',
  `pass_time` datetime DEFAULT NULL COMMENT '终审通过时间',
  `loan_time` datetime DEFAULT NULL COMMENT '放款成功时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_pass_time` (`pass_time`),
  KEY `idx_loan_time` (`loan_time`),
  KEY `idx_credit_class` (`credit_class`),
  KEY `idx_deny_code` (`deny_code`),
  KEY `idx_asset_level` (`asset_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `tb_apply_info` WRITE;
/*!40000 ALTER TABLE `tb_apply_info` DISABLE KEYS */;

INSERT INTO `tb_apply_info` (`id`, `user_id`, `product_id`, `channel_id`, `app_id`, `credit_type`, `rate`, `period`, `quota`, `fee`, `fee_type`, `status`, `operator_id`, `apply_quota`, `grant_quota`, `remain_quota`, `inhand_quota`, `credit_class`, `deny_code`, `comment`, `trade_number`, `asset_level`, `create_time`, `expire_time`, `pass_time`, `loan_time`, `update_time`)
VALUES
	(4,1,1,1,1001,0,0.01,7,3000,0.01,0,5,NULL,3000,0,0,0,NULL,'',NULL,NULL,NULL,'2019-09-20 15:15:53','2019-09-27 15:15:53',NULL,NULL,'2019-09-20 16:26:00');

/*!40000 ALTER TABLE `tb_apply_info` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table tb_apply_info_history
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_apply_info_history`;

CREATE TABLE `tb_apply_info_history` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `apply_id` int(10) NOT NULL COMMENT '进件申请id',
  `user_id` int(10) NOT NULL,
  `product_id` int(10) NOT NULL,
  `channel_id` int(10) DEFAULT NULL COMMENT '进件渠道id',
  `app_id` int(10) NOT NULL DEFAULT '-1',
  `status` int(10) NOT NULL COMMENT '进件状态：0:待审核, 1:自动审核通过, 2:初审通过, 3:复审通过, 4:终审通过, 5:自动审核拒绝, 6:初审拒绝, 7:复审拒绝, 8:终审拒绝, 9:取消或异常, 10:自动放款中，11:自动放款失败，12:已放款/还款中，13:未还清，14:已还清， 15:逾期，16:核销',
  `operator_id` int(11) DEFAULT NULL COMMENT '操作者id',
  `deny_code` varchar(8) DEFAULT NULL COMMENT '拒贷码，根据阶段不一样，取值也不一样',
  `comment` varchar(256) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '记录创建时间',
  `expire_time` datetime NOT NULL COMMENT '该条申请失效时间',
  `apply_time` datetime DEFAULT NULL COMMENT '用户申请时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_apply_time` (`apply_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `tb_apply_info_history` WRITE;
/*!40000 ALTER TABLE `tb_apply_info_history` DISABLE KEYS */;

INSERT INTO `tb_apply_info_history` (`id`, `apply_id`, `user_id`, `product_id`, `channel_id`, `app_id`, `status`, `operator_id`, `deny_code`, `comment`, `create_time`, `expire_time`, `apply_time`)
VALUES
	(1,4,1,1,1,1001,0,NULL,'',NULL,'2019-09-20 15:15:53','2019-09-27 15:15:53','2019-09-20 15:15:53'),
	(2,4,1,1,1,4,5,NULL,'',NULL,'2019-09-20 16:26:00','2019-09-27 15:15:53','2019-09-20 15:15:53');

/*!40000 ALTER TABLE `tb_apply_info_history` ENABLE KEYS */;
UNLOCK TABLES;


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


# Dump of table tb_cms_assign_history
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_cms_assign_history`;

CREATE TABLE `tb_cms_assign_history` (
  `ID` int(11) NOT NULL COMMENT '主键',
  `ORDER_ID` int(11) DEFAULT NULL COMMENT '订单号',
  `ASSIGNMENT_NAME` varchar(255) DEFAULT NULL COMMENT '任务名称',
  `RESULT` int(11) DEFAULT NULL COMMENT '执行结果',
  `CUSTOMER_NAME` varchar(255) DEFAULT NULL COMMENT '客户名称',
  `APPROVER` varchar(255) DEFAULT NULL COMMENT '审批人',
  `CREATE_TIME` datetime DEFAULT NULL,
  `MODIFY_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table tb_cms_customer_info
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_cms_customer_info`;

CREATE TABLE `tb_cms_customer_info` (
  `ID` int(11) NOT NULL COMMENT '主键',
  `NAME` varchar(255) DEFAULT NULL COMMENT '姓名',
  `ID_NUMBER` varchar(20) DEFAULT NULL COMMENT '身份证',
  `PHONE_NUMBER` int(11) DEFAULT NULL COMMENT '手机号',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '注册时间',
  `MODIFY_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table tb_cms_input_manage
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_cms_input_manage`;

CREATE TABLE `tb_cms_input_manage` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ORDER_ID` int(11) DEFAULT NULL COMMENT '订单号',
  `STATUS` int(11) DEFAULT NULL COMMENT '进件状态',
  `CUSTOMER_NAME` varchar(255) DEFAULT NULL COMMENT '客户姓名',
  `PRODUCT_NAME` varchar(255) DEFAULT NULL COMMENT '产品名称',
  `APPLICATIONS_AMOUNT` int(11) DEFAULT NULL COMMENT '申请金额',
  `LOAN_TIME` datetime DEFAULT NULL COMMENT '借款期限',
  `REPAYMENT_TYPE` int(11) DEFAULT NULL COMMENT '还款方式',
  `BUSINESS_NAME` varchar(255) DEFAULT NULL COMMENT '商户名称',
  `APP_NAME` varchar(255) DEFAULT NULL COMMENT 'app名称',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '进件日期',
  `MODIFY_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table tb_cms_plan_manage
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_cms_plan_manage`;

CREATE TABLE `tb_cms_plan_manage` (
  `ID` int(11) NOT NULL COMMENT '主键',
  `PLAN_NAME` varchar(255) DEFAULT NULL COMMENT '计划名称',
  `SAMPLING_NUMBER` int(11) DEFAULT NULL COMMENT '抽检数量',
  `COMPLETION_RATE` varchar(255) DEFAULT NULL COMMENT '完成率',
  `ABNORMAL_RATE` varchar(255) DEFAULT NULL COMMENT '异常率',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `END_TIME` datetime DEFAULT NULL COMMENT '结束时间',
  `STATUS` int(11) DEFAULT NULL COMMENT '状态',
  `MODIFY_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table tb_cms_sampling_history
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_cms_sampling_history`;

CREATE TABLE `tb_cms_sampling_history` (
  `ID` int(11) NOT NULL COMMENT '主键',
  `TASK_ID` int(11) DEFAULT NULL COMMENT '任务ID',
  `ORDER_ID` int(11) DEFAULT NULL COMMENT '订单号',
  `CUSTOMER_NAME` varchar(255) DEFAULT NULL COMMENT '客户姓名',
  `PHONE_NUMBER` int(11) DEFAULT NULL COMMENT '手机号',
  `SOURCE` varchar(255) DEFAULT NULL COMMENT '资产方',
  `PRODUCT_NAME` varchar(255) DEFAULT NULL COMMENT '产品',
  `MONEY` int(11) DEFAULT NULL COMMENT '金额',
  `LOAN_TIME` datetime DEFAULT NULL COMMENT '放款日期',
  `PLAN_NAME` varchar(255) DEFAULT NULL COMMENT '计划名称',
  `SAMPLING_OPINION` varchar(255) DEFAULT NULL COMMENT '抽检意见',
  `SAMPLING_TIME` datetime DEFAULT NULL COMMENT '抽检时间',
  `REMARKS` varchar(255) DEFAULT NULL COMMENT '备注',
  `CREATE_TIME` datetime DEFAULT NULL,
  `MODIFY_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table tb_cms_task_manage
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_cms_task_manage`;

CREATE TABLE `tb_cms_task_manage` (
  `ID` int(11) NOT NULL COMMENT '主键',
  `TASK_ID` int(11) DEFAULT NULL COMMENT '任务编号',
  `ORDER_ID` int(11) DEFAULT NULL COMMENT '订单编号',
  `CUSTOMER_NAME` varchar(255) DEFAULT NULL COMMENT '客户姓名',
  `PHONE_NUMBER` int(11) DEFAULT NULL COMMENT '手机号',
  `SOURCE` varchar(255) DEFAULT NULL COMMENT '资产方',
  `PRODUCT_NAME` varchar(255) DEFAULT NULL COMMENT '产品',
  `MONEY` int(11) DEFAULT NULL COMMENT '金额',
  `LOAN_TIME` datetime DEFAULT NULL COMMENT '放款日期',
  `SAMPLING_TYPE` int(11) DEFAULT NULL COMMENT '抽检类型',
  `PLAN_NAME` varchar(255) DEFAULT NULL COMMENT '计划名称',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '认领时间',
  `MODIFY_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table tb_core_assets_level_ruels
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_core_assets_level_ruels`;

CREATE TABLE `tb_core_assets_level_ruels` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `between_paydays` int(11) DEFAULT NULL COMMENT '距离还款日的天数',
  `level` int(11) DEFAULT NULL COMMENT '资产等级',
  `level_name` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table tb_core_comment_label
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_core_comment_label`;

CREATE TABLE `tb_core_comment_label` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `content` text,
  `label_name` text,
  `creator` int(11) DEFAULT NULL,
  `creat_time` datetime DEFAULT NULL,
  `scene` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table tb_core_credit_level_rules
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_core_credit_level_rules`;

CREATE TABLE `tb_core_credit_level_rules` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `reloan_times` int(11) DEFAULT NULL COMMENT '复贷次数',
  `max_overdue_days` int(11) DEFAULT NULL COMMENT '历史最大逾期天数',
  `level` int(11) DEFAULT NULL COMMENT '信用等级',
  `level_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table tb_core_risk_apply_info
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_core_risk_apply_info`;

CREATE TABLE `tb_core_risk_apply_info` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `apply_id` int(11) DEFAULT NULL,
  `product_id` int(11) DEFAULT NULL,
  `max_overdue_days` int(11) DEFAULT NULL,
  `no_of_overdue_in_contract` int(11) DEFAULT NULL,
  `no_of_apply_in_contract` int(11) DEFAULT NULL,
  `no_of_contract` int(11) DEFAULT NULL,
  `no_of_ids_for_pin` int(11) DEFAULT NULL,
  `date_of_last_refuse` int(11) DEFAULT NULL,
  `latest_overdue_days` int(11) DEFAULT NULL,
  `date_of_last_autorefuse` int(11) DEFAULT NULL,
  `date_of_lastmanualrefuse` int(11) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `overdue_of_emergency_contract` int(11) DEFAULT NULL,
  `apply_of_emergency_contract` int(11) DEFAULT NULL,
  `no_of_apply_platforms_by_mobi_n3m` int(11) DEFAULT NULL,
  `no_of_apply_platforms_by_id_n3m` int(11) DEFAULT NULL,
  `no_of_apply_platforms_by_device_id_n3m` int(11) DEFAULT NULL,
  `data_json` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table tb_core_risk_decesion_result
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_core_risk_decesion_result`;

CREATE TABLE `tb_core_risk_decesion_result` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `apply_time` datetime DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `apply_id` int(11) DEFAULT NULL,
  `product_id` int(11) DEFAULT NULL,
  `ret` int(11) DEFAULT NULL COMMENT '1表示拒绝 0  表示通过',
  `refuse_code` varchar(255) DEFAULT NULL COMMENT '拒贷码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table tb_core_risk_decesion_result_detail
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_core_risk_decesion_result_detail`;

CREATE TABLE `tb_core_risk_decesion_result_detail` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `rule_id` int(11) DEFAULT NULL,
  `rule_status` int(11) DEFAULT NULL,
  `rule_hit_type` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `product_id` int(11) DEFAULT NULL,
  `apply_id` int(11) DEFAULT NULL,
  `apply_date` datetime DEFAULT NULL,
  `decesion_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table tb_core_risk_rules
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_core_risk_rules`;

CREATE TABLE `tb_core_risk_rules` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `product_id` int(11) DEFAULT NULL,
  `hit_type` int(11) DEFAULT NULL COMMENT '1表示必须通过的规则， 0 表示提示信审规则',
  `value_type` int(11) DEFAULT NULL COMMENT '1表示名单命中， 0 表示数值比较',
  `is_in` int(11) DEFAULT NULL COMMENT '1 表示必须在名单中，0 表示必须不在名单中才能通过规则',
  `range_left` int(11) DEFAULT NULL COMMENT '数值范围的左侧的类型1表示大于，0表示大于等于',
  `range_right` int(11) DEFAULT NULL COMMENT '数值范围的左侧的类型1表示小于，0表示小于等于',
  `variable_name` int(11) DEFAULT NULL COMMENT '变量名字',
  `credit_level` int(11) DEFAULT NULL COMMENT '信用等级',
  `val_left` float DEFAULT NULL COMMENT '左值',
  `val_right` float DEFAULT NULL COMMENT '右值',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table tb_core_risk_variables
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_core_risk_variables`;

CREATE TABLE `tb_core_risk_variables` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL COMMENT '变量名称',
  `value_type` int(11) DEFAULT NULL COMMENT '1表示名单   0表示数值',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table tb_credit_class
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_credit_class`;

CREATE TABLE `tb_credit_class` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL DEFAULT '' COMMENT '信用级别',
  `rate` float unsigned NOT NULL COMMENT '费率',
  `period` int(11) unsigned NOT NULL COMMENT '期限（天）',
  `quota` float DEFAULT NULL COMMENT '额度',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table tb_manual_repay
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_manual_repay`;

CREATE TABLE `tb_manual_repay` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `plan_id` int(10) NOT NULL COMMENT '还款计划id',
  `user_id` int(10) NOT NULL,
  `apply_id` int(10) NOT NULL,
  `seq_no` int(10) NOT NULL COMMENT '期数从1开始计数',
  `repay_start_date` datetime NOT NULL COMMENT '当前开始还款时间',
  `repay_end_date` datetime NOT NULL COMMENT '还款截止日期',
  `is_overdue` tinyint(4) NOT NULL COMMENT '是否逾期 0|没有 1|逾期',
  `need_total` bigint(20) NOT NULL COMMENT '应还总额',
  `act_total` bigint(20) NOT NULL COMMENT '实还总额',
  `trade_no` varchar(128) DEFAULT NULL COMMENT '交易id，防止一个图片被多次使用',
  `repay_image` varchar(128) NOT NULL COMMENT '还款图片',
  `status` tinyint(4) NOT NULL COMMENT '状态： 0|待处理  1|还款成功 2|还款失败',
  `create_time` datetime NOT NULL COMMENT '记录创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table tb_market_plan
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_market_plan`;

CREATE TABLE `tb_market_plan` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `topic` varchar(128) NOT NULL COMMENT 'mq topic:  user_business_state',
  `tag` varchar(128) NOT NULL COMMENT 'mq tag: new_regist, loan_succ, ...',
  `market_way` varchar(32) NOT NULL COMMENT 'sms/push/facebook',
  `market_ext` varchar(1024) NOT NULL COMMENT '{"msg": "hello"}',
  `priority` int(11) NOT NULL DEFAULT '100',
  `status` int(4) NOT NULL DEFAULT '0' COMMENT '0: 无效  1： 有效',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_topic` (`topic`),
  KEY `idx_tag` (`tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table tb_operation_task
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_operation_task`;

CREATE TABLE `tb_operation_task` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `apply_id` int(10) NOT NULL COMMENT '进件申请id',
  `operator_id` int(11) DEFAULT NULL COMMENT '操作者id',
  `distributor_id` int(10) DEFAULT NULL COMMENT '指派人id',
  `task_type` tinyint(4) NOT NULL COMMENT '任务类型，0:初审，1:复审，2:终审，3:逾期（未还）',
  `status` int(10) NOT NULL COMMENT '任务状态：0:未审，1:已审，2:回收',
  `comment` varchar(256) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '记录创建时间',
  `expire_time` datetime NOT NULL DEFAULT '2199-01-01 00:00:00' COMMENT '操作截止时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `has_owner` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否被指派或领取 0 不是 1 是',
  PRIMARY KEY (`id`),
  KEY `idx_apply_id` (`apply_id`),
  KEY `idx_operator_id` (`operator_id`),
  KEY `idx_task_type` (`task_type`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



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


# Dump of table tb_repay_material_info
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_repay_material_info`;

CREATE TABLE `tb_repay_material_info` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `user_id` int(10) NOT NULL COMMENT '关联用户id',
  `apply_id` varchar(32) NOT NULL DEFAULT '' COMMENT '还款订单号',
  `name` varchar(32) NOT NULL COMMENT '姓名',
  `mobile` varchar(32) NOT NULL DEFAULT '' COMMENT '联系方式',
  `att_1` varchar(128) NOT NULL DEFAULT '' COMMENT '还款凭证',
  `att_2` varchar(128) DEFAULT NULL COMMENT '还款凭证2',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '是否确认，0:待确认，1:确认，2:其他',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table tb_repay_plan
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_repay_plan`;

CREATE TABLE `tb_repay_plan` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '作为plan_id',
  `user_id` int(10) NOT NULL,
  `apply_id` int(10) NOT NULL,
  `product_id` int(10) NOT NULL,
  `seq_no` int(10) NOT NULL COMMENT '期数从1开始计数',
  `repay_start_date` datetime NOT NULL COMMENT '当前开始还款时间',
  `repay_end_date` datetime NOT NULL COMMENT '还款截止日期',
  `repay_time` datetime DEFAULT NULL COMMENT '还款时间，未还为空',
  `repay_status` tinyint(4) NOT NULL COMMENT '还款状态 0|未还  1|未还清 2|已还清 3|自助还款处理中 4|自助还款处理失败 5|核销',
  `is_overdue` tinyint(4) NOT NULL COMMENT '是否逾期 0|没有 1|逾期',
  `need_principal` bigint(20) NOT NULL COMMENT '应还本金',
  `act_principal` bigint(20) NOT NULL COMMENT '实还本金',
  `need_interest` bigint(20) NOT NULL COMMENT '应还利息',
  `act_interest` bigint(20) NOT NULL COMMENT '实还利息',
  `need_penalty_interest` bigint(20) NOT NULL COMMENT '应还罚息',
  `act_penalty_interest` bigint(20) NOT NULL COMMENT '实还罚息',
  `need_management_fee` bigint(20) NOT NULL COMMENT '应还管理费',
  `act_management_fee` bigint(20) NOT NULL COMMENT '实还管理费',
  `need_late_payment_fee` bigint(20) NOT NULL COMMENT '应还滞纳金',
  `act_late_payment_fee` bigint(20) NOT NULL COMMENT '实还滞纳金',
  `need_breach_fee` bigint(20) NOT NULL COMMENT '应还违约金',
  `act_breach_fee` bigint(20) NOT NULL COMMENT '实还违约金',
  `need_other` bigint(20) NOT NULL COMMENT '应还其他',
  `act_other` bigint(20) NOT NULL COMMENT '实还其他',
  `need_total` bigint(20) NOT NULL COMMENT '应还总额',
  `act_total` bigint(20) NOT NULL COMMENT '实还总额',
  `operator_id` int(11) DEFAULT '0' COMMENT '操作者id',
  `repay_code` varchar(256) DEFAULT NULL COMMENT '还款交易码',
  `repay_location` varchar(256) DEFAULT NULL COMMENT '还款便利店地址',
  `trade_number` varchar(256) DEFAULT NULL COMMENT '自动还款流水号',
  `expire_time` datetime NOT NULL COMMENT '还款交易码失效时间',
  `create_time` datetime NOT NULL COMMENT '记录创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_apply_id` (`apply_id`),
  KEY `idx_repay_status` (`repay_status`),
  KEY `idx_repay_start_date` (`repay_start_date`),
  KEY `idx_repay_end_date` (`repay_end_date`),
  KEY `idx_repay_time` (`repay_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table tb_repay_stat
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_repay_stat`;

CREATE TABLE `tb_repay_stat` (
  `apply_id` int(10) NOT NULL COMMENT 'apply_id 作为主键',
  `need_principal` bigint(20) NOT NULL COMMENT '应还本金',
  `act_principal` bigint(20) NOT NULL COMMENT '实还本金',
  `need_interest` bigint(20) NOT NULL COMMENT '应还利息',
  `act_interest` bigint(20) NOT NULL COMMENT '实还利息',
  `need_penalty_interest` bigint(20) NOT NULL COMMENT '应还罚息',
  `act_penalty_interest` bigint(20) NOT NULL COMMENT '实还罚息',
  `need_management_fee` bigint(20) NOT NULL COMMENT '应还管理费',
  `act_management_fee` bigint(20) NOT NULL COMMENT '实还管理费',
  `need_late_payment_fee` bigint(20) NOT NULL COMMENT '应还滞纳金',
  `act_late_payment_fee` bigint(20) NOT NULL COMMENT '实还滞纳金',
  `need_breach_fee` bigint(20) NOT NULL COMMENT '应还违约金',
  `act_breach_fee` bigint(20) NOT NULL COMMENT '实还违约金',
  `need_other` bigint(20) NOT NULL COMMENT '应还其他',
  `act_other` bigint(20) NOT NULL COMMENT '实还其他',
  `need_total` bigint(20) NOT NULL COMMENT '应还总额',
  `act_total` bigint(20) NOT NULL COMMENT '实还总额',
  `current_seq` int(10) NOT NULL COMMENT '当前期数',
  `normal_repay_times` int(10) NOT NULL COMMENT '正常还款期数',
  `overdue_repay_times` int(10) NOT NULL COMMENT '逾期还款期数',
  `overdue_times` int(10) NOT NULL COMMENT '逾期期数',
  `create_time` datetime NOT NULL COMMENT '记录创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`apply_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



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


# Dump of table tb_user_sns_info
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_user_sns_info`;

CREATE TABLE `tb_user_sns_info` (
  `id` int(12) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(32) NOT NULL DEFAULT '' COMMENT '用户id',
  `type` int(11) NOT NULL DEFAULT '0' COMMENT 'SNS类型，0:facebook，1:Line，2:Zalo，3:Skype，4:Ins，5:Viber',
  `name` varchar(128) NOT NULL DEFAULT '1' COMMENT 'SNS登陆账号',
  `password` varchar(32) NOT NULL DEFAULT '' COMMENT 'SNS登陆密码',
  `create_time` datetime NOT NULL COMMENT '注册时间',
  PRIMARY KEY (`id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table tp_app_sdk_contract_info
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tp_app_sdk_contract_info`;

CREATE TABLE `tp_app_sdk_contract_info` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `device_id` varchar(255) DEFAULT NULL COMMENT '设备号',
  `mobile` varchar(255) DEFAULT NULL COMMENT '手机号',
  `contract_name` varchar(255) DEFAULT NULL COMMENT '通讯录姓名',
  `contract_info` varchar(255) DEFAULT NULL COMMENT '通讯录联系系信息',
  `contract_memo` varchar(255) DEFAULT NULL COMMENT '通讯录备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '客户端上传时间',
  PRIMARY KEY (`id`),
  KEY `idx_mobile` (`mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

# ************************************************************
# Sequel Pro SQL dump
# Version 4541
#
# http://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: 140.143.207.211 (MySQL 5.6.45)
# Database: test
# Generation Time: 2019-10-23 09:07:21 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


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



# Dump of table tb_cms_auth_resource
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_cms_auth_resource`;

CREATE TABLE `tb_cms_auth_resource` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(50) DEFAULT NULL COMMENT '名称',
  `url` varchar(255) DEFAULT NULL COMMENT 'url路径',
  `comment` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `level1` varchar(50) DEFAULT NULL,
  `level2` varchar(50) DEFAULT NULL,
  `level3` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `tb_cms_auth_resource` WRITE;
/*!40000 ALTER TABLE `tb_cms_auth_resource` DISABLE KEYS */;

INSERT INTO `tb_cms_auth_resource` (`id`, `name`, `url`, `comment`, `create_time`, `level1`, `level2`, `level3`)
VALUES
	(1,'/order/approval','','进件审批','2019-10-07 18:12:10',NULL,NULL,NULL),
	(2,'/order/manage','','进件管理','2019-10-07 18:13:05',NULL,NULL,NULL),
	(3,'/order/history','','指派历史','2019-10-07 18:13:43',NULL,NULL,NULL),
	(4,'/loan/index','','贷款管理','2019-10-07 18:14:35',NULL,NULL,NULL),
	(5,'/loan/repayment','','已还款管理','2019-10-07 18:15:15',NULL,NULL,NULL),
	(6,'/loan/unpaid','','未还款管理','2019-10-07 18:15:39',NULL,NULL,NULL),
	(7,'/postLoan/overdueManage','','逾期管理','2019-10-07 18:17:08',NULL,NULL,NULL),
	(8,'/overdueLoanCollection/myTasks','','我的催收','2019-10-10 12:00:37',NULL,NULL,NULL),
	(9,'/overdueLoanCollection/assignTasks','','逾期派单','2019-10-10 12:01:09',NULL,NULL,NULL),
	(10,'/overdueLoanCollection/archives','','催收档案','2019-10-10 12:01:53',NULL,NULL,NULL),
	(11,'/customer','','客户管理','2019-10-10 12:02:51',NULL,NULL,NULL),
	(12,'/products','','产品管理','2019-10-10 12:04:15',NULL,NULL,NULL),
	(13,'/assets','','资产分级管理','2019-10-10 12:06:18',NULL,NULL,NULL),
	(14,'/creditLevel','','信用等级管理','2019-10-10 12:07:26',NULL,NULL,NULL),
	(15,'/riskRules','','风险规则管理','2019-10-10 12:08:03',NULL,NULL,NULL),
	(16,'/dashboard','','首页','2019-10-10 12:09:04',NULL,NULL,NULL),
	(17,'/user/roleAuth','','角色管理','2019-10-10 12:09:42',NULL,NULL,NULL),
	(18,'/user/manage','','用户管理','2019-10-10 12:10:18',NULL,NULL,NULL),
	(19,'/user/password','','修改密码','2019-10-10 12:10:36',NULL,NULL,NULL),
	(20,'/report/operationReport','','操作日志','2019-10-22 22:25:13',NULL,NULL,NULL),
	(21,'/report/checkReport','','检查日志','2019-10-22 22:25:32',NULL,NULL,NULL),
	(22,'/label','','标签管理','2019-10-22 23:24:52',NULL,NULL,NULL);

/*!40000 ALTER TABLE `tb_cms_auth_resource` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table tb_cms_auth_role
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_cms_auth_role`;

CREATE TABLE `tb_cms_auth_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(50) DEFAULT NULL COMMENT '角色名',
  `comment` varchar(255) DEFAULT NULL COMMENT '备注',
  `is_valid` int(11) DEFAULT '1',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `tb_cms_auth_role` WRITE;
/*!40000 ALTER TABLE `tb_cms_auth_role` DISABLE KEYS */;

INSERT INTO `tb_cms_auth_role` (`id`, `name`, `comment`, `is_valid`, `create_time`, `update_time`)
VALUES
	(1,'admin','测试admin',1,'2019-10-07 23:07:37',NULL),
	(2,'test','test用户',1,'2019-10-08 03:03:48',NULL),
	(3,'ceshi','ceshi-1630',1,'2019-10-08 03:30:53',NULL);

/*!40000 ALTER TABLE `tb_cms_auth_role` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table tb_cms_auth_role_resource
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_cms_auth_role_resource`;

CREATE TABLE `tb_cms_auth_role_resource` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) DEFAULT NULL,
  `resource_id` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `tb_cms_auth_role_resource` WRITE;
/*!40000 ALTER TABLE `tb_cms_auth_role_resource` DISABLE KEYS */;

INSERT INTO `tb_cms_auth_role_resource` (`id`, `role_id`, `resource_id`, `create_time`)
VALUES
	(15,2,2,'2019-10-08 03:03:48'),
	(16,2,6,'2019-10-08 03:03:48'),
	(17,3,5,'2019-10-08 03:30:53'),
	(18,3,6,'2019-10-08 03:30:53'),
	(96,1,1,'2019-10-22 23:29:54'),
	(97,1,2,'2019-10-22 23:29:54'),
	(98,1,3,'2019-10-22 23:29:54'),
	(99,1,4,'2019-10-22 23:29:54'),
	(100,1,5,'2019-10-22 23:29:54'),
	(101,1,6,'2019-10-22 23:29:55'),
	(102,1,7,'2019-10-22 23:29:55'),
	(103,1,8,'2019-10-22 23:29:55'),
	(104,1,9,'2019-10-22 23:29:55'),
	(105,1,10,'2019-10-22 23:29:55'),
	(106,1,11,'2019-10-22 23:29:55'),
	(107,1,12,'2019-10-22 23:29:55'),
	(108,1,14,'2019-10-22 23:29:55'),
	(109,1,15,'2019-10-22 23:29:55'),
	(110,1,16,'2019-10-22 23:29:55'),
	(111,1,17,'2019-10-22 23:29:55'),
	(112,1,18,'2019-10-22 23:29:55'),
	(113,1,19,'2019-10-22 23:29:55'),
	(114,1,13,'2019-10-22 23:29:55'),
	(115,1,20,'2019-10-22 23:29:56'),
	(116,1,21,'2019-10-22 23:29:56'),
	(117,1,22,'2019-10-22 23:29:56');

/*!40000 ALTER TABLE `tb_cms_auth_role_resource` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table tb_cms_auth_user
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_cms_auth_user`;

CREATE TABLE `tb_cms_auth_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_name` varchar(50) DEFAULT NULL COMMENT '用户名',
  `password` varchar(100) DEFAULT NULL COMMENT '密码',
  `name` varchar(50) DEFAULT NULL COMMENT '姓名',
  `mobile` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `comment` varchar(255) DEFAULT NULL COMMENT '备注',
  `is_valid` int(11) DEFAULT '1' COMMENT '是否生效',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `tb_cms_auth_user` WRITE;
/*!40000 ALTER TABLE `tb_cms_auth_user` DISABLE KEYS */;

INSERT INTO `tb_cms_auth_user` (`id`, `user_name`, `password`, `name`, `mobile`, `email`, `comment`, `is_valid`, `create_time`, `update_time`)
VALUES
	(1,'admin','e10adc3949ba59abbe56e057f20f883e','管理员','18611111112','22@email.com',NULL,1,'2019-10-08 01:46:38','2019-10-08 01:56:14'),
	(2,'test','e10adc3949ba59abbe56e057f20f883e','测试用户','18611111111','333@email.com',NULL,1,'2019-10-08 03:54:05',NULL);

/*!40000 ALTER TABLE `tb_cms_auth_user` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table tb_cms_auth_user_role
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_cms_auth_user_role`;

CREATE TABLE `tb_cms_auth_user_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `role_id` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `tb_cms_auth_user_role` WRITE;
/*!40000 ALTER TABLE `tb_cms_auth_user_role` DISABLE KEYS */;

INSERT INTO `tb_cms_auth_user_role` (`id`, `user_id`, `role_id`, `create_time`)
VALUES
	(6,1,1,'2019-10-08 03:55:10'),
	(7,2,1,'2019-10-14 08:57:19');

/*!40000 ALTER TABLE `tb_cms_auth_user_role` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table tb_cms_collection_allocate_record
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_cms_collection_allocate_record`;

CREATE TABLE `tb_cms_collection_allocate_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `apply_id` int(11) DEFAULT NULL COMMENT '进件id',
  `action_time` datetime DEFAULT NULL COMMENT '指派时间',
  `collector_id` int(11) DEFAULT NULL COMMENT '催收人id',
  `distributor_id` int(11) DEFAULT NULL COMMENT '指派人id',
  `collector_name` varchar(255) DEFAULT NULL COMMENT '催收人姓名',
  `distributor_name` varchar(255) DEFAULT NULL COMMENT '指派人姓名',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `tb_cms_collection_allocate_record` WRITE;
/*!40000 ALTER TABLE `tb_cms_collection_allocate_record` DISABLE KEYS */;

INSERT INTO `tb_cms_collection_allocate_record` (`id`, `apply_id`, `action_time`, `collector_id`, `distributor_id`, `collector_name`, `distributor_name`, `create_time`)
VALUES
	(1,4,'2019-10-11 02:40:25',2,1,'测试用户','管理员','2019-10-11 02:40:25'),
	(2,4,'2019-10-11 03:17:15',1,1,'管理员','管理员',NULL);

/*!40000 ALTER TABLE `tb_cms_collection_allocate_record` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table tb_cms_collection_record
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_cms_collection_record`;

CREATE TABLE `tb_cms_collection_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `mobile` varchar(20) DEFAULT NULL COMMENT '催收电话',
  `status` varchar(255) DEFAULT NULL COMMENT '催收状态',
  `alert_date` datetime DEFAULT NULL COMMENT '提醒日期',
  `comment` varchar(255) DEFAULT NULL COMMENT '催收备注',
  `apply_id` int(40) DEFAULT NULL COMMENT '进件id',
  `periods` varchar(255) DEFAULT NULL COMMENT '期次 预留',
  `operator_id` int(40) DEFAULT NULL COMMENT '催收人id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `tb_cms_collection_record` WRITE;
/*!40000 ALTER TABLE `tb_cms_collection_record` DISABLE KEYS */;

INSERT INTO `tb_cms_collection_record` (`id`, `mobile`, `status`, `alert_date`, `comment`, `apply_id`, `periods`, `operator_id`, `create_time`)
VALUES
	(1,'18611111111','0','2019-10-10 19:00:00','测试备注',4,NULL,1,NULL);

/*!40000 ALTER TABLE `tb_cms_collection_record` ENABLE KEYS */;
UNLOCK TABLES;


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




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;


SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;


-- Create syntax for TABLE 'tb_app_version'
DROP TABLE tb_app_version;
CREATE TABLE if NOT EXISTS `tb_app_version` (
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

-- Create syntax for TABLE 'tb_operation_task'
DROP TABLE tb_operation_task;
CREATE TABLE if NOT EXISTS `tb_operation_task` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `apply_id` int(10) NOT NULL COMMENT '进件申请id',
  `operator_id` int(11) DEFAULT NULL COMMENT '操作者id',
  `distributor_id` int(10) DEFAULT NULL COMMENT '指派人id',
  `task_type` tinyint NOT NULL COMMENT '任务类型，0:初审，1:复审，2:终审，3:逾期（未还）',
  `status` int(10) NOT NULL COMMENT '任务状态：0:未审，1:已审，2:回收',
  `has_owner` tinyint NOT NULL DEFAULT 0 COMMENT '是否被指派或领取 0 不是 1 是',
  `comment` varchar(256) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '记录创建时间',
  `expire_time` datetime NOT NULL DEFAULT '2199-01-01 00:00:00' COMMENT '操作截止时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_apply_id` (`apply_id`),
  KEY `idx_operator_id` (`operator_id`),
  KEY `idx_task_type` (`task_type`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE tb_operation_task_history;
CREATE TABLE IF NOT EXISTS `tb_operation_task_history` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `apply_id` int(10) NOT NULL COMMENT '进件申请id',
  `operator_id` int(11) DEFAULT NULL COMMENT '操作者id',
  `distributor_id` int(10) DEFAULT NULL COMMENT '指派人id',
  `task_type` tinyint(4) NOT NULL COMMENT '任务类型，0:初审，1:复审，2:终审，3:逾期（未还）',
  `status` int(10) NOT NULL COMMENT '任务状态：0:未审，1:已审，2:回收',
  `has_owner` int(11) NOT NULL DEFAULT '0' COMMENT '是否被指派或领取 0 不是 1 是',
  `comment` varchar(256) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '记录创建时间',
  `expire_time` datetime NOT NULL DEFAULT '2199-01-01 00:00:00' COMMENT '操作截止时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_apply_id` (`apply_id`),
  KEY `idx_operator_id` (`operator_id`),
  KEY `idx_task_type` (`task_type`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Create syntax for TABLE 'tb_apply_info'
DROP TABLE tb_apply_info;
CREATE TABLE if NOT EXISTS `tb_apply_info` (
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
  `fee_type` int(16) NOT NULL COMMENT '服务费收取方式，0:先扣除服务费，1:先扣除服务费和利息，2:到期扣除服务费和利息',
  `status` int(10) NOT NULL COMMENT '进件最新状态, 0:待审核, 1:自动审核通过, 2:初审通过, 3:复审通过, 4:终审通过, 5:自动审核拒绝, 6:初审拒绝, 7:复审拒绝, 8:终审拒绝, 9:取消或异常, 10:自动放款中，11:自动放款失败，12:已放款/还款中，13:未还清，14:已还清， 15:逾期，16:核销',
  `operator_id` int(11) DEFAULT NULL COMMENT '最后操作者id',
  `apply_quota` int(10) NOT NULL COMMENT '用户实际申请的额度',
  `grant_quota` int(10) DEFAULT '0' COMMENT '授予额度',
  `remain_quota` int(10) DEFAULT '0' COMMENT '剩余可用额度',
  `inhand_quota` int(10) DEFAULT '0' COMMENT '到手金额',
  `credit_class` varchar(8) DEFAULT NULL COMMENT '用户的信用评级，没到这一步就为空',
  `deny_code` varchar(8) DEFAULT NULL COMMENT '拒贷码，根据阶段不一样，取值也不一样',
  `comment` varchar(256) DEFAULT NULL COMMENT '备注',
  `order_number` varchar(64) DEFAULT NULL COMMENT '自动放款订单号，唯一',
  `trade_number` varchar(64) DEFAULT NULL COMMENT '自动放款流水号',
  `asset_level` int(8)  DEFAULT  NULL COMMENT '资产等级，0',
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

-- Create syntax for TABLE 'tb_apply_info_history'
DROP TABLE tb_apply_info_history;
CREATE TABLE if NOT EXISTS `tb_apply_info_history` (
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

-- Create syntax for TABLE 'tb_apply_material_info'
DROP TABLE tb_apply_material_info;
CREATE TABLE if NOT EXISTS `tb_apply_material_info` (
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

-- Create syntax for TABLE 'tb_channel_info'
DROP TABLE tb_channel_info;
CREATE TABLE if NOT EXISTS `tb_channel_info` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '',
  `channelId` int(11) NOT NULL COMMENT '自定义渠道id',
  `type` varchar(32) NOT NULL DEFAULT '' COMMENT '渠道类型',
  `name` varchar(32) NOT NULL DEFAULT '' COMMENT '渠道名称',
  `status` int(10) NOT NULL DEFAULT 0 COMMENT '渠道状态, 0:offline 1:online',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_name` (`name`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Create syntax for TABLE 'tb_cms_assign_history'
DROP TABLE tb_cms_assign_history;
CREATE TABLE if NOT EXISTS `tb_cms_assign_history` (
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

-- Create syntax for TABLE 'tb_cms_customer_info'
DROP TABLE tb_cms_customer_info;
CREATE TABLE if NOT EXISTS `tb_cms_customer_info` (
  `ID` int(11) NOT NULL COMMENT '主键',
  `NAME` varchar(255) DEFAULT NULL COMMENT '姓名',
  `ID_NUMBER` varchar(20) DEFAULT NULL COMMENT '身份证',
  `PHONE_NUMBER` int(11) DEFAULT NULL COMMENT '手机号',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '注册时间',
  `MODIFY_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Create syntax for TABLE 'tb_cms_input_manage'
DROP TABLE tb_cms_input_manage;
CREATE TABLE if NOT EXISTS `tb_cms_input_manage` (
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

-- Create syntax for TABLE 'tb_cms_plan_manage'
DROP TABLE tb_cms_plan_manage;
CREATE TABLE if NOT EXISTS `tb_cms_plan_manage` (
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

-- Create syntax for TABLE 'tb_cms_sampling_history'
DROP TABLE tb_cms_sampling_history;
CREATE TABLE if NOT EXISTS `tb_cms_sampling_history` (
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

-- Create syntax for TABLE 'tb_cms_task_manage'
DROP TABLE tb_cms_task_manage;
CREATE TABLE if NOT EXISTS `tb_cms_task_manage` (
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

-- Create syntax for TABLE 'tb_credit_class'
DROP TABLE tb_credit_class;
CREATE TABLE if NOT EXISTS `tb_credit_class` (
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

-- Create syntax for TABLE 'tb_product_info'
DROP TABLE IF EXISTS tb_product_info;
CREATE TABLE if NOT EXISTS `tb_product_info` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '产品id',
  `name` varchar(32) NOT NULL COMMENT '产品名称',
  `product_desc` varchar(256) NOT NULL DEFAULT '' COMMENT '产品描述',
  `status` int(11) NOT NULL DEFAULT 0 COMMENT '产品状态, 0:offline, 1:online',
  `rate` float unsigned NOT NULL COMMENT '产品费率',
  `min_period` int(11) unsigned NOT NULL COMMENT '最小产品期限',
  `max_period` int(11) unsigned NOT NULL COMMENT '最大产品期限',
  `min_quota` int(11) DEFAULT 0 COMMENT '最小额度',
  `max_quota` int(11) DEFAULT 0 COMMENT '最大额度',
  `period_type` int(11) NOT NULL DEFAULT 0 COMMENT '产品期限单位，0:天，1:月，2:年',
  `value_date_type` int(11) NOT NULL DEFAULT 0 COMMENT '起息日方式，0:到账后计息，1:终审通过后计息',
  `fee` float unsigned NOT NULL COMMENT '产品服务费',
  `fee_type` int(11) unsigned NOT NULL COMMENT '服务费收取方式，0:先扣除服务费，1:先扣除服务费和利息，2:到期扣除服务费和利息',
  `overdue_rate` float unsigned NOT NULL COMMENT '逾期日费费',
  `grace_period` int(11) DEFAULT 0 COMMENT '宽限期',
  `product_order` int(11) DEFAULT 0 COMMENT '排列顺序',
  `credit_level` int(11) DEFAULT 0 COMMENT '信用等级',
  `material_needed` varchar(128) NOT NULL DEFAULT '' COMMENT '所需资料',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Create syntax for TABLE 'tb_repay_material_info'
DROP TABLE tb_repay_material_info;
CREATE TABLE if NOT EXISTS `tb_repay_material_info` (
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

-- Create syntax for TABLE 'tb_repay_plan'
DROP TABLE tb_repay_plan;
CREATE TABLE if NOT EXISTS `tb_repay_plan` (
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
  `reduction_fee` bigint(20) DEFAULT 0 COMMENT '减免费用',
  `need_total` bigint(20) NOT NULL COMMENT '应还总额',
  `act_total` bigint(20) NOT NULL COMMENT '实还总额',
  `operator_id` int(11) DEFAULT 0 COMMENT '操作者id',
  `repay_code` varchar(256) DEFAULT NULL COMMENT '还款交易码',
  `repay_location` varchar(256) DEFAULT NULL COMMENT '还款便利店地址',
  `trade_number` varchar(256) DEFAULT NULL COMMENT '自动还款流水号',
  `expire_time` datetime DEFAULT NULL COMMENT '还款交易码失效时间',
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

-- Create syntax for TABLE 'tb_repay_stat'
DROP TABLE tb_repay_stat;
CREATE TABLE if NOT EXISTS `tb_repay_stat` (
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
  `reduction_fee` bigint(20) DEFAULT 0 COMMENT '减免费用',
  `need_total` bigint(20) NOT NULL COMMENT '应还总额',
  `act_total` bigint(20) NOT NULL COMMENT '实还总额',
  `normal_repay` bigint(20) NOT NULL DEFAULT 0 COMMENT '正常还款总额，逾期后还款不计入内',
  `current_seq` int(10) NOT NULL  COMMENT '当前期数',
  `normal_repay_times` int(10) NOT NULL COMMENT '正常还款期数',
  `overdue_repay_times` int(10) NOT NULL COMMENT '逾期还款期数',
  `overdue_times` int(10) NOT NULL COMMENT '逾期期数',
  `overdue_days` int(10) NOT NULL DEFAULT 0 COMMENT '逾期天数，从最近逾期算起',
  `overdue_days_max` int(10) NOT NULL DEFAULT 0 COMMENT '最大逾期天数',
  `create_time` datetime NOT NULL COMMENT '记录创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`apply_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Create syntax for TABLE 'tb_user_bank_account_info'
DROP TABLE tb_user_bank_account_info;
CREATE TABLE if NOT EXISTS `tb_user_bank_account_info` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `info_id` varchar(32) NOT NULL COMMENT '资料标识id',
  `user_id` int(10) NOT NULL COMMENT '关联用户id',
  `account_type` tinyint(4) NOT NULL COMMENT '账户类型  0|借记卡 1|信用卡',
  `name` varchar(64) NOT NULL COMMENT '持卡人姓名',
  `bank` int(10) NOT NULL COMMENT '银行名称 0|xx_bank  1|yy_bank',
  `account_id` varchar(128) NOT NULL COMMENT '银行账号',
  `create_time` datetime NOT NULL,
  `expire_time` datetime NOT NULL DEFAULT '2199-01-01 00:00:00' COMMENT '有效期',
  PRIMARY KEY (`id`),
  KEY `idx_info_id` (`info_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Create syntax for TABLE 'tb_user_basic_info'
DROP TABLE tb_user_basic_info;
CREATE TABLE IF NOT EXISTS `tb_user_basic_info` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `info_id` varchar(32) NOT NULL COMMENT '资料标识id',
  `user_id` int(10) NOT NULL COMMENT '关联用户id',
  `medical_insurance_pic` varchar(128) default '' comment '医保证件照片',
  `refund_proof_pic` varchar(128) default '' comment '还款证明照片',
  `driving_license_pic` varchar(128) default '' comment '驾照照片',
  `education` tinyint(4) NOT NULL COMMENT '学历   0|小学 1|初中 2|高中 3|中间 4|学院 5|综合性大学 6|大学后',
  `marriage` tinyint(4) DEFAULT null COMMENT '婚姻状态    0|已婚 1|单身 2|离异 3|丧偶',
  `children_count` tinyint(4) DEFAULT null COMMENT '子女个数',
  `residence_province` int(10) NOT NULL COMMENT '省',
  `residence_city` int(10) NOT NULL  COMMENT '城市',
  `residence_country` int(10) NOT NULL  COMMENT '县',
  `residence_addr` varchar(256) NOT NULL COMMENT '详细居住地址',
  `residence_duration` tinyint(4) DEFAULT null COMMENT '居住时长 0|3个月  1|6个月',
  `purpose` tinyint(4) DEFAULT null COMMENT '用途 0|旅游  1|买车',
  `purpose_other` varchar(128) DEFAULT '' COMMENT '用途其他 如果需要用户填写',
  `zalo_id` varchar(64) DEFAULT '' COMMENT 'zalo id',
  `facebook_link` varchar(256) not NULL COMMENT 'facebook_link',
  `age` tinyint(4) DEFAULT '0' COMMENT '年龄',
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

DROP TABLE IF EXISTS tb_install_click_info;
CREATE TABLE IF NOT EXISTS `tb_install_click_info`(
  `id` int(10) not null auto_increment,
  `install_referrer` varchar(256),
  `referrer_click_date` datetime,
  `install_begin_date` datetime,
  `deviceid` varchar(256),
  `mobile` varchar(16),
  primary key(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Create syntax for TABLE 'tb_user_citizen_identity_card_info'
DROP TABLE tb_user_citizen_identity_card_info;
CREATE TABLE if NOT EXISTS `tb_user_citizen_identity_card_info` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `info_id` varchar(32) NOT NULL COMMENT '资料标识id',
  `user_id` int(10) NOT NULL COMMENT '关联用户id',
  `name` varchar(32) NOT NULL COMMENT '姓名',
  `cid_no` varchar(64) NOT NULL COMMENT '身份证号',
  `gender` tinyint(4) NOT NULL COMMENT '性别  0|男  1|女',
  `birthday` datetime NOT NULL,
  `age` int(10) NOT NULL,
  `pic_1` varchar(128) NOT NULL COMMENT '照片1',
  `pic_2` varchar(128) NOT NULL COMMENT '照片2',
  `pic_3` varchar(128) NOT NULL COMMENT '照片3',
  `pic_4` varchar(128) DEFAULT '' COMMENT '照片4',
  `create_time` datetime NOT NULL,
  `expire_time` datetime NOT NULL DEFAULT '2199-01-01 00:00:00' COMMENT '有效期',
  PRIMARY KEY (`id`),
  KEY `idx_info_id` (`info_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Create syntax for TABLE 'tb_user_emergency_contact'
DROP TABLE tb_user_emergency_contact;
CREATE TABLE if NOT EXISTS `tb_user_emergency_contact` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `info_id` varchar(32) NOT NULL COMMENT '资料标识id',
  `user_id` int(10) NOT NULL COMMENT '关联用户id',
  `relationship` tinyint(4) not null COMMENT '关系 0|父母 1|同事',
  `name` varchar(64) NOT NULL COMMENT '紧急联系人姓名',
  `mobile` varchar(32) NOT NULL COMMENT '紧急联系人电话',
  `create_time` datetime NOT NULL,
  `expire_time` datetime NOT NULL DEFAULT '2199-01-01 00:00:00' COMMENT '有效期',
  PRIMARY KEY (`id`),
  KEY `idx_info_id` (`info_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Create syntax for TABLE 'tb_user_employment_info'
DROP TABLE tb_user_employment_info;
CREATE TABLE if NOT EXISTS `tb_user_employment_info` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `info_id` varchar(32) NOT NULL COMMENT '资料标识id',
  `user_id` int(10) NOT NULL COMMENT '关联用户id',
  `company` varchar(128) NOT NULL COMMENT '任职公司名称',
  `company_province` int(10) default null COMMENT '省',
  `company_city` int(10) default null COMMENT '城市',
  `company_country` int(10) default null COMMENT '县城',
  `company_addr` varchar(256) default null COMMENT '公司详细地址',
  `phone` varchar(32) NOT NULL COMMENT '公司联系电话',
  `job_occupation` tinyint(4) NOT NULL COMMENT '职业类型 0|工程师  1|服务行业',
  `work_period` tinyint(4) default null COMMENT '工作时长',
  `income` tinyint(4) default null COMMENT '收入状态 0|1~100  1|100~1000',
  `work_pic` varchar(128) default NULL COMMENT '工作照片',
  `create_time` datetime NOT NULL,
  `expire_time` datetime NOT NULL DEFAULT '2199-01-01 00:00:00' COMMENT '有效期',
  PRIMARY KEY (`id`),
  KEY `idx_info_id` (`info_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Create syntax for TABLE 'tb_user_regist_info'
DROP TABLE tb_user_regist_info;
CREATE TABLE if NOT EXISTS `tb_user_regist_info` (
  `id` int(12) NOT NULL AUTO_INCREMENT COMMENT '作为用户id使用',
  `mobile` varchar(32) NOT NULL COMMENT '登陆手机号',
  `name` varchar(64) COMMENT '用户姓名，在验证后补充',
  `type` int(11) NOT NULL DEFAULT '0' COMMENT '用户类型，0:借款用户',
  `credit_level` int(11) DEFAULT 0 COMMENT '用户信用等级，根据这个找到匹配的产品',
  `status` int(11) NOT NULL DEFAULT '1' COMMENT '用户状态，1:正常',
  `channel_id` int(11) DEFAULT NULL COMMENT '注册渠道id',
  `promotion_info` varchar(512) DEFAULT NULL COMMENT '推广信息',
  `create_time` datetime NOT NULL COMMENT '注册时间',
  PRIMARY KEY (`id`),
  KEY `idx_mobile` (`mobile`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_channel_id` (`channel_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1001 DEFAULT CHARSET=utf8;

-- Create syntax for TABLE 'tb_user_sns_info'
DROP TABLE tb_user_sns_info;
CREATE TABLE if NOT EXISTS `tb_user_sns_info` (
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

DROP TABLE IF EXISTS tb_app_sdk_location_info;
CREATE TABLE if NOT EXISTS `tb_app_sdk_location_info` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `info_id` varchar(32) NOT NULL DEFAULT '' COMMENT '资料标识id',
  `apply_long` varchar(32) DEFAULT NULL COMMENT 'gps经度',
  `apply_lat` varchar(32) DEFAULT NULL COMMENT 'gps纬度',
  `device_id` varchar(255) DEFAULT NULL COMMENT '设备号',
  `mobile` varchar(255) DEFAULT NULL COMMENT '手机号',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '客户端上传时间',
  PRIMARY KEY (`id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_mobile` (`mobile`),
  KEY `idx_info_id` (`info_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS tb_app_sdk_contract_info;
CREATE TABLE if NOT EXISTS `tb_app_sdk_contract_info` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `info_id` varchar(32) NOT NULL DEFAULT '' COMMENT '资料标识id',
  `device_id` varchar(255) DEFAULT NULL COMMENT '设备号',
  `mobile` varchar(255) DEFAULT NULL COMMENT '手机号',
  `contract_name` varchar(255) DEFAULT NULL COMMENT '通讯录姓名',
  `contract_info` varchar(255) DEFAULT NULL COMMENT '通讯录联系系信息',
  `contract_memo` varchar(255) DEFAULT NULL COMMENT '通讯录备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '客户端上传时间',
  PRIMARY KEY (`id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_mobile` (`mobile`),
  KEY `idx_info_id` (`info_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS tb_app_sdk_applist_info;
CREATE TABLE if NOT EXISTS `tb_app_sdk_applist_info` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `info_id` varchar(32) NOT NULL DEFAULT '' COMMENT '资料标识id',
  `device_id` varchar(255) DEFAULT NULL COMMENT '设备号',
  `mobile` varchar(255) DEFAULT NULL COMMENT '手机号',
  `apk_name` varchar(255) DEFAULT NULL COMMENT 'app包名',
  `apk_label` varchar(255) DEFAULT NULL COMMENT 'applabel信息',
  `install_time` datetime DEFAULT NULL COMMENT '安装时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '客户端上传时间',
  PRIMARY KEY (`id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_mobile` (`mobile`),
  KEY `idx_info_id` (`info_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




DROP TABLE IF EXISTS tb_core_risk_rules;
CREATE TABLE if NOT EXISTS `tb_core_risk_rules` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `product_id` int(11) DEFAULT NULL,
  `hit_type` int(11) DEFAULT NULL COMMENT '1表示必须通过的规则， 0 表示提示信审规则',
  `value_type` int(11) DEFAULT NULL COMMENT '1表示名单命中， 0 表示数值比较',
  `is_in` int(11) DEFAULT NULL COMMENT '1 表示必须在名单中，0 表示必须不在名单中才能通过规则',
  `range_left` int(11) DEFAULT NULL COMMENT '数值范围的左侧的类型1表示大于，0表示大于等于',
  `range_right` int(11) DEFAULT NULL COMMENT '数值范围的左侧的类型1表示小于，0表示小于等于',
  `variable_name` varchar(255) DEFAULT NULL COMMENT '变量名字',
  `credit_level` int(11) DEFAULT NULL COMMENT '信用等级',
  `val_left` float DEFAULT NULL COMMENT '左值',
  `val_right` float DEFAULT NULL COMMENT '右值',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS tb_core_credit_level_rules;
CREATE TABLE if NOT EXISTS `tb_core_credit_level_rules` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `reloan_times` int(11) DEFAULT NULL COMMENT '复贷次数',
  `max_overdue_days` int(11) DEFAULT NULL COMMENT '历史最大逾期天数',
  `level` int(11) DEFAULT NULL COMMENT '信用等级',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS tb_core_risk_decesion_result;
CREATE TABLE if NOT EXISTS `tb_core_risk_decesion_result` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `apply_time` datetime DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `apply_id` int(11) DEFAULT NULL,
  `product_id` int(11) DEFAULT NULL,
  `ret` int(11) DEFAULT NULL COMMENT '1表示拒绝 0  表示通过',
  `refuse_code` varchar(255) DEFAULT NULL COMMENT '拒贷码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS tb_core_risk_decesion_result_detail;
CREATE TABLE if NOT EXISTS `tb_core_risk_decesion_result_detail` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `rule_id` int(11) DEFAULT NULL,
  `rule_status` int(11) DEFAULT NULL,
  `rule_hit_type` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `product_id` int(11) DEFAULT NULL,
  `apply_id` int(11) DEFAULT NULL,
  `apply_date` datetime DEFAULT NULL,
  `decesion_id` int(11) DEFAULT NULL,
  `val` double DEFAULT NULL,
  `variable` varchar(128)  DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS tb_core_risk_variables;
CREATE TABLE if NOT EXISTS `tb_core_risk_variables` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL COMMENT '变量名称',
  `value_type` int(11) DEFAULT NULL COMMENT '1表示名单   0表示数值',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS tb_market_plan;
CREATE TABLE IF NOT EXISTS `tb_market_plan` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `topic` varchar(128) NOT NULL COMMENT 'mq topic:  user_business_state',
  `tag` varchar(128) NOT NULL COMMENT 'mq tag: new_regist, loan_succ, ...',
  `market_way` varchar(32) NOT NULL COMMENT 'sms/push/facebook',
  `market_ext` varchar(1024) NOT NULL COMMENT '{"msg": "hello"}',
  `priority` int(11) DEFAULT 100 NOT NULL,
  `status` int(4) DEFAULT 0 NOT NULL COMMENT '0: 无效  1： 有效',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_topic` (`topic`),
  KEY `idx_tag` (`tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS tb_manual_repay;
CREATE TABLE IF NOT EXISTS `tb_manual_repay` (
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
  `trade_no` varchar(128) COMMENT '交易id，防止一个图片被多次使用',
  `repay_image` varchar(128) NOT NULL COMMENT '还款图片',
  `status` tinyint NOT NULL COMMENT '状态： 0|待处理  1|还款成功 2|还款失败',
  `create_time` datetime NOT NULL COMMENT '记录创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS tb_core_assets_level_rules;
CREATE TABLE if NOT EXISTS `tb_core_assets_level_ruels` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `between_paydays` int(11) DEFAULT NULL COMMENT '距离还款日的天数',
  `level` int(11) DEFAULT NULL COMMENT '资产等级',
  `level_name` varchar(128) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS tb_core_comment_label;
CREATE TABLE IF NOT EXISTS `tb_core_comment_label` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `content` text,
  `label_name` text,
  `creator` int(11) DEFAULT NULL,
  `creat_time` datetime DEFAULT NULL,
  `scene` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS tb_repay_history;
CREATE TABLE IF NOT EXISTS `tb_repay_history` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `user_id` int(10) NOT NULL,
  `apply_id` int(10) NOT NULL,
  `repay_plan_id` int(10) DEFAULT NULL COMMENT '还款计划id',
  `repay_time` datetime DEFAULT NULL COMMENT '还款时间',
  `repay_amount` bigint(20) NOT NULL COMMENT '还款金额',
  `operator_id` int(11) DEFAULT '0' COMMENT '操作人id',
  `repay_status` int(11) DEFAULT NULL COMMENT '还款状态 0:处理中 1:还款成功 2:还款失败 3:费用减免',
  `repay_img` varchar(256) DEFAULT NULL COMMENT '线下还款凭证（ssdb中文件对应的key）',
  `repay_code` varchar(256) DEFAULT NULL COMMENT '还款交易码',
  `repay_location` varchar(256) DEFAULT NULL COMMENT '还款便利店地址',
  `trade_number` varchar(256) DEFAULT NULL COMMENT '自动还款流水号',
  `comment` varchar(256) DEFAULT NULL COMMENT '备注',
  `expire_time` datetime DEFAULT NULL COMMENT '还款交易码失效时间',
  `create_time` datetime NOT NULL COMMENT '记录创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_apply_id` (`apply_id`),
  KEY `idx_repay_status` (`repay_status`),
  KEY `idx_repay_time` (`repay_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS tb_report_operation_daily;
CREATE TABLE IF NOT EXISTS `tb_report_operation_daily` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `data_dt` date DEFAULT NULL COMMENT '报表日期',
  `channel_id` int(11) DEFAULT NULL COMMENT '渠道号',
  `register` int(11) DEFAULT NULL COMMENT '注册',
  `apply_num` int(11) DEFAULT NULL COMMENT '申请数量',
  `apply_user_num` int(11) DEFAULT NULL COMMENT '申请用户数',
  `auto_pass` int(11) DEFAULT NULL COMMENT '自动审核通过',
  `auto_deny` int(11) DEFAULT NULL COMMENT '自动审核拒绝',
  `first_pass` int(11) DEFAULT NULL COMMENT '初审通过订单数',
  `first_deny` int(11) DEFAULT NULL COMMENT '初审拒绝订单数',
  `final_pass` int(11) DEFAULT NULL COMMENT '终审通过订单数',
  `final_deny` int(11) DEFAULT NULL COMMENT '终审拒绝订单数',
  `manual_pass` int(11) DEFAULT NULL COMMENT '人工审核通过',
  `manual_deny` int(11) DEFAULT NULL COMMENT '人工审核拒绝',
  `loan_num` int(11) DEFAULT NULL COMMENT '放款笔数',
  `repay` int(11) DEFAULT NULL COMMENT '应还订单数',
  `repay_actual` int(11) DEFAULT NULL COMMENT '实际还款订单数',
  `first_overdue` int(11) DEFAULT NULL COMMENT '首逾订单数',
  `loan_amt` bigint(20) DEFAULT NULL COMMENT '放款合同金额',
  `loan_inhand_amt` bigint(20) DEFAULT NULL COMMENT '放款到手金额',
  `repay_amt` bigint(20) DEFAULT NULL COMMENT '应还金额',
  `repay_actual_amt` bigint(20) DEFAULT NULL COMMENT '实还总额',
  `first_overdue_amt` bigint(20) DEFAULT NULL COMMENT '首逾金额',
  `forate` double DEFAULT NULL COMMENT '首逾率',
  `download` int(11) DEFAULT NULL COMMENT '下载',
  `loan_failed` int(11) DEFAULT NULL COMMENT '放款失败',
  `loan_pending` int(11) DEFAULT NULL COMMENT '放款中',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `channel_id` (`channel_id`),
  KEY `data_dt` (`data_dt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS tb_report_check_daily;
CREATE TABLE if NOT EXISTS `tb_report_check_daily` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `data_dt` date DEFAULT NULL,
  `task_type` int(11) DEFAULT NULL,
  `total` int(11) DEFAULT NULL,
  `allocated` int(11) DEFAULT NULL,
  `checked` int(11) DEFAULT NULL,
  `denyed` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `data_dt` (`data_dt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS tb_report_collection_daily;
CREATE TABLE if NOT EXISTS `tb_report_collection_daily` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `data_dt` date DEFAULT NULL COMMENT '日期',
  `in_apply` int(11) DEFAULT 0 COMMENT '在库合同数',
  `in_amt` BIGINT(12) DEFAULT 0 COMMENT '在库金额',
  `repay_amt` BIGINT(12) DEFAULT 0 COMMENT '当日还款金额',
  `repay_apply` INT(11) DEFAULT 0 COMMENT '当日还款合同数',
  `ptp_apply` INT(11) DEFAULT 0 COMMENT 'ptp合同数',
  `tracked_apply` INT(11) DEFAULT 0 COMMENT '跟进合同数',
  `repay_amt_rate` double DEFAULT 0 COMMENT '还款金额占比',
  `repay_apply_rate` double DEFAULT 0 COMMENT '还款申请占比',
  PRIMARY KEY (`id`),
  KEY `data_dt` (`data_dt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS tb_blacklist;
CREATE TABLE if NOT EXISTS `tb_blacklist` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `cid_no` varchar(64) DEFAULT NULL COMMENT '身份证号',
  `name` varchar(32) DEFAULT NULL COMMENT '姓名',
  `mobile` varchar(32) DEFAULT NULL COMMENT '手机号',
  `apply_id` int(10) DEFAULT NULL,
  `status` int(4) NOT NULL DEFAULT '0' COMMENT '0:正常，1:灰名单, 2:黑名单',
  `reason` varchar(256) DEFAULT NULL COMMENT '非正常的原因',
  `platform` varchar(64) DEFAULT NULL COMMENT '第三方平台标识',
  `origin_message` text COMMENT '三方服务返回的原始信息',
  `expire_time` datetime NOT NULL DEFAULT '2199-01-01 00:00:00' COMMENT '操作截止时间',
  `create_time` datetime NOT NULL COMMENT '记录创建时间',
  PRIMARY KEY (`id`),
  KEY `cid_no` (`cid_no`),
  KEY `name` (`name`),
  KEY `mobile` (`mobile`),
  KEY `apply_id` (`apply_id`),
  KEY `status` (`status`),
  KEY `platform` (`platform`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE tb_operation_log;
CREATE TABLE if NOT EXISTS `tb_operation_log` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `apply_id` int(10) NOT NULL COMMENT '进件申请id',
  `operator_id` int(11) NOT NULL COMMENT '操作者id',
  `operator_name` varchar(256) NOT NULL DEFAULT '' COMMENT '操作者姓名',
  `operation_type` tinyint(4) NOT NULL COMMENT '操作类型，0:初审，1:复审，2:终审，3:催收',
  `comment` varchar(256) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '记录创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_apply_id` (`apply_id`),
  KEY `idx_operator_id` (`operator_id`),
  KEY `idx_task_type` (`operation_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE tb_user_documentary_image;
CREATE TABLE IF NOT EXISTS `tb_user_documentary_image` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `info_id` varchar(32) NOT NULL COMMENT '资料标识id， 通过 apply id 找到此id',
  `user_id` int(10) NOT NULL COMMENT '关联用户id',
  `image_key` varchar(32) NOT NULL COMMENT '表示 ssdb 存储路径',
  `image_category` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0| 常规 1|补充凭证 2|还款',
  `image_object` int(11) NOT NULL COMMENT '0| 身份证正面 1| 身份证反面  2|手持身份证 3|工作环境 4|驾驶证 5|医保证 6|还款记录 7|其他',
  `image_rel_id` varchar(32) NOT NULL DEFAULT '' COMMENT '用户图片关联反查，例如：还款图片可以表示是第几期',
  `upload_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0|用户正常提交 1|信审人员补充提交 2|用户后提交',
  `upload_user` int(10) DEFAULT NULL COMMENT '上传者id，为空表示用户自己提交，否则为信审人员，备忘一下',
  `ext` varchar(256) DEFAULT '' COMMENT '扩展使用，json 字段，不能索引，根据类型自行扩展',
  `create_time` datetime NOT NULL,
  `expire_time` datetime NOT NULL DEFAULT '2199-01-01 00:00:00' COMMENT '有效期',
  PRIMARY KEY (`id`),
  KEY `idx_info_id` (`info_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `upload_user` (`upload_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE tb_channel_product;
CREATE TABLE IF NOT EXISTS `tb_channel_product` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `channel_id` int(11) NOT NULL COMMENT '渠道id',
  `app_id` int(11) NOT NULL DEFAULT '20000' COMMENT 'App id',
  `product_id` int(11) NOT NULL COMMENT '产品id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`channel_id`),
  KEY `idx_name` (`product_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `app_id` (`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE tb_report_overdue_detail;
CREATE TABLE IF NOT EXISTS `tb_report_overdue_detail` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `data_dt` date NOT NULL COMMENT '报表日期',
  `task_id` int(11) NOT NULL COMMENT '催收任务id',
  `operator_id` int(11) NOT NULL COMMENT '催收员id',
  `name` varchar(128) NOT NULL DEFAULT '""' COMMENT '催收员姓名',
  `apply_id` int(11) NOT NULL COMMENT '申请ID',
  `product_id` int(11) NOT NULL DEFAULT '0' COMMENT '产品ID',
  `status` int(10) NOT NULL COMMENT '订单状态',
  `is_overdue` int(11) NOT NULL DEFAULT '0' COMMENT '是否逾期',
  `overdue_days` int(11) NOT NULL DEFAULT '0' COMMENT '逾期天数',
  `grant_quota` int(11) NOT NULL DEFAULT '0' COMMENT '放款合同金额',
  `need_total` int(11) NOT NULL DEFAULT '0' COMMENT '应还总额',
  `act_total` int(11) NOT NULL DEFAULT '0' COMMENT '实际还款总额',
  `normal_repay` int(11) NOT NULL DEFAULT '0' COMMENT '任务分配前，正常还款总额',
  `overdue_amount` int(11) NOT NULL DEFAULT '0' COMMENT '逾期金额，应还-正常还款',
  `recall_amount` int(11) NOT NULL DEFAULT '0' COMMENT '催回金额，实还-正常还款',
  `repay_end_date` datetime NOT NULL COMMENT '还款截止日期',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `data_dt_2` (`data_dt`,`operator_id`,`apply_id`),
  KEY `data_dt` (`data_dt`),
  KEY `operator_id` (`operator_id`),
  KEY `apply_id` (`apply_id`),
  KEY `product_id` (`product_id`),
  KEY `status` (`status`),
  KEY `overdue_days` (`overdue_days`),
  KEY `repay_end_date` (`repay_end_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE tb_loan_info;
CREATE TABLE IF NOT EXISTS `tb_loan_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `apply_id` int(10) NOT NULL COMMENT 'apply_id 作为主键',
  `user_id` int(11) NOT NULL,
  `product_id` int(11) DEFAULT NULL,
  `channel_id` int(11) DEFAULT NULL,
  `app_id` int(11) DEFAULT NULL,
  `current_seq` int(10) DEFAULT NULL COMMENT '当前期数',
  `status` int(11) DEFAULT NULL COMMENT '订单状态，参见tb_apply_info的状态',
  `is_overdue` int(11) NOT NULL DEFAULT '0',
  `max_overdue_days` int(11) NOT NULL DEFAULT '0' COMMENT '最大逾期天数',
  `regist_time` datetime DEFAULT NULL,
  `apply_time` datetime DEFAULT NULL,
  `loan_time` datetime DEFAULT NULL,
  `repay_end_date` datetime DEFAULT NULL COMMENT '应还日期',
  `repay_time` datetime DEFAULT NULL COMMENT '最后还款日期',
  `contract_amount` bigint(20) NOT NULL DEFAULT '0' COMMENT '合同金额',
  `inhand_amount` bigint(20) NOT NULL DEFAULT '0' COMMENT '到手金额',
  `need_principal` bigint(20) NOT NULL DEFAULT '0' COMMENT '应还本金',
  `act_principal` bigint(20) NOT NULL DEFAULT '0' COMMENT '已还本金',
  `need_total` bigint(20) NOT NULL DEFAULT '0' COMMENT '应还总额',
  `act_total` bigint(20) NOT NULL DEFAULT '0' COMMENT '实还总额',
  `normal_repay` bigint(20) NOT NULL DEFAULT '0' COMMENT '正常还款总额，逾期后还款不计入内',
  `reduction_fee` bigint(20) NOT NULL DEFAULT '0' COMMENT '减免费用',
  `need_penalty_interest` bigint(20) NOT NULL DEFAULT '0' COMMENT '应还罚息',
  `act_penalty_interest` bigint(20) NOT NULL DEFAULT '0' COMMENT '实还罚息',
  `overdue_amount` bigint(20) NOT NULL DEFAULT '0' COMMENT '逾期金额（首逾金额）',
  `remain_overdue_amount` bigint(20) NOT NULL DEFAULT '0' COMMENT '剩余逾期金额',
  `create_time` datetime NOT NULL COMMENT '记录创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `apply_id` (`apply_id`),
  KEY `status` (`status`),
  KEY `regist_time` (`regist_time`),
  KEY `apply_time` (`apply_time`),
  KEY `loan_time` (`loan_time`),
  KEY `product_id` (`product_id`),
  KEY `channel_id` (`channel_id`),
  KEY `repay_end_date` (`repay_end_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE tb_apply_quickpass_rules;
CREATE TABLE IF NOT EXISTS `tb_apply_quickpass_rules` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `stage_from` int(11) NOT NULL COMMENT '审核起始状态',
  `stage_skip_to` int(11) NOT NULL COMMENT '满足条件后，自动跳至下一个审核状态',
  `apply_count` int(10) NOT NULL DEFAULT '0' COMMENT '累计申请次数（已结清）',
  `overdue_count` int(11) NOT NULL DEFAULT '0' COMMENT '逾期申请次数',
  `apply_amount` bigint(10) NOT NULL DEFAULT '0' COMMENT '累计申请金额',
  `last_apply_days` int(10) NOT NULL DEFAULT '0' COMMENT '上一次申请时间间隔',
  `overdue_days` int(10) NOT NULL DEFAULT '0' COMMENT '累计逾期天数',
  `max_overdue_days` int(10) NOT NULL DEFAULT '0' COMMENT '最大逾期天数',
  `operator_id` int(11) DEFAULT '0' COMMENT '规则添加人',
  `comment` varchar(256) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '记录创建时间',
  `update_time` datetime NOT NULL COMMENT '记录更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `stage_from` (`stage_from`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP table tb_report_check_operator_daily;
CREATE TABLE if not exists `tb_report_check_operator_daily` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `operator` int(11) DEFAULT NULL,
  `passed` int(11) DEFAULT NULL,
  `checked` int(11) DEFAULT NULL,
  `allocated` int(11) DEFAULT NULL,
  `loan_num` int(11) DEFAULT NULL,
  `loan_amt` int(11) DEFAULT NULL,
  `fpd` double DEFAULT NULL,
  `pd3` double DEFAULT NULL,
  `pd7` double DEFAULT NULL,
  `pass_rate` double DEFAULT NULL,
  `loan_rate` double DEFAULT NULL,
  `data_dt` date DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `operator_name` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP table tb_asset_level_history;
CREATE TABLE if not exists  `tb_asset_level_history` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `asset_level` int(11) DEFAULT NULL,
  `data_dt` datetime DEFAULT NULL,
  `apply_list` varchar(10240) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP table tb_cms_auth_user_group;
CREATE TABLE if not exists  `tb_cms_auth_user_group` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP table   tb_cms_operation_task_config;
CREATE TABLE if not exists `tb_cms_operation_task_config` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `group_id` int(11) DEFAULT NULL,
  `asset_level` int(11) DEFAULT NULL,
  `enabled` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `strategy` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for tb_cms_auth_resource
-- ----------------------------
DROP TABLE IF EXISTS `tb_cms_auth_resource`;
CREATE TABLE `tb_cms_auth_resource` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(50) DEFAULT NULL COMMENT '名称',
  `url` varchar(255) DEFAULT NULL COMMENT 'url路径',
  `comment` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `level_1` varchar(50) DEFAULT NULL,
  `level_2` varchar(50) DEFAULT NULL,
  `level_3` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tb_cms_auth_role
-- ----------------------------
DROP TABLE IF EXISTS `tb_cms_auth_role`;
CREATE TABLE `tb_cms_auth_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(50) DEFAULT NULL COMMENT '角色名',
  `comment` varchar(255) DEFAULT NULL COMMENT '备注',
  `is_valid` int(11) DEFAULT '1',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tb_cms_auth_role_resource
-- ----------------------------
DROP TABLE IF EXISTS `tb_cms_auth_role_resource`;
CREATE TABLE `tb_cms_auth_role_resource` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) DEFAULT NULL,
  `resource_id` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tb_cms_auth_user
-- ----------------------------
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
  `group_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tb_cms_auth_user_role
-- ----------------------------
DROP TABLE IF EXISTS `tb_cms_auth_user_role`;
CREATE TABLE `tb_cms_auth_user_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `role_id` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tb_cms_collection_allocate_record
-- ----------------------------
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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tb_cms_collection_record
-- ----------------------------
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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `tb_virtual_card_bank_info` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `bank_account_name` varchar(256) NOT NULL DEFAULT '' COMMENT '银行户主名称',
  `bank_name` varchar(64) NOT NULL DEFAULT '' COMMENT '银行名称',
  `branch_name` varchar(128) NOT NULL DEFAULT '' COMMENT '银行分支名称',
  `link` varchar(256) DEFAULT '' COMMENT '银行地图信息',
  `service_fee` int(10) NOT NULL DEFAULT '0' COMMENT '用户服务费',
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `bank_name` (`bank_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tb_virtual_card_info` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `user_id` int(10) NOT NULL COMMENT '用户id',
  `bank_name` varchar(64) NOT NULL DEFAULT '' COMMENT '银行名称',
  `apply_id` int(10) NOT NULL COMMENT '订单号',
  `order_no` int(10) DEFAULT NULL COMMENT '还款历史中的id作为订单号',
  `vc_no` varchar(128) NOT NULL DEFAULT '' COMMENT '虚拟卡号',
  `status` int(10) NOT NULL COMMENT '虚拟卡状态，0:无效，1:有效',
  `service_fee` int(10) NOT NULL DEFAULT '0' COMMENT '用户服务费',
  `create_time` datetime NOT NULL,
  `expire_time` datetime DEFAULT NULL COMMENT '虚拟卡过期时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id_2` (`user_id`),
  KEY `user_id` (`user_id`),
  KEY `apply_id` (`apply_id`),
  KEY `order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tb_virtual_card_info_history` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `user_id` int(10) NOT NULL COMMENT '用户id',
  `bank_name` varchar(64) NOT NULL DEFAULT '' COMMENT '银行名称',
  `apply_id` int(10) NOT NULL COMMENT '订单号',
  `order_no` int(10) DEFAULT NULL COMMENT '还款记录中的id作为订单号',
  `vc_no` varchar(128) NOT NULL DEFAULT '' COMMENT '虚拟卡号',
  `status` int(10) NOT NULL COMMENT '虚拟卡状态，0:无效，1:有效',
  `service_fee` int(10) NOT NULL DEFAULT '0' COMMENT '用户服务费',
  `create_time` datetime NOT NULL,
  `expire_time` datetime DEFAULT NULL COMMENT '虚拟卡过期时间',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `apply_id` (`apply_id`),
  KEY `order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

drop table if EXISTS `tb_sdk_token_mapping`;
create table `tb_sdk_token_mapping` (
  `id` int(10) not null auto_increment,
  `phone` varchar(16) not null,
  `token` varchar(1024) not null,
  `create_time` datetime not null,
  PRIMARY KEY (`id`),
  key `idx_phone` (`phone`)
) ENGINE = InnoDB DEFAULT CHARSET =utf8;



drop table if exists `tb_sdk_dial_history`;
create table `tb_sdk_dial_history` (
  `id` int(11) not null auto_increment,
  `user_id` int(11) not null comment '用户id',
  `counterpart_number` varchar(32) not null comment '对方手机号',
  `name` varchar(64) comment '短信用户',
  `location` varchar(64) comment '对方电话区域',
  `call_time` datetime not null comment '通话时间',
  `duration` int(11) not null comment '通话时长，单位是秒',
  `call_type` tinyint comment '通话类型',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  primary key (`id`),
  key `idx_user_id` (`user_id`)
) ENGINE = InnoDB default charset = utf8mb4;


drop table if exists `tb_sdk_sms_history`;
create table `tb_sdk_sms_history` (
  `id` int(11) not null auto_increment,
  `user_id` int(11) not null comment '用户id',
  `name` varchar(64) comment '短信用户',
  `address` varchar(36) not null comment '短信号码',
  `body` varchar(512) comment '短信内容',
  `sms_time` datetime not null comment '短信时间戳',
  `readed` tinyint,
  `type` tinyint,
  `status` tinyint,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  primary key (`id`),
  key `idx_user_id` (`user_id`)
) ENGINE = InnoDB default charset = utf8mb4;


SET FOREIGN_KEY_CHECKS = 1;



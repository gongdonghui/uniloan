

drop table tb_user_regist_info;
create table if not exists `tb_user_regist_info` (
  `id` int(10) primary key auto_increment comment '作为用户id使用',
  `mobile` varchar(32) not null comment '登陆手机号',
  `create_time` datetime not null comment '创建时间'
) engine=innodb default charset=utf8;
create index idx_mobile on tb_user_regist_info(mobile);
create index idx_create_time on tb_user_regist_info(create_time);


drop table tb_user_citizen_identity_card_info;
create table if not exists `tb_user_citizen_identity_card_info` (
  `id` int(10) primary key auto_increment,
  `info_id` varchar(32) not null comment '资料标识id',
  `user_id` int(10) not null comment '关联用户id',
  `name` varchar(32) not null comment '姓名',
  `cic_no` varchar(64) not null comment '身份证号',
  `gender` tinyint not null default 0 comment '性别  0|男  1|女',
  `pic_1` varchar(128) not null default '' comment '照片1',
  `pic_2` varchar(128) not null default '' comment '照片2',
  `pic_3` varchar(128) not null default '' comment '照片3',
  `pic_4` varchar(128) not null default '' comment '照片4',
  `create_time` datetime not null,
  `expire_time` datetime not null default '2199-01-01 00:00:00' comment '有效期'
) engine=innodb default charset=utf8;
create index idx_info_id on tb_user_citizen_identity_card_info(info_id);
create index idx_user_id on tb_user_citizen_identity_card_info(user_id);
create index idx_create_time on tb_user_citizen_identity_card_info(create_time);

drop table tb_user_basic_info;
create table if not exists `tb_user_basic_info` (
  `id` int(10) primary key auto_increment,
  `info_id` varchar(32) not null comment '资料标识id',
  `user_id` int(10) not null comment '关联用户id',
  `education` tinyint not null comment '学历   0|小学 1|初中 2|高中 3|中间 4|学院 5|综合性大学 6|大学后',
  `marriage` tinyint not null default 1 comment '婚姻状态    0|已婚 1|单身 2|离异 3|丧偶',
  `children_count` tinyint not null default 0 comment '子女个数',
  `residence_city` smallint not null default 0 comment '居住城市 0|河内  1|河外',
  `residence_addr` varchar(256) not null comment '详细居住地址',
  `residen_duration` tinyint not null default 0 comment '居住时长 0|3个月  1|6个月',
  `purpose` tinyint not null default 0 comment '用途 0|旅游  1|买车',
  `purpose_other` varchar(128) not null default '' comment '用途其他 如果需要用户填写',
  `zalo_id` varchar(64) not null default '' comment 'zalo id',
  `age` tinyint not null default 0 comment '年龄',
  `create_time` datetime not null,
  `expire_time` datetime not null default '2199-01-01 00:00:00' comment '有效期'
) engine=innodb default charset=utf8;
create index idx_info_id on tb_user_basic_info(info_id);
create index idx_user_id on tb_user_basic_info(user_id);
create index idx_create_time on tb_user_basic_info(create_time);


drop table tb_user_emergency_contact;
create table if not exists `tb_user_emergency_contact` (
  `id` int(10) primary key auto_increment,
  `info_id` varchar(32) not null comment '资料标识id',
  `user_id` int(10) not null comment '关联用户id',
  `relationship` tinyint not null comment '关系 0|父母 1|同事',
  `name` varchar(64) not null comment '紧急联系人姓名',
  `mobile` varchar(32) not null comment '紧急联系人电话',
  `create_time` datetime not null,
  `expire_time` datetime not null default '2199-01-01 00:00:00' comment '有效期'
) engine=innodb default charset=utf8;
create index idx_info_id on tb_user_emergency_contact(info_id);
create index idx_user_id on tb_user_emergency_contact(user_id);
create index idx_create_time on tb_user_emergency_contact(create_time);


drop table tb_user_employment_info;
create table if not exists `tb_user_employment_info` (
  `id` int(10) primary key auto_increment,
  `info_id` varchar(32) not null comment '资料标识id',
  `user_id` int(10) not null comment '关联用户id',
  `company` varchar(128) not null comment '任职公司名称',
  `company_city` smallint not null comment '公司所在区域 0|河内 1|河外',
  `company_addr` varchar(256) not null comment '公司详细地址',
  `phone` varchar(32) not null comment '公司联系电话',
  `job_occupation` tinyint not null comment '职业类型 0|工程师  1|服务行业',
  `income` tinyint not null comment '收入状态 0|1~100  1|100~1000',
  `work_pic` varchar(128) not null comment '工作照片',
  `create_time` datetime not null,
  `expire_time` datetime not null default '2199-01-01 00:00:00' comment '有效期'
) engine=innodb default charset=utf8;
create index idx_info_id on tb_user_employment_info(info_id);
create index idx_user_id on tb_user_employment_info(user_id);
create index idx_create_time on tb_user_employment_info(create_time);


drop table tb_user_bank_account_info;
create table if not exists `tb_user_bank_account_info` (
  `id` int(10) primary key auto_increment,
  `info_id` varchar(32) not null comment '资料标识id',
  `user_id` int(10) not null comment '关联用户id',
  `account_type` tinyint not null default 0 comment '账户类型  0|借记卡 1|信用卡',
  `name` varchar(64) not null comment '持卡人姓名',
  `bank` tinyint not null comment '银行名称 0|xx_bank  1|yy_bank',
  `account_id` varchar(128) not null comment '银行账号',
  `create_time` datetime not null,
  `expire_time` datetime not null default '2199-01-01 00:00:00' comment '有效期'
) engine=innodb default charset=utf8;
create index idx_info_id on tb_user_bank_account_info(info_id);
create index idx_user_id on tb_user_bank_account_info(user_id);
create index idx_create_time on tb_user_bank_account_info(create_time);



drop table tb_apply_info;
create table if not exists `tb_apply_info`(
  `id` int(10) primary key auto_increment comment '作为apply_id使用',
  `user_id` int(10) not null,
  `product_id` int(10) not null,
  `credit_type` tinyint not null default 0 comment '授信类型 0|普通  1|循环',
  `rate` varchar(16) not null comment '费率，从产品关联得到',
  `period` varchar(16) not null comment '期限，从产品关联到',
  `quota` varchar(16) not null comment '额度，从产品关联到',
  `app_env_id` int(10) not null default -1,
  `status` int(10) not null comment '状态 0|待初审  1|初审通过 2|初审失败 3|待复审  4|复审通过 5|复审失败',
  `apply_quota` varchar(16) not null comment '用户实际申请的额度',
  `contract_amount` int(10) not null comment '合同金额',
  `inhand_amount` int(10) not null comment '到手金额',
  `credit_class` varchar(8) not null default '' comment '用户的信用评级，没到这一步就为空',
  `deny_code` varchar(8) not null comment '拒贷码，根据阶段不一样，取值也不一样',
  `create_time` datetime not null comment '记录创建时间',
  `expire_time` datetime not null comment '该条申请失效时间',
  `apply_time` datetime comment '用户申请时间',
  `loan_time` datetime comment '放款时间',
  `audit_person` varchar(32) comment '初审人员',
  `re_audit_person` varchar(32) comment '复审人员',
  `update_time` datetime not null comment '更新时间'
) engine=innodb default charset=utf8;
create index idx_user_id on tb_apply_info(user_id);
create index idx_status on tb_apply_info(status);
create index idx_create_time on tb_apply_info(create_time);
create index idx_apply_time on tb_apply_info(apply_time);

drop table tb_apply_material_info;
create table if not exists `tb_apply_material_info`(
  `id` int(10) primary key auto_increment,
  `apply_id` int(10) not null comment '用户申请id',
  `info_id` varchar(32) not null comment '用户申请使用的材料id',
  `info_type` tinyint not null comment '0|身份证信息  1|基本信息 2|紧急联系人 3|职业信息 4|银行卡信息',
  `create_time` datetime not null
) engine=innodb default charset=utf8;
create index idx_apply_id on tb_apply_material_info(apply_id);
create index idx_info_id on tb_apply_material_info(info_id);
create index idx_create_time on tb_apply_material_info(create_time);

drop table tb_repay_plan;
create table if not exists `tb_repay_plan` (
  `id` int(10) primary key auto_increment comment '作为plan_id',
  `user_id` int(10) not null,
  `apply_id` int(10) not null,
  `seq_no` int(10) not null comment '期数从1开始计数',
  `repay_start_date` datetime not null comment '当前开始还款时间',
  `repay_end_date` datetime not null comment '还款截止日期',
  `repay_time` datetime comment '还款时间，未还为空',
  `repay_status` tinyint not null comment '还款状态 0|待还  1|已还',
  `is_overdue` tinyint not null comment '是否逾期 0|没有 1|逾期',
  `need_principal` int(10) not null comment '应还本金',
  `act_principal` int(10) not null comment '实还本金',
  `need_interest` int(10) not null comment '应还利息',
  `act_interest` int(10) not null comment '实还利息',
  `need_penalty_interest` int(10) not null comment '应还罚息',
  `act_penalty_interest` int(10) not null comment '实还罚息',
  `need_management_fee` int(10) not null comment '应还管理费',
  `act_management_fee` int(10) not null comment '实还管理费',
  `need_late_payment_fee` int(10) not null comment '应还滞纳金',
  `act_late_payment_fee` int(10) not null comment '实还滞纳金',
  `need_breach_fee` int(10) not null comment '应还违约金',
  `act_breach_fee` int(10) not null comment '实还违约金',
  `need_other` int(10) not null comment '应还其他',
  `act_other` int(10) not null comment '实还其他',
  `need_total` int(10) not null comment '应还总额',
  `act_total` int(10) not null comment '实还总额',
  `create_time` datetime not null comment '记录创建时间',
  `update_time` datetime not null comment '更新时间'
) engine=innodb default charset=utf8;
create index idx_user_id on tb_repay_plan(user_id);
create index idx_apply_id on tb_repay_plan(apply_id);

drop table tb_repay_stat;
create table if not exists `tb_repay_stat` (
  `apply_id` int(10) primary key comment 'apply_id 作为主键',
  `need_principal` int(10) not null comment '应还本金',
  `act_principal` int(10) not null comment '实还本金',
  `need_interest` int(10) not null comment '应还利息',
  `act_interest` int(10) not null comment '实还利息',
  `need_penalty_interest` int(10) not null comment '应还罚息',
  `act_penalty_interest` int(10) not null comment '实还罚息',
  `need_management_fee` int(10) not null comment '应还管理费',
  `act_management_fee` int(10) not null comment '实还管理费',
  `need_late_payment_fee` int(10) not null comment '应还滞纳金',
  `act_late_payment_fee` int(10) not null comment '实还滞纳金',
  `need_breach_fee` int(10) not null comment '应还违约金',
  `act_breach_fee` int(10) not null comment '实还违约金',
  `need_other` int(10) not null comment '应还其他',
  `act_other` int(10) not null comment '实还其他',
  `need_total` int(10) not null comment '应还总额',
  `act_total` int(10) not null comment '实还总额',
  `current_seq` int(10) not null comment '当前期数',
  `normal_repay_times` int(10) not null comment '正常还款期数',
  `overdue_repay_times` int(10) not null comment '逾期还款期数',
  `overdue_times` int(10) not null comment '逾期期数',
  `create_time` datetime not null comment '记录创建时间',
  `update_time` datetime not null comment '更新时间'
) engine=innodb default charset=utf8;
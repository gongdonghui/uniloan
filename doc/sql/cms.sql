/*
 Navicat Premium Data Transfer

 Source Server         : 47.92.200.47
 Source Server Type    : MySQL
 Source Server Version : 50173
 Source Host           : 47.92.200.47:3306
 Source Schema         : test_uniloan

 Target Server Type    : MySQL
 Target Server Version : 50173
 File Encoding         : 65001

 Date: 03/10/2019 18:38:10
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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

SET FOREIGN_KEY_CHECKS = 1;

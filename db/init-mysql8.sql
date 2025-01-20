/*
 Navicat Premium Dump SQL

 Source Server         : mysql8
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : localhost:3306
 Source Schema         : smart-demo

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 20/01/2025 10:26:26
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for gen_demo
-- ----------------------------
DROP TABLE IF EXISTS `gen_demo`;
CREATE TABLE `gen_demo`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `table_column1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'T1',
  `table_column2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'T2',
  `table_column3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'T3',
  `table_column4` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'T4',
  `table_column5` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'T5',
  `table_column6` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'T6',
  `table_column7` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'T7',
  `table_column8` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'T8',
  `table_column9` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'T9',
  `table_column10` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'T10',
  `table_column11` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'T11',
  `table_column12` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'T12',
  `table_column13` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'T13',
  `table_column14` int NULL DEFAULT NULL COMMENT 'T14',
  `is_deleted` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0正常 1删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建者',
  `create_user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人ID',
  `create_dept` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人部门ID',
  `create_organization` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人公司ID',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '生成案例' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gen_demo
-- ----------------------------
INSERT INTO `gen_demo` VALUES ('1812864635448193025', '测试数据', '<p>啊实打实打算</p>', '啊实打实打算', '2', '一级', '三级', '4,2,3,1', '2', '2024-07-17 22:58:34', '22:58:54', '1', '1876459278030704641', 'proxy-upload/2XKLSAJ20250107162426.jpg', 123555, '0', 'system', 'system', '', '', '2024-07-15 22:59:45', 'system', '2025-01-08 13:34:21', NULL);

-- ----------------------------
-- Table structure for sys_buttons
-- ----------------------------
DROP TABLE IF EXISTS `sys_buttons`;
CREATE TABLE `sys_buttons`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '按钮编号',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '按钮名称',
  `sort` int NOT NULL COMMENT '排序',
  `menu_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `code_index`(`code` ASC) USING BTREE COMMENT '按钮code唯一索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '按钮表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_buttons
-- ----------------------------
INSERT INTO `sys_buttons` VALUES ('1804894853872521218', 'menu:add', '菜单新增', 1, '1477239327678414750');
INSERT INTO `sys_buttons` VALUES ('1804894853872521219', 'menu:update', '菜单修改', 2, '1477239327678414750');
INSERT INTO `sys_buttons` VALUES ('1804894853872521220', 'menu:delete', '菜单删除', 3, '1477239327678414750');
INSERT INTO `sys_buttons` VALUES ('1804894950840635393', 'role:add', '角色新增', 1, '1481002161528868865');
INSERT INTO `sys_buttons` VALUES ('1804894950907744258', 'role:update', '角色修改', 2, '1481002161528868865');
INSERT INTO `sys_buttons` VALUES ('1804894950907744259', 'role:delete', '角色删除', 3, '1481002161528868865');
INSERT INTO `sys_buttons` VALUES ('1805845128137535489', 'post:add', '岗位新增', 1, '1502438095121973250');
INSERT INTO `sys_buttons` VALUES ('1805845128137535490', 'post:update', '岗位修改', 2, '1502438095121973250');
INSERT INTO `sys_buttons` VALUES ('1805845128137535491', 'post:delete', '岗位删除', 3, '1502438095121973250');
INSERT INTO `sys_buttons` VALUES ('1806208274958159873', 'dict:add', '字典新增', 1, '1477239136195854337');
INSERT INTO `sys_buttons` VALUES ('1806208274958159874', 'dict:update', '字典修改', 2, '1477239136195854337');
INSERT INTO `sys_buttons` VALUES ('1806208274958159875', 'dict:delete', '字典删除', 3, '1477239136195854337');
INSERT INTO `sys_buttons` VALUES ('1808023796922204161', 'log:delete', '操作日志删除', 1, '1488415792415588353');
INSERT INTO `sys_buttons` VALUES ('1808034645028130818', 'loginLog:delete', '登录日志删除', 1, '1588072580118642690');
INSERT INTO `sys_buttons` VALUES ('1808157238888591361', 'errorLog:delete', '异常日志删除', 1, '1552180830791774210');
INSERT INTO `sys_buttons` VALUES ('1808309840729088002', 'config:add', '参数新增', 1, '1663455985822781441');
INSERT INTO `sys_buttons` VALUES ('1808309840762642433', 'config:update', '参数修改', 2, '1663455985822781441');
INSERT INTO `sys_buttons` VALUES ('1808309840762642434', 'config:delete', '参数删除', 3, '1663455985822781441');
INSERT INTO `sys_buttons` VALUES ('1808506030909296642', 'jobGroup:add', '执行器新增', 1, '1511936878172078081');
INSERT INTO `sys_buttons` VALUES ('1808506030909296643', 'jobGroup:update', '执行器修改', 2, '1511936878172078081');
INSERT INTO `sys_buttons` VALUES ('1808506030909296644', 'jobGroup:delete', '执行器删除', 3, '1511936878172078081');
INSERT INTO `sys_buttons` VALUES ('1809248128025509890', 'job:add', '任务新增', 1, '1510410640500604929');
INSERT INTO `sys_buttons` VALUES ('1809248128059064322', 'job:update', '任务修改', 2, '1510410640500604929');
INSERT INTO `sys_buttons` VALUES ('1809248128059064323', 'job:delete', '任务删除', 3, '1510410640500604929');
INSERT INTO `sys_buttons` VALUES ('1809248128059064324', 'job:trigger', '任务执行一次', 4, '1510410640500604929');
INSERT INTO `sys_buttons` VALUES ('1809248128059064325', 'job:start', '任务启动', 5, '1510410640500604929');
INSERT INTO `sys_buttons` VALUES ('1809248128059064326', 'job:stop', '任务停止', 6, '1510410640500604929');
INSERT INTO `sys_buttons` VALUES ('1809587595328475138', 'jobLog:delete', '执行日志删除', 1, '1511936861684269057');
INSERT INTO `sys_buttons` VALUES ('1811065114447585282', 'scope:add', '数据权限新增', 1, '1485207321130029057');
INSERT INTO `sys_buttons` VALUES ('1811065114447585283', 'scope:update', '数据权限修改', 2, '1485207321130029057');
INSERT INTO `sys_buttons` VALUES ('1811065114447585284', 'scope:delete', '数据权限删除', 3, '1485207321130029057');
INSERT INTO `sys_buttons` VALUES ('1815325834852966401', 'file:upload', '文件上传', 1, '1507980643286515713');
INSERT INTO `sys_buttons` VALUES ('1815325834852966402', 'file:download', '文件下载', 2, '1507980643286515713');
INSERT INTO `sys_buttons` VALUES ('1815325834861355010', 'file:delete', '文件删除', 3, '1507980643286515713');
INSERT INTO `sys_buttons` VALUES ('1818818747406807041', 'gen:add', '代码生成器新增', 1, '1477239327678414850');
INSERT INTO `sys_buttons` VALUES ('1818818747415195650', 'gen:update', '代码生成器修改', 2, '1477239327678414850');
INSERT INTO `sys_buttons` VALUES ('1818818747415195651', 'gen:delete', '代码生成器删除', 3, '1477239327678414850');
INSERT INTO `sys_buttons` VALUES ('1819546766098391042', 'user:add', '用户新增', 1, '1477239028339326977');
INSERT INTO `sys_buttons` VALUES ('1819546766106779650', 'user:update', '用户修改', 2, '1477239028339326977');
INSERT INTO `sys_buttons` VALUES ('1819546766106779651', 'user:delete', '用户删除', 3, '1477239028339326977');
INSERT INTO `sys_buttons` VALUES ('1819546766106779652', 'user:export', '用户导出', 4, '1477239028339326977');
INSERT INTO `sys_buttons` VALUES ('1819546766106779653', 'user:import', '用户导入', 5, '1477239028339326977');
INSERT INTO `sys_buttons` VALUES ('1819547096810872833', 'dept:add', '部门新增', 1, '1477238932050690050');
INSERT INTO `sys_buttons` VALUES ('1819547096819261442', 'dept:update', '部门修改', 2, '1477238932050690050');
INSERT INTO `sys_buttons` VALUES ('1819547096819261443', 'dept:delete', '部门删除', 3, '1477238932050690050');
INSERT INTO `sys_buttons` VALUES ('1819547096819261444', 'dept:export', '部门导出', 4, '1477238932050690050');
INSERT INTO `sys_buttons` VALUES ('1819547096819261445', 'dept:import', '部门导入', 5, '1477238932050690050');
INSERT INTO `sys_buttons` VALUES ('1821088514343759874', 'genDemo:add', '生成案例新增', 1, '1812863571709693953');
INSERT INTO `sys_buttons` VALUES ('1821088514347954178', 'genDemo:update', '生成案例修改', 2, '1812863571709693953');
INSERT INTO `sys_buttons` VALUES ('1821088514347954179', 'genDemo:delete', '生成案例删除', 3, '1812863571709693953');
INSERT INTO `sys_buttons` VALUES ('1821088514347954180', 'genDemo:export', '生成案例导出', 4, '1812863571709693953');
INSERT INTO `sys_buttons` VALUES ('1821088514347954181', 'genDemo:import', '生成案例导入', 5, '1812863571709693953');
INSERT INTO `sys_buttons` VALUES ('1838767094389395457', 'frontUser:delete', '微信用户删除', 1, '1821461884822388737');
INSERT INTO `sys_buttons` VALUES ('1871128593359196162', 'notice:add', '通知公告新增', 1, '1867495401284767745');
INSERT INTO `sys_buttons` VALUES ('1871128593359196163', 'notice:update', '通知公告修改', 2, '1867495401284767745');
INSERT INTO `sys_buttons` VALUES ('1871128593359196164', 'notice:delete', '通知公告删除', 3, '1867495401284767745');
INSERT INTO `sys_buttons` VALUES ('1871128593359196165', 'notice:export', '通知公告导出', 4, '1867495401284767745');
INSERT INTO `sys_buttons` VALUES ('1871128593359196166', 'notice:import', '通知公告导入', 5, '1867495401284767745');
INSERT INTO `sys_buttons` VALUES ('1871128593359196167', 'noticeRecord:view', '通知公告发布记录查看', 6, '1867495401284767745');
INSERT INTO `sys_buttons` VALUES ('1871128593359196168', 'noticeRecord:add', '通知公告发布', 7, '1867495401284767745');
INSERT INTO `sys_buttons` VALUES ('1871128593359196169', 'noticeRef:update', '通知公告撤销', 8, '1867495401284767745');
INSERT INTO `sys_buttons` VALUES ('1873646182220169217', 'oss:add', '对象存储新增', 1, '1507937416818860034');
INSERT INTO `sys_buttons` VALUES ('1873646182220169218', 'oss:update', '对象存储修改', 2, '1507937416818860034');
INSERT INTO `sys_buttons` VALUES ('1873646182220169219', 'oss:delete', '对象存储删除', 3, '1507937416818860034');
INSERT INTO `sys_buttons` VALUES ('1879454256671494146', 'redis:add', '缓存新增', 1, '1877554252449132545');
INSERT INTO `sys_buttons` VALUES ('1879454256671494147', 'redis:update', '缓存修改', 2, '1877554252449132545');
INSERT INTO `sys_buttons` VALUES ('1879454256671494148', 'redis:delete', '缓存删除', 3, '1877554252449132545');

-- ----------------------------
-- Table structure for sys_client
-- ----------------------------
DROP TABLE IF EXISTS `sys_client`;
CREATE TABLE `sys_client`  (
  `client_id` varchar(48) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '客户端ID',
  `client_secret` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '客户端令牌',
  `resource_ids` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `scope` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `authorized_grant_types` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录方式',
  `web_server_redirect_uri` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `authorities` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `access_token_validity` int NOT NULL COMMENT 'accessToken有效期',
  `refresh_token_validity` int NOT NULL COMMENT 'refreshToken有效期',
  `additional_information` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `autoapprove` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '客户端' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_client
-- ----------------------------
INSERT INTO `sys_client` VALUES ('sword', 'sword_secret', '', 'all', 'refresh_token,password,authorization_code,captcha,wechat_code', 'http://localhost:8888', '', 3600, 604800, NULL, '');
INSERT INTO `sys_client` VALUES ('saber', 'saber_secret', '', 'all', 'refresh_token,password,authorization_code,captcha,wechat_code', 'http://localhost:8080', '', 259200, 604800, NULL, '');

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `config_key` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '键',
  `config_value` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '值',
  `config_desc` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '描述',
  `is_system` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT ' 是否系统配置',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `key_unique_index`(`config_key` ASC) USING BTREE COMMENT 'key唯一索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES ('1', 'has_dept_post', '1', '是否开启组织机构', '1');
INSERT INTO `sys_config` VALUES ('2', 'captcha_auth', '1', 'PC端是否开启验证码', '1');
INSERT INTO `sys_config` VALUES ('3', 'auto_sign_up', '1', '开启会员注册', '1');
INSERT INTO `sys_config` VALUES ('4', 'file_document_url', 'http://192.168.0.100:8001', '文件服务地址', '1');
INSERT INTO `sys_config` VALUES ('5', 'file_document_callback_url', 'http://192.168.0.100:8888/file/callback', '在线编辑回调地址', '1');
INSERT INTO `sys_config` VALUES ('6', 'wechat_app_id', 'appId', '微信小程序APP_ID', '1');
INSERT INTO `sys_config` VALUES ('7', 'wechat_secret', 'secret', '微信小程序APP_SECRET', '1');
INSERT INTO `sys_config` VALUES ('8', 'sys_email_config', '{"host":"smtp.qq.com","port":465,"username":"342480071@qq.com","from":"342480071@qq.com","password":"ubgpjpfmctpcbhia"}', '邮箱配置', '1');

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `is_deleted` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0正常 1删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建者',
  `create_user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人ID',
  `create_dept` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人部门ID',
  `create_organization` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人公司ID',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  `dept_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '机构编号',
  `parent_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '父级ID',
  `ancestors` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '祖级列表',
  `dept_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '部门类型',
  `dept_name` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '部门名称',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '启用状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '机构' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES ('1650709468683456514', '0', 'system', 'system', '', '', '2023-04-25 11:53:01', 'system', '2024-12-17 08:54:41', NULL, 'R000001', '0', '0', '1', '总公司', 1, '1');
INSERT INTO `sys_dept` VALUES ('1650709569262866433', '0', 'system', 'system', '', '', '2023-04-25 11:53:25', 'system', '2024-12-17 08:54:54', NULL, 'R000001-01', '1650709468683456514', '0,1650709468683456514', '2', '技术部', 1, '1');
INSERT INTO `sys_dept` VALUES ('1650709616285208577', '0', 'system', 'system', '', '', '2023-04-25 11:53:36', 'system', '2024-12-17 08:55:03', NULL, 'R000001-02', '1650709468683456514', '0,1650709468683456514', '2', '人事部', 2, '1');
INSERT INTO `sys_dept` VALUES ('1650709667321499649', '0', 'system', 'system', '', '', '2023-04-25 11:53:48', 'system', '2024-12-17 08:55:09', NULL, 'R000001-03', '1650709468683456514', '0,1650709468683456514', '2', '财务部', 3, '1');
INSERT INTO `sys_dept` VALUES ('1868822323444887553', '0', 'system', 'system', '', '', '2024-12-17 08:55:38', 'system', '2024-12-17 08:56:46', NULL, 'RT000001', '1650709468683456514', '0,1650709468683456514', '1', '天津分公司我', 4, '1');
INSERT INTO `sys_dept` VALUES ('1868822454307172354', '0', 'system', 'system', '', '', '2024-12-17 08:56:09', 'system', '2024-12-17 08:56:53', NULL, 'RT000001-01', '1868822323444887553', '0,1650709468683456514,1868822323444887553', '2', '天津办公室', 1, '1');
INSERT INTO `sys_dept` VALUES ('1868822764152991745', '0', 'system', 'system', '', '', '2024-12-17 08:57:23', 'system', '2024-12-17 08:57:30', NULL, 'RT000001-02', '1868822323444887553', '0,1650709468683456514,1868822323444887553', '2', '天津华苑办事处', 2, '1');

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `parent_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '父级ID',
  `ancestors` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '祖级列表',
  `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典名称',
  `dict_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类型 1系统 2业务',
  `dict_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典编码',
  `dict_value` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典值',
  `sort` int NOT NULL COMMENT '排序',
  `is_deleted` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0正常 1删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建者',
  `create_user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人ID',
  `create_dept` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人部门ID',
  `create_organization` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人公司ID',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO `sys_dict` VALUES ('1507952180051705857', '0', '0', 'OSS存储类型', '1', 'sys_oss_type', NULL, 2, '0', 'system', 'system', NULL, NULL, '2022-03-27 05:26:31', 'system', '2022-11-01 15:16:37', NULL);
INSERT INTO `sys_dict` VALUES ('1507952306459639809', '1507952180051705857', '0,1507952180051705857', 'MinIO', NULL, NULL, '1', 1, '0', 'system', 'system', NULL, NULL, '2022-03-27 05:27:01', 'system', '2022-03-27 05:27:05', NULL);
INSERT INTO `sys_dict` VALUES ('1507952365112786946', '1507952180051705857', '0,1507952180051705857', '阿里云', NULL, NULL, '2', 2, '0', 'system', 'system', NULL, NULL, '2022-03-27 05:27:15', 'system', '2022-03-27 05:27:15', NULL);
INSERT INTO `sys_dict` VALUES ('1507952405076115458', '1507952180051705857', '0,1507952180051705857', '七牛', NULL, NULL, '3', 3, '0', 'system', 'system', NULL, NULL, '2022-03-27 05:27:24', 'system', '2022-03-27 05:27:24', NULL);
INSERT INTO `sys_dict` VALUES ('1587342790659035138', '0', '0', '机构类型', '1', 'sys_dept_type', NULL, 1, '0', 'system', 'system', NULL, NULL, '2022-11-01 15:16:27', 'system', '2024-07-01 14:30:06', NULL);
INSERT INTO `sys_dict` VALUES ('1587342876386414593', '1587342790659035138', '0,1587342790659035138', '公司', NULL, NULL, '1', 1, '0', 'system', 'system', NULL, NULL, '2022-11-01 15:16:48', 'system', '2023-03-23 17:00:18', NULL);
INSERT INTO `sys_dict` VALUES ('1587342904056238081', '1587342790659035138', '0,1587342790659035138', '部门', NULL, NULL, '2', 2, '0', 'system', 'system', NULL, NULL, '2022-11-01 15:16:54', 'system', '2022-11-01 15:16:54', NULL);
INSERT INTO `sys_dict` VALUES ('1789293166801793026', '0', '0', '是否', '1', 'sys_yes_or_no', NULL, 3, '0', 'system', 'system', NULL, NULL, '2024-05-11 21:55:09', 'system', '2024-05-12 18:15:35', NULL);
INSERT INTO `sys_dict` VALUES ('1789293189513949185', '1789293166801793026', '0,1789293166801793026', '是', NULL, NULL, '1', 1, '0', 'system', 'system', NULL, NULL, '2024-05-11 21:55:15', 'system', '2024-05-11 21:55:15', NULL);
INSERT INTO `sys_dict` VALUES ('1789293211596959745', '1789293166801793026', '0,1789293166801793026', '否', NULL, NULL, '0', 2, '0', 'system', 'system', NULL, NULL, '2024-05-11 21:55:20', 'system', '2024-05-11 21:55:20', NULL);
INSERT INTO `sys_dict` VALUES ('1789599939764969473', '0', '0', '用户状态', '1', 'sys_user_status', NULL, 4, '0', 'system', 'system', NULL, NULL, '2024-05-12 18:14:09', 'system', '2024-05-12 18:14:09', NULL);
INSERT INTO `sys_dict` VALUES ('1789605818325307394', '1789599939764969473', '0,1789599939764969473', '正常', NULL, NULL, '1', 1, '0', 'system', 'system', NULL, NULL, '2024-05-12 18:37:31', 'system', '2024-05-12 18:37:31', NULL);
INSERT INTO `sys_dict` VALUES ('1789605845877690369', '1789599939764969473', '0,1789599939764969473', '停用', NULL, NULL, '0', 2, '0', 'system', 'system', NULL, NULL, '2024-05-12 18:37:38', 'system', '2024-05-12 18:37:38', NULL);
INSERT INTO `sys_dict` VALUES ('1807663092545368066', '0', '0', '多级字典', '1', 'sys_tree_dict', NULL, 5, '0', 'system', 'system', NULL, NULL, '2024-07-01 14:30:41', 'system', '2024-11-20 10:51:22', NULL);
INSERT INTO `sys_dict` VALUES ('1811662672119238657', '1807663092545368066', '0,1807663092545368066', 'first', NULL, NULL, '一级', 1, '0', 'system', 'system', NULL, NULL, '2024-07-12 15:23:35', 'system', '2024-07-15 22:14:01', NULL);
INSERT INTO `sys_dict` VALUES ('1811662688724488194', '1811662672119238657', '0,1807663092545368066,1811662672119238657', 'second', NULL, NULL, '二级', 1, '0', 'system', 'system', NULL, NULL, '2024-07-12 15:23:39', 'system', '2024-07-15 22:14:13', NULL);
INSERT INTO `sys_dict` VALUES ('1811662702465028097', '1811662688724488194', '0,1807663092545368066,1811662672119238657,1811662688724488194', 'three', NULL, NULL, '三级', 1, '0', 'system', 'system', NULL, NULL, '2024-07-12 15:23:42', 'system', '2024-07-15 22:14:22', NULL);
INSERT INTO `sys_dict` VALUES ('1823587751321255937', '1507952180051705857', '0,1507952180051705857', '腾讯COS', NULL, NULL, '4', 4, '0', 'system', 'system', '', '', '2024-08-14 13:09:35', 'system', '2024-08-14 13:09:35', NULL);
INSERT INTO `sys_dict` VALUES ('1859066958079619073', '0', '0', '性别', '1', 'sys_sex', NULL, 6, '0', 'system', 'system', '', '', '2024-11-20 10:51:17', 'system', '2024-11-20 10:51:17', NULL);
INSERT INTO `sys_dict` VALUES ('1859067048601088001', '1859066958079619073', '0,1859066958079619073', '男', NULL, NULL, '1', 1, '0', 'system', 'system', '', '', '2024-11-20 10:51:39', 'system', '2024-11-20 10:51:39', NULL);
INSERT INTO `sys_dict` VALUES ('1859067074584801282', '1859066958079619073', '0,1859066958079619073', '女', NULL, NULL, '2', 2, '0', 'system', 'system', '', '', '2024-11-20 10:51:45', 'system', '2024-11-20 10:51:45', NULL);
INSERT INTO `sys_dict` VALUES ('1867492647485116418', '0', '0', '通知公告类型', '1', 'sys_notice_type', NULL, 7, '0', 'system', 'system', '', '', '2024-12-13 16:51:58', 'system', '2024-12-13 16:53:39', NULL);
INSERT INTO `sys_dict` VALUES ('1867493030332796929', '1867492647485116418', '0,1867492647485116418', '普通', NULL, NULL, '1', 1, '0', 'system', 'system', '', '', '2024-12-13 16:53:29', 'system', '2024-12-13 17:24:30', NULL);
INSERT INTO `sys_dict` VALUES ('1867493058954727426', '1867492647485116418', '0,1867492647485116418', '紧急', NULL, NULL, '2', 2, '0', 'system', 'system', '', '', '2024-12-13 16:53:36', 'system', '2024-12-13 17:24:35', NULL);
INSERT INTO `sys_dict` VALUES ('1867493160851148801', '0', '0', '通知公告类别', '1', 'sys_notice_category', NULL, 8, '0', 'system', 'system', '', '', '2024-12-13 16:54:01', 'system', '2024-12-13 16:54:24', NULL);
INSERT INTO `sys_dict` VALUES ('1867493212709523457', '1867493160851148801', '0,1867493160851148801', '公告', NULL, NULL, '1', 1, '0', 'system', 'system', '', '', '2024-12-13 16:54:13', 'system', '2024-12-13 17:24:45', NULL);
INSERT INTO `sys_dict` VALUES ('1867493240337403905', '1867493160851148801', '0,1867493160851148801', '通知', NULL, NULL, '2', 2, '0', 'system', 'system', '', '', '2024-12-13 16:54:20', 'system', '2024-12-13 17:24:50', NULL);
INSERT INTO `sys_dict` VALUES ('1867504712337711105', '0', '0', '通知公告发布类型', '1', 'sys_release_type', NULL, 9, '0', 'system', 'system', '', '', '2024-12-13 17:39:55', 'system', '2024-12-13 17:39:55', NULL);
INSERT INTO `sys_dict` VALUES ('1867505790651326465', '1867504712337711105', '0,1867504712337711105', '机构', NULL, NULL, '1', 1, '0', 'system', 'system', '', '', '2024-12-13 17:44:12', 'system', '2024-12-13 17:44:12', NULL);
INSERT INTO `sys_dict` VALUES ('1867505817939468290', '1867504712337711105', '0,1867504712337711105', '角色', NULL, NULL, '3', 3, '0', 'system', 'system', '', '', '2024-12-13 17:44:18', 'system', '2024-12-17 08:46:25', NULL);
INSERT INTO `sys_dict` VALUES ('1867505842824273921', '1867504712337711105', '0,1867504712337711105', '岗位', NULL, NULL, '2', 2, '0', 'system', 'system', '', '', '2024-12-13 17:44:24', 'system', '2024-12-17 08:46:31', NULL);
INSERT INTO `sys_dict` VALUES ('1867505878689767425', '1867504712337711105', '0,1867504712337711105', '人员', NULL, NULL, '4', 4, '0', 'system', 'system', '', '', '2024-12-13 17:44:33', 'system', '2024-12-13 17:44:33', NULL);
INSERT INTO `sys_dict` VALUES ('1876101937989017601', '1507952180051705857', '0,1507952180051705857', '本地上传', NULL, NULL, '0', 0, '0', 'system', 'system', '', '', '2025-01-06 11:02:13', 'system', '2025-01-06 11:02:13', NULL);

-- ----------------------------
-- Table structure for sys_error_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_error_log`;
CREATE TABLE `sys_error_log`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `error_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '错误编号',
  `exception_class` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '异常类型',
  `exception_message` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '异常信息',
  `stacktrace` blob NULL COMMENT '堆栈信息',
  `log_method` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '方法名',
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作人ID',
  `user_nickname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作人',
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作人账号',
  `ip` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '错误日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_error_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_file
-- ----------------------------
DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `file_key` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件Key',
  `file_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件名称',
  `file_size` decimal(50, 0) NULL DEFAULT NULL COMMENT '文件大小',
  `file_path` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件地址',
  `is_editable` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否可编辑 (1是 0否)',
  `content_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '响应头类型',
  `upload_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上传方式',
  `ref_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '业务类型',
  `ref_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '业务ID',
  `is_deleted` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0正常 1删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建者',
  `create_user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人ID',
  `create_dept` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人部门ID',
  `create_organization` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人公司ID',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件中心' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_file
-- ----------------------------

-- ----------------------------
-- Table structure for sys_file_record
-- ----------------------------
DROP TABLE IF EXISTS `sys_file_record`;
CREATE TABLE `sys_file_record`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `file_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件ID',
  `file_key` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件Key',
  `file_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件名称',
  `file_size` decimal(50, 0) NULL DEFAULT NULL COMMENT '文件大小',
  `file_path` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件地址',
  `is_deleted` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0正常 1删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建者',
  `create_user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人ID',
  `create_dept` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人部门ID',
  `create_organization` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人公司ID',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件回调记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_file_record
-- ----------------------------

-- ----------------------------
-- Table structure for sys_front_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_front_user`;
CREATE TABLE `sys_front_user`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `is_deleted` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0正常 1删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建者',
  `create_user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人ID',
  `create_dept` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人部门ID',
  `create_organization` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人公司ID',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  `user_status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '启用状态',
  `nickname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户昵称',
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登录账号',
  `password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登录密码',
  `password_base` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '密码加密',
  `wx_openid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '绑定的微信号',
  `wx_unionid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '开放平台的唯一标识符',
  `phone` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '性别',
  `avatar` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '头像路径',
  `last_login_ip` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最后登陆IP',
  `last_login_date` datetime NULL DEFAULT NULL COMMENT '最后登陆时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '前台用户' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_front_user
-- ----------------------------

-- ----------------------------
-- Table structure for sys_gen_table
-- ----------------------------
DROP TABLE IF EXISTS `sys_gen_table`;
CREATE TABLE `sys_gen_table`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `table_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '表名',
  `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '实体类名称',
  `comments` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '表说明',
  `package_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成模块名',
  `function_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成功能作者',
  `options` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '其它生成选项',
  `menu_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属菜单ID',
  `is_deleted` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0正常 1删除 2停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建者',
  `create_user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人ID',
  `create_dept` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人部门ID',
  `create_organization` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人公司ID',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码生成表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_gen_table
-- ----------------------------

-- ----------------------------
-- Table structure for sys_gen_table_column
-- ----------------------------
DROP TABLE IF EXISTS `sys_gen_table_column`;
CREATE TABLE `sys_gen_table_column`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `table_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '表ID',
  `column_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '列名',
  `column_sort` decimal(10, 0) NULL DEFAULT NULL COMMENT '列排序（升序）',
  `column_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型',
  `column_length` bigint NULL DEFAULT NULL COMMENT '长度',
  `comments` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '列备注说明',
  `attr_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '类的属性名',
  `attr_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '类的属性类型',
  `is_pk` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否主键',
  `is_not_null` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否不为空',
  `is_list` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否列表',
  `is_form` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否可编辑',
  `query_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '查询方式',
  `options` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '其它生成选项',
  `components` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件',
  `dict_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典code',
  `row_style` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '行布局 (1 100% 2 50%)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_gen_table_column_tn`(`table_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码生成表列' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_gen_table_column
-- ----------------------------

-- ----------------------------
-- Table structure for sys_identity
-- ----------------------------
DROP TABLE IF EXISTS `sys_identity`;
CREATE TABLE `sys_identity`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `role_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色ID',
  `dept_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '部门ID',
  `organization_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '机构ID',
  `post_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '岗位ID',
  `is_deleted` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0正常 1删除 2停用）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '身份' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_identity
-- ----------------------------
INSERT INTO `sys_identity` VALUES ('1650709770333605889', '1491958375410450434', '1491958183135166466', '1650709569262866433', '1650709468683456514', '1502447074023813122', '0');
INSERT INTO `sys_identity` VALUES ('1805510274011111425', '1491958375410450434', '1491958183135166466', '1650709616285208577', '1650709468683456514', '1502447410981613570', '0');

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '执行器主键ID',
  `job_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务描述',
  `alarm_email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '报警邮件',
  `schedule_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度类型',
  `schedule_conf` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '调度配置，值含义取决于调度类型',
  `misfire_strategy` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度过期策略',
  `executor_route_strategy` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '执行器路由策略',
  `executor_handler` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '执行器任务handler',
  `executor_param` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '执行器任务参数',
  `executor_block_strategy` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '阻塞处理策略',
  `executor_timeout` int NOT NULL DEFAULT 0 COMMENT '任务执行超时时间，单位秒',
  `executor_fail_retry_count` int NOT NULL DEFAULT 0 COMMENT '失败重试次数',
  `glue_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'GLUE类型',
  `glue_source` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'GLUE源代码',
  `glue_remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'GLUE备注',
  `glue_update_time` datetime NULL DEFAULT NULL COMMENT 'GLUE更新时间',
  `child_job_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '子任务ID，多个逗号分隔',
  `trigger_status` tinyint NOT NULL DEFAULT 0 COMMENT '调度状态：0-停止，1-运行',
  `trigger_last_time` bigint NOT NULL DEFAULT 0 COMMENT '上次调度时间',
  `trigger_next_time` bigint NOT NULL DEFAULT 0 COMMENT '下次调度时间',
  `is_deleted` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0正常 1删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建者',
  `create_user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人ID',
  `create_dept` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人部门ID',
  `create_organization` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人公司ID',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '任务' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_job
-- ----------------------------
INSERT INTO `sys_job` VALUES ('1746541997541515265', '1724151927454830594', '测试任务', '3163077578@qq.com', 'CRON', '*/5 * * * * ?', 'DO_NOTHING', 'FIRST', 'demoJobHandler', NULL, 'SERIAL_EXECUTION', 0, 0, 'BEAN', NULL, NULL, '2024-12-05 09:41:33', NULL, 0, 0, 0, '0', 'system', 'system', '', '', '2024-01-14 22:37:16', 'system', '2024-12-05 09:41:33', NULL);

-- ----------------------------
-- Table structure for sys_job_group
-- ----------------------------
DROP TABLE IF EXISTS `sys_job_group`;
CREATE TABLE `sys_job_group`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `app_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '执行器AppName',
  `title` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '执行器名称',
  `address_type` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '执行器地址类型：0=自动注册、1=手动录入',
  `address_list` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '执行器地址列表，多地址逗号分隔',
  `online_status` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '执行器状态',
  `is_deleted` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0正常 1删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建者',
  `create_user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人ID',
  `create_dept` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人部门ID',
  `create_organization` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人公司ID',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '执行器' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sys_job_lock
-- ----------------------------
DROP TABLE IF EXISTS `sys_job_lock`;
CREATE TABLE `sys_job_lock`  (
  `lock_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '锁名称',
  PRIMARY KEY (`lock_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '任务锁' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_job_lock
-- ----------------------------
INSERT INTO `sys_job_lock` VALUES ('schedule_lock');

-- ----------------------------
-- Table structure for sys_job_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_job_log`;
CREATE TABLE `sys_job_log`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '执行器主键ID',
  `job_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务，主键ID',
  `executor_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '执行器地址，本次执行的地址',
  `executor_handler` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '执行器任务handler',
  `executor_param` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '执行器任务参数',
  `executor_sharding_param` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '执行器任务分片参数，格式如 1/2',
  `executor_fail_retry_count` int NOT NULL DEFAULT 0 COMMENT '失败重试次数',
  `trigger_time` datetime NULL DEFAULT NULL COMMENT '调度-时间',
  `trigger_code` int NULL DEFAULT NULL COMMENT '调度-结果',
  `trigger_msg` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '调度-日志',
  `handle_time` datetime NULL DEFAULT NULL COMMENT '执行-时间',
  `handle_code` int NULL DEFAULT NULL COMMENT '执行-状态',
  `handle_msg` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '执行-日志',
  `alarm_status` tinyint NOT NULL DEFAULT 0 COMMENT '告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '任务执行日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_job_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_job_log_report
-- ----------------------------
DROP TABLE IF EXISTS `sys_job_log_report`;
CREATE TABLE `sys_job_log_report`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `trigger_day` datetime NULL DEFAULT NULL COMMENT '调度-时间',
  `running_count` int NOT NULL DEFAULT 0 COMMENT '运行中-日志数量',
  `suc_count` int NOT NULL DEFAULT 0 COMMENT '执行成功-日志数量',
  `fail_count` int NOT NULL DEFAULT 0 COMMENT '执行失败-日志数量',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `i_trigger_day`(`trigger_day` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '调度日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_job_log_report
-- ----------------------------

-- ----------------------------
-- Table structure for sys_job_registry
-- ----------------------------
DROP TABLE IF EXISTS `sys_job_registry`;
CREATE TABLE `sys_job_registry`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `registry_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '注册组',
  `registry_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '注册key',
  `registry_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '注册值',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `i_g_k_v`(`registry_group` ASC, `registry_key` ASC, `registry_value` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '任务注册' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_job_registry
-- ----------------------------
INSERT INTO `sys_job_registry` VALUES ('1881155858198609922', 'EXECUTOR', 'smart-job', 'http://192.168.1.200:9999/', '2025-01-20 10:26:12');

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `log_module` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模块',
  `log_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日志类型',
  `log_desc` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '日志描述',
  `log_params` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '日志参数',
  `log_result` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '日志结果',
  `log_method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '方法名',
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作人ID',
  `user_nickname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作人',
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作人账号',
  `ip` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_login_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作人ID',
  `user_nickname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作人',
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作人账号',
  `grant_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登录方式',
  `ip` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '登录日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_login_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `is_deleted` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0正常 1删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建者',
  `create_user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人ID',
  `create_dept` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人部门ID',
  `create_organization` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人公司ID',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  `parent_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '父级ID',
  `menu_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单类型',
  `menu_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单名称',
  `route_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路由名称',
  `route_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路由路径',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件',
  `path_param` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路径参数',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
  `icon_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标类型',
  `ant_icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Ant Design Icon',
  `i18n_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '国际化key',
  `keep_alive` bit(1) NULL DEFAULT NULL COMMENT '缓存路由',
  `constant` bit(1) NULL DEFAULT NULL COMMENT '常量路由',
  `order` int NULL DEFAULT NULL COMMENT '排序',
  `href` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '外链',
  `redirect` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '重定向',
  `hide_in_menu` bit(1) NULL DEFAULT NULL COMMENT '隐藏菜单',
  `active_menu` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '高亮的菜单',
  `multi_tab` bit(1) NULL DEFAULT NULL COMMENT '支持多页签',
  `fixed_index_in_tab` int NULL DEFAULT NULL COMMENT '固定在页签中的序号',
  `query` json NULL COMMENT '路由参数',
  `layout` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '布局',
  `props` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'props参数',
  `weight` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权重',
  `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '启用状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES ('1477238932050690050', '0', 'system', 'system', NULL, NULL, '2024-06-16 21:45:29', 'system', '2024-08-03 09:33:28', NULL, '1638414940387676161', '2', '机构管理', 'system_dept', '/system/dept', 'view.system_dept', NULL, 'clarity:organization-line', '1', 'DeploymentUnitOutlined', 'route.system_dept', b'1', b'0', 101, NULL, NULL, b'0', NULL, b'0', NULL, NULL, NULL, NULL, '2', '1');
INSERT INTO `sys_menu` VALUES ('1477239028339326977', '0', 'system', 'system', NULL, NULL, '2024-06-16 21:45:29', 'system', '2024-08-03 09:32:09', NULL, '1638414940387676161', '2', '人员管理', 'system_user', '/system/user', 'view.system_user', NULL, 'ic:round-manage-accounts', '1', 'UserOutlined', 'route.system_user', b'0', b'0', 103, NULL, NULL, b'0', NULL, b'0', NULL, NULL, NULL, NULL, '2', '1');
INSERT INTO `sys_menu` VALUES ('1477239136195854337', '0', 'system', 'system', NULL, NULL, '2024-06-16 21:45:29', 'system', '2024-06-27 14:09:45', NULL, '1638414940387676161', '2', '数据字典', 'system_dict', '/system/dict', 'view.system_dict', NULL, 'fluent-mdl2:dictionary', '1', 'FileTextOutlined', 'route.system_dict', b'0', b'0', 107, NULL, NULL, b'0', NULL, b'0', NULL, NULL, NULL, NULL, '2', '1');
INSERT INTO `sys_menu` VALUES ('1477239327678414750', '0', 'system', 'system', NULL, NULL, '2024-06-16 21:45:29', 'system', '2024-06-23 23:10:41', NULL, '1638414940387676161', '2', '菜单管理', 'system_menu', '/system/menu', 'view.system_menu', NULL, 'material-symbols:route', '1', 'MenuOutlined', 'route.system_menu', b'0', b'0', 105, NULL, NULL, b'0', NULL, b'0', NULL, NULL, NULL, NULL, '2', '1');
INSERT INTO `sys_menu` VALUES ('1477239327678414850', '0', 'system', 'system', NULL, NULL, '2024-06-16 21:45:29', 'system', '2024-08-01 09:19:16', NULL, '1812764791497289729', '2', '代码生成器', 'tools_gen', '/tools/gen', 'view.tools_gen', NULL, 'material-symbols:terminal', '1', 'SettingOutlined', 'route.tools_gen', b'0', b'0', 400, NULL, NULL, b'0', NULL, b'0', NULL, NULL, NULL, NULL, '2', '1');
INSERT INTO `sys_menu` VALUES ('1481002161528868865', '0', 'system', 'system', NULL, NULL, '2024-06-16 21:45:29', 'system', '2024-06-23 23:11:04', NULL, '1638414940387676161', '2', '角色管理', 'system_role', '/system/role', 'view.system_role', NULL, 'carbon:user-role', '1', 'SolutionOutlined', 'route.system_role', b'0', b'0', 104, NULL, NULL, b'0', NULL, b'0', NULL, NULL, NULL, NULL, '2', '1');
INSERT INTO `sys_menu` VALUES ('1483266586797350913', '0', 'system', 'system', NULL, NULL, '2024-06-16 21:45:29', 'system', '2024-12-30 16:24:10', NULL, '1638414940387676161', '1', '系统监控', 'system_monitor', '/system/monitor', '', NULL, 'carbon:cloud-monitoring', '1', 'FundProjectionScreenOutlined', 'route.system_monitor', b'0', b'0', 112, NULL, NULL, b'0', NULL, b'0', NULL, NULL, NULL, NULL, '2', '1');
INSERT INTO `sys_menu` VALUES ('1483357745183875074', '0', 'system', 'system', NULL, NULL, '2024-06-16 21:47:43', 'system', '2024-07-03 08:45:44', NULL, '1483266586797350913', '2', '数据监控', 'system_monitor_druid', '/system/monitor/druid', 'view.system_monitor_druid', NULL, 'icon-park-outline:data-user', '1', 'DashboardOutlined', 'route.system_monitor_druid', b'0', b'0', 1, '', NULL, b'0', NULL, b'0', NULL, NULL, NULL, NULL, '2', '1');
INSERT INTO `sys_menu` VALUES ('1485207321130029057', '0', 'system', 'system', NULL, NULL, '2024-06-16 21:45:29', 'system', '2024-07-10 23:49:06', NULL, '1638414940387676161', '2', '数据权限', 'system_scope', '/system/scope', 'view.system_scope', NULL, 'bx:data', '1', 'ReadOutlined', 'route.system_scope', b'0', b'0', 106, NULL, NULL, b'0', NULL, b'0', NULL, NULL, NULL, NULL, '2', '1');
INSERT INTO `sys_menu` VALUES ('1488415792415588353', '0', 'system', 'system', NULL, NULL, '2024-06-16 21:45:29', 'system', '2024-07-02 14:23:59', NULL, '1552190538357305346', '2', '操作日志', 'system_logs_operate', '/system/logs/operate', 'view.system_logs_operate', NULL, 'ep:operation', '1', 'DiffOutlined', 'route.system_logs_operate', b'0', b'0', 1, NULL, NULL, b'0', NULL, b'0', NULL, NULL, NULL, NULL, '2', '1');
INSERT INTO `sys_menu` VALUES ('1502438095121973250', '0', 'system', 'system', NULL, NULL, '2024-06-16 21:45:29', 'system', '2024-06-26 14:06:44', NULL, '1638414940387676161', '2', '岗位管理', 'system_post', '/system/post', 'view.system_post', NULL, 'hugeicons:job-share', '1', 'ContactsOutlined', 'route.system_post', b'0', b'0', 102, NULL, NULL, b'0', NULL, b'0', NULL, NULL, NULL, NULL, '2', '1');
INSERT INTO `sys_menu` VALUES ('1507937416818860034', '0', 'system', 'system', NULL, NULL, '2024-06-16 21:45:29', 'system', '2024-12-30 16:23:55', NULL, '1638414940387676161', '2', '存储管理', 'system_oss', '/system/oss', 'view.system_oss', NULL, 'mdi:storage', '1', 'CloudServerOutlined', 'route.system_oss', b'0', b'0', 110, NULL, NULL, b'0', NULL, b'0', NULL, '[{\"key\": \"\", \"value\": \"\"}]', NULL, NULL, '2', '1');
INSERT INTO `sys_menu` VALUES ('1507980643286515713', '0', 'system', 'system', NULL, NULL, '2024-06-16 21:45:29', 'system', '2024-07-22 17:59:41', NULL, '0', '2', '文件管理', 'file', '/file', 'layout.base$view.file', NULL, 'mdi:folder-outline', '1', 'FileOutlined', 'route.file', b'0', b'0', 300, NULL, NULL, b'0', NULL, b'0', NULL, '[{\"key\": \"\", \"value\": \"\"}]', 'base', NULL, '2', '1');
INSERT INTO `sys_menu` VALUES ('1510410640500604929', '0', 'system', 'system', NULL, NULL, '2024-06-16 21:45:29', 'system', '2024-07-05 23:29:03', NULL, '1511969257825665025', '2', '任务列表', 'task_job', '/task/job', 'view.task_job', NULL, 'arcticons:jobstreet', '1', 'BarChartOutlined', 'route.task_job', b'0', b'0', 2, NULL, NULL, b'0', NULL, b'0', NULL, NULL, NULL, NULL, '2', '1');
INSERT INTO `sys_menu` VALUES ('1511936861684269057', '0', 'system', 'system', NULL, NULL, '2024-06-16 21:45:29', 'system', '2024-07-06 21:57:58', NULL, '1511969257825665025', '2', '执行日志', 'task_log', '/task/log', 'view.task_log', NULL, 'ph:log', '1', 'OrderedListOutlined', 'route.task_log', b'0', b'0', 3, NULL, NULL, b'0', NULL, b'0', NULL, NULL, NULL, NULL, '2', '1');
INSERT INTO `sys_menu` VALUES ('1511936878172078081', '0', 'system', 'system', NULL, NULL, '2024-06-16 21:45:29', 'system', '2024-07-03 22:20:13', NULL, '1511969257825665025', '2', '执行器管理', 'task_group', '/task/group', 'view.task_group', NULL, 'carbon:executable-program', '1', 'HistoryOutlined', 'route.task_group', b'0', b'0', 1, NULL, NULL, b'0', NULL, b'0', NULL, NULL, NULL, NULL, '2', '1');
INSERT INTO `sys_menu` VALUES ('1511969257825665025', '0', 'system', 'system', NULL, NULL, '2024-06-16 21:45:29', 'system', '2024-07-15 16:25:23', NULL, '0', '1', '任务管理', 'task', '/task', 'layout.base', NULL, 'bx:task', '1', 'PlaySquareOutlined', 'route.task', b'0', b'0', 500, NULL, NULL, b'0', NULL, b'0', NULL, NULL, NULL, NULL, '2', '1');
INSERT INTO `sys_menu` VALUES ('1552180830791774210', '0', 'system', 'system', NULL, NULL, '2024-06-16 21:45:29', 'system', '2024-07-02 23:14:14', NULL, '1552190538357305346', '2', '异常日志', 'system_logs_error', '/system/logs/error', 'view.system_logs_error', NULL, 'ic:baseline-error', '1', 'AlertOutlined', 'route.system_logs_error', b'0', b'0', 2, NULL, NULL, b'0', NULL, b'0', NULL, NULL, NULL, NULL, '2', '1');
INSERT INTO `sys_menu` VALUES ('1552190538357305346', '0', 'system', 'system', NULL, NULL, '2024-06-16 21:45:29', 'system', '2024-12-30 16:24:04', NULL, '1638414940387676161', '1', '日志管理', 'system_logs', '/system/logs', '', NULL, 'mdi:math-log', '1', 'GatewayOutlined', 'route.system_logs', b'0', b'0', 111, NULL, NULL, b'0', NULL, b'0', NULL, NULL, NULL, NULL, '2', '1');
INSERT INTO `sys_menu` VALUES ('1588072580118642690', '0', 'system', 'system', NULL, NULL, '2024-06-16 21:45:29', 'system', '2024-07-02 15:07:06', NULL, '1552190538357305346', '2', '登录日志', 'system_logs_login', '/system/logs/login', 'view.system_logs_login', NULL, 'majesticons:login-line', '1', 'AntDesignOutlined', 'route.system_logs_login', b'0', b'0', 3, NULL, NULL, b'0', NULL, b'0', NULL, NULL, NULL, NULL, '2', '1');
INSERT INTO `sys_menu` VALUES ('1638414940387676161', '0', 'system', 'system', NULL, NULL, '2024-06-16 21:45:29', 'system', '2024-07-15 17:12:23', NULL, '0', '1', '系统管理', 'system', '/system', 'layout.base', NULL, 'carbon:cloud-service-management', '1', 'SettingOutlined', 'route.system', b'0', b'0', 100, NULL, NULL, b'0', NULL, b'0', NULL, '[{\"key\": \"test\", \"value\": \"123\"}]', NULL, NULL, '2', '1');
INSERT INTO `sys_menu` VALUES ('1663455985822781441', '0', 'system', 'system', NULL, NULL, '2024-06-16 21:45:29', 'system', '2024-07-03 09:20:37', NULL, '1638414940387676161', '2', '参数管理', 'system_config', '/system/config', 'view.system_config', NULL, 'eos-icons:configuration-file-outlined', '1', 'ToolOutlined', 'route.system_config', b'0', b'0', 108, NULL, NULL, b'0', NULL, b'0', NULL, NULL, NULL, NULL, '2', '1');
INSERT INTO `sys_menu` VALUES ('1812764791497289729', '0', 'system', 'system', NULL, NULL, '2024-07-15 16:23:00', 'system', '2024-07-22 13:25:04', NULL, '0', '1', '系统工具', 'tools', '/tools', 'layout.base', NULL, 'carbon:tool-box', '1', 'OneToOneOutlined', 'route.tools', b'0', b'0', 200, NULL, NULL, b'0', NULL, b'0', NULL, NULL, NULL, NULL, '2', '1');
INSERT INTO `sys_menu` VALUES ('1812863571709693953', '0', 'system', 'system', NULL, NULL, '2024-07-15 22:55:32', 'system', '2024-08-07 15:38:31', NULL, '1812764791497289729', '2', '生成案例', 'tools_gen-demo', '/tools/gen-demo', 'view.tools_gen-demo', NULL, 'carbon:demo', '1', 'SettingOutlined', 'route.tools_gen-demo', b'1', b'0', 401, NULL, NULL, NULL, NULL, b'0', NULL, NULL, NULL, NULL, '2', '1');
INSERT INTO `sys_menu` VALUES ('1814490981215551489', '0', 'system', 'system', NULL, NULL, '2024-07-20 10:42:16', 'system', '2024-11-22 09:21:47', NULL, '0', '2', '关于', 'about', '/about', 'layout.base$view.about', NULL, 'fluent:book-information-24-regular', '1', 'AppstoreOutlined', 'route.about', b'0', b'0', 9999, NULL, NULL, b'0', NULL, b'0', NULL, NULL, NULL, NULL, '2', '1');
INSERT INTO `sys_menu` VALUES ('1821447561915924482', '0', 'system', 'system', NULL, NULL, '2024-08-08 15:25:14', 'system', '2024-09-25 10:26:58', NULL, '0', '1', '会员管理', 'front-user-manage', '/front-user-manage', 'layout.base', NULL, 'ph:users', '1', 'UserOutlined', 'route.front-user-manage', b'0', b'0', 2, NULL, NULL, b'0', NULL, b'0', NULL, NULL, NULL, NULL, '2', '1');
INSERT INTO `sys_menu` VALUES ('1821461884822388737', '0', 'system', 'system', NULL, NULL, '2024-08-08 16:22:09', 'system', '2024-09-25 10:26:53', NULL, '1821447561915924482', '2', '会员列表', 'front-user-manage_front-user', '/front-user-manage/front-user', 'view.front-user-manage_front-user', NULL, 'ph:users-four', '1', 'UserOutlined', 'route.front-user-manage_front-user', b'1', b'0', 0, NULL, NULL, NULL, NULL, b'0', NULL, NULL, NULL, NULL, '2', '1');
INSERT INTO `sys_menu` VALUES ('1867495401284767745', '0', 'system', 'system', NULL, NULL, '2024-12-13 17:02:55', 'system', '2024-12-23 17:39:55', NULL, '0', '2', '通知公告', 'notice', '/notice', 'layout.base$view.notice', NULL, 'tabler:message', '1', 'MessageOutlined', 'route.notice', b'1', b'0', 3, NULL, NULL, NULL, NULL, b'0', NULL, NULL, NULL, NULL, '2', '1');
INSERT INTO `sys_menu` VALUES ('1873646530771025922', '0', 'system', 'system', NULL, NULL, '2024-12-30 16:25:18', 'system', '2024-12-31 11:45:37', NULL, '1638414940387676161', '2', '系统设置', 'system_setting', '/system/setting', 'view.system_setting', NULL, 'uil:setting', '1', 'SaveOutlined', 'route.system_setting', b'0', b'0', 109, NULL, '', b'0', NULL, b'0', NULL, NULL, NULL, '', '1', '1');
INSERT INTO `sys_menu` VALUES ('1877554252449132545', '0', 'system', 'system', NULL, NULL, '2025-01-10 11:13:12', 'system', '2025-01-15 17:03:08', NULL, '1638414940387676161', '2', '缓存管理', 'system_redis', '/system/redis', 'view.system_redis', NULL, 'logos:redis', '1', 'DatabaseOutlined', 'route.system_redis', b'0', b'0', 113, NULL, '', b'0', NULL, b'0', NULL, NULL, NULL, '', '1', '1');

-- ----------------------------
-- Table structure for sys_message
-- ----------------------------
DROP TABLE IF EXISTS `sys_message`;
CREATE TABLE `sys_message`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `message_title` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '消息标题',
  `message_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '消息内容',
  `message_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '发送状态（1已发送 0未发送）',
  `is_to_mail` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否同步发送邮箱',
  `mail_send_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱发件人名称',
  `send_time` datetime NULL DEFAULT NULL COMMENT '发送时间',
  `receive_users` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '接收人IDS',
  `is_deleted` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0正常 1删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建者',
  `create_user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人ID',
  `create_dept` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人部门ID',
  `create_organization` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人公司ID',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_message
-- ----------------------------

-- ----------------------------
-- Table structure for sys_message_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_message_user`;
CREATE TABLE `sys_message_user`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `chat_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会话ID',
  `message_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '消息ID',
  `send_time` datetime NOT NULL COMMENT '发送时间',
  `receive_user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '接收人ID',
  `is_read` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否已读',
  `read_time` datetime NULL DEFAULT NULL COMMENT '已读时间',
  `deleted_status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '删除状态',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `message_id_index`(`message_id` ASC) USING BTREE COMMENT '消息ID索引',
  INDEX `receive_user_index`(`receive_user` ASC) USING BTREE COMMENT '人员ID索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '消息-用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_message_user
-- ----------------------------

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `is_deleted` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0正常 1删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建者',
  `create_user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人ID',
  `create_dept` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人部门ID',
  `create_organization` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人公司ID',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  `notice_title` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标题',
  `notice_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '内容',
  `notice_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类型（1普通 2紧急）',
  `notice_category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类别（1公告 2通知）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知公告' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_notice
-- ----------------------------

-- ----------------------------
-- Table structure for sys_notice_record
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice_record`;
CREATE TABLE `sys_notice_record`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `is_deleted` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0正常 1删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建者',
  `create_user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人ID',
  `create_dept` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人部门ID',
  `create_organization` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人公司ID',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  `notice_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '通知公告ID',
  `release_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `release_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '发布类型',
  `release_value` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '发布数据IDS',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知公告发布记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_notice_record
-- ----------------------------

-- ----------------------------
-- Table structure for sys_notice_ref
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice_ref`;
CREATE TABLE `sys_notice_ref`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `notice_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '通知公告ID',
  `send_time` datetime NULL DEFAULT NULL COMMENT '发送时间',
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户ID',
  `is_read` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否已读（0否 1是）',
  `read_time` datetime NULL DEFAULT NULL COMMENT '已读时间',
  `is_cancel` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否撤销（0否 1是）',
  `cancel_time` datetime NULL DEFAULT NULL COMMENT '撤销时间',
  `release_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '发布ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知公告-用户' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_notice_ref
-- ----------------------------

-- ----------------------------
-- Table structure for sys_oss
-- ----------------------------
DROP TABLE IF EXISTS `sys_oss`;
CREATE TABLE `sys_oss`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `oss_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '存储类型',
  `oss_host` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地址',
  `access_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'key',
  `access_secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'secret',
  `bucket` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '桶名',
  `oss_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否启用',
  `oss_dir` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '目录',
  `region` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地区',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '对象存储' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_oss
-- ----------------------------
INSERT INTO `sys_oss` VALUES ('1876106235711971329', '0', NULL, NULL, NULL, 'proxy-upload', '1', 'C:\\Users\\34248\\Desktop\\upload', NULL);

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `is_deleted` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0正常 1删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建者',
  `create_user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人ID',
  `create_dept` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人部门ID',
  `create_organization` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人公司ID',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  `category` int NULL DEFAULT NULL COMMENT '岗位类型',
  `post_code` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '岗位编号',
  `post_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '岗位名称',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '启用状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '岗位' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_post
-- ----------------------------
INSERT INTO `sys_post` VALUES ('1502447074023813122', '0', 'system', NULL, NULL, NULL, '2022-03-12 00:51:11', 'system', '2022-05-21 11:14:03', NULL, NULL, 'staff', '普通员工', 1, '1');
INSERT INTO `sys_post` VALUES ('1502447410981613570', '0', 'system', NULL, NULL, NULL, '2022-03-12 00:52:32', 'system', '2022-03-12 01:01:35', NULL, NULL, 'manager', '总经理', 2, '1');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `is_deleted` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0正常 1删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建者',
  `create_user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人ID',
  `create_dept` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人部门ID',
  `create_organization` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人公司ID',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '启用状态',
  `role_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色编码',
  `role_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `role_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('1491958183135166466', '0', 'system', NULL, NULL, NULL, '2022-02-11 02:12:05', '1805510274011111425', '2024-11-13 17:12:10', NULL, '1', 'admin', '系统管理员', '管理系统一切事务的管理人员');
INSERT INTO `sys_role` VALUES ('1491959087703932929', '0', 'system', NULL, NULL, NULL, '2022-02-11 02:15:40', 'system', '2022-02-11 02:22:25', NULL, '1', 'staff', '普通员工', '公司内普通员工');
INSERT INTO `sys_role` VALUES ('1506113423581253634', '0', 'system', NULL, NULL, NULL, '2022-03-22 03:39:57', 'system', '2024-08-01 11:25:37', NULL, '1', 'leader', '部门负责人', '公司内部门负责人');

-- ----------------------------
-- Table structure for sys_role_buttons
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_buttons`;
CREATE TABLE `sys_role_buttons`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `role_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色ID',
  `button_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '按钮code',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `button_code_index`(`button_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色-按钮' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_buttons
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `role_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色ID',
  `menu_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色-菜单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role_scope
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_scope`;
CREATE TABLE `sys_role_scope`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `scope_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据权限编码',
  `role_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色编码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色-数据权限' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_scope
-- ----------------------------

-- ----------------------------
-- Table structure for sys_scope
-- ----------------------------
DROP TABLE IF EXISTS `sys_scope`;
CREATE TABLE `sys_scope`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `menu_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单ID',
  `scope_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据权限名称',
  `scope_field` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据权限字段',
  `scope_class` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据权限类名',
  `scope_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据权限类型',
  `scope_sql` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '自定义数据权限规则',
  `visibility_ids` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '可见IDS',
  `is_deleted` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0正常 1删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建者',
  `create_user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人ID',
  `create_dept` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人部门ID',
  `create_organization` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人公司ID',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据权限' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_scope
-- ----------------------------

-- ----------------------------
-- Table structure for sys_tenant
-- ----------------------------
DROP TABLE IF EXISTS `sys_tenant`;
CREATE TABLE `sys_tenant`  (
  `id` bigint NOT NULL,
  `tenant_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '000000',
  `tenant_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `domain` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `background_url` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `linkman` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `contact_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `account_number` int NULL DEFAULT -1,
  `expire_time` datetime NULL DEFAULT NULL,
  `is_deleted` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0正常 1删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建者',
  `create_user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人ID',
  `create_dept` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人部门ID',
  `create_organization` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人公司ID',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_tenant
-- ----------------------------
INSERT INTO `sys_tenant` VALUES (1123598820738675201, '000000', '管理组', '', '', 'admin', '666666', '管理组', -1, NULL, '0', 'system', NULL, NULL, NULL, '2023-11-17 13:31:50', 'system', '2023-11-17 13:31:52', NULL);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `nickname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户昵称',
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录账号',
  `password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录密码',
  `password_base` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '密码加密',
  `phone` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '办公电话',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '性别',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像路径',
  `wx_openid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '绑定的微信号',
  `wx_unionid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '开放平台的唯一标识符',
  `git_lab_token` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'GitLab访问令牌',
  `last_login_ip` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最后登陆IP',
  `last_login_date` datetime NULL DEFAULT NULL COMMENT '最后登陆时间',
  `user_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '状态 0 停用 1 正常  2 冻结',
  `freeze_date` datetime NULL DEFAULT NULL COMMENT '冻结时间',
  `freeze_cause` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '冻结原因',
  `is_sys` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否系统 1是 0否',
  `is_deleted` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0正常 1删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建者',
  `create_user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人ID',
  `create_dept` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人部门ID',
  `create_organization` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人公司ID',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sys_user_lc`(`username` ASC) USING BTREE,
  INDEX `idx_sys_user_wo`(`wx_openid` ASC) USING BTREE,
  INDEX `idx_sys_user_ud`(`update_date` ASC) USING BTREE,
  INDEX `idx_sys_user_status`(`is_deleted` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('1491958375410450434', '系统管理员', 'admin', 'e10adc3949ba59abbe56e057f20f883e', 'QEwd/DWmy/4yGncCqBofQQ==', '18322049108', NULL, '3163077578@qq.com', 'https://pst-test.oss-cn-beijing.aliyuncs.com/pst-test/file/2-768af9e13e3143b5af58e8dde6856abb.jpg', NULL, NULL, '038bdaf98f2037b31f1e75b5b4c9b26e', '0:0:0:0:0:0:0:1', '2025-01-09 17:11:41', '1', NULL, NULL, '1', '0', 'system', NULL, NULL, NULL, '2022-02-11 02:12:51', 'system', '2025-01-15 10:05:55', NULL);
INSERT INTO `sys_user` VALUES ('system', '超级管理员', 'system', 'e10adc3949ba59abbe56e057f20f883e', NULL, '18322049102', NULL, NULL, 'https://pst-test.oss-cn-beijing.aliyuncs.com/pst-test/file/2-768af9e13e3143b5af58e8dde6856abb.jpg', '5d11065b97f647eeb93e7aa1e3a1ad20', NULL, '5d11065b97f647eeb93e7aa1e3a1ad20', '106.47.31.176', '2025-01-17 10:43:12', '1', NULL, NULL, '1', '0', 'system', NULL, NULL, NULL, '2021-04-08 12:57:06', 'system', '2024-08-15 10:31:50', '123444');

-- ----------------------------
-- Table structure for sys_user_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_config`;
CREATE TABLE `sys_user_config`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `config_value` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '用户配置',
  `naive_ui_config_value` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'naive-ui用户配置',
  `lang_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '国际化配置',
  `quick_entry_value` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '快捷入口配置',
  `naive_ui_quick_entry_value` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'naive-ui 快捷入口配置',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_config
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;

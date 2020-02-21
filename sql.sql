CREATE TABLE `permission_system` (
 `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键',
 `name` varchar(64) DEFAULT NULL COMMENT '应用名称',
 `code` varchar(64) DEFAULT '' COMMENT '应用code',
 `status` tinyint(3) DEFAULT NULL COMMENT '状态:1启用 2停用',
 `comment` varchar(255) DEFAULT NULL COMMENT '描述',
 `creator` varchar(50) DEFAULT NULL COMMENT '创建人',
 `modifier` varchar(50) DEFAULT NULL COMMENT '更新人',
 `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
 `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
 `home` varchar(256) DEFAULT NULL COMMENT '系统访问URL',
 `owners` varchar(256) DEFAULT NULL COMMENT '系统owners(花名逗号隔开)',
 PRIMARY KEY (`id`),
 UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='权限系统表';

CREATE TABLE `permission_module` (
 `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键',
 `name` varchar(64) DEFAULT NULL COMMENT '应用名称',
 `code` varchar(64) DEFAULT '' COMMENT '应用code',
 `system_code` varchar(64) DEFAULT '' COMMENT '应用code',
 `status` tinyint(3) DEFAULT NULL COMMENT '状态:1启用 2停用',
 `comment` varchar(255) DEFAULT NULL COMMENT '描述',
 `creator` varchar(50) DEFAULT NULL COMMENT '创建人',
 `modifier` varchar(50) DEFAULT NULL COMMENT '更新人',
 `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
 `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
 `home` varchar(256) DEFAULT NULL COMMENT '应用系统访问URL',
 `owners` varchar(256) DEFAULT NULL COMMENT '应用owners(花名逗号隔开)',
 PRIMARY KEY (`id`),
 UNIQUE KEY `uk_code` (`system_code`, `code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='业务模块表';

CREATE TABLE `permission_resource` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(64) DEFAULT NULL COMMENT '资源名称',
  `code` varchar(255) DEFAULT NULL COMMENT '资源编号',
  `parent_code` varchar(255) DEFAULT NULL COMMENT '资源父code',
  `items` varchar(2048) DEFAULT NULL COMMENT '资源项，如URL列表',
  `type` tinyint(3) DEFAULT NULL COMMENT '资源类型  1: Group，2: Action',
  `system_code` varchar(64) DEFAULT NULL COMMENT '系统编号',
  `module_code` varchar(64) DEFAULT NULL COMMENT '系统编号',
  `sort_num` int(10) DEFAULT NULL COMMENT '排序号',
  `comment` varchar(255) DEFAULT NULL COMMENT '资源备注',
  `status` varchar(10) DEFAULT NULL COMMENT '状态',
  `feature` varchar(255) DEFAULT NULL COMMENT '扩展字段',
  `creator` varchar(50) DEFAULT NULL COMMENT '创建人',
  `modifier` varchar(50) DEFAULT NULL COMMENT '更新人',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`system_code`, `code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='资源表';

CREATE TABLE `permission_role` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) DEFAULT NULL COMMENT '角色名称',
  `code` varchar(100) DEFAULT '' COMMENT '角色代号，推荐appcode+rolecode',
  `type` varchar(20) DEFAULT NULL COMMENT '角色类型，超级管理员、系统管理员',
  `status` varchar(10) NOT NULL COMMENT '状态',
  `system_code` varchar(64) NOT NULL DEFAULT '' COMMENT '系统编号',
  `module_code` varchar(64) DEFAULT NULL COMMENT '业务模块编号',
  `comment` varchar(255) DEFAULT NULL COMMENT '描述',
  `creator` varchar(50) DEFAULT NULL COMMENT '创建人',
  `modifier` varchar(50) DEFAULT NULL COMMENT '更新人',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`system_code`, `code`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色表';

CREATE TABLE `permission_role_resource` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_id` int(10) DEFAULT NULL COMMENT '角色id',
  `resource_id` int(10) DEFAULT NULL COMMENT '资源id',
  `creator` varchar(50) DEFAULT NULL COMMENT '创建人',
  `modifier` varchar(50) DEFAULT NULL COMMENT '更新人',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE `uk_roleid` (`role_id`, `resource_id`),
  KEY `idx_resource`(`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色资源关系表';

CREATE TABLE `permission_user` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `usercode` varchar(64) DEFAULT NULL COMMENT '用户编号',
  `name` varchar(64) DEFAULT NULL COMMENT '名称',
  `channel` varchar(10) DEFAULT NULL COMMENT '用户来源渠道',
  `status` tinyint(3) DEFAULT '2' COMMENT '账户状态：1启用,2禁用',
  `mobile` varchar(64) DEFAULT NULL COMMENT '手机号码',
  `third_id` varchar(64) DEFAULT NULL COMMENT '渠道用户ID',
  `creator` varchar(50) DEFAULT NULL COMMENT '创建人',
  `modifier` varchar(50) DEFAULT NULL COMMENT '更新人',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_usercode` (`usercode`, `channel`),
  UNIQUE KEY `uk_third` (`third_id`, `channel`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';

CREATE TABLE `permission_user_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `channel` varchar(10) NOT NULL COMMENT '用户来源',
  `role_id` int(11) NOT NULL COMMENT '角色id',
  `related_key` varchar(32) NOT NULL DEFAULT '' COMMENT '角色关联业务键',
  `system_code` varchar(64) NOT NULL COMMENT '系统编号[冗余]',
  `creator` varchar(50) DEFAULT NULL COMMENT '创建人',
  `modifier` varchar(50) DEFAULT NULL COMMENT '更新人',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `channel`, `role_id`, `related_key`),
  KEY `idx_roleid` (`role_id`, `related_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户角色关系表';

CREATE TABLE `permission_setting` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `code` varchar(64) DEFAULT NULL COMMENT '配置Code',
  `key_code` varchar(64) DEFAULT NULL COMMENT '配置键',
  `value` varchar(2048) DEFAULT NULL COMMENT '值',
  `creator` varchar(50) DEFAULT NULL COMMENT '创建人',
  `modifier` varchar(50) DEFAULT NULL COMMENT '更新人',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code_key` (`code`, `key_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';

CREATE TABLE `permission_event` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `event_type` varchar(64) DEFAULT NULL COMMENT '类型，0 will, 1 done',
  `event_code` varchar(64) DEFAULT NULL COMMENT 'code',
  `val` varchar(64) DEFAULT NULL COMMENT '值',
  `val1` varchar(64) DEFAULT NULL COMMENT '值',
  `creator` varchar(50) DEFAULT NULL COMMENT '创建人',
  `modifier` varchar(50) DEFAULT NULL COMMENT '更新人',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_code` (`event_code`, `val`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';

INSERT INTO `permission_system`(id, `name`, `code`, `status`, `comment`)
VALUES (1, '权限系统', 'UPS', 1, '权限管理系统');


INSERT INTO `permission_role`(id, `name`, `code`, `type`, `status`, `system_code`, `comment`)
VALUES (1, '超级管理员', 'ROOT', 'ROOT', 1, 'UPS', '超级管理员');

-- INSERT INTO `permission_user_role`(`uid`, `role_id) VALUES ({uid}, 1);



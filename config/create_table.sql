/*
SQLyog Ultimate v11.24 (32 bit)
MySQL - 5.0.95 : Database - cronhub_manage_system
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`cronhub_manage_system` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `cronhub_manage_system`;

/*Table structure for table `daemon` */

DROP TABLE IF EXISTS `daemon`;

CREATE TABLE `daemon` (
  `id` bigint(20) unsigned NOT NULL auto_increment,
  `machine_ip` varchar(16) NOT NULL COMMENT '机器ip',
  `machine_port` int(4) NOT NULL COMMENT '机器端口号',
  `daemon_version_name` varchar(100) NOT NULL COMMENT 'daemon版本名',
  `must_lostconn_email` tinyint(1) default NULL COMMENT 'bool.是否失去联络通信的通知email报警',
  `lostconn_emailaddress` varchar(1000) default NULL COMMENT '失去联络通信的报警email,#隔开',
  `conn_status` tinyint(1) default NULL COMMENT '通信状态:0通讯异常，1通信正常',
  `comment` text NOT NULL COMMENT '简介',
  `update_time` datetime NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `machine_ip` (`machine_ip`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

/*Table structure for table `task` */

DROP TABLE IF EXISTS `task`;

CREATE TABLE `task` (
  `id` bigint(20) unsigned NOT NULL auto_increment,
  `daemon_id` bigint(20) unsigned NOT NULL COMMENT '执行此任务daemon执行器的id',
  `cron_exp` varchar(16) NOT NULL COMMENT 'crontab表达式',
  `shell_cmd` varchar(1000) NOT NULL COMMENT '运行的命令,原始命令,未替换参数前',
  `must_replace_cmd` tinyint(1) NOT NULL COMMENT 'bool.是否需要替换`撇号中的命令为执行结果',
  `run_mode` tinyint(1) NOT NULL COMMENT '0--被动模式,1--主动模式',
  `run_start_reportaddress` varchar(500) default NULL COMMENT 'cmd命令开始执行的汇报地址',
  `run_end_reportaddress` varchar(500) default NULL COMMENT 'cmd命令结束执行的汇报地址',
  `is_process_node` tinyint(1) NOT NULL default '0' COMMENT '0--不是流程中的一个节点,1--是流程中的一个节点',
  `is_process_chain` tinyint(1) default '0' COMMENT '0--不是流程链task,1--是流程链task',
  `process_tasks` text COMMENT '流程链模式下的子链条task id集合',
  `comment` text NOT NULL COMMENT '简介',
  `operate_uid` bigint(20) default '-1' COMMENT '操作人的id号',
  `update_time` datetime default NULL COMMENT '修改日期',
  `is_redo` tinyint(1) NOT NULL default '0' COMMENT '0--不重新执行此任务,1--重新执行此任务',
  `end_redo_times` int(11) NOT NULL default '0' COMMENT '截止重新执行次数',
  PRIMARY KEY  (`id`),
  KEY `daemon_id` (`daemon_id`),
  KEY `shell_cmd` (`shell_cmd`(255)),
  CONSTRAINT `task_ibfk_1` FOREIGN KEY (`daemon_id`) REFERENCES `daemon` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8;

/*Table structure for table `task_record_done` */

DROP TABLE IF EXISTS `task_record_done`;

CREATE TABLE `task_record_done` (
  `id` bigint(20) unsigned NOT NULL auto_increment,
  `task_id` bigint(20) unsigned NOT NULL COMMENT '该task任务是哪个task id执行的结果',
  `real_cmd` varchar(1000) default NULL COMMENT '被替换参数为现场时间后后真实的命令',
  `exit_code` int(10) NOT NULL COMMENT '完成的返回值。0--成功，其他都--失败',
  `complete_success` tinyint(1) default NULL COMMENT '完成的返回状态。1--成功，0--失败',
  `start_datetime` datetime NOT NULL COMMENT '任务开始时间(如果是自动重执行时,每次执行不修改起始时间)',
  `end_datetime` datetime NOT NULL COMMENT '任务结束时间',
  `exec_type` int(10) NOT NULL COMMENT '执行类型,0--crontab执行，1--手动重执行,2--自动重执行,3--当场执行等',
  `exec_return_str` longtext COMMENT '执行后的外部进程字符串返回结果。',
  `current_redo_times` int(11) default NULL COMMENT '当前第几次自动重试执行',
  `on_processing` tinyint(1) NOT NULL default '0' COMMENT 'bool是否正在执行中,0--没有正在执行,1--正在手动/自动执行',
  PRIMARY KEY  (`id`),
  KEY `task_id` (`task_id`),
  KEY `real_cmd` (`real_cmd`(255)),
  KEY `exit_code` (`exit_code`),
  KEY `complete_success` (`complete_success`),
  KEY `start_datetime` (`start_datetime`),
  KEY `exec_type` (`exec_type`),
  CONSTRAINT `task_record_done_ibfk_1` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `task_record_done_201404` */

DROP TABLE IF EXISTS `task_record_done_201404`;

CREATE TABLE `task_record_done_201404` (
  `id` bigint(20) unsigned NOT NULL auto_increment,
  `task_id` bigint(20) NOT NULL COMMENT '该task任务是哪个task id执行的结果',
  `real_cmd` varchar(1000) default NULL COMMENT '被替换参数为现场时间后后真实的命令',
  `exit_code` int(10) NOT NULL COMMENT '完成的返回值。0--成功，其他都--失败',
  `complete_success` tinyint(1) default NULL COMMENT '完成的返回状态。1--成功，0--失败',
  `start_datetime` datetime NOT NULL COMMENT '任务开始时间(如果是自动重执行时,每次执行不修改起始时间)',
  `end_datetime` datetime NOT NULL COMMENT '任务结束时间',
  `exec_type` int(10) NOT NULL COMMENT '执行类型,0--crontab执行，1--手动重执行,2--自动重执行,3--当场执行等',
  `exec_return_str` longtext COMMENT '执行后的外部进程字符串返回结果。',
  `current_redo_times` int(11) default NULL COMMENT '当前第几次自动重试执行',
  `on_processing` tinyint(1) NOT NULL default '0' COMMENT 'bool是否正在执行中,0--没有正在执行,1--正在手动/自动执行',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=5989 DEFAULT CHARSET=utf8;

/*Table structure for table `task_record_done_201405` */

DROP TABLE IF EXISTS `task_record_done_201405`;

CREATE TABLE `task_record_done_201405` (
  `id` bigint(20) unsigned NOT NULL auto_increment,
  `task_id` bigint(20) unsigned NOT NULL COMMENT '该task任务是哪个task id执行的结果',
  `real_cmd` varchar(1000) default NULL COMMENT '被替换参数为现场时间后后真实的命令',
  `exit_code` int(10) NOT NULL COMMENT '完成的返回值。0--成功，其他都--失败',
  `complete_success` tinyint(1) default NULL COMMENT '完成的返回状态。1--成功，0--失败',
  `start_datetime` datetime NOT NULL COMMENT '任务开始时间(如果是自动重执行时,每次执行不修改起始时间)',
  `end_datetime` datetime NOT NULL COMMENT '任务结束时间',
  `exec_type` int(10) NOT NULL COMMENT '执行类型,0--crontab执行，1--手动重执行,2--自动重执行,3--当场执行等',
  `exec_return_str` longtext COMMENT '执行后的外部进程字符串返回结果。',
  `current_redo_times` int(11) default NULL COMMENT '当前第几次自动重试执行',
  `on_processing` tinyint(1) NOT NULL default '0' COMMENT 'bool是否正在执行中,0--没有正在执行,1--正在手动/自动执行',
  PRIMARY KEY  (`id`),
  KEY `task_id` (`task_id`),
  KEY `real_cmd` (`real_cmd`(255)),
  KEY `exit_code` (`exit_code`),
  KEY `complete_success` (`complete_success`),
  KEY `start_datetime` (`start_datetime`),
  KEY `exec_type` (`exec_type`)
) ENGINE=InnoDB AUTO_INCREMENT=113 DEFAULT CHARSET=utf8;

/*Table structure for table `task_record_undo` */

DROP TABLE IF EXISTS `task_record_undo`;

CREATE TABLE `task_record_undo` (
  `id` bigint(20) unsigned NOT NULL auto_increment,
  `task_id` bigint(20) unsigned NOT NULL COMMENT '该任务执行者的task_id号码',
  `real_cmd` varchar(1000) default NULL COMMENT '被替换参数为现场时间后后真实的命令',
  `run_status` int(4) NOT NULL COMMENT '运行状态---0 进行中...',
  `start_datetime` datetime NOT NULL COMMENT '执行开始时间',
  `exec_type` int(10) NOT NULL COMMENT '执行类型,0--crontab执行，1--手动重执行...',
  PRIMARY KEY  (`id`),
  KEY `task_id` (`task_id`),
  CONSTRAINT `task_record_undo_ibfk_1` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

/*Table structure for table `task_user` */

DROP TABLE IF EXISTS `task_user`;

CREATE TABLE `task_user` (
  `id` bigint(20) unsigned NOT NULL auto_increment,
  `task_id` bigint(20) unsigned NOT NULL,
  `user_id` bigint(10) unsigned NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `task_id` (`task_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `task_user_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `task_user_ibfk_1` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` bigint(10) unsigned NOT NULL auto_increment,
  `user_name` varchar(8) character set utf8 NOT NULL,
  `mail_name` varchar(8) collate utf8_bin NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `user_name` (`user_name`),
  KEY `mail_name` (`mail_name`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

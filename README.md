CronHub
=======

CronHub is a better crontab, it is a web application which can monitor a large number of machine's crontab, and easy to manage it from web page

Introduction
------------
Manage a large number of cluster's Linux crontab is nasty thing, especially system administrators always must login multiple machines to 
check whether the crontab job has been SUCCESSFULLY executed. If it is not SUCCESSFULLY done, administrators had to RE-EXECUTE job one by one. 
This cronhub project aim to ease this burden, and supply a friendly web interface to manage it within JUST A MOUSE CLICK.

NOTE:this project only have chinese language edition up to now, but English edition will come very soon.

statement(声明)
------------

Copyright [2015] [ma chen(马晨)]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


From SQLite: The author disclaims copyright to this source code. In place of a legal notice, here is a blessing:

> May you do good and not evil.

> May you find forgiveness for yourself and forgive others.

> May you share freely

> May you never taking more than you give.

摘自SQLite：作者声明对源代码拥有版权。以下是作者的一些企盼，而非法律公告：

> 希望你行善不为恶

> 希望你恕已亦恕人

> 希望你自由地共享

> 希望永远不要索取甚于付出

Installation(Chinese)
------------
调度系统的安装

安装cronhub非常简单。cronhub是java语言写成的系统，因此为了安装此软件，你需要有java语言运行环境作为支持.

此外,cronhub现在只支持linux/centos系统，未来的计划中会支持windows系统。

安装准备:你需要以下东西:

1.jdk 1.6,下载地址请访问jdk下载官方网站:oracle的jdk下载官方网站.

2.apache tomcat 6,下载地址请访问apache tomcat下载官方网站apache tomcat下载官方网站.

准备软件：中央服务器安装mysql，jdk6（官网下载www.oracle.com/technetwork/java/javase/downloads/jdk6-downloads-1637591.html)，apache-tomcat 6.x

第一步：安装mysql

转载:centos安装mysql

安装MySQL。

[root@sample ~]# yum -y install mysql-server　 ← 安装MySQL

[root@sample ~]# yum -y install php-mysql　 ← 安装php-mysql

配置MySQL

[root@sample ~]#vim /etc/my.cnf　 ← 编辑MySQL的配置文件

[mysqld]

datadir=/var/lib/mysql

socket=/var/lib/mysql/mysql.sock

> Default to using old password format for compatibility with mysql 3.x

> clients (those using the mysqlclient10 compatibility package).

old_passwords=1　 ← 找到这一行，在这一行的下面添加新的规则，让MySQL的默认编码为UTF-8

default-character-set = utf8　 ← 添加这一行

然后在配置文件的文尾填加如下语句：

[mysql]

default-character-set = utf8

启动MySQL服务

[root@sample ~]# chkconfig mysqld on　 ← 设置MySQL服务随系统启动自启动

[root@sample ~]# chkconfig --list mysqld　 ← 确认MySQL自启动

mysqld 0:off 1:off 2:on 3:on 4:on 5:on 6:off　 ← 如果2--5为on的状态就OK

[root@sample ~]#/etc/rc.d/init.d/mysqld start　 ← 启动MySQL服务

Initializing MySQL database:　　　　　　　　 [ OK ]

Starting MySQL: 　　　　　　　　　　　　　[ OK ]

MySQL初始环境设定

[1]为MySQL的root用户设置密码

MySQL在刚刚被安装的时候，它的root用户是没有被设置密码的。首先来设置MySQL的root密码。

[root@sample ~]# mysql -u root　 ← 用root用户登录MySQL服务器

Welcome to the MySQL monitor. Commands end with ; or \g.

Your MySQL connection id is 2 to server version: 4.1.20

Type 'help;' or '\h' for help. Type '\c' to clear the buffer.

mysql> select user,host,password from mysql.user;　 ← 查看用户信息

+------+------------------------------+---------------+

| user | host 　　　　　　　　　| password　|

+------+------------------------------+---------------+

| root | localhost　　　　 　　　| 　　　　　　|　 ← root密码为空

| root | sample.centospub.com　 |　　　　　　 |　 ← root密码为空

|　　　| sample.centospub.com | 　　　　　　|

|　　　| localhost　　　　　　 | 　　　　　　|

|root　| % |XXX 　　　　|

|　　　| 　　　 | 　　　　　　|

+------+------------------------------+---------------+

4 rows in set (0.00 sec)

mysql> set password for 在这里填入root密码');　 ← 设置root密码

Query OK, 0 rows affected (0.01 sec)

mysql> set password for 在这里填入root密码');　 ← 设置root密码

Query OK, 0 rows affected (0.01 sec)只有设置了这个才可以，才可以通过数据库来安装网址

mysql> set password for );　 ← 设置root密码

Query OK, 0 rows affected (0.01 sec)

mysql> select user,host,password from mysql.user;　 ← 查看用户信息

+------+--------------------------------+--------------------------+

| user | host　　　　　　　　　　| password　　 　　|

+------+--------------------------------+--------------------------+

| root | localhost　　　　　　　　| 19b68057189b027f |　 ← root密码被设置

| root | sample.centospub.com　　 | 19b68057189b027f |　 ← root密码被设置

| 　　 | sample.centospub.com　　 | 　　　　　　　　　|

| 　　 | localhost　　　　　　　　|　　　　　　　　　 |

+------+--------------------------------+--------------------------+

4 rows in set (0.01 sec)

mysql> exit　 ← 退出MySQL服务器

Bye

然后，测试一下root密码有没有生效。

[root@sample ~]# mysql -u root　 ← 通过空密码用root登录

ERROR 1045 (28000): Access denied for user (using password: NO)　 ← 出现此错误信息说明密码设置成功

[root@localhost ~]# mysql -u root -h sample.centospub.com　 ← 通过空密码用root登录

ERROR 1045 (28000): Access denied for user (using password: NO)　 ← 出现此错误信息说明密码设置成功

[root@sample ~]#mysql -u root -p　 ← 通过密码用root登录

Enter password:　 ← 在这里输入密码

Welcome to the MySQL monitor. Commands end with ; or \g.　 ← 确认用密码能够成功登录

Your MySQL connection id is 5 to server version: 4.1.20

Type 'help;' or '\h' for help. Type '\c' to clear the buffer.

mysql> exit

Bye

[root@sample ~]# mysql -u root -h sample.centospub.com -p　 ← 通过密码用root登录

Enter password:　 ← 在这里输入密码

Welcome to the MySQL monitor. Commands end with ; or \g.　 ← 确认用密码能够成功登录

Your MySQL connection id is 6 to server version: 4.1.20

Type 'help;' or '\h' for help. Type '\c' to clear the buffer.

mysql> exit　 ← 退出MySQL服务器

Bye

[2] 删除匿名用户

在MySQL刚刚被安装后，存在用户名、密码为空的用户。这使得数据库服务器有无需密码被登录的可能性。为消除隐患，将匿名用户删除。

[root@sample ~]# mysql -u root -p　 ← 通过密码用root登录

Enter password:　 ← 在这里输入密码

Welcome to the MySQL monitor. Commands end with ; or \g.

Your MySQL connection id is 7 to server version: 4.1.20

Type 'help;' or '\h' for help. Type '\c' to clear the buffer.

mysql> select user,host from mysql.user;　 ← 查看用户信息

+------+----------------------------+

| user | host　　　　　　　　 |

+------+----------------------------+

|　　　| localhost 　　　　　　|

| root | localhost 　　　　　　|

|　　　| sample.centospub.com |

| root | sample.centospub.com　 |

+------+----------------------------+

4 rows in set (0.02 sec)

mysql> delete from mysql.user where user='';　 ← 删除匿名用户

Query OK, 2 rows affected (0.17 sec)

mysql> select user,host from mysql.user;　 ← 查看用户信息

+------+----------------------------+

| user | host　　　　　　　　 |

+------+----------------------------+

| root | localhost　　　　　　|

| root | sample.centospub.com |

+------+----------------------------+

2 rows in set (0.00 sec)

mysql> exit　 ← 退出MySQL服务器

Bye

2.新开通一个用户，使得可以远程链接该mysql，并拥有建表权限

新开通一个服务器远程连接mysql，使mysql授权办法

敲入mysql进入控制台

use mysql

show tables; //可以查看下有哪几张表

Insert INTO user(Host,User,Password) VALUES("124.205.148.226","root",password('123456'));//这样远程用SQLyog就能连接登录了。

GRANT ALL PRIVILEGES ON *.* TO root@124.205.148.226 identified by '123456';//这样就有建库的权限了

FLUSH PRIVILEGES; //令权限立即生效

第二步：mysql建表

3.在SQLyog等远程连接的GUI工具中建表，sql如下:(重要：注意调度结果记录是"按月分表"的）

/*

SQLyog 企业版 - MySQL GUI v8.14

MySQL - 5.0.90-log : Database - cronhub_manage_system

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

`lostconn_emailaddress` varchar(1000) NOT NULL COMMENT '失去联络通信的报警email,#隔开',

`conn_status` tinyint(1) default NULL COMMENT '通信状态:0通讯异常，1通信正常',

`comment` text NOT NULL COMMENT '简介',

`update_time` datetime NOT NULL,

PRIMARY KEY (`id`)

) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

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

PRIMARY KEY (`id`)

) ENGINE=MyISAM AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

/*Table structure for table `task_record_done` */

DROP TABLE IF EXISTS `task_record_done`;

CREATE TABLE `task_record_done` (

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

PRIMARY KEY (`id`)

) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*Table structure for table `task_record_undo` */

DROP TABLE IF EXISTS `task_record_undo`;

CREATE TABLE `task_record_undo` (

`id` bigint(20) unsigned NOT NULL auto_increment,

`task_id` bigint(20) NOT NULL COMMENT '该任务执行者的task_id号码',

`real_cmd` varchar(1000) default NULL COMMENT '被替换参数为现场时间后后真实的命令',

`run_status` int(4) NOT NULL COMMENT '运行状态---0 进行中...',

`start_datetime` datetime NOT NULL COMMENT '执行开始时间',

`exec_type` int(10) NOT NULL COMMENT '执行类型,0--crontab执行，1--手动重执行...',

PRIMARY KEY (`id`)

) ENGINE=MyISAM AUTO_INCREMENT=10855 DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;

/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;

/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;

/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

第三步：修改java web中央服务器配置文件，打包（可以用ant，也可以不用ant自己手工改配置文件打包）

4.修改ant(这一步也可以手工修改properties配置文件,包含application.properties和log4j.properties，然后自己打包，ant打包就是根据一个配置文件然后调用ant脚本自动修改2个properties配置文件，然后打成war包，方便一些)

在eclipse的ant文件夹下放入最新的服务器要连接数据库的properties文件（ant打包要用）

例如：

> 这一项的ip和端口一定要改为mysql数据库的ip和端口

db.jdbcurl=jdbc\:mysql\://192.168.101.9\:3306/cronhub_manage_system?useUnicode\=true&characterEncoding\=UTF-8

db.username=admin

db.password=123456

> 这一项的ip和端口一定要改为中央服务器的tomcat启动该server的ip和端口

undoReportHttpUrl=http\://192.168.101.9\:8085/record_undo/report.action

serverRootUrl=http\://192.168.101.9\:8085

alertFromUserMail=sharpstill@163.com

alertFromPassword=xxx

alertHostMail=mail.163.com

alertDestMail={"sharpstill@163.com"\:"\u9A6C\u6668"}

########################log4j.properties######################

log4j.rootLogger=info, stdout

log4j.appender.stdout=org.apache.log4j.ConsoleAppender

log4j.appender.stdout.layout=org.apache.log4j.PatternLayout

log4j.appender.stdout.layout.ConversionPattern=[%d{yyyy-MM-dd HH\:mm\:ss}] %p\t| %m%n

log4j.logger.Validate=info, validate

log4j.appender.validate=org.apache.log4j.DailyRollingFileAppender

log4j.appender.validate.File=/opt/modules/cronhub_system_tomcat/logs/cronhub_sys/service.log

log4j.appender.validate.DatePattern='.'yyyy-MM-dd'.bak'

log4j.appender.validate.layout=org.apache.log4j.PatternLayout

log4j.appender.validate.layout.ConversionPattern=[%d{yyyy-MM-dd HH\:mm\:ss}] %p\t| %m%n

log4j.logger.Error=error, err

log4j.appender.err=org.apache.log4j.DailyRollingFileAppender

log4j.appender.err.File=/opt/modules/cronhub_system_tomcat/logs/cronhub_sys/error.log

log4j.appender.err.DatePattern='.'yyyy-MM-dd'.bak'

log4j.appender.err.layout=org.apache.log4j.PatternLayout

log4j.appender.err.layout.ConversionPattern=[%d{yyyy-MM-dd HH\:mm\:ss}] %p\t| %m%n

log4j.logger.DaemonError=error, daemonError

log4j.appender.daemonError=org.apache.log4j.DailyRollingFileAppender

log4j.appender.daemonError.File=/opt/modules/cronhub_system_tomcat/logs/cronhub_sys/daemonError.log

log4j.appender.daemonError.DatePattern='.'yyyy-MM-dd'.bak'

log4j.appender.daemonError.layout=org.apache.log4j.PatternLayout

log4j.appender.daemonError.layout.ConversionPattern=[%d{yyyy-MM-dd HH\:mm\:ss}] %p\t| %m%n

log4j.logger.RecordUndoLogger=info, recordUndoLogger

log4j.appender.recordUndoLogger=org.apache.log4j.DailyRollingFileAppender

log4j.appender.recordUndoLogger.File=/opt/modules/cronhub_system_tomcat/logs/cronhub_sys/recordUndoLogger.log

log4j.appender.recordUndoLogger.DatePattern='.'yyyy-MM-dd'.bak'

log4j.appender.recordUndoLogger.layout=org.apache.log4j.PatternLayout

log4j.appender.recordUndoLogger.layout.ConversionPattern=[%d{yyyy-MM-dd HH\:mm\:ss}] %p\t| %m%n

log4j.logger.RecordDoneLogger=info, recordDoneLogger

log4j.appender.recordDoneLogger=org.apache.log4j.DailyRollingFileAppender

log4j.appender.recordDoneLogger.File=/opt/modules/cronhub_system_tomcat/logs/cronhub_sys/recordDoneLogger.log

log4j.appender.recordDoneLogger.DatePattern='.'yyyy-MM-dd'.bak'

log4j.appender.recordDoneLogger.layout=org.apache.log4j.PatternLayout

log4j.appender.recordDoneLogger.layout.ConversionPattern=[%d{yyyy-MM-dd HH\:mm\:ss}] %p\t| %m%n

log4j.logger.RecordDoneError=info, recordDoneError

log4j.appender.recordDoneError=org.apache.log4j.DailyRollingFileAppender

log4j.appender.recordDoneError.File=/opt/modules/cronhub_system_tomcat/logs/cronhub_sys/recordDoneError.log

log4j.appender.recordDoneError.DatePattern='.'yyyy-MM-dd'.bak'

log4j.appender.recordDoneError.layout=org.apache.log4j.PatternLayout

5.运行ant文件夹下的build.xml

打完包后压缩包就放入了war文件夹下

第四步：部署war包到apache-tomcat,启动

6.下载apache-tomcat

进入官网下载http://tomcat.apache.org/download-60.cgi

打成war包后，一定要把war压缩文件放入webapp/ROOT下（一定要放到ROOT下）

然后敲入jar -xvf xxx.war解压缩

在tomcat的bin下敲入sh start.sh启动

第五步：修改一键安装daemon端的shell脚本，并在各个需要被调度的奴隶机上一键执行shell安装

7.安装daemon

(1)下载daemon端安装脚本：

一键脚本和jar包等安装必备都位于webapp/ROOT/WebRoot/download下

其中安装脚本文件名:install_start.sh位于webapp/ROOT/WebRoot/download

(2)在各个需要执行crontab的机器上安装daemon：

安装脚本install_start.sh使用指南

Usage Example: ./install_start.sh -d /opt/modules/daemon -s 2012 -i 192.168.0.1 -p 8080

-d install directory path

-s daemon boot start port

-i cronhub center server's ip used for download daemon's jar and jdk and jsvc and so on

-p cronhub center server's port used for download daemon's jar and jdk and jsvc and so on

执行完脚本就会启动，启动后你会在控制台上ps的时候发现2个jsvc的进程,如下：

[root@localhost ~]# ps -ef|grep jsvc

root 1965 1 0 Jul02 ? 00:00:00 jsvc.exec -home /opt/modules/daemon/jdk1.6.0_30 -Xmx2000m -pidfile /opt/modules/daemon/2012.pid -cp /opt/modules/daemon/DispatchSystemDaemon.jar com.baofeng.cronhubexecutor.boot.DaemonBoot -p 2012

root 1966 1965 0 Jul02 ? 00:00:48 jsvc.exec -home /opt/modules/daemon/jdk1.6.0_30 -Xmx2000m -pidfile /opt/modules/daemon/2012.pid -cp /opt/modules/daemon/DispatchSystemDaemon.jar com.baofeng.cronhubexecutor.boot.DaemonBoot -p 2012

### 说明：

安装daemon脚本会加入chkconfig到linux系统启动项（加入service）里头

最后,大功告成，当你在浏览器敲入http://xxx.xxx.xxx.xxx:8080/时，你就会看到一个这个系统的界面，直接点击daemon执行器管理下的增加daemon执行器将你刚刚添加的daemonIP和端口填入吧！

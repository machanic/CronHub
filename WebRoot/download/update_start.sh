#!/bin/bash
if [ $# = 0 ];
then
    echo "please input port number.for example 2012";
    exit
fi
base_url="http://192.168.50.177:8080/download/";
jsvc_zip="jsvc.zip";
jsvc_dir="jsvc";
jsvc_url=$base_url$jsvc_zip;
jar_name="DispatchSystemDaemon.jar";
daemon_url=$base_url$jar_name;
current_path=`pwd`;
daemon_jar_path=$current_path"/"$jar_name;
jsvc_dir=$current_path"/"$jsvc_dir;
jsvc_target_bin=$jsvc_dir"/jsvc";
wget $jsvc_url -O $current_path"/"$jsvc_zip;
if [[ -e $current_path"/"$jsvc_zip ]];
then
    unzip -o $jsvc_zip;
fi;
if [ ! -e $jsvc_target_bin ];
then
    cd $jsvc_dir;
    chmod +x configure;
    chmod +x make;
    ./configure;
    make;
    cd $current_path;
fi;
wget $daemon_url -O $daemon_jar_path;
ps_num=`ps -ef|grep $jar_name|grep $1|grep -v grep|grep -v sh|wc -l`;
if [ $ps_num -gt 0 ];
then
    kill_pid=`ps -ef|grep $jar_name|grep $1|grep -v grep|grep -v sh|awk -F ' ' '{print $2}'`;
    echo "kill  "$kill_pid" .process numer is "$ps_num;
    kill -9 $kill_pid;
    echo "killing service to reboot service...";
    sleep 2;
fi;
cmd="$jsvc_target_bin -cp $daemon_jar_path com.baofeng.dispatchexecutor.boot.DaemonBoot -p $1";
echo $cmd
$jsvc_target_bin -cp $daemon_jar_path com.baofeng.dispatchexecutor.boot.DaemonBoot -p $1
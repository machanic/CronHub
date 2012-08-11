#!/bin/bash
function usage(){
	echo "Usage Example: ./install_start.sh -d /opt/modules/daemon -s 2012 -i 192.168.0.1 -p 8080"
	echo "Options:";
	echo "-d  install directory path";
	echo "-s daemon boot start port";
	echo "-i  cronhub center server's ip used for download daemon's jar and  jdk and jsvc  and so on"
	echo "-p  cronhub center server's port used for download daemon's jar and  jdk and jsvc  and so on"
}
if [ $# -ne 8 ];
then
:<<BLOCK
    echo "please input the directory path that you want to install";
    read install_path;
    echo "please input the cronhub center server's ip..."
    read center_server_ip;
    echo "please input the cronhub center server's port..."
    read center_server_port;
    echo "please input the daemon boot start port..."
    read daemon_port;
BLOCK
	usage
    exit -1
else
	while getopts ":d:i:h:p:s:" flag
	do
		case $flag in
			d)
			 	echo "install directory path: "$OPTARG
			 	install_path=$OPTARG
			 	;;
			 i)
			 	echo "cronhub center server's ip: "$OPTARG
			 	center_server_ip=$OPTARG
			 	;;
			 p)
			 	echo "cronhub center server's port: "$OPTARG
			 	center_server_port=$OPTARG
			 	;;
			 s)
			 	echo "daemon boot start port's port: "$OPTARG
			 	daemon_port=$OPTARG
			 	;;
			 ?) usage
			 	exit -1
			 	;;
		esac
	done
fi
mkdir -p $install_path
cd $install_path;
jdk_bin="jdk.bin";
jdk_bin_path=$install_path"/"$jdk_bin;
java_home=$install_path"/jdk1.6.0_30";
base_url="http://${center_server_ip}:${center_server_port}/download/";
jdk_download_url=$base_url"jdk-6u30-linux-x64.bin"
jsvc_zip="jsvc.zip";
jsvc_dir="jsvc";
jsvc_url=${base_url}${jsvc_zip};
jar_name="DispatchSystemDaemon.jar";
daemon_url=$base_url$jar_name;
daemon_jar_path=$install_path"/"$jar_name;
jsvc_dir=$install_path"/"$jsvc_dir;
jsvc_target_bin=$jsvc_dir"/jsvc";

#install jdk
if [ ! $JAVA_HOME ];
    then
        if [ ! -e $jdk_bin_path ];
        then
            wget $jdk_download_url -O $jdk_bin_path;
            chmod 755 $jdk_bin_path;
        fi;
        if [ ! -d $java_home ];
        then
            $jdk_bin_path;
        fi;
elif [ $JAVA_HOME ];
    then
        echo "set java_home :$JAVA_HOME"
        java_home=$JAVA_HOME;
fi;

#install jsvc
if [ ! -e $install_path"/"$jsvc_zip ];
then
    wget $jsvc_url;
fi;

if [[ -e $install_path"/"$jsvc_zip && ! -e $jsvc_target_bin ]];
then
    unzip -o $jsvc_zip;
fi;

if [ ! -e $jsvc_target_bin ];
then
    cd $jsvc_dir;
    chmod +x configure;
    chmod +x make;
    ./configure --with-java=$java_home;
    make;
    cd $install_path;
fi;


#install daemon jar
if [ ! -e $daemon_jar_path ];
then
    wget $daemon_url;
fi;

#shutdown previous cronhub daemon
if [ -e /etc/init.d/cronhub_daemon ];
then
    /sbin/service cronhub_daemon stop;
    sleep 2;
fi;

#add to system service
cmd="$jsvc_target_bin -home $java_home -Xmx2000m -pidfile $install_path/$daemon_port.pid -cp $daemon_jar_path com.baofeng.dispatchexecutor.boot.DaemonBoot -p $daemon_port";
echo -e "#description:cronhub_daemon
#chkconfig:231 80 80
case \"\$1\" in
start)
\tif [ ! -x ${install_path}/jsvc/jsvc -o ! -w ${install_path} ];then
\techo \"error!You don't have permission to execute ${install_path}/jsvc/jsvc OR to write pid_file in ${install_path} ,please check!\";
\texit 1;
\tfi;
\t${install_path}/jsvc/jsvc -home ${install_path}/jdk1.6.0_30 -Xmx2000m -pidfile ${install_path}/$daemon_port.pid -cp ${install_path}/DispatchSystemDaemon.jar com.baofeng.dispatchexecutor.boot.DaemonBoot -p $daemon_port
\techo \"starting cronhub daemon service...\"
\t;;
stop)
\tkill \$(cat ${install_path}/$daemon_port.pid);
\techo \"shutdown cronhub daemon service...\";
\t;;
restart)
\t/sbin/service cronhub_daemon stop;
\tsleep 2;
\t/sbin/service cronhub_daemon start;
\t;;
esac
" > /etc/init.d/cronhub_daemon
/sbin/chkconfig --add cronhub_daemon

#start daemon
echo "adding to service done,now start daemon service..."
chmod +x /etc/init.d/cronhub_daemon
/sbin/service cronhub_daemon start
echo "all done! now you can use /sbin/service cronhub_daemon [start|stop|restart] to boot."
#the final start cmd
#$jsvc_target_bin -home $java_home -Xmx2000m -pidfile $install_path"/"$daemon_port".pid" -cp $daemon_jar_path com.baofeng.dispatchexecutor.boot.DaemonBoot -p $daemon_port


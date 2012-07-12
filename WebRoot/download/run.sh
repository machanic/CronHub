mkdir -p /opt/modules/daemon
wget "http://xxx:8080/download/install_start_daemon.sh"
cp install_start_daemon.sh /opt/modules/daemon
cd /opt/modules/daemon/ && chmod +x install_start_daemon.sh && ./install_start_daemon.sh 2012

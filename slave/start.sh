#!/bin/sh

sleep 120

date >/tmp/slave_bootup
ping 192.168.1.13 >/dev/null 2>/dev/null  &


echo "Remove old flags each reboot"
rm -f /home/beer/brewerslab/slave/localweb/wifistate/*



/etc/init.d/rpcbind restart

cd /home/beer/brewerslab/slave
mkdir ipc 2>/dev/null


echo "including i2c"
modprobe i2c-dev
modprobe w1-gpio
modprobe w1-therm

chown /var/log/beer -R
mkdir /web
mkdir /currentdata
mkdir /archivedata
chown beer:beer /web
chown beer:beer /currentdata
chown beer:beer /archivedata


ntpdate -s uk.pool.ntp.org

echo "Mount Tmpfs"
mount -t tmpfs -o size=50m tmpfs /currentdata
mkdir /currentdata/lastreading


###################################
### LOCAL ENVIRONMENT
echo "Mount NAS "
mount -t nfs -o nolock 192.168.1.31:/Transient/piTempMonitor/archivedata /archivedata
mount 192.168.1.31:/web /web
/etc/init.d/rpcbind restart
###################################


sh temperature.sh "Launching"
sh ssr.sh "Launching"
sh relay.sh "Launching"
sh grapher.sh "Launching"
sh bidir.sh "Launching"


screen -dmS localweb python localweb/localserve.py



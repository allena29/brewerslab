#!/bin/sh

echo "Killing stuff"
killall dhcpd
killall hostapd
killall wpa_supplicant
killall dhclient

echo "Going to use DCHCP for address"
cp /home/beer/brewerslab/dhcpcd.conf-preferred /etc/dhcpcd.conf
/etc/init.d/dhcpcd restart

ssid=` head -n1 /boot/wifissid.txt  | sed -e's/[^A-Za-z0-9]//'`
sudo python /home/beer/brewerslab/ledmatrix.py "wifi: $ssid"
psk=` head -n1 /boot/wifipsk.txt  | sed -e's/[^A-Za-z0-9]//'`
echo "Rewriting WPA SUpplicant"
sed -e "s/<SSID>/$ssid/"  /home/beer/brewerslab/wpa_supplicant.conf  | sed -e "s/<PSK>/$psk/" >/etc/wpa_supplicant/wpa_supplicant.conf

echo "Starting WPA Supplicant - then wait 10 seconds"
wpa_supplicant -B -D wext -i wlan0 -c /etc/wpa_supplicant/wpa_supplicant.conf
sleep 10
iwconfig wlan0 >/home/beer/last.wifi
ifconfig >>/home/beer/last.wifi
sleep 5

ntpdate -s uk.pool.ntp.org
 

echo "nameserver 8.8.8.8" >>/etc/resolv.conf
sleep 10
ip=`ifconfig wlan0 | grep Bcast | sed -e 's/.*ddr://' | sed -e 's/ .*//'`
sudo python /home/beer/brewerslab/ledmatrix.py "ready.. http://$ip:54661/cgi/index.py"


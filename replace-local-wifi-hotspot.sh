#!/bin/sh

echo "Killing stuff"
killall dhcpd
killall hostapd
killall wpa_supplicant
killall dhclient

echo "Going to use DCHCP for address"

localip=0
if [ -f /boot/wifiip.txt ]
then
 if [ -f /boot/wificidr.txt ]
 then
  if [ -f /boot/wifigw.txt ]
  then
	localip=1
	ip=` head -n1 /boot/wifiip.txt  | sed -e's/[^A-Za-z0-9\.]//g'`
	cidr=` head -n1 /boot/wificidr.txt  | sed -e's/[^A-Za-z0-9\.]//g'`
	gw=` head -n1 /boot/wifigw.txt  | sed -e's/[^A-Za-z0-9\.]//g'`
	sed -e "s/<IP>/$ip/" /home/beer/brewerslab/dhcpcd.conf-staticip | sed -e "s/<CIDR>/$cidr/" | sed -e "s/<ROUTER>/$gw/" > /etc/dhcpcd.conf
  fi
 fi
fi
if [ "$localip" = "0" ] 
then
	cp /home/beer/brewerslab/dhcpcd.conf-preferred /etc/dhcpcd.conf
fi
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


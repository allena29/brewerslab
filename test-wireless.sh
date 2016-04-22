#!/bin/sh
if [ "$1" = "" ]
then
	echo "Need WIFI details to try"
	exit 4
fi
echo "TRYING" >/home/beer/brewerslab/slave/localweb/wifistate/$1
rm -f /home/beer/brewerslab/slave/localweb/wifistate/$1.ip
rm -f /home/beer/brewerslab/slave/localweb/wifistate/.__GLOBAL__

echo "Killing"
killall dhcpd
killall hostapd
killall wpa_supplicant
killall dhclient
sleep 5

echo "Rewriting WPA SUpplicant"
sed -e "s/<SSID>/$1/"  /home/beer/brewerslab/wpa_supplicant.conf  | sed -e "s/<PSK>/$2/" >/etc/wpa_supplicant/wpa_supplicant.conf

echo "Starting WPA Supplicant - then wait 10 seconds"
wpa_supplicant -B -D wext -i wlan0 -c /etc/wpa_supplicant/wpa_supplicant.conf
sleep 10
iwconfig wlan0 >/home/beer/last.wifi.test

sudo dhclient wlan0
if [ "$?" = "0" ]
then 
echo "Have A DHCP lease"
 ifconfig >>/home/beer/last.wifi.test
 route -n >>/home/beer/last.wifi.test

else
  echo "no dhcpd elase" >>/home/beer/last.wifi.test
  sh /home/beer/brewerslab/start-local-wifi-hotspot.sh
  exit 9

fi
echo "Testing locally"



ping 8.8.8.8 -c 10 -w 20 >>/home/beer/last.wifi.test
googleDnsStatus=$?
echo "$googleDnsStatus" >>/home/beer/last.wifi.test

if [ "$googleDnsStatus" = "0" ]
then
	echo "Successful"
	echo "SUCCESS" >/home/beer/brewerslab/slave/localweb/wifistate/$1
	ifconfig wlan0  | grep "inet addr:" | sed -e's/.*inet addr://' | sed -e's/\s.*//' >/home/beer/brewerslab/slave/localweb/wifistate/$1.ip

else
	echo "FAIL_NO_IP_CONNECTIVITY" >/home/beer/brewerslab/slave/localweb/wifistate/$1
	echo "Not successful"
	sh /home/beer/brewerslab/start-local-wifi-hotspot.sh
	exit 8
fi


echo "Restart back the local wifi hotspot"
## this lets us tell the user the ip address..
sh /home/beer/brewerslab/start-local-wifi-hotspot.sh


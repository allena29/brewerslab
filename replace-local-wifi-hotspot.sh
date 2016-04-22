#!/bin/sh


if [ -f /home/beer/brewerslab/slave/localweb/wifistate/.__GLOBAL__ ]
then
 echo "need to replace wifi local setup"
 sleep 360

 echo "Killing stuff"
 killall dhcpd
 killall hostapd
 killall wpa_supplicant
 killall dhclient

 sleep 5
ssid=`awk -F: '{print $1}' /home/beer/brewerslab/slave/localweb/wifistate/.__GLOBAL__`
psk=`awk -F: '{print $2}' /home/beer/brewerslab/slave/localweb/wifistate/.__GLOBAL__`
echo "Rewriting WPA SUpplicant"
sed -e "s/<SSID>/$ssid/"  /home/beer/brewerslab/wpa_supplicant.conf  | sed -e "s/<PSK>/$psk/" >/etc/wpa_supplicant/wpa_supplicant.conf

echo "Starting WPA Supplicant - then wait 10 seconds"
wpa_supplicant -B -D wext -i wlan0 -c /etc/wpa_supplicant/wpa_supplicant.conf
sleep 10
iwconfig wlan0 >/home/beer/last.wifi

sudo dhclient wlan0
ifconfig >>/home/beer/last.wifi

fi

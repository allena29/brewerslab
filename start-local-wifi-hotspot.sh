
echo "Killing dhc server/wpa supplicant"
killall dhclient
killall wpa_supplicant



if [ -f try-own-wifi ]
then
 echo "TODO: try own wifi credential "
else

 if [ -f own-wifi-works ]
 then
  echo "TODO: we are using our wifi - no need for hostapd"
 else

   echo "Making sure we don't use wpa supplicant"
   cp /home/beer/brewerslab/interfaces-localwifi /etc/network/interfaces
   echo "Using hostpad"
   killall hostapd
   /home/beer/brewerslab/hostapd -B /home/beer/brewerslab/hostapd.conf
   sleep 5
   ifconfig wlan0 172.12.12.122 netmask 255.255.255.128
   echo "and starting dhcpd"
   killall dhcpd

   dhcpd

 fi
fi




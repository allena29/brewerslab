touch /boot/wifiresetflag


sudo python /home/beer/brewerslab/ledmatrix.py "WIFI: aaaBREWERSLAB"

touch /tmp/local-wifi-active
echo "Killing dhc server/wpa supplicant"
killall dhclient
killall wpa_supplicant
cp /home/beer/brewerslab/dhcpcd.conf-localwifi /etc/dhcpcd.conf
/etc/init.d/dhcpcd restart
ifconfig wlan0 172.12.12.122 netmask 255.255.255.128


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



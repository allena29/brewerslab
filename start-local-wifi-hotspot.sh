
# turn on red led
kill `ps -ef | grep gpio23led | head -n 1 | sed -e 's/^\S* *//' | sed -e 's/ .*//'`
python /home/beer/brewerslab/gpio23led.py 0 &
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



kill `ps -ef | grep gpio23led | head -n 1 | sed -e 's/^\S* *//' | sed -e 's/ .*//'`


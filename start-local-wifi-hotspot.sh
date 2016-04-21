
if [ -f try-own-wifi ]
then
 echo "TODO: try own wifi credential 
else

 if [ -f own-wifi-works ]
 then
  echo "TODO: we are using our wifi - no need for hostapd"
 else

   echo "Using hostpad"
   /home/beer/brewerslab/hostpad -B /home/beer/brewerslab/hostpad.conf
   echo "and starting dhcpd"
   killall dhcpd

   dhcpd

 fi
fi

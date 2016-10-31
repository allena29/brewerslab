
kill `ps -ef | grep pitmLedMatrix | awk '{print $2}'`
sleep 1

if [ "$1" = "poweroff" ]
then
sudo python /home/beer/brewerslab/ledmatrix.py "poweroff"
touch /tmp/poweroff
else
sudo python /home/beer/brewerslab/ledmatrix.py "reboot"
touch /tmp/reboot
fi
sleep 15
if [ "$1" = "poweroff" ]
then
sudo poweroff
else
sudo reboot
fi

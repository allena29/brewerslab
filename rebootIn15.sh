sudo python /home/beer/brewerslab/gpio23led.py 2 &
if [ "$1" = "poweroff" ]
then
touch /tmp/poweroff
else
touch /tmp/reboot
fi
sleep 15
if [ "$1" = "poweroff" ]
then
sudo poweroff
else
sudo reboot
fi

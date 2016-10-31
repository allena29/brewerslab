#!/usr/bin/python

# The only purpose of this is to write boot files
# sudo echo "">xx doesn't work

import sys

if sys.argv[1] == "ssid":
	o=open("/boot/wifissid.txt","w")
	o.write("%s" %(sys.argv[2]))
	o.close()
if sys.argv[1] == "psk":
	o=open("/boot/wifipsk.txt","w")
	o.write("%s" %(sys.argv[2]))
	o.close()
if sys.argv[1] == "ip":
	o=open("/boot/wifiip.txt","w")
	o.write("%s" %(sys.argv[2]))
	o.close()
if sys.argv[1] == "cidr":
	o=open("/boot/wificidr.txt","w")
	o.write("%s" %(sys.argv[2]))
	o.close()
if sys.argv[1] == "gw":
	o=open("/boot/wifigw.txt","w")
	o.write("%s" %(sys.argv[2]))
	o.close()

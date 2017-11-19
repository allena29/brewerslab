from __future__ import division
#!/usr/bin/python

# piTempLedFlasher
import json
import os
import hashlib
import struct
import socket
import syslog
import sys
import threading
import time





class x:


	def __init__(self):
		self.logging=2		# 1 = syslog, 2 = stderr


	def updateStatsZoneA(self):
		self.sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
		self.sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
		self.sock.setsockopt(socket.SOL_IP, socket.IP_MULTICAST_TTL, 4)
		self.sock.bind(('', 5087))
		mreq = struct.pack("4sl", socket.inet_aton('239.232.168.250'), socket.INADDR_ANY)
		self.sock.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, mreq)
		self.mcastMembership=True

		while True:
			(data, addr) = self.sock.recvfrom(1200)
			time.sleep(0.5)		

			

	def updateStats2(self):
		self.sockb = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
		self.sockb.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
		self.sockb.setsockopt(socket.SOL_IP, socket.IP_MULTICAST_TTL, 4)
		self.sockb.bind(('', 5187))
		mreq = struct.pack("4sl", socket.inet_aton('239.232.168.250'), socket.INADDR_ANY)
		self.sockb.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, mreq)
		self.mcastMembership=True
#
		while True:
			(data, addr) = self.sockb.recvfrom(1200)
			time.sleep(0.5)		

	def zoneTempThread(self):
		sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
		sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
		sock.setsockopt(socket.SOL_IP, socket.IP_MULTICAST_TTL, 4)
		sock.bind(('', 5087))
		mreq = struct.pack("4sl", socket.inet_aton('239.232.168.250'), socket.INADDR_ANY)
		sock.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, mreq)
		
		while True:
			(data, addr) = sock.recvfrom(1200)		
                        time.sleep(1)
if __name__ == '__main__':
	try:
	        controller=x()


		updateStatsThread = threading.Thread(target=controller.updateStatsZoneA)
		updateStatsThread.daemon = True
		updateStatsThread.start()
	
		updateStats2Thread = threading.Thread(target=controller.updateStats2)
		updateStats2Thread.daemon = True
		updateStats2Thread.start()
		# get temperature status from zone a
		zoneTempThread = threading.Thread(target=controller.zoneTempThread)
		zoneTempThread.daemon = True
		zoneTempThread.start()
		
		
                while 1==1:
                    time.sleep(1)


	except KeyboardInterrupt:
                y=x()



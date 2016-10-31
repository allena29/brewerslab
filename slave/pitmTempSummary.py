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



from pitmCfg import pitmCfg
from pitmLedMatrix import *


if os.path.exists("simulator"):
	import fakeRPi.GPIO as GPIO
else:
	import RPi.GPIO as GPIO


from pitmCfg import pitmCfg

class pitmTempSummary:


	def __init__(self):
		self.logging=2		# 1 = syslog, 2 = stderr
		self.cfg = pitmCfg()
		self.mcastMembership=False

		self.probeVal={ self.cfg.mashAProbe:0,self.cfg.mashBProbe:0,self.cfg.hltProbe:0,self.cfg.boilProbe:0,self.cfg.fermProbe:0,self.cfg.tempProbe:0}
		self.probeStamp={ self.cfg.mashAProbe:0,self.cfg.mashBProbe:0,self.cfg.hltProbe:0,self.cfg.boilProbe:0,self.cfg.fermProbe:0,self.cfg.tempProbe:0}
		self.probes=[ self.cfg.mashAProbe,self.cfg.mashBProbe, self.cfg.hltProbe,self.cfg.boilProbe,self.cfg.fermProbe,self.cfg.tempProbe]
		
		self.mcastTime=0
		self.activeProbes=[]
		self.ledm=None
 		if not os.path.exists("simulator"):
 			self.ledm = pitmLEDmatrix()
 			self.ledm.sendMessage("--------")		


	def uncontrol(self):
		try:
 			controller.ledm.sendMessage("........")		
		except:
			pass

	def _log(self,msg):
		if self.logging == 1:
			syslog.syslog(syslog.LOG_DEBUG, msg)
		elif self.logging == 2:
			sys.stderr.write("%s,%s,monitor,%s\n" %(time.ctime(),time.time(),msg))

			
	def _err(self,msg):
		syslog.syslog(syslog.LOG_ERR, msg)
		sys.stderr.write("%s\n" %(msg))



	def getStats(self):
		self.sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
		self.sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
		self.sock.setsockopt(socket.SOL_IP, socket.IP_MULTICAST_TTL, 4)
		self.sock.bind(('', self.cfg.mcastTemperaturePort))
		mreq = struct.pack("4sl", socket.inet_aton(self.cfg.mcastGroup), socket.INADDR_ANY)
		self.sock.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, mreq)
		self.mcastMembership=True

		while True:
			(data, addr) = self.sock.recvfrom(1200)
			time.sleep(0.5)
			self.decodeMessage(data)	
				
	def decodeMessage(self,data,zone="Unknown"):
		"""
		"""

		try:
			cm = json.loads( data )
		except:
			self._log("Error unpickling input message\n%s" %(data))
			return
		
		for probe in self.probes:
			if cm['currentResult'].has_key( probe ):
				if  cm['currentResult'][probe]['valid']:
					self.probeStamp[probe]= cm['currentResult'][probe]['timestamp']
					self.probeVal[probe]= cm['currentResult'][probe]['temperature']
				if cm['currentResult'][probe]['timestamp'] > self.mcastTime:
					self.mcastTime = cm['currentResult'][probe]['timestamp']


	def updateStats(self):
		cycle=0
		lastactive=0
		while 1:

			self.activeProbes=[]
			for probe in self.probes:
				#print self.mcastTime,self.mcastTime - self.probeStamp[probe],probe 
				if (self.mcastTime - self.probeStamp[probe]) < 10:
					self.activeProbes.append(probe)
				self.activeProbes.sort()

			if not len(self.activeProbes) 	 == lastactive:
				cycle=0

			lastactive=len(self.activeProbes)
			print len(self.activeProbes),self.probeVal
			if not os.path.exists("simultor"):
				if len(self.activeProbes) == 0:
					self.ledm.sendMessage("--------")
				# just one probe
				elif len(self.activeProbes) == 1:
					for probe in self.activeProbes:
						self.ledm.sendMessage("%s%.1f" %( self.cfg.probeId[ probe ], self.probeVal[probe]  ))
				# two probes
				elif len(self.activeProbes) == 2:
					# just mash probes
					half1=""
					half2=""	
					if cycle < 3:
						half1=self.cfg.probeId[ self.activeProbes[0] ]
						half2=self.cfg.probeId[ self.activeProbes[1] ]
					elif cycle < 11:
						half1="%.1f" %(self.probeVal[self.activeProbes[0] ])
						half2="%.1f" %(self.probeVal[self.activeProbes[1] ])
					self.ledm.sendMessage("%s%s" %(half1,half2))
					if cycle > 10:
						cycle=0
				# threes probes
				elif len(self.activeProbes) == 3:
					#  mash + hlt probes
					if self.cfg.mashAProbe in self.activeProbes and self.cfg.mashBProbe in self.activeProbes and self.cfg.hltProbe in self.activeProbes:
						half1="    "
						half2="    "
						if cycle < 3:
							half1=self.cfg.probeId[ self.cfg.hltProbe ]
							half2=self.cfg.probeId[ self.cfg.mashAProbe ]
						elif cycle < 11:
							half1="%.1f" %(self.probeVal[ self.cfg.hltProbe ] )
							half2="%.1f" %(self.probeVal[ self.cfg.mashAProbe ] )
						elif cycle < 13:
							half1="%.1f" %(self.probeVal[ self.cfg.hltProbe ] )
							half2=self.cfg.probeId[ self.cfg.mashBProbe ]
						else: 
							half1="%.1f" %(self.probeVal[ self.cfg.hltProbe ] )
							half2="%.1f" %(self.probeVal[ self.cfg.mashBProbe ] )
						if cycle > 20:
							cycle=0
						self.ledm.sendMessage("%s%s" %(half1,half2))
						
			cycle=cycle+1
				
			time.sleep(1)
			
if __name__ == '__main__':
	try:
		controller = pitmTempSummary()


		updateMeasureThread = threading.Thread(target=controller.getStats)
		updateMeasureThread.daemon = True
		updateMeasureThread.start()

		updateDisplayThread = threading.Thread(target=controller.updateStats)
		updateDisplayThread.daemon = True
		updateDisplayThread.start()

		
		while 1:
			
			time.sleep(1)

	except KeyboardInterrupt:
		controller.uncontrol()




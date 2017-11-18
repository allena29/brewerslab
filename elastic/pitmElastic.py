from __future__ import division
#!/usr/bin/python

# piTempLedFlasher
import socket
import json
import os
import hashlib
import struct
import socket
import syslog
import sys
import threading
import time
from datetime import datetime
from elasticsearch import Elasticsearch

"""
Elastic search running on a dedicated raspberry pi 
    ES_JAVA_OPTS="-Xms512m -Xmx5120m" bin/elasticsearch

https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-5.4.1.tar.gz

pip install elasticsea:q
ch
"""

from pitmCfg import pitmCfg

class pitmELKMonitor:


	SERVER_ADDRESS = ('192.168.3.1', 5959)

	def __init__(self):
		self.logging=2		# 1 = syslog, 2 = stderr
		self.cfg = pitmCfg()

		self.elasticsock = None
		
		self.msg_dict = {
                        'author' : 'dr-rudi.mellon-collie.net',
		}

	

	def _open_socket_if_it_is_closed(self):
		if self.elasticsock:
			return True

		try:
                        self.elasticsock = Elasticsearch()

		except:
			self.elasticsock = None
		
		return self.elasticsock


	def decodeTempMessage(self,data,zone="Unknown"):
		"""
		"""

		try:
			cm = json.loads( data )
		except:
			return

		checksum = cm['_checksum']
		cm['_checksum'] ="                                        "
		ourChecksum = hashlib.sha1("%s%s" %(cm,self.cfg.checksum)).hexdigest()
		self.doMonitoring = False
		if cm.has_key("_mode"):
			self._mode=cm['_mode']
			if cm['_mode'].count( "delayed_HLT"):
				self.doMonitoring=True
			if cm['_mode'].count( "hlt"):
				self.doMonitoring=True
			if cm['_mode'] == "sparge":
				self.doMonitoring=True

			if cm['_mode'].count("mash"):
				self.doMonitoring=True

			if cm['_mode'].count("boil"):
				self.doMonitoring=True
			
			if cm['_mode'].count("pump"):
				self.doMonitoring=True

			if cm['_mode'].count("cool"):
				self.doMonitoring=True
			
			if cm['_mode'].count("ferm"):
				self.doMonitoring=True

		if self.doMonitoring:
			for probe in cm['currentResult']:
				if cm['currentResult'][probe]['valid']:
					print probe,cm['currentResult'][probe]['temperature']
                                        print cm
					now = time.localtime()

					probeId = self.cfg.probeId[probe]

					self.msg_dict[probeId] = float(cm['currentResult'][probe]['temperature'])
					self.msg_dict["timestamp"] = datetime.now()


                                    
                                        target= cm['tempTarget%s%s' % (probeId[0].upper(), probeId[1:])]                    
		    			self._open_socket_if_it_is_closed()
                                        self.msg_dict['%s_low' %(probeId)] = float(target[0])
                                        self.msg_dict['%s_high' %(probeId)] = float(target[1])
                                        self.msg_dict['%s_target' %(probeId)] = float(target[2])
                                        
                                        ### TODO have to clear out mdg_dict each time we change mode.

        
                                        self.msg_dict["recipe"] = cm['_recipe']
					try:
                                                res = self.elasticsock.index(index="pitmtemp", doc_type='mcast-temp', id=int(time.time()*10), body=self.msg_dict)
						print "sent", res

					except:
						print "Unable to send on socket"
						self.elasticsock = None
	
	def updateStatsZoneA(self):
		self.sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
		self.sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
		self.sock.setsockopt(socket.SOL_IP, socket.IP_MULTICAST_TTL, 4)
		self.sock.bind(('', self.cfg.mcastTemperaturePort))
		mreq = struct.pack("4sl", socket.inet_aton(self.cfg.mcastGroup), socket.INADDR_ANY)
		self.sock.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, mreq)
		self.mcastMembership=True

		while True:
			(data, addr) = self.sock.recvfrom(1200)
			self.decodeTempMessage(data,zone="A")	
			time.sleep(2)		


if __name__ == '__main__':
	try:
		controller = pitmELKMonitor()

		updateStatsThread = threading.Thread(target=controller.updateStatsZoneA)
		updateStatsThread.daemon = True
		updateStatsThread.start()
	
		while 1:
			time.sleep(1)	

	except KeyboardInterrupt:
		controller.uncontrol()



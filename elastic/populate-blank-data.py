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

https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-6.0.0.tar.gz

pip install elasticsearch


It seems that elasticsearch uses the bind address to determine development vs
production.

The following was needed in the config...
 bootstrap.system_call_filter: false
 discovery.type: single-node


Kibana configuration is updated to not use http://localhost:9200 as the
elasticsearch url.


"""

from pitmCfg import pitmCfg

class pitmElasticMonitor:


    SERVER_NAME = 'dr-rudi.mellon-collie.net'


    def __init__(self):
        self.cfg = pitmCfg()

        self.elasticsock = None

        self.msg_dict = {
                'host' : 'init.localhost.localdomain'
        }

        for probe in self.cfg.probeId:
            now = time.localtime()
            print "adding probe",self.cfg.probeId
            probeId = self.cfg.probeId[probe]
            self
            self.msg_dict[probeId] = 0.0015
            self.msg_dict["timestamp"] = datetime.now()
        
            target= [0.002,0.001,0.003]
            self._open_socket_if_it_is_closed()
            self.msg_dict['%s_low' %(probeId)] = float(target[0])
            self.msg_dict['%s_high' %(probeId)] = float(target[1])
            self.msg_dict['%s_target' %(probeId)] = float(target[2])
                
            self.msg_dict["recipe"] = '__init'
            res = self.elasticsock.index(index="pitmtemp", doc_type='mcast-temp', id=int(time.time()*10), body=self.msg_dict)

	
    def _open_socket_if_it_is_closed(self):
        if self.elasticsock:
            return True

        try:
            self.elasticsock = Elasticsearch(['192.168.1.182'])
        except:
            self.elasticsock = None
        
        return self.elasticsock


if __name__ == '__main__':
    controller = pitmElasticMonitor()
    print "Populated elasticsearch with blank data for each probe"

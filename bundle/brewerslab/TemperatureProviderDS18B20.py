#!/usr/bin/python

import Villain 
import os
import hashlib
import struct
import socket
import syslog
import json
import sys
import time
import re

"""
This is an example of a basic temperature provider with basic logic
included to filter out known bad results. In this case the 1wire
temperature probes can return spurious readings if multiple probes
are on the same bus and there is interference - typically this means
a reading of 85deg C is returned. 

In the use case of the brewery there is never a reading which suddenly
becomes 85deg C - the boil and heating sparge water may return 85 deg
but there will be a steady rise/fall in temperature because it takes
a lot of energy to heat the large of volume of liquor.

Potentially Tilt Hydrometers could be an alternative provide but I 
would prbably stick with DS18B20 probes even if I went for a Tilt (of
course that would provide a new SpecificGravityProvider ;-) but nothing
says we have to care about the temperature it sends us.

"""

class TemperatureProviderDS18B20(Villain.Goblin):

    def setup(self):
        self.rxTemp = re.compile("^.*t=(\d+)")

        # odd readings
        self.odd_readings = {}

        # we need to supress results of 0 and 85 if they are the instant result
        self.lastResult = {}

        print os.getcwd()
        if os.path.exists("simulator"):
            self.one_wire_temp_result_directory = "test/artefacts/sys_bus_w1_devices"
            self.log.warning('Using fake 1wire directory')
        else:
            self.one_wire_temp_result_directory = "/sys/bus/w1/devices/"


    def _reject_result(self, probe, temperature, reason="unspecified"):
        self.odd_readings[probe].append(temperature)
        self._log('rejecting result %s %s (reason: %s)' % (probe, temperature, reason))
        self.currentTemperatures[probe] = {'timestamp': time.time(), 'temperature': temperature, 'valid': False}

    def _accept_adjust_and_add_a_reading(self, probe, temperature):
        adjust = 0
        # TODO: this needs to use xpath and path handler.
        # Note: if we get from a list though we get a list back - depends on how good our
        # xpath is ther emight be multiple elements, there might not be 
        # However this all seems to work
        """
        {
    "brewhouse": {
        "temperature": {
            "hardware": {
                "probe": {
                    "132-123": {
                        "id": "132-123",
                        "offsets": {
                            "1.2 1.3": {
                                "high": 1.3,
                                "low": 1.2,
                                "offset": 0.0
                            },
                            "1.3 1.99": {
                                "high": 1.99,
                                "low": 1.3,
                                "offset": 0.0
                            }
                        }
                    }
                }
            },
            "fermentation": {
                "setpoint": 17.0,
                "probe": {
                    "id": ""
                },
                "lowpoint": 16.7,
                "results": {
                    "average": {}
                },
                "highpoint": 17.3
            }
        }
    },
    "recipes": {
        "recipe": {}
    },
    "ingredients": {
        "adjunct": {},
        "hops": {},
        "fermentable": {}
    }
}

In [45]: #probe.offsets.add('1.3 1.99')

In [46]: print( ph.get("/brewhouse/temperature/hardware/probe[id='132-123']/offsets[low='1.2'][high='1.3']"))
[<pyangbind.lib.yangtypes.YANGBaseClass object at 0x104c0b168>]

In [47]: offsets = ph.get("/brewhouse/temperature/hardware/probe[id='132-123']/offsets")

In [48]:  ph.get("/brewhouse/temperature/hardware/probe[id='132-123']/offsets")
Out[48]:
[<pyangbind.lib.yangtypes.YANGBaseClass object at 0x104c0b168>,
 <pyangbind.lib.yangtypes.YANGBaseClass object at 0x104c56050>]

In [49]:  ph.get("/brewhouse/temperature/hardware/probe[id='132-123']/offsets")[0].low
Out[49]: Decimal('1.20')

In [50]:  ph.get("/brewhouse/temperature/hardware/probe[id='132-123']/offsets")[1].low
Out[50]: Decimal('1.30')

In [51]:


#Appears if we fetchon a container we get a list
# in fact we always get a list... just if it has multiple entries or ot is the question.

        """

        probe_offsets = self.get_config("/hardware/probe[id='%s']" % (probe))

        for offset in probe_offsets:
            adjustMin = offset.low
            adjustMax = offset.high
            adjustAmount = offset.amount
            if temperature >= adjustMin and temperature < adjustMax:
                adjust = adjustAmount
                temperature = temperature + adjust
                break

        self.log.info("Accepting result %s lastResult %s (Adjusted by %s)" % (temperature, self.lastResult[probe], adjust))
        self.currentTemperatures[probe] = {'timestamp': time.time(), 'temperature': temperature, 'valid': True}
        self.lastResult[probe] = temperature
        self.odd_readings[probe] = []

    def _read_temperature_from_external_probe(self, probe):
        temperature = 0
        ok = False
        text = "NON"

        try:
            o = open("%s/%s/w1_slave" % (self.one_wire_temp_result_directory, probe))
            text = o.readline()
            temp = o.readline()
            o.close()
        except:
            pass

        if text.count("YES") and self.rxTemp.match(temp):		# CRC=NO for failed results
            (temp,) = self.rxTemp.match(temp).groups()
            temperature = float(temp) / 1000
            ok = True

        return (temperature, ok)

    def _get_probes_to_monitor(self):
        probes = []
        for probe in os.listdir(self.one_wire_temp_result_directory):
            if probe[0:2] == "28":
                self.log.debug('Checking if we should monitor %s' % (probe))
                probes.append(probe)
#                if self.probesToMonitor.has_key(probe):
#                    probes.append(probe)
        return probes

    def _error_reading_from_external_temperature_probe(self, probe):
        """
        A reading of exactly 85 can be an error from the 1wire probe.
        This rejects a reading of exactly 85 if the preceeding reading isn't 
        close enough 
        """
        if self.lastResult[probe] > 80 and self.lastResult[probe] < 85:
            pass
        elif self.lastResult[probe] > 85:
            pass
        else:
            self._reject_result(probe, 85, '85 indicates mis-read')
            return True
        return False

    def getResult(self):
        self.log.debug('getResult - %s' % (self._get_probes_to_monitor()))
        for probe in self._get_probes_to_monitor():
            # A place to store odd results
            if not self.odd_readings.has_key(probe):
                self.odd_readings[probe] = []

            (temperature, ok) = self._read_temperature_from_external_probe(probe)
            if ok:
                if not self.lastResult.has_key(probe):
                    self.lastResult[probe] = 0

                # Exactly 85 indictes misread
                if temperature == 85 and self._error_reading_from_external_temperature_probe(probe):
                    return True

                if (self.lastResult[probe]) == 0 or len(self.odd_readings[probe]) > 5:
                    self._accept_adjust_and_add_a_reading(probe, temperature)
                else:
                    if temperature > self.lastResult[probe] * 1.05 or temperature < self.lastResult[probe] * 0.95:
                        self._reject_result(probe, temperature, '+/- 5%% swing')
                    else:
                        self._accept_adjust_and_add_a_reading(probe, temperature)

                time.sleep(1.0)

    def start(self):
        while True:
            self.getResult()
            time.sleep(1)


if __name__ == '__main__':
    try:
        daemon = TemperatureProviderDS18B20('TemperatureDS18B20', 'brewerslab', '/brewhouse/temperature')
        daemon.start()
    except KeyboardInterrupt:
        pass

import sys
from i2ctools import *
import time
from pitmLCDisplay import *
lcd=pitmLCDisplay()

testtype="all"
if len(sys.argv) > 1:
	testtype=sys.argv[1]

lcd.sendMessage("Testing",0)
lcd.sendMessage(" Temperature",1)


i2c=i2ctools()
if testtype == "all" or testtype == "temp":




	lcd.sendMessage(" Mash + HLT" ,2)

	i2c.output('fermProbePower',0)
	i2c.output('fermProbeData',0)
	i2c.output('hltProbePower',1)
	i2c.output('hltProbeData',1)
	i2c.output('boilProbeData',0)
	i2c.output('boilProbePower',0)
	i2c.output('mashProbePower',1)
	i2c.output('mashProbeData',1)
	print "Temperature - probe port Mash + HLT" 
	tempBaseDir="/sys/bus/w1/devices/"
	for x in range(600):
		print os.listdir(tempBaseDir)
		for probe in os.listdir( tempBaseDir ):
			if probe.count("28-"):
				print x,time.ctime(),
				try:
					o=open( "%s/%s/w1_slave" %(tempBaseDir,probe))
					print probe,		
					text=o.readline()
					if text.count("NO"):
						print "invalid"
						lcd.sendMessage(" invalid",3)

					if text.count("YES"):		# CRC=NO for failed results
						temp=o.readline()
						o.close()
						
						temperature = float(temp.split(" ")[9][2:])/1000

						# fudge factor for 0 / 85 results when not connected	

						lcd.sendMessage(" %s %s" %(temperature,probe[3:]),3)
						print temperature	
				except:
					lcd.sendMessage(" file missing",3)
					print "file disappeared",probe
			time.sleep(1)


	for probe in ['mash','hlt','ferm','boil']:

		lcd.sendMessage(" %s" %(probe),2)

		i2c.output('fermProbePower',0)
		i2c.output('fermProbeData',0)
		i2c.output('hltProbePower',0)
		i2c.output('hltProbeData',0)
		i2c.output('boilProbeData',0)
		i2c.output('boilProbePower',0)
		i2c.output('mashProbePower',0)
		i2c.output('mashProbeData',0)
		i2c.output('%sProbeData' %(probe) ,1)
		i2c.output('%sProbePower' %(probe)  ,1)
		print "Temperature - probe port %s" %(probe)
		tempBaseDir="/sys/bus/w1/devices/"
		for x in range(600):
			print os.listdir(tempBaseDir)
			for probe in os.listdir( tempBaseDir ):
				if probe.count("28-"):
					print x,time.ctime(),
					try:
						o=open( "%s/%s/w1_slave" %(tempBaseDir,probe))
						print probe,		
						text=o.readline()
						if text.count("NO"):
							print "invalid"
							lcd.sendMessage(" invalid",3)

						if text.count("YES"):		# CRC=NO for failed results
							temp=o.readline()
							o.close()
							
							temperature = float(temp.split(" ")[9][2:])/1000

							# fudge factor for 0 / 85 results when not connected	

							lcd.sendMessage(" %s" %(temperature),3)
							print temperature	
					except:
						lcd.sendMessage(" file missing",3)
						print "file disappeared",probe
				time.sleep(1)

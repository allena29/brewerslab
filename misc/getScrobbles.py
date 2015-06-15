#!/usr/bin/python

import re
import urllib
import os
import sys
import time
import json
import _mysql

# ime(tm_year=2015, tm_mon=6, tm_mday=15, tm_hour=19, tm_min=47, tm_sec=7, tm_wday=0, tm_yday=166, tm_isdst=1)


apiKey=open("last.fm.apikey").read()[:-1]
lastFmUser="allena29"
db=_mysql.connect(host="localhost",user="brewerslab",passwd='beer',db="brewerslab")

baseTime=time.mktime( ( int(sys.argv[1]),int(sys.argv[2]),int(sys.argv[3]),5,0,0,0,0,0))

TRACKS={}
TRACKTIME=[]
TRACKSTEP={}
STEPS=[]
STEPTIME={}

lastTrack=("","")

#print "Base Time: ",time.ctime(baseTime)
for c in range(6):
	
	segment=baseTime+(c*3*60*60)
#	print "",time.ctime(segment),"-",time.ctime(segment+10799)
	if not os.path.exists( "%s.%s.lastfm.scrobbles" %(lastFmUser,segment)):
		url="http://ws.audioscrobbler.com/2.0/?method=user.getrecenttracks&user=%s&from=%s&to=%s&limit=200&api_key=%s&format=json" %(lastFmUser,int(segment),int(segment+10799),apiKey)
		o=open( "%s.%s.lastfm.scrobbles" %(lastFmUser,segment),"w")
		f=urllib.urlopen(url)
		o.write(f.read())
		o.close()
	
	o=open( "%s.%s.lastfm.scrobbles" %(lastFmUser,segment))
	j=json.loads(o.read())
	o.close()

	if j['recenttracks'].has_key("track"):
		for x in j['recenttracks']['track']:
			if not (x['name'],x['artist']['#text']) == lastTrack:
				lastTrack= (x['name'],x['artist']['#text'])
				TRACKS[ x['date']['uts'] ] = x
				TRACKTIME.append( x['date']['uts'] )

TRACKTIME.sort()
TRACKTIME.sort()

#for track in TRACKTIME:
#	print time.ctime(float(track)),TRACKS[track]['name'],"by",TRACKS[track]['artist']['#text']


lastStepTime=baseTime
cursor=db.query ("select stepEndTime,stepName FROM gBrewlogStep WHERE stepEndTime > %s and stepEndTime < %s ORDER BY stepEndTime ASC" %(int(baseTime),int(baseTime+86400)))
result=db.use_result()
row=result.fetch_row()
while row:
	((stepEnd,stepName),)=row
	STEPS.append(stepName)
	STEPTIME[ stepName ] =(lastStepTime, stepEnd )
	lastStepTime=stepEnd
	i=0
	while i >=0 and i < len(TRACKTIME):
		if TRACKTIME[i] < stepEnd and not TRACKS[TRACKTIME[i]].has_key("_mapped"):
			track= TRACKS[ TRACKTIME[i] ]
			TRACKS[TRACKTIME[i]]['_mapped']=True
			if not TRACKSTEP.has_key(stepName):
				TRACKSTEP[stepName]=[]
			TRACKSTEP[stepName].append(track)
		i=i+1
	
	row=result.fetch_row()



spaceRegex=re.compile("^\s*\-\s*")
for step in STEPS:
	if TRACKSTEP.has_key(step):
		(a,z) =  STEPTIME[ step ]
		print spaceRegex.sub('',step), " {",time.ctime(float(a)),"-",time.ctime(float(z)),"}"
		for track in TRACKSTEP[step]:
			print " -", track['name']," by ",track['artist']['#text']

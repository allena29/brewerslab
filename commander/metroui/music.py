import time
import cgi
import json
import os
import sys
from thememetro import *

form=cgi.FieldStorage()
theme=webTheme()
theme.bgcolor="#ffffff"
theme.noHeader=True
sys.stdout.write("Content-Type:text/html\n\n")
theme.pagetitle=""
theme.goBackHome=""
theme.bodytitle=""
theme.presentHead()
print "<div class=\"container\">"
o=open("../../misc/%s.lastfm.json" %(form['brewlog'].value))
j=json.loads(o.read())
o.close()
for lastfm in j:
	print "<BR><b>%s</b><br>&nbsp;&nbsp;(<i>%s - %s</i>)<BR>" %( lastfm['step'], time.ctime( lastfm['a']), time.ctime( lastfm['z']) )
	for (track,artist) in lastfm['tracks']:
		try:
			print "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- "
			print track
			print " by "
			print artist
			print "<BR>"
		except:
			pass


from __future__ import division
#!/usr/bin/python
import os
import json
import re
import sys
import _mysql
import mysql.connector
import time


host="192.168.1.13"

db=_mysql.connect(host=host,user="brewerslab",passwd='beer',db="brewerslab")
i=0
sumIbu=0
con=mysql.connector.connect(host=host,user='brewerslab',password='beer',database="brewerslab")
con2=mysql.connector.connect(host=host,user='brewerslab',password='beer',database="brewerslab")
con3=mysql.connector.connect(host=host,user='brewerslab',password='beer',database="brewerslab")
con4=mysql.connector.connect(host=host,user='brewerslab',password='beer',database="brewerslab")



# Get Description
cursor=con.cursor()
cursor.execute("select process from gProcesses ORDER BY process DESC LIMIT 0,1")
for row in cursor:
    (process,)=row
cursor.close()



O=open("recipes/process.html","w")
o=open("templateprocess.html")
y=o.readline()
while y != "":
    if y.count("<!-- PROCESS -->"):
        O.write( y.replace("<!-- PROCESS -->", process ) )
    else:
        O.write(y)

    y=o.readline()
o.close()



BLACKLIST=['mash_mid18_temp','mash_mid19_temp','mash_mid20_temp','mash_mid21_temp','mash_mid22_temp','mash_mid23_temp','mash_mid24_temp','mash_mid25_temp','kettle1volume','mash_mid10_temp','mash_mid11_temp','mash_mid12_temp','mash_mid13_temp','mash_mid8_temp','mash_mid9_temp','mash_mid14_temp','postboil_precool_og']

print "process",process
cursor=con.cursor()
cursor.execute("select stepName,activityNum from gProcess WHERE activityNum >= 0 AND stepNum=-1 AND subStepNum=-1 AND process='%s' ORDER BY activityNum" %(process))
for row in cursor:
    (actStepName,activityNum)=row
    print actStepName
    i=0

    STEPS=""
    cursor2=con2.cursor()
    cursor2.execute("select stepName,stepNum,text,img,attention,auto from gProcess WHERE activityNum = %s AND stepNum > -1  AND subStepNum = -1 AND process ='%s' ORDER BY stepNum ASC" %(activityNum,process))
    for row2 in cursor2:
        i=i+1
        (stepName,stepNum,stepText,img,warning,auto)=row2
        print stepNum,stepName 
        STEP="<p style='font-family: body;font-size: 200%%'>%s. " %(i)
        STEP=STEP+stepName
        if len(auto)>3:
            STEP=STEP+"&nbsp;&nbsp;<span style='color:blue'>["+auto+"]</span> "
        STEP=STEP+"</p>"
        STEP=STEP+"<p style='font-family: body; font-size: 150%'>"+stepText.replace("\n","<br>")


        STEP=STEP+"</p>"
        if len(warning)>3:
            STEP=STEP+"<table border=0><tr><td><img src='images/alert.png' width=32 height=32'></td><td><span style='font-family: body; font-size: 150%; color:red'>"+warning+"</span></td></tr></table><br>"
        imgs=json.loads(img)
        for img in imgs:
            if not os.path.exists("recipes/processimg/%s" %(img)):  
                STEP=STEP+"<blockquote>MISSING IMAGE %s></blockquote>" %(img)
            else:
                STEP=STEP+"<blockquote><img src=processimg/%s></blockquote>" %(img)


        STEP=STEP+"<table border=0>"

        cursor4=con3.cursor()
        cursor4.execute("select fieldLabel,fieldKey,fieldWidget from gField WHERE process ='%s' AND  activityNum = %s AND stepNum = %s" %( process,activityNum,stepNum)) 
        rec=None
        for (reclabel,reckey,widget) in cursor4:
            if not reckey in BLACKLIST and len(widget) < 1:
                STEP=STEP+"<tr><td><img src='images/totick.png' width=32 height=32></td><td><span style='font-family: body;font-size:150%%;'>Make a note of %s</span></td></tr>" %(reclabel)


        cursor3=con3.cursor()
        cursor3.execute("select stepName,subStepNum from gProcess WHERE activityNum = %s AND stepNum = %s AND subStepNum > -1 AND process ='%s' ORDER BY subStepNum ASC" %( activityNum,stepNum,process)) 
        subSteps=False
        for row3 in cursor3:
            subSteps=True
            (stepName,subStepNum) = row3
            STEP=STEP+"<tr><td><img src='images/totick.png' width=32 height=32></td><td><span style='font-family: body;font-size:150%%;'>%s</span></td></tr>" %(stepName)
        STEP=STEP+"</table>"
        STEPS=STEPS+STEP
        STEPS=STEPS+"<hr>" 
    o=open("templateactivity.html")
    y=o.readline()
    while y != "":
        if y.count("<!-- ACTIVITY -->"):
            O.write( y.replace("<!-- ACTIVITY -->", actStepName  ) )
        elif y.count("<!-- STEPS -->"):
            O.write( y.replace("<!-- STEPS -->", STEPS) )
        else:
            O.write(y)
        y=o.readline()
    o.close()
cursor.close()









O.close()

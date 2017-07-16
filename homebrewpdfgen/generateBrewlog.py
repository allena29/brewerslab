from __future__ import division
#!/usr/bin/python
import json
import os
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


brewlog=sys.argv[1]

# Get Description
cursor=con.cursor()
cursor.execute("select process from gProcesses ORDER BY process DESC LIMIT 0,1")
for row in cursor:
    (process,)=row
cursor.close()

cosmeticName=None
cursor=con.cursor()
print "select cosmeticName,recipe,gBrewlogs.process,gRecipes.process from gBrewlogs,gRecipes WHERE brewlog='%s' AND recipename=recipe;" %(brewlog)
cursor.execute("select cosmeticName,recipe,gBrewlogs.process,gRecipes.process from gBrewlogs,gRecipes WHERE brewlog='%s' AND recipename=recipe;" %(brewlog))
for (cosmeticName,recipe,p1,p2) in cursor:
    if not p1 == p2 or not p1 == process:
        sys.stderr.write("Need to be using the latest process")
        sys.exit(3)

print cosmeticName,brewlog

"""
MATRIX={}

anum=0
TOR=[]
cursor=con.cursor()
cursor.execute("select toReplace,stepNum from gCompileText where process='%s' AND activityNum = %s ORDER BY activityNum,stepNum,subStepNum" %(process,anum))
for (toReplace,stepNum) in cursor:
    tor = json.loads(toReplace)
    tor.sort()
    for t in tor:
        if not t in TOR:
            TOR.append(t)
            if not MATRIX.has_key(stepNum):
                MATRIX[stepNum]=[]
            MATRIX[stepNum].append( (t,'',True,False)    )
cursor.close()

REC=[]
cursor=con.cursor()
cursor.execute("select fieldKey,fieldLabel,fieldWidget,stepNum from gField where process='%s' AND activityNum = %s ORDER BY activityNum,stepNum,fieldKey" %(process,anum))
for (fieldKey,fieldLabel,fieldWidget,stepNum) in cursor:
    if not fieldKey in REC:
        REC.append(fieldKey)
        if not MATRIX.has_key(stepNum):
            MATRIX[stepNum]=[]
        MATRIX[stepNum].append( (fieldKey,fieldLabel,False,fieldWidget)    )
        
if not cosmeticName:
    sys.stderr.write("No brewlog")
    sys.exit(4)

table=""
i=0
steps=MATRIX.keys()
steps.sort()
"""



STATS={}
cursor=con.cursor()
cursor.execute("select mash_liquid_6,strike_temp_5,strike_temp,mash_liquid,target_mash_temp,sparge_water,sparge_heating_time,sparge_temp,boil_vol,kettle1evaporation,kettle1preboilgravity,estimated_og,postboilprecoolgravity,preboil_gravity,topupvol,estimated_abv,estimated_fg,precoolfvvolume,crs,calciumsulphate,calciumchloride,magnesiumsulphate,sodiumchloride,calciumcarbonate,sodiumcarbonate,sodiumsulphate,magnesiumcarbonate  FROM gRecipeStats WHERE brewlog='%s'" %(brewlog))
for (mash_liquid_6,strike_temp_5,strike_temp,mash_liquid,target_mash_temp,sparge_water,sparge_heating_time,sparge_temp,boil_vol,kettle1_evaporation,kettle1preboilgravity,estimated_og,postboilprecoolgravity,preboil_gravity,topup_vol,estimated_abv,estimated_fg,precoolfvvolume,w9,w1,w2,w3,w4,w5,w6,w7,w8) in cursor:
    STATS['mash_liquid_6'] = "%.1f L" %( mash_liquid_6)
    STATS['mash_liquid'] = "%.1f L" %( mash_liquid)
    STATS['sparge_water'] = "%.1f L" %( sparge_water)
    STATS['strike_temp_5'] = "%.1f &deg;C" %( strike_temp_5)
    STATS['strike_temp'] = "%.1f &deg;C" %( strike_temp)
    STATS['target_mash_temp'] = "%.1f &deg;C" %( target_mash_temp )
    STATS['sparge_temp'] = "%.1f &deg;C" %( sparge_temp )
    STATS['sparge_heating_time'] = "%.0f min" %(sparge_heating_time)
    STATS['boil_vol'] = "%.1f L" %(boil_vol)
    STATS['kettle1evaporation'] = "%.1f L" %(kettle1_evaporation)
    STATS['kettle1preboilgravity'] = "%.3f" %(1+(kettle1preboilgravity)/1000)
    STATS['estimated_og']= "%.3f" %(0+(estimated_og)/1)
    STATS['estimated_fg']= "%.3f" %(0+(estimated_fg)/1)
    STATS['postboilprecoolgravity'] = "%.3f" %(1 + (postboilprecoolgravity/1000))
    STATS['preboil_gravity'] = "%.3f" %(1 + (preboil_gravity/1000))
    STATS['topupvol'] = "%.1f L" %(topup_vol)
    STATS['precoolfvvolume'] = "%.1f L" %(precoolfvvolume)
    STATS['estimated_abv'] = "%.2f %%" %(estimated_abv)
#    STATS['crs'] = "%.1f ml/L" %(w9)
#    STATS["calciumsulphate"] = "%.1f mg/L" %(w1)
#    STATS["calciumchloride"] = "%.1f mg/L" %(w2)
#    STATS["magnesiumsulphate"] = "%.1f mg/L" %(w3)
#    STATS["sodiumchloride"] = "%.1f mg/L" %(w4)
#    STATS["calciumcarbonate"] = "%.1f mg/L" %(w5)
#    STATS["sodiumcarbonate"]= "%.1f mg/L" %(w6)
#    STATS["sodiumsulphate"] = "%.1f mg/L" %(w7)
#    STATS["magnesiumcarbonate"] = "%.1f mg/L" %(w8)


BLACKLIST=['mash_mid18_temp','mash_mid19_temp','mash_mid20_temp','mash_mid21_temp','mash_mid22_temp','mash_mid23_temp','mash_mid24_temp','mash_mid25_temp','kettle1volume','mash_mid10_temp','mash_mid11_temp','mash_mid12_temp','mash_mid13_temp','mash_mid8_temp','mash_mid9_temp','mash_mid14_temp','postboil_precool_og','tempdrainedgravity','tempdraingravity','tempdraintemp','__2additionvol','__2additionadjustedgravity','__2additiongravity','__prerinseFg_abv','__prerinseOg_abv','2precoolgravity','__2additionvol','__2gatheredvol','__2additiontemp','fvpostbottlewastage','numbottlesbadfills','fridgetriggerdelay','fermtemp12','fermtemp24','fermtemp48','fermtemp72','fermtemp96','fermtemp120']

tables=[]
table=""
anum=0
letters=['a','b','c','d','e','f','g','h','i','j','k','l','m']
for anum in [0,1,2]:
    cursor=con.cursor()
    cursor.execute("select activityNum,stepNum,stepName,numSubSteps,compileStep FROM gBrewlogStep WHERE activityNum=%s AND brewlog='%s' AND subStepNum =-1 AND stepName > -1 ORDER BY stepNum,subStepNum" %(anum,brewlog))

    i=0
    for (activityNum,stepNum,stepName,numSubSteps,compileStep) in cursor:



        if i == 0:
            table=table+"<tr>"
        else:
            table=table+"<td>&nbsp;</td>"
        table=table+"<td width=50% style='vertical-align:top; border: 2px solid black'>"
   
        table=table+"  <table border=0><tr>"
        if len(stepName) >60:
            stepTitle="%s...." %(stepName[0:60])
        else:
            stepTitle=stepName

        table=table+"    <td><img src='images/spacer.png' width=32 height=32></td><td colspan=2><span style='font-family: body;font-size:140%%'>%s) %s</span>" %(stepNum+1,stepTitle)
        table=table+"   </td>"
        table=table+"   </tr>"

        cursor2=con2.cursor()
        cursor2.execute("select subStepNum,stepName FROM gBrewlogStep WHERE activityNum = %s AND brewlog='%s' AND stepNum = %s AND subStepNum > -1  ORDER BY subStepNum" %(anum,brewlog,stepNum))
        for (subStepNum,subStepName) in cursor2:
            subTitle=subStepName
            if len(subTitle)> 50:
                subTitle="%s..." %(subTitle[0:50])
            table=table+"  <tr>  <td><img src='images/spacer.png' width=32 height=32></td><td><img src='images/totick.png' width=32 height=32></td><td colspan=1><span style='font-family: body;font-size:140%%'>%s) %s</span></tr>" %(letters[subStepNum],subTitle)
        
        #else:
        #    table=table+"  <td><img src='images/totick.png' width=32 height=32></td><td colspan=2><span style='font-family: body;font-size:140%%'>%s) %s</span>" %(stepNum+1,stepTitle)
        #    table=table+"   </td>"
        #    table=table+"   </tr>"
        
        R={}
        cursor3=con3.cursor()
        cursor3.execute("SELECT toReplace FROM gCompileText where process='%s' AND activityNum=%s AND stepNum=%s" %(process,anum,stepNum))
        for (toreplace,) in cursor3:
            for r in json.loads(toreplace):
                if STATS.has_key(r) and not r in BLACKLIST:
                    R[r] = STATS[r]

        for r in R:
            table=table+"<tr><td colspan=2></td><img src='images/spacer.png' width=64 height=1><td><span style='font-family: body; font-size:130%%'>%s = %s</td></tr>" %(r,R[r])

        L={}        
        cursor4=con4.cursor()
        cursor4.execute("SELECT fieldLabel,fieldKey FROM gField WHERE process='%s' AND brewlog='' AND fieldWidget='' AND activityNum=%s AND stepNum=%s" %(process,anum,stepNum))
        for (label,key) in cursor4:
            if not key in BLACKLIST:
                L[label]=1

        if len(L):
            table=table+"<tr><td colspan=2></td><td><table border=2>"
            for l in L:
                table=table+ "<tr height=50><td>%s</td><td width=250>&nbsp;</td></tr>" %(l)
            table=table+"</table></td></tr>"

        table=table+"  </table>"
    
        # outer table belo
        table=table+"</td>"
        if i == 1:
            table=table+"</tr>"
            i=-1
        i=i+1

        print stepName
    tables.append(table)
    table=""


O=open("recipes/brewlog_%s.html" %(brewlog),"w")
o=open("templatebrewlog.html")
y=o.readline()
while y != "":
    if y.count("<!-- BREWLOG -->"):
        O.write( y.replace("<!-- BREWLOG -->", brewlog ) )
    elif y.count("<!-- PROCESS -->"):
        O.write( y.replace("<!-- PROCESS -->", process ) )
    elif y.count("<!-- RECIPE -->"):
        O.write( y.replace("<!-- RECIPE -->", cosmeticName ) )
    elif y.count("<!-- TABLE0 -->"):
        O.write( y.replace("<!-- TABLE0 -->", tables[0]))
    elif y.count("<!-- TABLE1 -->"):
        O.write( y.replace("<!-- TABLE1 -->", tables[1]))
    elif y.count("<!-- TABLE2 -->"):
        O.write( y.replace("<!-- TABLE2 -->", tables[2]))
    else:
        O.write(y)

    y=o.readline()
o.close()




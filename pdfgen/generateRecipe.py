from __future__ import division
#!/usr/bin/python
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


recipename=sys.argv[1]
recipeName=re.compile("[^A-Za-z0-9\._-]").sub( "", sys.argv[1])



firstBrew=99999999999
lastBrew=0
r=[]
b={}
cursor=con.cursor()
cursor.execute("select recipeName FROM gRecipes WHERE relatedRecipe='%s'" %(recipename))
for (R,) in cursor:
    r.append(R)
cursor.close()
r.append(recipename)

for R in r:
    cursor=con.cursor()
    print R,"<<<<<<<<<<<<R",R,firstBrew,lastBrew
    cursor.execute("select entity,brewlog FROM gBrewlogs WHERE recipe = '%s' ORDER BY entity" %(R))
    for (entity,brewlog) in cursor:
        b[entity]=brewlog
        if entity > lastBrew:   
            lastBrew = entity
        if entity < firstBrew:
            firstBrew = entity
    cursor.close()

FIRSTBREW=""
LASTBREW=""
if firstBrew==lastBrew:
    FIRSTBREW=b[ firstBrew ]
elif lastBrew > 0:
    FIRSTBREW=b[ firstBrew ] +"<BR>"
    LASTBREW=b[ lastBrew]

description=None
# Get Description
cursor=con.cursor()
cursor.execute("select recipeName,description,strapline,export,cosmeticName,estimated_abv,estimated_og,estimated_fg,estimated_ibu,batch_size_required,estimated_ebc,target_mash_temp,fermTemp,waterProfile,alkalinity from gRecipes WHERE recipeName = '%s' ;" %( recipename ))
for row in cursor:
    (recipe,description,strapline,brewnum,cosmeticRecipeName,ABV,OG,FG,IBU,BATCH,EBC,MASH,FERMTEMP,WATERPROFILE,ALKALINITY)=row
cursor.close()


if float(EBC) < 1:
    sys.stderr.write("No colour of r %s\n" %(recipeName))
    sys.exit(3)
if not description:
    sys.stderr.write("No such recipe %s\n" %(recipeName))
    sys.exit(2)

BOIL=None
cursor=con.cursor()
cursor.execute("select boil_vol FROM gRecipeStats WHERE recipe='%s' ORDER BY process DESC LIMIT 0,1" %(recipename))
for row in cursor:
    (BOIL)=row
cursor.close()

if not BOIL:
    sys.stderr.write("No recipe stats %s\n" %(recipeName))
    sys.exit(3)

ATTEN = 100 * (OG-FG)/(OG-1.0)



HOPS=""
def hops(hopAddAtA,hopAddAtB):
    global HOPS,recipeName,con
    hop_values=[ 60, 15, 5, 0.08, 0.06, 0.02, 20.222]
    hop_labels = {60:'Copper (60min)',
                  15:'Aroma (15min)',
                  5:'Finishing (5min)',
                  0.08:'Flameout (0min)',
                  0.06:'Whirlpool/Hopback (0min)',
                  0.02:'Dryhop' ,
                  20.222:'First Wort Hop'}
    cursor=con.cursor()
    cursor.execute("select entity,recipeName,ingredient,qty,hopAddAt,hopAlpha,unit FROM gIngredients WHERE recipeName = '%s' AND ingredientType = '%s' AND hopAddAt >=%s and hopAddAt <=%s ORDER BY hopAddAt DESC,qty DESC" %(recipename,"hops",hopAddAtA,hopAddAtB))
    for row in cursor:
        (ent,recipe,ingredient,qty,hopAddAt,hopAlpha,unit)=row
        if hopAddAt <2 or hopAddAt > 2:
            HOPS=HOPS+"<tr>"
            HOPS=HOPS+"<td><span style='font-family: body;font-size:150%%'>%s &nbsp; &nbsp; (%.1f %%)</span></td>" %(ingredient,hopAlpha)
            HOPS=HOPS+"<td><span style='font-family: body;font-size:150%%'></span></td>"
            HOPS=HOPS+"<td><span style='font-family: body;font-size:150%%'>%s</span></td>" %(hop_labels[hopAddAt])
            HOPS=HOPS+"<td><span style='font-family: body;font-size:150%%'>%s gm</span></td>" %(qty)
            HOPS=HOPS+"</tr>"
            HOPS=HOPS+"<tr><td colspan=4><hr style='height: 1px'></td></tr>"

hops(20,21)
hops(20.3,6000)
hops(2.2,20.1)
hops(1.5,2.1)
hops(0,1.4)

TWIST=""
TWISTvisible="display:none"
cursor=con.cursor()
cursor.execute("select entity,recipeName,ingredient,qty,unit FROM gIngredients WHERE recipeName = '%s' AND ingredientType ='misc' AND NOT category LIKE '%%water%%'" %(recipename))
for row in cursor:
    (ent,recipe,ingredient,qty,unit)=row
    TWISTvisible=""
    TWIST=TWIST+"<tr>"
    TWIST=TWIST+"<td colspan=2><span style='font-family: body;font-size:150%%'>%s</span></td>" %(ingredient)
    if qty == 0:
        TWIST=TWIST+"<td colspan=2 align=right><span style='font-family: body;font-size:150%%'>(a little bit)</span></td>"
    else:
        TWIST=TWIST+"<td colspan=2 align=right><span style='font-family: body;font-size:150%%'>%.0f %s</span></td>" %(qty,unit)
    TWIST=TWIST+"</tr>"
    TWIST=TWIST+"<tr><td colspan=4><hr style='height: 1px'></td></tr>"
cursor=con.cursor()
cursor.execute("select entity,recipeName,ingredient,qty,unit FROM gIngredients WHERE recipeName = '%s' AND ingredientType ='hops' AND hopAddAt = 2  AND NOT category LIKE '%%water%%'" %(recipename))
for row in cursor:
    (ent,recipe,ingredient,qty,unit)=row
    TWISTvisible=""
    TWIST=TWIST+"<tr>"
    TWIST=TWIST+"<td colspan=2><span style='font-family: body;font-size:150%%'>%s</span></td>" %(ingredient)
    if qty == 0:
        TWIST=TWIST+"<td colspan=2 align=right><span style='font-family: body;font-size:150%%'>(a little bit)</span></td>"
    else:
        TWIST=TWIST+"<td colspan=2 align=right><span style='font-family: body;font-size:150%%'>%.0f %s</span></td>" %(qty,unit)
    TWIST=TWIST+"</tr>"
    TWIST=TWIST+"<tr><td colspan=4><hr style='height: 1px'></td></tr>"


MALT=""
cursor=con.cursor()
cursor.execute("select entity,recipeName,ingredient,qty,unit FROM gIngredients WHERE recipeName = '%s' AND ingredientType = '%s' ORDER BY qty DESC" %(recipename,"fermentables"))
for row in cursor:
    (ent,recipe,ingredient,qty,unit)=row
    MALT=MALT+"<tr>"
    MALT=MALT+"<td colspan=2><span style='font-family: body;font-size:150%%'>%s</span></td>" %(ingredient)
    MALT=MALT+"<td colspan=2 align=right><span style='font-family: body;font-size:150%%'>%.0f gm</span></td>" %(qty)
    MALT=MALT+"</tr>"
    MALT=MALT+"<tr><td colspan=4><hr style='height: 1px'></td></tr>"
YEAST=""
cursor=con.cursor()
cursor.execute("select entity,recipeName,ingredient,qty,unit FROM gIngredients WHERE recipeName = '%s' AND ingredientType = '%s' ORDER BY qty DESC" %(recipename,"yeast"))
for row in cursor:
    (ent,recipe,ingredient,qty,unit)=row
    YEAST=YEAST+"<tr>"
    YEAST=YEAST+"<td colspan=4><span style='font-family: body;font-size:150%%'>%s</span></td>" %(ingredient)
    YEAST=YEAST+"</tr>"
    YEAST=YEAST+"<tr><td colspan=4><hr style='height: 1px'></td></tr>"

LABEL='<img src="images/label.png">'
if os.path.exists("labels/%s.png" %(recipeName)):
    LABEL='<img src="../labels/%s.png">' %(recipeName)

WATER="<tr>"
WATER=WATER+"<td colspan=2><span style='font-family: body;font-size:150%'>Profile</span></td>"
WATER=WATER+"<td colspan=2 align=right><span style='font-family: body;font-size:150%%'>%s</span></td>" %(WATERPROFILE)
WATER=WATER+"</tr>"
WATER=WATER+"<tr><td colspan=4><hr style='height: 1px'></td></tr>"
WATER=WATER+"<tr>"
WATER=WATER+"<td colspan=2><span style='font-family: body;font-size:150%'>Alkalinity</span></td>"
WATER=WATER+"<td colspan=2 align=right><span style='font-family: body;font-size:150%%'>%s CaCO3</span></td>" %(ALKALINITY)
WATER=WATER+"</tr>"
WATER=WATER+"<tr><td colspan=4><hr style='height: 1px'></td></tr>"
    
o=open("template.html")
O=open("recipes/%03d.%s.html" %(brewnum,recipeName),"w")
y=o.readline()
while y != "":
    if y.count("<!-- description -->"):
        O.write( y.replace("<!-- description -->", description ) )  
    elif y.count("<!-- brewnum -->"):
        O.write( y.replace("<!-- brewnum -->","%s" %( brewnum)) )
    elif y.count("<!-- strapline -->"):
        O.write( y.replace("<!-- strapline -->","%s" %(strapline)) )
    elif y.count("<!-- recipeName -->"):
        O.write( y.replace("<!-- recipeName -->", cosmeticRecipeName ) )  
    elif y.count("<!-- ABV -->"):
        O.write( y.replace("<!-- ABV -->", "%.1f" %( ABV ) ))
    elif y.count("<!-- IBU -->"):
        O.write( y.replace("<!-- IBU -->", "%.0f" %( IBU ) ))
    elif y.count("<!-- OG -->"):
        O.write( y.replace("<!-- OG -->", "%.3f" %( OG ) ))
    elif y.count("<!-- FG -->"):
        O.write( y.replace("<!-- FG -->", "%.3f" %( FG ) ))
    elif y.count("<!-- EBC -->"):
        O.write( y.replace("<!-- EBC -->", "%.0f" %( EBC ) ))
    elif y.count("<!-- ATTEN -->"):
        O.write( y.replace("<!-- ATTEN -->", "%.1f" %( ATTEN ) ))
    elif y.count("<!-- BATCH -->"):
        O.write( y.replace("<!-- BATCH -->", "%.1f" %( BATCH ) ))
    elif y.count("<!-- BOIL -->"):
        O.write( y.replace("<!-- BOIL -->", "%.1f" %( BOIL ) ))
    elif y.count("<!-- FIRSTBREW -->"):
        O.write( y.replace("<!-- FIRSTBREW -->", "%s" %( FIRSTBREW ) ))
    elif y.count("<!-- LASTBREW -->"):
        O.write( y.replace("<!-- LASTBREW -->", "%s" %( LASTBREW ) ))
    elif y.count("<!-- EBC -->"):
        O.write( y.replace("<!-- EBC -->", "%.1f" %( EBC ) ))
    elif y.count("<!-- MASH -->"):
        O.write( y.replace("<!-- MASH -->", "%.0f" %( MASH ) ))
    elif y.count("<!-- FERMTEMP -->"):
        O.write( y.replace("<!-- FERMTEMP -->", "%.0f" %( FERMTEMP ) ))
    elif y.count("<!-- HOPS -->"):
        O.write( y.replace("<!-- HOPS -->", "%s" %( HOPS ) ))
    elif y.count("<!-- MALT -->"):
        O.write( y.replace("<!-- MALT -->", "%s" %( MALT ) ))
    elif y.count("<!-- YEAST -->"):
        O.write( y.replace("<!-- YEAST -->", "%s" %( YEAST ) ))
    elif y.count("<!-- LABEL -->"):
        O.write( y.replace("<!-- LABEL -->", "%s" %( LABEL ) ))
    elif y.count("<!-- WATER -->"):
        O.write( y.replace("<!-- WATER -->", "%s" %( WATER ) ))
    elif y.count("<!-- TWISTvisible -->"):
        O.write( y.replace("<!-- TWISTvisible -->", "%s" %( TWISTvisible ) ))
    elif y.count("<!-- TWIST -->"):
        O.write( y.replace("<!-- TWIST -->", "%s" %( TWIST ) ))
    else:
        O.write( y )
    y=o.readline()
O.close()
sys.exit(0)


from __future__ import division
#!/usr/bin/python
import re
import sys
import _mysql
import mysql.connector
import time




db=_mysql.connect(host="localhost",user="brewerslab",passwd='beer',db="brewerslab")
i=0
sumIbu=0
con=mysql.connector.connect(user='brewerslab',password='beer',database="brewerslab")
con2=mysql.connector.connect(user='brewerslab',password='beer',database="brewerslab")
con3=mysql.connector.connect(user='brewerslab',password='beer',database="brewerslab")
con4=mysql.connector.connect(user='brewerslab',password='beer',database="brewerslab")

recipeName=re.compile("[^A-Za-z0-9\._-]").sub( "", sys.argv[1])

description=None
# Get Description
cursor=con.cursor()
cursor.execute("select recipeName,description,strapline,export,cosmeticName,estimated_abv,estimated_og,estimated_fg,estimated_ibu,batch_size_required from gRecipes WHERE recipeName = '%s' ;" %( recipeName ))
for row in cursor:
    (recipe,description,strapline,brewnum,cosmeticRecipeName,ABV,OG,FG,IBU,batchsize)=row
cursor.close()


if not description:
    sys.stderr.write("No such recipe %s\n" %(recipeName))
    sys.exit(2)


ATTEN = 100 * (OG-FG)/(OG-1.0)


o=open("template.html")
O=open("recipes/%s.html" %(recipeName),"w")
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
    else:
        O.write( y )
    y=o.readline()
O.close()
sys.exit(0)


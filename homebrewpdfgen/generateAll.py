#!/usr/bin/env python2.7
from __future__ import division
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



#cursor=con.cursor()
#cursor.execute("select process from gProcesses ORDER BY process DESC LIMIT 0,1")
#for row in cursor:
#    (process,)=row
#cursor.close()
#
#sys.stderr.write('Genearting process %s\n' %(process))
#os.system('./generateProcess.py %s' %(process))



cursor=con.cursor()
cursor.execute("select recipename from gRecipes WHERE export > 0 ORDER BY export")
for (recipename,) in cursor:
    sys.stderr.write('Generating recipe %s\n'  %(recipename))
    os.system("./generateRecipe.py %s" %(recipename))


sys.stderr.write("Creting PDF's\n")
os.chdir("recipes")
os.system("./genpdf.sh")


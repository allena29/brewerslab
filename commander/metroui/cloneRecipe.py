#!/usr/bin/python
import re
import sys
import cgi
import _mysql
import mysql.connector
from thememetro import *
from cloudNG import *
con=mysql.connector.connect(user='brewerslab',password='beer',database="brewerslab")


form=cgi.FieldStorage()
theme=webTheme()
theme.bgcolor="#ffffff"
#sys.stdout.write("Content-Type:text/html\n\n")
grid={}

if theme.localUser:
	db=_mysql.connect(host="localhost",user="brewerslab",passwd='beer',db="brewerslab")


	orig=form['oldrecipe'].value
	new=form['newrecipe'].value

	brewerslabCloudApi().cloneRecipe("test@example.com", orig,new)

	print "Location: editRecipe.py?recipeName=%s\n" %(new)

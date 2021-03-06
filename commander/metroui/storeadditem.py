#!/usr/bin/python
import re
import sys
import time
import cgi
import _mysql
import mysql.connector
from cloudNG import *
from thememetro import *
con=mysql.connector.connect(user='brewerslab',password='beer',database="brewerslab")


form=cgi.FieldStorage()
theme=webTheme()
theme.bgcolor="#ffffff"
theme.pagetitle="Stores" 
theme.goBackHome="storeaddpage.py?type=%s" %(form['type'].value)
theme.bodytitle="Stores"
grid={}

db=_mysql.connect(host="localhost",user="brewerslab",passwd='beer',db="brewerslab")



if form.has_key("unit"):

#	print "Content-Type:text/html\n\n"
	qtyMultiple=1
	colour=0
	aromatic=0
	biscuit=0
	body=0
	burnt=0
	caramel=0
	chocolate=0
	coffee=0
	grainy=0
	malty=0
	head=0
	nutty=0
	roasted=0
	smoked=0
	sweet=0
	toasted=0
	ppg=0
	hwe=0
	extract=0
	mustMash=0
	isAdjunct=0
	hopAlpha=0
	hopAddAt=0
	attenuation=0
	dosage=0
	wastageFixed=0
	caprequired=0
	co2required=0
	isGrain=0
	fullvolume=0
	volume=0

	hopForm=""
	hopUse=""
	category=""
	subcategory=""
	styles=""
	description=""

	
	if form['type'].value == "yeast":
		attenuation=float(form['attenuation'].value)
	if form['type'].value == "hops":
		sql=sql+",'%s'" %(form['hopForm'].value)
	if form['type'].value == "fermentables":
		hwe=float(form['hwe'].value)
		ppg=hwe/8.345	
		extract=(ppg/46)*100
		if form.has_key("mustmash"):	mustMash=1
		if form.has_key("adjunct"):	isAdjunct=1
		if form.has_key("grain"):	isGrain=1
		colour=float(form['colour'].value)

	sql="INSERT INTO gItems (owner,majorcategory,unit,name,idx"
	sql=sql+",qtyMultiple,colour,aromatic,biscuit,body,burnt,caramel,chocolate,coffee,grainy,malty,head,nutty,roasted,smoked,sweet,toasted,ppg,hwe,extract,mustMash,isAdjunct,hopAlpha,hopAddAt,attenuation,dosage,wastageFixed,caprequired,co2required,isGrain,fullvolume,volume"
	sql=sql+",hopForm,hopUse,category,subcategory,styles,description"
	sql=sql+") VALUES ('test@example.com','%s','%s','%s','%s'" %(form['type'].value,form['unit'].value,form['item'].value, re.compile('[^A-Za-z0-9]').sub('',form['item'].value)	)
	sql=sql+",%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s" %(qtyMultiple,colour,aromatic,biscuit,body,burnt,caramel,chocolate,coffee,grainy,malty,head,nutty,roasted,smoked,sweet,toasted,ppg,hwe,extract,mustMash,isAdjunct,hopAlpha,hopAddAt,attenuation,dosage,wastageFixed,caprequired,co2required,isGrain,fullvolume,volume)
	sql=sql+",'%s','%s','%s','%s','%s','%s'" %(hopForm,hopUse,category,subcategory,styles,description)
	sql=sql+")"
	sys.stderr.write(sql)
#	print sql
	cursor=con.cursor()
	cursor.execute(sql)
	print "Location: storeaddpage.py?type=%s&utc=%s\n" %(form['type'].value,time.time())
	sys.exit(0)
sys.stdout.write("Content-Type:text/html\n\n")
theme.presentHead()

theme.presentBody()
print "<div class=\"container\">"








print """


            <div class="grid fluid">
                <div class="row">
                    <div class="span4">
                    </div>

                    <div class="span4">
                    </div>

                </div>
            </div>


"""

if not theme.localUser:
	print "Page only valid for localusers"
	sys.exit(0)

print """
	    	<div class="content">
			<p>

<form method=POST action='storeadditem.py'>
<input type='hidden' name='type' value='%s'>
 <table class="table">

                        <tbody>

	<tr>
		<td>Item:</td>
		<td><input type='text' name='item' value=''>
""" %(form['type'].value)

print """<br>
		</td>
	</tr>
	<tr>
		<td>Unit:</td>
		<td><select name='unit'>"""
if form['type'].value == "fermentables" or form['type'].value == "hops":
	print "<option>gm</option>"
if not form['type'].value == "fermentables" and not form['type'].value == "hops":
	cursor=con.cursor()
	cursor.execute("select distinct(unit) FROM gItems WHERE length(unit) > 0 ORDER BY unit")
	for row in cursor:
		(unit,)=row
		print "<option>%s</option>" %(unit)
print """</select> <br>
	</td>
	</tr>"""

if form['type'].value == "fermentables":
	print """
		<tr>
			<td>HWE Extract:</td>
			<td><input type="text" size=7 name="hwe" value=""></td>
		</tr>
		<tr>
			<td>Colour:</td>
			<td><input type="text" size=7 name="colour" value=""></td>
		</tr>
		<tr>
			<td>Must Mash:</td>
			<td><input type="checkbox" name="mustmash" value="1"></td>
		</tr>
		<tr>
			<td>Adjunct:</td>
			<td><input type="checkbox" name="adjunct" value="1"></td>
		</tr>
		<tr>
			<td>Is Grain:</td>
			<td><input type="checkbox" name="grain" value="1"></td>
		</tr>
	"""

"""

lt | Extra          |
+---------------+-----------+------+-----+---------+----------------+
| entity        | int(11)   | NO   | PRI | NULL    | auto_increment |
| owner         | char(255) | YES  |     | NULL    |                |
| majorcategory | char(255) | YES  |     | NULL    |                |
| category      | char(255) | YES  |     | NULL    |                |
| subcategory   | char(255) | YES  |     | NULL    |                |
| name          | char(255) | YES  |     | NULL    |                |
| idx           | char(255) | YES  |     | NULL    |                |
| qtyMultiple   | float     | YES  |     | NULL    |                |
| unit          | char(255) | YES  |     | NULL    |                |
| colour        | float     | YES  |     | NULL    |                |
| aromatic      | int(11)   | YES  |     | NULL    |                |
| biscuit       | int(11)   | YES  |     | NULL    |                |
| body          | int(11)   | YES  |     | NULL    |                |
| burnt         | int(11)   | YES  |     | NULL    |                |
| caramel       | int(11)   | YES  |     | NULL    |                |
| chocolate     | int(11)   | YES  |     | NULL    |                |
| coffee        | int(11)   | YES  |     | NULL    |                |
| grainy        | int(11)   | YES  |     | NULL    |                |
| malty         | int(11)   | YES  |     | NULL    |                |
| head          | int(11)   | YES  |     | NULL    |                |
| nutty         | int(11)   | YES  |     | NULL    |                |
| roasted       | int(11)   | YES  |     | NULL    |                |
| smoked        | int(11)   | YES  |     | NULL    |                |
| sweet         | int(11)   | YES  |     | NULL    |                |
| toasted       | int(11)   | YES  |     | NULL    |                |
| ppg           | float     | YES  |     | NULL    |                |
| hwe           | float     | YES  |     | NULL    |                |
| extract       | float     | YES  |     | NULL    |                |
| mustMash      | int(11)   | YES  |     | NULL    |                |
| isAdjunct     | int(11)   | YES  |     | NULL    |                |
| hopAlpha      | float     | YES  |     | NULL    |                |
| hopForm       | char(255) | YES  |     | NULL    |                |
| hopUse        | char(255) | YES  |     | NULL    |                |
| hopAddAt      | float     | YES  |     | NULL    |                |
| attenuation   | float     | YES  |     | NULL    |                |
| dosage        | float     | YES  |     | NULL    |                |
| wastageFixed  | float     | YES  |     | NULL    |                |
| styles        | char(255) | YES  |     | NULL    |                |
| description   | text      | YES  |     | NULL    |                |
| caprequired   | int(11)   | YES  |     | NULL    |                |
| co2required   | int(11)   | YES  |     | NULL    |                |
| isGrain       | int(11)   | YES  |     | NULL    |                |
| fullvolume    | float     | YES  |     | NULL    |                |
| volume        | float     | YES  |     | NULL    |                |
+---------------+-----------+------+-----+---------+----------------+

|    159 | test@example.com | fermentables  | Grain    | Speciality     | CaraGold              | caragold           |           1 | gm   |       9 |        1 |       1 |    1 |     1 |       1 |         1 |      1 |      1 |     1 |    1 |     1 |       1 |      1 |     1 |       1 |  34.04 |  284.16 |      74 |        0 |         0 |        0 |         |        |        0 |           0 |      0 |            0 |        |             |           0 |           0 |       1 |          0 |      0 |
"""

if form['type'].value == "hops":
	print """<tr>
		<td>Hop Alpha Acid</td>
		<td><input type='text' name='hopAlpha' value=''>%</td>
	</tr>"""
	print """<tr>
		<td>Hop Form</td>
		<td><input type='radio' name='hopForm' value='leaf' CHECKED> Leaf <input type='radio' name='hopForm' value='pellet'> Pellet </td>
	</tr>"""

if form['type'].value == "yeast":
	print """<tr>
		<td>Attenuation</td>
		<td><input type='text' name='attenuation' value=''>%</td>
	</tr>"""

print """
	<tr>
		<td colspan=2 align=center><input type="submit" value="Add Item" name="action"></td>
	</tr>

                        </tbody>

                        <tfoot></tfoot>
                    </table>

		</p>
		</div>

"""

print "</form></div>"
theme.presentFoot()



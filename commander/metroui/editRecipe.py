from __future__ import division
#!/usr/bin/python
import re
import sys
import cgi
import _mysql
import mysql.connector
from thememetro import *



class editRecipe:


	def __init__(self):
		self.recipeName=""
		self.activeCalclog=""
		self.activeStats="active "
		self.activeFermentables=""
		self.activeHops=""
		self.activeYeast=""
		self.activeMisc=""
		self.colWidth=100
		self.export=False
		self.editable=False
		self.hideCalclog=False

	def displayRecipe(self):

		if self.export:
			self.editable=False
		if not self.localUser:
			self.editable=False

		db=_mysql.connect(host="localhost",user="brewerslab",passwd='beer',db="brewerslab")
		self.i=0
		self.sumIbu=0
		con=mysql.connector.connect(user='brewerslab',password='beer',database="brewerslab")
		con2=mysql.connector.connect(user='brewerslab',password='beer',database="brewerslab")
		self.con=con
		self.con2=con2

		recipeName=self.recipeName
		description=""
		cursor=con.cursor()
		cursor.execute("select recipeName,description from gRecipes WHERE recipeName = '%s' ;" %( self.recipeName))
		for row in cursor:
			(recipe,description)=row
		cursor.close()



		print """


			    <div class="grid fluid">

				<div class="row">
				    <div class="span12">
		"""

		if self.editable:
			print """
					<form method='POST' action='cgiUpdateDescription.py'>	
						<input type='hidden' value='%s' name='recipeName'>
						<textarea rows=4 cols=80 name="description">%s</textarea><br>
				""" %(self.recipeName,description)
			
			if self.localUser:
				print """
						<input type='submit' value='update description'>
				"""
			print """
					</form>
			 """

		if not self.editable:
			print description
		print """
				</div>
				</div>
		"""

		if self.editable:

			print """

				<div class="row">
				    <div class="span4">

					<div id='calculateOutstanding' class="panel" style='visibility: hidden' onClick="recalculate()">
					    <div class="panel-header bg-darkRed fg-white">
						Recalculate
					    </div>
					</div>


				    </div>

				    <div class="span4">
				    </div>
		"""

		if self.editable:
			print """
				    <div class="span4">
					<div class="panel" data-role="panel">
					    <div class="panel-header bg-darkRed fg-white">
						Clone Recipe
					    </div>
					    <div class="panel-content" style="display:none">
						<form method=POST action='cloneRecipe.py'>
						<input type='text' name='newrecipe' id='newrecipe' value=''> <input type='submit' value='Clone Recipe'>
						<input type='hidden' name='oldrecipe' value='%s'>
						</form>
					    </div>
					</div>
				    </div>
		""" %(form['recipeName'].value)

			print """
					</div>

			"""

		print """
			    </div>
		"""


		print """


		    <div class="accordion" data-role="accordion">


		"""

		# set defaults for when a recipe is created
		batchsize=0
		estimatedOg=0
		estimatedFg=0
		estimatedAbv=0
		estimatedIbu=0	
		boilVolume=0
		topup=0

		print """
		    <div class="accordion-frame">
				<a href="#" class="%sheading">Recipe Stats</a>

				<div class="content">
					<p>
		""" %(self.activeStats)
		cursor=con.cursor()
		cursor.execute("select recipeName,process,target_mash_temp,mash_efficiency,alkalinity,fermTemp,fermLowTemp,fermHighTemp from gRecipes WHERE recipeName = '%s' ;" %(self.recipeName))
		mashEfficiency=0
		for row in cursor:
			(recipeName,process,target_mash_temp,mashEfficiency,alkalinity,fermTemp,fermLowTemp,fermHighTemp)=row
		cursor=db.query ("select recipe,estimated_og,estimated_fg,estimated_abv,estimated_ibu,topupvol,boil_vol,batchsize from gRecipeStats WHERE recipe='%s' AND process = '%s' ORDER BY entity DESC LIMIT 0,1" %(recipeName,process))
		result=db.use_result()
		row=result.fetch_row()
		#((recipe,batchsize,estimatedOg,estimatedFg,estimatedAbv,estimatedIbu,topup,boilVolume),)=row
		try:
			((recipe,estimatedOg,estimatedFg,estimatedAbv,estimatedIbu,topup,boilVolume,batchsize),)=row
		except:
			pass

		#result=db.use_result()
		#row=result.fetch_row()
		#
		if self.editable:
			print "<b>Batch Size:</b> <select id='batchsize'>"
			
			for c in range(45):
				C=c+1
				selected=""
				if float(C) == float(batchsize):
					selected="SELECTED "
				print "<option value='%s' %s>%s L</option>" %(C,selected,C)

			print "</select>"
			print """<a href='javascript:adjustBatchSize()'><i class="icon-checkmark fg-blue"></i></a>"""
			print "<BR>"
		else:
			print "<b>Batch Size:</b> %.1f L<BR>" %(float(batchsize))

		if self.editable:	
			print "<b>Target Ferm Temp:</b> <select id='fermtemp'>"
			selected=""
			NotDone=False
			for c in range(25):
				for d in range(5):
					selected=""
					C=float(c+5+(d/5))
					if C > float(fermTemp)-0.001 and not NotDone:
						selected="SELECTED" 
						NotDone=True
					print "<option value='%.1f' %s>%.1f degC</option>" %(C,selected,C)
				selected=""
			print "</select>"
			print """<a href='javascript:adjustFermTemp()'><i class="icon-checkmark fg-blue"></i></a><br>"""
			print "<b>Target Ferm Temp (Low Threshold):</b> <select id='fermlowtemp'>"
			selected=""
			NotDone=False
			for c in range(25):
				for d in range(5):
					C=float(c+5+(d/5))
					if C > float(fermLowTemp)-0.001 and not NotDone:
						selected="SELECTED" 	
						NotDone=True
					print "<option value='%.1f' %s>%.1f degC</option>" %(C,selected,C)
				selected=""
			print "</select>"
			print """<a href='javascript:adjustFermTemp()'><i class="icon-checkmark fg-blue"></i></a><br>"""
			print "<b>Target Ferm Temp (High Threshold):</b> <select id='fermhightemp'>"
			selected=""
			NotDone=False
			for c in range(25):
				for d in range(5):
					selected=""
					C=float(c+5+(d/5))
					if C > float(fermHighTemp)-0.001 and not NotDone:
						selected="SELECTED" 
						NotDone=True
					print "<option value='%.1f' %s>%.1f degC</option>" %(C,selected,C)
				selected=""
			print "</select>"
			print """<a href='javascript:adjustFermTemp()'><i class="icon-checkmark fg-blue"></i></a><br>"""
			print "<b>Target Mash Temp:</b> <select id='mashtemp'>"
			for c in range(10):
				selected=""

				if float(target_mash_temp) <= float(c+61) and float(target_mash_temp) > float(c+60):
					selected="SELECTED" 
				print "<option value='%s' %s>%s degC</option>" %(c+61,selected,c+61)
			print "</select>"
			print """<a href='javascript:adjustMashTemp()'><i class="icon-checkmark fg-blue"></i></a><br>"""
			print "<b>Mash Efficiency:</b> <select id='efficiency'>"
			for c in range(40):
				selected=""
				if float(c+55) == float(mashEfficiency):
					selected="SELECTED" 
				print "<option value='%s' %s>%s %%</option>" %(c+55,selected,c+55)
			print "</select>"
			print """<a href='javascript:adjustMashEfficiency()'><i class="icon-checkmark fg-blue"></i></a><br>"""

			print "<b>Alkalinity:</b> <select id='alkalinity'>"
			for c in range(50):
				selected=""
				if float(c*5)+5 == float(alkalinity):
					selected="SELECTED" 
				print "<option value='%s' %s>%s CaCo3 mg/l</option>" %((c*5)+5,selected,(c*5)+5)
			print "</select>"
			print """<a href='javascript:adjustAlkalinity()'><i class="icon-checkmark fg-blue"></i></a><br>"""

		else:
			print "<b>Target Ferm Temp:</b> %.1f'C<br>" %(float(fermTemp))
			print "<b>Target Mash Temp:</b> %.1f'C<br>" %(float(target_mash_temp))
			print "<b>Mash Efficiency:</b> %.0f %%<br>" %(float(mashEfficiency))
			print "<b>Alkalinity:</b> %.1f CaCo3 mg/l<br>" %(float(alkalinity))


		print "<b>Process:</b> %s<BR>" %(process)

		print """
		<b>Estimated Gravity:</b> %.3f OG - %.3f FG<br>
		<b>Estimated ABV:</b> %.1f %%<br>
		<b>Estimated IBU:</b> %.0f IBU<br>
		""" %(float(estimatedOg),float(estimatedFg),float(estimatedAbv),float(estimatedIbu))
		  
	



		print """
		<b>Boil Volume:</b> %.1f L<br>
		<b>Topup Volume:</b> %.1f L<br>
		  
		""" %(float(boilVolume),float(topup) ) 

		if not self.export and not self.editable:
			print "<b>Calclog:</b> <a href='calclog.py?recipeName=%s&noheader=%s'>View Log</a><BR>" %(self.recipeName,'True')
		print """
					</p>
				</div>
		    </div>
			"""








		#
		#
		#
		# Fermentables
		#
		#
		#

		print """ 
		    <div class="accordion-frame">
				<a href="#" class="%sheading">Fermentables</a>
				<div class="content">
					<p>

		 <table class="table" width=80%%>
					<thead>
					<tr>
					    <th class="text-left" width=%s>&nbsp;</th>
					    <th class="text-left" width=200>Qty</th>
					    <th class="text-left">Ingredient</th>
					</tr>
					</thead>

					<tbody>
		""" %(self.activeFermentables,self.colWidth)

		itemType='fermentables'
		if self.editable:
			print """
			<tr><td><a href="javascript:addItem('%s')"><i class='icon-plus fg-green'></i></a></td>
			<td><select id='%sQty'>""" %(itemType,itemType)
			for c in range(750):
				print "<option value='%s'>%s gm</option>" %(c*10,c*10)
			print """</select><td><select id='%sItem'>""" %(itemType)
			cursor=con.cursor()
			cursor.execute("select idx,name,isGrain,isAdjunct FROM gItems where majorcategory ='%s' order by name" %(itemType))
			for row in cursor: 
				(idx,name,isGrain,isAdjunct) = row
				row=result.fetch_row()
				if isGrain:
					val="%s (Grain)" %(name)
				elif isAdjunct:
					val="%s (Adjunct)" %(name)
				else:
					val="%s" %(name)
				key=name
				print "<option value=\"%s\">%s</option>" %(key,val)
			print "</select>"

			print """<td><td></td></tr>
			"""
		sumGravity=0
		i=0
		cursor=con.cursor()
		cursor.execute("select entity,recipeName,ingredient,qty,mustMash,isGrain,isAdjunct,hwe,unit FROM gIngredients WHERE recipeName = '%s' AND ingredientType = 'fermentables' ORDER BY qty DESC" %(self.recipeName))
		for row in cursor:
			(ent,recipe,ingredient,qty,mustMash,isGrain,isAdjunct,hwe,unit)=row
			row=result.fetch_row()
			if self.export:
				print "<tr><td>&nbsp;</td>"
				print "<td>%.0f %s</td><td><b>%s</b><br>" %(float(qty),unit,ingredient)
			else:
				print "<tr><td><a href='editIngredient.py?entity=%s&action=delete&type=%s&ingredient=%s&hopAddAt=%s&recipe=%s'><i class='icon-minus fg-red'></i></a></td>" %(ent,itemType,ingredient,0, self.recipeName)
				print "<td id='%sQtyCell%s'><a href=\"javascript:editqty(%s,'%s',%s,%.0f,'%s',%s)\">%.0f %s</a></td><td><b>%s</b><br>" %(itemType,i,ent,itemType,i,float(qty),ingredient,0,float(qty),unit,ingredient)
			if isAdjunct:	print "Adjunct<br>"
			if isGrain:	print "Grain<br>"
			if mustMash:	print "Mash Required<br>"
			print "HWE: %.1f<br>" %(float(hwe))

			print "</td><td>"
			subcursor=con2.cursor()
			subcursor.execute("select recipeName,gravity from gContributions where recipeName='%s' and ingredientType='fermentables' AND ingredient='%s' AND gravity > 0 ORDER BY entity DESC LIMIT 0,1" %(self.recipeName,ingredient) )
			for subrow in subcursor:
				(xxx,gravity)=subrow
				print "Gravity: %.3f" %(1+(gravity/1000))
				sumGravity=sumGravity+gravity
			print "</td></tr>"
			i=i+1
		cursor.close()

		print """
			<tr><td colspan=3></td><td>Gravity: %.3f</td></tr>	
			""" %(1+(sumGravity)/1000)
		print """

					</tbody>

					<tfoot></tfoot>
				    </table>

				</p>
				</div>
		    </div>
		"""










		#
		#
		#
		# Hops
		#
		#
		#
		itemType="hops"
		print """ 
		    <div class="accordion-frame">
				<a href="#" class="%sheading">Hops</a>
				<div class="content">
					<p>

		 <table class="table">
					<thead>
					<tr>
					    <th class="text-left" width=%s>&nbsp;</th>
					    <th class="text-left" width=200>Qty</th>
					    <th class="text-left">Ingredient</th>
						<th class="text-left"></th>

					</tr>
					</thead>

					<tbody>
		""" %(self.activeHops,self.colWidth)

		sumIbu=0
		if self.editable:

			hop_values=[0.009,0.001,5,15,60,20.222]
			hop_labels = {60:'Copper (60min)',15:'Aroma (15min)',5:'Finishing (5min)',0.001:'Flameout (0min)',0.009:'Dryhop',20.222:'First Wort Hop' }
			print """
			<tr><td><a href="javascript:addItem('%s')"><i class='icon-plus fg-green'></i></a></td>
			<td><select id='%sQty'>""" %(itemType,itemType)
			for c in range(500):
				print "<option value='%s'>%s gm</option>" %(c,c)
			print """</select><td><select id='%sItem'>""" %(itemType)
			cursor=con.cursor()
			cursor.execute("select idx,name, hopAlpha,hopUse from gItems where category ='hop' order by name")
			for row in cursor:
				(idx,name,hopAlpha,hopUse) = row
				row=result.fetch_row()
				if len(hopUse):
					val="%s %s %s %%" %(name,hopUse,hopAlpha)
				else:
					val="%s %s %%" %(name,hopAlpha)
				key=name
				print "<option value=\"%s\">%s</option>" %(key,val)
			print "</select>"
			print "<br><select id='hopAddAt'>"
			for hopAddAt in hop_values:
				print "<option value='%s'>%s</option>" %(hopAddAt,hop_labels[hopAddAt])
			print "</select>"
			print "</td><td>"
			
			print """<td></tr>
			"""


		self.hops(20,21)
		self.hops(20.3,6000)
		self.hops(0,20.1)

		print """
			<tr><td colspan=3></td><td>%.1f IBU</td></tr>	
			""" %(self.sumIbu)


		print """

				</tbody>

				<tfoot></tfoot>
			    </table>

			</p>
			</div>
		</div>
		"""


		itemType="yeast"
		print """ 
		    <div class="accordion-frame">
				<a href="#" class="%sheading">Yeast</a>
				<div class="content">
					<p>

		 <table class="table">
					<thead>
					<tr>
					    <th class="text-left" width=%s>&nbsp;</th>
					    <th class="text-left" width=200>Qty</th>
					    <th class="text-left">Ingredient</th>
					</tr>
					</thead>

					<tbody>
		""" %(self.activeYeast,self.colWidth)


		if self.editable:
			print """
			<tr><td><a href="javascript:addItem('%s')"><i class='icon-plus fg-green'></i></a></td>
			<td><select id='%sQty'>""" %(itemType,itemType)
			for c in range(7):
				print "<option value='%s'>%s pkt</option>" %(c,c)
			print """</select><td><select id='%sItem'>""" %(itemType)
			cursor=con.cursor()
			cursor.execute("select idx,name FROM gItems where majorcategory ='%s' order by name" %(itemType))
			for row in cursor: 
				(idx,name) = row
				row=result.fetch_row()
				val="%s" %(name)
				key=name
				print "<option value=\"%s\">%s</option>" %(key,val)
			print "</select>"

			print """<td><td></td></tr>
			"""

		cursor=con.cursor()
		cursor.execute("select entity,recipeName,ingredient,qty,unit FROM gIngredients WHERE recipeName = '%s' AND ingredientType = 'yeast' ORDER BY qty DESC" %(self.recipeName))
		for row in cursor:
			(ent,recipe,ingredient,qty,unit)=row
			row=result.fetch_row()
			if self.export:
				print "<tr><td>&nbsp;</td><td>%.0f %s</td><td><b>%s</b><br>" %(float(qty),unit,ingredient)
			else:
				print "<tr><td><a href='editIngredient.py?entity=%s&action=delete&type=%s&ingredient=%s&recipe=%s'><i class='icon-minus fg-red'></i></a></td><td>%.0f %s</td><td><b>%s</b><br>" %(ent,itemType,ingredient,self.recipeName,float(qty),unit,ingredient)
			print "</td></tr>"
		cursor.close()

		print """

					</tbody>

					<tfoot></tfoot>
				    </table>

				</p>
				</div>
		    </div>
		"""




		print """ 
		    <div class="accordion-frame">
				<a href="#" class="%sheading">Other</a>
				<div class="content">
					<p>

		 <table class="table">
					<thead>
					<tr>
					    <th class="text-left" width=%s>&nbsp;</th>
					    <th class="text-left" width=200>Qty</th>
					    <th class="text-left">Ingredient</th>
					</tr>
					</thead>

					<tbody>
		""" %(self.activeMisc,self.colWidth)

		cursor=con.cursor()
		cursor.execute("select entity,recipeName,ingredient,qty,unit FROM gIngredients WHERE recipeName = '%s' AND ingredientType = 'misc' ORDER BY qty DESC" %(self.recipeName))
		for row in cursor:
			(ent,recipe,ingredient,qty,unit)=row
			row=result.fetch_row()
			print "<tr><td>&nbsp;</td><td>%.0f %s</td><td><b>%s</b><br>" %(float(qty),unit,ingredient)
			print "</td></tr>"
		cursor.close()

		print """

					</tbody>

					<tfoot></tfoot>
				    </table>

				</p>
				</div>
		    </div>
		"""



		if not self.export:
			print """ 
			    <div class="accordion-frame">
					<a href="#" class="%sheading">Calclog</a>
					<div class="content">
						<p>
			""" %(self.activeCalclog)

		if not self.export or not self.localUser:
			print """
			 <table class="table">
						<thead>
						<tr>
						    <th class="text-left" width=100>&nbsp;</th>
						</tr>
						</thead>

						<tbody>
			""" 

			cursor=con.cursor()
			cursor.execute("select recipe,calclog FROM gCalclogs WHERE recipe = '%s' ORDER BY entity DESC LIMIT 0,1" %(self.recipeName))
			for row in cursor:
				(recipe,calclog)=row
				row=result.fetch_row()
				print "<tr><td><tt>%s</tt>" %(re.compile(" ").sub('&nbsp;',re.compile("[\n\r]").sub('<BR>', calclog)))
				print "</td></tr>"
			cursor.close()

			print """

						</tbody>

						<tfoot></tfoot>
					    </table>

					</p>
					</div>
			    </div>
			"""


			
	def hops(self,hopAddAtA,hopAddAtB):
		sumIbu=self.sumIbu
		i=self.i

		hop_values=[0.009,0.001,5,15,60,20.222]
		hop_labels = {60:'Copper (60min)',15:'Aroma (15min)',5:'Finishing (5min)',0.001:'Flameout (0min)',0.009:'Dryhop',20.222:'First Wort Hop' }
		itemType='hops'
		cursor=self.con.cursor()
		cursor.execute("select entity,recipeName,ingredient,qty,hopAddAt,unit FROM gIngredients WHERE recipeName = '%s' AND ingredientType = '%s' AND hopAddAt >=%s and hopAddAt <=%s ORDER BY hopAddAt DESC,qty DESC" %(self.recipeName,itemType,hopAddAtA,hopAddAtB))
		for row in cursor:
			(ent,recipe,ingredient,qty,hopAddAt,unit)=row
#			row=result.fetch_row()
			if self.export or not self.localUser:
				print "<tr><td>&nbsp;</td>"
				print "<td>%.0f %s</a></td><td><b>%s</b><br>" %(float(qty),unit,ingredient)
			else:
				print "<tr><td><a href='editIngredient.py?entity=%s&action=delete&type=%s&ingredient=%s&hopAddAt=%s&recipe=%s'><i class='icon-minus fg-red'></i></a></td>" %(ent,itemType,ingredient,hopAddAt,self.recipeName)
				print "<td id='%sQtyCell%s'><a href=\"javascript:editqty(%s,'%s',%s,%.0f,'%s',%s)\">%.0f %s</a></td><td><b>%s</b><br>" %(itemType,i,ent,itemType,i,float(qty),ingredient,hopAddAt,float(qty),unit,ingredient)

			if hop_labels.has_key( hopAddAt ):
				print "%s - " %(hop_labels[hopAddAt])
			print "%.0f min<br>" %(float(hopAddAt))
		#	print "Estimate %.0f IBU</td></tr>" %(float(
			i=i+1
			print "</td><td>"
			subcursor=self.con2.cursor()
			subcursor.execute("select recipeName,ibu from gContributions where recipeName='%s' and ingredientType='hops' AND ingredient='%s' AND hopAddAt > %s AND hopAddAt < %s   ORDER BY entity DESC LIMIT 0,1" %(self.recipeName,ingredient, hopAddAt-0.02, hopAddAt+0.02) )
			for subrow in subcursor:
				(xxx,ibu)=subrow
				print "%.1f IBU" %(ibu)
				sumIbu=sumIbu+ibu

			print "</td></tr>"
		cursor.close()
		self.i=i
		self.sumIbu=sumIbu




if __name__ == '__main__':

	con=mysql.connector.connect(user='brewerslab',password='beer',database="brewerslab")
	con2=mysql.connector.connect(user='brewerslab',password='beer',database="brewerslab")


	form=cgi.FieldStorage()
	theme=webTheme()
	if form.has_key("noheader"):
		theme.noHeader=True
	theme.bgcolor="#ffffff"
	sys.stdout.write("Content-Type:text/html\n\n")
	if not form.has_key("noheader"):
		theme.pagetitle="%s - Edit Recipe" %(form['recipeName'].value)
		theme.goBackHome="brewerslab.py?recipeName=%s" %(form['recipeName'].value)
		theme.bodytitle="%s" %(form['recipeName'].value)
	theme.presentHead()
	grid={}

	db=_mysql.connect(host="localhost",user="brewerslab",passwd='beer',db="brewerslab")

	export=False
	if form.has_key("export"):	export=True


	theme.presentBody()

	if theme.localUser:
		print """
		<input type=hidden id='outstandingSig' value='-1'>
		<script language="Javascript">
		function addItem(itemType) {
		showdeadcenterdiv(100,30,"spinner",200,50);
		document.getElementById('spinnerText').innerHTML="updating";
		d = new Date();
		document.getElementById('outstandingSig').value=d.getTime();
		document.getElementById('spinner').style.visibility="visible";
		document.getElementById('spinner').style.height="100%%";
		url="editIngredient.py?recipe=%s&action=add&type="+itemType+"&ingredient="+document.getElementById(itemType+'Item').value+"&qty="+document.getElementById(itemType+'Qty').value+"&hopAddAt="+document.getElementById('hopAddAt').value+"&outstandingSig="+document.getElementById('outstandingSig').value;
		window.location.replace(url);
		}


		function recalculate(){
		showdeadcenterdiv(100,30,"spinner",200,50);
		document.getElementById('spinnerText').innerHTML="recalculating"
		document.getElementById('spinner').style.visibility="visible";
		document.getElementById('spinner').style.height="100%%";
		xmlREQ(reloadAfterAjax,"ajaxRecalculate.py?recipe=%s");

		}


		function adjustAlkalinity(){
		url="editIngredient.py?recipe=%s&type=null&action=changeAlkalinity&alkalinity="+document.getElementById('alkalinity').value;
		window.location.replace(url);
		}
		function adjustFermTemp(){
		url="editIngredient.py?recipe=%s&type=null&action=changeFermTemp&fermtemp="+document.getElementById('fermtemp').value+"&fermlowtemp="+document.getElementById('fermlowtemp').value+"&fermhightemp="+document.getElementById('fermhightemp').value;
		window.location.replace(url);
		}
		function adjustMashTemp(){
		url="editIngredient.py?recipe=%s&type=null&action=changeMashTemp&mashtemp="+document.getElementById('mashtemp').value;
		window.location.replace(url);
		}
		function adjustMashEfficiency(){
		url="editIngredient.py?recipe=%s&type=null&action=changeMashEfficiency&mashefficiency="+document.getElementById('efficiency').value;
		window.location.replace(url);
		}
		function adjustBatchSize(){
		url="editIngredient.py?recipe=%s&type=null&action=changeBatchSize&batchsize="+document.getElementById('batchsize').value;
		window.location.replace(url);
		}

		function updateqty(ent,itemtype,i,item,hopAddAt){
		showdeadcenterdiv(100,30,"spinner",200,50);
		d = new Date();
		document.getElementById('outstandingSig').value=d.getTime();
		document.getElementById('spinnerText').innerHTML="updating";
		document.getElementById('spinner').style.visibility="visible";
		document.getElementById('spinner').style.height="100%%";
		qty=document.getElementById(itemtype+"Qty"+i).value;
		url="editIngredient.py?entity="+ent+"&recipe=%s&action=changeqty&type="+itemtype+"&ingredient="+item+"&qty="+qty+"&hopAddAt="+hopAddAt+"&outstandingSig="+document.getElementById('outstandingSig').value;
		window.location.replace(url);
		}

		function editqty(ent,itemtype,i,j,k,l){
			html="<select id='"+itemtype+"Qty"+i+"'>"
			for(c=0;c<=750;c++){
				if(itemtype == "fermentables"){
					C=c*10;
				}else{
					C=c;
				}
				if(C==j){
				html=html+"<option value="+C+" SELECTED>"+C+" gm</option>"
				}else{
				html=html+"<option value="+C+">"+C+" gm</option>"
				}
			}
			html=html+'</select> <a href="';
			html=html+"javascript:updateqty("+ent+",'"+itemtype+"','"+i+"','"+k+"',"+l+")";
			html=html+'"><i class="icon-checkmark fg-blue"></i></a>';
			document.getElementById(itemtype+'QtyCell'+i).innerHTML=html;
		}
		</script>
		""" %(form['recipeName'].value,form['recipeName'].value,form['recipeName'].value,form['recipeName'].value,form['recipeName'].value,form['recipeName'].value,form['recipeName'].value,form['recipeName'].value)


	print "<div class=\"container\">"


	
	r=editRecipe()
	if form.has_key("active") or export:
		if not export:	activeStats=""
		if export or form['active'].value == "fermentables":	r.activeFermentables = "active "
		if export or form['active'].value == "hops":	r.activeHops = "active "
		if export or form['active'].value == "yeast":	r.activeYeast = "active "
		if export or form['active'].value == "misc":	r.activeMisc = "active "
		if export or form['active'].value == "calclog":	r.activeCalclog="active"
		if export:	r.colWidth=10
	r.recipeName=form['recipeName'].value
	r.export=export
	r.editable=True
	r.localUser=theme.localUser
	r.displayRecipe()


	print """
					<!-- begin spinner -->
					<div id='spinner' style='height: 0px; visibility: hidden; margin: 12px;'>
						<div id='box'>
							Please Wait, <span id='spinnerText'>recalculating</span> recipe<br>
							<img src="images/ajax_progress2.gif">
						</div>
					</div>
					<!-- end spinner -->
	"""


	if not export and theme.localUser:
		print "</div>"
		print """
		<a href="?recipeName=%s&export=True"><i class="icon-link-2"></i></a>
		""" %(form['recipeName'].value)

	theme.presentFoot()

	cursor=db.query("select recipeName,calculationOutstanding from gRecipes WHERE recipeName = '%s';" %(form['recipeName'].value))
	result=db.use_result()
	row=result.fetch_row()
	((recipe,calculationOutstanding),)=row

	if calculationOutstanding == "1":


	#if form.has_key("outstandingSig"):
		print """<script language=Javascript>
	document.getElementById('calculateOutstanding').style.visibility="visible";
	//recalculate();
	</script>"""

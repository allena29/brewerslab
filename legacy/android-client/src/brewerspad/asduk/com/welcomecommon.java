package brewerspad.asduk.com;

/* 

 Copyright (c) 2011 Adam Allen, 
 All Rights Reserved, including the right to allow you 
 to use the software in accordance with the GPL Licence (v3)


 This file is part of brewerspad.

 Brewerspad is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Brewerspad is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Brewerspad.  If not, see <http://www.gnu.org/licenses/>.

 $Revision: 1.13 $ $Date: 2011-10-31 23:37:10 $ $Author: codemonkey $


 */

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import brewerspad.asduk.com.welcome.BrewlogItemAdapter;
import brewerspad.asduk.com.welcome.BrewlogItemAdapterImg;
import android.app.Activity;

import android.app.ProgressDialog;
import android.content.Intent;

import android.util.Log;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class welcomecommon extends Activity {

	public ProgressDialog pleasewait;
	public Boolean pleasewaitActive = false;
	public Boolean tabletDevice;
	public Boolean debugXMLRPC = true;

	public String hostnameIp = "127.0.0.2";
	public Integer portNum = 12702;

	public String exception = "";
	public String exceptionTitle = "";
	public String exceptionResponse = "";

	// globals
	public String recipeName = "";

	// Listviews
	ArrayList<String> recipeListData;
	ArrayList<brewlogitem> brewlogsListData = new ArrayList<brewlogitem>();;;
	ArrayList<brewlogitem> brewlogActivityListData = new ArrayList<brewlogitem>();;;
	public ArrayList<CharSequence> processArrayData = new ArrayList<CharSequence>();

	// Try this it might not work
	ArrayAdapter brewlogsAdapter;

	// BrewlogItemAdapter brewlogsAdapter2; // this only works because of the
	// import we have import brewerspad.asduk.com.welcome.BrewlogItemAdapter;
	BrewlogItemAdapterImg brewlogsAdapter2; // this only works because of the
											// import we have import
											// brewerspad.asduk.com.welcome.BrewlogItemAdapter;
	// BrewlogItemAdapter brewlogActivityAdapter; // this only works because of
	// the import we have import
	// brewerspad.asduk.com.welcome.BrewlogItemAdapter;
	BrewlogItemAdapterImg brewlogActivityAdapterImg; // this only works because
														// of the import we have
														// import
														// brewerspad.asduk.com.welcome.BrewlogItemAdapter;
	ArrayAdapter recipesAdapter;

	// New Cloud
	public String cloudKey = "";
	public String cloudUser = "";
	public String cloudDevice = "";

	// Boss
	public String boss = "<na>";
	public welcome welcomeBoss;
	public welcome2 welcome2Boss;
	public welcome3 welcome3Boss;
	public outofstockwarning outofstockBoss;
	
	public void nextStepFromBrewlog(brewlogitem bi) {

		Log.i("brewerspad", "___________________________________________RECIPE"
				+ bi.BrewlogName);
		if (bi.BrewlogName.equals("<View Recipe>")) {
			if(tabletDevice){
				welcomeBoss.viewRecipe(recipeName);
			}else{
				welcome2Boss.viewRecipe(recipeName);
			}
		} else if (bi.BrewlogName.equals("<New Brewlog>")) {
			welcomeBoss.newBrewlog(recipeName);			
		} else if (bi.BrewlogName.equals("<Clone Recipe>")) {
			welcomeBoss.cloneRecipe(recipeName); // was bi.RecipeName
		} else {

			fluffycloud cloudtask = new fluffycloud();
			cloudtask.themWelcome = this;
			cloudtask.themId = "welcome";
			cloudtask.hostnameIp = hostnameIp;
			cloudtask.cloudKey = cloudKey;
			cloudtask.cloudDevice = cloudDevice;
			cloudtask.cloudUser = cloudUser;
			cloudtask.portNum = portNum + 1;
			cloudtask.execute("listActivitiesFromBrewlog", bi.ProcessName,bi.RecipeName, bi.BrewlogName);

		}

	}

	public void nextStepFromRecipe(String recipeName) {

		fluffycloud cloudtask = new fluffycloud();
		cloudtask.themWelcome = this;
		cloudtask.themId = "welcome";
		cloudtask.hostnameIp = hostnameIp;
		cloudtask.cloudKey = cloudKey;
		cloudtask.cloudDevice = cloudDevice;
		cloudtask.cloudUser = cloudUser;
		cloudtask.portNum = portNum + 1;
		cloudtask.execute("listBrewlogsByRecipe", recipeName);

	}

	public Boolean decodeXmlrpc(String jsonresponse, String taskOperation,
			String exception) {
		/*
		 * 
		 * This will do nothing more than call out to another operation to do
		 * something. and if we have an exception coming in it will throw the
		 * exception early on.
		 */
		Log.i("brewerspad", "welcomecommon.decodeXmlrpc");
		Log.i("brewerspad", "jsonresponse " + jsonresponse);
		Log.i("brewerspad", "taskOperation " + taskOperation);
		Log.i("brewerspad", "boss " + boss);

		if (exception.length() > 0) {
			Log.i("brewerspad", "storescommon.decodeXmlrpc - exception");
			findErrorPresenter(jsonresponse, taskOperation, exception);
			// if we have an exception in retreiving the xmlrpc call then we
			// need an exception
			return false;
		}

		if (taskOperation.equals("cloneRecipe")) {
			Log.i("brewerspad", "back from cloneRecipe");

			fluffycloud cloudtask = new fluffycloud();
			cloudtask.themWelcome = this;
			cloudtask.themId = "welcome";
			cloudtask.hostnameIp = hostnameIp;
			cloudtask.cloudKey = cloudKey;
			cloudtask.cloudDevice = cloudDevice;
			cloudtask.cloudUser = cloudUser;
			cloudtask.portNum = portNum + 1;
			cloudtask.execute("listRecipes");

		}

		if (taskOperation.equals("createBrewlogWrapper")){
			createBrewlogConfirmation(jsonresponse);
		}
		
		
		if (taskOperation.equals("listRecipes")) {

			try {
				Log.i("brewerspad", "here after listRecipes 0");
				JSONObject jObject = new JSONObject(jsonresponse);
				// Toast.makeText(this,"jsonResponse"+jsonResponse,Toast.LENGTH_LONG).show();
				recipeListData.clear();
				Log.i("brewerspad", "here after listRecipes 1");
				JSONArray RECIPES = jObject.getJSONArray("result");
				for (int i = 0; i < RECIPES.length(); ++i) {
					Log.i("brewerspad", "here after listRecipes2");
					String recipe = RECIPES.getString(i);
					recipeListData.add(recipe);
				}
				Log.i("brewerspad", "here after listRecipes3");

			} catch (JSONException e) {
				Log.i("brewerspad", "THIS IS A NEW ERROR");
				exception = "JSONException: " + e.getMessage();
				findErrorPresenter(jsonresponse, taskOperation, exception);
			} catch (Exception e) {
				Log.i("brewerspad", "THIS IS A NEW ERROR ASWELL");
				exception = "Exception: " + e.getMessage();
				findErrorPresenter(jsonresponse, taskOperation, exception);
			}

			recipesAdapter.notifyDataSetChanged();

			welcomeBoss.kickGui();

		}

		if (taskOperation.equals("listBrewlogsByRecipe")) {
			Log.i("brewerspad", "tablet device seems to be missing");
			Log.i("brewerspad", "tablet device seems to be missing"
					+ tabletDevice);
			if (tabletDevice) {// &&
								// getWindowManager().getDefaultDisplay().getWidth()
								// >
								// getWindowManager().getDefaultDisplay().getHeight()){
				// we are on a tablet in horizontal mode
				listBrewlogsFromRecipe(jsonresponse);

			} else {
				// we are on a phone or a tablet in vertical mode
				
				welcomeBoss.goToWelcome2(jsonresponse,recipeName);

			}

		}

		if (taskOperation.equals("listActivitiesFromBrewlog")) {

			if (tabletDevice) {// &&
								// getWindowManager().getDefaultDisplay().getWidth()
								// >
								// getWindowManager().getDefaultDisplay().getHeight()){
				// we are on a tablet in horizontal mode
				listActivitiesFromBrewlog(jsonresponse);
			} else {
				welcome2Boss.goToWelcome3(jsonresponse);
			}
		}

		return true;

	}
	
	
	
	public void createBrewlogConfirmation(String jsonresponse){
		
		
		/*
		 * {u'cost': {u'ingredients': {u'__total__': 0}, u'hops': {u'Willamette': 1.2283195749504061, u'Green Bullet': 1.7640000000000002, u'Hallertau Hersbrucker': 0.3358686337755017, u'__total__': 6.0011882087259085}, u'consumables': {u'__total__': 0.60685, u'500ml glass bottle': 0.00075, u'Crown Caps': 0.3781, u'priming sugar': 0.228}, u'misc': {u'__total__': 0.0, u'Bottled Water (2L)': 0.0}, u'fermentables': {u'Crystal 80': 0.79, u'CaraGold': 2.0000000000000002e-07, u'Honey': 1.9249999999999998, u'__total__': 8.8646502, u'Torrified Wheat': 0.34965000000000007, u'Maris Otter': 5.8}, u'yeast': {u'__total__': 2.4375, u'Safale S04': 2.4375}}, 
		 *  u'stock_status': False, 
		 *  u'stock': {u'__qty_available__': {u'Crystal 80': 500.0, u'CaraGold': 2000.0, u'Hallertau Hersbrucker': 100.0, u'Safale S04': 4.0, u'Willamette': 100.0, u'Honey': 680.0, u'500ml glass bottle': 15.0, u'Torrified Wheat': 1000.0, u'Bottled Water (2L)': 25.196477187083612, u'Maris Otter': 25000.0, u'Green Bullet': 100.0}, u'__pcnt_left__': {u'Crystal 80': 0.0, u'CaraGold': 0.99999995, u'Hallertau Hersbrucker': 0.8400625553449992, u'Safale S04': 0.75, u'Willamette': 0.48820017710399743, u'Honey': 0.0, u'500ml glass bottle': 0, u'Torrified Wheat': 0.667, u'Bottled Water (2L)': 0, u'Maris Otter': 0.8, u'Green Bullet': 0.51}, u'ingredients': {u'__total__': 0}, u'hops': {u'Willamette': 51.179982289600254, u'Green Bullet': 49.0, u'Hallertau Hersbrucker': 15.993744465500079, u'__total__': 215.17372675510032}, u'__qty_required__': {u'Crystal 80': 500.0, u'CaraGold': 0.0001, u'Hallertau Hersbrucker': 15.993744465500079, u'Safale S04': 1.0, u'Willamette': 51.179982289600254, u'Honey': 680.0, u'500ml glass bottle': 54.0, u'Torrified Wheat': 333.0, u'Bottled Water (2L)': 44.33151376298643, u'Maris Otter': 5000.0, u'Green Bullet': 49.0}, u'consumables': {u'__total__': 0}, u'misc': {u'__total__': 44.33151376298643, u'Bottled Water (2L)': 44.33151376298643}, u'__stockrequirements__': [[u'Bottled Water (2L)', 25.196477187083612, 44.33151376298643], [u'500ml glass bottle', 15.0, 54.0]], u'fermentables': {u'Crystal 80': 500.0, u'CaraGold': 0.0001, u'Honey': 680.0, u'__total__': 6513.0001, u'Torrified Wheat': 333.0, u'Maris Otter': 5000.0}, u'__out_of_stock__': [u'Bottled Water (2L)', u'500ml glass bottle'], u'yeast': {u'__total__': 1.0, u'Safale S04': 1.0}}}
		 */
		Log.i("brewerspad","createBrewlogConfirmation - in for decoding xmlrpc json response");
		try {
			Log.i("brewerspad","createBrewlogConfirmation - ");
			JSONObject jObject = new JSONObject(jsonresponse);
			Log.i("brewerspad","createBrewlogConfirmation - jObject");
			
			// create brewlog with stock and createblock without stock look different :-(
			
			Boolean outOfStockStatus=true;
			Boolean outOfDateStockStatus=true;
			Boolean stockstatus=false;
			try{
				JSONObject jResult = jObject.getJSONObject("result");
							
				//Log.i("brewerspad","createBrewlogConfirmation - jResult");
				outOfStockStatus = jResult.getBoolean("out_of_stock");
				
				outOfDateStockStatus = jResult.getBoolean("out_of_date_stock");
			}catch (Exception e){
				Log.i("brewerspad","looks like we are getting stockstatus from jObject");
			}
			
			try{
				
				stockstatus = jObject.getBoolean("stock_status");
				
				
			}catch (Exception e){
				Log.i("brewerspad","looks like we didn't get stockstatus from jObject");
			}
			
						
			
			
			
			if( outOfDateStockStatus == false || outOfStockStatus == false || stockstatus == false){
				// we are out of stock :-(
				if(tabletDevice){
					Log.i("brewerspad","we don't have enough stock for the brewlog");
					welcomeBoss.outOfStockWarning(jsonresponse);
				}
				
			}else{
				Log.i("brewerspad","we do have enough stock for the brewlog");
				Log.i("brewrespad",jsonresponse);
				Log.i("brewrespad","jsonResponse");
				if (tabletDevice) {// &&
									// getWindowManager().getDefaultDisplay().getWidth()
									// >
									// getWindowManager().getDefaultDisplay().getHeight()){
					// we are on a tablet in horizontal mode
					listBrewlogsFromRecipe(jsonresponse);

					
					
				} else {
					// we are on a phone or a tablet in vertical mode
					welcomeBoss.goToWelcome2(jsonresponse,recipeName);

				}

			}
		
		
		
		
		}catch (JSONException e){
			Log.i("brewerspad","createBrewlogConfirmation - exception in JSON Decoding");
				
		}
		

		
		
	}

	public boolean listActivitiesFromBrewlog(String jsonresponse) {

		brewlogActivityListData.clear();

		try {
			JSONObject jObject = new JSONObject(jsonresponse);
			JSONObject jResult = jObject.getJSONObject("result");

			JSONArray BREWLOGACTIVITIES = jResult.getJSONArray("activities");
			JSONArray COMPLETEACTIVITIES = jResult
					.getJSONArray("completeactivities");
			for (int i = 0; i < BREWLOGACTIVITIES.length(); ++i) {

				brewlogitem bi = new brewlogitem();
				bi.BrewlogName = jResult.getString("brewlog");
				bi.ProcessName = jResult.getString("process");
				bi.RecipeName = jResult.getString("recipe");
				bi.ActivityName = BREWLOGACTIVITIES.getString(i);
				bi.Display = bi.ActivityName;
				bi.Complete = COMPLETEACTIVITIES.getBoolean(i);

				brewlogActivityListData.add(bi);

			}

		} catch (JSONException e) {
			findErrorPresenter(jsonresponse, "listActivitiesFromBrewlog",
					exception);
		} catch (Exception e) {
			findErrorPresenter(jsonresponse, "listActivitiesFromBrewlog",
					exception);
		}

		if (tabletDevice) {// &&
							// getWindowManager().getDefaultDisplay().getWidth()
							// >
							// getWindowManager().getDefaultDisplay().getHeight()){
			brewlogActivityAdapterImg.notifyDataSetChanged();

			welcomeBoss.listActivitiesPostUpdate();

			welcomeBoss.kickGui();
		} else {
			welcome3Boss.brewlogAdapter.notifyDataSetChanged();
		}

		return true;
	}

	public boolean listBrewlogsFromRecipe(String jsonresponse) {
		
		
		Log.i("brewerspad","got into listBrewlogsFromRecipe");
		Log.i("brewerspad", jsonresponse);
		
		processArrayData.clear();
		
		Log.i("brewerspad"," cleared data");
		try {
			JSONObject jObject = new JSONObject(jsonresponse);
			Log.i("brewerspad"," got jObject");			
			JSONArray jList = jObject.getJSONArray("result2");
			Log.i("brewerspad","  got jList");
			for (int i = 0; i <jList.length(); ++i){
				String pr = jList.getString(i);
				Log.i("brewerspad","i"+i+" _ "+ pr );
				processArrayData.add(pr);


			}
	
			
		} catch (JSONException e) {			
			Log.i("brewerspad","got exception when doing some json decoding");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i("brewerspad","got into decodeRecipeStatsAndIngredients -ast basic decode xmlrpc");
		
		

		
		// for phone/tablet we might want some common decoding.	
		String RecipeName = "";
		Log.i("brewerspad",
				"INTO listBrewlogsFromRecipe ... can start making a difference");
		try {
			JSONObject jObject = new JSONObject(jsonresponse);
			JSONArray BREWLOGS = jObject.getJSONArray("result");
			for (int i = 0; i < BREWLOGS.length(); ++i) {
				Log.i("brewerspad", "here after listBrewlogs2");
				JSONObject BREWLOG = BREWLOGS.getJSONObject(i);
				RecipeName = BREWLOG.getString("recipe");

			}
		} catch (JSONException e) {
			findErrorPresenter(jsonresponse, "listBrewlogsFromRecipe",
					exception);
		} catch (Exception e) {
			findErrorPresenter(jsonresponse, "listBrewlogsFromReipce",
					exception);
		}
		
		

		brewlogsListData.clear();

		brewlogitem bi = new brewlogitem();
		bi.BrewlogName = "<View Recipe>";
		bi.Display = bi.BrewlogName;
		bi.GUI = 1;
		brewlogsListData.add(bi);
		bi = new brewlogitem();
		bi.BrewlogName = "<New Brewlog>";
		bi.Display = bi.BrewlogName;
		bi.GUI = 1;
		brewlogsListData.add(bi);
		bi = new brewlogitem();
		bi.BrewlogName = "<Clone Recipe>";
		bi.Display = bi.BrewlogName;
		bi.RecipeName = RecipeName;
		bi.GUI = 1;
		brewlogsListData.add(bi);

		try {
			JSONObject jObject = new JSONObject(jsonresponse);
			Log.i("brewerspad", "here after listBrewlogs");
			Log.i("brewerspad", "here after listBrewlogsA");
			// Toast.makeText(this,"jsonResponse"+jsonResponse,Toast.LENGTH_LONG).show();

			Log.i("brewerspad", "here after listBrewlogs");
			JSONArray BREWLOGS = jObject.getJSONArray("result");
			for (int i = 0; i < BREWLOGS.length(); ++i) {
				Log.i("brewerspad", "here after listBrewlogs2");
				JSONObject BREWLOG = BREWLOGS.getJSONObject(i);

				bi = new brewlogitem();
				bi.BrewlogName = BREWLOG.getString("name");
				bi.Display = bi.BrewlogName;
				bi.ProcessName = BREWLOG.getString("process");
				bi.RecipeName = BREWLOG.getString("recipe");
				Log.i("brewerspad", "complete new 2012-10-27");
				bi.Complete = BREWLOG.getBoolean("complete");
				// String brewlog = BREWLOGS.getString(bi);

				brewlogsListData.add(bi);

			}
			Log.i("brewerspad", "here after listBrewlogs");

		} catch (JSONException e) {
			findErrorPresenter(jsonresponse, "listBrewlogsFromRecipe",
					exception);
		} catch (Exception e) {
			findErrorPresenter(jsonresponse, "listBrewlogsFromRecipe",
					exception);
		}

		if (tabletDevice) {
			brewlogsAdapter2.notifyDataSetChanged();
			// even though dataset changes can be done here setting any other
			// activity
			// bits like text views/visibility of linear layouts can't be done
			// from here.
			welcomeBoss.listBrewlogsPostUpdate(RecipeName);

			// clear the 3rd activity
			brewlogActivityListData.clear();
			brewlogActivityAdapterImg.notifyDataSetChanged();

			welcomeBoss.kickGui();

		} else {
			welcome2Boss.brewlogAdapter.notifyDataSetChanged();
		}

		
		
		
		
		//
		return true;

	}

	public void findErrorPresenter(String jsonresponse, String taskOperation,
			String exception) {
		Log.i("brewerspad", "findErrrorPresenter");

		/*
		 * If we have an exception set then we need to find the place to get our
		 * execption from
		 */

		if (boss.equals("welcome")) {
			welcomeBoss.presentError(taskOperation, exception, jsonresponse);
			welcomeBoss.APIerror = true;
		}

		if (boss.equals("welcome2")) {
			welcome2Boss.presentError(taskOperation, exception, jsonresponse);
			welcome2Boss.APIerror = true;
		}

		if (boss.equals("welcome2")) {
			welcome3Boss.presentError(taskOperation, exception, jsonresponse);
			welcome3Boss.APIerror = true;
		}

	}

	public class brewlogitem {
		public String Display = "";
		public String ActivityName = "";
		public String RecipeName = "";
		public String ProcessName = "";
		public String BrewlogName = "";
		public Boolean Complete = false;
		public Integer GUI = 0;

	}

}

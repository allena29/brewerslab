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
   
$Revision: 1.17 $ $Date: 2011-10-26 12:06:36 $ $Author: codemonkey $


 */


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;



public class recipecommon extends Activity {

	public Boolean pleasewaitActive = false;
	public ProgressDialog pleasewait;
	
	public Boolean debugXMLRPC = true;
	public Boolean tabletDevice=true;
	public Boolean page2=false;
	public Boolean page3=false;
	public String exception = "";
	public String exceptionTitle = "";
	public String exceptionResponse = "";

	public String cloudHost="";
	public Integer cloudPort=0;
	public String cloudKey="";
	public String cloudDevice="";
	public String cloudUser="";
	
	//public ArrayList<String> listViewIngredients;
	//public ArrayList<String> listViewRecipeItems;
	public ArrayList<recipeitem> listViewIngredients;
	public ArrayList<recipeitem> listViewRecipeItems;

	public ArrayList<CharSequence> processArrayData = new ArrayList<CharSequence>();	
	public ArrayList<CharSequence> effiencyArrayData = new ArrayList<CharSequence>();
	
	public String boss="n/a";
	public recipe recipeBoss;
	public recipe2 recipeBoss2;
	
	
	public Double estimatedABV = 0.00;
	public Double estimatedFG=0.00;
	public Double estimatedOG=0.00;
	public Integer estimatedEBC=0;
	public Integer estimatedIBU=0;
	public Double thisEstimatedABV = 0.00;
	public Double thisEstimatedFG=0.00;
	public Double thisEstimatedOG=0.00;
	public Integer thisEstimatedEBC=0;
	public Integer thisEstimatedIBU=0;

	public Double valuePostboilTopup=0.00;
	public Integer mashEfficiency=0;
	public Double batchsize=0.00;
	public Double thisbatchsize=0.00;
	public Double spargeWater=0.00;
	public Double mashWater=0.00;
	public Double boilVolume=0.00;
	public Double totalWater=0.00;
	public Double totalGrain=0.00;
	public Double totalAdjuncts=0.00;
	public Double totalHops=0.00;
	public String process="";
	
	
	
    	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		Log.i("brewerspad","does this ever get called onCreate in recipecommon");
	}
	

	public void decodeProcessList(String jsonResponse){
		Log.i("brewerspad","got into decodeProcessList");

		processArrayData.clear();
		
		Log.i("brewerspad"," cleared data");
		try {
			JSONObject jObject = new JSONObject(jsonResponse);
			Log.i("brewerspad"," got jObject");			
			JSONArray jList = jObject.getJSONArray("result");
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
		
		
		
		recipeBoss.kickGui();
		Log.i("brewerspad"," kickGui called on Boss, about to call changeProcess");
		
		recipeBoss.changeProcess();
		Log.i("brewerspad"," called changeProcess");
		
	}
	
	
	
	public void decodeRecipeStatsAndIngredients(String jsonResponse, Boolean recalculateRequired){
		
		Log.i("brewerspad","got i to decodeRecipeStatsAndIngredients");
		Log.i("brewerspad",jsonResponse);
		
		try {
			JSONObject jObject = new JSONObject(jsonResponse);			
			JSONObject jStats = jObject.getJSONObject("stats");
			if(jStats.getBoolean("calculationOutstanding")){
				recalculateRequired=true;	//override whatever flag we get because a recalcuation is needed
			}
			estimatedABV = jStats.getDouble("estimated_abv");			
			estimatedFG = jStats.getDouble("estimated_fg");
			estimatedOG = jStats.getDouble("estimated_og");
			estimatedEBC = jStats.getInt("estimated_ebc");
			estimatedIBU = jStats.getInt("estimated_ibu");
			
			thisEstimatedABV = jStats.getDouble("this_estimated_abv");			
			thisEstimatedFG = jStats.getDouble("this_estimated_fg");
			thisEstimatedOG = jStats.getDouble("this_estimated_og");
			valuePostboilTopup=jStats.getDouble("postBoilTopup");
			//thisEstimatedEBC = jStats.getInt("this_estimated_ebc");
			thisEstimatedIBU = jStats.getInt("this_estimated_ibu");
			
			
			mashEfficiency=jStats.getInt("mash_efficiency");
			batchsize =jStats.getDouble("batch_size_required");
			thisbatchsize =jStats.getDouble("this_batch_size");
			process=jStats.getString("process");
			
			if(tabletDevice || page2){
				listViewRecipeItems.clear();
			}
			
			JSONArray recipeitems;
			// get fermentables
			recipeitem r = new recipeitem();
			r.header=true;
			r.StockName="Fermentables";
			
			if(tabletDevice || page2){
				listViewRecipeItems.add(r);
			}
			
			recipeitems = jObject.getJSONArray("fermentableitems");
			
			
			
			for (int i = 0; i <recipeitems.length(); ++i){
				JSONObject ri = recipeitems.getJSONObject(i);
				r = new recipeitem();
				r.StockName=ri.getString("name");
				r.StockQty=ri.getString("qty");
				r.OriginalQty = ri.getString("originalqty");
				r.StockUnit=ri.getString("unit");
				r.Extraline=ri.getString("gravity");
				r.StockCategory="fermentables";
				
				if(tabletDevice || page2){
					listViewRecipeItems.add(r);
				}
			}
	

			// get hops
			r = new recipeitem();
			r.header=true;
			r.StockName="Hops";
			if(tabletDevice || page2){
				listViewRecipeItems.add(r);
			}
			
			recipeitems = jObject.getJSONArray("hopitems");
			//listViewRecipe2Items.clear();
			
			
			for (int i = 0; i <recipeitems.length(); ++i){
				JSONObject ri = recipeitems.getJSONObject(i);
				Log.i("brewerspad","ri.getString(name)"+ri.getString("name"));
				 r = new recipeitem();
				r.StockName=ri.getString("name");
				//+ " "+ri.getString("hopaddat");
				r.HopAddAt = ri.getString("hopaddat");
				r.Extraline=ri.getString("ibu");
				r.StockQty=ri.getString("qty");
				r.OriginalQty = ri.getString("originalqty");
				r.StockUnit=ri.getString("unit");
				r.StockCategory="hops";
				if(tabletDevice || page2){
					listViewRecipeItems.add(r);
				}

			}
	

			// get yeast
			r = new recipeitem();
			r.header=true;
			r.StockName="Yeast";
			if(tabletDevice || page2){
				listViewRecipeItems.add(r);
			}
			recipeitems = jObject.getJSONArray("yeastitems");
			//listViewRecipe3Items.clear();
			
			
			for (int i = 0; i <recipeitems.length(); ++i){
				JSONObject ri = recipeitems.getJSONObject(i);
				Log.i("brewerspad","ri.getStri]ng(name)"+ri.getString("name"));
				r = new recipeitem();
				r.StockName=ri.getString("name");
				r.StockQty=ri.getString("qty");
				r.OriginalQty = ri.getString("originalqty");
				r.StockUnit=ri.getString("unit");
				r.StockCategory="yeast";
				if(tabletDevice || page2){
					listViewRecipeItems.add(r);
				}

			}
			
			Log.i("brewerspad","yeast is might what be causing teh execption");
			
			
			
			// get consumables
			r = new recipeitem();
			r.header=true;
			r.StockName="Consumables";
			if(tabletDevice || page2){
				listViewRecipeItems.add(r);
			}
			recipeitems = jObject.getJSONArray("miscitems");
			//listViewRecipe4Items.clear();
			
			
			for (int i = 0; i <recipeitems.length(); ++i){
				JSONObject ri = recipeitems.getJSONObject(i);
				Log.i("brewerspad","ri.getStri]ng(name)"+ri.getString("name"));
				r = new recipeitem();
				r.StockName=ri.getString("name");
				r.StockQty=ri.getString("qty");
				r.OriginalQty = ri.getString("originalqty");
				r.StockUnit=ri.getString("unit");
				r.StockCategory="misc";
				if(tabletDevice || page2){
					listViewRecipeItems.add(r);
				}
			}
	
			
	
			
			// get other
			r = new recipeitem();
			r.header=true;
			r.StockName="Other";
			if(tabletDevice || page2){
				listViewRecipeItems.add(r);
			}
			recipeitems = jObject.getJSONArray("otheritems");
			//listViewRecipe5Items.clear();
			
			
			for (int i = 0; i <recipeitems.length(); ++i){
				JSONObject ri = recipeitems.getJSONObject(i);
				r = new recipeitem();
				r.StockName=ri.getString("name");
				r.StockQty=ri.getString("qty");
				r.OriginalQty = ri.getString("originalqty");
				r.StockCategory="other";
				r.StockUnit=ri.getString("unit");
				if(tabletDevice || page2){
					listViewRecipeItems.add(r);
				}

			}
	
	
			
			
			
			JSONArray ingredients= jObject.getJSONArray("ingredients");
			if(tabletDevice || page3){
				listViewIngredients.clear();
			}
			
			
			for (int i = 0; i <ingredients.length(); ++i){
				JSONObject in = ingredients.getJSONObject(i);
				recipeitem I = new recipeitem();
				I.StockName=in.getString("name");
				if(tabletDevice || page3){
					listViewIngredients.add(I);
				}
			}
	
			
			// do these later			
			spargeWater=jStats.getDouble("spargeWater");
			mashWater=jStats.getDouble("mashWater");
			boilVolume=jStats.getDouble("boilVolume");
			totalWater=jStats.getDouble("totalWater");
			totalGrain=jStats.getDouble("totalGrain");
			totalAdjuncts=jStats.getDouble("totalAdjuncts");
			totalHops=jStats.getDouble("totalHops");
            


			
		} catch (JSONException e) {			
			Log.i("brewerspad","got exception when doing some json decoding -decodeRecipeStatsAndIngredients");
			e.printStackTrace();
		}
		Log.i("brewerspad","got through decodeRecipeStatsAndIngredients xmlrpc conversion- triggering display");
		
		if(boss.equals("recipe")){
			// tablet refresh
			recipeBoss.onRecipeStatsAndIngredientsPostUpdate(recalculateRequired);
			
		}else{
			recipeBoss2.onRecipeStatsAndIngredientsPostUpdate(recalculateRequired);
		}
		
	}


	
	
	public void decodeNewBatchSize(String jsonResponse){
		
		Log.i("brewerspad","got into decodeNewBacthSize");
		Log.i("brewerspad",jsonResponse);
		
		try {
			JSONObject jObject = new JSONObject(jsonResponse);
			JSONObject jStats = jObject.getJSONObject("stats");
			batchsize =jStats.getDouble("batch_size_required");
			
		} catch (JSONException e) {			
			Log.i("brewerspad","got exception when doing some json decoding - decodeNewBatchSize");
			e.printStackTrace();
		}
		Log.i("brewerspad","got trhough decodeNewBatchSize xmlrpc conversion -triggering display");			
		
		recipeBoss.onRecipeStatsAndIngredientsPostUpdate(true);
	}


	
	public void decodeNewTopupVolume(String jsonResponse){

		Log.i("brewerspad","got into decodeNewTopupVolume");
		Log.i("brewerspad",jsonResponse);
		
		try {
			JSONObject jObject = new JSONObject(jsonResponse);
			JSONObject jStats = jObject.getJSONObject("stats");
			//batchsize =jStats.getDouble("batch_size_required");
			valuePostboilTopup = jStats.getDouble("postBoilTopup"); 			
			
		} catch (JSONException e) {			
			Log.i("brewerspad","got exception when doing some json decoding - decodeNewTopupVOlume");
			e.printStackTrace();
		}
		Log.i("brewerspad","got trhough decodeNewTopUpVolume xmlrpc conversion -triggering display");			
		
		recipeBoss.onRecipeStatsAndIngredientsPostUpdate(true);
	}
	
	
	public void decodeNewMashEfficiency(String jsonResponse){
		
		Log.i("brewerspad","got into decodeNewBacthSize");
		Log.i("brewerspad",jsonResponse);
		
		try {
			JSONObject jObject = new JSONObject(jsonResponse);
			JSONObject jStats = jObject.getJSONObject("stats");
			//batchsize =jStats.getDouble("batch_size_required");
			mashEfficiency=jStats.getInt("mash_efficiency");
		} catch (JSONException e) {			
			Log.i("brewerspad","got exception when doing some json decoding - decodeNewMashEfficiency");
			e.printStackTrace();
		}
		Log.i("brewerspad","got trhough decodeNewMashEfficiency xmlrpc conversion -triggering display");			
		
		recipeBoss.onRecipeStatsAndIngredientsPostUpdate(true);
	}
	
	
	
	
	
	public class recipeitem {
	    public Boolean specialAddItem = false;
	    public Boolean specialAddPurchase=false;
		public Integer stepId = 0;
	    public String StockName = "";
	    public String StockCategory = "";
	    public String StockQty = "0";
	    public String OriginalQty = "0";
	    public String StockUnit = "";
   	    public Double StockCost = 0.00;
   	    public String StockDescription="";

   	    public String HopAddAt = "0.0";
   	    public Double HopAlpha = 0.00;
   	    // overloaded to deal with purchases in addition to stock
   	    public String Supplier="";
   	    public String PurchaseDate="";
   	    public String PurchaseBestBefore="";
   	    public String PurchaseStockTag="";
   	    public String PurchaseSupplier="";

   	    
   	    public String Extraline="";
   	    public Boolean header=false;
	}

	

	
	public void decodeXmlrpc(String jsonresponse,String taskOperation, String exception){
		/*
		 * 
		 * This will do nothing more than call out to another operation to do something.
		 * and if we have an exception coming in it will throw the exception early on.
		 * 
		 */
		Log.i("brewerspad","recipecommon.decodeXmlrpc");
		Log.i("brewerspad","jsonresponse "+jsonresponse);
		Log.i("brewerspad","taskOperation "+taskOperation);
		Log.i("brewerspad","boss "+boss);

		

		
		if(exception.length() > 0){
			Log.i("brewerspad","recipecommon.decodeXmlrpc - exception (ABC)");
			findErrorPresenter(jsonresponse, taskOperation, exception);
			// if we have an exception in retreiving the xmlrpc call  then we need an exception
			return;
		}
		
		recipeBoss.kickGui();
		
		if(taskOperation.equals("viewRecipe")){
			decodeRecipeStatsAndIngredients( jsonresponse,false );
			
		}
		
		if(taskOperation.equals("calculateRecipeWrapper")){
			decodeRecipeStatsAndIngredients( jsonresponse,false );
		}
		
		if(taskOperation.equals("fixRecipe")){
			decodeRecipeStatsAndIngredients( jsonresponse,false );
		}
		
		if(taskOperation.equals("addItemToRecipe")){
			decodeRecipeStatsAndIngredients( jsonresponse,true	 );
		}
		
		if(taskOperation.equals("changeItemInRecipe")){
			decodeRecipeStatsAndIngredients( jsonresponse,true );
		}
		
		if(taskOperation.equals("changeProcess")){
			decodeRecipeStatsAndIngredients( jsonresponse,false );
		}

		if(taskOperation.equals("setMashEfficiency")){
			decodeNewMashEfficiency( jsonresponse );
		}
		
		if(taskOperation.equals("setTopupVolume")){
			decodeNewTopupVolume( jsonresponse );
		}

		
		if(taskOperation.equals("listProcess")){
			decodeProcessList( jsonresponse );
		}
	
		if(taskOperation.equals("setBatchSize")){
			decodeNewBatchSize( jsonresponse);
		}
		
//		if(taskOperation.equals("addNewPurchase")){
	
		
	}
		
		
	public void findErrorPresenter(String jsonresponse, String taskOperation, String exception){
		Log.i("brewerspad","findErrrorPresenter");

		/*
		 * If we have an exception set then we need to find the place to get our execption from
		 */
				
		//if(boss.equals("recipe")){
			recipeBoss.presentError(taskOperation, exception, jsonresponse);
						
		//}
		


	}

	

	
}









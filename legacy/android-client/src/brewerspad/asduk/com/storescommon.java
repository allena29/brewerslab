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



public class storescommon extends Activity {

	public Boolean pleasewaitActive = false;
	public ProgressDialog pleasewait;
	public Boolean tabletDevice;
	public Boolean debugXMLRPC = true;
	
	public String exception = "";
	public String exceptionTitle = "";
	public String exceptionResponse = "";

	public String cloudKey="";
	public String cloudDevice="";
	public String cloudUser="";
	
	public ArrayList<String> listViewCategories;
	//public ArrayList<String> listViewStockItems;
	public ArrayList<stockitem> listViewStockItems;
	public ArrayList<stockitem> listViewPurchases;	

	public ArrayList<CharSequence> supplierArrayData;
	public ArrayList<CharSequence> itemsArrayData;
	
	public String storeTitle;

	public stockitem StockItem = new stockitem();
	

	
	public String boss = "<na>";
	public storepurchase storepurchaseBoss;
	public stores3 stores3Boss;
	public stores2 stores2Boss;
	public stores storesBoss;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("brewerspad","<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");		
		Log.i("brewerspad","does this ever get called onCreate in storescommon");
	}
	
	
	
	public Boolean updateStoreItems(String jsonResponse){
			/*
			 * Both Phone/Tablet will call this at the same time
			 */

			// this updates the 1st third 
			
			Log.i("brewerspad", "storescommon.updateStoreItems");

			
						// on a tablet we need to build the list view for substeps because it is visible			
			listViewStockItems.clear();


			try {

				JSONObject jObject = new JSONObject(jsonResponse);
				Log.i("brewerspad","got hereA");
				
				Log.i("brewerspad","jsonResponseIn\n"+jsonResponse);
				JSONObject jResult = jObject.getJSONObject("result");
				storeTitle = jResult.getString("category");
				Log.i("brewerspad","got hereB");
				
				
				JSONArray STEPS = jResult.getJSONArray("items");
			
				Log.i("brewerspad","got hereC");
				for (int i = 0; i < STEPS.length(); ++i) {		
					stockitem SI = new stockitem();
					Log.i("brewerspad","got hereD"+i);
					JSONObject stockItemObject = STEPS.getJSONObject(i);
					SI.StockName= stockItemObject.getString("name");
					SI.StockCategory=stockItemObject.getString("category");
					SI.StockQty= stockItemObject.getInt("totalqty");
					SI.StockUnit = stockItemObject.getString("unit");
					
					Log.i("brewerspad","stockUnit is going wrong "+stockItemObject.getString("unit"));
					SI.StockCost = stockItemObject.getDouble("cost");
					listViewStockItems.add(SI);
					
				}
				
				stockitem SI = new stockitem();
				SI.specialAddItem=true;
				SI.StockCategory=jResult.getString("category");
				listViewStockItems.add(SI);
				
				Log.i("brewerspad","got hereZ");
			} catch (JSONException e) {
				Log.i("brewerspad","got here JSONEXECPTION");
				exceptionTitle="storescommon.updateStoreItems()/JSONException";
				exception=e.getMessage();
				exceptionResponse=jsonResponse;
				return false;			
			} catch(Exception e){
				Log.i("brewerspad","got here EXCEPTION");
				Log.i("brewerspad",e.getMessage());
				exceptionTitle="storescommon.updateStoreItems()/Exception";
				exception=e.getMessage();
				exceptionResponse=jsonResponse;
				return false;	
			}		
			return true;
	}


	
	
	public Boolean updateStoreCategories(String jsonResponse){
			/*
			 * Both Phone/Tablet will call this at the same time
			 */

			// this updates the 1st third on the tablet 
			
			Log.i("brewerspad", "storescommon.updateStoreCategories");

			
						// on a tablet we need to build the list view for substeps because it is visible			
			listViewCategories.clear();


			try {
				JSONObject jObject = new JSONObject(jsonResponse);
				Log.i("brewerspad", jObject.toString());
				JSONArray STEPS = jObject.getJSONArray("result");

				for (int i = 0; i < STEPS.length(); ++i) {
					String storeCategory = STEPS.getString(i);				
					listViewCategories.add(storeCategory);
					
				}
			} catch (JSONException e) {
				exceptionTitle="storescommon.updateStoreCAteogires()/JSONException";
				exception=e.getMessage();
				exceptionResponse=jsonResponse;
				return false;			
			} catch(Exception e){
				exceptionTitle="storescommon.updateStoreCAteogires()/Exception";
				exception=e.getMessage();
				exceptionResponse=jsonResponse;
				return false;	
			}		
			return true;
	}

	

	
	
	public Boolean updateStockDetail(String jsonResponse){

			// this updates the 2nd third
			
			Log.i("brewerspad", "storescommon.updateStockDetails");

			
			
			// on a tablet we need to build the list view for substeps because it is visible			
			listViewPurchases.clear();


			try {

				JSONObject jObject = new JSONObject(jsonResponse);
				JSONObject jResult = jObject.getJSONObject("result");
				JSONObject stockItem = jResult.getJSONObject("items");							
				StockItem.StockName = stockItem.getString("name");				
				StockItem.StockCategory = stockItem.getString("category");
				StockItem.StockUnit = stockItem.getString("unit");				
				StockItem.StockQty = stockItem.getInt("totalqty");								
				StockItem.StockDescription = stockItem.getString("description");				
				StockItem.StockCost = stockItem.getDouble("cost");
				if(StockItem.StockCategory.equals("Hops")){
					StockItem.HopAlpha = stockItem.getDouble("hopalpha");
				}


				
				JSONArray PURCHASES = stockItem.getJSONArray("purchases");
				Log.i("brewerspad","got hereG");

				
				for (int i = 0; i < PURCHASES.length(); ++i) {		
					stockitem PI = new stockitem();		//overloded for both purchases and stock itmes
					
					JSONObject purchaseItemObject = PURCHASES.getJSONObject(i);
					//PI.StockName= purchaseItemObject.getString("name");
					//PI.StockCategory=purchaseItemObject.getString("category");
					PI.StockQty= purchaseItemObject.getInt("purchasedQty");
					PI.StockUnit = stockItem.getString("unit");
					PI.StockName =  stockItem.getString("name");
					PI.StockCategory =stockItem.getString("category");
					PI.StockCost = purchaseItemObject.getDouble("purchasedCost");
					PI.PurchaseStockTag = purchaseItemObject.getString("purchasedStockTag");			
					PI.PurchaseSupplier = purchaseItemObject.getString("purchasedSupplier");
					if(StockItem.StockCategory.equals("Hops")){
						PI.HopAlpha = purchaseItemObject.getDouble("hopalpha");
					}
					Long tmpDate = purchaseItemObject.getLong("purchasedBestBefore");
					Date expiry = new Date(tmpDate * 1000);
					SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
					PI.PurchaseBestBefore = formatter.format(expiry);	
					listViewPurchases.add(PI);
										
				}

				stockitem PI = new stockitem();
				PI.specialAddPurchase=true;
				PI.StockCategory=jResult.getString("category");
				stockItem = jResult.getJSONObject("items");											
				PI.StockName=stockItem.getString("name");
				listViewPurchases.add(PI);
				
				
			} catch (JSONException e) {
				Log.i("brewerspad","got here JSONEXECPTION");
				exceptionTitle="storescommon.updateStockDetail()/JSONException";
				exception="Exception:"+e.getMessage();
				exceptionResponse=jsonResponse;
				return false;			
			} catch(Exception e){
				Log.i("brewerspad","got here EXCEPTION");
				Log.i("brewerspad",e.getMessage());
				exceptionTitle="storescommon.updateStockDetail()/Exception";
				exception="Exception"+e.getMessage();
				exceptionResponse=jsonResponse;
				return false;	
			}		
			return true;
	}


	public class stockitem {
	    public Boolean specialAddItem = false;
	    public Boolean specialAddPurchase=false;
		public Integer stepId = 0;
	    public String StockName = "";
	    public String StockCategory = "";
	    public Integer StockQty = 0;
	    public String StockUnit = "";
   	    public Double StockCost = 0.00;
   	    public String StockDescription="";
   	    
   	    public Double HopAlpha = 0.00;
   	    // overloaded to deal with purchases in addition to stock
   	    public String Supplier="";
   	    public String PurchaseDate="";
   	    public String PurchaseBestBefore="";
   	    public String PurchaseStockTag="";
   	    public String PurchaseSupplier="";

	}
	
	
	
	
	
	
	/*
	 * 
	 * 
	 * This serves as a pattern for xmlrpc on android
	 *    the real activity instantiates a "common"
	 *    	and populates the boss (TBD is there an improvement in passing the object to find presentError)
	 *    there is an associated xmlrpc.java class which the main activity gets a task from
	 *    the real activity puts a pointer to this class in to the xmlrpc instance
	 *
	 *    
	 * // set outselves as a boss in storecommon
	 * storeLogic.storepurchaseBoss = this;
	 * storeLogic.boss = "storepurchase";
     *
	 *    		
	 *  Log.i("brewerspad","stoperpurchase[0]");
 	 * 	storexmlrpc task = new storexmlrpc();
	 *	task.them = storeLogic;		// hopefully this works
	 *  task.hostnameIp = hostnameIp;
	 *  task.portNum = portNum;
	 *  task.execute("listIngredientsAndSuppliers", stockCategory);
	 * 
	 * 
	 *  the xmlrpc will then call on this call to use decodeXmlrpc
	 *  
	 *   FindErrorPresenter is used to point back to the main activity which will have access to th egui etc to 
	 *   actually present the error. 
	 *   
	 *   In some cases it might make more sense to call the gui, in which case the method 
	 *   can be called on the boss.
	 *   
	 *    Each real activity should have an APIerrors boolean, although not used right now.
	 */
	


	
	public void populateIngredientsAndSuppliers(String jsonResponse){		
		Log.i("brewerspad","populateIngredientsAndSuppliers");
		String category="";
		try {

			JSONObject jObject = new JSONObject(jsonResponse);
			
			JSONObject jResult = jObject.getJSONObject("result");
			category = jResult.getString("category");
			//JSONObject stockItem = jResult.getJSONObject("items");		
			JSONArray suppliers = jResult.getJSONArray("suppliers");
			Log.i("brewerspad","suppliers"+suppliers);
			
			
			supplierArrayData.clear();
	//		spinnerSuppliers.add("AF");
			for (int i = 0; i <suppliers.length(); ++i){
				String s = suppliers.getString(i);
				supplierArrayData.add(s);
				//Log.i("brewerspad","supplier "+i+" "+s);
			}
			
				
			JSONArray items = jResult.getJSONArray("items");
			
			itemsArrayData.clear();
			for (int i = 0; i<items.length(); ++i){
				String s = items.getString(i);
				itemsArrayData.add(s);
			}
			
		} catch (JSONException e) {
			Log.i("brewerspad","got here JSONEXECPTION in populateIngredientsAndSuppliers");
			exceptionTitle="storescommon.populateIngredientsAndSuppliers()/JSONException";
			exception="Exception:"+e.getMessage();
			exceptionResponse=jsonResponse;
			
		} catch(Exception e){
			Log.i("brewerspad","got here EXCEPTION in populateIngredientsAndSuppliers");
			Log.i("brewerspad",e.getMessage());
			exceptionTitle="storescommon.populateIngredientsAndSuppliers()/Exception";
			exception="Exception"+e.getMessage();
			exceptionResponse=jsonResponse;
	
		}
		
		
		storepurchaseBoss.populateIngredientsAndSuppliersPostUpdate(category);
		
	}
	
	//changeItemQty invokes a method on the boss

	//getFullStockDetails invokes a method on the boss
	
	
	
	public void decodeXmlrpc(String jsonresponse,String taskOperation, String exception){
		/*
		 * 
		 * This will do nothing more than call out to another operation to do something.
		 * and if we have an exception coming in it will throw the exception early on.
		 * 
		 */
		Log.i("brewerspad","storescommon.decodeXmlrpc");
		Log.i("brewerspad","jsonresponse "+jsonresponse);
		Log.i("brewerspad","taskOperation "+taskOperation);
		Log.i("brewerspad","boss "+boss);

		

		
		if(exception.length() > 0){
			Log.i("brewerspad","storescommon.decodeXmlrpc - exception (ABC)");
			findErrorPresenter(jsonresponse, taskOperation, exception);
			// if we have an exception in retreiving the xmlrpc call  then we need an exception
			return;
		}
		
		
		if(taskOperation.equals("addNewPurchase")){
			storepurchaseBoss.purchasePostUpdate();			
		}
		
		
		if(taskOperation.equals("listIngredientsAndSuppliers")){
			populateIngredientsAndSuppliers(jsonresponse);
		}
		
		if(taskOperation.equals("changeItemQty")){
			if(boss.equals("stores3")){		// this is good practcise to protect against new activitieis
				stores3Boss.itemQtyPostUpdate(jsonresponse);
			}else{
				Log.i("brewerspad","can't find boss to execute");
			}
		}

		if(taskOperation.equals("getStockFullDetails")){
			if(boss.equals("stores2")){		// this is good practcise to protect against new activitieis
				stores2Boss.getStockFullDetailPostUpdate(jsonresponse);
			}else if(boss.equals("stores")){
				storesBoss.getStockFullDetailPostUpdate(jsonresponse);
			}else{
				Log.i("brewerspad","can't find boss to execute");
			}
		}
			
	
		if(taskOperation.equals("listStoreItems")){
			if(boss.equals("stores")){				
				storesBoss.upateStoreItemsPostUpdate(jsonresponse);
			}else{
				Log.i("brewerspad","can't find boss to execute");
			}
		}

		
		if(taskOperation.equals("listStoreCategories")){
			if(boss.equals("stores")){				
				storesBoss.updateStoreCategoriespostUpdate(jsonresponse);				
			}else{
				Log.i("brewerspad","can't find boss to execute");
			}
		}
				
		
	}
		
		
	public void findErrorPresenter(String jsonresponse, String taskOperation, String exception){
		Log.i("brewerspad","findErrrorPresenter");

		/*
		 * If we have an exception set then we need to find the place to get our execption from
		 */
				
		if(boss.equals("storepurchase")){
			storepurchaseBoss.presentError(taskOperation, exception, jsonresponse);
			storepurchaseBoss.APIerror=true;
		}else if(boss.equals("stores3")){
			stores3Boss.presentError(taskOperation, exception, jsonresponse);
			stores3Boss.APIerror=true;			
		}else if(boss.equals("stores2")){
			stores2Boss.presentError(taskOperation, exception, jsonresponse);
			stores2Boss.APIerror=true;			
		}else if(boss.equals("stores")){
			storesBoss.presentError(taskOperation, exception, jsonresponse);
			storesBoss.APIerror=true;			
		}
		


	}

	

	
}









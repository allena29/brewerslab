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
   
$Revision: 1.12 $ $Date: 2011-11-05 00:26:05 $ $Author: codemonkey $


 */


import java.text.DecimalFormat;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.method.NumberKeyListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;







public class outofstockwarning extends Activity {
	
	
	welcomecommon welcomeLogic;
	Boolean APIerror;
	String hostnameIp;
	Integer portNum;
	Boolean tabletDevice;

	
	Integer stepid =-1;
	
	
	ArrayList edittexts = new ArrayList<EditText>();
	ArrayList fieldkeys = new ArrayList<String>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		

		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		tabletDevice = shrdPrefs.getBoolean("tabletDevice", true);
		hostnameIp = shrdPrefs.getString("hostname", "127.0.0.1");
		portNum = shrdPrefs.getInt("portNumber", 1027);
		
		


		
		Log.i("brewerspad","about to load content of layout for widgetsfields");

		setContentView(R.layout.outofstockwarning);

		
		welcomeLogic = new welcomecommon();
		// set outselves as a boss in storecommon
		welcomeLogic.outofstockBoss = this;
		welcomeLogic.boss = "widgets";
		welcomeLogic.hostnameIp = hostnameIp;
		welcomeLogic.portNum = portNum;
		// set data on common object
		//brewdayLogic.pleasewait = pleasewait;
		welcomeLogic.tabletDevice = tabletDevice;

		
		Log.i("brewerspad","prePopulate in outofstock warning()");
		populate();
	}

	
	public void populate(){
		
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.outofstockwarningLL);

		/// this will come in from the launcher
		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		String jsonResponse = shrdPrefs.getString("jsonResponseZ", "");

		

		Log.i("brewerspad","out of stock warning - populate()");
		JSONObject jResult = null;
		Boolean outofdatestock=false;
		Boolean outofstock=false;
		try {
			JSONObject jObject = new JSONObject(jsonResponse);				
			jResult = jObject.getJSONObject("result");
			outofdatestock= jResult.getBoolean("out_of_date_stock");
			outofstock = jResult.getBoolean("out_of_stock");
		}catch(JSONException e){
			Log.i("brewerspad","outofstock first pass json exception");
		}
		

		/*
		 * {u'cost': {u'ingredients': {u'__total__': 0}, u'hops': {u'Willamette': 1.2283195749504061, u'Green Bullet': 1.7640000000000002, u'Hallertau Hersbrucker': 0.3358686337755017, u'__total__': 6.0011882087259085}, u'consumables': {u'__total__': 0.60685, u'500ml glass bottle': 0.00075, u'Crown Caps': 0.3781, u'priming sugar': 0.228}, u'misc': {u'__total__': 0.0, u'Bottled Water (2L)': 0.0}, u'fermentables': {u'Crystal 80': 0.79, u'CaraGold': 2.0000000000000002e-07, u'Honey': 1.9249999999999998, u'__total__': 8.8646502, u'Torrified Wheat': 0.34965000000000007, u'Maris Otter': 5.8}, u'yeast': {u'__total__': 2.4375, u'Safale S04': 2.4375}}, 
		 *  u'stock_status': False, 
		 *  u'stock': {
		 *  		u'__qty_available__': {u'Crystal 80': 500.0, u'CaraGold': 2000.0, u'Hallertau Hersbrucker': 100.0, u'Safale S04': 4.0, u'Willamette': 100.0, u'Honey': 680.0, u'500ml glass bottle': 15.0, u'Torrified Wheat': 1000.0, u'Bottled Water (2L)': 25.196477187083612, u'Maris Otter': 25000.0, u'Green Bullet': 100.0}, u'__pcnt_left__': {u'Crystal 80': 0.0, u'CaraGold': 0.99999995, u'Hallertau Hersbrucker': 0.8400625553449992, u'Safale S04': 0.75, u'Willamette': 0.48820017710399743, u'Honey': 0.0, u'500ml glass bottle': 0, u'Torrified Wheat': 0.667, u'Bottled Water (2L)': 0, u'Maris Otter': 0.8, u'Green Bullet': 0.51}, u'ingredients': {u'__total__': 0}, u'hops': {u'Willamette': 51.179982289600254, u'Green Bullet': 49.0, u'Hallertau Hersbrucker': 15.993744465500079, u'__total__': 215.17372675510032}, 
		 *  		u'__qty_required__': {u'Crystal 80': 500.0, u'CaraGold': 0.0001, u'Hallertau Hersbrucker': 15.993744465500079, u'Safale S04': 1.0, u'Willamette': 51.179982289600254, u'Honey': 680.0, u'500ml glass bottle': 54.0, u'Torrified Wheat': 333.0, u'Bottled Water (2L)': 44.33151376298643, u'Maris Otter': 5000.0, u'Green Bullet': 49.0}, u'consumables': {u'__total__': 0}, u'misc': {u'__total__': 44.33151376298643, u'Bottled Water (2L)': 44.33151376298643},
		 *  	 	u'__stockrequirements__': [[u'Bottled Water (2L)', 25.196477187083612, 44.33151376298643], [u'500ml glass bottle', 15.0, 54.0]], u'fermentables': {u'Crystal 80': 500.0, u'CaraGold': 0.0001, u'Honey': 680.0, u'__total__': 6513.0001, u'Torrified Wheat': 333.0, u'Maris Otter': 5000.0}, 
		 *  		u'__out_of_stock__': [u'Bottled Water (2L)', u'500ml glass bottle'], u'yeast': {u'__total__': 1.0, u'Safale S04': 1.0}}}
		 *  
		 *  
		 *  
		 *  
		 *   OLDSTOCK{"result": {"stock_status": false, "out_of_date_stock": true, "cost": {"yeast": {"__total__": 2.4375, "Safale S04": 2.4375}, "hops": {"Green Bullet": 1.7640000000000002, "__total__": 6.0011882087259085, "Hallertau Hersbrucker": 0.3358686337755017, "Willamette": 1.2283195749504061}, "consumables": {"Minikeg (5L)": 0.0, "Crown Caps": 0.7761, "priming sugar": 0.45602394, "Polypin (5L)": 6.99, "__total__": 9.22087394, "500ml glass bottle": 0.00075, "16g CO2 bulb": 0.998}, "misc": {"__total__": 0.49310300293925685, "Bottled Water (2L)": 0.49310300293925685}, "fermentables": {"Crystal 80": 0.79, "CaraGold": 2.0000000000000002e-07, "Honey": 1.9433333333333334, "__total__": 8.882983533333334, "Torrified Wheat": 0.34965000000000007, "Maris Otter": 5.8}, "ingredients": {"__total__": 0}}, 
		 *   
		 *   
		 *   "oldstockindex": 
		 *   	["Minikeg (5L)", "Burton Water Salts", "Hallertau Hersbrucker", "Protofloc", "Yeast Vit", "Willamette", "Sterilising Powder", "Muslin Bag", "500ml glass bottle", "Campden Tablets", "16g CO2 bulb", "PFTE Tape"], "stock": {"__qty_available__": {"Crystal 80": 500.0, "CaraGold": 2000.0, "Hallertau Hersbrucker": 100.0, "Safale S04": 4.0, "Willamette": 100.0, "Honey": 1020.0, "Torrified Wheat": 1000.0, "Bottled Water (2L)": 30.0, "Maris Otter": 25000.0, "Green Bullet": 100.0}, "__pcnt_left__": {"Crystal 80": 0.0, "CaraGold": 0.99999995, "Hallertau Hersbrucker": 0.8400625553449992, "Safale S04": 0.75, "Willamette": 0.48820017710399743, "Honey": 0.33333333333333337, "Torrified Wheat": 0.667, "Bottled Water (2L)": 0.2748485250893282, "Maris Otter": 0.8, "Green Bullet": 0.51}, "consumables": {"__total__": 0}, "ingredients": {"__total__": 0}, "hops": {"Green Bullet": 49.0, "__total__": 215.17372675510032, "Hallertau Hersbrucker": 15.993744465500079, "Willamette": 51.179982289600254}, "misc": {"__total__": 21.754544247320155, "Bottled Water (2L)": 21.754544247320155}, "yeast": {"__total__": 1.0, "Safale S04": 1.0}, "__qty_required__": {"Crystal 80": 500.0, "CaraGold": 0.0001, "Hallertau Hersbrucker": 15.993744465500079, "Safale S04": 1.0, "Willamette": 51.179982289600254, "Honey": 680.0, "Torrified Wheat": 333.0, "Bottled Water (2L)": 21.754544247320155, "Maris Otter": 5000.0, "Green Bullet": 49.0}, "__stockrequirements__": [], "fermentables": {"Crystal 80": 500.0, "CaraGold": 0.0001, "Honey": 680.0, "__total__": 6513.0001, "Torrified Wheat": 333.0, "Maris Otter": 5000.0}, 
		 *   
		 *   "__out_of_stock__": []}, "out_of_stock": false, 
		 *   
		 *    "oldstock": {"Minikeg (5L)": [[1, 1033909.1238958836, "Minikeg (5L)", "BRI000098"]], "Burton Water Salts": [[1, 962194.1238958836, "Burton Water Salts", "WCOMV0023"]], "Hallertau Hersbrucker": [[1, 688309.1238958836, "Hallertau Hersbrucker", "WHOMV0019"]], "Protofloc": [[1, 1033909.1238958836, "Protofloc", "BRI000111"]], "Yeast Vit": [[1, 962202.1238958836, "Yeast Vit", "WCOMV0008"]], "Willamette": [[1, 688309.1238958836, "Willamette", "WHOMV0020"]], "Sterilising Powder": [[1, 962142.1238958836, "Sterilising Powder", "WCOMV0066"], [1, 962139.1238958836, "Sterilising Powder", "WCOMV0068"]], "Muslin Bag": [[1, 256309.12389588356, "Muslin Bag", "WCOMV0069"]], "500ml glass bottle": [[1, 1033909.1238958836, "500ml glass bottle", "BRI000110"]], "Campden Tablets": [[1, 688309.1238958836, "Campden Tablets", "WCOMV0067"]], "16g CO2 bulb": [[1, 962176.1238958836, "16g CO2 bulb", "WCOMV0065"], [1, 962183.1238958836, "16g CO2 bulb", "BRI000035"]], "PFTE Tape": [[1, 962150.1238958836, "PFTE Tape", "WCOMV0047"]]}}}
		 *
		 */
		
		

		TextView tv = new TextView(this);
		
		tv.setText( "Item  ");
		tv.setTextSize(18);
		//tv.setTextAppearance(null, Typeface.BOLD);
		//LayoutParams lparm = (LayoutParams) tv.getLayoutParams();
		//lparm.width=300;
		//tv.setLayoutParams(lparm);

		TextView tv2 = new TextView(this);
		//tv2.setTextAppearance(getApplicationContext(), R.mystyle.boldText);
		
		
		if(outofdatestock){
			tv2.setText( "Days Over  " );
		}else{
			tv2.setText( "Required  " );
		}
		tv2.setTextSize(18);
	//	tv2.setTextAppearance(null, Typeface.BOLD);
//		lparm = (LayoutParams) tv2.getLayoutParams();
	//	lparm.width=150;
//		tv.setLayoutParams(lparm);
//
		
		TextView tv3 = new TextView(this);
		if(outofdatestock){
			tv3.setText( " ");
		}else{
			tv3.setText( "Available");
		}
		
	//	tv3.setTextAppearance(null, Typeface.BOLD);
		tv3.setTextSize(18);
		
		LinearLayout ll = new LinearLayout(this);
		ll.setPadding(4,0,0,0);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		ll.addView(tv);

		ll.addView(tv2);
		ll.addView(tv3);
		linearLayout.addView(ll, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		
		DecimalFormat dfsingle = new DecimalFormat("##0.0");

	
		LayoutParams lparm = (LayoutParams) tv3.getLayoutParams();
		lparm.width=150;
		tv3.setLayoutParams(lparm);
		lparm = (LayoutParams) tv2.getLayoutParams();
		lparm.width=150;
		tv2.setLayoutParams(lparm);
		lparm = (LayoutParams) tv.getLayoutParams();
		lparm.width=440;
		tv.setLayoutParams(lparm);
	
			
		
		if(outofdatestock){
				// "oldstock": {"Minikeg (5L)": [[1, 1032515.8618090153, "Minikeg (5L)", "BRI000098"]], "Burton Water Salts": [[1, 960800.8618090153, "Burton Water Salts", "WCOMV0023"]], "Hallertau Hersbrucker": [[1, 686915.8618090153, "Hallertau Hersbrucker", "WHOMV0019"]], "Protofloc": [[1, 1032515.8618090153, "Protofloc", "BRI000111"]], "Yeast Vit": [[1, 960808.8618090153, "Yeast Vit", "WCOMV0008"]], "Willamette": [[1, 686915.8618090153, "Willamette", "WHOMV0020"]], "Sterilising Powder": [[1, 960748.8618090153, "Sterilising Powder", "WCOMV0066"], [1, 960745.8618090153, "Sterilising Powder", "WCOMV0068"]], "Muslin Bag": [[1, 254915.86180901527, "Muslin Bag", "WCOMV0069"]], "500ml glass bottle": [[1, 1032515.8618090153, "500ml glass bottle", "BRI000110"]], "Campden Tablets": [[1, 686915.8618090153, "Campden Tablets", "WCOMV0067"]], "16g CO2 bulb": [[1, 960782.8618090153, "16g CO2 bulb", "WCOMV0065"], [1, 960789.8618090153, "16g CO2 bulb", "BRI000035"]], "PFTE Tape": [[1, 960756.8618090153, "PFTE Tape", "WCOMV0047"]]}}}
			
			
			TextView t = (TextView) findViewById(R.id.OUTOFSTOCKtextView1);
			t.setText("Some of the stock we require is too old for this brewlog");
			
			
			try {
				//JSONObject jObject = new JSONObject(jsonResponse);				
				//JSONObject jResult = jObject.getJSONObject("result");
				
				Log.i("brewerspad","oldstock A");
				Log.i("brewerspad","OLDSTOCK"+jsonResponse);
				
				
				JSONArray jOldStockIndex= jResult.getJSONArray("oldstockindex");				
				Log.i("brewerspad"," got past the oldstockindex");
				
				JSONObject jOldStock= jResult.getJSONObject("oldstock");
				Log.i("brewerspad"," got past the oldstock");
				
					
				for (int i = 0; i < jOldStockIndex.length(); ++i) {
					Log.i("brewerspad", "inside oldstock loop"+i);
					String oldstockname=(String) jOldStockIndex.get(i);
					Log.i("brewerspad","oldstockname "+oldstockname);
					JSONArray tmp2 = jOldStock.getJSONArray( oldstockname );
					
					
					
					for(int j = 0;j < tmp2.length(); ++j){
						
						
						JSONArray tmp = tmp2.getJSONArray(j);
								
							String stockitem = tmp.getString(2);
							String stocktag = tmp.getString(3);
							Integer outofdate= tmp.getInt(1);
							
							
						
							tv = new TextView(this);
							tv.setText( stockitem +"/"+stocktag+"  ");
							tv.setTextSize(18);
							
							tv3 = new TextView(this);
							tv3.setText(  outofdate +"" );
							tv3.setTextSize(18);
							
							ll = new LinearLayout(this);
							ll.setPadding(4,0,0,0);
							ll.setOrientation(LinearLayout.HORIZONTAL);
							ll.addView(tv);
			
							//ll.addView(tv2);
							ll.addView(tv3);
			
						
							linearLayout.addView(ll, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
							//linearLayout.addView(tv2, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
						//linearLayout.addView(tv3, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
						
					}
				}
		
		
			} catch(JSONException e){
				Log.i("brewerspad","JSONexception"+e.getMessage());
			}

			
		}
		
		if(outofstock){
			try {
				//JSONObject jObject = new JSONObject(jsonResponse);				
				//JSONObject jResult = jObject.getJSONObject("result");
				JSONObject jStock = jResult.getJSONObject("stock");
				Log.i("brewerspad","outofstock A");
				
				JSONArray jStockRequirements= jStock.getJSONArray("__stockrequirements__");
				Log.i("brewerspad","OUT OF STOCK"+jsonResponse);
				
				Log.i("brewerspad","outofstock A2");
	
				for (int i = 0; i < jStockRequirements.length(); ++i) {
					JSONArray tmp = jStockRequirements.getJSONArray(i);
				
	
					String stockitem = tmp.getString(0);
					Double stockrequired= tmp.getDouble(1);
					Double stockavailable= tmp.getDouble(2);
					
					Log.i("brewerspad","ok B heree" + i +" "+stockitem);
	
					tv = new TextView(this);
					tv.setText( stockitem +"  ");
					tv.setTextSize(18);
	
					tv2 = new TextView(this);
					tv2.setText( dfsingle.format(stockrequired)+" /" );
					tv2.setTextSize(18);
					
					tv3 = new TextView(this);
					tv3.setText( dfsingle.format(stockavailable)+"" );
					tv3.setTextSize(18);
					
					ll = new LinearLayout(this);
					ll.setPadding(4,0,0,0);
					ll.setOrientation(LinearLayout.HORIZONTAL);
					ll.addView(tv);
	
					ll.addView(tv2);
					ll.addView(tv3);
	
				
					
					//linearLayout.addView(tv);
					//linearLayout.addView(tv2);
					//linearLayout.addView(tv3);
					
					linearLayout.addView(ll, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
					//linearLayout.addView(tv2, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
					//linearLayout.addView(tv3, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
					
	
							}
		
		
			} catch(JSONException e){
				Log.i("brewerspad","JSONexception"+e.getMessage());
			}
		}
		
		
		
		
	}
	
/*


	public void presentError(String title,String exception, String jsonResponse){

		//Toast.makeText(this, "Unable to communicate with the brewerspad server.", Toast.LENGTH_LONG).show();

		Log.i("brewerspad", "________________________________________________________________________");
		Log.i("brewerspad", "		Error/Exception in "+title);
		Log.i("brewerspad", "Response"+jsonResponse);
		Log.i("brewerspad", "Exception"+exception);
		Log.i("brewerspad", "________________________________________________________________________");

			SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
			SharedPreferences.Editor editor = shrdPrefs.edit();
			editor.putString("errorlocation", "Error:" +title );
			editor.putString("exception","Exception:" +exception);
			editor.putString("jsonresponse", "Response:"+jsonResponse);
			editor.commit();
			startActivity(new Intent(this, debugxmlrpc.class));


	}
*/
	
}

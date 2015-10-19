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


/*
 * 
 * 
 * 
 * 
 * NOTE SOME LIST VIEW BITS CAN MOVE TO COMMON AND THE ASSOCIATED "PostUpdates" can be removed
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */


import java.text.DecimalFormat;
import java.util.ArrayList;
import brewerspad.asduk.com.storescommon.stockitem;


import android.app.Activity;
import android.app.ProgressDialog;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;






public class stores2 extends Activity {
	Boolean APIerror=false;
	
	Integer dbg = 0;

	ProgressDialog pleasewait;

	//common 
	storescommon storeLogic = new storescommon();


	// this will be populated from sharedprefs
	String hostnameIp;
	Integer portNum;
	Boolean tabletDevice;

	Boolean debugXMLRPC = true;

	DisplayMetrics dm;

	ListView listViewStockItems;
	ListView listViewB2; 

	ArrayAdapter stockItemsArrayAdapter;






	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("brewerspad","stores2/onCreate()");
		
		// set outselves as a boss in storecommon
		storeLogic.stores2Boss = this;
		storeLogic.boss = "stores2";

		
		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		storeLogic.cloudKey = shrdPrefs.getString("cloudkey", "expired");
		storeLogic.cloudUser = shrdPrefs.getString("clouduser", "expired");
		storeLogic.cloudDevice = "androidApp";

		tabletDevice = shrdPrefs.getBoolean("tabletDevice", true);
		hostnameIp = shrdPrefs.getString("hostname", "127.0.0.1");
		portNum = shrdPrefs.getInt("portNumber", 1027);

		setContentView(R.layout.storesphone2);

		// set data on common object
		storeLogic.pleasewait = pleasewait;
		storeLogic.tabletDevice = tabletDevice;


		// Get Screen Dimensions
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);


		// show the background image on the layout
		String path = Environment.getExternalStorageDirectory().toString();
		String pathName = path + "/brewerspad/storesbg.png";
		Resources res = getResources();
		Bitmap bitmap = BitmapFactory.decodeFile(pathName);
		BitmapDrawable bd = new BitmapDrawable(res, bitmap);
		View view = findViewById(R.id.storesOuterLayoutPage2);
		view.setBackgroundDrawable(bd);



		// add list view for steps
		listViewStockItems = new ListView(this);
		listViewStockItems.setFocusableInTouchMode(true);
		Log.i("brewerspad", "store2.listViewa");
		storeLogic.listViewStockItems = new ArrayList<stockitem>();
		stockItemsArrayAdapter = new  StockItemAdapter(this, R.layout.customrowitem2, storeLogic.listViewStockItems);
		listViewStockItems.setAdapter(stockItemsArrayAdapter);
		listViewStockItems.setClickable(true);
		listViewStockItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {		
			
			public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {	
				stockitem o = (stockitem) listViewStockItems.getItemAtPosition(arg2);
				Log.i("brewerspad", "store2.listview Selected" + o);
				Log.i("brewerspad", "store2.listview Selected about to show progressdialog");
				Log.i("brewrespad","store2.category = "+o.StockCategory);
				Log.i("brewrespad","store2.stockname = "+o.StockName);

				
				/* 		Old direct xmlrpc
				storexmlrpc task = new storexmlrpc();
				task.them = storeLogic;
				task.hostnameIp = hostnameIp;
				task.portNum = portNum;			
				task.execute("getStockFullDetails", o.StockCategory,o.StockName);				
				*/
				
				// new 
				fluffycloud cloudtask = new fluffycloud();
				cloudtask.themStore = storeLogic;
				cloudtask.themId = "stores"; //not sure how this really works anymore
				cloudtask.hostnameIp = hostnameIp;
				cloudtask.cloudKey = storeLogic.cloudKey;
				cloudtask.cloudDevice = storeLogic.cloudDevice;
				cloudtask.cloudUser = storeLogic.cloudUser;
				cloudtask.portNum = portNum+1;
				cloudtask.execute("getStockFullDetails",o.StockCategory,o.StockName);


				if(!tabletDevice){	
					// show pleasewait dialog box while we download more data
								//pleasewait = ProgressDialog.show(stores2.this,"please wait","",true);
				}
				//Toast.makeText(this,"this changed",Toast.LENGTH_SHORT).show();





			}
		});					 

		Log.i("brewerspad","stores2[2]");

		//

		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.storesInner2of3);
		linearLayout.addView(listViewStockItems, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT));


		// Update List object 

		if( storeLogic.updateStoreItems(  shrdPrefs.getString("jsonresponseA", "127.0.0.1") )) 	{
			stockItemsArrayAdapter.notifyDataSetChanged();
			TextView tv = (TextView) findViewById(R.id.storesTitle2);
			tv.setText(storeLogic.storeTitle);
			Log.i("brewerspad","stores2[3] - commented out");
		}else{
			presentError(storeLogic.exceptionTitle, storeLogic.exception, storeLogic.exceptionResponse);
			Log.i("brewerspad","returning an exception");
		}

		Log.i("brewerspad","stores2[4]");

	}






	public void getStockFullDetailPostUpdate(String jsonResponse){
		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		SharedPreferences.Editor editor = shrdPrefs.edit();
		editor.putString("jsonresponseB", jsonResponse);
		editor.commit();

		// we have writte this to 
		Log.i("brewerspad","stores2.writing the following jsonresponse" + jsonResponse);
		//Toast.makeText(this,"getStockFullDetailPostUpdate called",Toast.LENGTH_LONG).show();
		startActivity(new Intent(this, stores3.class));
	}




	public void presentError(String title,String exception, String jsonResponse){

		//Toast.makeText(this, "Unable to communicate with the brewerspad server.", Toast.LENGTH_LONG).show();

		Log.i("brewerspad", "________________________________________________________________________");
		Log.i("brewerspad", "		Error/Exception in "+title);
		Log.i("brewerspad", exception);
		Log.i("brewerspad", jsonResponse);
		Log.i("brewerspad", "________________________________________________________________________");
		if(debugXMLRPC==true){
			SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
			SharedPreferences.Editor editor = shrdPrefs.edit();
			editor.putString("errorlocation", title );
			editor.putString("exception",exception);
			editor.putString("jsonresponse", jsonResponse);
			editor.commit();
			startActivity(new Intent(this, debugxmlrpc.class));
		}

	}




	private class StockItemAdapter extends ArrayAdapter<stockitem> {

		private ArrayList<stockitem> items;

		public StockItemAdapter(Context context, int textViewResourceId, ArrayList<stockitem> items) {
			super(context, textViewResourceId, items);

			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.customrowitem2, null);

			}

			stockitem o = items.get(position);
			if (o != null) {

				TextView tt = (TextView) v.findViewById(R.id.cri2toptext);
				tt.setText( ""+o.StockName );							
				TextView bt = (TextView) v.findViewById(R.id.cri2bottomtext);

				bt.setText( ""+o.StockQty +" "+o.StockUnit+"\t");
				TextView bt2 = (TextView) v.findViewById(R.id.cri2bottomtext2);
				DecimalFormat df = new DecimalFormat("##0.00");

				bt2.setText( "£ "+df.format(o.StockCost)); 

			}
			return v;
		}
	}


}






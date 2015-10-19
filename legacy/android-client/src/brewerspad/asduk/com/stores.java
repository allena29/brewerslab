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
   
$Revision: 1.16 $ $Date: 2011-10-26 15:36:55 $ $Author: codemonkey $


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
import android.app.AlertDialog;
import android.app.ProgressDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.os.Environment;

import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class stores extends Activity {

	
	Boolean APIerror = false;
	
	Integer dbg = 0;

	ProgressDialog pleasewait;

	//common 
	storescommon storeLogic = new storescommon();

	// in stores3 and stores
	ListView listViewPurchases;
	ArrayAdapter purchasesArrayAdapter;
	
	// this will be populated from sharedprefs
	String hostnameIp;
	Integer portNum;
	Boolean tabletDevice;

	Boolean debugXMLRPC = true;

	DisplayMetrics dm;

	ListView listViewStockCategories;
	ListView listViewB2;
	
	String selectedItem;
	String selectedCategory;
	String selectedTag;

	ArrayAdapter stockCategoriesArrayAdapter;

	// only really needed for the phone
	ListView listViewStockItems;

	/*
	 * Duplicates store Phone 2
	 */
	ArrayAdapter stockItemsArrayAdapter;






	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("brewerspad","stores/onCreate()");
		
		// set outselves as a boss in storecommon
		storeLogic.storesBoss = this;
		storeLogic.boss = "stores";

		
		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		tabletDevice = shrdPrefs.getBoolean("tabletDevice", true);
		hostnameIp = shrdPrefs.getString("hostname", "127.0.0.1");
		portNum = shrdPrefs.getInt("portNumber", 1027);

		setContentView(R.layout.stores);

		// set data on common object
		storeLogic.pleasewait = pleasewait;
		storeLogic.tabletDevice = tabletDevice;
		// the common logic needs to be updated
		storeLogic.cloudKey = shrdPrefs.getString("cloudkey", "expired");
		storeLogic.cloudUser = shrdPrefs.getString("clouduser", "expired");
		storeLogic.cloudDevice = "androidApp";


		// Get Screen Dimensions
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);


		// show the background image on the layout
		String path = Environment.getExternalStorageDirectory().toString();
		String pathName = path + "/brewerspad/storesbg.png.xml";
		Resources res = getResources();
		Bitmap bitmap = BitmapFactory.decodeFile(pathName);
		BitmapDrawable bd = new BitmapDrawable(res, bitmap);
		View view = findViewById(R.id.storesOuterLayout);
		view.setBackgroundDrawable(bd);



		if(tabletDevice && getWindowManager().getDefaultDisplay().getWidth() > getWindowManager().getDefaultDisplay().getHeight()){
			tabletOnCreate();
			
		}


		// in the preivous version we set all the widths here!


		// this is back in the right place now, the only change this time
		// is that we have a return type now
		//        if(tabletDevice){
		//      	tabletOnCreate();
		//    }


		// then we did the list view

		// add list view for steps
		listViewStockCategories = new ListView(this);
		listViewStockCategories.setFocusableInTouchMode(true);
		storeLogic.listViewCategories = new ArrayList<String>();
		stockCategoriesArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, storeLogic.listViewCategories);
		listViewStockCategories.setAdapter(stockCategoriesArrayAdapter);	
		listViewStockCategories.setClickable(true);
		listViewStockCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {		
			public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {	
				String o = (String) listViewStockCategories.getItemAtPosition(arg2);
				Log.i("brewerspad", "store.listview Selected" + o);
				Log.i("brewerspad", "store.listview Selected about to show progressdialog");


				fluffycloud cloudtask = new fluffycloud();
				cloudtask.themStore = storeLogic;
				cloudtask.themId = "stores";
				cloudtask.hostnameIp = hostnameIp;
				cloudtask.cloudKey = storeLogic.cloudKey;
				cloudtask.cloudDevice = storeLogic.cloudDevice;
				cloudtask.cloudUser = storeLogic.cloudUser;
				cloudtask.portNum = portNum+1;
				cloudtask.execute("listStoreItems",o);

				//Toast.makeText(this, "looking for", Toast.LENGTH_LONG).show();
				//Toast.makeText(this,"loooking for store items"+o+"",Toast.LENGTH_LONG).show();

				if(!tabletDevice){	
					// show pleasewait dialog box while we download more data
					
					
				}





			}
		});					 

		Log.i("brewerspad","stores2[2]");

		//175	
		// add list view for the steps (common for tablet/phone)
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.storesInner1of3);
		linearLayout.addView(listViewStockCategories, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT));





		// Download the list of store categories
		fluffycloud cloudtask = new fluffycloud();
		cloudtask.themStore = storeLogic;
		cloudtask.themId = "stores";
		cloudtask.hostnameIp = hostnameIp;
		cloudtask.cloudKey = shrdPrefs.getString("cloudkey", "expired");
		cloudtask.cloudDevice = shrdPrefs.getString("clouddevice","<testdevice>");
		cloudtask.cloudUser = shrdPrefs.getString("clouduser","<user>");
		cloudtask.portNum = portNum+1;
		cloudtask.execute("listStoreCategories");



//		Log.i("brewerspad","stores[4]");

		// set ourselves as the last page
		SharedPreferences.Editor editor = shrdPrefs.edit();
		editor.putString("lastActivity", "stores");
		editor.commit();



	}



	public void tabletOnCreate() {

		/*
		 * 
		 *	duplicates store2 
		 * 
		 */
		
		
		// add list view for steps
		final ListView listViewStockItems = (ListView) findViewById(R.id.storesStockItems);
		listViewStockItems.setFocusableInTouchMode(true);
		Log.i("brewerspad", "store2.listViewa");
		storeLogic.listViewStockItems = new ArrayList<stockitem>();
		Log.i("brewerspad", "store2.listViewb");
		stockItemsArrayAdapter = new  StockItemAdapter(this, R.layout.customrowitem2, storeLogic.listViewStockItems);
		Log.i("brewerspad", "store2.listViewc");
		listViewStockItems.setAdapter(stockItemsArrayAdapter);
		Log.i("brewerspad", "store2.listViewd");
		listViewStockItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {		
			
			public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {	
				stockitem o = (stockitem) listViewStockItems.getItemAtPosition(arg2);

				
				
				

				if(o.specialAddItem){
					/*
					 * Add Item
					 */
					
					addNewPurchaseOrItem(o.StockCategory,"");
					
				}else{
					/*
					 * Lookup purchases
					 */
					fluffycloud cloudtask = new fluffycloud();
					cloudtask.themStore = storeLogic;
					cloudtask.themId = "stores"; //them = main logic 
					cloudtask.hostnameIp = hostnameIp;
					cloudtask.cloudKey = storeLogic.cloudKey;
					cloudtask.cloudDevice = storeLogic.cloudDevice;
					cloudtask.cloudUser = storeLogic.cloudUser;
					cloudtask.portNum = portNum+1;
					cloudtask.execute("getStockFullDetails",o.StockCategory,o.StockName);
	
	
	
					SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
					if( storeLogic.updateStoreItems(  shrdPrefs.getString("jsonresponseA", "127.0.0.1") )) 	{
						stockItemsArrayAdapter.notifyDataSetChanged();
						TextView tv = (TextView) findViewById(R.id.storesTitle2);
						tv.setText(storeLogic.storeTitle);
						Log.i("brewerspad","stores2[3] - commented out");
					}else{
						presentError(storeLogic.exceptionTitle, storeLogic.exception, storeLogic.exceptionResponse);
						Log.i("brewerspad","returning an exception");
					}

				}
			}
		});
		
		Log.i("brewerspad","peekaboo");
		//LinearLayout linearLayout = (LinearLayout) findViewById(R.id.storesInner2of3);
		//linearLayout.addView(listViewStockItems, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		
		

		
		
		tabletOnResume();

	}


	@Override
	public void onResume(){		
		super.onResume();
		Log.i("brewerspad","brewday onResume()");
		if(tabletDevice && getWindowManager().getDefaultDisplay().getWidth() > getWindowManager().getDefaultDisplay().getHeight()){
			tabletOnResume();			
		}
	}
	

	
	public void tabletOnResume() {  	
		Log.i("brewerspad","stores:tabletOnResume()");
		// 	1st third
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.storesOuter1of3);		
		LayoutParams frame = (LayoutParams) linearLayout.getLayoutParams();
		frame.width = dm.widthPixels / 3;
		frame.height =  dm.heightPixels-80;
		linearLayout.setLayoutParams(frame); 

/*
		linearLayout = (LinearLayout) findViewById(R.id.storesOuter2of3);		
		frame = (LayoutParams) linearLayout.getLayoutParams();
		frame.width = dm.widthPixels / 3;
		frame.height =  dm.heightPixels-80;
		linearLayout.setLayoutParams(frame); 

		
		linearLayout = (LinearLayout) findViewById(R.id.storesOuter3of3);		
		frame = (LayoutParams) linearLayout.getLayoutParams();
		frame.width = dm.widthPixels / 3;
		frame.height =  dm.heightPixels-80;
		linearLayout.setLayoutParams(frame); 

		*/
		linearLayout = (LinearLayout) findViewById(R.id.storesOuter1of3);		
		frame = (LayoutParams) linearLayout.getLayoutParams();
		
		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		if(shrdPrefs.getBoolean("refreshstore",false)){
			//Toast.makeText(this,"how do we refresh this????",Toast.LENGTH_LONG).show();
			

			fluffycloud cloudtask = new fluffycloud();
			cloudtask.themStore = storeLogic;
			cloudtask.themId = "stores"; //them = main logic 
			cloudtask.hostnameIp = hostnameIp;
			cloudtask.cloudKey = storeLogic.cloudKey;
			cloudtask.cloudDevice = storeLogic.cloudDevice;
			cloudtask.cloudUser = storeLogic.cloudUser;
			cloudtask.portNum = portNum+1;
			if(shrdPrefs.getString("presetitem","").equals("")){
				cloudtask.execute("listStoreItems",shrdPrefs.getString("presetcategory",""));				
			}else{
				cloudtask.execute("getStockFullDetails",shrdPrefs.getString("presetcategory",""),shrdPrefs.getString("presetitem",""));
			}


			
			
			SharedPreferences.Editor editor = shrdPrefs.edit();
			editor.putBoolean("refreshstore", false);
			editor.commit();
		}

	}
	
	public void upateStoreItemsPostUpdate(String jsonResponse){

		Log.i("brewerspad","safely back in stores.updateStoreItemsPostUpdate");	
		Log.i("brewerspad","is this where we get to ???? yes or no????   stores.java:A");
		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		SharedPreferences.Editor editor = shrdPrefs.edit();
		editor.putString("jsonresponseA", jsonResponse);
		editor.commit();
		Log.i("brewerspad","committed json responseA "+jsonResponse);
		// dismiss the progress bar
//			pleasewait.dismiss();
		if(tabletDevice && getWindowManager().getDefaultDisplay().getWidth() > getWindowManager().getDefaultDisplay().getHeight()){
			// we are on a tablet in horizontal mode
			Log.i("brewerspad","Adoing nothing because we are in tablet mode.... but we do need to do something here");
			LinearLayout linearLayout = (LinearLayout) findViewById(R.id.storesOuter2of3);
			linearLayout.setVisibility(View.VISIBLE);
			
			//
			if( storeLogic.updateStoreItems(  shrdPrefs.getString("jsonresponseA", "127.0.0.1") )) 	{
				stockItemsArrayAdapter.notifyDataSetChanged();
				TextView tv = (TextView) findViewById(R.id.storesTitle2);
				tv.setText(storeLogic.storeTitle);
				Log.i("brewerspad","stores2[3] - commented out");
			}
			
			
		}else{
			// we are on a phone or a tablet in vertical mode
			startActivity(new Intent(this, stores2.class));
		}
	}


	
	

	public void getStockFullDetailPostUpdate(String jsonResponse){
		// Tablet only, phone version in stores2 (and stores3.onCreate())
		LinearLayout ll = (LinearLayout) findViewById(R.id.storesOuter3of3);
		ll.setVisibility(View.VISIBLE);
		Log.i("brewerspad","stores. set outer 3of 3 to visible");
		
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.storesOuter2of3);		
		LayoutParams frame = (LayoutParams) linearLayout.getLayoutParams();
		frame.width = dm.widthPixels / 3;
		frame.height =  dm.heightPixels-80;
		linearLayout.setLayoutParams(frame); 

		
		
		listViewPurchases = (ListView) findViewById(R.id.storesPurchases);
    	// add list view for purchases

		listViewPurchases.setFocusableInTouchMode(true);
		Log.i("brewerspad", "stores.listViewa");
			// this below is redundant because we use it from common
		storeLogic.listViewPurchases = new ArrayList<stockitem>();
		Log.i("brewerspad", "stores.listViewb");
		purchasesArrayAdapter = new  PurchaseItemAdapter(this, R.layout.customrowitem3, storeLogic.listViewPurchases);
		Log.i("brewerspad", "stores.listViewc");
		listViewPurchases.setAdapter(purchasesArrayAdapter);
		Log.i("brewerspad", "stores.listViewd");
		listViewPurchases.setClickable(true);

		
		listViewPurchases.setOnItemClickListener(new AdapterView.OnItemClickListener() {		
			
			public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {	
				stockitem o = (stockitem) listViewPurchases.getItemAtPosition(arg2);
				if(o.specialAddPurchase){
					addNewPurchaseOrItem(o.StockCategory,o.StockName);
				}
				
			}
		});
		//http://android.konreu.com/developer-how-to/click-long-press-event-listeners-list-activity/
		listViewPurchases.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {



			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				stockitem o = (stockitem) listViewPurchases.getItemAtPosition(arg2);
				Log.i("brewerspad", "stores.listview long Selected" + o.StockName+" "+o.StockCategory);
				
				selectedItem=o.StockName;
				selectedCategory=o.StockCategory;
				selectedTag=o.PurchaseStockTag;
				askForNewQty(o.StockCategory,o.StockName,o.StockQty);
				
				return false;
			}
		});					 
		

		storeLogic.updateStockDetail(  jsonResponse );
		
		TextView tv = (TextView) findViewById(R.id.storesTitle3);
		tv.setText( storeLogic.StockItem.StockName+" ("+storeLogic.StockItem.StockCategory+")");
		Log.i("brewerspad","stores[3] ");
		
		DecimalFormat df = new DecimalFormat("##0.00");
//		purchasesArrayAdapter.notifyDataSetChanged();

		TextView tv2 = (TextView) findViewById(R.id.storesDescription);
		if(storeLogic.StockItem.StockCategory.equals("Hops")){
			tv2.setText( Html.fromHtml("Average Alpha: "+df.format(storeLogic.StockItem.HopAlpha)+" %<br><i>"+storeLogic.StockItem.StockDescription+"</i>"));
		}else{
			tv2.setText( Html.fromHtml("<i>"+storeLogic.StockItem.StockDescription+"</i>"));
		}
		TextView tv3 = (TextView) findViewById(R.id.storesTotalCost);
		tv3.setText( Html.fromHtml( "<b>Total Stock Cost:</b> £" + df.format(storeLogic.StockItem.StockCost))  );
		
		TextView tv4 = (TextView) findViewById(R.id.storesTotalQty);
		tv4.setText( Html.fromHtml("<b>Total Qty:</b> "+storeLogic.StockItem.StockQty +" "+storeLogic.StockItem.StockUnit ));
		
	}

	
	
	
	public void askForNewQty(String stockCategory, String stockItem, Integer stockQty){
		// duplicated with stores3
		Log.i("brewerspad","getNewQty");
		Log.i("brewerspad"," just added onCreate() to storesCOmmon");		
		
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle(selectedTag);
		alert.setMessage("Enter new quantity for "+stockItem);

		// let use get the category back
		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		SharedPreferences.Editor editor = shrdPrefs.edit();
		editor.putString("jsonresponseC", stockCategory);
		editor.commit();

		
		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		input.setText(""+stockQty);
		final CheckBox input2 = new CheckBox(this);
		final LinearLayout ll = new LinearLayout(this);
		ll.setGravity(Gravity.VERTICAL_GRAVITY_MASK); 
		ll.addView(input);
		ll.addView(input2);
		input2.setText("Update best before");
		
		alert.setView(ll);
		//input.setInputType(2);	// TYPE CLASS NUMBER
		input.setInputType(8192);	// TYPE CLASS NUMBER
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			Log.i("brewerspad","Do something to get text here");
			String value = input.getText().toString();
			Log.i("brewerspad","got a value"+value);
		  // Do something with value!

/*
			storexmlrpc task = new storexmlrpc();
			task.them = storeLogic;
			task.hostnameIp = hostnameIp;
			task.portNum = portNum;
			//DownloadWebPageTask task = new DownloadWebPageTask();				
			task.execute("changeItemQty",selectedCategory,selectedItem,selectedTag,value);
*/
			String resetBestBefore="N";
			if(input2.isChecked()){
				resetBestBefore="Y";
			}
			// new 
			fluffycloud cloudtask = new fluffycloud();
			cloudtask.themStore = storeLogic;
			cloudtask.themId = "stores"; //not sure how this really works anymore
			cloudtask.hostnameIp = hostnameIp;
			cloudtask.cloudKey = storeLogic.cloudKey;
			cloudtask.cloudDevice = storeLogic.cloudDevice;
			cloudtask.cloudUser = storeLogic.cloudUser;
			cloudtask.portNum = portNum+1;
			cloudtask.execute("changeItemQty",selectedCategory,selectedItem,selectedTag,value,resetBestBefore);
 
		  }
		});

		
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.		  	
				Log.i("brewerspad","ignored input dialog");
		  }
		});

		alert.show();
		// see http://androidsnippets.com/prompt-user-input-with-an-alertdialog
		

		
	}





	public void updateStoreCategoriespostUpdate(String jsonResponse){
		// duplicated with stores3
		Log.i("brewerspad","safely back in stores.updateStoreCategoriespostUpdate");
		storeLogic.updateStoreCategories(jsonResponse);
		stockCategoriesArrayAdapter.notifyDataSetChanged();	
	}


	public void addNewPurchaseOrItem(String category, String item){
		//Toast.makeText(this,"add purchase or item"+category+" "+item,Toast.LENGTH_LONG).show();
		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		SharedPreferences.Editor editor = shrdPrefs.edit();
		editor.putString("presetcategory", category);
		editor.putString("presetitem", item);
		editor.commit();

		startActivity(new Intent(this, storepurchase.class));		
		
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



	/*
	 * Duplicates store Phone 2
	 */

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

				if(o.specialAddItem){
        			TextView tt = (TextView) v.findViewById(R.id.cri2toptext);
        			tt.setText(R.string.addStockItem);
					TextView bt = (TextView) v.findViewById(R.id.cri2bottomtext);
					bt.setText("");
					
				}else{
					TextView tt = (TextView) v.findViewById(R.id.cri2toptext);
					tt.setText( ""+o.StockName );							
					TextView bt = (TextView) v.findViewById(R.id.cri2bottomtext);
	
					bt.setText( ""+o.StockQty +" "+o.StockUnit+"\t");
					TextView bt2 = (TextView) v.findViewById(R.id.cri2bottomtext2);
					
					DecimalFormat df = new DecimalFormat("##0.00");
					bt2.setText( "£ "+df.format(o.StockCost)); 
				}
			}
			return v;
		}
	}

	

	private class PurchaseItemAdapter extends ArrayAdapter<stockitem> {
		
        private ArrayList<stockitem> items;

        public PurchaseItemAdapter(Context context, int textViewResourceId, ArrayList<stockitem> items) {
                super(context, textViewResourceId, items);
                
                this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
    			
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.customrowitem3, null);
                }
                stockitem o = items.get(position);
                if (o != null) {
    					DecimalFormat df = new DecimalFormat("##0.00");

    					
    					if(o.specialAddPurchase){
                			TextView tt = (TextView) v.findViewById(R.id.cri2toptext);
                			tt.setText(R.string.addPurchase);
                			// cleanup header rows
	                        TextView tt2 = (TextView) v.findViewById(R.id.cri2toptext2);
							tt2.setText("");
	                        TextView tt3 = (TextView) v.findViewById(R.id.cri2toptext3);							
							tt3.setText("");
	                        TextView bt = (TextView) v.findViewById(R.id.cri2bottomtext);
	                        bt.setText("");	                        
	                        bt.setVisibility(View.GONE);
	                        tt2.setVisibility(View.GONE);
	                        tt3.setVisibility(View.GONE);	                        
    					}else{
	                		// Stock Name
	                		if(o.StockCategory.equals("Hops")){
	                			TextView tt = (TextView) v.findViewById(R.id.cri2toptext);
								tt.setText( ""+o.StockName +" Alpha:~"+ df.format(o.HopAlpha) +"% ("+o.StockQty+" "+o.StockUnit+")");
	                		}else{
	                            TextView tt = (TextView) v.findViewById(R.id.cri2toptext);
	    						tt.setText( ""+o.StockName +" ("+o.StockQty+" "+o.StockUnit+")");                			
	                		}
							// SHould be stock Tag + SUpplier
	                        TextView tt2 = (TextView) v.findViewById(R.id.cri2toptext2);
							tt2.setText( "  Tag: "+o.PurchaseStockTag+" " );
							
							// Best Bfore
	                        TextView tt3 = (TextView) v.findViewById(R.id.cri2toptext3);
							tt3.setText( "  Best Before: "+o.PurchaseBestBefore );
							
							// Qty
	                        TextView bt = (TextView) v.findViewById(R.id.cri2bottomtext);
							bt.setText( "  Supplier: "+o.PurchaseSupplier);
							
	                        tt2.setVisibility(View.VISIBLE);
	                        tt3.setVisibility(View.VISIBLE);
    					}
                }
                return v;
        }

	}


}






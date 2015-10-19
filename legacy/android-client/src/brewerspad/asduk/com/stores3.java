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
   
$Revision: 1.17 $ $Date: 2011-10-25 10:25:47 $ $Author: codemonkey $


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
import android.os.Bundle;
import android.os.Environment;

import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;






public class stores3 extends Activity {
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
	
	ListView listViewPurchases;
	ListView listViewB2; 
	
	ArrayAdapter purchasesArrayAdapter;
	
	String selectedItem;
	String selectedCategory;
	String selectedTag;
	

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.storesphone3);

		// set outselves as a boss in storecommon
		storeLogic.stores3Boss = this;
		storeLogic.boss = "stores3";

		
		commonOnCreate();
	}
	
	public void commonOnCreate(){
				Log.i("brewerspad","stores/onCreate3()");

		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		tabletDevice = shrdPrefs.getBoolean("tabletDevice", true);
		hostnameIp = shrdPrefs.getString("hostname", "127.0.0.1");
		portNum = shrdPrefs.getInt("portNumber", 1027);
		

	
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
		View view = findViewById(R.id.storesOuterLayoutPage3);
		view.setBackgroundDrawable(bd);


        
    	// add list view for purchases
		listViewPurchases = new ListView(this);
		listViewPurchases.setFocusableInTouchMode(true);
		Log.i("brewerspad", "store3.listViewa");
		storeLogic.listViewPurchases = new ArrayList<stockitem>();
		Log.i("brewerspad", "store3.listViewb");
		purchasesArrayAdapter = new  PurchaseItemAdapter(this, R.layout.customrowitem3, storeLogic.listViewPurchases);
		Log.i("brewerspad", "store3.listViewc");
		listViewPurchases.setAdapter(purchasesArrayAdapter);
		Log.i("brewerspad", "store3.listViewd");
		listViewPurchases.setClickable(true);
		
		//http://android.konreu.com/developer-how-to/click-long-press-event-listeners-list-activity/
		listViewPurchases.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {



			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				stockitem o = (stockitem) listViewPurchases.getItemAtPosition(arg2);
				Log.i("brewerspad", "store3.listview long Selected" + o.StockName+" "+o.StockCategory);
				
				selectedItem=o.StockName;
				selectedCategory=o.StockCategory;
				selectedTag=o.PurchaseStockTag;
				askForNewQty(o.StockCategory,o.StockName);
				
				return false;
			}
		});					 
		

		
		
		
    	Log.i("brewerspad","stores3[2]");
    	
        //
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.storesInner3of3);
        linearLayout.addView(listViewPurchases, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

        
        // Update List object 
        
		// we have writte this to 
		//Log.i("brewerspad","stores3.readingthe following jsonresponse" + shrdPrefs.getString("jsonresponse", "127.0.0.1"));
        
        // note this updateStockDetail is the important one!
        
		if( storeLogic.updateStockDetail(  shrdPrefs.getString("jsonresponseB", "nojsonresponse-b") )) 	{
			DecimalFormat df = new DecimalFormat("##0.00");
			purchasesArrayAdapter.notifyDataSetChanged();
			
			TextView tv = (TextView) findViewById(R.id.storesTitle3);
			tv.setText( storeLogic.StockItem.StockName+" ("+storeLogic.StockItem.StockCategory+")");
			Log.i("brewerspad","stores3[3] ");
			
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
			
		}else{
			presentError(storeLogic.exceptionTitle, storeLogic.exception, storeLogic.exceptionResponse);
			Log.i("brewerspad","stores[3]returning an exception");
		}
		
    	Log.i("brewerspad","stores3[4]");

	}


	public void onClickAddPurchase(View v){
		Log.i("brewerspad","onClickSaveComment");
		
		
		startActivity(new Intent(this, storepurchase.class));
	}

	

	public void askForNewQty(String stockCategory, String stockItem){
		
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
		alert.setView(input);
		//input.setInputType(2);	// TYPE CLASS NUMBER
		input.setInputType(8192);	// TYPE CLASS NUMBER
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			Log.i("brewerspad","Do something to get text here");
			String value = input.getText().toString();
			Log.i("brewerspad","got a value"+value);
		  // Do something with value!


			storexmlrpc task = new storexmlrpc();
			task.them = storeLogic;
			task.hostnameIp = hostnameIp;
			task.portNum = portNum;
			//DownloadWebPageTask task = new DownloadWebPageTask();				
			task.execute("changeItemQty",selectedCategory,selectedItem,selectedTag,value);

			
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
	

	
	
	public void itemQtyPostUpdate(String jsonResponse){
		Log.i("brewerspad","stores3.itemQtyPostUpdate()");
		//Toast.makeText(this,"Need to Refresh",Toast.LENGTH_LONG).show();
		Log.i("brewerspad","about to commonOnCreate");
		storeLogic.listViewPurchases.clear();
		purchasesArrayAdapter.notifyDataSetChanged();
		
		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		SharedPreferences.Editor editor = shrdPrefs.edit();
		editor.putString("jsonresponseB", jsonResponse);
		editor.commit();

		
		commonOnCreate();
		Log.i("brewerspad","have done commonOnCreatae");

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
					
                        
                }
                return v;
        }
}


	
	
}






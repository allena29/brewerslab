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

 $Revision: 1.6 $ $Date: 2011-10-16 14:43:47 $ $Author: codemonkey $


 */

import java.util.ArrayList;
import java.util.Date;
import android.app.Activity;
import android.app.ProgressDialog;

import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class storepurchase extends Activity {

	Integer dbg = 0;

	ProgressDialog pleasewait;

	// common
	storescommon storeLogic = new storescommon();
	ArrayAdapter suppliersArrayAdapter;
	ArrayAdapter itemsArrayAdapter;

	// this will be populated from sharedprefs
	String hostnameIp;
	Integer portNum;
	Boolean tabletDevice;

	Boolean APIerror = false;
	Boolean debugXMLRPC = true;

	DisplayMetrics dm;

	ListView listViewPurchases;
	ListView listViewB2;

	ArrayAdapter purchasesArrayAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.storepurchase);

		// set outselves as a boss in storecommon
		storeLogic.storepurchaseBoss = this;
		storeLogic.boss = "storepurchase";

		Log.i("brewerspad", "storesAddPurchase()");

		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		tabletDevice = shrdPrefs.getBoolean("tabletDevice", true);
		hostnameIp = shrdPrefs.getString("hostname", "127.0.0.1");
		portNum = shrdPrefs.getInt("portNumber", 1027);

		// set data on common object
		storeLogic.pleasewait = pleasewait;
		storeLogic.tabletDevice = tabletDevice;

		String stockCategory = shrdPrefs.getString("presetcategory",
				"<category>");
		String stockItem = shrdPrefs.getString("presetitem", "<item>");

		TextView tv = (TextView) findViewById(R.id.store4title);
		tv.setText(stockCategory);

		Log.i("brewerspad", "stoperpurchase[0]");
		/*
		 * storexmlrpc task = new storexmlrpc(); task.them = storeLogic; //
		 * hopefully this works task.hostnameIp = hostnameIp; task.portNum =
		 * portNum; task.execute("listIngredientsAndSuppliers", stockCategory,
		 * stockItem);
		 */

		Spinner suppliers = (Spinner) findViewById(R.id.store4supplierSpinner);
		storeLogic.supplierArrayData = new ArrayList<CharSequence>();
		suppliersArrayAdapter = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item,
				storeLogic.supplierArrayData);
		// ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
		// this, R.array.planets_array, android.R.layout.simple_spinner_item);
		suppliersArrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// suppliers.setAdapter(adapter);
		suppliers.setAdapter(suppliersArrayAdapter);

		suppliers.setClickable(true);

		Spinner items = (Spinner) findViewById(R.id.store4itemsSpinner);
		storeLogic.itemsArrayData = new ArrayList<CharSequence>();
		itemsArrayAdapter = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, storeLogic.itemsArrayData);
		itemsArrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		items.setAdapter(itemsArrayAdapter);
		items.setClickable(true);

		// suppliers.setAdapter(suppliersArrayAdapter);//

		// Download the list of recipes
		fluffycloud cloudtask = new fluffycloud();
		cloudtask.themStore = storeLogic;
		cloudtask.themId = "stores"; // this is without a doubt based on the
										// common not the screen

		cloudtask.hostnameIp = hostnameIp;
		cloudtask.cloudKey = shrdPrefs.getString("cloudkey", "expired");
		cloudtask.cloudDevice = shrdPrefs.getString("clouddevice",
				"<testdevice>");
		cloudtask.cloudUser = shrdPrefs.getString("clouduser", "<user>");
		cloudtask.portNum = portNum + 1;
		cloudtask.execute("listIngredientsAndSuppliers", stockCategory,
				stockItem);

		Log.i("brewerspad", "storespurchase[1]");

		long epoch = System.currentTimeMillis() / 1000;
		epoch = epoch + (86400 * 365);
		// 365000);

		Date expiry = new Date(epoch * 1000);

		Integer year = 1901 + expiry.getYear();
		Integer month = expiry.getMonth();
		Integer day = expiry.getDate();
		// Toast.makeText(this,"Date:   "+year+" "+month+" "+day,Toast.LENGTH_LONG).show();
		DatePicker dinput = (DatePicker) findViewById(R.id.store4bestbefore);
		dinput.updateDate(year, month, day);

		
	}

	public void store4addpurchaseOnClick(View v) {
		Log.i("brewerspad", "store4addpurchaseOnClick.onClick()");
		
		EditText input;

		input = (EditText) findViewById(R.id.store4qty);
		String qtyvalue = input.getText().toString();
		input = (EditText) findViewById(R.id.store4cost);
		String costvalue = input.getText().toString();
		input = (EditText) findViewById(R.id.store4packsPurchased);
		String packspurchasevalue = input.getText().toString();
		input = (EditText) findViewById(R.id.store4hopalpha);
		String hopalphavalue = input.getText().toString();

		// Toast.makeText(this," qtyvalue"+qtyvalue+"\n",Toast.LENGTH_LONG).show();

		DatePicker dinput = (DatePicker) findViewById(R.id.store4bestbefore);
		Integer day = dinput.getDayOfMonth();
		Integer month = dinput.getMonth() + 1;
		Integer year = dinput.getYear();

		// Toast.makeText(this," date"+day+"-"+month+"-"+year+"\n",Toast.LENGTH_LONG).show();

		Spinner sinput = (Spinner) findViewById(R.id.store4supplierSpinner);
		Integer supplier = sinput.getSelectedItemPosition();
		//Toast.makeText(this, ""+sinput.getSelectedItem(),Toast.LENGTH_LONG).show();
		String supplierText  = (String) sinput.getSelectedItem();
		sinput = (Spinner) findViewById(R.id.store4itemsSpinner);
		Integer itemid = sinput.getSelectedItemPosition();
		String itemText  = (String) sinput.getSelectedItem();
		
		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);

		// Download the list of recipes
		fluffycloud cloudtask = new fluffycloud();
		cloudtask.themStore = storeLogic;
		cloudtask.themId = "stores"; // this is without a doubt based on the
										// common not the screen

		cloudtask.hostnameIp = hostnameIp;
		cloudtask.cloudKey = shrdPrefs.getString("cloudkey", "expired");
		cloudtask.cloudDevice = shrdPrefs.getString("clouddevice",
				"<testdevice>");
		cloudtask.cloudUser = shrdPrefs.getString("clouduser", "<user>");
		cloudtask.portNum = portNum + 1;

		String category = shrdPrefs.getString("presetcategory", "<category>");

		
		cloudtask.execute("addNewPurchase", category, itemText,
				qtyvalue, costvalue, day.toString(), month.toString(),
				year.toString(), supplierText, packspurchasevalue,
				hopalphavalue);

		// Toast.makeText(this," date"+day+"-"+month+"-"+year+"\n",Toast.LENGTH_LONG).show();

		pleasewait();

	}

	public void purchasePostUpdate() {
		kickGui();
		
		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		SharedPreferences.Editor editor = shrdPrefs.edit();
		editor.putBoolean("refreshstore", true);
		editor.commit();
		finish();

	}

	public void populateIngredientsAndSuppliersPostUpdate(String category) {

		// storeLogic.supplierArrayData.add("test");
		suppliersArrayAdapter.notifyDataSetChanged();
		itemsArrayAdapter.notifyDataSetChanged();
		// Toast.makeText(this,"HERE AS WELL", Toast.LENGTH_LONG).show();

		/*
		 * the way that we make sure the keyboard doesn't pop up is to set them
		 * as non focusable but now we need to reverse that.
		 */
		EditText et;
		et = (EditText) findViewById(R.id.store4qty);
		et.setFocusableInTouchMode(true);
		et.setFocusable(true);
		et = (EditText) findViewById(R.id.store4cost);
		et.setFocusableInTouchMode(true);
		et.setFocusable(true);
		et = (EditText) findViewById(R.id.store4packsPurchased);
		et.setFocusableInTouchMode(true);
		et.setFocusable(true);
		et = (EditText) findViewById(R.id.store4hopalpha);
		et.setFocusableInTouchMode(true);
		et.setFocusable(true);

		if (category.equals("Hops")) {
			TextView tv = (TextView) findViewById(R.id.store4hopalphaLabel);
			tv.setVisibility(View.VISIBLE);
			et = (EditText) findViewById(R.id.store4hopalpha);
			et.setVisibility(View.VISIBLE);
		} else {
			TextView tv = (TextView) findViewById(R.id.store4hopalphaLabel);
			tv.setVisibility(View.GONE);
			et = (EditText) findViewById(R.id.store4hopalpha);
			et.setVisibility(View.GONE);
		}
	}

	public void kickGui() {
		if (storeLogic.pleasewaitActive) {
			storeLogic.pleasewait.dismiss();
			storeLogic.pleasewaitActive = false;
		}

		/*
		 * // this is part of the pattern but not mandatory if(tabletDevice &&
		 * getWindowManager().getDefaultDisplay().getWidth() >
		 * getWindowManager().getDefaultDisplay().getHeight()){
		 * 
		 * tabletOnResume(); }else{ phoneOnResume(); }
		 */
	}

	public void pleasewait() {
		// progressbar
		storeLogic.pleasewait = ProgressDialog.show(this, "",
				"Please Wait Connecting to the cloud", true, true);
		storeLogic.pleasewaitActive = true;
	}

	public void presentError(String title, String exception, String jsonResponse) {

		// Toast.makeText(this,
		// "Unable to communicate with the brewerspad server.",
		// Toast.LENGTH_LONG).show();

		Log.i("brewerspad",
				"________________________________________________________________________");
		Log.i("brewerspad", "		Error/Exception in " + title);
		Log.i("brewerspad", exception);
		Log.i("brewerspad", jsonResponse);
		Log.i("brewerspad",
				"________________________________________________________________________");
		if (debugXMLRPC == true) {
			SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
			SharedPreferences.Editor editor = shrdPrefs.edit();
			editor.putString("errorlocation", title);
			editor.putString("exception", exception);
			editor.putString("jsonresponse", jsonResponse);
			editor.commit();
			startActivity(new Intent(this, debugxmlrpc.class));
		}

	}

}

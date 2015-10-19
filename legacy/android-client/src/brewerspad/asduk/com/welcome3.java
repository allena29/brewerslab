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

import java.text.DecimalFormat;
import java.util.ArrayList;

import brewerspad.asduk.com.welcomecommon.brewlogitem;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class welcome3 extends Activity {

	Boolean APIerror = false;

	Integer dbg = 0;

	ProgressDialog pleasewait;

	// common
	welcomecommon welcomeLogic = new welcomecommon();

	// this will be populated from sharedprefs
	String hostnameIp;
	Integer portNum;
	Boolean tabletDevice;

	Boolean debugXMLRPC = true;

	DisplayMetrics dm;

	BrewlogItemAdapterImg brewlogAdapter;

	ListView listViewBrewlogItems;
	ListView listViewB2;

	@Override
	public void onPause(){
		super.onPause();
		

		if (welcomeLogic.pleasewaitActive) {
			welcomeLogic.pleasewaitActive = false;
			welcomeLogic.pleasewait.dismiss();
		}

	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("brewerspad", "welcome2/onCreate()");

		// set outselves as a boss in storecommon
		welcomeLogic.welcome3Boss = this;
		welcomeLogic.boss = "welcome3";

		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		welcomeLogic.cloudKey = shrdPrefs.getString("cloudkey", "expired");
		welcomeLogic.cloudUser = shrdPrefs.getString("clouduser", "expired");
		welcomeLogic.cloudDevice = "androidApp";

		tabletDevice = shrdPrefs.getBoolean("tabletDevice", true);
		hostnameIp = shrdPrefs.getString("hostname", "127.0.0.1");
		portNum = shrdPrefs.getInt("portNumber", 1027);

		setContentView(R.layout.welcomephone3);

		// set data on common object
		welcomeLogic.pleasewait = pleasewait;
		welcomeLogic.tabletDevice = tabletDevice;

		// Get Screen Dimensions
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		// show the background image on the layout
		String path = Environment.getExternalStorageDirectory().toString();
		String pathName = path + "/brewerspad/welcomebg.png";
		Resources res = getResources();
		Bitmap bitmap = BitmapFactory.decodeFile(pathName);
		BitmapDrawable bd = new BitmapDrawable(res, bitmap);
		View view = findViewById(R.id.welcomeOuterLayoutPage3);
		view.setBackgroundDrawable(bd);

		// add list view for steps
		listViewBrewlogItems = new ListView(this);
		listViewBrewlogItems.setFocusableInTouchMode(true);
		Log.i("brewerspad", "welcome3.listViewa");
		welcomeLogic.brewlogActivityListData = new ArrayList<brewlogitem>();
		brewlogAdapter = new BrewlogItemAdapterImg(this,
				R.layout.customrowitem5, welcomeLogic.brewlogActivityListData);
		listViewBrewlogItems.setAdapter(brewlogAdapter);
		listViewBrewlogItems.setClickable(true);
		listViewBrewlogItems
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						brewlogitem bi = (brewlogitem) listViewBrewlogItems
								.getItemAtPosition(arg2);
						Log.i("brewerspad", "welcome3.listview Selected"
								+ bi.BrewlogName);
						Log.i("brewerspad)",
								"openBrewLog clicked about to start intent");
						SharedPreferences shrdPrefs = getSharedPreferences(
								"activity", 0);
						SharedPreferences.Editor editor = shrdPrefs.edit();


						editor = shrdPrefs.edit();
						editor.putString("brewlog", bi.BrewlogName);
						editor.putString("process",bi.ProcessName);
						editor.putString("recipe", bi.RecipeName);
						editor.putString("activity", bi.ActivityName);
						editor.commit();

						pleasewait();
						
						startBrewday();

						


					}
				});

		Log.i("brewerspad", "welcome3[2]");

		//

		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.welcomeInner3of3);
		linearLayout.addView(listViewBrewlogItems,
				new LinearLayout.LayoutParams(
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

		// Update List object

		if (welcomeLogic.listActivitiesFromBrewlog(shrdPrefs.getString(		"jsonresponseB", "127.0.0.1"))) {
			brewlogAdapter.notifyDataSetChanged();
			TextView tv = (TextView) findViewById(R.id.welcomeTitle3);
			tv.setText(welcomeLogic.recipeName);
			Log.i("brewerspad", "welcome2[3] - commented out");
		} else {
			presentError(welcomeLogic.exceptionTitle, welcomeLogic.exception,
					welcomeLogic.exceptionResponse);
			Log.i("brewerspad", "returning an exception");
		}

		Log.i("brewerspad", "welcome[4]");

	}

	
	public void pleasewait() {
		// progressbar
		welcomeLogic.pleasewait = ProgressDialog.show(this, "",
				"Please Wait Connecting to the cloud", true, true);
		welcomeLogic.pleasewaitActive = true;
	}

	public void startBrewday() {
		Log.i("brewerspad", "  start brewday for the phone");
		startActivity(new Intent(this, brewday.class));
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

		// what about progress bars as we transition between things

	}

	public class BrewlogItemAdapterImg extends ArrayAdapter<brewlogitem> {

		private ArrayList<brewlogitem> items;

		public BrewlogItemAdapterImg(Context context, int textViewResourceId,
				ArrayList<brewlogitem> items) {
			super(context, textViewResourceId, items);

			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.i("brewerspad",
					"in grtView of BrewlogItemAdapterImg -- new version");
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.customrowitem5, null);

			}

			brewlogitem o = items.get(position);
			if (o != null) {
				ImageView ii = (ImageView) v.findViewById(R.id.iconif);

				if (o.Complete) {
					ii.setImageResource(R.drawable.greytick);
				} else {
					ii.setImageResource(R.drawable.spacer);
				}

				TextView tt = (TextView) v.findViewById(R.id.toptext);
				tt.setText("" + o.Display);

			}
			Log.i("brewerspad", "end of BrewlogItemAdapterImg -- new version");
			return v;
		}
	}

}

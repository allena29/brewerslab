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

 $Revision: 1.30 $ $Date: 2011-11-04 22:55:46 $ $Author: codemonkey $


 */
import java.io.File;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import brewerspad.asduk.com.welcomecommon.brewlogitem;
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

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class welcome extends Activity {

	Boolean APIerror = false;
	Boolean APIsuccess = false;

	ArrayAdapter recipeListViewAdapter;

	String hostnameIp;
	Integer portNum;
	Boolean tabletDevice;
	DisplayMetrics dm;
	String dbgAutoLaunchScreen = "brewday"; // brewd ay is an option
	// stores is also an option
	// recipe is an options
	// stores2 providing jsonresponse is sotres
	// stores3 is an option providing the jsonresponse2 of stores is prsent
	// storespurchase is an option providing the presetcategory/presettem is set

	// common
	welcomecommon welcomeLogic = new welcomecommon();

	Boolean successfulApi = false;
	Boolean debugXMLRPC = true;

	@Override
	@SuppressWarnings("unchecked")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i("brewerspad", "about to open layout - onCreate() welcome.java");
		setContentView(R.layout.welcome);
		Log.i("brewerspad", "have opened layout");

		// Set so that we don't close ourself
		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		SharedPreferences.Editor editor = shrdPrefs.edit();
		editor.putBoolean("forceAppClose", false);
		editor.commit();

		// Setup our common/logics
		welcomeLogic.welcomeBoss = this;
		welcomeLogic.boss = "welcome";
		// the common logic needs to be updated
		welcomeLogic.cloudKey = shrdPrefs.getString("cloudkey", "expired");
		welcomeLogic.cloudUser = shrdPrefs.getString("clouduser", "expired");
		welcomeLogic.cloudDevice = "androidApp";

		// Determine if we are a tablet
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		if (dm.widthPixels > 959 && dm.heightPixels > 719) {
			tabletDevice = true;
			Log.i("brewerspad", "width: " + dm.widthPixels + "height: "
					+ dm.heightPixels + " = tablet");
			// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			tabletDevice = false;
			Log.i("brewerspad", "width: " + dm.widthPixels + "height: "
					+ dm.heightPixels + " = non-tablet");
		}

		editor = shrdPrefs.edit();
		welcomeLogic.tabletDevice = tabletDevice;
		editor.putBoolean("tabletDevice", tabletDevice);
		editor.commit();

		String android_id = Secure.getString(this.getContentResolver(),
				Secure.ANDROID_ID);
		Log.i("brewerspad", "android_id" + android_id);

		hostnameIp = ""; // removeFromDist
		portNum = 64666;
		// removeFromDist
		if (android_id.equals("36fcfd6f4a14ba66")) { // emulator id cyanogenmod
			// //removeFromDist
			hostnameIp = "eleven.mellon-collie.net"; // removeFromDist
			portNum = 54659; // removeFromDist
			editor = shrdPrefs.edit(); // removeFromDist
			editor.putBoolean("internalDebug", true); // removeFromDist
			editor.commit(); // removeFromDist

		} else if (android_id.equals("950457d795bbd7d")) { // seems to be
			// emulator tablet
			// id
			// //removeFromDist
			hostnameIp = "gae.mellon-collie.net"; // removeFromDist
			hostnameIp = "seventytwo.mellon-collie.net"; // removeFromDist
			hostnameIp = "eleven.mellon-collie.net"; // removeFromDist
			portNum = 54659; // removeFromDist
			editor = shrdPrefs.edit(); // removeFromDist
			editor.putBoolean("internalDebug", true); // removeFromDist
			editor.commit(); // removeFromDist
		} else if (android_id.equals("28c54fa7a37f972")) { // this is my phone
			// with cyangenomod
			// //removeFromDist
			hostnameIp = "sixtyfour.mellon-collie.net"; // removeFromDist
			portNum = 54659; // removeFromDist
			dbgAutoLaunchScreen = ""; // removeFromDist
			editor = shrdPrefs.edit(); // removeFromDist
			editor.putBoolean("internalDebug", true); // removeFromDist
			editor.commit(); // removeFromDist
		} else if (android_id.equals("a961349f3ccd78cb")) { // this is my tablet
			// //removeFromDist
			hostnameIp = "sixtyfour.mellon-collie.net"; // removeFromDist
			// hostnameIp = "eleven.mellon-collie.net"; // removeFromDist
			// hostnameIp = "gaedev.mellon-collie.net"; //removeFromDist
			portNum = 54659; // removeFromDist
			// dbgAutoLaunchScreen=""; //removeFromDist
			editor = shrdPrefs.edit(); // removeFromDist
			editor.putBoolean("internalDebug", true); // removeFromDist
			editor.commit(); // removeFromDist
		} else { // removeFromDist
			dbgAutoLaunchScreen = ""; // removeFromDist
		} // removeFromDist

		// hostnameIp = "gae.mellon-collie.net"; // removeFromD
		/*
		 * 
		 * Allow custom urls
		 */
		if (prefs.getHostname(this).equals("127.0.0.1")
				&& hostnameIp.length() == 0) {
			Toast.makeText(
					this,
					"Please set the Hostname and Port number of the brewerslab server. \n\nThe app will close after entering these details.",
					Toast.LENGTH_LONG).show();
			editor = shrdPrefs.edit();
			editor.putBoolean("forceAppClose", true);
			editor.commit();
			startActivity(new Intent(this, prefs.class));
			return;
		} else if (hostnameIp.length() == 0) {
			hostnameIp = prefs.getHostname(this);
			portNum = Integer.parseInt(prefs.getPort(this));
		}
		Log.i("brewerspad", "setting hostname/port to " + hostnameIp + ":"
				+ portNum);

		/*
		 * 
		 * Cloud enabling - if we don't have a cloudkey then we should launch a
		 * browser
		 */
		try {
			// get intent data or throw an exception
			Log.i("brewerspad", "intent getting data");
			Uri data = getIntent().getData();
			String scheme = data.getScheme(); // "http"
			String host = data.getHost(); // "twitter.com"
			List<String> params = data.getPathSegments();
			Log.i("brewerspad", "intent scheme" + scheme);
			Log.i("brewerspad", "intent host" + host);
			Log.i("brewerspad",
					"intent getting data params.size()" + params.size());
			if (params.size() == 3) {
				if (params.get(0).equals("authcookie")) {
					editor = shrdPrefs.edit();
					editor.putString("cloudkey", params.get(1));
					Log.i("brewerspad", " setting cloudkey " + params.get(1));
					editor.putString("clouduser", params.get(2));
					Log.i("brewerspad", " setting clouduser " + params.get(2));
					editor.putString("clouddevice", "androidApp");

					// the common logic needs to be updated
					welcomeLogic.cloudKey = params.get(1);
					welcomeLogic.cloudUser = params.get(2);
					welcomeLogic.cloudDevice = "androidApp";

					editor.commit();
					Toast.makeText(this,
							"Updated Cloud Authentication Details",
							Toast.LENGTH_LONG).show();
				}
			}

		} catch (Exception e) {
			Log.i("brewerspad", "Exception while getting intent information");
			Log.i("brewerspad", "exception" + e.getMessage());

		}

		Log.i("brewerspad",
				"cloudkey is set to"
						+ shrdPrefs.getString("cloudkey", "default value"));
		if (shrdPrefs.getString("cloudkey", "expired").equals("expired")) {

			editor = shrdPrefs.edit();
			editor.putBoolean("forceAppClose", true);
			editor.commit();
			reAuthorise();
			return;
		}
		// hostnameIp = "gae.mellon-collie.net"; // removeFromDist
		/*
		 * Download background images
		 */
		Log.i("brewerspad",
				"internalDebug version"
						+ shrdPrefs.getBoolean("internalDebug", false));

		// check if we need resources to be downloaded so that we can keep the
		// size of the .apk down
		if (!checkInitialResources()) {

			/*
			 * Default this will come from the front screen gui in the future
			 * for now decodeData() will call out for the brewerday activity
			 */

			editor = shrdPrefs.edit();
			editor.putString("hostname", hostnameIp);
			editor.putInt("portNumber", portNum);
			editor.commit();
			welcomeLogic.hostnameIp = hostnameIp;
			welcomeLogic.portNum = portNum;

			if (dbgAutoLaunchScreen.length() > 0) {

				// Temporary to auto-launch the breday page
				if (dbgAutoLaunchScreen.equals("brewday")) {
					editor = shrdPrefs.edit();
					editor.putString("brewlog", "2014_2_10");
					editor.putString("process", "22AG19i20");
					editor.putString("recipe", "Citra13");
					// editor.putString("activity", "Bottling/Kegging");
					// editor.putString("activity", "Post Brewday");
					editor.putString("activity", "Brewday");
					editor.commit();

					startActivity(new Intent(this, brewday.class));
				}
				if (dbgAutoLaunchScreen.equals("stores")) {
					startActivity(new Intent(this, stores.class));
				}
				if (dbgAutoLaunchScreen.equals("stores2")) {
					startActivity(new Intent(this, stores2.class));
				}
				if (dbgAutoLaunchScreen.equals("stores3")) {
					startActivity(new Intent(this, stores3.class));
				}
				if (dbgAutoLaunchScreen.equals("storespurchase")) {
					startActivity(new Intent(this, storepurchase.class));
				}
				if (dbgAutoLaunchScreen.equals("recipe")) {
					editor = shrdPrefs.edit();
					editor.putString("recipename", "Dark Green Goose");
					editor.commit();

					startActivity(new Intent(this, recipe.class));
				}

			} else {

				reinitaliseGui();

			}

		}

	}

	public void reAuthorise() {
		Toast.makeText(
				this,
				"Please sign in to authenticate your device.",
				Toast.LENGTH_LONG).show();
		Log.i("brewerspad", "Expired cloudkey authentication cookied: "
				+ hostnameIp + ":" + (portNum + 1) + "/authorise/");
		Uri uriUrl = Uri.parse("http://" + hostnameIp + ":" + (portNum + 1)
				+ "/ngauthorise.py?authorise=yes&deviceId=androidApp");
		Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
		startActivity(launchBrowser);

	}

	public void reinitaliseGui() {
		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		SharedPreferences.Editor editor = shrdPrefs.edit();

		// show the background image on the layout
		String path = Environment.getExternalStorageDirectory().toString();
		String pathName = path + "/brewerspad/welcomebg.png.xml";
		Resources res = getResources();
		Bitmap bitmap = BitmapFactory.decodeFile(pathName);
		BitmapDrawable bd = new BitmapDrawable(res, bitmap);

		View view = findViewById(R.id.welcomeScreenOuterlayout);
		view.setBackgroundDrawable(bd);

		if (tabletDevice) {
			tabletOnCreate();
		} else {
		}
		phoneOnCreate();

		/*
		 * 
		 * If our cloudkey isn't locally marked as expired then we need to
		 * connect to the web to determine if our key has expired or not.
		 * 
		 * Overload this with list recipes
		 */

		pleasewait();

		// Download the list of recipes
		fluffycloud cloudtask = new fluffycloud();
		cloudtask.themWelcome = welcomeLogic;
		cloudtask.themId = "welcome";
		cloudtask.hostnameIp = hostnameIp;
		cloudtask.cloudKey = shrdPrefs.getString("cloudkey", "expired");
		cloudtask.cloudDevice = shrdPrefs.getString("clouddevice",
				"<testdevice>");
		cloudtask.cloudUser = shrdPrefs.getString("clouduser", "<user>");
		cloudtask.portNum = portNum + 1;
		cloudtask.execute("listRecipes");

		final ListView listView = (ListView) findViewById(R.id.welcomeRecipeslistView);
		welcomeLogic.recipeListData = new ArrayList<String>();

		welcomeLogic.recipesAdapter = new ArrayAdapter(this,
				android.R.layout.simple_list_item_1,
				welcomeLogic.recipeListData);
		listView.setAdapter(welcomeLogic.recipesAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				// Toast.makeText(this," BREWLOGNEAME"+o.BrewlogName,Toast.LENGTH_LONG).show();
				String o = (String) listView.getItemAtPosition(arg2);
				welcomeLogic.recipeName = o;

				// progressbar
				pleasewait();

				welcomeLogic.nextStepFromRecipe(o);
				if (tabletDevice) {
					TextView tv = (TextView) findViewById(R.id.welcomeTitle2of3);
					tv.setVisibility(View.VISIBLE);
					tv.setText("Brewlogs for " + o);
				}

			}
		});
		Log.i("brewerspad", "this is experimental now");

		// tabletDevice only
		if (tabletDevice) {
			final ListView listView2 = (ListView) findViewById(R.id.welcomeBrewlogslistView);
			welcomeLogic.brewlogsAdapter2 = new BrewlogItemAdapterImg(this,
					R.layout.customrowitem5, welcomeLogic.brewlogsListData);
			listView2.setAdapter(welcomeLogic.brewlogsAdapter2);
			// welcomeLogic.brewlogsAdapter = new ArrayAdapter(this,
			// android.R.layout.simple_expandable_list_item_1,
			// welcomeLogic.brewlogsListData);
			// listView2.setAdapter(welcomeLogic.brewlogsAdapter);
			listView2
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// this is only resolved by an import like import
							// brewerspad.asduk.com.welcomecommon.brewlogitem
							Log.i("brewerspad",
									"in on click buton haven't gotItemAtPosition yet");

							brewlogitem o = (brewlogitem) listView2
									.getItemAtPosition(arg2);

							// progressbar
							pleasewait();

							Log.i("brewerspad", "This worked");
							welcomeLogic.nextStepFromBrewlog(o);

							if (o.BrewlogName.equals("<New Brewlog>")
									|| o.BrewlogName.equals("<View Recipe>")
									|| o.BrewlogName.equals("<Clone Recipe>")) {
								TextView tv = (TextView) findViewById(R.id.welcomeTitle3of3);
								tv.setVisibility(View.INVISIBLE);
							} else {
								TextView tv = (TextView) findViewById(R.id.welcomeTitle3of3);
								tv.setVisibility(View.VISIBLE);
								tv.setText("Activities in " + o.BrewlogName);
								Log.i("brewerspad",
										"should have listview3 next");
							}
						}
					});

			final ListView listView3 = (ListView) findViewById(R.id.welcomeActivitieslistView);
			welcomeLogic.brewlogActivityAdapterImg = new BrewlogItemAdapterImg(
					this, R.layout.customrowitem5,
					welcomeLogic.brewlogActivityListData);
			listView3.setAdapter(welcomeLogic.brewlogActivityAdapterImg);
			Log.i("brewerspad", "not sure if this is getting added or not");
			listView3
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// this is only resolved by an import like import
							// brewerspad.asduk.com.welcomecommon.brewlogitem
							brewlogitem o = (brewlogitem) listView3
									.getItemAtPosition(arg2);

							Log.i("brewerspad",
									"This worked listView3 onClicked<<<<<<<<<<<<<<<<<<<<<<<");
							openBrewLog(o);

						}
					});

		}

	}

	public void cloneRecipe(String oldRecipe) {
		//
		Log.i("brewerspad", "cloneRecipe");

		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Clone " + oldRecipe);
		alert.setMessage("Enter new recipe name");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		input.setText("new recipe");
		input.setTag(oldRecipe);
		final LinearLayout ll = new LinearLayout(this);
		ll.setGravity(Gravity.VERTICAL_GRAVITY_MASK);
		ll.addView(input);

		alert.setView(ll);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				// new
				fluffycloud cloudtask = new fluffycloud();
				cloudtask.themWelcome = welcomeLogic;
				cloudtask.themId = "welcome";
				cloudtask.hostnameIp = hostnameIp;
				cloudtask.cloudKey = welcomeLogic.cloudKey;
				cloudtask.cloudDevice = welcomeLogic.cloudDevice;
				cloudtask.cloudUser = welcomeLogic.cloudUser;
				cloudtask.portNum = portNum + 1;
				cloudtask.execute("cloneRecipe", input.getTag().toString(),
						input.getText().toString());

			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
						Log.i("brewerspad", "ignored input dialog");
						kickGui();
					}
				});

		alert.show();
		// see http://androidsnippets.com/prompt-user-input-with-an-alertdialog

	}

	public void outOfStockWarning(String jsonresponse) {
		Log.i("brewerspad", "outofstockwarning neeeded");
		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		SharedPreferences.Editor editor = shrdPrefs.edit();

		editor = shrdPrefs.edit();
		editor.putString("jsonResponseZ", jsonresponse);

		editor.commit();

		startActivity(new Intent(this, outofstockwarning.class));
	}

	public void newBrewlog(String newRecipe) {
		//
		Log.i("brewerspad", "newBrewlog");

		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Create Brewlog for " + newRecipe);
		alert.setMessage("Enter new brewlog name");

		// Set an EditText view to get user input
		// final EditText input = new EditText(this);
		// input.setText("brewlog");
		// input.setTag(oldRecipe);

		long epoch = System.currentTimeMillis() / 1000;
		epoch = epoch + (86400 * 365);
		// epoch = epoch + (0);
		Date expiry = new Date(epoch * 1000);
		Integer year = 1900 + expiry.getYear();
		Integer month = expiry.getMonth();
		Integer day = expiry.getDate();
		final DatePicker dinput = new DatePicker(this);
		dinput.updateDate(year, month, day);
		dinput.setTag(newRecipe);

		final LinearLayout ll = new LinearLayout(this);
		ll.setGravity(Gravity.VERTICAL_GRAVITY_MASK);
		ll.addView(dinput);

		// Set an EditText view to get user input
		final Spinner input = new Spinner(this);
		alert.setView(input);

		// H
		final TextView tvt = new TextView(this);
		tvt.setText("Process:   ");

		final LinearLayout llhorizontal = new LinearLayout(this);
		llhorizontal.setGravity(Gravity.HORIZONTAL_GRAVITY_MASK);
		llhorizontal.addView(tvt);
		llhorizontal.addView(input);

		ArrayAdapter processArrayAdapter = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item,
				welcomeLogic.processArrayData);
		processArrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		input.setAdapter(processArrayAdapter);
		input.setClickable(true);

		ll.addView(llhorizontal);

		alert.setView(ll);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				Integer day = dinput.getDayOfMonth();
				Integer month = dinput.getMonth() + 1;
				Integer year = dinput.getYear();
				String dateString = year + "_" + month + "_" + day;
				String processText = (String) input.getSelectedItem();

				// new
				fluffycloud cloudtask = new fluffycloud();
				cloudtask.themWelcome = welcomeLogic;
				cloudtask.themId = "welcome";
				cloudtask.hostnameIp = hostnameIp;
				cloudtask.cloudKey = welcomeLogic.cloudKey;
				cloudtask.cloudDevice = welcomeLogic.cloudDevice;
				cloudtask.cloudUser = welcomeLogic.cloudUser;
				cloudtask.portNum = portNum + 1;
				cloudtask.execute("createBrewlogWrapper", dinput.getTag()
						.toString(), dateString, processText);

			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
						Log.i("brewerspad", "ignored input dialog");
						kickGui();
					}
				});

		alert.show();
		// see http://androidsnippets.com/prompt-user-input-with-an-alertdialog

	}

	public void openBrewLog(brewlogitem o) {
		Log.i("brewerspad)", "openBrewLog clicked about to start intent");
		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		SharedPreferences.Editor editor = shrdPrefs.edit();

		editor = shrdPrefs.edit();
		editor.putString("brewlog", o.BrewlogName);
		editor.putString("process", o.ProcessName);
		editor.putString("recipe", o.RecipeName);
		editor.putString("activity", o.ActivityName);
		editor.commit();

		pleasewait();

		startActivity(new Intent(this, brewday.class));

	}

	public void kickGui() {

		/*
		 * When first running onResume() we get width =0 height=0 for the outer
		 * linear layout this workaround will force refresh it
		 */

		Log.i("brewerspad", "welcome:kickGui()");
		if (tabletDevice
				&& getWindowManager().getDefaultDisplay().getWidth() > getWindowManager()
						.getDefaultDisplay().getHeight()) {
			tabletOnResume();
		} else {
			phoneOnResume();
		}

		// If we have a progress dialog box hide it.
		if (welcomeLogic.pleasewaitActive) {
			welcomeLogic.pleasewaitActive = false;
			welcomeLogic.pleasewait.dismiss();
		}

	}

	public void phoneOnCreate() {

		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.welcomeOuter1of3);
		LayoutParams frame = (LayoutParams) linearLayout.getLayoutParams();
		frame.width = dm.widthPixels;
		frame.height = dm.heightPixels;
		linearLayout.setLayoutParams(frame);

	}

	public void phoneOnResume() {

	}

	public void tabletOnResume() {

		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.welcomeScreenOuterlayout);
		Integer height = linearLayout.getHeight();
		Integer width = linearLayout.getWidth();

		// Outer Image
		// ll = (LinearLayout) findViewById(R.id.recipeHorizontalOuterview);
		// Integer thov = ll.getHeight();
		// Integer twov = ll.getWidth();

		// 1st third
		linearLayout = (LinearLayout) findViewById(R.id.welcomeOuter1of3);
		LayoutParams frame = (LayoutParams) linearLayout.getLayoutParams();
		frame.width = width / 3;
		frame.height = height; // don't use dm as it's the real display use the
		// outer container instead
		linearLayout.setLayoutParams(frame);

		// 2nd third
		linearLayout = (LinearLayout) findViewById(R.id.welcomeOuter2of3);
		frame = (LayoutParams) linearLayout.getLayoutParams();
		frame.height = height;
		frame.width = width / 3;
		linearLayout.setLayoutParams(frame);

		// 3rd third
		linearLayout = (LinearLayout) findViewById(R.id.welcomeOuter3of3);
		frame = (LayoutParams) linearLayout.getLayoutParams();
		frame.height = height;
		frame.width = width / 3;
		linearLayout.setLayoutParams(frame);

	}

	public void tabletOnCreate() {
		Log.i("brewerspad", "welcome:tabletOnCreate()");

		TextView tv = (TextView) findViewById(R.id.welcomeTitle2of3);
		tv.setVisibility(View.INVISIBLE);
		TextView tv2 = (TextView) findViewById(R.id.welcomeTitle3of3);
		tv2.setVisibility(View.INVISIBLE);
		tv.setText("Brew logs");
		tv2.setText("Activities");

		tabletOnResume();

	}

	@Override
	public void onResume() {
		super.onResume();

		Log.i("brewerspad", "BrewerspadActivity onResumse");
		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		if (shrdPrefs.getBoolean("forceAppClose", false) == true) {
			Log.i("brewerspad", "BrewerspadActivity to close app");
			if (shrdPrefs.getString("appCloseReason", "").equals(
					"imagedownloads")) {
				Toast.makeText(
						this,
						"Finished downloading images, now closing application.",
						Toast.LENGTH_LONG).show();
			}
			finish();
		} else {
			Log.i("brewerspad", "BrewerspadActivity not to close app");
		}

		// try put the app stuff in here
		if (tabletDevice
				&& getWindowManager().getDefaultDisplay().getWidth() > getWindowManager()
						.getDefaultDisplay().getHeight()) {
			tabletOnCreate();
		} else {
			phoneOnCreate();
		}

		// If we have a progress dialog box hide it.
		if (welcomeLogic.pleasewaitActive) {
			welcomeLogic.pleasewait.dismiss();
			welcomeLogic.pleasewaitActive = false;
		}

	}

	/*
	 * 
	 * 
	 * This bit here produces a menu. references res/menu/menu.xml
	 * 
	 * Don't forget to add this to the Manifest
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);

		return true;
	}

	/*
	 * 
	 * This is called whenever the User selects something on the menu contex t
	 * menu (i.e. menu key)
	 * 
	 * 
	 * Don't forget to add this to the Manifest
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		SharedPreferences.Editor editor = shrdPrefs.edit();
		editor = shrdPrefs.edit();

		switch (item.getItemId()) {

		case R.id.menuStores:
			startActivity(new Intent(this, stores.class));
			return true;
		case R.id.menuReauthorise:
			reAuthorise();
			return true;
		case R.id.menuReset:
			reinitaliseGui();
			return true;
		case R.id.menuSettings:
			startActivity(new Intent(this, prefs.class));
			return true;
		}

		return false;
	}

	public void pleasewait() {
		// progressbar
		welcomeLogic.pleasewait = ProgressDialog.show(this, "",
				"Please Wait Connecting to the cloud", true, true);
		welcomeLogic.pleasewaitActive = true;
	}

	public Boolean checkInitialResources() {
		/*
		 * Check if we have a number of initial resources and if we don't then
		 * download them
		 */
		Boolean needInitialResources = false;
		String filepath = Environment.getExternalStorageDirectory().toString();
		File file = new File(filepath + "/brewerspad/recipebg.png.xml");
		if (file.exists() == false) {
			needInitialResources = true;
			Log.i("brewerspad", "we don't already have recipebg");
		} else {
			Log.i("brewerspad", "we already have recipebg");
		}

		file = new File(filepath + "/brewerspad/storesbg.png.xml");
		if (file.exists() == false) {
			needInitialResources = true;
			Log.i("brewerspad", "we don't already have storesbg");
		}

		file = new File(filepath + "/brewerspad/brewlogbg.png.xml");
		if (file.exists() == false) {
			needInitialResources = true;
			Log.i("brewerspad", "we don't already have brewlogbg");
		} else {
			Log.i("brewerspad", "we already have brewlogbg");
		}

		file = new File(filepath + "/brewerspad/welcomebg.png.xml");
		if (file.exists() == false) {
			needInitialResources = true;
			Log.i("brewerspad", "we don't already have welcomebg");
		} else {
			Log.i("brewerspad", "we already have welcomebg");
		}

		if (needInitialResources) {
			Log.i("brewerspad", "need initial resources");

			// Toast.makeText(this,
			// "Downloading a number of resources for the first time. Once this is complete the app will need to be closed",
			// Toast.LENGTH_LONG).show();
			startActivity(new Intent(this, downloadlist.class));

		}
		return needInitialResources;

	}

	public void listActivitiesPostUpdate() {
		// Nothing to do here
		// Toast.makeText(this,"need to sort out gui",Toast.LENGTH_LONG).show();
		// TextView tv = (TextView) findViewById(R.id.welcomeTitle2of3);
		// tv.setVisibility(View.VISIBLE);

	}

	public void listBrewlogsPostUpdate(String recipe) {
		TextView tv = (TextView) findViewById(R.id.welcomeTitle2of3);
		tv.setVisibility(View.VISIBLE);

	}

	public void listRecipesPostUpdate(String jsonResponse) {
		recipeListViewAdapter.notifyDataSetChanged();
		successfulApi = true;

	}

	public void viewRecipe(String recipeName) {
		Log.i("brewerspad", "need to switch to recipe view " + recipeName);

		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		SharedPreferences.Editor editor = shrdPrefs.edit();
		editor.putString("recipename", recipeName);
		editor.commit();

		startActivity(new Intent(this, recipe.class));

	}

	public void presentError(String title, String exception, String jsonResponse) {

		// Toast.makeText(this,
		// "Unable to communicate with the brewerslab server.",
		// Toast.LENGTH_LONG).show();
		if (exception.equals("NotAuthorised")) {
			Toast.makeText(
					this,
					"This app is no longer linked to the cloud, please sign in with your google account.",
					Toast.LENGTH_LONG).show();

			Uri uriUrl = Uri.parse("http://" + hostnameIp + ":" + (portNum + 1)
					+ "/authorise/?deviceId=androidApp");
			Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
			startActivity(launchBrowser);

		} else {
			Log.i("brewerspad",
					"________________________________________________________________________");
			Log.i("brewerspad", "		Error/Exception in " + title);
			Log.i("brewerspad", exception);
			Log.i("brewerspad", jsonResponse);
			Log.i("brewerspad",
					"________________________________________________________________________");
			if (debugXMLRPC == true) {
				SharedPreferences shrdPrefs = getSharedPreferences("activity",
						0);
				SharedPreferences.Editor editor = shrdPrefs.edit();
				editor.putString("errorlocation", title);
				editor.putString("exception", exception);
				editor.putString("jsonresponse", jsonResponse);
				editor.commit();
				startActivity(new Intent(this, debugxmlrpc.class));
			}
		}
	}

	public void goToWelcome2(String jsonresponse, String recipename) {
		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		SharedPreferences.Editor editor = shrdPrefs.edit();
		editor.putString("recipename", recipename);
		editor.putString("jsonresponseA", jsonresponse);
		editor.commit();

		startActivity(new Intent(this, welcome2.class));
	}

	public class BrewlogItemAdapter extends ArrayAdapter<brewlogitem> {

		private ArrayList<brewlogitem> items;

		public BrewlogItemAdapter(Context context, int textViewResourceId,
				ArrayList<brewlogitem> items) {
			super(context, textViewResourceId, items);

			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.i("brewerspad", "in grtView of BrewlogItemAdapter");
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.customrowitem0, null);

			}

			brewlogitem o = items.get(position);
			if (o != null) {

				TextView tt = (TextView) v.findViewById(R.id.toptext0);
				tt.setText("" + o.Display);

			}
			return v;
		}
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
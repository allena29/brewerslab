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

import org.json.JSONArray;
import org.json.JSONObject;

import brewerspad.asduk.com.recipecommon.recipeitem;

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
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class recipe extends Activity {

	Boolean startTracking = false;
	Boolean stopTracking = false;

	Integer dbg = 0;
	Boolean debugXMLRPC = true;
	String activeRecipe = "";
	String activeIngredient = "<NULL>"; // this is used for the 3rd listitem
	String activeCategory = "";
	String activeItem = "";
	Boolean tabletDevice;

	TextView seekBarVolLabel;
	TextView seekBarIbuLabel;
	TextView seekBarAbvlLabel;
	Integer seekBarVolOrigValue;
	Integer seekBarIbuOrigValue;
	Integer seekBarAbvOrigValue;

	Boolean trackProgressBars = false;
	Integer progressBarVolume = 0;
	Integer progressBarIbu = 0;
	Integer progressBarAbv = 0;

	ListView listviewRecipeItems;
	ListView listviewRecipe2Items;
	ListView listviewRecipe3Items;
	ListView listviewRecipe4Items;
	ListView listviewRecipe5Items;
	ListView listviewIngredients;
	ArrayAdapter listViewRecipeItemsArrayAdapter;
	ArrayAdapter listViewRecipe2ItemsArrayAdapter;
	ArrayAdapter listViewRecipe3ItemsArrayAdapter;
	ArrayAdapter listViewRecipe4ItemsArrayAdapter;
	ArrayAdapter listViewRecipe5ItemsArrayAdapter;
	ArrayAdapter listViewIngredientsArrayAdapter;
	recipecommon recipeLogic = new recipecommon();

	DisplayMetrics dm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("brewerspad", "recipe/onCreate()");

		// set outselves as a boss in storecommon
		// storeLogic.storesBoss = this;
		// storeLogic.boss = "stores";

		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		//
		tabletDevice = shrdPrefs.getBoolean("tabletDevice", true);
		if (tabletDevice && getWindowManager().getDefaultDisplay().getWidth() > getWindowManager().getDefaultDisplay().getHeight()) {
			setContentView(R.layout.recipes);
			recipeLogic.tabletDevice=true;
		}else{
			setContentView(R.layout.recipesphone);
			recipeLogic.tabletDevice=false;
		}

		
		
		recipeLogic.boss="recipe";
		recipeLogic.recipeBoss = this;
		recipeLogic.cloudHost = shrdPrefs.getString("hostname", "127.0.0.1");
		recipeLogic.cloudKey = shrdPrefs.getString("cloudkey", "expired");
		recipeLogic.cloudDevice = shrdPrefs.getString("clouddevice",
				"<testdevice>");
		recipeLogic.cloudUser = shrdPrefs.getString("clouduser", "<user>");
		recipeLogic.cloudPort = shrdPrefs.getInt("portNumber", 1027) + 1;

		// Get Screen Dimensions
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		// show the background image on the layout
		String path = Environment.getExternalStorageDirectory().toString();
		String pathName = path + "/brewerspad/recipebg.png.xml";
		Resources res = getResources();
		Bitmap bitmap = BitmapFactory.decodeFile(pathName);
		BitmapDrawable bd = new BitmapDrawable(res, bitmap);
		View view = findViewById(R.id.recipeOuterLayout);
		view.setBackgroundDrawable(bd);



		activeRecipe = shrdPrefs.getString("recipename", "");

		TextView title = (TextView) findViewById(R.id.recipeTitle);
		title.setText("Recipe - " + activeRecipe);

		// Download the list of recipes
		fluffycloud cloudtask = new fluffycloud();
		cloudtask.themRecipe = recipeLogic;
		cloudtask.themId = "recipe";

		cloudtask.hostnameIp = shrdPrefs.getString("hostname", "127.0.0.1");
		cloudtask.cloudKey = shrdPrefs.getString("cloudkey", "expired");
		cloudtask.cloudDevice = shrdPrefs.getString("clouddevice",
				"<testdevice>");
		cloudtask.cloudUser = shrdPrefs.getString("clouduser", "<user>");
		cloudtask.portNum = shrdPrefs.getInt("portNumber", 1027) + 1;
		cloudtask.execute("viewRecipe", activeRecipe, activeIngredient);

		pleasewait();

		
		// potentially can use just a table for the recipe and rely on different layouts
		// - although it looks like if layout/recipe.xml and layout-xlarge/recipe.xml differ too much
		//   in structure it leads to cast erros
		if (tabletDevice && getWindowManager().getDefaultDisplay().getWidth() > getWindowManager().getDefaultDisplay().getHeight()) {
			tabletOnCreate();
		
				//phneOnCreate isn't needed as it is another page
		}

	}

	public void tabletOnCreate() {

		listviewRecipeItems = (ListView) findViewById(R.id.listview2);
		listviewRecipeItems.setFocusableInTouchMode(true);
		recipeLogic.listViewRecipeItems = new ArrayList<recipeitem>();
		// stockItemsArrayAdapter = new StockItemAdapter(this,
		// R.layout.customrowitem2, storeLogic.listViewStockItems);
		// Log.i("brewerspad", "store2.listViewc");
		listViewRecipeItemsArrayAdapter = new RecipeItemAdapter(this,
				R.layout.customrowitem4, recipeLogic.listViewRecipeItems);
		listviewRecipeItems.setAdapter(listViewRecipeItemsArrayAdapter);
		listviewRecipeItems.setClickable(true);
		listviewRecipeItems
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						recipeitem o = (recipeitem) listviewRecipeItems
								.getItemAtPosition(arg2);
						Log.i("brewerspad", "recipe.listview2 Selected"
								+ o.StockName);

						if (o.header) {
							//
							activeIngredient = o.StockName;
							// Download the list of ingredients
							fluffycloud cloudtask = new fluffycloud();
							cloudtask.themRecipe = recipeLogic;
							cloudtask.themId = "recipe";

							cloudtask.hostnameIp = recipeLogic.cloudHost;
							cloudtask.cloudKey = recipeLogic.cloudKey;
							cloudtask.cloudDevice = recipeLogic.cloudDevice;
							cloudtask.cloudUser = recipeLogic.cloudUser;
							cloudtask.portNum = recipeLogic.cloudPort;
							cloudtask.execute("viewRecipe", activeRecipe,
									activeIngredient);

							pleasewait();
						} else {
							//
							changeQty(o.StockName, o.HopAddAt, o.StockCategory,
									o.StockQty);
						}

					}
				});

		listviewIngredients = (ListView) findViewById(R.id.listview9);
		listviewIngredients.setFocusableInTouchMode(true);
		recipeLogic.listViewIngredients = new ArrayList<recipeitem>();
		// stockItemsArrayAdapter = new StockItemAdapter(this,
		// R.layout.customrowitem2, storeLogic.listViewStockItems);
		// Log.i("brewerspad", "store2.listViewc");
		listViewIngredientsArrayAdapter = new RecipeIngredientAdapter(this,
				R.layout.customrowitem2, recipeLogic.listViewIngredients);
		listviewIngredients.setAdapter(listViewIngredientsArrayAdapter);
		listviewIngredients.setClickable(true);
		listviewIngredients
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						recipeitem o = (recipeitem) listviewIngredients
								.getItemAtPosition(arg2);
						Log.i("brewerspad", "recipe.listview9 Selected"
								+ o.StockName);

						addNewItem(o.StockName);

					}
				});

	}

	
	public void recipePhonePage2(View v){
		startActivity(new Intent(this, recipe2.class));	
	}
	
	
	public void onRecipeStatsAndIngredientsPostUpdate(Boolean recalcNeeded) {
		trackProgressBars = false;
		DecimalFormat df = new DecimalFormat("##0.00");
		DecimalFormat dfsingle = new DecimalFormat("##0.0");
		DecimalFormat dfgrav = new DecimalFormat("##0.000");

		// show a warning that there has been a change whcih has not been
		// reflected in the calcultions
		warning(recalcNeeded);

		TextView tv;
		SeekBar sb;

		if(tabletDevice){
			tv = (TextView) findViewById(R.id.recipePart3);
			if (activeIngredient.equals("<NULL>")) {
				tv.setText("");
			} else {
				tv.setText(activeIngredient);
			}
		}
		
		
		tv = (TextView) findViewById(R.id.valueABV);
		if (recipeLogic.thisEstimatedABV.equals(recipeLogic.estimatedABV)) {
			tv.setText(df.format(recipeLogic.estimatedABV ));
		} else {
			tv.setText(df.format(recipeLogic.thisEstimatedABV ) + " ("
					+ df.format(recipeLogic.estimatedABV ) + ")");

		}

		Log.i("brewerspad", " recipeLogic.estimatedABV: "
				+ recipeLogic.estimatedABV);
		Log.i("brewerspad", " recipeLogic.thisEstimatedABV: "
				+ recipeLogic.thisEstimatedABV);
		Log.i("brewerspad", " recipeLogic.estimatedIBU: "
				+ recipeLogic.estimatedIBU);
		Log.i("brewerspad", " recipeLogic.thisEstimatedIBU: "
				+ recipeLogic.thisEstimatedIBU);

		sb = (SeekBar) findViewById(R.id.seekBarABV);
		sb.setMax(160);
		Integer tmp = (int) Math.round(recipeLogic.thisEstimatedABV * 10);
		sb.setProgress(tmp);

		seekBarIbuLabel = (TextView) findViewById(R.id.valueIBU);
		if (recipeLogic.thisEstimatedIBU.equals(recipeLogic.estimatedIBU)) {
			seekBarIbuLabel.setText("" + recipeLogic.estimatedIBU);
		} else {
			seekBarIbuLabel.setText("" + recipeLogic.thisEstimatedIBU + " ("
					+ recipeLogic.estimatedIBU + ")");

		}

		sb = (SeekBar) findViewById(R.id.seekBarIBU);
		sb.setMax(800);
		tmp = (int) Math.round(recipeLogic.thisEstimatedIBU * 10);
		sb.setProgress(tmp);
		seekBarIbuOrigValue = tmp;
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (seekBarIbuOrigValue != progressBarIbu) {
					Log.i("brewerspad", "seekbarvol moved to a different value");
					Double d = Integer.valueOf(progressBarIbu).doubleValue();
					d = d / 10;
					Double e = Integer.valueOf(seekBarIbuOrigValue)
							.doubleValue();
					e = e / 10;
					seekBarIbuLabel.setText(d + " [" + e + "]");
				}
			}

			public void onStopTrackingTouch(SeekBar arg0) {
				if (trackProgressBars) {
					Log.i("brewerspad",
							" on seekbar change listener for volume");
					fluffycloud cloudtask = new fluffycloud();
					cloudtask.themRecipe = recipeLogic;
					cloudtask.themId = "recipe";
					cloudtask.hostnameIp = recipeLogic.cloudHost;

					cloudtask.cloudKey = recipeLogic.cloudKey;
					cloudtask.cloudDevice = recipeLogic.cloudDevice;
					cloudtask.cloudUser = recipeLogic.cloudUser;
					cloudtask.portNum = recipeLogic.cloudPort;
					// note batch size is always an integer, but will need this
					// kind of
					// trickery for ABV
					Double d = Integer.valueOf(progressBarIbu).doubleValue();
					d = d / 10;
					Log.i("brewerspad", "changing ibu requested for "
							+ activeRecipe + " to " + d);
					cloudtask.execute("scaleIBU", activeRecipe,
							Double.toString(d), "0");

					pleasewait();

					// value.setText("SeekBar value is "+progress);
				}

			}

			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}
		});

		sb = (SeekBar) findViewById(R.id.seekBarVol);
		sb.setMax(40);
		seekBarVolLabel = (TextView) findViewById(R.id.valueFinalVolume);
		int x = (int) (recipeLogic.batchsize * 1);
		sb.setProgress(x);
		// sb.setTag(x); // we might want to keep this ready for future use
		seekBarVolOrigValue = x;
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

				Log.i("brewerspad", "seekBarVol - onProgressCHanged" + fromUser
						+ " stop " + stopTracking);
				progressBarVolume = progress;

				if (seekBarVolOrigValue != progressBarVolume) {
					Log.i("brewerspad", "seekbarvol moved to a different value");
					seekBarVolLabel.setText(progressBarVolume + " ["
							+ seekBarVolOrigValue + "]");
				} else {
					Log.i("brewerspad", "seekbarvol at the same value");
				}

			}

			public void onStartTrackingTouch(SeekBar arg0) {
				Log.i("brewerspad", "seekBarVol -startTracking");

				startTracking = true;
				stopTracking = false;
			}

			public void onStopTrackingTouch(SeekBar arg0) {

				Log.i("brewerspad", "seekBarVol -stopTracking");
				startTracking = false;
				stopTracking = true;

				if (trackProgressBars) {
					Log.i("brewerspad",
							" on seekbar change listener for volume");
					fluffycloud cloudtask = new fluffycloud();
					cloudtask.themRecipe = recipeLogic;
					cloudtask.themId = "recipe";

					cloudtask.hostnameIp = recipeLogic.cloudHost;
					cloudtask.cloudKey = recipeLogic.cloudKey;
					cloudtask.cloudDevice = recipeLogic.cloudDevice;
					cloudtask.cloudUser = recipeLogic.cloudUser;
					cloudtask.portNum = recipeLogic.cloudPort;
					// note batch size is always an integer, but will need this
					// kind of
					// trickery for ABV
					// Double d = Integer.valueOf(progress).doubleValue();
					// d=d/10;
					Log.i("brewerspad", "changing batchsize for "
							+ activeRecipe + " to " + progressBarVolume);
					cloudtask.execute("setBatchSize", activeRecipe,
							Integer.toString(progressBarVolume), "0");

					pleasewait();

					// value.setText("SeekBar value is "+progress);
				}

			}
		});

		tv = (TextView) findViewById(R.id.valueEstimatedFG);
		if (recipeLogic.thisEstimatedFG.equals(recipeLogic.estimatedFG)) {
			tv.setText(dfgrav.format(recipeLogic.estimatedFG));
		} else {
			tv.setText(dfgrav.format(recipeLogic.thisEstimatedFG) + " ("
					+ dfgrav.format(recipeLogic.estimatedFG) + ")");

		}

		tv = (TextView) findViewById(R.id.valueEstimatedOG);
		if (recipeLogic.thisEstimatedOG.equals(recipeLogic.estimatedOG)) {
			tv.setText(dfgrav.format(recipeLogic.estimatedOG));
		} else {
			tv.setText(dfgrav.format(recipeLogic.thisEstimatedOG) + " ("
					+ dfgrav.format(recipeLogic.estimatedOG) + ")");

		}

		/*
		 * *
		 * Set colour *
		 */

		tv = (TextView) findViewById(R.id.valueEstimatedColour);
		tv.setText("" + recipeLogic.estimatedEBC);
		setSrmColourImage();

		tv = (TextView) findViewById(R.id.valueMashEfficiency);
		tv.setText(recipeLogic.mashEfficiency + " %");

		tv = (TextView) findViewById(R.id.valueFinalVolume);
		
		if (!recipeLogic.batchsize.equals( recipeLogic.thisbatchsize)) {
			tv.setText( ""+dfsingle.format(recipeLogic.thisbatchsize) + " ("+dfsingle.format(recipeLogic.batchsize)+")"	);
		}else{
			tv.setText(dfsingle.format(recipeLogic.batchsize));
		}
		
		
		tv = (TextView) findViewById(R.id.valueSpargeWater);
		tv.setText(dfsingle.format(recipeLogic.spargeWater));

		tv = (TextView) findViewById(R.id.valueMashWater);
		tv.setText(dfsingle.format(recipeLogic.mashWater));

		tv = (TextView) findViewById(R.id.valueBoileWater);
		tv.setText(dfsingle.format(recipeLogic.boilVolume));

		tv = (TextView) findViewById(R.id.valueWater);
		tv.setText(dfsingle.format(recipeLogic.totalWater));

		tv = (TextView) findViewById(R.id.valuePostboilTopup);
		tv.setText(dfsingle.format(recipeLogic.valuePostboilTopup));

		tv = (TextView) findViewById(R.id.valueTotalGrain);
		tv.setText(dfsingle.format(recipeLogic.totalGrain));

		tv = (TextView) findViewById(R.id.valueTotalAdjunct);
		tv.setText(dfsingle.format(recipeLogic.totalAdjuncts));

		tv = (TextView) findViewById(R.id.valueTotalHops);
		tv.setText(dfsingle.format(recipeLogic.totalHops));

		tv = (TextView) findViewById(R.id.valueProcess);
		tv.setText(recipeLogic.process);

		if(tabletDevice){
			// this is in recipe2.java for phones
			ListView lv = (ListView) findViewById(R.id.listview2);
			((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();
			ListView lv2 = (ListView) findViewById(R.id.listview9);
			((BaseAdapter) lv2.getAdapter()).notifyDataSetChanged();

		}
		trackProgressBars = true;
	}

	public void refreshGui(View v) {
		// DBG Button to help out while developing
		// Download the list of recipes
		fluffycloud cloudtask = new fluffycloud();
		cloudtask.themRecipe = recipeLogic;
		cloudtask.themId = "recipe";
		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		cloudtask.hostnameIp = shrdPrefs.getString("hostname", "127.0.0.1");
		cloudtask.cloudKey = shrdPrefs.getString("cloudkey", "expired");
		cloudtask.cloudDevice = shrdPrefs.getString("clouddevice",
				"<testdevice>");
		cloudtask.cloudUser = shrdPrefs.getString("clouduser", "<user>");
		cloudtask.portNum = shrdPrefs.getInt("portNumber", 1027) + 1;
		cloudtask.execute("viewRecipe", activeRecipe, activeIngredient);

		pleasewait();
	}

	public void recalculate(View v) {
		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		fluffycloud cloudtask = new fluffycloud();
		cloudtask.themRecipe = recipeLogic;
		cloudtask.themId = "recipe";
		cloudtask.hostnameIp = shrdPrefs.getString("hostname", "127.0.0.1");
		cloudtask.cloudKey = shrdPrefs.getString("cloudkey", "expired");
		cloudtask.cloudDevice = shrdPrefs.getString("clouddevice",
				"<testdevice>");
		cloudtask.cloudUser = shrdPrefs.getString("clouduser", "<user>");
		cloudtask.portNum = shrdPrefs.getInt("portNumber", 1027) + 1;
		cloudtask.execute("calculateRecipeWrapper", activeRecipe,
				activeIngredient);

		pleasewait();
	}

	public void addNewItem(String stockName) {
		Log.i("brewerspad", "addNewItem");
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Add " + stockName);
		alert.setMessage(stockName);

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		input.setText("0");
		final TextView tv = new TextView(this);
		tv.setText("Qty:");
		final LinearLayout llA = new LinearLayout(this);
		llA.setGravity(Gravity.HORIZONTAL_GRAVITY_MASK);
		llA.addView(tv);
		llA.addView(input);

		final LinearLayout ll = new LinearLayout(this);
		ll.setGravity(Gravity.VERTICAL_GRAVITY_MASK);
		ll.addView(llA);

		final EditText input2 = new EditText(this);
		input2.setText("60");

		if (activeIngredient.equals("Hops")) {

			final TextView tv2 = new TextView(this);
			tv2.setText("Hop Add At:");
			final LinearLayout llB = new LinearLayout(this);
			llB.setGravity(Gravity.HORIZONTAL_GRAVITY_MASK);
			llB.addView(tv2);
			llB.addView(input2);

			ll.addView(llB);
		}

		alert.setView(ll);
		activeItem = stockName;

		input.setInputType(8192); // TYPE CLASS NUMBER

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				String value = input.getText().toString();
				String value2 = input2.getText().toString();

				fluffycloud cloudtask = new fluffycloud();
				cloudtask.themRecipe = recipeLogic;
				cloudtask.themId = "recipe";

				cloudtask.hostnameIp = recipeLogic.cloudHost;
				cloudtask.cloudKey = recipeLogic.cloudKey;
				cloudtask.cloudDevice = recipeLogic.cloudDevice;
				cloudtask.cloudUser = recipeLogic.cloudUser;
				cloudtask.portNum = recipeLogic.cloudPort;
				cloudtask.execute("addItemToRecipe", activeRecipe,
						activeIngredient, activeItem, value, value2, "0");

				pleasewait();

			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
						Log.i("brewerspad", "ignored input dialog");
					}
				});

		alert.show();

	}

	public void changeQty(String stockName, String hopaddat, String category,
			String stockQty) {

		Log.i("brewerspad", "changeQty");
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Change Qty");
		if (hopaddat.length() > 0 && !hopaddat.equals("0.0")) {
			alert.setMessage(stockName + " (" + hopaddat + " min)");
		} else {
			alert.setMessage(stockName);
		}
		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		input.setText("" + stockQty);
		input.setTag(hopaddat);		
		alert.setView(input);
		activeItem = stockName;
		activeCategory = category;
		input.setInputType(8192); // TYPE CLASS NUMBER

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				String value = input.getText().toString();
				fluffycloud cloudtask = new fluffycloud();
				cloudtask.themRecipe = recipeLogic;
				cloudtask.themId = "recipe";

				cloudtask.hostnameIp = recipeLogic.cloudHost;
				cloudtask.cloudKey = recipeLogic.cloudKey;
				cloudtask.cloudDevice = recipeLogic.cloudDevice;
				cloudtask.cloudUser = recipeLogic.cloudUser;
				cloudtask.portNum = recipeLogic.cloudPort;
				cloudtask.execute("changeItemInRecipe", activeRecipe, activeCategory,
						activeItem, value, input.getTag().toString(), "0");

				pleasewait();

			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
						Log.i("brewerspad", "ignored input dialog");
					}
				});

		alert.show();

	}

	/*
	 * 
	 * 
	 * Buttons to change recipe attributes
	 */

	public void ChangeMashEfficency(View v) {
		Log.i("brewerspad", "changeMashEfficency");
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Mash Effiency");
		alert.setMessage("Select Effiency: ");

		// Set an EditText view to get user input
		final Spinner input = new Spinner(this);
		alert.setView(input);

		recipeLogic.effiencyArrayData.clear();
		Log.i("brewerspad", " cleared data");
		for (int i = 50; i < 95; ++i) {
			recipeLogic.effiencyArrayData.add("" + i + " %");

		}

		ArrayAdapter processArrayAdapter = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item,
				recipeLogic.effiencyArrayData);
		processArrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		input.setAdapter(processArrayAdapter);
		input.setClickable(true);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				String efficencyText = (String) input.getSelectedItem();
				fluffycloud cloudtask = new fluffycloud();
				cloudtask.themRecipe = recipeLogic;
				cloudtask.themId = "recipe";

				cloudtask.hostnameIp = recipeLogic.cloudHost;
				cloudtask.cloudKey = recipeLogic.cloudKey;
				cloudtask.cloudDevice = recipeLogic.cloudDevice;
				cloudtask.cloudUser = recipeLogic.cloudUser;
				cloudtask.portNum = recipeLogic.cloudPort;
				cloudtask.execute("setMashEfficiency", activeRecipe,
						efficencyText, "0");

				pleasewait();

			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
						Log.i("brewerspad", "ignored input dialog");
					}
				});

		alert.show();

	}

	
	
	
	public void ChangePostBoilTopup(View v) {
		Log.i("brewerspad", "ChangePostBoilTopup");
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Post Boil Topup");
		Log.i("brewerspad", "ChangePostBoilTopup 2");
		alert.setMessage("Topup (Litres): ");
		Log.i("brewerspad", "ChangePostBoilTopup 3");
		// Set an EditText view to get user input
				final EditText input = new EditText(this);
				//input.setTag(hopaddat);		
				input.setText("0");
				Log.i("brewerspad", "ChangePostBoilTopup 4");
				alert.setView(input);				
				input.setInputType(8192); // TYPE CLASS NUMBER
				Log.i("brewerspad", "ChangePostBoilTopup 5");
				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Log.i("brewerspad", "ChangePostBoilTopup 8");
						String value = input.getText().toString();
						fluffycloud cloudtask = new fluffycloud();
						cloudtask.themRecipe = recipeLogic;
						cloudtask.themId = "recipe";
						cloudtask.hostnameIp = recipeLogic.cloudHost;
						cloudtask.cloudKey = recipeLogic.cloudKey;
						Log.i("brewerspad", "ChangePostBoilTopup 9");
						cloudtask.cloudDevice = recipeLogic.cloudDevice;
						cloudtask.cloudUser = recipeLogic.cloudUser;
						cloudtask.portNum = recipeLogic.cloudPort;
						cloudtask.execute("setTopupVolume", activeRecipe, input.getText().toString(),"0");
						Log.i("brewerspad", "ChangePostBoilTopup 10");
						pleasewait();

					}
				});
				Log.i("brewerspad", "ChangePostBoilTopup 11");
				alert.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								// Canceled.
								Log.i("brewerspad", "ignored input dialog");
							}
						});
				Log.i("brewerspad", "ChangePostBoilTopup 12");
				alert.show();
				Log.i("brewerspad", "ChangePostBoilTopup 14");
			}

	
	
	public void changeProcess() {

		Log.i("brewerspad", "changeProcess");
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Select Process");
		alert.setMessage("Select Process: ");

		// Set an EditText view to get user input
		final Spinner input = new Spinner(this);
		alert.setView(input);

		ArrayAdapter processArrayAdapter = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item,
				recipeLogic.processArrayData);
		processArrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		input.setAdapter(processArrayAdapter);
		input.setClickable(true);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				String processText = (String) input.getSelectedItem();
				fluffycloud cloudtask = new fluffycloud();
				cloudtask.themRecipe = recipeLogic;
				cloudtask.themId = "recipe";

				cloudtask.hostnameIp = recipeLogic.cloudHost;
				cloudtask.cloudKey = recipeLogic.cloudKey;
				cloudtask.cloudDevice = recipeLogic.cloudDevice;
				cloudtask.cloudUser = recipeLogic.cloudUser;
				cloudtask.portNum = recipeLogic.cloudPort;
				cloudtask.execute("changeProcess", activeRecipe, processText,
						activeCategory);

				pleasewait();

			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
						Log.i("brewerspad", "ignored input dialog");
					}
				});

		alert.show();

	}

	public void setSrmColourImage() {
		ImageView iv = (ImageView) findViewById(R.id.imageView1);
		if (recipeLogic.estimatedEBC < 2) {
			iv.setImageResource(R.drawable.srm1);
		} else if (recipeLogic.estimatedEBC < 3) {
			iv.setImageResource(R.drawable.srm2);
		} else if (recipeLogic.estimatedEBC < 4) {
			iv.setImageResource(R.drawable.srm3);
		} else if (recipeLogic.estimatedEBC < 5) {
			iv.setImageResource(R.drawable.srm4);
		} else if (recipeLogic.estimatedEBC < 6) {
			iv.setImageResource(R.drawable.srm5);
		} else if (recipeLogic.estimatedEBC < 7) {
			iv.setImageResource(R.drawable.srm6);
		} else if (recipeLogic.estimatedEBC < 8) {
			iv.setImageResource(R.drawable.srm7);
		} else if (recipeLogic.estimatedEBC < 9) {
			iv.setImageResource(R.drawable.srm8);
		} else if (recipeLogic.estimatedEBC < 10) {
			iv.setImageResource(R.drawable.srm9);
		} else if (recipeLogic.estimatedEBC < 11) {
			iv.setImageResource(R.drawable.srm10);
		} else if (recipeLogic.estimatedEBC < 12) {
			iv.setImageResource(R.drawable.srm11);
		} else if (recipeLogic.estimatedEBC < 13) {
			iv.setImageResource(R.drawable.srm12);
		} else if (recipeLogic.estimatedEBC < 14) {
			iv.setImageResource(R.drawable.srm13);
		} else if (recipeLogic.estimatedEBC < 15) {
			iv.setImageResource(R.drawable.srm14);
		} else if (recipeLogic.estimatedEBC < 16) {
			iv.setImageResource(R.drawable.srm15);
		} else if (recipeLogic.estimatedEBC < 17) {
			iv.setImageResource(R.drawable.srm16);
		} else if (recipeLogic.estimatedEBC < 18) {
			iv.setImageResource(R.drawable.srm17);
		} else if (recipeLogic.estimatedEBC < 19) {
			iv.setImageResource(R.drawable.srm18);
		} else if (recipeLogic.estimatedEBC < 20) {
			iv.setImageResource(R.drawable.srm19);
		} else if (recipeLogic.estimatedEBC < 21) {
			iv.setImageResource(R.drawable.srm20);
		} else if (recipeLogic.estimatedEBC < 22) {
			iv.setImageResource(R.drawable.srm21);
		} else if (recipeLogic.estimatedEBC < 23) {
			iv.setImageResource(R.drawable.srm22);
		} else if (recipeLogic.estimatedEBC < 24) {
			iv.setImageResource(R.drawable.srm23);
		} else if (recipeLogic.estimatedEBC < 25) {
			iv.setImageResource(R.drawable.srm24);
		} else if (recipeLogic.estimatedEBC < 26) {
			iv.setImageResource(R.drawable.srm25);
		} else if (recipeLogic.estimatedEBC < 27) {
			iv.setImageResource(R.drawable.srm26);
		} else if (recipeLogic.estimatedEBC < 28) {
			iv.setImageResource(R.drawable.srm27);
		} else if (recipeLogic.estimatedEBC < 29) {
			iv.setImageResource(R.drawable.srm28);
		} else if (recipeLogic.estimatedEBC < 30) {
			iv.setImageResource(R.drawable.srm29);
		} else if (recipeLogic.estimatedEBC < 31) {
			iv.setImageResource(R.drawable.srm30);
		} else if (recipeLogic.estimatedEBC < 32) {
			iv.setImageResource(R.drawable.srm31);
		} else if (recipeLogic.estimatedEBC < 33) {
			iv.setImageResource(R.drawable.srm32);
		} else if (recipeLogic.estimatedEBC < 34) {
			iv.setImageResource(R.drawable.srm33);
		} else if (recipeLogic.estimatedEBC < 35) {
			iv.setImageResource(R.drawable.srm34);
		} else if (recipeLogic.estimatedEBC < 36) {
			iv.setImageResource(R.drawable.srm35);
		} else if (recipeLogic.estimatedEBC < 37) {
			iv.setImageResource(R.drawable.srm36);
		} else if (recipeLogic.estimatedEBC < 38) {
			iv.setImageResource(R.drawable.srm37);
		} else if (recipeLogic.estimatedEBC < 39) {
			iv.setImageResource(R.drawable.srm38);
		} else if (recipeLogic.estimatedEBC < 40) {
			iv.setImageResource(R.drawable.srm39);
		} else if (recipeLogic.estimatedEBC < 40) {
			iv.setImageResource(R.drawable.srm40);
		} else {
			iv.setImageResource(R.drawable.srmx);
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
		Log.i("brewerspad", "onCreateOptionsMenu");
		MenuInflater inflater = getMenuInflater();
		Log.i("brewerspad", "R.menu.recipemenu");
		inflater.inflate(R.menu.menu2, menu);

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
		fluffycloud cloudtask;
		switch (item.getItemId()) {
		case R.id.menuCalclog:

			SharedPreferences.Editor editor = shrdPrefs.edit();

			Integer portNum1 = shrdPrefs.getInt("portNumber", 1027) + 1;
			Log.i("brewerslab","launchCalclog");
			//Log.i("brewerslab",v.getTag() );
			// Set so that we don't close ourself
			String weburl = "http://" + shrdPrefs.getString("hostname", "127.0.0.1")+":"+portNum1+"/ngbrewlab.py?rawCalcLog="+activeRecipe+"&owner=" + shrdPrefs.getString("clouduser", "<user>")+"";
			Log.i("brewerslab","url to our calclog "+weburl);
			editor.putString("weburl", weburl);
			editor.putString("webstyle", "url");
			editor.commit();		
			
			startActivity(new Intent(this, webviewer.class));
			
			return true;
		case R.id.menuFixRecipe:
			Log.i("brewerspad", "fixRecipe");

			cloudtask = new fluffycloud();
			cloudtask.themRecipe = recipeLogic;
			cloudtask.themId = "recipe";

			cloudtask.hostnameIp = shrdPrefs.getString("hostname", "127.0.0.1");
			cloudtask.cloudKey = shrdPrefs.getString("cloudkey", "expired");
			cloudtask.cloudDevice = shrdPrefs.getString("clouddevice",
					"<testdevice>");
			cloudtask.cloudUser = shrdPrefs.getString("clouduser", "<user>");
			cloudtask.portNum = shrdPrefs.getInt("portNumber", 1027) + 1;
			cloudtask.execute("fixRecipe", activeRecipe);

			pleasewait();
			return true;
		case R.id.menuEditProcess:
			Log.i("brewerspad", "menuEditProcess");

			cloudtask = new fluffycloud();
			cloudtask.themRecipe = recipeLogic;
			cloudtask.themId = "recipe";

			cloudtask.hostnameIp = shrdPrefs.getString("hostname", "127.0.0.1");
			cloudtask.cloudKey = shrdPrefs.getString("cloudkey", "expired");
			cloudtask.cloudDevice = shrdPrefs.getString("clouddevice",
					"<testdevice>");
			cloudtask.cloudUser = shrdPrefs.getString("clouduser", "<user>");
			cloudtask.portNum = shrdPrefs.getInt("portNumber", 1027) + 1;
			cloudtask.execute("listProcess");

			return true;
		}

		return false;
	}

	public void warning(Boolean required) {
		// progressbar
		if (required) {
			TextView wartext = (TextView) findViewById(R.id.recipeWarningText);
			wartext.setVisibility(View.VISIBLE);
			ImageView warimg = (ImageView) findViewById(R.id.recipeWarningImage);
			warimg.setVisibility(View.VISIBLE);
		} else {
			TextView wartext = (TextView) findViewById(R.id.recipeWarningText);
			wartext.setVisibility(View.GONE);
			ImageView warimg = (ImageView) findViewById(R.id.recipeWarningImage);
			warimg.setVisibility(View.GONE);
		}

	}

	public void pleasewait() {
		// progressbar
		recipeLogic.pleasewait = ProgressDialog.show(this, "",
				"Please Wait Connecting to the cloud", true, true);
		recipeLogic.pleasewaitActive = true;
	}

	public void kickGui() {

		// If we have a progress dialog box hide it.
		if (recipeLogic.pleasewaitActive) {
			recipeLogic.pleasewaitActive = false;
			recipeLogic.pleasewait.dismiss();
		}

	}

	public void presentError(String title, String exception, String jsonResponse) {

		// Toast.makeText(this,
		// "Unable to communicate with the brewerspad server.",
		// Toast.LENGTH_LONG).show();
		if (exception.equals("NotAuthorised")) {
			Toast.makeText(
					this,
					"This app is no longer linked to the cloud, please sign in with your google account.",
					Toast.LENGTH_LONG).show();
			SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
			Uri uriUrl = Uri.parse("http://"
					+ shrdPrefs.getString("hostname", "127.0.0.1") + ":"
					+ shrdPrefs.getInt("portNumber", 1027) + 1
					+ "/authorise/?deviceId=androidApp");
			Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
			startActivity(launchBrowser);

		} else {
			Log.i("brewerspad",
					"________________________________________________________________________");
			Log.i("brewerspad", "		Error/Exception in " + title);
			Log.i("brewerspad", "Response" + jsonResponse);
			Log.i("brewerspad", "Exception" + exception);
			Log.i("brewerspad",
					"________________________________________________________________________");
			if (debugXMLRPC == true) {
				SharedPreferences shrdPrefs = getSharedPreferences("activity",
						0);
				SharedPreferences.Editor editor = shrdPrefs.edit();
				editor.putString("errorlocation", "Error:" + title);
				editor.putString("exception", "Exception:" + exception);
				editor.putString("jsonresponse", "Response:" + jsonResponse);
				editor.commit();
				startActivity(new Intent(this, debugxmlrpc.class));

				kickGui();
			}
		}

	}

	// Recipe Item
	private class RecipeItemAdapter extends ArrayAdapter<recipeitem> {

		private ArrayList<recipeitem> items;

		public RecipeItemAdapter(Context context, int textViewResourceId,
				ArrayList<recipeitem> items) {
			super(context, textViewResourceId, items);

			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.customrowitem4, null);

			}

			recipeitem o = items.get(position);
			if (o != null) {

				if (o.header) {
					TextView th = (TextView) v.findViewById(R.id.criheadertext);
					th.setText("" + o.StockName);
					th.setVisibility(View.VISIBLE);
					TextView tt = (TextView) v.findViewById(R.id.cri2toptext);
					tt.setText("");
					TextView bt = (TextView) v
							.findViewById(R.id.cri2bottomtext);
					bt.setText("");
					TextView bt2 = (TextView) v.findViewById(R.id.cri2toptext2);
					bt2.setText("");
					tt.setVisibility(View.GONE);
					bt.setVisibility(View.GONE);
					bt2.setVisibility(View.GONE);
				} else {
					TextView th = (TextView) v.findViewById(R.id.criheadertext);
					th.setVisibility(View.GONE);
					TextView tt = (TextView) v.findViewById(R.id.cri2toptext);

					if (o.StockCategory.equals("hops")) {
						tt.setText("" + o.StockName + " (" + o.HopAddAt
								+ " min)");
					} else {
						tt.setText("" + o.StockName);
					}
					TextView bt = (TextView) v
							.findViewById(R.id.cri2bottomtext);
					if (o.StockQty.equals(o.OriginalQty)) {
						bt.setText("" + o.StockQty + " " + o.StockUnit + " ");
					} else {
						bt.setText("" + o.StockQty + " " + o.StockUnit + " ("
								+ o.OriginalQty + " " + o.StockUnit + ")");
					}

					if (o.Extraline.length() > 0) {
						TextView bt2 = (TextView) v
								.findViewById(R.id.cri2toptext2);
						bt2.setText(o.Extraline);
						bt2.setVisibility(View.VISIBLE);
					}
					tt.setVisibility(View.VISIBLE);
					bt.setVisibility(View.VISIBLE);

				}
			}
			return v;
		}
	}

	// Recipe Ingredient
	private class RecipeIngredientAdapter extends ArrayAdapter<recipeitem> {

		private ArrayList<recipeitem> items;

		public RecipeIngredientAdapter(Context context, int textViewResourceId,
				ArrayList<recipeitem> items) {
			super(context, textViewResourceId, items);

			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.customrowitem2, null);

			}

			recipeitem o = items.get(position);
			if (o != null) {

				TextView tt = (TextView) v.findViewById(R.id.cri2toptext);
				tt.setText("" + o.StockName);

			}
			return v;
		}
	}

}
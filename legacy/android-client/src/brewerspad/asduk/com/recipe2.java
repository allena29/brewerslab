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

public class recipe2 extends Activity {

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
		

		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		//
		tabletDevice = shrdPrefs.getBoolean("tabletDevice", true);
		
		setContentView(R.layout.recipesphone2);
		

		
		recipeLogic.page2=true;
		recipeLogic.tabletDevice=false;
		recipeLogic.boss="recipe2";
		recipeLogic.recipeBoss2 = this;
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

		
		
		phoneOnCreate();
		

	}

	public void phoneOnCreate() {

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

		/* 
		 *  these are phone3
		 
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
		*/
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



	public void onRecipeStatsAndIngredientsPostUpdate(Boolean recalcNeeded) {
		// a very trimmed down version of the tablet
		ListView lv = (ListView) findViewById(R.id.listview2);
		((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();

		
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
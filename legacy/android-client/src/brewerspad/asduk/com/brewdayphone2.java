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
   
$Revision: 1.5 $ $Date: 2011-10-25 10:25:47 $ $Author: codemonkey $


 */


/*
 * 
 * some reduction gone on here
 * 
 * 
 */


import java.util.ArrayList;
import android.app.Activity;
import android.app.ProgressDialog;

import android.content.Context;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class brewdayphone2 extends Activity {

	Integer dbg = 0;

	ProgressDialog pleasewait;
	
	//common 
	brewdaycommon brewdayLogic = new brewdaycommon();

	
	// this will be populated from sharedprefs
	String hostnameIp;
	Integer portNum;
	Boolean tabletDevice;
	
	//String taskOperation;
	String brewlog;
	String recipe;
	String process;
	String activity;


	
	Boolean debugXMLRPC = true;
	
	DisplayMetrics dm;
	
	ListView listViewB;
	ListView listViewB2; 
	ArrayList<brewlogstep> listViewStepos; 
	ArrayList<brewlogstep> listViewSubStepos;	
	ArrayAdapter steposArrayAdapterB;
	ArrayAdapter subSteposArrayAdapterB;	
	
	
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		
		/*
		 * This is common with onCreate of brewday.java
		 */
		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		tabletDevice = shrdPrefs.getBoolean("tabletDevice", true);
		hostnameIp = shrdPrefs.getString("hostname", "127.0.0.1");
		portNum = shrdPrefs.getInt("portNumber", 1027);
		
		setContentView(R.layout.brewday);
	
		// set data on common object
		brewdayLogic.pleasewait = pleasewait;
		brewdayLogic.tabletDevice = tabletDevice;
		
        
		// entry point (this is a workaround while the gui isn't complete
		brewlog = shrdPrefs.getString("brewlog", "");
		recipe = shrdPrefs.getString("recipe", "");
		process = shrdPrefs.getString("process", "");
		activity = shrdPrefs.getString("activity", "");

		
		// show the background image on the layout
		String path = Environment.getExternalStorageDirectory().toString();
		String pathName = path + "/brewerspad/brewlogbg.png";
		Resources res = getResources();
		Bitmap bitmap = BitmapFactory.decodeFile(pathName);
		BitmapDrawable bd = new BitmapDrawable(res, bitmap);
		View view = findViewById(R.id.BouterLayout);
		view.setBackgroundDrawable(bd);



		
		/*
		 * This is parts of onTabletCreate()
		 */		
			
		// Reset otehr gui elemnts
		// add list view for sub steps
		listViewB2 = new ListView(this);
		listViewB2.setFocusableInTouchMode(true);
		listViewSubStepos = new ArrayList<brewlogstep>();
		subSteposArrayAdapterB = new BrewLogStepAdapter(this,	R.layout.customrowitem, listViewSubStepos);
		listViewB2.setAdapter(subSteposArrayAdapterB);	
		listViewB2.setClickable(true);
		listViewB2.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Log.i("brewerspad", "listviewB2 onItemClick");
				
				brewlogstep o = (brewlogstep) listViewB2.getItemAtPosition(arg2);
				Log.i("brewerspad", "listviewB2 Selected" + o);

				TextView tv = (TextView) findViewById(R.id.brewdaySubstepText);
				tv.setText(o.getStepName());
				
				
				// show button in 3rd third for marking complete,  
				Button  button1 = (Button) findViewById(R.id.brewdaySubstepComplete);
				button1.setVisibility(View.VISIBLE);
				
				TextView tv2 = (TextView) findViewById(R.id.brewdaySubstepComplete);
				if(o.getStepComplete() == true){
					tv2.setText("Completed: "+ o.getDateCompelte());
					button1.setText("Uncomplete");
				}else{
					tv2.setText("");
					button1.setText("Complete");
				}

				

				//View view1 = (View) findViewById(R.id.Bdivider1);
				//view1.setVisibility(View.VISIBLE);
				
				EditText et1 = (EditText) findViewById(R.id.store4packsPurchased);
				et1.setVisibility(View.VISIBLE);


				
			}		
			

		});
		
		//232
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.brewdayInner2of3bottom);
		linearLayout.addView(listViewB2, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		

			
		
		/*
		 * This is parts of the action
		 */		
			
		
				// on a tablet we need to build the list view for substeps because it is visible			
		listViewSubStepos.clear();
		ProgressBar pbar = (ProgressBar) findViewById(R.id.brewdaySubstepsProgress);


	
		// text & imgs
		LinearLayout linearLayout2 = (LinearLayout) findViewById(R.id.brewdayInner2of3top);
		LayoutParams frame2 = (LayoutParams) linearLayout2.getLayoutParams();
		
		// sub listview
		linearLayout = (LinearLayout) findViewById(R.id.brewdayInner2of3bottom);		
		LayoutParams frame = (LayoutParams) linearLayout.getLayoutParams();
				
		Button  button3 = (Button) findViewById(R.id.Bbutton3);
		
		// hide button in 3rd third for marking complete, when we open the substep we will show it
		Button  button1 = (Button) findViewById(R.id.brewdaySubstepComplete);
		button1.setVisibility(View.INVISIBLE);
		
		
		if(brewdayLogic.HaveSubSteps == true){
			Log.i("brewerspad","changing heigh of listview for substesp to a third");
								
			if(brewdayLogic.HaveImgs == false){
				frame.height = (dm.heightPixels / 6) * 5; 
				frame2.height = (dm.heightPixels / 6);						
			}else{
				frame.height = (dm.heightPixels / 6) * 3;
				frame2.height = (dm.heightPixels / 6) * 3;				
			}
			
			// hide the button in the 2nd third becuase we have substeps
			button3.setVisibility(View.INVISIBLE);
		}else{
			Log.i("brewerspad","changing heigh of listview for substesp to 0");
			
			// show button in 2nd third for marking complete because we don't have substeps
			button3.setVisibility(View.VISIBLE);			
			// hide button in 3rd third for marking complete because we don't have substeps
			button1.setVisibility(View.INVISIBLE);

			
			frame.height = 0 ;					// no substeps
			frame2.height=dm.heightPixels;		// so make big screen big
		}
		
		linearLayout.setLayoutParams(frame);
		linearLayout2.setLayoutParams(frame2);
		
		Log.i("brewerspad","notifying list set changed (substpes)");		
		subSteposArrayAdapterB.notifyDataSetChanged();


		
	}
	

	
	
	




	/*

	http://www.softwarepassion.com/android-series-custom-listview-items-and-adapters/
	*/

	private class BrewLogStepAdapter extends ArrayAdapter<brewlogstep> {
	
	        private ArrayList<brewlogstep> items;
	
	        public BrewLogStepAdapter(Context context, int textViewResourceId, ArrayList<brewlogstep> items) {
	                super(context, textViewResourceId, items);
	                
	                this.items = items;
	        }
	
	        @Override
	        public View getView(int position, View convertView, ViewGroup parent) {

	                View v = convertView;
	                if (v == null) {
	                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	                    v = vi.inflate(R.layout.customrowitem, null);
	                }
	                brewlogstep o = items.get(position);
	                if (o != null) {               		
	                        TextView tt = (TextView) v.findViewById(R.id.toptext);
							tt.setText( ""+o.getStepName() );							
							ImageView ii = (ImageView) v.findViewById(R.id.iconif);
							if(o.getStepComplete()){
								ii.setImageResource(R.drawable.tick);
							}else if (o.getStepStarted() == false){
								ii.setImageResource(R.drawable.start);								
																					
							}else{
								ii.setImageResource(R.drawable.cross);								
							}
							Log.i("bottom of image for ",o.getStepName());
							
	                }
	                return v;
	        }
	}

	
}






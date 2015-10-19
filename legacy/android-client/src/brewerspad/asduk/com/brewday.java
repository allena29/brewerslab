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
   
$Revision: 1.57 $ $Date: 2011-11-05 00:26:05 $ $Author: codemonkey $


 */

//TODO:
/*
 * NOTE: 
 * 
 * Seem to be back on track
 * Progress bar is screwed up height wise
 * 
 * 
 * and some things are not returning a completeData giving us a jsonExecption
 * 
 * 
 * 
 * 
 * 
 */
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.LinearLayout.LayoutParams;

public class brewday extends Activity {

	Boolean APIerror =false;


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

	ArrayAdapter steposArrayAdapterB;
	ArrayAdapter subSteposArrayAdapterB;	





	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		tabletDevice = shrdPrefs.getBoolean("tabletDevice", true);
		hostnameIp = shrdPrefs.getString("hostname", "127.0.0.1");
		portNum = shrdPrefs.getInt("portNumber", 1027);
		Log.i("brewerspad","about to load content of layout for brewday");
		setContentView(R.layout.brewday);


		// set outselves as a boss in brewday common
		brewdayLogic.brewdayBoss = this;
		brewdayLogic.boss = "brewday";
		brewdayLogic.hostnameIp = hostnameIp;
		brewdayLogic.portNum = portNum;
		// set data on common object so it can run it's own tasks
		brewdayLogic.pleasewait = pleasewait;
		brewdayLogic.tabletDevice = tabletDevice;
		brewdayLogic.clouddevice=shrdPrefs.getString("clouddevice", "");
		brewdayLogic.clouduser=shrdPrefs.getString("clouduser", "");
		brewdayLogic.cloudkey=shrdPrefs.getString("cloudkey", "");
		
		
		
		
		brewdayLogic.brewlog=shrdPrefs.getString("brewlog","");
		brewdayLogic.activity=shrdPrefs.getString("activity","");
		brewdayLogic.process=shrdPrefs.getString("process","");
		brewdayLogic.recipe=shrdPrefs.getString("recipe","<r>") ;
		
		// Get Screen Dimensions
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);


		// entry point (this is a workaround while the gui isn't complete
		brewlog = shrdPrefs.getString("brewlog", "");
		recipe = shrdPrefs.getString("recipe", "");
		process = shrdPrefs.getString("process", "");
		activity = shrdPrefs.getString("activity", "");


		// show the background image on the layout
		String path = Environment.getExternalStorageDirectory().toString();
		String pathName = path + "/brewerspad/brewlogbg.png.xml";
		Resources res = getResources();
		Bitmap bitmap = BitmapFactory.decodeFile(pathName);
		BitmapDrawable bd = new BitmapDrawable(res, bitmap);
		View view = findViewById(R.id.BouterLayout);
		view.setBackgroundDrawable(bd);

		// set IInitial title
		TextView activityTitle = (TextView) findViewById(R.id.brewdayActivityTitle);
		activityTitle.setText(activity);


		pleasewait();
		

		if(tabletDevice){
			Log.i("brewerspad","setting layout to landscape");
	        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		// in the preivous version we set all the widths here!


		// this is back in the right place now, the only change this time
		// is that we have a return type now
		if(tabletDevice){
			tabletOnCreate();
		}


		// then we did the list view

		// add list view for steps
		listViewB = new ListView(this);
		listViewB.setFocusableInTouchMode(true);
		listViewStepos = new ArrayList<brewlogstep>();
		steposArrayAdapterB = new BrewLogStepAdapter(this,	R.layout.customrowitem, listViewStepos);
		listViewB.setAdapter(steposArrayAdapterB);	
		listViewB.setClickable(true);
		
		listViewB.setOnItemClickListener(new AdapterView.OnItemClickListener() {		


			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				brewlogstep o = (brewlogstep) listViewB.getItemAtPosition(arg2);
				Log.i("brewerspad", "listview Selected" + o.stepId);

				Button comment =(Button) findViewById(R.id.brewdayCommentButtonB);
				if(comment.getVisibility() == View.VISIBLE){
					cannotChangeStep();
					return;
				}
				
				/*
				brewdayxmlrpc task = new brewdayxmlrpc();
				task.them = brewdayLogic;
				task.hostnameIp = hostnameIp;
				task.portNum = portNum;
				task.execute("getStepDetail",o.stepId.toString());				
				 */

				// Download the list of recipes		
				SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);

				fluffycloud cloudtask = new fluffycloud();
				cloudtask.themBrewday  = brewdayLogic;		
				cloudtask.themId = "brewday";
				cloudtask.hostnameIp = hostnameIp;
				cloudtask.cloudKey = shrdPrefs.getString("cloudkey", "expired");
				cloudtask.cloudDevice = shrdPrefs.getString("clouddevice","<testdevice>");
				cloudtask.cloudUser = shrdPrefs.getString("clouduser","<user>");
				cloudtask.portNum = portNum+1;
				
				
				cloudtask.execute("getStepDetail",shrdPrefs.getString("process","<>"), shrdPrefs.getString("activity","") ,shrdPrefs.getString("brewlog","<>"),o.stepId.toString() ,shrdPrefs.getString("recipe","<r>") );
				
				
				if(tabletDevice){
					TextView tv = (TextView) findViewById(R.id.brewdayStepText);
					tv.setText("....");
				}


				
			}
		});					 

		Log.i("brewerspad","brewday[2]");

		//175	
		// add list view for the steps (common for tablet/phone)
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.brewdayInner1of3);
		linearLayout.addView(listViewB, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT));




		//260
		//linearLayout = (LinearLayout) findViewById(R.id.brewdayOuter1of3);
		//linearLayout.addView(listViewB, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));


		// Tablet only layout
		// resize layouts into thirds
		Log.i("brewerspad","brewday[0]");

		Log.i("brewerspad","brewday[1]");


		
		// Download the list of recipes		
		fluffycloud cloudtask = new fluffycloud();
		cloudtask.themBrewday  = brewdayLogic;		
		cloudtask.themId = "brewday";
		cloudtask.hostnameIp = hostnameIp;
		cloudtask.cloudKey = shrdPrefs.getString("cloudkey", "expired");
		cloudtask.cloudDevice = shrdPrefs.getString("clouddevice","<testdevice>");
		cloudtask.cloudUser = shrdPrefs.getString("clouduser","<user>");
		cloudtask.portNum = portNum+1;
		cloudtask.execute("listProcessImages",process);



	}


	@Override
	public void onResume(){		
		super.onResume();
		Log.i("brewerspad","brewday onResume()");
		if(tabletDevice && getWindowManager().getDefaultDisplay().getWidth() > getWindowManager().getDefaultDisplay().getHeight()){
			tabletOnResume();			
		}
	}
	
	
	
	public void phoneOnResume(){
		Log.i("brewerspad","phoneOnResume()");
		
		
		//kickGui();
	}
	
	
	public void tabletOnResume(){
		Log.i("brewerspad","tabletOnResume()");

		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.BouterLayout);
		Integer height = linearLayout.getHeight() -36;		//workaround for missing steps 
		Integer width = linearLayout.getWidth();
		
		
		//     	1st third
		linearLayout = (LinearLayout) findViewById(R.id.brewdayInner1of3);		
		LayoutParams frame = (LayoutParams) linearLayout.getLayoutParams();
		frame.width = width / 3;
		frame.height =  height;
		linearLayout.setLayoutParams(frame); 
		Log.i("brewerspad"," width:" + dm.widthPixels/3+" height: "+dm.heightPixels);
		Log.i("brewerspad","brewday inner1o3 set width to a third height to heigh");
		Log.i("brewerspad","now we have converted to outerlayout we have set to width"+width/3+" height"+height);

		// 2nd third  
		linearLayout = (LinearLayout) findViewById(R.id.brewdayOuter2of3);		
		frame = (LayoutParams) linearLayout.getLayoutParams();
		frame.width = (width / 3);
		frame.height =  height;
		linearLayout.setLayoutParams(frame);
		Log.i("brewerspad"," width:" + dm.widthPixels/3+" height: "+dm.heightPixels);
		Log.i("brewerspad","brewday outer2of3 set width to a third height to heigh");
		Log.i("brewerspad","now we have converted to outerlayout we have set to width"+width/3+" height"+height);

		linearLayout = (LinearLayout) findViewById(R.id.brewdayInner2of3top);		
		frame = (LayoutParams) linearLayout.getLayoutParams();
		frame.width = (dm.widthPixels / 3) ;
		frame.height = 0 ;		
		linearLayout.setLayoutParams(frame);				

		linearLayout = (LinearLayout) findViewById(R.id.brewdayInner2of3bottom);		
		frame = (LayoutParams) linearLayout.getLayoutParams();
		frame.width = (dm.widthPixels / 3);
		frame.height = 0 ;		
		linearLayout.setLayoutParams(frame);



		// 3rd third
		linearLayout = (LinearLayout) findViewById(R.id.brewdayOuter3of3);		
		frame = (LayoutParams) linearLayout.getLayoutParams();
		frame.width = (width / 3)-64;
		frame.height =  height;		
		linearLayout.setLayoutParams(frame);
		Log.i("brewerspad"," width:" + dm.widthPixels/3+" height: "+dm.heightPixels);
		Log.i("brewerspad","brewday outer3of3 set width to a third height to heigh");
		Log.i("brewerspad","now we have converted to outerlayout we have set to width"+width/3+" height"+height);

		linearLayout = (LinearLayout) findViewById(R.id.brewdayInner3of3top);		
		frame = (LayoutParams) linearLayout.getLayoutParams();
		frame.width = (dm.widthPixels / 3)-64 ;
		frame.height =  (dm.heightPixels / 6) * 2;		
		linearLayout.setLayoutParams(frame);

		Log.i("brewerspad"," width:" + dm.widthPixels/3+" height: "+dm.heightPixels/2);
		Log.i("brewerspad","brewday inner3of3 set width to a third height to heigh");

		linearLayout = (LinearLayout) findViewById(R.id.brewdayInner3of3bottom);		
		frame = (LayoutParams) linearLayout.getLayoutParams();
		frame.width = (dm.widthPixels / 3) -64;
		frame.height =  (dm.heightPixels / 6) * 4;		
		linearLayout.setLayoutParams(frame);

		Log.i("brewerspad"," width:" + dm.widthPixels/3+" height: "+dm.heightPixels/2);
		Log.i("brewerspad","brewday inner3of3 set width to a third height to heigh");

		
		//kickGui();

	}

	public Boolean tabletOnCreate() {



		EditText etB = (EditText) findViewById(R.id.editTextB);
		etB.addTextChangedListener(new TextWatcher(){

			public void afterTextChanged(Editable s) {
				Button b =(Button) findViewById(R.id.brewdayCommentButtonB);
				b.setVisibility(View.VISIBLE);				
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }				
			public void onTextChanged(CharSequence s, int start, int before,int count) { }


	
		});	



		// Reset otehr gui elemnts
		// add list view for sub steps
		listViewB2 = new ListView(this);
		listViewB2.setFocusableInTouchMode(true);
		brewdayLogic.listViewSubStepos = new ArrayList<brewlogstep>();
		subSteposArrayAdapterB = new BrewLogStepAdapter(this,	R.layout.customrowitem, brewdayLogic.listViewSubStepos);
		
		
		listViewB2.setAdapter(subSteposArrayAdapterB);	
		listViewB2.setClickable(true);


		listViewB2.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener (){

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
    
        	Log.i("brewerspad","listviewB2 OnItemLongClick");
                    toggleSubstepHeight();
                    return true; 
    
			} 
 
				}); 
		
		
		listViewB2.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Log.i("brewerspad", "listviewB2 onItemClick");

				brewlogstep o = (brewlogstep) listViewB2.getItemAtPosition(arg2);
				Log.i("brewerspad", "listviewB2 Selected" + o);

				TextView tv = (TextView) findViewById(R.id.brewdaySubstepText);
				tv.setText(o.getStepName());
				brewdayLogic.currentSubStep=o.stepId;
				// set butons invisible
				Button  button3 = (Button) findViewById(R.id.brewdayCompleteButtonA);
				button3.setEnabled(true);
				button3.setVisibility(View.INVISIBLE);	// hide the substep button


				// show button in 3rd third for marking complete,  
				Button  button1 = (Button) findViewById(R.id.brewdayCompleteButtonB);
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

				EditText et1 = (EditText) findViewById(R.id.editTextB);
				et1.setVisibility(View.VISIBLE);



				
			}		
			
		});

		//232
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.brewdayInner2of3bottom);
		linearLayout.addView(listViewB2, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT));


		


		return true;
	}



	public void onClickSaveComment(View v){
		Log.i("brewerspad","onClickSaveComment");
		EditText et;
		//if(brewdayLogic.HaveSubSteps){
		et = (EditText) findViewById(R.id.editTextB);
//		}else{
	//		et = (EditText) findViewById(R.id.editTextA);
		//}
		
		brewdayLogic.saveComment( et.getText().toString() );


	}
	
	
	
	
	
	
	public void kickGui(){		
		if(brewdayLogic.pleasewaitActive){
			brewdayLogic.pleasewait.dismiss();
			brewdayLogic.pleasewaitActive=false;
		}
		
		
		
		if(tabletDevice && getWindowManager().getDefaultDisplay().getWidth() > getWindowManager().getDefaultDisplay().getHeight()){
			tabletOnResume();
		}else{
			phoneOnResume();
		}
	}


	public void launchWidgetFields(View v){
		Log.i("brewerspad","launchWidgetFields");
		
		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		SharedPreferences.Editor editor = shrdPrefs.edit();
		editor.putString("jsonresponse4", brewdayLogic.jsonResponseStepDetail);
		editor.commit();

		
		startActivity(new Intent(this, widgetfields.class));
		
		
	}
	public void onClickBrewdayCompleteA(View v){
		Log.i("brewerspad","onClickBrewdayComplete or UncompleteA ");
		Button b = (Button) findViewById(R.id.brewdayCompleteButtonA);
											//brewdayCompleteButtonA
		
		Button comment =(Button) findViewById(R.id.brewdayCommentButtonB);
		if(comment.getVisibility() == View.VISIBLE){
			Toast.makeText(this,"Cannot complete step because the latest comment is unsaved",Toast.LENGTH_LONG).show();
			return;
		}
		
		
		b.setEnabled(false);
		String complete;
		if(b.getText().equals("Complete")){
				complete="1";
		}else{
				complete="0";
		}

		brewdayLogic.completeStep( complete );
	}




	public void onClickBrewdayCompleteB(View v){
		Log.i("brewerspad","onClickBrewdayComplete or UncompleteB");
		
		Button comment =(Button) findViewById(R.id.brewdayCommentButtonB);
		if(comment.getVisibility() == View.VISIBLE){
			Toast.makeText(this,"Cannot complete substep because the latest comment is unsaved",Toast.LENGTH_LONG).show();
			return;
		}
		
		
		
		Button b = (Button) findViewById(R.id.brewdayCompleteButtonB);
		b.setEnabled(false);
		String complete;
		if(b.getText().equals("Complete")){
				complete="1";
		}else{
				complete="0";
		}		
		brewdayLogic.completeStep( complete); 
	}





	public void populateBrewlogActivity(String jsonResponse) {
		// this updates the 1st third 
		
		Log.i("brewerspad", "populateBrew");

		// Now parse steps from Json Reponse
		Log.i("brewerspad", "jsonResponse");
		
		listViewStepos.clear();

		
		
		try {
			JSONObject jObject = new JSONObject(jsonResponse);
			Log.i("brewerspad", jObject.toString());
			JSONArray STEPS = jObject.getJSONArray("result");

			for (int i = 0; i < STEPS.length(); ++i) {
				JSONObject stepObject = STEPS.getJSONObject(i);
				String step = stepObject.getString("name");				

				brewlogstep stepo = new brewlogstep();
				stepo.setStepName(step);
				stepo.stepId=i;
				stepo.setStepComplete( stepObject.getBoolean("complete") );

				listViewStepos.add(stepo);

			}
		} catch (JSONException e) {
			presentError("brewday.populateBrewLogDetails()/JSONException",e.getMessage(), jsonResponse);
		} catch(Exception e){
			presentError("brewday.populateBrewLogDetails()",e.getMessage(), " .. " );
		}
		Log.i("brewerspad","notifydatasetchange (steps)");		
		steposArrayAdapterB.notifyDataSetChanged();

		
		// kick Gui to get rid of the progressbar;
		kickGui();
	}





	public void toggleSubstepHeight(){
		/* toggle list view */
		Log.i("brewerspad","toggleSubstepHeight");
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.BouterLayout);
		Integer height = linearLayout.getHeight(); 
		Integer width = linearLayout.getWidth();

		
		linearLayout = (LinearLayout) findViewById(R.id.brewdayInner2of3top);		
		LayoutParams frame = (LayoutParams) linearLayout.getLayoutParams();
		frame.width = (width/3);
		frame.height = 0 ;		
		linearLayout.setLayoutParams(frame);				

		linearLayout = (LinearLayout) findViewById(R.id.brewdayInner2of3bottom);		
		frame = (LayoutParams) linearLayout.getLayoutParams();
		frame.width = (width/3);
		frame.height = height ;		
		linearLayout.setLayoutParams(frame);


		
	}








	/*
	 * 
	 * This is the hard work which carries on when we have the response
	 */
	public void decodeData(String jsonResponse,String taskOperation) {
		Log.i("brewerspad","entered decodeData() with " +taskOperation);
		//

		if (taskOperation.equals("listProcessImages")) {
			// Toast.makeText(this, "now about to execute DownloadImageTask",
			// Toast.LENGTH_LONG).show();
			Log.i("brewerspad", "decodeData - listProcessImages");

			// download the images
			DownloadImageTask task = new DownloadImageTask();
			task.execute(jsonResponse, process);

			/*
			// open the process
			brewdayxmlrpc task2 = new brewdayxmlrpc();
			task2.them = brewdayLogic;
			task2.hostnameIp = hostnameIp;
			task2.portNum = portNum;
			task2.execute("openBrewlog",process,recipe,brewlog);
			Log.i("brewerspad","guessed where some of this data comes from");
*/
			
			SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
			fluffycloud cloudtask = new fluffycloud();
			cloudtask.themBrewday  = brewdayLogic;		
			cloudtask.themId = "brewday";
			cloudtask.hostnameIp = hostnameIp;
			cloudtask.cloudKey = shrdPrefs.getString("cloudkey", "expired");
			cloudtask.cloudDevice = shrdPrefs.getString("clouddevice","<testdevice>");
			cloudtask.cloudUser = shrdPrefs.getString("clouduser","<user>");
			cloudtask.portNum = portNum+1;
			cloudtask.execute("openBrewlog",process,recipe,brewlog);


			SharedPreferences.Editor editor = shrdPrefs.edit();			
			editor = shrdPrefs.edit();
			editor.putString("process",process);
			editor.commit();
			
		}

		if (taskOperation.equals("openBrewlog")) {
			Log.i("brewerspad", "decodeData - openBrewlog");
/*
			// now we have the process open we can start listing the steps
			brewdayxmlrpc task = new brewdayxmlrpc();
			task.them = brewdayLogic;
			task.hostnameIp = hostnameIp;
			task.portNum = portNum;
			task.execute("selectActivity",activity);
*/
			SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
			fluffycloud cloudtask = new fluffycloud();
			cloudtask.themBrewday  = brewdayLogic;		
			cloudtask.themId = "brewday";
			cloudtask.hostnameIp = hostnameIp;
			cloudtask.cloudKey = shrdPrefs.getString("cloudkey", "expired");
			cloudtask.cloudDevice = shrdPrefs.getString("clouddevice","<testdevice>");
			cloudtask.cloudUser = shrdPrefs.getString("clouduser","<user>");
			cloudtask.portNum = portNum+1;
			cloudtask.execute("selectActivity",activity);
			
			 
			SharedPreferences.Editor editor = shrdPrefs.edit();			
			editor = shrdPrefs.edit();
			editor.putString("activity",activity);
			editor.commit();
			
		}

		if (taskOperation.equals("selectActivity")) {
			Log.i("brewerspad", "decodeData - selectActivity");
			
			
			SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
			Log.i("brewerspad","we have brewlog of "+shrdPrefs.getString("brewlog","<>"));
			fluffycloud cloudtask = new fluffycloud();
			cloudtask.themBrewday  = brewdayLogic;		
			cloudtask.themId = "brewday";
			cloudtask.hostnameIp = hostnameIp;
			cloudtask.cloudKey = shrdPrefs.getString("cloudkey", "expired");
			cloudtask.cloudDevice = shrdPrefs.getString("clouddevice","<testdevice>");
			cloudtask.cloudUser = shrdPrefs.getString("clouduser","<user>");
			cloudtask.portNum = portNum+1;
			cloudtask.execute("listActivitySteps",shrdPrefs.getString("process","<>"), shrdPrefs.getString("activity","") ,shrdPrefs.getString("brewlog","<>") );
			
			 /*
			SharedPreferences.Editor editor = shrdPrefs.edit();			
			editor = shrdPrefs.edit();
			editor.putString("activity",activity);
			editor.commit();
			*/
			
		}

		
		/*
		if (taskOperation.equals("listActivitySteps")) {
			// now call
			Log.i("brewerspad", "decodeData - listActivtyStpes");
			populateBrewlogActivity(jsonResponse);
			
			/// no idead why we come here at this way
		}
		*/

	}


	public void launchTools(View v){
		startActivity(new Intent(this, tools.class));
	}

	public void launchBigImg(View v){
		Log.i("brewerslab","launchBigImg");
		//Log.i("brewerslab",v.getTag() );
		// Set so that we don't close ourself
		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		SharedPreferences.Editor editor = shrdPrefs.edit();
		editor.putString("bigurl", (String) v.getTag() );
		editor.putString("webstyle", "img");
		//editor.putString("bigurl", (String) v.getTag() );
		editor.commit();
		
		
		startActivity(new Intent(this, webviewer.class));
		

	}


	public void updateBrewlogStep(String jsonResponse){

		// this is now called direct from brewdaycommon's decodeXmlrpc() operation
		/*
		 * 
		 * on a phone open up a new screen
		 */
		//


		if(!brewdayLogic.getSubStepDetail(jsonResponse)){
			presentError(brewdayLogic.exceptionTitle, brewdayLogic.exception, brewdayLogic.exceptionResponse);
			Log.i("brewerspad","returning an exception.. make sure we don't see the next line");
			return;
		}


		if(tabletDevice && getWindowManager().getDefaultDisplay().getWidth() > getWindowManager().getDefaultDisplay().getHeight()){
		
		}else{
		

			SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
			SharedPreferences.Editor editor = shrdPrefs.edit();
			editor.putString("jsonresponse", jsonResponse);
			editor.commit();

			if(brewdayLogic.HaveSubSteps){
				startActivity(new Intent(this, brewdayphone2.class));
			}else{
				Toast.makeText(this, "need to jump to brewdayphone3.java which we don't have yet", Toast.LENGTH_SHORT).show();
			}
		}


		/*
		 * 
		 *   on a tablet this updates the 2nd third of the screen. 
		 */			
		
		if(tabletDevice && getWindowManager().getDefaultDisplay().getWidth() > getWindowManager().getDefaultDisplay().getHeight()){

			Log.i("brewerspad","in brewday.updateBrewLogStep - tabletDevice");
			String widgetvalues= "";
			// populate widgets 
			Log.i("brewerspad","populating widget details");
			Log.i("brewerspad","populate()");
			try {
				JSONObject jObject = new JSONObject(jsonResponse);
				Log.i("brewerspad", "widgetsfields ()");
							
				JSONObject jResult = jObject.getJSONObject("result");
				JSONArray fields = jResult.getJSONArray("fields");
				//stepid = jResult.getInt("stepNum");				
				for (int i = 0; i < fields.length(); ++i) {
					JSONArray tmp = fields.getJSONArray(i);				
					String fieldLabel = tmp.getString(0);
					String fieldValue = tmp.getString(2);	
					widgetvalues=widgetvalues+"<b>"+fieldLabel+"</b> "+fieldValue+"<br>\n";
					Log.i("brewerspad",">>>>>>>> fieldName "+fieldLabel+" "+fieldValue);
				}
				
				
				if(brewdayLogic.HaveImgs){
				// set images if we have any
			        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			        	Toast.makeText(this,"SD Card not mounted, cannot load image",Toast.LENGTH_LONG).show();
			        }else{
						ImageView iv = (ImageView) findViewById(R.id.brewdayStepProcessImg);
						iv.setVisibility(View.VISIBLE);
						JSONArray imgs = jResult.getJSONArray("img");
						//stepid = jResult.getInt("stepNum");				
						for (int i = 0; i < imgs.length(); ++i) {
							// TODO: in future need an image for each image in the result
							Log.i("brewerspad","need to open image "+imgs.getString(i));
							String path = Environment.getExternalStorageDirectory().toString();
							
							Log.i("brewerspad"," maybe a new exception");
							Log.i("brewerspad"," maybe a new exception" + jResult.getString("process"));
							String pathName = path + "/brewerspad/"+ jResult.getString("process") +"_" + imgs.getString(i)+".xml";
							//iv.setTag(pathName);
							iv.setTag(jResult.getString("process") +"_"+imgs.getString(i)+".xml");
							
							
							Log.i("brewerspad"," img url"+pathName);
							
							Log.i("brewerspad", "full path for img "+pathName);
							//Resources res = getResources();
							Bitmap bitmap = BitmapFactory.decodeFile(pathName);
							//BitmapDrawable bm = new BitmapDrawable(res, bitmap);
							iv.setImageBitmap(bitmap);
						
							//iv.setImageBitmap(bg);
						}
					}
					
				}else{
					ImageView iv = (ImageView) findViewById(R.id.brewdayStepProcessImg);
					iv.setVisibility(View.GONE);
				}

				
			}catch(JSONException e){
				Log.i("brewerspad","exception while decoding fields/widgets in brewday");
				
			}
				
			if(widgetvalues.length() > 0){
				TextView wafh = (TextView) findViewById(R.id.brewdayFieldHeaders);			
				wafh.setVisibility(View.VISIBLE);
				TextView waf = (TextView) findViewById(R.id.brewdayFieldValues);
				waf.setText(Html.fromHtml(widgetvalues));
			}else{
				TextView wafh = (TextView) findViewById(R.id.brewdayFieldHeaders);			
				wafh.setVisibility(View.INVISIBLE);
				
				
			}
			
			
			// get the substeps populated in the list view
			Log.i("brewerspad","after exception.. make sure we don't see the previous line");

			// set text views of the left hand so they are blank.
			TextView stt = (TextView) findViewById(R.id.brewdaySubstepText);
			TextView stc = (TextView) findViewById(R.id.brewdaySubstepComplete);		
			stc.setText("");
			stt.setText("");
			TextView tvt = (TextView) findViewById(R.id.brewdayStepName);								
			TextView tv = (TextView) findViewById(R.id.brewdayStepText);				
			TextView tvc = (TextView) findViewById(R.id.brewdayStepComplete);





			// set progress bar
			ProgressBar pbar = (ProgressBar) findViewById(R.id.brewdaySubstepsProgress);
			pbar.setProgress( brewdayLogic.StepSubStepProgress );


			tvt.setText( brewdayLogic.StepTitle);
			tv.setText(  brewdayLogic.StepText);
			tvc.setText("");

			
			// set warning details
			if(brewdayLogic.StepWarning.length() > 0){
				TextView tvw = (TextView) findViewById(R.id.brewdayStepWarning);
				tvw.setVisibility(View.VISIBLE);
				tvw.setText( brewdayLogic.StepWarning );
				ImageView ivw = (ImageView) findViewById(R.id.brewdayStepWarningImg);
				ivw.setVisibility(View.VISIBLE);
								
			}else{
				TextView tvw = (TextView) findViewById(R.id.brewdayStepWarning);
				tvw.setVisibility(View.GONE);
				tvw.setText( "");
				ImageView ivw = (ImageView) findViewById(R.id.brewdayStepWarningImg);
				ivw.setVisibility(View.GONE);
								
			}
			
			TextView commenttext = (TextView) findViewById(R.id.brewdayCommentsTitleB);
			commenttext.setText("Comments "+brewdayLogic.StepCommentTimestamp);
			
			
			if( brewdayLogic.HaveSubSteps==false){
				Log.i("brewerspad","Showing editTextA hiding editTextB beause we don't have substeps");
				EditText edittextB = (EditText) findViewById(R.id.editTextB );
				edittextB.setText( brewdayLogic.StepComments );				
				edittextB.setVisibility(View.VISIBLE);
				
				//EditText edittextB = (EditText) findViewById(R.id.editTextB);				
				//edittextB.setVisibility(View.INVISIBLE);

				//TextView commentTitleA = (TextView) findViewById(R.id.brewdayCommentsTitleA);
				TextView commentTitleB = (TextView) findViewById(R.id.brewdayCommentsTitleB);
				//commentTitleA.setVisibility(View.VISIBLE);
				commentTitleB.setVisibility(View.VISIBLE);					

				tvc.setText("Complete: "+ brewdayLogic.StepCompleteDate);					
				Button  button3 = (Button) findViewById(R.id.brewdayCompleteButtonA);
				if( brewdayLogic.StepComplete ){
					Log.i("brewerspad","setting uncomplete");
					button3.setText("Uncomplete");					
				}else{
					Log.i("brewerspad","setting complete");
					button3.setText("Complete");
				}

				View divider = findViewById(R.id.brewdayDivider3);
				divider.setVisibility(View.INVISIBLE);


				pbar.setVisibility(View.INVISIBLE);
				
				Log.i("brewerspad","end of HaveSubSteps == false");
			}else{
				Log.i("brewerspad","Showing editTextB hiding editTextA beause we do t have substeps");					
				EditText edittextB = (EditText) findViewById(R.id.editTextB);
				edittextB.setText( brewdayLogic.StepComments );					
				edittextB.setVisibility(View.VISIBLE);
				//EditText edittextA = (EditText) findViewById(R.id.editTextA);										
				//edittextA.setVisibility(View.GONE);

				//TextView commentTitleA = (TextView) findViewById(R.id.brewdayCommentsTitleA);
				TextView commentTitleB = (TextView) findViewById(R.id.brewdayCommentsTitleB);
				commentTitleB.setVisibility(View.VISIBLE);
				//commentTitleA.setVisibility(View.GONE);	

				View divider = findViewById(R.id.brewdayDivider3);
				divider.setVisibility(View.VISIBLE);


				pbar.setVisibility(View.VISIBLE);
				
				Log.i("brewerspad","end of HaveSubSteps == true");
			}


			Button Bbutton6 = (Button) findViewById(R.id.brewdayWidgetButton);
			if( brewdayLogic.HaveWidgets ){
				Bbutton6.setVisibility(View.VISIBLE);
			}else{
				Bbutton6.setVisibility(View.INVISIBLE);
			}
			
			if( brewdayLogic.HaveFields){
				Bbutton6.setVisibility(View.VISIBLE);
			}



			// text & imgs
			LinearLayout linearLayout2 = (LinearLayout) findViewById(R.id.brewdayInner2of3top);
			LayoutParams frame2 = (LayoutParams) linearLayout2.getLayoutParams();

			// sub listview
			LinearLayout linearLayout = (LinearLayout) findViewById(R.id.brewdayInner2of3bottom);		
			LayoutParams frame = (LayoutParams) linearLayout.getLayoutParams();

			// set butons invisible
			Button  button3 = (Button) findViewById(R.id.brewdayCompleteButtonA);
			button3.setEnabled(true);
			
			// hide button in 3rd third for marking complete, when we open the substep we will show it
			Button  button1 = (Button) findViewById(R.id.brewdayCompleteButtonB);
			button1.setVisibility(View.INVISIBLE);
			button1.setEnabled(true);

			
			
			// Layout heights is done here
			if(brewdayLogic.HaveSubSteps == true){
				

				if(brewdayLogic.HaveImgs == false){
					frame.height = (dm.heightPixels / 2); 
					frame2.height = (dm.heightPixels / 2);						
				}else{
					frame.height = (dm.heightPixels / 2);

					frame2.height = (dm.heightPixels / 2);				
					
				}

				frame.height = frame.height - 80;		/// asus transformer only
				
				
				
				// hide the button in the 2nd third becuase we have substeps
				button3.setVisibility(View.INVISIBLE);
				
			}else{
				Log.i("brewerspad","changing heigh of listview for substesp to 0");

				// show button in 2nd third for marking complete because we don't have substeps
				button3.setVisibility(View.VISIBLE);			
				// hide button in 3rd third for marking complete because we don't have substeps
				button1.setVisibility(View.INVISIBLE);

				
				/*
				 * 
				 * Now we have the text comments we should set the frame to 0 height
				
				frame.height = 0 ;					// no substeps
				*/
				frame.height=dm.heightPixels/2;
				frame2.height=dm.heightPixels /2;		// so make big screen big
			}

			linearLayout.setLayoutParams(frame);
			linearLayout2.setLayoutParams(frame2);

			
			
			
			Log.i("brewerspad","notifying list set changed (substpes)");		
			subSteposArrayAdapterB.notifyDataSetChanged();

		}
		
		
		// hide the button for the comments
		Button  button2B = (Button) findViewById(R.id.brewdayCommentButtonB);
		button2B.setVisibility(View.INVISIBLE);

		if(tabletDevice && getWindowManager().getDefaultDisplay().getWidth() > getWindowManager().getDefaultDisplay().getHeight()){
			EditText et = (EditText) findViewById(R.id.editTextB);
			et.setFocusableInTouchMode(true);
			et.setFocusable(true);			
		}
		
		
		Log.i("brewerspad","last command of updateding step functonninin");
	}




	public void cannotChangeStep(){
		Toast.makeText(this,"Cannot change step because the latest comment is unsaved",Toast.LENGTH_LONG).show();
	}

	
	public void pleasewait(){
		//progressbar
		brewdayLogic.pleasewait = ProgressDialog.show(this, "", "Please Wait Connecting to the cloud", true,true);				
		brewdayLogic.pleasewaitActive=true;
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
		inflater.inflate(R.menu.menu3, menu);

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

		case R.id.menuReset:
			//Toast.makeText(this,"Brewday " + brew)
			Log.i("brewerspad","reset brewlog (aka recompile)");
			Log.i("brewerspad","brewlog: "+brewdayLogic.brewlog);
			Log.i("brewerspad","recipe: "+brewdayLogic.recipe);
			
			fluffycloud cloudtask = new fluffycloud();
			cloudtask.themBrewday = brewdayLogic;
			cloudtask.themId = "brewday";
			cloudtask.hostnameIp = hostnameIp;
			cloudtask.cloudKey = shrdPrefs.getString("cloudkey", "expired");
			cloudtask.cloudDevice = shrdPrefs.getString("clouddevice","<testdevice>");
			cloudtask.cloudUser = shrdPrefs.getString("clouduser", "<user>");
			cloudtask.portNum = portNum + 1;
			cloudtask.execute("resetBrewlog",brewdayLogic.recipe,brewdayLogic.brewlog);
			
			return true;
		case R.id.menuCalclog:
			Integer portNum1 = portNum + 1;
			Log.i("brewerslab","launchCalclog");
			//Log.i("brewerslab",v.getTag() );
			// Set so that we don't close ourself
			String weburl = "http://" + hostnameIp+":"+portNum1+"/ngbrewlab.py?rawCalcLog="+recipe+"&owner=" + shrdPrefs.getString("clouduser", "<user>")+"&brewlog="+brewdayLogic.brewlog;
			Log.i("brewerslab","url to our calclog "+weburl);
			editor.putString("weburl", weburl);
			editor.putString("webstyle", "url");
			editor.commit();		
			
			startActivity(new Intent(this, webviewer.class));
			
			
			return true;
		}
		
		

		return false;
	}

	
	

	public void updateGui(String jsonresponse, String taskoperation){
		Log.i("brewerspad","brewday.updateGui");
		Log.i("brewerspad","taskoperation"+taskoperation);
		Log.i("brewerspad","jsonresponse"+jsonresponse);
		
		
		if(taskoperation.equals("saveComment")){
		//	if(brewdayLogic.HaveSubSteps){
			Button  button2B = (Button) findViewById(R.id.brewdayCommentButtonB);
			button2B.setVisibility(View.INVISIBLE);
		//	}else{
		//		Button  button2 = (Button) findViewById(R.id.brewdayCommentButtonA);
		//		button2.setVisibility(View.INVISIBLE);
		//	}

		}
		
	}


	// Images
	private class DownloadImageTask extends AsyncTask<String, Void, String> {
		String response = "";
		Integer status = 0;

		@Override
		protected String doInBackground(String... jsonResponseL) {
			Log.i("brewerspad", "downladImageTask() in doInBackground()");

			String jsonResponse = jsonResponseL[0];
			String process = jsonResponseL[1];

			String path = Environment.getExternalStorageDirectory().toString();
			new File(path + "/brewerspad").mkdirs();

			try {
				JSONObject jObject = new JSONObject(jsonResponse);
				// Toast.makeText(this,"jsonResponse"+jsonResponse,Toast.LENGTH_LONG).show();

				JSONArray IMAGES = jObject.getJSONArray("result");
				for (int i = 0; i < IMAGES.length(); ++i) {
					String image = IMAGES.getString(i);

					File file = new File(path + "/brewerspad/" + process + "_"	+ image+".xml");
					if (file.exists() == false) {
						try {
							Log.i("brewerspad","about to download http://brewerspad.mellon-collie.net/processimgs/"+ process + "/" + image);
							URL url = new URL("http://brewerspad.mellon-collie.net/processimgs/"+ process + "/" + image); // you can

							//long startTime = System.currentTimeMillis();
							URLConnection ucon = url.openConnection();
							InputStream is = ucon.getInputStream();
							BufferedInputStream bis = new BufferedInputStream(
									is);

							/*
							 * Read bytes to the Buffer until there is nothing
							 * more to read(-1).
							 */
							ByteArrayBuffer baf = new ByteArrayBuffer(50);
							int current = 0;
							while ((current = bis.read()) != -1) {
								baf.append((byte) current);
							}

							/* Convert the Bytes read to a String. */
							FileOutputStream fos = new FileOutputStream(file);
							fos.write(baf.toByteArray());
							fos.close();							

						} catch (IOException e) {
							//presentError("brewday.downloadImageTask()/IOException",e.getMessage(), "no response");
							// don't present this error

							Log.i("brewerspad","file not available on webserver:");
							Log.i("brewerspad","exception (no presentation)"+e.getMessage());
						}
						Log.i("brewerspad", "file not already on sdcard:"	+ image);
					} else {
						Log.i("brewerspad", "skipping file already on sdcard:"	+ image);
					}

				}

			} catch (JSONException e) {
				presentError("brewday.downloadImageTask()/JSONException","JsonException"+e.getMessage(), "no response");
			} catch (Exception e) {
				presentError("brewday.downloadImageTask()","Exception"+e.getMessage(), "no response");
			}		

			return response;

		}
	}


	
	
	public void presentError(String title,String exception, String jsonResponse){

		//Toast.makeText(this, "Unable to communicate with the brewerspad server.", Toast.LENGTH_LONG).show();

		Log.i("brewerspad", "________________________________________________________________________");
		Log.i("brewerspad", "		Error/Exception in "+title);
		Log.i("brewerspad", "Response"+jsonResponse);
		Log.i("brewerspad", "Exception"+exception);
		Log.i("brewerspad", "________________________________________________________________________");
		if(debugXMLRPC==true){
			SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
			SharedPreferences.Editor editor = shrdPrefs.edit();
			editor.putString("errorlocation", "Error:" +title );
			editor.putString("exception","Exception:" +exception);
			editor.putString("jsonresponse", "Response:"+jsonResponse);
			editor.commit();
			startActivity(new Intent(this, debugxmlrpc.class));
			
			kickGui();
		}

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
				
				if(!o.stepNeedToComplete){
					ii.setImageResource(R.drawable.spacer);
				}else{
					if(o.getStepComplete()){
						ii.setImageResource(R.drawable.tick);
					}else if (o.getStepStarted() == false){
						ii.setImageResource(R.drawable.start);								
					}else{
						ii.setImageResource(R.drawable.cross);								
					}
				}
				Log.i("bottom of image for ",o.getStepName());

			}
			return v;
		}
	}


}




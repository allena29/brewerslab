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
   
$Revision: 1.12 $ $Date: 2011-11-05 00:26:05 $ $Author: codemonkey $


 */


import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.method.NumberKeyListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;







public class widgetfields extends Activity {
	
	
	brewdaycommon brewdayLogic;
	Boolean APIerror;
	String hostnameIp;
	Integer portNum;
	Boolean tabletDevice;

	
	Integer stepid =-1;
	
	
	ArrayList edittexts = new ArrayList<EditText>();
	ArrayList fieldkeys = new ArrayList<String>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		

		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		tabletDevice = shrdPrefs.getBoolean("tabletDevice", true);
		hostnameIp = shrdPrefs.getString("hostname", "127.0.0.1");
		portNum = shrdPrefs.getInt("portNumber", 1027);
		
		


		
		Log.i("brewerspad","about to load content of layout for widgetsfields");

		setContentView(R.layout.widgetfields);

		
		brewdayLogic = new brewdaycommon();
		// set outselves as a boss in storecommon
		brewdayLogic.widgetsBoss = this;
		brewdayLogic.boss = "widgets";
		brewdayLogic.hostnameIp = hostnameIp;
		brewdayLogic.portNum = portNum;
		// set data on common object
		//brewdayLogic.pleasewait = pleasewait;
		brewdayLogic.tabletDevice = tabletDevice;

		
		Log.i("brewerspad","prePopulate()");
		populate();
	}

	
	public void populate(){
		
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.widgetsFields);

		/// this will come in from the launcher
		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		String jsonResponse = shrdPrefs.getString("jsonresponse4", "");

		//String jsonResponse = " {result : {\"fields\": [[\"Original Gravity\", \"og\", \"\",\"dummywidget\"], [\"Fermentation bin Weight\", \"postboilweight\", \"\",\"\"], [\"Fermentation bin vol\", \"postboilvol\", \"\",\"\"], [\"Wort left in boiler vol\", \"leftovervol\", \"\",\"\"]] } }";
		
		Log.i("brewerspad","populate()");
		try {
			JSONObject jObject = new JSONObject(jsonResponse);
			Log.i("brewerspad", "widgetsfields ()");
						
			JSONObject jResult = jObject.getJSONObject("result");
			JSONArray fields = jResult.getJSONArray("fields");
			stepid = jResult.getInt("stepNum");
			
			
			
			for (int i = 0; i < fields.length(); ++i) {
				JSONArray tmp = fields.getJSONArray(i);
			

				String fieldLabel = tmp.getString(0);
				String fieldName = tmp.getString(1);
				String fieldValue = tmp.getString(2);		
				String fieldWidget = tmp.getString(3);
				Log.i("brewerspad","ok heree");

				Log.i("brewerspad","fieldName"+fieldName);
				TextView tv = new TextView(this);
				tv.setText( fieldLabel );
				tv.setTextSize(18);
				
				EditText et = new EditText(this);

				et.setText( fieldValue );

				edittexts.add(et);
				fieldkeys.add( fieldName );
				
				
				
				/*
				 * 
				 * If we are a widget
				 * 
				 * 
				 */
				Boolean isWidget=false;
				String textformat="decimal";
				if(fieldWidget.length()> 0 ){
					isWidget=true;
					if(fieldWidget.equals("decimal")){isWidget=false;textformat="decimal";}
					if(fieldWidget.equals("string")){isWidget=false;textformat="string";}					
				}
				
					
				if(isWidget){
					et.setEnabled(false);
					if(fieldValue.length() == 0){
						et.setText("widget");
					}
				}else{
					if(textformat.equals("decimal")){

						//et.setInputType(8192);
						
						et.setInputType(InputType.TYPE_CLASS_PHONE);
						et.setKeyListener(new NumberKeyListener() {
							
							public int getInputType() {
								return InputType.TYPE_CLASS_PHONE;
							}
							
							@Override
							protected char[] getAcceptedChars() {

								return new char[] {'0','1','2','3','4','5','6','7','8','9','.'} ;
							}
						});
						Log.i("brewerspad","setting text format to decimal/8192");
					
						

					}else if(textformat.equals("string")){
						et.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
						Log.i("brewerspad","setting text format to string/131072");
						//et.setInputType(131072);
					}
				}
				
				
				Button bt =  new Button(this);
				bt.setText("...");
				bt.setTag( i );
				bt.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						Log.i("brewerspad","Clicked and item "+v.getTag());
						Integer index = (Integer) v.getTag();
						
						EditText tmpet=(EditText) edittexts.get(index  );
						Editable input = tmpet.getText();
						
						String fieldValue = input.toString();
						String fieldKey = (String) fieldkeys.get(index);
						Log.i("brewerspad","Clicked and item "+ fieldKey);;
						Log.i("brewerspad","Clicked and item "+  fieldValue );
						
/*
						brewdayxmlrpc task = new brewdayxmlrpc();
						task.them = brewdayLogic;
						task.hostnameIp = hostnameIp;
						task.portNum = portNum;
						task.execute("setFieldWidget",stepid.toString(), fieldKey ,fieldValue, index.toString() );
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
						Log.i("brewerspad","guessing for "+shrdPrefs.getString("activity","<activity>"));
						Log.i("brewerspad","guessing for "+shrdPrefs.getString("brewlog","<brewlog"));
						Log.i("brewerspad","guessing for "+shrdPrefs.getString("process","<prcoess>"));
						
						cloudtask.execute("setFieldWidget",shrdPrefs.getString("process","<process>"),shrdPrefs.getString("brewlog","<brewlog"), shrdPrefs.getString("activity","<activity>"), stepid.toString(),fieldKey,fieldValue,index.toString() );
						
						
					}		


				});					 
			
				LinearLayout ll = new LinearLayout(this);
				ll.setPadding(4,0,0,0);
				ll.setOrientation(LinearLayout.HORIZONTAL);
				
				
				
				
				ll.addView(et, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				
				LayoutParams lparm = (LayoutParams) et.getLayoutParams();
				lparm.width=300;
				et.setLayoutParams(lparm);
				ll.addView(bt, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT));				
				
				linearLayout.addView(tv, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				linearLayout.addView(ll, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			}
	
	
		} catch(JSONException e){
			Log.i("brewerspad","JSONexception"+e.getMessage());
		}
		
		
		
		
		
	}
	

	public void updateField(String jsonresponse, String taskoperation){
		
		
		try {
			JSONObject jObject = new JSONObject(jsonresponse);
			Log.i("brewerspad", "widgetsfields.updateField()");					
			JSONObject jResult = jObject.getJSONObject("result");

			String newFieldValue = jResult.getString("value");
			Log.i("brewerspad","got new value back"+newFieldValue);
			Integer index = jResult.getInt("guiid");

			EditText tmpet=(EditText) edittexts.get(index  );
			tmpet.setText( newFieldValue );

			
			
		} catch(JSONException e){
			Log.i("brewerspad","JSONexception"+e.getMessage());
		}
	
		
	}


	public void presentError(String title,String exception, String jsonResponse){

		//Toast.makeText(this, "Unable to communicate with the brewerspad server.", Toast.LENGTH_LONG).show();

		Log.i("brewerspad", "________________________________________________________________________");
		Log.i("brewerspad", "		Error/Exception in "+title);
		Log.i("brewerspad", "Response"+jsonResponse);
		Log.i("brewerspad", "Exception"+exception);
		Log.i("brewerspad", "________________________________________________________________________");

			SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
			SharedPreferences.Editor editor = shrdPrefs.edit();
			editor.putString("errorlocation", "Error:" +title );
			editor.putString("exception","Exception:" +exception);
			editor.putString("jsonresponse", "Response:"+jsonResponse);
			editor.commit();
			startActivity(new Intent(this, debugxmlrpc.class));


	}

}

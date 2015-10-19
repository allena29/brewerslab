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
   
$Revision: 1.20 $ $Date: 2011-11-04 22:55:46 $ $Author: codemonkey $


 */


import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.util.Log;



public class brewdaycommon extends Activity {


	public ProgressDialog pleasewait;
	public Boolean pleasewaitActive=false;
	public Boolean tabletDevice;
	
	public String exception = "";
	public String exceptionTitle = "";
	public String exceptionResponse = "";

	public String hostnameIp="";
	public Integer portNum=0;
	
	public Boolean HaveSubSteps=false;
	public Boolean HaveWidgets=false;
	public Boolean HaveImgs=false;
	public Boolean HaveFields=false;

	
	public String jsonResponseStepDetail;
	public String StepWarning="";
	public String StepText="";
	public String StepTitle="";
	public String StepComments="";
	public String StepCommentTimestamp="";
	public String StepCompleteDate="";
	public Boolean StepComplete = false;
	public Integer StepSubStepProgress=0;
	
	public Integer  currentStep= -1;		// brewdayxmlrpc sets this
	public Integer  currentSubStep= -1;	//brewdayxmlrpc set this

	public ArrayList<brewlogstep> listViewSubStepos;

	
	public String boss = "<na>";
	public brewday brewdayBoss;
	public widgetfields widgetsBoss;

	// need this so we can launch our own cloud tasks
	public String cloudkey= "";
	public String clouduser="";
	public String clouddevice="";
	
	
	// other misc
	public String brewlog="";
	public String activity="";
	public String process="";
	public String recipe="";
	
	
	/*
	 * On a phone many activities will be used for the same purpose
	 * On a tablet a small number of activities will be used for the same purpose
	 */


	public void completeStep(String complete){
		// save the step as complete
		
		/*
		brewdayxmlrpc task2 = new brewdayxmlrpc();
		task2.them = this;
		task2.hostnameIp = hostnameIp;
		task2.portNum = portNum;
		*/
		

		// Download the list of recipes		
		fluffycloud cloudtask = new fluffycloud();
		cloudtask.themBrewday = this;
		cloudtask.themId = "brewday";
		cloudtask.hostnameIp = hostnameIp;
		cloudtask.cloudKey = cloudkey;
		cloudtask.cloudDevice = clouddevice;
		cloudtask.cloudUser = clouduser;
		cloudtask.portNum = portNum+1;
		


		
		if(HaveSubSteps == false){
		//	task2.execute("setStepComplete",currentStep.toString(),complete);
			Log.i("brewerspad","just a guess"+ brewlog+"////"+activity); 
			//cloudtask.execute("listRecipes");
			cloudtask.execute("setStepComplete",brewlog,activity,currentStep.toString(),complete);
		}else{
			//task2.execute("setSubStepComplete",currentStep.toString(),currentSubStep.toString(),complete);
			//
			Log.i("brewerspad","just a guess"+ brewlog+"/////"+activity);
			cloudtask.execute("setSubStepComplete",brewlog,activity,currentStep.toString(),currentSubStep.toString(),complete);
			
			
			
		}
		
				
	}
	
	
	
	
	
	public void saveComment(String commentText){
		/*
		 * 
		 * 
		 * Note: this doesn't work in commoon
		 * 
		 * EditText et;
		
		if(tabletDevice){
			if(HaveSubSteps){
				et = (EditText) findViewById(R.id.editTextB);
			}else{
				et = (EditText) findViewById(R.id.editTextA);
			}
		} else {
			// this will need to change
			et = (EditText) findViewById(R.id.editText1);
		}
		*/
		
		//Editable commentText = et.getText();
		Log.i("brewerspad","comment " + commentText);
		//Log.i("brewerspad","comment " + commentText.toString());
		// save the step as complete
		
		/*
		brewdayxmlrpc task2 = new brewdayxmlrpc();
		task2.them = this;
		task2.hostnameIp = hostnameIp;
		task2.portNum = portNum;
		task2.execute("saveComment",currentStep.toString(),commentText.toString());
		*/

		fluffycloud cloudtask1 = new fluffycloud();
		cloudtask1.themBrewday  = this;		
		cloudtask1.themId = "brewday";
		cloudtask1.hostnameIp = hostnameIp;
		cloudtask1.cloudKey = cloudkey;
		cloudtask1.cloudDevice = clouddevice;
		cloudtask1.cloudUser = clouduser;
		cloudtask1.portNum = portNum+1;
		cloudtask1.execute("saveComment",brewlog,process,activity,currentStep.toString(),commentText.toString());
					
		
//		pleasewait.show();

	}
	


	
	
	
	
	public Boolean getSubStepDetail(String jsonResponse){
		// this is called getSubStepDetail but really it is providing the 
		// postexecute json decoding for getStepDetail, but called from brewday.java
		
		// on a tablet we need to build the list view for substeps because it is visible			
		listViewSubStepos.clear();

		
		jsonResponseStepDetail = jsonResponse;		/// keep this for later (the widget launcher 
		try {
			JSONObject jObject = new JSONObject(jsonResponse);
			Log.i("brewerspad", "brewdaycommon.getSubSteps()");
			
			
			
			
			JSONObject jResult = jObject.getJSONObject("result");
			JSONArray substeps = jResult.getJSONArray("substeps");
			
			currentSubStep=-1;
					
			currentStep=jResult.getInt("stepNum");
					
			if(substeps.length() > 0){
					HaveSubSteps=true;
			}else{
					HaveSubSteps=false;
			}
			JSONArray imgs = jResult.getJSONArray("img");
			if(imgs.length() > 0){HaveImgs=true;}
		
			HaveWidgets = jResult.getBoolean("widgets");
			JSONArray tmpFields = jResult.getJSONArray("fields");
			if(tmpFields.length() > 0){
				HaveFields = true;
			}else{
				HaveFields = false;
			}
			
			
			StepWarning = jResult.getString("warning");
			
			StepTitle =jResult.getString("title");
			StepText = jResult.getString("text");
			StepComplete = jResult.getBoolean("complete");
			StepComments =  jResult.getString("comments");
			StepCommentTimestamp = jResult.getString("commentsTimestamp");
			for (int i = 0; i < substeps.length(); ++i) {
				JSONObject substep= substeps.getJSONObject(i);
				
				brewlogstep stepo = new brewlogstep();
				stepo.setStepName( substep.getString("text"));
				stepo.stepId=i;			
				stepo.stepNeedToComplete=substep.getBoolean("needtocomplete");
				//if(substep.getBoolean("needtocomplete") == False){
//					stepo.stepNeedToComplete=false;
	//			}
				
				stepo.setStepComplete( substep.getBoolean("complete") );
				if(substep.getBoolean("complete")){
					stepo.setDateComplete( substep.getString("completeDate"));
				}
				listViewSubStepos.add(stepo);
			}

			StepSubStepProgress = jResult.getInt("substepcomplete");
			
				
			
		}catch(JSONException e){
			exceptionTitle="brewdaycommon.updateBrewDayStep()/JsonException";
			exception=e.getMessage();
			exceptionResponse=jsonResponse;
			return false;			
		}catch(Exception e){
			exceptionTitle="brewdaycommon.updateBrewDayStep()/Exception";
			exception=e.getMessage();
			exceptionResponse=jsonResponse;
			return false;			
		}		

		return true;
	}
	
	


	
	public void updateStepComplete(String jsonResponse,String taskOperation){
		
			Log.i("brewerspad","brewday.updateStepComplete");
			Log.i("brewerspad","taskoperation"+taskOperation);
			Log.i("brewerspad","jsonresponse"+jsonResponse);
			
			
			try {
				JSONObject jObject = new JSONObject(jsonResponse);
				JSONObject jResult = jObject.getJSONObject("result");
				Log.i("brewerspad", "brewdaycommon.getSubSteps()");
				
				if(taskOperation.equals("setSubStepComplete")){
					if(jResult.getBoolean("lastcomplete")){
							//
					}else{
							//
					}						
				}else{
					if(jResult.getBoolean("lastcomplete")){
							//
					}else{
							//
					}					
				}
				
				
				/*
				brewdayxmlrpc task = new brewdayxmlrpc();
				task.them = this;
				task.hostnameIp = hostnameIp;
				task.portNum = portNum;
				task.execute("getStepDetail", jResult.getString("stepid") );
				*/

				fluffycloud cloudtask2 = new fluffycloud();
				cloudtask2.themBrewday  = this;		
				cloudtask2.themId = "brewday";
				cloudtask2.hostnameIp = hostnameIp;
				cloudtask2.cloudKey = cloudkey;
				cloudtask2.cloudDevice = clouddevice;
				cloudtask2.cloudUser = clouduser;
				cloudtask2.portNum = portNum+1;
				cloudtask2.execute("getStepDetail",process,activity,brewlog, currentStep.toString(),recipe);
				
				
				/*
			 * brewdayxmlrpc task2 = neayxmlrpc(); task2.them = this;
			 * task2.hostnameIp = hostnameIp; task2.portNum = portNum;
			 * task2.execute("listActivitySteps");
			 */
				
				fluffycloud cloudtask1 = new fluffycloud();
				cloudtask1.themBrewday  = this;		
				cloudtask1.themId = "brewday";
				cloudtask1.hostnameIp = hostnameIp;
				cloudtask1.cloudKey = cloudkey;
				cloudtask1.cloudDevice = clouddevice;
				cloudtask1.cloudUser = clouduser;
				cloudtask1.portNum = portNum+1;
				cloudtask1.execute("listActivitySteps",process,activity,brewlog );
							


				
				
				
			}catch(JSONException e){
				exceptionTitle="brewdaycommon.updateBrewDayStep()/JsonException";
				exception=e.getMessage();
				exceptionResponse=jsonResponse;
							
			}catch(Exception e){
				exceptionTitle="brewdaycommon.updateBrewDayStep()/Exception";
				exception=e.getMessage();
				exceptionResponse=jsonResponse;
							
			}		

			
	}
	
	public void decodeXmlrpc(String jsonresponse,String taskOperation, String exception){
		/*
		 * 
		 * This will do nothing more than call out to another operation to do something.
		 * and if we have an exception coming in it will throw the exception early on.
		 * 
		 */
		Log.i("brewerspad","brewdaycommon.decodeXmlrpc");
		Log.i("brewerspad","jsonresponse "+jsonresponse);
		Log.i("brewerspad","taskOperation "+taskOperation);
		Log.i("brewerspad","boss "+boss);

		


		if(exception.length() > 0){
			Log.i("brewerspad","brewdaycommon.decodeXmlrpc - exception");
			findErrorPresenter(jsonresponse, taskOperation, exception);
			// if we have an exception in retreiving the xmlrpc call  then we need an exception
			return;
		}
		
		/*
		 * 
		 * 
		 * 
		 * this isn't following the pattern but is a wrokaround for now
		 * 
		 * 
		 * 
		 */
		
		if(taskOperation.equals("openBrewlog")){
			//populateIngredientsAndSuppliers(jsonresponse);
			brewdayBoss.decodeData(jsonresponse, taskOperation);
		}
		
		if(taskOperation.equals("listActivitySteps")){
		
			brewdayBoss.populateBrewlogActivity(jsonresponse);
		

		}

		if(taskOperation.equals("selectActivity")){
			brewdayBoss.decodeData(jsonresponse, taskOperation);
		}
			
			
		if(taskOperation.equals("getStepDetail")){

			brewdayBoss.updateBrewlogStep(jsonresponse);					
		}
	
		if(taskOperation.equals("listProcessImages")){
			brewdayBoss.decodeData(jsonresponse, taskOperation);
		}
		
		if(taskOperation.equals("setStepComplete")){
			updateStepComplete(jsonresponse,taskOperation);
			
		}
		if(taskOperation.equals("setSubStepComplete")){
			updateStepComplete(jsonresponse, taskOperation);			
		}
		if(taskOperation.equals("saveComment")){
			brewdayBoss.updateGui(jsonresponse, taskOperation);			
		}
	
		
		
		
		if(taskOperation.equals("setFieldWidget")){
			widgetsBoss.updateField(jsonresponse,taskOperation);
		}
	}
		

	
	
	
	public void findErrorPresenter(String jsonresponse, String taskOperation, String exception){
		Log.i("brewerspad","findErrrorPresenter");

		/*
		 * If we have an exception set then we need to find the place to get our execption from
		 */
				
		if(boss.equals("brewday")){
			brewdayBoss.presentError(taskOperation, exception, jsonresponse);
			brewdayBoss.APIerror=true;
		}
		
		if(boss.equals("widgets")){
			widgetsBoss.presentError(taskOperation, exception, jsonresponse);
			widgetsBoss.APIerror=true;
		}
		


	}

}
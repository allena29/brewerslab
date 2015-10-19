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
   
$Revision: 1.8 $ $Date: 2011-10-16 14:43:47 $ $Author: codemonkey $


 */

import java.util.HashMap;
import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;
import android.os.AsyncTask;
import android.util.Log;



	
	
	
	/*
	 * 
	 * Download Web Page Task. This carries out the download and needs to be
	 * parameterised to show the different typs of data we need
	 */
	public class brewdayxmlrpc extends AsyncTask<String, Void, String[]> {
	// This looks to be AsyncTask<input to >
		String response = "";
		
		Integer status = 0;
		public String hostnameIp="";
		public Integer portNum=0;
		public brewdaycommon them;
		
		@SuppressWarnings("unchecked")
		@Override
		protected String[] doInBackground(String... inputData) {
			response = "";
			String exception="";
			String taskOperation = inputData[0];
			
			Log.i("brewerspad","taskOperation into doInBackground()"+taskOperation);
			
			
			XMLRPCClient client = new XMLRPCClient("http://" + hostnameIp + ":"+portNum+"/");
			Log.i("brewerspad","URL FOR XMLRPC http://" + hostnameIp + ":"+portNum+"/");
			try {
				Log.i("brewerlab", "xmlrpc task operation" + taskOperation);

				HashMap map = new HashMap();
				// map=null;
				if (taskOperation.equals("openBrewlog")) {
					String process=inputData[1];
					String recipe=inputData[2];
					String brewlog=inputData[3];
					Log.i("brewerspad","Calling xmlrpc openBrewLog "+process+" "+recipe+" "+brewlog);
					map = (HashMap) client.call("openBrewlog", process, recipe,brewlog);
					
					
				} else if (taskOperation.equals("listActivitySteps")) {
					Log.i("brewerspad","Calling xmlrpc listActivity Steps");					
					map = (HashMap) client.call("listActivitySteps");
					
					
				} else if (taskOperation.equals("selectActivity")) {
					String activity=inputData[1];
					Log.i("brewerspad","Calling xmlrpc selectActivity "+activity);
					map = (HashMap) client.call("selectActivity", activity);					
				} else if (taskOperation.equals("getStepDetail")){
					Integer stepNumber = Integer.parseInt(inputData[1]);
					Log.i("brewerspad","Calling xmlrpc getStepDetail "+stepNumber);
					

					them.currentStep  =stepNumber; 
					map = (HashMap) client.call("getStepDetail", stepNumber);					
				} else if (taskOperation.equals("listProcessImages")){
					String process=inputData[1];					
					Log.i("brewerspad","Calling xmlrpc listProcessImages "+process);
					map = (HashMap) client.call("listProcessImages", process);
					
					
				} else if (taskOperation.equals("saveComment")){
					String stepid=inputData[1];
					String comment=inputData[2];					
					Log.i("brewerspad","Calling xmlrpc saveComment "+stepid+"/"+comment);
					map = (HashMap) client.call("saveComment", stepid,comment );
					
					
				} else if (taskOperation.equals("setStepComplete")){
					String stepid=inputData[1];
					String stepComplete=inputData[2];					
					Log.i("brewerspad","Calling xmlrpc setStepComplete "+stepid+"/"+stepComplete);
					map = (HashMap) client.call("setStepComplete", stepid,stepComplete);
					
				} else if (taskOperation.equals("setSubStepComplete")){
					String stepid=inputData[1];
					String stepComplete=inputData[2];
					String subStepComplete=inputData[3];						
					Log.i("brewerspad","Calling xmlrpc setSubStepComplete "+stepid+"/"+subStepComplete);
					map = (HashMap) client.call("setSubStepComplete", stepid,stepComplete,subStepComplete);
					
					
				} else if(taskOperation.equals("setFieldWidget")){
					String stepid=inputData[1];
					String fieldKey = inputData[2];
					String fieldVal = inputData[3];
					String formId = inputData[4];
					
					Log.i("brewerspad","Calling xmlrpc setFieldWidget "+fieldKey+"/"+fieldVal);
					map = (HashMap) client.call("setFieldWidget", stepid,fieldKey,fieldVal,formId);
					
				}
				
				


				if (map.get("status") != null) {
					status = (Integer) map.get("status");
					response = (String) map.get("json");
				}

			} catch (XMLRPCException e) {
				exception="XMLRPCException: "+e.getMessage();
							
			}

			String[] ReturnResponse = new String[3];
			ReturnResponse[0]=response;
			ReturnResponse[1]=taskOperation;
			ReturnResponse[2]=exception;

			//return response;
			return ReturnResponse;
			
		}

		/*
		 * @Override This triggers the action when it is finished
		 */
		@Override
		protected void onPostExecute(String[] result) {
			Log.i("brewerspad","onPostExecute() taskOperation "+result[1]);
			Log.i("brewerspad","onPostExecute() jsonReponse "+result[0]);
			Log.i("brewerspad","onPostExecute() exception "+result[2]);
			
			String jsonResponse = result[0];
			String taskOperation = result[1];
			String exception = result[2];
			them.decodeXmlrpc(jsonResponse,taskOperation,exception);
		}
	}

	



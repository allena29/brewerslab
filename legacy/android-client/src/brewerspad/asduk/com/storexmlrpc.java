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
   
$Revision: 1.5 $ $Date: 2011-10-16 14:43:47 $ $Author: codemonkey $


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


	public class storexmlrpc extends AsyncTask<String, Void, String[]> {
		// This looks to be AsyncTask<input to >
		String response = "";
		Integer status = 0;

		String hostnameIp="";
		Integer portNum=0;
		
		public storescommon them;
		
		@SuppressWarnings("unchecked")
		@Override
		protected String[] doInBackground(String... inputData) {
			response = "";
			String taskOperation = inputData[0];

			String exception="";
			Log.i("brewerspad","storexmlrpc.taskOperation "+taskOperation);


			XMLRPCClient client = new XMLRPCClient("http://" + hostnameIp + ":"+portNum+"/");
			Log.i("brewerspad","URL FOR XMLRPC http://" + hostnameIp + ":"+portNum+"/");
			try {
				Log.i("brewerlab", "xmlrpc task operation" + taskOperation);

				HashMap map = new HashMap();


				// add purchase
				if (taskOperation.equals("listIngredientsAndSuppliers")) {
					Log.i("brewerspad","Calling xmlrpc "+taskOperation);
					String selectedCategory=inputData[1];
					map = (HashMap) client.call(taskOperation,selectedCategory);
				}

				// store 3
				if (taskOperation.equals("changeItemQty")) {
					Log.i("brewerspad","Calling xmlrpc changeItemQty");
					String selectedCategory=inputData[1];
					String selectedName=inputData[2];
					String selectedTag=inputData[3];
					String selectedQty=inputData[4];
					map = (HashMap) client.call("changeItemQty",selectedCategory,selectedName,selectedTag,selectedQty);
				}

				
				// store 2
				if (taskOperation.equals("getStockFullDetails")) {
					Log.i("brewerspad","Calling xmlrpc getStockFullDetails");
					String category = inputData[1];
					String item = inputData[2];
					Log.i("brewerspad","________________________________________________________________");
					Log.i("brewerspad","________________________________________________________________");
					Log.i("brewerspad","________________________________________________________________");
					Log.i("brewerspad","________________________________________________________________");
					
					Log.i("brewerspad","Calling xmlrpc with argument "+category);
					Log.i("brewerspad","Calling xmlrpc with argument "+item);					
					map = (HashMap) client.call("getStockFullDetails", category,item);

				}

				
				// store 1
				if (taskOperation.equals("listStoreCategories")) {
					Log.i("brewerspad","Calling xmlrpc listStoreCategories");
					map = (HashMap) client.call("listStoreCategories");
				}
				
				
				if (taskOperation.equals("listStoreItems")){
					String StoreItemCategory = inputData[1];
					Log.i("brewerspad","Calling xmlrpc listStoreItems;" + StoreItemCategory);
					map =(HashMap) client.call("listStoreItems", StoreItemCategory);
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


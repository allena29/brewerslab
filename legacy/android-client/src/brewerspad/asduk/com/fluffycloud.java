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

 $Revision: 1.4 $ $Date: 2011-10-31 23:24:32 $ $Author: codemonkey $


 */

// This should get deprecated as we move to the cloud
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

public class fluffycloud extends AsyncTask<String, Void, String[]> {
	String response = "";
	Integer status = 0;

	String cloudUser = "";
	String cloudDevice = "";
	String cloudKey = "";
	String hostnameIp = "";
	Integer portNum = 0;

	public String themId; /*
						 * this is the id of the common module as per
						 * onPostExecute() i.e. stores or welcome
						 */

	
	public brewdaycommon themBrewday;
	public welcomecommon themWelcome;
	public storescommon themStore;
	public recipecommon themRecipe;

	@Override
	protected String[] doInBackground(String... inputData) {
		String taskOperation = inputData[0];
		String exception = "";
		String result = "";

		Log.i("brewerspad", "cloud://" + hostnameIp + ":" + portNum
				+ "/brewlab/" + taskOperation);
		/*
		 * Added timeout but need to make sure we drop any progress bars
		 */
		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 50000;
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		int timeoutSocket = 50000;
		
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		Log.i("brewerspad","build http-post: http://" + hostnameIp + ":" + portNum+ "/ngbrewlab.py?taskOperation=" + taskOperation);
		HttpClient httpclient = new DefaultHttpClient(httpParameters);
		HttpPost httppost = new HttpPost("http://" + hostnameIp + ":" + portNum
				+ "/ngbrewlab.py?taskOperation=" + taskOperation);

		/*
		 * 
		 * Set the request
		 */

		String cloudRequest = "";
		Integer requestArgs = 0;
		JSONObject jsonObject = new JSONObject();

		try {
			/*
			 * Add the arguments based on the task operations
			 */
			if (taskOperation.equals("listBrewlogsByRecipe")) {
				requestArgs = 1;
			} else if (taskOperation.equals("cloneRecipe")){
				requestArgs=2;
			} else if (taskOperation.equals("createBrewlogWrapper")){
				requestArgs=3;
			} else if (taskOperation.equals("calculateRecipe")){
				requestArgs=1;
			} else if (taskOperation.equals("calculateRecipeWrapper")){
				requestArgs=2;
			} else if (taskOperation.equals("listProcess")){
				requestArgs=0;
			} else if (taskOperation.equals("changeProcess")){
				requestArgs=3;
			} else if (taskOperation.equals("setMashEfficiency")){
				requestArgs=3;
			} else if (taskOperation.equals("setBatchSize")){
				requestArgs=3;
			} else if (taskOperation.equals("scaleIBU")){
				requestArgs=3;
			} else if (taskOperation.equals("changeItemInRecipe")){
				requestArgs=6;
			} else if (taskOperation.equals("addItemToRecipe")){
				requestArgs=6;
			} else if (taskOperation.equals("fixRecipe")){
				requestArgs=1;					
			} else if (taskOperation.equals("listActivitiesFromBrewlog")) {
				requestArgs = 3;
			} else if (taskOperation.equals("listStoreItems")) {
				requestArgs = 1;
			} else if (taskOperation.equals("getStockFullDetails")) {
				requestArgs = 2;
			} else if (taskOperation.equals("listIngredientsAndSuppliers")) {
				requestArgs = 2;
			} else if (taskOperation.equals("changeItemQty")){
				requestArgs =5;
			} else if (taskOperation.equals("addNewPurchase")) {
				requestArgs = 10;
			} else if (taskOperation.equals("viewRecipe")) {
				requestArgs = 2;
			} else if (taskOperation.equals("listProcessImages")) {
				requestArgs = 1;
			} else if (taskOperation.equals("openBrewlog")) {
				requestArgs = 3;
			} else if (taskOperation.equals("selectActivity")) {
				requestArgs = 1;
			} else if (taskOperation.equals("listActivitySteps")) {
				requestArgs = 3;
			} else if (taskOperation.equals("getStepDetail")) {
				requestArgs = 5;
			} else if (taskOperation.equals("setSubStepComplete")){
				requestArgs = 5;			
			} else if (taskOperation.equals("setStepComplete")){
				requestArgs = 4;
			} else if (taskOperation.equals("saveComment")){
				requestArgs = 5;
			} else if (taskOperation.equals("setFieldWidget")){
				requestArgs=7;
			} else if(taskOperation.equals("resetBrewlog")){
				requestArgs=2;
			} else if(taskOperation.equals("setTopupVolume")){
				requestArgs=2;
			}
			if (requestArgs > 0) {
				jsonObject.put("arg0", inputData[1]);
			}
			if (requestArgs > 1) {
				jsonObject.put("arg1", inputData[2]);
			}
			if (requestArgs > 2) {
				jsonObject.put("arg2", inputData[3]);
			}
			if (requestArgs > 3) {
				jsonObject.put("arg3", inputData[4]);
			}
			if (requestArgs > 4) {
				jsonObject.put("arg4", inputData[5]);
			}
			if (requestArgs > 5) {
				jsonObject.put("arg5", inputData[6]);
			}
			if (requestArgs > 6) {
				jsonObject.put("arg6", inputData[7]);
			}
			if (requestArgs > 7) {
				jsonObject.put("arg7", inputData[8]);
			}
			if (requestArgs > 8) {
				jsonObject.put("arg8", inputData[9]);
			}
			if (requestArgs > 9) {
				jsonObject.put("arg9", inputData[10]);
			}
			if (requestArgs > 10) {
				jsonObject.put("arg10", inputData[11]);
			}
			if (requestArgs > 11) {
				jsonObject.put("arg11", inputData[12]);
			}
			if (requestArgs > 12) {
				jsonObject.put("arg12", inputData[13]);
			}
			jsonObject.put("argNum", requestArgs);
		} catch (JSONException e) {
			Log.i("brewerspad", "exception while encoding JSON Object ");
		}

		cloudRequest = jsonObject.toString();
		Log.i("brewerspad", " cloudRequest encoded to " + cloudRequest);

		/*
		 * Calculate the API Key
		 */

		String tmp = "";
		StringBuffer cloudRequestKey = new StringBuffer();

		try {
			tmp = "cloudRequest=" + cloudRequest
					+ "_cloudSalt=Dolphin_cloudKey=" + cloudKey;
			Log.i("brewerspad", "cloudtmp=" + tmp);
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(tmp.getBytes());
			byte messageDigest[] = digest.digest();
			for (int i = 0; i < messageDigest.length; i++) {
				cloudRequestKey.append(Integer
						.toHexString(0xFF & messageDigest[i]));
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		Log.i("brewerspad", "  cloudKey = " + cloudKey);
		Log.i("brewerspad", "  cloudRequestKey = " + cloudRequestKey.toString());
		Log.i("brewerspad", "  cloudUser = " + cloudUser);
		Log.i("brewerspad", "  cloudDeviceId = " + cloudDevice);
		
		try {

			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
			nameValuePairs.add(new BasicNameValuePair("cloudKey",
					cloudRequestKey.toString()));
			nameValuePairs.add(new BasicNameValuePair("cloudDevice",
					cloudDevice));
			nameValuePairs.add(new BasicNameValuePair("cloudUser", cloudUser));
			nameValuePairs.add(new BasicNameValuePair("cloudRequest",
					cloudRequest));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			Log.i("brewerspad","set form details, clinet exeucte next");
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			Log.i("brewerspad","set client execute");
			HttpEntity entity = response.getEntity();
			Integer statusCode = response.getStatusLine().getStatusCode();
			Log.i("brewerspad", "HttpResponse" + statusCode);
			if (statusCode != 200) {
				exception = "HttpResponse" + statusCode; // forbidden
				
			} else {
				Log.i("brewerspad", "entity");
			
				if (entity != null) {
					Log.i("brewerspad", "entity is not null");
					InputStream inputStream = entity.getContent();

					Log.i("brewerspad", "inputStream" + inputStream.toString());
					result = convertStreamToString(inputStream);
					if (result.length() == 0) {
						exception = "convertStreamToString is zero length";
					}
				} else {
					Log.i("brewerspad", "entity is null");
					exception = "entity is null";
				}
			}

		} catch (ClientProtocolException e) {
			Log.i("brewerspad", "ClientProtocolException:" + e.getMessage());
			exception = "ClientProtocolException: " + e.getMessage();

		} catch (IOException e) {
			Log.i("brewerspad", "IOException experienced in fluffycloud");
		//	for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
	//		    Log.i("brewerspad","IOE: " + ste.toString() );
//			}

			Log.i("brewerspad","IOException:" +e.getStackTrace().toString());
			Log.i("brewerspad", "IOException:" + e.getMessage());
			exception = "IOException: " + e.getMessage();
		}

		String[] ReturnResponse = new String[3];
		ReturnResponse[0] = result;
		ReturnResponse[1] = taskOperation;
		ReturnResponse[2] = exception;

		// return response;
		return ReturnResponse;

	}

	/*
	 * @Override This triggers the action when it is finished
	 */
	@Override
	protected void onPostExecute(String[] result) {
		Log.i("brewerspad", "onPostExecute() taskOperation " + result[1]);
		Log.i("brewerspad", "onPostExecute() jsonReponse " + result[0]);
		Log.i("brewerspad", "onPostExecute() exception " + result[2]);
		String jsonResponse = result[0];
		String taskOperation = result[1];
		String exception = result[2];

		try {
			Log.i("brewerspad",
					"thinkk this is where the issue might be!!!!!!!!!!!");
			Log.i("brewerspad", "jsonResponse\n" + result[0]);
			Log.i("brewerspad",
					"thinkk this is where the issue might be!!!!!!!!!!!");
			JSONObject jObject = new JSONObject(result[0]);
			// Log.i("brewerspad","thinkk this is where the issue might be!!!!!!!!!!!");
			Log.i("brewerspad", "Status extracted from cloud response"
					+ jObject.getInt("status"));

			if (jObject.getInt("status") >= 1) {
				Log.i("brewerspad",
						"json from cloud response" + jObject.getString("json"));
				jsonResponse = jObject.getString("json");
			} else if (jObject.getInt("status") == -1) {
				exception = "NotAuthorised";
				Log.i("brewerspad",
						"not authorised settng exception to NotAuthorised");
			} else {
				exception = "JsonDecodedResponseStatus = "
						+ jObject.getInt("status");

			}

		} catch (JSONException e) {
			Log.i("brewerspad", "jsonexception in onPostExeucte()");
			exception = "JsonException: " + e.getMessage();
		}

		if (themId.equals("welcome")) {
			themWelcome.decodeXmlrpc(jsonResponse, taskOperation, exception);
		} else if (themId.equals("stores")) {
			themStore.decodeXmlrpc(jsonResponse, taskOperation, exception);
		} else if (themId.equals("recipe")) {
			themRecipe.decodeXmlrpc(jsonResponse, taskOperation, exception);
		} else if (themId.equals("brewday")) {
			themBrewday.decodeXmlrpc(jsonResponse, taskOperation, exception);
		} else {
			Log.i("brewerspad",
					"fluffycloud onPostExecute() no idea who they are ");
		}

	}

	private static String convertStreamToString(InputStream is) {
		Log.i("brewerspad", "convertStreamToString()");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;
		Log.i("brewerspad", "convertStreamToString() - C");
		try {
			Log.i("brewerspad", "convertStreamToString() - D");
			while ((line = reader.readLine()) != null) {
				Log.i("brewerspad", "convertStreamToString() - E");
				stringBuilder.append(line + "\n");
			}
			Log.i("brewerspad", "convertStreamToString() - G");
		} catch (IOException e) {
			Log.i("brewerspad",
					"IO Exception while converting stream to string");
			return "";

		} finally {
			try {
				is.close();
			} catch (IOException e) {
				Log.i("brewerspad",
						"IO Exception while converting stream to string");

			}
		}
		Log.i("brewerspad", "convertStreamToString() - P");
		Log.i("brewerspad", "string" + stringBuilder.toString());
		return stringBuilder.toString();
	}

}

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
   
$Revision: 1.1 $ $Date: 2011-11-03 21:51:24 $ $Author: codemonkey $


 */

import android.app.Activity;
import android.app.ProgressDialog;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;

import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

public class webviewer extends Activity {


	ProgressDialog progressbar;

	
    /** Called when the activity is first created. */
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
		Log.i("brewerspad","webview.onCreate()");
		
		SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		Log.i("brewerspad", "getting image "+shrdPrefs.getString("bigurl","about:blank"));
		//Toast.makeText(this,"getting image "+shrdPrefs.getString("bigurl","about:blank"),Toast.LENGTH_LONG).show();
		
		
		WebView mWebView = (WebView) findViewById(R.id.webView1);
        mWebView.getSettings().setBuiltInZoomControls(true);
        //mWebView.zoomOut();
//        mWebView.zoomOut();        
        
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
        	Toast.makeText(this,"SD Card not mounted, cannot load image",Toast.LENGTH_LONG).show();
        }else{
        	String path = Environment.getExternalStorageDirectory().toString();
        	
        
        	if(shrdPrefs.getString("webstyle","").equals("img")){
        		Log.i("brewerspad"," webviewer - localfile");
        			mWebView.loadDataWithBaseURL("file://"+path+"/brewerspad/", "<table border=0><tr><td width=50%><img src='"+ shrdPrefs.getString("bigurl", "about:blank") +"'></td></tr></table>", "text/html", "UTF-8", "");        		
        	}
        	if(shrdPrefs.getString("webstyle","").equals("url")){
        		Log.i("brewerspad"," webviewer - url " + shrdPrefs.getString("weburl",""));
        		mWebView.loadUrl( shrdPrefs.getString("weburl",""));
    			//mWebView.loadDataWithBaseURL("file://"+path+"/brewerspad/", "<table border=0><tr><td width=50%><img src='"+ shrdPrefs.getString("bigurl", "about:blank") +"'></td></tr></table>", "text/html", "UTF-8", "");        		
        	}
    	        	
        	
        	//mWebView.loadData(data, mimeType, encoding)
        	//mWebView.loadUrl("file://"+ shrdPrefs.getString("bigurl", "about:blank"));
        }

		 
		 

    }
    


	
	
}

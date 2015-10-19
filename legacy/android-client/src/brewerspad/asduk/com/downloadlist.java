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
   
$Revision: 1.12 $ $Date: 2011-10-19 22:20:21 $ $Author: codemonkey $


 */


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;


 
public class downloadlist extends Activity implements Runnable {

    ImageView imView;
    Bitmap bmImg;
	ProgressDialog progressbar;
	Boolean tabletDevice = false;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    	SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
		tabletDevice = shrdPrefs.getBoolean("tabletDevice", true);
        Log.i("brewerspad:downloadlist","onCreate()");


        downloadList();

        
        
    }

    
    
    public void downloadList(){
    	//Toast.makeText(this,"Loading data",Toast.LENGTH_SHORT).show();
        Log.i("brewerspad:downloadlist","downloadList()");    	
    	progressbar = new ProgressDialog(this);  	
    	    //progressbar= ProgressDialog.show(this, "", "Download Data", true);
	    progressbar.setCancelable(true);
	    progressbar.setProgressStyle(1);
	    progressbar.setProgress(0);
	    progressbar.setTitle("Downloading Data");
	    progressbar.show();
    	
    	Thread thread = new Thread(this);
    	thread.start();

    	
    	
    }
    
    public void DownloadFromUrl(String imageURL, String fileName) {  //this is the downloader method
        Log.i("brewerspad:downloadlist","downloadFromUrl("+imageURL+","+fileName+")");
//    	Toast.makeText(this,"Downloading img "+imageURL, Toast.LENGTH_SHORT).show();
        try {
        	
            	String path = Environment.getExternalStorageDirectory().toString();
                new File(path + "/brewerspad/").mkdirs();

                URL url = new URL(imageURL); //you can write here any link
                File file = new File(path + "/brewerspad/"+fileName+".png.xml");
                //File file = new File(fileName);

                long startTime = System.currentTimeMillis();
                Log.d("brewerspad", "downloaded file name:" + fileName);
                /* Open a connection to that URL. */
                URLConnection ucon = url.openConnection();

                /*
                 * Define InputStreams to read from the URLConnection.
                 */
                InputStream is = ucon.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);

                /*
                 * Read bytes to the Buffer until there is nothing more to read(-1).
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
                Log.d("brewerspad", "download ready in"
                                + ((System.currentTimeMillis() - startTime) / 1000)
                                + " sec");

        } catch (IOException e) {
                Log.d("brewerspad", "Error: " + e);
        }

    }
    

	

 	
    private Handler handler = new Handler() {
  	  
  	  
  	  
  	  @Override
        public void handleMessage(Message msg) {
  		  
			SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);			
			SharedPreferences.Editor editor = shrdPrefs.edit();
			editor = shrdPrefs.edit();
			editor.putBoolean("forceAppClose", true);
			editor.putString("appCloseReason","imagedownloads");
			editor.commit();
			
  	  		progressbar.dismiss();
	  	 	finish();
  	  	}
      };

       
		 	public void run(){
		 
					// a simple case which just downloads a fixed set of images
		 							//http://mycrap.mellon-collie.net/brewerspad.bgimgs/brewlogbg.png
		 		
		 		   String url1;		 		
		 		   String url2;
		 		   String url3;
		 		   String url4;		 		   
		 		   if(tabletDevice){
		 			   //url1="http://lh4.googleusercontent.com/-ga2UujRO9rA/TprvGhZfQSI/AAAAAAAAARE/7Hl3MavXRVk/s912/";
		 			   //url2="http://lh4.googleusercontent.com/-e7_yS2A3EwU/TprvDIh5DfI/AAAAAAAAAQs/nBPIstkO6yo/s912/";
		 			   
		 			   //url3="http://lh6.googleusercontent.com/-Mmvp8CS3U1Y/TprvFkdRQOI/AAAAAAAAAQ8/nVVLzeHf2cw/s912/";
		 			   //url4="http://lh6.googleusercontent.com/-sW-uC8IRUgk/TprvEXLa7EI/AAAAAAAAAQ0/6jywaN1DQWo/s912/";
		 			   url1="http://sixtysix.mellon-collie.net/brewerspad/";
		 			  url2="http://sixtysix.mellon-collie.net/brewerspad/";
		 			 url3="http://sixtysix.mellon-collie.net/brewerspad/";
		 			url4="http://sixtysix.mellon-collie.net/brewerspad/";
		 			   

		 		   }else{
		 			   //url1="http://lh3.googleusercontent.com/-9nzZqVw_ad0/Tpru5WD38XI/AAAAAAAAAQk/u0XjzUyb9k8/s720/";
			 		   //url2="http://lh3.googleusercontent.com/-eDTXVnKaIlo/Tpru4CXA1qI/AAAAAAAAAQM/CqtWb4-AZeg/s720/";
			 		   //url3="http://lh6.googleusercontent.com/-y2MwcQELbb0/Tpru4s5VVoI/AAAAAAAAAQU/DW_yWM9Yvco/s720/";
			 		   //url4="http://lh3.googleusercontent.com/-GslMG-hLvIo/Tpru4RX7ChI/AAAAAAAAAQY/y3nhhTd9U4E/s720/";
			 		   url1="http://mycrap.mellon-collie.net/brewerspad/phone";
			 		   url2="http://mycrap.mellon-collie.net/brewerspad/phone";
			 		   url3="http://mycrap.mellon-collie.net/brewerspad/phone";
			 		   url4="http://mycrap.mellon-collie.net/brewerspad/phone";
		 		   }

					SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);
					/*
		 		   if( shrdPrefs.getBoolean("internalDebug", false) ){		//removeFromDist
			 		   url1="http://brewerspad.mellon-collie.net/bgimgs/";		//removeFromDist
			 		   url2="http://brewerspad.mellon-collie.net/bgimgs/";		//removeFromDist
			 		   url3="http://brewerspad.mellon-collie.net/bgimgs/";		//removeFromDist
			 		   url4="http://brewerspad.mellon-collie.net/bgimgs/";		//removeFromDist		 		   
			 		   if(tabletDevice){												//removeFromDist
			 			  url1="http://brewerspad.mellon-collie.net/bgimgs/tablet/";		//removeFromDist
			 			  url2="http://brewerspad.mellon-collie.net/bgimgs/tablet/";		//removeFromDist
			 			  url3="http://brewerspad.mellon-collie.net/bgimgs/tablet/";		//removeFromDist		 			  
			 			  url4="http://brewerspad.mellon-collie.net/bgimgs/tablet/";		//removeFromDist		 			  
			 		   }								
		 		   }		//removeFromDist
		 		*/
		 		   Integer p = 0;
		 		   Integer pi=24;

		 			String filepath = Environment.getExternalStorageDirectory().toString();
		 			File file = new File(filepath + "/brewerspad/welcomebg.png.xml");
		 			if (file.exists() == false) {
		 				Log.i("brewerspad","about to download welcomebg from "+url1);
						DownloadFromUrl(url1 +"welcomebg.png", "welcomebg");
		 			}		 			
				   p = p + pi;
				   progressbar.setProgress(p);
				   
					file = new File(filepath + "/brewerspad/brewlogbg.png.xml");
					if (file.exists() == false) {
						Log.i("brewerspad","about to download brewlogbg from "+url2);
						DownloadFromUrl(url2 +"brewlogbg.png", "brewlogbg");
					}
				   p = p + pi;				   				  
				   progressbar.setProgress(p);
				   

					file = new File(filepath + "/brewerspad/recipebg.png.xml");
					if (file.exists() == false) {
						Log.i("brewerspad","about to download recipebg");
						DownloadFromUrl(url3+"recipebg.png", "recipebg");
					}
				   p = p + pi;
				   progressbar.setProgress(p);
				   
				   
					file = new File(filepath + "/brewerspad/storesbg.png.xml");
					if (file.exists() == false) {
						Log.i("brewerspad","about to download storesbg from "+url4);
						DownloadFromUrl(url4+"storesbg.png", "storesbg");
					}
				   p = p + pi;
				   progressbar.setProgress(p);
				   
				   progressbar.setProgress(100);
	               Message msg = new Message();
	               msg.arg1=1;
	               handler.sendMessageAtFrontOfQueue(msg);
		 	}
				    
}

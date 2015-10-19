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
   
$Revision: 1.3 $ $Date: 2011-10-16 14:43:47 $ $Author: codemonkey $


 */


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
	


public class debugxmlrpc  extends Activity {
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debugxmlrpc);
        
        TextView tv1 = (TextView) findViewById(R.id.CtextView1);
        TextView tv2 = (TextView) findViewById(R.id.CtextView2);
        TextView tv3 = (TextView) findViewById(R.id.CtextView3);
        
        
        SharedPreferences shrdPrefs = getSharedPreferences("activity", 0);		
		tv1.setText( shrdPrefs.getString("errorlocation","(errorlocation)"));
		tv2.setText( shrdPrefs.getString("exception","(exception)"));
		tv3.setText( shrdPrefs.getString("jsonresponse","(jsonresponse)"));
		
		
		
        Log.i("brewerslab.debugxmlrpc","onCreate()");


        
        
    }

    
}
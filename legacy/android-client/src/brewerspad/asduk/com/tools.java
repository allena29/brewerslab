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
import android.view.View;
import android.webkit.WebView;
import android.widget.ScrollView;
import android.widget.Toast;

public class tools extends Activity {


	ProgressDialog progressbar;

	
    /** Called when the activity is first created. */
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tools);
		


    }

    
    public void allInvisible(){
    	ScrollView st = (ScrollView) findViewById(R.id.scrollViewTempGrav);
    	st.setVisibility(View.GONE);
    	
    }
    
    
    
    public void onClickTempGravity(View v){
    	allInvisible();
    	ScrollView st = (ScrollView) findViewById(R.id.scrollViewTempGrav);
    	st.setVisibility(View.VISIBLE);
    	
    }


	
	
}

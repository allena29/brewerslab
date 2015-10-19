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
   
$Revision: 1.2 $ $Date: 2011-10-16 15:41:00 $ $Author: codemonkey $


 */


import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;



public class prefs extends PreferenceActivity {

	
	
	  @Override
	   protected void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      addPreferencesFromResource(R.xml.prefs);	      
			
	   }
	  
	  
	  public static String getHostname(Context context) {   
		    return PreferenceManager.getDefaultSharedPreferences(context).getString("serverUrl", "127.0.0.1");		    
	  }     
	  public static String getPort(Context context) {   
		    return PreferenceManager.getDefaultSharedPreferences(context).getString("serverPort","54659");		    
	  }     
	  
}
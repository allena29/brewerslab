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
   
$Revision: 1.6 $ $Date: 2011-11-04 22:55:46 $ $Author: codemonkey $


 */


public class brewlogstep {
    public Boolean stepNeedToComplete = true;
	public Integer stepId = 0;
    private String stepName;
    private Boolean stepComplete = false;
    private Boolean stepStarted = false;
    private String dateComplete = "";
    
    
    public String getDateCompelte(){
    	return this.dateComplete;
    }
    
    public void setDateComplete(String dateComplete){
    	this.dateComplete= dateComplete;
    }
    public void setStepStarted(){
    	this.stepStarted=true;    	
    }
    
    public Boolean getStepStarted(){
    	return this.stepStarted;
    }
    
    public String getStepName() {
        return this.stepName;
    }
    public void setStepName(String stepName) {
        this.stepName = stepName;       
    }
    public Boolean getStepComplete() {
        return this.stepComplete;
    }
    public void setStepComplete(Boolean stepComplete) {
        this.stepComplete = stepComplete;
    }
}
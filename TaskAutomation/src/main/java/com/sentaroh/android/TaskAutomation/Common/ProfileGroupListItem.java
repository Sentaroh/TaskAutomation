package com.sentaroh.android.TaskAutomation.Common;
/*
The MIT License (MIT)
Copyright (c) 2011-2013 Sentaroh

Permission is hereby granted, free of charge, to any person obtaining a copy of 
this software and associated documentation files (the "Software"), to deal 
in the Software without restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
and to permit persons to whom the Software is furnished to do so, subject to 
the following conditions:

The above copyright notice and this permission notice shall be included in all copies or 
substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE 
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.

*/

import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.SERIALIZABLE_NUMBER;

import java.io.Serializable;

public class ProfileGroupListItem 
	implements Serializable, Comparable<ProfileGroupListItem>{
	private static final long serialVersionUID = SERIALIZABLE_NUMBER;
	
	private String profile_group_name="default";
	private boolean profile_group_selected=false;
	private int no_of_task=0, no_of_action=0, no_of_time=0;
	
	private boolean isSelected=false;
	
	public ProfileGroupListItem(String grp, boolean sel, int no_task, int no_action, int no_time) {
		profile_group_name=grp;
		profile_group_selected=sel;
		no_of_task=no_task;
		no_of_action=no_action;
		no_of_time=no_time;
	}
	
	
	public boolean isProfileGroupActivated() {return profile_group_selected;}
	public void setProfileGroupActivated(boolean p) {profile_group_selected=p;}
	
	public void setSelected(boolean p) {isSelected=p;}
	public boolean isSelected() {return isSelected;}
	
	public String getProfileGroupName() {return profile_group_name;}
	public void setProfileGroupName(String grp) {profile_group_name=grp;}
	
	public int getNoOfTask() {return no_of_task;}
	public int getNoOfAction() {return no_of_action;}
	public int getNoOfTime() {return no_of_time;}
	
	@Override
	public int compareTo(ProfileGroupListItem o) {
		if(this.profile_group_name != null)
			return this.profile_group_name.compareTo(o.getProfileGroupName()) ; 
	//			return this.filename.toLowerCase().compareTo(o.getName().toLowerCase()) * (-1);
		else 
			throw new IllegalArgumentException();
	}
}

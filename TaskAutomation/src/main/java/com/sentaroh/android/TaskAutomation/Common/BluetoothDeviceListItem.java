package com.sentaroh.android.TaskAutomation.Common;

import java.io.Serializable;

public class BluetoothDeviceListItem implements Serializable {
	private static final long serialVersionUID = 1L;

	public boolean isSelected=false;
	
	public String btName="";
	public String btAddr="";
	
	public boolean isSelected() {return isSelected;}
	public void setSelected(boolean p) {isSelected=p;}			

}

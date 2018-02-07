package com.sentaroh.android.TaskAutomation.Common;

public class TrustDeviceItem {
	
	public boolean isSelected=false;
	public int trustedItemType=TYPE_WIFI_AP;
	public static final int TYPE_WIFI_AP=0;
	public static final int TYPE_BLUETOOTH_DEVICE=1;
	
	public String trustedItemName="";
	public String trustedItemAddr="";
	
	public boolean isSelected() {return isSelected;}
	public void setSelected(boolean p) {isSelected=p;}			
}

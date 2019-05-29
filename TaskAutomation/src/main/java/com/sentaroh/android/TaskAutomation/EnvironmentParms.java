package com.sentaroh.android.TaskAutomation;
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

import static com.sentaroh.android.TaskAutomation.CommonConstants.*;
import static com.sentaroh.android.TaskAutomation.CommonConstants.BATTERY_CONSUMPTION_DATA_KEY_1;
import static com.sentaroh.android.TaskAutomation.CommonConstants.BATTERY_CONSUMPTION_DATA_KEY_2;
import static com.sentaroh.android.TaskAutomation.CommonConstants.BATTERY_CONSUMPTION_DATA_KEY_3;
import static com.sentaroh.android.TaskAutomation.CommonConstants.BATTERY_CONSUMPTION_DATA_KEY_4;
import static com.sentaroh.android.TaskAutomation.CommonConstants.CURRENT_POWER_SOURCE_AC;
import static com.sentaroh.android.TaskAutomation.CommonConstants.SERIALIZABLE_NUMBER;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.content.res.Configuration;
import android.location.Location;
import android.media.AudioManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.sentaroh.android.Utilities.LocalMountPoint;

public class EnvironmentParms implements Serializable {
	private static final long serialVersionUID = SERIALIZABLE_NUMBER;
	
	public String deviceManufacturer="";
	public String deviceModel="";

	public GlobalParameters glblParms=null;

//	Task list build time
	public long taskListBuildTime=0;
	public String taskListBuildTimeString="";
//	Task statistics
	public int    statsActiveTaskCount=0;
	public String statsActiveTaskCountString="0";
	public int    statsHighTaskCountWoMaxTask=0;
	public int    statsHighTaskCountThisPeriod=0;
	public int    statsMaxTaskReacheCount=0;
	public int    statsUseOutsideThreadPoolCountTaskExec=0;
	public int    statsHighAverageTaskScheduleTime=0;
	public int    statsUseOutsideThreadPoolCountTaskCtrl=0;
//	Next schedule time    	
	public long nextScheduleTime=0;
	public String nextScheduleTimeString=null;
	
//	ScheduledTimer List
	public String[] scheduledTimerTaskList=null;
	
//	Settings.System
	public int airplane_mode_on=0;
	public boolean isAirplaneModeOn() {
		return airplane_mode_on==1?true:false;
	}
//	Network connection
//	public boolean networkIsConnected=false;    //true=Mobile or WIFI is connected
	public boolean mobileNetworkIsConnected=false; //true=Mobile is connected
//	Telephony
	public boolean telephonyIsAvailable=false;
	public int telephonyStatus=0;;
	public final int TELEPHONY_CALL_STATE_IDLE=TelephonyManager.CALL_STATE_IDLE;
	public final int TELEPHONY_CALL_STATE_OFFHOOK=TelephonyManager.CALL_STATE_OFFHOOK;
	public final int TELEPHONY_CALL_STATE_RINGING=TelephonyManager.CALL_STATE_RINGING;
	final public boolean isTelephonyCallStateIdle() {
		return telephonyStatus==TelephonyManager.CALL_STATE_IDLE?true:false;
	}
	final public boolean isTelephonyCallStateOffhook() {
		return telephonyStatus==TelephonyManager.CALL_STATE_OFFHOOK?true:false;
	}
	final public boolean isTelephonyCallStateRinging() {
		return telephonyStatus==TelephonyManager.CALL_STATE_RINGING?true:false;
	}
//	Sensor	    	
	public boolean lightSensorAvailable=false,
			proximitySensorAvailable=false,
			magneticFieldSensorAvailable=false,
			accelerometerSensorAvailable=false;
	public boolean lightSensorActive=false,
			proximitySensorActive=false,
			magneticFieldSensorActive=false,
			accelerometerSensorActive=false;
	public int    lightSensorValue=-1;
	public boolean lightSensorDetected=false;
	
	public int    proximitySensorValue=-1;
	final public boolean isProximitySensorDetected() {
		return proximitySensorValue==0 ? true:false;
	}
//		Battery	    	
	public int    batteryLevel=-1;
	public String batteryPowerSource="";
	public String batteryChargeStatusString="";
	public int batteryChargeStatusInt=0;
	public final int BATTERY_CHARGE_STATUS_INT_FULL=3;
	public final int BATTERY_CHARGE_STATUS_INT_CHARGING=1;
	public final int BATTERY_CHARGE_STATUS_INT_DISCHARGING=2;
	public boolean isBatteryCharging() {
		return batteryPowerSource.equals(CURRENT_POWER_SOURCE_AC)?true:false;
	}
//		WiFi	    	
	public String  wifiConnectedSsidName="";
	public String  wifiConnectedSsidAddr="";
	public final static String WIFI_DIRECT_SSID="*WIFI-DIRECT";
	public boolean wifiIsActive=false;
//	public boolean wifiIsAvailable=false;
	final public boolean isWifiActive() {return wifiIsActive;}
	final public void setWifiActive(boolean p) {wifiIsActive=p;}
	final public boolean isWifiConnected() {
		return wifiConnectedSsidName.equals("") ? false:true;
	}
//		Bluetooth	    	
	public String  blutoothLastEventDeviceNamex="";
	public String  blutoothLastEventDeviceAddrx="";
	public String  blutoothConnectedDeviceName="";
	public String  blutoothConnectedDeviceAddr="";
	public boolean bluetoothIsActive=false;
	public boolean bluetoothIsAvailable=false;
	private ArrayList<BluetoothDeviceListItem> bluetoothConnectedDeviceList=new ArrayList<BluetoothDeviceListItem>();
	final public void addBluetoothConnectedDevice(String name, String addr) {
		BluetoothDeviceListItem bcd=new BluetoothDeviceListItem();
		bcd.btName=name;
		bcd.btAddr=addr;
		bluetoothConnectedDeviceList.add(bcd);
	};
	final public void removeBluetoothConnectedDevice(String name, String addr) {
		ArrayList<BluetoothDeviceListItem>dl=new ArrayList<BluetoothDeviceListItem>();
		for(int i=0;i<bluetoothConnectedDeviceList.size();i++) {
			BluetoothDeviceListItem bcd=bluetoothConnectedDeviceList.get(i);
			if (bcd.btName.equals(name)) {
				if (addr.equals("")) dl.add(bcd);
				else {
					if (bcd.btAddr.equals(addr)) dl.add(bcd);
				}
			}
		}
//		Log.v("","remove name="+name+", addr="+addr);
		for(int i=0;i<dl.size();i++) {
//			Log.v("","remove item name="+dl.get(i).btName+", addr="+dl.get(i).btAddr);
			bluetoothConnectedDeviceList.remove(dl.get(i));
		}
		
	};
	final public ArrayList<BluetoothDeviceListItem> getBluetoothConnectedDeviceList() {
		return bluetoothConnectedDeviceList;
	};
	final public void setBluetoothConnectedDeviceList(ArrayList<BluetoothDeviceListItem> bdcl) {
		bluetoothConnectedDeviceList=bdcl;
	};
	final public void clearBluetoothConnectedDeviceList() {
		synchronized(bluetoothConnectedDeviceList) {
			bluetoothConnectedDeviceList=new ArrayList<BluetoothDeviceListItem>();
		}
	};
	final public int getBluetoothConnectedDeviceListCount() {return bluetoothConnectedDeviceList.size();}
	final public String getBluetoothConnectedDeviceNameAtPos(int pos) {
		if (pos<bluetoothConnectedDeviceList.size() && pos>=0) return bluetoothConnectedDeviceList.get(pos).btName;
		return null;
	}
	final public String getBluetoothConnectedDeviceAddrAtPos(int pos) {
		if (pos<bluetoothConnectedDeviceList.size() && pos>=0) return bluetoothConnectedDeviceList.get(pos).btAddr;
		return null;
	}
	final public boolean isBluetoothActive() {return bluetoothIsActive;}
	final public boolean isBluetoothConnected() {
		return bluetoothConnectedDeviceList.size()>0 ? true:false;
	}
//		Screen status	    	
	public boolean screenIsLocked=false;
	public boolean screenIsOn=false;
//	Ringer mode	    	
	public int     currentRingerMode=0;
	final public boolean isRingerModeNormal() {
		return currentRingerMode==AudioManager.RINGER_MODE_NORMAL?true:false;
	}
	final public boolean isRingerModeSilent() {
		return currentRingerMode==AudioManager.RINGER_MODE_SILENT?true:false;
	}
	final public boolean isRingerModeVibrate() {
		return currentRingerMode==AudioManager.RINGER_MODE_VIBRATE?true:false;
	}
	
//	Location
	transient public Location currentLocation=null;
	
//	Configuration
	public int currentOrientation=Configuration.ORIENTATION_PORTRAIT;
	
	public boolean isOrientationLanscape() {
		return currentOrientation==Configuration.ORIENTATION_LANDSCAPE ? true : false;
	}
	
	public String quickTaskVersion="unkown";

	public String localRootDir="";
	
	@Override
	final public EnvironmentParms clone() {  
        EnvironmentParms env=new EnvironmentParms();
		byte[] buf=serialize();
		env=deSerialize(buf);
		return env;  
    }
	
	final public void dumpEnvParms(String id) {
		
		final SimpleDateFormat sdfDateTimeSss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.getDefault());
		Log.v(APPLICATION_TAG, id+" "+"Task list build time="+sdfDateTimeSss.format(taskListBuildTime)+
				", String="+taskListBuildTimeString);
		Log.v(APPLICATION_TAG,id+" "+"Number of active task="+statsActiveTaskCount+
				" String="+statsActiveTaskCountString);
		Log.v(APPLICATION_TAG,id+" "+"High conncuret task count w/o max task reached="+statsHighTaskCountWoMaxTask);
		Log.v(APPLICATION_TAG,id+" "+"High conncuret task count this period="+statsHighTaskCountThisPeriod);
		Log.v(APPLICATION_TAG,id+" "+"Max concurrent task reach count="+statsMaxTaskReacheCount);
		Log.v(APPLICATION_TAG,id+" "+"Task started by outside thread pool="+statsUseOutsideThreadPoolCountTaskExec);
		Log.v(APPLICATION_TAG,id+" "+"Average task schedule time(ms)="+statsHighAverageTaskScheduleTime);

		Log.v(APPLICATION_TAG, id+" "+"Next schedule time="+sdfDateTimeSss.format(nextScheduleTime)+
				", String="+nextScheduleTimeString);
		Log.v(APPLICATION_TAG, id+" "+"Airplane mode on="+airplane_mode_on);
		Log.v(APPLICATION_TAG, id+" "+"Sensor available : Light="+lightSensorAvailable+
				", Accelerometer="+proximitySensorAvailable+
				", Proximity="+proximitySensorAvailable+
				", Magnetic-Field="+magneticFieldSensorAvailable);
		Log.v(APPLICATION_TAG, id+" "+"Sensor active : Light="+lightSensorActive+
				", Accelerometer="+proximitySensorActive+
				", Proximity="+proximitySensorActive+
				", Magnetic-Field="+magneticFieldSensorActive);
		Log.v(APPLICATION_TAG, id+" "+"Sensor value : Light="+lightSensorValue+
				", Proximity="+proximitySensorValue);
		Log.v(APPLICATION_TAG, id+" "+"Battery : Levelt="+batteryLevel+
				", CurrentPowerSource="+batteryPowerSource+
				", Charge status="+batteryChargeStatusString);
		Log.v(APPLICATION_TAG, id+" "+"WiFi : Active="+wifiIsActive+
				", SSID="+wifiConnectedSsidName);
		Log.v(APPLICATION_TAG, id+" "+"Bluetooth : Active="+bluetoothIsActive+
				", Device name="+blutoothConnectedDeviceName);
		Log.v(APPLICATION_TAG, id+" "+"Screen locked="+screenIsLocked+
				", Screen is On="+screenIsOn+
				", Telephony status="+telephonyStatus+
				", Ringer mode="+currentRingerMode);
	}
	
	final public void loadSettingParms(Context c) {
		deviceManufacturer=Build.MANUFACTURER;
		deviceModel=Build.MODEL;
		
		localRootDir=LocalMountPoint.getExternalStorageDir();

	}
	
	final static public EnvironmentParms deSerialize(byte[] buf) {
		EnvironmentParms env_parms=null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(buf); 
			ObjectInput in = new ObjectInputStream(bis); 
		    env_parms=(EnvironmentParms) in.readObject(); 
		    in.close(); 
		} catch (StreamCorruptedException e) {
			Log.v(APPLICATION_TAG, "EnvironmentParameters deSerialize error", e);
		} catch (IOException e) {
			Log.v(APPLICATION_TAG, "EnvironmentParameters deSerialize error", e);
		} catch (ClassNotFoundException e) {
			Log.v(APPLICATION_TAG, "EnvironmentParameters deSerialize error", e);
		}
		return env_parms;
	};
	final public byte[] serialize() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(100000); 
		byte[] buf=null; 
	    try { 
	    	ObjectOutput out = new ObjectOutputStream(bos); 
		    out.writeObject(this);
		    out.flush(); 
		    buf= bos.toByteArray(); 
	    } catch(IOException e) { 
	    	Log.v(APPLICATION_TAG, "EnvironmentParameters serialize error", e); 
		}
		return buf;
	};
}

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

import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.*;
import static com.sentaroh.android.TaskAutomation.Config.QuickTaskConstants.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.widget.ListView;

import com.sentaroh.android.TaskAutomation.Common.BluetoothDeviceListItem;
import com.sentaroh.android.TaskAutomation.Common.EnvironmentParms;
import com.sentaroh.android.TaskAutomation.Common.ProfileListItem;
import com.sentaroh.android.TaskAutomation.Common.TrustDeviceItem;
import com.sentaroh.android.TaskAutomation.Config.ProfileUtilities;
import com.sentaroh.android.TaskAutomation.Log.LogFileManagemntListItem;
import com.sentaroh.android.Utilities.LocalMountPoint;
import com.sentaroh.android.Utilities.MiscUtil;
import com.sentaroh.android.Utilities.StringUtil;
import com.sentaroh.android.Utilities.ZipUtil;

public final class CommonUtilities {
	private boolean DEBUG_ENABLE=true;

//	private SimpleDateFormat sdfTimeHHMM = new SimpleDateFormat("HH:mm");
	
	private Context mContext=null;

	private String mLogIdent="";
	
   	private EnvironmentParms envParms=null;
	
	public CommonUtilities(Context c, String li, EnvironmentParms ep) {
		mContext=c;// ContextはApplicationContext
		setLogId(li);
		envParms=ep;
        if (envParms.settingDebugLevel==0) DEBUG_ENABLE=false;
        else DEBUG_ENABLE=true;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("InlinedApi")
	final public SharedPreferences getPrefMgr() {
    	return mContext.getSharedPreferences(DEFAULT_PREFS_FILENAME,
        		Context.MODE_PRIVATE|Context.MODE_MULTI_PROCESS);
    }

	@SuppressWarnings("deprecation")
	@SuppressLint("InlinedApi")
	final static public SharedPreferences getPrefMgr(Context c) {
    	return c.getSharedPreferences(DEFAULT_PREFS_FILENAME,
        		Context.MODE_PRIVATE|Context.MODE_MULTI_PROCESS);
    }

	final public void setLogId(String li) {
		mLogIdent=(li+"                 ").substring(0,16)+" ";
	};
	
	final public void resetLogReceiver() {
		Intent intent = new Intent(BROADCAST_LOG_RESET);
		mContext.sendOrderedBroadcast(intent,null);
	};

	final public void flushLog() {
		Intent intent = new Intent(BROADCAST_LOG_FLUSH);
		mContext.sendOrderedBroadcast(intent,null);
	};

	final public void rotateLogFile() {
		Intent intent = new Intent(BROADCAST_LOG_ROTATE);
		mContext.sendOrderedBroadcast(intent,null);
	};

	final public void reBuildTaskExecList() {
		if (DEBUG_ENABLE) addDebugMsg(2, "I", "reBuildTaskExecList entered");
		Intent intent = new Intent(BROADCAST_BUILD_TASK_LIST);
		mContext.sendBroadcast(intent);
	};

	final public void startScheduler() {
		if (DEBUG_ENABLE) addDebugMsg(2, "I", "startScheduler entered");
//		Intent intent = new Intent(BROADCAST_START_SCHEDULER);
//		mContext.sendBroadcast(intent);
		Intent intent = new Intent(mContext, SchedulerService.class);
		intent.setAction(BROADCAST_START_SCHEDULER);
		mContext.startService(intent);
	};

	final public void reloadSchedulerTrustList() {
		if (DEBUG_ENABLE) addDebugMsg(2, "I", "reloadSchedulerTrustList entered");
		reloadSchedulerTrustList(mContext);
	};
	final static public void reloadSchedulerTrustList(Context c) {
		Intent intent = new Intent(c, SchedulerService.class);
		intent.setAction(BROADCAST_LOAD_TRUST_LIST);
		c.startService(intent);
	};

	final public void resetScheduler() {
		if (DEBUG_ENABLE) addDebugMsg(2, "I", "resetScheduler entered");
		resetScheduler(mContext);
	};

	final static public void resetScheduler(Context c) {
		Intent intent = new Intent(BROADCAST_RESET_SCHEDULER);
		c.sendBroadcast(intent);
	};

	final public void restartScheduler() {
		if (DEBUG_ENABLE) addDebugMsg(2, "I", "restartScheduler entered");
		restartScheduler(mContext);
	};

	final static public void restartScheduler(Context c) {
		Intent intent = new Intent(BROADCAST_RESTART_SCHEDULER);
		c.sendBroadcast(intent);
	};

    final public static boolean isNetworkConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if( ni != null ){
            return cm.getActiveNetworkInfo().isConnected();
        }
        return false;
    };
	
	final public void putSavedBluetoothConnectedDeviceList(ArrayList<BluetoothDeviceListItem> bdcl) {
		putSavedBluetoothConnectedDeviceList(mContext,bdcl);
    	if (DEBUG_ENABLE) 
    		addDebugMsg(2, "I", "Bluetooth Connected Device list was saved, count="+bdcl.size());
	};
	
	final public void clearSavedBluetoothConnectedDeviceList() {
		putSavedBluetoothConnectedDeviceList(mContext, null);
    	if (DEBUG_ENABLE) 
    		addDebugMsg(2, "I", "Bluetooth Connected Device list was cleared");
	};
	final static public void clearSavedBluetoothConnectedDeviceList(Context c) {
		putSavedBluetoothConnectedDeviceList(c,null);
	};

	final static public void putSavedBluetoothConnectedDeviceList(Context c, 
			ArrayList<BluetoothDeviceListItem> bdcl) {
		if (bdcl==null || bdcl.size()==0) getPrefMgr(c).edit().remove(PREFS_BLUETOOTH_CONNECTED_DEVICE_LIST_KEY).commit();
		else {
			String data="", sep="", line="";
			for(int i=0;i<bdcl.size();i++) {
				line=bdcl.get(i).btName+"\t"+bdcl.get(i).btAddr;
				data+=sep+line;
				sep="\n";
			}
			getPrefMgr(c).edit().putString(PREFS_BLUETOOTH_CONNECTED_DEVICE_LIST_KEY,data).commit();
//			Log.v("","saved="+data);
		}
	};

	final public ArrayList<BluetoothDeviceListItem> loadSavedBluetoothConnectedDeviceList() {
		ArrayList<BluetoothDeviceListItem> bdcl=loadSavedBluetoothConnectedDeviceListAddr(mContext);
    	if (DEBUG_ENABLE) 
    		addDebugMsg(2, "I", "Bluetooth Connected Device list was loaded, count="+bdcl.size());
		return bdcl;
	};
	final static public ArrayList<BluetoothDeviceListItem> loadSavedBluetoothConnectedDeviceListAddr(Context c) {
		ArrayList<BluetoothDeviceListItem> bdcl=new ArrayList<BluetoothDeviceListItem>();
		String raw_data=getPrefMgr(c).getString(PREFS_BLUETOOTH_CONNECTED_DEVICE_LIST_KEY,"");
//		Log.v("","load="+raw_data);
		if (!raw_data.equals("")) {
			String[] line=raw_data.split("\n");
			if (line!=null && line.length>0) {
				for(int i=0;i<line.length;i++) {
					BluetoothDeviceListItem bdli=new BluetoothDeviceListItem();
					String[]data=line[i].split("\t");
//					Log.v("","data="+data);
					if (data!=null) {
//						Log.v("","data0="+data[0]);
						if (data.length>=1) bdli.btName=data[0];
						if (data.length>=2) bdli.btAddr=data[1];
					}
					if (!bdli.btName.equals("")) bdcl.add(bdli);
				}
			}
		}
		return bdcl;
	};

//	final public void clearSavedWifiSsidName() {
//		setSavedWifiSsidName(mContext,"");
//	};
//	final public String getSavedWifiSsidName() {
//		return getPrefMgr(mContext).getString(PREFS_WIFI_CONNECTED_DEVICE_NAME_KEY, "");
//	};
//	final static public void clearSavedWifiSsidName(Context c) {
//		setSavedWifiSsidName(c,"");
//	};
//	final public void setSavedWifiSsidName(String dev) {
//    	getPrefMgr(mContext).edit().putString(PREFS_WIFI_CONNECTED_DEVICE_NAME_KEY, dev).commit();
//	};
//	final static public void setSavedWifiSsidName(Context c, String dev) {
//    	getPrefMgr(c).edit().putString(PREFS_WIFI_CONNECTED_DEVICE_NAME_KEY, dev).commit();
//	};
//
//	final public void clearSavedWifiSsidAddr() {
//		setSavedWifiSsidAddr(mContext,"");
//	};
//	final public String getSavedWifiSsidAddr() {
//		return getPrefMgr(mContext).getString(PREFS_WIFI_CONNECTED_DEVICE_ADDR_KEY, "");
//	};
//	final static public void clearSavedWifiSsidAddr(Context c) {
//		setSavedWifiSsidAddr(c,"");
//	};
//	final public void setSavedWifiSsidAddr(String dev) {
//    	getPrefMgr(mContext).edit().putString(PREFS_WIFI_CONNECTED_DEVICE_ADDR_KEY, dev).commit();
//	};
//	final static public void setSavedWifiSsidAddr(Context c, String dev) {
//    	getPrefMgr(c).edit().putString(PREFS_WIFI_CONNECTED_DEVICE_ADDR_KEY, dev).commit();
//	};

	
	final public Sensor isProximitySensorAvailable() {
    	Sensor result=null;
        SensorManager sm = (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_PROXIMITY);
        for(Sensor sensor: sensors) {
           	if (DEBUG_ENABLE) addDebugMsg(2,"I", "Proximity sensor list size="+sensors.size()+
           			", type="+sensor.getType()+", vendor="+sensor.getVendor()+
        			", ver="+sensor.getVersion());
        	if (sensor.getType()==Sensor.TYPE_PROXIMITY) {
	            result=sensor;
//	            break;
            }
        }
        if (result!=null) {
        	if (DEBUG_ENABLE) addDebugMsg(2,"I", "Proximity sensor is available, name="+result.getName()+", vendor="+result.getVendor()+", version="+result.getVersion());
        } else {
        	if (DEBUG_ENABLE) addDebugMsg(2,"I", "Proximity sensor is not available");
        }
        return result;
    };

    final public Sensor isAccelerometerSensorAvailable() {
    	Sensor result=null;
	    final SensorManager sm = (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors_list = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
        for(Sensor sensor: sensors_list) {
           	if (DEBUG_ENABLE) addDebugMsg(2,"I", "Accelerometer sensor list size="+sensors_list.size()+
           			", type="+sensor.getType()+", vendor="+sensor.getVendor()+
        			", ver="+sensor.getVersion());
        	if (sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
        		if (DEBUG_ENABLE) addDebugMsg(2, "I", "Accelerometer sensor is available, name="+sensor.getName()+", vendor="+sensor.getVendor()+", version="+sensor.getVersion());
        		result=sensor;
            }
        }
        return result;
	};

    final public Sensor isLightSensorAvailable() {
    	Sensor result=null;
	    final SensorManager sm = (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors_list = sm.getSensorList(Sensor.TYPE_LIGHT);
        for(Sensor sensor: sensors_list) {
           	if (DEBUG_ENABLE) addDebugMsg(2,"I", "Light sensor list size="+sensors_list.size()+
           			", type="+sensor.getType()+", vendor="+sensor.getVendor()+
        			", ver="+sensor.getVersion());
        	if (sensor.getType()==Sensor.TYPE_LIGHT) {
        		if (DEBUG_ENABLE) addDebugMsg(2, "I", "Light sensor is available, name="+sensor.getName()+", vendor="+sensor.getVendor()+", version="+sensor.getVersion());
        		result=sensor;
            }
        }
        return result;
	};

	final public Sensor isMagneticFieldSensorAvailable() {
    	Sensor result=null;
	    final SensorManager sm = (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensor_list = sm.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
        for(Sensor sensor: sensor_list) {
           	if (DEBUG_ENABLE) addDebugMsg(2,"I", "Magnetic-field sensor list size="+sensor_list.size()+
           			", type="+sensor.getType()+", vendor="+sensor.getVendor()+
        			", ver="+sensor.getVersion());
        	if (sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD) {
        		if (DEBUG_ENABLE) addDebugMsg(2, "I", "Magnetic-field sensor is available, name="+sensor.getName()+", vendor="+sensor.getVendor()+", version="+sensor.getVersion());
        		result=sensor;
            }
        }
        return result;
	};

	final public boolean screenLockNow() {
		boolean result=false;
        DevicePolicyManager dpm = 
        		(DevicePolicyManager)mContext.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName darcn = new ComponentName(mContext, DevAdmReceiver.class);
        if (dpm.isAdminActive(darcn)) {
        	dpm.lockNow();
        	result=true;
        } else result=false;
        return result;
	};
	
	@SuppressWarnings("deprecation")
	final public boolean isTelephonyAvailable() {
		boolean result=false;
      	ConnectivityManager cm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] ni_array=cm.getAllNetworkInfo();
        if( ni_array != null ){
         	for (int i=0;i<ni_array.length;i++) {
         		if (ni_array[i].getType()==ConnectivityManager.TYPE_MOBILE||
         				ni_array[i].getType()==ConnectivityManager.TYPE_MOBILE_DUN||
         				ni_array[i].getType()==ConnectivityManager.TYPE_MOBILE_HIPRI||
         				ni_array[i].getType()==ConnectivityManager.TYPE_MOBILE_MMS||
         				ni_array[i].getType()==ConnectivityManager.TYPE_MOBILE_SUPL) {
         			result=true;
         			break;
         		}
        	}
        }
        return result;
	};
	
	final public boolean isKeyguardEffective() {
    	boolean result=isKeyguardEffective(mContext);
    	if (DEBUG_ENABLE) addDebugMsg(2, "I", "isKeyguardEffective result="+result);
    	return result;
    };

	@SuppressLint("NewApi")
	static final public boolean isKeyguardEffective(Context c) {
        KeyguardManager keyguardMgr=(KeyguardManager)c.getSystemService(Context.KEYGUARD_SERVICE);
    	boolean result=false;
		if (Build.VERSION.SDK_INT>=20) {
			result=keyguardMgr.isKeyguardLocked();			
		} else {
			result=keyguardMgr.inKeyguardRestrictedInputMode();
		}

    	return result;
    };

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	final static public boolean isScreenOn(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
	    if (Build.VERSION.SDK_INT >= 20) {
	        return pm.isInteractive();
	    } else {
	        return pm.isScreenOn();
	    }
	}

    final public static ArrayList<TrustDeviceItem> loadTrustedDeviceList(Context c) {
    	ArrayList<TrustDeviceItem> tl=new ArrayList<TrustDeviceItem>();
    	SharedPreferences prefs=getPrefMgr(c);
    	String tl_data=prefs.getString(PREFS_TRUST_LIST_KEY, "");
    	if (tl_data!=null && !tl_data.equals("")) {
    		String[] tl_data_array=tl_data.split("\n");
    		for(int i=0;i<tl_data_array.length;i++) {
    			String[] tl_item_array=tl_data_array[i].split("\t");
    			TrustDeviceItem tl_item=new TrustDeviceItem();
    			if (tl_item_array.length>=3 && tl_item_array[0]!=null && 
    					(tl_item_array[0].equals("0") || tl_item_array[0].equals("1")) ) {
        			tl_item.trustedItemType=Integer.parseInt(tl_item_array[0]);
        			if (tl_item_array[1]!=null) tl_item.trustedItemName=tl_item_array[1];
        			if (tl_item_array[2]!=null) tl_item.trustedItemAddr=tl_item_array[2];
        			tl.add(tl_item);
    			}
    		}
    	}
    	return tl;
    };

    final public static void saveTrustedDeviceList(Context c, ArrayList<TrustDeviceItem> tl) {
    	SharedPreferences prefs=getPrefMgr(c);
    	String tl_data="";
    	String sep="";
    	for(int i=0;i<tl.size();i++) {
    		String tl_item=tl.get(i).trustedItemType+"\t"+tl.get(i).trustedItemName+"\t"+tl.get(i).trustedItemAddr+"\t";
    		tl_data+=sep+tl_item;
    		sep="\n";
    	}
    	prefs.edit().putString(PREFS_TRUST_LIST_KEY, tl_data).commit();
    };

	final public String saveProfileToFileByService(ArrayList<ProfileListItem> prof_list) {
//		AdapterProfileList pfl = new AdapterProfileList(mContext, 
//				R.layout.task_profile_list_view_item, prof_list);
		return saveProfileToFile(false, false,false,prof_list, null, "", "");
	};
	
	final public String saveProfileToFileProfileOnly(boolean sdcard,  
			ArrayList<ProfileListItem> prof_list, ListView pflv, String fd, String fn) {
		return saveProfileToFile(sdcard, false,false, prof_list, pflv, fd, fn); 
	};
	final public String saveProfileToFile(boolean sdcard, 
			boolean settings,boolean quick_task, 
			ArrayList<ProfileListItem> prof_list, 
			ListView pflv, String fd, String fn) {
		String result=null;
		String ofp="";
		PrintWriter pw;
		BufferedWriter bw=null;
		try {
			if (sdcard) {
				ofp=fd+"/"+fn;
				File lf=new File(fd);
				if (!lf.exists()) lf.mkdir();
				bw = new BufferedWriter(new FileWriter(ofp), GENERAL_FILE_BUFFER_SIZE);
				pw = new PrintWriter(bw);
			} else {
//				OutputStream out = context.openFileOutput(SMBSYNC_PROFILE_FILE_NAME,
//						Context.MODE_PRIVATE);
//				pw = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));
//				ofp=SMBSYNC_PROFILE_FILE_NAME;
				ofp=mContext.getFilesDir().toString()+"/"+PROFILE_FILE_NAME;
				File lf=new File(mContext.getFilesDir().toString());
				if (!lf.exists()) lf.mkdir();
				bw =new BufferedWriter(new FileWriter(ofp), GENERAL_FILE_BUFFER_SIZE);
				pw = new PrintWriter(bw);
			}

			if (prof_list.size() > 0) {
				String pl;
				for (int i = 0; i < prof_list.size(); i++) {
					pl=ProfileUtilities.buildProfileRecord(prof_list.get(i));
					String plx=pl.replaceAll("\t",",");
					addDebugMsg(3,"I","saveProfileToFile=" +
							plx.replaceAll("\u0001",";"));
					pw.println(pl);
				}
			}
			if (settings) saveSettingsParmsToFile(pw);
			if (quick_task) saveQuickTaskSettingToFile(pw);
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
			addLogMsg("E",String.format(
					mContext.getString(R.string.msgs_save_to_profile_error),ofp));
			addLogMsg("E",e.toString());
			result=e.toString();
		}
		
		return result;
	};
	
	final static private void saveSettingsParmsToFileString(SharedPreferences pref,
			String group, PrintWriter pw, String dflt, String key) {
		String k_type, k_val;

		k_val=pref.getString(key, dflt);
		k_type=PROFILE_SETTINGS_TYPE_STRING;
		pw.println(PROFILE_TYPE_SETTINGS+"\t"+key+"\t"+k_type+"\t"+k_val);
		
	};
	@SuppressWarnings("unused")
	final static private void saveSettingsParmsToFileInt(SharedPreferences pref,
			String group, PrintWriter pw, int dflt, String key) {
		String k_type;
		int k_val;

		k_val=pref.getInt(key, dflt);
		k_type=PROFILE_SETTINGS_TYPE_INT;
		pw.println(PROFILE_TYPE_SETTINGS+"\t"+key+"\t"+k_type+"\t"+k_val);
		
	};
	final static private void saveSettingsParmsToFileBoolean(SharedPreferences pref,
			String group, PrintWriter pw, boolean dflt, String key) {
		String k_type;
		boolean k_val;

		k_val=pref.getBoolean(key, dflt);
		k_type=PROFILE_SETTINGS_TYPE_BOOLEAN;
		pw.println(PROFILE_TYPE_SETTINGS+"\t"+key+"\t"+k_type+"\t"+k_val);
		
	};
	
	final private void saveQuickTaskSettingToFile(PrintWriter pw) {
		addDebugMsg(2, "I", "saveQuickTaskSettingToFile entered");
		String group="Default";// 12Bytes
		SharedPreferences pref=getPrefMgr();
		saveSettingsParmsToFileString(pref,group, pw, QUICK_TASK_CURRENT_VERSION, QUICK_TASK_VERSION_KEY);
		
		saveSettingsParmsToFileBoolean(pref,group, pw, false, QUICK_TASK_PROFILE_USE_BEAN_SHELL);
		
		saveSettingsParmsToFileBoolean(pref,group, pw, true, QUICK_TASK_WIFI_SCREEN_LOCKED);
		saveSettingsParmsToFileBoolean(pref,group, pw, true, QUICK_TASK_WIFI_SCREEN_LOCKED_AC);
		saveSettingsParmsToFileBoolean(pref,group, pw, true, QUICK_TASK_WIFI_WIFI_ON);
		saveSettingsParmsToFileBoolean(pref,group, pw, true, QUICK_TASK_WIFI_WIFI_ON_AC);
		saveSettingsParmsToFileBoolean(pref,group, pw, true, QUICK_TASK_WIFI_SCREEN_UNLOCKED);
		saveSettingsParmsToFileBoolean(pref,group, pw, true, QUICK_TASK_WIFI_SCREEN_UNLOCKED_AC);

		saveSettingsParmsToFileBoolean(pref,group, pw, true, QUICK_TASK_BT_SCREEN_LOCKED);
		saveSettingsParmsToFileBoolean(pref,group, pw, true, QUICK_TASK_BT_SCREEN_LOCKED_AC);
		saveSettingsParmsToFileBoolean(pref,group, pw, true, QUICK_TASK_BT_BT_ON);
		saveSettingsParmsToFileBoolean(pref,group, pw, true, QUICK_TASK_BT_BT_ON_AC);
		saveSettingsParmsToFileBoolean(pref,group, pw, true, QUICK_TASK_BT_SCREEN_UNLOCKED);
		saveSettingsParmsToFileBoolean(pref,group, pw, true, QUICK_TASK_BT_SCREEN_UNLOCKED_AC);
		
		saveSettingsParmsToFileBoolean(pref,group, pw, true, QUICK_TASK_SCREEN_PROXIMITY_UNDETECTED);
		saveSettingsParmsToFileBoolean(pref,group, pw, true, QUICK_TASK_SCREEN_PROXIMITY_DETECTED);
		saveSettingsParmsToFileBoolean(pref,group, pw, false,QUICK_TASK_SCREEN_PROXIMITY_DETECTED_IGNORE_LANDSCAPE);
		saveSettingsParmsToFileBoolean(pref,group, pw, true, QUICK_TASK_SCREEN_LIGHT_DETECTED);
		saveSettingsParmsToFileBoolean(pref,group, pw, true, QUICK_TASK_SCREEN_LIGHT_UNDETECTED);

		saveSettingsParmsToFileBoolean(pref,group, pw, false,QUICK_TASK_ALARM_CLOCK01_ENABLED);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_ALARM_CLOCK01_DATE_TYPE);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_ALARM_CLOCK01_DAY_OF_WEEK);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_ALARM_CLOCK01_DATE_DAY);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_ALARM_CLOCK01_DATE_TIME);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_ALARM_CLOCK01_SOUND_TYPE);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_ALARM_CLOCK01_SOUND_NAME);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_ALARM_CLOCK01_RINGTONE_PATH);
		saveSettingsParmsToFileBoolean(pref,group, pw, false,QUICK_TASK_ALARM_CLOCK02_ENABLED);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_ALARM_CLOCK02_DATE_TYPE);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_ALARM_CLOCK02_DAY_OF_WEEK);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_ALARM_CLOCK02_DATE_DAY);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_ALARM_CLOCK02_DATE_TIME);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_ALARM_CLOCK02_SOUND_TYPE);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_ALARM_CLOCK02_SOUND_NAME);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_ALARM_CLOCK02_RINGTONE_PATH);
		saveSettingsParmsToFileBoolean(pref,group, pw, false,QUICK_TASK_ALARM_CLOCK03_ENABLED);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_ALARM_CLOCK03_DATE_TYPE);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_ALARM_CLOCK03_DAY_OF_WEEK);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_ALARM_CLOCK03_DATE_DAY);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_ALARM_CLOCK03_DATE_TIME);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_ALARM_CLOCK03_SOUND_TYPE);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_ALARM_CLOCK03_SOUND_NAME);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_ALARM_CLOCK03_RINGTONE_PATH);
		
		saveSettingsParmsToFileBoolean(pref,group, pw, false,QUICK_TASK_TIME_ACTIVITY01_ENABLED);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_TIME_ACTIVITY01_DATE_TYPE);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_TIME_ACTIVITY01_DAY_OF_WEEK);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_TIME_ACTIVITY01_DATE_DAY);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_TIME_ACTIVITY01_DATE_TIME);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_TIME_ACTIVITY01_ACTIVITY);
		saveSettingsParmsToFileBoolean(pref,group, pw, false,QUICK_TASK_TIME_ACTIVITY02_ENABLED);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_TIME_ACTIVITY02_DATE_TYPE);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_TIME_ACTIVITY02_DAY_OF_WEEK);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_TIME_ACTIVITY02_DATE_DAY);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_TIME_ACTIVITY02_DATE_TIME);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_TIME_ACTIVITY02_ACTIVITY);
		saveSettingsParmsToFileBoolean(pref,group, pw, false,QUICK_TASK_TIME_ACTIVITY03_ENABLED);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_TIME_ACTIVITY03_DATE_TYPE);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_TIME_ACTIVITY03_DAY_OF_WEEK);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_TIME_ACTIVITY03_DATE_DAY);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_TIME_ACTIVITY03_DATE_TIME);
		saveSettingsParmsToFileString(pref,group,  pw, "",   QUICK_TASK_TIME_ACTIVITY03_ACTIVITY);
	};
	
	final private void saveSettingsParmsToFile(PrintWriter pw) {
		addDebugMsg(2, "I", "saveSettingsParmsToFile entered");
		String group="Default";// 12Bytes
		SharedPreferences pref=getPrefMgr();
		saveSettingsParmsToFileBoolean(pref,group, pw, false,mContext.getString(R.string.settings_main_enable_scheduler));
		saveSettingsParmsToFileString( pref,group, pw, "20", mContext.getString(R.string.settings_main_scheduler_max_task_count));
		saveSettingsParmsToFileString( pref,group, pw,  "5", mContext.getString(R.string.settings_main_scheduler_thread_pool_count));
		saveSettingsParmsToFileBoolean(pref,group, pw, false,mContext.getString(R.string.settings_main_scheduler_monitor));
		saveSettingsParmsToFileBoolean(pref,group, pw, false,mContext.getString(R.string.settings_main_device_admin));

		saveSettingsParmsToFileString( pref,group, pw,  "5", mContext.getString(R.string.settings_main_scheduler_sleep_wake_lock_option));
		saveSettingsParmsToFileBoolean(pref,group, pw, false,mContext.getString(R.string.settings_main_scheduler_sleep_wake_lock_light_sensor));
		saveSettingsParmsToFileBoolean(pref,group, pw, false,mContext.getString(R.string.settings_main_scheduler_sleep_wake_lock_proximity_sensor));
		
		saveSettingsParmsToFileBoolean(pref,group, pw, false,mContext.getString(R.string.settings_main_use_root_privilege));
		
		saveSettingsParmsToFileBoolean(pref,group, pw, false,mContext.getString(R.string.settings_main_log_option));
		saveSettingsParmsToFileString(pref,group,  pw, "0",  mContext.getString(R.string.settings_main_log_level));
		saveSettingsParmsToFileString(pref,group,  pw, "/",  mContext.getString(R.string.settings_main_log_dir));
		saveSettingsParmsToFileString(pref,group,  pw, "10", mContext.getString(R.string.settings_main_log_file_max_count));
		
		saveSettingsParmsToFileBoolean(pref,group, pw, false,mContext.getString(R.string.settings_main_light_sensor_use_thread));
		saveSettingsParmsToFileString(pref,group,  pw, "0",  mContext.getString(R.string.settings_main_light_sensor_monitor_interval_time));
		saveSettingsParmsToFileString(pref,group,  pw, "0",  mContext.getString(R.string.settings_main_light_sensor_monitor_active_time));
		saveSettingsParmsToFileString(pref,group,  pw, "0",  mContext.getString(R.string.settings_main_light_sensor_detect_thresh_hold));
		saveSettingsParmsToFileString(pref,group,  pw, "0",  mContext.getString(R.string.settings_main_light_sensor_ignore_time));

		saveSettingsParmsToFileBoolean(pref,group, pw, false,mContext.getString(R.string.settings_main_exit_clean));
		
		saveSettingsParmsToFileString(pref,group, pw, "", PREFS_TRUST_LIST_KEY);
	};

    final public void deleteLogFile() {
		Intent intent = new Intent(BROADCAST_LOG_DELETE);
		mContext.sendOrderedBroadcast(intent,null);
	};

	final public void addLogMsg(String cat, String... msg) {
		if (envParms.settingDebugLevel>0 || envParms.settingLogOption || cat.equals("E")) {
			addLogMsg(envParms, mContext, mLogIdent, cat, msg);
		}
	};
	final public void addDebugMsg(int lvl, String cat, String... msg) {
		if (envParms.settingDebugLevel>=lvl ) {
			addDebugMsg(envParms, mContext, mLogIdent, lvl, cat, msg);
		}
	};

	final static private void addLogMsg(EnvironmentParms envParms,
			Context context, String log_id, String cat, String... msg) {
			StringBuilder log_msg=new StringBuilder(512);
			for (int i=0;i<msg.length;i++) log_msg.append(msg[i]);
			if (envParms.settingLogOption) {
				Intent intent = new Intent(BROADCAST_LOG_SEND);
				StringBuilder print_msg=new StringBuilder("M ")
				.append(cat)
				.append(" ")
				.append(StringUtil.convDateTimeTo_YearMonthDayHourMinSecMili(System.currentTimeMillis()))
				.append(" ")
				.append(log_id)
				.append(log_msg.toString());
				intent.putExtra("LOG", print_msg.toString());
				context.sendOrderedBroadcast(intent,null);
			}
			Log.v(APPLICATION_TAG,cat+" "+log_id+log_msg.toString());
	};

	final static private void addDebugMsg(EnvironmentParms envParms,
			Context context, String log_id, int lvl, String cat, String... msg) {
			StringBuilder print_msg=new StringBuilder("D ");
			print_msg.append(cat);
			StringBuilder log_msg=new StringBuilder(512);
			for (int i=0;i<msg.length;i++) log_msg.append(msg[i]);
			if (envParms.settingLogOption) {
				Intent intent = new Intent(BROADCAST_LOG_SEND);
				print_msg.append(" ")
				.append(StringUtil.convDateTimeTo_YearMonthDayHourMinSecMili(System.currentTimeMillis()))
				.append(" ")
				.append(log_id)
				.append(log_msg.toString());
				intent.putExtra("LOG", print_msg.toString());
				context.sendOrderedBroadcast(intent,null);
			}
			Log.v(APPLICATION_TAG,cat+" "+log_id+log_msg.toString());
	};

	final public boolean isLogFileExists() {
		boolean result = false;
		File lf = new File(getLogFilePath());
		result=lf.exists();
		if (envParms.settingDebugLevel>=3) addDebugMsg(3,"I","Log file exists="+result);
		return result;
	};

	final public boolean getSettingsLogOption() {
		boolean result = false;
		result=getPrefMgr().getBoolean(mContext.getString(R.string.settings_main_log_option), false);
		if (envParms.settingDebugLevel>=3) addDebugMsg(3,"I","LogOption="+result);
		return result;
	};

	final public boolean setSettingsLogOption(boolean enabled) {
		boolean result = false;
		getPrefMgr().edit().putBoolean(mContext.getString(R.string.settings_main_log_option), enabled).commit();
		if (envParms.settingDebugLevel>=3) addDebugMsg(3,"I","setLLogOption="+result);
		return result;
	};

	final public String getLogFilePath() {
		return envParms.settingLogMsgDir+envParms.settingLogMsgFilename;
	};
	
	final public void sendLogFileToDeveloper(String log_file_path) {
		resetLogReceiver();
		
		String zip_file_name=envParms.settingLogMsgDir+"log.zip";
		
		File lf=new File(zip_file_name);
		lf.delete();
		
//		createZipFile(zip_file_name,log_file_path);
		String[] lmp=LocalMountPoint.convertFilePathToMountpointFormat(mContext, log_file_path);
		ZipUtil.createZipFile(mContext, null, null, zip_file_name, lmp[0], log_file_path);
		
	    Intent intent=new Intent();
	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    intent.setAction(Intent.ACTION_SEND);  
//	    intent.setType("message/rfc822");  
//	    intent.setType("text/plain");
	    intent.setType("application/zip");
	      
	    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"gm.developer.fhoshino@gmail.com"});  
//		    intent.putExtra(Intent.EXTRA_CC, new String[]{"cc@example.com"});  
//		    intent.putExtra(Intent.EXTRA_BCC, new String[]{"bcc@example.com"});  
	    intent.putExtra(Intent.EXTRA_SUBJECT, "TaskAutomation log file");  
	    intent.putExtra(Intent.EXTRA_TEXT, "Any comment");
	    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(lf)); 
	    mContext.startActivity(intent);
	};
	
    final static public ArrayList<LogFileManagemntListItem> createLogFileList(EnvironmentParms envParms) {
    	ArrayList<LogFileManagemntListItem> lfm_fl=new ArrayList<LogFileManagemntListItem>();
    	
    	File lf=new File(envParms.settingLogMsgDir);
    	File[] file_list=lf.listFiles();
    	if (file_list!=null) {
    		for (int i=0;i<file_list.length;i++) {
    			if (file_list[i].getName().startsWith("TaskAutomation_log")) {
    				if (file_list[i].getName().startsWith("TaskAutomation_log_20")) {
        		    	LogFileManagemntListItem t=new LogFileManagemntListItem();
        		    	t.log_file_name=file_list[i].getName();
        		    	t.log_file_path=file_list[i].getPath();
        		    	t.log_file_size=MiscUtil.convertFileSize(file_list[i].length());
        		    	t.log_file_last_modified=file_list[i].lastModified();
        		    	String lm_date=StringUtil.convDateTimeTo_YearMonthDayHourMinSec(file_list[i].lastModified());
        		    	if (file_list[i].getPath().equals(envParms.settingLogMsgDir+envParms.settingLogMsgFilename))
        		    		t.isCurrentLogFile=true;
        		    	t.log_file_last_modified_date=lm_date.substring(0,10);
        		    	t.log_file_last_modified_time=lm_date.substring(11);
        		    	lfm_fl.add(t);
    				} else if (file_list[i].getName().equals("TaskAutomation_log.txt")){
        		    	LogFileManagemntListItem t=new LogFileManagemntListItem();
        		    	t.log_file_name=file_list[i].getName();
        		    	t.log_file_path=file_list[i].getPath();
        		    	t.log_file_size=MiscUtil.convertFileSize(file_list[i].length());
        		    	t.log_file_last_modified=file_list[i].lastModified();
        		    	if (file_list[i].getPath().equals(envParms.settingLogMsgDir+envParms.settingLogMsgFilename))
        		    		t.isCurrentLogFile=true;
        		    	String lm_date=StringUtil.convDateTimeTo_YearMonthDayHourMinSec(file_list[i].lastModified());
        		    	t.log_file_last_modified_date=lm_date.substring(0,10);
        		    	t.log_file_last_modified_time=lm_date.substring(11);
        		    	lfm_fl.add(t);
    				}
    			}
    		}
    		Collections.sort(lfm_fl,new Comparator<LogFileManagemntListItem>(){
				@Override
				public int compare(LogFileManagemntListItem arg0,
						LogFileManagemntListItem arg1) {
					int result=0;
					long comp=arg1.log_file_last_modified-arg0.log_file_last_modified;
					if (comp==0) result=0;
					else if(comp<0) result=-1;
					else if(comp>0) result=1;
					return result;
				}
    			
    		});
    	}
    	if (lfm_fl.size()==0) {
    		LogFileManagemntListItem t=new LogFileManagemntListItem();
    		lfm_fl.add(t);
    	}
    	return lfm_fl;
    };

}

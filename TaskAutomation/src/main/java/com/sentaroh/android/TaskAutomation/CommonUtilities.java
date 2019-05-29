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
import static com.sentaroh.android.TaskAutomation.Log.LogConstants.BROADCAST_LOG_FLUSH;
import static com.sentaroh.android.TaskAutomation.Log.LogConstants.BROADCAST_LOG_RESET;
import static com.sentaroh.android.TaskAutomation.Log.LogConstants.BROADCAST_LOG_ROTATE;
import static com.sentaroh.android.TaskAutomation.Log.LogConstants.BROADCAST_LOG_SEND;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.PowerManager;
import android.widget.ListView;

import com.sentaroh.android.TaskAutomation.Log.LogService;
import com.sentaroh.android.TaskAutomation.Log.LogUtil;

public final class CommonUtilities {
//	private SimpleDateFormat sdfTimeHHMM = new SimpleDateFormat("HH:mm");
	
	private Context mContext=null;

   	private EnvironmentParms envParms=null;

   	private GlobalParameters mGp=null;

   	private String mLogId="";

	public CommonUtilities(Context c, String li, EnvironmentParms ep, GlobalParameters gp) {
		mContext=c;// ContextはApplicationContext
        mGp=gp;
        mLogId=(li+"                 ").substring(0,16)+" ";
		envParms=ep;
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

	static final public void resetLogReceiver(Context c) {
//        Intent in=new Intent(c, LogReceiver.class);
//        in.setAction(BROADCAST_LOG_RESET);
//        c.sendBroadcast(in);
        Intent in=new Intent(c, LogService.class);
        in.setAction(BROADCAST_LOG_RESET);
        c.startService(in);
    };

	static final public void flushLog(Context c, GlobalParameters mGp) {
//        Intent in=new Intent(c, LogReceiver.class);
//        in.setAction(BROADCAST_LOG_FLUSH);
//        c.sendBroadcast(in);
        Intent in=new Intent(c, LogService.class);
        in.setAction(BROADCAST_LOG_FLUSH);
        c.startService(in);

    };

	static final public void rotateLogFile(Context c) {
//        Intent in=new Intent(c, LogReceiver.class);
//        in.setAction(BROADCAST_LOG_ROTATE);
//        c.sendBroadcast(in);
        Intent in=new Intent(c, LogService.class);
        in.setAction(BROADCAST_LOG_ROTATE);
        c.startService(in);

    };

	final public void reBuildTaskExecList() {
        if (mGp.settingDebugLevel==2)  addDebugMsg(2, "I", "reBuildTaskExecList entered");
        Intent intent = new Intent(mContext, SchedulerService.class);
        intent.setAction(BROADCAST_BUILD_TASK_LIST);
        mContext.startService(intent);
	};

	final public void startScheduler() {
        if (mGp.settingDebugLevel==2) addDebugMsg(2, "I", "startScheduler entered");
//		Intent intent = new Intent(BROADCAST_START_SCHEDULER);
//		mContext.sendBroadcast(intent);
		Intent intent = new Intent(mContext, SchedulerService.class);
		intent.setAction(BROADCAST_START_SCHEDULER);
		mContext.startService(intent);
	};

	final public void resetScheduler() {
        if (mGp.settingDebugLevel==2) addDebugMsg(2, "I", "resetScheduler entered");
		resetScheduler(mContext);
	};

	final static public void resetScheduler(Context c) {
        Intent intent = new Intent(c, SchedulerService.class);
        intent.setAction(BROADCAST_RESET_SCHEDULER);
        c.startService(intent);
	};

	final public void restartScheduler() {
        if (mGp.settingDebugLevel==2)addDebugMsg(2, "I", "restartScheduler entered");
		restartScheduler(mContext);
	};

	final static public void restartScheduler(Context c) {
        Intent intent = new Intent(c, SchedulerService.class);
		intent.setAction(BROADCAST_RESTART_SCHEDULER);
		c.startService(intent);
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
        if (mGp.settingDebugLevel==2)
    		addDebugMsg(2, "I", "Bluetooth Connected Device list was saved, count="+bdcl.size());
	};
	
	final public void clearSavedBluetoothConnectedDeviceList() {
		putSavedBluetoothConnectedDeviceList(mContext, null);
        if (mGp.settingDebugLevel==2)
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
        if (mGp.settingDebugLevel==2)
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
            if (mGp.settingDebugLevel==2) addDebugMsg(2,"I", "Proximity sensor list size="+sensors.size()+
           			", type="+sensor.getType()+", vendor="+sensor.getVendor()+
        			", ver="+sensor.getVersion());
        	if (sensor.getType()==Sensor.TYPE_PROXIMITY) {
	            result=sensor;
//	            break;
            }
        }
        if (result!=null) {
            if (mGp.settingDebugLevel==2)addDebugMsg(2,"I", "Proximity sensor is available, name="+result.getName()+", vendor="+result.getVendor()+", version="+result.getVersion());
        } else {
            if (mGp.settingDebugLevel==2)addDebugMsg(2,"I", "Proximity sensor is not available");
        }
        return result;
    };

    final public Sensor isAccelerometerSensorAvailable() {
    	Sensor result=null;
	    final SensorManager sm = (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors_list = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
        for(Sensor sensor: sensors_list) {
            if (mGp.settingDebugLevel==2) addDebugMsg(2,"I", "Accelerometer sensor list size="+sensors_list.size()+
           			", type="+sensor.getType()+", vendor="+sensor.getVendor()+
        			", ver="+sensor.getVersion());
        	if (sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
                if (mGp.settingDebugLevel==2) addDebugMsg(2, "I", "Accelerometer sensor is available, name="+sensor.getName()+", vendor="+sensor.getVendor()+", version="+sensor.getVersion());
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
            if (mGp.settingDebugLevel==2) addDebugMsg(2,"I", "Light sensor list size="+sensors_list.size()+
           			", type="+sensor.getType()+", vendor="+sensor.getVendor()+
        			", ver="+sensor.getVersion());
        	if (sensor.getType()==Sensor.TYPE_LIGHT) {
                if (mGp.settingDebugLevel==2) addDebugMsg(2, "I", "Light sensor is available, name="+sensor.getName()+", vendor="+sensor.getVendor()+", version="+sensor.getVersion());
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
            if (mGp.settingDebugLevel==2) addDebugMsg(2,"I", "Magnetic-field sensor list size="+sensor_list.size()+
           			", type="+sensor.getType()+", vendor="+sensor.getVendor()+
        			", ver="+sensor.getVersion());
        	if (sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD) {
                if (mGp.settingDebugLevel==2) addDebugMsg(2, "I", "Magnetic-field sensor is available, name="+sensor.getName()+", vendor="+sensor.getVendor()+", version="+sensor.getVersion());
        		result=sensor;
            }
        }
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
        if (mGp.settingDebugLevel==2) addDebugMsg(2, "I", "isKeyguardEffective result="+result);
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

	final public String saveProfileToFileByService(ArrayList<ProfileListItem> prof_list) {
//		AdapterProfileList pfl = new AdapterProfileList(mContext, 
//				R.layout.task_profile_list_view_item, prof_list);
		return saveProfileToFile(false, false, prof_list, null, "", "");
	};
	
	final public String saveProfileToFileProfileOnly(boolean sdcard,  
			ArrayList<ProfileListItem> prof_list, ListView pflv, String fd, String fn) {
		return saveProfileToFile(sdcard, false, prof_list, pflv, fd, fn);
	};
	final public String saveProfileToFile(boolean sdcard, 
			boolean settings, ArrayList<ProfileListItem> prof_list,
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
	};

    final public void deleteLogFile() {
        LogUtil.deleteLogFile(mContext, mGp);
	};

	final public void addLogMsg(String cat, String... msg) {
		if (mGp.settingDebugLevel>0 || mGp.settingLogOption || cat.equals("E")) {
		    Intent in=new Intent(mContext, LogService.class);
		    in.setAction(BROADCAST_LOG_SEND);
		    in.putExtra("TYPE","M");
		    in.putExtra("ID", mLogId);
		    in.putExtra("CAT",cat);
//            Log.v("TaskAutomation","length="+msg.length+", "+log_msg);
            in.putExtra("MSG",msg);
            mContext.startService(in);
 		}
	};
	final public void addDebugMsg(int lvl, String cat, String... msg) {
		if (mGp.settingDebugLevel>=lvl ) {
            Intent in=new Intent(mContext, LogService.class);
            in.setAction(BROADCAST_LOG_SEND);
            in.putExtra("TYPE","D");
            in.putExtra("ID", mLogId);
            in.putExtra("CAT",cat);
//            Log.v("TaskAutomation","length="+msg.length+", "+log_msg);
            in.putExtra("MSG",msg);
            mContext.startService(in);
 		}
	};

	final public boolean isLogFileExists() {
	    boolean result = LogUtil.isLogFileExists(mGp);
		if (mGp.settingDebugLevel>=3) addDebugMsg(3,"I","Log file exists="+result);
		return result;
	};

	final public boolean getSettingsLogOption() {
		boolean result = false;
		result=getPrefMgr().getBoolean(mContext.getString(R.string.settings_main_log_option), false);
		if (mGp.settingDebugLevel>=3) addDebugMsg(3,"I","LogOption="+result);
		return result;
	};

	final public boolean setSettingsLogOption(boolean enabled) {
		boolean result = false;
		getPrefMgr().edit().putBoolean(mContext.getString(R.string.settings_main_log_option), enabled).commit();
		if (mGp.settingDebugLevel>=3) addDebugMsg(3,"I","setLLogOption="+result);
		return result;
	};

	final public String getLogFilePath() {
	    return LogUtil.getLogFilePath(mGp);
	};
	

}

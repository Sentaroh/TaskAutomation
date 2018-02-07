package com.sentaroh.android.TaskAutomationInterface;

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

import static com.sentaroh.android.TaskAutomationInterface.TaCommonConstants.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

final public class TaApplicationInterface {
	final public static String[] REQUEST_RESULT_REASON_DESC=TaServiceInterface.REQUEST_RESULT_REASON_DESC;
	final public static int REQUEST_RESULT_REASON_SUCCESS=0;
	final public static int REQUEST_RESULT_REASON_SCHEDULER_DISABLED=1;
	final public static int REQUEST_RESULT_REASON_UNKNOWN_COMMAND=2;
	final public static int REQUEST_RESULT_REASON_UNKNOWN_COMMAND_SUB_TYPE=3;
	final public static int REQUEST_RESULT_REASON_IVALID_REPLY_ACTION=4;
	final static public int REQUEST_RESULT_REASON_TASK_ACTIVE=5;
	final static public int REQUEST_RESULT_REASON_TASK_ALREADY_ACTIVE=6;
	final static public int REQUEST_RESULT_REASON_TASK_NOT_ACTIVE=7;
	final static public int REQUEST_RESULT_REASON_TASK_INACTIVE=8;
	final static public int REQUEST_RESULT_REASON_TASK_ENABLED=9;
	final static public int REQUEST_RESULT_REASON_TASK_DISABLED=10;
	final static public int REQUEST_RESULT_REASON_TASK_NOT_FOUND=11;
	final static public int REQUEST_RESULT_REASON_GROUP_ACTIVE=12;
	final static public int REQUEST_RESULT_REASON_GROUP_INACTIVE=13;
	final static public int REQUEST_RESULT_REASON_GROUP_NOT_FOUND=14;
	
	final public static String 	REQUEST_TYPE_REQUEST=TaServiceInterface.REQUEST_TYPE_REQUEST;
	final public static String 	REQUEST_TYPE_REPLY=TaServiceInterface.REQUEST_TYPE_REPLY;
	
	final public static String 	REQUEST_SYS_INFO=TaServiceInterface.REQUEST_SYS_INFO;
	final public static String 	REQUEST_SYS_INFO_GET=TaServiceInterface.REQUEST_SYS_INFO_GET;
	
	final public static String 	REQUEST_TASK=TaServiceInterface.REQUEST_TASK;
	final public static String 	REQUEST_TASK_LIST=TaServiceInterface.REQUEST_TASK_LIST;
	final public static String 	REQUEST_TASK_START=TaServiceInterface.REQUEST_TASK_START;
	final public static String 	REQUEST_TASK_CANCEL=TaServiceInterface.REQUEST_TASK_CANCEL;
	final public static String 	REQUEST_TASK_STATUS=TaServiceInterface.REQUEST_TASK_STATUS;

	final public static String 	REQUEST_GROUP=TaServiceInterface.REQUEST_GROUP;
	final public static String 	REQUEST_GROUP_ACTIVATED=TaServiceInterface.REQUEST_GROUP_ACTIVATED;
	final public static String 	REQUEST_GROUP_DEACTIVATED=TaServiceInterface.REQUEST_GROUP_DEACTIVATED;
	final public static String 	REQUEST_GROUP_STATUS=TaServiceInterface.REQUEST_GROUP_STATUS;
	final public static String 	REQUEST_GROUP_LIST=TaServiceInterface.REQUEST_GROUP_LIST;

	private TaInterfaceParms mTaInterfaceParms=null;
	private Context mContext=null;
	
	private TaReplyListener mReplyListener=null;
	
    private BroadcastReceiver mReplyReceiver=new BroadcastReceiver() {
		@Override
		final public void onReceive(Context c, Intent intent) {
			if (mReplyListener!=null) {
				if (intent.getAction().startsWith(TaServiceInterface.BROADCAST_REPLY)) {
					mTaInterfaceParms=readReply(mTaInterfaceParms,intent);
					TaReplyContents tar=new TaReplyContents(mTaInterfaceParms);
					mReplyListener.onReplyReceived(mTaInterfaceParms.reply_result_success,
							mTaInterfaceParms.reply_result_status_code, tar);
				} else if (intent.getAction().equals(TaServiceInterface.BROADCAST_NOTIFICATION)) {
					
				}
			} else {
				Log.e("TaskAutomationInterface","Reply ignored, because replyListener was null.");
			}
		}
    };

 	public TaApplicationInterface(Context c) throws Exception{
		mContext=c.getApplicationContext();
		mTaInterfaceParms=new TaInterfaceParms();
		mTaInterfaceParms.reply_action=TaServiceInterface.BROADCAST_REPLY+"."+android.os.Process.myPid();
        IntentFilter i_flt = new IntentFilter();
        i_flt.addAction(mTaInterfaceParms.reply_action);
        i_flt.addAction(TaServiceInterface.BROADCAST_NOTIFICATION);
	    mContext.registerReceiver(mReplyReceiver, i_flt);
	    
	    PackageManager pm=(PackageManager)mContext.getPackageManager();
	    mTaInterfaceParms.requestor_pkg=mContext.getPackageName();
	    try {
			mTaInterfaceParms.requestor_name=pm.getApplicationLabel(
					pm.getApplicationInfo(mTaInterfaceParms.requestor_pkg, 0)).toString();
		} catch (NameNotFoundException e) {
			Log.e("TaskAutomationInterface","Initialyzation failed, Can not get android name.");
			e.printStackTrace();
		}
	    
	    ActivityManager activityManager = ((ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE));
	    List<ActivityManager.RunningServiceInfo> taskInfo = activityManager.getRunningServices(100);
	    boolean found=false;
	    if(taskInfo != null){
	        for (RunningServiceInfo task : taskInfo){
	        	if (task.process.startsWith(TASK_AUTO_PKG)) {
	        		found=true;
	        		break;
	        	}
	        }
	    }
	    if (!found) {
	    	throw new Exception("TaskAutomationInterface Initialyzation failed, TaskAutomation was not installed or active.");
	    }
	}

	public void destroyInterface() {
	    mContext.unregisterReceiver(mReplyReceiver);
	}

	final public void setListener(TaReplyListener reply_listener) {
		mReplyListener=reply_listener;
	}
	final public void unsetListener() {
		mReplyListener=null;
	}
	
	final public void getSysInfo() {
		mTaInterfaceParms.request_method_name=getExecutedMethodName();
		Intent intent=new Intent();
		mTaInterfaceParms.request_cmd=REQUEST_SYS_INFO;
		mTaInterfaceParms.request_cmd_sub_type=REQUEST_SYS_INFO_GET;
		requestTaskAutomation(mContext,mTaInterfaceParms,intent);
	}
	final public void taskStatus(String groupname, String taskname) {
		mTaInterfaceParms.request_method_name=getExecutedMethodName();
		Intent intent=new Intent();
		mTaInterfaceParms.request_cmd=REQUEST_TASK;
		mTaInterfaceParms.request_cmd_sub_type=REQUEST_TASK_STATUS;
		intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_REQUEST_GROUP_NAME, groupname);
		intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_REQUEST_TASK_NAME, taskname);
		requestTaskAutomation(mContext,mTaInterfaceParms,intent);
	}
	final public void taskList(String groupname) {
		mTaInterfaceParms.request_method_name=getExecutedMethodName();
		Intent intent=new Intent();
		mTaInterfaceParms.request_cmd=REQUEST_TASK;
		mTaInterfaceParms.request_cmd_sub_type=REQUEST_TASK_LIST;
		intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_REQUEST_GROUP_NAME, groupname);
		requestTaskAutomation(mContext,mTaInterfaceParms,intent);
	}
	final public void taskStart(String groupname, String taskname) {
		mTaInterfaceParms.request_method_name=getExecutedMethodName();
		Intent intent=new Intent();
		mTaInterfaceParms.request_cmd=REQUEST_TASK;
		mTaInterfaceParms.request_cmd_sub_type=REQUEST_TASK_START;
		intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_REQUEST_GROUP_NAME, groupname);
		intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_REQUEST_TASK_NAME, taskname);
		requestTaskAutomation(mContext,mTaInterfaceParms,intent);
	}
	final public void taskCancel(String groupname, String taskname) {
		mTaInterfaceParms.request_method_name=getExecutedMethodName();
		Intent intent=new Intent();
		mTaInterfaceParms.request_cmd=REQUEST_TASK;
		mTaInterfaceParms.request_cmd_sub_type=REQUEST_TASK_CANCEL;
		intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_REQUEST_GROUP_NAME, groupname);
		intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_REQUEST_TASK_NAME, taskname);
		requestTaskAutomation(mContext,mTaInterfaceParms,intent);
	}
	final public void groupStatus(String groupname) {
		mTaInterfaceParms.request_method_name=getExecutedMethodName();
		Intent intent=new Intent();
		mTaInterfaceParms.request_cmd=REQUEST_GROUP;
		mTaInterfaceParms.request_cmd_sub_type=REQUEST_GROUP_STATUS;
		intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_REQUEST_GROUP_NAME, groupname);
		requestTaskAutomation(mContext,mTaInterfaceParms,intent);
	}
	final public void groupList() {
		mTaInterfaceParms.request_method_name=getExecutedMethodName();
		Intent intent=new Intent();
		mTaInterfaceParms.request_cmd=REQUEST_GROUP;
		mTaInterfaceParms.request_cmd_sub_type=REQUEST_GROUP_LIST;
		requestTaskAutomation(mContext,mTaInterfaceParms,intent);
	}
	
	final static private void requestTaskAutomation(Context context, TaInterfaceParms taip, Intent intent) {
		intent.setAction(TaServiceInterface.BROADCAST_REQUEST);
		intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_REQUESTOR_INFO_NAME,taip.requestor_name);
		intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_REQUESTOR_INFO_PKG,taip.requestor_pkg);
		intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_REPLY_ACTION,taip.reply_action);
		intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_REQUEST_CMD,taip.request_cmd);
		intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_REQUEST_CMD_SUBTYPE,taip.request_cmd_sub_type);
		context.sendBroadcast(intent);
	}
	
	final static private TaInterfaceParms readReply(TaInterfaceParms taip,Intent intent) {
		TaInterfaceParms n_taip=reInitTaskAutomationInterfaceParms(taip);
		n_taip.reply_result_success=intent.getBooleanExtra(TASK_AUTO_INTF_EXTRA_KEY_REPLY_RESULT_SUCCESS,false);
		n_taip.reply_result_status_code=intent.getIntExtra(TASK_AUTO_INTF_EXTRA_KEY_REPLY_RESULT_STATUS_CODE,0);
		n_taip.availavility_sys_info=intent.getBooleanExtra(TASK_AUTO_INTF_EXTRA_KEY_DATA_AVAILABE_SYS_INFO,false);
		n_taip.availavility_task_list=intent.getBooleanExtra(TASK_AUTO_INTF_EXTRA_KEY_DATA_AVAILABE_TASK_LIST,false);
		n_taip.availavility_group_list=intent.getBooleanExtra(TASK_AUTO_INTF_EXTRA_KEY_DATA_AVAILABE_GROUP_LIST,false);
		if (n_taip.reply_result_success) {
			if (n_taip.availavility_sys_info) readReplyStatus(n_taip, intent);
			if (n_taip.availavility_task_list) readReplyTaskList(n_taip, intent);
			if (n_taip.availavility_group_list) readReplyGroupList(n_taip, intent);
		}
		return n_taip;
	}
	final static private void readReplyTaskList(TaInterfaceParms taip,Intent intent) {
		byte[] buff=intent.getByteArrayExtra(TASK_AUTO_INTF_EXTRA_KEY_REPLY_TASK_LIST);
		if (buff!=null) {
			ByteArrayInputStream bis=new ByteArrayInputStream(buff);
			try {
				ObjectInput oi=new ObjectInputStream(bis);
				taip.reply_task_list=(String[][])oi.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	final static private void readReplyGroupList(TaInterfaceParms taip,Intent intent) {
		byte[] buff=intent.getByteArrayExtra(TASK_AUTO_INTF_EXTRA_KEY_REPLY_GROUP_LIST);
		if (buff!=null) {
			ByteArrayInputStream bis=new ByteArrayInputStream(buff);
			try {
				ObjectInput oi=new ObjectInputStream(bis);
				taip.reply_group_list=(String[][])oi.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	final static private void readReplyStatus(TaInterfaceParms taip, Intent intent) {
		taip.availavility_sys_info=intent.getBooleanExtra(TASK_AUTO_INTF_EXTRA_KEY_DATA_AVAILABE_SYS_INFO, false);
		if (taip.availavility_sys_info) {
			taip.airplane_mode_on=intent.getBooleanExtra(TASK_AUTO_INTF_EXTRA_KEY_AIRPLANE_MODE_ON,false);
			taip.battery_charging=intent.getBooleanExtra(TASK_AUTO_INTF_EXTRA_KEY_BATTERY_CHARGING,false);
			taip.battery_level=intent.getIntExtra(TASK_AUTO_INTF_EXTRA_KEY_BATTERY_LEVEL,0);
			taip.bluetooth_active=intent.getBooleanExtra(TASK_AUTO_INTF_EXTRA_KEY_BLUETOOTH_ACTIVE, false);
			taip.bluetooth_available=intent.getBooleanExtra(TASK_AUTO_INTF_EXTRA_KEY_BLUETOOTH_AVAILABLE, false);
			taip.bluetooth_device_name=intent.getStringExtra(TASK_AUTO_INTF_EXTRA_KEY_BLUETOOTH_DEVICE_NAME);
			taip.bluetooth_device_addr=intent.getStringExtra(TASK_AUTO_INTF_EXTRA_KEY_BLUETOOTH_DEVICE_ADDR);
			taip.ringer_mode_normal=intent.getBooleanExtra(TASK_AUTO_INTF_EXTRA_KEY_RINGER_MODE_NORMAL, false);
			taip.ringer_mode_silent=intent.getBooleanExtra(TASK_AUTO_INTF_EXTRA_KEY_RINGER_MODE_SILENT, false);
			taip.ringer_mode_vibrate=intent.getBooleanExtra(TASK_AUTO_INTF_EXTRA_KEY_RINGER_MODE_VIBRATE, false);
			taip.light_sensor_active=intent.getBooleanExtra(TASK_AUTO_INTF_EXTRA_KEY_LIGHT_SENSOR_ACTIVE, false);
			taip.light_sensor_available=intent.getBooleanExtra(TASK_AUTO_INTF_EXTRA_KEY_LIGHT_SENSOR_AVAILABLE, false);
			taip.light_sensor_value=intent.getIntExtra(TASK_AUTO_INTF_EXTRA_KEY_LIGHT_SENSOR_VALUE,0);
			taip.mobile_network_connected=intent.getBooleanExtra(TASK_AUTO_INTF_EXTRA_KEY_MOBILE_NETWORK_CONNECTED, false);
			taip.proximity_sensor_active=intent.getBooleanExtra(TASK_AUTO_INTF_EXTRA_KEY_PROXIMITY_SENSOR_ACTIVE, false);
			taip.proximity_sensor_available=intent.getBooleanExtra(TASK_AUTO_INTF_EXTRA_KEY_PROXIMITY_SENSOR_AVAILABLE, false);
			taip.proximity_sensor_detected=intent.getBooleanExtra(TASK_AUTO_INTF_EXTRA_KEY_PROXIMITY_SENSOR_DETECTED,false);
			taip.screen_locked=intent.getBooleanExtra(TASK_AUTO_INTF_EXTRA_KEY_SCREEN_LOCKED, false);
			taip.telephony_available=intent.getBooleanExtra(TASK_AUTO_INTF_EXTRA_KEY_TELEPHONEY_STATE_AVAILABLE, false);
			taip.telephony_state_idle=intent.getBooleanExtra(TASK_AUTO_INTF_EXTRA_KEY_TELEPHONEY_STATE_IDLE, false);
			taip.telephony_state_offhook=intent.getBooleanExtra(TASK_AUTO_INTF_EXTRA_KEY_TELEPHONEY_STATE_OFFHOOK, false);
			taip.telephony_state_ringing=intent.getBooleanExtra(TASK_AUTO_INTF_EXTRA_KEY_TELEPHONEY_STATE_RINGING, false);
			taip.wifi_active=intent.getBooleanExtra(TASK_AUTO_INTF_EXTRA_KEY_WIFI_ACTIVE, false);
			taip.wifi_ssid_name=intent.getStringExtra(TASK_AUTO_INTF_EXTRA_KEY_WIFI_SSID_NAME);
			taip.wifi_ssid_addr=intent.getStringExtra(TASK_AUTO_INTF_EXTRA_KEY_WIFI_SSID_ADDR);
		}
	}
	final static private TaInterfaceParms reInitTaskAutomationInterfaceParms(TaInterfaceParms taip) {
		return reInitTaskAutomationInterfaceParmsStatus(taip);
	}
	final static private TaInterfaceParms reInitTaskAutomationInterfaceParmsStatus(TaInterfaceParms taip) {
		TaInterfaceParms n_taip=new TaInterfaceParms();
		n_taip.request_type=taip.request_type;
		n_taip.reply_action=taip.reply_action;
		n_taip.requestor_name=taip.requestor_name;
		n_taip.requestor_pkg=taip.requestor_pkg;

		n_taip.request_method_name=taip.request_method_name;
		n_taip.request_cmd=taip.request_cmd;
		n_taip.request_cmd_sub_type=taip.request_cmd_sub_type;
		n_taip.request_group_name=taip.request_group_name;
		n_taip.request_task_name=taip.request_task_name;
		return n_taip;
	}
	final static private String getExecutedMethodName() {
//		Thread.dumpStack();
//		StackTraceElement[] ste=Thread.currentThread().getStackTrace();
//		for (int i=0;i<ste.length;i++) Log.v("","i="+i+", name="+ste[i].getMethodName());
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		return name+"()";
	}
}

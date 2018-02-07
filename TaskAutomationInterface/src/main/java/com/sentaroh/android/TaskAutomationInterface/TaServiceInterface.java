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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import com.sentaroh.android.TaskAutomationInterface.TaInterfaceParms;

import android.app.Service;
import android.content.Intent;

final public class TaServiceInterface {
	final static public String BROADCAST_REQUEST=
			"com.sentaroh.android.TaskAutomation.EXTERNAL_INTERFACE_REQUEST";
	final static public String BROADCAST_REPLY=
			"com.sentaroh.android.TaskAutomation.EXTERNAL_INTERFACE_REPLY";
	final static public String BROADCAST_NOTIFICATION=
			"com.sentaroh.android.TaskAutomation.EXTERNAL_INTERFACE_NOTIFICATION";

	final public static String[] REQUEST_RESULT_REASON_DESC=new String[] {
		"Success",
		"Scheduler is disabled",
		"Unknow command",
		"Unknow sub command",
		"Reply action is invalid",
		"Task was active",
		"Task was already active",
		"Task was not active",
		"Task was inactive",
		"Task was enabled",
		"Task was disabled",
		"Task was not found",
		"Group was active",
		"Group was inactive",
		"Group was not found"
	};
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

	final public static String 	REQUEST_TYPE_REQUEST="Request";
	final public static String 	REQUEST_TYPE_REPLY="Reply";
	final public static String 	REQUEST_SYS_INFO="SysInfo";
	final public static String 	REQUEST_SYS_INFO_GET="Get";
	
	final public static String 	REQUEST_TASK="Task";
	final public static String 	REQUEST_TASK_LIST="List";
	final public static String 	REQUEST_TASK_START="Start";
	final public static String 	REQUEST_TASK_CANCEL="Cancel";
	final public static String 	REQUEST_TASK_STATUS="Status";

	final public static String 	REQUEST_GROUP="Group";
	final public static String 	REQUEST_GROUP_ACTIVATED="Activated";
	final public static String 	REQUEST_GROUP_DEACTIVATED="Deactivated";
	final public static String 	REQUEST_GROUP_STATUS="Status";
	final public static String 	REQUEST_GROUP_LIST="List";

	static final public void replyExternalApplication(
		Service svcInstance, TaInterfaceParms taip, Intent intent) {
		intent.setAction(taip.reply_action);
		svcInstance.sendBroadcast(intent);
	}

	static final public void readRequest(TaInterfaceParms taip, Intent intent) {
		taip.reply_action=intent.getStringExtra(TASK_AUTO_INTF_EXTRA_KEY_REPLY_ACTION);
		
		taip.requestor_name=intent.getStringExtra(TASK_AUTO_INTF_EXTRA_KEY_REQUESTOR_INFO_NAME);
		taip.requestor_pkg=intent.getStringExtra(TASK_AUTO_INTF_EXTRA_KEY_REQUESTOR_INFO_PKG);
		
		taip.request_cmd=intent.getStringExtra(TASK_AUTO_INTF_EXTRA_KEY_REQUEST_CMD);
		taip.request_cmd_sub_type=intent.getStringExtra(TASK_AUTO_INTF_EXTRA_KEY_REQUEST_CMD_SUBTYPE);
		taip.request_group_name=intent.getStringExtra(TASK_AUTO_INTF_EXTRA_KEY_REQUEST_GROUP_NAME);
		taip.request_task_name=intent.getStringExtra(TASK_AUTO_INTF_EXTRA_KEY_REQUEST_TASK_NAME);
	}
	
	static final public void writeReply(TaInterfaceParms taip, Intent intent) {
		intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_REPLY_RESULT_SUCCESS, taip.reply_result_success);
		intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_REPLY_RESULT_STATUS_CODE, taip.reply_result_status_code);
		writeReplySysInfo(taip,intent);
		writeReplyTaskList(taip,intent);
		writeReplyGroupList(taip,intent);
		intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_DATA_AVAILABE_SYS_INFO, taip.availavility_sys_info);
		intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_DATA_AVAILABE_TASK_LIST, taip.availavility_task_list);
		intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_DATA_AVAILABE_GROUP_LIST, taip.availavility_group_list);
	}

	static final private void writeReplyTaskList(TaInterfaceParms taip, Intent intent) {
		if (taip.reply_task_list!=null) {
			ByteArrayOutputStream bos=new ByteArrayOutputStream(1024*64);
			try {
				ObjectOutput oo=new ObjectOutputStream(bos);
				oo.writeObject(taip.reply_task_list);
				oo.flush();
				byte[] buff=bos.toByteArray();
				intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_REPLY_TASK_LIST,buff);
				taip.availavility_task_list=true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	static final private void writeReplyGroupList(TaInterfaceParms taip, Intent intent) {
		if (taip.reply_group_list!=null) {
			ByteArrayOutputStream bos=new ByteArrayOutputStream(1024*64);
			try {
				ObjectOutput oo=new ObjectOutputStream(bos);
				oo.writeObject(taip.reply_group_list);
				oo.flush();
				byte[] buff=bos.toByteArray();
				intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_REPLY_GROUP_LIST,buff);
				taip.availavility_group_list=true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	static final private void writeReplySysInfo(TaInterfaceParms taip, Intent intent) {
		if (taip.availavility_sys_info) {
			intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_AIRPLANE_MODE_ON, taip.airplane_mode_on);
			intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_BATTERY_CHARGING, taip.battery_charging);
			intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_BATTERY_LEVEL, taip.battery_level);
			intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_BLUETOOTH_ACTIVE, taip.bluetooth_active);
			intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_BLUETOOTH_AVAILABLE, taip.bluetooth_available);
			intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_BLUETOOTH_DEVICE_NAME, taip.bluetooth_device_name);
			intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_BLUETOOTH_DEVICE_ADDR, taip.bluetooth_device_addr);
			intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_RINGER_MODE_NORMAL, taip.ringer_mode_normal);
			intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_RINGER_MODE_SILENT, taip.ringer_mode_silent);
			intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_RINGER_MODE_VIBRATE, taip.ringer_mode_silent);
			intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_LIGHT_SENSOR_ACTIVE, taip.light_sensor_active);
			intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_LIGHT_SENSOR_AVAILABLE, taip.light_sensor_available);
			intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_LIGHT_SENSOR_VALUE, taip.light_sensor_value);
			intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_MOBILE_NETWORK_CONNECTED, taip.mobile_network_connected);
			intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_PROXIMITY_SENSOR_ACTIVE, taip.proximity_sensor_active);
			intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_PROXIMITY_SENSOR_AVAILABLE, taip.proximity_sensor_available);
			intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_PROXIMITY_SENSOR_DETECTED, taip.proximity_sensor_detected);
			intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_SCREEN_LOCKED, taip.screen_locked);
			intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_TELEPHONEY_STATE_AVAILABLE, taip.telephony_available);
			intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_TELEPHONEY_STATE_IDLE, taip.telephony_state_idle);
			intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_TELEPHONEY_STATE_OFFHOOK, taip.telephony_state_offhook);
			intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_TELEPHONEY_STATE_RINGING, taip.telephony_state_ringing);
			intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_WIFI_ACTIVE, taip.wifi_active);
			intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_WIFI_SSID_NAME, taip.wifi_ssid_name);
			intent.putExtra(TASK_AUTO_INTF_EXTRA_KEY_WIFI_SSID_ADDR, taip.wifi_ssid_addr);
		}
	}
}

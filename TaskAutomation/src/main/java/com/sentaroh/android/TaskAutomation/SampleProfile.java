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
import android.annotation.SuppressLint;

import java.util.ArrayList;

public class SampleProfile {


	public static void addSampleProfile(AdapterProfileList pla, boolean sample, boolean bhs) {
		if (sample) {
			addSampleProfilePhone(false,"*Sample for Task",pla.getDataList());
//			addSampleProfileSmbsyncTaFunc(false,"SMBSync_TAFUNC",pla.getDataList());
		}
		if (bhs) addBhsProfile(false,"*Sample for BeanShell API",pla.getDataList());
	};

	public static void addSampleProfile(ArrayList<ProfileListItem> pfl, boolean sample, boolean bhs) {
		if (sample) {
			addSampleProfilePhone(false,"Sample for Task",pfl);
//			addSampleProfileSmbsyncTaFunc(false,"SMBSync_TAFUNC",pfl);
		}
		if (bhs) addBhsProfile(false,"Sample for BeanShell API",pfl);
	};
	public static void addBhsProfile(boolean active, 
			String grp, ArrayList<ProfileListItem> pfl) {
		ProfileListItem tpli;

		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"a.Activity-NoParm", 
				PROFILE_ENABLED,"TaCmd.startActivity(\"com.sentaroh.android.SMBSync2\");");
		pfl.add(tpli);

		tpli= new ProfileListItem();
		String script="in=TaCmd.intentCreate();"+"\n"+
                "TaCmd.intentSetAction(in, \"com.sentaroh.android.SMBSync2.ACTION_START_SYNC\");"+"\n"+
				"String p2=\"HOGE1,HOGE2\";"+"\n"+
				"TaCmd.intentAddExtraData(in,\"SyncProfile\",p2);"+"\n"+
				"TaCmd.startActivity(in);";
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"a.Activity-ExtraData", 
				PROFILE_ENABLED,script);
		pfl.add(tpli);

		tpli= new ProfileListItem();
		script="TaCmd.startActivity(\"com.android.browser\",\"http://www.google.com\");";
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"a.Activity-UriData", 
				PROFILE_ENABLED,script);
		pfl.add(tpli);

		tpli= new ProfileListItem();
		script=
				"if (TaCmd.isWifiConnected()){\n" +
				"  ssid=TaCmd.getWifiSsidName().toLowerCase();\n" +
				"  if (!ssid.equals(\"my-wlan\") && !ssid.equals(\"docomo\") ) {\n" +
				"    if (TaCmd.setWifiSsidDisabled())\n" +
				"      TaCmd.showMessageNotification(ssid+\"は無効なAPのため削除しました。\", true, true,\"BLUE\");" +
				"  }\n"+
				"}";
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"b.Check Wifi SSID", 
				PROFILE_ENABLED,script);
		pfl.add(tpli);

		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.abort", 
				PROFILE_ENABLED,"TaCmd.abort();");
		pfl.add(tpli);

		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"b.status", 
				PROFILE_ENABLED,
				"TaCmd.debugMsg(0,\"I\",\"Android SDK=\"+TaCmd.getAndroidSdkInt());\n"+
				"TaCmd.debugMsg(0,\"I\",\"Battery level=\"+TaCmd.getBatteryLevel());\n"+ 
				"TaCmd.debugMsg(0,\"I\",\"Bluetooth device name=\"+TaCmd.getBluetoothDeviceName());\n"+ 
				"TaCmd.debugMsg(0,\"I\",\"Light sensor value=\"+TaCmd.getLightSensorValue());\n"+
//				"TaCmd.debugMsg(0,\"I\",\"Magnetic-field sensor value=\"+TaCmd.getMagneticFieldSensorValue());\n"+ 
				"TaCmd.debugMsg(0,\"I\",\"Wifi SSID name=\"+TaCmd.getWifiSsidName());\n"+
				"TaCmd.debugMsg(0,\"I\",\"Airplane mode on=\"+TaCmd.isAirplaneModeOn());\n"+ 
				"TaCmd.debugMsg(0,\"I\",\"Battery is charging=\"+TaCmd.isBatteryCharging());\n"+ 
				"TaCmd.debugMsg(0,\"I\",\"Bluetooth active=\"+TaCmd.isBluetoothActive());\n"+
				"TaCmd.debugMsg(0,\"I\",\"Bluetooth connected=\"+TaCmd.isBluetoothConnected());\n"+ 
				"TaCmd.debugMsg(0,\"I\",\"Mobile network connected=\"+TaCmd.isMobileNetworkConnected());\n"+ 
				"TaCmd.debugMsg(0,\"I\",\"Proximity sensor detected=\"+TaCmd.isProximitySensorDetected());\n"+ 
				"TaCmd.debugMsg(0,\"I\",\"Ringer mode normal=\"+TaCmd.isRingerModeNormal());\n"+
				"TaCmd.debugMsg(0,\"I\",\"Ringer mode silent=\"+TaCmd.isRingerModeSilent());\n"+
				"TaCmd.debugMsg(0,\"I\",\"Ringer mode vibrate=\"+TaCmd.isRingerModeVibrate());\n"+
				"TaCmd.debugMsg(0,\"I\",\"Screen locked=\"+TaCmd.isScreenLocked());\n"+
				"TaCmd.debugMsg(0,\"I\",\"Telephony call state idle=\"+TaCmd.isTelephonyCallStateIdle());\n"+
				"TaCmd.debugMsg(0,\"I\",\"Telephony call state offhook=\"+TaCmd.isTelephonyCallStateOffhook());\n"+
				"TaCmd.debugMsg(0,\"I\",\"Telephony call state ringing=\"+TaCmd.isTelephonyCallStateRinging());\n"+
				"TaCmd.debugMsg(0,\"I\",\"Wifi active=\"+TaCmd.isWifiActive());\n"+
				"TaCmd.debugMsg(0,\"I\",\"Wifi connected=\"+TaCmd.isWifiConnected());\n");
		pfl.add(tpli);
		
		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.cancelTask", 
				PROFILE_ENABLED,"TaCmd.cancelTask(\"TestTask\");");
		pfl.add(tpli);

		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.startTask", 
				PROFILE_ENABLED,"TaCmd.startTask(\"TestTask\");");
		pfl.add(tpli);

		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.resetIntervalTimer", 
				PROFILE_ENABLED,"TaCmd.resetIntervalTimer(\"TestTask\");");
		pfl.add(tpli);

		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.playBackDefaultAlarm", 
				PROFILE_ENABLED,"TaCmd.playBackDefaultAlarm();");
		pfl.add(tpli);

		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.playBackDefaultNotification", 
				PROFILE_ENABLED,"TaCmd.playBackDefaultNotification();");
		pfl.add(tpli);

		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.playBackDefaultRingtone", 
				PROFILE_ENABLED,"TaCmd.playBackDefaultRingtone();");
		pfl.add(tpli);

		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.restartScheduler", 
				PROFILE_ENABLED,"TaCmd.restartScheduler();");
		pfl.add(tpli);

		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.setAutoSyncDisabled", 
				PROFILE_ENABLED,"TaCmd.setAutoSyncDisabled();");
		pfl.add(tpli);
		
		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.setAutoSyncEnabled", 
				PROFILE_ENABLED,"TaCmd.setAutoSyncEnabled();");
		pfl.add(tpli);
		
		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.setRingerModeNormal", 
				PROFILE_ENABLED,"TaCmd.setRingerModeNormal();");
		pfl.add(tpli);

		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.setRingerModeVibrate", 
				PROFILE_ENABLED,"TaCmd.setRingerModeVibrate();");
		pfl.add(tpli);
		
		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.setRingerModeSilent", 
				PROFILE_ENABLED,"TaCmd.setRingerModeSilent();");
		pfl.add(tpli);
		
		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.setScreenLocked", 
				PROFILE_ENABLED,"TaCmd.setScreenLocked();");
		pfl.add(tpli);

		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.setScreenOnSync", 
				PROFILE_ENABLED,"TaCmd.setScreenOnSync();");
		pfl.add(tpli);

		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.setScreenOnAsync", 
				PROFILE_ENABLED,"TaCmd.setScreenOnAsync();");
		pfl.add(tpli);

		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.setScreenSwitchToHome", 
				PROFILE_ENABLED,"TaCmd.setScreenSwitchToHome();");
		pfl.add(tpli);
		
		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.setWifiSsidDisabled", 
				PROFILE_ENABLED,"TaCmd.setWifiSsidDisabled();");
		pfl.add(tpli);

		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.setWifiSsidRemoved", 
				PROFILE_ENABLED,"TaCmd.setWifiSsidRemoved();");
		pfl.add(tpli);

		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.showMessageDialog", 
				PROFILE_ENABLED,"TaCmd.showMessageDialog(\"Test Dialog\",true);");
		pfl.add(tpli);

		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.showMessageNotification", 
				PROFILE_ENABLED,"TaCmd.showMessageNotification(\"Test Notification\",true,true,\"RED\");");
		pfl.add(tpli);
		

		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.waitSeconds", 
				PROFILE_ENABLED,"TaCmd.waitSeconds(10);");
		pfl.add(tpli);

		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.playBackMusicDefVol", 
				PROFILE_ENABLED,"TaCmd.playBackMusic(\"/mnt/sdcard/test.mp3\");");
		pfl.add(tpli);

		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.playBackMusicVol", 
				PROFILE_ENABLED,"TaCmd.playBackMusic(\"/mnt/sdcard/test.mp3\",50,50);");
		pfl.add(tpli);

		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.playBackRingToneNobelium", 
				PROFILE_ENABLED,"TaCmd.playBackRingtone(\"ALERT\",\"Nobelium\");");
		pfl.add(tpli);

		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.playBackRingToneTejat", 
				PROFILE_ENABLED,"TaCmd.playBackRingtone(\"NOTIFICATION\",\"Tejat\");");
		pfl.add(tpli);

		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.playBackRingToneZeta", 
				PROFILE_ENABLED,"TaCmd.playBackRingtone(\"RINGTONE\",\"Zeta\");");
		pfl.add(tpli);

		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.setBluetoothOff", 
				PROFILE_ENABLED,"TaCmd.setBluetoothOff();");
		pfl.add(tpli);

		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.setBluetoothOn", 
				PROFILE_ENABLED,"TaCmd.setBluetoothOn();");
		pfl.add(tpli);

		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.setWifiOff", 
				PROFILE_ENABLED,"TaCmd.setWifiOff();");
		pfl.add(tpli);

		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.setWifiOn", 
				PROFILE_ENABLED,"TaCmd.setWifiOn();");
		pfl.add(tpli);

		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.vibrateDefaultPattern", 
				PROFILE_ENABLED,"TaCmd.vibrateDefaultPattern();");
		pfl.add(tpli);

		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.waitUntilBluetoothConnected", 
				PROFILE_ENABLED,"TaCmd.waitUntilBluetoothConnected(10);");
		pfl.add(tpli);

		tpli= new ProfileListItem();
		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.waitUntilWifiConnected", 
				PROFILE_ENABLED,"TaCmd.waitUntilWifiConnected(10);");
		pfl.add(tpli);
	};
	@SuppressLint("SdCardPath")
	public static void addSampleProfilePhone(boolean active, 
			String grp, ArrayList<ProfileListItem> pfl) {
		ProfileListItem tpli;
		ArrayList<String> act;
		ArrayList<String> trig;

		act=new ArrayList<String>();
		trig=new ArrayList<String>();
		act.add("AlarmMusic");
		trig.add("AlarmClock");
		tpli= new ProfileListItem();
		tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_TASK,"A.Alarm-clock", 
				PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
				PROFILE_ERROR_NOTIFICATION_DISABLED,act,trig);
		tpli.setProfileGroupShowed(active);
		pfl.add(tpli);

		
		ProfileListItem apli= new ProfileListItem();
		apli.setActionShellCmdEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"c.ShellCmdTest", 
				PROFILE_ENABLED,"fstrim -v /data", true);
		pfl.add(apli);
		
		act=new ArrayList<String>();
		trig=new ArrayList<String>();
		act.add("c.ShellCmdTest");
		act.add(BUILTIN_ACTION_WIFI_ON);
		act.add(BUILTIN_ACTION_WAIT_1_MIN);
		act.add("SSID Check");
		act.add("SMBSync2");
		trig.add("DailyTimer");
		tpli= new ProfileListItem();
		tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_TASK,"A.Daily-Activity", 
				PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
				PROFILE_ERROR_NOTIFICATION_DISABLED,act,trig);
		tpli.setProfileGroupShowed(active);
		pfl.add(tpli);

		act=new ArrayList<String>();
		trig=new ArrayList<String>();
		trig.add(BUILTIN_EVENT_BLUETOOTH_DISCONNECTED);
		act.add("BT.Wait-until-connected");
		act.add(BUILTIN_ACTION_ABORT_IF_BLUETOOTH_CONNECTED);
		act.add(BUILTIN_ACTION_BLUETOOTH_OFF);
		tpli= new ProfileListItem();
		tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_TASK,"BT.Bluetooth-Disconnected", 
				PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
				PROFILE_ERROR_NOTIFICATION_DISABLED,act,trig);
		tpli.setProfileGroupShowed(active);
		pfl.add(tpli);

		act=new ArrayList<String>();
		trig=new ArrayList<String>();
		trig.add(BUILTIN_EVENT_BLUETOOTH_OFF);
		act.add(BUILTIN_ACTION_CANCEL_EVENT_BLUETOOTH_ON);
		act.add(BUILTIN_ACTION_CANCEL_EVENT_BLUETOOTH_CONNECTED);
		act.add(BUILTIN_ACTION_CANCEL_EVENT_BLUETOOTH_DISCONNECTED);
		tpli= new ProfileListItem();
		tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_TASK,"BT.Bluetooth-Off", 
				PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
				PROFILE_ERROR_NOTIFICATION_DISABLED,act,trig);
		tpli.setProfileGroupShowed(active);
		pfl.add(tpli);

		act=new ArrayList<String>();
		trig=new ArrayList<String>();
		trig.add(BUILTIN_EVENT_BLUETOOTH_ON);
		act.add("BT.Wait-until-connected");
		act.add(BUILTIN_ACTION_ABORT_IF_BLUETOOTH_CONNECTED);
		act.add(BUILTIN_ACTION_BLUETOOTH_OFF);
		tpli= new ProfileListItem();
		tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_TASK,"BT.Bluetooth-On", 
				PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
				PROFILE_ERROR_NOTIFICATION_DISABLED,act,trig);
		tpli.setProfileGroupShowed(active);
		pfl.add(tpli);

		act=new ArrayList<String>();
		trig=new ArrayList<String>();
		trig.add(BUILTIN_EVENT_BLUETOOTH_CONNECTED);
		act.add(BUILTIN_ACTION_CANCEL_EVENT_BLUETOOTH_ON);
		tpli= new ProfileListItem();
		tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_TASK,"BT.Bluetooth-Connected", 
				PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
				PROFILE_ERROR_NOTIFICATION_DISABLED,act,trig);
		tpli.setProfileGroupShowed(active);
		pfl.add(tpli);

		act=new ArrayList<String>();
		trig=new ArrayList<String>();
		trig.add(BUILTIN_EVENT_SCREEN_LOCKED);
		act.add(BUILTIN_ACTION_WAIT_1_MIN);
		act.add(BUILTIN_ACTION_ABORT_IF_SCREEN_UNLOCKED);
		act.add(BUILTIN_ACTION_ABORT_IF_BLUETOOTH_CONNECTED);
		act.add(BUILTIN_ACTION_BLUETOOTH_OFF);
		tpli= new ProfileListItem();
		tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_TASK,"BT.Screen-Locked", 
				PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
				PROFILE_ERROR_NOTIFICATION_DISABLED,act,trig);
		tpli.setProfileGroupShowed(active);
		pfl.add(tpli);
		
		act=new ArrayList<String>();
		trig=new ArrayList<String>();
		trig.add(BUILTIN_EVENT_SCREEN_UNLOCKED);
		act.add(BUILTIN_ACTION_ABORT_IF_SCREEN_LOCKED);
		act.add(BUILTIN_ACTION_CANCEL_EVENT_SCREEN_LOCKED);
		act.add(BUILTIN_ACTION_ABORT_IF_POWER_IS_BATTERY);
		act.add(BUILTIN_ACTION_BLUETOOTH_ON);
		tpli= new ProfileListItem();
		tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_TASK,"BT.Screen-Unlocked", 
				PROFILE_DISABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
				PROFILE_ERROR_NOTIFICATION_DISABLED,act,trig);
		tpli.setProfileGroupShowed(active);
		pfl.add(tpli);
		
		act=new ArrayList<String>();
		trig=new ArrayList<String>();
		trig.add(BUILTIN_EVENT_SCREEN_LOCKED);
		act.add(BUILTIN_ACTION_CANCEL_EVENT_SCREEN_UNLOCKED);
		act.add(BUILTIN_ACTION_WAIT_1_MIN);
		act.add(BUILTIN_ACTION_ABORT_IF_SCREEN_UNLOCKED);
		act.add(BUILTIN_ACTION_ABORT_IF_POWER_IS_AC_OR_CHRAGE);
		act.add(BUILTIN_ACTION_WIFI_OFF);
		tpli= new ProfileListItem();
		tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_TASK,"WIFI.Screen-Locked", 
				PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
				PROFILE_ERROR_NOTIFICATION_DISABLED,act,trig);
		tpli.setProfileGroupShowed(active);
		pfl.add(tpli);

		act=new ArrayList<String>();
		trig=new ArrayList<String>();
		trig.add(BUILTIN_EVENT_SCREEN_UNLOCKED);
		act.add(BUILTIN_ACTION_ABORT_IF_SCREEN_LOCKED);
		act.add(BUILTIN_ACTION_CANCEL_EVENT_SCREEN_LOCKED);
		act.add(BUILTIN_ACTION_ABORT_IF_POWER_IS_BATTERY);
		act.add(BUILTIN_ACTION_WIFI_ON);
		tpli= new ProfileListItem();
		tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_TASK,"WIFI.Screen-Unlocked", 
				PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
				PROFILE_ERROR_NOTIFICATION_DISABLED,act,trig);
		tpli.setProfileGroupShowed(active);
		pfl.add(tpli);

		act=new ArrayList<String>();
		trig=new ArrayList<String>();
		trig.add(BUILTIN_EVENT_WIFI_DISCONNECTED);
		act.add("WIFI.Wait-until-connected");
		act.add(BUILTIN_ACTION_ABORT_IF_WIFI_CONNECTED);
		act.add(BUILTIN_ACTION_WIFI_OFF);
		tpli= new ProfileListItem();
		tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_TASK,"WIFI.Wifi-Disconnected", 
				PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
				PROFILE_ERROR_NOTIFICATION_DISABLED,act,trig);
		tpli.setProfileGroupShowed(active);
		pfl.add(tpli);
		
		act=new ArrayList<String>();
		trig=new ArrayList<String>();
		trig.add(BUILTIN_EVENT_WIFI_ON);
		act.add("WIFI.Wait-until-connected");
		act.add(BUILTIN_ACTION_ABORT_IF_WIFI_CONNECTED);
		act.add(BUILTIN_ACTION_WIFI_OFF);
		tpli= new ProfileListItem();
		tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_TASK,"WIFI.Wifi-On", 
				PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
				PROFILE_ERROR_NOTIFICATION_DISABLED,act,trig);
		tpli.setProfileGroupShowed(active);
		pfl.add(tpli);

		act=new ArrayList<String>();
		trig=new ArrayList<String>();
		trig.add(BUILTIN_EVENT_WIFI_OFF);
		act.add(BUILTIN_ACTION_CANCEL_EVENT_WIFI_ON);
		act.add(BUILTIN_ACTION_CANCEL_EVENT_WIFI_CONNECTED);
		act.add(BUILTIN_ACTION_CANCEL_EVENT_WIFI_DISCONNECTED);
		tpli= new ProfileListItem();
		tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_TASK,"WIFI.Wifi-Off", 
				PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
				PROFILE_ERROR_NOTIFICATION_DISABLED,act,trig);
		tpli.setProfileGroupShowed(active);
		pfl.add(tpli);

		act=new ArrayList<String>();
		trig=new ArrayList<String>();
		trig.add(BUILTIN_EVENT_WIFI_CONNECTED);
		act.add(BUILTIN_ACTION_CANCEL_EVENT_WIFI_ON);
		act.add(BUILTIN_ACTION_CANCEL_EVENT_WIFI_OFF);
		act.add(BUILTIN_ACTION_CANCEL_EVENT_WIFI_DISCONNECTED);
		tpli= new ProfileListItem();
		tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_TASK,"WIFI.Wifi-Connected", 
				PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
				PROFILE_ERROR_NOTIFICATION_DISABLED,act,trig);
		tpli.setProfileGroupShowed(active);
		pfl.add(tpli);

		act=new ArrayList<String>();
		trig=new ArrayList<String>();
		trig.add(BUILTIN_EVENT_PROXIMITY_UNDETECTED);
		act.add(BUILTIN_ACTION_CANCEL_EVENT_PROXIMITY_DETECTED);
		act.add(BUILTIN_ACTION_CANCEL_EVENT_LIGHT_DETECTED);
		act.add(BUILTIN_ACTION_ABORT_IF_SCREEN_UNLOCKED);
		act.add(BUILTIN_ACTION_SCREEN_ON);
		tpli= new ProfileListItem();
		tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_TASK,"SO.Screen-On-by-Proximity ", 
				PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
				PROFILE_ERROR_NOTIFICATION_DISABLED,act,trig);
		tpli.setProfileGroupShowed(active);
		pfl.add(tpli);

		act=new ArrayList<String>();
		trig=new ArrayList<String>();
		trig.add(BUILTIN_EVENT_LIGHT_DETECTED);
		act.add(BUILTIN_ACTION_CANCEL_EVENT_PROXIMITY_UNDETECTED);
		act.add(BUILTIN_ACTION_ABORT_IF_SCREEN_UNLOCKED);
		act.add(BUILTIN_ACTION_SCREEN_ON);
		tpli= new ProfileListItem();
		tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_TASK,"SO.Screen-On-by-LightSensor ", 
				PROFILE_DISABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
				PROFILE_ERROR_NOTIFICATION_DISABLED,act,trig);
		tpli.setProfileGroupShowed(active);
		pfl.add(tpli);
		
		act=new ArrayList<String>();
		trig=new ArrayList<String>();
		trig.add(BUILTIN_EVENT_POWER_SOURCE_CHANGED_BATTERY);
		act.add(BUILTIN_ACTION_ABORT_IF_BLUETOOTH_CONNECTED);
		act.add(BUILTIN_ACTION_ABORT_IF_SCREEN_UNLOCKED);
		act.add(BUILTIN_ACTION_WAIT_1_MIN);
		act.add(BUILTIN_ACTION_ABORT_IF_POWER_IS_AC_OR_CHRAGE);
		act.add(BUILTIN_ACTION_ABORT_IF_SCREEN_UNLOCKED);
		act.add(BUILTIN_ACTION_BLUETOOTH_OFF);
		tpli= new ProfileListItem();
		tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_TASK,"PWR.To-Battery-Bluetooth", 
				PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
				PROFILE_ERROR_NOTIFICATION_DISABLED,act,trig);
		tpli.setProfileGroupShowed(active);
		pfl.add(tpli);

		act=new ArrayList<String>();
		trig=new ArrayList<String>();
		trig.add(BUILTIN_EVENT_POWER_SOURCE_CHANGED_BATTERY);
		act.add(BUILTIN_ACTION_ABORT_IF_SCREEN_UNLOCKED);
		act.add(BUILTIN_ACTION_WAIT_1_MIN);
		act.add(BUILTIN_ACTION_ABORT_IF_POWER_IS_AC_OR_CHRAGE);
		act.add(BUILTIN_ACTION_ABORT_IF_SCREEN_UNLOCKED);
		act.add(BUILTIN_ACTION_WIFI_OFF);
		tpli= new ProfileListItem();
		tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_TASK,"PWR.To-Battery-Wifi", 
				PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
				PROFILE_ERROR_NOTIFICATION_DISABLED,act,trig);
		tpli.setProfileGroupShowed(active);
		pfl.add(tpli);

		tpli= new ProfileListItem();
		tpli.setActionMusicEntry(PROFILE_VERSION_CURRENT,grp,false, System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"AlarmMusic", 
				PROFILE_ENABLED,"/mnt/sdcard/AlarmMusic.mp3","-1","-1");
		tpli.setProfileGroupShowed(active);
		pfl.add(tpli);

		tpli= new ProfileListItem();
		ActivityExtraDataItem aedi=new ActivityExtraDataItem();
		ArrayList<ActivityExtraDataItem> aed_list=new ArrayList<ActivityExtraDataItem>();
		aedi.key_value="AutoStart";
		aedi.data_type=PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_BOOLEAN;
		aedi.data_value="true";
		aedi.data_value_array=PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_ARRAY_NO;
		aed_list.add(aedi);

		aedi=new ActivityExtraDataItem();
		aedi.key_value="AutoTerm";
		aedi.data_type=PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_BOOLEAN;
		aedi.data_value="true";
		aedi.data_value_array=PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_ARRAY_NO;
		aed_list.add(aedi);
		
		aedi=new ActivityExtraDataItem();
		aedi.key_value="SyncProfile";
		aedi.data_type=PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_STRING;
		aedi.data_value="S-TEST\u0003S-SAMPLE\u0003";
		aedi.data_value_array=PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_ARRAY_YES;
		aed_list.add(aedi);
		
		tpli.setActionAndroidEntry(PROFILE_VERSION_CURRENT,grp,false, System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"SMBSync2",
				PROFILE_ENABLED,"SMBSync2","com.sentaroh.android.SMBSync2",
				PROFILE_ACTION_TYPE_ACTIVITY_DATA_TYPE_EXTRA,"",aed_list);
		tpli.setProfileGroupShowed(active);
		pfl.add(tpli);
		
		tpli= new ProfileListItem();
//		setActionWifiEntry(String pt, String pn, String pe,  
//				String comp_type, String ssid, String ra)
		String[] c_val=new String[1];
		c_val[0]="MY-WLAN";
		tpli.setActionCompareEntry(PROFILE_VERSION_CURRENT,grp,false, System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"SSID Check", 
				PROFILE_ENABLED,
				PROFILE_ACTION_TYPE_COMPARE_TARGET_WIFI,
				PROFILE_ACTION_TYPE_COMPARE_CPMPARE_NE,c_val,
				PROFILE_ACTION_TYPE_COMPARE_RESULT_ABORT);
		tpli.setProfileGroupShowed(active);
		pfl.add(tpli);
		
		tpli= new ProfileListItem();
		tpli.setActionWaitEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"BT.Wait-until-connected", 
				PROFILE_ENABLED,PROFILE_ACTION_TYPE_WAIT_TARGET_BLUETOOTH_CONNECTED,"1",PROFILE_ACTION_TYPE_WAIT_TIMEOUT_UNITS_MIN);
		pfl.add(tpli);

		tpli= new ProfileListItem();
		tpli.setActionWaitEntry(PROFILE_VERSION_CURRENT,grp,false,System.currentTimeMillis(),
				PROFILE_TYPE_ACTION,"WIFI.Wait-until-connected", 
				PROFILE_ENABLED,PROFILE_ACTION_TYPE_WAIT_TARGET_WIFI_CONNECTED,"1",PROFILE_ACTION_TYPE_WAIT_TIMEOUT_UNITS_MIN);
		pfl.add(tpli);
		
		tpli= new ProfileListItem();
		tpli.setTimeEventEntry(PROFILE_VERSION_CURRENT,grp,false, System.currentTimeMillis(),
				PROFILE_TYPE_TIME,"AlarmClock", 
				PROFILE_ENABLED,PROFILE_DATE_TIME_TYPE_EVERY_DAY,"0000000","****/**/**","06:00");
		tpli.setProfileGroupShowed(active);
		pfl.add(tpli);

		tpli= new ProfileListItem();
		tpli.setTimeEventEntry(PROFILE_VERSION_CURRENT,grp,false, System.currentTimeMillis(),
				PROFILE_TYPE_TIME,"DailyTimer", 
				PROFILE_ENABLED,PROFILE_DATE_TIME_TYPE_EVERY_DAY,"0000000","****/**/**","00:00");
		tpli.setProfileGroupShowed(active);
		pfl.add(tpli);
		
		ProfileUtilities.sortProfileArrayList(null, pfl);
	};
	
}

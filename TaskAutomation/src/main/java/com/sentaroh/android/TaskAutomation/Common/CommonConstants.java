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

public class CommonConstants {
	public static final boolean POSITIVE=true;
	public static final boolean NEGATIVE=false;
	
	public static long SERIALIZABLE_NUMBER=26L;
	
	public static final int GENERAL_FILE_BUFFER_SIZE=4096*128;
	public static final int LOG_FILE_BUFFER_SIZE=4096*64;
	
	public final static int TASK_CTRL_THREAD_POOL_COUNT=7;
	public final static int HIGH_TASK_CTRL_THREAD_POOL_COUNT=3; 
	
	public static final int THREAD_PRIORITY_TASK_CTRL=Thread.NORM_PRIORITY+1;
	public static final int THREAD_PRIORITY_TASK_CTRL_HIGH=Thread.MAX_PRIORITY;
	public static final int THREAD_PRIORITY_TASK_EXEC=Thread.NORM_PRIORITY+1;
//	static final int THREAD_PRIORITY_TASK_SCHED=Thread.NORM_PRIORITY+2;
	
	public static final int RINGTONE_PLAYBACK_TIME=5*1000*60;
//	public static final int LIGHT_SENSOR_THRESHOLD=50;
    public static final int MAX_NOTIFICATION_COUNT=20;
    public static final int BATTERY_LEVEL_THRESHOLD_LOW=20;
    public static final int BATTERY_LEVEL_THRESHOLD_HIGH=90;
    public static final int BATTERY_LEVEL_THRESHOLD_CRITICAL=10;
    
	public static final long RESOURCE_CLEANUP_INTERVAL=30*60*1000;//10*60*1000;
	
	public static final String APPLICATION_TAG="TaskAutomation";
	
	public static final String LOG_FILE_NAME=APPLICATION_TAG+"_log";

	public static final String PREFS_BLUETOOTH_CONNECTED_DEVICE_LIST_KEY="bluetooth_connected_device_list";
	public static final String PREFS_TRUST_LIST_KEY="trust_list_key";
	public static final String CURRENT_POWER_SOURCE_AC="AC";
	public static final String CURRENT_POWER_SOURCE_BATTERY="BATTERY";
	public static final String EXPORT_IMPORT_SETTING_NAME="*TaskAutomation Settings";
	public static final String DEFAULT_PREFS_FILENAME="default_preferences";
	public static final String ACTIVITY_TASK_DATA_FILE_NAME="ActivityHolder.dat";
	
	public static final String SERVICE_TASK_LIST_FILE_NAME="TaskListHolder.dat";
	
	public final static String WAKE_LOCK_OPTION_SYSTEM="0";
	public final static String WAKE_LOCK_OPTION_ALWAYS="1";
	public final static String WAKE_LOCK_OPTION_DISCREATE="2";

	public static final String BATTERY_CONSUMPTION_DATA_KEY_1="battery_consumption_data_1";
	public static final String BATTERY_CONSUMPTION_DATA_KEY_2="battery_consumption_data_2";
	public static final String BATTERY_CONSUMPTION_DATA_KEY_3="battery_consumption_data_3";
	public static final String BATTERY_CONSUMPTION_DATA_KEY_4="battery_consumption_data_4";
	
	public static final String BROADCAST_LOG_SEND=
			"com.sentaroh.android.TaskAutomation.ACTION_LOG_SEND";
	public static final String BROADCAST_LOG_RESET=
			"com.sentaroh.android.TaskAutomation.ACTION_LOG_RESET";
	public static final String BROADCAST_LOG_ROTATE=
			"com.sentaroh.android.TaskAutomation.ACTION_LOG_ROTATE";
	public static final String BROADCAST_LOG_DELETE=
			"com.sentaroh.android.TaskAutomation.ACTION_LOG_DELETE";
	public static final String BROADCAST_LOG_FLUSH=
			"com.sentaroh.android.TaskAutomation.ACTION_LOG_FLUSH";

	public static final String BROADCAST_DISABLE_KEYGUARD=
			"com.sentaroh.android.TaskAutomation.ACTION_DISABLE_KEYGUARD";
	public static final String BROADCAST_ENABLE_KEYGUARD=
			"com.sentaroh.android.TaskAutomation.ACTION_ENABLE_KEYGUARD";
	public static final String BROADCAST_START_ACTIVITY_TASK_STATUS=
			"com.sentaroh.android.TaskAutomation.ACTION_START_ACTIVITY_TASK_STATUS";
	public static final String BROADCAST_TIMER_EXPIRED=
			"com.sentaroh.android.TaskAutomation.ACTION_TIMER_EXPIRED";
	public static final String BROADCAST_START_SCHEDULER=
			"com.sentaroh.android.TaskAutomation.ACTION_START_SCHEDULER";
	public static final String BROADCAST_LOAD_TRUST_LIST=
			"com.sentaroh.android.TaskAutomation.ACTION_LOAD_TRUST_LIST";
	public static final String BROADCAST_RESTART_SCHEDULER=
			"com.sentaroh.android.TaskAutomation.ACTION_RESTART_SCHEDULER";
	public static final String BROADCAST_RESET_SCHEDULER=
			"com.sentaroh.android.TaskAutomation.ACTION_RESET_SCHEDULER";
	public static final String BROADCAST_BUILD_TASK_LIST=
			"com.sentaroh.android.TaskAutomation.ACTION_BUILD_EXEC_TASK_LIST";

	public static final String BROADCAST_RELOAD_DEVICE_ADMIN=
			"com.sentaroh.android.TaskAutomation.ACTION_RELOAD_DEVICE_ADMIN";

	public static final String BROADCAST_SERVICE_HEARTBEAT=
			"com.sentaroh.android.TaskAutomation.ACTION_SERVICE_HEARTBEAT";
	
	public static final String IMMEDIATE_TASK_EXEC_GROUP_NAME="*ImmTask";

	public static final String CANCEL_ALL_SOUND_PLAYBACK_STOP_REQUEST="CANCEL_ALL_SOUND_PLAYBACK_STOP_REQUEST";
	
	public static final String CMD_THREAD_TO_SVC_RESET_INTERVAL_TIMER="CMD_THREAD_TO_SVC_RESET_INTERVAL_TIMER";
	public static final String CMD_THREAD_TO_SVC_START_TASK="CMD_THREAD_TO_SVC_START_TASK";
	public static final String CMD_THREAD_TO_SVC_FORCE_RESTART_SCHEDULER="CMD_THREAD_TO_SVC_FORCE_RESTART_SCHEDULER";
	public static final String CMD_THREAD_TO_SVC_RESTART_SCHEDULER="CMD_THREAD_TO_SVC_RESTART_SCHEDULER";

	public static final int EXTRA_DEVICE_EVENT_DEVICE_ON=1;
	public static final int EXTRA_DEVICE_EVENT_DEVICE_OFF=2;
	public static final int EXTRA_DEVICE_EVENT_DEVICE_CONNECTED=3;
	public static final int EXTRA_DEVICE_EVENT_DEVICE_DISCONNECTED=4;

	
	public static final String NTFY_TO_ACTV_TASK_STARTED="NTFY_TO_ACTV_TASK_STARTED"; 
	public static final String NTFY_TO_ACTV_TASK_ENDED="NTFY_TO_ACTV_TASK_ENDED";
	public static final String NTFY_TO_ACTV_TASK_UPDATE="NTFY_TO_ACTV_TASK_UPDATE";
	public static final String NTFY_TO_ACTV_ACTION_STARTED="NTFY_TO_ACTV_ACTION_STARTED"; 
	public static final String NTFY_TO_ACTV_ACTION_ENDED="NTFY_TO_ACTV_ACTION_ENDED";
	public static final String NTFY_TO_ACTV_ACTION_TACMD_STARTED="NTFY_TO_ACTV_ACTION_TACMD_STARTED";
	public static final String NTFY_TO_ACTV_ACTION_TACMD_ENDED="NTFY_TO_ACTV_ACTION_TACMD_ENDED";
	public static final String NTFY_TO_ACTV_INTERNAL_ERROR="NTFY_TO_ACTV_INTERNAL_ERROR";
	public static final String NTFY_TO_ACTV_CLOSE_DIALOG="NTFY_TO_ACTV_CLOSE_DIALOG";
	
	public static final String NTFY_TO_SVC_TASK_ENDED="NTFY_TO_SVC_TASK_ENDED";
	public static final String NTFY_TO_SVC_TASK_STARTED="NTFY_TO_SVC_TASK_STARTED";
	
	public static final String TRIGGER_EVENT_TASK="TASK";
	public static final String TRIGGER_EVENT_CATEGORY_TASK="TASK";
	public static final String TRIGGER_EVENT_CATEGORY_TIME="TIME";
	public static final String TRIGGER_EVENT_CATEGORY_BUILTIN="BUILTIN EVENT";

	public static final String MESSAGE_DIALOG_MESSAGE_KEY_GROUP="MESSAGE_GROUP"; 
	public static final String MESSAGE_DIALOG_MESSAGE_KEY_TASK="MESSAGE_TASK";
	public static final String MESSAGE_DIALOG_MESSAGE_KEY_ACTION="MESSAGE_ACTION";
	public static final String MESSAGE_DIALOG_MESSAGE_KEY_DIALOG_ID="MESSAGE_DLGID";
	public static final String MESSAGE_DIALOG_MESSAGE_KEY_TYPE="MESSAGE_TYPE";
	public static final String MESSAGE_DIALOG_MESSAGE_KEY_TEXT="MESSAGE_TEXT";
	public static final String MESSAGE_DIALOG_MESSAGE_TYPE_SOUND="MESSAGE_TYPE_SOUND";
	public static final String MESSAGE_DIALOG_MESSAGE_TYPE_DIALOG="MESSAGE_TYPE_MESSAGE";
//	public static final String MESSAGE_DIALOG_MESSAGE_TYPE_NOTIFICATION="MESSAGE_TYPE_NOTIFICATION";
	
	public static final String PROFILE_FILE_NAME="profile.txt";
	public static final String PROFILE_VERSION_V001="PROF-V-0001";
	public static final String PROFILE_VERSION_V002="PROF-V-0002";
	public static final String PROFILE_VERSION_V003="PROF-V-0003";
	public static final String PROFILE_VERSION_V004="PROF-V-0004";
	public static final String PROFILE_VERSION_V005="PROF-V-0005";
	public static final String PROFILE_VERSION_CURRENT=PROFILE_VERSION_V005;
	public static final String PROFILE_VERSION_PREFIX="PROF-V-";
	public static final String PROFILE_GROUP_DEFAULT="group";
	public static final String PROFILE_GROUP_ACTIVATED="S";
	public static final String PROFILE_GROUP_NOT_ACTIVATED="N";
	
	public final static String PROFILE_SETTINGS_TYPE_STRING="S";
	public final static String PROFILE_SETTINGS_TYPE_BOOLEAN="B";
	public final static String PROFILE_SETTINGS_TYPE_INT="I";
	
	public final static String PROFILE_TYPE_TASK="TASK";
	public final static String PROFILE_TYPE_TIME="TIME";
	public final static String PROFILE_TYPE_ACTION="ACTION";
//	public final static String PROFILE_TYPE_STATUS="STAT";
	public final static String PROFILE_TYPE_SETTINGS="SETTINGS";

	public final static String PROFILE_FILTER_TIME_EVENT_TASK="TIME-EVENT";

	public final static String PROFILE_ENABLED="E";
	public final static String PROFILE_DISABLED="D";
	public final static String PROFILE_ERROR_NOTIFICATION_ENABLED="1";
	public final static String PROFILE_ERROR_NOTIFICATION_DISABLED="0";
	
	public final static String PROFILE_ACTION_TYPE_ACTIVITY="ANDROID";
	public final static String PROFILE_ACTION_TYPE_ACTIVITY_DATA_TYPE_NONE="None";
	public final static String PROFILE_ACTION_TYPE_ACTIVITY_DATA_TYPE_URI="Uri";
	public final static String PROFILE_ACTION_TYPE_ACTIVITY_DATA_TYPE_EXTRA="Extra";
	public final static String PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_STRING="String";
	public final static String PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_INT="Int";
	public final static String PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_BOOLEAN="boolean";
	public final static String PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_ARRAY_NO="No";
	public final static String PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_ARRAY_YES="Yes";
	public final static String PROFILE_ACTION_TYPE_BUILTIN="BUILTIN";
	public final static String PROFILE_ACTION_TYPE_MUSIC="MUSIC";
	public final static String PROFILE_ACTION_TYPE_RINGTONE="RINGTONE";

	public final static String PROFILE_ACTION_TYPE_COMPARE="COMPARE";
	public final static String PROFILE_ACTION_TYPE_COMPARE_TARGET_BLUETOOTH="BLUETOOTH-DEV-IS";
	public final static String PROFILE_ACTION_TYPE_COMPARE_TARGET_BATTERY="BATTERY-LEVEL-IS";
	public final static String PROFILE_ACTION_TYPE_COMPARE_TARGET_LIGHT="LIGHT-VALUE-IS";
	public final static String PROFILE_ACTION_TYPE_COMPARE_TARGET_WIFI="WIFI-AP-IS";
	public final static String PROFILE_ACTION_TYPE_COMPARE_TARGET_TIME="TIME-IS";
	public final static String PROFILE_ACTION_TYPE_COMPARE_COMPARE_EQ="EQUALS";
	public final static String PROFILE_ACTION_TYPE_COMPARE_CPMPARE_NE="NOT-EQUALS";
	public final static String PROFILE_ACTION_TYPE_COMPARE_COMPARE_GT="GREATER-THAN";
	public final static String PROFILE_ACTION_TYPE_COMPARE_COMPARE_LT="LESS-THAN";
	public final static String PROFILE_ACTION_TYPE_COMPARE_COMPARE_BETWEEN="BETWEEN";
	public final static String PROFILE_ACTION_TYPE_COMPARE_RESULT_CONTINUE="CONTINUE";
	public final static String PROFILE_ACTION_TYPE_COMPARE_RESULT_ABORT="ABORT";
	public final static String PROFILE_ACTION_TYPE_COMPARE_RESULT_SKIP="SKIP";

	public final static String PROFILE_ACTION_TYPE_MESSAGE="MESSAGE";
	public final static String PROFILE_ACTION_TYPE_MESSAGE_DIALOG="DIALOG";
	public final static String PROFILE_ACTION_TYPE_MESSAGE_NOTIFICATION="NOTIFICATION";
	public final static String PROFILE_ACTION_TYPE_MESSAGE_LED="LED";
	public final static String PROFILE_ACTION_TYPE_MESSAGE_LED_RED="RED";
	public final static String PROFILE_ACTION_TYPE_MESSAGE_LED_BLUE="BLUE";
	public final static String PROFILE_ACTION_TYPE_MESSAGE_LED_GREEN="GREEN";
	public final static String PROFILE_ACTION_TYPE_MESSAGE_VIBRATION="VIBRATION";

	public final static String PROFILE_ACTION_TYPE_TIME="TIME";
	public final static String PROFILE_ACTION_TYPE_TIME_RESET_INTERVAL_TIMER=
			"RESET-INTERVAL-TIMER";
	public final static String PROFILE_ACTION_TYPE_TASK="TASK";
	public final static String PROFILE_ACTION_TYPE_TASK_START_TASK="START-TASK";
	public final static String PROFILE_ACTION_TYPE_TASK_CANCEL_TASK="CANCEL-TASK";

	public final static String PROFILE_ACTION_TYPE_WAIT="WAIT";
	public final static String PROFILE_ACTION_TYPE_WAIT_TARGET_WIFI_CONNECTED="UNTIL-WIFI-CONNECTED";
	public final static String PROFILE_ACTION_TYPE_WAIT_TARGET_BLUETOOTH_CONNECTED="UNTIL-BLUETOOTH-CONNECTED";
	public final static String PROFILE_ACTION_TYPE_WAIT_TIMEOUT_TYPE_NOTIMEOUT="NO-TIMEOUT";
	public final static String PROFILE_ACTION_TYPE_WAIT_TIMEOUT_TYPE_TIMEOUTIS="TIMEOUT-IS";
	public final static String PROFILE_ACTION_TYPE_WAIT_TIMEOUT_UNITS_SEC="SECONDS";
	public final static String PROFILE_ACTION_TYPE_WAIT_TIMEOUT_UNITS_MIN="MINUTES";

	public final static String PROFILE_ACTION_TYPE_SHELL_COMMAND="SHELL-COMMAND";

	public final static String PROFILE_ACTION_RINGTONE_TYPE_ALERT="ALERT";
	public final static String PROFILE_ACTION_RINGTONE_TYPE_ALARM="ALARM";
	public final static String PROFILE_ACTION_RINGTONE_TYPE_NOTIFICATION="NOTIFICATION";
	public final static String PROFILE_ACTION_RINGTONE_TYPE_RINGTONE="RINGTONE";

	public final static String PROFILE_ACTION_TYPE_BSH_SCRIPT="BSH-SCRIPT";
	
	public final static String PROFILE_RETROSPECIVE_ENABLED="E";
	public final static String PROFILE_RETROSPECIVE_DISABLED="D";

	public static final String PROFILE_DATE_TIME_TYPE_ONE_SHOT="ONE_SHOT";
	public static final String PROFILE_DATE_TIME_TYPE_DAY_OF_THE_WEEK="DAY_OF_THE_WEEK";
	public static final String PROFILE_DATE_TIME_TYPE_EVERY_YEAR="EVERY_YEAR";
	public static final String PROFILE_DATE_TIME_TYPE_EVERY_MONTH="EVERY_MONTH";
	public static final String PROFILE_DATE_TIME_TYPE_EVERY_DAY="EVERY_DAY";
	public static final String PROFILE_DATE_TIME_TYPE_EVERY_HOUR="EVERY_HOUR";
	public static final String PROFILE_DATE_TIME_TYPE_INTERVAL="INTERVAL";
	
	public static final String BUILTIN_PREFIX="*";
	public static final String BUILTIN_ACTION_PRIMITIVE_PREFIX="*[";
	public static final String BUILTIN_ACTION_ABORT="*[ABORT]";
	public static final String BUILTIN_ACTION_AUTO_SYNC_ENABLED="*[AUTO-SYNC-ENABLED]";
	public static final String BUILTIN_ACTION_AUTO_SYNC_DISABLED="*[AUTO-SYNC-DISABLED]";
	public static final String BUILTIN_ACTION_BLUETOOTH_ON="*[BLUETOOTH-ON]";
	public static final String BUILTIN_ACTION_BLUETOOTH_OFF="*[BLUETOOTH-OFF]";
	public static final String BUILTIN_ACTION_PLAYBACK_DEFAULT_ALARM="*[PLAYBACK-DEFALT-ALARM]";
	public static final String BUILTIN_ACTION_PLAYBACK_DEFAULT_NOTIFICATION="*[PLAYBACK-DEFALT-NOTIFICATION]";
	public static final String BUILTIN_ACTION_PLAYBACK_DEFAULT_RINGTONE="*[PLAYBACK-DEFALT-RINGTONE]";
	public static final String BUILTIN_ACTION_RESTART_SCHEDULER="*[RESTART-SCHEDULER]";
	public static final String BUILTIN_ACTION_RINGER_VIBRATE="*[RINGER-VIBRATE]";
	public static final String BUILTIN_ACTION_RINGER_NORMAL="*[RINGER-NORMAL]";
	public static final String BUILTIN_ACTION_RINGER_SILENT="*[RINGER-SILENT]";
	public static final String BUILTIN_ACTION_SCREEN_LOCKED="*[LOCK-SCREEN]";
	public static final String BUILTIN_ACTION_SCREEN_KEYGUARD_DISABLED="*[KEYGUARD-DISABLED]";
	public static final String BUILTIN_ACTION_SCREEN_KEYGUARD_ENABLED="*[KEYGUARD-ENABLED]";
	public static final String BUILTIN_ACTION_SCREEN_ON="*[SCREEN-ON]";
	public static final String BUILTIN_ACTION_SCREEN_ON_ASYNC="*[SCREEN-ON-ASYNC]";
	public static final String BUILTIN_ACTION_SWITCH_TO_HOME="*[SWITCH-TO-HOME]";
	public static final String BUILTIN_ACTION_VIBRATE="*[VIBRATE]";
	public static final String BUILTIN_ACTION_WAIT_1_SEC="*[WAIT-1-SEC]";
	public static final String BUILTIN_ACTION_WAIT_5_SEC="*[WAIT-5-SEC]";
	public static final String BUILTIN_ACTION_WAIT_1_MIN="*[WAIT-1-MIN]";
	public static final String BUILTIN_ACTION_WAIT_5_MIN="*[WAIT-5-MIN]";
	public static final String BUILTIN_ACTION_WIFI_ON="*[WIFI-ON]";
	public static final String BUILTIN_ACTION_WIFI_OFF="*[WIFI-OFF]";
	public static final String BUILTIN_ACTION_WIFI_DISABLE_CONNECTED_SSID="*[WIFI-DISABLE-CONN-SSID]";
	public static final String BUILTIN_ACTION_WIFI_REMOVE_CONNECTED_SSID="*[WIFI-REMOVE-CONN-SSID]";
	
	public static final String BUILTIN_ACTION_ABORT_PREFIX="*ABORT(";
	public static final String BUILTIN_ACTION_ABORT_IF_WIFI_ON="*ABORT(IF-WIFI-ON)";
	public static final String BUILTIN_ACTION_ABORT_IF_WIFI_CONNECTED="*ABORT(IF-WIFI-CONNECTED)";
	public static final String BUILTIN_ACTION_ABORT_IF_WIFI_DISCONNECTED="*ABORT(IF-WIFI-DISCONNECTED)";
	public static final String BUILTIN_ACTION_ABORT_IF_WIFI_OFF="*ABORT(IF-WIFI-OFF)";
	public static final String BUILTIN_ACTION_ABORT_IF_BLUETOOTH_ON="*ABORT(IF-BLUETOOTH-ON)";
	public static final String BUILTIN_ACTION_ABORT_IF_BLUETOOTH_CONNECTED="*ABORT(IF-BLUETOOTH-CONNECTED)";
	public static final String BUILTIN_ACTION_ABORT_IF_BLUETOOTH_DISCONNECTED="*ABORT(IF-BLUETOOTH-DISCONNECTED)";
	public static final String BUILTIN_ACTION_ABORT_IF_BLUETOOTH_OFF="*ABORT(IF-BLUETOOTH-OFF)";
	public static final String BUILTIN_ACTION_ABORT_IF_SCREEN_UNLOCKED="*ABORT(IF-SCREEN-UNLOCKED)";
	public static final String BUILTIN_ACTION_ABORT_IF_SCREEN_LOCKED="*ABORT(IF-SCREEN-LOCKED)";
	
	public static final String BUILTIN_ACTION_ABORT_IF_SCREEN_ON="*ABORT(IF-SCREEN-ON)";
	public static final String BUILTIN_ACTION_ABORT_IF_SCREEN_OFF="*ABORT(IF-SCREEN-OFF)";
	
	public static final String BUILTIN_ACTION_ABORT_IF_TRUSTED="*ABORT(IF-TRUSTED)";
	public static final String BUILTIN_ACTION_ABORT_IF_NOT_TRUSTED="*ABORT(IF-NOT_TRUSTED)";
	
	public static final String BUILTIN_ACTION_ABORT_IF_POWER_IS_AC_OR_CHRAGE="*ABORT(IF-PWR-IS-AC/CHARGE)";
	public static final String BUILTIN_ACTION_ABORT_IF_POWER_IS_BATTERY="*ABORT(IF-PWR-IS-BATTERY)";
	public static final String BUILTIN_ACTION_ABORT_IF_PROXIMITY_DETECTED="*ABORT(IF-PROXIMITY-DETECTED)";
	public static final String BUILTIN_ACTION_ABORT_IF_PROXIMITY_UNDETECTED="*ABORT(IF-PROXIMITY-UNDETECTED)";
	public static final String BUILTIN_ACTION_ABORT_IF_LIGHT_DETECTED="*ABORT(IF-LIGHT-DETECTED)";
	public static final String BUILTIN_ACTION_ABORT_IF_LIGHT_UNDETECTED="*ABORT(IF-LIGHT-UNDETECTED)";
	public static final String BUILTIN_ACTION_ABORT_IF_CALL_STATE_IDLE="*ABORT(IF-CALL-STATE-IDLE)";
	public static final String BUILTIN_ACTION_ABORT_IF_CALL_STATE_OFF_HOOK="*ABORT(IF-CALL-STATE-OFF-HOOK)";
	public static final String BUILTIN_ACTION_ABORT_IF_CALL_STATE_RINGING="*ABORT(IF-CALL-STATE-RINGING)";
	public static final String BUILTIN_ACTION_ABORT_IF_AIRPLANE_MODE_ON="*ABORT(IF-AIRPLANE-MODE-ON)";
	public static final String BUILTIN_ACTION_ABORT_IF_AIRPLANE_MODE_OFF="*ABORT(IF-AIRPLANE-MODE-OFF)";
	public static final String BUILTIN_ACTION_ABORT_IF_MOBILE_NETWORK_CONNECTED="*ABORT(IF-MOBILE_NETWORK_CONNECTED)";
	public static final String BUILTIN_ACTION_ABORT_IF_MOBILE_NETWORK_DISCONNECTED="*ABORT(IF-MOBILE_NETWORK_DISCONNECTED)";
//	public static final String BUILTIN_ACTION_ABORT_IF_NETWORK_CONNECTED="*ABORT(IF-NETWORK_CONNECTED)";
//	public static final String BUILTIN_ACTION_ABORT_IF_NETWORK_DISCONNECTED="*ABORT(IF-NETWORK_DISCONNECTED)";
	public static final String BUILTIN_ACTION_ABORT_IF_ORIENTATION_LANDSCAPE="*ABORT(IF-ORIENTATION-LANDSCAPE)";
	public static final String BUILTIN_ACTION_ABORT_IF_ORIENTATION_PORTRAIT="*ABORT(IF-ORIENTATION-PORTRAIT)";

	public static final String BUILTIN_ACTION_SKIP_PREFIX="*SKIP(";
	public static final String BUILTIN_ACTION_SKIP_IF_WIFI_ON="*SKIP(IF-WIFI-ON)";
	public static final String BUILTIN_ACTION_SKIP_IF_WIFI_CONNECTED="*SKIP(IF-WIFI-CONNECTED)";
	public static final String BUILTIN_ACTION_SKIP_IF_WIFI_DISCONNECTED="*SKIP(IF-WIFI-DISCONNECTED)";
	public static final String BUILTIN_ACTION_SKIP_IF_WIFI_OFF="*SKIP(IF-WIFI-OFF)";
	public static final String BUILTIN_ACTION_SKIP_IF_BLUETOOTH_ON="*SKIP(IF-BLUETOOTH-ON)";
	public static final String BUILTIN_ACTION_SKIP_IF_BLUETOOTH_CONNECTED="*SKIP(IF-BLUETOOTH-CONNECTED)";
	public static final String BUILTIN_ACTION_SKIP_IF_BLUETOOTH_DISCONNECTED="*SKIP(IF-BLUETOOTH-DISCONNECTED)";
	public static final String BUILTIN_ACTION_SKIP_IF_BLUETOOTH_OFF="*SKIP(IF-BLUETOOTH-OFF)";
	public static final String BUILTIN_ACTION_SKIP_IF_SCREEN_UNLOCKED="*SKIP(IF-SCREEN-UNLOCKED)";
	public static final String BUILTIN_ACTION_SKIP_IF_SCREEN_LOCKED="*SKIP(IF-SCREEN-LOCKED)";
	
	public static final String BUILTIN_ACTION_SKIP_IF_SCREEN_ON="*SKIP(IF-SCREEN-ON)";
	public static final String BUILTIN_ACTION_SKIP_IF_SCREEN_OFF="*SKIP(IF-SCREEN-OFF)";

	public static final String BUILTIN_ACTION_SKIP_IF_TRUSTED="*SKIP(IF-TRUSTED)";
	public static final String BUILTIN_ACTION_SKIP_IF_NOT_TRUSTED="*SKIP(IF-NOT_TRUSTED)";
	
	public static final String BUILTIN_ACTION_SKIP_IF_POWER_IS_AC_OR_CHRAGE="*SKIP(IF-PWR-IS-AC/CHARGE)";
	public static final String BUILTIN_ACTION_SKIP_IF_POWER_IS_BATTERY="*SKIP(IF-PWR-IS-BATTERY)";
	public static final String BUILTIN_ACTION_SKIP_IF_PROXIMITY_DETECTED="*SKIP(IF-PROXIMITY-DETECTED)";
	public static final String BUILTIN_ACTION_SKIP_IF_PROXIMITY_UNDETECTED="*SKIP(IF-PROXIMITY-UNDETECTED)";
	public static final String BUILTIN_ACTION_SKIP_IF_LIGHT_DETECTED="*SKIP(IF-LIGHT-DETECTED)";
	public static final String BUILTIN_ACTION_SKIP_IF_LIGHT_UNDETECTED="*SKIP(IF-LIGHT-UNDETECTED)";
	public static final String BUILTIN_ACTION_SKIP_IF_CALL_STATE_IDLE="*SKIP(IF-CALL-STATE-IDLE)";
	public static final String BUILTIN_ACTION_SKIP_IF_CALL_STATE_OFF_HOOK="*SKIP(IF-CALL-STATE-OFF-HOOK)";
	public static final String BUILTIN_ACTION_SKIP_IF_CALL_STATE_RINGING="*SKIP(IF-CALL-STATE-RINGING)";
	public static final String BUILTIN_ACTION_SKIP_IF_AIRPLANE_MODE_ON="*SKIP(IF-AIRPLANE-MODE-ON)";
	public static final String BUILTIN_ACTION_SKIP_IF_AIRPLANE_MODE_OFF="*SKIP(IF-AIRPLANE-MODE-OFF)";
	public static final String BUILTIN_ACTION_SKIP_IF_MOBILE_NETWORK_CONNECTED="*SKIP(IF-MOBILE_NETWORK_CONNECTED)";
	public static final String BUILTIN_ACTION_SKIP_IF_MOBILE_NETWORK_DISCONNECTED="*SKIP(IF-MOBILE_NETWORK_DISCONNECTED)";
//	public static final String BUILTIN_ACTION_SKIP_IF_NETWORK_CONNECTED="*SKIP(IF-NETWORK_CONNECTED)";
//	public static final String BUILTIN_ACTION_SKIP_IF_NETWORK_DISCONNECTED="*SKIP(IF-NETWORK_DISCONNECTED)";
	public static final String BUILTIN_ACTION_SKIP_IF_ORIENTATION_LANDSCAPE="*SKIP(IF-ORIENTATION-LANDSCAPE)";
	public static final String BUILTIN_ACTION_SKIP_IF_ORIENTATION_PORTRAIT="*SKIP(IF-ORIENTATION-PORTRAIT)";
	
	public static final String BUILTIN_ACTION_BLOCK_PREFIX="*BLOCK";
	public static final String BUILTIN_ACTION_BLOCK_EVENT_CLEAR="*BLOCK(CLEAR)";
	public static final String BUILTIN_ACTION_BLOCK_EVENT_BLOCK_ALL="*BLOCK(ALL-EVENT)";
	public static final String BUILTIN_ACTION_BLOCK_EVENT_BOOT_COMPLETED="*BLOCK(BOOT-COMPLETED)";
	public static final String BUILTIN_ACTION_BLOCK_EVENT_WIFI_ON="*BLOCK(WIFI-ON)";
	public static final String BUILTIN_ACTION_BLOCK_EVENT_WIFI_CONNECTED="*BLOCK(WIFI-CONNECTED)";
	public static final String BUILTIN_ACTION_BLOCK_EVENT_WIFI_DISCONNECT="*BLOCK(WIFI-DISCONNECTED)";
	public static final String BUILTIN_ACTION_BLOCK_EVENT_WIFI_OFF="*BLOCK(WIFI-OFF)";
	public static final String BUILTIN_ACTION_BLOCK_EVENT_BLUETOOTH_ON="*BLOCK(BLUETOOTH-ON)";
	public static final String BUILTIN_ACTION_BLOCK_EVENT_BLUETOOTH_CONNECTED="*BLOCK(BLUETOOTH-CONNECTED)";
	public static final String BUILTIN_ACTION_BLOCK_EVENT_BLUETOOTH_DISCONNECTED="*BLOCK(BLUETOOTH-DISCONNECTED)";
	public static final String BUILTIN_ACTION_BLOCK_EVENT_BLUETOOTH_OFF="*BLOCK(BLUETOOTH-OFF)";
	public static final String BUILTIN_ACTION_BLOCK_EVENT_PROXIMITY_DETECTED="*BLOCK(PROXIMITY-DETECTED)";
	public static final String BUILTIN_ACTION_BLOCK_EVENT_PROXIMITY_UNDETECTED="*BLOCK(PROXIMITY-UNDETECTED)";
	public static final String BUILTIN_ACTION_BLOCK_EVENT_LIGHT_DETECTED="*BLOCK(LIGHT-DETECTED)";
	public static final String BUILTIN_ACTION_BLOCK_EVENT_LIGHT_UNDETECTED="*BLOCK(LIGHT-UNDETECTED)";
	public static final String BUILTIN_ACTION_BLOCK_EVENT_PHONE_CALL_STATE_IDLE="*BLOCK(PHONE-CALL-STATE-IDLE)";
	public static final String BUILTIN_ACTION_BLOCK_EVENT_PHONE_CALL_STATE_OFF_HOOK="*BLOCK(PHONE-CALL-STATE-OFFHOOK)";
	public static final String BUILTIN_ACTION_BLOCK_EVENT_PHONE_CALL_STATE_RINGING="*BLOCK(PHONE-CALL-STATE-RINGING)";
	public static final String BUILTIN_ACTION_BLOCK_EVENT_AIRPLANE_MODE_ON="*BLOCK(AIRPLANE-MODE-ON)";
	public static final String BUILTIN_ACTION_BLOCK_EVENT_AIRPLANE_MODE_OFF="*BLOCK(AIRPLANE-MODE-OFF)";
	public static final String BUILTIN_ACTION_BLOCK_EVENT_MOBILE_NETWORK_CONNECTED="*BLOCK(MOBILE_NETWORK_CONNECTED)";
	public static final String BUILTIN_ACTION_BLOCK_EVENT_MOBILE_NETWORK_DISCONNECTED="*BLOCK(MOBILE_NETWORK_DISCONNECTED)";
//	public static final String BUILTIN_ACTION_BLOCK_EVENT_NETWORK_CONNECTED="*BLOCK(NETWORK_CONNECTED)";
//	public static final String BUILTIN_ACTION_BLOCK_EVENT_NETWORK_DISCONNECTED="*BLOCK(NETWORK_DISCONNECTED)";

	public static final String BUILTIN_ACTION_CANCEL_PREFIX="*CANCEL";
	public static final String BUILTIN_ACTION_CANCEL_EVENT_BOOT_COMPLETED="*CANCEL(BOOT-COMPLETED)";
	public static final String BUILTIN_ACTION_CANCEL_EVENT_WIFI_ON="*CANCEL(WIFI-ON)";
	public static final String BUILTIN_ACTION_CANCEL_EVENT_WIFI_CONNECTED="*CANCEL(WIFI-CONNECTED)";
	public static final String BUILTIN_ACTION_CANCEL_EVENT_WIFI_DISCONNECTED="*CANCEL(WIFI-DISCONNECTED)";
	public static final String BUILTIN_ACTION_CANCEL_EVENT_WIFI_OFF="*CANCEL(WIFI-OFF)";
	public static final String BUILTIN_ACTION_CANCEL_EVENT_BLUETOOTH_ON="*CANCEL(BLUETOOTH-ON)";
	public static final String BUILTIN_ACTION_CANCEL_EVENT_BLUETOOTH_CONNECTED="*CANCEL(BLUETOOTH-CONNECTED)";
	public static final String BUILTIN_ACTION_CANCEL_EVENT_BLUETOOTH_DISCONNECTED="*CANCEL(BLUETOOTH-DISCONNECTED)";
	public static final String BUILTIN_ACTION_CANCEL_EVENT_BLUETOOTH_OFF="*CANCEL(BLUETOOTH-OFF)";
	public static final String BUILTIN_ACTION_CANCEL_EVENT_PROXIMITY_DETECTED="*CANCEL(PROXIMITY-DETECTED)";
	public static final String BUILTIN_ACTION_CANCEL_EVENT_PROXIMITY_UNDETECTED="*CANCEL(PROXIMITY-UNDETECTED)";
	public static final String BUILTIN_ACTION_CANCEL_EVENT_LIGHT_DETECTED="*CANCEL(LIGHT-DETECTED)";
	public static final String BUILTIN_ACTION_CANCEL_EVENT_LIGHT_UNDETECTED="*CANCEL(LIGHT-UNDETECTED)";
	public static final String BUILTIN_ACTION_CANCEL_EVENT_SCREEN_LOCKED="*CANCEL(SCREEN-LOCKED)";
	public static final String BUILTIN_ACTION_CANCEL_EVENT_SCREEN_UNLOCKED="*CANCEL(SCREEN-UNLOCKED)";
	public static final String BUILTIN_ACTION_CANCEL_EVENT_POWER_SOURCE_CHANGED_AC="*CANCEL(PWR-SOURCE-CHANGED-TO-AC)";
	public static final String BUILTIN_ACTION_CANCEL_EVENT_POWER_SOURCE_CHANGED_BATTERY="*CANCEL(PWR-SOURCE-CHANGED-TO-BATTERY)";
	public static final String BUILTIN_ACTION_CANCEL_EVENT_PHONE_CALL_STATE_IDLE="*CANCEL(PHONE-STATE-IDLE)";
	public static final String BUILTIN_ACTION_CANCEL_EVENT_PHONE_CALL_STATE_OFF_HOOK="*CANCEL(PHONE-STATE-OFF-HOOK)";
	public static final String BUILTIN_ACTION_CANCEL_EVENT_PHONE_CALL_STATE_RINGING="*CANCEL(PHONE-STATE-RINGING)";
	public static final String BUILTIN_ACTION_CANCEL_EVENT_AIRPLANE_MODE_ON="*CANCEL(AIRPLANE-MODE-ON)";
	public static final String BUILTIN_ACTION_CANCEL_EVENT_AIRPLANE_MODE_OFF="*CANCEL(AIRPLANE-MODE-OFF)";
	public static final String BUILTIN_ACTION_CANCEL_EVENT_MOBILE_NETWORK_CONNECTED="*CANCEL(MOBILE_NETWORK_CONNECTED)";
	public static final String BUILTIN_ACTION_CANCEL_EVENT_MOBILE_NETWORK_DISCONNECTED="*CANCEL(MOBILE_NETWORK_DISCONNECTED)";
//	public static final String BUILTIN_ACTION_CANCEL_EVENT_NETWORK_CONNECTED="*CANCEL(NETWORK_CONNECTED)";
//	public static final String BUILTIN_ACTION_CANCEL_EVENT_NETWORK_DISCONNECTED="*CANCEL(NETWORK_DISCONNECTED)";

	public static final String BUILTIN_EVENT_ALL="*ALL";
	public static final String BUILTIN_EVENT_BOOT_COMPLETED="*BOOT-COMPLETED";
	public static final String BUILTIN_EVENT_WIFI_ON="*WIFI-ON";
	public static final String BUILTIN_EVENT_WIFI_CONNECTED="*WIFI-CONNECTED";
	public static final String BUILTIN_EVENT_WIFI_DISCONNECTED="*WIFI-DISCONNECTED";
	public static final String BUILTIN_EVENT_WIFI_OFF="*WIFI-OFF";
	public static final String BUILTIN_EVENT_BLUETOOTH_ON="*BLUETOOTH-ON";
	public static final String BUILTIN_EVENT_BLUETOOTH_DISCONNECTED="*BLUETOOTH-DISCONNECTED";
	public static final String BUILTIN_EVENT_BLUETOOTH_CONNECTED="*BLUETOOTH-CONNECTED";
	public static final String BUILTIN_EVENT_BLUETOOTH_OFF="*BLUETOOTH-OFF";
	public static final String BUILTIN_EVENT_PROXIMITY_DETECTED="*PROXIMITY-DETECTED";
	public static final String BUILTIN_EVENT_PROXIMITY_UNDETECTED="*PROXIMITY-UNDETECTED";
	public static final String BUILTIN_EVENT_LIGHT_DETECTED="*LIGHT-DETECTED";
	public static final String BUILTIN_EVENT_LIGHT_UNDETECTED="*LIGHT-UNDETECTED";
	public static final String BUILTIN_EVENT_SCREEN_UNLOCKED="*SCREEN-UNLOCKED";
	public static final String BUILTIN_EVENT_SCREEN_LOCKED="*SCREEN-LOCKED";
	public static final String BUILTIN_EVENT_POWER_SOURCE_CHANGED_AC="*PWR-SOURCE-CHANGED-TO-AC";
	public static final String BUILTIN_EVENT_POWER_SOURCE_CHANGED_BATTERY="*PWR-SOURCE-CHANGED-TO-BATTERY";
	public static final String BUILTIN_EVENT_PHONE_CALL_STATE_IDLE="*PHONE-CALL-STATE-IDLE";
	public static final String BUILTIN_EVENT_PHONE_CALL_STATE_OFF_HOOK="*PHONE-CALL-STATE-OFF-HOOK";
	public static final String BUILTIN_EVENT_PHONE_CALL_STATE_RINGING="*PHONE-CALL-STATE-RINGING";
	public static final String BUILTIN_EVENT_BATTERY_LEVEL_CHANGED="*BATTERY-LEVEL-CHANGED";
	public static final String BUILTIN_EVENT_BATTERY_FULLY_CHARGED="*BATTERY-FULLY-CHARGED";
	public static final String BUILTIN_EVENT_BATTERY_LEVEL_LOW="*BATTERY-LEVEL-LOW";
	public static final String BUILTIN_EVENT_BATTERY_LEVEL_CRITICAL="*BATTERY-LEVEL-CRITICAL";
	public static final String BUILTIN_EVENT_BATTERY_LEVEL_HIGH="*BATTERY-LEVEL-HIGH";
	public static final String BUILTIN_EVENT_AIRPLANE_MODE_ON="*AIRPLANE-MODE-ON";
	public static final String BUILTIN_EVENT_AIRPLANE_MODE_OFF="*AIRPLANE-MODE-OFF";
//	public static final String BUILTIN_EVENT_NETWORK_CONNECTED="*NETWORK_CONNCTED";
//	public static final String BUILTIN_EVENT_NETWORK_DISCONNECTED="*NETWORK_DISCONNCTED";
	public static final String BUILTIN_EVENT_MOBILE_NETWORK_CONNECTED="*MOBILE_NETWORK_CONNCTED";
	public static final String BUILTIN_EVENT_MOBILE_NETWORK_DISCONNECTED="*MOBILE_NETWORK_DISCONNCTED";
}

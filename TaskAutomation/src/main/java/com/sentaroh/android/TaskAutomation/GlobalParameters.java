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

import java.util.ArrayList;

import com.sentaroh.android.Utilities.CommonGlobalParms;
import com.sentaroh.android.Utilities.ThemeColorList;
import com.sentaroh.android.Utilities.ThreadCtrl;
import com.sentaroh.android.Utilities.ContextMenu.CustomContextMenu;
import com.sentaroh.android.Utilities.Dialog.CommonDialog;
import com.sentaroh.android.Utilities.Widget.CustomSpinnerAdapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.util.SparseArray;
import android.widget.ListView;
import android.widget.Spinner;

import static com.sentaroh.android.TaskAutomation.CommonConstants.APPLICATION_TAG;
import static com.sentaroh.android.TaskAutomation.CommonConstants.LOG_FILE_NAME;
import static com.sentaroh.android.TaskAutomation.CommonConstants.WAKE_LOCK_OPTION_ALWAYS;
import static com.sentaroh.android.TaskAutomation.CommonConstants.WAKE_LOCK_OPTION_SYSTEM;

public class GlobalParameters extends CommonGlobalParms {

    //	Settings parameter
    public boolean settingExitClean;
    public int     settingMaxTaskCount=20;
    public int     settingMaxBshDriverCount=5;
    public int     settingTaskExecThreadPoolCount=5;
    public int     settingDebugLevel;
    public boolean settingEnableScheduler,settingEnableMonitor;
    public int     settingLogMaxFileCount=10;
    public String  settingLogMsgDir="", settingLogMsgFilename=LOG_FILE_NAME;
    public boolean settingLogOption=false;
    public long    settingHeartBeatIntervalTime=600*1000;

    public String settingWakeLockOption=WAKE_LOCK_OPTION_ALWAYS;
    public boolean settingWakeLockLightSensor=false;
    public boolean settingWakeLockProximitySensor=false;

    public boolean  settingUseRootPrivilege=false;

    public int settingLightSensorMonitorIntervalTime,
            settingLightSensorMonitorActiveTime,
            settingLightSensorDetectThreshHold,
            settingLightSensorDetectIgnoreTime;
    public boolean  settingLightSensorUseThread=false;//true;
    public int settingProximitySensorMonitorIntervalTime=1500,
            settingProximitySensorMonitorActiveTime=5;

    public boolean initialyzeRequired=true;

	public FragmentManager frgamentMgr=null;
	
	public EnvironmentParms envParms=null;
	
	public AdapterProfileList profileAdapter=null;
	public ListView profileListView=null;
	public Spinner spinnerProfileGroupSelector=null;
	public CustomSpinnerAdapter adapterProfileGroupSelector=null;
	public Spinner spinnerProfileFilterSelector=null;
	public CustomSpinnerAdapter adapterProfileFilterSelector=null;
	public ListView profileGroupListView=null;
	public AdapterProfileGroupList profileGroupAdapter=null;

	public ArrayList<String> androidApplicationList=null;
	public CommonUtilities util=null;
	public Context context=null;
	public CustomContextMenu ccMenu = null;
	public CommonDialog commonDlg=null;

	public ThemeColorList themeColorList=null;
	
	//
	public SparseArray<String[]> importedSettingParmList=new SparseArray<String[]>();
	public String localRootDir;
	public MediaPlayer mpMusic=null, mpRingtone=null;
	public ThreadCtrl tcMusic=null, tcRingtone=null;
	
	public ArrayList<RingtoneListItem> ringtoneList=null;
	
	public ISchedulerClient svcServer=null;
	public ISchedulerCallback consoleCallbackListener=null;

	public EnvironmentParms immTaskTestEnvParms=null;

	public String currentSelectedExtraDataType="";
	public boolean ringTonePlayBackEnabled=false;
	public Thread ringtonePlayBackThread=null;

	//ActionProfile
	public AdapterActivityExtraDataEditList activityExtraDataEditListAdapter=null;
	public AdapterDataArrayEditList actionCompareDataAdapter=null;
	
	//TaskProfile
	public AdapterTaskActionEditList taskActionListAdapter=null;


	public GlobalParameters() {};

    public void initGlobalParamter(Context c) {
        if (initialyzeRequired) {
            initialyzeRequired=false;
            loadSettingParms(c);
        }
    }

    public void setLogParms(GlobalParameters gp) {
        setDebugLevel(gp.settingDebugLevel);
        setLogcatEnabled(gp.settingLogOption);
        setLogLimitSize(10 * 1024 * 1024);
        setLogMaxFileCount(gp.settingLogMaxFileCount);
        setLogEnabled(gp.settingLogOption);
        setLogDirName(gp.settingLogMsgDir);
        setLogFileName(gp.settingLogMsgFilename);
        setApplicationTag(APPLICATION_TAG);
    }

    public void loadSettingParms(Context c) {
        settingExitClean=
                CommonUtilities.getPrefMgr(c).getBoolean(c.getString(R.string.settings_main_exit_clean),true);
        settingDebugLevel=Integer.parseInt(
                CommonUtilities.getPrefMgr(c).getString(c.getString(R.string.settings_main_log_level),"0"));
        settingEnableScheduler=
                CommonUtilities.getPrefMgr(c).getBoolean(c.getString(R.string.settings_main_enable_scheduler),true);
        settingMaxTaskCount=Integer.valueOf(
                CommonUtilities.getPrefMgr(c).getString(c.getString(R.string.settings_main_scheduler_max_task_count),"20"));

        settingTaskExecThreadPoolCount=Integer.valueOf(
                CommonUtilities.getPrefMgr(c).getString(c.getString(R.string.settings_main_scheduler_thread_pool_count),"5"));

        settingEnableMonitor=
                CommonUtilities.getPrefMgr(c).getBoolean(c.getString(R.string.settings_main_scheduler_monitor),true);

        settingWakeLockOption=
                CommonUtilities.getPrefMgr(c).getString(c.getString(R.string.settings_main_scheduler_sleep_wake_lock_option),WAKE_LOCK_OPTION_SYSTEM);
        settingWakeLockLightSensor=
                CommonUtilities.getPrefMgr(c).getBoolean(c.getString(R.string.settings_main_scheduler_sleep_wake_lock_light_sensor),false);
        settingWakeLockProximitySensor=
                CommonUtilities.getPrefMgr(c).getBoolean(c.getString(R.string.settings_main_scheduler_sleep_wake_lock_proximity_sensor),false);

        settingUseRootPrivilege=
                CommonUtilities.getPrefMgr(c).getBoolean(c.getString(R.string.settings_main_use_root_privilege),false);

        settingLogMsgDir=
                CommonUtilities.getPrefMgr(c).getString(c.getString(R.string.settings_main_log_dir),"");
        settingLogOption=
                CommonUtilities.getPrefMgr(c).getBoolean(c.getString(R.string.settings_main_log_option),false);
        settingLogMaxFileCount=Integer.valueOf(
                CommonUtilities.getPrefMgr(c).getString(c.getString(R.string.settings_main_log_file_max_count),"10"));
        settingLightSensorUseThread=
                CommonUtilities.getPrefMgr(c).getBoolean(
                        c.getString(R.string.settings_main_light_sensor_use_thread),true);
        String lmit=CommonUtilities.getPrefMgr(c).getString(
                c.getString(R.string.settings_main_light_sensor_monitor_interval_time),"1000");
        settingLightSensorMonitorIntervalTime=Integer.valueOf(lmit);
        String lmat=CommonUtilities.getPrefMgr(c).getString(
                c.getString(R.string.settings_main_light_sensor_monitor_active_time),"10");
        settingLightSensorMonitorActiveTime=Integer.valueOf(lmat);
        String ldth=CommonUtilities.getPrefMgr(c).getString(
                c.getString(R.string.settings_main_light_sensor_detect_thresh_hold),"30");
        settingLightSensorDetectThreshHold=Integer.valueOf(ldth);
        String llsoat=CommonUtilities.getPrefMgr(c).getString(
                c.getString(R.string.settings_main_light_sensor_ignore_time),"2");
        settingLightSensorDetectIgnoreTime=Integer.valueOf(llsoat);

        setLogParms(this);
    }

    final public void setSettingEnableScheduler(Context c, boolean p) {
        CommonUtilities.getPrefMgr(c).edit().putBoolean(c.getString(R.string.settings_main_enable_scheduler),p).commit();
        settingEnableScheduler=p;
    };

    public void setSettingOptionLogEnabled(boolean enabled) {
        CommonUtilities.getPrefMgr(context).edit().putBoolean(context.getString(R.string.settings_main_log_option), enabled).commit();
        if (settingDebugLevel == 0 && enabled) {
            CommonUtilities.getPrefMgr(context).edit().putString(context.getString(R.string.settings_main_log_level), "1").commit();
        }
        settingLogOption=enabled;
    }

    public void clearParms() {
		frgamentMgr=null;
		
		envParms=null;
		
		profileAdapter=null;
		profileListView=null;
		spinnerProfileGroupSelector=null;
		adapterProfileGroupSelector=null;
		spinnerProfileFilterSelector=null;
		adapterProfileFilterSelector=null;
		profileGroupListView=null;
		profileGroupAdapter=null;

		androidApplicationList=null;
		util=null;
		context=null;
		ccMenu = null;
		commonDlg=null;

		//
		importedSettingParmList=new SparseArray<String[]>();
		localRootDir=null;
		mpMusic=mpRingtone=null;
		tcMusic=tcRingtone=null;
		
		ringtoneList=null;
		
		svcServer=null;
		consoleCallbackListener=null;

		immTaskTestEnvParms=null;

		currentSelectedExtraDataType="";
		ringTonePlayBackEnabled=false;
		ringtonePlayBackThread=null;

		//ActionProfile
		activityExtraDataEditListAdapter=null;
		actionCompareDataAdapter=null;
		
		//TaskProfile
		taskActionListAdapter=null;

	}
}

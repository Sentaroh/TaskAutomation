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
import static com.sentaroh.android.TaskAutomation.Config.QuickTaskConstants.QUICK_TASK_CURRENT_VERSION;
import static com.sentaroh.android.TaskAutomation.Config.QuickTaskConstants.QUICK_TASK_GROUP_NAME;
import static com.sentaroh.android.TaskAutomation.Config.QuickTaskConstants.QUICK_TASK_VERSION_KEY;
import static com.sentaroh.android.TaskAutomation.WidgetConstants.DEVICE_BTN_PREFIX;
import static com.sentaroh.android.TaskAutomation.WidgetConstants.WIDGET_INTENT_PREFIX;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.sentaroh.android.TaskAutomation.Common.ActivityExtraDataItem;
import com.sentaroh.android.TaskAutomation.Common.BluetoothDeviceListItem;
import com.sentaroh.android.TaskAutomation.Common.EnvironmentParms;
import com.sentaroh.android.TaskAutomation.Common.ProfileListItem;
import com.sentaroh.android.TaskAutomation.Common.TaskActionItem;
import com.sentaroh.android.TaskAutomation.Common.TaskHistoryItem;
import com.sentaroh.android.TaskAutomation.Common.TaskListHolder;
import com.sentaroh.android.TaskAutomation.Common.TaskListItem;
import com.sentaroh.android.TaskAutomation.Common.TaskLookupListItem;
import com.sentaroh.android.TaskAutomation.Common.TaskManagerParms;
import com.sentaroh.android.TaskAutomation.Common.TaskResponse;
import com.sentaroh.android.TaskAutomation.Config.ProfileUtilities;
import com.sentaroh.android.TaskAutomation.Config.QuickTaskMaintenance;
import com.sentaroh.android.TaskAutomation.Config.SampleProfile;
import com.sentaroh.android.TaskAutomationInterface.TaServiceInterface;
import com.sentaroh.android.Utilities.StringUtil;
import com.sentaroh.android.Utilities.NotifyEvent;
import com.sentaroh.android.Utilities.NotifyEvent.NotifyEventListener;
import com.sentaroh.android.Utilities.ThreadCtrl;

public final class SchedulerService extends Service {

	static private BroadcastReceiver mBatteryStatusReceiver=null;
	static private BroadcastReceiver mWifiReceiver=null;
	static private BroadcastReceiver mSleepReceiver=null;
	static private BroadcastReceiver mBluetoothReceiver=null;
	static private MiscellaneousReceiver mMiscellaneousReceiver=null;

	static private Context mContext;
	
	static private CommonUtilities mUtil=null;
	
	static private ArrayList<ProfileListItem>mProfileArrayList=null;
	
	static private ArrayList<TaskListItem> mTimerEventTaskList=null;
	static private ArrayList<TaskListItem> mBuiltinEventTaskList=null;
	static private ArrayList<TaskLookupListItem> mBuiltinEventTaskLookupTable=null;
	static private ArrayList<TaskListItem> mTaskEventTaskList=null;

	static private EnvironmentParms mEnvParms=null;
	static private TaskManagerParms mTaskMgrParms=null;

    private static Sensor mSensorLight=null, mSensorMagneticField=null, 
    		mSensorProximity=null, mSensorAccelerometer=null;

    static private boolean mBatteryLevelLowNotified=false,
    				mBatteryLevelHighNotified=false,
    				mBatteryLevelCriticalNotified=false;
    
//	static private long mLastProximityDetectedTime=0;
    
    static private long mLastLightDetectedTime=0;
    static private int mLastLightSensorDetectedValue=-1;
	static private ThreadCtrl mTcLightSensorListener=null;
	static private Thread mThreadLightSensorListener=null;
	static private LightSensorReceiver mReceiverLligh=null;
	static private MagneticFieldSensorReceiver mReceiverMagneticField=null;
	static private ProximitySensorReceiver mReceiverProximity=null;
	static private AccelerometerSensorReceiver mReceiverAccelerometer=null;
	static private SensorManager mSensorManager=null;

	static private ServiceConnection mSvcMonitorConnection=null;
	static private ISchedulerMonitor mSchedulerMonitor=null;
	
	static private boolean mRequiredSensorLight = false, mRequiredSensorProximity = false,
			mRequiredSensorMagneticField = false, mRequiredSensorAccelerometer = false;
	static private boolean mRequiredBatteryLevelExecution=false;
	static private WakeLock mWakelockForSleep=null;//, mWakelockSvcProcess=null;
//							mWakelockProximityHandler=null, mWakelockLightHandler=null
//							mWakelockMagneticHandler=null
							;
//							mWakelockWifiHandler=null, mWakelockBluetoothHandler=null;
	
	static private AudioManager mAudioManager;
	
	static private Service mSvcInstance=null;
	
	static private WidgetService mWidgetSvc=null;
	
	static private WifiManager mWifiMgr=null;
	
	static private Handler mUiHandler=null;

	@Override
	public void onConfigurationChanged(Configuration newconfig) {
		mUtil.addDebugMsg(1,"I","onConfigurationChanged entered,"+
				" current Orientation="+mEnvParms.currentOrientation+
				", new Orientation="+newconfig.orientation);
		if (mEnvParms.currentOrientation!=newconfig.orientation) {
			mEnvParms.currentOrientation=newconfig.orientation;
		}
	};
	
	@Override
    public void onCreate() {
//    	StrictMode.enableDefaults();
		mContext=getApplicationContext();
		mSvcInstance=this;
		mSensorManager=(SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);
		mWifiMgr=(WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
		mUiHandler=new Handler();

	    mLastLightDetectedTime=0;
	    mLastLightSensorDetectedValue=-1;
		mTcLightSensorListener=new ThreadCtrl();
		mThreadLightSensorListener=null;
		mReceiverLligh=new LightSensorReceiver();
		mReceiverMagneticField=new MagneticFieldSensorReceiver();
		mReceiverProximity=new ProximitySensorReceiver();
		mReceiverAccelerometer=new AccelerometerSensorReceiver();

		mBatteryStatusReceiver=new BatteryStatusReceiver();
		mWifiReceiver=new WifiReceiver();
		mSleepReceiver=new SleepReceiver();
		mBluetoothReceiver=new BluetoothReceiver();
		mMiscellaneousReceiver=new MiscellaneousReceiver();
		
		mProfileArrayList=new ArrayList<ProfileListItem>();
		
		mTimerEventTaskList=new ArrayList<TaskListItem>();
		mBuiltinEventTaskList=new ArrayList<TaskListItem>();
		mBuiltinEventTaskLookupTable=new ArrayList<TaskLookupListItem> ();
		mTaskEventTaskList=new ArrayList<TaskListItem>();

		mEnvParms=new EnvironmentParms();
		mTaskMgrParms=new TaskManagerParms();
		

		mTaskMgrParms.svcMsgs.loadString(mContext);
		mEnvParms.loadSettingParms(mContext);
        mUtil=new CommonUtilities(mContext, "SchedSvc", mEnvParms);
		mWakelockForSleep=((PowerManager)mContext.getSystemService(Context.POWER_SERVICE))
    			.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
    					| PowerManager.ON_AFTER_RELEASE, "TaskAutomation-Sensor");
//		mWakelockSvcProcess=((PowerManager)mContext.getSystemService(Context.POWER_SERVICE))
//    			.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK 
//    					| PowerManager.ON_AFTER_RELEASE
//    					, "TaskAutomation-Service");
//		
//		mWakelockProximityHandler=((PowerManager)mContext.getSystemService(Context.POWER_SERVICE))
//    			.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
//    					| PowerManager.ON_AFTER_RELEASE, "TaskAutomation-Proximity");
//		mWakelockLightHandler=((PowerManager)mContext.getSystemService(Context.POWER_SERVICE))
//    			.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
//    					| PowerManager.ON_AFTER_RELEASE, "TaskAutomation-Light");

		mAudioManager=(AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
		
		mUtil.addDebugMsg(1,"I","onCreate entered");
		listInitSettingsParm();

		mUtil.addLogMsg("I",mTaskMgrParms.svcMsgs.msgs_svc_started, " ", String.valueOf(android.os.Process.myPid()));

	    TaskManager.initTaskMgrParms(mEnvParms,mTaskMgrParms, mContext, mUtil);
	    initialyzeThreadResponseNotifyEvent(mContext);
		TaskManager.initNotification(mTaskMgrParms, mEnvParms);

        mTaskMgrParms.locationUtil=new LocationUtilities(mTaskMgrParms,mEnvParms,mUtil);

		startBasicEventReceiver(mContext);

		initialyzeTaskEnvironmentParms(mContext);

		mWidgetSvc=new WidgetService(mContext,mTaskMgrParms, mEnvParms, mUtil);
		
		if (!isProfileFileExisted()) {
			SampleProfile.addSampleProfile(mProfileArrayList,true,true);
			QuickTaskMaintenance.buildQuickTaskProfile(mContext, mProfileArrayList, 
					mUtil, QUICK_TASK_GROUP_NAME);
			ProfileUtilities.sortProfileArrayList(mUtil, mProfileArrayList);
			ProfileUtilities.setQuickTaskProfileActivated(mUtil,mProfileArrayList,true);
			mUtil.saveProfileToFileByService(mProfileArrayList);
			mContext.deleteFile(SERVICE_TASK_LIST_FILE_NAME);
			buildTaskList();
		} else restoreTaskList();

		startSvcMonitor();
		
		setHeartBeat(mContext);
    	if (mEnvParms.settingEnableScheduler) {
    		initialExecuteSchedulerTask(mContext);
    	}
//    	startMagneticFieldSensorReceiver();
//    	startAccelerometerSensorReceiver();
    	
//    	startSleepDetector();
    };
    
    private Thread mThreadSleepDetector=null;
    private long mSleepDetectorPrevTime=0;
    private ThreadCtrl mSleepDetectorTc=null;
    @SuppressWarnings("unused")
	private boolean mSleepDetctorSleepDetected=false;
    @SuppressWarnings("unused")
	private void startSleepDetector() {
    	mSleepDetectorTc=new ThreadCtrl();
    	mThreadSleepDetector=new Thread(){
    		@Override
    		public void run() {
    			mUtil.addDebugMsg(1,"I","SleepDector started.");
    			mSleepDetectorPrevTime=System.currentTimeMillis();
    			long diff=0;
    			while(mSleepDetectorTc.isEnabled()) {
    				SystemClock.sleep(30000);
    				diff=System.currentTimeMillis()-mSleepDetectorPrevTime;
    				mSleepDetectorPrevTime=System.currentTimeMillis();
    				if (diff>30500) {
    					mSleepDetctorSleepDetected=true;
    					mUtil.addDebugMsg(1,"I","SleepDector Sleep detected, diff="+diff);
    				}
    			}
    			mUtil.addDebugMsg(1,"I","SleepDector ended.");
    		}
    	};
    	mThreadSleepDetector.setName("SleepDetector");
    	mThreadSleepDetector.start();
    };
    
	@Override
    public int onStartCommand(Intent in, int flags, int startId) {
		acqSvcWakeLock();
		String t_act="";
    	if (in!=null && in.getAction()!=null) t_act=in.getAction();
    	else t_act="";
    	final String action=t_act;
		if (mEnvParms.settingDebugLevel>=2 && !action.equals(BROADCAST_SERVICE_HEARTBEAT))
			mUtil.addDebugMsg(2,"I","onStartCommand entered, action=",action,", flag=",String.valueOf(flags));
		if (action.startsWith(WIDGET_INTENT_PREFIX)) {
			mWidgetSvc.startWidgetIntentThread(action);
		} else if (action.startsWith(DEVICE_BTN_PREFIX)) {
			mWidgetSvc.processDeviceButton(action);
		} else if (action.startsWith(TaServiceInterface.BROADCAST_REQUEST)) {
			ExternalApplicationInterface.processExternalIntent(
					mTaskMgrParms, mEnvParms, mUtil, mSvcInstance, in, action, 
					mTimerEventTaskList, mBuiltinEventTaskList, mTaskEventTaskList, 
					mProfileArrayList);
		} else if (action.startsWith(CANCEL_ALL_SOUND_PLAYBACK_STOP_REQUEST)) {
			cancelSoundPlayBackTask();
		} else {
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				analyzeConnectivityChanged(in);
//			} else if (action.equals("android.intent.action.ACTION_IDLE_MAINTENANCE_START")) {
//				mUtil.addDebugMsg(1,"I","onStartCommand entered, action=",action,", flag=",String.valueOf(flags));
//			} else if (action.equals("com.android.server.task.controllers.IdleController.ACTION_TRIGGER_IDLE")) {
//				mUtil.addDebugMsg(1,"I","onStartCommand entered, action=",action,", flag=",String.valueOf(flags));
			} else if (action.equals(BROADCAST_RELOAD_DEVICE_ADMIN)) {
				mEnvParms.settingDeviceAdmin=
						CommonUtilities.getPrefMgr(mContext).getBoolean(mContext.getString(R.string.settings_main_device_admin),false);
						
			} else if (action.equals(BROADCAST_BUILD_TASK_LIST)) {
	    		buildTaskList();
	    		if (mEnvParms.settingEnableScheduler) {
	    			rescheduleTimerEventTask(null);
	    			stopLightSensorReceiver();
	    			stopProximitySensorReceiver();
	    			startLightSensorReceiver();
	    			startProximitySensorReceiver();
	    		}
    		} else if (action.equals(BROADCAST_DISABLE_KEYGUARD)) {
    			if (mEnvParms.settingScreenKeyguardControlEnabled) {
        			mTaskMgrParms.setKeyguardDisabled();
        			mUtil.addDebugMsg(1,"I","disableKeyguard issued");
    			} else {
    				mUtil.addDebugMsg(1,"I","disableKeyguard ignored, Keyguard control is disabled");
    			}
    		} else if (action.equals(BROADCAST_ENABLE_KEYGUARD)) {
    			if (mEnvParms.settingScreenKeyguardControlEnabled) {
        			mTaskMgrParms.setKeyguardEnabled();
        			mUtil.addDebugMsg(1,"I","reenableKeyguard issued");
    			} else {
    				mUtil.addDebugMsg(1,"I","EnableKeyguard ignored, Keyguard control is disabled");
    			}
    		} else if (action.equals(BROADCAST_START_ACTIVITY_TASK_STATUS)) {
    			Intent in_b=new Intent(mContext.getApplicationContext(),ActivityTaskStatus.class);
				in_b.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(in_b);
    		} else if (action.equals(BROADCAST_SERVICE_HEARTBEAT)) {
    			cancelHeartBeat(mContext);
    			setHeartBeat(mContext);
	    		resourceCleanup(mTaskMgrParms, mEnvParms, mUtil);
    		} else if (action.equals(BROADCAST_LOAD_TRUST_LIST)) {
    			mTaskMgrParms.truestedList=CommonUtilities.loadTrustedDeviceList(mContext);
    			if (mTaskMgrParms.truestedList==null || mTaskMgrParms.truestedList.size()==0) mEnvParms.settingScreenKeyguardControlEnabled=false;
    		} else if (action.equals(BROADCAST_START_SCHEDULER)) {
    			mEnvParms.loadSettingParms(mContext);
    			mTaskMgrParms.truestedList=CommonUtilities.loadTrustedDeviceList(mContext);
    			if (mTaskMgrParms.truestedList==null || mTaskMgrParms.truestedList.size()==0) mEnvParms.settingScreenKeyguardControlEnabled=false;
//	    		initialExecuteSchedulerTask();
	    	} else if (action.equals(BROADCAST_RESTART_SCHEDULER)) {
	    		restartScheduler();
//	    	} else if (action.equals(BROADCAST_RESET_SCHEDULER)) {
//	    		boolean ena_sched=in.getBooleanExtra("settingEnableScheduler", false);
//	    		envParms.setSettingEnableScheduler(appContext,ena_sched);
//    			resetScheduler();
	    	} else if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
	    		if (mEnvParms.settingEnableScheduler) {
//    				initialExecuteSchedulerTask(appContext);
//	    			addTaskScheduleQueueBuiltin(mTaskMgrParms,mBuiltinEventTaskList,BUILTIN_EVENT_BOOT_COMPLETED);
	    			scheduleBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_BOOT_COMPLETED);
	    		}
	    	} else if (action.equals(Intent.ACTION_SHUTDOWN)) {
//	    		mUtil.resetLogReceiver();
	    	} else if (action.equals(Intent.ACTION_LOCALE_CHANGED)) {
	    		mTaskMgrParms.teMsgs.loadString(mContext);
	    		mTaskMgrParms.svcMsgs.loadString(mContext);
	    		mEnvParms.batteryChargeStatusString=parseBatteryChargeStatus(mLastBatteryStatusSt);
	    		mWidgetSvc.processBatteryStatusChanged();
	    		TaskManager.buildNotification(mTaskMgrParms, mEnvParms);
	    		TaskManager.showNotification(mTaskMgrParms, mEnvParms, mUtil);
    		} else if (action.equals(BROADCAST_TIMER_EXPIRED)) {
	    		if (mEnvParms.settingEnableScheduler) {
//	    			addTaskScheduleQueueTime(mTaskMgrParms,mTimerEventTaskList);
	    			scheduleTimeEventTask(mTimerEventTaskList);
    				rescheduleTimerEventTask(null);
	    		}
    		} 
		}
		if (!mEnvParms.settingEnableScheduler && !mWidgetSvc.isWidgetActive()) {
			//Scheduler not required
			if (mEnvParms.settingScreenKeyguardControlEnabled) {
				mTaskMgrParms.setKeyguardEnabled();
			} else {
				mUtil.addDebugMsg(1,"I","EnableKeyguard ignored, Keyguard control is disabled");
			}
			mTaskMgrParms.pendingRequestForEnableKeyguard=mTaskMgrParms.enableKeyguard=false;
    		mUtil.addDebugMsg(1,"I","Scheduler will be terminated");
			stopSvcMonitor();
			stopSelf();
		}
		relSvcWakeLock();
    	return START_STICKY; //START_STICKY;
    };

    
    @SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	final static private void initialyzeTaskEnvironmentParms(Context c) {
// 		envParms.currentPowerSource=getCurrentPowerSource();
// 		envParms.proximitySensorValue=getCurrentProximityValue();
    	mEnvParms.currentOrientation=mSvcInstance.getResources().getConfiguration().orientation;
    	
		ArrayList<BluetoothDeviceListItem>bdl=CommonUtilities.loadSavedBluetoothConnectedDeviceListAddr(mContext);
    	if (BluetoothAdapter.getDefaultAdapter()!=null) {
 			mEnvParms.bluetoothIsAvailable=true;
 	 		if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
 	 			mEnvParms.bluetoothIsActive=true;
 	 			mEnvParms.setBluetoothConnectedDeviceList(bdl);
 	 			if (bdl.size()>0) {
// 	 	 			mEnvParms.blutoothLastEventDeviceName=bdl.get(bdl.size()-1).btName;
// 	 	 			mEnvParms.blutoothLastEventDeviceAddr=bdl.get(bdl.size()-1).btAddr;
 	 	 			mEnvParms.blutoothConnectedDeviceName=bdl.get(bdl.size()-1).btName;
 	 	 			mEnvParms.blutoothConnectedDeviceAddr=bdl.get(bdl.size()-1).btAddr;
 	 			}
 	 		} else {
 	    		mEnvParms.bluetoothIsActive=false;
 	 			mUtil.clearSavedBluetoothConnectedDeviceList();
// 	 			mEnvParms.blutoothLastEventDeviceName="";
// 	 			mEnvParms.blutoothLastEventDeviceAddr="";
 	 			mEnvParms.blutoothConnectedDeviceName="";
 	 			mEnvParms.blutoothConnectedDeviceAddr="";
 	 		}
    	} else {
    		mEnvParms.bluetoothIsAvailable=false;
    		mEnvParms.bluetoothIsActive=false;
    	}
		if (mWifiMgr!=null) {
			mEnvParms.wifiIsActive=mWifiMgr.isWifiEnabled();
			String tssid=mWifiMgr.getConnectionInfo().getSSID();
			String tmac=mWifiMgr.getConnectionInfo().getMacAddress();
			String wssid="";
			if (tssid==null || tssid.equals("<unknown ssid>")) wssid="";
			else wssid=tssid.replaceAll("\"", "");
			if (wssid.equals("0x")) wssid="";
			mEnvParms.wifiConnectedSsidName=wssid;
			mEnvParms.wifiConnectedSsidAddr=tmac;
//			if (wm.getConnectionInfo().getSSID()!=null) {
//				if (wm.getConnectionInfo().getSSID().startsWith("<unknown ssid>")) mEnvParms.wifiSsid="";
//				else mEnvParms.wifiSsid=wm.getConnectionInfo().getSSID().replaceAll("\"", "");
//			} else mEnvParms.wifiSsid="";
//			if (mEnvParms.wifiIsActive) {
//				mEnvParms.wifiSsid=mUtil.getSavedWifiSsidName();
//				mEnvParms.wifiMacAddr=mUtil.getSavedWifiSsidAddr();
//			} else {
//				mEnvParms.wifiSsid="";
//				mEnvParms.wifiMacAddr="";
//			}
		}

		mEnvParms.screenIsLocked=mUtil.isKeyguardEffective();
		mEnvParms.screenIsOn=CommonUtilities.isScreenOn(mContext);

 		mSensorLight=mUtil.isLightSensorAvailable();
 		if (mSensorLight!=null) mEnvParms.lightSensorAvailable=true;
 		mSensorProximity=mUtil.isProximitySensorAvailable();
 		if (mSensorProximity!=null) mEnvParms.proximitySensorAvailable=true;
 		mSensorMagneticField=mUtil.isMagneticFieldSensorAvailable();
 		if (mSensorMagneticField!=null) mEnvParms.magneticFieldSensorAvailable=true;
 		mSensorAccelerometer=mUtil.isAccelerometerSensorAvailable();
 		if (mSensorAccelerometer!=null) mEnvParms.accelerometerSensorAvailable=true;
 		
 		mEnvParms.currentRingerMode=mAudioManager.getRingerMode();
 		
 		TelephonyManager tm = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
		mEnvParms.telephonyStatus=tm.getCallState();
 		
 		mEnvParms.airplane_mode_on=getAirplaneModeOn();
    	mEnvParms.quickTaskVersion=mUtil.getPrefMgr().getString(QUICK_TASK_VERSION_KEY, "unknown");
    	
      	ConnectivityManager cm = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] ni_array=cm.getAllNetworkInfo();
        if( ni_array != null ){
         	for (int i=0;i<ni_array.length;i++) {
         		mUtil.addDebugMsg(1,"I","Network Type="+ni_array[i].getTypeName()+
         				", SubType="+ni_array[i].getSubtypeName()+
         				", State="+ni_array[i].getState().toString()+
         				", ExtraInfo="+ni_array[i].getExtraInfo());
         		if (ni_array[i].getType()==ConnectivityManager.TYPE_MOBILE||
         				ni_array[i].getType()==ConnectivityManager.TYPE_MOBILE_DUN||
         				ni_array[i].getType()==ConnectivityManager.TYPE_MOBILE_HIPRI||
         				ni_array[i].getType()==ConnectivityManager.TYPE_MOBILE_MMS||
         				ni_array[i].getType()==ConnectivityManager.TYPE_MOBILE_SUPL) {
         			mEnvParms.telephonyIsAvailable=true;
         			break;
         		}
        	}
              
        }
        NetworkInfo ni=cm.getActiveNetworkInfo();
        if (ni!=null) {
        	if (ni.isConnected() && ni.getType()==ConnectivityManager.TYPE_MOBILE) {
                mEnvParms.mobileNetworkIsConnected=true;
        	}
        } else {
            mEnvParms.mobileNetworkIsConnected=false;
        }
        
//        buildTaskScheduleThread(mTaskMgrParms);
        
//        mUtil.addLogMsg("I","locked="+mTaskMgrParms.keyguardManager.isDeviceLocked());
//        mUtil.addLogMsg("I","kg locked="+mTaskMgrParms.keyguardManager.isKeyguardLocked());
//        mUtil.addLogMsg("I","secured="+mTaskMgrParms.keyguardManager.isKeyguardSecure());

        mUtil.addLogMsg("I","EnvironmentParameters initialized");
		mUtil.addLogMsg("I","    Airplane mode on="+mEnvParms.airplane_mode_on);
		mUtil.addLogMsg("I","    Ringer mode="+mEnvParms.currentRingerMode);
		mUtil.addLogMsg("I","    Telephony is available="+mEnvParms.telephonyIsAvailable);
		mUtil.addLogMsg("I","    Telephony status="+mEnvParms.telephonyStatus);
		mUtil.addLogMsg("I","    Mobile network is connected="+mEnvParms.mobileNetworkIsConnected);
		mUtil.addLogMsg("I","    Bluetooth active="+mEnvParms.bluetoothIsActive);
		if (bdl.size()>0) {
			for(int i=0;i<bdl.size();i++) {
				mUtil.addLogMsg("I","    Bluetooth device name="+bdl.get(i).btName+", addr="+bdl.get(i).btAddr);
			}
		} else {
			mUtil.addLogMsg("I","    Bluetooth device not connected");
		}
//		mUtil.addLogMsg("I","    Bluetooth Last event device name="+mEnvParms.blutoothLastEventDeviceName+
//				", addr="+mEnvParms.blutoothLastEventDeviceAddr);
		mUtil.addLogMsg("I","    Wifi active="+mEnvParms.wifiIsActive);
		mUtil.addLogMsg("I","    Wifi SSID="+mEnvParms.wifiConnectedSsidName);
		mUtil.addLogMsg("I","    Screen locked="+mEnvParms.screenIsLocked);
		mUtil.addLogMsg("I","    Light sensor available="+mEnvParms.lightSensorAvailable);
		mUtil.addLogMsg("I","    Proxiity sensor available="+mEnvParms.proximitySensorAvailable);
		mUtil.addLogMsg("I","    Accelerometer sensor available="+mEnvParms.accelerometerSensorAvailable);
		mUtil.addLogMsg("I","    Magnetic-Field sensor available="+mEnvParms.magneticFieldSensorAvailable);
		mUtil.addLogMsg("I","    QuickTaskVersion="+mEnvParms.quickTaskVersion);
    };
    
	static final private synchronized void resourceCleanup(final TaskManagerParms taskMgrParms,
    		final EnvironmentParms envParms, final CommonUtilities util) {
		mUtil.flushLog();
		final long c_time=System.currentTimeMillis();
    	if (taskMgrParms.resourceCleanupTime<=c_time) {
    		final WakeLock wl=((PowerManager)mContext.getSystemService(Context.POWER_SERVICE))
        			.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
        					| PowerManager.ON_AFTER_RELEASE, "TaskAutomation-executeBuiltinEventTask");
    		wl.acquire();
    		taskMgrParms.resourceCleanupTime=c_time+RESOURCE_CLEANUP_INTERVAL;
    		Thread th=new Thread() {
    			@SuppressWarnings("unchecked")
				@Override
    			public void run() {
    				TaskManager.acqLock(TaskManager.LOCK_ID_PROFILE_LIST, TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms,util);
    				TaskManager.acqLock(TaskManager.LOCK_ID_EVENT_TASK_LIST, TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms,util);
    				TaskManager.acqLock(TaskManager.LOCK_ID_TIMER_TASK_LIST, TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms,util);

    				TaskManager.acqLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms, util);
		    		if (taskMgrParms.activeTaskList.size()==0 && taskMgrParms.taskQueueList.size()==0) {
	    				util.addDebugMsg(1, "I", "Resource cleanup started");
						if (envParms.statsHighTaskCountThisPeriod<envParms.settingMaxTaskCount) {
							if (envParms.statsHighTaskCountThisPeriod>envParms.statsHighTaskCountWoMaxTask)
								envParms.statsHighTaskCountWoMaxTask=envParms.statsHighTaskCountThisPeriod;
						}
						envParms.statsHighTaskCountThisPeriod=0;
		    	    	taskMgrParms.activeTaskList=TaskManager.buildActiveTaskList();
		    	    	taskMgrParms.blockActionList=TaskManager.buildBlockActionList();
		    	 		taskMgrParms.wifiNotifyEventList=new ArrayList<ThreadCtrl>();
		    	 		taskMgrParms.bluetoothNotifyEventList=new ArrayList<ThreadCtrl>();
		    	    	taskMgrParms.taskQueueList=TaskManager.buildTaskQueueList();
		    	    	
		    	    	LinkedList<TaskHistoryItem> t_hist=TaskManager.buildTaskHistoryList(taskMgrParms);
		    	    	t_hist=(LinkedList<TaskHistoryItem>) taskMgrParms.taskHistoryList.clone();
		    	    	taskMgrParms.taskHistoryList=t_hist;

		    	    	TaskManager.removeTaskExecThreadPool(envParms, taskMgrParms, util);
		    	    	TaskManager.buildTaskExecThreadPool(envParms, taskMgrParms, util);

		    	    	TaskManager.removeTaskCtrlThreadPool(envParms, taskMgrParms, util);
		    	    	TaskManager.buildTaskCtrlThreadPool(envParms, taskMgrParms, util);

//		    	    	TaskManager.removeHighTaskCtrlThreadPool(envParms, taskMgrParms, util);
//		    	    	TaskManager.buildHighTaskCtrlThreadPool(envParms, taskMgrParms, util);
		    	    	taskMgrParms.highTaskControlThreadPool.purge();

		    	    	taskMgrParms.locationUtil.deactivateLocationProvider();
//		    	    	removeTaskScheduleThread(taskMgrParms);
//		    	    	buildTaskScheduleThread(taskMgrParms);
		    	    	System.gc();
		    	    	int bsh_method_cnt=0;
		    	    	synchronized(taskMgrParms.bshExecEnvList) {
			    	    	bsh_method_cnt=taskMgrParms.bshExecEnvList.size();
		    	    		TaskManager.buildBshEnvironmentList(taskMgrParms, envParms, util);
		    	    	}
	            		System.gc();
						util.addLogMsg("I", "Resource cleanup ended.",
									" High concurrent task=",String.valueOf(envParms.statsHighTaskCountWoMaxTask),
									", Maximum number of concurrent task reached=",String.valueOf(envParms.statsMaxTaskReacheCount)
									,", Outside TaskCtrl="+envParms.statsUseOutsideThreadPoolCountTaskCtrl
									, ", Outsize TaskExec="+envParms.statsUseOutsideThreadPoolCountTaskExec
									, ", Bsh method count="+bsh_method_cnt);
//						envParms.statsHighTaskCountWoMaxTask=envParms.statsMaxTaskReacheCount=
//								envParms.statsUseOutsideThreadPoolCountTaskCtrl=
//								envParms.statsUseOutsideThreadPoolCountTaskExec=0;
					}
		    		TaskManager.relLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms, util);
					
		    		TaskManager.relLock(TaskManager.LOCK_ID_TIMER_TASK_LIST, TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms,util);
		    		TaskManager.relLock(TaskManager.LOCK_ID_EVENT_TASK_LIST, TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms,util);
		    		TaskManager.relLock(TaskManager.LOCK_ID_PROFILE_LIST, TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms,util);
	    			wl.release();
    			}
    		};
    		th.setPriority(Thread.MAX_PRIORITY);
    		th.start();
    	}	
    };

    @SuppressWarnings("deprecation")
	final static public int getAirplaneModeOn() {
    	int result=0;
       	result=Settings.System.getInt(mContext.getContentResolver(),
   	           Settings.System.AIRPLANE_MODE_ON, 0);
    	return result;
    }
    
    final static private void initialyzeThreadResponseNotifyEvent(Context c) {
    	mTaskMgrParms.threadReponseNotify = new NotifyEvent(c);
    	mTaskMgrParms.threadReponseNotify.setListener(new NotifyEventListener() {
			@Override
			public void positiveResponse(Context c, Object[] o) {
				TaskResponse tr=(TaskResponse)o[0];
				processThreadPositiveResponse(tr.active_thread_ctrl, c, tr);
			}

			@Override
			public void negativeResponse(Context c, Object[] o) {
				TaskResponse tr=(TaskResponse)o[0];
				processThreadNegativeResponse(tr.active_thread_ctrl, c, tr);
			}
    	});
    };

    private static void initialExecuteSchedulerTask(Context c){
   		mUtil.addDebugMsg(1,"I","Scheduler initial execution was started");
   		TaskManager.showNotification(mTaskMgrParms, mEnvParms, mUtil);
 		mSvcInstance.startForeground(R.string.app_name,mTaskMgrParms.mainNotification);
		stopLightSensorReceiver();
		stopProximitySensorReceiver();
		startLightSensorReceiver();
		startProximitySensorReceiver();

		rescheduleTimerEventTask(null); 		

     	if (BluetoothAdapter.getDefaultAdapter()!=null) {
 			if (mEnvParms.isBluetoothConnected()) {
 				scheduleBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_BLUETOOTH_CONNECTED); 
 			} else if (mEnvParms.bluetoothIsActive) {
 				scheduleBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_BLUETOOTH_ON);
 			}
 		}
 		if (mEnvParms.isWifiConnected()) scheduleBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_WIFI_CONNECTED);	
 		else if (mEnvParms.wifiIsActive) scheduleBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_WIFI_ON);
    };

    final static private void buildTaskList() {
		TaskManager.acqLock(TaskManager.LOCK_ID_PROFILE_LIST, TaskManager.LOCK_MODE_WRITE,mEnvParms,mTaskMgrParms,mUtil);
		TaskManager.acqLock(TaskManager.LOCK_ID_EVENT_TASK_LIST, TaskManager.LOCK_MODE_WRITE,mEnvParms,mTaskMgrParms,mUtil);
		TaskManager.acqLock(TaskManager.LOCK_ID_TIMER_TASK_LIST, TaskManager.LOCK_MODE_WRITE,mEnvParms,mTaskMgrParms,mUtil);

		if (!mUtil.getPrefMgr().getString(QUICK_TASK_VERSION_KEY, QUICK_TASK_CURRENT_VERSION).equals(QUICK_TASK_CURRENT_VERSION)) {
			boolean qta=ProfileUtilities.isQuickTaskProfileActivated(mUtil,mProfileArrayList);
			ProfileUtilities.deleteProfileGroup(mUtil,mProfileArrayList,QUICK_TASK_GROUP_NAME);
			QuickTaskMaintenance.buildQuickTaskProfile(mContext,mProfileArrayList,mUtil,QUICK_TASK_GROUP_NAME);
			ProfileUtilities.sortProfileArrayList(mUtil, mProfileArrayList);
			ProfileUtilities.setQuickTaskProfileActivated(mUtil,mProfileArrayList,qta);
			mEnvParms.quickTaskVersion=QUICK_TASK_CURRENT_VERSION;
			Runnable r=new Runnable(){
				@Override
				public void run() {
					mUtil.saveProfileToFileByService(mProfileArrayList);
				}
			};
			mTaskMgrParms.normalTaskControlThreadPool.execute(r);
		}

		TaskManager.acqLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE,mEnvParms,mTaskMgrParms, mUtil);
		mRequiredBatteryLevelExecution=false;
		mRequiredSensorLight=mRequiredSensorProximity=false;
		if (mTimerEventTaskList.size()!=0 || mBuiltinEventTaskList.size()!=0) {
	 		ArrayList<TaskListItem> task_list_timer=new ArrayList<TaskListItem>();
			ArrayList<TaskListItem> task_list_builtin=new ArrayList<TaskListItem>();
			ArrayList<TaskListItem> task_list_task=new ArrayList<TaskListItem>();
	 		buildTimeEventTaskList(mProfileArrayList,task_list_timer);
	 		buildEventTaskList(mProfileArrayList,task_list_builtin,task_list_task);
	 		ArrayList<TaskListItem> cancel_task_list=new ArrayList<TaskListItem>();
	 		ArrayList<TaskListItem> cancel_valid_grp_list=new ArrayList<TaskListItem>();
	 		buildCancelTaskList(cancel_valid_grp_list,cancel_task_list,task_list_builtin);
	 		buildCancelTaskList(cancel_valid_grp_list,cancel_task_list,task_list_timer);
	 		buildCancelTaskList(cancel_valid_grp_list,cancel_task_list,task_list_task);
	 		cancelTaskByTaskList(cancel_task_list);
	 		cancelTaskByGroupList(cancel_valid_grp_list);

	 		mEnvParms.taskListBuildTime=System.currentTimeMillis();
	 		mEnvParms.taskListBuildTimeString=StringUtil.convDateTimeTo_MonthDayHourMin(mEnvParms.taskListBuildTime);

	 		mTimerEventTaskList=task_list_timer;
	 		mBuiltinEventTaskList=task_list_builtin;
	 		mTaskEventTaskList=task_list_task;
		} else {
			TaskManager.cancelAllActiveTask(mTaskMgrParms,mEnvParms,mUtil);

			buildTimeEventTaskList(mProfileArrayList,mTimerEventTaskList);
	 		buildEventTaskList(mProfileArrayList,mBuiltinEventTaskList,mTaskEventTaskList);
	 		mEnvParms.taskListBuildTime=System.currentTimeMillis();
	 		mEnvParms.taskListBuildTimeString=StringUtil.convDateTimeTo_MonthDayHourMin(mEnvParms.taskListBuildTime);
		}
 		buildTaskListLookupTable();
 		
 		saveTaskList();
 		
 		TaskManager.relLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE,mEnvParms,mTaskMgrParms, mUtil);

 		TaskManager.relLock(TaskManager.LOCK_ID_TIMER_TASK_LIST, TaskManager.LOCK_MODE_WRITE,mEnvParms,mTaskMgrParms,mUtil);
		TaskManager.relLock(TaskManager.LOCK_ID_EVENT_TASK_LIST, TaskManager.LOCK_MODE_WRITE,mEnvParms,mTaskMgrParms,mUtil);
		TaskManager.relLock(TaskManager.LOCK_ID_PROFILE_LIST, TaskManager.LOCK_MODE_WRITE,mEnvParms,mTaskMgrParms,mUtil);
    };

    final static private void buildTaskListLookupTable() {
		mBuiltinEventTaskLookupTable=new ArrayList<TaskLookupListItem> ();
		for (int i=0;i<mBuiltinEventTaskList.size();i++) {
			TaskListItem bietl=mBuiltinEventTaskList.get(i);
			boolean found=false;
			for (int j=0;j<mBuiltinEventTaskLookupTable.size();j++) {
				TaskLookupListItem tlui=mBuiltinEventTaskLookupTable.get(j);
				if (bietl.event_name.equals(tlui.event_name)) {
					if (tlui.start_pos>i) tlui.start_pos=i;
					if (tlui.end_pos<i) tlui.end_pos=i;
					found=true;
					break;
				}
			}
			if (!found) {
				TaskLookupListItem tlui=new TaskLookupListItem();
				tlui.event_name=bietl.event_name;
				tlui.start_pos=i;
				tlui.end_pos=i;
				mBuiltinEventTaskLookupTable.add(tlui);
			}
		};
		for (int i=0;i<mBuiltinEventTaskLookupTable.size();i++) {
			TaskLookupListItem tlui=mBuiltinEventTaskLookupTable.get(i);
			mUtil.addDebugMsg(2, "I", "TaskListLookupTable pos=",String.valueOf(i),
					", event=",tlui.event_name, ", s_pos=",String.valueOf(tlui.start_pos),
					", e_pos=",String.valueOf(tlui.end_pos));
		}
		mUtil.addDebugMsg(1, "I", "TaskListLookupTable created size=",
				String.valueOf(mBuiltinEventTaskLookupTable.size()));
		
    };
    
    final static private void buildCancelTaskList(
    		ArrayList<TaskListItem> cancel_valid_grp_list,
    		ArrayList<TaskListItem> cancel_task_list, ArrayList<TaskListItem> task_list) {
//		activeTaskList
    	for (int i_task=0;i_task<task_list.size();i_task++) {
 			boolean task_list_update_required=false;
 			TaskListItem ti=task_list.get(i_task);
 			if (ti.profile_update_time<=mEnvParms.taskListBuildTime) {
 				ArrayList<TaskActionItem> tal=ti.taskActionList;
 				for (int i_act=0;i_act<tal.size();i_act++) {
 					if (tal.get(i_act).profile_update_time>mEnvParms.taskListBuildTime) {
 						task_list_update_required=true;
 						break;
 					}
 				}
 			} else task_list_update_required=true;
 			if (task_list_update_required) {
				boolean found=false;
 				for (int i=0;i<cancel_task_list.size();i++) {
 					TaskListItem ati=cancel_task_list.get(i);
 					if (ati.group_name.equals(ti.group_name) && ati.task_name.equals(ti.task_name)) {
 						found=true;
 						break;
 					}
 				}
 				if (!found) {
 					TaskListItem c_ati=
 							TaskManager.getActiveTaskListItem(mTaskMgrParms, mEnvParms, mUtil, ti.group_name, ti.task_name);
 					if (c_ati!=null) {
 		 				TaskListItem a_ati=new TaskListItem();
 		 				mUtil.addDebugMsg(2, "I", "buildCancelTaskList added cancel task: group="+ti.group_name+", task="+ti.task_name);
 		 				a_ati.group_name=ti.group_name;
 		 				a_ati.task_name=ti.task_name;
 		 				cancel_task_list.add(a_ati);
 					}
 				}
 			}
 		};
//		taskQueueList 		
 		for (int i_task=0;i_task<task_list.size();i_task++) {
 			TaskListItem ti=task_list.get(i_task);
 			boolean found=false;
 			for (int i=0;i<cancel_valid_grp_list.size();i++) {
 				if (cancel_valid_grp_list.get(i).group_name.equals(ti.group_name)) {
 					found=true;
 					break;
 				}
 			}
 			if (!found) {
				TaskListItem c_ati=
 							TaskManager.getActiveTaskListItem(mTaskMgrParms, mEnvParms, mUtil, ti.group_name, ti.task_name);
				if (c_ati!=null) {
	 				TaskListItem a_ati=new TaskListItem();
	 				mUtil.addDebugMsg(2, "I", "buildCancelTaskList added valid group: group="+ti.group_name);
	 				a_ati.group_name=ti.group_name;
	 				cancel_valid_grp_list.add(a_ati);
				}
 			}
 		}
    };
    
    final static void cancelSoundPlayBackTask() {
    	if (mTaskMgrParms.soundPlayBackTaskList.size()>0) {
    		for (int i=0;i<mTaskMgrParms.soundPlayBackTaskList.size();i++) {
    			TaskResponse tr=mTaskMgrParms.soundPlayBackTaskList.get(i);
    			TaskManager.cancelSpecificTask(mTaskMgrParms,mEnvParms,mUtil,
    					tr.active_group_name,tr.active_task_name);
    		}
    	}
    };
    
    final static private void cancelTaskByTaskList(ArrayList<TaskListItem> task_cancel_list) {
 		for (int i_task=0;i_task<task_cancel_list.size();i_task++) {
			TaskManager.cancelSpecificTask(mTaskMgrParms,mEnvParms,mUtil,
					task_cancel_list.get(i_task).group_name,task_cancel_list.get(i_task).task_name);

 		}
    };

    final static private void cancelTaskByGroupList(
    		ArrayList<TaskListItem> cancel_valid_grp_list) {
			ArrayList<TaskListItem> c_g_list=new ArrayList<TaskListItem>();
			
		if (mTaskMgrParms.activeTaskList.size()!=0) {
			for (int i_at=0;i_at<mTaskMgrParms.activeTaskList.size();i_at++) {
				TaskListItem ati=mTaskMgrParms.activeTaskList.get(i_at);
				boolean found=false;
				for (int i_cg=0;i_cg<cancel_valid_grp_list.size();i_cg++) {
					if (cancel_valid_grp_list.get(i_cg).group_name.equals(ati.group_name)) {
						found=true;
						break;
					}
				}
				if (!found) {
					mUtil.addDebugMsg(2, "I", "cancelTaskByGroupList cancel task: group="+ati.group_name+", task="+ati.task_name);
					c_g_list.add(ati);
				}
			}
		}
		
		if (mTaskMgrParms.taskQueueList.size()!=0) {
			for (int i_tq=0;i_tq<mTaskMgrParms.taskQueueList.size();i_tq++) {
				TaskListItem ti=mTaskMgrParms.taskQueueList.get(i_tq);
				boolean found=false;
				for (int i_cg=0;i_cg<cancel_valid_grp_list.size();i_cg++) {
					if (cancel_valid_grp_list.get(i_cg).group_name.equals(ti.group_name)) {
						found=true;
						break;
					}
				}
				if (!found) {
	 				TaskListItem a_ati=new TaskListItem();
	 				a_ati.group_name=ti.group_name;
	 				a_ati.task_name=ti.task_name;
	 				mUtil.addDebugMsg(2, "I", "cancelTaskByGroupList cancel group: group="+a_ati.group_name+", task="+a_ati.task_name);
	 				c_g_list.add(a_ati);
				}
			}
		}
		
 		for (int i=0;i<c_g_list.size();i++) {
			TaskManager.cancelSpecificTask(mTaskMgrParms,mEnvParms,mUtil,
					c_g_list.get(i).group_name,c_g_list.get(i).task_name);
 		}
    };
    
    final static private void saveTaskList() {
    	TaskListHolder tlh=new TaskListHolder();
    	tlh.lookup_list=mBuiltinEventTaskLookupTable;
    	tlh.profile_array_list=mProfileArrayList;
    	tlh.builtin_task_list=mBuiltinEventTaskList;
    	tlh.timer_task_list=mTimerEventTaskList;
    	tlh.task_task_list=mTaskEventTaskList;
		tlh.req_bl=mRequiredBatteryLevelExecution;
		tlh.req_light=mRequiredSensorLight;
		tlh.req_acc=mRequiredSensorAccelerometer;
		tlh.req_mag=mRequiredSensorMagneticField;
		tlh.req_prx=mRequiredSensorProximity;
		tlh.build_time=mEnvParms.taskListBuildTime;
		try {
		    FileOutputStream fos=mContext.openFileOutput(SERVICE_TASK_LIST_FILE_NAME, MODE_PRIVATE);
		    BufferedOutputStream bos=new BufferedOutputStream(fos,
		    		GENERAL_FILE_BUFFER_SIZE);
		    ObjectOutputStream oos = new ObjectOutputStream(bos);
		    tlh.writeExternal(oos);
		    oos.close();
		    mUtil.addDebugMsg(1,"I", "Task list was saved, build time=",
		    		StringUtil.convDateTimeTo_YearMonthDayHourMin(mEnvParms.taskListBuildTime));
		} catch (Exception e) {
			e.printStackTrace();
			mUtil.addDebugMsg(1,"E", "saveTaskList error, ",e.getMessage());
		}
    };
     
    final static private void deleteTaskList() {
    	mContext.deleteFile(SERVICE_TASK_LIST_FILE_NAME);
    };
    
    final static private void restoreTaskList() {
		if (mEnvParms.quickTaskVersion.equals(QUICK_TASK_CURRENT_VERSION)) {
			try {
			    FileInputStream fis=mContext.openFileInput(SERVICE_TASK_LIST_FILE_NAME);
			    BufferedInputStream bis=new BufferedInputStream(fis,
			    		GENERAL_FILE_BUFFER_SIZE);
			    ObjectInputStream ois = new ObjectInputStream(bis);
			    TaskListHolder tlh= new TaskListHolder();
			    tlh.readExternal(ois);
			    mProfileArrayList=tlh.profile_array_list;
			    mBuiltinEventTaskList=tlh.builtin_task_list;
			    mTimerEventTaskList=tlh.timer_task_list;
			    mTaskEventTaskList=tlh.task_task_list;
			    mBuiltinEventTaskLookupTable=tlh.lookup_list;
				mRequiredBatteryLevelExecution=tlh.req_bl;
				mRequiredSensorLight=tlh.req_light;
				mRequiredSensorProximity=tlh.req_prx;
				mRequiredSensorAccelerometer=tlh.req_acc;
				mRequiredSensorMagneticField=tlh.req_mag;
				mEnvParms.taskListBuildTime=tlh.build_time;
				mEnvParms.taskListBuildTimeString=StringUtil.convDateTimeTo_MonthDayHourMin(mEnvParms.taskListBuildTime);
				for (int i=0;i<mTimerEventTaskList.size();i++) {
					mTimerEventTaskList.get(i).timer_update_required=true;
				}
//			 		buildTaskListLookupTable();
			    mUtil.addDebugMsg(1,"I", "Saved task list was restored, build time=",
			    		StringUtil.convDateTimeTo_YearMonthDayHourMin(mEnvParms.taskListBuildTime));
			    ois.close();
			} catch (Exception e) {
				e.printStackTrace();
				mUtil.addDebugMsg(1,"E", "restoreTaskList error, ",e.getMessage());
				mUtil.addDebugMsg(1,"E", "Rebuild task list has been started");
				mProfileArrayList=new ArrayList<ProfileListItem>();
				mBuiltinEventTaskList=new ArrayList<TaskListItem>();
				mTimerEventTaskList=new ArrayList<TaskListItem>();
				mTaskEventTaskList=new ArrayList<TaskListItem>();
	    		mProfileArrayList=buildProfileList();
				buildTaskList();
			}
		} else {
			mUtil.addDebugMsg(1,"E", "Rebuild task list has been started(QuickTask version different)");
			mProfileArrayList=new ArrayList<ProfileListItem>();
			mBuiltinEventTaskList=new ArrayList<TaskListItem>();
			mTimerEventTaskList=new ArrayList<TaskListItem>();
			mTaskEventTaskList=new ArrayList<TaskListItem>();
    		mProfileArrayList=buildProfileList();
			buildTaskList();
			
		}
    };
     
    final static private void startSvcMonitor() {
		if (mEnvParms.settingEnableMonitor) {
	    	mUtil.addDebugMsg(1,"I", "startSvcMonitor Monitor has been started");
			Intent intent = new Intent(mContext, SchedulerMonitor.class);
			intent.setAction("Create-Monitor");
			mContext.startService(intent);

			if (mSvcMonitorConnection==null) setMonitorServiceConnListener();
			if (mSchedulerMonitor==null) {
				intent = new Intent(mContext, SchedulerMonitor.class);
				intent.setAction("Bind-Monitor");
				mContext.bindService(intent, mSvcMonitorConnection, BIND_AUTO_CREATE);
			}
		} else {
	    	if (mEnvParms.settingDebugLevel!=0) 
	    		mUtil.addDebugMsg(1,"I", "startSvcMonitor Monitor was not started");
		}
    };

    final static private void refreshSvcMonitor() {
//    	if (envParms.settingDebugLevel!=0) util.addDebugMsg(1,"I", "refreshSvcMonitor entered");
		if (mEnvParms.settingEnableMonitor) {
	    	mUtil.addDebugMsg(1,"I", "refreshSvcMonitor Refresh monitor has been started");
			Intent intent = new Intent(mContext, SchedulerMonitor.class);
			intent.setAction("Refresh-Monitor");
			mContext.startService(intent);
		} else{
	    	mUtil.addDebugMsg(1,"I", "refreshSvcMonitor Monitor was not refreshed");
		}
    };

    final static private void setMonitorServiceConnListener() {
        mSvcMonitorConnection = new ServiceConnection(){
    		public void onServiceConnected(ComponentName name, IBinder service) {
    			mUtil.addDebugMsg(1, "I", "Monitor service was connected");
    			mSchedulerMonitor = ISchedulerMonitor.Stub.asInterface(service);
    		}
    		public void onServiceDisconnected(ComponentName name) {
    			WakeLock wl= ((PowerManager)mContext.getSystemService(Context.POWER_SERVICE))
    	    			.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
    	        				| PowerManager.ON_AFTER_RELEASE, "StartMon");
    			try {
        			wl.acquire(100);
        			mUtil.addDebugMsg(1, "I", "Monitor service was disconnected");
        			mSchedulerMonitor = null;
        			if (mEnvParms.settingEnableMonitor) {
        				mUtil.addLogMsg("W", mTaskMgrParms.svcMsgs.msgs_main_monitor_issued);
        				mUtil.addDebugMsg(1, "I", "Bind service was issued.");

        				Intent intent = new Intent(mContext, SchedulerMonitor.class);
        				intent.setAction("Service-Restart");
//        				bindService(intent, svcConnMonitor, BIND_AUTO_CREATE);
        				mContext.startService(intent);
        			} else {
        				mUtil.addLogMsg("W", mTaskMgrParms.svcMsgs.msgs_main_monitor_ignored);
        			}
    			} finally {
//        			wl.release();
    			}
    		}
    	};
    }; 
    
    final static private void stopSvcMonitor() {
    	if (mSchedulerMonitor!=null && mSvcMonitorConnection!=null) {
	    	mUtil.addDebugMsg(1,"I", "stopSvcMonitor Monitor has been stopped");
    		mContext.unbindService(mSvcMonitorConnection);
    		mSvcMonitorConnection=null;
    		mSchedulerMonitor=null;
    	}
		Intent intent = new Intent(mContext, SchedulerMonitor.class);
		intent.setAction("Service-Stop");
		mContext.stopService(intent);
    };

	@Override
    public IBinder onBind(Intent in) {
		String action="";
		if (in!=null && in.getAction()!=null) action=in.getAction();
		mUtil.addDebugMsg(1,"I","onBind entered, action=",action);
		return mSvcSchedulerClient;
    };

	@Override
	public boolean onUnbind(Intent in) {
		mUtil.addDebugMsg(1,"I","onUnBind entered, action=",in.getAction());
    	if (!mEnvParms.settingEnableScheduler) {
    		if (!mWidgetSvc.isWidgetActive()) {
    			stopSvcMonitor();
    			stopSelf();
    		}
    	}
		return true;
	};

    @Override
    public void onDestroy() {
    	mUtil.addDebugMsg(1,"I","onDestroy enterd");
    	mUtil.addLogMsg("I",mTaskMgrParms.svcMsgs.msgs_svc_termination);
    	stopBasicEventReceiver(mContext);
    	stopProximitySensorReceiver();
        stopLightSensorReceiver();
        
        cancelHeartBeat(mContext);

        mWidgetSvc.removeWidget();
        
    	TaskManager.removeTaskExecThreadPool(mEnvParms,mTaskMgrParms,mUtil);
    	
    	TaskManager.removeTaskCtrlThreadPool(mEnvParms,mTaskMgrParms,mUtil);
    	
    	TaskManager.removeHighTaskCtrlThreadPool(mEnvParms,mTaskMgrParms,mUtil);

//    	removeTaskScheduleThread(mTaskMgrParms);
    	
    	if (mSleepDetectorTc!=null) mSleepDetectorTc.setDisabled();
    	
    	TaskManager.cancelNotification(mTaskMgrParms);

    	mUtil.resetLogReceiver();
    	
//        util=null;

        if (mEnvParms.settingExitClean) {
			System.gc();
			new Handler().postDelayed(new Runnable(){
				@Override
				public void run() {
					android.os.Process.killProcess(android.os.Process.myPid());
				}
				
			}, 100);
        }
    };
    
    final static private ISchedulerClient.Stub mSvcSchedulerClient = 
			new ISchedulerClient.Stub() {
		final public void setCallBack(final ISchedulerCallback callback)
				throws RemoteException {
			mUtil.addDebugMsg(2,"I","setCallBack entered");
			mTaskMgrParms.callBackList.register(callback);
		};
		
		final public void removeCallBack(ISchedulerCallback callback)
				throws RemoteException {
			mUtil.addDebugMsg(2,"I","removeCallBack entered");
			mTaskMgrParms.callBackList.unregister(callback);
		};

        @Override 
        final public void aidlCancelAllActiveTask() throws RemoteException {
        	mUtil.addDebugMsg(2,"I", "aidlCancelAllActiveTask entered, cnt=",
        			String.valueOf(mTaskMgrParms.activeTaskList.size()));
			TaskManager.acqLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE,mEnvParms,mTaskMgrParms, mUtil);
        	TaskManager.cancelAllActiveTask(mTaskMgrParms,mEnvParms,mUtil);
			TaskManager.relLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE,mEnvParms,mTaskMgrParms, mUtil);
            return ;
        };

        @Override 
        final public void aidlCancelSpecificTask(String grp, String task_name) 
        		throws RemoteException {
        	mUtil.addDebugMsg(2,"I","aidlCancelSpecifiTask entered, cnt=",
        			String.valueOf(mTaskMgrParms.activeTaskList.size()));
			TaskManager.acqLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE,mEnvParms,mTaskMgrParms, mUtil);
        	TaskManager.cancelSpecificTask(mTaskMgrParms,mEnvParms,mUtil,grp,task_name);
			TaskManager.relLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE,mEnvParms,mTaskMgrParms, mUtil);
            return ;
        };
        
		@Override
		final public String[] aidlGetActiveTaskList() throws RemoteException {
			//event+tab+task+tab+SOUND/NOSOUND+tab+time			
			String[] rv=TaskManager.buildActiveTaskStringArray(mTaskMgrParms,mEnvParms,mUtil);
			int tc=0;
			if (rv!=null) tc=rv.length;
			mUtil.addDebugMsg(2,"I", "aidlGetActiveTask result=",String.valueOf(tc));
			return rv;
		};

		@Override
		final public void aidlClearTaskHistory() throws RemoteException {
			TaskManager.clearTaskHistoryList(mTaskMgrParms,mEnvParms,mUtil);
		};

		@Override
		final public void aidlResetScheduler() throws RemoteException {
			resetScheduler();
		};

		@Override
		final public String[] aidlGetTaskHistoryList() throws RemoteException {
			TaskManager.buildTaskHistoryStringArray(mTaskMgrParms, mEnvParms, mUtil);
			return mTaskMgrParms.task_history_string_array; 
		};

		@Override
		final public void aidlScreenOff() throws RemoteException {
			mUtil.addDebugMsg(2,"I", "aidlScreenOff entered");
			lockScreen();
		};

		@Override
		final public void aidlMessageDialogMoveToFront() throws RemoteException {
			mUtil.addDebugMsg(2,"I", "aidlSoundDialogMoveToFront entered");

			Intent in_b=
				new Intent(mContext.getApplicationContext(),ActivityMessage.class);
			in_b.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			in_b.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			mContext.startActivity(in_b);
		};

		@Override
		final public void aidlImmediateTaskExecution(byte[] task,
				byte[] time, byte[] action, byte[] tep) throws RemoteException {
			mUtil.addDebugMsg(1,"I", "Build immediate task exec list started");
			ArrayList<ProfileListItem> prof_task=ProfileUtilities.deSerializeProfilelist(task);
			for (int i=0;i<prof_task.size();i++) {
//				prof_task.get(i).dumpProfile();
				mUtil.addDebugMsg(1,"I", "Task profile added, group=", 
						prof_task.get(i).getProfileGroup(),
						", name=",prof_task.get(i).getProfileName());
			}

			ArrayList<ProfileListItem> prof_time=ProfileUtilities.deSerializeProfilelist(time);
			for (int i=0;i<prof_time.size();i++) {
//				prof_time.get(i).dumpProfile();
				mUtil.addDebugMsg(1,"I", "Time profile added, group=",
						prof_time.get(i).getProfileGroup(),", name=",prof_time.get(i).getProfileName());
			}

			ArrayList<ProfileListItem> prof_action=ProfileUtilities.deSerializeProfilelist(action);
			for (int i=0;i<prof_action.size();i++) {
//				prof_action.get(i).dumpProfile();
				mUtil.addDebugMsg(1,"I", "Action profile added, group=",prof_action.get(i).getProfileGroup(),
						", name=",prof_action.get(i).getProfileName());
			}

			EnvironmentParms testEnvParms=mEnvParms;
			if (tep!=null) {
				testEnvParms=EnvironmentParms.deSerialize(tep);
			}

			ArrayList<ProfileListItem> prof_list=new ArrayList<ProfileListItem>();
			if (prof_task.size()!=0) prof_list.addAll(prof_task);
			if (prof_time.size()!=0) prof_list.addAll(prof_time);
			if (prof_action.size()!=0) prof_list.addAll(prof_action);
			
			mUtil.addDebugMsg(1,"I", "Build immediate task exec list completed");

			mUtil.addDebugMsg(1,"I", "Immediate task execution start has been issued");
			boolean s_light=mRequiredSensorLight;
			boolean s_proximity=mRequiredSensorProximity;
			boolean s_battery=mRequiredBatteryLevelExecution;
			TaskManager.acqLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE, testEnvParms, mTaskMgrParms, mUtil);
			if (prof_task.get(0).getTaskTriggerList().get(0).startsWith(BUILTIN_PREFIX)) {
				ArrayList<TaskListItem> bitl=new ArrayList<TaskListItem>();
				ArrayList<TaskListItem> ttl=new ArrayList<TaskListItem>();
				buildEventTaskList(prof_list,bitl,ttl);
				bitl.get(0).event_name="*Immediate";
				bitl.get(0).task_action_notification=true;
				TaskManager.startTask(mTaskMgrParms,testEnvParms,mUtil,bitl.get(0));
			} else if (prof_task.get(0).getTaskTriggerList().get(0).equals(TRIGGER_EVENT_TASK)) {
				ArrayList<TaskListItem> bitl=new ArrayList<TaskListItem>();
				ArrayList<TaskListItem> ttl=new ArrayList<TaskListItem>();
				buildEventTaskList(prof_list,bitl,ttl);
				ttl.get(0).event_name="*Immediate";
				ttl.get(0).task_action_notification=true;
				TaskManager.startTask(mTaskMgrParms,testEnvParms,mUtil,ttl.get(0));
			} else {
				ArrayList<TaskListItem> tetl=new ArrayList<TaskListItem>();
				buildTimeEventTaskList(prof_list,tetl);
				tetl.get(0).event_name="*Immediate";
				tetl.get(0).task_action_notification=true;
				TaskManager.startTask(mTaskMgrParms,testEnvParms,mUtil,tetl.get(0));
			}
			TaskManager.relLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE, testEnvParms, mTaskMgrParms, mUtil);
//			initiateQueuedTask();
			mRequiredSensorLight=s_light;
			mRequiredSensorProximity=s_proximity;
			mRequiredBatteryLevelExecution=s_battery;
		};
		
		@Override
		final public int aidlGetTaskListCount() {
			int result=0;
			result+=mTimerEventTaskList.size();
			result+=mBuiltinEventTaskList.size();
			return result;
		}

		@Override
		final public void aidlCopyProfileToService(byte[] buf) {
			mUtil.addDebugMsg(2,"I", "aidlCopyProfileToService entered");
			ArrayList<ProfileListItem> prof_list=ProfileUtilities.deSerializeProfilelist(buf);
    		TaskManager.acqLock(TaskManager.LOCK_ID_PROFILE_LIST, TaskManager.LOCK_MODE_WRITE,mEnvParms,mTaskMgrParms,mUtil);
    		mProfileArrayList=prof_list;
		    saveTaskList();
    		TaskManager.relLock(TaskManager.LOCK_ID_PROFILE_LIST, TaskManager.LOCK_MODE_WRITE,mEnvParms,mTaskMgrParms,mUtil);
			mUtil.saveProfileToFileByService(prof_list);
			mUtil.addDebugMsg(2,"I", "aidlCopyProfileToService ended, size=",String.valueOf(prof_list.size()));
		};

		@Override
		final public byte[] aidlCopyProfileFromService() {
			mUtil.addDebugMsg(2,"I", "aidlCopyProfileFromService entered");
    		TaskManager.acqLock(TaskManager.LOCK_ID_PROFILE_LIST, TaskManager.LOCK_MODE_READ,mEnvParms,mTaskMgrParms,mUtil);
    		byte[] buf=ProfileUtilities.serializeProfilelist(mProfileArrayList);
    		TaskManager.relLock(TaskManager.LOCK_ID_PROFILE_LIST, TaskManager.LOCK_MODE_READ,mEnvParms,mTaskMgrParms,mUtil);
    		return buf;
		};

		@Override
		final public byte[] aidlCopyEnvParmsFromService() {
			mUtil.addDebugMsg(2,"I", "aidlCopyEnvParmsFromService entered");
//			return EnvironmentParameters.serialize(envParms);
			return mEnvParms.serialize();
		};

    };

	final static private void resetScheduler() {
		int p_ss=mEnvParms.settingEnableScheduler ? 1 : 0;
		int p_ms=mEnvParms.settingEnableMonitor ? 1 : 0;
		mEnvParms.loadSettingParms(mContext);
		int n_ss=mEnvParms.settingEnableScheduler ? 1 : 0;
		int n_ms=mEnvParms.settingEnableMonitor ? 1 : 0;
		listInitSettingsParm();

		if (p_ms!=n_ms) {
			if (mEnvParms.settingEnableMonitor) startSvcMonitor();
			else stopSvcMonitor();
		} else {
			if (mEnvParms.settingEnableMonitor) {
				refreshSvcMonitor();
			}
		}
		TaskManager.buildNotification(mTaskMgrParms, mEnvParms);
		TaskManager.showNotification(mTaskMgrParms, mEnvParms, mUtil);
		mSvcInstance.startForeground(R.string.app_name,mTaskMgrParms.mainNotification);
    	if (mEnvParms.settingEnableScheduler) {
			stopProximitySensorReceiver();
			stopLightSensorReceiver();
    		if (p_ss!=n_ss) initialExecuteSchedulerTask(mContext);
    		else {
	    		startProximitySensorReceiver();
	    		startLightSensorReceiver();
    		}
		} else {
			stopProximitySensorReceiver();
			stopLightSensorReceiver();
			if (mEnvParms.settingScreenKeyguardControlEnabled) {
				mTaskMgrParms.setKeyguardEnabled();
			} else {
				mUtil.addDebugMsg(1,"I","EnableKeyguard ignored, Keyguard control is disabled");
			}
			mTaskMgrParms.pendingRequestForEnableKeyguard=mTaskMgrParms.enableKeyguard=false;
		}
	};

    
    final static private void analyzeConnectivityChanged(final Intent in) {
		final WakeLock wl=((PowerManager)mContext.getSystemService(Context.POWER_SERVICE))
    			.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
    					| PowerManager.ON_AFTER_RELEASE, "TaskAutomation-executeBuiltinEventTask");
		wl.acquire();
    	Runnable r = new Runnable(){
    		@Override
    		public void run() {
    	      	ConnectivityManager cm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    	        NetworkInfo ni=cm.getActiveNetworkInfo();
    	        if (ni!=null) {
    	        	boolean available=ni.isAvailable();
    	        	boolean connected=ni.isConnected();
    	        	boolean connected_or_connecting=ni.isConnectedOrConnecting();
    	        	boolean failover=ni.isFailover();
    	        	boolean roaming=ni.isRoaming();
    		        String extra_info=ni.getExtraInfo();
    		        String reason=ni.getReason();
    		        String type_name=ni.getTypeName();
    		        String sub_type_name=ni.getSubtypeName();
    		        if (mEnvParms.settingDebugLevel>=2)
    		        	mUtil.addDebugMsg(2,"I", "extra_info="+extra_info+", name="+type_name+
    		        		", sub_name="+sub_type_name+", reason="+reason+
    						", available="+available+", connected="+connected+
    						", connected_or_connecting="+connected_or_connecting+
    						", failover="+failover+", roaming="+roaming);
    		        if (mEnvParms.telephonyIsAvailable) {
    	    	        if (ni.getType()==ConnectivityManager.TYPE_MOBILE) {
    	        	        if (connected) {
    	        	        	if (!mEnvParms.mobileNetworkIsConnected) {
    	        	        		mEnvParms.mobileNetworkIsConnected=true;
//    	 	        				addTaskScheduleQueueBuiltin(mTaskMgrParms,mBuiltinEventTaskList,BUILTIN_EVENT_MOBILE_NETWORK_CONNECTED);
    	   	        				scheduleBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_MOBILE_NETWORK_CONNECTED);
    	        	        	} 
    	        	        } else {
    	        	        	if (mEnvParms.mobileNetworkIsConnected) {
    	        	        		mEnvParms.mobileNetworkIsConnected=false;
//    	        	        		addTaskScheduleQueueBuiltin(mTaskMgrParms,mBuiltinEventTaskList,BUILTIN_EVENT_MOBILE_NETWORK_DISCONNECTED);
    	        	        		scheduleBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_MOBILE_NETWORK_DISCONNECTED);
    	        	        	} 
    	        	        }
    	    	        } else {
    	    	        	if (connected) {
    	    	        		if (mEnvParms.mobileNetworkIsConnected) {
    	    	        			mEnvParms.mobileNetworkIsConnected=false;
//    	    	        			addTaskScheduleQueueBuiltin(mTaskMgrParms,mBuiltinEventTaskList,BUILTIN_EVENT_MOBILE_NETWORK_DISCONNECTED);
    	    	        			scheduleBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_MOBILE_NETWORK_DISCONNECTED);
    	    	        		}
    	    	        	} else {
    	    	        		if (!mEnvParms.mobileNetworkIsConnected) {
    	    	        			mEnvParms.mobileNetworkIsConnected=true;
//    	    	        			addTaskScheduleQueueBuiltin(mTaskMgrParms,mBuiltinEventTaskList,BUILTIN_EVENT_MOBILE_NETWORK_CONNECTED);
    	    	        			scheduleBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_MOBILE_NETWORK_CONNECTED);
    	    	        		}
    	    	        	}
    	    	        }
    		        }
    	        } else {
    		        mUtil.addDebugMsg(2,"I", "No network connection");
    		        if (mEnvParms.telephonyIsAvailable) {
    	    	        if (mEnvParms.mobileNetworkIsConnected) {
    	    	        	mEnvParms.mobileNetworkIsConnected=false;
//    	    	        	addTaskScheduleQueueBuiltin(mTaskMgrParms,mBuiltinEventTaskList,BUILTIN_EVENT_MOBILE_NETWORK_DISCONNECTED);
    	    	        	scheduleBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_MOBILE_NETWORK_DISCONNECTED);
    	    	        }
    		        }
    	        }
    	        wl.release();
    		}
    	};
    	mTaskMgrParms.normalTaskControlThreadPool.execute(r);
    };

//	final static private void forceRestartSchedulerX() {
//		deleteTaskList();
//		android.os.Process.killProcess(android.os.Process.myPid());
//	};
	final static private void restartScheduler() {
		TaskManager.acqLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE,
				mEnvParms, mTaskMgrParms, mUtil);
		deleteTaskList();
//		mTaskMgrParms.setKeyguardEnabled();
		
		Handler restartHandler=new Handler();
		restartHandler.postDelayed(new Runnable(){
			@Override
			public void run() {
				System.gc();
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		}, 500);
	};

	final static private boolean lockScreen() {
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

	@SuppressLint("InlinedApi")
	final static private void startBasicEventReceiver(Context c) {
  		IntentFilter intent = new IntentFilter();

  		intent.addAction(Intent.ACTION_BATTERY_CHANGED);
  		c.registerReceiver(mBatteryStatusReceiver, intent);
  		
  		intent = new IntentFilter();
  		intent.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
  		intent.addAction(WifiManager.RSSI_CHANGED_ACTION);
  		intent.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
		intent.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		intent.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		intent.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
		
		if (Build.VERSION.SDK_INT>=14) {
			intent.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
			intent.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
			intent.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
			intent.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
			intent.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		}
		c.registerReceiver(mWifiReceiver, intent);
        
        intent = new IntentFilter();
        
//        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
//        intent.addAction(BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED);
//        intent.addAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//        intent.addAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//        intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intent.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intent.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        c.registerReceiver(mBluetoothReceiver, intent);
        
        intent = new IntentFilter();
        intent.addAction(Intent.ACTION_SCREEN_OFF);
        intent.addAction(Intent.ACTION_SCREEN_ON);
        intent.addAction(Intent.ACTION_USER_PRESENT);
        c.registerReceiver(mSleepReceiver, intent);
        
        IntentFilter i_flt = new IntentFilter();
        i_flt.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
        i_flt.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        c.registerReceiver(mMiscellaneousReceiver, i_flt);
        
//        i_flt = new IntentFilter();
//        i_flt.addAction(HOME_SCREEN_DEVICE_BTN_BATTERY);
//        i_flt.addAction(HOME_SCREEN_DEVICE_BTN_WIFI);
//        i_flt.addAction(HOME_SCREEN_DEVICE_BTN_BLUETOOTH);
//        i_flt.addAction(HOME_SCREEN_DEVICE_BTN_SILENT);
//        i_flt.addAction(LOCKED_SCREEN_DEVICE_BTN_BATTERY);
//        i_flt.addAction(LOCKED_SCREEN_DEVICE_BTN_WIFI);
//        i_flt.addAction(LOCKED_SCREEN_DEVICE_BTN_BLUETOOTH);
//        i_flt.addAction(LOCKED_SCREEN_DEVICE_BTN_SILENT);
//        c.registerReceiver(mDeviceButtonReceiver, i_flt);
//        
        startPhoneStateListener();
        
    };

    final static private void stopBasicEventReceiver(Context c) {
    	
    	if (mBatteryStatusReceiver!=null) {
        	c.unregisterReceiver(mBatteryStatusReceiver);
    		mBatteryStatusReceiver=null;
    	}
    	if (mWifiReceiver!=null) {
        	c.unregisterReceiver(mWifiReceiver);
        	mWifiReceiver=null;
    	}
    	if (mBluetoothReceiver!=null) {
        	c.unregisterReceiver(mBluetoothReceiver);
        	mBluetoothReceiver=null;
    	}
    	if (mSleepReceiver!=null) {
        	c.unregisterReceiver(mSleepReceiver);
        	mSleepReceiver=null;
    	}
    	if (mMiscellaneousReceiver!=null) {
        	c.unregisterReceiver(mMiscellaneousReceiver);
        	mMiscellaneousReceiver=null;
    	}

    };
    
    final static private void startPhoneStateListener() {
    	final TelephonyManager tm = (TelephonyManager)mContext.getSystemService(TELEPHONY_SERVICE);
    	tm.listen(new PhoneStateListener() {
    	    @Override
    	    public void onCallStateChanged(int state, String number) {
    	        switch(state) {
    	        case TelephonyManager.CALL_STATE_RINGING:
    	        	mEnvParms.telephonyStatus=TelephonyManager.CALL_STATE_RINGING;
//    	        	addTaskScheduleQueueBuiltin(mTaskMgrParms,mBuiltinEventTaskList,BUILTIN_EVENT_PHONE_CALL_STATE_RINGING);
    	        	scheduleBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_PHONE_CALL_STATE_RINGING);
    	            break;
    	 
    	        case TelephonyManager.CALL_STATE_OFFHOOK:
    	        	mEnvParms.telephonyStatus=TelephonyManager.CALL_STATE_OFFHOOK;
//    	        	addTaskScheduleQueueBuiltin(mTaskMgrParms,mBuiltinEventTaskList,BUILTIN_EVENT_PHONE_CALL_STATE_OFF_HOOK);
    	        	scheduleBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_PHONE_CALL_STATE_OFF_HOOK);
    	            break;
    	 
    	        case TelephonyManager.CALL_STATE_IDLE:
    	        	mEnvParms.telephonyStatus=TelephonyManager.CALL_STATE_IDLE;
//    	        	addTaskScheduleQueueBuiltin(mTaskMgrParms,mBuiltinEventTaskList,BUILTIN_EVENT_PHONE_CALL_STATE_IDLE);
    	        	scheduleBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_PHONE_CALL_STATE_IDLE);
    	            break;
    	    };
    	    }
    	}, PhoneStateListener.LISTEN_CALL_STATE);
    };

    final static private class MiscellaneousReceiver extends BroadcastReceiver {
		@SuppressLint("Wakelock")
		@Override
		final public void onReceive(Context c, Intent in) {
			WakeLock wl=((PowerManager)mContext.getSystemService(Context.POWER_SERVICE))
	    			.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
	    					| PowerManager.ON_AFTER_RELEASE, "TaskAutomation-Misc");
			wl.acquire();
			String action=in.getAction();
			mUtil.addDebugMsg(1,"I", "MiscellaneousReceiver entered, action=",action);
			if(AudioManager.RINGER_MODE_CHANGED_ACTION.equals(action)){
				mUtil.addDebugMsg(1,"I", "RingerMode from="+mEnvParms.currentRingerMode+", new="+mAudioManager.getRingerMode());
				mEnvParms.currentRingerMode=mAudioManager.getRingerMode();
				mWidgetSvc.processRingerModeChanged();
			} else if (action.equals(Intent.ACTION_AIRPLANE_MODE_CHANGED)) {
				int nv=getAirplaneModeOn();
				if (nv!=mEnvParms.airplane_mode_on) {
					mEnvParms.airplane_mode_on=nv;
//					if (nv==0) addTaskScheduleQueueBuiltin(mTaskMgrParms,mBuiltinEventTaskList, BUILTIN_EVENT_AIRPLANE_MODE_OFF);
//					else addTaskScheduleQueueBuiltin(mTaskMgrParms,mBuiltinEventTaskList, BUILTIN_EVENT_AIRPLANE_MODE_ON);
					if (nv==0) scheduleBuiltinEventTask(mBuiltinEventTaskList, BUILTIN_EVENT_AIRPLANE_MODE_OFF);
					else scheduleBuiltinEventTask(mBuiltinEventTaskList, BUILTIN_EVENT_AIRPLANE_MODE_ON);
				}
			}
			wl.release();
		}
    };

//    final static private class DeviceButtonReceiver extends BroadcastReceiver {
//		@Override
//		final public void onReceive(Context c, Intent in) {
//			mUtil.addDebugMsg(1,"I", "DeviceButtonReceiver entered, action=",in.getAction());
//			mWidgetSvc.processDeviceButton(in);
//		}
//    };
//    
    final static private class  BatteryStatusReceiver  extends BroadcastReceiver {
		@Override
		final public void onReceive(Context c, Intent in) {
//			WakeLock wl=((PowerManager)mAppContext.getSystemService(Context.POWER_SERVICE))
//	    			.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TaskAutomation-Battery");
//			wl.acquire(100);
//			util.sendDebugLogMsg(2,"I","Battery receiver entered");
			mLastBatteryStatusSt=in.getIntExtra("status", 0);
//			int health = in.getIntExtra("health", 0);
//			int voltage = in.getIntExtra("voltage", 0);
//			int temperature = in.getIntExtra("temperature", 0);
			mLastBatteryStatusBl = in.getIntExtra("level", 0);
			mLastBatteryStatusBs = in.getIntExtra("scale", 0);
			analyzeBatteryStatusValue(mLastBatteryStatusSt,mLastBatteryStatusBl,mLastBatteryStatusBs);
		}
    };

    static private int mLastBatteryStatusSt,mLastBatteryStatusBl,mLastBatteryStatusBs;
    static final private String parseBatteryChargeStatus(int st) {
		String n_bcs=mTaskMgrParms.svcMsgs.msgs_widget_battery_status_charge_discharging;
		if (st==BatteryManager.BATTERY_PLUGGED_AC||st==BatteryManager.BATTERY_STATUS_CHARGING){
			n_bcs=mTaskMgrParms.svcMsgs.msgs_widget_battery_status_charge_charging;
			mEnvParms.batteryChargeStatusInt=mEnvParms.BATTERY_CHARGE_STATUS_INT_CHARGING;
		} else if (st==BatteryManager.BATTERY_STATUS_DISCHARGING||st==BatteryManager.BATTERY_STATUS_NOT_CHARGING){
			n_bcs=mTaskMgrParms.svcMsgs.msgs_widget_battery_status_charge_discharging;
			mEnvParms.batteryChargeStatusInt=mEnvParms.BATTERY_CHARGE_STATUS_INT_DISCHARGING;
		} else if (st==BatteryManager.BATTERY_STATUS_FULL){
			n_bcs=mTaskMgrParms.svcMsgs.msgs_widget_battery_status_charge_full;
			mEnvParms.batteryChargeStatusInt=mEnvParms.BATTERY_CHARGE_STATUS_INT_FULL;
		}
		return n_bcs;
    };
    
    static final private void analyzeBatteryStatusValue(int st,int bl, int bs) {
		String n_ps="";
		int n_bl=0;
		if (bs==0) n_bl=bl;
		else n_bl=(bl*100)/bs;
		if (st==BatteryManager.BATTERY_PLUGGED_AC||
				st==BatteryManager.BATTERY_PLUGGED_USB ||
				st==BatteryManager.BATTERY_STATUS_FULL )
//				st==BatteryManager.BATTERY_STATUS_NOT_CHARGING) 
				n_ps=CURRENT_POWER_SOURCE_AC;
			else n_ps=CURRENT_POWER_SOURCE_BATTERY;
		if (n_ps==CURRENT_POWER_SOURCE_AC) {
			if (mEnvParms.batteryPowerSource.equals(n_ps)) {
				//充電中が継続なのでなにもしない
			} else {
				//放電から充電に切りかわったのでなにもしない
			}
		} else {
			long sctm=System.currentTimeMillis();
			if (mEnvParms.batteryPowerSource.equals(n_ps)) {
				if (mEnvParms.batteryLevel==-1) {
					//起動直後ため何もしない
				} else {
					if (mEnvParms.battery_comsumption_data_begin_level==0) {
						//reset
						mEnvParms.battery_comsumption_data_begin_time=sctm;
						mEnvParms.battery_comsumption_data_begin_level=bl;
						mUtil.addDebugMsg(1,"I","Battery comsumption ratio was reset(Begin value was invalid)");
					}
					if (mEnvParms.battery_comsumption_data_end_level!=bl) {
						mEnvParms.battery_comsumption_data_end_time=sctm;
						mEnvParms.battery_comsumption_data_end_level=bl;
						mEnvParms.saveBatteryComsumptionData(mContext);
						if (mEnvParms.settingDebugLevel>=1) {
							long rate=-1;
							long diff_time=(mEnvParms.battery_comsumption_data_end_time-mEnvParms.battery_comsumption_data_begin_time);
							long diff_level=(mEnvParms.battery_comsumption_data_begin_level-mEnvParms.battery_comsumption_data_end_level);
							if (diff_level>0) rate=diff_time/diff_level;
							
							mUtil.addDebugMsg(1,"I","Battery consumption rate="+rate+", time="+diff_time+", level="+diff_level);
						}
					}
				}
			} else {
				//放電に切りかわった
				mEnvParms.battery_comsumption_data_begin_time=sctm;
				mEnvParms.battery_comsumption_data_begin_level=bl;
				mEnvParms.battery_comsumption_data_end_time=sctm;
				mEnvParms.battery_comsumption_data_end_level=bl;
				mEnvParms.saveBatteryComsumptionData(mContext);
				mUtil.addDebugMsg(1,"I","Battery comsumption ratio was reset");
			}
		}
		String n_bcs=parseBatteryChargeStatus(st);
//		mUtil.addDebugMsg(1,"I","Battery receiver bl=",String.valueOf(bl));
		if (mEnvParms.batteryLevel==-1) {
			if (mEnvParms.settingDebugLevel>=1)
				mUtil.addDebugMsg(1,"I","Initial battery status, level=",String.valueOf(n_bl),
					", source=",n_ps,", charge=",n_bcs,
					", notify_high=",String.valueOf(mBatteryLevelHighNotified),
					", notify_low=",String.valueOf(mBatteryLevelLowNotified),
					", notify_critical=",String.valueOf(mBatteryLevelCriticalNotified));
			mEnvParms.batteryChargeStatusString=n_bcs;
			mEnvParms.batteryLevel=n_bl;
			mEnvParms.batteryPowerSource=n_ps;
			checkBatteryNotification(true, false, true);
			mWidgetSvc.processBatteryStatusChanged();

			TaskManager.showNotification(mTaskMgrParms, mEnvParms, mUtil);
		} else if (!n_ps.equals(mEnvParms.batteryPowerSource) || (n_bl!=mEnvParms.batteryLevel) ||
				(n_bcs!=mEnvParms.batteryChargeStatusString)) {
//			if (envParms.settingDebugLevel>=1)
			mUtil.addLogMsg("I","Battery status changed,",
					" Level=(", String.valueOf(mEnvParms.batteryLevel), ",", String.valueOf(n_bl),")",
					", Power=(",mEnvParms.batteryPowerSource, ",", n_ps,")", 
					", Charge=(", mEnvParms.batteryChargeStatusString, ",", n_bcs,")",
					", notify_high=",String.valueOf(mBatteryLevelHighNotified),
					", notify_low=",String.valueOf(mBatteryLevelLowNotified),
					", notify_critical=",String.valueOf(mBatteryLevelCriticalNotified));
			boolean change_ps=false, change_bcs=false, change_bl=false;
			if (!n_ps.equals(mEnvParms.batteryPowerSource)) change_ps=true;
			if (n_bl!=mEnvParms.batteryLevel) change_bl=true;
			if (n_bcs!=mEnvParms.batteryChargeStatusString) change_bcs=true; 
			mEnvParms.batteryChargeStatusString=n_bcs;
			mEnvParms.batteryLevel=n_bl;
			mEnvParms.batteryPowerSource=n_ps;
			checkBatteryNotification(change_bcs, change_ps, change_bl);
			mWidgetSvc.processBatteryStatusChanged();

			TaskManager.showNotification(mTaskMgrParms, mEnvParms, mUtil);
		}
		//Level reset
		if (n_bl<BATTERY_LEVEL_THRESHOLD_HIGH) mBatteryLevelHighNotified=false;
		if (n_bl>BATTERY_LEVEL_THRESHOLD_LOW) mBatteryLevelLowNotified=false;
		if (n_bl>BATTERY_LEVEL_THRESHOLD_CRITICAL) mBatteryLevelCriticalNotified=false;
    };
    
    final static private void checkBatteryNotification(boolean change_bcs, 
    		boolean change_ps, boolean change_bl) {
		if (change_bcs &&
				mEnvParms.batteryChargeStatusString.equals(mTaskMgrParms.svcMsgs.msgs_widget_battery_status_charge_full)) {
//			addTaskScheduleQueueBuiltin(mTaskMgrParms,mBuiltinEventTaskList, BUILTIN_EVENT_BATTERY_FULLY_CHARGED);
			scheduleBuiltinEventTask(mBuiltinEventTaskList, BUILTIN_EVENT_BATTERY_FULLY_CHARGED);
		}
		if (change_ps) {
			if (mEnvParms.batteryPowerSource.equals(CURRENT_POWER_SOURCE_AC)) {
//				addTaskScheduleQueueBuiltin(mTaskMgrParms,mBuiltinEventTaskList,BUILTIN_EVENT_POWER_SOURCE_CHANGED_AC);
				scheduleBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_POWER_SOURCE_CHANGED_AC);
			} else {
//				addTaskScheduleQueueBuiltin(mTaskMgrParms,mBuiltinEventTaskList,BUILTIN_EVENT_POWER_SOURCE_CHANGED_BATTERY);
				scheduleBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_POWER_SOURCE_CHANGED_BATTERY);
			}
		}
		if (change_bl) {
			if (mRequiredBatteryLevelExecution) {
//				addTaskScheduleQueueBuiltin(mTaskMgrParms,mBuiltinEventTaskList, BUILTIN_EVENT_BATTERY_LEVEL_CHANGED);
				scheduleBuiltinEventTask(mBuiltinEventTaskList, BUILTIN_EVENT_BATTERY_LEVEL_CHANGED);
			}
			if (mEnvParms.batteryLevel<=BATTERY_LEVEL_THRESHOLD_CRITICAL && !mBatteryLevelCriticalNotified) {
				mBatteryLevelCriticalNotified=true;
//				addTaskScheduleQueueBuiltin(mTaskMgrParms,mBuiltinEventTaskList, BUILTIN_EVENT_BATTERY_LEVEL_CRITICAL);
				scheduleBuiltinEventTask(mBuiltinEventTaskList, BUILTIN_EVENT_BATTERY_LEVEL_CRITICAL);
			}
			if (mEnvParms.batteryLevel<=BATTERY_LEVEL_THRESHOLD_LOW && !mBatteryLevelLowNotified) {
				mBatteryLevelLowNotified=true;
//				addTaskScheduleQueueBuiltin(mTaskMgrParms,mBuiltinEventTaskList, BUILTIN_EVENT_BATTERY_LEVEL_LOW);
				scheduleBuiltinEventTask(mBuiltinEventTaskList, BUILTIN_EVENT_BATTERY_LEVEL_LOW);
			}
			if (mEnvParms.batteryLevel>=BATTERY_LEVEL_THRESHOLD_HIGH &&
					mEnvParms.batteryChargeStatusString.equals(mTaskMgrParms.svcMsgs.msgs_widget_battery_status_charge_charging) &&
					!mBatteryLevelHighNotified) {
				mBatteryLevelHighNotified=true;
//				addTaskScheduleQueueBuiltin(mTaskMgrParms,mBuiltinEventTaskList, BUILTIN_EVENT_BATTERY_LEVEL_HIGH);
				scheduleBuiltinEventTask(mBuiltinEventTaskList, BUILTIN_EVENT_BATTERY_LEVEL_HIGH);
			}
		}
    	
    }
//    private String getCurrentPowerSource() {
//    	String cps=util.getPrefMgr().getString(PREFS_CURRENT_POWER_SOURCE_KEY, "UNKOWN");
//    	if (envParms.settingDebugLevel!=0) util.addDebugMsg(3, "I", "getCurrentPowerSource result="+cps);
//		return cps;
//    };
//    private void setCurrentPowerSource(String ps) {
//    	if (envParms.settingDebugLevel!=0) util.addDebugMsg(3, "I", "setCurrentPowerSource result="+ps);
//		prefsMgutil.getPrefMgr().putString(PREFS_CURRENT_POWER_SOURCE, ps).commit();
//		util.getPrefMgr().edit().putString(PREFS_CURRENT_POWER_SOURCE_KEY, ps).apply();
//    };
    
    final static private class WifiReceiver  extends BroadcastReceiver {
		@SuppressLint({ "InlinedApi", "NewApi" })
		@Override
		final public void onReceive(Context c, Intent in) {
//			WakeLock wl=((PowerManager)mAppContext.getSystemService(Context.POWER_SERVICE))
//	    			.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TaskAutomation-Wifi");
//			wl.acquire(100);

//			String action = in.getAction();
			String tssid=mWifiMgr.getConnectionInfo().getSSID();
			String tmac=mWifiMgr.getConnectionInfo().getBSSID();
			String wssid="";
			String ss=mWifiMgr.getConnectionInfo().getSupplicantState().toString();
			NetworkInfo ni=in.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
			if (ni!=null) {
//				WifiP2pManager wpm=(WifiP2pManager)mAppContext.getSystemService(Context.WIFI_P2P_SERVICE);
//				Log.v("","state="+ni.getState().toString()+", ssid="+tssid);
				if (mEnvParms.wifiConnectedSsidName.equals("") && ni.getState().equals(NetworkInfo.State.CONNECTED)){
					tssid=EnvironmentParms.WIFI_DIRECT_SSID;
					ss="COMPLETED";
				} else if (mEnvParms.wifiConnectedSsidName.equals(EnvironmentParms.WIFI_DIRECT_SSID) && ni.getState().equals(NetworkInfo.State.DISCONNECTED)){
					tssid="";
					ss="DISCONNECTED";
				}
			}
			if (tssid==null || tssid.equals("<unknown ssid>")) wssid="";
			else wssid=tssid.replaceAll("\"", "");
			if (wssid.equals("0x")) wssid="";
			
			boolean new_wifi_enabled=mWifiMgr.isWifiEnabled();
			mUtil.addDebugMsg(2,"I","WIFI receiver " +"Action="+in.getAction()+
					", SupplicantState="+ss+
					", mEnvParms.wifiIsActive="+mEnvParms.wifiIsActive+
					", new_wifi_enabled="+new_wifi_enabled+
					", mEnvParms.wifiSsid="+mEnvParms.wifiConnectedSsidName+
					", tssid="+tssid+", wssid="+wssid+", mac addr="+tmac);
			if (!new_wifi_enabled && mEnvParms.wifiIsActive ) {
				mUtil.addDebugMsg(1,"I","WIFI receiver, WIFI Off");
				mEnvParms.wifiConnectedSsidName="";
				mEnvParms.wifiConnectedSsidAddr="";
				mEnvParms.wifiIsActive=false;
				mWidgetSvc.processWifiStatusChanged();
				TaskManager.notifyToEventList(mTaskMgrParms, 
						mTaskMgrParms.wifiNotifyEventList,EXTRA_DEVICE_EVENT_DEVICE_OFF);
//				addTaskScheduleQueueBuiltin(mTaskMgrParms,mBuiltinEventTaskList,BUILTIN_EVENT_WIFI_OFF);
				scheduleBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_WIFI_OFF);
			} else {
				if (ss.equals("COMPLETED")  
//						|| ss.equals("ASSOCIATING") 
//						|| ss.equals("ASSOCIATED")
						) {
					if (in.getAction().equals(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)) {
//						NetworkInfo ni=in.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
//						Log.v("","state="+ni.getState());
//						if (ni.getState().equals(NetworkInfo.State.CONNECTED)) {
//							TaskManager.notifyToEventList(mTaskMgrParms, 
//									mTaskMgrParms.wifiNotifyEventList, EXTRA_DEVICE_EVENT_DEVICE_CONNECTED);
//							executeBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_WIFI_CONNECTED);
//						} else if (ni.getState().equals(NetworkInfo.State.DISCONNECTED)) {
//							TaskManager.notifyToEventList(mTaskMgrParms, 
//									mTaskMgrParms.wifiNotifyEventList, EXTRA_DEVICE_EVENT_DEVICE_DISCONNECTED);
//							executeBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_WIFI_DISCONNECTED);
//						}
					}
					if (mEnvParms.wifiConnectedSsidName.equals("") && !wssid.equals("")) {
						mUtil.addDebugMsg(1,"I","WIFI receiver, Connected WIFI Access point ssid=",wssid);
						mEnvParms.wifiConnectedSsidName=wssid;
						mEnvParms.wifiConnectedSsidAddr=tmac;
						mEnvParms.wifiIsActive=true; //2013/09/04
//						mUtil.setSavedWifiSsidName(wssid);
//						mUtil.setSavedWifiSsidAddr(tmac);
						mWidgetSvc.processWifiStatusChanged();
						TaskManager.notifyToEventList(mTaskMgrParms, 
								mTaskMgrParms.wifiNotifyEventList, EXTRA_DEVICE_EVENT_DEVICE_CONNECTED);
//						addTaskScheduleQueueBuiltin(mTaskMgrParms,mBuiltinEventTaskList,BUILTIN_EVENT_WIFI_CONNECTED);
						scheduleBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_WIFI_CONNECTED);
					}
				} else if (ss.equals("INACTIVE") ||
						ss.equals("DISCONNECTED") ||
						ss.equals("UNINITIALIZED") ||
						ss.equals("INTERFACE_DISABLED") ||
						ss.equals("SCANNING")) {
//					Log.v("","ss="+ss+", previousWifiActive="+previousWifiActive+
//							", !wifiMgr.isWifiEnabled="+!wifiMgr.isWifiEnabled()+
//							", wssid="+wssid+", previousSSID="+previousSSID);
					if (mEnvParms.wifiIsActive) {
						if (!mEnvParms.wifiConnectedSsidName.equals("")) {
							mUtil.addDebugMsg(1,"I","WIFI receiver, Disconnected WIFI Access point ssid=", mEnvParms.wifiConnectedSsidName);
							mEnvParms.wifiConnectedSsidName="";
							mEnvParms.wifiConnectedSsidAddr="";
//							mUtil.setSavedWifiSsidName("");
//							mUtil.setSavedWifiSsidAddr("");
							mEnvParms.wifiIsActive=true;
							mWidgetSvc.processWifiStatusChanged();
							TaskManager.notifyToEventList(mTaskMgrParms, 
									mTaskMgrParms.wifiNotifyEventList,EXTRA_DEVICE_EVENT_DEVICE_DISCONNECTED);
//							addTaskScheduleQueueBuiltin(mTaskMgrParms,mBuiltinEventTaskList,BUILTIN_EVENT_WIFI_DISCONNECTED);
							scheduleBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_WIFI_DISCONNECTED);							
						}
					} else {
						if (new_wifi_enabled) {
							mUtil.addDebugMsg(1,"I","WIFI receiver, WIFI On");
							mEnvParms.wifiConnectedSsidName="";
							mEnvParms.wifiConnectedSsidAddr="";
//							mUtil.setSavedWifiSsidName("");
//							mUtil.setSavedWifiSsidAddr("");
							mEnvParms.wifiIsActive=true;
							mWidgetSvc.processWifiStatusChanged();
							TaskManager.notifyToEventList(mTaskMgrParms, 
									mTaskMgrParms.wifiNotifyEventList,EXTRA_DEVICE_EVENT_DEVICE_ON);
//							addTaskScheduleQueueBuiltin(mTaskMgrParms,mBuiltinEventTaskList,BUILTIN_EVENT_WIFI_ON);
							scheduleBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_WIFI_ON);
						} //else mWidgetSvc.processWifiStatusChanged();
					}
				}
			}
		}
    };

    final static private class BluetoothReceiver  extends BroadcastReceiver {
		@Override
		final public void onReceive(Context c, Intent in) {
			String action = in.getAction();
			mUtil.addDebugMsg(2,"I","Bluetooth receiver entered, action=",action);
			if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				int bs=BluetoothAdapter.getDefaultAdapter().getState();
				if (bs==BluetoothAdapter.STATE_OFF) {
					
					mEnvParms.bluetoothIsActive=false;
					mEnvParms.clearBluetoothConnectedDeviceList();
					mEnvParms.blutoothConnectedDeviceName=mEnvParms.blutoothConnectedDeviceAddr="";
					mWidgetSvc.processBluetoothStatusChanged();
					TaskManager.notifyToEventList(mTaskMgrParms, 
							mTaskMgrParms.bluetoothNotifyEventList,EXTRA_DEVICE_EVENT_DEVICE_OFF);
					scheduleBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_BLUETOOTH_OFF);
				} else if (bs==BluetoothAdapter.STATE_ON) {
					mEnvParms.bluetoothIsActive=true;
					mEnvParms.clearBluetoothConnectedDeviceList();
					mEnvParms.blutoothConnectedDeviceName=mEnvParms.blutoothConnectedDeviceAddr="";
					mWidgetSvc.processBluetoothStatusChanged();
					TaskManager.notifyToEventList(mTaskMgrParms, 
							mTaskMgrParms.bluetoothNotifyEventList,EXTRA_DEVICE_EVENT_DEVICE_ON);
					scheduleBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_BLUETOOTH_ON);
				}
			} else {
				BluetoothDevice device = in.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
					mUtil.addDebugMsg(1,"I","Bluetooth connected, dev=",device.getName()+", addr="+device.getAddress());
					mEnvParms.addBluetoothConnectedDevice(device.getName(), device.getAddress());
					mUtil.putSavedBluetoothConnectedDeviceList(mEnvParms.getBluetoothConnectedDeviceList());
					mEnvParms.blutoothConnectedDeviceName=device.getName();
					mEnvParms.blutoothConnectedDeviceAddr=device.getAddress();
					mWidgetSvc.processBluetoothStatusChanged();
					TaskManager.notifyToEventList(mTaskMgrParms, 
							mTaskMgrParms.bluetoothNotifyEventList,EXTRA_DEVICE_EVENT_DEVICE_CONNECTED);
					scheduleBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_BLUETOOTH_CONNECTED);
				} else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
					mUtil.addDebugMsg(1,"I","Bluetooth disconnected, dev=",device.getName()+", addr="+device.getAddress());
					mEnvParms.removeBluetoothConnectedDevice(device.getName(), device.getAddress());
					mUtil.putSavedBluetoothConnectedDeviceList(mEnvParms.getBluetoothConnectedDeviceList());
	 	 			ArrayList<BluetoothDeviceListItem>bdl=mEnvParms.getBluetoothConnectedDeviceList();
	 	 			if (bdl.size()>0) {
//	 	 	 			mEnvParms.blutoothLastEventDeviceName=bdl.get(bdl.size()-1).btName;
//	 	 	 			mEnvParms.blutoothLastEventDeviceAddr=bdl.get(bdl.size()-1).btAddr;
	 	 	 			mEnvParms.blutoothConnectedDeviceName=bdl.get(bdl.size()-1).btName;
	 	 	 			mEnvParms.blutoothConnectedDeviceAddr=bdl.get(bdl.size()-1).btAddr;
	 	 			} else {
						mEnvParms.blutoothConnectedDeviceName="";
						mEnvParms.blutoothConnectedDeviceAddr="";
	 	 			}

					mWidgetSvc.processBluetoothStatusChanged();
					TaskManager.notifyToEventList(mTaskMgrParms, 
							mTaskMgrParms.bluetoothNotifyEventList,EXTRA_DEVICE_EVENT_DEVICE_DISCONNECTED);
					scheduleBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_BLUETOOTH_DISCONNECTED);
				}
			}
		}	
    };

    final static private class SleepReceiver  extends BroadcastReceiver {
		@SuppressLint({ "Wakelock", "NewApi"})
		@Override 
		final public void onReceive(Context c, Intent in) {
			String action = in.getAction();
			if (mEnvParms.settingDebugLevel>=1) mUtil.addDebugMsg(1,"I","Sleep receiver entered, action=",action,
					", enableKeyguard="+mTaskMgrParms.enableKeyguard,
					", pendingRequestForEnableKeyguard="+mTaskMgrParms.pendingRequestForEnableKeyguard,
					", isKeyguardEffective()="+mUtil.isKeyguardEffective()+
					", screenIsOn="+mEnvParms.screenIsOn+
					", proximitySensorValue="+mEnvParms.proximitySensorValue);
			if(action.equals(Intent.ACTION_SCREEN_ON)) {
				boolean kge=mUtil.isKeyguardEffective();
				mEnvParms.screenIsOn=true;
				if (!kge) {
					relWakeLockForSleep();
			 		stopLightSensorReceiver();
//			 		stopProximitySensorReceiver();
			 		startLightSensorReceiver();
//			 		startProximitySensorReceiver();
			 		if (mEnvParms.screenIsLocked) {
						mEnvParms.screenIsLocked=false;
			 			scheduleBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_SCREEN_UNLOCKED);
			 			if (Build.VERSION.SDK_INT>=21) {
				 			TaskManager.buildNotification(mTaskMgrParms, mEnvParms);
				 			TaskManager.showNotification(mTaskMgrParms, mEnvParms, mUtil);
			 			}
			 		}
			 		if (mEnvParms.proximitySensorActive && mEnvParms.proximitySensorValue==0) {
			 			mUiHandler.postDelayed(new Runnable(){
							@Override
							public void run() {
					 			scheduleBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_PROXIMITY_DETECTED);
							}
			 			}, 200);
			 		}
				} else {
			 		if (mEnvParms.proximitySensorActive && mEnvParms.proximitySensorValue==0) {
			 			scheduleBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_PROXIMITY_DETECTED);
			 		}
				}
			} else if(action.equals(Intent.ACTION_SCREEN_OFF)) {
//				if (mTaskMgrParms.pendingRequestForEnableKeyguard && mTaskMgrParms.enableKeyguard) {
				if (mTaskMgrParms.enableKeyguard && mEnvParms.settingEnableScheduler) {
					mUtil.addDebugMsg(1,"I","reenableKeyguard issued during screen off");
					if (mEnvParms.settingScreenKeyguardControlEnabled) {
						mTaskMgrParms.setKeyguardEnabled();
					} else {
						mUtil.addDebugMsg(1,"I","EnableKeyguard ignored, Keyguard control is disabled");
					}
					mTaskMgrParms.pendingRequestForEnableKeyguard=false;
				}
				mEnvParms.screenIsOn=false;
				if (!mEnvParms.screenIsLocked) {
					mEnvParms.screenIsLocked=true;
					acqWakeLockForSleep();
			 		stopLightSensorReceiver();
//			 		stopProximitySensorReceiver();
			 		startLightSensorReceiver();
					setIgnoreProxitySensorValue();
//			 		startProximitySensorReceiver();
			 		scheduleBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_SCREEN_LOCKED);
		 			if (Build.VERSION.SDK_INT>=21) {
			 			TaskManager.buildNotification(mTaskMgrParms, mEnvParms);
			 			TaskManager.showNotification(mTaskMgrParms, mEnvParms, mUtil);
		 			}
				} else {
			 		stopLightSensorReceiver();
			 		startLightSensorReceiver();
				}
			} else if(action.equals(Intent.ACTION_USER_PRESENT)) {
//				if (mTaskMgrParms.pendingRequestForEnableKeyguard && !mTaskMgrParms.enableKeyguard) {
				if (!mTaskMgrParms.enableKeyguard && mEnvParms.settingEnableScheduler) {
					//無効化する前にパスワードやパターンを入れてクリアする必要がある
					mUtil.addDebugMsg(1,"I","disableKeyguard issued during user present");
					if (mEnvParms.settingScreenKeyguardControlEnabled) {
						mTaskMgrParms.setKeyguardDisabled();
					} else {
						mUtil.addDebugMsg(1,"I","DisableKeyguard ignored, Keyguard control is disabled");
					}
					mTaskMgrParms.pendingRequestForEnableKeyguard=false;
				}
				mEnvParms.screenIsOn=true;
				mEnvParms.screenIsLocked=false;
				relWakeLockForSleep();
		 		stopLightSensorReceiver();
//		 		stopProximitySensorReceiver();
		 		startLightSensorReceiver();
//		 		startProximitySensorReceiver();
		 		scheduleBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_SCREEN_UNLOCKED);
	 			if (Build.VERSION.SDK_INT>=21) {
		 			TaskManager.buildNotification(mTaskMgrParms, mEnvParms);
		 			TaskManager.showNotification(mTaskMgrParms, mEnvParms, mUtil);
	 			}
			}
		}	
    };

    private static boolean isIgnoreProxitySensorValue() {
    	return mIgnoreProximitySensorValue;
    };
    private static boolean mIgnoreProximitySensorValue=false;
    private static void setIgnoreProxitySensorValue() {
//    	mIgnoreProximitySensorValue=true;
//    	mUiHandler.postDelayed(new Runnable(){
//			@Override
//			public void run() {
//				mIgnoreProximitySensorValue=false;
//			}
//    	}, 200);
    };

	@SuppressLint("Wakelock")
	final static private class ProximitySensorReceiver implements SensorEventListener {
    	@Override
    	final public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    	@Override
    	final public void onSensorChanged(SensorEvent event) {
			WakeLock wl=((PowerManager)mContext.getSystemService(Context.POWER_SERVICE))
					.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TaskAutomation-Proximity");
			wl.acquire();

    		int nv=Integer.valueOf((int) event.values[0]);
        	if (mEnvParms.settingDebugLevel>=1) mUtil.addDebugMsg(1,"I","Proximity sensor current=",
    				String.valueOf(mEnvParms.proximitySensorValue),", new=",String.valueOf(nv));
        	if (mEnvParms.proximitySensorValue==nv) {
        		if (mEnvParms.settingDebugLevel>=1) mUtil.addDebugMsg(1,"I","Proximity sensor ignored");
        	} else {
    			mEnvParms.proximitySensorValue=nv;
    			if (!isIgnoreProxitySensorValue()) {
        			if (mEnvParms.proximitySensorValue>=1) scheduleBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_PROXIMITY_UNDETECTED);
        			else scheduleBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_PROXIMITY_DETECTED);
    			} else {
    				if (mEnvParms.settingDebugLevel>=1) 
    					mUtil.addDebugMsg(1,"I","Proximity sensor ignored, disable flag active");
    			}
        	}
			wl.release();
    	}
    };

	private static float[] mLowpassAccelerometerValue=new float[3];
	private static float[] mHighpassAccelerometerValue=new float[3];
    final static private class AccelerometerSensorReceiver implements SensorEventListener {
    	@Override
    	final public void onAccuracyChanged(Sensor sensor, int accuracy) {}
		@Override
		final public void onSensorChanged(SensorEvent event) {
			// ローパスフィルタ
			mLowpassAccelerometerValue[0]=mLowpassAccelerometerValue[0]*0.9f+event.values[0]*0.1f;
			mLowpassAccelerometerValue[1]=mLowpassAccelerometerValue[1]*0.9f+event.values[1]*0.1f;
			mLowpassAccelerometerValue[2]=mLowpassAccelerometerValue[2]*0.9f+event.values[2]*0.1f;
	    	
		    // ハイパスフィルター
	    	mHighpassAccelerometerValue[0] = event.values[0] - mLowpassAccelerometerValue[0];
	    	mHighpassAccelerometerValue[1] = event.values[1] - mLowpassAccelerometerValue[1];
	    	mHighpassAccelerometerValue[2] = event.values[2] - mLowpassAccelerometerValue[2];

//		    Log.v("","x="+mHighpassAccelerometerValue[0]+
//		    		", y="+mHighpassAccelerometerValue[1]+
//		    		", z="+mHighpassAccelerometerValue[2]);
		    
			getOrientation();
    	}
    };
    
    static private float[] mRotationMatrix = new float[9];
    static private float[] mRotationAttitude = new float[3];
    static private int currentOrientation=-1;
    final static private void getOrientation() {
        final double RAD2DEG = 180/Math.PI;
			SensorManager.getRotationMatrix(mRotationMatrix, null, 
					mLowpassAccelerometerValue, mLowpassMagneticFieldValue);
//			SensorManager.getRotationMatrix(mRotationMatrix, null, 
//					mHighpassAccelerometerValue, mHighpassMagneticFieldValue);
			
			SensorManager.getOrientation(mRotationMatrix,mRotationAttitude);
			
//			String azimuthText=Integer.toString((int)(mRotationAttitude[0] * RAD2DEG));
//			String pitchText=Integer.toString((int)(mRotationAttitude[1] * RAD2DEG));
//			String rollText=Integer.toString((int)(mRotationAttitude[2] * RAD2DEG));
//			Log.v("","azimuth="+azimuthText+", pitch="+pitchText+", roll="+rollText);
			
			int orientation=0;
			int roll=(int)(mRotationAttitude[2] * RAD2DEG);
			//inputのroll(Y軸のDegree)は-180～180の範囲を想定
		    if(-225 < roll  && roll <= -135  ) orientation=180;
		    if(-135 < roll  && roll <=  -45  ) orientation=90;
		    if( -45 < roll  && roll <=   45  ) orientation=0;
		    if(  45 < roll  && roll <=  135  ) orientation=-90;
		    if( 135 < roll  && roll <=  225  ) orientation=-180;
		 
		    if (currentOrientation!=orientation) {
//		    	Log.v("TaskAutomation","orientation="+orientation);
		    	currentOrientation=orientation;
		    }
    };
    
//	private static float mLowPassMagneticFieldValue=0;
	private static float[] mLowpassMagneticFieldValue=new float[3];
	private static float[] mHighpassMagneticFieldValue=new float[3];
    final static private class MagneticFieldSensorReceiver implements SensorEventListener {
    	@Override
    	final public void onAccuracyChanged(Sensor sensor, int accuracy) {}
		@Override
		final public void onSensorChanged(SensorEvent event) {
	    	
			// ローパスフィルタ
			mLowpassMagneticFieldValue[0]=mLowpassMagneticFieldValue[0]*0.9f+event.values[0]*0.1f;
			mLowpassMagneticFieldValue[1]=mLowpassMagneticFieldValue[1]*0.9f+event.values[1]*0.1f;
			mLowpassMagneticFieldValue[2]=mLowpassMagneticFieldValue[2]*0.9f+event.values[2]*0.1f;
	    	
		    // ハイパスフィルター
		    mHighpassMagneticFieldValue[0] = event.values[0] - mLowpassMagneticFieldValue[0];
		    mHighpassMagneticFieldValue[1] = event.values[1] - mLowpassMagneticFieldValue[1];
		    mHighpassMagneticFieldValue[2] = event.values[2] - mLowpassMagneticFieldValue[2];

			getOrientation();
    	}
    };
    
    @SuppressLint("Wakelock")
	final static private class LightSensorReceiver implements SensorEventListener {
    	@Override
    	final public void onAccuracyChanged(Sensor sensor, int accuracy) {
    	}
    	
    	@Override
    	final public void onSensorChanged(SensorEvent event) {
			WakeLock wl=((PowerManager)mContext.getSystemService(Context.POWER_SERVICE))
					.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TaskAutomation-Light");
			wl.acquire();

    		mEnvParms.lightSensorValue=(int)(event.values[0]+0.5f);;
    		long sctm=System.currentTimeMillis();
    		int nv=0;
    		if (mLastLightSensorDetectedValue==1) {
        		if (mEnvParms.lightSensorValue<1) nv=0;
        		else nv=1;
    		} else {
        		if (mEnvParms.lightSensorValue>=mEnvParms.settingLightSensorDetectThreshHold) nv=1;
        		else nv=0;
    		}
    		if (nv!=mLastLightSensorDetectedValue) {
    			if ((mLastLightDetectedTime+
    					mEnvParms.settingLightSensorDetectIgnoreTime*1000)<sctm) {
    				if (nv==1) {
    					mEnvParms.lightSensorDetected=true;
    					scheduleBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_LIGHT_DETECTED);
    				} else {
    					mEnvParms.lightSensorDetected=false;
    					scheduleBuiltinEventTask(mBuiltinEventTaskList,BUILTIN_EVENT_LIGHT_UNDETECTED);
    				}

    				mLastLightDetectedTime=sctm;
    	    		mLastLightSensorDetectedValue=nv;
    			}
    		}
    		wl.release();
    	}
    };
    
    static private void startLightSensorReceiver() {
    	if (mEnvParms.settingLightSensorUseThread) startLightSensorReceiverThread();
    	else startLightSensorReceiverNonThread();
    };
    
    static private void stopLightSensorReceiver() {
    	if (mEnvParms.settingLightSensorUseThread) stopLightSensorReceiverThread();
    	else stopLightSensorReceiverNonThread();
    };
    
	static private void startLightSensorReceiverThread() {
		mTcLightSensorListener.setDisabled();
		if (mRequiredSensorLight && mSensorLight!=null && mEnvParms.settingEnableScheduler) {
			
			mEnvParms.lightSensorActive=true;
			mTcLightSensorListener.setEnabled();
			mThreadLightSensorListener=new Thread(){
				@Override
				public void run() {
					Thread.currentThread().setName("Light sensor receiver");
					Thread.currentThread().setPriority(Thread.NORM_PRIORITY+1);
					mUtil.addDebugMsg(1, "I", "Light sensor receiver thread has been started.");
					if (mEnvParms.lightSensorActive) {
						long mon_intv=mEnvParms.settingLightSensorMonitorIntervalTime-mEnvParms.settingLightSensorMonitorActiveTime;
						while (mTcLightSensorListener.isEnabled()) {
					        synchronized(mTcLightSensorListener) {
								try {
									if (mEnvParms.lightSensorActive) 
										mSensorManager.registerListener(mReceiverLligh, 
											mSensorLight, SensorManager.SENSOR_DELAY_UI);
				    		        mTcLightSensorListener.
				    		        	wait(mEnvParms.settingLightSensorMonitorActiveTime);
				    		        if (mEnvParms.lightSensorActive) 
				    		        	mSensorManager.unregisterListener(mReceiverLligh);
									mTcLightSensorListener.wait(mon_intv);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
					        }
						}
					}
					mEnvParms.lightSensorActive=false;
					mUtil.addDebugMsg(1, "I", "Light sensor receiver was terminated.",
		    				" Thread ID=",Thread.currentThread().getName());
				}
			};
			mThreadLightSensorListener.start();
		}
    };
    
    static private void stopLightSensorReceiverThread() {
    	if (mTcLightSensorListener.isEnabled()) {
    		mUtil.addDebugMsg(1, "I", "Stop light sensor receiver was issued.");
    		mTcLightSensorListener.setDisabled();
    		synchronized(mTcLightSensorListener) {
    			mTcLightSensorListener.notify();
    			mTcLightSensorListener.notify();
    			mTcLightSensorListener.notify();
    		}
    		try {
				if (mThreadLightSensorListener!=null) {
					mThreadLightSensorListener.join();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    };

	@SuppressLint("NewApi")
	static private void startLightSensorReceiverNonThread() {
		if (mRequiredSensorLight && mSensorLight!=null && mEnvParms.settingEnableScheduler) {
			mEnvParms.lightSensorActive=true;
			boolean result=mSensorManager.registerListener(mReceiverLligh, 
								mSensorLight, SensorManager.SENSOR_DELAY_UI);
			mUtil.addDebugMsg(1, "I", "Light sensor receiver was started. result="+result);
		}
    };
    
    static private void stopLightSensorReceiverNonThread() {
    	if (mEnvParms.lightSensorActive) {
    		mSensorManager.unregisterListener(mReceiverLligh);
    		mUtil.addDebugMsg(1, "I", "Light sensor receiver was stopped.");
    	}
    };

	static private void startProximitySensorReceiver() {
		if (mRequiredSensorProximity && mSensorProximity!=null && mEnvParms.settingEnableScheduler) {
			mEnvParms.proximitySensorActive=true;
			mSensorManager.registerListener(mReceiverProximity, mSensorProximity, SensorManager.SENSOR_DELAY_UI);
			mUtil.addDebugMsg(1, "I", "Proximity sensor receiver was started.");
		}
    };
    
    static private void stopProximitySensorReceiver() {
		if (mEnvParms.proximitySensorActive) {
			mSensorManager.unregisterListener(mReceiverProximity);
			mEnvParms.proximitySensorActive=false;
			mUtil.addDebugMsg(1, "I", "Proximity sensor receiver was stopped.");
		}
    };

	@SuppressWarnings("unused")
	static private void startMagneticFieldSensorReceiver() {
		mEnvParms.magneticFieldSensorActive=true;
		mSensorManager.registerListener(mReceiverMagneticField, mSensorMagneticField, SensorManager.SENSOR_DELAY_UI);
		mUtil.addDebugMsg(1, "I", "Magnetic-field sensor receiver was started.");
    };
    
    @SuppressWarnings("unused")
	static private void stopMagneticFieldSensorReceiver() {
		if (mEnvParms.magneticFieldSensorActive) {
			mSensorManager.unregisterListener(mReceiverMagneticField);
			mEnvParms.magneticFieldSensorActive=false;
			mUtil.addDebugMsg(1, "I", "Magnetic-field sensor receiver was stopped.");
		}
    };

	@SuppressWarnings("unused")
	static private void startAccelerometerSensorReceiver() {
		mEnvParms.accelerometerSensorAvailable=true;
		mSensorManager.registerListener(mReceiverAccelerometer, mSensorAccelerometer, SensorManager.SENSOR_DELAY_UI);
		mUtil.addDebugMsg(1, "I", "Accelerometer sensor receiver was started.");
    };
    
    @SuppressWarnings("unused")
	static private void stopAccelerometerSensorReceiver() {
    	if (mEnvParms.accelerometerSensorActive) {
    		mSensorManager.unregisterListener(mReceiverAccelerometer);
    		mEnvParms.accelerometerSensorActive=false;
    		mUtil.addDebugMsg(1, "I", "Accelerometer sensor receiver was stopped.");
    	}
    };

    static private void acqWakeLockForSleep() {
    	if (mEnvParms.settingDebugLevel>=3) mUtil.addDebugMsg(3, "I", "acqWakeLockForSleep Light="+mEnvParms.lightSensorActive,
//    			", Magnetic-field=",String.valueOf(mEnvParms.magneticFieldSensorActive)+
    			", Proximity=",String.valueOf(mEnvParms.proximitySensorActive),
    			", Telephony=",String.valueOf(mEnvParms.telephonyIsAvailable),
    			", Airplane=",String.valueOf(mEnvParms.airplane_mode_on),
    			", Held=",String.valueOf(mWakelockForSleep.isHeld()));
    	if (TaskManager.isAcqWakeLockRequired(mEnvParms)) {
    		if (!mWakelockForSleep.isHeld()) mWakelockForSleep.acquire();
    	}
    	if (mEnvParms.settingDebugLevel>=2) 
    		mUtil.addDebugMsg(2, "I", "acqWakeLockForSleep Result=",String.valueOf(mWakelockForSleep.isHeld()));
    };
    
    static private void relWakeLockForSleep() {
    	if (mWakelockForSleep.isHeld()) mWakelockForSleep.release();
    	if (mEnvParms.settingDebugLevel>=2) 
    		mUtil.addDebugMsg(2, "I", "relWakeLockForSleep released");
    };
    static private void acqSvcWakeLock() {
//    	if (!mWakelockSvcProcess.isHeld()) mWakelockSvcProcess.acquire();
    };
    static private void relSvcWakeLock() {
//    	if (mWakelockSvcProcess.isHeld()) mWakelockSvcProcess.release();
    };
    
    private static LinkedList<String> mBuiltinEventQueue=new LinkedList<String>();
//    private static boolean mBuiltinEventTaskThreadActive=false;
    final static private void scheduleBuiltinEventTask(
    		final ArrayList<TaskListItem> bietl, final String event) {
    	if (!mEnvParms.settingEnableScheduler) return;
		final WakeLock wl=((PowerManager)mContext.getSystemService(Context.POWER_SERVICE))
    			.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
    					| PowerManager.ON_AFTER_RELEASE, "TaskAutomation-executeBuiltinEventTask");
		wl.acquire();
		synchronized(mBuiltinEventQueue) {
			mBuiltinEventQueue.add(event);
		}
    	Runnable r=new Runnable(){
			@Override
			public void run() {
				synchronized(mBuiltinEventQueue) {
					while(!mBuiltinEventQueue.isEmpty()) {
						String eid=mBuiltinEventQueue.poll();
						scheduleBuiltinEventTaskThread(bietl, eid);
					}
				}
				wl.release();
			}
    	};
		mTaskMgrParms.normalTaskControlThreadPool.execute(r);

    };

    final static private synchronized void scheduleBuiltinEventTaskThread(
    		final ArrayList<TaskListItem> bietl, final String event) {
		TaskManager.acqLock(TaskManager.LOCK_ID_EVENT_TASK_LIST, TaskManager.LOCK_MODE_READ,mEnvParms,mTaskMgrParms,mUtil);
    	if (mEnvParms.settingDebugLevel>=1) mUtil.addDebugMsg(1,"I","executeBuitinEventTaskThread entered cnt=",
    			String.valueOf(bietl.size()),", event=",event);
    	long b_time=System.currentTimeMillis(),e_time=0;
    	long cnt_sched=0;
    	TaskLookupListItem tlui=getTaskListLookupItem(event);
    	if (tlui!=null) {
        	TaskListItem etl;
        	TaskListItem ati,tqli;
        	String btn;
        	for (int i=tlui.start_pos;i<(tlui.end_pos+1);i++) {
    			etl=bietl.get(i);
        		TaskManager.acqLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_READ,mEnvParms,mTaskMgrParms,mUtil);
    	    	btn=TaskManager.isEventIsBlocked(mTaskMgrParms,mEnvParms,mUtil,etl.group_name,event);
        		TaskManager.relLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_READ,mEnvParms,mTaskMgrParms,mUtil);
    	    	if (btn==null) {
    	    		ati=TaskManager.getActiveTaskListItem(mTaskMgrParms,mEnvParms,mUtil,etl.group_name,etl.task_name);
    	    		boolean already_started=false;
    	    		if (ati==null) {
    					tqli=TaskManager.getTaskQueueListItem(mTaskMgrParms,mEnvParms,mUtil,etl.group_name,etl.task_name);
    					if (tqli==null) {
        	    			cnt_sched++;
    						TaskManager.scheduleTask(mTaskMgrParms,mEnvParms,mUtil,etl);
    					} else already_started=true;
    				} else already_started=true;
    	    		if (already_started)
						mUtil.addLogMsg("W", String.format(mTaskMgrParms.svcMsgs.msgs_svc_task_already_started,
								etl.group_name,etl.event_name,etl.task_name));
    	    	} else {
    	    		mUtil.addLogMsg("I",String.format(mTaskMgrParms.svcMsgs.msgs_svc_action_blocked, 
    	    						etl.group_name,event, btn));
    	    	}
        	}
    	}
		e_time=System.currentTimeMillis()-b_time;
		TaskManager.relLock(TaskManager.LOCK_ID_EVENT_TASK_LIST, TaskManager.LOCK_MODE_READ,mEnvParms,mTaskMgrParms,mUtil);
		int a_time=0;
		if (cnt_sched>0) {
			a_time=(int)(e_time/cnt_sched);
			if (mEnvParms.statsHighAverageTaskScheduleTime<a_time) mEnvParms.statsHighAverageTaskScheduleTime=a_time;
		}
    	if (mEnvParms.settingDebugLevel>=1) 
    		mUtil.addDebugMsg(1,"I","executeBuitinEventTaskThread exited.",
    				" Scheduled task(s)=",String.valueOf(cnt_sched),
    				", Elapsed time=",String.valueOf(e_time), 
    				", Average elapsed time=",String.valueOf(a_time),
    				", Queued task=",String.valueOf(mTaskMgrParms.taskQueueList.size()),
    				", Event=",event);
    };
    
    final static private TaskLookupListItem getTaskListLookupItem(String event) {
    	TaskLookupListItem tlui=new TaskLookupListItem();
    	tlui.event_name=event;
    	int ltp=Collections.binarySearch(mBuiltinEventTaskLookupTable, tlui,
    			new Comparator<TaskLookupListItem>(){
			@Override
			public int compare(TaskLookupListItem arg0, TaskLookupListItem arg1) {
				return arg0.event_name.compareToIgnoreCase(arg1.event_name);
			}
    	});
    	if (ltp<0) return null;
    	else return mBuiltinEventTaskLookupTable.get(ltp);
    };
    
    final static private void scheduleTimeEventTask(ArrayList<TaskListItem> tetl) {
    	if (!mEnvParms.settingEnableScheduler) return;
    	long b_time=System.currentTimeMillis(),e_time=0;
    	long cnt_sched=0;
		TaskManager.acqLock(TaskManager.LOCK_ID_TIMER_TASK_LIST, TaskManager.LOCK_MODE_READ,mEnvParms,mTaskMgrParms,mUtil);
    	if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(2,"I","executeTimeEventTask entered cnt=",
    			String.valueOf(tetl.size()));
    	long ct=System.currentTimeMillis()/(60*1000)*(1000*60);
    	TaskListItem etl;
    	TaskListItem ati,tqli;
		for (int i=0;i<tetl.size();i++) {
			etl=tetl.get(i);
			if (etl.sched_time==ct) {
				etl.timer_update_required=true;
				ati=TaskManager.getActiveTaskListItem(mTaskMgrParms,mEnvParms,mUtil,etl.group_name,etl.task_name);
	    		boolean already_started=false;
	    		if (ati==null) {
					tqli=TaskManager.getTaskQueueListItem(mTaskMgrParms,mEnvParms,mUtil,etl.group_name,etl.task_name);
					if (tqli==null) {
		    			cnt_sched++;
						TaskManager.scheduleTask(mTaskMgrParms,mEnvParms,mUtil,etl);
					} else already_started=true;
				} else already_started=true;
	    		if (already_started)
					mUtil.addLogMsg("W", String.format(mTaskMgrParms.svcMsgs.msgs_svc_task_already_started,
							etl.group_name,etl.event_name,etl.task_name));
			}
		}
		e_time=System.currentTimeMillis()-b_time;
		TaskManager.relLock(TaskManager.LOCK_ID_TIMER_TASK_LIST, TaskManager.LOCK_MODE_READ,mEnvParms,mTaskMgrParms,mUtil);
		int a_time=0;
		if (cnt_sched>0) {
			a_time=(int)(e_time/cnt_sched);
			if (mEnvParms.statsHighAverageTaskScheduleTime<a_time) mEnvParms.statsHighAverageTaskScheduleTime=a_time;
		}
    	if (mEnvParms.settingDebugLevel>=1) 
    		mUtil.addDebugMsg(1,"I","executeTimeEventTask exited.",
    				" Scheduled task(s)=",String.valueOf(cnt_sched),
    				", Elapsed time=",String.valueOf(e_time), 
    				", Average elapsed time=",String.valueOf(a_time),
    				", Queued task=",String.valueOf(mTaskMgrParms.taskQueueList.size()));
    };

    static private void processThreadNegativeResponse(ThreadCtrl tc, Context c, TaskResponse tr) {
		mUtil.addLogMsg("I",
				"Negative response was received. task=",tr.active_task_name,
				", event=",tr.active_event_name,", action=",tr.active_action_name,
				", sub_resp=",tr.resp_id, ", msg=", tr.resp_msg_text);
		TaskManager.rescheduleTask(mTaskMgrParms,mEnvParms,mUtil);
	};

	static private void processThreadPositiveResponse(ThreadCtrl tc, Context c, TaskResponse tr) {
		if (mEnvParms.settingDebugLevel>=2) mUtil.addDebugMsg(2,"I", 
				"Positive response was received. task=", tr.active_task_name,
				", event=",tr.active_event_name, ", action=", tr.active_action_name,
				", dlg_id=",tr.active_dialog_id,
				", resp_id=", tr.resp_id, ", resp_code=", String.valueOf(tr.resp_code),
				", msg=", tr.resp_msg_text, ", target event=", tr.cmd_tgt_event_name,
				", target task=", tr.cmd_tgt_task_name, ", target action=", tr.cmd_tgt_action_name,
				", cmd_message_text=", tr.cmd_message_text);
		if (tr.resp_id.equals(CMD_THREAD_TO_SVC_FORCE_RESTART_SCHEDULER)) {
			restartScheduler();
		} else if (tr.resp_id.equals(CMD_THREAD_TO_SVC_RESET_INTERVAL_TIMER)) {
			rescheduleTimerEventTask(tr.cmd_tgt_event_name);
	    } else if (tr.resp_id.equals(CMD_THREAD_TO_SVC_START_TASK)) {
    		scheduleTaskEventTask(tr,mTaskEventTaskList);
	    } else if (tr.resp_id.equals(CMD_THREAD_TO_SVC_RESTART_SCHEDULER)) {
    		Intent in_b=
   				new Intent(mContext.getApplicationContext(),ActivityRestartScheduler.class);
    		in_b.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
    		mContext.startActivity(in_b);
	    }
	};
	
    static private boolean scheduleTaskEventTask(TaskResponse tr, ArrayList<TaskListItem> task_list) {
    	boolean started=false;
    	int tlsz=task_list.size();
    	TaskListItem etl;
    	for (int i=0;i<tlsz;i++) {
			etl=task_list.get(i);
			if (etl.group_name.equals(tr.active_group_name) && 
					etl.task_name.equals(tr.cmd_tgt_task_name)) {
				started=true;
	    		TaskListItem ati=TaskManager.getActiveTaskListItem(mTaskMgrParms,mEnvParms,mUtil,etl.group_name,etl.task_name);
				if (ati==null) {
					TaskManager.scheduleTask(mTaskMgrParms,mEnvParms,mUtil,etl);
//					initiateQueuedTask();
				} else mUtil.addLogMsg("W", 
						String.format(mTaskMgrParms.svcMsgs.msgs_svc_task_already_started,
							etl.group_name,
							tr.active_task_name,
							etl.task_name));
				break;
			}
    	}
    	return started;
    };

    static private void rescheduleTimerEventTask(String tgt_time_event_name) {
		TaskManager.acqLock(TaskManager.LOCK_ID_TIMER_TASK_LIST, TaskManager.LOCK_MODE_WRITE,mEnvParms,mTaskMgrParms,mUtil);
		updateTimerEventTaskListScheduleTime(mTimerEventTaskList,tgt_time_event_name);
		setTimeEventTaskTimer(mTimerEventTaskList);
		TaskManager.relLock(TaskManager.LOCK_ID_TIMER_TASK_LIST, TaskManager.LOCK_MODE_WRITE,mEnvParms,mTaskMgrParms,mUtil);
    };
    
	static private void setTimeEventTaskTimer(ArrayList<TaskListItem>tetl) {
//		TaskManager.acqTaskEventListLock(taskMgrParms,util);
    	if (mEnvParms.settingDebugLevel!=0) 
    		mUtil.addDebugMsg(2,"I","scheduleNextWakeUp entered, candidate action count=",
    				String.valueOf(tetl.size()));
		long min_time=0;
		mEnvParms.nextScheduleTime=0;
		cancelTimer(BROADCAST_TIMER_EXPIRED);
		long ct=(System.currentTimeMillis()/(60*1000))*(60*1000);
		int tesz=tetl.size();
		TaskListItem etl;
		String stl="",sep="";
		for (int i=0;i<tesz;i++) {
			etl=tetl.get(i);
			if (etl.sched_time>ct) {
				if (min_time==0 || etl.sched_time<min_time) {
					min_time=etl.sched_time;
				}
//				Log.v("","sd="+StringUtil.convDateTimeTo_YearMonthDayHourMin(etl.sched_time));
				stl+=sep+tetl.get(i).group_name+" "+tetl.get(i).task_name+" "+
						StringUtil.convDateTimeTo_YearMonthDayHourMin(etl.sched_time);
				sep="\n";
//				mEnvParms.scheduledTimerTaskList[i]=tetl.get(i).group_name+" "+tetl.get(i).task_name+
//						" "+tetl.get(i).sched_month+"/"+tetl.get(i).sched_day+
//						" "+tetl.get(i).sched_hour+":"+tetl.get(i).sched_min;
			}
		}
		
		if (!stl.equals("")) mEnvParms.scheduledTimerTaskList=stl.split("\n");
		if (min_time>0) {
			setTimer(BROADCAST_TIMER_EXPIRED,min_time);
			mEnvParms.nextScheduleTime=min_time;
			mEnvParms.nextScheduleTimeString=StringUtil.convDateTimeTo_MonthDayHourMin(mEnvParms.nextScheduleTime);
		}
		TaskManager.showNotification(mTaskMgrParms, mEnvParms, mUtil);
//		TaskManager.relTaskEventListLock(taskMgrParms,util);
    };

    static private void buildEventTaskList(
			ArrayList<ProfileListItem> prof_list, 
			ArrayList<TaskListItem>bietl, ArrayList<TaskListItem>tetl) {
		bietl.clear();
		tetl.clear();
		int tpsz=prof_list.size();
		for (int task_idx=0;task_idx<tpsz;task_idx++) {
			ProfileListItem tpli=prof_list.get(task_idx);
//			util.addDebugMsg(1, "I", "Profile Type="+tpli.getProfileType()+
//					", Group="+tpli.getProfileGroup()+", Name="+tpli.getProfileName()+
//					", GroupActivated="+tpli.isProfileGroupActivated()+
//					", isEnabled="+tpli.isProfileEnabled());
			if (tpli.isProfileGroupActivated() &&
					tpli.getProfileType().equals(PROFILE_TYPE_TASK) &&
					tpli.isProfileEnabled()) {
				ArrayList<String> tel=tpli.getTaskTriggerList();
				TaskListItem etl=new TaskListItem();
				etl.group_name=tpli.getProfileGroup();
				etl.profile_update_time=tpli.getProfileUpdateTime();
				etl.task_name=tpli.getProfileName();
				for (int event_idx=0;event_idx<tel.size();event_idx++) {
					if (tpli.getTaskTriggerList().get(event_idx).startsWith(BUILTIN_PREFIX) ||
							tpli.getTaskTriggerList().get(event_idx).equals(TRIGGER_EVENT_TASK)) {
						String event_id=tpli.getTaskTriggerList().get(event_idx);
						etl.event_name=event_id;
						ArrayList<String> tal=tpli.getTaskActionList();
						int talsz=tal.size();
						for (int act_idx=0;act_idx<talsz;act_idx++) {
							//Add exec list
							if (tal.get(act_idx).startsWith(BUILTIN_PREFIX)){
								checkSensorUsedStatusByConditionalAction(tal.get(act_idx));
								setTaskExecActionItem(etl.taskActionList,
										event_id,tpli,tal.get(act_idx),null);
							} else {
								ProfileListItem apli=getActionProfileItem(prof_list,
										tpli.getProfileGroup(),tal.get(act_idx));
								if (apli!=null) {
									//Add exec list
									checkSensorUsedStatusByConditionalAction(tal.get(act_idx));
									setTaskExecActionItem(etl.taskActionList,
											event_id,tpli,tal.get(act_idx),apli);
								} else {
				    				mUtil.addLogMsg("E","Action profile is disabled or not found",
				    						", group=",etl.group_name,
				    						", task_prof=",etl.task_name,
				    						", action_prof=",tal.get(act_idx));
								}
							}
						}
					} 					
				}
				if (etl.taskActionList.size()!=0) {
					checkSensorUsedStatusByTriggerEvent(etl.event_name);
					if (etl.event_name.startsWith(BUILTIN_PREFIX)) bietl.add(etl);
					else tetl.add(etl);
				}
			}
		}
		Collections.sort(bietl,new Comparator<TaskListItem>(){
			@Override
			public int compare(TaskListItem cl, TaskListItem nl) {
				return cl.event_name.compareTo(nl.event_name);
			}
		});
		Collections.sort(tetl,new Comparator<TaskListItem>(){
			@Override
			public int compare(TaskListItem cl, TaskListItem nl) {
				return cl.event_name.compareTo(nl.event_name);
			}
		});
		if (mEnvParms.settingDebugLevel!=0) {
			mUtil.addDebugMsg(1,"I", "buildEventTaskList required sensor" ,
					", Accelerometer=",String.valueOf(mRequiredSensorAccelerometer),
					", Proximity=",String.valueOf(mRequiredSensorProximity),
					", Light=",String.valueOf(mRequiredSensorLight),
					", requiredBatteryLevel=", String.valueOf(mRequiredBatteryLevelExecution));			
			mUtil.addDebugMsg(1,"I", 
					"buildEventTaskList result, builtin=",
					String.valueOf(bietl.size()), ", task=", String.valueOf(tetl.size()));
		}
	};

	static private void checkSensorUsedStatusByTriggerEvent(String trig) {
		if (trig.equals(BUILTIN_EVENT_LIGHT_DETECTED) || 
				trig.equals(BUILTIN_EVENT_LIGHT_UNDETECTED)) mRequiredSensorLight=true;
		if (trig.equals(BUILTIN_EVENT_PROXIMITY_DETECTED) || 
				trig.equals(BUILTIN_EVENT_PROXIMITY_UNDETECTED)) mRequiredSensorProximity=true;
		if (trig.equals(BUILTIN_EVENT_BATTERY_LEVEL_CHANGED))
				mRequiredBatteryLevelExecution=true;
	};
	
	static private void checkSensorUsedStatusByConditionalAction(String act) {
		if (act.equals(BUILTIN_ACTION_ABORT_IF_LIGHT_DETECTED) || 
				act.equals(BUILTIN_ACTION_ABORT_IF_LIGHT_UNDETECTED)) mRequiredSensorLight=true;
		if (act.equals(BUILTIN_ACTION_ABORT_IF_PROXIMITY_DETECTED) || 
				act.equals(BUILTIN_ACTION_ABORT_IF_PROXIMITY_UNDETECTED)) mRequiredSensorProximity=true;
	};
	
	static private void setTaskExecActionItem(ArrayList<TaskActionItem> beeal, 
			String event_id, ProfileListItem tpli, 
			String apn, ProfileListItem apli) {
		TaskActionItem bial= new TaskActionItem();

		bial.action_name=apn;
		bial.action_dialog_id=String.valueOf(beeal.size()+1);
		if (apn.startsWith(BUILTIN_PREFIX)) {
			bial.action_type=PROFILE_ACTION_TYPE_BUILTIN;
			bial.action_builtin_action=apn;
			if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(2, "I","Task action list added",
					", event=",event_id,
					", group=",tpli.getProfileGroup(),
					", task_prof=",tpli.getProfileName(),
					", action_prof=",apn,
					", action_type=",bial.action_type,
					", builtin action=",bial.action_builtin_action
					);
		} else {
			bial.profile_update_time=apli.getProfileUpdateTime();
			bial.action_type=apli.getActionType();
			if (bial.action_type.equals(PROFILE_ACTION_TYPE_ACTIVITY)) {
				bial.action_activity_pkgname=apli.getActionActivityPackageName();
				bial.action_activity_name=apli.getActionActivityName();
				bial.action_activity_data_type=apli.getActionActivityDataType();
				bial.action_activity_data_uri=apli.getActionActivityUriData();
				bial.action_activity_data_extra_list=apli.getActionActivityExtraData();
				if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(2, "I","Task action list added",
						", event=",event_id,
						", group=",tpli.getProfileGroup(),
						", task_prof=",tpli.getProfileName(),
						", action_prof=",apn,
						", action_type=",bial.action_type,
						", activity_name=",bial.action_activity_name,
						", package=",bial.action_activity_pkgname,
						", data_type=",bial.action_activity_data_type,
						", data_uri=",bial.action_activity_data_uri
						);
				for (int i=0;i<bial.action_activity_data_extra_list.size();i++) {
					ActivityExtraDataItem aedi=bial.action_activity_data_extra_list.get(i);
					mUtil.addDebugMsg(2, "I","      Extra data : Key="+aedi.key_value,
							", type=",aedi.data_type,", array=",aedi.data_value_array,", data=",aedi.data_value);
				}
			} else if (bial.action_type.equals(PROFILE_ACTION_TYPE_MUSIC)) {
				bial.action_sound_file_name=apli.getActionSoundFileName();
				bial.action_sound_vol_left=apli.getActionSoundVolLeft();
				bial.action_sound_vol_right=apli.getActionSoundVolRight();
				if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(2, "I","Task action list added",
						", event=",event_id,
						", group=",tpli.getProfileGroup(),
						", task_prof=",tpli.getProfileName(),
						", action_prof=",apn,
						", action_type=",bial.action_type,
						", file_name=",bial.action_sound_file_name,
						", sound_vol_left=",bial.action_sound_vol_left,
						", sound_vol_right=",bial.action_sound_vol_right);
	
			} else if (bial.action_type.equals(PROFILE_ACTION_TYPE_RINGTONE)) {
				bial.action_ringtone_type=apli.getActionRingtoneType();
				bial.action_ringtone_name=apli.getActionRingtoneName();
				bial.action_ringtone_path=apli.getActionRingtonePath();
				bial.action_ringtone_vol_left=apli.getActionRingtoneVolLeft();
				bial.action_ringtone_vol_right=apli.getActionRingtoneVolRight();
				if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(2, "I","Task action list added",
						", event=",event_id,
						", group=",tpli.getProfileGroup(),
						", task_prof=",tpli.getProfileName(),
						", action_prof=",apn,
						", action_type=",bial.action_type,
						", ringtone_type=",bial.action_ringtone_type,
						", ringtone_name=",bial.action_ringtone_name,
						", ringtone_path=",bial.action_ringtone_path,
						", ringtone_vol_left=",bial.action_ringtone_vol_left,
						", ringtone_vol_right=",bial.action_ringtone_vol_right);
			} else if (bial.action_type.equals(PROFILE_ACTION_TYPE_COMPARE)) {
				bial.action_compare_target=apli.getActionCompareTarget();
				bial.action_compare_type=apli.getActionCompareType();
				bial.action_compare_value=apli.getActionCompareValue();
				bial.action_compare_result_action=apli.getActionCompareResultAction();
				if (bial.action_compare_target.equals(PROFILE_ACTION_TYPE_COMPARE_TARGET_LIGHT)) 
					mRequiredSensorLight=true;
				if (mEnvParms.settingDebugLevel!=0) {
					String c_val="", sep="";
					for (int i=0;i<bial.action_compare_value.length;i++) {
						if (bial.action_compare_value[i]!=null && !bial.action_compare_value[i].equals("")) {
							c_val+=sep+bial.action_compare_value[i];
							sep=",";
						}
					}
					mUtil.addDebugMsg(2, "I","Task action list added",
							", event=",event_id,
							", group=",tpli.getProfileGroup(),
							", task_prof=",tpli.getProfileName(),
							", action_prof=",apn,
							", action_type=",bial.action_type,
							", compare target=",bial.action_compare_target,
							", compare type=",bial.action_compare_type,
							", value=",c_val,
							", result action=",bial.action_compare_result_action
							);
				}
			} else if (bial.action_type.equals(PROFILE_ACTION_TYPE_MESSAGE)) {
				bial.action_message_type=apli.getActionMessageType();
				bial.action_message_text=apli.getActionMessageText();
				bial.action_message_use_vib=apli.isActionMessageUseVibration();
				bial.action_message_use_led=apli.isActionMessageUseLed();
				bial.action_message_led_color=apli.getActionMessageLedColor();
				if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(2, "I","Task action list added",
						", event=",event_id,
						", group=",tpli.getProfileGroup(),
						", task_prof=",tpli.getProfileName(),
						", action_prof=",apn,
						", action_type=",bial.action_type,
						", dlg_id=",String.valueOf(bial.action_dialog_id),
						", message type=",bial.action_message_type,
						", vibration=",String.valueOf(bial.action_message_use_vib),
						", LED=",String.valueOf(bial.action_message_use_led),
						", Color=",String.valueOf(bial.action_message_led_color)
						);
			} else if (bial.action_type.equals(PROFILE_ACTION_TYPE_TIME)) {
				bial.action_time_type=apli.getActionTimeType();
				bial.action_time_target=apli.getActionTimeTarget();
				if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(2, "I","Task action list added",
						", event=",event_id,
						", group=",tpli.getProfileGroup(),
						", task_prof=",tpli.getProfileName(),
						", action_prof=",apn,
						", action_type=",bial.action_type,
						", time type=",bial.action_time_type,
						", target=",bial.action_time_target
						);
			} else if (bial.action_type.equals(PROFILE_ACTION_TYPE_TASK)) {
				bial.action_task_type=apli.getActionTaskType();
				bial.action_task_target=apli.getActionTaskTarget();
				if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(2, "I","Task action list added",
						", event=",event_id,
						", group=",tpli.getProfileGroup(),
						", task_prof=",tpli.getProfileName(),
						", action_prof=",apn,
						", action_type=",bial.action_type,
						", task type=",bial.action_task_type,
						", target=",bial.action_task_target
						);
			} else if (bial.action_type.equals(PROFILE_ACTION_TYPE_WAIT)) {
				bial.action_wait_target=apli.getActionWaitTarget();
				bial.action_wait_timeout_value=apli.getActionWaitTimeoutValue();
				bial.action_wait_timeout_units=apli.getActionWaitTimeoutUnits();
				if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(2, "I","Task action list added",
						", event=",event_id,
						", group=",tpli.getProfileGroup(),
						", task_prof=",tpli.getProfileName(),
						", action_prof=",apn,
						", action_type=",bial.action_type,
						", target=",bial.action_wait_target,
						", timeout_value=",bial.action_wait_timeout_value,
						", timeout_units=",bial.action_wait_timeout_units
						);
			} else if (bial.action_type.equals(PROFILE_ACTION_TYPE_BSH_SCRIPT)) {
				bial.action_bsh_script=apli.getActionBeanShellScriptScript();
				if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(2, "I","Task action list added",
						", event=",event_id,
						", group=",tpli.getProfileGroup(),
						", task_prof=",tpli.getProfileName(),
						", action_prof=",apn,
						", action_type=",bial.action_type,"\n",
						"Script:","\n",bial.action_bsh_script
						);
			} else if (bial.action_type.equals(PROFILE_ACTION_TYPE_SHELL_COMMAND)) {
				bial.action_shell_cmd=apli.getActionShellCmd();
				bial.action_shell_cmd_with_su=apli.isActionShellCmdWithSu();
				if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(2, "I","Task action list added",
						", event=",event_id,
						", group=",tpli.getProfileGroup(),
						", task_prof=",tpli.getProfileName(),
						", action_prof=",apn,
						", action_type=",bial.action_type,
						", Shell cmd SU="+bial.action_shell_cmd_with_su,"\n",
						"shell cmd:","\n",bial.action_shell_cmd
						);
			}
		}
		beeal.add(bial);

	};
	
	static private ProfileListItem getActionProfileItem(
			ArrayList<ProfileListItem> prof_list,
			String grp, String name) {
		ProfileListItem result=null;
		int apsz=prof_list.size();
		for (int i=0;i<apsz;i++) {
//			prof_list.get(i).dumpProfile();
			if (prof_list.get(i).isProfileGroupActivated() &&
					prof_list.get(i).getProfileType().equals(PROFILE_TYPE_ACTION) &&
					prof_list.get(i).getProfileGroup().equals(grp) &&
					prof_list.get(i).getProfileName().equals(name)) {
				if (prof_list.get(i).isProfileEnabled()) {
					result=prof_list.get(i);
				}
				break;
			}
		}
		return result;
	};

	@SuppressWarnings("unused")
	static private ProfileListItem getTaskProfileItem(
			ArrayList<ProfileListItem> prof_list,
			String grp, String name) {
		ProfileListItem result=null;
		int apsz=prof_list.size();
		for (int i=0;i<apsz;i++) {
			if (prof_list.get(i).isProfileGroupActivated() &&
					prof_list.get(i).getProfileType().equals(PROFILE_TYPE_TASK) &&
					prof_list.get(i).getProfileGroup().equals(grp) &&
					prof_list.get(i).getProfileName().equals(name)) {
				if (prof_list.get(i).isProfileEnabled()) {
					result=prof_list.get(i);
				}
				break;
			}
		}
		return result;
	};
	
	static private void buildTimeEventTaskList(
			ArrayList<ProfileListItem> prof_list,
			ArrayList<TaskListItem>tetl) {
		tetl.clear();
		int tesz=prof_list.size();
		for (int time_idx=0;time_idx<tesz;time_idx++) {
			ProfileListItem time_profile_item = prof_list.get(time_idx);
			if (time_profile_item.isProfileGroupActivated() &&
					time_profile_item.getProfileType().equals(PROFILE_TYPE_TIME) &&
					time_profile_item.isProfileEnabled()) {
				String tp_grp=time_profile_item.getProfileGroup();
				int tpsz=prof_list.size();
			    for (int task_idx=0;task_idx<tpsz;task_idx++) {
//			    	util.addDebugMsg(1,"I","Time profile name="+tpli.getProfileName()+
//			    			", active="+taskProfile.get(j).isProfileEnabled()+
//			    			", Task profile trigger="+taskProfile.get(j).getTaskTrigger().get(0));
		    		ProfileListItem task_profile_item = prof_list.get(task_idx);
			    	if (task_profile_item.getProfileGroup().equals(tp_grp) &&
			    			task_profile_item.isProfileGroupActivated() &&
			    			task_profile_item.getProfileType().equals(PROFILE_TYPE_TASK) &&
			    			task_profile_item.isProfileEnabled()) {
						TaskListItem etl=new TaskListItem(); 
						etl.time_type=time_profile_item.getTimeType();
						etl.day_of_the_week=time_profile_item.getTimeDayOfTheWeek();
						etl.sched_yyyy=time_profile_item.getTimeDate().substring(0,4);
						etl.sched_month=time_profile_item.getTimeDate().substring(5,7);
						etl.sched_day=time_profile_item.getTimeDate().substring(8,10);
						etl.sched_hour=time_profile_item.getTimeTime().substring(0,2);
						etl.sched_min=time_profile_item.getTimeTime().substring(3,5);
						etl.task_name=task_profile_item.getProfileName();
						etl.group_name=task_profile_item.getProfileGroup();
						etl.event_name=time_profile_item.getProfileName();
						etl.profile_update_time=time_profile_item.getProfileUpdateTime();
				    	ArrayList<String> task_trigger=task_profile_item.getTaskTriggerList();
				    	for (int e_idx=0;e_idx<task_trigger.size();e_idx++) {
							if (time_profile_item.getProfileName().equals(task_trigger.get(e_idx))) {
					    		setTimeEventExecActionList(task_profile_item,
					    				prof_list,
					    				etl.taskActionList,
					    				time_profile_item.getProfileGroup(),
					    				time_profile_item.getProfileName(),
					    				task_profile_item.getProfileName(),
					    				task_profile_item.getTaskActionList());
						    	if (etl.taskActionList.size()!=0) tetl.add(etl);
					    	}
				    	}
			    	}
			    }
			}
		}
		if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(1,"I", "buildTimeEventExecList result, task=",String.valueOf(tetl.size()));
	};

	static private void setTimeEventExecActionList(ProfileListItem task_profile_item,
			ArrayList<ProfileListItem> prof_list,
			ArrayList<TaskActionItem> eal,
			String e_grp, String event_id, 
			String task_name, 
			ArrayList<String> tal) {
		for (int al_idx=0;al_idx<tal.size();al_idx++) {
			//Add exec list
			if (tal.get(al_idx).startsWith(BUILTIN_PREFIX)){
				checkSensorUsedStatusByConditionalAction(tal.get(al_idx));
				setTaskExecActionItem(eal,event_id,task_profile_item,tal.get(al_idx),null);
			} else {
				ProfileListItem apli=getActionProfileItem(prof_list,e_grp,tal.get(al_idx));
				if (apli!=null) {
					//Add exec list
					checkSensorUsedStatusByConditionalAction(tal.get(al_idx));
					setTaskExecActionItem(eal,event_id,task_profile_item,tal.get(al_idx),apli);
				} else {
    				mUtil.addLogMsg("E","Action profile is disabled or not found",
    						", group=",e_grp,
    						", task_prof=",task_name,
    						", action_prof=",tal.get(al_idx));
				}
			}
		}
	};

	static private void updateTimerEventTaskListScheduleTime(ArrayList<TaskListItem>tetl, String s_ev) {
		long ct=System.currentTimeMillis();
		String c_yyyymmddhhmm=StringUtil.convDateTimeTo_YearMonthDayHourMin(ct);
		
//		c_day_of_the_week=sdfDayOfTheWeek.format(System.currentTimeMillis());
		String c_yyyy=c_yyyymmddhhmm.substring(0,4);
		String c_month=c_yyyymmddhhmm.substring(5,7);
		String c_day=c_yyyymmddhhmm.substring(8,10);
		String c_hour=c_yyyymmddhhmm.substring(11,13);
		String c_min=c_yyyymmddhhmm.substring(14,16);
//		Log.v("","y="+c_yyyy+", m="+c_month+", d="+c_day+", h="+c_hour+", min="+c_min);
		
		int tetlsz=tetl.size();
		for (int i=0;i<tetlsz;i++) {
			TaskListItem etl=tetl.get(i);
	    	mUtil.addDebugMsg(1, "I","Update timer group="+etl.group_name+", name=",etl.event_name,
	    			", type=",etl.time_type);
			if ((etl.timer_update_required && s_ev==null) || etl.event_name.equals(s_ev)) {
				long dw_off=0;//, prev_sched=0;
			    long next_s_time = 0;
			    etl.sched_time=-1;
				if (etl.time_type.equals(PROFILE_DATE_TIME_TYPE_ONE_SHOT)) {
					next_s_time=convertEpocToUtc(etl.sched_yyyy,etl.sched_month,etl.sched_day,etl.sched_hour,etl.sched_min)+dw_off;
					mUtil.addDebugMsg(2, "I","    s_yyyy=", etl.sched_yyyy, ", s_month=", etl.sched_month+", s_day=", etl.sched_day, ", s_hour=", etl.sched_hour, ", s_min=", etl.sched_min, ", offset=", String.valueOf(dw_off));					
				} else if (etl.time_type.equals(PROFILE_DATE_TIME_TYPE_DAY_OF_THE_WEEK)) {
					dw_off=getDayOfTheWeekOffset(
							etl.day_of_the_week, etl.sched_hour,etl.sched_min,c_hour,c_min);
					dw_off=dw_off*24*60*60*1000;
					String s_yyyy=c_yyyy;
					String s_month=c_month;
					String s_day=c_day;
					next_s_time=convertEpocToUtc(s_yyyy,s_month,s_day,etl.sched_hour,etl.sched_min)+dw_off;
					mUtil.addDebugMsg(2, "I","    s_yyyy=",s_yyyy,", s_month=",s_month,", s_day=",s_day,", s_hour=",etl.sched_hour,", s_min=",etl.sched_min,", offset=",String.valueOf(dw_off));
				} else if (etl.time_type.equals(PROFILE_DATE_TIME_TYPE_EVERY_YEAR)) {
					String s_yyyy=c_yyyy;
					if (convertEpocToUtc(s_yyyy,etl.sched_month,etl.sched_day,etl.sched_hour,etl.sched_min)<=
							convertEpocToUtc(c_yyyy,c_month,c_day,c_hour,c_min)) {
						s_yyyy=""+(Integer.parseInt(s_yyyy)+1);
					}
					next_s_time=convertEpocToUtc(s_yyyy,etl.sched_month,etl.sched_day,etl.sched_hour,etl.sched_min)+dw_off;
					mUtil.addDebugMsg(2, "I","    s_yyyy=",s_yyyy,", s_month=",etl.sched_month,", s_day=",etl.sched_day,", s_hour=",etl.sched_hour,", s_min=",etl.sched_min,", offset=",String.valueOf(dw_off));
				} else if (etl.time_type.equals(PROFILE_DATE_TIME_TYPE_EVERY_MONTH)) {
					String s_yyyy=c_yyyy;
					String s_month=c_month;
					if (convertEpocToUtc(s_yyyy,s_month,etl.sched_day,etl.sched_hour,etl.sched_min)<=
							convertEpocToUtc(c_yyyy,c_month,c_day,c_hour,c_min)) {
						if (s_month.equals("12")) {
							s_yyyy=""+(Integer.parseInt(s_yyyy)+1);
							s_month="01";
						} else {
							s_month=""+(Integer.parseInt(s_month)+1);
						}
					}
					next_s_time=convertEpocToUtc(s_yyyy,s_month,etl.sched_day,etl.sched_hour,etl.sched_min)+dw_off;
					mUtil.addDebugMsg(2, "I","    s_yyyy=",s_yyyy,", s_month=",s_month,", s_day=",etl.sched_day,", s_hour=",etl.sched_hour,", s_min=",etl.sched_min,", offset=",String.valueOf(dw_off));
				} else if (etl.time_type.equals(PROFILE_DATE_TIME_TYPE_EVERY_DAY)) {
					String s_yyyy=c_yyyy;
					String s_month=c_month;
					String s_day=c_day;
					if ((Integer.parseInt(etl.sched_hour)*60+Integer.parseInt(etl.sched_min))<=
							(Integer.parseInt(c_hour)*60+Integer.parseInt(c_min))) 
						dw_off=1*24*60*60*1000;
					next_s_time=convertEpocToUtc(s_yyyy,s_month,s_day,etl.sched_hour,etl.sched_min)+dw_off;
					mUtil.addDebugMsg(2, "I","    s_yyyy=",s_yyyy,", s_month=",s_month,", s_day=",s_day,", s_hour=",etl.sched_hour,", s_min=",etl.sched_min,", offset=",String.valueOf(dw_off));
				} else if (etl.time_type.equals(PROFILE_DATE_TIME_TYPE_EVERY_HOUR)) {
					String s_yyyy=c_yyyy;
					String s_month=c_month;
					String s_day=c_day;
					String s_hour=c_hour;
					if (Integer.parseInt(etl.sched_min)<=Integer.parseInt(c_min)) dw_off=1*60*60*1000; 
					next_s_time=convertEpocToUtc(s_yyyy,s_month,s_day,s_hour,etl.sched_min)+dw_off;
					mUtil.addDebugMsg(2, "I","    s_yyyy=",s_yyyy,", s_month=",s_month,", s_day=",s_day,", s_hour=",s_hour,", s_min=",etl.sched_min,", offset=",String.valueOf(dw_off));					
				} else if (etl.time_type.equals(PROFILE_DATE_TIME_TYPE_INTERVAL)) {
					String s_yyyy=c_yyyy;
					String s_month=c_month;
					String s_day=c_day;
					String s_hour=c_hour;
					String s_min=c_min;
					dw_off=(Integer.valueOf(etl.sched_hour)*60+Integer.valueOf(etl.sched_min))*60*1000;
					etl.sched_day=c_day;
					etl.sched_month=c_month;
					next_s_time=convertEpocToUtc(s_yyyy,s_month,s_day,s_hour,s_min)+dw_off;
					mUtil.addDebugMsg(2, "I","    s_yyyy=",s_yyyy,", s_month=",s_month,", s_day=",s_day,", s_hour=",s_hour,", s_min=",s_min,", offset=",String.valueOf(dw_off));
				}
			    mUtil.addDebugMsg(1, "I","    Next_schedule_time="+StringUtil.convDateTimeTo_YearMonthDayHourMin(next_s_time)+
			    		", current_time="+StringUtil.convDateTimeTo_YearMonthDayHourMin(ct));
			    if (next_s_time>ct) {
			    	etl.sched_time=next_s_time;
				    mUtil.addDebugMsg(1, "I","    Timer was updated, required=",String.valueOf(etl.timer_update_required),
						", selected=",s_ev);
			    } else {
				    mUtil.addDebugMsg(1, "I","    Timer was not scheduled because timer was past, required=",String.valueOf(etl.timer_update_required),
						", selected=",s_ev);
			    	
			    }
				etl.timer_update_required=false;
			} else {
			    mUtil.addDebugMsg(1, "I","    Next_schedule_time=",StringUtil.convDateTimeTo_YearMonthDayHourMin(etl.sched_time),
			    			", current_time=",StringUtil.convDateTimeTo_YearMonthDayHourMin(ct));
				mUtil.addDebugMsg(1, "I","    Timer was not updated, required=",String.valueOf(etl.timer_update_required),
						", selected=",s_ev);
			}
		}
		if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(1,"I", "updateScheduleTime result, action count=",String.valueOf(tetl.size()));
	};
	
	static private long convertEpocToUtc(String year, String month, String day,
			String hour, String min) {
		Calendar calendar = Calendar.getInstance();
	    calendar.clear();
	    calendar.set(Integer.parseInt(year), Integer.parseInt(month)-1, 
	    		Integer.parseInt(day), Integer.parseInt(hour), Integer.parseInt(min), 0);
	    return calendar.getTimeInMillis();
	};
	
	static private long getDayOfTheWeekOffset(String dw, 
			String s_hour, String s_min, String c_hour, String c_min) {
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_WEEK);

		long dw_off=0;
		String ndw="";
		if (dw.substring(day-1,day).equals("1")) {
			if ((Integer.parseInt(s_hour)*60+Integer.parseInt(s_min))>
				(Integer.parseInt(c_hour)*60+Integer.parseInt(c_min))) {
				dw_off=0;
			} else {//Search next day
				if (day==7) {
					ndw=dw.substring(0,6);
				} else {
					ndw=dw.substring(day,7)+dw.substring(0,day);
				}
				for (int j=0;j<ndw.length();j++) {
					if (ndw.substring(j,j+1).equals("1")) {
						dw_off=j+1;
						break;
					}
				}
			}
		} else {//Search next day
			if (day==7) {
				ndw=dw.substring(0,6);
			} else {
				ndw=dw.substring(day,7)+dw.substring(0,day);
			}
			for (int j=0;j<ndw.length();j++) {
				if (ndw.substring(j,j+1).equals("1")) {
					dw_off=j+1;
					break;
				}
			}
		}
		return dw_off;
	};
	
	static private boolean isProfileFileExisted() {
		File lf;
		String pf = PROFILE_FILE_NAME;
		lf= new File(mContext.getFilesDir().toString()+"/"+pf);
		return lf.exists();
	};
	
	static private ArrayList<ProfileListItem> buildProfileList() {
		ArrayList<ProfileListItem>task=new ArrayList<ProfileListItem>();
		ArrayList<ProfileListItem>time=new ArrayList<ProfileListItem>();
		ArrayList<ProfileListItem>action=new ArrayList<ProfileListItem>();
		
		ArrayList<ProfileListItem>prof_list=new ArrayList<ProfileListItem>();

		BufferedReader br;
		String pf = PROFILE_FILE_NAME; 
		try {
			File lf;
			lf= new File(mContext.getFilesDir().toString()+"/"+pf);
			
			if (lf.exists()) {
				br = new BufferedReader(
						new FileReader(mContext.getFilesDir().toString()+"/"+pf),
						GENERAL_FILE_BUFFER_SIZE); 
				String pl;
				while ((pl = br.readLine()) != null) {
					ProfileUtilities.parseProfileList(pl, "", task, time, action);
				}
				br.close();
			} else {
				if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(1, "W", 
						"profile not found, empty profile list created. fn=",
								mContext.getFilesDir().toString(),"/",pf);
			}
		} catch (IOException e) {
			e.printStackTrace();
			mUtil.addLogMsg("E",String.format(mTaskMgrParms.svcMsgs.msgs_create_profile_error,pf));
			mUtil.addLogMsg("E",e.toString());
		}

		prof_list.addAll(task);
		prof_list.addAll(time);
		prof_list.addAll(action);
		ProfileUtilities.sortProfileArrayList(mUtil,prof_list);

		if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(1,"I","buildProfileList result",
				", task=",String.valueOf(task.size()),
				", time=",String.valueOf(time.size()),
				", action=",String.valueOf(action.size())
				);
		return prof_list;
	};
	
    @SuppressLint("NewApi")
	static private void setTimer(String action, long time ) {
    	if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(1,"I", 
    			"setTimer entered, time=",StringUtil.convDateTimeTo_YearMonthDayHourMin(time));
		Intent iw = new Intent();
		iw.setAction(action);
		iw.putExtra("date_time",time);
		PendingIntent piw = PendingIntent.getBroadcast(mContext, 0, iw,
				PendingIntent.FLAG_UPDATE_CURRENT);
	    AlarmManager amw = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
	    if (Build.VERSION.SDK_INT>=23) amw.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, piw);
	    else amw.set(AlarmManager.RTC_WAKEUP, time, piw);
    };

    static private void cancelTimer(String action) {
    	if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(1,"I", "cancelTimer entered");
		Intent iw = new Intent();
		iw.setAction(action);
		PendingIntent piw = PendingIntent.getBroadcast(mContext, 0, iw,
				PendingIntent.FLAG_UPDATE_CURRENT);
	    AlarmManager amw = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
	    amw.cancel(piw);
    };
    
    @SuppressLint("NewApi")
	final static private void setHeartBeat(Context context) {
//		Intent iw = new Intent();
    	Intent iw = new Intent(context,SchedulerService.class);
		iw.setAction(BROADCAST_SERVICE_HEARTBEAT);
		long time=System.currentTimeMillis()+mEnvParms.settingHeartBeatIntervalTime;
//		PendingIntent piw = PendingIntent.getBroadcast(context, 0, iw,
//				PendingIntent.FLAG_UPDATE_CURRENT);
		PendingIntent piw = PendingIntent.getService(context, 0, iw,
				PendingIntent.FLAG_UPDATE_CURRENT);
	    AlarmManager amw = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	    if (Build.VERSION.SDK_INT>=23) amw.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, piw);
	    else amw.set(AlarmManager.RTC_WAKEUP, time, piw);

//	    amw.setRepeating(AlarmManager.RTC_WAKEUP, time, mEnvParms.settingHeartBeatIntervalTime,piw);
    };
    
	final static private void cancelHeartBeat(Context context) {
//		Intent iw = new Intent();
    	Intent iw = new Intent(context,SchedulerService.class);
		iw.setAction(BROADCAST_SERVICE_HEARTBEAT);
//		PendingIntent piw = PendingIntent.getBroadcast(context, 0, iw,
//				PendingIntent.FLAG_UPDATE_CURRENT);
		PendingIntent piw = PendingIntent.getService(context, 0, iw,
				PendingIntent.FLAG_UPDATE_CURRENT);
	    AlarmManager amw = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	    amw.cancel(piw);
    };


    static private void listInitSettingsParm() {
    	mUtil.addDebugMsg(1,"I","initSettingParms "+Build.MANUFACTURER + " - " + Build.MODEL);
    	mUtil.addDebugMsg(1,"I","General parameters");
  		mUtil.addDebugMsg(1,"I","   localRootDir=",mEnvParms.localRootDir);
  		mUtil.addDebugMsg(1,"I","   settingDebugLevel=",String.valueOf(mEnvParms.settingDebugLevel));
  		mUtil.addDebugMsg(1,"I","   settingLogMsgDir=",mEnvParms.settingLogMsgDir);
  		mUtil.addDebugMsg(1,"I","   settingLogOption=",String.valueOf(mEnvParms.settingLogOption));
  		mUtil.addDebugMsg(1,"I","   settingEnableScheduler=",String.valueOf(mEnvParms.settingEnableScheduler));
  		mUtil.addDebugMsg(1,"I","   settingMaxTaskCount=",String.valueOf(mEnvParms.settingMaxTaskCount));
  		mUtil.addDebugMsg(1,"I","   settingThreadPoolCount=",String.valueOf(mEnvParms.settingTaskExecThreadPoolCount));
  		mUtil.addDebugMsg(1,"I","   settingEnableMonitor=",String.valueOf(mEnvParms.settingEnableMonitor));
  		mUtil.addDebugMsg(1,"I","   settingWakeLockAlways=",String.valueOf(mEnvParms.settingWakeLockOption));
  		mUtil.addDebugMsg(1,"I","   settingWakeLockLightSensor=",String.valueOf(mEnvParms.settingWakeLockLightSensor));
  		mUtil.addDebugMsg(1,"I","   settingWakeLockProximitySensor=",String.valueOf(mEnvParms.settingWakeLockProximitySensor));
  		mUtil.addDebugMsg(1,"I","   settingHeartBeatIntervalTime=",String.valueOf(mEnvParms.settingHeartBeatIntervalTime));
    	mUtil.addDebugMsg(1,"I","Light sensor parameteres");
    	mUtil.addDebugMsg(1,"I","   LightSensorUseThread=",String.valueOf(mEnvParms.settingLightSensorUseThread));
    	mUtil.addDebugMsg(1,"I","   MonitorIntervalTime=",String.valueOf(mEnvParms.settingLightSensorMonitorIntervalTime));
    	mUtil.addDebugMsg(1,"I","   MonitorActiveTime=",String.valueOf(mEnvParms.settingLightSensorMonitorActiveTime));
    	mUtil.addDebugMsg(1,"I","   DetectThreshHold=",String.valueOf(mEnvParms.settingLightSensorDetectThreshHold));
    	mUtil.addDebugMsg(1,"I","   LightSensorDetectIgnoreTime=",String.valueOf(mEnvParms.settingLightSensorDetectIgnoreTime));
    };
}
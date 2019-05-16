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

import com.sentaroh.android.TaskAutomation.EnvironmentParms;
import com.sentaroh.android.Utilities.NotifyEvent;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class SchedulerMonitor extends Service {
	private Context mContext;
	
	private CommonUtilities mUtil=null;
	
	private ServiceConnection mConnScheduler=null;
	private ISchedulerClient mSchedulerClient=null;
	
	private int mUnpredictableStopCount=0;
	private long mUnpredictableStopTime=0;
	
	private EnvironmentParms mEnvParms=null;
	private GlobalParameters mGp=null;
	
	@Override
    public void onCreate() {
		mContext=getApplicationContext();
		mEnvParms=new EnvironmentParms();
		mEnvParms.loadSettingParms(mContext);

		mGp=GlobalWorkArea.getGlobalParameters(mContext);

        mUtil=new CommonUtilities(mContext, "SchedMon", mEnvParms, mGp);
		mUtil.addDebugMsg(1,"I","onCreate entered");
		mUtil.addDebugMsg(1,"I","initSettingParms "+
				"localRootDir="+mEnvParms.localRootDir+
				", debug_level="+mGp.settingDebugLevel+
				", settingExitClean="+mGp.settingExitClean+
				", settingEnableMonitor="+mGp.settingEnableMonitor);
		mUtil.addLogMsg("I",getString(R.string.msgs_monitor_started)+" "+
				android.os.Process.myPid());

//		uiHandler = new Handler();
		
		startSvcScheduler(null);
		
//		if (mEnvParms.settingEnableMonitor) startHeartBeat(mContext);
	};

    @Override
    public int onStartCommand(Intent in, int flags, int startId) {
		mEnvParms.loadSettingParms(mContext);
		String action="";
//		cancelHeartBeat(mContext);
    	if (in!=null) 
    		if (in.getAction()!=null) action=in.getAction();
		if (mGp.settingDebugLevel>=3) mUtil.addDebugMsg(3,"I","onStartCommand entered, action="+action);
    	if (!mGp.settingEnableMonitor) {
    		mUtil.addLogMsg("W",getString(R.string.msgs_main_abort_scheduling_option));
    		
    		terminateService();
    		return START_STICKY;
//    	} else {
//        	startHeartBeat(mContext);
    	}
//		startSvcScheduler(null);
		return START_STICKY; //START_STICKY;
    };

	@Override
    public IBinder onBind(Intent in) {
		mUtil.addDebugMsg(1,"I","onBind entered, action="+in.getAction());
		return svcMonitorClient;
    };

	@Override
	public boolean onUnbind(Intent in) {
		mUtil.addDebugMsg(1,"I","onUnBind entered, action="+in.getAction());
		return true;
	};

    @Override
    public void onDestroy() {
    	mUtil.addDebugMsg(1,"I","onDestroy enterd");
    	mUtil.addLogMsg("I",getString(R.string.msgs_monitor_termination));

    	stopSvcScheduler();

        mUtil=null;

        if (mGp.settingExitClean) {
			System.gc();
			android.os.Process.killProcess(android.os.Process.myPid());
        }
    }; 

//    final static private void startHeartBeat(Context context) {
//    	Intent iw = new Intent();
//		iw.setAction(BROADCAST_MONITOR_HEARTBEAT);
//		long time=System.currentTimeMillis()+(1000*180);
//		PendingIntent piw = PendingIntent.getBroadcast(context, 0, iw,
//				PendingIntent.FLAG_UPDATE_CURRENT);
//	    AlarmManager amw = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//	    amw.set(AlarmManager.RTC_WAKEUP, time, piw);
//    };
//    
//	final static private void cancelHeartBeat(Context context) {
//		Intent iw = new Intent();
//		iw.setAction(BROADCAST_MONITOR_HEARTBEAT);
//		PendingIntent piw = PendingIntent.getBroadcast(context, 0, iw,
//				PendingIntent.FLAG_UPDATE_CURRENT);
//	    AlarmManager amw = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//	    amw.cancel(piw);
//    };

    private void terminateService() {
    	stopSelf();
    };
    
    private final ISchedulerMonitor.Stub svcMonitorClient=
    		new ISchedulerMonitor.Stub() {
	    };

    @SuppressWarnings("deprecation")
	@SuppressLint("InlinedApi")
	private SharedPreferences getPrefsMgr() {
        return mContext.getSharedPreferences(DEFAULT_PREFS_FILENAME,
        		Context.MODE_PRIVATE|Context.MODE_MULTI_PROCESS);
    }
    
	@SuppressLint("Wakelock")
	private void startSvcScheduler(final NotifyEvent p_ntfy) {
		if (mSchedulerClient != null) return;
		mUtil.addDebugMsg(1,"I", "startSvcScheduler entered");
		
        mConnScheduler = new ServiceConnection(){
    		public void onServiceConnected(ComponentName name, IBinder service) {
				mUtil.addDebugMsg(1, "I", "Scheduler service was connected");
    			mSchedulerClient = ISchedulerClient.Stub.asInterface(service);
    		}
    		public void onServiceDisconnected(ComponentName name) {
    			WakeLock wl= 
    					((PowerManager)mContext.getSystemService(Context.POWER_SERVICE))
    	    				.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
    	        				| PowerManager.ON_AFTER_RELEASE, "StartSvc");
    			try {
    				wl.acquire(100);
        			mUtil.addDebugMsg(1, "I", "Scheduler service was disconnected");
        			mSchedulerClient = null;
        			if (mGp.settingEnableMonitor) {
        				mUnpredictableStopCount++;
        				if (mUnpredictableStopCount>5) {
        					getPrefsMgr().edit().putBoolean(getString(
       							R.string.settings_main_enable_scheduler), false).commit();
            				mUtil.addLogMsg("W", 
            						getString(R.string.msgs_monitor_max_unpredictable_restart));
            				mUnpredictableStopCount=0;
        				} else {
        					if ((mUnpredictableStopTime+1000*60)<System.currentTimeMillis()) {
        						mUnpredictableStopCount=0;
        					}
        				}
        				mUtil.addLogMsg("W", getString(R.string.msgs_monitor_issued));
        				mUtil.startScheduler();
        				mUnpredictableStopTime=System.currentTimeMillis();
        			} else {
        				mUtil.addLogMsg("W", getString(R.string.msgs_monitor_ignored));
        			}
    			} finally {
//    				wl.release();
    			}
    		}
    	};
		Intent intent = new Intent(mContext, SchedulerService.class);
		intent.setAction("Monitor");
		bindService(intent, mConnScheduler, BIND_AUTO_CREATE);
	};
	
	private void stopSvcScheduler() { 
		mUtil.addDebugMsg(1, "I", "stopSvcScheduler entered");
		unbindService(mConnScheduler);
	};
}

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

import static com.sentaroh.android.TaskAutomation.WidgetConstants.*;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.*;

import java.util.LinkedList;

import com.sentaroh.android.TaskAutomation.Common.EnvironmentParms;
import com.sentaroh.android.TaskAutomation.Common.TaskManagerParms;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.widget.RemoteViews;
import android.widget.Toast;

public class WidgetService {

	static private boolean mWifiButtonEnabled=true;
	static private boolean mBluetoothButtonEnabled=true;
	static private boolean mBluetoothBeingOff=false;
	static private boolean mSilentButtonEnabled=true;
	static private ComponentName mCnBattery=null, mCnBluetooth=null, mCnWifi=null, 
			mCnSilent=null, mCnLockedScreenSimple=null;
	static private ComponentName mCnLockedScreenWithInfo=null;
	static private RemoteViews mRvBattery=null, mRvBluetooth=null, mRvWifi=null, 
			mRvSilent=null, mRvLockedScreenSimple=null;
	static private RemoteViews mRvLockedScreenWithInfo=null;

    private static StringBuilder mLockedScreenInfoViewText=new StringBuilder(); 

	static private AppWidgetManager mWidgetManager=null;
	static private Context mAppContext=null;
	static private EnvironmentParms mEnvParms=null;
	static private CommonUtilities mUtil=null;
	static private AudioManager mAudioManager=null;
    static private long mLastScreenLockIgnoredMsgIssued=0;

    static private TaskManagerParms mTaskMgrParms=null;
    
	public WidgetService(Context c, TaskManagerParms tmp, EnvironmentParms ep, CommonUtilities ut) {
		mAppContext=c;
		mEnvParms=ep;
		mUtil=ut;
		mTaskMgrParms=tmp;
		mWidgetManager=AppWidgetManager.getInstance(mAppContext);
		mAudioManager=(AudioManager)mAppContext.getSystemService(Context.AUDIO_SERVICE);

		createBatteryRemoteView();
		createBluetoothRemoteView();
		createWifiRemoteView();
		createSilentRemoteView();
		createLockedScreenSimpleRemoteView();
		createLockedScreenWithInfoRemoteView();
	    setBluetoothButtonIcon(mRvBluetooth,false);
	    setWifiButtonIcon(mRvWifi,false);
		setBatteryButtonIntent();
		setBluetoothButtonIntent();
		setWifiButtonIntent();
		setSilentButtonIntent();
		
		setLockedScreenSimpleButtonIcon();
		setLockedScreenSimpleButtonIntent();
		
		setLockedScreenWithInfoButtonIcon();
		setLockedScreenWithInfoButtonIntent();

		setBatteryButtonIcon(mRvBattery);
		updateBatteryWidget();
		updateBluetoothWidget();
		updateWifiWidget();
		updateSilentWidget();
		
		updateLockedScreenSimpleWidget();
		
		updateLockedScreenWithInfoWidget();
	};
	
	final public boolean isWidgetActive() {
		if (mRvBattery!=null || mRvBluetooth!=null || 
				mRvWifi!=null || mRvLockedScreenSimple!=null || mRvLockedScreenWithInfo!=null) 
			return true;
		else return  false;
	};
	
	final public void removeWidget() {
    	removeBatteryRemoteView();
    	removeBluetoothRemoteView();
    	removeWifiRemoteView();
    	removeLockedScreenSimpleRemoteView();
	};
	
    private static Runnable mRunnableWidgetIntent=null;
    private static LinkedList<String> mWidgetActionQueue=new LinkedList<String>();
    final public void startWidgetIntentThread(final String action) {
		synchronized(mWidgetActionQueue) {
			mWidgetActionQueue.add(action);
		}
    	if (mRunnableWidgetIntent==null) {
    		mRunnableWidgetIntent=new Runnable(){
        		@Override
        		public void run() {
    				synchronized(mWidgetActionQueue) {
    					while(!mWidgetActionQueue.isEmpty()) {
    						String action=mWidgetActionQueue.poll();
    						processWidgetIntent(action);
    					}
    					mWidgetActionQueue=new LinkedList<String>();
    				}
        		}
        	};
    	}
    	mTaskMgrParms.normalTaskControlThreadPool.execute(mRunnableWidgetIntent);
//    	Runnable r=new Runnable(){
//			@Override
//			public void run() {
//				processWidgetIntent(action);
//			}
//    	};
//    	mTaskMgrParms.highTaskControlThreadPool.execute(r);
//    	processWidgetIntent(action);
    };

    private static final void processWidgetIntent(String action) {
    	if (action.equals(WIDGET_BATTERY_ENABLE)) {
    		createBatteryRemoteView();
    	} else if (action.equals(WIDGET_BATTERY_UPDATE)) {
    		if (mRvBattery==null) createBatteryRemoteView();
    		setBatteryButtonIntent();
    		setBatteryButtonIcon(mRvBattery);
    		updateBatteryWidget();
    	} else if (action.equals(WIDGET_BATTERY_DISABLE)) {
    		removeBatteryRemoteView();
    	} else if (action.equals(WIDGET_BLUETOOTH_ENABLE)) {
    		createBluetoothRemoteView();
    	} else if (action.equals(WIDGET_BLUETOOTH_UPDATE)) {
    		if (mRvBluetooth==null) createBluetoothRemoteView();
    		setBluetoothButtonIntent();
    		setBluetoothButtonIcon(mRvBluetooth,false);
    		updateBluetoothWidget();
    	} else if (action.equals(WIDGET_BLUETOOTH_DISABLE)) {
    		removeBluetoothRemoteView();
    	} else if (action.equals(WIDGET_WIFI_ENABLE)) {
    		createWifiRemoteView();
    	} else if (action.equals(WIDGET_WIFI_UPDATE)) {
    		if (mRvWifi==null) createWifiRemoteView();
    		setWifiButtonIntent();
    		setWifiButtonIcon(mRvWifi,false);
    		updateWifiWidget();
    	} else if (action.equals(WIDGET_WIFI_DISABLE)) {
    		removeWifiRemoteView();
    	} else if (action.equals(WIDGET_SILENT_ENABLE)) {
    		createSilentRemoteView();
    	} else if (action.equals(WIDGET_SILENT_UPDATE)) {
    		if (mRvSilent==null) createSilentRemoteView();
    		setSilentButtonIntent();
    		setSilentButtonIcon(mRvSilent);
    		updateSilentWidget();
    	} else if (action.equals(WIDGET_SILENT_DISABLE)) {
    		removeSilentRemoteView();
    	} else if (action.equals(WIDGET_LOCKED_SCREEN_SIMPLE_ENABLE)) {
    		createLockedScreenSimpleRemoteView();
    	} else if (action.equals(WIDGET_LOCKED_SCREEN_SIMPLE_UPDATE)) {
    		if (mRvLockedScreenSimple==null) createLockedScreenSimpleRemoteView();
    		setLockedScreenSimpleButtonIntent();
    		setLockedScreenSimpleButtonIcon();
    		updateLockedScreenSimpleWidget();
    	} else if (action.equals(WIDGET_LOCKED_SCREEN_SIMPLE_DISABLE)) {
    		removeLockedScreenSimpleRemoteView();
    	} else if (action.equals(WIDGET_LOCKED_SCREEN_WITH_INFO_ENABLE)) {
    		createLockedScreenWithInfoRemoteView();
    	} else if (action.equals(WIDGET_LOCKED_SCREEN_WITH_INFO_UPDATE)) {
    		if (mRvLockedScreenWithInfo==null) createLockedScreenWithInfoRemoteView();
    		setLockedScreenWithInfoButtonIntent();
    		setLockedScreenWithInfoButtonIcon();
    		updateLockedScreenWithInfoWidget();
    	} else if (action.equals(WIDGET_LOCKED_SCREEN_WITH_INFO_DISABLE)) {
    		removeLockedScreenWithInfoRemoteView();
    	}

    }
    
    private static Runnable mRunnableDeviceButtonWifi=null,
    						mRunnableDeviceButtonBluetooth=null,
    						mRunnableDeviceButtonSilent=null,
    						mRunnableDeviceButtonBattery=null;
    final public void processDeviceButton(final String action) {
		mUtil.addDebugMsg(1,"I","processDeviceButton entered"+
				", action="+action+
				", wifiBtnEnabled="+mWifiButtonEnabled+
				", BluetoothBtnEnabled="+mBluetoothButtonEnabled);

    	if (mRunnableDeviceButtonWifi==null)
    		mRunnableDeviceButtonWifi=new Runnable(){
    		@Override
    		public void run() {
        		if (mWifiButtonEnabled) {
    				mWifiButtonEnabled=false;
        			setWifiButtonIcon(mRvWifi,true);
        			setWifiButtonIcon(mRvLockedScreenSimple,true);
        			setWifiButtonIcon(mRvLockedScreenWithInfo,true);
        			updateWifiWidget();
        			updateLockedScreenSimpleWidget();
        			updateLockedScreenWithInfoWidget();
    				if (mEnvParms.wifiIsActive) {
    					setWifiOff();
    				} else {
    					setWifiOn();
    				}
    			}
    		}
    	};
    	if (mRunnableDeviceButtonBluetooth==null)
    		mRunnableDeviceButtonBluetooth=new Runnable(){
    		@Override
    		public void run() {
    			if (mEnvParms.bluetoothIsAvailable) {
    				if (mBluetoothButtonEnabled) {
    					mBluetoothButtonEnabled=false;
    					int bs=BluetoothAdapter.getDefaultAdapter().getState();
    					if (bs==BluetoothAdapter.STATE_OFF||bs==BluetoothAdapter.STATE_ON) {
    						setBluetoothButtonIcon(mRvBluetooth,true);
    		    			setBluetoothButtonIcon(mRvLockedScreenSimple,true);
    		    			setBluetoothButtonIcon(mRvLockedScreenWithInfo,true);
    						updateBluetoothWidget();
    		    			updateLockedScreenSimpleWidget();
    		    			updateLockedScreenWithInfoWidget();
    						if (mEnvParms.bluetoothIsActive) {
    				        	setBluetoothOff();
    				        	mBluetoothBeingOff=true;
    				        } else {
    				        	mBluetoothBeingOff=false;
    				        	setBluetoothOn();
    				        }
    					} 
    				}
    			}
    		}
    	};
    	if (mRunnableDeviceButtonBattery==null)
    		mRunnableDeviceButtonBattery=new Runnable(){
    		@Override
    		public void run() {
    			if (mEnvParms.settingDeviceAdmin) {
    				mUtil.screenLockNow();
//    				mTaskMgrParms.svcHandler.postDelayed(new Runnable(){
//    					@Override
//    					public void run() {
//    						mUtil.screenLockNow();
//    					}
//    				}, 500);
    			} else {
    				if(Math.abs(System.currentTimeMillis()-mLastScreenLockIgnoredMsgIssued)>2000) { 
        				mTaskMgrParms.svcHandler.post(new Runnable(){
        					@Override
        					public void run() {
            					Toast.makeText(mAppContext, 
            							mAppContext.getString(R.string.msgs_widget_battery_button_not_functional), Toast.LENGTH_SHORT).show();
            					mLastScreenLockIgnoredMsgIssued=System.currentTimeMillis();
        					}
        				});
    				}
    			}
    		}
    	};
    	if (mRunnableDeviceButtonSilent==null) {
    		final Handler hndl=new Handler();
			mRunnableDeviceButtonSilent=new Runnable(){
	    		@SuppressWarnings("deprecation")
				@Override
	    		public void run() {
	    			if (mSilentButtonEnabled) {
	    				mSilentButtonEnabled=false;
	    				if (mAudioManager.getRingerMode()==AudioManager.RINGER_MODE_NORMAL) {
	    					mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
	    					mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
	    					mAudioManager.setStreamMute(AudioManager.STREAM_RING, true);
	    					mAudioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
	    					mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
	    					mAudioManager.setStreamMute(AudioManager.STREAM_ALARM, true);
	    					if (Build.VERSION.SDK_INT==21) {
		    					hndl.postDelayed(new Runnable(){
									@Override
									public void run() {
				    					mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
									}
		    					}, 200);
	    					}
	    				} else {
	    					mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
	    					mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
	    					mAudioManager.setStreamMute(AudioManager.STREAM_RING, false);
	    					mAudioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
	    					mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
	    					mAudioManager.setStreamMute(AudioManager.STREAM_ALARM, false);
	    				}  
	    			}
	    		}
			};
    	}
    	if(HOME_SCREEN_DEVICE_BTN_WIFI.equals(action) || 
    			LOCKED_SCREEN_SIMPLE_DEVICE_BTN_WIFI.equals(action) ||
    			LOCKED_SCREEN_WITH_INFO_DEVICE_BTN_WIFI.equals(action)){
        	mTaskMgrParms.highTaskControlThreadPool.execute(mRunnableDeviceButtonWifi);
		} else if(HOME_SCREEN_DEVICE_BTN_BLUETOOTH.equals(action)||
				LOCKED_SCREEN_SIMPLE_DEVICE_BTN_BLUETOOTH.equals(action)||
				LOCKED_SCREEN_WITH_INFO_DEVICE_BTN_BLUETOOTH.equals(action)){
			mTaskMgrParms.highTaskControlThreadPool.execute(mRunnableDeviceButtonBluetooth);
		} else if(HOME_SCREEN_DEVICE_BTN_BATTERY.equals(action)||
				LOCKED_SCREEN_SIMPLE_DEVICE_BTN_BATTERY.equals(action)||
				LOCKED_SCREEN_WITH_INFO_DEVICE_BTN_BATTERY.equals(action)){
			mTaskMgrParms.highTaskControlThreadPool.execute(mRunnableDeviceButtonBattery);
		} else if(HOME_SCREEN_DEVICE_BTN_SILENT.equals(action) ||
				LOCKED_SCREEN_SIMPLE_DEVICE_BTN_SILENT.equals(action)||
				LOCKED_SCREEN_WITH_INFO_DEVICE_BTN_SILENT.equals(action)){
			mTaskMgrParms.highTaskControlThreadPool.execute(mRunnableDeviceButtonSilent);
		}
    };

    private static Runnable mRunnableRingerModeChanged=null;
    final public void processRingerModeChanged() {
    	if (mRunnableRingerModeChanged==null) {
    		mRunnableRingerModeChanged=new Runnable(){
        		@Override
        		public void run() {
        			setSilentButtonIcon(mRvSilent);
        			setSilentButtonIcon(mRvLockedScreenSimple);
        			setSilentButtonIcon(mRvLockedScreenWithInfo);
        			updateSilentWidget();
        			updateLockedScreenSimpleWidget();
        			updateLockedScreenWithInfoWidget();
        			mSilentButtonEnabled=true;
        			
    				TaskManager.buildNotification(mTaskMgrParms, mEnvParms);
    				TaskManager.showNotification(mTaskMgrParms, mEnvParms, mUtil);
        		}
        	};
    	}
    	mTaskMgrParms.highTaskControlThreadPool.execute(mRunnableRingerModeChanged);
    };

    private static Runnable mRunnableBatteryStatusChanged=null;
    final public void processBatteryStatusChanged() {
    	if (mRunnableBatteryStatusChanged==null) {
    		mRunnableBatteryStatusChanged=new Runnable(){
        		@Override
        		public void run() {
        			setBatteryButtonIcon(mRvBattery);
        			setBatteryButtonIcon(mRvLockedScreenSimple);
        			setBatteryButtonIcon(mRvLockedScreenWithInfo);
        			updateBatteryWidget();
        			updateLockedScreenSimpleWidget();
        			updateLockedScreenWithInfoWidget();
        		}
        	};
    	}
    	mTaskMgrParms.highTaskControlThreadPool.execute(mRunnableBatteryStatusChanged);
    };

    private static Runnable mRunnableWifiStatusChanged=null;
    final public void processWifiStatusChanged() {
    	if (mRunnableWifiStatusChanged==null) {
    		mRunnableWifiStatusChanged=new Runnable(){
        		@Override
        		public void run() {
        			boolean connect=mEnvParms.isWifiConnected();
        			mUtil.addDebugMsg(1,"I","processWifiStatusChanged entered"+
        					", wifiIsActive="+mEnvParms.wifiIsActive+
        					", connect="+connect+", btnEnabled="+mWifiButtonEnabled);
        			mWifiButtonEnabled=true;
        			setWifiButtonIcon(mRvWifi,false);
        			setLockedScreenSimpleButtonIcon();
        			setLockedScreenWithInfoButtonIcon();
        			updateWifiWidget();
        			updateLockedScreenSimpleWidget();
        			updateLockedScreenWithInfoWidget();
        			
    				TaskManager.buildNotification(mTaskMgrParms, mEnvParms);
    				TaskManager.showNotification(mTaskMgrParms, mEnvParms, mUtil);
        		}
        	};
    	}
    	mTaskMgrParms.highTaskControlThreadPool.execute(mRunnableWifiStatusChanged);
    };

    private static Runnable mRunnableBluetoothStatusChanged=null;
    final public void processBluetoothStatusChanged() {
    	if (mRunnableBluetoothStatusChanged==null) {
    		mRunnableBluetoothStatusChanged=new Runnable(){
        		@Override
        		public void run() {
        	    	if ((mBluetoothBeingOff && !mEnvParms.bluetoothIsActive) ||
        	        		!mBluetoothBeingOff) {
        	    		mBluetoothButtonEnabled=true;
        	            setBluetoothButtonIcon(mRvBluetooth,false);
        	            setBluetoothButtonIcon(mRvLockedScreenSimple,false);
        	            setBluetoothButtonIcon(mRvLockedScreenWithInfo,false);
        	            updateBluetoothWidget();
        	            updateLockedScreenSimpleWidget();
        	            updateLockedScreenWithInfoWidget();
        	            
        				TaskManager.buildNotification(mTaskMgrParms, mEnvParms);
        				TaskManager.showNotification(mTaskMgrParms, mEnvParms, mUtil);
        	       	}
        	       	if (!mEnvParms.bluetoothIsActive) mBluetoothBeingOff=false;
        		}
        	};
    	}
    	mTaskMgrParms.highTaskControlThreadPool.execute(mRunnableBluetoothStatusChanged);
    };

    static private void createBatteryRemoteView() {
        int[] wids =mWidgetManager.getAppWidgetIds(new ComponentName(mAppContext, WidgetProviderBattery.class));
        if (wids!=null && wids.length>0) {
        	mRvBattery = new RemoteViews(mAppContext.getPackageName(), R.layout.widget_layout_battery);
    		mCnBattery = new ComponentName(mAppContext, WidgetProviderBattery.class);
    		if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(1,"I","Battery RemoteViews was created");
        }
    };

    static private void removeBatteryRemoteView() {
    	if (mRvBattery!=null) {
        	Intent intent = new Intent();
        	intent.setAction(HOME_SCREEN_DEVICE_BTN_BATTERY);
        	PendingIntent pi = 
        		PendingIntent.getBroadcast(mAppContext, 0, intent,PendingIntent.FLAG_CANCEL_CURRENT);
        	mRvBattery.setOnClickPendingIntent(R.id.device_layout_battery_btn, pi);
    		mRvBattery = null;
    		mCnBattery = null;
    		if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(1,"I","Battery RemoteViews was removed");
    	}
    };
    
    static private void setBatteryButtonIcon(final RemoteViews rv) {
		if (rv==null) return;
		if (mEnvParms.batteryChargeStatusInt==mEnvParms.BATTERY_CHARGE_STATUS_INT_FULL) {
			rv.setTextViewText(R.id.device_layout_battery_level, "FULL");
			setRemoteViewImage(rv, R.id.device_layout_battery_btn, R.drawable.device_battery_dischg_100);
		} else {
			if (mEnvParms.batteryPowerSource.equals(CURRENT_POWER_SOURCE_AC)) {
			    if (mEnvParms.batteryLevel<10)
			    	setRemoteViewImage(rv, R.id.device_layout_battery_btn, R.drawable.device_battery_charge_000);
			    else if (mEnvParms.batteryLevel<30)
			    	setRemoteViewImage(rv, R.id.device_layout_battery_btn, R.drawable.device_battery_charge_020);
			    else if (mEnvParms.batteryLevel<50)
			    	setRemoteViewImage(rv, R.id.device_layout_battery_btn, R.drawable.device_battery_charge_040);
			    else if (mEnvParms.batteryLevel<70)
			    	setRemoteViewImage(rv, R.id.device_layout_battery_btn, R.drawable.device_battery_charge_060);
			    else if (mEnvParms.batteryLevel<90)
			    	setRemoteViewImage(rv, R.id.device_layout_battery_btn, R.drawable.device_battery_charge_080);
			    else setRemoteViewImage(rv, R.id.device_layout_battery_btn, R.drawable.device_battery_charge_100);
			} else {
			    if (mEnvParms.batteryLevel<10)
			    	setRemoteViewImage(rv, R.id.device_layout_battery_btn, R.drawable.device_battery_dischg_000);
			    else if (mEnvParms.batteryLevel<30)
			    	setRemoteViewImage(rv, R.id.device_layout_battery_btn, R.drawable.device_battery_dischg_020);
			    else if (mEnvParms.batteryLevel<50)
			    	setRemoteViewImage(rv, R.id.device_layout_battery_btn, R.drawable.device_battery_dischg_040);
			    else if (mEnvParms.batteryLevel<70)
			    	setRemoteViewImage(rv, R.id.device_layout_battery_btn, R.drawable.device_battery_dischg_060);
			    else if (mEnvParms.batteryLevel<90)
			    	setRemoteViewImage(rv, R.id.device_layout_battery_btn, R.drawable.device_battery_dischg_080);
			    else setRemoteViewImage(rv, R.id.device_layout_battery_btn, R.drawable.device_battery_dischg_100);
			}
		    if (mEnvParms.batteryLevel<10) {
			    rv.setTextViewText(R.id.device_layout_battery_level, ""+mEnvParms.batteryLevel+"%");
		    } else if (mEnvParms.batteryLevel<100) {
		    	rv.setTextViewText(R.id.device_layout_battery_level, ""+mEnvParms.batteryLevel+"%");
		    } else {
		    	rv.setTextViewText(R.id.device_layout_battery_level, ""+mEnvParms.batteryLevel+"%");
		    }
		}
	};

	static private void updateBatteryWidget() {
	    if (mRvBattery!=null) 
	    	mWidgetManager.updateAppWidget(mCnBattery, mRvBattery);
	};

	static private void setBatteryButtonIntent() {
    	if (mRvBattery==null) return;
//    	util.sendDebugLogMsg(1,"I","setBatteryButtonIntent entered");
    	Intent intent = new Intent();
    	intent.setAction(HOME_SCREEN_DEVICE_BTN_BATTERY);
    	PendingIntent pendingIntent = PendingIntent.getBroadcast(mAppContext, 0, intent,
    			PendingIntent.FLAG_UPDATE_CURRENT);
    	mRvBattery.setOnClickPendingIntent(R.id.device_layout_battery_btn, pendingIntent);
    };

	static private void createWifiRemoteView() { 
        int[] wids =mWidgetManager.getAppWidgetIds(new ComponentName(mAppContext, WidgetProviderWIFI.class));
        if (wids!=null && wids.length>0) {
			mRvWifi = new RemoteViews(mAppContext.getPackageName(), R.layout.widget_layout_wifi);
			mCnWifi = new ComponentName(mAppContext, WidgetProviderWIFI.class);
			if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(1,"I","WIFI RemoteViews created");
        }
    };

    static private void setWifiButtonIcon(final RemoteViews rv, final boolean on_off) {
    	mUtil.addDebugMsg(1,"I","setWifiButtonIcon entered, on/off="+on_off,", wifi active="+mEnvParms.wifiIsActive,", button enabled="+mWifiButtonEnabled);
    	if (rv==null) return;
		if (on_off) {
			setRemoteViewImage(rv, R.id.device_layout_wifi_btn, R.drawable.device_wifi_off_on);
		} else {
	    	mWifiButtonEnabled=true;
	    	if (mEnvParms.wifiIsActive) {
	    		setRemoteViewImage(rv, R.id.device_layout_wifi_btn, R.drawable.device_wifi_on);
	    	} else {
	    		setRemoteViewImage(rv, R.id.device_layout_wifi_btn, R.drawable.device_wifi_off);
	    	}
		}
	};

	static private void updateWifiWidget() {
	    if (mRvWifi!=null) 
	    	mWidgetManager.updateAppWidget(mCnWifi, mRvWifi);
	};

	static private void removeWifiRemoteView() {
    	if (mRvWifi!=null) {
        	Intent intent = new Intent();
        	intent.setAction(HOME_SCREEN_DEVICE_BTN_WIFI);
        	PendingIntent pi = 
        		PendingIntent.getBroadcast(mAppContext, 0, intent,PendingIntent.FLAG_CANCEL_CURRENT);
        	mRvWifi.setOnClickPendingIntent(R.id.device_layout_wifi_btn, pi);
    		mRvWifi = null;
    		mCnWifi = null;
    		if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(1,"I","Wifi RemoteViews was removed");
    	}
    };

	static final private void setWifiButtonIntent() {
    	if (mRvWifi==null) return;
//    	util.addDebugMsg(1,"I","setWifiButtonIntent entered");
        Intent intent = new Intent();
    	intent.setAction(HOME_SCREEN_DEVICE_BTN_WIFI);
    	PendingIntent pi = PendingIntent.getBroadcast(mAppContext, 0, intent,
    			PendingIntent.FLAG_UPDATE_CURRENT);
    	mRvWifi.setOnClickPendingIntent(R.id.device_layout_wifi_btn, pi);
    };

	static final private void setBluetoothButtonIcon(final RemoteViews rv,final boolean on_off) {
		if (rv==null) return;
    	if (on_off) {
    		setRemoteViewImage(rv, R.id.device_layout_bt_btn, R.drawable.device_bluetooth_on_off);
    	} else {
	    	if (mEnvParms.bluetoothIsActive)
	    		setRemoteViewImage(rv, R.id.device_layout_bt_btn, R.drawable.device_bluetooth_on);
	    	else setRemoteViewImage(rv, R.id.device_layout_bt_btn, R.drawable.device_bluetooth_off);
    	}
    };
    
    static final private void createBluetoothRemoteView() {
        int[] wids =mWidgetManager.getAppWidgetIds(new ComponentName(mAppContext, WidgetProviderBluetooth.class));
        if (wids!=null && wids.length>0) {
			mRvBluetooth = new RemoteViews(mAppContext.getPackageName(), R.layout.widget_layout_bt);
			mCnBluetooth = new ComponentName(mAppContext, WidgetProviderBluetooth.class);
			if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(1,"I","Bluetooth RemoteViews created");
        }
    };

    static final private void removeBluetoothRemoteView() {
    	if (mRvBluetooth!=null) {
        	Intent intent = new Intent();
        	intent.setAction(HOME_SCREEN_DEVICE_BTN_BLUETOOTH);
        	PendingIntent pi = 
        		PendingIntent.getBroadcast(mAppContext, 0, intent,PendingIntent.FLAG_CANCEL_CURRENT);
        	mRvBluetooth.setOnClickPendingIntent(R.id.device_layout_bt_btn, pi);
    		mRvBluetooth = null;
    		mCnBluetooth = null;
    		if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(1,"I","Bluetooth RemoteViews was removed");
    	}
    };

    static final private void updateBluetoothWidget() {
	    if (mRvBluetooth!=null) 
	    	mWidgetManager.updateAppWidget(mCnBluetooth, mRvBluetooth);
	};

	static final private void setBluetoothButtonIntent() {
    	if (mRvBluetooth==null) return;
//    	util.addDebugMsg(1,"I","setBluetoothButtonIntent entered");
        Intent intent = new Intent();
    	intent.setAction(HOME_SCREEN_DEVICE_BTN_BLUETOOTH);
    	PendingIntent pendingIntent = PendingIntent.getBroadcast(mAppContext, 0, intent, 
    			PendingIntent.FLAG_UPDATE_CURRENT);
    	mRvBluetooth.setOnClickPendingIntent(R.id.device_layout_bt_btn, pendingIntent);
    };

	static final private void setSilentButtonIcon(final RemoteViews rv) {
    	if (rv==null) return;
		if (mAudioManager.getRingerMode()!=AudioManager.RINGER_MODE_NORMAL) {
			setRemoteViewImage(rv, R.id.device_layout_silent_btn, R.drawable.device_silent_on);
		} else {
			setRemoteViewImage(rv, R.id.device_layout_silent_btn, R.drawable.device_silent_off);
		}
    };

    static final private void setRemoteViewImage(final RemoteViews rv, 
    		final int vid, final int rid) {
		rv.setImageViewResource(vid, rid);
    }
    
    static final private void createSilentRemoteView() {
        int[] wids =mWidgetManager.getAppWidgetIds(new ComponentName(mAppContext, WidgetProviderSilent.class));
        if (wids!=null && wids.length>0) {
			mRvSilent = new RemoteViews(mAppContext.getPackageName(), R.layout.widget_layout_silent);
			mCnSilent = new ComponentName(mAppContext, WidgetProviderSilent.class);
			if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(1,"I","Silent RemoteViews created");
        }
    };

    static final private void removeSilentRemoteView() {
    	if (mRvSilent!=null) {
        	Intent intent = new Intent();
        	intent.setAction(HOME_SCREEN_DEVICE_BTN_SILENT);
        	PendingIntent pi = 
        		PendingIntent.getBroadcast(mAppContext, 0, intent,PendingIntent.FLAG_CANCEL_CURRENT);
        	mRvSilent.setOnClickPendingIntent(R.id.device_layout_silent_btn, pi);
    		mRvSilent = null;
    		mCnSilent = null;
    		if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(1,"I","Silent RemoteViews was removed");
    	}
    };

    static final private void updateSilentWidget() {
	    if (mRvSilent!=null) 
	    	mWidgetManager.updateAppWidget(mCnSilent, mRvSilent);
	};

	static final private void setSilentButtonIntent() {
    	if (mRvSilent==null) return;
//    	util.addDebugMsg(1,"I","setBluetoothButtonIntent entered");
        Intent intent = new Intent();
    	intent.setAction(HOME_SCREEN_DEVICE_BTN_SILENT);
    	PendingIntent pendingIntent = PendingIntent.getBroadcast(mAppContext, 0, intent, 
    			PendingIntent.FLAG_UPDATE_CURRENT);
    	mRvSilent.setOnClickPendingIntent(R.id.device_layout_silent_btn, pendingIntent);
    };    
    
    static final private void setLockedScreenSimpleButtonIcon() {
    	if (mRvLockedScreenSimple==null) return;
    	
    	setBatteryButtonIcon(mRvLockedScreenSimple);
    	setWifiButtonIcon(mRvLockedScreenSimple,false);
    	setBluetoothButtonIcon(mRvLockedScreenSimple,false);
    	setSilentButtonIcon(mRvLockedScreenSimple);
    };
    
    static final private void setLockedScreenWithInfoButtonIcon() {
    	if (mRvLockedScreenWithInfo==null) return;
    	
    	setBatteryButtonIcon(mRvLockedScreenWithInfo);
    	setWifiButtonIcon(mRvLockedScreenWithInfo,false);
    	setBluetoothButtonIcon(mRvLockedScreenWithInfo,false);
    	setSilentButtonIcon(mRvLockedScreenWithInfo);
    };

    static final private void createLockedScreenSimpleRemoteView() {
        int[] wids =mWidgetManager.getAppWidgetIds(new ComponentName(mAppContext, WidgetProviderLockedScreenSimple.class));
        if (wids!=null && wids.length>0) {
			mRvLockedScreenSimple = new RemoteViews(mAppContext.getPackageName(), R.layout.widget_layout_locked_screen_simple);
			mCnLockedScreenSimple = new ComponentName(mAppContext, WidgetProviderLockedScreenSimple.class);
			if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(1,"I","LockedScreen RemoteViews created");
        }
    };
    
    static final private void createLockedScreenWithInfoRemoteView() {
        int[] wids =mWidgetManager.getAppWidgetIds(new ComponentName(mAppContext, WidgetProviderLockedScreenWithInfo.class));
        if (wids!=null && wids.length>0) {
			mRvLockedScreenWithInfo = new RemoteViews(mAppContext.getPackageName(), R.layout.widget_layout_locked_screen_with_info);
			mCnLockedScreenWithInfo = new ComponentName(mAppContext, WidgetProviderLockedScreenWithInfo.class);
			if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(1,"I","LockedScreenWithInfo RemoteViews created");
        }
    };

    static final private void removeLockedScreenSimpleRemoteView() {
    	if (mRvLockedScreenSimple!=null) {
        	Intent intent_battery = new Intent();
        	intent_battery.setAction(LOCKED_SCREEN_SIMPLE_DEVICE_BTN_BATTERY);
        	PendingIntent pi_battery = 
        		PendingIntent.getBroadcast(mAppContext, 0, intent_battery,PendingIntent.FLAG_CANCEL_CURRENT);
        	mRvLockedScreenSimple.setOnClickPendingIntent(R.id.device_layout_silent_btn, pi_battery);

        	Intent intent_wifi = new Intent();
        	intent_wifi.setAction(LOCKED_SCREEN_SIMPLE_DEVICE_BTN_WIFI);
        	PendingIntent pi_wifi = 
        		PendingIntent.getBroadcast(mAppContext, 0, intent_wifi,PendingIntent.FLAG_CANCEL_CURRENT);
        	mRvLockedScreenSimple.setOnClickPendingIntent(R.id.device_layout_silent_btn, pi_wifi);

        	Intent intent_bt = new Intent();
        	intent_bt.setAction(LOCKED_SCREEN_SIMPLE_DEVICE_BTN_BLUETOOTH);
        	PendingIntent pi_bt = 
        		PendingIntent.getBroadcast(mAppContext, 0, intent_bt,PendingIntent.FLAG_CANCEL_CURRENT);
        	mRvLockedScreenSimple.setOnClickPendingIntent(R.id.device_layout_silent_btn, pi_bt);

        	Intent intent_silent = new Intent();
        	intent_silent.setAction(LOCKED_SCREEN_SIMPLE_DEVICE_BTN_SILENT);
        	PendingIntent pi_silent = 
        		PendingIntent.getBroadcast(mAppContext, 0, intent_silent,PendingIntent.FLAG_CANCEL_CURRENT);
        	mRvLockedScreenSimple.setOnClickPendingIntent(R.id.device_layout_silent_btn, pi_silent);

        	mRvLockedScreenSimple = null;
    		mCnLockedScreenSimple = null;

    		if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(1,"I","LockedScreen RemoteViews was removed");
    	}
    };
    
    static final private void removeLockedScreenWithInfoRemoteView() {
    	if (mRvLockedScreenWithInfo!=null) {
        	Intent intent_battery = new Intent();
        	intent_battery.setAction(LOCKED_SCREEN_WITH_INFO_DEVICE_BTN_BATTERY);
        	PendingIntent pi_battery = 
        		PendingIntent.getBroadcast(mAppContext, 0, intent_battery,PendingIntent.FLAG_CANCEL_CURRENT);
        	mRvLockedScreenWithInfo.setOnClickPendingIntent(R.id.device_layout_silent_btn, pi_battery);

        	Intent intent_wifi = new Intent();
        	intent_wifi.setAction(LOCKED_SCREEN_WITH_INFO_DEVICE_BTN_WIFI);
        	PendingIntent pi_wifi = 
        		PendingIntent.getBroadcast(mAppContext, 0, intent_wifi,PendingIntent.FLAG_CANCEL_CURRENT);
        	mRvLockedScreenWithInfo.setOnClickPendingIntent(R.id.device_layout_silent_btn, pi_wifi);

        	Intent intent_bt = new Intent();
        	intent_bt.setAction(LOCKED_SCREEN_WITH_INFO_DEVICE_BTN_BLUETOOTH);
        	PendingIntent pi_bt = 
        		PendingIntent.getBroadcast(mAppContext, 0, intent_bt,PendingIntent.FLAG_CANCEL_CURRENT);
        	mRvLockedScreenWithInfo.setOnClickPendingIntent(R.id.device_layout_silent_btn, pi_bt);

        	Intent intent_silent = new Intent();
        	intent_silent.setAction(LOCKED_SCREEN_WITH_INFO_DEVICE_BTN_SILENT);
        	PendingIntent pi_silent = 
        		PendingIntent.getBroadcast(mAppContext, 0, intent_silent,PendingIntent.FLAG_CANCEL_CURRENT);
        	mRvLockedScreenWithInfo.setOnClickPendingIntent(R.id.device_layout_silent_btn, pi_silent);

        	mRvLockedScreenWithInfo = null;
    		mCnLockedScreenWithInfo = null;
    		
    		if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(1,"I","LockedScreenWithInfo RemoteViews was removed");
    	}
    };

    static final private void setLockedScreenSimpleButtonIntent() {
    	if (mRvLockedScreenSimple==null) return;
    	mUtil.addDebugMsg(1,"I","setScreenLockedButtonIntent entered");
        Intent intent_battery = new Intent();
    	intent_battery.setAction(LOCKED_SCREEN_SIMPLE_DEVICE_BTN_BATTERY);
    	PendingIntent pi_battery = PendingIntent.getBroadcast(mAppContext, 0, intent_battery, 
    			PendingIntent.FLAG_UPDATE_CURRENT);
    	mRvLockedScreenSimple.setOnClickPendingIntent(R.id.device_layout_battery_btn, pi_battery);

    	Intent intent_wifi = new Intent();
    	intent_wifi.setAction(LOCKED_SCREEN_SIMPLE_DEVICE_BTN_WIFI);
    	PendingIntent pi_wifi = PendingIntent.getBroadcast(mAppContext, 0, intent_wifi, 
    			PendingIntent.FLAG_UPDATE_CURRENT);
    	mRvLockedScreenSimple.setOnClickPendingIntent(R.id.device_layout_wifi_btn, pi_wifi);
        
    	Intent intent_bt = new Intent();
    	intent_bt.setAction(LOCKED_SCREEN_SIMPLE_DEVICE_BTN_BLUETOOTH);
    	PendingIntent pi_bt = PendingIntent.getBroadcast(mAppContext, 0, intent_bt, 
    			PendingIntent.FLAG_UPDATE_CURRENT);
    	mRvLockedScreenSimple.setOnClickPendingIntent(R.id.device_layout_bt_btn, pi_bt);
        
    	Intent intent_silent = new Intent();
    	intent_silent.setAction(LOCKED_SCREEN_SIMPLE_DEVICE_BTN_SILENT);
    	PendingIntent pi_silent = PendingIntent.getBroadcast(mAppContext, 0, intent_silent, 
    			PendingIntent.FLAG_UPDATE_CURRENT);
    	mRvLockedScreenSimple.setOnClickPendingIntent(R.id.device_layout_silent_btn, pi_silent);
    };
    
    static final private void setLockedScreenWithInfoButtonIntent() {
    	if (mRvLockedScreenWithInfo==null) return;
    	mUtil.addDebugMsg(1,"I","setLockedScreenWithInfoButtonIntent entered");
        Intent intent_battery = new Intent();
    	intent_battery.setAction(LOCKED_SCREEN_WITH_INFO_DEVICE_BTN_BATTERY);
    	PendingIntent pi_battery = PendingIntent.getBroadcast(mAppContext, 0, intent_battery, 
    			PendingIntent.FLAG_UPDATE_CURRENT);
    	mRvLockedScreenWithInfo.setOnClickPendingIntent(R.id.device_layout_battery_btn, pi_battery);

    	Intent intent_wifi = new Intent();
    	intent_wifi.setAction(LOCKED_SCREEN_WITH_INFO_DEVICE_BTN_WIFI);
    	PendingIntent pi_wifi = PendingIntent.getBroadcast(mAppContext, 0, intent_wifi, 
    			PendingIntent.FLAG_UPDATE_CURRENT);
    	mRvLockedScreenWithInfo.setOnClickPendingIntent(R.id.device_layout_wifi_btn, pi_wifi);
        
    	Intent intent_bt = new Intent();
    	intent_bt.setAction(LOCKED_SCREEN_WITH_INFO_DEVICE_BTN_BLUETOOTH);
    	PendingIntent pi_bt = PendingIntent.getBroadcast(mAppContext, 0, intent_bt, 
    			PendingIntent.FLAG_UPDATE_CURRENT);
    	mRvLockedScreenWithInfo.setOnClickPendingIntent(R.id.device_layout_bt_btn, pi_bt);
        
    	Intent intent_silent = new Intent();
    	intent_silent.setAction(LOCKED_SCREEN_WITH_INFO_DEVICE_BTN_SILENT);
    	PendingIntent pi_silent = PendingIntent.getBroadcast(mAppContext, 0, intent_silent, 
    			PendingIntent.FLAG_UPDATE_CURRENT);
    	mRvLockedScreenWithInfo.setOnClickPendingIntent(R.id.device_layout_silent_btn, pi_silent);
    };    

    @SuppressLint("NewApi")
	static final private void updateLockedScreenSimpleWidget() {
        if (mRvLockedScreenSimple!=null) {
        	mWidgetManager.updateAppWidget(mCnLockedScreenSimple, mRvLockedScreenSimple);
        }
    };

    static final public void updateLockedScreenWithInfoWidget() {
        if (mRvLockedScreenWithInfo!=null) {
        	updateLockedScreenWithInfoView();
        	mWidgetManager.updateAppWidget(mCnLockedScreenWithInfo, mRvLockedScreenWithInfo);
        }
    };

	static final private void updateLockedScreenWithInfoView() {
        if (mRvLockedScreenWithInfo!=null) {
        	mLockedScreenInfoViewText.setLength(0);
        	if (mEnvParms.nextScheduleTimeString!=null) {
        		mLockedScreenInfoViewText.append("Timer=")
        			.append(mEnvParms.nextScheduleTimeString);
        	} else mLockedScreenInfoViewText.append("Timer=none");
        	mLockedScreenInfoViewText.append("\n")
        		.append("Active task=")
        		.append(mEnvParms.statsActiveTaskCountString);
        	mRvLockedScreenWithInfo.setTextViewText(R.id.device_layout_info_view, mLockedScreenInfoViewText);
        }
    };

    static final private void setBluetoothOff() {
		if (BluetoothAdapter.getDefaultAdapter()==null) return;
        if (mEnvParms.bluetoothIsActive) {
    		setBluetoothButtonIcon(mRvBluetooth,true);
        	BluetoothAdapter.getDefaultAdapter().disable();
        	if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(1,"I","setBluetoothOff Bluetooth off");
        }
	};
	
	static final private void setBluetoothOn() {
		if (BluetoothAdapter.getDefaultAdapter()==null) return;
        if (!mEnvParms.bluetoothIsActive) {
        	setBluetoothButtonIcon(mRvBluetooth,true);
        	BluetoothAdapter.getDefaultAdapter().enable();
        	if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(1,"I","setBluetoothOn Bluetooth on");
        }
	};

	static final private void setWifiOn() {
    	WifiManager wm =(WifiManager)mAppContext.getSystemService(Context.WIFI_SERVICE);
		int ws=-1;
		if (wm!=null) ws=wm.getWifiState();
    	if (!mEnvParms.wifiIsActive) {
    		setWifiButtonIcon(mRvWifi,true);
    		wm.setWifiEnabled(true);
    		if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(1,"I",
    				"setWifiOn WIFI On"+", wifiMgrStatus="+ws);
    	} else {
    		if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(1,"I",
    				"setWifiOn WIFI already On"+", wifiMgrStatus="+ws);
    	}
    };
    
    final static private void setWifiOff() {
    	WifiManager wm =(WifiManager)mAppContext.getSystemService(Context.WIFI_SERVICE);
		int ws=-1;
		if (wm!=null) ws=wm.getWifiState();
    	if (mEnvParms.wifiIsActive) {
    		setWifiButtonIcon(mRvWifi,true);
    		wm.setWifiEnabled(false);
    		if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(1,"I",
    				"setWifiOff WIFI Off"+", wifiMgrStatus="+ws);
    	} else {
    		if (mEnvParms.settingDebugLevel!=0) mUtil.addDebugMsg(1,"I",
    				"setWifiOff WIFI Already Off"+", wifiMgrStatus="+ws);
    	}
    };
}
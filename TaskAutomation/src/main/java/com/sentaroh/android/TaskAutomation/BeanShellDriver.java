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

import java.util.Locale;

import com.sentaroh.android.Utilities.StringUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;

@SuppressLint("DefaultLocale")
public class BeanShellDriver {
	private TaskManagerParms taskMgrParms; 
	private EnvironmentParms envParms;
	private CommonUtilities util;
	private GlobalParameters mGp=null;
	private TaskResponse taskResponse;
	private ActionResponse actionResponse;
	public String action_name=null;
	public String action_dialog_id=null;
	
	public BeanShellDriver(TaskManagerParms tmp, EnvironmentParms ep, CommonUtilities ut,
			TaskResponse tr, ActionResponse ar, TaskActionItem tai, GlobalParameters gp) {
		taskMgrParms=tmp;
		envParms=ep;
        mGp=gp;
		util=ut;
		taskResponse=tr;
		actionResponse=ar;
		if (tai!=null) {
			action_name=tai.action_name;
			action_dialog_id=tai.action_dialog_id;
		}
	}

	public void setBeanShellMethod(TaskManagerParms tmp, EnvironmentParms ep, CommonUtilities ut,
			TaskResponse tr, ActionResponse ar, TaskActionItem tai) {
		taskMgrParms=tmp;
		envParms=ep;
		util=ut;
		taskResponse=tr;
		actionResponse=ar;
		if (tai!=null) {
			action_name=tai.action_name;
			action_dialog_id=tai.action_dialog_id;
		} else {
			action_name=null;
			action_dialog_id=null;
		}
	}

	final static private void notifyToActivityStarted(TaskManagerParms tmp, EnvironmentParms ep,
			CommonUtilities util, TaskResponse tr, ActionResponse ar, String mn, GlobalParameters gp) {
		tr.resp_time=StringUtil.convDateTimeTo_HourMinSecMili(System.currentTimeMillis());
		TaskManager.callBackToActivity(tmp,ep,util,
				tr.resp_time,NTFY_TO_ACTV_ACTION_TACMD_STARTED,
				tr.active_group_name,
				tr.active_task_name,mn,null,
				tr.active_dialog_id,ActionResponse.ACTION_SUCCESS,"", gp);
	}
	final static private void notifyToActivityEnded(TaskManagerParms tmp, EnvironmentParms ep,
			CommonUtilities util, TaskResponse tr, ActionResponse ar, String mn, GlobalParameters gp) {
		tr.resp_time=StringUtil.convDateTimeTo_HourMinSecMili(System.currentTimeMillis());
		TaskManager.callBackToActivity(tmp,ep,util,
				tr.resp_time,NTFY_TO_ACTV_ACTION_TACMD_ENDED,
				tr.active_group_name,
				tr.active_task_name,mn,null,
				tr.active_dialog_id,ar.action_resp,ar.resp_msg_text, gp);
	}
	final public void debugMsg(int lvl, String cat, String msg) {
		util.addDebugMsg(lvl, cat.toUpperCase(Locale.getDefault()), "Bsh DebugMsg ",msg);
	}
	final public void logMsg(String cat, String msg) {
		util.addLogMsg(cat.toUpperCase(Locale.getDefault()),        "Bsh LogMsg   ",msg);
	}

	final public boolean isOrientationLandscape() throws Exception{
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		boolean result=envParms.isOrientationLanscape();
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, result="+result);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+result;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return result;
	}
	
	final public boolean isLocationProviderAvailable()throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		boolean result=TaskExecutor.isLocationProviderAvailable(taskMgrParms, envParms, util);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, result="+result);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+result;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return result;
	}

	final public boolean isGpsLocationProviderAvailable()throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		boolean result=TaskExecutor.isGpsLocationProviderAvailable(taskMgrParms, envParms, util);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, result="+result);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+result;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return result;
	}

	final public boolean isNetworkLocationProviderAvailable()throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		boolean result=TaskExecutor.isNetworkLocationProviderAvailable(taskMgrParms, envParms, util);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, result="+result);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+result;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return result;
	}

	final public boolean activateAvailableLocationProvider()throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		boolean result=TaskExecutor.activateAvailableLocationProvider(taskMgrParms, envParms, util);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, result="+result);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+result;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return result;
	}

	final public boolean activateGpsLocationProvider()throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		boolean result=TaskExecutor.activateGpsLocationProvider(taskMgrParms, envParms, util);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, result="+result);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+result;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return result;
	}

	final public boolean activateNetworkLocationProvider()throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		boolean result=TaskExecutor.activateNetworkLocationProvider(taskMgrParms, envParms, util);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, result="+result);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+result;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return result;
	}
	
	final public void deactivateLocationProvider()throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		TaskExecutor.deactivateLocationProvider(taskMgrParms, envParms, util);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed");
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="";
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
	}
	
	final public String[] getCurrentLocation()throws Exception {
		return getCurrentLocation(60);
	}

	final public String[] getCurrentLocation(int to)throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		Location loc=null;
		int lp_cnt=0;
		while(loc==null && lp_cnt<to) {
			loc=TaskExecutor.getCurrentLocation(taskMgrParms, envParms, util);
			if (loc==null) {
				synchronized(taskResponse.active_thread_ctrl) {
					try {
						taskResponse.active_thread_ctrl.wait(1000);
						checkCancel(taskResponse,actionResponse);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				lp_cnt++;
			} else {
				break;
			}
		}
		double[] res_tmp=new double[]{-9999f,-9999f,-9999f};
		String provider="";
		if (loc!=null) {
			provider=loc.getProvider();
			res_tmp[0]=loc.getAltitude();
			res_tmp[1]=loc.getLatitude();
			res_tmp[2]=loc.getLongitude();
		} 
		String[] result=new String[]{provider,String.valueOf(res_tmp[0]),String.valueOf(res_tmp[1]),String.valueOf(res_tmp[2])};
		
		if (mGp.settingDebugLevel>=1) {
				util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName(),
						" executed, Provider="+provider+", Altitude="+result[0]+", Latitude="+result[1]+", Longitude="+result[2]);
		}
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="Provider="+result[0]+", Altitude="+result[1]+", Latitude="+result[2]+", Longitude="+result[3];
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return result;
	}

	final public String[] getLastKnownLocation()throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		Location loc=TaskExecutor.getLastKnownLocation(taskMgrParms, envParms, util);
		double[] res_tmp=new double[]{-9999f,-9999f,-9999f};
		String provider="";
		if (loc!=null) {
			provider=loc.getProvider();
			res_tmp[0]=loc.getAltitude();
			res_tmp[1]=loc.getLatitude();
			res_tmp[2]=loc.getLongitude();
		}
		String[] result=new String[]{provider,String.valueOf(res_tmp[0]),String.valueOf(res_tmp[1]),String.valueOf(res_tmp[2])};
		
		if (mGp.settingDebugLevel>=1) {
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, Provider="+provider+", Altitude="+result[0]+", Latitude="+result[1]+", Longitude="+result[2]);
		}
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="Provider="+result[0]+", Altitude="+result[1]+", Latitude="+result[2]+", Longitude="+result[3];
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return result;
	}

	final public String[] getLastKnownLocationGpsProvider()throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		Location loc=TaskExecutor.getLastKnownLocationGpsProvider(taskMgrParms, envParms, util);
		double[] res_tmp=new double[]{-9999f,-9999f,-9999f};
		String provider="";
		if (loc!=null) {
			provider=loc.getProvider();
			res_tmp[0]=loc.getAltitude();
			res_tmp[1]=loc.getLatitude();
			res_tmp[2]=loc.getLongitude();
		}
		String[] result=new String[]{provider,String.valueOf(res_tmp[0]),String.valueOf(res_tmp[1]),String.valueOf(res_tmp[2])};
		
		if (mGp.settingDebugLevel>=1) {
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, Provider="+provider+", Altitude="+result[0]+", Latitude="+result[1]+", Longitude="+result[2]);
		}
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="Provider="+result[0]+", Altitude="+result[1]+", Latitude="+result[2]+", Longitude="+result[3];
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return result;
	}

	final public String[] getLastKnownLocationNetworkProvider()throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		Location loc=TaskExecutor.getLastKnownLocationNetworkProvider(taskMgrParms, envParms, util);
		double[] res_tmp=new double[]{-9999f,-9999f,-9999f};
		String provider="";
		if (loc!=null) {
			provider=loc.getProvider();
			res_tmp[0]=loc.getAltitude();
			res_tmp[1]=loc.getLatitude();
			res_tmp[2]=loc.getLongitude();
		}
		String[] result=new String[]{provider,String.valueOf(res_tmp[0]),String.valueOf(res_tmp[1]),String.valueOf(res_tmp[2])};
		
		if (mGp.settingDebugLevel>=1) {
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, Provider="+provider+", Altitude="+result[0]+", Latitude="+result[1]+", Longitude="+result[2]);
		}
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="Provider="+result[0]+", Altitude="+result[1]+", Latitude="+result[2]+", Longitude="+result[3];
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return result;
	}

	final public boolean isBluetoothActive() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, result="+envParms.bluetoothIsActive);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+envParms.bluetoothIsActive;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return envParms.bluetoothIsActive;
	}
	final public boolean isBluetoothConnected() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		boolean result=false;
		if (envParms.bluetoothIsActive) result=envParms.isBluetoothConnected();
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, result="+result);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+result;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return result;
	}
	final public String getBluetoothDeviceName() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, result="+envParms.blutoothConnectedDeviceName);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+envParms.blutoothConnectedDeviceName;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return envParms.blutoothConnectedDeviceName;
	}
	final public String getBluetoothDeviceAddr() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, result="+envParms.blutoothConnectedDeviceAddr);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+envParms.blutoothConnectedDeviceAddr;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return envParms.blutoothConnectedDeviceAddr;
	}
	final public int getBluetoothConnectedDeviceListCount() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		int result=envParms.getBluetoothConnectedDeviceListCount();
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, result="+result);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+result;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return result;
	}
	final public String getBluetoothConnectedDeviceNameAtPos(int pos) throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		String result=envParms.getBluetoothConnectedDeviceNameAtPos(pos);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, result="+result);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+result;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return result;
	}
	final public String getBluetoothConnectedDeviceAddrAtPos(int pos) throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		String result=envParms.getBluetoothConnectedDeviceAddrAtPos(pos);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, result="+result);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+result;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return result;
	}
	final public int getAndroidSdkInt() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, result="+Build.VERSION.SDK_INT);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+Build.VERSION.SDK_INT;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return Build.VERSION.SDK_INT;
	}
	final public boolean isAirplaneModeOn() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		boolean result=envParms.isAirplaneModeOn();
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, result="+result);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+result;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return result;
	}
	final public int getBatteryLevel() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, result="+envParms.batteryLevel);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+envParms.batteryLevel;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return envParms.batteryLevel;
	}
	final public boolean isBatteryCharging() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		boolean result=envParms.isBatteryCharging();
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, result="+result);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+result;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return result;
	}
	final public boolean isRingerModeNormal() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		boolean result=envParms.isRingerModeNormal();
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, result="+result);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+result;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return result;
	}
	final public boolean isRingerModeVibrate() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		boolean result=envParms.isRingerModeVibrate();
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, result="+result);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+result;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return result;
	}
	final public boolean isRingerModeSilent() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		boolean result=envParms.isRingerModeSilent();
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, result="+result);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+result;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return result;
	}
	final public int getLightSensorValue() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, result="+envParms.lightSensorValue);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+envParms.lightSensorValue;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return envParms.lightSensorValue;
	}
	final public boolean isMobileNetworkConnected() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		boolean result=envParms.mobileNetworkIsConnected;
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName(),"  executed, result="+result);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+result;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return result;
	}
	final public boolean isProximitySensorDetected() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		boolean result=envParms.isProximitySensorDetected();
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, result="+result+
					", value="+envParms.proximitySensorValue);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+result;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return result;
	}
	final public boolean isScreenLocked() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		boolean result=envParms.screenIsLocked;
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, result="+result);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+result;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return result;
	}
	final public boolean isScreenOn() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		boolean result=envParms.screenIsOn;
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, result="+result);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+result;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return result;
	}
	final public boolean isTelephonyCallStateIdle() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		boolean result=envParms.isTelephonyCallStateIdle();
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, result="+result);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+result;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return result;
	}
	final static private String getExecutedMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		return name+"()";
	}
	final static private String getExecutedMethodName(String val) {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		return name+"("+val+")";
	}
	
	final public boolean isTelephonyCallStateRinging() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		boolean result=envParms.isTelephonyCallStateRinging();
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, result="+result);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+result;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return result;
	}
	final public boolean isTelephonyCallStateOffhook() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		boolean result=envParms.isTelephonyCallStateOffhook();
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, result="+result);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+result;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return result;
	}
	final public boolean isWifiActive() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		boolean result=envParms.wifiIsActive;
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, result="+result);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+result;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return result;
	}
	final public boolean isWifiConnected() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		boolean result=false;
		if (envParms.wifiIsActive) result=envParms.isWifiConnected();
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, result="+result);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+result;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return result;
	}
	final public String getWifiSsidName() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, result="+envParms.wifiConnectedSsidName);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+envParms.wifiConnectedSsidName;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return envParms.wifiConnectedSsidName;
	}
	final public String getWifiSsidAddr() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed, result="+envParms.wifiConnectedSsidName);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="result="+envParms.wifiConnectedSsidName;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return envParms.wifiConnectedSsidAddr;
	}
	
	final static private void checkCancel(TaskResponse tr, ActionResponse ar) throws Exception {
		if (!tr.active_thread_ctrl.isEnabled()) {
			ar.action_resp=ActionResponse.ACTION_CANCELLED;
			ar.action_resp=ActionResponse.ACTION_CANCELLED;
			throw new Exception("TaskAutomationCancelException");
		}
	}
	
	final public void abort() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed.");
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="";
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		throw new Exception("TaskAutomationAbortException");
	}
	 
	final public void startActivityAddExtra(Intent in, String key, String value) throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		in.putExtra(key,value);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed.");
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="";
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
	}
	final public void startActivityAddExtra(Intent in, String key, String[] value) throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		in.putExtra(key,value);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed.");
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="";
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
	}
	final public void startActivityAddExtra(Intent in, String key, int value) throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		in.putExtra(key,value);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed.");
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="";
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
	}
	final public void startActivityAddExtra(Intent in, String key, int[] value) throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		in.putExtra(key,value);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed.");
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="";
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
	}
	final public void startActivityAddExtra(Intent in, String key, boolean value) throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		in.putExtra(key,value);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed.");
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="";
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
	}
	final public void startActivityAddExtra(Intent in, String key, boolean[] value) throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		in.putExtra(key,value);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed.");
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="";
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
	}
	final public Intent startActivityBuildIntent(String pkgname) throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(pkgname), mGp);
		Intent result=null;
		checkCancel(taskResponse,actionResponse);
		final PackageManager pm = taskMgrParms.context.getPackageManager();
		Intent in=pm.getLaunchIntentForPackage(pkgname);
		actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
		if (in!=null) {
			in.setAction(Intent.ACTION_VIEW);
			result=in;
		} else {
			actionResponse.action_resp=ActionResponse.ACTION_ERROR;
			actionResponse.resp_msg_text="Package does not exitst, name="+pkgname;
		}
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed. result=",actionResponse.resp_msg_text);
		if (taskResponse.task_action_notification) {
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(pkgname), mGp);
		}
		return result;
	}
	final public boolean startActivity(Intent in) throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName("Your specified intent"), mGp);
		checkCancel(taskResponse,actionResponse);
		taskMgrParms.context.startActivity(in);
		taskResponse.active_thread_ctrl.setThreadResultSuccess();
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed.");
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="";
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName("Your specified intent"), mGp);
		}
		return true;
	}
	
	final public boolean startActivity(String pkgname) throws Exception {
		boolean result=startActivity(pkgname,null);
		return result;
	}

	final public boolean startActivity(String pkgname, String uri) throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(pkgname), mGp);
		boolean result=false;
		checkCancel(taskResponse,actionResponse);
		final PackageManager pm = taskMgrParms.context.getPackageManager();
		Intent in=pm.getLaunchIntentForPackage(pkgname);
		actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
		if (in!=null) {
			if (uri!=null && !uri.equals("")) {
				in.setData(Uri.parse(uri));
			}
			in.setAction(Intent.ACTION_VIEW);
			taskMgrParms.context.startActivity(in);
			taskResponse.active_thread_ctrl.setThreadResultSuccess();
			result=true;
		} else {
			actionResponse.action_resp=ActionResponse.ACTION_ERROR;
			actionResponse.resp_msg_text="Package does not exitst, name="+pkgname;
			result=false;
		}
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed. result=",actionResponse.resp_msg_text);
		if (taskResponse.task_action_notification) {
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(pkgname), mGp);
		}
		return result;
	}
	
	final public void addIntentExtraData(Intent in, String key, String value) throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		in.putExtra(key,value);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed.");
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="";
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
	}
	final public void addIntentExtraData(Intent in, String key, String[] value) throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		in.putExtra(key,value);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed.");
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="";
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
	}
	final public void addIntentExtraData(Intent in, String key, int value) throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		in.putExtra(key,value);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed.");
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="";
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
	}
	final public void addIntentExtraData(Intent in, String key, int[] value) throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		in.putExtra(key,value);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed.");
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="";
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
	}
	final public void addIntentExtraData(Intent in, String key, boolean value) throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		in.putExtra(key,value);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed.");
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="";
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
	}
	final public void addIntentExtraData(Intent in, String key, boolean[] value) throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		in.putExtra(key,value);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed.");
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="";
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
	}
	final public Intent buildIntent() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		Intent result=new Intent();
		actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed. result=",actionResponse.resp_msg_text);
		if (taskResponse.task_action_notification) {
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return result;
	}
	final public void setIntentAction(Intent in, String action) throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(action), mGp);
		checkCancel(taskResponse,actionResponse);
		actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
		in.setAction(action);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed. result=",actionResponse.resp_msg_text);
		if (taskResponse.task_action_notification) {
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(action), mGp);
		}
	}

	final public boolean sendBroadcastIntent(Intent in) throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName("Your specified intent"), mGp);
		checkCancel(taskResponse,actionResponse);
		taskMgrParms.context.sendBroadcast(in);
		taskResponse.active_thread_ctrl.setThreadResultSuccess();
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed.");
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="";
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName("Your specified intent"), mGp);
		}
		return true;
	}
	
	
	
	final public void showMessageDialog(String msg_text, boolean use_vibrator) throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		showMessage(PROFILE_ACTION_TYPE_MESSAGE_DIALOG, msg_text, use_vibrator, false, "");
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed.");
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
	}
	final public void showMessageNotification(String msg_text, boolean use_vibrator, 
			boolean use_led, String led_color) throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		showMessage(PROFILE_ACTION_TYPE_MESSAGE_NOTIFICATION, msg_text, 
				use_vibrator, use_led, led_color.toUpperCase(Locale.getDefault()));
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed.");
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
	}
	final private void showMessage(String msg_type, String msg_text, 
			boolean use_vibrator, boolean use_led, String led_color) throws Exception {
		checkCancel(taskResponse,actionResponse);
		TaskExecutor.showMessage(taskMgrParms,envParms,util,taskResponse, 
				actionResponse,action_name, action_dialog_id,
				msg_type, msg_text, use_vibrator, use_led, led_color,false);
	}
	final public void resetIntervalTimer(String timer_name) throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(timer_name), mGp);
		checkCancel(taskResponse,actionResponse);
		TaskExecutor.resetIntervalTimer(taskMgrParms,envParms,util,taskResponse,
				actionResponse, action_name, action_dialog_id,
				PROFILE_ACTION_TYPE_TIME_RESET_INTERVAL_TIMER, timer_name);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed. timer=", timer_name);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="";
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(timer_name), mGp);
		}
	}
	final public void startTask(String task_name) throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(task_name), mGp);
		checkCancel(taskResponse,actionResponse);
		TaskExecutor.taskTriggerTaskControl(taskMgrParms, envParms, util, taskResponse, 
				actionResponse, action_name, action_dialog_id,
				PROFILE_ACTION_TYPE_TASK_START_TASK, task_name, mGp);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName(task_name)," executed. task=",task_name);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="";
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(task_name), mGp);
		}
	}
	final public void cancelTask(String task_name) throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(task_name), mGp);
		checkCancel(taskResponse,actionResponse);
		TaskExecutor.taskTriggerTaskControl(taskMgrParms, envParms, util, taskResponse, 
				actionResponse, action_name, action_dialog_id,
				PROFILE_ACTION_TYPE_TASK_CANCEL_TASK, task_name, mGp);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName(task_name)," executed. task=",task_name);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="";
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(task_name), mGp);
		}
	}
	final public boolean playBackRingtone(String rt, String rn) throws Exception {
		return playBackRingtone(rt,rn,-1,-1);
	}
	final public boolean playBackRingtone(String rt, String rn, int v_l, int v_r) throws Exception {
//		taskResponse.active_action_name=action;
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(rt+","+rn), mGp);
		boolean result=false;
		int r_type=RingtoneManager.TYPE_NOTIFICATION;
		if (rt.equalsIgnoreCase(PROFILE_ACTION_RINGTONE_TYPE_ALERT) ||
				rt.equalsIgnoreCase(PROFILE_ACTION_RINGTONE_TYPE_ALARM)
				) r_type=RingtoneManager.TYPE_ALARM;
		else if (rt.equalsIgnoreCase(PROFILE_ACTION_RINGTONE_TYPE_RINGTONE)) r_type=RingtoneManager.TYPE_RINGTONE;
		
        RingtoneManager rm = new RingtoneManager(taskMgrParms.context);
        rm.setType(r_type);
        Cursor cursor = rm.getCursor();
        int idx=0;
        Uri r_uri=null;
        while (cursor.moveToNext()) {
        	if (cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX).equals(rn)) {
        		r_uri=rm.getRingtoneUri(idx);	
        	}
        	idx++;
        }
        cursor.close();
        if (r_uri!=null) {
    		TaskExecutor.playBackRingtone(taskMgrParms, envParms, util, taskResponse,
    				actionResponse, action_dialog_id,
    				rt.toUpperCase(Locale.getDefault()), rn, r_uri.getPath(),
    				String.valueOf(v_l), String.valueOf(v_r), mGp);
    		checkCancel(taskResponse,actionResponse);
    		if (actionResponse.action_resp==ActionResponse.ACTION_SUCCESS) result=true;
    		else {
    			util.addLogMsg("W", actionResponse.resp_msg_text);
    		}
        } else {
        	actionResponse.action_resp=ActionResponse.ACTION_ERROR;
        	actionResponse.resp_msg_text=String.format(taskMgrParms.teMsgs.msgs_thread_task_play_ringtone_notfound,rn);
        }
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed. result=",actionResponse.resp_msg_text);
		if (taskResponse.task_action_notification) notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(rt+","+rn), mGp);
		return result;
	}
	final public boolean playBackMusic(String fpath) throws Exception {
		return playBackMusic(fpath,-1,-1);
	}
	final public boolean playBackMusic(String fpath, int v_l, int v_r) throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(fpath), mGp);
		boolean result=false;
		TaskExecutor.playBackMusic(taskMgrParms,envParms,util,taskResponse,
				actionResponse, action_name, action_dialog_id,
				fpath, String.valueOf(v_l), String.valueOf(v_r), mGp);
		checkCancel(taskResponse,actionResponse);
		if (actionResponse.action_resp==ActionResponse.ACTION_SUCCESS) result=true;
		else {
			util.addLogMsg("W", actionResponse.resp_msg_text);
		}
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed. result=",actionResponse.resp_msg_text);
		if (taskResponse.task_action_notification) notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(fpath), mGp);
		return result;
	}

	final public boolean setAutoSyncEnabled() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		boolean result=TaskExecutor.setAutoSyncEnabled(taskMgrParms, envParms, util, taskResponse, actionResponse);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed. result="+result);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return result;
	}
	final public boolean setAutoSyncDisabled() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		boolean result=TaskExecutor.setAutoSyncDisabled(taskMgrParms, envParms, util, taskResponse, actionResponse);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed. result="+result);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		return result;
	}
	final public void setBluetoothOn() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		TaskExecutor.setBluetoothOn(taskMgrParms,envParms,util,taskResponse,actionResponse, mGp);
		if (actionResponse.action_resp==ActionResponse.ACTION_SUCCESS) {
			while(taskResponse.active_thread_ctrl.isEnabled()) {
				if (envParms.bluetoothIsActive) break;
				synchronized(taskResponse.active_thread_ctrl) {
					try {
						taskResponse.active_thread_ctrl.wait(20);
						checkCancel(taskResponse,actionResponse);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed. result=",actionResponse.resp_msg_text);
		if (taskResponse.task_action_notification) notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
	}
	final public void setBluetoothOff() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		TaskExecutor.setBluetoothOff(taskMgrParms,envParms,util,taskResponse,actionResponse, mGp);
		while(taskResponse.active_thread_ctrl.isEnabled()) {
			if (!envParms.bluetoothIsActive) break;
			synchronized(taskResponse.active_thread_ctrl) {
				try {
					taskResponse.active_thread_ctrl.wait(20);
					checkCancel(taskResponse,actionResponse);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed. result=",actionResponse.resp_msg_text);
		if (taskResponse.task_action_notification) notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
	}
	final public boolean playBackDefaultAlarm() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		boolean result=TaskExecutor.playBackDefaultAlarm(taskMgrParms, envParms, util, taskResponse, actionResponse, mGp);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed. result="+result);
		if (taskResponse.task_action_notification) notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		return result;
	}
	final public boolean playBackDefaultNotification() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		boolean result=TaskExecutor.playBackDefaultNotification(taskMgrParms, envParms, util, taskResponse, actionResponse, mGp);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed. result="+result);
		if (taskResponse.task_action_notification) notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		return result;
	}
	final public boolean playBackDefaultRingtone() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		boolean result=TaskExecutor.playBackDefaultRingtone(taskMgrParms, envParms, util, taskResponse, actionResponse, mGp);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed. result="+result);
		if (taskResponse.task_action_notification) notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		return result;
	}
	final public void restartScheduler() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed.");
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
		TaskExecutor.sendCmdToService(taskResponse, BUILTIN_ACTION_RESTART_SCHEDULER, action_dialog_id,
				CMD_THREAD_TO_SVC_RESTART_SCHEDULER,BUILTIN_ACTION_RESTART_SCHEDULER);
	}
	final public void setRingerModeVibrate() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		AudioManager am=(AudioManager)taskMgrParms.context.getSystemService(Context.AUDIO_SERVICE);
		am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
		
//		am.setStreamMute(AudioManager.STREAM_MUSIC, true);
//		am.setStreamMute(AudioManager.STREAM_RING, true);
//		am.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
//      am.setStreamMute(AudioManager.STREAM_SYSTEM, true);
//        am.setStreamMute(AudioManager.STREAM_ALARM, true);

		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed.");
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
	}
	final public void setRingerModeNormal() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		AudioManager am=(AudioManager)taskMgrParms.context.getSystemService(Context.AUDIO_SERVICE);
		am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

//		am.setStreamMute(AudioManager.STREAM_MUSIC, false);
//		am.setStreamMute(AudioManager.STREAM_RING, false);
//		am.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
//      am.setStreamMute(AudioManager.STREAM_SYSTEM, false);
//        am.setStreamMute(AudioManager.STREAM_ALARM, false);

        if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed.");
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="";
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
	}
	final public void setRingerModeSilent() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		AudioManager am=(AudioManager)taskMgrParms.context.getSystemService(Context.AUDIO_SERVICE);
		am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		
//		am.setStreamMute(AudioManager.STREAM_MUSIC, true);
//		am.setStreamMute(AudioManager.STREAM_RING, true);
//		am.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
//      am.setStreamMute(AudioManager.STREAM_SYSTEM, true);
//		am.setStreamMute(AudioManager.STREAM_ALARM, true);
		
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed.");
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="";
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
	}

	final public void setScreenOnSync() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		if (Build.VERSION.SDK_INT>=17) TaskExecutor.setScreenOnAsync(taskMgrParms,taskResponse,actionResponse);
		else TaskExecutor.setScreenOnSync(taskMgrParms,taskResponse,actionResponse);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed.");
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="";
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
	}
	final public void setScreenOnAsync() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		TaskExecutor.setScreenOnAsync(taskMgrParms,taskResponse,actionResponse);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed.");
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="";
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
	}
	final public void setScreenSwitchToHome() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		TaskExecutor.setScreenSwitchToHome(taskMgrParms, envParms, util, taskResponse, actionResponse);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed.");
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="";
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
	}
	final public void vibrateDefaultPattern() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		TaskExecutor.vibrateDefaultPattern(taskMgrParms.context,actionResponse);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed.");
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
	}
	final public boolean setWifiSsidDisabled() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		boolean result=TaskExecutor.setWifiDisableSsid(taskMgrParms, envParms, util, taskResponse, actionResponse);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed. result="+result);
		if (taskResponse.task_action_notification) notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		return result;
	}
	final public boolean setWifiSsidRemoved() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		boolean result=TaskExecutor.setWifiRemoveSsid(taskMgrParms, envParms, util, taskResponse, actionResponse);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed. result="+result);
		if (taskResponse.task_action_notification) notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		return result;
	}
	
	final public void setWifiOn() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		TaskExecutor.setWifiOn(taskMgrParms,envParms,util,taskResponse,actionResponse, mGp);
		if (actionResponse.action_resp==ActionResponse.ACTION_SUCCESS) {
			WifiManager wm = (WifiManager)taskMgrParms.context.getSystemService(Context.WIFI_SERVICE);
			while(taskResponse.active_thread_ctrl.isEnabled()) {
				if (wm.getWifiState()==WifiManager.WIFI_STATE_UNKNOWN) {
					actionResponse.action_resp=ActionResponse.ACTION_WARNING;
					actionResponse.resp_msg_text="Wifi on error";
					break;
				}
				if (envParms.wifiIsActive) break;
				synchronized(taskResponse.active_thread_ctrl) {
					try {
						taskResponse.active_thread_ctrl.wait(20);
						checkCancel(taskResponse,actionResponse);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed. result=",actionResponse.resp_msg_text);
		if (taskResponse.task_action_notification) notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
	}
	final public void setWifiOff() throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		checkCancel(taskResponse,actionResponse);
		TaskExecutor.setWifiOff(taskMgrParms,envParms,util,taskResponse,actionResponse, mGp);
		while(taskResponse.active_thread_ctrl.isEnabled()) {
			if (!envParms.wifiIsActive) break;
			synchronized(taskResponse.active_thread_ctrl) {
				try {
					taskResponse.active_thread_ctrl.wait(20);
					checkCancel(taskResponse,actionResponse);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed. result=",actionResponse.resp_msg_text);
		if (taskResponse.task_action_notification) notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
	}
	final public void waitSeconds(String wt) throws Exception {
		waitSeconds(Integer.valueOf(wt));
	}
	final public void waitSeconds(int wt) throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(String.valueOf(wt)), mGp);
		synchronized(taskResponse.active_thread_ctrl) {
			try {
				taskResponse.active_thread_ctrl.wait(wt*1000);
				checkCancel(taskResponse,actionResponse);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName(String.valueOf(wt))," executed.");
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="";
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(String.valueOf(wt)), mGp);
		}
	}
//	final public boolean waitUntilBluetoothConnected() throws Exception{
//		return waitUntilBluetoothConnected("0");
//	}
	final public boolean waitUntilBluetoothConnected(String to) throws Exception{
		return waitUntilBluetoothConnected(Integer.valueOf(to));
	}
	final public boolean waitUntilBluetoothConnected(int timeout_seconds) throws Exception{
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(String.valueOf(timeout_seconds)), mGp);
		boolean connected=false;
		if (envParms.bluetoothIsActive) {
			if (!envParms.isBluetoothConnected()) {
				TaskManager.addNotifyEventListItem(
						taskMgrParms.bluetoothNotifyEventList,taskResponse.active_thread_ctrl);
				connected=TaskExecutor.waitDeviceEvent(taskResponse, actionResponse, timeout_seconds*1000);
				TaskManager.removeNotifyEventListItem(
						taskMgrParms.bluetoothNotifyEventList,taskResponse.active_thread_ctrl);
				checkCancel(taskResponse,actionResponse);
			} else {
				connected=true;
				actionResponse.action_resp=ActionResponse.ACTION_WARNING;
				actionResponse.resp_msg_text="Bluetooth already connected";
				if (mGp.settingDebugLevel>=1)
					util.addDebugMsg(1, "I", "Bsh waitUntilBluetoothConnected not executed(Already connected)");
			}
		} else {
			actionResponse.action_resp=ActionResponse.ACTION_WARNING;
			actionResponse.resp_msg_text="Bluetoooth not On";
			if (mGp.settingDebugLevel>=1)
				util.addDebugMsg(1, "I", "Bsh waitUntilBluetoothConnected not executed(Bluetooth was inactive)");
		}
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName(String.valueOf(timeout_seconds))," executed. result="+connected+
					", timeout value="+timeout_seconds);
		if (taskResponse.task_action_notification) notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(String.valueOf(timeout_seconds)), mGp);
		return connected;
	}
	
//	final public boolean waitUntilWifiConnected() throws Exception {
//		return waitUntilWifiConnected("0");
//	}
	final public boolean waitUntilWifiConnected(String to) throws Exception {
		return waitUntilWifiConnected(Integer.valueOf(to));
	}
	final public boolean waitUntilWifiConnected(int timeout_seconds) throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(String.valueOf(timeout_seconds)), mGp);
		boolean connected=false;
		if (envParms.wifiIsActive) {
			if (envParms.wifiConnectedSsidName.equals("")) {
				TaskManager.addNotifyEventListItem(
						taskMgrParms.wifiNotifyEventList,taskResponse.active_thread_ctrl);
				connected=TaskExecutor.waitDeviceEvent(taskResponse, actionResponse, timeout_seconds*1000);
				TaskManager.removeNotifyEventListItem(
						taskMgrParms.wifiNotifyEventList,taskResponse.active_thread_ctrl);
				checkCancel(taskResponse,actionResponse);
			} else {
				connected=true;
				actionResponse.action_resp=ActionResponse.ACTION_WARNING;
				actionResponse.resp_msg_text="Wifi already connected";
				if (mGp.settingDebugLevel>=1)
					util.addDebugMsg(1, "I", "Bsh waitUntilWifiConnected not executed(Already connected)");
			}
		} else {
			actionResponse.action_resp=ActionResponse.ACTION_WARNING;
			actionResponse.resp_msg_text="Wifi not On";
			if (mGp.settingDebugLevel>=1)
				util.addDebugMsg(1, "I", "Bsh waitUntilWifiConnected not executed(Wifi was inactive)");
		}
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName(String.valueOf(timeout_seconds))," executed. result="+connected+
					", timeout value="+timeout_seconds);
		if (taskResponse.task_action_notification) notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(String.valueOf(timeout_seconds)), mGp);
		return connected;
	}

	final public void clearBlockEventAll() {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp); 
		TaskManager.clearBlockActionList(taskMgrParms, envParms, util, mGp);
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",getExecutedMethodName()," executed.");
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,getExecutedMethodName(), mGp);
		}
	}

	final public void addBlockEventAll() throws Exception {addBlockEvent(BUILTIN_EVENT_ALL,getExecutedMethodName(), mGp);};
	final public void addBlockEventWifiOn() throws Exception { addBlockEvent(BUILTIN_EVENT_WIFI_ON,getExecutedMethodName(), mGp); };
	final public void addBlockEventWifiConnected() throws Exception { addBlockEvent(BUILTIN_EVENT_WIFI_CONNECTED,getExecutedMethodName(), mGp); };
	final public void addBlockEventWifiDisconnected() throws Exception { addBlockEvent(BUILTIN_EVENT_WIFI_DISCONNECTED,getExecutedMethodName(), mGp); };
	final public void addBlockEventWifiOff() throws Exception { addBlockEvent(BUILTIN_EVENT_WIFI_OFF,getExecutedMethodName(), mGp); };
	final public void addBlockEventBluetoothOn() throws Exception { addBlockEvent(BUILTIN_EVENT_BLUETOOTH_ON,getExecutedMethodName(), mGp); };
	final public void addBlockEventBluetoothOff() throws Exception { addBlockEvent(BUILTIN_EVENT_BLUETOOTH_OFF,getExecutedMethodName(), mGp); };
	final public void addBlockEventBluetoothConnected() throws Exception { addBlockEvent(BUILTIN_EVENT_BLUETOOTH_CONNECTED,getExecutedMethodName(), mGp); };
	final public void addBlockEventBluetoothDisconnected() throws Exception { addBlockEvent(BUILTIN_EVENT_BLUETOOTH_DISCONNECTED,getExecutedMethodName(), mGp); };
	final public void addBlockEventProximityDetected() throws Exception { addBlockEvent(BUILTIN_EVENT_PROXIMITY_DETECTED,getExecutedMethodName(), mGp); };
	final public void addBlockEventProximityUndetected() throws Exception { addBlockEvent(BUILTIN_EVENT_PROXIMITY_UNDETECTED,getExecutedMethodName(), mGp); };
	final public void addBlockEventLightDetected() throws Exception { addBlockEvent(BUILTIN_EVENT_LIGHT_DETECTED,getExecutedMethodName(), mGp); };
	final public void addBlockEventLightUndetected() throws Exception { addBlockEvent(BUILTIN_EVENT_LIGHT_UNDETECTED,getExecutedMethodName(), mGp); };
	final public void addBlockEventScreenLocked() throws Exception { addBlockEvent(BUILTIN_EVENT_SCREEN_LOCKED,getExecutedMethodName(), mGp); };
	final public void addBlockEventScreenUnlocked() throws Exception { addBlockEvent(BUILTIN_EVENT_SCREEN_UNLOCKED,getExecutedMethodName(), mGp); };
    final public void addBlockEventScreenOn() throws Exception { addBlockEvent(BUILTIN_EVENT_SCREEN_ON,getExecutedMethodName(), mGp); };
    final public void addBlockEventScreenOff() throws Exception { addBlockEvent(BUILTIN_EVENT_SCREEN_OFF,getExecutedMethodName(), mGp); };
	final public void addBlockEventPowerSourceChangedAc() throws Exception { addBlockEvent(BUILTIN_EVENT_POWER_SOURCE_CHANGED_AC,getExecutedMethodName(), mGp); };
	final public void addBlockEventPowerSourceChangedBattery() throws Exception { addBlockEvent(BUILTIN_EVENT_POWER_SOURCE_CHANGED_BATTERY,getExecutedMethodName(), mGp); };
	final public void addBlockEventCallStateIdle() throws Exception { addBlockEvent(BUILTIN_EVENT_PHONE_CALL_STATE_IDLE,getExecutedMethodName(), mGp); };
	final public void addBlockEventCallStateRinging() throws Exception { addBlockEvent(BUILTIN_EVENT_PHONE_CALL_STATE_RINGING,getExecutedMethodName(), mGp); };
	final public void addBlockEventCallStateOffhook() throws Exception { addBlockEvent(BUILTIN_EVENT_PHONE_CALL_STATE_OFF_HOOK,getExecutedMethodName(), mGp); };
	final public void addBlockEventAirplaneModeOn() throws Exception { addBlockEvent(BUILTIN_EVENT_AIRPLANE_MODE_ON,getExecutedMethodName(), mGp); };
	final public void addBlockEventAirplaneModeOff() throws Exception { addBlockEvent(BUILTIN_EVENT_AIRPLANE_MODE_OFF,getExecutedMethodName(), mGp); };
	final public void addBlockEventMobileNetworkConnected() throws Exception { addBlockEvent(BUILTIN_EVENT_MOBILE_NETWORK_CONNECTED,getExecutedMethodName(), mGp); };
	final public void addBlockEventMobileNetworkDisconnected() throws Exception { addBlockEvent(BUILTIN_EVENT_MOBILE_NETWORK_DISCONNECTED,getExecutedMethodName(), mGp); };
	
	final private void addBlockEvent(String event, String method, GlobalParameters gp) throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,method, gp);
		checkCancel(taskResponse,actionResponse);
		try {
			TaskManager.acqLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms, util, gp);
			taskResponse.cmd_tgt_event_name=event;
			TaskManager.addBlockActionListItem(taskMgrParms, envParms, util, taskResponse, gp);
		} finally {
			TaskManager.relLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms, util, gp);
		}
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",method);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="";
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,method, gp);
		}
	}
	
	final public void cancelTaskByEventBootCompleted() throws Exception { cancelTaskByEvent(BUILTIN_EVENT_BOOT_COMPLETED,getExecutedMethodName(), mGp); };
	final public void cancelTaskByEventWifiOn() throws Exception { cancelTaskByEvent(BUILTIN_EVENT_WIFI_ON,getExecutedMethodName(), mGp); };
	final public void cancelTaskByEventWifiConnected() throws Exception { cancelTaskByEvent(BUILTIN_EVENT_WIFI_CONNECTED,getExecutedMethodName(), mGp); };
	final public void cancelTaskByEventWifiDisconnected() throws Exception { cancelTaskByEvent(BUILTIN_EVENT_WIFI_DISCONNECTED,getExecutedMethodName(), mGp); };
	final public void cancelTaskByEventWifiOff() throws Exception { cancelTaskByEvent(BUILTIN_EVENT_WIFI_OFF,getExecutedMethodName(), mGp); };
	final public void cancelTaskByEventBluetoothOn() throws Exception { cancelTaskByEvent(BUILTIN_EVENT_BLUETOOTH_ON,getExecutedMethodName(), mGp); };
	final public void cancelTaskByEventBluetoothOff() throws Exception { cancelTaskByEvent(BUILTIN_EVENT_BLUETOOTH_OFF,getExecutedMethodName(), mGp); };
	final public void cancelTaskByEventBluetoothConnected() throws Exception { cancelTaskByEvent(BUILTIN_EVENT_BLUETOOTH_CONNECTED,getExecutedMethodName(), mGp); };
	final public void cancelTaskByEventBluetoothDisconnected() throws Exception { cancelTaskByEvent(BUILTIN_EVENT_BLUETOOTH_DISCONNECTED,getExecutedMethodName(), mGp); };
	final public void cancelTaskByEventProximityDetected() throws Exception { cancelTaskByEvent(BUILTIN_EVENT_PROXIMITY_DETECTED,getExecutedMethodName(), mGp); };
	final public void cancelTaskByEventProximityUndetected() throws Exception { cancelTaskByEvent(BUILTIN_EVENT_PROXIMITY_UNDETECTED,getExecutedMethodName(), mGp); };
	final public void cancelTaskByEventLightDetected() throws Exception { cancelTaskByEvent(BUILTIN_EVENT_LIGHT_DETECTED,getExecutedMethodName(), mGp); };
	final public void cancelTaskByEventLightUndetected() throws Exception { cancelTaskByEvent(BUILTIN_EVENT_LIGHT_UNDETECTED,getExecutedMethodName(), mGp); };
	final public void cancelTaskByEventScreenLocked() throws Exception { cancelTaskByEvent(BUILTIN_EVENT_SCREEN_LOCKED,getExecutedMethodName(), mGp); };
	final public void cancelTaskByEventScreenUnlocked() throws Exception { cancelTaskByEvent(BUILTIN_EVENT_SCREEN_UNLOCKED,getExecutedMethodName(), mGp); };
    final public void cancelTaskByEventScreenOn() throws Exception { cancelTaskByEvent(BUILTIN_EVENT_SCREEN_ON,getExecutedMethodName(), mGp); };
    final public void cancelTaskByEventScreenOff() throws Exception { cancelTaskByEvent(BUILTIN_EVENT_SCREEN_OFF,getExecutedMethodName(), mGp); };
	final public void cancelTaskByEventPowerSourceChangedAc() throws Exception { cancelTaskByEvent(BUILTIN_EVENT_POWER_SOURCE_CHANGED_AC,getExecutedMethodName(), mGp); };
	final public void cancelTaskByEventPowerSourceChangedBattery() throws Exception { cancelTaskByEvent(BUILTIN_EVENT_POWER_SOURCE_CHANGED_BATTERY,getExecutedMethodName(), mGp); };
	final public void cancelTaskByEventCallStateIdle() throws Exception { cancelTaskByEvent(BUILTIN_EVENT_PHONE_CALL_STATE_IDLE,getExecutedMethodName(), mGp); };
	final public void cancelTaskByEventCallStateRinging() throws Exception { cancelTaskByEvent(BUILTIN_EVENT_PHONE_CALL_STATE_RINGING,getExecutedMethodName(), mGp); };
	final public void cancelTaskByEventCallStateOffhook() throws Exception { cancelTaskByEvent(BUILTIN_EVENT_PHONE_CALL_STATE_OFF_HOOK,getExecutedMethodName(), mGp); };
	final public void cancelTaskByEventAirplaneModeOn() throws Exception { cancelTaskByEvent(BUILTIN_EVENT_AIRPLANE_MODE_ON,getExecutedMethodName(), mGp); };
	final public void cancelTaskByEventAirplaneModeOff() throws Exception { cancelTaskByEvent(BUILTIN_EVENT_AIRPLANE_MODE_OFF,getExecutedMethodName(), mGp); };
	final public void cancelTaskByEventMobileNetworkConnected() throws Exception { cancelTaskByEvent(BUILTIN_EVENT_MOBILE_NETWORK_CONNECTED,getExecutedMethodName(), mGp); };
	final public void cancelTaskByEventMobileNetworkDisconnected() throws Exception { cancelTaskByEvent(BUILTIN_EVENT_MOBILE_NETWORK_DISCONNECTED,getExecutedMethodName(), mGp); };
	final private void cancelTaskByEvent(String event, String method, GlobalParameters gp) throws Exception {
		if (taskResponse.task_action_notification)notifyToActivityStarted(taskMgrParms,envParms,util,taskResponse,actionResponse,method, gp);
		checkCancel(taskResponse,actionResponse);
		try {
			TaskManager.acqLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms, util, gp);
			taskResponse.cmd_tgt_event_name=event;
			TaskManager.cancelTaskByEventId(taskMgrParms, envParms, util, taskResponse, gp);
		} finally {
			TaskManager.relLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms, util, gp);
		}
		if (mGp.settingDebugLevel>=1)
			util.addDebugMsg(1, "I", "Bsh ",method);
		if (taskResponse.task_action_notification) {
			actionResponse.action_resp=ActionResponse.ACTION_SUCCESS;
			actionResponse.resp_msg_text="";
			notifyToActivityEnded(taskMgrParms,envParms,util,taskResponse,actionResponse,method, gp);
		}
	}
}

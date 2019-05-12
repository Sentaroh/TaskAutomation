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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import bsh.EvalError;
import bsh.ParseException;

import com.sentaroh.android.TaskAutomation.Common.ActionResponse;
import com.sentaroh.android.TaskAutomation.Common.ActivityExtraDataItem;
import com.sentaroh.android.TaskAutomation.Common.BshExecEnvListItem;
import com.sentaroh.android.TaskAutomation.Common.EnvironmentParms;
import com.sentaroh.android.TaskAutomation.Common.TaskActionItem;
import com.sentaroh.android.TaskAutomation.Common.TaskHistoryItem;
import com.sentaroh.android.TaskAutomation.Common.TaskListItem;
import com.sentaroh.android.TaskAutomation.Common.TaskManagerParms;
import com.sentaroh.android.TaskAutomation.Common.TaskResponse;
import com.sentaroh.android.TaskAutomation.Common.TrustDeviceItem;
import com.sentaroh.android.Utilities.StringUtil;
import com.sentaroh.android.Utilities.LocalMountPoint;
import com.sentaroh.android.Utilities.ShellCommandUtil;
import com.sentaroh.android.Utilities.ThreadCtrl;

//public class TaskExecutor extends Thread {
public class TaskExecutor implements Runnable {	
	private TaskListItem mTaskListItem=null;
	private CommonUtilities mUtil=null;
	private EnvironmentParms mEnvParms=null;
	private TaskResponse mTaskResponse=null;
	private TaskManagerParms mTaskMgrParms=null;
	private WakeLock mWakelockTask=null;
	
	public TaskExecutor(TaskResponse tr, TaskManagerParms tmp,
			TaskListItem tai, EnvironmentParms sdv) {
		mTaskListItem=tai;
		mEnvParms=sdv;
		mTaskResponse=tr;
		mTaskMgrParms=tmp;
        mTaskResponse.active_group_name=tai.group_name;
        mTaskResponse.active_task_name=tai.task_name;
        mTaskResponse.active_event_name=tai.event_name;
        mTaskResponse.prof_notification=tai.prof_notification;
        mTaskResponse.task_action_notification=tai.task_action_notification;
        mWakelockTask=((PowerManager)mTaskMgrParms.context.getSystemService(Context.POWER_SERVICE))
   	    			.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
   	    			, "TaskAutomation-TaskExecutor");
//        if (TaskManager.isAcqWakeLockRequired(ep)) 
        mWakelockTask.acquire();
//        CommonUtilities.acqWakeLockIfRequired(ep,mWakelockTask);
	};
	
	final public String toSting() {
		return new String("Group="+mTaskListItem.group_name+", Task="+mTaskListItem.task_name+
				", Event="+mTaskListItem.event_name);
	};

	@Override
	final public void run() {
		try {
			long b_time=System.currentTimeMillis();
			
			String log_ident=""+Thread.currentThread().getId();
			mUtil=new CommonUtilities(mTaskMgrParms.context, "Executor",mEnvParms);
			mUtil.setLogId(Thread.currentThread().getName());
			ArrayList<TaskActionItem> taskActionList = mTaskListItem.taskActionList;
	        mTaskResponse.active_thread_id=log_ident;
			mUtil.addLogMsg("I",
				String.format(mTaskMgrParms.teMsgs.msgs_thread_task_started,
						mTaskResponse.active_group_name,mTaskResponse.active_event_name,mTaskResponse.active_task_name));
			
			mTaskResponse.resp_time=StringUtil.convDateTimeTo_HourMinSecMili(System.currentTimeMillis());

	    	TaskManager.callBackToActivity(mTaskMgrParms,mEnvParms,mUtil,
	    			mTaskResponse.resp_time,NTFY_TO_ACTV_TASK_STARTED,
	    			mTaskResponse.active_group_name,mTaskResponse.active_task_name,null,null,null,
	    			TaskResponse.RESP_CODE_SUCCESS,null);
	    	
	    	int alsz=taskActionList.size();
	    	TaskActionItem tai;
			for (int i=0;i<alsz;i++) {
				mTaskResponse.active_thread_ctrl.setThreadMessage("");
				tai=taskActionList.get(i);
				ActionResponse ar=new ActionResponse();
				mTaskResponse.active_action_name=ar.current_action=tai.action_name;
				mTaskResponse.active_dialog_id=tai.action_dialog_id;
				mTaskResponse.active_shell_cmd=tai.action_shell_cmd;
				if (mTaskResponse.task_action_notification) {
					mTaskResponse.resp_time=StringUtil.convDateTimeTo_HourMinSecMili(System.currentTimeMillis());
					TaskManager.callBackToActivity(mTaskMgrParms,mEnvParms,mUtil,
							mTaskResponse.resp_time,NTFY_TO_ACTV_ACTION_STARTED,
							mTaskResponse.active_group_name,
							mTaskResponse.active_task_name,mTaskResponse.active_action_name,mTaskResponse.active_shell_cmd,
							mTaskResponse.active_dialog_id,mTaskResponse.resp_code,mTaskResponse.resp_msg_text);
				}
				String next_action="";
				if ((i+1)<taskActionList.size()) {
					next_action=taskActionList.get(i+1).action_name;
				}
				executeAction(mTaskMgrParms, mEnvParms, mUtil, mTaskResponse, ar,tai,next_action);
				if (ar.action_resp==ActionResponse.ACTION_ABORT) {
					if (ar.resp_msg_text!=null && ar.resp_msg_text.length()>0) mUtil.addLogMsg("I",ar.resp_msg_text);
					if (mTaskResponse.task_action_notification) {
						mTaskResponse.resp_time=StringUtil.convDateTimeTo_HourMinSecMili(System.currentTimeMillis());
						TaskManager.callBackToActivity(mTaskMgrParms,mEnvParms,mUtil,
								mTaskResponse.resp_time,NTFY_TO_ACTV_ACTION_ENDED,
								mTaskResponse.active_group_name,
								mTaskResponse.active_task_name,mTaskResponse.active_action_name,mTaskResponse.active_shell_cmd,
								mTaskResponse.active_dialog_id,ar.action_resp,ar.resp_msg_text);
					}
					break;
				} else if (ar.action_resp==ActionResponse.ACTION_CANCELLED || 
						ar.action_resp==ActionResponse.ACTION_WARNING || 
						ar.action_resp==ActionResponse.ACTION_SUCCESS) {
					if (ar.resp_msg_text!=null && ar.resp_msg_text.length()>0) {
						if (ar.action_resp==ActionResponse.ACTION_CANCELLED ||ar.action_resp==ActionResponse.ACTION_WARNING) {
							mUtil.addLogMsg("W",ar.resp_msg_text);
						} else if (ar.action_resp==ActionResponse.ACTION_SUCCESS) {
							mUtil.addLogMsg("I",ar.resp_msg_text);
						}
					}
					if (mTaskResponse.task_action_notification) {
						mTaskResponse.resp_time=StringUtil.convDateTimeTo_HourMinSecMili(System.currentTimeMillis());
						TaskManager.callBackToActivity(mTaskMgrParms,mEnvParms,mUtil,
								mTaskResponse.resp_time,NTFY_TO_ACTV_ACTION_ENDED,
								mTaskResponse.active_group_name,
								mTaskResponse.active_task_name,mTaskResponse.active_action_name,mTaskResponse.active_shell_cmd,
								mTaskResponse.active_dialog_id,ar.action_resp,ar.resp_msg_text);
					}
				} else if (ar.action_resp==ActionResponse.ACTION_SKIP) {
					if (ar.resp_msg_text!=null && ar.resp_msg_text.length()>0) mUtil.addLogMsg("I",ar.resp_msg_text);
					if (mTaskResponse.task_action_notification) {
						mTaskResponse.resp_time=StringUtil.convDateTimeTo_HourMinSecMili(System.currentTimeMillis());
						TaskManager.callBackToActivity(mTaskMgrParms,mEnvParms,mUtil,
								mTaskResponse.resp_time,NTFY_TO_ACTV_ACTION_ENDED,
								mTaskResponse.active_group_name,
								mTaskResponse.active_task_name,mTaskResponse.active_action_name,mTaskResponse.active_shell_cmd,
								mTaskResponse.active_dialog_id,ActionResponse.ACTION_SUCCESS,ar.resp_msg_text);
					}
					i++;
					if (i<taskActionList.size()) {
						mTaskResponse.active_action_name=ar.current_action=tai.action_name;
						if (mTaskResponse.task_action_notification) {
							mTaskResponse.resp_time=StringUtil.convDateTimeTo_HourMinSecMili(System.currentTimeMillis());
							TaskManager.callBackToActivity(mTaskMgrParms,mEnvParms,mUtil,
									mTaskResponse.resp_time,NTFY_TO_ACTV_ACTION_ENDED,
									mTaskResponse.active_group_name,
									mTaskResponse.active_task_name,mTaskResponse.active_action_name,mTaskResponse.active_shell_cmd,
									mTaskResponse.active_dialog_id,ActionResponse.ACTION_SUCCESS,
									"Action was skipped");
						}
					}
				} else if (ar.action_resp==ActionResponse.ACTION_ERROR) {
//					if (ar.resp_msg_text!=null && ar.resp_msg_text.length()>0) util.addLogMsg("E",ar.resp_msg_text);
					if (mTaskResponse.task_action_notification) {
						mTaskResponse.resp_time=StringUtil.convDateTimeTo_HourMinSecMili(System.currentTimeMillis());
						TaskManager.callBackToActivity(mTaskMgrParms,mEnvParms,mUtil,
								mTaskResponse.resp_time,NTFY_TO_ACTV_ACTION_ENDED,
								mTaskResponse.active_group_name,
								mTaskResponse.active_task_name,mTaskResponse.active_action_name,mTaskResponse.active_shell_cmd,
								mTaskResponse.active_dialog_id,ar.action_resp,ar.resp_msg_text);
					}
					mTaskResponse.active_thread_ctrl.setThreadMessage(ar.resp_msg_text);
					mTaskResponse.active_thread_ctrl.setThreadResultError();
					break;
				}
				if (!mTaskResponse.active_thread_ctrl.isEnabled()) {
					mTaskResponse.active_thread_ctrl.setThreadResultCancelled();
					mTaskResponse.active_thread_ctrl.setThreadMessage("Task was cancelled");
					break;
				}
			}
			mTaskResponse.active_action_name="";
			mTaskResponse.active_dialog_id="";
			if (mTaskMgrParms.schedulerEnabled) processTaskEnd(mTaskMgrParms,mEnvParms,mUtil,mTaskResponse);

			if (mEnvParms.settingDebugLevel>=1) {
				long e_time=System.currentTimeMillis()-b_time;
				mUtil.addDebugMsg(1,"I","Task execution elapsed time="+e_time);
//				if (tr.active_task_name.equals("#QT-Screen-Proximity-undetected")) {
//					if (e_time>11000) {
//						util.addDebugMsg(1,"E","Overtime is detected");
//					}
//				}
			}
		} finally {
			mTaskListItem=null;
			mUtil=null;
			mEnvParms=null;
			mTaskResponse=null;
			mTaskMgrParms=null;
			if (mWakelockTask.isHeld()) mWakelockTask.release();
		}
	};

	final static private void processTaskEnd(TaskManagerParms tmp,
    		EnvironmentParms ep, CommonUtilities util, TaskResponse tr) {
		TaskListItem ati=TaskManager.removeActiveTaskListItem(tmp,ep,util,
				tr.active_group_name,tr.active_task_name);
//		Log.v("","end entered");
		if (ati!=null) {
			String th_result=tr.active_thread_ctrl.getThreadResult();
			if (th_result.equals(ThreadCtrl.THREAD_RESULT_SUCCESS)) {
				util.addLogMsg("I",String.format(tmp.teMsgs.msgs_thread_task_end_success,tr.active_group_name,tr.active_event_name, tr.active_task_name));
				tr.resp_msg_text=tr.active_thread_ctrl.getThreadMessage();
				tr.resp_code=TaskResponse.RESP_CODE_SUCCESS;
				tr.resp_time=StringUtil.convDateTimeTo_HourMinSecMili(System.currentTimeMillis());
			} else if (th_result.equals(ThreadCtrl.THREAD_RESULT_CANCELLED)) {
				util.addLogMsg("W",String.format(tmp.teMsgs.msgs_thread_task_end_cancelled,tr.active_group_name,tr.active_event_name, tr.active_task_name));
				tr.resp_msg_text=tr.active_thread_ctrl.getThreadMessage();
				tr.resp_code=TaskResponse.RESP_CODE_CANCELLED;
				tr.resp_time=StringUtil.convDateTimeTo_HourMinSecMili(System.currentTimeMillis());
			} else if (th_result.equals(ThreadCtrl.THREAD_RESULT_ERROR)) {
//				Log.v("","error entered");
				tr.resp_msg_text=tr.active_thread_ctrl.getThreadMessage();
				tr.resp_code=TaskResponse.RESP_CODE_ERROR;
				tr.resp_time=StringUtil.convDateTimeTo_HourMinSecMili(System.currentTimeMillis());
				util.addLogMsg("E",tr.resp_msg_text);
				util.addLogMsg("E",String.format(tmp.teMsgs.msgs_thread_task_end_error,tr.active_group_name,tr.active_event_name, tr.active_task_name));
		    	if (ati.prof_notification) {
		    		TaskManager.showErrorNotificationMessage(tmp,
		    				"task="+tr.active_task_name+", "+tr.resp_msg_text);
		    	}
			}
			TaskManager.updateTaskHistoryListItem(tmp,ep,util,
					tr.resp_time,tr.active_group_name,
					tr.active_event_name,tr.active_task_name,
					TaskHistoryItem.TASK_HISTORY_TASK_STATUS_ENDED,tr.resp_code,"");
			TaskManager.showNotification(tmp,ep,util);
			TaskManager.callBackToActivity(tmp,ep,util,
					tr.resp_time,NTFY_TO_ACTV_TASK_ENDED,
					tr.active_group_name,
					tr.active_task_name,tr.active_action_name,tr.active_shell_cmd,
					tr.active_dialog_id,tr.resp_code,tr.resp_msg_text);
			TaskManager.rescheduleTask(tmp,ep,util);
			if (tmp.activeTaskList.size()==0 && tmp.taskQueueList.size()==0) {
				tmp.locationUtil.deactivateLocationProvider();
			}
//			TaskManager.resourceCleanup(tmp,ep,util);
		} else {
			TaskManager.acqLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE,ep,tmp,util);
			tmp.schedulerEnabled=false;
    		String msg="Internal error: Active task list was corrupted. activeTaskList size="+tmp.activeTaskList.size()+
    				", group="+tr.active_group_name+", task="+tr.active_task_name;
    		util.addLogMsg("E", msg);
    		for (int i=0;i<tmp.activeTaskList.size();i++) {
    			util.addLogMsg("E", "    ",String.valueOf(i),
    					", group=",tmp.activeTaskList.get(i).group_name,
    					", task=",tmp.activeTaskList.get(i).task_name);
    		}
    		TaskManager.showMessageDialog(tmp,ep,util,
    				"*Syetm","*System","*System","1",
					MESSAGE_DIALOG_MESSAGE_TYPE_DIALOG,msg+"\n"+
							"Scheduler has been restarted.");
			sendCmdToService(tr,"*System","1",
					CMD_THREAD_TO_SVC_FORCE_RESTART_SCHEDULER,"*Interbal error");
//			TaskManager.relLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE,ep,tmp,util);
		}
	};
	
	final static private void executeAction(TaskManagerParms tmp,
    		EnvironmentParms ep, CommonUtilities util, TaskResponse tr,
    		ActionResponse ar, TaskActionItem tai, String next_action) {
		String action_type=tai.action_type;

		if (action_type.equals(PROFILE_ACTION_TYPE_ACTIVITY)) {
			executeAndroidActivity(tmp,ep,util,tr,
					ar,tai.action_activity_name,tai.action_activity_pkgname,tai);
		} else if (action_type.equals(PROFILE_ACTION_TYPE_MUSIC)) {
			executePlayBackMusic(tmp,ep,util,tr,ar,tai.action_name,tai.action_dialog_id,
					tai.action_sound_file_name,
					tai.action_sound_vol_left,
					tai.action_sound_vol_left);
		} else if (action_type.equals(PROFILE_ACTION_TYPE_RINGTONE)) {
			executePlayBackRingtone(tmp,ep,util,tr,ar,tai.action_name,tai.action_dialog_id,
					tai.action_ringtone_type,
					tai.action_ringtone_name,
					tai.action_ringtone_path,
					tai.action_ringtone_vol_left,
					tai.action_ringtone_vol_left);
		} else if (action_type.equals(PROFILE_ACTION_TYPE_COMPARE)) {
			executeCompareAction(tmp,ep,util,tr,ar,tai.action_name,tai.action_dialog_id,
					tai.action_compare_target,
					tai.action_compare_type,
					tai.action_compare_value,
					tai.action_compare_result_action,
					next_action);
		} else if (action_type.equals(PROFILE_ACTION_TYPE_MESSAGE)) {
			executeMessageAction(tmp,ep,util,tr, ar,tai.action_name,tai.action_dialog_id,
					tai.action_message_type,
					tai.action_message_text,
					tai.action_message_use_vib,
					tai.action_message_use_led,
					tai.action_message_led_color);
		} else if (action_type.equals(PROFILE_ACTION_TYPE_TIME)) {
			executeTimeAction(tmp,ep,util,tr,ar,tai.action_name,tai.action_dialog_id,
					tai.action_time_type,tai.action_time_target);
		} else if (action_type.equals(PROFILE_ACTION_TYPE_TASK)) {
			executeTaskAction(tmp,ep,util,tr,ar,tai.action_name,tai.action_dialog_id,
					tai.action_task_type, tai.action_task_target);
		} else if (action_type.equals(PROFILE_ACTION_TYPE_WAIT)) {
			executeWaitAction(tmp,ep,util,tr,ar,tai.action_name,tai.action_dialog_id,
					tai.action_wait_target,tai.action_wait_timeout_value,tai.action_wait_timeout_units);
		} else if (action_type.equals(PROFILE_ACTION_TYPE_BSH_SCRIPT)) {
			executeBeanShellScriptAction(tmp,ep,util,tr,ar,tai);
		} else if (action_type.equals(PROFILE_ACTION_TYPE_SHELL_COMMAND)) {
			if (tai.action_shell_cmd_with_su && !ep.settingUseRootPrivilege) {
				ar.action_resp=ActionResponse.ACTION_ERROR;
				ar.resp_msg_text="Shell command not executed, because shell command required su priviledge but TaskAutomation is not have su priviledge, command="+tai.action_shell_cmd;
			} else {
				if (ep.settingUseRootPrivilege && tai.action_shell_cmd_with_su) executeShellCommandWithSu(tmp,ep,util,tr,ar,tai);
				else executeShellCommand(tmp,ep,util,tr,ar,tai);
			}
		} else if (action_type.equals(PROFILE_ACTION_TYPE_BUILTIN)) {
			util.addLogMsg("I",
				String.format(tmp.teMsgs.msgs_thread_task_exec_builtin, tai.action_builtin_action));
			executeBuiltinAction(tmp,ep,util,tr,ar,tai.action_builtin_action,tai.action_dialog_id,
					tr.active_event_name,tr.active_task_name, next_action);
		} else {
			ar.action_resp=ActionResponse.ACTION_ERROR;
			String tmsg=String.format(tmp.teMsgs.msgs_thread_task_unknoww_action, action_type);
			util.addLogMsg("E",tmsg);
			ar.resp_msg_text=tmsg;
		}
	};
	
	final static private void executeAndroidActivity(TaskManagerParms tmp,
			EnvironmentParms ep, CommonUtilities util, TaskResponse tr,
			ActionResponse ar,String task,String pkg, TaskActionItem eali) {
		util.addLogMsg("I",String.format(tmp.teMsgs.msgs_thread_task_exec_android, ar.current_action, task,pkg));
		final PackageManager pm = tmp.context.getPackageManager();
		Intent in=pm.getLaunchIntentForPackage(pkg);
		ar.action_resp=ActionResponse.ACTION_SUCCESS;
		if (in!=null) {
			in.setAction(Intent.ACTION_MAIN);
			in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			if (eali.action_activity_data_type.equals(PROFILE_ACTION_TYPE_ACTIVITY_DATA_TYPE_URI)){
				in.setData(Uri.parse(eali.action_activity_data_uri));
				if (ep.settingDebugLevel>=1) util.addDebugMsg(1, "I", "   Uri data added : Uri=",eali.action_activity_data_uri);
			} else if (eali.action_activity_data_type.equals(PROFILE_ACTION_TYPE_ACTIVITY_DATA_TYPE_EXTRA)){
				ArrayList<ActivityExtraDataItem>aed_list=eali.action_activity_data_extra_list;
				for (int i=0;i<aed_list.size();i++) {
					ActivityExtraDataItem aedi=aed_list.get(i);
					if (aedi.data_value_array.equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_ARRAY_NO)) {
						String d_val_string="";
						boolean d_val_boolean=false;
						int d_val_int=0;
						if (aedi.data_type.equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_STRING)) {
							d_val_string=aedi.data_value;
							in.putExtra(aedi.key_value, d_val_string);
							if (ep.settingDebugLevel>=1) util.addDebugMsg(1, "I", "   Extra String data added : key=",aedi.key_value,", value=",d_val_string);
						}else if (aedi.data_type.equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_INT)) {
							d_val_int=Integer.valueOf(aedi.data_value);
							in.putExtra(aedi.key_value, d_val_int);						
							if (ep.settingDebugLevel>=1) util.addDebugMsg(1, "I", "   Extra Int data added : key=",aedi.key_value,", value=",String.valueOf(d_val_int));
						}else if (aedi.data_type.equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_BOOLEAN)) {
							if (aedi.data_value.equals("true")) d_val_boolean=true;
							in.putExtra(aedi.key_value, d_val_boolean);						
							if (ep.settingDebugLevel>=1) util.addDebugMsg(1, "I", "   Extra Boolean data added : key=",aedi.key_value,", value=",String.valueOf(d_val_boolean));
						}
					} else if (aedi.data_value_array.equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_ARRAY_YES)) {
						if (aedi.data_type.equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_STRING)) {
							String[] d_val_array=aedi.data_value.split("\u0003");
							String[] d_val_extra=new String[d_val_array.length];
							for (int ai=0;ai<d_val_array.length;ai++) {
								d_val_extra[ai]=d_val_array[ai];
								if (ep.settingDebugLevel>=1) util.addDebugMsg(1, "I", "   Extra array String data added : key=",aedi.key_value,", value=",d_val_extra[ai]);
							}
							in.putExtra(aedi.key_value, d_val_extra);
						}else if (aedi.data_type.equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_INT)) {
							String[] d_val_array=aedi.data_value.split("\u0003");
							int[] d_val_extra=new int[d_val_array.length];
							for (int ai=0;ai<d_val_array.length;ai++) {
								d_val_extra[ai]=Integer.valueOf(d_val_array[ai]);
								if (ep.settingDebugLevel>=1) util.addDebugMsg(1, "I", "   Extra array Int data added : key=",aedi.key_value,", value=",String.valueOf(d_val_extra[ai]));
							}
							in.putExtra(aedi.key_value, d_val_extra);						
						}else if (aedi.data_type.equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_BOOLEAN)) {
							String[] d_val_array=aedi.data_value.split("\u0003");
							boolean[] d_val_extra=new boolean[d_val_array.length];
							for (int ai=0;ai<d_val_array.length;ai++) {
								if (d_val_array[ai].equals("true")) d_val_extra[ai]=true;
								else d_val_extra[ai]=false;
								if (ep.settingDebugLevel>=1) util.addDebugMsg(1, "I", "   Extra array Boolean data added : key=",aedi.key_value,", value=",String.valueOf(d_val_extra[ai]));
							}
							in.putExtra(aedi.key_value, d_val_extra);						
						}
					}
				}
			} else {
				if (ep.settingDebugLevel>=1) util.addDebugMsg(1, "I", "   No data was supplied");
			} 
			tmp.context.startActivity(in);
			tr.active_thread_ctrl.setThreadResultSuccess();
			waitTimeTc(tr,100);
		} else {
			String msg=String.format(tmp.teMsgs.msgs_thread_task_intent_notfound,pkg);
			util.addLogMsg("E",msg);
			ar.action_resp=ActionResponse.ACTION_ERROR;
			ar.resp_msg_text=msg;
		}
	}

	final static private void executeBuiltinAction(TaskManagerParms tmp,
			EnvironmentParms ep, CommonUtilities util, TaskResponse tr,
			ActionResponse ar,String bia, 
			String dlg_id, String en, String tn, String next_action) {
		ar.action_resp=ActionResponse.ACTION_SUCCESS;
		if (bia.startsWith(BUILTIN_ACTION_PRIMITIVE_PREFIX)) {
			executeBuiltinActionPrimitive(tmp,ep,util,tr,ar,bia,dlg_id,en,tn);
		} else if (bia.startsWith(BUILTIN_ACTION_ABORT_PREFIX)) {
			executeBuiltinActionAbort(tmp,ep,util,tr,ar,bia,en,tn);
		} else if (bia.startsWith(BUILTIN_ACTION_SKIP_PREFIX)) {
			executeBuiltinActionSkip(tmp,ep,util,tr,ar,bia,en,tn, next_action);
		} else if (bia.startsWith(BUILTIN_ACTION_CANCEL_PREFIX)) {
			executeBuiltinActionCancel(tmp,ep,util,tr,ar,bia,dlg_id,en,tn);
		} else if (bia.startsWith(BUILTIN_ACTION_BLOCK_PREFIX)) {
			executeBuiltinActionBlockAction(tmp,ep,util,tr,ar,bia,dlg_id,en,tn);
		} else {
			ar.action_resp=ActionResponse.ACTION_ERROR;
			ar.resp_msg_text=String.format(tmp.teMsgs.msgs_thread_task_unknoww_action, bia);
		}
	}

	@SuppressWarnings("deprecation")
	final static private void executeBuiltinActionPrimitive(TaskManagerParms tmp,
			EnvironmentParms ep, CommonUtilities util, TaskResponse tr,
			ActionResponse ar,String bia, 
			String dlg_id, String en, String tn) {
		if (bia.equals(BUILTIN_ACTION_WIFI_ON)) {
			tr.active_thread_ctrl.setThreadMessage(BUILTIN_ACTION_WIFI_ON);
			setWifiOn(tmp,ep,util,tr,ar);
		} else if (bia.equals(BUILTIN_ACTION_WIFI_OFF)) {
			tr.active_thread_ctrl.setThreadMessage(BUILTIN_ACTION_WIFI_OFF);
			setWifiOff(tmp,ep,util,tr,ar);
		} else if (bia.equals(BUILTIN_ACTION_WIFI_DISABLE_CONNECTED_SSID)) {
			tr.active_thread_ctrl.setThreadMessage(BUILTIN_ACTION_WIFI_DISABLE_CONNECTED_SSID);
			setWifiDisableSsid(tmp, ep, util, tr, ar);
		} else if (bia.equals(BUILTIN_ACTION_WIFI_REMOVE_CONNECTED_SSID)) {
			tr.active_thread_ctrl.setThreadMessage(BUILTIN_ACTION_WIFI_REMOVE_CONNECTED_SSID);
			setWifiRemoveSsid(tmp, ep, util, tr, ar);
		} else if (bia.equals(BUILTIN_ACTION_BLUETOOTH_ON)) {
			tr.active_thread_ctrl.setThreadMessage(BUILTIN_ACTION_BLUETOOTH_ON);
			setBluetoothOn(tmp,ep,util,tr,ar);
		} else if (bia.equals(BUILTIN_ACTION_BLUETOOTH_OFF)) {
			tr.active_thread_ctrl.setThreadMessage(BUILTIN_ACTION_BLUETOOTH_OFF);
			setBluetoothOff(tmp,ep,util,tr,ar);
		} else if (bia.equals(BUILTIN_ACTION_WAIT_1_SEC)) {
			waitTimeTc(tr,1*1000);
			if (!tr.active_thread_ctrl.isEnabled()) {
				ar.action_resp=ActionResponse.ACTION_CANCELLED;
				ar.resp_msg_text="Action was cancelled";
			}
		} else if (bia.equals(BUILTIN_ACTION_WAIT_5_SEC)) {
			waitTimeTc(tr,5*1000);
			if (!tr.active_thread_ctrl.isEnabled()) {
				ar.action_resp=ActionResponse.ACTION_CANCELLED;
				ar.resp_msg_text="Action was cancelled";
			}
		} else if (bia.equals(BUILTIN_ACTION_WAIT_1_MIN)) {
			waitTimeTc(tr,1*60*1000);
			if (!tr.active_thread_ctrl.isEnabled()) {
				ar.action_resp=ActionResponse.ACTION_CANCELLED;
				ar.resp_msg_text="Action was cancelled";
			}
		} else if (bia.equals(BUILTIN_ACTION_WAIT_5_MIN)) {
			waitTimeTc(tr,5*60*1000);
			if (!tr.active_thread_ctrl.isEnabled()) {
				ar.action_resp=ActionResponse.ACTION_CANCELLED;
				ar.resp_msg_text="Action was cancelled";
			}
		} else if (bia.equals(BUILTIN_ACTION_SWITCH_TO_HOME)) {
			setScreenSwitchToHome(tmp, ep, util, tr, ar);
		} else if (bia.equals(BUILTIN_ACTION_SCREEN_LOCKED)) {
			setScreenLocked(tmp, util, tr, ar);
		} else if (bia.equals(BUILTIN_ACTION_SCREEN_KEYGUARD_DISABLED)) {
			setKeyguardDisabled(tmp, ep, util, tr, ar);
		} else if (bia.equals(BUILTIN_ACTION_SCREEN_KEYGUARD_ENABLED)) {
			setKeyguardEnabled(tmp, ep, util, tr, ar);
		} else if (bia.equals(BUILTIN_ACTION_SCREEN_ON)) {
//			setKg(tmp);
			if (Build.VERSION.SDK_INT>=17) setScreenOnAsync(tmp,tr,ar);
			else setScreenOnSync(tmp,tr,ar);
		} else if (bia.equals(BUILTIN_ACTION_SCREEN_ON_ASYNC)) {
			setScreenOnAsync(tmp,tr,ar);
		} else if (bia.equals(BUILTIN_ACTION_PLAYBACK_DEFAULT_ALARM)) {
			playBackDefaultAlarm(tmp, ep, util, tr, ar);
		} else if (bia.equals(BUILTIN_ACTION_PLAYBACK_DEFAULT_NOTIFICATION)) {
			playBackDefaultNotification(tmp, ep, util, tr, ar);
		} else if (bia.equals(BUILTIN_ACTION_PLAYBACK_DEFAULT_RINGTONE)) {
			playBackDefaultRingtone(tmp, ep, util, tr, ar);
		} else if (bia.equals(BUILTIN_ACTION_VIBRATE)) {
			vibrateDefaultPattern(tmp.context,ar);
		} else if (bia.equals(BUILTIN_ACTION_RESTART_SCHEDULER)) {
			sendCmdToService(tr, BUILTIN_ACTION_RESTART_SCHEDULER,dlg_id,
					CMD_THREAD_TO_SVC_RESTART_SCHEDULER,BUILTIN_ACTION_RESTART_SCHEDULER);
		} else if (bia.equals(BUILTIN_ACTION_RINGER_NORMAL)) {
			AudioManager am=(AudioManager)tmp.context.getSystemService(Context.AUDIO_SERVICE);
			am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			am.setStreamMute(AudioManager.STREAM_MUSIC, false);
			am.setStreamMute(AudioManager.STREAM_RING, false);
			am.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
	        am.setStreamMute(AudioManager.STREAM_SYSTEM, false);
	        am.setStreamMute(AudioManager.STREAM_ALARM, false);
		} else if (bia.equals(BUILTIN_ACTION_RINGER_SILENT)) {
			AudioManager am=(AudioManager)tmp.context.getSystemService(Context.AUDIO_SERVICE);
			am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			am.setStreamMute(AudioManager.STREAM_MUSIC, true);
			am.setStreamMute(AudioManager.STREAM_RING, true);
			am.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
	        am.setStreamMute(AudioManager.STREAM_SYSTEM, true);
	        am.setStreamMute(AudioManager.STREAM_ALARM, true);
		} else if (bia.equals(BUILTIN_ACTION_RINGER_VIBRATE)) {
			AudioManager am=(AudioManager)tmp.context.getSystemService(Context.AUDIO_SERVICE);
			am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			am.setStreamMute(AudioManager.STREAM_MUSIC, true);
			am.setStreamMute(AudioManager.STREAM_RING, true);
			am.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
	        am.setStreamMute(AudioManager.STREAM_SYSTEM, true);
	        am.setStreamMute(AudioManager.STREAM_ALARM, true);
		} else if (bia.equals(BUILTIN_ACTION_AUTO_SYNC_ENABLED)) {
			setAutoSyncEnabled(tmp, ep, util, tr, ar);
		} else if (bia.equals(BUILTIN_ACTION_AUTO_SYNC_DISABLED)) {
			setAutoSyncDisabled(tmp, ep, util, tr, ar);
		} else if (bia.equals(BUILTIN_ACTION_ABORT)) {
				ar.action_resp=ActionResponse.ACTION_ABORT;
				ar.resp_msg_text="Task was aborted";
		} else {
			ar.action_resp=ActionResponse.ACTION_ERROR;
			ar.resp_msg_text=String.format(tmp.teMsgs.msgs_thread_task_unknoww_action, bia);
		}
	}

//	@SuppressLint("NewApi")
//	final static private void setKg(TaskManagerParms tmp) {
//        DevicePolicyManager dpm = 
//        		(DevicePolicyManager)tmp.context.getSystemService(Context.DEVICE_POLICY_SERVICE);
//        ComponentName darcn = new ComponentName(tmp.context, DevAdmReceiver.class);
//        if (dpm.isAdminActive(darcn)) {
//        	dpm.setKeyguardDisabledFeatures(darcn, DevicePolicyManager.KEYGUARD_DISABLE_FEATURES_NONE);//.KEYGUARD_DISABLE_TRUST_AGENTS);
//        	Log.v("","kg set");
//        }
//	}

	final static private void executeBuiltinActionAbort(TaskManagerParms tmp,
	    		EnvironmentParms ep, CommonUtilities util, TaskResponse tr,
				ActionResponse ar,String bia, String en, String tn) {
			if (bia.equals(BUILTIN_ACTION_ABORT_IF_WIFI_ON)) {
				if (ep.wifiIsActive) ar.action_resp=ActionResponse.ACTION_ABORT;
			} else if (bia.equals(BUILTIN_ACTION_ABORT_IF_WIFI_CONNECTED)) {
				if (ep.isWifiConnected()) ar.action_resp=ActionResponse.ACTION_ABORT;
			} else if (bia.equals(BUILTIN_ACTION_ABORT_IF_WIFI_DISCONNECTED)) {
				if (!ep.isWifiConnected()) ar.action_resp=ActionResponse.ACTION_ABORT;
			} else if (bia.equals(BUILTIN_ACTION_ABORT_IF_WIFI_OFF)) {
				if (!ep.wifiIsActive) ar.action_resp=ActionResponse.ACTION_ABORT;
			} else if (bia.equals(BUILTIN_ACTION_ABORT_IF_BLUETOOTH_ON)) {
				if (ep.bluetoothIsActive) ar.action_resp=ActionResponse.ACTION_ABORT;
			} else if (bia.equals(BUILTIN_ACTION_ABORT_IF_BLUETOOTH_CONNECTED)) {
				if (ep.isBluetoothConnected()) ar.action_resp=ActionResponse.ACTION_ABORT;
			} else if (bia.equals(BUILTIN_ACTION_ABORT_IF_BLUETOOTH_DISCONNECTED)) {
				if (!ep.isBluetoothConnected()) ar.action_resp=ActionResponse.ACTION_ABORT;
			} else if (bia.equals(BUILTIN_ACTION_ABORT_IF_BLUETOOTH_OFF)) {
				if (!ep.bluetoothIsActive) ar.action_resp=ActionResponse.ACTION_ABORT;
			} else if (bia.equals(BUILTIN_ACTION_ABORT_IF_SCREEN_UNLOCKED)) {
				if (!ep.screenIsLocked) ar.action_resp=ActionResponse.ACTION_ABORT;
			} else if (bia.equals(BUILTIN_ACTION_ABORT_IF_SCREEN_LOCKED)) {
				if (ep.screenIsLocked) ar.action_resp=ActionResponse.ACTION_ABORT;
			} else if (bia.equals(BUILTIN_ACTION_ABORT_IF_SCREEN_ON)) {
				if (ep.screenIsOn) ar.action_resp=ActionResponse.ACTION_ABORT;
			} else if (bia.equals(BUILTIN_ACTION_ABORT_IF_SCREEN_OFF)) {
				if (!ep.screenIsOn) ar.action_resp=ActionResponse.ACTION_ABORT;
			} else if (bia.equals(BUILTIN_ACTION_ABORT_IF_TRUSTED)) {
				if (!isTrusted(tmp,ep,util,tr,ar)) ar.action_resp=ActionResponse.ACTION_ABORT;
			} else if (bia.equals(BUILTIN_ACTION_ABORT_IF_NOT_TRUSTED)) {
				if (!ep.screenIsOn) ar.action_resp=ActionResponse.ACTION_ABORT;
			} else if (bia.equals(BUILTIN_ACTION_ABORT_IF_PROXIMITY_DETECTED)) {
				if (ep.proximitySensorAvailable) {
					if (ep.proximitySensorValue==0) ar.action_resp=ActionResponse.ACTION_ABORT;
				} else {
					ar.action_resp=ActionResponse.ACTION_ERROR;
					ar.resp_msg_text=tmp.teMsgs.msgs_thread_task_exec_proximity_not_available;
					return ;
				}
			} else if (bia.equals(BUILTIN_ACTION_ABORT_IF_PROXIMITY_UNDETECTED)) {
				if (ep.proximitySensorAvailable) {
					if (ep.proximitySensorValue==1) ar.action_resp=ActionResponse.ACTION_ABORT;
				} else {
					ar.action_resp=ActionResponse.ACTION_ERROR;
					ar.resp_msg_text=tmp.teMsgs.msgs_thread_task_exec_proximity_not_available;
					return ;
				}
			} else if (bia.equals(BUILTIN_ACTION_ABORT_IF_LIGHT_DETECTED)) {
				if (ep.lightSensorAvailable) {
					if (ep.lightSensorValue==1) ar.action_resp=ActionResponse.ACTION_ABORT;
				} else {
					ar.action_resp=ActionResponse.ACTION_ERROR;
					ar.resp_msg_text=tmp.teMsgs.msgs_thread_task_exec_light_not_available;
					return ;
				}
			} else if (bia.equals(BUILTIN_ACTION_ABORT_IF_LIGHT_UNDETECTED)) {
				if (ep.lightSensorAvailable) {
					if (ep.lightSensorValue==0) ar.action_resp=ActionResponse.ACTION_ABORT;
				} else {
					ar.action_resp=ActionResponse.ACTION_ERROR;
					ar.resp_msg_text=tmp.teMsgs.msgs_thread_task_exec_light_not_available;
					return ;
				}
			} else if (bia.equals(BUILTIN_ACTION_ABORT_IF_POWER_IS_AC_OR_CHRAGE)) {
				if (ep.batteryPowerSource.equals(CURRENT_POWER_SOURCE_AC)) ar.action_resp=ActionResponse.ACTION_ABORT;
			} else if (bia.equals(BUILTIN_ACTION_ABORT_IF_POWER_IS_BATTERY)) {
				if (ep.batteryPowerSource.equals(CURRENT_POWER_SOURCE_BATTERY)) ar.action_resp=ActionResponse.ACTION_ABORT;
			} else if (bia.equals(BUILTIN_ACTION_ABORT_IF_CALL_STATE_IDLE)) {
				if (ep.telephonyStatus==TelephonyManager.CALL_STATE_IDLE) ar.action_resp=ActionResponse.ACTION_ABORT;
			} else if (bia.equals(BUILTIN_ACTION_ABORT_IF_CALL_STATE_OFF_HOOK)) {
				if (ep.telephonyStatus==TelephonyManager.CALL_STATE_OFFHOOK) ar.action_resp=ActionResponse.ACTION_ABORT;
			} else if (bia.equals(BUILTIN_ACTION_ABORT_IF_CALL_STATE_RINGING)) {
				if (ep.telephonyStatus==TelephonyManager.CALL_STATE_RINGING) ar.action_resp=ActionResponse.ACTION_ABORT;
			} else if (bia.equals(BUILTIN_ACTION_ABORT_IF_AIRPLANE_MODE_ON)) {
				if (ep.airplane_mode_on==1) ar.action_resp=ActionResponse.ACTION_ABORT;
			} else if (bia.equals(BUILTIN_ACTION_ABORT_IF_AIRPLANE_MODE_OFF)) {
				if (ep.airplane_mode_on==0) ar.action_resp=ActionResponse.ACTION_ABORT;
			} else if (bia.equals(BUILTIN_ACTION_ABORT_IF_MOBILE_NETWORK_CONNECTED)) {
				if (ep.mobileNetworkIsConnected) ar.action_resp=ActionResponse.ACTION_ABORT;
			} else if (bia.equals(BUILTIN_ACTION_ABORT_IF_MOBILE_NETWORK_DISCONNECTED)) {
				if (!ep.mobileNetworkIsConnected) ar.action_resp=ActionResponse.ACTION_ABORT;
	//		} else if (bia.equals(BUILTIN_ACTION_ABORT_IF_NETWORK_CONNECTED)) {
	//			if (ep.networkIsConnected) ar.action_resp=ActionResponse.ACTION_ABORT;
	//		} else if (bia.equals(BUILTIN_ACTION_ABORT_IF_NETWORK_DISCONNECTED)) {
	//			if (!ep.networkIsConnected) ar.action_resp=ActionResponse.ACTION_ABORT;
			} else if (bia.equals(BUILTIN_ACTION_ABORT_IF_ORIENTATION_LANDSCAPE)) {
				if (ep.isOrientationLanscape()) ar.action_resp=ActionResponse.ACTION_ABORT;
			} else if (bia.equals(BUILTIN_ACTION_ABORT_IF_ORIENTATION_PORTRAIT)) {
				if (!ep.isOrientationLanscape()) ar.action_resp=ActionResponse.ACTION_ABORT;
			} else {
				ar.action_resp=ActionResponse.ACTION_ERROR;
				ar.resp_msg_text=String.format(tmp.teMsgs.msgs_thread_task_unknoww_action, bia);
			}
			if (ar.action_resp==ActionResponse.ACTION_ABORT) {
				ar.resp_msg_text=String.format(tmp.teMsgs.msgs_thread_task_exec_builtin_abort,bia);
			}
	};

	final static private void executeBuiltinActionSkip(TaskManagerParms tmp,
    		EnvironmentParms ep, CommonUtilities util, TaskResponse tr,
			ActionResponse ar,String bia, String en, String tn, String next_action) {
		if (bia.equals(BUILTIN_ACTION_SKIP_IF_WIFI_ON)) {
			if (ep.wifiIsActive) ar.action_resp=ActionResponse.ACTION_SKIP;
		} else if (bia.equals(BUILTIN_ACTION_SKIP_IF_WIFI_CONNECTED)) {
			if (ep.isWifiConnected()) ar.action_resp=ActionResponse.ACTION_SKIP;
		} else if (bia.equals(BUILTIN_ACTION_SKIP_IF_WIFI_DISCONNECTED)) {
			if (!ep.isWifiConnected()) ar.action_resp=ActionResponse.ACTION_SKIP;
		} else if (bia.equals(BUILTIN_ACTION_SKIP_IF_WIFI_OFF)) {
			if (!ep.wifiIsActive) ar.action_resp=ActionResponse.ACTION_SKIP;
		} else if (bia.equals(BUILTIN_ACTION_SKIP_IF_BLUETOOTH_ON)) {
			if (ep.bluetoothIsActive) ar.action_resp=ActionResponse.ACTION_SKIP;
		} else if (bia.equals(BUILTIN_ACTION_SKIP_IF_BLUETOOTH_CONNECTED)) {
			if (ep.isBluetoothConnected()) ar.action_resp=ActionResponse.ACTION_SKIP;
		} else if (bia.equals(BUILTIN_ACTION_SKIP_IF_BLUETOOTH_DISCONNECTED)) {
			if (!ep.isBluetoothConnected()) ar.action_resp=ActionResponse.ACTION_SKIP;
		} else if (bia.equals(BUILTIN_ACTION_SKIP_IF_BLUETOOTH_OFF)) {
			if (!ep.bluetoothIsActive) ar.action_resp=ActionResponse.ACTION_SKIP;
		} else if (bia.equals(BUILTIN_ACTION_SKIP_IF_SCREEN_LOCKED)) {
			if (ep.screenIsLocked) ar.action_resp=ActionResponse.ACTION_SKIP;
		} else if (bia.equals(BUILTIN_ACTION_SKIP_IF_SCREEN_UNLOCKED)) {
			if (!ep.screenIsLocked) ar.action_resp=ActionResponse.ACTION_SKIP;
		} else if (bia.equals(BUILTIN_ACTION_SKIP_IF_SCREEN_ON)) {
			if (ep.screenIsOn) ar.action_resp=ActionResponse.ACTION_SKIP;
		} else if (bia.equals(BUILTIN_ACTION_SKIP_IF_SCREEN_OFF)) {
			if (!ep.screenIsOn) ar.action_resp=ActionResponse.ACTION_SKIP;
		} else if (bia.equals(BUILTIN_ACTION_SKIP_IF_TRUSTED)) {
			if (isTrusted(tmp,ep,util,tr,ar)) ar.action_resp=ActionResponse.ACTION_SKIP;
		} else if (bia.equals(BUILTIN_ACTION_SKIP_IF_NOT_TRUSTED)) {
			if (!isTrusted(tmp,ep,util,tr,ar)) ar.action_resp=ActionResponse.ACTION_SKIP;
		} else if (bia.equals(BUILTIN_ACTION_SKIP_IF_PROXIMITY_DETECTED)) {
			if (ep.proximitySensorAvailable) {
				if (ep.proximitySensorValue==0) ar.action_resp=ActionResponse.ACTION_SKIP;
			} else {
				ar.action_resp=ActionResponse.ACTION_ERROR;
				ar.resp_msg_text=tmp.teMsgs.msgs_thread_task_exec_proximity_not_available;
				return ;
			}
		} else if (bia.equals(BUILTIN_ACTION_SKIP_IF_PROXIMITY_UNDETECTED)) {
			if (ep.proximitySensorAvailable) {
				if (ep.proximitySensorValue==1) ar.action_resp=ActionResponse.ACTION_SKIP;
			} else {
				ar.action_resp=ActionResponse.ACTION_ERROR;
				ar.resp_msg_text=tmp.teMsgs.msgs_thread_task_exec_proximity_not_available;
				return ;
			}
		} else if (bia.equals(BUILTIN_ACTION_SKIP_IF_LIGHT_DETECTED)) {
			if (ep.lightSensorAvailable) {
				if (ep.lightSensorValue==1) ar.action_resp=ActionResponse.ACTION_SKIP;
			} else {
				ar.action_resp=ActionResponse.ACTION_ERROR;
				ar.resp_msg_text=tmp.teMsgs.msgs_thread_task_exec_light_not_available;
				return ;
			}
		} else if (bia.equals(BUILTIN_ACTION_SKIP_IF_LIGHT_UNDETECTED)) {
			if (ep.lightSensorAvailable) {
				if (ep.lightSensorValue==0) ar.action_resp=ActionResponse.ACTION_SKIP;
			} else {
				ar.action_resp=ActionResponse.ACTION_ERROR;
				ar.resp_msg_text=tmp.teMsgs.msgs_thread_task_exec_light_not_available;
				return ;
			}
		} else if (bia.equals(BUILTIN_ACTION_SKIP_IF_POWER_IS_AC_OR_CHRAGE)) {
			if (ep.batteryPowerSource.equals(CURRENT_POWER_SOURCE_AC)) ar.action_resp=ActionResponse.ACTION_SKIP;
		} else if (bia.equals(BUILTIN_ACTION_SKIP_IF_POWER_IS_BATTERY)) {
			if (ep.batteryPowerSource.equals(CURRENT_POWER_SOURCE_BATTERY)) ar.action_resp=ActionResponse.ACTION_SKIP;
		} else if (bia.equals(BUILTIN_ACTION_SKIP_IF_CALL_STATE_IDLE)) {
			if (ep.telephonyStatus==TelephonyManager.CALL_STATE_IDLE) ar.action_resp=ActionResponse.ACTION_SKIP;
		} else if (bia.equals(BUILTIN_ACTION_SKIP_IF_CALL_STATE_OFF_HOOK)) {
			if (ep.telephonyStatus==TelephonyManager.CALL_STATE_OFFHOOK) ar.action_resp=ActionResponse.ACTION_SKIP;
		} else if (bia.equals(BUILTIN_ACTION_SKIP_IF_CALL_STATE_RINGING)) {
			if (ep.telephonyStatus==TelephonyManager.CALL_STATE_RINGING) ar.action_resp=ActionResponse.ACTION_SKIP;
		} else if (bia.equals(BUILTIN_ACTION_SKIP_IF_AIRPLANE_MODE_ON)) {
			if (ep.airplane_mode_on==1) ar.action_resp=ActionResponse.ACTION_SKIP;
		} else if (bia.equals(BUILTIN_ACTION_SKIP_IF_AIRPLANE_MODE_OFF)) {
			if (ep.airplane_mode_on==0) ar.action_resp=ActionResponse.ACTION_SKIP;
		} else if (bia.equals(BUILTIN_ACTION_SKIP_IF_MOBILE_NETWORK_CONNECTED)) {
			if (ep.mobileNetworkIsConnected) ar.action_resp=ActionResponse.ACTION_SKIP;
		} else if (bia.equals(BUILTIN_ACTION_SKIP_IF_MOBILE_NETWORK_DISCONNECTED)) {
			if (!ep.mobileNetworkIsConnected) ar.action_resp=ActionResponse.ACTION_SKIP;
//		} else if (bia.equals(BUILTIN_ACTION_SKIP_IF_NETWORK_CONNECTED)) {
//			if (ep.networkIsConnected) ar.action_resp=ActionResponse.ACTION_SKIP;
//		} else if (bia.equals(BUILTIN_ACTION_SKIP_IF_NETWORK_DISCONNECTED)) {
//			if (!ep.networkIsConnected) ar.action_resp=ActionResponse.ACTION_SKIP;
		} else if (bia.equals(BUILTIN_ACTION_SKIP_IF_ORIENTATION_LANDSCAPE)) {
			if (ep.isOrientationLanscape()) ar.action_resp=ActionResponse.ACTION_SKIP;
		} else if (bia.equals(BUILTIN_ACTION_SKIP_IF_ORIENTATION_PORTRAIT)) {
			if (!ep.isOrientationLanscape()) ar.action_resp=ActionResponse.ACTION_SKIP;
		} else {
			ar.action_resp=ActionResponse.ACTION_ERROR;
			ar.resp_msg_text=String.format(tmp.teMsgs.msgs_thread_task_unknoww_action, bia);
		}
		if (ar.action_resp==ActionResponse.ACTION_SKIP) {
			ar.resp_msg_text=String.format(tmp.teMsgs.msgs_thread_task_exec_compare_skip,next_action);		
		}
	};
	
	final static private void executeBuiltinActionCancel(TaskManagerParms tmp,
	    		EnvironmentParms ep, CommonUtilities util, TaskResponse tr,
				ActionResponse ar,String bia, 
				String dlg_id, String en, String tn) {
			try {
				TaskManager.acqLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE,ep,tmp, util);
				if (bia.equals(BUILTIN_ACTION_CANCEL_EVENT_BOOT_COMPLETED)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_BOOT_COMPLETED;
					TaskManager.cancelTaskByEventId(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_CANCEL_EVENT_WIFI_ON)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_WIFI_ON;
					TaskManager.cancelTaskByEventId(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_CANCEL_EVENT_WIFI_CONNECTED)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_WIFI_CONNECTED;
					TaskManager.cancelTaskByEventId(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_CANCEL_EVENT_WIFI_DISCONNECTED)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_WIFI_DISCONNECTED;
					TaskManager.cancelTaskByEventId(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_CANCEL_EVENT_WIFI_OFF)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_WIFI_OFF;
					TaskManager.cancelTaskByEventId(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_CANCEL_EVENT_BLUETOOTH_ON)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_BLUETOOTH_ON;
					TaskManager.cancelTaskByEventId(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_CANCEL_EVENT_BLUETOOTH_CONNECTED)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_BLUETOOTH_CONNECTED;
					TaskManager.cancelTaskByEventId(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_CANCEL_EVENT_BLUETOOTH_DISCONNECTED)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_BLUETOOTH_DISCONNECTED;
					TaskManager.cancelTaskByEventId(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_CANCEL_EVENT_BLUETOOTH_OFF)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_BLUETOOTH_OFF;
					TaskManager.cancelTaskByEventId(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_CANCEL_EVENT_PROXIMITY_DETECTED)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_PROXIMITY_DETECTED;
					TaskManager.cancelTaskByEventId(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_CANCEL_EVENT_PROXIMITY_UNDETECTED)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_PROXIMITY_UNDETECTED;
					TaskManager.cancelTaskByEventId(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_CANCEL_EVENT_LIGHT_DETECTED)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_LIGHT_DETECTED;
					TaskManager.cancelTaskByEventId(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_CANCEL_EVENT_LIGHT_UNDETECTED)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_LIGHT_UNDETECTED;
					TaskManager.cancelTaskByEventId(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_CANCEL_EVENT_SCREEN_LOCKED)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_SCREEN_LOCKED;
					TaskManager.cancelTaskByEventId(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_CANCEL_EVENT_SCREEN_UNLOCKED)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_SCREEN_UNLOCKED;
					TaskManager.cancelTaskByEventId(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_CANCEL_EVENT_POWER_SOURCE_CHANGED_AC)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_POWER_SOURCE_CHANGED_AC;
					TaskManager.cancelTaskByEventId(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_CANCEL_EVENT_POWER_SOURCE_CHANGED_BATTERY)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_POWER_SOURCE_CHANGED_BATTERY;
					TaskManager.cancelTaskByEventId(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_CANCEL_EVENT_PHONE_CALL_STATE_IDLE)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_PHONE_CALL_STATE_IDLE;
					TaskManager.cancelTaskByEventId(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_CANCEL_EVENT_PHONE_CALL_STATE_OFF_HOOK)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_PHONE_CALL_STATE_OFF_HOOK;
					TaskManager.cancelTaskByEventId(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_CANCEL_EVENT_PHONE_CALL_STATE_RINGING)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_PHONE_CALL_STATE_RINGING;
					TaskManager.cancelTaskByEventId(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_CANCEL_EVENT_AIRPLANE_MODE_ON)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_AIRPLANE_MODE_ON;
					TaskManager.cancelTaskByEventId(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_CANCEL_EVENT_AIRPLANE_MODE_OFF)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_AIRPLANE_MODE_OFF;
					TaskManager.cancelTaskByEventId(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_CANCEL_EVENT_MOBILE_NETWORK_CONNECTED)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_MOBILE_NETWORK_CONNECTED;
					TaskManager.cancelTaskByEventId(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_CANCEL_EVENT_MOBILE_NETWORK_DISCONNECTED)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_MOBILE_NETWORK_DISCONNECTED;
					TaskManager.cancelTaskByEventId(tmp, ep, util, tr);
	//			} else if (bia.equals(BUILTIN_ACTION_CANCEL_EVENT_NETWORK_CONNECTED)) {
	//				tr.cmd_tgt_event_name=BUILTIN_EVENT_NETWORK_CONNECTED;
	//				TaskManager.cancelTaskByEventId(tmp, ep, util, tr);
	//			} else if (bia.equals(BUILTIN_ACTION_CANCEL_EVENT_NETWORK_DISCONNECTED)) {
	//				tr.cmd_tgt_event_name=BUILTIN_EVENT_NETWORK_DISCONNECTED;
	//				TaskManager.cancelTaskByEventId(tmp, ep, util, tr);
				} else {
					ar.action_resp=ActionResponse.ACTION_ERROR;
					ar.resp_msg_text=String.format(tmp.teMsgs.msgs_thread_task_unknoww_action, bia);
				}
			} finally {
				TaskManager.relLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE,ep,tmp, util);
			}
	};

	final static private void executeBuiltinActionBlockAction(TaskManagerParms tmp,
	    		EnvironmentParms ep, CommonUtilities util, TaskResponse tr,
				ActionResponse ar,String bia, 
				String dlg_id, String en, String tn) {
			try {
				TaskManager.acqLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE,ep,tmp, util);
				if (bia.equals(BUILTIN_ACTION_BLOCK_EVENT_CLEAR)) {
					TaskManager.clearBlockActionList(tmp, ep, util);
				} else if (bia.equals(BUILTIN_ACTION_BLOCK_EVENT_BLOCK_ALL)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_ALL;
					TaskManager.addBlockActionListItem(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_BLOCK_EVENT_BOOT_COMPLETED)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_BOOT_COMPLETED;
					TaskManager.addBlockActionListItem(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_BLOCK_EVENT_WIFI_ON)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_WIFI_ON;
					TaskManager.addBlockActionListItem(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_BLOCK_EVENT_WIFI_CONNECTED)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_WIFI_CONNECTED;
					TaskManager.addBlockActionListItem(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_BLOCK_EVENT_WIFI_DISCONNECT)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_WIFI_DISCONNECTED;
					TaskManager.addBlockActionListItem(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_BLOCK_EVENT_WIFI_OFF)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_WIFI_OFF;
					TaskManager.addBlockActionListItem(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_BLOCK_EVENT_BLUETOOTH_ON)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_BLUETOOTH_ON;
					TaskManager.addBlockActionListItem(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_BLOCK_EVENT_BLUETOOTH_CONNECTED)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_BLUETOOTH_CONNECTED;
					TaskManager.addBlockActionListItem(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_BLOCK_EVENT_BLUETOOTH_DISCONNECTED)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_BLUETOOTH_DISCONNECTED;
					TaskManager.addBlockActionListItem(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_BLOCK_EVENT_BLUETOOTH_OFF)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_BLUETOOTH_OFF;
					TaskManager.addBlockActionListItem(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_BLOCK_EVENT_PROXIMITY_DETECTED)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_PROXIMITY_DETECTED;
					TaskManager.addBlockActionListItem(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_BLOCK_EVENT_PROXIMITY_UNDETECTED)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_PROXIMITY_UNDETECTED;
					TaskManager.addBlockActionListItem(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_BLOCK_EVENT_LIGHT_DETECTED)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_LIGHT_DETECTED;
					TaskManager.addBlockActionListItem(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_BLOCK_EVENT_LIGHT_UNDETECTED)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_LIGHT_UNDETECTED;
					TaskManager.addBlockActionListItem(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_BLOCK_EVENT_PHONE_CALL_STATE_IDLE)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_PHONE_CALL_STATE_IDLE;
					TaskManager.addBlockActionListItem(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_BLOCK_EVENT_PHONE_CALL_STATE_OFF_HOOK)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_PHONE_CALL_STATE_OFF_HOOK;
					TaskManager.addBlockActionListItem(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_BLOCK_EVENT_PHONE_CALL_STATE_RINGING)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_PHONE_CALL_STATE_RINGING;
					TaskManager.addBlockActionListItem(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_BLOCK_EVENT_AIRPLANE_MODE_ON)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_AIRPLANE_MODE_ON;
					TaskManager.addBlockActionListItem(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_BLOCK_EVENT_AIRPLANE_MODE_OFF)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_AIRPLANE_MODE_OFF;
					TaskManager.addBlockActionListItem(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_BLOCK_EVENT_MOBILE_NETWORK_CONNECTED)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_MOBILE_NETWORK_CONNECTED;
					TaskManager.addBlockActionListItem(tmp, ep, util, tr);
				} else if (bia.equals(BUILTIN_ACTION_BLOCK_EVENT_MOBILE_NETWORK_DISCONNECTED)) {
					tr.cmd_tgt_event_name=BUILTIN_EVENT_MOBILE_NETWORK_DISCONNECTED;
					TaskManager.addBlockActionListItem(tmp, ep, util, tr);
	//			} else if (bia.equals(BUILTIN_ACTION_BLOCK_EVENT_NETWORK_CONNECTED)) {
	//				tr.cmd_tgt_event_name=BUILTIN_EVENT_NETWORK_CONNECTED;
	//				TaskManager.addBlockActionItem(tmp, ep, util, tr);
	//			} else if (bia.equals(BUILTIN_ACTION_BLOCK_EVENT_NETWORK_DISCONNECTED)) {
	//				tr.cmd_tgt_event_name=BUILTIN_EVENT_NETWORK_DISCONNECTED;
	//				TaskManager.addBlockActionItem(tmp, ep, util, tr);
				} else {
					ar.action_resp=ActionResponse.ACTION_ERROR;
					ar.resp_msg_text=String.format(tmp.teMsgs.msgs_thread_task_unknoww_action, bia);
				}
			} finally {
				TaskManager.relLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE,ep,tmp, util);
			}
	};
	
	final static private void executeBeanShellScriptAction(final TaskManagerParms tmp,
			final EnvironmentParms ep, final CommonUtilities util, final TaskResponse tr,
			final ActionResponse ar, final TaskActionItem tai) {
//		Log.v("","bsh start");
		BshExecEnvListItem bsli=TaskManager.acqBshExecEnvItem(tmp,ep,util,tr,ar,tai);
//		Log.v("","bsh prepare");
		tr.active_thread_ctrl.setExtraDataObject(new Object[]{Thread.currentThread(),bsli.interpreter});
		String script_text="cd(\""+LocalMountPoint.getExternalStorageDir()+"\");\n"+
				tai.action_bsh_script;
		try {
//			bsli.bshInterpreter.set("TaCmd", bsli.bshInstance);
//			Log.v("","bsh execute");
			bsli.interpreter.eval(script_text);
			ar.action_resp=ActionResponse.ACTION_SUCCESS;
			ar.resp_msg_text="";
//			Log.v("","bsh end");
		} catch (ParseException e) {
			ar.action_resp=ActionResponse.ACTION_ERROR;
			ar.resp_msg_text="BeanShell Parser error :\n"+e.getMessage()+"\n"+
					"Script text:\n"+
					"123456789012345678901234567890123456789012345678901234567890\n"+
					script_text;
		} catch (EvalError e) {
			if (e.getErrorText()!=null&&e.getErrorText().startsWith("TaCmd .abort ( )")) {
				ar.action_resp=ActionResponse.ACTION_ABORT;
				ar.resp_msg_text="BeanShell Task was aborted";
			} else {
				if (tr.active_thread_ctrl.isEnabled()) {
					ar.action_resp=ActionResponse.ACTION_ERROR;
					ar.resp_msg_text="BeanShell Script error :\n"+e.getMessage()+"\n"+
							"Script text:\n"+
							"123456789012345678901234567890123456789012345678901234567890\n"+
							script_text;
				} else {
					ar.action_resp=ActionResponse.ACTION_CANCELLED;
					ar.resp_msg_text="BeanShell Task was cancelled";
				}
			}
		}
//		try {
//			bsli.bshInterpreter.unset("TaCmd");
////			Log.v("","clear start");
//			bsli.bshInterpreter.eval("clear();");
////			Log.v("","clear ended");
//		} catch (EvalError e) {
//			e.printStackTrace();
//		}
		TaskManager.relBshExecEnvItem(tmp,ep,util,bsli);
	};
	
	public static void executeShellCommandWithSu(final TaskManagerParms tmp,
			final EnvironmentParms ep, final CommonUtilities util, final TaskResponse tr,
			final ActionResponse ar, final TaskActionItem tai) {
		util.addLogMsg("I","Shell command issued"+ " su "+tai.action_shell_cmd);
		try {
			ar.action_resp=ActionResponse.ACTION_SUCCESS;
			ar.resp_msg_text=ShellCommandUtil.executeShellCommandWithSu(tai.action_shell_cmd);
		} catch (IOException e) {
			ar.action_resp=ActionResponse.ACTION_ERROR;
			ar.resp_msg_text=e.getMessage();
			e.printStackTrace();
		} catch (InterruptedException e) {
			ar.action_resp=ActionResponse.ACTION_ERROR;
			ar.resp_msg_text=e.getMessage();
			e.printStackTrace();
		}
	};

	public static void executeShellCommand(final TaskManagerParms tmp,
			final EnvironmentParms ep, final CommonUtilities util, final TaskResponse tr,
			final ActionResponse ar, final TaskActionItem tai) {
		util.addLogMsg("I","Shell command issued"+ " "+tai.action_shell_cmd);
		try {
			ar.action_resp=ActionResponse.ACTION_SUCCESS;
			ar.resp_msg_text=ShellCommandUtil.executeShellCommand(tai.action_shell_cmd);
		} catch (IOException e) {
			ar.action_resp=ActionResponse.ACTION_ERROR;
			ar.resp_msg_text=e.getMessage();
			e.printStackTrace();
		} catch (InterruptedException e) {
			ar.action_resp=ActionResponse.ACTION_ERROR;
			ar.resp_msg_text=e.getMessage();
			e.printStackTrace();
		}
	};

	final static private void executeMessageAction(TaskManagerParms tmp,
			EnvironmentParms ep, CommonUtilities util, TaskResponse tr,
			ActionResponse ar,String actionnm, String dlg_id,
			String msg_type, String msg_text, boolean use_vib, boolean use_led, String led_color) {
		util.addLogMsg("I",
				String.format(tmp.teMsgs.msgs_thread_task_exec_message,
						actionnm,msg_type,use_vib,use_led, led_color));
		showMessage(tmp, ep, util, tr, ar, actionnm, dlg_id,
			msg_type, msg_text, use_vib, use_led, led_color,false);
	}

	final static private void executePlayBackMusic(TaskManagerParms tmp,
			EnvironmentParms ep, CommonUtilities util, TaskResponse tr,
			ActionResponse ar,String action, String dlg_id, 
			String fp, String vol_left, String vol_right) {
		util.addLogMsg("I",String.format(tmp.teMsgs.msgs_thread_task_exec_play_sound,ar.current_action, fp));
		playBackMusic(tmp, ep, util, tr, ar, action, dlg_id, 
				fp, vol_left, vol_right);
	}

	final static private void executePlayBackRingtone(TaskManagerParms tmp,
			EnvironmentParms ep, CommonUtilities util, TaskResponse tr,
			ActionResponse ar,String action, String dlg_id,
			String rt, String rn, String rp,
			String vol_left, String vol_right) {
		util.addLogMsg("I",String.format(tmp.teMsgs.msgs_thread_task_exec_play_ringtone,ar.current_action, rt,rn));
		tr.active_action_name=action;
		playBackRingtone(tmp, ep, util, tr, ar, dlg_id,
				rt, rn, rp, vol_left, vol_right);
	}

	final static private void executeTaskAction(TaskManagerParms tmp,
			EnvironmentParms ep, CommonUtilities util,TaskResponse tr, ActionResponse ar,
			String action_nm, String dlg_id,
			String task_type, String task_target) {
		util.addLogMsg("I",
				String.format(tmp.teMsgs.msgs_thread_task_exec_task, action_nm,task_type,task_target));
		taskTriggerTaskControl(tmp, ep, util, tr, ar, action_nm, dlg_id, task_type, task_target);
	}

	final static private void executeTimeAction(TaskManagerParms tmp,
    		EnvironmentParms ep, CommonUtilities util,TaskResponse tr, 
    		ActionResponse ar,
			String action_name, String dlg_id,
			String time_type, String time_target) {
		util.addLogMsg("I",
				String.format(tmp.teMsgs.msgs_thread_task_exec_time, action_name,time_type,time_target));
		resetIntervalTimer(tmp, ep, util, tr, ar, action_name, dlg_id,
				time_type, time_target);
	};

	final static private void executeWaitAction(TaskManagerParms tmp,
			EnvironmentParms ep, CommonUtilities util,TaskResponse tr, ActionResponse ar,
			String action_nm, String dlg_id,
			String wait_target, String wait_timeout_value, String wait_timeout_units) {
		util.addLogMsg("I",
				String.format(tmp.teMsgs.msgs_thread_task_exec_wait, action_nm,wait_target, wait_timeout_value, wait_timeout_units));
		tr.active_action_name=action_nm;
		tr.active_dialog_id=dlg_id;
		tr.resp_time=StringUtil.convDateTimeTo_HourMinSecMili(System.currentTimeMillis());
		ar.action_resp=ActionResponse.ACTION_SUCCESS;
		ar.resp_msg_text="";
		int timeout_val=0;
		long timeout_millis=0;
		if (!wait_timeout_value.equals("")) timeout_val=Integer.valueOf(wait_timeout_value);
		if (wait_timeout_units.equals(PROFILE_ACTION_TYPE_WAIT_TIMEOUT_UNITS_MIN)) 
			timeout_millis=timeout_val*60*1000;
		else timeout_millis=timeout_val*1000;
	
		if (wait_target.equals(PROFILE_ACTION_TYPE_WAIT_TARGET_WIFI_CONNECTED)) {
			if (ep.wifiIsActive) {
				if (ep.wifiConnectedSsidName.equals("")) {
					TaskManager.addNotifyEventListItem(
							tmp.wifiNotifyEventList,tr.active_thread_ctrl);
					waitDeviceEvent(tr, ar, timeout_millis);
					TaskManager.removeNotifyEventListItem(
							tmp.wifiNotifyEventList,tr.active_thread_ctrl);
				} else {
					ar.action_resp=ActionResponse.ACTION_WARNING;
					ar.resp_msg_text="Wifi is already connected";
				}
			} else {
				ar.action_resp=ActionResponse.ACTION_WARNING;
				ar.resp_msg_text="Wifi is inactive(Off)";
			}
		} else if (wait_target.equals(PROFILE_ACTION_TYPE_WAIT_TARGET_BLUETOOTH_CONNECTED)) {
			if (ep.bluetoothIsActive) {
				if (!ep.isBluetoothConnected()) {
					TaskManager.addNotifyEventListItem(
							tmp.bluetoothNotifyEventList,tr.active_thread_ctrl);
					waitDeviceEvent(tr, ar, timeout_millis);
					TaskManager.removeNotifyEventListItem(
							tmp.bluetoothNotifyEventList,tr.active_thread_ctrl);
				} else {
					ar.action_resp=ActionResponse.ACTION_WARNING;
					ar.resp_msg_text="Bluetooth is already connected";
				}
			} else {
				ar.action_resp=ActionResponse.ACTION_WARNING;
				ar.resp_msg_text="Bluetooth is inactive(Off)";
			}
		}
	}

	final static public void resetIntervalTimer(TaskManagerParms tmp,
    		EnvironmentParms ep, CommonUtilities util,TaskResponse tr, 
    		ActionResponse ar,
			String action_name, String dlg_id,
			String time_type, String time_target) {
		tr.resp_id=CMD_THREAD_TO_SVC_RESET_INTERVAL_TIMER;
		tr.active_action_name=action_name;
		tr.active_dialog_id=dlg_id;
		tr.cmd_tgt_event_name=time_target;
		tr.resp_time=StringUtil.convDateTimeTo_HourMinSecMili(System.currentTimeMillis());
		tr.active_notify_event.notifyToListener(true, new Object[] {tr});
		ar.action_resp=ActionResponse.ACTION_SUCCESS;
    };

    final static public void taskTriggerTaskControl(TaskManagerParms tmp,
    		EnvironmentParms ep, CommonUtilities util,TaskResponse tr, ActionResponse ar,
			String action_nm, String dlg_id,
			String task_type, String task_target) {
		tr.active_action_name=action_nm;
		tr.active_dialog_id=dlg_id;
		tr.cmd_tgt_task_name=task_target;
		tr.resp_time=StringUtil.convDateTimeTo_HourMinSecMili(System.currentTimeMillis());
		if (task_type.equals(PROFILE_ACTION_TYPE_TASK_START_TASK)) {
			tr.resp_id=CMD_THREAD_TO_SVC_START_TASK;
			tr.active_notify_event.notifyToListener(true, new Object[] {tr});
		} else {
			try {
				TaskManager.acqLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE,ep,tmp, util);
				TaskManager.cancelSpecificTask(tmp, ep, util,
						tr.active_group_name, tr.cmd_tgt_task_name);
			} finally {
				TaskManager.relLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE,ep,tmp, util);
			}
		}
		ar.action_resp=ActionResponse.ACTION_SUCCESS;
    };

    final static public boolean waitDeviceEvent(TaskResponse tr, 
    		ActionResponse ar, long timeout_millis){
    	long timeout_to_val=System.currentTimeMillis()+timeout_millis;
    	while(timeout_millis==0 || System.currentTimeMillis()<timeout_to_val) {
			synchronized(tr.active_thread_ctrl) {
				try {
					tr.active_thread_ctrl.wait(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (tr.active_thread_ctrl.isEnabled()) {
				if (tr.active_thread_ctrl.getExtraDataInt()==EXTRA_DEVICE_EVENT_DEVICE_CONNECTED) {
					ar.action_resp=ActionResponse.ACTION_SUCCESS;
			    	ar.resp_msg_text="";
					return true;
				} else {
					ar.action_resp=ActionResponse.ACTION_WARNING;
					ar.resp_msg_text="Device off";
				}
			} else {
				ar.action_resp=ActionResponse.ACTION_CANCELLED;
		    	ar.resp_msg_text="";
				return false;
			}
		}
		ar.action_resp=ActionResponse.ACTION_WARNING;
    	ar.resp_msg_text="Timeout";
		return false;
    }

    final static public void sendCmdToService(TaskResponse tr,  
			String action, String dlg_id, String resp_cat, String tgt_event) {
		tr.resp_id=resp_cat;
		tr.active_action_name=action;
		tr.active_dialog_id=dlg_id;
		tr.cmd_tgt_event_name=tgt_event;
		tr.resp_time=StringUtil.convDateTimeTo_HourMinSecMili(System.currentTimeMillis());
//		Thread th=new Thread() {
//			@Override
//			public void run() {
//				ntfy.notifyToListener(true, new Object[] {tr});
//			};
//		};
//		th.start();
		tr.active_notify_event.notifyToListener(true, new Object[] {tr});
	};
	
	final static public void playBackMusic(TaskManagerParms tmp,
    		EnvironmentParms ep, CommonUtilities util, TaskResponse tr,
			ActionResponse ar,String action, String dlg_id, 
			String fp, String vol_left, String vol_right) {
		ar.action_resp=ActionResponse.ACTION_SUCCESS;
		File lf=new File(fp);
		if (!lf.exists()) {
			ar.action_resp=ActionResponse.ACTION_ERROR;
			ar.resp_msg_text=String.format(tmp.teMsgs.msgs_thread_task_play_sound_notfound,fp);
			return;
		}
		if (!isRingerModeNormal(ep)) {
			ar.action_resp=ActionResponse.ACTION_WARNING;
			ar.resp_msg_text=tmp.teMsgs.msgs_thread_task_exec_ignore_sound_ringer_not_normal;
			return;
		}
		MediaPlayer player = MediaPlayer.create(tmp.context, Uri.parse(fp));
		tr.active_action_name=action;

		if (player!=null) {
			int duration=player.getDuration();
			if (Build.VERSION.SDK_INT==21) {
				TaskManager.showStopSoundPlayBackNotification(tmp,ep,util, tr,
						tr.active_group_name,tr.active_task_name,
						tr.active_action_name,tr.active_dialog_id,
						MESSAGE_DIALOG_MESSAGE_TYPE_SOUND,fp);
			} else {
				TaskManager.showMessageDialog(tmp,ep,util,
						tr.active_group_name,tr.active_task_name,
						tr.active_action_name,tr.active_dialog_id,
						MESSAGE_DIALOG_MESSAGE_TYPE_SOUND,fp);
			}

			if (!vol_left.equals("-1") && !vol_left.equals(""))
				player.setVolume(Float.valueOf(vol_left)/100, 
						Float.valueOf(vol_right)/100);
			player.start();
			waitTimeTc(tr,duration+10);
			if (!tr.active_thread_ctrl.isEnabled()) {
				ar.action_resp=ActionResponse.ACTION_CANCELLED;
				ar.resp_msg_text="Action was cancelled";
			}
			player.stop();
			player.release();
			if (Build.VERSION.SDK_INT==21) {
				TaskManager.closeStopSoundPlayBackNotification(tmp,ep,util, tr);
			} else {
				TaskManager.closeMessageDialog(tmp,ep,util,tr);
			}
		} else {
			ar.action_resp=ActionResponse.ACTION_ERROR;
			ar.resp_msg_text=String.format(tmp.teMsgs.msgs_thread_task_play_sound_error,fp);
		}
	};

	final static public boolean isRingerModeNormal(EnvironmentParms env_parms) {
		boolean result=false;
		if (env_parms.currentRingerMode==AudioManager.RINGER_MODE_NORMAL) result=true;
		return result;
	}
	
	final static private void waitTimeTc(TaskResponse tr, long wt) {
		if (tr.active_thread_ctrl.isEnabled()) {
			synchronized(tr.active_thread_ctrl) {
				try {
					tr.active_thread_ctrl.wait(wt);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};
	
	final static public boolean isLocationProviderAvailable(TaskManagerParms tmp,
    		EnvironmentParms ep, CommonUtilities util) {
		return tmp.locationUtil.isLocationProviderAvailable();
	};

	final static public boolean isGpsLocationProviderAvailable(TaskManagerParms tmp,
    		EnvironmentParms ep, CommonUtilities util) {
		return tmp.locationUtil.isGpsLocationProviderAvailable();
	};

	final static public boolean isNetworkLocationProviderAvailable(TaskManagerParms tmp,
    		EnvironmentParms ep, CommonUtilities util) {
		return tmp.locationUtil.isNetworkLocationProviderAvailable();
	};

	final static public boolean activateNetworkLocationProvider(TaskManagerParms tmp,
    		EnvironmentParms ep, CommonUtilities util) {
		return tmp.locationUtil.activateNetworkLocationProvider();
	};

	final static public boolean activateGpsLocationProvider(TaskManagerParms tmp,
    		EnvironmentParms ep, CommonUtilities util) {
		return tmp.locationUtil.activateGpsLocationProvider();
	};

	final static public boolean activateAvailableLocationProvider(TaskManagerParms tmp,
    		EnvironmentParms ep, CommonUtilities util) {
		return tmp.locationUtil.activateAvailableLocationProvider();
	};

	final static public void deactivateLocationProvider(TaskManagerParms tmp,
    		EnvironmentParms ep, CommonUtilities util) {
		tmp.locationUtil.deactivateLocationProvider();
	};

	final static public Location getCurrentLocation(TaskManagerParms tmp,
    		EnvironmentParms ep, CommonUtilities util) {
		return tmp.locationUtil.getCurrentLocation();
	};

	final static public Location getLastKnownLocation(TaskManagerParms tmp,
    		EnvironmentParms ep, CommonUtilities util) {
		return tmp.locationUtil.getLastKnownLocation();
	};
	
	final static public Location getLastKnownLocationGpsProvider(TaskManagerParms tmp,
    		EnvironmentParms ep, CommonUtilities util) {
		return tmp.locationUtil.getLastKnownLocationGpsProvider();
	};

	final static public Location getLastKnownLocationNetworkProvider(TaskManagerParms tmp,
    		EnvironmentParms ep, CommonUtilities util) {
		return tmp.locationUtil.getLastKnownLocationNetworkProvider();
	};

	final static public boolean setWifiDisableSsid(TaskManagerParms tmp,
    		EnvironmentParms ep, CommonUtilities util, TaskResponse tr,
    		ActionResponse ar) {
		WifiManager wm=(WifiManager)tmp.context.getSystemService(Context.WIFI_SERVICE);
		int nwid=wm.getConnectionInfo().getNetworkId();
		if (ep.isWifiConnected()) {
			if (!ep.wifiConnectedSsidName.equals(EnvironmentParms.WIFI_DIRECT_SSID)) {
				wm.disableNetwork(nwid);
				ar.action_resp=ActionResponse.ACTION_SUCCESS;
				ar.resp_msg_text="";
				return true;
			} else {
				ar.action_resp=ActionResponse.ACTION_WARNING;
				ar.resp_msg_text="Can not disabled, WiFi is connected to WiFi-Direct";
				return false;
			}
		}
		ar.action_resp=ActionResponse.ACTION_WARNING;
		ar.resp_msg_text="Wifi not connected";
		return false;
	}
	final static public boolean setWifiRemoveSsid(TaskManagerParms tmp,
    		EnvironmentParms ep, CommonUtilities util, TaskResponse tr,
    		ActionResponse ar) {
		WifiManager wm=(WifiManager)tmp.context.getSystemService(Context.WIFI_SERVICE);
		int nwid=wm.getConnectionInfo().getNetworkId();
		if (ep.isWifiConnected()) {
			if (!ep.wifiConnectedSsidName.equals(EnvironmentParms.WIFI_DIRECT_SSID)) {
				wm.removeNetwork(nwid);
				ar.action_resp=ActionResponse.ACTION_SUCCESS;
				ar.resp_msg_text="";
				return true;
			} else {
				ar.action_resp=ActionResponse.ACTION_WARNING;
				ar.resp_msg_text="Can not removed, WiFi is connected to WiFi-Direct";
				return false;
			}
		}
		ar.action_resp=ActionResponse.ACTION_WARNING;
		ar.resp_msg_text="Wifi not connected";
		return false;
	}
	@SuppressLint("NewApi")
	final static public boolean setScreenSwitchToHome(TaskManagerParms tmp,
    		EnvironmentParms ep, CommonUtilities util, TaskResponse tr,
    		ActionResponse ar) {
		Intent in=new Intent();
		in.setAction(Intent.ACTION_MAIN);
		in.addCategory(Intent.CATEGORY_HOME);
		in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		tmp.context.startActivity(in);
		
//        DevicePolicyManager dpm = 
//        		(DevicePolicyManager)tmp.context.getSystemService(Context.DEVICE_POLICY_SERVICE);
//        ComponentName darcn = new ComponentName(tmp.context, DevAdmReceiver.class);
//        
//        dpm.setKeyguardDisabledFeatures(darcn, DevicePolicyManager.KEYGUARD_DISABLE_FEATURES_NONE);
//        dpm.setKeyguardDisabledFeatures(darcn, DevicePolicyManager.KEYGUARD_DISABLE_TRUST_AGENTS);

		return true;
	}
	
	final static public boolean isTrusted(TaskManagerParms tmp, 
			EnvironmentParms ep, final CommonUtilities util,
			TaskResponse tr, ActionResponse ar) {
		boolean result=false;
		if (Build.VERSION.SDK_INT==17) {
			util.addDebugMsg(1, "I", "isTrusted ignored, API-17(4.2) not supported");
			ar.action_resp=ActionResponse.ACTION_ERROR;
			ar.resp_msg_text="isTrusted ignored, API-17(4.2) not supported";
			return result;
		}
		if (tmp.truestedList.size()>0) {
//			Log.v("","wifi conn="+ep.isWifiConnected()+", name="+ep.wifiSsid+", addr="+ep.wifiMacAddr);
			if (ep.isWifiConnected()) {
				for(int i=0;i<tmp.truestedList.size();i++) {
					TrustDeviceItem tli=tmp.truestedList.get(i);
					if (tli.trustedItemType==TrustDeviceItem.TYPE_WIFI_AP) {
						if (tli.trustedItemName.equals(ep.wifiConnectedSsidName)) {
							if (tli.trustedItemAddr.equals("")) {
								result=true;
								break;
							} else {
								if (tli.trustedItemAddr.equals(ep.wifiConnectedSsidAddr)) {
									result=true;
									break;
								}
							}
						}
					}
				}
			}
//			Log.v("","result="+result+", btc="+ep.isBluetoothConnected()+", name="+ep.blutoothDeviceName+", addr="+ep.blutoothDeviceAddr);
			if (!result && ep.isBluetoothConnected()) {
				for(int i=0;i<tmp.truestedList.size();i++) {
					TrustDeviceItem tli=tmp.truestedList.get(i);
					if (tli.trustedItemType==TrustDeviceItem.TYPE_BLUETOOTH_DEVICE) {
						if (tli.trustedItemName.equals(ep.blutoothConnectedDeviceName)) {
							if (tli.trustedItemAddr.equals("")) {
								result=true;
								break;
							} else {
								if (tli.trustedItemAddr.equals(ep.blutoothConnectedDeviceAddr)) {
									result=true;
									break;
								}
							}
						}
					}
				}
				
			}
		}
		return result;
	}
	
	final static public boolean setScreenLocked(TaskManagerParms tmp, final CommonUtilities util, 
			TaskResponse tr, ActionResponse ar) {
		if (!util.screenLockNow()) {
			String msg=String.format(tmp.teMsgs.msgs_thread_task_screen_lock_ignored,
					tr.active_event_name, tr.active_task_name);
			util.addLogMsg("W", msg);
			ar.action_resp=ActionResponse.ACTION_WARNING;
			ar.resp_msg_text=msg;
			return true;
		}
		return false;
	};
	
	final static public boolean setKeyguardDisabled(TaskManagerParms tmp, 
			EnvironmentParms ep, final CommonUtilities util, 
			TaskResponse tr, ActionResponse ar) {
		boolean result=false;
		if (Build.VERSION.SDK_INT==17) {
			util.addDebugMsg(1, "I", "disableKeyguard ignored, API-17(4.2) not supported");
			ar.action_resp=ActionResponse.ACTION_ERROR;
			ar.resp_msg_text="disableKeyguard ignored, API-17(4.2) not supported";
		} else {
			tmp.enableKeyguard=false;
			if (!ep.screenIsLocked) {
				tmp.setKeyguardDisabled();
				tmp.pendingRequestForEnableKeyguard=false;
				util.addDebugMsg(1, "I", "disableKeyguard issued immediately");
				result=true;
			} else {
				util.addDebugMsg(1, "I", "disableKeyguard will be issued during USER_PRESENT processed");
				tmp.pendingRequestForEnableKeyguard=true;
				result=true;
			}
			TaskManager.showNotification(tmp, ep, util);
		}

//		if (tmp.enableKeyguard) {
//			tmp.enableKeyguard=false;
//			if (!ep.screenIsLocked) {
//				tmp.setKeyguardDisabled();
//				tmp.pendingRequestForEnableKeyguard=false;
//				util.addDebugMsg(1, "I", "disableKeyguard issued immediately");
//				result=true;
//			} else {
//				util.addDebugMsg(1, "I", "disableKeyguard will be issued during USER_PRESENT processed");
//				tmp.pendingRequestForEnableKeyguard=true;
//				result=true;
//			}
//			TaskManager.showNotification(tmp, ep, util);
//		} else {
//			util.addDebugMsg(1, "I", "disableKeyguard request ignored, because keyguard is already disabled");
//		}
		return result;
	};

	final static public boolean setKeyguardEnabled(TaskManagerParms tmp, 
			EnvironmentParms ep, final CommonUtilities util, 
			TaskResponse tr, ActionResponse ar) {
		boolean result=false;
		if (Build.VERSION.SDK_INT==17) {
			util.addDebugMsg(1, "I", "reenableKeyguard ignored, API-17(4.2) not supported");
			ar.action_resp=ActionResponse.ACTION_ERROR;
			ar.resp_msg_text="reenableKeyguard ignored, API-17(4.2) not supported";
		} else {
			tmp.enableKeyguard=true;
			if (ep.screenIsLocked && ep.screenIsOn) {
				tmp.pendingRequestForEnableKeyguard=true;
				util.addDebugMsg(1, "I", "reenableKeyguard will be issued during SCREEN_OFF processed");
				result=true;
			} else {
				tmp.setKeyguardEnabled();
				if (ep.screenIsLocked) util.screenLockNow();
				tmp.pendingRequestForEnableKeyguard=false;
				util.addDebugMsg(1, "I", "reenableKeyguard issued immediately");
				result=true;
			}
			TaskManager.showNotification(tmp, ep, util);
		}
//		if (!tmp.enableKeyguard) {
//			tmp.enableKeyguard=true;
//			if (ep.screenIsLocked && ep.screenIsOn) {
//				tmp.pendingRequestForEnableKeyguard=true;
//				util.addDebugMsg(1, "I", "reenableKeyguard will be issued during SCREEN_OFF processed");
//				result=true;
//			} else {
//				tmp.setKeyguardEnabled();
//				if (ep.screenIsLocked) util.screenLockNow();
//				tmp.pendingRequestForEnableKeyguard=false;
//				util.addDebugMsg(1, "I", "reenableKeyguard issued immediately");
//				result=true;
//			}
//			TaskManager.showNotification(tmp, ep, util);
//		} else {
//			util.addDebugMsg(1, "I", "reenableKeyguard request ignored, because keyguard is already enabled");
//		}
		
//		if (Build.VERSION.SDK_INT>=21) {
//			tmp.setKeyguardEnabled();
//			tmp.pendingRequestForEnableKeyguard=false;
//			if (ep.settingForceLockWhenKeyguardEnabled) util.screenLockNow();
//			util.addDebugMsg(1, "I", "reenableKeyguard issued immediately");
//		} else {
//			if (ep.screenIsLocked && !ep.screenIsOn) {
//				tmp.setKeyguardEnabled();
//				tmp.pendingRequestForEnableKeyguard=false;
//				if (ep.settingForceLockWhenKeyguardEnabled) util.screenLockNow();
//				util.addDebugMsg(1, "I", "reenableKeyguard issued immediately");
//			} else {
//				util.addDebugMsg(1, "I", "reenableKeyguard was delayed");
//				tmp.pendingRequestForEnableKeyguard=true;
//			}
//		}
		return result;
	};

	final static public boolean playBackDefaultAlarm(TaskManagerParms tmp,
    		EnvironmentParms ep, CommonUtilities util, TaskResponse tr,
    		ActionResponse ar) {
		if (!isRingerModeNormal(ep)) {
			ar.action_resp=ActionResponse.ACTION_WARNING;
			ar.resp_msg_text=tmp.teMsgs.msgs_thread_task_exec_ignore_sound_ringer_not_normal;
			return false;
		}
		Uri uri=RingtoneManager.getActualDefaultRingtoneUri(tmp.context, RingtoneManager.TYPE_ALARM);
//		Uri uri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		if (uri!=null) {
			playBackRingtone(tmp,ep,util,tr,ar,tr.active_dialog_id, 
					PROFILE_ACTION_RINGTONE_TYPE_ALARM, "", uri.getEncodedPath(),"-1", "-1");			
		} else {
			ar.action_resp=ActionResponse.ACTION_WARNING;
			ar.resp_msg_text="Default alarm does not exists";
		}
		return true;
	}
	final static public boolean playBackDefaultNotification(TaskManagerParms tmp,
    		EnvironmentParms ep, CommonUtilities util, TaskResponse tr,
    		ActionResponse ar) {
		if (!isRingerModeNormal(ep)) {
			ar.action_resp=ActionResponse.ACTION_WARNING;
			ar.resp_msg_text=tmp.teMsgs.msgs_thread_task_exec_ignore_sound_ringer_not_normal;
			return false;
		}
		Uri uri=RingtoneManager.getActualDefaultRingtoneUri(tmp.context, RingtoneManager.TYPE_NOTIFICATION);
//		Uri uri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		if (uri!=null) {
			playBackRingtone(tmp,ep,util,tr,ar,tr.active_dialog_id, 
					PROFILE_ACTION_RINGTONE_TYPE_NOTIFICATION, "", uri.getEncodedPath(),"-1", "-1");			
		} else {
			ar.action_resp=ActionResponse.ACTION_WARNING;
			ar.resp_msg_text="Default notification does not exists";
		}

		return true;
	}
	final static public boolean playBackDefaultRingtone(TaskManagerParms tmp,
			EnvironmentParms ep, CommonUtilities util, TaskResponse tr,
			ActionResponse ar) {
		if (!isRingerModeNormal(ep)) {
			ar.action_resp=ActionResponse.ACTION_WARNING;
			ar.resp_msg_text=tmp.teMsgs.msgs_thread_task_exec_ignore_sound_ringer_not_normal;
			return false;
		}
		Uri uri=RingtoneManager.getActualDefaultRingtoneUri(tmp.context, RingtoneManager.TYPE_RINGTONE);
//		Uri uri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		if (uri!=null) {
			playBackRingtone(tmp,ep,util,tr,ar,tr.active_dialog_id, 
					PROFILE_ACTION_RINGTONE_TYPE_RINGTONE, "", uri.getEncodedPath(),"-1", "-1");			
		} else {
			ar.action_resp=ActionResponse.ACTION_WARNING;
			ar.resp_msg_text="Default ringtone does not exists";
		}
		return true;
	}

	final static public boolean setAutoSyncEnabled(TaskManagerParms tmp,
    		EnvironmentParms ep, CommonUtilities util, TaskResponse tr,
    		ActionResponse ar) {
		if (!ContentResolver.getMasterSyncAutomatically()) {
			ContentResolver.setMasterSyncAutomatically(true);
			return true;
		} else {
			ar.action_resp=ActionResponse.ACTION_WARNING;
			ar.resp_msg_text="AutoSync was already enabled, action ignored";
			return false;
		}
	};
	
	final static public boolean setAutoSyncDisabled(TaskManagerParms tmp,
    		EnvironmentParms ep, CommonUtilities util, TaskResponse tr,
    		ActionResponse ar) {
		if (ContentResolver.getMasterSyncAutomatically()) {
			ContentResolver.setMasterSyncAutomatically(false);
			return true;
		} else {
			ar.action_resp=ActionResponse.ACTION_WARNING;
			ar.resp_msg_text="AutoSync was already disabled, action ignored";
			return false;
		}
	};
	
	final static public void playBackRingtone(TaskManagerParms tmp,
			EnvironmentParms ep, CommonUtilities util, TaskResponse tr,
			ActionResponse ar, String dlg_id,
			String rt, String rn, String rp,
			String vol_left, String vol_right) {
		ar.action_resp=ActionResponse.ACTION_SUCCESS;
		if (!isRingerModeNormal(ep)) {
			ar.action_resp=ActionResponse.ACTION_WARNING;
			ar.resp_msg_text=tmp.teMsgs.msgs_thread_task_exec_ignore_sound_ringer_not_normal;
			return;
		}
		MediaPlayer player = 
				MediaPlayer.create(tmp.context, Uri.parse("content://media"+rp));
//		tr.active_action_name=action;
		if (player!=null) {
			int duration=0;
			if (!rt.equals(PROFILE_ACTION_RINGTONE_TYPE_NOTIFICATION)) duration=RINGTONE_PLAYBACK_TIME;
			else duration=player.getDuration();
	
			if (duration>=5000) {
				if (Build.VERSION.SDK_INT==21) {
					TaskManager.showStopSoundPlayBackNotification(tmp,ep,util, tr,
							tr.active_group_name,tr.active_task_name,tr.active_action_name,
							tr.active_dialog_id,MESSAGE_DIALOG_MESSAGE_TYPE_SOUND,rt+" "+rn);
				} else {
					TaskManager.showMessageDialog(tmp,ep,util,
							tr.active_group_name,tr.active_task_name,tr.active_action_name,
							tr.active_dialog_id,MESSAGE_DIALOG_MESSAGE_TYPE_SOUND,rt+" "+rn);
				}
			}
			if (!vol_left.equals("-1") && !vol_left.equals(""))
				player.setVolume(Float.valueOf(vol_left)/100, Float.valueOf(vol_right)/100);
			player.start();
			waitTimeTc(tr, duration+10);
			if (!tr.active_thread_ctrl.isEnabled()) {
				ar.action_resp=ActionResponse.ACTION_CANCELLED;
				ar.resp_msg_text="Action was cancelled";
			}
			player.stop();
			player.release();
			if (duration>=5000) {
				if (Build.VERSION.SDK_INT==21) {
					TaskManager.closeStopSoundPlayBackNotification(tmp,ep,util,tr);
				} else {
					TaskManager.closeMessageDialog(tmp,ep,util,tr);
				}
			}
		} else {
			ar.action_resp=ActionResponse.ACTION_ERROR;
			ar.resp_msg_text=String.format(tmp.teMsgs.msgs_thread_task_play_sound_error,rp);
		}
	}

	final static public void showMessage(TaskManagerParms tmp,
    		EnvironmentParms ep, CommonUtilities util, TaskResponse tr,
			ActionResponse ar,String actionnm, String dlg_id,
			String msg_type, String msg_text, boolean use_vib, boolean use_led, String led_color, boolean sound) {
		if (msg_type.equals(PROFILE_ACTION_TYPE_MESSAGE_DIALOG)) {
			tr.active_action_name=actionnm;
			tr.active_dialog_id=dlg_id;
			tr.cmd_message_type=msg_type;
			tr.cmd_message_text=msg_text;
			tr.resp_time=StringUtil.convDateTimeTo_HourMinSecMili(System.currentTimeMillis());
			TaskManager.showMessageDialog(tmp,ep,util,
					tr.active_group_name,tr.active_task_name,tr.active_action_name,
					tr.active_dialog_id,MESSAGE_DIALOG_MESSAGE_TYPE_DIALOG,tr.cmd_message_text);

		} else if (msg_type.equals(PROFILE_ACTION_TYPE_MESSAGE_NOTIFICATION)) {
			if (use_led) {
				if (led_color.equals(PROFILE_ACTION_TYPE_MESSAGE_LED_BLUE)) {
					showMessageNotification(tmp,ep,util,tr,msg_text,0xff0000ff,300,600, sound);
				} else if (led_color.equals(PROFILE_ACTION_TYPE_MESSAGE_LED_RED)) {
					showMessageNotification(tmp,ep,util,tr,msg_text,0xffff0000,300,600, sound);
				} else {
					showMessageNotification(tmp,ep,util,tr,msg_text,0xff00ff00,300,600, sound);
				}
			} else showMessageNotification(tmp,ep,util,tr,msg_text,00,0,0, sound);
		}
		if (use_vib) vibrateDefaultPattern(tmp.context,ar);
		ar.action_resp=ActionResponse.ACTION_SUCCESS;
		ar.resp_msg_text="";
    };
    
	final static private void showMessageNotification(TaskManagerParms tmp,
    		EnvironmentParms ep, CommonUtilities util, TaskResponse tr,
    		String m_text, int led_color, int led_on, int led_off, boolean sound) {
   		NotificationManager nm= (NotificationManager) 
   				tmp.context.getSystemService(Context.NOTIFICATION_SERVICE);
   		NotificationCompat.Builder nb=
   				new NotificationCompat.Builder(tmp.context);
    	nm.cancel("MSG",tmp.msgNotificationId);
		nb//.setContentIntent(pi)
		   	.setOngoing(false)
		   	.setAutoCancel(true)
		   	.setSmallIcon(R.drawable.action)
		    .setContentTitle(tmp.context.getString(R.string.app_name))
		    .setContentText(m_text)
		    .setWhen(System.currentTimeMillis())
		    
		    ;
		if (Build.VERSION.SDK_INT<=10) {
	   	    Intent in=new Intent();
			PendingIntent pi = PendingIntent.getActivity(tmp.context, 0, in,PendingIntent.FLAG_CANCEL_CURRENT);
			pi.cancel();
			nb.setContentIntent(pi);
		}
		if (sound) nb.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND);
		else nb.setDefaults(Notification.DEFAULT_VIBRATE);
		if (led_color!=0) nb.setLights(led_color, led_on, led_off);
    	Notification nf=nb.build();
    	nm.notify("MSG",tmp.msgNotificationId, nf);
    	synchronized(ep) {
        	if (tmp.msgNotificationId>=MAX_NOTIFICATION_COUNT) tmp.msgNotificationId=1;
        	else tmp.msgNotificationId++;
    	}
    };

    final static public void vibrateDefaultPattern(Context context, ActionResponse ar) {
		Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(new long[]{0,200,100,200},-1);
//		vibrator.vibrate(1000);
		ar.action_resp=ActionResponse.ACTION_SUCCESS;
		ar.resp_msg_text="";
    };
    
	final static private void executeCompareAction(TaskManagerParms tmp,
    		EnvironmentParms ep, CommonUtilities util, TaskResponse tr,
			ActionResponse ar,String tasknm, String dlg_id, 
			String comp_target, String comp_type, String[] comp_val, String result_action, String next_action) {
		String clv_str="";
		int clv=0;
		int tlv1=0,tlv2=0;
		String c_v="", sep="";
		String[] c_v_a=comp_val;
		for (int c_i=0;c_i<c_v_a.length;c_i++) {
			if (c_v_a[c_i]!=null && !c_v_a[c_i].equals("")) {
				c_v+=sep+c_v_a[c_i];
				sep=", ";
			}
		} 

		if (comp_target.equals(PROFILE_ACTION_TYPE_COMPARE_TARGET_BATTERY)) {
			clv_str=String.valueOf(ep.batteryLevel);
			clv=ep.batteryLevel;
			if (!comp_val[0].equals("")) tlv1=Integer.valueOf(comp_val[0]);
			if (comp_val.length>1 && !comp_val[1].equals("")) tlv2=Integer.valueOf(comp_val[1]);
			else tlv2=-1;
		} else if (comp_target.equals(PROFILE_ACTION_TYPE_COMPARE_TARGET_BLUETOOTH)) {
			clv_str=ep.blutoothConnectedDeviceName;
		} else if (comp_target.equals(PROFILE_ACTION_TYPE_COMPARE_TARGET_LIGHT)) {
			clv_str=String.valueOf(ep.lightSensorValue);
			clv=ep.lightSensorValue;
			if (!comp_val[0].equals("")) tlv1=Integer.valueOf(comp_val[0]);
			if (comp_val.length>1 && !comp_val[1].equals("")) tlv2=Integer.valueOf(comp_val[1]);
			else tlv2=-1;
		} else if (comp_target.equals(PROFILE_ACTION_TYPE_COMPARE_TARGET_WIFI)) {
			clv_str=ep.wifiConnectedSsidName;
		} else if (comp_target.equals(PROFILE_ACTION_TYPE_COMPARE_TARGET_TIME)) {
			SimpleDateFormat sdf=new SimpleDateFormat("HH",Locale.getDefault());
			clv_str=sdf.format(System.currentTimeMillis());
			clv=Integer.valueOf(clv_str);
			if (!comp_val[0].equals("")) tlv1=Integer.valueOf(comp_val[0]);
			if (comp_val.length>1 && !comp_val[1].equals("")) tlv2=Integer.valueOf(comp_val[1]);
			else tlv2=-1;
		}
		util.addLogMsg("I",
				String.format(tmp.teMsgs.msgs_thread_task_exec_compare, ar.current_action,
						tasknm,clv_str,comp_target,comp_type,c_v,result_action));
		if (comp_target.equals(PROFILE_ACTION_TYPE_COMPARE_TARGET_LIGHT)) {
			if (!ep.lightSensorAvailable) {
//				util.addLogMsg("W",tmp.teMsgs.msgs_thread_task_exec_compare_light_not_available);
				ar.action_resp=ActionResponse.ACTION_ERROR;
				ar.resp_msg_text=tmp.teMsgs.msgs_thread_task_exec_light_not_available;
				return ;
			}
		}
		int comp_result=0;
		if (comp_target.equals(PROFILE_ACTION_TYPE_COMPARE_TARGET_BATTERY)) {
			comp_result=executeCompareActionNumeric(comp_type,clv,tlv1,tlv2,result_action);
		} else if (comp_target.equals(PROFILE_ACTION_TYPE_COMPARE_TARGET_BLUETOOTH)) {
			comp_result=executeCompareActionString(comp_type,clv_str,comp_val,result_action);
		} else if (comp_target.equals(PROFILE_ACTION_TYPE_COMPARE_TARGET_LIGHT)) {
			comp_result=executeCompareActionNumeric(comp_type,clv,tlv1,tlv2,result_action);
		} else if (comp_target.equals(PROFILE_ACTION_TYPE_COMPARE_TARGET_WIFI)) {
			comp_result=executeCompareActionString(comp_type,clv_str,comp_val,result_action);
		} else if (comp_target.equals(PROFILE_ACTION_TYPE_COMPARE_TARGET_TIME)) {
			comp_result=executeCompareActionTime(comp_type,clv,tlv1,tlv2,result_action);
		}
		if (comp_result==ActionResponse.ACTION_ABORT) {
			ar.action_resp=ActionResponse.ACTION_ABORT;
			ar.resp_msg_text=tmp.teMsgs.msgs_thread_task_exec_compare_abort;		
		} else if (comp_result==ActionResponse.ACTION_SKIP) {
			ar.action_resp=ActionResponse.ACTION_SKIP;
			ar.resp_msg_text=String.format(tmp.teMsgs.msgs_thread_task_exec_compare_skip, next_action);		
		}
	};

	final static private int executeCompareActionString(String comp_type, String clv, String[] tlv, String result_action) {
		if (comp_type.equals(PROFILE_ACTION_TYPE_COMPARE_COMPARE_EQ)) {
			for (int i=0;i<tlv.length;i++) {
				if (clv.equals(tlv[i])) {
					if (result_action.equals(PROFILE_ACTION_TYPE_COMPARE_RESULT_ABORT)) {
						return ActionResponse.ACTION_ABORT;
					} else if (result_action.equals(PROFILE_ACTION_TYPE_COMPARE_RESULT_SKIP)) {
						return ActionResponse.ACTION_SKIP;
					}
				}
			}
		} else if (comp_type.equals(PROFILE_ACTION_TYPE_COMPARE_CPMPARE_NE)) {
			boolean cond_unsatisfied=false;
			for (int i=0;i<tlv.length;i++) {
				if (clv.equals(tlv[i])) {
					cond_unsatisfied=true;
					break;
				}
			}
			if (!cond_unsatisfied) {
				if (result_action.equals(PROFILE_ACTION_TYPE_COMPARE_RESULT_ABORT)) {
					return ActionResponse.ACTION_ABORT;
				} else if (result_action.equals(PROFILE_ACTION_TYPE_COMPARE_RESULT_SKIP)) {
					return ActionResponse.ACTION_SKIP;
				}
			}
		}
		return ActionResponse.ACTION_SUCCESS;
	};

	final static private int executeCompareActionNumeric(String comp_type, int clv, int tlv1, int tlv2, String result_action) {
		if (comp_type.equals(PROFILE_ACTION_TYPE_COMPARE_COMPARE_EQ)) {
			if (clv==tlv1) {
				if (result_action.equals(PROFILE_ACTION_TYPE_COMPARE_RESULT_ABORT)) {
					return ActionResponse.ACTION_ABORT;
				} else if (result_action.equals(PROFILE_ACTION_TYPE_COMPARE_RESULT_SKIP)) {
					return ActionResponse.ACTION_SKIP;
				}
			}
		} else if (comp_type.equals(PROFILE_ACTION_TYPE_COMPARE_CPMPARE_NE)) {
			if (clv!=tlv1) {
				if (result_action.equals(PROFILE_ACTION_TYPE_COMPARE_RESULT_ABORT)) {
					return ActionResponse.ACTION_ABORT;
				} else if (result_action.equals(PROFILE_ACTION_TYPE_COMPARE_RESULT_SKIP)) {
					return ActionResponse.ACTION_SKIP;
				}
			}
		} else if (comp_type.equals(PROFILE_ACTION_TYPE_COMPARE_COMPARE_GT)) {
			if (clv>tlv1) {
				if (result_action.equals(PROFILE_ACTION_TYPE_COMPARE_RESULT_ABORT)) {
					return ActionResponse.ACTION_ABORT;
				} else if (result_action.equals(PROFILE_ACTION_TYPE_COMPARE_RESULT_SKIP)) {
					return ActionResponse.ACTION_SKIP;
				}
			}
		} else if (comp_type.equals(PROFILE_ACTION_TYPE_COMPARE_COMPARE_LT)) {
			if (clv<tlv1) {
				if (result_action.equals(PROFILE_ACTION_TYPE_COMPARE_RESULT_ABORT)) {
					return ActionResponse.ACTION_ABORT;
				} else if (result_action.equals(PROFILE_ACTION_TYPE_COMPARE_RESULT_SKIP)) {
					return ActionResponse.ACTION_SKIP;
				}
			}
		} else if (comp_type.equals(PROFILE_ACTION_TYPE_COMPARE_COMPARE_BETWEEN)) {
			if (clv>=tlv1 && clv<=tlv2) {
				if (result_action.equals(PROFILE_ACTION_TYPE_COMPARE_RESULT_ABORT)) {
					return ActionResponse.ACTION_ABORT;
				} else if (result_action.equals(PROFILE_ACTION_TYPE_COMPARE_RESULT_SKIP)) {
					return ActionResponse.ACTION_SKIP;
				}
			}
		}
		return ActionResponse.ACTION_SUCCESS;
	};

	final static private int executeCompareActionTime(String comp_type, int clv, int tlv1, int tlv2, String result_action) {
		if (comp_type.equals(PROFILE_ACTION_TYPE_COMPARE_COMPARE_EQ)) {
			if (clv==tlv1) {
				if (result_action.equals(PROFILE_ACTION_TYPE_COMPARE_RESULT_ABORT)) {
					return ActionResponse.ACTION_ABORT;
				} else if (result_action.equals(PROFILE_ACTION_TYPE_COMPARE_RESULT_SKIP)) {
					return ActionResponse.ACTION_SKIP;
				}
			}
		} else if (comp_type.equals(PROFILE_ACTION_TYPE_COMPARE_CPMPARE_NE)) {
			if (clv!=tlv1) {
				if (result_action.equals(PROFILE_ACTION_TYPE_COMPARE_RESULT_ABORT)) {
					return ActionResponse.ACTION_ABORT;
				} else if (result_action.equals(PROFILE_ACTION_TYPE_COMPARE_RESULT_SKIP)) {
					return ActionResponse.ACTION_SKIP;
				}
			}
		} else if (comp_type.equals(PROFILE_ACTION_TYPE_COMPARE_COMPARE_GT)) {
			if (clv>tlv1) {
				if (result_action.equals(PROFILE_ACTION_TYPE_COMPARE_RESULT_ABORT)) {
					return ActionResponse.ACTION_ABORT;
				} else if (result_action.equals(PROFILE_ACTION_TYPE_COMPARE_RESULT_SKIP)) {
					return ActionResponse.ACTION_SKIP;
				}
			}
		} else if (comp_type.equals(PROFILE_ACTION_TYPE_COMPARE_COMPARE_LT)) {
			if (clv<tlv1) {
				if (result_action.equals(PROFILE_ACTION_TYPE_COMPARE_RESULT_ABORT)) {
					return ActionResponse.ACTION_ABORT;
				} else if (result_action.equals(PROFILE_ACTION_TYPE_COMPARE_RESULT_SKIP)) {
					return ActionResponse.ACTION_SKIP;
				}
			}
		} else if (comp_type.equals(PROFILE_ACTION_TYPE_COMPARE_COMPARE_BETWEEN)) {
			if (tlv2>23) {
				int t_clv=clv;
				if (clv>23) t_clv=(clv+24);
				if (clv>=tlv1 && t_clv<=tlv2) {
					if (result_action.equals(PROFILE_ACTION_TYPE_COMPARE_RESULT_ABORT)) {
						return ActionResponse.ACTION_ABORT;
					} else if (result_action.equals(PROFILE_ACTION_TYPE_COMPARE_RESULT_SKIP)) {
						return ActionResponse.ACTION_SKIP;
					}
				}
			} else {
				if (clv>=tlv1 && clv<=tlv2) {
					if (result_action.equals(PROFILE_ACTION_TYPE_COMPARE_RESULT_ABORT)) {
						return ActionResponse.ACTION_ABORT;
					} else if (result_action.equals(PROFILE_ACTION_TYPE_COMPARE_RESULT_SKIP)) {
						return ActionResponse.ACTION_SKIP;
					}
				}
			}
		}
		return ActionResponse.ACTION_SUCCESS;
	};
	
	@SuppressWarnings("deprecation")
	final static public void setScreenOnAsync(TaskManagerParms tmp, TaskResponse tr, ActionResponse ar) {
   		WakeLock wakelock= 
   	    		((PowerManager)tmp.context.getSystemService(Context.POWER_SERVICE))
   	    			.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
   	    				| PowerManager.ACQUIRE_CAUSES_WAKEUP
//   	   	    				| PowerManager.ON_AFTER_RELEASE
   	    				, "Scheduler-ScreenOn");
		wakelock.acquire(10*1*1000);
//   		if (Build.VERSION.SDK_INT>=13) {
//			screenOnByActivity(tmp,tr,ar);
//		} else {
//			wakelock.acquire(10*1*1000);
//		}
		ar.action_resp=ActionResponse.ACTION_SUCCESS;
		ar.resp_msg_text="";
	};
	
	@SuppressWarnings("deprecation")
	final static public void setScreenOnSync(TaskManagerParms tmp,TaskResponse tr, ActionResponse ar) {
   		WakeLock wakelock= 
   	    		((PowerManager)tmp.context.getSystemService(Context.POWER_SERVICE))
   	    			.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
   	    				| PowerManager.ACQUIRE_CAUSES_WAKEUP
   	   	    				| PowerManager.ON_AFTER_RELEASE
   	    				, "Scheduler-ScreenOn");
   		try {
   			wakelock.acquire();
//   			if (Build.VERSION.SDK_INT>=13) {
//   				screenOnByActivity(tmp,tr,ar);
//   			} else {
//   	   			wakelock.acquire();
//   			}
   			waitTimeTc(tr,10*1*1000);
   			ar.action_resp=ActionResponse.ACTION_SUCCESS;
   			ar.resp_msg_text="";
   		} finally {
   			if (wakelock.isHeld()) wakelock.release();
   		}
	};

    final static public void setBluetoothOff(TaskManagerParms tmp,
    		EnvironmentParms ep, CommonUtilities util, TaskResponse tr,
			ActionResponse ar) {
			if (BluetoothAdapter.getDefaultAdapter()!=null) {
		        if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
		        	if (ep.settingDebugLevel>=1) util.addDebugMsg(1,"I","setBluetoothOff Bluetooth off");
		        	BluetoothAdapter.getDefaultAdapter().disable();
		        	ar.action_resp=ActionResponse.ACTION_SUCCESS;
		        	ar.resp_msg_text="";
		        } else {
					ar.action_resp=ActionResponse.ACTION_WARNING;
					ar.resp_msg_text="Bluetooth already off";
		        }
			} else {
				if (ep.settingDebugLevel>=1) util.addDebugMsg(1,"I","setBluetoothOff BluetoothAdapter not available, Bluetooth off ignored");
				ar.action_resp=ActionResponse.ACTION_WARNING;
				ar.resp_msg_text="Bluetooth not available";
			}
	};

	final static public void setBluetoothOn(TaskManagerParms tmp,
    		EnvironmentParms ep, CommonUtilities util, TaskResponse tr,
			ActionResponse ar) {
			if (BluetoothAdapter.getDefaultAdapter()!=null) {
		        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
		        	if (ep.settingDebugLevel>=1) util.addDebugMsg(1,"I","setBluetoothOn Bluetooth on");
		        	BluetoothAdapter.getDefaultAdapter().enable();
		        	ar.action_resp=ActionResponse.ACTION_SUCCESS;
		        	ar.resp_msg_text="";
		        } else {
					ar.action_resp=ActionResponse.ACTION_WARNING;
					ar.resp_msg_text="Bluetooth already on";
		        }
			} else {
				if (ep.settingDebugLevel>=1) util.addDebugMsg(1,"I","setBluetoothOn BluetoothAdapter not available, Bluetooth on ignored");
				ar.action_resp=ActionResponse.ACTION_WARNING;
				ar.resp_msg_text="Bluetooth not available";
			}
	};

	final static public void setWifiOn(TaskManagerParms tmp,
    		EnvironmentParms ep, CommonUtilities util, TaskResponse tr,
			ActionResponse ar) {
		WifiManager wm = (WifiManager)tmp.context.getSystemService(Context.WIFI_SERVICE);
		int ws=-1;
		if (wm!=null) ws=wm.getWifiState();
		if (ep.settingDebugLevel>=1) 
			util.addDebugMsg(1,"I","setWifiOn WIFI On entered, wifiIsActive="+ep.wifiIsActive+
					", wifiMgrStatus="+ws);
    	if (!ep.wifiIsActive) {
    		if (wm!=null) {
    			if (wm.setWifiEnabled(true)) {
            		ar.action_resp=ActionResponse.ACTION_SUCCESS;
            		ar.resp_msg_text="setWifiOn WIFI On Success";
        		} else {
        			if (ep.settingDebugLevel>=1) util.addDebugMsg(1,"I","setWifiOn WifiManager error, WIFI On ignored");
    				ar.action_resp=ActionResponse.ACTION_WARNING;
    				ar.resp_msg_text="setWifiOn WifiManager error";
        		}
    		} else {
    			if (ep.settingDebugLevel>=1) util.addDebugMsg(1,"I","setWifiOn WifiManager not available, WIFI On ignored");
				ar.action_resp=ActionResponse.ACTION_WARNING;
				ar.resp_msg_text="setWifiOn WIFI not available";
    		}
    	} else {
			ar.action_resp=ActionResponse.ACTION_WARNING;
			ar.resp_msg_text="setWifiOn WIFI already on";
    	}
    };
    
    final static public void setWifiOff(TaskManagerParms tmp,
    		EnvironmentParms ep, CommonUtilities util, TaskResponse tr,
			ActionResponse ar) {
    	WifiManager wm = (WifiManager)tmp.context.getSystemService(Context.WIFI_SERVICE);
		int ws=-1;
		if (wm!=null) ws=wm.getWifiState();
		if (ep.settingDebugLevel>=1) 
			util.addDebugMsg(1,"I","setWifiOff WIFI Off entered, wifiIsActive="+ep.wifiIsActive+
					", wifiMgrStatus="+ws);
    	if (ep.wifiIsActive) {
    		if (wm!=null) {
        		wm.setWifiEnabled(false);
        		ar.action_resp=ActionResponse.ACTION_SUCCESS;
        		ar.resp_msg_text="setWifiOff WIFI Off Success";
    		} else {
    			if (ep.settingDebugLevel>=1) util.addDebugMsg(1,"I","setWifiOff WifiManager not available, WIFI Off ignored");
    			ar.action_resp=ActionResponse.ACTION_WARNING;
    			ar.resp_msg_text="setWifiOff WIFI not available";
    		}
    	} else {
			ar.action_resp=ActionResponse.ACTION_WARNING;
			ar.resp_msg_text="setWifiOff WIFI already off";
    	} 
    };
}



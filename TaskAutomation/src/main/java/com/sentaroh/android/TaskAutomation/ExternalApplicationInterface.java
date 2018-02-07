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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import com.sentaroh.android.TaskAutomation.Common.EnvironmentParms;
import com.sentaroh.android.TaskAutomation.Common.ProfileListItem;
import com.sentaroh.android.TaskAutomation.Common.TaskListItem;
import com.sentaroh.android.TaskAutomation.Common.TaskManagerParms;
import com.sentaroh.android.TaskAutomation.Config.ProfileUtilities;
import com.sentaroh.android.TaskAutomationInterface.TaInterfaceParms;
import com.sentaroh.android.TaskAutomationInterface.TaServiceInterface;








import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.TelephonyManager;

public class ExternalApplicationInterface {
	
	static final public void processExternalIntent(final TaskManagerParms taskMgrParms,
			final EnvironmentParms envParms, final CommonUtilities util, 
			final Service svcInstance, Intent in, String action,
			final ArrayList<TaskListItem> timerEventTaskList, 
			final ArrayList<TaskListItem> builtinEventTaskList,
			final ArrayList<TaskListItem> taskEventTaskList, 
			final ArrayList<ProfileListItem>profileArrayList) {
    	final TaInterfaceParms taip=new TaInterfaceParms();
    	taskMgrParms.externalInterfaceRequestId++;
    	final long req_id=taskMgrParms.externalInterfaceRequestId;
    	TaServiceInterface.readRequest(taip, in);
    	if (envParms.settingDebugLevel>=1) {
    		util.addDebugMsg(1, "I", "ExtIntf request: ID="+req_id,
    				", Requestor name=",taip.requestor_name,", Package=",taip.requestor_pkg+
    				", Request=",taip.request_cmd,", Sub=",taip.request_cmd_sub_type,", Group=",taip.request_group_name,", Task=",taip.request_task_name);
    	}
    	if (envParms.settingEnableScheduler) {
        	Runnable r=new Runnable() {
        		@Override
        		public void run() {
        			if (taip.reply_action!=null && !taip.reply_action.equals("")) {
        				if (taip.request_cmd.equals(TaServiceInterface.REQUEST_SYS_INFO)) {
        					processSysInfo(taskMgrParms, envParms, util,svcInstance, taip);
        				} else if (taip.request_cmd.equals(TaServiceInterface.REQUEST_TASK)) {
        					processTask(taskMgrParms, envParms, util,svcInstance, taip,
        							timerEventTaskList, builtinEventTaskList, taskEventTaskList, profileArrayList);
        				} else if (taip.request_cmd.equals(TaServiceInterface.REQUEST_GROUP)) {
        					processGroup(taskMgrParms, envParms, util,svcInstance, taip,
        							timerEventTaskList, builtinEventTaskList, taskEventTaskList,
        							profileArrayList);
        				} else {
        					taip.reply_result_success=false;;
            				taip.reply_result_status_code=TaServiceInterface.REQUEST_RESULT_REASON_UNKNOWN_COMMAND;
            				Intent r_in=new Intent();
            				TaServiceInterface.writeReply(taip, r_in);
            				TaServiceInterface.replyExternalApplication(svcInstance, taip, r_in);
        				}
        			} else {
        				taip.reply_result_success=false;;
        				taip.reply_result_status_code=TaServiceInterface.REQUEST_RESULT_REASON_IVALID_REPLY_ACTION;
        				Intent r_in=new Intent();
        				TaServiceInterface.writeReply(taip, r_in);
        				TaServiceInterface.replyExternalApplication(svcInstance, taip, r_in);
        			}
        	    	if (envParms.settingDebugLevel>=1) {
        	    		util.addDebugMsg(1, "I", "ExtIntf response: ID="+req_id,
        	    				", Success="+taip.reply_result_success+", Status="+taip.reply_result_status_code);
        	    	}
        		}
        	};
        	taskMgrParms.normalTaskControlThreadPool.execute(r);
    	} else {
			taip.reply_result_success=false;;
			taip.reply_result_status_code=TaServiceInterface.REQUEST_RESULT_REASON_SCHEDULER_DISABLED;
			Intent r_in=new Intent();
			TaServiceInterface.writeReply(taip, r_in);
			TaServiceInterface.replyExternalApplication(svcInstance, taip, r_in);
			
	    	if (envParms.settingDebugLevel>=1) {
	    		util.addDebugMsg(1, "I", "ExtIntf response: ID="+req_id,
	    				", Request=",taip.request_cmd,", Sub=",taip.request_cmd_sub_type,", Group=",taip.request_group_name,", Task=",taip.request_task_name);
    		}
    	}
	};

	final static private void processGroup(TaskManagerParms taskMgrParms,
			final EnvironmentParms envParms, CommonUtilities util, final Service svcInstance, TaInterfaceParms taip,
			ArrayList<TaskListItem> timerEventTaskList, ArrayList<TaskListItem> builtinEventTaskList, 
			ArrayList<TaskListItem> taskEventTaskList, ArrayList<ProfileListItem> profileArrayList) {
		Intent r_in=new Intent();
		if (taip.request_cmd_sub_type.equals(TaServiceInterface.REQUEST_TASK_STATUS) ) {
			processGroupStatus(taskMgrParms,
					envParms, util, svcInstance, taip,
					timerEventTaskList, builtinEventTaskList, taskEventTaskList, profileArrayList);
		} else if (taip.request_cmd_sub_type.equals(TaServiceInterface.REQUEST_TASK_LIST) ) {
			processGroupList(taskMgrParms,
					envParms, util, svcInstance, taip,
					timerEventTaskList, builtinEventTaskList, taskEventTaskList,profileArrayList);
		} else {
			taip.reply_result_success=false;
			taip.reply_result_status_code=TaServiceInterface.REQUEST_RESULT_REASON_UNKNOWN_COMMAND_SUB_TYPE;
			TaServiceInterface.writeReply(taip, r_in);
			TaServiceInterface.replyExternalApplication(svcInstance, taip, r_in);
		}
	};

	final static private int getGroupStatus(TaskManagerParms taskMgrParms,
			final EnvironmentParms envParms, CommonUtilities util, final Service svcInstance, TaInterfaceParms taip,
			ArrayList<TaskListItem> timerEventTaskList, ArrayList<TaskListItem> builtinEventTaskList, 
			ArrayList<TaskListItem> taskEventTaskList, ArrayList<ProfileListItem> profileArrayList) {
		int result=0;
		if (ProfileUtilities.isProfileGroupExists(util, profileArrayList, taip.request_group_name)) {
			if (ProfileUtilities.isProfileGroupActive(util, profileArrayList, taip.request_group_name)) {
				result=TaServiceInterface.REQUEST_RESULT_REASON_GROUP_ACTIVE;
			} else {
				result=TaServiceInterface.REQUEST_RESULT_REASON_GROUP_INACTIVE;
			}
		} else {
			result=TaServiceInterface.REQUEST_RESULT_REASON_GROUP_NOT_FOUND;
		}
		return result;
	}
	final static private void processGroupStatus(TaskManagerParms taskMgrParms,
			final EnvironmentParms envParms, CommonUtilities util, final Service svcInstance, TaInterfaceParms taip,
			ArrayList<TaskListItem> timerEventTaskList, ArrayList<TaskListItem> builtinEventTaskList, 
			ArrayList<TaskListItem> taskEventTaskList, ArrayList<ProfileListItem> profileArrayList) {
		Intent r_in=new Intent();
		taip.reply_result_success=true;
		taip.reply_result_status_code=getGroupStatus(taskMgrParms, envParms, util, svcInstance, taip,timerEventTaskList, builtinEventTaskList, taskEventTaskList,profileArrayList);
		TaServiceInterface.writeReply(taip, r_in);
		TaServiceInterface.replyExternalApplication(svcInstance, taip, r_in);
	};
	final static private void processGroupList(TaskManagerParms taskMgrParms,
			final EnvironmentParms envParms, CommonUtilities util, final Service svcInstance, TaInterfaceParms taip,
			ArrayList<TaskListItem> timerEventTaskList, ArrayList<TaskListItem> builtinEventTaskList, ArrayList<TaskListItem> taskEventTaskList, ArrayList<ProfileListItem>profileArrayList) {
		Intent r_in=new Intent();
		
		ArrayList<String>act_list=new ArrayList<String>();
		ArrayList<String>inact_list=new ArrayList<String>();
		for (int i=0;i<profileArrayList.size();i++) {
			ProfileListItem pfli=profileArrayList.get(i);
			if (pfli.isProfileGroupActivated()) {
				if (!act_list.contains(pfli.getProfileGroup())) {
					act_list.add(pfli.getProfileGroup());
				}
			} else {
				if (!inact_list.contains(pfli.getProfileGroup())) {
					inact_list.add(pfli.getProfileGroup());
				}
			}
		}
		Collections.sort(act_list);
		Collections.sort(inact_list);
		
		int s_sz=act_list.size()+inact_list.size();
		String[][]g_list=new String[s_sz][2];
		int cnt=0;
		for (int i=0;i<act_list.size();i++) {
			g_list[cnt][0]="Active";
			g_list[cnt][1]=act_list.get(i);
			cnt++;
		}
		for (int i=0;i<inact_list.size();i++) {
			g_list[cnt][0]="Inactive";
			g_list[cnt][1]=inact_list.get(i);
			cnt++;
		}
		taip.reply_group_list=g_list;

		taip.reply_result_success=true;
		TaServiceInterface.writeReply(taip, r_in);
		TaServiceInterface.replyExternalApplication(svcInstance, taip, r_in);
	};
	
	final static private void processTask(TaskManagerParms taskMgrParms,
			final EnvironmentParms envParms, CommonUtilities util, final Service svcInstance, TaInterfaceParms taip,
			ArrayList<TaskListItem> timerEventTaskList, ArrayList<TaskListItem> builtinEventTaskList, ArrayList<TaskListItem> taskEventTaskList, ArrayList<ProfileListItem> profileArrayList) {		Intent r_in=new Intent();
		if (taip.request_cmd_sub_type.equals(TaServiceInterface.REQUEST_TASK_STATUS) ) {
			processTaskStatus(taskMgrParms,
					envParms, util, svcInstance, taip,
					timerEventTaskList, builtinEventTaskList, taskEventTaskList,profileArrayList);
		} else if (taip.request_cmd_sub_type.equals(TaServiceInterface.REQUEST_TASK_LIST) ) {
			processTaskList(taskMgrParms,
					envParms, util, svcInstance, taip,
					timerEventTaskList, builtinEventTaskList, taskEventTaskList, profileArrayList);
		} else if (taip.request_cmd_sub_type.equals(TaServiceInterface.REQUEST_TASK_CANCEL) ) {
			processTaskCancel(taskMgrParms,
					envParms, util, svcInstance, taip,
					timerEventTaskList, builtinEventTaskList, taskEventTaskList,profileArrayList);
		} else if (taip.request_cmd_sub_type.equals(TaServiceInterface.REQUEST_TASK_START) ) {
			processTaskStart(taskMgrParms,
					envParms, util, svcInstance, taip,
					timerEventTaskList, builtinEventTaskList, taskEventTaskList,profileArrayList);
		} else {
			taip.reply_result_success=false;
			taip.reply_result_status_code=TaServiceInterface.REQUEST_RESULT_REASON_UNKNOWN_COMMAND_SUB_TYPE;
			TaServiceInterface.writeReply(taip, r_in);
			TaServiceInterface.replyExternalApplication(svcInstance, taip, r_in);
		}
	};
	
	final static private void processTaskStatus(TaskManagerParms taskMgrParms,
			final EnvironmentParms envParms, CommonUtilities util, final Service svcInstance, TaInterfaceParms taip,
			ArrayList<TaskListItem> timerEventTaskList, ArrayList<TaskListItem> builtinEventTaskList, ArrayList<TaskListItem> taskEventTaskList, ArrayList<ProfileListItem> profileArrayList) {
		Intent r_in=new Intent();
		taip.reply_result_success=true;
		taip.reply_result_status_code=getTaskStaus(taskMgrParms, envParms, util, svcInstance, taip,timerEventTaskList, builtinEventTaskList, taskEventTaskList, profileArrayList);
		TaServiceInterface.writeReply(taip, r_in);
		TaServiceInterface.replyExternalApplication(svcInstance, taip, r_in);
	};

	final static private ArrayList<String[]> buildProfileTaskList(TaskManagerParms taskMgrParms,
			final EnvironmentParms envParms, CommonUtilities util, final Service svcInstance, TaInterfaceParms taip,
			ArrayList<TaskListItem> timerEventTaskList, ArrayList<TaskListItem> builtinEventTaskList, ArrayList<TaskListItem> taskEventTaskList, ArrayList<ProfileListItem>profileArrayList) {
		ArrayList<String[]>prof_task_list=new ArrayList<String[]>();
		for (int i=0;i<profileArrayList.size();i++) {
			ProfileListItem pli=profileArrayList.get(i);
			if (pli.getProfileType().equals(PROFILE_TYPE_TASK) && 
					pli.getProfileGroup().equals(taip.request_group_name)) {
				String[] tli=new String[3];
				tli[0]=pli.getProfileGroup();
				tli[1]=pli.getProfileName();
				if (pli.isProfileEnabled()) tli[2]="Enabled";
				else tli[2]="Disabled";
				prof_task_list.add(tli);
			}
		}
		
		updateProfileTaskListByEventTaskList(prof_task_list,timerEventTaskList,"Inactive");
		updateProfileTaskListByEventTaskList(prof_task_list,builtinEventTaskList,"Inactive");
		updateProfileTaskListByEventTaskList(prof_task_list,taskEventTaskList,"Inactive");
		updateProfileTaskListByEventTaskList(prof_task_list,taskMgrParms.taskQueueList,"Active");
		updateProfileTaskListByEventTaskList(prof_task_list,taskMgrParms.activeTaskList,"Active");
		
		return prof_task_list;
	};
	final static private void updateProfileTaskListByEventTaskList(
			ArrayList<String[]>task_list, ArrayList<TaskListItem> event_task_list,
			String status) {
		for (int i=0;i<event_task_list.size();i++) {
			TaskListItem tli=event_task_list.get(i);
			int pos=findProfileTaskList(task_list,tli.group_name,tli.task_name);
			if (pos>=0) {
				task_list.get(pos)[2]=status;
			}
		}
	};
	final static private void updateProfileTaskListByEventTaskList(
			ArrayList<String[]>task_list, LinkedList<TaskListItem> event_task_list,
			String status) {
		for (int i=0;i<event_task_list.size();i++) {
			TaskListItem tli=event_task_list.get(i);
			int pos=findProfileTaskList(task_list,tli.group_name,tli.task_name);
			if (pos>=0) {
				task_list.get(pos)[2]=status;
			}
		}
	};
	final static private int findProfileTaskList(
			ArrayList<String[]>task_list, String grp, String name) {
		int result=-1;
		for (int i=0;i<task_list.size();i++) {
			String[] tli=task_list.get(i);
			if (tli[0].equals(grp) && tli[1].equals(name)) {
				result=i;
				break;
			}
		}
		return result;
	};
	
	final static private void processTaskList(TaskManagerParms taskMgrParms,
			final EnvironmentParms envParms, CommonUtilities util, final Service svcInstance, TaInterfaceParms taip,
			ArrayList<TaskListItem> timerEventTaskList, ArrayList<TaskListItem> builtinEventTaskList, 
			ArrayList<TaskListItem> taskEventTaskList, ArrayList<ProfileListItem>profileArrayList) {
		Intent r_in=new Intent();
		TaskManager.acqLock(TaskManager.LOCK_ID_EVENT_TASK_LIST, TaskManager.LOCK_MODE_READ, envParms, taskMgrParms, util);
		TaskManager.acqLock(TaskManager.LOCK_ID_TIMER_TASK_LIST, TaskManager.LOCK_MODE_READ, envParms, taskMgrParms, util);
		TaskManager.acqLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_READ, envParms, taskMgrParms, util);
		taip.reply_result_success=true;
		taip.reply_result_status_code=0;
		ArrayList<String[]>task_list=
				buildProfileTaskList(taskMgrParms, envParms, util, svcInstance,taip,
				timerEventTaskList, builtinEventTaskList, taskEventTaskList, profileArrayList);

		if (task_list.size()>0) {
			taip.reply_task_list=new String[task_list.size()][3];
			for (int i=0;i<task_list.size();i++) {
				taip.reply_task_list[i][0]=task_list.get(i)[0];//group name
				taip.reply_task_list[i][1]=task_list.get(i)[1];//task name
				taip.reply_task_list[i][2]=task_list.get(i)[2];//status
			}
		} else taip.reply_result_status_code=TaServiceInterface.REQUEST_RESULT_REASON_TASK_NOT_FOUND;
		
		TaskManager.relLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_READ, envParms, taskMgrParms, util);
		TaskManager.relLock(TaskManager.LOCK_ID_TIMER_TASK_LIST, TaskManager.LOCK_MODE_READ, envParms, taskMgrParms, util);
		TaskManager.relLock(TaskManager.LOCK_ID_EVENT_TASK_LIST, TaskManager.LOCK_MODE_READ, envParms, taskMgrParms, util);
		TaServiceInterface.writeReply(taip, r_in);
		TaServiceInterface.replyExternalApplication(svcInstance, taip, r_in);
	};
	
	final static private void processTaskCancel(TaskManagerParms taskMgrParms,
			final EnvironmentParms envParms, CommonUtilities util, final Service svcInstance, TaInterfaceParms taip,
			ArrayList<TaskListItem> timerEventTaskList, ArrayList<TaskListItem> builtinEventTaskList, ArrayList<TaskListItem> taskEventTaskList, ArrayList<ProfileListItem> profileArrayList) {		Intent r_in=new Intent();
		TaskManager.acqLock(TaskManager.LOCK_ID_EVENT_TASK_LIST, TaskManager.LOCK_MODE_READ, envParms, taskMgrParms, util);
		TaskManager.acqLock(TaskManager.LOCK_ID_TIMER_TASK_LIST, TaskManager.LOCK_MODE_READ, envParms, taskMgrParms, util);
		TaskManager.acqLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE, envParms, taskMgrParms, util);
		taip.reply_result_success=true;
		taip.reply_result_status_code=getTaskStaus(taskMgrParms, envParms, util, svcInstance, taip,timerEventTaskList, builtinEventTaskList, taskEventTaskList, profileArrayList);
		if (taip.reply_result_status_code==TaServiceInterface.REQUEST_RESULT_REASON_TASK_ACTIVE) {
			taip.reply_result_status_code=0;
			TaskManager.cancelSpecificTask(taskMgrParms, envParms, util, taip.request_group_name, taip.request_task_name);
		} else {
			if (taip.reply_result_status_code!=TaServiceInterface.REQUEST_RESULT_REASON_TASK_NOT_FOUND)
				taip.reply_result_status_code=TaServiceInterface.REQUEST_RESULT_REASON_TASK_NOT_ACTIVE;
		}
		TaskManager.relLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE, envParms, taskMgrParms, util);
		TaskManager.relLock(TaskManager.LOCK_ID_TIMER_TASK_LIST, TaskManager.LOCK_MODE_READ, envParms, taskMgrParms, util);
		TaskManager.relLock(TaskManager.LOCK_ID_EVENT_TASK_LIST, TaskManager.LOCK_MODE_READ, envParms, taskMgrParms, util);
		TaServiceInterface.writeReply(taip, r_in);
		TaServiceInterface.replyExternalApplication(svcInstance, taip, r_in);
	};

	final static private void processTaskStart(TaskManagerParms taskMgrParms,
			final EnvironmentParms envParms, CommonUtilities util, final Service svcInstance, TaInterfaceParms taip,
			ArrayList<TaskListItem> timerEventTaskList, ArrayList<TaskListItem> builtinEventTaskList, ArrayList<TaskListItem> taskEventTaskList, ArrayList<ProfileListItem> profileArrayList) {		Intent r_in=new Intent();
		TaskManager.acqLock(TaskManager.LOCK_ID_EVENT_TASK_LIST, TaskManager.LOCK_MODE_READ, envParms, taskMgrParms, util);
		TaskManager.acqLock(TaskManager.LOCK_ID_TIMER_TASK_LIST, TaskManager.LOCK_MODE_READ, envParms, taskMgrParms, util);
		TaskManager.acqLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE, envParms, taskMgrParms, util);
		taip.reply_result_success=true;
		taip.reply_result_status_code=getTaskStaus(taskMgrParms, envParms, util, svcInstance, taip,timerEventTaskList, builtinEventTaskList, taskEventTaskList, profileArrayList);
		if (taip.reply_result_status_code==TaServiceInterface.REQUEST_RESULT_REASON_TASK_INACTIVE) {
			taip.reply_result_status_code=0;
			TaskListItem tli=getTaskListItem(taskMgrParms, envParms, util, svcInstance, taip,timerEventTaskList, builtinEventTaskList, taskEventTaskList);
			TaskManager.scheduleTask(taskMgrParms, envParms, util, tli);
		} else {
			if (taip.reply_result_status_code==TaServiceInterface.REQUEST_RESULT_REASON_TASK_ACTIVE) 
				taip.reply_result_status_code=TaServiceInterface.REQUEST_RESULT_REASON_TASK_ALREADY_ACTIVE;
		}
		TaskManager.relLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE, envParms, taskMgrParms, util);
		TaskManager.relLock(TaskManager.LOCK_ID_TIMER_TASK_LIST, TaskManager.LOCK_MODE_READ, envParms, taskMgrParms, util);
		TaskManager.relLock(TaskManager.LOCK_ID_EVENT_TASK_LIST, TaskManager.LOCK_MODE_READ, envParms, taskMgrParms, util);
		TaServiceInterface.writeReply(taip, r_in);
		TaServiceInterface.replyExternalApplication(svcInstance, taip, r_in);
	};
	
	final static private int getTaskStaus(TaskManagerParms taskMgrParms,
			final EnvironmentParms envParms, CommonUtilities util, final Service svcInstance, TaInterfaceParms taip,
			ArrayList<TaskListItem> timerEventTaskList, ArrayList<TaskListItem> builtinEventTaskList, ArrayList<TaskListItem> taskEventTaskList, ArrayList<ProfileListItem> profileArrayList) {
		int result=-1;
		TaskListItem ati=TaskManager.getActiveTaskListItem(taskMgrParms, envParms, util, taip.request_group_name, taip.request_task_name);
		if (ati==null) {
			ati=TaskManager.getTaskQueueListItem(taskMgrParms, envParms, util, taip.request_group_name, taip.request_task_name);
			if (ati==null) {
				if (findTaskFromEventTaskList(timerEventTaskList, taip.request_group_name, taip.request_task_name)==null &&
					findTaskFromEventTaskList(builtinEventTaskList, taip.request_group_name, taip.request_task_name)==null &&
					findTaskFromEventTaskList(taskEventTaskList, taip.request_group_name, taip.request_task_name)==null) {
					boolean found=false;
					ProfileListItem pfli=null;
					for (int i=0;i<profileArrayList.size();i++) {
						pfli=profileArrayList.get(i);
						if (pfli.getProfileType().equals(PROFILE_TYPE_TASK)&&
								pfli.getProfileGroup().equals(taip.request_group_name)&&
								pfli.getProfileName().equals(taip.request_task_name)) {
							found=true;
							break;
						}
					}
					if (found) {
						if (pfli.isProfileEnabled()) result=TaServiceInterface.REQUEST_RESULT_REASON_TASK_ENABLED;
						else result=TaServiceInterface.REQUEST_RESULT_REASON_TASK_DISABLED;
					} else {
						//Task not found
						result=TaServiceInterface.REQUEST_RESULT_REASON_TASK_NOT_FOUND;
					}
				} else {
					//Task inactive
					result=TaServiceInterface.REQUEST_RESULT_REASON_TASK_INACTIVE;
				}
			} else {
				//Task was queued
				result=TaServiceInterface.REQUEST_RESULT_REASON_TASK_ACTIVE;
			}
		} else {
			//Task was active
			result=TaServiceInterface.REQUEST_RESULT_REASON_TASK_ACTIVE;
		}
		return result;
	};

	final static private TaskListItem getTaskListItem(TaskManagerParms taskMgrParms,
			final EnvironmentParms envParms, CommonUtilities util, final Service svcInstance, TaInterfaceParms taip,
			ArrayList<TaskListItem> timerEventTaskList, ArrayList<TaskListItem> builtinEventTaskList, ArrayList<TaskListItem> taskEventTaskList) {
		TaskListItem tli=null;
		if (findTaskFromEventTaskList(timerEventTaskList, taip.request_group_name, taip.request_task_name)!=null) {
			tli=findTaskFromEventTaskList(timerEventTaskList, taip.request_group_name, taip.request_task_name);
		} else if (findTaskFromEventTaskList(builtinEventTaskList, taip.request_group_name, taip.request_task_name)!=null){
			tli=findTaskFromEventTaskList(builtinEventTaskList, taip.request_group_name, taip.request_task_name);
		} else if(findTaskFromEventTaskList(taskEventTaskList, taip.request_group_name, taip.request_task_name)!=null) {
			tli=findTaskFromEventTaskList(taskEventTaskList, taip.request_group_name, taip.request_task_name);
		}
		return tli;
	};
	
	final static private void processSysInfo(TaskManagerParms taskMgrParms,
			final EnvironmentParms envParms, CommonUtilities util, final Service svcInstance, TaInterfaceParms taip) {
		if (taip.request_cmd_sub_type.equals(TaServiceInterface.REQUEST_SYS_INFO_GET) ) {
			Intent in=new Intent();
			taip.reply_result_success=true;
			taip.reply_result_status_code=0;
			taip.availavility_sys_info=true;
			taip.airplane_mode_on=envParms.airplane_mode_on==1?true:false;
			taip.battery_charging=envParms.batteryPowerSource.equals(CURRENT_POWER_SOURCE_AC)?true:false;
			taip.battery_level=envParms.batteryLevel;
			taip.bluetooth_active=envParms.bluetoothIsActive;
			taip.bluetooth_available=envParms.bluetoothIsAvailable;
			taip.bluetooth_device_name=envParms.blutoothConnectedDeviceName;
			taip.bluetooth_device_addr=envParms.blutoothConnectedDeviceAddr;
			taip.ringer_mode_normal=envParms.currentRingerMode==AudioManager.RINGER_MODE_NORMAL?true:false;
			taip.ringer_mode_silent=envParms.currentRingerMode==AudioManager.RINGER_MODE_SILENT?true:false;
			taip.ringer_mode_vibrate=envParms.currentRingerMode==AudioManager.RINGER_MODE_VIBRATE?true:false;
			taip.light_sensor_active=envParms.lightSensorActive;
			taip.light_sensor_available=envParms.lightSensorAvailable;
			taip.light_sensor_value=envParms.lightSensorValue;
			taip.mobile_network_connected=envParms.mobileNetworkIsConnected;
			taip.proximity_sensor_active=envParms.proximitySensorActive;
			taip.proximity_sensor_available=envParms.proximitySensorAvailable;
			taip.proximity_sensor_detected=envParms.proximitySensorValue==0?true:false;
			taip.screen_locked=envParms.screenIsLocked;
			taip.telephony_available=envParms.telephonyIsAvailable;
			taip.telephony_state_idle=envParms.telephonyStatus==TelephonyManager.CALL_STATE_IDLE?true:false;
			taip.telephony_state_offhook=envParms.telephonyStatus==TelephonyManager.CALL_STATE_OFFHOOK?true:false;
			taip.telephony_state_ringing=envParms.telephonyStatus==TelephonyManager.CALL_STATE_RINGING?true:false;
			taip.wifi_active=envParms.wifiIsActive;
			taip.wifi_ssid_name=envParms.wifiConnectedSsidName;
			taip.wifi_ssid_addr=envParms.wifiConnectedSsidAddr;
			TaServiceInterface.writeReply(taip, in);
			TaServiceInterface.replyExternalApplication(svcInstance, taip, in);
		} else {
			taip.reply_result_success=false;
			taip.reply_result_status_code=TaServiceInterface.REQUEST_RESULT_REASON_UNKNOWN_COMMAND_SUB_TYPE;
			Intent r_in=new Intent();
			TaServiceInterface.writeReply(taip, r_in);
			TaServiceInterface.replyExternalApplication(svcInstance, taip, r_in);
		}
	};
	
	final static private TaskListItem findTaskFromEventTaskList(ArrayList<TaskListItem> task_list, String grp, String task) {
		TaskListItem tli=null;
    	for (int i=0;i<task_list.size();i++) {
			tli=task_list.get(i);
			if (tli.group_name.equals(grp) && tli.task_name.equals(task)) {
				break;
			} else tli=null;
    	}
    	return tli;
	};
}

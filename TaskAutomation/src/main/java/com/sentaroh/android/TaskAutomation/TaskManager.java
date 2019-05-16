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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Handler;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;

import bsh.EvalError;
import bsh.Interpreter;

import com.sentaroh.android.Utilities.StringUtil;
import com.sentaroh.android.Utilities.ThreadCtrl;

//    @SuppressWarnings("unused") 
	final public class TaskManager {
		
		final static public boolean isAcqWakeLockRequired(EnvironmentParms envParms, GlobalParameters gp) {
			boolean result=false;
			if (gp.settingWakeLockOption.equals(WAKE_LOCK_OPTION_ALWAYS)) {
				result=true;
			} else if (gp.settingWakeLockOption.equals(WAKE_LOCK_OPTION_SYSTEM)) {
		    	if (envParms.lightSensorActive || envParms.proximitySensorActive) {
		    		if (envParms.airplane_mode_on==1 && envParms.telephonyIsAvailable) {
		    			result=true;
		    		} else if (!envParms.telephonyIsAvailable) {
		    			result=true;
		    		}
				}
			} else {
				if ((gp.settingWakeLockLightSensor && envParms.lightSensorActive) || 
						(gp.settingWakeLockProximitySensor && envParms.proximitySensorActive)) {
					result=true;
				}
			}
			return result;
		}
		
//    	private static final int THREAD_POOL_SIZE_M1=THREAD_POOL_SIZE-1;
    	final static private void acqRWLock(EnvironmentParms envParms, 
    			CommonUtilities util, 
    			ReentrantReadWriteLock lock,
    			String id, int l_mode, GlobalParameters gp) {
    		boolean l_r=false;
//    		Thread.dumpStack();
    		String l_mode_string=id+" Write";
    		if (l_mode==LOCK_MODE_READ) l_mode_string=id+" Read";
    		
    		if (l_mode==LOCK_MODE_READ) l_r=lock.readLock().tryLock();
    		else l_r=lock.writeLock().tryLock();
    		
			if (l_r) {
	    		if (gp.settingDebugLevel>=3) {
	    			util.addDebugMsg(3, "I", l_mode_string," Lock acquired, Thread name=",Thread.currentThread().getName());
	    		}
			} else {
				long b_time=System.currentTimeMillis();
	    		if (gp.settingDebugLevel>=2) {
	    			util.addDebugMsg(2, "I", l_mode_string," Lock wait detected, Thread name=",Thread.currentThread().getName()+
	    					", Lock="+lock.toString());
	    		}
	    		
	    		if (l_mode==LOCK_MODE_READ) lock.readLock().lock();
	    		else lock.writeLock().lock();
	    		
	    		if (gp.settingDebugLevel>=2) {
	    			util.addDebugMsg(2, "I", l_mode_string," Lock wait time="+(System.currentTimeMillis()-b_time)+"(ms), Thread name=", Thread.currentThread().getName());
	    		}
			}
    	};
    	final static private void relRWLock(EnvironmentParms envParms, 
    			CommonUtilities util, 
    			ReentrantReadWriteLock lock,
    			String id, int l_mode, GlobalParameters gp) {
//    		Thread.dumpStack();
    		String l_mode_string=id+" Write";
    		if (l_mode==LOCK_MODE_READ) l_mode_string=id+" Read";
    		if (l_mode==LOCK_MODE_READ) lock.readLock().unlock();
    		else lock.writeLock().unlock();
    		if (gp.settingDebugLevel>=3) {
    			util.addDebugMsg(3, "I", l_mode_string," Lock released, Thread name=",Thread.currentThread().getName());
    		}
    	};
    	
    	final static int LOCK_ID_EVENT_TASK_LIST=1;
    	final static int LOCK_ID_TIMER_TASK_LIST=2;
    	final static int LOCK_ID_PROFILE_LIST=3;
    	final static int LOCK_ID_TASK_CONTROL=4;
    	final static int LOCK_ID_TASK_HISTORY=5;
    	final static int LOCK_MODE_READ=1;
    	final static int LOCK_MODE_WRITE=2;
    	final static public void acqLock(int l_id, int l_mode,
    			EnvironmentParms envParms,TaskManagerParms taskMgrParms, 
    			CommonUtilities util, GlobalParameters gp) {
//			Log.v("","acq id="+l_id+", mode="+l_mode);
    		switch (l_id) {
				case LOCK_ID_EVENT_TASK_LIST:
	   				acqRWLock(envParms,util, taskMgrParms.lockEventTaskListRW, "EventTaskList",l_mode, gp);
	   				return;
				case LOCK_ID_TIMER_TASK_LIST:
					acqRWLock(envParms,util, taskMgrParms.lockTimerTaskListRW, "TimerTaskList",l_mode, gp);
					return;
				case LOCK_ID_PROFILE_LIST:
					acqRWLock(envParms,util, taskMgrParms.lockProfileListRW, "ProfileList",l_mode, gp);
					return;
				case LOCK_ID_TASK_CONTROL:
					acqRWLock(envParms,util, taskMgrParms.lockTaskControlRW, "TaskControl",l_mode, gp);
					return;
				case LOCK_ID_TASK_HISTORY:
					acqRWLock(envParms,util, taskMgrParms.lockTaskHistoryRW, "TaskHistory",l_mode, gp);
					return;
				default:
					return;
    		}
    	};
    	
    	final static public boolean isLockHeld(int l_id, int l_mode,
    			EnvironmentParms envParms,TaskManagerParms taskMgrParms, 
    			CommonUtilities util, GlobalParameters gp) {
//			Log.v("","acq id="+l_id+", mode="+l_mode);
    		int hc=0;
    		switch (l_id) {
				case LOCK_ID_EVENT_TASK_LIST:
					if (l_mode==LOCK_MODE_READ) hc=taskMgrParms.lockEventTaskListRW.getReadHoldCount();
					else hc=taskMgrParms.lockEventTaskListRW.getWriteHoldCount();
					break;
				case LOCK_ID_TIMER_TASK_LIST:
					if (l_mode==LOCK_MODE_READ) hc=taskMgrParms.lockTimerTaskListRW.getReadHoldCount();
					else hc=taskMgrParms.lockTimerTaskListRW.getWriteHoldCount();
					break;
				case LOCK_ID_PROFILE_LIST:
					if (l_mode==LOCK_MODE_READ) hc=taskMgrParms.lockProfileListRW.getReadHoldCount();
					else hc=taskMgrParms.lockProfileListRW.getWriteHoldCount();
					break;
				case LOCK_ID_TASK_CONTROL:
					if (l_mode==LOCK_MODE_READ) hc=taskMgrParms.lockTaskControlRW.getReadHoldCount();
					else hc=taskMgrParms.lockTaskControlRW.getWriteHoldCount();
					break;
				case LOCK_ID_TASK_HISTORY:
					if (l_mode==LOCK_MODE_READ) hc=taskMgrParms.lockTaskHistoryRW.getReadHoldCount();
					else hc=taskMgrParms.lockTaskHistoryRW.getWriteHoldCount();
					break;
				default:
					break;
    		}
			return hc==0?false:true;
    	};
    	
    	
    	final static public void relLock(int l_id, int l_mode,
    			EnvironmentParms envParms,TaskManagerParms taskMgrParms, 
    			CommonUtilities util, GlobalParameters gp) {
//    		Log.v("","rel id="+l_id+", mode="+l_mode);
    		switch (l_id) {
				case LOCK_ID_EVENT_TASK_LIST:
					relRWLock(envParms,util, taskMgrParms.lockEventTaskListRW, "EventTaskList",l_mode, gp);
	   				return;
				case LOCK_ID_TIMER_TASK_LIST:
					relRWLock(envParms,util, taskMgrParms.lockTimerTaskListRW, "TimerTaskList",l_mode, gp);
					return;
				case LOCK_ID_PROFILE_LIST:
					relRWLock(envParms,util, taskMgrParms.lockProfileListRW, "ProfileList",l_mode, gp);
					return;
				case LOCK_ID_TASK_CONTROL:
					relRWLock(envParms,util, taskMgrParms.lockTaskControlRW, "TaskControl",l_mode, gp);
					return;
				case LOCK_ID_TASK_HISTORY:
					relRWLock(envParms,util, taskMgrParms.lockTaskHistoryRW, "TaskHistory",l_mode, gp);
					return;
				default:
					return;
    		}
    	};
    	
    	final static public void notifyToEventList(
    			TaskManagerParms taskMgrParms, ArrayList<ThreadCtrl> notify_list,
    			int extra_id, GlobalParameters gp) {
    		synchronized(notify_list) {
    			int lsz=notify_list.size();
    			ThreadCtrl tc;
        		for (int i=0;i<lsz;i++) {
        			tc=notify_list.get(i);
        			synchronized(tc) {
            			tc.setExtraDataInt(extra_id);
            			tc.notifyAll();
        			}
        		}
    		}
    	};
    	
    	final static public void addNotifyEventListItem(ArrayList<ThreadCtrl>list, ThreadCtrl ale) {
    		synchronized(list) {
    			list.add(ale);
    		}
    	};
    	
    	final static public void removeNotifyEventListItem(ArrayList<ThreadCtrl>list, ThreadCtrl rle) {
    		synchronized(list) {
    			list.remove(rle);
    		}
    	};

       	final static public void initNotification(TaskManagerParms taskMgrParms, EnvironmentParms envParms, GlobalParameters gp) {
       		String appl_ver="";
    		try {
    		    String packegeName = taskMgrParms.context.getPackageName();
    		    PackageInfo packageInfo = taskMgrParms.context.getPackageManager().getPackageInfo(packegeName, PackageManager.GET_META_DATA);
    		    appl_ver=packageInfo.versionName;
    		} catch (NameNotFoundException e) {
    		}

    		taskMgrParms.main_notification_msgs_appname=taskMgrParms.context.getString(R.string.app_name);
    		taskMgrParms.main_notification_msgs_svc_active_task=
    				taskMgrParms.main_notification_msgs_appname+" "+appl_ver+"   "+"T=";
//       				"   "+taskMgrParms.context.getString(R.string.msgs_svc_active_task);
    		taskMgrParms.main_notification_msgs_main_task_scheduler_not_running=
    				taskMgrParms.context.getString(R.string.msgs_main_task_scheduler_not_running);
    		taskMgrParms.mainNotificationManager = (NotificationManager)
    				taskMgrParms.context.getSystemService(Context.NOTIFICATION_SERVICE);
    		taskMgrParms.mainNotificationManager.cancelAll();
       	    
    		Intent in=new Intent(taskMgrParms.context.getApplicationContext(), SchedulerService.class);
    		in.setAction(BROADCAST_START_ACTIVITY_TASK_STATUS);
    		taskMgrParms.mainNotificationPi= 
       				PendingIntent.getService(taskMgrParms.context, 0, in,PendingIntent.FLAG_UPDATE_CURRENT);
    		buildNotification(taskMgrParms, envParms, gp);
       	};

        final public static String NOTIFICATION_CHANNEL_DEFAULT="Default";
       	final static public void buildNotification(TaskManagerParms taskMgrParms, EnvironmentParms envParms, GlobalParameters gp) {
       		int icon_id=0;
       		if (gp.settingEnableScheduler) icon_id=R.drawable.main_icon_small;
       		else icon_id=R.drawable.main_icon_stop;
    		taskMgrParms.mainNotificationBuilder = new NotificationCompat.Builder(taskMgrParms.context);
		   	taskMgrParms.mainNotificationBuilder.setContentIntent(taskMgrParms.mainNotificationPi)
//		   		.setTicker("Ticker")
			   	.setOngoing(true)
			   	.setAutoCancel(false)
			   	.setSmallIcon(icon_id)
//			    .setContentTitle("ContentTitle")
//			    .setContentText("ContentText")
//		    	.setSubText("subtext")
//		    	.setLargeIcon(largeIcon)
//			    .setWhen(System.currentTimeMillis())
//			    .addAction(action_icon, action_title, action_pi)
			    ;

            if (Build.VERSION.SDK_INT>=26) {
                taskMgrParms.mainNotificationBuilder.setChannelId(NOTIFICATION_CHANNEL_DEFAULT);
            }

            taskMgrParms.mainNotification=taskMgrParms.mainNotificationBuilder.build();

            if (Build.VERSION.SDK_INT>=26) {
                NotificationChannel def_ch = new NotificationChannel(
                        NOTIFICATION_CHANNEL_DEFAULT,
                        NOTIFICATION_CHANNEL_DEFAULT,
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                def_ch.enableLights(false);
                def_ch.setSound(null,null);
                def_ch.enableVibration(false);
                def_ch.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                taskMgrParms.mainNotificationManager.deleteNotificationChannel(NOTIFICATION_CHANNEL_DEFAULT);
                taskMgrParms.mainNotificationManager.createNotificationChannel(def_ch);
            }

        };

       	final static public void showNotification(TaskManagerParms taskMgrParms, EnvironmentParms envParms,
       			CommonUtilities util, GlobalParameters gp) {
       		if (gp.settingEnableScheduler) {
              	synchronized(taskMgrParms.mainNotification) {
    	       		StringBuilder title=new StringBuilder(256)
	    	       		.append(taskMgrParms.main_notification_msgs_svc_active_task)
	    	    		.append(envParms.statsActiveTaskCountString);
		       		String basic_info="";
		    		if (envParms.nextScheduleTime!=0) {
			       		basic_info=String.format(taskMgrParms.svcMsgs.msgs_svc_notification_info_next_schedule,
			       				envParms.nextScheduleTimeString)+", "+
		       				String.format(taskMgrParms.svcMsgs.msgs_svc_notification_info_build_time,
		       						envParms.taskListBuildTimeString);
		    		} else {
			       		basic_info=String.format(taskMgrParms.svcMsgs.msgs_svc_notification_info_build_time,
		       						envParms.taskListBuildTimeString);
		    		}
    			   	taskMgrParms.mainNotificationBuilder
//			   	    	.setContentTitle(title)
			   	    	.setContentText(basic_info);
            		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
//            		inboxStyle.setBigContentTitle(title.toString());
            		inboxStyle.addLine(basic_info);

//            		inboxStyle.addLine(taskMgrParms.svcMsgs.msgs_svc_notification_info_battery_title+" "+
//            				envParms.batteryLevel+"%"+" "+envParms.batteryChargeStatusString);
            		if (gp.settingDebugLevel>0) {
                		if (TaskManager.isAcqWakeLockRequired(envParms, gp)) inboxStyle.addLine(taskMgrParms.svcMsgs.msgs_svc_notification_info_wake_lock_status_hold);
                		else inboxStyle.addLine(taskMgrParms.svcMsgs.msgs_svc_notification_info_wake_lock_status_not_hold);
            		}

            		taskMgrParms.mainNotificationBuilder.setStyle(inboxStyle);
    			   	
				    taskMgrParms.mainNotification=taskMgrParms.mainNotificationBuilder.build();
		    		taskMgrParms.mainNotificationManager.notify(R.string.app_name,taskMgrParms.mainNotification);
           		}
       		} else {
              	synchronized(taskMgrParms.mainNotification) {
    			   	taskMgrParms.mainNotificationBuilder.setContentIntent(taskMgrParms.mainNotificationPi)
    				    .setContentTitle(taskMgrParms.main_notification_msgs_appname)
    				    .setContentText(taskMgrParms.main_notification_msgs_main_task_scheduler_not_running)
//    				    .setWhen(System.currentTimeMillis())
    				    ;
            		if (Build.VERSION.SDK_INT>=21) {
                		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle(taskMgrParms.mainNotificationBuilder);
                		inboxStyle.setBigContentTitle(taskMgrParms.main_notification_msgs_appname);
//                		inboxStyle.addLine(taskMgrParms.svcMsgs.msgs_svc_notification_info_battery_title+" "+
//                				envParms.batteryLevel+"%"+" "+envParms.batteryChargeStatusString);
                		inboxStyle.setSummaryText(taskMgrParms.main_notification_msgs_main_task_scheduler_not_running);
            		}

				    taskMgrParms.mainNotification=taskMgrParms.mainNotificationBuilder.build();
		    		taskMgrParms.mainNotificationManager.notify(R.string.app_name,taskMgrParms.mainNotification);
           		}
       		}
        };
        
       	final static public void cancelNotification(TaskManagerParms taskMgrParms) {
       		taskMgrParms.mainNotificationManager.cancel(R.string.app_name);
       	};
    	
		final static public void showErrorNotificationMessage(TaskManagerParms taskMgrParms, String msg) {
    		Intent in=new Intent(taskMgrParms.context.getApplicationContext(), SchedulerService.class);
    		in.setAction(BROADCAST_START_ACTIVITY_TASK_STATUS);
       	    in.putExtra("Home","History");
    		PendingIntent pi = PendingIntent.getService(taskMgrParms.context, 0, in,PendingIntent.FLAG_UPDATE_CURRENT);
    		
        	NotificationCompat.Builder nb=new NotificationCompat.Builder(taskMgrParms.context);
        	nb.setAutoCancel(true)
	        	.setSmallIcon(R.drawable.error)
	        	.setContentTitle(taskMgrParms.context.getString(R.string.notification_error))
	        	.setContentText(msg)
	        	.setLights(0xffff0000, 900, 100)
	        	.setContentIntent(pi)
	        	.setWhen(System.currentTimeMillis());
        	taskMgrParms.mainNotificationManager.cancel("ERROR",R.string.notification_error);
        	
    		Notification nf=nb.build();
    		taskMgrParms.mainNotificationManager.notify("ERROR",R.string.notification_error, nf);
        };

    	static final public void callBackToActivity(TaskManagerParms taskMgrParms,
        		EnvironmentParms envParms, CommonUtilities util,
    			String resp_time, String resp_id, 
    			String grp, String task, String action, String shell_cmd, String dlg_id, int resp_cd, 
    			String msg, GlobalParameters gp) {
    		if (gp.settingDebugLevel>=2) util.addDebugMsg(2, "I", "callBackToActivity entered, resp=", resp_id,
    				", task=",task,", action=",action,", msg=",msg);
    		synchronized(taskMgrParms.callBackList) {
    			int on = taskMgrParms.callBackList.beginBroadcast();
    			if (on!=0) {
        			ISchedulerCallback isv=null;
        			for(int i = 0; i < on; i++){
        				try {
        					isv=taskMgrParms.callBackList.getBroadcastItem(i);
        					if (isv!=null && envParms!=null)
        						isv.notifyToClient(resp_time, resp_id, grp, task, action, 
        							shell_cmd, dlg_id, envParms.statsActiveTaskCount, resp_cd, msg);
        				} catch (RemoteException e) {
        					e.printStackTrace();
        					util.addLogMsg("E", "callBackToActivity error, num=",String.valueOf(on),
        							"\n",e.toString());
        				}
        			}
        			taskMgrParms.callBackList.finishBroadcast();
    			}
    		}
    	};
    	
    	static final public void showStopSoundPlayBackNotification(TaskManagerParms taskMgrParms,
        		EnvironmentParms envParms, CommonUtilities util, TaskResponse tr,
        		String group, String task, String action,
    			String dialog_id, String msg_type, String msg_text) {
    		
    		Intent in=new Intent(taskMgrParms.context.getApplicationContext(),SchedulerService.class);
    		in.setAction(CANCEL_ALL_SOUND_PLAYBACK_STOP_REQUEST);
    		PendingIntent pi= 
       				PendingIntent.getService(taskMgrParms.context, 0, in,PendingIntent.FLAG_UPDATE_CURRENT);
    		
    		taskMgrParms.soundPlayBackTaskList.add(tr);
    		
    		NotificationCompat.Builder nb = new NotificationCompat.Builder(taskMgrParms.context);
		   	nb.setPriority(Notification.PRIORITY_MAX).setCategory(NotificationCompat.CATEGORY_ALARM)
//		   		.setTicker("Ticker")
			   	.setOngoing(true)
			   	.setAutoCancel(false)
			   	.setSmallIcon(R.drawable.error)
			   	.setContentTitle(
			   			taskMgrParms.context.getString(R.string.msgs_thread_task_sound_playback_notification_title))
			   	.setContentText(taskMgrParms.context.getString(R.string.msgs_thread_task_sound_playback_notification_text)+
			   			"\n"+"group="+group+", task="+task)
//		    	.setSubText("subtext")
//		    	.setLargeIcon(largeIcon)
			    .setWhen(System.currentTimeMillis())
			    .setContentIntent(pi)
			    .addAction(R.drawable.cancel_48, null, pi)
			    ;
    		
//    		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle(nb);
//    		inboxStyle.setBigContentTitle("Title");
//    		inboxStyle.addLine(msg_text);
    		Notification notify=nb.build();
    		taskMgrParms.mainNotificationManager.notify(R.string.notification_plyback_close,notify);
    	};

    	static final public void closeStopSoundPlayBackNotification(TaskManagerParms taskMgrParms,
        		EnvironmentParms envParms, CommonUtilities util, TaskResponse tr) {
    		taskMgrParms.mainNotificationManager.cancel(R.string.notification_plyback_close);
    	};

    	static final public void showMessageDialog(TaskManagerParms taskMgrParms,
        		EnvironmentParms envParms, CommonUtilities util, 
        		String group, String task, String action,
    			String dialog_id, String msg_type, String msg_text) {
    		Intent in_b=
    				new Intent(taskMgrParms.context.getApplicationContext(),ActivityMessage.class);
//    		in_b.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
    		in_b.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|
    				Intent.FLAG_ACTIVITY_MULTIPLE_TASK|
    				Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
    		in_b.putExtra(MESSAGE_DIALOG_MESSAGE_KEY_GROUP, group);
    		in_b.putExtra(MESSAGE_DIALOG_MESSAGE_KEY_TASK, task);
    		in_b.putExtra(MESSAGE_DIALOG_MESSAGE_KEY_ACTION, action);
    		in_b.putExtra(MESSAGE_DIALOG_MESSAGE_KEY_DIALOG_ID, dialog_id);
    		in_b.putExtra(MESSAGE_DIALOG_MESSAGE_KEY_TYPE, msg_type);
    		in_b.putExtra(MESSAGE_DIALOG_MESSAGE_KEY_TEXT, msg_text);

    		taskMgrParms.context.startActivity(in_b);
    	};

    	static final public void closeMessageDialog(TaskManagerParms taskMgrParms,
        		EnvironmentParms envParms, CommonUtilities util,
    			TaskResponse tr, GlobalParameters gp) {
        	callBackToActivity(taskMgrParms,envParms,util,
        			tr.resp_time,NTFY_TO_ACTV_CLOSE_DIALOG,
        			tr.active_group_name, tr.active_task_name, tr.active_action_name,
        			null, tr.active_dialog_id, tr.resp_code, null, gp);
    	};
    	static final public void buildTaskExecThreadPool(final EnvironmentParms envParms, 
    			final TaskManagerParms taskMgrParms, final CommonUtilities util, GlobalParameters gp) {
    		if (taskMgrParms.taskExecutorThreadPool!=null) 
    			removeTaskExecThreadPool(envParms,taskMgrParms,util);
    		SynchronousQueue <Runnable> slq=new SynchronousQueue <Runnable>();
    		RejectedExecutionHandler rh=new RejectedExecutionHandler() {
				@Override
				public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
					util.addDebugMsg(1,"W", "Task executor reject handler entered.");
					startTaskOutsideThreadPool(taskMgrParms,envParms,util,(TaskExecutor)r);
				}
    		};
     	    taskMgrParms.taskExecutorThreadPool =new ThreadPoolExecutor(
     	    		gp.settingTaskExecThreadPoolCount+2, 
     	    		gp.settingTaskExecThreadPoolCount+2,
					10, TimeUnit.SECONDS, slq, rh);
     	    for (int i=0;i<gp.settingTaskExecThreadPoolCount+2;i++) {
     	    	final int num=i+1;
     	    	Runnable rt=new Runnable() {
					@Override
					public void run() {
							Thread.currentThread().setPriority(THREAD_PRIORITY_TASK_EXEC);
							Thread.currentThread().setName("TaskExec-"+num);
					}
     	    	};
     	    	taskMgrParms.taskExecutorThreadPool.execute(rt);
     	    }
     	    taskMgrParms.taskExecutorThreadPool.prestartAllCoreThreads();
     	    util.addDebugMsg(2,"I", "Task executor thread pool was created.");
    	};

    	static final public void removeTaskExecThreadPool(EnvironmentParms envParms, 
        		TaskManagerParms taskMgrParms, CommonUtilities util) {
    		if (taskMgrParms.taskExecutorThreadPool!=null) {
        		try {
        			taskMgrParms.taskExecutorThreadPool.shutdown();
    				boolean rs=taskMgrParms.taskExecutorThreadPool.awaitTermination(1000, TimeUnit.MILLISECONDS);
            		if (!rs) taskMgrParms.taskExecutorThreadPool.shutdownNow();
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
        		util.addDebugMsg(2,"i","Task executor thread pool was removed");
    			taskMgrParms.taskExecutorThreadPool=null;
    		}
    	};
    	
    	static final public void buildTaskCtrlThreadPool(final EnvironmentParms envParms, 
    			final TaskManagerParms taskMgrParms, final CommonUtilities util) {
    		if (taskMgrParms.normalTaskControlThreadPool!=null) 
    			removeTaskCtrlThreadPool(envParms,taskMgrParms,util);
    		SynchronousQueue <Runnable> slq=new SynchronousQueue <Runnable>();
    		RejectedExecutionHandler rh=new RejectedExecutionHandler() {
				@Override
				public void rejectedExecution(final Runnable r, ThreadPoolExecutor executor) {
					util.addDebugMsg(1,"W", "Task control reject handler entered.");
					envParms.statsUseOutsideThreadPoolCountTaskCtrl++;
					Thread th=new Thread() {
						@Override
						public void run() {
							r.run();
						}
					};
					th.setPriority(THREAD_PRIORITY_TASK_CTRL);
					th.start();
				}
    		};
     	    taskMgrParms.normalTaskControlThreadPool =new ThreadPoolExecutor(
     	    		TASK_CTRL_THREAD_POOL_COUNT, TASK_CTRL_THREAD_POOL_COUNT,
					10, TimeUnit.SECONDS, slq, rh);
     	    for (int i=0;i<TASK_CTRL_THREAD_POOL_COUNT;i++) {
     	    	final int num=i+1;
     	    	Runnable rt=new Runnable() {
					@Override
					public void run() {
							Thread.currentThread().setPriority(THREAD_PRIORITY_TASK_CTRL);
							Thread.currentThread().setName("TaskCtrl-"+num);
					}
     	    	};
     	    	taskMgrParms.normalTaskControlThreadPool.execute(rt);
     	    }
     	    taskMgrParms.normalTaskControlThreadPool.prestartAllCoreThreads();
     	    util.addDebugMsg(2,"I", "Task control thread pool was created.");
    	};

    	static final public void removeTaskCtrlThreadPool(EnvironmentParms envParms, 
        		TaskManagerParms taskMgrParms, CommonUtilities util) {
    		if (taskMgrParms.normalTaskControlThreadPool!=null) {
        		try {
        			taskMgrParms.normalTaskControlThreadPool.shutdown();
    				boolean rs=taskMgrParms.normalTaskControlThreadPool.awaitTermination(1000, TimeUnit.MILLISECONDS);
            		if (!rs) taskMgrParms.normalTaskControlThreadPool.shutdownNow();
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
        		util.addDebugMsg(2,"i","Task control thread pool was removed");
    			taskMgrParms.normalTaskControlThreadPool=null;
    		}
    	};

    	static final public void buildHighTaskCtrlThreadPool(final EnvironmentParms envParms, 
    			final TaskManagerParms taskMgrParms, final CommonUtilities util) {
    		if (taskMgrParms.highTaskControlThreadPool!=null) 
    			removeHighTaskCtrlThreadPool(envParms,taskMgrParms,util);
    		SynchronousQueue <Runnable> slq=new SynchronousQueue <Runnable>();
    		RejectedExecutionHandler rh=new RejectedExecutionHandler() {
				@Override
				public void rejectedExecution(final Runnable r, ThreadPoolExecutor executor) {
					util.addDebugMsg(1,"W", "High task control reject handler entered.");
					envParms.statsUseOutsideThreadPoolCountTaskCtrl++;
					Thread th=new Thread() {
						@Override
						public void run() {
							r.run();
						}
					};
					th.setPriority(THREAD_PRIORITY_TASK_CTRL_HIGH);
					th.start();
				}
    		};
     	    taskMgrParms.highTaskControlThreadPool =new ThreadPoolExecutor(
     	    		HIGH_TASK_CTRL_THREAD_POOL_COUNT, HIGH_TASK_CTRL_THREAD_POOL_COUNT,
					10, TimeUnit.SECONDS, slq, rh);
     	    for (int i=0;i<HIGH_TASK_CTRL_THREAD_POOL_COUNT;i++) {
     	    	final int num=i+1;
     	    	Runnable rt=new Runnable() {
					@Override
					public void run() {
							Thread.currentThread().setPriority(THREAD_PRIORITY_TASK_CTRL_HIGH);
							Thread.currentThread().setName("TaskCtrlHigh-"+num);
					}
     	    	};
     	    	taskMgrParms.highTaskControlThreadPool.execute(rt);
     	    }
     	    taskMgrParms.highTaskControlThreadPool.prestartAllCoreThreads();
     	    util.addDebugMsg(2,"I", "High task control thread pool was created.");
    	};

    	static final public void removeHighTaskCtrlThreadPool(EnvironmentParms envParms, 
        		TaskManagerParms taskMgrParms, CommonUtilities util) {
    		if (taskMgrParms.highTaskControlThreadPool!=null) {
        		try {
        			taskMgrParms.highTaskControlThreadPool.shutdown();
    				boolean rs=taskMgrParms.highTaskControlThreadPool.awaitTermination(1000, TimeUnit.MILLISECONDS);
            		if (!rs) taskMgrParms.highTaskControlThreadPool.shutdownNow();
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
        		util.addDebugMsg(2,"i","Task control thread pool high was removed");
    			taskMgrParms.highTaskControlThreadPool=null;
    		}
    	};

    	final static public BshExecEnvListItem acqBshExecEnvItem(final TaskManagerParms taskMgrParms,
    			final EnvironmentParms envParms, final CommonUtilities util, final TaskResponse tr,
    			final ActionResponse ar, final TaskActionItem tai, GlobalParameters gp) {
    		BshExecEnvListItem result=null;
    		synchronized(taskMgrParms.bshExecEnvList) {
    			if (taskMgrParms.bshExecEnvCached) {
        			for(int i=0;i<taskMgrParms.bshExecEnvList.size();i++) {
        				BshExecEnvListItem li=taskMgrParms.bshExecEnvList.get(i);
        				if (!li.isUsed) {
        					result=li;
        					li.isUsed=true;
        					li.driver.setBeanShellMethod(taskMgrParms, envParms, util, tr, ar, tai);
        					if (gp.settingDebugLevel>=2) 
        						util.addDebugMsg(2,"i","Bsh execution environment assigned, id="+li.driverId+", pos="+i);
        					break;
        				}
        			};
    			}
    			if (result==null) {
    				BshExecEnvListItem nli=createBshExecEnvItem(taskMgrParms,envParms,util,tr,ar,tai, gp);
    				nli.isUsed=true;
    				if (taskMgrParms.bshExecEnvCached) taskMgrParms.bshExecEnvList.add(nli);
    				result=nli;
    				if (gp.settingDebugLevel>=2) 
    					util.addDebugMsg(2,"i","New Bsh execution environment created and assigned, id="+nli.driverId);
    			}
    		}
    		return result;
    	};

    	final static private void deleteBshExecEnvListItem(final TaskManagerParms taskMgrParms,
    			final EnvironmentParms envParms, final CommonUtilities util, final BshExecEnvListItem bsli) {
			try {
				bsli.isUsed=false;
				bsli.interpreter.unset("TaCmd");
				bsli.interpreter.eval("clear();");
				bsli.interpreter=null;
				bsli.driver.setBeanShellMethod(null, null, null, null, null, null);
				bsli.driver=null;
			} catch (EvalError e) {
				e.printStackTrace();
				bsli.interpreter=null;
				bsli.driver.setBeanShellMethod(null, null, null, null, null, null);
				bsli.driver=null;
			}
    	};
    	
    	final static public void buildBshEnvironmentList(final TaskManagerParms taskMgrParms,
    			final EnvironmentParms envParms, final CommonUtilities util, GlobalParameters gp) {
    		if (taskMgrParms.bshExecEnvList.size()>0) {
    			for(int i=0;i<taskMgrParms.bshExecEnvList.size();i++) {
					deleteBshExecEnvListItem(taskMgrParms,envParms, util,
			    			taskMgrParms.bshExecEnvList.get(i));
    			}
        		util.addDebugMsg(2,"i","Bsh execution environment list was removed");
    		}
    		taskMgrParms.bshExecEnvList=new ArrayList<BshExecEnvListItem>();
    		if (taskMgrParms.bshExecEnvCached) {
        		for(int i=0;i<2;i++) {
    				BshExecEnvListItem nli=createBshExecEnvItem(null, null, null, null, null, null, gp);
    				taskMgrParms.bshExecEnvList.add(nli);
        		}
    		}
    		util.addDebugMsg(2,"i","Bsh execution environment list was created");
    	};
    	
    	final static private BshExecEnvListItem createBshExecEnvItem(
    			final TaskManagerParms taskMgrParms,
    			final EnvironmentParms envParms, final CommonUtilities util, final TaskResponse tr,
    			final ActionResponse ar, final TaskActionItem tai, GlobalParameters gp) {
    		BshExecEnvListItem nli=new BshExecEnvListItem();
			nli.driver=new BeanShellDriver(taskMgrParms,envParms,util,tr,ar,tai, gp);
			nli.interpreter=new Interpreter();
			nli.driverId=System.currentTimeMillis();
			try {
				nli.interpreter.set("TaCmd", nli.driver);
			} catch (EvalError e) {
				e.printStackTrace();
			}
    		return nli;
    	};
    	
    	final static public void relBshExecEnvItem(final TaskManagerParms taskMgrParms,
    			final EnvironmentParms envParms, final CommonUtilities util, final BshExecEnvListItem bsli, GlobalParameters gp) {
    		synchronized(taskMgrParms.bshExecEnvList) {
    			boolean found=false;
    			for(int i=0;i<taskMgrParms.bshExecEnvList.size();i++) {
    				if (taskMgrParms.bshExecEnvList.get(i).driver.equals(bsli.driver)) {
    					found=true;
						if (taskMgrParms.bshExecEnvList.size()>gp.settingMaxBshDriverCount) {
							if (gp.settingDebugLevel>=2) 
								util.addDebugMsg(2,"i","Beanshell environment was removed id="+bsli.driverId);
    						taskMgrParms.bshExecEnvList.remove(bsli);
    						deleteBshExecEnvListItem(taskMgrParms,envParms, util,bsli);
						} else {
							bsli.isUsed=false;
						}
    					break;
    				}
    			}
    			if (!found) {
					deleteBshExecEnvListItem(taskMgrParms,envParms, util,bsli);
    			}
    			if (gp.settingDebugLevel>=2) 
    				util.addDebugMsg(2,"i","Beanshell environment list count="+taskMgrParms.bshExecEnvList.size());
    		}
//    		Log.v("","bsh count="+taskMgrParms.bshInstanceList.size());
    	};


		static final public void initTaskMgrParms(EnvironmentParms envParms, 
        		TaskManagerParms taskMgrParms, Context appContext, CommonUtilities util, GlobalParameters gp) {
    		taskMgrParms.resourceCleanupTime=System.currentTimeMillis()+RESOURCE_CLEANUP_INTERVAL;
     	    taskMgrParms.activeTaskList=TaskManager.buildActiveTaskList();
     	    taskMgrParms.blockActionList=TaskManager.buildBlockActionList();
     	    taskMgrParms.taskHistoryList=TaskManager.buildTaskHistoryList(taskMgrParms);
     		taskMgrParms.taskQueueList=TaskManager.buildTaskQueueList();
     		taskMgrParms.wifiNotifyEventList=new ArrayList<ThreadCtrl>();
     		taskMgrParms.bluetoothNotifyEventList=new ArrayList<ThreadCtrl>();
     	    taskMgrParms.context=appContext;
     	    taskMgrParms.callBackList=new RemoteCallbackList<ISchedulerCallback>();
     	    taskMgrParms.teMsgs.loadString(appContext);
     	    taskMgrParms.svcHandler=new Handler();

     	    buildTaskExecThreadPool(envParms,taskMgrParms,util, gp);
     	    
     	    buildTaskCtrlThreadPool(envParms,taskMgrParms,util);
     	    
     	    buildHighTaskCtrlThreadPool(envParms,taskMgrParms,util);
     	    
     	    buildBshEnvironmentList(taskMgrParms, envParms, util, gp);
        };

        static final public LinkedList<TaskListItem> buildActiveTaskList() {
        	return new LinkedList<TaskListItem>();
        };
        static final public LinkedList<BlockActionItem> buildBlockActionList() {
        	return new LinkedList<BlockActionItem>();
        };
        static final public LinkedList<TaskListItem> buildTaskQueueList() {
        	return new LinkedList<TaskListItem>();
        };
        static final public LinkedList<TaskHistoryItem> buildTaskHistoryList(TaskManagerParms taskMgrParms) {
        	taskMgrParms.task_history_list_was_updated=true;
        	return new LinkedList<TaskHistoryItem>();
        };
    	
    	static final public void scheduleTask(TaskManagerParms taskMgrParms,
        		EnvironmentParms envParms, CommonUtilities util,TaskListItem patl, final GlobalParameters gp) {
			acqLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms, util, gp);
    		if (gp.settingDebugLevel>=2) util.addDebugMsg(2, "I","Schedule Task entered." ,
					" Event=",patl.event_name,
					", Task=",patl.task_name);
    		if (taskMgrParms.activeTaskList.size()<gp.settingMaxTaskCount) {
    			startTask(taskMgrParms,envParms,util,patl, gp);
	        	addTaskHistoryListItem(taskMgrParms,envParms,util,
	            		patl.task_start_time,patl.group_name,patl.event_name,patl.task_name,
	    				TaskHistoryItem.TASK_HISTORY_TASK_STATUS_STARTED, gp);
    		} else {
    			envParms.statsMaxTaskReacheCount++;
    			taskMgrParms.taskQueueList.add(patl);
    			String create_time=StringUtil.convDateTimeTo_HourMinSecMili(System.currentTimeMillis());
	        	addTaskHistoryListItem(taskMgrParms,envParms,util,
	            		create_time,patl.group_name,patl.event_name,patl.task_name,
	    				TaskHistoryItem.TASK_HISTORY_TASK_STATUS_QUEUED, gp);
				util.addLogMsg("W", "Reached the max concurrent task count(",
						String.valueOf(gp.settingMaxTaskCount), "), Task start was deleayed.",
    					" Group=", patl.group_name, ", Task="+patl.task_name,
    					", TaskQueue=", String.valueOf(taskMgrParms.taskQueueList.size()));
    		}
			relLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms, util, gp);
        };

        static final public void rescheduleTask(final TaskManagerParms taskMgrParms,
        		final EnvironmentParms envParms, final CommonUtilities util, final GlobalParameters gp) {
        	if (taskMgrParms.taskQueueList.size()==0) return;
        	Runnable r=new Runnable(){
        		@Override
        		public void run() {
//       			TaskListItem atli;
    				acqLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms, util, gp);
    	        	while (taskMgrParms.activeTaskList.size()<gp.settingMaxTaskCount) {
    	    			if (taskMgrParms.taskQueueList.size()!=0){
    	    				TaskListItem qtli=taskMgrParms.taskQueueList.get(0);
    	    				startTask(taskMgrParms,envParms,util,qtli, gp);
    	    				taskMgrParms.taskQueueList.remove(0);
            	        	updateTaskHistoryListItem(taskMgrParms,envParms,util,
            	        			qtli.task_start_time,qtli.group_name,qtli.event_name,qtli.task_name,
            	    				TaskHistoryItem.TASK_HISTORY_TASK_STATUS_STARTED,0,null, gp);
    	    			} else {
    	    				break;
    	    			}
    	        	}
    				relLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms, util, gp);
        		}
        	};
        	taskMgrParms.normalTaskControlThreadPool.execute(r);
//        	Thread th=new Thread() {
//        	};
//        	th.setPriority(Thread.MAX_PRIORITY);
//        	th.start();
        };

        static final public void startTask(final TaskManagerParms taskMgrParms,
        		final EnvironmentParms envParms, final CommonUtilities util,TaskListItem task_item, final GlobalParameters gp) {
        	if (gp.settingDebugLevel>=2) util.addDebugMsg(2,"I", "startTask entered, Started Event=",
        			task_item.event_name, ", Task=", task_item.task_name);
        	if (gp.settingDebugLevel>=2) {
        		boolean lh=isLockHeld(TaskManager.LOCK_ID_TASK_CONTROL,TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms, util, gp);
            	if (!lh) util.addLogMsg("E","startTask Task_Control Write lock was not held");
        	}
        	
        	TaskResponse tr=new TaskResponse();
        	tr.active_notify_event=taskMgrParms.threadReponseNotify;
        	tr.active_thread_ctrl=new ThreadCtrl();
        	tr.active_thread_ctrl.setThreadResultSuccess();
        	tr.active_thread_ctrl.setEnabled();
        	final TaskExecutor tet=new TaskExecutor(tr, taskMgrParms, task_item, envParms, gp) ;
        	addActiveTaskListItem(taskMgrParms,
            		envParms, util, task_item, tr.active_thread_ctrl, gp);
        	if (taskMgrParms.taskExecutorThreadPool.getActiveCount()<=gp.settingTaskExecThreadPoolCount)
        		taskMgrParms.taskExecutorThreadPool.execute(tet);
        	else startTaskOutsideThreadPool(taskMgrParms,envParms,util,tet);
        	
//			showNotification(taskMgrParms,envParms,util, gp);
        	if (gp.settingDebugLevel>=2) util.addDebugMsg(2,"I", "startTask exit, Current period high task count=",String.valueOf(envParms.statsHighTaskCountThisPeriod),
        			", Number of high task count=", String.valueOf(envParms.statsHighTaskCountWoMaxTask),
        			", Number of Max Tasks reached=", String.valueOf(envParms.statsMaxTaskReacheCount));
        };
        
        static final private void startTaskOutsideThreadPool(final TaskManagerParms taskMgrParms,
        		final EnvironmentParms envParms, final CommonUtilities util, final TaskExecutor tet) {
			if (envParms.statsUseOutsideThreadPoolCountTaskExec<Integer.MAX_VALUE) envParms.statsUseOutsideThreadPoolCountTaskExec++;
			else envParms.statsUseOutsideThreadPoolCountTaskExec=1;
			
			util.addLogMsg("I", "Task was started by outside the thread pool. "+tet.toSting());
			Thread th=new Thread() {
				@Override
				public void run() {
					tet.run();
				}
			};
			th.setPriority(Thread.NORM_PRIORITY-3);
			th.start();
        };
        
        static final public void cancelSpecificTask(TaskManagerParms taskMgrParms,
		        		EnvironmentParms envParms, CommonUtilities util,
		        		String grp, String task_name, GlobalParameters gp) {
        	if (gp.settingDebugLevel>=2) util.addDebugMsg(2,"I", 
        			"cancelSpecifiTask entered, cnt=", envParms.statsActiveTaskCountString,
        			", group=", grp, ", task=", task_name);
        	if (gp.settingDebugLevel>=2) {
        		boolean lh=isLockHeld(TaskManager.LOCK_ID_TASK_CONTROL,TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms, util, gp);
            	if (!lh) util.addLogMsg("E","cancelSpecificTask Task_Control Write lock was not held");
        	}
    		int atsz=taskMgrParms.activeTaskList.size();
        	if (atsz!=0) {
        		TaskListItem ati=null;
        		for (int i=0;i<atsz;i++) {
        			ati=taskMgrParms.activeTaskList.get(i);
        			if (ati.group_name.equals(grp) && 
        					ati.task_name.equals(task_name) && ati.task_ctrl_tc!=null)
        				cancelExecutedTask(util,ati.task_ctrl_tc);
        		}
        	}
        	int tqsz=taskMgrParms.taskQueueList.size();
        	if (tqsz>0) {
        		for (int i=tqsz-1;i>=0;i--) {
            		TaskListItem tqti=taskMgrParms.taskQueueList.get(i);
            		if (tqti.group_name.equals(grp) && tqti.task_name.equals(task_name)) {
            			cancelQueuedTask(taskMgrParms, envParms, util, i, 
            					tqti.group_name,tqti.event_name,tqti.task_name, gp);
            		}
        		}
        	}
            return ;
        };

        static final private void cancelQueuedTask(TaskManagerParms taskMgrParms,
        		EnvironmentParms envParms, CommonUtilities util,int del_pos,
        		String grp, String event, String task , GlobalParameters gp) {
			taskMgrParms.taskQueueList.remove(del_pos);
    		String c_time=StringUtil.convDateTimeTo_HourMinSecMili(System.currentTimeMillis());
    		updateTaskHistoryListItem(taskMgrParms,envParms,util,
    				c_time, grp, event, task,
					TaskHistoryItem.TASK_HISTORY_TASK_STATUS_ENDED,TaskResponse.RESP_CODE_CANCELLED,null, gp);
    		TaskManager.callBackToActivity(taskMgrParms,envParms,util,
					c_time,NTFY_TO_ACTV_TASK_ENDED,null, grp, task, null, null,
					TaskResponse.RESP_CODE_CANCELLED,"Cancelled", gp);
			util.addLogMsg("I", "Queued task was cancelled",", Group=", grp, ", Task=", task);
        	
        };
        
        static final public void cancelTaskByEventId(TaskManagerParms taskMgrParms,
        		EnvironmentParms envParms, CommonUtilities util, TaskResponse tr, GlobalParameters gp) {
        	if (gp.settingDebugLevel>=2) {
            	boolean lh=isLockHeld(TaskManager.LOCK_ID_TASK_CONTROL,TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms, util, gp);
            	if (!lh) util.addLogMsg("E","cancelTaskByEventId Task_Control Write lock was not held");
        	}
    		int atsz=taskMgrParms.activeTaskList.size();
    		TaskListItem ati=null;
			for (int i=0;i<atsz;i++) {
				ati=taskMgrParms.activeTaskList.get(i);
				if (ati.group_name.equals(tr.active_group_name) &&
					ati.event_name.equals(tr.cmd_tgt_event_name)) {
					cancelExecutedTask(util,ati.task_ctrl_tc) ;
//						util.addLogMsg("W",String.format(msgs_svc_task_cancel_by_task,
//										tr.active_group_name,tr.cmd_tgt_event_name, tr.active_task_name));
				}
			}
        	int tqsz=taskMgrParms.taskQueueList.size();
        	if (tqsz>0) {
        		TaskListItem tqti;
        		for (int i=tqsz-1;i>=0;i--) {
            		tqti=taskMgrParms.taskQueueList.get(i);
            		if (tqti.group_name.equals(tr.active_group_name) && 
            				tqti.event_name.equals(tr.cmd_tgt_event_name)) {
            			cancelQueuedTask(taskMgrParms, envParms, util, i, 
            					tqti.group_name,tqti.event_name,tqti.task_name, gp);
            		}
        		}
        	}
    	};

    	static final public void cancelAllActiveTask(TaskManagerParms taskMgrParms,
		        		EnvironmentParms envParms, CommonUtilities util, GlobalParameters gp) {
    		if (gp.settingDebugLevel>=2) util.addDebugMsg(2,"I", 
        		"cancelAllActiveTask entered, cnt=",envParms.statsActiveTaskCountString);
        	if (gp.settingDebugLevel>=2) {
            	boolean lh=isLockHeld(TaskManager.LOCK_ID_TASK_CONTROL,TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms, util, gp);
            	if (!lh) util.addLogMsg("E","cancelAllActiveTask Task_Control Write lock was not held");
        	}
    		int atsz=taskMgrParms.activeTaskList.size();
        	if (atsz!=0) {
        		TaskListItem ati=null;
        		for (int i=0;i<atsz;i++) {
        			ati=taskMgrParms.activeTaskList.get(i);
        			if (ati.task_ctrl_tc!=null){
        				cancelExecutedTask(util,ati.task_ctrl_tc);
//	        			util.addLogMsg("W",String.format(msgs_svc_task_cancel_by_user,
//									ati.group_name,ati.task_name));
        			}
        		}
        	}
        	int tqsz=taskMgrParms.taskQueueList.size();
        	if (tqsz>0) {
        		for (int i=tqsz-1;i>=0;i--) {
            		TaskListItem tqti=taskMgrParms.taskQueueList.get(i);
        			cancelQueuedTask(taskMgrParms, envParms, util, i, 
        					tqti.group_name,tqti.event_name,tqti.task_name, gp);
        		}
        	}
            return ;
        };

        static final public void cancelExecutedTask(CommonUtilities util, ThreadCtrl tc) {
			synchronized(tc) {
				tc.setDisabled();
				tc.notifyAll(); 
				Object[] tcobj=tc.getExtraDataObject();
				if (tcobj!=null) {
//					Thread tet=(Thread)tcobj[0];
					Interpreter bshi=(Interpreter)tcobj[1];
					bshi.cancel();
//					try {
//						bshi.eval("clear();");
//						Thread.sleep(10);
//						bshi.eval("clear();");
//					} catch (InterruptedException e) {
//						util.addDebugMsg(1, "E", "Bsh cancel error",e.getMessage());
//					} catch (EvalError e) {
//						util.addDebugMsg(1, "E", "Bsh cancel error",e.getMessage());
//					}
				}
			}
		};

		static final public String isEventIsBlocked(TaskManagerParms taskMgrParms,
				EnvironmentParms envParms, CommonUtilities util,
				String grp,String event, GlobalParameters gp) {
			String btn=null;
    		acqLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_READ,envParms,taskMgrParms,util, gp);
			int alsz=taskMgrParms.blockActionList.size();
			if (alsz!=0) {
				BlockActionItem bai=null;
	        	for (int i=0;i<alsz;i++) {
	        		bai=taskMgrParms.blockActionList.get(i);
	        		if (bai.group_name.equals(grp)) {
		        		if (bai.event_name.equals(event) ||
		        				bai.event_name.equals(BUILTIN_EVENT_ALL)) { 
		        			btn=bai.task_name;
		        			break;
		        		}
	        		}
	        	}
			}
			if (gp.settingDebugLevel>=2) util.addDebugMsg(2, "I", "checkBlockedEvent result=",btn,
					", group=", grp, ", event=", event);
    		relLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_READ,envParms,taskMgrParms,util, gp);
			return btn;
		};

		static final public void addBlockActionListItem(TaskManagerParms taskMgrParms,
    		EnvironmentParms envParms, CommonUtilities util, TaskResponse tr, GlobalParameters gp) {
        	if (gp.settingDebugLevel>=2) {
            	boolean lh=isLockHeld(TaskManager.LOCK_ID_TASK_CONTROL,TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms, util, gp);
            	if (!lh) util.addLogMsg("E","addBlockActionListItem Task_Control Write lock was not held");
        	}
    		BlockActionItem bal=new BlockActionItem();
    		bal.group_name=tr.active_group_name;
    		bal.task_name=tr.active_task_name;
    		bal.event_name=tr.cmd_tgt_event_name;
    		taskMgrParms.blockActionList.add(bal);
    	};

    	static final public void removeBlockActionListItem(TaskManagerParms taskMgrParms,
			EnvironmentParms envParms, CommonUtilities util, String grp,String task_name, GlobalParameters gp) {
        	if (gp.settingDebugLevel>=2) {
            	boolean lh=isLockHeld(TaskManager.LOCK_ID_TASK_CONTROL,TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms, util, gp);
            	if (!lh) util.addLogMsg("E","removeBlockActionListItem Task_Control Write lock was not held");
        	}
			int balsz=taskMgrParms.blockActionList.size()-1;
			BlockActionItem bai;
			for (int i=balsz;i>=0;i--) {
				bai=taskMgrParms.blockActionList.get(i);
				if (bai.group_name.equals(grp) && bai.task_name.equals(task_name)) 
					taskMgrParms.blockActionList.remove(i);
			}
		};

		static final public void clearBlockActionList(TaskManagerParms taskMgrParms,
				EnvironmentParms envParms, CommonUtilities util, GlobalParameters gp) {
        	if (gp.settingDebugLevel>=2) {
            	boolean lh=isLockHeld(TaskManager.LOCK_ID_TASK_CONTROL,TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms, util, gp);
            	if (!lh) util.addLogMsg("E","clearBlockActionList Task_Control Write lock was not held");
        	}
    		taskMgrParms.blockActionList.clear();
		};
		
		static final public void addActiveTaskListItem(TaskManagerParms taskMgrParms,
        		EnvironmentParms envParms, CommonUtilities util, TaskListItem task_item,
        		ThreadCtrl tc, GlobalParameters gp) {
			acqLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms, util, gp);
	    	task_item.task_ctrl_tc=tc;
	    	task_item.task_start_time=StringUtil.convDateTimeTo_HourMinSecMili(System.currentTimeMillis());
	    	taskMgrParms.activeTaskList.add(task_item);
	    	envParms.statsActiveTaskCount=taskMgrParms.activeTaskList.size();
	    	envParms.statsActiveTaskCountString=String.valueOf(envParms.statsActiveTaskCount);
	    	if (envParms.statsHighTaskCountThisPeriod<envParms.statsActiveTaskCount)
	    		envParms.statsHighTaskCountThisPeriod=envParms.statsActiveTaskCount;
			relLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms, util, gp);
		};

		static final public TaskListItem removeActiveTaskListItem(TaskManagerParms taskMgrParms,
		        		EnvironmentParms envParms, CommonUtilities util,
		        		String grp,String task_name, GlobalParameters gp) {
        	TaskListItem ati=null;
			acqLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms, util, gp);
    		int atsz=taskMgrParms.activeTaskList.size();
    		for (int atl_idx=0;atl_idx<atsz;atl_idx++) {
    			ati=taskMgrParms.activeTaskList.get(atl_idx);
    			if (ati.task_name.equals(task_name) && ati.group_name.equals(grp)) {
    				taskMgrParms.activeTaskList.remove(atl_idx);
        			envParms.statsActiveTaskCount=taskMgrParms.activeTaskList.size();
        			envParms.statsActiveTaskCountString=String.valueOf(envParms.statsActiveTaskCount);
            		if (envParms.statsActiveTaskCount==0) {
            			clearBlockActionList(taskMgrParms,envParms,util, gp);
            		} else {
            			removeBlockActionListItem(taskMgrParms, envParms, util, grp, task_name, gp);
            		}
    				break;
    			} else ati=null;
    		}
			relLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms, util, gp);
    		return ati;
        };

        static final public TaskListItem getActiveTaskListItem(TaskManagerParms taskMgrParms,
				EnvironmentParms envParms, CommonUtilities util,
				String grp, String task, GlobalParameters gp) {
			acqLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_READ,envParms,taskMgrParms, util, gp);
			int atsz=taskMgrParms.activeTaskList.size();
			TaskListItem ati=null;
			for (int i=0;i<atsz;i++) {
				ati=taskMgrParms.activeTaskList.get(i);
				if (ati.task_name.equals(task) && ati.group_name.equals(grp)) {
					break;
				} else ati=null;
			}
			relLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_READ,envParms,taskMgrParms, util, gp);
			return ati;
		};
        static final public TaskListItem getTaskQueueListItem(TaskManagerParms taskMgrParms,
				EnvironmentParms envParms, CommonUtilities util,
				String grp, String task, GlobalParameters gp) {
			acqLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_READ,envParms,taskMgrParms, util, gp);
			int atsz=taskMgrParms.taskQueueList.size();
			TaskListItem ati=null;
			for (int i=0;i<atsz;i++) {
				ati=taskMgrParms.taskQueueList.get(i);
				if (ati.task_name.equals(task) && ati.group_name.equals(grp)) {
					break;
				} else ati=null;
			}
			relLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_READ,envParms,taskMgrParms, util, gp);
			return ati;
		};

		static final public String[] buildActiveTaskStringArray(TaskManagerParms taskMgrParms,
		        		EnvironmentParms envParms, CommonUtilities util, GlobalParameters gp) {
    		//event+tab+task+tab+SOUND/NOSOUND+tab+time
    		String[] atsa=null;
			acqLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_READ,envParms,taskMgrParms, util, gp);
			StringBuilder sb=new StringBuilder(256);
			int atlsz=taskMgrParms.activeTaskList.size();
			int tqsz=taskMgrParms.taskQueueList.size();
			String sound="NOSOUND";
			atsa=new String[atlsz+tqsz];
			int lc=0;
			if (atlsz!=0) {
				TaskListItem ati;
				for (int i=0;i<atlsz;i++) {
					ati=taskMgrParms.activeTaskList.get(i);
					sb.setLength(0);
					sb.append(ati.group_name)
						.append("\t")
						.append(ati.event_name)
						.append("\t")
						.append(ati.task_name)
						.append("\t")
						.append(sound)
						.append("\t")
						.append(ati.task_start_time)
						.append("\t")
						.append("STARTED")
						.append("\t");
					atsa[lc]=sb.toString();
					lc++;
				} 
			}
			if (tqsz!=0) {
				TaskListItem tai;
				for (int i=0;i<tqsz;i++) {
					tai=taskMgrParms.taskQueueList.get(i);
					sb.setLength(0);
					sb.append(tai.group_name)
						.append("\t")
						.append(tai.event_name)
						.append("\t")
						.append(tai.task_name)
						.append("\t")
						.append(sound)
						.append("\t")
						.append(StringUtil.convDateTimeTo_HourMinSecMili(System.currentTimeMillis()))
						.append("\t")
						.append("QUEUED")
						.append("\t");
					atsa[lc]=sb.toString();
					lc++;
				} 
			}
			relLock(TaskManager.LOCK_ID_TASK_CONTROL, TaskManager.LOCK_MODE_READ,envParms,taskMgrParms, util, gp);
    		return atsa;
    	};
    	
    	static final public void clearTaskHistoryList(TaskManagerParms taskMgrParms,
        		EnvironmentParms envParms, CommonUtilities util, GlobalParameters gp) {
			acqLock(TaskManager.LOCK_ID_TASK_HISTORY, TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms, util, gp);
			taskMgrParms.taskHistoryList.clear();
			taskMgrParms.task_history_list_was_updated=true;
			relLock(TaskManager.LOCK_ID_TASK_HISTORY, TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms, util, gp);
        };

        static final public void addTaskHistoryListItem(TaskManagerParms taskMgrParms,
        		EnvironmentParms envParms, CommonUtilities util,
        		String add_time, String grp, String event, 
        		String task, String status, GlobalParameters gp) {
//			util.addDebugMsg(1, "I", "addTaskHistoryListItem entered, ",
//					", Group=",grp,", Event=",event,", Task=",task,
//					",Time=",add_time,", Status=",status);
			acqLock(TaskManager.LOCK_ID_TASK_HISTORY, TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms, util, gp);
    		TaskHistoryItem thi=new TaskHistoryItem();
    		thi.group_name=grp;
    		thi.event_name=event;
    		thi.task_name=task;
    		thi.task_status=status;
    		thi.start_time=add_time;
    		taskMgrParms.taskHistoryList.add(thi);
    		taskMgrParms.task_history_list_was_updated=true;
        	if (taskMgrParms.taskHistoryList.size()>99) {
        		for (int i=20;i>=0;i--) taskMgrParms.taskHistoryList.remove(0);
        	}
			relLock(TaskManager.LOCK_ID_TASK_HISTORY, TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms, util, gp);
        };
        static final public void updateTaskHistoryListItem(TaskManagerParms taskMgrParms,
        		EnvironmentParms envParms, CommonUtilities util,
        		String upd_time, String grp, String event, 
        		String task, String status, int resp_cd, String msg, GlobalParameters gp) {
//			util.addDebugMsg(1, "I", "updateTaskHistoryListItem entered, ",
//					", Group=",grp,", Event=",event,", Task=",task,
//					",Time=",upd_time,", Status=",status,", resp="+resp_cd,", Msg=",msg);
			acqLock(TaskManager.LOCK_ID_TASK_HISTORY, TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms, util, gp);
    		int thsz=taskMgrParms.taskHistoryList.size()-1;
    		TaskHistoryItem thli;
    		for (int i=thsz;i>=0;i--) {
    			thli=taskMgrParms.taskHistoryList.get(i);
    			if (thli.group_name.equals(grp) && thli.task_name.equals(task)) {
    				taskMgrParms.task_history_list_was_updated=true;
            		thli.task_status=status;
            		if (status.equals(TaskHistoryItem.TASK_HISTORY_TASK_STATUS_STARTED)) {
        				thli.start_time=upd_time;
    				} else if (status.equals(TaskHistoryItem.TASK_HISTORY_TASK_STATUS_ENDED)) {
        				thli.end_time=upd_time;
        				thli.result=TaskResponse.RESP_CONV_TBL_LONG[resp_cd];
        				thli.task_status=TaskHistoryItem.TASK_HISTORY_TASK_STATUS_ENDED;
        	    		thli.msg_text=msg;
    				} else if (status.equals(TaskHistoryItem.TASK_HISTORY_TASK_STATUS_QUEUED)) {
        				thli.queued_time=upd_time;
    				}
    				break;
    			}
    		}
			relLock(TaskManager.LOCK_ID_TASK_HISTORY, TaskManager.LOCK_MODE_WRITE,envParms,taskMgrParms, util, gp);
        };
		final static public String[] buildTaskHistoryStringArray(TaskManagerParms taskMgrParms,
        		EnvironmentParms envParms, CommonUtilities util, GlobalParameters gp) {
			acqLock(TaskManager.LOCK_ID_TASK_HISTORY, TaskManager.LOCK_MODE_READ,envParms,taskMgrParms,util, gp);
			if (taskMgrParms.task_history_list_was_updated) {
				if (taskMgrParms.taskHistoryList.size()==0) {
					taskMgrParms.task_history_string_array=null;
				} else {
					taskMgrParms.task_history_list_was_updated=false;
					int thlsz=taskMgrParms.taskHistoryList.size();
					taskMgrParms.task_history_string_array=new String[thlsz];
					StringBuilder sb=new StringBuilder(256);
					TaskHistoryItem thli;
					for (int i=0;i<thlsz;i++) {
						thli=taskMgrParms.taskHistoryList.get(i);
						sb.setLength(0);
						sb.append(thli.start_time)
							.append("\t")
							.append(thli.task_status)
							.append("\t")
							.append(thli.result)
							.append("\t")
							.append(thli.group_name)
							.append("\t")
							.append(thli.event_name)
							.append("\t")
							.append(thli.task_name)
							.append("\t");
						if (taskMgrParms.taskHistoryList.get(i).msg_text==null) sb.append("");
						else sb.append(thli.msg_text);
						sb.append("\t");
						taskMgrParms.task_history_string_array[i]=sb.toString();
					}
					util.addDebugMsg(2,"I", "buildTaskHistoryStringArray count=",
							String.valueOf(taskMgrParms.taskHistoryList.size()));
				}
			}
			relLock(TaskManager.LOCK_ID_TASK_HISTORY, TaskManager.LOCK_MODE_READ,envParms,taskMgrParms,util, gp);
			return taskMgrParms.task_history_string_array;
		};
    }
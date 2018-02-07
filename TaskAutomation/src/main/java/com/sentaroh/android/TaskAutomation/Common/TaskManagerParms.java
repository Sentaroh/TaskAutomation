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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.os.Handler;
import android.os.RemoteCallbackList;
import android.support.v4.app.NotificationCompat;

import com.sentaroh.android.TaskAutomation.ISchedulerCallback;
import com.sentaroh.android.TaskAutomation.LocationUtilities;
import com.sentaroh.android.Utilities.NotifyEvent;
import com.sentaroh.android.Utilities.ThreadCtrl;

@SuppressWarnings("deprecation")
public class TaskManagerParms {
	public long resourceCleanupTime=0;
	public boolean schedulerEnabled=true;
	
	public Handler svcHandler=null;
	
	public LocationUtilities locationUtil=null;
	
	public ThreadPoolExecutor taskExecutorThreadPool=null ;
	
	public ThreadPoolExecutor normalTaskControlThreadPool=null ;
	
	public ThreadPoolExecutor highTaskControlThreadPool=null ;

	public LinkedList<TaskListItem> activeTaskList=null;
//	public String activeTaskListForNotification=null;
	public LinkedList<BlockActionItem> blockActionList=null;
	
	public LinkedList<TaskHistoryItem> taskHistoryList=null;
	public boolean task_history_list_was_updated=true;
	public String[] task_history_string_array=null;
	
	public LinkedList<TaskListItem> taskQueueList;
	public ArrayList<ThreadCtrl> wifiNotifyEventList=null;
	public ArrayList<ThreadCtrl> bluetoothNotifyEventList=null;
	public Context context=null;
	public RemoteCallbackList<ISchedulerCallback> callBackList=null;
	public NotifyEvent threadReponseNotify=null;
	
//	Lock controls
//	public ReentrantLock lockTaskEventList=new ReentrantLock();
	public ReentrantReadWriteLock lockEventTaskListRW=new ReentrantReadWriteLock();
	public ReentrantReadWriteLock lockTimerTaskListRW=new ReentrantReadWriteLock();
	public ReentrantReadWriteLock lockProfileListRW=new ReentrantReadWriteLock();
	public ReentrantReadWriteLock lockTaskControlRW=new ReentrantReadWriteLock();
	public ReentrantReadWriteLock lockTaskHistoryRW=new ReentrantReadWriteLock();
	
//	Notification
	public int msgNotificationId=1;
	public NotificationManager mainNotificationManager; 
	public NotificationCompat.Builder mainNotificationBuilder;
	public Notification mainNotification;
	public PendingIntent mainNotificationPi;
	public String main_notification_msgs_svc_active_task,
		main_notification_msgs_appname,
		main_notification_msgs_main_task_scheduler_not_running;
//	public StringBuilder mainNotificationNoOfTask=new StringBuilder();
//	public StringBuilder mainNotificationBattery=new StringBuilder();
	
//	External interface
	public long externalInterfaceRequestId=0;
	
//	Service messages
	public ServiceMessages svcMsgs=new ServiceMessages();
//	TaskExecutor messages
	public TaskExecutorMessages teMsgs=new TaskExecutorMessages();
//	public WakeLock wakelockScreenOn=null;

//	Cancel sound play back task for SDK21(Other SDK was not used)	
	public ArrayList<TaskResponse> soundPlayBackTaskList=new ArrayList<TaskResponse>();
	
//	BeanShell2 execution environment list	
	public ArrayList<BshExecEnvListItem>bshExecEnvList=new ArrayList<BshExecEnvListItem>();
	public boolean bshExecEnvCached=true;
	
//	Keyguard control	
	private KeyguardManager keyguardManager = null;
	private KeyguardLock keyguardLock = null;
	public boolean pendingRequestForEnableKeyguard=true;
	public boolean enableKeyguard=true;
	public void initKeyguardLock() {
 	    keyguardManager = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
		keyguardLock= keyguardManager.newKeyguardLock("TaskAutomation");
	};
	public void setKeyguardEnabled() {
		keyguardLock.reenableKeyguard();
	}
	public void setKeyguardDisabled() {
		keyguardLock.disableKeyguard();
	}
	
//	Trusted network/bluetooth device 
	public ArrayList<TrustDeviceItem> truestedList=new ArrayList<TrustDeviceItem>();
}

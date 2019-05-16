package com.sentaroh.android.TaskAutomation;

import com.sentaroh.android.TaskAutomation.ISchedulerCallback;

interface ISchedulerClient{
	
	void setCallBack(ISchedulerCallback callback);
	void removeCallBack(ISchedulerCallback callback);

	String[] aidlGetActiveTaskList() ;
	void aidlCancelAllActiveTask() ;
	void aidlCancelSpecificTask(String grp, String task_name) ;
	void aidlResetScheduler();
	void aidlClearTaskHistory();
	String[] aidlGetTaskHistoryList();
	void aidlMessageDialogMoveToFront();
	
	void aidlImmediateTaskExecution(in byte[] task, in byte[] time,
							in byte[] action, in byte[] tep) ;
		
	int aidlGetTaskListCount();	
	
	byte[] aidlCopyProfileFromService() ;
	
	void aidlCopyProfileToService(in byte[] prof_list);
	
	byte[] aidlCopyEnvParmsFromService() ;
}
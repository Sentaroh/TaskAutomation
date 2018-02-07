package com.sentaroh.android.TaskAutomation;

interface ISchedulerCallback{ 
    void notifyToClient(String resp_time, String resp_id, String grp, 
    	String task,String action, String shell_cmd, String dialog_id, 
    	int atc, int resp_code, String msg);
}
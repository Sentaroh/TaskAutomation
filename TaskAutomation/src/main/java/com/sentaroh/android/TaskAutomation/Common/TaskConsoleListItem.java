package com.sentaroh.android.TaskAutomation.Common;

public class TaskConsoleListItem {
	public int item_type=0;
	public static final int ITEM_TYPE_TASK=0;
	public static final int ITEM_TYPE_ACTION=1;
	public static final int ITEM_TYPE_MESSAGE=2;
	public String thread_id="";
	public String group_name="", task_name="", action_name="", shell_cmd, result_msg;
	public boolean item_start=false;
	public int result_code=0;//TaskResponse.resp_codeと同じ
}

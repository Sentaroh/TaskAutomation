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

import com.sentaroh.android.Utilities.NotifyEvent;
import com.sentaroh.android.Utilities.ThreadCtrl;

public class TaskResponse implements Cloneable{
	public String resp_id=null;
	public String resp_time=null;
	public int resp_code=0;
	public final static int RESP_CODE_SUCCESS=0;
	public final static int RESP_CODE_WARNING=1;
	public final static int RESP_CODE_ABORT=2;
	public final static int RESP_CODE_CANCELLED=3;
	public final static int RESP_CODE_ERROR=4;
	public final static String RESP_CHAR_SUCCESS="SUCCESS";
	public final static String RESP_CHAR_WARNING="WARNING";
	public final static String RESP_CHAR_ABORT="ABORT";
	public final static String RESP_CHAR_CANCELLED="CANCELLED";
	public final static String RESP_CHAR_ERROR="ERROR";
	public final static String[] RESP_CONV_TBL_SHORT=new String[] 
			{"S","W","A","C","E"};
	public final static String[] RESP_CONV_TBL_LONG=new String[]
			{RESP_CHAR_SUCCESS,RESP_CHAR_WARNING,RESP_CHAR_ABORT,
			 RESP_CHAR_CANCELLED,RESP_CHAR_ERROR};
	public String resp_msg_text=null;
	
	public boolean prof_notification=false;
	public boolean task_action_notification=false;
	
	public String active_thread_id=null;
	public String active_group_name=null;
	public String active_event_name=null;
	public String active_task_name=null;
	public String active_action_name=null;
	public String active_shell_cmd=null;
	public String active_dialog_id=null;
	
	public String cmd_tgt_event_name=null;
	public String cmd_tgt_task_name=null;
	public String cmd_tgt_action_name=null;
	
	public String cmd_message_type=null;
	public String cmd_message_text=null;
	public int cmd_message_led_color=0;
	public int cmd_message_led_on=0;
	public int cmd_message_led_off=0;
	
	public ThreadCtrl active_thread_ctrl=null;
	public NotifyEvent active_notify_event=null;
	
	@Override
	public TaskResponse clone() throws CloneNotSupportedException{
			return (TaskResponse) super.clone();
	}
};

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

import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.SERIALIZABLE_NUMBER;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;

import com.sentaroh.android.Utilities.SerializeUtil;
import com.sentaroh.android.Utilities.ThreadCtrl;

public class TaskListItem implements Externalizable, Cloneable{
	private static final long serialVersionUID = SERIALIZABLE_NUMBER;
	public String group_name=null;
	public String event_name="";
	public String task_name="";
	public boolean prof_notification=false;
	public boolean task_action_notification=false;
	public long profile_update_time=0;
	public boolean timer_update_required=true;
	public String time_type="",day_of_the_week="", sched_yyyy="",
			sched_month="",sched_day="",sched_hour="",sched_min="";

	public long sched_time=0;
	public ArrayList<TaskActionItem> taskActionList=new ArrayList<TaskActionItem>();
	
	//Active task only
	transient public ThreadCtrl task_ctrl_tc=null;
	transient public String task_start_time=null;
	
	public TaskListItem() {};
	
	@Override
	public TaskListItem clone() throws CloneNotSupportedException{	
			return (TaskListItem)super.clone();
	};
	
	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException{
		long sid=input.readLong();
		if (serialVersionUID!=sid) {
			throw new IOException("serialVersionUID was not matched by saved UID");
		}

		group_name=input.readUTF();
		event_name=input.readUTF();
		task_name=input.readUTF();
		prof_notification=input.readBoolean();
		task_action_notification=input.readBoolean();
		profile_update_time=input.readLong();
		timer_update_required=input.readBoolean();
		time_type=input.readUTF();
		day_of_the_week=input.readUTF();
		sched_yyyy=input.readUTF();
		sched_month=input.readUTF();
		sched_day=input.readUTF();
		sched_hour=input.readUTF();
		sched_min=input.readUTF();
		sched_time=input.readLong();
//		taskActionList=SerializeUtil.readListTaskActionItem(input);
		taskActionList=(ArrayList<TaskActionItem>) SerializeUtil.readArrayList(input);
	}
	@Override
	public void writeExternal(ObjectOutput output) throws IOException {
		output.writeLong(serialVersionUID);
		
		output.writeUTF(group_name);
		output.writeUTF(event_name);
		output.writeUTF(task_name);
		output.writeBoolean(prof_notification);
		output.writeBoolean(task_action_notification);
		output.writeLong(profile_update_time);
		output.writeBoolean(timer_update_required);
		output.writeUTF(time_type);
		output.writeUTF(day_of_the_week);
		output.writeUTF(sched_yyyy);
		output.writeUTF(sched_month);
		output.writeUTF(sched_day);
		output.writeUTF(sched_hour);
		output.writeUTF(sched_min);
		output.writeLong(sched_time);
//		output.writeObject(taskActionList);
//		SerializeUtil.writeListTaskActionItem(output,taskActionList);
		SerializeUtil.writeArrayList(output,taskActionList);
	}
};

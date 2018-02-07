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

public class TaskListHolder implements Externalizable{
	private static final long serialVersionUID = SERIALIZABLE_NUMBER;
	public ArrayList<ProfileListItem> profile_array_list=null;
	public ArrayList<TaskListItem> builtin_task_list=null;
	public ArrayList<TaskListItem> timer_task_list=null;
	public ArrayList<TaskListItem> task_task_list=null;
	public ArrayList<TaskLookupListItem>lookup_list=null;
	public boolean req_mag=false, req_light=false, req_bl=false,req_prx=false;
	public boolean req_acc=false;
	public long build_time=0;
	
	public TaskListHolder() {};
	
	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException{
		long sid=input.readLong();
		if (serialVersionUID!=sid) {
			throw new IOException("serialVersionUID was not matched by saved UID");
		}

//		profile_array_list=SerializeUtil.readListProfileListItem(input);
//		builtin_task_list=SerializeUtil.readListTaskListItem(input);
//		timer_task_list=SerializeUtil.readListTaskListItem(input);
//		task_task_list=SerializeUtil.readListTaskListItem(input);
//		lookup_list=SerializeUtil.readListTaskListLookupItem(input);
		profile_array_list=(ArrayList<ProfileListItem>) SerializeUtil.readArrayList(input);
		builtin_task_list=(ArrayList<TaskListItem>) SerializeUtil.readArrayList(input);
		timer_task_list=(ArrayList<TaskListItem>) SerializeUtil.readArrayList(input);
		task_task_list=(ArrayList<TaskListItem>) SerializeUtil.readArrayList(input);
		lookup_list=(ArrayList<TaskLookupListItem>) SerializeUtil.readArrayList(input);
		req_mag=input.readBoolean(); 
		req_light=input.readBoolean(); 
		req_bl=input.readBoolean();
		req_prx=input.readBoolean();
		build_time=input.readLong();
	}
	@Override
	public void writeExternal(ObjectOutput output) throws IOException {
		output.writeLong(serialVersionUID);
		
//		SerializeUtil.writeListProfileListItem(output, profile_array_list);
//		SerializeUtil.writeListTaskListItem(output,builtin_task_list);
//		SerializeUtil.writeListTaskListItem(output,timer_task_list);
//		SerializeUtil.writeListTaskListItem(output,task_task_list);
//		SerializeUtil.writeListTaskListLookupItem(output,lookup_list);
		SerializeUtil.writeArrayList(output, profile_array_list);
		SerializeUtil.writeArrayList(output,builtin_task_list);
		SerializeUtil.writeArrayList(output,timer_task_list);
		SerializeUtil.writeArrayList(output,task_task_list);
		SerializeUtil.writeArrayList(output,lookup_list);
		output.writeBoolean(req_mag); 
		output.writeBoolean(req_light); 
		output.writeBoolean(req_bl);
		output.writeBoolean(req_prx);
		output.writeLong(build_time);
	}
};

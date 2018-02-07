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

import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.*;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_BSH_SCRIPT;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_BUILTIN;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_COMPARE;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_MESSAGE;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_MUSIC;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_RINGTONE;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_TASK;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_TIME;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_WAIT;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.SERIALIZABLE_NUMBER;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;

import com.sentaroh.android.Utilities.SerializeUtil;

public class TaskActionItem implements Externalizable, Cloneable{
	private static final long serialVersionUID = SERIALIZABLE_NUMBER;
	public long profile_update_time=0;
	public String action_type=null;
	public String action_name=null;
	public String action_dialog_id=null;
	//TaskType=Builtin
	public String action_builtin_action=null;
	//TaskType=Android
	public String action_activity_name=null, action_activity_pkgname=null;
	public String action_activity_data_type=null;
	public String action_activity_data_uri=null;
	public ArrayList<ActivityExtraDataItem>action_activity_data_extra_list=null;
	//TaskType=Music
	public String action_sound_file_name=null;
	public String action_sound_vol_left="-1",action_sound_vol_right="-1";
	//TaskType=Ringtone
	public String action_ringtone_type=null;
	public String action_ringtone_name=null;
	public String action_ringtone_path=null;
	public String action_ringtone_vol_left="-1",action_ringtone_vol_right="-1";
	//TaskType=Compare
	public String action_compare_target=null;
	public String action_compare_type=null;
	public String[] action_compare_value=null;
	public String action_compare_result_action=null;
	//TaskType=Message
	public String action_message_type=null;
	public String action_message_text=null;
	public String action_message_led_color=null;
	public boolean action_message_use_led=false;
	public boolean action_message_use_vib=false;
	//TaskType=Time
	public String action_time_type=null;
	public String action_time_target=null;
	//TaskType=Task
	public String action_task_type=null;
	public String action_task_target=null;
	//TaskType=Wait
	public String action_wait_target=null;
	public String action_wait_timeout_value=null;
	public String action_wait_timeout_units=null;

	//TaskType=BeanShellScript
	public String action_bsh_script=null;

	//TaskType=Shell
	public String action_shell_cmd="";
	public boolean action_shell_cmd_with_su=false;

	
	public TaskActionItem() {};
	
	@Override
	public TaskActionItem clone() throws CloneNotSupportedException{
			return (TaskActionItem)super.clone();
	};
	
	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException {
		long sid=input.readLong();
		if (serialVersionUID!=sid) {
			throw new IOException("serialVersionUID was not matched by saved UID");
		}

		profile_update_time=input.readLong();
		action_type=input.readUTF();
		action_name=input.readUTF();
		action_dialog_id=input.readUTF();
		if (action_type.equals(PROFILE_ACTION_TYPE_BUILTIN)) {
			action_builtin_action=input.readUTF();
		} else if (action_type.equals(PROFILE_ACTION_TYPE_ACTIVITY)) {
			action_activity_name=input.readUTF();
			action_activity_pkgname=input.readUTF();
			action_activity_data_type=input.readUTF();
			action_activity_data_uri=input.readUTF();
//			action_activity_data_extra_list=SerializeUtil.readListActivityExtraDataItem(input);
			action_activity_data_extra_list=(ArrayList<ActivityExtraDataItem>) SerializeUtil.readArrayList(input);
		} else if (action_type.equals(PROFILE_ACTION_TYPE_MUSIC)) {
			action_sound_file_name=input.readUTF();
			action_sound_vol_left=input.readUTF();
			action_sound_vol_right=input.readUTF();
		} else if (action_type.equals(PROFILE_ACTION_TYPE_RINGTONE)) {
			action_ringtone_type=input.readUTF();
			action_ringtone_name=input.readUTF();
			action_ringtone_path=input.readUTF();
			action_ringtone_vol_left=input.readUTF();
			action_ringtone_vol_right=input.readUTF();
		} else if (action_type.equals(PROFILE_ACTION_TYPE_COMPARE)) {
			action_compare_target=input.readUTF();
			action_compare_type=input.readUTF();
			action_compare_value=SerializeUtil.readArrayString(input);
			action_compare_result_action=input.readUTF();
		} else if (action_type.equals(PROFILE_ACTION_TYPE_MESSAGE)) {
			action_message_type=input.readUTF();
			action_message_text=input.readUTF();
			action_message_led_color=input.readUTF();
			action_message_use_led=input.readBoolean();
			action_message_use_vib=input.readBoolean();
		} else if (action_type.equals(PROFILE_ACTION_TYPE_TIME)) {
			action_time_type=input.readUTF();
			action_time_target=input.readUTF();
		} else if (action_type.equals(PROFILE_ACTION_TYPE_TASK)) {
			action_task_type=input.readUTF();
			action_task_target=input.readUTF();
		} else if (action_type.equals(PROFILE_ACTION_TYPE_WAIT)) {
			action_wait_target=input.readUTF();
			action_wait_timeout_value=input.readUTF();
			action_wait_timeout_units=input.readUTF();
		} else if (action_type.equals(PROFILE_ACTION_TYPE_BSH_SCRIPT)) {
			action_bsh_script=input.readUTF();
		} else if (action_type.equals(PROFILE_ACTION_TYPE_SHELL_COMMAND)) {
			action_shell_cmd=input.readUTF();
			action_shell_cmd_with_su=input.readBoolean();
		}
	}
	@Override
	public void writeExternal(ObjectOutput output) throws IOException {
		output.writeLong(serialVersionUID);
		
		output.writeLong(profile_update_time);
		output.writeUTF(action_type);
		output.writeUTF(action_name);
		output.writeUTF(action_dialog_id);
		if (action_type.equals(PROFILE_ACTION_TYPE_BUILTIN)) {
			output.writeUTF(action_builtin_action);
		} else if (action_type.equals(PROFILE_ACTION_TYPE_ACTIVITY)) {
			output.writeUTF(action_activity_name);
			output.writeUTF(action_activity_pkgname);
			output.writeUTF(action_activity_data_type);
			output.writeUTF(action_activity_data_uri);
//			SerializeUtil.writeListActivityExtraDataItem(output,action_activity_data_extra_list);
			SerializeUtil.writeArrayList(output,action_activity_data_extra_list);
		} else if (action_type.equals(PROFILE_ACTION_TYPE_MUSIC)) {
			output.writeUTF(action_sound_file_name);
			output.writeUTF(action_sound_vol_left);
			output.writeUTF(action_sound_vol_right);
		} else if (action_type.equals(PROFILE_ACTION_TYPE_RINGTONE)) {
			output.writeUTF(action_ringtone_type);
			output.writeUTF(action_ringtone_name);
			output.writeUTF(action_ringtone_path);
			output.writeUTF(action_ringtone_vol_left);
			output.writeUTF(action_ringtone_vol_right);
		} else if (action_type.equals(PROFILE_ACTION_TYPE_COMPARE)) {
			output.writeUTF(action_compare_target);
			output.writeUTF(action_compare_type);
			SerializeUtil.writeArrayString(output,action_compare_value);
			output.writeUTF(action_compare_result_action);
		} else if (action_type.equals(PROFILE_ACTION_TYPE_MESSAGE)) {
			output.writeUTF(action_message_type);
			output.writeUTF(action_message_text);
			output.writeUTF(action_message_led_color);
			output.writeBoolean(action_message_use_led);
			output.writeBoolean(action_message_use_vib);
		} else if (action_type.equals(PROFILE_ACTION_TYPE_TIME)) {
			output.writeUTF(action_time_type);
			output.writeUTF(action_time_target);
		} else if (action_type.equals(PROFILE_ACTION_TYPE_TASK)) {
			output.writeUTF(action_task_type);
			output.writeUTF(action_task_target);
		} else if (action_type.equals(PROFILE_ACTION_TYPE_WAIT)) {
			output.writeUTF(action_wait_target);
			output.writeUTF(action_wait_timeout_value);
			output.writeUTF(action_wait_timeout_units);
		} else if (action_type.equals(PROFILE_ACTION_TYPE_BSH_SCRIPT)) {
			output.writeUTF(action_bsh_script);
		} else if (action_type.equals(PROFILE_ACTION_TYPE_SHELL_COMMAND)) {
			output.writeUTF(action_shell_cmd);
			output.writeBoolean(action_shell_cmd_with_su);
		}
	}
};

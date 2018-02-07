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
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_RINGTONE_TYPE_ALARM;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_RINGTONE_TYPE_ALERT;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_RINGTONE_TYPE_NOTIFICATION;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_RINGTONE_TYPE_RINGTONE;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_ACTIVITY;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_BSH_SCRIPT;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_COMPARE;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_MESSAGE;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_MESSAGE_LED_BLUE;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_MUSIC;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_RINGTONE;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_TASK;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_TIME;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_WAIT;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ENABLED;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_GROUP_DEFAULT;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_RETROSPECIVE_ENABLED;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_VERSION_CURRENT;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.SERIALIZABLE_NUMBER;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.media.RingtoneManager;
import android.util.Log;

import com.sentaroh.android.Utilities.SerializeUtil;

public class ProfileListItem implements Cloneable, Externalizable{
	
	private static final long serialVersionUID = SERIALIZABLE_NUMBER;
	
//	変数を追加した場合は必ずwriteExternal、readExternalとdumpを更新すること	

//  Header	
	private String profile_version=PROFILE_VERSION_CURRENT;
	private String profile_group=PROFILE_GROUP_DEFAULT;
	private String profile_type="",profile_name="",profile_enable="";
	private int profile_group_active=0;
	private long profile_update_time=0;
	
//	Task	
	private String retrospective_enable="",retrospective_number="",
			profile_error_notification="0";
	private ArrayList<String> action_action=null;
	private ArrayList<String> action_trigger=null;

//	Timer	
	private String time_date_time_type="",
			time_day_of_the_week="",
			time_date="",time_time="";

//	Action	
	private String action_action_type="";
	//ActionType=Android
	private String action_activity_name="", action_activity_package="";
	private String action_activity_data_type="", action_activity_uri_data="";
	private ArrayList<ActivityExtraDataItem>action_activity_extra_data_list=null;
	//ActionType=Music
	private String action_sound_file_name="";
	private String action_sound_vol_left="",action_sound_vol_right="";
	//ActionType=Ringtone
	private String action_ringtone_type="";
	private String action_ringtone_name="";
	private String action_ringtone_path="";
	private String action_ringtone_vol_left="",action_ringtone_vol_right="";
	//Compare
	private String action_compare_target="", action_compare_type="", 
			action_compare_result_action="";
	private String[] action_compare_val=null;//new String[]{""};
	//Message
	private String action_message_type="", action_message_text=""; 
	private int action_message_use_vibration=0, action_message_use_led=0;
	private String action_message_led_color=PROFILE_ACTION_TYPE_MESSAGE_LED_BLUE;
	//Action Time 
	private String action_time_type="", action_time_target=""; 
	//Action Task 
	private String action_task_type="", action_task_target=""; 
	//Action Wait
	private String action_wait_target="",
			action_wait_timeout_value="", action_wait_timeout_units=""; 
	//Action Bean Shell Script
	private String action_bsh_script=""; 

	//Action shell cmd
	private String action_shell_cmd=""; 
	private boolean action_shell_cmd_with_su=false;

//	Not for profile file	
	private transient boolean isSelected=false;
	private transient boolean task_active=false;
	
	private transient boolean profile_group_selected=false;
	private transient boolean filter_selected=true;

	@Override
	final public ProfileListItem clone() {
		return deSerialize(serialize());
    };
	
	final static private ProfileListItem deSerialize(byte[] buf) {
		ProfileListItem o=null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(buf); 
			ObjectInput in = new ObjectInputStream(bis);
			o=(ProfileListItem) in.readObject(); 
		    in.close(); 
		} catch (StreamCorruptedException e) {
			Log.v(APPLICATION_TAG, "ProfileListItem deSerialize error", e);
		} catch (IOException e) {
			Log.v(APPLICATION_TAG, "ProfileListItem deSerialize error", e);
		} catch (ClassNotFoundException e) {
			Log.v(APPLICATION_TAG, "ProfileListItem deSerialize error", e);
		}
		return o;
	};
	final private byte[] serialize() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(10000); 
		byte[] buf=null; 
	    try { 
	    	ObjectOutput out = new ObjectOutputStream(bos); 
		    out.writeObject(this);
		    out.flush(); 
		    buf= bos.toByteArray(); 
	    } catch(IOException e) { 
	    	Log.v(APPLICATION_TAG, "ProfileListItem serialize error", e); 
		}
		return buf;
	};

//	Constructor for dummy
	public ProfileListItem() {};
	
	final public void dumpProfile() {
		final SimpleDateFormat sdfDateTimeSss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.getDefault());
		String spacer="    ";
		Log.v(APPLICATION_TAG,"Profile Version="+profile_version+
				", Group="+profile_group+
				", Type="+profile_type+
				", Name="+profile_name+
				", Enabled="+profile_enable+
				", Group activated="+profile_group_active+
				", Update Tme="+sdfDateTimeSss.format(profile_update_time));
		Log.v(APPLICATION_TAG,spacer+"   isSelected="+isSelected+
				", task_active="+task_active+
				", profile_group_selected="+profile_group_selected+
				", filter_selected="+filter_selected);
		
//		Task	
		Log.v(APPLICATION_TAG,spacer+"   Retrospective Enabled="+retrospective_enable+
				", number="+retrospective_number+
				", Error notification="+profile_error_notification);
		if (action_action!=null) {
			for (int i=0;i<action_action.size();i++) {
				Log.v(APPLICATION_TAG,spacer+"   Action list="+action_action.get(i));
			}
		} else Log.v(APPLICATION_TAG,spacer+"   No action list");
		if (action_trigger!=null) {
			for (int i=0;i<action_trigger.size();i++) {
				Log.v(APPLICATION_TAG,spacer+"   Trigger list="+action_trigger.get(i));
			}
		}else Log.v(APPLICATION_TAG,spacer+"   No trigger list");
//		Timer	
		Log.v(APPLICATION_TAG,spacer+"Date type="+time_date_time_type+
				", Day of the week="+time_day_of_the_week+
				", Date="+time_date+", Time="+time_time);

//		Action	
		Log.v(APPLICATION_TAG,spacer+"Action type="+action_action_type);
		//ActionType=Android
		Log.v(APPLICATION_TAG,spacer+"Activity name="+action_activity_name+
				", Package="+action_activity_package+
				", Data type="+action_activity_data_type+
				", Uri="+action_activity_uri_data);
		if (action_activity_extra_data_list!=null) {
			for (int i=0;i<action_activity_extra_data_list.size();i++) {
				Log.v(APPLICATION_TAG,spacer+"   Extra data key="+action_activity_extra_data_list.get(i).key_value+
						", Data type="+action_activity_extra_data_list.get(i).data_type+
						", Array="+action_activity_extra_data_list.get(i).data_value_array+
						", Data="+action_activity_extra_data_list.get(i).data_value);
			}
		} else Log.v(APPLICATION_TAG,spacer+"   No extra data");
		//ActionType=Music
		Log.v(APPLICATION_TAG,spacer+"Sound file name="+action_sound_file_name+
				", volume left="+action_sound_vol_left+
				", right=="+action_sound_vol_right);
		//ActionType=Ringtone
		Log.v(APPLICATION_TAG,spacer+"Ringtone type="+action_ringtone_type+
				", Name="+action_ringtone_name+
				", Path="+action_ringtone_path+
				", volume left="+action_sound_vol_left+
				", right=="+action_sound_vol_right);
		//Compare
		Log.v(APPLICATION_TAG,spacer+"Compare target="+action_compare_target+
				", type="+action_compare_type+
				", result="+action_compare_result_action);
		if (action_compare_val!=null) {
			for (int i=0;i<action_compare_val.length;i++)
				Log.v(APPLICATION_TAG,spacer+"   Compare value="+action_compare_val[i]);
		} else Log.v(APPLICATION_TAG,spacer+"   No compare value");
		//Message
		Log.v(APPLICATION_TAG,spacer+"Message type="+action_message_type+
				", vibration="+action_message_use_vibration+
				", led="+action_message_use_led+
				", color="+action_message_led_color+
				", text="+action_message_text); 
		//Action Time 
		Log.v(APPLICATION_TAG,spacer+"Time type="+action_time_type+", target="+action_time_target); 
		//Action Task 
		Log.v(APPLICATION_TAG,spacer+"Task type="+action_task_type+", target="+action_task_target);
		//Action Wait
		Log.v(APPLICATION_TAG,spacer+"Wait target="+action_wait_target+
				", timeout="+action_wait_timeout_value+
				", units="+action_wait_timeout_units); 
		//Action Bean Shell Script
		Log.v(APPLICATION_TAG,spacer+"BeanShell Script="+action_bsh_script); 

		//Action shell cmd
		Log.v(APPLICATION_TAG,spacer+"SU="+action_shell_cmd_with_su+", Shell cmd="+action_shell_cmd); 

	};
	
//	Builder for task entry
	final public void setTaskEntry(String pv, String pg, boolean pga,long put,
			String pt, String pn, String pe, String rs, 
			String rn, String nm, ArrayList<String> e_action,
			ArrayList<String> e_trig_event) {
		profile_version=pv;
		profile_group=pg;
		setProfileGroupActivated(pga);
		profile_update_time=put;
		profile_type=pt;
		profile_enable=pe;
		profile_name=pn;
		profile_error_notification=nm;
		retrospective_enable=rs;
		retrospective_number=rn;
		action_action=e_action;
		action_trigger=e_trig_event;
	}

//	Builder for time event entry	
	final public void setTimeEventEntry(String pv, String pg, boolean pga,long put,
			String pt, String pn, 
			String pe, String dtt, String dw, String ed, String et) {
		profile_version=pv;
		profile_group=pg;
		setProfileGroupActivated(pga);
		profile_update_time=put;
		profile_type=pt;
		profile_enable=pe;
		profile_name=pn;
		time_date_time_type=dtt;
		time_day_of_the_week=dw;
		time_date=ed;
		time_time=et;
	}
	
//	Builder for action entry	
	final public void setActionAndroidEntry(String pv, String pg, boolean pga,long put,
			String pt, String pn, String pe,  
			String act_name, String act_pkg, String dt, String ud,
			ArrayList<ActivityExtraDataItem>aedl) {
		profile_version=pv;
		profile_group=pg;
		setProfileGroupActivated(pga);
		profile_update_time=put;
		profile_type=pt;
		profile_enable=pe;
		profile_name=pn;
		action_action_type=PROFILE_ACTION_TYPE_ACTIVITY;
		action_activity_name=act_name;
		action_activity_package=act_pkg;
		action_activity_data_type=dt;
		action_activity_uri_data=ud;
		action_activity_extra_data_list=aedl;
	}
	final public String getActionActivityDataType() {return action_activity_data_type;}
	final public String getActionActivityUriData() {return action_activity_uri_data;}
	final public ArrayList<ActivityExtraDataItem> getActionActivityExtraData() {return action_activity_extra_data_list;}
	final public void setActionActivityDataType(String p) {action_activity_data_type=p;}
	final public void setActionActivityUriData(String p) {action_activity_uri_data=p;}
	final public void setActionActivityExtraData(ArrayList<ActivityExtraDataItem> p) {action_activity_extra_data_list=p;}
//	Constructor for action entry	
	final public void setActionMusicEntry(String pv, String pg, boolean pga,long put,
			String pt, String pn, String pe,  
			String file_name, String vol_left, String vol_right) {
		profile_version=pv;
		profile_group=pg;
		setProfileGroupActivated(pga);
		profile_update_time=put;
		profile_type=pt;
		profile_enable=pe;
		profile_name=pn;
		action_action_type=PROFILE_ACTION_TYPE_MUSIC;
		action_sound_file_name=file_name;
		action_sound_vol_left=vol_left;
		action_sound_vol_right=vol_right;
	}
	
//	Constructor for action entry	
	final public void setActionRingtoneEntry(String pv, String pg, boolean pga,long put,
			String pt, String pn, String pe,  
			String rt, String ringtone_name, String ringtone_path, String vol_left, String vol_right) {
		profile_version=pv;
		profile_group=pg;
		setProfileGroupActivated(pga);
		profile_update_time=put;
		profile_type=pt;
		profile_enable=pe;
		profile_name=pn;
		action_action_type=PROFILE_ACTION_TYPE_RINGTONE;
		action_ringtone_type=rt;
		action_ringtone_name=ringtone_name;
		action_ringtone_path=ringtone_path;
		action_ringtone_vol_left=vol_left;
		action_ringtone_vol_right=vol_right;
	}

//	Constructor for action entry
	final public void setActionCompareEntry(String pv, String pg, boolean pga,long put,
			String pt, String pn, String pe,
			String ct, String comp_type, String[] val, String ra) {
		profile_version=pv;
		profile_group=pg;
		setProfileGroupActivated(pga);
		profile_update_time=put;
		profile_type=pt;
		profile_enable=pe;
		profile_name=pn;
		action_action_type=PROFILE_ACTION_TYPE_COMPARE;
		action_compare_target=ct;
		action_compare_type=comp_type;
		action_compare_val=val;
		action_compare_result_action=ra;
	}
	final public String getActionCompareTarget() {return action_compare_target;}
	final public void   setActionCompareTarget(String p) {action_compare_target=p;}
	final public String getActionCompareType() {return action_compare_type;}
	final public void   setActionCompareType(String p) {action_compare_type=p;}

	final public String getActionCompareValue(int idx) {
		if (idx<action_compare_val.length) return action_compare_val[idx];
		else return null;
	}
	final public String[] getActionCompareValue() {return action_compare_val;}
	final public void     setActionCompareValue(String[] p) {action_compare_val=p;}
	
	final public String   getActionCompareResultAction() {return action_compare_result_action;}
	final public void     setActionCompareResultAction(String p) {action_compare_result_action=p;}

//	Constructor for action entry
	final public void setActionMessageEntry(String pv, String pg, boolean pga,long put,
			String pt, String pn, String pe,
			String mt, String mtxt, boolean uv, boolean ul, String lc) {
		profile_version=pv;
		profile_group=pg;
		setProfileGroupActivated(pga);
		profile_update_time=put;
		profile_type=pt;
		profile_enable=pe;
		profile_name=pn;
		action_action_type=PROFILE_ACTION_TYPE_MESSAGE;
		action_message_type=mt;
		action_message_text=mtxt;
		setActionMessageUseVibration(uv);
		setActionMessageUseLed(ul);
		action_message_led_color=lc;
	}
	final public String  getActionMessageType() {return action_message_type;}
	final public void    setActionMessageType(String p) {action_message_type=p;}
	final public String  getActionMessageText() {return action_message_text;}
	final public void    setActionMessageText(String p) {action_message_text=p;}
	final public String  getActionMessageLedColor() {return action_message_led_color;}
	final public void    setActionMessageLedColor(String p) {action_message_led_color=p;}
	final public boolean isActionMessageUseVibration() {return action_message_use_vibration==1?true:false;}
	final public void    setActionMessageUseVibration(boolean p) {
		if (p) action_message_use_vibration=1;
		else action_message_use_vibration=0;
	}
	final public boolean isActionMessageUseLed() {return action_message_use_led==1?true:false;}
	final public void    setActionMessageUseLed(boolean p) {
		if (p) action_message_use_led=1;
		else action_message_use_led=0;
	}

//	Constructor for action-time entry
	final public void setActionTimeEntry(String pv, String pg, boolean pga,long put,
			String pt, String pn, String pe,
			String tt, String tgt) {
		profile_version=pv;
		profile_group=pg;
		setProfileGroupActivated(pga);
		profile_update_time=put;
		profile_type=pt;
		profile_enable=pe;
		profile_name=pn;
		action_action_type=PROFILE_ACTION_TYPE_TIME;
		action_time_type=tt;
		action_time_target=tgt;
	}
	final public String getActionTimeType() {return action_time_type;}
	final public void   setActionTimeType(String p) {action_time_type=p;}
	final public String getActionTimeTarget() {return action_time_target;}
	final public void   setActionTimeTarget(String p) {action_time_target=p;}

//	Constructor for action-task entry
	final public void setActionTaskEntry(String pv, String pg, boolean pga,long put,
			String pt, String pn, String pe,
			String tt, String tgt) {
		profile_version=pv;
		profile_group=pg;
		setProfileGroupActivated(pga);
		profile_update_time=put;
		profile_type=pt;
		profile_enable=pe;
		profile_name=pn;
		action_action_type=PROFILE_ACTION_TYPE_TASK;
		action_task_type=tt;
		action_task_target=tgt;
	}
	final public String getActionTaskType() {return action_task_type;}
	final public void   setActionTaskType(String p) {action_task_type=p;}
	final public String getActionTaskTarget() {return action_task_target;}
	final public void   setActionTaskTarget(String p) {action_task_target=p;}

//	Constructor for action-wait entry
	final public void setActionWaitEntry(String pv, String pg, boolean pga,long put,
			String pt, String pn, String pe,
			String wtgt, String toval, String tounits) {
		profile_version=pv;
		profile_group=pg;
		setProfileGroupActivated(pga);
		profile_update_time=put;
		profile_type=pt;
		profile_enable=pe;
		profile_name=pn;
		action_action_type=PROFILE_ACTION_TYPE_WAIT;
		action_wait_target=wtgt;
		action_wait_timeout_value=toval;
		action_wait_timeout_units=tounits;
	}
	final public String getActionWaitTarget() {return action_wait_target;}
	final public void   setActionWaitTarget(String p) {action_wait_target=p;}
	final public String getActionWaitTimeoutValue() {return action_wait_timeout_value;}
	final public void   setActionWaitTimeoutValue(String p) {action_wait_timeout_value=p;}
	final public String getActionWaitTimeoutUnits() {return action_wait_timeout_units;}
	final public void   setActionWaitTimeoutUnits(String p) {action_wait_timeout_units=p;}

//	Constructor for action Bean Shell Script entry
	final public void setActionBeanShellScriptEntry(String pv, String pg, boolean pga,long put,
			String pt, String pn, String pe, String script) {
		profile_version=pv;
		profile_group=pg;
		setProfileGroupActivated(pga);
		profile_update_time=put;
		profile_type=pt;
		profile_enable=pe;
		profile_name=pn;
		action_action_type=PROFILE_ACTION_TYPE_BSH_SCRIPT;
		action_bsh_script=script;
	}
	final public String getActionBeanShellScriptScript() {return action_bsh_script;}
	final public void   setActionBeanShellScriptScript(String p) {action_bsh_script=p;}

//	Constructor for action shell cmd entry
	final public void setActionShellCmdEntry(String pv, String pg, boolean pga,long put,
			String pt, String pn, String pe, String cmd, boolean su) {
		profile_version=pv;
		profile_group=pg;
		setProfileGroupActivated(pga);
		profile_update_time=put;
		profile_type=pt;
		profile_enable=pe;
		profile_name=pn;
		action_action_type=PROFILE_ACTION_TYPE_SHELL_COMMAND;
		action_shell_cmd=cmd;
		action_shell_cmd_with_su=su;
	}
	final public String getActionShellCmd() {return action_shell_cmd;}
	final public void setActionShellCmd(String p) {action_shell_cmd=p;}
	final public boolean isActionShellCmdWithSu() {return action_shell_cmd_with_su;}
	final public void setActionShellCmdWithSu(boolean p) {action_shell_cmd_with_su=p;}
	
//	
	final public void    setFilterSelected(boolean p) {filter_selected=p;}
	final public boolean isFilterSelected() {return filter_selected;}
	
	final public boolean isProfileItemSelected() {return isSelected;}
	final public void    setProfileItemSelected(boolean p) {isSelected=p;}

	final public boolean isProfileGroupActivated() {return profile_group_active==1?true:false;};
	final public void    setProfileGroupActivated(boolean p) {
		if (p) profile_group_active=1;
		else profile_group_active=0;
	}
	
	final public boolean isProfileGroupShowed() {return profile_group_selected;}
	final public void    setProfileGroupShowed(boolean p) {profile_group_selected=p;}
	
	final public String  getProfileType() {return profile_type;}
	final public String  getProfileEnabled() {return profile_enable;}
	final public boolean isProfileEnabled() {
		if (profile_enable.equals(PROFILE_ENABLED)) return true;
		else return false;
	}
	final public boolean isProfileRetrospective() {
		if (retrospective_enable.equals(PROFILE_RETROSPECIVE_ENABLED)) return true;
		else return false;
	}

	final public String getProfileVersion() {return profile_version;}
	final public String getProfileGroup() {return profile_group;}
	final public String getProfileName() {return profile_name;}
	final public long   getProfileUpdateTime() {return profile_update_time;}
	final public String getTaskRetrospecEnabled() {return retrospective_enable;}
	final public String getTaskRetrospecNumber() {return retrospective_number;}
	final public String getTimeType() {return time_date_time_type;}
	final public String getTimeDayOfTheWeek() {return time_day_of_the_week;}
	final public String getTimeDate() {return time_date;}
	final public String getTimeTime() {return time_time;}
	final public String getActionType() {return action_action_type;}
	final public String getActionActivityName() {return action_activity_name;}
	final public String getActionActivityPackageName() {return action_activity_package;}
	final public String getActionSoundFileName() {return action_sound_file_name;}
	final public ArrayList<String> getTaskTriggerList() {return action_trigger;}
	final public ArrayList<String> getTaskActionList() {return action_action;}
//	final public long getLastExecTime() {return last_execute_time;}
	
	final public void setProfileVersion(String p) {profile_version=p;}
	final public void setProfileGroup(String p) {profile_group=p;}
	final public void setProfileType(String p) {profile_type=p;}
	final public void setProfileEnabled(String p) {profile_enable=p;}
	final public void setProfileName(String p) {profile_name=p;}
	final public void setProfileUpdateTime(long t) {profile_update_time=t;}
	final public void setTaskRetrospecEnabled(String p) {retrospective_enable=p;}
	final public void setTaskRetrospecNumber(String p) {retrospective_number=p;}
	final public void setTimeType(String p) {time_date_time_type=p;}
	final public void setTimeDayOfTheWeek(String p) {time_day_of_the_week=p;}
	final public void setTimeDate(String p) {time_date=p;}
	final public void setTimeTime(String p) {time_time=p;}
	final public void setActionType(String p) {action_action_type=p;}
	final public void setActionActivityName(String p) {action_activity_name=p;}
	final public void setActionActivityPackageName(String p) {action_activity_package=p;}
	final public void setActionSoundFileName(String p) {action_sound_file_name=p;}
	final public void setTaskTriggerList(ArrayList<String> p) {action_trigger=p;}
	final public void setTaskActionList(ArrayList<String> p) {action_action=p;}
//	final public void setLastExecTime(long p) {last_execute_time=p;}
	
	final public boolean isTaskActive() {return task_active;}
	final public void setTaskActive(boolean p) {task_active=p;}

	final public String getActionSoundVolLeft() {return action_sound_vol_left;}
	final public String getActionSoundVolRight() {return action_sound_vol_right;}
	final public void setActionSoundVol(String l, String r) {
		action_sound_vol_left=l;
		action_sound_vol_right=r;
	}

	final public String getActionRingtoneType() {return action_ringtone_type;}
	final public String getActionRingtoneName() {return action_ringtone_name;}
	final public String getActionRingtonePath() {return action_ringtone_path;}
	final public String getActionRingtoneVolLeft() {return action_ringtone_vol_left;}
	final public String getActionRingtoneVolRight() {return action_ringtone_vol_right;}
	
	final public void setActionRingtoneType(String p) {action_ringtone_type=p;}
	final public void setActionRingtoneName(String p) {action_ringtone_name=p;}
	final public void setActionRingtonePath(String p) {action_ringtone_path=p;}
	final public void setActionRingtoneVol(String l, String r) {
		action_ringtone_vol_left=l;
		action_ringtone_vol_right=r;
	}
	
	final public int getActionRingtoneTypeInt() {
		int r_type=0;
		if (action_ringtone_type.equals(PROFILE_ACTION_RINGTONE_TYPE_ALERT) || 
				action_ringtone_type.equals(PROFILE_ACTION_RINGTONE_TYPE_ALARM))
			r_type=RingtoneManager.TYPE_ALARM;
		else if (action_ringtone_type.equals(PROFILE_ACTION_RINGTONE_TYPE_NOTIFICATION))
			r_type=RingtoneManager.TYPE_NOTIFICATION;
		else if (action_ringtone_type.equals(PROFILE_ACTION_RINGTONE_TYPE_RINGTONE))
			r_type=RingtoneManager.TYPE_RINGTONE;
		return r_type;
	}

	final public void setProfileErrorNotification(String p) {profile_error_notification=p;}
	final public String getProfileErrorNotification() {return profile_error_notification;}
	final public boolean isProfileErrorNotificationEnabled() {
		return profile_error_notification.equals("1");
	}

	@SuppressWarnings("unchecked")
	@Override
	final public void readExternal(ObjectInput obj_in) throws IOException, ClassNotFoundException{
		long sid=obj_in.readLong();
		if (serialVersionUID!=sid) {
			throw new IOException("serialVersionUID was not matched by saved UID");
		}

//		Init 		
		isSelected=false;
		task_active=false;
		profile_group_selected=false;
		filter_selected=true;
		
//		Common		
		profile_group=obj_in.readUTF();
		profile_type=obj_in.readUTF();
		profile_name=obj_in.readUTF();
		profile_enable=obj_in.readUTF();
		profile_group_active=obj_in.readInt();
		profile_update_time=obj_in.readLong();
		
//		Task	
		retrospective_enable=obj_in.readUTF();
		retrospective_number=obj_in.readUTF();
		profile_error_notification=obj_in.readUTF();
		
//		action_action=SerializeUtil.readListString(obj_in);
		action_action=(ArrayList<String>) SerializeUtil.readArrayList(obj_in);
		
//		action_trigger=SerializeUtil.readListString(obj_in);
		action_trigger=(ArrayList<String>) SerializeUtil.readArrayList(obj_in);

//		String t_action_action=obj_in.readUTF();
//		action_action=stringToStringList(t_action_action);
//		String t_action_trigger=obj_in.readUTF();
//		action_trigger=stringToStringList(t_action_trigger);

//		Timer	
		time_date_time_type=obj_in.readUTF();
		time_day_of_the_week=obj_in.readUTF();
		time_date=obj_in.readUTF();
		time_time=obj_in.readUTF();

//		Action	
		action_action_type=obj_in.readUTF();
		//ActionType=Android
		action_activity_name=obj_in.readUTF();
		action_activity_package=obj_in.readUTF();
		action_activity_data_type=obj_in.readUTF();
		action_activity_uri_data=obj_in.readUTF();
		
//		action_activity_extra_data_list=(ArrayList<ActivityExtraDataItem>) obj_in.readObject();
//		action_activity_extra_data_list=SerializeUtil.readListActivityExtraDataItem(obj_in);
		action_activity_extra_data_list=(ArrayList<ActivityExtraDataItem>) SerializeUtil.readArrayList(obj_in);
		
//ActionType=Music
		action_sound_file_name=obj_in.readUTF();
		action_sound_vol_left=obj_in.readUTF();
		action_sound_vol_right=obj_in.readUTF();
//ActionType=Ringtone
		action_ringtone_type=obj_in.readUTF();
		action_ringtone_name=obj_in.readUTF();
		action_ringtone_path=obj_in.readUTF();
		action_ringtone_vol_left=obj_in.readUTF();
		action_ringtone_vol_right=obj_in.readUTF();
//Compare
		action_compare_target=obj_in.readUTF();
		action_compare_type=obj_in.readUTF();
		action_compare_result_action=obj_in.readUTF();

//		action_compare_val=(String[]) obj_in.readObject();
		action_compare_val=SerializeUtil.readArrayString(obj_in);
		
//		String t_compare_val=obj_in.readUTF();
//		compare_val=stringToStringArray(t_compare_val);
		//Message
		action_message_type=obj_in.readUTF();
		action_message_text=obj_in.readUTF(); 
		action_message_use_vibration=obj_in.readInt();
		action_message_use_led=obj_in.readInt();
		action_message_led_color=obj_in.readUTF();
		//Action Time 
		action_time_type=obj_in.readUTF();
		action_time_target=obj_in.readUTF(); 
		//Action Task 
		action_task_type=obj_in.readUTF();
		action_task_target=obj_in.readUTF(); 
		//Action Wait
		action_wait_target=obj_in.readUTF();
		action_wait_timeout_value=obj_in.readUTF();
		action_wait_timeout_units=obj_in.readUTF(); 
		//Action Bean Shell Script
		action_bsh_script=obj_in.readUTF();
		//Action shell cmd
		action_shell_cmd=obj_in.readUTF();
		action_shell_cmd_with_su=obj_in.readBoolean();
	}

	@Override
	final public void writeExternal(ObjectOutput obj_out) throws IOException {
		obj_out.writeLong(serialVersionUID);
		
		obj_out.writeUTF(profile_group);
		obj_out.writeUTF(profile_type);
		obj_out.writeUTF(profile_name);
		obj_out.writeUTF(profile_enable);
		obj_out.writeInt(profile_group_active);
		obj_out.writeLong(profile_update_time);
		
//		Task	
		obj_out.writeUTF(retrospective_enable);
		obj_out.writeUTF(retrospective_number);
		obj_out.writeUTF(profile_error_notification);
		
//		SerializeUtil.writeListString(obj_out,action_action);
		SerializeUtil.writeArrayList(obj_out,action_action);
	
//		SerializeUtil.writeListString(obj_out,action_trigger);
		SerializeUtil.writeArrayList(obj_out,action_trigger);

//		Timer	
		obj_out.writeUTF(time_date_time_type);
		obj_out.writeUTF(time_day_of_the_week);
		obj_out.writeUTF(time_date);
		obj_out.writeUTF(time_time);

//		Action	
		obj_out.writeUTF(action_action_type);
		//ActionType=Android
		obj_out.writeUTF(action_activity_name);
		obj_out.writeUTF(action_activity_package);
		obj_out.writeUTF(action_activity_data_type);
		obj_out.writeUTF(action_activity_uri_data);
		
//		obj_out.writeObject(action_activity_extra_data_list);
//		SerializeUtil.writeListActivityExtraDataItem(obj_out,action_activity_extra_data_list);
		SerializeUtil.writeArrayList(obj_out,action_activity_extra_data_list);
		
		//ActionType=Music
		obj_out.writeUTF(action_sound_file_name);
		obj_out.writeUTF(action_sound_vol_left);
		obj_out.writeUTF(action_sound_vol_right);
		//ActionType=Ringtone
		obj_out.writeUTF(action_ringtone_type);
		obj_out.writeUTF(action_ringtone_name);
		obj_out.writeUTF(action_ringtone_path);
		obj_out.writeUTF(action_ringtone_vol_left);
		obj_out.writeUTF(action_ringtone_vol_right);
		//Compare
		obj_out.writeUTF(action_compare_target);
		obj_out.writeUTF(action_compare_type);
		obj_out.writeUTF(action_compare_result_action);

//		obj_out.writeObject(action_compare_val);
		SerializeUtil.writeArrayString(obj_out,action_compare_val);
		
//		String t_compare_val=stringArrayToString(compare_val);
//		obj_out.writeUTF(t_compare_val);
		//Message
		obj_out.writeUTF(action_message_type);
		obj_out.writeUTF(action_message_text); 
		obj_out.writeInt(action_message_use_vibration);
		obj_out.writeInt(action_message_use_led);
		obj_out.writeUTF(action_message_led_color);
		//Action Time 
		obj_out.writeUTF(action_time_type);
		obj_out.writeUTF(action_time_target); 
		//Action Task 
		obj_out.writeUTF(action_task_type);
		obj_out.writeUTF(action_task_target); 
		//Action Wait
		obj_out.writeUTF(action_wait_target);
		obj_out.writeUTF(action_wait_timeout_value);
		obj_out.writeUTF(action_wait_timeout_units);
		//Action Bean Shell Script
		obj_out.writeUTF(action_bsh_script);
		//Action Bean Shell Script
		obj_out.writeUTF(action_shell_cmd);
		obj_out.writeBoolean(action_shell_cmd_with_su);
	}
};

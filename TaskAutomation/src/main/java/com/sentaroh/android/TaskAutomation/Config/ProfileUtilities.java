package com.sentaroh.android.TaskAutomation.Config;

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
import static com.sentaroh.android.TaskAutomation.Config.QuickTaskConstants.QUICK_TASK_GROUP_NAME;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.sentaroh.android.TaskAutomation.CommonUtilities;
import com.sentaroh.android.TaskAutomation.Common.ActivityExtraDataItem;
import com.sentaroh.android.TaskAutomation.Common.ProfileListItem;

import android.util.Log;

public class ProfileUtilities {

	final static public ArrayList<String> buildBuiltinEventList() {
		ArrayList<String> bevt=new ArrayList<String>();
		bevt.add(BUILTIN_EVENT_BOOT_COMPLETED);
		bevt.add(BUILTIN_EVENT_WIFI_ON);
		bevt.add(BUILTIN_EVENT_WIFI_CONNECTED);
		bevt.add(BUILTIN_EVENT_WIFI_DISCONNECTED);
		bevt.add(BUILTIN_EVENT_WIFI_OFF);
		bevt.add(BUILTIN_EVENT_BLUETOOTH_ON);
		bevt.add(BUILTIN_EVENT_BLUETOOTH_CONNECTED);
		bevt.add(BUILTIN_EVENT_BLUETOOTH_DISCONNECTED);
		bevt.add(BUILTIN_EVENT_BLUETOOTH_OFF);
		bevt.add(BUILTIN_EVENT_PROXIMITY_DETECTED);
		bevt.add(BUILTIN_EVENT_PROXIMITY_UNDETECTED);
		bevt.add(BUILTIN_EVENT_LIGHT_DETECTED);
		bevt.add(BUILTIN_EVENT_LIGHT_UNDETECTED);
		bevt.add(BUILTIN_EVENT_SCREEN_UNLOCKED);
		bevt.add(BUILTIN_EVENT_SCREEN_LOCKED);
		bevt.add(BUILTIN_EVENT_POWER_SOURCE_CHANGED_AC);
		bevt.add(BUILTIN_EVENT_POWER_SOURCE_CHANGED_BATTERY);
		bevt.add(BUILTIN_EVENT_PHONE_CALL_STATE_IDLE);
		bevt.add(BUILTIN_EVENT_PHONE_CALL_STATE_OFF_HOOK);
		bevt.add(BUILTIN_EVENT_PHONE_CALL_STATE_RINGING);
		bevt.add(BUILTIN_EVENT_BATTERY_LEVEL_CHANGED);		
		bevt.add(BUILTIN_EVENT_BATTERY_FULLY_CHARGED);
		bevt.add(BUILTIN_EVENT_BATTERY_LEVEL_LOW);
		bevt.add(BUILTIN_EVENT_BATTERY_LEVEL_HIGH);
		bevt.add(BUILTIN_EVENT_BATTERY_LEVEL_CRITICAL);
		bevt.add(BUILTIN_EVENT_AIRPLANE_MODE_ON);
		bevt.add(BUILTIN_EVENT_AIRPLANE_MODE_OFF);
		bevt.add(BUILTIN_EVENT_MOBILE_NETWORK_CONNECTED);
		bevt.add(BUILTIN_EVENT_MOBILE_NETWORK_DISCONNECTED);
		Collections.sort(bevt);
		return bevt;
	};
	
	final static public ArrayList<ProfileListItem> deSerializeProfilelist(byte[] buf) {
//		long b_time=System.currentTimeMillis();
		ArrayList<ProfileListItem> prof_list=new ArrayList<ProfileListItem>();
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(buf); 
			ObjectInput in = new ObjectInputStream(bis);
			
			int sz=in.readInt();
			for (int i=0;i<sz;i++) {
				ProfileListItem pli=new ProfileListItem();
				pli.readExternal(in);
				prof_list.add(pli);
			}
			
//		    prof_list=(ArrayList<ProfileListItem>) in.readObject(); 
		    in.close(); 
		} catch (IOException e) {
			Log.v(APPLICATION_TAG, "deSerializeProfilelist error", e);
		} catch (ClassNotFoundException e) {
			Log.v(APPLICATION_TAG, "deSerializeProfilelist error", e);
		}
//		Log.v(APPLICATION_TAG, "deSerializeProfilelist elapsed time="+(System.currentTimeMillis()-b_time));
		return prof_list;
	};

	final static public byte[] serializeProfilelist(ArrayList<ProfileListItem> prof_list) {
//		long b_time=System.currentTimeMillis();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(1024*100); 
		byte[] buf=null; 
	    try { 
	    	ObjectOutput out = new ObjectOutputStream(bos);
	    	out.writeInt(prof_list.size());
	    	
	    	for (ProfileListItem pli : prof_list) {
	    		pli.writeExternal(out);
	    	}
	    	
//	    	out.writeObject(prof_list);
		    out.flush();
		    out.close();;
		    buf= bos.toByteArray(); 
	    } catch(IOException e) { 
	    	Log.v(APPLICATION_TAG, "serializeProfilelist error", e); 
		}
//		Log.v(APPLICATION_TAG, "serializeProfilelist elapsed time="+
//				(System.currentTimeMillis()-b_time)+", size="+buf.length);
		return buf;
	};

	final static public void copyProfile(CommonUtilities util,
			AdapterProfileList pfa, ProfileListItem from_pli, 
			String new_grp, String new_name) {
		ProfileListItem n_pli=from_pli.clone();
		boolean grp_act=isProfileGroupActive(util,pfa,new_grp);
		n_pli.setProfileName(new_name);
		n_pli.setProfileGroup(new_grp);
		n_pli.setProfileGroupActivated(grp_act);
		pfa.addProfItem(n_pli);
		pfa.sort();
		pfa.updateShowList();
	};
	final static public void renameProfile(
			CommonUtilities util,
			AdapterProfileList pfa, ProfileListItem from_pli, String new_name) {
		pfa.removeProfItem(from_pli);
		from_pli.setProfileName(new_name);
		pfa.addProfItem(from_pli);
		pfa.sort();
		pfa.updateShowList();
	};
	
	final static public ProfileListItem getProfileListItem(
			CommonUtilities util,
			AdapterProfileList tpfa, String prof_grp, String prof_type, String prof_name) {
		ProfileListItem result=null;
		for (int i=0;i<tpfa.getProfItemCount();i++) {
			if (tpfa.getProfItem(i).getProfileGroup().equals(prof_grp) &&
					tpfa.getProfItem(i).getProfileType().equals(prof_type) &&
					tpfa.getProfItem(i).getProfileName().equals(prof_name)) {
				result=tpfa.getProfItem(i);
			}
		}
		return result;
	}

	final static public ProfileListItem getProfileListItemFromAll(
			CommonUtilities util,
			AdapterProfileList tpfa, String prof_grp, String prof_type, String prof_name) {
		ProfileListItem result=null;
		for (int i=0;i<tpfa.getDataListCount();i++)
			if (tpfa.getDataListItem(i).getProfileGroup().equals(prof_grp) &&
					tpfa.getDataListItem(i).getProfileType().equals(prof_type) &&
					tpfa.getDataListItem(i).getProfileName().equals(prof_name)) {
				result=tpfa.getDataListItem(i);
			}
		return result;
	}

	final static public ArrayList<String> createProfileGroupList(CommonUtilities util,
			AdapterProfileList pfa) {
		ArrayList<String> gl=new ArrayList<String>();
		String c_loc="";
		if (pfa.getDataListCount()>0) {
			for (int i=0;i<pfa.getDataListCount();i++) {
				if (!pfa.getDataListItem(i).getProfileGroup().equals(c_loc)) {
					c_loc=pfa.getDataListItem(i).getProfileGroup();
					gl.add(c_loc);
				}
			}
			Collections.sort(gl);
		}
		return gl;
	};

	final static public void deleteProfileGroup(CommonUtilities util,
			AdapterProfileList pfa, String del_grp) {
		deleteProfileGroup(util, pfa.getDataList(),del_grp);
	};
	final static public void deleteProfileGroup(
			CommonUtilities util,
			ArrayList<ProfileListItem> prof_list, String del_grp) {
		util.addDebugMsg(2, "I", "deleteProfileGroup entered", ", del=", del_grp);
		for (int i=prof_list.size()-1;i>=0;i--) {
			if (prof_list.get(i).getProfileGroup().equals(del_grp)) {
				prof_list.remove(i);
			}
		}
	};
	
	final static public boolean isProfileGroupExists(CommonUtilities util,
			AdapterProfileList pfla,String grp) {
		return isProfileGroupExists(util,pfla.getDataList(),grp);
	};
	final static public boolean isProfileGroupExists(CommonUtilities util,
			ArrayList<ProfileListItem> pfli, String grp) {
		boolean result=false;
		for (int i=0;i<pfli.size();i++) 
			if (pfli.get(i).getProfileGroup().equals(grp)) {
				result=true;
				break;
			}
		return result;
	};

	final static public boolean isProfileGroupActive(CommonUtilities util,
			AdapterProfileList pfla,String grp) {
		return isProfileGroupActive(util,pfla.getDataList(),grp);
	};
	final static public boolean isProfileGroupActive(CommonUtilities util,
			ArrayList<ProfileListItem> pfli,String grp) {
		boolean result=false;
		for (int i=0;i<pfli.size();i++) 
			if (pfli.get(i).getProfileGroup().equals(grp)) {
				result=pfli.get(i).isProfileGroupActivated();
				break;
			}
		return result;
	};
	
	final static public boolean setProfileGroupActive(CommonUtilities util,
			AdapterProfileList pfla,String grp, boolean active) {
		return setProfileGroupActive(util,pfla.getDataList(),grp,active);
	};
	final static public boolean setProfileGroupActive(CommonUtilities util,
			ArrayList<ProfileListItem> pfli,String grp, boolean active) {
		boolean result=false;
		for (int i=0;i<pfli.size();i++) {
			if (pfli.get(i).getProfileGroup().equals(grp)) {
				pfli.get(i).setProfileGroupActivated(active);
				pfli.get(i).setProfileUpdateTime(System.currentTimeMillis());
				result=true;
			}
		}
		return result;
	};

	final static public void removeDummyProfile(AdapterProfileList pfla, String grp) {
		for (int i=pfla.getProfItemCount()-1;i>=0;i--) {
			ProfileListItem tpli=pfla.getProfItem(i);
			if (tpli.getProfileType().equals("")) {
				pfla.removeProfItem(i);
			}
		}
	};
	
	final static public boolean isValidProfileName(CommonUtilities util,
			AdapterProfileList tpfa,
			String n_grp, String n_type, String n_name) {
		boolean result=true;
		String key="";
		for (int i=0;i<tpfa.getDataListCount();i++) {
			key=tpfa.getDataListItem(i).getProfileGroup()+
					tpfa.getDataListItem(i).getProfileType()+
					tpfa.getDataListItem(i).getProfileName();
			if (key.equals(n_grp+n_type+n_name)) {
				result=false;
				break;
			}
		}
		util.addDebugMsg(1,"I", "isValidProfileName result="+result+
				", Group="+n_grp+", Type="+n_type+", name="+n_name);
		return result;
	};

	final static public boolean verifyProfileIntegrity(CommonUtilities util, boolean set, 
			AdapterProfileList tpfa, String grp) {
		boolean ic_found=false;
		for (int prof_idx=0;prof_idx<tpfa.getDataListCount();prof_idx++) {
			ProfileListItem tpli=tpfa.getDataListItem(prof_idx);
			if (tpli.isProfileEnabled() &&
				tpli.getProfileType().equals(PROFILE_TYPE_TASK) &&
				tpli.getProfileGroup().equals(grp)) {
				util.addDebugMsg(2, "I", "verifyProfileIntegrity Task Profile"+
						", Group="+tpli.getProfileGroup()+
						", Name="+tpli.getProfileName()+
						", Enable="+tpli.isProfileEnabled()+
						", Trigger="+tpli.getTaskTriggerList()+
						", Action="+tpli.getTaskActionList()
						);
				ArrayList<String> tal=tpli.getTaskActionList();
				boolean disable=false;
				for (int act_idx=0;act_idx<tal.size();act_idx++) {
					if (!tal.get(act_idx).startsWith(BUILTIN_PREFIX)) {
						ProfileListItem ap=
								getProfileListItem(util,tpfa,tpli.getProfileGroup(), 
									PROFILE_TYPE_ACTION,tal.get(act_idx));
						if (ap!=null) {
							util.addDebugMsg(2, "I", "verifyProfileIntegrity Action Profile"+
									", Group="+ap.getProfileGroup()+
									", Name="+ap.getProfileName()+
									", Enable="+ap.isProfileEnabled()+
									", ActionType="+ap.getActionType()
									);
							if (!ap.isProfileEnabled()) {
								disable=true;
								util.addDebugMsg(1, "I", "Task profile is disabled, because Action profile was disabled."+
										", Group="+tpli.getProfileGroup()+
										", Name="+tpli.getProfileName());
								break;
							}
						} else {
							disable=true;
							break;
						}
					}					
				}
				if (!tpli.getTaskTriggerList().get(0).equals(TRIGGER_EVENT_TASK) && 
						!tpli.getTaskTriggerList().get(0).startsWith(BUILTIN_PREFIX)) {
					ProfileListItem tp= getProfileListItem(util,tpfa,tpli.getProfileGroup(), 
							PROFILE_TYPE_TIME,tpli.getTaskTriggerList().get(0));
					if (tp==null) {
						disable=true;
						util.addDebugMsg(1, "I", "Task profile is disabled, because Time profile was does not exists."+
								", Group="+tpli.getProfileGroup()+
								", Name="+tpli.getProfileName());
					} else {
						util.addDebugMsg(2, "I", "verifyProfileIntegrity Time Profile"+
							", Group="+tp.getProfileGroup()+
							", Name="+tp.getProfileName()+
							", Enable="+tp.isProfileEnabled());
						if (!tp.isProfileEnabled()) {
							util.addDebugMsg(1, "I", "Task profile is disabled, because Time profile was disabled."+
									", Group="+tpli.getProfileGroup()+
									", Name="+tpli.getProfileName());
							disable=true;
						}
					}
				} else {
					util.addDebugMsg(2, "I", "verifyProfileIntegrity Trigger="+tpli.getTaskTriggerList().get(0));
				}
				if (disable) {
					if (set) {
						tpli.setProfileEnabled(PROFILE_DISABLED);
//						tpfa.replaceProfItem(prof_idx, tpli);
						ic_found=true;
					}
				}
			}
		}
		return ic_found;
	};
	
	final static public boolean isQuickTaskProfileExisted(
			CommonUtilities util, AdapterProfileList pfa) {
		boolean result=false;
		for (int i=0;i<pfa.getDataListCount();i++) 
			if (pfa.getDataListItem(i).getProfileGroup().equals(QUICK_TASK_GROUP_NAME)) {
				result=true;
				break;
			}
		return result;
	};

	final static public boolean isQuickTaskProfileActivated(
			CommonUtilities util,
			AdapterProfileList pfa) {
		return isQuickTaskProfileActivated(util,pfa.getDataList());
	};
    final static public boolean isQuickTaskProfileActivated(
    		CommonUtilities util,
    		ArrayList<ProfileListItem> prof_list) {
		boolean result=false;
		for (int i=0;i<prof_list.size();i++) 
			if (prof_list.get(i).getProfileGroup().equals(QUICK_TASK_GROUP_NAME)) {
				result=prof_list.get(i).isProfileGroupActivated();
				break;
			}
		return result;
	};
	
	final static public void setQuickTaskProfileActivated(
			CommonUtilities util,
			AdapterProfileList pfa, boolean qa) {
		setQuickTaskProfileActivated(util,pfa.getDataList(), qa);
	};
	final static public void setQuickTaskProfileActivated(
			CommonUtilities util,
			ArrayList<ProfileListItem> prof_list, boolean qa) {
		if (isQuickTaskProfileActivated(util,prof_list)) return;
		for (int i=0;i<prof_list.size();i++) 
			if (prof_list.get(i).getProfileGroup().equals(QUICK_TASK_GROUP_NAME)) {
				prof_list.get(i).setProfileGroupActivated(qa);
			}
	};
	
    final static public void sortProfileArrayList(CommonUtilities util,
    		ArrayList<ProfileListItem> array_list) {
		Collections.sort(array_list, new Comparator<ProfileListItem>(){
	        @Override
	        public int compare(ProfileListItem s1, ProfileListItem s2){
	        	
				String c_t="0",n_t="0";
				if (s1.getProfileType().equals(PROFILE_TYPE_TASK)) c_t="0";
				else if (s1.getProfileType().equals(PROFILE_TYPE_ACTION)) c_t="1";
				else if (s1.getProfileType().equals(PROFILE_TYPE_TIME)) c_t="2";
				if (s2.getProfileType().equals(PROFILE_TYPE_TASK)) n_t="0";
				else if (s2.getProfileType().equals(PROFILE_TYPE_ACTION)) n_t="1";
				else if (s2.getProfileType().equals(PROFILE_TYPE_TIME)) n_t="2";
				
				if (!s1.getProfileGroup().equals(s2.getProfileGroup())) {
					return s1.getProfileGroup().compareToIgnoreCase(s2.getProfileGroup()) ;
				}
				if (!c_t.equals(n_t)) {
					return c_t.compareToIgnoreCase(n_t) ;
				}
				return s1.getProfileName().compareToIgnoreCase(s2.getProfileName()) ;
	        }
		});
	};

	final static public void parseProfileList(String pl, String grp_selection, 
			ArrayList<ProfileListItem> task,
			ArrayList<ProfileListItem> time, ArrayList<ProfileListItem> action) {

		if (pl.startsWith("SETTINGS")) return; //ignore settings entry
		
		String[] tmp_ver=pl.split("\t");
		String pl_prof_ver=tmp_ver[0];
		String n_pl=pl.replace(pl_prof_ver+"\t", "");
		
		if (pl_prof_ver.equals(PROFILE_VERSION_V001)) parseProfileListV0001(n_pl,grp_selection,task,time,action);
		else if (pl_prof_ver.equals(PROFILE_VERSION_V002)) parseProfileListV0002(n_pl,grp_selection,task,time,action);
		else if (pl_prof_ver.equals(PROFILE_VERSION_V003)) parseProfileListV0003(n_pl,grp_selection,task,time,action);
		else if (pl_prof_ver.equals(PROFILE_VERSION_V004)) parseProfileListV0004(n_pl,grp_selection,task,time,action);
		else if (pl_prof_ver.equals(PROFILE_VERSION_V005)) parseProfileListV0005(n_pl,grp_selection,task,time,action);
		else parseProfileListV0001(pl,grp_selection,task,time,action);
	};
	
	final static public void parseProfileListV0001(String pl, String grp_selection, 
			ArrayList<ProfileListItem> task,
			ArrayList<ProfileListItem> time, ArrayList<ProfileListItem> action) {
		
		String[] tmp_pl=pl.split("\t");// {"type","name","active",options...};
		String[] parm= new String[50];
		for (int i=0;i<50;i++) parm[i]="";
		for (int i=0;i<tmp_pl.length;i++) {
			if (tmp_pl[i]==null) parm[i]="";
			else {
				if (tmp_pl[i]==null) parm[i]="";
				else parm[i]=tmp_pl[i];
			}
		}
		if (grp_selection.equals("") || parm[1].equals(grp_selection)) {
			ProfileListItem tpli= new ProfileListItem();
			long put=(System.currentTimeMillis()/1000)*1000;
			boolean grp_activated=false;
			if (parm[1].equals(PROFILE_GROUP_ACTIVATED)) grp_activated=true;
			if (parm[2].equals(PROFILE_TYPE_TASK)) {//Task
				ArrayList<String> act=new ArrayList<String>();
				ArrayList<String> trig=new ArrayList<String>();
				String[] act_string = parm[8].split("\u0001");
				String[] trig_string = parm[9].split("\u0001");
				for (int i=0;i<act_string.length;i++) 
					if (act_string[i]!=null && !act_string[i].equals("")) 
						act.add(act_string[i]);
				for (int i=0;i<trig_string.length;i++) 
					if (trig_string[i]!=null && !trig_string[i].equals("")) 
						trig.add(trig_string[i]);

				tpli.setTaskEntry(
						PROFILE_VERSION_CURRENT,
						parm[0],//Group
						grp_activated,
						put,
						parm[2],//Type
						parm[3],//Name
						parm[4],//Active
						parm[5],//RetrospectiveEnable
						parm[6],//RetrospectiveNumber
						parm[7],//Notification
						act,//action					
						trig//Trigger
						);
				task.add(tpli);
			} 
			if (parm[2].equals(PROFILE_TYPE_TIME)) {//Time
				tpli.setTimeEventEntry(
						PROFILE_VERSION_CURRENT,
						parm[0],//Group
						grp_activated,
						put,
						parm[2],//Type
						parm[3],//Name
						parm[4],//Active
						parm[5],//DateTimeType
						parm[6],//Execution day of the week
						parm[7],//Execution date
						parm[8] //Execution time
						);
				time.add(tpli);
			}
			if (parm[2].equals(PROFILE_TYPE_ACTION)) {//Action
				if (parm[5].equals(PROFILE_ACTION_TYPE_ACTIVITY)) {
					ArrayList<ActivityExtraDataItem>aed_list=new ArrayList<ActivityExtraDataItem>();
					if (!parm[10].equals("")) {
						ActivityExtraDataItem aedi=new ActivityExtraDataItem();
						aedi.data_type=PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_STRING;
						aedi.key_value=parm[10];
						aedi.data_value=parm[12];
						aed_list.add(aedi);
					}
					if (!parm[13].equals("")) {
						ActivityExtraDataItem aedi=new ActivityExtraDataItem();
						aedi.data_type=PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_STRING;
						aedi.key_value=parm[13];
						aedi.data_value=parm[15];
						aed_list.add(aedi);
					}
					if (!parm[16].equals("")) {
						ActivityExtraDataItem aedi=new ActivityExtraDataItem();
						aedi.data_type=PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_STRING;
						aedi.key_value=parm[16];
						aedi.data_value=parm[18];
						aed_list.add(aedi);
					}
					tpli.setActionAndroidEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[2],//Type
							parm[3],//Name
							parm[4],//Active
							parm[6],//Activity name name
							parm[7],//package name
							parm[8],//data type
							parm[9],//uri data
							aed_list
							);
					action.add(tpli);
				} else if (parm[5].equals(PROFILE_ACTION_TYPE_MUSIC)) {
					tpli.setActionMusicEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[2],//Type
							parm[3],//Name
							parm[4],//Active
							parm[6],//File name
							parm[7],//Volume left
							parm[8]//Volume right
							);
					action.add(tpli);
				} else if (parm[5].equals(PROFILE_ACTION_TYPE_RINGTONE)) {
					tpli.setActionRingtoneEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[2],//Type
							parm[3],//Name
							parm[4],//Active
							parm[6],//Ringtone type
							parm[7],//Ringtone name
							parm[8],//Ringtone path
							parm[9],//Volume left
							parm[10]//Volume right
							);
					action.add(tpli);
				} else if (parm[5].equals(PROFILE_ACTION_TYPE_COMPARE)) {
					String[] c_v=tpli.getActionCompareValue();
					c_v[0]=parm[8];
					c_v[1]=parm[9];
					tpli.setActionCompareEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[2],//Type
							parm[3],//Name
							parm[4],//Active
							parm[6],//Compare target
							parm[7],//Compare type
							c_v,
							parm[10]//Compare result action
							);
					action.add(tpli);
				} else if (parm[5].equals(PROFILE_ACTION_TYPE_MESSAGE)) {
					boolean vib_used=false, led_used=false;
					if (parm[8].equals("1")) vib_used=true;
					if (parm[9].equals("1")) led_used=true;
					tpli.setActionMessageEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[2],//Type
							parm[3],//Name
							parm[4],//Active
							parm[6],//Message type
							parm[7],//Message text
							vib_used,
							led_used,
							parm[10]//Message led color
							);
					action.add(tpli);
				}
			} 
		}
	};

	final static public void parseProfileListV0002(String pl, String grp_selection, 
			ArrayList<ProfileListItem> task,
			ArrayList<ProfileListItem> time, ArrayList<ProfileListItem> action) {
		
		String[] tmp_pl=pl.split("\t");// {"type","name","active",options...};
		String[] parm= new String[50];
		for (int i=0;i<50;i++) parm[i]="";
		for (int i=0;i<tmp_pl.length;i++) {
			if (tmp_pl[i]==null) parm[i]="";
			else {
				if (tmp_pl[i]==null) parm[i]="";
				else parm[i]=tmp_pl[i];
			}
		}
		long put=Long.valueOf(parm[2]);
		if (grp_selection.equals("") || parm[1].equals(grp_selection)) {
			ProfileListItem tpli= new ProfileListItem();
			boolean grp_activated=false;
			if (parm[1].equals(PROFILE_GROUP_ACTIVATED)) grp_activated=true;
			if (parm[3].equals(PROFILE_TYPE_TASK)) {//Task
				ArrayList<String> act=new ArrayList<String>();
				ArrayList<String> trig=new ArrayList<String>();
				String[] act_string = parm[9].split("\u0001");
				String[] trig_string = parm[10].split("\u0001");
				for (int i=0;i<act_string.length;i++) 
					if (act_string[i]!=null && !act_string[i].equals("")) 
						act.add(act_string[i]);
				for (int i=0;i<trig_string.length;i++) 
					if (trig_string[i]!=null && !trig_string[i].equals("")) 
						trig.add(trig_string[i]);

				tpli.setTaskEntry(
						PROFILE_VERSION_CURRENT,
						parm[0],//Group
						grp_activated,
						put,
						parm[3],//Type
						parm[4],//Name
						parm[5],//Active
						parm[6],//RetrospectiveEnable
						parm[7],//RetrospectiveNumber
						parm[8],//Notification
						act,//action					
						trig//Trigger
						);
				task.add(tpli);
			} 
			if (parm[3].equals(PROFILE_TYPE_TIME)) {//Time
				tpli.setTimeEventEntry(
						PROFILE_VERSION_CURRENT,
						parm[0],//Group
						grp_activated,
						put,
						parm[3],//Type
						parm[4],//Name
						parm[5],//Active
						parm[6],//DateTimeType
						parm[7],//Execution day of the week
						parm[8],//Execution date
						parm[9] //Execution time
						);
				time.add(tpli);
			}
			if (parm[3].equals(PROFILE_TYPE_ACTION)) {//Action
				if (parm[6].equals(PROFILE_ACTION_TYPE_ACTIVITY)) {
					ArrayList<ActivityExtraDataItem>aed_list=new ArrayList<ActivityExtraDataItem>();
					if (!parm[11].equals("")) {
						ActivityExtraDataItem aedi=new ActivityExtraDataItem();
						aedi.data_type=PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_STRING;
						aedi.key_value=parm[11];
						aedi.data_value=parm[13];
						aed_list.add(aedi);
					}
					if (!parm[14].equals("")) {
						ActivityExtraDataItem aedi=new ActivityExtraDataItem();
						aedi.data_type=PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_STRING;
						aedi.key_value=parm[14];
						aedi.data_value=parm[16];
						aed_list.add(aedi);
					}
					if (!parm[17].equals("")) {
						ActivityExtraDataItem aedi=new ActivityExtraDataItem();
						aedi.data_type=PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_STRING;
						aedi.key_value=parm[17];
						aedi.data_value=parm[19];
						aed_list.add(aedi);
					}
					tpli.setActionAndroidEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[3],//Type
							parm[4],//Name
							parm[5],//Active
							parm[7],//Activity name name
							parm[8],//package name
							parm[9],//data type
							parm[10],//uri data
							aed_list
							);
					action.add(tpli);
				} else if (parm[6].equals(PROFILE_ACTION_TYPE_MUSIC)) {
					tpli.setActionMusicEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[3],//Type
							parm[4],//Name
							parm[5],//Active
							parm[7],//File name
							parm[8],//Volume left
							parm[9]//Volume right
							);
					action.add(tpli);
				} else if (parm[6].equals(PROFILE_ACTION_TYPE_RINGTONE)) {
					tpli.setActionRingtoneEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[3],//Type
							parm[4],//Name
							parm[5],//Active
							parm[7],//Ringtone type
							parm[8],//Ringtone name
							parm[9],//Ringtone path
							parm[10],//Volume left
							parm[11]//Volume right
							);
					action.add(tpli);
				} else if (parm[6].equals(PROFILE_ACTION_TYPE_COMPARE)) {
					String[] c_v=tpli.getActionCompareValue();
					if (c_v==null) c_v=new String[2];
					c_v[0]=parm[9];
					c_v[1]=parm[10];
					tpli.setActionCompareEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[3],//Type
							parm[4],//Name
							parm[5],//Active
							parm[7],//Compare target
							parm[8],//Compare type
							c_v,
							parm[11]//Compare result action
							);
					action.add(tpli);
				} else if (parm[6].equals(PROFILE_ACTION_TYPE_MESSAGE)) {
					boolean vib_used=false, led_used=false;
					if (parm[9].equals("1")) vib_used=true;
					if (parm[10].equals("1")) led_used=true;
					tpli.setActionMessageEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[3],//Type
							parm[4],//Name
							parm[5],//Active
							parm[7],//Message type
							parm[8],//Message text
							vib_used,
							led_used,
							parm[11]//Message led color
							);
					action.add(tpli);
				} else if (parm[6].equals(PROFILE_ACTION_TYPE_TIME)) {
					tpli.setActionTimeEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[3],//Type
							parm[4],//Name
							parm[5],//Active
							parm[7],//Time type
							parm[8] //Time target
							);
					action.add(tpli);
				} else if (parm[6].equals(PROFILE_ACTION_TYPE_TASK)) {
					tpli.setActionTaskEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[3],//Type
							parm[4],//Name
							parm[5],//Active
							parm[7],//Task type
							parm[8] //Task target
							);
					action.add(tpli);
				}
			} 
		}
	};

	final static public void parseProfileListV0003(String pl, String grp_selection, 
			ArrayList<ProfileListItem> task,
			ArrayList<ProfileListItem> time, ArrayList<ProfileListItem> action) {
		
		String[] tmp_pl=pl.split("\t");// {"type","name","active",options...};
		String[] parm= new String[50];
		for (int i=0;i<50;i++) parm[i]="";
		for (int i=0;i<tmp_pl.length;i++) {
			if (tmp_pl[i]==null) parm[i]="";
			else {
				if (tmp_pl[i]==null) parm[i]="";
				else parm[i]=tmp_pl[i];
			}
		}
		long put=Long.valueOf(parm[2]);
		if (grp_selection.equals("") || parm[1].equals(grp_selection)) {
			ProfileListItem tpli= new ProfileListItem();
			boolean grp_activated=false;
			if (parm[1].equals(PROFILE_GROUP_ACTIVATED)) grp_activated=true;
			if (parm[3].equals(PROFILE_TYPE_TASK)) {//Task
				ArrayList<String> act=new ArrayList<String>();
				ArrayList<String> trig=new ArrayList<String>();
				String[] act_string = parm[9].split("\u0001");
				String[] trig_string = parm[10].split("\u0001");
				for (int i=0;i<act_string.length;i++) 
					if (act_string[i]!=null && !act_string[i].equals("")) 
						act.add(act_string[i]);
				for (int i=0;i<trig_string.length;i++) 
					if (trig_string[i]!=null && !trig_string[i].equals("")) 
						trig.add(trig_string[i]);

				tpli.setTaskEntry(
						PROFILE_VERSION_CURRENT,
						parm[0],//Group
						grp_activated,
						put,
						parm[3],//Type
						parm[4],//Name
						parm[5],//Active
						parm[6],//RetrospectiveEnable
						parm[7],//RetrospectiveNumber
						parm[8],//Notification
						act,//action					
						trig//Trigger
						);
				task.add(tpli);
			} 
			if (parm[3].equals(PROFILE_TYPE_TIME)) {//Time
				tpli.setTimeEventEntry(
						PROFILE_VERSION_CURRENT,
						parm[0],//Group
						grp_activated,
						put,
						parm[3],//Type
						parm[4],//Name
						parm[5],//Active
						parm[6],//DateTimeType
						parm[7],//Execution day of the week
						parm[8],//Execution date
						parm[9] //Execution time
						);
				time.add(tpli);
			}
			if (parm[3].equals(PROFILE_TYPE_ACTION)) {//Action
				if (parm[6].equals(PROFILE_ACTION_TYPE_ACTIVITY)) {
					ArrayList<ActivityExtraDataItem> aed_list=new ArrayList<ActivityExtraDataItem>();
					String[] aed_list_string = parm[11].split("\u0001");
					for (int i=0;i<aed_list_string.length;i++) {
						if (aed_list_string[i]!=null && !aed_list_string[i].equals("")) {
							String[] p_str=aed_list_string[i].split("\u0002");
							ActivityExtraDataItem aedi=new ActivityExtraDataItem();
							aedi.key_value=p_str[0];
							aedi.data_type=p_str[1];
							aedi.data_value_array=p_str[2];
							if (p_str.length==4) aedi.data_value=p_str[3];
							else aedi.data_value="";
							aed_list.add(aedi);
						}
					}
					tpli.setActionAndroidEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[3],//Type
							parm[4],//Name
							parm[5],//Active
							parm[7],//Activity name name
							parm[8],//package name
							parm[9],//data type
							parm[10],//uri data
							aed_list
							);
					action.add(tpli);
				} else if (parm[6].equals(PROFILE_ACTION_TYPE_MUSIC)) {
					tpli.setActionMusicEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[3],//Type
							parm[4],//Name
							parm[5],//Active
							parm[7],//File name
							parm[8],//Volume left
							parm[9]//Volume right
							);
					action.add(tpli);
				} else if (parm[6].equals(PROFILE_ACTION_TYPE_RINGTONE)) {
					tpli.setActionRingtoneEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[3],//Type
							parm[4],//Name
							parm[5],//Active
							parm[7],//Ringtone type
							parm[8],//Ringtone name
							parm[9],//Ringtone path
							parm[10],//Volume left
							parm[11]//Volume right
							);
					action.add(tpli);
				} else if (parm[6].equals(PROFILE_ACTION_TYPE_COMPARE)) {
					String[] c_v=tpli.getActionCompareValue();
					c_v[0]=parm[9];
					c_v[1]=parm[10];
					tpli.setActionCompareEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[3],//Type
							parm[4],//Name
							parm[5],//Active
							parm[7],//Compare target
							parm[8],//Compare type
							c_v,
							parm[11]//Compare result action
							);
					action.add(tpli);
				} else if (parm[6].equals(PROFILE_ACTION_TYPE_MESSAGE)) {
					boolean vib_used=false, led_used=false;
					if (parm[9].equals("1")) vib_used=true;
					if (parm[10].equals("1")) led_used=true;
					tpli.setActionMessageEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[3],//Type
							parm[4],//Name
							parm[5],//Active
							parm[7],//Message type
							parm[8],//Message text
							vib_used,
							led_used,
							parm[11]//Message led color
							);
					action.add(tpli);
				} else if (parm[6].equals(PROFILE_ACTION_TYPE_TIME)) {
					tpli.setActionTimeEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[3],//Type
							parm[4],//Name
							parm[5],//Active
							parm[7],//Time type
							parm[8] //Time target
							);
					action.add(tpli);
				} else if (parm[6].equals(PROFILE_ACTION_TYPE_TASK)) {
					tpli.setActionTaskEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[3],//Type
							parm[4],//Name
							parm[5],//Active
							parm[7],//Task type
							parm[8] //Task target
							);
					action.add(tpli);
				}
			} 
		}
	};

	final static public void parseProfileListV0004(String pl, String grp_selection, 
			ArrayList<ProfileListItem> task,
			ArrayList<ProfileListItem> time, ArrayList<ProfileListItem> action) {
		
		String[] tmp_pl=pl.split("\t");// {"type","name","active",options...};
		String[] parm= new String[50];
		for (int i=0;i<50;i++) parm[i]="";
		for (int i=0;i<tmp_pl.length;i++) {
			if (tmp_pl[i]==null) parm[i]="";
			else {
				if (tmp_pl[i]==null) parm[i]="";
				else parm[i]=tmp_pl[i];
			}
		}
		long put=Long.valueOf(parm[2]);
		if (grp_selection.equals("") || parm[1].equals(grp_selection)) {
			ProfileListItem tpli= new ProfileListItem();
			boolean grp_activated=false;
			if (parm[1].equals(PROFILE_GROUP_ACTIVATED)) grp_activated=true;
			if (parm[3].equals(PROFILE_TYPE_TASK)) {//Task
				ArrayList<String> act=new ArrayList<String>();
				ArrayList<String> trig=new ArrayList<String>();
				String[] act_string = parm[9].split("\u0001");
				String[] trig_string = parm[10].split("\u0001");
				for (int i=0;i<act_string.length;i++) 
					if (act_string[i]!=null && !act_string[i].equals("")) 
						act.add(act_string[i]);
				for (int i=0;i<trig_string.length;i++) 
					if (trig_string[i]!=null && !trig_string[i].equals("")) 
						trig.add(trig_string[i]);

				tpli.setTaskEntry(
						PROFILE_VERSION_CURRENT,
						parm[0],//Group
						grp_activated,
						put,
						parm[3],//Type
						parm[4],//Name
						parm[5],//Active
						parm[6],//RetrospectiveEnable
						parm[7],//RetrospectiveNumber
						parm[8],//Notification
						act,//action					
						trig//Trigger
						);
				task.add(tpli);
			} 
			if (parm[3].equals(PROFILE_TYPE_TIME)) {//Time
				tpli.setTimeEventEntry(
						PROFILE_VERSION_CURRENT,
						parm[0],//Group
						grp_activated,
						put,
						parm[3],//Type
						parm[4],//Name
						parm[5],//Active
						parm[6],//DateTimeType
						parm[7],//Execution day of the week
						parm[8],//Execution date
						parm[9] //Execution time
						);
				time.add(tpli);
			}
			if (parm[3].equals(PROFILE_TYPE_ACTION)) {//Action
				if (parm[6].equals(PROFILE_ACTION_TYPE_ACTIVITY)) {
					ArrayList<ActivityExtraDataItem> aed_list=new ArrayList<ActivityExtraDataItem>();
					String[] aed_list_string = parm[11].split("\u0001");
					for (int i=0;i<aed_list_string.length;i++) {
						if (aed_list_string[i]!=null && !aed_list_string[i].equals("")) {
							String[] p_str=aed_list_string[i].split("\u0002");
							ActivityExtraDataItem aedi=new ActivityExtraDataItem();
							aedi.key_value=p_str[0];
							aedi.data_type=p_str[1];
							aedi.data_value_array=p_str[2];
							if (p_str.length==4) aedi.data_value=p_str[3];
							else aedi.data_value="";
							aed_list.add(aedi);
						}
					}
					tpli.setActionAndroidEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[3],//Type
							parm[4],//Name
							parm[5],//Active
							parm[7],//Activity name name
							parm[8],//package name
							parm[9],//data type
							parm[10],//uri data
							aed_list
							);
					action.add(tpli);
				} else if (parm[6].equals(PROFILE_ACTION_TYPE_MUSIC)) {
					tpli.setActionMusicEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[3],//Type
							parm[4],//Name
							parm[5],//Active
							parm[7],//File name
							parm[8],//Volume left
							parm[9]//Volume right
							);
					action.add(tpli);
				} else if (parm[6].equals(PROFILE_ACTION_TYPE_RINGTONE)) {
					tpli.setActionRingtoneEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[3],//Type
							parm[4],//Name
							parm[5],//Active
							parm[7],//Ringtone type
							parm[8],//Ringtone name
							parm[9],//Ringtone path
							parm[10],//Volume left
							parm[11]//Volume right
							);
					action.add(tpli);
				} else if (parm[6].equals(PROFILE_ACTION_TYPE_COMPARE)) {
					String n_parm=parm[9].replaceAll("\u0003", "\u00a0\u0003");
					String[] t_cv=n_parm.split("\u0003");
					String[]n_cv=new String[20];
					for (int i=0;i<t_cv.length;i++) n_cv[i]=t_cv[i].substring(0,t_cv[i].length()-1);
					tpli.setActionCompareEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[3],//Type
							parm[4],//Name
							parm[5],//Active
							parm[7],//Compare target
							parm[8],//Compare type
							n_cv,
							parm[10]//Compare result action
							);
					action.add(tpli);
				} else if (parm[6].equals(PROFILE_ACTION_TYPE_MESSAGE)) {
					boolean vib_used=false, led_used=false;
					if (parm[9].equals("1")) vib_used=true;
					if (parm[10].equals("1")) led_used=true;
					tpli.setActionMessageEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[3],//Type
							parm[4],//Name
							parm[5],//Active
							parm[7],//Message type
							parm[8],//Message text
							vib_used,
							led_used,
							parm[11]//Message led color
							);
					action.add(tpli);
				} else if (parm[6].equals(PROFILE_ACTION_TYPE_TIME)) {
					tpli.setActionTimeEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[3],//Type
							parm[4],//Name
							parm[5],//Active
							parm[7],//Time type
							parm[8] //Time target
							);
					action.add(tpli);
				} else if (parm[6].equals(PROFILE_ACTION_TYPE_TASK)) {
					tpli.setActionTaskEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[3],//Type
							parm[4],//Name
							parm[5],//Active
							parm[7],//Task type
							parm[8] //Task target
							);
					action.add(tpli);
				} else if (parm[6].equals(PROFILE_ACTION_TYPE_WAIT)) {
					tpli.setActionWaitEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[3],//Type
							parm[4],//Name
							parm[5],//Active
							parm[7],//Wait target
							parm[8],//Timeout value
							parm[9] //Timeout units
							);
					action.add(tpli);
				}
			} 
		}
	};

	final static public void parseProfileListV0005(String pl, String grp_selection, 
			ArrayList<ProfileListItem> task,
			ArrayList<ProfileListItem> time, ArrayList<ProfileListItem> action) {
		
		String[] tmp_pl=pl.split("\t");// {"type","name","active",options...};
		String[] parm= new String[50];
		for (int i=0;i<50;i++) parm[i]="";
		for (int i=0;i<tmp_pl.length;i++) {
			if (tmp_pl[i]==null) parm[i]="";
			else {
				if (tmp_pl[i]==null) parm[i]="";
				else parm[i]=tmp_pl[i];
			}
		}
		long put=Long.valueOf(parm[2]);
		if (grp_selection.equals("") || parm[1].equals(grp_selection)) {
			ProfileListItem tpli= new ProfileListItem();
			boolean grp_activated=false;
			if (parm[1].equals(PROFILE_GROUP_ACTIVATED)) grp_activated=true;
			if (parm[3].equals(PROFILE_TYPE_TASK)) {//Task
				ArrayList<String> act=new ArrayList<String>();
				ArrayList<String> trig=new ArrayList<String>();
				String[] act_string = parm[9].split("\u0001");
				String[] trig_string = parm[10].split("\u0001");
				for (int i=0;i<act_string.length;i++) 
					if (act_string[i]!=null && !act_string[i].equals("")) 
						act.add(act_string[i]);
				for (int i=0;i<trig_string.length;i++) 
					if (trig_string[i]!=null && !trig_string[i].equals("")) 
						trig.add(trig_string[i]);

				tpli.setTaskEntry(
						PROFILE_VERSION_CURRENT,
						parm[0],//Group
						grp_activated,
						put,
						parm[3],//Type
						parm[4],//Name
						parm[5],//Active
						parm[6],//RetrospectiveEnable
						parm[7],//RetrospectiveNumber
						parm[8],//Notification
						act,//action					
						trig//Trigger
						);
				task.add(tpli);
			} 
			if (parm[3].equals(PROFILE_TYPE_TIME)) {//Time
				tpli.setTimeEventEntry(
						PROFILE_VERSION_CURRENT,
						parm[0],//Group
						grp_activated,
						put,
						parm[3],//Type
						parm[4],//Name
						parm[5],//Active
						parm[6],//DateTimeType
						parm[7],//Execution day of the week
						parm[8],//Execution date
						parm[9] //Execution time
						);
				time.add(tpli);
			}
			if (parm[3].equals(PROFILE_TYPE_ACTION)) {//Action
				if (parm[6].equals(PROFILE_ACTION_TYPE_ACTIVITY)) {
					ArrayList<ActivityExtraDataItem> aed_list=new ArrayList<ActivityExtraDataItem>();
					String[] aed_list_string = parm[11].split("\u0001");
					for (int i=0;i<aed_list_string.length;i++) {
						if (aed_list_string[i]!=null && !aed_list_string[i].equals("")) {
							String[] p_str=aed_list_string[i].split("\u0002");
							ActivityExtraDataItem aedi=new ActivityExtraDataItem();
							aedi.key_value=p_str[0];
							aedi.data_type=p_str[1];
							aedi.data_value_array=p_str[2];
							if (p_str.length==4) aedi.data_value=p_str[3];
							else aedi.data_value="";
							aed_list.add(aedi);
						}
					}
					tpli.setActionAndroidEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[3],//Type
							parm[4],//Name
							parm[5],//Active
							parm[7],//Activity name name
							parm[8],//package name
							parm[9],//data type
							parm[10],//uri data
							aed_list
							);
					action.add(tpli);
				} else if (parm[6].equals(PROFILE_ACTION_TYPE_MUSIC)) {
					tpli.setActionMusicEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[3],//Type
							parm[4],//Name
							parm[5],//Active
							parm[7],//File name
							parm[8],//Volume left
							parm[9]//Volume right
							);
					action.add(tpli);
				} else if (parm[6].equals(PROFILE_ACTION_TYPE_RINGTONE)) {
					tpli.setActionRingtoneEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[3],//Type
							parm[4],//Name
							parm[5],//Active
							parm[7],//Ringtone type
							parm[8],//Ringtone name
							parm[9],//Ringtone path
							parm[10],//Volume left
							parm[11]//Volume right
							);
					action.add(tpli);
				} else if (parm[6].equals(PROFILE_ACTION_TYPE_COMPARE)) {
					String n_parm=parm[9].replaceAll("\u0003", "\u00a0\u0003");
					String[] t_cv=n_parm.split("\u0003");
					String[]n_cv=new String[20];
					for (int i=0;i<t_cv.length;i++) n_cv[i]=t_cv[i].substring(0,t_cv[i].length()-1);
					tpli.setActionCompareEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[3],//Type
							parm[4],//Name
							parm[5],//Active
							parm[7],//Compare target
							parm[8],//Compare type
							n_cv,
							parm[10]//Compare result action
							);
					action.add(tpli);
				} else if (parm[6].equals(PROFILE_ACTION_TYPE_MESSAGE)) {
					boolean vib_used=false, led_used=false;
					if (parm[9].equals("1")) vib_used=true;
					if (parm[10].equals("1")) led_used=true;
					tpli.setActionMessageEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[3],//Type
							parm[4],//Name
							parm[5],//Active
							parm[7],//Message type
							parm[8],//Message text
							vib_used,
							led_used,
							parm[11]//Message led color
							);
					action.add(tpli);
				} else if (parm[6].equals(PROFILE_ACTION_TYPE_TIME)) {
					tpli.setActionTimeEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[3],//Type
							parm[4],//Name
							parm[5],//Active
							parm[7],//Time type
							parm[8] //Time target
							);
					action.add(tpli);
				} else if (parm[6].equals(PROFILE_ACTION_TYPE_TASK)) {
					tpli.setActionTaskEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[3],//Type
							parm[4],//Name
							parm[5],//Active
							parm[7],//Task type
							parm[8] //Task target
							);
					action.add(tpli);
				} else if (parm[6].equals(PROFILE_ACTION_TYPE_WAIT)) {
					tpli.setActionWaitEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[3],//Type
							parm[4],//Name
							parm[5],//Active
							parm[7],//Wait target
							parm[8],//Timeout value
							parm[9] //Timeout units
							);
					action.add(tpli);
				} else if (parm[6].equals(PROFILE_ACTION_TYPE_BSH_SCRIPT)) {
					tpli.setActionBeanShellScriptEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[3],//Type
							parm[4],//Name
							parm[5],//Active
							parm[7].replaceAll("\u0003", "\n")//Script
							);
					action.add(tpli);
				} else if (parm[6].equals(PROFILE_ACTION_TYPE_SHELL_COMMAND)) {
					boolean su=false;
					if (parm[8].equals("0")) su=false;
					else if (parm[8].equals("1")) su=true;
					tpli.setActionShellCmdEntry(
							PROFILE_VERSION_CURRENT,
							parm[0],//Group
							grp_activated,
							put,
							parm[3],//Type
							parm[4],//Name
							parm[5],//Active
							parm[7].replaceAll("\u0003", "\n"),//cmd
							su
							);
					action.add(tpli);
				}
			} 
		}
	};

	final static public String buildProfileRecord(ProfileListItem item) {
		String pl_profile_group_activated=PROFILE_GROUP_NOT_ACTIVATED;
		if (item.isProfileGroupActivated()) 
			pl_profile_group_activated=PROFILE_GROUP_ACTIVATED;

		String pl=item.getProfileVersion()+"\t"+
				item.getProfileGroup()+"\t"+
				pl_profile_group_activated+"\t"+
				String.valueOf(item.getProfileUpdateTime())+"\t";
		String pl_profile_type=item.getProfileType();
		String pl_name=item.getProfileName();
		String pl_active=item.getProfileEnabled();
		
		if (!pl_profile_type.equals("")) {
			if (pl_profile_type.equals(PROFILE_TYPE_TASK)) {
				String pl_retrospec_enable=item.getTaskRetrospecEnabled();
				String pl_retrospec_number=item.getTaskRetrospecNumber();
				String pl_notification=item.getProfileErrorNotification();
				ArrayList<String> trig=item.getTaskTriggerList();
				ArrayList<String> act=item.getTaskActionList();
				String act_string="", trig_string="";
				if (act!=null)
					for (int j=0;j<act.size();j++) 
						act_string+=act.get(j)+"\u0001";
				if (trig!=null)
					for (int j=0;j<trig.size();j++) 
						trig_string+=trig.get(j)+"\u0001";

				pl +=pl_profile_type+ "\t" +
						pl_name + "\t" +
						pl_active + "\t" +
						pl_retrospec_enable + "\t" +
						pl_retrospec_number + "\t" +
						pl_notification + "\t" +
						act_string+ "\t" +
						trig_string+ "\t";
			} else if (pl_profile_type.equals(PROFILE_TYPE_TIME)) {
				String pl_date_time_type=item.getTimeType(); 
				String pl_execute_day_of_the_week=item.getTimeDayOfTheWeek();
				String pl_execute_date=item.getTimeDate(); 
				String pl_execute_time=item.getTimeTime(); 
				pl +=pl_profile_type+ "\t" +
						pl_name + "\t" +
						pl_active + "\t" +
						pl_date_time_type + "\t" +
						pl_execute_day_of_the_week + "\t" +
						pl_execute_date + "\t" +
						pl_execute_time ;

			} else if (pl_profile_type.equals(PROFILE_TYPE_ACTION)) {
				String pl_execute_task_type=item.getActionType();
				if (pl_execute_task_type.equals(PROFILE_ACTION_TYPE_ACTIVITY)) {
					String pl_execute_activity_name=item.getActionActivityName(); 
					String pl_execute_activity_pkgname=item.getActionActivityPackageName();
					String pl_execute_activity_data_type=item.getActionActivityDataType();
					String pl_execute_activity_uri_data=item.getActionActivityUriData();
					
					String pl_execute_activity_extra_data="";
					ArrayList<ActivityExtraDataItem>aed_list=item.getActionActivityExtraData();
					for (int i=0;i<aed_list.size();i++) {
						ActivityExtraDataItem aedi=aed_list.get(i);
						String p_str=aedi.key_value+"\u0002"+aedi.data_type+"\u0002"+
								aedi.data_value_array+"\u0002"+aedi.data_value;
						pl_execute_activity_extra_data+=p_str+"\u0001";
					}
					
					pl +=pl_profile_type+ "\t" +
							pl_name + "\t" +
							pl_active + "\t" +
							pl_execute_task_type + "\t" +
							pl_execute_activity_name + "\t" +
							pl_execute_activity_pkgname+ "\t" + 
							pl_execute_activity_data_type+ "\t" +
							pl_execute_activity_uri_data+ "\t" +
							pl_execute_activity_extra_data+ "\t" 
							;
				} else if (pl_execute_task_type.equals(PROFILE_ACTION_TYPE_MUSIC)) {
					String pl_execute_mp_file_name=item.getActionSoundFileName();
					String pl_mp_vol_left=item.getActionSoundVolLeft();
					String pl_mp_vol_right=item.getActionSoundVolRight();
					pl +=pl_profile_type+ "\t" +
							pl_name + "\t" +
							pl_active + "\t" +
							pl_execute_task_type + "\t" +
							pl_execute_mp_file_name + "\t" +
							pl_mp_vol_left+ "\t"+ 
							pl_mp_vol_right+ "\t"
							;
				} else if (pl_execute_task_type.equals(PROFILE_ACTION_TYPE_RINGTONE)) {
					String pl_rt_type=item.getActionRingtoneType();
					String pl_rt_name=item.getActionRingtoneName();
					String pl_rt_path=item.getActionRingtonePath();
					String pl_rt_vol_left=item.getActionRingtoneVolLeft();
					String pl_rt_vol_right=item.getActionRingtoneVolRight();
					pl +=pl_profile_type+ "\t" +
							pl_name + "\t" +
							pl_active + "\t" +
							pl_execute_task_type + "\t" +
							pl_rt_type + "\t" +
							pl_rt_name + "\t" +
							pl_rt_path + "\t" +
							pl_rt_vol_left+ "\t"+ 
							pl_rt_vol_right+ "\t"
							;
				} else if (pl_execute_task_type.equals(PROFILE_ACTION_TYPE_COMPARE)) {
					String pl_comp_target=item.getActionCompareTarget();
					String pl_comp_type=item.getActionCompareType();
					String[] t_comp_val=item.getActionCompareValue();
					String pl_comp_val="";
					for (int i=0;i<t_comp_val.length;i++) {
						if (t_comp_val[i]!=null) pl_comp_val+=t_comp_val[i]+"\u0003";
						else pl_comp_val+=""+"\u0003";
					}
					String pl_comp_ra=item.getActionCompareResultAction();
					pl +=pl_profile_type+ "\t" +
							pl_name + "\t" +
							pl_active + "\t" +
							pl_execute_task_type + "\t" +
							pl_comp_target + "\t" +
							pl_comp_type + "\t" +
							pl_comp_val+ "\t" +
							pl_comp_ra+ "\t"
							;
				} else if (pl_execute_task_type.equals(PROFILE_ACTION_TYPE_MESSAGE)) {
					String pl_msg_type=item.getActionMessageType();
					String pl_msg_text=item.getActionMessageText();
					String pl_led_color=item.getActionMessageLedColor();
					String pl_vib_used="0";
					if (item.isActionMessageUseVibration()) pl_vib_used="1";
					String pl_led_used="0";
					if (item.isActionMessageUseLed()) pl_led_used="1";
					pl +=pl_profile_type+ "\t" +
							pl_name + "\t" +
							pl_active + "\t" +
							pl_execute_task_type + "\t" +
							pl_msg_type + "\t" +
							pl_msg_text+ "\t" +
							pl_vib_used+ "\t" +
							pl_led_used+ "\t" +
							pl_led_color+ "\t"
							;
				} else if (pl_execute_task_type.equals(PROFILE_ACTION_TYPE_TIME)) {
					String pl_t_typ=item.getActionTimeType();
					String pl_t_tgt=item.getActionTimeTarget();
					pl +=pl_profile_type+ "\t" +
							pl_name + "\t" +
							pl_active + "\t" +
							pl_execute_task_type + "\t" +
							pl_t_typ + "\t" +
							pl_t_tgt
							;
				} else if (pl_execute_task_type.equals(PROFILE_ACTION_TYPE_TASK)) {
					String pl_t_typ=item.getActionTaskType();
					String pl_t_tgt=item.getActionTaskTarget();
					pl +=pl_profile_type+ "\t" +
							pl_name + "\t" +
							pl_active + "\t" +
							pl_execute_task_type + "\t" +
							pl_t_typ + "\t" +
							pl_t_tgt
							;
				} else if (pl_execute_task_type.equals(PROFILE_ACTION_TYPE_WAIT)) {
					String pl_w_tgt=item.getActionWaitTarget();
					String pl_w_tov=item.getActionWaitTimeoutValue();
					String pl_w_tou=item.getActionWaitTimeoutUnits();
					pl +=pl_profile_type+ "\t" +
							pl_name + "\t" +
							pl_active + "\t" +
							pl_execute_task_type + "\t" +
							pl_w_tgt + "\t" +
							pl_w_tov + "\t" +
							pl_w_tou
							;
				} else if (pl_execute_task_type.equals(PROFILE_ACTION_TYPE_BSH_SCRIPT)) {
					String pl_script=item.getActionBeanShellScriptScript().replaceAll("\n", "\u0003");
					pl +=pl_profile_type+ "\t" +
							pl_name + "\t" +
							pl_active + "\t" +
							pl_execute_task_type + "\t" +
							pl_script
							;
				} else if (pl_execute_task_type.equals(PROFILE_ACTION_TYPE_SHELL_COMMAND)) {
//					Log.v("","cmd="+item.getActionShellCmd());
					String pl_cmd=item.getActionShellCmd().replaceAll("\n", "\u0003");
					String su="0";
					if (item.isActionShellCmdWithSu()) su="1";
					pl +=pl_profile_type+ "\t" +
							pl_name + "\t" +
							pl_active + "\t" +
							pl_execute_task_type + "\t" +
							pl_cmd + "\t" +
							su
							;
//					Log.v("","pl="+pl);
				}
			}
		}
		return pl;
	};

}

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
import java.util.ArrayList;

import com.sentaroh.android.TaskAutomation.R;
import com.sentaroh.android.TaskAutomation.Common.ProfileListItem;
import com.sentaroh.android.Utilities.NotifyEvent;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class AdapterProfileList extends BaseAdapter {

	private Context c;
	private int id;
	private ArrayList<Integer>dummy_items=new ArrayList<Integer>();
	private ArrayList<ProfileListItem>data_items;
	private String sel_grp="";	
	
	private String msgs_repeat_type_one_shot,
		msgs_repeat_type_every_year,msgs_repeat_type_every_month,
		msgs_repeat_type_day_of_the_week,msgs_repeat_type_every_day,
		msgs_repeat_type_every_hour,msgs_repeat_type_interval;

	public AdapterProfileList(Context context, int textViewResourceId,
			ArrayList<ProfileListItem> objects) {
		c = context;
		id = textViewResourceId;
		data_items = objects;
//		Log.v("","size="+data_items.size());
		
		msgs_repeat_type_one_shot=context.getString(R.string.msgs_repeat_type_one_shot);
		msgs_repeat_type_every_year=context.getString(R.string.msgs_repeat_type_every_year);
		msgs_repeat_type_every_month=context.getString(R.string.msgs_repeat_type_every_month);
		msgs_repeat_type_day_of_the_week=context.getString(R.string.msgs_repeat_type_day_of_the_week);
		msgs_repeat_type_every_day=context.getString(R.string.msgs_repeat_type_every_day);
		msgs_repeat_type_every_hour=context.getString(R.string.msgs_repeat_type_every_hour);
		msgs_repeat_type_interval=context.getString(R.string.msgs_repeat_type_interval);
		
		updateShowList();
	}
	
	@Override
	final public int getCount() {return dummy_items.size();}
	
	@Override
	final public Integer getItem(int arg0) {return dummy_items.get(arg0);}

	@Override
	final public long getItemId(int arg0) {return dummy_items.get(arg0);}

	final public void sort() {
		ProfileUtilities.sortProfileArrayList(null, data_items);
		notifyDataSetChanged();
	};
	
	private boolean mShowCheckBox=false;
	public void setShowCheckBox(boolean p) {mShowCheckBox=p;}
	public boolean isShowCheckBox() {return mShowCheckBox;}
	
	public boolean isEmptyAdapter() {
		boolean result=true;
		if (dummy_items!=null) {
			if (dummy_items.size()>0) {
				if (!getProfItem(0).getProfileType().equals("")) result=false;
			}
		}
		return result;
	};

	private boolean mDisableCheckBoxClickListener=false;
	public void setAllProfItemSelected(boolean p) {
		mDisableCheckBoxClickListener=true;
		for(int i=0;i<getProfItemCount();i++) getProfItem(i).setProfileItemSelected(p);
		mDisableCheckBoxClickListener=false;
	};

	public int getProfItemSelectedCount() {
		int count=0;
		for(int i=0;i<getProfItemCount();i++) {
			if (getProfItem(i).isProfileItemSelected()) count++;
		}
		return count;
	};

	public boolean isQuickTaskGroupSelected() {
		boolean result=false;
		if (sel_grp.startsWith("*QuickTask")) result=true;
		return result;
	};

	final public void clear() {
		dummy_items.clear();
		data_items.clear();
		notifyDataSetChanged();
	};
	
	final public void setShowedProfileGroupName(String grp) {sel_grp=grp;}
	final public String getShowedProfileGroupName() {return sel_grp;}
	final private void updateProfileGroupShowedStatus() {
//		Log.v("","sloc="+sel_loc);
		for (int i=0;i<data_items.size();i++) {
			ProfileListItem pli=data_items.get(i);
//			Log.v("","i="+i+", pli="+pli);
			if (pli.getProfileGroup().equals(sel_grp)) {
				pli.setProfileGroupShowed(true);
//				data_items.set(i,pli);
			} else {
				pli.setProfileGroupShowed(false);
//				data_items.set(i,pli);
			}
		}
	};
	
	private String sel_filter="";
	final public void setSelectedFilter(String filter) {sel_filter=filter;}
	final public String getSelectedFilter() {return sel_filter;}
	final private void updateFilter() {
		if (sel_filter.equals(PROFILE_TYPE_TASK)) {
			for (int i=0;i<data_items.size();i++) {
				ProfileListItem pli=data_items.get(i);
				if (data_items.get(i).getProfileGroup().equals(sel_grp) &&
					data_items.get(i).getProfileType().equals(PROFILE_TYPE_TASK)) {
					pli.setFilterSelected(true);
//					data_items.set(i,pli);
				} else {
					pli.setFilterSelected(false);
					pli.setProfileItemSelected(false);
//					data_items.set(i,pli);
				}
			}
		} else if (sel_filter.equals(PROFILE_TYPE_ACTION)) {
			for (int i=0;i<data_items.size();i++) {
				ProfileListItem pli=data_items.get(i);
				if (data_items.get(i).getProfileGroup().equals(sel_grp) &&
					data_items.get(i).getProfileType().equals(PROFILE_TYPE_ACTION)) {
					pli.setFilterSelected(true);
//					data_items.set(i,pli);
				} else {
					pli.setFilterSelected(false);
					pli.setProfileItemSelected(false);
//					data_items.set(i,pli);
				}
			}
		} else if (sel_filter.equals(PROFILE_TYPE_TIME)) {
			for (int i=0;i<data_items.size();i++) {
				ProfileListItem pli=data_items.get(i);
				if (data_items.get(i).getProfileGroup().equals(sel_grp) &&
					data_items.get(i).getProfileType().equals(PROFILE_TYPE_TIME)) {
					pli.setFilterSelected(true);
//					data_items.set(i,pli);
				} else {
					pli.setFilterSelected(false);
					pli.setProfileItemSelected(false);
//					data_items.set(i,pli);
				}
			}
		} else if (sel_filter.startsWith(BUILTIN_PREFIX)) {//Task filter Builtin
			for (int i=0;i<data_items.size();i++) {
				ProfileListItem pli=data_items.get(i);
				if (data_items.get(i).getProfileType().equals(PROFILE_TYPE_TASK) &&
					data_items.get(i).getProfileGroup().equals(sel_grp) &&
					data_items.get(i).getTaskTriggerList().get(0).equals(sel_filter)) {
					pli.setFilterSelected(true);
//					data_items.set(i,pli);
				} else {
					pli.setFilterSelected(false);
					pli.setProfileItemSelected(false);
//					data_items.set(i,pli);
				}
			}
		} else if (sel_filter.equals(PROFILE_FILTER_TIME_EVENT_TASK)) {//Task filter time event
			for (int i=0;i<data_items.size();i++) {
				ProfileListItem pli=data_items.get(i);
				if (data_items.get(i).getProfileType().equals(PROFILE_TYPE_TASK) &&
					data_items.get(i).getProfileGroup().equals(sel_grp) &&
					!data_items.get(i).getTaskTriggerList().get(0).startsWith(BUILTIN_PREFIX) &&
					!data_items.get(i).getTaskTriggerList().get(0).equals(TRIGGER_EVENT_TASK)) {
					pli.setFilterSelected(true);
//					data_items.set(i,pli);
				} else {
					pli.setFilterSelected(false);
					pli.setProfileItemSelected(false);
//					data_items.set(i,pli);
				}
			}
		} else {
			for (int i=0;i<data_items.size();i++) {
				ProfileListItem pli=data_items.get(i);
				if (data_items.get(i).getProfileGroup().equals(sel_grp)) {
					pli.setFilterSelected(true);
					pli.setProfileItemSelected(false);
//					data_items.set(i,pli);
				}
			}
		}
	};

	final public int getProfItemCount() {return getCount();}
	
	final public void removeProfItem(int i) {
//		data_items.get(getItem(i)).dumpProfile();
//		Log.v("","i="+i+", t_i="+getItem(i)+", resule=");
		data_items.remove((int)getItem(i));
		updateShowList();
	};

	final public void removeProfItem(ProfileListItem di) {
//		di.dumpProfile();
		data_items.remove(di);
		updateShowList();
	};

//	final public void replaceProfItem(int i, ProfileListItem fi) {
//		data_items.set(getItem(i),fi);
//		notifyDataSetChanged();
//	};
	final public void replaceProfItem(ProfileListItem fi) {
		replaceDataListItem(fi);
		notifyDataSetChanged();
	};

	final public void insertProfItem(int i, ProfileListItem fi) {
		data_items.add(getItem(i),fi);
		notifyDataSetChanged();
	};

	final public ProfileListItem getProfItem(int i) {
		return data_items.get(getItem(i));
	};

//	Access to data_items directly	
	final public int getDataListCount() {
		return data_items.size();
	};
	final public int getDataListItemPos(int pi) {
		return dummy_items.get(pi);
	};
	final public ProfileListItem getDataListItem(int i) {
		return data_items.get(i);
	};
	final public void addDataListItem(ProfileListItem pfli) {
		data_items.add(pfli);
	};
	final public void addProfItem(ProfileListItem fi) {
		data_items.add(fi);
		notifyDataSetChanged();
	};
//	final public void replaceDataListItem(int i, ProfileListItem fi) {
//		data_items.set(i,fi);
//		notifyDataSetChanged();
//	};
	final public boolean replaceDataListItem(ProfileListItem fi) {
		boolean result=false;
		for (int i=0;i<data_items.size();i++) {
			ProfileListItem li=data_items.get(i);
			if (li.getProfileGroup().equals(fi.getProfileGroup()) && 
				li.getProfileType().equals(fi.getProfileType()) && 
				li.getProfileName().equals(fi.getProfileName()) ) {
				data_items.set(i,fi);
				result=true;
				break;
			}
		}
//		data_items.set(i,fi)
//		Log.v("","replace result="+result);
		notifyDataSetChanged();
		return result;
	};
	final public void removeDataListItem(int i) {
//		data_items.get(i).dumpProfile();
		data_items.remove(i);
		notifyDataSetChanged();
	};
	final public ArrayList<ProfileListItem> getDataList() {return data_items;}
	
	final public void setDataList(ArrayList<ProfileListItem> p) {
//		data_items.clear();
//		if (p!=null) data_items.addAll(p);
		if (p==null) data_items=new ArrayList<ProfileListItem>();
		else data_items=p;
		notifyDataSetChanged();
	}
	
	final public void updateShowList() {
		if (data_items==null) return;
//		Log.v("","before prct="+getDataListCount()+", sict="+getCount());
		dummy_items.clear();
		updateProfileGroupShowedStatus();
		updateFilter();
		for (int i=0;i<data_items.size();i++) {
			if (data_items.get(i).isProfileGroupShowed()
					&& data_items.get(i).isFilterSelected()) dummy_items.add(i);
		}
//		Log.v("","after  prct="+getDataListCount()+", sict="+getCount());
		notifyDataSetChanged();
	};
	
	
//	@Override
//	public boolean isEnabled(int idx) {
//		 return getItem(idx).getActive().equals("A");
//	}

	@Override
    final public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(id, null);
            holder=new ViewHolder();
            holder.ll_prof_view=(LinearLayout)v.findViewById(R.id.task_profile_list_view_item_prof_view);
            holder.ll_task_view1=(LinearLayout)v.findViewById(R.id.task_profile_list_view_item_task_view1);
			holder.ll_time_view1=(LinearLayout)v.findViewById(R.id.task_profile_list_view_item_time_view1);
			holder.ll_action_view1=(LinearLayout)v.findViewById(R.id.task_profile_list_view_item_action_view1);
            holder.cb_cb1= (CheckBox) v.findViewById(R.id.task_profile_list_view_item_cb1);
            holder.iv_icon1= (ImageView) v.findViewById(R.id.task_profile_list_view_item_icon1);
            holder.tv_profilename= (TextView) v.findViewById(R.id.task_profile_list_view_item_profilename);
            holder.tv_profile_status= (TextView) v.findViewById(R.id.task_profile_list_view_item_profile_status);
            holder.tv_task_status= (TextView) v.findViewById(R.id.task_profile_list_view_item_task_status);
            holder.tv_time_repeat= (TextView) v.findViewById(R.id.task_profile_list_view_item_time_repeat);
            holder.tv_time_date= (TextView) v.findViewById(R.id.task_profile_list_view_item_time_date);
            holder.tv_time_time= (TextView) v.findViewById(R.id.task_profile_list_view_item_time_time);
            holder.tv_task_trigger= (TextView) v.findViewById(R.id.task_profile_list_view_item_task_trigger);
            holder.tv_task_action= (TextView) v.findViewById(R.id.task_profile_list_view_item_task_action);
            holder.tv_time_dw= (TextView) v.findViewById(R.id.task_profile_list_view_item_time_day_of_the_week);
            holder.tv_action_task_type= (TextView) v.findViewById(R.id.task_profile_list_view_item_action_task_type);
            holder.tv_action_sub_name1= (TextView) v.findViewById(R.id.task_profile_list_view_item_action_sub_name1);
            holder.tv_action_sub_name2= (TextView) v.findViewById(R.id.task_profile_list_view_item_action_sub_name2);
            
            holder.day_of_week_sun=c.getString(R.string.msgs_edit_profile_time_hdr_sun);
            holder.day_of_week_mon=c.getString(R.string.msgs_edit_profile_time_hdr_mon);
            holder.day_of_week_tue=c.getString(R.string.msgs_edit_profile_time_hdr_tue);
            holder.day_of_week_wed=c.getString(R.string.msgs_edit_profile_time_hdr_wed);
            holder.day_of_week_thu=c.getString(R.string.msgs_edit_profile_time_hdr_thu);
            holder.day_of_week_fri=c.getString(R.string.msgs_edit_profile_time_hdr_fri);
            holder.day_of_week_sat=c.getString(R.string.msgs_edit_profile_time_hdr_sat);

            holder.config=v.getResources().getConfiguration();
            v.setTag(holder);
        } else {
        	holder= (ViewHolder)v.getTag();
        }
        ProfileListItem o = getProfItem(position);
        if (o != null) {
    		
//    		if (wsz_w>=700) 
//        		holder.tv_row_time.setVisibility(TextView.VISIBLE);
//        	else holder.tv_row_time.setVisibility(TextView.GONE);

   			holder.tv_profilename.setText(o.getProfileName());
//			holder.ll_prof_view.setBackgroundColor(Color.BLACK);
//			holder.ll_task_view1.setBackgroundColor(Color.BLACK);
//			holder.ll_time_view1.setBackgroundColor(Color.BLACK);
//			holder.ll_action_view1.setBackgroundColor(Color.BLACK);
   			if (o.isProfileEnabled()) {
   				holder.tv_profile_status.setText(
   						c.getString(R.string.msgs_edit_profile_hdr_enabled));
   	            holder.cb_cb1.setTextColor(Color.WHITE);
   	            holder.tv_profilename.setTextColor(Color.WHITE);
   	            holder.tv_profile_status.setTextColor(Color.WHITE);
   	            holder.tv_task_status.setTextColor(Color.WHITE);
   	            holder.tv_time_repeat.setTextColor(Color.WHITE);
   	            holder.tv_time_date.setTextColor(Color.WHITE);
   	            holder.tv_time_time.setTextColor(Color.WHITE);
   	            holder.tv_task_trigger.setTextColor(Color.WHITE);
   	            holder.tv_task_action.setTextColor(Color.WHITE);
   	            holder.tv_time_dw.setTextColor(Color.WHITE);
   	            holder.tv_action_task_type.setTextColor(Color.WHITE);
   	            holder.tv_action_sub_name1.setTextColor(Color.WHITE);
   	            holder.tv_action_sub_name1.setTextColor(Color.WHITE);
   			} else {
   				holder.tv_profile_status.setText(
   						c.getString(R.string.msgs_edit_profile_hdr_disabled));
   	            holder.cb_cb1.setTextColor(Color.GRAY);
   	            holder.tv_profilename.setTextColor(Color.GRAY);
   	            holder.tv_profile_status.setTextColor(Color.GRAY);
   	            holder.tv_task_status.setTextColor(Color.GRAY);
   	            holder.tv_time_repeat.setTextColor(Color.GRAY);
   	            holder.tv_time_date.setTextColor(Color.GRAY);
   	            holder.tv_time_time.setTextColor(Color.GRAY);
   	            holder.tv_task_trigger.setTextColor(Color.GRAY);
   	            holder.tv_task_action.setTextColor(Color.GRAY);
   	            holder.tv_time_dw.setTextColor(Color.GRAY);
   	            holder.tv_action_task_type.setTextColor(Color.GRAY);
   	            holder.tv_action_sub_name1.setTextColor(Color.GRAY);
   	            holder.tv_action_sub_name1.setTextColor(Color.GRAY);
   			}
   			
   			if (o.getProfileType().equals(PROFILE_TYPE_TASK)) {
   				holder.iv_icon1.setImageResource(R.drawable.task);
   				holder.ll_task_view1.setVisibility(LinearLayout.VISIBLE);
   				
   				holder.ll_time_view1.setVisibility(LinearLayout.GONE);
   				holder.ll_action_view1.setVisibility(LinearLayout.GONE);
   				if (o.getTaskTriggerList().size()>0)
   					holder.tv_task_trigger.setText(o.getTaskTriggerList().get(0));
   				if (o.getTaskActionList().size()>0) {
   					String al="",sep="";
   					for (int i=0;i<o.getTaskActionList().size();i++) {
   						al+=sep+o.getTaskActionList().get(i);
   						if (i==0) sep=",";
   					}
   					holder.tv_task_action.setText(al);
   				}
   				if (o.isTaskActive()) {
   					holder.tv_task_status.setText(
   							c.getString(R.string.msgs_edit_profile_hdr_started));
   	   	            holder.cb_cb1.setTextColor(Color.RED);
   	   	            holder.tv_profilename.setTextColor(Color.RED);
   	   	            holder.tv_profile_status.setTextColor(Color.RED);
   	   	            holder.tv_task_status.setTextColor(Color.RED);
   	   	            holder.tv_time_repeat.setTextColor(Color.RED);
   	   	            holder.tv_time_date.setTextColor(Color.RED);
   	   	            holder.tv_time_time.setTextColor(Color.RED);
   	   	            holder.tv_task_trigger.setTextColor(Color.RED);
   	   	            holder.tv_task_action.setTextColor(Color.RED);
   	   	            holder.tv_time_dw.setTextColor(Color.RED);
   	   	            holder.tv_action_task_type.setTextColor(Color.RED);
   	   	            holder.tv_action_sub_name1.setTextColor(Color.RED);
   	   	            holder.tv_action_sub_name1.setTextColor(Color.RED);
   					
//   					holder.tv_task_status.setBackgroundColor(Color.WHITE);
//   	   				holder.ll_prof_view.setBackgroundColor(Color.GRAY);
//   	   				holder.ll_task_view1.setBackgroundColor(Color.GRAY);
   				} else {
   					holder.tv_task_status.setText(
   							c.getString(R.string.msgs_edit_profile_hdr_stopped));
//   	   				holder.ll_prof_view.setBackgroundColor(Color.BLACK);
//   	   				holder.ll_task_view1.setBackgroundColor(Color.BLACK);
//   					holder.tv_task_status.setBackgroundColor(Color.BLACK);
//   					holder.tv_task_status.setTextColor(Color.WHITE);
   				}
   			} else if (o.getProfileType().equals(PROFILE_TYPE_ACTION)) {
   				holder.iv_icon1.setImageResource(R.drawable.action);
   				holder.ll_task_view1.setVisibility(LinearLayout.GONE);
   				holder.ll_time_view1.setVisibility(LinearLayout.GONE);
   				holder.ll_action_view1.setVisibility(LinearLayout.VISIBLE);
   				holder.tv_action_task_type.setText(o.getActionType());
   				if (o.getActionType().equals(PROFILE_ACTION_TYPE_ACTIVITY)) {
   	   				holder.tv_action_sub_name1.setVisibility(TextView.VISIBLE);
   	   				holder.tv_action_sub_name2.setVisibility(TextView.VISIBLE);
   	   				holder.tv_action_sub_name1.setText(o.getActionActivityName());
   	   				holder.tv_action_sub_name2.setText(o.getActionActivityPackageName());
   				} else if (o.getActionType().equals(PROFILE_ACTION_TYPE_MUSIC)) {
   	   				holder.tv_action_sub_name1.setVisibility(TextView.GONE);
   	   				holder.tv_action_sub_name2.setVisibility(TextView.VISIBLE);
   	   				holder.tv_action_sub_name2.setText(o.getActionSoundFileName());
   				} else if (o.getActionType().equals(PROFILE_ACTION_TYPE_RINGTONE)) {
   	   				holder.tv_action_sub_name1.setVisibility(TextView.VISIBLE);
   	   				holder.tv_action_sub_name2.setVisibility(TextView.VISIBLE);
   	   				holder.tv_action_sub_name1.setText(o.getActionRingtoneType());
   	   				holder.tv_action_sub_name2.setText(o.getActionRingtoneName());
   				} else if (o.getActionType().equals(PROFILE_ACTION_TYPE_COMPARE)) {
   	   				holder.tv_action_sub_name1.setVisibility(TextView.VISIBLE);
   	   				holder.tv_action_sub_name2.setVisibility(TextView.VISIBLE);
   	   				holder.tv_action_sub_name1.setText(o.getActionCompareTarget());
   	   				String c_v="", sep="";
		   	 		String[] c_v_a=o.getActionCompareValue();
		   	 		for (int c_i=0;c_i<c_v_a.length;c_i++) {
		   	 			if (c_v_a[c_i]!=null && !c_v_a[c_i].equals("")) {
		   	 				c_v+=sep+c_v_a[c_i];
		   	 				sep=", ";
		   	 			}
		   	 		} 
   	   				holder.tv_action_sub_name2.setText(o.getActionCompareType()+
   	   						"  ("+c_v+")"+
   	   						"  "+o.getActionCompareResultAction());
   				} else if (o.getActionType().equals(PROFILE_ACTION_TYPE_MESSAGE)) {
   	   				holder.tv_action_sub_name1.setVisibility(TextView.VISIBLE);
   	   				holder.tv_action_sub_name2.setVisibility(TextView.VISIBLE);
   	   				holder.tv_action_sub_name1.setText(o.getActionMessageType());
   	   				holder.tv_action_sub_name2.setText(o.getActionMessageText());
   				} else if (o.getActionType().equals(PROFILE_ACTION_TYPE_TIME)) {
   	   				holder.tv_action_sub_name1.setVisibility(TextView.VISIBLE);
   	   				holder.tv_action_sub_name2.setVisibility(TextView.VISIBLE);
   	   				holder.tv_action_sub_name1.setText(o.getActionTimeType());
   	   				holder.tv_action_sub_name2.setText(o.getActionTimeTarget());
   				} else if (o.getActionType().equals(PROFILE_ACTION_TYPE_TASK)) {
   	   				holder.tv_action_sub_name1.setVisibility(TextView.VISIBLE);
   	   				holder.tv_action_sub_name2.setVisibility(TextView.VISIBLE);
   	   				holder.tv_action_sub_name1.setText(o.getActionTaskType());
   	   				holder.tv_action_sub_name2.setText(o.getActionTaskTarget());
   				} else if (o.getActionType().equals(PROFILE_ACTION_TYPE_WAIT)) {
   	   				holder.tv_action_sub_name1.setVisibility(TextView.VISIBLE);
   	   				
   	   				holder.tv_action_sub_name1.setText(o.getActionWaitTarget());
   	   				if (!o.getActionWaitTarget().equals("")) {
   	   	   				holder.tv_action_sub_name2.setVisibility(TextView.VISIBLE);
   	   	   				holder.tv_action_sub_name2.setText(
   	   	   						o.getActionWaitTimeoutValue()+", "+o.getActionWaitTimeoutUnits());
   	   				}
   				} else {
   	   				holder.tv_action_sub_name1.setVisibility(TextView.GONE);
   	   				holder.tv_action_sub_name2.setVisibility(TextView.GONE);
   				}
   			} else if (o.getProfileType().equals(PROFILE_TYPE_TIME)) {
   				holder.iv_icon1.setImageResource(R.drawable.timer);
   				holder.ll_task_view1.setVisibility(LinearLayout.GONE);
   				holder.ll_time_view1.setVisibility(LinearLayout.VISIBLE);
   				holder.ll_action_view1.setVisibility(LinearLayout.GONE);
   				holder.tv_time_date.setVisibility(LinearLayout.GONE);
   				holder.tv_time_dw.setVisibility(LinearLayout.GONE);
   				holder.tv_time_time.setVisibility(LinearLayout.GONE);
   	   			holder.tv_time_repeat.setText(convertItemRepeatType(o.getTimeType()));
   	   			holder.tv_time_date.setText(
   	   					convertItemExecDate(o.getTimeType(),o.getTimeDate()));
   	   			holder.tv_time_time.setText(
   	   					convertItemExecTime(o.getTimeType(),o.getTimeTime()));
   	   			if (o.getTimeType().equals(PROFILE_DATE_TIME_TYPE_ONE_SHOT)) {
   	   				holder.tv_time_date.setVisibility(LinearLayout.VISIBLE);
   	   				holder.tv_time_time.setVisibility(LinearLayout.VISIBLE);
   	   			} else if (o.getTimeType().equals(PROFILE_DATE_TIME_TYPE_DAY_OF_THE_WEEK)) {
   	   				holder.tv_time_dw.setVisibility(LinearLayout.VISIBLE);
   	   				holder.tv_time_time.setVisibility(LinearLayout.VISIBLE);
   	   				holder.tv_time_dw.setText(getDayOfTheWeekString(o, holder));
   	   			} else if (o.getTimeType().equals(PROFILE_DATE_TIME_TYPE_EVERY_YEAR)) {
   	   				holder.tv_time_date.setVisibility(LinearLayout.VISIBLE);
   	   				holder.tv_time_time.setVisibility(LinearLayout.VISIBLE);
   	   			} else if (o.getTimeType().equals(PROFILE_DATE_TIME_TYPE_EVERY_MONTH)) {
   	   				holder.tv_time_date.setVisibility(LinearLayout.VISIBLE);
   	   				holder.tv_time_time.setVisibility(LinearLayout.VISIBLE);
   	   			} else if (o.getTimeType().equals(PROFILE_DATE_TIME_TYPE_EVERY_DAY)) {
   	   				holder.tv_time_time.setVisibility(LinearLayout.VISIBLE);
   	   			} else if (o.getTimeType().equals(PROFILE_DATE_TIME_TYPE_EVERY_HOUR)) {
   	   				holder.tv_time_time.setVisibility(LinearLayout.VISIBLE);
   	   			} else if (o.getTimeType().equals(PROFILE_DATE_TIME_TYPE_INTERVAL)) {
   	   				holder.tv_time_time.setVisibility(LinearLayout.VISIBLE);
   	   			}
   			} else {
   				holder.iv_icon1.setImageResource(R.drawable.blank);
   				holder.ll_task_view1.setVisibility(LinearLayout.GONE);
   				holder.ll_time_view1.setVisibility(LinearLayout.GONE);
   				holder.ll_action_view1.setVisibility(LinearLayout.GONE);
   			}
       	}
        final int p = position;
        if (mShowCheckBox) holder.cb_cb1.setVisibility(CheckBox.VISIBLE);
        else holder.cb_cb1.setVisibility(CheckBox.INVISIBLE);
        holder.cb_cb1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
					ProfileListItem tpli=data_items.get(getItem(p));
					tpli.setProfileItemSelected(isChecked);
//					data_items.set(getItem(p),tpli);
					if (!mDisableCheckBoxClickListener) {
						if (mNotifyCheckBoxClickListener!=null && mShowCheckBox) 
							mNotifyCheckBoxClickListener.notifyToListener(true, null);
					}
				}
			});
        holder.cb_cb1.setChecked(data_items.get(getItem(p)).isProfileItemSelected());
        return v;
	};

	private NotifyEvent mNotifyCheckBoxClickListener=null;
	public void setNotifyCheckBoxClickListener(NotifyEvent ntfy) {mNotifyCheckBoxClickListener=ntfy;}
	
	final static private String getDayOfTheWeekString(ProfileListItem o, ViewHolder holder) {
		String result="None";
		if (o.getTimeDayOfTheWeek().substring(0,1).equals("1")) result=holder.day_of_week_sun;
		if (o.getTimeDayOfTheWeek().substring(1,2).equals("1")) 
			if (result.equals("None")) result=holder.day_of_week_mon;else result+=","+holder.day_of_week_mon;
		if (o.getTimeDayOfTheWeek().substring(2,3).equals("1")) 
			if (result.equals("None")) result=holder.day_of_week_tue;else result+=","+holder.day_of_week_tue;
		if (o.getTimeDayOfTheWeek().substring(3,4).equals("1")) 
			if (result.equals("None")) result=holder.day_of_week_wed;else result+=","+holder.day_of_week_wed;
		if (o.getTimeDayOfTheWeek().substring(4,5).equals("1")) 
			if (result.equals("None")) result=holder.day_of_week_thu;else result+=","+holder.day_of_week_thu;
		if (o.getTimeDayOfTheWeek().substring(5,6).equals("1")) 
			if (result.equals("None")) result=holder.day_of_week_fri;else result+=","+holder.day_of_week_fri;
		if (o.getTimeDayOfTheWeek().substring(6,7).equals("1")) 
			if (result.equals("None")) result=holder.day_of_week_sat;else result+=","+holder.day_of_week_sat;
		return result;
	};
	
	final static private String convertItemExecDate(String rt, String e_date) {
		String n_date="";
		if (rt.equals(PROFILE_DATE_TIME_TYPE_ONE_SHOT)) {
			n_date=e_date;
		} else if (rt.equals(PROFILE_DATE_TIME_TYPE_EVERY_YEAR)) {
			n_date=e_date.substring(5,10);
		} else if (rt.equals(PROFILE_DATE_TIME_TYPE_EVERY_MONTH)) {
			n_date=e_date.substring(8,10);
		} else if (rt.equals(PROFILE_DATE_TIME_TYPE_DAY_OF_THE_WEEK)) {
			n_date=e_date.substring(5,10);
		} else if (rt.equals(PROFILE_DATE_TIME_TYPE_EVERY_DAY)) {
			n_date=e_date;
		} else if (rt.equals(PROFILE_DATE_TIME_TYPE_EVERY_HOUR)) {
			n_date=e_date;
		} else if (rt.equals(PROFILE_DATE_TIME_TYPE_INTERVAL)) {
			n_date=e_date;
		} 
		return n_date;
	};
	
	final static private String convertItemExecTime(String rt, String e_time) {
		String n_time="";
		if (rt.equals(PROFILE_DATE_TIME_TYPE_ONE_SHOT)) {
			n_time=e_time;
		} else if (rt.equals(PROFILE_DATE_TIME_TYPE_EVERY_YEAR)) {
			n_time=e_time;
		} else if (rt.equals(PROFILE_DATE_TIME_TYPE_EVERY_MONTH)) {
			n_time=e_time;
		} else if (rt.equals(PROFILE_DATE_TIME_TYPE_DAY_OF_THE_WEEK)) {
			n_time=e_time;
		} else if (rt.equals(PROFILE_DATE_TIME_TYPE_EVERY_DAY)) {
			n_time=e_time;
		} else if (rt.equals(PROFILE_DATE_TIME_TYPE_EVERY_HOUR)) {
			n_time=e_time;
		} else if (rt.equals(PROFILE_DATE_TIME_TYPE_INTERVAL)) {
			n_time=e_time;
		} 
		return n_time;
	};

	final private String convertItemRepeatType(String item) {
		String n_item="";
		
		if (item.equals(PROFILE_DATE_TIME_TYPE_ONE_SHOT)) {
			n_item=msgs_repeat_type_one_shot;
		} else if (item.equals(PROFILE_DATE_TIME_TYPE_EVERY_YEAR)) {
			n_item=msgs_repeat_type_every_year;
		} else if (item.equals(PROFILE_DATE_TIME_TYPE_EVERY_MONTH)) {
			n_item=msgs_repeat_type_every_month;
		} else if (item.equals(PROFILE_DATE_TIME_TYPE_DAY_OF_THE_WEEK)) {
			n_item=msgs_repeat_type_day_of_the_week;
		} else if (item.equals(PROFILE_DATE_TIME_TYPE_EVERY_DAY)) {
			n_item=msgs_repeat_type_every_day;
		} else if (item.equals(PROFILE_DATE_TIME_TYPE_EVERY_HOUR)) {
			n_item=msgs_repeat_type_every_hour;
		} else if (item.equals(PROFILE_DATE_TIME_TYPE_INTERVAL)) {
			n_item=msgs_repeat_type_interval;
		} 
		return n_item;
	}

	static class ViewHolder {
		CheckBox cb_cb1;
		ImageView iv_icon1;
		TextView tv_profilename,tv_profile_status,tv_task_status;
		TextView tv_time_repeat,tv_time_date,tv_time_time;
        TextView tv_task_action, tv_task_trigger, tv_time_dw;
        TextView tv_action_task_type, tv_action_sub_name1, tv_action_sub_name2;
        LinearLayout ll_prof_view,ll_task_view1, ll_time_view1, ll_action_view1;
        String day_of_week_sun,day_of_week_mon,day_of_week_tue,day_of_week_wed,
        day_of_week_thu,day_of_week_fri,day_of_week_sat;
		Configuration config;
	}
}


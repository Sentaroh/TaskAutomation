package com.sentaroh.android.TaskAutomation;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.sentaroh.android.TaskAutomation.R;
import com.sentaroh.android.TaskAutomation.ProfileGroupListItem;
import com.sentaroh.android.Utilities.NotifyEvent;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;


public class AdapterProfileGroupList extends ArrayAdapter<ProfileGroupListItem>{
	private Context mContext;
	private int mResourceId;
	private ArrayList<ProfileGroupListItem>items;
	
	private NotifyEvent mNotifyCbClick=null;
	
	public AdapterProfileGroupList(Context context, int textViewResourceId,
			ArrayList<ProfileGroupListItem> objects, NotifyEvent ntfy) {
		super(context, textViewResourceId, objects);
		mContext = context;
		mResourceId = textViewResourceId;
		items=objects;
		mNotifyCbClick=ntfy;
	}
	
	@Override
	final public int getCount() {
		return items.size();
	}
	
	private boolean mShowCheckBox=false;
	public void setShowCheckBox(boolean p) {mShowCheckBox=p;}
	public boolean isShowCheckBox() {return mShowCheckBox;}
	
	public void setNotifyCbClickListener(NotifyEvent ntfy) {mNotifyCbClick=ntfy;}
	 
	public boolean isAnyItemSelected() {
		boolean result=false;
		if (items!=null) {
			for(int i=0;i<items.size();i++) {
				if (items.get(i).isSelected()) {
					result=true;
					break;
				}
			}
		}
		return result;
	};

	public boolean isQuickTaskSelected() {
		boolean result=false;
		if (items!=null) {
			for(int i=0;i<items.size();i++) {
				if (items.get(i).getProfileGroupName().startsWith("*QuickTask") && items.get(i).isSelected()) {
					result=true;
					break;
				}
			}
		}
		return result;
	};

	public int getItemSelectedCount() {
		int result=0;
		if (items!=null) {
			for(int i=0;i<items.size();i++) {
				if (items.get(i).isSelected()) {
					result++;
				}
			}
		}
		return result;
	};
	
	public boolean isEmptyAdapter() {
		boolean result=true;
		if (items!=null) {
			if (items.size()>0) result=false;
		}
		return result;
	};

	public void setAllItemSelected(boolean p) {
		if (items!=null) {
			for(int i=0;i<items.size();i++) {
				items.get(i).setSelected(p);
			}
		}
	};

	final public void sort() {
		Collections.sort(items, new Comparator<ProfileGroupListItem>() {
			@Override
			public int compare(ProfileGroupListItem lhs, ProfileGroupListItem rhs) {
				String f_key=lhs.getProfileGroupName();
				String t_key=rhs.getProfileGroupName();
				return f_key.compareToIgnoreCase(t_key);
			}
		});
	}
	
	final public void remove(int i) {
		items.remove(i);
	}

	@Override
	final public void add(ProfileGroupListItem lli) {
		items.add(lli);
		notifyDataSetChanged();
	}
	
	@Override
	final public ProfileGroupListItem getItem(int i) {
		 return items.get(i);
	}
	
	final public ArrayList<ProfileGroupListItem> getAllItem() {return items;}
	
	final public void setAllItem(ArrayList<ProfileGroupListItem> p) {
		items.clear();
		if (p!=null) items.addAll(p);
		notifyDataSetChanged();
	}
	
//	@Override
//	public boolean isEnabled(int idx) {
//		 return getItem(idx).getActive().equals("A");
//	}

	@Override
	final public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(mResourceId, null);
            holder=new ViewHolder();
            holder.iv_status= (ImageView) v.findViewById(R.id.task_profile_group_list_view_status);
            holder.tv_profile_group= (TextView) v.findViewById(R.id.task_profile_group_list_view_profile_group);
            holder.tv_no_task= (TextView) v.findViewById(R.id.task_profile_group_list_view_no_of_task);
            holder.tv_no_action= (TextView) v.findViewById(R.id.task_profile_group_list_view_no_of_action);
            holder.tv_no_time= (TextView) v.findViewById(R.id.task_profile_group_list_view_no_of_time);
            holder.cb_cb1= (CheckBox) v.findViewById(R.id.task_profile_group_list_view_item_cb1);
            holder.config=v.getResources().getConfiguration();
            v.setTag(holder);
        } else {
        	holder= (ViewHolder)v.getTag();
        }
        final ProfileGroupListItem o = getItem(position);
        if (o != null) {
//       		wsz_w=activity.getWindow()
//    					.getWindowManager().getDefaultDisplay().getWidth();
//   			wsz_h=activity.getWindow()
//    					.getWindowManager().getDefaultDisplay().getHeight();
    		
//    		if (wsz_w>=700) 
//        		holder.tv_row_time.setVisibility(TextView.VISIBLE);
//        	else holder.tv_row_time.setVisibility(TextView.GONE);
   			
   			if (o.isProfileGroupActivated()) {
   				holder.iv_status.setImageResource(R.drawable.menu_group_activated);
   			} else {
   				holder.iv_status.setImageResource(R.drawable.blank);
   			}
   			holder.tv_profile_group.setText(o.getProfileGroupName());
   			holder.tv_no_task.setText(String.valueOf(o.getNoOfTask()));
   			holder.tv_no_action.setText(String.valueOf(o.getNoOfAction()));
   			holder.tv_no_time.setText(String.valueOf(o.getNoOfTime()));
   			
   	        if (mShowCheckBox) holder.cb_cb1.setVisibility(CheckBox.VISIBLE);
   	        else holder.cb_cb1.setVisibility(CheckBox.INVISIBLE);
//   	        Log.v("","name="+o.getProfileGroupName()+", chk="+mShowCheckBox);
   	        
    		// 必ずsetChecked前にリスナを登録(convertView != null の場合は既に別行用のリスナが登録されている！)
            holder.cb_cb1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
    			@Override
    			public void onCheckedChanged(CompoundButton buttonView,
    				boolean isChecked) {
    				o.setSelected(isChecked);
//    				Log.v("","name="+o.getProfileGroupName()+", chk="+isChecked);
    				if (mNotifyCbClick!=null && mShowCheckBox) 
    					mNotifyCbClick.notifyToListener(true, new Object[] {isChecked});
   				}
   			});
            holder.cb_cb1.setChecked(o.isSelected());

       	}
        return v;
	};


	class ViewHolder {
		TextView tv_profile_group;
		CheckBox cb_cb1;
		ImageView iv_status;
		TextView tv_no_task, tv_no_action,tv_no_time;
		Configuration config;
	}
}

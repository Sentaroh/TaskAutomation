package com.sentaroh.android.TaskAutomation.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.sentaroh.android.TaskAutomation.R;
import com.sentaroh.android.TaskAutomation.Common.TrustDeviceItem;
import com.sentaroh.android.Utilities.NotifyEvent;

public class AdapterTrustDeviceList extends ArrayAdapter<TrustDeviceItem>{
	private Context mContext;
	private int mResourceId;
	private ArrayList<TrustDeviceItem>items;
	
	private NotifyEvent mNotifyCbClick=null;
	
	public AdapterTrustDeviceList(Context context, int textViewResourceId,
			ArrayList<TrustDeviceItem> objects, NotifyEvent ntfy) {
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
		Collections.sort(items, new Comparator<TrustDeviceItem>() {
			@Override
			public int compare(TrustDeviceItem lhs, TrustDeviceItem rhs) {
				String f_key=lhs.trustedItemName+lhs.trustedItemAddr;
				String t_key=rhs.trustedItemName+rhs.trustedItemAddr;
				return f_key.compareToIgnoreCase(t_key);
			}
		});
	}
	
	final public void remove(int i) {
		items.remove(i);
	}

	@Override
	final public void add(TrustDeviceItem lli) {
		items.add(lli);
		notifyDataSetChanged();
	}
	
	@Override
	final public TrustDeviceItem getItem(int i) {
		 return items.get(i);
	}
	
	final public ArrayList<TrustDeviceItem> getAllItem() {return items;}
	
	final public void setAllItem(ArrayList<TrustDeviceItem> p) {
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
            holder.iv_icon= (ImageView) v.findViewById(R.id.edit_trust_list_item_icon);
            holder.tv_name= (TextView) v.findViewById(R.id.edit_trust_list_item_name);
            holder.tv_addr= (TextView) v.findViewById(R.id.edit_trust_list_item_addr);
            holder.cb_cb1= (CheckBox) v.findViewById(R.id.edit_trust_list_item_checkbox);
            v.setTag(holder);
        } else {
        	holder= (ViewHolder)v.getTag();
        }
        final TrustDeviceItem o = getItem(position);
        if (o != null) {
        	holder.tv_name.setText(o.trustedItemName);
        	holder.tv_addr.setText(o.trustedItemAddr);
        	if (o.trustedItemType==TrustDeviceItem.TYPE_WIFI_AP) {
        		holder.iv_icon.setImageResource(R.drawable.ic_32_device_wifi_on);
        	} else if (o.trustedItemType==TrustDeviceItem.TYPE_BLUETOOTH_DEVICE) {
        		holder.iv_icon.setImageResource(R.drawable.ic_32_device_bt_on);
        	} else {
        		holder.iv_icon.setImageDrawable(null);
        	}
   			
   	        if (mShowCheckBox) holder.cb_cb1.setVisibility(CheckBox.VISIBLE);
   	        else holder.cb_cb1.setVisibility(CheckBox.INVISIBLE);
    		// 必ずsetChecked前にリスナを登録(convertView != null の場合は既に別行用のリスナが登録されている！)
            holder.cb_cb1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
    			@Override
    			public void onCheckedChanged(CompoundButton buttonView,
    				boolean isChecked) {
    				o.setSelected(isChecked);
    				if (mNotifyCbClick!=null && mShowCheckBox) 
    					mNotifyCbClick.notifyToListener(true, new Object[] {isChecked});
   				}
   			});
            holder.cb_cb1.setChecked(o.isSelected());

       	}
        return v;
	};


	class ViewHolder {
		TextView tv_name, tv_addr;
		CheckBox cb_cb1;
		ImageView iv_icon;
	}
}

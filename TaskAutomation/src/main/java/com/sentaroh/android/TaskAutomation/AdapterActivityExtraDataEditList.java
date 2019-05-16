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

import com.sentaroh.android.TaskAutomation.R;
import com.sentaroh.android.TaskAutomation.ActivityExtraDataItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AdapterActivityExtraDataEditList extends ArrayAdapter<ActivityExtraDataItem> {
	private Context c;
	private int id;
	private ArrayList<ActivityExtraDataItem>items;
	private String no_data_exists="";
	
	public AdapterActivityExtraDataEditList(Context context, 
			int textViewResourceId, ArrayList<ActivityExtraDataItem> objects) {
		super(context, textViewResourceId, objects);
		c = context;
		id = textViewResourceId;
		items=objects;
		no_data_exists=context.getString(R.string.msgs_edit_profile_action_activity_extra_data_no_data_exists);
	};
	
	@Override
	final public int getCount() {
		return items.size();
	}
	
	final public void remove(int pos) {
		items.remove(pos);
	}

	@Override
    final public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(id, null);
            holder=new ViewHolder();
            holder.tv_key= (TextView) v.findViewById(R.id.edit_activity_extra_data_list_item_key);
            holder.tv_type= (TextView) v.findViewById(R.id.edit_activity_extra_data_list_item_type);
            holder.tv_array= (TextView) v.findViewById(R.id.edit_activity_extra_data_list_item_array);
            holder.tv_value= (TextView) v.findViewById(R.id.edit_activity_extra_data_list_item_value);
            v.setTag(holder);
        } else {
        	holder= (ViewHolder)v.getTag();
        }
        final ActivityExtraDataItem o = items.get(position);
        if (o != null) {
        	if (o.data_value.equals(no_data_exists) && o.key_value.equals("")) {
        		holder.tv_key.setVisibility(TextView.GONE);
            	holder.tv_type.setVisibility(TextView.GONE);
            	holder.tv_array.setVisibility(TextView.GONE);
            	holder.tv_value.setText(o.data_value);
        	} else {
        		holder.tv_key.setVisibility(TextView.VISIBLE);
            	holder.tv_type.setVisibility(TextView.VISIBLE);
            	holder.tv_array.setVisibility(TextView.VISIBLE);
            	holder.tv_key.setText(o.key_value);
            	holder.tv_type.setText(o.data_type);
            	holder.tv_array.setText(o.data_value_array);
            	holder.tv_value.setText((o.data_value.replace("\u00a0","")).replaceAll("\u0003", " "));
        	}
       	}
        return v;
	};

	class ViewHolder {
		TextView tv_key, tv_type, tv_array, tv_value;
		
	}
}

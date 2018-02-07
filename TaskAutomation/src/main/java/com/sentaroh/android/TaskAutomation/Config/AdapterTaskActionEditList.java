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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.sentaroh.android.TaskAutomation.R;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

class TaskActionEditListItem {
	public String action="", desc="";
	public boolean invalid=false;
}

public class AdapterTaskActionEditList extends ArrayAdapter<TaskActionEditListItem> {

	private Context c;
	private int id;
	private ArrayList<TaskActionEditListItem>items;
	
	public AdapterTaskActionEditList(Context context, int textViewResourceId,
			ArrayList<TaskActionEditListItem> objects) {
		super(context, textViewResourceId, objects);
		c = context;
		id = textViewResourceId;
		items = objects;
	}
	
	final public void sort() {
		Collections.sort(items, new Comparator<TaskActionEditListItem>() {
			@Override
			public int compare(TaskActionEditListItem lhs, TaskActionEditListItem rhs) {
				String f_key=lhs.action;
				String t_key=rhs.action;
				return f_key.compareToIgnoreCase(t_key);
			}
		});
	}
	
	final public void remove(int i) {
		items.remove(i);
	}

	@Override
	final public void add(TaskActionEditListItem mli) {
		items.add(mli);
		notifyDataSetChanged();
	}
	
	@Override
	final public TaskActionEditListItem getItem(int i) {
		 return items.get(i);
	}
	
	final public ArrayList<TaskActionEditListItem> getAllItem() {return items;}
	
	final public void setAllItem(List<TaskActionEditListItem> p) {
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
            LayoutInflater vi = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(id, null);
            holder=new ViewHolder();
            holder.tv_action= (TextView) v.findViewById(R.id.task_action_list_item_action);
            holder.tv_desc= (TextView) v.findViewById(R.id.task_action_list_item_desc);

            holder.config=v.getResources().getConfiguration();
            v.setTag(holder);
        } else {
        	holder= (ViewHolder)v.getTag();
        }
        TaskActionEditListItem o = getItem(position);
        if (o != null) {
   			holder.tv_action.setText(o.action);
   			holder.tv_desc.setText(o.desc);
   			if (o.invalid) {
   				holder.tv_action.setTextColor(Color.RED);
   				holder.tv_desc.setTextColor(Color.RED);
   			} else {
   				holder.tv_action.setTextColor(Color.WHITE);
   				holder.tv_desc.setTextColor(Color.WHITE);
   			}
       	}

        return v;
	};


	static class ViewHolder {
		TextView tv_action, tv_desc;
		Configuration config;
	}
}
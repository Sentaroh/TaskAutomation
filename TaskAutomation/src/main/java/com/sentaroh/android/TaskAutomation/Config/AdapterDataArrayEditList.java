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

import com.sentaroh.android.TaskAutomation.R;
import com.sentaroh.android.TaskAutomation.Common.DataArrayEditListItem;
import com.sentaroh.android.Utilities.NotifyEvent;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class AdapterDataArrayEditList extends ArrayAdapter<DataArrayEditListItem> {
	private Context c;
	private int id;
	private ArrayList<DataArrayEditListItem>items;
	
	public AdapterDataArrayEditList(Context context, 
			int textViewResourceId, ArrayList<DataArrayEditListItem> objects) {
		super(context, textViewResourceId, objects);
		c = context;
		id = textViewResourceId;
		items=objects;
	};
	
	@Override
	final public int getCount() {
		return items.size();
	};

	@Override
	final public void add(DataArrayEditListItem p) {
		items.add(p);
	};

	final public void remove(int p) {items.remove(p);};
	
	private NotifyEvent edit_btn_notify=null;
	final public void setEditBtnNotifyListener(NotifyEvent p_ntfy) {
		edit_btn_notify=p_ntfy;
	};

	@SuppressWarnings("deprecation")
	@Override
    final public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(id, null);
            holder=new ViewHolder();
            holder.tv_data= (TextView) v.findViewById(R.id.data_array_edit_list_item_data);
            holder.btn_delete=(Button) v.findViewById(R.id.data_array_edit_list_item_delete);
            holder.btn_edit=(Button) v.findViewById(R.id.data_array_edit_list_item_edit);
            holder.tv_drawable=holder.tv_data.getBackground();
            v.setTag(holder);
        } else {
        	holder= (ViewHolder)v.getTag();
        }
        final DataArrayEditListItem o = items.get(position);
        holder.btn_delete.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (items.size()==1) {
					items.get(0).dummy_data=true;
				} else {
					items.remove(position);
				}
				notifyDataSetChanged();
			}
        });
        holder.btn_edit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (edit_btn_notify!=null) {
					edit_btn_notify.notifyToListener(true, 
							new Object[] {o.data_value,position});
					o.while_edit=true;
					notifyDataSetChanged();
				}
			}
        });
        if (o.dummy_data) {
        	holder.btn_delete.setVisibility(Button.GONE);
        	holder.btn_edit.setVisibility(Button.GONE);
            holder.tv_data.setText("No data exist");
        } else {
        	holder.btn_delete.setVisibility(Button.VISIBLE);
        	holder.btn_edit.setVisibility(Button.VISIBLE);
            boolean while_editting=false;
            for (int i=0;i<items.size();i++) if (items.get(i).while_edit) while_editting=true;
            if (while_editting) {
            	holder.btn_delete.setEnabled(false);
            	holder.btn_edit.setEnabled(false);
            } else {
            	holder.btn_delete.setEnabled(true);
            	holder.btn_edit.setEnabled(true);
            }
            if (o.while_edit) holder.tv_data.setBackgroundColor(Color.CYAN);
            else holder.tv_data.setBackgroundDrawable(holder.tv_drawable);
            
            holder.tv_data.setText(o.data_value);
        }
        return v;
	};

	class ViewHolder {
		TextView tv_data, tv_type;
		Button btn_delete, btn_edit;
		Drawable tv_drawable;
	}
}

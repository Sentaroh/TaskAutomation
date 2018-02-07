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
import com.sentaroh.android.TaskAutomation.Common.TaskConsoleListItem;
import com.sentaroh.android.TaskAutomation.Common.TaskResponse;
import com.sentaroh.android.Utilities.Widget.CustomTextView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AdapterTaskConsoleList extends ArrayAdapter<TaskConsoleListItem> {
	private Context c;
	private int id;
	private ArrayList<TaskConsoleListItem>items;
	private boolean show_thread_id=false;
	private boolean show_group_name=false;
	public AdapterTaskConsoleList(Context context, 
			int textViewResourceId, ArrayList<TaskConsoleListItem> objects,
			boolean show_tid, boolean show_gnm) {
		super(context, textViewResourceId, objects);
		c = context;
		id = textViewResourceId;
		items=objects;
		show_thread_id=show_tid;
		show_group_name=show_gnm;
	};
	
	@Override
	final public int getCount() {
		return items.size();
	}

	@Override
	final public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(id, null);
            holder=new ViewHolder();
    		Typeface tf=Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL);
    		holder.ll_task=(LinearLayout)v.findViewById(R.id.task_exec_console_list_item_ll_task);
    		holder.tv_task_threadid=(TextView)v.findViewById(R.id.task_exec_console_list_item_task_threadid);
    		holder.tv_task_start_end=(TextView)v.findViewById(R.id.task_exec_console_list_item_task_start_end);
    		holder.tv_task_result=(TextView)v.findViewById(R.id.task_exec_console_list_item_task_exec_result);
    		holder.tv_task_grpname=(TextView)v.findViewById(R.id.task_exec_console_list_item_task_grpname);
    		holder.tv_task_taskname=(TextView)v.findViewById(R.id.task_exec_console_list_item_task_taskname);
    		holder.ll_action1=(LinearLayout)v.findViewById(R.id.task_exec_console_list_item_ll_action1);
    		holder.tv_action_threadid=(TextView)v.findViewById(R.id.task_exec_console_list_item_action_threadid);
    		holder.tv_action_result=(TextView)v.findViewById(R.id.task_exec_console_list_item_action_action_result);
    		holder.tv_action_actionname=(TextView)v.findViewById(R.id.task_exec_console_list_item_action_actionname);
    		holder.ll_action2=(LinearLayout)v.findViewById(R.id.task_exec_console_list_item_ll_action2);
    		holder.ll_msg=(LinearLayout)v.findViewById(R.id.task_exec_console_list_item_ll_msg);
    		holder.tv_msg=(TextView)v.findViewById(R.id.task_exec_console_list_item_msg_text);
    		holder.cv_action_result_msg= (CustomTextView) v.findViewById(R.id.task_exec_console_list_item_action_result_msg);
        	holder.cv_action_result_msg.setTypeface(tf);
        	holder.cv_action_result_msg.setLineBreak(CustomTextView.LINE_BREAK_NO_WORD_WRAP);
            v.setTag(holder);
        } else {
        	holder= (ViewHolder)v.getTag();
        }
        final TaskConsoleListItem o = items.get(position);
        if (o != null) {
        	if (o.item_type==TaskConsoleListItem.ITEM_TYPE_TASK) {
        		holder.ll_action1.setVisibility(LinearLayout.GONE);
        		holder.ll_action2.setVisibility(LinearLayout.GONE);
        		holder.ll_msg.setVisibility(LinearLayout.GONE);
        		holder.ll_task.setVisibility(LinearLayout.VISIBLE);
        		if (show_group_name) {
        			holder.tv_task_grpname.setVisibility(TextView.VISIBLE);
        			holder.tv_task_grpname.setText(o.group_name);	
        		} else {
        			holder.tv_task_grpname.setVisibility(TextView.GONE);
        		}
        		
        		holder.tv_task_taskname.setText(o.task_name);
        		if (show_thread_id) {
            		holder.tv_task_threadid.setText(o.thread_id);
            		holder.tv_task_threadid.setVisibility(TextView.VISIBLE);
        		} else {
            		holder.tv_task_threadid.setVisibility(TextView.GONE);
        		}
        		if (o.item_start) holder.tv_task_start_end.setText("Started");
        		else holder.tv_task_start_end.setText("Ended");
        		if (o.item_start) holder.tv_task_result.setText(""); 
        		else holder.tv_task_result.setText(TaskResponse.RESP_CONV_TBL_SHORT[o.result_code]);
        		if (o.result_code==TaskResponse.RESP_CODE_CANCELLED ||o.result_code==TaskResponse.RESP_CODE_CANCELLED) {
            		holder.tv_task_grpname.setTextColor(Color.YELLOW);
            		holder.tv_task_taskname.setTextColor(Color.YELLOW);
            		holder.tv_task_threadid.setTextColor(Color.YELLOW);
            		holder.tv_task_start_end.setTextColor(Color.YELLOW);
            		holder.tv_task_result.setTextColor(Color.YELLOW);
        		} else if (o.result_code==TaskResponse.RESP_CODE_ERROR) {
            		holder.tv_task_grpname.setTextColor(Color.YELLOW);
            		holder.tv_task_taskname.setTextColor(Color.YELLOW);
            		holder.tv_task_threadid.setTextColor(Color.YELLOW);
            		holder.tv_task_start_end.setTextColor(Color.YELLOW);
            		holder.tv_task_result.setTextColor(Color.YELLOW);
            	} else {
            		holder.tv_task_grpname.setTextColor(Color.WHITE);
            		holder.tv_task_taskname.setTextColor(Color.WHITE);
            		holder.tv_task_threadid.setTextColor(Color.WHITE);
            		holder.tv_task_start_end.setTextColor(Color.WHITE);
            		holder.tv_task_result.setTextColor(Color.WHITE);
            	}
        	} else if (o.item_type==TaskConsoleListItem.ITEM_TYPE_ACTION) {
        		holder.ll_msg.setVisibility(LinearLayout.GONE);
        		holder.ll_action1.setVisibility(LinearLayout.VISIBLE);
        		holder.ll_task.setVisibility(LinearLayout.GONE);
        		holder.tv_action_threadid.setText(o.thread_id);
        		if (o.item_start) {
        			holder.tv_action_result.setText("B");        			
        		} else {
            		holder.tv_action_result.setText(TaskResponse.RESP_CONV_TBL_SHORT[o.result_code]);
        		}
        		if (o.shell_cmd!=null && !o.shell_cmd.equals("")) holder.tv_action_actionname.setText(o.action_name+" "+o.shell_cmd);
        		else holder.tv_action_actionname.setText(o.action_name);
        		if (show_thread_id) {
            		holder.tv_action_threadid.setText(o.thread_id);
            		holder.tv_action_threadid.setVisibility(TextView.VISIBLE);
        		} else {
            		holder.tv_action_threadid.setVisibility(TextView.GONE);
        		}
        		if (o.result_msg!=null && o.result_msg.length()!=0) {
            		holder.ll_action2.setVisibility(LinearLayout.VISIBLE);
            		holder.cv_action_result_msg.setText(o.result_msg);
        			
        		} else {
            		holder.ll_action2.setVisibility(LinearLayout.GONE);
        		}
        		if (o.result_code==TaskResponse.RESP_CODE_CANCELLED ||o.result_code==TaskResponse.RESP_CODE_CANCELLED) {
            		holder.tv_action_threadid.setTextColor(Color.YELLOW);
            		holder.tv_action_result.setTextColor(Color.YELLOW);
            		holder.tv_action_actionname.setTextColor(Color.YELLOW);
               		holder.cv_action_result_msg.setTextColor(Color.YELLOW);
        		} else if (o.result_code==TaskResponse.RESP_CODE_ERROR) {
            		holder.tv_action_threadid.setTextColor(Color.YELLOW);
            		holder.tv_action_result.setTextColor(Color.YELLOW);
            		holder.tv_action_actionname.setTextColor(Color.YELLOW);
               		holder.cv_action_result_msg.setTextColor(Color.YELLOW);
            	} else {
            		holder.tv_action_threadid.setTextColor(Color.WHITE);
            		holder.tv_action_result.setTextColor(Color.WHITE);
            		holder.tv_action_actionname.setTextColor(Color.WHITE);
               		holder.cv_action_result_msg.setTextColor(Color.WHITE);
            	}
        	} else if (o.item_type==TaskConsoleListItem.ITEM_TYPE_MESSAGE) {
        		holder.ll_action1.setVisibility(LinearLayout.GONE);
        		holder.ll_action2.setVisibility(LinearLayout.GONE);
        		holder.ll_msg.setVisibility(LinearLayout.VISIBLE);
        		holder.ll_task.setVisibility(LinearLayout.GONE);
        		holder.tv_msg.setText(o.task_name);
        		holder.tv_msg.setTextColor(Color.YELLOW);
        	}
       	}
        return v;
	};

	class ViewHolder {
		CustomTextView cv_action_result_msg;
		TextView tv_task_start_end, tv_task_result, tv_task_grpname, 
			tv_task_taskname, tv_action_result, 
			tv_action_actionname;
		TextView tv_task_threadid,tv_action_threadid;
		TextView tv_msg;
		LinearLayout ll_task, ll_action1, ll_action2, ll_msg;
	};
}

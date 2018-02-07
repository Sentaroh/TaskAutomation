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

import com.sentaroh.android.TaskAutomation.R;
import com.sentaroh.android.Utilities.Widget.CustomSpinnerAdapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AdapterDateTimeTypeSpinner extends CustomSpinnerAdapter {
	
	@SuppressWarnings("unused")
	private int rid;
	private Context context;
	private String msgs_repeat_type_one_shot,
		msgs_repeat_type_every_year,msgs_repeat_type_every_month,
		msgs_repeat_type_day_of_the_week,msgs_repeat_type_every_day,
		msgs_repeat_type_every_hour,msgs_repeat_type_interval;

	public AdapterDateTimeTypeSpinner(Context c, int textViewResourceId) {
		super(c, textViewResourceId);
		rid=textViewResourceId;
		context=c;
		msgs_repeat_type_one_shot=context.getString(R.string.msgs_repeat_type_one_shot);
		msgs_repeat_type_every_year=context.getString(R.string.msgs_repeat_type_every_year);
		msgs_repeat_type_every_month=context.getString(R.string.msgs_repeat_type_every_month);
		msgs_repeat_type_day_of_the_week=context.getString(R.string.msgs_repeat_type_day_of_the_week);
		msgs_repeat_type_every_day=context.getString(R.string.msgs_repeat_type_every_day);
		msgs_repeat_type_every_hour=context.getString(R.string.msgs_repeat_type_every_hour);
		msgs_repeat_type_interval=context.getString(R.string.msgs_repeat_type_interval);

	}

	@Override
	final public View getView(int position, View convertView, ViewGroup parent) {
		
        TextView view;
        view=(TextView)super.getView(position, convertView, parent);
        view.setText(convertItemRepeatType(getItem(position)));
        view.setTextColor(Color.BLACK);

        return view;
	}
	@Override
	final public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView text;
        text=(TextView)super.getDropDownView(position, convertView, parent);
        text.setText(convertItemRepeatType(getItem(position)));
        text.setTextColor(Color.BLACK);
        text.setBackgroundColor(Color.LTGRAY);
        return text;
	}
	
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
}

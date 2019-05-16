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

import static com.sentaroh.android.TaskAutomation.CommonConstants.*;
import static com.sentaroh.android.TaskAutomation.QuickTaskConstants.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.sentaroh.android.Utilities.NotifyEvent.NotifyEventListener;
import com.sentaroh.android.Utilities.NotifyEvent;
import com.sentaroh.android.Utilities.Dialog.CommonDialog;
import com.sentaroh.android.Utilities.Widget.CustomSpinnerAdapter;

public class QuickTaskMaintenance {
	private CommonUtilities util=null;
	private Context mContext=null;
	private CommonDialog commonDlg;
	private ProfileMaintenance profMaint=null;
	private String[] dayOfTheWeekTable=new String[7];
	
	private NotifyEvent ntfyUpdateGroup=null;
	private AdapterProfileList profileAdapter=null;
	
	public QuickTaskMaintenance(Context c, CommonUtilities cu, CommonDialog cd,
			ProfileMaintenance pm, AdapterProfileList pfa, NotifyEvent ntfy) {
		mContext=c;
		util=cu;
//		ccMenu=cm;
//		localRootDir=LocalMountPoint.getExternalStorageDir();
		commonDlg=cd;
		profMaint=pm;
		profileAdapter=pfa;
		ntfyUpdateGroup=ntfy;
		
		dayOfTheWeekTable[0]=mContext.getString(R.string.msgs_edit_profile_time_hdr_sun);
		dayOfTheWeekTable[1]=mContext.getString(R.string.msgs_edit_profile_time_hdr_mon);
		dayOfTheWeekTable[2]=mContext.getString(R.string.msgs_edit_profile_time_hdr_tue);
		dayOfTheWeekTable[3]=mContext.getString(R.string.msgs_edit_profile_time_hdr_wed);
		dayOfTheWeekTable[4]=mContext.getString(R.string.msgs_edit_profile_time_hdr_thu);
		dayOfTheWeekTable[5]=mContext.getString(R.string.msgs_edit_profile_time_hdr_fri);
		dayOfTheWeekTable[6]=mContext.getString(R.string.msgs_edit_profile_time_hdr_sat);
	};
	
	final public void initializeQuickTaskView() {
		Dialog dialog=new Dialog(mContext);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(R.layout.edit_quick_task_dlg);
		
		CommonDialog.setDlgBoxSizeLimit(dialog, true);
		
		setWifiListener(dialog);
		setBluetoothListener(dialog);
		setScreenListener(dialog);
		setAlramClockListener(dialog);
		setTimeActivityListener(dialog);
		setQuickTaskListener(dialog);

		dialog.show();
	};
	
	final private void setWifiListener(Dialog dialog) {
        final CheckedTextView cb_wifi_screen_locked=(CheckedTextView) dialog.findViewById(R.id.quick_task_view_wifi_screen_locked);
        final RadioButton rb_wifi_screen_locked_rb_usual=(RadioButton) dialog.findViewById(R.id.quick_task_view_wifi_screen_locked_rb_usual);
        final RadioButton rb_wifi_screen_locked_rb_ac=(RadioButton) dialog.findViewById(R.id.quick_task_view_wifi_screen_locked_rb_ac);
        final CheckedTextView cb_wifi_wifi_on=(CheckedTextView) dialog.findViewById(R.id.quick_task_view_wifi_wifi_on);
        final RadioButton rb_wifi_wifi_on_rb_usual=(RadioButton) dialog.findViewById(R.id.quick_task_view_wifi_wifi_on_rb_usual);
        final RadioButton rb_wifi_wifi_on_rb_ac=(RadioButton) dialog.findViewById(R.id.quick_task_view_wifi_wifi_on_rb_ac);
        final CheckedTextView cb_wifi_screen_unlocked=(CheckedTextView) dialog.findViewById(R.id.quick_task_view_wifi_screen_unlocked);
        final RadioButton rb_wifi_screen_unlocked_rb_usual=(RadioButton) dialog.findViewById(R.id.quick_task_view_wifi_screen_unlocked_rb_usual);
        final RadioButton rb_wifi_screen_unlocked_rb_ac=(RadioButton) dialog.findViewById(R.id.quick_task_view_wifi_screen_unlocked_rb_ac);

        SharedPreferences prefs=util.getPrefMgr();

        cb_wifi_screen_locked.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				boolean isChecked=!((CheckedTextView)v).isChecked();
				((CheckedTextView)v).setChecked(isChecked);
				if (isChecked) {
					rb_wifi_screen_locked_rb_usual.setEnabled(true);
					rb_wifi_screen_locked_rb_ac.setEnabled(true);
				} else {
					rb_wifi_screen_locked_rb_usual.setEnabled(false);
					rb_wifi_screen_locked_rb_ac.setEnabled(false);
				}
			}
        });
        cb_wifi_wifi_on.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				boolean isChecked=!((CheckedTextView)v).isChecked();
				((CheckedTextView)v).setChecked(isChecked);
				if (isChecked) {
					rb_wifi_wifi_on_rb_usual.setEnabled(true);
					rb_wifi_wifi_on_rb_ac.setEnabled(true);
				} else {
					rb_wifi_wifi_on_rb_usual.setEnabled(false);
					rb_wifi_wifi_on_rb_ac.setEnabled(false);
				}
			}
        });
        cb_wifi_screen_unlocked.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				boolean isChecked=!((CheckedTextView)v).isChecked();
				((CheckedTextView)v).setChecked(isChecked);
				if (isChecked) {
					rb_wifi_screen_unlocked_rb_usual.setEnabled(true);
					rb_wifi_screen_unlocked_rb_ac.setEnabled(true);
				} else {
					rb_wifi_screen_unlocked_rb_usual.setEnabled(false);
					rb_wifi_screen_unlocked_rb_ac.setEnabled(false);
				}
			}
        });
        cb_wifi_screen_locked.setChecked(true);
        if (!prefs.getBoolean(QUICK_TASK_WIFI_SCREEN_LOCKED, false)) {
        	cb_wifi_screen_locked.setChecked(false);
    		rb_wifi_screen_locked_rb_usual.setEnabled(false);
    		rb_wifi_screen_locked_rb_ac.setEnabled(false);
        }
        if (prefs.getBoolean(QUICK_TASK_WIFI_SCREEN_LOCKED_AC, true)) rb_wifi_screen_locked_rb_ac.setChecked(true);
        else rb_wifi_screen_locked_rb_usual.setChecked(true);
        
        cb_wifi_wifi_on.setChecked(true);
        if (!prefs.getBoolean(QUICK_TASK_WIFI_WIFI_ON, false)) {
        	cb_wifi_wifi_on.setChecked(false);
    		rb_wifi_wifi_on_rb_usual.setEnabled(false);
    		rb_wifi_wifi_on_rb_ac.setEnabled(false);
        }
        if (prefs.getBoolean(QUICK_TASK_WIFI_WIFI_ON_AC, true)) rb_wifi_wifi_on_rb_ac.setChecked(true);
        else rb_wifi_wifi_on_rb_usual.setChecked(true);

        cb_wifi_screen_unlocked.setChecked(true);
        if (!prefs.getBoolean(QUICK_TASK_WIFI_SCREEN_UNLOCKED, false)) {
        	cb_wifi_screen_unlocked.setChecked(false);
    		rb_wifi_screen_unlocked_rb_usual.setEnabled(false);
    		rb_wifi_screen_unlocked_rb_ac.setEnabled(false);
        }
        if (prefs.getBoolean(QUICK_TASK_WIFI_SCREEN_UNLOCKED_AC, true)) rb_wifi_screen_unlocked_rb_ac.setChecked(true);
        else rb_wifi_screen_unlocked_rb_usual.setChecked(true);
	};
	
	final private void setBluetoothListener(Dialog dialog) {
		final CheckedTextView cb_bt_screen_locked=(CheckedTextView) dialog.findViewById(R.id.quick_task_view_bt_screen_locked);
		final RadioButton rb_bt_screen_locked_rb_usual=(RadioButton) dialog.findViewById(R.id.quick_task_view_bt_screen_locked_rb_usual);
		final RadioButton rb_bt_screen_locked_rb_ac=(RadioButton) dialog.findViewById(R.id.quick_task_view_bt_screen_locked_rb_ac);
		final CheckedTextView cb_bt_bt_on=(CheckedTextView) dialog.findViewById(R.id.quick_task_view_bt_bt_on);
		final RadioButton rb_bt_bt_on_rb_usual=(RadioButton) dialog.findViewById(R.id.quick_task_view_bt_bt_on_rb_usual);
		final RadioButton rb_bt_bt_on_rb_ac=(RadioButton) dialog.findViewById(R.id.quick_task_view_bt_bt_on_rb_ac);
		final CheckedTextView cb_bt_screen_unlocked=(CheckedTextView) dialog.findViewById(R.id.quick_task_view_bt_screen_unlocked);
		final RadioButton rb_bt_screen_unlocked_rb_usual=(RadioButton) dialog.findViewById(R.id.quick_task_view_bt_screen_unlocked_rb_usual);
		final RadioButton rb_bt_screen_unlocked_rb_ac=(RadioButton) dialog.findViewById(R.id.quick_task_view_bt_screen_unlocked_rb_ac);

		SharedPreferences prefs=util.getPrefMgr();
        cb_bt_screen_locked.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				boolean isChecked=!((CheckedTextView)v).isChecked();
				((CheckedTextView)v).setChecked(isChecked);
				if (isChecked) {
					rb_bt_screen_locked_rb_usual.setEnabled(true);
					rb_bt_screen_locked_rb_ac.setEnabled(true);
				} else {
					rb_bt_screen_locked_rb_usual.setEnabled(false);
					rb_bt_screen_locked_rb_ac.setEnabled(false);
				}
			}
        });
        cb_bt_bt_on.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				boolean isChecked=!((CheckedTextView)v).isChecked();
				((CheckedTextView)v).setChecked(isChecked);
				if (isChecked) {
					rb_bt_bt_on_rb_usual.setEnabled(true);
					rb_bt_bt_on_rb_ac.setEnabled(true);
				} else {
					rb_bt_bt_on_rb_usual.setEnabled(false);
					rb_bt_bt_on_rb_ac.setEnabled(false);
				}
			}
        });
        cb_bt_screen_unlocked.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				boolean isChecked=!((CheckedTextView)v).isChecked();
				((CheckedTextView)v).setChecked(isChecked);
				if (isChecked) {
					rb_bt_screen_unlocked_rb_usual.setEnabled(true);
					rb_bt_screen_unlocked_rb_ac.setEnabled(true);
				} else {
					rb_bt_screen_unlocked_rb_usual.setEnabled(false);
					rb_bt_screen_unlocked_rb_ac.setEnabled(false);
				}
			}
        });
		cb_bt_screen_locked.setChecked(true);
        if (!prefs.getBoolean(QUICK_TASK_BT_SCREEN_LOCKED, false)) {
        	cb_bt_screen_locked.setChecked(false);
			rb_bt_screen_locked_rb_usual.setEnabled(false);
			rb_bt_screen_locked_rb_ac.setEnabled(false);
        }
        if (prefs.getBoolean(QUICK_TASK_BT_SCREEN_LOCKED_AC, true)) rb_bt_screen_locked_rb_ac.setChecked(true);
        else rb_bt_screen_locked_rb_usual.setChecked(true);

        cb_bt_bt_on.setChecked(true);
        if (!prefs.getBoolean(QUICK_TASK_BT_BT_ON, false)) {
        	cb_bt_bt_on.setChecked(false);
			rb_bt_bt_on_rb_usual.setEnabled(false);
			rb_bt_bt_on_rb_ac.setEnabled(false);
        }
        if (prefs.getBoolean(QUICK_TASK_BT_BT_ON_AC, true)) rb_bt_bt_on_rb_ac.setChecked(true);
        else rb_bt_bt_on_rb_usual.setChecked(true);

        cb_bt_screen_unlocked.setChecked(true);
        if (!prefs.getBoolean(QUICK_TASK_BT_SCREEN_UNLOCKED, false)) {
        	cb_bt_screen_unlocked.setChecked(false);
			rb_bt_screen_unlocked_rb_usual.setEnabled(false);
			rb_bt_screen_unlocked_rb_ac.setEnabled(false);
        }
        if (prefs.getBoolean(QUICK_TASK_BT_SCREEN_UNLOCKED_AC, true)) rb_bt_screen_unlocked_rb_ac.setChecked(true);
        else rb_bt_screen_unlocked_rb_usual.setChecked(true);
	};
	
	private void setDefaultCheckedTextViewListener(final CheckedTextView ctv) {
		ctv.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				ctv.setChecked(!ctv.isChecked());
			}
		});
	};
	
	final private void setScreenListener(Dialog dialog) {
		final CheckedTextView cb_screen_proximity_undetected=(CheckedTextView) dialog.findViewById(R.id.quick_task_view_screen_proximity_undetected);

		setDefaultCheckedTextViewListener(cb_screen_proximity_undetected);

		SharedPreferences prefs=util.getPrefMgr();
		
		if (util.isProximitySensorAvailable()==null) {
			cb_screen_proximity_undetected.setEnabled(false);
			cb_screen_proximity_undetected.setChecked(false);
		} else {
			cb_screen_proximity_undetected.setChecked(prefs.getBoolean(QUICK_TASK_SCREEN_PROXIMITY_UNDETECTED, true));
			
		}

	};
	
	final private void setAlramClockListener(Dialog dialog) {
		View ac01=(View) dialog.findViewById(R.id.quick_task_view_alarm_clock_01);
		View ac02=(View) dialog.findViewById(R.id.quick_task_view_alarm_clock_02);
		View ac03=(View) dialog.findViewById(R.id.quick_task_view_alarm_clock_03);
		TextView tv_id01=(TextView)ac01.findViewById(R.id.quick_task_view_alarm_clock_id);
		TextView tv_id02=(TextView)ac02.findViewById(R.id.quick_task_view_alarm_clock_id);
		TextView tv_id03=(TextView)ac03.findViewById(R.id.quick_task_view_alarm_clock_id);
		tv_id01.setText("01");
		tv_id02.setText("02");
		tv_id03.setText("03");
		final CheckedTextView cb_alarm_clock01_enable=(CheckedTextView) ac01.findViewById(R.id.quick_task_view_alarm_clock_enable);
		setDefaultCheckedTextViewListener(cb_alarm_clock01_enable);
		final Button btn_alarm_clock01_btn=(Button) ac01.findViewById(R.id.quick_task_view_alarm_clock_edit);
		final TextView tv_alarm_clock01_datetype=(TextView) ac01.findViewById(R.id.quick_task_view_alarm_clock_datetype);
		final TextView tv_alarm_clock01_day_of_week=(TextView) ac01.findViewById(R.id.quick_task_view_alarm_clock_day_of_week);
		final TextView tv_alarm_clock01_day=(TextView) ac01.findViewById(R.id.quick_task_view_alarm_clock_day);
		final TextView tv_alarm_clock01_time=(TextView) ac01.findViewById(R.id.quick_task_view_alarm_clock_time);
		final TextView tv_alarm_clock01_sound_type=(TextView) ac01.findViewById(R.id.quick_task_view_alarm_clock_sound_type);
		final TextView tv_alarm_clock01_sound_name=(TextView) ac01.findViewById(R.id.quick_task_view_alarm_clock_sound_name);

		final CheckedTextView cb_alarm_clock02_enable=(CheckedTextView) ac02.findViewById(R.id.quick_task_view_alarm_clock_enable);
		setDefaultCheckedTextViewListener(cb_alarm_clock02_enable);
		final TextView tv_alarm_clock02_datetype=(TextView) ac02.findViewById(R.id.quick_task_view_alarm_clock_datetype);
		final TextView tv_alarm_clock02_day_of_week=(TextView) ac02.findViewById(R.id.quick_task_view_alarm_clock_day_of_week);
		final Button btn_alarm_clock02_btn=(Button) ac02.findViewById(R.id.quick_task_view_alarm_clock_edit);
		final TextView tv_alarm_clock02_day=(TextView) ac02.findViewById(R.id.quick_task_view_alarm_clock_day);
		final TextView tv_alarm_clock02_time=(TextView) ac02.findViewById(R.id.quick_task_view_alarm_clock_time);
		final TextView tv_alarm_clock02_sound_type=(TextView) ac02.findViewById(R.id.quick_task_view_alarm_clock_sound_type);
		final TextView tv_alarm_clock02_sound_name=(TextView) ac02.findViewById(R.id.quick_task_view_alarm_clock_sound_name);

		final CheckedTextView cb_alarm_clock03_enable=(CheckedTextView) ac03.findViewById(R.id.quick_task_view_alarm_clock_enable);
		setDefaultCheckedTextViewListener(cb_alarm_clock03_enable);
		final TextView tv_alarm_clock03_datetype=(TextView) ac03.findViewById(R.id.quick_task_view_alarm_clock_datetype);
		final TextView tv_alarm_clock03_day_of_week=(TextView) ac03.findViewById(R.id.quick_task_view_alarm_clock_day_of_week);
		final Button btn_alarm_clock03_btn=(Button) ac03.findViewById(R.id.quick_task_view_alarm_clock_edit);
		final TextView tv_alarm_clock03_day=(TextView) ac03.findViewById(R.id.quick_task_view_alarm_clock_day);
		final TextView tv_alarm_clock03_time=(TextView) ac03.findViewById(R.id.quick_task_view_alarm_clock_time);
		final TextView tv_alarm_clock03_sound_type=(TextView) ac03.findViewById(R.id.quick_task_view_alarm_clock_sound_type);
		final TextView tv_alarm_clock03_sound_name=(TextView) ac03.findViewById(R.id.quick_task_view_alarm_clock_sound_name);
		

		SharedPreferences prefs=util.getPrefMgr();

		cb_alarm_clock01_enable.setChecked(prefs.getBoolean(QUICK_TASK_ALARM_CLOCK01_ENABLED, false));
		setSchduleDateType(prefs.getString(QUICK_TASK_ALARM_CLOCK01_DATE_TYPE, PROFILE_DATE_TIME_TYPE_EVERY_DAY),tv_alarm_clock01_datetype);
		setSchduleDayOfWeek(prefs.getString(QUICK_TASK_ALARM_CLOCK01_DAY_OF_WEEK, ""),tv_alarm_clock01_day_of_week);
		setSchduleDay(prefs.getString(QUICK_TASK_ALARM_CLOCK01_DATE_DAY, ""),tv_alarm_clock01_day);		
		setSchduleTime(prefs.getString(QUICK_TASK_ALARM_CLOCK01_DATE_TIME, "00:00"),tv_alarm_clock01_time);
		setSchduleSoundType(prefs.getString(QUICK_TASK_ALARM_CLOCK01_SOUND_TYPE, ""),tv_alarm_clock01_sound_type);
		setSchduleSound(prefs.getString(QUICK_TASK_ALARM_CLOCK01_SOUND_NAME, ""),tv_alarm_clock01_sound_name);

		cb_alarm_clock02_enable.setChecked(prefs.getBoolean(QUICK_TASK_ALARM_CLOCK02_ENABLED, false));
		setSchduleDateType(prefs.getString(QUICK_TASK_ALARM_CLOCK02_DATE_TYPE, PROFILE_DATE_TIME_TYPE_EVERY_DAY),tv_alarm_clock02_datetype);
		setSchduleDayOfWeek(prefs.getString(QUICK_TASK_ALARM_CLOCK02_DAY_OF_WEEK, ""),tv_alarm_clock02_day_of_week);
		setSchduleDay(prefs.getString(QUICK_TASK_ALARM_CLOCK02_DATE_DAY, ""),tv_alarm_clock02_day);		
		setSchduleTime(prefs.getString(QUICK_TASK_ALARM_CLOCK02_DATE_TIME, "00:00"),tv_alarm_clock02_time);
		setSchduleSoundType(prefs.getString(QUICK_TASK_ALARM_CLOCK02_SOUND_TYPE, ""),tv_alarm_clock02_sound_type);
		setSchduleSound(prefs.getString(QUICK_TASK_ALARM_CLOCK02_SOUND_NAME, ""),tv_alarm_clock02_sound_name);

		cb_alarm_clock03_enable.setChecked(prefs.getBoolean(QUICK_TASK_ALARM_CLOCK03_ENABLED, false));
		setSchduleDateType(prefs.getString(QUICK_TASK_ALARM_CLOCK03_DATE_TYPE, PROFILE_DATE_TIME_TYPE_EVERY_DAY),tv_alarm_clock03_datetype);
		setSchduleDayOfWeek(prefs.getString(QUICK_TASK_ALARM_CLOCK03_DAY_OF_WEEK, ""),tv_alarm_clock03_day_of_week);
		setSchduleDay(prefs.getString(QUICK_TASK_ALARM_CLOCK03_DATE_DAY, ""),tv_alarm_clock03_day);		
		setSchduleTime(prefs.getString(QUICK_TASK_ALARM_CLOCK03_DATE_TIME, "00:00"),tv_alarm_clock03_time);
		setSchduleSoundType(prefs.getString(QUICK_TASK_ALARM_CLOCK03_SOUND_TYPE, ""),tv_alarm_clock03_sound_type);
		setSchduleSound(prefs.getString(QUICK_TASK_ALARM_CLOCK03_SOUND_NAME, ""),tv_alarm_clock03_sound_name);

		setScheduleViewVisibility("01",tv_alarm_clock01_datetype,tv_alarm_clock01_day_of_week,
				tv_alarm_clock01_day,tv_alarm_clock01_time);
		setScheduleViewVisibility("02",tv_alarm_clock02_datetype,tv_alarm_clock02_day_of_week,
				tv_alarm_clock02_day,tv_alarm_clock02_time);
		setScheduleViewVisibility("03",tv_alarm_clock03_datetype,tv_alarm_clock03_day_of_week,
				tv_alarm_clock03_day,tv_alarm_clock03_time);
		
		setAlarmClockEnableCheckBox(cb_alarm_clock01_enable, tv_alarm_clock01_sound_type,
				tv_alarm_clock01_sound_name);
		setAlarmClockEnableCheckBox(cb_alarm_clock02_enable, tv_alarm_clock02_sound_type,
				tv_alarm_clock02_sound_name);
		setAlarmClockEnableCheckBox(cb_alarm_clock03_enable, tv_alarm_clock03_sound_type,
				tv_alarm_clock03_sound_name);

		btn_alarm_clock01_btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				editAlarmClock(cb_alarm_clock01_enable,
						"01",tv_alarm_clock01_datetype,tv_alarm_clock01_day_of_week,
						tv_alarm_clock01_day,tv_alarm_clock01_time,
						tv_alarm_clock01_sound_type,tv_alarm_clock01_sound_name);
			}
		});
		btn_alarm_clock02_btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				editAlarmClock(cb_alarm_clock02_enable,
						"02",tv_alarm_clock02_datetype,tv_alarm_clock02_day_of_week,
						tv_alarm_clock02_day,tv_alarm_clock02_time,
						tv_alarm_clock02_sound_type,tv_alarm_clock02_sound_name);			
			}
		});
		btn_alarm_clock03_btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				editAlarmClock(cb_alarm_clock03_enable,
						"03",tv_alarm_clock03_datetype,tv_alarm_clock03_day_of_week,
						tv_alarm_clock03_day,tv_alarm_clock03_time,
						tv_alarm_clock03_sound_type,tv_alarm_clock03_sound_name);
			}
		});
		
	};
	
	final private void setAlarmClockEnableCheckBox(
			CheckedTextView cb_enable, TextView tv_sound_type, TextView tv_sound_name) {
		if (tv_sound_type.getText().toString().equals(mContext.getString(R.string.msgs_quick_vlue_not_specified)) ||
				tv_sound_name.getText().toString().equals(mContext.getString(R.string.msgs_quick_vlue_not_specified))) {
			cb_enable.setEnabled(false);
		} else cb_enable.setEnabled(true);
	};
	
	final private void setTimeActivityEnableCheckBox(CheckedTextView cb_enable, TextView tv_activity) {
		if (tv_activity.getText().toString().equals(mContext.getString(R.string.msgs_quick_vlue_not_specified))) {
			cb_enable.setEnabled(false);
		} else cb_enable.setEnabled(true);
	};

	final private void setScheduleViewVisibility(String id,final TextView tv_date_type, 
			final TextView tv_day_of_week, final TextView tv_day, final TextView tv_time) {
		String date_type=tv_date_type.getText().toString();
		tv_day_of_week.setVisibility(TextView.GONE);
		tv_day.setVisibility(TextView.GONE);
		if (date_type.equals(mContext.getString(R.string.msgs_repeat_type_one_shot))) {
			tv_day.setVisibility(TextView.VISIBLE);
		} else if (date_type.equals(mContext.getString(R.string.msgs_repeat_type_every_day))) {
			
		} else if (date_type.equals(mContext.getString(R.string.msgs_repeat_type_day_of_the_week))) {
			tv_day_of_week.setVisibility(TextView.VISIBLE);
		}
	};

	final private void setSchduleDateType(String datetype, TextView tv) {
		if (datetype.equals("")) tv.setText(mContext.getString(R.string.msgs_quick_vlue_not_specified));
		else {
			if (datetype.equals(PROFILE_DATE_TIME_TYPE_ONE_SHOT)) tv.setText(mContext.getString(R.string.msgs_repeat_type_one_shot));
			else if (datetype.equals(PROFILE_DATE_TIME_TYPE_EVERY_DAY)) tv.setText(mContext.getString(R.string.msgs_repeat_type_every_day));
			else if (datetype.equals(PROFILE_DATE_TIME_TYPE_DAY_OF_THE_WEEK)) tv.setText(mContext.getString(R.string.msgs_repeat_type_day_of_the_week));
		}
	};

	final private void setSchduleDayOfWeek(String data, TextView tv) {
		if (data.equals("") || data.length()!=7) tv.setText(mContext.getString(R.string.msgs_quick_vlue_not_specified));
		else {
			String day_of_week="";
			for (int i=0;i<7;i++) {
				if (data.substring(i,i+1).equals("1")){
					day_of_week=dayOfTheWeekTable[i];
				}
			}
			tv.setText(day_of_week);
		}
	};
	final private void setSchduleDay(String data, TextView tv) {
		if (data.equals("")) tv.setText(mContext.getString(R.string.msgs_quick_vlue_not_specified));
		else tv.setText(data);
	};
	final private void setSchduleTime(String data, TextView tv) {
		if (data.equals("")) tv.setText(mContext.getString(R.string.msgs_quick_vlue_not_specified));
		else tv.setText(data);
	};
	final private void setSchduleSoundType(String data, TextView tv) {
		if (data.equals("")) tv.setText(mContext.getString(R.string.msgs_quick_vlue_not_specified));
		else tv.setText(data);
	};
	final private void setSchduleSound(String data, TextView tv) {
		if (data.equals("")) tv.setText(mContext.getString(R.string.msgs_quick_vlue_not_specified));
		else tv.setText(data);
	};


	final private void setTimeActivityListener(Dialog dialog){
		View ta01=(View) dialog.findViewById(R.id.quick_task_view_time_activity_01);
		View ta02=(View) dialog.findViewById(R.id.quick_task_view_time_activity_02);
		View ta03=(View) dialog.findViewById(R.id.quick_task_view_time_activity_03);
		TextView tv_id01=(TextView)ta01.findViewById(R.id.quick_task_view_time_activity_id);
		TextView tv_id02=(TextView)ta02.findViewById(R.id.quick_task_view_time_activity_id);
		TextView tv_id03=(TextView)ta03.findViewById(R.id.quick_task_view_time_activity_id);
		tv_id01.setText("01");
		tv_id02.setText("02");
		tv_id03.setText("03");
		final CheckedTextView cb_time_activity01_enable=(CheckedTextView) ta01.findViewById(R.id.quick_task_view_time_activity_enable);
		setDefaultCheckedTextViewListener(cb_time_activity01_enable);
		final Button btn_time_activity01_btn=(Button) ta01.findViewById(R.id.quick_task_view_time_activity_edit);
		final TextView tv_time_activity01_datetype=(TextView) ta01.findViewById(R.id.quick_task_view_time_activity_datetype);
		final TextView tv_time_activity01_day_of_week=(TextView) ta01.findViewById(R.id.quick_task_view_time_activity_day_of_week);
		final TextView tv_time_activity01_day=(TextView) ta01.findViewById(R.id.quick_task_view_time_activity_day);
		final TextView tv_time_activity01_time=(TextView) ta01.findViewById(R.id.quick_task_view_time_activity_time);
		final TextView tv_time_activity01_activity=(TextView) ta01.findViewById(R.id.quick_task_view_time_activity_activity);

		final CheckedTextView cb_time_activity02_enable=(CheckedTextView) ta02.findViewById(R.id.quick_task_view_time_activity_enable);
		setDefaultCheckedTextViewListener(cb_time_activity02_enable);
		final Button btn_time_activity02_btn=(Button) ta02.findViewById(R.id.quick_task_view_time_activity_edit);
		final TextView tv_time_activity02_datetype=(TextView) ta02.findViewById(R.id.quick_task_view_time_activity_datetype);
		final TextView tv_time_activity02_day_of_week=(TextView) ta02.findViewById(R.id.quick_task_view_time_activity_day_of_week);
		final TextView tv_time_activity02_day=(TextView) ta02.findViewById(R.id.quick_task_view_time_activity_day);
		final TextView tv_time_activity02_time=(TextView) ta02.findViewById(R.id.quick_task_view_time_activity_time);
		final TextView tv_time_activity02_activity=(TextView) ta02.findViewById(R.id.quick_task_view_time_activity_activity);

		final CheckedTextView cb_time_activity03_enable=(CheckedTextView) ta03.findViewById(R.id.quick_task_view_time_activity_enable);
		setDefaultCheckedTextViewListener(cb_time_activity03_enable);
		final Button btn_time_activity03_btn=(Button) ta03.findViewById(R.id.quick_task_view_time_activity_edit);
		final TextView tv_time_activity03_datetype=(TextView) ta03.findViewById(R.id.quick_task_view_time_activity_datetype);
		final TextView tv_time_activity03_day_of_week=(TextView) ta03.findViewById(R.id.quick_task_view_time_activity_day_of_week);
		final TextView tv_time_activity03_day=(TextView) ta03.findViewById(R.id.quick_task_view_time_activity_day);
		final TextView tv_time_activity03_time=(TextView) ta03.findViewById(R.id.quick_task_view_time_activity_time);
		final TextView tv_time_activity03_activity=(TextView) ta03.findViewById(R.id.quick_task_view_time_activity_activity);

		SharedPreferences prefs=util.getPrefMgr();

		cb_time_activity01_enable.setChecked(prefs.getBoolean(QUICK_TASK_TIME_ACTIVITY01_ENABLED, false));
		setSchduleDateType(prefs.getString(QUICK_TASK_TIME_ACTIVITY01_DATE_TYPE, PROFILE_DATE_TIME_TYPE_EVERY_DAY),tv_time_activity01_datetype);
		setSchduleDay(prefs.getString(QUICK_TASK_TIME_ACTIVITY01_DATE_DAY, ""),tv_time_activity01_day);		
		setSchduleTime(prefs.getString(QUICK_TASK_TIME_ACTIVITY01_DATE_TIME, "00:00"),tv_time_activity01_time);
		setSchduleSound(prefs.getString(QUICK_TASK_TIME_ACTIVITY01_ACTIVITY, ""),tv_time_activity01_activity);

		cb_time_activity02_enable.setChecked(prefs.getBoolean(QUICK_TASK_TIME_ACTIVITY02_ENABLED, false));
		setSchduleDateType(prefs.getString(QUICK_TASK_TIME_ACTIVITY02_DATE_TYPE, PROFILE_DATE_TIME_TYPE_EVERY_DAY),tv_time_activity02_datetype);
		setSchduleDay(prefs.getString(QUICK_TASK_TIME_ACTIVITY02_DATE_DAY, ""),tv_time_activity02_day);		
		setSchduleTime(prefs.getString(QUICK_TASK_TIME_ACTIVITY02_DATE_TIME, "00:00"),tv_time_activity02_time);
		setSchduleSound(prefs.getString(QUICK_TASK_TIME_ACTIVITY02_ACTIVITY, ""),tv_time_activity02_activity);

		cb_time_activity03_enable.setChecked(prefs.getBoolean(QUICK_TASK_TIME_ACTIVITY03_ENABLED, false));
		setSchduleDateType(prefs.getString(QUICK_TASK_TIME_ACTIVITY03_DATE_TYPE, PROFILE_DATE_TIME_TYPE_EVERY_DAY),tv_time_activity03_datetype);
		setSchduleDay(prefs.getString(QUICK_TASK_TIME_ACTIVITY03_DATE_DAY, ""),tv_time_activity03_day);		
		setSchduleTime(prefs.getString(QUICK_TASK_TIME_ACTIVITY03_DATE_TIME, "00:00"),tv_time_activity03_time);
		setSchduleSound(prefs.getString(QUICK_TASK_TIME_ACTIVITY03_ACTIVITY, ""),tv_time_activity03_activity);

		setScheduleViewVisibility("01",tv_time_activity01_datetype,tv_time_activity01_day_of_week,
				tv_time_activity01_day,tv_time_activity01_time);
		setScheduleViewVisibility("02",tv_time_activity02_datetype,tv_time_activity02_day_of_week,
				tv_time_activity02_day,tv_time_activity02_time);
		setScheduleViewVisibility("03",tv_time_activity03_datetype,tv_time_activity03_day_of_week,
				tv_time_activity03_day,tv_time_activity03_time);

		setTimeActivityEnableCheckBox(cb_time_activity01_enable,tv_time_activity01_activity);
		setTimeActivityEnableCheckBox(cb_time_activity02_enable,tv_time_activity02_activity);
		setTimeActivityEnableCheckBox(cb_time_activity03_enable,tv_time_activity03_activity);
		
		btn_time_activity01_btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				editTimeActivity(cb_time_activity01_enable,
						"01",tv_time_activity01_datetype,tv_time_activity01_day_of_week,
						tv_time_activity01_day,tv_time_activity01_time,tv_time_activity01_activity);
			}
		});
		btn_time_activity02_btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				editTimeActivity(cb_time_activity02_enable,
						"02",tv_time_activity01_datetype,tv_time_activity02_day_of_week,
						tv_time_activity02_day,tv_time_activity02_time,tv_time_activity02_activity);
			}
		});
		btn_time_activity03_btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				editTimeActivity(cb_time_activity03_enable,
						"03",tv_time_activity01_datetype,tv_time_activity03_day_of_week,
						tv_time_activity01_day,tv_time_activity03_time,tv_time_activity03_activity);
			}
		});
		
	};
	
	final private void editAlarmClock(final CheckedTextView cb_enable,
			final String id,final TextView tv_date_type, 
			final TextView tv_day_of_week, final TextView tv_day, final TextView tv_time,
			final TextView tv_sound_type, final TextView tv_sound_name) {
		// カスタムダイアログの生成
		final Dialog dialog = new Dialog(mContext);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(R.layout.quick_task_edit_alarm_clock);
		final TextView dlg_title = (TextView) dialog.findViewById(R.id.quick_task_edit_alarm_clock_title);		
		final Button btnCancel = (Button) dialog.findViewById(R.id.quick_task_edit_alarm_clock_cancel_btn);
		final Button btnOK = (Button) dialog.findViewById(R.id.quick_task_edit_alarm_clock_ok_btn);
		
		dlg_title.setText(mContext.getString(R.string.msgs_quick_hdr_alarm_clock)+id);
		
		CommonDialog.setDlgBoxSizeCompact(dialog);
		
		final RadioButton rb_datetype_dw=(RadioButton) dialog.findViewById(R.id.quick_task_edit_schedule_datetype_day_of_week);
		final RadioButton rb_datetype_ed=(RadioButton) dialog.findViewById(R.id.quick_task_edit_schedule_datetype_everyday);
		final RadioButton rb_datetype_sd=(RadioButton) dialog.findViewById(R.id.quick_task_edit_schedule_datetype_specificdate);
		final LinearLayout ll_dw=(LinearLayout)dialog.findViewById(R.id.quick_task_edit_schedule_day_of_the_week);
		final RadioButton rb_dw_sun=(RadioButton) dialog.findViewById(R.id.quick_task_edit_schedule_day_of_the_week_sunday);
		final RadioButton rb_dw_mon=(RadioButton) dialog.findViewById(R.id.quick_task_edit_schedule_day_of_the_week_monday);
		final RadioButton rb_dw_tue=(RadioButton) dialog.findViewById(R.id.quick_task_edit_schedule_day_of_the_week_tuesday);
		final RadioButton rb_dw_wed=(RadioButton) dialog.findViewById(R.id.quick_task_edit_schedule_day_of_the_week_wedsday);
		final RadioButton rb_dw_thu=(RadioButton) dialog.findViewById(R.id.quick_task_edit_schedule_day_of_the_week_thursday);
		final RadioButton rb_dw_fri=(RadioButton) dialog.findViewById(R.id.quick_task_edit_schedule_day_of_the_week_friday);
		final RadioButton rb_dw_sat=(RadioButton) dialog.findViewById(R.id.quick_task_edit_schedule_day_of_the_week_satday);
		final LinearLayout ll_year=(LinearLayout)dialog.findViewById(R.id.quick_task_edit_schedule_ll_year);
		final LinearLayout ll_month=(LinearLayout)dialog.findViewById(R.id.quick_task_edit_schedule_ll_month);
		final LinearLayout ll_day=(LinearLayout)dialog.findViewById(R.id.quick_task_edit_schedule_ll_day);
		final Spinner sp_year=(Spinner) dialog.findViewById(R.id.quick_task_edit_schedule_year);
		final Spinner sp_mon=(Spinner) dialog.findViewById(R.id.quick_task_edit_schedule_month);
		final Spinner sp_day=(Spinner) dialog.findViewById(R.id.quick_task_edit_schedule_day);
		final Spinner sp_hour=(Spinner) dialog.findViewById(R.id.quick_task_edit_schedule_hours);
		final Spinner sp_min=(Spinner) dialog.findViewById(R.id.quick_task_edit_schedule_minutes);
		final Spinner sp_sound_type=(Spinner) dialog.findViewById(R.id.quick_task_edit_alarm_clock_sound_type);
		final LinearLayout ll_music=(LinearLayout)dialog.findViewById(R.id.quick_task_edit_alarm_clock_ll_music);
		final Button bt_sound_list=(Button)dialog.findViewById(R.id.quick_task_edit_alarm_clock_list_sound);
		final EditText et_sound_name=(EditText)dialog.findViewById(R.id.quick_task_edit_alarm_clock_sound_name);
		final Button bt_sound_playback=(Button)dialog.findViewById(R.id.quick_task_edit_alarm_clock_sound_play_back);
		final LinearLayout ll_ringtone=(LinearLayout)dialog.findViewById(R.id.quick_task_edit_alarm_clock_ll_ringtone);
		final Spinner sp_ringtone_name=(Spinner) dialog.findViewById(R.id.quick_task_edit_alarm_clock_ringtone_name);
		final Button bt_ringtone_playback=(Button)dialog.findViewById(R.id.quick_task_edit_alarm_clock_ringtone_play_back);

		CustomSpinnerAdapter adapterSoundType = new CustomSpinnerAdapter(mContext, android.R.layout.simple_spinner_item);
        setSpinnerSoundType(dialog,sp_sound_type,adapterSoundType,tv_sound_type.getText().toString());

        CustomSpinnerAdapter adapterYear = new CustomSpinnerAdapter(mContext, android.R.layout.simple_spinner_item);
        setSpinnerYear(dialog,sp_year,adapterYear,tv_day.getText().toString());

        CustomSpinnerAdapter adapterMonth = new CustomSpinnerAdapter(mContext, android.R.layout.simple_spinner_item);
        setSpinnerMonth(dialog,sp_mon,adapterMonth,tv_day.getText().toString());

        CustomSpinnerAdapter adapterDay = new CustomSpinnerAdapter(mContext, android.R.layout.simple_spinner_item);
        setSpinnerDay(dialog,sp_day,adapterDay,tv_day.getText().toString());

        CustomSpinnerAdapter adapterHour = new CustomSpinnerAdapter(mContext, android.R.layout.simple_spinner_item);
        setSpinnerHour(dialog,sp_hour,adapterHour,tv_time.getText().toString());

        CustomSpinnerAdapter adapterMin = new CustomSpinnerAdapter(mContext, android.R.layout.simple_spinner_item);
        setSpinnerMin(dialog,sp_min,adapterMin,tv_time.getText().toString());

        final CustomSpinnerAdapter adapterRingtoneName = new CustomSpinnerAdapter(mContext, android.R.layout.simple_spinner_item);        
        profMaint.setSpinnerRingtoneName(dialog, sp_ringtone_name,
        		adapterRingtoneName, tv_sound_name.getText().toString(), 
        		ProfileMaintenance.getRingtoneTypeInt(tv_sound_type.getText().toString()));

        setDayOfTheWeekButtonListener(rb_dw_sun,rb_dw_mon,rb_dw_tue,
        		rb_dw_wed,rb_dw_thu,rb_dw_fri,rb_dw_sat,tv_day_of_week.getText().toString());
        
        setDateTypeButtonListener(rb_datetype_sd,rb_datetype_ed,rb_datetype_dw,
        		ll_dw, ll_year, ll_month,ll_day,getScheduleDateType(tv_date_type));
        
		sp_sound_type.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				profMaint.stopMusicPlayBack();
				profMaint.stopRingtonePlayBack();
				if (pos==0) {
					ll_music.setVisibility(LinearLayout.VISIBLE);
					ll_ringtone.setVisibility(LinearLayout.GONE);
				} else {
					ll_music.setVisibility(LinearLayout.GONE);
					ll_ringtone.setVisibility(LinearLayout.VISIBLE);
					int rt=0;
					if (pos==1) rt=RingtoneManager.TYPE_ALARM;
					else if (pos==2) rt=RingtoneManager.TYPE_RINGTONE;
					String rname="";
					if (sp_ringtone_name.getSelectedItem()!=null)
						rname=sp_ringtone_name.getSelectedItem().toString();
			    	profMaint.setSpinnerRingtoneName(dialog,sp_ringtone_name,
		    			adapterRingtoneName,rname,rt);
			    	bt_ringtone_playback.setClickable(true);

				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
			
		});
		
        // Music Listingボタンの指定
		profMaint.setActionListSoundBtnListener(dialog,et_sound_name,null,
				null,bt_sound_list,bt_sound_playback);
		profMaint.setMusicPlayBackBtnListener(dialog, et_sound_name,
				null, null,bt_sound_playback);

		// Ringtone ボタンの指定
		profMaint.setRingtonePlayBackBtnListener(dialog, sp_sound_type, 
				sp_ringtone_name,
				null,null, bt_ringtone_playback);

		bt_ringtone_playback.setEnabled(true);
		bt_ringtone_playback.setClickable(true);
		
		if (tv_sound_type.getText().toString().equals(PROFILE_ACTION_TYPE_MUSIC))
			if (!tv_sound_type.getText().toString().equals(mContext.getString(R.string.msgs_quick_vlue_not_specified)))
				et_sound_name.setText(tv_sound_name.getText());
		
		// CANCELボタンの指定
		btnCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				profMaint.stopMusicPlayBack();
				profMaint.stopRingtonePlayBack();
				dialog.dismiss(); 
			}
		});
		// OKボタンの指定
		btnOK.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (sp_sound_type.getSelectedItem().toString().equals(PROFILE_ACTION_TYPE_MUSIC) &&
						et_sound_name.getText().toString().equals("")) {
					commonDlg.showCommonDialog(false, "E",  
							mContext.getString(R.string.msgs_edit_profile_action_mp_file_not_specified), "", null);
					return;
				}
				profMaint.stopMusicPlayBack();
				profMaint.stopRingtonePlayBack();
				dialog.dismiss();
				if (rb_datetype_sd.isChecked()) {
					tv_date_type.setText(mContext.getString(R.string.msgs_repeat_type_one_shot));
					tv_day.setText(
							sp_year.getSelectedItem().toString()+"/"+
							sp_mon.getSelectedItem().toString()+"/"+
							sp_day.getSelectedItem().toString());
				} else if (rb_datetype_ed.isChecked()) {
					tv_date_type.setText(mContext.getString(R.string.msgs_repeat_type_every_day));
					tv_day_of_week.setText("");
					tv_day.setText("");
				} else if (rb_datetype_dw.isChecked()) {
					tv_date_type.setText(mContext.getString(R.string.msgs_repeat_type_day_of_the_week));
					if (rb_dw_sun.isChecked()) tv_day_of_week.setText(dayOfTheWeekTable[0]);
					else if (rb_dw_mon.isChecked()) tv_day_of_week.setText(dayOfTheWeekTable[1]);
					else if (rb_dw_tue.isChecked()) tv_day_of_week.setText(dayOfTheWeekTable[2]);
					else if (rb_dw_wed.isChecked()) tv_day_of_week.setText(dayOfTheWeekTable[3]);
					else if (rb_dw_thu.isChecked()) tv_day_of_week.setText(dayOfTheWeekTable[4]);
					else if (rb_dw_fri.isChecked()) tv_day_of_week.setText(dayOfTheWeekTable[5]);
					else if (rb_dw_sat.isChecked()) tv_day_of_week.setText(dayOfTheWeekTable[6]);
					tv_day.setText("");
				}

				tv_time.setText(sp_hour.getSelectedItem().toString()+":"+
						sp_min.getSelectedItem().toString());
				
				if (sp_sound_type.getSelectedItem().toString().equals(PROFILE_ACTION_TYPE_MUSIC)) {
					tv_sound_type.setText(sp_sound_type.getSelectedItem().toString());
					tv_sound_name.setText(et_sound_name.getText());
				} else {
					tv_sound_type.setText(sp_sound_type.getSelectedItem().toString());
					tv_sound_name.setText(sp_ringtone_name.getSelectedItem().toString());
				}
				setScheduleViewVisibility(id,tv_date_type,tv_day_of_week,tv_day,tv_time);
				setAlarmClockEnableCheckBox(cb_enable, tv_sound_type,
						tv_sound_name);

			}
		});
		dialog.setOnCancelListener(new OnCancelListener(){
			@Override
			public void onCancel(DialogInterface arg0) {
				btnCancel.performClick();
			}
		});

		dialog.show();
	};

	final private void setDateTypeButtonListener(final RadioButton rb_datetype_sd,
			final RadioButton rb_datetype_ed, final RadioButton rb_datetype_dw,
			final LinearLayout ll_dw, final LinearLayout ll_year,
			final LinearLayout ll_month,final LinearLayout ll_day,
			String date_type){
		rb_datetype_sd.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if (isChecked) {
					ll_dw.setVisibility(LinearLayout.GONE);
					ll_year.setVisibility(LinearLayout.VISIBLE);
					ll_month.setVisibility(LinearLayout.VISIBLE);
					ll_day.setVisibility(LinearLayout.VISIBLE);
				}
			}
		});
		rb_datetype_ed.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if (isChecked) {
					ll_dw.setVisibility(LinearLayout.GONE);
					ll_year.setVisibility(LinearLayout.GONE);
					ll_month.setVisibility(LinearLayout.GONE);
					ll_day.setVisibility(LinearLayout.GONE);
				}
			}
		});
		rb_datetype_dw.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if (isChecked) {
					ll_dw.setVisibility(LinearLayout.VISIBLE);
					ll_year.setVisibility(LinearLayout.GONE);
					ll_month.setVisibility(LinearLayout.GONE);
					ll_day.setVisibility(LinearLayout.GONE);
				}
			}
		});
		if (date_type.equals(PROFILE_DATE_TIME_TYPE_ONE_SHOT)) rb_datetype_sd.setChecked(true);
		else if (date_type.equals(PROFILE_DATE_TIME_TYPE_EVERY_DAY)) rb_datetype_ed.setChecked(true);
		else if (date_type.equals(PROFILE_DATE_TIME_TYPE_DAY_OF_THE_WEEK)) rb_datetype_dw.setChecked(true);
		else rb_datetype_sd.setChecked(true);
	};

	final private void setDayOfTheWeekButtonListener(final RadioButton rb_dw_sun,
			final RadioButton rb_dw_mon, final RadioButton rb_dw_tue,
			final RadioButton rb_dw_wed, final RadioButton rb_dw_thu,
			final RadioButton rb_dw_fri, final RadioButton rb_dw_sat,
			String day_of_week) {
        rb_dw_sun.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if (isChecked) {
					rb_dw_mon.setChecked(false);
					rb_dw_tue.setChecked(false);
					rb_dw_wed.setChecked(false);
					rb_dw_thu.setChecked(false);
					rb_dw_fri.setChecked(false);
					rb_dw_sat.setChecked(false);
				}
			}
		});
        rb_dw_mon.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if (isChecked) {
					rb_dw_sun.setChecked(false);
					rb_dw_tue.setChecked(false);
					rb_dw_wed.setChecked(false);
					rb_dw_thu.setChecked(false);
					rb_dw_fri.setChecked(false);
					rb_dw_sat.setChecked(false);
				}
			}
		});
        rb_dw_tue.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if (isChecked) {
					rb_dw_sun.setChecked(false);
					rb_dw_mon.setChecked(false);
					rb_dw_wed.setChecked(false);
					rb_dw_thu.setChecked(false);
					rb_dw_fri.setChecked(false);
					rb_dw_sat.setChecked(false);
				}
			}
		});
        rb_dw_wed.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if (isChecked) {
					rb_dw_sun.setChecked(false);
					rb_dw_mon.setChecked(false);
					rb_dw_tue.setChecked(false);
					rb_dw_thu.setChecked(false);
					rb_dw_fri.setChecked(false);
					rb_dw_sat.setChecked(false);
				}
			}
		});
        rb_dw_thu.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if (isChecked) {
					rb_dw_sun.setChecked(false);
					rb_dw_mon.setChecked(false);
					rb_dw_tue.setChecked(false);
					rb_dw_wed.setChecked(false);
					rb_dw_fri.setChecked(false);
					rb_dw_sat.setChecked(false);
				}
			}
		});
        rb_dw_fri.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if (isChecked) {
					rb_dw_sun.setChecked(false);
					rb_dw_mon.setChecked(false);
					rb_dw_tue.setChecked(false);
					rb_dw_wed.setChecked(false);
					rb_dw_thu.setChecked(false);
					rb_dw_sat.setChecked(false);
				}
			}
		});
        rb_dw_sat.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if (isChecked) {
					rb_dw_sun.setChecked(false);
					rb_dw_mon.setChecked(false);
					rb_dw_tue.setChecked(false);
					rb_dw_wed.setChecked(false);
					rb_dw_thu.setChecked(false);
					rb_dw_fri.setChecked(false);
				}
			}
		});
		if (day_of_week.equals(dayOfTheWeekTable[0])) rb_dw_sun.setChecked(true);
		else if (day_of_week.equals(dayOfTheWeekTable[1])) rb_dw_mon.setChecked(true);
		else if (day_of_week.equals(dayOfTheWeekTable[2])) rb_dw_tue.setChecked(true);
		else if (day_of_week.equals(dayOfTheWeekTable[3])) rb_dw_wed.setChecked(true);
		else if (day_of_week.equals(dayOfTheWeekTable[4])) rb_dw_thu.setChecked(true);
		else if (day_of_week.equals(dayOfTheWeekTable[5])) rb_dw_fri.setChecked(true);
		else if (day_of_week.equals(dayOfTheWeekTable[6])) rb_dw_sat.setChecked(true);
	};
	
	final private void setSpinnerSoundType(Dialog dialog, Spinner spinner, 
			CustomSpinnerAdapter adapter, String selected) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mContext.getString(R.string.msgs_edit_profile_time_select_year));
        spinner.setAdapter(adapter);
        adapter.clear();
        adapter.add(PROFILE_ACTION_TYPE_MUSIC);
        adapter.add(PROFILE_ACTION_RINGTONE_TYPE_ALARM);
        adapter.add(PROFILE_ACTION_RINGTONE_TYPE_RINGTONE);
        if (selected.equals(PROFILE_ACTION_TYPE_MUSIC)) spinner.setSelection(0);
        if (selected.equals(PROFILE_ACTION_RINGTONE_TYPE_ALERT) ||
        		selected.equals(PROFILE_ACTION_RINGTONE_TYPE_ALARM)) spinner.setSelection(1);
        if (selected.equals(PROFILE_ACTION_RINGTONE_TYPE_RINGTONE)) spinner.setSelection(2);

	};

	final private void setSpinnerYear(Dialog dialog, Spinner spinner, 
			CustomSpinnerAdapter adapter, String selected) {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy",Locale.getDefault());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mContext.getString(R.string.msgs_edit_profile_time_select_year));
        spinner.setAdapter(adapter);
        adapter.clear();
        String yyyy=sdfDate.format(System.currentTimeMillis());
		int year=Integer.parseInt(yyyy);
		adapter.add(yyyy);
		adapter.add(""+(year+1));
		spinner.setSelection(0);

	};
	
	final private void setSpinnerMonth(Dialog dialog, Spinner spinner, 
			CustomSpinnerAdapter adapter, String selected) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mContext.getString(R.string.msgs_edit_profile_time_select_month));
        spinner.setAdapter(adapter);
        adapter.clear();
		for (int i=1;i<=12;i++) 
			if (i>9) adapter.add(""+i);
			else adapter.add("0"+i);
        if (!selected.equals("")) {
        	String[] mon=selected.split("/");
        	int selno=0;
        	if (mon!=null&&mon.length>=3&&mon[1]!=null) {
        		for (int i=0;i<adapter.getCount();i++) {
        			if (adapter.getItem(i).equals(mon[1])) {
        				selno=i;
        				break;
        			}
        		}
        	}
        	spinner.setSelection(selno);
        }
	};

	final private void setSpinnerDay(Dialog dialog, Spinner spinner, 
			CustomSpinnerAdapter adapter, String selected) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mContext.getString(R.string.msgs_edit_profile_time_select_day));
        spinner.setAdapter(adapter);
        adapter.clear();

		for (int i=1;i<=31;i++)
			if (i>9) adapter.add(""+i);
			else adapter.add("0"+i);
        if (!selected.equals("")) {
        	String[] day=selected.split("/");
        	int selno=0;
        	if (day!=null&&day.length>=3&&day[2]!=null) {
        		for (int i=0;i<adapter.getCount();i++) {
        			if (adapter.getItem(i).equals(day[2])) {
        				selno=i;
        				break;
        			}
        		}
        	}
        	spinner.setSelection(selno);
        }

	};
	
	final private void setSpinnerHour(Dialog dialog, Spinner spinner, 
			CustomSpinnerAdapter adapter, String selected) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mContext.getString(R.string.msgs_edit_profile_time_select_hour));
        spinner.setAdapter(adapter);
        adapter.clear();
        
		for (int i=0;i<=23;i++)
			if (i>9) adapter.add(""+i);
			else adapter.add("0"+i);
		
        if (selected!=null && !selected.equals("") &&
            	!mContext.getString(R.string.msgs_quick_vlue_not_specified).equals(selected)) {
    		if (!selected.equals("")) {
    			String hh="";
    			if (selected.indexOf(":")>0)
    				hh=selected.substring(0,selected.indexOf(":"));
    			if (!hh.equals("**") && !hh.equals("")) {
    				int hour=Integer.parseInt(hh);
    				spinner.setSelection(hour);
    			} else spinner.setSelection(0);
    		} else spinner.setSelection(0);
        } else spinner.setSelection(0);
	};
	
	final private void setSpinnerMin(Dialog dialog, Spinner spinner, 
			CustomSpinnerAdapter adapter, String selected) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mContext.getString(R.string.msgs_edit_profile_time_select_min));
        spinner.setAdapter(adapter);
        adapter.clear();

		for (int i=0;i<=59;i++) 
			if (i>9) adapter.add(""+i);
			else adapter.add("0"+i);

        if (selected!=null && !selected.equals("") &&
        	!mContext.getString(R.string.msgs_quick_vlue_not_specified).equals(selected)) {
    		if (!selected.equals("")) {
    			String hh=selected.substring(0,selected.indexOf(":"));
    			String mm=selected.replace(hh+":","");
    			if (!mm.equals("**") && !mm.equals("")) {
    				int min=Integer.parseInt(mm);
    				spinner.setSelection(min);
    			} else spinner.setSelection(0);
    		} else spinner.setSelection(0);
        } else spinner.setSelection(0);
        
	};

	final private void editTimeActivity(final CheckedTextView cb_enable,
			final String id,final TextView tv_date_type, 
			final TextView tv_day_of_week, final TextView tv_day, 
			final TextView tv_time, final TextView tv_activity) {
		// カスタムダイアログの生成
		final Dialog dialog = new Dialog(mContext);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(R.layout.quick_task_edit_time_activity);
		final TextView dlg_title = (TextView) dialog.findViewById(R.id.quick_task_edit_time_activity_title);		
		final Button btnCancel = (Button) dialog.findViewById(R.id.quick_task_edit_time_activity_cancel_btn);
		final Button btnOK = (Button) dialog.findViewById(R.id.quick_task_edit_time_activity_ok_btn);
		
		dlg_title.setText(mContext.getString(R.string.msgs_quick_hdr_time_activity)+id);
		
		CommonDialog.setDlgBoxSizeCompact(dialog);
		
		final RadioButton rb_datetype_dw=(RadioButton) dialog.findViewById(R.id.quick_task_edit_schedule_datetype_day_of_week);
		final RadioButton rb_datetype_ed=(RadioButton) dialog.findViewById(R.id.quick_task_edit_schedule_datetype_everyday);
		final RadioButton rb_datetype_sd=(RadioButton) dialog.findViewById(R.id.quick_task_edit_schedule_datetype_specificdate);
		final LinearLayout ll_dw=(LinearLayout)dialog.findViewById(R.id.quick_task_edit_schedule_day_of_the_week);
		final RadioButton rb_dw_sun=(RadioButton) dialog.findViewById(R.id.quick_task_edit_schedule_day_of_the_week_sunday);
		final RadioButton rb_dw_mon=(RadioButton) dialog.findViewById(R.id.quick_task_edit_schedule_day_of_the_week_monday);
		final RadioButton rb_dw_tue=(RadioButton) dialog.findViewById(R.id.quick_task_edit_schedule_day_of_the_week_tuesday);
		final RadioButton rb_dw_wed=(RadioButton) dialog.findViewById(R.id.quick_task_edit_schedule_day_of_the_week_wedsday);
		final RadioButton rb_dw_thu=(RadioButton) dialog.findViewById(R.id.quick_task_edit_schedule_day_of_the_week_thursday);
		final RadioButton rb_dw_fri=(RadioButton) dialog.findViewById(R.id.quick_task_edit_schedule_day_of_the_week_friday);
		final RadioButton rb_dw_sat=(RadioButton) dialog.findViewById(R.id.quick_task_edit_schedule_day_of_the_week_satday);
		final LinearLayout ll_year=(LinearLayout)dialog.findViewById(R.id.quick_task_edit_schedule_ll_year);
		final LinearLayout ll_month=(LinearLayout)dialog.findViewById(R.id.quick_task_edit_schedule_ll_month);
		final LinearLayout ll_day=(LinearLayout)dialog.findViewById(R.id.quick_task_edit_schedule_ll_day);
		final Spinner sp_year=(Spinner) dialog.findViewById(R.id.quick_task_edit_schedule_year);
		final Spinner sp_mon=(Spinner) dialog.findViewById(R.id.quick_task_edit_schedule_month);
		final Spinner sp_day=(Spinner) dialog.findViewById(R.id.quick_task_edit_schedule_day);
		final Spinner sp_hour=(Spinner) dialog.findViewById(R.id.quick_task_edit_schedule_hours);
		final Spinner sp_min=(Spinner) dialog.findViewById(R.id.quick_task_edit_schedule_minutes);
		final Spinner sp_activity=(Spinner) dialog.findViewById(R.id.quick_task_edit_time_activity_list);

        CustomSpinnerAdapter adapterYear = new CustomSpinnerAdapter(mContext, android.R.layout.simple_spinner_item);
        setSpinnerYear(dialog,sp_year,adapterYear,tv_day.getText().toString());

        CustomSpinnerAdapter adapterMonth = new CustomSpinnerAdapter(mContext, android.R.layout.simple_spinner_item);
        setSpinnerMonth(dialog,sp_mon,adapterMonth,tv_day.getText().toString());

        CustomSpinnerAdapter adapterDay = new CustomSpinnerAdapter(mContext, android.R.layout.simple_spinner_item);
        setSpinnerDay(dialog,sp_day,adapterDay,tv_day.getText().toString());

        CustomSpinnerAdapter adapterHour = new CustomSpinnerAdapter(mContext, android.R.layout.simple_spinner_item);
        setSpinnerHour(dialog,sp_hour,adapterHour,tv_time.getText().toString());

        CustomSpinnerAdapter adapterMin = new CustomSpinnerAdapter(mContext, android.R.layout.simple_spinner_item);
        setSpinnerMin(dialog,sp_min,adapterMin,tv_time.getText().toString());

        CustomSpinnerAdapter adapterActivity = new CustomSpinnerAdapter(mContext, android.R.layout.simple_spinner_item);
        profMaint.setSpinnerActivityName(dialog, sp_activity,
    			adapterActivity, tv_activity.getText().toString());
        int s_pos=sp_activity.getSelectedItemPosition();
        adapterActivity.remove(adapterActivity.getItem(0));
        if (s_pos!=0) s_pos--;
        sp_activity.setSelection(s_pos);
        
        setDayOfTheWeekButtonListener(rb_dw_sun,rb_dw_mon,rb_dw_tue,
        		rb_dw_wed,rb_dw_thu,rb_dw_fri,rb_dw_sat,tv_day_of_week.getText().toString());
        
        setDateTypeButtonListener(rb_datetype_sd,rb_datetype_ed,rb_datetype_dw,
        		ll_dw, ll_year, ll_month,ll_day,getScheduleDateType(tv_date_type));
        
		
		// CANCELボタンの指定
		btnCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss(); 
			}
		});
		// OKボタンの指定
		btnOK.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
				if (rb_datetype_sd.isChecked()) {
					tv_date_type.setText(mContext.getString(R.string.msgs_repeat_type_one_shot));
					tv_day.setText(
							sp_year.getSelectedItem().toString()+"/"+
							sp_mon.getSelectedItem().toString()+"/"+
							sp_day.getSelectedItem().toString());
				} else if (rb_datetype_ed.isChecked()) {
					tv_date_type.setText(mContext.getString(R.string.msgs_repeat_type_every_day));
					tv_day_of_week.setText("");
					tv_day.setText("");
				} else if (rb_datetype_dw.isChecked()) {
					tv_date_type.setText(mContext.getString(R.string.msgs_repeat_type_day_of_the_week));
					if (rb_dw_sun.isChecked()) tv_day_of_week.setText(dayOfTheWeekTable[0]);
					else if (rb_dw_mon.isChecked()) tv_day_of_week.setText(dayOfTheWeekTable[1]);
					else if (rb_dw_tue.isChecked()) tv_day_of_week.setText(dayOfTheWeekTable[2]);
					else if (rb_dw_wed.isChecked()) tv_day_of_week.setText(dayOfTheWeekTable[3]);
					else if (rb_dw_thu.isChecked()) tv_day_of_week.setText(dayOfTheWeekTable[4]);
					else if (rb_dw_fri.isChecked()) tv_day_of_week.setText(dayOfTheWeekTable[5]);
					else if (rb_dw_sat.isChecked()) tv_day_of_week.setText(dayOfTheWeekTable[6]);
					tv_day.setText("");
				}
				tv_time.setText(sp_hour.getSelectedItem().toString()+":"+
						sp_min.getSelectedItem().toString());
				tv_activity.setText(sp_activity.getSelectedItem().toString());
				setScheduleViewVisibility(id,tv_date_type,tv_day_of_week,tv_day,tv_time);
				setTimeActivityEnableCheckBox(cb_enable,tv_activity);
			}
		});
		dialog.setOnCancelListener(new OnCancelListener(){
			@Override
			public void onCancel(DialogInterface arg0) {
				btnCancel.performClick();
			}
		});
		
		dialog.show();
		
	};
	
	final static private boolean isQuickTaskUseBsh(CommonUtilities util) {
		return util.getPrefMgr().getBoolean(QUICK_TASK_PROFILE_USE_BEAN_SHELL, false);
	}

	final static private boolean isQuickTaskIgnoreAirplaneMode(CommonUtilities util) {
		return util.getPrefMgr().getBoolean(QUICK_TASK_PROFILE_IGNORE_AIRPLANE_MODE, false);
	}

	final static public boolean isQuickTaskAlarmClock01Enabled(CommonUtilities util) {
		return util.getPrefMgr().getBoolean(QUICK_TASK_ALARM_CLOCK01_ENABLED, false);
	}
	final static public boolean isQuickTaskAlarmClock02Enabled(CommonUtilities util) {
		return util.getPrefMgr().getBoolean(QUICK_TASK_ALARM_CLOCK02_ENABLED, false);
	}
	final static public boolean isQuickTaskAlarmClock03Enabled(CommonUtilities util) {
		return util.getPrefMgr().getBoolean(QUICK_TASK_ALARM_CLOCK03_ENABLED, false);
	}
	final static public boolean isQuickTaskTimerActivity01Enabled(CommonUtilities util) {
		return util.getPrefMgr().getBoolean(QUICK_TASK_TIME_ACTIVITY01_ENABLED, false);
	}
	final static public boolean isQuickTaskTimerActivity02Enabled(CommonUtilities util) {
		return util.getPrefMgr().getBoolean(QUICK_TASK_TIME_ACTIVITY02_ENABLED, false);
	}
	final static public boolean isQuickTaskTimerActivity03Enabled(CommonUtilities util) {
		return util.getPrefMgr().getBoolean(QUICK_TASK_TIME_ACTIVITY03_ENABLED, false);
	}

	final static public String getQuickTaskAlarmClock01SchedInfo(CommonUtilities util) {
		String result=util.getPrefMgr().getString(QUICK_TASK_ALARM_CLOCK01_DATE_TIME,"");
		return result;
	}
	final static public String getQuickTaskAlarmClock02SchedInfo(CommonUtilities util) {
		String result=util.getPrefMgr().getString(QUICK_TASK_ALARM_CLOCK02_DATE_TIME,"");
		return result;
	}
	final static public String getQuickTaskAlarmClock03SchedInfo(CommonUtilities util) {
		String result=util.getPrefMgr().getString(QUICK_TASK_ALARM_CLOCK03_DATE_TIME,"");
		return result;
	}
	final static public String getQuickTaskTimerActivity01SchedInfo(CommonUtilities util) {
		String result=util.getPrefMgr().getString(QUICK_TASK_TIME_ACTIVITY01_DATE_TIME,"");
		return result;
	}
	final static public String getQuickTaskTimerActivity02SchedInfo(CommonUtilities util) {
		String result=util.getPrefMgr().getString(QUICK_TASK_TIME_ACTIVITY02_DATE_TIME,"");
		return result;
	}
	final static public String getQuickTaskTimerActivity03SchedInfo(CommonUtilities util) {
		String result=util.getPrefMgr().getString(QUICK_TASK_TIME_ACTIVITY03_DATE_TIME,"");
		return result;
	}

	final static public boolean isQuickTaskAlarmClock01Configured(CommonUtilities util) {
		if (getQuickTaskAlarmClock01SchedInfo(util).equals("")) return false;
		else return true;
	}
	final static public boolean isQuickTaskAlarmClock02Configured(CommonUtilities util) {
		if (getQuickTaskAlarmClock02SchedInfo(util).equals("")) return false;
		else return true;
	}
	final static public boolean isQuickTaskAlarmClock03Configured(CommonUtilities util) {
		if (getQuickTaskAlarmClock03SchedInfo(util).equals("")) return false;
		else return true;
	}
	final static public boolean isQuickTaskTimerActivity01Configured(CommonUtilities util) {
		if (getQuickTaskTimerActivity01SchedInfo(util).equals("")) return false;
		else return true;
	}
	final static public boolean isQuickTaskTimerActivity02Configured(CommonUtilities util) {
		if (getQuickTaskTimerActivity02SchedInfo(util).equals("")) return false;
		else return true;
	}
	final static public boolean isQuickTaskTimerActivity03Configured(CommonUtilities util) {
		if (getQuickTaskTimerActivity03SchedInfo(util).equals("")) return false;
		else return true;
	}

	final private void setQuickTaskListener(final Dialog dialog) {
		final CheckedTextView cb_use_bsh=(CheckedTextView) dialog.findViewById(R.id.quick_task_use_bean_shell);
		setDefaultCheckedTextViewListener(cb_use_bsh);
		final CheckedTextView cb_ignore_airplane=(CheckedTextView) dialog.findViewById(R.id.quick_task_ignore_airplane_mode);
		setDefaultCheckedTextViewListener(cb_ignore_airplane);
		final Button btn_cancel=(Button) dialog.findViewById(R.id.quick_task_view_cancel);
		final Button btn_apply=(Button) dialog.findViewById(R.id.quick_task_view_apply);
		
		cb_use_bsh.setChecked(isQuickTaskUseBsh(util));
		
		cb_ignore_airplane.setChecked(isQuickTaskIgnoreAirplaneMode(util));
		
		btn_cancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		btn_apply.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				NotifyEvent ntfy=new NotifyEvent(mContext);
				ntfy.setListener(new NotifyEventListener(){
					@Override
					public void positiveResponse(Context c, Object[] o) {
						applyChanges(dialog);
						boolean qa=ProfileUtilities.isQuickTaskProfileActivated(util,profileAdapter);
						ProfileUtilities.deleteProfileGroup(util,profileAdapter, QUICK_TASK_GROUP_NAME);
						buildQuickTaskProfile(mContext, profileAdapter,
								util, QUICK_TASK_GROUP_NAME);
						ProfileUtilities.setQuickTaskProfileActivated(util,profileAdapter, qa);
						profileAdapter.sort();
						ntfyUpdateGroup.notifyToListener(POSITIVE, null);

						dialog.dismiss();
					}
					@Override
					public void negativeResponse(Context c, Object[] o) {}
				});
				commonDlg.showCommonDialog(true, "W", 
						mContext.getString(R.string.msgs_quick_profile_gen_dlg_msg),  "", ntfy);
			}
		});
		
		dialog.setOnCancelListener(new OnCancelListener(){
			@Override
			public void onCancel(DialogInterface arg0) {
				btn_cancel.performClick();
			}
		});
	};
	
	final private void applyChanges(final Dialog dialog) {
		SharedPreferences prefs=util.getPrefMgr();
		
		prefs.edit().putString(QUICK_TASK_VERSION_KEY, QUICK_TASK_CURRENT_VERSION);

		final CheckedTextView cb_use_bsh=(CheckedTextView) dialog.findViewById(R.id.quick_task_use_bean_shell);
		prefs.edit().putBoolean(QUICK_TASK_PROFILE_USE_BEAN_SHELL,cb_use_bsh.isChecked()).commit();

		final CheckedTextView cb_ignore_airplane=(CheckedTextView) dialog.findViewById(R.id.quick_task_ignore_airplane_mode);
		prefs.edit().putBoolean(QUICK_TASK_PROFILE_IGNORE_AIRPLANE_MODE,cb_ignore_airplane.isChecked()).commit();

		final CheckedTextView cb_wifi_screen_locked=(CheckedTextView) dialog.findViewById(R.id.quick_task_view_wifi_screen_locked);
		final RadioButton rb_wifi_screen_locked_rb_ac=(RadioButton) dialog.findViewById(R.id.quick_task_view_wifi_screen_locked_rb_ac);
		final CheckedTextView cb_wifi_wifi_on=(CheckedTextView) dialog.findViewById(R.id.quick_task_view_wifi_wifi_on);
		final RadioButton rb_wifi_wifi_on_rb_ac=(RadioButton) dialog.findViewById(R.id.quick_task_view_wifi_wifi_on_rb_ac);
		final CheckedTextView cb_wifi_screen_unlocked=(CheckedTextView) dialog.findViewById(R.id.quick_task_view_wifi_screen_unlocked);
		final RadioButton rb_wifi_screen_unlocked_rb_ac=(RadioButton) dialog.findViewById(R.id.quick_task_view_wifi_screen_unlocked_rb_ac);
		prefs.edit().putBoolean(QUICK_TASK_WIFI_SCREEN_LOCKED,cb_wifi_screen_locked.isChecked())
		.putBoolean(QUICK_TASK_WIFI_SCREEN_LOCKED_AC,rb_wifi_screen_locked_rb_ac.isChecked())
		.putBoolean(QUICK_TASK_WIFI_WIFI_ON,cb_wifi_wifi_on.isChecked())
		.putBoolean(QUICK_TASK_WIFI_WIFI_ON_AC,rb_wifi_wifi_on_rb_ac.isChecked())
		.putBoolean(QUICK_TASK_WIFI_SCREEN_UNLOCKED,cb_wifi_screen_unlocked.isChecked())
		.putBoolean(QUICK_TASK_WIFI_SCREEN_UNLOCKED_AC,rb_wifi_screen_unlocked_rb_ac.isChecked())
		.commit();

		final CheckedTextView cb_bt_screen_locked=(CheckedTextView) dialog.findViewById(R.id.quick_task_view_bt_screen_locked);
		final RadioButton rb_bt_screen_locked_rb_ac=(RadioButton) dialog.findViewById(R.id.quick_task_view_bt_screen_locked_rb_ac);
		final CheckedTextView cb_bt_bt_on=(CheckedTextView) dialog.findViewById(R.id.quick_task_view_bt_bt_on);
		final RadioButton rb_bt_bt_on_rb_ac=(RadioButton) dialog.findViewById(R.id.quick_task_view_bt_bt_on_rb_ac);
		final CheckedTextView cb_bt_screen_unlocked=(CheckedTextView) dialog.findViewById(R.id.quick_task_view_bt_screen_unlocked);
		final RadioButton rb_bt_screen_unlocked_rb_ac=(RadioButton) dialog.findViewById(R.id.quick_task_view_bt_screen_unlocked_rb_ac);

		prefs.edit().putBoolean(QUICK_TASK_BT_SCREEN_LOCKED,cb_bt_screen_locked.isChecked())
		.putBoolean(QUICK_TASK_BT_SCREEN_LOCKED_AC,rb_bt_screen_locked_rb_ac.isChecked())
		.putBoolean(QUICK_TASK_BT_BT_ON,cb_bt_bt_on.isChecked())
		.putBoolean(QUICK_TASK_BT_BT_ON_AC,rb_bt_bt_on_rb_ac.isChecked())
		.putBoolean(QUICK_TASK_BT_SCREEN_UNLOCKED,cb_bt_screen_unlocked.isChecked())
		.putBoolean(QUICK_TASK_BT_SCREEN_UNLOCKED_AC,rb_bt_screen_unlocked_rb_ac.isChecked())
		.commit();
		final CheckedTextView cb_screen_proximity_undetected=(CheckedTextView) dialog.findViewById(R.id.quick_task_view_screen_proximity_undetected);

		prefs.edit().putBoolean(QUICK_TASK_SCREEN_PROXIMITY_UNDETECTED,cb_screen_proximity_undetected.isChecked())
		.commit();

		View ac01=(View) dialog.findViewById(R.id.quick_task_view_alarm_clock_01);
		View ac02=(View) dialog.findViewById(R.id.quick_task_view_alarm_clock_02);
		View ac03=(View) dialog.findViewById(R.id.quick_task_view_alarm_clock_03);
		final CheckedTextView cb_alarm_clock01_enable=(CheckedTextView) ac01.findViewById(R.id.quick_task_view_alarm_clock_enable);
//		final Button btn_alarm_clock01_btn=(Button) ac01.findViewById(R.id.quick_task_view_alarm_clock_edit);
		final TextView tv_alarm_clock01_datetype=(TextView) ac01.findViewById(R.id.quick_task_view_alarm_clock_datetype);
		final TextView tv_alarm_clock01_day_of_week=(TextView) ac01.findViewById(R.id.quick_task_view_alarm_clock_day_of_week);
		final TextView tv_alarm_clock01_day=(TextView) ac01.findViewById(R.id.quick_task_view_alarm_clock_day);
		final TextView tv_alarm_clock01_time=(TextView) ac01.findViewById(R.id.quick_task_view_alarm_clock_time);
		final TextView tv_alarm_clock01_sound_type=(TextView) ac01.findViewById(R.id.quick_task_view_alarm_clock_sound_type);
		final TextView tv_alarm_clock01_sound_name=(TextView) ac01.findViewById(R.id.quick_task_view_alarm_clock_sound_name);

		final CheckedTextView cb_alarm_clock02_enable=(CheckedTextView) ac02.findViewById(R.id.quick_task_view_alarm_clock_enable);
//		final Button btn_alarm_clock02_btn=(Button) ac02.findViewById(R.id.quick_task_view_alarm_clock_edit);
		final TextView tv_alarm_clock02_day_of_week=(TextView) ac02.findViewById(R.id.quick_task_view_alarm_clock_day_of_week);
		final TextView tv_alarm_clock02_datetype=(TextView) ac02.findViewById(R.id.quick_task_view_alarm_clock_datetype);
		final TextView tv_alarm_clock02_day=(TextView) ac02.findViewById(R.id.quick_task_view_alarm_clock_day);
		final TextView tv_alarm_clock02_time=(TextView) ac02.findViewById(R.id.quick_task_view_alarm_clock_time);
		final TextView tv_alarm_clock02_sound_type=(TextView) ac02.findViewById(R.id.quick_task_view_alarm_clock_sound_type);
		final TextView tv_alarm_clock02_sound_name=(TextView) ac02.findViewById(R.id.quick_task_view_alarm_clock_sound_name);

		final CheckedTextView cb_alarm_clock03_enable=(CheckedTextView) ac03.findViewById(R.id.quick_task_view_alarm_clock_enable);
//		final Button btn_alarm_clock03_btn=(Button) ac03.findViewById(R.id.quick_task_view_alarm_clock_edit);
		final TextView tv_alarm_clock03_day_of_week=(TextView) ac03.findViewById(R.id.quick_task_view_alarm_clock_day_of_week);
		final TextView tv_alarm_clock03_datetype=(TextView) ac03.findViewById(R.id.quick_task_view_alarm_clock_datetype);
		final TextView tv_alarm_clock03_day=(TextView) ac03.findViewById(R.id.quick_task_view_alarm_clock_day);
		final TextView tv_alarm_clock03_time=(TextView) ac03.findViewById(R.id.quick_task_view_alarm_clock_time);
		final TextView tv_alarm_clock03_sound_type=(TextView) ac03.findViewById(R.id.quick_task_view_alarm_clock_sound_type);
		final TextView tv_alarm_clock03_sound_name=(TextView) ac03.findViewById(R.id.quick_task_view_alarm_clock_sound_name);

		prefs.edit().putBoolean(QUICK_TASK_ALARM_CLOCK01_ENABLED,cb_alarm_clock01_enable.isChecked())
		.putString(QUICK_TASK_ALARM_CLOCK01_DATE_TYPE,getScheduleDateType(tv_alarm_clock01_datetype))
		.putString(QUICK_TASK_ALARM_CLOCK01_DAY_OF_WEEK,getScheduleDayOfWeek(tv_alarm_clock01_day_of_week))
		.putString(QUICK_TASK_ALARM_CLOCK01_DATE_DAY,getScheduleDay(tv_alarm_clock01_day))
		.putString(QUICK_TASK_ALARM_CLOCK01_DATE_TIME,getScheduleTime(tv_alarm_clock01_time))
		.putString(QUICK_TASK_ALARM_CLOCK01_SOUND_TYPE,getScheduleSoundType(tv_alarm_clock01_sound_type))
		.putString(QUICK_TASK_ALARM_CLOCK01_SOUND_NAME,getScheduleSound(tv_alarm_clock01_sound_name))
		.putString(QUICK_TASK_ALARM_CLOCK01_RINGTONE_PATH,setRingtonePath(getScheduleSoundType(tv_alarm_clock01_sound_type),getScheduleSoundType(tv_alarm_clock01_sound_name)))
		
		.putBoolean(QUICK_TASK_ALARM_CLOCK02_ENABLED,cb_alarm_clock02_enable.isChecked())
		.putString(QUICK_TASK_ALARM_CLOCK02_DATE_TYPE,getScheduleDateType(tv_alarm_clock02_datetype))
		.putString(QUICK_TASK_ALARM_CLOCK02_DAY_OF_WEEK,getScheduleDayOfWeek(tv_alarm_clock02_day_of_week))
		.putString(QUICK_TASK_ALARM_CLOCK02_DATE_DAY,getScheduleDay(tv_alarm_clock02_day))
		.putString(QUICK_TASK_ALARM_CLOCK02_DATE_TIME,getScheduleTime(tv_alarm_clock02_time))
		.putString(QUICK_TASK_ALARM_CLOCK02_SOUND_TYPE,getScheduleSoundType(tv_alarm_clock02_sound_type))
		.putString(QUICK_TASK_ALARM_CLOCK02_SOUND_NAME,getScheduleSound(tv_alarm_clock02_sound_name))
		.putString(QUICK_TASK_ALARM_CLOCK02_RINGTONE_PATH,setRingtonePath(getScheduleSoundType(tv_alarm_clock02_sound_type),getScheduleSoundType(tv_alarm_clock02_sound_name)))

		.putBoolean(QUICK_TASK_ALARM_CLOCK03_ENABLED,cb_alarm_clock03_enable.isChecked())
		.putString(QUICK_TASK_ALARM_CLOCK03_DATE_TYPE,getScheduleDateType(tv_alarm_clock03_datetype))
		.putString(QUICK_TASK_ALARM_CLOCK03_DAY_OF_WEEK,getScheduleDayOfWeek(tv_alarm_clock03_day_of_week))
		.putString(QUICK_TASK_ALARM_CLOCK03_DATE_DAY,getScheduleDay(tv_alarm_clock03_day))
		.putString(QUICK_TASK_ALARM_CLOCK03_DATE_TIME,getScheduleTime(tv_alarm_clock03_time))
		.putString(QUICK_TASK_ALARM_CLOCK03_SOUND_TYPE,getScheduleSoundType(tv_alarm_clock03_sound_type))
		.putString(QUICK_TASK_ALARM_CLOCK03_SOUND_NAME,getScheduleSound(tv_alarm_clock03_sound_name))
		.putString(QUICK_TASK_ALARM_CLOCK03_RINGTONE_PATH,setRingtonePath(getScheduleSoundType(tv_alarm_clock03_sound_type),getScheduleSoundType(tv_alarm_clock03_sound_name)))
		.commit();
      
		View ta01=(View) dialog.findViewById(R.id.quick_task_view_time_activity_01);
		View ta02=(View) dialog.findViewById(R.id.quick_task_view_time_activity_02);
		View ta03=(View) dialog.findViewById(R.id.quick_task_view_time_activity_03);
		final CheckedTextView cb_time_activity01_enable=(CheckedTextView) ta01.findViewById(R.id.quick_task_view_time_activity_enable);
//		final Button btn_time_activity01_btn=(Button) ta01.findViewById(R.id.quick_task_view_time_activity_edit);
		final TextView tv_time_activity01_datetype=(TextView) ta01.findViewById(R.id.quick_task_view_time_activity_datetype);
		final TextView tv_time_activity01_day_of_week=(TextView) ta01.findViewById(R.id.quick_task_view_time_activity_day_of_week);
		final TextView tv_time_activity01_day=(TextView) ta01.findViewById(R.id.quick_task_view_time_activity_day);
		final TextView tv_time_activity01_time=(TextView) ta01.findViewById(R.id.quick_task_view_time_activity_time);
		final TextView tv_time_activity01_activity=(TextView) ta01.findViewById(R.id.quick_task_view_time_activity_activity);

		final CheckedTextView cb_time_activity02_enable=(CheckedTextView) ta02.findViewById(R.id.quick_task_view_time_activity_enable);
//		final Button btn_time_activity02_btn=(Button) ta02.findViewById(R.id.quick_task_view_time_activity_edit);
		final TextView tv_time_activity02_datetype=(TextView) ta02.findViewById(R.id.quick_task_view_time_activity_datetype);
		final TextView tv_time_activity02_day_of_week=(TextView) ta02.findViewById(R.id.quick_task_view_time_activity_day_of_week);
		final TextView tv_time_activity02_day=(TextView) ta02.findViewById(R.id.quick_task_view_time_activity_day);
		final TextView tv_time_activity02_time=(TextView) ta02.findViewById(R.id.quick_task_view_time_activity_time);
		final TextView tv_time_activity02_activity=(TextView) ta02.findViewById(R.id.quick_task_view_time_activity_activity);

		final CheckedTextView cb_time_activity03_enable=(CheckedTextView) ta03.findViewById(R.id.quick_task_view_time_activity_enable);
//		final Button btn_time_activity03_btn=(Button) ta03.findViewById(R.id.quick_task_view_time_activity_edit);
		final TextView tv_time_activity03_datetype=(TextView) ta03.findViewById(R.id.quick_task_view_time_activity_datetype);
		final TextView tv_time_activity03_day_of_week=(TextView) ta03.findViewById(R.id.quick_task_view_time_activity_day_of_week);
		final TextView tv_time_activity03_day=(TextView) ta03.findViewById(R.id.quick_task_view_time_activity_day);
		final TextView tv_time_activity03_time=(TextView) ta03.findViewById(R.id.quick_task_view_time_activity_time);
		final TextView tv_time_activity03_activity=(TextView) ta03.findViewById(R.id.quick_task_view_time_activity_activity);

		prefs.edit().putBoolean(QUICK_TASK_TIME_ACTIVITY01_ENABLED,cb_time_activity01_enable.isChecked())
		.putString(QUICK_TASK_TIME_ACTIVITY01_DATE_TYPE,getScheduleDateType(tv_time_activity01_datetype))
		.putString(QUICK_TASK_TIME_ACTIVITY01_DAY_OF_WEEK,getScheduleDayOfWeek(tv_time_activity01_day_of_week))
		.putString(QUICK_TASK_TIME_ACTIVITY01_DATE_DAY,getScheduleDay(tv_time_activity01_day))
		.putString(QUICK_TASK_TIME_ACTIVITY01_DATE_TIME,getScheduleTime(tv_time_activity01_time))
		.putString(QUICK_TASK_TIME_ACTIVITY01_ACTIVITY,getScheduleActivity(tv_time_activity01_activity))

		.putBoolean(QUICK_TASK_TIME_ACTIVITY02_ENABLED,cb_time_activity02_enable.isChecked())
		.putString(QUICK_TASK_TIME_ACTIVITY02_DATE_TYPE,getScheduleDateType(tv_time_activity02_datetype))
		.putString(QUICK_TASK_TIME_ACTIVITY02_DAY_OF_WEEK,getScheduleDayOfWeek(tv_time_activity02_day_of_week))
		.putString(QUICK_TASK_TIME_ACTIVITY02_DATE_DAY,getScheduleDay(tv_time_activity02_day))
		.putString(QUICK_TASK_TIME_ACTIVITY02_DATE_TIME,getScheduleTime(tv_time_activity02_time))
		.putString(QUICK_TASK_TIME_ACTIVITY02_ACTIVITY,getScheduleActivity(tv_time_activity02_activity))

		.putBoolean(QUICK_TASK_TIME_ACTIVITY03_ENABLED,cb_time_activity03_enable.isChecked())
		.putString(QUICK_TASK_TIME_ACTIVITY02_DATE_TYPE,getScheduleDateType(tv_time_activity03_datetype))
		.putString(QUICK_TASK_TIME_ACTIVITY03_DAY_OF_WEEK,getScheduleDayOfWeek(tv_time_activity03_day_of_week))
		.putString(QUICK_TASK_TIME_ACTIVITY03_DATE_DAY,getScheduleDay(tv_time_activity03_day))
		.putString(QUICK_TASK_TIME_ACTIVITY03_DATE_TIME,getScheduleTime(tv_time_activity03_time))
		.putString(QUICK_TASK_TIME_ACTIVITY03_ACTIVITY,getScheduleActivity(tv_time_activity03_activity))
		.commit();

	};

	final static public void setWifiControlEnabled(CommonUtilities util, boolean enabled) {
		SharedPreferences prefs=util.getPrefMgr();
		prefs.edit().putBoolean(QUICK_TASK_WIFI_SCREEN_LOCKED,enabled)
		.putBoolean(QUICK_TASK_WIFI_WIFI_ON,enabled)
		.putBoolean(QUICK_TASK_WIFI_SCREEN_UNLOCKED,enabled)
		.commit();
	};
	final static public void setBtControlEnabled(CommonUtilities util, boolean enabled) {
		SharedPreferences prefs=util.getPrefMgr();
		prefs.edit().putBoolean(QUICK_TASK_BT_SCREEN_LOCKED,enabled)
		.putBoolean(QUICK_TASK_BT_BT_ON,enabled)
		.putBoolean(QUICK_TASK_BT_SCREEN_UNLOCKED,enabled)
		.commit();
	};
	final static public void setScreenControlEnabled(CommonUtilities util, boolean enabled) {
		SharedPreferences prefs=util.getPrefMgr();
		prefs.edit().putBoolean(QUICK_TASK_SCREEN_PROXIMITY_DETECTED,enabled)
		.putBoolean(QUICK_TASK_SCREEN_PROXIMITY_DETECTED_IGNORE_LANDSCAPE,enabled)
		.putBoolean(QUICK_TASK_SCREEN_PROXIMITY_UNDETECTED,enabled)
		.putBoolean(QUICK_TASK_SCREEN_LIGHT_DETECTED,enabled)
		.putBoolean(QUICK_TASK_SCREEN_LIGHT_UNDETECTED,enabled)
		.commit();
	};
	final static public void setAlarmClock01Enabled(CommonUtilities util) {
		SharedPreferences prefs=util.getPrefMgr();
		prefs.edit().putBoolean(QUICK_TASK_ALARM_CLOCK01_ENABLED,false).commit();
	};
	final static public void setAlarmClock01Enabled(CommonUtilities util, boolean enabled) {
		SharedPreferences prefs=util.getPrefMgr();
		prefs.edit().putBoolean(QUICK_TASK_ALARM_CLOCK01_ENABLED,enabled).commit();
	};
	final static public void setAlarmClock02Enabled(CommonUtilities util) {
		SharedPreferences prefs=util.getPrefMgr();
		prefs.edit().putBoolean(QUICK_TASK_ALARM_CLOCK02_ENABLED,false).commit();
	};
	final static public void setAlarmClock02Enabled(CommonUtilities util, boolean enabled) {
		SharedPreferences prefs=util.getPrefMgr();
		prefs.edit().putBoolean(QUICK_TASK_ALARM_CLOCK02_ENABLED,enabled).commit();
	};
	final static public void setAlarmClock03Enabled(CommonUtilities util) {
		SharedPreferences prefs=util.getPrefMgr();
		prefs.edit().putBoolean(QUICK_TASK_ALARM_CLOCK03_ENABLED,false).commit();
	};
	final static public void setAlarmClock03Enabled(CommonUtilities util, boolean enabled) {
		SharedPreferences prefs=util.getPrefMgr();
		prefs.edit().putBoolean(QUICK_TASK_ALARM_CLOCK03_ENABLED,enabled).commit();
	};
	final static public void setTimeActivity01Enabled(CommonUtilities util) {
		SharedPreferences prefs=util.getPrefMgr();
		prefs.edit().putBoolean(QUICK_TASK_TIME_ACTIVITY01_ENABLED,false).commit();
	};
	final static public void setTimeActivity01Enabled(CommonUtilities util, boolean enabled) {
		SharedPreferences prefs=util.getPrefMgr();
		prefs.edit().putBoolean(QUICK_TASK_TIME_ACTIVITY01_ENABLED,enabled).commit();
	};
	final static public void setTimeActivity02Enabled(CommonUtilities util) {
		SharedPreferences prefs=util.getPrefMgr();
		prefs.edit().putBoolean(QUICK_TASK_TIME_ACTIVITY02_ENABLED,false).commit();
	};
	final static public void setTimeActivity02Enabled(CommonUtilities util, boolean enabled) {
		SharedPreferences prefs=util.getPrefMgr();
		prefs.edit().putBoolean(QUICK_TASK_TIME_ACTIVITY02_ENABLED,enabled).commit();
	};
	final static public void setTimeActivity03Enabled(CommonUtilities util) {
		SharedPreferences prefs=util.getPrefMgr();
		prefs.edit().putBoolean(QUICK_TASK_TIME_ACTIVITY03_ENABLED,false).commit();
	};
	final static public void setTimeActivity03Enabled(CommonUtilities util, boolean enabled) {
		SharedPreferences prefs=util.getPrefMgr();
		prefs.edit().putBoolean(QUICK_TASK_TIME_ACTIVITY03_ENABLED,enabled).commit();
	};
	
	final private String setRingtonePath(String s_type, String s_name) {
		if (s_type.equals(PROFILE_ACTION_TYPE_MUSIC)) return "";
		return profMaint.getRingtonePath(s_type, s_name);
	};
	
	final static public void buildQuickTaskProfile(Context context, 
			AdapterProfileList pfa, CommonUtilities util, String grp_name) {
		if (isQuickTaskUseBsh(util)) {
			buildQuickTaskWifiBsh(context, pfa, util, grp_name,PROFILE_ERROR_NOTIFICATION_DISABLED);
			buildQuickTaskBtBsh(context, pfa, util, grp_name,PROFILE_ERROR_NOTIFICATION_DISABLED);
			buildQuickTaskScreenBsh(context, pfa, util, grp_name,PROFILE_ERROR_NOTIFICATION_DISABLED);
			buildPowerSourceChangeCancelBsh(context, pfa, util, grp_name,PROFILE_ERROR_NOTIFICATION_DISABLED);
			buildQuickTaskAlarmClockBsh(context, pfa, util, grp_name,PROFILE_ERROR_NOTIFICATION_DISABLED);
			buildQuickTaskTimeActivityBsh(context, pfa, util, grp_name,PROFILE_ERROR_NOTIFICATION_DISABLED);
			util.addLogMsg("I", "Building the Bsh QuickTaskProfile is complete, Version="+QUICK_TASK_CURRENT_VERSION);
		} else {
			buildQuickTaskWifiTaFunc(context, pfa, util, grp_name,PROFILE_ERROR_NOTIFICATION_DISABLED);
			buildQuickTaskBtTaFunc(context, pfa, util, grp_name,PROFILE_ERROR_NOTIFICATION_DISABLED);
			buildQuickTaskScreenTaFunc(context, pfa, util, grp_name,PROFILE_ERROR_NOTIFICATION_DISABLED);
			buildPowerSourceChangeCancelTaFunc(context, pfa, util, grp_name,PROFILE_ERROR_NOTIFICATION_DISABLED);
			buildQuickTaskAlarmClockTaFunc(context, pfa, util, grp_name,PROFILE_ERROR_NOTIFICATION_DISABLED);
			buildQuickTaskTimeActivityTaFunc(context, pfa, util, grp_name,PROFILE_ERROR_NOTIFICATION_DISABLED);
			util.addLogMsg("I", "Building the TaskAutomation QuickTaskProfile is complete, Version="+QUICK_TASK_CURRENT_VERSION);
		}
		util.getPrefMgr().edit().putString(QUICK_TASK_VERSION_KEY, QUICK_TASK_CURRENT_VERSION).commit();
	};

	final static public void buildQuickTaskProfile(Context context, 
			ArrayList<ProfileListItem>prof_list, CommonUtilities util, String grp_name) {
		AdapterProfileList pfa=new AdapterProfileList(context, 0, prof_list);
		buildQuickTaskProfile(context,pfa,util,grp_name);
	};

	final static private void buildPowerSourceChangeCancelBsh(Context context, AdapterProfileList pfa, 
			CommonUtilities util, String grp_name, String prof_notification) {
		SharedPreferences pref=util.getPrefMgr();
		if (pref.getBoolean(QUICK_TASK_WIFI_SCREEN_LOCKED_AC,true) ||
				pref.getBoolean(QUICK_TASK_BT_SCREEN_LOCKED_AC,true)) {
			ProfileListItem tpli;
			ArrayList<String> act;
			ArrayList<String> trig;
			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			trig.add(BUILTIN_EVENT_POWER_SOURCE_CHANGED_AC);
			act.add("#QT-PWR.To-AC");
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-PWR.To-AC", 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);
			
			String script_text=new String("");
			script_text+="TaCmd.cancelTaskByEventPowerSourceChangedBattery();"+"\n";
			tpli= new ProfileListItem();
			tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_ACTION,"#QT-PWR.To-AC", 
					PROFILE_ENABLED,script_text);
			pfa.addProfItem(tpli);
		}
	}
	
	final static private void buildQuickTaskWifiBsh(Context context, AdapterProfileList pfa, 
			CommonUtilities util, String grp_name, String prof_notification) {
		SharedPreferences pref=util.getPrefMgr();
		ProfileListItem tpli;
		ArrayList<String> act;
		ArrayList<String> trig;
		boolean profile_added=false;

		if (pref.getBoolean(QUICK_TASK_WIFI_SCREEN_LOCKED,false)) {
			profile_added=true;
			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			trig.add(BUILTIN_EVENT_SCREEN_LOCKED);
			act.add("#QT-Wifi-Screen-Locked");
			String script_text="";
			script_text+="TaCmd.cancelTaskByEventScreenUnlocked();"+"\n";
			script_text+="if (TaCmd.isWifiActive()) {"+"\n";
			script_text+="  TaCmd.waitSeconds(60);"+"\n";
			script_text+="  if (TaCmd.isScreenLocked()) {"+"\n";
			if (pref.getBoolean(QUICK_TASK_WIFI_SCREEN_LOCKED_AC,true)) {
				script_text+="    if (!TaCmd.isBatteryCharging()) {"+"\n";
				script_text+="        TaCmd.setWifiOff();"+"\n";
				script_text+="    }"+"\n";
			} else {
				script_text+="    TaCmd.setWifiOff();"+"\n";
			}
			script_text+="  }"+"\n";
			script_text+="}"+"\n";
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-Wifi-Screen-Locked", 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);

			tpli= new ProfileListItem();
			tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_ACTION,"#QT-Wifi-Screen-Locked", 
					PROFILE_ENABLED,script_text);
			pfa.addProfItem(tpli);

			if (pref.getBoolean(QUICK_TASK_WIFI_SCREEN_LOCKED_AC,true)) {
				act=new ArrayList<String>();
				trig=new ArrayList<String>();
				trig.add(BUILTIN_EVENT_POWER_SOURCE_CHANGED_BATTERY);
				act.add("#QT-Wifi-PWR.To-Battery");
				tpli= new ProfileListItem();
				tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
						PROFILE_TYPE_TASK,"#QT-Wifi-PWR.To-Battery", 
						PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
						prof_notification,act,trig);
				pfa.addProfItem(tpli);
				
				script_text=new String("");;
				script_text+="if (TaCmd.isScreenLocked()) {"+"\n";
				script_text+="  TaCmd.waitSeconds(60);"+"\n";
				script_text+="  if (!TaCmd.isBatteryCharging()) {"+"\n";
				script_text+="    if (TaCmd.isScreenLocked()) {"+"\n";
				script_text+="      TaCmd.setWifiOff();"+"\n";
				script_text+="    }"+"\n";
				script_text+="  }"+"\n";
				script_text+="}"+"\n";
				tpli= new ProfileListItem();
				tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
						PROFILE_TYPE_ACTION,"#QT-Wifi-PWR.To-Battery", 
						PROFILE_ENABLED,script_text);
				pfa.addProfItem(tpli);
			}
		}
		
		if (pref.getBoolean(QUICK_TASK_WIFI_WIFI_ON,false)) {
			profile_added=true;
			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			trig.add(BUILTIN_EVENT_WIFI_ON);
			act.add("#QT-Wifi-On");
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-Wifi-On", 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);
			
			String script_text=new String("");
			script_text+="TaCmd.waitUntilWifiConnected(60);"+"\n";
			script_text+="if (!TaCmd.isWifiConnected()) {"+"\n";
			if (pref.getBoolean(QUICK_TASK_WIFI_WIFI_ON_AC,true)) {
				script_text+="  if (!TaCmd.isBatteryCharging()) {"+"\n";
				script_text+="    TaCmd.setWifiOff();"+"\n";
				script_text+="  }"+"\n";
			} else {
				script_text+="  TaCmd.setWifiOff();"+"\n";
			}
			script_text+="}"+"\n";
			tpli= new ProfileListItem();
			tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_ACTION,"#QT-Wifi-On", 
					PROFILE_ENABLED,script_text);
			pfa.addProfItem(tpli);
			
			
			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			trig.add(BUILTIN_EVENT_WIFI_DISCONNECTED);
			act.add("#QT-Wifi-Disconnected");
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-Wifi-Disconnected", 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);

			script_text=new String("");
			script_text+="if (!TaCmd.waitUntilWifiConnected(60)) {"+"\n";
			script_text+="  TaCmd.setWifiOff();"+"\n";
			script_text+="}"+"\n";
			tpli= new ProfileListItem();
			tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_ACTION,"#QT-Wifi-Disconnected", 
					PROFILE_ENABLED,script_text);
			pfa.addProfItem(tpli);

		}

		if (pref.getBoolean(QUICK_TASK_WIFI_SCREEN_UNLOCKED,false)) {
			profile_added=true;
			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			trig.add(BUILTIN_EVENT_SCREEN_UNLOCKED);
			act.add("#QT-Wifi-Screen-Unlocked");
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-Wifi-Screen-Unlocked", 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);
			
			String script_text=new String("");
			script_text+="if (!TaCmd.isScreenLocked()) {"+"\n";
			if (pref.getBoolean(QUICK_TASK_PROFILE_IGNORE_AIRPLANE_MODE,false)) {
				script_text+="    TaCmd.cancelTaskByEventScreenLocked();"+"\n";
				if (pref.getBoolean(QUICK_TASK_WIFI_SCREEN_UNLOCKED_AC,true)) {
					script_text+="    if (TaCmd.isBatteryCharging()) {"+"\n";
					script_text+="      TaCmd.setWifiOn();"+"\n";
					script_text+="    }"+"\n";
				} else {
					script_text+="  TaCmd.setWifiOn();"+"\n";
				}
				script_text+="}"+"\n";
			} else {
				script_text+="  if (!TaCmd.isAirplaneModeOn()) {"+"\n";
				script_text+="    TaCmd.cancelTaskByEventScreenLocked();"+"\n";
				if (pref.getBoolean(QUICK_TASK_WIFI_SCREEN_UNLOCKED_AC,true)) {
					script_text+="    if (TaCmd.isBatteryCharging()) {"+"\n";
					script_text+="      TaCmd.setWifiOn();"+"\n";
					script_text+="    }"+"\n";
				} else {
					script_text+="  TaCmd.setWifiOn();"+"\n";
				}
				script_text+="  }"+"\n";
				script_text+="}"+"\n";
			}
			tpli= new ProfileListItem();
			tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_ACTION,"#QT-Wifi-Screen-Unlocked", 
					PROFILE_ENABLED,script_text);
			pfa.addProfItem(tpli);
		}
		if (profile_added) {
			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			String script_text=new String("");

			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			trig.add(BUILTIN_EVENT_WIFI_OFF);
			act.add("#QT-Wifi-Off");
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-Wifi-Off", 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);

			script_text=new String("");
			script_text+="TaCmd.cancelTaskByEventWifiOn();"+"\n";
			script_text+="TaCmd.cancelTaskByEventWifiConnected();"+"\n";
			script_text+="TaCmd.cancelTaskByEventWifiDisconnected();"+"\n";
			tpli= new ProfileListItem();
			tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_ACTION,"#QT-Wifi-Off", 
					PROFILE_ENABLED,script_text);
			pfa.addProfItem(tpli);
		}
	};

	final static private void buildQuickTaskBtBsh(Context context, AdapterProfileList pfa, 
			CommonUtilities util, String grp_name, String prof_notification) {
		SharedPreferences pref=util.getPrefMgr();
		ProfileListItem tpli;
		ArrayList<String> act;
		ArrayList<String> trig;
		ArrayList<String> pre=new ArrayList<String>();
		ArrayList<String> post=new ArrayList<String>();
		pre.add("pre1");pre.add("pre2");
		post.add("post1");post.add("post2");

		boolean profile_added=false;
		if (pref.getBoolean(QUICK_TASK_BT_SCREEN_LOCKED,false)) {
//			profile_added=true;
			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			trig.add(BUILTIN_EVENT_SCREEN_LOCKED);
			act.add("#QT-Bt-Screen-Locked");
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-Bt-Screen-Locked", 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);

			String script_text=new String("");
			script_text+="if (TaCmd.isBluetoothActive()) {"+"\n";
			script_text+="  TaCmd.cancelTaskByEventScreenUnlocked();"+"\n";
			script_text+="  TaCmd.waitSeconds(60);"+"\n";
			script_text+="  if (TaCmd.isScreenLocked()) {"+"\n";
			script_text+="    if (!TaCmd.isBluetoothConnected()) {"+"\n";
			if (pref.getBoolean(QUICK_TASK_BT_SCREEN_LOCKED_AC,true)) {
				script_text+="      if (!TaCmd.isBatteryCharging()) {"+"\n";
				script_text+="        TaCmd.setBluetoothOff();"+"\n";
				script_text+="      }"+"\n";
			} else {
				script_text+="      TaCmd.setBluetoothOff();"+"\n";
			}
			script_text+="    }"+"\n";
			script_text+="  }"+"\n";
			script_text+="}"+"\n";
			tpli= new ProfileListItem();
			tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_ACTION,"#QT-Bt-Screen-Locked", 
					PROFILE_ENABLED,script_text);
			pfa.addProfItem(tpli);

			if (pref.getBoolean(QUICK_TASK_BT_SCREEN_LOCKED_AC,true)) {
				act=new ArrayList<String>();
				trig=new ArrayList<String>();
				trig.add(BUILTIN_EVENT_POWER_SOURCE_CHANGED_BATTERY);
				act.add("#QT-Bt-PWR.To-Battery");
				tpli= new ProfileListItem();
				tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
						PROFILE_TYPE_TASK,"#QT-Bt-PWR.To-Battery", 
						PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
						prof_notification,act,trig);
				pfa.addProfItem(tpli);
				
				script_text=new String("");
				script_text+="if (!TaCmd.isBluetoothConnected()) {"+"\n";
				script_text+="  if (TaCmd.isScreenLocked()) {"+"\n";
				script_text+="    TaCmd.waitSeconds(60);"+"\n";
				script_text+="    if (!TaCmd.isBatteryCharging() && TaCmd.isScreenLocked() && !TaCmd.isBluetoothConnected()) {"+"\n";
				script_text+="      TaCmd.setBluetoothOff();"+"\n";
				script_text+="    }"+"\n";
				script_text+="  }"+"\n";
				script_text+="}"+"\n";
				tpli= new ProfileListItem();
				tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
						PROFILE_TYPE_ACTION,"#QT-Bt-PWR.To-Battery", 
						PROFILE_ENABLED,script_text);
				pfa.addProfItem(tpli);
			}
		}

		if (pref.getBoolean(QUICK_TASK_BT_BT_ON,false)) {
			profile_added=true;
			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			trig.add(BUILTIN_EVENT_BLUETOOTH_ON);
			act.add("#QT-Bt-On");
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-Bt-On", 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);

			String script_text=new String("");
			script_text+="if (!TaCmd.waitUntilBluetoothConnected(60)) {"+"\n";
			if (pref.getBoolean(QUICK_TASK_BT_BT_ON_AC,true)) {
				script_text+="  if (!TaCmd.isBatteryCharging()) {"+"\n";
				script_text+="    TaCmd.setBluetoothOff();"+"\n";
				script_text+="  }"+"\n";
			} else {
				script_text+="  TaCmd.setBluetoothOff();"+"\n";
			}
			script_text+="}"+"\n";
			tpli= new ProfileListItem();
			tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_ACTION,"#QT-Bt-On", 
					PROFILE_ENABLED,script_text);
			pfa.addProfItem(tpli);
		}
		if (pref.getBoolean(QUICK_TASK_BT_SCREEN_UNLOCKED,false)) {
//			profile_added=true;
			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			trig.add(BUILTIN_EVENT_SCREEN_UNLOCKED);
			act.add("#QT-Bt-Screen-Unlocked");
			
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-Bt-Screen-Unlocked", 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);

			String script_text=new String("");
			if (!isQuickTaskIgnoreAirplaneMode(util)) script_text+="if (!TaCmd.isScreenLocked() && !TaCmd.isAirplaneModeOn()) {"+"\n";
			else script_text+="if (!TaCmd.isScreenLocked()) {"+"\n";
			script_text+="  TaCmd.cancelTaskByEventScreenLocked();"+"\n";
			if (pref.getBoolean(QUICK_TASK_BT_SCREEN_UNLOCKED_AC,true)) {
				act.add(BUILTIN_ACTION_ABORT_IF_POWER_IS_BATTERY);
				script_text+="  if (!TaCmd.isBatteryCharging()) {"+"\n";
				script_text+="    TaCmd.setBluetoothOn();"+"\n";
				script_text+="  }"+"\n";
			} else {
				script_text+="  TaCmd.setBluetoothOn();"+"\n";
			}
			script_text+="}"+"\n";
			tpli= new ProfileListItem();
			tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_ACTION,"#QT-Bt-Screen-Unlocked", 
					PROFILE_ENABLED,script_text);
			pfa.addProfItem(tpli);
		}
		if (profile_added) {
			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			trig.add(BUILTIN_EVENT_BLUETOOTH_DISCONNECTED);
			act.add("#QT-Bt-Disconnected");
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-Bt-Disconnected", 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);

			String script_text=new String("");
			script_text+="if (!TaCmd.waitUntilBluetoothConnected(60)) {"+"\n";
			if (pref.getBoolean(QUICK_TASK_BT_BT_ON_AC,true)) {
				script_text+="  if (!TaCmd.isBatteryCharging()) {"+"\n";
				script_text+="    TaCmd.setBluetoothOff();"+"\n";
				script_text+="  }"+"\n";
			} else {
				script_text+="  TaCmd.setBluetoothOff();"+"\n";
			}
			script_text+="}"+"\n";

			tpli= new ProfileListItem();
			tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_ACTION,"#QT-Bt-Disconnected", 
					PROFILE_ENABLED,script_text);
			pfa.addProfItem(tpli);

			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			trig.add(BUILTIN_EVENT_BLUETOOTH_OFF);
			act.add("#QT-Bt-Off");
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-Bt-Off", 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);

			script_text=new String("");
			script_text+="TaCmd.cancelTaskByEventBluetoothOn();"+"\n";
			script_text+="TaCmd.cancelTaskByEventBluetoothConnected();"+"\n";
			script_text+="TaCmd.cancelTaskByEventBluetoothDisconnected();"+"\n";
			tpli= new ProfileListItem();
			tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_ACTION,"#QT-Bt-Off", 
					PROFILE_ENABLED,script_text);
			pfa.addProfItem(tpli);
		}
		
	};

	final static private void buildQuickTaskScreenBsh(Context context, 
			AdapterProfileList pfa,  
			CommonUtilities util, String grp_name, String prof_notification) {
		SharedPreferences pref=util.getPrefMgr();
		ProfileListItem tpli;
		ArrayList<String> act;
		ArrayList<String> trig;
		ArrayList<String> pre=new ArrayList<String>();
		ArrayList<String> post=new ArrayList<String>();
		pre.add("pre1");pre.add("pre2");
		post.add("post1");post.add("post2");

		act=new ArrayList<String>();
		trig=new ArrayList<String>();
//		trig.add(BUILTIN_EVENT_POWER_SOURCE_CHANGED_BATTERY);
//		act.add("#QT-Screen-PWR-to-battery");
//		tpli= new ProfileListItem();
//		tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
//				PROFILE_TYPE_TASK,"#QT-Screen-PWR-to-battery", 
//				PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
//				prof_notification,act,trig);
//		pfa.addProfItem(tpli);
//		
		String script_text=new String("");
//		script_text+="if (TaCmd.isScreenLocked()) {"+"\n";
//		script_text+="  TaCmd.setScreenLocked();"+"\n";
//		script_text+="}"+"\n";
//		tpli= new ProfileListItem();
//		tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
//				PROFILE_TYPE_ACTION,"#QT-Screen-PWR-to-battery", 
//				PROFILE_ENABLED,script_text);
//		pfa.addProfItem(tpli);
		

		boolean cancel_on_unlock=false;
		if (util.isProximitySensorAvailable()!=null &&
				pref.getBoolean(QUICK_TASK_SCREEN_PROXIMITY_UNDETECTED,true)) {
			cancel_on_unlock=true;
			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			trig.add(BUILTIN_EVENT_PROXIMITY_UNDETECTED);
			act.add("#QT-Screen-Proximity-undetected");
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-Screen-Proximity-undetected", 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);
			
			script_text=new String("");
			script_text+="TaCmd.cancelTaskByEventProximityDetected();"+"\n";
			script_text+="TaCmd.cancelTaskByEventLightDetected();"+"\n";
			script_text+="if (TaCmd.isScreenLocked()) {"+"\n";
			script_text+="  TaCmd.setScreenOnSync();"+"\n";
//			script_text+="  if (TaCmd.isScreenLocked()) TaCmd.setScreenLocked();"+"\n";
			script_text+="}"+"\n";
			tpli= new ProfileListItem();
			tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_ACTION,"#QT-Screen-Proximity-undetected", 
					PROFILE_ENABLED,script_text);
			pfa.addProfItem(tpli);
		}
		if (util.isProximitySensorAvailable()!=null &&
				(pref.getBoolean(QUICK_TASK_SCREEN_PROXIMITY_DETECTED,true)||
					pref.getBoolean(QUICK_TASK_SCREEN_PROXIMITY_UNDETECTED,true))) {
			if (pref.getBoolean(QUICK_TASK_SCREEN_PROXIMITY_DETECTED,true)) {
				cancel_on_unlock=true;
				act=new ArrayList<String>();
				trig=new ArrayList<String>();
				trig.add(BUILTIN_EVENT_PROXIMITY_DETECTED);
				act.add("#QT-Screen-Proximity-detected");
				tpli= new ProfileListItem();
				tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
						PROFILE_TYPE_TASK,"#QT-Screen-Proximity-detected", 
						PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
						prof_notification,act,trig);
				pfa.addProfItem(tpli);

				script_text=new String("");
				script_text+="TaCmd.cancelTaskByEventProximityUndetected();"+"\n";
				script_text+="TaCmd.cancelTaskByEventLightUndetected();"+"\n";
				
				if (pref.getBoolean(QUICK_TASK_SCREEN_PROXIMITY_DETECTED_IGNORE_LANDSCAPE,false)) {
					script_text+="if (TaCmd.isOrientationLandscape()) TaCmd.abort();"+"\n";
				}
				
				script_text+="if (!TaCmd.isScreenLocked()) {"+"\n";
				script_text+="  if (!TaCmd.isTelephonyCallStateOffhook() && !TaCmd.isTelephonyCallStateRinging()) {"+"\n";
				script_text+="    TaCmd.waitSeconds(60);"+"\n";
				script_text+="    if (!TaCmd.isTelephonyCallStateOffhook() && !TaCmd.isTelephonyCallStateRinging() && TaCmd.isProximitySensorDetected()) {"+"\n";

				if (pref.getBoolean(QUICK_TASK_SCREEN_PROXIMITY_DETECTED_IGNORE_LANDSCAPE,false)) {
					script_text+="      if (TaCmd.isOrientationLandscape()) TaCmd.abort();"+"\n";
				}
				
				script_text+="      TaCmd.setScreenLocked();"+"\n";
				script_text+="    }"+"\n";
				script_text+="  }"+"\n";
				script_text+="}"+"\n";
//				script_text+="} else if (TaCmd.isScreenOn()) TaCmd.setScreenLocked();"+"\n";
				tpli= new ProfileListItem();
				tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
						PROFILE_TYPE_ACTION,"#QT-Screen-Proximity-detected", 
						PROFILE_ENABLED,script_text);
				pfa.addProfItem(tpli);
			}
		}

		if (util.isLightSensorAvailable()!=null &&
				pref.getBoolean(QUICK_TASK_SCREEN_LIGHT_DETECTED,false)) {
			cancel_on_unlock=true;
			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			trig.add(BUILTIN_EVENT_LIGHT_DETECTED);
			act.add("#QT-Screen-Light-detected");
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-Screen-Light-detected", 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);

			script_text=new String("");
			script_text+="TaCmd.cancelTaskByEventLightUndetected();"+"\n";
			script_text+="if (TaCmd.isScreenLocked()) {"+"\n";
			script_text+="  TaCmd.setScreenOnSync();"+"\n";
//			script_text+="  if (TaCmd.isScreenLocked()) TaCmd.setScreenLocked();"+"\n";
			script_text+="}"+"\n";
			tpli= new ProfileListItem();
			tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_ACTION,"#QT-Screen-Light-detected", 
					PROFILE_ENABLED,script_text);
			pfa.addProfItem(tpli);
		}
		if (util.isLightSensorAvailable()!=null &&
			(pref.getBoolean(QUICK_TASK_SCREEN_LIGHT_DETECTED,false)||pref.getBoolean(QUICK_TASK_SCREEN_LIGHT_UNDETECTED,false))) {
			if (pref.getBoolean(QUICK_TASK_SCREEN_LIGHT_UNDETECTED,false)) {
				cancel_on_unlock=true;
				act=new ArrayList<String>();
				trig=new ArrayList<String>();
				trig.add(BUILTIN_EVENT_LIGHT_UNDETECTED);
				act.add("#QT-Screen-Light-undetected");
				tpli= new ProfileListItem();
				tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
						PROFILE_TYPE_TASK,"#QT-Screen-Light-undetected", 
						PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
						prof_notification,act,trig);
				pfa.addProfItem(tpli);

				script_text=new String("");
				script_text+="TaCmd.cancelTaskByEventLightDetected();"+"\n";
				script_text+="if (!TaCmd.isScreenLocked()) {"+"\n";
				script_text+="  TaCmd.waitSeconds(60);"+"\n";
				script_text+="  if (!TaCmd.isScreenLocked()) {"+"\n";
				script_text+="    TaCmd.setScreenLocked();"+"\n";
//				script_text+="  } else {"+"\n";
//				script_text+="    if (TaCmd.isScreenOn()) TaCmd.setScreenLocked();"+"\n";
				script_text+="  }"+"\n";
				script_text+="}"+"\n";
				tpli= new ProfileListItem();
				tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
						PROFILE_TYPE_ACTION,"#QT-Screen-Light-undetected", 
						PROFILE_ENABLED,script_text);
				pfa.addProfItem(tpli);
			}
		}

		if (cancel_on_unlock) {
			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			trig.add(BUILTIN_EVENT_SCREEN_UNLOCKED);
			act.add("#QT-Screen-unlocked");
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-Screen-unlocked", 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);
			
			script_text=new String("");
			script_text+="TaCmd.cancelTaskByEventScreenLocked();"+"\n";
			script_text+="TaCmd.cancelTaskByEventProximityDetected();"+"\n";
			script_text+="TaCmd.cancelTaskByEventProximityUndetected();"+"\n";
			script_text+="TaCmd.cancelTaskByEventLightDetected();"+"\n";
			script_text+="TaCmd.cancelTaskByEventLightUndetected();"+"\n";
			tpli= new ProfileListItem();
			tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_ACTION,"#QT-Screen-unlocked", 
					PROFILE_ENABLED,script_text);
			pfa.addProfItem(tpli);

			
			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			trig.add(BUILTIN_EVENT_SCREEN_LOCKED);
			act.add("#QT-Screen-locked");
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-Screen-locked", 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);

			script_text=new String("");
			script_text+="TaCmd.cancelTaskByEventScreenUnlocked();"+"\n";
			script_text+="TaCmd.cancelTaskByEventProximityDetected();"+"\n";
			script_text+="TaCmd.cancelTaskByEventProximityUndetected();"+"\n";
			script_text+="TaCmd.cancelTaskByEventLightDetected();"+"\n";
			script_text+="TaCmd.cancelTaskByEventLightUndetected();"+"\n";
			tpli= new ProfileListItem();
			tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_ACTION,"#QT-Screen-locked", 
					PROFILE_ENABLED,script_text);
			pfa.addProfItem(tpli);
		}
	};
	
	final static private void buildQuickTaskAlarmClockBsh(Context context, AdapterProfileList pfa, 
			CommonUtilities util, String grp_name, String prof_notification) {
		SharedPreferences pref=util.getPrefMgr();

		buildAlarmClokProfileBsh(context,pfa,pref,
				grp_name, "01",
				pref.getBoolean(QUICK_TASK_ALARM_CLOCK01_ENABLED,true),
				pref.getString(QUICK_TASK_ALARM_CLOCK01_DATE_TYPE,""),
				pref.getString(QUICK_TASK_ALARM_CLOCK01_DAY_OF_WEEK,""),
				pref.getString(QUICK_TASK_ALARM_CLOCK01_DATE_DAY,""),
				pref.getString(QUICK_TASK_ALARM_CLOCK01_DATE_TIME,""),
				pref.getString(QUICK_TASK_ALARM_CLOCK01_SOUND_TYPE,""),
				pref.getString(QUICK_TASK_ALARM_CLOCK01_SOUND_NAME,""),
				QUICK_TASK_ALARM_CLOCK01_RINGTONE_PATH,prof_notification);

		buildAlarmClokProfileBsh(context,pfa,pref,
				grp_name, "02",
				pref.getBoolean(QUICK_TASK_ALARM_CLOCK02_ENABLED,true),
				pref.getString(QUICK_TASK_ALARM_CLOCK02_DATE_TYPE,""),
				pref.getString(QUICK_TASK_ALARM_CLOCK02_DAY_OF_WEEK,""),
				pref.getString(QUICK_TASK_ALARM_CLOCK02_DATE_DAY,""),
				pref.getString(QUICK_TASK_ALARM_CLOCK02_DATE_TIME,""),
				pref.getString(QUICK_TASK_ALARM_CLOCK02_SOUND_TYPE,""),
				pref.getString(QUICK_TASK_ALARM_CLOCK02_SOUND_NAME,""),
				QUICK_TASK_ALARM_CLOCK02_RINGTONE_PATH,prof_notification);
		
		buildAlarmClokProfileBsh(context,pfa,pref,
				grp_name, "03",
				pref.getBoolean(QUICK_TASK_ALARM_CLOCK03_ENABLED,true),
				pref.getString(QUICK_TASK_ALARM_CLOCK03_DATE_TYPE,""),
				pref.getString(QUICK_TASK_ALARM_CLOCK03_DAY_OF_WEEK,""),
				pref.getString(QUICK_TASK_ALARM_CLOCK03_DATE_DAY,""),
				pref.getString(QUICK_TASK_ALARM_CLOCK03_DATE_TIME,""),
				pref.getString(QUICK_TASK_ALARM_CLOCK03_SOUND_TYPE,""),
				pref.getString(QUICK_TASK_ALARM_CLOCK03_SOUND_NAME,""),
				QUICK_TASK_ALARM_CLOCK03_RINGTONE_PATH,prof_notification);
	};

	final static private void buildAlarmClokProfileBsh(Context context, AdapterProfileList pfa, 
			SharedPreferences pref, String grp_name, String id, 
			boolean alarm_enabled, String date_type,
			String day_of_week, String day, String time, String sound_type, 
			String sound_name, String ringtone_id, String prof_notification) {
		
		ProfileListItem tpli;
		ArrayList<String> act;
		ArrayList<String> trig;
		ArrayList<String> pre=new ArrayList<String>();
		ArrayList<String> post=new ArrayList<String>();
		pre.add("pre1");pre.add("pre2");
		post.add("post1");post.add("post2");
		if (alarm_enabled && !sound_type.equals("")) {
			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			act.add("#QT-AM"+id);
			trig.add("#QT-AT"+id);
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-AC"+id, 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);
			if (sound_type.equals(PROFILE_ACTION_TYPE_MUSIC) ) {
				String script_text=new String("");
				script_text+="TaCmd.playBackMusic(\""+sound_name+"\");"+"\n";
				tpli= new ProfileListItem();
				tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
						PROFILE_TYPE_ACTION,"#QT-AM"+id, 
						PROFILE_ENABLED,script_text);
				pfa.addProfItem(tpli);
			} else {
				String script_text=new String("");
				script_text+="TaCmd.playBackRingtone(\""+sound_type+"\",\""+sound_name+"\");"+"\n";
				tpli= new ProfileListItem();
				tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
						PROFILE_TYPE_ACTION,"#QT-AM"+id, 
						PROFILE_ENABLED,script_text);
				pfa.addProfItem(tpli);
			}
			
			String e_day="****/**/**",e_dw="0000000";
			if (date_type.equals(PROFILE_DATE_TIME_TYPE_DAY_OF_THE_WEEK)) {
				e_dw=day_of_week;
			} else if (date_type.equals(PROFILE_DATE_TIME_TYPE_EVERY_DAY)) {
				
			} else if (date_type.equals(PROFILE_DATE_TIME_TYPE_ONE_SHOT)) {
				e_day=day;
			}
			tpli= new ProfileListItem();
			tpli.setTimeEventEntry(PROFILE_VERSION_CURRENT,grp_name,false, System.currentTimeMillis(),
					PROFILE_TYPE_TIME,"#QT-AT"+id, 
					PROFILE_ENABLED,
					date_type,
					e_dw,
					e_day,
					time);
			pfa.addProfItem(tpli);
		}
	};
	 
	final static private void buildQuickTaskTimeActivityBsh(Context context, AdapterProfileList pfa, 
			CommonUtilities util, String grp_name, String prof_notification) {
		SharedPreferences pref=util.getPrefMgr();
		buildTimeActivityProfileBsh(context, pfa,pref, 
				grp_name, "01",
				pref.getBoolean(QUICK_TASK_TIME_ACTIVITY01_ENABLED,true),
				pref.getString(QUICK_TASK_TIME_ACTIVITY01_DATE_TYPE,""),
				pref.getString(QUICK_TASK_TIME_ACTIVITY01_DAY_OF_WEEK,""),
				pref.getString(QUICK_TASK_TIME_ACTIVITY01_DATE_DAY,""),
				pref.getString(QUICK_TASK_TIME_ACTIVITY01_DATE_TIME,""),
				pref.getString(QUICK_TASK_TIME_ACTIVITY01_ACTIVITY,""),
				prof_notification);

		buildTimeActivityProfileBsh(context, pfa,pref, 
				grp_name, "02",
				pref.getBoolean(QUICK_TASK_TIME_ACTIVITY02_ENABLED,true),
				pref.getString(QUICK_TASK_TIME_ACTIVITY02_DATE_TYPE,""),
				pref.getString(QUICK_TASK_TIME_ACTIVITY02_DAY_OF_WEEK,""),
				pref.getString(QUICK_TASK_TIME_ACTIVITY02_DATE_DAY,""),
				pref.getString(QUICK_TASK_TIME_ACTIVITY02_DATE_TIME,""),
				pref.getString(QUICK_TASK_TIME_ACTIVITY02_ACTIVITY,""),
				prof_notification);

		buildTimeActivityProfileBsh(context, pfa,pref, 
				grp_name, "03",
				pref.getBoolean(QUICK_TASK_TIME_ACTIVITY03_ENABLED,true),
				pref.getString(QUICK_TASK_TIME_ACTIVITY03_DATE_TYPE,""),
				pref.getString(QUICK_TASK_TIME_ACTIVITY03_DAY_OF_WEEK,""),
				pref.getString(QUICK_TASK_TIME_ACTIVITY03_DATE_DAY,""),
				pref.getString(QUICK_TASK_TIME_ACTIVITY03_DATE_TIME,""),
				pref.getString(QUICK_TASK_TIME_ACTIVITY03_ACTIVITY,""),
				prof_notification);

	};

	final static private void buildTimeActivityProfileBsh(Context context, AdapterProfileList pfa, 
			SharedPreferences pref, String grp_name, String id, 
			boolean alarm_enabled, String date_type,
			String day_of_week, String day, String time, String time_activity, String prof_notification) {
		
		ProfileListItem tpli;
		ArrayList<String> act;
		ArrayList<String> trig;
		ArrayList<String> pre=new ArrayList<String>();
		ArrayList<String> post=new ArrayList<String>();
		pre.add("pre1");pre.add("pre2");
		post.add("post1");post.add("post2");
		if (alarm_enabled && !time_activity.equals("")) {
			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			act.add("#QT-TA"+id);
			trig.add("#QT-TT"+id);
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-TC"+id, 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);

			String act_name=time_activity.substring(0,time_activity.indexOf("("));
			String pkg_name=time_activity.replace(act_name+"(", "").replace(")", "");
			String script_text=new String("");
			script_text+="TaCmd.startActivity(\""+pkg_name+"\");"+"\n";
			tpli= new ProfileListItem();
			tpli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_ACTION,"#QT-TA"+id, 
					PROFILE_ENABLED,script_text);
			pfa.addProfItem(tpli);

			
			String e_day="****/**/**",e_dw="0000000";
			if (date_type.equals(PROFILE_DATE_TIME_TYPE_DAY_OF_THE_WEEK)) {
				e_dw=day_of_week;
			} else if (date_type.equals(PROFILE_DATE_TIME_TYPE_EVERY_DAY)) {
				
			} else if (date_type.equals(PROFILE_DATE_TIME_TYPE_ONE_SHOT)) {
				e_day=day;
			}
			tpli= new ProfileListItem();
			tpli.setTimeEventEntry(PROFILE_VERSION_CURRENT,grp_name,false, System.currentTimeMillis(),
					PROFILE_TYPE_TIME,"#QT-TT"+id, 
					PROFILE_ENABLED,
					date_type,
					e_dw,
					e_day,
					time);
			pfa.addProfItem(tpli);

		}

	};

	final private String getScheduleDateType(TextView tv) {
		String result="";
		if (tv.getText().toString().equals(mContext.getString(R.string.msgs_repeat_type_one_shot))) result=PROFILE_DATE_TIME_TYPE_ONE_SHOT;
		else if (tv.getText().toString().equals(mContext.getString(R.string.msgs_repeat_type_every_day))) result=PROFILE_DATE_TIME_TYPE_EVERY_DAY;
		else if (tv.getText().toString().equals(mContext.getString(R.string.msgs_repeat_type_day_of_the_week))) result=PROFILE_DATE_TIME_TYPE_DAY_OF_THE_WEEK;
		return result;
	};
	
	final private String getScheduleDayOfWeek(TextView tv) {
		String result="";
		if (tv.getText().toString().equals(mContext.getString(R.string.msgs_quick_vlue_not_specified))) result="0000000";
		else {
			for (int i=0;i<7;i++) {
				if (dayOfTheWeekTable[i].equals(tv.getText().toString())){
					result+="1";
				} else {
					result+="0";
				}
			}
		}
		return result;
	};
	
	final private String getScheduleDay(TextView tv) {
		String result="";
		if (tv.getText().toString().equals(mContext.getString(R.string.msgs_quick_vlue_not_specified))) result="";
		else result=tv.getText().toString();
		return result;
	};
	final private String getScheduleTime(TextView tv) {
		String result="";
		if (tv.getText().toString().equals(mContext.getString(R.string.msgs_quick_vlue_not_specified))) result="";
		else result=tv.getText().toString();
		return result;
	};
	final private String getScheduleSoundType(TextView tv) {
		String result="";
		if (tv.getText().toString().equals(mContext.getString(R.string.msgs_quick_vlue_not_specified))) result="";
		else result=tv.getText().toString();
		return result;
	};
	final private String getScheduleSound(TextView tv) {
		String result="";
		if (tv.getText().toString().equals(mContext.getString(R.string.msgs_quick_vlue_not_specified))) result="";
		else result=tv.getText().toString();
		return result;
	};
	final private String getScheduleActivity(TextView tv) {
		String result="";
		if (tv.getText().toString().equals(mContext.getString(R.string.msgs_quick_vlue_not_specified))) result="";
		else result=tv.getText().toString();
		return result;
	};
	
	final static private void buildPowerSourceChangeCancelTaFunc(Context context, AdapterProfileList pfa, 
			CommonUtilities util, String grp_name, String prof_notification) {
		SharedPreferences pref=util.getPrefMgr();
		if (pref.getBoolean(QUICK_TASK_WIFI_SCREEN_LOCKED_AC,true) ||
				pref.getBoolean(QUICK_TASK_BT_SCREEN_LOCKED_AC,true)) {
			ProfileListItem tpli;
			ArrayList<String> act;
			ArrayList<String> trig;
			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			trig.add(BUILTIN_EVENT_POWER_SOURCE_CHANGED_AC);
			act.add(BUILTIN_ACTION_CANCEL_EVENT_POWER_SOURCE_CHANGED_BATTERY);
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-PWR.To-AC", 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);
		}
	}
	
	final static private void buildQuickTaskWifiTaFunc(Context context, AdapterProfileList pfa, 
			CommonUtilities util, String grp_name, String prof_notification) {
		SharedPreferences pref=util.getPrefMgr();
		ProfileListItem tpli;
		ArrayList<String> act;
		ArrayList<String> trig;

		boolean profile_added=false;

		if (pref.getBoolean(QUICK_TASK_WIFI_SCREEN_LOCKED,false)) {
			profile_added=true;
			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			trig.add(BUILTIN_EVENT_SCREEN_LOCKED);
			act.add(BUILTIN_ACTION_CANCEL_EVENT_SCREEN_UNLOCKED);
			act.add(BUILTIN_ACTION_ABORT_IF_WIFI_OFF);
			act.add(BUILTIN_ACTION_WAIT_1_MIN);
			act.add(BUILTIN_ACTION_ABORT_IF_SCREEN_UNLOCKED);
			if (pref.getBoolean(QUICK_TASK_WIFI_SCREEN_LOCKED_AC,true)) {
				act.add(BUILTIN_ACTION_ABORT_IF_POWER_IS_AC_OR_CHRAGE);
			}
			act.add(BUILTIN_ACTION_WIFI_OFF);
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-Wifi-Screen-Locked", 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);
			if (pref.getBoolean(QUICK_TASK_WIFI_SCREEN_LOCKED_AC,true)) {
				act=new ArrayList<String>();
				trig=new ArrayList<String>();
				trig.add(BUILTIN_EVENT_POWER_SOURCE_CHANGED_BATTERY);
				act.add(BUILTIN_ACTION_ABORT_IF_SCREEN_UNLOCKED);
				act.add(BUILTIN_ACTION_WAIT_1_MIN);
				act.add(BUILTIN_ACTION_ABORT_IF_POWER_IS_AC_OR_CHRAGE);
				act.add(BUILTIN_ACTION_ABORT_IF_SCREEN_UNLOCKED);
				act.add(BUILTIN_ACTION_WIFI_OFF);
				tpli= new ProfileListItem();
				tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
						PROFILE_TYPE_TASK,"#QT-Wifi-PWR.To-Battery", 
						PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
						prof_notification,act,trig);
				pfa.addProfItem(tpli);
			}
		}
		
		if (pref.getBoolean(QUICK_TASK_WIFI_WIFI_ON,false)) {
			profile_added=true;
			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			trig.add(BUILTIN_EVENT_WIFI_ON);
			act.add("#QT-Wifi-Wait-until-connected");
			if (pref.getBoolean(QUICK_TASK_WIFI_WIFI_ON_AC,true)) {
				act.add(BUILTIN_ACTION_ABORT_IF_POWER_IS_AC_OR_CHRAGE);
				act.add(BUILTIN_ACTION_ABORT_IF_WIFI_CONNECTED);
				act.add(BUILTIN_ACTION_WIFI_OFF);
			} else {
				act.add(BUILTIN_ACTION_ABORT_IF_WIFI_CONNECTED);
				act.add(BUILTIN_ACTION_WIFI_OFF);
			}
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-Wifi-On", 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);
			
			tpli= new ProfileListItem();
			tpli.setActionWaitEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_ACTION,"#QT-Wifi-Wait-until-connected", 
					PROFILE_ENABLED,PROFILE_ACTION_TYPE_WAIT_TARGET_WIFI_CONNECTED,"1",PROFILE_ACTION_TYPE_WAIT_TIMEOUT_UNITS_MIN);
			pfa.addProfItem(tpli);

			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			trig.add(BUILTIN_EVENT_WIFI_DISCONNECTED);
			act.add("#QT-Wifi-Wait-until-connected");
			act.add(BUILTIN_ACTION_ABORT_IF_WIFI_CONNECTED);
			act.add(BUILTIN_ACTION_WIFI_OFF);
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-Wifi-Disconnected", 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);

		}

		if (pref.getBoolean(QUICK_TASK_WIFI_SCREEN_UNLOCKED,false)) {
			profile_added=true;
			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			trig.add(BUILTIN_EVENT_SCREEN_UNLOCKED);
			act.add(BUILTIN_ACTION_ABORT_IF_SCREEN_LOCKED);
			
			if (pref.getBoolean(QUICK_TASK_PROFILE_IGNORE_AIRPLANE_MODE,false)) {
				act.add(BUILTIN_ACTION_CANCEL_EVENT_SCREEN_LOCKED);
				if (pref.getBoolean(QUICK_TASK_WIFI_SCREEN_UNLOCKED_AC,true)) {
					act.add(BUILTIN_ACTION_ABORT_IF_POWER_IS_BATTERY);
				}
				act.add(BUILTIN_ACTION_WIFI_ON);
			} else {
				act.add(BUILTIN_ACTION_ABORT_IF_AIRPLANE_MODE_ON);
				act.add(BUILTIN_ACTION_CANCEL_EVENT_SCREEN_LOCKED);
				if (pref.getBoolean(QUICK_TASK_WIFI_SCREEN_UNLOCKED_AC,true)) {
					act.add(BUILTIN_ACTION_ABORT_IF_POWER_IS_BATTERY);
				}
				act.add(BUILTIN_ACTION_WIFI_ON);
			}
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-Wifi-Screen-Unlocked", 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);
		}
		if (profile_added) {
			act=new ArrayList<String>();
			trig=new ArrayList<String>();

			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			trig.add(BUILTIN_EVENT_WIFI_OFF);
			act.add(BUILTIN_ACTION_CANCEL_EVENT_WIFI_ON);
			act.add(BUILTIN_ACTION_CANCEL_EVENT_WIFI_DISCONNECTED);
			act.add(BUILTIN_ACTION_CANCEL_EVENT_WIFI_CONNECTED);
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-Wifi-Off", 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);
		}
	};

	final static private void buildQuickTaskBtTaFunc(Context context, AdapterProfileList pfa, 
			CommonUtilities util, String grp_name, String prof_notification) {
		SharedPreferences pref=util.getPrefMgr();
		ProfileListItem tpli;
		ArrayList<String> act;
		ArrayList<String> trig;
		ArrayList<String> pre=new ArrayList<String>();
		ArrayList<String> post=new ArrayList<String>();
		pre.add("pre1");pre.add("pre2");
		post.add("post1");post.add("post2");

		boolean profile_added=false;
		if (pref.getBoolean(QUICK_TASK_BT_SCREEN_LOCKED,false)) {
//			profile_added=true;
			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			trig.add(BUILTIN_EVENT_SCREEN_LOCKED);
			act.add(BUILTIN_ACTION_ABORT_IF_BLUETOOTH_OFF);
			act.add(BUILTIN_ACTION_CANCEL_EVENT_SCREEN_UNLOCKED);
			act.add(BUILTIN_ACTION_WAIT_1_MIN);
			act.add(BUILTIN_ACTION_ABORT_IF_SCREEN_UNLOCKED);
			act.add(BUILTIN_ACTION_ABORT_IF_BLUETOOTH_CONNECTED);
			if (pref.getBoolean(QUICK_TASK_BT_SCREEN_LOCKED_AC,true)) {
				act.add(BUILTIN_ACTION_ABORT_IF_POWER_IS_AC_OR_CHRAGE);
			}
			act.add(BUILTIN_ACTION_BLUETOOTH_OFF);
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-Bt-Screen-Locked", 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);
			
			if (pref.getBoolean(QUICK_TASK_BT_SCREEN_LOCKED_AC,true)) {
				act=new ArrayList<String>();
				trig=new ArrayList<String>();
				trig.add(BUILTIN_EVENT_POWER_SOURCE_CHANGED_BATTERY);
				act.add(BUILTIN_ACTION_ABORT_IF_BLUETOOTH_CONNECTED);
				act.add(BUILTIN_ACTION_ABORT_IF_SCREEN_UNLOCKED);
				act.add(BUILTIN_ACTION_WAIT_1_MIN);
				act.add(BUILTIN_ACTION_ABORT_IF_POWER_IS_AC_OR_CHRAGE);
				act.add(BUILTIN_ACTION_ABORT_IF_BLUETOOTH_CONNECTED);
				act.add(BUILTIN_ACTION_ABORT_IF_SCREEN_UNLOCKED);
				act.add(BUILTIN_ACTION_BLUETOOTH_OFF);
				tpli= new ProfileListItem();
				tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
						PROFILE_TYPE_TASK,"#QT-Bt-PWR.To-Battery", 
						PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
						prof_notification,act,trig);
				pfa.addProfItem(tpli);
			}
		}

		if (pref.getBoolean(QUICK_TASK_BT_BT_ON,false)) {
			profile_added=true;
			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			trig.add(BUILTIN_EVENT_BLUETOOTH_ON);
			act.add("#QT-Bt-Wait-until-connected");
			act.add(BUILTIN_ACTION_ABORT_IF_BLUETOOTH_CONNECTED);
			if (pref.getBoolean(QUICK_TASK_BT_BT_ON_AC,true)) {
				act.add(BUILTIN_ACTION_ABORT_IF_POWER_IS_AC_OR_CHRAGE);
			}
			act.add(BUILTIN_ACTION_BLUETOOTH_OFF);
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-Bt-On", 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);
		}
		if (pref.getBoolean(QUICK_TASK_BT_SCREEN_UNLOCKED,false)) {
//			profile_added=true;
			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			trig.add(BUILTIN_EVENT_SCREEN_UNLOCKED);
			act.add(BUILTIN_ACTION_ABORT_IF_SCREEN_LOCKED);
			if (!isQuickTaskIgnoreAirplaneMode(util)) act.add(BUILTIN_ACTION_ABORT_IF_AIRPLANE_MODE_ON);
			act.add(BUILTIN_ACTION_CANCEL_EVENT_SCREEN_LOCKED);
			if (pref.getBoolean(QUICK_TASK_BT_SCREEN_UNLOCKED_AC,true)) {
				act.add(BUILTIN_ACTION_ABORT_IF_POWER_IS_BATTERY);
			}
			act.add(BUILTIN_ACTION_BLUETOOTH_ON);
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-Bt-Screen-Unlocked", 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);
		}
		if (profile_added) {
			tpli= new ProfileListItem();
			tpli.setActionWaitEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_ACTION,"#QT-Bt-Wait-until-connected", 
					PROFILE_ENABLED,PROFILE_ACTION_TYPE_WAIT_TARGET_BLUETOOTH_CONNECTED,"1",PROFILE_ACTION_TYPE_WAIT_TIMEOUT_UNITS_MIN);
			pfa.addProfItem(tpli);

			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			trig.add(BUILTIN_EVENT_BLUETOOTH_DISCONNECTED);
			act.add("#QT-Bt-Wait-until-connected");
			act.add(BUILTIN_ACTION_ABORT_IF_BLUETOOTH_CONNECTED);
			act.add(BUILTIN_ACTION_BLUETOOTH_OFF);
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-Bt-Disconnected", 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);
			
			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			trig.add(BUILTIN_EVENT_BLUETOOTH_OFF);
			act.add(BUILTIN_ACTION_CANCEL_EVENT_BLUETOOTH_ON);
			act.add(BUILTIN_ACTION_CANCEL_EVENT_BLUETOOTH_DISCONNECTED);
			act.add(BUILTIN_ACTION_CANCEL_EVENT_BLUETOOTH_CONNECTED);
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-Bt-Off", 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);
		}
		
	};

	final static private void buildQuickTaskScreenTaFunc(Context context, 
			AdapterProfileList pfa,  
			CommonUtilities util, String grp_name, String prof_notification) {
		SharedPreferences pref=util.getPrefMgr();
		ProfileListItem tpli;
		ArrayList<String> act;
		ArrayList<String> trig;
		ArrayList<String> pre=new ArrayList<String>();
		ArrayList<String> post=new ArrayList<String>();
		pre.add("pre1");pre.add("pre2");
		post.add("post1");post.add("post2");

		act=new ArrayList<String>();
		trig=new ArrayList<String>();
//		trig.add(BUILTIN_EVENT_POWER_SOURCE_CHANGED_BATTERY);
//		act.add(BUILTIN_ACTION_ABORT_IF_SCREEN_UNLOCKED);
//		act.add(BUILTIN_ACTION_SCREEN_LOCKED);
//		tpli= new ProfileListItem();
//		tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
//				PROFILE_TYPE_TASK,"#QT-Screen-PWR-to-battery", 
//				PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
//				prof_notification,act,trig);
//		pfa.addProfItem(tpli);

		boolean cancel_on_unlock=false;
		if (util.isProximitySensorAvailable()!=null &&
				pref.getBoolean(QUICK_TASK_SCREEN_PROXIMITY_UNDETECTED,true)) {
			cancel_on_unlock=true;
			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			trig.add(BUILTIN_EVENT_PROXIMITY_UNDETECTED);
//			act.add(BUILTIN_ACTION_SCREEN_ON_ASYNC);
			act.add(BUILTIN_ACTION_CANCEL_EVENT_PROXIMITY_DETECTED);
			act.add(BUILTIN_ACTION_CANCEL_EVENT_LIGHT_DETECTED);
//			act.add(BUILTIN_ACTION_ABORT_IF_SCREEN_ON);//for Refresh
			act.add(BUILTIN_ACTION_ABORT_IF_SCREEN_UNLOCKED);
			act.add(BUILTIN_ACTION_SCREEN_ON);
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-Screen-Proximity-undetected", 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);
		}

		if (util.isLightSensorAvailable()!=null &&
				pref.getBoolean(QUICK_TASK_SCREEN_LIGHT_DETECTED,false)) {
			cancel_on_unlock=true;
			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			trig.add(BUILTIN_EVENT_LIGHT_DETECTED);
			act.add(BUILTIN_ACTION_CANCEL_EVENT_LIGHT_UNDETECTED);
			act.add(BUILTIN_ACTION_ABORT_IF_SCREEN_ON);
			act.add(BUILTIN_ACTION_ABORT_IF_SCREEN_UNLOCKED);
			act.add(BUILTIN_ACTION_SCREEN_ON);
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-Screen-Light-detected", 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);
		}

		if (cancel_on_unlock) {
			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			trig.add(BUILTIN_EVENT_SCREEN_UNLOCKED);
			act.add(BUILTIN_ACTION_CANCEL_EVENT_SCREEN_LOCKED);
			act.add(BUILTIN_ACTION_CANCEL_EVENT_PROXIMITY_DETECTED);
			act.add(BUILTIN_ACTION_CANCEL_EVENT_PROXIMITY_UNDETECTED);
			act.add(BUILTIN_ACTION_CANCEL_EVENT_LIGHT_DETECTED);
			act.add(BUILTIN_ACTION_CANCEL_EVENT_LIGHT_UNDETECTED);
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-Screen-unlocked", 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);
			
			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			trig.add(BUILTIN_EVENT_SCREEN_LOCKED);
			act.add(BUILTIN_ACTION_CANCEL_EVENT_SCREEN_UNLOCKED);
			act.add(BUILTIN_ACTION_CANCEL_EVENT_PROXIMITY_DETECTED);
			act.add(BUILTIN_ACTION_CANCEL_EVENT_PROXIMITY_UNDETECTED);
			act.add(BUILTIN_ACTION_CANCEL_EVENT_LIGHT_DETECTED);
			act.add(BUILTIN_ACTION_CANCEL_EVENT_LIGHT_UNDETECTED);
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-Screen-locked", 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);

		}
	};
	
	final static private void buildQuickTaskAlarmClockTaFunc(Context context, AdapterProfileList pfa, 
			CommonUtilities util, String grp_name, String prof_notification) {
		SharedPreferences pref=util.getPrefMgr();

		buildAlarmClokProfileTaFunc(context,pfa,pref,
				grp_name, "01",
				pref.getBoolean(QUICK_TASK_ALARM_CLOCK01_ENABLED,true),
				pref.getString(QUICK_TASK_ALARM_CLOCK01_DATE_TYPE,""),
				pref.getString(QUICK_TASK_ALARM_CLOCK01_DAY_OF_WEEK,""),
				pref.getString(QUICK_TASK_ALARM_CLOCK01_DATE_DAY,""),
				pref.getString(QUICK_TASK_ALARM_CLOCK01_DATE_TIME,""),
				pref.getString(QUICK_TASK_ALARM_CLOCK01_SOUND_TYPE,""),
				pref.getString(QUICK_TASK_ALARM_CLOCK01_SOUND_NAME,""),
				QUICK_TASK_ALARM_CLOCK01_RINGTONE_PATH,prof_notification);

		buildAlarmClokProfileTaFunc(context,pfa,pref,
				grp_name, "02",
				pref.getBoolean(QUICK_TASK_ALARM_CLOCK02_ENABLED,true),
				pref.getString(QUICK_TASK_ALARM_CLOCK02_DATE_TYPE,""),
				pref.getString(QUICK_TASK_ALARM_CLOCK02_DAY_OF_WEEK,""),
				pref.getString(QUICK_TASK_ALARM_CLOCK02_DATE_DAY,""),
				pref.getString(QUICK_TASK_ALARM_CLOCK02_DATE_TIME,""),
				pref.getString(QUICK_TASK_ALARM_CLOCK02_SOUND_TYPE,""),
				pref.getString(QUICK_TASK_ALARM_CLOCK02_SOUND_NAME,""),
				QUICK_TASK_ALARM_CLOCK02_RINGTONE_PATH,prof_notification);
		
		buildAlarmClokProfileTaFunc(context,pfa,pref,
				grp_name, "03",
				pref.getBoolean(QUICK_TASK_ALARM_CLOCK03_ENABLED,true),
				pref.getString(QUICK_TASK_ALARM_CLOCK03_DATE_TYPE,""),
				pref.getString(QUICK_TASK_ALARM_CLOCK03_DAY_OF_WEEK,""),
				pref.getString(QUICK_TASK_ALARM_CLOCK03_DATE_DAY,""),
				pref.getString(QUICK_TASK_ALARM_CLOCK03_DATE_TIME,""),
				pref.getString(QUICK_TASK_ALARM_CLOCK03_SOUND_TYPE,""),
				pref.getString(QUICK_TASK_ALARM_CLOCK03_SOUND_NAME,""),
				QUICK_TASK_ALARM_CLOCK03_RINGTONE_PATH,prof_notification);
	};

	final static private void buildAlarmClokProfileTaFunc(Context context, AdapterProfileList pfa, 
			SharedPreferences pref, String grp_name, String id, 
			boolean alarm_enabled, String date_type,
			String day_of_week, String day, String time, String sound_type, 
			String sound_name, String ringtone_id, String prof_notification) {
		
		ProfileListItem tpli;
		ArrayList<String> act;
		ArrayList<String> trig;
		ArrayList<String> pre=new ArrayList<String>();
		ArrayList<String> post=new ArrayList<String>();
		pre.add("pre1");pre.add("pre2");
		post.add("post1");post.add("post2");
		if (alarm_enabled && !sound_type.equals("")) {
			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			act.add("#QT-AM"+id);
			trig.add("#QT-AT"+id);
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-AC"+id, 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);
			if (sound_type.equals(PROFILE_ACTION_TYPE_MUSIC) ) {
				tpli= new ProfileListItem();
				tpli.setActionMusicEntry(PROFILE_VERSION_CURRENT,grp_name,false, System.currentTimeMillis(),
						PROFILE_TYPE_ACTION,"#QT-AM"+id, 
						PROFILE_ENABLED,
						sound_name,
						"-1","-1");
				pfa.addProfItem(tpli);
			} else {
				tpli= new ProfileListItem();
				tpli.setActionRingtoneEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(), 
						PROFILE_TYPE_ACTION,"#QT-AM"+id, 
						PROFILE_ENABLED,
						sound_type,
						sound_name,
						pref.getString(ringtone_id,""),
						"-1","-1");
				pfa.addProfItem(tpli);
			}
			
			String e_day="****/**/**",e_dw="0000000";
			if (date_type.equals(PROFILE_DATE_TIME_TYPE_DAY_OF_THE_WEEK)) {
				e_dw=day_of_week;
			} else if (date_type.equals(PROFILE_DATE_TIME_TYPE_EVERY_DAY)) {
				
			} else if (date_type.equals(PROFILE_DATE_TIME_TYPE_ONE_SHOT)) {
				e_day=day;
			}
			tpli= new ProfileListItem();
			tpli.setTimeEventEntry(PROFILE_VERSION_CURRENT,grp_name,false, System.currentTimeMillis(),
					PROFILE_TYPE_TIME,"#QT-AT"+id, 
					PROFILE_ENABLED,
					date_type,
					e_dw,
					e_day,
					time);
			pfa.addProfItem(tpli);
		}
	};
	 
	final static private void buildQuickTaskTimeActivityTaFunc(Context context, AdapterProfileList pfa, 
			CommonUtilities util, String grp_name, String prof_notification) {
		SharedPreferences pref=util.getPrefMgr();
		buildTimeActivityProfileTaFunc(context, pfa,pref, 
				grp_name, "01",
				pref.getBoolean(QUICK_TASK_TIME_ACTIVITY01_ENABLED,true),
				pref.getString(QUICK_TASK_TIME_ACTIVITY01_DATE_TYPE,""),
				pref.getString(QUICK_TASK_TIME_ACTIVITY01_DAY_OF_WEEK,""),
				pref.getString(QUICK_TASK_TIME_ACTIVITY01_DATE_DAY,""),
				pref.getString(QUICK_TASK_TIME_ACTIVITY01_DATE_TIME,""),
				pref.getString(QUICK_TASK_TIME_ACTIVITY01_ACTIVITY,""),
				prof_notification);

		buildTimeActivityProfileTaFunc(context, pfa,pref, 
				grp_name, "02",
				pref.getBoolean(QUICK_TASK_TIME_ACTIVITY02_ENABLED,true),
				pref.getString(QUICK_TASK_TIME_ACTIVITY02_DATE_TYPE,""),
				pref.getString(QUICK_TASK_TIME_ACTIVITY02_DAY_OF_WEEK,""),
				pref.getString(QUICK_TASK_TIME_ACTIVITY02_DATE_DAY,""),
				pref.getString(QUICK_TASK_TIME_ACTIVITY02_DATE_TIME,""),
				pref.getString(QUICK_TASK_TIME_ACTIVITY02_ACTIVITY,""),
				prof_notification);

		buildTimeActivityProfileTaFunc(context, pfa,pref, 
				grp_name, "03",
				pref.getBoolean(QUICK_TASK_TIME_ACTIVITY03_ENABLED,true),
				pref.getString(QUICK_TASK_TIME_ACTIVITY03_DATE_TYPE,""),
				pref.getString(QUICK_TASK_TIME_ACTIVITY03_DAY_OF_WEEK,""),
				pref.getString(QUICK_TASK_TIME_ACTIVITY03_DATE_DAY,""),
				pref.getString(QUICK_TASK_TIME_ACTIVITY03_DATE_TIME,""),
				pref.getString(QUICK_TASK_TIME_ACTIVITY03_ACTIVITY,""),
				prof_notification);

	};

	final static private void buildTimeActivityProfileTaFunc(Context context, AdapterProfileList pfa, 
			SharedPreferences pref, String grp_name, String id, 
			boolean alarm_enabled, String date_type,
			String day_of_week, String day, String time, String time_activity, String prof_notification) {
		
		ProfileListItem tpli;
		ArrayList<String> act;
		ArrayList<String> trig;
		ArrayList<String> pre=new ArrayList<String>();
		ArrayList<String> post=new ArrayList<String>();
		pre.add("pre1");pre.add("pre2");
		post.add("post1");post.add("post2");
		if (alarm_enabled && !time_activity.equals("")) {
			act=new ArrayList<String>();
			trig=new ArrayList<String>();
			act.add("#QT-TA"+id);
			trig.add("#QT-TT"+id);
			tpli= new ProfileListItem();
			tpli.setTaskEntry(PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_TASK,"#QT-TC"+id, 
					PROFILE_ENABLED,PROFILE_RETROSPECIVE_DISABLED,"0",
					prof_notification,act,trig);
			pfa.addProfItem(tpli);

			String act_name=time_activity.substring(0,time_activity.indexOf("("));
			String pkg_name=time_activity.replace(act_name+"(", "").replace(")", "");
			tpli= new ProfileListItem();
			tpli.setActionAndroidEntry( PROFILE_VERSION_CURRENT,grp_name,false,System.currentTimeMillis(),
					PROFILE_TYPE_ACTION,"#QT-TA"+id, 
					PROFILE_ENABLED,act_name,pkg_name,"","",new ArrayList<ActivityExtraDataItem>());
			pfa.addProfItem(tpli);
			
			String e_day="****/**/**",e_dw="0000000";
			if (date_type.equals(PROFILE_DATE_TIME_TYPE_DAY_OF_THE_WEEK)) {
				e_dw=day_of_week;
			} else if (date_type.equals(PROFILE_DATE_TIME_TYPE_EVERY_DAY)) {
				
			} else if (date_type.equals(PROFILE_DATE_TIME_TYPE_ONE_SHOT)) {
				e_day=day;
			}
			tpli= new ProfileListItem();
			tpli.setTimeEventEntry(PROFILE_VERSION_CURRENT,grp_name,false, System.currentTimeMillis(),
					PROFILE_TYPE_TIME,"#QT-TT"+id, 
					PROFILE_ENABLED,
					date_type,
					e_dw,
					e_day,
					time);
			pfa.addProfItem(tpli);

		}

	};

}

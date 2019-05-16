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

import com.sentaroh.android.TaskAutomation.R;
import android.content.Context;

public class ServiceMessages {
    public String msgs_svc_started;
    public String msgs_main_monitor_ignored;
    public String msgs_main_monitor_issued;
    public String msgs_svc_termination;
    public String msgs_widget_battery_status_charge_charging;
    public String msgs_widget_battery_status_charge_discharging;
    public String msgs_widget_battery_status_charge_full;
    public String msgs_svc_task_already_started;
    public String msgs_svc_action_blocked;
    public String msgs_create_profile_error;
    public String msgs_svc_notification_info_next_schedule;
    public String msgs_svc_notification_info_build_time;
    public String msgs_svc_notification_info_task_list;
    public String msgs_svc_notification_info_no_task_list;
    
    public String msgs_svc_notification_info_battery_title;
    public String msgs_svc_notification_info_battery_level;
    
    public String msgs_svc_notification_info_wake_lock_status_hold;
    public String msgs_svc_notification_info_wake_lock_status_not_hold;
    
    public String msgs_key_gurad_info_keyguard_ctrl_enabled;
    public String msgs_key_gurad_info_keyguard_disabled_after_unlock;
    public String msgs_key_gurad_info_keyguard_ctrl_disabled;
    
    final public void loadString(Context c) {
    	msgs_svc_started=c.getString(R.string.msgs_svc_started);
    	msgs_main_monitor_ignored=c.getString(R.string.msgs_main_monitor_ignored);
    	msgs_main_monitor_issued=c.getString(R.string.msgs_main_monitor_issued); 
    	msgs_svc_termination=c.getString(R.string.msgs_svc_termination);
    	msgs_widget_battery_status_charge_charging=c.getString(R.string.msgs_widget_battery_status_charge_charging);
    	msgs_widget_battery_status_charge_discharging=c.getString(R.string.msgs_widget_battery_status_charge_discharging);
    	msgs_widget_battery_status_charge_full=c.getString(R.string.msgs_widget_battery_status_charge_full);
    	msgs_svc_task_already_started=c.getString(R.string.msgs_svc_task_already_started);
    	msgs_svc_action_blocked=c.getString(R.string.msgs_svc_action_blocked);
    	msgs_create_profile_error=c.getString(R.string.msgs_create_profile_error);
    	
    	msgs_svc_notification_info_next_schedule=c.getString(R.string.msgs_svc_notification_info_next_schedule);
    	msgs_svc_notification_info_build_time=c.getString(R.string.msgs_svc_notification_info_build_time);
    	msgs_svc_notification_info_task_list=c.getString(R.string.msgs_svc_notification_info_task_list);
    	msgs_svc_notification_info_no_task_list=c.getString(R.string.msgs_svc_notification_info_no_task_list);
    	
    	msgs_svc_notification_info_battery_title=c.getString(R.string.msgs_svc_notification_info_battery_title);
    	msgs_svc_notification_info_battery_level=c.getString(R.string.msgs_svc_notification_info_battery_level);
    	
    	msgs_svc_notification_info_wake_lock_status_hold=c.getString(R.string.msgs_svc_notification_info_wake_lock_status_hold);
    	msgs_svc_notification_info_wake_lock_status_not_hold=c.getString(R.string.msgs_svc_notification_info_wake_lock_status_not_hold);
    	
        msgs_key_gurad_info_keyguard_ctrl_disabled=c.getString(R.string.msgs_key_gurad_info_keyguard_ctrl_disabled);
        msgs_key_gurad_info_keyguard_ctrl_enabled=c.getString(R.string.msgs_key_gurad_info_keyguard_ctrl_enabled);
        msgs_key_gurad_info_keyguard_disabled_after_unlock=c.getString(R.string.msgs_key_gurad_info_keyguard_disabled_after_unlock);

    };
}

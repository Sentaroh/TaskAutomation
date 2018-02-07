package com.sentaroh.android.TaskAutomationInterface;

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

public class TaInterfaceParms {
	//Request parameters
	public String request_type=null;
	public String reply_action=null;
	public String requestor_name=null;
	public String requestor_pkg=null;

	public String request_method_name=null;
	public String request_cmd=null;
	public String request_cmd_sub_type=null;
	public String request_group_name=null;
	public String request_task_name=null;
	
	public boolean reply_result_success=false;
	public int reply_result_status_code=0;
	
	public boolean availavility_sys_info=false;
	public boolean availavility_task_list=false;
	public boolean availavility_group_list=false;
	
	//Reply Task list
	public String[][] reply_task_list=null;

	//Reply Group list
	public String[][] reply_group_list=null;

	//Reply Status
	public boolean airplane_mode_on=false;
	public boolean battery_charging=false;
	public int battery_level=0;
	public boolean bluetooth_active=false;
	public boolean bluetooth_available=false;
	public String bluetooth_device_name=null;
	public String bluetooth_device_addr=null;
	public boolean ringer_mode_normal=false;
	public boolean ringer_mode_silent=false;
	public boolean ringer_mode_vibrate=false;
	public boolean light_sensor_active=false;
	public boolean light_sensor_available=false;
	public int light_sensor_value=0;
	public boolean mobile_network_connected=false;
	public boolean proximity_sensor_active=false;
	public boolean proximity_sensor_available=false;
	public boolean proximity_sensor_detected=false;
	public boolean screen_locked=false;
	public boolean telephony_available=false;
	public boolean telephony_state_idle=false;
	public boolean telephony_state_offhook=false;
	public boolean telephony_state_ringing=false;
	public boolean wifi_active=false;
	public String wifi_ssid_name=null;
	public String wifi_ssid_addr=null;
}

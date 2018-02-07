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

final public class TaSysInfo {
	private TaInterfaceParms taip=null;
	public TaSysInfo(TaInterfaceParms taip) {
		this.taip=taip;
	}
	final public boolean 	isAirplaneModeon() {return taip.airplane_mode_on;}
	final public boolean 	isBatteryCharging() {return taip.battery_charging;}
	final public int 		getBatteryLevel() {return taip.battery_level;}
	final public boolean 	isBluetoothActive() {return taip.bluetooth_active;}
	final public boolean 	isBluetoothAvailabe() {return taip.bluetooth_available;}
	final public String 	getBluetoothDeviceName() {return taip.bluetooth_device_name;}
	final public String 	getBluetoothDeviceAddr() {return taip.bluetooth_device_addr;}
	final public boolean 	isRingerModeNormal() {return taip.ringer_mode_normal;}
	final public boolean 	isRingerModeSilent() {return taip.ringer_mode_silent;}
	final public boolean 	isRingerModeVibrate() {return taip.ringer_mode_vibrate;}
	final public boolean 	isLightSensorActive() {return taip.light_sensor_active;}
	final public boolean 	isLightSensorAvailable() {return taip.light_sensor_available;}
	final public int 		getLightSensorValue() {return taip.light_sensor_value;}
	final public boolean 	isMobileNetworkConnected() {return taip.mobile_network_connected;}
	final public boolean 	isProximitySensorActive() {return taip.proximity_sensor_active;}
	final public boolean 	isProximitySensorAvailable() {return taip.proximity_sensor_available;}
	final public boolean 	isProximitySensorDetected() {return taip.proximity_sensor_detected;}
	final public boolean 	isScreenLocked() {return taip.screen_locked;}
	final public boolean 	isTelephonyAvailable() {return taip.telephony_available;}
	final public boolean 	isTelephonyStateIdle() {return taip.telephony_state_idle;}
	final public boolean 	isTelephonyStateOffhook() {return taip.telephony_state_offhook;}
	final public boolean 	isTelephonyStateRinging() {return taip.telephony_state_ringing;}
	final public boolean 	isWifiActive() {return taip.wifi_active;}
	final public String 	getWifiSsidName() {return taip.wifi_ssid_name;}
	final public String 	getWifiSsidAddr() {return taip.wifi_ssid_addr;}

}

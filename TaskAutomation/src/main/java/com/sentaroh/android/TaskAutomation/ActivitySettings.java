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

import java.util.List;

import com.sentaroh.android.TaskAutomation.CommonUtilities;
import com.sentaroh.android.TaskAutomation.GlobalParameters;
import com.sentaroh.android.TaskAutomation.GlobalWorkArea;
import com.sentaroh.android.TaskAutomation.R;
import com.sentaroh.android.TaskAutomation.EnvironmentParms;
import com.sentaroh.android.Utilities.LocalMountPoint;
import com.sentaroh.android.Utilities.ShellCommandUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

@SuppressLint("NewApi")
@SuppressWarnings("deprecation")
public class ActivitySettings extends PreferenceActivity{
	private static Context mContext=null;
	private static PreferenceActivity mPrefAct=null;
	private static PreferenceFragment mPrefFrag=null;
	
	private static EnvironmentParms mEnvParms=null;
	private static CommonUtilities mUtilMain=null;
    
	private static void initEnvParms(Context c, boolean force) {
        if (mEnvParms==null) {
        	mContext=c;
        	mEnvParms=new EnvironmentParms();
        	mEnvParms.loadSettingParms(c);
            GlobalParameters mGp= GlobalWorkArea.getGlobalParameters(c);
            mUtilMain=new CommonUtilities(c.getApplicationContext(), "SettingsActivity",mEnvParms, mGp);
        }
	}

    @Override
    protected boolean isValidFragment(String fragmentName) {
        // 使用できる Fragment か確認する

        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
		mPrefAct=this;
		mContext=getApplicationContext();
        initEnvParms(this,false);
        GlobalParameters mGp= GlobalWorkArea.getGlobalParameters(mContext);
        mUtilMain.addDebugMsg(1,"I","onCreate entered");

        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT>=11) return;
        
		PreferenceManager pm=getPreferenceManager();
		pm.setSharedPreferencesName(DEFAULT_PREFS_FILENAME);
		pm.setSharedPreferencesMode(Context.MODE_PRIVATE|Context.MODE_MULTI_PROCESS);
		addPreferencesFromResource(R.xml.main_settings);

		SharedPreferences shared_pref=pm.getSharedPreferences();
		
		setRootPrivelegeCBListener(pm, mContext);

		pref_light=mPrefAct.findPreference(mContext.getString(R.string.settings_main_scheduler_sleep_wake_lock_light_sensor));
		pref_proximity=mPrefAct.findPreference(mContext.getString(R.string.settings_main_scheduler_sleep_wake_lock_proximity_sensor));

		initSettingValueBeforeHc(shared_pref,getString(R.string.settings_main_scheduler_monitor));
		
		initSettingValueBeforeHc(shared_pref,getString(R.string.settings_main_enable_scheduler));
		initSettingValueBeforeHc(shared_pref,getString(R.string.settings_main_scheduler_max_task_count));
		initSettingValueBeforeHc(shared_pref,getString(R.string.settings_main_scheduler_thread_pool_count));
		initSettingValueBeforeHc(shared_pref,getString(R.string.settings_main_device_admin));
		initSettingValueBeforeHc(shared_pref,getString(R.string.settings_main_scheduler_sleep_wake_lock_option));
		initSettingValueBeforeHc(shared_pref,getString(R.string.settings_main_scheduler_sleep_wake_lock_light_sensor));
		initSettingValueBeforeHc(shared_pref,getString(R.string.settings_main_scheduler_sleep_wake_lock_proximity_sensor));
		initSettingValueBeforeHc(shared_pref,getString(R.string.settings_main_use_root_privilege));
		initSettingValueBeforeHc(shared_pref,getString(R.string.settings_main_log_option));
		initSettingValueBeforeHc(shared_pref,getString(R.string.settings_main_log_file_max_count));
		initSettingValueBeforeHc(shared_pref,getString(R.string.settings_main_log_level));
		initSettingValueBeforeHc(shared_pref,getString(R.string.settings_main_exit_clean));

		initSettingValueBeforeHc(shared_pref,getString(R.string.settings_main_light_sensor_use_thread));
		initSettingValueBeforeHc(shared_pref,getString(R.string.settings_main_light_sensor_monitor_interval_time));
		initSettingValueBeforeHc(shared_pref,getString(R.string.settings_main_light_sensor_monitor_active_time));
		initSettingValueBeforeHc(shared_pref,getString(R.string.settings_main_light_sensor_detect_thresh_hold));
		initSettingValueBeforeHc(shared_pref,getString(R.string.settings_main_light_sensor_ignore_time));

	};

	private static void initSettingValueBeforeHc(SharedPreferences shared_pref, String key_string) {
		initSettingValue(mPrefAct.findPreference(key_string),shared_pref,key_string);
	};

	private static void initSettingValueAfterHc(SharedPreferences shared_pref, String key_string) {
		initSettingValue(mPrefFrag.findPreference(key_string),shared_pref,key_string);
	};
	
	private static void initSettingValue(Preference pref_key,
			SharedPreferences shared_prefs, String key_string) {
		if (!checkSchedulerSettings(pref_key,shared_prefs, key_string,mContext)) 
    	if (!checkLogSettings(pref_key,shared_prefs, key_string,mContext))
   		if (!checkLightSensorSettings(pref_key,shared_prefs, key_string,mContext))
		if (!checkMiscSettings(pref_key,shared_prefs, key_string,mContext))			
			checkOtherSettings(pref_key,shared_prefs, key_string,mContext);
	};
		
 
    @Override
    public void onStart(){
        super.onStart();
        mUtilMain.addDebugMsg(1,"I","onStart entered");
    };
 
    @Override
    public void onResume(){
        super.onResume();
        mUtilMain.addDebugMsg(1,"I","onResume entered");
		setTitle(R.string.settings_main_title);
        if (Build.VERSION.SDK_INT<=10) {
    	    mPrefAct.getPreferenceScreen().getSharedPreferences()
    			.registerOnSharedPreferenceChangeListener(listenerBeforeHc);  
        } else {
//    	    mPrefFrag.getPreferenceScreen().getSharedPreferences()
//    			.registerOnSharedPreferenceChangeListener(listenerAfterHc);  
        }
    };
 
    @Override
    public void onBuildHeaders(List<Header> target) {
    	initEnvParms(this,false);
    	mUtilMain.addDebugMsg(1,"I","onBuildHeaders entered");
        loadHeadersFromResource(R.xml.settings_frag, target);
    };

//    @Override
//    public boolean isMultiPane () {
//    	if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"isMultiPane entered");
//        return true;
//    };

    @Override
    public boolean onIsMultiPane () {
    	initEnvParms(this,false);
    	mUtilMain.addDebugMsg(1,"I","onIsMultiPane entered");
        return true;
    };

	@Override  
	protected void onPause() {  
	    super.onPause();  
	    mUtilMain.addDebugMsg(1,"I","onPause entered");
        if (Build.VERSION.SDK_INT<=10) {
    	    mPrefAct.getPreferenceScreen().getSharedPreferences()
    			.unregisterOnSharedPreferenceChangeListener(listenerBeforeHc);  
        } else {
//    	    mPrefFrag.getPreferenceScreen().getSharedPreferences()
//    			.unregisterOnSharedPreferenceChangeListener(listenerAfterHc);  
        }
	};

	@Override
	final public void onStop() {
		super.onStop();
		mUtilMain.addDebugMsg(1,"I","onStop entered");
	};

	@Override
	final public void onDestroy() {
		super.onDestroy();
		mUtilMain.addDebugMsg(1,"I","onDestroy entered");
	};

	static Preference pref_light=null;
	static Preference pref_proximity=null;

	private SharedPreferences.OnSharedPreferenceChangeListener listenerBeforeHc =   
		    new SharedPreferences.OnSharedPreferenceChangeListener() {  
		    public void onSharedPreferenceChanged(SharedPreferences shared_pref, 
		    		String key_string) {
		    	Preference pref_key=mPrefAct.findPreference(key_string);
		    	
		    	if (!checkSchedulerSettings(pref_key,shared_pref, key_string,mContext)) 
	    		if (!checkLogSettings(pref_key,shared_pref, key_string,mContext))
    			if (!checkLightSensorSettings(pref_key,shared_pref, key_string,mContext))
   				if (!checkMiscSettings(pref_key,shared_pref, key_string,mContext))   					
		    		checkOtherSettings(pref_key,shared_pref, key_string,mContext);
		    }
	};
	
	private static SharedPreferences.OnSharedPreferenceChangeListener listenerAfterHc =   
		    new SharedPreferences.OnSharedPreferenceChangeListener() {  
		    public void onSharedPreferenceChanged(SharedPreferences shared_pref, 
		    		String key_string) {
		    	Preference pref=mPrefFrag.findPreference(key_string);
		    	
		    	if (!checkSchedulerSettings(pref,shared_pref, key_string,mContext)) 
	    		if (!checkLogSettings(pref,shared_pref, key_string,mContext))
    			if (!checkLightSensorSettings(pref,shared_pref, key_string,mContext))
				if (!checkMiscSettings(pref,shared_pref, key_string,mContext))		    				
  					checkOtherSettings(pref,shared_pref, key_string,mContext);
		    }
	};


	private static boolean checkSchedulerSettings(final Preference pref_key, 
			final SharedPreferences shared_pref, final String key_string, final Context c) {
		boolean isChecked = false;
		if (key_string.equals(c.getString(R.string.settings_main_scheduler_monitor))) {
    		isChecked=true;
    	} else if (key_string.equals(c.getString(R.string.settings_main_enable_scheduler))) {
    		isChecked=true;
    	} else if (key_string.equals(c.getString(R.string.settings_main_scheduler_max_task_count))) {
    		isChecked=true;
    		if (shared_pref.getString(c.getString(R.string.settings_main_scheduler_max_task_count), "").equals("")) {
    			shared_pref.edit().putString(c.getString(R.string.settings_main_scheduler_max_task_count),"20").commit();
    		}
    		checkOtherSettings(pref_key,shared_pref, key_string,c);
    	} else if (key_string.equals(c.getString(R.string.settings_main_scheduler_thread_pool_count))) {
    		isChecked=true;
    		if (shared_pref.getString(c.getString(R.string.settings_main_scheduler_thread_pool_count), "").equals("")) {
    			shared_pref.edit().putString(c.getString(R.string.settings_main_scheduler_thread_pool_count),"5").commit();
    		}
    		checkOtherSettings(pref_key, shared_pref, key_string,c);
    	} else if (key_string.equals(c.getString(R.string.settings_main_device_admin))) {
    		isChecked=true;
    	} else if (key_string.equals(c.getString(R.string.settings_main_scheduler_sleep_wake_lock_option))) {
    		isChecked=true;
    		String wl_option=shared_pref.getString(c.getString(R.string.settings_main_scheduler_sleep_wake_lock_option), "");
    		if (wl_option.equals("")) {
    			shared_pref.edit().putString(c.getString(R.string.settings_main_scheduler_sleep_wake_lock_option), WAKE_LOCK_OPTION_SYSTEM);
    			wl_option=WAKE_LOCK_OPTION_SYSTEM;
    		}
    		String[] wl_label= c.getResources().getStringArray(R.array.settings_main_scheduler_sleep_wake_lock_option_list_entries);
    		int pos=Integer.parseInt(wl_option);
    		if (pos<wl_label.length) pref_key.setSummary(wl_label[pos]);
    		else pref_key.setSummary(wl_label[0]);

    		if (shared_pref.getString(c.getString(R.string.settings_main_scheduler_sleep_wake_lock_option), WAKE_LOCK_OPTION_SYSTEM).equals(WAKE_LOCK_OPTION_DISCREATE)) {
    			pref_light.setEnabled(mUtilMain.isLightSensorAvailable()!=null);
    			pref_proximity.setEnabled(mUtilMain.isProximitySensorAvailable()!=null);
    		} else {
    			pref_light.setEnabled(false);
    			pref_proximity.setEnabled(false);
    		}
    	} else if (key_string.equals(c.getString(R.string.settings_main_scheduler_sleep_wake_lock_light_sensor))) {
    		isChecked=true;
    	} else if (key_string.equals(c.getString(R.string.settings_main_scheduler_sleep_wake_lock_proximity_sensor))) {
    		isChecked=true;
    	} else if (key_string.equals(c.getString(R.string.settings_main_use_root_privilege))) {
    		isChecked=true;
    	}
    	return isChecked;
	};
	
	private static void setRootPrivelegeCBListener(PreferenceManager pm, Context c) {
		CheckBoxPreference cbp=(CheckBoxPreference) pm.findPreference(c.getString(R.string.settings_main_use_root_privilege));
		cbp.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {  
	          @Override  
	          public boolean onPreferenceChange( Preference preference, Object newValue) {
	        	  boolean n_v=(Boolean)newValue;
	        	  boolean result=false;
	        	  if (n_v) {
	        		  result=ShellCommandUtil.isSuperUserAvailable();
	        		  if (!result)
	        			  Toast.makeText(mContext, 
        					  mContext.getString(R.string.settings_main_use_root_privilege_summary_not_granted), Toast.LENGTH_LONG).show();
		          } else {
		        	  result=true;
		          }
		          return result;  
	          }      
		}); 
	}

	private static boolean checkMiscSettings(Preference pref_key, 
			SharedPreferences shared_pref, String key_string, Context c) {
		boolean isChecked = false;
    	if (key_string.equals(c.getString(R.string.settings_main_exit_clean))) {
    		isChecked=true;
    	}
    	return isChecked;
	};

	private static boolean checkLogSettings(Preference pref_key, 
			SharedPreferences shared_pref, String key_string, Context c) {
		boolean isChecked = false;
    	if (key_string.equals(c.getString(R.string.settings_main_log_option))) {
    		isChecked=true;
    	} else if (key_string.equals(c.getString(R.string.settings_main_log_file_max_count))) {
    		isChecked=true;
    		pref_key.setSummary(c.getString(R.string.settings_main_log_file_max_count_summary)+
    				shared_pref.getString(key_string,"10"));
    	} 

    	return isChecked;
	};

	private static boolean checkLightSensorSettings(Preference pref_key, 
			SharedPreferences shared_pref, String key_string, Context c) {
		boolean isChecked = false;
		if (key_string.equals(c.getString(R.string.settings_main_light_sensor_use_thread))) {
    		isChecked=true;
    	} else if (key_string.equals(c.getString(R.string.settings_main_light_sensor_monitor_interval_time))) {
    		isChecked=true;
    		pref_key.setSummary(c.getString(R.string.settings_main_light_sensor_monitor_interval_time_summary)+
    				shared_pref.getString(key_string,"0"));
    	} else if (key_string.equals(c.getString(R.string.settings_main_light_sensor_monitor_active_time))) {
    		isChecked=true;
    		pref_key.setSummary(c.getString(R.string.settings_main_light_sensor_monitor_active_time_summary)+
    				shared_pref.getString(key_string,"0"));
    	} else if (key_string.equals(c.getString(R.string.settings_main_light_sensor_detect_thresh_hold))) {
    		isChecked=true;
    		pref_key.setSummary(c.getString(R.string.settings_main_light_sensor_detect_thresh_hold_summary)+
    				shared_pref.getString(key_string,"0"));
    	} else if (key_string.equals(c.getString(R.string.settings_main_light_sensor_ignore_time))) {
    		isChecked=true;
    		pref_key.setSummary(c.getString(R.string.settings_main_light_sensor_ignore_time_summary)+
    				shared_pref.getString(key_string,"2"));
    	}
    	return isChecked;
	};
	
	private static boolean checkOtherSettings(Preference pref_key, 
			SharedPreferences shared_pref, String key_string, Context c) {
		boolean isChecked = true;
//		Log.v("","key="+key_string);
		pref_key.setSummary(
    			c.getString(R.string.settings_main_default_current_setting)+
	    		shared_pref.getString(key_string, "0"));    	
    	return isChecked;
	};

 
    public static class SettingsSceduler extends PreferenceFragment {
    	private static CommonUtilities mUtilScheduler=null;
        @Override
        public void onCreate(Bundle savedInstanceState) {
        	super.onCreate(savedInstanceState);
            mPrefFrag=this;
            mContext=this.getActivity().getApplicationContext();
            GlobalParameters mGp= GlobalWorkArea.getGlobalParameters(mContext);
            initEnvParms(mContext,false);
            mUtilScheduler=new CommonUtilities(mContext.getApplicationContext(), "SettingsScheduler",mEnvParms, mGp);
        	mUtilScheduler.addDebugMsg(1,"I","onCreate entered");
            
    		PreferenceManager pm=getPreferenceManager();
    		pm.setSharedPreferencesName(DEFAULT_PREFS_FILENAME);
    		pm.setSharedPreferencesMode(Context.MODE_PRIVATE|Context.MODE_MULTI_PROCESS);

    		addPreferencesFromResource(R.xml.settings_frag_scheduler);

//    		getActivity().setTitle("TaskAutomation設定");
    		
    		SharedPreferences shared_pref=pm.getSharedPreferences();
    		
    		setRootPrivelegeCBListener(pm, mContext);

			pref_light=mPrefFrag.findPreference(mContext.getString(R.string.settings_main_scheduler_sleep_wake_lock_light_sensor));
			pref_proximity=mPrefFrag.findPreference(mContext.getString(R.string.settings_main_scheduler_sleep_wake_lock_proximity_sensor));

    		initSettingValueAfterHc(shared_pref,getString(R.string.settings_main_scheduler_monitor));
    		initSettingValueAfterHc(shared_pref,getString(R.string.settings_main_enable_scheduler));
    		initSettingValueAfterHc(shared_pref,getString(R.string.settings_main_scheduler_max_task_count));
    		initSettingValueAfterHc(shared_pref,getString(R.string.settings_main_scheduler_thread_pool_count));
    		initSettingValueAfterHc(shared_pref,getString(R.string.settings_main_device_admin));
    		initSettingValueAfterHc(shared_pref,getString(R.string.settings_main_scheduler_sleep_wake_lock_option));
    		initSettingValueAfterHc(shared_pref,getString(R.string.settings_main_scheduler_sleep_wake_lock_light_sensor));
    		initSettingValueAfterHc(shared_pref,getString(R.string.settings_main_scheduler_sleep_wake_lock_proximity_sensor));
    		initSettingValueAfterHc(shared_pref,getString(R.string.settings_main_use_root_privilege));
    		
   		};
        
        @Override
        public void onStart() {
        	super.onStart();
        	mUtilScheduler.addDebugMsg(1,"I","onStart entered");
    	    getPreferenceScreen().getSharedPreferences()
    			.registerOnSharedPreferenceChangeListener(listenerAfterHc);
//    		getActivity().setTitle(R.string.settings_main_title);
        };
        @Override
        public void onStop() {
        	super.onStop();
        	mUtilScheduler.addDebugMsg(1,"I","onStop entered");
    	    getPreferenceScreen().getSharedPreferences()
    			.unregisterOnSharedPreferenceChangeListener(listenerAfterHc);  
        };
    };
    
    public static class SettingsLog extends PreferenceFragment {
    	private static CommonUtilities mUtilLog=null;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mPrefFrag=this;
            mContext=this.getActivity().getApplicationContext();
            GlobalParameters mGp= GlobalWorkArea.getGlobalParameters(mContext);
            initEnvParms(mContext,false);

            mUtilLog=new CommonUtilities(mContext.getApplicationContext(), "SettingsLog",mEnvParms, mGp);
            mUtilLog.addDebugMsg(1,"I","onCreate entered");
            
    		PreferenceManager pm=getPreferenceManager();
    		pm.setSharedPreferencesName(DEFAULT_PREFS_FILENAME);
    		pm.setSharedPreferencesMode(Context.MODE_PRIVATE|Context.MODE_MULTI_PROCESS);

            addPreferencesFromResource(R.xml.settings_frag_log);

    		SharedPreferences shared_pref=pm.getSharedPreferences();

    		initSettingValueAfterHc(shared_pref,getString(R.string.settings_main_log_option));
    		initSettingValueAfterHc(shared_pref,getString(R.string.settings_main_log_file_max_count));
    		initSettingValueAfterHc(shared_pref,getString(R.string.settings_main_log_level));
        };
        
        @Override
        public void onStart() {
        	super.onStart();
        	mUtilLog.addDebugMsg(1,"I","onStart entered");
    	    getPreferenceScreen().getSharedPreferences()
    			.registerOnSharedPreferenceChangeListener(listenerAfterHc);
//    	    getActivity().setTitle(R.string.settings_main_title);
        };
        @Override
        public void onStop() {
        	super.onStop();
        	mUtilLog.addDebugMsg(1,"I","onStop entered");
    	    getPreferenceScreen().getSharedPreferences()
    			.unregisterOnSharedPreferenceChangeListener(listenerAfterHc);  
        };

    };

    public static class SettingsMisc extends PreferenceFragment {
    	private static CommonUtilities mUtilMisc=null;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mPrefFrag=this;
            mContext=this.getActivity().getApplicationContext();
            GlobalParameters mGp= GlobalWorkArea.getGlobalParameters(mContext);
            initEnvParms(mContext,false);
            mUtilMisc=new CommonUtilities(mContext.getApplicationContext(), "SettingsMisc",mEnvParms, mGp);
            mUtilMisc.addDebugMsg(1,"I","onCreate entered");

    		PreferenceManager pm=getPreferenceManager();
    		pm.setSharedPreferencesName(DEFAULT_PREFS_FILENAME);
    		pm.setSharedPreferencesMode(Context.MODE_PRIVATE|Context.MODE_MULTI_PROCESS);

            addPreferencesFromResource(R.xml.settings_frag_misc);

            mPrefFrag=this;
            mContext=this.getActivity().getApplicationContext();

    		SharedPreferences shared_pref=pm.getSharedPreferences();

    		initSettingValueAfterHc(shared_pref,getString(R.string.settings_main_exit_clean));
        };
        
        @Override
        public void onStart() {
        	super.onStart();
        	mUtilMisc.addDebugMsg(1,"I","onStart entered");
    	    getPreferenceScreen().getSharedPreferences()
    			.registerOnSharedPreferenceChangeListener(listenerAfterHc);
//    	    getActivity().setTitle(R.string.settings_main_title);
        };
        @Override
        public void onStop() {
        	super.onStop();
        	mUtilMisc.addDebugMsg(1,"I","onStop entered");
    	    getPreferenceScreen().getSharedPreferences()
    			.unregisterOnSharedPreferenceChangeListener(listenerAfterHc);  
        };
    };

    public static class SettingsLight extends PreferenceFragment {
    	private static CommonUtilities mUtilLight=null;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mPrefFrag=this;
            mContext=this.getActivity().getApplicationContext();
            GlobalParameters mGp= GlobalWorkArea.getGlobalParameters(mContext);
            initEnvParms(mContext,false);
            mUtilLight=new CommonUtilities(mContext.getApplicationContext(), "SettingsLight",mEnvParms, mGp);
            mUtilLight.addDebugMsg(1,"I","onCreate entered");
    		PreferenceManager pm=getPreferenceManager();
    		pm.setSharedPreferencesName(DEFAULT_PREFS_FILENAME);
    		pm.setSharedPreferencesMode(Context.MODE_PRIVATE|Context.MODE_MULTI_PROCESS);

            addPreferencesFromResource(R.xml.settings_frag_light);

    		SharedPreferences shared_pref=pm.getSharedPreferences();

    		initSettingValueAfterHc(shared_pref,getString(R.string.settings_main_light_sensor_use_thread));
    		initSettingValueAfterHc(shared_pref,getString(R.string.settings_main_light_sensor_monitor_interval_time));
    		initSettingValueAfterHc(shared_pref,getString(R.string.settings_main_light_sensor_monitor_active_time));
    		initSettingValueAfterHc(shared_pref,getString(R.string.settings_main_light_sensor_detect_thresh_hold));
    		initSettingValueAfterHc(shared_pref,getString(R.string.settings_main_light_sensor_ignore_time));
        }
        @Override
        public void onStart() {
        	super.onStart();
        	mUtilLight.addDebugMsg(1,"I","onStart entered");
    	    getPreferenceScreen().getSharedPreferences()
    			.registerOnSharedPreferenceChangeListener(listenerAfterHc);
//    	    getActivity().setTitle(R.string.settings_main_title);
        };
        @Override
        public void onStop() {
        	super.onStop();
        	mUtilLight.addDebugMsg(1,"I","onStop entered");
    	    getPreferenceScreen().getSharedPreferences()
    			.unregisterOnSharedPreferenceChangeListener(listenerAfterHc);  
        };
    };

}
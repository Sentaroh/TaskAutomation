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
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_RINGTONE_TYPE_ALERT;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_RINGTONE_TYPE_NOTIFICATION;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_RINGTONE_TYPE_RINGTONE;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_ACTIVITY;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_ACTIVITY_DATA_TYPE_EXTRA;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_ACTIVITY_DATA_TYPE_NONE;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_ACTIVITY_DATA_TYPE_URI;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_ARRAY_NO;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_ARRAY_YES;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_BOOLEAN;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_INT;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_STRING;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_BSH_SCRIPT;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_COMPARE;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_COMPARE_COMPARE_BETWEEN;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_COMPARE_COMPARE_EQ;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_COMPARE_COMPARE_GT;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_COMPARE_COMPARE_LT;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_COMPARE_CPMPARE_NE;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_COMPARE_RESULT_ABORT;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_COMPARE_RESULT_CONTINUE;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_COMPARE_RESULT_SKIP;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_COMPARE_TARGET_BATTERY;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_COMPARE_TARGET_BLUETOOTH;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_COMPARE_TARGET_LIGHT;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_COMPARE_TARGET_TIME;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_COMPARE_TARGET_WIFI;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_MESSAGE;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_MESSAGE_DIALOG;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_MESSAGE_LED_BLUE;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_MESSAGE_LED_GREEN;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_MESSAGE_LED_RED;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_MESSAGE_NOTIFICATION;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_MUSIC;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_RINGTONE;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_TASK;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_TASK_START_TASK;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_TIME;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_WAIT;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_WAIT_TIMEOUT_TYPE_NOTIMEOUT;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_WAIT_TIMEOUT_TYPE_TIMEOUTIS;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_DISABLED;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ENABLED;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_TYPE_ACTION;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_VERSION_CURRENT;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.sentaroh.android.TaskAutomation.GlobalParameters;
import com.sentaroh.android.TaskAutomation.R;
import com.sentaroh.android.TaskAutomation.Common.ActivityExtraDataItem;
import com.sentaroh.android.TaskAutomation.Common.DataArrayEditListItem;
import com.sentaroh.android.TaskAutomation.Common.ProfileListItem;
import com.sentaroh.android.Utilities.LocalMountPoint;
import com.sentaroh.android.Utilities.NotifyEvent;
import com.sentaroh.android.Utilities.NotifyEvent.NotifyEventListener;
import com.sentaroh.android.Utilities.ContextMenu.CustomContextMenuItem.CustomContextMenuOnClickListener;
import com.sentaroh.android.Utilities.Dialog.CommonDialog;
import com.sentaroh.android.Utilities.Widget.CustomSpinnerAdapter;

public class ProfileMaintenanceActionProfile extends DialogFragment{
	private final static boolean DEBUG_ENABLE=false;
	private final static String APPLICATION_TAG="ProfileMaintenanceTaskProfile";

	private Dialog mDialog=null;
	private boolean mTerminateRequired=true;
//	private Context mContext=null;
	private ProfileMaintenanceActionProfile mFragment=null;
	private GlobalParameters mGlblParms=null;

	public static ProfileMaintenanceActionProfile newInstance() {
		if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"newInstance");
		ProfileMaintenanceActionProfile frag = new ProfileMaintenanceActionProfile();
        Bundle bundle = new Bundle();
        frag.setArguments(bundle);
        return frag;
    }
	public ProfileMaintenanceActionProfile() {
		if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"Constructor(Default)");
	};

	@Override
	public void onSaveInstanceState(Bundle outState) {  
		super.onSaveInstanceState(outState);
		if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"onSaveInstanceState");
		if(outState.isEmpty()){
	        outState.putBoolean("WORKAROUND_FOR_BUG_19917_KEY", true);
	    }
	};  
	
	@Override
	public void onConfigurationChanged(final Configuration newConfig) {
	    // Ignore orientation change to keep activity from restarting
	    super.onConfigurationChanged(newConfig);
	    if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"onConfigurationChanged");

	    reInitViewWidget();
	};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"onCreateView");
    	View view=super.onCreateView(inflater, container, savedInstanceState);
    	CommonDialog.setDlgBoxSizeLimit(mDialog,true);
    	return view;
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"onCreate");
    	mFragment=this;
        if (!mTerminateRequired) {
	        mGlblParms=(GlobalParameters)getActivity().getApplication();
        }
    };

	@Override
	final public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"onActivityCreated");
	};
	@Override
	final public void onAttach(Activity activity) {
	    super.onAttach(activity);
	    if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"onAttach");
	};
	@Override
	final public void onDetach() {
	    super.onDetach();
	    if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"onDetach");
	};
	@Override
	final public void onStart() {
	    super.onStart();
	    if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"onStart");
	    if (mTerminateRequired) mDialog.cancel(); 
	};
	@Override
	final public void onStop() {
	    super.onStop();
	    if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"onStop");
	};

	@Override
	public void onDestroyView() {
		if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"onDestroyView");
	    if (getDialog() != null && getRetainInstance())
	        getDialog().setDismissMessage(null);
	    super.onDestroyView();
	}
	@Override
	public void onCancel(DialogInterface di) {
		if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"onCancel");
		if (!mTerminateRequired) {
			final Button btnCancel = (Button) mDialog.findViewById(R.id.edit_profile_action_cancel_btn);
			btnCancel.performClick();
		}
		mFragment.dismiss();
		super.onCancel(di);
	}
	@Override
	public void onDismiss(DialogInterface di) {
		if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"onDismiss");
		super.onDismiss(di);
	}

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"onCreateDialog");

//    	mContext=getActivity().getApplicationContext();
    	mDialog=new Dialog(getActivity());
    	mDialog.setCanceledOnTouchOutside(false);
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		if (!mTerminateRequired) {
			initViewWidget();
		}
        return mDialog;
    };

    class SavedViewContents {
        CharSequence dlg_prof_name_et;
        int dlg_prof_name_et_spos, dlg_prof_name_et_epos;
        boolean cb_active, cb_enable_env_parms;

        CharSequence tv_sound_filename;
		boolean cb_music_vol;
		int sb_music_vol;
		boolean cb_ringtone_vol;
		int sb_ringtone_vol,spinnerActionType,spinnerActivityName;
		int spinnerActivityDataType,spinnerRingtoneType,spinnerRingtoneName;
		int spinnerCompareType;
		CharSequence et_comp_value1, et_comp_value2;
        int[] lv_comp_data=new int[]{-1,-1};
        int spinnerCompareResult,spinnerCompareTarget;
        int spinnerMessageType;
        CharSequence et_msg_text;
        boolean cb_vib_used,cb_led_used,rb_msg_blue,rb_msg_red,rb_msg_green;
        int spinnerTimeType,spinnerTimeTarget,spinnerTaskType,spinnerTaskTarget;
        int spinnerWaitTarget,spinnerWaitTimeoutType,spinnerWaitTimeoutValue;
        int spinnerWaitTimeoutUnits;
        CharSequence et_bsh_script;
		int spinnerBshMethod,spinnerCatMethod;
		int[] lv_aed=new int[]{-1,-1};
		CharSequence uri_data;
		
		CharSequence shell_cmd="";
		
        ArrayList<DataArrayEditListItem>data_array_adapter_list=new ArrayList<DataArrayEditListItem>();
        ArrayList<ActivityExtraDataItem>aed_adapter_list=new ArrayList<ActivityExtraDataItem>();

    };
    
    private SavedViewContents saveViewContents() {
    	SavedViewContents sv=new SavedViewContents();
		final EditText dlg_prof_name_et = (EditText) mDialog.findViewById(R.id.edit_profile_action_profile_et_name);
		final CheckBox cb_active = (CheckBox) mDialog.findViewById(R.id.edit_profile_action_enabled);
        final CheckBox cb_enable_env_parms=(CheckBox)mDialog.findViewById(R.id.edit_profile_action_enable_env_parms);

		final TextView tv_sound_filename=(TextView)mDialog.findViewById(R.id.edit_profile_action_exec_sound_file_name);
		final CheckBox cb_music_vol=(CheckBox)mDialog.findViewById(R.id.edit_profile_action_profile_sound_use_volume);
		final SeekBar sb_music_vol=(SeekBar)mDialog.findViewById(R.id.edit_profile_action_profile_sound_volume);
		final CheckBox cb_ringtone_vol=(CheckBox)mDialog.findViewById(R.id.edit_profile_action_profile_ringtone_use_volume);
		final SeekBar sb_ringtone_vol=(SeekBar)mDialog.findViewById(R.id.edit_profile_action_profile_ringtone_volume);
		final Spinner spinnerActionType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_action_type);
        final Spinner spinnerActivityName = (Spinner) mDialog.findViewById(R.id.edit_profile_action_exec_activity_name);
        final Spinner spinnerActivityDataType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_exec_activity_data_type);
        final Spinner spinnerRingtoneType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_exec_ringtone_type);
        final Spinner spinnerRingtoneName = (Spinner) mDialog.findViewById(R.id.edit_profile_action_exec_ringtone_name);
        final Spinner spinnerCompareType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_compare_type);
        final EditText et_comp_value1=(EditText)mDialog.findViewById(R.id.edit_profile_action_compare_value1);
        final EditText et_comp_value2=(EditText)mDialog.findViewById(R.id.edit_profile_action_compare_value2);
        final ListView lv_comp_data=(ListView)mDialog.findViewById(R.id.edit_profile_action_compare_value_listview);
        final Spinner spinnerCompareResult = (Spinner) mDialog.findViewById(R.id.edit_profile_action_compare_result);
        final Spinner spinnerCompareTarget = (Spinner) mDialog.findViewById(R.id.edit_profile_action_compare_target);
        final Spinner spinnerMessageType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_message_type);
        final EditText et_msg_text=(EditText)mDialog.findViewById(R.id.edit_profile_action_message_message);
        final CheckBox cb_vib_used=(CheckBox)mDialog.findViewById(R.id.edit_profile_action_message_vibration);
        final CheckBox cb_led_used=(CheckBox)mDialog.findViewById(R.id.edit_profile_action_message_led);
        final RadioButton rb_msg_blue=(RadioButton)mDialog.findViewById(R.id.edit_profile_action_message_led_blue);
        final RadioButton rb_msg_red=(RadioButton)mDialog.findViewById(R.id.edit_profile_action_message_led_red);
        final RadioButton rb_msg_green=(RadioButton)mDialog.findViewById(R.id.edit_profile_action_message_led_green);
        final Spinner spinnerTimeType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_time_type);
        final Spinner spinnerTimeTarget = (Spinner) mDialog.findViewById(R.id.edit_profile_action_time_target);
        final Spinner spinnerTaskType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_task_type);
        final Spinner spinnerTaskTarget = (Spinner) mDialog.findViewById(R.id.edit_profile_action_task_target);
        final Spinner spinnerWaitTarget = (Spinner) mDialog.findViewById(R.id.edit_profile_action_wait_target);
        final Spinner spinnerWaitTimeoutType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_wait_timeout);
        final Spinner spinnerWaitTimeoutValue = (Spinner) mDialog.findViewById(R.id.edit_profile_action_wait_timeout_value);
        final Spinner spinnerWaitTimeoutUnits = (Spinner) mDialog.findViewById(R.id.edit_profile_action_wait_timeout_units);
        final EditText et_bsh_script=(EditText) mDialog.findViewById(R.id.edit_profile_action_dlg_bsh_script_text);        
		final Spinner spinnerBshMethod=(Spinner) mDialog.findViewById(R.id.edit_profile_action_dlg_bsh_add_method);
		final Spinner spinnerCatMethod=(Spinner)mDialog.findViewById(R.id.edit_profile_action_dlg_bsh_cat_method);

        final EditText uri_data=(EditText)mDialog.findViewById(R.id.edit_profile_action_exec_activity_uri_data);
        final ListView lv_aed=(ListView)mDialog.findViewById(R.id.edit_profile_action_exec_activity_extra_data_listview);
        
        final EditText et_shell_cmd=(EditText)mDialog.findViewById(R.id.edit_profile_action_dlg_shell_cmd_text);

        sv.dlg_prof_name_et=dlg_prof_name_et.getText();
        sv.dlg_prof_name_et_spos=dlg_prof_name_et.getSelectionStart();
        sv.dlg_prof_name_et_epos=dlg_prof_name_et.getSelectionEnd();
        sv.cb_active=cb_active.isChecked();
        sv.cb_enable_env_parms=cb_enable_env_parms.isChecked();
        
        sv.tv_sound_filename=tv_sound_filename.getText();
        sv.cb_music_vol=cb_music_vol.isChecked();
        sv.sb_music_vol=sb_music_vol.getProgress();
        sv.cb_ringtone_vol=cb_ringtone_vol.isChecked();
        sv.sb_ringtone_vol=sb_ringtone_vol.getProgress();
        sv.spinnerActionType=spinnerActionType.getSelectedItemPosition();
        sv.spinnerActivityName=spinnerActivityName.getSelectedItemPosition();
        sv.spinnerActivityDataType=spinnerActivityDataType.getSelectedItemPosition();
        sv.spinnerRingtoneType=spinnerRingtoneType.getSelectedItemPosition();
        sv.spinnerRingtoneName=spinnerRingtoneName.getSelectedItemPosition();
        sv.spinnerCompareType=spinnerCompareType.getSelectedItemPosition();
		sv.et_comp_value1=et_comp_value1.getText();
		sv.et_comp_value2=et_comp_value2.getText();
        sv.lv_comp_data[0]=lv_comp_data.getFirstVisiblePosition();
        if (lv_comp_data.getChildAt(0)!=null) sv.lv_comp_data[1]=lv_comp_data.getChildAt(0).getTop();
        sv.spinnerCompareResult=spinnerCompareResult.getSelectedItemPosition();
        sv.spinnerCompareTarget=spinnerCompareTarget.getSelectedItemPosition();
        sv.spinnerMessageType=spinnerMessageType.getSelectedItemPosition();
        sv.et_msg_text=et_msg_text.getText();
        sv.cb_vib_used=cb_vib_used.isChecked();
        sv.cb_led_used=cb_led_used.isChecked();
        sv.rb_msg_blue=rb_msg_blue.isChecked();
        sv.rb_msg_red=rb_msg_red.isChecked();
        sv.rb_msg_green=rb_msg_green.isChecked();
        sv.spinnerTimeType=spinnerTimeType.getSelectedItemPosition();
        sv.spinnerTimeTarget=spinnerTimeTarget.getSelectedItemPosition();
        sv.spinnerTaskType=spinnerTaskType.getSelectedItemPosition();
        sv.spinnerTaskTarget=spinnerTaskTarget.getSelectedItemPosition();
        sv.spinnerWaitTarget=spinnerWaitTarget.getSelectedItemPosition();
        sv.spinnerWaitTimeoutType=spinnerWaitTimeoutType.getSelectedItemPosition();
        sv.spinnerWaitTimeoutValue=spinnerWaitTimeoutValue.getSelectedItemPosition();
        sv.spinnerWaitTimeoutUnits=spinnerWaitTimeoutUnits.getSelectedItemPosition();
        sv.et_bsh_script=et_bsh_script.getText();

		sv.spinnerBshMethod=spinnerBshMethod.getSelectedItemPosition();
		sv.spinnerCatMethod=spinnerCatMethod.getSelectedItemPosition();
        sv.uri_data=uri_data.getText();
		sv.lv_aed[0]=lv_aed.getFirstVisiblePosition();
		if (lv_aed.getChildAt(0)!=null) sv.lv_aed[1]=lv_aed.getChildAt(0).getTop();
		for (int i=0;i<mGlblParms.activityExtraDataEditListAdapter.getCount();i++) sv.aed_adapter_list.add(mGlblParms.activityExtraDataEditListAdapter.getItem(i));
		for (int i=0;i<mGlblParms.actionCompareDataAdapter.getCount();i++) sv.data_array_adapter_list.add(mGlblParms.actionCompareDataAdapter.getItem(i));

		sv.shell_cmd=et_shell_cmd.getText();
				
    	return sv;
    };

    private void restoreViewContents(final SavedViewContents sv) {
		final EditText dlg_prof_name_et = (EditText) mDialog.findViewById(R.id.edit_profile_action_profile_et_name);
		final CheckBox cb_active = (CheckBox) mDialog.findViewById(R.id.edit_profile_action_enabled);
        final CheckBox cb_enable_env_parms=(CheckBox)mDialog.findViewById(R.id.edit_profile_action_enable_env_parms);

		final TextView tv_sound_filename=(TextView)mDialog.findViewById(R.id.edit_profile_action_exec_sound_file_name);
		final CheckBox cb_music_vol=(CheckBox)mDialog.findViewById(R.id.edit_profile_action_profile_sound_use_volume);
		final SeekBar sb_music_vol=(SeekBar)mDialog.findViewById(R.id.edit_profile_action_profile_sound_volume);
		final CheckBox cb_ringtone_vol=(CheckBox)mDialog.findViewById(R.id.edit_profile_action_profile_ringtone_use_volume);
		final SeekBar sb_ringtone_vol=(SeekBar)mDialog.findViewById(R.id.edit_profile_action_profile_ringtone_volume);
		final Spinner spinnerActionType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_action_type);
        final Spinner spinnerActivityName = (Spinner) mDialog.findViewById(R.id.edit_profile_action_exec_activity_name);
        final Spinner spinnerActivityDataType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_exec_activity_data_type);
        final Spinner spinnerRingtoneType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_exec_ringtone_type);
        final Spinner spinnerRingtoneName = (Spinner) mDialog.findViewById(R.id.edit_profile_action_exec_ringtone_name);
        final Spinner spinnerCompareType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_compare_type);
        final EditText et_comp_value1=(EditText)mDialog.findViewById(R.id.edit_profile_action_compare_value1);
        final EditText et_comp_value2=(EditText)mDialog.findViewById(R.id.edit_profile_action_compare_value2);
        final ListView lv_comp_data=(ListView)mDialog.findViewById(R.id.edit_profile_action_compare_value_listview);
        final Spinner spinnerCompareResult = (Spinner) mDialog.findViewById(R.id.edit_profile_action_compare_result);
        final Spinner spinnerCompareTarget = (Spinner) mDialog.findViewById(R.id.edit_profile_action_compare_target);
        final Spinner spinnerMessageType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_message_type);
        final EditText et_msg_text=(EditText)mDialog.findViewById(R.id.edit_profile_action_message_message);
        final CheckBox cb_vib_used=(CheckBox)mDialog.findViewById(R.id.edit_profile_action_message_vibration);
        final CheckBox cb_led_used=(CheckBox)mDialog.findViewById(R.id.edit_profile_action_message_led);
        final RadioButton rb_msg_blue=(RadioButton)mDialog.findViewById(R.id.edit_profile_action_message_led_blue);
        final RadioButton rb_msg_red=(RadioButton)mDialog.findViewById(R.id.edit_profile_action_message_led_red);
        final RadioButton rb_msg_green=(RadioButton)mDialog.findViewById(R.id.edit_profile_action_message_led_green);
        final Spinner spinnerTimeType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_time_type);
        final Spinner spinnerTimeTarget = (Spinner) mDialog.findViewById(R.id.edit_profile_action_time_target);
        final Spinner spinnerTaskType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_task_type);
        final Spinner spinnerTaskTarget = (Spinner) mDialog.findViewById(R.id.edit_profile_action_task_target);
        final Spinner spinnerWaitTarget = (Spinner) mDialog.findViewById(R.id.edit_profile_action_wait_target);
        final Spinner spinnerWaitTimeoutType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_wait_timeout);
        final Spinner spinnerWaitTimeoutValue = (Spinner) mDialog.findViewById(R.id.edit_profile_action_wait_timeout_value);
        final Spinner spinnerWaitTimeoutUnits = (Spinner) mDialog.findViewById(R.id.edit_profile_action_wait_timeout_units);
        final EditText et_bsh_script=(EditText) mDialog.findViewById(R.id.edit_profile_action_dlg_bsh_script_text);        
		final Spinner spinnerBshMethod=(Spinner) mDialog.findViewById(R.id.edit_profile_action_dlg_bsh_add_method);
		final Spinner spinnerCatMethod=(Spinner)mDialog.findViewById(R.id.edit_profile_action_dlg_bsh_cat_method);

        final EditText uri_data=(EditText)mDialog.findViewById(R.id.edit_profile_action_exec_activity_uri_data);
        final ListView lv_aed=(ListView)mDialog.findViewById(R.id.edit_profile_action_exec_activity_extra_data_listview);

        final EditText et_shell_cmd=(EditText)mDialog.findViewById(R.id.edit_profile_action_dlg_shell_cmd_text);
        
		dlg_prof_name_et.setText(sv.dlg_prof_name_et);
		dlg_prof_name_et.setSelection(sv.dlg_prof_name_et_spos,sv.dlg_prof_name_et_epos);
		cb_active.setChecked(sv.cb_active);
        tv_sound_filename.setText(sv.tv_sound_filename);
        cb_music_vol.setChecked(sv.cb_music_vol);
        sb_music_vol.setProgress(sv.sb_music_vol);
        cb_ringtone_vol.setChecked(sv.cb_ringtone_vol);
        sb_ringtone_vol.setProgress(sv.sb_ringtone_vol);
		et_comp_value1.setText(sv.et_comp_value1);
		et_comp_value2.setText(sv.et_comp_value2);
        lv_comp_data.setSelectionFromTop(sv.lv_comp_data[0],sv.lv_comp_data[1]);
        et_msg_text.setText(sv.et_msg_text);
        cb_vib_used.setChecked(sv.cb_vib_used);
        cb_led_used.setChecked(sv.cb_led_used);
        if (sv.rb_msg_blue) rb_msg_blue.setChecked(sv.rb_msg_blue);
        if (sv.rb_msg_red) rb_msg_red.setChecked(sv.rb_msg_red);
        if (sv.rb_msg_green) rb_msg_green.setChecked(sv.rb_msg_green);
        et_bsh_script.setText(sv.et_bsh_script);
        uri_data.setText(sv.uri_data);
		lv_aed.setSelectionFromTop(sv.lv_aed[0],sv.lv_aed[1]);
		cb_enable_env_parms.setChecked(sv.cb_enable_env_parms);
		
		for (int i=0;i<mGlblParms.activityExtraDataEditListAdapter.getCount();i++) 
			mGlblParms.activityExtraDataEditListAdapter.remove(0);
		for (int i=0;i<sv.aed_adapter_list.size();i++) 
			mGlblParms.activityExtraDataEditListAdapter.add(sv.aed_adapter_list.get(i));
		mGlblParms.activityExtraDataEditListAdapter.notifyDataSetChanged();
		
		for (int i=0;i<mGlblParms.actionCompareDataAdapter.getCount();i++) 
			mGlblParms.actionCompareDataAdapter.remove(0); 
		for (int i=0;i<sv.data_array_adapter_list.size();i++) 
			mGlblParms.actionCompareDataAdapter.add(sv.data_array_adapter_list.get(i));
		mGlblParms.actionCompareDataAdapter.notifyDataSetChanged();

        spinnerActionType.setSelection(sv.spinnerActionType);
        spinnerActivityName.setSelection(sv.spinnerActivityName);
        spinnerActivityDataType.setSelection(sv.spinnerActivityDataType);
        spinnerRingtoneType.setSelection(sv.spinnerRingtoneType);
        spinnerRingtoneName.setSelection(sv.spinnerRingtoneName);
        spinnerCompareType.setSelection(sv.spinnerCompareType);
        spinnerCompareResult.setSelection(sv.spinnerCompareResult);
        spinnerCompareTarget.setSelection(sv.spinnerCompareTarget);
        spinnerMessageType.setSelection(sv.spinnerMessageType);
        spinnerTimeType.setSelection(sv.spinnerTimeType);
        spinnerTimeTarget.setSelection(sv.spinnerTimeTarget);
        spinnerTaskType.setSelection(sv.spinnerTaskType);
        spinnerTaskTarget.setSelection(sv.spinnerTaskTarget);
        spinnerWaitTarget.setSelection(sv.spinnerWaitTarget);
        spinnerWaitTimeoutType.setSelection(sv.spinnerWaitTimeoutType);
        spinnerWaitTimeoutValue.setSelection(sv.spinnerWaitTimeoutValue);
        spinnerWaitTimeoutUnits.setSelection(sv.spinnerWaitTimeoutUnits);
		spinnerBshMethod.setSelection(sv.spinnerBshMethod);
		spinnerCatMethod.setSelection(sv.spinnerCatMethod);

		et_shell_cmd.setText(sv.shell_cmd);
		
//    	Handler hndl1=new Handler();
//    	hndl1.postDelayed(new Runnable(){
//			@Override
//			public void run() {
//
//				Handler hndl2=new Handler();
//		    	hndl2.postDelayed(new Runnable(){
//					@Override
//					public void run() {
//					}
//		    	},50);
//			}
//    	},50);
    }
    
    public void reInitViewWidget() {
    	if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"reInitViewWidget");
    	if (!mTerminateRequired) {
    		Handler hndl=new Handler();
    		hndl.post(new Runnable(){
				@Override
				public void run() {
			    	SavedViewContents sv=null;
			    	if (!mOpType.equals("BROWSE")) sv=saveViewContents();
			    	
			    	initViewWidget();
			
			    	if (!mOpType.equals("BROWSE")) restoreViewContents(sv);
			    	
			    	CommonDialog.setDlgBoxSizeLimit(mDialog,true);
				}
    		});
    	}
    };
    
    public void initViewWidget() {
    	if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"initViewWidget");
		if (mOpType.equals("EDIT")) editProfile();
		else if (mOpType.equals("ADD")) addProfile();
		else if (mOpType.equals("BROWSE")) browseProfile();
    };

	private String mCurrentGroup;
	private String mOpType="";
	private ProfileListItem mCurrentProfileListItem;
	private NotifyEvent mNotifyCompletion=null;
    public void showDialog(FragmentManager fm, Fragment frag,
    		final String op_type,
    		final String c_grp, 
			final ProfileListItem tpli,
			NotifyEvent nc) {
    	if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"showDialog");
    	mTerminateRequired=false;
    	mOpType=op_type;
    	mCurrentGroup=c_grp;
    	mCurrentProfileListItem=tpli;
    	mNotifyCompletion=nc;
	    FragmentTransaction ft = fm.beginTransaction();
	    ft.add(frag,null);
	    ft.commitAllowingStateLoss();
//	    show(fm,APPLICATION_TAG);
    };
    
    final private void addProfile() {
		mDialog.setContentView(R.layout.edit_profile_action_dlg);
		
		final TextView dlg_title = (TextView) mDialog.findViewById(R.id.edit_profile_action_title);
		dlg_title.setText(mGlblParms.context.getString(R.string.msgs_edit_profile_hdr_add_action));
		final EditText dlg_prof_name_et=(EditText)mDialog.findViewById(R.id.edit_profile_action_profile_et_name);
		
        final ArrayList<DataArrayEditListItem> comp_data_list=new ArrayList<DataArrayEditListItem>();
        final ArrayList<ActivityExtraDataItem>aed_edit_list=new ArrayList<ActivityExtraDataItem>();

//		final CheckBox cb_active=(CheckBox)mDialog.findViewById(R.id.edit_profile_action_enabled);
		final TextView tv_sound_filename=(TextView)mDialog.findViewById(R.id.edit_profile_action_exec_sound_file_name);
		final CheckBox cb_music_vol=(CheckBox)mDialog.findViewById(R.id.edit_profile_action_profile_sound_use_volume);
		final SeekBar sb_music_vol=(SeekBar)mDialog.findViewById(R.id.edit_profile_action_profile_sound_volume);
		final Button playBtnMusic = (Button)mDialog.findViewById(R.id.edit_profile_action_profile_sound_play_back);
		final Button playBtnRingtone = (Button)mDialog.findViewById(R.id.edit_profile_action_profile_ringtone_play_back);
		final CheckBox cb_ringtone_vol=(CheckBox)mDialog.findViewById(R.id.edit_profile_action_profile_ringtone_use_volume);
		final SeekBar sb_ringtone_vol=(SeekBar)mDialog.findViewById(R.id.edit_profile_action_profile_ringtone_volume);
		final Spinner spinnerActionType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_action_type);
        final Spinner spinnerActivityName = (Spinner) mDialog.findViewById(R.id.edit_profile_action_exec_activity_name);
        final Spinner spinnerActivityDataType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_exec_activity_data_type);
        final Spinner spinnerRingtoneType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_exec_ringtone_type);
        final Spinner spinnerRingtoneName = (Spinner) mDialog.findViewById(R.id.edit_profile_action_exec_ringtone_name);
        final Spinner spinnerCompareType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_compare_type);
//        final EditText et_comp_value1=(EditText)mDialog.findViewById(R.id.edit_profile_action_compare_value1);
//        final EditText et_comp_value2=(EditText)mDialog.findViewById(R.id.edit_profile_action_compare_value2);
//        final ListView lv_comp_data=(ListView)mDialog.findViewById(R.id.edit_profile_action_compare_value_listview);
//        final Spinner spinnerCompareResult = (Spinner) mDialog.findViewById(R.id.edit_profile_action_compare_result);
        final Spinner spinnerCompareTarget = (Spinner) mDialog.findViewById(R.id.edit_profile_action_compare_target);
        final Spinner spinnerMessageType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_message_type);
//        final EditText et_msg_text=(EditText)mDialog.findViewById(R.id.edit_profile_action_message_message);
//        final CheckBox cb_vib_used=(CheckBox)mDialog.findViewById(R.id.edit_profile_action_message_vibration);
//        final CheckBox cb_led_used=(CheckBox)mDialog.findViewById(R.id.edit_profile_action_message_led);
//        final RadioButton rb_msg_blue=(RadioButton)mDialog.findViewById(R.id.edit_profile_action_message_led_blue);
//        final RadioButton rb_msg_red=(RadioButton)mDialog.findViewById(R.id.edit_profile_action_message_led_red);
//        final RadioButton rb_msg_green=(RadioButton)mDialog.findViewById(R.id.edit_profile_action_message_led_green);
        final Spinner spinnerTimeType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_time_type);
        final Spinner spinnerTimeTarget = (Spinner) mDialog.findViewById(R.id.edit_profile_action_time_target);
        final Spinner spinnerTaskType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_task_type);
        final Spinner spinnerTaskTarget = (Spinner) mDialog.findViewById(R.id.edit_profile_action_task_target);
//        final Spinner spinnerWaitTarget = (Spinner) mDialog.findViewById(R.id.edit_profile_action_wait_target);
//        final Spinner spinnerWaitTimeoutType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_wait_timeout);
//        final Spinner spinnerWaitTimeoutValue = (Spinner) mDialog.findViewById(R.id.edit_profile_action_wait_timeout_value);
//        final Spinner spinnerWaitTimeoutUnits = (Spinner) mDialog.findViewById(R.id.edit_profile_action_wait_timeout_units);
        
        final CustomSpinnerAdapter adapterActionType = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterActivityName = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterActivityDataType = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterRingtoneType = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterRingtoneName = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterCompareType = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        mGlblParms.actionCompareDataAdapter=new AdapterDataArrayEditList(mGlblParms.context,R.layout.data_array_edit_list_item,comp_data_list);
        final CustomSpinnerAdapter adapterCompareResult = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterCompareTarget = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterMessageType = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterTimeType = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterTimeTarget = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterTaskType = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterTaskTarget = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterWaitTarget = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterWaitTimeoutType = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterWaitTimeoutValue = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterWaitTimeoutUnits = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);

        initProfileActionWidget(mGlblParms,mDialog, null, comp_data_list, aed_edit_list,			
    			mCurrentGroup, mGlblParms.profileAdapter, adapterActionType, adapterActivityName,
    			adapterActivityDataType, adapterRingtoneType, adapterRingtoneName,
    			adapterCompareType, adapterCompareResult,
    			adapterCompareTarget, adapterMessageType, adapterTimeType,
    			adapterTimeTarget, adapterTaskType, adapterTaskTarget,
    			adapterWaitTarget, adapterWaitTimeoutType, adapterWaitTimeoutValue,
    			adapterWaitTimeoutUnits);

        if (mGlblParms.immTaskTestEnvParms==null) ProfileMaintenance.loadEnvparmsFromService(mGlblParms);
        
        setProfileActionActivityListener(mGlblParms,mDialog,mGlblParms.profileAdapter,mCurrentGroup, aed_edit_list);
        ProfileMaintenance.setProfileActionTestExecListener(mGlblParms,mDialog,mGlblParms.profileAdapter,mCurrentGroup,aed_edit_list);
        setProfileActionCompareListener(mGlblParms,mDialog,mGlblParms.profileAdapter,mCurrentGroup,adapterCompareType,"");
        setProfileActionMessageListener(mGlblParms,mDialog,mGlblParms.profileAdapter,mCurrentGroup);
        setProfileActionTimeListener(mGlblParms,mDialog,mGlblParms.profileAdapter,mCurrentGroup);
        setProfileActionTaskListener(mGlblParms,mDialog,mGlblParms.profileAdapter,mCurrentGroup, adapterTaskType, adapterTaskTarget,"");
        setProfileActionWaitListener(mGlblParms,mDialog);      
        setProfileActionBshListener(mGlblParms,mDialog,mGlblParms.profileAdapter,mCurrentGroup,null);
    	
        setViewVisibilityByActionType(mGlblParms,mDialog, PROFILE_ACTION_TYPE_ACTIVITY,
        		PROFILE_ACTION_TYPE_ACTIVITY_DATA_TYPE_NONE);
        setActionTypeSelectionListner(mGlblParms,mDialog,spinnerActionType,adapterActionType,
        		spinnerActivityDataType,adapterActivityDataType);
        ProfileMaintenance.setActionRingtoneTypeSelectionListener(mGlblParms,mDialog,spinnerRingtoneType, spinnerRingtoneName, adapterRingtoneName);

		// Music Listingボタンの指定
		Button btnListSound = (Button) mDialog.findViewById(R.id.edit_profile_action_list_sound);
		ProfileMaintenance.setActionListSoundBtnListener(mGlblParms,mDialog,tv_sound_filename,cb_music_vol,sb_music_vol,
				btnListSound,playBtnMusic);
		ProfileMaintenance.setMusicPlayBackBtnListener(mGlblParms,mDialog, tv_sound_filename,cb_music_vol,sb_music_vol, 
				playBtnMusic);
		// Ringtone ボタンの指定
		ProfileMaintenance.setRingtonePlayBackBtnListener(mGlblParms,mDialog, spinnerRingtoneType, 
				spinnerRingtoneName,cb_ringtone_vol,sb_ringtone_vol,
				playBtnRingtone);

		// CANCELボタンの指定
		final Button btnCancel = (Button) mDialog.findViewById(R.id.edit_profile_action_cancel_btn);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			final public void onClick(View v) {
				ProfileMaintenance.stopMusicPlayBack(mGlblParms);
				ProfileMaintenance.stopRingtonePlayBack(mGlblParms);
				mFragment.dismiss();
			}
		});
		// OKボタンの指定
		Button btnOK = (Button) mDialog.findViewById(R.id.edit_profile_action_ok_btn);
		btnOK.setOnClickListener(new View.OnClickListener() {
			final public void onClick(View v) {
				dlg_prof_name_et.selectAll();
				String audit_msg=ProfileMaintenance.auditProfileName(mGlblParms,mGlblParms.profileAdapter,mCurrentGroup,PROFILE_TYPE_ACTION,
						dlg_prof_name_et.getText().toString());
				if (!audit_msg.equals("")) {
					mGlblParms.commonDlg.showCommonDialog(false, "E",  
							audit_msg, "", null);
					return;
				} 
				audit_msg=ProfileMaintenance.auditActionProfile(mGlblParms,mDialog, mCurrentGroup,mGlblParms.profileAdapter, 
						spinnerActionType, spinnerActivityName,spinnerActivityDataType,
						spinnerCompareTarget, spinnerCompareType,spinnerMessageType,
						spinnerTimeType, spinnerTimeTarget,
						spinnerTaskType, spinnerTaskTarget,
						aed_edit_list
						);
				if (!audit_msg.equals("")) {
					mGlblParms.commonDlg.showCommonDialog(false, "E",  
							audit_msg, "", null);
					return;
				} 
				ProfileMaintenance.stopMusicPlayBack(mGlblParms);
				ProfileMaintenance.stopRingtonePlayBack(mGlblParms);
				ProfileListItem ntpli=createProfileListItemFromScreenData(mGlblParms,mDialog,mGlblParms.profileAdapter,mCurrentGroup);
				ntpli.setActionActivityExtraData(aed_edit_list);
				ntpli.setProfileGroupShowed(true);
				mFragment.dismiss();
				
				ProfileUtilities.removeDummyProfile(mGlblParms.profileAdapter,mCurrentGroup);
				
				mGlblParms.profileAdapter.addProfItem(ntpli);
				mGlblParms.profileAdapter.sort();
				mGlblParms.profileAdapter.updateShowList();
				mGlblParms.profileAdapter.notifyDataSetChanged();
				ProfileMaintenance.putProfileListToService(mGlblParms,mGlblParms.profileAdapter,
						ProfileUtilities.isProfileGroupActive(mGlblParms.util,mGlblParms.profileAdapter,mCurrentGroup));
			}
		});

    };

    final private void browseProfile() {
		mDialog.setContentView(R.layout.edit_profile_action_dlg);
		
		final TextView dlg_title = (TextView) mDialog.findViewById(R.id.edit_profile_action_title);
		final TextView dlg_title_sub = (TextView) mDialog.findViewById(R.id.edit_profile_action_title_sub);
		final EditText dlg_prof_name_et=(EditText)mDialog.findViewById(R.id.edit_profile_action_profile_et_name);
		dlg_prof_name_et.setVisibility(EditText.GONE);
		dlg_title.setText(mGlblParms.context.getString(R.string.msgs_edit_profile_hdr_browse_action));
		dlg_title_sub.setText("("+mCurrentProfileListItem.getProfileName()+")");

        final ArrayList<DataArrayEditListItem> comp_data_list=new ArrayList<DataArrayEditListItem>();
        final ArrayList<ActivityExtraDataItem>aed_edit_list=new ArrayList<ActivityExtraDataItem>();

		final CheckBox cb_active=(CheckBox)mDialog.findViewById(R.id.edit_profile_action_enabled);
//		final TextView tv_sound_filename=(TextView)mDialog.findViewById(R.id.edit_profile_action_exec_sound_file_name);
		final CheckBox cb_music_vol=(CheckBox)mDialog.findViewById(R.id.edit_profile_action_profile_sound_use_volume);
		final SeekBar sb_music_vol=(SeekBar)mDialog.findViewById(R.id.edit_profile_action_profile_sound_volume);
		final Button playBtnMusic = (Button)mDialog.findViewById(R.id.edit_profile_action_profile_sound_play_back);
		final Button playBtnRingtone = (Button)mDialog.findViewById(R.id.edit_profile_action_profile_ringtone_play_back);
		final CheckBox cb_ringtone_vol=(CheckBox)mDialog.findViewById(R.id.edit_profile_action_profile_ringtone_use_volume);
		final SeekBar sb_ringtone_vol=(SeekBar)mDialog.findViewById(R.id.edit_profile_action_profile_ringtone_volume);
		final Spinner spinnerActionType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_action_type);
        final Spinner spinnerActivityName = (Spinner) mDialog.findViewById(R.id.edit_profile_action_exec_activity_name);
        final Spinner spinnerActivityDataType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_exec_activity_data_type);
        final Spinner spinnerRingtoneType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_exec_ringtone_type);
        final Spinner spinnerRingtoneName = (Spinner) mDialog.findViewById(R.id.edit_profile_action_exec_ringtone_name);
//        final Spinner spinnerCompareType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_compare_type);
//        final EditText et_comp_value1=(EditText)mDialog.findViewById(R.id.edit_profile_action_compare_value1);
//        final EditText et_comp_value2=(EditText)mDialog.findViewById(R.id.edit_profile_action_compare_value2);
//        final ListView lv_comp_data=(ListView)mDialog.findViewById(R.id.edit_profile_action_compare_value_listview);
//        final Spinner spinnerCompareResult = (Spinner) mDialog.findViewById(R.id.edit_profile_action_compare_result);
//        final Spinner spinnerCompareTarget = (Spinner) mDialog.findViewById(R.id.edit_profile_action_compare_target);
//        final Spinner spinnerMessageType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_message_type);
//        final EditText et_msg_text=(EditText)mDialog.findViewById(R.id.edit_profile_action_message_message);
//        final CheckBox cb_vib_used=(CheckBox)mDialog.findViewById(R.id.edit_profile_action_message_vibration);
//        final CheckBox cb_led_used=(CheckBox)mDialog.findViewById(R.id.edit_profile_action_message_led);
//        final RadioButton rb_msg_blue=(RadioButton)mDialog.findViewById(R.id.edit_profile_action_message_led_blue);
//        final RadioButton rb_msg_red=(RadioButton)mDialog.findViewById(R.id.edit_profile_action_message_led_red);
//        final RadioButton rb_msg_green=(RadioButton)mDialog.findViewById(R.id.edit_profile_action_message_led_green);
//        final Spinner spinnerTimeType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_time_type);
//        final Spinner spinnerTimeTarget = (Spinner) mDialog.findViewById(R.id.edit_profile_action_time_target);
//        final Spinner spinnerTaskType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_task_type);
//        final Spinner spinnerTaskTarget = (Spinner) mDialog.findViewById(R.id.edit_profile_action_task_target);
//        final Spinner spinnerWaitTarget = (Spinner) mDialog.findViewById(R.id.edit_profile_action_wait_target);
//        final Spinner spinnerWaitTimeoutType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_wait_timeout);
//        final Spinner spinnerWaitTimeoutValue = (Spinner) mDialog.findViewById(R.id.edit_profile_action_wait_timeout_value);
//        final Spinner spinnerWaitTimeoutUnits = (Spinner) mDialog.findViewById(R.id.edit_profile_action_wait_timeout_units);
        final Spinner spinnerCatMethod = (Spinner) mDialog.findViewById(R.id.edit_profile_action_dlg_bsh_cat_method);
        final Spinner spinnerAddMethod = (Spinner) mDialog.findViewById(R.id.edit_profile_action_dlg_bsh_add_method);
        final EditText et_bsh_script=(EditText) mDialog.findViewById(R.id.edit_profile_action_dlg_bsh_script_text);
        
        final CustomSpinnerAdapter adapterActionType = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterActivityName = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterActivityDataType = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterRingtoneType = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterRingtoneName = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterCompareType = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        mGlblParms.actionCompareDataAdapter=new AdapterDataArrayEditList(mGlblParms.context,R.layout.data_array_edit_list_item,comp_data_list);
        final CustomSpinnerAdapter adapterCompareResult = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterCompareTarget = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterMessageType = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterTimeType = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterTimeTarget = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterTaskType = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterTaskTarget = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterWaitTarget = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterWaitTimeoutType = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterWaitTimeoutValue = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterWaitTimeoutUnits = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);

        initProfileActionWidget(mGlblParms,mDialog, mCurrentProfileListItem, comp_data_list, aed_edit_list,			
    			mCurrentGroup, mGlblParms.profileAdapter, adapterActionType, adapterActivityName,
    			adapterActivityDataType, adapterRingtoneType, adapterRingtoneName,
    			adapterCompareType, adapterCompareResult,
    			adapterCompareTarget, adapterMessageType, adapterTimeType,
    			adapterTimeTarget, adapterTaskType, adapterTaskTarget,
    			adapterWaitTarget, adapterWaitTimeoutType, adapterWaitTimeoutValue,
    			adapterWaitTimeoutUnits);

		cb_active.setClickable(false);
		cb_music_vol.setClickable(false);
		sb_music_vol.setEnabled(false);
		playBtnMusic.setVisibility(Button.GONE);
		playBtnRingtone.setVisibility(Button.GONE);
		cb_ringtone_vol.setClickable(false);
		sb_ringtone_vol.setClickable(false);
        spinnerActionType.setClickable(false);
        spinnerActivityName.setClickable(false);
        spinnerActivityDataType.setClickable(false);
        spinnerRingtoneType.setClickable(false);
        spinnerRingtoneName.setClickable(false);

        setProfileActionActivityListener(mGlblParms,mDialog,mGlblParms.profileAdapter,mCurrentGroup,aed_edit_list);
        setProfileActionCompareListener(mGlblParms,mDialog,mGlblParms.profileAdapter,mCurrentGroup,adapterCompareType,mCurrentProfileListItem.getActionCompareType());
        setProfileActionMessageListener(mGlblParms,mDialog,mGlblParms.profileAdapter,mCurrentGroup);
        setProfileActionTimeListener(mGlblParms,mDialog,mGlblParms.profileAdapter,mCurrentGroup);
        setProfileActionTaskListener(mGlblParms,mDialog,mGlblParms.profileAdapter,mCurrentGroup, adapterTaskType, adapterTaskTarget,mCurrentProfileListItem.getActionTaskTarget()); 
    	ProfileMaintenance.setProfileActionTestExecListener(mGlblParms,mDialog,mGlblParms.profileAdapter,mCurrentGroup,aed_edit_list);
    	setProfileActionWaitListener(mGlblParms,mDialog);
    	setProfileActionBshListener(mGlblParms,mDialog,mGlblParms.profileAdapter,mCurrentGroup,mCurrentProfileListItem);
        et_bsh_script.setEnabled(false);
        et_bsh_script.setTextColor(Color.LTGRAY);
        spinnerCatMethod.setVisibility(Spinner.GONE);
        spinnerAddMethod.setVisibility(Spinner.GONE);
        
        setViewVisibilityByActionType(mGlblParms,mDialog, mCurrentProfileListItem.getActionType(), mCurrentProfileListItem.getActionActivityDataType());
        setActionTypeSelectionListner(mGlblParms,mDialog,spinnerActionType,adapterActionType,
        		spinnerActivityDataType,adapterActivityDataType);
        ProfileMaintenance.setActionRingtoneTypeSelectionListener(mGlblParms,
        	mDialog,spinnerRingtoneType, spinnerRingtoneName, adapterRingtoneName);
        
        // Music Listingボタンの指定
		Button btnListSound = (Button) mDialog.findViewById(R.id.edit_profile_action_list_sound);
		btnListSound.setVisibility(Button.GONE);
		// CANCELボタンの指定
		final Button btnCancel = (Button) mDialog.findViewById(R.id.edit_profile_action_cancel_btn);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			final public void onClick(View v) {
				ProfileMaintenance.stopMusicPlayBack(mGlblParms);
				ProfileMaintenance.stopRingtonePlayBack(mGlblParms);
				mFragment.dismiss();
			}
		});
		Button btnOk = (Button) mDialog.findViewById(R.id.edit_profile_action_ok_btn);
		btnOk.setVisibility(Button.GONE);

    };
    
    final private void editProfile() {
		mDialog.setContentView(R.layout.edit_profile_action_dlg);
		
		final TextView dlg_title = (TextView) mDialog.findViewById(R.id.edit_profile_action_title);
		final TextView dlg_title_sub = (TextView) mDialog.findViewById(R.id.edit_profile_action_title_sub);
		final EditText dlg_prof_name_et=(EditText)mDialog.findViewById(R.id.edit_profile_action_profile_et_name);
		dlg_prof_name_et.setVisibility(EditText.GONE);
		dlg_prof_name_et.setText(mCurrentProfileListItem.getProfileName());
		dlg_title.setText(mGlblParms.context.getString(R.string.msgs_edit_profile_hdr_edit_action));
		dlg_title_sub.setText("("+mCurrentProfileListItem.getProfileName()+")");

		
        final ArrayList<DataArrayEditListItem> comp_data_list=new ArrayList<DataArrayEditListItem>();
        final ArrayList<ActivityExtraDataItem>aed_edit_list=new ArrayList<ActivityExtraDataItem>();

//		final CheckBox cb_active=(CheckBox)mDialog.findViewById(R.id.edit_profile_action_enabled);
		final TextView tv_sound_filename=(TextView)mDialog.findViewById(R.id.edit_profile_action_exec_sound_file_name);
		final CheckBox cb_music_vol=(CheckBox)mDialog.findViewById(R.id.edit_profile_action_profile_sound_use_volume);
		final SeekBar sb_music_vol=(SeekBar)mDialog.findViewById(R.id.edit_profile_action_profile_sound_volume);
		final Button playBtnMusic = (Button)mDialog.findViewById(R.id.edit_profile_action_profile_sound_play_back);
		final Button playBtnRingtone = (Button)mDialog.findViewById(R.id.edit_profile_action_profile_ringtone_play_back);
		final CheckBox cb_ringtone_vol=(CheckBox)mDialog.findViewById(R.id.edit_profile_action_profile_ringtone_use_volume);
		final SeekBar sb_ringtone_vol=(SeekBar)mDialog.findViewById(R.id.edit_profile_action_profile_ringtone_volume);
		final Spinner spinnerActionType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_action_type);
        final Spinner spinnerActivityName = (Spinner) mDialog.findViewById(R.id.edit_profile_action_exec_activity_name);
        final Spinner spinnerActivityDataType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_exec_activity_data_type);
        final Spinner spinnerRingtoneType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_exec_ringtone_type);
        final Spinner spinnerRingtoneName = (Spinner) mDialog.findViewById(R.id.edit_profile_action_exec_ringtone_name);
        final Spinner spinnerCompareType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_compare_type);
//        final EditText et_comp_value1=(EditText)mDialog.findViewById(R.id.edit_profile_action_compare_value1);
//        final EditText et_comp_value2=(EditText)mDialog.findViewById(R.id.edit_profile_action_compare_value2);
//        final ListView lv_comp_data=(ListView)mDialog.findViewById(R.id.edit_profile_action_compare_value_listview);
//        final Spinner spinnerCompareResult = (Spinner) mDialog.findViewById(R.id.edit_profile_action_compare_result);
        final Spinner spinnerCompareTarget = (Spinner) mDialog.findViewById(R.id.edit_profile_action_compare_target);
        final Spinner spinnerMessageType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_message_type);
//        final EditText et_msg_text=(EditText)mDialog.findViewById(R.id.edit_profile_action_message_message);
//        final CheckBox cb_vib_used=(CheckBox)mDialog.findViewById(R.id.edit_profile_action_message_vibration);
//        final CheckBox cb_led_used=(CheckBox)mDialog.findViewById(R.id.edit_profile_action_message_led);
//        final RadioButton rb_msg_blue=(RadioButton)mDialog.findViewById(R.id.edit_profile_action_message_led_blue);
//        final RadioButton rb_msg_red=(RadioButton)mDialog.findViewById(R.id.edit_profile_action_message_led_red);
//        final RadioButton rb_msg_green=(RadioButton)mDialog.findViewById(R.id.edit_profile_action_message_led_green);
        final Spinner spinnerTimeType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_time_type);
        final Spinner spinnerTimeTarget = (Spinner) mDialog.findViewById(R.id.edit_profile_action_time_target);
        final Spinner spinnerTaskType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_task_type);
        final Spinner spinnerTaskTarget = (Spinner) mDialog.findViewById(R.id.edit_profile_action_task_target);
//        final Spinner spinnerWaitTarget = (Spinner) mDialog.findViewById(R.id.edit_profile_action_wait_target);
//        final Spinner spinnerWaitTimeoutType = (Spinner) mDialog.findViewById(R.id.edit_profile_action_wait_timeout);
//        final Spinner spinnerWaitTimeoutValue = (Spinner) mDialog.findViewById(R.id.edit_profile_action_wait_timeout_value);
//        final Spinner spinnerWaitTimeoutUnits = (Spinner) mDialog.findViewById(R.id.edit_profile_action_wait_timeout_units);
        
        final CustomSpinnerAdapter adapterActionType = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterActivityName = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterActivityDataType = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterRingtoneType = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterRingtoneName = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterCompareType = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        mGlblParms.actionCompareDataAdapter=new AdapterDataArrayEditList(mGlblParms.context,R.layout.data_array_edit_list_item,comp_data_list);
        final CustomSpinnerAdapter adapterCompareResult = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterCompareTarget = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterMessageType = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterTimeType = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterTimeTarget = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterTaskType = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterTaskTarget = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterWaitTarget = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterWaitTimeoutType = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterWaitTimeoutValue = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        final CustomSpinnerAdapter adapterWaitTimeoutUnits = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);

        if (mGlblParms.immTaskTestEnvParms==null) ProfileMaintenance.loadEnvparmsFromService(mGlblParms);
        
        initProfileActionWidget(mGlblParms,mDialog, mCurrentProfileListItem, comp_data_list, aed_edit_list,			
    			mCurrentGroup, mGlblParms.profileAdapter, adapterActionType, adapterActivityName,
    			adapterActivityDataType, adapterRingtoneType, adapterRingtoneName,
    			adapterCompareType, adapterCompareResult,
    			adapterCompareTarget, adapterMessageType, adapterTimeType,
    			adapterTimeTarget, adapterTaskType, adapterTaskTarget,
    			adapterWaitTarget, adapterWaitTimeoutType, adapterWaitTimeoutValue,
    			adapterWaitTimeoutUnits);

        setProfileActionActivityListener(mGlblParms,mDialog,mGlblParms.profileAdapter,mCurrentGroup,aed_edit_list);
        setProfileActionCompareListener(mGlblParms,mDialog,mGlblParms.profileAdapter,mCurrentGroup,adapterCompareType,mCurrentProfileListItem.getActionCompareType());
        setProfileActionMessageListener(mGlblParms,mDialog,mGlblParms.profileAdapter,mCurrentGroup);
        setProfileActionTimeListener(mGlblParms,mDialog,mGlblParms.profileAdapter,mCurrentGroup);
        setProfileActionTaskListener(mGlblParms,mDialog,mGlblParms.profileAdapter,mCurrentGroup, adapterTaskType, adapterTaskTarget,mCurrentProfileListItem.getActionTaskTarget()); 
        ProfileMaintenance.setProfileActionTestExecListener(mGlblParms,mDialog,mGlblParms.profileAdapter,mCurrentGroup,aed_edit_list);
        setProfileActionWaitListener(mGlblParms,mDialog);
        setProfileActionBshListener(mGlblParms,mDialog,mGlblParms.profileAdapter,mCurrentGroup,mCurrentProfileListItem);

        setViewVisibilityByActionType(mGlblParms,mDialog, mCurrentProfileListItem.getActionType(),
        		mCurrentProfileListItem.getActionActivityDataType());
        setActionTypeSelectionListner(mGlblParms,mDialog,spinnerActionType,adapterActionType,
        		spinnerActivityDataType,adapterActivityDataType);
        ProfileMaintenance.setActionRingtoneTypeSelectionListener(mGlblParms,
        	mDialog,spinnerRingtoneType, spinnerRingtoneName, adapterRingtoneName);
        
        final EditText uri_data=(EditText)mDialog.findViewById(R.id.edit_profile_action_exec_activity_uri_data);
        uri_data.setText(mCurrentProfileListItem.getActionActivityUriData());
        
		// Music Listingボタンの指定
		Button btnListSound = (Button) mDialog.findViewById(R.id.edit_profile_action_list_sound);
		ProfileMaintenance.setActionListSoundBtnListener(mGlblParms,mDialog,tv_sound_filename,cb_music_vol,sb_music_vol,
				btnListSound,playBtnMusic);
		ProfileMaintenance.setMusicPlayBackBtnListener(mGlblParms,mDialog, tv_sound_filename,cb_music_vol,sb_music_vol, 
				playBtnMusic);
		// Ringtone ボタンの指定
		ProfileMaintenance.setRingtonePlayBackBtnListener(mGlblParms,mDialog, spinnerRingtoneType, 
				spinnerRingtoneName,cb_ringtone_vol,sb_ringtone_vol,
				playBtnRingtone);

		// CANCELボタンの指定
		final Button btnCancel = (Button) mDialog.findViewById(R.id.edit_profile_action_cancel_btn);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ProfileMaintenance.stopMusicPlayBack(mGlblParms);
				ProfileMaintenance.stopRingtonePlayBack(mGlblParms);
				mFragment.dismiss();
			}
		});
		// OKボタンの指定
		Button btnOK = (Button) mDialog.findViewById(R.id.edit_profile_action_ok_btn);
		btnOK.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String audit_msg=ProfileMaintenance.auditActionProfile(
						mGlblParms,mDialog, mCurrentGroup,mGlblParms.profileAdapter, 
						spinnerActionType, spinnerActivityName,spinnerActivityDataType,
						spinnerCompareTarget, spinnerCompareType,spinnerMessageType,
						spinnerTimeType, spinnerTimeTarget,
						spinnerTaskType, spinnerTaskTarget,aed_edit_list);
				if (!audit_msg.equals("")) {
					mGlblParms.commonDlg.showCommonDialog(false, "E", 
							mGlblParms.context.getString(R.string.msgs_edit_profile_action_profle_error), 
							audit_msg, null);
					return;
				} 
				ProfileMaintenance.stopMusicPlayBack(mGlblParms);
				ProfileMaintenance.stopRingtonePlayBack(mGlblParms);
				
				ProfileListItem ntpli=new ProfileListItem();
				ntpli=createProfileListItemFromScreenData(mGlblParms,mDialog,mGlblParms.profileAdapter,mCurrentProfileListItem.getProfileGroup());
				ntpli.setActionActivityExtraData(aed_edit_list);
				ntpli.setProfileGroupShowed(mCurrentProfileListItem.isProfileGroupShowed());
				mFragment.dismiss();

				mGlblParms.profileAdapter.replaceProfItem(ntpli);
				ProfileUtilities.verifyProfileIntegrity(mGlblParms.util,true,
						mGlblParms.profileAdapter,mCurrentGroup);
				ProfileMaintenance.putProfileListToService(mGlblParms,
					mGlblParms.profileAdapter,
					ProfileUtilities.isProfileGroupActive(mGlblParms.util,
							mGlblParms.profileAdapter,mCurrentGroup));
				
				if (mNotifyCompletion!=null) mNotifyCompletion.notifyToListener(true, null);
			}
		});

    };
    
	final static private void setViewVisibilityByActionType(final GlobalParameters mGlblParms,Dialog dialog, String action_type, String data_type) {
        Spinner sp_exec_android_activity=(Spinner)dialog.findViewById(R.id.edit_profile_action_exec_activity_name);
        LinearLayout ll_exec_sound=(LinearLayout)dialog.findViewById(R.id.edit_profile_action_ll_exec_sound);
        LinearLayout ll_exec_ringtone=(LinearLayout)dialog.findViewById(R.id.edit_profile_action_ll_exec_ringtone);
        LinearLayout ll_exec_compare=(LinearLayout)dialog.findViewById(R.id.edit_profile_action_ll_compare);
        LinearLayout ll_exec_message=(LinearLayout)dialog.findViewById(R.id.edit_profile_action_ll_message);
        LinearLayout ll_exec_time=(LinearLayout)dialog.findViewById(R.id.edit_profile_action_ll_time);
        LinearLayout ll_exec_task=(LinearLayout)dialog.findViewById(R.id.edit_profile_action_ll_task);
        LinearLayout ll_exec_wait=(LinearLayout)dialog.findViewById(R.id.edit_profile_action_ll_wait);
        LinearLayout ll_exec_bsh=(LinearLayout)dialog.findViewById(R.id.edit_profile_action_ll_bsh);
        LinearLayout ll_exec_shell=(LinearLayout)dialog.findViewById(R.id.edit_profile_action_ll_shell);
        LinearLayout ll_exec_test=(LinearLayout)dialog.findViewById(R.id.edit_profile_action_ll_test_exec);
//        TextView tv_layout_normal=(TextView)dialog.findViewById(R.id.edit_profile_action_dlg_bsh_layout_normal);
		final CheckBox cb_volume=(CheckBox)dialog.findViewById(R.id.edit_profile_action_profile_sound_use_volume);
		final SeekBar sb_volume=(SeekBar)dialog.findViewById(R.id.edit_profile_action_profile_sound_volume);
		
		final Spinner sp_type=(Spinner)dialog.findViewById(R.id.edit_profile_action_exec_activity_data_type);
        final Button uri_fl=(Button)dialog.findViewById(R.id.edit_profile_action_exec_activity_uri_data_filelist);
        final EditText uri_data=(EditText)dialog.findViewById(R.id.edit_profile_action_exec_activity_uri_data);
//        final Button test_exec_btn=(Button)dialog.findViewById(R.id.edit_profile_action_test_exec);
        final ListView lv_extra_data=(ListView)dialog.findViewById(R.id.edit_profile_action_exec_activity_extra_data_listview);
		uri_fl.setVisibility(Button.GONE);
		uri_data.setVisibility(EditText.GONE);
		ll_exec_test.setVisibility(Button.GONE);
		lv_extra_data.setVisibility(ListView.GONE);

		final EditText et_msg_text=(EditText)dialog.findViewById(R.id.edit_profile_action_message_message);
		et_msg_text.setVisibility(EditText.GONE);
		
		sp_exec_android_activity.setVisibility(Spinner.VISIBLE);
        sp_type.setVisibility(Spinner.VISIBLE);
        ll_exec_sound.setVisibility(LinearLayout.VISIBLE);
        ll_exec_ringtone.setVisibility(LinearLayout.VISIBLE);
        ll_exec_compare.setVisibility(LinearLayout.VISIBLE);
        ll_exec_message.setVisibility(LinearLayout.VISIBLE);
        ll_exec_time.setVisibility(LinearLayout.VISIBLE);
        ll_exec_task.setVisibility(LinearLayout.VISIBLE);
        ll_exec_wait.setVisibility(LinearLayout.VISIBLE);
        ll_exec_shell.setVisibility(LinearLayout.VISIBLE);

		if (action_type.equals(PROFILE_ACTION_TYPE_ACTIVITY)) {
	        ll_exec_sound.setVisibility(LinearLayout.GONE);
	        ll_exec_ringtone.setVisibility(LinearLayout.GONE);
	        ll_exec_compare.setVisibility(LinearLayout.GONE);
	        ll_exec_message.setVisibility(LinearLayout.GONE);
	        ll_exec_test.setVisibility(Button.VISIBLE);
	        ll_exec_time.setVisibility(LinearLayout.GONE);
	        ll_exec_task.setVisibility(LinearLayout.GONE);
	        ll_exec_wait.setVisibility(LinearLayout.GONE);
	        ll_exec_bsh.setVisibility(LinearLayout.GONE);
        	if (data_type.equals(PROFILE_ACTION_TYPE_ACTIVITY_DATA_TYPE_EXTRA)) {
        		uri_fl.setVisibility(Button.GONE);
        		uri_data.setVisibility(EditText.GONE);
        		lv_extra_data.setVisibility(ListView.VISIBLE);
        	} else if (data_type.equals(PROFILE_ACTION_TYPE_ACTIVITY_DATA_TYPE_URI)) {
        		uri_fl.setVisibility(Button.VISIBLE);
        		uri_data.setVisibility(EditText.VISIBLE);
        		lv_extra_data.setVisibility(ListView.GONE);
        	}
        	ll_exec_shell.setVisibility(LinearLayout.GONE);
		} else if (action_type.equals(PROFILE_ACTION_TYPE_MUSIC)) {
			sp_type.setVisibility(Spinner.GONE);
			sp_exec_android_activity.setVisibility(Spinner.GONE);
	        ll_exec_ringtone.setVisibility(LinearLayout.GONE);
			if (cb_volume.isChecked()) sb_volume.setEnabled(true);
			else sb_volume.setEnabled(false);
			ll_exec_compare.setVisibility(LinearLayout.GONE);
			ll_exec_message.setVisibility(LinearLayout.GONE);
	        ll_exec_time.setVisibility(LinearLayout.GONE);
	        ll_exec_task.setVisibility(LinearLayout.GONE);
	        ll_exec_wait.setVisibility(LinearLayout.GONE);
	        ll_exec_bsh.setVisibility(LinearLayout.GONE);
	        ll_exec_shell.setVisibility(LinearLayout.GONE);
		} else if (action_type.equals(PROFILE_ACTION_TYPE_RINGTONE)) {
			sp_type.setVisibility(Spinner.GONE);
			sp_exec_android_activity.setVisibility(Spinner.GONE);
	        ll_exec_sound.setVisibility(LinearLayout.GONE);
			if (cb_volume.isChecked()) sb_volume.setEnabled(true);
			else sb_volume.setEnabled(false);
			ll_exec_compare.setVisibility(LinearLayout.GONE);
			ll_exec_message.setVisibility(LinearLayout.GONE);
	        ll_exec_time.setVisibility(LinearLayout.GONE);
	        ll_exec_task.setVisibility(LinearLayout.GONE);
	        ll_exec_wait.setVisibility(LinearLayout.GONE);
	        ll_exec_bsh.setVisibility(LinearLayout.GONE);
	        ll_exec_shell.setVisibility(LinearLayout.GONE);
		} else if (action_type.equals(PROFILE_ACTION_TYPE_COMPARE)) {
			sp_type.setVisibility(Spinner.GONE);
			sp_exec_android_activity.setVisibility(Spinner.GONE);
	        ll_exec_sound.setVisibility(LinearLayout.GONE);
	        ll_exec_ringtone.setVisibility(LinearLayout.GONE);
	        ll_exec_compare.setVisibility(LinearLayout.VISIBLE);
	        ll_exec_message.setVisibility(LinearLayout.GONE);
	        ll_exec_time.setVisibility(LinearLayout.GONE);
	        ll_exec_task.setVisibility(LinearLayout.GONE);
	        ll_exec_wait.setVisibility(LinearLayout.GONE);
	        ll_exec_bsh.setVisibility(LinearLayout.GONE);
	        ll_exec_shell.setVisibility(LinearLayout.GONE);
		} else if (action_type.equals(PROFILE_ACTION_TYPE_MESSAGE)) {
			sp_type.setVisibility(Spinner.GONE);
			sp_exec_android_activity.setVisibility(Spinner.GONE);
	        ll_exec_sound.setVisibility(LinearLayout.GONE);
	        ll_exec_ringtone.setVisibility(LinearLayout.GONE);
	        ll_exec_compare.setVisibility(LinearLayout.GONE);
	        ll_exec_message.setVisibility(LinearLayout.VISIBLE);
	        et_msg_text.setVisibility(EditText.VISIBLE);
	        ll_exec_time.setVisibility(LinearLayout.GONE);
	        ll_exec_task.setVisibility(LinearLayout.GONE);
	        ll_exec_wait.setVisibility(LinearLayout.GONE);
	        ll_exec_bsh.setVisibility(LinearLayout.GONE);
	        ll_exec_shell.setVisibility(LinearLayout.GONE);
		} else if (action_type.equals(PROFILE_ACTION_TYPE_TIME)) {
			sp_type.setVisibility(Spinner.GONE);
			sp_exec_android_activity.setVisibility(Spinner.GONE);
	        ll_exec_sound.setVisibility(LinearLayout.GONE);
	        ll_exec_ringtone.setVisibility(LinearLayout.GONE);
	        ll_exec_compare.setVisibility(LinearLayout.GONE);
	        ll_exec_message.setVisibility(LinearLayout.GONE);
	        et_msg_text.setVisibility(EditText.GONE);
	        ll_exec_time.setVisibility(LinearLayout.VISIBLE);
	        ll_exec_task.setVisibility(LinearLayout.GONE);
	        ll_exec_wait.setVisibility(LinearLayout.GONE);
	        ll_exec_bsh.setVisibility(LinearLayout.GONE);
	        ll_exec_shell.setVisibility(LinearLayout.GONE);
		} else if (action_type.equals(PROFILE_ACTION_TYPE_TASK)) {
			sp_type.setVisibility(Spinner.GONE);
			sp_exec_android_activity.setVisibility(Spinner.GONE);
	        ll_exec_sound.setVisibility(LinearLayout.GONE);
	        ll_exec_ringtone.setVisibility(LinearLayout.GONE);
	        ll_exec_compare.setVisibility(LinearLayout.GONE);
	        ll_exec_message.setVisibility(LinearLayout.GONE);
	        et_msg_text.setVisibility(EditText.GONE);
	        ll_exec_time.setVisibility(LinearLayout.GONE);
	        ll_exec_task.setVisibility(LinearLayout.VISIBLE);
	        ll_exec_wait.setVisibility(LinearLayout.GONE);
	        ll_exec_bsh.setVisibility(LinearLayout.GONE);
	        ll_exec_shell.setVisibility(LinearLayout.GONE);
		} else if (action_type.equals(PROFILE_ACTION_TYPE_WAIT)) {
			sp_type.setVisibility(Spinner.GONE);
			sp_exec_android_activity.setVisibility(Spinner.GONE);
	        ll_exec_sound.setVisibility(LinearLayout.GONE);
	        ll_exec_ringtone.setVisibility(LinearLayout.GONE);
	        ll_exec_compare.setVisibility(LinearLayout.GONE);
	        ll_exec_message.setVisibility(LinearLayout.GONE);
	        et_msg_text.setVisibility(EditText.GONE);
	        ll_exec_time.setVisibility(LinearLayout.GONE);
	        ll_exec_task.setVisibility(LinearLayout.GONE);
	        ll_exec_wait.setVisibility(LinearLayout.VISIBLE);
	        ll_exec_bsh.setVisibility(LinearLayout.GONE);
	        ll_exec_shell.setVisibility(LinearLayout.GONE);
		} else if (action_type.equals(PROFILE_ACTION_TYPE_BSH_SCRIPT)) {
//			test_exec_btn.setVisibility(Button.VISIBLE);
			sp_type.setVisibility(Spinner.GONE);
			sp_exec_android_activity.setVisibility(Spinner.GONE);
	        ll_exec_sound.setVisibility(LinearLayout.GONE);
	        ll_exec_ringtone.setVisibility(LinearLayout.GONE);
	        ll_exec_compare.setVisibility(LinearLayout.GONE);
	        ll_exec_message.setVisibility(LinearLayout.GONE);
        	ll_exec_test.setVisibility(Button.VISIBLE);
	        et_msg_text.setVisibility(EditText.GONE);
	        ll_exec_time.setVisibility(LinearLayout.GONE);
	        ll_exec_task.setVisibility(LinearLayout.GONE);
	        ll_exec_wait.setVisibility(LinearLayout.GONE);
	        ll_exec_bsh.setVisibility(LinearLayout.VISIBLE);
	        ll_exec_shell.setVisibility(LinearLayout.GONE);
		} else if (action_type.equals(PROFILE_ACTION_TYPE_SHELL_COMMAND)) {
			sp_type.setVisibility(Spinner.GONE);
			sp_exec_android_activity.setVisibility(Spinner.GONE);
	        ll_exec_sound.setVisibility(LinearLayout.GONE);
	        ll_exec_ringtone.setVisibility(LinearLayout.GONE);
	        ll_exec_compare.setVisibility(LinearLayout.GONE);
	        ll_exec_message.setVisibility(LinearLayout.GONE);
        	ll_exec_test.setVisibility(Button.VISIBLE);
	        et_msg_text.setVisibility(EditText.GONE);
	        ll_exec_time.setVisibility(LinearLayout.GONE);
	        ll_exec_task.setVisibility(LinearLayout.GONE);
	        ll_exec_wait.setVisibility(LinearLayout.GONE);
	        ll_exec_bsh.setVisibility(LinearLayout.GONE);
	        ll_exec_shell.setVisibility(LinearLayout.VISIBLE);
		} 
	};

	final static private void initProfileActionWidget(GlobalParameters mGlblParms,
			Dialog dialog, 
			ProfileListItem tpli,
			ArrayList<DataArrayEditListItem> comp_data_list,
			ArrayList<ActivityExtraDataItem>aed_edit_list,			
			String curr_grp, 
			AdapterProfileList pfla,
			CustomSpinnerAdapter adapterActionType,
			CustomSpinnerAdapter adapterActivityName,
			CustomSpinnerAdapter adapterActivityDataType,
			CustomSpinnerAdapter adapterRingtoneType,
			CustomSpinnerAdapter adapterRingtoneName,
			CustomSpinnerAdapter adapterCompareType,
			CustomSpinnerAdapter adapterCompareResult,
			CustomSpinnerAdapter adapterCompareTarget,
			CustomSpinnerAdapter adapterMessageType,
			CustomSpinnerAdapter adapterTimeType,
			CustomSpinnerAdapter adapterTimeTarget,
			CustomSpinnerAdapter adapterTaskType,
			CustomSpinnerAdapter adapterTaskTarget,
			CustomSpinnerAdapter adapterWaitTarget,
			CustomSpinnerAdapter adapterWaitTimeoutType,
			CustomSpinnerAdapter adapterWaitTimeoutValue,
			CustomSpinnerAdapter adapterWaitTimeoutUnits) {
		final CheckBox cb_active=(CheckBox)dialog.findViewById(R.id.edit_profile_action_enabled);
		final TextView tv_sound_filename=(TextView)dialog.findViewById(R.id.edit_profile_action_exec_sound_file_name);
		final CheckBox cb_music_vol=(CheckBox)dialog.findViewById(R.id.edit_profile_action_profile_sound_use_volume);
		final SeekBar sb_music_vol=(SeekBar)dialog.findViewById(R.id.edit_profile_action_profile_sound_volume);
//		final Button playBtnMusic = (Button)dialog.findViewById(R.id.edit_profile_action_profile_sound_play_back);
//		final Button playBtnRingtone = (Button)dialog.findViewById(R.id.edit_profile_action_profile_ringtone_play_back);
		final CheckBox cb_ringtone_vol=(CheckBox)dialog.findViewById(R.id.edit_profile_action_profile_ringtone_use_volume);
		final SeekBar sb_ringtone_vol=(SeekBar)dialog.findViewById(R.id.edit_profile_action_profile_ringtone_volume);
		final Spinner spinnerActionType = (Spinner) dialog.findViewById(R.id.edit_profile_action_action_type);
        final Spinner spinnerActivityName = (Spinner) dialog.findViewById(R.id.edit_profile_action_exec_activity_name);
        final Spinner spinnerActivityDataType = (Spinner) dialog.findViewById(R.id.edit_profile_action_exec_activity_data_type);
        final Spinner spinnerRingtoneType = (Spinner) dialog.findViewById(R.id.edit_profile_action_exec_ringtone_type);
        final Spinner spinnerRingtoneName = (Spinner) dialog.findViewById(R.id.edit_profile_action_exec_ringtone_name);
        final Spinner spinnerCompareType = (Spinner) dialog.findViewById(R.id.edit_profile_action_compare_type);
        final EditText et_comp_value1=(EditText)dialog.findViewById(R.id.edit_profile_action_compare_value1);
        final EditText et_comp_value2=(EditText)dialog.findViewById(R.id.edit_profile_action_compare_value2);
        final ListView lv_comp_data=(ListView)dialog.findViewById(R.id.edit_profile_action_compare_value_listview);
        final Spinner spinnerCompareResult = (Spinner) dialog.findViewById(R.id.edit_profile_action_compare_result);
        final Spinner spinnerCompareTarget = (Spinner) dialog.findViewById(R.id.edit_profile_action_compare_target);
        final Spinner spinnerMessageType = (Spinner) dialog.findViewById(R.id.edit_profile_action_message_type);
        final EditText et_msg_text=(EditText)dialog.findViewById(R.id.edit_profile_action_message_message);
        final CheckBox cb_vib_used=(CheckBox)dialog.findViewById(R.id.edit_profile_action_message_vibration);
        final CheckBox cb_led_used=(CheckBox)dialog.findViewById(R.id.edit_profile_action_message_led);
        final RadioButton rb_msg_blue=(RadioButton)dialog.findViewById(R.id.edit_profile_action_message_led_blue);
        final RadioButton rb_msg_red=(RadioButton)dialog.findViewById(R.id.edit_profile_action_message_led_red);
        final RadioButton rb_msg_green=(RadioButton)dialog.findViewById(R.id.edit_profile_action_message_led_green);
        final Spinner spinnerTimeType = (Spinner) dialog.findViewById(R.id.edit_profile_action_time_type);
        final Spinner spinnerTimeTarget = (Spinner) dialog.findViewById(R.id.edit_profile_action_time_target);
        final Spinner spinnerTaskType = (Spinner) dialog.findViewById(R.id.edit_profile_action_task_type);
        final Spinner spinnerTaskTarget = (Spinner) dialog.findViewById(R.id.edit_profile_action_task_target);
        final Spinner spinnerWaitTarget = (Spinner) dialog.findViewById(R.id.edit_profile_action_wait_target);
        final Spinner spinnerWaitTimeoutType = (Spinner) dialog.findViewById(R.id.edit_profile_action_wait_timeout);
        final Spinner spinnerWaitTimeoutValue = (Spinner) dialog.findViewById(R.id.edit_profile_action_wait_timeout_value);
        final Spinner spinnerWaitTimeoutUnits = (Spinner) dialog.findViewById(R.id.edit_profile_action_wait_timeout_units);
//        final EditText et_bsh_script=(EditText) dialog.findViewById(R.id.edit_profile_action_dlg_bsh_script_text);
        final EditText et_shell_cmd=(EditText) dialog.findViewById(R.id.edit_profile_action_dlg_shell_cmd_text);
        final CheckBox cb_shell_cmd_with_su=(CheckBox) dialog.findViewById(R.id.edit_profile_action_dlg_shell_cmd_with_su);
        
        if (tpli==null || tpli.getActionSoundVolLeft().equals("-1") || tpli.getActionSoundVolLeft().equals("")) {
			cb_music_vol.setChecked(false);
			sb_music_vol.setProgress(100);
			sb_music_vol.setEnabled(false);
		} else {
			cb_music_vol.setChecked(true);
			sb_music_vol.setProgress(Integer.valueOf(tpli.getActionSoundVolLeft()));
			sb_music_vol.setEnabled(true);
		}
		if (tpli==null || tpli.getActionRingtoneVolLeft().equals("-1") || tpli.getActionRingtoneVolLeft().equals("")) {
			cb_ringtone_vol.setChecked(false);
			sb_ringtone_vol.setProgress(100);
			sb_ringtone_vol.setEnabled(false);
		} else {
			cb_ringtone_vol.setChecked(true);
			sb_ringtone_vol.setProgress(Integer.valueOf(tpli.getActionRingtoneVolLeft()));
			sb_ringtone_vol.setEnabled(true);
		}

		if (tpli==null || tpli.isProfileEnabled()) cb_active.setChecked(true);
		else cb_active.setChecked(false);
		
		if (tpli!=null && tpli.getActionType().equals(PROFILE_ACTION_TYPE_MUSIC)) {
			if (tpli.getActionSoundFileName().equals("")) tv_sound_filename.setText("Not specified");
			else tv_sound_filename.setText(tpli.getActionSoundFileName());
		} else tv_sound_filename.setText("Not specified");  

		if (tpli!=null && tpli.getActionCompareValue()!=null) {
	        String[]c_data_array=tpli.getActionCompareValue();
	        for (int i=0;i<c_data_array.length;i++) {
	        	if (c_data_array[i]!=null && !c_data_array[i].equals("")) {
	                DataArrayEditListItem daeli=new DataArrayEditListItem();
	                daeli.data_value=c_data_array[i];
	                comp_data_list.add(daeli);
	        	}
	        }
		}
        if (comp_data_list.size()==0) {
        	DataArrayEditListItem daeli=new DataArrayEditListItem();
            daeli.data_value="";
            daeli.dummy_data=true;
            comp_data_list.add(daeli);
        }
        if (tpli!=null && tpli.getActionActivityExtraData()!=null) {
        	for (int i=0;i<tpli.getActionActivityExtraData().size();i++)
        		aed_edit_list.add(tpli.getActionActivityExtraData().get(i).clone());
        }
        
        if (tpli!=null && tpli.getActionType().equals(PROFILE_ACTION_TYPE_SHELL_COMMAND)) {
            et_shell_cmd.setText(tpli.getActionShellCmd());
//            if (!mGlblParms.envParms.settingUseRootPrivilege) cb_shell_cmd_with_su.setVisibility(CheckBox.GONE);
            cb_shell_cmd_with_su.setChecked(tpli.isActionShellCmdWithSu());
        }

        String action_type="";
        if (tpli!=null) action_type=tpli.getActionType();
        setSpinnerActionType(mGlblParms,dialog,spinnerActionType,adapterActionType,action_type);

        String action_activity_name="";
        if (tpli!=null) action_activity_name=tpli.getActionActivityName();
        ProfileMaintenance.setSpinnerActivityName(mGlblParms,dialog,spinnerActivityName,adapterActivityName,action_activity_name);

        String action_activity_data_type="";
        if (tpli!=null) action_activity_data_type=tpli.getActionActivityDataType();
        setSpinnerActivityDataType(mGlblParms,dialog,spinnerActivityDataType,adapterActivityDataType,
        		action_activity_data_type);

        String action_ringtone_type="";
        if (tpli!=null) action_ringtone_type=tpli.getActionRingtoneType();
        setSpinnerRingtoneType(mGlblParms,dialog,spinnerRingtoneType,adapterRingtoneType,
        		action_ringtone_type);

        String action_ringtone_name="";
        int action_ringtone_type_int=0;
        if (tpli!=null) action_ringtone_name=tpli.getActionRingtoneName();
        if (tpli!=null) action_ringtone_type_int=tpli.getActionRingtoneTypeInt();
        ProfileMaintenance.setSpinnerRingtoneName(mGlblParms,dialog,spinnerRingtoneName,adapterRingtoneName,
        		action_ringtone_name, action_ringtone_type_int);

        String action_compare_type="";
        if (tpli!=null) action_compare_type=tpli.getActionCompareType();
        setSpinnerCompareType(mGlblParms,dialog,spinnerCompareType,adapterCompareType,
        		action_compare_type,true,true,true,true,true);

        lv_comp_data.setAdapter(mGlblParms.actionCompareDataAdapter);

        et_comp_value2.setVisibility(EditText.GONE);
        String action_compare_result="";
        if (tpli!=null && tpli.getActionCompareValue()!=null) {
        	action_compare_result=tpli.getActionCompareResultAction();
            et_comp_value1.setText(tpli.getActionCompareValue(0));
            et_comp_value2.setText(tpli.getActionCompareValue(1));
        } else {
            et_comp_value1.setText("");
            et_comp_value2.setText("");
        }
        setSpinnerCompareResult(mGlblParms,dialog,spinnerCompareResult,adapterCompareResult,
        		action_compare_result);

        String action_compare_target="";
        if (tpli!=null) action_compare_target=tpli.getActionCompareTarget();
        setSpinnerCompareTarget(mGlblParms,dialog,spinnerCompareTarget,adapterCompareTarget,
        		action_compare_target);
		setCompareEditTextAttr(mGlblParms,action_compare_target, et_comp_value1,et_comp_value1);
		
        String action_message_type="";
        if (tpli!=null) action_message_type=tpli.getActionMessageType();
        setSpinnerMessageType(mGlblParms,dialog,spinnerMessageType,adapterMessageType,
        		action_message_type);

        et_msg_text.setText("");
        rb_msg_blue.setChecked(true);
        cb_vib_used.setChecked(false);
        cb_led_used.setChecked(false);
        if (tpli!=null) {
            et_msg_text.setText(tpli.getActionMessageText());
            rb_msg_blue.setChecked(true);
            if (tpli.getActionMessageLedColor().equals(PROFILE_ACTION_TYPE_MESSAGE_LED_RED)) rb_msg_red.setChecked(true);
            else if (tpli.getActionMessageLedColor().equals(PROFILE_ACTION_TYPE_MESSAGE_LED_GREEN)) rb_msg_green.setChecked(true);
            cb_vib_used.setChecked(tpli.isActionMessageUseVibration());
            cb_led_used.setChecked(tpli.isActionMessageUseLed());
        }

        String action_time_type="";
        if (tpli!=null) action_time_type=tpli.getActionTimeType();
        ProfileMaintenance.setSpinnerTimeType(mGlblParms,dialog,spinnerTimeType,adapterTimeType,action_time_type);
        
        String action_time_target="";
        if (tpli!=null) action_time_target=tpli.getActionTimeTarget();
        ProfileMaintenance.setSpinnerTimeTarget(mGlblParms,dialog,spinnerTimeTarget,adapterTimeTarget,pfla,curr_grp,
        		action_time_target);

        String action_task_type="";
        if (tpli!=null) action_task_type=tpli.getActionTaskType();
        ProfileMaintenance.setSpinnerTaskType(mGlblParms,dialog,spinnerTaskType,adapterTaskType,action_task_type);
        
        String action_task_target="";
        if (tpli!=null) action_task_target=tpli.getActionTaskTarget();
        ProfileMaintenance.setSpinnerTaskTarget(mGlblParms,true,dialog,spinnerTaskTarget,adapterTaskTarget,pfla,curr_grp,
        		action_task_target);


        String action_wait_target="";
        if (tpli!=null) action_wait_target=tpli.getActionWaitTarget();
        ProfileMaintenance.setSpinnerWaitTarget(mGlblParms,dialog,spinnerWaitTarget,adapterWaitTarget,pfla,curr_grp,
        		action_wait_target);

        String to_sel=PROFILE_ACTION_TYPE_WAIT_TIMEOUT_TYPE_NOTIMEOUT;
        if (tpli!=null && !tpli.getActionWaitTimeoutValue().equals("")) to_sel=PROFILE_ACTION_TYPE_WAIT_TIMEOUT_TYPE_TIMEOUTIS;
        ProfileMaintenance.setSpinnerWaitTimeoutType(mGlblParms,dialog,spinnerWaitTimeoutType,adapterWaitTimeoutType,pfla,curr_grp,to_sel);

        String action_wait_timeout_value="";
        if (tpli!=null) action_wait_timeout_value=tpli.getActionWaitTimeoutValue();
        ProfileMaintenance.setSpinnerWaitTimeoutValue(mGlblParms,dialog,spinnerWaitTimeoutValue,adapterWaitTimeoutValue,pfla,curr_grp,
        		action_wait_timeout_value);

        String action_wait_timeout_units="";
        if (tpli!=null) action_wait_timeout_units=tpli.getActionWaitTimeoutUnits();
        ProfileMaintenance.setSpinnerWaitTimeoutUnits(mGlblParms,dialog,spinnerWaitTimeoutUnits,adapterWaitTimeoutUnits,pfla,curr_grp,
        		action_wait_timeout_units);

//        CommonDialog.setDlgBoxSizeLimit(dialog,true);
	};

	
	final static private void setActionTypeSelectionListner(final GlobalParameters mGlblParms,final Dialog dialog,
    		final Spinner spinnerActionType, final CustomSpinnerAdapter adapterActionType,
    		final Spinner spinnerDataType, final CustomSpinnerAdapter adapterDataType) {
        spinnerActionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            final public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
            	setViewVisibilityByActionType(mGlblParms,dialog, 
            			spinnerActionType.getSelectedItem().toString(),
            			spinnerDataType.getSelectedItem().toString());
            }
            @Override
            final public void onNothingSelected(AdapterView<?> arg0) {}
        });
        spinnerDataType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            final public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
            	setViewVisibilityByActionType(mGlblParms,dialog, 
            			spinnerActionType.getSelectedItem().toString(),
            			spinnerDataType.getSelectedItem().toString());
            }
            @Override
            final public void onNothingSelected(AdapterView<?> arg0) {}
        });

    };
	final static private void setSpinnerActionType(final GlobalParameters mGlblParms,Dialog dialog, Spinner spinner, 
			CustomSpinnerAdapter adapter, String selected) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_action_select_action_type));
        spinner.setAdapter(adapter);
        adapter.clear();
		adapter.add(PROFILE_ACTION_TYPE_ACTIVITY);
		adapter.add(PROFILE_ACTION_TYPE_BSH_SCRIPT);
		adapter.add(PROFILE_ACTION_TYPE_COMPARE);
		adapter.add(PROFILE_ACTION_TYPE_MESSAGE);
		adapter.add(PROFILE_ACTION_TYPE_MUSIC);
		adapter.add(PROFILE_ACTION_TYPE_RINGTONE);
		adapter.add(PROFILE_ACTION_TYPE_TASK);
		adapter.add(PROFILE_ACTION_TYPE_TIME);
		adapter.add(PROFILE_ACTION_TYPE_WAIT);
		adapter.add(PROFILE_ACTION_TYPE_SHELL_COMMAND);		
		for (int i=0;i<adapter.getCount();i++)
			if (adapter.getItem(i).equals(selected)) spinner.setSelection(i);
		
	};

	
	final static private void setSpinnerActivityDataType(final GlobalParameters mGlblParms,Dialog dialog, Spinner spinner,
			CustomSpinnerAdapter adapter, String selected) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_action_select_activity_data_type));
        spinner.setAdapter(adapter);
        adapter.clear();
        adapter.add(PROFILE_ACTION_TYPE_ACTIVITY_DATA_TYPE_NONE);
        adapter.add(PROFILE_ACTION_TYPE_ACTIVITY_DATA_TYPE_EXTRA);
        adapter.add(PROFILE_ACTION_TYPE_ACTIVITY_DATA_TYPE_URI);
        int sel_no=0;
        if (selected.equals(PROFILE_ACTION_TYPE_ACTIVITY_DATA_TYPE_EXTRA)) sel_no=1;
        else if (selected.equals(PROFILE_ACTION_TYPE_ACTIVITY_DATA_TYPE_URI)) sel_no=2;
        spinner.setSelection(sel_no);
	};

	final static private void setSpinnerExtraDataType(final GlobalParameters mGlblParms,Dialog dialog, Spinner spinner,
			CustomSpinnerAdapter adapter, String selected) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_action_select_activity_data_type));
        spinner.setAdapter(adapter);
        adapter.clear();
        adapter.add(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_STRING);
        adapter.add(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_INT);
        adapter.add(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_BOOLEAN);
        int sel_no=0;
        if (selected.equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_STRING)) sel_no=0;
        else if (selected.equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_INT)) sel_no=1;
        else sel_no=2;
        spinner.setSelection(sel_no);
	};

	final static private void setSpinnerExtraDataBoolean(final GlobalParameters mGlblParms,Dialog dialog, Spinner spinner,
			CustomSpinnerAdapter adapter, String selected) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_action_select_activity_data_type));
        spinner.setAdapter(adapter);
        adapter.clear();
        adapter.add("false");
        adapter.add("true");
        int sel_no=0;
        if (selected.equals("true")) sel_no=1;
        spinner.setSelection(sel_no);
	};
	
	final static private void setSpinnerRingtoneType(final GlobalParameters mGlblParms,Dialog dialog, Spinner spinner,
			CustomSpinnerAdapter adapter, String selected) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_action_select_ringtone_type));
        spinner.setAdapter(adapter);
        adapter.add(PROFILE_ACTION_RINGTONE_TYPE_ALARM);
        adapter.add(PROFILE_ACTION_RINGTONE_TYPE_NOTIFICATION);
        adapter.add(PROFILE_ACTION_RINGTONE_TYPE_RINGTONE);
        int sidx=0;
        if (selected.equals(PROFILE_ACTION_RINGTONE_TYPE_ALERT) ||
        		selected.equals(PROFILE_ACTION_RINGTONE_TYPE_ALARM)) sidx=0; 
        else if (selected.equals(PROFILE_ACTION_RINGTONE_TYPE_NOTIFICATION)) sidx=1;
        else if (selected.equals(PROFILE_ACTION_RINGTONE_TYPE_RINGTONE)) sidx=2;
        spinner.setSelection(sidx);
	};
	
	final static private void setSpinnerCompareTarget(final GlobalParameters mGlblParms,Dialog dialog, Spinner spinner, 
			CustomSpinnerAdapter adapter, String selected) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_action_compare_select_target));
        spinner.setAdapter(adapter);
        adapter.clear();
        adapter.add(PROFILE_ACTION_TYPE_COMPARE_TARGET_BATTERY);
        adapter.add(PROFILE_ACTION_TYPE_COMPARE_TARGET_BLUETOOTH);
        adapter.add(PROFILE_ACTION_TYPE_COMPARE_TARGET_LIGHT);
        adapter.add(PROFILE_ACTION_TYPE_COMPARE_TARGET_WIFI);
        adapter.add(PROFILE_ACTION_TYPE_COMPARE_TARGET_TIME);
        for (int i=0;i<adapter.getCount();i++) 
        	if (adapter.getItem(i).equals(selected)) {
        		spinner.setSelection(i);
        		break;
        	}
	};

	final static private void setSpinnerCompareType(final GlobalParameters mGlblParms,Dialog dialog, Spinner spinner, 
			CustomSpinnerAdapter adapter, String selected,
			boolean c_eq, boolean c_ne, boolean c_gt, boolean c_lt, boolean c_bw) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_action_compare_select_type));
        spinner.setAdapter(adapter);
        adapter.clear();
        if (c_eq) adapter.add(PROFILE_ACTION_TYPE_COMPARE_COMPARE_EQ);
        if (c_ne) adapter.add(PROFILE_ACTION_TYPE_COMPARE_CPMPARE_NE);
        if (c_gt) adapter.add(PROFILE_ACTION_TYPE_COMPARE_COMPARE_GT);
        if (c_lt) adapter.add(PROFILE_ACTION_TYPE_COMPARE_COMPARE_LT);
        if (c_bw) adapter.add(PROFILE_ACTION_TYPE_COMPARE_COMPARE_BETWEEN);
        for (int i=0;i<adapter.getCount();i++) 
        	if (adapter.getItem(i).equals(selected)) {
        		spinner.setSelection(i);
        		break;
        	}
	};

	final static private void setSpinnerCompareResult(final GlobalParameters mGlblParms,Dialog dialog, Spinner spinner, 
			CustomSpinnerAdapter adapter, String selected) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_action_compare_select_type));
        spinner.setAdapter(adapter);
        adapter.clear();
        adapter.add(PROFILE_ACTION_TYPE_COMPARE_RESULT_CONTINUE);
        adapter.add(PROFILE_ACTION_TYPE_COMPARE_RESULT_ABORT);
        adapter.add(PROFILE_ACTION_TYPE_COMPARE_RESULT_SKIP);
        for (int i=0;i<adapter.getCount();i++) 
        	if (adapter.getItem(i).equals(selected)) {
        		spinner.setSelection(i);
        		break;
        	}
	};

	final static private void setSpinnerMessageType(final GlobalParameters mGlblParms,Dialog dialog, Spinner spinner, 
			CustomSpinnerAdapter adapter, String selected) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_action_compare_select_type));
        spinner.setAdapter(adapter);
        adapter.clear();
        adapter.add(PROFILE_ACTION_TYPE_MESSAGE_DIALOG);
        adapter.add(PROFILE_ACTION_TYPE_MESSAGE_NOTIFICATION);
//        adapter.add(PROFILE_ACTION_TYPE_MESSAGE_WITHOUT_TEXT);
        for (int i=0;i<adapter.getCount();i++) 
        	if (adapter.getItem(i).equals(selected)) {
        		spinner.setSelection(i);
        		break;
        	}
	};

	final static private void setProfileActionActivityListener(
			final GlobalParameters mGlblParms,
			final Dialog dialog, 
			final AdapterProfileList pfla,
			final String curr_grp, 
			final ArrayList<ActivityExtraDataItem>p_aed_edit_list) {
        final Button uri_fl=(Button)dialog.findViewById(R.id.edit_profile_action_exec_activity_uri_data_filelist);
        final EditText uri_data=(EditText)dialog.findViewById(R.id.edit_profile_action_exec_activity_uri_data);
        uri_data.setOnKeyListener(new OnKeyListener(){
			@Override
			public boolean onKey(View arg0, int keyCode, KeyEvent event) {
	             if (//event.getAction() == KeyEvent.ACTION_DOWN &&
	                       keyCode == KeyEvent.KEYCODE_ENTER) {
	            	 return true;
	             }
	             return false;
			}
        });
		// Uri filelist ボタンの指定
		uri_fl.setOnClickListener(new View.OnClickListener() {
			final public void onClick(View v) {
    			NotifyEvent ntfy=new NotifyEvent(mGlblParms.context);
    			ntfy.setListener(new NotifyEventListener() {
    				@Override
    				final public void positiveResponse(Context c, Object[] o) {
    					uri_data.selectAll();
    					uri_data.setText(Uri.parse((String)o[0]).toString());
    				}
    				@Override
    				final public void negativeResponse(Context c, Object[] o) {}
    			});
    			mGlblParms.commonDlg.fileOnlySelectWithoutCreate(
    					LocalMountPoint.getExternalStorageDir(),"", 
    					"", "Select file", ntfy);
			}
		});
		mGlblParms.activityExtraDataEditListAdapter=
				new AdapterActivityExtraDataEditList(mGlblParms.context, R.layout.edit_activity_extra_data_list_item,p_aed_edit_list);
		ListView lv_aed=(ListView)dialog.findViewById(R.id.edit_profile_action_exec_activity_extra_data_listview);
		lv_aed.setAdapter(mGlblParms.activityExtraDataEditListAdapter);
		if (mGlblParms.activityExtraDataEditListAdapter.getCount()==0) {
			ActivityExtraDataItem aedi=new ActivityExtraDataItem();
			aedi.key_value="";
			aedi.data_value=mGlblParms.context.getString(R.string.msgs_edit_profile_action_activity_extra_data_no_data_exists);
			mGlblParms.activityExtraDataEditListAdapter.add(aedi);
		}
		lv_aed.setOnItemClickListener(new OnItemClickListener(){
			@Override
			final public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				int t_pos;
				if (mGlblParms.activityExtraDataEditListAdapter.getItem(pos).key_value.equals("")) t_pos=-1;
				else t_pos=pos;
					editActivityExtraDataItem(mGlblParms,t_pos);
			}
		});
		lv_aed.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			final public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int pos, long arg3) {
				mGlblParms.ccMenu.addMenuItem(mGlblParms.context.getString(R.string.msgs_edit_profile_action_activity_extra_data_ccmenu_add))
			  	.setOnClickListener(new CustomContextMenuOnClickListener() {
				  @Override
				  final public void onClick(CharSequence menuTitle) {
					  editActivityExtraDataItem(mGlblParms,-1);
				  	}
			  	});
				if (!mGlblParms.activityExtraDataEditListAdapter.getItem(pos).key_value.equals("")) {
					mGlblParms.ccMenu.addMenuItem(mGlblParms.context.getString(R.string.msgs_edit_profile_action_activity_extra_data_ccmenu_edit))
				  	.setOnClickListener(new CustomContextMenuOnClickListener() {
					  @Override
					  final public void onClick(CharSequence menuTitle) {
						  editActivityExtraDataItem(mGlblParms,pos);			  		}
				  	});
					mGlblParms.ccMenu.addMenuItem(mGlblParms.context.getString(R.string.msgs_edit_profile_action_activity_extra_data_ccmenu_delete))
				  	.setOnClickListener(new CustomContextMenuOnClickListener() {
					  @Override
					  final public void onClick(CharSequence menuTitle) {
						  mGlblParms.activityExtraDataEditListAdapter.remove(pos);
						  	if (mGlblParms.activityExtraDataEditListAdapter.getCount()==0) {
								ActivityExtraDataItem aedi=new ActivityExtraDataItem();
								aedi.key_value="";
								aedi.data_value=mGlblParms.context.getString(R.string.msgs_edit_profile_action_activity_extra_data_no_data_exists);
								mGlblParms.activityExtraDataEditListAdapter.add(aedi);
						  	}
						  	mGlblParms.activityExtraDataEditListAdapter.notifyDataSetChanged();
				  		}
				  	});
				}
				mGlblParms.ccMenu.createMenu();

				return true;
			}
		});
	};

	final static private void editActivityExtraDataItem(final GlobalParameters mGlblParms,
			final int sel_pos) {
		// カスタムダイアログの生成
		final Dialog dialog = new Dialog(mGlblParms.context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCanceledOnTouchOutside(false);
//		dialog.getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		dialog.setContentView(R.layout.edit_activity_extra_data_item_dlg);
		final TextView dlg_msg = (TextView) dialog.findViewById(R.id.edit_activity_extra_data_item_msg);
//		final TextView dlg_title = (TextView) dialog.findViewById(R.id.edit_activity_extra_data_item_title);

//		CommonDialog.setDlgBoxSizeLimit(dialog,true);
		final Button btn_cancel= (Button) dialog.findViewById(R.id.edit_activity_extra_data_item_cancel_btn);
		final Button btn_ok = (Button) dialog.findViewById(R.id.edit_activity_extra_data_item_ok_btn);

		final EditText et_key=(EditText)dialog.findViewById(R.id.edit_activity_extra_data_item_key);
		final EditText et_string=(EditText)dialog.findViewById(R.id.edit_activity_extra_data_item_data_string);
		final EditText et_int=(EditText)dialog.findViewById(R.id.edit_activity_extra_data_item_data_int);
		final CheckBox cb_array=(CheckBox)dialog.findViewById(R.id.edit_activity_extra_data_item_array);
		final Button btn_add_array=(Button)dialog.findViewById(R.id.edit_activity_extra_data_item_add_array);
		final ListView lv_array=(ListView)dialog.findViewById(R.id.edit_activity_extra_data_item_array_listview);
		final TextView lv_spacer=(TextView)dialog.findViewById(R.id.edit_activity_extra_data_item_array_spacer);
		
		final Spinner spinnerExtraDataType = (Spinner) dialog.findViewById(R.id.edit_activity_extra_data_item_data_type);
        final CustomSpinnerAdapter adapterExtraDataType = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        setSpinnerExtraDataType(mGlblParms,dialog,spinnerExtraDataType,adapterExtraDataType,PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_STRING);

        final Spinner spinnerExtraDataBoolean = (Spinner) dialog.findViewById(R.id.edit_activity_extra_data_item_data_boolean);
        final CustomSpinnerAdapter adapterExtraDataBoolean = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        setSpinnerExtraDataBoolean(mGlblParms,dialog,spinnerExtraDataBoolean,adapterExtraDataBoolean,"false");

        final Button update_apply=(Button)dialog.findViewById(R.id.edit_activity_extra_data_item_data_update_apply);
        final Button update_cancel=(Button)dialog.findViewById(R.id.edit_activity_extra_data_item_data_update_cancel);
        update_apply.setVisibility(Button.GONE);
        update_cancel.setVisibility(Button.GONE);
        spinnerExtraDataBoolean.setVisibility(Spinner.GONE);
		et_string.setVisibility(EditText.GONE);
		et_int.setVisibility(EditText.GONE);
		btn_add_array.setVisibility(Button.GONE);
		lv_array.setVisibility(ListView.GONE);
		lv_spacer.setVisibility(TextView.VISIBLE);

		final ArrayList<DataArrayEditListItem> aed_array_list=new ArrayList<DataArrayEditListItem>();
		final AdapterDataArrayEditList aed_array_adapter=new AdapterDataArrayEditList(mGlblParms.context,
				R.layout.data_array_edit_list_item,aed_array_list);
		lv_array.setAdapter(aed_array_adapter);
		setActivityExtraDataEditItemViewVisibility(mGlblParms,dialog, aed_array_adapter,spinnerExtraDataType);

		cb_array.setChecked(false);
		if (sel_pos!=-1) {
			et_key.setEnabled(false);
			et_key.setTextColor(Color.WHITE);
			et_key.selectAll();
			et_key.setText(mGlblParms.activityExtraDataEditListAdapter.getItem(sel_pos).key_value);
			if (mGlblParms.activityExtraDataEditListAdapter.getItem(sel_pos).data_type.equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_STRING)) {
				et_string.setText(mGlblParms.activityExtraDataEditListAdapter.getItem(sel_pos).data_value);
				spinnerExtraDataType.setSelection(0);
			} else if (mGlblParms.activityExtraDataEditListAdapter.getItem(sel_pos).data_type.equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_INT)) {
				et_int.setText(mGlblParms.activityExtraDataEditListAdapter.getItem(sel_pos).data_value);
				spinnerExtraDataType.setSelection(1);
			} else if (mGlblParms.activityExtraDataEditListAdapter.getItem(sel_pos).data_type.equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_BOOLEAN)) {
				spinnerExtraDataType.setSelection(2);
				if (mGlblParms.activityExtraDataEditListAdapter.getItem(sel_pos).data_value.equals("false")) spinnerExtraDataBoolean.setSelection(0);
				else spinnerExtraDataBoolean.setSelection(1);
			}
			mGlblParms.currentSelectedExtraDataType=spinnerExtraDataType.getSelectedItem().toString();
			if (mGlblParms.activityExtraDataEditListAdapter.getItem(sel_pos).data_value_array.equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_ARRAY_YES)) {
				cb_array.setChecked(true);
				et_string.setText("");
				et_int.setText("");
				createActivityExtraDataArrayList(aed_array_adapter, mGlblParms.activityExtraDataEditListAdapter.getItem(sel_pos));
				aed_array_adapter.notifyDataSetChanged();
		        update_apply.setVisibility(Button.GONE);
		        update_cancel.setVisibility(Button.GONE);
		        spinnerExtraDataBoolean.setVisibility(Spinner.GONE);
				et_string.setVisibility(EditText.GONE);
				et_int.setVisibility(EditText.GONE);
				btn_add_array.setVisibility(Button.VISIBLE);
				lv_array.setVisibility(ListView.VISIBLE);
				lv_spacer.setVisibility(TextView.GONE);
			} else {
				cb_array.setChecked(false);
		        update_apply.setVisibility(Button.GONE);
		        update_cancel.setVisibility(Button.GONE);
		        spinnerExtraDataBoolean.setVisibility(Spinner.GONE);
				et_string.setVisibility(EditText.GONE);
				et_int.setVisibility(EditText.GONE);
				btn_add_array.setVisibility(Button.GONE);
				lv_array.setVisibility(ListView.GONE);
				lv_spacer.setVisibility(TextView.VISIBLE);
				if (mGlblParms.activityExtraDataEditListAdapter.getItem(sel_pos).data_type.equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_STRING)) {
					et_string.setVisibility(EditText.VISIBLE);
				} else if (mGlblParms.activityExtraDataEditListAdapter.getItem(sel_pos).data_type.equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_INT)) {
					et_int.setVisibility(EditText.VISIBLE);
				} else if (mGlblParms.activityExtraDataEditListAdapter.getItem(sel_pos).data_type.equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_BOOLEAN)) {
					spinnerExtraDataBoolean.setVisibility(Spinner.VISIBLE);
				}
			}
		}

		btn_add_array.setOnClickListener(new OnClickListener(){
			@Override
			final public void onClick(View arg0) {
				String n_data="";
				if (spinnerExtraDataType.getSelectedItem().equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_STRING)) {
					n_data="";
				} else if (spinnerExtraDataType.getSelectedItem().equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_INT)) {
					n_data="0";
				} else if (spinnerExtraDataType.getSelectedItem().equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_BOOLEAN)) {
					n_data="false";
				}
				DataArrayEditListItem aeda_item=new DataArrayEditListItem();
				aeda_item.data_value=n_data;
				aed_array_adapter.add(aeda_item);
				aed_array_adapter.notifyDataSetChanged();
			}
		});
		
		NotifyEvent ntfy=new NotifyEvent(mGlblParms.context);
		ntfy.setListener(new NotifyEventListener(){
			@Override
			final public void positiveResponse(Context c, Object[] o) {
				spinnerExtraDataType.setEnabled(false);
				String c_data=(String)o[0];
				if (spinnerExtraDataType.getSelectedItem().equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_STRING)) {
					et_string.setText(c_data);
			        spinnerExtraDataBoolean.setVisibility(Spinner.GONE);
					et_string.setVisibility(EditText.VISIBLE);
					et_int.setVisibility(EditText.GONE);
					et_string.requestFocus();
				} else if (spinnerExtraDataType.getSelectedItem().equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_INT)) {
					et_int.setText(c_data);
			        spinnerExtraDataBoolean.setVisibility(Spinner.GONE);
					et_string.setVisibility(EditText.GONE);
					et_int.setVisibility(EditText.VISIBLE);
					et_int.requestFocus();
				} else if (spinnerExtraDataType.getSelectedItem().equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_BOOLEAN)) {
					if (c_data.equals("false")) spinnerExtraDataBoolean.setSelection(0);
					else spinnerExtraDataBoolean.setSelection(1);
			        spinnerExtraDataBoolean.setVisibility(Spinner.VISIBLE);
					et_string.setVisibility(EditText.GONE);
					et_int.setVisibility(EditText.GONE);
				}
				update_apply.setVisibility(Button.VISIBLE);
				update_cancel.setVisibility(Button.VISIBLE);
				btn_ok.setEnabled(false);
				btn_cancel.setEnabled(false);
				cb_array.setEnabled(false);
				btn_add_array.setEnabled(false);

			}
			@Override
			final public void negativeResponse(Context c, Object[] o) {}
		});
		aed_array_adapter.setEditBtnNotifyListener(ntfy);
		update_apply.setVisibility(Button.GONE);
		update_cancel.setVisibility(Button.GONE);
		update_apply.setOnClickListener(new View.OnClickListener() {
			final public void onClick(View v) {
					boolean no_err=auditActivityExtraData(mGlblParms,dialog,spinnerExtraDataType,spinnerExtraDataBoolean);
					if (!no_err) return;
					int pos=-1;
					for (int i=0;i<aed_array_list.size();i++) {
						if (aed_array_list.get(i).while_edit) {
							pos=i;
							break;
						}
					}
					if (spinnerExtraDataType.getSelectedItem().equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_STRING)) {
						aed_array_list.get(pos).data_value=et_string.getText().toString();
						aed_array_list.get(pos).while_edit=false;
					} else if (spinnerExtraDataType.getSelectedItem().equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_INT)) {
						aed_array_list.get(pos).data_value=et_int.getText().toString();
						aed_array_list.get(pos).while_edit=false;
					} else if (spinnerExtraDataType.getSelectedItem().equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_BOOLEAN)) {
						String n_boolean="true";
						if (spinnerExtraDataBoolean.getSelectedItem().toString().equals("false")) n_boolean="false"; 
						aed_array_list.get(pos).data_value=n_boolean;
						aed_array_list.get(pos).while_edit=false;
					}
					et_string.setText("");
					et_int.setText("");
					et_string.setVisibility(EditText.GONE);
					et_int.setVisibility(EditText.GONE);
					spinnerExtraDataBoolean.setVisibility(Spinner.GONE);
					aed_array_adapter.notifyDataSetChanged();
					update_apply.setVisibility(Button.GONE);
					update_cancel.setVisibility(Button.GONE);
					spinnerExtraDataType.setEnabled(true);
					btn_ok.setEnabled(true);
					btn_cancel.setEnabled(true);
					cb_array.setEnabled(true);
					btn_add_array.setEnabled(true);
			}
		});
		update_cancel.setOnClickListener(new View.OnClickListener() {
			final public void onClick(View v) {
				for (int i=0;i<aed_array_list.size();i++) {
					aed_array_list.get(i).while_edit=false;
				}
				et_string.setText("");
				et_int.setText("");
				et_string.setVisibility(EditText.GONE);
				et_int.setVisibility(EditText.GONE);
				spinnerExtraDataBoolean.setVisibility(Spinner.GONE);
				aed_array_adapter.notifyDataSetChanged();
				update_apply.setVisibility(Button.GONE);
				update_cancel.setVisibility(Button.GONE);
				spinnerExtraDataType.setEnabled(true);
				btn_ok.setEnabled(true);
				btn_cancel.setEnabled(true);
				cb_array.setEnabled(true);
				btn_add_array.setEnabled(true);
			}
		});
		
		// CANCELボタンの指定
		btn_cancel.setOnClickListener(new View.OnClickListener() {
			final public void onClick(View v) {
				dialog.dismiss();
			}
		});
		// OKボタンの指定
		btn_ok.setOnClickListener(new View.OnClickListener() {
			final public void onClick(View v) {
				ActivityExtraDataItem aedi=null;
				if (sel_pos==-1) {//Add item
					if (et_key.getText().toString().equals("")) {
						dlg_msg.setText(mGlblParms.context.getString(R.string.msgs_edit_profile_action_activity_extra_data_key_name_missing));
						return;
					}
					aedi=new ActivityExtraDataItem();
				} else {//Edit item
					aedi=mGlblParms.activityExtraDataEditListAdapter.getItem(sel_pos);
				}
				aedi.key_value=et_key.getText().toString();
				if (cb_array.isChecked()) {
					if (aed_array_list.size()==0) {
						dlg_msg.setText(mGlblParms.context.getString(R.string.msgs_edit_profile_action_activity_extra_data_array_data_missing));
						return;
					}
					aedi.data_value_array=PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_ARRAY_YES;
					aedi.data_type=spinnerExtraDataType.getSelectedItem().toString();
					aedi.data_value="";
//					Log.v("","array size="+aed_array_list.size());
					for (int i=0;i<aed_array_list.size();i++) {
						if (aedi.data_type.equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_STRING)) {
//							aedi.data_value+="\u00a0"+aed_array_list.get(i).array_data_value+"\u0003";
							aedi.data_value+=aed_array_list.get(i).data_value+"\u0003";
//							GeneralUtilities.hexString("String",aed_array_list.get(i).array_data_value.getBytes(),0,aed_array_list.get(i).array_data_value.getBytes().length);
						} else {
							aedi.data_value+=aed_array_list.get(i).data_value+"\u0003";
//							GeneralUtilities.hexString("int",aed_array_list.get(i).data_value.getBytes(),0,aed_array_list.get(i).data_value.getBytes().length);
						}
					}
//					GeneralUtilities.hexString("test",aedi.data_value.getBytes(),0,aedi.data_value.getBytes().length);
//					Log.v("","dv="+aedi.data_value.replaceAll("\u0003", ";"));
				} else {
					boolean no_err=auditActivityExtraData(mGlblParms,dialog,spinnerExtraDataType,spinnerExtraDataBoolean);
					if (!no_err) return;
					else {
						aedi.data_value_array=PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_ARRAY_NO;
						aedi.data_type=spinnerExtraDataType.getSelectedItem().toString();
						if (spinnerExtraDataType.getSelectedItem().toString().equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_STRING)) {
							aedi.data_value=et_string.getText().toString();
						} else if (spinnerExtraDataType.getSelectedItem().toString().equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_INT)) {
							aedi.data_value=et_int.getText().toString();
						} else if (spinnerExtraDataType.getSelectedItem().toString().equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_BOOLEAN)) {
							if (spinnerExtraDataBoolean.getSelectedItem().toString().equals("false")) aedi.data_value="false";
							else aedi.data_value="true";
						}
					}
				}
				if (sel_pos==-1) mGlblParms.activityExtraDataEditListAdapter.add(aedi);
				if (mGlblParms.activityExtraDataEditListAdapter.getItem(0).key_value.equals("")) mGlblParms.activityExtraDataEditListAdapter.remove(0);
				mGlblParms.activityExtraDataEditListAdapter.notifyDataSetChanged();

				dialog.dismiss();
			}
		});
		// Cancelリスナーの指定
		dialog.setOnCancelListener(new Dialog.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				btn_cancel.performClick();
			}
		});
//		dialog.setCancelable(false);
		dialog.show();
	};

	final static private boolean auditActivityExtraData(
			GlobalParameters mGlblParms,
			Dialog dialog,
			Spinner spinnerExtraDataType,
			Spinner spinnerExtraDataBoolean) {
		final TextView dlg_msg = (TextView) dialog.findViewById(R.id.edit_activity_extra_data_item_msg);
//		final EditText et_key=(EditText)dialog.findViewById(R.id.edit_activity_extra_data_item_key);
//		final EditText et_string=(EditText)dialog.findViewById(R.id.edit_activity_extra_data_item_data_string);
		final EditText et_int=(EditText)dialog.findViewById(R.id.edit_activity_extra_data_item_data_int);
		if (spinnerExtraDataType.getSelectedItem().toString().equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_STRING)) {
//			if (et_string.getText().toString().equals("")) {
//				dlg_msg.setText("Specify String value");
//				return false;
//			}
		} else if (spinnerExtraDataType.getSelectedItem().toString().equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_INT)) {
			if (et_int.getText().toString().equals("")) {
				dlg_msg.setText(mGlblParms.context.getString(R.string.msgs_edit_profile_action_activity_extra_data_int_data_missing));
				return false;
			}
		} else if (spinnerExtraDataType.getSelectedItem().toString().equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_BOOLEAN)) {
		}
		return true;
	};

	final static private void createActivityExtraDataArrayList(
			AdapterDataArrayEditList aed_array_adapter, ActivityExtraDataItem data_item) {
		if (data_item.data_value_array.equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_ARRAY_YES)) {
//			Log.v("","dv="+data_item.data_value.replaceAll("\u0003", ";"));
			String n_data=data_item.data_value.replaceAll("\u0003", "\u00a0\u0003");
			String[] array_item=n_data.split("\u0003");
//			Log.v("","array size="+array_item.length+", dv="+array_item[0]);
			if (array_item!=null && array_item.length!=0) {
				for (int i=0;i<array_item.length;i++) {
					DataArrayEditListItem aeda_item=new DataArrayEditListItem();
					aeda_item.data_value=array_item[i].substring(0, array_item[i].length()-1);
					aed_array_adapter.add(aeda_item);
				}
			}
		}
	};

	final static private void setActivityExtraDataEditItemViewVisibility(
			final GlobalParameters mGlblParms,Dialog dialog,
			final AdapterDataArrayEditList aed_array_adapter,
			final Spinner spinnerExtraDataType) {
//		final EditText et_key=(EditText)dialog.findViewById(R.id.edit_activity_extra_data_item_key);
		final EditText et_string=(EditText)dialog.findViewById(R.id.edit_activity_extra_data_item_data_string);
		final EditText et_int=(EditText)dialog.findViewById(R.id.edit_activity_extra_data_item_data_int);
		final CheckBox cb_array=(CheckBox)dialog.findViewById(R.id.edit_activity_extra_data_item_array);
		
		final Button btn_add_array=(Button)dialog.findViewById(R.id.edit_activity_extra_data_item_add_array);
		final ListView lv_array=(ListView)dialog.findViewById(R.id.edit_activity_extra_data_item_array_listview);
		final TextView lv_spacer=(TextView)dialog.findViewById(R.id.edit_activity_extra_data_item_array_spacer);
		final Button update_apply=(Button)dialog.findViewById(R.id.edit_activity_extra_data_item_data_update_apply);
		final Button update_cancel=(Button)dialog.findViewById(R.id.edit_activity_extra_data_item_data_update_cancel);
		final Spinner spinnerExtraDataBoolean = (Spinner) dialog.findViewById(R.id.edit_activity_extra_data_item_data_boolean);
		
		cb_array.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			final public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if (isChecked) {
					btn_add_array.setVisibility(Button.VISIBLE);
					lv_array.setVisibility(Button.VISIBLE);
					lv_spacer.setVisibility(TextView.GONE);
					et_string.setVisibility(EditText.GONE);
					et_int.setVisibility(EditText.GONE);
					spinnerExtraDataBoolean.setVisibility(Spinner.GONE);
				} else {
					btn_add_array.setVisibility(Button.GONE);
					lv_array.setVisibility(Button.GONE);
					lv_spacer.setVisibility(TextView.VISIBLE);
					update_apply.setVisibility(Button.GONE);
					update_cancel.setVisibility(Button.GONE);
					if (spinnerExtraDataType.getSelectedItem().toString().equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_STRING)) {
						et_string.setVisibility(EditText.VISIBLE);
						et_int.setVisibility(EditText.GONE);
						spinnerExtraDataBoolean.setVisibility(LinearLayout.GONE);
					} else if (spinnerExtraDataType.getSelectedItem().toString().equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_INT)) {
						et_string.setVisibility(EditText.GONE);
						et_int.setVisibility(EditText.VISIBLE);
						spinnerExtraDataBoolean.setVisibility(LinearLayout.GONE);
					} else if (spinnerExtraDataType.getSelectedItem().toString().equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_BOOLEAN)) {
						et_string.setVisibility(EditText.GONE);
						et_int.setVisibility(EditText.GONE);
						spinnerExtraDataBoolean.setVisibility(LinearLayout.VISIBLE);
					}
				}
			}
		});
		spinnerExtraDataType.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			final public void onItemSelected(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				if (!cb_array.isChecked()) {
					if (spinnerExtraDataType.getSelectedItem().toString().equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_STRING)) {
						et_string.setVisibility(EditText.VISIBLE);
						et_int.setVisibility(EditText.GONE);
						spinnerExtraDataBoolean.setVisibility(LinearLayout.GONE);
					} else if (spinnerExtraDataType.getSelectedItem().toString().equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_INT)) {
						et_string.setVisibility(EditText.GONE);
						et_int.setVisibility(EditText.VISIBLE);
						spinnerExtraDataBoolean.setVisibility(LinearLayout.GONE);
					} else if (spinnerExtraDataType.getSelectedItem().toString().equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_BOOLEAN)) {
						et_string.setVisibility(EditText.GONE);
						et_int.setVisibility(EditText.GONE);
						spinnerExtraDataBoolean.setVisibility(LinearLayout.VISIBLE);
					}
				}
//				Log.v("","c="+currentSelectedExtraDataType+", n="+spinnerExtraDataType.getSelectedItem().toString());
				if (!spinnerExtraDataType.getSelectedItem().toString().equals(mGlblParms.currentSelectedExtraDataType)) {
					aed_array_adapter.clear();
					aed_array_adapter.notifyDataSetChanged();
//					Log.v("","size="+aed_array_adapter.getCount());
					mGlblParms.currentSelectedExtraDataType=spinnerExtraDataType.getSelectedItem().toString();
				}
			}

			@Override
			final public void onNothingSelected(AdapterView<?> arg0) {}
			
		});
	};

	final static private void setProfileActionCompareListener(
			final GlobalParameters mGlblParms,
			final Dialog dialog, 
			final AdapterProfileList pfla,
			final String curr_grp,
			final CustomSpinnerAdapter adapterCompareType,
			final String curr_comp_type) {
        final Spinner spinnerCompareType = (Spinner) dialog.findViewById(R.id.edit_profile_action_compare_type);
        
        final EditText et_comp_value1=(EditText)dialog.findViewById(R.id.edit_profile_action_compare_value1);
        final EditText et_comp_value2=(EditText)dialog.findViewById(R.id.edit_profile_action_compare_value2);
        
        final Spinner spinnerCompareTarget = (Spinner) dialog.findViewById(R.id.edit_profile_action_compare_target);
        final ListView lv_comp_data = (ListView) dialog.findViewById(R.id.edit_profile_action_compare_value_listview);
        final Button add_btn=(Button)dialog.findViewById(R.id.edit_profile_action_compare_value_add);
		add_btn.setVisibility(Button.GONE);
		lv_comp_data.setVisibility(ListView.GONE);
        spinnerCompareType.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			final public void onItemSelected(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				if (spinnerCompareType.getItemAtPosition(pos).toString().equals(PROFILE_ACTION_TYPE_COMPARE_COMPARE_BETWEEN)) {
					et_comp_value2.setVisibility(EditText.VISIBLE);
				} else {
					et_comp_value2.setVisibility(EditText.GONE);
				}
			}
			@Override
			final public void onNothingSelected(AdapterView<?> arg0) {}
        });
        spinnerCompareTarget.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			final public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				if (spinnerCompareTarget.getItemAtPosition(pos).toString().equals(PROFILE_ACTION_TYPE_COMPARE_TARGET_BLUETOOTH) ||
						spinnerCompareTarget.getItemAtPosition(pos).toString().equals(PROFILE_ACTION_TYPE_COMPARE_TARGET_WIFI)) {
					setSpinnerCompareType(mGlblParms,dialog,spinnerCompareType,adapterCompareType,curr_comp_type,
							true,true,false,false,false);
					lv_comp_data.setVisibility(ListView.VISIBLE);
					add_btn.setVisibility(Button.VISIBLE);
					et_comp_value1.setVisibility(EditText.GONE);
					et_comp_value2.setVisibility(EditText.GONE);
				} else {
					setSpinnerCompareType(mGlblParms,dialog,spinnerCompareType,adapterCompareType,curr_comp_type,
							true,true,true,true,true);
					lv_comp_data.setVisibility(ListView.GONE);
					add_btn.setVisibility(Button.GONE);
					et_comp_value1.setVisibility(EditText.VISIBLE);
					et_comp_value2.setVisibility(EditText.VISIBLE);
				}
				setCompareEditTextAttr(mGlblParms,spinnerCompareTarget.getItemAtPosition(pos).toString(),
						et_comp_value1,et_comp_value1);
			}
			@Override
			final public void onNothingSelected(AdapterView<?> arg0) {}
        });
        NotifyEvent ntfy=new NotifyEvent(mGlblParms.context);
        ntfy.setListener(new NotifyEventListener() {
			@Override
			final public void positiveResponse(Context c, Object[] o) {
				editDataArrayListItem(mGlblParms,(String)o[0],(Integer)o[1]);
			}
			@Override
			final public void negativeResponse(Context c, Object[] o) {}
        });
        mGlblParms.actionCompareDataAdapter.setEditBtnNotifyListener(ntfy);
        add_btn.setOnClickListener(new OnClickListener(){
			@Override
			final public void onClick(View arg0) {
				if (mGlblParms.actionCompareDataAdapter.getCount()==1) {
					if (mGlblParms.actionCompareDataAdapter.getItem(0).dummy_data) 
						mGlblParms.actionCompareDataAdapter.remove(0);
				}
				DataArrayEditListItem daeli=new DataArrayEditListItem();
				mGlblParms.actionCompareDataAdapter.add(daeli);
				mGlblParms.actionCompareDataAdapter.notifyDataSetChanged();
			}
        });
	};

	final static private void editDataArrayListItem(
			final GlobalParameters mGlblParms,
			final String edit_data, final int pos) {
		final Dialog dialog = new Dialog(mGlblParms.context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCanceledOnTouchOutside(false);
//		dialog.getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		dialog.setContentView(R.layout.data_array_item_edit_dlg);
//		final TextView dlg_msg = (TextView) dialog.findViewById(R.id.data_array_item_edit_dlg_msg);
		final TextView dlg_title = (TextView) dialog.findViewById(R.id.data_array_item_edit_dlg_title);
		dlg_title.setText("Edit data");
		final EditText dlg_value = (EditText) dialog.findViewById(R.id.data_array_item_edit_dlg_value);
		final Button dlg_apply = (Button) dialog.findViewById(R.id.data_array_item_edit_dlg_apply);
		final Button dlg_cancel = (Button) dialog.findViewById(R.id.data_array_item_edit_dlg_cancel);

//		CommonDialog.setDlgBoxSizeCompact(dialog);
		
		dlg_value.setText(edit_data);
		
		// CANCELボタンの指定
		dlg_cancel.setOnClickListener(new View.OnClickListener() {
			final public void onClick(View v) {
				mGlblParms.actionCompareDataAdapter.getItem(pos).while_edit=false;
				mGlblParms.actionCompareDataAdapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});
		// Applyボタンの指定
		dlg_apply.setOnClickListener(new View.OnClickListener() {
			final public void onClick(View v) {
				mGlblParms.actionCompareDataAdapter.getItem(pos).while_edit=false;
				mGlblParms.actionCompareDataAdapter.getItem(pos).data_value=dlg_value.getText().toString();
				mGlblParms.actionCompareDataAdapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});
		// Cancelリスナーの指定
		dialog.setOnCancelListener(new Dialog.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				dlg_cancel.performClick();
			}
		});
//		dialog.setCancelable(false);
		dialog.show();
	};
	
	final static private void setProfileActionMessageListener(
			final GlobalParameters mGlblParms,
			final Dialog dialog, 
			final AdapterProfileList pfla,
			final String curr_grp) {
        final Spinner spinnerMessageType = (Spinner) dialog.findViewById(R.id.edit_profile_action_message_type);
        final EditText et_msg_text=(EditText)dialog.findViewById(R.id.edit_profile_action_message_message);
        final CheckBox cb_led_used=(CheckBox)dialog.findViewById(R.id.edit_profile_action_message_led);
        final RadioButton rb_msg_blue=(RadioButton)dialog.findViewById(R.id.edit_profile_action_message_led_blue);
        final RadioButton rb_msg_red=(RadioButton)dialog.findViewById(R.id.edit_profile_action_message_led_red);
        final RadioButton rb_msg_green=(RadioButton)dialog.findViewById(R.id.edit_profile_action_message_led_green);
        cb_led_used.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			final public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if (isChecked) {
					rb_msg_blue.setEnabled(true);
					rb_msg_red.setEnabled(true);
					rb_msg_green.setEnabled(true);
				} else {
					rb_msg_blue.setEnabled(false);
					rb_msg_red.setEnabled(false);
					rb_msg_green.setEnabled(false);
				}
			}
        });
        spinnerMessageType.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			final public void onItemSelected(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				et_msg_text.setVisibility(EditText.VISIBLE);
				if (spinnerMessageType.getItemAtPosition(pos).toString().equals(PROFILE_ACTION_TYPE_MESSAGE_NOTIFICATION)) {
					cb_led_used.setVisibility(CheckBox.VISIBLE);
					rb_msg_blue.setVisibility(RadioButton.VISIBLE);
					rb_msg_red.setVisibility(RadioButton.VISIBLE);
					rb_msg_green.setVisibility(RadioButton.VISIBLE);
				} else {
					cb_led_used.setVisibility(CheckBox.GONE);
					rb_msg_blue.setVisibility(RadioButton.GONE);
					rb_msg_red.setVisibility(RadioButton.GONE);
					rb_msg_green.setVisibility(RadioButton.GONE);
				}
			}
			@Override
			final public void onNothingSelected(AdapterView<?> arg0) {}
        });
	};

	final static private void setProfileActionTimeListener(
			final GlobalParameters mGlblParms,
			final Dialog dialog, 
			final AdapterProfileList pfla,
			final String curr_grp) {
        final Spinner spinnerTimeType = (Spinner) dialog.findViewById(R.id.edit_profile_action_time_type);
        spinnerTimeType.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			final public void onItemSelected(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
			}
			@Override
			final public void onNothingSelected(AdapterView<?> arg0) {}
        });
	};
	
	final static private void setProfileActionTaskListener(
			final GlobalParameters mGlblParms,
			final Dialog dialog, 
			final AdapterProfileList pfla,
			final String curr_grp,
			final CustomSpinnerAdapter adapterTaskType,
			final CustomSpinnerAdapter adapterTaskTarget,
			final String tgt_task) {
        final Spinner spinnerTaskType = (Spinner) dialog.findViewById(R.id.edit_profile_action_task_type);
        final Spinner spinnerTaskTarget = (Spinner) dialog.findViewById(R.id.edit_profile_action_task_target);
        spinnerTaskType.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			final public void onItemSelected(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				if (adapterTaskType.getItem(pos).equals(PROFILE_ACTION_TYPE_TASK_START_TASK)) {
					ProfileMaintenance.setSpinnerTaskTarget(mGlblParms,true,dialog,spinnerTaskTarget,adapterTaskTarget,pfla,curr_grp,tgt_task);
				} else ProfileMaintenance.setSpinnerTaskTarget(mGlblParms,false,dialog,spinnerTaskTarget,adapterTaskTarget,pfla,curr_grp,tgt_task);
			}
			@Override
			final public void onNothingSelected(AdapterView<?> arg0) {}
        });
	};

	final static private void setProfileActionWaitListener(final GlobalParameters mGlblParms,final Dialog dialog) {
//        final Spinner spinnerWaitTarget = (Spinner) dialog.findViewById(R.id.edit_profile_action_wait_target);
        final Spinner spinnerWaitTimeoutType = (Spinner) dialog.findViewById(R.id.edit_profile_action_wait_timeout);
        final Spinner spinnerWaitTimeoutValue = (Spinner) dialog.findViewById(R.id.edit_profile_action_wait_timeout_value);
        final Spinner spinnerWaitTimeoutUnits = (Spinner) dialog.findViewById(R.id.edit_profile_action_wait_timeout_units);
        spinnerWaitTimeoutType.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			final public void onItemSelected(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				if (spinnerWaitTimeoutType.getSelectedItem().toString().equals(PROFILE_ACTION_TYPE_WAIT_TIMEOUT_TYPE_NOTIMEOUT)) {
					spinnerWaitTimeoutValue.setVisibility(Spinner.GONE);
					spinnerWaitTimeoutUnits.setVisibility(Spinner.GONE);
				} else {
					spinnerWaitTimeoutValue.setVisibility(Spinner.VISIBLE);
					spinnerWaitTimeoutUnits.setVisibility(Spinner.VISIBLE);
				}
			}
			@Override
			final public void onNothingSelected(AdapterView<?> arg0) {}
        });
	};

	final static private void setProfileActionBshListener(GlobalParameters mGlblParms,final Dialog dialog, 
			final AdapterProfileList pfla, final String curr_grp, ProfileListItem tpli) {
//      final Spinner spinnerWaitTarget = (Spinner) dialog.findViewById(R.id.edit_profile_action_wait_target);
      
      BeanShellMethodEditor bse=new BeanShellMethodEditor(mGlblParms.context, mGlblParms.util, 
				mGlblParms.ccMenu, mGlblParms.commonDlg, pfla, curr_grp, mGlblParms.androidApplicationList, mGlblParms.ringtoneList);
      bse.editProfileActionBshText(dialog,tpli);

	};
	
	final static private ProfileListItem createProfileListItemFromScreenData(
			final GlobalParameters mGlblParms,
			final Dialog dialog, 
			final AdapterProfileList pfla,
			final String curr_grp) {
		final EditText dlg_prof_name_et=(EditText)dialog.findViewById(R.id.edit_profile_action_profile_et_name);
		
		final CheckBox cb_active=(CheckBox)dialog.findViewById(R.id.edit_profile_action_enabled);
		final TextView tv_sound_filename=(TextView)dialog.findViewById(R.id.edit_profile_action_exec_sound_file_name);
		final CheckBox cb_music_vol=(CheckBox)dialog.findViewById(R.id.edit_profile_action_profile_sound_use_volume);
		final SeekBar sb_music_vol=(SeekBar)dialog.findViewById(R.id.edit_profile_action_profile_sound_volume);
		final CheckBox cb_ringtone_vol=(CheckBox)dialog.findViewById(R.id.edit_profile_action_profile_ringtone_use_volume);
		final SeekBar sb_ringtone_vol=(SeekBar)dialog.findViewById(R.id.edit_profile_action_profile_ringtone_volume);

        final Spinner spinnerActionType = (Spinner) dialog.findViewById(R.id.edit_profile_action_action_type);
        final Spinner spinnerActivityName = (Spinner) dialog.findViewById(R.id.edit_profile_action_exec_activity_name);
        final Spinner spinnerActivityDataType = (Spinner) dialog.findViewById(R.id.edit_profile_action_exec_activity_data_type);
        final Spinner spinnerRingtoneType = (Spinner) dialog.findViewById(R.id.edit_profile_action_exec_ringtone_type);
        final Spinner spinnerRingtoneName = (Spinner) dialog.findViewById(R.id.edit_profile_action_exec_ringtone_name);
        final Spinner spinnerCompareType = (Spinner) dialog.findViewById(R.id.edit_profile_action_compare_type);
        final EditText et_comp_value1=(EditText)dialog.findViewById(R.id.edit_profile_action_compare_value1);
        final EditText et_comp_value2=(EditText)dialog.findViewById(R.id.edit_profile_action_compare_value2);

        final Spinner spinnerCompareTarget = (Spinner) dialog.findViewById(R.id.edit_profile_action_compare_target);
        final Spinner spinnerCompareResult = (Spinner) dialog.findViewById(R.id.edit_profile_action_compare_result);
        final Spinner spinnerMessageType = (Spinner) dialog.findViewById(R.id.edit_profile_action_message_type);
        final EditText et_msg_text=(EditText)dialog.findViewById(R.id.edit_profile_action_message_message);
        final CheckBox cb_vib_used=(CheckBox)dialog.findViewById(R.id.edit_profile_action_message_vibration);
        final CheckBox cb_led_used=(CheckBox)dialog.findViewById(R.id.edit_profile_action_message_led);
        final RadioButton rb_msg_red=(RadioButton)dialog.findViewById(R.id.edit_profile_action_message_led_red);
        final RadioButton rb_msg_green=(RadioButton)dialog.findViewById(R.id.edit_profile_action_message_led_green);
        final Spinner spinnerTimeType = (Spinner) dialog.findViewById(R.id.edit_profile_action_time_type);
        final Spinner spinnerTimeTarget = (Spinner) dialog.findViewById(R.id.edit_profile_action_time_target);
        final Spinner spinnerTaskType = (Spinner) dialog.findViewById(R.id.edit_profile_action_task_type);
        final Spinner spinnerTaskTarget = (Spinner) dialog.findViewById(R.id.edit_profile_action_task_target);
        
      	final EditText uri_data=(EditText)dialog.findViewById(R.id.edit_profile_action_exec_activity_uri_data);
      	
		String prof_active;
		if (cb_active.isChecked()) prof_active=PROFILE_ENABLED;
		else prof_active=PROFILE_DISABLED;
		
		String prof_act_name = null,prof_act_pkgname = null;
		String file_name="";
		ProfileListItem ntpli=new ProfileListItem();
		String act_type=spinnerActionType.getSelectedItem().toString();
		if (act_type.equals(PROFILE_ACTION_TYPE_ACTIVITY)) {
			String t_tn=spinnerActivityName.getSelectedItem().toString();
			prof_act_name=t_tn.substring(0,t_tn.indexOf("("));
			prof_act_pkgname=t_tn.replace(prof_act_name+"(", "").replace(")","");
			ArrayList<ActivityExtraDataItem>aed_edit_list=new ArrayList<ActivityExtraDataItem>();
			ntpli.setActionAndroidEntry(
					PROFILE_VERSION_CURRENT,curr_grp,ProfileUtilities.isProfileGroupActive(mGlblParms.util,pfla,curr_grp),
					System.currentTimeMillis(),
					PROFILE_TYPE_ACTION,
					dlg_prof_name_et.getText().toString(),
					prof_active,
					prof_act_name,
					prof_act_pkgname,
					spinnerActivityDataType.getSelectedItem().toString(),
					uri_data.getText().toString(),aed_edit_list);
		} else if (act_type.equals(PROFILE_ACTION_TYPE_MUSIC)) {
			int vol=-1;
			if (cb_music_vol.isChecked()) vol=sb_music_vol.getProgress();
			file_name=tv_sound_filename.getText().toString();
			ntpli.setActionMusicEntry(
					PROFILE_VERSION_CURRENT,curr_grp,ProfileUtilities.isProfileGroupActive(mGlblParms.util,pfla,curr_grp),
					System.currentTimeMillis(),
					PROFILE_TYPE_ACTION,
					dlg_prof_name_et.getText().toString(),
					prof_active,
					file_name,
					String.valueOf(vol),String.valueOf(vol));
		} else if (act_type.equals(PROFILE_ACTION_TYPE_RINGTONE)) {
			int vol=-1;
			if (cb_ringtone_vol.isChecked()) vol=sb_ringtone_vol.getProgress();
			String rt_type_str=spinnerRingtoneType.getSelectedItem().toString();
			String rt_name_str=spinnerRingtoneName.getSelectedItem().toString();
			String rt_path="";
			for (int i=0;i<mGlblParms.ringtoneList.size();i++) {
				if (mGlblParms.ringtoneList.get(i).ringtone_type==ProfileMaintenance.getRingtoneTypeInt(rt_type_str)){
					if (mGlblParms.ringtoneList.get(i).ringtone_name.equals(rt_name_str)) {
						rt_path=mGlblParms.ringtoneList.get(i).ringtone_uri.getPath();
						break;
					}
				}
			}
			ntpli.setActionRingtoneEntry(
					PROFILE_VERSION_CURRENT,curr_grp,ProfileUtilities.isProfileGroupActive(mGlblParms.util,pfla,curr_grp),
					System.currentTimeMillis(),
					PROFILE_TYPE_ACTION,
					dlg_prof_name_et.getText().toString(),
					prof_active,rt_type_str,rt_name_str,rt_path,
					String.valueOf(vol),String.valueOf(vol));
		} else if (act_type.equals(PROFILE_ACTION_TYPE_COMPARE)) {
			String c_tgt=spinnerCompareTarget.getSelectedItem().toString();
			String c_typ=spinnerCompareType.getSelectedItem().toString();
			String ra=spinnerCompareResult.getSelectedItem().toString();
			String[] c_val=null;
			if (c_tgt.equals(PROFILE_ACTION_TYPE_COMPARE_TARGET_BLUETOOTH) || 
					c_tgt.equals(PROFILE_ACTION_TYPE_COMPARE_TARGET_WIFI)) {
				int a_idx=0;
				for (int i=0;i<mGlblParms.actionCompareDataAdapter.getCount();i++) {
					if (mGlblParms.actionCompareDataAdapter.getItem(i).data_value!=null && 
							!mGlblParms.actionCompareDataAdapter.getItem(i).data_value.equals("")) {
						a_idx++;
					}
				}
				c_val=new String[a_idx+1];
				a_idx=0;
				for (int i=0;i<mGlblParms.actionCompareDataAdapter.getCount();i++) {
					if (mGlblParms.actionCompareDataAdapter.getItem(i).data_value!=null && 
							!mGlblParms.actionCompareDataAdapter.getItem(i).data_value.equals("")) {
						c_val[a_idx]=mGlblParms.actionCompareDataAdapter.getItem(i).data_value;
						a_idx++;
					}
				}
			} else {
				if (c_typ.equals(PROFILE_ACTION_TYPE_COMPARE_COMPARE_BETWEEN)) {
					c_val=new String[2];
					c_val[0]=et_comp_value1.getText().toString();
					c_val[1]=et_comp_value2.getText().toString();
				} else {
					c_val=new String[1];
					c_val[0]=et_comp_value1.getText().toString();
				}
			}
			ntpli.setActionCompareEntry(
					PROFILE_VERSION_CURRENT,curr_grp,ProfileUtilities.isProfileGroupActive(mGlblParms.util,pfla,curr_grp),
					System.currentTimeMillis(),
					PROFILE_TYPE_ACTION,
					dlg_prof_name_et.getText().toString(),
					prof_active,
					c_tgt,c_typ,c_val,ra);
//			ntpli.dumpProfile();
		} else if (act_type.equals(PROFILE_ACTION_TYPE_MESSAGE)) {
			String m_typ=spinnerMessageType.getSelectedItem().toString();
			String m_txt=et_msg_text.getText().toString();
	        String led_color=PROFILE_ACTION_TYPE_MESSAGE_LED_BLUE;
	        if (rb_msg_red.isChecked()) led_color=PROFILE_ACTION_TYPE_MESSAGE_LED_RED;
	        else if (rb_msg_green.isChecked()) led_color=PROFILE_ACTION_TYPE_MESSAGE_LED_GREEN;
			ntpli.setActionMessageEntry(
					PROFILE_VERSION_CURRENT,curr_grp,ProfileUtilities.isProfileGroupActive(mGlblParms.util,pfla,curr_grp),
					System.currentTimeMillis(),
					PROFILE_TYPE_ACTION,
					dlg_prof_name_et.getText().toString(),
					prof_active,
					m_typ,m_txt,cb_vib_used.isChecked(),
					cb_led_used.isChecked(),led_color);
		} else if (act_type.equals(PROFILE_ACTION_TYPE_TIME)) {
			String t_typ=spinnerTimeType.getSelectedItem().toString();
			String t_tgt=spinnerTimeTarget.getSelectedItem().toString();
			ntpli.setActionTimeEntry(
					PROFILE_VERSION_CURRENT,curr_grp,ProfileUtilities.isProfileGroupActive(mGlblParms.util,pfla,curr_grp),
					System.currentTimeMillis(),
					PROFILE_TYPE_ACTION,
					dlg_prof_name_et.getText().toString(),
					prof_active,
					t_typ, t_tgt);
		} else if (act_type.equals(PROFILE_ACTION_TYPE_TASK)) {
			String t_typ=spinnerTaskType.getSelectedItem().toString();
			String t_tgt=spinnerTaskTarget.getSelectedItem().toString();
			ntpli.setActionTaskEntry(
					PROFILE_VERSION_CURRENT,curr_grp,ProfileUtilities.isProfileGroupActive(mGlblParms.util,pfla,curr_grp),
					System.currentTimeMillis(),
					PROFILE_TYPE_ACTION,
					dlg_prof_name_et.getText().toString(),
					prof_active,
					t_typ, t_tgt);
		} else if (act_type.equals(PROFILE_ACTION_TYPE_WAIT)) {
	        final Spinner spinnerWaitTarget = (Spinner) dialog.findViewById(R.id.edit_profile_action_wait_target);
		    final Spinner spinnerWaitTimeoutType = (Spinner) dialog.findViewById(R.id.edit_profile_action_wait_timeout);
		    final Spinner spinnerWaitTimeoutValue = (Spinner) dialog.findViewById(R.id.edit_profile_action_wait_timeout_value);
		    final Spinner spinnerWaitTimeoutUnits = (Spinner) dialog.findViewById(R.id.edit_profile_action_wait_timeout_units);
			String w_tgt=spinnerWaitTarget.getSelectedItem().toString();
			String w_tov="";
			String w_tou="";
			if (spinnerWaitTimeoutType.getSelectedItem().toString().equals(PROFILE_ACTION_TYPE_WAIT_TIMEOUT_TYPE_TIMEOUTIS)) {
				w_tov=spinnerWaitTimeoutValue.getSelectedItem().toString();
				w_tou=spinnerWaitTimeoutUnits.getSelectedItem().toString();
			}
			ntpli.setActionWaitEntry(
					PROFILE_VERSION_CURRENT,curr_grp,ProfileUtilities.isProfileGroupActive(mGlblParms.util,pfla,curr_grp),
					System.currentTimeMillis(),
					PROFILE_TYPE_ACTION,
					dlg_prof_name_et.getText().toString(),
					prof_active,
					w_tgt, w_tov, w_tou);
		} else if (act_type.equals(PROFILE_ACTION_TYPE_BSH_SCRIPT)) {
	        final EditText et_bsh_script=(EditText) dialog.findViewById(R.id.edit_profile_action_dlg_bsh_script_text);
			String w_text=et_bsh_script.getText().toString().replaceAll("\t", " ");
			ntpli.setActionBeanShellScriptEntry(
					PROFILE_VERSION_CURRENT,curr_grp,ProfileUtilities.isProfileGroupActive(mGlblParms.util,pfla,curr_grp),
					System.currentTimeMillis(),
					PROFILE_TYPE_ACTION,
					dlg_prof_name_et.getText().toString(),
					prof_active,
					w_text);
		} else if (act_type.equals(PROFILE_ACTION_TYPE_SHELL_COMMAND)) {
	        final EditText et_shell_cmd=(EditText) dialog.findViewById(R.id.edit_profile_action_dlg_shell_cmd_text);
	        final CheckBox cb_shell_cmd_with_su=(CheckBox) dialog.findViewById(R.id.edit_profile_action_dlg_shell_cmd_with_su);
			String w_text=et_shell_cmd.getText().toString().replaceAll("\t", " ");
			ntpli.setActionShellCmdEntry(
					PROFILE_VERSION_CURRENT,curr_grp,ProfileUtilities.isProfileGroupActive(mGlblParms.util,pfla,curr_grp),
					System.currentTimeMillis(),
					PROFILE_TYPE_ACTION,
					dlg_prof_name_et.getText().toString(),
					prof_active,
					w_text, cb_shell_cmd_with_su.isChecked());
		};
		return ntpli;
	};

	final static private void setCompareEditTextAttr(
			GlobalParameters mGlblParms,String c_tgt, EditText et_value1, EditText et_value2) {
		if (c_tgt.equals(PROFILE_ACTION_TYPE_COMPARE_TARGET_BLUETOOTH)) {
			et_value1.setInputType(InputType.TYPE_CLASS_TEXT);
			et_value2.setInputType(InputType.TYPE_CLASS_TEXT);
		} else if (c_tgt.equals(PROFILE_ACTION_TYPE_COMPARE_TARGET_WIFI)) {
			et_value1.setInputType(InputType.TYPE_CLASS_TEXT);
			et_value2.setInputType(InputType.TYPE_CLASS_TEXT);
		} else if (c_tgt.equals(PROFILE_ACTION_TYPE_COMPARE_TARGET_BATTERY)) {
			try {
				Integer.parseInt(et_value1.getText().toString());
			} catch (NumberFormatException e) {
				et_value1.setText("");
			}
			try {
				Integer.parseInt(et_value2.getText().toString());
			} catch (NumberFormatException e) {
				et_value2.setText("");
			}
			et_value1.setInputType(InputType.TYPE_CLASS_NUMBER);
			et_value2.setInputType(InputType.TYPE_CLASS_NUMBER);
		} else if (c_tgt.equals(PROFILE_ACTION_TYPE_COMPARE_TARGET_LIGHT)) {
			try {
				Integer.parseInt(et_value1.getText().toString());
			} catch (NumberFormatException e) {
				et_value1.setText("");
			}
			try {
				Integer.parseInt(et_value2.getText().toString());
			} catch (NumberFormatException e) {
				et_value2.setText("");
			}
			et_value1.setInputType(InputType.TYPE_CLASS_NUMBER);
			et_value2.setInputType(InputType.TYPE_CLASS_NUMBER);
		} else if (c_tgt.equals(PROFILE_ACTION_TYPE_COMPARE_TARGET_TIME)) {
			try {
				Integer.parseInt(et_value1.getText().toString());
			} catch (NumberFormatException e) {
				et_value1.setText("");
			}
			try {
				Integer.parseInt(et_value2.getText().toString());
			} catch (NumberFormatException e) {
				et_value2.setText("");
			}
			et_value1.setInputType(InputType.TYPE_CLASS_NUMBER);
			et_value2.setInputType(InputType.TYPE_CLASS_NUMBER);
		}
	};

	
}

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
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_ACTIVITY;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_BSH_SCRIPT;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_COMPARE;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_MESSAGE;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_MUSIC;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_RINGTONE;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_TASK;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_TIME;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_WAIT;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_DISABLED;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ENABLED;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ERROR_NOTIFICATION_DISABLED;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ERROR_NOTIFICATION_ENABLED;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_TYPE_ACTION;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_TYPE_TASK;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_VERSION_CURRENT;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.TRIGGER_EVENT_CATEGORY_BUILTIN;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.TRIGGER_EVENT_CATEGORY_TASK;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.TRIGGER_EVENT_CATEGORY_TIME;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.TRIGGER_EVENT_TASK;
import static com.sentaroh.android.TaskAutomation.Config.QuickTaskConstants.QUICK_TASK_GROUP_NAME;

import java.util.ArrayList;

import com.sentaroh.android.TaskAutomation.GlobalParameters;
import com.sentaroh.android.TaskAutomation.R;
import com.sentaroh.android.TaskAutomation.Common.ProfileListItem;
import com.sentaroh.android.Utilities.NotifyEvent.NotifyEventListener;
import com.sentaroh.android.Utilities.NotifyEvent;
import com.sentaroh.android.Utilities.ContextMenu.CustomContextMenuItem.CustomContextMenuOnClickListener;
import com.sentaroh.android.Utilities.Dialog.CommonDialog;
import com.sentaroh.android.Utilities.Widget.CustomSpinnerAdapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ProfileMaintenanceTaskProfile extends DialogFragment{
	private final static boolean DEBUG_ENABLE=false;
	private final static String APPLICATION_TAG="ProfileMaintenanceTaskProfile";

	private Dialog mDialog=null;
	private boolean mTerminateRequired=true;
//	private Context mContext=null;
	private ProfileMaintenanceTaskProfile mFragment=null;
	private GlobalParameters mGlblParms=null;

	public static ProfileMaintenanceTaskProfile newInstance() {
		if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"newInstance");
		ProfileMaintenanceTaskProfile frag = new ProfileMaintenanceTaskProfile();
        Bundle bundle = new Bundle();
        frag.setArguments(bundle);
        return frag;
    }
	public ProfileMaintenanceTaskProfile() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"onCreateView");
    	View view=super.onCreateView(inflater, container, savedInstanceState);
    	CommonDialog.setDlgBoxSizeLimit(mDialog,true);
    	return view;
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
	};
	
	@Override
	public void onCancel(DialogInterface di) {
		if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"onCancel");
		if (!mTerminateRequired) {
			final Button btnCancel = (Button) mDialog.findViewById(R.id.edit_profile_task_cancel_btn);
			btnCancel.performClick();
		}
		mFragment.dismiss();
		super.onCancel(di);
	};
	
	@Override
	public void onDismiss(DialogInterface di) {
		if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"onDismiss");
		super.onDismiss(di);
	};

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"onCreateDialog");

    	mDialog=new Dialog(getActivity());
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		if (!mTerminateRequired) {
			initViewWidget();
		}
        return mDialog;
    };
    
    class SavedViewContents {
        CharSequence dlg_prof_name_et;
        int dlg_prof_name_et_spos;
        int dlg_prof_name_et_epos;
        boolean cb_active;
        boolean cb_notification;
        int spinnerTriggerCat;
        int spinnerEvent;
        boolean cb_enable_env_parms;
        
        int spinnerActionProfile;
        int spinnerBuiltinPrimitiveAction;
        int spinnerBuiltinAbortAction;
        int spinnerBuiltinSkipAction;
        int spinnerBuiltinCancelAction;
        int spinnerBuiltinBlockAction;
        int[] lv_act_list=new int[]{-1,-1};
        public ArrayList<TaskActionEditListItem> action_adapter_list=new ArrayList<TaskActionEditListItem>();
        int spinnerSelectAction;

    };
    private SavedViewContents saveViewContents() {
    	SavedViewContents sv=new SavedViewContents();
		final EditText dlg_prof_name_et = (EditText) mDialog.findViewById(R.id.edit_profile_task_profile_et_name);
		final CheckBox cb_active = (CheckBox) mDialog.findViewById(R.id.edit_profile_task_enabled);
		final CheckBox cb_notification = (CheckBox) mDialog.findViewById(R.id.edit_profile_task_error_notification);
		final Spinner spinnerTriggerCat = (Spinner) mDialog.findViewById(R.id.edit_profile_task_exec_trigger_category);
		final Spinner spinnerEvent = (Spinner) mDialog.findViewById(R.id.edit_profile_task_exec_trigger_event);
        final CheckBox cb_enable_env_parms=(CheckBox)mDialog.findViewById(R.id.edit_profile_task_enable_env_parms);

        sv.dlg_prof_name_et=dlg_prof_name_et.getText();
        sv.dlg_prof_name_et_spos=dlg_prof_name_et.getSelectionStart();
        sv.dlg_prof_name_et_epos=dlg_prof_name_et.getSelectionEnd();
        sv.cb_active=cb_active.isChecked();
        sv.cb_notification=cb_notification.isChecked();
        sv.spinnerTriggerCat=spinnerTriggerCat.getSelectedItemPosition();
        sv.spinnerEvent=spinnerEvent.getSelectedItemPosition();
        sv.cb_enable_env_parms=cb_enable_env_parms.isChecked();
        
        final Spinner spinnerActionProfile = (Spinner) mDialog.findViewById(R.id.edit_profile_task_user_actionlist);
        final Spinner spinnerBuiltinPrimitiveAction = (Spinner) mDialog.findViewById(R.id.edit_profile_task_builtin_primitive_actionlist);
        final Spinner spinnerBuiltinAbortAction = (Spinner) mDialog.findViewById(R.id.edit_profile_task_builtin_abort_actionlist);
        final Spinner spinnerBuiltinSkipAction = (Spinner) mDialog.findViewById(R.id.edit_profile_task_builtin_skip_actionlist);
        final Spinner spinnerBuiltinCancelAction = (Spinner) mDialog.findViewById(R.id.edit_profile_task_builtin_cancel_actionlist);
        final Spinner spinnerBuiltinBlockAction = (Spinner) mDialog.findViewById(R.id.edit_profile_task_builtin_block_actionlist);
        final ListView lv_act_list=(ListView)mDialog.findViewById(android.R.id.list);
        final Spinner spinnerSelectAction = (Spinner) mDialog.findViewById(R.id.edit_profile_task_select_action);

        sv.spinnerActionProfile=spinnerActionProfile.getSelectedItemPosition();
        sv.spinnerBuiltinPrimitiveAction= spinnerBuiltinPrimitiveAction.getSelectedItemPosition();
        sv.spinnerBuiltinAbortAction =spinnerBuiltinAbortAction.getSelectedItemPosition();
        sv.spinnerBuiltinSkipAction =spinnerBuiltinSkipAction.getSelectedItemPosition();
        sv.spinnerBuiltinCancelAction = spinnerBuiltinCancelAction.getSelectedItemPosition();
        sv.spinnerBuiltinBlockAction = spinnerBuiltinBlockAction.getSelectedItemPosition();
        sv.lv_act_list[0]=lv_act_list.getFirstVisiblePosition();
        if (lv_act_list.getChildAt(0)!=null) sv.lv_act_list[1]=lv_act_list.getChildAt(0).getTop();
        
        for (int i=0;i<mGlblParms.taskActionListAdapter.getCount();i++)
        	sv.action_adapter_list.add(mGlblParms.taskActionListAdapter.getItem(i));
        
        sv.spinnerSelectAction = spinnerSelectAction.getSelectedItemPosition();
    	
    	return sv;
    }

    private void restoreViewContents(final SavedViewContents sv) {
		final EditText dlg_prof_name_et = (EditText) mDialog.findViewById(R.id.edit_profile_task_profile_et_name);
		final CheckBox cb_active = (CheckBox) mDialog.findViewById(R.id.edit_profile_task_enabled);
		final CheckBox cb_notification = (CheckBox) mDialog.findViewById(R.id.edit_profile_task_error_notification);
		final Spinner spinnerTriggerCat = (Spinner) mDialog.findViewById(R.id.edit_profile_task_exec_trigger_category);
		final Spinner spinnerEvent = (Spinner) mDialog.findViewById(R.id.edit_profile_task_exec_trigger_event);
        final CheckBox cb_enable_env_parms=(CheckBox)mDialog.findViewById(R.id.edit_profile_task_enable_env_parms);
        final Spinner spinnerActionProfile = (Spinner) mDialog.findViewById(R.id.edit_profile_task_user_actionlist);
        final Spinner spinnerBuiltinPrimitiveAction = (Spinner) mDialog.findViewById(R.id.edit_profile_task_builtin_primitive_actionlist);
        final Spinner spinnerBuiltinAbortAction = (Spinner) mDialog.findViewById(R.id.edit_profile_task_builtin_abort_actionlist);
        final Spinner spinnerBuiltinSkipAction = (Spinner) mDialog.findViewById(R.id.edit_profile_task_builtin_skip_actionlist);
        final Spinner spinnerBuiltinCancelAction = (Spinner) mDialog.findViewById(R.id.edit_profile_task_builtin_cancel_actionlist);
        final Spinner spinnerBuiltinBlockAction = (Spinner) mDialog.findViewById(R.id.edit_profile_task_builtin_block_actionlist);
        final ListView lv_act_list=(ListView)mDialog.findViewById(android.R.id.list);
        final Spinner spinnerSelectAction = (Spinner) mDialog.findViewById(R.id.edit_profile_task_select_action);

    	Handler hndl1=new Handler();
    	hndl1.postDelayed(new Runnable(){
			@Override
			public void run() {
				dlg_prof_name_et.setText(sv.dlg_prof_name_et);
				dlg_prof_name_et.setSelection(sv.dlg_prof_name_et_spos,sv.dlg_prof_name_et_epos);
				cb_active.setChecked(sv.cb_active);
				cb_notification.setChecked(sv.cb_notification);
				if (spinnerTriggerCat.getSelectedItemPosition()!=sv.spinnerTriggerCat) {
					spinnerTriggerCat.setSelection(sv.spinnerTriggerCat);
				}
		    	Handler hndl2=new Handler();
		    	hndl2.postDelayed(new Runnable(){
					@Override
					public void run() {
						spinnerEvent.setSelection(sv.spinnerEvent);

						lv_act_list.setSelectionFromTop(sv.lv_act_list[0],sv.lv_act_list[1]);
				        for (int i=0;i<mGlblParms.taskActionListAdapter.getCount();i++)
				        	mGlblParms.taskActionListAdapter.remove(0);
				        for (int i=0;i<sv.action_adapter_list.size();i++)
				        	mGlblParms.taskActionListAdapter.add(sv.action_adapter_list.get(i));
				        mGlblParms.taskActionListAdapter.notifyDataSetChanged();
					}
		    	},50);
				cb_enable_env_parms.setChecked(sv.cb_enable_env_parms);
				
		        spinnerActionProfile.setSelection(sv.spinnerActionProfile);
		        spinnerBuiltinPrimitiveAction.setSelection(sv.spinnerBuiltinPrimitiveAction);
		        spinnerBuiltinAbortAction.setSelection(sv.spinnerBuiltinAbortAction);
		        spinnerBuiltinSkipAction.setSelection(sv.spinnerBuiltinSkipAction);
		        spinnerBuiltinCancelAction.setSelection(sv.spinnerBuiltinCancelAction);
		        spinnerBuiltinBlockAction.setSelection(sv.spinnerBuiltinBlockAction);
		        spinnerSelectAction.setSelection(sv.spinnerSelectAction);
			}
    	},50);
    };
    
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
			final ProfileListItem tpli, NotifyEvent nc) {
    	if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"showDialog");
    	mTerminateRequired=false;
    	mOpType=op_type;
    	mCurrentGroup=c_grp;
    	mCurrentProfileListItem=tpli;
    	mNotifyCompletion=nc;
	    FragmentTransaction ft = fm.beginTransaction();
	    ft.add(frag,null);
	    ft.commitAllowingStateLoss();
//    	show(fm,APPLICATION_TAG);
    };

    final private void addProfile() {
		mDialog.setContentView(R.layout.edit_profile_task_dlg);
		final TextView dlg_title = (TextView) mDialog.findViewById(R.id.edit_profile_task_title);
		final EditText dlg_prof_name_et = (EditText) mDialog.findViewById(R.id.edit_profile_task_profile_et_name);
		final CheckBox cb_active = (CheckBox) mDialog.findViewById(R.id.edit_profile_task_enabled);
		final CheckBox cb_notification = (CheckBox) mDialog.findViewById(R.id.edit_profile_task_error_notification);
		final Spinner spinnerTriggerCat = (Spinner) mDialog.findViewById(R.id.edit_profile_task_exec_trigger_category);
		CustomSpinnerAdapter adapterTriggerCat = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
		final Spinner spinnerEvent = (Spinner) mDialog.findViewById(R.id.edit_profile_task_exec_trigger_event);
		final CustomSpinnerAdapter adapterEvent = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
		final Button btnCancel = (Button) mDialog.findViewById(R.id.edit_profile_task_cancel_btn);
		final Button btnOK = (Button) mDialog.findViewById(R.id.edit_profile_task_ok_btn);
//		final Button btnEdit = (Button) mDialog.findViewById(R.id.edit_profile_task_test_edit_parms);
        final CheckBox cb_enable_env_parms=(CheckBox)mDialog.findViewById(R.id.edit_profile_task_enable_env_parms);
		final Button btnExecute = (Button) mDialog.findViewById(R.id.edit_profile_task_test_exec);
		final Button btnEnvEdit = (Button) mDialog.findViewById(R.id.edit_profile_task_test_edit_parms);

		dlg_title.setText(mGlblParms.context.getString(R.string.msgs_edit_profile_hdr_add_task));
		
//		CommonDialog.setDlgBoxSizeLimit(mDialog,true);
		
		cb_active.setChecked(true);
		cb_notification.setChecked(false);

        ProfileMaintenance.setSpinnerTriggerCat(mGlblParms,mDialog,mGlblParms.profileAdapter,mGlblParms.profileListView,spinnerTriggerCat,adapterTriggerCat,"");
		
//        setSpinnerEvent(mDialog,mGlblParms.profileAdapter,mGlblParms.profileListView,spinnerEvent,adapterEvent,"");
        spinnerTriggerCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			final public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				if (spinnerTriggerCat.getSelectedItem().toString().equals(TRIGGER_EVENT_CATEGORY_BUILTIN)) {
					ProfileMaintenance.setSpinnerEventBuiltin(mGlblParms,mDialog,mGlblParms.profileAdapter,mGlblParms.profileListView,spinnerEvent,adapterEvent,"");
				} else if (spinnerTriggerCat.getSelectedItem().toString().equals(TRIGGER_EVENT_CATEGORY_TASK)) {
					ProfileMaintenance.setSpinnerEventTask(mGlblParms,mDialog,mGlblParms.profileAdapter,mGlblParms.profileListView,spinnerEvent,adapterEvent,"");
				} else if (spinnerTriggerCat.getSelectedItem().toString().equals(TRIGGER_EVENT_CATEGORY_TIME)) {
					ProfileMaintenance.setSpinnerEventTime(mGlblParms,mDialog,mGlblParms.profileAdapter,mGlblParms.profileListView,spinnerEvent,adapterEvent,"");
				}
			}
			@Override
			final public void onNothingSelected(AdapterView<?> arg0) {}
        });

        final ArrayList<TaskActionEditListItem>adapter_act_list=new ArrayList<TaskActionEditListItem>();
        setEditTaskActionListener(mGlblParms,mDialog,mGlblParms.profileAdapter, mGlblParms.profileListView, mCurrentGroup, adapter_act_list) ;

        if (mGlblParms.immTaskTestEnvParms==null) ProfileMaintenance.loadEnvparmsFromService(mGlblParms);
        btnEnvEdit.setVisibility(Button.GONE);
        cb_enable_env_parms.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if (isChecked) btnEnvEdit.setVisibility(Button.VISIBLE);
				else btnEnvEdit.setVisibility(Button.GONE);
			}
        });

		// Executeボタンの指定
		btnExecute.setOnClickListener(new View.OnClickListener() {
			final public void onClick(View v) {
				dlg_prof_name_et.selectAll();
				String audit_msg=ProfileMaintenance.auditProfileName(mGlblParms,mGlblParms.profileAdapter,mCurrentGroup,PROFILE_TYPE_TASK,
						dlg_prof_name_et.getText().toString());
				if (!audit_msg.equals("")) {
					mGlblParms.commonDlg.showCommonDialog(false, "E", audit_msg, "", null);
					return;
				}
				if (spinnerEvent.getSelectedItem().toString().equals("** Profile not available **")) {
					mGlblParms.commonDlg.showCommonDialog(false, "E",  
							mGlblParms.context.getString(R.string.msgs_edit_profile_event_not_specified), "", null);
					return;
				}
				if (adapter_act_list.size()==0) {
					mGlblParms.commonDlg.showCommonDialog(false, "E",  
							mGlblParms.context.getString(R.string.msgs_edit_profile_action_not_specified), "", null);
					return;
				}
				ProfileMaintenance.invokeTaskExecution(mGlblParms,mCurrentGroup,mGlblParms.profileAdapter,dlg_prof_name_et.getText().toString(),
						spinnerEvent.getSelectedItem().toString(),
						cb_notification.isChecked(),cb_enable_env_parms.isChecked(),adapter_act_list);
			}
		});
		// EnvEditボタンの指定
		btnEnvEdit.setOnClickListener(new View.OnClickListener() {
			final public void onClick(View v) {
				ProfileMaintenance.editEnvParmsDlg(mGlblParms);
			}
		});
		// CANCELボタンの指定
		btnCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
//				mDialog.dismiss();
				mFragment.dismiss();
			}
		});
		// OKボタンの指定
		btnOK.setOnClickListener(new View.OnClickListener() {
			final public void onClick(View v) {
				dlg_prof_name_et.selectAll();
				String audit_msg=ProfileMaintenance.auditProfileName(mGlblParms,mGlblParms.profileAdapter,mCurrentGroup,PROFILE_TYPE_TASK,
						dlg_prof_name_et.getText().toString());
				if (!audit_msg.equals("")) {
					mGlblParms.commonDlg.showCommonDialog(false, "E",  
							audit_msg, "", null);
					return;
				} 
				if (spinnerEvent.getSelectedItem().toString().equals("** Profile not available **")) {
					mGlblParms.commonDlg.showCommonDialog(false, "E",  
							mGlblParms.context.getString(R.string.msgs_edit_profile_event_not_specified), "", null);
					return;
				}
				if (adapter_act_list.size()==0) {
					mGlblParms.commonDlg.showCommonDialog(false, "E",  
							mGlblParms.context.getString(R.string.msgs_edit_profile_action_not_specified), "", null);
					return;
				}

				mFragment.dismiss();
				String prof_active, prof_retrospec="",prof_notification="";
				if (cb_active.isChecked()) prof_active=PROFILE_ENABLED;
				else prof_active=PROFILE_DISABLED;
				
				if (cb_notification.isChecked()) prof_notification=PROFILE_ERROR_NOTIFICATION_ENABLED;
				else prof_notification=PROFILE_ERROR_NOTIFICATION_ENABLED;

				ProfileUtilities.removeDummyProfile(mGlblParms.profileAdapter,mCurrentGroup);

				ProfileListItem ntpli=new ProfileListItem();
				ArrayList<String> trig=new ArrayList<String>();
//				act.add(spinnerActionProfile.getSelectedItem().toString());
				trig.add(spinnerEvent.getSelectedItem().toString());
				ArrayList<String>string_act_list=new ArrayList<String>();
				for (int i=0;i<adapter_act_list.size();i++) string_act_list.add(adapter_act_list.get(i).action);

				ntpli.setTaskEntry(
						PROFILE_VERSION_CURRENT,mCurrentGroup,
						ProfileUtilities.isProfileGroupActive(mGlblParms.util,mGlblParms.profileAdapter,mCurrentGroup),
						System.currentTimeMillis(),
						PROFILE_TYPE_TASK,
						dlg_prof_name_et.getText().toString(),
						prof_active,
						prof_retrospec,
						"0",
						prof_notification,
						string_act_list,
						trig);
				mGlblParms.profileAdapter.addProfItem(ntpli);
				mGlblParms.profileAdapter.sort();
				mGlblParms.profileAdapter.updateShowList();
				mGlblParms.profileAdapter.notifyDataSetChanged();
				ProfileMaintenance.putProfileListToService(mGlblParms,mGlblParms.profileAdapter,
						ProfileUtilities.isProfileGroupActive(mGlblParms.util,mGlblParms.profileAdapter,mCurrentGroup));
				if (mNotifyCompletion!=null) mNotifyCompletion.notifyToListener(true, null);
			}
		});

    };

    final private void browseProfile() {
		mDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		mDialog.setContentView(R.layout.edit_profile_task_dlg);
		final TextView dlg_title = (TextView) mDialog.findViewById(R.id.edit_profile_task_title);
		final TextView dlg_title_sub = (TextView) mDialog.findViewById(R.id.edit_profile_task_title_sub);
		final EditText dlg_prof_name_et=(EditText)mDialog.findViewById(R.id.edit_profile_task_profile_et_name);
		dlg_prof_name_et.setVisibility(EditText.GONE);
		dlg_title.setText(mGlblParms.context.getString(R.string.msgs_edit_profile_hdr_browse_task));
		dlg_title_sub.setText("("+mCurrentProfileListItem.getProfileName()+")");

		final CheckBox cb_active=(CheckBox)mDialog.findViewById(R.id.edit_profile_task_enabled);
		cb_active.setClickable(false);
		final CheckBox cb_notification=(CheckBox)mDialog.findViewById(R.id.edit_profile_task_error_notification);
		cb_notification.setClickable(false);
		
        final Button btnOK = (Button) mDialog.findViewById(R.id.edit_profile_task_ok_btn);
		final Button btnCancel = (Button) mDialog.findViewById(R.id.edit_profile_task_cancel_btn);
		btnOK.setVisibility(Button.GONE);
		
//		CommonDialog.setDlgBoxSizeLimit(mDialog,true);
		
		if (mCurrentProfileListItem.isProfileEnabled()) cb_active.setChecked(true);
		else cb_active.setChecked(false);

		if (mCurrentProfileListItem.isProfileErrorNotificationEnabled()) cb_notification.setChecked(true);
		else cb_notification.setChecked(false);

        final Spinner spinnerTriggerCat = (Spinner) mDialog.findViewById(R.id.edit_profile_task_exec_trigger_category);
        CustomSpinnerAdapter adapterTriggerCat = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        String trig=TRIGGER_EVENT_CATEGORY_BUILTIN;
        if (mCurrentProfileListItem.getTaskTriggerList().get(0).equals(TRIGGER_EVENT_TASK)) trig=TRIGGER_EVENT_CATEGORY_TASK;
        else if (!mCurrentProfileListItem.getTaskTriggerList().get(0).startsWith(BUILTIN_PREFIX)) trig=TRIGGER_EVENT_CATEGORY_TIME;
        ProfileMaintenance.setSpinnerTriggerCat(mGlblParms,mDialog,mGlblParms.profileAdapter,mGlblParms.profileListView,spinnerTriggerCat,adapterTriggerCat,trig);
        spinnerTriggerCat.setClickable(false);
		
        final Spinner spinnerEvent = (Spinner) mDialog.findViewById(R.id.edit_profile_task_exec_trigger_event);
        final CustomSpinnerAdapter adapterEvent = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
//        setSpinnerEvent(mGlblParms,mDialog,mGlblParms.profileAdapter,mGlblParms.profileListView,spinnerEvent,adapterEvent,"");
        spinnerTriggerCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			final public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				if (spinnerTriggerCat.getSelectedItem().toString().equals(TRIGGER_EVENT_CATEGORY_BUILTIN)) {
					ProfileMaintenance.setSpinnerEventBuiltin(mGlblParms,mDialog,mGlblParms.profileAdapter,mGlblParms.profileListView,spinnerEvent,adapterEvent,mCurrentProfileListItem.getTaskTriggerList().get(0));
				} else if (spinnerTriggerCat.getSelectedItem().toString().equals(TRIGGER_EVENT_CATEGORY_TASK)) {
					ProfileMaintenance.setSpinnerEventTask(mGlblParms,mDialog,mGlblParms.profileAdapter,mGlblParms.profileListView,spinnerEvent,adapterEvent,mCurrentProfileListItem.getTaskTriggerList().get(0));
				} else if (spinnerTriggerCat.getSelectedItem().toString().equals(TRIGGER_EVENT_CATEGORY_TIME)) {
					ProfileMaintenance.setSpinnerEventTime(mGlblParms,mDialog,mGlblParms.profileAdapter,mGlblParms.profileListView,spinnerEvent,adapterEvent,mCurrentProfileListItem.getTaskTriggerList().get(0));
				}
			}
			@Override
			final public void onNothingSelected(AdapterView<?> arg0) {}
        });
        spinnerEvent.setClickable(false);

        ArrayList<TaskActionEditListItem>adapter_act_list=
        		createTaskActionEditList(mGlblParms,mDialog, mGlblParms.profileAdapter,mCurrentGroup,mCurrentProfileListItem.getTaskActionList());
        final Spinner spinnerActionProfile = (Spinner) mDialog.findViewById(R.id.edit_profile_task_user_actionlist);
        final Spinner spinnerBuiltinPrimitiveAction = (Spinner) mDialog.findViewById(R.id.edit_profile_task_builtin_primitive_actionlist);
        final Spinner spinnerBuiltinAbortAction = (Spinner) mDialog.findViewById(R.id.edit_profile_task_builtin_abort_actionlist);
        final Spinner spinnerBuiltinSkipAction = (Spinner) mDialog.findViewById(R.id.edit_profile_task_builtin_skip_actionlist);
        final Spinner spinnerBuiltinCancelAction = (Spinner) mDialog.findViewById(R.id.edit_profile_task_builtin_cancel_actionlist);
        final Spinner spinnerBuiltinBlockAction = (Spinner) mDialog.findViewById(R.id.edit_profile_task_builtin_block_actionlist);
        final Spinner spinnerSelectAction = (Spinner) mDialog.findViewById(R.id.edit_profile_task_select_action);
        final TextView tv_cat = (TextView) mDialog.findViewById(R.id.edit_profile_task_action_category);
        spinnerActionProfile.setVisibility(Spinner.GONE);
        spinnerBuiltinPrimitiveAction.setVisibility(Spinner.GONE);
        spinnerBuiltinAbortAction.setVisibility(Spinner.GONE);
        spinnerBuiltinSkipAction.setVisibility(Spinner.GONE);
        spinnerBuiltinBlockAction.setVisibility(Spinner.GONE);
        spinnerBuiltinCancelAction.setVisibility(Spinner.GONE);
        spinnerSelectAction.setVisibility(Spinner.GONE);
        tv_cat.setVisibility(TextView.GONE);
        
        final ListView lv_act_list=(ListView)mDialog.findViewById(android.R.id.list);
        mGlblParms.taskActionListAdapter=new AdapterTaskActionEditList(mGlblParms.context,
        		R.layout.task_action_list_item, adapter_act_list);
        lv_act_list.setAdapter(mGlblParms.taskActionListAdapter);
        lv_act_list.setEnabled(true);
        lv_act_list.setSelected(true);
    
        Button btnExec = (Button) mDialog.findViewById(R.id.edit_profile_task_test_exec);
        btnExec.setVisibility(Button.GONE);
		final Button btnEdit = (Button) mDialog.findViewById(R.id.edit_profile_task_test_edit_parms);
        final CheckBox cb_enable_env_parms=(CheckBox)mDialog.findViewById(R.id.edit_profile_task_enable_env_parms);
        btnEdit.setVisibility(Button.GONE);
        cb_enable_env_parms.setVisibility(CheckBox.GONE);
		// CANCELボタンの指定
		btnCancel.setOnClickListener(new View.OnClickListener() {
			final public void onClick(View v) {
				mFragment.dismiss();
			}
		});
    	
    };
    
    final private void editProfile() {
//		mDialog.getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		mDialog.setContentView(R.layout.edit_profile_task_dlg);
		final TextView dlg_title = (TextView) mDialog.findViewById(R.id.edit_profile_task_title);
		final TextView dlg_title_sub = (TextView) mDialog.findViewById(R.id.edit_profile_task_title_sub);
		final EditText dlg_prof_name_et=(EditText)mDialog.findViewById(R.id.edit_profile_task_profile_et_name);
		dlg_prof_name_et.setVisibility(EditText.GONE);
		dlg_prof_name_et.setText(mCurrentProfileListItem.getProfileName());
		dlg_title.setText(mGlblParms.context.getString(R.string.msgs_edit_profile_hdr_edit_task));
		dlg_title_sub.setText("("+mCurrentProfileListItem.getProfileName()+")");

		final CheckBox cb_active = (CheckBox) mDialog.findViewById(R.id.edit_profile_task_enabled);
		final CheckBox cb_notification = (CheckBox) mDialog.findViewById(R.id.edit_profile_task_error_notification);
		final Spinner spinnerTriggerCat = (Spinner) mDialog.findViewById(R.id.edit_profile_task_exec_trigger_category);
		CustomSpinnerAdapter adapterTriggerCat = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
		final Spinner spinnerEvent = (Spinner) mDialog.findViewById(R.id.edit_profile_task_exec_trigger_event);
		final CustomSpinnerAdapter adapterEvent = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
		final Button btnCancel = (Button) mDialog.findViewById(R.id.edit_profile_task_cancel_btn);
		final Button btnOK = (Button) mDialog.findViewById(R.id.edit_profile_task_ok_btn);
		final Button btnExecute = (Button) mDialog.findViewById(R.id.edit_profile_task_test_exec);
		final Button btnEnvEdit = (Button) mDialog.findViewById(R.id.edit_profile_task_test_edit_parms);
		final CheckBox cb_enable_env_parms = (CheckBox) mDialog.findViewById(R.id.edit_profile_task_enable_env_parms);

//		CommonDialog.setDlgBoxSizeLimit(mDialog, true);

		if (mCurrentProfileListItem.isProfileEnabled()) cb_active.setChecked(true);
		else cb_active.setChecked(false);

		if (mCurrentProfileListItem.isProfileErrorNotificationEnabled()) cb_notification.setChecked(true);
		else cb_notification.setChecked(false);

		String trig = TRIGGER_EVENT_CATEGORY_BUILTIN;
		if (mCurrentProfileListItem.getTaskTriggerList().get(0).equals(TRIGGER_EVENT_TASK))
			trig = TRIGGER_EVENT_CATEGORY_TASK;
		else if (!mCurrentProfileListItem.getTaskTriggerList().get(0).startsWith(BUILTIN_PREFIX))
			trig = TRIGGER_EVENT_CATEGORY_TIME;
		ProfileMaintenance.setSpinnerTriggerCat(mGlblParms, mDialog,
				mGlblParms.profileAdapter, mGlblParms.profileListView,
				spinnerTriggerCat, adapterTriggerCat, trig);

		spinnerTriggerCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			final public void onItemSelected(AdapterView<?> arg0,
					View arg1, int arg2, long arg3) {
				if (spinnerTriggerCat.getSelectedItem().toString().equals(TRIGGER_EVENT_CATEGORY_BUILTIN)) {
					ProfileMaintenance.setSpinnerEventBuiltin(
							mGlblParms, mDialog,
							mGlblParms.profileAdapter,
							mGlblParms.profileListView, spinnerEvent,
							adapterEvent, mCurrentProfileListItem.getTaskTriggerList().get(0));
				} else if (spinnerTriggerCat.getSelectedItem().toString().equals(TRIGGER_EVENT_CATEGORY_TASK)) {
					ProfileMaintenance.setSpinnerEventTask(mGlblParms,
							mDialog, mGlblParms.profileAdapter,
							mGlblParms.profileListView, spinnerEvent,
							adapterEvent, mCurrentProfileListItem.getTaskTriggerList().get(0));
				} else if (spinnerTriggerCat.getSelectedItem().toString().equals(TRIGGER_EVENT_CATEGORY_TIME)) {
					ProfileMaintenance.setSpinnerEventTime(mGlblParms,
							mDialog, mGlblParms.profileAdapter,
							mGlblParms.profileListView, spinnerEvent,
							adapterEvent, mCurrentProfileListItem.getTaskTriggerList().get(0));
				}
			}

			@Override
			final public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		final ArrayList<TaskActionEditListItem> act_list = createTaskActionEditList(mGlblParms, mDialog,
						mGlblParms.profileAdapter, mCurrentGroup,
						mCurrentProfileListItem.getTaskActionList());
		setEditTaskActionListener(mGlblParms, mDialog,
				mGlblParms.profileAdapter, mGlblParms.profileListView,
				mCurrentProfileListItem.getProfileGroup(), act_list);

		if (mGlblParms.immTaskTestEnvParms == null)
			ProfileMaintenance.loadEnvparmsFromService(mGlblParms);

		btnEnvEdit.setVisibility(Button.GONE);
		cb_enable_env_parms.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0,
					boolean isChecked) {
				if (isChecked)
					btnEnvEdit.setVisibility(Button.VISIBLE);
				else
					btnEnvEdit.setVisibility(Button.GONE);
			}
		});
		// Executeボタンの指定
		btnExecute.setOnClickListener(new View.OnClickListener() {
			@Override
			final public void onClick(View v) {
				if (spinnerEvent.getSelectedItem().toString()
						.equals("** Profile not available **")) {
					mGlblParms.commonDlg
							.showCommonDialog(false,"E",
							mGlblParms.context.getString(R.string.msgs_edit_profile_event_not_specified),
							"", null);
					return;
				}
				if (act_list.size() == 0) {
					mGlblParms.commonDlg
					.showCommonDialog(false,"E",
							mGlblParms.context.getString(R.string.msgs_edit_profile_action_not_specified),
							"", null);
					return;
				}
				ProfileMaintenance.invokeTaskExecution(mGlblParms,
						mCurrentGroup, mGlblParms.profileAdapter,
						dlg_prof_name_et.getText().toString(), spinnerEvent
								.getSelectedItem().toString(), cb_notification
								.isChecked(), cb_enable_env_parms.isChecked(),
						act_list);
			}
		});
		// Editボタンの指定
		btnEnvEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			final public void onClick(View v) {
				ProfileMaintenance.editEnvParmsDlg(mGlblParms);
			}
		});
		// CANCELボタンの指定
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			final public void onClick(View v) {
				mFragment.dismiss();
			}
		});
		// OKボタンの指定
		btnOK.setOnClickListener(new View.OnClickListener() {
			@Override
			final public void onClick(View v) {
				if (spinnerEvent.getSelectedItem().toString()
						.equals("** Profile not available **")) {
					mGlblParms.commonDlg.showCommonDialog(false,"E",
							mGlblParms.context.getString(R.string.msgs_edit_profile_event_not_specified),
							"", null);
					return;
				}
				if (act_list.size() == 0) {
					mGlblParms.commonDlg.showCommonDialog(false,"E",mGlblParms.context.getString(R.string.msgs_edit_profile_action_not_specified),
							 "", null);
					return;
				}
				mFragment.dismiss();
				String prof_active, prof_retrospec = "", prof_notification = "";
				if (cb_active.isChecked())
					prof_active = PROFILE_ENABLED;
				else
					prof_active = PROFILE_DISABLED;

				if (cb_notification.isChecked())
					prof_notification = PROFILE_ERROR_NOTIFICATION_ENABLED;
				else
					prof_notification = PROFILE_ERROR_NOTIFICATION_DISABLED;

				ProfileListItem new_tpli = new ProfileListItem();
				ArrayList<String> trig = new ArrayList<String>();
				trig.add(spinnerEvent.getSelectedItem().toString());

				ArrayList<String> string_act_list = new ArrayList<String>();
				for (int i = 0; i < act_list.size(); i++)
					string_act_list.add(act_list.get(i).action);
				new_tpli.setTaskEntry(PROFILE_VERSION_CURRENT,
						mCurrentProfileListItem.getProfileGroup(),
						mCurrentProfileListItem.isProfileGroupActivated(),
						System.currentTimeMillis(), PROFILE_TYPE_TASK,
						dlg_prof_name_et.getText().toString(), prof_active,
						prof_retrospec, "0", prof_notification,
						string_act_list, trig);
				new_tpli.setProfileGroupShowed(mCurrentProfileListItem.isProfileGroupShowed());
				new_tpli.setTaskActive(mCurrentProfileListItem.isTaskActive());
				mGlblParms.profileAdapter.replaceProfItem(new_tpli);
				ProfileMaintenance.putProfileListToService(mGlblParms,
						mGlblParms.profileAdapter, ProfileUtilities
								.isProfileGroupActive(mGlblParms.util,
										mGlblParms.profileAdapter,
										mCurrentGroup));
			}
		});
    };
	
    final static private void setEditTaskActionListener(
    		final GlobalParameters mGlblParms, final Dialog dialog, 
			final AdapterProfileList pfla, ListView pflv, final String curr_grp, 
			final ArrayList<TaskActionEditListItem> adapter_act_list) {
        final Spinner spinnerActionProfile = (Spinner) dialog.findViewById(R.id.edit_profile_task_user_actionlist);
        CustomSpinnerAdapter adapterActionProfile = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        ProfileMaintenance.setSpinnerActionProfile(mGlblParms,dialog,pfla,pflv,spinnerActionProfile,adapterActionProfile);
        if (adapterActionProfile.getCount()==0) spinnerActionProfile.setEnabled(false);

        final Spinner spinnerBuiltinPrimitiveAction = (Spinner) dialog.findViewById(R.id.edit_profile_task_builtin_primitive_actionlist);
        CustomSpinnerAdapter adapterBuiltinPrimitiveAction = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        ProfileMaintenance.setSpinnerBuiltinPrimitiveAction(mGlblParms,dialog,spinnerBuiltinPrimitiveAction,adapterBuiltinPrimitiveAction);
        if (adapterBuiltinPrimitiveAction.getCount()==0) spinnerBuiltinPrimitiveAction.setEnabled(false);

        final Spinner spinnerBuiltinAbortAction = (Spinner) dialog.findViewById(R.id.edit_profile_task_builtin_abort_actionlist);
        CustomSpinnerAdapter adapterBuiltinAbortAction = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        ProfileMaintenance.setSpinnerBuiltinAbortAction(mGlblParms,dialog,spinnerBuiltinAbortAction,adapterBuiltinAbortAction);
        if (adapterBuiltinAbortAction.getCount()==0) spinnerBuiltinAbortAction.setEnabled(false);

        final Spinner spinnerBuiltinSkipAction = (Spinner) dialog.findViewById(R.id.edit_profile_task_builtin_skip_actionlist);
        CustomSpinnerAdapter adapterBuiltinSkipAction = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        ProfileMaintenance.setSpinnerBuiltinSkipAction(mGlblParms,dialog,spinnerBuiltinSkipAction,adapterBuiltinSkipAction);
        if (adapterBuiltinSkipAction.getCount()==0) spinnerBuiltinSkipAction.setEnabled(false);
        
        final Spinner spinnerBuiltinCancelAction = (Spinner) dialog.findViewById(R.id.edit_profile_task_builtin_cancel_actionlist);
        CustomSpinnerAdapter adapterBuiltinCancelAction = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        ProfileMaintenance.setSpinnerBuiltinCancelAction(mGlblParms,dialog,spinnerBuiltinCancelAction,adapterBuiltinCancelAction);
        if (adapterBuiltinCancelAction.getCount()==0) spinnerBuiltinCancelAction.setEnabled(false);

        final Spinner spinnerBuiltinBlockAction = (Spinner) dialog.findViewById(R.id.edit_profile_task_builtin_block_actionlist);
        CustomSpinnerAdapter adapterBuiltinBlockAction = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        ProfileMaintenance.setSpinnerBuiltinBlockAction(mGlblParms,dialog,spinnerBuiltinBlockAction,adapterBuiltinBlockAction);
        if (adapterBuiltinBlockAction.getCount()==0) spinnerBuiltinBlockAction.setEnabled(false);

        final ListView lv_act_list=(ListView)dialog.findViewById(android.R.id.list);
        mGlblParms.taskActionListAdapter=new AdapterTaskActionEditList(mGlblParms.context,
        		R.layout.task_action_list_item, adapter_act_list);
        lv_act_list.setAdapter(mGlblParms.taskActionListAdapter);
        lv_act_list.setEnabled(true);
        lv_act_list.setSelected(true);

        lv_act_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			final public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				if (!mGlblParms.taskActionListAdapter.getItem(position).action.startsWith(BUILTIN_PREFIX) &&
						!curr_grp.equals(QUICK_TASK_GROUP_NAME)) {
					for (int i=0;i<pfla.getDataListCount();i++) {
						if (pfla.getDataListItem(i).getProfileType().equals(PROFILE_TYPE_ACTION)&&
							pfla.getDataListItem(i).getProfileGroup().equals(curr_grp) &&
							pfla.getDataListItem(i).getProfileName().equals(mGlblParms.taskActionListAdapter.getItem(position).action)) {
							NotifyEvent ntfy=new NotifyEvent(mGlblParms.context);
							ntfy.setListener(new NotifyEventListener(){
								@Override
								public void positiveResponse(Context c,Object[] o) {
									updateTaskActionEditListDescription(mGlblParms,dialog,pfla,curr_grp, adapter_act_list);
									mGlblParms.taskActionListAdapter.notifyDataSetChanged();
								}
								@Override
								public void negativeResponse(Context c,Object[] o) {}
							});
							ProfileMaintenanceActionProfile pmap=ProfileMaintenanceActionProfile.newInstance();
							pmap.showDialog(mGlblParms.frgamentMgr, pmap, "EDIT",
									curr_grp,pfla.getDataListItem(i), ntfy);
							break;
						}
					}
				}
			}
        });

        lv_act_list.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			final public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				createTaskActionListContextMenu(mGlblParms,dialog,curr_grp,arg2,pfla);
				return true;
			}
        });
        
		// Add user actionボタンの指定
        spinnerActionProfile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			final public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				if (arg2!=0) {
					TaskActionEditListItem taeli=new TaskActionEditListItem();
					taeli.action=(String)spinnerActionProfile.getSelectedItem();
					mGlblParms.taskActionListAdapter.add(taeli);
					updateTaskActionEditListDescription(mGlblParms,dialog,pfla,curr_grp, adapter_act_list);
					mGlblParms.taskActionListAdapter.notifyDataSetChanged();
					lv_act_list.setSelection(mGlblParms.taskActionListAdapter.getCount()-1);
					arg0.setSelection(0);
				}
			}
			@Override
			final public void onNothingSelected(AdapterView<?> arg0) {}
        });
		
		// Add builtin action primitiveボタンの指定
        spinnerBuiltinPrimitiveAction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			final public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				if (arg2!=0) {
					TaskActionEditListItem taeli=new TaskActionEditListItem();
					taeli.action=(String)spinnerBuiltinPrimitiveAction.getSelectedItem();
					mGlblParms.taskActionListAdapter.add(taeli);
					mGlblParms.taskActionListAdapter.notifyDataSetChanged();
					lv_act_list.setSelection(mGlblParms.taskActionListAdapter.getCount()-1);
					arg0.setSelection(0);
				}
			}
			@Override
			final public void onNothingSelected(AdapterView<?> arg0) {}
        	
        });

		// Add builtin action abortボタンの指定
        spinnerBuiltinAbortAction.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			final public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				if (arg2!=0) {
					TaskActionEditListItem taeli=new TaskActionEditListItem();
					taeli.action=(String)spinnerBuiltinAbortAction.getSelectedItem();
					mGlblParms.taskActionListAdapter.add(taeli);
					mGlblParms.taskActionListAdapter.notifyDataSetChanged();
					lv_act_list.setSelection(mGlblParms.taskActionListAdapter.getCount()-1);
					arg0.setSelection(0);
				}
			}
			@Override
			final public void onNothingSelected(AdapterView<?> arg0) {}
        });

		// Add builtin action skipボタンの指定
        spinnerBuiltinSkipAction.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			final public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				if (arg2!=0) {
					TaskActionEditListItem taeli=new TaskActionEditListItem();
					taeli.action=(String)spinnerBuiltinSkipAction.getSelectedItem();
					mGlblParms.taskActionListAdapter.add(taeli);
					mGlblParms.taskActionListAdapter.notifyDataSetChanged();
					lv_act_list.setSelection(mGlblParms.taskActionListAdapter.getCount()-1);
					arg0.setSelection(0);
				}
			}
			@Override
			final public void onNothingSelected(AdapterView<?> arg0) {}
        });
        
		// Add builtin action cancelボタンの指定
        spinnerBuiltinCancelAction.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			final public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				if (arg2!=0) {
					TaskActionEditListItem taeli=new TaskActionEditListItem();
					taeli.action=(String)spinnerBuiltinCancelAction.getSelectedItem();
					mGlblParms.taskActionListAdapter.add(taeli);
					mGlblParms.taskActionListAdapter.notifyDataSetChanged();
					lv_act_list.setSelection(mGlblParms.taskActionListAdapter.getCount()-1);
					arg0.setSelection(0);
				}
			}
			@Override
			final public void onNothingSelected(AdapterView<?> arg0) {}
        });

		// Add builtin action blockボタンの指定
        spinnerBuiltinBlockAction.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			final public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				if (arg2!=0) {
					TaskActionEditListItem taeli=new TaskActionEditListItem();
					taeli.action=(String)spinnerBuiltinBlockAction.getSelectedItem();
					mGlblParms.taskActionListAdapter.add(taeli);
					mGlblParms.taskActionListAdapter.notifyDataSetChanged();
					lv_act_list.setSelection(mGlblParms.taskActionListAdapter.getCount()-1);
					arg0.setSelection(0);
				}
			}
			@Override
			final public void onNothingSelected(AdapterView<?> arg0) {}
        });

        final Spinner spinnerSelectAction = (Spinner) dialog.findViewById(R.id.edit_profile_task_select_action);
        CustomSpinnerAdapter adapterSelectAction = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        ProfileMaintenance.setSpinnerSelectAction(mGlblParms,dialog,spinnerSelectAction,adapterSelectAction);
        spinnerSelectAction.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			final public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
		        spinnerActionProfile.setVisibility(Spinner.GONE);
		        spinnerBuiltinPrimitiveAction.setVisibility(Spinner.GONE);
		        spinnerBuiltinAbortAction.setVisibility(Spinner.GONE);
		        spinnerBuiltinSkipAction.setVisibility(Spinner.GONE);
		        spinnerBuiltinCancelAction.setVisibility(Spinner.GONE);
		        spinnerBuiltinBlockAction.setVisibility(Spinner.GONE);
				String act=(String)spinnerSelectAction.getSelectedItem();
				if (act.equals(mGlblParms.context.getString(R.string.msgs_edit_profile_action_select_user))) {
			        spinnerActionProfile.setVisibility(Spinner.VISIBLE);
				} else if (act.equals(mGlblParms.context.getString(R.string.msgs_edit_profile_action_select_primitive))) {
			        spinnerBuiltinPrimitiveAction.setVisibility(Spinner.VISIBLE);
				} else if (act.equals(mGlblParms.context.getString(R.string.msgs_edit_profile_action_select_abort))) {
			        spinnerBuiltinAbortAction.setVisibility(Spinner.VISIBLE);
				} else if (act.equals(mGlblParms.context.getString(R.string.msgs_edit_profile_action_select_skip))) {
			        spinnerBuiltinSkipAction.setVisibility(Spinner.VISIBLE);
				} else if (act.equals(mGlblParms.context.getString(R.string.msgs_edit_profile_action_select_cancel))) {
			        spinnerBuiltinCancelAction.setVisibility(Spinner.VISIBLE);
				} else if (act.equals(mGlblParms.context.getString(R.string.msgs_edit_profile_action_select_block))) {
			        spinnerBuiltinBlockAction.setVisibility(Spinner.VISIBLE);
				}
			}
			@Override
			final public void onNothingSelected(AdapterView<?> arg0) {}
        });
        
	};

	final static private void createTaskActionListContextMenu(
			final GlobalParameters mGlblParms,
			final Dialog dialog, 
			final String curr_grp,
			final int pos, final AdapterProfileList pfla){
		mGlblParms.ccMenu.addMenuItem(
				mGlblParms.context.getString(R.string.msgs_main_ccmenu_delete),R.drawable.menu_trash)
	  	.setOnClickListener(new CustomContextMenuOnClickListener() {
		  @Override
		  final public void onClick(CharSequence menuTitle) {
			  mGlblParms.taskActionListAdapter.remove(pos);
			  mGlblParms.taskActionListAdapter.notifyDataSetChanged();
		  }
	  	});
		if (!mGlblParms.taskActionListAdapter.getItem(pos).action.startsWith(BUILTIN_PREFIX)) {
			mGlblParms.ccMenu.addMenuItem(
					mGlblParms.context.getString(R.string.msgs_main_ccmenu_edit_profile),R.drawable.menu_edit)
		  	.setOnClickListener(new CustomContextMenuOnClickListener() {
			  @Override
			  final public void onClick(CharSequence menuTitle) {
				  for (int i=0;i<pfla.getDataListCount();i++) {
					  if (pfla.getDataListItem(i).getProfileType().equals(PROFILE_TYPE_ACTION)&&
							  pfla.getDataListItem(i).getProfileGroup().equals(curr_grp)&&
							  pfla.getDataListItem(i).getProfileName().equals(mGlblParms.taskActionListAdapter.getItem(pos).action)) {
							NotifyEvent ntfy=new NotifyEvent(mGlblParms.context);
							ntfy.setListener(new NotifyEventListener(){
								@Override
								public void positiveResponse(Context c,Object[] o) {
									updateTaskActionEditListDescription(mGlblParms,dialog,pfla,curr_grp, 
											mGlblParms.taskActionListAdapter.getAllItem());
									mGlblParms.taskActionListAdapter.notifyDataSetChanged();
								}
								@Override
								public void negativeResponse(Context c,Object[] o) {}
							});
							ProfileMaintenanceActionProfile pmap=ProfileMaintenanceActionProfile.newInstance();
							pmap.showDialog(mGlblParms.frgamentMgr, pmap, "EDIT",
									curr_grp,pfla.getDataListItem(i), ntfy);
							break;
					  }
				  }
			  }
		  	});
		}
		if (pos>0) {
			mGlblParms.ccMenu.addMenuItem(
					mGlblParms.context.getString(R.string.msgs_main_ccmenu_move_top),R.drawable.menu_top)
		  	.setOnClickListener(new CustomContextMenuOnClickListener() {
			  @Override
			  final public void onClick(CharSequence menuTitle) {
				  TaskActionEditListItem curr_entery=mGlblParms.taskActionListAdapter.getItem(pos);
				  mGlblParms.taskActionListAdapter.remove(pos);
				  mGlblParms.taskActionListAdapter.insert(curr_entery, 0);
				  mGlblParms.taskActionListAdapter.notifyDataSetChanged();
			  }
		  	});
			mGlblParms.ccMenu.addMenuItem(
					mGlblParms.context.getString(R.string.msgs_main_ccmenu_move_up),R.drawable.menu_up)
		  	.setOnClickListener(new CustomContextMenuOnClickListener() {
			  @Override
			  final public void onClick(CharSequence menuTitle) {
				  TaskActionEditListItem curr_entery=mGlblParms.taskActionListAdapter.getItem(pos);
				  mGlblParms.taskActionListAdapter.remove(pos);
				  mGlblParms.taskActionListAdapter.insert(curr_entery, pos-1);
				  mGlblParms.taskActionListAdapter.notifyDataSetChanged();
		  	  }
		  	});
		}
		if ((pos+1)<mGlblParms.taskActionListAdapter.getCount()) {
			mGlblParms.ccMenu.addMenuItem(
					mGlblParms.context.getString(R.string.msgs_main_ccmenu_move_down),R.drawable.menu_down)
		  	.setOnClickListener(new CustomContextMenuOnClickListener() {
			  @Override
			  final public void onClick(CharSequence menuTitle) {
				  TaskActionEditListItem curr_entery=mGlblParms.taskActionListAdapter.getItem(pos);
				  mGlblParms.taskActionListAdapter.remove(pos);
				  mGlblParms.taskActionListAdapter.insert(curr_entery, pos+1);
				  mGlblParms.taskActionListAdapter.notifyDataSetChanged();
			  }
		  	});
			mGlblParms.ccMenu.addMenuItem(
					mGlblParms.context.getString(R.string.msgs_main_ccmenu_move_bottom),R.drawable.menu_bottom)
		  	.setOnClickListener(new CustomContextMenuOnClickListener() {
			  @Override
			  final public void onClick(CharSequence menuTitle) {
				  TaskActionEditListItem curr_entery=mGlblParms.taskActionListAdapter.getItem(pos);
				  mGlblParms.taskActionListAdapter.remove(pos);
				  mGlblParms.taskActionListAdapter.add(curr_entery);
				  mGlblParms.taskActionListAdapter.notifyDataSetChanged();
			  }
		  	});
		}
		mGlblParms.ccMenu.createMenu();
	};

	final static private ArrayList<TaskActionEditListItem> createTaskActionEditList(
			GlobalParameters mGlblParms,
			Dialog dialog, AdapterProfileList tpfa,
			String curr_grp, ArrayList<String>prof_act_list) {
		ArrayList<TaskActionEditListItem> adapter_act_list=new ArrayList<TaskActionEditListItem>();

        for (int i=0;i<prof_act_list.size();i++) {
        	TaskActionEditListItem taeli=new TaskActionEditListItem();
        	taeli.action=prof_act_list.get(i);
        	adapter_act_list.add(taeli);
        }
        updateTaskActionEditListDescription(mGlblParms,dialog,tpfa,curr_grp, adapter_act_list);
		return adapter_act_list;
	};
	
	final static private void updateTaskActionEditListDescription(
			GlobalParameters mGlblParms,Dialog dialog, AdapterProfileList tpfa,
			String curr_grp, ArrayList<TaskActionEditListItem>adapter_act_list) {

//		final Button btnExecute = (Button) dialog.findViewById(R.id.edit_profile_task_test_exec);
//		final Button btnEdit = (Button) dialog.findViewById(R.id.edit_profile_task_test_edit_parms);
//        final CheckBox cb_enable_env_parms=(CheckBox)dialog.findViewById(R.id.edit_profile_task_enable_env_parms);
//        btnExecute.setEnabled(true);
//        btnEdit.setEnabled(true);
//        cb_enable_env_parms.setEnabled(true);
        for (int i=0;i<adapter_act_list.size();i++) {
        	TaskActionEditListItem taeli=adapter_act_list.get(i);
        	if (taeli.action.startsWith(BUILTIN_PREFIX)) taeli.desc="Builtin";
        	else {
//        		Log.v("","grp="+curr_grp+", action="+taeli.action);
        		ProfileListItem pfli=ProfileUtilities.getProfileListItemFromAll(mGlblParms.util,tpfa,curr_grp,PROFILE_TYPE_ACTION,taeli.action);
        		if (pfli!=null) {
            		taeli.desc=pfli.getActionType();
            		if (pfli.getActionType().equals(PROFILE_ACTION_TYPE_ACTIVITY)) {
            			taeli.desc+="  "+pfli.getActionActivityName();
            		} else if (pfli.getActionType().equals(PROFILE_ACTION_TYPE_MUSIC)) {
            			taeli.desc+="  "+pfli.getActionSoundFileName();
            		} else if (pfli.getActionType().equals(PROFILE_ACTION_TYPE_RINGTONE)) {
            			taeli.desc+="  "+pfli.getActionRingtoneName();
            		} else if (pfli.getActionType().equals(PROFILE_ACTION_TYPE_COMPARE)) {
            			String c_v="", sep="";
            			String[] c_v_a=pfli.getActionCompareValue();
            			for (int c_i=0;c_i<c_v_a.length;c_i++) {
            				if (c_v_a[c_i]!=null && !c_v_a[c_i].equals("")) {
                				c_v+=sep+c_v_a[c_i];
                				sep=", ";
            				}
            			} 
            			taeli.desc+="  "+pfli.getActionCompareTarget()+
            					" "+pfli.getActionCompareType()+" ("+c_v+")\n"+
            					pfli.getActionCompareResultAction();
            		} else if (pfli.getActionType().equals(PROFILE_ACTION_TYPE_MESSAGE)) {
            			taeli.desc+="  "+pfli.getActionMessageType()+"\n"+pfli.getActionMessageText();
            		} else if (pfli.getActionType().equals(PROFILE_ACTION_TYPE_TIME)) {
            			taeli.desc+="  "+pfli.getActionTimeType()+"\n"+pfli.getActionTimeTarget();
            		} else if (pfli.getActionType().equals(PROFILE_ACTION_TYPE_TASK)) {
            			taeli.desc+="  "+pfli.getActionTaskType()+"\n"+pfli.getActionTaskTarget();
            		} else if (pfli.getActionType().equals(PROFILE_ACTION_TYPE_WAIT)) {
            			taeli.desc+="  "+pfli.getActionWaitTarget()+"\n"+
            					pfli.getActionWaitTimeoutValue()+", "+pfli.getActionWaitTimeoutUnits();
            		} else if (pfli.getActionType().equals(PROFILE_ACTION_TYPE_BSH_SCRIPT)) {
            			taeli.desc+="  "+"\n"+pfli.getActionBeanShellScriptScript();
            		} else if (pfli.getActionType().equals(PROFILE_ACTION_TYPE_SHELL_COMMAND)) {
            			if (mGlblParms.envParms.settingUseRootPrivilege && pfli.isActionShellCmdWithSu()) {
            				taeli.desc+=" "+mGlblParms.context.getString(R.string.msgs_edit_profile_task_use_root_privilege);
            			}
            			taeli.desc+="  "+"\n"+pfli.getActionShellCmd();
            		}
        		} else {
        			taeli.desc="Error(Action not found)";
        			taeli.invalid=true;
//        	        btnExecute.setEnabled(false);
//        	        btnEdit.setEnabled(false);
//        	        cb_enable_env_parms.setEnabled(false);
        		}
        	}
        }
	};

}

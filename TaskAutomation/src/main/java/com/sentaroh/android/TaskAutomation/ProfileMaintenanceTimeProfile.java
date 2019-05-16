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

import static com.sentaroh.android.TaskAutomation.CommonConstants.PROFILE_DATE_TIME_TYPE_DAY_OF_THE_WEEK;
import static com.sentaroh.android.TaskAutomation.CommonConstants.PROFILE_DATE_TIME_TYPE_EVERY_DAY;
import static com.sentaroh.android.TaskAutomation.CommonConstants.PROFILE_DATE_TIME_TYPE_EVERY_HOUR;
import static com.sentaroh.android.TaskAutomation.CommonConstants.PROFILE_DATE_TIME_TYPE_EVERY_MONTH;
import static com.sentaroh.android.TaskAutomation.CommonConstants.PROFILE_DATE_TIME_TYPE_EVERY_YEAR;
import static com.sentaroh.android.TaskAutomation.CommonConstants.PROFILE_DATE_TIME_TYPE_INTERVAL;
import static com.sentaroh.android.TaskAutomation.CommonConstants.PROFILE_DATE_TIME_TYPE_ONE_SHOT;
import static com.sentaroh.android.TaskAutomation.CommonConstants.PROFILE_DISABLED;
import static com.sentaroh.android.TaskAutomation.CommonConstants.PROFILE_ENABLED;
import static com.sentaroh.android.TaskAutomation.CommonConstants.PROFILE_TYPE_TIME;
import static com.sentaroh.android.TaskAutomation.CommonConstants.PROFILE_VERSION_CURRENT;

import java.text.SimpleDateFormat;
import java.util.Locale;

import com.sentaroh.android.Utilities.NotifyEvent;
import com.sentaroh.android.Utilities.Dialog.CommonDialog;
import com.sentaroh.android.Utilities.Widget.CustomSpinnerAdapter;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class ProfileMaintenanceTimeProfile extends DialogFragment{
	private final static boolean DEBUG_ENABLE=false;
	private final static String APPLICATION_TAG="ProfileMaintenanceTaskProfile";

	private Dialog mDialog=null;
	private boolean mTerminateRequired=true;
//	private Context mContext=null;
	private ProfileMaintenanceTimeProfile mFragment=null;
	private GlobalParameters mGlblParms=null;

	public static ProfileMaintenanceTimeProfile newInstance() {
		if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"newInstance");
		ProfileMaintenanceTimeProfile frag = new ProfileMaintenanceTimeProfile();
        Bundle bundle = new Bundle();
        frag.setArguments(bundle);
        return frag;
    };
    
	public ProfileMaintenanceTimeProfile() {
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
        	mGlblParms=GlobalWorkArea.getGlobalParameters(getActivity().getApplicationContext());
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
    	CommonDialog.setDlgBoxSizeLimit(mDialog,true);
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
			final Button btnCancel = (Button) mDialog.findViewById(R.id.edit_profile_time_cancel_btn);
			btnCancel.performClick();
		}
		mFragment.dismiss();
		super.onCancel(di);
	};
	
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

        int spinnerDateTimeType;
        int spinnerYear;
        int spinnerMonth;
        int spinnerDay;
        int spinnerHour;
        int spinnerMin;
        String day_of_the_week;

    };

    private SavedViewContents saveViewContents() {
    	SavedViewContents sv=new SavedViewContents();
		final EditText dlg_prof_name_et = (EditText) mDialog.findViewById(R.id.edit_profile_time_profile_et_name);
		final CheckBox cb_active = (CheckBox) mDialog.findViewById(R.id.edit_profile_time_enabled);

        sv.dlg_prof_name_et=dlg_prof_name_et.getText();
        sv.dlg_prof_name_et_spos=dlg_prof_name_et.getSelectionStart();
        sv.dlg_prof_name_et_epos=dlg_prof_name_et.getSelectionEnd();
        sv.cb_active=cb_active.isChecked();

        final Spinner spinnerDateTimeType = (Spinner) mDialog.findViewById(R.id.edit_profile_time_date_time_type);
        final Spinner spinnerYear = (Spinner) mDialog.findViewById(R.id.edit_profile_time_exec_year);
        final Spinner spinnerMonth = (Spinner) mDialog.findViewById(R.id.edit_profile_time_exec_month);
        final Spinner spinnerDay = (Spinner) mDialog.findViewById(R.id.edit_profile_time_exec_day);
        final Spinner spinnerHour = (Spinner) mDialog.findViewById(R.id.edit_profile_time_exec_hours);
        final Spinner spinnerMin = (Spinner) mDialog.findViewById(R.id.edit_profile_time_exec_minutes);
        
        sv.spinnerDateTimeType=spinnerDateTimeType.getSelectedItemPosition();
        sv.spinnerYear=spinnerYear.getSelectedItemPosition();
        sv.spinnerMonth=spinnerMonth.getSelectedItemPosition();
        sv.spinnerDay=spinnerDay.getSelectedItemPosition();
        sv.spinnerHour=spinnerHour.getSelectedItemPosition();
        sv.spinnerMin=spinnerMin.getSelectedItemPosition();
        
        sv.day_of_the_week=getDayOfTheWeekString(mGlblParms,mDialog); 
        return sv;
    };

    private void restoreViewContents(final SavedViewContents sv) {
		final EditText dlg_prof_name_et = (EditText) mDialog.findViewById(R.id.edit_profile_time_profile_et_name);
		final CheckBox cb_active = (CheckBox) mDialog.findViewById(R.id.edit_profile_time_enabled);
        final Spinner spinnerDateTimeType = (Spinner) mDialog.findViewById(R.id.edit_profile_time_date_time_type);
        final Spinner spinnerYear = (Spinner) mDialog.findViewById(R.id.edit_profile_time_exec_year);
        final Spinner spinnerMonth = (Spinner) mDialog.findViewById(R.id.edit_profile_time_exec_month);
        final Spinner spinnerDay = (Spinner) mDialog.findViewById(R.id.edit_profile_time_exec_day);
        final Spinner spinnerHour = (Spinner) mDialog.findViewById(R.id.edit_profile_time_exec_hours);
        final Spinner spinnerMin = (Spinner) mDialog.findViewById(R.id.edit_profile_time_exec_minutes);

    	Handler hndl1=new Handler();
    	hndl1.postDelayed(new Runnable(){
			@Override
			public void run() {
				dlg_prof_name_et.setText(sv.dlg_prof_name_et);
				dlg_prof_name_et.setSelection(sv.dlg_prof_name_et_spos,sv.dlg_prof_name_et_epos);
				cb_active.setChecked(sv.cb_active);
				
		        spinnerDateTimeType.setSelection(sv.spinnerDateTimeType);
		        spinnerYear.setSelection(sv.spinnerYear);
		        spinnerMonth.setSelection(sv.spinnerMonth);
		        spinnerDay.setSelection(sv.spinnerDay);
		        spinnerHour.setSelection(sv.spinnerHour);
		        spinnerMin.setSelection(sv.spinnerMin);
		        setDayOfTheWeekCheckBox(mGlblParms,mDialog,sv.day_of_the_week);
		    	Handler hndl2=new Handler();
		    	hndl2.postDelayed(new Runnable(){
					@Override
					public void run() {
					}
		    	},50);
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
		mDialog.setContentView(R.layout.edit_profile_time_dlg);

		final TextView dlg_title = (TextView) mDialog.findViewById(R.id.edit_profile_time_title);
		final EditText dlg_prof_name_et=(EditText)mDialog.findViewById(R.id.edit_profile_time_profile_et_name);
		
		final CheckBox cb_active=(CheckBox)mDialog.findViewById(R.id.edit_profile_time_enabled);
		dlg_title.setText(mGlblParms.context.getString(R.string.msgs_edit_profile_hdr_add_time));

//		CommonDialog.setDlgBoxSizeCompact(mDialog);
		
		cb_active.setChecked(true);
		
        final Spinner spinnerDateTimeType = (Spinner) mDialog.findViewById(R.id.edit_profile_time_date_time_type);
        final AdapterDateTimeTypeSpinner adapterDateTimeType = new AdapterDateTimeTypeSpinner(mGlblParms.context, android.R.layout.simple_spinner_item);
        setSpinnerDateTimeType(mGlblParms,mDialog,spinnerDateTimeType,adapterDateTimeType,
        		PROFILE_DATE_TIME_TYPE_ONE_SHOT);

        final Spinner spinnerYear = (Spinner) mDialog.findViewById(R.id.edit_profile_time_exec_year);
        CustomSpinnerAdapter adapterYear = new CustomSpinnerAdapter(mGlblParms.context, android.R.layout.simple_spinner_item);
        setSpinnerYear(mGlblParms,mDialog,spinnerYear,adapterYear,"****/**/**");

        final Spinner spinnerMonth = (Spinner) mDialog.findViewById(R.id.edit_profile_time_exec_month);
        CustomSpinnerAdapter adapterMonth = new CustomSpinnerAdapter(mGlblParms.context, android.R.layout.simple_spinner_item);
        setSpinnerMonth(mGlblParms,mDialog,spinnerMonth,adapterMonth,"****/**/**");
        
        final Spinner spinnerDay = (Spinner) mDialog.findViewById(R.id.edit_profile_time_exec_day);
        CustomSpinnerAdapter adapterDay = new CustomSpinnerAdapter(mGlblParms.context, android.R.layout.simple_spinner_item);
        setSpinnerDay(mGlblParms,mDialog,spinnerDay,adapterDay,"****/**/**");
        
        final Spinner spinnerHour = (Spinner) mDialog.findViewById(R.id.edit_profile_time_exec_hours);
        CustomSpinnerAdapter adapterHour = new CustomSpinnerAdapter(mGlblParms.context, android.R.layout.simple_spinner_item);
        setSpinnerHour(mGlblParms,mDialog,spinnerHour,adapterHour,"**:**");
        
        final Spinner spinnerMin = (Spinner) mDialog.findViewById(R.id.edit_profile_time_exec_minutes);
        CustomSpinnerAdapter adapterMin = new CustomSpinnerAdapter(mGlblParms.context, android.R.layout.simple_spinner_item);
        setSpinnerMin(mGlblParms,mDialog,spinnerMin,adapterMin,"**:**");

        setViewVisibilityByDateTimeType(mGlblParms,mDialog, PROFILE_DATE_TIME_TYPE_ONE_SHOT);

        spinnerDateTimeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            final public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
            	setViewVisibilityByDateTimeType(mGlblParms,mDialog, adapterDateTimeType.getItem(position));
            }
            @Override
            final public void onNothingSelected(AdapterView<?> arg0) {}
        });

		// CANCELボタンの指定
		final Button btnCancel = (Button) mDialog.findViewById(R.id.edit_profile_time_cancel_btn);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			final public void onClick(View v) {
				mFragment.dismiss();
			}
		});
		// OKボタンの指定
		Button btnOK = (Button) mDialog.findViewById(R.id.edit_profile_time_ok_btn);
		btnOK.setOnClickListener(new View.OnClickListener() {
			final public void onClick(View v) {
				if (!isValidDayOfTheWeek(mGlblParms,mDialog, spinnerDateTimeType.getSelectedItem().toString())) {
					mGlblParms.commonDlg.showCommonDialog(false, "E",  
							mGlblParms.context.getString(R.string.msgs_edit_profile_day_not_selected), "", null);
					return;
				}
				dlg_prof_name_et.selectAll();
				String audit_msg=ProfileMaintenance.auditProfileName(mGlblParms,mGlblParms.profileAdapter,mCurrentGroup,PROFILE_TYPE_TIME,
						dlg_prof_name_et.getText().toString());
				if (!audit_msg.equals("")) {
					mGlblParms.commonDlg.showCommonDialog(false, "E",  
							audit_msg,  "", null);
					return;
				} 
				String prof_active, prof_dw="";
				prof_dw=getDayOfTheWeekString(mGlblParms,mDialog);
				String prof_repeat_type,prof_exec_date,prof_exec_time;
				prof_repeat_type=spinnerDateTimeType.getSelectedItem().toString();
				prof_exec_date=getExecDateByDateTimeType(mGlblParms,prof_repeat_type,
						spinnerYear, spinnerMonth, spinnerDay);
				prof_exec_time=getExecTimeByDateTimeType(mGlblParms,prof_repeat_type,
						spinnerHour, spinnerMin);
				if (spinnerDateTimeType.getSelectedItem().toString().equals(PROFILE_DATE_TIME_TYPE_INTERVAL)) {
					if (prof_exec_time.equals("00:00")) {
						mGlblParms.commonDlg.showCommonDialog(false, "E",  
								mGlblParms.context.getString(R.string.msgs_edit_profile_time_interval_must_be_greater_than_0), "", null);
						return;
					}
				}
				mFragment.dismiss();
				if (cb_active.isChecked()) prof_active=PROFILE_ENABLED;
				else prof_active=PROFILE_DISABLED;

				ProfileUtilities.removeDummyProfile(mGlblParms.profileAdapter,mCurrentGroup);
				
				ProfileListItem ntpli=new ProfileListItem();
				ntpli.setTimeEventEntry(
						PROFILE_VERSION_CURRENT,mCurrentGroup,ProfileUtilities.isProfileGroupActive(mGlblParms.util,mGlblParms.profileAdapter,mCurrentGroup),
						System.currentTimeMillis(),
						PROFILE_TYPE_TIME,
						dlg_prof_name_et.getText().toString(),
						prof_active,
						prof_repeat_type,
						prof_dw,
						prof_exec_date,
						prof_exec_time);
				
				mGlblParms.profileAdapter.addProfItem(ntpli);
				mGlblParms.profileAdapter.sort();
				mGlblParms.profileAdapter.updateShowList();
				mGlblParms.profileAdapter.notifyDataSetChanged();
				ProfileMaintenance.putProfileListToService(mGlblParms,mGlblParms.profileAdapter,ProfileUtilities.isProfileGroupActive(mGlblParms.util,mGlblParms.profileAdapter,mCurrentGroup));
				if (mNotifyCompletion!=null) mNotifyCompletion.notifyToListener(true, null);
			}
		});

    };

    final private void browseProfile() {
		mDialog.setContentView(R.layout.edit_profile_time_dlg);
		
		final TextView dlg_title = (TextView) mDialog.findViewById(R.id.edit_profile_time_title);
		final TextView dlg_title_sub = (TextView) mDialog.findViewById(R.id.edit_profile_time_title_sub);
		final EditText dlg_prof_name_et=(EditText)mDialog.findViewById(R.id.edit_profile_time_profile_et_name);
		dlg_prof_name_et.setVisibility(EditText.GONE);
//		dlg_prof_name_et.setText(mCurrentProfileListItem.getProfileName());
//		dlg_prof_name_et.setEnabled(false);
//		dlg_prof_name_et.setTextColor(Color.WHITE);
		
		final CheckBox cb_active=(CheckBox)mDialog.findViewById(R.id.edit_profile_time_enabled);
		cb_active.setClickable(false);
		dlg_title.setText(mGlblParms.context.getString(R.string.msgs_edit_profile_hdr_browse_time));
		dlg_title_sub.setText("("+mCurrentProfileListItem.getProfileName()+")");

//		CommonDialog.setDlgBoxSizeCompact(mDialog);
		
		if (mCurrentProfileListItem.isProfileEnabled()) cb_active.setChecked(true);
		else cb_active.setChecked(false);
		
		setDayOfTheWeekCheckBox(mGlblParms,mDialog,mCurrentProfileListItem.getTimeDayOfTheWeek());
		
        final Spinner spinnerDateTimeType = (Spinner) mDialog.findViewById(R.id.edit_profile_time_date_time_type);
        final AdapterDateTimeTypeSpinner adapterDateTimeType = new AdapterDateTimeTypeSpinner(mGlblParms.context, android.R.layout.simple_spinner_item);
        setSpinnerDateTimeType(mGlblParms,mDialog,spinnerDateTimeType,adapterDateTimeType,mCurrentProfileListItem.getTimeType());
        spinnerDateTimeType.setClickable(false);

        final Spinner spinnerYear = (Spinner) mDialog.findViewById(R.id.edit_profile_time_exec_year);
        CustomSpinnerAdapter adapterYear = new CustomSpinnerAdapter(mGlblParms.context, android.R.layout.simple_spinner_item);
        setSpinnerYear(mGlblParms,mDialog,spinnerYear,adapterYear,mCurrentProfileListItem.getTimeDate());
        spinnerYear.setClickable(false);

        final Spinner spinnerMonth = (Spinner) mDialog.findViewById(R.id.edit_profile_time_exec_month);
        CustomSpinnerAdapter adapterMonth = new CustomSpinnerAdapter(mGlblParms.context, android.R.layout.simple_spinner_item);
        setSpinnerMonth(mGlblParms,mDialog,spinnerMonth,adapterMonth,mCurrentProfileListItem.getTimeDate());
        spinnerMonth.setClickable(false);
        
        final Spinner spinnerDay = (Spinner) mDialog.findViewById(R.id.edit_profile_time_exec_day);
        CustomSpinnerAdapter adapterDay = new CustomSpinnerAdapter(mGlblParms.context, android.R.layout.simple_spinner_item);
        setSpinnerDay(mGlblParms,mDialog,spinnerDay,adapterDay,mCurrentProfileListItem.getTimeDate());
        spinnerDay.setClickable(false);
        
        final Spinner spinnerHour = (Spinner) mDialog.findViewById(R.id.edit_profile_time_exec_hours);
        CustomSpinnerAdapter adapterHour = new CustomSpinnerAdapter(mGlblParms.context, android.R.layout.simple_spinner_item);
        setSpinnerHour(mGlblParms,mDialog,spinnerHour,adapterHour,mCurrentProfileListItem.getTimeTime());
        spinnerHour.setClickable(false);
        
        final Spinner spinnerMin = (Spinner) mDialog.findViewById(R.id.edit_profile_time_exec_minutes);
        CustomSpinnerAdapter adapterMin = new CustomSpinnerAdapter(mGlblParms.context, android.R.layout.simple_spinner_item);
        setSpinnerMin(mGlblParms,mDialog,spinnerMin,adapterMin,mCurrentProfileListItem.getTimeTime());
        spinnerMin.setClickable(false);

        setViewVisibilityByDateTimeType(mGlblParms,mDialog, mCurrentProfileListItem.getTimeType());

		// CANCELボタンの指定
		final Button btnCancel = (Button) mDialog.findViewById(R.id.edit_profile_time_cancel_btn);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			final public void onClick(View v) {
				mFragment.dismiss();
			}
		});
		Button btnOk = (Button) mDialog.findViewById(R.id.edit_profile_time_ok_btn);
		btnOk.setVisibility(Button.GONE);

    };
    
    final private void editProfile() {
		mDialog.setContentView(R.layout.edit_profile_time_dlg);
		
		final TextView dlg_title = (TextView) mDialog.findViewById(R.id.edit_profile_time_title);
		final TextView dlg_title_sub = (TextView) mDialog.findViewById(R.id.edit_profile_time_title_sub);
		final EditText dlg_prof_name_et=(EditText)mDialog.findViewById(R.id.edit_profile_time_profile_et_name);
		dlg_prof_name_et.setVisibility(EditText.GONE);
		dlg_prof_name_et.setText(mCurrentProfileListItem.getProfileName());
//		dlg_prof_name_et.setEnabled(false);
//		dlg_prof_name_et.setTextColor(Color.WHITE);
		dlg_title.setText(mGlblParms.context.getString(R.string.msgs_edit_profile_hdr_edit_time));
		dlg_title_sub.setText("("+mCurrentProfileListItem.getProfileName()+")");
		
		final CheckBox cb_active=(CheckBox)mDialog.findViewById(R.id.edit_profile_time_enabled);

//		CommonDialog.setDlgBoxSizeCompact(mDialog);
		
		if (mCurrentProfileListItem.isProfileEnabled()) cb_active.setChecked(true);
		else cb_active.setChecked(false);
		
		setDayOfTheWeekCheckBox(mGlblParms,mDialog,mCurrentProfileListItem.getTimeDayOfTheWeek());
		
        final Spinner spinnerDateTimeType = (Spinner) mDialog.findViewById(R.id.edit_profile_time_date_time_type);
        final AdapterDateTimeTypeSpinner adapterDateTimeType = new AdapterDateTimeTypeSpinner(mGlblParms.context, android.R.layout.simple_spinner_item);
        setSpinnerDateTimeType(mGlblParms,mDialog,spinnerDateTimeType,adapterDateTimeType,mCurrentProfileListItem.getTimeType());

        final Spinner spinnerYear = (Spinner) mDialog.findViewById(R.id.edit_profile_time_exec_year);
        CustomSpinnerAdapter adapterYear = new CustomSpinnerAdapter(mGlblParms.context, android.R.layout.simple_spinner_item);
        setSpinnerYear(mGlblParms,mDialog,spinnerYear,adapterYear,mCurrentProfileListItem.getTimeDate());

        final Spinner spinnerMonth = (Spinner) mDialog.findViewById(R.id.edit_profile_time_exec_month);
        CustomSpinnerAdapter adapterMonth = new CustomSpinnerAdapter(mGlblParms.context, android.R.layout.simple_spinner_item);
        setSpinnerMonth(mGlblParms,mDialog,spinnerMonth,adapterMonth,mCurrentProfileListItem.getTimeDate());
        
        final Spinner spinnerDay = (Spinner) mDialog.findViewById(R.id.edit_profile_time_exec_day);
        CustomSpinnerAdapter adapterDay = new CustomSpinnerAdapter(mGlblParms.context, android.R.layout.simple_spinner_item);
        setSpinnerDay(mGlblParms,mDialog,spinnerDay,adapterDay,mCurrentProfileListItem.getTimeDate());
        
        final Spinner spinnerHour = (Spinner) mDialog.findViewById(R.id.edit_profile_time_exec_hours);
        CustomSpinnerAdapter adapterHour = new CustomSpinnerAdapter(mGlblParms.context, android.R.layout.simple_spinner_item);
        setSpinnerHour(mGlblParms,mDialog,spinnerHour,adapterHour,mCurrentProfileListItem.getTimeTime());
        
        final Spinner spinnerMin = (Spinner) mDialog.findViewById(R.id.edit_profile_time_exec_minutes);
        CustomSpinnerAdapter adapterMin = new CustomSpinnerAdapter(mGlblParms.context, android.R.layout.simple_spinner_item);
        setSpinnerMin(mGlblParms,mDialog,spinnerMin,adapterMin,mCurrentProfileListItem.getTimeTime());

        setViewVisibilityByDateTimeType(mGlblParms,mDialog, mCurrentProfileListItem.getTimeType());

        spinnerDateTimeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            final public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
            	setViewVisibilityByDateTimeType(mGlblParms,mDialog, adapterDateTimeType.getItem(position));
            }
            @Override
            final public void onNothingSelected(AdapterView<?> arg0) {}
        });

		// CANCELボタンの指定
		final Button btnCancel = (Button) mDialog.findViewById(R.id.edit_profile_time_cancel_btn);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			final public void onClick(View v) {
				mFragment.dismiss();
			}
		});
		// OKボタンの指定
		Button btnOK = (Button) mDialog.findViewById(R.id.edit_profile_time_ok_btn);
		btnOK.setOnClickListener(new View.OnClickListener() {
			final public void onClick(View v) {
				if (!isValidDayOfTheWeek(mGlblParms,mDialog, spinnerDateTimeType.getSelectedItem().toString())) {
					mGlblParms.commonDlg.showCommonDialog(false, "E",  
							"Select any day", "", null);
					return;
				}
				mFragment.dismiss();
				String prof_active, prof_dw="";
				if (cb_active.isChecked()) prof_active=PROFILE_ENABLED;
				else prof_active=PROFILE_DISABLED;
				
				prof_dw=getDayOfTheWeekString(mGlblParms,mDialog);
				
				String prof_repeat_type,prof_exec_date,prof_exec_time;
				
				prof_repeat_type=spinnerDateTimeType.getSelectedItem().toString();
				
				prof_exec_date=getExecDateByDateTimeType(mGlblParms,prof_repeat_type,
						spinnerYear, spinnerMonth, spinnerDay);
				
				prof_exec_time=getExecTimeByDateTimeType(mGlblParms,prof_repeat_type,
						spinnerHour, spinnerMin);
				
				ProfileListItem ntpli=new ProfileListItem();
				ntpli.setTimeEventEntry(
						PROFILE_VERSION_CURRENT,mCurrentProfileListItem.getProfileGroup(),mCurrentProfileListItem.isProfileGroupActivated(),
						System.currentTimeMillis(),
						PROFILE_TYPE_TIME,
						mCurrentProfileListItem.getProfileName(),
						prof_active,
						prof_repeat_type,
						prof_dw,
						prof_exec_date,
						prof_exec_time);
				ntpli.setProfileGroupShowed(mCurrentProfileListItem.isProfileGroupShowed());
				mGlblParms.profileAdapter.replaceProfItem(ntpli);
				ProfileMaintenance.putProfileListToService(mGlblParms,mGlblParms.profileAdapter,ProfileUtilities.isProfileGroupActive(mGlblParms.util,mGlblParms.profileAdapter,mCurrentGroup));
			}
		});

    }
	
	final static private boolean isValidDayOfTheWeek(final GlobalParameters mGlblParms,Dialog dialog, String rt) {
		boolean result=false;
		if (rt.equals(PROFILE_DATE_TIME_TYPE_DAY_OF_THE_WEEK)) {
			CheckBox cb_dw_mon=(CheckBox)dialog.findViewById(R.id.edit_profile_time_day_of_the_week_monday);
			CheckBox cb_dw_tue=(CheckBox)dialog.findViewById(R.id.edit_profile_time_day_of_the_week_tuesday);
			CheckBox cb_dw_wed=(CheckBox)dialog.findViewById(R.id.edit_profile_time_day_of_the_week_wedsday);
			CheckBox cb_dw_thu=(CheckBox)dialog.findViewById(R.id.edit_profile_time_day_of_the_week_thursday);
			CheckBox cb_dw_fri=(CheckBox)dialog.findViewById(R.id.edit_profile_time_day_of_the_week_friday);
			CheckBox cb_dw_sat=(CheckBox)dialog.findViewById(R.id.edit_profile_time_day_of_the_week_satday);
			CheckBox cb_dw_sun=(CheckBox)dialog.findViewById(R.id.edit_profile_time_day_of_the_week_sunday);
		
			if (cb_dw_mon.isChecked() || cb_dw_tue.isChecked() ||
					cb_dw_wed.isChecked() || cb_dw_thu.isChecked() ||
					cb_dw_fri.isChecked() || cb_dw_sat.isChecked() ||
					cb_dw_sun.isChecked()) result=true;
		} else result=true;
		mGlblParms.util.addDebugMsg(1,"I", "isValidDayOfTheWeek result="+result+", repeat="+rt);
		return result;
	};
	
	final static private String getExecDateByDateTimeType(final GlobalParameters mGlblParms, String rt, 
			Spinner sp_year, Spinner sp_month, Spinner sp_day) {
		String sp_yy="----",sp_mm="--",sp_dd="--";
		if (sp_year.getCount()!=0) sp_yy=sp_year.getSelectedItem().toString();
		if (sp_month.getCount()!=0) sp_mm=sp_month.getSelectedItem().toString();
		if (sp_day.getCount()!=0) sp_dd=sp_day.getSelectedItem().toString();
		
		if (rt.equals(PROFILE_DATE_TIME_TYPE_ONE_SHOT)) {
			//NOP
		} else if (rt.equals(PROFILE_DATE_TIME_TYPE_EVERY_YEAR)) {
			sp_yy="****";
		} else if (rt.equals(PROFILE_DATE_TIME_TYPE_EVERY_MONTH)) {
			sp_yy="****";
			sp_mm="**";
		} else if (rt.equals(PROFILE_DATE_TIME_TYPE_DAY_OF_THE_WEEK)) {
			sp_yy="****";
			sp_mm="**";
			sp_dd="**";
		} else if (rt.equals(PROFILE_DATE_TIME_TYPE_EVERY_DAY)) {
			sp_yy="****";
			sp_mm="**";
			sp_dd="**";
		} else if (rt.equals(PROFILE_DATE_TIME_TYPE_EVERY_HOUR)) {
			sp_yy="****";
			sp_mm="**";
			sp_dd="**";
		}
		mGlblParms.util.addDebugMsg(1,"I", "getExecDateByDateTimeType result="+sp_yy+"/"+sp_mm+"/"+sp_dd+", repeat="+rt);
		return sp_yy+"/"+sp_mm+"/"+sp_dd;
	};
	
	final static private String getExecTimeByDateTimeType(final GlobalParameters mGlblParms, String rt, 
			Spinner sp_hour, Spinner sp_min) {
		String sp_thh="**", sp_tmm="**";
		if (sp_hour.getCount()!=0) sp_thh=sp_hour.getSelectedItem().toString();
		if (sp_min.getCount()!=0) sp_tmm=sp_min.getSelectedItem().toString();
		
		if (rt.equals(PROFILE_DATE_TIME_TYPE_ONE_SHOT)) {
			//NOP
		} else if (rt.equals(PROFILE_DATE_TIME_TYPE_EVERY_YEAR)) {
		} else if (rt.equals(PROFILE_DATE_TIME_TYPE_EVERY_MONTH)) {
		} else if (rt.equals(PROFILE_DATE_TIME_TYPE_DAY_OF_THE_WEEK)) {
		} else if (rt.equals(PROFILE_DATE_TIME_TYPE_EVERY_DAY)) {
		} else if (rt.equals(PROFILE_DATE_TIME_TYPE_EVERY_HOUR)) {
			sp_thh="**";
		} else if (rt.equals(PROFILE_DATE_TIME_TYPE_INTERVAL)) {
		}
		mGlblParms.util.addDebugMsg(1,"I", "getExecTimeByDateTimeType result="+sp_thh+":"+sp_tmm+", repeat="+rt);
		return sp_thh+":"+sp_tmm;
		
	};
	
	final static private String getDayOfTheWeekString(final GlobalParameters mGlblParms,Dialog dialog) {
		CheckBox cb_dw_mon=(CheckBox)dialog.findViewById(R.id.edit_profile_time_day_of_the_week_monday);
		CheckBox cb_dw_tue=(CheckBox)dialog.findViewById(R.id.edit_profile_time_day_of_the_week_tuesday);
		CheckBox cb_dw_wed=(CheckBox)dialog.findViewById(R.id.edit_profile_time_day_of_the_week_wedsday);
		CheckBox cb_dw_thu=(CheckBox)dialog.findViewById(R.id.edit_profile_time_day_of_the_week_thursday);
		CheckBox cb_dw_fri=(CheckBox)dialog.findViewById(R.id.edit_profile_time_day_of_the_week_friday);
		CheckBox cb_dw_sat=(CheckBox)dialog.findViewById(R.id.edit_profile_time_day_of_the_week_satday);
		CheckBox cb_dw_sun=(CheckBox)dialog.findViewById(R.id.edit_profile_time_day_of_the_week_sunday);

		String prof_dw_mon="0",prof_dw_tue="0",prof_dw_wed="0",
				prof_dw_thu="0",prof_dw_fri="0",prof_dw_sat="0",
				prof_dw_sun="0";
		
		if (cb_dw_mon.isChecked()) prof_dw_mon="1";
		if (cb_dw_tue.isChecked()) prof_dw_tue="1";
		if (cb_dw_wed.isChecked()) prof_dw_wed="1";
		if (cb_dw_thu.isChecked()) prof_dw_thu="1";
		if (cb_dw_fri.isChecked()) prof_dw_fri="1";
		if (cb_dw_sat.isChecked()) prof_dw_sat="1";
		if (cb_dw_sun.isChecked()) prof_dw_sun="1";

		mGlblParms.util.addDebugMsg(1,"I", "getDayOfTheWeekString result="+prof_dw_mon+prof_dw_tue+prof_dw_wed+prof_dw_thu+
				prof_dw_fri+prof_dw_sat+prof_dw_sun);
		return prof_dw_sun+prof_dw_mon+prof_dw_tue+prof_dw_wed+prof_dw_thu+
				prof_dw_fri+prof_dw_sat;
	};
	
	final static private void setDayOfTheWeekCheckBox(final GlobalParameters mGlblParms,Dialog dialog, String dw) {
		CheckBox cb_dw_mon=(CheckBox)dialog.findViewById(R.id.edit_profile_time_day_of_the_week_monday);
		CheckBox cb_dw_tue=(CheckBox)dialog.findViewById(R.id.edit_profile_time_day_of_the_week_tuesday);
		CheckBox cb_dw_wed=(CheckBox)dialog.findViewById(R.id.edit_profile_time_day_of_the_week_wedsday);
		CheckBox cb_dw_thu=(CheckBox)dialog.findViewById(R.id.edit_profile_time_day_of_the_week_thursday);
		CheckBox cb_dw_fri=(CheckBox)dialog.findViewById(R.id.edit_profile_time_day_of_the_week_friday);
		CheckBox cb_dw_sat=(CheckBox)dialog.findViewById(R.id.edit_profile_time_day_of_the_week_satday);
		CheckBox cb_dw_sun=(CheckBox)dialog.findViewById(R.id.edit_profile_time_day_of_the_week_sunday);

		if (dw.substring(0,1).equals("1")) cb_dw_sun.setChecked(true);
		if (dw.substring(1,2).equals("1")) cb_dw_mon.setChecked(true);
		if (dw.substring(2,3).equals("1")) cb_dw_tue.setChecked(true);
		if (dw.substring(3,4).equals("1")) cb_dw_wed.setChecked(true);
		if (dw.substring(4,5).equals("1")) cb_dw_thu.setChecked(true);
		if (dw.substring(5,6).equals("1")) cb_dw_fri.setChecked(true);
		if (dw.substring(6,7).equals("1")) cb_dw_sat.setChecked(true);
		mGlblParms.util.addDebugMsg(1,"I", "setDayOfTheWeekCheckBox dw="+dw);

	};

	
	final static private void setViewVisibilityByDateTimeType(final GlobalParameters mGlblParms,Dialog dialog, String repeat_type) {
        LinearLayout ll_dw=(LinearLayout)dialog.findViewById(R.id.edit_profile_time_day_of_the_week);
        LinearLayout ll_exec_year=(LinearLayout)dialog.findViewById(R.id.edit_profile_time_ll_exec_year);
        LinearLayout ll_exec_month=(LinearLayout)dialog.findViewById(R.id.edit_profile_time_ll_exec_month);
        LinearLayout ll_exec_day=(LinearLayout)dialog.findViewById(R.id.edit_profile_time_ll_exec_day);
        LinearLayout ll_exec_hour=(LinearLayout)dialog.findViewById(R.id.edit_profile_time_ll_exec_hour);
        LinearLayout ll_exec_ymd=(LinearLayout)dialog.findViewById(R.id.edit_profile_time_ll_exec_ymd);
        LinearLayout ll_exec_hm=(LinearLayout)dialog.findViewById(R.id.edit_profile_time_ll_exec_hm);

		ll_dw.setVisibility(LinearLayout.VISIBLE);
		ll_exec_year.setVisibility(Spinner.VISIBLE);
		ll_exec_month.setVisibility(Spinner.VISIBLE);
		ll_exec_day.setVisibility(Spinner.VISIBLE);
		ll_exec_hour.setVisibility(Spinner.VISIBLE);
		ll_exec_ymd.setVisibility(LinearLayout.VISIBLE);
		ll_exec_hm.setVisibility(LinearLayout.VISIBLE);

		if (repeat_type.equals(PROFILE_DATE_TIME_TYPE_ONE_SHOT)) {
			ll_dw.setVisibility(LinearLayout.GONE);
		} else if (repeat_type.equals(PROFILE_DATE_TIME_TYPE_EVERY_YEAR)) {
			ll_dw.setVisibility(LinearLayout.GONE);
			ll_exec_year.setVisibility(Spinner.GONE);
		} else if (repeat_type.equals(PROFILE_DATE_TIME_TYPE_EVERY_MONTH)) {
			ll_dw.setVisibility(LinearLayout.GONE);
			ll_exec_year.setVisibility(Spinner.GONE);
			ll_exec_month.setVisibility(Spinner.GONE);
		} else if (repeat_type.equals(PROFILE_DATE_TIME_TYPE_DAY_OF_THE_WEEK)) {
			ll_exec_year.setVisibility(Spinner.GONE);
			ll_exec_month.setVisibility(Spinner.GONE);
			ll_exec_day.setVisibility(Spinner.GONE);
			ll_exec_ymd.setVisibility(LinearLayout.GONE);
		} else if (repeat_type.equals(PROFILE_DATE_TIME_TYPE_EVERY_DAY)) {
			ll_dw.setVisibility(LinearLayout.GONE);
			ll_exec_year.setVisibility(Spinner.GONE);
			ll_exec_month.setVisibility(Spinner.GONE);
			ll_exec_day.setVisibility(Spinner.GONE);
			ll_exec_ymd.setVisibility(LinearLayout.GONE);
		} else if (repeat_type.equals(PROFILE_DATE_TIME_TYPE_EVERY_HOUR)) {
			ll_dw.setVisibility(LinearLayout.GONE);
			ll_exec_year.setVisibility(Spinner.GONE);
			ll_exec_month.setVisibility(Spinner.GONE);
			ll_exec_day.setVisibility(Spinner.GONE);
			ll_exec_hour.setVisibility(Spinner.GONE);
			ll_exec_ymd.setVisibility(LinearLayout.GONE);
		} else if (repeat_type.equals(PROFILE_DATE_TIME_TYPE_INTERVAL)) {
			ll_dw.setVisibility(LinearLayout.GONE);
			ll_exec_year.setVisibility(Spinner.GONE);
			ll_exec_month.setVisibility(Spinner.GONE);
			ll_exec_day.setVisibility(Spinner.GONE);
			ll_exec_ymd.setVisibility(LinearLayout.GONE);
		} 
	
	};

	final static private void setSpinnerDateTimeType(final GlobalParameters mGlblParms,Dialog dialog, Spinner spinner, 
			AdapterDateTimeTypeSpinner adapter, String selected) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_time_select_date_time_type));
        spinner.setAdapter(adapter);
        adapter.clear();

		adapter.add(PROFILE_DATE_TIME_TYPE_ONE_SHOT);
		adapter.add(PROFILE_DATE_TIME_TYPE_EVERY_YEAR);
		adapter.add(PROFILE_DATE_TIME_TYPE_EVERY_MONTH);
		adapter.add(PROFILE_DATE_TIME_TYPE_EVERY_DAY);
		adapter.add(PROFILE_DATE_TIME_TYPE_EVERY_HOUR);
		adapter.add(PROFILE_DATE_TIME_TYPE_DAY_OF_THE_WEEK);
		adapter.add(PROFILE_DATE_TIME_TYPE_INTERVAL);
		for (int i=0;i<adapter.getCount();i++)
			if (adapter.getItem(i).equals(selected)) spinner.setSelection(i);
		
	};
	
	final static private void setSpinnerYear(final GlobalParameters mGlblParms,Dialog dialog, Spinner spinner, 
			CustomSpinnerAdapter adapter, String selected) {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy/MM/dd",Locale.getDefault());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_time_select_year));
        spinner.setAdapter(adapter);
        adapter.clear();

		String yyyy=selected.substring(0,selected.indexOf("/"));
		if (yyyy.equals("****")) {
			String tyy=sdfDate.format(System.currentTimeMillis());
			yyyy=tyy.substring(0,tyy.indexOf("/"));
		}
		int year=Integer.parseInt(yyyy);
		adapter.add(yyyy);
		adapter.add(""+(year+1));
		spinner.setSelection(0);

	};
	
	final static private void setSpinnerMonth(final GlobalParameters mGlblParms,Dialog dialog, Spinner spinner, 
			CustomSpinnerAdapter adapter, String selected) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_time_select_month));
        spinner.setAdapter(adapter);
        adapter.clear();

		for (int i=1;i<=12;i++) 
			if (i>9) adapter.add(""+i);
			else adapter.add("0"+i);
		String yyyy=selected.substring(0,selected.indexOf("/"));
		String mm=selected.replace(yyyy+"/","").substring(0,2);
		if (!mm.equals("**")) {
			int month=Integer.parseInt(mm);
			spinner.setSelection(month-1);
		} else spinner.setSelection(0);
	};

	final static private void setSpinnerDay(final GlobalParameters mGlblParms,Dialog dialog, Spinner spinner, 
			CustomSpinnerAdapter adapter, String selected) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_time_select_day));
        spinner.setAdapter(adapter);
        adapter.clear();

		for (int i=1;i<=31;i++)
			if (i>9) adapter.add(""+i);
			else adapter.add("0"+i);
		String yyyy=selected.substring(0,selected.indexOf("/"));
		String mm=selected.replace(yyyy+"/","").substring(0,2);
		String dd=selected.replace(yyyy+"/"+mm+"/","");
		if (!dd.equals("**")) {
			int day=Integer.parseInt(dd);
			spinner.setSelection(day-1);
		} else spinner.setSelection(0);
	};
	
	final static private void setSpinnerHour(final GlobalParameters mGlblParms,Dialog dialog, Spinner spinner, 
			CustomSpinnerAdapter adapter, String selected) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_time_select_hour));
        spinner.setAdapter(adapter);
        adapter.clear();

		for (int i=0;i<=23;i++)
			if (i>9) adapter.add(""+i);
			else adapter.add("0"+i);
		String hh=selected.substring(0,selected.indexOf(":"));
		if (!hh.equals("**")) {
			int hour=Integer.parseInt(hh);
			spinner.setSelection(hour);
		} else spinner.setSelection(0);
	};
	
	final static private void setSpinnerMin(final GlobalParameters mGlblParms,Dialog dialog, Spinner spinner, 
			CustomSpinnerAdapter adapter, String selected) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_time_select_min));
        spinner.setAdapter(adapter);
        adapter.clear();

		for (int i=0;i<=59;i++) 
			if (i>9) adapter.add(""+i);
			else adapter.add("0"+i);
		String hh=selected.substring(0,selected.indexOf(":"));
		String mm=selected.replace(hh+":","");
		if (!mm.equals("**")) {
			int min=Integer.parseInt(mm);
			spinner.setSelection(min);
		} else spinner.setSelection(0);
	};

}

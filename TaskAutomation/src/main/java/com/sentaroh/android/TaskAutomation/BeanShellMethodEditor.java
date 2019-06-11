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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.sentaroh.android.Utilities.LocalMountPoint;
import com.sentaroh.android.Utilities.NotifyEvent;
import com.sentaroh.android.Utilities.NotifyEvent.NotifyEventListener;
import com.sentaroh.android.Utilities.ThreadCtrl;
import com.sentaroh.android.Utilities.ContextMenu.CustomContextMenu;
import com.sentaroh.android.Utilities.Dialog.CommonDialog;
import com.sentaroh.android.Utilities.Widget.CustomSpinnerAdapter;

public class BeanShellMethodEditor {

	private Context context=null;
	@SuppressWarnings("unused")
	private CommonUtilities util=null;
	private ArrayList<String> androidApplicationList;
	@SuppressWarnings("unused")
	private CustomContextMenu ccMenu = null;
	private ArrayList<RingtoneListItem> ringtoneList=null;
	private CommonDialog commonDlg;
	private AdapterProfileList profileAdapter=null;
	private String currentGroup=null;
	private String currentRingtoneType="";
	private MediaPlayer mMediaPlayer=null;
	private ThreadCtrl mTcMediaPlayer=null;

	public BeanShellMethodEditor(Context c, CommonUtilities cu, CustomContextMenu cm,
			CommonDialog cd, AdapterProfileList pa, String cg, 
			ArrayList<String>aal, ArrayList<RingtoneListItem>rtl) {
		context=c;
		util=cu;
		ccMenu=cm;
		commonDlg=cd;
		profileAdapter=pa;
		currentGroup=cg;
		androidApplicationList=aal;
		ringtoneList=rtl;
	}
	
	final public void editProfileActionBshText(final Dialog dialog, ProfileListItem tpli) {
		
		final EditText et_bsh_script=(EditText) dialog.findViewById(R.id.edit_profile_action_dlg_bsh_script_text);
		final Spinner spinnerBshMethod=(Spinner) dialog.findViewById(R.id.edit_profile_action_dlg_bsh_add_method);

		final Spinner spinnerCatMethod=(Spinner)dialog.findViewById(R.id.edit_profile_action_dlg_bsh_cat_method);
		final CustomSpinnerAdapter adapterCatMethod = new CustomSpinnerAdapter(context, android.R.layout.simple_spinner_item);
		setSpinnerCatMethod(dialog,spinnerCatMethod,adapterCatMethod);

        final CustomSpinnerAdapter adapterAddMethod = new CustomSpinnerAdapter(context, android.R.layout.simple_spinner_item);
        setSpinnerAddMethod(dialog,spinnerBshMethod,adapterAddMethod,
        		spinnerCatMethod.getItemAtPosition(0).toString());

        if (tpli!=null) et_bsh_script.setText(tpli.getActionBeanShellScriptScript());

        spinnerCatMethod.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		        setSpinnerAddMethod(dialog,spinnerBshMethod,adapterAddMethod,spinnerCatMethod.getSelectedItem().toString());
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
        });

        spinnerBshMethod.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				if (pos>0) {
					final int ss=et_bsh_script.getSelectionStart();
					final int se=et_bsh_script.getSelectionEnd();
					final String method="TaCmd."+spinnerBshMethod.getSelectedItem().toString();
					final String parameters=method.substring(method.indexOf("("));
					int f_pos=-1;
					for (int i=0;i<taCmdMethod.length;i++) {
						if (taCmdMethod[i][2].equals(spinnerBshMethod.getSelectedItem().toString())) {
							f_pos=i;
							break;
						}
					}
					final String assgn_to_var=taCmdMethod[f_pos][1];
					NotifyEvent ntfy=new NotifyEvent(context);
					ntfy.setListener(new NotifyEventListener(){
						@Override
						public void positiveResponse(Context c, Object[] o) {
							String c_method=(String)o[0];
							if (ss==se) {
								et_bsh_script.getText().insert(ss, assgn_to_var+c_method);
							} else {
								et_bsh_script.getText().replace(ss, se, assgn_to_var+c_method);
							}
						}
						@Override
						public void negativeResponse(Context c, Object[] o) {}
					});

					spinnerBshMethod.setSelection(0);
					if (parameters.equals("()")) {//No parameter
						ntfy.notifyToListener(POSITIVE, new Object[]{method});
						return;
					} else {//Parameter existed
						String t_parm=parameters.substring(1,parameters.length()-1);
						String[] a_parm=t_parm.split(",");
						for (int i=0;i<a_parm.length;i++) a_parm[i]=a_parm[i].trim();
						bshMethodParameterParser(method,a_parm,0, ntfy);
					}
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
        });

	};

	final public void bshMethodParameterParser(final String method, final String[]a_parm, 
			final int pos, final NotifyEvent p_ntfy) {
		String t_cast="";
		String t_name="";
		if (a_parm[pos].indexOf(" ")>=1) {
			t_cast=a_parm[pos].substring(0,a_parm[pos].indexOf(" ")).trim();
			t_name=a_parm[pos].substring(a_parm[pos].indexOf(" ")+1).trim();
		} else {
			t_name=a_parm[pos].trim();
		}
		final String p_cast=t_cast;
		final String p_name=t_name;
//		Log.v("","pos="+pos+", a_parm_"+pos+"="+a_parm[pos]+", p_cast="+p_cast+", p_name="+p_name);
//		Log.v("","method=="+method);
		NotifyEvent ntfy=new NotifyEvent(context);
		ntfy.setListener(new NotifyEventListener(){
			@Override
			public void positiveResponse(Context c, Object[] o) {
				int n_pos=pos+1;
				String n_method=(String)o[0];
//				Log.v("","n_method="+n_method);
				if (n_pos<a_parm.length) bshMethodParameterParser(n_method,a_parm,n_pos, p_ntfy);
				else p_ntfy.notifyToListener(true, new String[]{n_method});
			}
			@Override
			public void negativeResponse(Context c, Object[] o) {
				p_ntfy.notifyToListener(false, null);
			}
		});
		if (p_cast.equals("String")&&p_name.equals("actv_package_name")) {
			parseParameterNonArrayValueListSelector(method,a_parm,pos,ntfy,androidApplicationList);
		} else if (p_cast.equals("String")&&p_name.equals("file_path")) {
			parseParameterFilePath(method,a_parm,pos,ntfy);
		} else if (p_cast.equals("String")&&p_name.equals("ringtone_name")) {
			ArrayList<String>rt_list=new ArrayList<String>();
			for (int i=0;i<ringtoneList.size();i++) {
				RingtoneListItem rli=ringtoneList.get(i);
				String rt="";
				if (rli.ringtone_type==RingtoneManager.TYPE_ALARM) rt=PROFILE_ACTION_RINGTONE_TYPE_ALARM;
				else if (rli.ringtone_type==RingtoneManager.TYPE_NOTIFICATION) rt=PROFILE_ACTION_RINGTONE_TYPE_NOTIFICATION;
				else rt=PROFILE_ACTION_RINGTONE_TYPE_RINGTONE;
				if (rt.equals(currentRingtoneType)) 
					rt_list.add(rt+"("+rli.ringtone_name+")");
			}
			if (rt_list.size()==0) rt_list.add(context.getString(R.string.msgs_bsh_method_editor_dlg_empty_ringtone_list));
			parseParameterNonArrayValueListSelector(method,a_parm,pos,ntfy,rt_list);
		} else if (p_cast.equals("String")&&p_name.equals("ringtone_type")) {
			ArrayList<String>rt_list=new ArrayList<String>();
			rt_list.add(PROFILE_ACTION_RINGTONE_TYPE_ALARM);
			rt_list.add(PROFILE_ACTION_RINGTONE_TYPE_NOTIFICATION);
			rt_list.add(PROFILE_ACTION_RINGTONE_TYPE_RINGTONE);
			parseParameterNonArrayValueListSelector(method,a_parm,pos,ntfy,rt_list);
		} else if (p_cast.equals("String")&&p_name.equals("log_category")) {
			ArrayList<String>rt_list=new ArrayList<String>();
			rt_list.add("I");
			rt_list.add("W");
			rt_list.add("E");
			parseParameterNonArrayValueListSelector(method,a_parm,pos,ntfy,rt_list);
		} else if (p_cast.equals("String")&&p_name.equals("task_name")) {
			ArrayList<String>task_list=createTaskTriggerTaskList();
			if (task_list.size()==0) task_list.add(context.getString(R.string.msgs_bsh_method_editor_dlg_empty_task_trigger_task_list));
			parseParameterNonArrayValueListSelector(method,a_parm,pos,ntfy,task_list);
		} else if (p_cast.equals("String")&&p_name.equals("interval_timer_name")) {
			ArrayList<String>timer_list=createIntervalTimerList();
			if (timer_list.size()==0) timer_list.add(context.getString(R.string.msgs_bsh_method_editor_dlg_empty_interval_timer_list));
			parseParameterNonArrayValueListSelector(method,a_parm,pos,ntfy,timer_list);
			
		} else if (p_cast.equals("Intent")) {			
			parseParameterNonArrayValueParticularVariable(method,a_parm,pos,ntfy,null,null,null,null);
		} else {
			if (p_name.equals("message_text")) parseParameterNonArrayValueParticularVariable(method,a_parm,pos,ntfy,null,null,null,"Specify message text");
			else if (p_name.equals("time_out_seconds")) parseParameterNonArrayValueParticularVariable(method,a_parm,pos,ntfy,
					60,new String[]{"1","600"},null,context.getString(R.string.msgs_bsh_method_editor_dlg_specify_value_seconds));
			else if (p_name.equals("debug_level"))parseParameterNonArrayValueParticularVariable(method,a_parm,pos,ntfy,
					1,new String[]{"1","3"},null,null);
			else if (p_name.equals("use_vibrator"))parseParameterNonArrayValueParticularVariable(method,a_parm,pos,ntfy,
					null,null,null,null);
			else if (p_name.equals("use_led"))parseParameterNonArrayValueParticularVariable(method,a_parm,pos,ntfy,
					true,null,null,null);
			else if (p_name.equals("led_color")){
				ArrayList<String>rt_list=new ArrayList<String>();
				rt_list.add(PROFILE_ACTION_TYPE_MESSAGE_LED_RED);
				rt_list.add(PROFILE_ACTION_TYPE_MESSAGE_LED_BLUE);
				rt_list.add(PROFILE_ACTION_TYPE_MESSAGE_LED_GREEN);
				parseParameterNonArrayValueListSelector(method,a_parm,pos,ntfy,rt_list);
//				bshMethodParameterParser_nonaray_value(method,a_parm,pos,ntfy,
//						"blue",null,new String[]{"blue","red","green"},"Specify Led color");
			}
			else if (p_name.equals("volume_left"))parseParameterNonArrayValueParticularVariable(method,a_parm,pos,ntfy,
					100,new String[]{"0","100"},null,context.getString(R.string.msgs_bsh_method_editor_dlg_specify_value_int));
			else if (p_name.equals("volume_right"))parseParameterNonArrayValueParticularVariable(method,a_parm,pos,ntfy,
					100,new String[]{"0","100"},null,context.getString(R.string.msgs_bsh_method_editor_dlg_specify_value_int));
			else if (p_name.equals("wait_time_seconds"))parseParameterNonArrayValueParticularVariable(method,a_parm,pos,ntfy,
					60,new String[]{"1","3600"},null,context.getString(R.string.msgs_bsh_method_editor_dlg_specify_value_seconds));
			else if (p_name.equals("timeout_seconds"))parseParameterNonArrayValueParticularVariable(method,a_parm,pos,ntfy,
					60,new String[]{"1","3600"},null,context.getString(R.string.msgs_bsh_method_editor_dlg_specify_value_seconds));
			else {
				if (p_cast.equals("int") || p_cast.equals("boolean") ||p_cast.equals("String")) 
					parseParameterNonArrayValueParticularVariable(method,a_parm,pos,ntfy,null,null,null,null);
				else parseParameterNop(method,a_parm,pos,ntfy);	
			}
		}
	}

	final private ArrayList<String> createTaskTriggerTaskList() {
		ArrayList<String>task_list=new ArrayList<String>();
//		Log.v("","cg="+currentGroup);
		for (int i=0;i<profileAdapter.getDataListCount();i++) {
			ProfileListItem pfli=profileAdapter.getDataListItem(i);
			if (pfli.getProfileGroup().equals(currentGroup) &&
					pfli.getProfileType().equals(PROFILE_TYPE_TASK)) {
				pfli.dumpProfile();
				if (pfli.getTaskTriggerList().get(0).equals(TRIGGER_EVENT_TASK))
					task_list.add(pfli.getProfileName());
			}
		}
		return task_list;
	}
	final private ArrayList<String> createIntervalTimerList() {
		ArrayList<String>timer_list=new ArrayList<String>();
		for (int i=0;i<profileAdapter.getDataListCount();i++) {
			ProfileListItem pfli=profileAdapter.getDataListItem(i);
			if (pfli.getProfileGroup().equals(currentGroup) &&
					pfli.getProfileType().equals(PROFILE_TYPE_TIME)) {
				if (pfli.getTimeType().equals(PROFILE_DATE_TIME_TYPE_INTERVAL))
					timer_list.add(pfli.getProfileName());
			}
		}
		return timer_list;
	}
	final private void parseParameterNop(String method,
			String[] a_parm, int pos,NotifyEvent p_ntfy) {
		p_ntfy.notifyToListener(true, new Object[]{method});
	}
	final private void parseParameterFilePath(final String method,
			final String[] a_parm, final int pos,final NotifyEvent p_ntfy){
		NotifyEvent ntfy=new NotifyEvent(context);
		ntfy.setListener(new NotifyEventListener(){
			@Override
			public void positiveResponse(Context c, Object[] o) {
				String fp=(String)o[0]+"/"+(String)o[1];
				String n_method=method.replace(a_parm[pos], "\""+fp+"\"");
//				Log.v("","file_path="+fp);
//				Log.v("","n_method="+n_method);
				p_ntfy.notifyToListener(true, new Object[]{n_method});
			}
			@Override
			public void negativeResponse(Context c, Object[] o) {
				p_ntfy.notifyToListener(false, null);
			}
		});
		String mn=method.substring(0,method.indexOf("("));
		String title=String.format(context.getString(R.string.msgs_bsh_method_editor_dlg_select_file),mn,a_parm[pos]);
		commonDlg.fileSelectorFileOnlyWithCreate(true, LocalMountPoint.getExternalStorageDir(), "", "",
				title, ntfy);
	}
	final private void parseParameterNonArrayValueListSelector(final String method,
				final String[] a_parm, final int pos,final NotifyEvent p_ntfy, 
				final ArrayList<String>s_list){
			final String p_cast=a_parm[pos].substring(0,a_parm[pos].indexOf(" ")).trim();
			final String p_name=a_parm[pos].substring(a_parm[pos].indexOf(" ")+1).trim();
			final NotifyEvent ntfy=new NotifyEvent(context);
			ntfy.setListener(new NotifyEventListener(){
				@Override
				public void positiveResponse(Context c, Object[] o) {
					String key=(String)o[0];
					String n_method="";
					if (p_name.equalsIgnoreCase("ringtone_type")) currentRingtoneType=key;
					else currentRingtoneType=""; 
					if (p_cast.equalsIgnoreCase("String")) n_method=method.replace(a_parm[pos], "\""+key+"\"");
					else n_method=method.replace(a_parm[pos], key);
					p_ntfy.notifyToListener(true, new Object[]{n_method});
				}
				@Override
				public void negativeResponse(Context c, Object[] o) {
					p_ntfy.notifyToListener(false, null);
				}
			});
			final String method_name=method.substring(0,method.indexOf("("));
			
			// カスタムダイアログの生成
			final Dialog dialog = new Dialog(context);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	//		dialog.getWindow().setSoftInputMode(
	//                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			dialog.setContentView(R.layout.bsh_script_edit_parse_assign_dlg);
	//		final TextView dlg_msg = (TextView) dialog.findViewById(R.id.bsh_script_edit_parse_assign_dlg_msg);
			final TextView dlg_title = (TextView) dialog.findViewById(R.id.bsh_script_edit_parse_assign_dlg_title);
			dlg_title.setText(String.format(
					context.getString(R.string.msgs_bsh_method_editor_dlg_assign_title),
					method_name,a_parm[pos]));
			
			CommonDialog.setDlgBoxSizeLimit(dialog,true);
	
			final ListView lv_itemlist=(ListView)dialog.findViewById(R.id.bsh_script_edit_parse_assign_dlg_select_itemlist);
			final EditText et_bsh_string=(EditText)dialog.findViewById(R.id.bsh_script_edit_parse_assign_dlg_string_value);
			final EditText et_bsh_int=(EditText)dialog.findViewById(R.id.bsh_script_edit_parse_assign_dlg_int_value);
			final RadioGroup rg_bsh_boolean=(RadioGroup)dialog.findViewById(R.id.bsh_script_edit_parse_assign_dlg_rg_boolean);
	//		final RadioButton rg_bsh_boolean_true=(RadioButton)dialog.findViewById(R.id.bsh_script_edit_parse_assign_dlg_rg_boolean_true);
	//		final RadioButton rg_bsh_boolean_false=(RadioButton)dialog.findViewById(R.id.bsh_script_edit_parse_assign_dlg_rg_boolean_false);
			final Button btn_pb_start=(Button)dialog.findViewById(R.id.bsh_script_edit_parse_assign_dlg_playback_sound_start);
			final Button btn_pb_stop=(Button)dialog.findViewById(R.id.bsh_script_edit_parse_assign_dlg_playback_sound_stop);
			final Button btnCancel = (Button) dialog.findViewById(R.id.bsh_script_edit_parse_assign_dlg_cancel_btn);
			final Button btnOK = (Button) dialog.findViewById(R.id.bsh_script_edit_parse_assign_dlg_ok_btn);
			et_bsh_string.setVisibility(EditText.VISIBLE);
			et_bsh_int.setVisibility(EditText.GONE);
			rg_bsh_boolean.setVisibility(RadioGroup.GONE);

			if (method_name.equals("TaCmd.playBackRingtone") && 
					p_name.equals("ringtone_name")) {
				btn_pb_start.setVisibility(LinearLayout.VISIBLE);
				btn_pb_start.setEnabled(false);
				btn_pb_stop.setVisibility(LinearLayout.GONE);
			} else {
				btn_pb_start.setVisibility(LinearLayout.GONE);
				btn_pb_stop.setVisibility(LinearLayout.GONE);
			}

			
			lv_itemlist.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, s_list));
			lv_itemlist.setScrollingCacheEnabled(false);
			lv_itemlist.setScrollbarFadingEnabled(false);
	
			lv_itemlist.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
					if (s_list.get(pos).startsWith("**")) return; 
					if (p_name.equals("actv_package_name") ||
							p_name.equals("ringtone_name")) {
						String t_se=s_list.get(pos);
						String se=t_se.substring(t_se.indexOf("(")+1,t_se.lastIndexOf(")"));
						et_bsh_string.setText(se);
					} else {
						et_bsh_string.setText(s_list.get(pos));
					}
				}
			});
			
			btn_pb_start.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Uri uri=null;
					if (method_name.equals("TaCmd.playBackRingtone")) {
						uri=getRingtoneUriByName(currentRingtoneType, et_bsh_string.getText().toString());
						playbackSound(currentRingtoneType, uri,btn_pb_start, btn_pb_stop);
					}
				}
			});
			btn_pb_stop.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					stopMediaPlayer();
				}
			});
			
			btnOK.setEnabled(false);
			et_bsh_string.addTextChangedListener(new TextWatcher() {
				@Override
				public void afterTextChanged(Editable s) {
					if (s.length()!=0) {
						btnOK.setEnabled(true);
						if (getRingtoneUriByName(currentRingtoneType, et_bsh_string.getText().toString())!=null) {
							btn_pb_start.setEnabled(true);
						} else {
							btn_pb_start.setEnabled(false);
						}
//						Log.v("","mn="+method_name+", et="+et_bsh_string.getText().toString());
						if (method_name.equals("TaCmd.playBackRingtone") && 
								p_name.equals("ringtone_name")) {
							btn_pb_start.setVisibility(LinearLayout.VISIBLE);
							btn_pb_stop.setVisibility(LinearLayout.GONE);
							stopMediaPlayer();
						} else {
							btn_pb_start.setVisibility(LinearLayout.GONE);
							btn_pb_stop.setVisibility(LinearLayout.GONE);
						}

					} else {
						btnOK.setEnabled(false);
						btn_pb_start.setEnabled(false);
					}
				}
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
				@Override
				public void onTextChanged(CharSequence s, int start, int before,int count) {}
			});
			// CANCELボタンの指定
			btnCancel.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					stopMediaPlayer();
					dialog.dismiss();
					p_ntfy.notifyToListener(false, null);
				}
			});
			// OKボタンの指定
			btnOK.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					dialog.dismiss();
					stopMediaPlayer();
					ntfy.notifyToListener(true, new Object[]{et_bsh_string.getText().toString()});
				}
			});
			// Cancelリスナーの指定
			dialog.setOnCancelListener(new Dialog.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) {
					btnCancel.performClick();
				}
			});
//			dialog.setCancelable(false);
			dialog.show();
	};
	
	final private Uri getRingtoneUriByName(String type, String name) {
		Uri uri=null;
		for (int i=0;i<ringtoneList.size();i++) {
			RingtoneListItem rtli=ringtoneList.get(i);
			String rt="";
			if (rtli.ringtone_type==RingtoneManager.TYPE_ALARM) rt="ALARM";
			else if (rtli.ringtone_type==RingtoneManager.TYPE_NOTIFICATION) rt="NOTIFICATION";
			else if (rtli.ringtone_type==RingtoneManager.TYPE_RINGTONE) rt="RINGTONE";
			if (rt.equals(type) && rtli.ringtone_name.equals(name)) {
				uri=rtli.ringtone_uri;
			}
		}
		return uri;
	};
	
	final private void playbackSound(String type, Uri uri, final Button btn_start, final Button btn_stop) {
//		Log.v("","type="+type+", uri="+uri.getEncodedPath());
		mMediaPlayer=new MediaPlayer();
		mTcMediaPlayer=new ThreadCtrl();

		mMediaPlayer.setOnCompletionListener(new OnCompletionListener(){
			@Override
			public void onCompletion(MediaPlayer arg0) {
				btn_start.setEnabled(true);
				btn_start.setVisibility(Button.VISIBLE);
				btn_stop.setEnabled(false);
				btn_stop.setVisibility(Button.GONE);
			}
		});

		mMediaPlayer.setOnErrorListener(new OnErrorListener(){
			@Override
			public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
				btn_start.setEnabled(true);
				btn_start.setVisibility(Button.VISIBLE);
				btn_stop.setEnabled(false);
				btn_stop.setVisibility(Button.GONE);
				return false;
			}
		});

		mMediaPlayer.setOnPreparedListener(new OnPreparedListener(){
			@Override
			public void onPrepared(MediaPlayer arg0) {
				final Handler hndl=new Handler();
				Thread th=new Thread() {
					@Override
					final public void run() {
						mMediaPlayer.start();
						int duration=mMediaPlayer.getDuration();
						synchronized(mTcMediaPlayer) {
							try {
								mTcMediaPlayer.wait(duration);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						mTcMediaPlayer.setDisabled();
						mMediaPlayer.reset();
						mMediaPlayer.release();
						mMediaPlayer=null;
						hndl.post(new Runnable(){
							@Override
							final public void run() {
								btn_start.setEnabled(true);
								btn_start.setVisibility(Button.VISIBLE);
								btn_stop.setEnabled(false);
								btn_stop.setVisibility(Button.GONE);
							}
						});
					}
				};
				th.start();
			}
		});

		btn_start.setEnabled(false);
		btn_start.setVisibility(Button.GONE);

		btn_stop.setEnabled(true);
		btn_stop.setVisibility(Button.VISIBLE);

		if (type.equals("ALARM")) mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
		else if (type.equals("NOTIFICATION")) mMediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
		else mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
		try {
			mMediaPlayer.setDataSource(context, uri);
			mMediaPlayer.prepareAsync();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	};
	
	private void stopMediaPlayer() {
		if (mTcMediaPlayer!=null && mTcMediaPlayer.isEnabled()) {
			if (mMediaPlayer!=null) {
				synchronized(mTcMediaPlayer) {
					if (mMediaPlayer.isPlaying()) {
						mTcMediaPlayer.setDisabled();
						mTcMediaPlayer.notify();
					};
				};
			};
		};
	};
	
	final private void parseParameterNonArrayValueParticularVariable(final String method,
			final String[] a_parm, final int pos,final NotifyEvent p_ntfy,
			final Object def_val, final Object[] int_val_range, 
			final Object[] valid_string_val, final String msg_text){
		final String p_cast=a_parm[pos].substring(0,a_parm[pos].indexOf(" ")).trim();
		@SuppressWarnings("unused")
		final String p_name=a_parm[pos].substring(a_parm[pos].indexOf(" ")+1).trim();
//		Log.v("","pos="+pos+",p_cast="+p_cast+", p_name="+p_name);
		final NotifyEvent ntfy=new NotifyEvent(context);
		ntfy.setListener(new NotifyEventListener(){
			@Override
			public void positiveResponse(Context c, Object[] o) {
				String key=(String)o[0];
				String n_method="";
				if (p_cast.equalsIgnoreCase("String")) n_method=method.replace(a_parm[pos], "\""+key+"\"");
				else n_method=method.replace(a_parm[pos], key);
				p_ntfy.notifyToListener(true, new Object[]{n_method});
			}
			@Override
			public void negativeResponse(Context c, Object[] o) {
				p_ntfy.notifyToListener(false, null);
			}
		});
		String mn=method.substring(0,method.indexOf("("));
		
		// カスタムダイアログの生成
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		dialog.getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		dialog.setContentView(R.layout.bsh_script_edit_parse_assign_dlg);
		final TextView dlg_msg = (TextView) dialog.findViewById(R.id.bsh_script_edit_parse_assign_dlg_msg);
		final TextView dlg_title = (TextView) dialog.findViewById(R.id.bsh_script_edit_parse_assign_dlg_title);
		dlg_title.setText(String.format(
				context.getString(R.string.msgs_bsh_method_editor_dlg_assign_title),
				mn,a_parm[pos]));
		
		CommonDialog.setDlgBoxSizeLimit(dialog,true);

		final ListView lv_itemlist=(ListView)dialog.findViewById(R.id.bsh_script_edit_parse_assign_dlg_select_itemlist);		final EditText et_bsh_string=(EditText)dialog.findViewById(R.id.bsh_script_edit_parse_assign_dlg_string_value);
		lv_itemlist.setVisibility(ListView.GONE);
		final EditText et_bsh_int=(EditText)dialog.findViewById(R.id.bsh_script_edit_parse_assign_dlg_int_value);
		final RadioGroup rg_bsh_boolean=(RadioGroup)dialog.findViewById(R.id.bsh_script_edit_parse_assign_dlg_rg_boolean);
		final RadioButton rg_bsh_boolean_true=(RadioButton)dialog.findViewById(R.id.bsh_script_edit_parse_assign_dlg_rg_boolean_true);
//		final RadioButton rg_bsh_boolean_false=(RadioButton)dialog.findViewById(R.id.bsh_script_edit_parse_assign_dlg_rg_boolean_false);
		final Button btn_pb_start=(Button)dialog.findViewById(R.id.bsh_script_edit_parse_assign_dlg_playback_sound_start);
		final Button btn_pb_stop=(Button)dialog.findViewById(R.id.bsh_script_edit_parse_assign_dlg_playback_sound_stop);
		btn_pb_start.setVisibility(Button.GONE);
		btn_pb_stop.setVisibility(Button.GONE);
		et_bsh_string.setVisibility(EditText.VISIBLE);
		et_bsh_int.setVisibility(EditText.GONE);
		rg_bsh_boolean.setVisibility(RadioGroup.GONE);
		int[] t_int_value_range=null;
		String[] t_string_valid_value=null;
		if (p_cast.equalsIgnoreCase("int")) {
			et_bsh_string.setVisibility(EditText.GONE);
			et_bsh_int.setVisibility(EditText.VISIBLE);
			rg_bsh_boolean.setVisibility(RadioGroup.GONE);
			if (def_val!=null) et_bsh_int.setText(String.valueOf((Integer)def_val));
			if (int_val_range!=null) {
				t_int_value_range=new int[2];
				t_int_value_range[0]=Integer.valueOf((String)int_val_range[0]);
				t_int_value_range[1]=Integer.valueOf((String)int_val_range[1]);
			}
		} else if (p_cast.equalsIgnoreCase("boolean")) {
			et_bsh_string.setVisibility(EditText.GONE);
			et_bsh_int.setVisibility(EditText.GONE);
			rg_bsh_boolean.setVisibility(RadioGroup.VISIBLE);
			if (def_val!=null && (Boolean)def_val) rg_bsh_boolean_true.setChecked(true);
		} else {
			et_bsh_string.setVisibility(EditText.VISIBLE);
			et_bsh_int.setVisibility(EditText.GONE);
			rg_bsh_boolean.setVisibility(RadioGroup.GONE);
			if (def_val!=null) et_bsh_string.setText((String)def_val);
			if (valid_string_val!=null) {
				t_string_valid_value=new String[valid_string_val.length];
				for (int i=0;i<valid_string_val.length;i++) {
					t_string_valid_value[i]=(String)valid_string_val[i];
				}
			}
		}
		final int[] int_value_range=t_int_value_range;
		final String[] string_valid_value=t_string_valid_value;
		
		// CANCELボタンの指定
		final Button btnCancel = (Button) dialog.findViewById(R.id.bsh_script_edit_parse_assign_dlg_cancel_btn);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
				p_ntfy.notifyToListener(false, null);
			}
		});
		// OKボタンの指定
		Button btnOK = (Button) dialog.findViewById(R.id.bsh_script_edit_parse_assign_dlg_ok_btn);
		btnOK.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String val="";
				if (p_cast.equalsIgnoreCase("int")) {
					if (et_bsh_int.getText().toString().equals("")) val="0";
					val=et_bsh_int.getText().toString();
					if (int_value_range!=null) {
						if (Integer.valueOf(val)<int_value_range[0] ||
								Integer.valueOf(val)>int_value_range[1]) {
							dlg_msg.setText(
									String.format(context.getString(R.string.msgs_bsh_method_editor_dlg_outofrange_int),
											int_value_range[0],int_value_range[1]));
							return;
						}
					}
				} else if (p_cast.equalsIgnoreCase("boolean")) {
					if (rg_bsh_boolean_true.isChecked()) val="true";
					else val="false";
				} else {
					val=et_bsh_string.getText().toString();
					if (string_valid_value!=null) {
						boolean found=false;
						for (int i=0;i<string_valid_value.length;i++) {
							if (val.equalsIgnoreCase(string_valid_value[i])) {
								found=true;
								break;
							}
						}
						if (!found) {
							String vv="",sep="";
							for (int i=0;i<string_valid_value.length;i++) {
								vv+=sep+string_valid_value[i];
								sep=",";
							}
							dlg_msg.setText(
									String.format(context.getString(R.string.msgs_bsh_method_editor_dlg_outofrange_string),vv));
							return;
						}
					}
				}
				dialog.dismiss();
				ntfy.notifyToListener(true, new Object[]{val});
			}
		});
		// Cancelリスナーの指定
		dialog.setOnCancelListener(new Dialog.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				btnCancel.performClick();
			}
		});
//		dialog.setCancelable(false);
		dialog.show();
	}
	
	final private void setSpinnerAddMethod(Dialog dialog,Spinner spinner, 
			CustomSpinnerAdapter adapter, String sel_cat) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(context.getString(R.string.msgs_bsh_method_editor_dlg_method_spinner_title));
        spinner.setAdapter(adapter);
		adapter.clear();
		adapter.add("** Method not selected");
		for (int i=0;i<taCmdMethod.length;i++) {
			if (taCmdMethod[i][0].equals(sel_cat)) adapter.add(taCmdMethod[i][2]);
		}
	}

	final private void setSpinnerCatMethod(Dialog dialog,Spinner spinner, CustomSpinnerAdapter adapter) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(context.getString(R.string.msgs_bsh_method_editor_dlg_method_spinner_title));
        spinner.setAdapter(adapter);
		adapter.clear();
		ArrayList<String>cat_list=new ArrayList<String>();
		for (int i=0;i<taCmdMethod.length;i++) {
			boolean found=false;
			for (int j=0;j<cat_list.size();j++) {
				if (cat_list.get(j).equals(taCmdMethod[i][0])) {
					found=true;
					break;
				}
			}
			if (!found) cat_list.add(taCmdMethod[i][0]);
		}
		Collections.sort(cat_list);
		for (int i=0;i<cat_list.size();i++) adapter.add(cat_list.get(i));
	}

	final static private String[][] taCmdMethod =new String[][]{
			{"Task",				"",		"abort()"},
			{"Loccation",			"",		"activateAvailableLocationProvider()"},
			{"Loccation",			"",		"activateGpsLocationProvider()"},
			{"Loccation",			"",		"activateNetworkLocationProvider()"},
			{"Airplane mode",		"",		"addBlockEventAirplaneModeOff()"},
			{"Airplane mode",		"",		"addBlockEventAirplaneModeOn()"},
			{"Other",				"",		"addBlockEventAll()"},
			{"Bluetooth",			"",		"addBlockEventBluetoothConnected()"},
			{"Bluetooth",			"",		"addBlockEventBluetoothDisconnected()"},
			{"Bluetooth",			"",		"addBlockEventBluetoothOff()"},
			{"Bluetooth",			"",		"addBlockEventBluetoothOn()"},
			{"Telephony",			"",		"addBlockEventCallStateIdle()"},
			{"Telephony",			"",		"addBlockEventCallStateOffhook()"},
			{"Telephony",			"",		"addBlockEventCallStateRinging()"},
			{"Light sensor",		"",		"addBlockEventLightDetected()"},
			{"Light sensor",		"",		"addBlockEventLightUndetected()"},
			{"Mobile network",		"",		"addBlockEventMobileNetworkConnected()"},
			{"Mobile network",		"",		"addBlockEventMobileNetworkDisconnected()"},
			{"Battery",				"",		"addBlockEventPowerSourceChangedAc()"},
			{"Battery",				"",		"addBlockEventPowerSourceChangedBattery()"},
			{"Proximity sensor",	"",		"addBlockEventProximityDetected()"},
			{"Proximity sensor",	"",		"addBlockEventProximityUndetected()"},
			{"Screen",				"",		"addBlockEventScreenLocked()"},
			{"Screen",				"",		"addBlockEventScreenUnlocked()"},
			{"WiFi",				"",		"addBlockEventWifiConnected()"},
			{"WiFi",				"",		"addBlockEventWifiDisconnected()"},
			{"WiFi",				"",		"addBlockEventWifiOff()"},
			{"WiFi",				"",		"addBlockEventWifiOn()"},
			{"Airplane mode",		"",		"cancelTaskByEventAirplaneModeOff()"},
			{"Airplane mode",		"",		"cancelTaskByEventAirplaneModeOn()"},
			{"Bluetooth",			"",		"cancelTaskByEventBluetoothConnected()"},
			{"Bluetooth",			"",		"cancelTaskByEventBluetoothDisconnected()"},
			{"Bluetooth",			"",		"cancelTaskByEventBluetoothOff()"},
			{"Bluetooth",			"",		"cancelTaskByEventBluetoothOn()"},
			{"Other",				"",		"cancelTaskByEventBootCompleted()"},
			{"Telephony",			"",		"cancelTaskByEventCallStateIdle()"},
			{"Telephony",			"",		"cancelTaskByEventCallStateOffhook()"},
			{"Telephony",			"",		"cancelTaskByEventCallStateRinging()"},
			{"Light sensor",		"",		"cancelTaskByEventLightDetected()"},
			{"Light sensor",		"",		"cancelTaskByEventLightUndetected()"},
			{"Mobile network",		"",		"cancelTaskByEventMobileNetworkConnected()"},
			{"Mobile network",		"",		"cancelTaskByEventMobileNetworkDisconnected()"},
			{"Battery",				"",		"cancelTaskByEventPowerSourceChangedAc()"},
			{"Battery",				"",		"cancelTaskByEventPowerSourceChangedBattery()"},
			{"Proximity sensor",	"",		"cancelTaskByEventProximityDetected()"},
			{"Proximity sensor",	"",		"cancelTaskByEventProximityUndetected()"},
			{"Screen",				"",		"cancelTaskByEventScreenLocked()"},
			{"Screen",				"",		"cancelTaskByEventScreenUnlocked()"},
			{"WiFi",				"",		"cancelTaskByEventWifiConnected()"},
			{"WiFi",				"",		"cancelTaskByEventWifiDisconnected()"},
			{"WiFi",				"",		"cancelTaskByEventWifiOff()"},
			{"WiFi",				"",		"cancelTaskByEventWifiOn()"},
			{"Task",				"",		"cancelTask(String task_name)"},			
			{"Other",				"",		"clearBlockEventAll()"},
			{"Loccation",			"",		"deactivateLocationProvider()"},
			{"Message",				"",		"debugMsg(int debug_level, String log_category, String log_message)"},
			{"Battery",				"",		"getBatteryLevel()"},
			{"Bluetooth",			"",		"getBluetoothConnectedDeviceAddrAtPos(int position)"},
			{"Bluetooth",			"",		"getBluetoothConnectedDeviceListCount()"},
			{"Bluetooth",			"",		"getBluetoothConnectedDeviceNameAtPos(int position)"},
			{"Bluetooth",			"",		"getBluetoothDeviceName()"},
			{"Bluetooth",			"",		"getBluetoothDeviceAddr()"},
			{"Loccation",			"",		"getCurrentLocation()"},
			{"Loccation",			"",		"getCurrentLocation(int timeout)"},
			{"Loccation",			"",		"getLastKnownLocation()"},
			{"Loccation",			"",		"getLastKnownLocationGpsProvider()"},
			{"Loccation",			"",		"getLastKnownLocationNetworkProvider()"},
			{"Light sensor",		"",		"getLightSensorValue()"},
			{"WiFi",				"",		"getWifiSsidName()"},
			{"WiFi",				"",		"getWifiSsidAddr()"},
			{"Airplane mode",		"",		"isAirplaneModeOn()"},
			{"Battery",				"",		"isBatteryCharging()"},
			{"Bluetooth",			"",		"isBluetoothActive()"},
			{"Bluetooth",			"",		"isBluetoothConnected()"},
			{"Loccation",			"",		"isGpsLocationProviderAvailable()"},
			{"Loccation",			"",		"isLocationProviderAvailable()"},
			{"Mobile network",		"",		"isMobileNetworkConnected()"},
			{"Loccation",			"",		"isNetworkLocationProviderAvailable()"},
			{"Orientation", 		"",		"isOrientationLandscape()"},
			{"Proximity sensor",	"",		"isProximitySensorDetected()"},
			{"Ringer mode",			"",		"isRingerModeNormal()"},
			{"Ringer mode",			"",		"isRingerModeSilent()"},
			{"Ringer mode",			"",		"isRingerModeVibrate()"},
			{"Screen",				"",		"isScreenLocked()"},
			{"Screen",				"",		"isScreenOn()"},
			{"Telephony",			"",		"isTelephonyCallStateIdle()"},
			{"Telephony",			"",		"isTelephonyCallStateOffhook()"},
			{"Telephony",			"",		"isTelephonyCallStateRinging()"},
			{"Security",			"",		"isTrusted()"},
			{"WiFi",				"",		"isWifiActive()"},
			{"WiFi",				"",		"isWifiConnected()"},
			{"Message",				"",		"logMsg(String log_category, String log_message)"},
			{"Playback sound",		"",		"playBackDefaultAlarm()"},
			{"Playback sound",		"",		"playBackDefaultNotification()"},
			{"Playback sound",		"",		"playBackDefaultRingtone()"},
			{"Playback sound",		"",		"playBackMusic(String file_path)"},
			{"Playback sound",		"",		"playBackMusic(String file_path, int volume_left, int volume_right)"},
			{"Playback sound",		"",		"playBackRingtone(String ringtone_type, String ringtone_name)"},
			{"Playback sound",		"",		"playBackRingtone(String ringtone_type, String ringtone_name, int volume_left, int volume_right)"},
			{"Timer",				"",		"resetIntervalTimer(String interval_timer_name)"},
			{"Other",				"",		"restartScheduler()"},
			{"Account sync",		"",		"setAutoSyncDisabled()"},
			{"Account sync",		"",		"setAutoSyncEnabled()"},
			{"Bluetooth",			"",		"setBluetoothOff()"},
			{"Bluetooth",			"",		"setBluetoothOn()"},
			{"Ringer mode",			"",		"setRingerModeNormal()"},
			{"Ringer mode",			"",		"setRingerModeSilent()"},
			{"Ringer mode",			"",		"setRingerModeVibrate()"},
			{"Screen",				"",		"setScreenLocked()"},
			{"Screen",				"",		"setKeyguardDisabled()"},
			{"Screen",				"",		"setKeyguardEnabled()"},
			{"Screen",				"",		"setScreenOnAsync()"},
			{"Screen",				"",		"setScreenOnSync()"},
			{"Screen",				"",		"setScreenSwitchToHome()"},
			{"WiFi",				"",		"setWifiOff()"},
			{"WiFi",				"",		"setWifiOn()"},
			{"WiFi",				"",		"setWifiSsidDisabled()"},
			{"WiFi",				"",		"setWifiSsidRemoved()"},
			{"Message",				"",		"showMessageDialog(String meesage_text, boolean use_vibrator)"},
			{"Message",				"",		"showMessageNotification(String message_text, boolean use_vibrator, boolean use_led, String led_color)"}, 
			{"Activity",			"",		"startActivity(intent)"},
            {"Activity",			"",		"createIntent()"},
			{"Activity",			"",		"createIntentWithPackageName(String actv_package_name)"},
            {"Activity",			"",		"intentSetAction(Intent in, String action)"},
			{"Activity",			"",		"startActivity(String actv_package_name, String actv_data)"},
			{"Activity",			"",		"intentAddExtra(intent, String actv_extra_key, boolean actv_boolean)"},
			{"Activity",			"",		"intentAddExtra(intent, \"hoge key\", new boolean[]{true,false})"},
			{"Activity",			"",		"intentAddExtra(intent, String actv_extra_key, int actv_int)"},
			{"Activity",			"",		"intentAddExtra(intent, \"hoge key\", new int[]{value,value})"},
			{"Activity",			"",		"intentAddExtra(intent, String actv_extra_key, String actv_string)"},
			{"Activity",			"",		"intentAddExtra(intent, \"hoge key\", new String[] {\"hoge\",\"hoge\"})"},
//			{"Activity",			"intent=",	"startActivityBuildIntent(String actv_package_name)"},

			{"Intent",			"",		"sendBroadcastIntent(Intent intent)"},
			{"Intent",			"",		"setIntentAction(intent, String action)"},
			{"Intent",			"",		"intentAddExtra(intent, String actv_extra_key, boolean actv_boolean)"}, 
			{"Intent",			"",		"intentAddExtra(intent, \"hoge key\", new boolean[]{true,false})"},
			{"Intent",			"",		"intentAddExtra(intent, String actv_extra_key, int actv_int)"},
			{"Intent",			"",		"intentAddExtra(intent, \"hoge key\", new int[]{value,value})"},
			{"Intent",			"",		"intentAddExtra(intent, String actv_extra_key, String actv_string)"},
			{"Intent",			"",		"intentAddExtra(intent, \"hoge key\", new String[] {\"hoge\",\"hoge\"})"},
			{"Intent",			"intent=",	"buildIntent()"},
			
			{"Task",				"",		"startTask(String task_name)"},
			{"Message",				"",		"vibrateDefaultPattern()"},
			{"Task",				"",		"waitSeconds(int wait_time_seconds)"},
			{"Bluetooth",			"",		"waitUntilBluetoothConnected(int timeout_seconds)"},
			{"WiFi",				"",		"waitUntilWifiConnected(int timeout_seconds)"},
			{"Other",				"",		"getAndroidSdkInt()"} //added 2013-04-23
//			{"Magnetic-Field sensor","",	"isMagneticFieldSensorDetected()"},
//			{"Magnetic-Field sensor","",	"cancelTaskByEventMagneticFieldDetected()"},
//			{"Magnetic-Field sensor","",	"cancelTaskByEventMagneticFieldUndetected()"},
//			{"Magnetic-Field sensor","",	"addBlockEventMagneticFieldDetected()"},
//			{"Magnetic-Field sensor","",	"addBlockEventMagneticFieldUndetected()"}
			};

}

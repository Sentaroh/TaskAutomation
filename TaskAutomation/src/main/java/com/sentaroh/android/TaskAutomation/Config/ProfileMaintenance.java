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
import static com.sentaroh.android.TaskAutomation.Config.QuickTaskConstants.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.sentaroh.android.TaskAutomation.GlobalParameters;
import com.sentaroh.android.TaskAutomation.ISchedulerCallback;
import com.sentaroh.android.TaskAutomation.ISchedulerClient;
import com.sentaroh.android.TaskAutomation.R;
import com.sentaroh.android.TaskAutomation.Common.ActivityExtraDataItem;
import com.sentaroh.android.TaskAutomation.Common.EnvironmentParms;
import com.sentaroh.android.TaskAutomation.Common.ExportImportProfileListItem;
import com.sentaroh.android.TaskAutomation.Common.ProfileListItem;
import com.sentaroh.android.TaskAutomation.Common.TaskConsoleListItem;
import com.sentaroh.android.Utilities.LocalMountPoint;
import com.sentaroh.android.Utilities.NotifyEvent;
import com.sentaroh.android.Utilities.NotifyEvent.NotifyEventListener;
import com.sentaroh.android.Utilities.ThreadCtrl;
import com.sentaroh.android.Utilities.ContextMenu.CustomContextMenuItem.CustomContextMenuOnClickListener;
import com.sentaroh.android.Utilities.Dialog.CommonDialog;
import com.sentaroh.android.Utilities.Widget.CustomSpinnerAdapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RingtoneManager;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Handler;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ProfileMaintenance {

	private GlobalParameters mGlblParms=null;
	
	public ProfileMaintenance(EnvironmentParms ep, GlobalParameters gp) {
		mGlblParms=gp;
		mGlblParms.localRootDir=LocalMountPoint.getExternalStorageDir();

		mGlblParms.tcMusic=new ThreadCtrl();
		mGlblParms.mpMusic=null;
		mGlblParms.tcRingtone=new ThreadCtrl();
		mGlblParms.mpRingtone=null;
		
		buildAndroidApplicationList(mGlblParms);
		buildAvailableRingtoneList(mGlblParms);
	};
	
	final static public void releaseMediaPlayer(GlobalParameters mGlblParms) {
		if (mGlblParms.mpMusic!=null) {
			mGlblParms.tcMusic.setDisabled();
			mGlblParms.mpMusic.reset();
			mGlblParms.mpMusic.release();
		}
		if (mGlblParms.mpRingtone!=null) {
			mGlblParms.tcMusic.setDisabled();
			mGlblParms.mpRingtone.reset();
			mGlblParms.mpRingtone.release();
		}
	};

//	@SuppressLint("InlinedApi")
//	final static public SharedPreferences getPrefsMgr() {
//        return context.getSharedPreferences(DEFAULT_PREFS_FILENAME,
//        		Context.MODE_static static public|Context.MODE_MULTI_PROCESS);
//    };
//
    final static public ArrayList<RingtoneListItem> addRingtoneList(GlobalParameters mGlblParms,int type) {
		ArrayList<RingtoneListItem> rl=new ArrayList<RingtoneListItem>();
        RingtoneManager rm = new RingtoneManager(mGlblParms.context);
        if (rm!=null) {
            rm.setType(type);
            Cursor cursor=null;
            try {
            	cursor = rm.getCursor();
                int idx=0;
                while (cursor.moveToNext()) {
                	RingtoneListItem rli=new RingtoneListItem();
                	rli.ringtone_type=type;
                	rli.ringtone_name=cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
                	
                	rli.ringtone_uri=rm.getRingtoneUri(idx);
                	rli.index_no=idx;
                	idx++;
                	if (mGlblParms==null || mGlblParms.util==null) break;
                	mGlblParms.util.addDebugMsg(3, "I", "Ringtone name=",rli.ringtone_name,
        					", type=", String.valueOf(rli.ringtone_type), ", uri=", rli.ringtone_uri.getPath());
        			rl.add(rli);
                }
                rm=null;
                cursor.close();
            } catch(Exception e) {
            	mGlblParms.util.addLogMsg("E", "addRingtoneList failed, type="+type);
            	StackTraceElement[] st=e.getStackTrace();
            	String st_msg="";
            	for (int i=0;i<st.length;i++) {
            		st_msg+="\n at "+st[i].getClassName()+"."+
            				st[i].getMethodName()+"("+st[i].getFileName()+
            				":"+st[i].getLineNumber()+")";
            	}
            	mGlblParms.util.addLogMsg("E", e.getMessage());
            	mGlblParms.util.addLogMsg("E", st_msg);
            }
            
        }
        return rl;
	};
	
	final static public void buildAvailableRingtoneList(final GlobalParameters mGlblParms) {
		mGlblParms.util.addDebugMsg(1, "I", "buildAvailableRingtoneList has started");
		mGlblParms.ringtoneList=new ArrayList<RingtoneListItem>();
		mGlblParms.ringtoneList.addAll(addRingtoneList(mGlblParms,RingtoneManager.TYPE_ALARM));
		mGlblParms.ringtoneList.addAll(addRingtoneList(mGlblParms,RingtoneManager.TYPE_NOTIFICATION));
		mGlblParms.ringtoneList.addAll(addRingtoneList(mGlblParms,RingtoneManager.TYPE_RINGTONE));
		mGlblParms.util.addDebugMsg(1, "I", "buildAvailableRingtoneList was ended, count=", String.valueOf(mGlblParms.ringtoneList.size()));
//		Thread th=new Thread() {			
//			@Override
//			public void run() {
//			}
//		};
//		th.setPriority(Thread.MIN_PRIORITY);
//		th.start();
	};

	final static public void buildAndroidApplicationList(final GlobalParameters mGlblParms) {
		mGlblParms.androidApplicationList=new ArrayList<String>();
		mGlblParms.util.addDebugMsg(1, "I", "BuildAndroidApplicationList has started");
		Thread th=new Thread() {			
			@Override
			public void run() {
				final PackageManager pm = mGlblParms.context.getPackageManager();
				if (pm!=null && mGlblParms!=null && mGlblParms.androidApplicationList!=null) {
					List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
					for (ApplicationInfo packageInfo : packages) {
						 if(pm.getLaunchIntentForPackage(packageInfo.packageName)!= null &&   
						    !pm.getLaunchIntentForPackage(packageInfo.packageName).equals("")){
							 if (mGlblParms!=null && mGlblParms.androidApplicationList!=null) {
								 mGlblParms.androidApplicationList.add(
								 		pm.getApplicationLabel(packageInfo)+"("+
							    		packageInfo.packageName+")");
							 } else break;
						 }
					}
					if (mGlblParms!=null && mGlblParms.util!=null)
						mGlblParms.util.addDebugMsg(1, "I", "BuildAndroidApplicationList was ended, count=", String.valueOf(mGlblParms.androidApplicationList.size()));
				}
			}
		};
		th.setPriority(Thread.MIN_PRIORITY);
		th.start();
	};
	
	final static public void restoreQuickTaskParm(GlobalParameters mGlblParms) {
		mGlblParms.util.addDebugMsg(2, "I", "restoreQuickTaskParm entered");
		if (mGlblParms.importedSettingParmList!=null && mGlblParms.importedSettingParmList.size()!=0) {
			Editor editor=mGlblParms.util.getPrefMgr().edit();
			for (int i=0;i<mGlblParms.importedSettingParmList.size();i++) {
				if (mGlblParms.importedSettingParmList.get(i)[0].startsWith("quick_task")) {
					if (mGlblParms.importedSettingParmList.get(i)[1].equals(PROFILE_SETTINGS_TYPE_STRING)) {
						editor.putString(mGlblParms.importedSettingParmList.get(i)[0],mGlblParms.importedSettingParmList.get(i)[2]);
						mGlblParms.util.addDebugMsg(2,"I","Imported Settings=",
								mGlblParms.importedSettingParmList.get(i)[0], "=", mGlblParms.importedSettingParmList.get(i)[2]);
					} else if (mGlblParms.importedSettingParmList.get(i)[1].equals(PROFILE_SETTINGS_TYPE_BOOLEAN)) {
						boolean b_val = false;
						if (mGlblParms.importedSettingParmList.get(i)[2].equals("false")) b_val = false;
						else b_val = true;
						editor.putBoolean(mGlblParms.importedSettingParmList.get(i)[0],b_val);
						mGlblParms.util.addDebugMsg(2,"I","Imported Settings=",
								mGlblParms.importedSettingParmList.get(i)[0], "=", mGlblParms.importedSettingParmList.get(i)[2]);
					} else if (mGlblParms.importedSettingParmList.get(i)[1].equals(PROFILE_SETTINGS_TYPE_INT)) {
						int i_val = 0;
						i_val = Integer.parseInt(mGlblParms.importedSettingParmList.get(i)[2]);;
						editor.putInt(mGlblParms.importedSettingParmList.get(i)[0],i_val);
						mGlblParms.util.addDebugMsg(2,"I","Imported Settings=", 
								mGlblParms.importedSettingParmList.get(i)[0], "=", mGlblParms.importedSettingParmList.get(i)[2]);
					}
				}
			}
			editor.commit();
		}
	};

	final static public void restoreSettingParms(GlobalParameters mGlblParms) {
		mGlblParms.util.addDebugMsg(2, "I", "restoreSettingParms entered");
		if (mGlblParms.importedSettingParmList.size()>=0) {
			Editor editor=mGlblParms.util.getPrefMgr().edit();
			for (int i=0;i<mGlblParms.importedSettingParmList.size();i++) {
				if (!mGlblParms.importedSettingParmList.get(i)[0].startsWith("quick_task")) {
					if (mGlblParms.importedSettingParmList.get(i)[1].equals(PROFILE_SETTINGS_TYPE_STRING)) {
						editor.putString(mGlblParms.importedSettingParmList.get(i)[0],
								mGlblParms.importedSettingParmList.get(i)[2]);
						mGlblParms.util.addDebugMsg(2,"I","Imported Settings=",
								mGlblParms.importedSettingParmList.get(i)[0],"=",
								mGlblParms.importedSettingParmList.get(i)[2]);
					} else if (mGlblParms.importedSettingParmList.get(i)[1].equals(PROFILE_SETTINGS_TYPE_BOOLEAN)) {
						boolean b_val = false;
						if (mGlblParms.importedSettingParmList.get(i)[2].equals("false")) b_val = false;
						else b_val = true;
						editor.putBoolean(mGlblParms.importedSettingParmList.get(i)[0],b_val);
						mGlblParms.util.addDebugMsg(2,"I","Imported Settings=",
								mGlblParms.importedSettingParmList.get(i)[0],"=",
								mGlblParms.importedSettingParmList.get(i)[2]);
					} else if (mGlblParms.importedSettingParmList.get(i)[1].equals(PROFILE_SETTINGS_TYPE_INT)) {
						int i_val = 0;
						i_val = Integer.parseInt(mGlblParms.importedSettingParmList.get(i)[2]);;
						editor.putInt(mGlblParms.importedSettingParmList.get(i)[0],i_val);
						mGlblParms.util.addDebugMsg(2,"I","Imported Settings=",
								mGlblParms.importedSettingParmList.get(i)[0],"=",
								mGlblParms.importedSettingParmList.get(i)[2]);
					}
				}
			}
			editor.commit();
		}
	};

//	final static public void replaceProfileAdapterList(GlobalParameters mGlblParms, AdapterProfileList pflaTo,
//			ArrayList<ProfileListItem> from) {
////		pflaTo.clear();
////		for (int i=0;i<pflaFrom.getDataListCount();i++) 
////			pflaTo.addProfItem(pflaFrom.getDataListItem(i));
//		pflaTo.setDataList(from);
//		pflaTo.updateShowList();
//		mGlblParms.util.addDebugMsg(2, "I", 
//				"replaceProfileAdapterList DataItemCount=", String.valueOf(pflaTo.getDataListCount()));
//	};
	
	final static public void importProfileDlg(final GlobalParameters mGlblParms,final NotifyEvent p_ntfy,
			final AdapterProfileList pfla, final ListView pflv) {
		NotifyEvent ntfy=new NotifyEvent(mGlblParms.context);
		// set mGlblParms.commonDlg.showCommonDialog response 
		ntfy.setListener(new NotifyEventListener() {
			@Override
			final public void positiveResponse(Context c, Object[] o) {
				importProfileSelectItem(mGlblParms, pfla, (String)o[0], p_ntfy);
			}
			@Override
			final public void negativeResponse(Context c, Object[] o) {
				p_ntfy.notifyToListener(false, null);
			}
		});
		mGlblParms.commonDlg.fileOnlySelectWithoutCreate(
				mGlblParms.localRootDir,"/"+APPLICATION_TAG,"profile.txt",
				mGlblParms.context.getString(R.string.msgs_import_profile_select_file),ntfy);
	};
	
	final static private void importProfileSelectItem(final GlobalParameters mGlblParms, final AdapterProfileList pfla, 
			final String fp, final NotifyEvent p_ntfy) {
		final AdapterProfileList tfl = loadProfileAdapterFromFile(mGlblParms,true,fp);
		if (tfl==null) {
			mGlblParms.commonDlg.showCommonDialog(false,"E",
					mGlblParms.context.getString(R.string.msgs_import_profile_import_fail),"File="+fp,null);
			return;
		}
		//create selection list
		final ArrayList<ExportImportProfileListItem> epl=new ArrayList<ExportImportProfileListItem>();
		ExportImportProfileListItem epli;
		for (int i=0;i<tfl.getDataListCount();i++) {
			String grp=tfl.getDataListItem(i).getProfileGroup();
			tfl.getDataListItem(i).setProfileGroupActivated(false);//set profile group to inactive
			boolean found=false;
			for (int j=0;j<epl.size();j++) {
				if (epl.get(j).item_name.equals(grp)) {
					found=true;
					break;
				}
			}
			epli=new ExportImportProfileListItem();
			epli.item_name=grp;
			epli.isChecked=true;
			if (!found) {
				epl.add(epli);
			}
		}
		if (mGlblParms.importedSettingParmList.size()!=0) {
			epli=new ExportImportProfileListItem();
			epli.item_name=EXPORT_IMPORT_SETTING_NAME;
			epli.isChecked=true;
			epl.add(epli);
		}
		if (epl.size()==0) {
			mGlblParms.commonDlg.showCommonDialog(false, "E", 
					mGlblParms.context.getString(R.string.msgs_import_profile_noitem_title),
					mGlblParms.context.getString(R.string.msgs_import_profile_noitem_msg),
					null);
			return;
		}
		
		// カスタムダイアログの生成
		final Dialog dialog = new Dialog(mGlblParms.context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(R.layout.export_import_profile_dlg);
		final TextView dlg_msg = (TextView) dialog.findViewById(R.id.export_import_profile_msg);
		final TextView dlg_title = (TextView) dialog.findViewById(R.id.export_import_profile_title);
		final LinearLayout ll_file_list = (LinearLayout) dialog.findViewById(R.id.export_import_profile_file_list);
		final Button btnCancel = (Button) dialog.findViewById(R.id.export_import_profile_cancel_btn);
		final Button btnOK = (Button) dialog.findViewById(R.id.export_import_profile_ok_btn);
		ll_file_list.setVisibility(LinearLayout.GONE);
		dlg_title.setText(mGlblParms.context.getString(R.string.msgs_import_profile_title));
		dlg_msg.setVisibility(TextView.GONE);
		CommonDialog.setDlgBoxSizeLimit(dialog,true);

		final AdapterExportImportProfileList aeplv=new AdapterExportImportProfileList(
				mGlblParms.context,R.layout.export_import_profile_list_item,epl);
		ListView lv=(ListView)dialog.findViewById(R.id.export_import_profile_listview);
		lv.setAdapter(aeplv);
		
		lv.setOnItemClickListener(new OnItemClickListener(){
			@Override
			final public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
					epl.get(pos).isChecked=!epl.get(pos).isChecked;
					btnOK.setEnabled(false);
					for (int i=0;i<epl.size();i++)
						  if (epl.get(i).isChecked) btnOK.setEnabled(true);
					aeplv.notifyDataSetChanged();
			}
		});

		lv.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			final public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				mGlblParms.ccMenu.addMenuItem(
						mGlblParms.context.getString(R.string.msgs_main_context_label_select_all),R.drawable.blank)
			  	.setOnClickListener(new CustomContextMenuOnClickListener() {
				  @Override
				  final public void onClick(CharSequence menuTitle) {
					  for (int i=0;i<epl.size();i++)
						  epl.get(i).isChecked=true;
					  aeplv.notifyDataSetChanged();
					  btnOK.setEnabled(true);
				  	}
			  	});
				mGlblParms.ccMenu.addMenuItem(
						mGlblParms.context.getString(R.string.msgs_main_context_label_unselect_all),R.drawable.blank)
			  	.setOnClickListener(new CustomContextMenuOnClickListener() {
				  @Override
				  final public void onClick(CharSequence menuTitle) {
					  for (int i=0;i<epl.size();i++)
						  epl.get(i).isChecked=false;
					  aeplv.notifyDataSetChanged();
					  btnOK.setEnabled(false);
				  	}
			  	});
				mGlblParms.ccMenu.createMenu();
				return false;
			}
		});
		
		final Button btn_select_all=(Button)dialog.findViewById(R.id.export_import_profile_list_btn_select_all);
		final Button btn_unselect_all=(Button)dialog.findViewById(R.id.export_import_profile_list_btn_unselect_all);
		
		btn_select_all.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				for (int i=0;i<epl.size();i++) epl.get(i).isChecked=true;
				aeplv.notifyDataSetChanged();
				btnOK.setEnabled(true);
			}
		});

		btn_unselect_all.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				for (int i=0;i<epl.size();i++) epl.get(i).isChecked=false;
				aeplv.notifyDataSetChanged();
				btnOK.setEnabled(false);
			}
		});
		
		NotifyEvent ntfy=new NotifyEvent(mGlblParms.context);
		ntfy.setListener(new NotifyEventListener(){
			@Override
			final public void positiveResponse(Context c, Object[] o) {
				btnOK.setEnabled(false);
				for (int i=0;i<epl.size();i++)
					  if (epl.get(i).isChecked) btnOK.setEnabled(true);
			}
			@Override
			final public void negativeResponse(Context c, Object[] o) {}
		});
		aeplv.setCheckButtonListener(ntfy);

		// CANCELボタンの指定
		btnCancel.setOnClickListener(new View.OnClickListener() {
			final public void onClick(View v) {
				dialog.dismiss();
			}
		});
		// OKボタンの指定
		btnOK.setOnClickListener(new View.OnClickListener() {
			final public void onClick(View v) {
				dialog.dismiss();
				boolean quick_task=false, settings=false;
				for (int i=0;i<epl.size();i++) 
					if (epl.get(i).isChecked && 
							epl.get(i).item_name.equals(QUICK_TASK_GROUP_NAME)) quick_task=true;
				for (int i=0;i<epl.size();i++) 
					if (epl.get(i).isChecked && 
							epl.get(i).item_name.equals(EXPORT_IMPORT_SETTING_NAME)) settings=true;
				importProfileConfirm(mGlblParms,pfla,tfl,settings,quick_task,epl,fp, p_ntfy);
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
	};
	
	final static private void importProfileConfirm(final GlobalParameters mGlblParms,final AdapterProfileList pfla, 
			final AdapterProfileList tfl, final boolean settings, final boolean quick_task,
			final ArrayList<ExportImportProfileListItem> epl, final String fpath,
			final NotifyEvent p_ntfy) {
		String conf_name="";
		String imp_name="";
		for (int i=0;i<epl.size();i++) {
			if (epl.get(i).isChecked) {
				if (ProfileUtilities.isProfileGroupExists(mGlblParms.util,pfla,epl.get(i).item_name) ||
						epl.get(i).item_name.equals(EXPORT_IMPORT_SETTING_NAME)) {
					conf_name+=epl.get(i).item_name+"\n";
				}
				imp_name+=epl.get(i).item_name+"\n";
			}
		}
		if (!conf_name.equals("")) {//Confirmation required
			NotifyEvent ntfy=new NotifyEvent(mGlblParms.context);
			ntfy.setListener(new NotifyEventListener(){
				@Override
				final public void positiveResponse(Context c, Object[] o) {
					for (int i=0;i<epl.size();i++) {
						if (epl.get(i).isChecked) {
							ProfileUtilities.deleteProfileGroup(mGlblParms.util,pfla,epl.get(i).item_name);
						}
					}
					importProfileFromFile(mGlblParms,pfla,tfl,settings,quick_task,epl,fpath);
					p_ntfy.notifyToListener(true, null);
				}
				@Override
				final public void negativeResponse(Context c, Object[] o) {
					p_ntfy.notifyToListener(false, null);
				}
				
			});
			mGlblParms.commonDlg.showCommonDialog(true, "W",
					mGlblParms.context.getString(R.string.msgs_import_profile_confirm_title),
					mGlblParms.context.getString(R.string.msgs_import_profile_confirm1_msg)+
					"\n"+imp_name+"\n"+
					mGlblParms.context.getString(R.string.msgs_import_profile_confirm2_msg)+
					"\n"+conf_name,ntfy);
		} else {
			importProfileFromFile(mGlblParms,pfla,tfl,settings,quick_task,epl,fpath);
			p_ntfy.notifyToListener(true, null);
		}
	};
	
	final static private void importProfileFromFile(GlobalParameters mGlblParms, final AdapterProfileList pfla, 
			final AdapterProfileList tfl, boolean settings, boolean quick_task,
			final ArrayList<ExportImportProfileListItem> epl, String fpath) {
		for (int i=0;i<tfl.getDataListCount();i++) {
			boolean found=false;
			for (int j=0;j<epl.size();j++) 
				if (epl.get(j).isChecked &&
						tfl.getDataListItem(i).getProfileGroup().equals(epl.get(j).item_name)) {
					found=true;
					break;
				}
			if (found) {
				pfla.addDataListItem(tfl.getDataListItem(i));
			}
		}
		if (settings) restoreSettingParms(mGlblParms);
		if (quick_task) {
			restoreQuickTaskParm(mGlblParms);
			ProfileUtilities.deleteProfileGroup(mGlblParms.util,pfla, QUICK_TASK_GROUP_NAME);
			QuickTaskMaintenance.buildQuickTaskProfile(mGlblParms.context, pfla, 
					mGlblParms.util, QUICK_TASK_GROUP_NAME);

		}
		pfla.sort();
		pfla.notifyDataSetChanged();
		putProfileListToService(mGlblParms,pfla,true);
//		saveProfileToFileProfileOnly(false,pfla,null,"","");
//		mGlblParms.util.reBuildTaskExecList();
		mGlblParms.commonDlg.showCommonDialog(false,"I",
				mGlblParms.context.getString(R.string.msgs_import_profile_import_success),"File="+fpath,null); 
		mGlblParms.util.addDebugMsg(1,"I","Profile was imported. fn=",fpath);

	};
	
	final static public void exportProfileDlg(final GlobalParameters mGlblParms,final AdapterProfileList pfla, final ListView pflv) {
		// カスタムダイアログの生成
		final Dialog dialog = new Dialog(mGlblParms.context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(R.layout.export_import_profile_dlg);
		final TextView dlg_msg = (TextView) dialog.findViewById(R.id.export_import_profile_msg);
		final TextView dlg_title = (TextView) dialog.findViewById(R.id.export_import_profile_title);
		final EditText dlg_filename=(EditText)dialog.findViewById(R.id.export_import_profile_filename);
		final Button btnCancel = (Button) dialog.findViewById(R.id.export_import_profile_cancel_btn);
		final Button btnOK = (Button) dialog.findViewById(R.id.export_import_profile_ok_btn);
		dlg_title.setText(mGlblParms.context.getString(R.string.msgs_export_profile_title));
		dlg_filename.setText(mGlblParms.localRootDir+"/"+APPLICATION_TAG+"/"+"profile.txt");
		
		CommonDialog.setDlgBoxSizeLimit(dialog,true);
		
		final ArrayList<ExportImportProfileListItem> epl=new ArrayList<ExportImportProfileListItem>();
		ExportImportProfileListItem epli;
		for (int i=0;i<pfla.getDataListCount();i++) {
			String grp=pfla.getDataListItem(i).getProfileGroup();
			boolean found=false;
			for (int j=0;j<epl.size();j++) {
				if (epl.get(j).item_name.equals(grp)) {
					found=true;
					break;
				}
			}
			epli=new ExportImportProfileListItem();
			epli.item_name=grp;
			epli.isChecked=true;
			if (!found) {
				epl.add(epli);
			}
		}
		epli=new ExportImportProfileListItem();
		epli.item_name=EXPORT_IMPORT_SETTING_NAME;
		epli.isChecked=true;
		epl.add(epli);
		
		final AdapterExportImportProfileList aeplv=new AdapterExportImportProfileList(
				mGlblParms.context,R.layout.export_import_profile_list_item,epl);
		ListView lv=(ListView)dialog.findViewById(R.id.export_import_profile_listview);
		lv.setAdapter(aeplv);
		
		lv.setOnItemClickListener(new OnItemClickListener(){
			@Override
			final public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
					epl.get(pos).isChecked=!epl.get(pos).isChecked;
					btnOK.setEnabled(false);
					for (int i=0;i<epl.size();i++)
						  if (epl.get(i).isChecked) btnOK.setEnabled(true);
					aeplv.notifyDataSetChanged();
			}
		});

		lv.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			final public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				mGlblParms.ccMenu.addMenuItem(
						mGlblParms.context.getString(R.string.msgs_main_context_label_select_all),R.drawable.blank)
			  	.setOnClickListener(new CustomContextMenuOnClickListener() {
				  @Override
				  final public void onClick(CharSequence menuTitle) {
					  for (int i=0;i<epl.size();i++)
						  epl.get(i).isChecked=true;
					  aeplv.notifyDataSetChanged();
					  btnOK.setEnabled(true);
				  	}
			  	});
				mGlblParms.ccMenu.addMenuItem(
						mGlblParms.context.getString(R.string.msgs_main_context_label_unselect_all),R.drawable.blank)
			  	.setOnClickListener(new CustomContextMenuOnClickListener() {
				  @Override
				  final public void onClick(CharSequence menuTitle) {
					  for (int i=0;i<epl.size();i++)
						  epl.get(i).isChecked=false;
					  aeplv.notifyDataSetChanged();
					  btnOK.setEnabled(false);
				  	}
			  	});
				mGlblParms.ccMenu.createMenu();
				return false;
			}
		});
		
		final Button btn_select_all=(Button)dialog.findViewById(R.id.export_import_profile_list_btn_select_all);
		final Button btn_unselect_all=(Button)dialog.findViewById(R.id.export_import_profile_list_btn_unselect_all);
		
		btn_select_all.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				for (int i=0;i<epl.size();i++) epl.get(i).isChecked=true;
				aeplv.notifyDataSetChanged();
				btnOK.setEnabled(true);
			}
		});

		btn_unselect_all.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				for (int i=0;i<epl.size();i++) epl.get(i).isChecked=false;
				aeplv.notifyDataSetChanged();
				btnOK.setEnabled(false);
			}
		});

		NotifyEvent ntfy=new NotifyEvent(mGlblParms.context);
		ntfy.setListener(new NotifyEventListener(){
			@Override
			final public void positiveResponse(Context c, Object[] o) {
				btnOK.setEnabled(false);
				for (int i=0;i<epl.size();i++) 
					  if (epl.get(i).isChecked) btnOK.setEnabled(true);
			}
			@Override
			final public void negativeResponse(Context c, Object[] o) {}
		});
		aeplv.setCheckButtonListener(ntfy);

		// Listingボタンの指定
		final Button btnListing=(Button)dialog.findViewById(R.id.export_import_profile_listing);
		btnListing.setOnClickListener(new View.OnClickListener() {
			final public void onClick(View v) {
				NotifyEvent ntfy_listing=new NotifyEvent(mGlblParms.context);
				// set mGlblParms.commonDlg.showCommonDialog response 
				ntfy_listing.setListener(new NotifyEventListener() {
					@Override
					final public void positiveResponse(Context c, Object[] o) {
		    			dlg_filename.setText((String)o[0]);
					}
					@Override
					final public void negativeResponse(Context c, Object[] o) {}
				});
				mGlblParms.commonDlg.fileOnlySelectWithCreate(
						mGlblParms.localRootDir,"/"+APPLICATION_TAG,"profile.txt",
						mGlblParms.context.getString(R.string.msgs_export_profile_select_file),ntfy_listing);
			}
		});
		
		
		// CANCELボタンの指定
		btnCancel.setOnClickListener(new View.OnClickListener() {
			final public void onClick(View v) {
				dialog.dismiss();
			}
		});
		// OKボタンの指定
		btnOK.setOnClickListener(new View.OnClickListener() {
			final public void onClick(View v) {
				dlg_filename.selectAll();
				if (dlg_filename.getText().toString().equals("")) {
					dlg_msg.setText(mGlblParms.context.getString(R.string.msgs_export_import_profile_specify_filename));
					return;
				}
				dialog.dismiss();
				AdapterProfileList pfl = new AdapterProfileList(mGlblParms.context, 
						R.layout.task_profile_list_view_item, new ArrayList<ProfileListItem>() );
				boolean quick_task=false;
				for (int i=0;i<epl.size();i++) 
					if (epl.get(i).isChecked && 
							epl.get(i).item_name.equals(QUICK_TASK_GROUP_NAME)) quick_task=true;
				for (int i=0;i<pfla.getDataListCount();i++) {
					boolean found=false;
					for (int j=0;j<epl.size();j++) 
						if (epl.get(j).isChecked &&
								pfla.getDataListItem(i).getProfileGroup().equals(epl.get(j).item_name)) {
							found=true;
							break;
						}
					if (found) pfl.addDataListItem(pfla.getDataListItem(i));
				}
				boolean settings=false;
				for (int i=0;i<epl.size();i++) 
					if (epl.get(i).isChecked && 
							epl.get(i).item_name.equals(EXPORT_IMPORT_SETTING_NAME)) settings=true;
				String fpath=dlg_filename.getText().toString();
    			String fd=fpath.substring(0,fpath.lastIndexOf("/"));
    			String fn=fpath.replace(fd+"/","");
    			exportProfileToFile(mGlblParms,pfl,pflv,fd,fn,settings,quick_task);
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
	};

	final static private void exportProfileToFile(final GlobalParameters mGlblParms,final AdapterProfileList pfla, final ListView pflv,
			final String profile_dir, final String profile_filename, 
			final boolean settings, final boolean quick_task) {
		
		File lf = new File(profile_dir+"/"+profile_filename);
		NotifyEvent ntfy=new NotifyEvent(mGlblParms.context);
		// set mGlblParms.commonDlg.showCommonDialog response 
		ntfy.setListener(new NotifyEventListener() {
			@Override
			final public void positiveResponse(Context c, Object[] o) {
    			String fp =profile_dir+"/"+profile_filename;
    			String fd =profile_dir;
    			String result=null;
    			result=mGlblParms.util.saveProfileToFile(true,settings,
    					quick_task, pfla.getDataList(),
    					pflv,fd,profile_filename);
				if (result==null) {
					mGlblParms.commonDlg.showCommonDialog(false,
						"I",String.format(mGlblParms.context.getString(
						R.string.msgs_export_profile_import_success),fp),"",null);
					mGlblParms.util.addDebugMsg(1,"I","Profile was exported. fn=",fp);						
				} else {
					mGlblParms.commonDlg.showCommonDialog(false,"E",
						mGlblParms.context.getString(R.string.msgs_export_profile_import_fail),
						"File="+fp+"\n"+result,null);
				}
			}

			@Override
			final public void negativeResponse(Context c, Object[] o) {}
		});
		if (lf.exists()) {
			mGlblParms.commonDlg.showCommonDialog(true,"W",
					mGlblParms.context.getString(R.string.msgs_export_profile_title),
					profile_dir+"/"+profile_filename+" "+
							mGlblParms.context.getString(R.string.msgs_export_profile_override),ntfy);
		} else {
			ntfy.notifyToListener(true, null);
		}
	};


	final static public void setProfileToEnable(GlobalParameters mGlblParms,final String curr_grp, 
			AdapterProfileList pfla, ListView pflv) {
		ProfileListItem item;

		for (int i=0;i<pfla.getProfItemCount();i++) {
			item = pfla.getProfItem(i);

			if (item.isProfileItemSelected()) {
				item.setProfileEnabled(PROFILE_ENABLED);
//				pfla.replaceProfItem(i,item);
			} 
		}
		ProfileUtilities.verifyProfileIntegrity(mGlblParms.util,true, pfla, curr_grp);
		putProfileListToService(mGlblParms,pfla,ProfileUtilities.isProfileGroupActive(mGlblParms.util,pfla,curr_grp));
		pfla.notifyDataSetChanged();
//		saveProfileToFileProfileOnly(false,pfla, pflv,"","");
//		if (isProfileGroupActive(pfla,curr_grp)) mGlblParms.util.reBuildTaskExecList();
		//createProfileList(false);
		
	}
	
	final static public void setProfileToDisable(GlobalParameters mGlblParms, final String curr_grp, 
			AdapterProfileList pfla, ListView pflv) {
		ProfileListItem item;

		for (int i=0;i<pfla.getProfItemCount();i++) {
			item = pfla.getProfItem(i);

			if (item.isProfileItemSelected()) {
				item.setProfileEnabled(PROFILE_DISABLED);
//				pfla.replaceProfItem(i,item);
			} 
		}
		ProfileUtilities.verifyProfileIntegrity(mGlblParms.util,true, pfla, curr_grp);
		putProfileListToService(mGlblParms,pfla,ProfileUtilities.isProfileGroupActive(mGlblParms.util,pfla,curr_grp));
		pfla.notifyDataSetChanged();
//		saveProfileToFileProfileOnly(false,pfla,pflv,"","");
//		if (isProfileGroupActive(pfla,curr_grp)) mGlblParms.util.reBuildTaskExecList();
		//createProfileList(false);
	}

	
	final static public void setAllProfileItemUnChecked(GlobalParameters mGlblParms,final String curr_grp, 
			AdapterProfileList pfla, ListView pflv) {
		ProfileListItem item;
		for (int i=0;i<pfla.getDataListCount();i++) {
			item=pfla.getDataListItem(i);
			item.setProfileItemSelected(false);
//			pfla.replaceDataListItem(i,item);
		}
		pfla.notifyDataSetChanged();
	};

	final static public void setAllProfileItemChecked(GlobalParameters mGlblParms,final String curr_grp, 
			AdapterProfileList pfla, ListView pflv) {
		ProfileListItem item;
		for (int i=0;i<pfla.getProfItemCount();i++) {
			item=pfla.getProfItem(i);
			item.setProfileItemSelected(true);
//			pfla.replaceProfItem(i,item);
		}
		pfla.notifyDataSetChanged();
	};

	final static public void setProfileItemChecked(GlobalParameters mGlblParms, 
			AdapterProfileList pfla, int pos, boolean checked) {
		pfla.getProfItem(pos).setProfileItemSelected(checked);
	};

	final static public boolean isAnyProfileItemSelected(GlobalParameters mGlblParms, 
			AdapterProfileList pfla, String curr_grp) {
		boolean result=false;
		for(int i=0;i<pfla.getProfItemCount();i++) {
			if (pfla.getProfItem(i).getProfileGroup().equals(curr_grp) && 
					pfla.getProfItem(i).isProfileItemSelected()) {
				result=true;
				break;
			}
		}
		return result;
	};

//	final static public void refreshProfileAdapter(AdapterProfileList pfla, ListView pflv) {
//		AdapterProfileList tpfa=createProfileList(false,"");
//		replaceProfileAdapter(pfla,tpfa);
//		pflv.setAdapter(pfla);
//	};
	

	
	final static public void setISchedulerClient(GlobalParameters mGlblParms,ISchedulerClient svc) {
		if (mGlblParms.svcServer==null) {
			mGlblParms.svcServer=svc;
		} else {
			mGlblParms.svcServer=svc;
		}
	};
	
	final static private void removeConsoleCallBackListener(GlobalParameters mGlblParms) {
		try {
			mGlblParms.svcServer.removeCallBack(mGlblParms.consoleCallbackListener);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	};

	final static private void setConsoleCallbackListener(
			final GlobalParameters mGlblParms,
			final ListView taskConsoleListView,
			final AdapterTaskConsoleList taskConsoleAdapter,
			final Button btnCancelImmTask) {
		final Handler handler=new Handler();
		mGlblParms.consoleCallbackListener = new ISchedulerCallback.Stub() {
			final public void notifyToClient(String resp_time, final String resp, 
					final String grp,final String task, final String action, final String shell_cmd,
					String dialog_id, final int atc, final int resp_cd, final String msg) 
							throws RemoteException {
				mGlblParms.util.addDebugMsg(2, "I", "Notify received ",
					"Resp=",resp,", Task=",task,", action=",action,", dialog_id=",dialog_id,
					", resp_cd=",String.valueOf(resp_cd),", msg=",msg);
				if (taskConsoleAdapter!=null && grp.equals(IMMEDIATE_TASK_EXEC_GROUP_NAME)) {
					handler.post(new Runnable() {
						@Override
	                    public void run() {
							TaskConsoleListItem cli=new TaskConsoleListItem();
							if (resp.equals(NTFY_TO_ACTV_TASK_STARTED)) {
								cli.item_type=TaskConsoleListItem.ITEM_TYPE_TASK;
								cli.group_name=grp;
								cli.task_name=task;
								cli.item_start=true;
								cli.result_code=resp_cd;
								taskConsoleAdapter.add(cli);
							} else if (resp.equals(NTFY_TO_ACTV_TASK_ENDED)) {
								cli.item_type=TaskConsoleListItem.ITEM_TYPE_TASK;
								cli.group_name=grp;
								cli.task_name=task;
								cli.item_start=false;
								cli.result_code=resp_cd;
								taskConsoleAdapter.add(cli);
								btnCancelImmTask.setEnabled(false);
							} else if (resp.equals(NTFY_TO_ACTV_ACTION_STARTED)) {
								cli.item_type=TaskConsoleListItem.ITEM_TYPE_ACTION;
								cli.action_name=action;
								cli.item_start=true;
								cli.result_code=resp_cd;
								cli.shell_cmd=shell_cmd;
								taskConsoleAdapter.add(cli);
							} else if (resp.equals(NTFY_TO_ACTV_ACTION_ENDED)) {
								cli.item_type=TaskConsoleListItem.ITEM_TYPE_ACTION;
								cli.action_name=action;
								cli.item_start=false;
								cli.result_code=resp_cd;
								cli.result_msg=msg;
								cli.shell_cmd=shell_cmd;
								taskConsoleAdapter.add(cli);
							} else if (resp.equals(NTFY_TO_ACTV_ACTION_TACMD_STARTED)) {
								cli.item_type=TaskConsoleListItem.ITEM_TYPE_ACTION;
								cli.action_name=action;
								cli.item_start=true;
								cli.result_code=resp_cd;
								cli.result_msg=msg;
								taskConsoleAdapter.add(cli);
							} else if (resp.equals(NTFY_TO_ACTV_ACTION_TACMD_ENDED)) {
								cli.item_type=TaskConsoleListItem.ITEM_TYPE_ACTION;
								cli.action_name=action;
								cli.item_start=false;
								cli.result_code=resp_cd;
								cli.result_msg=msg;
								taskConsoleAdapter.add(cli);
							}
							taskConsoleAdapter.notifyDataSetChanged();
							taskConsoleListView.setSelection(taskConsoleAdapter.getCount());
						}
					});
				}
			}
        };
		try{
			mGlblParms.svcServer.setCallBack(mGlblParms.consoleCallbackListener);
		} catch (RemoteException e){
			e.printStackTrace();
			mGlblParms.util.addLogMsg("E", "setCallbackListener error :"+e.toString());
		}
	};
	
	final static public void invokeTaskExecution(final GlobalParameters mGlblParms, 
			final String curr_grp, 
			final AdapterProfileList pfla, String prof_name, String trig_event,
			boolean prof_notify, boolean use_test_env_parms,
			ArrayList<TaskActionEditListItem> act_list) {
		String prof_active, prof_retrospec="",prof_notification=PROFILE_ERROR_NOTIFICATION_ENABLED;
		prof_active=PROFILE_ENABLED;
		if (!prof_notify) prof_notification=PROFILE_ERROR_NOTIFICATION_DISABLED;

		final ProfileListItem ntpli=new ProfileListItem();
		ArrayList<String> trig=new ArrayList<String>();
		trig.add(trig_event);

		ArrayList<String>string_act_list=new ArrayList<String>();
		for (int i=0;i<act_list.size();i++) string_act_list.add(act_list.get(i).action);

		ntpli.setTaskEntry(
				PROFILE_VERSION_CURRENT,IMMEDIATE_TASK_EXEC_GROUP_NAME,true,
				System.currentTimeMillis(),
				PROFILE_TYPE_TASK,
				prof_name,
				prof_active,
				prof_retrospec,
				"0",
				prof_notification,
				string_act_list,
				trig);
		
		// common カスタムダイアログの生成
		final Dialog dialog = new Dialog(mGlblParms.context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(R.layout.task_exec_console_dlg);

		TextView title = 
				(TextView) dialog.findViewById(R.id.task_exec_console_dlg_title);
		title.setText(mGlblParms.context.getString(R.string.msgs_execute_dialog_title));
		ListView taskConsoleListView = 
				(ListView)dialog.findViewById(R.id.task_exec_console_dlg_listview);
		ArrayList<TaskConsoleListItem>  taskConsoleList=new ArrayList<TaskConsoleListItem>();
		AdapterTaskConsoleList taskConsoleAdapter = 
			new AdapterTaskConsoleList(mGlblParms.context,R.layout.task_exec_console_list_item, 
					taskConsoleList, false, false);
		taskConsoleListView.setAdapter(taskConsoleAdapter);
		taskConsoleListView.setEnabled(true);
		taskConsoleListView.setSelected(true);
		taskConsoleAdapter.notifyDataSetChanged();

		final Button btnCancelImmTask = (Button) dialog.findViewById(R.id.task_exec_console_dlg_cancel_btn);
		final Button btnLog = (Button) dialog.findViewById(R.id.task_exec_console_dlg_log_btn);
		final Button btnClose = (Button) dialog.findViewById(R.id.task_exec_console_dlg_close_btn);
		
		setConsoleCallbackListener(mGlblParms,taskConsoleListView,taskConsoleAdapter,btnCancelImmTask);
		
		CommonDialog.setDlgBoxSizeLimit(dialog,true);
//		dialog.setOnKeyListener(new DialogOnKeyListener(mGlblParms.context));		

		btnCancelImmTask.setOnClickListener(new OnClickListener() {
			@Override
			final public void onClick(View v) {
				try {
					mGlblParms.svcServer.aidlCancelSpecificTask(IMMEDIATE_TASK_EXEC_GROUP_NAME, 
							ntpli.getProfileName());
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				btnCancelImmTask.setEnabled(false);
			}
		});
		
		if (mGlblParms.util.isLogFileExists()) btnLog.setEnabled(true);
		else btnLog.setEnabled(false);
		btnLog.setOnClickListener(new OnClickListener() {
			@Override
			final public void onClick(View v) {
				mGlblParms.util.resetLogReceiver();
				Intent intent = new Intent();
				intent = new Intent(android.content.Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.parse("file://"+mGlblParms.util.getLogFilePath()),
						"text/plain");
				mGlblParms.context.startActivity(intent);
			}
		});

		// Closeボタンの指定
		btnClose.setOnClickListener(new OnClickListener() {
			final public void onClick(View v) {
				dialog.dismiss();
				try {
					mGlblParms.svcServer.aidlCancelSpecificTask(IMMEDIATE_TASK_EXEC_GROUP_NAME, 
							ntpli.getProfileName());
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				removeConsoleCallBackListener(mGlblParms);
//				mGlblParms.commonDlg.setFixedOrientation(false);
			}
		});
		// Cancelリスナーの指定
		dialog.setOnCancelListener(new Dialog.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				btnClose.performClick();
			}
		});
//		mGlblParms.commonDlg.setFixedOrientation(true);
//		dialog.setCancelable(false);
		dialog.show();

		startTaskForTest(mGlblParms,curr_grp,pfla,ntpli,use_test_env_parms,taskConsoleAdapter);

	};

	final static private void startTaskForTest(final GlobalParameters mGlblParms,String curr_grp, 
			AdapterProfileList pfa, ProfileListItem task, boolean use_test_env_parms,
			AdapterTaskConsoleList taskConsoleAdapter) {
		ArrayList<ProfileListItem> task_list=new ArrayList<ProfileListItem>();
		ArrayList<ProfileListItem> time_list=new ArrayList<ProfileListItem>();
		ArrayList<ProfileListItem> action_list=new ArrayList<ProfileListItem>();
		task_list.add(task);
		if (!task.getTaskTriggerList().get(0).startsWith(BUILTIN_PREFIX)) {
			ProfileListItem t_list=ProfileUtilities.getProfileListItem(mGlblParms.util,
					pfa,curr_grp,PROFILE_TYPE_TIME,task.getTaskTriggerList().get(0));
			if (t_list!=null) {
				ProfileListItem nt_list=t_list.clone();
				nt_list.setProfileGroup(IMMEDIATE_TASK_EXEC_GROUP_NAME);
				nt_list.setProfileGroupActivated(true);
				nt_list.setProfileEnabled(PROFILE_ENABLED);
				time_list.add(nt_list);
			}
		}
		
		ArrayList<String> string_act_list=task.getTaskActionList();
		for (int i=0;i<string_act_list.size();i++) {
			if (!string_act_list.get(i).startsWith(BUILTIN_PREFIX)) {
				if (ProfileUtilities.getProfileListItemFromAll(mGlblParms.util,pfa,curr_grp,PROFILE_TYPE_ACTION,string_act_list.get(i))!=null) {
					ProfileListItem t_list=
						(ProfileListItem) ProfileUtilities.getProfileListItemFromAll(mGlblParms.util,
							pfa,curr_grp,PROFILE_TYPE_ACTION,string_act_list.get(i)).clone();
					t_list.setProfileGroup(IMMEDIATE_TASK_EXEC_GROUP_NAME);
					t_list.setProfileGroupActivated(true);
					t_list.setProfileEnabled(PROFILE_ENABLED);
					action_list.add(t_list);
				} else {
					TaskConsoleListItem tcli=new TaskConsoleListItem();
					tcli.item_type=TaskConsoleListItem.ITEM_TYPE_MESSAGE;
					tcli.task_name="Action:"+string_act_list.get(i)+" does not exists, Action was ignored";
					taskConsoleAdapter.add(tcli);
					taskConsoleAdapter.notifyDataSetChanged();
				}
			}
		}
		
		byte[] task_buf=ProfileUtilities.serializeProfilelist(task_list); 
		byte[] time_buf=ProfileUtilities.serializeProfilelist(time_list); 
		byte[] action_buf=ProfileUtilities.serializeProfilelist(action_list); 
		byte[] tep_buf=null; 
		if (use_test_env_parms) {
//			tep_buf=EnvironmentParameters.serialize(immTaskTestEnvParms);
			tep_buf=mGlblParms.immTaskTestEnvParms.serialize();
		}
		try {
			mGlblParms.svcServer.aidlImmediateTaskExecution(task_buf,time_buf,action_buf,tep_buf);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	};

	final static public void loadEnvparmsFromService(GlobalParameters mGlblParms) {
		try {
			byte[] buf=mGlblParms.svcServer.aidlCopyEnvParmsFromService();
			mGlblParms.immTaskTestEnvParms=EnvironmentParms.deSerialize(buf); 
			Log.v("","size="+mGlblParms.immTaskTestEnvParms.getBluetoothConnectedDeviceList().size());
		} catch (RemoteException e) {
			Log.v(APPLICATION_TAG, "loadEnvparmsFromService error", e);
		}
	};
	
	final static public void editEnvParmsDlg(final GlobalParameters mGlblParms) {
		
		// common カスタムダイアログの生成
		final Dialog dialog = new Dialog(mGlblParms.context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCanceledOnTouchOutside(false);
		dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		dialog.setContentView(R.layout.edit_environment_parms_dlg);

//		TextView dlg_title = (TextView) dialog.findViewById(R.id.edit_environment_parms_dlg_title);
//		TextView dlg_msg = (TextView) dialog.findViewById(R.id.edit_environment_parms_dlg_msg);
		Button  btn_ok = (Button) dialog.findViewById(R.id.edit_environment_parms_dlg_ok);
		final Button  btn_cancel = (Button) dialog.findViewById(R.id.edit_environment_parms_dlg_cancel);
		Button  btn_reload = (Button) dialog.findViewById(R.id.edit_environment_parms_dlg_reload_value);

		CommonDialog.setDlgBoxSizeLimit(dialog,true);
//		dialog.setOnKeyListener(new DialogOnKeyListener(mGlblParms.context));		
		initEnvParmsView(mGlblParms,dialog);
		// Reloadボタンの指定
		btn_reload.setOnClickListener(new OnClickListener() {
			final public void onClick(View v) {
				loadEnvparmsFromService(mGlblParms);
				initEnvParmsView(mGlblParms,dialog);
			}
		});
		// Cancelボタンの指定
		btn_cancel.setOnClickListener(new OnClickListener() {
			final public void onClick(View v) {
				dialog.dismiss();
			}
		});
		// OKボタンの指定
		btn_ok.setOnClickListener(new OnClickListener() {
			final public void onClick(View v) {
				updateEnvParmsView(mGlblParms,dialog);
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
//		mGlblParms.commonDlg.setFixedOrientation(true);
//		dialog.setCancelable(false);
		dialog.show();

	};

	final static private void initEnvParmsView(GlobalParameters mGlblParms,Dialog dialog) {
		EditText et_light=(EditText)dialog.findViewById(R.id.edit_environment_parms_dlg_light_value);
		RadioButton rb_proximity_detected=(RadioButton)dialog.findViewById(R.id.edit_environment_parms_dlg_proximity_detected);
		RadioButton rb_proximity_undetected=(RadioButton)dialog.findViewById(R.id.edit_environment_parms_dlg_proximity_undetected);
		EditText et_battery_level=(EditText)dialog.findViewById(R.id.edit_environment_parms_dlg_battery_level);
		RadioButton rb_battery_power_ac=(RadioButton)dialog.findViewById(R.id.edit_environment_parms_dlg_battery_power_ac);
		RadioButton rb_battery_power_battery=(RadioButton)dialog.findViewById(R.id.edit_environment_parms_dlg_battery_power_battery);
		RadioButton rb_wifi_on=(RadioButton)dialog.findViewById(R.id.edit_environment_parms_dlg_wifi_on);
		RadioButton rb_wifi_off=(RadioButton)dialog.findViewById(R.id.edit_environment_parms_dlg_wifi_off);
		RadioButton rb_wifi_connected=(RadioButton)dialog.findViewById(R.id.edit_environment_parms_dlg_wifi_connected);
		final EditText et_wifi_ssid=(EditText)dialog.findViewById(R.id.edit_environment_parms_dlg_wifi_ssid);
		RadioButton rb_bt_on=(RadioButton)dialog.findViewById(R.id.edit_environment_parms_dlg_bluetooth_on);
		RadioButton rb_bt_off=(RadioButton)dialog.findViewById(R.id.edit_environment_parms_dlg_bluetooth_off);
		RadioButton rb_bt_connected=(RadioButton)dialog.findViewById(R.id.edit_environment_parms_dlg_bluetooth_connected);
		final EditText et_bt_dev_name=(EditText)dialog.findViewById(R.id.edit_environment_parms_dlg_bluetooth_device_name);
		
		CheckBox cb_airplane_on=(CheckBox)dialog.findViewById(R.id.edit_environment_parms_dlg_airplane_mode);
		RadioButton rb_ringer_normal=(RadioButton)dialog.findViewById(R.id.edit_environment_parms_dlg_ringer_mode_normal);
		RadioButton rb_ringer_vibrate=(RadioButton)dialog.findViewById(R.id.edit_environment_parms_dlg_ringer_mode_vibrate);
		RadioButton rb_ringer_silent=(RadioButton)dialog.findViewById(R.id.edit_environment_parms_dlg_ringer_mode_silent);
		
		RadioButton rb_telephony_idle=(RadioButton)dialog.findViewById(R.id.edit_environment_parms_dlg_telephony_call_idle);
		RadioButton rb_telephony_off_hook=(RadioButton)dialog.findViewById(R.id.edit_environment_parms_dlg_telephony_call_off_hook);
		RadioButton rb_telephony_ringing=(RadioButton)dialog.findViewById(R.id.edit_environment_parms_dlg_telephony_call_ringing);

		CheckBox cb_screen_locked=(CheckBox)dialog.findViewById(R.id.edit_environment_parms_dlg_screen_locked);
		
		et_light.setText(String.valueOf(mGlblParms.immTaskTestEnvParms.lightSensorValue));
		
		et_battery_level.setText(String.valueOf(mGlblParms.immTaskTestEnvParms.batteryLevel));
		et_wifi_ssid.setText(mGlblParms.immTaskTestEnvParms.wifiConnectedSsidName);
		et_bt_dev_name.setText(mGlblParms.immTaskTestEnvParms.blutoothConnectedDeviceName);
		
		if (mGlblParms.immTaskTestEnvParms.proximitySensorValue==0) rb_proximity_detected.setChecked(true);
		else rb_proximity_undetected.setChecked(true);
		if (mGlblParms.immTaskTestEnvParms.batteryPowerSource.equals(CURRENT_POWER_SOURCE_AC)) rb_battery_power_ac.setChecked(true);
		else rb_battery_power_battery.setChecked(true);

		et_wifi_ssid.setVisibility(EditText.GONE);
		if (mGlblParms.immTaskTestEnvParms.wifiIsActive) {
			if (mGlblParms.immTaskTestEnvParms.isWifiConnected()) {
				rb_wifi_connected.setChecked(true);
				et_wifi_ssid.setVisibility(EditText.VISIBLE);
			} else rb_wifi_on.setChecked(true);
		} else rb_wifi_off.setChecked(true);
		
		et_bt_dev_name.setVisibility(EditText.GONE);
		if (mGlblParms.immTaskTestEnvParms.bluetoothIsActive) {
			if (mGlblParms.immTaskTestEnvParms.isBluetoothConnected()) {
				rb_bt_connected.setChecked(true);
				et_bt_dev_name.setVisibility(EditText.VISIBLE);
			} else rb_bt_on.setChecked(true);
		} else rb_bt_off.setChecked(true);
		
		if (mGlblParms.immTaskTestEnvParms.airplane_mode_on==1) cb_airplane_on.setChecked(true);
		else cb_airplane_on.setChecked(false);
		
		if (mGlblParms.immTaskTestEnvParms.currentRingerMode==AudioManager.RINGER_MODE_NORMAL) rb_ringer_normal.setChecked(true);
		else if (mGlblParms.immTaskTestEnvParms.currentRingerMode==AudioManager.RINGER_MODE_VIBRATE) rb_ringer_vibrate.setChecked(true);
		else if (mGlblParms.immTaskTestEnvParms.currentRingerMode==AudioManager.RINGER_MODE_SILENT) rb_ringer_silent.setChecked(true);

		if (mGlblParms.immTaskTestEnvParms.telephonyStatus==TelephonyManager.CALL_STATE_IDLE) rb_telephony_idle.setChecked(true);
		else if (mGlblParms.immTaskTestEnvParms.telephonyStatus==TelephonyManager.CALL_STATE_OFFHOOK) rb_telephony_off_hook.setChecked(true);
		else if (mGlblParms.immTaskTestEnvParms.telephonyStatus==TelephonyManager.CALL_STATE_RINGING) rb_telephony_ringing.setChecked(true);

		if (mGlblParms.immTaskTestEnvParms.screenIsLocked) cb_screen_locked.setChecked(true);
		else cb_screen_locked.setChecked(false);
		
		RadioGroup rg_wifi=(RadioGroup)dialog.findViewById(R.id.edit_environment_parms_dlg_wifi);
		RadioGroup rg_bt=(RadioGroup)dialog.findViewById(R.id.edit_environment_parms_dlg_bluetooth);
		
		rg_wifi.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId==R.id.edit_environment_parms_dlg_wifi_on || 
						checkedId==R.id.edit_environment_parms_dlg_wifi_off) {
					et_wifi_ssid.setVisibility(EditText.GONE);
					et_wifi_ssid.setText("");
				} else {
					et_wifi_ssid.setVisibility(EditText.VISIBLE);	
				}
			}
		});
		rg_bt.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId==R.id.edit_environment_parms_dlg_bluetooth_on || 
						checkedId==R.id.edit_environment_parms_dlg_bluetooth_off) {
					et_bt_dev_name.setVisibility(EditText.GONE);
					et_bt_dev_name.setText("");
				} else {
					et_bt_dev_name.setVisibility(EditText.VISIBLE);	
				}
			}
		});

	};

	final static private void updateEnvParmsView(GlobalParameters mGlblParms,Dialog dialog) {
		EditText et_light=(EditText)dialog.findViewById(R.id.edit_environment_parms_dlg_light_value);
		RadioButton rb_proximity_detected=(RadioButton)dialog.findViewById(R.id.edit_environment_parms_dlg_proximity_detected);
//		RadioButton rb_proximity_undetected=(RadioButton)dialog.findViewById(R.id.edit_environment_parms_dlg_proximity_undetected);
		EditText et_battery_level=(EditText)dialog.findViewById(R.id.edit_environment_parms_dlg_battery_level);
		RadioButton rb_battery_power_ac=(RadioButton)dialog.findViewById(R.id.edit_environment_parms_dlg_battery_power_ac);
//		RadioButton rb_battery_power_battery=(RadioButton)dialog.findViewById(R.id.edit_environment_parms_dlg_battery_power_battery);
		RadioButton rb_wifi_on=(RadioButton)dialog.findViewById(R.id.edit_environment_parms_dlg_wifi_on);
//		RadioButton rb_wifi_off=(RadioButton)dialog.findViewById(R.id.edit_environment_parms_dlg_wifi_off);
		RadioButton rb_wifi_connected=(RadioButton)dialog.findViewById(R.id.edit_environment_parms_dlg_wifi_connected);
		EditText et_wifi_ssid=(EditText)dialog.findViewById(R.id.edit_environment_parms_dlg_wifi_ssid);
		RadioButton rb_bt_on=(RadioButton)dialog.findViewById(R.id.edit_environment_parms_dlg_bluetooth_on);
//		RadioButton rb_bt_off=(RadioButton)dialog.findViewById(R.id.edit_environment_parms_dlg_bluetooth_off);
		RadioButton rb_bt_connected=(RadioButton)dialog.findViewById(R.id.edit_environment_parms_dlg_bluetooth_connected);
		EditText et_bt_dev_name=(EditText)dialog.findViewById(R.id.edit_environment_parms_dlg_bluetooth_device_name);
		
		CheckBox cb_airplane_on=(CheckBox)dialog.findViewById(R.id.edit_environment_parms_dlg_airplane_mode);
		RadioButton rb_ringer_normal=(RadioButton)dialog.findViewById(R.id.edit_environment_parms_dlg_ringer_mode_normal);
		RadioButton rb_ringer_vibrate=(RadioButton)dialog.findViewById(R.id.edit_environment_parms_dlg_ringer_mode_vibrate);
		RadioButton rb_ringer_silent=(RadioButton)dialog.findViewById(R.id.edit_environment_parms_dlg_ringer_mode_silent);
		RadioButton rb_telephony_idle=(RadioButton)dialog.findViewById(R.id.edit_environment_parms_dlg_telephony_call_idle);
		RadioButton rb_telephony_off_hook=(RadioButton)dialog.findViewById(R.id.edit_environment_parms_dlg_telephony_call_off_hook);
		RadioButton rb_telephony_ringing=(RadioButton)dialog.findViewById(R.id.edit_environment_parms_dlg_telephony_call_ringing);
		CheckBox cb_screen_locked=(CheckBox)dialog.findViewById(R.id.edit_environment_parms_dlg_screen_locked);
		
		mGlblParms.immTaskTestEnvParms.lightSensorValue=Integer.valueOf(et_light.getText().toString());

		mGlblParms.immTaskTestEnvParms.batteryLevel=Integer.valueOf(et_battery_level.getText().toString());
		
		if (rb_proximity_detected.isChecked()) mGlblParms.immTaskTestEnvParms.proximitySensorValue=0;
		else mGlblParms.immTaskTestEnvParms.proximitySensorValue=1;
		
		if (rb_battery_power_ac.isChecked()) mGlblParms.immTaskTestEnvParms.batteryPowerSource=CURRENT_POWER_SOURCE_AC;
		else mGlblParms.immTaskTestEnvParms.batteryPowerSource=CURRENT_POWER_SOURCE_BATTERY;
		
		if (rb_wifi_on.isChecked()) {
			mGlblParms.immTaskTestEnvParms.wifiIsActive=true;
			mGlblParms.immTaskTestEnvParms.wifiConnectedSsidName="";
		} else if (rb_wifi_connected.isChecked()) {
			mGlblParms.immTaskTestEnvParms.wifiConnectedSsidName=et_wifi_ssid.getText().toString();
			mGlblParms.immTaskTestEnvParms.wifiIsActive=true;
		} else {
			mGlblParms.immTaskTestEnvParms.wifiIsActive=false;
			mGlblParms.immTaskTestEnvParms.wifiConnectedSsidName="";
		} 
		
		if (rb_bt_on.isChecked()) {
			mGlblParms.immTaskTestEnvParms.bluetoothIsActive=true;
			mGlblParms.immTaskTestEnvParms.blutoothConnectedDeviceName="";
		} else if (rb_bt_connected.isChecked()) {
			mGlblParms.immTaskTestEnvParms.bluetoothIsActive=true;
			mGlblParms.immTaskTestEnvParms.blutoothConnectedDeviceName=et_bt_dev_name.getText().toString();
		} else {
			mGlblParms.immTaskTestEnvParms.bluetoothIsActive=false;
			mGlblParms.immTaskTestEnvParms.blutoothConnectedDeviceName="";
		}

		if (cb_airplane_on.isChecked()) mGlblParms.immTaskTestEnvParms.airplane_mode_on=1;
		else mGlblParms.immTaskTestEnvParms.airplane_mode_on=0;
		
		if (rb_ringer_normal.isChecked()) mGlblParms.immTaskTestEnvParms.currentRingerMode=AudioManager.RINGER_MODE_NORMAL;
		else if (rb_ringer_vibrate.isChecked()) mGlblParms.immTaskTestEnvParms.currentRingerMode=AudioManager.RINGER_MODE_VIBRATE;
		else if (rb_ringer_silent.isChecked()) mGlblParms.immTaskTestEnvParms.currentRingerMode=AudioManager.RINGER_MODE_SILENT;
	
		if (rb_telephony_idle.isChecked()) mGlblParms.immTaskTestEnvParms.telephonyStatus=TelephonyManager.CALL_STATE_IDLE;
		else if (rb_telephony_off_hook.isChecked()) mGlblParms.immTaskTestEnvParms.telephonyStatus=TelephonyManager.CALL_STATE_OFFHOOK;
		else if (rb_telephony_ringing.isChecked()) mGlblParms.immTaskTestEnvParms.telephonyStatus=TelephonyManager.CALL_STATE_RINGING;
		
		if (cb_screen_locked.isChecked()) mGlblParms.immTaskTestEnvParms.screenIsLocked=true;
		else mGlblParms.immTaskTestEnvParms.screenIsLocked=false;
	};

	
	final static public void setProfileActionTestExecListener(
			final GlobalParameters mGlblParms,
			final Dialog dialog, 
			final AdapterProfileList pfla,
			final String curr_grp,
			final ArrayList<ActivityExtraDataItem> aed_edit_list) {
        final Spinner spinnerActionType = (Spinner) dialog.findViewById(R.id.edit_profile_action_action_type);
        final Spinner spinnerActivityName = (Spinner) dialog.findViewById(R.id.edit_profile_action_exec_activity_name);
        final Spinner spinnerActivityDataType = (Spinner) dialog.findViewById(R.id.edit_profile_action_exec_activity_data_type);
        final Spinner spinnerCompareType = (Spinner) dialog.findViewById(R.id.edit_profile_action_compare_type);
        final Spinner spinnerCompareTarget = (Spinner) dialog.findViewById(R.id.edit_profile_action_compare_target);
        final Spinner spinnerMessageType = (Spinner) dialog.findViewById(R.id.edit_profile_action_message_type);
        final Spinner spinnerTimeType = (Spinner) dialog.findViewById(R.id.edit_profile_action_time_type);
        final Spinner spinnerTimeTarget = (Spinner) dialog.findViewById(R.id.edit_profile_action_time_target);
        final Spinner spinnerTaskType = (Spinner) dialog.findViewById(R.id.edit_profile_action_task_type);
        final Spinner spinnerTaskTarget = (Spinner) dialog.findViewById(R.id.edit_profile_action_task_target);
        final EditText uri_data=(EditText)dialog.findViewById(R.id.edit_profile_action_exec_activity_uri_data);
        Button test_exec_btn=(Button)dialog.findViewById(R.id.edit_profile_action_test_exec);
        final EditText et_bsh_script=(EditText)dialog.findViewById(R.id.edit_profile_action_dlg_bsh_script_text);
		final Button btnEnvEdit = (Button) dialog.findViewById(R.id.edit_profile_action_test_edit_parms);
        final CheckBox cb_enable_env_parms=(CheckBox)dialog.findViewById(R.id.edit_profile_action_enable_env_parms);

        final EditText et_shell_cmd=(EditText) dialog.findViewById(R.id.edit_profile_action_dlg_shell_cmd_text);
        final CheckBox cb_shell_cmd_with_su=(CheckBox) dialog.findViewById(R.id.edit_profile_action_dlg_shell_cmd_with_su);

        btnEnvEdit.setVisibility(Button.GONE);
        cb_enable_env_parms.setOnCheckedChangeListener(new OnCheckedChangeListener(){
		@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if (isChecked) btnEnvEdit.setVisibility(Button.VISIBLE);
				else btnEnvEdit.setVisibility(Button.GONE);
			}
        });
		// EnvEditボタンの指定
		btnEnvEdit.setOnClickListener(new View.OnClickListener() {
			final public void onClick(View v) {
				editEnvParmsDlg(mGlblParms);
			}
		});
        test_exec_btn.setOnClickListener(new View.OnClickListener() {
        	final public void onClick(View v) {
				String audit_msg=auditActionProfile(mGlblParms,dialog, curr_grp,pfla, 
						spinnerActionType, spinnerActivityName,spinnerActivityDataType,
						spinnerCompareTarget, spinnerCompareType,spinnerMessageType,
						spinnerTimeType, spinnerTimeTarget,
						spinnerTaskType, spinnerTaskTarget,
						aed_edit_list
						);
				if (!audit_msg.equals("")) {
					mGlblParms.commonDlg.showCommonDialog(false, "E", 
							mGlblParms.context.getString(R.string.msgs_edit_profile_action_profle_error), 
							audit_msg, null);
					return;
				}
				if (spinnerActionType.getSelectedItem().toString().equals(PROFILE_ACTION_TYPE_ACTIVITY)) {
					String t_tn=spinnerActivityName.getSelectedItem().toString();
					String prof_act_name=t_tn.substring(0,t_tn.indexOf("("));
					String prof_act_pkgname=t_tn.replace(prof_act_name+"(", "").replace(")","");
					testActivityExecution(mGlblParms,prof_act_pkgname,spinnerActivityDataType,
							uri_data.getText().toString(),aed_edit_list);
				} else if (spinnerActionType.getSelectedItem().toString().equals(PROFILE_ACTION_TYPE_BSH_SCRIPT)){
					String p_grp="*ImmTask";
					String p_name="ImmTask";
					ProfileListItem pli=new ProfileListItem();
					pli.setActionBeanShellScriptEntry(PROFILE_VERSION_CURRENT,p_grp,false,System.currentTimeMillis(),
							PROFILE_TYPE_ACTION,p_name, 
							PROFILE_ENABLED,et_bsh_script.getText().toString());
					pli.setProfileGroupActivated(true);
					pli.setProfileGroupShowed(true);
					
					ArrayList<ProfileListItem>p_list=new ArrayList<ProfileListItem>();
					p_list.add(pli);
					AdapterProfileList a_prof=new AdapterProfileList(mGlblParms.context, 0, p_list);
					
					TaskActionEditListItem taeli=new TaskActionEditListItem();
					taeli.action=p_name;
					
					ArrayList<TaskActionEditListItem>a_list=new ArrayList<TaskActionEditListItem>();
					a_list.add(taeli);
					
					invokeTaskExecution(mGlblParms,p_grp,a_prof,p_name, TRIGGER_EVENT_TASK,
							 true, cb_enable_env_parms.isChecked(),a_list);
				} else if (spinnerActionType.getSelectedItem().toString().equals(PROFILE_ACTION_TYPE_SHELL_COMMAND)){
					String p_grp="*ImmTask";
					String p_name="ImmTask";
					ProfileListItem pli=new ProfileListItem();
					pli.setActionShellCmdEntry(PROFILE_VERSION_CURRENT,p_grp,false,System.currentTimeMillis(),
							PROFILE_TYPE_ACTION,p_name, 
							PROFILE_ENABLED,et_shell_cmd.getText().toString(), cb_shell_cmd_with_su.isChecked());
					pli.setProfileGroupActivated(true);
					pli.setProfileGroupShowed(true);
					
					ArrayList<ProfileListItem>p_list=new ArrayList<ProfileListItem>();
					p_list.add(pli);
					AdapterProfileList a_prof=new AdapterProfileList(mGlblParms.context, 0, p_list);
					
					TaskActionEditListItem taeli=new TaskActionEditListItem();
					taeli.action=p_name;
					
					ArrayList<TaskActionEditListItem>a_list=new ArrayList<TaskActionEditListItem>();
					a_list.add(taeli);
					
					invokeTaskExecution(mGlblParms,p_grp,a_prof,p_name, TRIGGER_EVENT_TASK,
							 true, cb_enable_env_parms.isChecked(),a_list);
				}
			}
		});
	};

	
	final static private  void testActivityExecution(
			GlobalParameters mGlblParms,String pkg, Spinner spinnerDataType, 
			String uri_data, ArrayList<ActivityExtraDataItem> aed_edit_list) {
		final PackageManager pm = mGlblParms.context.getPackageManager();
		Intent in=pm.getLaunchIntentForPackage(pkg);
		if (in!=null) {
			mGlblParms.util.addDebugMsg(1, "I", "Activity has been started, Activity=",pkg);
			in.setAction(Intent.ACTION_MAIN);
			in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			if (spinnerDataType.getSelectedItem().toString().equals(PROFILE_ACTION_TYPE_ACTIVITY_DATA_TYPE_URI)){
				in.setData(Uri.parse(uri_data));
				mGlblParms.util.addDebugMsg(1, "I", "   Uri data added : Uri=",uri_data);
			} else if (spinnerDataType.getSelectedItem().toString().equals(PROFILE_ACTION_TYPE_ACTIVITY_DATA_TYPE_EXTRA)){
				for (int i=0;i<aed_edit_list.size();i++) {
					ActivityExtraDataItem aedi=aed_edit_list.get(i);
					if (aedi.data_value_array.equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_ARRAY_NO)) {
						String d_val_string="";
						boolean d_val_boolean=false;
						int d_val_int=0;
						if (aedi.data_type.equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_STRING)) {
							d_val_string=aedi.data_value;
							in.putExtra(aedi.key_value, d_val_string);
							mGlblParms.util.addDebugMsg(1, "I", "   Extra String data added : key=",aedi.key_value,", value=",d_val_string);
						}else if (aedi.data_type.equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_INT)) {
							d_val_int=Integer.valueOf(aedi.data_value);
							in.putExtra(aedi.key_value, d_val_int);						
							mGlblParms.util.addDebugMsg(1, "I", "   Extra Int data added : key="+aedi.key_value+", value="+d_val_int);
						}else if (aedi.data_type.equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_BOOLEAN)) {
							if (aedi.data_value.equals("true")) d_val_boolean=true;
							in.putExtra(aedi.key_value, d_val_boolean);						
							mGlblParms.util.addDebugMsg(1, "I", "   Extra Boolean data added : key="+aedi.key_value+", value="+d_val_boolean);
						}
					} else if (aedi.data_value_array.equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_ARRAY_YES)) {
						if (aedi.data_type.equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_STRING)) {
							String[] d_val_array=aedi.data_value.split("\u0003");
							String[] d_val_extra=new String[d_val_array.length];
							for (int ai=0;ai<d_val_array.length;ai++) {
								d_val_extra[ai]=d_val_array[ai];
								mGlblParms.util.addDebugMsg(1, "I", "   Extra array String data added : key="+aedi.key_value+", value="+d_val_extra[ai]);
							}
							in.putExtra(aedi.key_value, d_val_extra);
						}else if (aedi.data_type.equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_INT)) {
							String[] d_val_array=aedi.data_value.split("\u0003");
							int[] d_val_extra=new int[d_val_array.length];
							for (int ai=0;ai<d_val_array.length;ai++) {
								d_val_extra[ai]=Integer.valueOf(d_val_array[ai]);
								mGlblParms.util.addDebugMsg(1, "I", "   Extra array Int data added : key="+aedi.key_value+", value="+d_val_extra[ai]);
							}
							in.putExtra(aedi.key_value, d_val_extra);						
						}else if (aedi.data_type.equals(PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_BOOLEAN)) {
							String[] d_val_array=aedi.data_value.split("\u0003");
							boolean[] d_val_extra=new boolean[d_val_array.length];
							for (int ai=0;ai<d_val_array.length;ai++) {
								if (d_val_array[ai].equals("true")) d_val_extra[ai]=true;
								else d_val_extra[ai]=false;
								mGlblParms.util.addDebugMsg(1, "I", "   Extra array Boolean data added : key="+aedi.key_value+", value="+d_val_extra[ai]);
							}
							in.putExtra(aedi.key_value, d_val_extra);						
						}
					}
				}
			} else {
				mGlblParms.util.addDebugMsg(1, "I", "   No data was supplied");
			} 
			mGlblParms.context.startActivity(in);
		}
	};

//	final static private  boolean getProfileGroupActiveStatus(
//			final AdapterProfileList pfla, String grp) {
//		boolean result=false;
//		for (int i=0;i<pfla.getDataListCount();i++) {
//			if (pfla.getDataListItem(i).getProfileGroup().equals(grp)) {
//				result=pfla.getDataListItem(i).isProfileGroupActivated();
//				break;
//			}
//		}
//		return result;
//	};
	
	final static public String auditProfileName( 
			GlobalParameters mGlblParms,final AdapterProfileList pfla, String curr_grp, 
			String prof_type, String prof_name ) {
		String result="";
		if (prof_name.equals("")) {
			result=mGlblParms.context.getString(R.string.msgs_edit_profile_profile_name_missing);
		} else if (!ProfileUtilities.isValidProfileName(mGlblParms.util,pfla, curr_grp, prof_type, prof_name)) {
			result=mGlblParms.context.getString(R.string.msgs_edit_profile_profile_name_invalid);
		}
		return result;
	};
	
	final static public String auditActionProfile(
			GlobalParameters mGlblParms,Dialog dialog, String curr_grp,
			AdapterProfileList pfla, Spinner spinnerActionType, 
			Spinner spinnerActivityName, Spinner spinnerActivityDataType,
			Spinner spinnerCompareTarget, Spinner spinnerCompareType, 
			Spinner spinnerMessageType,
			Spinner spinnerTimeType, Spinner spinnerTimeTarget,
			Spinner spinnerTaskType, Spinner spinnerTaskTarget,
			ArrayList<ActivityExtraDataItem> aed_edit_list) {
		String act_type=spinnerActionType.getSelectedItem().toString();
		if (act_type.equals(PROFILE_ACTION_TYPE_ACTIVITY)) {
			String t_tn=spinnerActivityName.getSelectedItem().toString();
			if (t_tn.equals("** Not Selected **")) {
				return mGlblParms.context.getString(R.string.msgs_edit_profile_action_activity_name_not_selected);
			}
			if (spinnerActivityDataType.getSelectedItem().toString().equals(PROFILE_ACTION_TYPE_ACTIVITY_DATA_TYPE_NONE)) 
				return "";
			
			if (spinnerActivityDataType.getSelectedItem().toString().equals(PROFILE_ACTION_TYPE_ACTIVITY_DATA_TYPE_URI)) {
				EditText et_uri=(EditText)dialog.findViewById(R.id.edit_profile_action_exec_activity_uri_data);
				if (et_uri.getText().toString().equals(""))
					return mGlblParms.context.getString(R.string.msgs_edit_profile_action_activity_data_uri_not_specified);
			}
			if (spinnerActivityDataType.getSelectedItem().toString().equals(PROFILE_ACTION_TYPE_ACTIVITY_DATA_TYPE_EXTRA)) {
				if (aed_edit_list.size()==1) {
					if (aed_edit_list.get(0).key_value.equals(""))
						return "Extra data was not specified";
				}
			}
		} else if (act_type.equals(PROFILE_ACTION_TYPE_MUSIC)) {
			TextView tv_sound_filename=(TextView)dialog.findViewById(R.id.edit_profile_action_exec_sound_file_name);
			if (tv_sound_filename.getText().toString().equals("")) {
				return mGlblParms.context.getString(R.string.msgs_edit_profile_action_music_file_name_not_specified);				
			}
		} else if (act_type.equals(PROFILE_ACTION_TYPE_COMPARE)) {
	        final EditText et_comp_value1=(EditText)dialog.findViewById(R.id.edit_profile_action_compare_value1);
	        final EditText et_comp_value2=(EditText)dialog.findViewById(R.id.edit_profile_action_compare_value2);
	        String c_tgt=spinnerCompareTarget.getSelectedItem().toString();
			String c_typ=spinnerCompareType.getSelectedItem().toString();
			String c_val1=et_comp_value1.getText().toString();
			String c_val2=et_comp_value2.getText().toString();
			if (c_tgt.equals(PROFILE_ACTION_TYPE_COMPARE_TARGET_BLUETOOTH)) {
				if (mGlblParms.actionCompareDataAdapter.getCount()==0 || 
					mGlblParms.actionCompareDataAdapter.getItem(0).data_value.equals("")) 
					return mGlblParms.context.getString(R.string.msgs_edit_profile_action_compare_value_not_specified);
			} else if (c_tgt.equals(PROFILE_ACTION_TYPE_COMPARE_TARGET_BATTERY)) {
				if (c_val1.equals("")) 
					return mGlblParms.context.getString(R.string.msgs_edit_profile_action_compare_val1_not_specified);
				if (c_typ.equals(PROFILE_ACTION_TYPE_COMPARE_COMPARE_BETWEEN) && c_val2.equals("")) 
					return mGlblParms.context.getString(R.string.msgs_edit_profile_action_compare_val2_not_specified);
				int b_val1=0, b_val2=0;
				try {
					b_val1=Integer.parseInt(c_val1);
				} catch (NumberFormatException e) {
					return "value1 is not numeric";
				}
				if (b_val1>=0 && b_val1<=100) {
					if (c_typ.equals(PROFILE_ACTION_TYPE_COMPARE_COMPARE_BETWEEN)) {
						try {
							b_val2=Integer.parseInt(c_val2);
						} catch (NumberFormatException e) {
							return "value2 is not numeric";
						}
						if (b_val1>=b_val2) return "value2 is must be grater than value1";
						if (b_val2<0 || b_val2>100) return "value2 is must be 0 to 100"; 
					}
				} else return "value1 is must be 0 to 100";
			} else if (c_tgt.equals(PROFILE_ACTION_TYPE_COMPARE_TARGET_LIGHT)) {
				if (c_val1.equals("")) 
					return mGlblParms.context.getString(R.string.msgs_edit_profile_action_compare_val1_not_specified);
				if (c_typ.equals(PROFILE_ACTION_TYPE_COMPARE_COMPARE_BETWEEN) && c_val2.equals("")) 
					return mGlblParms.context.getString(R.string.msgs_edit_profile_action_compare_val2_not_specified);
			} else if (c_tgt.equals(PROFILE_ACTION_TYPE_COMPARE_TARGET_WIFI)) {
				if (mGlblParms.actionCompareDataAdapter.getCount()==0 || 
					mGlblParms.actionCompareDataAdapter.getItem(0).data_value.equals("")) 
					return mGlblParms.context.getString(R.string.msgs_edit_profile_action_compare_value_not_specified);
			} else if (c_tgt.equals(PROFILE_ACTION_TYPE_COMPARE_TARGET_TIME)) {
				if (c_val1.equals("")) 
					return mGlblParms.context.getString(R.string.msgs_edit_profile_action_compare_val1_not_specified);
				if (c_typ.equals(PROFILE_ACTION_TYPE_COMPARE_COMPARE_BETWEEN) && c_val2.equals("")) 
					return mGlblParms.context.getString(R.string.msgs_edit_profile_action_compare_val2_not_specified);
				int c_time1=0, c_time2=0;
				try {
					c_time1=Integer.parseInt(c_val1);
				} catch (NumberFormatException e) {
					return "value1 is not numeric";
				}
				if (c_time1>=0 && c_time1<=23) {
					if (c_typ.equals(PROFILE_ACTION_TYPE_COMPARE_COMPARE_BETWEEN)) {
						try {
							c_time2=Integer.parseInt(c_val2);
						} catch (NumberFormatException e) {
							return "value2 is not numeric";
						}
						if (c_time1>=c_time2) return "value2 is must be grater than value1";
						if (c_time2<0 || c_time2>47) return "value2 is must be 0 to 47"; 
					}
				} else return "value1 must be 0 to 23";
			}
		} else if (act_type.equals(PROFILE_ACTION_TYPE_MESSAGE)) {
	        final EditText et_msg_text=(EditText)dialog.findViewById(R.id.edit_profile_action_message_message);
			if (et_msg_text.getText().toString().equals("")) {
					return mGlblParms.context.getString(R.string.msgs_edit_profile_action_message_text_not_specified);
			}
		} else if (act_type.equals(PROFILE_ACTION_TYPE_TIME)) {
			if (spinnerTimeTarget.getSelectedItem().toString().startsWith("** ")) {
				return mGlblParms.context.getString(R.string.msgs_edit_profile_action_time_target_not_specified);
			}
		} else if (act_type.equals(PROFILE_ACTION_TYPE_TASK)) {
			if (spinnerTaskTarget.getSelectedItem().toString().startsWith("** ")) {
				return mGlblParms.context.getString(R.string.msgs_edit_profile_action_task_target_not_specified);
			}
		} else if (act_type.equals(PROFILE_ACTION_TYPE_BSH_SCRIPT)) {
			final EditText et_bsh_script=(EditText) dialog.findViewById(R.id.edit_profile_action_dlg_bsh_script_text);
			if (et_bsh_script.getText().toString().equals("")) {
				return mGlblParms.context.getString(R.string.msgs_edit_profile_action_bsh_text_not_specified);
			}
		} else if (act_type.equals(PROFILE_ACTION_TYPE_SHELL_COMMAND)) {
			final EditText et_shell_cmd=(EditText) dialog.findViewById(R.id.edit_profile_action_dlg_shell_cmd_text);
			if (et_shell_cmd.getText().toString().equals("")) {
				return mGlblParms.context.getString(R.string.msgs_edit_profile_action_shell_cmd_not_specified);
			}
		};
		return "";
	};
	
	final public String getRingtonePath(String rt, String rn) {
		return getRingtonePath(mGlblParms,rt, rn);
	};
	final static public String getRingtonePath(GlobalParameters mGlblParms,String rt, String rn) {
		String rt_path="";
		for (int i=0;i<mGlblParms.ringtoneList.size();i++) {
			if (mGlblParms.ringtoneList.get(i).ringtone_type==getRingtoneTypeInt(rt)){
				if (mGlblParms.ringtoneList.get(i).ringtone_name.equals(rn)) {
					rt_path=mGlblParms.ringtoneList.get(i).ringtone_uri.getPath();
					break;
				}
			}
		}
		return rt_path;
	};

	final static public void copyProfileDlg(final GlobalParameters mGlblParms,final String curr_grp, 
			final AdapterProfileList pfla, final ListView pflv, 
			final ProfileListItem tpli,final int pos, final NotifyEvent ntfy_unselect) {
		
		// カスタムダイアログの生成
		final Dialog dialog = new Dialog(mGlblParms.context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCanceledOnTouchOutside(false);
		dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		dialog.setContentView(R.layout.copy_profile_dlg);
		final TextView dlg_msg = (TextView) dialog.findViewById(R.id.copy_profile_dlg_msg);
		final EditText dlg_prof_name_et=(EditText)dialog.findViewById(R.id.copy_profile_dlg_new_name);
		final Button okBtn = (Button)dialog.findViewById(R.id.copy_profile_dlg_ok_btn);
		final Button cancelBtn = (Button)dialog.findViewById(R.id.copy_profile_dlg_cancel_btn);
		
        final Spinner spinnerGroup = (Spinner) dialog.findViewById(R.id.copy_profile_group);
        final CustomSpinnerAdapter adapterGroup = new CustomSpinnerAdapter(mGlblParms.context, R.layout.custom_simple_spinner_item);
        setSpinnerGroup(mGlblParms,dialog,pfla,spinnerGroup,adapterGroup,tpli.getProfileGroup());
		
        if (spinnerGroup.getCount()==0) {
        	mGlblParms.commonDlg.showCommonDialog(false, "E", 
        			mGlblParms.context.getString(R.string.msgs_edit_profile_copy_error),
        			mGlblParms.context.getString(R.string.msgs_edit_profile_required_other_than_qt_grp)
        			, null);
        	return;
        }
        
		dlg_prof_name_et.setText(tpli.getProfileName());
		CommonDialog.setDlgBoxSizeCompact(dialog);
//		dialog.setOnKeyListener(new DialogOnKeyListener(mGlblParms.context));
		
		spinnerGroup.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			final public void onItemSelected(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				String s_grp=adapterGroup.getItem(pos);
				if (ProfileUtilities.isValidProfileName(mGlblParms.util,pfla,s_grp,
						tpli.getProfileType(),tpli.getProfileName())){
					okBtn.setEnabled(true);
					dlg_msg.setText("");
				} else {
					okBtn.setEnabled(false);
					dlg_msg.setText(mGlblParms.context.getString(R.string.msgs_edit_profile_profile_name_invalid));
				}
			}
			@Override
			final public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
		dlg_prof_name_et.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable arg0) {
				String s_grp=spinnerGroup.getSelectedItem().toString();
				if (arg0.length()!=0) {
					if (!arg0.toString().equals(tpli.getProfileName())) {
						if (ProfileUtilities.isValidProfileName(mGlblParms.util,pfla,s_grp,
								tpli.getProfileType(),arg0.toString())){
							okBtn.setEnabled(true);
							dlg_msg.setText("");
						} else {
							okBtn.setEnabled(false);
							dlg_msg.setText(mGlblParms.context.getString(R.string.msgs_edit_profile_profile_name_invalid));
						}
					} else {
						okBtn.setEnabled(false);
						dlg_msg.setText(mGlblParms.context.getString(R.string.msgs_edit_profile_profile_name_invalid));
					}
				} else {
					okBtn.setEnabled(false);
					dlg_msg.setText(mGlblParms.context.getString(R.string.msgs_edit_profile_profile_name_invalid));
				}
			}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,int arg2, int arg3) {}
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {}
		});
		okBtn.setEnabled(false);
		
		okBtn.setOnClickListener(new OnClickListener(){
			@Override
			final public void onClick(View arg0) {
				dialog.dismiss();
				dlg_prof_name_et.selectAll();
				final String new_name=dlg_prof_name_et.getText().toString();
				final String s_grp=spinnerGroup.getSelectedItem().toString();
				NotifyEvent ntfy=new NotifyEvent(mGlblParms.context);
				ntfy.setListener(new NotifyEventListener(){
					@Override
					final public void positiveResponse(Context c, Object[] o) {
						ProfileUtilities.copyProfile(mGlblParms.util, pfla, tpli, s_grp, new_name);
						ProfileUtilities.verifyProfileIntegrity(mGlblParms.util,true, pfla, s_grp);
						putProfileListToService(mGlblParms,pfla,ProfileUtilities.isProfileGroupActive(mGlblParms.util,pfla,s_grp));
//						saveProfileToFileProfileOnly(false,pfla,pflv,"","");
//						if (isProfileGroupActive(pfla,s_grp)) mGlblParms.util.reBuildTaskExecList();

						mGlblParms.commonDlg.showCommonDialog(false,
								"I",mGlblParms.context.getString(R.string.msgs_copy_profile_title), 
								String.format(mGlblParms.context.getString(R.string.msgs_copy_profile_result),
										new_name ,s_grp),null);
						ntfy_unselect.notifyToListener(true, null);
					}
					@Override
					final public void negativeResponse(Context c, Object[] o) {}
				});
				mGlblParms.commonDlg.showCommonDialog(true,
						"W",mGlblParms.context.getString(R.string.msgs_copy_profile_title), 
						String.format(mGlblParms.context.getString(R.string.msgs_copy_profile_msg),
								new_name,s_grp),ntfy);
			}
		});
		cancelBtn.setOnClickListener(new OnClickListener(){
			@Override
			final public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		// Cancelリスナーの指定
		dialog.setOnCancelListener(new Dialog.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				cancelBtn.performClick();
			}
		});
//		dialog.setCancelable(false);
		dialog.show();
		
	};

	final static public void renameProfileDlg(final GlobalParameters mGlblParms,final String curr_grp, 
			final AdapterProfileList pfla, final ListView pflv, 
			final ProfileListItem tpli,final int pos, final NotifyEvent ntfy_unselect) {
		// カスタムダイアログの生成
		final Dialog dialog = new Dialog(mGlblParms.context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCanceledOnTouchOutside(false);
		dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		dialog.setContentView(R.layout.rename_profile_dlg);
		final TextView dlg_msg = (TextView) dialog.findViewById(R.id.rename_profile_dlg_msg);
		final EditText dlg_prof_name_et=(EditText)dialog.findViewById(R.id.rename_profile_dlg_new_name);
		final Button okBtn = (Button)dialog.findViewById(R.id.rename_profile_dlg_ok_btn);
		final Button cancelBtn = (Button)dialog.findViewById(R.id.rename_profile_dlg_cancel_btn);
		dlg_prof_name_et.setText(tpli.getProfileName());
		CommonDialog.setDlgBoxSizeCompact(dialog);
//		dialog.setOnKeyListener(new DialogOnKeyListener(mGlblParms.context));
		
		dlg_prof_name_et.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable arg0) {
				if (arg0.length()!=0) {
					if (!arg0.toString().equals(tpli.getProfileName())) {
						if (ProfileUtilities.isValidProfileName(mGlblParms.util,pfla,tpli.getProfileGroup(),
								tpli.getProfileType(),arg0.toString())){
							okBtn.setEnabled(true);
							dlg_msg.setText("");
						} else {
							okBtn.setEnabled(false);
							dlg_msg.setText(mGlblParms.context.getString(R.string.msgs_edit_profile_profile_name_invalid));
						}
					} else {
						okBtn.setEnabled(false);
						dlg_msg.setText(mGlblParms.context.getString(R.string.msgs_edit_profile_profile_name_invalid));
					}
				} else {
					okBtn.setEnabled(false);
					dlg_msg.setText(mGlblParms.context.getString(R.string.msgs_edit_profile_profile_name_invalid));
				}
			}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,int arg2, int arg3) {}
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {}
		});
		okBtn.setEnabled(false);
		
		okBtn.setOnClickListener(new OnClickListener(){
			@Override
			final public void onClick(View arg0) {
				dialog.dismiss();
				dlg_prof_name_et.selectAll();
				final String new_name=dlg_prof_name_et.getText().toString();
				NotifyEvent ntfy=new NotifyEvent(mGlblParms.context);
				ntfy.setListener(new NotifyEventListener(){
					@Override
					final public void positiveResponse(Context c, Object[] o) {
						String old_name=tpli.getProfileName();
						ProfileUtilities.renameProfile(mGlblParms.util, pfla, tpli, new_name);
						putProfileListToService(mGlblParms,pfla,ProfileUtilities.isProfileGroupActive(mGlblParms.util,pfla,curr_grp));
//						saveProfileToFileProfileOnly(false,pfla,pflv,"","");
//						if (isProfileGroupActive(pfla,curr_grp)) mGlblParms.util.reBuildTaskExecList();

						mGlblParms.commonDlg.showCommonDialog(false,
								"I",mGlblParms.context.getString(R.string.msgs_rename_profile_title), 
								String.format(mGlblParms.context.getString(R.string.msgs_rename_profile_result),
										old_name,new_name),null);
						ntfy_unselect.notifyToListener(true, null);
					}

					@Override
					final public void negativeResponse(Context c, Object[] o) {}
					
				});
				mGlblParms.commonDlg.showCommonDialog(true,
						"W",mGlblParms.context.getString(R.string.msgs_rename_profile_title), 
						String.format(mGlblParms.context.getString(R.string.msgs_rename_profile_msg),
								tpli.getProfileName(),new_name),ntfy);
			}
		});
		cancelBtn.setOnClickListener(new OnClickListener(){
			@Override
			final public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		// Cancelリスナーの指定
		dialog.setOnCancelListener(new Dialog.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				cancelBtn.performClick();
			}
		});
//		dialog.setCancelable(false);
		dialog.show();
	};
	

    final static public int getRingtoneTypeInt(String type) {
		int r_type=0;
		if (type.equals(PROFILE_ACTION_RINGTONE_TYPE_ALERT) || type.equals(PROFILE_ACTION_RINGTONE_TYPE_ALARM))
			r_type=RingtoneManager.TYPE_ALARM;
		else if (type.equals(PROFILE_ACTION_RINGTONE_TYPE_NOTIFICATION))
			r_type=RingtoneManager.TYPE_NOTIFICATION;
		else if (type.equals(PROFILE_ACTION_RINGTONE_TYPE_RINGTONE))
			r_type=RingtoneManager.TYPE_RINGTONE;
		return r_type;

    }
    
    final static public void setActionRingtoneTypeSelectionListener(final GlobalParameters mGlblParms,final Dialog dialog, final Spinner spinnerType,
    		final Spinner spinnerName, final CustomSpinnerAdapter adapterName) {
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            final public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
            	String name="";
            	if (spinnerName!=null && spinnerName.getSelectedItem()!=null) 
            		name=spinnerName.getSelectedItem().toString();
            	setSpinnerRingtoneName(mGlblParms,dialog,spinnerName,adapterName,name,
            			getRingtoneTypeInt(spinnerType.getSelectedItem().toString()));
            }
            @Override
            final public void onNothingSelected(AdapterView<?> arg0) {}
        });
    };

	final public void setSpinnerRingtoneName(Dialog dialog, Spinner spinner,
			CustomSpinnerAdapter adapter, String rname, int rt) {
		setSpinnerRingtoneName(mGlblParms,dialog,spinner,adapter, rname, rt);
	}
	
	final static public void setSpinnerRingtoneName(final GlobalParameters mGlblParms,Dialog dialog, Spinner spinner,
			CustomSpinnerAdapter adapter, String rname, int rt) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_action_select_ringtone_name));
        spinner.setAdapter(adapter);
        adapter.clear();
//        mGlblParms.ringtoneList.addAll(addRingtoneList(rt));
        int fidx=-1;
        for (int i=0;i<mGlblParms.ringtoneList.size();i++) {
        	if (mGlblParms.ringtoneList.get(i).ringtone_type==rt) {
        		adapter.add(mGlblParms.ringtoneList.get(i).ringtone_name);
        	}
        }
        for (int i=0;i<adapter.getCount();i++) {
        	if (adapter.getItem(i).equals(rname)) {
        		fidx=i;
        		break;
        	}
        }
        if (fidx!=-1) {
        	spinner.setSelected(true);
        	spinner.setSelection(fidx);
        }
	};

    
    final public void setActionListSoundBtnListener(Dialog dialog, 
    		final TextView sound_file_name, CheckBox cb_volume, 
    		final SeekBar sb_volume, Button btnList, 
    		final Button playBtn) {
    	setActionListSoundBtnListener(mGlblParms, dialog, 
        		sound_file_name, cb_volume, 
        		sb_volume, btnList, 
        		playBtn);
    }
    final static public void setActionListSoundBtnListener(final GlobalParameters mGlblParms,Dialog dialog, 
    		final TextView sound_file_name, CheckBox cb_volume, 
    		final SeekBar sb_volume, Button btnList, 
    		final Button playBtn) {
    	btnList.setOnClickListener(new View.OnClickListener() {
    		final public void onClick(View v) {
//    			et_file.selectAll();
    			String[] mpdf=new String[]{mGlblParms.localRootDir,"",""};
    			if (!sound_file_name.getText().toString().equals(""))
    				mpdf=LocalMountPoint.convertFilePathToMountpointFormat(
    						mGlblParms.context, sound_file_name.getText().toString());
//    			Log.v("","mpdf 0="+mpdf[0]+", 1="+mpdf[1]+", 2="+mpdf[2]);
    			NotifyEvent ntfy=new NotifyEvent(mGlblParms.context);
    			ntfy.setListener(new NotifyEventListener() {
    				@Override
    				final public void positiveResponse(Context c, Object[] o) {
    					sound_file_name.setText((String)o[0]);
    					playBtn.setEnabled(true);
    				}
    				@Override
    				final public void negativeResponse(Context c, Object[] o) {}
    			});
    			mGlblParms.commonDlg.fileOnlySelectWithoutCreate(
    					mpdf[0], mpdf[1], mpdf[2], "Select file", ntfy);
    		}
    	});
    	
    };
    
    final public void setMusicPlayBackBtnListener(Dialog dialog, 
			final TextView sound_file_name, final CheckBox cb_volume, 
			final SeekBar sb_volume, final Button playBtn){
    	setMusicPlayBackBtnListener(mGlblParms, dialog, 
    			sound_file_name, cb_volume, 
    			sb_volume, playBtn);
    }
    final static public void setMusicPlayBackBtnListener(final GlobalParameters mGlblParms,Dialog dialog, 
			final TextView sound_file_name, final CheckBox cb_volume, 
			final SeekBar sb_volume, final Button playBtn){
		if (mGlblParms.mpMusic==null) mGlblParms.mpMusic=new MediaPlayer();
		
//		et_sound.selectAll();
		if (sound_file_name.getText().toString().equals("Not specified")) playBtn.setEnabled(false);
		if (cb_volume!=null) {
	    	cb_volume.setOnCheckedChangeListener(new OnCheckedChangeListener(){
	    		@Override
	    		final public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
	    			sb_volume.setEnabled(arg1);
	    		}
	    	});

			sb_volume.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
				@Override
				final public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
					if (mGlblParms.mpMusic.isPlaying()) {
						float nvol=((float)arg1)/100f;
						mGlblParms.mpMusic.setVolume(nvol, nvol);
					}
				}
				@Override
				final public void onStartTrackingTouch(SeekBar arg0) {}
				@Override
				final public void onStopTrackingTouch(SeekBar arg0) {}
			});
		}
		mGlblParms.mpMusic.setOnErrorListener(new OnErrorListener() {
			final public boolean onError(MediaPlayer arg0,
					int arg1, int arg2) {
				mGlblParms.commonDlg.showCommonDialog(false, "E", 
						mGlblParms.context.getString(R.string.msgs_edit_profile_action_mp_error_title), 
						String.format(mGlblParms.context.getString(R.string.msgs_edit_profile_action_mp_error_msg),
						arg1,arg2),null);
				mGlblParms.util.addDebugMsg(1, "E", "Media player error error="+arg1+
						", extra="+arg2+", file="+sound_file_name.getText().toString());
				playBtn.setClickable(true);
				return true;
			}
		});
		mGlblParms.mpMusic.setOnCompletionListener(new OnCompletionListener(){
			@Override
			final public void onCompletion(MediaPlayer arg0) {
				mGlblParms.util.addDebugMsg(2, "I", "setMusicPlayBack completed, file="+
						sound_file_name.getText().toString());
				playBtn.setText(mGlblParms.context.getString(R.string.msgs_edit_profile_action_start_play_back));
				playBtn.setClickable(true);
			}
		});
		mGlblParms.mpMusic.setOnPreparedListener(new OnPreparedListener(){
			@Override
			final public void onPrepared(MediaPlayer mp) {
				mGlblParms.util.addDebugMsg(2, "I", "setMusicPlayBack prepared, file="+
						sound_file_name.getText().toString());
				playBtn.setText(mGlblParms.context.getString(R.string.msgs_edit_profile_action_stop_play_back));
				mGlblParms.tcMusic.setEnabled();
				startMusicPlayBack(mGlblParms);
				playBtn.setClickable(true);
			}
		});
		playBtn.setClickable(true);
		playBtn.setText(mGlblParms.context.getString(R.string.msgs_edit_profile_action_start_play_back));
		playBtn.setOnClickListener(new OnClickListener() {
			@Override
			final public void onClick(View arg0) {
				if (sound_file_name.getText().toString().equals("")) 
					playBtn.setEnabled(false);
				else {
					if (mGlblParms.mpMusic.isPlaying()) {//Stop play back
						stopMusicPlayBack(mGlblParms);
						playBtn.setText(mGlblParms.context.getString(R.string.msgs_edit_profile_action_start_play_back));
					} else {//Start play back
						playBtn.setClickable(false);
						File lf=new File(sound_file_name.getText().toString());
						if (lf.exists()) {
							mGlblParms.mpMusic.reset();
							try {
								mGlblParms.mpMusic.setAudioStreamType(AudioManager.STREAM_MUSIC);
								mGlblParms.mpMusic.setDataSource(sound_file_name.getText().toString());
								mGlblParms.mpMusic.prepareAsync();
								float vol=1;
								if (cb_volume!=null && cb_volume.isChecked()) 
									vol=((float)sb_volume.getProgress())/100f;
								mGlblParms.mpMusic.setVolume(vol,vol);
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalStateException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							playBtn.setClickable(true);
							mGlblParms.commonDlg.showCommonDialog(false, "E", 
									mGlblParms.context.getString(R.string.msgs_edit_profile_action_mp_error_title), 
									mGlblParms.context.getString(R.string.msgs_edit_profile_action_mp_file_not_found),null);
							mGlblParms.util.addDebugMsg(1, "E", "Specified file can not be found, file="+sound_file_name.getText().toString());
						}
					}
				}
			}
		});
	};

	final static private  void startMusicPlayBack(final GlobalParameters mGlblParms) {
		final int duration=mGlblParms.mpMusic.getDuration();
		mGlblParms.util.addDebugMsg(1, "I", "startMusicPlayBack started, duration="+duration);
		new Thread() {
			@Override
			public void run() {
				mGlblParms.mpMusic.start();
				for (int i=0;i<((duration+100)/100);i++) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (!mGlblParms.tcMusic.isEnabled()) {
						mGlblParms.util.addDebugMsg(1, "I", "startMusicPlayBack cancelled");
						break;
					}
				}
				mGlblParms.util.addDebugMsg(1, "I", "startMusicPlayBack expired, current position="+mGlblParms.mpMusic.getCurrentPosition());
				mGlblParms.mpMusic.reset();
			}
		}.start();
	};
	
	final public void stopMusicPlayBack() {
		stopMusicPlayBack(mGlblParms);
	}

	final static public void stopMusicPlayBack(GlobalParameters mGlblParms) {
		mGlblParms.util.addDebugMsg(1, "I", "stopMusicPlayBack enterd");
		if (mGlblParms.mpMusic!=null && mGlblParms.mpMusic.isPlaying()) {//Stop play back
			mGlblParms.tcMusic.setDisabled();
//			mGlblParms.mpMusic.reset();
		}
	};

	final public void setRingtonePlayBackBtnListener(
			final Dialog dialog, 
			final Spinner spinnerType, final Spinner spinnerName, 
			final CheckBox cb_volume, final SeekBar sb_volume, 
			final Button playBtn){
		setRingtonePlayBackBtnListener(mGlblParms, dialog, 
				spinnerType, spinnerName, 
				cb_volume, sb_volume, 
				playBtn);
	}
	final static public void setRingtonePlayBackBtnListener(
			final GlobalParameters mGlblParms, final Dialog dialog, 
			final Spinner spinnerType, final Spinner spinnerName, 
			final CheckBox cb_volume, final SeekBar sb_volume, 
			final Button playBtn){
		mGlblParms.ringTonePlayBackEnabled=false;
		if (mGlblParms.mpRingtone==null) mGlblParms.mpRingtone=new MediaPlayer();

		playBtn.setClickable(false);
		
		if (cb_volume!=null) {
	    	cb_volume.setOnCheckedChangeListener(new OnCheckedChangeListener(){
	    		@Override
	    		final public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
	    			sb_volume.setEnabled(arg1);
	    		}
	    	});

			sb_volume.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
				@Override
				final public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
					if (mGlblParms.mpRingtone.isPlaying()) {
						float nvol=((float)arg1)/100f;
						mGlblParms.mpRingtone.setVolume(nvol, nvol);
					}
				}
				@Override
				final public void onStartTrackingTouch(SeekBar arg0) {}
				@Override
				final public void onStopTrackingTouch(SeekBar arg0) {}
			});
		}
		mGlblParms.mpRingtone.setOnErrorListener(new OnErrorListener() {
			final public boolean onError(MediaPlayer arg0,
					int arg1, int arg2) {
				mGlblParms.commonDlg.showCommonDialog(false, "E", 
						mGlblParms.context.getString(R.string.msgs_edit_profile_action_mp_error_title), 
						String.format(mGlblParms.context.getString(R.string.msgs_edit_profile_action_mp_error_msg),
						arg1,arg2),null);
				mGlblParms.util.addDebugMsg(1, "E", "Media player error error="+arg1+
						", extra="+arg2+", file="+spinnerName.getSelectedItem().toString());
				playBtn.setClickable(true);
				return true;
			}
		});
		mGlblParms.mpRingtone.setOnCompletionListener(new OnCompletionListener(){
			@Override
			final public void onCompletion(MediaPlayer arg0) {
				mGlblParms.util.addDebugMsg(2, "I", "setRingtonePlayBack completed, uri="+
						spinnerName.getSelectedItem().toString());
				playBtn.setText(mGlblParms.context.getString(R.string.msgs_edit_profile_action_start_play_back));
				playBtn.setClickable(true);
			}
		});
		mGlblParms.mpRingtone.setOnPreparedListener(new OnPreparedListener(){
			@Override
			final public void onPrepared(MediaPlayer arg0) {
				mGlblParms.util.addDebugMsg(2, "I", "setRingtonePlayBack prepared, uri="+
						spinnerName.getSelectedItem().toString());
				playBtn.setText(mGlblParms.context.getString(R.string.msgs_edit_profile_action_stop_play_back));
				mGlblParms.tcRingtone.setEnabled();
				startRingtonePlayBack(mGlblParms,dialog, spinnerType.getSelectedItem().toString(),
						playBtn);
				playBtn.setClickable(true);
			}
		});
		spinnerName.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			final public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				mGlblParms.util.addDebugMsg(2, "I", "Ringtone name was selected, name="+
					spinnerName.getSelectedItem().toString()+
					", clickable="+playBtn.isClickable()+
					", playBackEnabled="+mGlblParms.ringTonePlayBackEnabled+
					", pos="+arg2);
//				String b_name=playBtn.getText().toString();
//				if (playBtn.isClickable() && b_name.equals(
//						context.getString(R.string.msgs_edit_profile_action_start_play_back)))
				if (playBtn.isClickable() && mGlblParms.ringTonePlayBackEnabled) {
					stopRingtonePlayBack(mGlblParms);
					if (mGlblParms.mpRingtone.isPlaying()&&mGlblParms.ringtonePlayBackThread!=null)
						try {
							mGlblParms.ringtonePlayBackThread.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						} 
					initiateRingtonePlayBack(mGlblParms,spinnerType, spinnerName,cb_volume, sb_volume,
						playBtn);
				}
			}
			@Override
			final public void onNothingSelected(AdapterView<?> arg0) {}
			
		});
		playBtn.setClickable(true);
		playBtn.setText(mGlblParms.context.getString(R.string.msgs_edit_profile_action_start_play_back));
		playBtn.setOnClickListener(new OnClickListener() {
			@Override
			final public void onClick(View arg0) {
				initiateRingtonePlayBack(mGlblParms,spinnerType, spinnerName,cb_volume, sb_volume,
						playBtn);
			}
		});
//		ringTonePlayBackEnabled=true;
		playBtn.postDelayed(new Runnable(){
			@Override
			final public void run() {
				mGlblParms.ringTonePlayBackEnabled=true;				
			}
		},1000);
	};

	final static private  void initiateRingtonePlayBack(
			GlobalParameters mGlblParms, Spinner spinnerType, Spinner spinnerName,
			CheckBox cb_volume, SeekBar sb_volume,
			Button playBtn) {
		int r_type=getRingtoneTypeInt(spinnerType.getSelectedItem().toString());
		String r_name="";
		r_name=spinnerName.getSelectedItem().toString();
		if (r_type==0) 
			playBtn.setEnabled(false);
		else {
			if (mGlblParms.mpRingtone.isPlaying()) {//Stop play back
				stopRingtonePlayBack(mGlblParms);
				playBtn.setText(mGlblParms.context.getString(R.string.msgs_edit_profile_action_start_play_back));
			} else {//Start play back
				playBtn.setClickable(false);
				mGlblParms.mpRingtone.reset();
				try {
					int r_idx=spinnerName.getSelectedItemPosition();
					for (int i=0;i<mGlblParms.ringtoneList.size();i++) {
						if (mGlblParms.ringtoneList.get(i).ringtone_type==r_type &&
								mGlblParms.ringtoneList.get(i).ringtone_name.equals(r_name) &&
								mGlblParms.ringtoneList.get(i).index_no==r_idx) {
//							mGlblParms.mpRingtone.setAudioStreamType(r_type);
							mGlblParms.mpRingtone.setAudioStreamType(AudioManager.STREAM_RING);
							mGlblParms.mpRingtone.setDataSource(mGlblParms.context, mGlblParms.ringtoneList.get(i).ringtone_uri);
							mGlblParms.mpRingtone.setLooping(false);
							float vol=1;
							if (cb_volume!=null && cb_volume.isChecked()) 
								vol=((float)sb_volume.getProgress())/100f;
							mGlblParms.mpRingtone.setVolume(vol,vol);
							mGlblParms.mpRingtone.prepareAsync();
							break;
						}
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	
	};
	
	final static private  void startRingtonePlayBack(
			final GlobalParameters mGlblParms, Dialog dialog, String rt, final Button playBtn) {
		int t_dur=0;
		if (rt.equals(PROFILE_ACTION_RINGTONE_TYPE_NOTIFICATION)) t_dur=mGlblParms.mpRingtone.getDuration();
		else t_dur=RINGTONE_PLAYBACK_TIME;
		final int duration=t_dur;

		mGlblParms.util.addDebugMsg(1, "I", "startRingtonePlayBack started");
		final Handler hndl=new Handler();
		mGlblParms.ringtonePlayBackThread=new Thread() {
			@Override
			final public void run() {
				mGlblParms.mpRingtone.start();
				int lpno=(duration+100)/100;
				for (int i=0;i<lpno;i++) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (mGlblParms.tcRingtone!=null && !mGlblParms.tcRingtone.isEnabled()) {
						mGlblParms.util.addDebugMsg(1, "I", "startRingtonePlayBack cancelled");
//						mGlblParms.mpRingtone.stop();						
						break;
					}
				}
				mGlblParms.util.addDebugMsg(1, "I", "startRingtonePlayBack expired, current position="+mGlblParms.mpRingtone.getCurrentPosition());
				
				mGlblParms.mpRingtone.stop();
				mGlblParms.mpRingtone.reset();
				hndl.post(new Runnable(){
					@Override
					final public void run() {
						playBtn.setText(mGlblParms.context.getString(R.string.msgs_edit_profile_action_start_play_back));
						playBtn.setClickable(true);
					}
				});
			}
		};
		mGlblParms.ringtonePlayBackThread.start();
	};

	final public void stopRingtonePlayBack() {
		stopRingtonePlayBack(mGlblParms);
	};
	final static public void stopRingtonePlayBack(GlobalParameters mGlblParms) {
		mGlblParms.util.addDebugMsg(1, "I", "stopRingtonePlayBack entered");
		if (mGlblParms.mpRingtone!=null && mGlblParms.mpRingtone.isPlaying()) {//Stop play back
			mGlblParms.tcRingtone.setDisabled();
//			mGlblParms.mpRingtone.reset();
		}
	};

	final static public void deleteProfileDlg(
			final GlobalParameters mGlblParms, final String curr_grp, 
			final AdapterProfileList pfla, final ListView pflv, final NotifyEvent ntfy_unselect) {
		mGlblParms.util.addDebugMsg(2,"I", "deleteProfileDlg entered");
		final ArrayList<ProfileListItem> del_pfl = new ArrayList<ProfileListItem>();
		
		for (int i=0;i<pfla.getProfItemCount();i++) {
			if (pfla.getProfItem(i).isProfileItemSelected()) del_pfl.add(pfla.getProfItem(i));
		}
		if (del_pfl.size()!=0) {
			String del_il="";
			for (int i=0;i<del_pfl.size();i++) {
				del_il=del_il+del_pfl.get(i).getProfileName()+"\n";
			}
			NotifyEvent ntfy = new NotifyEvent(mGlblParms.context);
			ntfy.setListener(new NotifyEventListener() {
				@Override
				final public void positiveResponse(Context c, Object[] o) {
					String del_il="";
					for (int i=0;i<del_pfl.size();i++) {
						pfla.removeProfItem(del_pfl.get(i));
						del_il=del_il+del_pfl.get(i).getProfileName()+"\n";
					}
					mGlblParms.commonDlg.showCommonDialog(false,"I",
							mGlblParms.context.getString(R.string.msgs_delete_profile_result),
							del_il,null);
					mGlblParms.util.addDebugMsg(1,"I", "deleteProfileDlg profile was deleted:"+del_il);
					ProfileUtilities.verifyProfileIntegrity(mGlblParms.util,true,pfla,curr_grp);
					putProfileListToService(mGlblParms,pfla,ProfileUtilities.isProfileGroupActive(mGlblParms.util,pfla,curr_grp));
//					saveProfileToFileProfileOnly(false,pfla,pflv,"","");
//					if (isProfileGroupActive(pfla,curr_grp)) mGlblParms.util.reBuildTaskExecList();
					if (pfla.getProfItemCount()==0) {
						ProfileListItem tpli= new ProfileListItem();
						tpli.setTaskEntry(
								PROFILE_VERSION_CURRENT,curr_grp,false,
								System.currentTimeMillis(),"",
								mGlblParms.context.getString(R.string.msgs_no_profile_entry),
								"","","","",null,null);
						pfla.addProfItem(tpli);
						pfla.updateShowList();
					}
					ntfy_unselect.notifyToListener(true, null);
				}
				@Override
				final public void negativeResponse(Context c, Object[] o) {}
				
			});
			mGlblParms.commonDlg.showCommonDialog(true,"W",
					mGlblParms.context.getString(R.string.msgs_delete_profile_msg),
					del_il,ntfy);
		}
	};
	

	
	final static public void setSpinnerTriggerCat(final GlobalParameters mGlblParms,Dialog dialog, 
			final AdapterProfileList pfla, final ListView pflv,Spinner spinner, 
			CustomSpinnerAdapter adapter, String selected) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_task_select_trigger_event));
        spinner.setAdapter(adapter);
        adapter.clear();
        adapter.add(TRIGGER_EVENT_CATEGORY_BUILTIN);
        adapter.add(TRIGGER_EVENT_CATEGORY_TIME);
        adapter.add(TRIGGER_EVENT_CATEGORY_TASK);
		for (int i=0;i<adapter.getCount();i++)
			if (adapter.getItem(i).equals(selected)) spinner.setSelection(i);
	};
	
	final static public void setSpinnerEventBuiltin(final GlobalParameters mGlblParms,Dialog dialog, 
			final AdapterProfileList pfla, final ListView pflv,Spinner spinner, 
			CustomSpinnerAdapter adapter, String selected) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_task_select_trigger_event));
        spinner.setAdapter(adapter);
        adapter.clear();
        ArrayList<String> bevt=ProfileUtilities.buildBuiltinEventList();
        for (int i=0;i<bevt.size();i++) adapter.add(bevt.get(i));
        
        adapter.sort(new Comparator<String>() {
			@Override
			public int compare(String arg0, String arg1) {
				return arg0.compareToIgnoreCase(arg1);
			}
        });

        spinner.setSelection(0);
		for (int i=0;i<adapter.getCount();i++)
			if (adapter.getItem(i).equals(selected)) spinner.setSelection(i);
	};

	final static public void setSpinnerEventTime(final GlobalParameters mGlblParms,Dialog dialog, 
			final AdapterProfileList pfla, final ListView pflv,Spinner spinner, 
			CustomSpinnerAdapter adapter, String selected) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_task_select_trigger_event));
        spinner.setAdapter(adapter);
        adapter.clear();
        for (int i=0;i<pfla.getDataListCount();i++) {
        	if (pfla.getDataListItem(i).isProfileEnabled() &&
        			pfla.getDataListItem(i).isProfileGroupShowed() &&
        			pfla.getDataListItem(i).getProfileType()
        			.equals(PROFILE_TYPE_TIME)) {
        		adapter.add(pfla.getDataListItem(i).getProfileName());
        	}
        }
        
        adapter.sort(new Comparator<String>() {
			@Override
			public int compare(String arg0, String arg1) {
				return arg0.compareToIgnoreCase(arg1);
			}
        });

        if (adapter.getCount()==0) adapter.add("** Profile not available **");
        spinner.setSelection(0);
		for (int i=0;i<adapter.getCount();i++)
			if (adapter.getItem(i).equals(selected)) spinner.setSelection(i);
	};

	final static public void setSpinnerEventTask(final GlobalParameters mGlblParms,Dialog dialog, 
			final AdapterProfileList pfla, final ListView pflv,Spinner spinner, 
			CustomSpinnerAdapter adapter, String selected) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_task_select_trigger_event));
        spinner.setAdapter(adapter);
        adapter.clear();
        adapter.add(TRIGGER_EVENT_TASK);
        spinner.setSelection(0);
	};

	final static public void setSpinnerGroup(final GlobalParameters mGlblParms,Dialog dialog, AdapterProfileList pfla, 
			Spinner spinner, CustomSpinnerAdapter adapter, String c_grp) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_hdr_group));
        spinner.setAdapter(adapter);
        adapter.clear();
        ArrayList<String> gl=ProfileUtilities.createProfileGroupList(mGlblParms.util,pfla);
        for (int i=0;i<gl.size();i++) adapter.add(gl.get(i));
        for (int i=adapter.getCount()-1;i>=0;i--) {
        	if (adapter.getItem(i).equals(QUICK_TASK_GROUP_NAME)) 
        		adapter.remove(adapter.getItem(i));
        }
        int sel=0;
        for (int i=adapter.getCount()-1;i>=0;i--) {
        	if (adapter.getItem(i).equals(c_grp)) {
        		sel=i;
        		break;
        	}
        }
   		spinner.setSelection(sel);
		
	};
	
	final static public void setSpinnerSelectAction(final GlobalParameters mGlblParms,Dialog dialog, Spinner spinner, 
			CustomSpinnerAdapter adapter) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_action_select_category));
        spinner.setAdapter(adapter);
        adapter.clear();
        adapter.add(mGlblParms.context.getString(R.string.msgs_edit_profile_action_select_user));
        adapter.add(mGlblParms.context.getString(R.string.msgs_edit_profile_action_select_primitive));
        adapter.add(mGlblParms.context.getString(R.string.msgs_edit_profile_action_select_abort));
        adapter.add(mGlblParms.context.getString(R.string.msgs_edit_profile_action_select_skip));
        adapter.add(mGlblParms.context.getString(R.string.msgs_edit_profile_action_select_cancel));
        adapter.add(mGlblParms.context.getString(R.string.msgs_edit_profile_action_select_block));

   		spinner.setSelection(0);
	};
	
	final public void setSpinnerActivityName(Dialog dialog, Spinner spinner,
			CustomSpinnerAdapter adapter, String selected) {
		setSpinnerActivityName(mGlblParms,dialog, spinner,
				adapter, selected);
	};
	
	final static public void setSpinnerActivityName(final GlobalParameters mGlblParms,Dialog dialog, Spinner spinner,
			CustomSpinnerAdapter adapter, String selected) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_action_select_android_activity));
        spinner.setAdapter(adapter);
        adapter.clear();
        for (int i=0;i<mGlblParms.androidApplicationList.size();i++) 
        	adapter.add(mGlblParms.androidApplicationList.get(i));
		adapter.sort(new Comparator<String>() {
	        @Override
	        public int compare(String s1, String s2){
	            return s1.compareToIgnoreCase(s2);
	        }
		});
		adapter.insert("** Not Selected **",0);
		spinner.setSelection(0);
		if (!selected.equals("")) {
			for (int i=0;i<adapter.getCount();i++)
				if (adapter.getItem(i).startsWith(selected)) spinner.setSelection(i);
		}
	};

	final static public void setSpinnerActionProfile(final GlobalParameters mGlblParms,Dialog dialog, 
			final AdapterProfileList pfla, final ListView pflv,Spinner spinner, 
			CustomSpinnerAdapter adapter) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_action_select_user));
        spinner.setAdapter(adapter);
        adapter.clear();
        
//        if (adapter!=null) {
        for (int i=0;i<pfla.getDataListCount();i++) {
//        	Log.v("","group="+pfla.getDataListItem(i).getProfileGroup()+", name="+pfla.getDataListItem(i).getProfileName()+
//        			", show="+pfla.getDataListItem(i).isProfileGroupShowed());
        	if (pfla.getDataListItem(i).isProfileEnabled() &&
        			pfla.getDataListItem(i).getProfileType().equals(PROFILE_TYPE_ACTION)) {
//        		mGlblParms.util.addDebugMsg(1, "I", "setSpinnerActionProfile prof="+pfla.getDataListItem(i).getProfileName()+
//        				", GroupShowed="+pfla.getDataListItem(i).isProfileGroupShowed());
        		if (pfla.getDataListItem(i).isProfileGroupShowed()) {
            		adapter.add(pfla.getDataListItem(i).getProfileName());
        		}
        	}
        }
//        }
        adapter.sort(new Comparator<String>() {
			@Override
			public int compare(String arg0, String arg1) {
				return arg0.compareToIgnoreCase(arg1);
			}
        });
        adapter.insert("** Not Selected **",0);
//        spinner.setSelection(0);
//		for (int i=0;i<adapter.getCount();i++)
//			if (adapter.getItem(i).startsWith(selected)) spinner.setSelection(i);
		
	};

	final static public void setSpinnerBuiltinPrimitiveAction(final GlobalParameters mGlblParms,Dialog dialog, Spinner spinner, 
			CustomSpinnerAdapter adapter) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_action_select_primitive));
        adapter.clear();
//        adapter.add(BUILTIN_ACTION_BLINK_LED_BLUE);
//        adapter.add(BUILTIN_ACTION_BLINK_LED_GREEN);
//        adapter.add(BUILTIN_ACTION_BLINK_LED_RED);
        adapter.add(BUILTIN_ACTION_ABORT);
        adapter.add(BUILTIN_ACTION_AUTO_SYNC_ENABLED);
        adapter.add(BUILTIN_ACTION_AUTO_SYNC_DISABLED);
        adapter.add(BUILTIN_ACTION_BLUETOOTH_ON);
        adapter.add(BUILTIN_ACTION_BLUETOOTH_OFF);
        adapter.add(BUILTIN_ACTION_SWITCH_TO_HOME);
        adapter.add(BUILTIN_ACTION_SCREEN_LOCKED);
        adapter.add(BUILTIN_ACTION_SCREEN_KEYGUARD_DISABLED);
        adapter.add(BUILTIN_ACTION_SCREEN_KEYGUARD_ENABLED);
        adapter.add(BUILTIN_ACTION_SCREEN_ON);
        adapter.add(BUILTIN_ACTION_SCREEN_ON_ASYNC);
        adapter.add(BUILTIN_ACTION_VIBRATE);
        adapter.add(BUILTIN_ACTION_RESTART_SCHEDULER);
        adapter.add(BUILTIN_ACTION_RINGER_NORMAL);
        adapter.add(BUILTIN_ACTION_RINGER_SILENT);
        adapter.add(BUILTIN_ACTION_RINGER_VIBRATE);
        adapter.add(BUILTIN_ACTION_WAIT_1_SEC);
        adapter.add(BUILTIN_ACTION_WAIT_5_SEC);
        adapter.add(BUILTIN_ACTION_WAIT_1_MIN);
        adapter.add(BUILTIN_ACTION_WAIT_5_MIN);
        adapter.add(BUILTIN_ACTION_WIFI_ON);
        adapter.add(BUILTIN_ACTION_WIFI_OFF);
        adapter.add(BUILTIN_ACTION_WIFI_DISABLE_CONNECTED_SSID);
        adapter.add(BUILTIN_ACTION_WIFI_REMOVE_CONNECTED_SSID);
//        adapter.add(BUILTIN_ACTION_PLAYBACK_DEFAULT_ALARM);
        adapter.add(BUILTIN_ACTION_PLAYBACK_DEFAULT_NOTIFICATION);
//        adapter.add(BUILTIN_ACTION_PLAYBACK_DEFAULT_RINGTONE);
        adapter.sort(new Comparator<String>() {
			@Override
			public int compare(String arg0, String arg1) {
				return arg0.compareToIgnoreCase(arg1);
			}
        });
        adapter.insert("** Not Selected **",0);
        spinner.setAdapter(adapter);
	};

	final static public void setSpinnerBuiltinAbortAction(final GlobalParameters mGlblParms,Dialog dialog, Spinner spinner, 
			CustomSpinnerAdapter adapter) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_action_select_abort));
        spinner.setAdapter(adapter);
        adapter.clear();
        adapter.add(BUILTIN_ACTION_ABORT_IF_WIFI_ON);
        adapter.add(BUILTIN_ACTION_ABORT_IF_WIFI_DISCONNECTED);
        adapter.add(BUILTIN_ACTION_ABORT_IF_WIFI_CONNECTED);
        adapter.add(BUILTIN_ACTION_ABORT_IF_WIFI_OFF);
        adapter.add(BUILTIN_ACTION_ABORT_IF_BLUETOOTH_ON);
        adapter.add(BUILTIN_ACTION_ABORT_IF_BLUETOOTH_CONNECTED);
        adapter.add(BUILTIN_ACTION_ABORT_IF_BLUETOOTH_DISCONNECTED);
        adapter.add(BUILTIN_ACTION_ABORT_IF_BLUETOOTH_OFF);
        adapter.add(BUILTIN_ACTION_ABORT_IF_SCREEN_UNLOCKED);
        adapter.add(BUILTIN_ACTION_ABORT_IF_SCREEN_LOCKED);
        adapter.add(BUILTIN_ACTION_ABORT_IF_SCREEN_ON);
        adapter.add(BUILTIN_ACTION_ABORT_IF_SCREEN_OFF);
        
        adapter.add(BUILTIN_ACTION_ABORT_IF_TRUSTED);
        adapter.add(BUILTIN_ACTION_ABORT_IF_NOT_TRUSTED);
        
        adapter.add(BUILTIN_ACTION_ABORT_IF_PROXIMITY_DETECTED);
        adapter.add(BUILTIN_ACTION_ABORT_IF_PROXIMITY_UNDETECTED);
        adapter.add(BUILTIN_ACTION_ABORT_IF_LIGHT_DETECTED);
        adapter.add(BUILTIN_ACTION_ABORT_IF_LIGHT_UNDETECTED);
        adapter.add(BUILTIN_ACTION_ABORT_IF_POWER_IS_AC_OR_CHRAGE);
        adapter.add(BUILTIN_ACTION_ABORT_IF_POWER_IS_BATTERY);
        adapter.add(BUILTIN_ACTION_ABORT_IF_CALL_STATE_IDLE);
        adapter.add(BUILTIN_ACTION_ABORT_IF_CALL_STATE_OFF_HOOK);
        adapter.add(BUILTIN_ACTION_ABORT_IF_CALL_STATE_RINGING);
        adapter.add(BUILTIN_ACTION_ABORT_IF_AIRPLANE_MODE_ON);
        adapter.add(BUILTIN_ACTION_ABORT_IF_AIRPLANE_MODE_OFF);
        adapter.add(BUILTIN_ACTION_ABORT_IF_MOBILE_NETWORK_CONNECTED);
        adapter.add(BUILTIN_ACTION_ABORT_IF_MOBILE_NETWORK_DISCONNECTED);
//        adapter.add(BUILTIN_ACTION_ABORT_IF_NETWORK_CONNECTED);
//        adapter.add(BUILTIN_ACTION_ABORT_IF_NETWORK_DISCONNECTED);
        adapter.add(BUILTIN_ACTION_ABORT_IF_ORIENTATION_LANDSCAPE);
        adapter.add(BUILTIN_ACTION_ABORT_IF_ORIENTATION_PORTRAIT);
        
        adapter.sort(new Comparator<String>() {
			@Override
			public int compare(String arg0, String arg1) {
				return arg0.compareToIgnoreCase(arg1);
			}
        });
        adapter.insert("** Not Selected **",0);
	};

	final static public void setSpinnerBuiltinSkipAction(final GlobalParameters mGlblParms,Dialog dialog, Spinner spinner, 
			CustomSpinnerAdapter adapter) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_action_select_skip));
        spinner.setAdapter(adapter);
        adapter.clear();
    	adapter.add(BUILTIN_ACTION_SKIP_IF_WIFI_ON);
    	adapter.add(BUILTIN_ACTION_SKIP_IF_WIFI_CONNECTED);
    	adapter.add(BUILTIN_ACTION_SKIP_IF_WIFI_DISCONNECTED);
    	adapter.add(BUILTIN_ACTION_SKIP_IF_WIFI_OFF);
    	adapter.add(BUILTIN_ACTION_SKIP_IF_BLUETOOTH_ON);
    	adapter.add(BUILTIN_ACTION_SKIP_IF_BLUETOOTH_CONNECTED);
    	adapter.add(BUILTIN_ACTION_SKIP_IF_BLUETOOTH_DISCONNECTED);
    	adapter.add(BUILTIN_ACTION_SKIP_IF_BLUETOOTH_OFF);
    	adapter.add(BUILTIN_ACTION_SKIP_IF_SCREEN_UNLOCKED);
    	adapter.add(BUILTIN_ACTION_SKIP_IF_SCREEN_LOCKED);
    	adapter.add(BUILTIN_ACTION_SKIP_IF_SCREEN_ON);
    	adapter.add(BUILTIN_ACTION_SKIP_IF_SCREEN_OFF);
    	
        adapter.add(BUILTIN_ACTION_SKIP_IF_TRUSTED);
        adapter.add(BUILTIN_ACTION_SKIP_IF_NOT_TRUSTED);
    	
    	adapter.add(BUILTIN_ACTION_SKIP_IF_POWER_IS_AC_OR_CHRAGE);
    	adapter.add(BUILTIN_ACTION_SKIP_IF_POWER_IS_BATTERY);
    	adapter.add(BUILTIN_ACTION_SKIP_IF_PROXIMITY_DETECTED);
    	adapter.add(BUILTIN_ACTION_SKIP_IF_PROXIMITY_UNDETECTED);
    	adapter.add(BUILTIN_ACTION_SKIP_IF_LIGHT_DETECTED);
    	adapter.add(BUILTIN_ACTION_SKIP_IF_LIGHT_UNDETECTED);
    	adapter.add(BUILTIN_ACTION_SKIP_IF_CALL_STATE_IDLE);
    	adapter.add(BUILTIN_ACTION_SKIP_IF_CALL_STATE_OFF_HOOK);
    	adapter.add(BUILTIN_ACTION_SKIP_IF_CALL_STATE_RINGING);
    	adapter.add(BUILTIN_ACTION_SKIP_IF_AIRPLANE_MODE_ON);
    	adapter.add(BUILTIN_ACTION_SKIP_IF_AIRPLANE_MODE_OFF);
    	adapter.add(BUILTIN_ACTION_SKIP_IF_MOBILE_NETWORK_CONNECTED);
    	adapter.add(BUILTIN_ACTION_SKIP_IF_MOBILE_NETWORK_DISCONNECTED);
//    	adapter.add(BUILTIN_ACTION_SKIP_IF_NETWORK_CONNECTED);
//    	adapter.add(BUILTIN_ACTION_SKIP_IF_NETWORK_DISCONNECTED);
    	adapter.add(BUILTIN_ACTION_SKIP_IF_ORIENTATION_LANDSCAPE);
    	adapter.add(BUILTIN_ACTION_SKIP_IF_ORIENTATION_PORTRAIT);
        
        adapter.sort(new Comparator<String>() {
			@Override
			public int compare(String arg0, String arg1) {
				return arg0.compareToIgnoreCase(arg1);
			}
        });
        adapter.insert("** Not Selected **",0);
	};

	final static public void setSpinnerBuiltinCancelAction(final GlobalParameters mGlblParms,Dialog dialog, Spinner spinner, 
			CustomSpinnerAdapter adapter) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_action_select_cancel));
        spinner.setAdapter(adapter);
        adapter.clear();

        adapter.add(BUILTIN_ACTION_CANCEL_EVENT_BOOT_COMPLETED);
        adapter.add(BUILTIN_ACTION_CANCEL_EVENT_WIFI_ON);
        adapter.add(BUILTIN_ACTION_CANCEL_EVENT_WIFI_CONNECTED);
        adapter.add(BUILTIN_ACTION_CANCEL_EVENT_WIFI_DISCONNECTED);
        adapter.add(BUILTIN_ACTION_CANCEL_EVENT_WIFI_OFF);
        adapter.add(BUILTIN_ACTION_CANCEL_EVENT_BLUETOOTH_ON);
        adapter.add(BUILTIN_ACTION_CANCEL_EVENT_BLUETOOTH_CONNECTED);
        adapter.add(BUILTIN_ACTION_CANCEL_EVENT_BLUETOOTH_DISCONNECTED);
        adapter.add(BUILTIN_ACTION_CANCEL_EVENT_BLUETOOTH_OFF);
        adapter.add(BUILTIN_ACTION_CANCEL_EVENT_PROXIMITY_DETECTED);
        adapter.add(BUILTIN_ACTION_CANCEL_EVENT_PROXIMITY_UNDETECTED);
        adapter.add(BUILTIN_ACTION_CANCEL_EVENT_LIGHT_DETECTED);
        adapter.add(BUILTIN_ACTION_CANCEL_EVENT_LIGHT_UNDETECTED);
        adapter.add(BUILTIN_ACTION_CANCEL_EVENT_SCREEN_LOCKED);
        adapter.add(BUILTIN_ACTION_CANCEL_EVENT_SCREEN_UNLOCKED);
        adapter.add(BUILTIN_ACTION_CANCEL_EVENT_POWER_SOURCE_CHANGED_AC);
        adapter.add(BUILTIN_ACTION_CANCEL_EVENT_POWER_SOURCE_CHANGED_BATTERY);
        adapter.add(BUILTIN_ACTION_CANCEL_EVENT_PHONE_CALL_STATE_IDLE);
        adapter.add(BUILTIN_ACTION_CANCEL_EVENT_PHONE_CALL_STATE_OFF_HOOK);
        adapter.add(BUILTIN_ACTION_CANCEL_EVENT_PHONE_CALL_STATE_RINGING);
        adapter.add(BUILTIN_ACTION_CANCEL_EVENT_AIRPLANE_MODE_ON);
        adapter.add(BUILTIN_ACTION_CANCEL_EVENT_AIRPLANE_MODE_OFF);
        adapter.add(BUILTIN_ACTION_CANCEL_EVENT_MOBILE_NETWORK_CONNECTED);
        adapter.add(BUILTIN_ACTION_CANCEL_EVENT_MOBILE_NETWORK_DISCONNECTED);
//        adapter.add(BUILTIN_ACTION_CANCEL_EVENT_NETWORK_CONNECTED);
//        adapter.add(BUILTIN_ACTION_CANCEL_EVENT_NETWORK_DISCONNECTED);

        adapter.sort(new Comparator<String>() {
			@Override
			public int compare(String arg0, String arg1) {
				return arg0.compareToIgnoreCase(arg1);
			}
        });
        adapter.insert("** Not Selected **",0);
    };

	final static public void setSpinnerBuiltinBlockAction(final GlobalParameters mGlblParms,Dialog dialog, Spinner spinner, 
			CustomSpinnerAdapter adapter) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_action_select_block));
        spinner.setAdapter(adapter);
        adapter.clear();

        adapter.add(BUILTIN_ACTION_BLOCK_EVENT_CLEAR);
        adapter.add(BUILTIN_ACTION_BLOCK_EVENT_BLOCK_ALL);
        adapter.add(BUILTIN_ACTION_BLOCK_EVENT_BOOT_COMPLETED);
        adapter.add(BUILTIN_ACTION_BLOCK_EVENT_WIFI_ON);
        adapter.add(BUILTIN_ACTION_BLOCK_EVENT_WIFI_CONNECTED);
        adapter.add(BUILTIN_ACTION_BLOCK_EVENT_WIFI_DISCONNECT);
        adapter.add(BUILTIN_ACTION_BLOCK_EVENT_WIFI_OFF);
        adapter.add(BUILTIN_ACTION_BLOCK_EVENT_BLUETOOTH_ON);
        adapter.add(BUILTIN_ACTION_BLOCK_EVENT_BLUETOOTH_CONNECTED);
        adapter.add(BUILTIN_ACTION_BLOCK_EVENT_BLUETOOTH_DISCONNECTED);
        adapter.add(BUILTIN_ACTION_BLOCK_EVENT_BLUETOOTH_OFF);
        adapter.add(BUILTIN_ACTION_BLOCK_EVENT_PROXIMITY_DETECTED);
        adapter.add(BUILTIN_ACTION_BLOCK_EVENT_PROXIMITY_UNDETECTED);
        adapter.add(BUILTIN_ACTION_BLOCK_EVENT_LIGHT_DETECTED);
        adapter.add(BUILTIN_ACTION_BLOCK_EVENT_LIGHT_UNDETECTED);
        adapter.add(BUILTIN_ACTION_BLOCK_EVENT_PHONE_CALL_STATE_IDLE);
        adapter.add(BUILTIN_ACTION_BLOCK_EVENT_PHONE_CALL_STATE_OFF_HOOK);
        adapter.add(BUILTIN_ACTION_BLOCK_EVENT_PHONE_CALL_STATE_RINGING);
        adapter.add(BUILTIN_ACTION_BLOCK_EVENT_AIRPLANE_MODE_ON);
        adapter.add(BUILTIN_ACTION_BLOCK_EVENT_AIRPLANE_MODE_OFF);
        adapter.add(BUILTIN_ACTION_BLOCK_EVENT_MOBILE_NETWORK_CONNECTED);
        adapter.add(BUILTIN_ACTION_BLOCK_EVENT_MOBILE_NETWORK_DISCONNECTED);
//        adapter.add(BUILTIN_ACTION_BLOCK_EVENT_NETWORK_CONNECTED);
//        adapter.add(BUILTIN_ACTION_BLOCK_EVENT_NETWORK_DISCONNECTED);
        
        adapter.sort(new Comparator<String>() {
			@Override
			public int compare(String arg0, String arg1) {
				return arg0.compareToIgnoreCase(arg1);
			}
        });
        adapter.insert("** Not Selected **",0);
	};


	final static public void setSpinnerTimeType(final GlobalParameters mGlblParms,Dialog dialog, Spinner spinner, 
			CustomSpinnerAdapter adapter, String selected) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_action_time_select_type));
        spinner.setAdapter(adapter);
        adapter.clear();
        adapter.add(PROFILE_ACTION_TYPE_TIME_RESET_INTERVAL_TIMER);
//        adapter.add(PROFILE_ACTION_TYPE_MESSAGE_WITHOUT_TEXT);
        for (int i=0;i<adapter.getCount();i++) 
        	if (adapter.getItem(i).equals(selected)) {
        		spinner.setSelection(i);
        		break;
        	}
	};

	final static public void setSpinnerTimeTarget(final GlobalParameters mGlblParms,Dialog dialog, Spinner spinner, 
			CustomSpinnerAdapter adapter, AdapterProfileList pfla, String curr_grp, String selected) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_action_time_select_target));
        spinner.setAdapter(adapter);
        adapter.clear();
        
        for (int i=0;i<pfla.getDataListCount();i++) {
        	if (pfla.getDataListItem(i).getProfileType().equals(PROFILE_TYPE_TIME) &&
        			pfla.getDataListItem(i).getProfileGroup().equals(curr_grp) &&
        			pfla.getDataListItem(i).getTimeType().equals(PROFILE_DATE_TIME_TYPE_INTERVAL)) {
        		adapter.add(pfla.getDataListItem(i).getProfileName());
        	}
        }
        if (adapter.getCount()==0) adapter.add("** Not available **");
        else adapter.insert("** Not Selected **", 0);
        
//        adapter.add(PROFILE_ACTION_TYPE_MESSAGE_WITHOUT_TEXT);
        for (int i=0;i<adapter.getCount();i++) 
        	if (adapter.getItem(i).equals(selected)) {
        		spinner.setSelection(i);
        		break;
        	}
	};

	final static public void setSpinnerTaskType(final GlobalParameters mGlblParms,Dialog dialog, Spinner spinner, 
			CustomSpinnerAdapter adapter, String selected) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_action_task_select_type));
        spinner.setAdapter(adapter);
        adapter.clear();
        adapter.add(PROFILE_ACTION_TYPE_TASK_START_TASK);
        adapter.add(PROFILE_ACTION_TYPE_TASK_CANCEL_TASK);
//        adapter.add(PROFILE_ACTION_TYPE_MESSAGE_WITHOUT_TEXT);
        for (int i=0;i<adapter.getCount();i++) 
        	if (adapter.getItem(i).equals(selected)) {
        		spinner.setSelection(i);
        		break;
        	}
	};

	final static public void setSpinnerTaskTarget(final GlobalParameters mGlblParms,boolean task_only, 
			Dialog dialog, Spinner spinner, CustomSpinnerAdapter adapter, 
			AdapterProfileList pfla, String curr_grp, String selected) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_action_task_select_target));
        spinner.setAdapter(adapter);
        adapter.clear();
        
        for (int i=0;i<pfla.getDataListCount();i++) {
        	if (pfla.getDataListItem(i).getProfileType().equals(PROFILE_TYPE_TASK) &&
        			pfla.getDataListItem(i).getProfileGroup().equals(curr_grp)) {
        		if (task_only) {
        			if (pfla.getDataListItem(i).getTaskTriggerList().get(0).equals(TRIGGER_EVENT_TASK)) {
        				adapter.add(pfla.getDataListItem(i).getProfileName());
        			}
        		} else {
        			adapter.add(pfla.getDataListItem(i).getProfileName());
        		}
        	}
        }
        if (adapter.getCount()==0) adapter.add("** Not available **");
        else adapter.insert("** Not Selected **", 0);

//        adapter.add(PROFILE_ACTION_TYPE_MESSAGE_WITHOUT_TEXT);
        for (int i=0;i<adapter.getCount();i++) 
        	if (adapter.getItem(i).equals(selected)) {
        		spinner.setSelection(i);
        		break;
        	}
	};

	final static public void setSpinnerWaitTarget(final GlobalParameters mGlblParms, 
			Dialog dialog, Spinner spinner, CustomSpinnerAdapter adapter, 
			AdapterProfileList pfla, String curr_grp, String selected) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_action_task_select_target));
        spinner.setAdapter(adapter);
        adapter.clear();
        adapter.add(PROFILE_ACTION_TYPE_WAIT_TARGET_BLUETOOTH_CONNECTED);
        adapter.add(PROFILE_ACTION_TYPE_WAIT_TARGET_WIFI_CONNECTED);

//        adapter.add(PROFILE_ACTION_TYPE_MESSAGE_WITHOUT_TEXT);
        for (int i=0;i<adapter.getCount();i++) 
        	if (adapter.getItem(i).equals(selected)) {
        		spinner.setSelection(i);
        		break;
        	}
	};

	final static public void setSpinnerWaitTimeoutType( final GlobalParameters mGlblParms,
			Dialog dialog, Spinner spinner, CustomSpinnerAdapter adapter, 
			AdapterProfileList pfla, String curr_grp, String selected) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_action_task_select_target));
        spinner.setAdapter(adapter);
        adapter.clear();
        
        adapter.add(PROFILE_ACTION_TYPE_WAIT_TIMEOUT_TYPE_NOTIMEOUT);
        adapter.add(PROFILE_ACTION_TYPE_WAIT_TIMEOUT_TYPE_TIMEOUTIS);
        
//        adapter.add(PROFILE_ACTION_TYPE_MESSAGE_WITHOUT_TEXT);
        for (int i=0;i<adapter.getCount();i++) 
        	if (adapter.getItem(i).equals(selected)) {
        		spinner.setSelection(i);
        		break;
        	}
	};
	
	final static public void setSpinnerWaitTimeoutValue( final GlobalParameters mGlblParms,
			Dialog dialog, Spinner spinner, CustomSpinnerAdapter adapter, 
			AdapterProfileList pfla, String curr_grp, String selected) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_action_task_select_target));
        spinner.setAdapter(adapter);
        adapter.clear();
        
        for (int i=1;i<61;i++) adapter.add(String.valueOf(i));
        
//        adapter.add(PROFILE_ACTION_TYPE_MESSAGE_WITHOUT_TEXT);
        for (int i=0;i<adapter.getCount();i++) 
        	if (adapter.getItem(i).equals(selected)) {
        		spinner.setSelection(i);
        		break;
        	}
	};

	final static public void setSpinnerWaitTimeoutUnits( final GlobalParameters mGlblParms,
			Dialog dialog, Spinner spinner, CustomSpinnerAdapter adapter, 
			AdapterProfileList pfla, String curr_grp, String selected) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(mGlblParms.context.getString(R.string.msgs_edit_profile_action_task_select_target));
        spinner.setAdapter(adapter);
        adapter.clear();
        adapter.add(PROFILE_ACTION_TYPE_WAIT_TIMEOUT_UNITS_MIN);
        adapter.add(PROFILE_ACTION_TYPE_WAIT_TIMEOUT_UNITS_SEC);
//        adapter.add(PROFILE_ACTION_TYPE_MESSAGE_WITHOUT_TEXT);
        for (int i=0;i<adapter.getCount();i++) 
        	if (adapter.getItem(i).equals(selected)) {
        		spinner.setSelection(i);
        		break;
        	}
	};

	final static public void putProfileListToService(
			final GlobalParameters mGlblParms,final AdapterProfileList pfla, 
			final boolean rebuild_req) {
//		final Dialog dialog=CommonDialog.showProgressSpinDlg(context, "TaskAutomation");
		Thread th=new Thread() {
			@Override
			public void run() {
				byte[] buf=null; 
			    try { 
			    	ArrayList<ProfileListItem> pli=pfla.getDataList();
			    	buf=ProfileUtilities.serializeProfilelist(pli);
			    	mGlblParms.svcServer.aidlCopyProfileToService(buf);
			    	if (rebuild_req) mGlblParms.util.reBuildTaskExecList();
			    	
			    } catch (RemoteException e) {
			    	Log.v(APPLICATION_TAG, "putProfileListToService error", e);
				}
//			    dialog.dismiss();
			}
		};
		th.start();
	};

	final static public ArrayList<ProfileListItem> getProfileListFromService(final GlobalParameters mGlblParms) {
//		long b_time=System.currentTimeMillis();
		ArrayList<ProfileListItem> prof_list=null;
		byte[]buf=null;
		try {
			buf=mGlblParms.svcServer.aidlCopyProfileFromService();
			prof_list=ProfileUtilities.deSerializeProfilelist(buf);
		} catch (RemoteException e) {
			Log.v(APPLICATION_TAG, "getProfileListFromService error", e);
		}
//		Log.v("","External elapsed="+(System.currentTimeMillis()-b_time));
		return prof_list;
		
	};
	
	final static private  AdapterProfileList loadProfileAdapterFromFile(
			final GlobalParameters mGlblParms,boolean sdcard, String fp) {
		AdapterProfileList pfl;
		
		ArrayList<ProfileListItem> task = new ArrayList<ProfileListItem>();
		ArrayList<ProfileListItem> time = new ArrayList<ProfileListItem>();
		ArrayList<ProfileListItem> action = new ArrayList<ProfileListItem>();
		
		mGlblParms.importedSettingParmList.clear();

		String file_path="";
		if (sdcard) file_path=fp;
		else file_path=mGlblParms.context.getFilesDir().toString()+"/"+PROFILE_FILE_NAME;
		
		File sf = new File(file_path);
		if (sf.exists()) {
			try {
				BufferedReader br;
				br = new BufferedReader(new FileReader(file_path),8192);
				String pl;
				while ((pl = br.readLine()) != null) {
					ProfileUtilities.parseProfileList(pl, "",task,time,action);
					addImportSettingsParm(mGlblParms,pl);
				}
				br.close();
			} catch (FileNotFoundException e) { 
				e.printStackTrace();
				mGlblParms.util.addLogMsg("E",String.format(
						mGlblParms.context.getString(R.string.msgs_create_profile_error),file_path));
				mGlblParms.util.addLogMsg("E",e.toString());
			} catch (IOException e) {
				e.printStackTrace();
				mGlblParms.util.addLogMsg("E",String.format(
						mGlblParms.context.getString(R.string.msgs_create_profile_error),file_path));
				mGlblParms.util.addLogMsg("E",e.toString());
			}
		} else {
			if (sdcard) {
				mGlblParms.util.addLogMsg("E",String.format(
						mGlblParms.context.getString(R.string.msgs_create_profile_error),file_path));
				mGlblParms.util.addDebugMsg(1, "W", 
						"Profile file does not exists, empty profile list created. fn=",
								mGlblParms.context.getFilesDir().toString()+"/",file_path);
			}
		}

		task.addAll(action);
		task.addAll(time);
		ProfileUtilities.sortProfileArrayList(mGlblParms.util,task);
		pfl = new AdapterProfileList(mGlblParms.context, R.layout.task_profile_list_view_item, task);
		mGlblParms.util.addDebugMsg(2, "I", 
				"createProfileList DataItemCount=",String.valueOf(pfl.getProfItemCount()),
				", showCount=", String.valueOf(pfl.getProfItemCount()));

		return pfl;
	};

	final static private  void addImportSettingsParm(final GlobalParameters mGlblParms,String pl) {
		String tmp_ps=pl;//.substring(7,pl.length());
		String[] tmp_pl=tmp_ps.split("\t");// {"type","name","active",options...};
		String[] parm= new String[90];
		for (int i=0;i<30;i++) parm[i]="";
		for (int i=0;i<tmp_pl.length;i++) {
			if (tmp_pl[i]==null) parm[i]="";
			else {
				if (tmp_pl[i]==null) parm[i]="";
				else parm[i]=tmp_pl[i];
			}
		}
		if (parm[0].equals(PROFILE_TYPE_SETTINGS)) {
			int newkey=mGlblParms.importedSettingParmList.size();
			String[] val = new String[]{parm[1],parm[2],parm[3]};
			mGlblParms.importedSettingParmList.put(newkey, val);
		}
	};
}

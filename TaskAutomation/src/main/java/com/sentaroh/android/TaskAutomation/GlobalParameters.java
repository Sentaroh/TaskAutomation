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

import java.util.ArrayList;

import com.sentaroh.android.TaskAutomation.Common.EnvironmentParms;
import com.sentaroh.android.TaskAutomation.Config.AdapterActivityExtraDataEditList;
import com.sentaroh.android.TaskAutomation.Config.AdapterDataArrayEditList;
import com.sentaroh.android.TaskAutomation.Config.AdapterProfileGroupList;
import com.sentaroh.android.TaskAutomation.Config.AdapterProfileList;
import com.sentaroh.android.TaskAutomation.Config.AdapterTaskActionEditList;
import com.sentaroh.android.TaskAutomation.Config.RingtoneListItem;
import com.sentaroh.android.Utilities.ThemeColorList;
import com.sentaroh.android.Utilities.ThreadCtrl;
import com.sentaroh.android.Utilities.ContextMenu.CustomContextMenu;
import com.sentaroh.android.Utilities.Dialog.CommonDialog;
import com.sentaroh.android.Utilities.Widget.CustomSpinnerAdapter;

import android.app.Application;
import android.content.Context;
import android.media.MediaPlayer;
import android.support.v4.app.FragmentManager;
import android.util.SparseArray;
import android.widget.ListView;
import android.widget.Spinner;

public class GlobalParameters extends Application{
	
	public boolean initialyzeRequired=true;

	public FragmentManager frgamentMgr=null;
	
	public EnvironmentParms envParms=null;
	
	public AdapterProfileList profileAdapter=null;
	public ListView profileListView=null;
	public Spinner spinnerProfileGroupSelector=null;
	public CustomSpinnerAdapter adapterProfileGroupSelector=null;
	public Spinner spinnerProfileFilterSelector=null;
	public CustomSpinnerAdapter adapterProfileFilterSelector=null;
	public ListView profileGroupListView=null;
	public AdapterProfileGroupList profileGroupAdapter=null;

	public ArrayList<String> androidApplicationList=null;
	public CommonUtilities util=null;
	public Context context=null;
	public CustomContextMenu ccMenu = null;
	public CommonDialog commonDlg=null;

	public ThemeColorList themeColorList=null;
	
	//
	public SparseArray<String[]> importedSettingParmList=new SparseArray<String[]>();
	public String localRootDir;
	public MediaPlayer mpMusic=null, mpRingtone=null;
	public ThreadCtrl tcMusic=null, tcRingtone=null;
	
	public ArrayList<RingtoneListItem> ringtoneList=null;
	
	public ISchedulerClient svcServer=null;
	public ISchedulerCallback consoleCallbackListener=null;

	public EnvironmentParms immTaskTestEnvParms=null;

	public String currentSelectedExtraDataType="";
	public boolean ringTonePlayBackEnabled=false;
	public Thread ringtonePlayBackThread=null;

	//ActionProfile
	public AdapterActivityExtraDataEditList activityExtraDataEditListAdapter=null;
	public AdapterDataArrayEditList actionCompareDataAdapter=null;
	
	//TaskProfile
	public AdapterTaskActionEditList taskActionListAdapter=null;
	
	public GlobalParameters() {};
	
//	@Override
//	public void  onCreate() {
//		super.onCreate();
//		Log.v("GlobalParms","onCreate entered");
//	};
	
	public void clearParms() {
		frgamentMgr=null;
		
		envParms=null;
		
		profileAdapter=null;
		profileListView=null;
		spinnerProfileGroupSelector=null;
		adapterProfileGroupSelector=null;
		spinnerProfileFilterSelector=null;
		adapterProfileFilterSelector=null;
		profileGroupListView=null;
		profileGroupAdapter=null;

		androidApplicationList=null;
		util=null;
		context=null;
		ccMenu = null;
		commonDlg=null;

		//
		importedSettingParmList=new SparseArray<String[]>();
		localRootDir=null;
		mpMusic=mpRingtone=null;
		tcMusic=tcRingtone=null;
		
		ringtoneList=null;
		
		svcServer=null;
		consoleCallbackListener=null;

		immTaskTestEnvParms=null;

		currentSelectedExtraDataType="";
		ringTonePlayBackEnabled=false;
		ringtonePlayBackThread=null;

		//ActionProfile
		activityExtraDataEditListAdapter=null;
		actionCompareDataAdapter=null;
		
		//TaskProfile
		taskActionListAdapter=null;

	}
}

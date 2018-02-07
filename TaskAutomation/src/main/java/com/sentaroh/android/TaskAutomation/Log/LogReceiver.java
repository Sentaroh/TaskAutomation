package com.sentaroh.android.TaskAutomation.Log;

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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import com.sentaroh.android.TaskAutomation.CommonUtilities;
import com.sentaroh.android.TaskAutomation.R;
import com.sentaroh.android.TaskAutomation.Common.EnvironmentParms;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

public class LogReceiver extends BroadcastReceiver{

	private final static String LOG_TAG="TaskAutomation";
	private static final int LOG_LIMIT_SIZE=1024*1024*2;
	private static PrintWriter printWriter=null;
//	private static BufferedWriter bufferedWriter;
	private static FileWriter fileWriter ;	
	private static String log_dir=null;
	private static int debug_level=0;
	private static boolean log_option=false;
	private static boolean shutdown_received=false;
	private static File logFile=null;
	private static boolean mediaUsable=false;
	private static final SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.getDefault());
	
	private static String log_id="";
	
	@Override
	final public void onReceive(Context c, Intent in) {
//		StrictMode.allowThreadDiskWrites();
//		StrictMode.allowThreadDiskReads();
		if (!mediaUsable) {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				mediaUsable=true;
			}
		} 
		if (log_dir==null) {
			setLogId("LogReceiver");
			initParms(c);
			if (debug_level>0) {
				String line="initialized dir="+log_dir+", debug="+debug_level;
				Log.v(LOG_TAG,"I "+log_id+line);
				putLogMsg(c,"M I "+sdfDateTime.format(System.currentTimeMillis())+" "+log_id+line);
			}
		}
//		Log.v("","media="+mediaUsable);
		if (in.getAction().equals(Intent.ACTION_MEDIA_EJECT)) {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED)) {
				mediaUsable=false;
				closeLogFile();
			}
		} else if (in.getAction().equals(Intent.ACTION_SHUTDOWN)) {
			shutdown_received=true;
			if (printWriter!=null) printWriter.flush();
		} else if (in.getAction().equals(BROADCAST_LOG_SEND)) {
				String line=in.getExtras().getString("LOG");
				putLogMsg(c,line);
		} else if (in.getAction().equals(BROADCAST_LOG_RESET)) {
				initParms(c);
				closeLogFile();
				if (log_option) openLogFile(c);
				if (debug_level>0) {
					String line="re-initialized dir="+log_dir+", debug="+debug_level;
					Log.v(LOG_TAG,"I "+log_id+line);
					putLogMsg(c,"M I "+sdfDateTime.format(System.currentTimeMillis())+" "+log_id+line);
				}
		} else if (in.getAction().equals(BROADCAST_LOG_DELETE)) {
			closeLogFile();
			logFile.delete();
			if (log_option) openLogFile(c);
		} else if (in.getAction().equals(BROADCAST_LOG_ROTATE)) {
			rotateLogFileForce(c);
		} else if (in.getAction().equals(BROADCAST_LOG_FLUSH)) {
			if (printWriter!=null) printWriter.flush();
		}
//		StrictMode.enableDefaults();
	};

	final static private void setLogId(String li) {
		log_id=(li+"                 ").substring(0,16)+" ";
	};

	final static private void putLogMsg(Context c,String msg) {
//		Log.v("","log_option="+log_option+", mu="+mediaUsable+", pw="+printWriter);
		if (log_option && mediaUsable) {
			rotateLogFileConditional(c);
			if (printWriter==null) {
				openLogFile(c);
				if (printWriter!=null) {
					printWriter.println(msg);
					if (shutdown_received) printWriter.flush();
				}
			} else {
				printWriter.println(msg);
				if (shutdown_received) printWriter.flush();
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("InlinedApi")
	final static private void initParms(Context context) {
		SharedPreferences prefsMgr=
				context.getSharedPreferences(DEFAULT_PREFS_FILENAME,
        		Context.MODE_PRIVATE|Context.MODE_MULTI_PROCESS);

		log_dir=prefsMgr.getString(context.getString(R.string.settings_main_log_dir),"");
		debug_level=Integer.parseInt(
				prefsMgr.getString(context.getString(R.string.settings_main_log_level),"0"));
		log_option=
				prefsMgr.getBoolean(context.getString(R.string.settings_main_log_option),false);
		logFile=new File(log_dir+LOG_FILE_NAME+".txt");
	};
	
	final static private void rotateLogFileConditional(Context c) {
		if (printWriter!=null && mediaUsable && logFile.length()>=LOG_LIMIT_SIZE) {
			rotateLogFileForce(c);
		}
	};

	@SuppressLint("SimpleDateFormat")
	final static private void rotateLogFileForce(Context c) {
		if (printWriter!=null && mediaUsable) {
			printWriter.flush();
			closeLogFile();
			SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS");
			File lf=new File(log_dir+LOG_FILE_NAME+"_"+sdf.format(System.currentTimeMillis())+".txt");
			logFile.renameTo(lf);
			openLogFile(c);
			logFile=new File(log_dir+LOG_FILE_NAME+".txt");
			if (debug_level>0) {
				String line="Logfile was rotated "+log_dir+LOG_FILE_NAME+sdf.format(System.currentTimeMillis())+".txt";
				Log.v(LOG_TAG,"I "+log_id+line);
				putLogMsg(c,"M I "+sdfDateTime.format(System.currentTimeMillis())+" "+log_id+line);
			}
		} else if (printWriter==null && mediaUsable) {
			File tlf=new File(log_dir+LOG_FILE_NAME+".txt");
			if (tlf.exists()) {
				SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS");
				File lf=new File(log_dir+LOG_FILE_NAME+"_"+sdf.format(System.currentTimeMillis())+".txt");
				tlf.renameTo(lf);
			}
		}
	};

	
	final static private void closeLogFile() {
		if (printWriter!=null && mediaUsable) {
			printWriter.flush();
			printWriter.close(); 
			try {
//				bufferedWriter.close();
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			printWriter=null;
		}
	};
	
	final static private void openLogFile(Context c) { 
		if (printWriter==null && mediaUsable) {
			BufferedWriter bw=null;
			try {
				File lf=new File(log_dir);
				if (!lf.exists()) lf.mkdirs();
				fileWriter=new FileWriter(log_dir+LOG_FILE_NAME+".txt",true);
				bw=new BufferedWriter(fileWriter,LOG_FILE_BUFFER_SIZE);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (bw!=null) {
				printWriter=new PrintWriter(bw,false);
				houseKeepLogFile(c);
			} else {
				log_option=false;
			}
		}
	};
	
	final static private void houseKeepLogFile(Context c) {
		EnvironmentParms envParms=new EnvironmentParms();
		envParms.loadSettingParms(c);
		ArrayList<LogFileManagemntListItem> lfml=CommonUtilities.createLogFileList(envParms);
		Collections.sort(lfml,new Comparator<LogFileManagemntListItem>(){
			@Override
			public int compare(LogFileManagemntListItem arg0,
					LogFileManagemntListItem arg1) {
				int result=0;
				long comp=arg0.log_file_last_modified-arg1.log_file_last_modified;
				if (comp==0) result=0;
				else if(comp<0) result=-1;
				else if(comp>0) result=1;
				return result;
			}
		});
		
		int l_epos=lfml.size()-(envParms.settingLogMaxFileCount+1);
		if (l_epos>0) {
			for (int i=0;i<l_epos;i++) {
				String line="Logfile was deleted "+lfml.get(0).log_file_path;
				Log.v(LOG_TAG,"I "+log_id+line);
				putLogMsg(c,"M I "+sdfDateTime.format(System.currentTimeMillis())+" "+log_id+line);
				File lf=new File(lfml.get(0).log_file_path);
				lf.delete();
				lfml.remove(0);
			}
			
		}
	};
}

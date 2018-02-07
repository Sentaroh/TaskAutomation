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

import com.sentaroh.android.TaskAutomation.Common.EnvironmentParms;
import com.sentaroh.android.Utilities.NotifyEvent;
import com.sentaroh.android.Utilities.ThemeColorList;
import com.sentaroh.android.Utilities.ThemeUtil;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class ActivityRestartScheduler extends Activity {
	private boolean isTerminateApplication=false;
	
	private static int restartStatus=0;

	private CommonUtilities util;
	private EnvironmentParms envParms=null;
	
	private Context context;
	
	private ServiceConnection svcConnScheduler=null;
	private ISchedulerClient svcServer=null;
	private Handler uiHandler=null;
	
	private ThemeColorList mThemeColorList=null;
	
	@Override  
	protected void onSaveInstanceState(Bundle outState) {  
		super.onSaveInstanceState(outState);
		util.addDebugMsg(1,"I","onSaveInstanceState entered");
	};  
	  
	@Override  
	protected void onRestoreInstanceState(Bundle savedState) {  
		super.onRestoreInstanceState(savedState);
		util.addDebugMsg(1,"I","onRestoreInstanceState entered");
		restartStatus=2;
	};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_message);
        
        context=this;
        mThemeColorList=ThemeUtil.getThemeColorList(this); 
        uiHandler=new Handler();
        
        restartStatus=0;
        envParms=new EnvironmentParms();
        envParms.loadSettingParms(context);
        util=new CommonUtilities(context.getApplicationContext(), "RestartSched",envParms);

        util.addDebugMsg(1,"I","onCreate entered");
        
		FrameLayout fl_main=(FrameLayout)findViewById(R.id.activity_message_fl_main);
		fl_main.setBackgroundColor(mThemeColorList.window_background_color_content);

//		if (util.isKeyguardEffective()) {
//		}
		Window win = getWindow();
		win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER |
			WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
//			WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
			WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
		);


    };
    
	@Override
	public void onStart() {
		super.onStart();
		util.addDebugMsg(1,"I","onStart entered");
	};

	@Override
	public void onRestart() {
		super.onStart();
		util.addDebugMsg(1,"I","onRestart entered");
	};
	
	@Override
	public void onResume() {
		super.onResume();
		
		util.addDebugMsg(1,"I","onResume entered, restartStatus="+restartStatus);

		if (restartStatus==0) {
			showMessage();
		} else if (restartStatus==1) {
		} else if (restartStatus==2) {
			finish();
		}
		restartStatus=1;
		startSvcSchduler(null);
	};
	
	@Override
	public void onPause() {
		super.onPause();
		util.addDebugMsg(1,"I","onPause entered");
	};

	@SuppressWarnings("deprecation")
	@Override
	public void onStop() {
		super.onStop();
		util.addDebugMsg(1,"I","onStop entered");

		if (!isTerminateApplication) {
			try {
				if (Build.VERSION.SDK_INT<19) {
					if (svcServer!=null) svcServer.aidlMessageDialogMoveToFront();
				} else if (Build.VERSION.SDK_INT==19) {
					PowerManager pm=((PowerManager)getSystemService(Context.POWER_SERVICE)); 
					if (pm.isScreenOn()) {
						if (svcServer!=null) svcServer.aidlMessageDialogMoveToFront();
					}
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		util.addDebugMsg(1,"I","onDestroy entered");
		stopSvcSchduler();
		
		util=null;
		envParms=null;
		context=null;
	};
	
	@Override
	public void onConfigurationChanged(final Configuration newConfig) {
	    // Ignore orientation change to keep activity from restarting
	    super.onConfigurationChanged(newConfig);
	    util.addDebugMsg(1,"I","onConfigurationChanged Entered");
	};
	
	final public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
//			isTerminateApplication=true;
//			finish();
			return true;
			// break;
		default:
			return super.onKeyDown(keyCode, event);
			// break;
		}
	};

	private void showMessage() {
		TextView title = 
				(TextView) findViewById(R.id.activity_message_title);
		title.setText(getString(R.string.app_name));
		
		TextView subtitle = 
				(TextView) findViewById(R.id.activity_message_subtitle);
		subtitle.setGravity(Gravity.CENTER_HORIZONTAL);
		subtitle.setText(getString(R.string.msgs_restart_dialog_dlg_subtitle_waiting));

		final Button btnStop = (Button) findViewById(R.id.activity_message_stop_btn);
		btnStop.setText(getString(R.string.msgs_restart_dialog_dlg_stop));
		Button btnClose = (Button) findViewById(R.id.activity_message_close_btn);
		btnClose.setText(getString(R.string.msgs_restart_dialog_dlg_close));
		
		btnStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					svcServer.aidlCancelAllActiveTask();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				btnStop.setEnabled(false);
			}
		});
		btnClose.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				isTerminateApplication=true;
				finish();
			}
		});
	};

	
	private boolean restartIssued=false;
	private void startSvcSchduler(final NotifyEvent p_ntfy) {
		util.addDebugMsg(1,"I", "startSvcSchduler entered");
		
        svcConnScheduler = new ServiceConnection(){
			public void onServiceConnected(ComponentName name, IBinder service) {
				util.addDebugMsg(1, "I", "Callback onServiceConnected entered");
    			svcServer = ISchedulerClient.Stub.asInterface(service);
    			final Handler hndl=new Handler();
    			if (!restartIssued) {
					final TextView subtitle =(TextView) findViewById(R.id.activity_message_subtitle);
					try{
						svcServer.setCallBack(svcClientCallback);
					} catch (RemoteException e){
						e.printStackTrace();
						util.addLogMsg("E", "setCallbackListener error :"+e.toString());
					}

					if (isActiveTaskExisted()) {
	    				hndl.post(new Runnable(){
							@Override
							public void run() {
								subtitle.setText(getString(R.string.msgs_restart_dialog_dlg_subtitle_waiting));
							}
	    				});
					} else {
						issueRestartRequest(hndl);
					}

    			} else {
    				isTerminateApplication=true;
    				finish();
    			}
    		}
    		public void onServiceDisconnected(ComponentName name) {
				util.addDebugMsg(1, "I", "Callback onServiceDisconnected entered");
    			svcServer = null;
    			Intent intent = new Intent(context, SchedulerService.class);
    			intent.setAction("RestartSched");
    			startService(intent);
    		}
    	};
		Intent intent = new Intent(context, SchedulerService.class);
		intent.setAction("Main");
		bindService(intent, svcConnScheduler, BIND_AUTO_CREATE);
	};
	
	private ISchedulerCallback svcClientCallback = new ISchedulerCallback.Stub() {
    	final public void notifyToClient(String resp_time, final String resp,
				final String grp,final String task,
				final String action, final String shell_cmd, final String dialog_id, final int atc,
				final int resp_cd, final String msg) throws RemoteException {
			if (envParms.settingDebugLevel>=1)
				util.addDebugMsg(2, "I", "Notify received ",
						"Resp=",resp,", Task=",task,", action=",action,", " +
								"dialog_id=",dialog_id);
			if (atc==0) issueRestartRequest(uiHandler);
		}
    };
			
	private void issueRestartRequest(Handler hndl) {
		final Button btnClose = (Button) findViewById(R.id.activity_message_close_btn);
		final TextView subtitle = 
				(TextView) findViewById(R.id.activity_message_subtitle);
		final Button btnStop = (Button) findViewById(R.id.activity_message_stop_btn);
		hndl.post(new Runnable(){
			@Override
			public void run() {
				subtitle.setText(getString(R.string.msgs_restart_dialog_dlg_subtitle_restarting));
				btnStop.setEnabled(false);
				btnClose.setEnabled(false);
			}
		});
		restartIssued=true;
		util.restartScheduler();
	}
	
	private boolean isActiveTaskExisted() {
		String[] atl=null;
		try {
			atl=svcServer.aidlGetActiveTaskList();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		if (atl!=null && atl.length!=0) return true;
		else return false;
	};
	
	private void stopSvcSchduler() { 
		util.addDebugMsg(1, "I", "stopSvcSchduler entered");
		if (svcClientCallback!=null) {
			try {
				svcServer.removeCallBack(svcClientCallback);
				svcClientCallback=null;
			} catch (RemoteException e) {
				e.printStackTrace();
				util.addLogMsg("E", "removeListener error :"+e.toString());
			}
		}
		unbindService(svcConnScheduler);
	};
}

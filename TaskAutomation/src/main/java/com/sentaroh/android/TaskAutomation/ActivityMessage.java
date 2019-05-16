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

import com.sentaroh.android.Utilities.NotifyEvent;
import com.sentaroh.android.Utilities.NotifyEvent.NotifyEventListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class ActivityMessage extends Activity {
	private static int restartStatus=0;

	private boolean isTerminateApplication=false;
	
	private CommonUtilities util;
	private EnvironmentParms envParms=null;
	
	private Context context;

	private ISchedulerCallback svcClientCallback=null;
	private ServiceConnection svcConnScheduler=null;
	private ISchedulerClient svcServer=null;
	
	private String msg_group_name=null, msg_task_name=null, msg_action_name=null;
	private String msg_dialog_id=null;
	private String msg_type=null, msg_text=null;

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

    @SuppressLint("ResourceAsColor")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_message);
		
        context=this;
        
        restartStatus=0;
        envParms=new EnvironmentParms();
        envParms.loadSettingParms(context);
        GlobalParameters mGp= GlobalWorkArea.getGlobalParameters(context);
        util=new CommonUtilities(context.getApplicationContext(), "MessageDlg",envParms, mGp);

        util.addDebugMsg(1,"I","onCreate entered");

//		if (util.isKeyguardEffective()) {
//		};
		Window win = getWindow();
		win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER |
			WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED 
//			| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD 
			| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON 
		);

//		LinearLayout ll_main=(LinearLayout)findViewById(R.id.activity_message_ll_main);
		FrameLayout fl_main=(FrameLayout)findViewById(R.id.activity_message_fl_main);
		TextView tv_spacer_bot=(TextView)findViewById(R.id.activity_message_spacer_bottom);
		fl_main.setBackgroundColor(Color.argb(255, 50, 50, 50));
		tv_spacer_bot.setBackgroundColor(Color.argb(150, 0, 0, 0));
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

		final Intent in=getIntent();
		if ((in!=null) && 
				(in!=null && in.getStringExtra(MESSAGE_DIALOG_MESSAGE_KEY_TYPE)!=null)) {
			NotifyEvent ntfy = new NotifyEvent(this);
			ntfy.setListener(new NotifyEventListener() {
				@Override
				public void positiveResponse(Context c, Object[] o) {
					if (restartStatus==0) {
						readMsgParms(in);
					} else if (restartStatus==1) {
					} else if (restartStatus==2) {
						readMsgParms(in);
					}
					restartStatus=1;
					
					util.addDebugMsg(1, "I", "msg_type="+msg_type+", msg_group="+msg_group_name+
							", msg_task="+msg_task_name+", msg_action="+msg_action_name+
							", msg_dlg_id="+msg_dialog_id+", msg_text="+msg_text
							);

					if (msg_type.equals(MESSAGE_DIALOG_MESSAGE_TYPE_DIALOG)) showMsgTypeMessage();
					else showMsgTypeSound();
				}
				@Override
				public void negativeResponse(Context c, Object[] o) {}
			});
			startSvcSchduler(ntfy);
		} else {
			finish();
		}

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
//		if (msg_type.equals(MESSAGE_DIALOG_MESSAGE_TYPE_SOUND)) 
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

	private void readMsgParms(Intent in) {
		msg_type=in.getStringExtra(MESSAGE_DIALOG_MESSAGE_KEY_TYPE);
		msg_text=in.getStringExtra(MESSAGE_DIALOG_MESSAGE_KEY_TEXT);
		msg_group_name=in.getStringExtra(MESSAGE_DIALOG_MESSAGE_KEY_GROUP);
		msg_task_name=in.getStringExtra(MESSAGE_DIALOG_MESSAGE_KEY_TASK);
		msg_action_name=in.getStringExtra(MESSAGE_DIALOG_MESSAGE_KEY_ACTION);
		msg_dialog_id=in.getStringExtra(MESSAGE_DIALOG_MESSAGE_KEY_DIALOG_ID);
	};
		
	private void showMsgTypeMessage() {
		TextView title = (TextView)findViewById(R.id.activity_message_title);
		title.setText(getString(R.string.app_name));
		TextView subtitle =(TextView)findViewById(R.id.activity_message_subtitle);
		TextView msg =(TextView)findViewById(R.id.activity_message_msg);
		Button btnStop = (Button)findViewById(R.id.activity_message_stop_btn);
		final Button btnClose = (Button)findViewById(R.id.activity_message_close_btn);

		subtitle.setText(msg_group_name+", "+msg_task_name+", "+msg_action_name); 
		
		btnStop.setVisibility(Button.GONE);
		msg.setText(msg_text);

		// Closeボタンの指定
		btnClose.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				isTerminateApplication=true;
				finish();
			}
		});
	};
	
	private void showMsgTypeSound() {
		TextView title = (TextView)findViewById(R.id.activity_message_title);
		title.setText(getString(R.string.app_name));
		TextView subtitle =(TextView)findViewById(R.id.activity_message_subtitle);
		TextView msg =(TextView)findViewById(R.id.activity_message_msg);
		Button btnStop = (Button)findViewById(R.id.activity_message_stop_btn);
		final Button btnClose = (Button)findViewById(R.id.activity_message_close_btn);
		btnClose.setVisibility(Button.GONE);
		
		subtitle.setText(msg_group_name+", "+msg_task_name+", "+msg_action_name); 
		
		msg.setText(getString(R.string.msgs_sound_dialog_dlg_subtitle)+" : "+msg_text);

		// Stopボタンの指定
		btnStop.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					if (svcServer!=null)
					svcServer.aidlCancelSpecificTask(msg_group_name, msg_task_name);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
//				isTerminateApplication=true;
//				dialog.dismiss();
//				finish();
			}
		});

		// Closeボタンの指定
//		btnClose.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				isTerminateApplication=true;
//				finish();
//			}
//		});
	}
	
	Handler handler = new Handler();
	private void setCallbackListener() {
		util.addDebugMsg(1, "I", "setCallbackListener entered");
        svcClientCallback = new ISchedulerCallback.Stub() {
			public void notifyToClient(String resp_time, final String resp, 
					final String grp,final String task, final String action, final String shell_cmd,
					final String dialog_id, final int atc, final int resp_cd, final String msg) 
							throws RemoteException {
				util.addDebugMsg(2, "I", "Notify received "+
						"Resp="+resp+", Group="+grp+", Task="+task+", action="+action+", dialog_id="+dialog_id);
				if (resp.equals(NTFY_TO_ACTV_CLOSE_DIALOG)) {
					if (grp.equals(msg_group_name) && task.equals(msg_task_name) &&
							action.equals(msg_action_name) && dialog_id.equals(msg_dialog_id)) {
						handler.post(new Runnable() {
							@Override
		                    public void run() {
								isTerminateApplication=true;
//								if (msg_type.equals(MESSAGE_DIALOG_MESSAGE_TYPE_DIALOG)) dismissDialog(dlg_type_msg);
//								else dismissDialog(dlg_type_sound);
								finish();
							}
						});
					}
				}
			}
        };
		try{
			svcServer.setCallBack(svcClientCallback);
		} catch (RemoteException e){
			e.printStackTrace();
			util.addLogMsg("E", "setCallbackListener error :"+e.toString());
		}
	};
	
	private void startSvcSchduler(final NotifyEvent p_ntfy) {
		if (svcServer != null) {
//			p_ntfy.notifyToListener(true, null);
			return;
		}
		util.addDebugMsg(1,"I", "startSvcSchduler entered");
		
        svcConnScheduler = new ServiceConnection(){
    		public void onServiceConnected(ComponentName name, IBinder service) {
				util.addDebugMsg(1, "I", "startSvcSchduler onServiceConnected entered");
    			svcServer = ISchedulerClient.Stub.asInterface(service);
    			setCallbackListener();
    			p_ntfy.notifyToListener(true, null);
    		}
    		public void onServiceDisconnected(ComponentName name) {
				util.addDebugMsg(1, "I", "startSvcSchduler onServiceDisconnected entered");
    			svcServer = null;
    		}
    	};
		Intent intent = new Intent(context, SchedulerService.class);
		intent.setAction("Main");
		bindService(intent, svcConnScheduler, BIND_AUTO_CREATE);
	};
	
	private void stopSvcSchduler() { 
		util.addDebugMsg(1, "I", "stopSvcSchduler entered");
		if (svcClientCallback!=null) {
			try {
				if (svcServer!=null) svcServer.removeCallBack(svcClientCallback);
				svcClientCallback=null;
			} catch (RemoteException e) {
				e.printStackTrace();
				util.addLogMsg("E", "removeListener error :"+e.toString());
			}
		}
		unbindService(svcConnScheduler);
	};
}

package com.sentaroh.android.TaskAutomation;

import static com.sentaroh.android.TaskAutomation.CommonConstants.APPLICATION_TAG;
import static com.sentaroh.android.TaskAutomation.Log.LogConstants.BROADCAST_LOG_SEND;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import com.sentaroh.android.Utilities.StringUtil;

public class ActivityNfcReceiver extends Activity {
	private boolean isTerminateApplication=false;
	
	private static final boolean DEBUG_ENABLED=false;
	
	private static int restartStatus=0;

	private String log_id="NfcReceiver      ";
	private StringBuilder print_msg=new StringBuilder();
	private StringBuilder log_msg=new StringBuilder(512);
	
	private void addDebugMsg(int lvl, String cat, String... msg) {
		if (DEBUG_ENABLED) {
			print_msg.setLength(0);
			print_msg.append("D ");
			print_msg.append(cat);
			log_msg.setLength(0);
			for (int i=0;i<msg.length;i++) log_msg.append(msg[i]);
			
			Intent intent = new Intent(BROADCAST_LOG_SEND);
			print_msg.append(" ")
			.append(StringUtil.convDateTimeTo_YearMonthDayHourMinSecMili(System.currentTimeMillis()))
			.append(" ")
			.append(log_id)
			.append(log_msg.toString());
			intent.putExtra("LOG", print_msg.toString());
			sendOrderedBroadcast(intent,null);
			
			Log.v(APPLICATION_TAG,cat+" "+log_id+log_msg.toString());
		}
	}
	
	@Override  
	protected void onSaveInstanceState(Bundle outState) {  
		super.onSaveInstanceState(outState);
		addDebugMsg(1,"I","onSaveInstanceState entered");
	};  
	  
	@Override  
	protected void onRestoreInstanceState(Bundle savedState) {  
		super.onRestoreInstanceState(savedState);
		addDebugMsg(1,"I","onRestoreInstanceState entered");
		restartStatus=2;
	};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.activity_message);
        
        new Handler();
        
        restartStatus=0;

        addDebugMsg(1,"I","onCreate entered");
        
        Intent in=getIntent();
        if (in!=null) {
        	processNfcIntent(in);
        } else addDebugMsg(1,"I","Intent was not specififed");
        
    };
    
    private void processNfcIntent(Intent in) {
    	addDebugMsg(1,"I","NFC Intent Action="+in.getAction());
    	Tag tag = (Tag) in.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    	if (tag != null) {
    		Intent n_svc=new Intent(this,SchedulerService.class);
    		n_svc.setAction(in.getAction());
    		n_svc.putExtra(NfcAdapter.EXTRA_TAG, tag);
    		startService(n_svc);
    	    byte[] idm2 = tag.getId();
    	    String idm2_text=StringUtil.getHexString(idm2, 0, idm2.length);
    	    addDebugMsg(1,"I","idm2=",idm2_text);
    	    String[] tl=tag.getTechList();
    	    for(int i=0;i<tl.length;i++) 
    	    	addDebugMsg(1,"I","Tech_list["+i+"]="+tl[i]);
    	}
    }
    
	@Override
	public void onStart() {
		super.onStart();
		addDebugMsg(1,"I","onStart entered");
	};

	@Override
	public void onRestart() {
		super.onStart();
		addDebugMsg(1,"I","onRestart entered");
	};
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		addDebugMsg(1,"I","onNewIntent entered, "+"resartStatus="+restartStatus);
        Intent in=getIntent();
        if (in!=null) {
        	processNfcIntent(in);
        } else addDebugMsg(1,"I","Intent was not specififed");
	};
	
	@Override
	public void onResume() {
		super.onResume();
		
		addDebugMsg(1,"I","onResume entered, restartStatus="+restartStatus);

//		if (restartStatus==0) {
//		} else if (restartStatus==1) {
//		} else if (restartStatus==2) {
//			finish();
//		}
		finish();
		restartStatus=1;
	};
	
	@Override
	public void onPause() {
		super.onPause();
		addDebugMsg(1,"I","onPause entered");
	};

	@Override
	public void onStop() {
		super.onStop();
		addDebugMsg(1,"I","onStop entered");

		if (!isTerminateApplication) {
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		addDebugMsg(1,"I","onDestroy entered");
	};
	
	@Override
	public void onConfigurationChanged(final Configuration newConfig) {
	    // Ignore orientation change to keep activity from restarting
	    super.onConfigurationChanged(newConfig);
	    addDebugMsg(1,"I","onConfigurationChanged Entered");
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

}

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

import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.NTFY_TO_SVC_TASK_STARTED;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.sentaroh.android.TaskAutomation.Common.EnvironmentParms;
import com.sentaroh.android.TaskAutomation.Common.TaskHistoryItem;
import com.sentaroh.android.TaskAutomation.Common.TaskResponse;
import com.sentaroh.android.Utilities.NotifyEvent;
import com.sentaroh.android.Utilities.NotifyEvent.NotifyEventListener;
import com.sentaroh.android.Utilities.ContextMenu.CustomContextMenu;
import com.sentaroh.android.Utilities.ContextMenu.CustomContextMenuItem.CustomContextMenuOnClickListener;
import com.sentaroh.android.Utilities.Dialog.CommonDialog;
import com.sentaroh.android.Utilities.Widget.CustomViewPager;
import com.sentaroh.android.Utilities.Widget.CustomViewPagerAdapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.ListView;
import android.widget.TextView;

public class ActivityTaskStatus extends FragmentActivity {
	private boolean isTerminateApplication=true;
	
	private static int restartStatus=0;

	private CommonUtilities util;
	private EnvironmentParms envParms=null;
	
	private Context context;
	
	private ISchedulerCallback svcClientCallback=null;
	private ServiceConnection svcConnScheduler=null;
	private ISchedulerClient svcServer=null;
	
	private CustomContextMenu ccMenu;
	
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
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.activity_transrucent);
        context=this;

        restartStatus=0;
        envParms=new EnvironmentParms();
        envParms.loadSettingParms(context);
        util=new CommonUtilities(context.getApplicationContext(), "TaskStatus", envParms);
		
        util.addDebugMsg(1,"I","onCreate entered");
        
        if (ccMenu ==null) ccMenu = new CustomContextMenu(getResources(),getSupportFragmentManager());
        
        // Application process is follow
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
		util.addDebugMsg(1,"I","onResume entered, restartStatus=",String.valueOf(restartStatus));

		if (restartStatus==0) {
		} else if (restartStatus==1) {
			envParms.loadSettingParms(context);
			setSchedulerStatus();
		} else if (restartStatus==2) {
		}
		final Intent in=getIntent();
		if (restartStatus!=1) {
			NotifyEvent ntfy = new NotifyEvent(this);
			ntfy.setListener(new NotifyEventListener() {
				@Override
				public void positiveResponse(Context c, Object[] o) {
					String home="";
					if (in!=null && in.getStringExtra("Home")!=null) {
						home=in.getStringExtra("Home");
					}
					showMainDialog(home);
				}
				@Override
				public void negativeResponse(Context c, Object[] o) {}
			});
			startSvcSchduler(ntfy);
		}
		restartStatus=1;
		
        // Application process are as follow
	};
	
	@Override
	public void onPause() {
		super.onPause();
		util.addDebugMsg(1,"I","onPause entered");
		
        // Application process is follow

	};

	@Override
	public void onStop() {
		super.onStop();
		util.addDebugMsg(1,"I","onStop entered");
		
        // Application process is follow

		
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		util.addDebugMsg(1,"I","onDestroy entered");
		if (mainDialog!=null) {
			mainDialog.dismiss();
			mainDialog=null;
		}
        // Application process is follow
		
		if (isTerminateApplication) {
			isTerminateApplication=false;
			unsetCallbackListener();
		}
		stopSvcSchduler();
		
		util=null;
		envParms=null;
		context=null;
		
		ccMenu=null;
	};
	
	@Override
	public void onConfigurationChanged(final Configuration newConfig) {
	    // Ignore orientation change to keep activity from restarting
	    super.onConfigurationChanged(newConfig);
	    util.addDebugMsg(1,"I","onConfigurationChanged Entered");
	};
	
	final private void setCallbackListener() {
		final Handler handler = new Handler();
		util.addDebugMsg(1, "I", "setCallbackListener entered");
        svcClientCallback = new ISchedulerCallback.Stub() {
        	final public void notifyToClient(String resp_time, final String resp, 
					final String grp,final String task, final String action, final String shell_cmd, 
					final String dialog_id, final int atc, final int resp_cd, final String msg) throws RemoteException {
				if (envParms.settingDebugLevel>=2) util.addDebugMsg(1, "I", "Notify received ",
						"Resp=",resp,", Task=",task,", action=",action,
						", dialog_id=",dialog_id);
				handler.post(new Runnable() {
					@Override
                    public void run() {
                    	updateActiveTaskByResponse(atc, resp,grp,task);
                    	setSchedulerStatus();
					}
				});
			}
        };
		try{
			svcServer.setCallBack(svcClientCallback);
		} catch (RemoteException e){
			e.printStackTrace();
			util.addLogMsg("E", "setCallbackListener error :",e.toString());
		}
	};
	
	private void unsetCallbackListener() {
		try{
			svcServer.removeCallBack(svcClientCallback);
		} catch (RemoteException e){
			e.printStackTrace();
			util.addLogMsg("E", "unsetCallbackListener error :",e.toString());
		}
	};

	final private void updateActiveTaskByResponse(int atc,String resp, String grp, String task) {
		ArrayList<TaskStatusTaskHistoryListItem> thli=getTaskHistoryList();
		historyAdapter.buildList(thli);
		historyAdapter.notifyDataSetChanged();
		int cnt=historyAdapter.getCount()-1;
		historyListView.setSelection(cnt);
		ArrayList<TaskStatusActiveTaskListItem> atl=getActiveTaskList(atc);
		statusAdapter.setAllItem(atl);
	};

	
	private void startSvcSchduler(final NotifyEvent p_ntfy) {
		if (svcServer != null) return;
		util.addDebugMsg(1,"I", "startSvcSchduler entered");
		
        svcConnScheduler = new ServiceConnection(){
    		public void onServiceConnected(ComponentName name, IBinder service) {
				util.addDebugMsg(1, "I", "Callback onServiceConnected entered");
    			svcServer = ISchedulerClient.Stub.asInterface(service);
    			setCallbackListener();
    			p_ntfy.notifyToListener(true, null);
    		}
    		public void onServiceDisconnected(ComponentName name) {
				util.addDebugMsg(1, "I", "Callback onServiceDisconnected entered");
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
				svcServer.removeCallBack(svcClientCallback);
				svcClientCallback=null;
			} catch (RemoteException e) {
				e.printStackTrace();
				util.addLogMsg("E", "removeListener error :",e.toString());
			}
		}
		unbindService(svcConnScheduler);
	};

	private ArrayList<TaskStatusActiveTaskListItem> getActiveTaskList(int atc) {
		ArrayList<TaskStatusActiveTaskListItem> tal=new ArrayList<TaskStatusActiveTaskListItem>();	
		try {
			if (atc!=0) {
				String[] atl=svcServer.aidlGetActiveTaskList();
				if (atl!=null) {
					TaskStatusActiveTaskListItem tsatli;
					for (int i=0;i<atl.length;i++) {
						String[] atl_item=atl[i].split("\t");
						if (atl_item!=null) {
							tsatli=new TaskStatusActiveTaskListItem();
							tsatli.profile_grp=atl_item[0];
							tsatli.event_name=atl_item[1];
							tsatli.task_name=atl_item[2];
							tsatli.start_time=atl_item[4];
							tsatli.task_active_state=atl_item[5];
							tal.add(tsatli);
						}
					}
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return tal;
	};
	
	private ArrayList<TaskStatusTaskHistoryListItem> getTaskHistoryList() {
		ArrayList<TaskStatusTaskHistoryListItem> thl=new ArrayList<TaskStatusTaskHistoryListItem>();
		try {
			String[] thsal=svcServer.aidlGetTaskHistoryList();
			if (thsal!=null) {
				TaskStatusTaskHistoryListItem tsatli;
				for (int i=0;i<thsal.length;i++) {
					String[] atl_item=thsal[i].split("\t");
					if (atl_item!=null) {
						tsatli=new TaskStatusTaskHistoryListItem();
						tsatli.end_time=atl_item[0];
						tsatli.task_status=atl_item[1];
						tsatli.result=atl_item[2];
						tsatli.profile_grp=atl_item[3];
						tsatli.event_name=atl_item[4];
						tsatli.task_name=atl_item[5];
						if (atl_item.length>6) tsatli.msg_text=atl_item[6];
						else tsatli.msg_text="";
						thl.add(tsatli);
					}
				}
				
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return thl;
	};

	private ListView historyListView, statusListView;
	private AdapterTaskStatusHistoryList historyAdapter;
	private AdapterTaskStatusActiveList statusAdapter;
	private Dialog mainDialog=null;
	
	private TabHost mTabHost;
	private TabWidget mTabWidget;
	
	private CustomViewPager mViewPager;
	private CustomViewPagerAdapter mViewPagerAdapter;
	
	@SuppressWarnings("deprecation")
	@SuppressLint({ "NewApi", "InflateParams" })
	private void showMainDialog(String home) {
		
		// common カスタムダイアログの生成
		mainDialog = new Dialog(context);
		mainDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mainDialog.setContentView(R.layout.task_status_dlg);

//		if (util.isKeyguardEffective()) {
//			Window win = mainDialog.getWindow();
//			win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER |
//				WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
////				WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
//				WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
//			);
//		};
		
        // get our tabHost from the xml
        mTabHost = (TabHost)mainDialog.findViewById(R.id.task_status_dlg_tab_host);
        mTabHost.setup();
        
        mTabWidget = (TabWidget)mainDialog.findViewById(android.R.id.tabs);
		 
		if (Build.VERSION.SDK_INT>=11) {
		    mTabWidget.setStripEnabled(false);  
		    mTabWidget.setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);  
		}

		CustomTabContentView tabViewProf = new CustomTabContentView(this,getString(R.string.msgs_status_dialog_active_task));
		mTabHost.addTab(mTabHost.newTabSpec("task").setIndicator(tabViewProf).setContent(android.R.id.tabcontent));
		
		CustomTabContentView tabViewHist = new CustomTabContentView(this,getString(R.string.msgs_status_dialog_task_history));
		mTabHost.addTab(mTabHost.newTabSpec("history").setIndicator(tabViewHist).setContent(android.R.id.tabcontent));

        LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout ll_task=(LinearLayout)vi.inflate(R.layout.task_status_dlg_task,null);
        LinearLayout ll_hist=(LinearLayout)vi.inflate(R.layout.task_status_dlg_history,null);

		TextView title = (TextView) mainDialog.findViewById(R.id.task_status_dlg_title);
		title.setText(getString(R.string.msgs_status_dialog_title));
		setSchedulerStatus();
		historyListView = 
				(ListView)ll_hist.findViewById(R.id.task_status_dlg_history_listview);
		statusListView = 
				(ListView)ll_task.findViewById(R.id.task_status_dlg_status_listview);
		
		historyAdapter=new AdapterTaskStatusHistoryList(this, R.layout.task_history_list_item, getTaskHistoryList());
		historyListView.setAdapter(historyAdapter);
		historyListView.setSelection(historyAdapter.getCount()-1);
		historyListView.setEnabled(true);
		historyListView.setSelected(true);
		setTaskListLongClickListener();

		statusAdapter=new AdapterTaskStatusActiveList(this,
        		R.layout.task_status_list_item, getActiveTaskList(1));
		statusListView.setAdapter(statusAdapter);
		statusListView.setSelection(statusAdapter.getCount()-1);
		statusListView.setEnabled(true);
		statusListView.setSelected(true);
		
		setCancelBtnListener();

		final CheckBox cb_enable_scheduler = (CheckBox) mainDialog.findViewById(R.id.task_status_dlg_status_enable_scheduler);
		
		final Button btnLog = (Button) mainDialog.findViewById(R.id.task_status_dlg_log_btn);
		final Button btnClose = (Button) mainDialog.findViewById(R.id.task_status_dlg_close_btn);
		
		CommonDialog.setDlgBoxSizeLimit(mainDialog,true);
//		mainDialog.setOnKeyListener(new DialogOnKeyListener(context));

		mViewPagerAdapter=new CustomViewPagerAdapter(this, new View[]{ll_task, ll_hist});
		mViewPager=(CustomViewPager)mainDialog.findViewById(R.id.task_status_dlg_view_pager);
//	    mMainViewPager.setBackgroundColor(mThemeColorList.window_color_background);
		mViewPager.setAdapter(mViewPagerAdapter);
		mViewPager.setOnPageChangeListener(new PageChangeListener()); 

		mTabHost.setOnTabChangedListener(new OnTabChange());

		cb_enable_scheduler.setChecked(envParms.settingEnableScheduler);
		cb_enable_scheduler.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			private boolean ignoreEvent=false;
			@Override
			public void onCheckedChanged(CompoundButton arg0, final boolean isChecked) {
				if (ignoreEvent) {
					ignoreEvent=false;
					return;
				}
				NotifyEvent ntfy=new NotifyEvent(null);
				ntfy.setListener(new NotifyEventListener(){
					@Override
					public void positiveResponse(Context c, Object[] o) {
						envParms.setSettingEnableScheduler(context, isChecked);
						try {
							svcServer.aidlCancelAllActiveTask();
							svcServer.aidlResetScheduler();
						} catch (RemoteException e) {
							e.printStackTrace();
						}
						setSchedulerStatus();
					}
					@Override
					public void negativeResponse(Context c, Object[] o) {
						ignoreEvent=true;
						cb_enable_scheduler.setChecked(!isChecked);
					}
				});
				String msg="";
				if (!isChecked) msg=context.getString(R.string.msgs_status_dialog_enable_scheduler_confirm_msg_disable); 
				else msg=context.getString(R.string.msgs_status_dialog_enable_scheduler_confirm_msg_enable);
				CommonDialog cd=new CommonDialog(context, getSupportFragmentManager());
				cd.showCommonDialog(true, "W", msg, "", ntfy);
			}
		});

		if (util.isLogFileExists()) btnLog.setEnabled(true);
		else btnLog.setEnabled(false);
		btnLog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				util.resetLogReceiver();
				Intent intent = new Intent();
				intent = new Intent(android.content.Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.parse("file://"+util.getLogFilePath()),
						"text/plain");
				startActivity(intent);
				setSchedulerStatus();
			}
		});

		// Closeボタンの指定
		btnClose.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mainDialog.dismiss();
//				commonDlg.setFixedOrientation(false);
			}
		});
		mainDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				finish();
			}
		});
		// Cancelリスナーの指定
		mainDialog.setOnCancelListener(new Dialog.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				btnClose.performClick();
			}
		});
//		commonDlg.setFixedOrientation(true);
		mainDialog.setCancelable(true);
		mainDialog.show();

	};
	
	private class OnTabChange implements OnTabChangeListener {
		@Override
		public void onTabChanged(String tabId){
			mViewPager.setCurrentItem(mTabHost.getCurrentTab());
		};
	};
	
	private class PageChangeListener implements ViewPager.OnPageChangeListener {  
	    @Override  
	    public void onPageSelected(int position) {
//	    	util.addDebugLogMsg(2,"I","onPageSelected entered, pos="+position);
	        mTabWidget.setCurrentTab(position);
	        mTabHost.setCurrentTab(position);
	    }  
	  
	    @Override  
	    public void onPageScrollStateChanged(int state) {  
//	    	util.addDebugLogMsg(2,"I","onPageScrollStateChanged entered, state="+state);
	    }  
	  
	    @Override  
	    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//	    	util.addDebugLogMsg(2,"I","onPageScrolled entered, pos="+position);
	    }  
	};

	final private class CustomTabContentView extends FrameLayout {  
        public CustomTabContentView(Context context) {  
            super(context);  
        }  
        @SuppressLint("InflateParams")
		public CustomTabContentView(Context context, String title) {  
            this(context);  
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
            View childview1 = inflater.inflate(R.layout.tab_widget1, null);  
            TextView tv1 = (TextView) childview1.findViewById(R.id.tab_widget1_textview);  
            tv1.setText(title);  
            addView(childview1);  
       }  
    };
	private void setSchedulerStatus() {
		TextView dlg_msg = 
				(TextView) mainDialog.findViewById(R.id.task_status_dlg_msg);
		String msg_text="", msg_pref="";
		if (!envParms.settingEnableScheduler) {
			msg_text=msg_pref+getString(R.string.msgs_main_task_scheduler_not_running);
			msg_pref="\n";
		}
		try {
			int cnt = svcServer.aidlGetTaskListCount();
			if (cnt==0) {
				msg_text+=msg_pref+getString(R.string.msgs_main_task_scheduler_no_valid_profile);
				msg_pref="\n";
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		if (msg_text.equals("")) {
			dlg_msg.setVisibility(TextView.GONE);
		} else {
			dlg_msg.setVisibility(TextView.VISIBLE);
			dlg_msg.setText(msg_text);
		}
	};

	private void setTaskListLongClickListener() {
		historyListView
		.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				createTaskListContextMenu(arg1, arg2);
				return true;
			}
		});
	};
	
	private void createTaskListContextMenu(View view, int idx) {
		ccMenu.addMenuItem(
				getString(R.string.msgs_status_ccmenu_refresh))
	  	.setOnClickListener(new CustomContextMenuOnClickListener() {
		  @Override
		  public void onClick(CharSequence menuTitle) {
			  historyListView.setSelection(0);
		  }
	  	});
		ccMenu.addMenuItem(
				getString(R.string.msgs_status_ccmenu_move_top),R.drawable.menu_top)
	  	.setOnClickListener(new CustomContextMenuOnClickListener() {
		  @Override
		  public void onClick(CharSequence menuTitle) {
			  historyListView.setSelection(0);
		  }
	  	});
		ccMenu.addMenuItem(
				getString(R.string.msgs_status_ccmenu_move_bottom),R.drawable.menu_bottom)
	  	.setOnClickListener(new CustomContextMenuOnClickListener() {
		  @Override
		  public void onClick(CharSequence menuTitle) {
			  historyListView.setSelection(historyAdapter.getCount()-1);
		  }
	  	});
		ccMenu.addMenuItem(
				getString(R.string.msgs_status_ccmenu_clear),R.drawable.menu_trash)
	  	.setOnClickListener(new CustomContextMenuOnClickListener() {
		  @Override
		  public void onClick(CharSequence menuTitle) {
			  try {
				  svcServer.aidlClearTaskHistory();
				  historyAdapter.buildList(getTaskHistoryList());
				  historyAdapter.notifyDataSetChanged();
				  historyListView.setSelection(historyAdapter.getCount()-1);

			} catch (RemoteException e) {
				e.printStackTrace();
			}
		  }
	  	});

		
		ccMenu.createMenu();
	};


	private void setCancelBtnListener() {
		NotifyEvent ntfy=new NotifyEvent(this);
		ntfy.setListener(new NotifyEventListener(){
			@Override
			public void positiveResponse(Context c, Object[] o) {
				try {
					svcServer.aidlCancelSpecificTask((String)o[0],(String)o[1]);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			@Override
			public void negativeResponse(Context c, Object[] o) {}
		});
		statusAdapter.setCancelBtnListener(ntfy);
	};
}
class TaskStatusActiveTaskListItem {
	public String profile_grp=null, event_name="", task_name="";
	public String start_time="";
	public String task_active_state="STARTED";
};

class TaskStatusTaskHistoryListItem {
	public String profile_grp=null, event_name="", task_name="";
	public String task_status="";
	public String result="";
	public String start_time="", end_time="";
	public String msg_text="";
};

class AdapterTaskStatusActiveList extends ArrayAdapter<TaskStatusActiveTaskListItem>{
	private Context c;
	private int id;
	private ArrayList<TaskStatusActiveTaskListItem>items;
	
	public AdapterTaskStatusActiveList(Context context, int textViewResourceId,
			ArrayList<TaskStatusActiveTaskListItem> objects) {
		super(context, textViewResourceId, objects);
		c = context;
		id = textViewResourceId;
		buildList(objects);
	}
	
	@Override
	final public int getCount() {
		return items.size();
	}
	
	@Override
	final public void clear() {
		items.clear();
	}

	final public void buildList(ArrayList<TaskStatusActiveTaskListItem> sa) {
		items=new ArrayList<TaskStatusActiveTaskListItem>();
		if (sa!=null && sa.size()!=0) {
			items.addAll(sa);
		} else {
			TaskStatusActiveTaskListItem atl=new TaskStatusActiveTaskListItem();
			atl.task_name="No tasks";
			items.add(atl);
		}
	};
	
	final public void sort() {
		Collections.sort(items, new Comparator<TaskStatusActiveTaskListItem>() {
			@Override
			public int compare(TaskStatusActiveTaskListItem lhs, TaskStatusActiveTaskListItem rhs) {
				if (!lhs.profile_grp.equals(rhs.profile_grp)) 
					return lhs.profile_grp.compareToIgnoreCase(rhs.profile_grp); 
				else return lhs.task_name.compareToIgnoreCase(rhs.task_name);
			}
		});
	}
	
	final public void remove(int i) {
		items.remove(i);
	}

	
	final public void replace(int i, TaskStatusActiveTaskListItem nv) {
		items.set(i,nv);
	}

	@Override
	final public void add(TaskStatusActiveTaskListItem mli) {
		items.add(mli);
		notifyDataSetChanged();
	}
	
	@Override
	final public TaskStatusActiveTaskListItem getItem(int i) {
		 return items.get(i);
	}
	
	final public ArrayList<TaskStatusActiveTaskListItem> getAllItem() {return items;}
	
	final public void setAllItem(ArrayList<TaskStatusActiveTaskListItem> p) {
		items.clear();
		if (p!=null && p.size()!=0) {
			items.addAll(p);
		} else {
			TaskStatusActiveTaskListItem atl=new TaskStatusActiveTaskListItem();
			atl.task_name="No tasks";
			items.add(atl);
		}
		notifyDataSetChanged();
	}
	
	private NotifyEvent ntfy_listener=null;
	final public void setCancelBtnListener(NotifyEvent ntfy) {
		ntfy_listener=ntfy;
	}
	final public void unsetCancelBtnListener(NotifyEvent ntfy) {
		ntfy_listener=null;
	}
	
//	@Override
//	public boolean isEnabled(int idx) {
//		 return getItem(idx).getActive().equals("A");
//	}

	@Override
	final public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(id, null);
            holder=new ViewHolder();
            holder.tv_active_state= (TextView) v.findViewById(R.id.task_status_list_item_active_state);
            holder.tv_grp= (TextView) v.findViewById(R.id.task_status_list_item_grp);
            holder.tv_task= (TextView) v.findViewById(R.id.task_status_list_item_task);
            holder.btn_cancel=(Button)v.findViewById(R.id.task_status_list_item_cancel_btn);
            holder.config=v.getResources().getConfiguration();
            v.setTag(holder);
        } else {
        	holder= (ViewHolder)v.getTag();
        }
        final TaskStatusActiveTaskListItem o = getItem(position);
        if (o != null) {
   			if (o.task_name.startsWith("No")) {
   				holder.tv_task.setText("No active tasks");
   				holder.tv_active_state.setVisibility(TextView.GONE);
   				holder.tv_grp.setVisibility(TextView.GONE);
   				holder.btn_cancel.setVisibility(Button.GONE);
   			} else {
   				holder.tv_active_state.setText(o.task_active_state.substring(0,1));
   				holder.tv_active_state.setVisibility(TextView.VISIBLE);
   	   			holder.tv_grp.setText(o.profile_grp);
   	   			holder.tv_task.setText(o.task_name);
   	   			holder.tv_grp.setVisibility(TextView.VISIBLE);
   	   			holder.btn_cancel.setVisibility(Button.VISIBLE);
   			}
   	        final int p=position;
   	        holder.btn_cancel.setOnClickListener(new OnClickListener(){
   				@Override
   				public void onClick(View v) {
   					if (ntfy_listener!=null)
   						ntfy_listener.notifyToListener(true, 
   								new Object[]{o.profile_grp,o.task_name});
   					remove(p);
   					if (items.size()==0) {
   						TaskStatusActiveTaskListItem atl=new TaskStatusActiveTaskListItem();
   						atl.task_name="No tasks";
   						items.add(atl);
   					}
   					notifyDataSetChanged();
   				}
   	        	
   	        });
       	}
        return v;
	};


	class ViewHolder {
		TextView tv_task, tv_grp, tv_active_state;
		Button btn_cancel;
		Configuration config;
	}
}

class AdapterTaskStatusHistoryList extends ArrayAdapter<TaskStatusTaskHistoryListItem>{
	private Context c;
	private int id;
	private ArrayList<TaskStatusTaskHistoryListItem>items;
	
	public AdapterTaskStatusHistoryList(Context context, int textViewResourceId,
			ArrayList<TaskStatusTaskHistoryListItem> objects) {
		super(context, textViewResourceId);
		c = context;
		id = textViewResourceId;
		buildList(objects);
	}
	
	@Override
	final public int getCount() {
		return items.size();
	}
	
	final public void buildList(ArrayList<TaskStatusTaskHistoryListItem> sa) {
		items=new ArrayList<TaskStatusTaskHistoryListItem>();
		if (sa!=null && sa.size()!=0) {
			items.addAll(sa);
		} else {
			TaskStatusTaskHistoryListItem thli=new TaskStatusTaskHistoryListItem();
			thli.task_name="No tasks";
			items.add(thli);
		}
	};
	
	final public void sort() {
		Collections.sort(items, new Comparator<TaskStatusTaskHistoryListItem>() {
			@Override
			public int compare(TaskStatusTaskHistoryListItem lhs, TaskStatusTaskHistoryListItem rhs) {
				if (!lhs.profile_grp.equals(rhs.profile_grp)) 
					return lhs.profile_grp.compareToIgnoreCase(rhs.profile_grp); 
				else return lhs.task_name.compareToIgnoreCase(rhs.task_name);
			}
		});
	}
	
	final public void remove(int i) {
		items.remove(i);
	}

	@Override
	final public void add(TaskStatusTaskHistoryListItem mli) {
		items.add(mli);
		notifyDataSetChanged();
	}
	
	@Override
	final public TaskStatusTaskHistoryListItem getItem(int i) {
		 return items.get(i);
	}
	
	final public List<TaskStatusTaskHistoryListItem> getAllItem() {return items;}
	
	final public void setAllItem(List<TaskStatusTaskHistoryListItem> p) {
		items.clear();
		if (p!=null) items.addAll(p);
		notifyDataSetChanged();
	}
	
//	@Override
//	public boolean isEnabled(int idx) {
//		 return getItem(idx).getActive().equals("A");
//	}

	private Drawable ll_background=null;
	@Override
	final public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(id, null);
            holder=new ViewHolder();
            holder.tv_time= (TextView) v.findViewById(R.id.task_history_list_item_time);
            holder.tv_status= (TextView) v.findViewById(R.id.task_history_list_item_status);
            holder.tv_grp= (TextView) v.findViewById(R.id.task_history_list_item_grp);
            holder.tv_task= (TextView) v.findViewById(R.id.task_history_list_item_task);
            holder.tv_msg= (TextView) v.findViewById(R.id.task_history_list_item_msg);
            holder.stat_success=c.getString(R.string.msgs_status_view_stat_success);
            holder.stat_error=c.getString(R.string.msgs_status_view_stat_error);
            holder.stat_cancelled=c.getString(R.string.msgs_status_view_stat_cancelled);
            holder.stat_started=c.getString(R.string.msgs_status_view_stat_started);
            holder.config=v.getResources().getConfiguration();
            if (ll_background==null) ll_background=holder.tv_time.getBackground();
            v.setTag(holder);
        } else {
        	holder= (ViewHolder)v.getTag();
        }
        TaskStatusTaskHistoryListItem o = getItem(position);
        if (o != null) {
			holder.tv_time.setTextColor(Color.WHITE);
            holder.tv_status.setTextColor(Color.WHITE);
            holder.tv_grp.setTextColor(Color.WHITE);
            holder.tv_task.setTextColor(Color.WHITE);
            holder.tv_msg.setTextColor(Color.WHITE);

   			if (o.task_name.startsWith("No")) {
   				holder.tv_time.setText("No task history");
   	            holder.tv_status.setText("");
   	            holder.tv_grp.setText("");
   	            holder.tv_task.setText("");
   	            holder.tv_msg.setVisibility(TextView.GONE);
   			} else {
   	   			holder.tv_time.setText(o.end_time);
   				if (o.task_status.equals(TaskHistoryItem.TASK_HISTORY_TASK_STATUS_STARTED)) holder.tv_status.setText("A");
   				else if (o.task_status.equals(TaskHistoryItem.TASK_HISTORY_TASK_STATUS_QUEUED)) holder.tv_status.setText("Q");
   				else if (o.task_status.equals(TaskHistoryItem.TASK_HISTORY_TASK_STATUS_ENDED)) holder.tv_status.setText("S");
   	   			if (o.result.equals(TaskResponse.RESP_CHAR_SUCCESS)) {
   	   			} else if (o.result.equals(TaskResponse.RESP_CHAR_ERROR)) { 
   	   				holder.tv_status.setText("E");
   	   				holder.tv_time.setTextColor(Color.RED);
	   	            holder.tv_status.setTextColor(Color.RED);
	   	            holder.tv_grp.setTextColor(Color.RED);
	   	            holder.tv_task.setTextColor(Color.RED);
	   	            holder.tv_msg.setTextColor(Color.RED);
   	   			} else if (o.result.equals(TaskResponse.RESP_CHAR_CANCELLED)) { 
   	   				holder.tv_status.setText("C");
   	   				holder.tv_time.setTextColor(Color.YELLOW);
	   	            holder.tv_status.setTextColor(Color.YELLOW);
	   	            holder.tv_grp.setTextColor(Color.YELLOW);
	   	            holder.tv_task.setTextColor(Color.YELLOW);
	   	            holder.tv_msg.setTextColor(Color.YELLOW);
   	   			} else if (o.result.equals(NTFY_TO_SVC_TASK_STARTED)) { 
   	   				holder.tv_status.setText("A");
   	   			}
   	   			holder.tv_msg.setVisibility(TextView.GONE);
   	   			holder.tv_grp.setText(o.profile_grp);
   	   			holder.tv_task.setText(o.task_name);
   	   			if (!o.msg_text.equals("") && !o.msg_text.equals(" ")) {
   	   	   			holder.tv_msg.setText(o.msg_text);
   	   				holder.tv_msg.setVisibility(TextView.VISIBLE);
   	   			}
   			}
       	}
        return v;
	};


	class ViewHolder {
		TextView tv_time, tv_status, tv_grp, tv_task, tv_msg;
		String stat_success, stat_error,stat_cancelled, stat_started;
		Configuration config;
	}
}


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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import com.sentaroh.android.TaskAutomation.Log.LogFileListDialogFragment;
import com.sentaroh.android.TaskAutomation.Log.LogUtil;
import com.sentaroh.android.Utilities.Dialog.CommonDialog;
import com.sentaroh.android.Utilities.LocalMountPoint;
import com.sentaroh.android.Utilities.NotifyEvent;
import com.sentaroh.android.Utilities.NotifyEvent.NotifyEventListener;
import com.sentaroh.android.Utilities.ThemeUtil;
import com.sentaroh.android.Utilities.ContextButton.ContextButtonUtil;
import com.sentaroh.android.Utilities.ContextMenu.CustomContextMenu;
import com.sentaroh.android.Utilities.Widget.CustomSpinnerAdapter;
import com.sentaroh.android.Utilities.Widget.CustomTabContentView;
import com.sentaroh.android.Utilities.Widget.CustomViewPager;
import com.sentaroh.android.Utilities.Widget.CustomViewPagerAdapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.StrictMode;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

@SuppressLint("NewApi")
public class ActivityMain extends AppCompatActivity {

	private static boolean DEBUG_ENABLE=true;

	private GlobalParameters mGp=null;
	private boolean mIsApplicationTerminated=false;
	
	private String mApplicationVersion="";
	
	private boolean mApplicationRunFirstTime=false;
	private static int mRestartStatus=0;
	
	private EnvironmentParms mEnvParms=null;
	
	private ProfileMaintenance mProfMaint=null;
	
	private TabHost mMainTabHost=null;
	
	private ArrayList<String> mAndroidApplicationList=null;
	
	private Context mContext=null;
	private Activity mActivity=null;
	
	private ISchedulerCallback mSvcClientCallback=null;
	private ServiceConnection mSvcConnScheduler=null;
	private ISchedulerClient mSvcServer=null;
	
	@Override  
	final protected void onSaveInstanceState(Bundle outState) {  
		super.onSaveInstanceState(outState);
		if (DEBUG_ENABLE) mGp.util.addDebugMsg(1,"I","onSaveInstanceState entered");
	};  
	  
	@Override  
	final protected void onRestoreInstanceState(Bundle savedState) {  
		super.onRestoreInstanceState(savedState);
		if (DEBUG_ENABLE) mGp.util.addDebugMsg(1,"I","onRestoreInstanceState entered");
		mRestartStatus=2;
	};
	
//	private void getOverflowMenu() {
//		http://stackoverflow.com/questions/9739498/android-action-bar-not-showing-overflow	
//	     try {
//	        ViewConfiguration config = ViewConfiguration.get(this);
//	        Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
//	        if(menuKeyField != null) {
//	            menuKeyField.setAccessible(true);
//	            menuKeyField.setBoolean(config, false);
//	        }
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	    }
//	};

    @SuppressLint("NewApi")
	@Override
    final public void onCreate(Bundle savedInstanceState) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        super.onCreate(savedInstanceState);
//    	StrictMode.enableDefaults();
//        if (Build.VERSION.SDK_INT<=10) 
//        	requestWindowFeature(Window.FEATURE_NO_TITLE); 
        
        mContext=this;
        mActivity=this;
        mGp= GlobalWorkArea.getGlobalParameters(mContext);
        mGp.frgamentMgr=getSupportFragmentManager();
		mGp.context=mContext;
		mGp.themeColorList=ThemeUtil.getThemeColorList(mActivity);
        mEnvParms=new EnvironmentParms();
        mGp.envParms=mEnvParms;
        mApplicationRunFirstTime=initSettingParms();
        mGp.ccMenu=new CustomContextMenu(this.getResources(), getSupportFragmentManager());
        
//		if (CommonUtilities.isKeyguardEffective(mContext)) {
//			Window win = getWindow();
//			win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER |
//				WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
//				WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
//			);
//		};
        setContentView(R.layout.activity_main);

//		if (Build.VERSION.SDK_INT>=14)
//			this.getActionBar().setHomeButtonEnabled(false);
//		getOverflowMenu();
        
		mGp.commonDlg=new CommonDialog(mContext, getSupportFragmentManager());
        mRestartStatus=0;
        mApplicationVersion=setApplVersionName();
        if (mGp.settingDebugLevel==0) DEBUG_ENABLE=false;
        else DEBUG_ENABLE=true;
        mGp.util=new CommonUtilities(mContext.getApplicationContext(), "Main",mEnvParms, mGp);
        mGp.util.startScheduler();
        mGp.initialyzeRequired=false;
        
        if (DEBUG_ENABLE) mGp.util.addDebugMsg(1,"I","onCreate entered");
        mGp.util.addDebugMsg(1,"I","initSettingParms "+
				"applicationRunFirstTime="+mApplicationRunFirstTime+
				", localRootDir="+mEnvParms.localRootDir+
				", settingDebugLevel="+mGp.settingDebugLevel+
//				", settingLogMsgDir="+mGp.settingLogMsgDir+
				", settingLogOption="+mGp.settingLogOption+
				", settingEnableScheduler="+mGp.settingEnableScheduler+
				", settingExitClean="+mGp.settingExitClean);
		mGp.util.addDebugMsg(1, "I", "Android SDK="+Build.VERSION.SDK_INT);
		mProfMaint=new ProfileMaintenance(mEnvParms, mGp);
        createMainTabView();
        mGp.profileAdapter=new AdapterProfileList(mContext, R.layout.task_profile_list_view_item, 
       		new ArrayList<ProfileListItem>());
        mGp.profileGroupAdapter=new AdapterProfileGroupList(mContext, R.layout.task_profile_group_list_view_item, 
        		new ArrayList<ProfileGroupListItem>(),null);
        checkRequiredPermissions();
    };
    
	@Override
	final public void onStart() {
		super.onStart();
		if (DEBUG_ENABLE) mGp.util.addDebugMsg(1,"I","onStart entered");
	};

	@Override
	final public void onRestart() {
		super.onStart();
		if (DEBUG_ENABLE) mGp.util.addDebugMsg(1,"I","onRestart entered");
	};
	
	@Override
	final public void onResume() {
		super.onResume();
		if (DEBUG_ENABLE) mGp.util.addDebugMsg(1,"I","onResume entered, restartStatus="+mRestartStatus);

		if (mRestartStatus!=1) {
			NotifyEvent ntfy = new NotifyEvent(this);
			ntfy.setListener(new NotifyEventListener() {
				@Override
				public void positiveResponse(Context c, Object[] o) {
					mGp.svcServer=mSvcServer;
					if (mRestartStatus==1) return;
					if (mRestartStatus==0) {
						mGp.util.addLogMsg("I",String.format(
								getString(R.string.msgs_main_started), mApplicationVersion));
						mGp.profileAdapter.setDataList(
								ProfileMaintenance.getProfileListFromService(mGp));
						mGp.profileListView.setAdapter(mGp.profileAdapter);
						mGp.profileGroupListView.setAdapter(mGp.profileGroupAdapter);
					} else if (mRestartStatus==2) {
						mGp.util.addLogMsg("I",getString(R.string.msgs_main_restarted));
						mGp.commonDlg.showCommonDialog(false, "W",
								getString(R.string.msgs_main_restarted), "", null);
						mGp.profileListView.setAdapter(mGp.profileAdapter);
						mGp.profileGroupListView.setAdapter(mGp.profileGroupAdapter);
						restoreTaskData();
						deleteTaskData();
						mGp.profileAdapter.setDataList(
								ProfileMaintenance.getProfileListFromService(mGp));
						mGp.profileListView.setAdapter(mGp.profileAdapter);
						
					}
					setProfileContextButtonListener();
					setProfileContextButtonNormalMode();
					
					createProfileGroupList();
					setProfileGroupSelectorListener();
					setProfileGroupTabListClickListener();
					setProfileGroupTabListLongClickListener();
					setProfileFilterSelectorListener();
					setGroupContextButtonListener();
					setGroupContextButtonNormalMode();

					refreshActiveTaskStatus(1);
					setSchedulerStatus();
					setMainViewButtonListener();

					setProfileItemClickListner();
					setProfilelistLongClickListener();
					
					refreshOptionMenu();

					mRestartStatus=1;
				}
				@Override
				public void negativeResponse(Context c, Object[] o) {}
			});
			bindSchedulerService(ntfy);
		} else {
			deleteTaskData();
			mEnvParms.loadSettingParms(mContext);
			setSchedulerStatus();
//			quickTask.initializeQuickTaskView();
//			setProfileItemClickListner();
//			setProfilelistLongClickListener();
			refreshOptionMenu();
		}
	};
	
	@Override
	final public void onPause() {
		super.onPause();
		if (DEBUG_ENABLE) mGp.util.addDebugMsg(1,"I","onPause entered");
		
		if (!mIsApplicationTerminated) saveTaskData(); 
	};

	@Override
	final public void onStop() {
		super.onStop();
		if (DEBUG_ENABLE) mGp.util.addDebugMsg(1,"I","onStop entered");
	};

	@Override
	final public void onDestroy() {
		super.onDestroy();
		if (DEBUG_ENABLE) mGp.util.addDebugMsg(1,"I","onDestroy entered");
		
        // Application process is follow
		
		if (mIsApplicationTerminated) {
			if (mProfMaint!=null) ProfileMaintenance.releaseMediaPlayer(mGp);
			unsetCallbackListener();
			unbindScheduler();
			mIsApplicationTerminated=false;
			deleteTaskData();
			
			mGp.clearParms();
			mProfMaint=null;
			mMainTabHost=null;
			mAndroidApplicationList=null;
			mContext=null;
			mActivity=null;
			mSvcClientCallback=null;
			mSvcConnScheduler=null;
			mSvcServer=null;

			if (mGp.settingExitClean) {
				System.gc();
				android.os.Process.killProcess(android.os.Process.myPid());
			} else {
				mEnvParms=null;
				System.gc();
			}
		} else {
			unbindScheduler();
		}
	};
	
	@Override
	final public void onConfigurationChanged(final Configuration newConfig) {
	    // Ignore orientation change to keep activity from restarting
	    super.onConfigurationChanged(newConfig);
	    mGp.util.addDebugMsg(1,"I","onConfigurationChanged Entered");
	    refreshOptionMenu();
	};
	
	@Override
	final public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_top, menu);
		return true;
	};
	
	@Override
	final public boolean onPrepareOptionsMenu(Menu menu) {
		if (LocalMountPoint.isExternalStorageAvailable()) {
//			menu.findItem(R.id.menu_top_export_profile).setVisible(true);
//			menu.findItem(R.id.menu_top_import_profile).setVisible(true);
			if (mGp.util.isLogFileExists()) {
				menu.findItem(R.id.menu_top_browse_logfile).setVisible(true);
			} else {
				menu.findItem(R.id.menu_top_browse_logfile).setVisible(false);
			}
		} else {
//			menu.findItem(R.id.menu_top_export_profile).setVisible(false);
//			menu.findItem(R.id.menu_top_import_profile).setVisible(false);
			menu.findItem(R.id.menu_top_browse_logfile).setVisible(false);
		}
		if (mGp.settingEnableScheduler) {
			menu.findItem(R.id.menu_top_toggle_scheduler).setIcon(R.drawable.scheduler_off_32);
			menu.findItem(R.id.menu_top_toggle_scheduler).setTitle(R.string.msgs_menu_toggle_scheduler_disabled);
		} else {
			menu.findItem(R.id.menu_top_toggle_scheduler).setIcon(R.drawable.scheduler_on_32);
			menu.findItem(R.id.menu_top_toggle_scheduler).setTitle(R.string.msgs_menu_toggle_scheduler_enabled);
		}

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
            menu.findItem(R.id.menu_top_location_permission).setVisible(true);
        } else {
            menu.findItem(R.id.menu_top_location_permission).setVisible(false);
        }

		super.onPrepareOptionsMenu(menu);
        return true;
	};
	
	@Override
	final public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				processHomeButtonPress();
				return true;
//			case R.id.menu_top_show_history:
//				showHistory();
//				return true;			
			case R.id.menu_top_export_profile:
				mProfMaint.exportProfileDlg(mGp, mGp.profileAdapter, mGp.profileListView);
				return true;
			case R.id.menu_top_import_profile:
				importProfile(mGp.profileAdapter, mGp.profileListView);
				return true;
			case R.id.menu_top_settings:
				invokeSettingsActivity();
				return true;			
			case R.id.menu_top_browse_logfile:
				invokeLogFileBrowser();
				return true;
            case R.id.menu_top_location_permission:
                checkLocationPermission(true);
                return true;
			case R.id.menu_top_toggle_scheduler:
                mGp.setSettingEnableScheduler(mContext, !mGp.settingEnableScheduler);
				try {
					mSvcServer.aidlCancelAllActiveTask();
					mSvcServer.aidlResetScheduler();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				refreshOptionMenu();
				setSchedulerStatus();
				return true;			
			case R.id.menu_top_restart_scheduler:
	    		Intent in_b=new Intent(mContext, ActivityRestartScheduler.class);
	    		in_b.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    		startActivity(in_b);
				return true;			
//			case R.id.menu_top_quick_task:
//		        NotifyEvent ntfy=setQuickTaskListener();
//		        QuickTaskMaintenance quickTask=new QuickTaskMaintenance(context,util, commonDlg, 
//							profMaint, profileAdapter, ntfy);
//		        quickTask.initializeQuickTaskView();
//				return true;			
			case R.id.menu_top_about:
				aboutTaskAutomation();
				return true;			
			case R.id.menu_top_log_management:
                invokeLogManagement();
				return true;
			case R.id.menu_top_uninstall:
				uninstallApplication();
				return true;			
			case R.id.menu_top_create_sample:
			    createSampleProfile();
				return true;
		}
		return false;
	};

	private void createSampleProfile() {
        boolean sample=ProfileUtilities.isProfileGroupActive(mGp.util, mGp.profileAdapter, "*Sample for Task");
        boolean bsh=ProfileUtilities.isProfileGroupActive(mGp.util, mGp.profileAdapter, "*Sample for BeanShell API");
        ProfileUtilities.deleteProfileGroup(mGp.util, mGp.profileAdapter, "*Sample for Task");
        ProfileUtilities.deleteProfileGroup(mGp.util, mGp.profileAdapter, "*Sample for BeanShell API");
        SampleProfile.addSampleProfile(mGp.profileAdapter, true, true);

        mGp.profileAdapter.sort();
        mGp.profileAdapter.updateShowList();
        mGp.profileAdapter.notifyDataSetChanged();
        if (sample || bsh) ProfileMaintenance.putProfileListToService(mGp,mGp.profileAdapter,true);
        else ProfileMaintenance.putProfileListToService(mGp,mGp.profileAdapter,false);

        createProfileGroupList();
        setProfileGroupSelectorListener();
        setProfileGroupTabListClickListener();
        setProfileGroupTabListLongClickListener();
        setProfileFilterSelectorListener();
    }

    private void invokeLogManagement() {
        NotifyEvent ntfy = new NotifyEvent(mContext);
        ntfy.setListener(new NotifyEventListener() {
            @Override
            public void positiveResponse(Context c, Object[] o) {
                mGp.setSettingOptionLogEnabled((boolean) o[0]);
                applySettingParms();
            }

            @Override
            public void negativeResponse(Context c, Object[] o) {
            }
        });
        LogUtil.flushLog(mContext, mGp);
        LogFileListDialogFragment lfm =
                LogFileListDialogFragment.newInstance(false, getString(R.string.msgs_log_management_title),
                        getString(R.string.msgs_log_management_send_log_file_warning),
                        getString(R.string.msgs_log_management_enable_log_file_warning),
                        "TaskAutomation log file");
        lfm.showDialog(getSupportFragmentManager(), lfm, mGp, ntfy);
    }

    private void processHomeButtonPress() {
		if (mMainTabHost.getCurrentTabTag().equals("Grp")) {
			mGp.profileGroupAdapter.setShowCheckBox(false);
			mGp.profileGroupAdapter.setAllItemSelected(false);
			mGp.profileGroupAdapter.notifyDataSetChanged();
			setGroupContextButtonNormalMode();
			setActionBarNormalMode();
		} else if (mMainTabHost.getCurrentTabTag().equals("Prof")) {
			mGp.profileAdapter.setShowCheckBox(false);
			mGp.profileAdapter.setAllProfItemSelected(false);
			mGp.profileAdapter.notifyDataSetChanged();
			setProfileContextButtonNormalMode();
			
			setActionBarNormalMode();
		} 
	};

    private final int REQUEST_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1;
    private final int REQUEST_PERMISSIONS_ACCESS_LOCATION = 2;

    private void checkRequiredPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            mGp.util.addDebugMsg(1, "I", "Prermission WriteExternalStorage=" + checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) +
                    ", WakeLock=" + checkSelfPermission(Manifest.permission.WAKE_LOCK)
            );
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                NotifyEvent ntfy = new NotifyEvent(mContext);
                ntfy.setListener(new NotifyEventListener() {
                    @Override
                    public void positiveResponse(Context c, Object[] o) {
//                        checkLocationPermission();
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
                    }

                    @Override
                    public void negativeResponse(Context c, Object[] o) {
                        NotifyEvent ntfy_term = new NotifyEvent(mContext);
                        ntfy_term.setListener(new NotifyEventListener() {
                            @Override
                            public void positiveResponse(Context c, Object[] o) {
                                finish();
                            }
                            @Override
                            public void negativeResponse(Context c, Object[] o) {}
                        });
                        mGp.commonDlg.showCommonDialog(false, "W",
                                mContext.getString(R.string.msgs_main_permission_external_storage_title),
                                mContext.getString(R.string.msgs_main_permission_external_storage_denied_msg), ntfy_term);
                    }
                });
                mGp.commonDlg.showCommonDialog(false, "W",
                        mContext.getString(R.string.msgs_main_permission_external_storage_title),
                        mContext.getString(R.string.msgs_main_permission_external_storage_request_msg), ntfy);
            } else {
                checkLocationPermission(false);
            }
        } else {
            checkLocationPermission(false);
        }
    }

    public void checkLocationPermission(boolean force_permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            mGp.util.addDebugMsg(1, "I", "Prermission LocationCoarse=" + checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION));
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED && (force_permission)) {
                NotifyEvent ntfy = new NotifyEvent(mContext);
                ntfy.setListener(new NotifyEventListener() {
                    @Override
                    public void positiveResponse(Context c, Object[] o) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_ACCESS_LOCATION);
                    }

                    @Override
                    public void negativeResponse(Context c, Object[] o) {
                    }
                });
                mGp.commonDlg.showCommonDialog(true, "W",
                        mContext.getString(R.string.msgs_main_permission_coarse_location_title),
                        mContext.getString(R.string.msgs_main_permission_coarse_location_request_msg), ntfy);
            } else {
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Handler hndl=new Handler();
        if (REQUEST_PERMISSIONS_WRITE_EXTERNAL_STORAGE == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                hndl.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkLocationPermission(true);
                    }
                }, 500);
            } else {
                NotifyEvent ntfy_term = new NotifyEvent(mContext);
                ntfy_term.setListener(new NotifyEventListener() {
                    @Override
                    public void positiveResponse(Context c, Object[] o) {
                        finish();
                    }

                    @Override
                    public void negativeResponse(Context c, Object[] o) {}
                });
                mGp.commonDlg.showCommonDialog(false, "W",
                        mContext.getString(R.string.msgs_main_permission_external_storage_title),
                        mContext.getString(R.string.msgs_main_permission_external_storage_denied_msg), ntfy_term);
            }
        }
        if (REQUEST_PERMISSIONS_ACCESS_LOCATION == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                NotifyEvent ntfy_deny=new NotifyEvent(mContext);
                ntfy_deny.setListener(new NotifyEventListener() {
                    @Override
                    public void positiveResponse(Context context, Object[] objects) {
                    }
                    @Override
                    public void negativeResponse(Context context, Object[] objects) {}
                });
                mGp.commonDlg.showCommonDialog(false, "W",
                        mContext.getString(R.string.msgs_main_permission_coarse_location_title),
                        mContext.getString(R.string.msgs_main_permission_coarse_location_denied_msg), ntfy_deny);
            }
        }
    }

	final private void uninstallApplication() {
		NotifyEvent ntfy=new NotifyEvent(mContext);
		ntfy.setListener(new NotifyEventListener(){
			@Override
			public void positiveResponse(Context c, Object[] o) {
				Uri uri=Uri.fromParts("package",getPackageName(),null);
				Intent intent=new Intent(Intent.ACTION_DELETE,uri);
				startActivity(intent);
			}
			@Override
			public void negativeResponse(Context c, Object[] o) {}
		});
		ntfy.notifyToListener(true, null);
	};
	
	@SuppressLint("NewApi")
	final private void refreshOptionMenu() {
		if (Build.VERSION.SDK_INT>=11) {
			mActivity.invalidateOptionsMenu();
		}
	};
	
	private CustomViewPagerAdapter mAboutViewPagerAdapter;
	private CustomViewPager mAboutViewPager;
	private TabHost mAboutTabHost ;
	private TabWidget mAboutTabWidget ;
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	final private void aboutTaskAutomation() {
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    dialog.setContentView(R.layout.about_dialog);

		final LinearLayout title_view = (LinearLayout) dialog.findViewById(R.id.about_dialog_title_view);
		final TextView title = (TextView) dialog.findViewById(R.id.about_dialog_title);
		title_view.setBackgroundColor(mGp.themeColorList.title_background_color);
		title.setTextColor(mGp.themeColorList.title_text_color);
		title.setText(getString(R.string.msgs_about_dlg_title)+" Ver "+getApplVersionName());
		
        // get our tabHost from the xml
        mAboutTabHost = (TabHost)dialog.findViewById(R.id.about_tab_host);
        mAboutTabHost.setup();
        
        mAboutTabWidget = (TabWidget)dialog.findViewById(android.R.id.tabs);
		 
		if (Build.VERSION.SDK_INT>=11) {
		    mAboutTabWidget.setStripEnabled(false); 
		    mAboutTabWidget.setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);  
		}

		CustomTabContentView tabViewProf = new CustomTabContentView(this,getString(R.string.msgs_about_dlg_func_btn));
		mAboutTabHost.addTab(mAboutTabHost.newTabSpec("func").setIndicator(tabViewProf).setContent(android.R.id.tabcontent));
		
		CustomTabContentView tabViewHist = new CustomTabContentView(this,getString(R.string.msgs_about_dlg_change_btn));
		mAboutTabHost.addTab(mAboutTabHost.newTabSpec("change").setIndicator(tabViewHist).setContent(android.R.id.tabcontent));

        LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout ll_func=(LinearLayout)vi.inflate(R.layout.about_dialog_func,null);
        LinearLayout ll_change=(LinearLayout)vi.inflate(R.layout.about_dialog_change,null);

		final WebView func_view=(WebView)ll_func.findViewById(R.id.about_dialog_function);
		func_view.loadUrl("file:///android_asset/"+getString(R.string.msgs_about_dlg_func_html));
		func_view.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		func_view.getSettings().setBuiltInZoomControls(true);
		
		final WebView change_view=
				(WebView)ll_change.findViewById(R.id.about_dialog_change_history);
		change_view.loadUrl("file:///android_asset/"+getString(R.string.msgs_about_dlg_change_html));
		change_view.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		change_view.getSettings().setBuiltInZoomControls(true);
		
		mAboutViewPagerAdapter=new CustomViewPagerAdapter(this, 
	    		new WebView[]{func_view, change_view});
		mAboutViewPager=(CustomViewPager)dialog.findViewById(R.id.about_view_pager);
//	    mMainViewPager.setBackgroundColor(mThemeColorList.window_color_background);
		mAboutViewPager.setAdapter(mAboutViewPagerAdapter);
		mAboutViewPager.setOnPageChangeListener(new AboutPageChangeListener()); 

		mAboutTabHost.setOnTabChangedListener(new AboutOnTabChange());
		
		final Button btnOk = (Button) dialog.findViewById(R.id.about_dialog_btn_ok);

		CommonDialog.setDlgBoxSizeLimit(dialog,true);

		// OKボタンの指定
		btnOk.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		// Cancelリスナーの指定
		dialog.setOnCancelListener(new Dialog.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				btnOk.performClick();
			}
		});

		dialog.show();
	};
	
	
	private class AboutOnTabChange implements OnTabChangeListener {
		@Override
		public void onTabChanged(String tabId){
			mGp.util.addDebugMsg(2,"I","onTabchanged entered. tab="+tabId);
			mAboutViewPager.setCurrentItem(mAboutTabHost.getCurrentTab());
		};
	};
	
	private class AboutPageChangeListener implements ViewPager.OnPageChangeListener {  
	    @Override  
	    public void onPageSelected(int position) {
//	    	util.addDebugLogMsg(2,"I","onPageSelected entered, pos="+position);
	        mAboutTabWidget.setCurrentTab(position);
	        mAboutTabHost.setCurrentTab(position);
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
	
	final private String getApplVersionName() {
		try {
		    String packegeName = getPackageName();
		    PackageInfo packageInfo = getPackageManager().getPackageInfo(packegeName, PackageManager.GET_META_DATA);
		    return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			return "";
		}
	};

	private LinearLayout mProfileView;
	private LinearLayout mGroupView;

	private TabWidget mMainTabWidget;
	private CustomViewPagerAdapter mMainViewPagerAdapter;
	private CustomViewPager mMainViewPager;
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	final private void createMainTabView() {
		mMainTabHost=(TabHost)findViewById(android.R.id.tabhost);
		//getTabHost();
		mMainTabHost.setup();

		mMainTabWidget = (TabWidget) findViewById(android.R.id.tabs);
		 
		if (Build.VERSION.SDK_INT>=11) {
		    mMainTabWidget.setStripEnabled(false);  
		    mMainTabWidget.setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);  
		}

		View childview2 = new CustomTabContentView(this,getString(R.string.msgs_main_tab_name_profile_group));
		TabSpec tabSpec=mMainTabHost.newTabSpec("Grp").setIndicator(childview2).setContent(android.R.id.tabcontent);
		mMainTabHost.addTab(tabSpec);

		View childview5 = new CustomTabContentView(this,getString(R.string.msgs_main_tab_name_profile));
		tabSpec=mMainTabHost.newTabSpec("Prof").setIndicator(childview5).setContent(android.R.id.tabcontent);
		mMainTabHost.addTab(tabSpec);
		
		LinearLayout ll_main=(LinearLayout)findViewById(R.id.main_view);
//		ll_main.setBackgroundColor(mGp.themeColorList.window_background_color_content);
		
        LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mGroupView=(LinearLayout)vi.inflate(R.layout.activity_main_group,null);
		mProfileView=(LinearLayout)vi.inflate(R.layout.activity_main_profile,null);


//		if (isFirstStart) tabHost.setCurrentTab(0);
		mMainTabHost.setOnTabChangedListener(new OnTabChangeListener(){
			@Override
			public void onTabChanged(String tabId){
				if (DEBUG_ENABLE) mGp.util.addDebugMsg(1,"I","onTabchanged entered. tab="+tabId);
				mMainViewPager.setCurrentItem(mMainTabHost.getCurrentTab());
				if (tabId.equals("Grp")) createProfileGroupList();

				boolean am=false;
				if (mGp.profileAdapter.isShowCheckBox()) {
					mGp.profileAdapter.setShowCheckBox(false);
					mGp.profileAdapter.setAllProfItemSelected(false);
					mGp.profileAdapter.notifyDataSetChanged();
					setProfileContextButtonNormalMode();
					am=true;
				}
				
				if (mGp.profileGroupAdapter.isShowCheckBox()) {
					mGp.profileGroupAdapter.setShowCheckBox(false);
					mGp.profileGroupAdapter.setAllItemSelected(false);
					mGp.profileGroupAdapter.notifyDataSetChanged();
					setGroupContextButtonNormalMode();
					am=true;
				}
				
				if (am) setActionBarNormalMode();
			};
		});
		mGp.profileGroupListView=(ListView)mGroupView.findViewById(R.id.main_profile_group_listview);
		mGp.profileListView=(ListView)mProfileView.findViewById(R.id.main_profedit_listview);
		
		setGroupContextButtonHide();
		
	    mMainViewPagerAdapter=new CustomViewPagerAdapter(this, 
	    		new View[]{mGroupView, mProfileView});
	    mMainViewPager=(CustomViewPager)findViewById(R.id.main_view_pager);
//	    mMainViewPager.setBackgroundColor(mThemeColorList.window_color_background);
	    mMainViewPager.setAdapter(mMainViewPagerAdapter);
	    mMainViewPager.setOnPageChangeListener(new MainPageChangeListener()); 
		if (mRestartStatus==0) {
			mMainTabHost.setCurrentTab(0);
			mMainViewPager.setCurrentItem(0);
		}

	};
	
	private class MainPageChangeListener implements ViewPager.OnPageChangeListener {  
	    @Override  
	    public void onPageSelected(int position) {
//	    	util.addDebugLogMsg(2,"I","onPageSelected entered, pos="+position);
	        mMainTabWidget.setCurrentTab(position);
	        mMainTabHost.setCurrentTab(position);
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


	final private void importProfile(AdapterProfileList pfla, ListView pflv) {
		NotifyEvent ntfy=new NotifyEvent(mContext);
		// set importProfileDlg response 
		ntfy.setListener(new NotifyEventListener() {
			@Override
			final public void positiveResponse(Context c,Object[] o) {
				applySettingParms();
				createProfileGroupList();
				setProfileGroupSelectorListener();
				setSchedulerStatus();
			}
			@Override
			final public void negativeResponse(Context c,Object[] o) {}
		});
		ProfileMaintenance.importProfileDlg(mGp,ntfy, pfla, pflv);
	};

	final private String setApplVersionName() {
		String ver="";
	    String packegeName = getPackageName();
	    PackageInfo packageInfo;
		try {
			packageInfo = getPackageManager().getPackageInfo(packegeName, PackageManager.GET_META_DATA);
		    ver=packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return ver;
	};

	final private SharedPreferences getPrefsMgr() {
        return CommonUtilities.getPrefMgr(mContext);
    };

    final private boolean initSettingParms() {
		boolean initialized=false;
		if (getPrefsMgr().getString(getString(R.string.settings_main_log_level),"-1").equals("-1")) {
			//first time
			PackageInfo packageInfo;
			String ddl="0";
			try {
				packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
				int flags = packageInfo.applicationInfo.flags;
				if ((flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) ddl="2";

			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}           
			getPrefsMgr().edit().putString(getString(R.string.settings_main_log_level),ddl).commit();
			getPrefsMgr().edit().putString(getString(R.string.settings_main_scheduler_max_task_count),"20").commit();
			getPrefsMgr().edit().putString(getString(R.string.settings_main_scheduler_thread_pool_count),"5").commit();
			getPrefsMgr().edit().putBoolean(getString(R.string.settings_main_enable_scheduler),true).commit();
			getPrefsMgr().edit().putBoolean(getString(R.string.settings_main_device_admin),true).commit();
			getPrefsMgr().edit().putBoolean(getString(R.string.settings_main_scheduler_monitor),false).commit();
			
			getPrefsMgr().edit().putString(
					getString(R.string.settings_main_light_sensor_monitor_interval_time),"1000").commit();
			getPrefsMgr().edit().putString(
					getString(R.string.settings_main_light_sensor_monitor_active_time),"10").commit();
			getPrefsMgr().edit().putString(
					getString(R.string.settings_main_light_sensor_detect_thresh_hold),"30").commit();
			getPrefsMgr().edit().putString(
					getString(R.string.settings_main_light_sensor_ignore_time),"2").commit();

			initialized=true;
		}
		mEnvParms.loadSettingParms(mContext);
		return initialized;
	};

	final private void applySettingParms() {
		try {
			boolean pms=mGp.settingEnableScheduler;
			int p_tpc=mGp.settingTaskExecThreadPoolCount;
//			String p_intvl=mEnvParms.settingSleepOption;
			boolean p_light_sensor_thread=mGp.settingLightSensorUseThread;
			
			mGp.loadSettingParms(mContext);
//    		if (mGp.settingLogMsgDir.equals("")) {
//                mGp.settingLogMsgDir=Environment.getExternalStorageDirectory().toString()+
//    					"/"+APPLICATION_TAG+"/";
//    			getPrefsMgr().edit().putString(mContext.getString(R.string.settings_main_log_dir),
//                        mGp.settingLogMsgDir).commit();
//    		} else {
//        		if (!mGp.settingLogMsgDir.endsWith("/")) {
//                    mGp.settingLogMsgDir+="/";
//        			getPrefsMgr().edit().putString(mContext.getString(R.string.settings_main_log_dir),
//                            mGp.settingLogMsgDir).commit();
//        		}
//    		}
			
			int n_tpc=mGp.settingTaskExecThreadPoolCount;
			
//			String n_intvl=mEnvParms.settingSleepOption;
			
	        if (mGp.settingDebugLevel==0) DEBUG_ENABLE=false;
	        else DEBUG_ENABLE=true;
	        
			mGp.util.addDebugMsg(1,"I","initSettingParms ");
			mGp.util.addDebugMsg(1,"I","  localRootDir="+mEnvParms.localRootDir);
			mGp.util.addDebugMsg(1,"I","  settingDebugLevel="+mGp.settingDebugLevel);
//			mGp.util.addDebugMsg(1,"I","  settingLogMsgDir="+mGp.settingLogMsgDir);
			mGp.util.addDebugMsg(1,"I","  settingLogOption="+mGp.settingLogOption);
			mGp.util.addDebugMsg(1,"I","  settingEnableScheduler="+mGp.settingEnableScheduler);
			mGp.util.addDebugMsg(1,"I","  settingMaxTaskCount="+mGp.settingMaxTaskCount);
			mGp.util.addDebugMsg(1,"I","  settingThreadPoolCount="+mGp.settingTaskExecThreadPoolCount);
			mGp.util.addDebugMsg(1,"I","  settingEnableMonitor="+mGp.settingEnableMonitor);
			mGp.util.addDebugMsg(1,"I","  settingWakeLockAlways="+mGp.settingWakeLockOption);
			mGp.util.addDebugMsg(1,"I","  settingWakeLockLightSensor="+mGp.settingWakeLockLightSensor);
			mGp.util.addDebugMsg(1,"I","  settingWakeLockProximitySensor="+mGp.settingWakeLockProximitySensor);
			mGp.util.addDebugMsg(1,"I","  settingExitClean="+mGp.settingExitClean);
			
			mGp.util.resetLogReceiver(mContext);

			if (p_tpc!=n_tpc ||
					isBooleanDifferent(p_light_sensor_thread,mGp.settingLightSensorUseThread)) {
				if (!pms && mGp.settingEnableScheduler) mGp.util.startScheduler();
//				mGlblParms.util.restartScheduler();
	    		Intent in_b=
	    				new Intent(mContext.getApplicationContext(),ActivityRestartScheduler.class);
	    		in_b.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    		startActivity(in_b);
			} else {
				if (!pms && mGp.settingEnableScheduler) mGp.util.startScheduler();
				mSvcServer.aidlResetScheduler();
			}

			setSchedulerStatus();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	};
	
	@SuppressLint("NewApi")
	final private void setSchedulerStatus() {
		TextView tv_msg=(TextView)findViewById(R.id.main_msg);
		String msg_text="", msg_pref="";
//		if (Build.VERSION.SDK_INT>=14)
//			this.getActionBar().setLogo(R.drawable.main_icon);
		if (!mGp.settingEnableScheduler) {
			msg_text=msg_pref+getString(R.string.msgs_main_task_scheduler_not_running);
			msg_pref="\n";
//			if (Build.VERSION.SDK_INT>=14)
//				this.getActionBar().setLogo(R.drawable.main_icon_stop);
		}
		if (!isActiveProfileGroupExists()) {
			msg_text+=msg_pref+getString(R.string.msgs_main_task_scheduler_no_valid_profile);
			msg_pref="\n";
//			if (Build.VERSION.SDK_INT>=14)
//				this.getActionBar().setLogo(R.drawable.main_icon_stop);
		}
		if (msg_text.equals("")) {
			tv_msg.setVisibility(TextView.GONE);
		} else {
			tv_msg.setVisibility(TextView.VISIBLE);
			tv_msg.setText(msg_text);
		}
	};
	
	final private boolean isActiveProfileGroupExists() {
		boolean result=false;
		for (int i=0;i<mGp.profileAdapter.getDataListCount();i++) {
			if (mGp.profileAdapter.getDataListItem(i).isProfileGroupActivated()) {
				result=true;
				break;
			}
		}
		return result;
	};
	
	final static private boolean isBooleanDifferent(boolean p1, boolean p2) {
		boolean result=true;
		if (p1 && p2) result=false;
		else if(!p1 && !p2) result=false;
		return result;
	};

	final private void invokeLogFileBrowser() {
		if (DEBUG_ENABLE) mGp.util.addDebugMsg(1,"I","Invoke log file browser.");
		mGp.util.resetLogReceiver(mContext);
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | 
//				Intent.FLAG_ACTIVITY_NEW_TASK);

		intent.setDataAndType(Uri.parse("file://"+
                        mGp.settingLogMsgDir +mGp.settingLogMsgFilename+".txt"),
				"text/plain");
		startActivity(intent);
	};
	
	final private void invokeSettingsActivity() {
		if (DEBUG_ENABLE) mGp.util.addDebugMsg(1,"I","Invoke Settings.");
		Intent intent = new Intent(this, ActivitySettings.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent,0);
	};

	@SuppressWarnings("unused")
	final private void showHistory() {
		if (DEBUG_ENABLE) mGp.util.addDebugMsg(1,"I","Invoke show history.");
		Intent intent = new Intent(this, ActivityTaskStatus.class);
		startActivity(intent);
	};

	final protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (DEBUG_ENABLE) mGp.util.addDebugMsg(1,"I","Return from External activity. ID="+
				requestCode+", result="+resultCode);
		if (requestCode==0) {
			applySettingParms();
		}
	};
	
	final public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			confirmTerminateApplication();
			return true;
			// break;
		default:
			return super.onKeyDown(keyCode, event);
			// break;
		}
	};

	final private void confirmTerminateApplication() {
		if (mGp==null || mGp.util==null) return;
		if (mMainTabHost.getCurrentTab()==0) {
			if (mGp.profileGroupAdapter!=null) {
				if (mGp.profileGroupAdapter.isShowCheckBox()) {
					mGp.profileGroupAdapter.setAllItemSelected(false);
					mGp.profileGroupAdapter.setShowCheckBox(false);
					mGp.profileGroupAdapter.notifyDataSetChanged();
					setGroupContextButtonNormalMode();
					
					setActionBarNormalMode();

					return;
				}
			}
		} else if (mMainTabHost.getCurrentTab()==1) {
			if (mGp.profileAdapter!=null) {
				final String curr_grp=mGp.spinnerProfileGroupSelector.getSelectedItem().toString();
				if (mGp.profileAdapter.isShowCheckBox()) {
					mGp.profileAdapter.setShowCheckBox(false);
					ProfileMaintenance.setAllProfileItemUnChecked(mGp,curr_grp,mGp.profileAdapter,mGp.profileListView);
					mGp.profileAdapter.notifyDataSetChanged();
					setProfileContextButtonNormalMode();
					
					setActionBarNormalMode();
					return;
				} else {
					mMainTabHost.setCurrentTab(0);
					return;
				}
			} else {
				mMainTabHost.setCurrentTab(0);
				return;
			}
		}  
//		NotifyEvent ntfy=new NotifyEvent(this);
//		ntfy.setListener(new NotifyEventListener() {
//			@Override
//			public void negativeResponse(Context arg0, Object[] arg1) {}
//			@Override
//			public void positiveResponse(Context arg0, Object[] arg1) {
//				mGlblParms.util.addLogMsg("I",getString(R.string.msgs_main_termination));
//				mIsApplicationTerminated=true;
//				finish();
//			}
//		});
//		mGlblParms.commonDlg.showCommonDialog(true,"W",
//				getString(R.string.msgs_main_terminate_appl),"",ntfy);
		mGp.util.addLogMsg("I",getString(R.string.msgs_main_termination));
		mIsApplicationTerminated=true;
		finish();
	};

	final private void bindSchedulerService(final NotifyEvent p_ntfy) {
		if (mSvcServer != null) return;
		mGp.util.addDebugMsg(1,"I", "bindScheduler entered");
		
        mSvcConnScheduler = new ServiceConnection(){
        	final public void onServiceConnected(ComponentName name, IBinder service) {
				mGp.util.addDebugMsg(1, "I", "Callback onServiceConnected entered");
    			mSvcServer = ISchedulerClient.Stub.asInterface(service);
				setCallbackListener();
    			if (p_ntfy!=null) p_ntfy.notifyToListener(true, null);
    		}
        	final public void onServiceDisconnected(ComponentName name) {
				mGp.util.addDebugMsg(1, "I", "Callback onServiceDisconnected entered");
    			mSvcServer = null;
    		}
    	};
		Intent intent = new Intent(mContext, SchedulerService.class);
		intent.setAction("Main");
		bindService(intent, mSvcConnScheduler, BIND_AUTO_CREATE);
	};
	
	final private void unbindScheduler() { 
		mGp.util.addDebugMsg(1, "I", "unbindScheduler entered");
		if (mSvcClientCallback!=null) {
			try {
				if (mSvcServer!=null)
					mSvcServer.removeCallBack(mSvcClientCallback);
				mSvcClientCallback=null;
			} catch (RemoteException e) {
				e.printStackTrace();
				mGp.util.addLogMsg("E", "removeListener error :"+e.toString());
			}
		}
		unbindService(mSvcConnScheduler);
	};
	
	private Handler handler = new Handler();
	final private void setCallbackListener() {
		mGp.util.addDebugMsg(1, "I", "setCallbackListener entered");
        mSvcClientCallback = new ISchedulerCallback.Stub() {
        	final public void notifyToClient(String resp_time, final String resp,
					final String grp,final String task, final String shell_cmd,
					final String action, String dialog_id, final int atc,
					final int resp_cd, final String msg) throws RemoteException {
				if (mGp.settingDebugLevel>=2)
					mGp.util.addDebugMsg(2, "I", "Notify received ",
							"Resp=",resp,", Task=",task,", action=",action,", " +
									"dialog_id=",dialog_id);
				handler.post(new Runnable() {
					@Override
                    public void run() {
						refreshActiveTaskStatus(atc);
					}
				});
			}
        };
		try{
			mSvcServer.setCallBack(mSvcClientCallback);
		} catch (RemoteException e){
			e.printStackTrace();
			mGp.util.addLogMsg("E", "setCallbackListener error :"+e.toString());
		}
	};
	
	final private void unsetCallbackListener() {
		try{
			if (mSvcServer!=null) mSvcServer.removeCallBack(mSvcClientCallback);
		} catch (RemoteException e){
			e.printStackTrace();
			mGp.util.addLogMsg("E", "unsetCallbackListener error :"+e.toString());
		}
	};

	final private String[] getActiveTaskList() {
		String[] atl=null;
//		String[] all_task=null;
		try {
			atl=mSvcServer.aidlGetActiveTaskList();
//			all_task=svcServer.aidlGetTaskList();
		} catch (RemoteException e) {
			e.printStackTrace();
			mGp.util.addLogMsg("E", "aidlGetActiveTaskList error :"+e.toString());
		}
		return atl;
	};
	
	final private void refreshActiveTaskStatus(int atc) {
		if (mGp.profileAdapter==null) return;
		String[] atl=null;
		if (atc!=0) atl=getActiveTaskList();
//		if (atl!=null) for (int i=0;i<atl.length;i++) Log.v("","atl="+atl[i]);
		boolean active_task_exists=false;
		for (int j=0;j<mGp.profileAdapter.getDataListCount();j++) {
			if (mGp.profileAdapter.getDataListItem(j).getProfileType()
					.equals(PROFILE_TYPE_TASK)) {
				ProfileListItem tl=mGp.profileAdapter.getDataListItem(j);
				tl.setTaskActive(false);
				if (atl!=null&&atl.length!=0) {
					for (int k=0;k<atl.length;k++) {
						String[] atn=atl[k].split("\t");
						if (mGp.profileAdapter.getDataListItem(j).getProfileGroup().equals(atn[0]) &&
								mGp.profileAdapter.getDataListItem(j).getProfileName().equals(atn[2])) {
							tl.setTaskActive(true);
						}
					}
				}
//				mGlblParms.profileAdapter.replaceDataListItem(j, tl);
				if (tl.isTaskActive()) active_task_exists=true;
			}
		}
		mGp.profileAdapter.notifyDataSetChanged();

		Button btn_cancel=(Button)findViewById(R.id.main_task_cancel);
		if (active_task_exists) {
//			btn_cancel.setEnabled(true);
			btn_cancel.setVisibility(Button.VISIBLE);
		} else {
//			btn_cancel.setEnabled(false);
			btn_cancel.setVisibility(Button.GONE);
		}
	};
	
	final private void setMainViewButtonListener() {
		Button btn_cancel=(Button)findViewById(R.id.main_task_cancel);
		
		btn_cancel.setOnClickListener(new OnClickListener(){
			@Override
			final public void onClick(View v) {
				cancelAllActiveTask(mGp.profileAdapter);
			}
		});
	};
	
	private NotifyEvent setQuickTaskListener() {
		final NotifyEvent ntfy=new NotifyEvent(mContext);
		ntfy.setListener(new NotifyEventListener(){
			@Override
			final public void positiveResponse(Context c, Object[] o) {
				mGp.profileAdapter.updateShowList();
				createProfileGroupList();
				setProfileGroupSelectorListener();
				ProfileMaintenance.putProfileListToService(mGp,mGp.profileAdapter,true);
//				profMaint.saveProfileToFileProfileOnly(false, mGlblParms.profileAdapter, mGlblParms.profileListView, "", "");
//				mGlblParms.util.reBuildTaskExecList();
				setSchedulerStatus();
			}

			@Override
			final public void negativeResponse(Context c, Object[] o) {}
		});
		return ntfy;
	};

	final private void createProfileGroupList() {
		String c_grp="";
		int no_task=0, no_action=0, no_time=0;
		mGp.profileGroupAdapter.clear();
		if (mGp.profileAdapter.getDataListCount()>0) {
			c_grp=mGp.profileAdapter.getDataListItem(0).getProfileGroup();
			boolean activated=mGp.profileAdapter.getDataListItem(0).isProfileGroupActivated();
			for (int i=0;i<mGp.profileAdapter.getDataListCount();i++) {
				if (!mGp.profileAdapter.getDataListItem(i).getProfileGroup().equals(c_grp)) {
					mGp.profileGroupAdapter.add(new ProfileGroupListItem(c_grp,activated,
							no_task,no_action,no_time));
					activated=mGp.profileAdapter.getDataListItem(i).isProfileGroupActivated();
					c_grp=mGp.profileAdapter.getDataListItem(i).getProfileGroup();
					no_task=no_action=no_time=0;
					if (mGp.profileAdapter.getDataListItem(i).getProfileType().equals(PROFILE_TYPE_TASK)) {
						no_task++;	
					} else if (mGp.profileAdapter.getDataListItem(i).getProfileType().equals(PROFILE_TYPE_ACTION)) {
						no_action++;	
					} else if (mGp.profileAdapter.getDataListItem(i).getProfileType().equals(PROFILE_TYPE_TIME)) {
						no_time++;	
					}

				} else {// Count up
					if (mGp.profileAdapter.getDataListItem(i).getProfileType().equals(PROFILE_TYPE_TASK)) {
						no_task++;	
					} else if (mGp.profileAdapter.getDataListItem(i).getProfileType().equals(PROFILE_TYPE_ACTION)) {
						no_action++;	
					} else if (mGp.profileAdapter.getDataListItem(i).getProfileType().equals(PROFILE_TYPE_TIME)) {
						no_time++;	
					}
				}
			}
			mGp.profileGroupAdapter.add(new ProfileGroupListItem(c_grp,activated,
						no_task,no_action,no_time));
			mGp.profileGroupAdapter.sort();
		}
		mGp.profileGroupAdapter.notifyDataSetChanged();
	};
	
	final private void setProfileGroupSelectorListener() {
		mGp.spinnerProfileGroupSelector=(Spinner)mProfileView.findViewById(R.id.main_profedit_profile_group_spinner);
		mGp.adapterProfileGroupSelector= new CustomSpinnerAdapter(mContext, android.R.layout.simple_spinner_item);
        mGp.adapterProfileGroupSelector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGp.spinnerProfileGroupSelector.setPrompt(mContext.getString(R.string.msgs_profile_group_select_profile_group));
        mGp.spinnerProfileGroupSelector.setAdapter(mGp.adapterProfileGroupSelector);
        mGp.adapterProfileGroupSelector.clear();
        
        for (int i=0;i<mGp.profileGroupAdapter.getCount();i++) {
        	mGp.adapterProfileGroupSelector.add(mGp.profileGroupAdapter.getItem(i).getProfileGroupName());
        };

        mGp.spinnerProfileGroupSelector.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			final public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (!mGp.spinnerProfileGroupSelector.getSelectedItem().toString().equals(mGp.profileAdapter.getShowedProfileGroupName())) {
					String c_grp=mGp.spinnerProfileGroupSelector.getSelectedItem().toString();
					ProfileMaintenance.setAllProfileItemUnChecked(mGp,c_grp, mGp.profileAdapter, mGp.profileListView);
					mGp.profileAdapter.setShowedProfileGroupName(c_grp);
					mGp.profileAdapter.updateShowList();
					if (mGp.profileAdapter.isShowCheckBox()) setProfileContextButtonSelectMode();
					else setProfileContextButtonNormalMode();
				}
			}
			@Override
			final public void onNothingSelected(AdapterView<?> arg0) {}
        });
        mGp.spinnerProfileGroupSelector.setSelected(true);
	};

	final private void setProfileFilterSelectorListener() {
		mGp.spinnerProfileFilterSelector=(Spinner)mProfileView.findViewById(R.id.main_profedit_filter_spinner);
		mGp.adapterProfileFilterSelector= new CustomSpinnerAdapter(mContext, android.R.layout.simple_spinner_item);
        mGp.adapterProfileFilterSelector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGp.spinnerProfileFilterSelector.setPrompt(mContext.getString(R.string.msgs_filter_select_filter));
        mGp.spinnerProfileFilterSelector.setAdapter(mGp.adapterProfileFilterSelector);
        mGp.adapterProfileFilterSelector.clear();

        final String flt_type_none=getString(R.string.msgs_filter_type_none);
        final String flt_type_task=getString(R.string.msgs_filter_type_task_profile);
        final String flt_type_action=getString(R.string.msgs_filter_type_action_profile);
        final String flt_type_time=getString(R.string.msgs_filter_type_time_profile);
        final String flt_type_time_event=getString(R.string.msgs_filter_type_time_event_task);
        
        mGp.adapterProfileFilterSelector.add(flt_type_none);
        mGp.adapterProfileFilterSelector.add(flt_type_task);
        mGp.adapterProfileFilterSelector.add(flt_type_action);
        mGp.adapterProfileFilterSelector.add(flt_type_time);
        mGp.adapterProfileFilterSelector.add(flt_type_time_event);
        ArrayList<String> bevt=ProfileUtilities.buildBuiltinEventList();
        for (int i=0;i<bevt.size();i++) 
        	mGp.adapterProfileFilterSelector.add(bevt.get(i));
        mGp.spinnerProfileFilterSelector.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			final public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String sel=mGp.spinnerProfileFilterSelector.getSelectedItem().toString();
				String sel_id="";
				if (sel.equals(flt_type_none)) mGp.profileAdapter.setSelectedFilter("");
				else {
					if (sel.equals(flt_type_task)) sel_id=PROFILE_TYPE_TASK;
					else if (sel.equals(flt_type_action)) sel_id=PROFILE_TYPE_ACTION;
					else if (sel.equals(flt_type_time)) sel_id=PROFILE_TYPE_TIME;
					else if (sel.equals(flt_type_time_event)) sel_id=PROFILE_FILTER_TIME_EVENT_TASK;
					else sel_id=sel;
					mGp.profileAdapter.setSelectedFilter(sel_id);
				}
				mGp.profileAdapter.updateShowList();
			}
			@Override
			final public void onNothingSelected(AdapterView<?> arg0) {}
        });
        mGp.spinnerProfileFilterSelector.setSelected(true);
        mGp.spinnerProfileFilterSelector.setSelection(0);
	};

	final private void setProfileGroupTabListClickListener() {
		mGp.profileGroupListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				if (mGp==null || mGp.profileGroupAdapter==null) return;
				if (!mGp.profileGroupAdapter.isShowCheckBox()) {
//					setGroupContextButtonNormalMode();
					mMainTabHost.setCurrentTabByTag("Prof");
					mGp.profileGroupListView.setEnabled(false);
					for (int i=0;i<mGp.adapterProfileGroupSelector.getCount();i++) {
						if (mGp.profileGroupAdapter.getItem(pos).getProfileGroupName().equals(
								mGp.adapterProfileGroupSelector.getItem(i))) {
							mGp.spinnerProfileGroupSelector.setSelection(i);
							break;
						}
					}
					Handler hndl=new Handler();
					hndl.postDelayed(new Runnable(){
						@Override
						public void run() {
							mGp.profileGroupListView.setEnabled(true);
						}
					}, 100);
				} else {
					mGp.profileGroupAdapter.getItem(pos).setSelected(!mGp.profileGroupAdapter.getItem(pos).isSelected());
					mGp.profileGroupAdapter.notifyDataSetChanged();
					setGroupContextButtonSelectMode();
				}
			}
		});
		NotifyEvent ntfy=new NotifyEvent(mContext);
		ntfy.setListener(new NotifyEventListener(){
			@Override
			public void positiveResponse(Context c, Object[] o) {
				if (!mGp.profileGroupAdapter.isShowCheckBox()) {
					setGroupContextButtonNormalMode();
				} else {
					setGroupContextButtonSelectMode();
				}
			}

			@Override
			public void negativeResponse(Context c, Object[] o) {
			}
		});
		mGp.profileGroupAdapter.setNotifyCbClickListener(ntfy);
	};
	
	final private void setProfileGroupTabListLongClickListener() {
		mGp.profileGroupListView
		.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				if (mGp.profileGroupAdapter.isEmptyAdapter()) return true;
				if (!mGp.profileGroupAdapter.getItem(pos).isSelected()) {
					if (mGp.profileGroupAdapter.isAnyItemSelected()) {
						int down_sel_pos=-1, up_sel_pos=-1;
						int tot_cnt=mGp.profileGroupAdapter.getCount();
						if (pos+1<=tot_cnt) {
							for(int i=pos+1;i<tot_cnt;i++) {
								if (mGp.profileGroupAdapter.getItem(i).isSelected()) {
									up_sel_pos=i;
									break;
								}
							}
						}
						if (pos>0) {
							for(int i=pos;i>=0;i--) {
								if (mGp.profileGroupAdapter.getItem(i).isSelected()) {
									down_sel_pos=i;
									break;
								}
							}
						}
//						Log.v("","up="+up_sel_pos+", down="+down_sel_pos);
						if (up_sel_pos!=-1 && down_sel_pos==-1) {
							for (int i=pos;i<up_sel_pos;i++) 
								mGp.profileGroupAdapter.getItem(i).setSelected(true);
						} else if (up_sel_pos!=-1 && down_sel_pos!=-1) {
							for (int i=down_sel_pos+1;i<up_sel_pos;i++) 
								mGp.profileGroupAdapter.getItem(i).setSelected(true);
						} else if (up_sel_pos==-1 && down_sel_pos!=-1) {
							for (int i=down_sel_pos+1;i<=pos;i++) 
								mGp.profileGroupAdapter.getItem(i).setSelected(true);
						}
						mGp.profileGroupAdapter.notifyDataSetChanged();
					} else {
						mGp.profileGroupAdapter.getItem(pos).setSelected(true);
						mGp.profileGroupAdapter.setShowCheckBox(true);
						mGp.profileGroupAdapter.notifyDataSetChanged();
					}
					setGroupContextButtonSelectMode();
				}
				return true;
			}
		});
	};

	private void setGroupContextButtonListener() {
		ImageButton ib_activate=(ImageButton)mGroupView.findViewById(R.id.context_button_activate);
		ImageButton ib_inactivate=(ImageButton)mGroupView.findViewById(R.id.context_button_inactivate);
		ImageButton ib_add_group=(ImageButton)mGroupView.findViewById(R.id.context_button_add_group);
		ImageButton ib_copy=(ImageButton)mGroupView.findViewById(R.id.context_button_copy);
        ImageButton ib_delete=(ImageButton)mGroupView.findViewById(R.id.context_button_delete);
        ImageButton ib_rename=(ImageButton)mGroupView.findViewById(R.id.context_button_rename);
        ImageButton ib_select_all=(ImageButton)mGroupView.findViewById(R.id.context_button_select_all);
        ImageButton ib_unselect_all=(ImageButton)mGroupView.findViewById(R.id.context_button_unselect_all);
        
        final NotifyEvent ntfy_button_listener=new NotifyEvent(mContext);
        ntfy_button_listener.setListener(new NotifyEventListener(){
			@Override
			public void positiveResponse(Context c, Object[] o) {
				if (mGp.profileGroupAdapter.isShowCheckBox()) {
					setGroupContextButtonSelectMode();
				} else {
					setGroupContextButtonNormalMode();
				}
				mGp.profileGroupAdapter.notifyDataSetChanged();
			}

			@Override
			public void negativeResponse(Context c, Object[] o) {
			}
        });
		final NotifyEvent ntfy_unselect=new NotifyEvent(mContext);
		ntfy_unselect.setListener(new NotifyEventListener(){
			@Override
			public void positiveResponse(Context c, Object[] o) {
	  			mGp.profileGroupAdapter.setAllItemSelected(false);
	  			mGp.profileGroupAdapter.setShowCheckBox(false);
	  			
				ntfy_button_listener.notifyToListener(true, null);
			}
			@Override
			public void negativeResponse(Context c, Object[] o) {}
		});
        
        ib_activate.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				confirmGroupActivate(ntfy_unselect);
			}
        });
        ContextButtonUtil.setButtonLabelListener(mContext, ib_activate, 
        		mContext.getString(R.string.msgs_main_context_label_group_activate));
        
        ib_inactivate.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				confirmGroupInactivate(ntfy_unselect);			}
        });
        ContextButtonUtil.setButtonLabelListener(mContext, ib_inactivate, 
        		mContext.getString(R.string.msgs_main_context_label_group_inactivate));

        ib_add_group.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				createNewProfileGroupDlg(ntfy_button_listener);
			}
        });
        ContextButtonUtil.setButtonLabelListener(mContext, ib_add_group, 
        		mContext.getString(R.string.msgs_main_context_label_group_add));

        ib_copy.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				for(int i=0;i<mGp.profileGroupAdapter.getCount();i++) {
					if (mGp.profileGroupAdapter.getItem(i).isSelected()) {
						copyProfileGroupDlg(mGp.profileGroupAdapter.getItem(i).getProfileGroupName(),
								ntfy_unselect);
						break;
					}
				}
			}
        });
        ContextButtonUtil.setButtonLabelListener(mContext, ib_copy, 
        		mContext.getString(R.string.msgs_main_context_label_group_copy));

        ib_delete.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				deleteProfileGroupDlg(ntfy_unselect);
			}
        });
        ContextButtonUtil.setButtonLabelListener(mContext, ib_delete, 
        		mContext.getString(R.string.msgs_main_context_label_group_delete));

        ib_rename.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				for(int i=0;i<mGp.profileGroupAdapter.getCount();i++) {
					if (mGp.profileGroupAdapter.getItem(i).isSelected()) {
						renameProfileGroupDlg(mGp.profileGroupAdapter.getItem(i).getProfileGroupName(),
								ntfy_unselect);
						break;
					}
				}
			}
        });
        ContextButtonUtil.setButtonLabelListener(mContext, ib_rename, 
        		mContext.getString(R.string.msgs_main_context_label_group_rename));

        ib_select_all.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mGp.profileGroupAdapter.setShowCheckBox(true);
				mGp.profileGroupAdapter.setAllItemSelected(true);
				mGp.profileGroupAdapter.notifyDataSetChanged();
				setGroupContextButtonSelectMode();
			}
        });
        ContextButtonUtil.setButtonLabelListener(mContext, ib_select_all, 
        		mContext.getString(R.string.msgs_main_context_label_select_all));

        ib_unselect_all.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
//				mGlblParms.profileGroupAdapter.setShowCheckBox(true);
				mGp.profileGroupAdapter.setAllItemSelected(false);
				mGp.profileGroupAdapter.notifyDataSetChanged();
				setGroupContextButtonSelectMode();
			}
        });
        ContextButtonUtil.setButtonLabelListener(mContext, ib_unselect_all, 
        		mContext.getString(R.string.msgs_main_context_label_unselect_all));

	};

	private void confirmGroupActivate(final NotifyEvent ntfy_unselect) {
		String list_name="", sep="";
		for(int i=0;i<mGp.profileGroupAdapter.getCount();i++) {
			if (!mGp.profileGroupAdapter.getItem(i).isProfileGroupActivated() && 
					mGp.profileGroupAdapter.getItem(i).isSelected()) {
				list_name+=sep+mGp.profileGroupAdapter.getItem(i).getProfileGroupName();
				sep=",";
			}
		}
		NotifyEvent ntfy_act=new NotifyEvent(mContext);
		ntfy_act.setListener(new NotifyEventListener(){
			@Override
			public void positiveResponse(Context c, Object[] o) {
				for(int i=0;i<mGp.profileGroupAdapter.getCount();i++) {
					if (!mGp.profileGroupAdapter.getItem(i).isProfileGroupActivated() && 
							mGp.profileGroupAdapter.getItem(i).isSelected()) {
			  			ProfileUtilities.setProfileGroupActive(mGp.util, mGp.profileAdapter, 
			  					mGp.profileGroupAdapter.getItem(i).getProfileGroupName(), true);
					}
				}
	  			ProfileMaintenance.putProfileListToService(mGp,mGp.profileAdapter,true);
	  			createProfileGroupList();
	  			ntfy_unselect.notifyToListener(POSITIVE, null);
	  			setSchedulerStatus();
			}

			@Override
			public void negativeResponse(Context c, Object[] o) {}
		});
		mGp.commonDlg.showCommonDialog(true, "W", mContext.getString(R.string.msgs_confirm_activate_group),
				list_name, ntfy_act);
	};

	private void confirmGroupInactivate(final NotifyEvent ntfy_unselect) {
		String list_name="", sep="";
		for(int i=0;i<mGp.profileGroupAdapter.getCount();i++) {
			if (mGp.profileGroupAdapter.getItem(i).isProfileGroupActivated() && 
					mGp.profileGroupAdapter.getItem(i).isSelected()) {
				list_name+=sep+mGp.profileGroupAdapter.getItem(i).getProfileGroupName();
				sep=",";
			}
		}
		NotifyEvent ntfy_act=new NotifyEvent(mContext);
		ntfy_act.setListener(new NotifyEventListener(){
			@Override
			public void positiveResponse(Context c, Object[] o) {
				for(int i=0;i<mGp.profileGroupAdapter.getCount();i++) {
					if (mGp.profileGroupAdapter.getItem(i).isProfileGroupActivated() && 
							mGp.profileGroupAdapter.getItem(i).isSelected()) {
			  			ProfileUtilities.setProfileGroupActive(mGp.util, mGp.profileAdapter, 
			  					mGp.profileGroupAdapter.getItem(i).getProfileGroupName(), false);
					}
				}
	  			ProfileMaintenance.putProfileListToService(mGp,mGp.profileAdapter,true);
	  			createProfileGroupList();
	  			ntfy_unselect.notifyToListener(POSITIVE, null);
	  			setSchedulerStatus();
			}

			@Override
			public void negativeResponse(Context c, Object[] o) {}
		});
		mGp.commonDlg.showCommonDialog(true, "W", mContext.getString(R.string.msgs_confirm_inactivate_group),
				list_name, ntfy_act);
	};

	private void setGroupContextButtonSelectMode() {
        int sel_cnt=mGp.profileGroupAdapter.getItemSelectedCount();
		setActionBarSelectMode(sel_cnt,mGp.profileGroupAdapter.getCount());

		LinearLayout ll_activate=(LinearLayout)mGroupView.findViewById(R.id.context_button_activate_view);
		LinearLayout ll_inactivate=(LinearLayout)mGroupView.findViewById(R.id.context_button_inactivate_view);
		LinearLayout ll_add_group=(LinearLayout)mGroupView.findViewById(R.id.context_button_add_group_view);
		LinearLayout ll_copy=(LinearLayout)mGroupView.findViewById(R.id.context_button_copy_view);
        LinearLayout ll_delete=(LinearLayout)mGroupView.findViewById(R.id.context_button_delete_view);
        LinearLayout ll_rename=(LinearLayout)mGroupView.findViewById(R.id.context_button_rename_view);
        LinearLayout ll_select_all=(LinearLayout)mGroupView.findViewById(R.id.context_button_select_all_view);
        LinearLayout ll_unselect_all=(LinearLayout)mGroupView.findViewById(R.id.context_button_unselect_all_view);

        boolean act_selected=false, inact_selected=false;
        if (sel_cnt>0) {
        	for(int i=0;i<mGp.profileGroupAdapter.getCount();i++) {
        		if (mGp.profileGroupAdapter.getItem(i).isSelected()) {
        			if (mGp.profileGroupAdapter.getItem(i).isProfileGroupActivated()) act_selected=true;
        			else inact_selected=true;
        			if (act_selected && inact_selected) break;
        		}
        	}
        }
        if (inact_selected) ll_activate.setVisibility(LinearLayout.VISIBLE);
        else ll_activate.setVisibility(LinearLayout.GONE);
        
        if (act_selected) ll_inactivate.setVisibility(LinearLayout.VISIBLE);
        else ll_inactivate.setVisibility(LinearLayout.GONE);
        
        ll_add_group.setVisibility(LinearLayout.GONE);
        
        if (!mGp.profileGroupAdapter.isQuickTaskSelected()) {
            if (sel_cnt==1) ll_copy.setVisibility(LinearLayout.VISIBLE);
            else ll_copy.setVisibility(LinearLayout.GONE);
            
            if (sel_cnt>0) ll_delete.setVisibility(LinearLayout.VISIBLE);
            else ll_delete.setVisibility(LinearLayout.GONE);
            
            if (sel_cnt==1) ll_rename.setVisibility(LinearLayout.VISIBLE);
            else ll_rename.setVisibility(LinearLayout.GONE);
        } else {
        	ll_copy.setVisibility(LinearLayout.GONE);
        	ll_delete.setVisibility(LinearLayout.GONE);
        	ll_rename.setVisibility(LinearLayout.GONE);
        }
        
        ll_select_all.setVisibility(LinearLayout.VISIBLE);
        if (mGp.profileGroupAdapter.isAnyItemSelected()) ll_unselect_all.setVisibility(LinearLayout.VISIBLE);
        else ll_unselect_all.setVisibility(LinearLayout.GONE);
	};

	private void setActionBarSelectMode(int sel_cnt, int total_cnt) {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
        String sel_txt=""+sel_cnt+"/"+total_cnt;
        actionBar.setTitle(sel_txt);
	};

	private void setActionBarNormalMode() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(R.string.app_name);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(false);
	};

	private void setGroupContextButtonHide() {
		setActionBarNormalMode();
		
		if (mGp.profileGroupAdapter!=null) {
			mGp.profileGroupAdapter.setAllItemSelected(false);
			mGp.profileGroupAdapter.setShowCheckBox(false);
			mGp.profileGroupAdapter.notifyDataSetChanged();
		}

		LinearLayout ll_activate=(LinearLayout)mGroupView.findViewById(R.id.context_button_activate_view);
		LinearLayout ll_inactivate=(LinearLayout)mGroupView.findViewById(R.id.context_button_inactivate_view);
		LinearLayout ll_add_group=(LinearLayout)mGroupView.findViewById(R.id.context_button_add_group_view);
		LinearLayout ll_copy=(LinearLayout)mGroupView.findViewById(R.id.context_button_copy_view);
        LinearLayout ll_delete=(LinearLayout)mGroupView.findViewById(R.id.context_button_delete_view);
        LinearLayout ll_rename=(LinearLayout)mGroupView.findViewById(R.id.context_button_rename_view);
        LinearLayout ll_select_all=(LinearLayout)mGroupView.findViewById(R.id.context_button_select_all_view);
        LinearLayout ll_unselect_all=(LinearLayout)mGroupView.findViewById(R.id.context_button_unselect_all_view);

        ll_activate.setVisibility(LinearLayout.GONE);
        ll_inactivate.setVisibility(LinearLayout.GONE);
        ll_add_group.setVisibility(LinearLayout.GONE);
        ll_copy.setVisibility(LinearLayout.GONE);
        ll_delete.setVisibility(LinearLayout.GONE);
        ll_rename.setVisibility(LinearLayout.GONE);
        ll_select_all.setVisibility(LinearLayout.GONE);
        ll_unselect_all.setVisibility(LinearLayout.GONE);
	};

	private void setGroupContextButtonNormalMode() {
		setActionBarNormalMode();

		mGp.profileGroupAdapter.setAllItemSelected(false);
		mGp.profileGroupAdapter.setShowCheckBox(false);
		mGp.profileGroupAdapter.notifyDataSetChanged();

		LinearLayout ll_activate=(LinearLayout)mGroupView.findViewById(R.id.context_button_activate_view);
		LinearLayout ll_inactivate=(LinearLayout)mGroupView.findViewById(R.id.context_button_inactivate_view);
		LinearLayout ll_add_group=(LinearLayout)mGroupView.findViewById(R.id.context_button_add_group_view);
		LinearLayout ll_copy=(LinearLayout)mGroupView.findViewById(R.id.context_button_copy_view);
        LinearLayout ll_delete=(LinearLayout)mGroupView.findViewById(R.id.context_button_delete_view);
        LinearLayout ll_rename=(LinearLayout)mGroupView.findViewById(R.id.context_button_rename_view);
        LinearLayout ll_select_all=(LinearLayout)mGroupView.findViewById(R.id.context_button_select_all_view);
        LinearLayout ll_unselect_all=(LinearLayout)mGroupView.findViewById(R.id.context_button_unselect_all_view);

		if (mGp.profileGroupAdapter.isEmptyAdapter())  {
	        ll_activate.setVisibility(LinearLayout.GONE);
	        ll_inactivate.setVisibility(LinearLayout.GONE);
	        ll_add_group.setVisibility(LinearLayout.VISIBLE);
	        ll_copy.setVisibility(LinearLayout.GONE);
	        ll_delete.setVisibility(LinearLayout.GONE);
	        ll_rename.setVisibility(LinearLayout.GONE);
	        ll_select_all.setVisibility(LinearLayout.GONE);
	        ll_unselect_all.setVisibility(LinearLayout.GONE);
		} else {
	        ll_activate.setVisibility(LinearLayout.GONE);
	        ll_inactivate.setVisibility(LinearLayout.GONE);
	        ll_add_group.setVisibility(LinearLayout.VISIBLE);
	        ll_copy.setVisibility(LinearLayout.GONE);
	        ll_delete.setVisibility(LinearLayout.GONE);
	        ll_rename.setVisibility(LinearLayout.GONE);
	        ll_select_all.setVisibility(LinearLayout.VISIBLE);
	        ll_unselect_all.setVisibility(LinearLayout.GONE);
		}
	};

	
	final private void createNewProfileGroupDlg(final NotifyEvent p_ntfy) {
		// カスタムダイアログの生成
		final Dialog dialog = new Dialog(mContext);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCanceledOnTouchOutside(false);
//		dialog.getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		dialog.setContentView(R.layout.single_item_input_dlg);
		final TextView dlg_title = (TextView) dialog.findViewById(R.id.single_item_input_title);		
		final EditText dlg_et_name = (EditText) dialog.findViewById(R.id.single_item_input_dir);
//		final TextView dlg_msg = (TextView) dialog.findViewById(R.id.single_item_input_msg);
		final Button btnCancel = (Button) dialog.findViewById(R.id.single_item_input_cancel_btn);
		final Button btnOK = (Button) dialog.findViewById(R.id.single_item_input_ok_btn);
		btnOK.setEnabled(false);
		
		dlg_title.setText(mContext.getString(R.string.msgs_profile_group_create_new_profile_group));
		
		CommonDialog.setDlgBoxSizeCompact(dialog);
		
		dlg_et_name.addTextChangedListener(new TextWatcher() {
    		@Override
    		final public void afterTextChanged(Editable s) {
    			if (s.length()!=0) {
//    				dlg_et_name.selectAll();
    				String newgrp=dlg_et_name.getText().toString();
    				if (!newgrp.startsWith("*")) {
        				btnOK.setEnabled(true);
        				for (int i=0;i<mGp.profileGroupAdapter.getCount();i++) {
        					if (mGp.profileGroupAdapter.getItem(i).getProfileGroupName().equals(newgrp)) {
        						btnOK.setEnabled(false);
        						break;
        					}  
        				}
    				} else btnOK.setEnabled(false);
    			} else btnOK.setEnabled(false);
    		}
    		@Override
    		final public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
    		@Override
    		final public void onTextChanged(CharSequence s, int start, int before,int count) {}
    	});
		
		final NotifyEvent ntfy=new NotifyEvent(mContext);
		ntfy.setListener(new NotifyEventListener(){
			@Override
			final public void positiveResponse(Context c, Object[] o) {
				dlg_et_name.selectAll();
				String newloc=dlg_et_name.getText().toString();
				mGp.profileGroupAdapter.add(new ProfileGroupListItem(newloc,false,0,0,0));
				mGp.profileGroupAdapter.sort();
				mGp.profileGroupAdapter.notifyDataSetChanged();
				
				ProfileListItem tpli= new ProfileListItem();
				tpli.setTaskEntry(PROFILE_VERSION_CURRENT,newloc,false,System.currentTimeMillis(),"",
						mContext.getString(R.string.msgs_no_profile_entry),
						"","","","",null,null);
				mGp.profileAdapter.addDataListItem(tpli);
				mGp.profileAdapter.updateShowList();
				
				setProfileGroupSelectorListener();
			}
			@Override
			final public void negativeResponse(Context c, Object[] o) {}
			
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
				ntfy.notifyToListener(POSITIVE, null);
				dialog.dismiss();
				p_ntfy.notifyToListener(POSITIVE, null);
			}
		});
		// Cancelリスナーの指定
		dialog.setOnCancelListener(new Dialog.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				btnCancel.performClick();
			}
		});
//		dialog.setOnKeyListener(new DialogOnKeyListener(context));
//		dialog.setCancelable(false);
		dialog.show();
	};

	final private void renameProfileGroupDlg(final String old_loc, final NotifyEvent p_ntfy) {
		// カスタムダイアログの生成
		final Dialog dialog = new Dialog(mContext);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCanceledOnTouchOutside(false);
//		dialog.getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		dialog.setContentView(R.layout.single_item_input_dlg);
		final TextView dlg_title = (TextView) dialog.findViewById(R.id.single_item_input_title);		
		final EditText dlg_et_name = (EditText) dialog.findViewById(R.id.single_item_input_dir);
//		final TextView dlg_msg = (TextView) dialog.findViewById(R.id.single_item_input_msg);
		final Button btnCancel = (Button) dialog.findViewById(R.id.single_item_input_cancel_btn);
		final Button btnOK = (Button) dialog.findViewById(R.id.single_item_input_ok_btn);
		btnOK.setEnabled(false);
		
		dlg_title.setText(mContext.getString(R.string.msgs_profile_group_rename_profile_group));
		
		CommonDialog.setDlgBoxSizeCompact(dialog);
		
		dlg_et_name.setText(old_loc);
		dlg_et_name.addTextChangedListener(new TextWatcher() {
    		@Override
    		final public void afterTextChanged(Editable s) {
    			if (s.length()!=0) {
//    				dlg_et_name.selectAll();
    				String newgrp=dlg_et_name.getText().toString();
    				if (!newgrp.startsWith("*")) {
        				btnOK.setEnabled(true);
        				for (int i=0;i<mGp.profileGroupAdapter.getCount();i++) {
        					if (mGp.profileGroupAdapter.getItem(i).getProfileGroupName().equals(newgrp)) {
        						btnOK.setEnabled(false);
        						break;
        					}  
        				}
    				} else btnOK.setEnabled(false);
    			} else btnOK.setEnabled(false);
    		}
    		@Override
    		final public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
    		@Override
    		final public void onTextChanged(CharSequence s, int start, int before,int count) {}
    	});
		
		final NotifyEvent ntfy=new NotifyEvent(mContext);
		ntfy.setListener(new NotifyEventListener(){
			@Override
			final public void positiveResponse(Context c, Object[] o) {
				dlg_et_name.selectAll();
				String new_loc=dlg_et_name.getText().toString();
				renameProfileGroup(true,old_loc,new_loc);
			}
			@Override
			final public void negativeResponse(Context c, Object[] o) {}
			
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
				ntfy.notifyToListener(POSITIVE, null);
				dialog.dismiss();
				p_ntfy.notifyToListener(POSITIVE, null);
			}
		});
		// Cancelリスナーの指定
		dialog.setOnCancelListener(new Dialog.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				btnCancel.performClick();
			}
		});
//		dialog.setOnKeyListener(new DialogOnKeyListener(context));
//		dialog.setCancelable(false);
		dialog.show();
	};

	final private void renameProfileGroup(boolean update_req,String old_loc, String new_loc) {
		mGp.util.addDebugMsg(2, "I", "renameProfileGroup entered, update="+update_req+
				", old="+old_loc+", new="+new_loc);
		boolean pga=ProfileUtilities.isProfileGroupActive(mGp.util,mGp.profileAdapter, old_loc);
		for (int i=mGp.profileAdapter.getDataListCount()-1;i>=0;i--) {
			ProfileListItem pli=mGp.profileAdapter.getDataListItem(i);
			if (pli.getProfileGroup().equals(old_loc)) {
				pli.setProfileGroup(new_loc);
				pli.setProfileUpdateTime(System.currentTimeMillis());
			}
		}
		mGp.profileAdapter.sort();
		if (update_req) {
			mGp.profileAdapter.updateShowList();
			createProfileGroupList();
			setProfileGroupSelectorListener();
			ProfileMaintenance.putProfileListToService(mGp,mGp.profileAdapter,pga);
//			profMaint.saveProfileToFileProfileOnly(false, mGlblParms.profileAdapter, mGlblParms.profileListView, "", "");
			if (pga) {
//				mGlblParms.util.reBuildTaskExecList();
	  			setSchedulerStatus();
			}
		}
	};
	
	final private void copyProfileGroupDlg(final String old_loc, final NotifyEvent p_ntfy) {
		// カスタムダイアログの生成
		final Dialog dialog = new Dialog(mContext);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCanceledOnTouchOutside(false);
//		dialog.getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		dialog.setContentView(R.layout.single_item_input_dlg);
		final TextView dlg_title = (TextView) dialog.findViewById(R.id.single_item_input_title);		
		final EditText dlg_et_name = (EditText) dialog.findViewById(R.id.single_item_input_dir);
//		final TextView dlg_msg = (TextView) dialog.findViewById(R.id.single_item_input_msg);
		final Button btnCancel = (Button) dialog.findViewById(R.id.single_item_input_cancel_btn);
		final Button btnOK = (Button) dialog.findViewById(R.id.single_item_input_ok_btn);
		btnOK.setEnabled(false);

		dlg_title.setText(mContext.getString(R.string.msgs_profile_group_copy_profile_group));
		
		CommonDialog.setDlgBoxSizeCompact(dialog);
		
		dlg_et_name.setText(old_loc);
		dlg_et_name.addTextChangedListener(new TextWatcher() {
    		@Override
    		final public void afterTextChanged(Editable s) {
    			if (s.length()!=0) {
//    				dlg_et_name.selectAll();
    				String newgrp=dlg_et_name.getText().toString();
    				if (!newgrp.startsWith("*")) {
        				btnOK.setEnabled(true);
        				for (int i=0;i<mGp.profileGroupAdapter.getCount();i++) {
        					if (mGp.profileGroupAdapter.getItem(i).getProfileGroupName().equals(newgrp)) {
        						btnOK.setEnabled(false);
        						break;
        					}  
        				}
    				} else btnOK.setEnabled(false);
    			} else btnOK.setEnabled(false);
    		}
    		@Override
    		final public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
    		@Override
    		final public void onTextChanged(CharSequence s, int start, int before,int count) {}
    	});
		
		final NotifyEvent ntfy=new NotifyEvent(mContext);
		ntfy.setListener(new NotifyEventListener(){
			@Override
			final public void positiveResponse(Context c, Object[] o) {
				dlg_et_name.selectAll();
				String new_loc=dlg_et_name.getText().toString();
				copyProfileGroup(true,old_loc,new_loc);
			}
			@Override
			final public void negativeResponse(Context c, Object[] o) {}
			
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
				ntfy.notifyToListener(POSITIVE, null);
				dialog.dismiss();
				p_ntfy.notifyToListener(POSITIVE, null);
			}
		});
		// Cancelリスナーの指定
		dialog.setOnCancelListener(new Dialog.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				btnCancel.performClick();
			}
		});
//		dialog.setOnKeyListener(new DialogOnKeyListener(context));
//		dialog.setCancelable(false);
		dialog.show();
	};

	final private void copyProfileGroup(boolean update_req, final String from_loc, final String to_loc) {
		mGp.util.addDebugMsg(2, "I", "copyProfileGroup entered, update="+update_req+
				", old="+from_loc+", new="+to_loc);
		for (int i=mGp.profileAdapter.getDataListCount()-1;i>=0;i--) {
//			ProfileListItem pli=mGlblParms.profileAdapter.getDataListItem(i);
			ProfileListItem pli=
					mGp.profileAdapter.getDataListItem(i).clone();
			if (pli.getProfileGroup().equals(from_loc)) {
				pli.setProfileGroup(to_loc);
				pli.setProfileGroupShowed(false);
				pli.setProfileGroupActivated(false);
				mGp.profileAdapter.addDataListItem(pli);
			}
		}
		mGp.profileAdapter.sort();
		if (update_req) {
			mGp.profileAdapter.updateShowList();
			createProfileGroupList();
			setProfileGroupSelectorListener();
			ProfileMaintenance.putProfileListToService(mGp,mGp.profileAdapter,false);
//			profMaint.saveProfileToFileProfileOnly(false, mGlblParms.profileAdapter, mGlblParms.profileListView, "", "");
		}
	};
	
	final private void deleteProfileGroupDlg(final NotifyEvent p_ntfy) {
		String msg_list="", sep="";
		final String[] del_list=new String[mGp.profileGroupAdapter.getItemSelectedCount()];
		int idx=0;
		for(int i=0;i<mGp.profileGroupAdapter.getCount();i++) {
			if (mGp.profileGroupAdapter.getItem(i).isSelected()) {
				msg_list+=sep+mGp.profileGroupAdapter.getItem(i).getProfileGroupName();
				sep=", ";
				del_list[idx]=mGp.profileGroupAdapter.getItem(i).getProfileGroupName();
				idx++;
			}
		}

		final NotifyEvent ntfy=new NotifyEvent(mContext);
		ntfy.setListener(new NotifyEventListener(){
			@Override
			final public void positiveResponse(Context c, Object[] o) {
				boolean pga=false;
				for(int i=0;i<del_list.length;i++) {
//					deleteProfileGroup(false,del_list[i]);
					pga=ProfileUtilities.isProfileGroupActive(mGp.util,mGp.profileAdapter, del_list[i]);
					ProfileUtilities.deleteProfileGroup(mGp.util,mGp.profileAdapter, del_list[i]);
				}
				mGp.profileAdapter.updateShowList();
				createProfileGroupList();
				setProfileGroupSelectorListener();
				ProfileMaintenance.putProfileListToService(mGp,mGp.profileAdapter,pga);
//				profMaint.saveProfileToFileProfileOnly(false, mGlblParms.profileAdapter, mGlblParms.profileListView, "", "");
				if (pga) {
//					mGlblParms.util.reBuildTaskExecList();
		  			setSchedulerStatus();
				}
				
				p_ntfy.notifyToListener(POSITIVE, null);
			}
			@Override
			final public void negativeResponse(Context c, Object[] o) {}
			
		});
		mGp.commonDlg.showCommonDialog(true, "W", 
				getString(R.string.msgs_profile_group_delete_confirm), msg_list, ntfy);
	};

//	final private void deleteProfileGroup(boolean update_req,String del_loc) {
//		mGlblParms.util.addDebugMsg(2, "I", "deleteProfileGroup entered, update="+update_req+
//				", del="+del_loc);
//		boolean pga=ProfileUtilities.isProfileGroupActive(mGlblParms.util,mGlblParms.profileAdapter, del_loc);
//		ProfileUtilities.deleteProfileGroup(mGlblParms.util,mGlblParms.profileAdapter, del_loc);
//		if (update_req) {
//			mGlblParms.profileAdapter.updateShowList();
//			createProfileGroupList();
//			setProfileGroupSelectorListener();
//			ProfileMaintenance.putProfileListToService(mGlblParms,mGlblParms.profileAdapter,pga);
////			profMaint.saveProfileToFileProfileOnly(false, mGlblParms.profileAdapter, mGlblParms.profileListView, "", "");
//			if (pga) {
////				mGlblParms.util.reBuildTaskExecList();
//	  			setSchedulerStatus();
//			}
//		}
//	};
	
	final private void setProfileItemClickListner() {
		mGp.profileListView
		.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		@Override
		final public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			ProfileListItem item = mGp.profileAdapter.getProfItem(position);
			mGp.util.addDebugMsg(1,"I","Profilelist item Clicked :" + item.getProfileName());
			if (!mGp.profileAdapter.isShowCheckBox()) {
				mGp.profileListView.setEnabled(false);
				String curr_grp=item.getProfileGroup();
                boolean selected=false;
                for (int i=0;i<mGp.profileAdapter.getProfItemCount();i++) {
                    if (mGp.profileAdapter.getProfItem(i).isProfileItemSelected()) {
                        selected=true;
                        break;
                    }
                }
                if (!selected) {
                    if (item.getProfileType().equals(PROFILE_TYPE_TASK)) {
                        ProfileMaintenanceTaskProfile pmtp=ProfileMaintenanceTaskProfile.newInstance();
                        pmtp.showDialog(getSupportFragmentManager(), pmtp, "EDIT",curr_grp,item, null);
                    } else if (item.getProfileType().equals(PROFILE_TYPE_TIME)){
                        ProfileMaintenanceTimeProfile pmip=ProfileMaintenanceTimeProfile.newInstance();
                        pmip.showDialog(getSupportFragmentManager(), pmip, "EDIT",curr_grp,item, null);
                    } else if (item.getProfileType().equals(PROFILE_TYPE_ACTION)){
                        ProfileMaintenanceActionProfile pmap=ProfileMaintenanceActionProfile.newInstance();
                        pmap.showDialog(getSupportFragmentManager(), pmap, "EDIT",curr_grp,item, null);
                    }
                } else {
                    item = mGp.profileAdapter.getProfItem(position);
                    if (item.isProfileItemSelected()) {
                        item.setProfileItemSelected(false);
                        mGp.profileAdapter.notifyDataSetChanged();
                    } else {
                        item.setProfileItemSelected(true);
                        mGp.profileAdapter.notifyDataSetChanged();
                    }
                }

                Handler hndl=new Handler();
				hndl.postDelayed(new Runnable(){
					@Override
					public void run() {
						mGp.profileListView.setEnabled(true);
					}
				}, 100);
			} else {
				item.setProfileItemSelected(!item.isProfileItemSelected());
				mGp.profileAdapter.notifyDataSetChanged();
				setProfileContextButtonSelectMode();
			}
		}});
		
		NotifyEvent cb_ntfy=new NotifyEvent(mContext);
		cb_ntfy.setListener(new NotifyEventListener(){
			@Override
			public void positiveResponse(Context c, Object[] o) {
				if (mGp.profileAdapter.isShowCheckBox()) setProfileContextButtonSelectMode();
			}

			@Override
			public void negativeResponse(Context c, Object[] o) {}
			
		});
		mGp.profileAdapter.setNotifyCheckBoxClickListener(cb_ntfy);
	};

	final private void setProfilelistLongClickListener() {
		mGp.profileListView
		.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			final public boolean onItemLongClick(AdapterView<?> arg0, View view,
					int pos, long arg3) {
				if (mGp.profileAdapter.isEmptyAdapter()) return true;
				if (!mGp.profileAdapter.getProfItem(pos).isProfileItemSelected()) {
					if (mGp.profileAdapter.getProfItemSelectedCount()>0) {
						int down_sel_pos=-1, up_sel_pos=-1;
						int tot_cnt=mGp.profileAdapter.getCount();
						if (pos+1<=tot_cnt) {
							for(int i=pos+1;i<tot_cnt;i++) {
								if (mGp.profileAdapter.getProfItem(i).isProfileItemSelected()) {
									up_sel_pos=i;
									break;
								}
							}
						}
						if (pos>0) {
							for(int i=pos;i>=0;i--) {
								if (mGp.profileAdapter.getProfItem(i).isProfileItemSelected()) {
									down_sel_pos=i;
									break;
								}
							}
						}
//						Log.v("","up="+up_sel_pos+", down="+down_sel_pos);
						if (up_sel_pos!=-1 && down_sel_pos==-1) {
							for (int i=pos;i<up_sel_pos;i++) 
								mGp.profileAdapter.getProfItem(i).setProfileItemSelected(true);
						} else if (up_sel_pos!=-1 && down_sel_pos!=-1) {
							for (int i=down_sel_pos+1;i<up_sel_pos;i++) 
								mGp.profileAdapter.getProfItem(i).setProfileItemSelected(true);
						} else if (up_sel_pos==-1 && down_sel_pos!=-1) {
							for (int i=down_sel_pos+1;i<=pos;i++) 
								mGp.profileAdapter.getProfItem(i).setProfileItemSelected(true);
						}
						mGp.profileAdapter.notifyDataSetChanged();
					} else {
						mGp.profileAdapter.setShowCheckBox(true);
						mGp.profileAdapter.getProfItem(pos).setProfileItemSelected(true);
						mGp.profileAdapter.notifyDataSetChanged();
					}
					setProfileContextButtonSelectMode();
				}
				return true;
			}
		});
	};
	
	private void setProfileContextButtonListener() {
		ImageButton ib_activate=(ImageButton)mProfileView.findViewById(R.id.context_button_activate);
		ImageButton ib_inactivate=(ImageButton)mProfileView.findViewById(R.id.context_button_inactivate);
		ImageButton ib_add_task=(ImageButton)mProfileView.findViewById(R.id.context_button_add_task);
		ImageButton ib_add_timer=(ImageButton)mProfileView.findViewById(R.id.context_button_add_timer);
		ImageButton ib_add_action=(ImageButton)mProfileView.findViewById(R.id.context_button_add_action);
		ImageButton ib_copy=(ImageButton)mProfileView.findViewById(R.id.context_button_copy);
        ImageButton ib_delete=(ImageButton)mProfileView.findViewById(R.id.context_button_delete);
        ImageButton ib_rename=(ImageButton)mProfileView.findViewById(R.id.context_button_rename);
        ImageButton ib_select_all=(ImageButton)mProfileView.findViewById(R.id.context_button_select_all);
        ImageButton ib_unselect_all=(ImageButton)mProfileView.findViewById(R.id.context_button_unselect_all);
        
		final NotifyEvent ntfy_completion=new NotifyEvent(mContext);
		ntfy_completion.setListener(new NotifyEventListener(){
			@Override
			public void positiveResponse(Context c, Object[] o) {
	  			mGp.profileAdapter.setAllProfItemSelected(false);
	  			mGp.profileAdapter.setShowCheckBox(false);
	  			setProfileContextButtonNormalMode();
			}
			@Override
			public void negativeResponse(Context c, Object[] o) {}
		});

        ib_activate.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				confirmProfileActivate(ntfy_completion);
			}
        });
        ContextButtonUtil.setButtonLabelListener(mContext, ib_activate, 
        		mContext.getString(R.string.msgs_main_context_label_profile_activate));

        ib_inactivate.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				confirmProfileInactivate(ntfy_completion);
			}
        });
        ContextButtonUtil.setButtonLabelListener(mContext, ib_inactivate, 
        		mContext.getString(R.string.msgs_main_context_label_profile_inactivate));

        ib_add_action.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String curr_grp=mGp.profileAdapter.getShowedProfileGroupName();
				ProfileMaintenanceActionProfile pmap=ProfileMaintenanceActionProfile.newInstance();
				pmap.showDialog(getSupportFragmentManager(), pmap, "ADD",curr_grp, null, ntfy_completion);
				ProfileMaintenance.setAllProfileItemUnChecked(mGp,curr_grp,mGp.profileAdapter,mGp.profileListView);
			}
        });
        ContextButtonUtil.setButtonLabelListener(mContext, ib_add_action, 
        		mContext.getString(R.string.msgs_main_context_label_profile_add_action));

        ib_add_timer.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String curr_grp=mGp.profileAdapter.getShowedProfileGroupName();
				ProfileMaintenanceTimeProfile pmip=ProfileMaintenanceTimeProfile.newInstance();
				pmip.showDialog(getSupportFragmentManager(), pmip, "ADD",curr_grp,null, ntfy_completion);
				ProfileMaintenance.setAllProfileItemUnChecked(mGp,curr_grp,mGp.profileAdapter,mGp.profileListView);				
			}
        });
        ContextButtonUtil.setButtonLabelListener(mContext, ib_add_timer, 
        		mContext.getString(R.string.msgs_main_context_label_profile_add_timer));

        ib_add_task.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String curr_grp=mGp.profileAdapter.getShowedProfileGroupName();
				ProfileMaintenanceTaskProfile pmtp=ProfileMaintenanceTaskProfile.newInstance();
				pmtp.showDialog(getSupportFragmentManager(), pmtp, "ADD",curr_grp,null, ntfy_completion);
				ProfileMaintenance.setAllProfileItemUnChecked(mGp,curr_grp,mGp.profileAdapter,mGp.profileListView);
			}
        });
        ContextButtonUtil.setButtonLabelListener(mContext, ib_add_task, 
        		mContext.getString(R.string.msgs_main_context_label_profile_add_task));

        ib_copy.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String curr_grp=mGp.profileAdapter.getShowedProfileGroupName();
				for(int i=0;i<mGp.profileAdapter.getProfItemCount();i++) {
					if (mGp.profileAdapter.getProfItem(i).isProfileItemSelected()) {
						ProfileMaintenance.copyProfileDlg(mGp,curr_grp,mGp.profileAdapter,
							mGp.profileListView, mGp.profileAdapter.getProfItem(i),i, ntfy_completion);
						break;
					}
				}
			}
        });
        ContextButtonUtil.setButtonLabelListener(mContext, ib_copy, 
        		mContext.getString(R.string.msgs_main_context_label_profile_copy));

        
        ib_delete.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String curr_grp=mGp.profileAdapter.getShowedProfileGroupName();
				ProfileMaintenance.deleteProfileDlg(mGp,curr_grp,mGp.profileAdapter,mGp.profileListView,
						ntfy_completion);
			}
        });
        ContextButtonUtil.setButtonLabelListener(mContext, ib_delete, 
        		mContext.getString(R.string.msgs_main_context_label_profile_delete));
        
        ib_rename.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String curr_grp=mGp.profileAdapter.getShowedProfileGroupName();
				for(int i=0;i<mGp.profileAdapter.getProfItemCount();i++) {
					if (mGp.profileAdapter.getProfItem(i).isProfileItemSelected()) {
						ProfileMaintenance.renameProfileDlg(mGp,curr_grp,mGp.profileAdapter,
							mGp.profileListView, mGp.profileAdapter.getProfItem(i), i, 
							ntfy_completion);
						break;
					}
				}
			}
        });
        ContextButtonUtil.setButtonLabelListener(mContext, ib_rename, 
        		mContext.getString(R.string.msgs_main_context_label_profile_rename));

        ib_select_all.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mGp.profileAdapter.setShowCheckBox(true);
				mGp.profileAdapter.setAllProfItemSelected(true);
				mGp.profileAdapter.notifyDataSetChanged();
				setProfileContextButtonSelectMode();
			}
        });
        ContextButtonUtil.setButtonLabelListener(mContext, ib_select_all, 
        		mContext.getString(R.string.msgs_main_context_label_select_all));

        ib_unselect_all.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mGp.profileAdapter.setAllProfItemSelected(false);
				mGp.profileAdapter.notifyDataSetChanged();
				setProfileContextButtonSelectMode();
			}
        });
        ContextButtonUtil.setButtonLabelListener(mContext, ib_unselect_all, 
        		mContext.getString(R.string.msgs_main_context_label_unselect_all));

	};
	
	private void confirmProfileActivate(final NotifyEvent ntfy_completion) {
		String list_name="", sep="";
		for(int i=0;i<mGp.profileAdapter.getProfItemCount();i++) {
			if (!mGp.profileAdapter.getProfItem(i).isProfileEnabled() && 
					mGp.profileAdapter.getProfItem(i).isProfileItemSelected()) {
				list_name+=sep+mGp.profileAdapter.getProfItem(i).getProfileName();
				sep=",";
			}
		}
		NotifyEvent ntfy_act=new NotifyEvent(mContext);
		ntfy_act.setListener(new NotifyEventListener(){
			@Override
			public void positiveResponse(Context c, Object[] o) {
				String curr_grp=mGp.profileAdapter.getShowedProfileGroupName();
				ProfileMaintenance.setProfileToEnable(mGp,curr_grp,mGp.profileAdapter,mGp.profileListView);
				ntfy_completion.notifyToListener(true, null);
			}

			@Override
			public void negativeResponse(Context c, Object[] o) {}
		});
		mGp.commonDlg.showCommonDialog(true, "W", mContext.getString(R.string.msgs_confirm_activate_profile),
				list_name, ntfy_act);
	};

	private void confirmProfileInactivate(final NotifyEvent ntfy_completion) {
		String list_name="", sep="";
		for(int i=0;i<mGp.profileAdapter.getProfItemCount();i++) {
			if (mGp.profileAdapter.getProfItem(i).isProfileEnabled() && 
					mGp.profileAdapter.getProfItem(i).isProfileItemSelected()) {
				list_name+=sep+mGp.profileAdapter.getProfItem(i).getProfileName();
				sep=",";
			}
		}
		NotifyEvent ntfy_act=new NotifyEvent(mContext);
		ntfy_act.setListener(new NotifyEventListener(){
			@Override
			public void positiveResponse(Context c, Object[] o) {
				String curr_grp=mGp.profileAdapter.getShowedProfileGroupName();
				ProfileMaintenance.setProfileToDisable(mGp,curr_grp,mGp.profileAdapter,mGp.profileListView);
				ntfy_completion.notifyToListener(true, null);
			}

			@Override
			public void negativeResponse(Context c, Object[] o) {}
		});
		mGp.commonDlg.showCommonDialog(true, "W", mContext.getString(R.string.msgs_confirm_inactivate_profile),
				list_name, ntfy_act);
	};

	private void setProfileContextButtonSelectMode() {
		setActionBarSelectMode(mGp.profileAdapter.getProfItemSelectedCount(),
				mGp.profileAdapter.getProfItemCount());
		LinearLayout ll_activate=(LinearLayout)mProfileView.findViewById(R.id.context_button_activate_view);
		LinearLayout ll_inactivate=(LinearLayout)mProfileView.findViewById(R.id.context_button_inactivate_view);
		LinearLayout ll_add_action=(LinearLayout)mProfileView.findViewById(R.id.context_button_add_action_view);
		LinearLayout ll_add_timer=(LinearLayout)mProfileView.findViewById(R.id.context_button_add_timer_view);
		LinearLayout ll_add_task=(LinearLayout)mProfileView.findViewById(R.id.context_button_add_task_view);
		LinearLayout ll_copy=(LinearLayout)mProfileView.findViewById(R.id.context_button_copy_view);
		ImageButton ib_copy=(ImageButton)mProfileView.findViewById(R.id.context_button_copy);
        LinearLayout ll_delete=(LinearLayout)mProfileView.findViewById(R.id.context_button_delete_view);
        LinearLayout ll_rename=(LinearLayout)mProfileView.findViewById(R.id.context_button_rename_view);
        LinearLayout ll_select_all=(LinearLayout)mProfileView.findViewById(R.id.context_button_select_all_view);
//        ImageButton ib_select_all=(ImageButton)mProfileView.findViewById(R.id.context_button_select_all);
        LinearLayout ll_unselect_all=(LinearLayout)mProfileView.findViewById(R.id.context_button_unselect_all_view);

        if (mGp.profileAdapter.isQuickTaskGroupSelected()) {
	        ll_activate.setVisibility(LinearLayout.GONE);
	        ll_inactivate.setVisibility(LinearLayout.GONE);
	        ll_add_action.setVisibility(LinearLayout.GONE);
	        ll_add_timer.setVisibility(LinearLayout.GONE);
	        ll_add_task.setVisibility(LinearLayout.GONE);
	        ll_copy.setVisibility(LinearLayout.VISIBLE);
	        if (mGp.profileAdapter.getProfItemSelectedCount()==1) {
	        	ib_copy.setEnabled(true);
	        	ib_copy.setImageResource(R.drawable.menu_copy);
	        } else {
	        	ib_copy.setImageResource(R.drawable.menu_copy_disabled);
	        	ib_copy.setEnabled(false);
	        }
	        ll_delete.setVisibility(LinearLayout.GONE);
	        ll_rename.setVisibility(LinearLayout.GONE);
	        ll_select_all.setVisibility(LinearLayout.GONE);
	        ll_unselect_all.setVisibility(LinearLayout.GONE);
        } else {
        	if (mGp.profileAdapter.getProfItemSelectedCount()>0) {
                boolean act_selected=false, inact_selected=false;
            	for(int i=0;i<mGp.profileAdapter.getProfItemCount();i++) {
            		if (mGp.profileAdapter.getProfItem(i).isProfileItemSelected()) {
            			if (mGp.profileAdapter.getProfItem(i).isProfileEnabled()) act_selected=true;
            			else inact_selected=true;
            			if (act_selected && inact_selected) break;
            		}
            	}

                if (inact_selected) ll_activate.setVisibility(LinearLayout.VISIBLE);
                else ll_activate.setVisibility(LinearLayout.GONE);
                
                if (act_selected) ll_inactivate.setVisibility(LinearLayout.VISIBLE);
                else ll_inactivate.setVisibility(LinearLayout.GONE);
                
                ll_add_action.setVisibility(LinearLayout.GONE);
                ll_add_timer.setVisibility(LinearLayout.GONE);
                ll_add_task.setVisibility(LinearLayout.GONE);
                
                if (mGp.profileAdapter.getProfItemSelectedCount()==1) ll_copy.setVisibility(LinearLayout.VISIBLE);
                else ll_copy.setVisibility(LinearLayout.GONE);
                
                ll_delete.setVisibility(LinearLayout.VISIBLE);
                
                if (mGp.profileAdapter.getProfItemSelectedCount()==1) ll_rename.setVisibility(LinearLayout.VISIBLE);
                else ll_rename.setVisibility(LinearLayout.GONE);
                
                ll_select_all.setVisibility(LinearLayout.VISIBLE);
                ll_unselect_all.setVisibility(LinearLayout.VISIBLE);
        	} else {
                ll_activate.setVisibility(LinearLayout.GONE);
                ll_inactivate.setVisibility(LinearLayout.GONE);
                ll_add_action.setVisibility(LinearLayout.GONE);
                ll_add_timer.setVisibility(LinearLayout.GONE);
                ll_add_task.setVisibility(LinearLayout.GONE);
                ll_copy.setVisibility(LinearLayout.GONE);
                ll_delete.setVisibility(LinearLayout.GONE);
                ll_rename.setVisibility(LinearLayout.GONE);
                ll_select_all.setVisibility(LinearLayout.VISIBLE);
                ll_unselect_all.setVisibility(LinearLayout.GONE);
        	}
        }
	};
	
	private void setProfileContextButtonNormalMode() {
		setActionBarNormalMode();
		LinearLayout ll_activate=(LinearLayout)mProfileView.findViewById(R.id.context_button_activate_view);
		LinearLayout ll_inactivate=(LinearLayout)mProfileView.findViewById(R.id.context_button_inactivate_view);
		LinearLayout ll_add_action=(LinearLayout)mProfileView.findViewById(R.id.context_button_add_action_view);
		LinearLayout ll_add_timer=(LinearLayout)mProfileView.findViewById(R.id.context_button_add_timer_view);
		LinearLayout ll_add_task=(LinearLayout)mProfileView.findViewById(R.id.context_button_add_task_view);
		LinearLayout ll_copy=(LinearLayout)mProfileView.findViewById(R.id.context_button_copy_view);
        LinearLayout ll_delete=(LinearLayout)mProfileView.findViewById(R.id.context_button_delete_view);
        LinearLayout ll_rename=(LinearLayout)mProfileView.findViewById(R.id.context_button_rename_view);
        LinearLayout ll_select_all=(LinearLayout)mProfileView.findViewById(R.id.context_button_select_all_view);
        LinearLayout ll_unselect_all=(LinearLayout)mProfileView.findViewById(R.id.context_button_unselect_all_view);

        ll_activate.setVisibility(LinearLayout.GONE);
        ll_inactivate.setVisibility(LinearLayout.GONE);
        ll_add_action.setVisibility(LinearLayout.GONE);
        ll_add_timer.setVisibility(LinearLayout.GONE);
        ll_add_task.setVisibility(LinearLayout.GONE);
        ll_copy.setVisibility(LinearLayout.GONE);
        ll_delete.setVisibility(LinearLayout.GONE);
        ll_rename.setVisibility(LinearLayout.GONE);
        ll_select_all.setVisibility(LinearLayout.GONE);
        ll_unselect_all.setVisibility(LinearLayout.GONE);

        if (mGp.profileAdapter.isQuickTaskGroupSelected()) {
	        ll_activate.setVisibility(LinearLayout.GONE);
	        ll_inactivate.setVisibility(LinearLayout.GONE);
	        ll_add_action.setVisibility(LinearLayout.GONE);
	        ll_add_timer.setVisibility(LinearLayout.GONE);
	        ll_add_task.setVisibility(LinearLayout.GONE);
	        ll_copy.setVisibility(LinearLayout.GONE);
	        ll_delete.setVisibility(LinearLayout.GONE);
	        ll_rename.setVisibility(LinearLayout.GONE);
	        ll_select_all.setVisibility(LinearLayout.GONE);
	        ll_unselect_all.setVisibility(LinearLayout.GONE);
        } else {
    		if (mGp.profileAdapter.isEmptyAdapter())  {
    	        ll_activate.setVisibility(LinearLayout.GONE);
    	        ll_inactivate.setVisibility(LinearLayout.GONE);
    	        ll_add_action.setVisibility(LinearLayout.VISIBLE);
    	        ll_add_timer.setVisibility(LinearLayout.VISIBLE);
    	        ll_add_task.setVisibility(LinearLayout.VISIBLE);
    	        ll_copy.setVisibility(LinearLayout.GONE);
    	        ll_delete.setVisibility(LinearLayout.GONE);
    	        ll_rename.setVisibility(LinearLayout.GONE);
    	        ll_select_all.setVisibility(LinearLayout.GONE);
    	        ll_unselect_all.setVisibility(LinearLayout.GONE);
    		} else {
    			ll_activate.setVisibility(LinearLayout.GONE);
    	        ll_inactivate.setVisibility(LinearLayout.GONE);
    	        ll_add_action.setVisibility(LinearLayout.VISIBLE);
    	        ll_add_timer.setVisibility(LinearLayout.VISIBLE);
    	        ll_add_task.setVisibility(LinearLayout.VISIBLE);
    	        ll_copy.setVisibility(LinearLayout.GONE);
    	        ll_delete.setVisibility(LinearLayout.GONE);
    	        ll_rename.setVisibility(LinearLayout.GONE);
    	        ll_select_all.setVisibility(LinearLayout.VISIBLE);
    	        ll_unselect_all.setVisibility(LinearLayout.GONE);
    		}
        }
	};


	final private void cancelAllActiveTask(final AdapterProfileList tpfa) {
		String tl="";
		for (int i=0;i<tpfa.getDataListCount();i++) {
			ProfileListItem tpli=tpfa.getDataListItem(i);
			if (tpli.isTaskActive()) {
				tl=tl+"\n"+tpli.getProfileGroup()+", "+tpli.getProfileName();
			}
		}
		NotifyEvent ntfy = new NotifyEvent(this);
		ntfy.setListener(new NotifyEventListener() {
			@Override
			final public void positiveResponse(Context c, Object[] o) {
				try {
					mSvcServer.aidlCancelAllActiveTask();
				} catch (RemoteException e) {
					e.printStackTrace();
				}

//				for (int i=0;i<tpfa.getDataListCount();i++) {
//					ProfileListItem tpli=tpfa.getDataListItem(i);
//					if (tpli.isTaskActive()) {
//						try {
//							svcServer.aidlCancelSpecificTask(
//									tpli.getProfileGroup(),tpli.getProfileName());
//						} catch (RemoteException e) {
//							e.printStackTrace();
//						}
//					}
//				}
			}
			@Override
			final public void negativeResponse(Context c, Object[] o) {}
		});
		mGp.commonDlg.showCommonDialog(true,"W",
				getString(R.string.msgs_main_cancel_confirm_msg),
				tl, ntfy);

	};

	@SuppressWarnings("unused")
	final private void cancelSpecificTask(boolean dlg_req, final AdapterProfileList tpfa, 
			final String grp, final String tn) {
		if (dlg_req) {
			NotifyEvent ntfy = new NotifyEvent(this);
			ntfy.setListener(new NotifyEventListener() {
				@Override
				final public void positiveResponse(Context c, Object[] o) {
					try {
						mSvcServer.aidlCancelSpecificTask(grp,tn);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
				@Override
				final public void negativeResponse(Context c, Object[] o) {}
			});
			mGp.commonDlg.showCommonDialog(true,"W",
					getString(R.string.msgs_main_cancel_confirm_msg),
					tn, ntfy);
			
		} else {
			try {
				mSvcServer.aidlCancelSpecificTask(grp, tn);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	};

	final private void saveTaskData() {
		
		ActivityMainDataHolder data = new ActivityMainDataHolder();
		try {
		    FileOutputStream fos=openFileOutput(ACTIVITY_TASK_DATA_FILE_NAME, MODE_PRIVATE);
		    ObjectOutputStream oos = new ObjectOutputStream(fos);
//		    ArrayList<ProfileListItem> tl=new ArrayList<ProfileListItem>();
//		    if (mGlblParms.profileAdapter!=null)
//		    	for (int i=0;i<mGlblParms.profileAdapter.getProfItemCount();i++)
//		    		tl.add(mGlblParms.profileAdapter.getProfItem(i));
//		    data.tpfal=mGlblParms.profileAdapter.getAllDataList();
		    data.aal=new ArrayList<String>();
		    if (mAndroidApplicationList!=null)
		    	data.aal.addAll(mAndroidApplicationList);
		    oos.writeObject(data);
		    oos.close();
		    mGp.util.addDebugMsg(1,"I", "Activity data was saved");
		} catch (Exception e) {
			e.printStackTrace();
			mGp.util.addDebugMsg(1,"E", "saveActivityData error, "+e.getMessage());
		}
	};
	
	final private void restoreTaskData() {
		try {
		    File lf =new File(getFilesDir()+"/"+ACTIVITY_TASK_DATA_FILE_NAME);
//		    FileInputStream fis = openFileInput(SMBSYNC_ACTIVITY_TASK_DATA_FILE_NAME);
		    FileInputStream fis = new FileInputStream(lf); 
		    ObjectInputStream ois = new ObjectInputStream(fis);
		    ActivityMainDataHolder data = (ActivityMainDataHolder) ois.readObject();
//		    profMaint.replacemGlblParms.profileAdapter(mGlblParms.profileAdapter,
//		    	new AdapterProfileList(this, R.layout.task_profile_list_view_item, 
//		    			data.tpfal));
		    mAndroidApplicationList=data.aal;
//		    androidApplicationList=new ArrayList<String>();
//		    androidApplicationList.addAll(data.aal);
		    ois.close();
		    
		    mGp.util.addDebugMsg(1,"I", "Activity data was restored");
		} catch (Exception e) {
			e.printStackTrace();
			mGp.util.addDebugMsg(1,"E","restoreActivityData error, "+e.getMessage());
		}
	};

	final private void deleteTaskData() {
	    File lf =new File(getFilesDir()+"/"+ACTIVITY_TASK_DATA_FILE_NAME);
	    if (lf.exists()) {
		    lf.delete();
		    mGp.util.addDebugMsg(2,"I", "Activity data was deleted");
	    }
	};

    static class ActivityMainDataHolder implements Serializable  {
        private static final long serialVersionUID = 1L;
        //	public ArrayList<ProfileListItem> tpfal;
        public ArrayList<String> aal;
    };

}



package com.sentaroh.android.TaskAutomation.Config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.Configuration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.sentaroh.android.TaskAutomation.ActivityRestartScheduler;
import com.sentaroh.android.TaskAutomation.CommonUtilities;
import com.sentaroh.android.TaskAutomation.GlobalParameters;
import com.sentaroh.android.TaskAutomation.R;
import com.sentaroh.android.TaskAutomation.Common.TrustDeviceItem;
import com.sentaroh.android.Utilities.NotifyEvent;
import com.sentaroh.android.Utilities.ContextButton.ContextButtonUtil;
import com.sentaroh.android.Utilities.Dialog.CommonDialog;
import com.sentaroh.android.Utilities.Dialog.MessageDialogFragment;
import com.sentaroh.android.Utilities.NotifyEvent.NotifyEventListener;

public class EditTrustListFragment extends DialogFragment{
	private final static boolean DEBUG_ENABLE=true;
	private final static String APPLICATION_TAG="TrustListEdit";

	private Dialog mDialog=null;
	private boolean mTerminateRequired=true;
	private EditTrustListFragment mFragment=null;
	private String mDialogTitle=null;
	@SuppressWarnings("unused")
	private GlobalParameters mGlblParms=null;
	
	private AdapterTrustDeviceList mAdapterTrustList=null;
	private ArrayList<TrustDeviceItem> mTrustList=null;
	private ArrayList<TrustDeviceItem> mInitialTrustList=null;
	
	private Context mContext=null;

	public static EditTrustListFragment newInstance(boolean retainInstance, String title) {
		if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"newInstance");
		EditTrustListFragment frag = new EditTrustListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("retainInstance", retainInstance);
        bundle.putString("title", title);
//        bundle.putString("msgtext", msgtext);
        frag.setArguments(bundle);
        return frag;
    }

	public EditTrustListFragment() {
		if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"Constructor(Default)");
	}; 
	
	@Override
	public void onAttach(Activity activity) {
	    super.onAttach(activity);
	    if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"onAttach");
	};

	@Override
	public void onSaveInstanceState(Bundle outState) {  
		super.onSaveInstanceState(outState);
		if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"onSaveInstanceState");
		if(outState.isEmpty()){
	        outState.putBoolean("WORKAROUND_FOR_BUG_19917_KEY", true);
	    }
    	saveViewContents();
	};  
	
	@Override
	public void onConfigurationChanged(final Configuration newConfig) {
	    // Ignore orientation change to keep activity from restarting
	    super.onConfigurationChanged(newConfig);
	    if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"onConfigurationChanged");

	    reInitViewWidget();
	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"onActivityCreated");
	};
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"onCreateView");
    	View view=super.onCreateView(inflater, container, savedInstanceState);
    	return view;
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"onCreate");
        
    	mFragment=this;
        if (!mTerminateRequired) {
            mGlblParms=(GlobalParameters)getActivity().getApplication();

            Bundle bd=getArguments();
            setRetainInstance(bd.getBoolean("retainInstance"));
            mDialogTitle=bd.getString("title");
        	mContext=getActivity().getApplicationContext();
        	
        	mTrustList=CommonUtilities.loadTrustedDeviceList(mContext);
        	mInitialTrustList=CommonUtilities.loadTrustedDeviceList(mContext);
        }
    };
    
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"onCreateDialog");

    	mDialog=new Dialog(getActivity());
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDialog.setCanceledOnTouchOutside(false);

		if (!mTerminateRequired) {
			initViewWidget();
		}
		
        return mDialog;
    };
    
	@Override
	public void onStart() {
    	CommonDialog.setDlgBoxSizeLimit(mDialog,true);
	    super.onStart();
	    if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"onStart");
	    if (mTerminateRequired) mDialog.cancel();
	    else {
	    	mDialog.setOnKeyListener(new OnKeyListener(){
    	        @Override
	    	    public boolean onKey ( DialogInterface dialog , int keyCode , KeyEvent event ){
	    	        // disable search button action
	    	        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN){
	    	        	if (mAdapterTrustList.isShowCheckBox()) {
		    	        	for(int i=0;i<mAdapterTrustList.getCount();i++) {
		    	        		mAdapterTrustList.getItem(i).isSelected=false;
		    	        	}
		    	        	mAdapterTrustList.setShowCheckBox(false);
		    	        	mAdapterTrustList.notifyDataSetChanged();
		    	        	setContextButtonNormalMode(mAdapterTrustList);
		    	        	return true;
	    	        	}
	    	        }
	    	        return false;
	    	    }
	    	});
	    }

	};
	
	@Override
	public void onCancel(DialogInterface di) {
		if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"onCancel");
		mFragment.dismiss();
		super.onCancel(di);
	};
	
	@Override
	public void onDismiss(DialogInterface di) {
		if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"onDismiss");
		super.onDismiss(di);
	};

	@Override
	public void onStop() {
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
	public void onDetach() {
	    super.onDetach();
	    if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"onDetach");
	};


    private void reInitViewWidget() {
    	if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"reInitViewWidget");
    	if (!mTerminateRequired) {
	    	saveViewContents();
	    	initViewWidget();
	    	restoreViewContents();
        	if (mAdapterTrustList.isAnyItemSelected()) {
        		setContextButtonSelectMode(mAdapterTrustList);
        	} else {
        		setContextButtonNormalMode(mAdapterTrustList);
        	}
//    		Handler hndl=new Handler();
//    		hndl.post(new Runnable(){
//				@Override
//				public void run() {
//			    	saveViewContents();
//			    	initViewWidget();
//			    	restoreViewContents();
//		        	if (mAdapterTrustList.isAnyItemSelected()) {
//		        		setContextButtonSelectMode(mAdapterTrustList);
//		        	} else {
//		        		setContextButtonNormalMode(mAdapterTrustList);
//		        	}
//				}
//    		});
    	}
    };
    
    private void saveViewContents() {
    	
    };
    
    private void restoreViewContents() {
    	
    };
    
    private void initViewWidget() {
    	if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"initViewWidget");

    	mDialog.setContentView(R.layout.edit_trust_dev_main_dlg);
    	
    	final TextView dlg_title=(TextView)mDialog.findViewById(R.id.edit_trust_dev_main_dlg_title);
    	dlg_title.setText(mDialogTitle);
    	final TextView dlg_msg=(TextView)mDialog.findViewById(R.id.edit_trust_dev_add_bt_dlg_msg);

    	final ImageButton dlg_done=(ImageButton)mDialog.findViewById(R.id.edit_trust_dev_main_dlg_btn_done);
    	dlg_done.setVisibility(ImageButton.GONE);
    	
    	final ListView lv=(ListView)mDialog.findViewById(R.id.edit_trust_dev_main_dlg_listview);
    	final Button btn_ok=(Button)mDialog.findViewById(R.id.edit_trust_dev_main_dlg_ok);
    	final Button btn_cancel=(Button)mDialog.findViewById(R.id.edit_trust_dev_main_dlg_cancel);

    	NotifyEvent ntfy_cb_listener=new NotifyEvent(mContext);
    	ntfy_cb_listener.setListener(new NotifyEventListener(){
			@Override
			public void positiveResponse(Context c, Object[] o) {
				if (mAdapterTrustList.isShowCheckBox()) {
					setContextButtonSelectMode(mAdapterTrustList);
				}
			};

			@Override
			public void negativeResponse(Context c, Object[] o) {}
    	});

    	mAdapterTrustList=new AdapterTrustDeviceList(mContext, R.layout.edit_trust_dev_item,
    						mTrustList, ntfy_cb_listener);
    	lv.setAdapter(mAdapterTrustList);
    	
    	if (mTrustList.size()>0) {
    		dlg_msg.setText("");
    	} else {
    		dlg_msg.setText(mContext.getString(R.string.msgs_key_guard_trust_device_no_trust_item_msg));
    	}
    	
    	setContextButtonListener();
    	setContextButtonNormalMode(mAdapterTrustList);

    	lv.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				if (mAdapterTrustList.getItem(0).trustedItemName==null) return;
				if (mAdapterTrustList.isShowCheckBox()) {
					mAdapterTrustList.getItem(pos).isSelected=
							!mAdapterTrustList.getItem(pos).isSelected;
					mAdapterTrustList.notifyDataSetChanged();
		        	if (mAdapterTrustList.isAnyItemSelected()) {
		        		setContextButtonSelectMode(mAdapterTrustList);
		        	} else {
		        		setContextButtonNormalMode(mAdapterTrustList);
		        	}
				}
			}
    	});
    	
    	lv.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				if (mAdapterTrustList.isEmptyAdapter()) return true;
				if (!mAdapterTrustList.getItem(pos).isSelected) {
					if (mAdapterTrustList.isAnyItemSelected()) {
						int down_sel_pos=-1, up_sel_pos=-1;
						int tot_cnt=mAdapterTrustList.getCount();
						if (pos+1<=tot_cnt) {
							for(int i=pos+1;i<tot_cnt;i++) {
								if (mAdapterTrustList.getItem(i).isSelected) {
									up_sel_pos=i;
									break;
								}
							}
						}
						if (pos>0) {
							for(int i=pos;i>=0;i--) {
								if (mAdapterTrustList.getItem(i).isSelected) {
									down_sel_pos=i;
									break;
								}
							}
						}
//						Log.v("","up="+up_sel_pos+", down="+down_sel_pos);
						if (up_sel_pos!=-1 && down_sel_pos==-1) {
							for (int i=pos;i<up_sel_pos;i++) 
								mAdapterTrustList.getItem(i).isSelected=true;
						} else if (up_sel_pos!=-1 && down_sel_pos!=-1) {
							for (int i=down_sel_pos+1;i<up_sel_pos;i++) 
								mAdapterTrustList.getItem(i).isSelected=true;
						} else if (up_sel_pos==-1 && down_sel_pos!=-1) {
							for (int i=down_sel_pos+1;i<=pos;i++) 
								mAdapterTrustList.getItem(i).isSelected=true;
						}
						mAdapterTrustList.notifyDataSetChanged();
					} else {
						mAdapterTrustList.setShowCheckBox(true);
						mAdapterTrustList.getItem(pos).isSelected=true;
						mAdapterTrustList.notifyDataSetChanged();
					}
					setContextButtonSelectMode(mAdapterTrustList);
				}
				return true;
			}
    	});
    	
    	btn_ok.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				CommonUtilities.saveTrustedDeviceList(mContext, mAdapterTrustList.getAllItem());
				NotifyEvent ntfy=new NotifyEvent(mContext);
				ntfy.setListener(new NotifyEventListener(){
					@Override
					public void positiveResponse(Context c, Object[] o) {
						mFragment.dismiss();
//						CommonUtilities.restartScheduler(mContext);
			    		Intent in_b=
			    				new Intent(mContext,ActivityRestartScheduler.class);
			    		in_b.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
			    		startActivity(in_b);
					}
					@Override
					public void negativeResponse(Context c, Object[] o) {
						mFragment.dismiss();
					}
				});
		        MessageDialogFragment cdf =MessageDialogFragment.newInstance(true, "W",
		        		mContext.getString(R.string.msgs_key_guard_trust_device_restart_scheduler_required),"");
		        cdf.showDialog(mFragment.getFragmentManager(),cdf,ntfy);

			}
    	});

    	btn_cancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				mFragment.dismiss();
			}
    	});
    	
//    	CommonDialog.setDlgBoxSizeLimit(mDialog, true);
    };

	private void setContextButtonListener() {
		LinearLayout ll_prof=(LinearLayout) mDialog.findViewById(R.id.edit_trust_dev_main_context_view);
		ImageButton ib_add=(ImageButton)ll_prof.findViewById(R.id.context_button_add);
        ImageButton ib_delete=(ImageButton)ll_prof.findViewById(R.id.context_button_delete);
        ImageButton ib_select_all=(ImageButton)ll_prof.findViewById(R.id.context_button_select_all);
        ImageButton ib_unselect_all=(ImageButton)ll_prof.findViewById(R.id.context_button_unselect_all);
        
    	final ImageButton dlg_done=(ImageButton)mDialog.findViewById(R.id.edit_trust_dev_main_dlg_btn_done);
    	dlg_done.setVisibility(ImageButton.VISIBLE);
    	
    	final NotifyEvent ntfy_save_btn=new NotifyEvent(mContext);
    	ntfy_save_btn.setListener(new NotifyEventListener(){
			@Override
			public void positiveResponse(Context c, Object[] o) {
//				setMainSaveButtonEnabled();
				setContextButtonNormalMode(mAdapterTrustList);
			}
			@Override
			public void negativeResponse(Context c, Object[] o) {
			}
    	});

    	dlg_done.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dlg_done.setVisibility(ImageButton.GONE);
				mAdapterTrustList.setAllItemSelected(false);
				mAdapterTrustList.setShowCheckBox(false);
				mAdapterTrustList.notifyDataSetChanged();
				setContextButtonNormalMode(mAdapterTrustList);
			}
    	});
    	
    	ib_add.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				addTrustedDev(ntfy_save_btn);
			}
        });
        ContextButtonUtil.setButtonLabelListener(mContext, ib_add, 
        		mContext.getString(R.string.msgs_key_guard_trust_device_label_add));

        ib_delete.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				confirmDeleteItem(mAdapterTrustList);
			}
        });
        ContextButtonUtil.setButtonLabelListener(mContext, ib_delete, 
        		mContext.getString(R.string.msgs_key_guard_trust_device_label_delete));
        
        ib_select_all.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mAdapterTrustList.setAllItemSelected(true);
				mAdapterTrustList.setShowCheckBox(true);
				setContextButtonSelectMode(mAdapterTrustList);
			}
        });
        ContextButtonUtil.setButtonLabelListener(mContext, ib_select_all, 
        		mContext.getString(R.string.msgs_key_guard_trust_device_label_select_all));

        ib_unselect_all.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mAdapterTrustList.setAllItemSelected(false);
//				mAdapterTrustList.setShowCheckBox(false);
				mAdapterTrustList.notifyDataSetChanged();
				setContextButtonSelectMode(mAdapterTrustList);
			}
        });
        ContextButtonUtil.setButtonLabelListener(mContext, ib_unselect_all, 
        		mContext.getString(R.string.msgs_key_guard_trust_device_label_unselect_all));

	};
	
	private void setMainSaveButtonEnabled() {
		boolean diff=false;
		if (mTrustList.size()==mInitialTrustList.size()) {
			for(int i=0;i<mTrustList.size();i++) {
				if (!mTrustList.get(i).trustedItemName.equals(mInitialTrustList.get(i).trustedItemName) ||
						!mTrustList.get(i).trustedItemAddr.equals(mInitialTrustList.get(i).trustedItemAddr)) {
					diff=true;
					break;
				}
			}
		} else {
			diff=true;
		}
		Button dlg_save=(Button)mDialog.findViewById(R.id.edit_trust_dev_main_dlg_ok);
		if (diff) dlg_save.setEnabled(true);
		else dlg_save.setEnabled(false);
	}
	
	private void setContextButtonSelectMode(AdapterTrustDeviceList lfm_adapter) {
    	final TextView dlg_title=(TextView)mDialog.findViewById(R.id.edit_trust_dev_main_dlg_title);
    	int sel_cnt=lfm_adapter.getItemSelectedCount();
    	String sel=""+sel_cnt+"/"+lfm_adapter.getCount();
    	dlg_title.setText(sel);

    	setMainSaveButtonEnabled();
    	
    	final ImageButton dlg_done=(ImageButton)mDialog.findViewById(R.id.edit_trust_dev_main_dlg_btn_done);
    	dlg_done.setVisibility(ImageButton.VISIBLE);

		LinearLayout ll_prof=(LinearLayout) mDialog.findViewById(R.id.edit_trust_dev_main_context_view);
		LinearLayout ll_add=(LinearLayout)ll_prof.findViewById(R.id.context_button_add_view);
		LinearLayout ll_delete=(LinearLayout)ll_prof.findViewById(R.id.context_button_delete_view);
		LinearLayout ll_select_all=(LinearLayout)ll_prof.findViewById(R.id.context_button_select_all_view);
		LinearLayout ll_unselect_all=(LinearLayout)ll_prof.findViewById(R.id.context_button_unselect_all_view);

		ll_add.setVisibility(LinearLayout.GONE);
		
		boolean deletable_log_selected=false;
		for(int i=0;i<lfm_adapter.getCount();i++) {
			if (lfm_adapter.getItem(i).isSelected) {
				deletable_log_selected=true;
				break;
			}
		}
		if (deletable_log_selected) ll_delete.setVisibility(LinearLayout.VISIBLE);
		else ll_delete.setVisibility(LinearLayout.GONE);
		
        ll_select_all.setVisibility(LinearLayout.VISIBLE);
        
        if (lfm_adapter.isAnyItemSelected()) ll_unselect_all.setVisibility(LinearLayout.VISIBLE);
        else ll_unselect_all.setVisibility(LinearLayout.GONE);
	};

	private void setContextButtonNormalMode(AdapterTrustDeviceList lfm_adapter) {
    	final TextView dlg_title=(TextView)mDialog.findViewById(R.id.edit_trust_dev_main_dlg_title);
    	dlg_title.setText(mDialogTitle);

    	final ImageButton dlg_done=(ImageButton)mDialog.findViewById(R.id.edit_trust_dev_main_dlg_btn_done);
    	dlg_done.setVisibility(ImageButton.GONE);

    	setMainSaveButtonEnabled();
    	
		LinearLayout ll_prof=(LinearLayout) mDialog.findViewById(R.id.edit_trust_dev_main_context_view);
		LinearLayout ll_add=(LinearLayout)ll_prof.findViewById(R.id.context_button_add_view);
		LinearLayout ll_delete=(LinearLayout)ll_prof.findViewById(R.id.context_button_delete_view);
		LinearLayout ll_select_all=(LinearLayout)ll_prof.findViewById(R.id.context_button_select_all_view);
		LinearLayout ll_unselect_all=(LinearLayout)ll_prof.findViewById(R.id.context_button_unselect_all_view);

		ll_add.setVisibility(LinearLayout.VISIBLE);

		ll_delete.setVisibility(LinearLayout.GONE);
        
    	if (lfm_adapter.isEmptyAdapter()) {
            ll_select_all.setVisibility(LinearLayout.GONE);
            ll_unselect_all.setVisibility(LinearLayout.GONE);
    	} else {
            ll_select_all.setVisibility(LinearLayout.VISIBLE);
            ll_unselect_all.setVisibility(LinearLayout.GONE);
    	}
	};

	private void addTrustedDev(final NotifyEvent ntfy_save_btn) {
		ArrayList<TrustDeviceItem> tdl=new ArrayList<TrustDeviceItem>();
		BluetoothAdapter bta=BluetoothAdapter.getDefaultAdapter();
		String bt_msg="", wifi_msg="";
		if (bta!=null && bta.isEnabled()) {
			ArrayList<TrustDeviceItem> btl=buildBluetoothDeviceList();
			if(btl.size()==0) {
		        bt_msg=mContext.getString(R.string.msgs_key_guard_trust_device_add_new_bt_no_dev_msg);
			} else {
				tdl.addAll(btl);
			}
		} else {
	        bt_msg=mContext.getString(R.string.msgs_key_guard_trust_device_add_new_bt_adapter_on_msg);
		}
		WifiManager wm=(WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
		if (wm.isWifiEnabled()) {
			String tssid="", addr="", ssid="";
			tssid=wm.getConnectionInfo().getSSID();
			addr=wm.getConnectionInfo().getBSSID();
			if (tssid==null || tssid.equals("<unknown ssid>")) ssid="";
			else ssid=tssid.replaceAll("\"", "");
			if (ssid.equals("0x")) ssid="";

			if (ssid!=null && !ssid.equals("")) {
				boolean found=false;
				for(int i=0;i<mAdapterTrustList.getCount();i++) {
					if (mAdapterTrustList.getItem(i).trustedItemName.equals(ssid) &&
							mAdapterTrustList.getItem(i).trustedItemAddr.equals(addr)) {
						found=true;
						break;
					}
				}
				if (found) {
					wifi_msg=mContext.getString(R.string.msgs_key_guard_trust_device_add_new_wifi_ap_msg);
				} else {
					TrustDeviceItem tli=new TrustDeviceItem();
					tli.trustedItemName=ssid;
					tli.trustedItemAddr=addr;
					tli.trustedItemType=TrustDeviceItem.TYPE_WIFI_AP;
					tdl.add(tli);
				}
			}
		} else {
			wifi_msg=mContext.getString(R.string.msgs_key_guard_trust_device_turn_on_wifi_msg);
		}
		
		addBtDevDlg(tdl,ntfy_save_btn);
		
		if (!bt_msg.equals("") || !wifi_msg.equals("")) {
			String msg_txt="";
			if (bt_msg.equals("")) msg_txt=wifi_msg;
			else {
				if (wifi_msg.equals("")) msg_txt=bt_msg;
				else msg_txt=bt_msg+"\n"+wifi_msg;
			}
	        MessageDialogFragment cdf =MessageDialogFragment.newInstance(false, "W",
	        		mContext.getString(R.string.msgs_key_guard_trust_device_add_new_dev_some_error),msg_txt);
	        cdf.showDialog(mFragment.getFragmentManager(),cdf,null);
		}

	};

	private ArrayList<TrustDeviceItem> buildBluetoothDeviceList() {
		BluetoothAdapter bta=BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> bd_list=bta.getBondedDevices();
		ArrayList<TrustDeviceItem> btl=new ArrayList<TrustDeviceItem>();
		Iterator<BluetoothDevice> bDeviceIterator = bd_list.iterator();
 	    while (bDeviceIterator.hasNext()) {
 	    	BluetoothDevice device = bDeviceIterator.next();
 	    	boolean found=false;
 	    	for(int i=0;i<mTrustList.size();i++) {
 	    		TrustDeviceItem tli=mTrustList.get(i);
 	    		if(tli.trustedItemType==TrustDeviceItem.TYPE_BLUETOOTH_DEVICE) {
 	    			if (tli.trustedItemName.equals(device.getName()) && tli.trustedItemAddr.equals(device.getAddress())) {
 	    				found=true;
 	    				break;
 	    			}
 	    		}
 	    	}
 	    	if (!found) {
 	    		TrustDeviceItem bdli=new TrustDeviceItem();
 	    		bdli.trustedItemType=TrustDeviceItem.TYPE_BLUETOOTH_DEVICE;
 	 	    	bdli.trustedItemName=device.getName();
 	 	    	bdli.trustedItemAddr=device.getAddress();
 	 	    	btl.add(bdli);
 	    	}
 	    }
 	    return btl;
	};
	
	private void addBtDevDlg(ArrayList<TrustDeviceItem> btl, final NotifyEvent ntfy_save_btn) {
		final Dialog dialog = new Dialog(getActivity());
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(R.layout.edit_trust_dev_add_bt_dlg);
		final Button btn_cancel= (Button) dialog.findViewById(R.id.edit_trust_dev_add_bt_dlg_cancel);
		final Button btn_ok = (Button) dialog.findViewById(R.id.edit_trust_dev_add_bt_dlg_ok);
		
		final AdapterTrustDeviceList adapter=new AdapterTrustDeviceList(mContext, R.layout.edit_trust_dev_item, btl, null);
		final ListView lv=(ListView)dialog.findViewById(R.id.edit_trust_dev_add_bt_dlg_listview);
		lv.setAdapter(adapter);
		
    	NotifyEvent ntfy_cb_listener=new NotifyEvent(mContext);
    	ntfy_cb_listener.setListener(new NotifyEventListener(){
			@Override
			public void positiveResponse(Context c, Object[] o) {
				if (adapter.isShowCheckBox()) {
					if (adapter.isAnyItemSelected()) btn_ok.setEnabled(true);
					else btn_ok.setEnabled(false);
				}
			};

			@Override
			public void negativeResponse(Context c, Object[] o) {}
    	});
    	adapter.setNotifyCbClickListener(ntfy_cb_listener);
		adapter.setShowCheckBox(true);
		adapter.notifyDataSetChanged();

    	final ImageButton dlg_done=(ImageButton)dialog.findViewById(R.id.edit_trust_dev_add_bt_dlg_btn_done);
    	dlg_done.setVisibility(ImageButton.GONE);
    	
		CommonDialog.setDlgBoxSizeLimit(dialog,true);
		
    	lv.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				if (adapter.isShowCheckBox()) {
					adapter.getItem(pos).isSelected=
							!adapter.getItem(pos).isSelected;
					adapter.notifyDataSetChanged();
					if (adapter.isAnyItemSelected()) btn_ok.setEnabled(true);
					else btn_ok.setEnabled(false);
				}
			}
    	});
    	
    	btn_ok.setEnabled(false);
    	btn_ok.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				for(int i=0;i<adapter.getCount();i++) {
					if (adapter.getItem(i).isSelected) {
						mAdapterTrustList.add(adapter.getItem(i));
						mAdapterTrustList.sort();
						mAdapterTrustList.notifyDataSetChanged();
					}
				}
				ntfy_save_btn.notifyToListener(true, null);
				dialog.dismiss();
			}
    	});

    	btn_cancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				ntfy_save_btn.notifyToListener(false, null);
			}
    	});

		dialog.show();

	};

    private void confirmDeleteItem(final AdapterTrustDeviceList lfm_adapter) {
    	final ArrayList<TrustDeviceItem> del_tl=new ArrayList<TrustDeviceItem>();
    	String delete_list="",sep="";
    	for (int i=0;i<lfm_adapter.getCount();i++) {
    		TrustDeviceItem item=lfm_adapter.getItem(i);
    		if (item.isSelected) {
    			delete_list+=sep+item.trustedItemName;
    			sep="\n";
    			del_tl.add(item);
    		}
    	}
    	if (delete_list.length()>0) delete_list+="\n";
    	
    	NotifyEvent ntfy=new NotifyEvent(null);
    	ntfy.setListener(new NotifyEventListener(){
			@Override
			public void positiveResponse(Context c, Object[] o) {
				for(int i=0;i<del_tl.size();i++) lfm_adapter.remove(del_tl.get(i));
				lfm_adapter.setAllItemSelected(false);
				lfm_adapter.setShowCheckBox(false);
				lfm_adapter.notifyDataSetChanged();
				setContextButtonNormalMode(lfm_adapter);
			}

			@Override
			public void negativeResponse(Context c, Object[] o) {}
    	});
        MessageDialogFragment cdf =MessageDialogFragment.newInstance(true, "W",
        		mContext.getString(R.string.msgs_key_guard_trust_device_delete_confirm_msg),
        		delete_list);
        cdf.showDialog(mFragment.getFragmentManager(),cdf,ntfy);
    };
    
    public void showDialog(FragmentManager fm, Fragment frag, GlobalParameters gp) {
    	if (DEBUG_ENABLE) Log.v(APPLICATION_TAG,"showDialog");
    	mTerminateRequired=false;
    	mGlblParms=gp;
	    FragmentTransaction ft = fm.beginTransaction();
	    ft.add(frag,null);
	    ft.commitAllowingStateLoss();
//    	show(fm, APPLICATION_TAG);
    };


}

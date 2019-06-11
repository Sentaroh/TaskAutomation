package com.sentaroh.android.TaskAutomation.Log;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sentaroh.android.TaskAutomation.GlobalParameters;
import com.sentaroh.android.TaskAutomation.GlobalWorkArea;
import com.sentaroh.android.Utilities.LogUtil.CommonLogWriter;
import com.sentaroh.android.Utilities.StringUtil;
import com.sentaroh.android.Utilities.ThreadCtrl;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.sentaroh.android.TaskAutomation.CommonConstants.APPLICATION_TAG;
import static com.sentaroh.android.TaskAutomation.Log.LogConstants.BROADCAST_LOG_DELETE;
import static com.sentaroh.android.TaskAutomation.Log.LogConstants.BROADCAST_LOG_FLUSH;
import static com.sentaroh.android.TaskAutomation.Log.LogConstants.BROADCAST_LOG_RESET;
import static com.sentaroh.android.TaskAutomation.Log.LogConstants.BROADCAST_LOG_ROTATE;
import static com.sentaroh.android.TaskAutomation.Log.LogConstants.BROADCAST_LOG_SEND;

public class LogService extends Service {

    private GlobalParameters mGp=null;

    private Context mContext=null;

    private ThreadCtrl mLogCatCtl=new ThreadCtrl();

    @Override
    public void onCreate() {
        if (mGp==null) {
            mContext=this;
            mGp= GlobalWorkArea.getGlobalParameters(this);
        }

//        mLogCatCtl.setDisabled();
//        Thread th=new Thread() {
//            @Override
//            public void run() {
//                Log.v(APPLICATION_TAG, "LogCat monitor started");
//                startLogcat();
//                boolean for_ever=true;
//                while(for_ever) {
//                    SystemClock.sleep(1000*60*60);
//                    mLogCatCtl.setDisabled();
//                    synchronized(mLogCatCtl) {
//                        try {
//                            mLogCatCtl.wait();
//                            startLogcat();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//                Log.v(APPLICATION_TAG, "LogCat monitor ended");
//            }
//        };
//        th.setName("LogCatMonitor");
//        th.start();

    }

    private boolean mLogcatActive=false;
    private String mLogcatDir="/storage/emulated/0/TaskAutomation", mLogcatFilename="logcat.txt";
    private void startLogcat() {
        Thread th=new Thread() {
            @Override
            public void run() {
                if (mLogCatCtl.isEnabled()) return;
                mLogCatCtl.setEnabled();
                Log.v(APPLICATION_TAG, "LogCat writer started");
                mLogcatActive=true;
                Process process = null;
                BufferedReader reader = null;
                BufferedOutputStream bos=null;
                try {
                    File log_dir=new File(mLogcatDir);
                    if (!log_dir.exists()) log_dir.mkdirs();
                    File log_out=new File(mLogcatDir+"/"+System.currentTimeMillis()+".txt");//mLogcatFilename);
                    bos=new BufferedOutputStream(new FileOutputStream(log_out), 1024*1024*2);

                    process = Runtime.getRuntime().exec("su");
                    DataOutputStream cmd_in=new DataOutputStream(process.getOutputStream());
                    String logcat_cmd = "logcat -v time";
                    cmd_in.writeBytes(logcat_cmd+"\n");
                    cmd_in.flush();
                    reader = new BufferedReader(new InputStreamReader(process.getInputStream()), 1024*64);
                    String line;
                    while ((line = reader.readLine()) != null) {
                        bos.write((line+"\n").getBytes());
//                        bos.flush();
                        if (!mLogCatCtl.isEnabled()) break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    synchronized(mLogCatCtl) {
                        mLogCatCtl.notify();
                    }
                    Log.v(APPLICATION_TAG, "LogCat writer ended");
                    mLogCatCtl.setDisabled();
                    if (reader != null) {
                        try {
                            bos.flush();
                            bos.close();
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        th.setName("LogCatWriter");
        th.start();
    }

    private StringBuilder log_msg=new StringBuilder(1024);
    private StringBuilder print_msg=new StringBuilder(512);
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent!=null) {
            String action=intent.getAction();
            if (action.equals(BROADCAST_LOG_SEND)) {
                String type=intent.getStringExtra("TYPE");
                String id=intent.getStringExtra("ID");
                String cat=intent.getStringExtra("CAT");
                String[] msg=intent.getStringArrayExtra("MSG");
                log_msg.setLength(0);
                for (String item:msg) {
                    if (item!=null) log_msg.append(item);
                }
                print_msg.setLength(0);
                if (type.equalsIgnoreCase("M")) {
                    print_msg
                            .append("M ")
                            .append(cat)
                            .append(" ")
                            .append(StringUtil.convDateTimeTo_YearMonthDayHourMinSecMili(System.currentTimeMillis()))
                            .append(" ")
                            .append(id)
                            .append(log_msg.toString());
                } else if (type.equalsIgnoreCase("D")) {
                    print_msg
                            .append("D ")
                            .append(cat)
                            .append(" ")
                            .append(StringUtil.convDateTimeTo_YearMonthDayHourMinSecMili(System.currentTimeMillis()))
                            .append(" ")
                            .append(id)
                            .append(log_msg.toString());
                }
                CommonLogWriter.enqueue(mGp, mContext, mGp.getLogIntentSend(), print_msg.toString(), false);
                Log.v(APPLICATION_TAG,cat+" "+id+log_msg.toString());
            } else if (action.equals(BROADCAST_LOG_RESET)) {
                CommonLogWriter.enqueue(mGp, mContext, mGp.getLogIntentReset(), "",  true);
            } else if (action.equals(BROADCAST_LOG_ROTATE)) {
                CommonLogWriter.enqueue(mGp, mContext, mGp.getLogIntentRotate(), "",  true);
            } else if (action.equals(BROADCAST_LOG_DELETE)) {
                CommonLogWriter.enqueue(mGp, mContext, mGp.getLogIntentDelete(), "",  true);
            } else if (action.equals(BROADCAST_LOG_FLUSH)) {
                CommonLogWriter.enqueue(mGp, mContext, mGp.getLogIntentFlush(), "",  true);
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

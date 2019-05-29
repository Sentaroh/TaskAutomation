package com.sentaroh.android.TaskAutomation.Log;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sentaroh.android.TaskAutomation.GlobalParameters;
import com.sentaroh.android.TaskAutomation.GlobalWorkArea;
import com.sentaroh.android.Utilities.LogUtil.CommonLogWriter;
import com.sentaroh.android.Utilities.StringUtil;

import static com.sentaroh.android.TaskAutomation.CommonConstants.APPLICATION_TAG;
import static com.sentaroh.android.TaskAutomation.Log.LogConstants.BROADCAST_LOG_DELETE;
import static com.sentaroh.android.TaskAutomation.Log.LogConstants.BROADCAST_LOG_FLUSH;
import static com.sentaroh.android.TaskAutomation.Log.LogConstants.BROADCAST_LOG_RESET;
import static com.sentaroh.android.TaskAutomation.Log.LogConstants.BROADCAST_LOG_ROTATE;
import static com.sentaroh.android.TaskAutomation.Log.LogConstants.BROADCAST_LOG_SEND;

public class LogService extends Service {

    private GlobalParameters mGp=null;

    private Context mContext=null;

    @Override
    public void onCreate() {
        if (mGp==null) {
            mContext=this;
            mGp= GlobalWorkArea.getGlobalParameters(this);
        }
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
